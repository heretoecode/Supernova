package com.archos.mediacenter.video.foundation;

import android.app.Application;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class FoundationAboutActivityTest {
    @Test public void classicAboutHostUsesActualIdentityAndSurvivesRecreation() {
        org.robolectric.android.controller.ActivityController<FoundationAboutActivity> controller=Robolectric.buildActivity(FoundationAboutActivity.class).setup();
        if(!com.archos.mediacenter.video.BuildConfig.FOUNDATION){assertTrue(controller.get().isFinishing());return;}
        String before=text(controller.get().findViewById(android.R.id.content));
        assertTrue(before.contains("Based on Nova Video Player 6.4.64"));assertFalse(before.contains("SUPERNOVA Preview"));
        controller.recreate();String after=text(controller.get().findViewById(android.R.id.content));assertTrue(after.contains("Based on Nova Video Player 6.4.64"));
        controller.pause().stop().destroy();
    }
    private String text(View view){StringBuilder out=new StringBuilder();if(view instanceof TextView)out.append(((TextView)view).getText());if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++)out.append(text(group.getChildAt(i)));}return out.toString();}
}
