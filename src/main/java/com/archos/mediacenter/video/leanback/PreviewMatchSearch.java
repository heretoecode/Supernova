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
        // One Fix match page (owner, 12 Nov 2026; mock-up network-files-tabs/14b): a page on charcoal, the path
        // across the top, the search and keyboard on the left, the matches on TMDB on the right. Back returns to
        // where it was opened from. Replaces the 0.156 side panel.
        // Opened from a title (owner, 16 Nov 2026; fix-match-from-details/v2): Files › Library Care's page,
        // the tab row with Library Care open, the path, and where Back returns to.
        setPadding(dp(48), dp(8), dp(48), dp(20));
        setBackgroundColor(PAGE);
        com.archos.mediacenter.video.leanback.files.FilesTabsPicture tabs =
            new com.archos.mediacenter.video.leanback.files.FilesTabsPicture(context);
        LayoutParams tabsSize = new LayoutParams(-1, -2);
        tabsSize.bottomMargin = dp(20);
        addView(tabs, tabsSize);
        LinearLayout crumb = new LinearLayout(context);
        crumb.setGravity(Gravity.CENTER_VERTICAL);
        TextView where = text("Library Care  ›  ", 17);
        where.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        crumb.addView(where);
        title = text(initial == null || initial.isEmpty() ? "Fix match" : "Fix match · " + initial, 17);
        title.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        crumb.addView(title, new LayoutParams(0, -2, 1));
        fromTitle = initial == null ? "" : initial;
        if (!fromTitle.isEmpty()) {
            TextView back = text("From " + fromTitle + " · Back returns there", 13);
            back.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            back.setTag("semantic:match.from");
            crumb.addView(back);
        }
        LayoutParams crumbSize = new LayoutParams(-1, -2);
        crumbSize.bottomMargin = dp(24);
        addView(crumb, crumbSize);
        LinearLayout body = new LinearLayout(context);
        body.setBaselineAligned(false);
        addView(body, new LayoutParams(-1, 0, 1));
        LinearLayout left = new LinearLayout(context);
        left.setOrientation(VERTICAL);
        body.addView(left, new LayoutParams(0, -1, 44));
        LinearLayout right = new LinearLayout(context);
        right.setOrientation(VERTICAL);
        LayoutParams rightSize = new LayoutParams(0, -1, 56);
        rightSize.leftMargin = dp(30);
        body.addView(right, rightSize);
        input = new KeyboardInput(context);
        input.setTag("semantic:match.query");
        input.setSingleLine(true);
        input.setShowSoftInputOnFocus(false);
        input.setTextColor(Color.WHITE);
        input.setTextSize(17);
        input.setBackground(PreviewDialog.focus(context));
        input.setText(initial);
        input.setSelection(input.length());
        left.addView(input, new LayoutParams(-1, KeyboardInput.compactHeight(context)));
        keyboard = new PreviewKeyboard(context, input, this::focusResults);
        LayoutParams keys = new LayoutParams(-1, -2);
        keys.topMargin = dp(10);
        left.addView(keyboard, keys);
        keyboard.setLeaveDown(this::focusResults);
        tips(left);
        status = text("Searching…", 15);
        status.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        status.setPadding(dp(2), 0, 0, dp(10));
        right.addView(status);
        scroll = new ScrollView(context);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        rows = new LinearLayout(context);
        rows.setOrientation(VERTICAL);
        rows.setClipChildren(false);
        rows.setClipToPadding(false);
        scroll.addView(rows);
        right.addView(scroll, new LayoutParams(-1, 0, 1));
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

    private static void inkWords(LinearLayout words, boolean focused) {
        for (int i = 0; i < words.getChildCount(); i++) {
            if (!(words.getChildAt(i) instanceof TextView))
                continue;
            TextView line = (TextView) words.getChildAt(i);
            int rest = i == 0 ? Color.WHITE : SupernovaStyle.TEXT_SECONDARY;
            line.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : rest);
        }
    }

    /** The two buttons on a match opened from a title; none when opened from Library Care itself. */
    private TextView[] matchButtons(LinearLayout row) {
        if (fromTitle.isEmpty())
            return new TextView[0];
        TextView[] buttons = {text("Match and return", 14), text("Match and stay", 14)};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setPadding(dp(14), dp(6), dp(14), dp(6));
            buttons[i].setVisibility(INVISIBLE);
            buttons[i].setTag(i == 0 ? "semantic:match.return" : "semantic:match.stay");
            LayoutParams size = new LayoutParams(-2, -2);
            size.leftMargin = dp(12);
            row.addView(buttons[i], size);
        }
        return buttons;
    }

    private void paintMatchButtons(TextView[] buttons, boolean shown, int chosen) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setVisibility(shown ? VISIBLE : INVISIBLE);
            android.graphics.drawable.GradientDrawable box = new android.graphics.drawable.GradientDrawable();
            box.setCornerRadius(dp(8));
            if (i == chosen)
                box.setColor(SupernovaStyle.FOCUS_TEXT);
            else
                box.setStroke(Math.max(1, dp(1)), SupernovaStyle.FOCUS_TEXT);
            buttons[i].setBackground(box);
            buttons[i].setTextColor(i == chosen ? Color.WHITE : SupernovaStyle.FOCUS_TEXT);
        }
    }

    /** The page's colour: plain charcoal, as every page (RULES). */
    static final int PAGE = 0xFF16181C;

    /** What the search understands, under the keyboard (mock-up network-files-tabs/14b). */
    private void tips(LinearLayout parent) {
        String[][] lines = {
            {"search", "Any style", "capitals, dots and dashes and a leading \u201cThe\u201d are ignored"},
            {"language", "Original titles", "search in the film\u2019s own language if you like"},
            {"info", "ID numbers", "type a TMDB or IMDb number"}
        };
        for (String[] line : lines) {
            TextView tip = text("", 12);
            android.text.SpannableStringBuilder words = new android.text.SpannableStringBuilder(line[1]);
            words.setSpan(new android.text.style.ForegroundColorSpan(Color.WHITE), 0, line[1].length(),
                          android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            words.append("   ").append(line[2]);
            tip.setText(words);
            tip.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            PreviewIcon.apply(tip, line[0], 14);
            LayoutParams size = new LayoutParams(-1, -2);
            size.topMargin = dp(parent.getChildCount() == 2 ? 18 : 8);
            parent.addView(tip, size);
        }
    }

    /**
     * Shows the hosting activity as the Fix match page, full screen (owner, 12 Nov 2026). Kept under its old name
     * for the two scraping activities.
     */
    public static void asSidePanel(android.app.Activity activity) {
        android.view.Window window = activity.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(PAGE));
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
    /** Names the file only when the page was not opened from a title (which already names it). */
    public void setSubject(String fileName) {
        if (!fromTitle.isEmpty())
            return;
        if (fileName == null || fileName.isEmpty())
            title.setText("Fix match");
        else
            title.setText("Fix match · " + fileName);
    }

    private String fromTitle = "";

    /**
     * After "Match and stay" the page that finishes the match opens Files › Library Care instead of returning to the
     * title (owner, 16 Nov 2026). Read once by the scraping activity when it closes.
     */
    public static volatile boolean stayOnFiles;

    /** Opens Files › Library Care if the last choice was "Match and stay". */
    public static void finishTo(android.app.Activity activity) {
        if (!stayOnFiles)
            return;
        stayOnFiles = false;
        activity.startActivity(new android.content.Intent(activity, MainActivityLeanback.class)
                                   .putExtra("preview_tab", 3)
                                   .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    /** The keyboard shows while you type; in the results it folds away so they get the room. */
    private final ViewTreeObserver.OnGlobalFocusChangeListener focusWatcher = this::focusMoved;

    private void focusMoved(View before, View after) {
        if (after == null || !isInside(after, this))
            return;
        // On the full page there is room for both (fix-match-from-details/v2): the keyboard stays in view.
        keyboard.setVisibility(VISIBLE);
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
            row.setBackground(null);
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
            // The focused match offers Match and return (first) and Match and stay (fix-match-from-details/v2);
            // Left/Right moves between them, OK applies.
            int[] choice = {0};
            TextView[] actions = matchButtons(row);
            row.setOnFocusChangeListener((v, focused) -> {
                // The focused match is the white box with black text (RULES §3; fix-match-from-details/v2).
                v.setBackground(focused ? SupernovaStyle.rounded(getContext(), SupernovaStyle.FOCUS_FILL, 10) : null);
                inkWords(words, focused);
                paintMatchButtons(actions, focused, choice[0]);
            });
            row.setOnClickListener(v -> {
                stayOnFiles = !fromTitle.isEmpty() && choice[0] == 1;
                choose.accept(tags);
            });
            row.setOnKeyListener((v, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (!fromTitle.isEmpty() && key == KeyEvent.KEYCODE_DPAD_RIGHT && choice[0] == 0) {
                    choice[0] = 1;
                    paintMatchButtons(actions, true, 1);
                    return true;
                }
                if (!fromTitle.isEmpty() && key == KeyEvent.KEYCODE_DPAD_LEFT && choice[0] == 1) {
                    choice[0] = 0;
                    paintMatchButtons(actions, true, 0);
                    return true;
                }
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
