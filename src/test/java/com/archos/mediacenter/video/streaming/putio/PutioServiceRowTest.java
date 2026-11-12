package com.archos.mediacenter.video.streaming.putio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.archos.mediacenter.video.leanback.filebrowsing.PutioServiceRow;
import org.junit.Test;

public class PutioServiceRowTest
{
    private static final long NOW = 1_800_000_000_000L;

    private static PutioServiceStatus status(boolean signedIn, boolean reachable, long checked,
                                             PutioServiceStatus.SyncState state, String reason)
    {
        return new PutioServiceStatus(signedIn, "yourname", 42_500_000_000L, 100_000_000_000L, reachable,
                                      checked, state, reason);
    }

    @Test
    public void signedInAndUpToDateReadsPlainly()
    {
        PutioServiceStatus s = status(true, true, NOW - 3 * 60_000L, PutioServiceStatus.SyncState.UP_TO_DATE, null);
        assertEquals("Put.io · yourname", PutioServiceRow.title(s));
        assertEquals("42.5 GB of 100 GB used · Library up to date · checked 3 min ago",
                     PutioServiceRow.summary(s, NOW));
        assertFalse(PutioServiceRow.offersRetry(s));
    }

    @Test
    public void failedSyncGivesReasonAndRetry()
    {
        PutioServiceStatus s =
            status(true, true, NOW - 2 * 3_600_000L, PutioServiceStatus.SyncState.FAILED, "Put.io did not answer");
        assertEquals("42.5 GB of 100 GB used · Library sync failed: Put.io did not answer · checked 2 hours ago",
                     PutioServiceRow.summary(s, NOW));
        assertTrue(PutioServiceRow.offersRetry(s));
    }

    @Test
    public void unreachableAfterACheckOffersRetry()
    {
        PutioServiceStatus s = status(true, false, NOW - 10_000L, PutioServiceStatus.SyncState.UNKNOWN, null);
        assertEquals("42.5 GB of 100 GB used · Not reachable · checked just now", PutioServiceRow.summary(s, NOW));
        assertTrue(PutioServiceRow.offersRetry(s));
    }

    @Test
    public void neverCheckedIsNotCalledUnreachable()
    {
        PutioServiceStatus s = new PutioServiceStatus(true, "", -1, -1, false, 0,
                                                      PutioServiceStatus.SyncState.UNKNOWN, null);
        assertEquals("Put.io", PutioServiceRow.title(s));
        assertEquals("Not checked yet", PutioServiceRow.summary(s, NOW));
        assertFalse(PutioServiceRow.offersRetry(s));
    }

    @Test
    public void signedOutSaysSo()
    {
        PutioServiceStatus s = status(false, false, 0, PutioServiceStatus.SyncState.UNKNOWN, null);
        assertEquals("Put.io", PutioServiceRow.title(s));
        assertEquals("Not signed in", PutioServiceRow.summary(s, NOW));
        assertFalse(PutioServiceRow.offersRetry(s));
    }
}
