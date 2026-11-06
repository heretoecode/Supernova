package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class SeekLoadingSignalTest
{
    @Test public void onlyTheCurrentBackendAndRequestCanEndWaitingAndChapterUsesTheRequestedPosition()
    {
        SeekLoadingSignal signal = new SeekLoadingSignal();
        Object backend = new Object();
        List<PlaybackChapters.Chapter> chapters = List.of(new PlaybackChapters.Chapter(0, "Start"),
                                                         new PlaybackChapters.Chapter(10000, "Second"));
        int old = signal.begin(backend, 9000, chapters);
        int latest = signal.begin(backend, 14000, chapters);
        signal.result(backend, old, SeekLoadingSignal.State.READY);
        signal.result(new Object(), latest, SeekLoadingSignal.State.READY);
        assertTrue(signal.current().loading());
        assertEquals(14000, signal.current().positionMs());
        assertEquals("Second", signal.current().chapterTitle());
        assertEquals(1, signal.current().chapterIndex());
        signal.result(backend, latest, SeekLoadingSignal.State.READY);
        assertFalse(signal.current().loading());
        signal.result(backend, latest, SeekLoadingSignal.State.FAILED);
        assertEquals(SeekLoadingSignal.State.READY, signal.current().state());
    }

    @Test public void newSourceDisposalAndSupersededUiCallbacksCannotRestoreAnOldRing()
    {
        SeekLoadingSignal signal = new SeekLoadingSignal();
        List<Runnable> queue = new ArrayList<>();
        List<SeekLoadingSignal.Snapshot> observed = new ArrayList<>();
        var subscription = signal.observe(queue::add, observed::add);
        Object backend = new Object();
        int token = signal.begin(backend, 100, List.of());
        signal.reset();
        signal.result(backend, token, SeekLoadingSignal.State.READY);
        queue.forEach(Runnable::run);
        assertEquals(1, observed.size());
        assertEquals(SeekLoadingSignal.State.IDLE, observed.get(0).state());
        subscription.close();
        signal.begin(new Object(), 200, List.of());
        queue.forEach(Runnable::run);
        assertEquals(1, observed.size());
    }

    @Test public void unsupportedAndFailedBackendsNeverPretendThePictureIsReady()
    {
        SeekLoadingSignal signal = new SeekLoadingSignal();
        Object backend = new Object();
        int token = signal.begin(backend, 42, List.of());
        signal.result(backend, token, SeekLoadingSignal.State.UNAVAILABLE);
        assertFalse(signal.current().loading());
        assertEquals(SeekLoadingSignal.State.UNAVAILABLE, signal.current().state());
        token = signal.begin(backend, 43, List.of());
        signal.result(backend, token, SeekLoadingSignal.State.FAILED);
        assertEquals(SeekLoadingSignal.State.FAILED, signal.current().state());
    }
}
