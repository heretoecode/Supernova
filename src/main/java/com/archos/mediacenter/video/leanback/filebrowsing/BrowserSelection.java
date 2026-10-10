package com.archos.mediacenter.video.leanback.filebrowsing;

import android.content.SharedPreferences;
import android.net.Uri;
import java.util.*;

/** Transactional browser selections. Opening folders or moving focus never mutates the library. */
public final class BrowserSelection {
    public static final String ROOTS = "supernova_library_roots";
    public static final String EXCLUSIONS = "supernova_library_exclusions";
    private Set<String> originalRoots, originalExclusions;
    public final Set<String> roots, exclusions;

    public BrowserSelection(Set<String> roots, Set<String> exclusions) {
        this.roots = new LinkedHashSet<>(roots);
        this.exclusions = new LinkedHashSet<>(exclusions);
        checkpoint();
    }

    public static String canonical(Uri uri) {
        if(uri==null)throw new IllegalArgumentException("Location required");
        return com.archos.mediaprovider.video.SupernovaLibraryPolicy.canonical(uri);
    }

    public static boolean contains(String parent, String child) {
        return child.equals(parent) || child.startsWith(parent.endsWith("/") ? parent : parent + "/");
    }

    public void include(Uri uri) {
        String key = canonical(uri);
        exclusions.remove(key);
        if (roots.stream().noneMatch(root -> contains(root, key))) {
            roots.removeIf(root -> contains(key, root));
            roots.add(key);
        }
    }

    public void exclude(Uri uri) { exclusions.add(canonical(uri)); }
    public void remove(Uri uri) { roots.remove(canonical(uri)); }
    public void restore(Uri uri) { exclusions.remove(canonical(uri)); }
    public boolean included(Uri uri) {
        String key = canonical(uri);
        return roots.stream().anyMatch(root -> contains(root, key))
                && exclusions.stream().noneMatch(excluded -> contains(excluded, key));
    }
    public boolean changed() { return !roots.equals(originalRoots) || !exclusions.equals(originalExclusions); }
    private static List<String> difference(Set<String> first, Set<String> second) {
        List<String> result = new ArrayList<>(first); result.removeAll(second); return result;
    }
    public List<String> added() { return difference(roots, originalRoots); }
    public List<String> excluded() { return difference(exclusions, originalExclusions); }
    public List<String> removed() { return difference(originalRoots, roots); }
    public List<String> restored() { return difference(originalExclusions, exclusions); }
    public void discard() {
        roots.clear(); roots.addAll(originalRoots);
        exclusions.clear(); exclusions.addAll(originalExclusions);
    }
    public void seedExisting(Collection<Uri> locations) {
        for(Uri uri:locations){String key=canonical(uri);if(originalRoots.add(key))roots.add(key);}
    }
    public boolean persist(SharedPreferences preferences) {
        boolean written = preferences.edit().putStringSet(ROOTS, new LinkedHashSet<>(roots))
                .putStringSet(EXCLUSIONS, new LinkedHashSet<>(exclusions)).putBoolean("supernova_library_policy_initialized",true).commit();
        if (written) checkpoint();
        return written;
    }
    public void applied(){checkpoint();}
    private void checkpoint() {
        originalRoots = new LinkedHashSet<>(roots); originalExclusions = new LinkedHashSet<>(exclusions);
    }
}
