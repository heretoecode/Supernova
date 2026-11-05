package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PipPlaybackActionsTest
{
    @Test public void actionsToggleAndReturnWithoutConstructingANewMediaLaunch() throws Exception
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        int[] toggles = {0};
        try (PipPlaybackActions actions = new PipPlaybackActions(activity, () -> toggles[0]++))
        {
            assertEquals(2, actions.actions(true).size());
            actions.actions(true).get(0).getActionIntent().send();
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertEquals(1, toggles[0]);
            actions.actions(true).get(1).getActionIntent().send();
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            Intent returned = Shadows.shadowOf(activity).getNextStartedActivity();
            assertTrue(returned.getBooleanExtra(PipPlaybackActions.RETURN, false));
        }
    }
}
