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
import static org.mockito.Mockito.*;

/** No token/account/provider request: verifies the real disconnected inline replacement. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi")
public class PutioConnectionFocusTest {
    private void drain(Object owner)throws Exception{java.util.concurrent.Future<?> task=ReflectionHelpers.getField(owner,"task");if(task!=null)try{task.get(5,TimeUnit.SECONDS);}catch(java.util.concurrent.CancellationException expected){}Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    @Test public void initialAndRepeatedDisconnectedReplacementNeverFallsToHome()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();PutioAccountController controller=null;try{LinearLayout root=new LinearLayout(host.get());root.setOrientation(LinearLayout.VERTICAL);Button home=new Button(host.get());home.setText("Home");root.addView(home);LinearLayout target=new LinearLayout(host.get());target.setOrientation(LinearLayout.VERTICAL);root.addView(target);host.get().setContentView(root);
            for(int attempt=0;attempt<3;attempt++){if(controller!=null)controller.close();controller=PutioAccountController.attach(host.get(),target);drain(controller);assertTrue(target.hasFocus());assertFalse(home.hasFocus());View connect=find(target,"Connect put.io");assertNotNull(connect);assertTrue(connect.hasFocus());
                ReflectionHelpers.callInstanceMethod(controller,"disconnected");drain(controller);connect=find(target,"Connect put.io");assertNotNull(connect);assertTrue(connect.hasFocus());assertFalse(home.hasFocus());}
        }finally{if(controller!=null)controller.close();host.pause().stop().destroy();}
    }
    @Test public void closingPendingAccountWorkPreventsLateFocusSteal()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();try{LinearLayout target=new LinearLayout(host.get());host.get().setContentView(target);PutioAccountController controller=PutioAccountController.attach(host.get(),target);controller.close();drain(controller);assertFalse(controller.activeInline());assertNull(find(target,"Connect put.io"));}finally{host.pause().stop().destroy();}
    }
    @Test public void simulatedExternalApprovalReturnsOnceThroughSecureStore()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();try(var stores=mockConstruction(PutioTokenStore.class);var clients=mockConstruction(PutioOAuthClient.class,(client,context)->{when(client.begin()).thenReturn(new PutioOAuthClient.Code("fixture","https://put.io/link?code=fixture"));when(client.poll(any())).thenReturn("fixture-authorisation-only");})){
            int[] returned={0};PutioAuthFlow flow=PutioAuthFlow.open(host.get(),()->returned[0]++);drain(flow);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofSeconds(5));drain(flow);assertEquals(1,returned[0]);verify(stores.constructed().get(0)).save("fixture-authorisation-only");Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofSeconds(20));drain(flow);assertEquals(1,returned[0]);flow.close();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void cancellingPendingLinkStopsPollingAndLateCompletionCannotReenter()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();try(var stores=mockConstruction(PutioTokenStore.class);var clients=mockConstruction(PutioOAuthClient.class,(client,context)->{when(client.begin()).thenReturn(new PutioOAuthClient.Code("fixture","https://put.io/link?code=fixture"));when(client.poll(any())).thenReturn("fixture-authorisation-only");})){
            int[] returned={0};PutioAuthFlow flow=PutioAuthFlow.open(host.get(),()->returned[0]++);drain(flow);org.robolectric.shadows.ShadowDialog.getLatestDialog().dismiss();flow.close();Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofSeconds(30));drain(flow);assertEquals(0,returned[0]);verify(clients.constructed().get(0),never()).poll(any());verify(stores.constructed().get(0),never()).save(any());
            PutioAuthFlow approved=PutioAuthFlow.open(host.get(),()->returned[0]++);drain(approved);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofSeconds(5));java.util.concurrent.Future<?> completing=ReflectionHelpers.getField(approved,"task");completing.get(5,TimeUnit.SECONDS);approved.close();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(0,returned[0]);assertFalse(org.robolectric.shadows.ShadowDialog.getLatestDialog().isShowing());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void linkingErrorDialogIsOwnedAndClosingCannotLeaveItAboveCloudServices()throws Exception{
        var host=Robolectric.buildActivity(Activity.class).setup().visible();try(var stores=mockConstruction(PutioTokenStore.class);var clients=mockConstruction(PutioOAuthClient.class,(client,context)->when(client.begin()).thenThrow(new java.io.IOException("fixture-unavailable")))){
            PutioAuthFlow flow=PutioAuthFlow.open(host.get(),()->fail("Failed link cannot return as authorised"));drain(flow);Dialog error=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(error.isShowing());assertNotNull(find(error.getWindow().getDecorView(),"Linking unavailable"));flow.close();assertFalse(error.isShowing());
        }finally{host.pause().stop().destroy();}
    }
    private View find(View view,String label){if(view instanceof TextView&&label.contentEquals(((TextView)view).getText()))return view;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){View found=find(((ViewGroup)view).getChildAt(i),label);if(found!=null)return found;}return null;}
}
