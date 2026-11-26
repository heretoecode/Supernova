package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.appcompat.widget.AppCompatImageView;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.util.*;

/** Fixed exposed-card composition from HOME §11. Neighbours are affordances, never focus peers. */
final class PreviewFeaturedCard extends FrameLayout {
    PreviewFeaturedCard(Context c, List<Entry> entries, int index, Runnable info, Runnable play) {
        this(c, entries, index, info, play, view -> info.run(), entry -> "Play");
    }

    /**
     * The Home hero, the same design as Details (page sheet HOME.md): logo artwork or the name, the metadata row, the
     * description on two lines (never the tagline), buttons fixed at the bottom left: Play (or "Resume S1 E5 · 16 min
     * left"), then More, which opens its menu beside the button.
     */
    PreviewFeaturedCard(Context c, List<Entry> entries, int index, Runnable info, Runnable play,
                        java.util.function.Consumer<View> more, java.util.function.Function<Entry, String> playLabel) {
        super(c);
        setClipChildren(false);
        setClipToPadding(false);
        setTag("semantic:home.featured");
        int current = entries.isEmpty() ? 0 : Math.floorMod(index, entries.size());
        Entry entry = entries.isEmpty() ? null : entries.get(current);
        for (int direction : new int[] {
            -1, 1
        })
            if (entries.size() > 1) {
                ImageView neighbour = art(c, entries.get(Math.floorMod(current + direction, entries.size())));
                neighbour.setAlpha(1f);
                neighbour.setTag(direction < 0 ? "semantic:featured.previous" : "semantic:featured.next");
                LayoutParams size = new LayoutParams(-1, -1);
                size.gravity = direction < 0 ? Gravity.LEFT : Gravity.RIGHT;
                addView(neighbour, size);
            }
        CinematicCard card = new CinematicCard(c);
        card.setTag("semantic:featured.active");
        rounded(card, c);
        card.setElevation(dp(8));
        addView(card, new LayoutParams(-1, -1, Gravity.CENTER));
        card.addView(art(c, entry), new LayoutParams(-1, -1));
        card.addTextScrim(); // same readability shade as the Details hero

        LinearLayout copy = new LinearLayout(c);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setTag("featured.copy");
        copy.setPadding(dp(24), dp(22), dp(18), dp(18));
        card.addView(copy, new LayoutParams(-1, -1));
        // The next-episode label above the logo (owner, 25 Nov; placement A), the same as on Details.
        TextView nextEpisode = NextEpisodeLabel.create(c);
        LinearLayout.LayoutParams nextSize = new LinearLayout.LayoutParams(-2, dp(NextEpisodeLabel.LABEL_DP));
        nextSize.bottomMargin = NextEpisodeLabel.gap(c);
        copy.addView(nextEpisode, nextSize);
        TextView title = text(entry == null ? "" : PreviewPages.displayName(entry), 30);
        title.setTag("semantic:featured.title");
        title.setSingleLine(true);
        title.setShadowLayer(dp(2), 0, dp(1), Color.BLACK);
        title.setTypeface(null, Typeface.BOLD);
        // Text and logo sit at the bottom of the title box, just above the metadata (0.155 notes #2).
        title.setGravity(Gravity.BOTTOM | Gravity.START);
        copy.addView(title, new LinearLayout.LayoutParams(-1, dp(86)));
        boolean series = entry != null && (entry.media instanceof Tvshow || entry.media instanceof Episode);
        NextEpisodeLabel.bindCached(nextEpisode, title, dp(86), series ? entry.onlineId : 0);
        if (entry != null)
            OfficialTitleArtwork.bindStable(title, entry.media);
        else
            title.setForeground(new HomePlaceholder.Skeleton());
        TextView metadata = text("", 18);
        metadata.setMaxLines(2);
        metadata.setTag("semantic:featured.metadata");
        metadata.setPadding(0, dp(4), 0, dp(4));
        LinearLayout.LayoutParams metadataSize = new LinearLayout.LayoutParams(-1, -2);
        metadataSize.topMargin = dp(18);
        copy.addView(metadata, metadataSize);
        LinearLayout badges = new LinearLayout(c);
        LinearLayout.LayoutParams badgeSize = new LinearLayout.LayoutParams(-1, -2);
        badgeSize.topMargin = dp(8);
        copy.addView(badges, badgeSize);
        SharedMetadata.bindCard(metadata, badges,
            entry != null && entry.homeMetadata != null ? entry.homeMetadata : SharedMetadata.entry(c, entry, true),
            true);
        // Year · genre in Medium, pure white, with a firmer shadow (0.155 notes #1).
        metadata.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        metadata.setTextColor(Color.WHITE);
        metadata.setShadowLayer(dp(3), 0, dp(1), 0xF0000000);
        String plot = entry == null ? ""
            : entry.media instanceof Tvshow ? ((Tvshow) entry.media).getPlot()
            : entry.media instanceof Video ? ((Video) entry.media).getDescriptionBody()
                                            : "";
        if (plot == null)
            plot = "";
        TextView synopsis = text(plot, 16);
        // The description on two lines; no tagline anywhere (owner, 13 Nov 2026; RULES §14).
        synopsis.setMaxLines(2);
        synopsis.setLineSpacing(dp(2), 1);
        if (plot.trim().isEmpty())
            synopsis.setVisibility(GONE);
        synopsis.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        synopsis.setShadowLayer(SharedMetadata.heroShadow(c), 0, dp(1), 0xCC000000);
        synopsis.setTag("semantic:featured.synopsis");
        LinearLayout.LayoutParams synopsisSize = new LinearLayout.LayoutParams(-1, -2);
        synopsisSize.topMargin = dp(12);
        copy.addView(synopsis, synopsisSize);
        card.addTextPatch(title, metadata, synopsis);
        copy.addView(new View(c), new LinearLayout.LayoutParams(1, 0, 1));
        LinearLayout actions = new LinearLayout(c);
        actions.setClipChildren(false);
        copy.addView(actions, new LinearLayout.LayoutParams(-1, dp(44)));
        // One button, More (owner, 19 Nov; HOME.md): its menu holds Details, Playback and This Title.
        TextView[] holder = {null};
        holder[0] = action("More", () -> more.accept(holder[0]));
        PreviewIcon.apply(holder[0], "more", 18);
        // The tag shared since 0.157 (Down-then-Up and the Details glide).
        holder[0].setTag("hero:info");
        actions.addView(holder[0], new LinearLayout.LayoutParams(-2, -1));
    }

    @Override
    protected void onMeasure(int w, int h) {
        int width = MeasureSpec.getSize(w);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            boolean active = "semantic:featured.active".equals(child.getTag());
            p.width = Math.round(width * (active ? .86f : .86f));
            p.height = -1;
            p.gravity = (active ? Gravity.CENTER : p.gravity | Gravity.CENTER_VERTICAL);
            if (!active)
                child.setTranslationX(
                    (p.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT ? -width * .80f : width * .80f);
        }
        View active = findViewWithTag("semantic:featured.active");
        if (active instanceof ViewGroup) {
            View copy = active.findViewWithTag("featured.copy");
            copy.getLayoutParams().width = Math.round(width * .86f * .55f);
            // The synopsis stays in the left half of the card, clear of the brightest part of most artwork.
            View synopsis = active.findViewWithTag("semantic:featured.synopsis");
            if (synopsis != null)
                synopsis.getLayoutParams().width = Math.round(width * .86f * .50f) - copy.getPaddingLeft();
        }
        super.onMeasure(w, h);
    }

    private ImageView art(Context c, Entry e) {
        Uri uri = e == null ? null : e.backdrop != null ? e.backdrop : e.media.getPosterUri();
        boolean backdrop = e != null && e.backdrop != null;
        ImageView image = new AppCompatImageView(c) {
            private boolean detached;
            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                if (detached && getDrawable() == null && uri != null)
                    loadArtwork(this, uri, backdrop);
                detached = false;
            }
            @Override
            protected void onDetachedFromWindow() {
                detached = true;
                super.onDetachedFromWindow();
            }
            @Override
            protected void onDraw(Canvas canvas) {
                int saved = canvas.save();
                Path mask = new Path();
                mask.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), dp(12), dp(12),
                    Path.Direction.CW);
                canvas.clipPath(mask);
                super.onDraw(canvas);
                canvas.restoreToCount(saved);
            }
        };
        image.setScaleType(backdrop ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_CENTER);
        rounded(image, c);
        if (uri != null)
            loadArtwork(image, uri, backdrop);
        return image;
    }

    private void loadArtwork(ImageView image, Uri uri, boolean backdrop) {
        RequestCreator request = Picasso.get().load(uri).resize(dp(960), dp(540)).noFade();
        if (backdrop)
            request.centerCrop();
        else
            request.centerInside();
        ArtworkRequest.load(
            image, uri, 0, "home.featured", backdrop ? "backdrop" : "poster", request, true);
    }

    void resetMotion() {
        resetMotion(this);
    }

    private void resetMotion(View view) {
        view.animate().cancel();
        view.setTranslationX(0);
        view.setTranslationY(0);
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
                resetMotion(((ViewGroup) view).getChildAt(i));
        requestLayout();
    }

    void releaseArtwork() {
        resetMotion();
        cancel(this);
    }

    private void rounded(View v, Context c) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xff1b1d22);
        bg.setCornerRadius(dp(12));
        v.setBackground(bg);
        v.setClipToOutline(true);
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(getContext());
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setIncludeFontPadding(false);
        t.setShadowLayer(dp(3), 0, dp(1), Color.BLACK);
        t.setEllipsize(TextUtils.TruncateAt.END);
        return t;
    }

    private TextView action(String label, Runnable run) {
        return TvButton.create(getContext(), label, run, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        cancel(this);
        super.onDetachedFromWindow();
    }

    private void cancel(View v) {
        if (v instanceof ImageView)
            ArtworkRequest.cancel((ImageView) v);
        if (v instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++)
                cancel(((ViewGroup) v).getChildAt(i));
    }

    private int dp(int n) {
        return PreviewDialog.dp(getContext(), n);
    }
}
