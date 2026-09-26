package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewMatchReviewTest {
    @Test public void previewAndBackNeverApplyMatchAndAcceptanceRunsOnce(){
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        java.util.concurrent.atomic.AtomicInteger applied=new java.util.concurrent.atomic.AtomicInteger();
        try{
            android.app.Dialog first=PreviewDialog.review(host.get(),"Match Preview","Fixture title","Use This Match",applied::incrementAndGet);
            assertEquals(0,applied.get());PreviewPagesTest.findText(first.getWindow().getDecorView(),"Back").performClick();assertEquals(0,applied.get());
            android.app.Dialog second=PreviewDialog.review(host.get(),"Match Preview","Fixture episode","Use This Episode",applied::incrementAndGet);
            android.view.View accept=PreviewPagesTest.findText(second.getWindow().getDecorView(),"Use This Episode");
            accept.performClick();assertEquals(1,applied.get());assertFalse(second.isShowing());accept.performClick();assertEquals(1,applied.get());
        }finally{host.pause().stop().destroy();}
    }
}
