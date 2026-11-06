package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import com.archos.mediacenter.video.utils.backup.BackupPresets;
import com.archos.mediacenter.video.utils.backup.BackupSelection;
import java.util.EnumSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class BackupPresetsTest
{
    private SharedPreferences preferences;

    @Before
    public void clean()
    {
        preferences = RuntimeEnvironment.getApplication().getSharedPreferences("presets", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    @Test
    public void freshUserGetsExactlyAllNonPrivateCategoriesAndPersistentChoice()
    {
        assertEquals(BackupSelection.Preset.EVERYTHING, BackupPresets.current(preferences));
        Set<BackupCategory> expected = EnumSet.allOf(BackupCategory.class);
        expected.removeAll(Set.of(BackupCategory.SOURCES, BackupCategory.BOOKMARKS, BackupCategory.LOCATIONS));
        assertEquals(expected, BackupPresets.selection(preferences));
        assertEquals("EVERYTHING", preferences.getString(BackupPresets.CHOICE, ""));
        for (BackupCategory category : BackupCategory.values())
        {
            assertEquals(!category.isPrivate(), preferences.getBoolean(category.preference(), category.isPrivate()));
        }
    }

    @Test
    public void settingsOnlyContainsExactlyTheTenSettingsCategories()
    {
        BackupPresets.select(preferences, BackupSelection.Preset.SETTINGS_ONLY);
        assertEquals(Set.of(BackupCategory.APPEARANCE, BackupCategory.AUDIO, BackupCategory.DIAGNOSTICS,
            BackupCategory.INTEGRATIONS, BackupCategory.LIBRARY, BackupCategory.NETWORK, BackupCategory.PLAYBACK,
            BackupCategory.STREAMING, BackupCategory.SUBTITLES, BackupCategory.VIDEO),
            BackupPresets.selection(preferences));
        assertEquals(BackupSelection.Preset.SETTINGS_ONLY, BackupPresets.current(preferences));
        assertFalse(preferences.getBoolean("supernova_backup_history", true));
    }

    @Test
    public void existingCustomAndPrivateChoicesSurviveMigrationWithoutChangingOtherPreferences()
    {
        preferences.edit().putBoolean(BackupCategory.SOURCES.preference(), true)
            .putBoolean(BackupCategory.AUDIO.preference(), false).putBoolean("unrelated", true).commit();
        Set<BackupCategory> expected = EnumSet.noneOf(BackupCategory.class);
        for (BackupCategory category : BackupCategory.values())
        {
            if (category.defaultIncluded)
                expected.add(category);
        }
        expected.add(BackupCategory.SOURCES);
        expected.remove(BackupCategory.AUDIO);
        assertEquals(BackupSelection.Preset.CUSTOM, BackupPresets.current(preferences));
        assertEquals(expected, BackupPresets.selection(preferences));
        BackupPresets.select(preferences, BackupSelection.Preset.CUSTOM);
        assertEquals(expected, BackupPresets.selection(preferences));
        assertTrue(preferences.getBoolean("unrelated", false));
    }

    @Test
    public void legacyHistoryAndResumeFallbacksSurviveAndExplicitNewKeysWin()
    {
        preferences.edit().putBoolean("supernova_backup_history", false)
            .putBoolean("supernova_backup_resume", false)
            .putBoolean(BackupCategory.HISTORY.preference(), true).commit();
        assertEquals(BackupSelection.Preset.CUSTOM, BackupPresets.current(preferences));
        assertTrue(BackupPresets.selection(preferences).contains(BackupCategory.HISTORY));
        assertFalse(BackupPresets.selection(preferences).contains(BackupCategory.POSITIONS));
        assertFalse(BackupPresets.selection(preferences).contains(BackupCategory.TITLE_PLAYBACK));
    }

    @Test
    public void existingEverythingStaysEverythingAndFutureCategoryEditsBecomeCustom()
    {
        for (BackupCategory category : BackupCategory.values())
        {
            preferences.edit().putBoolean(category.preference(), !category.isPrivate()).commit();
        }
        assertEquals(BackupSelection.Preset.EVERYTHING, BackupPresets.current(preferences));
        preferences.edit().putBoolean(BackupCategory.HISTORY.preference(), false).commit();
        assertEquals(BackupSelection.Preset.CUSTOM, BackupPresets.current(preferences));
        BackupPresets.setIncluded(preferences, BackupCategory.LOCATIONS, true);
        assertTrue(BackupPresets.selection(preferences).contains(BackupCategory.LOCATIONS));
        BackupPresets.select(preferences, BackupSelection.Preset.EVERYTHING);
        assertFalse(BackupPresets.selection(preferences).contains(BackupCategory.LOCATIONS));
        assertThrows(UnsupportedOperationException.class,
            () -> BackupPresets.selection(preferences).clear());
    }
}
