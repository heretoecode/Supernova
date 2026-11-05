package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import android.app.Application;
import java.io.File;
import java.nio.file.Files;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.json.JSONObject;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class ThumbnailDiskCacheTest
{
    private byte[] jpeg(int bytes)
    {
        byte[] data = new byte[bytes];
        data[0] = (byte)255;
        data[1] = (byte)216;
        return data;
    }

    @Test public void restartRebuildsIndexNearestHonorsInclusiveIntervalAndWatchedDeletesWholeSet() throws Exception
    {
        File root = Files.createTempDirectory("seek-cache").toFile();
        ThumbnailDiskCache cache = new ThumbnailDiskCache(root, "same-file", 100000);
        cache.put(10000, jpeg(100));
        cache.put(30000, jpeg(100));
        ThumbnailDiskCache restored = new ThumbnailDiskCache(root, "same-file", 100000);
        File indexFile = new File(restored.exact(10000).getParentFile(), "index.json");
        JSONObject index = new JSONObject(Files.readString(indexFile.toPath()));
        assertEquals(1, index.getInt("schema"));
        assertEquals(2, index.getJSONArray("frames").length());
        assertNotNull(restored.nearest(20000, 10000));
        assertNull(restored.nearest(20000, 9999));
        restored.watched();
        assertNull(restored.nearest(10000, 10000));
        assertTrue(root.delete());
    }

    @Test public void lruCapEvictsOldestVideoAndCountsIndexBytesToo() throws Exception
    {
        File root = Files.createTempDirectory("seek-lru").toFile();
        ThumbnailDiskCache first = new ThumbnailDiskCache(root, "first", 100000);
        first.put(0, jpeg(500));
        File firstDir = root.listFiles()[0];
        new File(firstDir, "index.json").setLastModified(1);
        ThumbnailDiskCache second = new ThumbnailDiskCache(root, "second", 800);
        second.put(0, jpeg(500));
        assertTrue(second.bytes() <= 800);
        assertTrue(!firstDir.exists());
        second.watched();
        assertTrue(root.delete());
    }
    @Test public void manualWatchedCleanupRemovesEveryCachedVersionOfOnlyThatOwner() throws Exception
    {
        File root = Files.createTempDirectory("seek-watched").toFile();
        ThumbnailDiskCache old = new ThumbnailDiskCache(root, "file-size-old", 100000, "same-owner");
        ThumbnailDiskCache current = new ThumbnailDiskCache(root, "file-size-new", 100000, "same-owner");
        ThumbnailDiskCache other = new ThumbnailDiskCache(root, "other-file", 100000, "other-owner");
        old.put(0, jpeg(100));
        current.put(10000, jpeg(100));
        other.put(0, jpeg(100));
        ThumbnailDiskCache.deleteOwner(root, "same-owner");
        assertNull(old.exact(0));
        assertNull(current.exact(10000));
        assertNotNull(other.exact(0));
        other.watched();
        assertTrue(root.delete());
    }
}
