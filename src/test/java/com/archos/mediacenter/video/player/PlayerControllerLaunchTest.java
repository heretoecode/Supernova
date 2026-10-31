package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Cold HUD initialization after the real backend has already reported a duration. */
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PlayerControllerLaunchTest {
    @Test public void preparedDurationCanRenderDuringFirstControllerInitializationAndRecreation() {
        var host=Robolectric.buildActivity(Activity.class).setup().visible();
        Player previous=Player.sPlayer;PlayerService previousService=PlayerService.sPlayerService;
        try {
            Activity activity=host.get();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("try_new_ui",true).putString("uimode","2").commit();
            Player player=mock(Player.class);Player.sPlayer=player;PlayerService.sPlayerService=null;
            when(player.getDuration()).thenReturn(20000);when(player.getCurrentPosition()).thenReturn(0);
            PlayerController controller=mock(PlayerController.class,CALLS_REAL_METHODS);
            ReflectionHelpers.setField(controller,"mContext",activity);
            ReflectionHelpers.setField(controller,"mAudioManager",activity.getSystemService(android.content.Context.AUDIO_SERVICE));
            ReflectionHelpers.setField(controller,"mSurfaceController",mock(SurfaceController.class));
            ReflectionHelpers.setField(controller,"mActionBar",mock(androidx.appcompat.app.ActionBar.class));
            ReflectionHelpers.setField(controller,"mNextSeek",-1);
            // Do not inject formatters: initialization must own them before its first clock update.
            View first=controllerView(activity);
            initialize(controller,first);
            assertEquals("00:00:20",((TextView)first.findViewById(R.id.time)).getText().toString());
            when(player.getDuration()).thenReturn(3723000);
            View recreated=controllerView(activity);
            initialize(controller,recreated);
            assertEquals("01:02:03",((TextView)recreated.findViewById(R.id.time)).getText().toString());
            assertFalse(((TextView)recreated.findViewById(R.id.clock)).getText().toString().contains("Ends"));
        } finally {Player.sPlayer=previous;PlayerService.sPlayerService=previousService;host.pause().stop().destroy();}
    }
    private View controllerView(Activity activity) {
        View view=LayoutInflater.from(activity).inflate(R.layout.player_controller_experimental,null);
        // This host has no device cutout; cutout handling belongs to PlayerActivity.
        view.findViewById(R.id.fullscreen_cutouts).setId(View.NO_ID);
        activity.setContentView(view);return view;
    }
    private void initialize(PlayerController controller,View view) {
        ReflectionHelpers.callInstanceMethod(controller,"initControllerView",ReflectionHelpers.ClassParameter.from(View.class,view),ReflectionHelpers.ClassParameter.from(boolean.class,true));
    }
}
