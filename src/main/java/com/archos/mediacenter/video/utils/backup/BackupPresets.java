package com.archos.mediacenter.video.utils.backup;

import android.content.SharedPreferences;
import java.util.EnumSet;
import java.util.Set;

/** Persisted export intent in the default preference store. Restore selections remain independent. */
public final class BackupPresets
{
    public static final String CHOICE = "supernova_backup_preset_v1";

    public static synchronized BackupSelection.Preset current(SharedPreferences preferences)
    {
        if (!preferences.contains(CHOICE))
        {
            Set<BackupCategory> legacy = stored(preferences);
            boolean existing = preferences.contains("supernova_backup_history")
                || preferences.contains("supernova_backup_resume");
            for (BackupCategory category : BackupCategory.values())
            {
                existing |= preferences.contains(category.preference());
            }
            BackupSelection.Preset migrated = existing && !legacy.equals(categories(BackupSelection.Preset.EVERYTHING))
                ? BackupSelection.Preset.CUSTOM : BackupSelection.Preset.EVERYTHING;
            write(preferences, migrated, existing ? legacy : categories(migrated));
            return migrated;
        }
        BackupSelection.Preset choice;
        try
        {
            choice = BackupSelection.Preset.valueOf(preferences.getString(CHOICE, "EVERYTHING"));
        }
        catch (IllegalArgumentException invalid)
        {
            choice = BackupSelection.Preset.CUSTOM;
        }
        // Existing category editors may still write the legacy keys directly. Keep their deliberate edits.
        if (choice != BackupSelection.Preset.CUSTOM && !stored(preferences).equals(categories(choice)))
        {
            choice = BackupSelection.Preset.CUSTOM;
            write(preferences, choice, stored(preferences));
        }
        return choice;
    }

    public static synchronized Set<BackupCategory> selection(SharedPreferences preferences)
    {
        BackupSelection.Preset choice = current(preferences);
        return BackupSelection.categories(choice, stored(preferences));
    }

    /** Switching to Custom retains the current category choices, including explicitly chosen private categories. */
    public static synchronized void select(SharedPreferences preferences, BackupSelection.Preset choice)
    {
        java.util.Objects.requireNonNull(choice);
        current(preferences);
        write(preferences, choice, BackupSelection.categories(choice, stored(preferences)));
    }

    /** The caller owns any private-data consent. One edit selects Custom and persists its full selection atomically. */
    public static synchronized void setIncluded(SharedPreferences preferences, BackupCategory category, boolean included)
    {
        current(preferences);
        Set<BackupCategory> custom = EnumSet.noneOf(BackupCategory.class);
        custom.addAll(stored(preferences));
        if (included)
            custom.add(category);
        else
            custom.remove(category);
        write(preferences, BackupSelection.Preset.CUSTOM, custom);
    }

    private static Set<BackupCategory> categories(BackupSelection.Preset choice)
    {
        return BackupSelection.categories(choice, Set.of());
    }

    private static Set<BackupCategory> stored(SharedPreferences preferences)
    {
        Set<BackupCategory> selected = EnumSet.noneOf(BackupCategory.class);
        for (BackupCategory category : BackupCategory.values())
        {
            boolean fallback = category.defaultIncluded;
            if (category == BackupCategory.HISTORY)
                fallback = preferences.getBoolean("supernova_backup_history", fallback);
            if (category == BackupCategory.POSITIONS)
                fallback = preferences.getBoolean("supernova_backup_resume", fallback);
            if (preferences.getBoolean(category.preference(), fallback))
                selected.add(category);
        }
        return selected;
    }

    private static void write(SharedPreferences preferences, BackupSelection.Preset choice,
                              Set<BackupCategory> selected)
    {
        SharedPreferences.Editor edit = preferences.edit().putString(CHOICE, choice.name());
        for (BackupCategory category : BackupCategory.values())
        {
            edit.putBoolean(category.preference(), selected.contains(category));
        }
        edit.putBoolean("supernova_backup_history", selected.contains(BackupCategory.HISTORY));
        edit.putBoolean("supernova_backup_resume", selected.contains(BackupCategory.POSITIONS));
        if (!edit.commit())
            throw new IllegalStateException("Backup selection was not saved");
    }

    private BackupPresets()
    {
    }
}
