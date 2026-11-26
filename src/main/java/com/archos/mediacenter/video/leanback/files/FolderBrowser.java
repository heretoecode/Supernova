package com.archos.mediacenter.video.leanback.files;

import com.archos.mediacenter.video.leanback.TvLayout;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.filecoreextension.upnp2.RawListerFactoryWithUpnp;
import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.ArtworkEdge;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediaprovider.ArchosMediaFile;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Browsing a drive, folder or connection inside Files (page sheet docs/design/pages/FILES.md, "Browsing"): the
 * breadcrumb under the tab (a signpost, not focusable), the toolbar on the right (Grid / List · Sort · Order ·
 * Videos only / All files), then the folders and files, List by default (F5). OK opens a folder or plays a file;
 * long-press (or Menu) opens the item's menu beside it (F16); Back goes up one level. Focus never leaves the page
 * while a folder loads (F9), and coming back lands on the item you left (F11).
 */
public final class FolderBrowser extends LinearLayout
{
    /** What OK on a file does: play it (from its library entry when it has one). */
    public interface Opener
    {
        void open(MetaFile2 file, Base inLibrary);
    }

    /** What a file's menu does that needs the hosting page: its Details page, its Fix match page. */
    public interface Actions
    {
        void details(Base item);

        void fixMatch(Base item);
    }

    /** The three sorts (F25): Name · Date modified · Size. */
    static final String[] SORTS = {"Name", "Date modified", "Size"};
    static final String[] SORT_ICONS = {"title", "date", "size"};
    static final int BY_NAME = 0;
    static final int BY_DATE = 1;
    static final int BY_SIZE = 2;

    private static final ExecutorService LIST = Executors.newSingleThreadExecutor();
    private static final int COLUMNS = 4;
    private static final int ROW_HEIGHT_DP = 52;
    private static final String PREFS = "files_browse_";

    private final Deque<Uri> path = new ArrayDeque<>();
    private final Deque<String> names = new ArrayDeque<>();
    private final Map<String, Base> library = new HashMap<>();
    private final List<Base> libraryItems = new ArrayList<>();
    private final TextView crumb;
    private final TextView viewButton;
    private final TextView sortButton;
    private final TextView orderButton;
    private final TextView filterButton;
    private final View holder;
    private final LinearLayout items;
    private final ScrollView scroll;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Opener opener;
    private final Runnable leave;
    private final SharedPreferences preferences;
    private Actions actions = new Actions()
    {
        @Override
        public void details(Base item)
        {
        }

        @Override
        public void fixMatch(Base item)
        {
        }
    };
    private List<MetaFile2> listed = new ArrayList<>();
    private boolean grid;
    private int sort;
    private boolean reversed;
    private boolean allFiles;
    private int generation;
    /** The item to focus once the folder is shown: the folder just left, or the file just played. */
    private String focusName;

    public FolderBrowser(Context context, Opener opener, Runnable leave)
    {
        super(context);
        this.opener = opener;
        this.leave = leave;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        grid = preferences.getBoolean(PREFS + "grid", false);
        sort = Math.max(0, Math.min(2, preferences.getInt(PREFS + "sort", BY_NAME)));
        reversed = preferences.getBoolean(PREFS + "reversed", false);
        allFiles = preferences.getBoolean(PREFS + "all", false);
        setOrientation(VERTICAL);
        setClipChildren(false);
        setClipToPadding(false);
        int side = TvLayout.sideMargin(getContext());
        LinearLayout header = new LinearLayout(context);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(side, 0, side, 0);
        crumb = text("", 16, SupernovaStyle.TEXT_SECONDARY);
        crumb.setSingleLine(true);
        crumb.setEllipsize(TextUtils.TruncateAt.START);
        crumb.setFocusable(false);
        header.addView(crumb, new LayoutParams(0, -2, 1));
        holder = new View(context);
        holder.setFocusable(false);
        header.addView(holder, new LayoutParams(1, 1));
        viewButton = button("grid", "Grid", () -> {
            grid = !grid;
            preferences.edit().putBoolean(PREFS + "grid", grid).apply();
            rememberFocused();
            render();
        });
        header.addView(viewButton, gap());
        sortButton = button("sort", "Sort: Name", this::openSortMenu);
        header.addView(sortButton, gap());
        orderButton = button("order", "A → Z", () -> {
            reversed = !reversed;
            preferences.edit().putBoolean(PREFS + "reversed", reversed).apply();
            rememberFocused();
            render();
        });
        header.addView(orderButton, gap());
        filterButton = button("filter", "Videos only", () -> {
            allFiles = !allFiles;
            preferences.edit().putBoolean(PREFS + "all", allFiles).apply();
            rememberFocused();
            render();
        });
        header.addView(filterButton, gap());
        LayoutParams headerSize = new LayoutParams(-1, -2);
        headerSize.topMargin = dp(18);
        headerSize.bottomMargin = dp(10);
        addView(header, headerSize);
        FrameLayout frame = new FrameLayout(context);
        frame.setClipChildren(false);
        scroll = new BelowBarScroll(context);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        items = new LinearLayout(context);
        items.setOrientation(VERTICAL);
        items.setClipChildren(false);
        items.setClipToPadding(false);
        items.setPadding(side, dp(8), side, dp(24));
        scroll.addView(items);
        frame.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        frame.addView(new ScrollArrows(context, scroll, side), new FrameLayout.LayoutParams(-1, -1));
        addView(frame, new LayoutParams(-1, 0, 1));
        labels();
    }

    public void setActions(Actions latest)
    {
        if (latest != null)
        {
            actions = latest;
        }
    }

    /** The library's videos, so each item can say In library / Not added and show its artwork. */
    public void setLibrary(List<Base> media)
    {
        library.clear();
        libraryItems.clear();
        for (Base item : media)
        {
            if (item instanceof Video && ((Video) item).getFilePath() != null)
            {
                String filePath = ((Video) item).getFilePath();
                library.put(key(filePath), item);
                if (filePath.contains("://"))
                {
                    library.put(key(Uri.parse(filePath).getPath()), item);
                }
                libraryItems.add(item);
            }
        }
    }

    /** Starts at a storage's top folder. */
    public void start(Uri root, String name)
    {
        path.clear();
        names.clear();
        enter(root, name);
    }

    /** Back: up one folder; false when already at the top (the caller then leaves the browser). */
    public boolean up()
    {
        if (path.size() <= 1)
        {
            return false;
        }
        path.pop();
        focusName = names.pop();
        load();
        return true;
    }

    /** The breadcrumb's words, for tests and the spoken name. */
    public String crumb()
    {
        return crumb.getText().toString();
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, android.graphics.Rect previouslyFocusedRect)
    {
        View target = focusName == null ? null : items.findViewWithTag(itemTag(focusName));
        if (target == null)
        {
            target = firstItem();
        }
        if (target != null && target.requestFocus())
        {
            return true;
        }
        if (loading)
        {
            // Asked for focus while the folder is still being read: hold it, and give it to the first item
            // once the folder is shown (never the toolbar, never the top bar).
            holder.setFocusable(true);
            holder.setFocusableInTouchMode(true);
            return holder.requestFocus();
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    private boolean loading;

    private void enter(Uri folder, String name)
    {
        path.push(folder);
        names.push(name);
        focusName = null;
        load();
    }

    private void load()
    {
        int asked = ++generation;
        Uri folder = path.peek();
        crumb.setText(crumbText());
        // Hold focus on the page while the folder is read, so it never jumps to the top bar (F9, F10).
        boolean parked = park();
        loading = true;
        items.removeAllViews();
        // FL2 (owner, 18 Nov 2026): the placeholder cards are back while a folder is read: outlines at once, the
        // real cards take their place as the folder arrives.
        placeholders();
        LIST.submit(() -> {
            List<MetaFile2> files;
            try
            {
                files = new ArrayList<>(RawListerFactoryWithUpnp.getRawListerForUrl(folder).getFileList());
            }
            catch (Exception failed)
            {
                files = null;
            }
            List<MetaFile2> result = files;
            main.post(() -> {
                if (asked != generation)
                {
                    return;
                }
                loading = false;
                boolean holding = parked || holder.isFocused();
                if (result == null)
                {
                    items.removeAllViews();
                    items.addView(text("This folder can't be opened right now", 15, SupernovaStyle.TEXT_SECONDARY));
                    unpark(holding);
                    return;
                }
                listed = result;
                render();
                unpark(holding);
            });
        });
    }

    private boolean park()
    {
        View focus = findFocus();
        if (focus == null || focus == holder || !FilesPage.isInside(items, focus))
        {
            return holder.isFocused();
        }
        holder.setFocusable(true);
        holder.setFocusableInTouchMode(true);
        holder.requestFocus();
        return true;
    }

    private void unpark(boolean parked)
    {
        if (!parked)
        {
            return;
        }
        View target = focusName == null ? null : items.findViewWithTag(itemTag(focusName));
        if (target == null)
        {
            target = firstItem();
        }
        if (target != null)
        {
            target.requestFocus();
        }
        else
        {
            viewButton.requestFocus();
        }
        holder.setFocusable(false);
    }

    private View firstItem()
    {
        return findFocusable(items);
    }

    private static View findFocusable(View view)
    {
        if (view.isFocusable() && view.getVisibility() == VISIBLE)
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++)
            {
                View match = findFocusable(group.getChildAt(index));
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    private void rememberFocused()
    {
        View focus = findFocus();
        if (focus != null && focus.getTag() instanceof String && ((String) focus.getTag()).startsWith(ITEM))
        {
            focusName = ((String) focus.getTag()).substring(ITEM.length());
        }
    }

    private String crumbText()
    {
        List<String> parts = new ArrayList<>(names);
        java.util.Collections.reverse(parts);
        return TextUtils.join("  ›  ", parts);
    }

    /** The toolbar's words follow the state (F5, F25): Grid / List shows the other view. */
    private void labels()
    {
        viewButton.setText(grid ? "List" : "Grid");
        PreviewIcon.apply(viewButton, grid ? "list" : "grid", 14);
        sortButton.setText("Sort: " + SORTS[sort] + "  ▾");
        orderButton.setText(orderLabel(sort, reversed));
        filterButton.setText(allFiles ? "All files" : "Videos only");
        for (TextView button : new TextView[] {viewButton, sortButton, orderButton, filterButton})
        {
            SupernovaStyle.tintIcons(button);
        }
    }

    /** Order's words follow the sort (F25): A → Z / Z → A, Newest / Oldest first, Largest / Smallest first. */
    static String orderLabel(int sort, boolean reversed)
    {
        if (sort == BY_DATE)
        {
            return reversed ? "Oldest first" : "Newest first";
        }
        if (sort == BY_SIZE)
        {
            return reversed ? "Smallest first" : "Largest first";
        }
        return reversed ? "Z → A" : "A → Z";
    }

    private void openSortMenu()
    {
        PreviewDialog.menu(getContext(), sortButton, "Sort", SORTS, SORT_ICONS, sort, choice -> {
            if (choice == sort)
            {
                return;
            }
            sort = choice;
            reversed = false;
            preferences.edit().putInt(PREFS + "sort", sort).putBoolean(PREFS + "reversed", false).apply();
            rememberFocused();
            render();
            sortButton.requestFocus();
        });
    }

    /** Folders always first (F6); then by the sort and order chosen. Pure, for tests. */
    static int compare(MetaFile2 a, MetaFile2 b, int sort, boolean reversed)
    {
        if (a.isDirectory() != b.isDirectory())
        {
            return a.isDirectory() ? -1 : 1;
        }
        int result;
        if (sort == BY_DATE)
        {
            result = Long.compare(b.lastModified(), a.lastModified());
        }
        else if (sort == BY_SIZE && !a.isDirectory())
        {
            result = Long.compare(b.length(), a.length());
        }
        else
        {
            result = a.getName().compareToIgnoreCase(b.getName());
        }
        return reversed ? -result : result;
    }

    private void render()
    {
        labels();
        List<MetaFile2> shown = new ArrayList<>();
        for (MetaFile2 file : listed)
        {
            if (file.getName() == null || file.getName().startsWith("."))
            {
                continue;
            }
            if (file.isDirectory() || allFiles || video(file))
            {
                shown.add(file);
            }
        }
        shown.sort((a, b) -> compare(a, b, sort, reversed));
        boolean hadFocus = FilesPage.isInside(items, findFocus());
        if (hadFocus)
        {
            park();
        }
        items.removeAllViews();
        if (shown.isEmpty())
        {
            // FILES.md (tv/08): a folder with no videos says so, with Show all files.
            items.addView(text(allFiles ? "This folder is empty" : "No videos in this folder", 15,
                               SupernovaStyle.TEXT_SECONDARY));
            if (!allFiles)
            {
                TextView showAll = button("filter", "Show all files", () -> {
                    allFiles = true;
                    preferences.edit().putBoolean(PREFS + "all", true).apply();
                    labels();
                    render();
                });
                showAll.setTag("files.empty.show-all");
                items.addView(showAll);
                if (hadFocus)
                {
                    showAll.requestFocus();
                    return;
                }
            }
            if (hadFocus)
            {
                unpark(true);
            }
            return;
        }
        if (grid)
        {
            renderGrid(shown);
        }
        else
        {
            renderList(shown);
        }
        if (hadFocus)
        {
            unpark(true);
        }
        countFolders(shown);
    }

    private void renderList(List<MetaFile2> shown)
    {
        for (int index = 0; index < shown.size(); index++)
        {
            if (index > 0)
            {
                View line = new View(getContext());
                line.setBackgroundColor(FilesPage.LINE);
                items.addView(line, new LayoutParams(-1, Math.max(1, dp(1))));
            }
            items.addView(listRow(shown.get(index)), new LayoutParams(-1, dp(ROW_HEIGHT_DP)));
        }
    }

    /** Two rows of outline cards (grid) or four outline rows (list), nothing to focus. */
    private void placeholders()
    {
        items.setContentDescription("Looking in this folder");
        for (int line = 0; line < (grid ? 2 : 4); line++)
        {
            LinearLayout row = new LinearLayout(getContext());
            row.setTag("files.placeholder");
            LayoutParams rowSize = new LayoutParams(-1, grid ? -2 : dp(ROW_HEIGHT_DP));
            rowSize.bottomMargin = grid ? dp(22) : dp(6);
            items.addView(row, rowSize);
            for (int index = 0; index < (grid ? COLUMNS : 1); index++)
            {
                View card = new View(getContext())
                {
                    @Override
                    protected void onMeasure(int w, int h)
                    {
                        int width = MeasureSpec.getSize(w);
                        int height = grid ? Math.round(width * 9f / 16f) : MeasureSpec.getSize(h);
                        setMeasuredDimension(width, height);
                    }
                };
                GradientDrawable outline = new GradientDrawable();
                outline.setCornerRadius(dp(8));
                outline.setColor(0x0FFFFFFF);
                outline.setStroke(Math.max(1, dp(1)), 0x33FFFFFF);
                card.setBackground(outline);
                card.setFocusable(false);
                card.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
                LayoutParams cardSize = new LayoutParams(0, grid ? -2 : -1, 1);
                if (index > 0)
                {
                    cardSize.leftMargin = dp(22);
                }
                row.addView(card, cardSize);
            }
        }
    }

    private void renderGrid(List<MetaFile2> shown)
    {
        LinearLayout row = null;
        for (int index = 0; index < shown.size(); index++)
        {
            if (index % COLUMNS == 0)
            {
                row = new LinearLayout(getContext());
                row.setClipChildren(false);
                LayoutParams rowSize = new LayoutParams(-1, -2);
                rowSize.bottomMargin = dp(22);
                items.addView(row, rowSize);
            }
            LayoutParams cardSize = new LayoutParams(0, -2, 1);
            if (index % COLUMNS > 0)
            {
                cardSize.leftMargin = dp(22);
            }
            row.addView(card(shown.get(index)), cardSize);
        }
        for (int index = shown.size() % COLUMNS; index > 0 && index < COLUMNS; index++)
        {
            LayoutParams filler = new LayoutParams(0, 1, 1);
            filler.leftMargin = dp(22);
            row.addView(new View(getContext()), filler);
        }
    }

    private static final String ITEM = "files.item:";

    public static String itemTag(String name)
    {
        return ITEM + name;
    }

    private Base inLibrary(MetaFile2 file)
    {
        if (file.isDirectory())
        {
            return null;
        }
        Base found = library.get(key(file.getUri().toString()));
        return found != null ? found : library.get(key(file.getUri().getPath()));
    }

    /** Library videos inside a folder (any depth). */
    private List<Base> inside(MetaFile2 folder)
    {
        String whole = key(folder.getUri().toString());
        String plain = key(folder.getUri().getPath());
        List<Base> found = new ArrayList<>();
        for (Base item : libraryItems)
        {
            String filePath = key(((Video) item).getFilePath());
            if (filePath.startsWith(slash(whole)) || filePath.startsWith(slash(plain)))
            {
                found.add(item);
            }
        }
        return found;
    }

    private static String slash(String value)
    {
        return value.endsWith("/") ? value : value + "/";
    }

    private boolean excluded(MetaFile2 file)
    {
        return LibraryChanges.excluded(getContext(), file.getUri());
    }

    /** The pill (F20, F23): In library · Excluded · Not added. Pure, for tests. */
    static String pill(boolean inLibrary, boolean excluded)
    {
        if (excluded)
        {
            return "Excluded";
        }
        return inLibrary ? "In library" : "Not added";
    }

    /** A file's second line (F20): "2024 · 14.2 GB"; the size alone when the year is unknown. */
    static String fileLine(int year, String size)
    {
        if (year > 0 && size != null && !size.isEmpty())
        {
            return year + " · " + size;
        }
        if (year > 0)
        {
            return String.valueOf(year);
        }
        return size == null || size.isEmpty() ? "Video" : size;
    }

    /** A folder holding one title: every video in it is the same film or episodes of the same show. */
    static boolean oneTitle(List<Base> held)
    {
        java.util.Set<String> titles = new java.util.HashSet<>();
        for (Base item : held)
        {
            if (item instanceof com.archos.mediacenter.video.browser.adapters.object.Episode)
            {
                titles.add("show:" + ((com.archos.mediacenter.video.browser.adapters.object.Episode) item).getShowName());
            }
            else if (item instanceof com.archos.mediacenter.video.browser.adapters.object.Movie)
            {
                titles.add("movie:" + ((com.archos.mediacenter.video.browser.adapters.object.Movie) item).getOnlineId());
            }
            else
            {
                return false;
            }
        }
        return titles.size() == 1;
    }

    /** "Folder · 31 videos · 48.6 GB" (FILES.md); the size from the library at once for folders in it. */
    static String folderLine(int videos, String size)
    {
        String line = folderLine(videos);
        return videos > 0 && size != null && !size.isEmpty() ? line + " · " + size : line;
    }

    /** A folder's second line (F23): "Folder · 86 videos"; just "Folder" while counting or when there are none. */
    static String folderLine(int videos)
    {
        if (videos <= 0)
        {
            return "Folder";
        }
        return "Folder · " + String.format(Locale.UK, "%,d", videos) + (videos == 1 ? " video" : " videos");
    }

    private String secondLine(MetaFile2 file, Base inLibrary)
    {
        if (file.isDirectory())
        {
            List<Base> held = inside(file);
            long bytes = 0;
            for (Base item : held)
            {
                bytes += Math.max(0, ((Video) item).getSize());
            }
            String size = bytes > 0 ? android.text.format.Formatter.formatShortFileSize(getContext(), bytes) : "";
            return folderLine(held.size(), size);
        }
        long bytes = file.length();
        String size = bytes > 0 ? android.text.format.Formatter.formatShortFileSize(getContext(), bytes) : "";
        int year = inLibrary instanceof com.archos.mediacenter.video.browser.adapters.object.Movie
            ? ((com.archos.mediacenter.video.browser.adapters.object.Movie) inLibrary).getYear() : 0;
        return fileLine(year, size);
    }

    private String title(MetaFile2 file, Base inLibrary)
    {
        if (inLibrary instanceof com.archos.mediacenter.video.browser.adapters.object.Episode)
        {
            return inLibrary.getName();
        }
        return inLibrary != null && inLibrary.getName() != null ? inLibrary.getName() : file.getName();
    }

    private View listRow(MetaFile2 file)
    {
        Context context = getContext();
        Base inLibrary = inLibrary(file);
        boolean folder = file.isDirectory();
        boolean playable = folder || video(file);
        boolean isIn = folder ? !inside(file).isEmpty() : inLibrary != null;
        String pill = playable ? pill(isIn, excluded(file)) : null;
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(playable);
        row.setFocusableInTouchMode(playable);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        row.setTag(itemTag(file.getName()));
        int pad = dp(20);
        row.setPadding(pad, 0, pad, 0);
        TextView mark = new TextView(context);
        PreviewIcon.apply(mark, folder ? "folder" : playable ? "film" : "file", 16);
        row.addView(mark, new LayoutParams(dp(36), -2));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(VERTICAL);
        TextView name = text(title(file, inLibrary), 18, Color.WHITE);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        TextView second = text(playable ? secondLine(file, inLibrary) : "Not a video", 13.5f, FilesPage.SECOND_LINE);
        second.setSingleLine(true);
        second.setTag(SECOND + file.getName());
        words.addView(name);
        words.addView(second);
        row.addView(words, new LayoutParams(0, -2, 1));
        // The pill sits in one fixed column on every row, focused or not (F29, RULES §7).
        TextView status = text(pill == null ? "" : pill, 13.5f, Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setTag(PILL + file.getName());
        status.setVisibility(pill == null ? INVISIBLE : VISIBLE);
        LayoutParams pillSize = new LayoutParams(dp(110), dp(24));
        pillSize.leftMargin = dp(16);
        row.addView(status, pillSize);
        // › only when OK opens a page: a folder (RULES §8).
        TextView chevron = text(folder ? "›" : "", 20, Color.WHITE);
        chevron.setGravity(Gravity.CENTER);
        row.addView(chevron, new LayoutParams(dp(24), -2));
        if (!playable)
        {
            row.setAlpha(0.55f);
        }
        row.setContentDescription(title(file, inLibrary) + (pill == null ? "" : ", " + pill));
        Runnable restyle = () -> {
            boolean focused = row.isFocused();
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            name.setTextColor(ink);
            second.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : FilesPage.SECOND_LINE);
            status.setTextColor(ink);
            status.setBackground(pill == null ? null : outline(ink));
            chevron.setTextColor(ink);
            mark.setTextColor(ink);
            SupernovaStyle.inkIcons(mark, ink);
            row.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL) : null);
        };
        row.setOnFocusChangeListener((view, focused) -> restyle.run());
        restyle.run();
        wire(row, file, inLibrary);
        return row;
    }

    private static final String SECOND = "files.second:";
    private static final String PILL = "files.pill:";

    /** OK opens or plays; long-press or Menu opens the item's menu beside it (F16). */
    private void wire(View view, MetaFile2 file, Base inLibrary)
    {
        if (!view.isFocusable())
        {
            return;
        }
        view.setOnClickListener(clicked -> {
            if (file.isDirectory())
            {
                enter(file.getUri(), file.getName());
            }
            else
            {
                focusName = file.getName();
                opener.open(file, inLibrary);
            }
        });
        view.setOnLongClickListener(pressed -> {
            openMenu(view, file, inLibrary);
            return true;
        });
        view.setOnKeyListener((pressed, key, event) -> {
            if (key == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP)
            {
                openMenu(view, file, inLibrary);
                return true;
            }
            return key == KeyEvent.KEYCODE_MENU;
        });
    }

    /**
     * X2 (diagnostics, 17 Nov 2026): Scan this folder on a local folder crashed, because Android refuses a plain
     * file address in a broadcast (FileUriExposedException), even one sent to this app only. The broadcast never
     * leaves the app, so the check is relaxed for that one call and put back straight after.
     */
    public static void scanFolder(Context context, android.net.Uri uri)
    {
        if (!"file".equals(uri.getScheme()))
        {
            com.archos.mediaprovider.NetworkScanner.scanVideos(context, uri);
            return;
        }
        android.os.StrictMode.VmPolicy policy = android.os.StrictMode.getVmPolicy();
        android.os.StrictMode.setVmPolicy(new android.os.StrictMode.VmPolicy.Builder().build());
        try
        {
            com.archos.mediaprovider.NetworkScanner.scanVideos(context, uri);
        }
        finally
        {
            android.os.StrictMode.setVmPolicy(policy);
        }
    }

    /**
     * The item's menu (F16; mock-up files-review-0157-1/01). A file: Play · Details · Mark as watched · Exclude ·
     * Fix match (Details, watched and Fix match only for a library title: never offer what does nothing, RULES §10).
     * A folder: Open · Add to library · Scan this folder · Exclude.
     */
    static List<String> menuItems(boolean folder, boolean inLibrary, boolean watched)
    {
        return menuItems(folder, inLibrary, watched, false);
    }

    /**
     * FILES.md (final, 26 Nov): a folder's menu is Open · Add to library · Scan this folder · Fix match · Exclude, with
     * Add only when the folder is not in the library yet, Scan only when it is, and Fix match only on a folder holding
     * one title (a film's or a show's folder), which re-matches everything inside in one go.
     */
    static List<String> menuItems(boolean folder, boolean inLibrary, boolean watched, boolean oneTitle)
    {
        List<String> labels = new ArrayList<>();
        if (folder)
        {
            labels.add("Open");
            labels.add(inLibrary ? "Scan this folder" : "Add to library");
            if (oneTitle)
            {
                labels.add("Fix match");
            }
            labels.add("Exclude");
            return labels;
        }
        labels.add("Play");
        if (inLibrary)
        {
            labels.add("Details");
            labels.add(watched ? "Mark as unwatched" : "Mark as watched");
        }
        labels.add("Exclude");
        if (inLibrary)
        {
            labels.add("Fix match");
        }
        return labels;
    }

    private static String menuIcon(String label)
    {
        switch (label)
        {
            case "Open":
                return "folder";
            case "Add to library":
                return "plus";
            case "Scan this folder":
                return "scan";
            case "Exclude":
                return "remove";
            case "Play":
                return "play";
            case "Details":
                return "details";
            case "Mark as watched":
            case "Mark as unwatched":
                return "check";
            case "Fix match":
                return "edit";
            default:
                return "more";
        }
    }

    private void openMenu(View anchor, MetaFile2 file, Base inLibrary)
    {
        boolean watched = inLibrary instanceof Video && ((Video) inLibrary).isWatched();
        List<Base> held = file.isDirectory() ? inside(file) : new ArrayList<>();
        List<String> labels = file.isDirectory()
            ? menuItems(true, !held.isEmpty(), false, oneTitle(held))
            : menuItems(false, inLibrary != null, watched);
        if (file.isDirectory() && oneTitle(held))
        {
            inLibrary = held.get(0);
        }
        Base target = inLibrary;
        String[] icons = new String[labels.size()];
        for (int index = 0; index < icons.length; index++)
        {
            icons[index] = menuIcon(labels.get(index));
        }
        focusName = file.getName();
        PreviewDialog.menu(getContext(), anchor, file.getName(), labels.toArray(new String[0]), icons, -1,
                           choice -> act(labels.get(choice), file, target));
    }

    private void act(String label, MetaFile2 file, Base inLibrary)
    {
        Context context = getContext();
        switch (label)
        {
            case "Open":
                enter(file.getUri(), file.getName());
                break;
            case "Play":
                opener.open(file, inLibrary);
                break;
            case "Details":
                actions.details(inLibrary);
                break;
            case "Fix match":
                actions.fixMatch(inLibrary);
                break;
            case "Mark as watched":
                com.archos.mediacenter.video.utils.DbUtils.markAsRead(context, (Video) inLibrary);
                break;
            case "Mark as unwatched":
                com.archos.mediacenter.video.utils.DbUtils.markAsNotRead(context, (Video) inLibrary);
                break;
            case "Add to library":
                if (LibraryChanges.apply(context, selection -> selection.include(file.getUri())))
                {
                    TextView added = items.findViewWithTag(PILL + file.getName());
                    if (added != null)
                    {
                        added.setText("In library");
                    }
                }
                break;
            case "Scan this folder":
                scanFolder(context, file.getUri());
                break;
            case "Exclude":
                exclude(file);
                break;
            default:
                break;
        }
    }

    /** Exclude: the library leaves this file or folder out from now on (Supernova's library exclusions). */
    private void exclude(MetaFile2 file)
    {
        if (!LibraryChanges.apply(getContext(), selection -> selection.exclude(file.getUri())))
        {
            return;
        }
        TextView status = items.findViewWithTag(PILL + file.getName());
        if (status != null)
        {
            status.setText("Excluded");
            status.setVisibility(VISIBLE);
        }
    }

    /** Folders on this device are counted where they are (any depth), so even folders not added show a count. */
    private void countFolders(List<MetaFile2> shown)
    {
        int asked = generation;
        for (MetaFile2 file : shown)
        {
            if (!file.isDirectory() || !"file".equals(file.getUri().getScheme()))
            {
                continue;
            }
            String name = file.getName();
            String filePath = file.getUri().getPath();
            int known = inside(file).size();
            LIST.submit(() -> {
                int found = countVideos(new File(filePath), 0, new int[] {0});
                if (found <= known)
                {
                    return;
                }
                main.post(() -> {
                    if (asked != generation)
                    {
                        return;
                    }
                    TextView second = items.findViewWithTag(SECOND + name);
                    if (second != null)
                    {
                        second.setText(folderLine(found));
                    }
                });
            });
        }
    }

    private static int countVideos(File folder, int depth, int[] seen)
    {
        File[] children = folder.listFiles();
        if (children == null || depth > 6)
        {
            return 0;
        }
        int videos = 0;
        for (File child : children)
        {
            if (++seen[0] > 5000)
            {
                break;
            }
            if (child.getName().startsWith("."))
            {
                continue;
            }
            if (child.isDirectory())
            {
                videos += countVideos(child, depth + 1, seen);
            }
            else if (videoName(child.getName()))
            {
                videos++;
            }
        }
        return videos;
    }

    /**
     * A 16:9 card (F8, F24; mock-up files-review-0157-1/03): a library file shows its artwork; a folder shows a
     * mosaic of the artwork inside, else the plain card with its icon; other files the plain card with the film
     * icon. The pill top right; the caption as in the list.
     */
    private View card(MetaFile2 file)
    {
        Base inLibrary = inLibrary(file);
        boolean folder = file.isDirectory();
        boolean playable = folder || video(file);
        FrameLayout card = new FrameLayout(getContext())
        {
            @Override
            protected void onMeasure(int w, int h)
            {
                int width = MeasureSpec.getSize(w);
                super.onMeasure(w, MeasureSpec.makeMeasureSpec(Math.round(width * 9f / 16f), MeasureSpec.EXACTLY));
            }
        };
        card.setFocusable(playable);
        card.setFocusableInTouchMode(playable);
        card.setTag(itemTag(file.getName()));
        card.setClipToOutline(true);
        card.setContentDescription(title(file, inLibrary));
        GradientDrawable base = new GradientDrawable();
        base.setColor(0xFF2A2E35);
        base.setCornerRadius(dp(8));
        card.setBackground(base);
        List<String> pictures = new ArrayList<>();
        if (folder)
        {
            for (Base item : inside(file))
            {
                Uri backdrop = ((Video) item).getPreviewBackdrop();
                String picture = backdrop == null ? null : backdrop.toString();
                if (picture != null && !pictures.contains(picture))
                {
                    pictures.add(picture);
                }
                if (pictures.size() == 4)
                {
                    break;
                }
            }
        }
        else if (inLibrary instanceof Video && ((Video) inLibrary).getPreviewBackdrop() != null)
        {
            pictures.add(((Video) inLibrary).getPreviewBackdrop().toString());
        }
        boolean still = pictures.isEmpty() && !folder && playable && "file".equals(file.getUri().getScheme());
        if (still)
        {
            // A video on this device that isn't in the library: a still taken from it (F24), the plain card meanwhile.
            ImageView frame = new ImageView(getContext());
            frame.setScaleType(ImageView.ScaleType.CENTER_CROP);
            card.addView(frame, new FrameLayout.LayoutParams(-1, -1));
            requestStill(file.getUri().getPath(), frame);
        }
        if (pictures.isEmpty())
        {
            TextView icon = new TextView(getContext());
            PreviewIcon.apply(icon, folder ? "folder" : playable ? "film" : "file", 34);
            icon.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams middle = new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER);
            middle.bottomMargin = dp(18);
            card.addView(icon, middle);
        }
        else
        {
            card.addView(mosaic(pictures), new FrameLayout.LayoutParams(-1, -1));
            View shade = new View(getContext());
            shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                     new int[] {0xE0000000, 0x00000000}));
            card.addView(shade, new FrameLayout.LayoutParams(-1, -1));
        }
        String pill = playable ? pill(folder ? !inside(file).isEmpty() : inLibrary != null, excluded(file)) : null;
        if (pill != null)
        {
            TextView status = text(pill, 11, Color.WHITE);
            status.setTag(PILL + file.getName());
            status.setPadding(dp(10), dp(3), dp(10), dp(3));
            GradientDrawable edge = new GradientDrawable();
            edge.setColor(0xCC16181C);
            edge.setCornerRadius(dp(12));
            edge.setStroke(Math.max(1, dp(1)), 0x88FFFFFF);
            status.setBackground(edge);
            FrameLayout.LayoutParams at = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.END);
            at.setMargins(0, dp(8), dp(8), 0);
            card.addView(status, at);
        }
        LinearLayout words = new LinearLayout(getContext());
        words.setOrientation(VERTICAL);
        TextView name = text(title(file, inLibrary), 15, Color.WHITE);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        words.addView(name);
        TextView second = text(playable ? secondLine(file, inLibrary) : "Not a video", 11,
                               SupernovaStyle.TEXT_SECONDARY);
        second.setTag(SECOND + file.getName());
        words.addView(second);
        FrameLayout.LayoutParams foot = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM | Gravity.START);
        foot.setMargins(dp(10), 0, dp(10), dp(8));
        card.addView(words, foot);
        card.setForeground(new ArtworkEdge(getContext(), 8, false));
        if (!playable)
        {
            card.setAlpha(0.55f);
        }
        card.setOnFocusChangeListener((view, focused) -> {
            // Focused artwork grows and gets the thick white ring (RULES, artwork edge).
            float scale = focused ? 1.06f : 1f;
            view.animate().scaleX(scale).scaleY(scale).setDuration(120).start();
            view.setForeground(focused ? ring() : new ArtworkEdge(getContext(), 8, false));
            view.setElevation(focused ? dp(4) : 0);
        });
        wire(card, file, inLibrary);
        return card;
    }

    private static final ExecutorService STILLS = Executors.newSingleThreadExecutor();
    private static final android.util.LruCache<String, android.graphics.Bitmap> STILL_CACHE =
        new android.util.LruCache<>(48);

    /** A frame from a tenth of the way in, 480 px wide, made off the main thread and kept for the session. */
    private void requestStill(String path, ImageView frame)
    {
        android.graphics.Bitmap known = STILL_CACHE.get(path);
        if (known != null)
        {
            frame.setImageBitmap(known);
            return;
        }
        int asked = generation;
        STILLS.submit(() -> {
            android.graphics.Bitmap made = null;
            android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
            try
            {
                retriever.setDataSource(path);
                String length = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                long at = length == null ? 60_000_000L : Long.parseLong(length) * 100L;
                android.graphics.Bitmap whole = retriever.getFrameAtTime(
                    at, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (whole != null && whole.getWidth() > 0)
                {
                    int height = Math.max(1, Math.round(480f * whole.getHeight() / whole.getWidth()));
                    made = android.graphics.Bitmap.createScaledBitmap(whole, 480, height, true);
                }
            }
            catch (RuntimeException unreadable)
            {
                made = null;
            }
            finally
            {
                try
                {
                    retriever.release();
                }
                catch (Exception ignored)
                {
                    // Nothing to release.
                }
            }
            android.graphics.Bitmap result = made;
            if (result == null)
            {
                return;
            }
            STILL_CACHE.put(path, result);
            main.post(() -> {
                if (asked == generation)
                {
                    frame.setImageBitmap(result);
                    View icon = ((ViewGroup) frame.getParent()).getChildAt(
                        ((ViewGroup) frame.getParent()).indexOfChild(frame) + 1);
                    if (icon instanceof TextView)
                    {
                        icon.setVisibility(GONE);
                    }
                }
            });
        });
    }

    /** One picture fills the card; two to four share it in a grid. */
    private View mosaic(List<String> pictures)
    {
        if (pictures.size() == 1)
        {
            return picture(pictures.get(0));
        }
        LinearLayout columns = new LinearLayout(getContext());
        int perColumn = pictures.size() == 4 ? 2 : 1;
        for (int start = 0; start < pictures.size(); start += perColumn)
        {
            LinearLayout column = new LinearLayout(getContext());
            column.setOrientation(VERTICAL);
            for (int index = start; index < Math.min(pictures.size(), start + perColumn); index++)
            {
                column.addView(picture(pictures.get(index)), new LayoutParams(-1, 0, 1));
            }
            columns.addView(column, new LayoutParams(0, -1, 1));
        }
        return columns;
    }

    private ImageView picture(String address)
    {
        ImageView art = new ImageView(getContext());
        art.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try
        {
            Picasso.get().load(address).fit().centerCrop().into(art);
        }
        catch (RuntimeException unavailable)
        {
            // No picture loader in this process: the card keeps its plain charcoal.
        }
        return art;
    }

    private GradientDrawable ring()
    {
        GradientDrawable ring = new GradientDrawable();
        ring.setColor(Color.TRANSPARENT);
        ring.setCornerRadius(dp(8));
        ring.setStroke(dp(3), Color.WHITE);
        return ring;
    }

    static boolean video(MetaFile2 file)
    {
        return videoName(file.getName());
    }

    static boolean videoName(String name)
    {
        ArchosMediaFile.MediaFileType type = ArchosMediaFile.getFileType(name);
        return type != null && ArchosMediaFile.isVideoFileType(type.fileType);
    }

    private static String key(String value)
    {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private LayoutParams gap()
    {
        LayoutParams gap = new LayoutParams(-2, -2);
        gap.leftMargin = dp(10);
        return gap;
    }

    private TextView button(String icon, String label, Runnable action)
    {
        TextView button = text(label, 14, Color.WHITE);
        button.setPadding(dp(14), dp(7), dp(14), dp(7));
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setSingleLine(true);
        PreviewIcon.apply(button, icon, 14);
        SupernovaStyle.applyButton(button, SupernovaStyle.Surface.PANEL);
        button.setOnClickListener(view -> action.run());
        return button;
    }

    private TextView text(String value, float size, int colour)
    {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(colour);
        return view;
    }

    private GradientDrawable outline(int colour)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dp(12));
        shape.setStroke(Math.max(1, dp(1)), colour);
        return shape;
    }

    private GradientDrawable box(int colour)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dp(10));
        shape.setColor(colour);
        return shape;
    }

    private int dp(float value)
    {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    /** Leaves the browser (back to the list of storages). */
    public void leave()
    {
        generation++;
        leave.run();
    }
}
