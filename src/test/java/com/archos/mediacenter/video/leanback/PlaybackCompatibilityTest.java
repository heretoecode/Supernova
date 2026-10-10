package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.media.MediaCodecInfo;
import android.view.Display;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PlaybackCompatibilityTest {
    private Movie movie(String range,int profile,int baseCompatibility) {
        Movie video=new Movie(1,"/storage/misleading-SDR-name.mkv","Film",1,"Plot",2024,7,"",null,100000,0,0,0,false,false,false,false,550,0,3840,2160,null,null,null,null,0,1,100000,0);
        video.setPreviewDynamicRange(range);video.setPreviewDolbyVision(profile,baseCompatibility);return video;
    }
    private PlaybackCompatibility device(int hdrType,String mime,int profile,int maximumWidth) {
        PlaybackCompatibility device=new PlaybackCompatibility();ReflectionHelpers.setField(device,"displayKnown",true);
        Set<Integer> hdr=ReflectionHelpers.getField(device,"hdr");hdr.add(hdrType);
        MediaCodecInfo.CodecProfileLevel level=new MediaCodecInfo.CodecProfileLevel();level.profile=profile;level.level=1<<20;
        android.media.MediaFormat format=android.media.MediaFormat.createVideoFormat(mime,maximumWidth,2160);
        MediaCodecInfo.CodecCapabilities codec=org.robolectric.shadows.MediaCodecInfoBuilder.CodecCapabilitiesBuilder.newBuilder().setMediaFormat(format).setProfileLevels(new MediaCodecInfo.CodecProfileLevel[]{level}).setColorFormats(new int[]{MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible}).setIsEncoder(false).build();
        Map<String,List<MediaCodecInfo.CodecCapabilities>> codecs=ReflectionHelpers.getField(device,"decoders");codecs.put(mime,List.of(codec));return device;
    }
    @Test public void dolbyVisionUsesActualSourceProfileAndDecoderSize() {
        PlaybackCompatibility device=device(Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION,"video/dolby-vision",1<<8,3840);
        assertTrue(device.compatible(movie("Dolby Vision",8,0)));
        assertFalse(device.compatible(movie("Dolby Vision",5,0)));
        assertFalse(device(Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION,"video/dolby-vision",1<<8,1920).compatible(movie("Dolby Vision",8,0)));
    }
    @Test public void verifiedProfileEightBaseLayerCanPlayAsHdr10() {
        PlaybackCompatibility device=device(Display.HdrCapabilities.HDR_TYPE_HDR10,"video/hevc",2,3840);
        Movie file=movie("Dolby Vision",8,1);assertTrue(device.compatible(file));assertEquals(1,device.hdrBonus(file,"Dolby Vision"));
        assertFalse(device.compatible(movie("Dolby Vision",8,0)));
    }
    @Test public void hdr10PlusFallsBackToHdr10WithoutInventingDisplaySupport() {
        PlaybackCompatibility device=device(Display.HdrCapabilities.HDR_TYPE_HDR10,"video/hevc",2,3840);
        Movie file=movie("HDR10+",0,0);assertTrue(device.compatible(file));assertEquals(1,device.hdrBonus(file,"HDR10+"));
        assertEquals(2,device(Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS,"video/hevc",2,3840).hdrBonus(file,"HDR10+"));
    }
}
