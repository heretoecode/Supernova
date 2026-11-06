package com.archos.mediacenter.video.streaming.putio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class PutioCodeSignInTest
{
    @Test public void signedInSnapshotNeverContainsTheCredential() throws Exception
    {
        CountDownLatch signedIn = new CountDownLatch(1);
        AtomicBoolean saved = new AtomicBoolean();
        PutioCodeSignIn.Provider provider = new PutioCodeSignIn.Provider() {
            @Override public PutioCodeSignIn.Code begin()
            {
                return new PutioCodeSignIn.Code("CODE", "https://app.put.io/link?code=CODE");
            }
            @Override public String poll(PutioCodeSignIn.Code code)
            {
                return "PRIVATE_TEST_TOKEN";
            }
            @Override public void save(String token)
            {
                saved.set("PRIVATE_TEST_TOKEN".equals(token));
            }
        };
        try (PutioCodeSignIn session = new PutioCodeSignIn(provider, Runnable::run, value -> {
            assertFalse(value.toString().contains("PRIVATE_TEST_TOKEN"));
            if (value.state() == PutioCodeSignIn.State.SIGNED_IN)
                signedIn.countDown();
        }, 2000, 1))
        {
            assertTrue(signedIn.await(2, TimeUnit.SECONDS));
            assertTrue(saved.get());
            assertEquals(PutioCodeSignIn.State.SIGNED_IN, session.snapshot().state());
            assertEquals("", session.snapshot().code());
        }
    }

    @Test public void expiryDiscardsACredentialFromAnUninterruptibleLatePoll() throws Exception
    {
        CountDownLatch polling = new CountDownLatch(1);
        CountDownLatch expired = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean saved = new AtomicBoolean();
        PutioCodeSignIn.Provider provider = new PutioCodeSignIn.Provider() {
            @Override public PutioCodeSignIn.Code begin()
            {
                return new PutioCodeSignIn.Code("CODE", "https://app.put.io/link");
            }
            @Override public String poll(PutioCodeSignIn.Code code)
            {
                polling.countDown();
                while (true)
                {
                    try
                    {
                        release.await();
                        return "late";
                    }
                    catch (InterruptedException ignored)
                    {
                        // Deliberately emulate a transport that ignores interruption.
                    }
                }
            }
            @Override public void save(String token)
            {
                saved.set(true);
            }
        };
        try (PutioCodeSignIn session = new PutioCodeSignIn(provider, Runnable::run, value -> {
            if (value.state() == PutioCodeSignIn.State.EXPIRED)
                expired.countDown();
        }, 300, 1))
        {
            assertTrue(polling.await(2, TimeUnit.SECONDS));
            assertTrue(expired.await(2, TimeUnit.SECONDS));
            release.countDown();
            assertEquals(PutioCodeSignIn.State.EXPIRED, session.snapshot().state());
            assertFalse(saved.get());
        }
        finally
        {
            release.countDown();
        }
    }
}
