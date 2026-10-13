package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import java.io.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class BackupWorkspaceTest {
    private static final List<BackupWorkspace.Paragraph> DETAILS=List.of(
        new BackupWorkspace.Paragraph("Your choices","Each category is independent. Titles and personal choices can reveal viewing interests."),
        new BackupWorkspace.Paragraph("Always excluded","Media, artwork, subtitle files, credentials, sessions and diagnostic recordings are excluded."),
        new BackupWorkspace.Paragraph("Locations and access","Locations are optional and start Off. Reconnect storage, services and Android permissions separately."),
        new BackupWorkspace.Paragraph("Restoration","Review the archive before confirming. Selected categories replace their configuration; unselected categories remain unchanged."),
        new BackupWorkspace.Paragraph("Matching and compatibility","Exact identities are used where available. Unresolved references remain stored. Hardware and file matching can differ."));
    private BackupWorkspace workspace(Context c){return new BackupWorkspace(c,s->{},()->{},()->{},DETAILS,List.of(new BackupWorkspace.Paragraph("1. Review","Keep a destination backup and review the validated archive before confirmation."),new BackupWorkspace.Paragraph("2. Reconnect","Grant permissions and connect storage and services separately."),new BackupWorkspace.Paragraph("3. Verify","Scan available media, fetch fresh metadata and check unresolved references.")));}
    private void layout(View view,int width,int height){view.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));view.layout(0,0,width,height);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    private void capture(View view,String name)throws Exception{Bitmap image=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);view.draw(new Canvas(image));File file=new File("build/reports/preview-ui/"+name+".png");file.getParentFile().mkdirs();try(FileOutputStream output=new FileOutputStream(file)){image.compress(Bitmap.CompressFormat.PNG,100,output);}}
    private boolean inside(View view,View root){int top=0,left=0;View cursor=view;while(cursor!=root){top+=cursor.getTop();left+=cursor.getLeft();if(!(cursor.getParent() instanceof View))return false;cursor=(View)cursor.getParent();top-=cursor.getScrollY();left-=cursor.getScrollX();}return top>=0&&left>=0&&top+view.getHeight()<=root.getHeight()&&left+view.getWidth()<=root.getWidth();}
    private int count(View view,Class<?> type){int total=type.isInstance(view)?1:0;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)total+=count(((ViewGroup)view).getChildAt(i),type);return total;}
    @Test public void allTwentySixCategoriesFitWithoutScrollingAndFinalDividerHasIndependentBottomPadding()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{BackupWorkspace workspace=workspace(host.get());host.get().setContentView(workspace);layout(workspace,628,468);host.windowFocusChanged(true);assertEquals(26,BackupCategory.values().length);assertEquals(0,count(workspace,ScrollView.class));
            int previousRisk=-1;String previousLabel="";
            for(BackupCategory category:BackupCategory.ordered()){
                TextView toggle=workspace.findViewWithTag("backup.category."+category.name());assertNotNull(category.name(),toggle);assertTrue(category.label+" bounds",inside(toggle,workspace));assertTrue(category.label+" readable target",toggle.getHeight()>=44);
                assertTrue(category.label+" full label",toggle.getLayout().getLineBottom(toggle.getLineCount()-1)<=toggle.getHeight()-toggle.getPaddingBottom());for(int line=0;line<toggle.getLineCount();line++)assertEquals(0,toggle.getLayout().getEllipsisCount(line));assertNotNull(toggle.getCompoundDrawables()[0]);assertNotNull(toggle.getCompoundDrawables()[2]);
                assertTrue(category.sensitivity>=previousRisk);if(category.sensitivity==previousRisk)assertTrue(previousLabel.compareToIgnoreCase(category.label)<0);previousRisk=category.sensitivity;previousLabel=category.label;
            }
            TextView locations=workspace.findViewWithTag("backup.category.LOCATIONS");LinearLayout cell=(LinearLayout)locations.getParent();assertEquals("backup.divider",cell.getChildAt(1).getTag());View options=workspace.findViewWithTag("backup.options");int[] at=new int[2],box=new int[2];cell.getChildAt(1).getLocationOnScreen(at);options.getLocationOnScreen(box);assertTrue("Distinct final divider and empty panel padding",box[1]+options.getHeight()-at[1]-cell.getChildAt(1).getHeight()>=20);
            assertFalse(workspace.included(BackupCategory.LOCATIONS));assertFalse(workspace.included(BackupCategory.BOOKMARKS));assertFalse(workspace.included(BackupCategory.SOURCES));assertTrue(workspace.included(BackupCategory.HISTORY));assertTrue(workspace.findViewWithTag("backup.create").requestFocus());capture(workspace,"0139-backup-right-main");
        }finally{host.pause().stop().destroy();}
    }
    @Test public void informationalPagesReplaceRightAreaAndBackRestoresExactOpener()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{BackupWorkspace workspace=workspace(host.get());host.get().setContentView(workspace);layout(workspace,628,468);
            for(String tag:List.of("backup.details","backup.instructions")){View opener=workspace.findViewWithTag(tag);assertTrue(opener.requestFocus());opener.performClick();layout(workspace,628,468);assertEquals(1,workspace.getChildCount());assertNull(workspace.findViewWithTag("backup.options"));View page=workspace.findViewWithTag(tag+".page");assertNotNull(page);assertTrue(page.hasFocus());ArrayList<View> controls=new ArrayList<>();page.addFocusables(controls,View.FOCUS_DOWN);assertEquals(1,controls.size());assertSame(page,controls.get(0));if(tag.equals("backup.details"))assertEquals(0,count(page,ScrollView.class));capture(workspace,"0139-"+tag.replace('.','-'));assertTrue(workspace.back());layout(workspace,628,468);assertEquals(tag,workspace.findFocus().getTag());}
        }finally{host.pause().stop().destroy();}
    }
    @Test public void checkboxStatePersistsAndDirectionRoutesRemainBounded(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{BackupWorkspace workspace=workspace(host.get());host.get().setContentView(workspace);layout(workspace,628,468);List<BackupCategory> rows=BackupCategory.ordered();TextView first=workspace.findViewWithTag("backup.category."+rows.get(0).name());assertTrue(first.requestFocus());first.performClick();assertFalse(workspace.included(rows.get(0)));assertFalse(workspace(host.get()).included(rows.get(0)));
            workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertEquals("backup.category."+rows.get(1).name(),workspace.findFocus().getTag());workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(first,workspace.findFocus());workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertEquals("backup.category."+rows.get(5).name(),workspace.findFocus().getTag());
            assertFalse(PreferenceManager.getDefaultSharedPreferences(host.get()).contains("include_credentials"));assertFalse(PreferenceManager.getDefaultSharedPreferences(host.get()).contains("include_media"));
        }finally{host.pause().stop().destroy();}
    }
}
