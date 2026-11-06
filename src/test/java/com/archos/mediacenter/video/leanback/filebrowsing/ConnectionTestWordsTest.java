package com.archos.mediacenter.video.leanback.filebrowsing;

import static org.junit.Assert.assertEquals;

import com.archos.filecorelibrary.connections.DraftConnectionTest.Field;
import org.junit.Test;

public class ConnectionTestWordsTest
{
    @Test
    public void successCountsFoldersAndVideos()
    {
        assertEquals("Connected · 2 folders, 21 videos found.",
                     ConnectionTestWords.message(false, Field.NONE, null, 2, 21));
        assertEquals("Connected · 1 folder, 1 video found.", ConnectionTestWords.message(false, Field.NONE, "", 1, 1));
    }

    @Test
    public void failureNamesTheFieldAndKeepsThePlainReason()
    {
        assertEquals("Check the user name and password. The server refused the sign-in.",
                     ConnectionTestWords.message(false, Field.SIGN_IN, "The server refused the sign-in", 0, 0));
        assertEquals("Check the address. Connection timed out.",
                     ConnectionTestWords.message(false, Field.ADDRESS, "Connection timed out", 0, 0));
        assertEquals("Check the port.", ConnectionTestWords.message(false, Field.PORT, null, 0, 0));
        assertEquals("Check the folder.", ConnectionTestWords.message(false, Field.FOLDER, "", 0, 0));
        assertEquals("The server's certificate was not accepted. Unknown issuer.",
                     ConnectionTestWords.message(false, Field.CERTIFICATE, "Unknown issuer.", 0, 0));
    }

    @Test
    public void cancelledSaysStopped()
    {
        assertEquals("Test stopped.", ConnectionTestWords.message(true, Field.ADDRESS, "Test stopped", 0, 0));
    }
}
