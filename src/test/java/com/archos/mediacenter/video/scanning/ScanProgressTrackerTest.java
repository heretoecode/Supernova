package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ScanProgressTrackerTest
{
    private final List<ScanProgress> finished = new ArrayList<>();
    private final ScanProgressTracker tracker = new ScanProgressTracker(finished::add);

    private boolean event(String batch, String source, String phase, long checked, long total, long elapsed)
    {
        return tracker.accept(batch, source, "USB drive Backup #1", phase, checked, total, 2, -1, 3, 1000 + elapsed,
                              elapsed, ScanProgress.Failure.SIGN_IN);
    }

    @Test public void repeatedProgressDoesNotDoubleCountAndUnknownTotalsStayUnknown()
    {
        event("one", "usb", "started", 0, -1, 10);
        event("one", "usb", "indexing", 15, -1, 20);
        event("one", "usb", "indexing", 15, -1, 30);
        assertEquals(15, tracker.current().checked);
        assertEquals(-1, tracker.current().total);
        assertEquals("USB drive Backup #1", tracker.current().sourceLabel);
        assertTrue(tracker.current().running);
        assertTrue(finished.isEmpty());
    }

    @Test public void stopRetainsActiveOwnershipUntilTerminalAndTheNextScanStartsNormally()
    {
        event("one", "usb", "started", 0, 100, 10);
        tracker.stopping();
        assertTrue(tracker.current().running);
        assertTrue(tracker.current().stopping);
        event("one", "usb", "failed", 30, 100, 60);
        assertFalse(tracker.current().running);
        assertFalse(tracker.current().stopping);
        assertEquals(ScanProgress.Failure.CANCELLED, tracker.current().failure);
        assertEquals(50, finished.get(0).durationMs);
        event("two", "usb", "started", 0, 10, 70);
        assertTrue(tracker.current().running);
        assertFalse(tracker.current().stopping);
        assertEquals(0, tracker.current().checked);
        assertEquals(ScanProgress.Failure.NONE, tracker.current().failure);
    }

    @Test public void concurrentSourcesFinishIndependentlyAndTotalsAreAdditiveOnlyWhenKnown()
    {
        event("one", "usb", "started", 2, 10, 10);
        event("one", "dav", "queued", 0, 20, 15);
        assertEquals("usb", tracker.current().sourceId);
        event("one", "usb", "complete", 10, 10, 20);
        assertTrue(tracker.current().running);
        assertEquals(10, tracker.current().checked);
        assertEquals(30, tracker.current().total);
        assertEquals("dav", tracker.current().sourceId);
        event("one", "dav", "complete", 20, 20, 25);
        assertFalse(tracker.current().running);
        assertEquals(30, tracker.current().checked);
        assertEquals(2, finished.size());
    }

    @Test public void duplicateAndLateTerminalEventsCannotFinishOrReopenACompletedBatch()
    {
        event("one", "usb", "started", 0, 10, 10);
        event("one", "usb", "complete", 10, 10, 20);
        assertFalse(event("one", "usb", "complete", 10, 10, 20));
        event("one", "usb", "batch_complete", 10, 10, 21);
        assertFalse(event("one", "usb", "started", 0, 10, 22));
        assertEquals(1, finished.size());
        assertEquals(10, tracker.current().checked);
    }

    @Test public void batchTerminalClosesOutstandingSourcesOnceWithoutInventingAFileTotal()
    {
        event("one", "usb", "started", 1, -1, 10);
        event("one", "dav", "queued", 0, -1, 15);
        event("one", "usb", "batch_failed", 1, -1, 30);
        assertFalse(tracker.current().running);
        assertEquals(2, finished.size());
        assertEquals(-1, tracker.current().total);
        assertEquals(ScanProgress.Failure.SIGN_IN, tracker.current().failure);
        assertEquals("Source sign-in failed", tracker.current().failureReason());
    }

    @Test public void malformedNotificationsLeaveTheMemorySnapshotUntouched()
    {
        event("one", "usb", "started", 5, -1, 10);
        assertFalse(event("../bad", "usb", "complete", 99, 99, 20));
        assertFalse(event("one", "usb", null, 99, 99, 20));
        assertEquals(5, tracker.current().checked);
    }

    @Test public void partialLocalImportIsTerminalWhilePartialNetworkTraversalRemainsRunning()
    {
        event("local-job", "local", "started", 0, -1, 10);
        event("local-job", "local", "partial", 15, -1, 30);
        assertFalse(tracker.current().running);
        assertEquals(1, finished.size());
        event("network-job", "dav", "started", 0, -1, 40);
        event("network-job", "dav", "partial", 2, -1, 50);
        assertTrue(tracker.current().running);
    }

    @Test public void lateBatchCompletionCannotAddOldCountersToTheNextRequest()
    {
        event("one", "usb", "started", 0, 100, 10);
        event("one", "usb", "complete", 100, 100, 20);
        event("two", "dav", "started", 0, 10, 30);
        assertFalse(event("one", "usb", "batch_complete", 100, 100, 40));
        assertEquals(0, tracker.current().checked);
        assertEquals("two", tracker.current().batchId);
        assertEquals(1, finished.size());
    }

    @Test public void coalescedSourceExplainsWhyItDidNotRunRatherThanLookingSuccessful()
    {
        event("one", "usb", "coalesced", 0, -1, 10);
        assertFalse(tracker.current().running);
        assertEquals(ScanProgress.Failure.BUSY, tracker.current().failure);
        assertEquals("A scan was already running", tracker.current().failureReason());
    }
}
