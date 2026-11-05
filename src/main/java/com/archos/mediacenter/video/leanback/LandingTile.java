package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.R;

/**
 * The one landing-page tile (owner, 3 Nov 2026), shared by Settings (4 × 2) and Network &amp; Files (3 × 2):
 * charcoal glass at rest, white box with dark text on focus; icon beside a one-line Medium title, description
 * underneath; the heading sits at a fixed share of the tile height so every row lines up.
 */
public final class LandingTile {
    /** Measured 2 Nov 2026: 35px titles are the largest that keep every title on one line at 1080p. */
    public static final float TITLE_SP = 17.5f, DESCRIPTION_SP = 12.5f, HEADING_OFFSET = .30f;

    private LandingTile() {
    }

    /** {@code scale} is the page's screen scale (1 at 960×540 dp). */
    public static LinearLayout create(Context c, String iconKind, String title, String description, float scale) {
        LinearLayout card = new LinearLayout(c) {
            @Override
            protected void onSizeChanged(int w, int h, int ow, int oh) {
                super.onSizeChanged(w, h, ow, oh);
                int top = Math.round(h * HEADING_OFFSET);
                if (getPaddingTop() != top)
                    post(() -> setPadding(getPaddingLeft(), top, getPaddingRight(), getPaddingBottom()));
            }
        };
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.TOP);
        card.setPadding(dp(c, 18, scale), dp(c, 20, scale), dp(c, 18, scale), dp(c, 14, scale));
        card.setFocusable(true);
        card.setFocusableInTouchMode(true);
        card.setContentDescription(title + ". " + description);
        LinearLayout heading = new LinearLayout(c);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        ImageView icon = new ImageView(c);
        icon.setImageDrawable(new PreviewIcon(iconKind));
        heading.addView(icon, new LinearLayout.LayoutParams(dp(c, 22, scale), dp(c, 22, scale)));
        TextView name = text(c, title, TITLE_SP * scale);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setTag("landing.tile.title");
        LinearLayout.LayoutParams nameSize = new LinearLayout.LayoutParams(0, -2, 1);
        nameSize.leftMargin = dp(c, 9, scale);
        heading.addView(name, nameSize);
        card.addView(heading, new LinearLayout.LayoutParams(-1, -2));
        TextView body = text(c, description, DESCRIPTION_SP * scale);
        body.setLineSpacing(dp(c, 2, scale), 1);
        SupernovaStyle.markSecondary(body);
        LinearLayout.LayoutParams bodySize = new LinearLayout.LayoutParams(-1, -2);
        bodySize.topMargin = dp(c, 8, scale);
        card.addView(body, bodySize);
        SupernovaStyle.applyButton(card, SupernovaStyle.Surface.TILE);
        return card;
    }

    /** Replaces a tile's description (live status tiles such as Scan now). */
    public static void describe(LinearLayout card, String description) {
        if (card.getChildCount() < 2 || !(card.getChildAt(1) instanceof TextView))
            return;
        TextView body = (TextView)card.getChildAt(1);
        if (!description.contentEquals(body.getText()))
            body.setText(description);
        TextView name = card.findViewWithTag("landing.tile.title");
        card.setContentDescription((name == null ? "" : name.getText() + ". ") + description);
    }

    /** Renames a tile (Scan now becomes "Scanning…" while it runs). */
    public static void rename(LinearLayout card, String title) {
        TextView name = card.findViewWithTag("landing.tile.title");
        if (name != null && !title.contentEquals(name.getText()))
            name.setText(title);
    }

    /** The amber count in the tile's top-right corner (0.156 #41); none when the count is zero. */
    public static void badge(LinearLayout card, int count) {
        card.setForeground(count <= 0 ? null : new Badge(card.getContext(), count));
        card.setTag(R.id.preview_diagnostic_semantic, count <= 0 ? null : "landing.badge." + count);
    }

    static final class Badge extends android.graphics.drawable.Drawable {
        static final int AMBER = 0xFFF2B544;
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        private final float density;
        private final String text;

        Badge(Context c, int count) {
            density = c.getResources().getDisplayMetrics().density;
            text = count > 99 ? "99+" : String.valueOf(count);
        }

        String text() {
            return text;
        }

        @Override
        public void draw(android.graphics.Canvas canvas) {
            android.graphics.Rect b = getBounds();
            float radius = 13 * density;
            float cx = b.right - 28 * density;
            float cy = b.top + 28 * density;
            paint.setColor(AMBER);
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setColor(0xFF111111);
            paint.setTextSize(13 * density);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
            paint.setTextAlign(android.graphics.Paint.Align.CENTER);
            canvas.drawText(text, cx, cy - (paint.descent() + paint.ascent()) / 2, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter filter) {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }
    }

    private static TextView text(Context c, String value, float sp) {
        TextView v = SharedThreePanel.text(c, value, 13);
        v.setTextSize(sp);
        v.setIncludeFontPadding(false);
        return v;
    }

    private static int dp(Context c, float value, float scale) {
        return Math.max(1, Math.round(value * scale * c.getResources().getDisplayMetrics().density));
    }
}
