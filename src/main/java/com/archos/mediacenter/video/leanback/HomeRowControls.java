package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The two round buttons that appear to the left of a Home row when you press Left on its first card (owner,
 * 4 Nov 2026, 0.155 notes #14, mock-ups {@code docs/design/proposals/home-row-controls/00}–{@code 03}): Hide and
 * Move, the same size, side by side, each labelled underneath; the focused one is white. While moving, the Move
 * button stays white and reads "Moving"; Up/Down carry the row, OK or Back drop it.
 */
final class HomeRowControls extends LinearLayout {
    /** What the buttons do; {@link HomeRowMover} supplies it. */
    interface Actions {
        void hide();

        void startMove();

        void step(int direction);

        void finishMove();

        /** Right from Move: back to the row's first card. */
        void backToRow();
    }

    static final int BUTTON_DP = 48;
    static final int COLUMN_DP = 58;
    private static final int UNFOCUSED = 0xFF2B2F37;
    private static final int UNFOCUSED_ICON = 0xFFE6EBF2;
    private static final int FOCUSED_ICON = 0xFF16181C;
    private static final int LABEL_FOCUSED = 0xFFFFFFFF;
    private static final int LABEL = 0xFFAEB4BF;

    final LinearLayout hide;
    final LinearLayout move;
    private final Actions actions;
    private boolean moving;

    HomeRowControls(Context c, Actions actions) {
        super(c);
        this.actions = actions;
        setTag("home.row.controls");
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClipChildren(false);
        hide = column(c, "Hide", RoundIcon.HIDE, "home.row.hide");
        move = column(c, "Move", RoundIcon.MOVE, "home.row.move");
        LayoutParams first = new LayoutParams(dp(COLUMN_DP), LayoutParams.WRAP_CONTENT);
        addView(hide, first);
        LayoutParams second = new LayoutParams(dp(COLUMN_DP), LayoutParams.WRAP_CONTENT);
        second.leftMargin = dp(4);
        addView(move, second);
        hide.setOnKeyListener(this::key);
        move.setOnKeyListener(this::key);
        hide.setOnClickListener(v -> actions.hide());
        move.setOnClickListener(v -> toggleMove());
    }

    /** Total width of the two buttons and the gap between them. */
    static int widthDp() {
        return COLUMN_DP * 2 + 4;
    }

    boolean moving() {
        return moving;
    }

    /** Puts the controls into (or out of) moving mode without calling back. */
    void setMoving(boolean value) {
        moving = value;
        label(move).setText(value ? "Moving" : "Move");
        refresh(move);
        refresh(hide);
    }

    private void toggleMove() {
        if (moving) {
            actions.finishMove();
        } else {
            actions.startMove();
        }
    }

    private boolean key(View view, int code, KeyEvent event) {
        boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
        if (moving) {
            if (code == KeyEvent.KEYCODE_DPAD_UP || code == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (down) {
                    actions.step(code == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
                }
                return true;
            }
            if (code == KeyEvent.KEYCODE_BACK) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    actions.finishMove();
                }
                return true;
            }
            return code == KeyEvent.KEYCODE_DPAD_LEFT || code == KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        if (code == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (down && view == move) {
                hide.requestFocus();
            }
            return true;
        }
        if (code == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (down && view == hide) {
                move.requestFocus();
            } else if (down) {
                actions.backToRow();
            }
            return true;
        }
        return false;
    }

    private LinearLayout column(Context c, String text, int icon, String tag) {
        LinearLayout column = new LinearLayout(c);
        column.setTag(tag);
        column.setOrientation(VERTICAL);
        column.setGravity(Gravity.CENTER_HORIZONTAL);
        column.setFocusable(true);
        column.setFocusableInTouchMode(true);
        column.setClickable(true);
        column.setClipChildren(false);
        View circle = new View(c);
        circle.setBackground(new RoundIcon(icon));
        column.addView(circle, new LayoutParams(dp(BUTTON_DP), dp(BUTTON_DP)));
        TextView label = new TextView(c);
        label.setText(text);
        label.setTextSize(13);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        label.setGravity(Gravity.CENTER_HORIZONTAL);
        label.setSingleLine(true);
        LayoutParams labelSize = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        labelSize.topMargin = dp(8);
        column.addView(label, labelSize);
        column.setOnFocusChangeListener((v, focused) -> refresh(column));
        refresh(column);
        return column;
    }

    private void refresh(LinearLayout column) {
        boolean white = column.isFocused() || moving && column == move;
        RoundIcon icon = (RoundIcon) column.getChildAt(0).getBackground();
        icon.setFocused(white);
        label(column).setTextColor(white ? LABEL_FOCUSED : LABEL);
    }

    private static TextView label(LinearLayout column) {
        return (TextView) column.getChildAt(1);
    }

    static boolean isWhite(LinearLayout column) {
        return ((RoundIcon) column.getChildAt(0).getBackground()).focused;
    }

    static String labelOf(LinearLayout column) {
        return label(column).getText().toString();
    }

    private int dp(int value) {
        return PreviewDialog.dp(getContext(), value);
    }

    /** A filled circle with a line icon: eye with a slash (Hide) or a single up-and-down arrow (Move). */
    static final class RoundIcon extends Drawable {
        static final int HIDE = 0;
        static final int MOVE = 1;
        private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int kind;
        private boolean focused;

        RoundIcon(int kind) {
            this.kind = kind;
            line.setStyle(Paint.Style.STROKE);
            line.setStrokeCap(Paint.Cap.ROUND);
            line.setStrokeJoin(Paint.Join.ROUND);
        }

        void setFocused(boolean value) {
            focused = value;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float size = Math.min(b.width(), b.height());
            float cx = b.exactCenterX();
            float cy = b.exactCenterY();
            fill.setColor(focused ? 0xFFFFFFFF : UNFOCUSED);
            canvas.drawCircle(cx, cy, size / 2f, fill);
            line.setColor(focused ? FOCUSED_ICON : UNFOCUSED_ICON);
            line.setStrokeWidth(size * .045f);
            float unit = size * .19f;
            if (kind == HIDE) {
                drawHide(canvas, cx, cy, unit);
            } else {
                drawMove(canvas, cx, cy, unit);
            }
        }

        private void drawHide(Canvas canvas, float cx, float cy, float unit) {
            Path eye = new Path();
            eye.moveTo(cx - unit * 1.35f, cy);
            eye.quadTo(cx, cy - unit * 1.25f, cx + unit * 1.35f, cy);
            eye.quadTo(cx, cy + unit * 1.25f, cx - unit * 1.35f, cy);
            canvas.drawPath(eye, line);
            canvas.drawCircle(cx, cy, unit * .42f, line);
            canvas.drawLine(cx - unit * 1.1f, cy - unit * 1.1f, cx + unit * 1.1f, cy + unit * 1.1f, line);
        }

        private void drawMove(Canvas canvas, float cx, float cy, float unit) {
            canvas.drawLine(cx, cy - unit * 1.3f, cx, cy + unit * 1.3f, line);
            Path heads = new Path();
            heads.moveTo(cx - unit * .55f, cy - unit * .75f);
            heads.lineTo(cx, cy - unit * 1.3f);
            heads.lineTo(cx + unit * .55f, cy - unit * .75f);
            heads.moveTo(cx - unit * .55f, cy + unit * .75f);
            heads.lineTo(cx, cy + unit * 1.3f);
            heads.lineTo(cx + unit * .55f, cy + unit * .75f);
            canvas.drawPath(heads, line);
        }

        @Override
        public void setAlpha(int alpha) {
            fill.setAlpha(alpha);
            line.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter filter) {
            fill.setColorFilter(filter);
            line.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
