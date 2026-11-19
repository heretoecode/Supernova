package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.PixelCopy;
import android.widget.*;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.foundation.DoubleRingDrawable;
import com.archos.mediacenter.video.leanback.OfficialTitleArtwork;
import com.archos.mediacenter.video.leanback.PreviewAccent;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediacenter.video.leanback.PreviewUtilityBackground;
import com.archos.mediacenter.video.leanback.ThinSpinner;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/** Cached artwork only. Visibility follows the existing player preparation timer. */
final class PreviewPlaybackLoading extends FrameLayout {
    private final ImageView artwork;
    private final TextView title, episode;
    private Uri cachedArtwork, source, fileSource;
    private boolean requestedArtwork;

    PreviewPlaybackLoading(Context context, Intent intent) {
        super(context);
        setId(R.id.progress_indicator);
        setVisibility(VISIBLE);
        setBackground(new PreviewUtilityBackground(context));
        setFocusable(false);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        artwork = new ImageView(context);
        artwork.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(artwork, new LayoutParams(-1, -1));
        // Owner decision (2 Nov 2026): the artwork is shown crystal clear, with no tint or
        // gradient over it. Text legibility comes from a shadow on the text itself (see text()).
        LinearLayout labels = new LinearLayout(context);
        labels.setOrientation(LinearLayout.VERTICAL);
        // PP3 / PL1 (agreed 18 Nov 2026): one title block for Preparing playback and the player, in the player's
        // place (top left) and sizes: the title or logo in a two-line box, then the details line, badge first.
        LayoutParams labelsParams = new LayoutParams(dp(560), -2, Gravity.START | Gravity.TOP);
        labelsParams.leftMargin = dp(28);
        labelsParams.topMargin = dp(20);
        addView(labels, labelsParams);
        title = text("", 23);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        // The logo's space is kept from the start (0.157 Shield review #7): a fixed two-line box, text at its foot,
        // so nothing below moves when the logo or the full title arrives.
        title.setLines(2);
        title.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
        title.setEllipsize(TextUtils.TruncateAt.END);
        labels.addView(title, new LinearLayout.LayoutParams(dp(380), dp(64)));
        episode = text("", 13);
        episode.setTextColor(0xFFCCDFEA);
        episode.setMaxLines(1);
        episode.setEllipsize(TextUtils.TruncateAt.END);
        episode.setPadding(0, dp(4), 0, dp(10));
        // PP1: the line keeps its height from the start, so the badge arriving never moves anything.
        episode.setMinLines(1);
        labels.addView(episode);
        // The age rating in its outlined badge on its own line (owner, 12 Nov 2026; player title block).
        age = text("", 14);
        age.setPadding(dp(7), dp(2), dp(7), dp(2));
        android.graphics.drawable.GradientDrawable badge = new android.graphics.drawable.GradientDrawable();
        badge.setCornerRadius(dp(4));
        badge.setStroke(Math.max(1, dp(1)), 0xDDFFFFFF);
        age.setBackground(badge);
        // PP2 (decided 18 Nov 2026): the badge is the first item of the details line; this box is no longer shown.
        age.setVisibility(GONE);
        LinearLayout.LayoutParams ageSize = new LinearLayout.LayoutParams(-2, -2);
        ageSize.bottomMargin = dp(10);
        labels.addView(age, ageSize);
        LinearLayout status = new LinearLayout(context);
        status.setGravity(Gravity.CENTER_VERTICAL);
        status.setPadding(0, dp(18), 0, 0);
        labels.addView(status);
        ProgressBar spinner = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        spinner.setIndeterminateDrawable(BuildConfig.FOUNDATION
                ? new DoubleRingDrawable()
                : new ThinSpinner());
        if (!BuildConfig.FOUNDATION)
            spinner.setIndeterminateTintList(ColorStateList.valueOf(
                PreviewAccent.color(context)));
        status.addView(spinner, new LinearLayout.LayoutParams(dp(28), dp(28)));
        TextView starting = text("Preparing playback…", 16);
        starting.setPadding(dp(14), 0, dp(8), 0);
        status.addView(starting);
        TextView buffer = text("", 13);
        buffer.setId(R.id.buffer_percentage);
        status.addView(buffer);
        Object value = intent.getSerializableExtra(PlayerService.VIDEO);
        if (value instanceof Video) {
            Video video = (Video) value;
            source = video.getUri();
            fileSource = video.getFileUri();
            cachedArtwork = video.getPreviewBackdrop();
            OfficialTitleArtwork.bind(title, video, true);
            title.setText(video instanceof Episode ? ((Episode) video).getShowName() : video.getName());
            long online = video instanceof Movie ? ((Movie) video).getOnlineId() : 0;
            PreviewLibraryLoader.Snapshot library =
                PreviewLibraryLoader.memoryCache();
            if (online <= 0 && library != null)
                for (PreviewLibraryLoader.Entry entry : library.episodes)
                    if (((Video) entry.media).getId() == video.getId()) {
                        online = entry.onlineId;
                        break;
                    }
            Episode selected = video instanceof Episode ? (Episode) video : null;
            if (selected != null)
                details = episodeLine(selected.getSeasonNumber(), selected.getEpisodeNumber(),
                                      selected.getEpisodeName());
            else if (video instanceof Movie && ((Movie) video).getYear() > 0)
                details = String.valueOf(((Movie) video).getYear());
            episode.setText(details);
            loadAge(selected != null, selected != null ? showOnlineId(library, video) : online);
        }
        episode.setVisibility(VISIBLE);
    }

    private TextView age;
    /** The details line without the badge ("Season 2 · Episode 3 · Title", or a film's year). */
    private String details = "";

    /** "Season 2 · Episode 8 · We Don't Talk Anymore", the same as Details and the player. */
    static String episodeLine(int season, int episode, String name) {
        String line = "Season " + season + " · Episode " + episode;
        return name == null || name.trim().isEmpty() ? line : line + " · " + name.trim();
    }

    private static long showOnlineId(PreviewLibraryLoader.Snapshot library, Video video) {
        if (library == null)
            return 0;
        for (PreviewLibraryLoader.Entry entry : library.episodes)
            if (((Video) entry.media).getId() == video.getId())
                return entry.onlineId;
        return 0;
    }

    /** Reads the age rating from the metadata cache only: Preparing playback never waits on the network for it. */
    private void loadAge(boolean show, long onlineId) {
        if (onlineId <= 0)
            return;
        Context app = getContext().getApplicationContext();
        String kind = show ? "tv" : "movie";
        String country = com.archos.mediacenter.video.streaming.StreamingRepository.country(app);
        com.archos.mediacenter.video.streaming.StreamingRepository.IO.submit(() -> {
            org.json.JSONObject cached = com.archos.mediacenter.video.leanback.PreviewMetadataCache.read(
                app, kind, onlineId, show ? "content_ratings" : "release_dates");
            String rating = PlaybackAgeOverlay.loadCertificate(cached, country, () -> null);
            post(() -> {
                boolean known = PlaybackAgeOverlay.valid(rating);
                age.setText(known ? rating : "");
                episode.setText(TitleBlockLine.line(getContext(), known ? rating : "", details));
            });
        });
    }

    private boolean waitingForFrame;
    private int generation;

    void begin() {
        if (cachedArtwork == null) {
            Picasso.get().cancelRequest(artwork);
            artwork.setImageDrawable(null);
        }
        generation++;
        waitingForFrame = false;
        animate().cancel();
        setAlpha(1f);
        setVisibility(VISIBLE);
    }

    void waitForFrame(View root) {
        if (waitingForFrame || getVisibility() != VISIBLE)
            return;
        waitingForFrame = true;
        final long started = SystemClock.uptimeMillis();
        final int expected = generation;
        final SurfaceView surface = root.findViewById(R.id.surface_view);
        post(new Runnable() {
            @Override
            public void run() {
                if (expected != generation)
                    return;
                if (!isAttachedToWindow() || getVisibility() != VISIBLE) {
                    waitingForFrame = false;
                    return;
                }
                if (Player.sPlayer != null && Player.sPlayer.hasRenderedPreviewFrame()) {
                    reveal();
                    return;
                }
                if (Build.VERSION.SDK_INT >= 26 && surface != null && surface.isShown()
                    && surface.getHolder().getSurface().isValid()) {
                    Bitmap probe =
                        Bitmap.createBitmap(32, 18, Bitmap.Config.ARGB_8888);
                    try {
                        PixelCopy.request(surface, probe, result -> {
                            if (expected != generation) {
                                probe.recycle();
                                return;
                            }
                            boolean frame = false;
                            if (result == PixelCopy.SUCCESS) {
                                int min = 255, max = 0;
                                for (int y = 0; y < 18; y++)
                                    for (int x = 0; x < 32; x++) {
                                        int pixel = probe.getPixel(x, y),
                                            l = (Color.red(pixel) + Color.green(pixel)
                                                    + Color.blue(pixel))
                                            / 3;
                                        min = Math.min(min, l);
                                        max = Math.max(max, l);
                                    }
                                frame = max > 8 && max - min > 3;
                            }
                            probe.recycle();
                            if (frame)
                                reveal();
                            else
                                retry(this, started);
                        }, new Handler(Looper.getMainLooper()));
                        return;
                    } catch (IllegalArgumentException invalid) {
                        probe.recycle();
                    }
                }
                retry(this, started);
            }
        });
    }

    private void retry(Runnable check, long started) {
        if (SystemClock.uptimeMillis() - started > 600 && Player.sPlayer != null
            && Player.sPlayer.isPlaying() && Player.sPlayer.getCurrentPosition() > 250) {
            reveal();
            return;
        }
        if (SystemClock.uptimeMillis() - started > 8000) {
            Log.w(
                "NovaPreview", "No first-frame signal; releasing loading overlay to preserve playback controls");
            reveal();
        } else
            postDelayed(check, 80);
    }

    private void reveal() {
        waitingForFrame = false;
        animate()
            .alpha(0f)
            .setDuration(120)
            .withEndAction(() -> {
                setVisibility(GONE);
                setAlpha(1f);
            })
            .start();
    }

    void bind(VideoDbInfo info, String fallback) {
        if (info == null) {
            if (fallback != null)
                title.setText(fallback);
            return;
        }
        OfficialTitleArtwork.bind(title, info);
        title.setText(info.isScraped && info.scraperTitle != null ? info.scraperTitle : fallback);
        boolean show = info.isShow && info.scraperSeasonNr >= 0 && info.scraperEpisodeNr >= 0;
        long id = 0;
        try {
            id = Long.parseLong(info.isShow ? info.scraperShowId : info.scraperMovieId);
        } catch (Exception unavailable) {
        }
        // Episodes: "Season 2 · Episode 8 · Title"; films keep their tagline.
        episode.setText(show
            ? episodeLine(info.scraperSeasonNr, info.scraperEpisodeNr, info.scraperEpisodeName)
            : PreviewTaglines.cached(getContext(), false, id, 0, 0));
        episode.setVisibility(episode.length() == 0 ? GONE : VISIBLE);
        loadAge(info.isShow, id);
        if (info.uri != null && !info.uri.equals(source) && !info.uri.equals(fileSource)) {
            requestedArtwork = false;
            cachedArtwork = null;
            Picasso.get().cancelRequest(artwork);
            artwork.setImageDrawable(null);
        }
    }

    @Override
    protected void onVisibilityChanged(View changed, int visibility) {
        super.onVisibilityChanged(changed, visibility);
        if (artwork == null)
            return;
        if (visibility == VISIBLE && !requestedArtwork && cachedArtwork != null) {
            RequestCreator request = cachedRequest(Picasso.get(), cachedArtwork);
            if (request != null) {
                requestedArtwork = true;
                request.into(artwork);
            }
        } else if (visibility != VISIBLE) {
            Picasso.get().cancelRequest(artwork);
            requestedArtwork = false;
        }
    }

    /** A remote artwork URI may already exist in Picasso's disk cache. Never fetch it at Play time. */
    static RequestCreator cachedRequest(Picasso picasso, Uri uri) {
        String scheme = uri == null ? null : uri.getScheme();
        boolean remote = "https".equals(scheme) || "http".equals(scheme);
        if (!remote && !"file".equals(scheme) && !"content".equals(scheme))
            return null;
        RequestCreator request = picasso.load(uri).resize(1280, 720).centerCrop().noFade();
        if (remote)
            request.networkPolicy(NetworkPolicy.OFFLINE);
        return request;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onVisibilityChanged(this, getVisibility());
    }

    @Override
    protected void onDetachedFromWindow() {
        generation++;
        Picasso.get().cancelRequest(artwork);
        super.onDetachedFromWindow();
    }

    private int dp(int n) {
        return Math.round(n * getResources().getDisplayMetrics().density);
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(getContext());
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        // Soft shadow keeps white text readable on bright artwork without dimming the image.
        t.setShadowLayer(dp(6), 0, dp(1), 0xCC000000);
        return t;
    }
}
