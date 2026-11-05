package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class LibraryScanControllerTest
{
    private static final String SOURCE = "aaaaaaaaaaaaaaaaaaaaaaaa";

    @Before public void install() throws Exception
    {
        LibraryScanController.install(RuntimeEnvironment.getApplication());
        LibraryScanController.IO.submit(() -> {}).get(5, TimeUnit.SECONDS);
    }

    private Intent event(String batch, String phase)
    {
        return new Intent()
            .putExtra("batch_id", batch)
            .putExtra("source_id", SOURCE)
            .putExtra("source_kind", "webdavs")
            .putExtra("source_location", "webdav.put.io/Films")
            .putExtra("phase", phase)
            .putExtra("checked", 12)
            .putExtra("total", -1);
    }

    @Test public void tileSnapshotIsMemoryOnlyAndPreservesSuppliedFriendlySourceNames()
    {
        LibraryScanController.sourceName(SOURCE, "Put.io › Films");
        LibraryScanController.accept(event("one", "started"));
        StrictMode.ThreadPolicy before = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                       .detectDiskReads()
                                       .detectDiskWrites()
                                       .detectNetwork()
                                       .penaltyDeath()
                                       .build());
        try
        {
            ScanProgress value = LibraryScanController.progress();
            assertEquals("Put.io › Films", value.sourceLabel);
            assertEquals(12, value.checked);
            assertEquals(-1, value.total);
            assertTrue(value.running);
        }
        finally
        {
            StrictMode.setThreadPolicy(before);
        }
    }

    @Test public void stopDoesNotClaimCompletionBeforeTheWorkerTerminalAndNewRequestsCanRun() throws Exception
    {
        LibraryScanController.accept(event("one", "started"));
        LibraryScanController.stop();
        assertTrue(LibraryScanController.progress().running);
        assertTrue(LibraryScanController.progress().stopping);
        ExecutorService control = ReflectionHelpers.getStaticField(LibraryScanController.class, "CONTROL");
        control.submit(() -> {}).get(5, TimeUnit.SECONDS);
        assertTrue(LibraryScanController.progress().running);
        LibraryScanController.accept(event("one", "failed"));
        assertFalse(LibraryScanController.progress().running);
        assertEquals(ScanProgress.Failure.CANCELLED, LibraryScanController.progress().failure);
        LibraryScanController.accept(event("two", "started"));
        assertTrue(LibraryScanController.progress().running);
        assertFalse(LibraryScanController.progress().stopping);
    }

    @Test public void plainFailureReasonDoesNotExposeExceptionMessageCredentialsOrAddresses()
    {
        Intent failed = event("one", "failed")
                            .putExtra("failure_class", "AuthenticationException")
                            .putExtra("failure_message", "password=SECRET_CANARY webdavs://AUTH_CANARY@server/private");
        LibraryScanController.accept(failed);
        ScanProgress value = LibraryScanController.progress();
        assertEquals(ScanProgress.Failure.SIGN_IN, value.failure);
        assertEquals("Source sign-in failed", value.failureReason());
        assertFalse(value.failureReason().contains("CANARY"));
    }

    @Test public void stopBeforeSourceRegistrationClosesOnlyTheSchedulerWindowAndAFreshRequestWorks() throws Exception
    {
        LibraryScanController.requested("first");
        assertTrue(LibraryScanController.progress().running);
        LibraryScanController.stop();
        ExecutorService control = ReflectionHelpers.getStaticField(LibraryScanController.class, "CONTROL");
        control.submit(() -> {}).get(5, TimeUnit.SECONDS);
        assertFalse(LibraryScanController.progress().running);
        assertEquals(ScanProgress.Failure.CANCELLED, LibraryScanController.progress().failure);
        LibraryScanController.requested("next");
        assertTrue(LibraryScanController.progress().running);
        LibraryScanController.accept(event("actual-batch", "started"));
        LibraryScanController.accept(event("actual-batch", "complete"));
        assertFalse(LibraryScanController.progress().running);
    }

    @Test public void sourceListCanSupplyNamesUsingTheActualIndexedRootUri() throws Exception
    {
        Uri root = Uri.parse("webdavs://webdav.put.io/Films");
        LibraryScanController.accept(
            event("one", "started").putExtra("source_id", ScanSourceLabels.id(root.toString())));
        LibraryScanController.sourceName(root, "Put.io › Films");
        assertEquals("Put.io › Films", LibraryScanController.progress().sourceLabel);
    }

    @Test public void lateMetadataAndBatchEventsCannotConsumeTheNextSchedulerAcknowledgementWindow()
    {
        LibraryScanController.accept(event("old", "started"));
        LibraryScanController.accept(event("old", "complete"));
        LibraryScanController.requested("next");
        LibraryScanController.accept(event("old", "metadata_queued"));
        LibraryScanController.accept(event("old", "batch_complete"));
        LibraryScanController.notStarted("next", true);
        assertFalse(LibraryScanController.progress().running);
        assertEquals("next", LibraryScanController.progress().batchId);
        assertEquals(ScanProgress.Failure.ADDRESS, LibraryScanController.progress().failure);
    }
    @Test public void finishedHistoryIncludesOnlyOneRecordPerSource() throws Exception
    {
        LibraryScanController.accept(event("history", "started"));
        LibraryScanController.accept(event("history", "complete").putExtra("added", 7).putExtra("removed", 2));
        LibraryScanController.accept(event("history", "complete"));
        LibraryScanController.IO.submit(() -> {}).get(5, TimeUnit.SECONDS);
        assertEquals(1, LibraryScanController.recentScans().size());
        assertEquals(7, LibraryScanController.recentScans().get(0).added);
        assertEquals(2, LibraryScanController.recentScans().get(0).removed);
    }
}
