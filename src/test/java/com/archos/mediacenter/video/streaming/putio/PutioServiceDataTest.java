package com.archos.mediacenter.video.streaming.putio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.os.StrictMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PutioServiceDataTest
{
    @Test public void rowPublishesOnlyAccountQuotaReachabilityAndSyncOutcome()
    {
        PutioServiceData data = new PutioServiceData();
        data.account(new PutioReadClient.Account(42, "viewer", "active", 500, 1000));
        data.syncing();
        assertEquals(PutioServiceStatus.SyncState.SYNCING, data.current().syncState);
        data.complete(false);
        PutioServiceStatus row = data.current();
        assertTrue(row.signedIn);
        assertTrue(row.reachable);
        assertTrue(row.lastCheckedUtcMs > 0);
        assertEquals("viewer", row.signedInAs);
        assertEquals(500, row.storageUsed);
        assertEquals(1000, row.storageTotal);
        assertEquals(PutioServiceStatus.SyncState.UP_TO_DATE, row.syncState);
        data.failed(new PutioReadClient.Unavailable(PutioReconciliation.Failure.OFFLINE, 0));
        assertFalse(data.current().reachable);
        assertEquals(500, data.current().storageUsed);
        assertEquals("Put.io could not be reached", data.current().failureReason);
        data.failed(new PutioReadClient.Unavailable(PutioReconciliation.Failure.UNAUTHORISED, 401));
        assertTrue(data.current().reachable);
        assertFalse(data.current().signedIn);
        assertEquals("", data.current().signedInAs);
        assertEquals(-1, data.current().storageTotal);
    }

    @Test public void mainThreadStatusReadIsMemoryOnlyAndFailureNeverExposesExceptionText()
    {
        PutioServiceData data = new PutioServiceData();
        data.failed(new IllegalStateException("SECRET_CANARY"));
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().build());
        try
        {
            assertEquals("Library sync could not finish", data.current().failureReason);
        }
        finally
        {
            StrictMode.setThreadPolicy(old);
        }
        data.complete(true);
        assertEquals(PutioServiceStatus.SyncState.FAILED, data.current().syncState);
        assertEquals("Some library files need review", data.current().failureReason);
    }
}
