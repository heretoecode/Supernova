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
        CONTINUE
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
    private int askAfter = DEFAULT_EPISODES;
    private boolean questionsEnabled = true;
    private boolean question;
    private boolean approvedAdvance;
    private boolean closed;

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
        questionsEnabled = enabled;
        askAfter = Math.max(1, Math.min(100, episodes));
        if (!enabled && question)
        {
            answerStillWatching(true);
        }
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
            return false;
        }
        if (approvedAdvance)
        {
            approvedAdvance = false;
            return true;
        }
        automaticEpisodes++;
        if (!questionsEnabled || automaticEpisodes < askAfter)
        {
            return true;
        }
        question = true;
        int expected = ++questionGeneration;
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
        approvedAdvance = keepWatching;
        emit(keepWatching ? Event.CONTINUE : Event.STOP);
    }

    private synchronized void expireTimer(int expected)
    {
        if (!closed && expected == generation)
        {
            setTimer(Timer.OFF);
            emit(Event.STOP);
        }
    }

    private synchronized void expireQuestion(int expected)
    {
        if (!closed && question && expected == questionGeneration)
        {
            question = false;
            automaticEpisodes = 0;
            emit(Event.STOP);
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
        generation++;
        questionGeneration++;
        cancelTimer.run();
        cancelQuestion.run();
        listeners.clear();
    }
}
