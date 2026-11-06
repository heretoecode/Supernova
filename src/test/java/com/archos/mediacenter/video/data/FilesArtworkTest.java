package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class FilesArtworkTest
{
    @Test public void mosaicIsDistinctBoundedAndCannotTakeASiblingFoldersPictures()
    {
        List<FilesArtwork.Artwork> rows = List.of(
            new FilesArtwork.Artwork("file:///Films2/a", "outside"),
            new FilesArtwork.Artwork("file:///Films/a", "one"),
            new FilesArtwork.Artwork("file:///Films/b", "one"),
            new FilesArtwork.Artwork("file:///Films/c", "two"),
            new FilesArtwork.Artwork("file:///Films/sub/d", "three"),
            new FilesArtwork.Artwork("file:///Films/e", "four"),
            new FilesArtwork.Artwork("file:///Films/f", "five"));
        assertEquals(List.of("one", "two", "three", "four"), FilesArtwork.folder("file:///Films", 1, rows));
        assertEquals(List.of(), FilesArtwork.folder("file:///Films", 2, List.of()));
    }

    @Test public void absentLocalFileReturnsNoneAsynchronously() throws Exception
    {
        CountDownLatch callback = new CountDownLatch(1);
        try (FilesArtwork.Job job = FilesArtwork.still(null, () -> false, Runnable::run, image -> {
            assertNull(image);
            callback.countDown();
        }))
        {
            assertTrue(callback.await(3, TimeUnit.SECONDS));
        }
    }
}
