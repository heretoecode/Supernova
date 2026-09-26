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
    @Test public void childBackRestoresMoreRowThenParentBackRestoresHero() {
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            Activity activity=host.get();Button hero=new Button(activity);hero.setText("More");hero.setFocusableInTouchMode(true);activity.setContentView(hero);hero.requestFocus();
            Dialog parent=PreviewDialog.choose(activity,"More",new String[]{"Versions","Artwork"},0,java.util.Collections.emptySet(),false,n->{});
            View opener=(View)parent.getWindow().getDecorView().findViewWithTag("preview-label:1").getParent();assertTrue(opener.requestFocus());
            Dialog child=PreviewDialog.read(activity,"Artwork","Fixture");
            child.dismiss();assertTrue(parent.isShowing());assertSame(opener,parent.getCurrentFocus());
            parent.dismiss();assertSame(hero,activity.getCurrentFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void rebuiltOpenerRestoresSemanticPeerInSameWindow() {
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            Activity activity=host.get();activity.setContentView(new FrameLayout(activity));
            Dialog parent=PreviewDialog.create(activity);LinearLayout panel=new LinearLayout(activity);
            Button opener=new Button(activity);opener.setTag("semantic:child");opener.setFocusableInTouchMode(true);panel.addView(opener);parent.setContentView(panel);parent.show();opener.requestFocus();
            Dialog child=PreviewDialog.read(activity,"Edit","Fixture");panel.removeAllViews();
            Button replacement=new Button(activity);replacement.setTag("semantic:child");replacement.setFocusableInTouchMode(true);panel.addView(replacement);
            child.dismiss();assertSame(replacement,parent.getCurrentFocus());parent.dismiss();
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
