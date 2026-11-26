package com.archos.mediacenter.video.leanback.search;

import android.app.Activity;
import com.archos.mediacenter.video.leanback.TvLayout;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.*;
import android.os.SystemClock;
import android.text.*;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.loader.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewVariants;
import com.archos.mediaprovider.video.ScraperStore;
import com.archos.mediaprovider.video.VideoStore;
import com.squareup.picasso.Picasso;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;

/** Local Nova search, with recycled compact rows and no remote discovery result universe. */
public final class PreviewSearch extends LinearLayout {
    private final Activity activity;
    private final EditText query;
    private PreviewKeyboard keyboard;
    private final TextView status;
    private final PreviewFocusRecycler results;
    private final LinearLayoutManager layout;
    private final ResultAdapter adapter = new ResultAdapter();
    private final List<Result> items = new ArrayList<>();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private CancellationSignal cancellation;
    private int generation;
    private long selectedId = -1;
    private boolean restoring;
    private Parcelable scrollState;
    private Runnable pending;
    private String detailsFocusToken = "";
    private WeakReference<View> detailsOpener = new WeakReference<>(null);
    private boolean detailsWindowAway;
    private final int mode;

    private static class Result {
        Video video;
        Tvshow show;
        String genres;
        int rank;
        long showId;
        boolean people;
        /** For a person match, who and why: "Taylor Sheridan · director" (SEARCH.md). */
        String reason = "";

        Result(Video video, String genres) {
            this.video = video;
            this.genres = genres;
        }

        Result(Tvshow show, String genres) {
            this.show = show;
            this.genres = genres;
        }

        /** A title to stream, under its group's heading ("Stream more from the world of …"). */
        SearchStreaming.Title stream;
        String streamHeading = "";

        Result(SearchStreaming.Title stream, String heading) {
            this.stream = stream;
            this.streamHeading = heading;
            this.genres = "";
        }

        long id() {
            if (stream != null)
                return -2_000_000_000L - stream.id;
            return show == null ? video.getId() : -show.getTvshowId() - 2;
        }

        /** Films · TV shows · Episodes · People (matched by cast, crew or studio rather than the title). */
        String group() {
            if (stream != null)
                return STREAM;
            if (people)
                return PEOPLE;
            if (show != null)
                return SHOWS;
            return video instanceof Episode ? EPISODES : FILMS;
        }
    }

    private ScrollView guidancePanel;
    private LinearLayout filters;
    private LinearLayout recentChips;
    private View recentLine;
    private String filter = ALL;
    private String lastQuery = "";

    static final String ALL = "All";
    static final String FILMS = "Films";
    static final String SHOWS = "TV shows";
    static final String EPISODES = "Episodes";
    static final String PEOPLE = "People";
    static final String STREAM = "Stream";
    static final String[] FILTERS = {ALL, FILMS, SHOWS, EPISODES, PEOPLE};
    static final String RECENT_KEY = "preview_search_recent";
    static final int RECENT_MAX = 8;
    static final int GROUP_CAP = 20;
    static final int MATCH = 0xFFFFFFFF;

    public PreviewSearch(Activity activity, int mode, Bundle state) {
        super(activity);
        Diagnostics.uiState("search", "results", "list", "none", "none", 0);
        this.activity = activity;
        this.mode = mode;
        setOrientation(VERTICAL);
        setPadding(TvLayout.sideMargin(getContext()), dp(8), TvLayout.sideMargin(getContext()), dp(14));
        setBackgroundColor(Color.TRANSPARENT);
        // Approved 0.156 Search (mock-ups search/01-07): one charcoal panel, keyboard left, results right.
        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(HORIZONTAL);
        android.graphics.drawable.GradientDrawable glass = new android.graphics.drawable.GradientDrawable();
        glass.setColor(SupernovaStyle.GLASS);
        glass.setCornerRadius(dp(Math.round(SupernovaStyle.PANEL_RADIUS_DP)));
        glass.setStroke(Math.max(1, dp(1)), SupernovaStyle.GLASS_EDGE);
        // 0.158 (page sheet SEARCH.md): plain charcoal, content straight on the page, no panel.
        panel.setBackground(null);
        panel.setPadding(0, dp(12), 0, dp(10));
        addView(panel, new LayoutParams(-1, 0, 1));
        LinearLayout keyboardColumn = new LinearLayout(activity);
        keyboardColumn.setOrientation(VERTICAL);
        LayoutParams keyboardSize = new LayoutParams(0, -1, .41f);
        panel.addView(keyboardColumn, keyboardSize);
        View divider = new View(activity);
        divider.setBackgroundColor(SupernovaStyle.HEADING_LINE);
        LayoutParams dividerSize = new LayoutParams(Math.max(1, dp(1)), -1);
        dividerSize.leftMargin = dp(18);
        dividerSize.rightMargin = dp(18);
        divider.setVisibility(INVISIBLE);
        panel.addView(divider, dividerSize);
        LinearLayout search = new LinearLayout(activity);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setPadding(dp(12), 0, dp(8), 0);
        android.graphics.drawable.GradientDrawable field = new android.graphics.drawable.GradientDrawable();
        field.setColor(SupernovaStyle.PANEL_REST);
        field.setCornerRadius(dp(12));
        search.setBackground(field);
        ImageView icon = new ImageView(activity);
        icon.setImageResource(R.drawable.preview_search);
        search.addView(icon, new LayoutParams(dp(18), dp(18)));
        query = new KeyboardInput(activity);
        query.setId(View.generateViewId());
        query.setHint("Movies, TV shows, people…");
        query.setTextColor(-1);
        query.setHintTextColor(0xff9aa3b2);
        query.setTextSize(17);
        query.setSingleLine(true);
        query.setMaxLines(1);
        query.setGravity(Gravity.CENTER_VERTICAL);
        query.setIncludeFontPadding(false);
        query.setBackgroundColor(Color.TRANSPARENT);
        query.setCursorVisible(true);
        query.setLongClickable(false);
        query.setImeOptions(EditorInfo.IME_ACTION_SEARCH |
                            EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        query.setPadding(dp(12), 0, dp(12), 0);
        search.addView(query, new LayoutParams(0, dp(38), 1));
        LayoutParams searchSize = new LayoutParams(-1, -2);
        searchSize.bottomMargin = dp(8);
        keyboardColumn.addView(search, searchSize);
        query.setShowSoftInputOnFocus(false);

        LinearLayout resultColumn = new LinearLayout(activity);
        resultColumn.setOrientation(VERTICAL);
        panel.addView(resultColumn, new LayoutParams(0, -1, .59f));
        LinearLayout top = new LinearLayout(activity);
        top.setGravity(Gravity.CENTER_VERTICAL);
        filters = new LinearLayout(activity);
        filters.setGravity(Gravity.CENTER_VERTICAL);
        for (String name : FILTERS)
            filters.addView(filterChip(name));
        top.addView(filters, new LayoutParams(-2, -2));
        status = text("", 12);
        status.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        status.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        top.addView(status, new LayoutParams(0, -2, 1));
        resultColumn.addView(top, new LayoutParams(-1, -2));
        filters.setVisibility(GONE);
        results = new PreviewFocusRecycler(activity);
        results.setId(View.generateViewId());
        query.setNextFocusRightId(results.getId());
        layout = new LinearLayoutManager(activity);
        results.setLayoutManager(layout);
        results.setAdapter(adapter);
        results.setPadding(0, dp(4), 0, dp(8));
        results.setClipToPadding(false);
        results.addItemDecoration(new GroupHeadings());
        FrameLayout resultArea = new FrameLayout(activity);
        resultColumn.addView(resultArea, new LayoutParams(-1, 0, 1));
        resultArea.addView(results, new FrameLayout.LayoutParams(-1, -1));
        guidancePanel = new ScrollView(activity);
        guidancePanel.addView(guidance());
        resultArea.addView(guidancePanel, new FrameLayout.LayoutParams(-1, -1));
        results.setVisibility(GONE);
        Diagnostics.uiRebuild(results, "search.results", "adapter_created", 0, adapter.getItemCount(), true);
        buildKeyboard(keyboardColumn);
        View spacer = new View(activity);
        keyboardColumn.addView(spacer, new LayoutParams(-1, 0, 1));

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schedule(s.toString());
            }
            @Override
            public void afterTextChanged(Editable e) {
            }
        });
        query.setOnEditorActionListener((v, id, event) -> {
            hideKeyboard();
            if (!items.isEmpty())
                results.requestFocus();
            return true;
        });
        if (state != null) {
            selectedId = state.getLong("preview_search_selected", -1);
            scrollState = state.getParcelable("preview_search_scroll");
            restoring = state.getBoolean("preview_search_results_focused", selectedId != -1);
            filter = state.getString("preview_search_filter", ALL);
            keyboard.restoreKey(state.getString("preview_search_key", "T"));
            query.setText(state.getString("preview_search_query", ""));
        }
        if (!restoring)
            keyboard.focusLastKey();
    }

    // ---- before typing: recent searches and "What you can search" ------------------------------------------

    /**
     * Before typing (mock-up `search-flat/17`): Recent searches (clock icon) as rows with a magnifying glass, Clear
     * history (plain words with a small ✕) at the end of the heading; then What you can search (light bulb), tips as
     * plain text with no lines. Headings with icons, about 26 px below and 64 px above (RULES §5).
     */
    private View guidance() {
        LinearLayout column = new LinearLayout(activity);
        column.setOrientation(VERTICAL);
        LinearLayout line = new LinearLayout(activity);
        line.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = heading("Recent searches", "history");
        line.addView(heading, new LayoutParams(0, -2, 1));
        TextView clear = TvButton.create(activity, "Clear history", this::clearRecent, false);
        PreviewIcon.apply(clear, "close", 12);
        SupernovaStyle.applyPlainButton(clear);
        clear.setTag("search.recent.clear");
        line.addView(clear);
        column.addView(line, new LayoutParams(-1, -2));
        recentLine = line;
        recentChips = new LinearLayout(activity);
        recentChips.setOrientation(VERTICAL);
        recentChips.setPadding(0, dp(6), 0, 0);
        column.addView(recentChips, new LayoutParams(-1, -2));
        bindRecent();
        LinearLayout tips = new LinearLayout(activity);
        tips.setOrientation(VERTICAL);
        tips.setPadding(0, dp(32), 0, 0);
        tips.addView(heading("What you can search", "info"));
        List<String[]> features = new ArrayList<>(Arrays.asList(
            new String[] {"film", "Titles", "movies and TV shows in your library"},
            new String[] {"people", "People", "actors, directors and writers"},
            new String[] {"genre", "Genres", "e.g. \"comedy\" or \"crime drama\""},
            new String[] {"year", "Years", "e.g. \"1994\" or \"1990s\""}));
        if (com.archos.mediacenter.video.streaming.StreamingRepository.enabled(activity))
            features.add(new String[] {"cloud", "Your services",
                                       "related titles you can stream, for things already in your library"});
        for (String[] feature : features) {
            LinearLayout row = new LinearLayout(activity);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(12), dp(6), 0, dp(6));
            ImageView glyph = new ImageView(activity);
            glyph.setImageDrawable(new PreviewIcon(feature[0]));
            row.addView(glyph, new LayoutParams(dp(16), dp(16)));
            TextView name = text(feature[1], 15);
            name.setTextColor(SupernovaStyle.TEXT);
            name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            LayoutParams nameSize = new LayoutParams(dp(120), -2);
            nameSize.leftMargin = dp(12);
            row.addView(name, nameSize);
            TextView line2 = text(feature[2], 14);
            line2.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            row.addView(line2, new LayoutParams(0, -2, 1));
            tips.addView(row);
        }
        column.addView(tips);
        return column;
    }

    /** A section heading with its icon, white, medium weight (RULES §5). */
    private TextView heading(String words, String icon) {
        TextView heading = text(words, 16);
        heading.setTextColor(SupernovaStyle.TEXT);
        heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        PreviewIcon.apply(heading, icon, 16);
        heading.setPadding(0, 0, 0, dp(6));
        return heading;
    }

    /** Settings › Library › Viewing History › Keep search history (owner, 25 Nov; SETTINGS.md, SEARCH.md). */
    public static final String KEEP_HISTORY = "supernova_keep_search_history";
    public static final String CLEAR_HISTORY = "supernova_clear_search_history";

    public static boolean keepsHistory(Context context) {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEEP_HISTORY, true);
    }

    /** Settings' Clear search history, and turning Keep search history off. */
    public static void clearHistory(Context context) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().remove(RECENT_KEY).apply();
    }

    static List<String> recent(Context context) {
        if (!keepsHistory(context))
            return new ArrayList<>();
        String saved = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                           .getString(RECENT_KEY, "");
        List<String> values = new ArrayList<>();
        for (String value : saved.split("\n"))
            if (!value.trim().isEmpty())
                values.add(value);
        return values;
    }

    /** Remembers a search that led somewhere (a result was opened), newest first, eight at most. */
    static void remember(Context context, String value) {
        String clean = value == null ? "" : value.trim().replace('\n', ' ');
        if (clean.isEmpty() || !keepsHistory(context))
            return;
        List<String> values = recent(context);
        values.removeIf(old -> old.equalsIgnoreCase(clean));
        values.add(0, clean);
        while (values.size() > RECENT_MAX)
            values.remove(values.size() - 1);
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(RECENT_KEY, TextUtils.join("\n", values)).apply();
    }

    /** Clear history: cleared at once, with an Undo notice (sheet). */
    private void clearRecent() {
        String saved = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity)
                           .getString(RECENT_KEY, "");
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(RECENT_KEY).apply();
        bindRecent();
        keyboard.focusLastKey();
        UndoNotice.show(activity, "Search history cleared", () -> {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit()
                .putString(RECENT_KEY, saved).apply();
            bindRecent();
        });
    }

    /** Recent searches as rows with a magnifying glass; faint lines between rows only (RULES §5). */
    private void bindRecent() {
        recentChips.removeAllViews();
        List<String> values = recent(activity);
        recentLine.setVisibility(values.isEmpty() ? GONE : VISIBLE);
        recentChips.setVisibility(values.isEmpty() ? GONE : VISIBLE);
        for (String value : values) {
            if (recentChips.getChildCount() >= 9)
                break;
            if (recentChips.getChildCount() > 0) {
                View rule = new View(activity);
                rule.setBackgroundColor(0x1FFFFFFF);
                recentChips.addView(rule, new LayoutParams(-1, Math.max(1, dp(1) / 2)));
            }
            TextView row = TvButton.create(activity, value, () -> {
                query.setText(value);
                query.setSelection(query.length());
            }, false);
            PreviewIcon.apply(row, "search", 14);
            SupernovaStyle.applyPlainButton(row);
            row.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            row.setTag("search.recent." + value);
            recentChips.addView(row, new LayoutParams(-1, dp(34)));
        }
    }

    // ---- filters --------------------------------------------------------------------------------------------

    private TextView filterChip(String name) {
        TextView chip = TvButton.create(activity, name, () -> setFilter(name), false);
        chip.setTag("search.filter." + name);
        LayoutParams gap = new LayoutParams(-2, -2);
        gap.rightMargin = dp(8);
        chip.setLayoutParams(gap);
        chip.setOnKeyListener((v, code, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (code == KeyEvent.KEYCODE_DPAD_DOWN) {
                focusFirstResult();
                return true;
            }
            if (code == KeyEvent.KEYCODE_DPAD_LEFT && filters.indexOfChild(v) == 0) {
                keyboard.focusLastKey();
                return true;
            }
            return code == KeyEvent.KEYCODE_DPAD_UP;
        });
        return chip;
    }

    void setFilter(String name) {
        filter = name;
        for (int i = 0; i < filters.getChildCount(); i++) {
            View chip = filters.getChildAt(i);
            chip.setSelected(("search.filter." + name).equals(chip.getTag()));
        }
        adapter.notifyDataSetChanged();
        updateStatus(null);
    }

    String filter() {
        return filter;
    }

    private void focusFirstResult() {
        if (visible().isEmpty())
            return;
        results.scrollToPosition(0);
        results.post(() -> {
            RecyclerView.ViewHolder first = results.findViewHolderForAdapterPosition(0);
            if (first != null)
                first.itemView.requestFocus();
        });
    }

    /** The results under the current filter; All shows every group, each capped at twenty. */
    List<Result> visible() {
        List<Result> shown = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        for (String group : new String[] {FILMS, SHOWS, EPISODES, PEOPLE, STREAM})
            for (Result result : items) {
                if (!group.equals(result.group()))
                    continue;
                if (!ALL.equals(filter) && !filter.equals(group))
                    continue;
                int count = counts.merge(group, 1, Integer::sum);
                if (ALL.equals(filter) && count > GROUP_CAP)
                    continue;
                shown.add(result);
            }
        return shown;
    }

    int groupCount(String group) {
        int count = 0;
        for (Result result : items)
            if (group.equals(result.group()))
                count++;
        return count;
    }

    private void updateStatus(String error) {
        if (error != null) {
            status.setText(error);
            return;
        }
        if (lastQuery.isEmpty()) {
            status.setText("");
            return;
        }
        int count = ALL.equals(filter) ? items.size() : groupCount(filter);
        status.setText(count == 0 ? "Nothing in your library matches “" + lastQuery + "”" : "");
    }

    private void buildKeyboard(LinearLayout column) {
        keyboard = new PreviewKeyboard(activity, query, () -> {
            if (!items.isEmpty()) {
                results.scrollToPosition(0);
                results.post(() -> {
                    RecyclerView.ViewHolder first = results.findViewHolderForAdapterPosition(0);
                    if (first != null)
                        first.itemView.requestFocus();
                });
            }
        });
        column.addView(keyboard, new LayoutParams(-1, -2));
        keyboard.setActions(() -> {
            if (!items.isEmpty())
                results.requestFocus();
        }, () -> activity.onBackPressed());
        // Search keeps the keyboard up while browsing results, so there is no Done · Cancel row.
        keyboard.setDoneRowVisible(false);
        if (activity instanceof VideoSearchActivity)
            keyboard.setVoice(((VideoSearchActivity)activity)::startVoice);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        super.onWindowFocusChanged(focused);
        if (!focused && !detailsFocusToken.isEmpty())
            detailsWindowAway = true;
        if (focused) {
            hideKeyboard();
            if (detailsWindowAway && !detailsFocusToken.isEmpty())
                main.post(() -> {
                    if (!isAttachedToWindow() || detailsFocusToken.isEmpty())
                        return;
                    View requested = detailsOpener.get(), restored = findFocus();
                    Diagnostics.focusRestored(detailsFocusToken, requested, restored, requested != restored,
                                              restored != null && results.hasFocus());
                    detailsFocusToken = "";
                    detailsOpener.clear();
                    detailsWindowAway = false;
                });
        }
    }

    private void recordDetailsEntry(View opener) {
        detailsOpener = new WeakReference<>(opener);
        detailsFocusToken = Diagnostics.focusEntry(opener, "search.details");
        detailsWindowAway = false;
    }

    public boolean atTop() {
        return keyboard.atTop();
    }

    public void focusQuery() {
        keyboard.focusLastKey();
    }

    public void acceptVoice(String text) {
        query.setText(text);
        query.setSelection(query.length());
    }

    public void save(Bundle state) {
        state.putBoolean("preview_search_results_focused", results.hasFocus());
        state.putString("preview_search_key", keyboard.focusedKey());
        state.putString("preview_search_query", query.getText().toString());
        state.putLong("preview_search_selected", selectedId);
        state.putString("preview_search_filter", filter);
        state.putParcelable("preview_search_scroll", layout.onSaveInstanceState());
    }

    private void hideKeyboard() {
        InputMethodManager ime = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (ime != null)
            ime.hideSoftInputFromWindow(query.getWindowToken(), 0);
    }

    private void schedule(String value) {
        if (pending != null)
            main.removeCallbacks(pending);
        if (cancellation != null)
            cancellation.cancel();
        int request = ++generation;
        String search = value.trim();
        if (search.isEmpty()) {
            int previous = items.size();
            items.clear();
            lastQuery = "";
            results.setVisibility(GONE);
            filters.setVisibility(GONE);
            bindRecent();
            guidancePanel.setVisibility(VISIBLE);
            adapter.notifyDataSetChanged();
            Diagnostics.uiRebuild(results, "search.results", "query_cleared", previous, 0, false);
            status.setText("");
            return;
        }
        pending = () -> load(search, request);
        main.postDelayed(pending, 250);
    }

    private void load(String value, int request) {
        final long started = SystemClock.elapsedRealtime();
        final String operation = Diagnostics.operation("search");
        VideoLoader loader;
        switch (mode) {
        case VideoSearchActivity.SEARCH_MODE_MOVIE:
            SearchMovieLoader movie = new SearchMovieLoader(activity);
            movie.setQuery("");
            loader = movie;
            break;
        case VideoSearchActivity.SEARCH_MODE_EPISODE:
            SearchEpisodeLoader ep = new SearchEpisodeLoader(activity);
            ep.setQuery("");
            loader = ep;
            break;
        case VideoSearchActivity.SEARCH_MODE_NON_SCRAPED:
            SearchNonScrapedVideoLoader file = new SearchNonScrapedVideoLoader(activity);
            file.setQuery("");
            loader = file;
            break;
        default:
            SearchVideoLoader all = new SearchVideoLoader(activity);
            all.setQuery("");
            loader = all;
        }
        Uri uri = loader.getUri();
        String[] extra = {
            VideoStore.Video.VideoColumns.SCRAPER_M_GENRES,  VideoStore.Video.VideoColumns.SCRAPER_S_GENRES,
            VideoStore.Video.VideoColumns.SCRAPER_ACTORS,    VideoStore.Video.VideoColumns.SCRAPER_DIRECTORS,
            VideoStore.Video.VideoColumns.SCRAPER_WRITERS,   VideoStore.Video.VideoColumns.SCRAPER_STUDIOS,
            VideoStore.Video.VideoColumns.SCRAPER_E_ACTORS,  VideoStore.Video.VideoColumns.SCRAPER_E_DIRECTORS,
            VideoStore.Video.VideoColumns.SCRAPER_E_WRITERS, VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID};
        String[] projection = Arrays.copyOf(loader.getProjection(), loader.getProjection().length + extra.length);
        System.arraycopy(extra, 0, projection, loader.getProjection().length, extra.length);
        String selection = loader.getSelection(), sort = loader.getSortOrder();
        String[] args = loader.getSelectionArgs();
        CancellationSignal signal = new CancellationSignal();
        cancellation = signal;
        status.setText("Searching library…");
        Context app = activity.getApplicationContext();
        AllTvshowsLoader showLoader = new AllTvshowsLoader(activity);
        Uri showUri = showLoader.getUri();
        String[] showProjection = showLoader.getProjection(), showArgs = showLoader.getSelectionArgs();
        String showSelection = showLoader.getSelection(), showSort = showLoader.getSortOrder();
        worker.execute(() -> {
            Map<Long, String[]> movieNames = metadata(app, false, signal), showNames = metadata(app, true, signal);
            List<Result> found = new ArrayList<>();
            String error = null;
            try (Cursor cursor = app.getContentResolver().query(uri, projection, selection, args, sort, signal)) {
                if (cursor != null) {
                    VideoCursorMapper mapper = new VideoCursorMapper();
                    mapper.bindColumns(cursor);
                    while (cursor.moveToNext()) {
                        signal.throwIfCanceled();
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(VideoLoader.COLUMN_NAME));
                        String episode = cursor.getString(
                            cursor.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_E_NAME));
                        String path =
                            cursor.getString(cursor.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.DATA));
                        Video video = (Video)mapper.bind(cursor);
                        List<String> titles = new ArrayList<>(Arrays.asList(name, episode));
                        List<String> searchable = new ArrayList<>();
                        for (String column : extra)
                            if (!column.endsWith("genres") &&
                                !column.equals(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID))
                                searchable.add(cell(cursor, column));
                        String[] local =
                            video instanceof Movie ? movieNames.get(((Movie)video).getMovieId())
                            : video instanceof Episode
                                ? showNames.get(longCell(cursor, VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID))
                                : null;
                        if (local != null)
                            Collections.addAll(titles, local);
                        int rank = PreviewSearchText.rankFields(value, titles, searchable,
                            path == null ? "" : Uri.parse(path).getLastPathSegment());
                        if (rank == 0)
                            continue;
                        String genres = cursor.getString(cursor.getColumnIndexOrThrow(
                            video instanceof Episode ? VideoStore.Video.VideoColumns.SCRAPER_S_GENRES
                                                     : VideoStore.Video.VideoColumns.SCRAPER_M_GENRES));
                        Result result = new Result(video, genres);
                        result.rank = rank;
                        result.people = PreviewSearchText.rankFields(value, titles, Collections.emptyList(),
                                                                     "") == 0 &&
                                        PreviewSearchText.rankFields(value, Collections.emptyList(), searchable,
                                                                     "") > 0;
                        result.showId = longCell(cursor, VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);
                        if (result.people) {
                            List<String> columns = new ArrayList<>();
                            List<String> values = new ArrayList<>();
                            for (String column : extra)
                                if (!column.endsWith("genres") &&
                                    !column.equals(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID)) {
                                    columns.add(column);
                                    values.add(cell(cursor, column));
                                }
                            result.reason = reason(value, columns, values);
                        }
                        found.add(result);
                    }
                }
            } catch (OperationCanceledException cancelled) {
                Diagnostics.event("search_cancelled", "operation_id", operation);
                Diagnostics.finishOperation(operation, "search", started);
                return;
            } catch (RuntimeException failure) {
                Diagnostics.error("search_failed", failure);
                error = "Library search is temporarily unavailable";
            }
            if (mode != VideoSearchActivity.SEARCH_MODE_MOVIE && mode != VideoSearchActivity.SEARCH_MODE_NON_SCRAPED) {
                List<Result> parents = new ArrayList<>();
                try (Cursor shows = app.getContentResolver().query(showUri, showProjection, showSelection, showArgs,
                                                                   showSort, signal)) {
                    if (shows != null) {
                        TvshowCursorMapper mapper =
                            new TvshowCursorMapper();
                        mapper.bindColumns(shows);
                        while (shows.moveToNext()) {
                            signal.throwIfCanceled();
                            Tvshow show = (Tvshow)mapper.bind(shows);
                            List<String> names = new ArrayList<>();
                            names.add(show.getName());
                            names.add(show.getActors());
                            names.add(show.getStudio());
                            String[] local = showNames.get(show.getTvshowId());
                            if (local != null)
                                Collections.addAll(names, local);
                            List<String> titles = new ArrayList<>();
                            titles.add(show.getName());
                            if (local != null) Collections.addAll(titles, local);
                            int rank = PreviewSearchText.rankFields(value, titles,
                                Arrays.asList(show.getActors(), show.getStudio()), "");
                            for (Result result : found)
                                if (result.showId == show.getTvshowId())
                                    rank = Math.max(rank, result.rank);
                            if (rank > 0) {
                                Result result = new Result(show, "");
                                result.rank = rank;
                                result.reason = reason(value, Arrays.asList("actors", "studio"),
                                                       Arrays.asList(show.getActors(), show.getStudio()));
                                result.people = PreviewSearchText.rankFields(value, titles,
                                                                             Collections.emptyList(), "") == 0 &&
                                                PreviewSearchText.rankFields(value, Collections.emptyList(),
                                                    Arrays.asList(show.getActors(), show.getStudio()), "") > 0;
                                parents.add(result);
                            }
                        }
                    }
                } catch (OperationCanceledException cancelled) {
                    Diagnostics.event("search_cancelled", "operation_id", operation);
                    Diagnostics.finishOperation(operation, "search", started);
                    return;
                } catch (RuntimeException failure) {
                    Diagnostics.error("search_series_failed", failure);
                }
                found.addAll(0, parents);
            }
            found.sort(Comparator.comparingInt((Result item) -> item.rank)
                           .reversed()
                           .thenComparing(item
                                          -> item.show == null ? item.video.getName() : item.show.getName(),
                                          String.CASE_INSENSITIVE_ORDER));
            String message = error;
            Diagnostics.finishOperation(operation, "search", started);
            Diagnostics.event("search_results", "operation_id", operation,
                                                                       "count", found.size());
            main.post(() -> {
                if (request != generation || !isAttachedToWindow())
                    return;
                boolean focusResults = results.hasFocus();
                int previous = items.size();
                items.clear();
                items.addAll(found);
                lastQuery = value;
                results.setVisibility(items.isEmpty() ? GONE : VISIBLE);
                // No filter chips on the 0.158 Search (page sheet): one list, library first.
                filters.setVisibility(GONE);
                loadStreaming(found, request);
                guidancePanel.setVisibility(GONE);
                setFilter(filter);
                Diagnostics.uiRebuild(results, "search.results", "query_result", previous, items.size(), false);
                updateStatus(message);
                // Coming back from Details restores the same query, filter and row (approved search/06).
                int position = 0;
                List<Result> shown = visible();
                if (selectedId != -1)
                    for (int i = 0; i < shown.size(); i++)
                        if (shown.get(i).id() == selectedId) {
                            position = i;
                            break;
                        }
                if (scrollState != null) {
                    layout.onRestoreInstanceState(scrollState);
                    scrollState = null;
                }
                if ((restoring || focusResults) && !items.isEmpty()) {
                    final int target = position;
                    results.scrollToPosition(target);
                    results.post(() -> {
                        RecyclerView.ViewHolder h = results.findViewHolderForAdapterPosition(target);
                        if (h != null)
                            h.itemView.requestFocus();
                    });
                }
                restoring = false;
            });
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_DOWN &&
            (e.getUnicodeChar() != 0 || e.getKeyCode() == KeyEvent.KEYCODE_DEL ||
             e.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL) &&
            query.onKeyDown(e.getKeyCode(), e))
            return true;
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT &&
            results.hasFocus()) {
            keyboard.focusLastKey();
            return true;
        }
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && results.hasFocus()) {
            View focus = results.findFocus(), row = focus == null ? null : results.findContainingItemView(focus);
            if (row != null && results.getChildAdapterPosition(row) == 0) {
                // Up from the first result reaches the filters (approved search/06).
                View chip = filters.findViewWithTag("search.filter." + filter);
                if (chip != null && filters.getVisibility() == VISIBLE)
                    chip.requestFocus();
                else
                    keyboard.focusLastKey();
                return true;
            }
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    protected void onDetachedFromWindow() {
        main.removeCallbacksAndMessages(null);
        if (cancellation != null)
            cancellation.cancel();
        worker.shutdownNow();
        super.onDetachedFromWindow();
    }

    private static long longCell(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index < 0 ? -1 : cursor.getLong(index);
    }

    private static String cell(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index < 0 || cursor.isNull(index) ? "" : cursor.getString(index);
    }

    private static Map<Long, String[]> metadata(Context context, boolean shows, CancellationSignal cancellation) {
        Map<Long, String[]> result = new HashMap<>();
        String[] columns =
            shows ? new String[] {"_id", "original_title_show"} : new String[] {"_id", "original_title_movie"};
        Uri uri = shows ? ScraperStore.Show.URI.ALL
                                    : ScraperStore.Movie.URI.ALL;
        try (Cursor cursor = context.getContentResolver().query(uri, columns, null, null, null, cancellation)) {
            if (cursor != null)
                while (cursor.moveToNext()) {
                    String[] values = new String[columns.length - 1];
                    for (int i = 1; i < columns.length; i++)
                        values[i - 1] = cell(cursor, columns[i]);
                    result.put(cursor.getLong(cursor.getColumnIndexOrThrow("_id")), values);
                }
        } catch (OperationCanceledException cancelled) {
            return result;
        } catch (RuntimeException unavailable) {
            Diagnostics.error("search_indexed_metadata_unavailable", unavailable);
        }
        return result;
    }

    private int dp(int n) {
        return PreviewDialog.dp(activity, n);
    }

    private TextView text(String s, int size) {
        TextView t = new TextView(activity);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(0xffc0d3df);
        t.setSingleLine(true);
        t.setEllipsize(TextUtils.TruncateAt.END);
        return t;
    }

    private class Holder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView metadata;
        TextView pill;

        Holder(LinearLayout row) {
            super(row);
            image = new ImageView(activity);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            android.graphics.drawable.GradientDrawable frame = new android.graphics.drawable.GradientDrawable();
            frame.setColor(0xFF22262D);
            frame.setCornerRadius(dp(4));
            image.setBackground(frame);
            image.setClipToOutline(true);
            row.addView(image, new LayoutParams(dp(32), dp(48)));
            LinearLayout labels = new LinearLayout(activity);
            labels.setOrientation(VERTICAL);
            labels.setPadding(dp(12), 0, dp(12), 0);
            title = text("", 16);
            title.setTextColor(SupernovaStyle.TEXT);
            metadata = text("", 12);
            metadata.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            labels.addView(title);
            labels.addView(metadata);
            row.addView(labels, new LayoutParams(0, -2, 1));
            pill = text("", 12);
            pill.setGravity(Gravity.CENTER);
            pill.setPadding(dp(10), dp(4), dp(10), dp(4));
            row.addView(pill, new LayoutParams(-2, -2));
        }
    }

    /** "Dune" with the typed letters bold and underlined wherever a word starts with them. */
    static CharSequence highlighted(String title, String typed) {
        SpannableStringBuilder text = new SpannableStringBuilder(title == null ? "" : title);
        if (typed == null || typed.trim().isEmpty() || text.length() == 0)
            return text;
        String lower = text.toString().toLowerCase(Locale.ROOT);
        for (String word : typed.trim().toLowerCase(Locale.ROOT).split("\\s+")) {
            if (word.isEmpty())
                continue;
            int from = 0;
            while (from <= lower.length() - word.length()) {
                int at = lower.indexOf(word, from);
                if (at < 0)
                    break;
                boolean start = at == 0 || !Character.isLetterOrDigit(lower.charAt(at - 1));
                if (start) {
                    text.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), at, at + word.length(),
                                 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(new android.text.style.UnderlineSpan(), at, at + word.length(),
                                 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                from = at + word.length();
            }
        }
        return text;
    }

    /** "Film · 155 min · 4K HDR · Science Fiction, Adventure": only what is known, never "unavailable". */
    static String details(String kind, int minutes, String quality, String genres) {
        List<String> parts = new ArrayList<>();
        parts.add(kind);
        if (minutes > 0)
            parts.add(minutes + " min");
        if (quality != null && !quality.isEmpty() && !quality.toLowerCase(Locale.ROOT).contains("unavailable"))
            parts.add(quality);
        if (genres != null && !genres.trim().isEmpty())
            parts.add(genres.trim());
        return TextUtils.join(" · ", parts);
    }

    /**
     * Pills in a fixed column (RULES §7; sheet): "Resume S5 E3" (or "Resume" for a film), "Watched", "Unwatched";
     * plain text in a thin outline, no colour; never "In library" (the heading says so).
     */
    /**
     * A title to stream (SEARCH.md): name and year, "Movie · sequel", the service's pill with the open-in-app icon;
     * OK opens it in the service's app (its own link where there is one), never a Supernova page.
     */
    private void bindStream(Holder h, SearchStreaming.Title title) {
        Diagnostics.semantic(h.itemView, "search.result.stream." + title.id);
        ArtworkRequest.cancel(h.image);
        h.image.setImageDrawable(null);
        h.title.setText(withYear(title.name, title.year));
        h.metadata.setText(title.relation.isEmpty() ? "Movie" : "Movie · " + title.relation);
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setCornerRadius(dp(12));
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(Math.max(1, dp(1)), 0x80FFFFFF);
        h.pill.setText(title.service);
        h.pill.setTextColor(SupernovaStyle.TEXT);
        h.pill.setBackground(shape);
        PreviewIcon.apply(h.pill, "external", 12);
        h.pill.setVisibility(VISIBLE);
        if (!title.poster.isEmpty()) {
            Uri art = Uri.parse("https://image.tmdb.org/t/p/w185" + title.poster);
            ArtworkRequest.load(h.image, art, title.id, "search.results", "poster",
                                Picasso.get().load(art).resize(dp(32), dp(48)).centerCrop().noFade());
        }
        h.itemView.setOnFocusChangeListener(null);
        h.itemView.setOnClickListener(v -> {
            remember(activity, lastQuery);
            if (!title.link.isEmpty())
                com.archos.mediacenter.video.streaming.StreamingActions.openWeb(activity, title.link);
            else
                com.archos.mediacenter.video.streaming.StreamingActions.openApp(activity, title.service);
        });
    }

    /** After the library results: the film's streaming group, if its story has titles on your services. */
    private void loadStreaming(List<Result> found, int request) {
        if (!com.archos.mediacenter.video.streaming.StreamingRepository.enabled(activity))
            return;
        Movie source = null;
        java.util.Set<Long> owned = new java.util.HashSet<>();
        for (Result result : found)
            if (result.video instanceof Movie && ((Movie)result.video).getOnlineId() > 0) {
                owned.add(((Movie)result.video).getOnlineId());
                if (source == null && !result.people)
                    source = (Movie)result.video;
            }
        if (source == null)
            return;
        Movie film = source;
        Context app = activity.getApplicationContext();
        worker.execute(() -> {
            SearchStreaming.Group group = SearchStreaming.worldOf(app, film.getOnlineId(), film.getName(),
                                                                  film.getYear(), owned, () -> request != generation);
            if (group == null)
                return;
            main.post(() -> {
                if (request != generation || !isAttachedToWindow())
                    return;
                items.removeIf(item -> item.stream != null);
                for (SearchStreaming.Title title : group.titles)
                    items.add(new Result(title, group.heading));
                adapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * Who matched and as what (SEARCH.md: "Taylor Sheridan · creator"): the first name in cast, crew or studio that the
     * typed words find, with its role from the field.
     */
    static String reason(String typed, List<String> columns, List<String> values) {
        for (int i = 0; i < columns.size() && i < values.size(); i++) {
            String value = values.get(i);
            if (value == null || value.trim().isEmpty())
                continue;
            for (String part : value.split("[,|]")) {
                String name = part.replaceAll("\\(.*?\\)", "").trim();
                if (name.isEmpty() || PreviewSearchText.rank(typed, name) <= 0)
                    continue;
                String column = columns.get(i).toLowerCase(Locale.ROOT);
                String role = column.contains("director") ? "director"
                              : column.contains("writer") ? "writer"
                              : column.contains("studio") ? "studio"
                                                          : "cast";
                return name + " · " + role;
            }
        }
        return "";
    }

    private void bindPill(TextView pill, Video video) {
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setCornerRadius(dp(12));
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(Math.max(1, dp(1)), 0x80FFFFFF);
        if (video == null) {
            pill.setVisibility(GONE);
            return;
        }
        if (video.isWatched())
            pill.setText("✓ Watched");
        else if (video.getResumeMs() > 0)
            pill.setText(video instanceof Episode ? "▶ Resume S" + ((Episode)video).getSeasonNumber() + " E" +
                                                        ((Episode)video).getEpisodeNumber()
                                                  : "▶ Resume");
        else
            pill.setText("○ Unwatched");
        pill.setTextColor(SupernovaStyle.TEXT);
        pill.setBackground(shape);
        pill.setVisibility(VISIBLE);
    }

    /** The group headings ("Films · 2") drawn above the first row of each group, without becoming list items. */
    private final class GroupHeadings extends RecyclerView.ItemDecoration {
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        GroupHeadings() {
            paint.setColor(SupernovaStyle.TEXT);
            paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            paint.setTextSize(16 * activity.getResources().getDisplayMetrics().scaledDensity);
        }

        /**
         * "In your library" above the first result (sheet: the library is the key), then each streaming group's own
         * heading ("Stream more from the world of …") above its first title.
         */
        private String heading(int position) {
            List<Result> shown = visible();
            if (position < 0 || position >= shown.size())
                return null;
            Result here = shown.get(position);
            if (here.stream != null)
                return position == 0 || shown.get(position - 1).stream == null ||
                           !shown.get(position - 1).streamHeading.equals(here.streamHeading)
                    ? here.streamHeading : null;
            return position == 0 ? "In your library" : null;
        }

        @Override
        public void getItemOffsets(android.graphics.Rect out, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            int at = parent.getChildAdapterPosition(view);
            // A later group's heading sits clearly apart from the group above (RULES §5).
            out.set(0, heading(at) == null ? 0 : at == 0 ? dp(36) : dp(58), 0, 0);
        }

        @Override
        public void onDraw(android.graphics.Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                String text = heading(parent.getChildAdapterPosition(child));
                if (text != null) {
                    PreviewIcon books = new PreviewIcon(text.startsWith("Stream") ? "cloud" : "library");
                    int size = dp(16);
                    int top = child.getTop() - dp(13) - size;
                    books.setBounds(child.getLeft() + dp(4), top, child.getLeft() + dp(4) + size, top + size);
                    books.draw(canvas);
                    canvas.drawText(text, child.getLeft() + dp(4) + size + dp(10), child.getTop() - dp(14), paint);
                }
            }
        }
    }

    private class ResultAdapter extends RecyclerView.Adapter<Holder> {
        @Override
        public int getItemCount() {
            return visible().size();
        }

        public Holder onCreateViewHolder(ViewGroup parent, int type) {
            LinearLayout row = new LinearLayout(activity);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(8), dp(6), dp(12), dp(6));
            row.setFocusable(true);
            row.setFocusableInTouchMode(true);
            row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            row.setBackground(SupernovaStyle.rowFocus(activity));
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp(60));
            lp.bottomMargin = dp(4);
            row.setLayoutParams(lp);
            return new Holder(row);
        }

        public void onBindViewHolder(Holder h, int position) {
            Result r = visible().get(position);
            if (r.stream != null) {
                bindStream(h, r.stream);
                return;
            }
            Diagnostics.semantic(h.itemView, "search.result." + (r.show == null ? "video." : "series.") +
                                                 (r.show == null ? r.video.getId() : r.show.getTvshowId()));
            String typed = query.getText().toString();
            ArtworkRequest.cancel(h.image);
            h.image.setImageDrawable(null);
            if (r.show != null) {
                Tvshow show = r.show;
                h.title.setText(withYear(highlighted(show.getName(), typed), show.getYear()));
                String seasons = show.getSeasonCount() > 0
                                     ? show.getSeasonCount() + (show.getSeasonCount() == 1 ? " season" : " seasons")
                                     : "";
                h.metadata.setText(r.reason.isEmpty() ? details("TV show", 0, seasons, "")
                                                      : r.reason + " · " + details("TV show", 0, seasons, ""));
                h.pill.setVisibility(GONE);
                if (show.getPosterUri() != null)
                    ArtworkRequest.load(
                        h.image, show.getPosterUri(), show.getTvshowId(), "search.results", "poster",
                        Picasso.get().load(show.getPosterUri()).resize(dp(32), dp(48)).centerCrop().noFade());
                h.itemView.setOnFocusChangeListener((v, focused) -> {
                    if (focused) {
                        selectedId = r.id();
                        Diagnostics.focusedMedia(show.getTvshowId());
                    }
                });
                h.itemView.setOnClickListener(v -> {
                    selectedId = r.id();
                    remember(activity, lastQuery.isEmpty() ? typed : lastQuery);
                    hideKeyboard();
                    recordDetailsEntry(v);
                    VideoViewClickedListener.showTvshowDetails(activity, show, new Presenter.ViewHolder(v));
                });
                return;
            }
            Video video = r.video;
            int minutes = video.getDurationMs() > 0 ? video.getDurationMs() / 60000 : 0;
            if (video instanceof Episode) {
                Episode e = (Episode)video;
                String name = TextUtils.isEmpty(e.getEpisodeName()) ? "Episode " + e.getEpisodeNumber()
                                                                    : e.getEpisodeName();
                h.title.setText(highlighted(e.getShowName() + " · " + name, typed));
                h.metadata.setText(details("Episode · S" + e.getSeasonNumber() + " E" + e.getEpisodeNumber(),
                                           minutes, PreviewVariants.label(video), ""));
            } else {
                int year = video instanceof Movie ? ((Movie)video).getYear() : 0;
                h.title.setText(withYear(highlighted(video.getName(), typed), year));
                String about = details("Movie", minutes, PreviewVariants.label(video), r.genres);
                h.metadata.setText(r.reason.isEmpty() ? about : r.reason + " · " + about);
            }
            bindPill(h.pill, video);
            Uri art = video instanceof Episode ? ((Episode)video).getPictureUri() : video.getPosterUri();
            if (art != null)
                ArtworkRequest.load(h.image, art, video.getId(), "search.results",
                                    video instanceof Episode ? "episode_still" : "poster",
                                    Picasso.get().load(art).resize(dp(32), dp(48)).centerCrop().noFade());
            h.itemView.setOnFocusChangeListener((v, focused) -> {
                if (focused) {
                    selectedId = video.getId();
                    Diagnostics.focusedMedia(video.getId());
                }
            });
            h.itemView.setOnClickListener(v -> {
                selectedId = video.getId();
                remember(activity, lastQuery.isEmpty() ? typed : lastQuery);
                hideKeyboard();
                recordDetailsEntry(v);
                VideoViewClickedListener.showVideoDetails(activity, video, new Presenter.ViewHolder(v), true, -1);
            });
        }

        public void onViewRecycled(Holder h) {
            ArtworkRequest.cancel(h.image);
            h.image.setImageDrawable(null);
        }
    }

    /** "Dune  2021": the year small and grey after the title. */
    private CharSequence withYear(CharSequence title, int year) {
        SpannableStringBuilder text = new SpannableStringBuilder(title);
        if (year <= 0)
            return text;
        int start = text.length();
        text.append("  ").append(String.valueOf(year));
        text.setSpan(new android.text.style.ForegroundColorSpan(SupernovaStyle.TEXT_SECONDARY), start, text.length(),
                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new android.text.style.RelativeSizeSpan(.85f), start, text.length(),
                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }
}
