package com.archos.mediacenter.video.utils.backup;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/** Presets are selection data only; the screens still own review and all wording. */
public final class BackupSelection
{
    public enum Preset
    {
        EVERYTHING, SETTINGS_ONLY, CUSTOM
    }

    public static Set<BackupCategory> categories(Preset preset, Set<BackupCategory> custom)
    {
        EnumSet<BackupCategory> selected = EnumSet.noneOf(BackupCategory.class);
        if (preset == Preset.CUSTOM)
        {
            selected.addAll(custom);
        }
        else
        {
            for (BackupCategory category : BackupCategory.values())
            {
                if (preset == Preset.EVERYTHING ? !category.isPrivate() : category.settings() != null)
                {
                    selected.add(category);
                }
            }
        }
        return Collections.unmodifiableSet(selected);
    }

    private BackupSelection()
    {
    }
}
