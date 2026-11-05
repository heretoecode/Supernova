package com.archos.mediacenter.video.leanback.search;

import android.app.Activity;
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

        Result(Video video, String genres) {
            this.video = video;
            this.genres = genres;
        }

        Result(Tvshow show, String genres) {
            this.show = show;
            this.genres = genres;
        }

        long id() {
            return show == null ? video.getId() : -show.getTvshowId() - 2;
        }

        /** Films · TV shows · Episodes · People (matched by cast, crew or studio rather than the title). */
        String group() {
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
        setPadding(dp(28), dp(8), dp(28), dp(14));
        setBackgroundColor(Color.TRANSPARENT);
        // Approved 0.156 Search (mock-ups search/01-07): one charcoal panel, keyboard left, results right.
        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(HORIZONTAL);
        android.graphics.drawable.GradientDrawable glass = new android.graphics.drawable.GradientDrawable();
        glass.setColor(SupernovaStyle.GLASS);
        glass.setCornerRadius(dp(Math.round(SupernovaStyle.PANEL_RADIUS_DP)));
        glass.setStroke(Math.max(1, dp(1)), SupernovaStyle.GLASS_EDGE);
        panel.setBackground(glass);
        panel.setPadding(dp(12), dp(12), dp(16), dp(10));
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
        query.setHint("Films, shows, episodes, people");
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
        TextView hint = text("/ . @ always on the letters page · #+= for every other symbol · ⇧ for capitals", 11);
        hint.setTextColor(0xff8b93a1);
        keyboardColumn.addView(hint);
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

    private View guidance() {
        LinearLayout column = new LinearLayout(activity);
        column.setOrientation(VERTICAL);
        LinearLayout line = new LinearLayout(activity);
        line.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = text("Recent searches", 14);
        heading.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        PreviewIcon.apply(heading, "history", 16);
        line.addView(heading, new LayoutParams(0, -2, 1));
        TextView clear = TvButton.create(activity, "Clear recent", this::clearRecent, false);
        clear.setTag("search.recent.clear");
        line.addView(clear);
        column.addView(line, new LayoutParams(-1, -2));
        recentLine = line;
        recentChips = new LinearLayout(activity);
        recentChips.setPadding(0, dp(8), 0, dp(4));
        column.addView(recentChips, new LayoutParams(-1, -2));
        bindRecent();
        LinearLayout tips = new LinearLayout(activity);
        tips.setOrientation(VERTICAL);
        tips.setPadding(dp(36), dp(20), dp(36), 0);
        TextView title = text("What you can search", 19);
        title.setTextColor(SupernovaStyle.TEXT);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        title.setPadding(0, 0, 0, dp(8));
        tips.addView(title);
        String[][] features = {
            {"movie", "Titles and episodes", "Films, TV shows, episode names — and file names as a last resort"},
            {"language", "Original titles", "A film or show’s original-language title finds it too"},
            {"people", "Cast, crew and studios", "Actors, directors, writers and studios, by full name or surname"},
            {"custom row", "Any order, part words", "“horses slow” and “slow hor” both find Slow Horses"},
            {"search", "Forgiving", "Capitals, accents, punctuation and a leading “The” don’t matter"},
            {"spelling", "Small typos", "One wrong letter in a longer word still finds it — “Dume” finds Dune"}};
        for (int i = 0; i < features.length; i++) {
            LinearLayout row = new LinearLayout(activity);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(7), 0, dp(7));
            ImageView glyph = new ImageView(activity);
            glyph.setImageDrawable(new PreviewIcon(features[i][0]));
            row.addView(glyph, new LayoutParams(dp(18), dp(18)));
            LinearLayout words = new LinearLayout(activity);
            words.setOrientation(VERTICAL);
            words.setPadding(dp(14), 0, 0, 0);
            TextView name = text(features[i][1], 15);
            name.setTextColor(SupernovaStyle.TEXT);
            words.addView(name);
            TextView line2 = text(features[i][2], 12);
            line2.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            words.addView(line2);
            row.addView(words, new LayoutParams(0, -2, 1));
            tips.addView(row);
            if (i + 1 < features.length) {
                View rule = new View(activity);
                rule.setBackgroundColor(0x1FFFFFFF);
                tips.addView(rule, new LayoutParams(-1, Math.max(1, dp(1))));
            }
        }
        column.addView(tips);
        return column;
    }

    static List<String> recent(Context context) {
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
        if (clean.isEmpty())
            return;
        List<String> values = recent(context);
        values.removeIf(old -> old.equalsIgnoreCase(clean));
        values.add(0, clean);
        while (values.size() > RECENT_MAX)
            values.remove(values.size() - 1);
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(RECENT_KEY, TextUtils.join("\n", values)).apply();
    }

    private void clearRecent() {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(RECENT_KEY).apply();
        bindRecent();
        keyboard.focusLastKey();
    }

    private void bindRecent() {
        recentChips.removeAllViews();
        List<String> values = recent(activity);
        recentLine.setVisibility(values.isEmpty() ? GONE : VISIBLE);
        recentChips.setVisibility(values.isEmpty() ? GONE : VISIBLE);
        for (String value : values) {
            if (recentChips.getChildCount() >= 5)
                break;
            TextView chip = TvButton.create(activity, value, () -> {
                query.setText(value);
                query.setSelection(query.length());
            }, false);
            chip.setTag("search.recent." + value);
            LayoutParams gap = new LayoutParams(-2, -2);
            gap.rightMargin = dp(8);
            recentChips.addView(chip, gap);
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
        for (String group : new String[] {FILMS, SHOWS, EPISODES, PEOPLE})
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
        status.setText(count == 0 ? "Nothing in your library matches “" + lastQuery + "”"
                                  : count + (count == 1 ? " result" : " results") + " for “" + lastQuery +
                                        "” · in your library");
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
                filters.setVisibility(items.isEmpty() ? GONE : VISIBLE);
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

    private void bindPill(TextView pill, Video video) {
        int percent = video == null || video.getDurationMs() <= 0 ? 0
                                                                  : Math.round(100f * video.getResumeMs() /
                                                                               video.getDurationMs());
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setCornerRadius(dp(12));
        if (video != null && video.isWatched()) {
            pill.setText("✓ Watched");
            pill.setTextColor(SupernovaStyle.FOCUS_TEXT);
            shape.setColor(SupernovaStyle.FOCUS_FILL);
        } else if (percent > 0 && percent < 100) {
            pill.setText(percent + "% watched");
            pill.setTextColor(SupernovaStyle.TEXT);
            shape.setColor(SupernovaStyle.PANEL_REST);
            shape.setStroke(Math.max(1, dp(1)), SupernovaStyle.GLASS_EDGE);
        } else {
            pill.setVisibility(GONE);
            return;
        }
        pill.setBackground(shape);
        pill.setVisibility(VISIBLE);
    }

    /** The group headings ("Films · 2") drawn above the first row of each group, without becoming list items. */
    private final class GroupHeadings extends RecyclerView.ItemDecoration {
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        GroupHeadings() {
            paint.setColor(SupernovaStyle.TEXT_SECONDARY);
            paint.setTextSize(13 * activity.getResources().getDisplayMetrics().scaledDensity);
        }

        private String heading(int position) {
            List<Result> shown = visible();
            if (position < 0 || position >= shown.size())
                return null;
            String group = shown.get(position).group();
            if (position > 0 && group.equals(shown.get(position - 1).group()))
                return null;
            return group + " · " + groupCount(group);
        }

        @Override
        public void getItemOffsets(android.graphics.Rect out, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            out.set(0, heading(parent.getChildAdapterPosition(view)) == null ? 0 : dp(30), 0, 0);
        }

        @Override
        public void onDraw(android.graphics.Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                String text = heading(parent.getChildAdapterPosition(child));
                if (text != null)
                    canvas.drawText(text, child.getLeft() + dp(4), child.getTop() - dp(10), paint);
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
            android.graphics.drawable.StateListDrawable states = new android.graphics.drawable.StateListDrawable();
            android.graphics.drawable.GradientDrawable focused = new android.graphics.drawable.GradientDrawable();
            focused.setColor(0x14FFFFFF);
            focused.setCornerRadius(dp(10));
            focused.setStroke(dp(2), Color.WHITE);
            states.addState(new int[] {android.R.attr.state_focused}, focused);
            states.addState(new int[] {}, new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            row.setBackground(states);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp(60));
            lp.bottomMargin = dp(4);
            row.setLayoutParams(lp);
            return new Holder(row);
        }

        public void onBindViewHolder(Holder h, int position) {
            Result r = visible().get(position);
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
                h.metadata.setText(details("TV show", 0, seasons, ""));
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
                h.metadata.setText(details("Film", minutes, PreviewVariants.label(video), r.genres));
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
