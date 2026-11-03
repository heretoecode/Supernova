package com.archos.mediacenter.video;

import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.foundation.FoundationUiPolicy;
import com.archos.mediacenter.video.leanback.MainActivityLeanback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class EntryActivityTest {
    @Test
    public void tvLaunchPreservesViewAndSearchContractsDespiteRestoredPhonePreferences() {
        Context context = RuntimeEnvironment.getApplication();
        var preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean("always_leanback_on_tv_key", false)
                .putString("uimode_leanback", "tablet").putBoolean("try_new_ui", false).commit();
        FoundationUiPolicy.apply(context);
        assertTrue(preferences.getBoolean("try_new_ui", false));
        assertTrue(UiModePolicy.applicationIsInLeanbackMode(context));
        Uri uri = Uri.parse("content://fixture/video/1");
        for (String action : new String[] {Intent.ACTION_VIEW, Intent.ACTION_SEARCH, Intent.ACTION_MAIN}) {
            Intent origin = new Intent(action).setDataAndType(uri, "video/mp4")
                    .putExtra("position", 12000).putExtra("query", "fixture")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            origin.setClipData(ClipData.newRawUri("video", uri));
            Intent routed = EntryActivity.tvIntent(context, origin);
            assertEquals(MainActivityLeanback.class.getName(), routed.getComponent().getClassName());
            assertEquals(action, routed.getAction());
            assertEquals(uri, routed.getData());
            assertEquals("video/mp4", routed.getType());
            assertEquals(12000, routed.getIntExtra("position", -1));
            assertEquals("fixture", routed.getStringExtra("query"));
            assertEquals(uri, routed.getClipData().getItemAt(0).getUri());
            int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP;
            assertEquals(flags, routed.getFlags() & flags);
            assertNull("Routing must not mutate the incoming intent", origin.getComponent());
        }
        UiModePolicy.updateUiModePreferences(context, false);
        assertEquals("tv", preferences.getString("uimode_leanback", ""));
        assertEquals("2", preferences.getString("uimode", ""));
    }
}
