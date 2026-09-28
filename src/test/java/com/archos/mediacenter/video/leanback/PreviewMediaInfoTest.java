package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.mediacenter.video.leanback.details.PreviewMediaInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewMediaInfoTest {
    @Test public void measuredChannelsAreNotGuessedSpeakerLayouts(){
        assertEquals("AC3 · 6 channels · 48000 Hz",PreviewMediaInfo.audioTrack("AC3","6",48000));
        assertEquals("DTS · 5.1",PreviewMediaInfo.audioTrack("DTS","5.1",0));
    }
    @Test public void unknownMeasurementsAreOmitted(){
        assertEquals("",PreviewMediaInfo.audioTrack(null,"unknown",0));
        assertEquals("AAC",PreviewMediaInfo.audioTrack("AAC","0",-1));
        assertEquals("AAC",PreviewMediaInfo.audioTrack("AAC",null,0));
        assertEquals("48000 Hz",PreviewMediaInfo.audioTrack("","",48000));
    }
}
