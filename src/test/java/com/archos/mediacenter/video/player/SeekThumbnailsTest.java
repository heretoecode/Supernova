package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class) @Config(application = Application.class, sdk = 30) public class SeekThumbnailsTest
{
    private static final class Worker implements Executor
    {
        final ArrayDeque<Runnable> tasks = new ArrayDeque<>();
        @Override public void execute(Runnable task)
        {
            tasks.add(task);
        }
        void next()
        {
            tasks.remove().run();
        }
        void drain()
        {
            while (!tasks.isEmpty())
                next();
        }
    }

    @Test public void bufferingDefersPriorityAndDuplicateRequestsShareOneOwnedExtraction() throws Exception
    {
        Worker io = new Worker();
        List<Long> positions = new ArrayList<>();
        List<Bitmap> callbacks = new ArrayList<>();
        Bitmap bitmap = Bitmap.createBitmap(32, 18, Bitmap.Config.ARGB_8888);
        SeekThumbnails engine = new SeekThumbnails(
            RuntimeEnvironment.getApplication(),
            new SeekThumbnails.Source(Uri.parse("/storage/priority.mkv"), null, 100000, 300, "32x18", "AVC"),
            () -> 30000, () -> new SeekThumbnails.FrameExtractor() {
                @Override public SeekThumbnails.Frame extract(SeekThumbnails.Source source, long position)
                {
                    positions.add(position);
                    return new SeekThumbnails.Frame(bitmap, -1);
                }
                @Override public void cancel()
                {
                }
                @Override public void close()
                {
                }
            }, io, new Handler(Looper.getMainLooper()), () -> 0);
        engine.state(true, false, true, false, false);
        engine.frameAt(2000, callbacks::add);
        engine.frameAt(2000, callbacks::add);
        io.drain();
        assertTrue(positions.isEmpty());
        engine.state(false, false, true, false, false);
        io.drain();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(List.of(2000L), positions);
        assertEquals(2, callbacks.size());
        assertNotNull(engine.nearest(12000));
        assertNull(engine.nearest(12001));
        engine.close();
        io.drain();
    }

    @Test public void closeDuringExtractionCancelsAndFencesQueuedUiDelivery() throws Exception
    {
        Worker io = new Worker();
        SeekThumbnails[] owner = new SeekThumbnails[1];
        int[] cancelled = {0}, delivered = {0};
        owner[0] = new SeekThumbnails(
            RuntimeEnvironment.getApplication(),
            new SeekThumbnails.Source(Uri.parse("smb://host/cancel.mkv"), null, 100000, 300, "32x18", "AVC"),
            () -> 0, () -> new SeekThumbnails.FrameExtractor() {
                @Override public SeekThumbnails.Frame extract(SeekThumbnails.Source source, long position)
                {
                    owner[0].close();
                    return new SeekThumbnails.Frame(Bitmap.createBitmap(32, 18, Bitmap.Config.ARGB_8888), -1);
                }
                @Override public void cancel()
                {
                    cancelled[0]++;
                }
                @Override public void close()
                {
                }
            }, io, new Handler(Looper.getMainLooper()), () -> 0);
        owner[0].frameAt(2000, bitmap -> delivered[0]++);
        io.drain();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(1, cancelled[0]);
        assertEquals(0, delivered[0]);
        assertNull(owner[0].nearest(2000));
    }
    @Test public void sourcesSelectLocalOrNetworkPolicyAndNormalizeLocalCacheOwnership() throws Exception
    {
        for (String uri :
             new String[] {"/storage/Movie #1.mkv", "file:///storage/Movie%20%231.mkv", "content://media/42"})
        {
            SeekThumbnails.Source source = new SeekThumbnails.Source(Uri.parse(uri), null, 10000, 0, null, null);
            assertTrue(!source.network);
            assertEquals(10000, new SeekThumbnailPolicy(source.network).intervalMs());
        }
        for (String scheme : new String[] {"smb", "smbj", "sftp", "sshj", "webdav", "webdavs", "upnp", "https"})
        {
            SeekThumbnails.Source source =
                new SeekThumbnails.Source(Uri.parse(scheme + "://host/video"), null, 10000, 0, null, null);
            assertTrue(source.network);
            assertEquals(45000, new SeekThumbnailPolicy(source.network).intervalMs());
        }
        assertEquals(SeekThumbnails.ownerIdentity(Uri.parse("/storage/Movie #1.mkv")),
                     SeekThumbnails.ownerIdentity(Uri.parse("file:///storage/Movie%20%231.mkv")));
    }
}
