package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.filecoreextension.upnp2.RawListerFactoryWithUpnp;
import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.ArtworkEdge;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediaprovider.ArchosMediaFile;
import com.squareup.picasso.Picasso;
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
 * Browsing a storage inside Files (approved folder grid, mock-up network-files-tabs/32): the path across the top,
 * then folders and videos as cards, four to a row, each with In library / Not added. OK opens a folder or the video;
 * Back goes up one folder, and from the top back to the list of storages. Replaces the old three-panel browser
 * (0.157 Shield review #25).
 */
public final class FolderBrowser extends LinearLayout
{
    /** What a chosen video does: open its Details when it is in the library, else play it. */
    public interface Opener
    {
        void open(MetaFile2 file, Base inLibrary);
    }

    private static final ExecutorService LIST = Executors.newSingleThreadExecutor();
    private static final int COLUMNS = 4;

    private final Deque<Uri> path = new ArrayDeque<>();
    private final Deque<String> names = new ArrayDeque<>();
    private final Map<String, Base> library = new HashMap<>();
    private final TextView crumb;
    private final TextView sortButton;
    private final TextView videosButton;
    private final LinearLayout grid;
    private final ScrollView scroll;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Opener opener;
    private final Runnable leave;
    private List<MetaFile2> listed = new ArrayList<>();
    private boolean newestFirst;
    private boolean videosOnly;
    private int generation;

    public FolderBrowser(Context context, Opener opener, Runnable leave)
    {
        super(context);
        this.opener = opener;
        this.leave = leave;
        setOrientation(VERTICAL);
        LinearLayout header = new LinearLayout(context);
        header.setGravity(Gravity.CENTER_VERTICAL);
        crumb = text("", 18, Color.WHITE);
        crumb.setSingleLine(true);
        crumb.setEllipsize(TextUtils.TruncateAt.START);
        header.addView(crumb, new LayoutParams(0, -2, 1));
        sortButton = button("sort", "Sort: Name", () -> {
            newestFirst = !newestFirst;
            render();
        });
        header.addView(sortButton);
        videosButton = button("filter", "Everything", () -> {
            videosOnly = !videosOnly;
            render();
        });
        LayoutParams gap = new LayoutParams(-2, -2);
        gap.leftMargin = dp(10);
        header.addView(videosButton, gap);
        LayoutParams headerSize = new LayoutParams(-1, -2);
        headerSize.topMargin = dp(20);
        headerSize.bottomMargin = dp(16);
        addView(header, headerSize);
        scroll = new ScrollView(context);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        grid = new LinearLayout(context);
        grid.setOrientation(VERTICAL);
        grid.setClipChildren(false);
        grid.setClipToPadding(false);
        grid.setPadding(0, dp(6), 0, dp(24));
        scroll.addView(grid);
        addView(scroll, new LayoutParams(-1, 0, 1));
    }

    /** The library's videos, so each card can say In library / Not added. */
    public void setLibrary(List<Base> media)
    {
        library.clear();
        for (Base item : media)
            if (item instanceof Video && ((Video) item).getFilePath() != null)
                library.put(key(((Video) item).getFilePath()), item);
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
            return false;
        path.pop();
        names.pop();
        load();
        return true;
    }

    private void enter(Uri folder, String name)
    {
        path.push(folder);
        names.push(name);
        load();
    }

    private void load()
    {
        int asked = ++generation;
        Uri folder = path.peek();
        crumb.setText(crumbText());
        grid.removeAllViews();
        TextView waiting = text("Looking in this folder…", 15, SupernovaStyle.TEXT_SECONDARY);
        grid.addView(waiting);
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
                    return;
                if (result == null)
                {
                    grid.removeAllViews();
                    grid.addView(text("This folder can't be opened right now", 15, SupernovaStyle.TEXT_SECONDARY));
                    return;
                }
                listed = result;
                render();
            });
        });
    }

    private String crumbText()
    {
        List<String> parts = new ArrayList<>(names);
        java.util.Collections.reverse(parts);
        return TextUtils.join("  ›  ", parts);
    }

    private void render()
    {
        sortButton.setText(newestFirst ? "Sort: Newest" : "Sort: Name");
        videosButton.setText(videosOnly ? "Videos only" : "Everything");
        List<MetaFile2> shown = new ArrayList<>();
        for (MetaFile2 file : listed)
        {
            if (file.getName() == null || file.getName().startsWith("."))
                continue;
            if (file.isDirectory() ? !videosOnly : video(file))
                shown.add(file);
        }
        shown.sort((a, b) -> {
            if (a.isDirectory() != b.isDirectory())
                return a.isDirectory() ? -1 : 1;
            if (newestFirst)
                return Long.compare(b.lastModified(), a.lastModified());
            return a.getName().compareToIgnoreCase(b.getName());
        });
        grid.removeAllViews();
        if (shown.isEmpty())
        {
            grid.addView(text(videosOnly ? "No videos in this folder" : "This folder is empty", 15,
                              SupernovaStyle.TEXT_SECONDARY));
            return;
        }
        LinearLayout row = null;
        for (int i = 0; i < shown.size(); i++)
        {
            if (i % COLUMNS == 0)
            {
                row = new LinearLayout(getContext());
                row.setClipChildren(false);
                LayoutParams rowSize = new LayoutParams(-1, -2);
                rowSize.bottomMargin = dp(22);
                grid.addView(row, rowSize);
            }
            LayoutParams cardSize = new LayoutParams(0, -2, 1);
            if (i % COLUMNS > 0)
                cardSize.leftMargin = dp(22);
            row.addView(card(shown.get(i)), cardSize);
        }
        for (int i = shown.size() % COLUMNS; i > 0 && i < COLUMNS; i++)
        {
            LayoutParams filler = new LayoutParams(0, 1, 1);
            filler.leftMargin = dp(22);
            row.addView(new View(getContext()), filler);
        }
        View first = grid.getChildAt(0) instanceof LinearLayout ? ((LinearLayout) grid.getChildAt(0)).getChildAt(0)
                                                                 : null;
        if (first != null && hasFocus())
            first.requestFocus();
    }

    /** A 16:9 card: folder mark or artwork, a pill top right, the name and a second line at the foot. */
    private View card(MetaFile2 file)
    {
        Base inLibrary = file.isDirectory() ? null : library.get(key(file.getUri().getPath()));
        FrameLayout card = new FrameLayout(getContext()) {
            @Override
            protected void onMeasure(int w, int h)
            {
                int width = MeasureSpec.getSize(w);
                super.onMeasure(w, MeasureSpec.makeMeasureSpec(Math.round(width * 9f / 16f), MeasureSpec.EXACTLY));
            }
        };
        card.setFocusable(true);
        card.setFocusableInTouchMode(true);
        card.setTag("files.folder.card");
        card.setClipToOutline(true);
        card.setContentDescription(file.getName());
        GradientDrawable base = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                     new int[] {0xFF2A2E35, 0xFF15171B});
        base.setCornerRadius(dp(8));
        card.setBackground(base);
        ImageView art = new ImageView(getContext());
        art.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.addView(art, new FrameLayout.LayoutParams(-1, -1));
        if (inLibrary instanceof Video && ((Video) inLibrary).getPreviewBackdrop() != null)
            Picasso.get().load(((Video) inLibrary).getPreviewBackdrop()).fit().centerCrop().into(art);
        View shade = new View(getContext());
        shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                 new int[] {0xE0000000, 0x00000000}));
        card.addView(shade, new FrameLayout.LayoutParams(-1, -1));
        if (file.isDirectory())
        {
            TextView folder = new TextView(getContext());
            PreviewIcon.apply(folder, "folder", 16);
            FrameLayout.LayoutParams at = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.START);
            at.setMargins(dp(10), dp(10), 0, 0);
            card.addView(folder, at);
        }
        else
        {
            TextView pill = text(inLibrary != null ? "In library" : "Not added", 11, Color.WHITE);
            pill.setPadding(dp(10), dp(3), dp(10), dp(3));
            GradientDrawable edge = new GradientDrawable();
            edge.setColor(0xCC16181C);
            edge.setCornerRadius(dp(12));
            edge.setStroke(Math.max(1, dp(1)), 0x88FFFFFF);
            pill.setBackground(edge);
            FrameLayout.LayoutParams at = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.END);
            at.setMargins(0, dp(8), dp(8), 0);
            card.addView(pill, at);
        }
        LinearLayout words = new LinearLayout(getContext());
        words.setOrientation(VERTICAL);
        TextView title = text(inLibrary != null ? inLibrary.getName() : file.getName(), 15, Color.WHITE);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        words.addView(title);
        TextView second = text(secondLine(file), 11, SupernovaStyle.TEXT_SECONDARY);
        words.addView(second);
        FrameLayout.LayoutParams foot = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM | Gravity.START);
        foot.setMargins(dp(10), 0, dp(10), dp(8));
        card.addView(words, foot);
        card.setForeground(new ArtworkEdge(getContext(), 8, false));
        card.setOnFocusChangeListener((view, focused) -> {
            // Focused artwork grows and gets the thick white ring (RULES, artwork edge).
            float scale = focused ? 1.06f : 1f;
            view.animate().scaleX(scale).scaleY(scale).setDuration(120).start();
            view.setForeground(focused ? ring() : new ArtworkEdge(getContext(), 8, false));
            view.setElevation(focused ? dp(4) : 0);
        });
        card.setOnClickListener(view -> {
            if (file.isDirectory())
                enter(file.getUri(), file.getName());
            else
                opener.open(file, inLibrary);
        });
        return card;
    }

    private String secondLine(MetaFile2 file)
    {
        if (file.isDirectory())
            return "Folder";
        long bytes = file.length();
        return bytes > 0 ? android.text.format.Formatter.formatShortFileSize(getContext(), bytes) : "Video";
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
        ArchosMediaFile.MediaFileType type = ArchosMediaFile.getFileType(file.getName());
        return type != null && ArchosMediaFile.isVideoFileType(type.fileType);
    }

    private static String key(String path)
    {
        return path == null ? "" : path.toLowerCase(Locale.ROOT);
    }

    private TextView button(String icon, String label, Runnable action)
    {
        TextView button = text(label, 14, Color.WHITE);
        button.setPadding(dp(14), dp(7), dp(14), dp(7));
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
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
