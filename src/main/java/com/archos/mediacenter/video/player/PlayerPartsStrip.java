package com.archos.mediacenter.video.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Chapters · Episodes below the seek bar (0.156 #21, mock-ups {@code docs/design/proposals/chapters/01}–{@code 04}).
 * At rest only two small buttons sit under the bar. Down from the bar focuses the first one and its row of cards
 * slides in; Left/Right switches between Chapters and Episodes; Down goes into the cards; Up goes back up a level;
 * OK jumps to a part or plays an episode; Back closes the row. Chapters merge the file's chapters with IntroDB parts
 * (engine: {@link PlaybackParts}); each card gets a frame from the video at its start.
 */
final class PlayerPartsStrip implements PlayerController.Strip
{
    static final String TAG = "player.strip";
    static final String TAG_ROW = "player.strip.row";
    static final String CHAPTERS = "Chapters";
    static final String EPISODES = "Episodes";
    static final int CARD_WIDTH_DP = 150;
    static final int CARD_HEIGHT_DP = 84;
    static final float FOCUSED_SCALE = 1.12f;
    static final int PLAYING_TEXT = 0xFF9CC9FF;

    /** Everything the strip needs from playback; the player supplies it, tests supply a fixture. */
    interface Source
    {
        PlaybackParts parts();

        List<PlaybackEpisodes.Item> episodes();

        long positionMs();

        long currentEpisodeId();

        PartPictures.FrameSource frames();

        void seekTo(long positionMs);

        void playEpisode(PlaybackEpisodes.Item item);
    }

    /** The bar's thin part lines; a focused chapter card lights up its stretch. */
    interface Highlight
    {
        void highlight(long startMs, long endMs);
    }

    private final Context context;
    private final Source source;
    private final View bar;
    private final Highlight highlight;
    private final Runnable keepShowing;
    private final List<View> lift;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final LinearLayout root;
    private final LinearLayout tabs;
    private final TextView chaptersTab;
    private final TextView episodesTab;
    private final HorizontalScrollView row;
    private final LinearLayout cards;
    private final List<Card> shown = new ArrayList<>();
    private String mode = CHAPTERS;
    private String signature = "";
    private boolean expanded;
    private PartPictures pictures;

    PlayerPartsStrip(Context context, Source source, View bar, Highlight highlight, Runnable keepShowing,
                     List<View> lift)
    {
        this.context = context;
        this.source = source;
        this.bar = bar;
        this.highlight = highlight;
        this.keepShowing = keepShowing;
        this.lift = lift == null ? Collections.emptyList() : lift;
        root = new LinearLayout(context);
        root.setTag(TAG);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setClipChildren(false);
        root.setClipToPadding(false);
        tabs = new LinearLayout(context);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setPadding(0, dp(6), 0, dp(4));
        chaptersTab = tab(CHAPTERS);
        episodesTab = tab(EPISODES);
        tabs.addView(chaptersTab);
        tabs.addView(episodesTab);
        root.addView(tabs, new LinearLayout.LayoutParams(-1, -2));
        // The shared tab row (RULES §3; player v8-v11): a faint line under the row, the open tab underlined on it.
        View rule = new View(context);
        rule.setBackgroundColor(TAB_RULE);
        LinearLayout.LayoutParams ruleSize = new LinearLayout.LayoutParams(-1, Math.max(1, dp(1)));
        // The card row sits centred between the tab line and the screen's bottom edge (player v11, approved 13 Nov):
        // 24dp here + the cards' 8dp padding matches the 8dp padding + the control bar's 24dp bottom inset below.
        ruleSize.bottomMargin = dp(24);
        root.addView(rule, ruleSize);
        tabs.getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> paintTabs());
        row = new HorizontalScrollView(context);
        row.setTag(TAG_ROW);
        row.setHorizontalScrollBarEnabled(false);
        row.setClipChildren(false);
        row.setClipToPadding(false);
        row.setFocusable(false);
        row.setVisibility(View.GONE);
        cards = new LinearLayout(context);
        cards.setOrientation(LinearLayout.HORIZONTAL);
        cards.setClipChildren(false);
        cards.setClipToPadding(false);
        cards.setPadding(0, dp(8), dp(24), 0);
        row.addView(cards, new FrameLayout.LayoutParams(-2, -2));
        root.addView(row, new LinearLayout.LayoutParams(-1, -2));
        refresh();
    }

    View view()
    {
        return root;
    }

    boolean expanded()
    {
        return expanded;
    }

    String mode()
    {
        return mode;
    }

    List<View> cardViews()
    {
        List<View> views = new ArrayList<>();
        for (Card card : shown)
            views.add(card.view);
        return views;
    }

    TextView tabView(String name)
    {
        return CHAPTERS.equals(name) ? chaptersTab : episodesTab;
    }

    // ---- what is available ----------------------------------------------------------------------------------

    private List<PlaybackParts.Part> parts()
    {
        PlaybackParts parts = source.parts();
        if (parts == null || parts.parts().size() < 2)
            return Collections.emptyList();
        return parts.parts();
    }

    private List<PlaybackEpisodes.Item> episodes()
    {
        List<PlaybackEpisodes.Item> episodes = source.episodes();
        if (episodes == null || episodes.size() < 2)
            return Collections.emptyList();
        return episodes;
    }

    /** Called with the HUD's regular refresh: shows only the buttons that have something behind them. */
    void refresh()
    {
        boolean chapters = !parts().isEmpty();
        boolean series = !episodes().isEmpty();
        chaptersTab.setVisibility(chapters ? View.VISIBLE : View.GONE);
        episodesTab.setVisibility(series ? View.VISIBLE : View.GONE);
        root.setVisibility(chapters || series ? View.VISIBLE : View.GONE);
        if (!chapters && !series) {
            collapse();
            return;
        }
        if (CHAPTERS.equals(mode) && !chapters)
            mode = EPISODES;
        if (EPISODES.equals(mode) && !series)
            mode = CHAPTERS;
        chaptersTab.setSelected(expanded && CHAPTERS.equals(mode));
        episodesTab.setSelected(expanded && EPISODES.equals(mode));
        openTab(chaptersTab, expanded && CHAPTERS.equals(mode));
        openTab(episodesTab, expanded && EPISODES.equals(mode));
        TextView first = chapters ? chaptersTab : episodesTab;
        TextView last = series ? episodesTab : chaptersTab;
        first.setNextFocusLeftId(first.getId());
        last.setNextFocusRightId(last.getId());
        if (chapters && series) {
            chaptersTab.setNextFocusRightId(episodesTab.getId());
            episodesTab.setNextFocusLeftId(chaptersTab.getId());
        }
        if (expanded)
            bindCards();
    }

    // ---- PlayerController.Strip -----------------------------------------------------------------------------

    @Override
    public boolean open()
    {
        refresh();
        if (root.getVisibility() != View.VISIBLE)
            return false;
        expand();
        tabView(mode).requestFocus();
        return true;
    }

    @Override
    public boolean contains(View focused)
    {
        for (View v = focused; v != null; v = v.getParent() instanceof View ? (View)v.getParent() : null)
            if (v == root)
                return true;
        return false;
    }

    @Override
    public void vertical(boolean up, View focused)
    {
        keepShowing.run();
        boolean inTabs = focused != null && focused.getParent() == tabs;
        if (inTabs && up) {
            collapse();
            bar.requestFocus();
        } else if (inTabs) {
            focusCurrentCard();
        } else if (up) {
            tabView(mode).requestFocus();
        }
    }

    @Override
    public boolean back()
    {
        if (!expanded)
            return false;
        collapse();
        bar.requestFocus();
        return true;
    }

    @Override
    public void collapse()
    {
        if (!expanded)
            return;
        expanded = false;
        row.setVisibility(View.GONE);
        chaptersTab.setSelected(false);
        episodesTab.setSelected(false);
        openTab(chaptersTab, false);
        openTab(episodesTab, false);
        highlight.highlight(-1, -1);
        closePictures();
        for (View v : lift)
            v.animate().translationY(0).setDuration(180).start();
    }

    private void expand()
    {
        if (!expanded) {
            expanded = true;
            signature = "";
            row.setVisibility(View.VISIBLE);
            row.setTranslationY(dp(24));
            row.setAlpha(0f);
            row.animate().translationY(0).alpha(1f).setDuration(200).start();
            float rise = -dp(CARD_HEIGHT_DP + 16);
            for (View v : lift)
                v.animate().translationY(rise).setDuration(200).start();
        }
        refresh();
    }

    private void setMode(String next)
    {
        if (next.equals(mode) && expanded)
            return;
        mode = next;
        signature = "";
        expand();
    }

    private void focusCurrentCard()
    {
        if (shown.isEmpty())
            return;
        int current = 0;
        for (int i = 0; i < shown.size(); i++)
            if (shown.get(i).playing)
                current = i;
        shown.get(current).view.requestFocus();
    }

    // ---- cards ----------------------------------------------------------------------------------------------

    private void bindCards()
    {
        long position = source.positionMs();
        long currentId = source.currentEpisodeId();
        String next = CHAPTERS.equals(mode) ? chapterSignature(parts()) : episodeSignature(episodes());
        if (!next.equals(signature)) {
            signature = next;
            rebuild();
        }
        if (CHAPTERS.equals(mode)) {
            List<PlaybackParts.Part> parts = parts();
            for (int i = 0; i < shown.size() && i < parts.size(); i++)
                updateChapter(shown.get(i), parts.get(i), position);
        } else {
            List<PlaybackEpisodes.Item> episodes = episodes();
            for (int i = 0; i < shown.size() && i < episodes.size(); i++)
                updateEpisode(shown.get(i), episodes.get(i), currentId, position);
        }
    }

    private void rebuild()
    {
        View focused = cards.findFocus();
        int focusedIndex = focused == null ? -1 : cards.indexOfChild(focused);
        cards.removeAllViews();
        shown.clear();
        closePictures();
        if (CHAPTERS.equals(mode)) {
            for (PlaybackParts.Part part : parts()) {
                Card card = card(part.name.isEmpty() ? "Unknown" : part.name);
                card.view.setOnClickListener(v -> jumpTo(part));
                card.view.setOnFocusChangeListener((v, has) -> focusChanged(card, has, part));
                add(card);
            }
            loadChapterPictures();
        } else {
            for (PlaybackEpisodes.Item item : episodes()) {
                Card card = card(episodeTitle(item));
                card.view.setOnClickListener(v -> play(item));
                card.view.setOnFocusChangeListener((v, has) -> focusChanged(card, has, null));
                if (item.still != null)
                    Picasso.get().load(item.still).fit().centerCrop().into(card.image);
                add(card);
            }
        }
        // Left/Right stay inside the row at both ends instead of jumping to the buttons above.
        if (!shown.isEmpty()) {
            View first = shown.get(0).view;
            View last = shown.get(shown.size() - 1).view;
            first.setNextFocusLeftId(first.getId());
            last.setNextFocusRightId(last.getId());
        }
        if (focusedIndex >= 0 && focusedIndex < shown.size())
            shown.get(focusedIndex).view.requestFocus();
    }

    private void add(Card card)
    {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(CARD_WIDTH_DP), dp(CARD_HEIGHT_DP));
        lp.rightMargin = dp(12);
        cards.addView(card.view, lp);
        shown.add(card);
    }

    private void focusChanged(Card card, boolean focused, PlaybackParts.Part part)
    {
        card.view.setPivotX(card.view.getWidth() / 2f);
        card.view.setPivotY(card.view.getHeight() / 2f);
        float scale = focused ? FOCUSED_SCALE : 1f;
        card.view.animate().scaleX(scale).scaleY(scale).setDuration(150).start();
        card.view.setForeground(focused ? ring() : null);
        if (focused) {
            keepShowing.run();
            if (part != null)
                highlight.highlight(part.startMs, part.endMs);
        } else if (part != null) {
            highlight.highlight(-1, -1);
        }
    }

    private void updateChapter(Card card, PlaybackParts.Part part, long position)
    {
        boolean playing = position >= part.startMs && position < part.endMs;
        card.playing = playing;
        String from = "from " + time(part.startMs);
        card.subtitle.setText(playing ? "Playing · " + from : "From " + time(part.startMs));
        card.subtitle.setTextColor(playing ? PLAYING_TEXT : SupernovaStyle.TEXT);
        float fraction = position >= part.endMs ? 1f
                         : playing ? (float)(position - part.startMs) / (part.endMs - part.startMs)
                                   : 0f;
        card.progress.set(fraction);
    }

    private void updateEpisode(Card card, PlaybackEpisodes.Item item, long currentId, long position)
    {
        boolean playing = item.id == currentId;
        card.playing = playing;
        card.subtitle.setText(episodeLine(item, playing));
        card.subtitle.setTextColor(playing ? PLAYING_TEXT : SupernovaStyle.TEXT);
        float fraction = 0f;
        if (playing && item.durationMs > 0)
            fraction = Math.min(1f, (float)position / item.durationMs);
        else if (item.watched)
            fraction = 1f;
        else if (item.resumeMs > 0 && item.durationMs > 0)
            fraction = Math.min(1f, (float)item.resumeMs / item.durationMs);
        card.progress.set(fraction);
    }

    private void jumpTo(PlaybackParts.Part part)
    {
        source.seekTo(part.startMs);
        collapse();
        bar.requestFocus();
    }

    private void play(PlaybackEpisodes.Item item)
    {
        if (item.id == source.currentEpisodeId()) {
            collapse();
            bar.requestFocus();
            return;
        }
        collapse();
        source.playEpisode(item);
    }

    private void loadChapterPictures()
    {
        PlaybackParts all = source.parts();
        PartPictures.FrameSource frames = source.frames();
        Uri still = currentStill();
        for (Card card : shown)
            if (still != null)
                Picasso.get().load(still).fit().centerCrop().into(card.image);
        if (all == null || frames == null)
            return;
        List<Card> targets = new ArrayList<>(shown);
        pictures = new PartPictures(all, null, frames, picture -> handler.post(() -> {
            int index = all.parts().indexOf(picture.part);
            Bitmap bitmap = picture.bitmap;
            if (index < 0 || index >= targets.size() || bitmap == null || !expanded)
                return;
            ImageView image = targets.get(index).image;
            Picasso.get().cancelRequest(image);
            image.setImageBitmap(bitmap);
        }));
    }

    private Uri currentStill()
    {
        long currentId = source.currentEpisodeId();
        List<PlaybackEpisodes.Item> episodes = source.episodes();
        if (episodes != null)
            for (PlaybackEpisodes.Item item : episodes)
                if (item.id == currentId)
                    return item.still;
        return null;
    }

    private void closePictures()
    {
        if (pictures != null)
            pictures.close();
        pictures = null;
    }

    static String chapterSignature(List<PlaybackParts.Part> parts)
    {
        StringBuilder key = new StringBuilder("c");
        for (PlaybackParts.Part part : parts)
            key.append('|').append(part.startMs).append(':').append(part.name);
        return key.toString();
    }

    static String episodeSignature(List<PlaybackEpisodes.Item> episodes)
    {
        StringBuilder key = new StringBuilder("e");
        for (PlaybackEpisodes.Item item : episodes)
            key.append('|').append(item.id);
        return key.toString();
    }

    /** The card's name: the episode's own title, or "Episode 4" when it has none (0.157 Shield review #15). */
    static String episodeTitle(PlaybackEpisodes.Item item)
    {
        if (!TextUtils.isEmpty(item.title) && !genericEpisodeTitle(item.title))
            return item.title;
        return item.number > 0 ? "Episode " + item.number : "Episode";
    }

    /**
     * The card's second line from what is known, never "Unknown": Playing / Watched, the episode number when the
     * name is on the first line, and the length. "Playing · Episode 2 · 46 min".
     */
    static String episodeLine(PlaybackEpisodes.Item item, boolean playing)
    {
        List<String> parts = new ArrayList<>();
        if (playing)
            parts.add("Playing");
        else if (item.watched)
            parts.add("Watched");
        if (item.number > 0 && !episodeTitle(item).equals("Episode " + item.number))
            parts.add("Episode " + item.number);
        if (item.durationMs > 0)
            parts.add(Math.max(1, Math.round(item.durationMs / 60000f)) + " min");
        return TextUtils.join(" · ", parts);
    }

    /** "Episode 4", "Ep. 4", "4": a title that only repeats the number. */
    static boolean genericEpisodeTitle(String title)
    {
        return title.trim().matches("(?i)(episode|ep\\.?)?\\s*0*[0-9]+");
    }

    /** "0:52", "11:40" or "1:02:05". */
    static String time(long ms)
    {
        long seconds = Math.max(0, ms / 1000);
        long hours = seconds / 3600;
        long minutes = seconds / 60 % 60;
        long rest = seconds % 60;
        if (hours > 0)
            return String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, rest);
        return String.format(Locale.ROOT, "%d:%02d", minutes, rest);
    }

    // ---- views ----------------------------------------------------------------------------------------------

    private static final class Card
    {
        FrameLayout view;
        ImageView image;
        TextView title;
        TextView subtitle;
        ProgressLine progress;
        boolean playing;
    }

    private Card card(String name)
    {
        Card card = new Card();
        card.view = new FrameLayout(context);
        card.view.setId(View.generateViewId());
        card.view.setFocusable(true);
        card.view.setFocusableInTouchMode(true);
        card.view.setClickable(true);
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(0xFF22262D);
        shape.setCornerRadius(dp(8));
        card.view.setBackground(shape);
        card.view.setClipToOutline(true);
        card.image = new ImageView(context);
        card.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.view.addView(card.image, new FrameLayout.LayoutParams(-1, -1));
        View scrim = new View(context);
        scrim.setBackground(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                 new int[] {0xE6000000, 0x00000000}));
        FrameLayout.LayoutParams scrimSize = new FrameLayout.LayoutParams(-1, dp(56), Gravity.BOTTOM);
        card.view.addView(scrim, scrimSize);
        LinearLayout text = new LinearLayout(context);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setPadding(dp(8), 0, dp(8), dp(9));
        card.title = label(name, 13, Typeface.BOLD, SupernovaStyle.TEXT);
        card.subtitle = label("", 10, Typeface.NORMAL, SupernovaStyle.TEXT);
        text.addView(card.title);
        text.addView(card.subtitle);
        card.view.addView(text, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));
        card.progress = new ProgressLine(context);
        FrameLayout.LayoutParams line = new FrameLayout.LayoutParams(-1, dp(2), Gravity.BOTTOM);
        line.leftMargin = dp(7);
        line.rightMargin = dp(7);
        line.bottomMargin = dp(4);
        card.view.addView(card.progress, line);
        card.view.setContentDescription(name);
        return card;
    }

    private TextView label(String value, int size, int style, int color)
    {
        TextView label = new TextView(context);
        label.setText(value);
        label.setTextSize(size);
        label.setTypeface(Typeface.create("sans-serif", style));
        label.setTextColor(color);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setShadowLayer(dp(2), 0, dp(1), Color.BLACK);
        return label;
    }

    private TextView tab(String name)
    {
        TextView tab = new TextView(context);
        tab.setId(View.generateViewId());
        tab.setText(name);
        tab.setTag("player.strip.tab." + name);
        tab.setTextSize(15);
        tab.setGravity(Gravity.CENTER);
        tab.setPadding(dp(10), dp(6), dp(10), dp(8));
        tab.setFocusable(true);
        tab.setFocusableInTouchMode(true);
        tab.setClickable(true);
        com.archos.mediacenter.video.leanback.PreviewIcon.apply(tab, CHAPTERS.equals(name) ? "list" : "tv", 16);
        tab.setTextColor(CLOSED_TAB);
        tab.setOnFocusChangeListener((v, focused) -> {
            if (focused) {
                keepShowing.run();
                setMode(name);
            }
            paintTabs();
        });
        tab.setOnClickListener(v -> {
            setMode(name);
            focusCurrentCard();
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.rightMargin = dp(20);
        tab.setLayoutParams(lp);
        return tab;
    }

    private static final int TAB_RULE = 0x24FFFFFF;
    private static final int CLOSED_TAB = 0xB3FFFFFF;

    /** Marks the open row's tab; the look follows the remote (see {@link #paintTabs()}). */
    private void openTab(TextView tab, boolean open)
    {
        tab.setActivated(open);
        paintTabs();
    }

    /**
     * Focus and path on the tab row (RULES §3; player v8-v11): the focused tab is the white box with black text and
     * icon; the open tab is underlined, bright while the remote is on the cards below, dull while it is on the row.
     */
    private void paintTabs()
    {
        boolean inRow = tabs.hasFocus();
        for (TextView tab : new TextView[] {chaptersTab, episodesTab}) {
            if (tab == null)
                continue;
            boolean focused = tab.isFocused();
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : tab.isActivated() ? Color.WHITE : CLOSED_TAB;
            tab.setTextColor(ink);
            SupernovaStyle.inkIcons(tab, ink);
            if (focused)
                tab.setBackground(SupernovaStyle.rounded(context, SupernovaStyle.FOCUS_FILL, 8));
            else if (tab.isActivated())
                tab.setBackground(new com.archos.mediacenter.video.leanback.TabUnderline(
                    inRow ? com.archos.mediacenter.video.leanback.TabUnderline.DULL
                          : com.archos.mediacenter.video.leanback.TabUnderline.BRIGHT, Math.max(2, dp(2))));
            else
                tab.setBackground(null);
        }
    }

    private GradientDrawable box(int fill, int edge)
    {
        GradientDrawable box = new GradientDrawable();
        box.setColor(fill);
        box.setCornerRadius(dp(Math.round(SupernovaStyle.RADIUS_DP)));
        if (edge != 0)
            box.setStroke(Math.max(1, dp(1)), edge);
        return box;
    }

    private GradientDrawable ring()
    {
        GradientDrawable ring = new GradientDrawable();
        ring.setColor(Color.TRANSPARENT);
        ring.setCornerRadius(dp(8));
        ring.setStroke(dp(2), Color.WHITE);
        return ring;
    }

    private int dp(int value)
    {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    /** The thin line along the bottom of a card: white for what has been watched, over a faint track. */
    static final class ProgressLine extends View
    {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float fraction;

        ProgressLine(Context context)
        {
            super(context);
            setFocusable(false);
        }

        void set(float value)
        {
            float clamped = Math.max(0f, Math.min(1f, value));
            if (clamped == fraction)
                return;
            fraction = clamped;
            invalidate();
        }

        float fraction()
        {
            return fraction;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            if (fraction <= 0f)
                return;
            float radius = getHeight() / 2f;
            paint.setColor(0x4DFFFFFF);
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(0, 0, getWidth() * fraction, getHeight(), radius, radius, paint);
        }
    }
}
