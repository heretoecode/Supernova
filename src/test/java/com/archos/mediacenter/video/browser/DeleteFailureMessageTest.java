package com.archos.mediacenter.video.browser;

import static org.junit.Assert.assertEquals;

import android.net.Uri;
import com.thegrizzlylabs.sardineandroid.impl.SardineException;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** D6 (0.158.8): a failed delete keeps the server's real reason and the warning says it in plain words. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class DeleteFailureMessageTest
{
    private static SardineException status(int code)
    {
        SardineException failure = org.mockito.Mockito.mock(SardineException.class);
        org.mockito.Mockito.when(failure.getStatusCode()).thenReturn(code);
        return failure;
    }

    @Test public void aRefusedWebdavDeleteNamesTheServer()
    {
        Uri file = Uri.parse("webdavs://webdav.put.io/Shows/Example/S01E01.mkv");
        Delete.recordFailure(file, status(403));
        assertEquals("Put.io didn't allow this file to be deleted.", Delete.failureMessage(file));
    }

    @Test public void aServerThatDoesNotAllowDeleteSaysSo()
    {
        Uri file = Uri.parse("webdav://media.example/Films/Long Film Name (2024).mkv");
        Delete.recordFailure(file, status(405));
        assertEquals("media.example doesn't allow deleting files over this connection.", Delete.failureMessage(file));
    }

    @Test public void aLostConnectionAsksToTryAgain()
    {
        Uri file = Uri.parse("sftp://nas.example/Films/a.mkv");
        Delete.recordFailure(file, new IOException("reset"));
        assertEquals("Couldn't reach nas.example to delete this file. Check the connection and try again.",
                     Delete.failureMessage(file));
    }

    @Test public void anUnknownFailureKeepsTheOldWords()
    {
        assertEquals("Couldn't delete the file.", Delete.failureMessage(Uri.parse("smb://other.example/x.mkv")));
    }
}
