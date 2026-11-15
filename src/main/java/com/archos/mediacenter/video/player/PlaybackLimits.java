package com.archos.mediacenter.video.player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/** Session-local sleep and automatic-episode limits, using elapsed time rather than the wall clock. */
public final class PlaybackLimits implements AutoCloseable
{
    public static final String KEY_STILL_WATCHING = "still_watching_enabled";
    public static final String KEY_EPISODES = "still_watching_episodes";
    public static final int DEFAULT_EPISODES = 3;
    public static final String KEY_COUNT_EPISODES = "still_watching_count_episodes";
    public static final String KEY_IDLE_MINUTES = "still_watching_idle_minutes";
    public static final long QUESTION_TIMEOUT_MS = 60000;

    public enum Timer
    {
        OFF,
        MINUTES_30,
        MINUTES_60,
        MINUTES_90,
        END_OF_EPISODE
    }

    public enum Event
    {
        TIMER_CHANGED,
        STILL_WATCHING,
        STOP,
        CONTINUE,
        IDLE_CONTINUE
    }

    public interface Scheduler
    {
        Runnable after(long delayMs, Runnable task);
    }

    private final LongSupplier clock;
    private final Scheduler scheduler;
    private final List<Consumer<Event>> listeners = new ArrayList<>();
    private Timer timer = Timer.OFF;
    private long deadline;
    private Runnable cancelTimer = () ->
    {
    };
    private Runnable cancelQuestion = () ->
    {
    };
    private int generation;
    private int questionGeneration;
    private int automaticEpisodes;
    private boolean episodeCounting = true;
    private int askAfter = DEFAULT_EPISODES;
    private boolean questionsEnabled = true;
    private boolean question;
    private boolean approvedAdvance;
    private boolean closed;
    private long idleLimitMs;
    private long idleDeadline;
    private long questionDeadline;
    private boolean idleQuestion;
    private boolean advanceAfterQuestion;
    private int idleGeneration;
    private Runnable cancelIdle = () -> {};

    /** Elapsed-time inactivity; zero disables. Call interaction() for real remote input, never progress ticks. */
    public synchronized void configureInactivity(long durationMs)
    {
        long bounded = Math.max(0, Math.min(24L * 60 * 60000, durationMs));
        if (bounded != idleLimitMs) {
            idleLimitMs = bounded;
            interaction();
        }
    }

    public synchronized void interaction()
    {
        cancelIdle.run();
        int expected = ++idleGeneration;
        idleDeadline = 0;
        if (!closed && questionsEnabled && idleLimitMs > 0) {
            idleDeadline = clock.getAsLong() + idleLimitMs;
            cancelIdle = scheduler.after(idleLimitMs, () -> idleExpired(expected));
        }
    }

    private synchronized void idleExpired(int expected)
    {
        if (closed || question || !questionsEnabled || expected != idleGeneration)
            return;
        question = true;
        idleQuestion = true;
        advanceAfterQuestion = false;
        int owner = ++questionGeneration;
        questionDeadline = clock.getAsLong() + QUESTION_TIMEOUT_MS;
        cancelQuestion = scheduler.after(QUESTION_TIMEOUT_MS, () -> expireQuestion(owner));
        emit(Event.STILL_WATCHING);
    }

    /** Wall-clock projections are rebuilt on each call; selected timers retain monotonic deadlines. */
    public synchronized long stopAtEpochMs(Timer choice, long wallClockMs)
    {
        long minutes = choice == Timer.MINUTES_30 ? 30 : choice == Timer.MINUTES_60 ? 60
                : choice == Timer.MINUTES_90 ? 90 : 0;
        return minutes == 0 ? -1 : wallClockMs + minutes * 60000;
    }

    public PlaybackLimits(LongSupplier clock, Scheduler scheduler, Consumer<Event> listener)
    {
        this.clock = clock;
        this.scheduler = scheduler;
        listeners.add(listener);
    }

    public synchronized void addListener(Consumer<Event> listener)
    {
        listeners.add(listener);
    }

    public synchronized void removeListener(Consumer<Event> listener)
    {
        listeners.remove(listener);
    }

    public synchronized void configureStillWatching(boolean enabled, int episodes)
    {
        boolean changed = questionsEnabled != enabled;
        questionsEnabled = enabled;
        if (changed)
            interaction();
        askAfter = Math.max(1, Math.min(100, episodes));
        if (!enabled && question)
        {
            answerStillWatching(true);
        }
    }

    public synchronized void configureEpisodeCounting(boolean enabled)
    {
        if (episodeCounting == enabled)
            return;
        episodeCounting = enabled;
        automaticEpisodes = 0;
        if (!enabled && question && !idleQuestion)
            answerStillWatching(true);
    }

    public synchronized Timer timer()
    {
        return timer;
    }

    public synchronized void setTimer(Timer choice)
    {
        if (closed)
        {
            return;
        }
        cancelTimer.run();
        int expected = ++generation;
        timer = choice;
        long minutes = choice == Timer.MINUTES_30   ? 30
                       : choice == Timer.MINUTES_60 ? 60
                       : choice == Timer.MINUTES_90 ? 90
                                                    : 0;
        deadline = minutes == 0 ? 0 : clock.getAsLong() + minutes * 60000;
        if (minutes > 0)
        {
            cancelTimer = scheduler.after(minutes * 60000, () -> expireTimer(expected));
        }
        emit(Event.TIMER_CHANGED);
    }

    /** -1 means end-of-episode; zero means off or elapsed. */
    public synchronized long remainingMs()
    {
        return timer == Timer.END_OF_EPISODE ? -1 : deadline == 0 ? 0 : Math.max(0, deadline - clock.getAsLong());
    }

    public synchronized boolean questionPending()
    {
        return question;
    }

    public synchronized boolean episodeCompleted()
    {
        if (timer == Timer.END_OF_EPISODE)
        {
            expireTimer(generation);
            return false;
        }
        return !closed;
    }

    /** Call only for an automatic transition, not explicit next/episode-card playback. */
    public synchronized boolean allowAutoAdvance()
    {
        if (closed || question)
        {
            if (question)
                advanceAfterQuestion = true;
            return false;
        }
        if (approvedAdvance)
        {
            approvedAdvance = false;
            return true;
        }
        automaticEpisodes++;
        if (!questionsEnabled || !episodeCounting || automaticEpisodes < askAfter)
        {
            return true;
        }
        question = true;
        idleQuestion = false;
        int expected = ++questionGeneration;
        questionDeadline = clock.getAsLong() + QUESTION_TIMEOUT_MS;
        cancelQuestion = scheduler.after(QUESTION_TIMEOUT_MS, () -> expireQuestion(expected));
        emit(Event.STILL_WATCHING);
        return false;
    }

    public synchronized void answerStillWatching(boolean keepWatching)
    {
        if (!question || closed)
        {
            return;
        }
        cancelQuestion.run();
        questionGeneration++;
        question = false;
        automaticEpisodes = 0;
        boolean advance = !idleQuestion || advanceAfterQuestion;
        approvedAdvance = keepWatching && advance;
        if (keepWatching)
            interaction();
        if (keepWatching)
            emit(advance ? Event.CONTINUE : Event.IDLE_CONTINUE);
        else
            stop();
    }

    public synchronized boolean inactivityQuestion()
    {
        return question && idleQuestion;
    }

    public synchronized long selectedStopAtEpochMs(long wallClockMs)
    {
        return deadline == 0 ? -1 : wallClockMs + remainingMs();
    }

    /** Call on return from device sleep, before treating a new input as activity. */
    public synchronized void checkDeadlines()
    {
        if (closed)
            return;
        long now = clock.getAsLong();
        if (deadline > 0 && now >= deadline) {
            expireTimer(generation);
            return;
        }
        if (question && now >= questionDeadline) {
            expireQuestion(questionGeneration);
            return;
        }
        if (!question && idleDeadline > 0 && now >= idleDeadline)
            idleExpired(idleGeneration);
    }

    private void stop()
    {
        question = false;
        automaticEpisodes = 0;
        questionGeneration++;
        cancelQuestion.run();
        idleGeneration++;
        idleDeadline = 0;
        cancelIdle.run();
        emit(Event.STOP);
    }

    private synchronized void expireTimer(int expected)
    {
        if (!closed && expected == generation)
        {
            setTimer(Timer.OFF);
            stop();
        }
    }

    private synchronized void expireQuestion(int expected)
    {
        if (!closed && question && expected == questionGeneration)
        {
            stop();
        }
    }

    private void emit(Event event)
    {
        for (Consumer<Event> listener : new ArrayList<>(listeners))
        {
            listener.accept(event);
        }
    }

    @Override public synchronized void close()
    {
        closed = true;
        question = false;
        generation++;
        questionGeneration++;
        cancelTimer.run();
        cancelQuestion.run();
        cancelIdle.run();
        idleGeneration++;
        listeners.clear();
    }
}
