package com.archos.mediacenter.video.leanback.files;

import android.app.Application;
import android.net.Uri;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/** F52 (approved 17 Nov): one connection per server and login, its folders inside. Names are made up. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class ConnectionsByServerTest
{
    @Test
    public void foldersOfOneServerAndLoginBecomeOneConnection()
    {
        List<FilesPage.Connection> plain = Arrays.asList(
            new FilesPage.Connection("Put.io", Uri.parse("webdavs://webdav.put.io/Films/"), true, 120, 5, "alex"),
            new FilesPage.Connection("Put.io", Uri.parse("webdavs://webdav.put.io/TV/"), null, 30, 9, "alex"),
            new FilesPage.Connection("Seedbox", Uri.parse("sftp://seedbox.example.net/Movies"), false, 12, 0, "alex"),
            new FilesPage.Connection("Seedbox", Uri.parse("sftp://seedbox.example.net/TV Shows"), false, 8, 0, "alex"),
            new FilesPage.Connection("Second login", Uri.parse("sftp://seedbox.example.net/Other"), true, 1, 0,
                                     "sam"));
        List<FilesPage.Connection> grouped = FilesPageData.byServer(plain);
        assertEquals(3, grouped.size());
        assertEquals("Put.io", grouped.get(0).name);
        assertEquals(2, grouped.get(0).folders.size());
        assertEquals(150, grouped.get(0).videos);
        assertEquals(Boolean.TRUE, grouped.get(0).reachable);
        assertEquals("Films, TV", FilesPageData.folderNames(grouped.get(0).folders));
        assertEquals("Movies, TV Shows", FilesPageData.folderNames(grouped.get(1).folders));
        assertEquals(Boolean.FALSE, grouped.get(1).reachable);
        assertEquals("A different login on the same server stays apart", "sam", grouped.get(2).account);
    }
}
