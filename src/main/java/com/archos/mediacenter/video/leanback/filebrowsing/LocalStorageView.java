package com.archos.mediacenter.video.leanback.filebrowsing;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archos.filecorelibrary.ListingEngine;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.filecoreextension.upnp2.ListingEngineFactoryWithUpnp;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.VideosInFolderLoader;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewFolderActions;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.details.PreviewMediaInfo;
import com.archos.mediacenter.video.leanback.details.VideoDetailsActivity;
import com.archos.mediacenter.video.leanback.details.VideoDetailsFragment;
import com.archos.mediacenter.video.player.PlayerService;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediacenter.video.utils.VideoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Network &amp; Files › Local Storage (owner, 3 Nov 2026; mock-ups docs/design/proposals/local-storage/14–21).
 * One full-width charcoal panel: a header (clickable path chips, folder title, status pills, Grid / Sort /
 * Videos only), then two-line rows. The focused row shows its own buttons; OK does the obvious thing; the
 * menu button opens the same actions as a pop-up. Folders open instantly from memory and refresh quietly;
 * nothing loading-related shows for quick folders; slow or failed listings explain themselves in the list area.
 * It lists through the existing FileCore engine and never changes files on disk.
 */
public final class LocalStorageView extends LinearLayout {
    /** What the surrounding Network &amp; Files screen provides. */
    public interface Host {
        /** Library choices changed (saved when leaving, through the browser's existing review). */
        void libraryChanged();
    }

    static final long LOADING_DELAY_MS = 250, WAIT_NOTICE_MS = 5000, TIMEOUT_MS = 30000;
    private static final Set<String> SYSTEM_FOLDERS = new HashSet<>(Arrays.asList(
        "android", "lost.dir", ".thumbnails", "system volume information", "$recycle.bin", ".trashes",
        ".spotlight-v100", ".fseventsd"));
    private static final String GRID = "supernova_browser_grid", SORT = "supernova_browser_sort";

    private final SharedPreferences prefs;
    private final BrowserSelection selection;
    private final Host host;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "SupernovaLocalStorage");
        thread.setDaemon(true);
        return thread;
    });

    private final LinearLayout chips, pills;
    private final TextView title, gridButton, sortButton, filterButton;
    private final ImageView tileIcon;
    private final FrameLayout area;
    private final RecyclerView list;
    private final GridLayoutManager layout;
    private final LinearLayout placeholders, notice;
    private final Rows adapter = new Rows();

    private final List<Box> volumes = new ArrayList<>();
    private final Map<String, List<MetaFile2>> listings = new HashMap<>();
    private final Map<String, Map<String, Video>> indexedByFolder = new HashMap<>();
    private final Map<String, String> focusMemory = new HashMap<>();
    private final List<Object> items = new ArrayList<>();
    private Map<String, Video> indexed = Collections.emptyMap();
    private boolean indexedReady, listed, failed, wantFocus, closed;
    private Uri current;
    private ListingEngine engine;
    private int generation, activePosition = -1;
    private long started;
    private View lastFocus;

    public LocalStorageView(Context c, SharedPreferences prefs, BrowserSelection selection, Host host) {
        super(c);
        this.prefs = prefs;
        this.selection = selection;
        this.host = host;
        setOrientation(VERTICAL);
        setTag("local.storage");
        setBackground(new Panel(c));
        setPadding(dp(11), dp(11), dp(11), dp(11));
        setClipChildren(false);

        // ---- header: folder tile · (path chips / title / pills) · tools ----
        LinearLayout head = new LinearLayout(c);
        head.setTag("local.header");
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(dp(3), dp(2), dp(3), dp(9));
        FrameLayout tile = new FrameLayout(c);
        GradientDrawable tileBackground = new GradientDrawable();
        tileBackground.setColor(SupernovaStyle.PANEL_REST);
        tileBackground.setCornerRadius(dp(11));
        tile.setBackground(tileBackground);
        tileIcon = new ImageView(c);
        tile.addView(tileIcon, new FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER));
        head.addView(tile, new LayoutParams(dp(48), dp(48)));
        LinearLayout column = new LinearLayout(c);
        column.setOrientation(VERTICAL);
        chips = new LinearLayout(c);
        chips.setTag("local.chips");
        chips.setGravity(Gravity.CENTER_VERTICAL);
        column.addView(chips, new LayoutParams(-1, -2));
        title = text("", 18, SupernovaStyle.TEXT);
        title.setTag("local.title");
        title.setTypeface(SupernovaStyle.LABEL);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setPadding(0, dp(2), 0, dp(3));
        column.addView(title, new LayoutParams(-1, -2));
        pills = new LinearLayout(c);
        pills.setTag("local.pills");
        column.addView(pills, new LayoutParams(-1, -2));
        LayoutParams columnSize = new LayoutParams(0, -2, 1);
        columnSize.leftMargin = dp(12);
        head.addView(column, columnSize);
        gridButton = tool("grid", "", this::toggleGrid);
        gridButton.setTag("local.grid");
        sortButton = tool("sort", "", this::chooseSort);
        sortButton.setTag("local.sort");
        filterButton = tool("filter", "", this::toggleFilter);
        filterButton.setTag("local.filter");
        for (TextView tool : new TextView[] {gridButton, sortButton, filterButton}) {
            LayoutParams size = new LayoutParams(-2, dp(32));
            size.leftMargin = dp(7);
            head.addView(tool, size);
        }
        addView(head, new LayoutParams(-1, -2));
        View rule = new View(c);
        rule.setBackgroundColor(SupernovaStyle.HEADING_LINE);
        LayoutParams ruleSize = new LayoutParams(-1, Math.max(1, dp(.5f)));
        ruleSize.leftMargin = dp(3);
        ruleSize.rightMargin = dp(3);
        ruleSize.bottomMargin = dp(5);
        addView(rule, ruleSize);

        // ---- list area: rows, or placeholder rows, or a notice ----
        area = new FrameLayout(c);
        list = new RecyclerView(c);
        list.setTag("local.list");
        layout = new GridLayoutManager(c, 1);
        list.setLayoutManager(layout);
        list.setAdapter(adapter);
        list.setItemAnimator(null);
        list.setClipToPadding(false);
        list.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        area.addView(list, new FrameLayout.LayoutParams(-1, -1));
        placeholders = new LinearLayout(c);
        placeholders.setTag("local.placeholders");
        placeholders.setOrientation(VERTICAL);
        placeholders.setVisibility(GONE);
        area.addView(placeholders, new FrameLayout.LayoutParams(-1, -1));
        notice = new LinearLayout(c);
        notice.setTag("local.notice");
        notice.setOrientation(VERTICAL);
        notice.setGravity(Gravity.CENTER);
        notice.setVisibility(GONE);
        area.addView(notice, new FrameLayout.LayoutParams(-1, -1));
        addView(area, new LayoutParams(-1, 0, 1));

        refreshTools();
        showDrives();
    }

    // ---- public contract (used by UniversalFileBrowser) -------------------------------------------------------

    /** Where the panel is: a folder, or null for the drive list. */
    public Uri location() {
        return current;
    }

    public void setVolumes(List<Box> boxes) {
        List<Box> copy = new ArrayList<>(boxes);
        if (signature(copy).equals(signature(volumes)))
            return;
        View focused = findFocus();
        Object tag = focused == null ? null : focused.getTag();
        volumes.clear();
        volumes.addAll(copy);
        if (current == null) {
            render();
            if (tag != null)
                list.post(() -> {
                    View again = list.findViewWithTag(tag);
                    if (again != null)
                        again.requestFocus();
                });
        }
    }

    /** Opens a folder (or a drive's top level). */
    public void open(Uri uri) {
        if (closed || uri == null)
            return;
        if (current != null && uri.equals(current) && !failed)
            return;
        rememberFocus();
        wantFocus = true;
        current = uri;
        load(true);
    }

    /** Back: one folder up, then the drive list. Returns false at the drive list (the screen leaves). */
    public boolean back() {
        if (current == null)
            return false;
        rememberFocus();
        wantFocus = true;
        Uri parent = parent(current);
        if (parent == null) {
            focusMemory.put("", current.toString());
            showDrives();
        } else {
            focusMemory.put(parent.toString(), current.toString());
            current = parent;
            load(true);
        }
        return true;
    }

    /** Puts focus where the user left it (or the first row). */
    public void focusDefault() {
        wantFocus = true;
        if (!restoreFocus() && !requestFirstFocusable())
            requestFocus();
    }

    /** True while focus is in the header, so Up may continue to the top navigation. */
    public boolean atTop() {
        View focused = findFocus();
        return focused != null && (isInside(focused, chips) || focused == gridButton || focused == sortButton ||
                                   focused == filterButton);
    }

    public void close() {
        closed = true;
        stopListing();
        main.removeCallbacksAndMessages(null);
        worker.shutdownNow();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopListing();
        main.removeCallbacksAndMessages(null);
        getViewTreeObserver().removeOnGlobalFocusChangeListener(focusWatcher);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(focusWatcher);
        if (current != null && !listed && engine == null)
            load(false);
    }

    // ---- loading ------------------------------------------------------------------------------------------------

    private void showDrives() {
        stopListing();
        current = null;
        failed = false;
        listed = true;
        indexed = Collections.emptyMap();
        indexedReady = true;
        render();
    }

    /**
     * Header switches at once. A folder seen this session shows immediately from memory and refreshes quietly;
     * otherwise placeholder rows appear only if the listing takes longer than {@link #LOADING_DELAY_MS}.
     */
    private void load(boolean resetScroll) {
        stopListing();
        failed = false;
        Uri target = current;
        int token = ++generation;
        List<MetaFile2> cached = listings.get(target.toString());
        Map<String, Video> cachedIndex = indexedByFolder.get(target.toString());
        indexed = cachedIndex != null ? cachedIndex : Collections.emptyMap();
        indexedReady = cachedIndex != null;
        listed = cached != null;
        items.clear();
        if (cached != null)
            items.addAll(cached);
        started = System.currentTimeMillis();
        render();
        if (resetScroll && cached == null)
            layout.scrollToPosition(0);
        if (cached == null)
            main.postDelayed(() -> {
                if (token == generation && !listed && !failed)
                    showPlaceholders();
            }, LOADING_DELAY_MS);
        main.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (token != generation || listed || failed)
                    return;
                showWaiting();
                main.postDelayed(this, 1000);
            }
        }, WAIT_NOTICE_MS);
        try {
            engine = ListingEngineFactoryWithUpnp.getListingEngineForUrl(getContext(), target);
            boolean all = prefs.getBoolean(UniversalFileBrowser.ALL_FILES, false);
            engine.setKeepHiddenFiles(all);
            engine.setFilter(all ? null : VideoUtils.getVideoFilterMimeTypes(), null);
            engine.setSortOrder(sortOrder());
            engine.setListingTimeOut((int)TIMEOUT_MS);
            engine.setListener(new ListingEngine.Listener() {
                private boolean valid() {
                    return !closed && token == generation && target.equals(current);
                }

                public void onListingStart() {
                }

                public void onListingUpdate(List<? extends MetaFile2> result) {
                    if (!valid())
                        return;
                    List<MetaFile2> visible = visibleFiles(result, all);
                    listings.put(target.toString(), visible);
                    if (listed && sameNames(visible))
                        return; // quiet refresh: nothing changed, nothing moves
                    listed = true;
                    items.clear();
                    items.addAll(visible);
                    render();
                }

                public void onListingEnd() {
                    if (valid() && !listed) {
                        listed = true;
                        listings.put(target.toString(), new ArrayList<>());
                        items.clear();
                        render();
                    }
                    engine = null;
                }

                public void onListingTimeOut() {
                    if (valid())
                        fail("No reply after " + TIMEOUT_MS / 1000 + " seconds.");
                }

                public void onCredentialRequired(Exception e) {
                    if (valid())
                        fail("This location needs a username and password.");
                }

                public void onListingFatalError(Exception e, ListingEngine.ErrorEnum code) {
                    if (valid())
                        fail(getContext().getString(ListingEngine.getErrorStringResId(code)) + ".");
                }

                public void onListingFileInfoUpdate(Uri uri, MetaFile2 file) {
                }
            });
            engine.start();
        } catch (RuntimeException failure) {
            fail("This location could not be opened.");
        }
        if (cachedIndex == null)
            worker.execute(() -> lookUp(target, token));
    }

    /** Library facts for the second lines; they fade in when they arrive. Nothing moves. */
    private void lookUp(Uri target, int token) {
        Map<String, Video> found = new HashMap<>();
        try (Cursor cursor =
                 new VideosInFolderLoader(getContext().getApplicationContext(), target.toString()).loadInBackground()) {
            if (cursor != null) {
                VideoCursorMapper mapper = new VideoCursorMapper();
                mapper.publicBindColumns(cursor);
                while (cursor.moveToNext()) {
                    Video video = (Video)mapper.publicBind(cursor);
                    found.put(video.getFileUri().toString(), video);
                }
            }
        } catch (RuntimeException failure) {
            Diagnostics.error("local_storage_lookup", failure);
        }
        main.post(() -> {
            if (closed)
                return;
            indexedByFolder.put(target.toString(), found);
            if (token == generation && target.equals(current)) {
                indexed = found;
                indexedReady = true;
                adapter.notifyItemRangeChanged(0, items.size(), FADE_IN);
                bindPills();
            }
        });
    }

    private void stopListing() {
        generation++;
        if (engine != null) {
            engine.setListener(null);
            engine.abort();
            engine = null;
        }
    }

    private void fail(String reason) {
        stopListing();
        failed = true;
        listed = false;
        String name = locationName();
        notice("Couldn't open " + name,
               reason + " Check the drive is still connected. Your library and watch progress are safe.",
               new String[] {"Retry", "Go back"}, new Runnable[] {() -> {
                   wantFocus = true;
                   load(false);
               }, () -> back()});
        bindPills();
    }

    static List<MetaFile2> visibleFiles(List<? extends MetaFile2> result, boolean all) {
        List<MetaFile2> visible = new ArrayList<>();
        for (MetaFile2 file : result)
            if (all || !(file.isDirectory() && isSystemFolder(file.getName())))
                visible.add(file);
        return visible;
    }

    static boolean isSystemFolder(String name) {
        return name != null && (name.startsWith(".") || SYSTEM_FOLDERS.contains(name.toLowerCase(Locale.ROOT)));
    }

    private boolean sameNames(List<MetaFile2> fresh) {
        if (fresh.size() != items.size())
            return false;
        for (int i = 0; i < fresh.size(); i++)
            if (!(items.get(i) instanceof MetaFile2) ||
                !((MetaFile2)items.get(i)).getUri().equals(fresh.get(i).getUri()))
                return false;
        return true;
    }

    // ---- rendering ----------------------------------------------------------------------------------------------

    private void render() {
        notice.setVisibility(GONE);
        placeholders.setVisibility(GONE);
        list.setVisibility(VISIBLE);
        if (current == null) {
            items.clear();
            items.addAll(volumes);
        }
        boolean grid = current != null && prefs.getBoolean(GRID, false);
        layout.setSpanCount(grid ? 4 : 1);
        activePosition = -1;
        bindHeader();
        adapter.notifyDataSetChanged();
        if (current != null && listed && items.isEmpty())
            notice("Nothing to show here",
                   prefs.getBoolean(UniversalFileBrowser.ALL_FILES, false)
                       ? "This folder is empty."
                       : "No videos or folders. Choose Videos only › All files to see everything.",
                   new String[] {"Go back"}, new Runnable[] {() -> back()});
        else if (current == null && volumes.isEmpty())
            notice("No drives found", "Connect a USB drive, or check the device's storage.", new String[0],
                   new Runnable[0]);
        if (wantFocus && (listed || current == null))
            list.post(this::focusAfterRender);
    }

    private void focusAfterRender() {
        if (!wantFocus || closed)
            return;
        if (notice.getVisibility() == VISIBLE) {
            View first = notice.findFocus() == null ? firstFocusable(notice) : null;
            if (first != null)
                first.requestFocus();
            wantFocus = false;
            return;
        }
        if (restoreFocus() || requestFirstFocusable())
            wantFocus = false;
    }

    private boolean restoreFocus() {
        String key = current == null ? "" : current.toString();
        String remembered = focusMemory.get(key);
        int position = -1;
        for (int i = 0; remembered != null && i < items.size(); i++)
            if (remembered.equals(identity(items.get(i)))) {
                position = i;
                break;
            }
        if (position < 0)
            return false;
        focusPosition(position);
        return true;
    }

    private boolean requestFirstFocusable() {
        if (items.isEmpty())
            return false;
        focusPosition(0);
        return true;
    }

    /** Focuses a row, waiting (a few frames at most) for the list to lay it out. */
    private void focusPosition(int position) {
        RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(position);
        if (holder != null && holder.itemView.isShown()) {
            holder.itemView.requestFocus();
            return;
        }
        layout.scrollToPositionWithOffset(position, dp(40));
        int token = generation;
        list.post(new Runnable() {
            int tries;

            @Override
            public void run() {
                if (closed || token != generation)
                    return;
                RecyclerView.ViewHolder later = list.findViewHolderForAdapterPosition(position);
                if (later != null && later.itemView.isShown())
                    later.itemView.requestFocus();
                else if (tries++ < 8)
                    list.postOnAnimation(this);
            }
        });
    }

    private void rememberFocus() {
        View focused = findFocus();
        if (focused == null || !isInside(focused, list))
            return;
        View row = list.findContainingItemView(focused);
        if (row == null)
            return;
        int position = list.getChildAdapterPosition(row);
        if (position >= 0 && position < items.size())
            focusMemory.put(current == null ? "" : current.toString(), identity(items.get(position)));
    }

    private static String identity(Object item) {
        if (item instanceof MetaFile2)
            return ((MetaFile2)item).getUri().toString();
        if (item instanceof Box)
            return driveUri((Box)item).toString();
        return "";
    }

    /** Placeholder rows in the final two-line shape; focus waits on the first one. */
    private void showPlaceholders() {
        bindPills();
        placeholders.removeAllViews();
        int[] first = {280, 300, 270, 305, 285, 295}, second = {380, 395, 370, 385, 375, 390};
        for (int i = 0; i < first.length; i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(10), 0, dp(10), 0);
            if (i == 0) {
                row.setFocusable(true);
                row.setBackground(SupernovaStyle.rowWithActions(getContext()));
                row.setTag("local.placeholder.first");
            }
            row.addView(bar(16, 16, .1f), new LayoutParams(dp(16), dp(16)));
            LinearLayout lines = new LinearLayout(getContext());
            lines.setOrientation(VERTICAL);
            LayoutParams l1 = new LayoutParams(dp(first[i]), dp(12));
            lines.addView(bar(first[i], 12, .1f), l1);
            LayoutParams l2 = new LayoutParams(dp(second[i]), dp(9));
            l2.topMargin = dp(6);
            lines.addView(bar(second[i], 9, .07f), l2);
            LayoutParams linesSize = new LayoutParams(0, -2, 1);
            linesSize.leftMargin = dp(10);
            row.addView(lines, linesSize);
            placeholders.addView(row, new LayoutParams(-1, dp(50)));
        }
        list.setVisibility(INVISIBLE);
        placeholders.setVisibility(VISIBLE);
        if (wantFocus || hasFocus())
            placeholders.getChildAt(0).requestFocus();
    }

    private void showWaiting() {
        long seconds = (System.currentTimeMillis() - started) / 1000;
        notice("Still waiting for " + locationName() + "…",
               "It's taking longer than usual (" + seconds + " s). Your library and watch progress are safe.",
               new String[] {"Cancel"}, new Runnable[] {() -> back()});
    }

    private void notice(String heading, String body, String[] labels, Runnable[] actions) {
        boolean keepFocus = hasFocus() || wantFocus;
        String focusedLabel = notice.findFocus() instanceof TextView ? String.valueOf(
            ((TextView)notice.findFocus()).getText()) : null;
        notice.removeAllViews();
        TextView head = text(heading, 16, SupernovaStyle.TEXT);
        head.setTypeface(SupernovaStyle.LABEL);
        head.setGravity(Gravity.CENTER);
        notice.addView(head, new LayoutParams(-2, -2));
        TextView detail = text(body, 11, SupernovaStyle.TEXT_SECONDARY);
        detail.setGravity(Gravity.CENTER);
        detail.setMaxWidth(dp(450));
        LayoutParams detailSize = new LayoutParams(-2, -2);
        detailSize.topMargin = dp(7);
        notice.addView(detail, detailSize);
        LinearLayout buttons = new LinearLayout(getContext());
        for (int i = 0; i < labels.length; i++) {
            TextView button = button(labels[i].equals("Retry") ? "history" : labels[i].equals("Cancel") ? "close"
                                                                                                      : "chevrons",
                                     labels[i], actions[i]);
            button.setTag("local.notice." + labels[i].toLowerCase(Locale.ROOT).replace(' ', '_'));
            LayoutParams size = new LayoutParams(-2, dp(32));
            if (i > 0)
                size.leftMargin = dp(8);
            buttons.addView(button, size);
        }
        LayoutParams buttonsSize = new LayoutParams(-2, -2);
        buttonsSize.topMargin = dp(14);
        notice.addView(buttons, buttonsSize);
        list.setVisibility(INVISIBLE);
        placeholders.setVisibility(GONE);
        notice.setVisibility(VISIBLE);
        if (keepFocus && buttons.getChildCount() > 0) {
            View target = buttons.getChildAt(0);
            for (int i = 0; focusedLabel != null && i < buttons.getChildCount(); i++)
                if (focusedLabel.contentEquals(((TextView)buttons.getChildAt(i)).getText()))
                    target = buttons.getChildAt(i);
            target.requestFocus();
        }
    }

    // ---- header -------------------------------------------------------------------------------------------------

    private void bindHeader() {
        chips.removeAllViews();
        Box drive = current == null ? null : driveOf(current);
        tileIcon.setImageDrawable(new PreviewIcon(current == null ? "local" : "folder"));
        if (current == null) {
            title.setText("Local Storage");
        } else {
            List<Uri> trail = trail(current, drive);
            addChip("Local Storage", "local", this::navigateToDrives);
            for (int i = 0; i < trail.size() - 1; i++) {
                Uri target = trail.get(i);
                addChip(i == 0 ? driveName(drive) : String.valueOf(target.getLastPathSegment()),
                        null, () -> navigateTo(target));
            }
            title.setText(trail.size() == 1 ? driveName(drive) : String.valueOf(current.getLastPathSegment()));
        }
        chips.setVisibility(chips.getChildCount() == 0 ? GONE : VISIBLE);
        bindPills();
        refreshTools();
    }

    private void addChip(String label, String icon, Runnable run) {
        if (chips.getChildCount() > 0) {
            TextView arrow = text("›", 11, SupernovaStyle.TEXT_SECONDARY);
            arrow.setPadding(dp(2), 0, dp(2), 0);
            chips.addView(arrow);
        }
        TextView chip = text(label, 11, SupernovaStyle.TEXT_SECONDARY);
        chip.setTag("local.chip." + (chips.getChildCount() + 1) / 2);
        chip.setSingleLine(true);
        chip.setFocusable(true);
        chip.setFocusableInTouchMode(true);
        chip.setPadding(dp(6), dp(2), dp(6), dp(2));
        chip.setBackground(SupernovaStyle.chip(getContext()));
        chip.setTextColor(SupernovaStyle.buttonContent());
        if (icon != null) {
            PreviewIcon.apply(chip, icon, 12);
            chip.setCompoundDrawablePadding(dp(4));
            SupernovaStyle.tintIcons(chip);
        }
        chip.setOnClickListener(v -> run.run());
        chip.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN || key != KeyEvent.KEYCODE_DPAD_DOWN)
                return false;
            focusDefault();
            return true;
        });
        chips.addView(chip, new LayoutParams(-2, -2));
    }

    private void bindPills() {
        pills.removeAllViews();
        if (current == null) {
            pill(volumes.size() == 1 ? "1 drive" : volumes.size() + " drives", 0);
            return;
        }
        if (failed) {
            pill("Not reachable", 0xFFF0B44C);
            return;
        }
        if (!listed) {
            if (System.currentTimeMillis() - started >= LOADING_DELAY_MS)
                pill("Loading…", 0);
            return;
        }
        int videos = 0, folders = 0, watched = 0, progress = 0;
        long bytes = 0;
        for (Object item : items)
            if (item instanceof MetaFile2) {
                MetaFile2 file = (MetaFile2)item;
                if (file.isDirectory())
                    folders++;
                else {
                    videos++;
                    bytes += Math.max(0, file.length());
                    Video video = indexed.get(file.getUri().toString());
                    if (video != null && video.isWatched())
                        watched++;
                    else if (video != null && video.getResumeMs() > 0)
                        progress++;
                }
            }
        boolean all = prefs.getBoolean(UniversalFileBrowser.ALL_FILES, false);
        if (videos > 0)
            pill(videos + (all ? (videos == 1 ? " file" : " files") : (videos == 1 ? " video" : " videos")), 0);
        if (folders > 0)
            pill(folders + (folders == 1 ? " folder" : " folders"), 0);
        if (bytes > 0)
            pill(Formatter.formatShortFileSize(getContext(), bytes), 0);
        String library = libraryState(current);
        if (library.equals("included"))
            pill("In your library", 0xFF5AD18A);
        else if (library.equals("excluded"))
            pill("Excluded", 0xFF8A93A3);
        if (watched + progress > 0)
            pill(join(watched > 0 ? watched + " watched" : "", progress > 0 ? progress + " in progress" : ""), 0);
        Box drive = driveOf(current);
        if (drive != null && driveUri(drive).equals(current)) {
            String space = space(drive);
            if (!space.isEmpty())
                pill(space, 0);
        }
    }

    private void pill(String label, int dot) {
        TextView pill = text(label, 9.5f, 0xFFDFE5F1);
        pill.setTag("local.pill");
        pill.setSingleLine(true);
        pill.setPadding(dp(6), dp(2), dp(6), dp(2));
        GradientDrawable background = new GradientDrawable();
        background.setColor(0x14FFFFFF);
        background.setCornerRadius(dp(6));
        pill.setBackground(background);
        if (dot != 0) {
            GradientDrawable mark = new GradientDrawable();
            mark.setShape(GradientDrawable.OVAL);
            mark.setColor(dot);
            mark.setSize(dp(5), dp(5));
            pill.setCompoundDrawablesWithIntrinsicBounds(mark, null, null, null);
            pill.setCompoundDrawablePadding(dp(4));
        }
        LayoutParams size = new LayoutParams(-2, -2);
        size.rightMargin = dp(5);
        size.topMargin = dp(2);
        pills.addView(pill, size);
    }

    // ---- tools --------------------------------------------------------------------------------------------------

    private TextView tool(String icon, String label, Runnable run) {
        TextView view = button(icon, label, run);
        view.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN || key != KeyEvent.KEYCODE_DPAD_DOWN)
                return false;
            focusDefault();
            return true;
        });
        return view;
    }

    private void refreshTools() {
        boolean grid = prefs.getBoolean(GRID, false);
        gridButton.setText(grid ? "List" : "Grid");
        String[] sorts = {"Name", "Name ↓", "Date", "Date ↓", "Size", "Size ↓"};
        sortButton.setText("Sort: " + sorts[Math.max(0, Math.min(5, prefs.getInt(SORT, 0)))]);
        filterButton.setText(prefs.getBoolean(UniversalFileBrowser.ALL_FILES, false) ? "All files" : "Videos only");
        int visibility = current == null ? GONE : VISIBLE;
        gridButton.setVisibility(visibility);
        sortButton.setVisibility(visibility);
        filterButton.setVisibility(visibility);
    }

    private void toggleGrid() {
        prefs.edit().putBoolean(GRID, !prefs.getBoolean(GRID, false)).apply();
        render();
    }

    private void chooseSort() {
        String[] labels = {"Name (A–Z)", "Name (Z–A)", "Oldest first", "Newest first", "Smallest first",
                           "Largest first"};
        PreviewDialog.choose(getContext(), "Sort", labels, Math.max(0, Math.min(5, prefs.getInt(SORT, 0))), n -> {
            prefs.edit().putInt(SORT, n).apply();
            listings.clear();
            refreshTools();
            if (current != null)
                load(false);
        });
    }

    private void toggleFilter() {
        boolean all = !prefs.getBoolean(UniversalFileBrowser.ALL_FILES, false);
        prefs.edit().putBoolean(UniversalFileBrowser.ALL_FILES, all).apply();
        listings.clear();
        refreshTools();
        if (current != null)
            load(false);
    }

    private ListingEngine.SortOrder sortOrder() {
        switch (Math.max(0, Math.min(5, prefs.getInt(SORT, 0)))) {
            case 1: return ListingEngine.SortOrder.SORT_BY_NAME_DESC;
            case 2: return ListingEngine.SortOrder.SORT_BY_DATE_ASC;
            case 3: return ListingEngine.SortOrder.SORT_BY_DATE_DESC;
            case 4: return ListingEngine.SortOrder.SORT_BY_SIZE_ASC;
            case 5: return ListingEngine.SortOrder.SORT_BY_SIZE_DESC;
            default: return ListingEngine.SortOrder.SORT_BY_NAME_ASC;
        }
    }

    // ---- rows ---------------------------------------------------------------------------------------------------

    private static final Object FADE_IN = new Object();

    private final class Rows extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public int getItemViewType(int position) {
            return layout.getSpanCount() > 1 ? 1 : 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new RecyclerView.ViewHolder(type == 1 ? new Card(getContext()) : new Row(getContext())) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            if (payloads.contains(FADE_IN) && holder.itemView instanceof Row) {
                ((Row)holder.itemView).bind(items.get(position), position, true);
                return;
            }
            onBindViewHolder(holder, position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof Row)
                ((Row)holder.itemView).bind(items.get(position), position, false);
            else
                ((Card)holder.itemView).bind(items.get(position), position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    /** Two-line list row: icon · name / facts · (buttons while focused) · watched tick. */
    private final class Row extends LinearLayout {
        final ImageView icon = new ImageView(getContext()), tick = new ImageView(getContext());
        final TextView line1, line2;
        final View pending;
        final LinearLayout buttons;
        Object item;
        int position;

        Row(Context c) {
            super(c);
            setGravity(Gravity.CENTER_VERTICAL);
            setFocusable(true);
            setFocusableInTouchMode(true);
            // The row itself takes focus first; Right moves onto its buttons (they are reached explicitly).
            setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
            setBackground(SupernovaStyle.rowWithActions(c));
            setPadding(dp(10), 0, dp(10), 0);
            setLayoutParams(new RecyclerView.LayoutParams(-1, dp(50)));
            addView(icon, new LayoutParams(dp(16), dp(16)));
            LinearLayout two = new LinearLayout(c);
            two.setOrientation(VERTICAL);
            line1 = text("", 13, SupernovaStyle.TEXT);
            line1.setTypeface(SupernovaStyle.LABEL);
            line1.setSingleLine(true);
            line1.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            two.addView(line1, new LayoutParams(-1, -2));
            FrameLayout second = new FrameLayout(c);
            line2 = text("", 10.5f, SupernovaStyle.TEXT_SECONDARY);
            line2.setSingleLine(true);
            line2.setEllipsize(TextUtils.TruncateAt.END);
            second.addView(line2, new FrameLayout.LayoutParams(-1, -2));
            pending = bar(370, 9, .07f);
            second.addView(pending, new FrameLayout.LayoutParams(dp(370), dp(9), Gravity.CENTER_VERTICAL));
            LayoutParams secondSize = new LayoutParams(-1, dp(14));
            secondSize.topMargin = dp(3);
            two.addView(second, secondSize);
            LayoutParams twoSize = new LayoutParams(0, -2, 1);
            twoSize.leftMargin = dp(10);
            twoSize.rightMargin = dp(8);
            addView(two, twoSize);
            buttons = new LinearLayout(c);
            buttons.setVisibility(GONE);
            addView(buttons, new LayoutParams(-2, -2));
            tick.setImageDrawable(new PreviewIcon("check"));
            tick.setColorFilter(0xFF5AD18A);
            LayoutParams tickSize = new LayoutParams(dp(13), dp(13));
            tickSize.leftMargin = dp(8);
            addView(tick, tickSize);
            setOnClickListener(v -> primary(item));
            setOnKeyListener((v, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    showButtons(this, true);
                    if (buttons.getChildCount() > 0)
                        buttons.getChildAt(0).requestFocus();
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_LEFT)
                    return true;
                if (key == KeyEvent.KEYCODE_MENU) {
                    menu(item);
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_UP && this.position == 0) {
                    View chip = lastChip();
                    if (chip != null) {
                        chip.requestFocus();
                        return true;
                    }
                }
                return false;
            });
            setOnLongClickListener(v -> {
                menu(item);
                return true;
            });
        }

        void bind(Object value, int at, boolean fade) {
            item = value;
            position = at;
            setTag(value instanceof Box ? "storage:" + driveUri((Box)value) : identity(value));
            List<Action> actions = actions(value);
            buttons.removeAllViews();
            for (int i = 0; i < actions.size(); i++) {
                Action action = actions.get(i);
                TextView button = button(action.icon, action.label, action.run);
                button.setTag("local.row.action." + action.label.toLowerCase(Locale.ROOT).replace(' ', '_'));
                final int index = i;
                button.setOnKeyListener((v, key, event) -> buttonKey(this, index, key, event));
                LayoutParams size = new LayoutParams(-2, dp(31));
                size.leftMargin = dp(6);
                buttons.addView(button, size);
            }
            buttons.setVisibility(at == activePosition ? VISIBLE : GONE);
            setActivated(false);
            boolean excluded = false;
            tick.setVisibility(GONE);
            if (value instanceof Box) {
                Box drive = (Box)value;
                icon.setImageDrawable(new PreviewIcon("local"));
                line1.setText(driveName(drive));
                line2.setText(join(driveKind(drive), space(drive)));
                showSecond(true, false);
            } else {
                MetaFile2 file = (MetaFile2)value;
                icon.setImageDrawable(new PreviewIcon(file.isDirectory() ? "folder" : "video-file"));
                line1.setText(file.isDirectory() ? file.getName() : withoutExtension(file.getName()));
                String library = libraryState(file.getUri());
                excluded = library.equals("excluded");
                if (file.isDirectory()) {
                    line2.setText(selection.roots.contains(BrowserSelection.canonical(file.getUri()))
                                      ? "Folder · Library folder"
                                      : excluded ? "Folder · Excluded" : "Folder");
                    showSecond(true, false);
                } else {
                    Video video = indexed.get(file.getUri().toString());
                    line2.setText(describe(file, video, excluded));
                    showSecond(indexedReady, fade);
                    tick.setVisibility(video != null && video.isWatched() ? VISIBLE : GONE);
                }
            }
            setAlpha(excluded ? .55f : 1f);
            icon.setColorFilter(SupernovaStyle.TEXT);
        }

        private void showSecond(boolean ready, boolean fade) {
            pending.setVisibility(ready ? GONE : VISIBLE);
            line2.setVisibility(ready ? VISIBLE : INVISIBLE);
            if (ready && fade) {
                line2.setAlpha(0f);
                line2.animate().alpha(1f).setDuration(180).start();
            } else
                line2.setAlpha(1f);
        }
    }

    /** Poster-grid card (Grid view): artwork or icon, name underneath; OK as the row; menu for actions. */
    private final class Card extends LinearLayout {
        final ImageView art = new ImageView(getContext());
        final TextView name;
        Object item;

        Card(Context c) {
            super(c);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER_HORIZONTAL);
            setFocusable(true);
            setFocusableInTouchMode(true);
            setBackground(SupernovaStyle.rowWithActions(c));
            setPadding(dp(8), dp(8), dp(8), dp(8));
            setLayoutParams(new RecyclerView.LayoutParams(-1, dp(118)));
            art.setScaleType(ImageView.ScaleType.FIT_CENTER);
            addView(art, new LayoutParams(dp(40), dp(56)));
            name = text("", 11, SupernovaStyle.TEXT);
            name.setGravity(Gravity.CENTER);
            name.setMaxLines(2);
            name.setEllipsize(TextUtils.TruncateAt.END);
            LayoutParams nameSize = new LayoutParams(-1, -2);
            nameSize.topMargin = dp(8);
            addView(name, nameSize);
            setOnClickListener(v -> primary(item));
            setOnLongClickListener(v -> {
                menu(item);
                return true;
            });
            setOnKeyListener((v, key, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_MENU) {
                    menu(item);
                    return true;
                }
                return false;
            });
        }

        void bind(Object value, int at) {
            item = value;
            setTag(identity(value));
            MetaFile2 file = (MetaFile2)value;
            art.setImageDrawable(new PreviewIcon(file.isDirectory() ? "folder" : "video-file"));
            art.setColorFilter(SupernovaStyle.TEXT);
            Video video = indexed.get(file.getUri().toString());
            String label = file.isDirectory() ? file.getName() : withoutExtension(file.getName());
            if (video != null && video.hasScraperData())
                label = matchedTitle(video);
            name.setText(label);
            setAlpha(libraryState(file.getUri()).equals("excluded") ? .55f : 1f);
        }
    }

    // ---- actions ------------------------------------------------------------------------------------------------

    private static final class Action {
        final String icon, label;
        final Runnable run;

        Action(String icon, String label, Runnable run) {
            this.icon = icon;
            this.label = label;
            this.run = run;
        }
    }

    private List<Action> actions(Object item) {
        List<Action> result = new ArrayList<>();
        if (item instanceof Box) {
            result.add(new Action("folder", "Open", () -> navigateTo(driveUri((Box)item))));
            return result;
        }
        MetaFile2 file = (MetaFile2)item;
        Uri uri = file.getUri();
        if (file.isDirectory()) {
            result.add(new Action("folder", "Open", () -> navigateTo(uri)));
        } else {
            Video video = indexed.get(uri.toString());
            boolean resume = video != null && video.getResumeMs() > 0 && !video.isWatched();
            result.add(new Action(resume ? "resume" : "play", resume ? "Resume" : "Play", () -> play(file, true)));
            if (resume)
                result.add(new Action("play", "From start", () -> play(file, false)));
            if (video != null)
                result.add(new Action("info", "Details", () -> getContext().startActivity(
                    new Intent(getContext(), VideoDetailsActivity.class).putExtra(VideoDetailsFragment.EXTRA_VIDEO,
                                                                                   video))));
        }
        String library = libraryState(uri);
        boolean root = selection.roots.contains(BrowserSelection.canonical(uri));
        if (library.equals("excluded"))
            result.add(new Action("library", "Include", () -> changeLibrary(uri, "restore")));
        else if (root)
            result.add(new Action("remove", "Remove", () -> changeLibrary(uri, "remove")));
        else if (library.equals("included"))
            result.add(new Action("remove", "Exclude", () -> changeLibrary(uri, "exclude")));
        else if (file.isDirectory())
            result.add(new Action("library", "Add", () -> changeLibrary(uri, "include")));
        if (file.isDirectory())
            result.add(new Action("save", "Save", () -> PreviewFolderActions.save(getContext(), uri, file.getName())));
        return result;
    }

    /** OK on a row: open the folder or drive; play the video (resuming where you stopped). */
    private void primary(Object item) {
        if (item instanceof Box)
            navigateTo(driveUri((Box)item));
        else if (item instanceof MetaFile2) {
            MetaFile2 file = (MetaFile2)item;
            if (file.isDirectory())
                navigateTo(file.getUri());
            else
                play(file, true);
        }
    }

    /** The menu button keeps the same actions as a pop-up (a shortcut for remote muscle memory). */
    private void menu(Object item) {
        List<Action> actions = actions(item);
        if (actions.isEmpty())
            return;
        String[] labels = new String[actions.size()];
        for (int i = 0; i < labels.length; i++)
            labels[i] = actions.get(i).label;
        String heading = item instanceof Box ? driveName((Box)item) : ((MetaFile2)item).getName();
        PreviewDialog.choose(getContext(), heading, labels, -1, n -> {
            if (n >= 0 && n < actions.size())
                actions.get(n).run.run();
        });
    }

    private void play(MetaFile2 file, boolean resume) {
        Video video = indexed.get(file.getUri().toString());
        Activity activity = activity();
        if (video != null)
            PlayUtils.startVideo(getContext(), video,
                                 resume && video.getResumeMs() > 0 ? PlayerService.RESUME_FROM_LAST_POS
                                                                   : PlayerService.RESUME_NO,
                                 false, -1, null, -1);
        else if (activity != null)
            PlayUtils.openAnyFile(file, activity);
    }

    private void changeLibrary(Uri uri, String change) {
        switch (change) {
            case "restore": selection.restore(uri); break;
            case "remove": selection.remove(uri); break;
            case "exclude": selection.exclude(uri); break;
            default: selection.include(uri); break;
        }
        host.libraryChanged();
        int keep = activePosition;
        adapter.notifyDataSetChanged();
        bindPills();
        if (keep >= 0)
            list.post(() -> focusPosition(keep));
    }

    private void navigateTo(Uri uri) {
        open(uri);
    }

    private void navigateToDrives() {
        rememberFocus();
        wantFocus = true;
        if (current != null) {
            Box drive = driveOf(current);
            if (drive != null)
                focusMemory.put("", driveUri(drive).toString());
        }
        showDrives();
    }

    // ---- focus: the focused row shows its buttons ---------------------------------------------------------------

    private final ViewTreeObserver.OnGlobalFocusChangeListener focusWatcher = this::focusMoved;

    private void focusMoved(View oldFocus, View newFocus) {
        if (newFocus == null || !isInside(newFocus, this)) {
            return;
        }
        lastFocus = newFocus;
        View rowView = isInside(newFocus, list) ? list.findContainingItemView(newFocus) : null;
        int position = rowView == null ? -1 : list.getChildAdapterPosition(rowView);
        if (position != activePosition) {
            for (int i = 0; i < list.getChildCount(); i++) {
                View child = list.getChildAt(i);
                if (child instanceof Row && child != rowView)
                    showButtons((Row)child, false);
            }
            activePosition = position;
        }
        if (rowView instanceof Row) {
            showButtons((Row)rowView, true);
            rowView.setActivated(newFocus != rowView);
        }
    }

    private void showButtons(Row row, boolean show) {
        row.buttons.setVisibility(show && row.buttons.getChildCount() > 0 ? VISIBLE : GONE);
        if (!show)
            row.setActivated(false);
    }

    private boolean buttonKey(Row row, int index, int key, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (key == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (index == 0)
                row.requestFocus();
            else
                row.buttons.getChildAt(index - 1).requestFocus();
            return true;
        }
        if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (index + 1 < row.buttons.getChildCount())
                row.buttons.getChildAt(index + 1).requestFocus();
            return true;
        }
        if (key == KeyEvent.KEYCODE_BACK || key == KeyEvent.KEYCODE_ESCAPE) {
            row.requestFocus();
            return true;
        }
        if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
            int next = row.position + (key == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
            if (next >= 0 && next < items.size())
                focusPosition(next);
            else if (next < 0) {
                View chip = lastChip();
                if (chip != null)
                    chip.requestFocus();
            }
            return true;
        }
        if (key == KeyEvent.KEYCODE_MENU) {
            menu(row.item);
            return true;
        }
        return false;
    }

    private View lastChip() {
        for (int i = chips.getChildCount() - 1; i >= 0; i--)
            if (chips.getChildAt(i).isFocusable())
                return chips.getChildAt(i);
        return null;
    }

    // ---- facts --------------------------------------------------------------------------------------------------

    /** Line 2: matched title · S/E · duration · resolution · audio · size, then Watched or Resume. */
    static String describe(MetaFile2 file, Video video, boolean excluded) {
        List<String> parts = new ArrayList<>();
        if (video != null && video.hasScraperData()) {
            parts.add(matchedTitle(video));
            if (video instanceof Episode)
                parts.add("S" + ((Episode)video).getSeasonNumber() + " E" + ((Episode)video).getEpisodeNumber());
        }
        if (video != null && video.getDurationMs() > 0)
            parts.add(Math.max(1, Math.round(video.getDurationMs() / 60000f)) + " min");
        if (video != null) {
            String resolution = resolution(video.getMeasuredWidth(), video.getMeasuredHeight());
            if (!resolution.isEmpty())
                parts.add(resolution);
            String audio = PreviewMediaInfo.format(video.getCalculatedBestAudioFormat());
            if (audio != null && !audio.isEmpty())
                parts.add(audio);
        }
        if (file.length() > 0)
            parts.add(size(file.length()));
        if (excluded)
            parts.add("Excluded");
        else if (video != null && video.isWatched())
            parts.add("Watched");
        else if (video != null && video.getResumeMs() > 0)
            parts.add(resumeText(video.getResumeMs(), video.getDurationMs()));
        if (parts.isEmpty())
            parts.add(video == null ? "Video · not in your library" : "Video");
        return TextUtils.join(" · ", parts);
    }

    static String matchedTitle(Video video) {
        if (video instanceof Episode && !TextUtils.isEmpty(((Episode)video).getShowName()))
            return ((Episode)video).getShowName();
        return video.getName();
    }

    static String resumeText(int resumeMs, int durationMs) {
        int seconds = Math.max(0, resumeMs / 1000);
        String at = seconds >= 3600 ? String.format(Locale.UK, "%d:%02d:%02d", seconds / 3600, seconds / 60 % 60,
                                                    seconds % 60)
                                    : String.format(Locale.UK, "%d:%02d", seconds / 60, seconds % 60);
        return durationMs > 0 ? "Resume at " + at + " (" + Math.min(99, Math.round(100f * resumeMs / durationMs)) + "%)"
                              : "Resume at " + at;
    }

    static String resolution(int width, int height) {
        if (width <= 0 || height <= 0)
            return "";
        return width >= 3840 || height >= 2160 ? "4K" : width >= 1920 || height >= 1080 ? "1080p"
            : width >= 1280 || height >= 720 ? "720p" : "SD";
    }

    static String withoutExtension(String name) {
        if (name == null)
            return "";
        int dot = name.lastIndexOf('.');
        return dot > 0 && name.length() - dot <= 6 ? name.substring(0, dot) : name;
    }

    static String size(long bytes) {
        double value = bytes;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unit = 0;
        while (value >= 1000 && unit < units.length - 1) {
            value /= 1000;
            unit++;
        }
        return (value >= 100 || unit == 0 ? String.format(Locale.UK, "%.0f", value)
                                          : String.format(Locale.UK, "%.1f", value)) + " " + units[unit];
    }

    private String libraryState(Uri uri) {
        String key = BrowserSelection.canonical(uri);
        for (String excluded : selection.exclusions)
            if (excluded.equals(key) || BrowserSelection.contains(excluded, key))
                return "excluded";
        return selection.included(uri) ? "included" : "";
    }

    // ---- drives -------------------------------------------------------------------------------------------------

    static Uri driveUri(Box box) {
        String path = box.getPath();
        return Uri.fromFile(new File(path == null || path.isEmpty() ? "/storage/emulated/0" : path));
    }

    static String driveName(Box box) {
        if (box == null)
            return "Drive";
        if (box.getBoxId() == Box.ID.FOLDERS)
            return "Internal storage";
        String name = box.getName();
        if (name == null || name.isEmpty())
            return "USB drive";
        return name.replaceFirst("^(External drive|External storage|USB storage):\\s*", "");
    }

    static String driveKind(Box box) {
        return box.getBoxId() == Box.ID.FOLDERS ? "This device"
            : box.getBoxId() == Box.ID.SDCARD ? "SD card" : "USB drive";
    }

    /** "412 GB free of 1 TB", or empty if the drive cannot say. */
    static String space(Box box) {
        try {
            StatFs stat = new StatFs(driveUri(box).getPath());
            long total = stat.getTotalBytes(), free = stat.getAvailableBytes();
            return total > 0 ? size(free) + " free of " + size(total) : "";
        } catch (RuntimeException unavailable) {
            return "";
        }
    }

    private Box driveOf(Uri uri) {
        Box best = null;
        int length = -1;
        String path = uri.getPath() == null ? "" : uri.getPath();
        for (Box box : volumes) {
            String root = driveUri(box).getPath();
            if (root != null && (path.equals(root) || path.startsWith(root.endsWith("/") ? root : root + "/")) &&
                root.length() > length) {
                best = box;
                length = root.length();
            }
        }
        return best;
    }

    /** Folders from the drive's top level down to {@code uri}, both included. */
    private List<Uri> trail(Uri uri, Box drive) {
        List<Uri> result = new ArrayList<>();
        Uri root = drive == null ? Uri.fromFile(new File("/")) : driveUri(drive);
        result.add(root);
        String rootPath = root.getPath() == null ? "/" : root.getPath();
        String path = uri.getPath() == null ? "" : uri.getPath();
        if (path.length() > rootPath.length()) {
            String rest = path.substring(rootPath.length());
            String walked = rootPath.endsWith("/") ? rootPath.substring(0, rootPath.length() - 1) : rootPath;
            for (String segment : rest.split("/")) {
                if (segment.isEmpty())
                    continue;
                walked += "/" + segment;
                result.add(Uri.fromFile(new File(walked)));
            }
        }
        return result;
    }

    /** The folder above, or null when {@code uri} is a drive's top level. */
    private Uri parent(Uri uri) {
        Box drive = driveOf(uri);
        List<Uri> trail = trail(uri, drive);
        return trail.size() < 2 ? null : trail.get(trail.size() - 2);
    }

    private String locationName() {
        Box drive = current == null ? null : driveOf(current);
        return drive == null ? "this drive" : driveName(drive);
    }

    private static String signature(List<Box> boxes) {
        StringBuilder text = new StringBuilder();
        for (Box box : boxes)
            text.append(box.getBoxId()).append(box.getPath()).append(box.getName()).append('|');
        return text.toString();
    }

    // ---- small helpers ------------------------------------------------------------------------------------------

    private TextView button(String icon, String label, Runnable run) {
        TextView view = new TextView(getContext());
        view.setText(label);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        PreviewIcon.apply(view, icon, 14);
        SupernovaStyle.styleButton(view, SupernovaStyle.Surface.PANEL, false);
        view.setOnClickListener(v -> run.run());
        return view;
    }

    private TextView text(String value, float sp, int colour) {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(colour);
        view.setIncludeFontPadding(false);
        view.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        return view;
    }

    private View bar(int widthDp, int heightDp, float strength) {
        View bar = new View(getContext());
        GradientDrawable fill = new GradientDrawable();
        fill.setColor(Color.argb(Math.round(255 * strength), 255, 255, 255));
        fill.setCornerRadius(dp(4));
        bar.setBackground(fill);
        return bar;
    }

    private static String join(String... values) {
        List<String> present = new ArrayList<>();
        for (String value : values)
            if (value != null && !value.isEmpty())
                present.add(value);
        return TextUtils.join(" · ", present);
    }

    private static boolean isInside(View view, View parent) {
        for (View v = view; v != null; v = v.getParent() instanceof View ? (View)v.getParent() : null)
            if (v == parent)
                return true;
        return false;
    }

    private static View firstFocusable(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.isFocusable() && child.isShown())
                return child;
            if (child instanceof ViewGroup) {
                View found = firstFocusable((ViewGroup)child);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    private Activity activity() {
        Context c = getContext();
        while (c instanceof ContextWrapper && !(c instanceof Activity))
            c = ((ContextWrapper)c).getBaseContext();
        return c instanceof Activity ? (Activity)c : null;
    }

    private int dp(float value) {
        return Math.max(1, Math.round(value * getResources().getDisplayMetrics().density));
    }

    /** The charcoal panel (shared glass, faint white edge). */
    private static final class Panel extends GradientDrawable {
        Panel(Context c) {
            setColor(SupernovaStyle.GLASS);
            setCornerRadius(13 * c.getResources().getDisplayMetrics().density);
            setStroke(Math.max(1, Math.round(c.getResources().getDisplayMetrics().density)), SupernovaStyle.GLASS_EDGE);
        }
    }
}
