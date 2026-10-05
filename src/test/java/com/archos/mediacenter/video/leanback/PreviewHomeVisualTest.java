package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.graphics.Rect;
import android.view.*;
import android.widget.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.util.*;
import static org.junit.Assert.*;

/** Capture the production Home child controls, including their focused viewport bounds. */
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PreviewHomeVisualTest {
    @Test public void genreHeaderAndDoneRemainVisibleWhenMiddleScrolls()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewGenres.choose(host.get(),Arrays.asList(PreviewGenres.NAMES),new HashSet<>(Arrays.asList("Drama","Science Fiction")),values->{});
            Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();View root=measure(dialog);
            View heading=PreviewPagesTest.findText(root,"Genres · match any selected");
            View done=(View)PreviewPagesTest.findText(root,"Done").getParent();
            Rect headerBefore=bounds(root,heading),footerBefore=bounds(root,done);
            ScrollView scroll=findScroll(root);assertNotNull(scroll);assertTrue(scroll.getChildAt(0).getHeight()>scroll.getHeight());
            scroll.fullScroll(View.FOCUS_DOWN);done.requestFocus();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();root=measure(dialog);
            assertTrue(done.hasFocus());assertEquals(headerBefore,bounds(root,heading));assertEquals(footerBefore,bounds(root,done));
            PreviewPagesTest.capture(root,"home-genres-footer-focused");dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void deleteConfirmationKeepsBothButtonsInsideWindow()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("try_new_ui",true).commit();
            boolean[] deleted={false};AlertDialog dialog=(AlertDialog)PreviewDialog.confirmDelete(host.get(),"Delete row?","Titles remain in your library. Only this Home row is removed.",()->deleted[0]=true);
            View root=measure(dialog);View cancel=dialog.getButton(AlertDialog.BUTTON_NEGATIVE),confirm=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            assertTrue(cancel.hasFocus());bounds(root,cancel);bounds(root,confirm);
            confirm.requestFocus();assertTrue(confirm.hasFocus());PreviewPagesTest.capture(root,"home-delete-focused");
            cancel.performClick();assertFalse(deleted[0]);assertFalse(dialog.isShowing());
        }finally{host.pause().stop().destroy();}
    }
    static View measure(Dialog dialog){
        View root=dialog.getWindow().getDecorView();int width=dialog.getWindow().getAttributes().width,height=dialog.getWindow().getAttributes().height;
        root.measure(View.MeasureSpec.makeMeasureSpec(width>0?width:912,width>0?View.MeasureSpec.EXACTLY:View.MeasureSpec.AT_MOST),View.MeasureSpec.makeMeasureSpec(height>0?height:492,height>0?View.MeasureSpec.EXACTLY:View.MeasureSpec.AT_MOST));
        root.layout(0,0,root.getMeasuredWidth(),root.getMeasuredHeight());return root;
    }
    private static Rect bounds(View root,View child){
        Rect result=new Rect();assertTrue(child.getGlobalVisibleRect(result));assertEquals(child.getWidth(),result.width());assertEquals(child.getHeight(),result.height());
        Rect window=new Rect();assertTrue(root.getGlobalVisibleRect(window));assertTrue(window.contains(result));return result;
    }
    private static ScrollView findScroll(View root){if(root instanceof ScrollView)return (ScrollView)root;if(root instanceof ViewGroup)for(int i=0;i<((ViewGroup)root).getChildCount();i++){ScrollView result=findScroll(((ViewGroup)root).getChildAt(i));if(result!=null)return result;}return null;}
}
