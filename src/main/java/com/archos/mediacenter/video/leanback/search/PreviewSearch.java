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
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediacenter.video.utils.PlayUtils;
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
        /** A 16:9 backdrop for the thumbnail (SEARCH.md), when the library has one. */
        Uri backdrop;
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

        /** The streaming group's "Looking on your services" row while it loads (SEARCH.md `tv/04`). */
        boolean loading;

        long id() {
            if (loading)
                return -1_999_999_999L;
            if (stream != null)
                return -2_000_000_000L - stream.id;
            return show == null ? video.getId() : -show.getTvshowId() - 2;
        }

        /** Films · TV shows · Episodes · People (matched by cast, crew or studio rather than the title). */
        String group() {
            if (stream != null || loading)
                return STREAM;
            if (people)
                return PEOPLE;
            if (show != null)
                return SHOWS;
            return video instanceof Episode ? EPISODES : FILMS;
        }
    }

    private ScrollView guidancePanel;
    /** Before typing: Genres and People in your library (SEARCH.md; `search-v6/tv/09`, `10`). */
    private TextView genreHeading;
    private LinearLayout genreGrid;
    private TextView peopleHeading;
    private LinearLayout peopleRow;
    private SearchLanding landing;
    /** Nothing in the library matches (`search-v6/tv/06`): centred on the right. */
    private LinearLayout nothingPanel;
    private TextView nothingTitle;
    private TextView nothingLines;
    private LinearLayout filters;
    /** The search field's box and icon, for Voice's "Listening…" (`search-v6/tv/07`). */
    private LinearLayout searchBox;
    private ImageView fieldIcon;
    private android.speech.SpeechRecognizer recognizer;
    private boolean listening;
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
    /** Recent searches on the landing: the last three (owner, 25 Nov; search-v6/tv/09). */
    static final int RECENT_SHOWN = 3;
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
        searchBox = search;
        fieldIcon = icon;
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
        // More than fits: arrows in the margin, never a scroll bar (RULES §16).
        guidancePanel.setVerticalScrollBarEnabled(false);
        guidancePanel.setClipChildren(false);
        guidancePanel.addView(guidance());
        resultArea.addView(guidancePanel, new FrameLayout.LayoutParams(-1, -1));
        resultArea.addView(nothing(), new FrameLayout.LayoutParams(-1, -1));
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
     * Before typing (SEARCH.md, `search-v6/tv/09` with history, `tv/10` without): Recent searches (clock icon), the
     * last three as rows with a magnifying glass, Clear history at the end of the heading; Genres in your library (tag
     * icon), 16:9 cards four across; People in your library (person icon), six cast cards. Headings with icons, about
     * 26 px below and 64 px above (RULES §5).
     */
    private View guidance() {
        LinearLayout column = new LinearLayout(activity);
        column.setOrientation(VERTICAL);
        column.setClipChildren(false);
        column.setClipToPadding(false);
        column.setPadding(dp(8), dp(4), dp(8), dp(16));
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
        genreHeading = heading("Genres in your library", "tag");
        column.addView(genreHeading, new LayoutParams(-1, -2));
        genreGrid = new LinearLayout(activity);
        genreGrid.setOrientation(VERTICAL);
        genreGrid.setClipChildren(false);
        genreGrid.setClipToPadding(false);
        column.addView(genreGrid, new LayoutParams(-1, -2));
        peopleHeading = heading("People in your library", "people");
        column.addView(peopleHeading, new LayoutParams(-1, -2));
        peopleRow = new LinearLayout(activity);
        peopleRow.setClipChildren(false);
        peopleRow.setClipToPadding(false);
        column.addView(peopleRow, new LayoutParams(-1, -2));
        genreHeading.setVisibility(GONE);
        peopleHeading.setVisibility(GONE);
        bindRecent();
        Context app = activity.getApplicationContext();
        worker.execute(() -> {
            SearchLanding found = SearchLanding.load(app);
            main.post(() -> {
                if (landing != null)
                    return;
                landing = found;
                bindLanding();
            });
        });
        return column;
    }

    /** One row of genres with recent searches, two without (SEARCH.md); then the six people. */
    void bindLanding() {
        if (landing == null || genreGrid == null)
            return;
        boolean recent = recentChips.getVisibility() == VISIBLE;
        int shown = Math.min(landing.genres.size(), SearchLanding.GENRES_ACROSS * (recent ? 1 : 2));
        genreGrid.removeAllViews();
        LinearLayout row = null;
        for (int i = 0; i < shown; i++) {
            if (i % SearchLanding.GENRES_ACROSS == 0) {
                row = new LinearLayout(activity);
                row.setClipChildren(false);
                row.setClipToPadding(false);
                LayoutParams gap = new LayoutParams(-1, -2);
                gap.bottomMargin = i + SearchLanding.GENRES_ACROSS < shown ? dp(12) : 0;
                genreGrid.addView(row, gap);
            }
            row.addView(genreCard(landing.genres.get(i)), cardGap(dp(107), dp(60)));
        }
        genreHeading.setVisibility(shown > 0 ? VISIBLE : GONE);
        spaceAbove(genreHeading, recent);
        peopleRow.removeAllViews();
        for (SearchLanding.Card person : landing.people)
            peopleRow.addView(personCard(person), cardGap(dp(56), -2));
        peopleHeading.setVisibility(landing.people.isEmpty() ? GONE : VISIBLE);
        spaceAbove(peopleHeading, recent || shown > 0);
    }

    /** A later section's heading sits 64 px below the section above (RULES §5); the first has no gap. */
    private void spaceAbove(View heading, boolean sectionAbove) {
        LayoutParams where = (LayoutParams)heading.getLayoutParams();
        where.topMargin = sectionAbove ? dp(32) : 0;
        where.bottomMargin = dp(7);
        heading.setLayoutParams(where);
    }

    private LayoutParams cardGap(int width, int height) {
        LayoutParams gap = new LayoutParams(width, height);
        gap.rightMargin = dp(12);
        return gap;
    }

    /**
     * A genre card (`search-v6/tv/09`): 214 × 120 at 1080p, a backdrop from a library title of that genre with the
     * artwork edge, the genre and "42 titles" over the foot on a dark fade. Focus grows it with the 4 px ring.
     */
    private View genreCard(SearchLanding.Card genre) {
        FrameLayout card = artworkCard(dp(8));
        card.setTag("search.genre." + genre.name);
        card.setContentDescription(genre.name + ", " + genre.count());
        ImageView picture = (ImageView)card.getChildAt(0);
        if (!genre.artwork.isEmpty()) {
            Uri art = Uri.parse(genre.artwork);
            ArtworkRequest.load(picture, art, 0, "search.landing", "backdrop",
                                Picasso.get().load(art).resize(dp(214), dp(120)).centerCrop().noFade());
        }
        View fade = new View(activity);
        android.graphics.drawable.GradientDrawable shade = new android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP, new int[] {0xD9000000, 0x00000000});
        fade.setBackground(shade);
        card.addView(fade, new FrameLayout.LayoutParams(-1, dp(40), Gravity.BOTTOM));
        LinearLayout words = new LinearLayout(activity);
        words.setOrientation(VERTICAL);
        words.setPadding(dp(8), 0, dp(6), dp(5));
        TextView name = text(genre.name, 13);
        name.setTextColor(SupernovaStyle.TEXT);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        words.addView(name);
        TextView count = text(genre.count(), 10);
        count.setTextColor(0xFFDCE3EE);
        words.addView(count);
        card.addView(words, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));
        card.setOnClickListener(v -> fill(genre.name));
        return card;
    }

    /** A person card as Details' cast cards (2:3, 112 × 168 at 1080p, initials when no photo), name and count under. */
    private View personCard(SearchLanding.Card person) {
        LinearLayout column = new LinearLayout(activity);
        column.setOrientation(VERTICAL);
        column.setClipChildren(false);
        column.setClipToPadding(false);
        FrameLayout card = artworkCard(dp(6));
        card.setTag("search.person." + person.name);
        card.setContentDescription(person.name + ", " + person.count());
        ImageView picture = (ImageView)card.getChildAt(0);
        picture.setImageDrawable(new com.archos.mediacenter.video.leanback.details.PersonInitials(person.name));
        if (!person.artwork.isEmpty()) {
            Uri art = Uri.parse(person.artwork);
            ArtworkRequest.load(picture, art, 0, "search.landing", "portrait",
                                Picasso.get().load(art).resize(dp(112), dp(168)).centerCrop().noFade()
                                    .placeholder(new com.archos.mediacenter.video.leanback.details.PersonInitials(
                                        person.name)));
        }
        card.setOnClickListener(v -> fill(person.name));
        column.addView(card, new LayoutParams(dp(56), dp(84)));
        TextView name = text(person.name, 11);
        name.setTextColor(SupernovaStyle.TEXT);
        name.setPadding(0, dp(6), 0, 0);
        column.addView(name);
        TextView count = text(person.count(), 10);
        count.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        column.addView(count);
        return column;
    }

    /** Artwork that is a card: the edge always, focus grows it with the solid ring, never a box (RULES §14). */
    private FrameLayout artworkCard(int radius) {
        FrameLayout card = new FrameLayout(activity);
        card.setFocusable(true);
        card.setFocusableInTouchMode(true);
        card.setClickable(true);
        android.graphics.drawable.GradientDrawable frame = new android.graphics.drawable.GradientDrawable();
        frame.setColor(0xFF22262D);
        frame.setCornerRadius(radius);
        card.setBackground(frame);
        card.setClipToOutline(true);
        ImageView picture = new ImageView(activity);
        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.addView(picture, new FrameLayout.LayoutParams(-1, -1));
        float radiusDp = radius / activity.getResources().getDisplayMetrics().density;
        card.setForeground(new ArtworkEdge(activity, radiusDp, false));
        card.setOnFocusChangeListener((v, focused) -> {
            card.setForeground(new ArtworkEdge(activity, radiusDp, focused));
            float scale = focused ? 1.08f : 1f;
            card.animate().scaleX(scale).scaleY(scale).setDuration(140).start();
        });
        return card;
    }

    /** OK on a recent search, a genre or a person: fills the field as if typed (`tv/11`). */
    private void fill(String words) {
        query.setText(words);
        query.setSelection(query.length());
    }

    /** The first thing on the right before typing: a recent search, else the first genre or person card. */
    private View firstLandingItem() {
        if (recentChips.getVisibility() == VISIBLE && recentChips.getChildCount() > 0)
            return recentChips.getChildAt(0);
        if (genreGrid.getChildCount() > 0)
            return ((ViewGroup)genreGrid.getChildAt(0)).getChildAt(0);
        if (peopleRow.getChildCount() > 0)
            return ((ViewGroup)peopleRow.getChildAt(0)).getChildAt(0);
        return null;
    }

    /**
     * Nothing in the library matches (`search-v6/tv/06`): centred on the right, an icon, "Nothing in your library
     * matches “terminus”", then two soft lines (Where to Watch off: the first only). Focus stays on the keyboard.
     */
    private View nothing() {
        nothingPanel = new LinearLayout(activity);
        nothingPanel.setOrientation(VERTICAL);
        nothingPanel.setGravity(Gravity.CENTER);
        nothingPanel.setTag("search.nothing");
        nothingPanel.setFocusable(false);
        ImageView icon = new ImageView(activity);
        icon.setImageDrawable(new PreviewIcon("search"));
        icon.setAlpha(.7f);
        nothingPanel.addView(icon, new LayoutParams(dp(28), dp(28)));
        nothingTitle = text("", 17);
        nothingTitle.setTextColor(SupernovaStyle.TEXT);
        nothingTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        nothingTitle.setGravity(Gravity.CENTER);
        nothingTitle.setSingleLine(false);
        nothingTitle.setMaxLines(2);
        nothingTitle.setPadding(dp(24), dp(14), dp(24), dp(8));
        nothingPanel.addView(nothingTitle, new LayoutParams(-1, -2));
        nothingLines = text("", 14);
        nothingLines.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        nothingLines.setGravity(Gravity.CENTER);
        nothingLines.setSingleLine(false);
        nothingLines.setLineSpacing(dp(3), 1f);
        nothingPanel.addView(nothingLines, new LayoutParams(-1, -2));
        nothingPanel.setVisibility(GONE);
        return nothingPanel;
    }

    /** The words of the nothing-found panel (SEARCH.md). */
    static String[] nothingWords(String typed, boolean streaming) {
        String title = "Nothing in your library matches \u201c" + typed + "\u201d";
        String first = "Search looks only in your library.";
        String lines = streaming ? first + "\nTitles you can stream show up under what it finds." : first;
        return new String[] {title, lines};
    }

    /**
     * What you can search (SEARCH.md, owner 25 Nov): under the keyboard at all times, results or not; plain tips, no
     * lines, not focusable.
     */
    private View tips() {
        LinearLayout tips = new LinearLayout(activity);
        tips.setOrientation(VERTICAL);
        tips.setTag("search.tips");
        tips.setFocusable(false);
        tips.setPadding(0, dp(24), 0, 0);
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
        return tips;
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
        bindLanding();
        keyboard.focusLastKey();
        UndoNotice.show(activity, "Search history cleared", () -> {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit()
                .putString(RECENT_KEY, saved).apply();
            bindRecent();
            bindLanding();
        });
    }

    /** Recent searches as rows with a magnifying glass; faint lines between rows only (RULES §5). */
    private void bindRecent() {
        recentChips.removeAllViews();
        List<String> values = recent(activity);
        recentLine.setVisibility(values.isEmpty() ? GONE : VISIBLE);
        recentChips.setVisibility(values.isEmpty() ? GONE : VISIBLE);
        for (String value : values) {
            if (recentChips.getChildCount() >= RECENT_SHOWN * 2 - 1)
                break; // the last three (SEARCH.md), each row with the line before it
            if (recentChips.getChildCount() > 0) {
                View rule = new View(activity);
                rule.setBackgroundColor(0x1FFFFFFF);
                recentChips.addView(rule, new LayoutParams(-1, Math.max(1, dp(1) / 2)));
            }
            TextView row = TvButton.create(activity, value, () -> fill(value), false);
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
        status.setText("");
        boolean empty = count == 0;
        if (empty) {
            String[] words = nothingWords(lastQuery,
                                          com.archos.mediacenter.video.streaming.StreamingRepository.enabled(activity));
            nothingTitle.setText(words[0]);
            nothingLines.setText(words[1]);
        }
        nothingPanel.setVisibility(empty ? VISIBLE : GONE);
    }

    private void buildKeyboard(LinearLayout column) {
        keyboard = new PreviewKeyboard(activity, query, this::focusRight);
        column.addView(keyboard, new LayoutParams(-1, -2));
        column.addView(tips(), new LayoutParams(-1, -2));
        keyboard.setActions(this::focusRight, () -> activity.onBackPressed());
        // Search keeps the keyboard up while browsing results, so there is no Done · Cancel row.
        keyboard.setDoneRowVisible(false);
        if (activity instanceof VideoSearchActivity)
            keyboard.setVoice(((VideoSearchActivity)activity)::startVoice);
    }

    /**
     * Right from the keyboard's last column: the first item on the right, a recent search, a genre card or a result
     * (SEARCH.md). Nothing found: focus stays on the keyboard.
     */
    private void focusRight() {
        if (!items.isEmpty()) {
            results.scrollToPosition(0);
            results.post(() -> {
                RecyclerView.ViewHolder first = results.findViewHolderForAdapterPosition(0);
                if (first != null)
                    first.itemView.requestFocus();
            });
            return;
        }
        if (guidancePanel.getVisibility() == VISIBLE) {
            View first = firstLandingItem();
            if (first != null)
                first.requestFocus();
        }
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

    static final int VOICE_PERMISSION = 4107;

    /**
     * Voice (`search-v6/tv/07`): the field turns into "Listening…" with the microphone and a soft ring; the words fill
     * the field as they are heard and the results follow; the right keeps what it showed until then. Returns false
     * when this device has no speech service, so the caller can fall back to Android's own voice screen.
     */
    public boolean listen() {
        if (!android.speech.SpeechRecognizer.isRecognitionAvailable(activity))
            return false;
        if (androidx.core.content.ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(
                activity, new String[] {android.Manifest.permission.RECORD_AUDIO}, VOICE_PERMISSION);
            return true;
        }
        if (recognizer == null) {
            recognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(activity);
            recognizer.setRecognitionListener(new Heard());
        }
        android.content.Intent speak = new android.content.Intent(
            android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speak.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                       android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speak.putExtra(android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        showListening(true);
        try {
            recognizer.startListening(speak);
        } catch (RuntimeException unavailable) {
            Diagnostics.error("search_voice_unavailable", unavailable);
            showListening(false);
            return false;
        }
        return true;
    }

    /** "Listening…" in the field, the microphone instead of the magnifying glass, and a soft ring round the box. */
    void showListening(boolean on) {
        listening = on;
        android.graphics.drawable.GradientDrawable field = new android.graphics.drawable.GradientDrawable();
        field.setColor(SupernovaStyle.PANEL_REST);
        field.setCornerRadius(dp(12));
        if (on) {
            field.setStroke(dp(2), 0x99FFFFFF);
            query.setText("");
        }
        searchBox.setBackground(field);
        query.setHint(on ? "Listening…" : "Movies, TV shows, people…");
        if (on)
            fieldIcon.setImageDrawable(new PreviewIcon("mic"));
        else
            fieldIcon.setImageResource(R.drawable.preview_search);
        searchBox.setTag(on ? "search.field.listening" : null);
    }

    boolean isListening() {
        return listening;
    }

    /** The words as they are heard fill the field; the results follow as if typed. */
    private final class Heard implements android.speech.RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
        }

        public void onBeginningOfSpeech() {
        }

        public void onRmsChanged(float rms) {
        }

        public void onBufferReceived(byte[] buffer) {
        }

        public void onEndOfSpeech() {
        }

        public void onError(int error) {
            Diagnostics.event("search_voice_ended", "reason", "error_" + error);
            showListening(false);
        }

        public void onResults(Bundle results) {
            heard(results);
            showListening(false);
        }

        public void onPartialResults(Bundle partial) {
            heard(partial);
        }

        public void onEvent(int type, Bundle params) {
        }

        private void heard(Bundle bundle) {
            List<String> words = bundle == null ? null
                : bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            if (words == null || words.isEmpty() || words.get(0).trim().isEmpty())
                return;
            query.setText(words.get(0));
            query.setSelection(query.length());
        }
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
            bindLanding();
            guidancePanel.setVisibility(VISIBLE);
            nothingPanel.setVisibility(GONE);
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
        String[] art = {VideoStore.Video.VideoColumns.SCRAPER_BACKDROP_URL,
                        VideoStore.Video.VideoColumns.SCRAPER_S_BACKDROP_URL};
        String[] projection = Arrays.copyOf(loader.getProjection(),
                                            loader.getProjection().length + extra.length + art.length);
        System.arraycopy(extra, 0, projection, loader.getProjection().length, extra.length);
        System.arraycopy(art, 0, projection, loader.getProjection().length + extra.length, art.length);
        String selection = loader.getSelection(), sort = loader.getSortOrder();
        String[] args = loader.getSelectionArgs();
        CancellationSignal signal = new CancellationSignal();
        cancellation = signal;
        status.setText("");
        Context app = activity.getApplicationContext();
        AllTvshowsLoader showLoader = new AllTvshowsLoader(activity);
        Uri showUri = showLoader.getUri();
        String[] showProjection = showLoader.getProjection(), showArgs = showLoader.getSelectionArgs();
        String showSelection = showLoader.getSelection(), showSort = showLoader.getSortOrder();
        worker.execute(() -> {
            Map<Long, String[]> movieNames = metadata(app, false, signal), showNames = metadata(app, true, signal);
            List<Result> found = new ArrayList<>();
            Map<Long, Uri> showArt = new HashMap<>();
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
                        long showOf = longCell(cursor, VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);
                        String showBackdrop = cell(cursor, VideoStore.Video.VideoColumns.SCRAPER_S_BACKDROP_URL);
                        if (showOf > 0 && !showBackdrop.isEmpty())
                            showArt.putIfAbsent(showOf, Uri.parse(showBackdrop));
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
                        String backdrop = cell(cursor, VideoStore.Video.VideoColumns.SCRAPER_BACKDROP_URL);
                        result.backdrop = backdrop.isEmpty() ? null : Uri.parse(backdrop);
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
                                result.backdrop = showArt.get(show.getTvshowId());
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
                present(found, value, message, request);
            });
        });
    }

    /** Shows what a search found (main thread): results, the streaming group, or the nothing-found panel. */
    void present(List<Result> found, String value, String message, int request) {
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
    }

    int generation() {
        return generation;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_DOWN &&
            (e.getUnicodeChar() != 0 || e.getKeyCode() == KeyEvent.KEYCODE_DEL ||
             e.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL) &&
            query.onKeyDown(e.getKeyCode(), e))
            return true;
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_MENU && results.hasFocus()) {
            View focus = results.findFocus();
            View row = focus == null ? null : results.findContainingItemView(focus);
            if (row != null && row.performLongClick())
                return true;
        }
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
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
        }
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

    /**
     * A result row (SEARCH.md): about 96 px, a 16:9 thumbnail 128 × 72 with the artwork edge and the progress badge
     * scaled to 30 px (RULES §14c, owner 25 Nov A), the name (32 px) and second line (24 px), a pill in a fixed column
     * only where it adds words ("Resume S2 E3").
     */
    private class Holder extends RecyclerView.ViewHolder {
        FrameLayout thumb;
        ImageView image;
        TextView title;
        TextView metadata;
        TextView pill;
        View ring;

        Holder(LinearLayout row) {
            super(row);
            thumb = new FrameLayout(activity);
            android.graphics.drawable.GradientDrawable frame = new android.graphics.drawable.GradientDrawable();
            frame.setColor(0xFF22262D);
            frame.setCornerRadius(dp(4));
            thumb.setBackground(frame);
            thumb.setClipToOutline(true);
            thumb.setForeground(new ArtworkEdge(activity, 4, false));
            image = new ImageView(activity);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumb.addView(image, new FrameLayout.LayoutParams(-1, -1));
            row.addView(thumb, new LayoutParams(dp(64), dp(36)));
            ring = new View(activity);
            ring.setVisibility(GONE);
            LayoutParams ringSize = new LayoutParams(dp(14), dp(14));
            ringSize.leftMargin = dp(4);
            row.addView(ring, ringSize);
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

        /** Back to a plain result row (a recycled loading row hides its ring and shows its thumbnail). */
        void reset() {
            itemView.setAlpha(1f);
            itemView.setFocusable(true);
            thumb.setVisibility(VISIBLE);
            ring.setVisibility(GONE);
            ring.setBackground(null);
            ProgressBadge.apply(thumb, 0f, false, ProgressBadge.SMALL_PX_1080);
            itemView.setOnLongClickListener(null);
        }
    }

    /**
     * Long-press / Menu on a library result: the library long-press menu beside the row (LIBRARY.md; `tv/03`), the
     * same rows and order as on a library page. A change re-runs the search so the row shows it.
     */
    private void menu(Result r, View anchor) {
        LibraryItemMenu.Spec spec = new LibraryItemMenu.Spec();
        Object media = r.show != null ? r.show : r.video;
        spec.title = r.show != null ? r.show.getName() : r.video.getName();
        spec.show = r.show != null;
        Runnable changed = () -> schedule(query.getText().toString());
        Video play = r.video;
        Video first = null;
        if (r.show != null) {
            List<PreviewLibraryLoader.Entry> episodes = SearchShowEpisodes.of(activity, r.show.getTvshowId());
            PreviewSeriesJourney.Selection next = PreviewSeriesJourney.select(activity, episodes);
            play = next.episode == null ? null : (Video)next.episode.media;
            spec.started = next.started && play != null;
            spec.playLabel = PreviewSeriesJourney.playLabel(next);
            spec.watched = r.show.getEpisodeCount() > 0 && r.show.isWatched();
            first = SearchShowEpisodes.first(episodes);
        } else {
            spec.watched = r.video.isWatched();
            spec.started = r.video.getResumeMs() > 0 && !spec.watched;
            long left = r.video.getDurationMs() - r.video.getResumeMs();
            spec.playLabel = !spec.started ? "Play" : left > 0 ? "Resume · " + length(left) + " left" : "Resume";
            first = r.video;
        }
        Video target = play;
        Video start = first;
        if (target != null)
            spec.play = () -> PlayUtils.startVideo(activity, target, PlayerActivity.RESUME_FROM_LAST_POS, false, -1,
                                                   null, -1);
        if (start != null)
            spec.playFromStart = () -> PlayUtils.startVideo(activity, start, PlayerActivity.RESUME_NO, false, -1,
                                                            null, -1);
        boolean watched = spec.watched;
        spec.markWatched = () -> {
            LibraryActions.markWatched(activity, media, !watched);
            changed.run();
        };
        Video matchable = r.video != null ? r.video : start;
        if (matchable != null)
            spec.fixMatch = () -> {
                android.content.Intent fix = new android.content.Intent(
                    activity, com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity.class);
                fix.putExtra(com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity.EXTRA_VIDEO,
                             matchable);
                activity.startActivity(fix);
            };
        spec.remove = () -> {
            LibraryActions.removeFromLibrary(activity, media, true);
            changed.run();
            UndoNotice.show(activity, spec.title + " left the library", () -> {
                LibraryActions.removeFromLibrary(activity, media, false);
                changed.run();
            });
        };
        spec.delete = () -> LibraryActions.confirmDelete(activity, media, spec.title, changed);
        LibraryItemMenu.show(activity, anchor, spec);
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

    /** The second line (SEARCH.md): "2019 · TV show · 4 seasons", "2025 · Movie · 1 h 52 min"; only what is known. */
    static String secondLine(int year, String kind, String extra) {
        List<String> parts = new ArrayList<>();
        if (year > 0)
            parts.add(String.valueOf(year));
        parts.add(kind);
        if (extra != null && !extra.isEmpty())
            parts.add(extra);
        return TextUtils.join(" · ", parts);
    }

    /** "52 min", "1 h 52 min", "2 h" (RULES §6, as the library pages). */
    static String length(long ms) {
        if (ms <= 0)
            return "";
        long minutes = Math.max(1, ms / 60000);
        if (minutes < 60)
            return minutes + " min";
        return minutes % 60 == 0 ? minutes / 60 + " h" : minutes / 60 + " h " + minutes % 60 + " min";
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
        h.title.setText(title.name);
        h.metadata.setText(secondLine(title.year, title.kind, title.relation));
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
            Uri art = Uri.parse("https://image.tmdb.org/t/p/w300" + title.poster);
            ArtworkRequest.load(h.image, art, title.id, "search.results", "backdrop",
                                Picasso.get().load(art).resize(dp(128), dp(72)).centerCrop().noFade());
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

    /**
     * Still fetching (`search-v6/tv/04`): under the streaming heading one row with the small loading ring (RULES §21)
     * and "Looking on your services"; not focusable.
     */
    private void bindLoading(Holder h) {
        Diagnostics.semantic(h.itemView, "search.result.loading");
        h.itemView.setFocusable(false);
        h.itemView.setOnClickListener(null);
        h.itemView.setOnFocusChangeListener(null);
        h.thumb.setVisibility(GONE);
        com.archos.mediacenter.video.foundation.DoubleRingDrawable spinner =
            new com.archos.mediacenter.video.foundation.DoubleRingDrawable();
        h.ring.setBackground(spinner);
        h.ring.setVisibility(VISIBLE);
        spinner.start();
        h.title.setText("Looking on your services");
        h.title.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        h.metadata.setText("");
        h.pill.setVisibility(GONE);
    }

    /**
     * After the library results: the film's streaming group, if its story has titles on your services. While it is
     * fetched, the heading and a "Looking on your services" row show; the found titles replace the row, or the heading
     * and row fade out together when none are found (nothing below them moves: they are last).
     */
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
        if (source == null) {
            loadPersonStreaming(found, owned, request);
            return;
        }
        Movie film = source;
        boolean nonFiction = PreviewGenres.parse(genresOf(found, film)).stream()
                                 .anyMatch(genre -> genre.equalsIgnoreCase("Documentary"));
        Result waiting = new Result((SearchStreaming.Title)null, SearchStreaming.heading(film.getName(), nonFiction));
        waiting.loading = true;
        items.add(waiting);
        Context app = activity.getApplicationContext();
        worker.execute(() -> {
            SearchStreaming.Group group = SearchStreaming.worldOf(app, film.getOnlineId(), film.getName(),
                                                                  film.getYear(), owned, () -> request != generation);
            main.post(() -> {
                if (request != generation || !isAttachedToWindow())
                    return;
                if (group == null) {
                    fadeOutLoading();
                    return;
                }
                items.removeIf(item -> item.stream != null || item.loading);
                for (SearchStreaming.Title title : group.titles)
                    items.add(new Result(title, group.heading));
                adapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * Only people matched (SEARCH.md): "Stream more from Mara Ellison", the person's other work on your services,
     * found through the first matching library film's credits; the same loading row and fade as a film's group.
     */
    private void loadPersonStreaming(List<Result> found, java.util.Set<Long> owned, int request) {
        Movie film = null;
        String person = "";
        for (Result result : found)
            if (result.people && result.video instanceof Movie && ((Movie)result.video).getOnlineId() > 0
                && !result.reason.isEmpty()) {
                film = (Movie)result.video;
                person = result.reason.split(" · ", 2)[0];
                break;
            }
        if (film == null)
            return;
        Result waiting = new Result((SearchStreaming.Title)null, SearchStreaming.personHeading(person));
        waiting.loading = true;
        items.add(waiting);
        Context app = activity.getApplicationContext();
        long id = film.getOnlineId();
        String name = person;
        worker.execute(() -> {
            SearchStreaming.Group group = SearchStreaming.fromPerson(app, id, name, owned,
                                                                     () -> request != generation);
            main.post(() -> {
                if (request != generation || !isAttachedToWindow())
                    return;
                if (group == null) {
                    fadeOutLoading();
                    return;
                }
                items.removeIf(item -> item.stream != null || item.loading);
                for (SearchStreaming.Title title : group.titles)
                    items.add(new Result(title, group.heading));
                adapter.notifyDataSetChanged();
            });
        });
    }

    private static String genresOf(List<Result> found, Movie film) {
        for (Result result : found)
            if (result.video == film)
                return result.genres;
        return "";
    }

    /** No titles to stream: the heading and the loading line fade out together, then leave. */
    private void fadeOutLoading() {
        int at = -1;
        List<Result> shown = visible();
        for (int i = 0; i < shown.size(); i++)
            if (shown.get(i).loading)
                at = i;
        RecyclerView.ViewHolder row = at < 0 ? null : results.findViewHolderForAdapterPosition(at);
        Runnable gone = () -> {
            items.removeIf(item -> item.loading);
            adapter.notifyDataSetChanged();
        };
        if (row == null) {
            gone.run();
            return;
        }
        row.itemView.animate().alpha(0f).setDuration(300).setUpdateListener(step -> results.invalidate())
            .withEndAction(gone).start();
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

    /**
     * Progress (owner, 25 Nov, A): the corner badge on the thumbnail; a pill only where it adds words, "Resume S2 E3"
     * (▶) or "Resume" for a film. No Watched, Unwatched or In library pills.
     */
    private void bindPill(TextView pill, Video video) {
        String words = pillWords(video);
        if (words.isEmpty()) {
            pill.setVisibility(GONE);
            return;
        }
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setCornerRadius(dp(12));
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(Math.max(1, dp(1)), 0x80FFFFFF);
        pill.setText(words);
        pill.setCompoundDrawables(null, null, null, null);
        pill.setTextColor(SupernovaStyle.TEXT);
        pill.setBackground(shape);
        pill.setVisibility(VISIBLE);
    }

    static String pillWords(Video video) {
        if (video == null || video.isWatched() || video.getResumeMs() <= 0)
            return "";
        if (video instanceof Episode)
            return "▶ Resume S" + ((Episode)video).getSeasonNumber() + " E" + ((Episode)video).getEpisodeNumber();
        return "▶ Resume";
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
            if (here.stream != null || here.loading)
                return position == 0 || shown.get(position - 1).stream == null && !shown.get(position - 1).loading ||
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
                    int alpha = Math.round(255 * child.getAlpha());
                    paint.setAlpha(alpha);
                    PreviewIcon books = new PreviewIcon(text.startsWith("Stream") ? "cloud" : "library");
                    int size = dp(16);
                    int top = child.getTop() - dp(13) - size;
                    books.setBounds(child.getLeft() + dp(4), top, child.getLeft() + dp(4) + size, top + size);
                    books.setAlpha(alpha);
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
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp(48));
            lp.bottomMargin = dp(2);
            row.setLayoutParams(lp);
            return new Holder(row);
        }

        public void onBindViewHolder(Holder h, int position) {
            Result r = visible().get(position);
            h.reset();
            h.title.setTextColor(SupernovaStyle.TEXT);
            if (r.loading) {
                bindLoading(h);
                return;
            }
            if (r.stream != null) {
                bindStream(h, r.stream);
                return;
            }
            Diagnostics.semantic(h.itemView, "search.result." + (r.show == null ? "video." : "series.") +
                                                 (r.show == null ? r.video.getId() : r.show.getTvshowId()));
            String typed = query.getText().toString();
            ArtworkRequest.cancel(h.image);
            h.image.setImageDrawable(null);
            h.itemView.setOnLongClickListener(v -> {
                menu(r, v);
                return true;
            });
            if (r.show != null) {
                Tvshow show = r.show;
                h.title.setText(highlighted(show.getName(), typed));
                String seasons = show.getSeasonCount() > 0
                                     ? show.getSeasonCount() + (show.getSeasonCount() == 1 ? " season" : " seasons")
                                     : "";
                String about = secondLine(show.getYear(), "TV show", seasons);
                h.metadata.setText(r.reason.isEmpty() ? about : r.reason + " · " + about);
                h.pill.setVisibility(GONE);
                ProgressBadge.apply(h.thumb, show.getEpisodeCount() > 0
                                                 ? (float)show.getEpisodeWatchedCount() / show.getEpisodeCount()
                                                 : 0f,
                                    show.getEpisodeCount() > 0 && show.isWatched(), ProgressBadge.SMALL_PX_1080);
                Uri art = r.backdrop != null ? r.backdrop : show.getPosterUri();
                if (art != null)
                    ArtworkRequest.load(
                        h.image, art, show.getTvshowId(), "search.results", "backdrop",
                        Picasso.get().load(art).resize(dp(128), dp(72)).centerCrop().noFade());
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
            long length = video.getDurationMs() > 0 ? video.getDurationMs() : 0;
            if (video instanceof Episode) {
                Episode e = (Episode)video;
                String name = TextUtils.isEmpty(e.getEpisodeName()) ? "Episode " + e.getEpisodeNumber()
                                                                    : e.getEpisodeName();
                h.title.setText(highlighted(e.getShowName() + " · " + name, typed));
                h.metadata.setText(secondLine(0, "Episode · S" + e.getSeasonNumber() + " E" + e.getEpisodeNumber(),
                                              length(length)));
            } else {
                int year = video instanceof Movie ? ((Movie)video).getYear() : 0;
                h.title.setText(highlighted(video.getName(), typed));
                String about = secondLine(year, "Movie", length(length));
                h.metadata.setText(r.reason.isEmpty() ? about : r.reason + " · " + about);
            }
            bindPill(h.pill, video);
            boolean watched = video.isWatched();
            ProgressBadge.apply(h.thumb, watched ? 1f : ProgressBadge.fraction(video.getResumeMs(), length), watched,
                                ProgressBadge.SMALL_PX_1080);
            Uri art = video instanceof Episode ? ((Episode)video).getPictureUri()
                      : r.backdrop != null ? r.backdrop : video.getPosterUri();
            if (art != null)
                ArtworkRequest.load(h.image, art, video.getId(), "search.results",
                                    video instanceof Episode ? "episode_still" : "backdrop",
                                    Picasso.get().load(art).resize(dp(128), dp(72)).centerCrop().noFade());
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
