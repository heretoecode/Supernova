package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import com.squareup.picasso.Picasso;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;

/** Shared landscape discovery/episode card with clean artwork and boundary focus. */
final class PreviewLandscapeCard extends LinearLayout {
    private final Drawable playGlyph;
    final ImageView image, availability;
    final TextView title, metadata;
    final ProgressBar progress;
    private final PreviewCardPresenter.Card visual;

    PreviewLandscapeCard(Context c) {
        super(c);
        setOrientation(VERTICAL);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setClipChildren(false);
        setClipToPadding(false);
        visual = new PreviewCardPresenter.Card(
            c, PreviewCardPresenter.Style.CONTINUE);
        visual.setFocusable(false);
        visual.setFocusableInTouchMode(false);
        visual.setLandscapeAspect(16f / 9);
        addView(visual, new LayoutParams(-1, -1));
        image = visual.image;
        title = visual.titleView();
        metadata = visual.subtitleView();
        progress = visual.progressView();
        FrameLayout body = (FrameLayout) visual.getChildAt(0);
        availability = new ImageView(c);
        availability.setImageDrawable(new PreviewIcon("play"));
        playGlyph = availability.getDrawable();
        availability.setVisibility(INVISIBLE);
        body.addView(availability, new FrameLayout.LayoutParams(dp(28), dp(28), Gravity.CENTER));
        setOnFocusChangeListener((v, focus) -> {
            visual.setPresentationFocus(focus);
            // No play icon on library items (D16, RULES §14); a service card's logo stays.
            if (availability.getDrawable() == playGlyph)
                availability.setVisibility(INVISIBLE);
        });
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int width = MeasureSpec.getSize(widthSpec);
        visual.getChildAt(0).getLayoutParams().height = Math.round(width * 9f / 16);
        super.onMeasure(widthSpec, heightSpec);
    }

    void bind(String name, String detail, Uri art, boolean localIndicator) {
        bind(name, detail, art, localIndicator, 0, "details", "landscape");
    }

    void bind(String name, String detail, Uri art, boolean localIndicator, long media, String surface, String type) {
        title.setText(name);
        metadata.setText(detail);
        metadata.setVisibility(detail == null || detail.isEmpty() ? GONE : VISIBLE);
        availability.setTag(localIndicator);
        availability.setVisibility(INVISIBLE);
        ArtworkRequest.cancel(image);
        ArtworkRequest.cancel(availability);
        availability.setImageDrawable(playGlyph);
        availability.clearColorFilter();
        availability.setImageAlpha(255);
        image.setImageDrawable(null);
        if (art != null)
            ArtworkRequest.load(image, art, media, surface, type,
                Picasso.get().load(art).resize(dp(352), dp(198)).centerCrop().noFade());
        setContentDescription(name + (detail == null ? "" : " · " + detail));
    }

    /**
     * The season card (DETAILS.md › Episodes tab, `tv/26`): the same size as an episode card; the season's own 2:3
     * poster whole on the left; "Season 1", "10 episodes · 2 watched" and "Open season ›" beside it.
     */
    void bindSeason(Uri poster, String name, String line) {
        title.setText("");
        metadata.setText("");
        metadata.setVisibility(GONE);
        availability.setVisibility(INVISIBLE);
        ArtworkRequest.cancel(image);
        image.setImageDrawable(new android.graphics.drawable.ColorDrawable(0xFF1E2126));
        FrameLayout body = (FrameLayout) visual.getChildAt(0);
        View old = body.findViewWithTag("season.overlay");
        if (old != null)
            body.removeView(old);
        LinearLayout overlay = new LinearLayout(getContext());
        overlay.setTag("season.overlay");
        overlay.setGravity(Gravity.CENTER_VERTICAL);
        ImageView picture = new ImageView(getContext());
        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int height = Math.round(PreviewCardPresenter.Card.ROW_WIDTH_DP * 9f / 16);
        overlay.addView(picture, new LinearLayout.LayoutParams(dp(height * 2 / 3), -1));
        LinearLayout words = new LinearLayout(getContext());
        words.setOrientation(VERTICAL);
        words.setPadding(dp(12), 0, dp(8), 0);
        TextView heading = text(16);
        heading.setTextColor(0xFFFFFFFF);
        heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        heading.setText(name);
        words.addView(heading);
        TextView second = text(12);
        second.setText(line);
        words.addView(second);
        TextView open = text(12);
        open.setText("Open season ›");
        open.setPadding(0, dp(8), 0, 0);
        words.addView(open);
        overlay.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        body.addView(overlay, new FrameLayout.LayoutParams(-1, -1));
        if (poster != null)
            Picasso.get().load(poster).resize(dp(height * 2 / 3), dp(height)).centerCrop().noFade().into(picture);
        setContentDescription(name + " · " + line + " · Open season");
    }

    void release() {
        ArtworkRequest.cancel(image);
        ArtworkRequest.cancel(availability);
        image.setImageDrawable(null);
    }

    private TextView text(int size) {
        TextView text = new TextView(getContext());
        text.setTextSize(size);
        text.setTextColor(0xffe1e9ef);
        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        return text;
    }

    private int dp(int n) {
        return PreviewDialog.dp(getContext(), n);
    }
}
