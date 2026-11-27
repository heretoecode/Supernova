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
import java.util.Locale;

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
    static final int LINE = 0x1FFFFFFF;

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
        setBackground(new com.archos.mediacenter.video.leanback.PreviewUtilityBackground(context));
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
        /** Every folder of this server and login (F52): one Scan one source button scans them all. */
        public final List<android.net.Uri> roots;

        public Source(String label, android.net.Uri root, boolean local)
        {
            this(label, root, local, java.util.Collections.singletonList(root));
        }

        public Source(String label, android.net.Uri root, boolean local, List<android.net.Uri> roots)
        {
            this.label = label;
            this.root = root;
            this.local = local;
            this.roots = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(roots));
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
        /** The folders chosen on this server with this login (F52); one row shows them all. */
        public final List<android.net.Uri> folders;

        public Connection(String name, android.net.Uri root, Boolean reachable, long videos, long lastSeenMs)
        {
            this(name, root, reachable, videos, lastSeenMs, "");
        }

        public Connection(String name, android.net.Uri root, Boolean reachable, long videos, long lastSeenMs,
                          String account)
        {
            this(name, root, reachable, videos, lastSeenMs, account,
                 root == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(root));
        }

        public Connection(String name, android.net.Uri root, Boolean reachable, long videos, long lastSeenMs,
                          String account, List<android.net.Uri> folders)
        {
            this.folders = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(folders));
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
        publishBadge();
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
        ScrollView scroll = new BelowBarScroll(getContext());
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
        statRow(column, "list", s.all.name, places(s.all), size(s.all.bytes()),
                joinDot(count(s.all.titles, "title"), count(s.all.videos, "video")), true);
        statRow(column, "check", "Watched", "Titles you have finished", s.percent(s.watchedTitles) + "%",
                s.watchedTitles + " of " + count(s.all.titles, "title"), false);
        statRow(column, "time", "Time to watch everything", "Every unwatched title and episode",
                String.format(java.util.Locale.UK, "%,d hours", s.hoursToWatch), false);
        // Every quality (F21): 4K, 1080p, 720p, SD and HDR; counts of videos, then their share.
        // FILES.md: values stay stacked, a quality label over its number; every quality's share on the line.
        statRow(column, "hdr", "Picture", s.picturePercents(), s.topPicture().isEmpty() ? "Unknown" : s.topPicture(),
                s.topPictureShare(), false);
        java.text.DateFormat month = new java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.UK);
        statRow(column, "folder", "Added this month", "Since " + month.format(monthStart()),
                count(s.addedThisMonth, "title"), false);
        heading(column, "By Library Page", "film", true);
        for (LibraryStats.Page page : new LibraryStats.Page[] {s.movies, s.shows})
        {
            String under = page == s.shows
                ? joinDot(count(page.titles, "title"), count(page.videos, "episode"))
                : count(page.titles, "title");
            statRow(column, page == s.shows ? "tv" : "film", page.name, places(page), size(page.bytes()), under,
                    false);
        }
        ownPage(column);
        bySource(column);
        biggestMovies(column);
        if (!s.heaviest.isEmpty())
        {
            heading(column, "Most Space Per Episode", "sort", true);
            for (LibraryStats.Heavy heavy : s.heaviest)
            {
                statRow(column, "tv", heavy.show,
                        joinDot(count(heavy.episodes, "episode"), size(heavy.bytes)),
                        size(heavy.perEpisode()), "per episode", false);
            }
        }
    }

    /** FL7 (owner, 18 Nov 2026): your own library page under By Library Page too, after Movies and TV Shows. */
    private void ownPage(LinearLayout column)
    {
        com.archos.mediacenter.video.leanback.CustomLibraryPage page =
            com.archos.mediacenter.video.leanback.CustomLibraryPage.load(getContext());
        com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot snapshot =
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
        if (page == null || snapshot == null)
        {
            return;
        }
        long bytes = 0;
        int titles = 0;
        for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : page.entries(snapshot))
        {
            titles++;
            if (entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Video)
            {
                bytes += ((com.archos.mediacenter.video.browser.adapters.object.Video) entry.media).getSize();
                continue;
            }
            for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry episode : snapshot.episodes)
            {
                if (episode.show == entry.show
                    && episode.media instanceof com.archos.mediacenter.video.browser.adapters.object.Video)
                {
                    bytes += ((com.archos.mediacenter.video.browser.adapters.object.Video) episode.media).getSize();
                }
            }
        }
        // M7 (owner, 26 Nov): every library page, built-in or your own, has its size on the right.
        statRow(column, "library", page.name, "Your own library page", size(bytes), count(titles, "title"), false);
    }

    /** Your Library › By Source (FILES.md): the videos on each drive or connection, then by connection type. */
    private void bySource(LinearLayout column)
    {
        com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot snapshot =
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
        if (snapshot == null)
        {
            return;
        }
        java.util.Map<String, long[]> sources = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        java.util.Map<String, long[]> kinds = new java.util.TreeMap<>();
        java.util.List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> all =
            new java.util.ArrayList<>(snapshot.movies);
        all.addAll(snapshot.episodes);
        for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : all)
        {
            if (!(entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Video))
            {
                continue;
            }
            com.archos.mediacenter.video.browser.adapters.object.Video video =
                (com.archos.mediacenter.video.browser.adapters.object.Video) entry.media;
            android.net.Uri uri = video.getFileUri();
            String kind = sourceKind(uri);
            String name = com.archos.mediacenter.video.browser.Delete.serverName(uri) + " · " + kind;
            long[] source = sources.computeIfAbsent(name, k -> new long[2]);
            source[0]++;
            source[1] += Math.max(0, video.getSize());
            long[] byKind = kinds.computeIfAbsent(kind, k -> new long[2]);
            byKind[0]++;
            byKind[1] += Math.max(0, video.getSize());
        }
        if (sources.isEmpty())
        {
            return;
        }
        heading(column, "By Source", "storage", true);
        statRow(column, "info", "The videos in your library on each source",
                "Not the space left on it", "", false);
        for (java.util.Map.Entry<String, long[]> source : sources.entrySet())
        {
            statRow(column, "source", source.getKey(), "Videos in your library here", size(source.getValue()[1]),
                    count(source.getValue()[0], "video"), false);
        }
        List<String> parts = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, long[]> kind : kinds.entrySet())
        {
            parts.add(kind.getKey() + " " + size(kind.getValue()[1]));
        }
        statRow(column, "network", "By connection type", TextUtils.join(" · ", parts), "", false);
    }

    /** "USB", "SMB", "WebDAV", "SFTP", "FTP" or "This device". */
    static String sourceKind(android.net.Uri uri)
    {
        String scheme = uri == null || uri.getScheme() == null ? "file" : uri.getScheme().toLowerCase(Locale.ROOT);
        switch (scheme)
        {
        case "smb":
            return "SMB";
        case "webdav":
        case "webdavs":
        case "https":
        case "http":
            return "WebDAV";
        case "sftp":
            return "SFTP";
        case "ftp":
        case "ftps":
            return "FTP";
        default:
            String path = uri == null ? "" : String.valueOf(uri.getPath());
            return path.startsWith("/storage/") && !path.startsWith("/storage/emulated") ? "USB" : "This device";
        }
    }

    /** Your Library › Biggest Movies (owner, 26 Nov): the three largest films, size on the right. */
    private void biggestMovies(LinearLayout column)
    {
        com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot snapshot =
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
        if (snapshot == null || snapshot.movies.isEmpty())
        {
            return;
        }
        java.util.List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> films =
            new java.util.ArrayList<>(snapshot.movies);
        films.sort((a, b) -> Long.compare(bytes(b), bytes(a)));
        heading(column, "Biggest Movies", "film", true);
        for (int i = 0; i < Math.min(3, films.size()); i++)
        {
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry film = films.get(i);
            com.archos.mediacenter.video.browser.adapters.object.Video video =
                (com.archos.mediacenter.video.browser.adapters.object.Video) film.media;
            int year = video instanceof com.archos.mediacenter.video.browser.adapters.object.Movie
                ? ((com.archos.mediacenter.video.browser.adapters.object.Movie) video).getYear() : 0;
            String quality = com.archos.mediacenter.video.leanback.PreviewVariants.fileBadge(getContext(), video);
            long ms = video.getDurationMs() > 0 ? video.getDurationMs() : film.runtime;
            List<String> facts = new java.util.ArrayList<>();
            if (year > 0)
            {
                facts.add(String.valueOf(year));
            }
            if (!quality.isEmpty())
            {
                facts.add(quality);
            }
            if (ms > 0)
            {
                long minutes = ms / 60000;
                facts.add(minutes >= 60 ? minutes / 60 + " h " + minutes % 60 + " min" : minutes + " min");
            }
            statRow(column, "film", video.getName(), TextUtils.join(" · ", facts), size(bytes(film)), false);
        }
    }

    private static long bytes(com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry)
    {
        return entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Video
            ? ((com.archos.mediacenter.video.browser.adapters.object.Video) entry.media).getSize() : 0;
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
        statRow(column, icon, name, line, value, "", first);
    }

    /**
     * One read-only fact row of Your Library (FILES.md, tv/22 v2): nothing focusable (Down / Up scroll the tab); the
     * value on the right is stacked over a quieter one ("1.4 TB" over "212 videos"), so sizes line up in one column.
     */
    private void statRow(LinearLayout column, String icon, String name, String line, String value, String under,
                         boolean first)
    {
        Context context = getContext();
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(false);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        int pad = SupernovaStyle.dp(context, 20);
        row.setPadding(pad, 0, pad, 0);
        row.setContentDescription(under.isEmpty() ? name + ", " + value : name + ", " + value + ", " + under);
        TextView mark = new TextView(context);
        PreviewIcon.apply(mark, icon, 16);
        row.addView(mark, new LayoutParams(SupernovaStyle.dp(context, 36), -2));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(VERTICAL);
        words.addView(text(name, 18, Color.WHITE));
        words.addView(text(line, 13.5f, SECOND_LINE));
        row.addView(words, new LayoutParams(0, -2, 1));
        LinearLayout figures = new LinearLayout(context);
        figures.setOrientation(VERTICAL);
        figures.setGravity(Gravity.END);
        TextView figure = text(value, 15, Color.WHITE);
        figure.setGravity(Gravity.END);
        figure.setFontFeatureSettings("tnum");
        figure.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        figures.addView(figure);
        if (!under.isEmpty())
        {
            TextView quiet = text(under, 13.5f, SECOND_LINE);
            quiet.setGravity(Gravity.END);
            quiet.setFontFeatureSettings("tnum");
            figures.addView(quiet);
        }
        row.addView(figures, new LayoutParams(-2, -2));
        Object previous = column.getChildCount() == 0 ? null : column.getChildAt(column.getChildCount() - 1).getTag();
        if (previous instanceof String && ((String) previous).startsWith(ROW))
        {
            // Faint row lines: a long read-only list of facts (FILES.md).
            View divider = new View(context);
            divider.setBackgroundColor(LINE);
            divider.setAlpha(0.5f);
            column.addView(divider, new LayoutParams(-1, Math.max(1, SupernovaStyle.dp(context, 1))));
        }
        row.setTag(first ? "files.stat.first" : ROW);
        column.addView(row, new LayoutParams(-1, SupernovaStyle.dp(context, ROW_HEIGHT_DP)));
    }

    /**
     * The tab row's right end (F18, approved 17 Nov 2026): only "⟳ Scan library" (OK scans every source), or
     * "Scanning · 42%" while a scan runs (OK stops). No facts: the badge, Library Health and Your Library say them.
     */
    private void showSummary()
    {
        summary.removeAllViews();
        ScanProgress progress = currentProgress();
        String words = progress != null && (scanning || progress.running
                                                || System.currentTimeMillis() - finishedAt(progress) < DONE_SHOWS_MS)
            ? narrate(progress) + (waiting.isEmpty() ? "" : " · " + waiting.size() + " more waiting")
            : scanLabel(scanning, scanPercent());
        TextView control = text(words, 18, Color.WHITE);
        // Fixed-width digits, so the words never jiggle as numbers change (FILES.md).
        control.setFontFeatureSettings("tnum");
        control.setSingleLine(true);
        control.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        PreviewIcon.apply(control, "scan", 16);
        summary.addView(control);
        scanControl(control);
    }

    /** How long "Done · 3 new · 1 missing" stays before "Scan library" comes back. */
    static final long DONE_SHOWS_MS = 5000;
    /** About 45 characters at most; long source names end "…". */
    static final int NARRATION_MAX = 45;

    private static ScanProgress currentProgress()
    {
        try
        {
            return com.archos.mediacenter.video.leanback.PreviewLibraryScan.progress();
        }
        catch (RuntimeException unavailable)
        {
            return null;
        }
    }

    private static long finishedAt(ScanProgress progress)
    {
        return progress.startedUtcMs + Math.max(0, progress.durationMs);
    }

    /**
     * Scan library narrates every step, never a bare "Scanning" (FILES.md, `tv/23`): Connecting to Seedbox… →
     * Looking in Seedbox · 42% → Found 3 new videos → Done · 3 new · 1 missing; a failure says so in place
     * ("Couldn't connect to Living-room NAS"). Pure, for tests.
     */
    public static String narrate(ScanProgress p)
    {
        String source = shortName(p.sourceLabel == null || p.sourceLabel.isEmpty() ? "your library" : p.sourceLabel);
        if (p.running && p.stopping)
        {
            return "Stopping…";
        }
        if (p.running)
        {
            if (p.total <= 0 && p.checked <= 0)
            {
                return "Connecting to " + source + "…";
            }
            if (p.total > 0 && p.checked >= p.total && p.added > 0)
            {
                return "Found " + p.added + (p.added == 1 ? " new video" : " new videos");
            }
            if (p.total > 0)
            {
                long percent = Math.max(0, Math.min(100, p.checked * 100 / p.total));
                return "Looking in " + source + " · " + percent + "%";
            }
            return "Looking in " + source;
        }
        switch (p.failure)
        {
        case NONE:
            List<String> parts = new java.util.ArrayList<>();
            parts.add("Done");
            parts.add(p.added + " new");
            if (p.removed > 0)
            {
                parts.add(p.removed + " missing");
            }
            if (p.updated > 0)
            {
                parts.add(p.updated + " updated");
            }
            return TextUtils.join(" · ", parts);
        case CANCELLED:
            return "Stopped";
        case SIGN_IN:
            return "Couldn't sign in to " + source;
        case TIMEOUT:
            return source + " took too long to answer";
        case PERMISSION:
            return source + " didn't allow Supernova in";
        case STORAGE:
            return "Couldn't read " + source;
        default:
            return "Couldn't connect to " + source;
        }
    }

    /** A source name short enough for the narration (the rest of the line stays). */
    static String shortName(String name)
    {
        int room = NARRATION_MAX - 22;
        return name.length() <= room ? name : name.substring(0, room - 1) + "…";
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
        int toFix = toFix().size();
        row(column, "edit", "To fix", "Titles that need a new match", LibraryCareCounts.pill(toFix, "title", "titles"),
            toFix > 0 ? () -> openCare("fix") : null, false);
        heading(column, "Kept By You", "eye-off", true);
        int hidden = com.archos.mediacenter.video.leanback.HeroExclusions.titles(getContext(),
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache()).size();
        row(column, "hero-exclude", "Excluded from hero", "Titles kept off Home's hero",
            LibraryCareCounts.pill(hidden, "title", "titles"), hidden > 0 ? () -> openCare("hero") : null, false);
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
                           "review", "Check matches", "scans", "All scans", "fix", "To fix",
                           "hero", "Excluded from hero"};
        String title = carePage;
        for (int index = 0; index < titles.length; index += 2)
        {
            if (titles[index].equals(carePage))
            {
                title = titles[index + 1];
            }
        }
        if (carePage.startsWith("scan:"))
        {
            List<ScanProgress> recent = LibraryScanController.recentScans();
            int at = Integer.parseInt(carePage.substring(5));
            title = "Recent scans" + (at < recent.size() && recent.get(at).sourceLabel != null
                ? "  ›  " + recent.get(at).sourceLabel : "");
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
            case "fix":
                toFixPage(column);
                break;
            case "hero":
                heroPage(column);
                break;
            default:
                if (carePage.startsWith("scan:"))
                {
                    scanDetails(column, Integer.parseInt(carePage.substring(5)));
                    break;
                }
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
        TextView note = text("Find it again looks for the same file and links it back, keeping where you stopped. "
                             + "Remove takes it out of your library only.", 14, SECOND_LINE);
        note.setSingleLine(false);
        note.setPadding(0, SupernovaStyle.dp(getContext(), 14), 0, 0);
        column.addView(note);
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
        java.util.Set<String> fine = getContext().getSharedPreferences(CARE, Context.MODE_PRIVATE)
            .getStringSet("plays_fine", java.util.Collections.emptySet());
        for (String[] row : new java.util.ArrayList<>(unreadableRows))
        {
            if (fine.contains(row[0]))
            {
                continue;
            }
            android.net.Uri uri = android.net.Uri.parse(row[0]);
            String name = uri.getLastPathSegment() == null ? row[0] : uri.getLastPathSegment();
            String key = "unreadable:" + row[0];
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Try again", "scan", () -> {
                android.net.Uri folder = parent(uri);
                if (folder != null)
                {
                    FolderBrowser.scanFolder(getContext(), folder);
                }
                outcomes.put(key, "Trying again with the next scan");
                show(LIBRARY_CARE);
            }));
            // "This is fine" beside the fix (FILES.md): the item leaves the list and its count.
            actions.add(new Action("It plays fine", "check", () -> {
                fine("plays_fine", row[0]);
                unreadableRows.remove(row);
                unreadable = Math.max(0, unreadable - 1);
                publishBadge();
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
            actions.add(new Action("Fix match", "edit", () -> {
                if (folderActions != null)
                {
                    folderActions.fixMatch(video);
                }
            }));
            actions.add(new Action("It's right", "check", () -> {
                fine("not_a_movie", String.valueOf(video.getId()));
                publishBadge();
                show(LIBRARY_CARE);
            }));
            String name = video.getName() == null ? video.getFilenameNonCryptic() : video.getName();
            actionRow(column, "film", name, bestGuess(video.getFilenameNonCryptic(), where(video)), null, actions,
                      false);
        }
        pendingLine(column);
    }

    /** M5: "Matching N new titles: they're not listed while Supernova is still looking them up." */
    private void pendingLine(LinearLayout column)
    {
        int pending = counts == null ? 0 : counts.pending;
        if (pending <= 0)
        {
            return;
        }
        TextView line = text("Matching " + pending + (pending == 1 ? " new title" : " new titles")
                             + ": they're not listed while Supernova is still looking them up.", 14, SECOND_LINE);
        line.setSingleLine(false);
        line.setPadding(0, SupernovaStyle.dp(getContext(), 14), 0, 0);
        column.addView(line);
    }

    private static String where(com.archos.mediacenter.video.browser.adapters.object.Video video)
    {
        android.net.Uri uri = video.getFileUri();
        android.net.Uri folder = uri == null ? null : parent(uri);
        return folder == null || folder.getLastPathSegment() == null ? "" : folder.getLastPathSegment();
    }

    /**
     * The best guess from the file name (FILES.md, tv/20b): "Looks like Blue Planet III · S1 E2 · from the file
     * name", or "No guess yet · <where>".
     */
    static String bestGuess(String fileName, String where)
    {
        String name = fileName == null ? "" : fileName.replaceAll("\\.[A-Za-z0-9]{2,4}$", "");
        java.util.regex.Matcher episode =
            java.util.regex.Pattern.compile("(?i)[ ._-]s(\\d{1,2})[ ._-]?e(\\d{1,3})").matcher(name);
        String title = name;
        String tail = "";
        if (episode.find())
        {
            title = name.substring(0, episode.start());
            tail = " · S" + Integer.parseInt(episode.group(1)) + " E" + Integer.parseInt(episode.group(2));
        }
        else
        {
            java.util.regex.Matcher year = java.util.regex.Pattern.compile("[ ._(\\[-](19|20)\\d\\d").matcher(name);
            if (year.find())
            {
                title = name.substring(0, year.start());
            }
        }
        title = title.replaceAll("[._]+", " ").trim();
        if (title.isEmpty() || title.length() < 2)
        {
            return "No guess yet" + (where == null || where.isEmpty() ? "" : " · " + where);
        }
        return "Looks like " + title + tail + " · from the file name";
    }

    /** An item answered "this is fine": it leaves its list and its count. */
    private void fine(String list, String id)
    {
        android.content.SharedPreferences care = getContext().getSharedPreferences(CARE, Context.MODE_PRIVATE);
        java.util.Set<String> kept = new java.util.HashSet<>(care.getStringSet(list, java.util.Collections.emptySet()));
        kept.add(id);
        care.edit().putStringSet(list, kept).apply();
    }

    /** To fix (FILES.md, tv/17): titles TMDB no longer knows (D16), each with Fix match · It's right. */
    private List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> toFix()
    {
        List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> found = new java.util.ArrayList<>();
        com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot snapshot =
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
        if (snapshot == null)
        {
            return found;
        }
        java.util.Set<String> fine = getContext().getSharedPreferences(CARE, Context.MODE_PRIVATE)
            .getStringSet("fix_is_right", java.util.Collections.emptySet());
        List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> all =
            new java.util.ArrayList<>(snapshot.movies);
        all.addAll(snapshot.shows);
        for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : all)
        {
            String kind = entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Tvshow
                ? "tv" : "movie";
            if (entry.onlineId > 0 && com.archos.mediacenter.video.leanback.TmdbGone.gone(getContext(), kind,
                                                                                       entry.onlineId)
                && !fine.contains(kind + ":" + entry.onlineId))
            {
                found.add(entry);
            }
        }
        return found;
    }

    private void toFixPage(LinearLayout column)
    {
        List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> titles = toFix();
        if (titles.isEmpty())
        {
            column.addView(text("Nothing to fix", 16, Color.WHITE));
            return;
        }
        for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : titles)
        {
            boolean show = entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Tvshow;
            String kind = show ? "tv" : "movie";
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Fix match", "edit", () -> {
                if (folderActions != null && entry.media instanceof com.archos.mediacenter.video.browser.adapters
                                                                        .object.Video)
                {
                    folderActions.fixMatch((com.archos.mediacenter.video.browser.adapters.object.Video) entry.media);
                }
            }));
            actions.add(new Action("It's right", "check", () -> {
                fine("fix_is_right", kind + ":" + entry.onlineId);
                show(LIBRARY_CARE);
            }));
            actionRow(column, show ? "tv" : "film",
                      com.archos.mediacenter.video.leanback.PreviewPages.displayName(entry),
                      com.archos.mediacenter.video.leanback.TmdbGone.line(kind), null, actions, false);
        }
    }

    /** Excluded from hero (FILES.md, tv/19): A–Z, Include again on the focused row, Include all again on top. */
    private void heroPage(LinearLayout column)
    {
        List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> titles =
            com.archos.mediacenter.video.leanback.HeroExclusions.titles(getContext(),
                com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache());
        if (titles.isEmpty())
        {
            column.addView(text("Every title can appear on the hero", 16, Color.WHITE));
            return;
        }
        row(column, "plus", "Include all again", count(titles.size(), "title"), null, () -> {
            com.archos.mediacenter.video.leanback.HeroExclusions.includeAll(getContext());
            show(LIBRARY_CARE);
        }, true);
        java.text.DateFormat day = new java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.UK);
        for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : titles)
        {
            boolean show = entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Tvshow;
            long at = com.archos.mediacenter.video.leanback.HeroExclusions.excludedAt(getContext(), entry);
            int year = entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Movie
                ? ((com.archos.mediacenter.video.browser.adapters.object.Movie) entry.media).getYear() : 0;
            String line = (show ? "TV show" : "Movie") + (year > 0 ? " · " + year : "")
                + (at > 0 ? " · excluded " + day.format(new java.util.Date(at)) : "");
            List<Action> actions = new java.util.ArrayList<>();
            actions.add(new Action("Include again", "plus", () -> {
                com.archos.mediacenter.video.leanback.HeroExclusions.include(getContext(), entry);
                show(LIBRARY_CARE);
            }));
            actionRow(column, show ? "tv" : "film",
                      com.archos.mediacenter.video.leanback.PreviewPages.displayName(entry), line, null, actions,
                      false);
        }
    }

    /** The Library Care badge, also the top bar's dot (FL8: the dot shows only when a tab has a badge). */
    private void publishBadge()
    {
        int count = toCheck();
        tabs.setBadge(LIBRARY_CARE, count);
        com.archos.mediacenter.video.leanback.TopBarSignal.filesBadge(getContext(), count);
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
        // Scan one source › (FILES.md, tv/21): OK opens a menu of every source with where it stands on the right;
        // OK queues it; several can wait their turn. While scanning, the row says what the tab bar says.
        ScanProgress now = currentProgress();
        String second = scanning && now != null ? narrate(now)
            + (waiting.isEmpty() ? "" : " · " + waiting.size() + " waiting")
            : waiting.isEmpty() ? "One drive, server or folder only"
            : TextUtils.join(", ", waiting) + (waiting.size() == 1 ? " is" : " are") + " waiting for its turn";
        row(column, "integration", "Scan one source", second, null, this::scanOneSourceMenu, false);
    }

    /** The menu of sources, each "Seedbox · Waiting" / "· Scanning · 42%" / "· 17:28" (its last scan). */
    private void scanOneSourceMenu()
    {
        List<ScanProgress> recent = LibraryScanController.recentScans();
        ScanProgress now = currentProgress();
        String[] labels = new String[sources.size()];
        String[] icons = new String[labels.length];
        java.text.DateFormat clock = new java.text.SimpleDateFormat("d MMM HH:mm", java.util.Locale.UK);
        for (int i = 0; i < labels.length; i++)
        {
            Source source = sources.get(i);
            String state = "";
            if (waiting.contains(source.label))
            {
                state = "Waiting";
            }
            else if (now != null && now.running && source.label.equals(now.sourceLabel))
            {
                state = now.total > 0 ? "Scanning · " + Math.min(100, now.checked * 100 / now.total) + "%" : "Scanning";
            }
            else
            {
                for (ScanProgress scan : recent)
                {
                    if (source.label.equals(scan.sourceLabel))
                    {
                        state = scan.outcome() == ScanProgress.Outcome.FAILED ? "Couldn't connect"
                            : clock.format(new java.util.Date(scan.startedUtcMs));
                        break;
                    }
                }
            }
            labels[i] = state.isEmpty() ? source.label : source.label + " · " + state;
            icons[i] = source.local ? (i == 0 ? "tv" : "usbdrive") : (i % 2 == 0 ? "integration" : "network");
        }
        com.archos.mediacenter.video.leanback.PreviewDialog.menu(getContext(), findFocus(), "Scan one source", labels,
            icons, -1, n -> scanSource(sources.get(n)));
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
            scanRow(column, scans.get(index), now, index);
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
            // Each scan is focusable with › and opens its Scan details (FILES.md, tv/23, tv/24).
            scanRow(column, scans.get(index), now, index);
        }
        if (scans.isEmpty())
        {
            column.addView(text("No scans yet", 16, Color.WHITE));
        }
    }

    /** One scan as plain information: no focus, no pill, no line (RULES §5). */
    /** A recent scan as a row with › (FILES.md, tv/23): source, "Today 18:28 · 3 new · 1 missing", its pill. */
    private void scanRow(LinearLayout column, ScanProgress scan, long now, int index)
    {
        String name = scan.sourceLabel == null || scan.sourceLabel.isEmpty() ? "Library" : scan.sourceLabel;
        String second = RecentScanLine.second(scan.outcome(), scan.failureReason(), scan.startedUtcMs, now,
                                              scan.checked, scan.added, scan.removed);
        row(column, scan.outcome() == ScanProgress.Outcome.FAILED ? "cancel" : "scan", name, second,
            RecentScanLine.pill(scan.outcome()), () -> openCare("scan:" + index), false);
    }

    /**
     * Scan details (FILES.md, tv/24): "Library Care › Recent scans › Seedbox  Today 18:28". Summary: When, Files
     * listed, Result; then why it failed, if it did.
     */
    private void scanDetails(LinearLayout column, int index)
    {
        List<ScanProgress> scans = LibraryScanController.recentScans();
        if (index < 0 || index >= scans.size())
        {
            column.addView(text("This scan is no longer kept", 16, Color.WHITE));
            return;
        }
        ScanProgress scan = scans.get(index);
        java.text.DateFormat clock = new java.text.SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.UK);
        heading(column, "Summary", "info", false);
        long seconds = Math.max(0, scan.durationMs / 1000);
        String length = seconds >= 60 ? seconds / 60 + " min " + seconds % 60 + " s" : seconds + " s";
        fact(column, "When", clock.format(new java.util.Date(scan.startedUtcMs)) + " · " + length);
        fact(column, "Files listed", scan.checked >= 0 ? String.format(java.util.Locale.UK, "%,d", scan.checked)
            : "Unknown");
        List<String> result = new java.util.ArrayList<>();
        result.add(Math.max(0, scan.added) + " new");
        if (scan.updated > 0)
        {
            result.add(scan.updated + " updated");
        }
        result.add(Math.max(0, scan.removed) + " missing");
        fact(column, "Result", TextUtils.join(" · ", result));
        fact(column, "Outcome", RecentScanLine.pill(scan.outcome())
            + (scan.outcome() == ScanProgress.Outcome.FAILED ? " · " + scan.failureReason() : ""));
        whatChanged(column, scan);
        // Step by step (tv/24): the transcript, times to the second; failures in soft red.
        List<com.archos.mediacenter.video.scanning.ScanTranscript.Line> steps =
            com.archos.mediacenter.video.scanning.ScanTranscript.lines(scan.batchId, scan.sourceId);
        heading(column, "Step by step", "list", true);
        if (steps.isEmpty())
        {
            fact(column, "Steps", "Kept only for scans since the app last started");
        }
        for (com.archos.mediacenter.video.scanning.ScanTranscript.Line step : steps)
        {
            TextView line = text(step.text(), 14, step.failed ? 0xFFFF8A80 : SECOND_LINE);
            line.setFontFeatureSettings("tnum");
            line.setPadding(SupernovaStyle.dp(getContext(), 20), SupernovaStyle.dp(getContext(), 3), 0,
                            SupernovaStyle.dp(getContext(), 3));
            column.addView(line, new LayoutParams(-1, -2));
        }
    }

    /**
     * What changed (tv/24): the titles that arrived in the library while this scan ran, each with its New pill, and
     * the count found missing (listed in Library Care › Missing files).
     */
    private void whatChanged(LinearLayout column, ScanProgress scan)
    {
        com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot snapshot =
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
        List<String> titles = new java.util.ArrayList<>();
        long from = scan.startedUtcMs / 1000 - 1;
        long to = (scan.startedUtcMs + Math.max(0, scan.durationMs)) / 1000 + 60;
        if (snapshot != null)
        {
            List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> all =
                new java.util.ArrayList<>(snapshot.movies);
            all.addAll(snapshot.episodes);
            for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : all)
            {
                if (entry.added >= from && entry.added <= to && titles.size() < 20)
                {
                    titles.add(com.archos.mediacenter.video.leanback.PreviewPages.displayName(entry));
                }
            }
        }
        if (titles.isEmpty() && scan.removed <= 0)
        {
            return;
        }
        heading(column, "What changed", "scan", true);
        for (String title : titles)
        {
            statRow(column, "film", title, "Added by this scan", "New", false);
        }
        if (scan.removed > 0)
        {
            statRow(column, "folder", scan.removed + (scan.removed == 1 ? " file missing" : " files missing"),
                    "Listed in Library Care › Missing files", "Missing", false);
        }
    }

    /** A read-only label and value (soft label, white value; RULES text on the page). */
    private void fact(LinearLayout column, String label, String value)
    {
        LinearLayout line = new LinearLayout(getContext());
        line.setPadding(SupernovaStyle.dp(getContext(), 20), SupernovaStyle.dp(getContext(), 6), 0,
                        SupernovaStyle.dp(getContext(), 6));
        line.addView(text(label, 15, SECOND_LINE), new LayoutParams(SupernovaStyle.dp(getContext(), 160), -2));
        TextView shown = text(value, 15, Color.WHITE);
        shown.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        line.addView(shown, new LayoutParams(0, -2, 1));
        column.addView(line, new LayoutParams(-1, -2));
    }

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
            // FL3 (owner, 18 Nov 2026; review-0157-2/01 3): Turn off keeps the settings and hides its titles; the row
            // says Off and dims; Turn on brings them back.
            boolean off = connectionOff(connection);
            List<Action> actions = new java.util.ArrayList<>();
            if (off)
            {
                actions.add(new Action("Turn on", "play", () -> setConnectionOff(connection, false)));
            }
            else
            {
                actions.add(new Action("Open", "folder", () -> openServer(connection)));
            }
            actions.add(new Action("Edit", "edit", () -> openForm(connection.root, connection.name)));
            if (!off)
            {
                actions.add(new Action("Test", "done", () -> testConnection(connection.root, key)));
                actions.add(new Action("Turn off", "pause", () -> setConnectionOff(connection, true)));
            }
            actions.add(new Action("Remove", "discard", () -> removeConnection(connection)));
            String title = connection.account == null || connection.account.isEmpty()
                ? connection.name : connection.name + " · " + connection.account;
            String second = off ? ConnectionLine.kind(scheme) + " · Off · its " + count(connection.videos, "video")
                    + (connection.videos == 1 ? " is" : " are") + " hidden"
                : outcomes.containsKey(key) ? outcomes.get(key)
                : ConnectionLine.kind(scheme) + " · " + (connection.folders.size() > 1
                    ? FilesPageData.folderNames(connection.folders) : count(connection.videos, "video"));
            String pill = off ? "Off" : connection.reachable == null ? null : ConnectionLine.pill(connection.reachable);
            actionRow(column, ConnectionLine.kind(scheme).equals("WebDAV") ? "cloud" : "integration", title, second,
                      pill, actions, true);
            if (off)
            {
                column.getChildAt(column.getChildCount() - 1).setAlpha(.6f);
            }
        }
        // Add a connection: the row itself opens the form (F39).
        row(column, "plus", "Add a connection", "SMB, WebDAV, SFTP or FTP", null, () -> openForm(null, null),
            column.findViewWithTag("files.row.first") == null);
        if (!found.isEmpty())
        {
            heading(column, "Found On Your Network", "search", true);
            for (Found device : found)
            {
                List<Action> actions = new java.util.ArrayList<>();
                actions.add(new Action("Open", "folder", () -> openConnection(device.uri, device.name)));
                actions.add(new Action("Add", "plus", () -> openFoundForm(device.uri)));
                actionRow(column, "tv", device.name, device.line, "New", actions, true);
            }
        }
        // Advanced › Network options, moved here from Settings (FILES.md, tv/15): the last section.
        heading(column, "Advanced", "advanced", true);
        row(column, "network", "Network options", "SMB, finding devices and SFTP", null,
            () -> com.archos.mediacenter.video.leanback.NetworkOptionsPanel.show(getContext(), () -> { }), false);
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
    /** Open: one folder opens straight away; several (F52) ask which, in a menu beside the row. */
    private void openServer(Connection connection)
    {
        if (connection.folders.size() <= 1)
        {
            openConnection(connection.root, connection.name);
            return;
        }
        String[] labels = new String[connection.folders.size()];
        String[] icons = new String[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            labels[i] = FilesPageData.folderName(connection.folders.get(i));
            icons[i] = i == 0 ? "folder" : i == 1 ? "files" : "file";
        }
        com.archos.mediacenter.video.leanback.PreviewDialog.menu(getContext(), findFocus(), connection.name, labels,
            icons, -1, n -> openConnection(connection.folders.get(n), labels[n]));
    }

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
            // FILES.md (tv/10): the answer in the row's second line, plainly.
            outcomes.put(key, answered ? "Connected" : "Can't reach it · check it's switched on and on this network");
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
        if (!LibraryChanges.apply(getContext(), selection -> {
            for (android.net.Uri folder : connection.folders)
            {
                selection.remove(folder);
            }
        }))
        {
            outcomes.put(connection.root.toString(), "Couldn't remove it just now");
            show(CONNECTIONS);
            return;
        }
        int at = connections.indexOf(connection);
        connections.remove(connection);
        notice = connection.name + " removed";
        undo = () -> {
            if (LibraryChanges.apply(getContext(), selection -> {
                for (android.net.Uri folder : connection.folders)
                {
                    selection.include(folder);
                }
            }))
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
        LayoutParams statusSize = new LayoutParams(SupernovaStyle.dp(context, PILL_DP), SupernovaStyle.dp(context, 24));
        // FL6: a clear gap between a long name's "…" and its pill.
        statusSize.leftMargin = SupernovaStyle.dp(context, 16);
        row.addView(status, statusSize);
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
            // FL5 (owner, 18 Nov 2026): smaller buttons, the one small-box size (RULES §3).
            TextView button = text(action.label, 13, Color.WHITE);
            button.setGravity(Gravity.CENTER);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            int inner = SupernovaStyle.dp(context, SupernovaStyle.SMALL_BOX_SIDE_DP);
            button.setPadding(inner, 0, inner, 0);
            // Render check (19 Nov 2026): five buttons beside the pill don't fit with icons, so a row with five shows
            // the words alone.
            if (actions.size() < 5)
            {
                PreviewIcon.apply(button, action.icon, 14);
            }
            button.setContentDescription(action.label + ", " + name);
            button.setOnClickListener(view -> action.run.run());
            button.setOnFocusChangeListener((view, focused) -> row.post(restyle));
            LayoutParams size = new LayoutParams(-2, SupernovaStyle.dp(context, SupernovaStyle.SMALL_BOX_HEIGHT_DP));
            size.leftMargin = SupernovaStyle.dp(context, 4);
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

    private boolean connectionOff(Connection connection)
    {
        if (connection.root == null)
        {
            return false;
        }
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext())
            .getStringSet(com.archos.mediaprovider.video.SupernovaLibraryPolicy.CONNECTIONS_OFF,
                          java.util.Collections.emptySet())
            .contains(com.archos.mediaprovider.video.SupernovaLibraryPolicy.canonical(connection.root));
    }

    /** Turn off / Turn on (FL3): the connection's titles leave or return to the library; its settings stay. */
    private void setConnectionOff(Connection connection, boolean off)
    {
        if (connection.root == null)
        {
            return;
        }
        android.content.SharedPreferences prefs =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        java.util.Set<String> roots = new java.util.HashSet<>(prefs.getStringSet(
            com.archos.mediaprovider.video.SupernovaLibraryPolicy.CONNECTIONS_OFF, java.util.Collections.emptySet()));
        String root = com.archos.mediaprovider.video.SupernovaLibraryPolicy.canonical(connection.root);
        if (off ? !roots.add(root) : !roots.remove(root))
        {
            return;
        }
        prefs.edit().putStringSet(com.archos.mediaprovider.video.SupernovaLibraryPolicy.CONNECTIONS_OFF, roots).apply();
        getContext().getContentResolver().notifyChange(
            com.archos.mediaprovider.video.VideoStore.Video.Media.EXTERNAL_CONTENT_URI, null);
        com.archos.mediacenter.video.leanback.UndoNotice.show(getContext(),
            connection.name + (off ? " is off: its titles are hidden" : " is on again"),
            () -> setConnectionOff(connection, !off));
        show(CONNECTIONS);
        focusContent();
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
            // FL9 (owner, 18 Nov 2026): focus stays on the row pressed while the page redraws around it.
            View focused = findFocus();
            Object tag = focused == null ? null : focused.getTag();
            show(LIBRARY_CARE);
            View again = tag == null ? null : findViewWithTag(tag);
            if (again != null && again.isFocusable())
            {
                again.requestFocus();
            }
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
            actions.add(new Action("Fix match", "edit", () -> {
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
        if (event.getAction() == android.view.KeyEvent.ACTION_DOWN && tabs.openTab() == YOUR_LIBRARY
            && browser == null && (focus == null || isInside(tabs, focus)) && scrollYourLibrary(event.getKeyCode()))
        {
            return true;
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

    /**
     * Your Library is read-only (FILES.md): Down scrolls it a step while the remote stays on the tab; Up scrolls back
     * and, at the top, goes on to the top bar as usual.
     */
    private boolean scrollYourLibrary(int key)
    {
        if (key != android.view.KeyEvent.KEYCODE_DPAD_DOWN && key != android.view.KeyEvent.KEYCODE_DPAD_UP)
        {
            return false;
        }
        ScrollView scroll = null;
        for (int i = 0; i < body.getChildCount(); i++)
        {
            if (body.getChildAt(i) instanceof ScrollView)
            {
                scroll = (ScrollView) body.getChildAt(i);
            }
        }
        if (scroll == null)
        {
            return false;
        }
        int step = SupernovaStyle.dp(getContext(), ROW_HEIGHT_DP) * 3;
        if (key == android.view.KeyEvent.KEYCODE_DPAD_UP)
        {
            if (scroll.getScrollY() <= 0)
            {
                return false;
            }
            scroll.smoothScrollBy(0, -step);
            return true;
        }
        scroll.smoothScrollBy(0, step);
        View tab = tabs.tab(YOUR_LIBRARY);
        if (!tab.isFocused())
        {
            tab.requestFocus();
        }
        return true;
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
