package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.View;
import android.widget.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewDialogStackTest {
    @Test @Config(qualifiers="w960dp-h540dp") public void frequencyChoiceUsesItsOpenerAsAnAnchor(){
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try{
            Activity activity=host.get();FrameLayout root=new FrameLayout(activity);Button opener=new Button(activity);opener.setFocusableInTouchMode(true);FrameLayout.LayoutParams size=new FrameLayout.LayoutParams(180,50);size.leftMargin=500;size.topMargin=100;root.addView(opener,size);activity.setContentView(root);
            root.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));root.layout(0,0,960,540);opener.requestFocus();Shadows.shadowOf(activity).setCurrentFocus(opener);
            Dialog choice=PreviewDialog.choose(activity,"Frequency",new String[]{"15 minutes","30 minutes","1 hour","6 hours","24 hours"},2,n->{});
            android.view.WindowManager.LayoutParams position=choice.getWindow().getAttributes();
            assertEquals(android.view.Gravity.TOP|android.view.Gravity.LEFT,position.gravity);
            assertTrue(position.x>0);assertTrue(position.y>=0);
            View selected=choice.getWindow().getDecorView().findViewWithTag("preview-check:2");assertEquals(View.VISIBLE,selected.getVisibility());
            choice.dismiss();assertTrue(opener.hasFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void childBackRestoresMoreRowThenParentBackRestoresHero() {
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            Activity activity=host.get();Button hero=new Button(activity);hero.setText("More");hero.setFocusableInTouchMode(true);
            Button other=new Button(activity);other.setText("Other");other.setFocusableInTouchMode(true);
            LinearLayout controls=new LinearLayout(activity);controls.addView(hero);controls.addView(other);activity.setContentView(controls);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();hero.requestFocus();
            // ShadowActivity stubs getCurrentFocus with an explicitly supplied field.
            Shadows.shadowOf(activity).setCurrentFocus(hero);assertTrue(hero.hasFocus());
            Dialog parent=PreviewDialog.choose(activity,"More",new String[]{"Versions","Artwork"},0,java.util.Collections.emptySet(),false,n->{});
            other.requestFocus();assertFalse(hero.hasFocus());
            View opener=(View)parent.getWindow().getDecorView().findViewWithTag("preview-label:1").getParent();assertTrue(opener.requestFocus());
            Dialog child=PreviewDialog.read(activity,"Artwork","Fixture");
            child.dismiss();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertTrue(parent.isShowing());assertSame(opener,parent.getCurrentFocus());
            parent.dismiss();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertSame(hero,activity.getCurrentFocus());assertTrue(hero.hasFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void rebuiltOpenerRestoresSemanticPeerInSameWindow() {
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            Activity activity=host.get();activity.setContentView(new FrameLayout(activity));
            Dialog parent=PreviewDialog.create(activity);LinearLayout panel=new LinearLayout(activity);
            Button opener=new Button(activity);opener.setTag("semantic:child");opener.setFocusableInTouchMode(true);panel.addView(opener);parent.setContentView(panel);parent.show();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();opener.requestFocus();assertSame(opener,parent.getCurrentFocus());
            Dialog child=PreviewDialog.read(activity,"Edit","Fixture");panel.removeAllViews();
            Button replacement=new Button(activity);replacement.setTag("semantic:child");replacement.setFocusableInTouchMode(true);panel.addView(replacement);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            child.dismiss();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertSame(replacement,parent.getCurrentFocus());parent.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void dismissingCoveredParentDoesNotStealChildFocus() {
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            Activity activity=host.get();Button hero=new Button(activity);hero.setFocusableInTouchMode(true);activity.setContentView(hero);hero.requestFocus();
            Dialog parent=PreviewDialog.choose(activity,"More",new String[]{"Artwork"},0,n->{});
            Dialog child=PreviewDialog.read(activity,"Artwork","Fixture");View focused=child.getCurrentFocus();
            parent.dismiss();assertTrue(child.isShowing());assertSame(focused,child.getCurrentFocus());child.dismiss();
        }finally{host.pause().stop().destroy();}
    }
}
