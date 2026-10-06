package com.archos.mediacenter.video.player;

import android.app.*;
import android.view.*;
import com.archos.mediacenter.video.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewTransportTest {
    @Test public void fourRealHudControlsKeepPauseAtThePhysicalCentreAcrossWidths(){
        Activity host=Robolectric.buildActivity(PreviewPlaybackMenusTest.TopNavigationTestHost.class).setup().get();
        View root=LayoutInflater.from(host).inflate(R.layout.player_controller_experimental,null);
        root.findViewById(R.id.control_bar).setVisibility(View.VISIBLE);
        PreviewTransport transport=root.findViewById(R.id.preview_transport);
        for(int width:new int[]{960,1280,640}){
            root.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));root.layout(0,0,width,540);
            View pause=root.findViewById(R.id.pause);View pauseGroup=(View)pause.getParent();
            assertEquals(transport.getWidth()/2f,pauseGroup.getLeft()+pause.getLeft()+pause.getWidth()/2f,.5f);
            assertEquals(View.GONE,((View)root.findViewById(R.id.preview_info).getParent()).getVisibility());
            int visible=0,last=-1;for(int id:new int[]{R.id.preview_subtitles,R.id.preview_audio,R.id.pause,R.id.preview_more}){
                View control=root.findViewById(id),group=(View)control.getParent();assertEquals(View.VISIBLE,group.getVisibility());assertTrue(group.getLeft()>last);last=group.getLeft();visible++;
                assertTrue(group.getLeft()>=0);assertTrue(group.getRight()<=transport.getWidth());
            }
            assertEquals(4,visible);
        }
        host.finish();
    }
}
