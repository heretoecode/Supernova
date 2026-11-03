package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewHomeMaximumTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
    @Test @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    @Config(qualifiers="w960dp-h540dp-land-mdpi") public void invalidExactValueKeepsSharedKeyboardOpen()throws Exception{
        InstrumentationRegistry.getInstrumentation().setInTouchMode(false);
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> controller=
                org.robolectric.Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            AtomicInteger accepted=new AtomicInteger(-1);
            PreviewTextInput.showValidated(controller.get(),"Maximum items","bad",10,
                    value->PreviewHomeRows.parseMaximum(value)==null?"Enter a whole number":null,
                    value->accepted.set(PreviewHomeRows.parseMaximum(value)));
            Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
            View root=dialog.getWindow().getDecorView();
            PreviewPagesTest.findText(root,"Done").performClick();
            assertTrue(dialog.isShowing());assertEquals(-1,accepted.get());
            EditText input=findInput(root);assertNotNull(input);assertNotNull(input.getError());input.setText("37");
            PreviewHomeVisualTest.measure(dialog);
            View select=PreviewPagesTest.findText(root,"Done");select.requestFocus();
            assertTrue(select.hasFocus());PreviewPagesTest.capture(PreviewHomeVisualTest.measure(dialog),"home-maximum-keyboard-focused");
            PreviewPagesTest.findText(root,"Done").performClick();
            assertEquals(37,accepted.get());assertFalse(dialog.isShowing());
        }finally{controller.pause().stop().destroy();}
    }
    private static EditText findInput(View view){
        if(view instanceof EditText)return (EditText)view;
        if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;
            for(int i=0;i<group.getChildCount();i++){EditText found=findInput(group.getChildAt(i));if(found!=null)return found;}}
        return null;
    }
    @Test public void exactCountAndNoLimitAreRepresentable(){
        assertEquals(Integer.valueOf(37),PreviewHomeRows.parseMaximum("37"));
        assertEquals(Integer.valueOf(0),PreviewHomeRows.parseMaximum("0"));
        assertEquals(Integer.valueOf(125),PreviewHomeRows.parseMaximum(" 125 "));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE),PreviewHomeRows.parseMaximum("2147483647"));
    }
    @Test public void invalidInputCannotChangeTheSavedLimit(){
        for(String value:new String[]{null,"","-1","1.5","2147483648","10000000000","12 titles"})assertNull(PreviewHomeRows.parseMaximum(value));
    }
}
