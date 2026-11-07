package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import android.app.Application;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PartPicturesBTest
{
    @Test public void introMergeRenumbersGenericRemainderAndEveryPartGetsAPictureAfterEngineStartup()
    {
        PlaybackParts parts = PlaybackParts.merge(List.of(
                new PlaybackParts.Part("Chapter 03", PlaybackParts.Kind.CHAPTER, 0, 60000),
                new PlaybackParts.Part("Chapter 05", PlaybackParts.Kind.CHAPTER, 60000, 120000)), List.of(),
                List.of(new PlaybackParts.Part("Intro", PlaybackParts.Kind.INTRO, 0, 10000)), 120000, true).numbered();
        assertEquals(List.of("Intro", "Chapter 1", "Chapter 2"), parts.parts().stream().map(p -> p.name).toList());
        DeferredPartFrames deferred = new DeferredPartFrames();
        PartPictures pictures = new PartPictures(parts, null, deferred, picture -> {});
        assertEquals(3, pictures.pictures().size());
        assertNull(pictures.pictures().get(0).bitmap);
        List<Long> requested = new ArrayList<>();
        Bitmap bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        deferred.attach((position, callback) -> {
            requested.add(position);
            callback.accept(bitmap);
        });
        assertEquals(List.of(2000L, 12000L, 62000L), requested);
        for (PartPictures.Picture picture : pictures.pictures())
            assertSame(bitmap, picture.bitmap);
        pictures.close();
        deferred.close();
    }

    @Test public void everyPictureOnALongTimelineCanCompleteWithoutOverflowingTheStartupQueue()
    {
        List<PlaybackParts.Part> chapters = new ArrayList<>();
        for (int i = 0; i < 300; i++)
            chapters.add(new PlaybackParts.Part("Chapter " + i, PlaybackParts.Kind.CHAPTER,
                    i * 10000L, (i + 1) * 10000L));
        PlaybackParts parts = PlaybackParts.merge(chapters, List.of(), List.of(), 3000000, false);
        DeferredPartFrames deferred = new DeferredPartFrames();
        PartPictures pictures = new PartPictures(parts, null, deferred, picture -> {});
        List<Long> requests = new ArrayList<>();
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        deferred.attach((position, callback) -> {
            requests.add(position);
            callback.accept(bitmap);
        });
        assertEquals(300, requests.size());
        for (PartPictures.Picture picture : pictures.pictures())
            assertSame(bitmap, picture.bitmap);
        pictures.close();
        deferred.close();
    }

    @Test public void replacementAndCloseFenceQueuedAndLateFrames()
    {
        DeferredPartFrames deferred = new DeferredPartFrames();
        List<Consumer<Bitmap>> callbacks = new ArrayList<>();
        List<Bitmap> delivered = new ArrayList<>();
        deferred.frameAt(100, delivered::add);
        deferred.attach((position, callback) -> callbacks.add(callback));
        deferred.close();
        callbacks.get(0).accept(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        assertEquals(0, delivered.size());
        DeferredPartFrames queued = new DeferredPartFrames();
        queued.frameAt(200, delivered::add);
        queued.close();
        queued.attach((position, callback) -> delivered.add(null));
        assertEquals(0, delivered.size());
    }

    @Test public void adapterKeepsAPreStartupFrameSourceAndSuppliesTheNumberedTimeline()
    {
        Player previous = Player.sPlayer;
        PlayerService previousService = PlayerService.sPlayerService;
        Player player = org.mockito.Mockito.mock(Player.class);
        PlayerService service = org.mockito.Mockito.mock(PlayerService.class);
        PlayerActivity activity = org.mockito.Mockito.mock(PlayerActivity.class);
        DeferredPartFrames frames = new DeferredPartFrames();
        org.mockito.Mockito.when(player.getDuration()).thenReturn(10000);
        PlaybackParts parts = PlaybackParts.merge(List.of(
                new PlaybackParts.Part("Chapter 03", PlaybackParts.Kind.CHAPTER, 0, 10000)),
                List.of(), List.of(), 10000, false);
        org.mockito.Mockito.when(activity.previewParts(org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.eq(10000L))).thenReturn(parts);
        org.mockito.Mockito.when(service.partFrameSource()).thenReturn(frames);
        try {
            Player.sPlayer = player;
            PlayerService.sPlayerService = service;
            PlayerStripSource adapter = new PlayerStripSource(activity);
            assertSame(frames, adapter.frames());
            assertEquals("Chapter 1", adapter.parts().parts().get(0).name);
        } finally {
            Player.sPlayer = previous;
            PlayerService.sPlayerService = previousService;
            frames.close();
        }
    }

    @Test public void deferredRequestsAreBoundedAndAuthoredChapterNamesSurvive()
    {
        DeferredPartFrames deferred = new DeferredPartFrames();
        List<Bitmap> rejected = new ArrayList<>();
        for (int i = 0; i < 257; i++)
            deferred.frameAt(i, rejected::add);
        assertEquals(1, rejected.size());
        assertNull(rejected.get(0));
        deferred.close();
        PlaybackParts parts = PlaybackParts.merge(List.of(
                new PlaybackParts.Part("Arrival", PlaybackParts.Kind.CHAPTER, 0, 10000)),
                List.of(), List.of(), 10000, false).numbered();
        assertEquals("Arrival", parts.parts().get(0).name);
    }
}
