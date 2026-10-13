package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import java.io.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class BackupWorkspaceDensityTest {
    @Test public void fullHdDensityKeepsAllLabelsAndFortyFourDpTargetsInsideRightArea()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            assertEquals(2,host.get().getResources().getDisplayMetrics().density,.01);
            BackupWorkspace workspace=new BackupWorkspace(host.get(),selection->{},()->{},()->{},List.of(),List.of());host.get().setContentView(workspace,new ViewGroup.LayoutParams(1256,936));
            workspace.measure(View.MeasureSpec.makeMeasureSpec(1256,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(936,View.MeasureSpec.EXACTLY));workspace.layout(0,0,1256,936);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();host.windowFocusChanged(true);
            assertEquals(1256,workspace.getWidth());assertEquals(936,workspace.getHeight());
            int[] root=new int[2];workspace.getLocationOnScreen(root);
            for(BackupCategory category:BackupCategory.values()){
                TextView toggle=workspace.findViewWithTag("backup.category."+category.name());assertNotNull(toggle);int[] point=new int[2];toggle.getLocationOnScreen(point);
                assertTrue(category.label+" target",toggle.getHeight()>=88);assertTrue(category.label+" left",point[0]>=root[0]);assertTrue(category.label+" right",point[0]+toggle.getWidth()<=root[0]+1256);assertTrue(category.label+" bottom",point[1]+toggle.getHeight()<=root[1]+936);
                assertTrue(category.label+" full text",toggle.getLayout().getLineBottom(toggle.getLineCount()-1)<=toggle.getHeight()-toggle.getPaddingBottom());for(int line=0;line<toggle.getLineCount();line++)assertEquals(0,toggle.getLayout().getEllipsisCount(line));
            }
            workspace.findViewWithTag("backup.create").requestFocus();Bitmap bitmap=Bitmap.createBitmap(1256,936,Bitmap.Config.ARGB_8888);workspace.draw(new Canvas(bitmap));File file=new File("build/reports/preview-ui/0139-backup-right-xhdpi.png");file.getParentFile().mkdirs();try(OutputStream output=new FileOutputStream(file)){bitmap.compress(Bitmap.CompressFormat.PNG,100,output);}
        }finally{host.pause().stop().destroy();}
    }
}
