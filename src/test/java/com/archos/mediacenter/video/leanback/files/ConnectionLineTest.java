package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConnectionLineTest
{
    private static final long HOUR = 3_600_000L;

    @Test
    public void reachableConnectionsCountTheirVideos()
    {
        assertEquals("SMB · 512 videos in your library", ConnectionLine.second("smb", true, 512, 0, 0));
        assertEquals("Reachable", ConnectionLine.pill(true));
    }

    @Test
    public void unreachableConnectionsSayWhenTheyWereLastSeen()
    {
        assertEquals("SFTP · last seen yesterday", ConnectionLine.second("sftp", false, 0, HOUR, 26 * HOUR));
        assertEquals("WebDAV · not reached yet", ConnectionLine.second("webdavs", false, 0, 0, HOUR));
        assertEquals("Not reachable", ConnectionLine.pill(false));
        assertEquals("Checking…", ConnectionLine.pill(null));
    }
}
