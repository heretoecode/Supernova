package com.archos.mediacenter.video.streaming.putio;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/** No token/account/provider request: verifies the real disconnected inline replacement. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi")
public class PutioConnectionFocusTest {
    private void drain()throws Exception{StreamingRepository.IO.submit(()->{}).get(5,TimeUnit.SECONDS);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    @Test public void initialAndRepeatedDisconnectedReplacementNeverFallsToHome()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();PutioAccountController controller=null;try{LinearLayout root=new LinearLayout(host.get());root.setOrientation(LinearLayout.VERTICAL);Button home=new Button(host.get());home.setText("Home");root.addView(home);LinearLayout target=new LinearLayout(host.get());target.setOrientation(LinearLayout.VERTICAL);root.addView(target);host.get().setContentView(root);
            for(int attempt=0;attempt<3;attempt++){if(controller!=null)controller.close();controller=PutioAccountController.attach(host.get(),target);drain();assertTrue(target.hasFocus());assertFalse(home.hasFocus());View connect=find(target,"Connect put.io");assertNotNull(connect);assertTrue(connect.hasFocus());
                ReflectionHelpers.callInstanceMethod(controller,"disconnected");drain();connect=find(target,"Connect put.io");assertNotNull(connect);assertTrue(connect.hasFocus());assertFalse(home.hasFocus());}
        }finally{if(controller!=null)controller.close();host.pause().stop().destroy();}
    }
    @Test public void closingPendingAccountWorkPreventsLateFocusSteal()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();try{LinearLayout target=new LinearLayout(host.get());host.get().setContentView(target);PutioAccountController controller=PutioAccountController.attach(host.get(),target);controller.close();drain();assertFalse(controller.activeInline());assertNull(find(target,"Connect put.io"));}finally{host.pause().stop().destroy();}
    }
    private View find(View view,String label){if(view instanceof TextView&&label.contentEquals(((TextView)view).getText()))return view;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){View found=find(((ViewGroup)view).getChildAt(i),label);if(found!=null)return found;}return null;}
}
