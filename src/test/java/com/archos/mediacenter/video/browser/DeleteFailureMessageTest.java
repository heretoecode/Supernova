package com.archos.mediacenter.video.browser;

import static org.junit.Assert.assertEquals;

import android.net.Uri;
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
    /** Stands in for Sardine's exception: a WebDAV failure that carries the server's HTTP status. */
    public static final class WebdavStatusException extends IOException
    {
        private final int code;

        WebdavStatusException(int code)
        {
            super("HTTP " + code);
            this.code = code;
        }

        public int getStatusCode()
        {
            return code;
        }
    }

    private static IOException status(int code)
    {
        return new WebdavStatusException(code);
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

    /** M2 (owner, 26 Nov 2026): "no such file" means already gone; a lost connection never does. */
    @org.junit.Test
    public void alreadyGoneOnlyWhenTheServerSaysNoSuchFile()
    {
        org.junit.Assert.assertTrue(Delete.alreadyGone(null));
        org.junit.Assert.assertTrue(Delete.alreadyGone(new java.io.FileNotFoundException("/tv/MobLand/S02E09.mkv")));
        org.junit.Assert.assertTrue(Delete.alreadyGone(new com.jcraft.jsch.SftpException(2, "No such file")));
        org.junit.Assert.assertTrue(Delete.alreadyGone(new java.io.IOException("STATUS_OBJECT_NAME_NOT_FOUND")));
        org.junit.Assert.assertFalse(Delete.alreadyGone(new java.net.ConnectException("Connection refused")));
        org.junit.Assert.assertFalse(Delete.alreadyGone(new com.jcraft.jsch.SftpException(3, "Permission denied")));
        org.junit.Assert.assertEquals(
            "This file was already gone from seedbox.example, so it's been removed from your library.",
            Delete.describe(android.net.Uri.parse("sftp://seedbox.example/tv/a.mkv"), Delete.REASON_MISSING, -1));
    }
}
