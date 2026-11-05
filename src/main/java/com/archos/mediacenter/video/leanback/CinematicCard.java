package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;

/** Shared artwork region, visually active while its real action buttons hold focus. */
public final class CinematicCard extends FrameLayout {
    public CinematicCard(Context c) {
        super(c);
        setFocusable(false);
        setClickable(false);
        setClipChildren(false);
        setClipToPadding(false);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xff1b1d22);
        background.setCornerRadius(PreviewDialog.dp(c, 12));
        setBackground(background);
        setClipToOutline(true);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        setForeground(new PreviewFocusGlow(getContext(), 12));
    }

    @Override
    public void clearChildFocus(View child) {
        super.clearChildFocus(child);
        post(() -> {
            if (!hasFocus()) {
                setForeground(null);
            }
        });
    }

    /**
     * Readability shade behind the hero text, shared by Home and Details (owner, 3 Nov 2026, option 2): gentle
     * black from the left edge (42%) fading to nothing by 58% of the width, so the right side of the artwork
     * keeps its own colour and the card is never dimmed as a whole.
     */
    public void addTextScrim() {
        View shade = new View(getContext());
        shade.setTag("details.text.scrim");
        shade.setBackground(new LeftFade());
        addView(shade, new LayoutParams(-1, -1));
    }

    /**
     * A soft, vignette-like dark patch behind the hero's text block only (owner, 4 Nov 2026, 0.155 notes #1), so
     * the year · genre line and synopsis stay readable on bright skies while the rest of the artwork is untouched.
     * The patch follows the block: it is an ellipse around the union of the given views, redrawn when they move.
     */
    public View addTextPatch(View... block) {
        TextPatch patch = new TextPatch(getContext(), this, block);
        patch.setTag("hero.text.patch");
        addView(patch, behind(block), new LayoutParams(-1, -1));
        for (View part : block) {
            part.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> patch.invalidate());
        }
        return patch;
    }

    /** The child index just below the card child that holds the text, so the patch draws under the text. */
    private int behind(View[] block) {
        if (block.length == 0) {
            return getChildCount();
        }
        View holder = block[0];
        while (holder != null && holder.getParent() != this) {
            holder = holder.getParent() instanceof View ? (View) holder.getParent() : null;
        }
        return holder == null ? getChildCount() : indexOfChild(holder);
    }

    public void addScrim() {
        View shade = new View(getContext());
        int[] colours = new int[] {
            0xe6071522, 0xb3071522, 0x00071522
        };
        shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colours));
        addView(shade, new LayoutParams(-1, -1));
    }

    private static final class LeftFade extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int[] colours = new int[] {
                0x6B000000, 0x2E000000, 0x00000000
            };
            float[] stops = new float[] {
                0, .30f, .58f
            };
            paint.setShader(new LinearGradient(b.left, 0, b.right, 0, colours, stops, Shader.TileMode.CLAMP));
            canvas.drawRect(b, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter filter) {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    /** Draws the dark ellipse; package-visible bounds so tests can check it covers the text. */
    static final class TextPatch extends View {
        private static final int CENTRE = 0x8C000000;
        private static final int MIDDLE = 0x52000000;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final FrameLayout card;
        private final View[] block;

        TextPatch(Context c, FrameLayout card, View[] block) {
            super(c);
            this.card = card;
            this.block = block;
            setFocusable(false);
            setClickable(false);
            setWillNotDraw(false);
        }

        /** The text block's union in card coordinates, or null when nothing in it is showing. */
        RectF textBounds() {
            RectF union = null;
            Rect part = new Rect();
            for (View view : block) {
                if (view.getVisibility() != VISIBLE || view.getWidth() == 0 || view.getParent() == null) {
                    continue;
                }
                part.set(0, 0, view.getWidth(), view.getHeight());
                card.offsetDescendantRectToMyCoords(view, part);
                if (union == null) {
                    union = new RectF(part);
                } else {
                    union.union(part.left, part.top, part.right, part.bottom);
                }
            }
            return union;
        }

        /** The ellipse that is drawn: the text block grown on every side so its edge fades well clear of the text. */
        RectF patchBounds() {
            RectF text = textBounds();
            if (text == null) {
                return null;
            }
            float grow = PreviewDialog.dp(getContext(), 70);
            return new RectF(text.left - grow, text.top - grow, text.right + grow, text.bottom + grow);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            RectF patch = patchBounds();
            if (patch == null) {
                return;
            }
            float radius = Math.max(patch.width(), patch.height()) / 2f;
            int[] colours = new int[] {
                CENTRE, MIDDLE, 0x00000000
            };
            float[] stops = new float[] {
                0, .55f, 1
            };
            canvas.save();
            canvas.translate(patch.centerX(), patch.centerY());
            canvas.scale(patch.width() / (2f * radius), patch.height() / (2f * radius));
            paint.setShader(new RadialGradient(0, 0, radius, colours, stops, Shader.TileMode.CLAMP));
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }
    }
}
