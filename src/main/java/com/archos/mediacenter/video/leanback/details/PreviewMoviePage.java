package com.archos.mediacenter.video.leanback.details;

import android.app.*;
import android.content.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.*;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.*;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.TextViewCompat;
import androidx.leanback.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import com.archos.mediacenter.video.leanback.tvshow.TvshowActionAdapter;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediacenter.video.streaming.StreamingActionPresenter;
import com.archos.mediacenter.video.streaming.StreamingActions;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.streaming.PreviewProviderIcons;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediacenter.video.utils.SubtitleDiscovery;
import com.archos.mediacenter.video.utils.VideoMetadata;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import com.archos.mediascraper.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.text.NumberFormat;
import java.util.*;
import java.util.SortedMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import org.json.JSONArray;
import org.json.JSONObject;

/** Presentation over the existing details fragment: playback, edits and deletion keep native handlers. */
public final class PreviewMoviePage extends ScrollView {
    private final LinearLayout body, cast, crew, details, related, trailers;
    private final Map<View, View> extraHeadings = new IdentityHashMap<>();
    private final ConnectedDetailsPanel sectionFrame;
    private final ScrollView reading;
    private final LinearLayout heroPage, tabBar, information;
    private final CinematicCard detailsCard;
    private final ImageView cardArtwork;
    private final FrameLayout lower;
    private final List<TextView> sectionTabs = new ArrayList<>();
    private String activeSection = "Details";
    private String boundTitle = "";
    private int versionCount;
    private final List<PreviewEpisodeRow> episodeRows = new ArrayList<>();
    private final List<PreviewLandscapeCard> landscapeCards = new ArrayList<>(), extraCards = new ArrayList<>();
    private final TextView title, meta, plot, heroLine, play, trailer, context, moreButton, matchMetadata;
    private final LinearLayout pills;
    /** Next-episode label above the logo (owner, 25 Nov; placement A; BRIEF_0158_B › Next episode on the hero). */
    private final TextView nextEpisode;
    private final LinearLayout providerActions;
    private TextView versions;
    private Runnable chooseVersions = () -> {
    };
    private Dialog moreDialog;
    private final Map<Integer, Action> moreRows = new HashMap<>();
    private ObjectAdapter observedActions;
    private final StreamingActionPresenter providerPresenter =
        new StreamingActionPresenter();
    private final List<Presenter.ViewHolder> providerHolders = new ArrayList<>();
    private final ObjectAdapter.DataObserver actionObserver = new ObjectAdapter.DataObserver() {
        public void onChanged() {
            renderProviders();
        }
        public void onItemRangeChanged(int start, int count) {
            renderProviders();
        }
        public void onItemRangeInserted(int start, int count) {
            renderProviders();
        }
        public void onItemRangeRemoved(int start, int count) {
            renderProviders();
        }
    };
    private final Supplier<ObjectAdapter> actions;
    private final Consumer<Action> action;
    private final Runnable nativeDetails;
    private final Consumer<Uri> artwork;
    private Video movie;
    private Tvshow show;
    private int seasonNumber = -1;
    private boolean compactCard;
    private SharedMetadata.Facts latestSummary;
    private Runnable showPlay;
    private final SortedMap<Integer, java.util.List<Episode>> seasons = new java.util.TreeMap<>();
    private LinearLayout episodes;
    private BaseTags tags;
    private final Map<String, ImageView> portraits = new HashMap<>();
    private List<ScraperTrailer> trailerList = Collections.emptyList();
    private Snapshot snapshot;
    private PreviewDetailsData.Result enrichment;
    private Future<?> enrichmentTask;
    private String enrichmentKey = "";
    private int enrichmentGeneration;
    private JSONObject remoteDetails;
    private String remoteKind;
    private long remoteId;
    private final List<Presenter.ViewHolder> cards = new ArrayList<>();
    private PreviewDetailsData.Result pendingEnrichment;
    private int pendingEnrichmentGeneration;
    private boolean enrichmentDeliveryScheduled;
    private final Runnable deliverEnrichment = () -> {
        enrichmentDeliveryScheduled = false;
        PreviewDetailsData.Result result = pendingEnrichment;
        pendingEnrichment = null;
        if (result != null)
            applyEnrichment(result, pendingEnrichmentGeneration);
    };

    /** Cached sections completed within one frame enter as one coherent update. Slow I/O never blocks Back. */
    private void enqueueEnrichment(PreviewDetailsData.Result result, int generation) {
        if (generation != enrichmentGeneration)
            return;
        if (pendingEnrichmentGeneration != generation)
            pendingEnrichment = null;
        pendingEnrichmentGeneration = generation;
        pendingEnrichment = PreviewDetailsData.merge(pendingEnrichment, result);
        if (!enrichmentDeliveryScheduled) {
            enrichmentDeliveryScheduled = true;
            postOnAnimation(deliverEnrichment);
        }
    }

    private String informationSignature = "", peopleSignature = "", receptionSignature = "";
    private DetailsBlueprintPanel informationPanel;
    private Picasso artworkLoader;
    private String artworkUri, failedArtworkUri;
    private boolean artworkReady;
    private long openedAt, artworkRetryAfter;
    private DetailsReviews reviews;
    private final PreviewCardPresenter presenter = new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);

    public PreviewMoviePage(Context c, Supplier<ObjectAdapter> actions, Consumer<Action> action, Runnable nativeDetails,
                            Consumer<Uri> artwork) {
        super(c);
        setBackground(new PreviewUtilityBackground(c));
        this.actions = actions;
        this.action = action;
        this.nativeDetails = nativeDetails;
        this.artwork = artwork;
        setFillViewport(true);
        setVerticalScrollBarEnabled(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setSmoothScrollingEnabled(false);
        setClipToPadding(false);
        setClipChildren(false);
        body = new LinearLayout(c);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(42), 0, dp(42), dp(24));
        body.setClipChildren(false);
        addView(body);
        heroPage = new LinearLayout(c);
        heroPage.setOrientation(LinearLayout.VERTICAL);
        heroPage.setPadding(0, dp(24), 0, 0);
        heroPage.setClipChildren(false);
        body.addView(heroPage, new LinearLayout.LayoutParams(-1, dp(488)));
        detailsCard = new CinematicCard(c);
        detailsCard.setTag("semantic:details.card");
        heroPage.addView(detailsCard, new LinearLayout.LayoutParams(-1, 0, 1));
        cardArtwork = new ImageView(c);
        cardArtwork.setScaleType(ImageView.ScaleType.CENTER_CROP);
        detailsCard.addView(cardArtwork, new FrameLayout.LayoutParams(-1, -1));
        detailsCard.addTextScrim();
        LinearLayout cardCopy = new LinearLayout(c);
        cardCopy.setTag("details.copy");
        cardCopy.setOrientation(LinearLayout.VERTICAL);
        cardCopy.setPadding(dp(24), dp(18), dp(24), dp(16));
        detailsCard.addView(cardCopy, new FrameLayout.LayoutParams(-1, -1));
        nextEpisode = NextEpisodeLabel.create(c);
        LinearLayout.LayoutParams nextSize = new LinearLayout.LayoutParams(-2, dp(NextEpisodeLabel.LABEL_DP));
        nextSize.bottomMargin = NextEpisodeLabel.gap(c);
        cardCopy.addView(nextEpisode, nextSize);
        title = text("", 34);
        title.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        title.setMaxLines(2);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setEllipsize(TextUtils.TruncateAt.END);
        cardCopy.addView(title, new LinearLayout.LayoutParams(dp(430), dp(TITLE_BOX_DP)));
        meta = text("", 18);
        meta.setSingleLine(true);
        meta.setEllipsize(TextUtils.TruncateAt.END);
        meta.setPadding(0, dp(10), 0, dp(4));
        cardCopy.addView(meta);
        context = text("", 13);
        context.setTextColor(0xffe1e9ef);
        context.setMaxLines(1);
        context.setEllipsize(TextUtils.TruncateAt.END);
        cardCopy.addView(context);
        pills = new LinearLayout(c);
        pills.setVisibility(GONE);
        cardCopy.addView(pills);
        // The full synopsis feeds the Details tab; the card itself shows the hero line (tagline, else a
        // two-line synopsis), the same as the Home hero (owner, 3 Nov 2026).
        plot = text("", 14);
        plot.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int d) {
            }

            public void onTextChanged(CharSequence s, int a, int b, int d) {
            }

            public void afterTextChanged(android.text.Editable e) {
                updateHeroLine();
            }
        });
        heroLine = text("", 14);
        heroLine.setTag("details.hero.line");
        heroLine.setMaxLines(2);
        heroLine.setLineSpacing(dp(2), 1);
        heroLine.setEllipsize(TextUtils.TruncateAt.END);
        heroLine.setShadowLayer(SharedMetadata.heroShadow(c), 0, dp(1), 0xCC000000);
        LinearLayout.LayoutParams plotSize = new LinearLayout.LayoutParams(
            Math.min(dp(440), getResources().getDisplayMetrics().widthPixels - dp(84)), dp(76));
        plotSize.topMargin = dp(8);
        View synopsisSpace = new View(c);
        cardCopy.addView(synopsisSpace, new LinearLayout.LayoutParams(1, 0, 1));
        cardCopy.addView(heroLine, plotSize);
        matchMetadata = button("Match Metadata", () -> {
            Action match = matchingAction();
            if (match != null)
                action.accept(match);
        });
        matchMetadata.setVisibility(GONE);
        cardCopy.addView(matchMetadata, new LinearLayout.LayoutParams(-2, dp(38)));
        LinearLayout buttons = new LinearLayout(c);
        buttons.setTag("details.actions");
        buttons.setClipChildren(false);
        buttons.setPadding(0, dp(12), 0, dp(10));
        cardCopy.addView(new View(c), new LinearLayout.LayoutParams(1, 0, 1));
        cardCopy.addView(buttons);
        trailer = button("Play Trailer", this::chooseTrailer);
        trailer.setVisibility(GONE);
        play = button("Play", this::play);
        buttons.addView(play);
        providerActions = new LinearLayout(c);
        providerActions.setClipChildren(false);
        buttons.addView(providerActions);
        moreButton = button("More", this::more);
        margin(buttons, moreButton);
        versions = button("Versions", () -> chooseVersions.run());
        versions.setVisibility(GONE);
        tabBar = new PreviewToolbar(c);
        tabBar.setClipChildren(false);
        LinearLayout.LayoutParams tabsSize = new LinearLayout.LayoutParams(-1, dp(44));
        tabsSize.topMargin = dp(14);
        sectionFrame = new ConnectedDetailsPanel(c);
        LinearLayout.LayoutParams sectionSize = new LinearLayout.LayoutParams(-1, dp(220));
        sectionSize.topMargin = dp(22);
        body.addView(sectionFrame, sectionSize);
        ((PreviewToolbar)tabBar).setConnected(true);
        tabBar.setPadding(0, 0, 0, 0);
        tabsSize.topMargin = 0;
        sectionFrame.addView(tabBar, tabsSize);
        episodes = section("Episodes");
        cast = section("Cast");
        crew = section("Crew");
        details = section("Details");
        related = section("More Like This");
        trailers = section("Extras");
        lower = new FrameLayout(c);
        lower.setClipChildren(false);
        reading = new ScrollView(c) {
            @Override
            protected void onMeasure(int w, int h) {
                // The Details tab runs about two screens and scrolls (v20): measured at its own height.
                super.onMeasure(w, h);
            }
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                revealContentFocus();
            }
            @Override
            protected void dispatchDraw(Canvas canvas) {
                int saved = canvas.save();
                // Clipped above and below only: a grown card or its ring may reach past the sides (D6, RULES §1).
                int side = getResources().getDisplayMetrics().widthPixels;
                canvas.clipRect(-side, getScrollY(), getWidth() + side, getScrollY() + getHeight());
                super.dispatchDraw(canvas);
                canvas.restoreToCount(saved);
            }
        };
        reading.setTag("details.reading.overflow");
        reading.setClipChildren(false);
        reading.setFillViewport(true);
        reading.setClipToPadding(false);
        reading.setSmoothScrollingEnabled(false);
        reading.setFocusable(false);
        reading.setPadding(dp(8), dp(6), dp(8), dp(8));
        reading.addView(lower, new ScrollView.LayoutParams(-1, -2));
        sectionFrame.addView(reading, new LinearLayout.LayoutParams(-1, 0, 1));
        information = new LinearLayout(c);
        information.setOrientation(LinearLayout.HORIZONTAL);
        lower.addView(information, new FrameLayout.LayoutParams(-1, -1));
        for (LinearLayout section : new LinearLayout[] {cast, crew, details}) {
            View wrapper = (View)section.getParent();
            body.removeView(wrapper);
            if (section == details) {
                wrapper.setPadding(0, 0, 0, 0);
                details.setPadding(0, 0, 0, 0);
                information.addView(wrapper, new LinearLayout.LayoutParams(-1, -1));
                details.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            }
        }
        for (LinearLayout section : new LinearLayout[] {episodes, related, trailers}) {
            View wrapper = (View)section.getParent();
            body.removeView(wrapper);
            lower.addView(wrapper, new FrameLayout.LayoutParams(-1, -2));
        }
        ((View)cast.getParent()).setVisibility(GONE);
        ((View)crew.getParent()).setVisibility(GONE);
        rebuildTabs();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int available = View.MeasureSpec.getSize(heightSpec);
        if (available > 0 && heroPage != null) {
            int viewport = Math.max(0, available - getPaddingTop() - getPaddingBottom());
            boolean compact = viewport < dp(380);
            compactCard = compact;
            // H1 (owner's 0.158 review): the Home hero's size and place, 1776 x 820 at 1080p, its top at the same
            // height as Home's, so going from the Home hero into Details the card stays put.
            int heroTop = compact ? dp(12) : Math.max(0, TvLayout.heroTop(getContext()) - getPaddingTop());
            int heroCard = compact ? Math.max(dp(160), viewport - dp(110)) : TvLayout.heroHeight(getContext());
            int heroHeight = heroTop + heroCard + dp(12);
            heroPage.setPadding(0, heroTop, 0, 0);
            LinearLayout.LayoutParams cardSize = (LinearLayout.LayoutParams) detailsCard.getLayoutParams();
            cardSize.width = compact ? -1 : TvLayout.heroWidth(getContext());
            cardSize.gravity = Gravity.CENTER_HORIZONTAL;
            LinearLayout.LayoutParams tabs = (LinearLayout.LayoutParams)tabBar.getLayoutParams();
            tabs.topMargin = 0;
            int cardHeight = heroHeight - heroPage.getPaddingTop() - heroPage.getPaddingBottom() - dp(12);
            ViewGroup copy = detailsCard.findViewWithTag("details.copy");
            copy.setPadding(dp(24), dp(compact ? 8 : 18), dp(24), dp(compact ? 8 : 16));
            title.getLayoutParams().width = Math.round(
                (View.MeasureSpec.getSize(widthSpec) - body.getPaddingLeft() - body.getPaddingRight()) * .49f);
            title.getLayoutParams().height =
                Math.min(TvLayout.dimension(getContext(), 96), Math.max(dp(40), Math.round(cardHeight * .24f)));
            title.setTextSize((compact ? 24 : 34) * TvLayout.scale(getContext()));
            title.setMaxLines(compact ? 1 : 2);
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                title, Math.round(18 * TvLayout.scale(getContext())),
                Math.round((compact ? 24 : 34) * TvLayout.scale(getContext())), 1,
                TypedValue.COMPLEX_UNIT_SP);
            meta.setTextSize(16 * TvLayout.scale(getContext()));
            meta.setMaxLines(1);
            meta.setPadding(0, dp(10), 0, dp(5));
            heroLine.getLayoutParams().height = -2;
            LinearLayout.LayoutParams synopsis = (LinearLayout.LayoutParams)heroLine.getLayoutParams();
            synopsis.topMargin = dp(compact ? 4 : 8);
            View actions = detailsCard.findViewWithTag("details.actions");
            actions.setPadding(0, dp(compact ? 3 : 12), 0, dp(compact ? 3 : 10));
            if (latestSummary != null)
                bindSummary();
            // Navigation measures the stage twice. Resolve before child measurement,
            // not from an intermediate layout size, so the lower-content teaser stays visible.
            if (heroPage.getLayoutParams().height != heroHeight)
                heroPage.getLayoutParams().height = heroHeight;
            tabBar.getLayoutParams().height = Math.max(dp(26), Math.round(available * 5f / 72));
            ((LinearLayout.LayoutParams)sectionFrame.getLayoutParams()).topMargin = dp(22);
            sectionFrame.getLayoutParams().height =
                Math.max(dp(90), available - Math.round(available / 6f) - Math.round(available * .04f));
            if (lower != null)
                lower.getLayoutParams().height =
                    activeSection.equals("Details")
                        ? Math.max(1, sectionFrame.getLayoutParams().height - tabBar.getLayoutParams().height -
                                          reading.getPaddingTop() - reading.getPaddingBottom())
                        : LayoutParams.WRAP_CONTENT;
        }
        int safeMargin = Math.round(MeasureSpec.getSize(widthSpec) * .04f);
        body.setPadding(safeMargin, 0, safeMargin, Math.round(available * .04f));
        super.onMeasure(widthSpec, heightSpec);
        // Resolve provider geometry during measurement so the first rendered frame matches Play.
        int actionHeight = Math.max(dp(44), play.getMeasuredHeight());
        boolean changed = false;
        for (int n = 0; n < providerActions.getChildCount(); n++) {
            View provider = providerActions.getChildAt(n);
            ViewGroup.LayoutParams size = provider.getLayoutParams();
            if (size.height != actionHeight) {
                size.height = actionHeight;
                provider.setLayoutParams(size);
                changed = true;
            }
        }
        if (changed)
            super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof TopNavigation) {
                ((TopNavigation)parent).setScrolled(top > 0);
                ((TopNavigation)parent).setDetailsExpanded(title != null && top > title.getBottom());
                break;
            }
            parent = parent.getParent();
        }
        invalidate();
    }

    /** Reuse the real title/logo; no second asynchronous request or focus target. */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        float progress = title == null ? 0 : compactTitleProgress(getScrollY(), title.getBottom(), dp(40));
        int content = canvas.save();
        if (progress > 0)
            canvas.clipRect(0, getScrollY() + Math.round(getHeight() / 6f) * progress, getWidth(),
                            getScrollY() + getHeight());
        super.dispatchDraw(canvas);
        canvas.restoreToCount(content);
        if (title == null || title.getWidth() == 0 || title.getHeight() == 0)
            return;
        if (progress <= 0)
            return;
        float scale = Math.min(dp(220) / (float)title.getWidth(), dp(40) / (float)title.getHeight());
        int saved = canvas.save();
        canvas.translate(body.getPaddingLeft(), getScrollY() + Math.round(getHeight() * .04f));
        canvas.scale(scale, scale);
        int layer = canvas.saveLayerAlpha(0, 0, title.getWidth(), title.getHeight(), Math.round(255 * progress));
        title.draw(canvas);
        canvas.restoreToCount(layer);
        canvas.restoreToCount(saved);
    }

    static float compactTitleProgress(int scroll, int titleBottom, int transition) {
        return Math.max(0f, Math.min(1f, (scroll - titleBottom) / (float)Math.max(1, transition)));
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        Rect safe = new Rect(rect);
        if (heroPage != null && rect.top >= heroPage.getBottom() - dp(44))
            safe.top -= dp(48);
        return super.computeScrollDeltaToGetChildRectOnScreen(safe);
    }

    public static final long TAB_SETTLE_MS = 8000;

    private void rebuildTabs() {
        View oldFocus = tabBar.findFocus();
        Object focusedTag = oldFocus == null ? null : oldFocus.getTag();
        String selected = activeSection;
        List<String> labels = new ArrayList<>();
        // One master order, each tab shown or hidden, never moved (owner, 12 Nov 2026; notes §29).
        labels.add("Details");
        if (show != null || "tv".equals(remoteKind))
            labels.add("Episodes");
        // Tabs never pop in (RULES §4): while the title's data is still arriving the whole set is drawn; a tab
        // that is still empty once it has had time to load hides (the rest close up).
        boolean settling = enrichment == null && SystemClock.elapsedRealtime() - openedAt < TAB_SETTLE_MS;
        if (settling)
            postDelayed(this::rebuildTabs, TAB_SETTLE_MS);
        if (hasPlayableExtras() || settling)
            labels.add("Extras");
        if (!landscapeCards.isEmpty() || settling)
            labels.add("More Like This");
        boolean unchanged = labels.size() == sectionTabs.size();
        for (int i = 0; unchanged && i < labels.size(); i++)
            unchanged = ("section:" + labels.get(i)).equals(sectionTabs.get(i).getTag());
        if (unchanged)
            return;
        tabBar.removeAllViews();
        sectionTabs.clear();
        for (String label : labels) {
            TextView tab = button(label, () -> showSection(label, true));
            tab.setTextSize(12 * TvLayout.scale(getContext()));
            tab.setSingleLine(true);
            PreviewIcon.apply(tab,
                              label.equals("Details")              ? "info"
                              : label.equals("Extras")             ? "play"
                              : label.equals("Episodes") ? "tv"
                                                                   : "recommendation",
                              Math.round(16 * TvLayout.scale(getContext())));
            tab.setTag("section:" + label);
            tab.setContentDescription(label);
            // Approved global button style: tab icons follow the text colour (white at rest, black on the white focus box).
            SupernovaStyle.applyButton(tab, SupernovaStyle.Surface.STANDARD);
            // The shared tab row (RULES §3; details-background/21): no box at rest, the white box when focused; the
            // open tab's underline is drawn by the frame below the row.
            tab.setBackground(null);
            if (label.equals("Details"))
                tab.setMinWidth(Math.round(getResources().getDisplayMetrics().widthPixels * .143f));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -1);
            lp.rightMargin = dp(16);
            tabBar.addView(tab, lp);
            tab.setForeground(null);
            if (Build.VERSION.SDK_INT >= 26)
                tab.setDefaultFocusHighlightEnabled(false);
            if (TvLayout.width(getContext()) < 780) {
                tab.setTextSize(10 * TvLayout.scale(getContext()));
                tab.setPadding(dp(8), dp(6), dp(8), dp(6));
            }
            tab.setOnFocusChangeListener((v, focused) -> {
                if (focused)
                    showSection(label, false);
                post(this::paintSectionTabs);
                sectionFrame.invalidate();
            });
            sectionTabs.add(tab);
        }
        showSection(labels.contains(selected) ? selected : labels.get(0), false);
        if (focusedTag != null) {
            View restore = tabBar.findViewWithTag(focusedTag);
            if (restore != null)
                restore.requestFocus();
        }
    }

    /**
     * DT2 (owner, 18 Nov 2026), the 0.158 top bar's look: the white box with black text where the remote is; once
     * focus goes down, the open tab keeps a soft grey box with a faded word; no underlines.
     */
    private void paintSectionTabs() {
        boolean inRow = tabBar.hasFocus();
        for (TextView tab : sectionTabs) {
            boolean focused = tab.isFocused();
            boolean open = ("section:" + activeSection).equals(tab.getTag()) && !inRow;
            if (focused)
                tab.setBackground(SupernovaStyle.rounded(getContext(), SupernovaStyle.FOCUS_FILL,
                                                         SupernovaStyle.SMALL_BOX_RADIUS_DP));
            else if (open)
                tab.setBackground(SupernovaStyle.rounded(getContext(), 0x26FFFFFF,
                                                         SupernovaStyle.SMALL_BOX_RADIUS_DP));
            else
                tab.setBackground(null);
            if (!focused)
                tab.setTextColor(open ? 0xB8FFFFFF : Color.WHITE);
            else
                tab.setTextColor(SupernovaStyle.buttonContent());
        }
    }

    private int expandedScrollTarget() {
        return Math.max(0, sectionFrame.getTop() + getPaddingTop() - Math.round(getHeight() / 6f));
    }

    /** Each tab's reading position and last focused item, so going back returns there (focus audit F4, F5). */
    private final java.util.Map<String, Integer> readingBySection = new java.util.HashMap<>();
    private final java.util.Map<String, View> focusBySection = new java.util.HashMap<>();
    private boolean watchingSections, switchingSection;

    private void watchSections() {
        if (watchingSections)
            return;
        watchingSections = true;
        getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> {
            if (!switchingSection && after != null && inside(after, lower))
                focusBySection.put(activeSection, after);
        });
    }

    private boolean focusRemembered(String label) {
        View last = focusBySection.get(label);
        return last != null && last.isAttachedToWindow() && last.isShown() && last.requestFocus();
    }

    private void showSection(String label, boolean enter) {
        watchSections();
        switchingSection = true;
        try {
            switchSection(label, enter);
        } finally {
            switchingSection = false;
            paintSectionTabs();
        }
    }

    private void switchSection(String label, boolean enter) {
        Diagnostics.uiState("details", label, "details", "none", "none",
                                                                     movie != null  ? movie.getId()
                                                                     : show != null ? show.getTvshowId()
                                                                                    : remoteId);
        if (!activeSection.equals(label)) {
            readingBySection.put(activeSection, reading.getScrollY());
            Integer offset = readingBySection.get(label);
            reading.scrollTo(0, offset == null ? 0 : offset);
            if (offset != null && offset > 0)
                reading.post(() -> reading.scrollTo(0, offset));
        }
        activeSection = label;
        reading.setVerticalScrollBarEnabled(!label.equals("Details"));
        lower.getLayoutParams().height =
            label.equals("Details") ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;
        lower.requestLayout();
        for (LinearLayout section : new LinearLayout[] {episodes, related, trailers})
            ((View)section.getParent())
                .setVisibility(label.equals(section == episodes  ? "Episodes"
                                            : section == related ? "More Like This"
                                                                 : "Extras")
                                   ? VISIBLE
                                   : GONE);
        information.setVisibility(label.equals("Details") ? VISIBLE : GONE);
        for (TextView tab : sectionTabs) {
            boolean selected = ("section:" + label).equals(tab.getTag());
            tab.setSelected(selected);
            if (selected) {
                ((PreviewToolbar)tabBar).setSelectedSegment(tab);
                sectionFrame.selected(tab);
            }
        }
        if (enter) {
            scrollTo(0, expandedScrollTarget());
            if (focusRemembered(label))
                return;
            if (label.equals("Episodes"))
                focusNextEpisode();
            else if (label.equals("Extras") && !extraCards.isEmpty())
                extraCards.get(0).requestFocus();
            else if (!label.equals("Details"))
                lower.requestFocus();
        }
    }

    /** Keep the existing growing media focus outline inside the new reading viewport. */
    private void revealContentFocus() {
        View focused = findFocus();
        if (focused == null || !inside(focused, lower))
            return;
        Rect rect = new Rect(0, 0, focused.getWidth(), focused.getHeight());
        lower.offsetDescendantRectToMyCoords(focused, rect);
        rect.inset(-dp(10), -dp(10));
        View heading = extraHeadings.get(focused);
        if (heading != null) {
            Rect header = new Rect(0, 0, heading.getWidth(), heading.getHeight());
            lower.offsetDescendantRectToMyCoords(heading, header);
            if (rect.bottom - header.top <= reading.getHeight())
                rect.top = header.top;
        }
        reading.requestChildRectangleOnScreen(lower, rect, true);
    }

    private void focusNextEpisode() {
        Snapshot cache = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
        if (cache != null && show != null) {
            List<Entry> values = new ArrayList<>();
            for (Entry e : cache.episodes)
                if (e.show == show.getTvshowId())
                    values.add(e);
            PreviewSeriesJourney.Selection selected = PreviewSeriesJourney.select(getContext(), values);
            if (selected.episode != null)
                for (PreviewEpisodeRow row : episodeRows)
                    if (row.focusEpisode(((Video)selected.episode.media).getId()))
                        return;
        }
        if (!episodeRows.isEmpty())
            episodeRows.get(0).focusRemembered();
    }

    public boolean atTop() {
        View focused = findFocus();
        return getScrollY() == 0 && focused != null && inside(focused, heroPage) && !inside(focused, tabBar);
    }

    public void focusPrimary() {
        if (play.getVisibility() == VISIBLE)
            play.requestFocus();
        else if (trailer.getVisibility() == VISIBLE)
            trailer.requestFocus();
        else if (!providerActions.requestFocus() && !sectionTabs.isEmpty())
            sectionTabs.get(0).requestFocus();
    }

    public void setVersions(int count, Runnable choose) {
        chooseVersions = choose;
        versions.setText("Versions (" + count + ")");
        versionCount = count;
        versions.setVisibility(count > 1 && movie != null ? VISIBLE : GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return super.dispatchKeyEvent(event);
        View focused = findFocus();
        int key = event.getKeyCode();
        String tag = focused == null ? "" : String.valueOf(focused.getTag());
        if (focused == play && key == KeyEvent.KEYCODE_DPAD_UP && matchMetadata.getVisibility() == VISIBLE) {
            matchMetadata.requestFocus();
            return true;
        }
        if (focused == matchMetadata) {
            if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                play.requestFocus();
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT)
                return true;
        }
        if (focused == play && key == KeyEvent.KEYCODE_DPAD_LEFT)
            return true;
        if (focused == play && key == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (providerActions.getChildCount() > 0)
                providerActions.getChildAt(0).requestFocus();
            else
                moreButton.requestFocus();
            return true;
        }
        if (focused != null && inside(focused, providerActions)) {
            if (key == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (play.getVisibility() == VISIBLE)
                    play.requestFocus();
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (moreButton.getVisibility() == VISIBLE)
                    moreButton.requestFocus();
                return true;
            }
        }
        if (focused == versions && key == KeyEvent.KEYCODE_DPAD_LEFT) {
            play.requestFocus();
            return true;
        }
        if (focused == versions && key == KeyEvent.KEYCODE_DPAD_RIGHT) {
            moreButton.requestFocus();
            return true;
        }
        if (focused == moreButton && key == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (providerActions.getChildCount() > 0)
                providerActions.getChildAt(0).requestFocus();
            else if (play.getVisibility() == VISIBLE)
                play.requestFocus();
            return true;
        }
        if (focused == moreButton && key == KeyEvent.KEYCODE_DPAD_RIGHT)
            return true;
        if (tag.equals("details.reviews")) {
            if (key == KeyEvent.KEYCODE_DPAD_UP) {
                focusSectionTab();
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_DOWN || key == KeyEvent.KEYCODE_DPAD_LEFT ||
                key == KeyEvent.KEYCODE_DPAD_RIGHT)
                return true;
        }
        if (tag.startsWith("section:")) {
            // Details tab (v20; owner, 5 Nov): Down scrolls to cast and crew, Up scrolls back, one more Up leaves.
            if (key == KeyEvent.KEYCODE_DPAD_UP && activeSection.equals("Details") && reading.getScrollY() > 0) {
                reading.scrollTo(0, 0);
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_UP) {
                focusPrimary();
                scrollTo(0, 0);
                // The taller Details tab can pull the page down again while focus settles: back to the top after.
                post(() -> scrollTo(0, 0));
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (activeSection.equals("Details")) {
                    int target = expandedScrollTarget();
                    int before = getScrollY();
                    scrollTo(0, target);
                    int readingBefore = reading.getScrollY();
                    if (before == getScrollY() && reading.getChildCount() > 0) {
                        int end = Math.max(0, reading.getChildAt(0).getHeight() - reading.getHeight()
                                              + reading.getPaddingTop() + reading.getPaddingBottom());
                        reading.scrollTo(0, end);
                    }
                    if (before == getScrollY() && readingBefore == reading.getScrollY()) {
                        View reviews = findViewWithTag("details.reviews");
                        if (reviews != null)
                            reviews.requestFocus();
                    }
                } else
                    showSection(tag.substring(8), true);
                return true;
            }
            if ((key == KeyEvent.KEYCODE_DPAD_CENTER || key == KeyEvent.KEYCODE_ENTER) &&
                activeSection.equals("Details"))
                return true;
        } else if (focused != null && inside(focused, heroPage) && key == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!sectionTabs.isEmpty()) {
                sectionTabs.get(0).requestFocus();
                return true;
            }
        } else if (focused != null && (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN)) {
            if (inside(focused, trailers)) {
                View rail = focused;
                while (rail.getParent() != trailers && rail.getParent() instanceof View)
                    rail = (View)rail.getParent();
                int index = trailers.indexOfChild(rail), next = index + (key == KeyEvent.KEYCODE_DPAD_UP ? -2 : 2);
                if (next < 0)
                    focusSectionTab();
                else if (next < trailers.getChildCount() && trailers.getChildAt(next) instanceof HorizontalScrollView) {
                    ViewGroup row = (ViewGroup)((HorizontalScrollView)trailers.getChildAt(next)).getChildAt(0);
                    View remembered = row.getTag() instanceof View ? (View)row.getTag() : row.getChildAt(0);
                    if (remembered != null)
                        remembered.requestFocus();
                }
                return true;
            }

            for (int i = 0; i < episodeRows.size(); i++)
                if (inside(focused, episodeRows.get(i))) {
                    int rowPosition = episodes.indexOfChild(episodeRows.get(i));
                    View heading = rowPosition > 0 ? episodes.getChildAt(rowPosition - 1) : null;
                    if (key == KeyEvent.KEYCODE_DPAD_UP && heading != null && heading.isFocusable()) {
                        heading.requestFocus();
                        return true;
                    }
                    int next = i + (key == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
                    if (next >= 0 && next < episodeRows.size())
                        episodeRows.get(next).focusRemembered();
                    else if (next < 0)
                        focusSectionTab();
                    return true;
                }
            if (key == KeyEvent.KEYCODE_DPAD_UP && getScrollY() > 0) {
                View next = focused.focusSearch(FOCUS_UP);
                if (next == null || !inside(next, lower)) {
                    focusSectionTab();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void focusSectionTab() {
        for (TextView tab : sectionTabs)
            if (tab.isSelected()) {
                tab.requestFocus();
                reading.scrollTo(0, 0);
                scrollTo(0, expandedScrollTarget());
                return;
            }
    }

    private boolean inside(View child, View parent) {
        while (child != null) {
            if (child == parent)
                return true;
            child = child.getParent() instanceof View ? (View)child.getParent() : null;
        }
        return false;
    }

    private int dp(int n) {
        return Math.round(n * getResources().getDisplayMetrics().density);
    }

    private TextView text(String s, int size) {
        TextView t = new TextView(getContext());
        t.setText(s);
        t.setTextColor(Color.WHITE);
        t.setTextSize(size);
        t.setIncludeFontPadding(false);
        t.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        return t;
    }

    private TextView button(String s, Runnable run) {
        TextView t = TvButton.create(getContext(), s, run, false);
        t.setTag("action:" + s);
        Diagnostics.semantic(
            t, "details.action." + s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "."));
        PreviewIcon.apply(t, s, 16);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(14), dp(9), dp(14), dp(9));
        t.setFocusable(true);
        t.setFocusableInTouchMode(true);
        // Hero buttons sit on artwork: a dark box at about 72% at rest, not solid black (D13).
        SupernovaStyle.applyArtworkButton(t);
        t.setOnClickListener(v -> run.run());
        return t;
    }

    private void margin(LinearLayout row, View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.leftMargin = dp(18);
        row.addView(view, lp);
    }

    private LinearLayout section(String name) {
        LinearLayout section = new LinearLayout(getContext());
        section.setOrientation(LinearLayout.VERTICAL);
        // DT3 (owner, 18 Nov 2026): no invisible frame. A grown card and its ring may reach past the row's sides,
        // and rows run off the page's edges (D6), so neither the section nor its content clips its children.
        section.setClipChildren(false);
        section.setClipToPadding(false);
        section.setPadding(0, dp(10), 0, dp(8));
        TextView label = text(name, 16);
        label.setTextColor(0xff9ed4f7);
        section.addView(label);
        if (name.equals("More Like This") || name.equals("Details") || name.equals("Episodes") || name.equals("Extras"))
            label.setVisibility(GONE);
        LinearLayout content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setClipChildren(false);
        content.setClipToPadding(false);
        content.setPadding(0, dp(6), 0, 0);
        section.addView(content);
        body.addView(section);
        return content;
    }

    private void freshTitle(String identity) {
        if (identity.equals(boundTitle))
            return;
        boundTitle = identity;
        providerSignature = "";
        openedAt = SystemClock.elapsedRealtime();
        activeSection = "Details";
        scrollTo(0, 0);
        enrichmentGeneration++;
        if (enrichmentTask != null)
            enrichmentTask.cancel(true);
        enrichmentTask = null;
        enrichmentKey = "";
        enrichment = null;
        if (reviews != null) {
            reviews.close();
            reviews = null;
        }
        plot.setText("");
        for (PreviewLandscapeCard card : landscapeCards)
            card.release();
        landscapeCards.clear();
        for (PreviewLandscapeCard card : extraCards)
            card.release();
        extraCards.clear();
        extraHeadings.clear();
        related.removeAllViews();
        trailers.removeAllViews();
        episodes.removeAllViews();
        episodeRows.clear();
        movie = null;
        show = null;
        tags = null;
        remoteDetails = null;
        remoteKind = null;
        remoteId = 0;
        latestSummary = null;
        informationSignature = peopleSignature = receptionSignature = summarySignature = "";
        informationPanel = null;
        artworkUri = null;
        failedArtworkUri = null;
        artworkRetryAfter = 0;
        artworkReady = false;
        if (artworkLoader != null)
            artworkLoader.cancelRequest(cardArtwork);
        cardArtwork.setImageDrawable(null);
        cast.removeAllViews();
        crew.removeAllViews();
        portraits.clear();
        trailerList = Collections.emptyList();
        seasons.clear();
    }

    public void bind(Video value) {
        seasonNumber = -1;
        freshTitle("video:" + value.getId() + ":" + (value instanceof Movie ? ((Movie)value).getOnlineId() : 0));
        movie = value;
        SubtitleDiscovery.details(
            getContext(), value, snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache());
        title.setText(movie instanceof Episode
                          ? safe(((Episode)movie).getShowName()) + " — " + safe(((Episode)movie).getEpisodeName())
                          : movie.getName());
        OfficialTitleArtwork.bind(title, value, false);
        if (plot.length() == 0 || !safe(movie.getDescriptionBody()).isEmpty())
            plot.setText(movie.getDescriptionBody());
        if (!movie.hasScraperData()) {
            if (safe(movie.getName()).isEmpty())
                title.setText(movie.getFilenameNonCryptic());
            plot.setText("Not matched · Synopsis unavailable");
        }
        play.setText(movie.getResumeMs() > 0 && !PreviewSeriesJourney.completed(movie) ? "Resume" : "Play");
        if (movie.getPreviewBackdrop() != null)
            cardArtwork(movie.getPreviewBackdrop());
        renderDetails();
        rebuildTabs();
        requestEnrichment();
    }

    public void setTags(BaseTags value, List<ScraperTrailer> videos, List<ScraperImage> backdrops) {
        View oldCast = cast.findFocus(), oldCrew = crew.findFocus();
        Object castKey = oldCast == null ? null : oldCast.getTag(), crewKey = oldCrew == null ? null : oldCrew.getTag();
        int oldY = getScrollY();
        if (value != null)
            tags = value;
        if (videos != null && !videos.isEmpty())
            trailerList = videos;
        ScraperImage selectedBackdrop = tags == null ? null : tags.getDefaultBackdrop();
        if (selectedBackdrop == null && backdrops != null && !backdrops.isEmpty())
            selectedBackdrop = backdrops.get(0);
        if (selectedBackdrop != null) {
            File file = selectedBackdrop.getLargeFileF();
            Uri uri = file != null && file.exists()            ? Uri.fromFile(file)
                      : selectedBackdrop.getLargeUrl() == null ? null
                                                               : Uri.parse(selectedBackdrop.getLargeUrl());
            if (uri != null) {
                cardArtwork(uri);
                if (movie != null)
                    movie.setPreviewBackdrop(uri.toString());
            }
        }
        if (enrichment == null || enrichment.credits == null) {
            portraits.clear();
            cast.removeAllViews();
            LinearLayout people = new DetailsPeopleGrid(getContext());
            cast.addView(people, new LinearLayout.LayoutParams(-1, -1));
            crew.removeAllViews();
            LinearLayout crewPeople = new DetailsPeopleGrid(getContext());
            crew.addView(crewPeople, new LinearLayout.LayoutParams(-1, -1));
            if (tags != null) {
                for (Map.Entry<String, String> person : tags.getActors().entrySet()) {
                    if (people.getChildCount() >= 8)
                        break;
                    person(people, person.getKey(), person.getValue());
                }
                if (tags.getDirectors() != null)
                    for (String name : tags.getDirectors())
                        if (!safe(name).isEmpty() && crewPeople.getChildCount() < 8)
                            person(crewPeople, name, "Director");
                if (tags.getWriters() != null)
                    for (String name : tags.getWriters())
                        if (!safe(name).isEmpty() && crewPeople.getChildCount() < 8)
                            person(crewPeople, name, "Writer");
            }
        }
        trailer.setVisibility(GONE);
        renderExtras();
        rebuildTabs();
        PreviewPeople.load(getContext().getApplicationContext(), tags, new HashMap<>(portraits));
        renderEnrichedPeople();
        renderDetails();
        renderRelated();
        requestEnrichment();
        restoreRowFocus(cast, castKey, oldY);
        restoreRowFocus(crew, crewKey, oldY);
    }

    public void setSnapshot(Snapshot value) {
        snapshot = value;
        renderDetails("indexed_snapshot");
        renderRelated();
        requestEnrichment();
    }

    public void setSeriesPlaybackTarget(PreviewSeriesJourney.Selection selection) {
        play.setText(PreviewSeriesJourney.playLabel(selection));
    }

    private void renderDetails() {
        renderDetails("information_refresh");
    }

    /** The description in two lines (one on a compact card); never the tagline (D10, page sheet). */
    private void updateHeroLine() {
        if (heroLine == null)
            return;
        heroLine.setText(plot.getText());
        heroLine.setMaxLines(compactCard ? 1 : 2);
        heroLine.setVisibility(heroLine.length() == 0 ? GONE : VISIBLE);
    }

    static final int TITLE_BOX_DP = 96;

    /**
     * Shows or hides the next-episode label. Its line is taken from the title box above the facts line, so the facts
     * line and the buttons never move (placement A).
     */
    private void bindNextEpisode(JSONObject enriched) {
        boolean series = show != null || movie instanceof Episode || "tv".equals(remoteKind);
        String words = !series || enriched == null ? null
            : NextEpisodeLabel.words(enriched,
                                     enrichment == null ? null : NextEpisodeLabel.airDates(enrichment.episodes.values()),
                                     java.time.LocalDate.now());
        NextEpisodeLabel.show(nextEpisode, words);
        int box = dp(TITLE_BOX_DP) - (words == null ? 0 : NextEpisodeLabel.lineHeight(getContext()));
        ViewGroup.LayoutParams size = title.getLayoutParams();
        if (size != null && size.height != box) {
            size.height = box;
            title.setLayoutParams(size);
        }
    }

    private void renderDetails(String reason) {
        updateHeroLine();
        int previous = details.getChildCount();
        View focused = details.findFocus();
        Object focusKey = focused == null ? null : focused.getTag();
        int oldY = getScrollY();
        observeActions();
        if (movie == null && show == null && remoteDetails == null)
            return;
        String synopsis = String.valueOf(plot.getText());
        LinearLayout panels = new LinearLayout(getContext());
        LinearLayout key = factPanel(panels, "Key Information"), reception = factPanel(panels, "Reception"),
                     technical =
                         factPanel(panels, remoteDetails != null ? "Streaming Availability" : "Technical Information");
        JSONObject enriched = enrichment != null ? enrichment.details : remoteDetails;
        bindNextEpisode(enriched);
        Episode selectedEpisode = movie instanceof Episode ? (Episode)movie : null;
        JSONObject episodeFacts =
            selectedEpisode != null && enrichment != null
                ? PreviewDetailsFacts.episode(enrichment.episodes, selectedEpisode.getSeasonNumber(),
                                              selectedEpisode.getEpisodeNumber())
                : null;
        long episodeDate = selectedEpisode == null ? 0 : selectedEpisode.getEpisodeDate();
        if (episodeDate <= 0 && tags instanceof EpisodeTags && ((EpisodeTags)tags).getAired() != null)
            episodeDate = ((EpisodeTags)tags).getAired().getTime();
        if (movie != null && !movie.hasScraperData())
            fact(key, "Metadata", "Not matched");
        String genre = tags instanceof VideoTags ? ((VideoTags)tags).getGenresFormatted() : "";
        if (safe(genre).isEmpty() && tags instanceof EpisodeTags) {
            ShowTags parent = ((EpisodeTags)tags).getShowTags();
            if (parent != null)
                genre = parent.getGenresFormatted();
        }
        if (safe(genre).isEmpty() && enriched != null)
            genre = jsonNames(enriched.optJSONArray("genres"), "name");
        if (safe(genre).isEmpty()) {
            Snapshot local = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
            if (local != null)
                for (Entry entry : movie != null ? local.technical : local.shows)
                    if (movie != null && entry.media instanceof Video &&
                            ((Video)entry.media).getId() == movie.getId() ||
                        show != null && entry.show == show.getTvshowId()) {
                        genre = entry.genres.replace('|', ',');
                        break;
                    }
        }
        // Title line under the logo, the same as the player (owner, 12 Nov 2026; notes §29):
        // "Season 2" or "Season 2 · Episode 8 · We Don't Talk Anymore".
        context.setText(seasonNumber >= 0 ? "Season " + seasonNumber
                        : selectedEpisode == null ? "" : episodeTitleLine(selectedEpisode));
        context.setVisibility(selectedEpisode == null && seasonNumber < 0 ? GONE : VISIBLE);
        int year = movie instanceof Movie ? ((Movie)movie).getYear() : show != null ? show.getYear() : 0;
        if (selectedEpisode != null)
            year = episodeDate > 0 ? PreviewDetailsFacts.dateYear(episodeDate) : PreviewDetailsFacts.year(episodeFacts);
        else if (year <= 0 || seasonNumber >= 0)
            year = PreviewDetailsFacts.year(enriched);
        if (year > 0)
            fact(key, "Year", String.valueOf(year));
        long runtime = movie == null ? 0 : movie.getDurationMs() / 60000;
        if (runtime <= 0 && selectedEpisode != null && tags instanceof EpisodeTags)
            runtime = tags.getRuntime(TimeUnit.MINUTES);
        if (runtime <= 0) {
            JSONObject runtimeFacts = selectedEpisode != null ? episodeFacts : enriched;
            if (runtimeFacts != null)
                runtime = runtimeFacts.optLong("runtime");
        }
        if (runtime > 0 && (movie != null || "movie".equals(remoteKind)))
            fact(key, "Runtime", SharedMetadata.runtime(runtime));
        else if (movie == null) {
            String episodeRuntime = PreviewDetailsFacts.episodeRuntimes(enriched);
            // TMDB often leaves a show's runtime empty: take it from its episodes instead (D15).
            if (episodeRuntime.isEmpty() && enrichment != null)
                episodeRuntime = PreviewDetailsFacts.typicalRuntime(enrichment.episodes.values());
            if (!episodeRuntime.isEmpty())
                fact(key, "Runtime", "Episodes · " + episodeRuntime);
        }
        if (enriched != null && (show != null || "tv".equals(remoteKind)) && selectedEpisode == null)
            fact(key, "Created by", jsonNames(enriched.optJSONArray("created_by"), "name"));

        String certificate =
            selectedEpisode == null && seasonNumber < 0 && enrichment != null
                ? SharedMetadata.certificate(enrichment.classification, StreamingRepository.country(getContext()))
                : "";
        // The show's rating, else its first episode's (D11, notes §29); a movie's own stored rating last.
        if (safe(certificate).isEmpty() && selectedEpisode == null && seasonNumber < 0)
            certificate = storedRating();

        fact(key, "Age rating", certificate);
        fact(key, "Genres", genre);
        String studios = tags instanceof MovieTags  ? ((MovieTags)tags).getStudiosFormatted()
                         : tags instanceof ShowTags ? ((ShowTags)tags).getStudiosFormatted()
                         : tags instanceof EpisodeTags && ((EpisodeTags)tags).getShowTags() != null
                             ? ((EpisodeTags)tags).getShowTags().getStudiosFormatted()
                             : "";
        if (safe(studios).isEmpty() && enriched != null)
            studios = jsonNames(
                enriched.optJSONArray(show != null || "tv".equals(remoteKind) ? "networks" : "production_companies"),
                "name");
        fact(key, "Studio / Network", studios);
        float rating = selectedEpisode != null  ? (tags instanceof EpisodeTags ? ((EpisodeTags)tags).getRating()
                                                                               : selectedEpisode.getEpisodeRating())
                       : movie instanceof Movie ? ((Movie)movie).getRating()
                       : show == null           ? 0
                                                : show.getRating();
        if (seasonNumber >= 0)
            rating = 0;
        if (rating <= 0 && selectedEpisode != null)
            rating = selectedEpisode.getEpisodeRating();
        boolean tmdbRating = episodeFacts != null && episodeFacts.optDouble("vote_average", 0) > 0 ||
                             selectedEpisode == null && enriched != null && enriched.optDouble("vote_average", 0) > 0;
        if (episodeFacts != null && episodeFacts.optDouble("vote_average", 0) > 0)
            rating = (float)episodeFacts.optDouble("vote_average", 0);
        if (enriched != null && !(movie instanceof Episode) && enriched.optDouble("vote_average", 0) > 0)
            rating = (float)enriched.optDouble("vote_average");
        if (rating > 0 && rating <= 10)
            fact(reception, tmdbRating ? "TMDB Rating" : "Indexed Rating",
                 String.format(Locale.UK, tmdbRating ? "%.1f / 10" : "%.1f", rating));
        if (selectedEpisode != null)
            fact(key, "Air date",
                 episodeDate > 0        ? PreviewDetailsFacts.date(episodeDate)
                 : episodeFacts == null ? ""
                                        : episodeFacts.optString("air_date"));
        if (enriched != null) {
            if (selectedEpisode == null)
                fact(key, "Release / first air date",
                     enriched.optString("release_date", enriched.optString("first_air_date")));
            fact(key, selectedEpisode != null ? "Series original title" : "Original title",
                 enriched.optString("original_title", enriched.optString("original_name")));
            if (show != null || "tv".equals(remoteKind)) {
                fact(key, "Last air date", enriched.optString("last_air_date"));
                fact(key, "Status", NextEpisodeLabel.status(enriched, java.time.LocalDate.now()));
            }
            fact(key, "Countries", jsonNames(enriched.optJSONArray("production_countries"), "name"));
            JSONObject collection = enriched.optJSONObject("belongs_to_collection");
            if (collection != null)
                fact(key, "Collection", collection.optString("name"));
            for (String field : new String[] {"budget", "revenue"})
                if (enriched.optLong(field) > 0)
                    fact(key, field.equals("budget") ? "Budget (TMDb)" : "Box office (TMDb)",
                         SharedMetadata.money(enriched.optLong(field)));
            if (enriched.optLong("vote_count") > 0 && !(movie instanceof Episode))
                fact(reception, "TMDb votes",
                     NumberFormat.getIntegerInstance().format(enriched.optLong("vote_count")));
            if (episodeFacts != null && episodeFacts.optLong("vote_count") > 0)
                fact(reception, "TMDb votes",
                     NumberFormat.getIntegerInstance().format(episodeFacts.optLong("vote_count")));
        }
        if (movie != null) {
            if (movie.getMeasuredWidth() > 0 && movie.getMeasuredHeight() > 0)
                fact(technical, "Resolution", movie.getMeasuredWidth() + " × " + movie.getMeasuredHeight());
            fact(technical, "Video codec", PreviewMediaInfo.format(movie.getCalculatedVideoFormat()));
            fact(technical, "Audio", PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat()));
            fact(technical, "Dynamic range", PreviewVariants.dynamicRange(getContext(), movie));
            fact(technical, "Source", source(movie.getFileUri()));
            if (movie.getSize() > 0)
                fact(technical, "File size",
                     Formatter.formatShortFileSize(getContext(), movie.getSize()));
            fact(technical, "Selected file", movie.getFilenameNonCryptic());
            fact(technical, "Location", PreviewVariants.safeLocation(movie.getFileUri()));
            String filename = movie.getFilenameNonCryptic();
            int extension = filename == null ? -1 : filename.lastIndexOf('.');
            if (extension >= 0 && filename.length() - extension <= 6)
                fact(technical, "Container", filename.substring(extension + 1).toUpperCase(Locale.ROOT));
            if (movie.getMetadata() != null) {
                VideoMetadata data = movie.getMetadata();
                VideoMetadata.VideoTrack video = data.getVideoTrack();
                if (video != null) {
                    if (video.bitRate > 0)
                        fact(technical, "Video bitrate", video.bitRate + " kb/s");
                    if (video.fpsScale > 0)
                        fact(technical, "Frame rate",
                             String.format(Locale.UK, "%.3f fps", video.fpsRate / (double)video.fpsScale));
                }
                LinkedHashSet<String> subtitles = new LinkedHashSet<>();
                LinkedHashSet<String> audioTracks = new LinkedHashSet<>();
                for (int n = 0; n < data.getAudioTrackNb(); n++) {
                    VideoMetadata.AudioTrack track = data.getAudioTrack(n);
                    if (track != null) {
                        String format = PreviewMediaInfo.audioTrack(track.format, track.channels, track.sampleRate);
                        if (!format.isEmpty())
                            audioTracks.add(format);
                    }
                }
                fact(technical, "Audio tracks", TextUtils.join("\n", audioTracks));
                for (int n = 0; n < data.getSubtitleTrackNb(); n++) {
                    VideoMetadata.SubtitleTrack track = data.getSubtitleTrack(n);
                    if (track != null && !safe(track.language).isEmpty())
                        subtitles.add(SharedMetadata.languageName(track.language));
                }
                fact(technical, "Subtitles", TextUtils.join(", ", subtitles));
            }
        } else if (show != null) {
            Snapshot library = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
            if (library != null) {
                Set<Long> physical = new HashSet<>();
                Set<String> logical = new HashSet<>();
                Set<Integer> availableSeasons = new HashSet<>();
                long bytes = 0;
                int specials = 0, knownSizes = 0;
                Set<String> locations = new TreeSet<>(), audioFormats = new TreeSet<>(), containers = new TreeSet<>();
                long firstAdded = Long.MAX_VALUE;
                Map<String, Integer> formats = new TreeMap<>();
                for (Entry entry : library.episodes)
                    if (entry.show == show.getTvshowId() && entry.media instanceof Episode &&
                        (seasonNumber < 0 || ((Episode)entry.media).getSeasonNumber() == seasonNumber)) {
                        if (!entry.audio.isEmpty())
                            audioFormats.add(entry.audio);
                        if (!entry.container.isEmpty())
                            containers.add(entry.container);
                        if (entry.added > 0)
                            firstAdded = Math.min(firstAdded, entry.added);
                        Episode episode = (Episode)entry.media;
                        logical.add(episode.getSeasonNumber() + ":" + episode.getEpisodeNumber());
                        availableSeasons.add(episode.getSeasonNumber());
                        if (physical.add(episode.getId())) {
                            bytes += Math.max(0, episode.getSize());
                            if (episode.getSize() > 0)
                                knownSizes++;
                            if (episode.getSeasonNumber() == 0)
                                specials++;
                            Uri uri = episode.getFileUri();
                            if (uri != null) {
                                String path = uri.getPath();
                                int slash = path == null ? -1 : path.lastIndexOf('/');
                                locations.add(
                                    PreviewVariants.safeLocation(uri.buildUpon()
                                                                     .path(slash >= 0 ? path.substring(0, slash) : "")
                                                                     .clearQuery()
                                                                     .fragment(null)
                                                                     .build()));
                            }
                            String format = (entry.resolution + " " + entry.codec + " " + entry.hdr).trim();
                            if (!format.isEmpty())
                                formats.put(format, formats.getOrDefault(format, 0) + 1);
                        }
                    }
                fact(technical, "Local episodes", String.valueOf(logical.size()));
                fact(technical, "Local seasons", String.valueOf(availableSeasons.size()));
                if (bytes > 0)
                    fact(technical, "Library size",
                         (knownSizes < physical.size() ? "≥ " : "") +
                             Formatter.formatShortFileSize(getContext(), bytes));
                if (!physical.isEmpty() && knownSizes == physical.size())
                    fact(technical, "Average file size",
                         Formatter.formatShortFileSize(getContext(), bytes / physical.size()));
                if (specials > 0)
                    fact(technical, "Specials (files)", String.valueOf(specials));
                if (enriched != null && enriched.optInt("number_of_episodes") > 0)
                    fact(technical, "Series episodes (metadata)",
                         String.valueOf(enriched.optInt("number_of_episodes")));
                List<String> formatCounts = new ArrayList<>();
                for (Map.Entry<String, Integer> format : formats.entrySet())
                    formatCounts.add(format.getKey() + ": " + format.getValue() + " files");
                fact(technical, "File formats", TextUtils.join(", ", formatCounts));
                fact(technical, "Storage locations", TextUtils.join("\n", locations));
                fact(technical, "Location", TextUtils.join(", ", locations));
                fact(technical, "Audio", TextUtils.join(", ", audioFormats));
                fact(technical, "Container", TextUtils.join(", ", containers));
                if (firstAdded != Long.MAX_VALUE)
                    fact(technical, "Added",
                         new java.text.SimpleDateFormat("d MMM yyyy", Locale.UK)
                             .format(new Date(firstAdded * 1000L)));
            }
        } else {
            fact(technical, "Region", StreamingRepository.country(getContext()));
            if (observedActions != null)
                for (int n = 0; n < observedActions.size(); n++) {
                    Object item = observedActions.get(n);
                    if (item instanceof Action &&
                        StreamingActions.isAvailableOffer((Action)item))
                        fact(technical, "Available on", String.valueOf(((Action)item).getLabel1()));
                }
        }
        if (enriched != null) {
            fact(key, "Language", jsonNames(enriched.optJSONArray("spoken_languages"), "english_name"));
            fact(key, "Alternative names",
                 PreviewDetailsFacts.alternativeNames(enrichment == null ? null : enrichment.alternatives,
                                                      StreamingRepository.country(getContext())));
        }
        if (movie != null) {
            Snapshot local = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
            if (local != null)
                for (Entry entry : local.technical)
                    if (entry.media instanceof Video && ((Video)entry.media).getId() == movie.getId() &&
                        entry.added > 0) {
                        fact(technical, "Added",
                             new java.text.SimpleDateFormat("d MMM yyyy", Locale.UK)
                                 .format(new Date(entry.added * 1000L)));
                        break;
                    }
        }
        // Each technical fact has its own row on the Details tab (v20), so nothing is merged here.
        Map<String, String> technicalFacts = DetailsBlueprintPanel.facts(technical);
        if (technicalFacts.containsKey("Average file size"))
            technicalFacts.put("Library size", technicalFacts.get("Library size") + " · avg " +
                                                   technicalFacts.get("Average file size"));
        Map<String, String> keyFacts = DetailsBlueprintPanel.facts(key), receptionFacts =
                                                                             DetailsBlueprintPanel.facts(reception);
        OmdbSupplement.Ratings supplied =
            enrichment != null && selectedEpisode == null && seasonNumber < 0
                ? enrichment.supplementary
                : OmdbSupplement.Ratings.EMPTY;
        String receptionKey = new TreeMap<>(receptionFacts).toString() + supplied.countsSignature();
        for (int slot = 0; slot < 4; slot++)
            receptionKey += "|" + supplied.value(slot);
        String signature = synopsis + new TreeMap<>(keyFacts) + new TreeMap<>(technicalFacts) +
                           new TreeMap<>(receptionFacts) + supplied.countsSignature();
        for (int slot = 0; slot < 4; slot++)
            signature += "|" + supplied.value(slot);
        if (!signature.equals(informationSignature)) {
            informationSignature = signature;
            Diagnostics.uiRebuild(details, "details.information", reason,
                                                                           previous, 1, false);
            if (informationPanel != null && receptionKey.equals(receptionSignature)) {
                informationPanel.updateText(synopsis, keyFacts, technicalFacts);
            } else {
                DetailsBlueprintPanel blueprint = new DetailsBlueprintPanel(
                    getContext(), synopsis, keyFacts, technicalFacts, receptionFacts, cast, crew, supplied);
                details.removeAllViews();
                details.addView(blueprint, new LinearLayout.LayoutParams(-1, -1));
                informationPanel = blueprint;
                receptionSignature = receptionKey;
            }
            Diagnostics.event(
                "details_information_ready", "media_id", diagnosticMedia(), "elapsed_ms",
                SystemClock.elapsedRealtime() - openedAt);
        }
        reception.setVisibility(reception.getChildCount() > 1 ? VISIBLE : GONE);
        technical.setVisibility(technical.getChildCount() > 1 ? VISIBLE : GONE);
        ((View)details.getParent()).setVisibility(VISIBLE);
        SharedMetadata.Facts summary = new SharedMetadata.Facts();
        summary.certificate = selectedEpisode == null ? safe(certificate) : "";
        summary.year = year > 0 ? String.valueOf(year) : "";
        summary.genres = safe(genre);
        summary.rating = tmdbRating ? rating : 0;
        JSONObject votesSource = selectedEpisode == null ? enriched : episodeFacts;
        summary.votes = votesSource == null ? 0 : votesSource.optLong("vote_count");
        if (show != null || "tv".equals(remoteKind)) {
            Snapshot library = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
            Entry local = null;
            if (library != null && show != null)
                for (Entry e : library.shows)
                    if (e.show == show.getTvshowId())
                        local = e;
            int seasonCount = local != null      ? local.seasons
                              : enriched == null ? (show == null ? 0 : show.getSeasonCount())
                                                 : Math.max(enriched.optInt("number_of_seasons"),
                                                            show == null ? 0 : show.getSeasonCount()),
                episodeCount = local != null      ? local.episodes
                               : enriched == null ? (show == null ? 0 : show.getEpisodeCount())
                                                  : Math.max(enriched.optInt("number_of_episodes"),
                                                             show == null ? 0 : show.getEpisodeCount());
            if (seasonNumber >= 0) {
                Set<Integer> localEpisodes = new HashSet<>();
                if (library != null)
                    for (Entry e : library.episodes)
                        if (e.show == show.getTvshowId() && ((Episode)e.media).getSeasonNumber() == seasonNumber)
                            localEpisodes.add(((Episode)e.media).getEpisodeNumber());
                summary.context = SharedMetadata.count(localEpisodes.size(), "Episode");
            } else
                summary.context = SharedMetadata.join(SharedMetadata.count(seasonCount, "Season"),
                                                      SharedMetadata.count(episodeCount, "Episode"));
        } else
            summary.context = SharedMetadata.runtime(runtime);
        if (movie != null)
            summary.technical.addAll(SharedMetadata.technical(getContext(), movie));
        latestSummary = summary;
        bindSummary();
        if (focused != null && !focused.hasFocus())
            restoreRowFocus(details, focusKey, oldY);
    }

    private String summarySignature = "";

    private void bindSummary() {
        SharedMetadata.Facts summary = latestSummary.copy();
        if (compactCard && movie instanceof Episode) {
            Episode episode = (Episode)movie;
            summary.context = SharedMetadata.join("S" + episode.getSeasonNumber() + " E" + episode.getEpisodeNumber(),
                                                  summary.context);
            context.setVisibility(GONE);
        } else
            context.setVisibility(movie instanceof Episode || seasonNumber >= 0 ? VISIBLE : GONE);
        SharedMetadata.Facts cardSummary = SharedMetadata.cardFacts(summary, false);
        String signature = compactCard + "|" + SharedMetadata.primary(cardSummary) + cardSummary.technical;
        if (!signature.equals(summarySignature)) {
            summarySignature = signature;
            SharedMetadata.bindCard(meta, pills, summary, false);
        }
        updateHeroLine();
    }

    private static String jsonNames(JSONArray values, String key) {
        if (values == null)
            return "";
        List<String> names = new ArrayList<>();
        for (int n = 0; n < values.length(); n++) {
            JSONObject item = values.optJSONObject(n);
            if (item != null && !item.optString(key).isEmpty())
                names.add(item.optString(key));
        }
        return TextUtils.join(", ", names);
    }

    private LinearLayout factPanel(LinearLayout panels, String name) {
        LinearLayout panel = new LinearLayout(getContext());
        panel.setTag("semantic:details.panel." + name.toLowerCase(Locale.ROOT).replace(' ', '.'));
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(6), dp(6), dp(6), dp(6));
        panel.setBackground(PreviewDialog.surface(getContext(), false));
        panel.setFocusable(false);
        panel.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1);
        size.rightMargin = dp(8);
        panels.addView(panel, size);
        panel.addView(text(name, 15));
        return panel;
    }

    private void fact(LinearLayout panel, String label, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        // Dates in words, never numbers (D14, RULES §6): "2015-06-24" → "24 June 2015".
        value = PreviewDetailsFacts.words(value);
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setContentDescription(label + ": " + value);
        TextView name = text(label, 12), content = text(value.replace("\n", ", "), 13);
        name.setTextColor(0xffb9c7d2);
        name.setSingleLine(false);
        content.setSingleLine(false);
        row.addView(name, new LinearLayout.LayoutParams(0, -2, .44f));
        LinearLayout.LayoutParams valueSize = new LinearLayout.LayoutParams(0, -2, .56f);
        valueSize.leftMargin = dp(8);
        row.addView(content, valueSize);
        panel.addView(row);
    }

    private void showReviews() {
        if (reviews != null)
            reviews.close();
        String kind = remoteDetails != null ? remoteKind : show != null ? "tv" : "movie";
        reviews = new DetailsReviews(getContext(), kind, metadataIdentity());
        reviews.show();
    }

    /** A gentle shade over a backdrop that is too bright behind the text (calmer hero backdrops, first version). */
    private void calmBackdrop() {
        Drawable shown = cardArtwork.getDrawable();
        if (!(shown instanceof android.graphics.drawable.BitmapDrawable))
            return;
        android.graphics.Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable)shown).getBitmap();
        if (bitmap == null || bitmap.isRecycled())
            return;
        android.graphics.Bitmap small = android.graphics.Bitmap.createScaledBitmap(bitmap, 96, 54, true);
        int[] pixels = new int[96 * 54];
        small.getPixels(pixels, 0, 96, 0, 0, 96, 54);
        if (small != bitmap)
            small.recycle();
        boolean shade = BackdropCalm.needsShade(BackdropCalm.leftBrightness(pixels, 96, 54));
        cardArtwork.setColorFilter(shade ? 0x40000000 : 0, android.graphics.PorterDuff.Mode.SRC_ATOP);
    }

    private void cardArtwork(Uri uri) {
        if (uri == null || uri.toString().equals(artworkUri) ||
            uri.toString().equals(failedArtworkUri) && SystemClock.elapsedRealtime() < artworkRetryAfter)
            return;
        artworkUri = uri.toString();
        String identity = boundTitle, requested = artworkUri;
        artworkLoader = Picasso.get();
        // Owner report (notes #9): no flat card while the backdrop loads. First show the copy the Home hero already
        // decoded (same Picasso key: hero size, centre-crop; memory/disk only), then load the full-size image with
        // whatever is showing as its placeholder, so the card never goes blank and the sharp one fades in.
        int heroWidth = Math.round(960 * getResources().getDisplayMetrics().density),
            heroHeight = Math.round(540 * getResources().getDisplayMetrics().density);
        RequestCreator quick = artworkLoader.load(uri)
            .resize(heroWidth, heroHeight)
            .centerCrop()
            .networkPolicy(NetworkPolicy.OFFLINE)
            .noFade();
        // Keep whatever is already showing while the quick copy is looked up; never blank the card.
        Drawable showing = cardArtwork.getDrawable();
        if (showing != null)
            quick.placeholder(showing);
        else
            quick.noPlaceholder();
        quick.into(cardArtwork, new Callback() {
                public void onSuccess() {
                    // Decide the shade on the first copy, so the picture does not darken a moment later.
                    calmBackdrop();
                    fullArtwork(uri, identity, requested);
                }
                public void onError(Exception error) {
                    fullArtwork(uri, identity, requested);
                }
            });
    }

    private void fullArtwork(Uri uri, String identity, String requested) {
        if (!identity.equals(boundTitle) || !requested.equals(artworkUri))
            return;
        Drawable showing = cardArtwork.getDrawable();
        RequestCreator full = artworkLoader.load(uri).fit().centerCrop();
        // With a copy already showing, the sharp one replaces it without a second fade (0.157 Shield review #6).
        if (showing != null)
            full.placeholder(showing).noFade();
        else
            full.noPlaceholder();
        full.into(
            cardArtwork, new Callback() {
                public void onSuccess() {
                    if (!identity.equals(boundTitle) || !requested.equals(artworkUri))
                        return;
                    artworkReady = true;
                    failedArtworkUri = null;
                    calmBackdrop();
                    Diagnostics.event(
                        "details_artwork_ready", "media_id", diagnosticMedia(), "elapsed_ms",
                        SystemClock.elapsedRealtime() - openedAt);
                }
                public void onError(Exception error) {
                    if (identity.equals(boundTitle) && requested.equals(artworkUri)) {
                        artworkUri = null;
                        failedArtworkUri = requested;
                        artworkRetryAfter = SystemClock.elapsedRealtime() + 30000;
                        Diagnostics.event("details_artwork_unavailable",
                                                                                   "media_id", diagnosticMedia());
                    }
                }
            });
    }

    private void observeActions() {
        ObjectAdapter next = actions.get();
        if (next != observedActions) {
            if (observedActions != null)
                observedActions.unregisterObserver(actionObserver);
            observedActions = next;
            if (next != null)
                next.registerObserver(actionObserver);
            renderProviders();
        }
    }

    private void renderProviders() {
        refreshMoreRows();
        matchMetadata.setVisibility(movie != null && !movie.hasScraperData() && matchingAction() != null ? VISIBLE
                                                                                                         : GONE);
        moreButton.setVisibility(remoteDetails == null ? VISIBLE : GONE);
        View focused = providerActions.findFocus();
        Object focusKey = focused == null ? null : focused.getTag();
        // The hero's buttons are rebuilt only when what they offer changes; rebuilding the same buttons on every
        // data update made them flicker while the page loaded (0.157 Shield review #6).
        String signature = providerSignature();
        if (signature.equals(providerSignature) && providerActions.getChildCount() > 0)
            return;
        providerSignature = signature;
        for (Presenter.ViewHolder holder : providerHolders)
            providerPresenter.onUnbindViewHolder(holder);
        providerHolders.clear();
        providerActions.removeAllViews();
        if (observedActions == null) {
            if (focused != null)
                play.requestFocus();
            return;
        }
        List<Action> offers = new ArrayList<>(), extraOptions = new ArrayList<>();
        for (int i = 0; i < observedActions.size(); i++) {
            Object item = observedActions.get(i);
            if (item instanceof Action &&
                StreamingActions.isAvailableOffer((Action)item))
                offers.add((Action)item);
            else if (item instanceof Action && "•••".contentEquals(((Action)item).getLabel1()))
                extraOptions.add((Action)item);
        }
        play.setVisibility(remoteDetails == null ? VISIBLE : GONE);
        if (!offers.isEmpty()) {
            Action offer = offers.get(0);
            // Approved 2 Nov 2026: provider name + one generic "open externally" icon, same size and style as Resume/More.
            String provider =
                offer instanceof StreamingActionPresenter.LogoAction
                    ? ((StreamingActionPresenter.LogoAction)offer).provider.name
                    : String.valueOf(offer.getLabel1());
            TextView open = button(provider, () -> action.accept(offer));
            PreviewIcon.apply(open, "external", 16);
            SupernovaStyle.tintIcons(open);
            open.setTag(offer.getId());
            open.setContentDescription("Open " + provider);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.leftMargin = dp(18);
            providerActions.addView(open, lp);
        }
        if (focused != null) {
            View replacement = focusKey == null ? null : providerActions.findViewWithTag(focusKey);
            if (replacement != null)
                replacement.requestFocus();
            else if (providerActions.getChildCount() > 0)
                providerActions.getChildAt(0).requestFocus();
            else
                play.requestFocus();
        }
    }

    private String providerSignature = "";

    /** What the hero's provider buttons show: the offers' ids and names, in order. */
    private String providerSignature() {
        if (observedActions == null)
            return "none";
        StringBuilder key = new StringBuilder(remoteDetails == null ? "local" : "remote");
        for (int i = 0; i < observedActions.size(); i++) {
            Object item = observedActions.get(i);
            if (item instanceof Action && StreamingActions.isAvailableOffer((Action) item))
                key.append('|').append(((Action) item).getId()).append(':').append(((Action) item).getLabel1());
        }
        return key.toString();
    }

    private Action matchingAction() {
        ObjectAdapter adapter = actions.get();
        if (adapter != null)
            for (int i = 0; i < adapter.size(); i++) {
                Object item = adapter.get(i);
                if (item instanceof Action && ((Action)item).getId() == VideoActionAdapter.ACTION_SCRAP)
                    return (Action)item;
            }
        return null;
    }

    static String episodeTitleLine(Episode episode) {
        String line = "Season " + episode.getSeasonNumber() + " · Episode " + episode.getEpisodeNumber();
        String name = episode.getEpisodeName();
        return name == null || name.trim().isEmpty() ? line : line + " · " + name.trim();
    }

    private void pill(String value) {
        if (value == null || value.isEmpty())
            return;
        TextView label = text(value, 10);
        label.setSingleLine(true);
        label.setPadding(dp(6), dp(3), dp(6), dp(3));
        GradientDrawable badge = new GradientDrawable();
        badge.setColor(0x50142634);
        badge.setCornerRadius(dp(3));
        badge.setStroke(dp(1), 0xff648196);
        label.setBackground(badge);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.rightMargin = dp(6);
        pills.addView(label, lp);
    }

    private void person(LinearLayout row, String name, String role) {
        if (row.getChildCount() >= 8)
            return;
        LinearLayout cell = new LinearLayout(getContext());
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.CENTER);
        cell.setFocusable(false);
        cell.setTag("person:" + name + ":" + role);
        cell.setContentDescription(name + ", " + role);
        // Portrait cards, 2:3 like TMDB's photos, rounded with the artwork edge; initials when there is no photo
        // (owner, 14 Nov 2026; details-background/21). Never circles.
        ImageView portrait = new AppCompatImageView(getContext());
        portrait.setImageDrawable(new PersonInitials(name));
        portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);
        portrait.setBackground(new ColorDrawable(0xff2a2d33));
        portrait.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dp(4));
            }
        });
        portrait.setClipToOutline(true);
        portrait.setForeground(new com.archos.mediacenter.video.leanback.ArtworkEdge(getContext(), 4, false));
        portraits.put(name, portrait);
        cell.addView(portrait, new LinearLayout.LayoutParams(dp(40), dp(60)));
        boolean first = true;
        for (String value : new String[] {name, safe(role)}) {
            TextView text = text(value, first ? 10 : 9);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(true);
            text.setEllipsize(TextUtils.TruncateAt.END);
            text.setTextColor(first ? Color.WHITE : SupernovaStyle.TEXT_SECONDARY);
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
            size.topMargin = first ? dp(8) : dp(2);
            cell.addView(text, size);
            first = false;
        }
        row.addView(cell, new LinearLayout.LayoutParams(-1, -1));
    }

    private void renderEnrichedPeople() {
        if (enrichment == null || enrichment.credits == null)
            return;
        String signature = enrichment.credits.toString();
        if (signature.equals(peopleSignature))
            return;
        peopleSignature = signature;
        for (String section : new String[] {"cast", "crew"}) {
            JSONArray values = enrichment.credits.optJSONArray(section);
            if (values == null)
                continue;
            LinearLayout target = section.equals("cast") ? cast : crew;
            View focused = target.findFocus();
            Object focusKey = focused == null ? null : focused.getTag();
            int y = getScrollY();
            LinearLayout people = new DetailsPeopleGrid(getContext());
            Set<String> seen = new HashSet<>();
            for (int n = 0; n < values.length() && people.getChildCount() < 8; n++) {
                JSONObject value = values.optJSONObject(n);
                if (value == null)
                    continue;
                String name = value.optString("name"),
                       role = value.optString(section.equals("cast") ? "character" : "job");
                if (name.isEmpty() ||
                    section.equals("crew") && !Arrays
                                                   .asList("Director", "Writer", "Screenplay", "Story", "Creator",
                                                           "Producer", "Executive Producer", "Original Music Composer",
                                                           "Director of Photography", "Editor")
                                                   .contains(role))
                    continue;
                if (!seen.add(value.optLong("id") + ":" + name))
                    continue;
                person(people, name, role);
                long personId = value.optLong("id");
                Diagnostics.semantic(
                    people.getChildAt(people.getChildCount() - 1),
                    "details." + section + (personId > 0 ? ".person." + personId : ".slot." + n));
                ImageView portrait = portraits.get(name);
                String path = value.optString("profile_path");
                if (portrait != null && path.matches("/[A-Za-z0-9._-]+")) {
                    Uri uri = Uri.parse("https://image.tmdb.org/t/p/w185" + path);
                    ArtworkRequest.load(
                        portrait, uri, diagnosticMedia(), "details." + section, "portrait",
                        Picasso.get()
                            .load(uri)
                            .fit()
                            .centerCrop()
                            .placeholder(new PersonInitials(name))
                            .error(new PersonInitials(name)),
                        true);
                }
            }
            target.removeAllViews();
            target.addView(people, new LinearLayout.LayoutParams(-1, -1));
            restoreRowFocus(target, focusKey, y);
        }
    }

    public void bindShow(Tvshow value, Runnable playAction) {
        freshTitle("show:" + value.getTvshowId() + ":" + seasonNumber);
        show = value;
        SubtitleDiscovery.details(
            getContext(), value, snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache());
        showPlay = playAction;
        OfficialTitleArtwork.bind(title, value, false);
        title.setText(value.getName());
        if (seasonNumber < 0 && (plot.length() == 0 || !safe(value.getPlot()).isEmpty()))
            plot.setText(value.getPlot());
        rebuildTabs();
        renderDetails();
        requestEnrichment();
    }

    public void bindSeason(Tvshow value, int number, List<Episode> local, Runnable playAction) {
        seasonNumber = number;
        bindShow(value, playAction);
        seasons.clear();
        setSeason(number, local);
        Uri art = seasonArtwork(value, local);
        if (art != null)
            cardArtwork(art);
        requestEnrichment();
    }

    /** R3 (owner, 18 Nov 2026): a season page shows the show's backdrop, else an episode's picture, else the poster. */
    static Uri seasonArtwork(Tvshow value, List<Episode> local) {
        com.archos.mediascraper.ShowTags tags = value.getShowTags();
        ScraperImage backdrop = tags == null ? null : tags.getDefaultBackdrop();
        if (backdrop != null) {
            File file = backdrop.getLargeFileF();
            if (file != null && file.exists())
                return Uri.fromFile(file);
            if (backdrop.getLargeUrl() != null)
                return Uri.parse(backdrop.getLargeUrl());
        }
        for (Episode episode : local)
            if (episode.getPreviewBackdrop() != null)
                return episode.getPreviewBackdrop();
        for (Episode episode : local)
            if (episode.getPictureUri() != null)
                return episode.getPictureUri();
        return value.getPosterUri();
    }

    public void setSeason(int number, List<Episode> values) {
        seasons.put(number, new ArrayList<>(values));
        renderSeasons();
    }

    private void renderSeasons() {
        View focused = findFocus();
        Object key = focused == null ? null : focused.getTag();
        int y = getScrollY();
        Map<Integer, List<Episode>> locals = new TreeMap<>();
        for (Map.Entry<Integer, List<Episode>> season : seasons.entrySet()) {
            List<Entry> physical = new ArrayList<>();
            for (Episode ep : season.getValue())
                if (LibraryHealth.state(getContext(), ep) == LibraryHealth.State.AVAILABLE)
                    physical.add(new Entry(ep, 0, show == null ? 0 : show.getTvshowId(), ""));
            List<Episode> logical = new ArrayList<>();
            for (Entry e : PreviewVariants.logicalChoices(physical))
                logical.add((Episode)e.media);
            locals.put(season.getKey(), logical);
        }
        episodes.removeAllViews();
        episodeRows.clear();
        long id = remoteDetails != null                        ? remoteId
                  : show != null && show.getShowTags() != null ? show.getShowTags().getOnlineId()
                                                               : 0;
        Map<Integer, List<PreviewEpisodeChoice>> indexedSeasons =
            PreviewEpisodeChoice.indexed(locals, enrichment == null ? Collections.emptyMap() : enrichment.episodes);
        // A single-season show has no season page: its heading is plain and the episodes are listed here
        // (owner, 12 Nov 2026; notes §29).
        int realSeasons = 0;
        for (Integer number : indexedSeasons.keySet())
            if (number != null && number > 0)
                realSeasons++;
        for (Map.Entry<Integer, List<PreviewEpisodeChoice>> season : indexedSeasons.entrySet()) {
            if (seasonNumber >= 0 && season.getKey() != seasonNumber)
                continue;
            if (seasonNumber < 0 && show != null && realSeasons > 1) {
                TextView seasonInfo =
                    button(season.getKey() == 0 ? "Specials" : "Season " + season.getKey(),
                           ()
                               -> getContext().startActivity(
                                   new android.content.Intent(getContext(), PreviewSeasonActivity.class)
                                       .putExtra("show", show.getTvshowId())
                                       .putExtra("season", season.getKey())));
                seasonInfo.setTag("season:" + season.getKey());
                seasonInfo.setTextSize(16 * TvLayout.scale(getContext()));
                seasonInfo.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                seasonInfo.setPadding(dp(10), dp(5), dp(10), dp(5));
                // Focused: the white box with black text (D5); at rest the heading sits plain on the page.
                seasonInfo.setBackground(SupernovaStyle.focusFill(getContext(), SupernovaStyle.SMALL_BOX_RADIUS_DP));
                // DT4 (owner, 18 Nov 2026): the gap from a season's row to the next season's heading halved.
                LinearLayout.LayoutParams headingSize = new LinearLayout.LayoutParams(-2, dp(44));
                headingSize.topMargin = episodeRows.isEmpty() ? dp(8) : -dp(10);
                headingSize.bottomMargin = dp(6);
                episodes.addView(seasonInfo, headingSize);
            } else {
                TextView heading = text(season.getKey() == 0 ? "Specials" : "Season " + season.getKey(), 18);
                PreviewIcon.apply(heading, "tv", 16);
                episodes.addView(heading);
            }
            PreviewEpisodeRow row =
                new PreviewEpisodeRow((Activity)getContext(), season.getValue(), id,
                                      enrichment == null ? null : enrichment.seasonAvailability.get(season.getKey()));
            episodeRows.add(row);
            // DT3 (D6): the row runs off the page's edges; its padding keeps the first card where it was.
            int side = Math.round(getResources().getDisplayMetrics().widthPixels * .04f) + dp(8);
            LinearLayout.LayoutParams rowSize = new LinearLayout.LayoutParams(-1, dp(156));
            rowSize.leftMargin = -side;
            rowSize.rightMargin = -side;
            row.setPadding(row.getPaddingLeft() + side, row.getPaddingTop(), row.getPaddingRight() + side,
                           row.getPaddingBottom());
            episodes.addView(row, rowSize);
        }
        if (key instanceof String) {
            String tag = (String)key;
            for (PreviewEpisodeRow row : episodeRows) {
                if (tag.startsWith("episode:")) {
                    try {
                        if (row.focusEpisode(Long.parseLong(tag.substring(8))))
                            break;
                    } catch (NumberFormatException ignored) {
                    }
                } else if (row.focusRemote(tag))
                    break;
            }
        }
        scrollTo(0, y);
    }

    public static String source(Uri uri) {
        if (uri == null)
            return "Unknown";
        String scheme = uri.getScheme();
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        if (host.equals("put.io") || host.endsWith(".put.io"))
            return "Put.io";
        if (scheme == null || scheme.equals("file") || scheme.equals("content"))
            return "Local storage";
        if (scheme.equals("smb"))
            return "NAS / SMB";
        if (scheme.equals("webdav") || scheme.equals("webdavs") || scheme.equals("dav") || scheme.equals("davs"))
            return "WebDAV";
        if (scheme.equals("http") || scheme.equals("https"))
            return "Network / HTTP(S)";
        return "Network / " + scheme.toUpperCase(Locale.ROOT);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String shortFile(String s) {
        if (s == null)
            return "";
        return s.length() > 90 ? s.substring(0, sixty(s)) + "…" + s.substring(s.length() - 20) : s;
    }

    private static int sixty(String s) {
        return Math.min(60, s.length());
    }

    private void info(LinearLayout row, String name, String value) {
        TextView t = text(value.replace("\n\n", "    ·    ").replace("\n", ": "), 13);
        t.setMaxLines(3);
        t.setEllipsize(TextUtils.TruncateAt.END);
        t.setTextColor(0xffa8becf);
        t.setPadding(0, dp(3), 0, dp(3));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(7);
        row.addView(t, lp);
    }

    private void renderRelated() {
        View focused = related.findFocus();
        Object focusKey = focused == null ? null : focused.getTag();
        int oldY = getScrollY();
        for (PreviewLandscapeCard card : landscapeCards)
            card.release();
        landscapeCards.clear();
        related.removeAllViews();
        relatedGroups.clear();
        relatedProviders.clear();
        if (movie == null && show == null && remoteDetails == null)
            return;
        boolean television = show != null || "tv".equals(remoteKind);
        Entry selected = remoteDetails != null
                             ? null
                             : new Entry(show != null ? show : movie, 0, show == null ? 0 : show.getTvshowId(),
                                         tags instanceof VideoTags ? ((VideoTags)tags).getGenresFormatted()
                                                                   : context.getText().toString());
        Map<Long, Entry> local = new HashMap<>();
        if (snapshot != null)
            for (Entry entry : television ? snapshot.shows : snapshot.movies)
                if (entry.onlineId > 0)
                    local.putIfAbsent(entry.onlineId, entry);
        Set<String> shown = new HashSet<>();
        if (enrichment != null)
            for (PreviewDetailsData.Remote recommendation : enrichment.related) {
                if (landscapeCards.size() >= 12)
                    break;
                long id = recommendation.title.optLong("id");
                Entry entry = local.get(id);
                if (entry != null) {
                    addLocalRecommendation(entry);
                    shown.add("id:" + id);
                    shown.add(entry.key());
                } else if (recommendation.provider != null) {
                    JSONObject value = recommendation.title;
                    String name = value.optString("title", value.optString("name"));
                    String backdrop = value.optString("backdrop_path");
                    PreviewLandscapeCard card = new PreviewLandscapeCard(getContext());
                    card.bind(
                        name,
                        value.optString("release_date", value.optString("first_air_date")).replaceFirst("-.*$", ""),
                        backdrop.matches("/[A-Za-z0-9._-]+") ? Uri.parse("https://image.tmdb.org/t/p/w780" + backdrop)
                                                             : null,
                        true, id, "details.related", "landscape");
                    PreviewProviderIcons.bindProvider(card.availability, recommendation.provider, id,
                                                      "details.related");
                    card.availability.setImageAlpha(205);
                    card.setTag("remote:" + id);
                    Diagnostics.semantic(card, "details.related.remote." + id);
                    card.setContentDescription(name + " · " + recommendation.provider.name);
                    // A title on a service opens that service's app straight away, never a Supernova page (D8).
                    String service = recommendation.provider.name;
                    card.setOnClickListener(v -> {
                        if (!(getContext() instanceof Activity) ||
                            !com.archos.mediacenter.video.streaming.StreamingActions.openApp(
                                (Activity)getContext(), service))
                            com.archos.mediacenter.video.leanback.PreviewNotice.show(
                                getContext(), "The " + service + " app isn't on this device", true);
                    });
                    addRecommendation(card, recommendation.provider);
                    shown.add("id:" + id);
                }
            }
        if (snapshot != null)
            for (Entry entry : PreviewDiscovery.similar(selected, snapshot)) {
                if (landscapeCards.size() >= 12)
                    break;
                if (television ? !(entry.media instanceof Tvshow) : !(entry.media instanceof Movie))
                    continue;
                if (shown.contains(entry.key()) || entry.onlineId > 0 && shown.contains("id:" + entry.onlineId))
                    continue;
                addLocalRecommendation(entry);
                shown.add(entry.key());
                if (entry.onlineId > 0)
                    shown.add("id:" + entry.onlineId);
            }
        layoutRelatedRows();
        restoreRowFocus(related, focusKey, oldY);
        rebuildTabs();
    }

    private void addLocalRecommendation(Entry entry) {
        PreviewLandscapeCard card = new PreviewLandscapeCard(getContext());
        card.bind(PreviewPages.displayName(entry), entry.year() > 0 ? String.valueOf(entry.year()) : "",
                  entry.backdrop != null ? entry.backdrop : entry.media.getPosterUri(), true,
                  entry.media instanceof Video    ? ((Video)entry.media).getId()
                  : entry.media instanceof Tvshow ? ((Tvshow)entry.media).getTvshowId()
                                                  : 0,
                  "details.related", "landscape");
        card.setTag("related:" + entry.key());
        Diagnostics.semantic(card, "details.related." + entry.key());
        card.setOnClickListener(v
                                -> new VideoViewClickedListener((Activity)getContext())
                                       .onItemClicked(new Presenter.ViewHolder(v), entry.media, null, null));
        addRecommendation(card, null);
    }

    /** The Episodes card width: a quarter of the row (PreviewEpisodeRow.cardWidth). */
    private int extraCardWidth() {
        int width = getResources().getDisplayMetrics().widthPixels - dp(84);
        return Math.max(1, (width - dp(36)) / 4);
    }

    /** More Like This, grouped: null = In your library, else the service the titles are on. */
    private final Map<String, List<PreviewLandscapeCard>> relatedGroups = new LinkedHashMap<>();
    private final Map<String, StreamingRepository.Provider> relatedProviders = new HashMap<>();
    private static final String IN_LIBRARY = "";

    private void addRecommendation(PreviewLandscapeCard card, StreamingRepository.Provider provider) {
        String group = provider == null ? IN_LIBRARY : provider.name;
        if (provider != null)
            relatedProviders.put(group, provider);
        relatedGroups.computeIfAbsent(group, key -> new ArrayList<>()).add(card);
        landscapeCards.add(card);
    }

    /**
     * DT5 (owner, 18 Nov 2026; review-0157-2/01 2): More Like This in rows like Episodes and Extras: In your library
     * first, then one row per service, the top of Your services first, each heading with the service's colour logo.
     */
    private void layoutRelatedRows() {
        List<String> order = new ArrayList<>(relatedGroups.keySet());
        List<String> yours = new ArrayList<>(StreamingRepository.selected(getContext()));
        order.sort((a, b) -> Integer.compare(groupRank(a, yours), groupRank(b, yours)));
        int side = Math.round(getResources().getDisplayMetrics().widthPixels * .04f) + dp(8);
        for (String group : order) {
            LinearLayout heading = new LinearLayout(getContext());
            heading.setGravity(Gravity.CENTER_VERTICAL);
            heading.setPadding(dp(5), dp(12), 0, dp(4));
            StreamingRepository.Provider provider = relatedProviders.get(group);
            if (provider != null) {
                ImageView logo = new ImageView(getContext());
                PreviewProviderIcons.bindProvider(logo, provider, 0, "details.related");
                LinearLayout.LayoutParams logoSize = new LinearLayout.LayoutParams(dp(22), dp(22));
                logoSize.rightMargin = dp(8);
                heading.addView(logo, logoSize);
            }
            TextView words = text(group.equals(IN_LIBRARY) ? "In your library" : "On " + group, 18);
            if (provider == null)
                PreviewIcon.apply(words, "library", 16);
            heading.addView(words);
            heading.setTag("related.heading:" + (group.equals(IN_LIBRARY) ? "library" : group));
            related.addView(heading);
            HorizontalScrollView rail = new HorizontalScrollView(getContext());
            rail.setFocusable(false);
            rail.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
            rail.setHorizontalScrollBarEnabled(false);
            rail.setClipChildren(false);
            rail.setClipToPadding(false);
            rail.setPadding(dp(10) + side, dp(10), dp(10) + side, dp(10));
            LinearLayout row = new LinearLayout(getContext());
            row.setClipChildren(false);
            row.setClipToPadding(false);
            rail.addView(row);
            LinearLayout.LayoutParams railSize =
                new LinearLayout.LayoutParams(-1, extraCardWidth() * 9 / 16 + dp(28));
            railSize.leftMargin = -side;
            railSize.rightMargin = -side;
            related.addView(rail, railSize);
            for (PreviewLandscapeCard card : relatedGroups.get(group)) {
                LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(extraCardWidth(), extraCardWidth() * 9 / 16);
                lp.rightMargin = dp(12);
                row.addView(card, lp);
                rowKeys(card, row);
            }
        }
    }

    /** In your library first, then services in the order of Your services, then any others. */
    private int groupRank(String group, List<String> yours) {
        if (group.equals(IN_LIBRARY))
            return -1;
        StreamingRepository.Provider provider = relatedProviders.get(group);
        int at = provider == null ? -1 : yours.indexOf(String.valueOf(provider.id));
        return at < 0 ? Integer.MAX_VALUE : at;
    }

    private boolean hasPlayableExtras() {
        for (ScraperTrailer e : trailerList)
            if ("YouTube".equals(e.mSite) && e.mVideoKey != null && e.mVideoKey.matches("[A-Za-z0-9_-]{11}"))
                return true;
        return false;
    }

    private void renderExtras() {
        View focused = trailers.findFocus();
        Object focusKey = focused == null ? null : focused.getTag();
        int y = getScrollY();
        for (PreviewLandscapeCard old : extraCards)
            old.release();
        extraCards.clear();
        extraHeadings.clear();
        trailers.removeAllViews();
        Map<String, List<ScraperTrailer>> categories = new LinkedHashMap<>();
        for (String category : new String[] {"Trailers", "Teasers", "Behind the Scenes", "Featurettes", "Interviews",
                                             "Clips", "Other Videos"})
            categories.put(category, new ArrayList<>());
        Set<String> seen = new HashSet<>();
        for (ScraperTrailer extra : trailerList) {
            if (!"YouTube".equals(extra.mSite) || extra.mVideoKey == null ||
                !extra.mVideoKey.matches("[A-Za-z0-9_-]{11}") || !seen.add(extra.mVideoKey))
                continue;
            String type = "";
            if (enrichment != null)
                for (PreviewDetailsData.Extra value : enrichment.extras)
                    if (value.key.equals(extra.mVideoKey)) {
                        type = value.type;
                        break;
                    }
            if (type.isEmpty()) {
                String name = safe(extra.mName).toLowerCase(Locale.ROOT);
                type = name.contains("trailer")      ? "Trailer"
                       : name.contains("teaser")     ? "Teaser"
                       : name.contains("featurette") ? "Featurette"
                       : name.contains("interview")  ? "Interview"
                                                     : "Video";
            }
            String category = type.equals("Trailer")             ? "Trailers"
                              : type.equals("Teaser")            ? "Teasers"
                              : type.equals("Behind the Scenes") ? "Behind the Scenes"
                              : type.equals("Featurette")        ? "Featurettes"
                              : type.equals("Interview")         ? "Interviews"
                              : type.equals("Clip")              ? "Clips"
                                                                 : "Other Videos";
            categories.get(category).add(extra);
        }
        for (Map.Entry<String, List<ScraperTrailer>> category : categories.entrySet()) {
            if (category.getValue().isEmpty())
                continue;
            TextView heading = text(category.getKey(), 18);
            // DT5: one row design: every row heading has its icon (Trailers a play mark, the rest film).
            PreviewIcon.apply(heading, category.getKey().equals("Trailers") ? "play" : "film", 16);
            heading.setPadding(dp(5), dp(12), 0, dp(4));
            trailers.addView(heading);
            HorizontalScrollView rail = new HorizontalScrollView(getContext());
            rail.setFocusable(false);
            rail.setFocusableInTouchMode(false);
            rail.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
            rail.setHorizontalScrollBarEnabled(false);
            rail.setClipChildren(false);
            rail.setClipToPadding(false);
            // DT3 (D6): Extras rows run off the page's edges too; the padding keeps the first card where it was.
            int side = Math.round(getResources().getDisplayMetrics().widthPixels * .04f) + dp(8);
            rail.setPadding(dp(10) + side, dp(10), dp(10) + side, dp(10));
            LinearLayout row = new LinearLayout(getContext());
            row.setClipChildren(false);
            row.setClipToPadding(false);
            rail.addView(row);
            LinearLayout.LayoutParams railSize = new LinearLayout.LayoutParams(-1, extraCardWidth() * 9 / 16 + dp(28));
            railSize.leftMargin = -side;
            railSize.rightMargin = -side;
            trailers.addView(rail, railSize);
            for (ScraperTrailer extra : category.getValue()) {
                PreviewLandscapeCard card = new PreviewLandscapeCard(getContext());
                card.bind(safe(extra.mName),
                          PreviewExtraDuration.label(PreviewExtraDuration.cached(getContext(), extra.mVideoKey)),
                          Uri.parse("https://i.ytimg.com/vi/" + extra.mVideoKey + "/hqdefault.jpg"), true,
                          diagnosticMedia(), "details.extras", "video_thumbnail");
                card.setTag("extra:" + extra.mVideoKey);
                Diagnostics.semantic(card, "details.extra." + extra.mVideoKey);
                card.setOnClickListener(v -> PreviewTrailer.show((Activity)getContext(), extra));
                View.OnFocusChangeListener focus = card.getOnFocusChangeListener();
                card.setOnFocusChangeListener((v, active) -> {
                    if (focus != null)
                        focus.onFocusChange(v, active);
                    if (active) {
                        row.setTag(card);
                        PreviewExtraDuration.request(card, extra.mVideoKey);
                        post(this::revealContentFocus);
                    }
                });
                // One card size everywhere (D7, RULES §14): the same quarter-row width as Episodes, 16:9.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(extraCardWidth(),
                                                                             extraCardWidth() * 9 / 16);
                lp.setMargins(0, 0, dp(12), 0);
                row.addView(card, lp);
                extraCards.add(card);
                extraHeadings.put(card, heading);
                rowKeys(card, row);
            }
        }
        restoreRowFocus(trailers, focusKey, y);
    }

    public void bindRemote(JSONObject details, String kind, long id) {
        freshTitle(kind + ":" + id);
        remoteDetails = details;
        remoteKind = kind;
        remoteId = id;
        title.setText(details.optString("title", details.optString("name")));
        OfficialTitleArtwork.bindRemote(title, kind, id);
        plot.setText(details.optString("overview"));
        String backdrop = details.optString("backdrop_path");
        if (backdrop.matches("/[A-Za-z0-9._-]+"))
            cardArtwork(Uri.parse("https://image.tmdb.org/t/p/w1280" + backdrop));
        play.setVisibility(GONE);
        renderDetails();
        requestEnrichment();
    }

    private String firstPaintTitle = "";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!boundTitle.isEmpty() && !boundTitle.equals(firstPaintTitle)) {
            firstPaintTitle = boundTitle;
            Diagnostics.event(
                "details_first_paint", "media_id", diagnosticMedia(), "elapsed_ms",
                SystemClock.elapsedRealtime() - openedAt);
        }
    }

    private long diagnosticMedia() {
        return movie != null ? movie.getId() : show != null ? show.getTvshowId() : remoteId;
    }

    private long metadataIdentity() {
        if (remoteDetails != null)
            return remoteId;
        if (movie instanceof Movie)
            return ((Movie)movie).getOnlineId();
        if (tags instanceof EpisodeTags && ((EpisodeTags)tags).getShowTags() != null)
            return ((EpisodeTags)tags).getShowTags().getOnlineId();
        if (show != null && show.getShowTags() != null)
            return show.getShowTags().getOnlineId();
        Snapshot library = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
        if (library != null)
            for (Entry entry : show != null ? library.shows : library.episodes)
                if (show != null && entry.show == show.getTvshowId() ||
                    movie != null && entry.media instanceof Video && ((Video)entry.media).getId() == movie.getId())
                    return entry.onlineId;
        return 0;
    }

    private void requestEnrichment() {
        long id = metadataIdentity();
        String kind = remoteDetails != null ? remoteKind : show != null || movie instanceof Episode ? "tv" : "movie";
        if (id <= 0)
            return;
        String key = kind + ":" + id + ":" + seasonNumber + ":" + Locale.getDefault().toLanguageTag();
        if (key.equals(enrichmentKey))
            return;
        if (!enrichmentKey.isEmpty()) {
            enrichment = null;
            peopleSignature = "";
        }
        enrichmentKey = key;
        if (seasonNumber < 0)
            PreviewEnrichmentQueue.enqueue(getContext(), kind, id, PreviewEnrichmentQueue.FOREGROUND);
        int generation = ++enrichmentGeneration;
        if (enrichmentTask != null)
            enrichmentTask.cancel(true);
        Set<Long> local = new HashSet<>();
        if (snapshot != null)
            for (Entry e : kind.equals("movie") ? snapshot.movies : snapshot.shows)
                local.add(e.onlineId);
        Context app = getContext().getApplicationContext();
        final int requestedSeason = seasonNumber;
        enrichmentTask = PreviewDetailsData.IO.submit(() -> {
            long start = SystemClock.elapsedRealtime();
            String operation = Diagnostics.operation("details_enrichment");
            try (Diagnostics.OperationScope ignored =
                     Diagnostics.operationScope(operation)) {
                if (requestedSeason < 0) {
                    PreviewDetailsData.Result core = PreviewDetailsData.cachedCore(app, kind, id);
                    if (core != null)
                        post(() -> enqueueEnrichment(core, generation));
                    Diagnostics.event(
                        "details_core_cache_ready", "media_id", id, "elapsed_ms",
                        SystemClock.elapsedRealtime() - start, "cached", core != null);
                }
                PreviewDetailsData.Result cached = requestedSeason >= 0
                                                       ? PreviewDetailsData.cachedSeason(app, id, requestedSeason)
                                                       : PreviewDetailsData.cached(app, kind, id, local);
                if (cached != null)
                    post(() -> enqueueEnrichment(cached, generation));
                Consumer<PreviewDetailsData.Result> progress =
                    partial -> post(() -> enqueueEnrichment(partial, generation));
                PreviewDetailsData.Result result =
                    requestedSeason >= 0 ? PreviewDetailsData.loadSeason(app, id, requestedSeason, progress)
                                         : PreviewDetailsData.load(app, kind, id, local, progress);
                post(() -> enqueueEnrichment(result, generation));
            } catch (Exception error) {
                Diagnostics.event(
                    "details_enrichment_unavailable", "operation_id", operation, "media_id", id, "failure_category",
                    error.getClass().getSimpleName());
                post(() -> {
                    if (generation == enrichmentGeneration)
                        enrichmentKey = "";
                });
            } finally {
                Diagnostics.event(
                    "details_enrichment", "operation_id", operation, "elapsed_ms",
                    SystemClock.elapsedRealtime() - start);
                Diagnostics.finishOperation(operation, "details_enrichment",
                                                                                     start);
            }
        });
    }

    private void applyEnrichment(PreviewDetailsData.Result result, int generation) {
        if (generation != enrichmentGeneration)
            return;
        String oldInformation = informationFingerprint(enrichment);
        String oldCredits = enrichment == null ? "" : String.valueOf(enrichment.credits);
        String oldEpisodes =
            enrichment == null ? "" : enrichment.episodes.toString() + enrichment.seasonAvailability.toString();
        String oldRelated = enrichment == null ? "" : relatedFingerprint(enrichment);
        String oldExtras = enrichment == null ? "" : extrasFingerprint(enrichment);
        if (enrichment == null && remoteDetails != null) {
            enrichment = new PreviewDetailsData.Result();
            enrichment.details = remoteDetails;
        }
        enrichment = PreviewDetailsData.merge(enrichment, result);
        if (enrichment.details != null && !enrichment.details.optString("overview").isEmpty() &&
            (seasonNumber >= 0 || plot.length() == 0 || movie != null && !movie.hasScraperData()))
            plot.setText(enrichment.details.optString("overview"));
        if (artworkUri == null && !artworkReady && enrichment.details != null) {
            String path = enrichment.details.optString("backdrop_path");
            if (path.matches("/[A-Za-z0-9._-]+"))
                cardArtwork(Uri.parse("https://image.tmdb.org/t/p/w1280" + path));
        }
        if (!oldCredits.equals(String.valueOf(enrichment.credits)))
            renderEnrichedPeople();
        if (!oldInformation.equals(informationFingerprint(enrichment)))
            renderDetails();
        if (!oldRelated.equals(relatedFingerprint(enrichment)))
            renderRelated();
        if (!oldExtras.equals(extrasFingerprint(enrichment))) {
            List<ScraperTrailer> extras = new ArrayList<>();
            for (PreviewDetailsData.Extra e : enrichment.extras)
                extras.add(new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER, e.name, e.key, "YouTube", ""));
            if (enrichment.extrasLoaded || !extras.isEmpty())
                trailerList = extras;
            renderExtras();
            rebuildTabs();
        }
        if (!oldEpisodes.equals(enrichment.episodes.toString() + enrichment.seasonAvailability.toString()))
            renderSeasons();
    }

    private static String extrasFingerprint(PreviewDetailsData.Result result) {
        if (result == null)
            return "";
        StringBuilder text = new StringBuilder().append(result.extrasLoaded);
        for (PreviewDetailsData.Extra e : result.extras)
            text.append(e.key).append(e.name).append(e.type);
        return text.toString();
    }

    private static String relatedFingerprint(PreviewDetailsData.Result result) {
        if (result == null)
            return "";
        StringBuilder text = new StringBuilder().append(result.relatedLoaded);
        for (PreviewDetailsData.Remote e : result.related)
            text.append(e.title).append(e.provider == null ? 0 : e.provider.id);
        return text.toString();
    }

    private String informationFingerprint(PreviewDetailsData.Result result) {
        if (result == null)
            return "";
        String text = String.valueOf(result.details) + result.classification + result.alternatives +
                      result.supplementary.countsSignature();
        for (int slot = 0; slot < 4; slot++)
            text += "|" + result.supplementary.value(slot);
        return text + (movie instanceof Episode ? result.episodes.toString() : "");
    }

    private void rowKeys(View card, LinearLayout row) {
        card.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN ||
                key != KeyEvent.KEYCODE_DPAD_LEFT && key != KeyEvent.KEYCODE_DPAD_RIGHT)
                return false;
            int next = row.indexOfChild(v) + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
            if (next >= 0 && next < row.getChildCount())
                row.getChildAt(next).requestFocus();
            return true;
        });
    }

    private void restoreRowFocus(ViewGroup area, Object key, int y) {
        if (key == null)
            return;
        View requested = area.findViewWithTag(key), target = requested;
        boolean fallback = false;
        if (target == null || !target.isShown() || !target.isFocusable()) {
            fallback = true;
            target = null;
            for (View candidate : area.getFocusables(View.FOCUS_FORWARD))
                if (candidate.isShown() && candidate.getTag() != null) {
                    target = candidate;
                    break;
                }
        }
        if (target == null) {
            fallback = true;
            target = play.getVisibility() == VISIBLE ? play : sectionTabs.isEmpty() ? null : sectionTabs.get(0);
        }
        boolean success = target != null && target.requestFocus();
        Diagnostics.focusRestored(requested, findFocus(), fallback, success);
        scrollTo(0, y);
        final View restored = target;
        post(() -> {
            if (restored != null && restored.hasFocus())
                scrollTo(0, y);
        });
    }

    private java.util.function.Consumer<View> playVersions;

    /** R5: with two or more files for one title, Play opens the versions menu beside it. */
    public void setPlayVersions(java.util.function.Consumer<View> chooser) {
        playVersions = chooser;
    }

    public void play() {
        if (playVersions != null && versionCount > 1 && movie != null) {
            playVersions.accept(play);
            return;
        }
        if (showPlay != null) {
            showPlay.run();
            return;
        }
        ObjectAdapter adapter = actions.get();
        if (adapter == null)
            return;
        for (int id : new int[] {VideoActionAdapter.ACTION_RESUME, VideoActionAdapter.ACTION_LOCAL_RESUME,
                                 VideoActionAdapter.ACTION_PLAY, VideoActionAdapter.ACTION_PLAY_FROM_BEGIN,
                                 VideoActionAdapter.ACTION_REMOTE_RESUME})
            for (int i = 0; i < adapter.size(); i++) {
                Object item = adapter.get(i);
                if (item instanceof Action && ((Action)item).getId() == id) {
                    action.accept((Action)item);
                    return;
                }
            }
    }

    private Entry rowEntry() {
        if (show != null)
            return new Entry(show, 0, show.getTvshowId(), "");
        if (movie == null)
            return null;
        long showId = 0;
        Snapshot cached = PreviewLibraryLoader.memoryCache();
        if (movie instanceof Episode && cached != null)
            for (Entry e : cached.episodes)
                if (((Video)e.media).getId() == movie.getId()) {
                    showId = e.show;
                    break;
                }
        return new Entry(movie, 0, showId, "");
    }

    private void moreInfo() {
        Dialog dialog = PreviewDialog.create(getContext(), "reader");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(false);
        scroll.setFocusable(true);
        scroll.setBackground(PreviewDialog.surface(getContext(), false));
        LinearLayout page = new LinearLayout(getContext());
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(20), dp(24), dp(22));
        scroll.addView(page);
        TextView heading = text(title.getText().toString(), 28);
        page.addView(heading);
        TextView metadata = text(meta.getText() + "  " + context.getText(), 13);
        metadata.setTextColor(0xffadc3d3);
        page.addView(metadata);
        LinearLayout columns = new LinearLayout(getContext());
        columns.setPadding(0, dp(10), 0, 0);
        page.addView(columns, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout overview = new LinearLayout(getContext());
        overview.setOrientation(LinearLayout.VERTICAL);
        columns.addView(overview, new LinearLayout.LayoutParams(0, -2, 1.65f));
        LinearLayout facts = new LinearLayout(getContext());
        facts.setOrientation(LinearLayout.VERTICAL);
        facts.setPadding(dp(30), 0, 0, 0);
        columns.addView(facts, new LinearLayout.LayoutParams(0, -2, 1));
        infoSection(overview, "Overview",
                    show != null    ? safe(show.getPlot())
                    : movie == null ? ""
                                    : safe(movie.getDescriptionBody()));
        if (tags != null) {
            StringBuilder people = new StringBuilder();
            int count = 0;
            for (Map.Entry<String, String> p : tags.getActors().entrySet()) {
                if (count++ == 3)
                    break;
                people.append(p.getKey()).append(safe(p.getValue()).isEmpty() ? "" : " · " + p.getValue()).append("\n");
            }
            if (!safe(tags.getDirectorsFormatted()).isEmpty())
                people.append("Director · ").append(tags.getDirectorsFormatted());
            infoSection(overview, "Cast & Crew", people.toString());
        }
        if (movie != null) {
            List<String> technical = new ArrayList<>();
            if (movie.getMeasuredWidth() > 0 && movie.getMeasuredHeight() > 0)
                technical.add(movie.getMeasuredWidth() + " × " + movie.getMeasuredHeight());
            String video = PreviewMediaInfo.format(movie.getCalculatedVideoFormat()),
                   audio = PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat());
            if (!safe(video).isEmpty())
                technical.add(video);
            if (!safe(audio).isEmpty())
                technical.add(audio);
            technicalBadges(facts, technical);
            String file = safe(movie.getFilenameNonCryptic()) + "\n" + source(movie.getFileUri());
            if (movie.getSize() > 0)
                file += " · " + Formatter.formatFileSize(getContext(), movie.getSize());
            infoSection(facts, "Indexed library file", file);
        }
        if (show != null)
            infoSection(facts, "Series", meta.getText() + "\n" + context.getText());
        TextView back = text("Press Back to return to Details", 11);
        back.setPadding(0, dp(26), 0, 0);
        back.setTextColor(0xff8da9bb);
        page.addView(back);
        Diagnostics.semantic(scroll, "details.reader");
        Runnable removeFrost = PreviewFrost.show((Activity)getContext());
        dialog.setContentView(scroll);
        dialog.setOnDismissListener(d -> removeFrost.run());
        dialog.show();
        android.view.Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(.48f);
        int width = Math.min(dp(840), getResources().getDisplayMetrics().widthPixels - dp(64));
        int maximum = Math.min(dp(360), getResources().getDisplayMetrics().heightPixels - dp(96));
        scroll.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                       View.MeasureSpec.makeMeasureSpec(maximum, View.MeasureSpec.AT_MOST));
        window.setLayout(width, Math.min(maximum, scroll.getMeasuredHeight()));
        window.setGravity(Gravity.CENTER);
        scroll.requestFocus();
    }

    private void infoSection(LinearLayout page, String label, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        TextView heading = text(label, 18);
        heading.setTextColor(PreviewAccent.color(getContext()));
        heading.setPadding(0, dp(22), 0, dp(8));
        page.addView(heading);
        TextView content = text(value.trim(), 14);
        content.setLineSpacing(dp(3), 1f);
        page.addView(content);
    }

    private void technicalBadges(LinearLayout parent, List<String> values) {
        if (values.isEmpty())
            return;
        infoSection(parent, "Technical Information", "");
        LinearLayout line = null;
        for (int i = 0; i < values.size(); i++) {
            if (i % 2 == 0) {
                line = new LinearLayout(getContext());
                parent.addView(line);
            }
            TextView badge = text(values.get(i), 11);
            badge.setPadding(dp(7), dp(5), dp(7), dp(5));
            badge.setBackground(PreviewDialog.surface(getContext(), false));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
            lp.setMargins(0, dp(4), dp(5), 0);
            line.addView(badge, lp);
        }
    }

    private static String titleStyle(String label) {
        StringBuilder out = new StringBuilder();
        for (String word : label.split(" ")) {
            if (out.length() > 0)
                out.append(' ');
            if (!word.isEmpty())
                out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return out.toString();
    }

    private void refreshMoreRows() {
        if (moreDialog == null || !moreDialog.isShowing())
            return;
        for (Map.Entry<Integer, Action> row : moreRows.entrySet()) {
            Action current = PreviewMoreActions.current(actions.get(), row.getValue(), show != null);
            if (current != null)
                PreviewDialog.updateLabel(moreDialog, row.getKey(), titleStyle(String.valueOf(current.getLabel1())));
        }
    }

    private void moreAction(List<String> labels, List<Runnable> commands, Action a) {
        moreRows.put(labels.size(), a);
        labels.add(titleStyle(String.valueOf(a.getLabel1())));
        commands.add(() -> {
            Action current = PreviewMoreActions.current(actions.get(), a, show != null);
            if (current != null)
                action.accept(current);
            refreshMoreRows();
        });
    }

    private static boolean isRemoveFromLibrary(Action a) {
        long id = a.getId();
        return id == VideoActionAdapter.ACTION_UNINDEX ||
            id == TvshowActionAdapter.ACTION_UNINDEX;
    }

    private void more() {
        if (seasonNumber >= 0) {
            // R4 (owner, 18 Nov 2026): the Details More menu beside its button, with Delete season.
            List<com.archos.mediacenter.video.browser.adapters.object.Episode> files = new ArrayList<>();
            for (PreviewEpisodeRow row : episodeRows)
                files.addAll(row.localEpisodes());
            List<String> labels = new ArrayList<>();
            List<String> icons = new ArrayList<>();
            List<Runnable> commands = new ArrayList<>();
            moreItem(labels, icons, commands, "Series details", "details", nativeDetails);
            moreItem(labels, icons, commands, "Episodes", "list", () -> showSection("Episodes", true));
            if (!files.isEmpty() && getContext() instanceof Activity) {
                moreItem(labels, icons, commands, "— Files & Media", null, null);
                moreItem(labels, icons, commands, "Delete season", "delete",
                         () -> PreviewEpisodeMenu.deleteSeason((Activity)getContext(), seasonNumber, files));
            }
            PreviewDialog.menu(getContext(), moreButton, "Season " + seasonNumber, labels.toArray(new String[0]),
                               icons.toArray(new String[0]), -1, n -> {
                                   if (commands.get(n) != null)
                                       commands.get(n).run();
                               });
            return;
        }
        // The More menu (page sheet DETAILS.md; D1, D2): beside More, an icon per item, groups Playback · Library ·
        // Files & Media, then TMDB page set apart at the foot. Only what makes sense for this title.
        ObjectAdapter adapter = actions.get();
        moreRows.clear();
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<Runnable> commands = new ArrayList<>();
        Action watched = null;
        Action unindex = null;
        Action delete = null;
        Action fixShow = null;
        for (int i = 0; adapter != null && i < adapter.size(); i++) {
            if (!(adapter.get(i) instanceof Action))
                continue;
            Action a = (Action)adapter.get(i);
            if (PreviewMoreActions.watched(a, show != null))
                watched = a;
            else if (isRemoveFromLibrary(a))
                unindex = a;
            else if (show != null && a.getId() == TvshowActionAdapter.ACTION_CHANGE_INFO)
                fixShow = a;
            else if (show == null && a.getId() == VideoActionAdapter.ACTION_DELETE ||
                     show != null && a.getId() == TvshowActionAdapter.ACTION_DELETE)
                delete = a;
        }
        if (movie != null && movie.getResumeMs() > 0 && getContext() instanceof Activity) {
            moreItem(labels, icons, commands, "— Playback", null, null);
            moreItem(labels, icons, commands, movie instanceof Episode ? "Restart episode" : "Restart movie",
                     "history", () -> PlayUtils.startVideo((Activity)getContext(), movie, PlayerActivity.RESUME_NO,
                                                           false, -1, null, -1));
        }
        if (watched != null || movie != null || unindex != null || fixShow != null) {
            moreItem(labels, icons, commands, "— Library", null, null);
            if (watched != null) {
                Action toggle = watched;
                long id = watched.getId();
                boolean markWatched = id == VideoActionAdapter.ACTION_MARK_AS_WATCHED ||
                                      id == TvshowActionAdapter.ACTION_MARK_SHOW_AS_WATCHED;
                moreItem(labels, icons, commands, markWatched ? "Mark as watched" : "Mark as unwatched", "check",
                         () -> action.accept(toggle));
            }
            if (movie != null) {
                moreItem(labels, icons, commands, "Fix match", "search",
                         () -> getContext().startActivity(new android.content.Intent(
                             getContext(), com.archos.mediacenter.video.leanback.scrapping
                                               .ManualVideoScrappingActivity.class)
                             .putExtra(com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity
                                           .EXTRA_VIDEO, movie)));
            }
            if (movie == null && fixShow != null) {
                // DT8 (D2): Fix match in More for a TV show too, through the show's own match action.
                Action fix = fixShow;
                moreItem(labels, icons, commands, "Fix match", "search", () -> action.accept(fix));
            }
            if (unindex != null) {
                Action remove = unindex;
                moreItem(labels, icons, commands, "Remove from library", "remove", () -> action.accept(remove));
            }
        }
        moreItem(labels, icons, commands, "— Files & Media", null, null);
        if (movie != null)
            moreItem(labels, icons, commands, "Subtitles", "sub", nativeDetails);
        moreItem(labels, icons, commands, "Artwork", "appearance",
                 artworkPanel != null ? artworkPanel : nativeDetails);
        moreItem(labels, icons, commands, "Full description", "synopsis", this::fullDescription);
        if (delete != null && movie != null) {
            moreItem(labels, icons, commands, "Delete file", "delete", () -> PreviewDialog.confirmDelete(
                getContext(), "Delete " + movie.getName() + "?",
                "The file is deleted from its drive or server. This can't be undone.",
                () -> action.accept(new Action(VideoActionAdapter.ACTION_CONFIRM_DELETE))));
        }
        String tmdb = tmdbPage();
        if (tmdb != null) {
            moreItem(labels, icons, commands, "— ", null, null);
            moreItem(labels, icons, commands, "TMDB page", "external",
                     () -> PreviewTmdbCode.show(getContext(), tmdb));
        }
        Dialog dialog = PreviewDialog.menu(getContext(), moreButton, "More", labels.toArray(new String[0]),
                                           icons.toArray(new String[0]), -1, n -> {
                                               if (commands.get(n) != null)
                                                   commands.get(n).run();
                                           });
        moreDialog = dialog;
        dialog.setOnDismissListener(d -> {
            if (moreDialog == dialog) {
                moreDialog = null;
                moreRows.clear();
            }
        });
    }

    private static void moreItem(List<String> labels, List<String> icons, List<Runnable> commands, String label,
                                 String icon, Runnable command) {
        labels.add(label);
        icons.add(icon);
        commands.add(command);
    }

    private Runnable artworkPanel;

    /** More › Artwork opens the side panel (D3); supplied by the Details screen, which knows the pictures. */
    public void setArtworkPanel(Runnable panel) {
        artworkPanel = panel;
    }

    /** The hero shows {@code uri} while the Artwork panel is on it; null shows the kept picture again (D3). */
    public void previewArtwork(Uri uri) {
        Uri shown = uri != null ? uri : artworkUri == null ? null : Uri.parse(artworkUri);
        if (cardArtwork == null || shown == null)
            return;
        try {
            com.squareup.picasso.Picasso.get().load(shown).fit().centerCrop().noFade().into(cardArtwork);
        } catch (RuntimeException unavailable) {
            // No picture loader in this process.
        }
    }

    /** Full description: only the text, sized to it (owner, 18 Nov 2026; DT7). */
    private void fullDescription() {
        PreviewDialog.readText(getContext(), String.valueOf(plot.getText()));
    }

    private String storedRating() {
        if (movie instanceof Movie)
            return safe(((Movie)movie).getContentRating());
        if (movie != null)
            return "";
        if (show == null)
            return "";
        if (!safe(show.getContentRating()).isEmpty())
            return show.getContentRating();
        Snapshot local = snapshot != null ? snapshot : PreviewLibraryLoader.memoryCache();
        if (local == null)
            return "";
        Episode first = null;
        for (Entry entry : local.episodes) {
            if (!(entry.media instanceof Episode) || safe(((Episode)entry.media).getContentRating()).isEmpty())
                continue;
            Episode episode = (Episode)entry.media;
            if (entry.show != show.getTvshowId())
                continue;
            boolean earlier = first == null || episode.getSeasonNumber() < first.getSeasonNumber() ||
                              episode.getSeasonNumber() == first.getSeasonNumber() &&
                                  episode.getEpisodeNumber() < first.getEpisodeNumber();
            if (earlier)
                first = episode;
        }
        return first == null ? "" : first.getContentRating();
    }

    /** The title's TMDB page, when its TMDB number is known. */
    private String tmdbPage() {
        JSONObject data = enrichment != null && enrichment.details != null ? enrichment.details : remoteDetails;
        long id = data == null ? 0 : data.optLong("id", 0);
        if (id <= 0 && movie instanceof Movie)
            id = ((Movie)movie).getOnlineId();
        if (id <= 0)
            return null;
        boolean television = show != null || movie instanceof Episode;
        return "https://www.themoviedb.org/" + (television ? "tv/" : "movie/") + id;
    }

    private ScraperTrailer primaryTrailer() {
        ScraperTrailer best = null;
        int score = 0;
        for (ScraperTrailer t : trailerList) {
            if (!"YouTube".equals(t.mSite) || t.mVideoKey == null || !t.mVideoKey.matches("[A-Za-z0-9_-]{11}"))
                continue;
            String name = t.mName == null ? "" : t.mName.toLowerCase(Locale.ROOT);
            if (!name.contains("trailer") || name.contains("fan") || name.contains("reaction"))
                continue;
            int rank = (name.contains("official") ? 4 : 1) + (name.contains("main") ? 2 : 0) +
                       (name.contains("teaser") ? -1 : 0);
            if (rank > score) {
                score = rank;
                best = t;
            }
        }
        return best;
    }

    private void chooseTrailer() {
        ScraperTrailer t = primaryTrailer();
        if (t != null)
            PreviewTrailer.show((Activity)getContext(), t);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(deliverEnrichment);
        pendingEnrichment = null;
        enrichmentDeliveryScheduled = false;
        ViewParent shell = getParent();
        while (shell != null) {
            if (shell instanceof TopNavigation) {
                ((TopNavigation)shell).setDetailsExpanded(false);
                break;
            }
            shell = shell.getParent();
        }
        enrichmentGeneration++;
        if (enrichmentTask != null)
            enrichmentTask.cancel(true);
        if (artworkLoader != null)
            artworkLoader.cancelRequest(cardArtwork);
        if (reviews != null)
            reviews.close();
        if (observedActions != null) {
            observedActions.unregisterObserver(actionObserver);
            observedActions = null;
        }
        for (Presenter.ViewHolder h : providerHolders)
            providerPresenter.onUnbindViewHolder(h);
        providerHolders.clear();
        for (Presenter.ViewHolder h : cards)
            presenter.onUnbindViewHolder(h);
        cards.clear();
        for (PreviewLandscapeCard card : landscapeCards)
            card.release();
        landscapeCards.clear();
        for (PreviewLandscapeCard card : extraCards)
            card.release();
        extraCards.clear();
        super.onDetachedFromWindow();
    }
}
