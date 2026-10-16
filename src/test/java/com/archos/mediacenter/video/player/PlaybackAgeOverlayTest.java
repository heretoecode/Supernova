package com.archos.mediacenter.video.player;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PlaybackAgeOverlayTest {
    @Test public void missingAndUnratedClassificationsDoNotRender(){for(String value:new String[]{""," ","N/A","NR","Unrated","Unknown","Not Yet Rated","TBC","No rating available","null","<script>"})assertFalse(value,PlaybackAgeOverlay.valid(value));for(String value:new String[]{"12A","15","18","TV-MA","PG-13"})assertTrue(value,PlaybackAgeOverlay.valid(value));}
    @Test public void changingTitleRequiresANewPlaybackEstablishedSignal(){PlaybackAgeOverlay view=new PlaybackAgeOverlay(RuntimeEnvironment.getApplication(),null);view.playbackStarted();com.archos.mediacenter.utils.videodb.VideoDbInfo info=new com.archos.mediacenter.utils.videodb.VideoDbInfo();info.id=2;view.bind(info);assertFalse((Boolean)ReflectionHelpers.getField(view,"output"));}
    @Test public void outputIsRequiredAndDwellExcludesBothTransitions(){var host=Robolectric.buildActivity(android.app.Activity.class).setup().visible();try{PlaybackAgeOverlay view=new PlaybackAgeOverlay(host.get(),null);host.get().setContentView(view);ReflectionHelpers.setField(view,"classification","15");assertEquals(android.view.View.GONE,view.getVisibility());assertFalse(view.isFocusable());view.playbackStarted();assertEquals(android.view.View.VISIBLE,view.getVisibility());assertEquals(10000,PlaybackAgeOverlay.DWELL_MS);view.stop();assertEquals(android.view.View.GONE,view.getVisibility());view.playbackStarted();assertEquals(android.view.View.GONE,view.getVisibility());}finally{host.pause().stop().destroy();}}
}
