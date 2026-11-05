package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
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

@RunWith(RobolectricTestRunner.class) @Config(application = Application.class, sdk = 30) public class PartPicturesTest
{
    @Test public void requestsTwoSecondsInUsesStillAndIgnoresLateFramesAfterClose()
    {
        List<Consumer<Bitmap>> callbacks = new ArrayList<>();
        Bitmap still = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Bitmap frame = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        List<Long> positions = new ArrayList<>();
        PartPictures pictures =
            new PartPictures(PlaybackParts.merge(List.of(), List.of(), List.of(), 10000, true), still,
                             (position, callback)
                                 -> {
                                 positions.add(position);
                                 callbacks.add(callback);
                             },
                             picture -> {});
        assertEquals(List.of(2000L), positions);
        assertSame(still, pictures.pictures().get(0).bitmap);
        pictures.close();
        callbacks.get(0).accept(frame);
        assertSame(still, pictures.pictures().get(0).bitmap);
    }
}
