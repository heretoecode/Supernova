package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, qualifiers = "land")
public class FirstPlayHelpRemovalTest
{
    @Test public void bothControllerLayoutsLackNovaHelpEvenForFreshPreferences()
    {
        Context context = new ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.ArchosThemeBlueOverlay);
        assertTrue(PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit());
        TypedValue actionBarSize = new TypedValue();
        assertTrue(context.getTheme().resolveAttribute(R.attr.actionBarSize, actionBarSize, true));
        assertEquals(TypedValue.TYPE_DIMENSION, actionBarSize.type);
        for (int layout : new int[] {R.layout.player_controller_experimental, R.layout.player_controller_inside})
        {
            View root = LayoutInflater.from(context).inflate(layout, null);
            int help = context.getResources().getIdentifier("help_overlay", "id", context.getPackageName());
            assertNull(root.findViewById(help));
        }
        assertEquals(0, context.getResources().getIdentifier("help_overlay_menu", "string", context.getPackageName()));
    }
}
