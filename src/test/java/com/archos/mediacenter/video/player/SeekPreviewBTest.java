package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.archos.medialib.AndroidMediaMetadataRetriever;
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

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SeekPreviewBTest
{
    private static final class Worker implements Executor
    {
        final ArrayDeque<Runnable> pending = new ArrayDeque<>();
        @Override public void execute(Runnable task)
        {
            pending.add(task);
        }
        void drain()
        {
            while (!pending.isEmpty())
                pending.remove().run();
        }
    }

    @Test public void localScrubCacheMissRequestsTheLatestPositionInsteadOfOnlyReadingDisk() throws Exception
    {
        miss(Uri.parse("file:///storage/local-preview-b.mkv"), null);
    }

    @Test public void putioScrubCacheMissUsesTheSameOwnedExtractionPipeline() throws Exception
    {
        miss(Uri.parse("putio://account/video/123"), Uri.parse("https://example.invalid/signed-video?token=test"));
    }

    private void miss(Uri uri, Uri resolved) throws Exception
    {
        Worker worker = new Worker();
        List<Long> extracted = new ArrayList<>();
        Bitmap bitmap = Bitmap.createBitmap(32, 18, Bitmap.Config.ARGB_8888);
        SeekThumbnails engine = new SeekThumbnails(RuntimeEnvironment.getApplication(),
                new SeekThumbnails.Source(uri, resolved, 120000, 333, "32x18", "AVC"), () -> 0,
                () -> new SeekThumbnails.FrameExtractor() {
                    @Override public SeekThumbnails.Frame extract(SeekThumbnails.Source source, long position) {
                        assertEquals(resolved, source.resolvedUri);
                        extracted.add(position);
                        return new SeekThumbnails.Frame(bitmap, -1);
                    }
                    @Override public void cancel()
                    {
                    }
                    @Override public void close()
                    {
                    }
                }, worker, new Handler(Looper.getMainLooper()), () -> 0);
        try {
            engine.state(false, false, true, false, false);
            assertNull(engine.nearest(20000));
            assertNull(engine.nearest(50000));
            worker.drain();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertEquals(List.of(50000L), extracted);
            assertSame(bitmap, engine.nearest(50000));
        } finally {
            engine.close();
            worker.drain();
        }
    }

    @Test public void actualExtractorUsesLocalOrResolvedUrlAndConvertsMillisecondsToMicroseconds() throws Exception
    {
        Context app = RuntimeEnvironment.getApplication();
        Bitmap frame = Bitmap.createBitmap(320, 180, Bitmap.Config.ARGB_8888);
        try (var construction = mockConstruction(AndroidMediaMetadataRetriever.class, (retriever, call) ->
                when(retriever.getScaledSyncFrame(anyLong(), eq(320), eq(180))).thenReturn(frame))) {
            for (Uri input : List.of(Uri.parse("file:///storage/file.mkv"),
                    Uri.parse("https://example.invalid/file"))) {
                Uri original = input.getScheme().equals("https") ? Uri.parse("putio://account/123") : input;
                try (KeyframeExtractor extractor = new KeyframeExtractor(app)) {
                    assertSame(frame, extractor.extract(new SeekThumbnails.Source(original, input, 120000, 12,
                            "320x180", "AVC"), 60000).bitmap);
                }
                AndroidMediaMetadataRetriever retriever = construction.constructed()
                        .get(construction.constructed().size() - 1);
                verify(retriever).setDataSource(app, input);
                verify(retriever).getScaledSyncFrame(60000000L, 320, 180);
                verify(retriever).release();
            }
        }
    }
}
