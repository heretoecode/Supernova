package com.archos.mediacenter.video.foundation;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class DoubleRingTest {
    @Test public void constantOppositeVelocityAndSeamlessLoop(){
        assertEquals(90f,DoubleRingMotion.outer(800)-DoubleRingMotion.outer(0),.001f);
        assertEquals(-90f,DoubleRingMotion.inner(800)-DoubleRingMotion.inner(0),.001f);
        assertEquals(DoubleRingMotion.outer(0),DoubleRingMotion.outer(3200),.001f);
        assertEquals(DoubleRingMotion.inner(0),DoubleRingMotion.inner(3200),.001f);
        assertEquals(DoubleRingMotion.outer(800)-DoubleRingMotion.outer(400),DoubleRingMotion.outer(1200)-DoubleRingMotion.outer(800),.001f);
    }
    @Test public void hidingAndRemovingCallbackStopsScheduling(){
        DoubleRingDrawable ring=new DoubleRingDrawable();final int[] scheduled={0},cancelled={0};
        ring.setCallback(new Drawable.Callback(){
            public void invalidateDrawable(Drawable d){}
            public void scheduleDrawable(Drawable d,Runnable r,long when){scheduled[0]++;}
            public void unscheduleDrawable(Drawable d,Runnable r){cancelled[0]++;}
        });
        ring.start();assertTrue(ring.isRunning());assertEquals(1,scheduled[0]);
        ring.setVisible(false,false);assertFalse(ring.isRunning());assertTrue(cancelled[0]>0);
        ring.run();assertEquals(1,scheduled[0]);ring.start();assertFalse(ring.isRunning());
        ring.setVisible(true,false);ring.start();assertTrue(ring.isRunning());
        ring.setCallback(null);ring.run();assertFalse(ring.isRunning());
    }
}
