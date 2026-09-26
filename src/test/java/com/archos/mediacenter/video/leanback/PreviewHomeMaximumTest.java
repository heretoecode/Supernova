package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewHomeMaximumTest {
    @Test public void invalidExactValueKeepsSharedKeyboardOpen(){
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> controller=
                org.robolectric.Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            java.util.concurrent.atomic.AtomicInteger accepted=new java.util.concurrent.atomic.AtomicInteger(-1);
            PreviewTextInput.showValidated(controller.get(),"Maximum items","bad",10,
                    value->PreviewHomeRows.parseMaximum(value)==null?"Enter a whole number":null,
                    value->accepted.set(PreviewHomeRows.parseMaximum(value)));
            android.app.Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
            android.view.View root=dialog.getWindow().getDecorView();
            PreviewPagesTest.findText(root,"Select").performClick();
            assertTrue(dialog.isShowing());assertEquals(-1,accepted.get());
            android.widget.EditText input=findInput(root);assertNotNull(input);assertNotNull(input.getError());input.setText("37");
            PreviewPagesTest.findText(root,"Select").performClick();
            assertEquals(37,accepted.get());assertFalse(dialog.isShowing());
        }finally{controller.pause().stop().destroy();}
    }
    private static android.widget.EditText findInput(android.view.View view){
        if(view instanceof android.widget.EditText)return (android.widget.EditText)view;
        if(view instanceof android.view.ViewGroup){android.view.ViewGroup group=(android.view.ViewGroup)view;
            for(int i=0;i<group.getChildCount();i++){android.widget.EditText found=findInput(group.getChildAt(i));if(found!=null)return found;}}
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
