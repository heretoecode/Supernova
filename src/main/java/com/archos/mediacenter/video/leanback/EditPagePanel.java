package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;

/**
 * The approved Edit Page side panel (owner, 2 Nov 2026; docs/design/EDIT_PAGE_SIDE_PANEL_APPROVED_2026-11-02.md).
 *
 * <p>A 690px panel docked top-right over the user's real custom library page: "the live preview is the
 * page itself". Every change edits the {@link Host#draft() draft} and asks the host to re-render the page
 * behind. One list, no tabs; lists that cannot be inline (genres, original language, studio/network)
 * open as a page inside this panel. Features are exactly those of the previous editor.
 */
public final class EditPagePanel extends LinearLayout {
    public interface Host {
        CustomLibraryPage draft();
        /** True while creating a page that has never been saved. */
        boolean isNew();
        Snapshot snapshot();
        /** The draft changed: re-render the page behind and the tab name. */
        void changed();
        void save();
        /** Back with nothing left to close inside the panel. */
        void back();
        void delete();

        /** A new page whose settings have not been touched yet. */
        default boolean untouched() {
            return false;
        }
    }

    static final String[] TYPES = {"movies", "tv", "both"}, TYPE_LABELS = {"Movies", "TV Shows", "Both"};
    static final String[] WATCHED = {"all", "watched", "unwatched", "progress"},
                          WATCHED_LABELS = {"All", "Watched", "Unwatched", "In Progress"};
    static final double[] RATINGS = {0, 5, 6, 7, 8, 9};
    static final String[] VIEWS = {"grid", "list"}, VIEW_LABELS = {"Grid", "List"};
    static final String[] SORTS = {"title", "year", "added"}, SORT_LABELS = {"Title", "Year", "Date Added"};

    private final Activity owner;
    private final Host host;
    private final SidePanelKit kit;
    private final LinearLayout header, body;
    private final TextView title, matches, save;
    private final SidePanelKit.FittedScroll scroll;
    private final List<Runnable> bindings = new ArrayList<>();
    private String page; // null on the main list; "genres", "language" or "studio" on a pick list
    private String returnTo;
    private Dialog dialog;
    private View headerRule;

    EditPagePanel(Activity owner, Host host) {
        super(owner);
        this.owner = owner;
        this.host = host;
        kit = new SidePanelKit(owner);
        kit.compact(); // owner, 4 Nov 2026: every option fits on screen without scrolling
        setOrientation(VERTICAL);
        setTag("edit.panel");
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        int glow = kit.s(8);
        setBackground(kit.panelSurface(glow));
        setPadding(kit.s(30) + glow, kit.s(24) + glow, kit.s(30) + glow, kit.s(22) + glow);

        header = new LinearLayout(owner);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout heading = new LinearLayout(owner);
        heading.setOrientation(VERTICAL);
        title = kit.text("Edit Page", 32, Color.WHITE, true);
        heading.addView(title);
        matches = kit.text("", 18, SidePanelKit.MATCH, false);
        matches.setTag("edit.matches");
        matches.setPadding(0, kit.s(6), 0, 0);
        heading.addView(matches);
        header.addView(heading, new LayoutParams(0, -2, 1));
        save = kit.text(host.isNew() ? "Save Page" : "Save Changes", 18, Color.WHITE, true);
        save.setTag("edit.save");
        save.setGravity(Gravity.CENTER);
        save.setFocusable(true);
        save.setFocusableInTouchMode(true);
        SupernovaStyle.applyButton(save, SupernovaStyle.Surface.PANEL);
        save.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        save.setPadding(kit.s(22), 0, kit.s(22), 0);
        save.setMinHeight(kit.s(46));
        PreviewIcon.apply(save, "check", 18);
        save.setCompoundDrawablePadding(kit.s(10));
        save.setOnClickListener(v -> host.save());
        save.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                focusFirst();
                return true;
            }
            return key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_LEFT ||
                key == KeyEvent.KEYCODE_DPAD_RIGHT;
        });
        header.addView(save, new LayoutParams(-2, kit.s(46)));
        LayoutParams headerSize = new LayoutParams(-1, -2);
        headerSize.bottomMargin = kit.s(14);
        addView(header, headerSize);
        // The title, live match count and Save Changes are the panel's header: a line separates them from the
        // options (owner, 4 Nov 2026).
        headerRule = new View(owner);
        headerRule.setTag("edit.header.rule");
        headerRule.setBackgroundColor(SidePanelKit.HEADING_RULE);
        LayoutParams ruleSize = new LayoutParams(-1, Math.max(1, kit.s(2)));
        ruleSize.bottomMargin = kit.s(6);
        addView(headerRule, ruleSize);

        scroll = new SidePanelKit.FittedScroll(owner);
        scroll.setVerticalScrollBarEnabled(false); // focus scrolls it; a bar over the values only clutters
        body = new LinearLayout(owner);
        body.setOrientation(VERTICAL);
        scroll.addView(body);
        addView(scroll, new LayoutParams(-1, -2));
        showMain();
    }

    // ---- window ----------------------------------------------------------------------------

    /** Opens the panel in its own undimmed window, docked top-right below the top navigation. */
    public static EditPagePanel show(Activity owner, Host host) {
        EditPagePanel panel = new EditPagePanel(owner, host);
        Dialog dialog = PreviewDialog.create(owner);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(panel);
        dialog.setCancelable(false);
        dialog.setOnKeyListener((d, key, event) -> {
            if (key == KeyEvent.KEYCODE_BACK || key == KeyEvent.KEYCODE_ESCAPE || key == KeyEvent.KEYCODE_BUTTON_B) {
                if (event.getAction() == KeyEvent.ACTION_UP && !panel.closePage())
                    host.back();
                return true;
            }
            return false;
        });
        panel.dialog = dialog;
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.TOP | Gravity.END);
        DisplayMetrics m = owner.getResources().getDisplayMetrics();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.x = Math.round(m.widthPixels * 40f / 1920f);
        attrs.y = Math.round(m.heightPixels * 96f / 1080f);
        window.setAttributes(attrs);
        window.setLayout(panel.panelWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        panel.post(panel::focusInitial);
        return panel;
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public Dialog dialog() {
        return dialog;
    }

    int panelWidth() {
        return Math.round(owner.getResources().getDisplayMetrics().widthPixels * 690f / 1920f);
    }

    /** Screen height less the 96px top and 40px bottom margins, the panel padding and the header. */
    private int maxBodyHeight() {
        return owner.getResources().getDisplayMetrics().heightPixels - kit.s(96 + 40) - kit.s(24 + 22 + 16) - kit.s(70);
    }

    /** Reopening Edit Page for the same page returns to the row you left (focus audit F16). */
    static final String LAST_PAGE = "supernova_edit_page_last_page";
    static final String LAST_ROW = "supernova_edit_page_last_row";

    private void focusInitial() {
        android.content.SharedPreferences prefs =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        String lastRowTag = prefs.getString(LAST_ROW, null);
        boolean samePage = host.draft().name.equals(prefs.getString(LAST_PAGE, null));
        boolean same = !host.isNew() && samePage && lastRowTag != null;
        View target = body.findViewWithTag(same ? lastRowTag : "edit.row.name");
        if (target == null)
            target = body.findViewWithTag("edit.row.name");
        if (target != null)
            target.requestFocus();
        getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> rememberRow(after));
    }

    private void rememberRow(View focused) {
        for (View at = focused; at != null; at = at.getParent() instanceof View ? (View) at.getParent() : null) {
            Object tag = at.getTag();
            if (tag instanceof String && ((String) tag).startsWith("edit.row.")) {
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                    .putString(LAST_PAGE, host.draft().name).putString(LAST_ROW, (String) tag).apply();
                return;
            }
            if (at == this)
                return;
        }
    }

    private void focusFirst() {
        for (int i = 0; i < body.getChildCount(); i++) {
            View child = body.getChildAt(i);
            if (child.isFocusable() && child.requestFocus())
                return;
        }
    }

    private void focusTop() {
        if (page != null) {
            View back = findViewWithTag("edit.page.back");
            if (back != null)
                back.requestFocus();
        } else
            save.requestFocus();
    }

    // ---- main list -------------------------------------------------------------------------

    private void showMain() {
        page = null;
        body.removeAllViews();
        bindings.clear();
        View oldBack = findViewWithTag("edit.page.back");
        if (oldBack != null)
            removeView(oldBack);
        header.setVisibility(VISIBLE);
        headerRule.setVisibility(VISIBLE);
        scroll.setMax(maxBodyHeight());
        CustomLibraryPage d = host.draft();

        LinearLayout name = kit.row(body, "edit.row.name", "edit", "Page Name", this::focusTop);
        TextView nameValue = kit.opener(name);
        bindings.add(() -> {
            nameValue.setText(d.name);
            name.setContentDescription("Page Name, " + d.name);
        });
        name.setOnClickListener(v -> PreviewTextInput.show(owner, "Page Name", d.name, 40, value -> {
            d.name = value;
            changed();
        }));

        choices("edit.row.type", "film", "Includes", TYPE_LABELS, () -> index(TYPES, d.type), n -> d.type = TYPES[n]);

        kit.section(body, "WHAT TO SHOW", false);
        LinearLayout genres = kit.row(body, "edit.row.genres", "genre", "Genres", this::focusTop);
        TextView genreValue = kit.opener(genres);
        bindings.add(() -> {
            genreValue.setText(genreSummary(d.genres));
            genres.setContentDescription("Genres, " + genreSummary(d.genres));
        });
        genres.setOnClickListener(v -> openGenres());
        choices("edit.row.match", "filter", "Match", new String[] {"Any", "All"},
                () -> d.allGenres ? 1 : 0, n -> d.allGenres = n == 1);

        LinearLayout year = kit.row(body, "edit.row.year", "date", "Year", this::focusTop);
        TextView yearValue = kit.opener(year);
        bindings.add(() -> {
            yearValue.setText(d.yearSummary());
            year.setContentDescription("Year, " + d.yearSummary());
        });
        year.setOnClickListener(v -> openYears());
        stepper("edit.row.watched", "eye", "Watched",
                () -> index(WATCHED, d.watched), WATCHED.length, n -> d.watched = WATCHED[n], n -> WATCHED_LABELS[n]);
        stepper("edit.row.rating", "star", "Minimum Rating",
                ()
                    -> ratingIndex(d.rating),
                RATINGS.length, n -> d.rating = RATINGS[n], n -> n == 0 ? "Any" : String.valueOf((int)RATINGS[n]));
        facet("language", "language", "Original Language");
        facet("studio", "network", "Studio / Network");

        kit.section(body, "HOW TO SHOW IT", false);
        choices("edit.row.view", "grid", "View", VIEW_LABELS, () -> index(VIEWS, d.view), n -> d.view = VIEWS[n]);
        choices("edit.row.sort", "sort", "Sort By", SORT_LABELS, () -> index(SORTS, d.sort), n -> d.sort = SORTS[n]);
        orderRow();

        if (!host.isNew()) {
            // Delete Page sits at the bottom behind its own line, away from Save Changes (owner, 4 Nov 2026).
            View rule = new View(owner);
            rule.setTag("edit.delete.rule");
            rule.setBackgroundColor(SidePanelKit.HEADING_RULE);
            LayoutParams ruleSize = new LayoutParams(-1, Math.max(1, kit.s(2)));
            ruleSize.topMargin = kit.s(8);
            ruleSize.bottomMargin = kit.s(6);
            body.addView(rule, ruleSize);
            LinearLayout delete = new LinearLayout(owner);
            delete.setTag("edit.row.delete");
            delete.setGravity(Gravity.CENTER_VERTICAL);
            delete.setMinimumHeight(kit.s(kit.rowHeight()));
            delete.setPadding(kit.s(16), 0, kit.s(16), 0);
            delete.setFocusable(true);
            delete.setFocusableInTouchMode(true);
            delete.setBackground(kit.rowFocus());
            delete.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            delete.addView(kit.icon("delete", 22, SidePanelKit.DANGER));
            TextView label = kit.text("Delete Page", 19, SidePanelKit.DANGER, true);
            label.setPadding(kit.s(14), 0, kit.s(18), 0);
            delete.addView(label);
            TextView note =
                kit.text("Removes this page only. Your media is never deleted.", 15, SidePanelKit.META, false);
            note.setSingleLine(true);
            note.setEllipsize(TextUtils.TruncateAt.END);
            delete.addView(note, new LayoutParams(0, -2, 1));
            delete.setContentDescription("Delete Page. Removes this page only. Your media is never deleted.");
            delete.setOnClickListener(v -> host.delete());
            kit.attach(body, delete, null);
            body.addView(delete, new LayoutParams(-1, -2));
        } else
            SidePanelKit.dropTrailingRule(body);
        refresh();
    }

    private interface IntSource {
        int get();
    }
    private interface IntSink {
        void set(int n);
    }
    private interface Label {
        String of(int n);
    }

    private void choices(String tag, String iconKind, String title, String[] labels, IntSource current, IntSink set) {
        LinearLayout row = kit.row(body, tag, iconKind, title, this::focusTop);
        TextView[] texts = kit.choices(row, labels);
        bindings.add(() -> {
            int n = current.get();
            SidePanelKit.select(texts, n);
            row.setContentDescription(title + ", " + (n >= 0 && n < labels.length ? labels[n] : ""));
        });
        SidePanelKit.Stepper step = direction -> {
            int n = current.get(), next = Math.max(0, Math.min(labels.length - 1, (n < 0 ? 0 : n) + direction));
            if (next != n) {
                set.set(next);
                changed();
            }
        };
        kit.attach(body, row, step);
        row.setOnClickListener(v -> {
            int n = current.get();
            set.set((n + 1) % labels.length);
            changed();
        });
    }

    private void stepper(String tag, String iconKind, String title, IntSource current, int count, IntSink set,
                         Label label) {
        LinearLayout row = kit.row(body, tag, iconKind, title, this::focusTop);
        TextView value = kit.stepper(body, row, 70, direction -> {
            int n = Math.max(0, current.get()), next = Math.max(0, Math.min(count - 1, n + direction));
            if (next != n) {
                set.set(next);
                changed();
            }
        });
        bindings.add(() -> {
            int n = Math.max(0, current.get());
            value.setText(label.of(n));
            row.setContentDescription(title + ", " + label.of(n));
        });
    }

    private void orderRow() {
        CustomLibraryPage d = host.draft();
        LinearLayout row = kit.row(body, "edit.row.order", "order", "Order", this::focusTop);
        TextView[] texts = kit.choices(row, new String[] {"", ""});
        // Persisted value stays ascending/descending; the labels follow the sort (title: A–Z first).
        bindings.add(() -> {
            boolean title = d.sort.equals("title");
            texts[0].setText(title ? "A–Z" : "Newest");
            texts[1].setText(title ? "Z–A" : "Oldest");
            int selected = title ? (d.descending ? 1 : 0) : (d.descending ? 0 : 1);
            SidePanelKit.select(texts, selected);
            row.setContentDescription("Order, " + texts[selected].getText());
        });
        kit.attach(body, row, direction -> {
            boolean title = d.sort.equals("title");
            boolean descending = title ? direction > 0 : direction < 0;
            if (descending != d.descending) {
                d.descending = descending;
                changed();
            }
        });
        row.setOnClickListener(v -> {
            d.descending = !d.descending;
            changed();
        });
    }

    private void facet(String kind, String iconKind, String title) {
        CustomLibraryPage d = host.draft();
        List<String> values = facetValues(kind);
        LinearLayout row = kit.row(body, "edit.row." + kind, iconKind, title, this::focusTop);
        TextView value = kit.opener(row);
        bindings.add(() -> {
            String current = kind.equals("language") ? d.language : d.studio;
            value.setText(facetLabel(kind, current));
            if (kind.equals("language"))
                PreviewLanguageIcon.apply(value, current.isEmpty() ? null : current, kit.s(26));
            if (current.isEmpty())
                value.setCompoundDrawables(null, null, null, null);
            value.setCompoundDrawablePadding(kit.s(10));
            row.setContentDescription(title + ", " + facetLabel(kind, current));
        });
        kit.attach(body, row, direction -> {
            String current = kind.equals("language") ? d.language : d.studio;
            int n = Math.max(0, values.indexOf(current)), next =
                                                              Math.max(0, Math.min(values.size() - 1, n + direction));
            if (next != n) {
                setFacet(kind, values.get(next));
                changed();
            }
        });
        row.setOnClickListener(v -> openFacet(kind, title));
    }

    private void setFacet(String kind, String value) {
        if (kind.equals("language"))
            host.draft().language = value;
        else
            host.draft().studio = value;
    }

    private static String facetLabel(String kind, String value) {
        if (value == null || value.isEmpty())
            return "Any";
        if (!kind.equals("language"))
            return value;
        String name = Locale.forLanguageTag(value.replace('_', '-')).getDisplayName();
        return name.isEmpty() ? value : name;
    }

    // ---- values from the library -----------------------------------------------------------

    private List<Entry> everything() {
        List<Entry> all = new ArrayList<>(host.snapshot().movies);
        all.addAll(host.snapshot().shows);
        return all;
    }

    List<Integer> years() {
        TreeSet<Integer> years = new TreeSet<>(Comparator.reverseOrder());
        for (Entry entry : everything())
            if (entry.year() > 0)
                years.add(entry.year());
        if (host.draft().year > 0)
            years.add(host.draft().year);
        if (host.draft().yearTo > 0)
            years.add(host.draft().yearTo);
        List<Integer> values = new ArrayList<>();
        values.add(0);
        values.addAll(years);
        return values;
    }

    List<String> facetValues(String kind) {
        Set<String> available = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Entry entry : everything()) {
            String value = kind.equals("language") ? entry.language : entry.studio;
            if (value == null || value.isEmpty() || kind.equals("language") && "und".equals(value))
                continue;
            if (kind.equals("studio"))
                available.addAll(PreviewGenres.parse(value));
            else
                available.add(value);
        }
        String current = kind.equals("language") ? host.draft().language : host.draft().studio;
        if (!current.isEmpty())
            available.add(current);
        List<String> values = new ArrayList<>();
        values.add("");
        values.addAll(available);
        return values;
    }

    private Set<String> genres() {
        Set<String> available = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Entry entry : everything())
            available.addAll(PreviewGenres.parse(entry.genres));
        available.addAll(host.draft().genres);
        return available;
    }

    static String genreSummary(Set<String> genres) {
        if (genres.isEmpty())
            return "Any";
        List<String> list = new ArrayList<>(genres);
        if (list.size() <= 2)
            return TextUtils.join(", ", list);
        return list.get(0) + ", " + list.get(1) + " +" + (list.size() - 2);
    }

    private static int index(String[] values, String value) {
        return Arrays.asList(values).indexOf(value);
    }

    private static int ratingIndex(double rating) {
        for (int i = 0; i < RATINGS.length; i++)
            if (RATINGS[i] == rating)
                return i;
        return 0;
    }

    // ---- in-panel pick lists ---------------------------------------------------------------

    private LinearLayout openPage(String which, String heading) {
        returnTo = which.equals("genres") ? "edit.row.genres" : "edit.row." + which;
        page = which;
        body.removeAllViews();
        bindings.clear();
        header.setVisibility(GONE);
        headerRule.setVisibility(GONE);
        LinearLayout back = new LinearLayout(owner);
        back.setTag("edit.page.back");
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setFocusable(true);
        back.setFocusableInTouchMode(true);
        back.setBackground(kit.rowFocus());
        back.setPadding(kit.s(4), kit.s(4), kit.s(4), kit.s(4));
        back.addView(kit.text("‹", 30, SidePanelKit.HEADING, true));
        TextView label = kit.text(heading, 28, Color.WHITE, true);
        label.setPadding(kit.s(12), 0, 0, 0);
        back.addView(label);
        back.setContentDescription("Back, " + heading);
        back.setOnClickListener(v -> closePage());
        back.setOnKeyListener((v, key, event)
                                  -> event.getAction() == KeyEvent.ACTION_DOWN &&
                                         (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_LEFT ||
                                          key == KeyEvent.KEYCODE_DPAD_RIGHT));
        LayoutParams size = new LayoutParams(-1, -2);
        size.bottomMargin = kit.s(12);
        addView(back, 1, size);
        scroll.setMax(Math.min(maxBodyHeight() + kit.s(60), kit.s(10 * SidePanelKit.ROW_HEIGHT)));
        scroll.scrollTo(0, 0);
        return back;
    }

    private LinearLayout pickRow(String tag, String label, String language, boolean ticked) {
        LinearLayout row = new LinearLayout(owner);
        row.setTag(tag);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(kit.s(SidePanelKit.ROW_HEIGHT));
        row.setPadding(kit.s(16), 0, kit.s(16), 0);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        row.setBackground(kit.rowFocus());
        row.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (language != null) {
            ImageView flag = new ImageView(owner);
            flag.setImageDrawable(new PreviewLanguageIcon(owner, language));
            LayoutParams f = new LayoutParams(kit.s(32), kit.s(32));
            f.rightMargin = kit.s(14);
            row.addView(flag, f);
        }
        TextView text = kit.text(label, 21, Color.WHITE, true);
        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(text, new LayoutParams(0, -2, 1));
        TextView tick = kit.text("✓", 24, SidePanelKit.VALUE, false);
        tick.setTag("tick");
        tick.setVisibility(ticked ? VISIBLE : INVISIBLE);
        row.addView(tick, new LayoutParams(kit.s(28), -2));
        row.setContentDescription(label + (ticked ? ", selected" : ""));
        row.setTag(com.archos.mediacenter.video.R.id.preview_row_top, (Runnable)this::focusTop);
        kit.attach(body, row, null);
        body.addView(row, new LayoutParams(-1, -2));
        return row;
    }

    private void openGenres() {
        LinearLayout back = openPage("genres", "Genres");
        Set<String> chosen = host.draft().genres;
        View first = null;
        int i = 0;
        for (String genre : genres()) {
            LinearLayout row = pickRow("edit.genre." + i++, genre, null, chosen.contains(genre));
            row.setOnClickListener(v -> {
                if (!chosen.remove(genre))
                    chosen.add(genre);
                row.findViewWithTag("tick").setVisibility(chosen.contains(genre) ? VISIBLE : INVISIBLE);
                row.setContentDescription(genre + (chosen.contains(genre) ? ", selected" : ""));
                host.changed();
            });
            if (first == null)
                first = row;
        }
        if (first == null)
            body.addView(kit.text("No genres in your library yet", 19, SidePanelKit.META, false));
        SidePanelKit.dropTrailingRule(body);
        if (first != null)
            first.requestFocus();
        else
            back.requestFocus();
    }

    private void openFacet(String kind, String title) {
        LinearLayout back = openPage(kind, title);
        List<String> values = facetValues(kind);
        String current = kind.equals("language") ? host.draft().language : host.draft().studio;
        View initial = null;
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            LinearLayout row =
                pickRow("edit." + kind + "." + i, facetLabel(kind, value),
                        kind.equals("language") && !value.isEmpty() ? value : null, value.equals(current));
            row.setOnClickListener(v -> {
                setFacet(kind, value);
                closePage();
                changed();
            });
            if (initial == null || value.equals(current))
                initial = row;
        }
        SidePanelKit.dropTrailingRule(body);
        if (initial != null)
            initial.requestFocus();
        else
            back.requestFocus();
    }

    /**
     * Year range (owner, 4 Nov 2026; mock-up edit-page/03): From and To step through the years in the library
     * (Left/Right), or pick a quick choice. Back returns to the main list.
     */
    private void openYears() {
        LinearLayout back = openPage("year", "Year");
        CustomLibraryPage d = host.draft();
        List<Integer> descending = years();
        List<Integer> years = new ArrayList<>();
        years.add(0);
        for (int i = descending.size() - 1; i > 0; i--)
            years.add(descending.get(i));
        stepper("edit.year.from", "date", "From", () -> Math.max(0, years.indexOf(d.year)), years.size(), n -> {
            d.year = years.get(n);
            if (d.year > 0 && d.yearTo > 0 && d.yearTo < d.year)
                d.yearTo = d.year;
        }, n -> n == 0 ? "Any" : String.valueOf(years.get(n)));
        stepper("edit.year.to", "date", "To", () -> Math.max(0, years.indexOf(d.yearTo)), years.size(), n -> {
            d.yearTo = years.get(n);
            if (d.year > 0 && d.yearTo > 0 && d.yearTo < d.year)
                d.year = d.yearTo;
        }, n -> n == 0 ? "Any" : String.valueOf(years.get(n)));
        kit.section(body, "Quick choices", false);
        int now = Calendar.getInstance().get(Calendar.YEAR);
        int decade = now / 10 * 10;
        Object[][] quick = {{"Any year", 0, 0},
                            {"This year", now, now},
                            {"Last 5 years", now - 4, now},
                            {decade + "s", decade, decade + 9},
                            {(decade - 10) + "s", decade - 10, decade - 1},
                            {(decade - 20) + "s", decade - 20, decade - 11},
                            {(decade - 30) + "s", decade - 30, decade - 21},
                            {"Before " + (decade - 30), 0, decade - 31}};
        for (int i = 0; i < quick.length; i++) {
            int from = (Integer)quick[i][1], to = (Integer)quick[i][2];
            LinearLayout row = pickRow("edit.year.quick." + i, (String)quick[i][0], null,
                                       d.year == from && d.yearTo == to);
            row.setOnClickListener(v -> {
                d.year = from;
                d.yearTo = to;
                closePage();
                changed();
            });
        }
        refresh();
        View from = body.findViewWithTag("edit.year.from");
        if (from != null)
            from.requestFocus();
        else
            back.requestFocus();
    }

    /** Closes an open pick list and returns to its row. False when the main list is showing. */
    public boolean closePage() {
        if (page == null)
            return false;
        String row = returnTo;
        showMain();
        View target = body.findViewWithTag(row);
        if (target != null)
            target.requestFocus();
        return true;
    }

    // ---- state -----------------------------------------------------------------------------

    private void changed() {
        host.changed();
        refresh();
    }

    /** Library snapshot or draft changed elsewhere: rebind values (and rebuild lists that depend on the library). */
    public void refresh() {
        if (page != null)
            return;
        title.setText("Edit Page");
        save.setText(host.isNew() ? "Save Page" : "Save Changes");
        List<Entry> shown = host.draft().entries(host.snapshot());
        if (host.untouched()) {
            matches.setText("Choose what to show");
            matches.setTextColor(SidePanelKit.META);
        } else if (shown.isEmpty()) {
            matches.setText("No titles match these settings");
            matches.setTextColor(SidePanelKit.WARNING);
        } else {
            LibraryStatistics stats =
                LibraryStatistics.calculate(host.snapshot(), shown, System.currentTimeMillis(), TimeZone.getDefault());
            long bytes = stats.localBytes + stats.networkBytes;
            String size = bytes > 0 ? " · " + (stats.unknownLocal + stats.unknownNetwork > 0 ? "≥ " : "") +
                                          Formatter.formatShortFileSize(owner, bytes)
                                    : "";
            matches.setText(shown.size() + (shown.size() == 1 ? " title matches" : " titles match") + size);
            matches.setTextColor(SidePanelKit.MATCH);
        }
        for (Runnable binding : bindings)
            binding.run();
    }

    /** A new library snapshot arrived: year and facet lists may have changed. */
    public void snapshotChanged() {
        if (page != null)
            return;
        View focused = body.findFocus();
        Object tag = focused == null ? null : focused.getTag();
        showMain();
        View target = tag == null ? null : body.findViewWithTag(tag);
        if (target != null)
            target.requestFocus();
    }

    boolean pageOpen() {
        return page != null;
    }
}
