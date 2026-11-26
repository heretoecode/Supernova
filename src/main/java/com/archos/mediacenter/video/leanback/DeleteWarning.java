package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The delete warning (owner, 20 Nov 2026; LIBRARY.md › Delete warnings, RULES §7b; `rows-captions/tv/16a`, `16`, `17`,
 * `18`), one for every place that deletes a title's files (library pages, Search, Details):
 * <ul>
 * <li>one file: "Deletes <b>file</b> (2160p · 58.2 GB) from <b>server</b>.";</li>
 * <li>several versions of a film: tick the ones to delete, Select all, the total, the button follows the ticks;</li>
 * <li>a show: the episodes grouped by season, four visible, Select all, the total.</li>
 * </ul>
 * Then **Clean Up** (clear viewing history: off; remove from Trakt history: off, only when Trakt is connected; delete
 * the folder if it is left empty: on) and the buttons, **Keep** focused first. "This can't be undone." shows only
 * while Delete is focused. Left-aligned throughout.
 */
public final class DeleteWarning
{
    /** One file the warning may delete. */
    public static final class File
    {
        public final Uri uri;
        public final String name;
        /** "2160p · HDR10 · Dolby Atmos" (the first three facts the file has), may be empty. */
        public final String facts;
        /** The server or drive ("Home server", "This device"). */
        public final String place;
        public final long bytes;
        /** Shows only: "S1 E1 · The Long Tide", and its season number for the grouping. */
        public String episode = "";
        public int season = -1;
        /** False for a video file in the show's folders that the library doesn't know (the "Not in library" pill). */
        public boolean inLibrary = true;
        boolean ticked = true;

        public File(Uri uri, String name, String facts, String place, long bytes)
        {
            this.uri = uri;
            this.name = name == null ? "" : name;
            this.facts = facts == null ? "" : facts;
            this.place = place == null ? "" : place;
            this.bytes = bytes;
        }
    }

    /** What the user chose. */
    public interface Choice
    {
        void delete(List<Uri> files, boolean clearHistory, boolean removeFromTrakt, boolean deleteEmptyFolder);
    }

    static final int VISIBLE_EPISODES = 4;

    private final Context context;
    private final String title;
    private final boolean show;
    private final List<File> files;
    private final boolean trakt;
    private final Choice choice;
    private Dialog dialog;
    private TextView total;
    private TextView deleteButton;
    private View selectAll;
    private boolean selectAllOn = true;
    private final boolean[] cleanUp = {false, false, true};
    private final List<View> ticks = new ArrayList<>();

    private DeleteWarning(Context context, String title, boolean show, List<File> files, boolean trakt, Choice choice)
    {
        this.context = context;
        this.title = title;
        this.show = show;
        this.files = files;
        this.trakt = trakt;
        this.choice = choice;
    }

    /** Shows the warning; {@code trakt} when Trakt is connected (its Clean Up row shows). */
    public static Dialog show(Context context, String title, boolean show, List<File> files, boolean trakt,
                              Choice choice)
    {
        DeleteWarning warning = new DeleteWarning(context, title, show, files, trakt, choice);
        return warning.build();
    }

    private int dp(float value)
    {
        return SupernovaStyle.dp(context, value);
    }

    private Dialog build()
    {
        dialog = PreviewDialog.create(context, "dialog");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setTag("delete.warning");
        panel.setPadding(dp(28), dp(22), dp(28), dp(20));
        panel.setBackground(SupernovaStyle.glass(context));
        TextView heading = text(show ? "Delete every episode of " + title + "?" : "Delete " + title + "?", 22);
        heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        heading.setTextColor(Color.WHITE);
        panel.addView(heading);
        if (files.size() == 1 && !show)
        {
            File only = files.get(0);
            panel.addView(spaced(text(oneLine(only), 15), 14));
            panel.addView(spaced(soft("Its own subtitle and .nfo files go with it; anything else in the folder stays."),
                                 8));
        }
        else
        {
            panel.addView(spaced(text(show ? new SpannableStringBuilder(
                                                 "Tick the episodes to delete, including any not yet in your library.")
                                           : bold("You have ", files.size() + " versions",
                                                  ". Tick the ones to delete."), 15), 14));
            selectAll = switchRow("delete.select.all", "checkbox", "Select all", true, on -> {
                selectAllOn = on;
                for (File file : files)
                {
                    file.ticked = on;
                }
                refreshTicks();
            });
            panel.addView(spaced(selectAll, 10));
            panel.addView(list());
            total = text("", 15);
            panel.addView(spaced(total, 10));
            panel.addView(spaced(soft(show ? "An episode's own subtitle files go with it; anything else in the folders"
                                             + " stays."
                                           : "Each file's own subtitle and .nfo files go with it; anything else in the"
                                             + " folder stays."), 6));
        }
        panel.addView(spaced(SupernovaStyle.groupHeading(context, "Clean Up", 12), 16));
        panel.addView(switchRow("delete.cleanup.history", "history", "Also clear viewing history in Supernova",
                                cleanUp[0], on -> cleanUp[0] = on));
        if (trakt)
        {
            panel.addView(switchRow("delete.cleanup.trakt", "trakt", "Also remove it from your Trakt history",
                                    cleanUp[1], on -> cleanUp[1] = on));
        }
        panel.addView(switchRow("delete.cleanup.folder", "folder", "Delete the folder if it's left empty",
                                cleanUp[2], on -> cleanUp[2] = on));
        panel.addView(spaced(buttons(), 18));
        refreshTicks();
        ScrollView scroll = new ScrollView(context);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.addView(panel);
        dialog.setContentView(scroll);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null)
        {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(Math.min(dp(470), TvLayout.dialogWidth(context) + dp(80)), -2);
        }
        View keep = panel.findViewWithTag("delete.keep");
        if (keep != null)
        {
            keep.requestFocus();
        }
        return dialog;
    }

    /** "Deletes <b>Anora.2024.2160p.mkv</b> (2160p · 58.2 GB) from <b>Home server</b>." */
    static CharSequence oneLine(File file)
    {
        SpannableStringBuilder line = new SpannableStringBuilder("Deletes ");
        int start = line.length();
        line.append(file.name);
        line.setSpan(new StyleSpan(Typeface.BOLD), start, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        String facts = joined(firstFact(file.facts), size(file.bytes));
        if (!facts.isEmpty())
        {
            line.append(" (").append(facts).append(")");
        }
        if (!file.place.isEmpty())
        {
            line.append(" from ");
            start = line.length();
            line.append(file.place);
            line.setSpan(new StyleSpan(Typeface.BOLD), start, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return line.append(".");
    }

    /** "Deletes <b>2 files · 72.3 GB</b>." */
    static CharSequence totalLine(List<File> files)
    {
        int count = 0;
        long bytes = 0;
        for (File file : files)
        {
            if (file.ticked)
            {
                count++;
                bytes += Math.max(0, file.bytes);
            }
        }
        String what = count + (count == 1 ? " file" : " files") + (bytes > 0 ? " · " + size(bytes) : "");
        return bold("Deletes ", what, ".");
    }

    /** The button's words follow the ticks: "Delete file", "Delete 2 files". */
    static String deleteWords(List<File> files, boolean show)
    {
        int count = 0;
        for (File file : files)
        {
            if (file.ticked)
            {
                count++;
            }
        }
        if (files.size() == 1 && !show)
        {
            return "Delete file";
        }
        return count == 1 ? "Delete 1 file" : "Delete " + count + " files";
    }

    /** "58.2 GB", "700 MB"; empty when unknown. */
    static String size(long bytes)
    {
        if (bytes <= 0)
        {
            return "";
        }
        double gb = bytes / 1_000_000_000d;
        if (gb >= 1)
        {
            return String.format(Locale.ROOT, "%.1f GB", gb);
        }
        return Math.max(1, Math.round(bytes / 1_000_000d)) + " MB";
    }

    private static String firstFact(String facts)
    {
        String[] parts = facts.split(" · ");
        return parts.length == 0 ? "" : parts[0].trim();
    }

    private static String joined(String... parts)
    {
        List<String> kept = new ArrayList<>();
        for (String part : parts)
        {
            if (part != null && !part.isEmpty())
            {
                kept.add(part);
            }
        }
        return android.text.TextUtils.join(" · ", kept);
    }

    private static SpannableStringBuilder bold(String before, String strong, String after)
    {
        SpannableStringBuilder text = new SpannableStringBuilder(before);
        int start = text.length();
        text.append(strong);
        text.setSpan(new StyleSpan(Typeface.BOLD), start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text.append(after);
    }

    /** The versions or episodes, each with the app's tick; shows grouped by season, four episodes visible. */
    private View list()
    {
        LinearLayout rows = new LinearLayout(context);
        rows.setOrientation(LinearLayout.VERTICAL);
        int lastSeason = Integer.MIN_VALUE;
        for (File file : files)
        {
            if (show && file.season != lastSeason)
            {
                lastSeason = file.season;
                TextView season = text(file.season > 0 ? "Season " + file.season : "Other episodes", 13);
                season.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                rows.addView(spaced(season, rows.getChildCount() == 0 ? 0 : 10));
            }
            rows.addView(fileRow(file));
        }
        if (!show)
        {
            return rows;
        }
        ScrollView scroll = new ScrollView(context);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setTag("delete.episodes");
        scroll.addView(rows);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(46) * VISIBLE_EPISODES + dp(20)));
        return scroll;
    }

    private View fileRow(File file)
    {
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setClickable(true);
        row.setTag("delete.file:" + file.name);
        row.setPadding(dp(10), dp(5), dp(10), dp(5));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(LinearLayout.VERTICAL);
        String first = show ? (file.episode.isEmpty() ? file.name : file.episode)
                            : file.facts.isEmpty() ? file.name : file.facts;
        String second = show ? joined(file.facts, size(file.bytes))
                             : joined(file.facts.isEmpty() ? "" : file.name, file.place, size(file.bytes));
        TextView line1 = text(first, 14);
        line1.setTag("first");
        TextView line2 = text(second, 11);
        line2.setTag("second");
        words.addView(line1);
        words.addView(line2);
        row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        if (!file.inLibrary)
        {
            // LIBRARY.md: files the library doesn't know carry a Not in library pill between the text and the tick
            // (outline = information, RULES §3).
            TextView pill = text("Not in library", 11);
            pill.setTag("delete.notInLibrary");
            pill.setPadding(dp(8), dp(2), dp(8), dp(2));
            android.graphics.drawable.GradientDrawable outline = new android.graphics.drawable.GradientDrawable();
            outline.setCornerRadius(dp(10));
            outline.setStroke(Math.max(1, dp(1)), 0xB3FFFFFF);
            pill.setBackground(outline);
            LinearLayout.LayoutParams pillSize = new LinearLayout.LayoutParams(-2, -2);
            pillSize.leftMargin = dp(12);
            pillSize.rightMargin = dp(12);
            row.addView(pill, pillSize);
        }
        ImageView tick = new ImageView(context);
        tick.setTag("tick");
        row.addView(tick, new LinearLayout.LayoutParams(dp(16), dp(16)));
        row.setTag(com.archos.mediacenter.video.R.id.panel_button_picked, file);
        row.setOnClickListener(v -> {
            file.ticked = !file.ticked;
            boolean all = true;
            for (File each : files)
            {
                all &= each.ticked;
            }
            selectAllOn = all;
            refreshTicks();
        });
        row.setOnFocusChangeListener((v, has) -> paintRow(row, has, file.ticked));
        ticks.add(row);
        return spaced(row, 2);
    }

    private void paintRow(LinearLayout row, boolean focused, boolean ticked)
    {
        row.setBackground(focused ? SupernovaStyle.rounded(context, SupernovaStyle.FOCUS_FILL,
                                                           SupernovaStyle.SMALL_BOX_RADIUS_DP) : null);
        int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
        TextView first = row.findViewWithTag("first");
        TextView second = row.findViewWithTag("second");
        first.setTextColor(ink);
        second.setTextColor(focused ? SupernovaStyle.TEXT_SECONDARY_ON_FOCUS : SupernovaStyle.TEXT_SECONDARY);
        ImageView tick = row.findViewWithTag("tick");
        PreviewIcon mark = new PreviewIcon("check");
        mark.setColorFilter(new android.graphics.PorterDuffColorFilter(ink, android.graphics.PorterDuff.Mode.SRC_IN));
        tick.setImageDrawable(ticked ? mark : null);
        row.setContentDescription(first.getText() + (ticked ? ", ticked" : ", not ticked"));
    }

    private void refreshTicks()
    {
        for (View row : ticks)
        {
            File file = (File) row.getTag(com.archos.mediacenter.video.R.id.panel_button_picked);
            paintRow((LinearLayout) row, row.isFocused(), file.ticked);
        }
        if (selectAll != null)
        {
            ((Runnable) selectAll.getTag(com.archos.mediacenter.video.R.id.panel_button_picked)).run();
        }
        if (total != null)
        {
            total.setText(totalLine(files));
        }
        if (deleteButton != null)
        {
            deleteButton.setText(deleteWords(files, show));
            boolean any = false;
            for (File file : files)
            {
                any |= file.ticked;
            }
            deleteButton.setEnabled(any);
            deleteButton.setAlpha(any ? 1f : .4f);
        }
    }

    /** A switch row (RULES §8); OK flips it. Its tag holds a refresh for Select all. */
    private View switchRow(String tag, String icon, String label, boolean on, java.util.function.Consumer<Boolean> set)
    {
        LinearLayout row = new LinearLayout(context);
        row.setTag(tag);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setClickable(true);
        row.setPadding(dp(10), dp(6), dp(10), dp(6));
        ImageView mark = new ImageView(context);
        row.addView(mark, new LinearLayout.LayoutParams(dp(16), dp(16)));
        TextView name = text(label, 14);
        LinearLayout.LayoutParams nameSize = new LinearLayout.LayoutParams(0, -2, 1);
        nameSize.leftMargin = dp(10);
        row.addView(name, nameSize);
        ImageView knob = new ImageView(context);
        row.addView(knob, new LinearLayout.LayoutParams(dp(30), dp(18)));
        boolean[] state = {on};
        Runnable paint = () -> {
            if (selectAll == row)
            {
                state[0] = selectAllOn;
            }
            boolean focused = row.isFocused();
            row.setBackground(focused ? SupernovaStyle.rounded(context, SupernovaStyle.FOCUS_FILL,
                                                               SupernovaStyle.SMALL_BOX_RADIUS_DP) : null);
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            PreviewIcon glyph = new PreviewIcon(icon);
            glyph.setColorFilter(new android.graphics.PorterDuffColorFilter(ink,
                                                                            android.graphics.PorterDuff.Mode.SRC_IN));
            mark.setImageDrawable(glyph);
            name.setTextColor(ink);
            knob.setImageDrawable(StandardSwitchDrawable.snapshot(context, state[0], focused));
            row.setContentDescription(label + ", " + (state[0] ? "On" : "Off"));
        };
        row.setTag(com.archos.mediacenter.video.R.id.panel_button_picked, paint);
        row.setOnFocusChangeListener((v, has) -> paint.run());
        row.setOnClickListener(v -> {
            state[0] = !state[0];
            set.accept(state[0]);
            paint.run();
        });
        paint.run();
        return row;
    }

    /** Keep (focused first) · Delete; "This can't be undone." beside Delete only while it is focused. */
    private View buttons()
    {
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        boolean several = show || files.size() > 1;
        TextView keep = TvButton.create(context, several ? "Keep files" : "Keep file", dialog::dismiss, false);
        keep.setTag("delete.keep");
        row.addView(keep);
        deleteButton = TvButton.create(context, deleteWords(files, show), this::confirm, false);
        deleteButton.setTag("delete.confirm");
        deleteButton.setTextColor(SupernovaStyle.destructiveContent());
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-2, -2);
        gap.leftMargin = dp(10);
        row.addView(deleteButton, gap);
        TextView undone = text("This can't be undone.", 13);
        undone.setTag("delete.undone");
        undone.setTextColor(Color.WHITE);
        undone.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams undoneAt = new LinearLayout.LayoutParams(-2, -2);
        undoneAt.leftMargin = dp(14);
        row.addView(undone, undoneAt);
        View.OnFocusChangeListener look = deleteButton.getOnFocusChangeListener();
        deleteButton.setOnFocusChangeListener((v, has) -> {
            if (look != null)
            {
                look.onFocusChange(v, has);
            }
            undone.setVisibility(has ? View.VISIBLE : View.INVISIBLE);
        });
        return row;
    }

    private void confirm()
    {
        List<Uri> chosen = new ArrayList<>();
        for (File file : files)
        {
            if (file.ticked)
            {
                chosen.add(file.uri);
            }
        }
        if (chosen.isEmpty())
        {
            return;
        }
        dialog.dismiss();
        choice.delete(chosen, cleanUp[0], trakt && cleanUp[1], cleanUp[2]);
    }

    private TextView text(CharSequence words, int size)
    {
        TextView text = new TextView(context);
        text.setText(words);
        text.setTextSize(size);
        text.setTextColor(Color.WHITE);
        text.setIncludeFontPadding(false);
        text.setLineSpacing(dp(3), 1f);
        return text;
    }

    private TextView soft(String words)
    {
        TextView text = text(words, 13);
        text.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        return text;
    }

    private View spaced(View view, int topDp)
    {
        LinearLayout.LayoutParams size = view.getLayoutParams() instanceof LinearLayout.LayoutParams
            ? (LinearLayout.LayoutParams) view.getLayoutParams() : new LinearLayout.LayoutParams(-1, -2);
        size.topMargin = dp(topDp);
        view.setLayoutParams(size);
        return view;
    }
}
