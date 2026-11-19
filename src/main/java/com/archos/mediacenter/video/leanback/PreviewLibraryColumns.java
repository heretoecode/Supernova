package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.*;
import android.widget.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.*;

/** Shared library table. All values come from the background-built database snapshot. */
public final class PreviewLibraryColumns {
    public enum Column {
        TITLE("Title", 3.5f),
        YEAR("Release date", 1.2f),
        SEASONS("Seasons", .85f),
        EPISODES("Episodes", .9f),
        DURATION("Running time", 1.1f),
        RESOLUTION("Resolution", 1.05f),
        HDR("HDR", .65f),
        AUDIO("Audio", 1.1f),
        SIZE("File size", 1.15f),
        AVERAGE("Size per episode", 1.3f),
        ADDED("Date added", 1.25f),
        MODIFIED("Date modified", 1.25f),
        CODEC("Codec", 1.05f),
        BITRATE("Bitrate", 1f),
        CONTAINER("Container", .9f),
        PATH("Path", 2f);
        final String label;
        final float width;
        Column(String label, float width) {
            this.label = label;
            this.width = width;
        }
    }
    private final Context context;
    private final SharedPreferences prefs;
    private final String prefix;
    private final boolean tv;
    /** Your own page (mixed films and shows): its own default columns (sheet). */
    private final boolean own;
    private final List<Column> order = new ArrayList<>();
    private final Set<Column> shown = EnumSet.noneOf(Column.class);
    public Column sortColumn;
    public boolean ascending = true;

    public PreviewLibraryColumns(Context c, boolean tv) {
        this(c, tv, tv ? "tv_" : "movies_");
    }

    public PreviewLibraryColumns(Context c, boolean tv, String namespace) {
        context = c;
        this.tv = tv;
        own = namespace.startsWith("custom");
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefix = "preview_columns_" + namespace;
        reset(false);
        if (prefs.getBoolean("remember_library_views", true)) {
            List<Column> saved = parse(prefs.getString(prefix + "order", ""));
            if (!saved.isEmpty()) {
                order.clear();
                order.addAll(saved);
                for (Column col : available())
                    if (!order.contains(col))
                        order.add(col);
            }
            String visible = prefs.getString(prefix + "shown", null);
            if (visible != null) {
                shown.clear();
                shown.addAll(parse(visible));
                shown.add(Column.TITLE);
            }
            try {
                sortColumn = Column.valueOf(prefs.getString(prefix + "sort", ""));
            } catch (IllegalArgumentException ignored) {
            }
            ascending = prefs.getBoolean(prefix + "ascending", true);
        }
    }

    public List<Column> available() {
        List<Column> values = new ArrayList<>(Arrays.asList(Column.values()));
        if (!tv)
            values.removeAll(Arrays.asList(Column.SEASONS, Column.EPISODES, Column.AVERAGE));
        return values;
    }

    private List<Column> parse(String value) {
        List<Column> result = new ArrayList<>();
        for (String key : value.split(","))
            try {
                Column c = Column.valueOf(key);
                if (available().contains(c) && !result.contains(c))
                    result.add(c);
            } catch (IllegalArgumentException ignored) {
            }
        return result;
    }

    private void reset(boolean save) {
        order.clear();
        order.addAll(available());
        shown.clear();
        // Defaults (page sheet LIBRARY.md): Movies, TV Shows and your own page each have their own.
        if (own)
            shown.addAll(Arrays.asList(Column.TITLE, Column.YEAR, Column.EPISODES, Column.DURATION,
                Column.RESOLUTION, Column.SIZE, Column.AVERAGE, Column.ADDED));
        else if (tv)
            shown.addAll(Arrays.asList(Column.TITLE, Column.SEASONS, Column.EPISODES, Column.DURATION,
                Column.RESOLUTION, Column.SIZE, Column.AVERAGE, Column.ADDED));
        else
            shown.addAll(Arrays.asList(Column.TITLE, Column.YEAR, Column.DURATION, Column.RESOLUTION, Column.AUDIO,
                Column.SIZE, Column.ADDED));
        List<Column> defaults = new ArrayList<>();
        for (Column c : order)
            if (shown.contains(c))
                defaults.add(c);
        order.removeAll(defaults);
        order.addAll(0, defaults);
        if (save)
            save();
    }

    private void save() {
        if (!prefs.getBoolean("remember_library_views", true))
            return;
        prefs.edit()
            .putString(prefix + "order", TextUtils.join(",", order))
            .putString(prefix + "shown", TextUtils.join(",", shown))
            .putString(prefix + "sort", sortColumn == null ? "" : sortColumn.name())
            .putBoolean(prefix + "ascending", ascending)
            .apply();
    }

    public List<Column> visible() {
        List<Column> result = new ArrayList<>();
        for (Column c : order)
            if (shown.contains(c))
                result.add(c);
        return result;
    }

    public void setAscending(boolean value) {
        ascending = value;
        save();
    }

    public void setSort(Column column, boolean direction) {
        sortColumn = column;
        ascending = direction;
        save();
    }

    public void clearSort() {
        sortColumn = null;
        save();
    }

    public List<Entry> sort(List<Entry> source) {
        if (sortColumn == null)
            return source;
        List<Entry> copy = new ArrayList<>(source);
        Column column = sortColumn;
        copy.sort((a, b) -> {
            boolean ak = known(a, column), bk = known(b, column);
            if (ak != bk)
                return ak ? -1 : 1;
            int compare = isNumeric(column)
                ? Long.compare(number(a, column), number(b, column))
                : (column == Column.TITLE ? PreviewPages.titleForSort(context, a) : value(a, column))
                      .compareToIgnoreCase(
                          column == Column.TITLE ? PreviewPages.titleForSort(context, b) : value(b, column));
            if (!ascending)
                compare = -compare;
            return compare != 0 ? compare
                                : PreviewPages.displayName(a).compareToIgnoreCase(PreviewPages.displayName(b));
        });
        return copy;
    }

    private static boolean isNumeric(Column c) {
        return Arrays
            .asList(Column.YEAR, Column.SEASONS, Column.EPISODES, Column.DURATION, Column.SIZE, Column.AVERAGE,
                Column.ADDED, Column.MODIFIED, Column.BITRATE, Column.RESOLUTION)
            .contains(c);
    }

    private static long number(Entry e, Column c) {
        switch (c) {
            case RESOLUTION:
                return resolutionRank(e.resolution);
            case YEAR:
                return e.year();
            case SEASONS:
                return e.seasons;
            case EPISODES:
                return e.episodes;
            case DURATION:
                return e.runtime;
            case SIZE:
                return e.bytes;
            case AVERAGE:
                return e.episodes > 0 ? e.bytes / e.episodes : 0;
            case ADDED:
                return e.added;
            case MODIFIED:
                return e.modified;
            case BITRATE:
                return e.bitrate;
            default:
                return 0;
        }
    }

    private static long resolutionRank(String value) {
        if (value == null)
            return 0;
        if (value.equals("SD"))
            return 480;
        if (value.equals("8K"))
            return 4320;
        if (value.equals("4K"))
            return 2160;
        try {
            return Long.parseLong(value.replace("p", "").replace("i", ""));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private boolean known(Entry e, Column c) {
        if (c == Column.AVERAGE && e.knownSizes != e.files)
            return false;
        return isNumeric(c) ? number(e, c) > 0 : !value(e, c).isEmpty();
    }

    public String value(Entry e, Column c) {
        switch (c) {
            case TITLE:
                return PreviewPages.displayName(e);
            case YEAR:
                return LibraryPageModel.releaseDate(e);
            case SEASONS:
                return e.seasons > 0 ? String.valueOf(e.seasons) : "";
            case EPISODES:
                return e.episodes > 0 ? String.valueOf(e.episodes) : "";
            case DURATION:
                return duration(e.runtime);
            case RESOLUTION:
                return e.resolution;
            case HDR:
                return e.hdr;
            case AUDIO:
                return e.audio;
            case CODEC:
                return e.codec;
            case SIZE:
                return e.knownSizes == 0 ? ""
                                         : (e.knownSizes < e.files ? "≥ " : "")
                        + Formatter.formatShortFileSize(context, e.bytes);
            case AVERAGE:
                return e.episodes > 0 && e.knownSizes == e.files
                    ? Formatter.formatShortFileSize(context, e.bytes / e.episodes)
                    : "";
            case ADDED:
                return date(e.added);
            case MODIFIED:
                return date(e.modified);
            case PATH:
                return e.path;
            case CONTAINER:
                return e.container;
            case BITRATE:
                return e.bitrate > 0 ? String.format(Locale.UK, "%.1f Mb/s", e.bitrate / 1000000d) : "";
            default:
                return "";
        }
    }

    private static String duration(long ms) {
        return LibraryPageModel.duration(ms);
    }

    private static String date(long date) {
        return LibraryPageModel.date(date);
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(context);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(0xffd5e4ee);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setPadding(dp(6), 0, dp(6), 0);
        t.setSingleLine(true);
        t.setEllipsize(TextUtils.TruncateAt.END);
        t.setIncludeFontPadding(false);
        return t;
    }

    /**
     * The column headings (mock-up `library-pages/26`): plain soft words, the sorted column bold with ⌄ / ⌃. Not a stop
     * for the remote: Down from the toolbar goes straight to the first title (sheet).
     */
    public View header(Runnable changed) {
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(5), 0, dp(3));
        row.setTag("library.columns.header");
        for (Column c : visible()) {
            boolean sorted = sortColumn == c;
            TextView label = text(c.label + (sorted ? (ascending ? "  ⌃" : "  ⌄") : ""), 11);
            label.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            if (sorted)
                label.setTypeface(android.graphics.Typeface.create("sans-serif-medium",
                    android.graphics.Typeface.NORMAL));
            label.setFocusable(false);
            label.setTag("column:" + c);
            if (c == Column.TITLE)
                label.setPadding(dp(5) + dp(58) + dp(6), 0, dp(6), 0);
            row.addView(label, new LinearLayout.LayoutParams(0, dp(26), c.width));
            Diagnostics.semantic(label, "library.header." + c.name().toLowerCase(Locale.ROOT));
        }
        return row;
    }

    public LinearLayout newRow() {
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        row.setForeground(PreviewDialog.focus(context));
        row.setBackground(separator(false));
        return row;
    }

    /** Faint lines between rows only (RULES §5); about 12% white on the speckled background (0.158 look sweep). */
    private Drawable separator(boolean vertical) {
        LayerDrawable d = new LayerDrawable(
            new Drawable[] {
                new ColorDrawable(Color.TRANSPARENT),
                new ColorDrawable(vertical ? Color.TRANSPARENT : 0x1FFFFFFF)
            });
        if (vertical) {
            d.setLayerWidth(1, dp(1));
            d.setLayerGravity(1, Gravity.END);
        } else {
            d.setLayerHeight(1, dp(1));
            d.setLayerGravity(1, Gravity.BOTTOM);
        }
        return d;
    }

    public void bind(LinearLayout row, Entry entry) {
        clear(row);
        row.removeAllViews();
        StringBuilder description = new StringBuilder();
        for (Column c : visible()) {
            if (c == Column.TITLE) {
                LinearLayout title = new LinearLayout(context);
                title.setGravity(Gravity.CENTER_VERTICAL);
                title.setPadding(dp(5), dp(3), 0, dp(3));
                ImageView image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                title.addView(image, new LinearLayout.LayoutParams(dp(58), dp(34)));
                Uri uri = entry.backdrop != null ? entry.backdrop : entry.media.getPosterUri();
                if (uri != null) {
                    long media = entry.media instanceof Video ? ((Video) entry.media).getId()
                        : entry.media instanceof Tvshow ? ((Tvshow) entry.media).getTvshowId()
                                                              : 0;
                    ArtworkRequest.load(image, uri, media, "library.list",
                        entry.backdrop != null ? "landscape" : "poster",
                        Picasso.get().load(uri).resize(dp(58), dp(34)).centerInside().noFade());
                }
                TextView name = text(value(entry, c), 12);
                name.setTextColor(Color.WHITE);
                name.setTypeface(android.graphics.Typeface.create("sans-serif-medium",
                    android.graphics.Typeface.NORMAL));
                title.addView(name, new LinearLayout.LayoutParams(0, -1, 1));
                row.addView(title, new LinearLayout.LayoutParams(0, dp(42), c.width));
            } else {
                String shown = value(entry, c);
                TextView cell = text(shown.isEmpty() ? "—" : shown, 11);
                cell.setTextColor(Color.WHITE);
                if (sortColumn == c)
                    cell.setTypeface(android.graphics.Typeface.create("sans-serif-medium",
                        android.graphics.Typeface.NORMAL));
                row.addView(cell, new LinearLayout.LayoutParams(0, dp(42), c.width));
            }
            String value = value(entry, c);
            if (!value.isEmpty())
                description.append(c.label).append(": ").append(value).append(". ");
        }
        row.setContentDescription(description.toString());
    }

    public void clear(View view) {
        if (view instanceof ImageView) {
            ArtworkRequest.cancel((ImageView) view);
            ((ImageView) view).setImageDrawable(null);
        }
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
                clear(((ViewGroup) view).getChildAt(i));
    }

    /** Icons for the Columns menu (one each, no two alike: RULES §10). */
    static String icon(Column c) {
        switch (c) {
            case YEAR:
                return "year";
            case SEASONS:
                return "tv";
            case EPISODES:
                return "list";
            case DURATION:
                return "runtime";
            case RESOLUTION:
                return "resolution";
            case HDR:
                return "hdr";
            case AUDIO:
                return "audio";
            case SIZE:
                return "total size";
            case AVERAGE:
                return "size";
            case ADDED:
                return "added";
            case MODIFIED:
                return "edit";
            case CODEC:
                return "codec";
            case BITRATE:
                return "speed";
            case CONTAINER:
                return "file";
            case PATH:
                return "folder";
            default:
                return "title";
        }
    }

    /** Hidden columns, A–Z. */
    private List<Column> hidden() {
        List<Column> result = new ArrayList<>();
        for (Column c : order)
            if (!shown.contains(c) && c != Column.TITLE)
                result.add(c);
        result.sort(Comparator.comparing(c -> c.label));
        return result;
    }

    private List<Column> shownMovable() {
        List<Column> result = visible();
        result.remove(Column.TITLE);
        return result;
    }

    /** The column picked up by OK in the Columns menu, moving with Up / Down (null when none). */
    private Column moving;

    /**
     * The Columns menu under its button (mock-ups `library-pages/28`, `33`): Shown (ticked, in column order), Hidden
     * (A–Z), Reset to default at the foot apart by a line. OK on a hidden column shows it; OK on a shown column picks it
     * up (it lifts, ⌃⌄) and Up / Down moves it, live; moved past the last shown column it goes back to Hidden; OK or
     * Back puts it down. No Done: Back closes. Title always shows and keeps the most room.
     */
    public void choose(View anchor, Runnable changed) {
        moving = null;
        choose(anchor, changed, null);
    }

    public void choose(Runnable changed) {
        choose(null, changed);
    }

    private void choose(View anchor, Runnable changed, Column focus) {
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<Column> rows = new ArrayList<>();
        Set<Integer> ticked = new HashSet<>();
        labels.add("— Shown");
        icons.add(null);
        rows.add(null);
        for (Column c : shownMovable()) {
            ticked.add(labels.size());
            labels.add(c == moving ? c.label + "   ⌃⌄" : c.label);
            icons.add(icon(c));
            rows.add(c);
        }
        labels.add("— Hidden");
        icons.add(null);
        rows.add(null);
        for (Column c : hidden()) {
            labels.add(c.label);
            icons.add(icon(c));
            rows.add(c);
        }
        labels.add("— ");
        icons.add(null);
        rows.add(null);
        labels.add("Reset to default");
        icons.add("history");
        rows.add(null);
        int initial = focus == null ? 1 : rows.indexOf(focus);
        final Dialog[] menu = {null};
        menu[0] = PreviewDialog.menuBelow(context, anchor, "Columns", labels.toArray(new String[0]),
            icons.toArray(new String[0]), ticked, initial, false, n -> {
                Column c = rows.get(n);
                menu[0].dismiss();
                if (c == null) {
                    moving = null;
                    reset(true);
                    changed.run();
                    choose(anchor, changed, null);
                    return;
                }
                if (moving != null) {
                    moving = null;
                } else if (shown.contains(c)) {
                    moving = c;
                } else {
                    shown.add(c);
                    order.remove(c);
                    int last = 0;
                    for (int i = 0; i < order.size(); i++)
                        if (shown.contains(order.get(i)))
                            last = i + 1;
                    order.add(last, c);
                    save();
                    changed.run();
                }
                choose(anchor, changed, c);
            });
        menu[0].setOnKeyListener((d, key, event) -> {
            if (moving == null || event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (key == KeyEvent.KEYCODE_BACK) {
                Column put = moving;
                moving = null;
                menu[0].dismiss();
                choose(anchor, changed, put);
                return true;
            }
            if (key != KeyEvent.KEYCODE_DPAD_UP && key != KeyEvent.KEYCODE_DPAD_DOWN)
                return key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT;
            move(moving, key == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
            changed.run();
            menu[0].dismiss();
            choose(anchor, changed, moving);
            return true;
        });
    }

    /** Moves a shown column one place; past the last shown column it is hidden again (the page follows live). */
    private void move(Column column, int step) {
        List<Column> movable = shownMovable();
        int at = movable.indexOf(column);
        int to = at + step;
        if (to < 0)
            return;
        if (to >= movable.size()) {
            shown.remove(column);
            moving = null;
            save();
            return;
        }
        Column other = movable.get(to);
        Collections.swap(order, order.indexOf(column), order.indexOf(other));
        save();
    }

    private int dp(int value) {
        return PreviewDialog.dp(context, value);
    }
}
