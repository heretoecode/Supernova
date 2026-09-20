package com.archos.mediacenter.video.player;

import android.app.Application;
import com.archos.mediacenter.video.utils.VideoMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewTechnicalInfoTest {
    @Test public void missingActiveMetadataDoesNotStartProbe(){assertTrue(PreviewTechnicalInfo.describe(null,"webdavs",0).contains("not available"));}
    @Test public void emptySnapshotHasNoNullTrackFailure(){String text=PreviewTechnicalInfo.describe(new VideoMetadata(),"webdavs",0);assertTrue(text.contains("Source: webdavs"));assertFalse(text.contains("null"));}
    @Test public void episodeInformationToTechnicalPanelKeepsOwningActivity(){
        org.robolectric.android.controller.ActivityController<android.app.Activity> host=Robolectric.buildActivity(android.app.Activity.class).setup();
        try{
            android.app.Activity activity=host.get();activity.setContentView(new android.widget.TextView(activity));
            PreviewPlaybackInfo.show(activity,"Example series","Season 1 · Episode 1",null,()->{},()->PreviewTechnicalInfo.show(activity,new VideoMetadata(),android.net.Uri.parse("webdavs://example/file.mkv"),0));
            android.app.Dialog information=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(information.isShowing());
            android.view.View action=find(information.getWindow().getDecorView(),"File and technical details");assertNotNull(action);action.performClick();
            android.app.Dialog technical=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(technical.isShowing());assertNotNull(find(technical.getWindow().getDecorView(),"File & Technical Details"));
            assertNull(Shadows.shadowOf(activity).getNextStartedActivity());assertFalse(activity.isFinishing());technical.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    private android.view.View find(android.view.View root,String text){if(root instanceof android.widget.TextView&&text.contentEquals(((android.widget.TextView)root).getText()))return root;if(root instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)root).getChildCount();i++){android.view.View found=find(((android.view.ViewGroup)root).getChildAt(i),text);if(found!=null)return found;}return null;}
}
