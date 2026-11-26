package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

/** Logical Android TV viewport tokens. Physical resolution is deliberately irrelevant. */
public final class TvLayout {
    private TvLayout() {
    }

    public static float width(Context c) {
        return c.getResources().getConfiguration().screenWidthDp;
    }

    public static float height(Context c) {
        return c.getResources().getConfiguration().screenHeightDp;
    }

    public static float scale(Context c) {
        return Math.max(.85f, Math.min(1.25f, Math.min(width(c) / 960f, height(c) / 540f)));
    }

    public static int px(Context c, float dp) {
        return Math.round(dp * c.getResources().getDisplayMetrics().density);
    }

    public static int dimension(Context c, float baseline) {
        return px(c, baseline * scale(c));
    }

    /**
     * The one hero size (RULES §14, HOME.md; owner's 0.158 review H1): 1776 x 820 at 1080p, on Home and Details
     * alike, so the card stays put going from one into the other.
     */
    public static int heroWidth(Context c) {
        // RULES §14d (owner, 21 Nov): 1816 × 820 at x 52, the hero's edges on the top bar's edges (was 1776 at 72).
        return Math.round(c.getResources().getDisplayMetrics().widthPixels * 1816f / 1920f);
    }

    public static int heroHeight(Context c) {
        return Math.round(c.getResources().getDisplayMetrics().widthPixels * 820f / 1920f);
    }

    /**
     * The hero's top edge from the top of the screen: just under the top bar (112 px at 1080p), so the first row's
     * cards still peek below the 820 px hero.
     */
    public static int heroTop(Context c) {
        return Math.round(c.getResources().getDisplayMetrics().widthPixels * 112f / 1920f);
    }

    /**
     * One side margin (owner, 18 Nov 2026; RULES §1): the top bar's own edges. The bar's first word box starts here
     * and the clock ends here; library pages, Files and Search line their content up with it. Home has none.
     */
    public static int sideMargin(Context c) {
        return dimension(c, 26);
    }

    public static int safeWidth(Context c) {
        return Math.max(1, px(c, width(c)) - 2 * dimension(c, 28));
    }

    public static int safeHeight(Context c) {
        return Math.max(1, px(c, height(c)) - 2 * dimension(c, 24));
    }

    public static int dialogWidth(Context c) {
        return Math.min(Math.round(safeWidth(c) * .9f),
            Math.max(px(c, 320), Math.min(px(c, 560), Math.round(safeWidth(c) * .40f))));
    }

    public static int gridColumns(Context c) {
        return width(c) < 780 ? 4 : width(c) < 920 ? 5 : width(c) > 1180 ? 8 : 6;
    }

    /** Keep system bars/cutouts outside the shared navigation and safe content region. */
    public static void applyInsets(View root) {
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            if (Build.VERSION.SDK_INT >= 30) {
                Insets edge =
                    insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                v.setPadding(edge.left, edge.top, edge.right, edge.bottom);
            } else
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        root.requestApplyInsets();
    }
}
