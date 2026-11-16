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
 * This Device · Connections · Library Care (Transfers later) with the open tab's facts on the right. Built up tab
 * by tab; it replaces the old Files page once every tab is in (see docs/0.157/HANDOVER.md).
 */
public final class FilesPage extends LinearLayout
{
    // Transfers waits for 0.158 (build order: "Files without Transfers").
    public static final String[] TABS = {"This Device", "Connections", "Library Care"};
    static final String[] TAB_ICONS = {"tv", "integration", "health"};
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
        // Content lines up with the top bar (mock-up network-files-tabs/39: 96px from the edge at 1080p).
        int side = SupernovaStyle.dp(context, 20);
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

    /** A storage on the This Device tab: this device itself or a USB drive. */
    public static final class Device
    {
        public final String name;
        /** "SanDisk USB drive"; empty for this device itself. */
        public final String kind;
        public final boolean removable;
        public final long videos;
        public final long freeBytes;
        public final long totalBytes;

        public Device(String name, String kind, boolean removable, long videos, long freeBytes, long totalBytes)
        {
            this.name = name;
            this.kind = kind;
            this.removable = removable;
            this.videos = videos;
            this.freeBytes = freeBytes;
            this.totalBytes = totalBytes;
        }
    }

    public static final int THIS_DEVICE = 0;
    private final List<Device> devices = new java.util.ArrayList<>();
    private java.util.function.Consumer<Device> openDevice = device -> { };

    public void setDevices(List<Device> latest, java.util.function.Consumer<Device> open)
    {
        devices.clear();
        devices.addAll(latest);
        openDevice = open;
        show(tabs.openTab());
    }

    /** A saved connection on the Connections tab. */
    public static final class Connection
    {
        public final String name;
        public final android.net.Uri root;
        /** Null while it is being checked. */
        public final Boolean reachable;
        public final long videos;
        public final long lastSeenMs;

        public Connection(String name, android.net.Uri root, Boolean reachable, long videos, long lastSeenMs)
        {
            this.name = name;
            this.root = root;
            this.reachable = reachable;
            this.videos = videos;
            this.lastSeenMs = lastSeenMs;
        }
    }

    /** What the Connections tab's buttons do; the hosting page supplies the existing flows. */
    public interface ConnectionActions
    {
        void open(Connection connection);

        void tryAgain(Connection connection);

        void edit(Connection connection);

        void remove(Connection connection);

        void add();
    }

    public static final int CONNECTIONS = 1;
    private final List<Connection> connections = new java.util.ArrayList<>();
    private ConnectionActions connectionActions;

    /** A device found on the network that is not a connection yet. */
    public static final class Found
    {
        public final String name;
        public final String line;
        public final android.net.Uri uri;

        public Found(String name, String line, android.net.Uri uri)
        {
            this.name = name;
            this.line = line;
            this.uri = uri;
        }
    }

    private final List<Found> found = new java.util.ArrayList<>();

    public void setFound(List<Found> latest)
    {
        found.clear();
        found.addAll(latest);
        if (tabs.openTab() == CONNECTIONS)
        {
            show(CONNECTIONS);
        }
    }

    public void setConnections(List<Connection> latest, ConnectionActions actions)
    {
        connections.clear();
        connections.addAll(latest);
        connectionActions = actions;
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
        else if (tab == CONNECTIONS)
        {
            connections(column);
        }
        else if (tab == THIS_DEVICE)
        {
            thisDevice(column);
        }
    }

    private void showSummary()
    {
        summary.removeAllViews();
        int toCheck = counts == null ? 0 : counts.toCheck();
        List<FilesSummary.Fact> facts =
            FilesSummary.facts(videos, toCheck, lastScanMs, System.currentTimeMillis(), scanning);
        if (tabs.openTab() == THIS_DEVICE)
        {
            long free = 0;
            long deviceVideos = 0;
            for (Device device : devices)
            {
                free += device.freeBytes;
                deviceVideos += device.videos;
            }
            facts = FilesSummary.deviceFacts(devices.size(), deviceVideos, free);
        }
        if (tabs.openTab() == CONNECTIONS)
        {
            int reachable = 0;
            for (Connection connection : connections)
            {
                if (Boolean.TRUE.equals(connection.reachable))
                {
                    reachable++;
                }
            }
            facts = FilesSummary.connectionFacts(connections.size(), reachable, videos);
        }
        for (int index = 0; index < facts.size(); index++)
        {
            if (index > 0)
            {
                summary.addView(text(" · ", 18, SECOND_LINE));
            }
            TextView fact = text(facts.get(index).text, 18, Color.WHITE);
            PreviewIcon.apply(fact, facts.get(index).icon, 16);
            summary.addView(fact);
            if (index == facts.size() - 1 && "scan".equals(facts.get(index).icon))
            {
                scanControl(fact);
            }
        }
    }

    private Runnable scanNow = () -> { };
    private Runnable stopScan = () -> { };

    /** What the tab row's scan control does: OK scans now, or stops the scan that is running. */
    public void setScanActions(Runnable now, Runnable stop)
    {
        scanNow = now;
        stopScan = stop;
    }

    /** The last fact is the scan control on every Files tab (owner, 12 Nov 2026): Right from the last tab. */
    private void scanControl(TextView fact)
    {
        int pad = SupernovaStyle.dp(getContext(), 10);
        fact.setPadding(pad, SupernovaStyle.dp(getContext(), 4), pad, SupernovaStyle.dp(getContext(), 4));
        fact.setFocusable(true);
        fact.setFocusableInTouchMode(true);
        fact.setTag("files.scan.control");
        fact.setContentDescription(scanning ? "Scanning. Press OK to stop" : fact.getText() + ". Press OK to scan now");
        fact.setOnClickListener(view -> (scanning ? stopScan : scanNow).run());
        fact.setOnFocusChangeListener((view, focused) -> {
            fact.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE);
            SupernovaStyle.tintIcons(fact);
            fact.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL) : null);
        });
    }

    private void libraryCare(LinearLayout column)
    {
        if (matchPage)
        {
            matchResults(column);
            return;
        }
        heading(column, "Library Health", "health", false);
        boolean ready = counts != null;
        row(column, "folder", "Missing files", "In your library but no longer found",
            ready ? LibraryCareCounts.pill(counts.missing, "file", "files") : "Loading…", this::openHealth, true);
        row(column, "film", "Couldn't be read", "Files Supernova could not open",
            unreadable < 0 ? "Loading…" : LibraryCareCounts.pill(unreadable, "file", "files"),
            this::openHealth, false);
        row(column, "search", "Not matched", "Videos without a movie or TV show",
            ready ? LibraryCareCounts.pill(counts.notMatched, "video", "videos") : "Loading…",
            this::openHealth, false);
        row(column, "done", "Check matches", "Look every title up again; you see the changes first", matchPill(),
            () -> {
                matchPage = true;
                startCheck();
                refreshMatches();
            }, false);
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
            SupernovaStyle.inkIcons(mark, ink);
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
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, ROW_HEIGHT_DP)));
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

    /** Files › This Device (mock-up network-files-tabs/01): one row per storage with a bar for the space used. */
    private void thisDevice(LinearLayout column)
    {
        if (devices.isEmpty())
        {
            column.addView(text("Looking for storage…", 13.5f, SECOND_LINE));
            return;
        }
        for (Device device : devices)
        {
            row(column, device.removable ? "usbdrive" : "tv", device.name,
                DeviceLine.second(device.kind, device.videos, device.freeBytes, device.totalBytes), null,
                () -> openDevice.accept(device), column.findViewWithTag("files.row.first") == null);
            View last = column.getChildAt(column.getChildCount() - 1);
            if (device.totalBytes > 0 && last instanceof LinearLayout)
            {
                addUsageBar((LinearLayout) last, DeviceLine.used(device.freeBytes, device.totalBytes));
            }
        }
    }

    /** The thin bar under a storage row's second line: the share of the drive in use. */
    private void addUsageBar(LinearLayout row, int usedPerThousand)
    {
        LinearLayout words = null;
        for (int index = 0; index < row.getChildCount(); index++)
        {
            if (row.getChildAt(index) instanceof LinearLayout)
            {
                words = (LinearLayout) row.getChildAt(index);
                break;
            }
        }
        if (words == null)
        {
            return;
        }
        android.widget.ProgressBar bar =
            new android.widget.ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(1000);
        bar.setProgress(usedPerThousand);
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFDCE3EE));
        bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0x33FFFFFF));
        LayoutParams size = new LayoutParams(SupernovaStyle.dp(getContext(), 310), SupernovaStyle.dp(getContext(), 3));
        size.topMargin = SupernovaStyle.dp(getContext(), 6);
        words.addView(bar, size);
        row.getLayoutParams().height = SupernovaStyle.dp(getContext(), 72);
    }

    /** Files › Connections (mock-up network-files-tabs/05): your connections, then Add a connection. */
    private void connections(LinearLayout column)
    {
        heading(column, "Your Connections", "integration", false);
        long now = System.currentTimeMillis();
        for (Connection connection : connections)
        {
            String scheme = connection.root == null ? "" : connection.root.getScheme();
            List<Action> actions = new java.util.ArrayList<>();
            if (Boolean.FALSE.equals(connection.reachable))
            {
                actions.add(new Action("Try again", "scan", () -> connectionActions.tryAgain(connection)));
            }
            else
            {
                actions.add(new Action("Open", "folder", () -> connectionActions.open(connection)));
            }
            actions.add(new Action("Edit", "formatting", () -> connectionActions.edit(connection)));
            actions.add(new Action("Remove", "discard", () -> connectionActions.remove(connection)));
            actionRow(column, ConnectionLine.kind(scheme).equals("WebDAV") ? "files" : "integration",
                      connection.name,
                      ConnectionLine.second(scheme, connection.reachable, connection.videos, connection.lastSeenMs,
                                            now),
                      // Unknown until reachability is wired: no pill rather than a "Checking…" that never ends.
                      connection.reachable == null ? null : ConnectionLine.pill(connection.reachable), actions);
        }
        row(column, "plus", "Add a connection", "Computer, NAS, WebDAV, SFTP or FTP", null,
            () -> {
                if (connectionActions != null)
                {
                    connectionActions.add();
                }
            }, connections.isEmpty());
        if (found.isEmpty())
        {
            return;
        }
        heading(column, "Found On Your Network", "search", true);
        for (Found device : found)
        {
            row(column, "integration", device.name, device.line, "Not added yet", () -> {
                if (connectionActions != null)
                {
                    connectionActions.add();
                }
            }, false);
        }
    }

    /** A button on a focused row. */
    static final class Action
    {
        final String label;
        final String icon;
        final Runnable run;

        Action(String label, String icon, Runnable run)
        {
            this.label = label;
            this.icon = icon;
            this.run = run;
        }
    }

    /**
     * A row whose buttons appear when it is focused (RULES §7; FLAT_PAGE_PRINCIPLES): focus lands on the first button,
     * Left and Right move between them, Up and Down move to the next row.
     */
    private void actionRow(LinearLayout column, String icon, String name, String line, String pill,
                           List<Action> actions)
    {
        Context context = getContext();
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        int pad = SupernovaStyle.dp(context, 20);
        row.setPadding(pad, 0, SupernovaStyle.dp(context, 10), 0);
        row.setContentDescription(pill == null ? name : name + ", " + pill);
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
        TextView status = text(pill == null ? "" : pill, 13.5f, Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setVisibility(pill == null ? INVISIBLE : VISIBLE);
        row.addView(status, new LayoutParams(SupernovaStyle.dp(context, 95), SupernovaStyle.dp(context, 22)));
        row.addView(new View(context), new LayoutParams(0, 1, 0.4f));
        List<TextView> buttons = new java.util.ArrayList<>();
        Runnable restyle = () -> {
            boolean inside = row.hasFocus();
            row.setBackground(inside ? box(SupernovaStyle.FOCUS_FILL) : null);
            int ink = inside ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            title.setTextColor(ink);
            second.setTextColor(inside ? SupernovaStyle.FOCUS_TEXT : SECOND_LINE);
            status.setTextColor(ink);
            status.setBackground(pill == null ? null : outline(ink));
            mark.setTextColor(ink);
            SupernovaStyle.inkIcons(mark, ink);
            for (TextView button : buttons)
            {
                boolean focused = button.isFocused();
                button.setVisibility(inside ? VISIBLE : GONE);
                button.setTextColor(focused ? Color.WHITE : ink);
                button.setBackground(focused ? box(SupernovaStyle.FOCUS_TEXT) : box(0x14000000));
                SupernovaStyle.tintIcons(button);
            }
        };
        for (Action action : actions)
        {
            TextView button = text(action.label, 15, Color.WHITE);
            button.setGravity(Gravity.CENTER);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            int inner = SupernovaStyle.dp(context, 12);
            button.setPadding(inner, 0, inner, 0);
            PreviewIcon.apply(button, action.icon, 14);
            button.setOnClickListener(view -> action.run.run());
            button.setOnFocusChangeListener((view, focused) -> row.post(restyle));
            LayoutParams size = new LayoutParams(-2, SupernovaStyle.dp(context, 30));
            size.leftMargin = SupernovaStyle.dp(context, 6);
            row.addView(button, size);
            buttons.add(button);
        }
        row.setOnFocusChangeListener((view, focused) -> {
            if (focused && !buttons.isEmpty())
            {
                for (TextView button : buttons)
                {
                    button.setVisibility(VISIBLE);
                }
                buttons.get(0).requestFocus();
            }
            row.post(restyle);
        });
        restyle.run();
        Object previous = column.getChildCount() == 0 ? null : column.getChildAt(column.getChildCount() - 1).getTag();
        if (previous instanceof String && ((String) previous).startsWith(ROW))
        {
            View divider = new View(context);
            divider.setBackgroundColor(LINE);
            column.addView(divider, new LayoutParams(-1, Math.max(1, SupernovaStyle.dp(context, 1))));
        }
        row.setTag(column.findViewWithTag("files.row.first") == null ? "files.row.first" : ROW);
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, ROW_HEIGHT_DP)));
    }

    private int unreadable = -1;

    // ---- Check matches (owner, 12 Nov 2026) --------------------------------------------------------------------
    private final List<com.archos.mediacenter.video.browser.adapters.object.Base> media = new java.util.ArrayList<>();
    private final List<MatchCheck.Change> changes = new java.util.ArrayList<>();
    private volatile boolean checking;
    private volatile boolean stopCheck;
    private int checkedSoFar;
    private long lastCheckMs;
    private boolean matchPage;

    /** The library the match check looks through. */
    public void setMedia(List<com.archos.mediacenter.video.browser.adapters.object.Base> latest)
    {
        media.clear();
        media.addAll(latest);
    }

    private void startCheck()
    {
        if (checking)
        {
            return;
        }
        checking = true;
        stopCheck = false;
        checkedSoFar = 0;
        changes.clear();
        List<com.archos.mediacenter.video.browser.adapters.object.Base> library = new java.util.ArrayList<>(media);
        Context app = getContext().getApplicationContext();
        new Thread(() -> {
            List<MatchCheck.Change> found = MatchCheck.run(app, library, count -> {
                if (count % 10 == 0)
                {
                    post(() -> {
                        checkedSoFar = count;
                        refreshMatches();
                    });
                }
            }, () -> stopCheck);
            post(() -> {
                checking = false;
                lastCheckMs = System.currentTimeMillis();
                changes.clear();
                changes.addAll(found);
                refreshMatches();
            });
        }, "CheckMatches").start();
    }

    private void refreshMatches()
    {
        if (tabs.openTab() == LIBRARY_CARE)
        {
            show(LIBRARY_CARE);
        }
    }

    private String matchPill()
    {
        if (checking)
        {
            return "Checking " + checkedSoFar + "…";
        }
        if (lastCheckMs <= 0)
        {
            return "Not run yet";
        }
        return "Last: " + new java.text.SimpleDateFormat("d MMM", java.util.Locale.UK).format(new java.util.Date(
            lastCheckMs));
    }

    /** The Check matches page: each change old → new with Accept · Keep current · Fix match; Accept all on top. */
    private void matchResults(LinearLayout column)
    {
        TextView crumb = text("Library Care  ›  Check matches", 15, SECOND_LINE);
        column.addView(crumb);
        if (checking)
        {
            column.addView(text("Looking every film up again… " + checkedSoFar + " checked", 16, Color.WHITE));
            actionRow(column, "cancel", "Stop checking", "Films checked so far keep their results", null,
                      java.util.Collections.singletonList(new Action("Stop", "cancel", () -> stopCheck = true)));
            return;
        }
        if (changes.isEmpty())
        {
            column.addView(text("Everything looks fine: every film still finds its own match", 16, Color.WHITE));
            return;
        }
        heading(column, changes.size() + (changes.size() == 1 ? " Suggested Change" : " Suggested Changes"),
                "health", true);
        actionRow(column, "done", "Accept all", "Use every new match below", null,
                  java.util.Collections.singletonList(new Action("Accept all", "done", () -> acceptAll())));
        for (MatchCheck.Change change : new java.util.ArrayList<>(changes))
        {
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Accept", "done", () -> accept(change)));
            actions.add(new Action("Keep current", "cancel", () -> {
                changes.remove(change);
                refreshMatches();
            }));
            actions.add(new Action("Fix match", "search", () -> getContext().startActivity(
                new Intent(getContext(), com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity
                    .class).putExtra(
                    com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity.EXTRA_VIDEO,
                    change.movie))));
            String current = change.movie.getName() == null ? "" : change.movie.getName();
            actionRow(column, "film", current + "  →  " + change.newTitle,
                      "Found from the file name: " + change.movie.getFilenameNonCryptic(), "Movie", actions);
        }
    }

    private void accept(MatchCheck.Change change)
    {
        Context app = getContext().getApplicationContext();
        new Thread(() -> {
            boolean saved = MatchCheck.accept(app, change);
            post(() -> {
                if (saved)
                {
                    changes.remove(change);
                }
                refreshMatches();
            });
        }, "AcceptMatch").start();
    }

    private void acceptAll()
    {
        for (MatchCheck.Change change : new java.util.ArrayList<>(changes))
        {
            accept(change);
        }
    }

    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event)
    {
        if (matchPage && event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK)
        {
            if (event.getAction() == android.view.KeyEvent.ACTION_UP)
            {
                matchPage = false;
                refreshMatches();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /** Files Supernova could not open (MediaLib's read evidence); -1 while it is being counted. */
    public void setUnreadable(int count)
    {
        unreadable = count;
        show(tabs.openTab());
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
        row.setContentDescription(pill == null ? name : name + ", " + pill);
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
        TextView status = text(pill == null ? "" : pill, 13.5f, Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setBackground(outline(Color.WHITE));
        // A row without a status keeps the column's space, so names and chevrons stay lined up.
        status.setVisibility(pill == null ? INVISIBLE : VISIBLE);
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
            status.setBackground(pill == null ? null : outline(ink));
            chevron.setTextColor(ink);
            mark.setTextColor(ink);
            SupernovaStyle.inkIcons(mark, ink);
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
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, ROW_HEIGHT_DP)));
    }

    private static final String ROW = "files.row";
    /** Row height (mock-up network-files-tabs/39: 96px at 1080p). */
    private static final int ROW_HEIGHT_DP = 48;

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
