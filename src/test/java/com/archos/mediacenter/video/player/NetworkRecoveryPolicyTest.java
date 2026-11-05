package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NetworkRecoveryPolicyTest
{
    @Test public void pauseThresholdKeepsOriginalPauseAndResumingClearsIt()
    {
        NetworkRecoveryPolicy policy = new NetworkRecoveryPolicy();
        policy.paused(10);
        policy.paused(50000);
        assertEquals(60000, policy.idleMs(60010));
        assertFalse(policy.longPause(60010));
        assertTrue(policy.longPause(60011));
        policy.resumed();
        assertFalse(policy.longPause(100000));
    }
    @Test public void readFailuresCannotLoopButIntentionalPauseReopensDoNotConsumeReadRetryBudget()
    {
        NetworkRecoveryPolicy policy = new NetworkRecoveryPolicy();
        assertTrue(policy.begin(0));
        assertFalse(policy.begin(1));
        policy.finished();
        assertTrue(policy.begin(2));
        policy.finished();
        assertFalse(policy.begin(3));
        assertTrue(policy.begin(4, false));
        policy.finished();
        assertTrue(policy.begin(5, false));
        policy.finished();
        assertTrue(policy.begin(30001));
        policy.reset();
        assertTrue(policy.begin(30002));
    }
    @Test public void onlyKnownReadErrorCodesUseNativeErrorRecovery()
    {
        assertTrue(NetworkRecoveryPolicy.readError(-1004, 0));
        assertTrue(NetworkRecoveryPolicy.readError(-110, 0));
        assertTrue(NetworkRecoveryPolicy.readError(1001, 1000));
        assertTrue(NetworkRecoveryPolicy.readError(1003, 1000));
        assertTrue(NetworkRecoveryPolicy.readError(1, -1004));
        assertFalse(NetworkRecoveryPolicy.readError(1001, 2000));
        assertFalse(NetworkRecoveryPolicy.readError(200, 0));
    }
}
