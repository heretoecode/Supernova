package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.details.*;
import com.archos.mediaprovider.ImportState;
import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.*;
import com.archos.mediacenter.video.health.MissingFilesRepository;
import com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity;

/** Library Health uses its host browser's centre and Information panels; no second shell. */
public final class LibraryHealthPanel {
    /** Plain names (approved 0.156, network-files-v2/06). Index order is unchanged: 0..4 as before. */
    public static final String[] NAMES = {
        "Not matched", "Possibly wrong match", "Missing artwork or details", "Missing files", "Sources offline"
    };
    /** What each check looks for, shown under its name. */
    public static final String[] LINES = {
        "Videos Supernova could not identify", "Matches you reported as wrong",
        "Matched, but without a poster or description", "Videos in your library whose file has gone",
        "Drives or shares that could not be reached"
    };
    /** The overview order in the approved mock-up: missing files first. */
    public static final int[] ORDER = {3, 0, 1, 2, 4};
    private final Context context;
    private final LinearLayout centre, right;
    private final Snapshot snapshot;

    public LibraryHealthPanel(Context c, LinearLayout middle, LinearLayout information, Snapshot s) {
        context = c;
        centre = middle;
        right = information;
        snapshot = s == null ? new Snapshot() : s;
    }

    private TextView action(String label, Runnable run) {
        return SharedThreePanel.accentAction(context, label, run);
    }

    private void information(String title, String text) {
        right.removeAllViews();
        SharedThreePanel.heading(right, title);
        SharedThreePanel.divider(right);
        right.addView(SharedThreePanel.text(context, text, 15));
    }

    public void show(int category) {
        centre.removeAllViews();
        SharedThreePanel.heading(centre, category < 0 ? "Library Health" : NAMES[category]);
        if (category < 0) {
            centre.addView(SharedThreePanel.text(context,
                LibraryHealth.needsAttention(context, snapshot) ? "Your library has issues to review."
                                                                : "Your library is healthy",
                18));
            centre.addView(SharedThreePanel.text(context,
                "Choose a category to review identification, metadata, availability and source issues. Importing and identification in progress are excluded from warnings.",
                15));
            information("Library Health", LibraryHealth.report(context));
            return;
        }
        SharedPreferences health =
            context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, 0);
        if (category == 3 && missingFiles() != null) {
            showMissing();
            return;
        }
        if (category == 4) {
            int count = 0;
            for (String key : health.getAll().keySet())
                if (key.startsWith("offline:") || key.startsWith("problem:")) {
                    String source = key.substring(8);
                    TextView row = action(source, () -> source(source));
                    row.setOnFocusChangeListener((v, yes) -> {
                        if (yes)
                            source(source);
                    });
                    centre.addView(row);
                    SharedThreePanel.divider(centre);
                    count++;
                }
            if (count == 0)
                empty();
            return;
        }
        boolean importing = ImportState.VIDEO.isInitialImport()
            || ImportState.VIDEO.isRegularImport()
            || LoaderUtils.getScrapeInProgress();
        int count = 0;
        for (Entry entry : snapshot.technical) {
            if (!(entry.media instanceof Video))
                continue;
            Video video = (Video) entry.media;
            boolean matched = video instanceof Movie || video instanceof Episode;
            String plot = video.getDescriptionBody();
            boolean include = category == 0 ? !importing && !matched
                : category == 1 ? health.getBoolean("incorrect:" + video.getId(), false)
                : category == 2
                ? !importing && matched && (video.getPosterUri() == null || plot == null || plot.trim().isEmpty())
                : LibraryHealth.state(context, video) != LibraryHealth.State.AVAILABLE;
            if (!include)
                continue;
            TextView row = action(video.getFilenameNonCryptic(), () -> review(video, category));
            row.setTag("health:" + video.getId());
            row.setOnFocusChangeListener((v, yes) -> {
                if (yes)
                    review(video, category);
            });
            centre.addView(row);
            SharedThreePanel.divider(centre);
            if (count++ == 0)
                review(video, category);
        }
        if (count == 0)
            empty();
    }

    private static MissingFilesRepository.Inventory missingFiles() {
        try {
            return MissingFilesRepository.current();
        } catch (RuntimeException unavailable) {
            return null;
        }
    }

    /**
     * Missing files (approved network-files-v2/07): titles, not addresses; where and since when, the old file name;
     * Find it again (same source, same name and size; watched state and progress kept) or Remove from the library.
     */
    private void showMissing() {
        MissingFilesRepository.Inventory inventory = missingFiles();
        if (inventory.missing.isEmpty()) {
            empty();
            return;
        }
        boolean first = true;
        for (MissingFilesRepository.MissingFile row : inventory.missing) {
            String where = (row.sourceName == null || row.sourceName.isEmpty() ? "Unknown source" : row.sourceName)
                + (row.folder == null || row.folder.isEmpty() ? "" : " › " + row.folder);
            TextView line = action((row.title == null || row.title.isEmpty() ? row.oldFileName : row.title)
                + "\n" + where, () -> missing(row));
            line.setTag("health:missing:" + row.id);
            line.setOnFocusChangeListener((v, yes) -> {
                if (yes)
                    missing(row);
            });
            centre.addView(line);
            SharedThreePanel.divider(centre);
            if (first)
                missing(row);
            first = false;
        }
    }

    private void missing(MissingFilesRepository.MissingFile row) {
        String since = row.sinceUtcMs > 0
            ? android.text.format.DateUtils.getRelativeTimeSpanString(row.sinceUtcMs, System.currentTimeMillis(),
                  android.text.format.DateUtils.DAY_IN_MILLIS).toString()
            : "Unknown";
        information(row.title == null || row.title.isEmpty() ? "Missing file" : row.title,
            "Where it was\n" + (row.sourceName == null || row.sourceName.isEmpty() ? "Unknown" : row.sourceName)
                + (row.folder == null || row.folder.isEmpty() ? "" : " › " + row.folder)
                + "\n\nMissing since\n" + since
                + "\n\nOld file name\n" + (row.oldFileName == null || row.oldFileName.isEmpty() ? "Unknown"
                                                                                                 : row.oldFileName)
                + "\n\nWatched state and progress are kept.");
        right.addView(action("Find it again", () -> work(row, true)));
        right.addView(action("Remove from library", () -> work(row, false)));
    }

    private void work(MissingFilesRepository.MissingFile row, boolean find) {
        information(row.title, find ? "Looking for the file on " + row.sourceName + "…" : "Removing…");
        Context app = context.getApplicationContext();
        new Thread(() -> {
            String outcome;
            try {
                if (find)
                    outcome = MissingFilesRepository.findAgain(app, row, () -> false)
                                  ? "Found it. The title plays from the same place again."
                                  : "Couldn't find exactly one file with that name and size. Nothing changed.";
                else
                    outcome = MissingFilesRepository.remove(app, row)
                                  ? "Removed from your library. No file was deleted."
                                  : "This entry had already changed. Nothing was removed.";
            } catch (Exception failed) {
                outcome = find ? "The search didn't finish. Nothing changed." : "Removing didn't finish.";
            }
            String message = outcome;
            centre.post(() -> information(row.title, message));
        }, "health-missing").start();
    }

    /** How many items a check finds, using exactly the rules of its list. */
    public int count(int category) {
        if (category == 3 && missingFiles() != null)
            return missingFiles().missing.size();
        SharedPreferences health = context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, 0);
        if (category == 4) {
            int sources = 0;
            for (String key : health.getAll().keySet())
                if (key.startsWith("offline:") || key.startsWith("problem:"))
                    sources++;
            return sources;
        }
        boolean importing = ImportState.VIDEO.isInitialImport()
            || ImportState.VIDEO.isRegularImport()
            || LoaderUtils.getScrapeInProgress();
        int count = 0;
        for (Entry entry : snapshot.technical) {
            if (!(entry.media instanceof Video))
                continue;
            Video video = (Video) entry.media;
            boolean matched = video instanceof Movie || video instanceof Episode;
            String plot = video.getDescriptionBody();
            boolean include = category == 0 ? !importing && !matched
                : category == 1 ? health.getBoolean("incorrect:" + video.getId(), false)
                : category == 2
                ? !importing && matched && (video.getPosterUri() == null || plot == null || plot.trim().isEmpty())
                : LibraryHealth.state(context, video) != LibraryHealth.State.AVAILABLE;
            if (include)
                count++;
        }
        return count;
    }

    private void empty() {
        centre.addView(SharedThreePanel.text(context, "No issues in this category", 16));
        information("Library Health",
            "Media still being imported or identified is excluded from unmatched and missing-metadata warnings. Your library data has not changed.");
    }

    private void source(String source) {
        information("Source Problem",
            source + "\n\nReconnect storage or check this source. Library records and viewing progress are retained.");
        right.addView(action("Scan Library", () -> PreviewLibraryScan.request(context)));
    }

    private void review(Video video, int category) {
        information(video.getName(),
            "Filename\n" + video.getFilenameNonCryptic() + "\n\nLocation\n"
                + SupernovaLibraryPolicy.canonical(video.getFileUri())
                + "\n\nMatch Status\n" + (video instanceof Movie || video instanceof Episode ? "Matched" : "Unmatched")
                + "\n\nAvailability\n" + LibraryHealth.message(LibraryHealth.state(context, video)));
        right.addView(action("View Details",
            ()
                -> context.startActivity(new Intent(context, VideoDetailsActivity.class)
                        .putExtra(VideoDetailsFragment.EXTRA_VIDEO, video))));
        right.addView(action("Find a Match",
            ()
                -> context.startActivity(new Intent(context, ManualVideoScrappingActivity.class)
                        .putExtra(ManualVideoScrappingActivity.EXTRA_VIDEO, video))));
        if (category == 1)
            right.addView(action("Match Is Correct", () -> {
                context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, 0)
                    .edit()
                    .remove("incorrect:" + video.getId())
                    .apply();
                show(category);
            }));
    }
}
