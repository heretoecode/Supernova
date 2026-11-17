package com.archos.mediacenter.video.leanback.files;

import com.archos.mediacenter.video.leanback.TvLayout;
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
    public static final String[] TABS = {"This Device", "Connections", "Library Care", "Your Library"};
    static final String[] TAB_ICONS = {"tv", "integration", "health", "chart"};
    public static final int LIBRARY_CARE = 2;
    public static final int YOUR_LIBRARY = 3;
    private LibraryStats stats;

    static final int SECOND_LINE = 0xFFC9D3E6;
    static final int LINE = 0x14FFFFFF;

    // Side margin: TvLayout.sideMargin, the top bar's edges (owner, 18 Nov 2026). Files fills the page itself; the
    // margin is inside it, so a grown card or ring may reach into it.

    private final PageTabs tabs;
    private final LinearLayout top;
    private final LinearLayout summary;
    private final FrameLayout body;
    /** Holds focus for a moment while a tab's content is rebuilt, so it never jumps to the top bar (F9). */
    private final View holder;
    /** What was last focused in the content, so a return (from the player, from another page) lands there (F11). */
    private View lastFocused;
    private LibraryCareCounts counts;
    private int videos;
    private long lastScanMs;
    private boolean scanning;

    public FilesPage(Context context)
    {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(0xFF16181C);
        setClipChildren(false);
        setClipToPadding(false);
        // Content lines up with the top bar's edges (one side margin, owner 18 Nov 2026; RULES §1).
        int side = TvLayout.sideMargin(context);
        setPadding(0, SupernovaStyle.dp(context, 8), 0, 0);
        top = new LinearLayout(context);
        top.setPadding(side, 0, side, 0);
        top.setClipChildren(false);
        top.setClipToPadding(false);
        top.setGravity(Gravity.CENTER_VERTICAL);
        tabs = new PageTabs(context, TABS, TAB_ICONS, this::show);
        top.addView(tabs, new LayoutParams(-2, -2));
        top.addView(new View(context), new LayoutParams(0, 1, 1));
        summary = new LinearLayout(context);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(summary, new LayoutParams(-2, -2));
        holder = new View(context);
        holder.setFocusable(false);
        top.addView(holder, new LayoutParams(1, 1));
        addView(top, new LayoutParams(-1, -2));
        View line = new View(context);
        line.setBackgroundColor(LINE);
        LayoutParams lineSize = new LayoutParams(-1, Math.max(1, SupernovaStyle.dp(context, 1)));
        lineSize.leftMargin = side;
        lineSize.rightMargin = side;
        addView(line, lineSize);
        body = new FrameLayout(context);
        body.setClipChildren(false);
        body.setClipToPadding(false);
        addView(body, new LayoutParams(-1, 0, 1));
        tabs.setDown(this::focusContent);
        tabs.setReselected(index -> {
            browsers.remove(index);
            browser = null;
            if (index == LIBRARY_CARE)
            {
                carePage = null;
            }
            if (index == CONNECTIONS)
            {
                form = null;
            }
            show(index);
        });
        getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> {
            tabs.refresh();
            if (after != null && after != holder && isInside(body, after))
            {
                lastFocused = after;
            }
        });
        // Files opens on This Device every time (F2).
        tabs.select(THIS_DEVICE);
    }

    /** Opening Files from the top bar: This Device, at its start (F2, F12). */
    public void reset()
    {
        browsers.clear();
        browser = null;
        carePage = null;
        form = null;
        notice = null;
        undo = null;
        lastFocused = null;
        tabs.select(THIS_DEVICE);
    }

    /** True when the remote is in the tab row, so Up goes on to the top bar (F1). */
    public boolean atTop()
    {
        View focus = findFocus();
        return focus == null || isInside(top, focus);
    }

    /**
     * Back from anywhere on the page (also from the top bar): up one folder, or out of a page inside a tab.
     * False when there is nothing to go back from.
     */
    public boolean back()
    {
        if (browser != null)
        {
            if (!browser.up())
            {
                browsers.remove(tabs.openTab());
                browser = null;
                show(tabs.openTab());
            }
            focusContent();
            return true;
        }
        if (form != null && tabs.openTab() == CONNECTIONS)
        {
            if (!form.back())
            {
                form = null;
                show(CONNECTIONS);
                focusContent();
            }
            return true;
        }
        if (carePage != null && tabs.openTab() == LIBRARY_CARE)
        {
            carePage = null;
            show(LIBRARY_CARE);
            focusContent();
            return true;
        }
        return false;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, android.graphics.Rect previouslyFocusedRect)
    {
        // Coming back (from the player, Details or the top bar): to what was focused, else the first row.
        if (lastFocused != null && lastFocused.isShown() && isInside(body, lastFocused) && lastFocused.requestFocus())
        {
            return true;
        }
        View first = body.findViewWithTag("files.row.first");
        if (first == null)
        {
            first = body.findViewWithTag("files.folder.card");
        }
        if (first != null && first.isShown() && first.requestFocus())
        {
            return true;
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    public static boolean isInside(View parent, View child)
    {
        for (View view = child; view != null; view = view.getParent() instanceof View ? (View) view.getParent() : null)
        {
            if (view == parent)
            {
                return true;
            }
        }
        return false;
    }

    /** Parks focus while the content is rebuilt; true when it was in the content. */
    private boolean park()
    {
        View focus = findFocus();
        if (focus == null || !isInside(body, focus))
        {
            return false;
        }
        holder.setFocusable(true);
        holder.setFocusableInTouchMode(true);
        holder.requestFocus();
        return true;
    }

    private void unpark(boolean parked, String key)
    {
        if (!parked)
        {
            return;
        }
        View again = key == null ? null : findByKey(body, key);
        if (again != null && again.requestFocus())
        {
            holder.setFocusable(false);
            return;
        }
        focusContent();
        holder.setFocusable(false);
        if (holder.isFocused())
        {
            tabs.tab(tabs.openTab()).requestFocus();
        }
    }

    /** What identifies a focused view across a rebuild: its spoken name, else its words. */
    private static String keyOf(View view)
    {
        if (view == null)
        {
            return null;
        }
        if (view.getContentDescription() != null)
        {
            return view.getContentDescription().toString();
        }
        return view instanceof TextView ? ((TextView) view).getText().toString() : null;
    }

    private static View findByKey(View view, String key)
    {
        if (view.isFocusable() && key.equals(keyOf(view)))
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++)
            {
                View match = findByKey(group.getChildAt(index), key);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
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
        /** The login's user name ("alex"), shown after the name (F38); empty when there is none. */
        public final String account;

        public Connection(String name, android.net.Uri root, Boolean reachable, long videos, long lastSeenMs)
        {
            this(name, root, reachable, videos, lastSeenMs, "");
        }

        public Connection(String name, android.net.Uri root, Boolean reachable, long videos, long lastSeenMs,
                          String account)
        {
            this.account = account == null ? "" : account;
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
        tabs.setBadge(LIBRARY_CARE, toCheck());
        nextWaiting();
        show(tabs.openTab());
    }

    private void show(int tab)
    {
        View focus = findFocus();
        String key = focus != null && isInside(body, focus) ? keyOf(focus) : null;
        boolean parked = park();
        body.removeAllViews();
        showSummary();
        browser = browsers.get(tab);
        if (browser == null && tab == CONNECTIONS && form != null)
        {
            body.addView(form, new FrameLayout.LayoutParams(-1, -1));
            unpark(parked, key);
            return;
        }
        if (browser != null)
        {
            body.addView(browser, new FrameLayout.LayoutParams(-1, -1));
            unpark(parked, key);
            return;
        }
        ScrollView scroll = new ScrollView(getContext());
        scroll.setClipToPadding(false);
        scroll.setClipChildren(false);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout column = new LinearLayout(getContext());
        column.setOrientation(VERTICAL);
        column.setClipChildren(false);
        int side = TvLayout.sideMargin(getContext());
        column.setPadding(side, SupernovaStyle.dp(getContext(), 24), side, SupernovaStyle.dp(getContext(), 27));
        scroll.addView(column);
        body.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        body.addView(new ScrollArrows(getContext(), scroll, side), new FrameLayout.LayoutParams(-1, -1));
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
        else if (tab == YOUR_LIBRARY)
        {
            yourLibrary(column);
        }
        unpark(parked, key);
    }

    private FolderBrowser browser;
    /** The folder each tab is browsing, kept while moving along the tabs (F11); OK on the tab clears it (F12). */
    private final java.util.Map<Integer, FolderBrowser> browsers = new java.util.HashMap<>();

    /** Opens a storage in the folder grid, under the tabs (mock-up network-files-tabs/32). */
    public void browse(android.net.Uri root, String name, FolderBrowser.Opener opener)
    {
        browse(root, name, opener, folderActions);
    }

    private FolderBrowser.Actions folderActions;

    /** What Details and Fix match in a file's menu open (supplied by the hosting page). */
    public void setFolderActions(FolderBrowser.Actions actions)
    {
        folderActions = actions;
    }

    public void browse(android.net.Uri root, String name, FolderBrowser.Opener opener, FolderBrowser.Actions actions)
    {
        int tab = tabs.openTab();
        boolean parked = park();
        browser = new FolderBrowser(getContext(), opener, () -> {
            browsers.remove(tab);
            browser = null;
            show(tabs.openTab());
        });
        browsers.put(tab, browser);
        browser.setActions(actions);
        browser.setLibrary(media);
        body.removeAllViews();
        body.addView(browser, new FrameLayout.LayoutParams(-1, -1));
        browser.start(root, name);
        browser.requestFocus();
        holder.setFocusable(false);
        if (parked && !browser.hasFocus())
        {
            browser.requestFocus();
        }
    }

    /** The library's figures for Files › Your Library. */
    public void setStats(LibraryStats latest)
    {
        stats = latest;
        if (tabs.openTab() == YOUR_LIBRARY)
        {
            show(YOUR_LIBRARY);
        }
    }

    /**
     * Files › Your Library (approved 13 Nov 2026, mock-up network-files-tabs/44): Total Library, By Library Page and
     * Most Space Per Episode, each row a name, a second line and its figures on the right.
     */
    private void yourLibrary(LinearLayout column)
    {
        LibraryStats s = stats == null ? LibraryStats.of(new java.util.ArrayList<>(), 0) : stats;
        heading(column, "Total Library", "chart", false);
        statRow(column, "list", s.all.name,
                joinDot(count(s.all.titles, "title"), count(s.all.videos, "video"), size(s.all.bytes())),
                places(s.all), true);
        statRow(column, "check", "Watched", s.watchedTitles + " of " + count(s.all.titles, "title"),
                s.percent(s.watchedTitles) + "%", false);
        statRow(column, "time", "Time to watch everything", "Every unwatched title and episode",
                String.format(java.util.Locale.UK, "%,d hours", s.hoursToWatch), false);
        // Every quality (F21): 4K, 1080p, 720p, SD and HDR; counts of videos, then their share.
        statRow(column, "hdr", "Picture", s.pictureCounts() + " videos", s.picturePercents(), false);
        java.text.DateFormat month = new java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.UK);
        statRow(column, "folder", "Added this month", "Since " + month.format(monthStart()),
                count(s.addedThisMonth, "title"), false);
        heading(column, "By Library Page", "film", true);
        for (LibraryStats.Page page : new LibraryStats.Page[] {s.movies, s.shows})
        {
            String line = page == s.shows
                ? joinDot(count(page.titles, "title"), count(page.videos, "episode"), size(page.bytes()))
                : joinDot(count(page.titles, "title"), size(page.bytes()));
            statRow(column, page == s.shows ? "tv" : "film", page.name, line, places(page), false);
        }
        if (!s.heaviest.isEmpty())
        {
            heading(column, "Most Space Per Episode", "sort", true);
            for (LibraryStats.Heavy heavy : s.heaviest)
            {
                statRow(column, "tv", heavy.show,
                        joinDot(count(heavy.episodes, "episode"), size(heavy.bytes)),
                        size(heavy.perEpisode()) + " per episode", false);
            }
        }
    }

    private static String joinDot(String... parts)
    {
        return TextUtils.join(" · ", parts);
    }

    private static String count(long value, String noun)
    {
        return String.format(java.util.Locale.UK, "%,d", value) + " " + noun + (value == 1 ? "" : "s");
    }

    private String size(long bytes)
    {
        return android.text.format.Formatter.formatShortFileSize(getContext(), bytes);
    }

    /** On this device and on the network, the two sizes in a mock-up line. */
    private String places(LibraryStats.Page page)
    {
        return "This device " + size(page.localBytes) + " · Network " + size(page.networkBytes);
    }

    public static java.util.Date monthStart()
    {
        java.util.Calendar start = java.util.Calendar.getInstance();
        start.set(java.util.Calendar.DAY_OF_MONTH, 1);
        start.set(java.util.Calendar.HOUR_OF_DAY, 0);
        start.set(java.util.Calendar.MINUTE, 0);
        start.set(java.util.Calendar.SECOND, 0);
        start.set(java.util.Calendar.MILLISECOND, 0);
        return start.getTime();
    }

    /** A row of figures: name and second line on the left, the figures in white on the right. */
    private void statRow(LinearLayout column, String icon, String name, String line, String value, boolean first)
    {
        Context context = getContext();
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        int pad = SupernovaStyle.dp(context, 20);
        row.setPadding(pad, 0, pad, 0);
        row.setContentDescription(name + ", " + value);
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
        TextView figure = text(value, 15, Color.WHITE);
        figure.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        row.addView(figure, new LayoutParams(-2, -2));
        row.setOnFocusChangeListener((view, focused) -> {
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            title.setTextColor(ink);
            second.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : SECOND_LINE);
            figure.setTextColor(ink);
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

    /**
     * The tab row's right end (F18, approved 17 Nov 2026): only "⟳ Scan library" (OK scans every source), or
     * "Scanning · 42%" while a scan runs (OK stops). No facts: the badge, Library Health and Your Library say them.
     */
    private void showSummary()
    {
        summary.removeAllViews();
        TextView control = text(scanLabel(scanning, scanPercent()), 18, Color.WHITE);
        PreviewIcon.apply(control, "scan", 16);
        summary.addView(control);
        scanControl(control);
    }

    /** "Scan library", or "Scanning · 42%" (just "Scanning" until the size is known). Pure, for tests. */
    public static String scanLabel(boolean running, int percent)
    {
        if (!running)
        {
            return "Scan library";
        }
        return percent >= 0 ? "Scanning · " + percent + "%" : "Scanning";
    }

    private static int scanPercent()
    {
        try
        {
            ScanProgress progress = com.archos.mediacenter.video.leanback.PreviewLibraryScan.progress();
            if (progress == null || progress.total <= 0)
            {
                return -1;
            }
            return (int) Math.max(0, Math.min(100, progress.checked * 100 / progress.total));
        }
        catch (RuntimeException unavailable)
        {
            return -1;
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

    // ---- Library Care (page sheet FILES.md; F43–F51) ------------------------------------------------------------

    /** A page inside Library Care: null for the tab itself, else missing / unreadable / unmatched / review / scans. */
    private String carePage;
    private final java.util.Map<String, String> outcomes = new java.util.HashMap<>();
    private final java.util.Set<String> waiting = new java.util.LinkedHashSet<>();
    private final List<String[]> unreadableRows = new java.util.ArrayList<>();

    private void libraryCare(LinearLayout column)
    {
        if (carePage != null)
        {
            carePage(column);
            return;
        }
        heading(column, "Library Health", "health", false);
        boolean ready = counts != null;
        int missing = ready ? counts.missing : -1;
        int unmatched = ready ? notMatched().size() : -1;
        row(column, "folder", "Missing files", "In your library but no longer found",
            ready ? LibraryCareCounts.pill(missing, "file", "files") : "Loading…",
            missing > 0 ? () -> openCare("missing") : null, true);
        row(column, "film", "Couldn't be read", "Files Supernova could not open",
            unreadable < 0 ? "Loading…" : LibraryCareCounts.pill(unreadable, "file", "files"),
            unreadable > 0 ? () -> openCare("unreadable") : null, false);
        row(column, "search", "Not matched", "Videos without a movie or TV show",
            ready ? LibraryCareCounts.pill(unmatched, "video", "videos") : "Loading…",
            unmatched > 0 ? () -> openCare("unmatched") : null, false);
        // Check matches runs in its row (F44): OK starts it; only with changes does OK open the review page.
        boolean review = !checking && lastCheckMs > 0 && !changes.isEmpty();
        row(column, "done", "Check matches", "Look every movie up again; you see the changes first", matchPill(),
            () -> {
                if (review)
                {
                    openCare("review");
                }
                else if (checking)
                {
                    stopCheck = true;
                }
                else
                {
                    startCheck();
                    refreshMatches();
                }
            }, false);
        View check = column.getChildAt(column.getChildCount() - 1);
        if (!review)
        {
            hideChevron(check);
        }
        scanOneSource(column);
        recentScans(column);
    }

    private static void hideChevron(View row)
    {
        if (row instanceof LinearLayout && ((LinearLayout) row).getChildCount() > 0)
        {
            View last = ((LinearLayout) row).getChildAt(((LinearLayout) row).getChildCount() - 1);
            last.setVisibility(INVISIBLE);
        }
    }

    private void openCare(String page)
    {
        carePage = page;
        if ("unreadable".equals(page))
        {
            loadUnreadable();
        }
        show(LIBRARY_CARE);
        focusContent();
    }

    /** The library's unmatched videos, leaving out the ones marked "Not a movie". */
    private List<com.archos.mediacenter.video.browser.adapters.object.Video> notMatched()
    {
        java.util.Set<String> skipped = getContext().getSharedPreferences(CARE, Context.MODE_PRIVATE)
            .getStringSet("not_a_movie", java.util.Collections.emptySet());
        List<com.archos.mediacenter.video.browser.adapters.object.Video> found = new java.util.ArrayList<>();
        for (com.archos.mediacenter.video.browser.adapters.object.Base item : media)
        {
            if (!(item instanceof com.archos.mediacenter.video.browser.adapters.object.Video)
                || item instanceof com.archos.mediacenter.video.browser.adapters.object.Movie
                || item instanceof com.archos.mediacenter.video.browser.adapters.object.Episode)
            {
                continue;
            }
            com.archos.mediacenter.video.browser.adapters.object.Video video =
                (com.archos.mediacenter.video.browser.adapters.object.Video) item;
            if (!skipped.contains(String.valueOf(video.getId())))
            {
                found.add(video);
            }
        }
        return found;
    }

    private static final String CARE = "files_library_care";

    private void carePage(LinearLayout column)
    {
        String[] titles = {"missing", "Missing files", "unreadable", "Couldn't be read", "unmatched", "Not matched",
                           "review", "Check matches", "scans", "All scans"};
        String title = carePage;
        for (int index = 0; index < titles.length; index += 2)
        {
            if (titles[index].equals(carePage))
            {
                title = titles[index + 1];
            }
        }
        // The path is a signpost, not focusable; Back returns to Library Care.
        TextView crumb = text("Library Care  ›  " + title, 15, SECOND_LINE);
        LayoutParams crumbSize = new LayoutParams(-2, -2);
        crumbSize.bottomMargin = SupernovaStyle.dp(getContext(), 12);
        column.addView(crumb, crumbSize);
        switch (carePage)
        {
            case "missing":
                missingPage(column);
                break;
            case "unreadable":
                unreadablePage(column);
                break;
            case "unmatched":
                unmatchedPage(column);
                break;
            case "review":
                matchResults(column);
                break;
            default:
                allScans(column);
                break;
        }
    }

    private void missingPage(LinearLayout column)
    {
        com.archos.mediacenter.video.health.MissingFilesRepository.Inventory inventory;
        try
        {
            inventory = com.archos.mediacenter.video.health.MissingFilesRepository.current();
        }
        catch (RuntimeException unavailable)
        {
            inventory = null;
        }
        if (inventory == null || inventory.missing.isEmpty())
        {
            column.addView(text("Nothing is missing: every file was found", 16, Color.WHITE));
            return;
        }
        for (com.archos.mediacenter.video.health.MissingFilesRepository.MissingFile file : inventory.missing)
        {
            String key = "missing:" + file.id;
            String where = (file.sourceName == null || file.sourceName.isEmpty() ? "Unknown source" : file.sourceName)
                + (file.folder == null || file.folder.isEmpty() ? "" : " › " + file.folder);
            String name = file.title == null || file.title.isEmpty() ? file.oldFileName : file.title;
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Find it again", "search", () -> missingWork(file, key, true)));
            actions.add(new Action("Remove", "discard", () -> missingWork(file, key, false)));
            actionRow(column, "folder", name, outcomes.containsKey(key) ? outcomes.get(key) : where, null, actions,
                      false);
        }
    }

    private void missingWork(com.archos.mediacenter.video.health.MissingFilesRepository.MissingFile file, String key,
                             boolean find)
    {
        outcomes.put(key, find ? "Looking for the file…" : "Removing…");
        show(LIBRARY_CARE);
        Context app = getContext().getApplicationContext();
        new Thread(() -> {
            String outcome;
            try
            {
                if (find)
                {
                    outcome = com.archos.mediacenter.video.health.MissingFilesRepository.findAgain(app, file, () -> false)
                        ? "Found it: it plays from the same place again"
                        : "Couldn't find exactly one file with that name and size; nothing changed";
                }
                else
                {
                    outcome = com.archos.mediacenter.video.health.MissingFilesRepository.remove(app, file)
                        ? "Removed from your library; no file was deleted"
                        : "This entry had already changed; nothing was removed";
                }
            }
            catch (Exception failed)
            {
                outcome = find ? "The search didn't finish; nothing changed" : "Removing didn't finish";
            }
            String message = outcome;
            post(() -> {
                outcomes.put(key, message);
                show(LIBRARY_CARE);
            });
        }, "FilesMissing").start();
    }

    /** Couldn't be read: the files MediaLib recorded, read on a worker thread when the page opens. */
    private void loadUnreadable()
    {
        Context app = getContext().getApplicationContext();
        new Thread(() -> {
            List<String[]> rows = new java.util.ArrayList<>();
            try (com.archos.mediaprovider.video.LibraryHealthEvidence store =
                     new com.archos.mediaprovider.video.LibraryHealthEvidence(app);
                 android.database.Cursor cursor = store.getReadableDatabase().rawQuery(
                     "SELECT * FROM evidence WHERE kind=?", new String[] {"COULD_NOT_READ"}))
            {
                int column = -1;
                for (String name : new String[] {"uri", "path", "file", "location", "source"})
                {
                    if (column < 0)
                    {
                        column = cursor.getColumnIndex(name);
                    }
                }
                while (cursor.moveToNext() && rows.size() < 200)
                {
                    String where = column < 0 ? "" : cursor.getString(column);
                    rows.add(new String[] {where == null ? "" : where, column < 0 ? "" : cursor.getColumnName(column)});
                }
            }
            catch (RuntimeException unavailable)
            {
                // Shown as none.
            }
            post(() -> {
                unreadableRows.clear();
                unreadableRows.addAll(rows);
                if (LIBRARY_CARE == tabs.openTab() && "unreadable".equals(carePage))
                {
                    show(LIBRARY_CARE);
                }
            });
        }, "FilesUnreadableRows").start();
    }

    private void unreadablePage(LinearLayout column)
    {
        if (unreadableRows.isEmpty())
        {
            column.addView(text(unreadable > 0 ? "Looking…" : "Every file could be read", 16, Color.WHITE));
            return;
        }
        for (String[] row : unreadableRows)
        {
            android.net.Uri uri = android.net.Uri.parse(row[0]);
            String name = uri.getLastPathSegment() == null ? row[0] : uri.getLastPathSegment();
            String key = "unreadable:" + row[0];
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Try again", "scan", () -> {
                android.net.Uri folder = parent(uri);
                if (folder != null)
                {
                    com.archos.mediaprovider.NetworkScanner.scanVideos(getContext(), folder);
                }
                outcomes.put(key, "Trying again with the next scan");
                show(LIBRARY_CARE);
            }));
            actions.add(new Action("Exclude", "remove", () -> {
                boolean done = LibraryChanges.apply(getContext(), selection -> selection.exclude(uri));
                outcomes.put(key, done ? "Excluded: the library leaves it out" : "Couldn't exclude it just now");
                show(LIBRARY_CARE);
            }));
            String folder = parent(uri) == null ? "" : parent(uri).getPath();
            actionRow(column, "film", name, outcomes.containsKey(key) ? outcomes.get(key) : folder, null, actions,
                      false);
        }
    }

    private static android.net.Uri parent(android.net.Uri uri)
    {
        String path = uri.getPath();
        if (path == null || path.lastIndexOf('/') <= 0)
        {
            return null;
        }
        return uri.buildUpon().path(path.substring(0, path.lastIndexOf('/'))).build();
    }

    private void unmatchedPage(LinearLayout column)
    {
        List<com.archos.mediacenter.video.browser.adapters.object.Video> videos = notMatched();
        if (videos.isEmpty())
        {
            column.addView(text("Every video has its movie or TV show", 16, Color.WHITE));
            return;
        }
        for (com.archos.mediacenter.video.browser.adapters.object.Video video : videos)
        {
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Fix match", "search", () -> {
                if (folderActions != null)
                {
                    folderActions.fixMatch(video);
                }
            }));
            actions.add(new Action("Not a movie", "cancel", () -> {
                android.content.SharedPreferences care =
                    getContext().getSharedPreferences(CARE, Context.MODE_PRIVATE);
                java.util.Set<String> skipped = new java.util.HashSet<>(
                    care.getStringSet("not_a_movie", java.util.Collections.emptySet()));
                skipped.add(String.valueOf(video.getId()));
                care.edit().putStringSet("not_a_movie", skipped).apply();
                tabs.setBadge(LIBRARY_CARE, toCheck());
                show(LIBRARY_CARE);
            }));
            String name = video.getName() == null ? video.getFilenameNonCryptic() : video.getName();
            actionRow(column, "film", name, video.getFilenameNonCryptic(), null, actions, false);
        }
    }

    private int toCheck()
    {
        return counts == null ? 0 : counts.missing + notMatched().size();
    }

    /**
     * Scan one source (F48, F51): nothing shown at rest; focused, one button per source. Pressed sources wait their
     * turn ("Waiting") while another scan runs; one scan at a time.
     */
    private void scanOneSource(LinearLayout column)
    {
        heading(column, "Scanning", "scan", true);
        if (sources.isEmpty())
        {
            return;
        }
        List<Action> actions = new java.util.ArrayList<>();
        for (Source source : sources)
        {
            String label = waiting.contains(source.label) ? source.label + " · Waiting" : source.label;
            actions.add(new Action(label, source.local ? "tv" : "integration", () -> scanSource(source)));
        }
        String second = waiting.isEmpty() ? "Look for new videos in one place only"
            : TextUtils.join(", ", waiting) + (waiting.size() == 1 ? " is" : " are") + " waiting for its turn";
        actionRow(column, "integration", "Scan one source", second, null, actions, false);
    }

    private void scanSource(Source source)
    {
        if (scanning || !waiting.isEmpty())
        {
            waiting.add(source.label);
            show(LIBRARY_CARE);
            return;
        }
        scanOne.accept(source);
    }

    /** When a scan ends, the next waiting source starts. */
    private void nextWaiting()
    {
        if (scanning || waiting.isEmpty())
        {
            return;
        }
        String next = waiting.iterator().next();
        waiting.remove(next);
        for (Source source : sources)
        {
            if (source.label.equals(next))
            {
                scanOne.accept(source);
                return;
            }
        }
    }

    /** Recent scans (F50): the last three as plain rows, then Show all scans for the last ten. */
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
            scanLine(column, scans.get(index), now);
        }
        if (scans.size() > 3)
        {
            row(column, "history", "Show all scans", "The last ten scans", null, () -> openCare("scans"), false);
        }
    }

    private void allScans(LinearLayout column)
    {
        List<ScanProgress> scans = LibraryScanController.recentScans();
        long now = System.currentTimeMillis();
        for (int index = 0; index < Math.min(10, scans.size()); index++)
        {
            scanLine(column, scans.get(index), now);
        }
        if (scans.isEmpty())
        {
            column.addView(text("No scans yet", 16, Color.WHITE));
        }
    }

    /** One scan as plain information: no focus, no pill, no line (RULES §5). */
    private void scanLine(LinearLayout column, ScanProgress scan, long now)
    {
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
        words.addView(text(name + " · " + RecentScanLine.pill(scan.outcome()), 16, Color.WHITE));
        words.addView(text(RecentScanLine.second(scan.outcome(), scan.failureReason(), scan.startedUtcMs, now,
                                                 scan.checked, scan.added, scan.removed), 13.5f, SECOND_LINE));
        line.addView(words, new LayoutParams(0, -2, 1));
        column.addView(line, new LayoutParams(-1, -2));
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

    /**
     * Files › Connections (page sheet FILES.md; mock-up files-review-0157-1/04, 1): your connections ("Put.io ·
     * alex", "WebDAV · 1,204 videos", Reachable), Add a connection, then devices found on the network.
     */
    private void connections(LinearLayout column)
    {
        heading(column, "Your Connections", "integration", false);
        if (notice != null)
        {
            List<Action> undoing = new java.util.ArrayList<>();
            undoing.add(new Action("Undo", "history", () -> {
                Runnable again = undo;
                notice = null;
                undo = null;
                if (again != null)
                {
                    again.run();
                }
                show(CONNECTIONS);
                focusContent();
            }));
            actionRow(column, "discard", notice, "Its videos leave the library; no file was deleted", null, undoing,
                      false);
        }
        for (Connection connection : new java.util.ArrayList<>(connections))
        {
            String scheme = connection.root == null ? "" : connection.root.getScheme();
            String key = connection.root == null ? connection.name : connection.root.toString();
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Open", "folder", () -> openConnection(connection.root, connection.name)));
            actions.add(new Action("Edit", "edit", () -> openForm(connection.root, connection.name)));
            actions.add(new Action("Test", "done", () -> testConnection(connection.root, key)));
            actions.add(new Action("Remove", "discard", () -> removeConnection(connection)));
            String title = connection.account == null || connection.account.isEmpty()
                ? connection.name : connection.name + " · " + connection.account;
            String second = outcomes.containsKey(key) ? outcomes.get(key)
                : ConnectionLine.kind(scheme) + " · " + count(connection.videos, "video");
            actionRow(column, ConnectionLine.kind(scheme).equals("WebDAV") ? "cloud" : "integration", title, second,
                      connection.reachable == null ? null : ConnectionLine.pill(connection.reachable), actions, true);
        }
        // Add a connection: the row itself opens the form (F39).
        row(column, "plus", "Add a connection", "SMB, WebDAV, SFTP or FTP", null, () -> openForm(null, null),
            column.findViewWithTag("files.row.first") == null);
        if (found.isEmpty())
        {
            return;
        }
        heading(column, "Found On Your Network", "search", true);
        for (Found device : found)
        {
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Open", "folder", () -> openConnection(device.uri, device.name)));
            actions.add(new Action("Add", "plus", () -> openFoundForm(device.uri)));
            actionRow(column, "tv", device.name, device.line, "New", actions, true);
        }
    }

    private String notice;
    private Runnable undo;
    private ConnectionFormPage form;
    private FolderBrowser.Opener player = (file, item) -> { };

    /** What OK on a file does when browsing (plays it); supplied by the hosting page. */
    public void setPlayer(FolderBrowser.Opener opener)
    {
        if (opener != null)
        {
            player = opener;
        }
    }

    /** Open (or OK) on a connection or a found device: browse it like a drive (F33, F40). */
    private void openConnection(android.net.Uri root, String name)
    {
        if (root == null)
        {
            return;
        }
        browse(root, name, player, folderActions);
    }

    private void openForm(android.net.Uri editing, String name)
    {
        form = new ConnectionFormPage(getContext(), this::connectionSaved);
        if (editing != null)
        {
            form.startEdit(editing, name);
        }
        else
        {
            form.startNew(null);
        }
        showForm();
    }

    private void openFoundForm(android.net.Uri address)
    {
        form = new ConnectionFormPage(getContext(), this::connectionSaved);
        form.startNew(address);
        showForm();
    }

    private void showForm()
    {
        show(CONNECTIONS);
        View first = form.findViewWithTag("files.form.field:Name");
        if (first != null)
        {
            first.requestFocus();
        }
    }

    /** Save and browse: the connection joins Your connections and opens (a found device leaves that list). */
    private void connectionSaved(android.net.Uri uri, String name)
    {
        form = null;
        Connection added = new Connection(name, uri, null, 0, 0);
        boolean known = false;
        for (Connection connection : connections)
        {
            known |= uri.equals(connection.root);
        }
        if (!known)
        {
            connections.add(added);
        }
        found.removeIf(device -> device.uri != null && uri.getHost() != null
            && uri.getHost().equalsIgnoreCase(device.uri.getHost()));
        openConnection(uri, name);
    }

    private void testConnection(android.net.Uri root, String key)
    {
        if (root == null)
        {
            return;
        }
        outcomes.put(key, "Testing…");
        show(CONNECTIONS);
        ConnectionCheck.check(getContext(), java.util.Collections.singletonList(root), (checked, answered) -> post(() -> {
            outcomes.put(key, answered ? "Connected" : "Not reachable: no answer within 15 seconds");
            show(CONNECTIONS);
        }));
    }

    /** Remove: the row goes at once, with Undo (F31; mock-up 24). */
    private void removeConnection(Connection connection)
    {
        if (connection.root == null)
        {
            return;
        }
        if (!LibraryChanges.apply(getContext(), selection -> selection.remove(connection.root)))
        {
            outcomes.put(connection.root.toString(), "Couldn't remove it just now");
            show(CONNECTIONS);
            return;
        }
        int at = connections.indexOf(connection);
        connections.remove(connection);
        notice = connection.name + " removed";
        undo = () -> {
            if (LibraryChanges.apply(getContext(), selection -> selection.include(connection.root)))
            {
                connections.add(Math.max(0, Math.min(at, connections.size())), connection);
            }
        };
        show(CONNECTIONS);
        focusContent();
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

    /** Names take a fixed width, so every pill sits in one column, focused or not (F29, F43; RULES §7). */
    private static final int WORDS_DP = 330;
    private static final int PILL_DP = 140;

    /**
     * A row whose buttons appear when it is focused (RULES §7): focus lands on the first button, Left and Right move
     * between them, Up and Down move to the next row. The pill keeps its column; › shows at rest when OK opens a
     * page. Black ink on the white box, icons included (F13, F30, F47).
     */
    private void actionRow(LinearLayout column, String icon, String name, String line, String pill,
                           List<Action> actions, boolean opens)
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
        boolean wide = pill == null && !opens;
        row.addView(words, wide ? new LayoutParams(0, -2, 1)
            : new LayoutParams(SupernovaStyle.dp(context, WORDS_DP), -2));
        TextView status = text(pill == null ? "" : pill, 13.5f, Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setVisibility(pill == null ? (wide ? GONE : INVISIBLE) : VISIBLE);
        row.addView(status, new LayoutParams(SupernovaStyle.dp(context, PILL_DP), SupernovaStyle.dp(context, 24)));
        if (!wide)
        {
            row.addView(new View(context), new LayoutParams(0, 1, 1));
        }
        List<TextView> buttons = new java.util.ArrayList<>();
        TextView chevron = text(opens ? "›" : "", 20, Color.WHITE);
        chevron.setGravity(Gravity.CENTER);
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
            chevron.setVisibility(inside || !opens ? GONE : VISIBLE);
            for (TextView button : buttons)
            {
                boolean focused = button.isFocused();
                button.setVisibility(inside ? VISIBLE : GONE);
                int buttonInk = focused ? Color.WHITE : SupernovaStyle.FOCUS_TEXT;
                button.setTextColor(buttonInk);
                button.setBackground(focused ? box(SupernovaStyle.FOCUS_TEXT) : box(0x1F000000));
                SupernovaStyle.inkIcons(button, buttonInk);
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
            button.setContentDescription(action.label + ", " + name);
            button.setOnClickListener(view -> action.run.run());
            button.setOnFocusChangeListener((view, focused) -> row.post(restyle));
            LayoutParams size = new LayoutParams(-2, SupernovaStyle.dp(context, 30));
            size.leftMargin = SupernovaStyle.dp(context, 6);
            row.addView(button, size);
            buttons.add(button);
        }
        row.addView(chevron, new LayoutParams(SupernovaStyle.dp(context, 24), -2));
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
        divider(column);
        row.setTag(column.findViewWithTag("files.row.first") == null ? "files.row.first" : ROW);
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, ROW_HEIGHT_DP)));
    }

    private void divider(LinearLayout column)
    {
        Object previous = column.getChildCount() == 0 ? null : column.getChildAt(column.getChildCount() - 1).getTag();
        if (previous instanceof String && ((String) previous).startsWith(ROW))
        {
            View divider = new View(getContext());
            divider.setBackgroundColor(LINE);
            column.addView(divider, new LayoutParams(-1, Math.max(1, SupernovaStyle.dp(getContext(), 1))));
        }
    }

    private int unreadable = -1;

    // ---- Check matches (owner, 12 Nov 2026) --------------------------------------------------------------------
    private final List<com.archos.mediacenter.video.browser.adapters.object.Base> media = new java.util.ArrayList<>();
    private final List<MatchCheck.Change> changes = new java.util.ArrayList<>();
    private volatile boolean checking;
    private volatile boolean stopCheck;
    private int checkedSoFar;
    private long lastCheckMs;

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

    /** Check matches' pill (F44): "Checking 120…", "No changes · 17 Nov", "4 to review", or "Not run yet". */
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
        if (!changes.isEmpty())
        {
            return changes.size() + " to review";
        }
        return "No changes · " + new java.text.SimpleDateFormat("d MMM", java.util.Locale.UK).format(
            new java.util.Date(lastCheckMs));
    }

    /** The Check matches review page: each change old → new with Accept · Keep current · Fix match. */
    private void matchResults(LinearLayout column)
    {
        if (changes.isEmpty())
        {
            column.addView(text("No changes: every movie still finds its own match", 16, Color.WHITE));
            return;
        }
        List<Action> all = new java.util.ArrayList<>();
        all.add(new Action("Accept all", "done", this::acceptAll));
        actionRow(column, "done", "Accept all", "Use every new match below", null, all, false);
        for (MatchCheck.Change change : new java.util.ArrayList<>(changes))
        {
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Accept", "done", () -> accept(change)));
            actions.add(new Action("Keep current", "cancel", () -> {
                changes.remove(change);
                refreshMatches();
            }));
            actions.add(new Action("Fix match", "search", () -> {
                if (folderActions != null)
                {
                    folderActions.fixMatch(change.movie);
                }
            }));
            String current = change.movie.getName() == null ? "" : change.movie.getName();
            actionRow(column, "film", current + "  →  " + change.newTitle,
                      "Found from the file name: " + change.movie.getFilenameNonCryptic(), "Movie", actions, false);
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
        View focus = findFocus();
        if (event.getAction() == android.view.KeyEvent.ACTION_DOWN
            && event.getKeyCode() == android.view.KeyEvent.KEYCODE_DPAD_UP && focus != null && isInside(body, focus))
        {
            // Up out of the content lands on the open tab (F17), never on the scan button or the top bar (F9).
            View next = focus.focusSearch(View.FOCUS_UP);
            if (next == null || !isInside(body, next))
            {
                tabs.tab(tabs.openTab()).requestFocus();
                return true;
            }
        }
        boolean inside = browser != null || form != null && tabs.openTab() == CONNECTIONS
            || carePage != null && tabs.openTab() == LIBRARY_CARE;
        if (inside && event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK)
        {
            if (event.getAction() == android.view.KeyEvent.ACTION_UP)
            {
                back();
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

    private void focusContent()
    {
        if (browser != null)
        {
            browser.requestFocus();
            return;
        }
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

    /**
     * A plain row: icon, name and second line, the pill in its fixed column, › only when OK opens something. A row
     * with nothing to open (a "None" row) has no › and OK does nothing (F46, RULES §10).
     */
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
        row.addView(words, new LayoutParams(SupernovaStyle.dp(context, WORDS_DP), -2));
        TextView status = text(pill == null ? "" : pill, 13.5f, Color.WHITE);
        status.setGravity(Gravity.CENTER);
        // A row without a status keeps the column's space, so names and pills stay lined up.
        status.setVisibility(pill == null ? INVISIBLE : VISIBLE);
        row.addView(status, new LayoutParams(SupernovaStyle.dp(context, PILL_DP), SupernovaStyle.dp(context, 24)));
        row.addView(new View(context), new LayoutParams(0, 1, 1));
        TextView chevron = text(open == null ? "" : "›", 20, Color.WHITE);
        chevron.setGravity(Gravity.CENTER);
        row.addView(chevron, new LayoutParams(SupernovaStyle.dp(context, 24), -2));
        if (open != null)
        {
            row.setOnClickListener(view -> open.run());
        }
        Runnable restyle = () -> {
            boolean focused = row.isFocused();
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            title.setTextColor(ink);
            second.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : SECOND_LINE);
            status.setTextColor(ink);
            status.setBackground(pill == null ? null : outline(ink));
            chevron.setTextColor(ink);
            mark.setTextColor(ink);
            SupernovaStyle.inkIcons(mark, ink);
            row.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL) : null);
        };
        row.setOnFocusChangeListener((view, focused) -> restyle.run());
        restyle.run();
        divider(column);
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
