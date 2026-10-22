package com.archos.mediacenter.video.utils;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.concurrent.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30)
public class SubtitleReplacementTest {
    private TextView text(View view,String label){if(view instanceof TextView&&label.contentEquals(((TextView)view).getText()))return (TextView)view;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){TextView found=text(((ViewGroup)view).getChildAt(i),label);if(found!=null)return found;}return null;}
    @Test public void explicitReplaceAndCancelAreDistinctAndDismissalCannotRaceApproval()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();ExecutorService io=Executors.newSingleThreadExecutor();try{for(String decision:new String[]{"Back","Replace"}){Future<Boolean> result=io.submit(()->SubtitleReplacement.confirm(host.get(),new Handler(Looper.getMainLooper()),()->false));long end=System.nanoTime()+TimeUnit.SECONDS.toNanos(5);Dialog dialog=null;while(dialog==null||!dialog.isShowing()){Shadows.shadowOf(Looper.getMainLooper()).idle();dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();if(System.nanoTime()>end)throw new AssertionError("Replacement review did not appear");Thread.sleep(2);}assertFalse(result.isDone());TextView button=text(dialog.getWindow().getDecorView(),decision);assertNotNull(button);button.performClick();Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(decision.equals("Replace"),result.get(5,TimeUnit.SECONDS));}}
        finally{io.shutdownNow();host.pause().stop().destroy();}
    }
    @Test public void cancelledWorkNeverAuthorisesAWrite()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();ExecutorService io=Executors.newSingleThreadExecutor();try{Future<Boolean> result=io.submit(()->SubtitleReplacement.confirm(host.get(),new Handler(Looper.getMainLooper()),()->true));for(int i=0;i<50&&!result.isDone();i++){Shadows.shadowOf(Looper.getMainLooper()).idle();Thread.sleep(2);}assertFalse(result.get(5,TimeUnit.SECONDS));}finally{io.shutdownNow();host.pause().stop().destroy();}
    }
}
