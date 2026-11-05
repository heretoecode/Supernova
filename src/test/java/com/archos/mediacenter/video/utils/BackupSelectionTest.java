package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.archos.mediacenter.video.utils.backup.BackupCategory;
import com.archos.mediacenter.video.utils.backup.BackupSelection;
import java.util.Set;
import org.junit.Test;

public class BackupSelectionTest
{
    @Test
    public void everythingExcludesAllPrivateDataButCustomRequiresItsExplicitChoice()
    {
        Set<BackupCategory> all = BackupSelection.categories(BackupSelection.Preset.EVERYTHING, Set.of());
        assertTrue(all.contains(BackupCategory.TITLE_PLAYBACK));
        for (BackupCategory category : BackupCategory.values())
        {
            if (category.isPrivate())
            {
                assertFalse(category.defaultIncluded);
                assertFalse(all.contains(category));
                assertFalse(category.privacyReveal().isEmpty());
            }
        }
        assertEquals(Set.of(BackupCategory.SOURCES),
            BackupSelection.categories(BackupSelection.Preset.CUSTOM, Set.of(BackupCategory.SOURCES)));
        for (BackupCategory category : BackupSelection.categories(BackupSelection.Preset.SETTINGS_ONLY, Set.of()))
        {
            assertTrue(category.settings() != null);
        }
    }
}
