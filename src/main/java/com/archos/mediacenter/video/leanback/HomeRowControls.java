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

    /** Told whenever focus enters or leaves Move or Hide (the heading shows them only while focused). */
    Runnable focusHook = () -> {
    };
    final LinearLayout hide;
    final LinearLayout move;
    private final Actions actions;
    private boolean moving;

    /**
     * Move and Hide beside a row's heading (page sheet HOME.md; mock-up `home-v2/02`): plain words with line icons,
     * no filled circles (Hide a minus circle); shown when focus reaches the heading. Right from Move to Hide.
     */
    HomeRowControls(Context c, Actions actions) {
        super(c);
        this.actions = actions;
        setTag("home.row.controls");
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClipChildren(false);
        move = column(c, "Move", RoundIcon.MOVE, "home.row.move");
        hide = column(c, "Hide", RoundIcon.HIDE, "home.row.hide");
        addView(move, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams second = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        second.leftMargin = dp(6);
        addView(hide, second);
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
            if (down && view == hide) {
                move.requestFocus();
            }
            return true;
        }
        if (code == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (down && view == move) {
                hide.requestFocus();
            }
            return true;
        }
        return false;
    }

    private LinearLayout column(Context c, String text, int icon, String tag) {
        LinearLayout button = new LinearLayout(c);
        button.setTag(tag);
        button.setOrientation(HORIZONTAL);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(dp(10), dp(5), dp(12), dp(5));
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setClickable(true);
        button.setContentDescription(text);
        View mark = new View(c);
        mark.setBackground(new RoundIcon(icon));
        button.addView(mark, new LayoutParams(dp(16), dp(16)));
        TextView label = new TextView(c);
        label.setText(text);
        label.setTextSize(13);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        label.setSingleLine(true);
        label.setIncludeFontPadding(false);
        LayoutParams labelSize = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        labelSize.leftMargin = dp(7);
        button.addView(label, labelSize);
        button.setOnFocusChangeListener((v, focused) -> {
            refresh(button);
            focusHook.run();
        });
        refresh(button);
        return button;
    }

    /** Plain at rest; the white box with black ink when focused, and on Move while moving (RULES §3, §12). */
    private void refresh(LinearLayout button) {
        boolean white = button.isFocused() || moving && button == move;
        RoundIcon icon = (RoundIcon) button.getChildAt(0).getBackground();
        icon.setFocused(white);
        label(button).setTextColor(white ? SupernovaStyle.FOCUS_TEXT : LABEL_FOCUSED);
        android.graphics.drawable.GradientDrawable box = new android.graphics.drawable.GradientDrawable();
        box.setCornerRadius(dp(8));
        box.setColor(white ? SupernovaStyle.FOCUS_FILL : 0);
        button.setBackground(box);
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
            // Line icons, no filled circle behind them (RULES §13).
            line.setColor(focused ? FOCUSED_ICON : UNFOCUSED_ICON);
            line.setStrokeWidth(size * .09f);
            float unit = size * .36f;
            if (kind == HIDE) {
                drawHide(canvas, cx, cy, unit);
            } else {
                drawMove(canvas, cx, cy, unit);
            }
        }

        /** Hide: a minus in a circle (sheet). */
        private void drawHide(Canvas canvas, float cx, float cy, float unit) {
            canvas.drawCircle(cx, cy, unit * 1.2f, line);
            canvas.drawLine(cx - unit * .6f, cy, cx + unit * .6f, cy, line);
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
