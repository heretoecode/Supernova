package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import androidx.preference.PreferenceManager;

import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.leanback.PreviewLibraryScan;
import com.archos.mediacenter.video.leanback.filebrowsing.BrowserSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Changes to what the library scans, made from Files (add a folder or connection, exclude, remove): the same
 * transaction the old browser used (library roots and exclusions, then NOVA's indexed folders), then a scan.
 */
public final class LibraryChanges
{
    private LibraryChanges()
    {
    }

    /** Applies one change; false when it could not be saved (nothing changes then). */
    public static boolean apply(Context context, Consumer<BrowserSelection> change)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        BrowserSelection selection = new BrowserSelection(
            preferences.getStringSet(BrowserSelection.ROOTS, Collections.emptySet()),
            preferences.getStringSet(BrowserSelection.EXCLUSIONS, Collections.emptySet()));
        try
        {
            selection.seedExisting(existing(context));
            change.accept(selection);
        }
        catch (RuntimeException unusable)
        {
            return false;
        }
        if (!selection.changed())
        {
            return true;
        }
        boolean written;
        try
        {
            written = ShortcutDbAdapter.VIDEO.applySupernovaRoots(
                context, selection.added(), selection.removed(),
                () -> preferences.edit()
                    .putStringSet(BrowserSelection.ROOTS, new LinkedHashSet<>(selection.roots))
                    .putStringSet(BrowserSelection.EXCLUSIONS, new LinkedHashSet<>(selection.exclusions))
                    .putBoolean("supernova_library_policy_initialized", true)
                    .commit());
        }
        catch (RuntimeException failed)
        {
            written = false;
        }
        if (written)
        {
            selection.applied();
            PreviewLibraryScan.requestAfterChanges(context);
        }
        return written;
    }

    /** True when the library leaves this place out (it or a folder above it was excluded). */
    public static boolean excluded(Context context, Uri uri)
    {
        try
        {
            String key = BrowserSelection.canonical(uri);
            Set<String> exclusions = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(BrowserSelection.EXCLUSIONS, Collections.emptySet());
            for (String excluded : exclusions)
            {
                if (BrowserSelection.contains(excluded, key))
                {
                    return true;
                }
            }
        }
        catch (RuntimeException unusable)
        {
            return false;
        }
        return false;
    }

    private static List<Uri> existing(Context context)
    {
        List<Uri> locations = new ArrayList<>();
        try (Cursor cursor = ShortcutDbAdapter.VIDEO.queryAllShortcuts(context))
        {
            if (cursor == null)
            {
                return locations;
            }
            for (ShortcutDbAdapter.Shortcut shortcut : ShortcutDbAdapter.VIDEO.cursorToShortcutList(cursor))
            {
                if (shortcut.getUri() != null)
                {
                    locations.add(Uri.parse(shortcut.getUri()));
                }
            }
        }
        catch (RuntimeException unavailable)
        {
            // No indexed-folder table yet: only the roots already in the preferences count.
        }
        return locations;
    }
}
