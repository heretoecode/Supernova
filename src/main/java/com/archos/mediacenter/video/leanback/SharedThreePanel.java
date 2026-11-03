package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Common geometry and row styling for storage, Settings and Library Health workspaces. */
public class SharedThreePanel extends LinearLayout {
    private boolean constrainViewport;

    /** Only the compact Backup workspace opts out of the ordinary horizontally revealed panes. */
    public void constrainViewport(boolean value) {
        constrainViewport = value;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (constrainViewport && getParent() instanceof View) {
            View viewport = (View)getParent();
            int width = viewport.getWidth() - viewport.getPaddingLeft() - viewport.getPaddingRight();
            if (width > 0)
                widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    public SharedThreePanel(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void panels(View left, View centre, View right) {
        removeAllViews();
        addPanel(left, .24f, true);
        addPanel(centre, .38f, true);
        addPanel(right, .38f, false);
    }

    /** On narrow logical TVs retain usable pane widths and reveal panes with remote focus. */
    public static View viewport(SharedThreePanel panels) {
        Context c = panels.getContext();
        if (TvLayout.width(c) >= 780)
            return panels;
        HorizontalScrollView viewport = new HorizontalScrollView(c);
        viewport.setHorizontalScrollBarEnabled(false);
        viewport.setFillViewport(false);
        viewport.setClipChildren(false);
        viewport.setClipToPadding(false);
        viewport.addView(panels, new FrameLayout.LayoutParams(TvLayout.px(c, 900), -1));
        return viewport;
    }

    public void widths(float left, float centre, float right) {
        float[] widths = {left, centre, right};
        for (int i = 0; i < Math.min(3, getChildCount()); i++) {
            LayoutParams size = (LayoutParams)getChildAt(i).getLayoutParams();
            size.weight = widths[i];
            getChildAt(i).setLayoutParams(size);
        }
    }

    public static void heading(LinearLayout column, String title) {
        column.addView(text(column.getContext(), title, 21));
        divider(column);
    }

    public static void divider(LinearLayout column) {
        View line = new View(column.getContext());
        line.setBackgroundColor(0x447f8996);
        LayoutParams size = new LayoutParams(-1, dp(column.getContext(), 1));
        size.topMargin = dp(column.getContext(), 8);
        size.bottomMargin = dp(column.getContext(), 10);
        column.addView(line, size);
    }

    private void addPanel(View view, float weight, boolean gap) {
        decorate(view);
        LayoutParams size = new LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
        if (gap)
            size.rightMargin = dp(getContext(), 16);
        addView(view, size);
    }

    public static void decorate(View panel) {
        Context context = panel.getContext();
        GradientDrawable surface =
            new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xb3212c39, 0xc00b121d});
        surface.setCornerRadius(dp(context, 16));
        surface.setStroke(dp(context, 1), 0x557f8996);
        panel.setBackground(surface);
        panel.setElevation(dp(context, 4));
        panel.setPadding(dp(context, 20), dp(context, 20), dp(context, 20), dp(context, 20));
        if (Build.VERSION.SDK_INT >= 28) {
            panel.setOutlineAmbientShadowColor(0x337f8996);
            panel.setOutlineSpotShadowColor(0x337f8996);
        }
    }

    public static TextView text(Context context, String value, int size) {
        TextView text = new TextView(context);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.WHITE);
        text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        text.setLineSpacing(dp(context, 3), 1);
        return text;
    }

    public static TextView action(Context context, String label, Runnable action) {
        TextView row = text(context, label, 18);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(context, 8), 0, dp(context, 8), 0);
        row.setMinHeight(dp(context, 50));
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setBackground(focus(context));
        row.setOnClickListener(view -> action.run());
        return row;
    }

    /** Refinement screens opt in; deferred Details/Playback retain the legacy focus factory. */
    public static TextView accentAction(Context context, String label, Runnable action) {
        TextView row = action(context, label, action);
        row.setBackground(accentFocus(context));
        return row;
    }

    /** Permanent inset control surface; row actions retain their separate focus-only outline. */
    public static void rowSeparator(View row) {
        Drawable line = new Drawable() {
            private final Paint paint = new Paint();
            @Override
            public void draw(Canvas canvas) {
                paint.setColor(0x337f8996);
                paint.setStrokeWidth(dp(row.getContext(), 1));
                canvas.drawLine(dp(row.getContext(), 8), getBounds().bottom - 1,
                                getBounds().right - dp(row.getContext(), 8), getBounds().bottom - 1, paint);
            }
            @Override
            public void setAlpha(int alpha) {
            }
            @Override
            public void setColorFilter(ColorFilter filter) {
            }
            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        };
        Drawable existing = row.getForeground();
        row.setForeground(existing == null ? line
                                           : new LayerDrawable(
                                                 new Drawable[] {existing, line}));
    }

    /** Button background from the shared style. Prefer {@link SupernovaStyle#applyButton}, which also sets the content colours. */
    public static StateListDrawable control(Context context, int radius) {
        return SupernovaStyle.button(context, SupernovaStyle.Surface.STANDARD);
    }

    /** Row focus from the shared style: white outline, no fill change. */
    public static StateListDrawable focus(Context context) {
        return SupernovaStyle.rowFocus(context);
    }

    public static StateListDrawable accentFocus(Context context) {
        return SupernovaStyle.rowFocus(context);
    }

    public static int dp(Context context, int value) {
        return PreviewDialog.dp(context, value);
    }
}
