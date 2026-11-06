package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import com.archos.mediascraper.*;
import java.util.*;
import java.util.function.Consumer;

/** Shared Find a Match presentation; native search and accepted-save handlers stay in the fragment. */
public final class PreviewMatchSearch extends LinearLayout {
    /** Side panels: #16181C at 90% (global look, owner 5 Nov 2026). */
    static final int PANEL = 0xE616181C;
    static final int PANEL_WIDTH_DP = 440;
    private final EditText input;
    private final PreviewKeyboard keyboard;
    private final LinearLayout rows;
    private final TextView status;
    private final TextView title;
    private final ScrollView scroll;
    private long currentOnlineId = -1;
    private final List<BaseTags> rendered = new ArrayList<>();
    private final Consumer<BaseTags> choose;
    private final Consumer<String> search;
    private final Runnable query;

    public PreviewMatchSearch(Context context, String initial, Consumer<String> search, Consumer<BaseTags> choose) {
        super(context);
        this.search = search;
        this.choose = choose;
        setOrientation(VERTICAL);
        // Approved 0.156 #40 (suggestions/04): a side panel over Details, not a page of its own.
        setPadding(dp(22), dp(18), dp(22), dp(14));
        setBackgroundColor(PANEL);
        title = text("Fix match", 15);
        title.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        addView(title);
        TextView help = text("Search by title, TMDB ID or IMDb ID", 12);
        help.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        help.setPadding(0, dp(2), 0, dp(10));
        addView(help);
        input = new KeyboardInput(context);
        input.setTag("semantic:match.query");
        input.setSingleLine(true);
        input.setShowSoftInputOnFocus(false);
        input.setTextColor(Color.WHITE);
        input.setTextSize(17);
        input.setBackground(PreviewDialog.focus(context));
        input.setText(initial);
        input.setSelection(input.length());
        addView(input, new LayoutParams(-1, KeyboardInput.compactHeight(context)));
        keyboard = new PreviewKeyboard(context, input, this::focusResults);
        LayoutParams keys = new LayoutParams(-1, -2);
        keys.topMargin = dp(10);
        addView(keyboard, keys);
        keyboard.setLeaveDown(this::focusResults);
        status = text("Searching…", 13);
        status.setTextColor(SupernovaStyle.HEADING);
        status.setPadding(dp(2), dp(12), 0, dp(8));
        addView(status);
        scroll = new ScrollView(context);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        rows = new LinearLayout(context);
        rows.setOrientation(VERTICAL);
        rows.setClipChildren(false);
        rows.setClipToPadding(false);
        scroll.addView(rows);
        addView(scroll, new LayoutParams(-1, 0, 1));
        query = () -> search.accept(input.getText().toString().trim());
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeCallbacks(query);
                postDelayed(query, 350);
            }
            @Override
            public void afterTextChanged(Editable value) {
            }
        });
        input.setOnEditorActionListener((v, action, event) -> {
            removeCallbacks(query);
            query.run();
            return true;
        });
        input.setOnKeyListener((v, key, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_DPAD_DOWN) {
                keyboard.focusLastKey();
                return true;
            }
            return false;
        });
        post(keyboard::focusLastKey);
    }

    /** Shows the hosting activity as a right-hand side panel; Details stays visible beside it. */
    public static void asSidePanel(android.app.Activity activity) {
        android.view.Window window = activity.getWindow();
        int width = Math.round(PANEL_WIDTH_DP * activity.getResources().getDisplayMetrics().density);
        window.setGravity(Gravity.END | Gravity.FILL_VERTICAL);
        window.setLayout(width, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
    }

    /** Marks the result that is today's match ("Current"). */
    public void setCurrentOnlineId(long onlineId) {
        currentOnlineId = onlineId;
    }

    private View poster(BaseTags tags) {
        ImageView image = new ImageView(getContext());
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackgroundColor(0xFF2A2D33);
        com.archos.mediascraper.ScraperImage art = tags.getDefaultPoster();
        String url = art == null ? null : art.getThumbUrl();
        if (url == null || url.isEmpty())
            return image;
        try {
            com.squareup.picasso.Picasso.get().load(url).resize(dp(40), dp(60)).centerCrop().into(image);
        } catch (RuntimeException unavailable) {
            // No picture loader (tests, early start): the grey card stays.
        }
        return image;
    }

    static String displayTitle(BaseTags tags) {
        if (tags instanceof MovieTags)
            return String.valueOf(((MovieTags) tags).getTitle());
        return label(tags);
    }

    /** "2026 · Film", "TV series", "Episode". */
    static String about(BaseTags tags) {
        if (tags instanceof MovieTags) {
            int year = ((MovieTags) tags).getYear();
            return year > 0 ? year + " · Film" : "Film";
        }
        if (tags instanceof ShowTags)
            return "TV series";
        if (tags instanceof EpisodeTags)
            return "Episode";
        return "";
    }

    /** "Fix match · File: name.mkv" — which file is being matched. */
    public void setSubject(String fileName) {
        if (fileName == null || fileName.isEmpty())
            title.setText("Fix match");
        else
            title.setText("Fix match · File: " + fileName);
    }

    /** The keyboard shows while you type; in the results it folds away so they get the room. */
    private final ViewTreeObserver.OnGlobalFocusChangeListener focusWatcher = this::focusMoved;

    private void focusMoved(View before, View after) {
        if (after == null || !isInside(after, this))
            return;
        boolean inResults = isInside(after, rows);
        keyboard.setVisibility(inResults ? GONE : VISIBLE);
    }

    private static boolean isInside(View view, View parent) {
        for (View at = view; at != null; ) {
            if (at == parent)
                return true;
            ViewParent next = at.getParent();
            at = next instanceof View ? (View) next : null;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(focusWatcher);
    }

    public void searching() {
        setResults(Collections.emptyList());
        status.setText("Searching…");
    }

    public void unavailable() {
        setResults(Collections.emptyList());
        status.setText("Search unavailable. Please try again.");
    }

    public void setResults(List<BaseTags> results) {
        boolean append = results.size() >= rendered.size();
        for (int i = 0; append && i < rendered.size(); i++)
            append = rendered.get(i) == results.get(i);
        if (!append) {
            boolean focused = rows.hasFocus();
            rows.removeAllViews();
            rendered.clear();
            if (focused) {
                keyboard.setVisibility(VISIBLE);
                keyboard.focusLastKey();
            }
        }
        for (int i = rendered.size(); i < results.size(); i++) {
            final int index = i;
            BaseTags tags = results.get(i);
            LinearLayout row = new LinearLayout(getContext());
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setTag("semantic:match.result:" + i);
            row.setFocusable(true);
            row.setFocusableInTouchMode(true);
            row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            row.setPadding(dp(8), dp(8), dp(12), dp(8));
            row.setBackground(PreviewDialog.focus(getContext()));
            row.addView(poster(tags), new LayoutParams(dp(40), dp(60)));
            LinearLayout words = new LinearLayout(getContext());
            words.setOrientation(VERTICAL);
            TextView title = text(displayTitle(tags), 16);
            title.setMaxLines(2);
            title.setEllipsize(TextUtils.TruncateAt.END);
            words.addView(title);
            String about = about(tags);
            if (!about.isEmpty()) {
                TextView detail = text(about, 13);
                detail.setTextColor(SupernovaStyle.TEXT_SECONDARY);
                detail.setPadding(0, dp(3), 0, 0);
                words.addView(detail);
            }
            LayoutParams wordsSize = new LayoutParams(0, -2, 1);
            wordsSize.leftMargin = dp(12);
            row.addView(words, wordsSize);
            if (currentOnlineId > 0 && tags.getOnlineId() == currentOnlineId) {
                TextView current = text("Current", 13);
                current.setTag("semantic:match.current");
                current.setTextColor(SupernovaStyle.TEXT_SECONDARY);
                row.addView(current);
            }
            row.setContentDescription(label(tags));
            row.setOnClickListener(v -> choose.accept(tags));
            row.setOnKeyListener((v, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (key == KeyEvent.KEYCODE_DPAD_LEFT || (key == KeyEvent.KEYCODE_DPAD_UP && index == 0)) {
                    keyboard.setVisibility(VISIBLE);
                    keyboard.focusLastKey();
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_RIGHT)
                    return true;
                if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int next = index + (key == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
                    if (next >= 0 && next < rows.getChildCount())
                        rows.getChildAt(next).requestFocus();
                    return true;
                }
                return false;
            });
            LayoutParams cell = new LayoutParams(-1, -2);
            cell.bottomMargin = dp(8);
            rows.addView(row, cell);
            rendered.add(tags);
        }
        status.setText(results.isEmpty() ? "No matches" : "Matches on TMDB");
    }

    public void focusInput() {
        keyboard.setVisibility(VISIBLE);
        keyboard.focusLastKey();
    }

    public void refresh() {
        removeCallbacks(query);
        query.run();
    }

    public void parentSeries(String name, Runnable change) {
        LinearLayout parent = new LinearLayout(getContext());
        parent.setGravity(Gravity.CENTER_VERTICAL);
        TextView label = text("Episodes in " + name + " · enter S1 E1, an episode title or ID", 13);
        parent.addView(label, new LayoutParams(0, -2, 1));
        TextView action = text("Change Series Match", 14);
        action.setPadding(dp(10), dp(6), dp(10), dp(6));
        action.setFocusable(true);
        action.setFocusableInTouchMode(true);
        action.setBackground(PreviewDialog.focus(getContext()));
        action.setOnClickListener(view -> change.run());
        action.setTag("semantic:match.change-series");
        parent.addView(action);
        addView(parent, 2, new LayoutParams(-1, dp(40)));
    }

    private void focusResults() {
        if (rows.getChildCount() > 0)
            rows.getChildAt(0).requestFocus();
    }

    public static String label(BaseTags tags) {
        if (tags instanceof MovieTags) {
            MovieTags movie = (MovieTags) tags;
            return movie.getTitle() + (movie.getYear() > 0 ? " (" + movie.getYear() + ")" : "");
        }
        if (tags instanceof ShowTags)
            return ((ShowTags) tags).getTitle();
        if (tags instanceof EpisodeTags) {
            EpisodeTags episode = (EpisodeTags) tags;
            return episode.getShowTitle() + " · S" + episode.getSeason() + " E" + episode.getEpisode();
        }
        return "Metadata match";
    }

    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalFocusChangeListener(focusWatcher);
        removeCallbacks(query);
        super.onDetachedFromWindow();
    }

    private TextView text(String value, int size) {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(Color.WHITE);
        return view;
    }

    private int dp(int size) {
        return PreviewDialog.dp(getContext(), size);
    }
}
