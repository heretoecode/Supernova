package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.archos.mediacenter.video.leanback.LibraryHealthActivity;
import com.archos.mediacenter.video.leanback.PageTabs;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.scanning.LibraryScanController;
import com.archos.mediacenter.video.scanning.ScanProgress;

import java.util.List;

/**
 * The redesigned Files page (owner, 6–12 Nov 2026; mock-ups network-files-tabs/33–44; RULES): plain charcoal, tabs
 * This Device · Connections · Library Care · Transfers with the open tab's facts on the right. Built up tab by tab;
 * it replaces the old Files page once every tab is in (see docs/0.157/HANDOVER.md).
 */
public final class FilesPage extends LinearLayout
{
    public static final String[] TABS = {"This Device", "Connections", "Library Care", "Transfers"};
    static final String[] TAB_ICONS = {"tv", "integration", "health", "arrow-down"};
    public static final int LIBRARY_CARE = 2;

    static final int SECOND_LINE = 0xFFC9D3E6;
    static final int LINE = 0x14FFFFFF;

    private final PageTabs tabs;
    private final LinearLayout summary;
    private final FrameLayout body;
    private LibraryCareCounts counts;
    private int videos;
    private long lastScanMs;
    private boolean scanning;

    public FilesPage(Context context)
    {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(0xFF16181C);
        int side = SupernovaStyle.dp(context, 48);
        setPadding(side, SupernovaStyle.dp(context, 8), side, 0);
        LinearLayout top = new LinearLayout(context);
        top.setGravity(Gravity.CENTER_VERTICAL);
        tabs = new PageTabs(context, TABS, TAB_ICONS, this::show);
        top.addView(tabs, new LayoutParams(-2, -2));
        top.addView(new View(context), new LayoutParams(0, 1, 1));
        summary = new LinearLayout(context);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(summary, new LayoutParams(-2, -2));
        addView(top, new LayoutParams(-1, -2));
        View line = new View(context);
        line.setBackgroundColor(LINE);
        addView(line, new LayoutParams(-1, Math.max(1, SupernovaStyle.dp(context, 1))));
        body = new FrameLayout(context);
        addView(body, new LayoutParams(-1, 0, 1));
        tabs.setDown(this::focusContent);
        getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> tabs.refresh());
        tabs.select(LIBRARY_CARE);
    }

    /** A place Supernova scans: this device's folders, or one network connection. */
    public static final class Source
    {
        public final String label;
        public final android.net.Uri root;
        public final boolean local;

        public Source(String label, android.net.Uri root, boolean local)
        {
            this.label = label;
            this.root = root;
            this.local = local;
        }
    }

    private final List<Source> sources = new java.util.ArrayList<>();
    private java.util.function.Consumer<Source> scanOne = source -> { };

    /** The sources for Scan one source, and what scanning one does. */
    public void setSources(List<Source> latest, java.util.function.Consumer<Source> scan)
    {
        sources.clear();
        sources.addAll(latest);
        scanOne = scan;
        show(tabs.openTab());
    }

    /** New figures from the library; the open tab and the summary redraw. */
    public void update(LibraryCareCounts latest, int videoCount, long lastScan, boolean scanRunning)
    {
        counts = latest;
        videos = videoCount;
        lastScanMs = lastScan;
        scanning = scanRunning;
        tabs.setBadge(LIBRARY_CARE, latest == null ? 0 : latest.toCheck());
        show(tabs.openTab());
    }

    private void show(int tab)
    {
        body.removeAllViews();
        showSummary();
        ScrollView scroll = new ScrollView(getContext());
        scroll.setClipToPadding(false);
        LinearLayout column = new LinearLayout(getContext());
        column.setOrientation(VERTICAL);
        column.setPadding(0, SupernovaStyle.dp(getContext(), 24), 0, SupernovaStyle.dp(getContext(), 27));
        scroll.addView(column);
        body.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        if (tab == LIBRARY_CARE)
        {
            libraryCare(column);
        }
    }

    private void showSummary()
    {
        summary.removeAllViews();
        int toCheck = counts == null ? 0 : counts.toCheck();
        List<FilesSummary.Fact> facts =
            FilesSummary.facts(videos, toCheck, lastScanMs, System.currentTimeMillis(), scanning);
        for (int index = 0; index < facts.size(); index++)
        {
            if (index > 0)
            {
                summary.addView(text(" · ", 18, SECOND_LINE));
            }
            TextView fact = text(facts.get(index).text, 18, Color.WHITE);
            PreviewIcon.apply(fact, facts.get(index).icon, 16);
            summary.addView(fact);
        }
    }

    private void libraryCare(LinearLayout column)
    {
        heading(column, "Library Health", "health", false);
        boolean ready = counts != null;
        row(column, "folder", "Missing files", "In your library but no longer found",
            ready ? LibraryCareCounts.pill(counts.missing, "file", "files") : "Loading…", this::openHealth, true);
        row(column, "search", "Not matched", "Videos without a movie or TV show",
            ready ? LibraryCareCounts.pill(counts.notMatched, "video", "videos") : "Loading…",
            this::openHealth, false);
        scanOneSource(column);
        recentScans(column);
    }

    /** "Scan one source": the row shows one button per source (mock-up network-files-tabs/39). */
    private void scanOneSource(LinearLayout column)
    {
        heading(column, "Scanning", "scan", true);
        if (sources.isEmpty())
        {
            return;
        }
        Context context = getContext();
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = SupernovaStyle.dp(context, 20);
        row.setPadding(pad, 0, SupernovaStyle.dp(context, 10), 0);
        row.setTag("files.scan.one");
        TextView mark = new TextView(context);
        PreviewIcon.apply(mark, "integration", 16);
        row.addView(mark, new LayoutParams(SupernovaStyle.dp(context, 36), -2));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(VERTICAL);
        TextView title = text("Scan one source", 18, Color.WHITE);
        TextView second = text("Look for new videos in one place only", 13.5f, SECOND_LINE);
        words.addView(title);
        words.addView(second);
        row.addView(words, new LayoutParams(0, -2, 1));
        List<TextView> buttons = new java.util.ArrayList<>();
        Runnable restyle = () -> {
            boolean inside = row.hasFocus();
            row.setBackground(inside ? box(SupernovaStyle.FOCUS_FILL) : null);
            int ink = inside ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            title.setTextColor(ink);
            second.setTextColor(inside ? SupernovaStyle.FOCUS_TEXT : SECOND_LINE);
            mark.setTextColor(ink);
            SupernovaStyle.tintIcons(mark);
            for (TextView button : buttons)
            {
                boolean focused = button.isFocused();
                button.setVisibility(inside || buttons.indexOf(button) == 0 ? VISIBLE : INVISIBLE);
                button.setTextColor(focused ? Color.WHITE : ink);
                button.setBackground(focused ? box(SupernovaStyle.FOCUS_TEXT) : outline(ink));
                SupernovaStyle.tintIcons(button);
            }
        };
        for (Source source : sources)
        {
            TextView button = text(source.label, 15, Color.WHITE);
            button.setGravity(Gravity.CENTER);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            int inner = SupernovaStyle.dp(context, 12);
            button.setPadding(inner, 0, inner, 0);
            PreviewIcon.apply(button, source.local ? "tv" : "integration", 14);
            button.setContentDescription("Scan " + source.label);
            button.setOnClickListener(view -> scanOne.accept(source));
            button.setOnFocusChangeListener((view, focused) -> restyle.run());
            LayoutParams size = new LayoutParams(-2, SupernovaStyle.dp(context, 30));
            size.leftMargin = SupernovaStyle.dp(context, 6);
            row.addView(button, size);
            buttons.add(button);
        }
        if (column.findViewWithTag("files.row.first") == null)
        {
            buttons.get(0).setTag("files.row.first");
        }
        restyle.run();
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, 64)));
    }

    /** The last three scans (approved Recent scans); plain information, so no row lines and no focus. */
    private void recentScans(LinearLayout column)
    {
        heading(column, "Recent Scans", "history", true);
        List<ScanProgress> scans = LibraryScanController.recentScans();
        if (scans.isEmpty())
        {
            column.addView(text("No scans yet", 13.5f, SECOND_LINE));
            return;
        }
        long now = System.currentTimeMillis();
        for (int index = 0; index < Math.min(3, scans.size()); index++)
        {
            ScanProgress scan = scans.get(index);
            LinearLayout line = new LinearLayout(getContext());
            line.setGravity(Gravity.CENTER_VERTICAL);
            int pad = SupernovaStyle.dp(getContext(), 20);
            line.setPadding(pad, SupernovaStyle.dp(getContext(), 6), pad, SupernovaStyle.dp(getContext(), 6));
            TextView mark = new TextView(getContext());
            PreviewIcon.apply(mark, "scan", 16);
            line.addView(mark, new LayoutParams(SupernovaStyle.dp(getContext(), 36), -2));
            LinearLayout words = new LinearLayout(getContext());
            words.setOrientation(VERTICAL);
            String name = scan.sourceLabel == null || scan.sourceLabel.isEmpty() ? "Library" : scan.sourceLabel;
            words.addView(text(name, 16, Color.WHITE));
            words.addView(text(RecentScanLine.second(scan.outcome(), scan.failureReason(), scan.startedUtcMs, now,
                                                     scan.checked, scan.added, scan.removed), 13.5f, SECOND_LINE));
            line.addView(words, new LayoutParams(0, -2, 1));
            TextView status = text(RecentScanLine.pill(scan.outcome()), 13.5f, Color.WHITE);
            status.setGravity(Gravity.CENTER);
            status.setBackground(outline(Color.WHITE));
            int pillWidth = SupernovaStyle.dp(getContext(), 95);
            line.addView(status, new LayoutParams(pillWidth, SupernovaStyle.dp(getContext(), 22)));
            column.addView(line, new LayoutParams(-1, -2));
        }
    }

    private void openHealth()
    {
        getContext().startActivity(new Intent(getContext(), LibraryHealthActivity.class));
    }

    private void focusContent()
    {
        View first = body.findViewWithTag("files.row.first");
        if (first != null)
        {
            first.requestFocus();
        }
    }

    // ---- page parts (RULES §5, §7) -----------------------------------------------------------------------------

    private void heading(LinearLayout column, String title, String icon, boolean gapAbove)
    {
        TextView heading = text(title, 16, Color.WHITE);
        PreviewIcon.apply(heading, icon, 16);
        LayoutParams size = new LayoutParams(-2, -2);
        size.topMargin = gapAbove ? SupernovaStyle.dp(getContext(), 32) : 0;
        size.bottomMargin = SupernovaStyle.dp(getContext(), 13);
        column.addView(heading, size);
    }

    private void row(LinearLayout column, String icon, String name, String line, String pill, Runnable open,
                     boolean first)
    {
        Context context = getContext();
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        int pad = SupernovaStyle.dp(context, 20);
        row.setPadding(pad, 0, pad, 0);
        row.setContentDescription(name + ", " + pill);
        TextView mark = new TextView(context);
        PreviewIcon.apply(mark, icon, 16);
        row.addView(mark, new LayoutParams(SupernovaStyle.dp(context, 36), -2));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(VERTICAL);
        TextView title = text(name, 18, Color.WHITE);
        TextView second = text(line, 13.5f, SECOND_LINE);
        words.addView(title);
        words.addView(second);
        row.addView(words, new LayoutParams(0, -2, 1));
        TextView status = text(pill, 13.5f, Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setBackground(outline(Color.WHITE));
        row.addView(status, new LayoutParams(SupernovaStyle.dp(context, 95), SupernovaStyle.dp(context, 22)));
        row.addView(new View(context), new LayoutParams(0, 1, 0.6f));
        TextView chevron = text("›", 20, Color.WHITE);
        row.addView(chevron);
        row.setOnClickListener(view -> open.run());
        row.setOnFocusChangeListener((view, focused) -> {
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            title.setTextColor(ink);
            second.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : SECOND_LINE);
            status.setTextColor(ink);
            status.setBackground(outline(ink));
            chevron.setTextColor(ink);
            mark.setTextColor(ink);
            SupernovaStyle.tintIcons(mark);
            row.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL) : null);
        });
        Object previous = column.getChildCount() == 0 ? null : column.getChildAt(column.getChildCount() - 1).getTag();
        if (previous instanceof String && ((String) previous).startsWith(ROW))
        {
            View divider = new View(context);
            divider.setBackgroundColor(LINE);
            column.addView(divider, new LayoutParams(-1, Math.max(1, SupernovaStyle.dp(context, 1))));
        }
        row.setTag(first ? "files.row.first" : ROW);
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, 64)));
    }

    private static final String ROW = "files.row";

    private TextView text(String value, float size, int colour)
    {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(colour);
        view.setSingleLine(true);
        view.setEllipsize(TextUtils.TruncateAt.END);
        return view;
    }

    private GradientDrawable outline(int colour)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(SupernovaStyle.dp(getContext(), 11));
        shape.setStroke(Math.max(1, SupernovaStyle.dp(getContext(), 1)), colour);
        return shape;
    }

    private GradientDrawable box(int colour)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(SupernovaStyle.dp(getContext(), 10));
        shape.setColor(colour);
        return shape;
    }
}
