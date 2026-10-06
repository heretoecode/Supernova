package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.net.Uri;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.browser.ShortcutDb;
import com.archos.mediaprovider.NetworkScanner;

/** Folder membership and bookmarks remain distinct; adding one never deletes the other. */
public final class PreviewFolderActions {
    public static void chooseLibrary(Context c, Uri folder, String name) {
        PreviewDialog.choose(c,"Add to Library",new String[]{"Add to Movies","Add to TV Shows"},-1,n->addLibrary(c,folder,name,n==0?"movie":"tv"));
    }
    public static void addLibrary(Context c, Uri folder, String name, String kind) {
        if (ShortcutDbAdapter.VIDEO.isShortcut(c,folder.toString()) <= 0
                && !ShortcutDbAdapter.VIDEO.addShortcut(c,new ShortcutDbAdapter.Shortcut(name,folder.toString()))) return;
        // This is a classification hint, never a fabricated title match.
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putString("preview_source_kind:"+folder,kind).apply();
        NetworkScanner.scanVideos(c,folder);
    }
    public static void save(Context c, Uri folder, String name) {
        if(ShortcutDb.STATIC.isShortcut(c,folder.toString())<0)ShortcutDb.STATIC.insertShortcut(c,folder,name,folder.buildUpon().encodedAuthority(folder.getHost()).clearQuery().fragment(null).build().toString());
    }
    private PreviewFolderActions() {}
}
