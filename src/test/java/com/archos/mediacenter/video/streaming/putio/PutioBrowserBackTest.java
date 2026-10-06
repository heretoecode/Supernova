package com.archos.mediacenter.video.streaming.putio;

import android.app.Application;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PutioBrowserBackTest {
    @Test public void dispatcherReturnsToRootBeforeClosingActivity(){
        Intent launch=new Intent(RuntimeEnvironment.getApplication(),PutioBrowserActivity.class).putExtra("folder",17L).putExtra("folder_name","Fixture folder");
        var controller=Robolectric.buildActivity(PutioBrowserActivity.class,launch).setup();
        try{
            PutioBrowserActivity activity=controller.get();
            activity.getOnBackPressedDispatcher().onBackPressed();
            assertEquals(0L,(long)ReflectionHelpers.getField(activity,"folder"));assertFalse(activity.isFinishing());
            activity.getOnBackPressedDispatcher().onBackPressed();assertTrue(activity.isFinishing());
        }finally{controller.pause().stop().destroy();}
    }
}
