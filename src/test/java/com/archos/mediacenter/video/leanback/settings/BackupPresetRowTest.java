package com.archos.mediacenter.video.leanback.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.PreviewAsyncFixtures;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import com.archos.mediacenter.video.utils.backup.BackupPresets;
import com.archos.mediacenter.video.utils.backup.BackupSelection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

/** Settings › System › Backup: What to include is Everything · Settings only · Custom; parts show only for Custom. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class BackupPresetRowTest
{
    @Before
    public void setup()
    {
        new ProductionBackupSettingsTest().setup();
    }

    @After
    public void drain() throws Exception
    {
        PreviewAsyncFixtures.drain();
    }

    @Test
    public void threeChoicesAndPartsOnlyForCustom()
    {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            var fragment = new VideoSettingsFragment.PrefsFragment();
            host.get().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commitNow();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            View root = fragment.requireView();
            root.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
            root.layout(0, 0, 960, 540);
            assertTrue(PreviewSettings.enterBackup(fragment));
            View row = root.findViewWithTag("backup.preset");
            assertNotNull(row);
            var prefs = PreferenceManager.getDefaultSharedPreferences(host.get());
            assertEquals(BackupSelection.Preset.EVERYTHING, BackupPresets.current(prefs));
            assertEquals(View.GONE, root.findViewWithTag("backup.category.APPEARANCE").getVisibility());
            assertTrue(row.requestFocus());
            row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
            assertEquals(BackupSelection.Preset.SETTINGS_ONLY, BackupPresets.current(prefs));
            assertEquals(View.GONE, root.findViewWithTag("backup.category.APPEARANCE").getVisibility());
            row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
            assertEquals(BackupSelection.Preset.CUSTOM, BackupPresets.current(prefs));
            assertEquals(View.VISIBLE, root.findViewWithTag("backup.category.APPEARANCE").getVisibility());
            row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
            assertEquals(BackupSelection.Preset.SETTINGS_ONLY, BackupPresets.current(prefs));
        } finally {
            host.pause().stop().destroy();
        }
    }
}
