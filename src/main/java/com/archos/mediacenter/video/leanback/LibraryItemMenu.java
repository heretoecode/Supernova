package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * The library long-press menu (page sheet LIBRARY.md › Long-press; owner 19–20 Nov 2026), one menu wherever a library
 * title is long-pressed: library pages, Search results, Home. **Playback** (Resume / Play, then Play from the start
 * only on a started title) · optional Details · **Library** (Mark as watched, Fix match, Remove from library) ·
 * **Files & Media** (Delete file). Icons each, no two alike. Each page supplies what the rows do.
 */
public final class LibraryItemMenu
{
    /** What the rows do; a null action leaves its row out. */
    public static final class Spec
    {
        public String title = "";
        public boolean show;
        public boolean watched;
        /** "Play", "Resume · 42 min left" or "Resume S1 E2". */
        public String playLabel = "Play";
        public boolean started;
        public Runnable play;
        public Runnable playFromStart;
        /** Home's hero only: OK already opens Details elsewhere (LIBRARY.md: no "More info"). */
        public Runnable details;
        public Runnable markWatched;
        public Runnable fixMatch;
        public Runnable remove;
        public Runnable delete;
    }

    /** The menu's rows in order: labels (group headings start with "— "), icons and commands, same length. */
    public static final class Rows
    {
        public final List<String> labels = new ArrayList<>();
        public final List<String> icons = new ArrayList<>();
        public final List<Runnable> commands = new ArrayList<>();

        void add(String label, String icon, Runnable command)
        {
            labels.add(label);
            icons.add(icon);
            commands.add(command);
        }

        void group(String name)
        {
            add("— " + name, null, null);
        }
    }

    private LibraryItemMenu()
    {
    }

    /** The rows for a title, in the sheet's order. */
    public static Rows rows(Spec spec)
    {
        Rows rows = new Rows();
        rows.group("Playback");
        if (spec.play != null)
        {
            rows.add(spec.playLabel, spec.started ? "resume" : "play", spec.play);
        }
        if (spec.started && spec.playFromStart != null)
        {
            rows.add("Play from the start", "history", spec.playFromStart);
        }
        if (spec.details != null)
        {
            rows.add("Details", "details", spec.details);
        }
        rows.group("Library");
        if (spec.markWatched != null)
        {
            String label = spec.watched ? (spec.show ? "Mark show as unwatched" : "Mark as unwatched")
                                        : (spec.show ? "Mark show as watched" : "Mark as watched");
            rows.add(label, "check", spec.markWatched);
        }
        if (spec.fixMatch != null)
        {
            rows.add("Fix match", "edit", spec.fixMatch);
        }
        if (spec.remove != null)
        {
            rows.add("Remove from library", "remove", spec.remove);
        }
        if (spec.delete != null)
        {
            rows.group("Files & Media");
            rows.add(spec.show ? "Delete all episode files" : "Delete file", "delete", spec.delete);
        }
        return rows;
    }

    /** Opens the menu beside the anchor; focus starts on the first row (Resume / Play). */
    public static void show(Context context, View anchor, Spec spec)
    {
        Rows rows = rows(spec);
        PreviewDialog.menu(context, anchor, spec.title, rows.labels.toArray(new String[0]),
                           rows.icons.toArray(new String[0]), -1, n -> {
                               Runnable command = rows.commands.get(n);
                               if (command != null)
                               {
                                   command.run();
                               }
                           });
    }
}
