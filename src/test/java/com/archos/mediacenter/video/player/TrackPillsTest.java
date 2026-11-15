package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import com.archos.mediacenter.video.utils.VideoUtils;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class TrackPillsTest
{
    @Test
    public void audioTracksReadLikeTheMockUp()
    {
        TrackDetails original = TrackDetails.of("eng", "eac3", "6", VideoUtils.AV_DISPOSITION_ORIGINAL);
        assertEquals("English", original.language());
        assertEquals(List.of("Original", "Dolby Digital Plus", "5.1"), PlaybackSubtitlePanel.pills(original));
        TrackDetails described = TrackDetails.of("eng", "ac3", "2", VideoUtils.AV_DISPOSITION_DESCRIPTIONS);
        assertEquals(List.of("Audio description", "Dolby Digital", "Stereo"), PlaybackSubtitlePanel.pills(described));
    }
}
