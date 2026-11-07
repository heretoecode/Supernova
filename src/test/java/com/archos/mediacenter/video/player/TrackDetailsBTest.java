package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class TrackDetailsBTest
{
    @Test public void typedTrackFieldsSeparateLanguageOriginalCodecChannelsAndFlags()
    {
        TrackDetails audio = TrackDetails.of("eng", "E-AC-3", "6 channels", 0x0004 | 0x0100);
        assertEquals("English", audio.language());
        assertEquals("Dolby Digital Plus", audio.format());
        assertEquals("5.1", audio.channels());
        assertTrue(audio.original());
        assertTrue(audio.audioDescription());
        assertFalse(audio.forced());
        TrackDetails subtitle = TrackDetails.of("en", "TEXT", "", 0x0080 | 0x0040);
        assertEquals("", subtitle.format());
        assertTrue(subtitle.sdh());
        assertTrue(subtitle.forced());
        assertEquals("Dolby Digital", TrackDetails.of("en", "AC3", "2", 0).format());
        assertEquals("Stereo", TrackDetails.of("en", "AC3", "2", 0).channels());
    }


}
