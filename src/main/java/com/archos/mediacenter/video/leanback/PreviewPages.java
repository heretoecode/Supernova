package com.archos.mediacenter.video.leanback;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.*;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.ListUpdateCallback;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.browser.ShortcutDb;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.adapter.GenericNetworkShortcutMapper;
import com.archos.mediacenter.video.leanback.adapter.NetworkShortcutMapper;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import com.archos.mediacenter.video.leanback.network.NetworkRootActivity;
import com.archos.mediacenter.video.leanback.network.NetworkShortcutDetailsActivity;
import com.archos.mediacenter.video.leanback.network.NetworkShortcutDetailsFragment;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediacenter.video.player.PlayerService;
import com.archos.mediacenter.video.streaming.PreviewProviderIcons;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediacenter.video.utils.SortUtils;
import com.archos.mediacenter.video.utils.SubtitleDiscovery;
import com.archos.mediaprovider.video.NetworkAutoRefresh;
import com.archos.mediaprovider.video.NetworkScannerReceiver;
import com.archos.mediaprovider.video.NetworkScannerServiceVideo;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.util.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** Recycled native TV grids. Only visible artwork is decoded. Classic Browse remains untouched. */
public final class PreviewPages extends FrameLayout {
    /** Owner 2 Nov 2026: equal space above the library toolbar (to the statistics) and below it (to the separator). */
    static final int TOOLBAR_TOP_GAP_DP = 9;
    public interface Click {
        void open(Presenter.ViewHolder holder, Object item);
    }
    private final RecyclerView list;
    private final GridLayoutManager layout;
    private final PageAdapter adapter = new PageAdapter();
    private final Click click;
    private Snapshot snapshot = new Snapshot();
    /** The redesigned Files page (0.157 step 8); false brings back the old page (tests of the old screens). */
    static final String FILES_PAGE_SWITCH = "supernova_files_page";
    private com.archos.mediacenter.video.leanback.files.FilesPage filesPage;
    private com.archos.mediacenter.video.connections.NetworkDeviceDiscovery.Job networkSearch;
    private final Map<String, Boolean> reachability = new java.util.HashMap<>();
    private long reachabilityCheckedAt;
    private List<Box> files = new ArrayList<>();
    private final List<Shortcut> librarySources =
                                                                                          new ArrayList<>(),
                                                                                      savedLocations =
                                                                                          new ArrayList<>();
    private boolean sourcesLoading, sourcesLoaded;
    private int sourceGeneration;
    private boolean networkEntryPending;

    private void loadSources() {
        if (sourcesLoading || !isAttachedToWindow())
            return;
        sourcesLoading = true;
        final int sourceRequest = ++sourceGeneration;
        Context app = getContext().getApplicationContext();
        new Thread(() -> {
            List<Shortcut> indexed = new ArrayList<>(),
                                                                                saved = new ArrayList<>();
            try (Cursor c =
                     ShortcutDbAdapter.VIDEO.getAllShortcuts(app, null, null)) {
                if (c != null) {
                    NetworkShortcutMapper m =
                        new NetworkShortcutMapper();
                    m.bindColumns(c);
                    while (c.moveToNext())
                        indexed.add((Shortcut)m.bind(c));
                }
            } catch (Exception e) {
                Diagnostics.error("source_summary_unavailable", e);
            }
            try (Cursor c =
                     ShortcutDb.STATIC.getCursorAllShortcuts(app)) {
                if (c != null) {
                    GenericNetworkShortcutMapper m =
                        new GenericNetworkShortcutMapper();
                    m.bindColumns(c);
                    while (c.moveToNext())
                        saved.add((Shortcut)m.bind(c));
                }
            } catch (Exception e) {
                Diagnostics.error("saved_locations_unavailable", e);
            }
            post(() -> {
                if (sourceRequest != sourceGeneration || !isAttachedToWindow())
                    return;
                sourcesLoading = false;
                sourcesLoaded = true;
                librarySources.clear();
                librarySources.addAll(indexed);
                savedLocations.clear();
                savedLocations.addAll(saved);
                if ((tab == 3 || tab == 0) && isAttachedToWindow())
                    render("sources_loaded");
            });
        }, "SupernovaSourceSummary").start();
    }

    private void openSource(Shortcut source) {
        getContext().startActivity(
            new Intent(getContext(),
                        NetworkShortcutDetailsActivity.class)
                .putExtra(NetworkShortcutDetailsFragment.EXTRA_SHORTCUT,
                          source));
    }

    private final List<Cell> cells = new ArrayList<>();
    private HomeRowMover rowMover;
    private int tab;
    private PreviewNetworkWorkspace sharedBrowser, networkBrowser, homeSetupBrowser;
    private final FocusAnchor[] anchors = new FocusAnchor[4];
    private final Parcelable[] scrollStates = new Parcelable[4];
    /** Each tab's hero position, so coming back shows the same title (focus audit F3). */
    private final int[] featuredByTab = new int[4];
    private boolean restoringFocus, switchingTab;
    private SharedPreferences preferences;
    private int focusGeneration;

    private static final class FocusAnchor {
        String cell, child, token = "";
        int position, inner;
    }

    private String cellKey(Cell c) {
        return c.type + ":" +
            (c.type == POSTER    ? ((Entry)c.value).key()
             : c.type == STORAGE ? ((Box)c.value).getBoxId() + ":" + ((Box)c.value).getPath()
                                 : c.title);
    }

    private boolean inside(View view, View ancestor) {
        while (view != null) {
            if (view == ancestor)
                return true;
            ViewParent p = view.getParent();
            view = p instanceof View ? (View)p : null;
        }
        return false;
    }

    private final class FocusRecycler extends PreviewFocusRecycler {
        final boolean horizontal;

        FocusRecycler(Context c, boolean horizontal) {
            super(c);
            this.horizontal = horizontal;
        }

        @Override
        protected boolean focusablePosition(int p) {
            if (horizontal)
                return true;
            if (p < 0 || p >= cells.size())
                return false;
            Cell c = cells.get(p);
            return c.type != NOTICE && (c.type != HEADER || Boolean.TRUE.equals(c.value));
        }

        @Override
        public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
            int position = getChildAdapterPosition(child);
            if (!horizontal && (tab == 1 || tab == 2) && position >= 0 && position < cells.size() &&
                cells.get(position).type == POSTER) {
                scheduleCentre();
                return false;
            }
            return super.requestChildRectangleOnScreen(child, rect, immediate);
        }

        @Override
        public View focusSearch(View focused, int direction) {
            View next = super.focusSearch(focused, direction);
            // At loaded-content boundaries retain the last valid card; do not let
            // RecyclerView's ancestor search fall through to global navigation.
            if (horizontal && (direction == FOCUS_LEFT || direction == FOCUS_RIGHT) && !inside(next, this))
                return focused;
            if (!horizontal && !inside(next, PreviewPages.this) && !(direction == FOCUS_UP && atTop()))
                return focused;
            return next;
        }
    }

    private void rememberFocus() {
        if (restoringFocus || switchingTab)
            return;
        View focused = list.findFocus();
        if (focused == null)
            return;
        View item = list.findContainingItemView(focused);
        if (item == null)
            return;
        int pos = list.getChildAdapterPosition(item);
        if (pos < 0 || pos >= cells.size())
            return;
        FocusAnchor a = new FocusAnchor();
        a.cell = cellKey(cells.get(pos));
        a.position = pos;
        a.child = focused.getTag() instanceof String ? (String)focused.getTag() : null;
        if (anchors[tab] != null && a.cell.equals(anchors[tab].cell) &&
            java.util.Objects.equals(a.child, anchors[tab].child))
            a.token = anchors[tab].token;
        if (cells.get(pos).type == RAIL) {
            List<Entry> entries = (List<Entry>)cells.get(pos).value;
            for (int i = 0; i < entries.size(); i++)
                if (entries.get(i).key().equals(a.child)) {
                    a.inner = i;
                    break;
                }
        }
        anchors[tab] = a;
    }

    private Entry preparingDetails;
    private String preparingKey = "";
    private final ViewTreeObserver.OnGlobalFocusChangeListener detailsFocusChanged =
        (previous, focused) -> prepareDetails(focused);
    private final Runnable prepareFocusedDetails = () -> {
        Entry entry = preparingDetails, current = detailsFocusEntry(findFocus());
        if (isAttachedToWindow() && entry != null && current != null && entry.key().equals(current.key()) &&
            entry.onlineId == current.onlineId)
            PreviewEnrichmentQueue.focused(getContext(), entry);
    };

    private Entry detailsFocusEntry(View focused) {
        if (list == null || focused == null)
            return null;
        View item = list.findContainingItemView(focused);
        int position = item == null ? -1 : list.getChildAdapterPosition(item);
        if (position < 0 || position >= cells.size())
            return null;
        Cell cell = cells.get(position);
        if (cell.value instanceof Entry)
            return (Entry)cell.value;
        if (cell.type == RAIL && cell.value instanceof List) {
            for (View cursor = focused; cursor != null && cursor != item;
                 cursor = cursor.getParent() instanceof View ? (View)cursor.getParent() : null) {
                Object key = cursor.getTag();
                for (Entry entry : (List<Entry>)cell.value)
                    if (entry.key().equals(key))
                        return entry;
            }
        }
        return cell.type == HERO && tab == 0 ? featured() : null;
    }

    private void prepareDetails(View focused) {
        Entry entry = detailsFocusEntry(focused);
        if (entry == null || entry.onlineId <= 0) {
            removeCallbacks(prepareFocusedDetails);
            preparingDetails = null;
            if (!preparingKey.isEmpty())
                PreviewEnrichmentQueue.focused(getContext(), null);
            preparingKey = "";
            return;
        }
        String key = entry.key() + ":" + entry.onlineId;
        if (key.equals(preparingKey))
            return;
        preparingKey = key;
        preparingDetails = entry;
        removeCallbacks(prepareFocusedDetails);
        postDelayed(prepareFocusedDetails, 250);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        prepareDetails(focused);
        if (list != null) {
            rememberFocus();
            scheduleCentre();
            if (tab == 1 || tab == 2) {
                View item = list.findContainingItemView(focused);
                int p = item == null ? -1 : list.getChildAdapterPosition(item);
                if (p >= 0 && p < cells.size() && cells.get(p).value instanceof Entry) {
                    artworkKeys[tab] = ((Entry)cells.get(p).value).key();
                    lastArtwork[tab] = ((Entry)cells.get(p).value).backdrop;
                    artwork.accept(null);
                }
            }
        }
    }

    private void scheduleCentre() {
        if (list == null || tab != 1 && tab != 2)
            return;
        list.removeCallbacks(centreLibraryRow);
        list.postOnAnimation(centreLibraryRow);
    }

    private final Runnable centreLibraryRow = this::centreFocusedLibraryRow;

    private void centreFocusedLibraryRow() {
        if (tab != 1 && tab != 2 || !list.hasFocus() || list.hasPendingAdapterUpdates())
            return;
        View focus = list.findFocus(), item = focus == null ? null : list.findContainingItemView(focus);
        int position = item == null ? -1 : list.getChildAdapterPosition(item);
        if (position < 0 || position >= cells.size() || cells.get(position).type != POSTER || item.getHeight() == 0)
            return;
        int viewportBottom = list.getHeight() - dp(12);
        int desiredBottom = Math.max(dp(12), (viewportBottom - list.getPaddingTop() - item.getHeight()) / 2 + dp(12));
        if (list.getPaddingBottom() != desiredBottom) {
            list.setPadding(list.getPaddingLeft(), list.getPaddingTop(), list.getPaddingRight(), desiredBottom);
            scheduleCentre();
            return;
        }
        int delta = Math.round((item.getTop() + item.getBottom()) / 2f - (list.getPaddingTop() + viewportBottom) / 2f);
        if (Math.abs(delta) > 1 && list.canScrollVertically(delta > 0 ? 1 : -1)) {
            list.stopScroll();
            list.removeCallbacks(centreLibraryRow);
            list.smoothScrollBy(0, delta, new DecelerateInterpolator(), 180);
        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect rect) {
        if (sharedBrowser != null && sharedBrowser.isShown())
            return sharedBrowser.browser().focusOverview();
        if (restoringFocus)
            return false;
        if (anchors[tab] != null) {
            holdFocus();
            restoreFocus();
            return true;
        }
        if ((tab == 1 || tab == 2) && focusFromNavigation())
            return true;
        return super.onRequestFocusInDescendants(direction, rect);
    }

    private static boolean libraryControl(View view) {
        if (view == null || !(view.getTag() instanceof String))
            return false;
        String tag = (String)view.getTag();
        return tag.startsWith("control:") || tag.startsWith("custom.control.");
    }

    private String firstLibraryControl() {
        return customLibrary == null ? "control:0" : "custom.control.filters";
    }

    /** Navigation entry is deliberate; returning from Details uses the saved item anchor instead. */
    public boolean focusFromNavigation() {
        if (tab != 1 && tab != 2)
            return false;
        for (int i = 0; i < cells.size(); i++)
            if (cells.get(i).type == HEADER && Boolean.TRUE.equals(cells.get(i).value)) {
                FocusAnchor anchor = new FocusAnchor();
                anchor.cell = cellKey(cells.get(i));
                anchor.position = i;
                anchor.child = firstLibraryControl();
                anchors[tab] = anchor;
                holdFocus();
                layout.scrollToPositionWithOffset(0, 0);
                restoreFocus();
                return true;
            }
        return false;
    }

    private void holdFocus() {
        restoringFocus = true;
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        requestFocus();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
    }

    private View tagged(View root, String tag) {
        if (tag != null && tag.equals(root.getTag()) && root.isFocusable())
            return root;
        if (root instanceof ViewGroup) {
            ViewGroup g = (ViewGroup)root;
            for (int i = 0; i < g.getChildCount(); i++) {
                View found = tagged(g.getChildAt(i), tag);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    private void restoreFocus() {
        final int generation = ++focusGeneration;
        final FocusAnchor anchor = anchors[tab];
        int position = anchor == null ? 0 : Math.min(anchor.position, Math.max(0, cells.size() - 1));
        if (anchor != null)
            for (int i = 0; i < cells.size(); i++)
                if (cellKey(cells.get(i)).equals(anchor.cell)) {
                    position = i;
                    break;
                }
        final int target = position;
        if (layout.findViewByPosition(target) == null) {
            // Custom Library enters before its first layout. Focusing Filters (row 1)
            // must not replace the pending scroll-to-top with scroll-to-row-1,
            // which moves the read-only statistics (row 0) out of the viewport.
            boolean initialCustomHeader = customLibrary != null && target < cells.size() &&
                                          cells.get(target).type == HEADER &&
                                          Boolean.TRUE.equals(cells.get(target).value);
            if (initialCustomHeader)
                layout.scrollToPositionWithOffset(0, 0);
            else
                list.scrollToPosition(target);
        }
        list.postOnAnimation(new Runnable() {
            int attempts;
            @Override
            public void run() {
                if (generation != focusGeneration)
                    return;
                if (!hasFocus()) {
                    restoringFocus = false;
                    return;
                }
                View item = layout.findViewByPosition(target);
                if (item == null || list.hasPendingAdapterUpdates()) {
                    if (attempts++ < 8) {
                        list.postOnAnimation(this);
                        return;
                    }
                }
                if (item != null && anchor != null && target < cells.size() && cells.get(target).type == RAIL) {
                    RecyclerView rail = railIn(item);
                    List<Entry> entries = (List<Entry>)cells.get(target).value;
                    int inner = Math.min(anchor.inner, Math.max(0, entries.size() - 1));
                    for (int i = 0; i < entries.size(); i++)
                        if (entries.get(i).key().equals(anchor.child)) {
                            inner = i;
                            break;
                        }
                    if (rail.findViewHolderForAdapterPosition(inner) == null && attempts++ < 8) {
                        rail.scrollToPosition(inner);
                        list.postOnAnimation(this);
                        return;
                    }
                    RecyclerView.ViewHolder holder = rail.findViewHolderForAdapterPosition(inner);
                    if (holder != null)
                        item = holder.itemView;
                }
                View exact = item == null || anchor == null ? null : tagged(item, anchor.child);
                restoringFocus = false;
                if (exact != null)
                    exact.requestFocus();
                else if (item != null && item.requestFocus()) {
                } else
                    list.requestFocus();
                if (anchor != null && !anchor.token.isEmpty()) {
                    Diagnostics.focusRestored(
                        anchor.token, null, list.findFocus(), exact == null, list.findFocus() != null);
                    anchor.token = "";
                    if (anchors[tab] != null)
                        anchors[tab].token = "";
                }
                rememberFocus();
            }
        });
    }

    private final int[] sorts = {0, 0, 0};
    private final String[] genres = {"", "", ""};
    private final int[] years = {0, 0, 0};
    private final String[] providers = {"", "", ""};
    private final String[] selectedYears = {"", "", ""};
    private final boolean[] unmatched = {false, false, false};
    private boolean loaded;
    private final Map<String, Integer> quietOrder = new HashMap<>();
    private PreviewDiscovery discovery = new PreviewDiscovery();
    private ExecutorService worker;
    private Consumer<Uri> artwork = uri -> {
    };
    private final boolean[] ascending = {false, false, false};
    private final boolean[] listMode = {false, false, false};
    // 0.158 library page filters (page sheet LIBRARY.md): Watched, a year range and Match Any / All for genres.
    private final int[] watchedFilter = {0, 0, 0};
    private final int[] yearFrom = {0, 0, 0};
    private final int[] yearTo = {0, 0, 0};
    private final boolean[] genreAll = {false, false, false};
    /** Each started show's next episode, for its poster caption ("S2 E3 · 18 of 30 left"). */
    private final Map<Long, Entry> nextEpisodes = new HashMap<>();
    /** Your own page before it is set up ("Make this page your own"). */
    private boolean customUnset;
    /** Titles just removed from the library: they leave the page at once (Undo brings them back). */
    private final Set<String> removedKeys = new HashSet<>();
    private int featuredIndex;
    private PreviewFeaturedIndicators featuredIndicators;
    private int featuredDirection;
    private boolean featuredMoving;
    private final HeroRotationPolicy.Clock heroClock = new HeroRotationPolicy.Clock();
    private boolean heroOwnsHorizontal;
    private int carouselGeneration;

    private void changeFeatured(int direction) {
        changeFeatured(direction, true);
    }

    private void changeFeatured(int direction, boolean remote) {
        if (remote)
            recordRemoteInput();
        if (featuredCandidates().size() < 2)
            return;
        // Input commits selection immediately. Only the incoming container animates;
        // cached inner cards must never retain an interrupted outgoing translation.
        carouselGeneration++;
        featuredMoving = false;
        featuredIndex += direction;
        featuredDirection = direction;
        renderFeatured();
    }

    private final Uri[] lastArtwork = new Uri[4];
    private final String[] artworkKeys = new String[4];
    private final Map<String, String> featuredReasons = new HashMap<>();
    private Snapshot heroPoolSnapshot;
    private PreviewDiscovery heroPoolDiscovery;
    private String heroPoolPreferences = "";
    private long heroPoolGeneration = -1;
    private List<Entry> heroPool = Collections.emptyList();

    private boolean eligibleFeatured(Entry entry) {
        if (!(entry.media instanceof Movie || entry.media instanceof Tvshow) || entry.heroSequential ||
            !entry.heroEligible || HeroPlaybackEligibility.consumed(entry))
            return false;
        if (entry.media instanceof Video) {
            Video video = (Video)entry.media;
            if (LibraryHealth.state(getContext(), video) != LibraryHealth.State.AVAILABLE ||
                PreviewLibraryLoader.watched(video) || video.getResumeMs() > 0)
                return false;
        }
        for (List<Entry> excluded :
             Arrays.asList(snapshot.watched, snapshot.continuingMovies, snapshot.continuingShows))
            for (Entry watched : excluded)
                if (watched.key().equals(entry.key()))
                    return false;
        if (entry.media instanceof Tvshow)
            for (Entry episode : snapshot.episodes)
                if (episode.show == entry.show &&
                    (PreviewLibraryLoader.watched((Video)episode.media) || ((Video)episode.media).getResumeMs() > 0))
                    return false;
        return true;
    }

    private List<HeroRotationPolicy.Candidate<Entry>> heroSource(List<Entry> entries) {
        List<HeroRotationPolicy.Candidate<Entry>> result = new ArrayList<>();
        for (Entry entry : entries)
            if (entry.media instanceof Movie || entry.media instanceof Tvshow)
                result.add(new HeroRotationPolicy.Candidate<>(HeroPlaybackEligibility.identity(entry),
                                                              entry.media instanceof Tvshow, entry));
        return result;
    }

    private List<Entry> featuredCandidates() {
        if (!SupernovaLibraryPolicy.configured(getContext()))
            return Collections.emptyList();
        String choices = (preferences == null || preferences.getBoolean("preview_featured_recent", true)) + ":" +
                         (preferences != null && preferences.getBoolean("preview_featured_trending", true)) + ":" +
                         (preferences != null && preferences.getBoolean("preview_featured_popular", true));
        long generation = HeroPlaybackEligibility.generation();
        if (heroPoolSnapshot == snapshot && heroPoolDiscovery == discovery && choices.equals(heroPoolPreferences) &&
            generation == heroPoolGeneration)
            return heroPool;
        heroPoolSnapshot = snapshot;
        heroPoolDiscovery = discovery;
        heroPoolPreferences = choices;
        heroPoolGeneration = generation;
        featuredReasons.clear();
        List<Entry> recent = new ArrayList<>(snapshot.movies);
        recent.addAll(snapshot.shows);
        recent.sort(Comparator.comparingLong((Entry entry) -> entry.added)
                        .reversed()
                        .thenComparing(HeroPlaybackEligibility::identity));
        Entry seed = snapshot.played.isEmpty() ? null : snapshot.played.get(0);
        List<List<HeroRotationPolicy.Candidate<Entry>>> sources = Arrays.asList(
            heroSource(preferences == null || preferences.getBoolean("preview_featured_recent", true)
                           ? recent
                           : Collections.emptyList()),
            heroSource(preferences != null && preferences.getBoolean("preview_featured_trending", true)
                           ? discovery.matches(snapshot, true)
                           : Collections.emptyList()),
            heroSource(preferences != null && preferences.getBoolean("preview_featured_popular", true)
                           ? discovery.matches(snapshot, false)
                           : Collections.emptyList()),
            heroSource(seed == null ? Collections.emptyList() : PreviewDiscovery.similar(seed, snapshot)));
        List<Entry> result = new ArrayList<>();
        String[] reasons = {"Recently Added", "TRENDING ON TRAKT · IN YOUR LIBRARY",
                            "POPULAR ON TRAKT · IN YOUR LIBRARY", "Because You Watched", "Recently Added"};
        for (HeroRotationPolicy.Selection<Entry> chosen :
             HeroRotationPolicy.select(sources, heroSource(recent), candidate -> eligibleFeatured(candidate.value))) {
            Entry entry = chosen.candidate.value;
            result.add(entry);
            featuredReasons.put(entry.key(), reasons[chosen.source]);
        }
        heroPool = Collections.unmodifiableList(result);
        return heroPool;
    }

    private final PreviewLibraryColumns[] columns = new PreviewLibraryColumns[3];
    private long lastInteraction;

    private boolean motionAllowed() {
        return Build.VERSION.SDK_INT < 26 || ValueAnimator.areAnimatorsEnabled();
    }

    public void recordRemoteInput() {
        lastInteraction = SystemClock.elapsedRealtime();
        heroClock.input(lastInteraction);
        removeCallbacks(rotateFeatured);
        if (tab == 0 && isAttachedToWindow())
            postDelayed(rotateFeatured, HeroRotationPolicy.IDLE_MS);
    }

    private void startHeroClock() {
        removeCallbacks(rotateFeatured);
        if (tab == 0 && isAttachedToWindow()) {
            heroClock.resume(SystemClock.elapsedRealtime());
            postDelayed(rotateFeatured, HeroRotationPolicy.IDLE_MS);
        } else
            heroClock.suspend();
    }

    private final Runnable rotateFeatured = new Runnable() {
        @Override
        public void run() {
            if (!isAttachedToWindow())
                return;
            long now = SystemClock.elapsedRealtime();
            boolean visible = tab == 0 && hasWindowFocus() && isShown() && !list.canScrollVertically(-1);
            int count = featuredCandidates().size();
            if (heroClock.due(now, visible, motionAllowed(), count))
                changeFeatured(1, false);
            postDelayed(this, visible && motionAllowed() && count > 1 ? Math.max(200, heroClock.delay(now))
                                                                      : HeroRotationPolicy.INTERVAL_MS);
        }
    };

    @Override
    public void onWindowFocusChanged(boolean focused) {
        super.onWindowFocusChanged(focused);
        if (focused)
        {
            startHeroClock();
            post(this::recoverLostFocus);
            // Once more after the page has re-laid itself out (the snapshot may refresh on return).
            postDelayed(this::recoverLostFocus, 400);
        }
        else {
            heroOwnsHorizontal = false;
            heroClock.suspend();
            removeCallbacks(rotateFeatured);
            carouselGeneration++;
            featuredMoving = false;
            View hero = list.findViewWithTag("semantic:featured.active");
            if (hero != null) {
                hero.animate().cancel();
                hero.setTranslationX(0);
            }
        }
    }

    private boolean reopenPending;
    private int reopenAttempts;

    /** The place to save for reopening: the current page's focused item, if any. */
    public ReopenMemory.Place reopenPlace() {
        rememberFocus();
        FocusAnchor anchor = anchors[tab];
        if (anchor == null)
        {
            return new ReopenMemory.Place(tab, null, null, 0, 0);
        }
        return new ReopenMemory.Place(tab, anchor.cell, anchor.child, anchor.position, anchor.inner);
    }

    /** Call after setTab: focus returns to this item once the page has its content. */
    public void reopenAt(ReopenMemory.Place place) {
        if (place == null || place.tab != tab || place.cell == null)
        {
            return;
        }
        FocusAnchor anchor = new FocusAnchor();
        anchor.cell = place.cell;
        anchor.child = place.child;
        anchor.position = Math.max(0, place.position);
        anchor.inner = Math.max(0, place.inner);
        anchors[tab] = anchor;
        reopenPending = true;
        reopenAttempts = 0;
        if (loaded)
        {
            post(this::finishReopen);
        }
    }

    private void finishReopen() {
        if (!reopenPending)
        {
            return;
        }
        if (!hasComposedContent() && reopenAttempts++ < 60)
        {
            postOnAnimation(this::finishReopen);
            return;
        }
        reopenPending = false;
        if (anchors[tab] == null || !isShown())
        {
            return;
        }
        Diagnostics.event("focus_reopened", "page", "library_tab_" + tab);
        requestFocus();
    }

    /** After standby the window can come back with nothing focused: return to the remembered card. */
    private void recoverLostFocus() {
        // Back from a title (D17): focus can come back to nothing, or to this page or its list themselves, which
        // shows no box and lets Down fall into the top bar. Either way, return to the remembered card.
        View current = getRootView().findFocus();
        boolean lost = current == null || current == this || current == list;
        if (!isShown() || !hasWindowFocus() || !lost || cells.isEmpty() || tab == 3)
        {
            return;
        }
        Diagnostics.event("focus_recovered", "page", "library_tab_" + tab, "had_anchor", anchors[tab] != null);
        list.requestFocus();
        restoreFocus();
    }

    private Runnable ready = () -> {
    };
    private final Runnable prioritiseVisible = this::prioritiseVisibleEntries;

    private void prioritiseVisibleEntries() {
        if (!loaded || !isAttachedToWindow() || tab == 3 ||
            !SupernovaLibraryPolicy.configured(getContext()))
            return;
        List<Entry> visible = new ArrayList<>(), next = new ArrayList<>();
        int last = -1;
        for (int n = 0; n < list.getChildCount(); n++) {
            int position = list.getChildAdapterPosition(list.getChildAt(n));
            if (position < 0 || position >= cells.size())
                continue;
            last = Math.max(last, position);
            Cell cell = cells.get(position);
            if (cell.value instanceof Entry)
                visible.add((Entry)cell.value);
            else if (cell.type == RAIL) {
                View item = list.getChildAt(n);
                if (railIn(item) != null)
                    collectVisibleRail(railIn(item), (List<Entry>)cell.value, visible, next);
            }
        }
        for (int n = last + 1; n < Math.min(cells.size(), last + 13); n++)
            if (cells.get(n).value instanceof Entry)
                next.add((Entry)cells.get(n).value);
        PreviewEnrichmentQueue.visible(getContext(), visible, next);
    }

    static void collectVisibleRail(RecyclerView rail, List<Entry> entries, List<Entry> visible, List<Entry> next) {
        int last = -1;
        for (int i = 0; i < rail.getChildCount(); i++) {
            View child = rail.getChildAt(i);
            int position = rail.getChildAdapterPosition(child);
            if (position >= 0 && position < entries.size() && child.getRight() > rail.getPaddingLeft() &&
                child.getLeft() < rail.getWidth() - rail.getPaddingRight()) {
                visible.add(entries.get(position));
                last = Math.max(last, position);
            }
        }
        if (last >= 0)
            next.addAll(entries.subList(last + 1, Math.min(entries.size(), last + 7)));
    }

    private void scheduleVisibleEnrichment() {
        removeCallbacks(prioritiseVisible);
        postDelayed(prioritiseVisible, 300);
    }

    public boolean hasComposedContent() {
        return loaded && !list.isComputingLayout() && list.getChildCount() > 0 && list.getWidth() > 0;
    }

    private boolean visibleArtworkReady(View view) {
        if (view instanceof TextView && !OfficialTitleArtwork.readyForFirstFrame((TextView)view))
            return false;
        if (view instanceof PreviewCardPresenter.Card)
            return ((PreviewCardPresenter.Card)view).artworkReady;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child.getVisibility() == VISIBLE && !visibleArtworkReady(child))
                    return false;
            }
        }
        return true;
    }

    public void setReadyListener(Runnable listener) {
        ready = listener;
        if (loaded)
            post(ready);
    }

    public boolean hasLoadedSnapshot() {
        return loaded;
    }

    public void setArtworkListener(Consumer<Uri> listener) {
        artwork = listener;
        updateArtwork();
    }

    public void setDiscovery(PreviewDiscovery value) {
        requestedDiscovery = true;
        discovery = value;
        render("discovery_update");
    }

    private boolean requestedDiscovery;
    private final Runnable providerRefresh = () -> {
        if (isAttachedToWindow() && (tab == 1 || tab == 2) && !providers[tab].isEmpty()) {
            Diagnostics.event("library_filter_refresh", "reason",
                                                                       "provider_cache_changed", "tab", tab);
            render("provider_cache_changed"); // Diff the existing snapshot; never re-query the media library here.
        }
    };

    static boolean providerCacheAffectsPage(String key, int tab, String country, boolean filtered) {
        return filtered && key != null && (tab == 1 || tab == 2) &&
            key.startsWith("streaming_known_at:" + (tab == 1 ? "movie:" : "tv:")) &&
            key.endsWith(":" + country + ":-1");
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener providerSettings =
        (prefs, key) -> {
        if (key == null)
            return;
        boolean scope = key.equals("streaming_country") || key.equals("streaming_enabled");
        if (scope)
            PreviewEnrichmentQueue.library(getContext(), snapshot, tab);
        if (scope || key.startsWith("streaming_providers_") ||
            providerCacheAffectsPage(key, tab,
                                     StreamingRepository.country(getContext()),
                                     tab > 0 && tab < 3 && !providers[tab].isEmpty())) {
            removeCallbacks(providerRefresh);
            postDelayed(providerRefresh, 500);
        }
    };
    private boolean homeBuilding;

    private boolean emptyHome() {
        return snapshot.movies.isEmpty() && snapshot.shows.isEmpty() && snapshot.recent.isEmpty();
    }

    private final Runnable importRefresh = new Runnable() {
        @Override
        public void run() {
            if (!isAttachedToWindow() || !loaded || !emptyHome())
                return;
            if (tab == 0 && PreviewLibraryScan.building() != homeBuilding)
                render("first_import_state_changed");
            else
                postDelayed(this, 1000);
        }
    };
    private final SharedPreferences.OnSharedPreferenceChangeListener homeSettings = (prefs, key) -> {
        if (key != null && (key.startsWith("preview_featured_") || key.equals("preview_home_rows41") ||
                            key.equals("preview_accent41") || key.equals("hide_watched") ||
                            key.equals("sort_ignore_articles") || key.equals("supernova_onboarding_complete")))
            post(() -> {
                if (isAttachedToWindow()) {
                    if (key.equals("hide_watched") || key.equals("sort_ignore_articles"))
                        quietOrder.clear();
                    render("home_settings_changed");
                }
            });
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(detailsFocusChanged);
        prepareDetails(findFocus());
        post(this::showWelcome);
        if (tab == 3 || loaded && snapshot.technical.isEmpty())
            loadSources();
        preferences.registerOnSharedPreferenceChangeListener(homeSettings);
        preferences.registerOnSharedPreferenceChangeListener(providerSettings);
        requestDiscovery();
        startHeroClock();
        post(importRefresh);
    }

    private void requestDiscovery() {
        if (!SupernovaLibraryPolicy.configured(getContext()) || requestedDiscovery ||
            !isAttachedToWindow() || snapshot.movies.isEmpty() && snapshot.shows.isEmpty())
            return;
        requestedDiscovery = true;
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            try {
                PreviewDiscovery result = PreviewDiscovery.load(getContext().getApplicationContext());
                post(() -> {
                    if (isAttachedToWindow())
                        setDiscovery(result);
                });
            } finally {
                worker.shutdown();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        if (networkSearch != null) {
            networkSearch.close();
            networkSearch = null;
        }
        if (getViewTreeObserver().isAlive())
            getViewTreeObserver().removeOnGlobalFocusChangeListener(detailsFocusChanged);
        removeCallbacks(prepareFocusedDetails);
        preparingDetails = null;
        preparingKey = "";
        PreviewEnrichmentQueue.focused(getContext(), null);
        sourceGeneration++;
        sourcesLoading = false;
        removeCallbacks(warmHeroes);
        for (PreviewFeaturedCard card : heroWindow.values())
            card.releaseArtwork();
        heroWindow.clear();
        removeCallbacks(rotateFeatured);
        heroClock.suspend();
        heroOwnsHorizontal = false;
        carouselGeneration++;
        removeCallbacks(providerRefresh);
        removeCallbacks(importRefresh);
        if (worker != null)
            worker.shutdownNow();
        preferences.unregisterOnSharedPreferenceChangeListener(homeSettings);
        preferences.unregisterOnSharedPreferenceChangeListener(providerSettings);
        super.onDetachedFromWindow();
    }

    private Entry featured() {
        List<Entry> entries = tab == 1 || tab == 2 ? filtered() : featuredCandidates();
        return entries.isEmpty() ? null
                                 : entries.get(tab == 1 || tab == 2 ? 0 : Math.floorMod(featuredIndex, entries.size()));
    }

    private void updateArtwork() {
        if (tab == 1 || tab == 2 || tab == 6) {
            artwork.accept(null);
            return;
        }
        if ((tab == 1 || tab == 2) && loaded) {
            if (artworkKeys[tab] != null) {
                for (Entry selected : filtered())
                    if (artworkKeys[tab].equals(selected.key())) {
                        lastArtwork[tab] = selected.backdrop;
                        artwork.accept(selected.backdrop);
                        return;
                    }
                artworkKeys[tab] = null;
                lastArtwork[tab] = null;
            }
            Entry first = featured();
            artwork.accept(first == null ? null : first.backdrop);
            return;
        }
        Entry entry = featured();
        artwork.accept(tab == 0 || tab == 3 || entry == null ? null : entry.backdrop);
    }

    public static String displayName(Entry e) {
        return e.media instanceof Episode ? ((Episode)e.media).getShowName() : e.media.getName();
    }

    private void open(Entry e, View v) {
        rememberFocus();
        if (anchors[tab] != null)
            anchors[tab].token = Diagnostics.focusEntry(v, "details");
        click.open(new Presenter.ViewHolder(v), e.media);
    }

    private void play(Entry e, View v) {
        if (e.media instanceof Tvshow) {
            List<Entry> episodes = new ArrayList<>();
            for (Entry ep : snapshot.episodes)
                if (ep.show == e.show)
                    episodes.add(ep);
            Entry next = PreviewSeriesJourney.select(getContext(), episodes).episode;
            if (next != null) {
                play(next, v);
                return;
            }
        }
        if (e.media instanceof Video && getContext() instanceof Activity) {
            PlayUtils.startVideo(
                (Activity)getContext(), (Video)e.media,
                PlayerActivity.RESUME_FROM_LAST_POS, false, -1, null, -1);
        } else
            open(e, v);
    }

    private static final int HEADER = 0, POSTER = 1, RAIL = 2, STORAGE = 3, HERO = 4, NOTICE = 5, LIST = 6, SCAN = 7,
                             CUSTOMISE = 8, NETWORK_PANEL = 9, NETWORK = 10, HEALTH = 11, PLACEHOLDERS = 12;

    static class Cell {
        int type;
        String title;
        Object value;
        String signature = "", homeRowId = "", headingIcon = "";
        int viewType;

        Cell(int type, String title, Object value) {
            this.type = type;
            this.title = title;
            this.value = value;
        }
    }

    public PreviewPages(Context c, Click click) {
        super(c);
        this.click = click;
        setBackgroundColor(Color.TRANSPARENT);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        list = new FocusRecycler(c, false);
        // No scroll bars anywhere (owner, 17 Nov 2026; F4, F36).
        list.setVerticalScrollBarEnabled(false);
        list.setHorizontalScrollBarEnabled(false);
        list.setClipChildren(false);
        list.setClipToPadding(false);
        setClipChildren(false);
        list.setPadding(TvLayout.sideMargin(c), dp(10), TvLayout.sideMargin(c), dp(12));
        // 840 spans divide into 4, 5, 6, 7 and 8 columns (library pages: seven posters a row).
        layout = new GridLayoutManager(c, SPANS) {
            @Override
            public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child,
                                               View focused) {
                int position = parent.getChildAdapterPosition(child);
                // RecyclerView calls the layout manager directly for focus scrolling. Keep
                // the read-only statistics above Filters when entering either library.
                if ((tab == 1 || tab == 2) && position >= 0 && position < cells.size()) {
                    if (cells.get(position).type == HEADER && Boolean.TRUE.equals(cells.get(position).value))
                        return true;
                }
                return super.onRequestChildFocus(parent, state, child, focused);
            }
        };
        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int p) {
                return cells.get(p).type == POSTER   ? (tab < 3 && listMode[tab] ? SPANS : SPANS / libraryColumns())
                : cells.get(p).type == NETWORK_PANEL ? SPANS / 3
                : cells.get(p).type == STORAGE       ? SPANS / 4
                                                     : SPANS;
            }
        });
        adapter.setHasStableIds(true);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rv, int state) {
                if (state == RecyclerView.SCROLL_STATE_IDLE)
                    scheduleCentre();
            }
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                notifyScroll();
                scheduleVisibleEnrichment();
            }
        });
        list.setLayoutManager(layout);
        list.setAdapter(adapter);
        rowMover().watchFirstCards(() -> tab == 0);
        list.setItemAnimator(null);
        Diagnostics.uiRebuild(list, "library.adapter", "adapter_created", 0,
                                                                       adapter.getItemCount(), true);
        addView(list, new FrameLayout.LayoutParams(-1, -1));
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        if (preferences.getBoolean("remember_library_views", true))
            for (int i = 1; i <= 2; i++) {
                String k = "preview_library_" + i + "_";
                sorts[i] = Math.max(0, Math.min(4, preferences.getInt(k + "sort", 0)));
                genres[i] = preferences.getString(k + "genre", "");
                years[i] = preferences.getInt(k + "year", 0);
                selectedYears[i] = preferences.getString(k + "years", years[i] == 0 ? "" : String.valueOf(years[i]));
                providers[i] = preferences.getString(k + "providers", "");
                ascending[i] = preferences.getBoolean(k + "ascending", false);
                listMode[i] = preferences.getBoolean(k + "list", false);
                watchedFilter[i] = preferences.getInt(k + "watched", 0);
                yearFrom[i] = preferences.getInt(k + "yearFrom", 0);
                yearTo[i] = preferences.getInt(k + "yearTo", 0);
                genreAll[i] = preferences.getBoolean(k + "genreAll", false);
            }
        columns[1] = new PreviewLibraryColumns(c, false);
        columns[2] = new PreviewLibraryColumns(c, true);
        render("initial");
        if (PreviewLibraryLoader.memoryCache() == null) {
            ExecutorService cacheWorker = Executors.newSingleThreadExecutor();
            cacheWorker.execute(() -> {
                try {
                    Snapshot previous = PreviewLibraryLoader.readCache(c.getApplicationContext());
                    post(() -> {
                        if (!loaded && previous != null)
                            setSnapshot(previous);
                    });
                } finally {
                    cacheWorker.shutdown();
                }
            });
        }
    }

    private Consumer<Boolean> scrollListener = value -> {
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != oldh)
            post(() -> {
                scheduleCentre();
                if (tab == 0 && !cells.isEmpty() && cells.get(0).type == HERO)
                    renderFeatured();
            });
    }

    private int navigationInset;

    public void setNavigationInset(int inset) {
        navigationInset = inset;
        int side = TvLayout.sideMargin(getContext());
        list.setPadding(side, inset + (tab == 1 || tab == 2 ? 0 : dp(10)), side, dp(12));
    }

    public void setScrollListener(Consumer<Boolean> listener) {
        scrollListener = listener;
        notifyScroll();
    }

    private java.util.function.IntConsumer scrollOffsetListener = offset -> {
    };

    /** The page's real scroll distance in pixels (0 at the top), for the top bar's fade. */
    public void setScrollOffsetListener(java.util.function.IntConsumer listener) {
        scrollOffsetListener = listener;
        notifyScroll();
    }

    private void notifyScroll() {
        scrollListener.accept(list.canScrollVertically(-1));
        scrollOffsetListener.accept(Math.max(0, list.computeVerticalScrollOffset()));
    }

    public boolean atTop() {
        View focused = list.findFocus();
        if (focused == null)
            return isFocused() && !restoringFocus;
        View item = list.findContainingItemView(focused);
        if (item == null)
            return false;
        int p = list.getChildAdapterPosition(item);
        if (p < 0 || p >= cells.size())
            return false;
        Cell cell = cells.get(p);
        if (tab == 0)
            return cell.type == HERO && item.getTop() >= list.getPaddingTop() ||
                cell.type == CUSTOMISE && cells.stream().noneMatch(c -> c.type == HERO) &&
                    !list.canScrollVertically(-1);
        if (tab == 1 || tab == 2)
            return cell.type == HEADER && Boolean.TRUE.equals(cell.value) && libraryControl(focused) ||
                cell.type == HERO && item.getTop() >= list.getPaddingTop();
        if (cell.type == NETWORK) {
            View cursor = focused;
            while (cursor != null && cursor != item) {
                if (cursor instanceof PreviewNetworkWorkspace)
                    return ((PreviewNetworkWorkspace)cursor).atTop();
                // Up from Files' tab row reaches the top bar (F1); from its content it goes to the tabs.
                if (cursor instanceof com.archos.mediacenter.video.leanback.files.FilesPage)
                    return ((com.archos.mediacenter.video.leanback.files.FilesPage)cursor).atTop();
                cursor = cursor.getParent() instanceof View ? (View)cursor.getParent() : null;
            }
            return false;
        }
        if (cell.type == NETWORK_PANEL)
            return p <= 3 && !list.canScrollVertically(-1);
        if (cell.type == SCAN)
            return !list.canScrollVertically(-1);
        if (cell.type != STORAGE)
            return false;
        int first = 0;
        while (first < cells.size() && cells.get(first).type != STORAGE)
            first++;
        return cells.stream().noneMatch(c -> c.type == SCAN) && first < cells.size() &&
            layout.getSpanSizeLookup().getSpanGroupIndex(p, layout.getSpanCount()) ==
                layout.getSpanSizeLookup().getSpanGroupIndex(first, layout.getSpanCount()) &&
            !list.canScrollVertically(-1);
    }

    public boolean browserBack(Runnable leave) {
        // Back from the top bar while Files is inside a folder goes up a level there, never out of the app (F10).
        if (tab == 3 && filesPage != null && filesPage.isShown() && filesPage.back())
            return true;
        return sharedBrowser != null && sharedBrowser.isShown() && sharedBrowser.browser().back(leave);
    }

    public void setTab(int tab) {
        // Files opens on This Device, at its start, every time it is chosen from the top bar (F2).
        if (tab == 3 && this.tab != 3 && filesPage != null)
            filesPage.reset();
        networkEntryPending = tab == 3;
        if (tab == 3)
            loadSources();
        if (this.tab == tab) {
            if (tab == 3)
                requestNetworkEntryFocus();
            return;
        }
        rememberFocus();
        scrollStates[this.tab] = layout.onSaveInstanceState();
        if (this.tab >= 0 && this.tab < featuredByTab.length)
            featuredByTab[this.tab] = featuredIndex;
        // TopNavigation owns the single full-viewport background, including the header.
        quietOrder.clear();
        switchingTab = true;
        heroOwnsHorizontal = false;
        carouselGeneration++;
        featuredMoving = false;
        this.tab = tab;
        startHeroClock();
        setBackground(null);
        featuredIndex = tab >= 0 && tab < featuredByTab.length ? featuredByTab[tab] : 0;
        render("tab_change");
        switchingTab = false;
        if (scrollStates[tab] != null)
            layout.onRestoreInstanceState(scrollStates[tab]);
        else
            list.scrollToPosition(0);
        PreviewMetadata.library(getContext(), snapshot, tab);
    }

    private void requestNetworkEntryFocus() {
        if (!networkEntryPending || tab != 3 || !isAttachedToWindow())
            return;
        if (sharedBrowser != null && sharedBrowser.isShown()) {
            networkEntryPending = false;
            sharedBrowser.browser().enterNetworkLanding();
        }
    }

    public void setSnapshot(Snapshot s) {
        if (s == null)
            return;
        String nextSignature = snapshotSignature(s);
        if (loaded && nextSignature.equals(snapshotSignature)) {
            // A scan that changed nothing (e.g. Put.io's frequent checks) must not rebuild or re-lay out the page:
            // that caused the stalls and the page "jumping" seen in the 13 Nov diagnostics.
            Diagnostics.event("ui_rebuild_skipped", "surface", tab == 0 ? "home" : "library", "reason",
                              "unchanged_snapshot");
            return;
        }
        snapshotSignature = nextSignature;
        quietOrder.clear();
        String featuredKey =
            loaded && tab == 0 && featured() != null ? HeroPlaybackEligibility.identity(featured()) : null;
        snapshot = s;
        loaded = true;
        if (featuredKey != null) {
            List<Entry> pool = featuredCandidates();
            for (int i = 0; i < pool.size(); i++)
                if (HeroPlaybackEligibility.identity(pool.get(i)).equals(featuredKey)) {
                    featuredIndex = i;
                    break;
                }
        }
        if (tab == 0)
            SubtitleDiscovery.home(getContext(), s);
        render("indexed_snapshot");
        if (SupernovaLibraryPolicy.configured(getContext())) {
            PreviewEnrichmentQueue.library(getContext(), s, tab);
            PreviewMetadata.library(getContext(), s, tab);
            postDelayed(this::requestDiscovery, 750);
        }
        post(ready);
        post(this::finishReopen);
    }

    private static void keepOrder(List<Entry> old, List<Entry> current) {
        Map<String, Integer> rank = new HashMap<>();
        for (Entry e : old)
            rank.put(e.key(), rank.size());
        current.sort(Comparator.comparingInt(e -> rank.getOrDefault(e.key(), Integer.MAX_VALUE)));
    }

    public void setFiles(List<Box> f) {
        boolean same = f.size() == files.size();
        for (int i = 0; same && i < f.size(); i++)
            same = f.get(i).getBoxId() == files.get(i).getBoxId() &&
                   Objects.equals(f.get(i).getName(), files.get(i).getName()) &&
                   Objects.equals(f.get(i).getPath(), files.get(i).getPath());
        if (same)
            return;
        files = f;
        if (sharedBrowser != null)
            sharedBrowser.browser().locations(files, librarySources, savedLocations, this::networkSection);
        if (tab == 3 || tab == 0 && snapshot.technical.isEmpty())
            render("storage_changed");
    }

    /**
     * Approved empty page (0.156 #43, mock-up suggestions/07): one calm line, why the page is empty and one useful
     * button. Rows and pages are never silently blank.
     */
    static final class EmptyPage {
        final String reason;
        final String button;
        final String icon;
        final Runnable action;
        /** A quiet line under the button ("More options for your library are in Files and Settings."). */
        String note = "";

        EmptyPage(String reason, String button, String icon, Runnable action) {
            this.reason = reason;
            this.button = button;
            this.icon = icon;
            this.action = action;
        }

        @Override
        public String toString() {
            return reason + "|" + button + "|" + note;
        }
    }

    /** "Make this page your own" (sheet; RULES §23): the same Customise page button the page keeps once set up. */
    private EmptyPage unsetPage() {
        EmptyPage page = new EmptyPage("", "Customise page", "edit", () -> {
            if (customEdit != null)
                customEdit.run();
        });
        page.note = "More options for your library are in Files and Settings.";
        return page;
    }

    /** Add your videos: Files (This Device) through the top bar, so it opens as the Files page itself. */
    private void openFiles() {
        View files = getRootView() == null ? null : getRootView().findViewWithTag("semantic:topnav.network");
        if (files != null)
            files.performClick();
    }

    /** Started shows' next episodes, by show, for the posters' second line. */
    private void refreshNextEpisodes() {
        nextEpisodes.clear();
        for (Entry episode : snapshot.continuingShows)
            if (episode.media instanceof Episode && episode.show > 0)
                nextEpisodes.put(episode.show, episode);
    }

    private boolean filtersActive() {
        return !genres[tab].isEmpty() || years[tab] != 0 || !selectedYears[tab].isEmpty() || !providers[tab].isEmpty()
            || watchedFilter[tab] != 0 || yearFrom[tab] > 0 || yearTo[tab] > 0;
    }

    EmptyPage emptyPage() {
        if (customLibrary != null && customEdit != null)
            return new EmptyPage("No titles in your library match this page’s choices.", "Edit Page", "edit",
                                 customEdit);
        if (filtersActive())
            return new EmptyPage(!providers[tab].isEmpty()
                                     ? "Nothing in your library is known to be on the services you chose."
                                     : "No titles match the filters you chose.",
                                 "Clear filters", "close", () -> {
                                     genres[tab] = "";
                                     years[tab] = 0;
                                     selectedYears[tab] = "";
                                     providers[tab] = "";
                                     render("filters_cleared");
                                 });
        return new EmptyPage(tab == 2 ? "Your library has no TV shows yet." : "Your library has no films yet.",
                             "Add your videos", "plus", this::openFiles);
    }

    private void bindEmptyPage(LinearLayout v, String title, EmptyPage page) {
        v.setOrientation(LinearLayout.VERTICAL);
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        v.setPadding(0, dp(48), 0, dp(48));
        v.setTag("library.empty");
        ImageView icon = new ImageView(getContext());
        if ("ring".equals(page.icon)) {
            // The loading ring (RULES §21): the first scan is running and nothing is found yet.
            com.archos.mediacenter.video.foundation.DoubleRingDrawable ring =
                new com.archos.mediacenter.video.foundation.DoubleRingDrawable();
            icon.setImageDrawable(ring);
            ring.start();
        } else {
            icon.setImageDrawable(new PreviewIcon(page.button == null ? "empty" : page.icon));
        }
        v.addView(icon, new LinearLayout.LayoutParams(dp(26), dp(26)));
        TextView heading = text(title, 21);
        heading.setTextColor(SupernovaStyle.TEXT);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, dp(12), 0, dp(6));
        v.addView(heading);
        if (!page.reason.isEmpty()) {
            TextView reason = text(page.reason, 13);
            reason.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            reason.setGravity(Gravity.CENTER);
            reason.setPadding(0, 0, 0, dp(14));
            v.addView(reason);
        } else {
            v.addView(new View(getContext()), new LinearLayout.LayoutParams(1, dp(8)));
        }
        if (page.button == null)
            return;
        TextView button = TvButton.create(getContext(), page.button, page.action, false);
        PreviewIcon.apply(button, page.icon, 14);
        button.setTag("library.empty.action");
        v.addView(button, new LinearLayout.LayoutParams(-2, -2));
        if (!page.note.isEmpty()) {
            TextView note = text(page.note, 12);
            note.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            note.setGravity(Gravity.CENTER);
            note.setPadding(0, dp(14), 0, 0);
            v.addView(note);
        }
    }

    private void header(String title, boolean controls) {
        cells.add(new Cell(HEADER, title, controls));
    }

    private void rail(String title, List<Entry> items) {
        if (items.isEmpty())
            return;
        header(title, false);
        cells.add(new Cell(RAIL, title, new ArrayList<>(items.subList(0, items.size()))));
    }

    private void persistViews() {
        if (preferences == null || !preferences.getBoolean("remember_library_views", true) || tab < 1 || tab > 2)
            return;
        if (customLibrary != null && (customEditing || customUnset))
            return; // a page not set up yet is never stored by looking at it
        if (customLibrary != null) {
            customLibrary.view = listMode[tab] ? "list" : "grid";
            customLibrary.sort = sorts[tab] == 1 ? "title" : sorts[tab] == 2 ? "year" : "added";
            customLibrary.descending = !ascending[tab];
            customLibrary.save(getContext());
            preferences.edit()
                .putString("preview_custom_genres", genres[tab])
                .putString("preview_custom_years", selectedYears[tab])
                .putString("preview_custom_providers", providers[tab])
                .apply();
            return;
        }
        String k = "preview_library_" + tab + "_";
        preferences.edit()
            .putInt(k + "sort", sorts[tab])
            .putString(k + "genre", genres[tab])
            .putInt(k + "year", years[tab])
            .putString(k + "years", selectedYears[tab])
            .putString(k + "providers", providers[tab])
            .putBoolean(k + "ascending", ascending[tab])
            .putBoolean(k + "list", listMode[tab])
            .putInt(k + "watched", watchedFilter[tab])
            .putInt(k + "yearFrom", yearFrom[tab])
            .putInt(k + "yearTo", yearTo[tab])
            .putBoolean(k + "genreAll", genreAll[tab])
            .apply();
    }

    /** The row's card rail: the item's only child, or its last child while the row controls are showing. */
    static RecyclerView railIn(View item) {
        if (!(item instanceof ViewGroup))
            return null;
        ViewGroup group = (ViewGroup)item;
        int count = group.getChildCount();
        if (count == 1 && group.getChildAt(0) instanceof RecyclerView)
            return (RecyclerView)group.getChildAt(0);
        if (count == 2 && group.getChildAt(0) instanceof HomeRowControls && group.getChildAt(1) instanceof RecyclerView)
            return (RecyclerView)group.getChildAt(1);
        return null;
    }

    HomeRowMover rowMover() {
        if (rowMover == null)
            rowMover = new HomeRowMover(getContext(), new HomeRowMover.Host() {
                @Override
                public RecyclerView list() {
                    return list;
                }

                @Override
                public List<Cell> cells() {
                    return cells;
                }

                @Override
                public RecyclerView.Adapter<?> adapter() {
                    return adapter;
                }

                @Override
                public void rerender() {
                    render();
                }
            });
        return rowMover;
    }

    /**
     * Holding Down or Up on Movies or TV Shows repeats faster than the grid can scroll. A repeat that arrives while
     * the grid is still moving, or while the focused poster is not on screen, used to send focus to the top; such
     * repeats now wait for the grid to settle.
     */
    private boolean heldVerticalMustWait(KeyEvent event) {
        if (tab != 1 && tab != 2)
        {
            return false;
        }
        int key = event.getKeyCode();
        boolean vertical = key == KeyEvent.KEYCODE_DPAD_DOWN || key == KeyEvent.KEYCODE_DPAD_UP;
        if (!vertical || event.getAction() != KeyEvent.ACTION_DOWN || event.getRepeatCount() == 0)
        {
            return false;
        }
        View focused = list.findFocus();
        if (focused == null)
        {
            return list.hasFocus() || list.getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
        }
        return list.getScrollState() != RecyclerView.SCROLL_STATE_IDLE || list.hasPendingAdapterUpdates()
            || list.isComputingLayout();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (tab == 0 && rowMover != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && !rowMover.moving()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && rowMover.showing()) {
                rowMover.dismiss(true);
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN && rowMover.undoOnBack())
                return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.ACTION_UP)
            recordRemoteInput();
        if ((tab == 1 || tab == 2) && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            // The remote's Menu button opens the same menu as a long-press (sheet).
            View focused = list.findFocus();
            View item = focused == null ? null : list.findContainingItemView(focused);
            int at = item == null ? -1 : list.getChildAdapterPosition(item);
            if (at >= 0 && at < cells.size() && cells.get(at).type == POSTER) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0)
                    item.performLongClick();
                return true;
            }
        }
        int remoteKey = event.getKeyCode();
        if (heldVerticalMustWait(event))
        {
            return true;
        }
        if (tab == 0 && (remoteKey == KeyEvent.KEYCODE_DPAD_LEFT || remoteKey == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            View focused = findFocus();
            String tag = focused == null ? "" : String.valueOf(focused.getTag());
            if (heroOwnsHorizontal || tag.startsWith("hero:") ||
                restoringFocus && anchors[0] != null && anchors[0].cell.equals(HERO + ":Featured")) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    heroOwnsHorizontal = true;
                    changeFeatured(remoteKey == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                }
                if (event.getAction() == KeyEvent.ACTION_UP)
                    heroOwnsHorizontal = false;
                return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_DOWN)
            heroOwnsHorizontal = false;
        if (tab == 0 && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            View focus = findFocus(), item = focus == null ? null : list.findContainingItemView(focus);
            int position = item == null ? -1 : list.getChildAdapterPosition(item);
            if (position >= 0 && cells.get(position).type == RAIL && !cells.get(position).homeRowId.isEmpty() &&
                item instanceof ViewGroup) {
                RecyclerView rail = railIn(item);
                if (rail != null) {
                    View card = rail.findContainingItemView(focus);
                    if (card != null && rail.getChildAdapterPosition(card) == 0) {
                        // Hide and Move appear beside the row (0.155 notes #14); no pop-up.
                        rowMover().reveal((LinearLayout)item, rail, cells.get(position).homeRowId);
                        return true;
                    }
                }
            }
        }
        if (tab == 0 && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN &&
            findFocus() != null && "hero:info".equals(findFocus().getTag())) {
            for (int i = 0; i < cells.size(); i++)
                if (cells.get(i).type == RAIL || cells.get(i).type == PLACEHOLDERS) {
                    FocusAnchor anchor = new FocusAnchor();
                    anchor.cell = cellKey(cells.get(i));
                    anchor.position = i;
                    anchors[tab] = anchor;
                    holdFocus();
                    restoreFocus();
                    return true;
                }
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            lastInteraction = SystemClock.elapsedRealtime();
        if ((tab == 1 || tab == 2) && event.getAction() == KeyEvent.ACTION_DOWN &&
            event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && libraryControl(findFocus())) {
            for (int i = 0; i < cells.size(); i++)
                if (cells.get(i).type == POSTER) {
                    FocusAnchor anchor = new FocusAnchor();
                    anchor.cell = cellKey(cells.get(i));
                    anchor.position = i;
                    anchors[tab] = anchor;
                    holdFocus();
                    restoreFocus();
                    return true;
                }
            return true; // Empty filtered library retains its control focus.
        }
        if ((tab == 1 || tab == 2) && event.getAction() == KeyEvent.ACTION_DOWN &&
            event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            View focused = list.findFocus(), item = focused == null ? null : list.findContainingItemView(focused);
            int p = item == null ? -1 : list.getChildAdapterPosition(item);
            if (p >= 0 && p < cells.size()) {
                Cell c = cells.get(p);
                int header = -1, first = -1;
                for (int i = 0; i < cells.size(); i++) {
                    if (cells.get(i).type == HEADER && Boolean.TRUE.equals(cells.get(i).value))
                        header = i;
                    if (cells.get(i).type == POSTER) {
                        first = i;
                        break;
                    }
                }
                boolean firstRow = first >= 0 && c.type == POSTER &&
                                   layout.getSpanSizeLookup().getSpanGroupIndex(p, layout.getSpanCount()) ==
                                       layout.getSpanSizeLookup().getSpanGroupIndex(first, layout.getSpanCount());
                if (firstRow && header >= 0) {
                    FocusAnchor a = new FocusAnchor();
                    a.cell = cellKey(cells.get(header));
                    a.position = header;
                    a.child = firstLibraryControl();
                    anchors[tab] = a;
                    holdFocus();
                    layout.scrollToPositionWithOffset(0, 0);
                    restoreFocus();
                    return true;
                }
                if ((c.type == HEADER || c.type == HERO) && list.canScrollVertically(-1)) {
                    layout.scrollToPositionWithOffset(0, 0);
                    return true;
                }
            }
        }
        if (tab == 0 && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            View focused = list.findFocus(), item = focused == null ? null : list.findContainingItemView(focused);
            if (item != null) {
                int p = list.getChildAdapterPosition(item);
                if (p >= 0 && p < cells.size()) {
                    if (cells.get(p).type == HERO && item.getTop() < list.getPaddingTop()) {
                        layout.scrollToPositionWithOffset(0, 0);
                        return true;
                    }
                    if (cells.get(p).type == RAIL || cells.get(p).type == PLACEHOLDERS) {
                        boolean first = true;
                        for (int i = 0; i < p; i++)
                            if (cells.get(i).type == RAIL)
                                first = false;
                        if (first) {
                            FocusAnchor a = new FocusAnchor();
                            a.cell = HERO + ":Featured";
                            a.child = "hero:info";
                            anchors[tab] = a;
                            holdFocus();
                            layout.scrollToPositionWithOffset(0, 0);
                            restoreFocus();
                            return true;
                        }
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /** A carousel change must not rebind the library rows below it. */
    private void renderFeatured() {
        if (tab != 0 || cells.isEmpty() || cells.get(0).type != HERO) {
            render("featured_structure_changed");
            return;
        }
        rememberFocus();
        boolean preserve = list.hasFocus() && anchors[tab] != null && anchors[tab].position == 0;
        if (preserve)
            holdFocus();
        Entry featuredEntry = featured();
        cells.set(0, new Cell(HERO, "Featured", featuredEntry == null ? HomePlaceholder.Kind.HERO : featuredEntry));
        updateArtwork();
        cells.get(0).signature = signature(cells.get(0));
        adapter.notifyItemChanged(0);
        if (preserve)
            restoreFocus();
    }

    private void render() {
        render("control_change");
    }

    private void render(String reason) {
        setNavigationInset(navigationInset);
        if (tab == 1 || tab == 2)
            Diagnostics.libraryState(
                tab == 1 ? "movies" : "tv", listMode[tab] ? "list" : "grid",
                (columns[tab].sortColumn == null ? "preset_" + sorts[tab] : columns[tab].sortColumn.name()) + ":" +
                    ((columns[tab].sortColumn == null ? ascending[tab] : columns[tab].ascending) ? "ascending"
                                                                                                 : "descending"),
                genres[tab], selectedYears[tab], providers[tab], unmatched[tab]);
        else
            Diagnostics.uiState(tab == 0 ? "home" : "network", "none", "grid",
                                                                         "none", "none", 0);
        long started = SystemClock.elapsedRealtime();
        List<Cell> previous = new ArrayList<>(cells);
        persistViews();
        rememberFocus();
        boolean preserve = false;
        View focused = list.findFocus();
        View focusedItem = focused == null ? null : list.findContainingItemView(focused);
        int focusedPosition = focusedItem == null ? -1 : list.getChildAdapterPosition(focusedItem);
        Object state = layout.onSaveInstanceState();
        cells.clear();
        if (tab == 0) {
            if (featured() == null)
                cells.add(new Cell(HERO, "Featured", HomePlaceholder.Kind.HERO));
            if (featured() != null)
                cells.add(new Cell(HERO, "Featured", featured()));
            List<Entry> continuing = new ArrayList<>(snapshot.continuingMovies);
            continuing.addAll(snapshot.continuingShows);
            continuing.sort(Comparator.comparingLong((Entry e) -> e.playedAt).reversed());
            PreviewHomeRows home = new PreviewHomeRows(getContext());
            Set<String> continuingKeys = new HashSet<>();
            for (Entry e : continuing)
                continuingKeys.add(e.key());
            continuing.removeIf(home::dismissed);
            home.supersedeWatchNext(continuingKeys);
            for (PreviewHomeRows.Row row : home.rows) {
                if (!row.visible || !SupernovaLibraryPolicy.configured(getContext()))
                    continue;
                List<Entry> entries = new ArrayList<>();
                String name = row.name;
                switch (row.id) {
                case "continue":
                    entries = new ArrayList<>(continuing);
                    break;
                case "recent":
                    for (Entry e : snapshot.recent)
                        if (!e.started && !continuingKeys.contains(e.key()) && entries.size() < 50)
                            entries.add(e);
                    break;
                case "trending":
                    entries = discovery.matches(snapshot, true);
                    break;
                case "popular":
                    entries = discovery.matches(snapshot, false);
                    break;
                case "watched":
                    entries = snapshot.watched;
                    break;
                case "similar":
                    Entry seed = snapshot.played.isEmpty() ? null : snapshot.played.get(0);
                    if (seed != null) {
                        entries = PreviewDiscovery.similar(seed, snapshot);
                        name = "Because You Watched " + displayName(seed);
                    }
                    break;
                default:
                    entries = home.members(row, snapshot);
                }
                if (!row.custom() && !row.id.equals("watchnext"))
                    entries = new ArrayList<>(entries.subList(0, Math.min(15, entries.size())));
                int before = cells.size();
                rail(name, entries);
                if (cells.size() > before) {
                    cells.get(cells.size() - 1).homeRowId = row.id;
                    cells.get(before).homeRowId = row.id;
                    cells.get(before).headingIcon = PreviewHomeRows.iconName(row);
                }
            }
            boolean recentPresent = false;
            for (Cell cell : cells)
                if ("recent".equals(cell.homeRowId))
                    recentPresent = true;
            boolean recentVisible = home.rows.stream().anyMatch(row -> row.id.equals("recent") && row.visible);
            if (recentVisible && !recentPresent &&
                (snapshot.recent.isEmpty() ||
                 !SupernovaLibraryPolicy.configured(getContext()))) {
                header("Recently Added", false);
                cells.get(cells.size() - 1).headingIcon = "date added";
                cells.add(new Cell(PLACEHOLDERS, "Recently Added", HomePlaceholder.Kind.RECENT));
            }
            if (loaded && !emptyHome() &&
                SupernovaLibraryPolicy.configured(getContext()))
                cells.add(new Cell(CUSTOMISE, "Customise Home", null));
        } else if (tab == 1 || tab == 2) {
            cells.add(new Cell(HERO,
                               customLibrary != null ? customLibrary.name
                               : tab == 1            ? "Movies"
                                                     : "TV Shows",
                               featured()));
            refreshNextEpisodes();
            boolean nothingAdded = customLibrary == null && loaded && source().isEmpty();
            if (customUnset) {
                // Your third page before it is set up (sheet; RULES §23): no toolbar, one message.
                cells.add(new Cell(NOTICE, "Make this page your own", unsetPage()));
            } else if (nothingAdded) {
                // Empty pages (`library-pages/34`): no toolbar while there is nothing to filter or sort.
                boolean scanning = TopBarSignal.scanning();
                String kind = tab == 2 ? "TV shows" : "films";
                cells.add(new Cell(NOTICE, scanning ? "Finding your " + kind : "No " + kind + " yet",
                                   scanning ? new EmptyPage("The first ones appear here in a moment.", null, "ring",
                                                            null)
                                            : new EmptyPage("", "Add your videos", "plus", this::openFiles)));
            } else {
                header(tab == 1 ? "Movie library" : "TV show library", true);
                List<Entry> entries =
                    customPlaceholder != null ? java.util.Collections.<Entry>emptyList() : filtered();
                for (Entry e : entries)
                    cells.add(new Cell(POSTER, "", e));
                if (customPlaceholder != null)
                    header(customPlaceholder, false);
                else if (loaded && entries.isEmpty())
                    cells.add(new Cell(NOTICE, "Nothing on this page yet", emptyPage()));
            }
        } else {
            cells.add(new Cell(NETWORK, "Files", null));
        }
        if (!loaded && tab > 0 && tab != 3)
            header(tab == 1 ? "Movies" : "TV Shows", false);
        updateArtwork();
        for (Cell cell : cells) {
            cell.signature = signature(cell);
            cell.viewType = cell.type == POSTER && tab < 3 && listMode[tab] ? LIST : cell.type;
        }
        // Keep the actual focused view across unrelated or no-op refreshes. Only
        // transfer focus when its own cell will be rebound, moved or removed.
        if (focusedPosition >= 0 && focusedPosition < previous.size()) {
            Cell old = previous.get(focusedPosition);
            int match = -1;
            for (int i = 0; i < cells.size(); i++)
                if (cellKey(old).equals(cellKey(cells.get(i)))) {
                    match = i;
                    break;
                }
            preserve = match != focusedPosition || match < 0 || old.viewType != cells.get(match).viewType ||
                       !old.signature.equals(cells.get(match).signature);
            if (preserve)
                holdFocus();
        }
        int[] changes = new int[4];
        AdapterListUpdateCallback updates =
            new AdapterListUpdateCallback(adapter);
        DiffUtil
            .calculateDiff(new DiffUtil.Callback() {
                public int getOldListSize() {
                    return previous.size();
                }
                public int getNewListSize() {
                    return cells.size();
                }
                public boolean areItemsTheSame(int a, int b) {
                    return previous.get(a).viewType == cells.get(b).viewType &&
                        cellKey(previous.get(a)).equals(cellKey(cells.get(b)));
                }
                public boolean areContentsTheSame(int a, int b) {
                    return previous.get(a).signature.equals(cells.get(b).signature);
                }
            })
            .dispatchUpdatesTo(new ListUpdateCallback() {
                public void onInserted(int position, int count) {
                    changes[0] += count;
                    updates.onInserted(position, count);
                }
                public void onRemoved(int position, int count) {
                    changes[1] += count;
                    updates.onRemoved(position, count);
                }
                public void onMoved(int from, int to) {
                    changes[2]++;
                    updates.onMoved(from, to);
                }
                public void onChanged(int position, int count, Object payload) {
                    changes[3] += count;
                    updates.onChanged(position, count, payload);
                }
            });
        Diagnostics.event(
            "home_page_render", "reason", reason, "tab", tab, "previous_cells", previous.size(), "cells", cells.size(),
            "inserted", changes[0], "removed", changes[1], "moved", changes[2], "rebound", changes[3],
            "adapter_recreated", false, "elapsed_ms", SystemClock.elapsedRealtime() - started);
        Diagnostics.uiRebuild(list,
                                                                       tab == 0   ? "home.rows"
                                                                       : tab == 1 ? "movies.library"
                                                                       : tab == 2 ? "tv.library"
                                                                                  : "network.page",
                                                                       reason, previous.size(), cells.size(), false);
        if (state != null && !switchingTab && preserve)
            layout.onRestoreInstanceState((Parcelable)state);
        if (preserve)
            restoreFocus();
        list.post(this::notifyScroll);
        scheduleVisibleEnrichment();
        removeCallbacks(importRefresh);
        if (loaded && emptyHome() && isAttachedToWindow())
            postDelayed(importRefresh, 1000);
    }

    private String snapshotSignature = "";

    /** Everything a page shows from a snapshot, so an identical one can be ignored. */
    private static String snapshotSignature(Snapshot s) {
        StringBuilder out = new StringBuilder();
        List<List<Entry>> parts = Arrays.asList(s.featured, s.technical, s.episodes, s.unmatched, s.watched, s.movies,
            s.shows, s.recent, s.played, s.continuingMovies, s.continuingShows);
        for (List<Entry> part : parts) {
            out.append('#').append(part.size());
            for (Entry e : part) {
                out.append('\n').append(entrySignature(e));
                if (e.media instanceof Video)
                    out.append('|').append(((Video)e.media).getLastPlayed());
            }
        }
        return out.toString();
    }

    private static String entrySignature(Entry e) {
        return e.key() + "|" + displayName(e) + "|" + e.backdrop + "|" + e.media.getPosterUri() + "|" + e.secondary +
            "|" + e.active + "|" + e.bytes + "|" + e.runtime + "|" + e.year() + "|" + e.resolution + "|" + e.audio +
            "|" + e.codec + "|" + e.hdr + "|" + e.tagline + "|" +
            (e.homeMetadata == null ? ""
                                    : SharedMetadata.primary(e.homeMetadata).toString() + e.homeMetadata.technical) +
            "|" + (e.media instanceof Video ? ((Video)e.media).getResumeMs() : 0);
    }

    private String signature(Cell c) {
        String base = tab + ":" + c.title + ":" + c.headingIcon + ":" + c.type + ":" +
                      (tab > 0 && tab < 3
                           ? listMode[tab] + ":" + sorts[tab] + ":" + ascending[tab] + ":" + genres[tab] + ":" +
                                 selectedYears[tab] + ":" + unmatched[tab] + ":" + providers[tab] + ":" +
                                 columns[tab].sortColumn + ":" + columns[tab].ascending + ":" + columns[tab].visible()
                           : "");
        if (c.value instanceof Entry)
            base += entrySignature((Entry)c.value);
        else if (c.type == RAIL) {
            StringBuilder rail = new StringBuilder(base);
            for (Entry e : (List<Entry>)c.value)
                rail.append(entrySignature(e));
            base = rail.toString();
        } else
            base += String.valueOf(c.value);
        if (c.type == HERO && tab > 0)
            base += LibraryStatistics
                        .calculate(snapshot,
                                   customLibrary != null ? customLibrary.entries(snapshot)
                                   : tab == 2            ? snapshot.shows
                                                         : snapshot.movies,
                                   System.currentTimeMillis(), TimeZone.getDefault())
                        .signature();
        if (c.type == NETWORK_PANEL || c.type == NETWORK) {
            StringBuilder sources = new StringBuilder(base);
            for (Shortcut source : librarySources)
                sources.append(source.getName()).append(source.getUri());
            for (Shortcut source : savedLocations)
                sources.append(source.getName()).append(source.getUri());
            base = sources.append(files).toString();
        }
        return base;
    }

    private CustomLibraryPage customLibrary;
    private Runnable customEdit;
    /** Text shown instead of results while a brand-new custom page has no settings yet (owner, 2 Nov 2026). */
    private String customPlaceholder;
    /** While the Edit Page panel is open the page shows the unsaved draft and must never store it. */
    private boolean customEditing;

    public void setCustomEditing(boolean editing) {
        customEditing = editing;
    }

    /** Your own page before it is set up: "Make this page your own" with Customise page (sheet). */
    public void setCustomUnset(boolean unset) {
        if (customUnset == unset)
            return;
        customUnset = unset;
        render("custom_unset");
    }

    public void setCustomPlaceholder(String text) {
        if (java.util.Objects.equals(text, customPlaceholder))
            return;
        customPlaceholder = text;
        render();
    }

    public void configureCustomLibrary(CustomLibraryPage page, Runnable edit) {
        customLibrary = page;
        customEdit = edit;
        tab = 1;
        listMode[1] = "list".equals(page.view);
        sorts[1] = "title".equals(page.sort) ? 1 : "year".equals(page.sort) ? 2 : 0;
        ascending[1] = !page.descending;
        columns[1] = new PreviewLibraryColumns(getContext(), true, "custom_");
        SharedPreferences saved = preferences;
        genres[1] = saved.getString("preview_custom_genres", "");
        selectedYears[1] = saved.getString("preview_custom_years", "");
        providers[1] = saved.getString("preview_custom_providers", "");
        render();
    }

    public void focusCustomControl(String id) {
        View target = "empty".equals(id) ? list.findViewWithTag("library.empty.action")
                                         : list.findViewWithTag("custom.control." + id);
        if (target != null)
            target.requestFocus();
        else
            list.post(() -> {
                View restored = list.findViewWithTag("custom.control." + id);
                if (restored != null)
                    restored.requestFocus();
            });
    }

    private List<Entry> source() {
        if (customLibrary != null)
            return customLibrary.entries(snapshot);
        if (!unmatched[tab])
            return tab == 1 ? snapshot.movies : snapshot.shows;
        List<Entry> result = new ArrayList<>();
        Map<Uri, PreviewMediaClassification.Kind> hints = PreviewMediaClassification.hints(preferences);
        if (snapshot.unmatched != null)
            for (Entry entry : snapshot.unmatched) {
                Video video = (Video)entry.media;
                PreviewMediaClassification.Kind kind =
                    PreviewMediaClassification.classify(video.getFilenameNonCryptic(), video.getFileUri(), hints);
                if (kind == PreviewMediaClassification.Kind.UNKNOWN ||
                    kind == (tab == 1 ? PreviewMediaClassification.Kind.MOVIE : PreviewMediaClassification.Kind.TV))
                    result.add(entry);
            }
        return result;
    }

    private void networkSection(String section) {
        getContext().startActivity(
            new Intent(getContext(), NetworkRootActivity.class)
                .putExtra("preview_source_section", section));
    }

    private void chart(String name, boolean trend) {
        List<Entry> matches = discovery.matches(snapshot, trend);
        if (!matches.isEmpty())
            rail(name, matches);
        else {
            cells.add(new Cell(NOTICE, name,
                               discovery.available ? "No matching titles in the current Trakt chart"
                                                   : "Unavailable — Trakt charts will appear here when connected"));
        }
    }

    public static List<Entry> sortEntries(List<Entry> input, int sort, boolean ascending, PreviewDiscovery discovery) {
        List<Entry> entries = new ArrayList<>(input);
        Comparator<Entry> cmp;
        if (sort >= 3) {
            Map<String, Integer> ranks = sort == 3 ? discovery.trending : discovery.popular;
            cmp = (a, b) -> {
                Integer x = ranks.get(PreviewDiscovery.key(a)), y = ranks.get(PreviewDiscovery.key(b));
                if (x == null || y == null)
                    return x == null ? (y == null ? 0 : 1) : -1;
                return ascending ? Integer.compare(y, x) : Integer.compare(x, y);
            };
        } else {
            cmp = sort == 1   ? Comparator.comparing(e -> displayName(e), String.CASE_INSENSITIVE_ORDER)
                  : sort == 2 ? PreviewReleaseOrder.comparator(true)
                              : Comparator.comparingLong(e -> e.added);
            if (!ascending)
                cmp = sort == 2 ? PreviewReleaseOrder.comparator(false) : cmp.reversed();
        }
        entries.sort(cmp.thenComparing(e -> displayName(e), String.CASE_INSENSITIVE_ORDER));
        return entries;
    }

    public static String titleForSort(Context context, Entry entry) {
        return SortUtils.isIgnoreArticlesEnabled(context) &&
                entry.sortTitle != null && !entry.sortTitle.isEmpty()
            ? entry.sortTitle
            : displayName(entry);
    }

    public static boolean hiddenByWatchedPreference(Context context, Entry entry) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hide_watched",
                                                                                                   false))
            return false;
        return entry.media instanceof Video
            ? PreviewSeriesJourney.completed((Video)entry.media)
            : entry.media instanceof Tvshow && ((Tvshow)entry.media).getEpisodeCount() > 0 &&
                  ((Tvshow)entry.media).isWatched();
    }

    private List<Entry> filtered() {
        List<Entry> entries = new ArrayList<>();
        for (Entry e : source())
            if (!hiddenByWatchedPreference(getContext(), e) && !removedKeys.contains(e.key()) &&
                LibraryFilters.watchedMatches(e, watchedFilter[tab]) &&
                LibraryFilters.yearMatches(e, yearFrom[tab], yearTo[tab]) &&
                PreviewGenres.matches(e.genres, PreviewGenres.parse(genres[tab]), genreAll[tab]) &&
                (selectedYears[tab].isEmpty() ||
                 PreviewGenres.parse(selectedYears[tab]).contains(String.valueOf(e.year()))) &&
                (providers[tab].isEmpty() ||
                 PreviewGenres.parse(providers[tab])
                     .stream()
                     .anyMatch(
                         provider
                         -> StreamingRepository.selected(getContext())
                                    .contains(provider) &&
                                StreamingRepository.knownOn(
                                    getContext(), e.media instanceof Movie ? "movie" : "tv", e.onlineId, provider))))
                entries.add(e);
        entries = sortEntries(entries, sorts[tab], ascending[tab], discovery);
        if (sorts[tab] == 1) {
            Comparator<Entry> titles =
                Comparator.comparing(e -> titleForSort(getContext(), e), String.CASE_INSENSITIVE_ORDER);
            if (!ascending[tab])
                titles = titles.reversed();
            entries.sort(titles);
        }
        if (columns[tab] != null)
            entries = columns[tab].sort(entries);
        if (!quietOrder.isEmpty())
            entries.sort(Comparator.comparingInt(e -> quietOrder.getOrDefault(e.key(), Integer.MAX_VALUE)));
        return entries;
    }

    private String[] sortLabels() {
        return new String[] {"Date Added", "Title",
                             customLibrary != null ? "Release / Air Date"
                             : tab == 2            ? "Air Date"
                                                   : "Release Date",
                             "Trakt Trending", "Trakt Popular"};
    }

    // ---- 0.158 library page (page sheet docs/design/pages/LIBRARY.md) -------------------------------------------

    private String sortKey() {
        return LibraryPageModel.key(sorts[tab], columns[tab] == null ? null : columns[tab].sortColumn);
    }

    private boolean sortAscending() {
        return columns[tab] == null || columns[tab].sortColumn == null ? ascending[tab] : columns[tab].ascending;
    }

    private boolean seriesOnPage() {
        return customLibrary != null ? !"movies".equals(customLibrary.type) : tab == 2;
    }

    private boolean onlySeries() {
        return customLibrary != null ? "tv".equals(customLibrary.type) : tab == 2;
    }

    private List<LibraryPageModel.SortItem> sortItems() {
        return LibraryPageModel.sortItems(discovery.available, seriesOnPage(), onlySeries());
    }

    private String sortLabel(String key) {
        for (LibraryPageModel.SortItem item : sortItems())
            if (item.key.equals(key))
                return item.label;
        return "Date added";
    }

    /** Applies a sort from the Sort menu or Customise page; it starts in its own natural direction. */
    void applySort(String key) {
        quietOrder.clear();
        for (LibraryPageModel.SortItem item : sortItems()) {
            if (!item.key.equals(key))
                continue;
            boolean up = LibraryPageModel.startsAscending(key);
            if (item.column == null) {
                columns[tab].clearSort();
                sorts[tab] = item.preset;
                ascending[tab] = up;
            } else {
                columns[tab].setSort(item.column, up);
                ascending[tab] = up;
            }
        }
        render("sort_change");
    }

    private void setOrder(boolean up) {
        quietOrder.clear();
        ascending[tab] = up;
        columns[tab].setAscending(up);
        render("order_change");
    }

    private void setListMode(boolean list) {
        quietOrder.clear();
        listMode[tab] = list;
        render("view_change");
    }

    /**
     * Even gaps between the seven posters, with the first and last on the page's side margins (so the posters line
     * up with the toolbar and the top bar's edges).
     */
    private void placeInRow(View card, int position) {
        int first = position;
        while (first > 0 && cells.get(first - 1).type == POSTER)
            first--;
        int column = (position - first) % libraryColumns();
        int gap = dp(12);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)card.getLayoutParams();
        lp.leftMargin = column * gap / libraryColumns();
        lp.rightMargin = gap - (column + 1) * gap / libraryColumns();
        lp.bottomMargin = dp(16);
        card.setLayoutParams(lp);
    }

    private TextView toolbarButton(String label, String icon, Consumer<View> action) {
        TextView[] made = {null};
        made[0] = TvButton.create(getContext(), label, () -> {
            quietOrder.clear();
            action.accept(made[0]);
        }, false);
        PreviewIcon.apply(made[0], icon, Math.round(18 * TvLayout.scale(getContext())));
        made[0].setContentDescription(label);
        return made[0];
    }

    /**
     * The toolbar (sheet; mock-ups `library-pages/27`, `26`): Filters · Sort: <choice> · Order · List view / Grid view
     * (· Columns in list view), Customise page locked on the right. Focus is the white box; menus drop down under the
     * button pressed.
     */
    private void bindLibraryToolbar(LinearLayout v, Runnable refreshColumns) {
        v.setOrientation(LinearLayout.VERTICAL);
        v.setPadding(0, dp(TOOLBAR_TOP_GAP_DP), 0, dp(listMode[tab] ? 4 : 14));
        v.setTag("library.toolbar.row");
        PreviewToolbar controls = new PreviewToolbar(getContext(), true);
        String key = sortKey();
        boolean up = sortAscending();
        List<TextView> buttons = new ArrayList<>();
        buttons.add(toolbarButton("Filters", "filter", this::filter));
        buttons.add(toolbarButton("Sort: " + sortLabel(key), "sort", this::sort));
        buttons.add(toolbarButton(LibraryPageModel.orderLabel(key, up), LibraryPageModel.orderIcon(up),
                                  b -> setOrder(!sortAscending())));
        buttons.add(toolbarButton(listMode[tab] ? "Grid view" : "List view", listMode[tab] ? "grid" : "list",
                                  b -> setListMode(!listMode[tab])));
        if (listMode[tab])
            buttons.add(toolbarButton("Columns", "columns", b -> columns[tab].choose(b, refreshColumns)));
        String[] controlIds = {"filters", "sort", "order", "view", "columns"};
        for (int i = 0; i < buttons.size(); i++) {
            TextView control = buttons.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.leftMargin = i == 0 ? 0 : dp(4);
            control.setTag(customLibrary == null ? "control:" + i : "custom.control." + controlIds[i]);
            Diagnostics.semantic(control, "library.toolbar." + controlIds[i]);
            controls.addView(control, lp);
        }
        TextView customise = toolbarButton("Customise page", "edit", b -> customisePage());
        customise.setTag(customLibrary == null ? "control:customise" : "custom.control.edit");
        v.addView(PreviewToolbar.withTrailingAction(controls, customise));
        View divider = new View(getContext());
        divider.setTag("library.toolbar.divider");
        divider.setBackgroundColor(0x14FFFFFF);
        LinearLayout.LayoutParams dividerSize = new LinearLayout.LayoutParams(-1, Math.max(1, dp(1)));
        dividerSize.topMargin = dp(10);
        v.addView(divider, dividerSize);
        if (listMode[tab])
            v.addView(columns[tab].header(refreshColumns));
    }

    /** The grouped Sort menu under its button (`library-pages/25`): no search row, a tick on the current choice. */
    private void sort(View anchor) {
        List<LibraryPageModel.SortItem> items = sortItems();
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        String group = "";
        String current = sortKey();
        int tick = -1;
        for (LibraryPageModel.SortItem item : items) {
            if (!item.group.equals(group)) {
                group = item.group;
                labels.add("— " + group);
                icons.add(null);
                keys.add(null);
            }
            if (item.key.equals(current))
                tick = labels.size();
            labels.add(item.label);
            icons.add(item.icon);
            keys.add(item.key);
        }
        PreviewDialog.menuBelow(getContext(), anchor, "Sort", labels.toArray(new String[0]),
                                icons.toArray(new String[0]),
                                tick < 0 ? Collections.emptySet() : Collections.singleton(tick), Math.max(1, tick),
                                true, n -> {
                                    if (keys.get(n) != null)
                                        applySort(keys.get(n));
                                });
    }

    private String watchedSummary() {
        return LibraryFilters.WATCHED_LABELS[Math.max(0, Math.min(3, watchedFilter[tab]))];
    }

    private String genreSummary() {
        Set<String> chosen = PreviewGenres.parse(genres[tab]);
        return chosen.isEmpty() ? "Any" : TextUtils.join(", ", chosen);
    }

    private String yearSummary() {
        if (yearFrom[tab] <= 0 && yearTo[tab] <= 0)
            return "Any";
        if (yearFrom[tab] == yearTo[tab])
            return String.valueOf(yearFrom[tab]);
        return (yearFrom[tab] > 0 ? String.valueOf(yearFrom[tab]) : "Any") + " – " +
            (yearTo[tab] > 0 ? String.valueOf(yearTo[tab]) : "Any");
    }

    private String providerSummary() {
        int count = PreviewGenres.parse(providers[tab]).size();
        return count == 0 ? "Any" : count + " chosen";
    }

    private static String titles(int count) {
        return count + (count == 1 ? " title" : " titles");
    }

    /**
     * Filters (`library-pages/23`, `24`): Watched · Genre · Year · Streaming services (when you have services), then
     * Clear filters apart by a line. Each opens its list on top of the menu; choices apply live; Back closes.
     */
    private void filter(View anchor) {
        boolean services = !StreamingRepository.selectedCatalogue(getContext()).isEmpty();
        List<String> labels = new ArrayList<>(Arrays.asList("Watched · " + watchedSummary(), "Genre · " + genreSummary(),
                                                            "Year · " + yearSummary()));
        List<String> icons = new ArrayList<>(Arrays.asList("eye", "genre", "year"));
        if (services) {
            labels.add("Streaming services · " + providerSummary());
            icons.add("cloud");
        }
        labels.add("— ");
        icons.add(null);
        labels.add("Clear filters");
        icons.add("close");
        int clear = labels.size() - 1;
        Dialog[] menu = {null};
        menu[0] = PreviewDialog.menuBelow(getContext(), anchor, "Filters", labels.toArray(new String[0]),
                                          icons.toArray(new String[0]), Collections.emptySet(), 0, false, n -> {
            if (n == clear) {
                genres[tab] = "";
                years[tab] = 0;
                selectedYears[tab] = "";
                providers[tab] = "";
                watchedFilter[tab] = 0;
                yearFrom[tab] = 0;
                yearTo[tab] = 0;
                quietOrder.clear();
                menu[0].dismiss();
                render("filters_cleared");
                return;
            }
            Runnable refreshMenu = () -> {
                PreviewDialog.updateLabel(menu[0], 0, "Watched · " + watchedSummary());
                PreviewDialog.updateLabel(menu[0], 1, "Genre · " + genreSummary());
                PreviewDialog.updateLabel(menu[0], 2, "Year · " + yearSummary());
                if (services)
                    PreviewDialog.updateLabel(menu[0], 3, "Streaming services · " + providerSummary());
            };
            if (n == 0)
                chooseWatched(refreshMenu);
            else if (n == 1)
                chooseGenres(refreshMenu);
            else if (n == 2)
                chooseYears(refreshMenu);
            else if (n == 3 && services)
                chooseServices(refreshMenu);
        });
    }

    private void chooseWatched(Runnable refreshMenu) {
        List<Entry> pool = source();
        String[] labels = new String[4];
        for (int i = 0; i < 4; i++)
            labels[i] = i == 0 ? LibraryFilters.WATCHED_LABELS[0]
                               : LibraryFilters.WATCHED_LABELS[i] + " · " + titles(LibraryFilters.count(pool, i));
        PreviewDialog.menuBelow(getContext(), null, "Watched", labels, LibraryFilters.WATCHED_ICONS,
                                Collections.singleton(watchedFilter[tab]), watchedFilter[tab], true, n -> {
                                    watchedFilter[tab] = n;
                                    quietOrder.clear();
                                    render("filter_watched");
                                    refreshMenu.run();
                                });
    }

    /** Genres present on the page with counts and genre icons; Match: Any · All at the top (sheet). */
    private void chooseGenres(Runnable refreshMenu) {
        Map<String, Integer> counts = new TreeMap<>();
        for (Entry e : source())
            for (String genre : PreviewGenres.parse(e.genres))
                counts.merge(genre, 1, Integer::sum);
        List<String> names = new ArrayList<>(counts.keySet());
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        labels.add(genreAll[tab] ? "Match: All" : "Match: Any");
        icons.add("chevrons");
        for (String name : names) {
            labels.add(name + " · " + titles(counts.get(name)));
            icons.add(name);
        }
        Set<String> chosen = new LinkedHashSet<>(PreviewGenres.parse(genres[tab]));
        Set<Integer> ticked = new HashSet<>();
        for (int i = 0; i < names.size(); i++)
            if (chosen.contains(names.get(i)))
                ticked.add(i + 1);
        Dialog[] list = {null};
        list[0] = PreviewDialog.menuBelow(getContext(), null, "Genres", labels.toArray(new String[0]),
                                          icons.toArray(new String[0]), ticked, 0, false, true, n -> {
            if (n == 0) {
                genreAll[tab] = !genreAll[tab];
                PreviewDialog.updateLabel(list[0], 0, genreAll[tab] ? "Match: All" : "Match: Any");
            } else if (!chosen.add(names.get(n - 1))) {
                chosen.remove(names.get(n - 1));
            }
            Set<Integer> now = new HashSet<>();
            for (int i = 0; i < names.size(); i++)
                if (chosen.contains(names.get(i)))
                    now.add(i + 1);
            PreviewDialog.updateChecks(list[0], now);
            genres[tab] = TextUtils.join("|", chosen);
            quietOrder.clear();
            render("filter_genres");
            refreshMenu.run();
        });
    }

    /** Year: quick choices that hold titles (with counts), then From / To (`library-pages/24`). */
    private void chooseYears(Runnable refreshMenu) {
        List<Entry> pool = source();
        List<Integer> present = LibraryFilters.years(pool);
        List<String> labels = new ArrayList<>();
        List<int[]> ranges = new ArrayList<>();
        LibraryFilters.quickYears(present, labels, ranges);
        for (int i = 0; i < labels.size(); i++) {
            int[] range = ranges.get(i);
            if (range != null && (range[0] > 0 || range[1] > 0))
                labels.set(i, labels.get(i) + " · " + titles(LibraryFilters.count(pool, range[0], range[1])));
        }
        labels.add("— Your Own Range");
        ranges.add(null);
        labels.add("From · " + (yearFrom[tab] > 0 ? yearFrom[tab] : "Any"));
        ranges.add(new int[] {-1, 0});
        labels.add("To · " + (yearTo[tab] > 0 ? yearTo[tab] : "Any"));
        ranges.add(new int[] {0, -1});
        String[] icons = new String[labels.size()];
        Set<Integer> ticked = new HashSet<>();
        for (int i = 0; i < labels.size(); i++) {
            int[] range = ranges.get(i);
            icons[i] = range == null ? null : range[0] < 0 || range[1] < 0 ? "edit" : "year";
            if (range != null && range[0] >= 0 && range[1] >= 0 && range[0] == yearFrom[tab] &&
                range[1] == yearTo[tab])
                ticked.add(i);
        }
        PreviewDialog.menuBelow(getContext(), null, "Year", labels.toArray(new String[0]), icons, ticked, 1, true,
                                n -> {
            int[] range = ranges.get(n);
            if (range == null)
                return;
            if (range[0] < 0 || range[1] < 0) {
                boolean from = range[0] < 0;
                LibraryFilters.chooseYear(getContext(), present, from ? "From" : "To",
                                          from ? yearFrom[tab] : yearTo[tab], year -> {
                    if (from)
                        yearFrom[tab] = year;
                    else
                        yearTo[tab] = year;
                    if (yearFrom[tab] > 0 && yearTo[tab] > 0 && yearTo[tab] < yearFrom[tab])
                        yearTo[tab] = yearFrom[tab];
                    selectedYears[tab] = "";
                    quietOrder.clear();
                    render("filter_years");
                    refreshMenu.run();
                });
                return;
            }
            yearFrom[tab] = range[0];
            yearTo[tab] = range[1];
            selectedYears[tab] = "";
            years[tab] = 0;
            quietOrder.clear();
            render("filter_years");
            refreshMenu.run();
        });
    }

    /** Your streaming services (several), with counts; ticks apply at once (RULES §9). */
    private void chooseServices(Runnable refreshMenu) {
        List<StreamingRepository.Provider> selected = StreamingRepository.selectedCatalogue(getContext());
        List<String> labels = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<Entry> pool = source();
        for (StreamingRepository.Provider provider : selected) {
            int count = 0;
            for (Entry e : pool)
                if (StreamingRepository.knownOn(getContext(), e.media instanceof Movie ? "movie" : "tv", e.onlineId,
                                                String.valueOf(provider.id)))
                    count++;
            labels.add(provider.name + " · " + titles(count));
            ids.add(String.valueOf(provider.id));
        }
        Set<String> chosen = new LinkedHashSet<>(PreviewGenres.parse(providers[tab]));
        Set<Integer> ticked = new HashSet<>();
        for (int i = 0; i < ids.size(); i++)
            if (chosen.contains(ids.get(i)))
                ticked.add(i);
        Dialog[] list = {null};
        list[0] = PreviewDialog.menuBelow(getContext(), null, "Streaming services", labels.toArray(new String[0]),
                                          new String[labels.size()], ticked, 0, false, true, n -> {
            if (!chosen.add(ids.get(n)))
                chosen.remove(ids.get(n));
            Set<Integer> now = new HashSet<>();
            for (int i = 0; i < ids.size(); i++)
                if (chosen.contains(ids.get(i)))
                    now.add(i);
            PreviewDialog.updateChecks(list[0], now);
            providers[tab] = TextUtils.join("|", chosen);
            quietOrder.clear();
            render("filter_services");
            refreshMenu.run();
        });
        for (int i = 0; i < selected.size(); i++)
            PreviewProviderIcons.bindProvider(list[0], i, selected.get(i), "library.filters");
    }

    /** Customise page: the floating side panel on the right (sheet), for Movies, TV Shows and your own page. */
    private void customisePage() {
        if (customLibrary != null) {
            if (customEdit != null)
                customEdit.run();
            return;
        }
        LibraryCustomisePanel.show(getContext(), libraryHost(null, null));
    }

    /** What the Customise page panel changes on this page. {@code changed}/{@code delete} serve your own page. */
    LibraryCustomisePanel.Host libraryHost(Runnable changed, Runnable delete) {
        return new LibraryCustomisePanel.Host() {
            public CustomLibraryPage page() {
                return customLibrary;
            }

            public Snapshot snapshot() {
                return snapshot;
            }

            public int show() {
                return watchedFilter[tab];
            }

            public void setShow(int show) {
                watchedFilter[tab] = show;
                quietOrder.clear();
                render("customise_show");
            }

            public boolean list() {
                return listMode[tab];
            }

            public void setList(boolean list) {
                setListMode(list);
            }

            public String sort() {
                return sortKey();
            }

            public void setSort(String key) {
                applySort(key);
            }

            public boolean ascending() {
                return sortAscending();
            }

            public void setAscending(boolean value) {
                setOrder(value);
            }

            public void pageChanged() {
                if (changed != null)
                    changed.run();
                render("customise_page");
            }

            public void deletePage() {
                if (delete != null)
                    delete.run();
            }
        };
    }

    /**
     * Long-press (or Menu) on a poster (`library-pages/30`): beside the poster, an icon each, Playback · Library ·
     * Files & Media; no "More info" (OK opens Details), no Restart on an unstarted title.
     */
    private void libraryMenu(Entry e, View anchor) {
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<Runnable> commands = new ArrayList<>();
        boolean show = e.media instanceof Tvshow;
        boolean watched = LibraryPageModel.watched(e);
        labels.add("— Playback");
        icons.add(null);
        commands.add(null);
        if (show) {
            Entry next = nextEpisodes.get(((Tvshow)e.media).getTvshowId());
            if (next != null && next.media instanceof Episode) {
                Episode episode = (Episode)next.media;
                labels.add("Resume S" + episode.getSeasonNumber() + " E" + episode.getEpisodeNumber());
                icons.add("resume");
                commands.add(() -> play(next, anchor));
                Entry first = firstEpisode(e);
                if (first != null) {
                    labels.add("Play from the start");
                    icons.add("history");
                    commands.add(() -> playFromStart(first));
                }
            } else {
                labels.add("Play");
                icons.add("play");
                commands.add(() -> play(e, anchor));
            }
        } else {
            boolean resume = e.media instanceof Video && ((Video)e.media).getResumeMs() > 0 && !watched;
            labels.add(resume ? "Resume" : "Play");
            icons.add(resume ? "resume" : "play");
            commands.add(() -> play(e, anchor));
        }
        labels.add("— Library");
        icons.add(null);
        commands.add(null);
        labels.add(watched ? (show ? "Mark show as unwatched" : "Mark as unwatched")
                           : (show ? "Mark show as watched" : "Mark as watched"));
        icons.add("check");
        commands.add(() -> markWatched(e, !watched));
        Video matchable = e.media instanceof Video ? (Video)e.media
                          : firstEpisode(e) != null ? (Video)firstEpisode(e).media
                                                    : null;
        if (matchable != null) {
            labels.add("Fix match");
            icons.add("search");
            commands.add(() -> adapter.openFixMatch(matchable));
        }
        labels.add("Remove from library");
        icons.add("remove");
        commands.add(() -> removeFromLibrary(e));
        labels.add("— Files & Media");
        icons.add(null);
        commands.add(null);
        labels.add(show ? "Delete all episode files" : "Delete file");
        icons.add("delete");
        commands.add(() -> deleteFiles(e));
        PreviewDialog.menu(getContext(), anchor, displayName(e), labels.toArray(new String[0]),
                           icons.toArray(new String[0]), -1, n -> {
                               if (commands.get(n) != null)
                                   commands.get(n).run();
                           });
    }

    private Entry firstEpisode(Entry show) {
        Entry first = null;
        for (Entry ep : snapshot.episodes) {
            if (ep.show != show.show || !(ep.media instanceof Episode))
                continue;
            Episode episode = (Episode)ep.media;
            if (first == null || episode.getSeasonNumber() < ((Episode)first.media).getSeasonNumber() ||
                episode.getSeasonNumber() == ((Episode)first.media).getSeasonNumber() &&
                    episode.getEpisodeNumber() < ((Episode)first.media).getEpisodeNumber())
                first = ep;
        }
        return first;
    }

    private void playFromStart(Entry episode) {
        if (episode.media instanceof Video && getContext() instanceof Activity)
            PlayUtils.startVideo((Activity)getContext(), (Video)episode.media, PlayerActivity.RESUME_NO, false, -1,
                                 null, -1);
    }

    /** Mark as watched: in place, with an Undo notice; caption and tick update (sheet). */
    private void markWatched(Entry e, boolean watched) {
        LibraryActions.markWatched(getContext(), e.media, watched);
        UndoNotice.show(getContext(), watched ? "Marked as watched" : "Marked as unwatched", () -> {
            LibraryActions.markWatched(getContext(), e.media, !watched);
            requestLibraryRefresh();
        });
        requestLibraryRefresh();
    }

    /** Remove from library: the poster leaves, with an Undo notice; the file stays. */
    private void removeFromLibrary(Entry e) {
        LibraryActions.removeFromLibrary(getContext(), e.media, true);
        removedKeys.add(e.key());
        UndoNotice.show(getContext(), displayName(e) + " left the library", () -> {
            removedKeys.remove(e.key());
            LibraryActions.removeFromLibrary(getContext(), e.media, false);
            requestLibraryRefresh();
        });
        requestLibraryRefresh();
    }

    /** Delete file(s): the delete warning, "Keep file" focused first (RULES §7b); then the files go. */
    private void deleteFiles(Entry e) {
        boolean show = e.media instanceof Tvshow;
        PreviewDialog.confirmDelete(
            getContext(), show ? "Delete every episode of " + displayName(e) + "?" : "Delete " + displayName(e) + "?",
            show ? "Every episode file is deleted from its drive or server. This can't be undone."
                 : "The file is deleted from its drive or server. This can't be undone.",
            () -> {
                List<Uri> files = LibraryActions.files(getContext(), e.media);
                if (files.isEmpty() || !(getContext() instanceof Activity))
                    return;
                com.archos.mediacenter.video.browser.Delete delete = new com.archos.mediacenter.video.browser.Delete(
                    new com.archos.mediacenter.video.browser.Delete.DeleteListener() {
                        public void onVideoFileRemoved(Uri videoFile, boolean askForFolderRemoval,
                                                       Uri folderToDelete) {
                            requestLibraryRefresh();
                        }

                        public void onDeleteVideoFailed(Uri videoFile) {
                            PreviewNotice.show(getContext(), "Couldn't delete the file", true);
                        }

                        public void onFolderRemoved(Uri folder) {
                        }

                        public void onDeleteSuccess() {
                            requestLibraryRefresh();
                        }
                    }, getContext());
                if (files.size() == 1)
                    delete.startDeleteProcess(files.get(0));
                else
                    delete.startMultipleDeleteProcess(files);
            });
    }

    /** The library changed underneath: reload the snapshot (the page refreshes as it arrives). */
    private void requestLibraryRefresh() {
        getContext().getContentResolver().notifyChange(
            com.archos.mediaprovider.video.VideoStore.Video.Media.EXTERNAL_CONTENT_URI, null);
        render("library_action");
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(getContext());
        t.setText(value);
        t.setTextColor(Color.WHITE);
        t.setTextSize(size);
        return t;
    }

    private long heroWarmAt;
    private final Runnable warmHeroes = () -> {
        if (!isAttachedToWindow() || tab != 0)
            return;
        heroWarmAt = SystemClock.elapsedRealtime();
        List<Entry> catalogue = featuredCandidates();
        for (int i = 0; i < Math.min(8, catalogue.size()); i++) {
            Entry candidate = catalogue.get(Math.floorMod(featuredIndex + i, catalogue.size()));
            OfficialTitleArtwork.prefetch(getContext(), candidate.media);
            Uri uri = candidate.backdrop != null ? candidate.backdrop : candidate.media.getPosterUri();
            if (uri != null) {
                RequestCreator request =
                    Picasso.get().load(uri).resize(dp(960), dp(540));
                if (candidate.backdrop != null)
                    request.centerCrop();
                else
                    request.centerInside();
                request.fetch();
            }
        }
    };
    private final LinkedHashMap<String, PreviewFeaturedCard> heroWindow = new LinkedHashMap<>();

    private PreviewFeaturedCard preparedHero(Entry e, View opener) {
        SubtitleDiscovery.hero(getContext(), e, snapshot);
        long preparedAt = SystemClock.elapsedRealtime();
        if (preparedAt - heroWarmAt > 30000) {
            removeCallbacks(warmHeroes);
            postDelayed(warmHeroes, 500);
        }
        List<Entry> entries = featuredCandidates();
        if (e == null)
            return new PreviewFeaturedCard(getContext(), java.util.Collections.emptyList(), 0, this::placeholderInfo,
                                           this::placeholderInfo);
        String activeKey = null;
        java.util.Set<String> keep = new java.util.HashSet<>();
        int current = Math.floorMod(featuredIndex, entries.size());
        for (int offset : new int[] {0, -1, 1}) {
            int index = Math.floorMod(current + offset, entries.size());
            Entry candidate = entries.get(index);
            String key = entrySignature(candidate) + "|logo:" + OfficialTitleArtwork.cachedRevision(candidate.media);
            if (offset == 0)
                activeKey = key;
            keep.add(key);
            OfficialTitleArtwork.prefetch(getContext(), candidate.media);
            Uri uri = candidate.backdrop != null ? candidate.backdrop : candidate.media.getPosterUri();
            if (uri != null) {
                RequestCreator request =
                    Picasso.get().load(uri).resize(dp(960), dp(540));
                if (candidate.backdrop != null)
                    request.centerCrop();
                else
                    request.centerInside();
                request.fetch();
            }
            if (!heroWindow.containsKey(key)) {
                PreviewFeaturedCard card =
                    new PreviewFeaturedCard(getContext(), entries, index,
                                            () -> open(candidate, cardAnchor()), () -> play(candidate, cardAnchor()));
                card.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        Math.max(dp(320), list.getWidth() - list.getPaddingLeft() - list.getPaddingRight()),
                        View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(Math.max(dp(280), getHeight() - list.getPaddingTop() - dp(110)),
                                                     View.MeasureSpec.EXACTLY));
                heroWindow.put(key, card);
            }
        }
        Iterator<java.util.Map.Entry<String, PreviewFeaturedCard>> retained =
            heroWindow.entrySet().iterator();
        while (retained.hasNext()) {
            java.util.Map.Entry<String, PreviewFeaturedCard> cached = retained.next();
            if (!keep.contains(cached.getKey())) {
                cached.getValue().releaseArtwork();
                retained.remove();
            }
        }
        PreviewFeaturedCard card = heroWindow.get(activeKey);
        card.resetMotion();
        if (card.getParent() instanceof ViewGroup)
            ((ViewGroup)card.getParent()).removeView(card);
        Diagnostics.event(
            "hero_window_prepared", "cards", heroWindow.size(), "elapsed_ms",
            SystemClock.elapsedRealtime() - preparedAt);
        return card;
    }

    private View cardAnchor() {
        View view = findFocus();
        return view == null ? this : view;
    }

    private void openNetwork() {
        View parent = this;
        while (parent.getParent() instanceof View) {
            parent = (View)parent.getParent();
            if (parent instanceof TopNavigation) {
                ((TopNavigation)parent).selectTab(3);
                break;
            }
        }
        setTab(3);
    }

    private void placeholderInfo() {
        boolean firstRun =
            !SupernovaLibraryPolicy.configured(getContext()) || emptyHome();
        PreviewDialog.message(
            getContext(), firstRun ? "No Media Details Yet" : "No Details for This Placeholder",
            firstRun
                ? "Your movies and TV shows will appear here once you've added folders to your library."
                : "No media is attached to this placeholder. Browse your library or add more folders in Files.",
            false, new String[] {"Files", "Continue Browsing"}, 0, n -> {
                if (n == 0)
                    openNetwork();
            });
    }

    private void showWelcome() {
        if (tab != 0 || !isAttachedToWindow() || preferences.getBoolean("supernova_welcome_dismissed", false) ||
            SupernovaLibraryPolicy.configured(getContext()))
            return;
        Dialog dialog = PreviewDialog.message(
            getContext(), "Welcome to Supernova",
            "Add movies and TV shows from local storage, external drives or network shares. Supernova will only begin scanning once you've selected and confirmed your folders.",
            true, new String[] {"Files", "Explore Supernova"}, 0, n -> {
                preferences.edit().putBoolean("supernova_welcome_dismissed", true).apply();
                if (n == 0)
                    openNetwork();
            });
        dialog.setOnCancelListener(d -> preferences.edit().putBoolean("supernova_welcome_dismissed", true).apply());
    }

    static final int SPANS = 840;

    /** One portrait poster size on every library page, seven a row (page sheet LIBRARY.md). */
    static final int POSTERS_A_ROW = 7;

    private int libraryColumns() {
        return POSTERS_A_ROW;
    }

    private TextView button(String name, Runnable action) {
        return TvButton.create(getContext(), name, () -> {
            quietOrder.clear();
            action.run();
        }, false);
    }

    private int dp(int n) {
        return Math.round(n * getResources().getDisplayMetrics().density);
    }

    class Holder extends RecyclerView.ViewHolder {
        PreviewCardPresenter presenter;
        Presenter.ViewHolder card;

        Holder(View v) {
            super(v);
        }
    }

    class PageAdapter extends RecyclerView.Adapter<Holder> {
        private void refreshColumns() {
            quietOrder.clear();
            render("columns_change");
        }

        @Override
        public long getItemId(int p) {
            Cell c = cells.get(p);
            String key = c.type == POSTER    ? ((Entry)c.value).key()
                         : c.type == STORAGE ? ((Box)c.value).getBoxId() + ":" + ((Box)c.value).getPath()
                                             : c.title;
            return ((long)c.type << 32) | (key.hashCode() & 0xffffffffL);
        }

        @Override
        public int getItemCount() {
            return cells.size();
        }

        @Override
        public int getItemViewType(int p) {
            return cells.get(p).type == POSTER && tab < 3 && listMode[tab] ? LIST : cells.get(p).type;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int type) {
            if (type == LIST) {
                LinearLayout row = columns[tab].newRow();
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp(44));
                lp.bottomMargin = dp(1);
                row.setLayoutParams(lp);
                return new Holder(row);
            }
            if (type == POSTER) {
                PreviewCardPresenter pr = new PreviewCardPresenter(type == LIST ? PreviewCardPresenter.Style.LIST
                                                                                : PreviewCardPresenter.Style.POSTER);
                Presenter.ViewHolder card = pr.onCreateViewHolder(parent);
                Holder h = new Holder(card.view);
                h.presenter = pr;
                h.card = card;
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
                lp.setMargins(0, 0, dp(10), dp(15));
                h.itemView.setLayoutParams(lp);
                return h;
            }
            LinearLayout v = new LinearLayout(getContext());
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setPadding(0, dp(6), 0, dp(6));
            v.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder h, int p) {
            Cell c = cells.get(p);
            if (c.type == POSTER && h.itemView instanceof LinearLayout) {
                Entry e = (Entry)c.value;
                columns[tab].bind((LinearLayout)h.itemView, e);
                h.itemView.setTag(e.key());
                h.itemView.setOnClickListener(v -> open(e, v));
                h.itemView.setOnLongClickListener(v -> {
                    libraryMenu(e, v);
                    return true;
                });
                return;
            }
            if (c.type == POSTER) {
                Entry e = (Entry)c.value;
                h.presenter.bindEntry(h.card, e);
                h.itemView.setTag(e.key());
                h.itemView.setOnClickListener(v -> open(e, v));
                if (tab == 1 || tab == 2) {
                    // One card: artwork and a three-line caption (sheet; RULES §14).
                    Entry next = e.media instanceof Tvshow ? nextEpisodes.get(((Tvshow)e.media).getTvshowId()) : null;
                    PreviewCardPresenter.bindLibraryCaption(
                        h.card, LibraryPageModel.secondLine(e, next),
                        LibraryPageModel.thirdLine(getContext(), columns[tab], sortKey(), e),
                        LibraryPageModel.progress(e, next), LibraryPageModel.watched(e));
                    placeInRow(h.itemView, p);
                    h.itemView.setOnLongClickListener(v -> {
                        libraryMenu(e, v);
                        return true;
                    });
                    return;
                }
                h.itemView.setOnLongClickListener(v -> {
                    contextMenu(e, v, false);
                    return true;
                });
                return;
            }
            LinearLayout v = (LinearLayout)h.itemView;
            if (rowMover != null && rowMover.owns(v) && !rowMover.rowId().equals(c.homeRowId))
                rowMover.dismiss(false);
            if (c.type == RAIL && railIn(v) != null) {
                RecyclerView rail = railIn(v);
                if (rail.getAdapter() instanceof RailAdapter) {
                    ((RailAdapter)rail.getAdapter()).update((List<Entry>)c.value);
                    return;
                }
            }
            v.removeAllViews();
            v.setFocusable(false);
            v.setOnClickListener(null);
            v.setBackground(null);
            v.setOrientation(LinearLayout.HORIZONTAL);
            v.setGravity(Gravity.CENTER_VERTICAL);
            if ("library.empty".equals(v.getTag()))
                v.setTag(null);
            v.setPadding(0, dp(6), 0, dp(6));
            v.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            if (tab == 0) {
                v.setClipChildren(false);
                v.setClipToPadding(false);
            }
            if (c.type == HERO) {
                Entry e = c.value instanceof Entry ? (Entry)c.value : null;
                v.setOrientation(LinearLayout.VERTICAL);
                v.setGravity(Gravity.TOP);
                v.setPadding(0, dp(10), 0, dp(8));
                v.setMinimumHeight(dp(tab == 0 ? 244 : 76));
                v.setLayoutParams(new RecyclerView.LayoutParams(
                    -1, tab == 0 ? dp(244) : android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                if (tab == 0) {
                    int heroHeight = Math.max(dp(280), getHeight() - list.getPaddingTop() - dp(110));
                    v.setMinimumHeight(heroHeight);
                    v.setLayoutParams(new RecyclerView.LayoutParams(-1, heroHeight));
                    v.setPadding(0, dp(12), 0, dp(18));
                    PreviewFeaturedCard featured = preparedHero(e, v);
                    v.addView(featured, new LinearLayout.LayoutParams(-1, -1));
                    v.animate().cancel();
                    v.setAlpha(1f);
                    v.setTranslationX(0);
                    if (featuredDirection != 0 && motionAllowed()) {
                        v.setTranslationX(dp(36) * featuredDirection);
                        v.setAlpha(1f);
                        v.animate().translationX(0).setDuration(220).start();
                        featuredDirection = 0;
                    }
                } else {
                    // No page title and no statistics row on a library page (sheet; the top bar says where you
                    // are, the figures moved to Files › Your Library).
                    v.setPadding(0, 0, 0, 0);
                    v.setMinimumHeight(0);
                    v.setLayoutParams(new RecyclerView.LayoutParams(-1, 0));
                }
            } else if (c.type == PLACEHOLDERS) {
                v.setPadding(0, 0, 0, dp(10));
                v.addView(HomePlaceholder.rail(getContext(), PreviewPages.this::placeholderInfo),
                          new LinearLayout.LayoutParams(-1, dp(125)));
            } else if (c.type == CUSTOMISE) {
                v.setGravity(Gravity.CENTER);
                TextView custom =
                    button("Customise Home", () -> PreviewHomeRows.customise(getContext(), () -> { render(); }));
                custom.setTag("home:customise");
                v.addView(custom);
            } else if (c.type == NETWORK) {
                networkLayout(v);
            } else if (c.type == NETWORK_PANEL) {
                bindNetworkPanel(v, c);
            } else if (c.type == SCAN) {
                v.setOrientation(LinearLayout.VERTICAL);
                TextView scan = button("Scan Library", () -> PreviewLibraryScan.request(getContext()));
                scan.setTag("scan:library");
                v.addView(scan, new LinearLayout.LayoutParams(dp(158), dp(40)));
                TextView description = text(
                    "Scan local storage and indexed network folders. Full Library Scan in Settings also retries unmatched descriptions.",
                    12);
                description.setPadding(0, dp(8), 0, dp(8));
                description.setTextColor(0xffaac1d1);
                v.addView(description);
                TextView progress = text("", 13);
                progress.setTextColor(PreviewAccent.color(getContext()));
                progress.setMinHeight(dp(36));
                v.addView(progress, new LinearLayout.LayoutParams(-1, -2));
                progress.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!progress.isAttachedToWindow())
                            return;
                        String status = PreviewLibraryScan.libraryStatus(getContext());
                        progress.setText(status);
                        progress.setVisibility(VISIBLE);
                        progress.postDelayed(this, 1000);
                    }
                });
            } else if (c.type == NOTICE && c.value instanceof EmptyPage) {
                bindEmptyPage(v, c.title, (EmptyPage)c.value);
            } else if (c.type == NOTICE) {
                v.setOrientation(LinearLayout.VERTICAL);
                TextView title = text(c.title, 18);
                title.setTextColor(0xff8298aa);
                v.addView(title);
                TextView status = text((String)c.value, 12);
                status.setTextColor(0xff8298aa);
                v.addView(status);
                v.setPadding(0, dp(12), 0, dp(12));
            } else if (c.type == HEADER) {
                if (Boolean.TRUE.equals(c.value)) {
                    bindLibraryToolbar(v, this::refreshColumns);
                } else {
                    TextView title = text(c.title, tab == 3 && c.title.equals("Network & files") ? 30 : 19);
                    title.setTextColor(tab == 0 ? Color.WHITE : 0xff9ed4f7);
                    if (tab == 0 && !c.headingIcon.isEmpty())
                        MenuFoundation.icon(title, c.headingIcon);
                    v.addView(title);
                }
            } else if (c.type == RAIL) {
                v.setPadding(0, 0, 0, dp(10));
                RecyclerView rail = new FocusRecycler(getContext(), true);
                rail.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                rail.setClipChildren(false);
                rail.setClipToPadding(false);
                rail.setPadding(list.getPaddingLeft(), dp(10), list.getPaddingRight(), dp(10));
                rail.setItemAnimator(null);
                rail.setAdapter(new RailAdapter((List<Entry>)c.value, "Continue Watching".equals(c.title)));
                Diagnostics.uiRebuild(rail, "home.rail", "row_bound", 0,
                                                                               rail.getAdapter().getItemCount(), true);
                rail.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView row, int dx, int dy) {
                        scheduleVisibleEnrichment();
                    }
                });
                RecyclerView.LayoutParams rowSize = new RecyclerView.LayoutParams(-1, -2);
                rowSize.leftMargin = -list.getPaddingLeft();
                rowSize.rightMargin = -list.getPaddingRight();
                v.setLayoutParams(rowSize);
                v.addView(rail, new LinearLayout.LayoutParams(-1, dp(125)));
            } else if (c.type == STORAGE) {
                Box b = (Box)c.value;
                v.setPadding(dp(12), dp(12), dp(12), dp(12));
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp(76));
                lp.setMargins(0, 0, dp(12), dp(12));
                v.setLayoutParams(lp);
                v.setBackground(PreviewDialog.surface(getContext(), false));
                v.setForeground(PreviewDialog.focus(getContext()));
                v.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                v.setFocusable(true);
                v.setClickable(true);
                ImageView icon = new ImageView(getContext());
                icon.setImageDrawable(new StorageIcon(b.getBoxId()));
                v.addView(icon, new LinearLayout.LayoutParams(dp(35), dp(35)));
                LinearLayout labels = new LinearLayout(getContext());
                labels.setOrientation(LinearLayout.VERTICAL);
                labels.setPadding(dp(12), 0, 0, 0);
                String name = b.getName(), sub = "";
                if (b.getBoxId() == Box.ID.NETWORK) {
                    name = "Browse network";
                    sub = "Find shared folders";
                } else if (b.getBoxId() == Box.ID.FOLDERS) {
                    name = "Internal storage";
                } else if (b.getBoxId() == Box.ID.VIDEOS_BY_LISTS) {
                    name = "Playlists";
                    sub = "Browse saved playlists";
                } else {
                    int at = name.indexOf('(');
                    if (at > 0) {
                        sub = name.substring(at + 1).replace(")", "").trim();
                        name = name.substring(0, at).trim();
                    }
                    name = name.replaceFirst("^[^:]+:\\s*", "");
                }
                TextView title = text(name, 13);
                title.setMaxLines(1);
                title.setEllipsize(TextUtils.TruncateAt.END);
                labels.addView(title);
                if (!sub.isEmpty()) {
                    TextView detail = text(sub, 10);
                    detail.setTextColor(0xffb4cbe0);
                    detail.setMaxLines(2);
                    labels.addView(detail);
                }
                v.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
                v.setContentDescription(name + " " + sub);
                v.setOnClickListener(view -> click.open(new Presenter.ViewHolder(view), b));
            }
        }

        private void bindNetworkPanel(LinearLayout v, Cell c) {
            v.setOrientation(LinearLayout.VERTICAL);
            v.setGravity(Gravity.TOP);
            v.setPadding(dp(14), dp(12), dp(14), dp(12));
            v.setBackground(PreviewDialog.surface(getContext(), false));
            RecyclerView.LayoutParams size = new RecyclerView.LayoutParams(
                -1, dp(c.title.equals("Local Storage") || c.title.equals("Discover Devices") ||
                               c.title.equals("Saved Locations")
                           ? 145
                           : 210));
            size.setMargins(0, 0, dp(10), dp(10));
            v.setLayoutParams(size);
            TextView heading = text(c.title, 16);
            PreviewIcon.apply(heading, c.title, 24);
            heading.setPadding(0, 0, 0, dp(8));
            v.addView(heading);
            if (c.title.equals("Scan Library")) {
                v.addView(text("Check local storage and library sources for new or changed media.", 12));
                v.addView(button("Scan Library", () -> PreviewLibraryScan.request(getContext())));
                TextView status = text(PreviewNetworkScanning.lastResult(getContext()), 12);
                v.addView(status, new LinearLayout.LayoutParams(-1, dp(70)));
                status.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!status.isAttachedToWindow())
                            return;
                        status.setText(
                            NetworkScannerReceiver.isScannerWorking()
                                ? "Scanning network sources · " +
                                      NetworkScannerServiceVideo.getFilesFoundCount() +
                                      " files found"
                                : PreviewNetworkScanning.lastResult(getContext()));
                        status.postDelayed(this, 1000);
                    }
                });
            } else if (c.title.equals("Network Scanning")) {
                v.addView(text("Automatic scanning for your network sources.", 12));
                int period = NetworkAutoRefresh.getRescanPeriod(getContext());
                TextView schedule = text(
                    "Automatic: " + (period > 0 ? "On" : "Off") +
                        "\nFrequency: " + (period > 0 ? period / 60000 + " minutes" : "Not scheduled") +
                        "\nSources: " + librarySources.size() + "\n" + PreviewNetworkScanning.lastResult(getContext()),
                    12);
                schedule.setLineSpacing(dp(4), 1);
                schedule.setPadding(0, dp(12), 0, dp(8));
                v.addView(schedule);
                v.addView(button("Configure Network Scanning", () -> PreviewNetworkScanning.show(getContext())));
            } else if (c.title.equals("Local Storage")) {
                for (Box box : files)
                    if (box.getBoxId() == Box.ID.FOLDERS || box.getBoxId() == Box.ID.USB ||
                        box.getBoxId() == Box.ID.SDCARD || box.getBoxId() == Box.ID.OTHER)
                        v.addView(button(box.getBoxId() == Box.ID.FOLDERS ? "Internal Storage" : box.getName(),
                                         () -> click.open(new Presenter.ViewHolder(v), box)));
            } else if (c.title.equals("Discover Devices")) {
                v.addView(button("Network Computers & NAS", () -> networkSection("smb")));
                v.addView(button("Media Servers · DLNA/UPnP", () -> networkSection("upnp")));
            } else {
                v.addView(text(c.title.equals("Library Sources")
                                   ? "Indexed network sources appearing in Movies and TV Shows."
                                   : "Saved browsing locations.",
                               12));
                List<Shortcut> sourceList =
                    c.title.equals("Library Sources") ? librarySources : savedLocations;
                for (int i = 0; i < Math.min(2, sourceList.size()); i++) {
                    Shortcut source = sourceList.get(i);
                    TextView item =
                        button(source.getName() + " · " + source.getUri().getScheme(), () -> openSource(source));
                    item.setSingleLine(true);
                    item.setEllipsize(TextUtils.TruncateAt.END);
                    v.addView(item, new LinearLayout.LayoutParams(-1, dp(34)));
                }
                v.addView(button("Open " + c.title,
                                 () -> networkSection(c.title.equals("Library Sources") ? "library" : "saved")));
                if (c.title.equals("Library Sources"))
                    v.addView(button("Add Network Source", () -> networkSection("add")));
            }
        }

        private void networkLayout(LinearLayout container) {
            if (tab == 3 && PreferenceManager.getDefaultSharedPreferences(getContext())
                                .getBoolean(FILES_PAGE_SWITCH, true)) {
                filesLayout(container);
                return;
            }
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.setGravity(Gravity.TOP);
            container.setClipChildren(false);
            container.setLayoutParams(new RecyclerView.LayoutParams(
                -1, Math.max(dp(360), getHeight() - list.getPaddingTop() - list.getPaddingBottom())));
            sharedBrowser = tab == 0 ? homeSetupBrowser : networkBrowser;
            if (sharedBrowser == null) {
                sharedBrowser = new PreviewNetworkWorkspace(getContext(), files, librarySources, savedLocations,
                                                            box
                                                            -> click.open(new Presenter.ViewHolder(container), box),
                                                            PreviewPages.this::networkSection, tab != 0);
                if (tab == 0)
                    homeSetupBrowser = sharedBrowser;
                else
                    networkBrowser = sharedBrowser;
            } else
                sharedBrowser.update(files, librarySources, savedLocations, PreviewPages.this::networkSection);
            if (sharedBrowser.getParent() instanceof ViewGroup)
                ((ViewGroup)sharedBrowser.getParent()).removeView(sharedBrowser);
            int browserHeight =
                tab == 0
                    ? Math.max(dp(260), (int)((getHeight() - list.getPaddingTop() - list.getPaddingBottom()) * .65f))
                    : Math.max(dp(360), getHeight() - list.getPaddingTop() - list.getPaddingBottom());
            RecyclerView.LayoutParams browserSize = new RecyclerView.LayoutParams(-1, browserHeight);
            if (tab == 3) {
                browserSize.leftMargin = -list.getPaddingLeft();
                browserSize.rightMargin = -list.getPaddingRight();
            }
            container.setLayoutParams(browserSize);
            sharedBrowser.browser().onboarding(tab == 0);
            sharedBrowser.update(files, librarySources, savedLocations, PreviewPages.this::networkSection);
            container.addView(sharedBrowser, new LinearLayout.LayoutParams(-1, -1));
            if (networkEntryPending)
                container.post(PreviewPages.this::requestNetworkEntryFocus);
        }

        /** The redesigned Files page (step 8 of 0.157); the old screens still open from it. */
        private void filesLayout(LinearLayout container) {
            // Files fills the page itself, not a cell inside the old page's padding (F3): the old list's 28 dp each
            // side is cancelled here and Files keeps the same visible margin on its own, without clipping.
            RecyclerView.LayoutParams whole = new RecyclerView.LayoutParams(
                -1, Math.max(dp(360), getHeight() - list.getPaddingTop() - list.getPaddingBottom()));
            whole.leftMargin = -list.getPaddingLeft();
            whole.rightMargin = -list.getPaddingRight();
            container.setLayoutParams(whole);
            container.setClipChildren(false);
            container.setClipToPadding(false);
            if (filesPage == null)
                filesPage = new com.archos.mediacenter.video.leanback.files.FilesPage(getContext());
            if (filesPage.getParent() instanceof ViewGroup)
                ((ViewGroup)filesPage.getParent()).removeView(filesPage);
            List<Base> media = new ArrayList<>();
            for (Entry entry : snapshot.technical)
                media.add(entry.media);
            com.archos.mediaprovider.ImportState imports = com.archos.mediaprovider.ImportState.VIDEO;
            boolean importing = imports.isInitialImport() || imports.isRegularImport();
            com.archos.mediacenter.video.leanback.files.LibraryCareCounts counts =
                com.archos.mediacenter.video.leanback.files.LibraryCareCounts.count(
                    media, importing,
                    video -> LibraryHealth.state(getContext(), video) != LibraryHealth.State.AVAILABLE);
            long lastScan = PreferenceManager.getDefaultSharedPreferences(getContext())
                                .getLong("preview_scan_result_time", 0);
            boolean scanning = PreviewLibraryScan.active();
            List<com.archos.mediacenter.video.leanback.files.FilesPage.Device> devices =
                com.archos.mediacenter.video.leanback.files.FilesPageData.devices(files, media);
            filesPage.update(counts, com.archos.mediacenter.video.leanback.files.FilesPageData.videoCount(media),
                             lastScan, scanning);
            filesPage.setDevices(devices, device -> {
                for (Box box : files)
                    if (device.name.equals(box.getName()) || !device.removable && box.getBoxId() == Box.ID.FOLDERS) {
                        Uri root = storageRoot(box);
                        // A drive without a folder to open stays where it is: never the old browser.
                        if (root == null)
                            return;
                        // Browsing stays in Files (page sheet, Browsing a drive).
                        filesPage.browse(root, device.name, (file, inLibrary) -> playFile(file, inLibrary));
                        return;
                    }
            });
            filesPage.setConnections(
                withReachability(
                    com.archos.mediacenter.video.leanback.files.FilesPageData.connections(
                        getContext(), librarySources, media)),
                // Every connection action now happens on the Files page itself (F31–F35, F40): no route leads to
                // NOVA's network screens or the three-panel browser.
                null);
            filesPage.setSources(
                com.archos.mediacenter.video.leanback.files.FilesPageData.sources(devices, librarySources),
                source -> {
                    if (source.local)
                        PreviewLibraryScan.request(getContext());
                    else
                        com.archos.mediaprovider.video.NetworkAutoRefresh.forceRescan(getContext(), source.root);
                });
            filesPage.setMedia(media);
            filesPage.setPlayer((file, inLibrary) -> playFile(file, inLibrary));
            filesPage.setFolderActions(new com.archos.mediacenter.video.leanback.files.FolderBrowser.Actions() {
                @Override public void details(Base item) {
                    click.open(new Presenter.ViewHolder(container), item);
                }

                @Override public void fixMatch(Base item) {
                    openFixMatch(item);
                }
            });
            filesPage.setStats(libraryStats());
            filesPage.setScanActions(() -> PreviewLibraryScan.request(getContext()),
                                     () -> PreviewLibraryScan.stop(getContext()));
            container.addView(filesPage, new LinearLayout.LayoutParams(-1, -1));
            final com.archos.mediacenter.video.leanback.files.FilesPage page = filesPage;
            final Context app = getContext().getApplicationContext();
            if (networkSearch == null) {
                try {
                    networkSearch = com.archos.mediacenter.video.connections.NetworkDeviceDiscovery.start(
                        app, page::post,
                        snapshot -> page.setFound(
                            com.archos.mediacenter.video.leanback.files.FilesPageData.found(snapshot)));
                } catch (RuntimeException unavailable) {
                    // No network search on this device: the section simply does not appear.
                    Diagnostics.error("files_network_search_unavailable", unavailable);
                }
            }
            new Thread(() -> {
                int unreadable = com.archos.mediacenter.video.leanback.files.FilesPageData.unreadable(app);
                page.post(() -> page.setUnreadable(unreadable));
            }, "FilesUnreadable").start();
        }

        /** Files › Fix match for a title (the approved page under the top bar; Back returns here). */
        private void openFixMatch(Base item) {
            if (!(item instanceof Video))
                return;
            Intent intent = new Intent(
                getContext(), com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity.class);
            intent.putExtra(com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity.EXTRA_VIDEO,
                            (Video)item);
            getContext().startActivity(intent);
        }

        /** A storage's top folder: its own path, or the device's shared storage for "This device". */
        private Uri storageRoot(Box box) {
            String path = box.getPath();
            if (path != null && !path.isEmpty())
                return Uri.fromFile(new java.io.File(path));
            if (box.getBoxId() == Box.ID.FOLDERS)
                return Uri.fromFile(android.os.Environment.getExternalStorageDirectory());
            return null;
        }

        /** Plays a file chosen in the folder grid, from the library entry when it has one. */
        private void playFile(com.archos.filecorelibrary.MetaFile2 file, Base inLibrary) {
            Video video = inLibrary instanceof Video
                ? (Video)inLibrary
                : new NonIndexedVideo(file.getStreamingUri(), file.getUri(), file.getName(), null);
            PlayUtils.startVideo((Activity)getContext(), video, PlayerActivity.RESUME_FROM_LAST_POS, false, -1, null, -1);
        }

        /** Files › Your Library: one line per video in the library snapshot (mock-up network-files-tabs/44). */
        private com.archos.mediacenter.video.leanback.files.LibraryStats libraryStats() {
            List<com.archos.mediacenter.video.leanback.files.LibraryStats.Item> items = new ArrayList<>();
            for (Entry entry : snapshot.technical) {
                if (!(entry.media instanceof Video))
                    continue;
                Video video = (Video)entry.media;
                String show = video instanceof Episode ? ((Episode)video).getShowName() : null;
                boolean uhd = video.getMeasuredHeight() >= 1800 || video.getMeasuredWidth() >= 3800;
                String range = video.getPreviewDynamicRange();
                boolean hdr = !range.isEmpty() && !"SDR".equalsIgnoreCase(range);
                items.add(new com.archos.mediacenter.video.leanback.files.LibraryStats.Item(
                    show == null || show.isEmpty() ? (video instanceof Episode ? "Unknown" : null) : show,
                    Math.max(0, video.getSize()), video.isLocalFile(), video.getDurationMs(), video.getResumeMs(),
                    video.isWatched(), uhd, hdr, entry.added, Math.max(video.getMeasuredHeight(), 0)));
            }
            long monthStart = com.archos.mediacenter.video.leanback.files.FilesPage.monthStart().getTime() / 1000L;
            return com.archos.mediacenter.video.leanback.files.LibraryStats.of(items, monthStart);
        }

        /** Adds what the last check found (and when each was last seen); starts a check at most once a minute. */
        private List<com.archos.mediacenter.video.leanback.files.FilesPage.Connection> withReachability(
            List<com.archos.mediacenter.video.leanback.files.FilesPage.Connection> plain) {
            List<com.archos.mediacenter.video.leanback.files.FilesPage.Connection> known = new ArrayList<>();
            List<Uri> roots = new ArrayList<>();
            for (com.archos.mediacenter.video.leanback.files.FilesPage.Connection c : plain) {
                Boolean state = c.root == null ? null : reachability.get(c.root.toString());
                long seen = c.root == null ? 0
                    : com.archos.mediacenter.video.leanback.files.ConnectionCheck.lastSeen(getContext(), c.root);
                known.add(new com.archos.mediacenter.video.leanback.files.FilesPage.Connection(
                    c.name, c.root, state, c.videos, seen, c.account));
                if (c.root != null)
                    roots.add(c.root);
            }
            long now = SystemClock.elapsedRealtime();
            if (!roots.isEmpty() && now - reachabilityCheckedAt > 60_000) {
                reachabilityCheckedAt = now;
                com.archos.mediacenter.video.leanback.files.ConnectionCheck.check(getContext(), roots, (root, ok) ->
                    post(() -> {
                        reachability.put(root.toString(), ok);
                        if (tab == 3)
                            render("connection_checked");
                    }));
            }
            return known;
        }

        private void setNetworkBack(View view, Runnable back) {
            if (view.isFocusable())
                view.setOnKeyListener((v, key, event) -> {
                    if (key == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
                        back.run();
                        return true;
                    }
                    return false;
                });
            if (view instanceof ViewGroup)
                for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                    setNetworkBack(((ViewGroup)view).getChildAt(i), back);
        }

        @Override
        public void onViewRecycled(Holder h) {
            if (h.presenter != null)
                h.presenter.onUnbindViewHolder(h.card);
            else if (h.itemView instanceof LinearLayout && h.itemView.isFocusable() && tab > 0 && tab < 3)
                columns[tab].clear(h.itemView);
            else if (h.itemView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup)h.itemView;
                for (int i = 0; i < group.getChildCount(); i++)
                    if (group.getChildAt(i) instanceof RecyclerView)
                        ((RecyclerView)group.getChildAt(i)).setAdapter(null);
            }
        }
    }

    class RailAdapter extends RecyclerView.Adapter<Holder> {
        final List<Entry> entries;
        final PreviewCardPresenter pr = new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
        final boolean resume;

        RailAdapter(List<Entry> e, boolean resume) {
            entries = new ArrayList<>(e);
            this.resume = resume;
            setHasStableIds(true);
        }

        void update(List<Entry> next) {
            List<Entry> old = new ArrayList<>(entries);
            entries.clear();
            entries.addAll(next);
            DiffUtil
                .calculateDiff(new DiffUtil.Callback() {
                    public int getOldListSize() {
                        return old.size();
                    }
                    public int getNewListSize() {
                        return entries.size();
                    }
                    public boolean areItemsTheSame(int a, int b) {
                        return old.get(a).key().equals(entries.get(b).key());
                    }
                    public boolean areContentsTheSame(int a, int b) {
                        return entrySignature(old.get(a)).equals(entrySignature(entries.get(b)));
                    }
                })
                .dispatchUpdatesTo(this);
        }

        @Override
        public long getItemId(int p) {
            return entries.get(p).key().hashCode();
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        public Holder onCreateViewHolder(ViewGroup p, int type) {
            Presenter.ViewHolder card = pr.onCreateViewHolder(p);
            Holder h = new Holder(card.view);
            h.card = card;
            h.presenter = pr;
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(dp(172), dp(105));
            lp.rightMargin = dp(12);
            h.itemView.setLayoutParams(lp);
            return h;
        }

        public void onBindViewHolder(Holder h, int p) {
            Entry e = entries.get(p);
            pr.bindEntry(h.card, e);
            h.itemView.setTag(e.key());
            h.itemView.setOnClickListener(v -> {
                if (resume)
                    play(e, v);
                else
                    open(e, v);
            });
            h.itemView.setOnLongClickListener(v -> {
                contextMenu(e, v, resume);
                return true;
            });
        }

        public void onViewRecycled(Holder h) {
            pr.onUnbindViewHolder(h.card);
        }
    }

    private void contextMenu(Entry entry, View view, boolean continuing) {
        List<String> labels = new ArrayList<>(Arrays.asList("More Info", "Add to Row"));
        if (continuing) {
            labels.add("Remove from Continue Watching Row");
            labels.add(entry.media instanceof Episode ? "Restart Episode" : "Restart Movie");
        }
        PreviewDialog.choose(getContext(), displayName(entry), labels.toArray(new String[0]), -1, index -> {
            if (index == 0)
                open(entry, view);
            else if (index == 1)
                PreviewHomeRows.add(getContext(), entry, this::render);
            else if (index == 2) {
                new PreviewHomeRows(getContext()).dismiss(entry);
                render();
            } else if (entry.media instanceof Video && getContext() instanceof Activity)
                PlayUtils.startVideo(
                    getContext(), (Video)entry.media, PlayerService.RESUME_NO,
                    false, -1, null, -1);
        });
    }

    static final class StorageIcon extends Drawable {
        final Box.ID id;
        final Paint paint = new Paint(3);

        StorageIcon(Box.ID id) {
            this.id = id;
            paint.setColor(0xffb7d7f5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.7f);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public void draw(Canvas c) {
            c.save();
            c.translate(getBounds().left, getBounds().top);
            c.scale(getBounds().width() / 32f, getBounds().height() / 32f);
            if (id == Box.ID.VIDEOS_BY_LISTS) {
                for (int y = 7; y < 29; y += 8) {
                    c.drawCircle(4, y, 1, paint);
                    c.drawLine(10, y, 29, y, paint);
                }
            } else if (id == Box.ID.NETWORK) {
                for (int y = 3; y < 25; y += 14) {
                    c.drawRoundRect(2, y, 30, y + 10, 2, 2, paint);
                    c.drawCircle(7, y + 5, 1, paint);
                }
            } else {
                c.drawRoundRect(4, 5, 28, 27, 3, 3, paint);
                c.drawLine(4, 20, 28, 20, paint);
                c.drawCircle(23, 24, 1, paint);
            }
            c.restore();
        }

        @Override
        public void setAlpha(int a) {
            paint.setAlpha(a);
        }

        @Override
        public void setColorFilter(ColorFilter f) {
            paint.setColorFilter(f);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
