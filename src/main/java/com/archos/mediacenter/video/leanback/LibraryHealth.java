package com.archos.mediacenter.video.leanback;
import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediaprovider.ImportState;
import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Availability is separate from IDs, metadata, bookmarks, watched state and row membership. */
public final class LibraryHealth
{
    public enum State
    {
        AVAILABLE,
        SOURCE_OFFLINE,
        MISSING
    }

    public static State state(Context c, Video video)
    {
        SharedPreferences health = c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, Context.MODE_PRIVATE);
        String path = video.getFileUri() == null ? "" : SupernovaLibraryPolicy.canonical(video.getFileUri());
        for (String key : health.getAll().keySet())
            if (key.startsWith("offline:") && SupernovaLibraryPolicy.contains(key.substring(8), path))
                return State.SOURCE_OFFLINE;
        return health.contains("media:" + video.getId()) ? State.MISSING : State.AVAILABLE;
    }

    public static String message(State state)
    {
        return state == State.SOURCE_OFFLINE ? "Source offline"
        : state == State.MISSING             ? "Missing media"
                                             : "Available";
    }

    public static String report(Context c)
    {
        SharedPreferences health = c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, Context.MODE_PRIVATE);
        List<String> offline = new ArrayList<>(), missing = new ArrayList<>();
        for (Map.Entry<String, ?> entry : health.getAll().entrySet())
            if (entry.getKey().startsWith("offline:"))
                offline.add(entry.getKey().substring(8));
            else if (entry.getKey().startsWith("media:"))
                missing.add(String.valueOf(entry.getValue()));
        Collections.sort(offline);
        Collections.sort(missing);
        StringBuilder text =
            new StringBuilder(
                "Library records, metadata, watched state and playback progress are retained when storage is " +
                "unavailable. Reconnect the source and scan to reconcile the same IDs.\n\nSources offline: ")
                .append(offline.size());
        for (String root : offline)
            text.append("\n").append(root);
        text.append("\n\nConfirmed missing: ").append(missing.size());
        for (String path : missing)
            text.append("\n").append(path);
        return text.toString();
    }

    public static boolean needsAttention(Context c, PreviewLibraryLoader.Snapshot snapshot)
    {
        SharedPreferences health = c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, 0);
        if (!health.getAll().isEmpty())
            return true;
        if (ImportState.VIDEO.isInitialImport() || ImportState.VIDEO.isRegularImport() ||
            LoaderUtils.getScrapeInProgress())
            return false;
        if (!snapshot.unmatched.isEmpty())
            return true;
        for (PreviewLibraryLoader.Entry entry : snapshot.technical)
            if (entry.media instanceof Video)
            {
                Video video = (Video)entry.media;
                if ((video instanceof Movie || video instanceof Episode) &&
                    (video.getPosterUri() == null || LibraryHealthActivity.plot(video).trim().isEmpty()))
                    return true;
            }
        return false;
    }

    private static final java.util.concurrent.ExecutorService COUNTS =
        java.util.concurrent.Executors.newSingleThreadExecutor();
    private static final java.util.concurrent.atomic.AtomicBoolean counting =
        new java.util.concurrent.atomic.AtomicBoolean();
    private static final java.util.concurrent.atomic.AtomicLong healthVersion =
        new java.util.concurrent.atomic.AtomicLong();
    private static final SharedPreferences.OnSharedPreferenceChangeListener COUNT_CHANGES =
        (prefs, key) -> healthVersion.incrementAndGet();
    private static SharedPreferences countedHealth;
    private static volatile PreviewLibraryLoader.Snapshot countedSnapshot;
    private static volatile long countedVersion = -1;
    private static volatile LibraryAttentionCount attention =
        LibraryAttentionCount.count(Collections.emptySet(), Collections.emptySet(), Collections.emptyMap());

    /** Constant-time on the landing tile; any full inventory work runs on the count worker. */
    public static int needsAttentionCount(Context context)
    {
        return attention(context).count;
    }

    public static String needsAttentionReason(Context context)
    {
        return attention(context).reason;
    }

    public static LibraryAttentionCount attention(Context context)
    {
        SharedPreferences health = context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, Context.MODE_PRIVATE);
        synchronized (LibraryHealth.class)
        {
            if (countedHealth != health)
            {
                if (countedHealth != null)
                    countedHealth.unregisterOnSharedPreferenceChangeListener(COUNT_CHANGES);
                countedHealth = health;
                health.registerOnSharedPreferenceChangeListener(COUNT_CHANGES);
                healthVersion.incrementAndGet();
            }
        }
        PreviewLibraryLoader.Snapshot snapshot = PreviewLibraryLoader.memoryCache();
        long version = healthVersion.get();
        if (snapshot != null && (snapshot != countedSnapshot || version != countedVersion) &&
            counting.compareAndSet(false, true))
        {
            COUNTS.execute(() -> {
                try
                {
                    Set<Long> indexed = new HashSet<>();
                    Set<Long> incomplete = new HashSet<>();
                    boolean scanning = ImportState.VIDEO.isInitialImport() || ImportState.VIDEO.isRegularImport() ||
                                       LoaderUtils.getScrapeInProgress();
                    for (PreviewLibraryLoader.Entry entry : snapshot.technical)
                    {
                        if (!(entry.media instanceof Video))
                            continue;
                        Video video = (Video)entry.media;
                        indexed.add(video.getId());
                        if (!scanning &&
                            (!(video instanceof Movie || video instanceof Episode) || video.getPosterUri() == null ||
                             LibraryHealthActivity.plot(video).trim().isEmpty()))
                        {
                            incomplete.add(video.getId());
                        }
                    }
                    attention = LibraryAttentionCount.count(indexed, incomplete, health.getAll());
                    countedSnapshot = snapshot;
                    countedVersion = version;
                }
                finally
                {
                    counting.set(false);
                }
            });
        }
        return attention;
    }

    private LibraryHealth()
    {
    }
}
