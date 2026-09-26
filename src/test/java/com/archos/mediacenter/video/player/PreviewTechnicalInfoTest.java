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
    @Test @Config(qualifiers="w960dp-h540dp-land-mdpi") public void readPanelsFitShortContentAndCapLongContent(){
        org.robolectric.android.controller.ActivityController<android.app.Activity> host=Robolectric.buildActivity(android.app.Activity.class).setup();
        try{android.app.Dialog small=com.archos.mediacenter.video.leanback.PreviewDialog.read(host.get(),"Information","One short line");int shortHeight=small.getWindow().getAttributes().height;assertTrue(shortHeight<200);small.dismiss();StringBuilder lines=new StringBuilder();for(int i=0;i<100;i++)lines.append("A technical line\n");android.app.Dialog large=com.archos.mediacenter.video.leanback.PreviewDialog.read(host.get(),"Information",lines.toString());assertTrue(large.getWindow().getAttributes().height<=360);assertTrue(large.getWindow().getAttributes().height>shortHeight);large.dismiss();}finally{host.pause().stop().destroy();}
    }
    @Test public void missingActiveMetadataDoesNotStartProbe(){assertTrue(PreviewTechnicalInfo.describe(null,"webdavs",0).contains("not available"));}
    @Test public void emptySnapshotHasNoNullTrackFailure(){String text=PreviewTechnicalInfo.describe(new VideoMetadata(),"webdavs",0);assertTrue(text.contains("Source: webdavs"));assertFalse(text.contains("null"));}
    @Test public void episodeInformationToTechnicalPanelKeepsOwningActivity(){
        org.robolectric.android.controller.ActivityController<android.app.Activity> host=Robolectric.buildActivity(android.app.Activity.class).setup();
        try{
            android.app.Activity activity=host.get();activity.setContentView(new android.widget.TextView(activity));
            PreviewTechnicalInfo.show(activity,new VideoMetadata(),android.net.Uri.parse("webdavs://example/file.mkv"),0);
            android.app.Dialog technical=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(technical.isShowing());
            for(String label:new String[]{"Video","Audio","File","Source"})assertNotNull(find(technical.getWindow().getDecorView(),label));
            for(String label:new String[]{"File & Technical Details","file.mkv","Resume","Close"})assertNull(find(technical.getWindow().getDecorView(),label));
            assertNull(Shadows.shadowOf(activity).getNextStartedActivity());assertFalse(activity.isFinishing());technical.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    private android.view.View find(android.view.View root,String text){if(root instanceof android.widget.TextView&&text.contentEquals(((android.widget.TextView)root).getText()))return root;if(root instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)root).getChildCount();i++){android.view.View found=find(((android.view.ViewGroup)root).getChildAt(i),text);if(found!=null)return found;}return null;}
}
