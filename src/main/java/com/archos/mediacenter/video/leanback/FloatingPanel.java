package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import com.archos.mediacenter.video.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * One side panel for pages that change live behind it (RULES §10, §11; Customise page, Customise Home): it floats on
 * the right with rounded corners and a gap from the edges, sized to its content, charcoal at 95% with the faint edge
 * over a page, no title (the button named it), no Save: changes apply as they are made and Back closes.
 *
 * <p>Rows (RULES §7, §8): one setting a row on its centre line. Up to five choices are chips on the row (Left / Right
 * moves the choice, live; the chosen chip light grey at rest, black with white text on the focused row). A row that
 * opens a list shows its value and ›. The focused row is the white box with black text and icon.
 */
final class FloatingPanel
{
    /** Panel width as a share of the screen (mock-up `library-pages/32`). */
    static final float WIDTH_SHARE = 0.45f;
    static final int ROW_DP = 31;
    static final int CHIP_REST = 0x38FFFFFF;

    private final Context context;
    final Dialog dialog;
    final LinearLayout body;
    private final ScrollView scroll;
    private Runnable closed = () -> {
    };

    FloatingPanel(Context context, String name)
    {
        this.context = context;
        dialog = PreviewDialog.create(context, "panel");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        body = new LinearLayout(context);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(14), dp(6), dp(14), dp(12));
        body.setClipChildren(false);
        body.setClipToPadding(false);
        body.setContentDescription(name);
        body.setTag("floating.panel");
        scroll = new ScrollView(context);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setBackground(SupernovaStyle.glass(context));
        scroll.setClipToPadding(false);
        scroll.addView(body);
        dialog.setContentView(scroll);
        dialog.setOnDismissListener(d -> closed.run());
        // The move gesture (RULES §16): while a row is picked up, Up / Down move it and OK or Back puts it down;
        // the dialog sees the keys before the rows, so the row's own keys wait meanwhile.
        dialog.setOnKeyListener((d, key, event) -> moving != null && carry(key, event));
    }

    void onClosed(Runnable action)
    {
        closed = action;
    }

    int dp(float value)
    {
        return SupernovaStyle.dp(context, value);
    }

    /** A group heading centred on a thin line, in Title Case (RULES §10). */
    void heading(String title)
    {
        View heading = SupernovaStyle.groupHeading(context, title, 12);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, dp(28));
        size.topMargin = body.getChildCount() == 0 ? 0 : dp(10);
        body.addView(heading, size);
    }

    /** The plain line that sets the foot action apart, no heading (RULES §10). */
    void line()
    {
        View line = new View(context);
        line.setBackgroundColor(SupernovaStyle.HEADING_LINE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, Math.max(1, dp(1) / 2));
        size.topMargin = dp(10);
        size.bottomMargin = dp(4);
        body.addView(line, size);
    }

    private LinearLayout row(String tag, String icon, String label)
    {
        return row(tag, icon, label, null);
    }

    /** A row with an optional soft second line under its name (e.g. "Unfinished movies and next episodes"). */
    private LinearLayout row(String tag, String icon, String label, String second)
    {
        LinearLayout row = new LinearLayout(context);
        row.setTag(tag);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(10), 0, dp(10), 0);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setClickable(true);
        row.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);
        ImageView mark = new ImageView(context);
        mark.setImageDrawable(new PreviewIcon(icon));
        mark.setTag("icon");
        row.addView(mark, new LinearLayout.LayoutParams(dp(16), dp(16)));
        TextView name = new TextView(context);
        name.setText(label);
        name.setTag("label");
        name.setTextSize(14);
        name.setTextColor(Color.WHITE);
        name.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        name.setSingleLine(true);
        name.setIncludeFontPadding(false);
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(LinearLayout.VERTICAL);
        words.addView(name, new LinearLayout.LayoutParams(-2, -2));
        if (second != null && !second.isEmpty())
        {
            TextView soft = new TextView(context);
            soft.setText(second);
            soft.setTag("second");
            soft.setTextSize(11);
            soft.setSingleLine(true);
            soft.setEllipsize(TextUtils.TruncateAt.END);
            soft.setIncludeFontPadding(false);
            LinearLayout.LayoutParams softSize = new LinearLayout.LayoutParams(-2, -2);
            softSize.topMargin = dp(3);
            words.addView(soft, softSize);
        }
        LinearLayout.LayoutParams wordsSize = new LinearLayout.LayoutParams(0, -2, 1);
        wordsSize.leftMargin = dp(10);
        row.addView(words, wordsSize);
        row.setContentDescription(second == null ? label : label + ", " + second);
        body.addView(row, new LinearLayout.LayoutParams(-1, dp(second == null ? ROW_DP : ROW_DP + 10)));
        return row;
    }

    /** Focus look for a row and its children: the white box with black ink, or plain (RULES §3). */
    private void paint(LinearLayout row, boolean focused, int chosen)
    {
        GradientDrawable box = new GradientDrawable();
        box.setCornerRadius(dp(8));
        box.setColor(focused ? SupernovaStyle.FOCUS_FILL : Color.TRANSPARENT);
        row.setBackground(box);
        int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
        for (int i = 0; i < row.getChildCount(); i++)
        {
            View child = row.getChildAt(i);
            if (child instanceof ImageView && ((ImageView) child).getDrawable() != null)
            {
                ((ImageView) child).getDrawable().setColorFilter(
                    new android.graphics.PorterDuffColorFilter(ink, android.graphics.PorterDuff.Mode.SRC_IN));
            }
            if (child instanceof TextView)
            {
                Object tag = child.getTag();
                boolean value = "value".equals(tag) || "chevron".equals(tag);
                ((TextView) child).setTextColor(value && !focused ? SupernovaStyle.TEXT_SECONDARY : ink);
            }
            if (child instanceof LinearLayout && "buttons".equals(child.getTag()))
            {
                paintButtons((LinearLayout) child, focused, ink);
                continue;
            }
            if (child instanceof LinearLayout && !"chips".equals(child.getTag()))
            {
                LinearLayout words = (LinearLayout) child;
                for (int n = 0; n < words.getChildCount(); n++)
                {
                    TextView text = (TextView) words.getChildAt(n);
                    boolean soft = "second".equals(text.getTag());
                    text.setTextColor(soft ? (focused ? SupernovaStyle.TEXT_SECONDARY_ON_FOCUS
                                                      : SupernovaStyle.TEXT_SECONDARY) : ink);
                }
            }
            if (child instanceof ImageView && ((ImageView) child).getDrawable() instanceof SwitchMark)
            {
                SwitchMark mark = (SwitchMark) ((ImageView) child).getDrawable();
                mark.focused = focused;
                mark.setColorFilter(null);
                mark.invalidateSelf();
            }
            if (child instanceof LinearLayout && "chips".equals(child.getTag()) && stepped((LinearLayout) child))
            {
                paintStepper((LinearLayout) child, focused, chosen, ink);
                continue;
            }
            if (child instanceof LinearLayout && "chips".equals(child.getTag()))
            {
                LinearLayout chips = (LinearLayout) child;
                for (int n = 0; n < chips.getChildCount(); n++)
                {
                    TextView chip = (TextView) chips.getChildAt(n);
                    boolean on = n == chosen;
                    GradientDrawable fill = new GradientDrawable();
                    fill.setCornerRadius(dp(7));
                    fill.setColor(on ? (focused ? SupernovaStyle.FOCUS_TEXT : CHIP_REST) : Color.TRANSPARENT);
                    chip.setBackground(fill);
                    chip.setTextColor(on && focused ? Color.WHITE : ink);
                }
            }
        }
    }

    /** RULES §8 (owner, 21 Nov): more than two choices are one value stepped with Left / Right, not chips. */
    static boolean stepped(LinearLayout chips)
    {
        return chips.getChildCount() > 2;
    }

    /**
     * The stepper look: only the current value, plain white; ‹ › beside it only while the row is focused, and at rest
     * the arrows keep their room so values line up down the panel.
     */
    private void paintStepper(LinearLayout chips, boolean focused, int chosen, int ink)
    {
        for (int n = 0; n < chips.getChildCount(); n++)
        {
            TextView chip = (TextView) chips.getChildAt(n);
            boolean on = n == chosen;
            chip.setVisibility(on ? View.VISIBLE : View.GONE);
            chip.setBackground(null);
            if (!on)
            {
                continue;
            }
            String option = String.valueOf(chip.getContentDescription());
            android.text.SpannableString text = new android.text.SpannableString("‹  " + option + "  ›");
            int arrows = focused ? ink : Color.TRANSPARENT;
            text.setSpan(new android.text.style.ForegroundColorSpan(arrows), 0, 1, 0);
            text.setSpan(new android.text.style.ForegroundColorSpan(arrows), text.length() - 1, text.length(), 0);
            chip.setText(text);
            chip.setTextColor(ink);
        }
    }

    /**
     * A row of up to five chips (RULES §8). Left / Right moves the choice, applied at once; OK moves on too.
     * Returns a way to change the chosen chip from outside (e.g. Order's words follow Sort by).
     */
    ChipRow chips(String tag, String icon, String label, String[] options, int chosen, IntConsumer changed)
    {
        LinearLayout row = row(tag, icon, label);
        LinearLayout chips = new LinearLayout(context);
        chips.setTag("chips");
        chips.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(chips, new LinearLayout.LayoutParams(-2, -1));
        ChipRow result = new ChipRow(row, chips, chosen, changed);
        result.setOptions(options);
        row.setOnFocusChangeListener((v, has) -> paint(row, has, result.chosen));
        row.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
            {
                return false;
            }
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT)
            {
                int next = result.chosen + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                if (next >= 0 && next < result.count())
                {
                    result.choose(next, true);
                }
                return true;
            }
            return false;
        });
        row.setOnClickListener(v -> result.choose((result.chosen + 1) % Math.max(1, result.count()), true));
        paint(row, false, chosen);
        return result;
    }

    /** A chip row's state. */
    final class ChipRow
    {
        final LinearLayout row;
        private final LinearLayout chips;
        private final IntConsumer changed;
        int chosen;

        ChipRow(LinearLayout row, LinearLayout chips, int chosen, IntConsumer changed)
        {
            this.row = row;
            this.chips = chips;
            this.chosen = chosen;
            this.changed = changed;
        }

        int count()
        {
            return chips.getChildCount();
        }

        void setOptions(String[] options)
        {
            chips.removeAllViews();
            for (String option : options)
            {
                TextView chip = new TextView(context);
                chip.setText(option);
                chip.setContentDescription(option);
                chip.setTextSize(13);
                chip.setSingleLine(true);
                chip.setIncludeFontPadding(false);
                chip.setGravity(Gravity.CENTER);
                chip.setPadding(dp(9), 0, dp(9), 0);
                LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-2, dp(23));
                size.leftMargin = dp(4);
                chips.addView(chip, size);
            }
            chosen = Math.max(0, Math.min(chosen, options.length - 1));
            row.setContentDescription(((TextView) row.findViewWithTag("label")).getText() + ", " + options[chosen]);
            paint(row, row.isFocused(), chosen);
        }

        void choose(int index, boolean notify)
        {
            chosen = index;
            CharSequence label = ((TextView) row.findViewWithTag("label")).getText();
            row.setContentDescription(label + ", " + chips.getChildAt(index).getContentDescription());
            paint(row, row.isFocused(), chosen);
            if (notify)
            {
                changed.accept(index);
            }
        }
    }

    /** A row that opens a list or keyboard: "value ›" on the right (RULES §8). Returns the value view. */
    TextView opener(String tag, String icon, String label, String value, Runnable open)
    {
        LinearLayout row = row(tag, icon, label);
        TextView shown = new TextView(context);
        shown.setTag("value");
        shown.setText(value);
        shown.setTextSize(13);
        shown.setSingleLine(true);
        shown.setEllipsize(TextUtils.TruncateAt.END);
        shown.setIncludeFontPadding(false);
        shown.setMaxWidth(dp(190));
        row.addView(shown, new LinearLayout.LayoutParams(-2, -2));
        TextView chevron = new TextView(context);
        chevron.setTag("chevron");
        chevron.setText("›");
        chevron.setTextSize(16);
        chevron.setIncludeFontPadding(false);
        LinearLayout.LayoutParams chevronSize = new LinearLayout.LayoutParams(-2, -2);
        chevronSize.leftMargin = dp(8);
        row.addView(chevron, chevronSize);
        row.setOnFocusChangeListener((v, has) -> paint(row, has, -1));
        row.setOnClickListener(v -> open.run());
        row.setContentDescription(label + ", " + value);
        paint(row, false, -1);
        return shown;
    }

    /** An on/off setting: a switch (RULES §8); OK flips it, applied at once. */
    View toggle(String tag, String icon, String label, String second, boolean on,
                java.util.function.Consumer<Boolean> changed)
    {
        LinearLayout row = row(tag, icon, label, second);
        SwitchMark mark = new SwitchMark(on);
        ImageView knob = new ImageView(context);
        knob.setTag("switch");
        knob.setImageDrawable(mark);
        row.addView(knob, new LinearLayout.LayoutParams(dp(StandardSwitchDrawable.WIDTH_DP),
                                                         dp(StandardSwitchDrawable.HEIGHT_DP)));
        row.setOnFocusChangeListener((v, has) -> paint(row, has, -1));
        row.setOnClickListener(v -> {
            mark.on = !mark.on;
            mark.invalidateSelf();
            changed.accept(mark.on);
        });
        paint(row, false, -1);
        return row;
    }

    /**
     * A row that opens its own settings (›) and also carries a switch: OK opens, Left / Right turns it on or off
     * (Your Rows in Customise Home).
     */
    View openerWithSwitch(String tag, String icon, String label, String second, boolean on, Runnable open,
                          java.util.function.Consumer<Boolean> changed)
    {
        LinearLayout row = row(tag, icon, label, second);
        TextView chevron = new TextView(context);
        chevron.setTag("chevron");
        chevron.setText("›");
        chevron.setTextSize(16);
        LinearLayout.LayoutParams chevronSize = new LinearLayout.LayoutParams(-2, -2);
        chevronSize.rightMargin = dp(12);
        row.addView(chevron, chevronSize);
        SwitchMark mark = new SwitchMark(on);
        ImageView knob = new ImageView(context);
        knob.setTag("switch");
        knob.setImageDrawable(mark);
        row.addView(knob, new LinearLayout.LayoutParams(dp(StandardSwitchDrawable.WIDTH_DP),
                                                         dp(StandardSwitchDrawable.HEIGHT_DP)));
        row.setOnFocusChangeListener((v, has) -> paint(row, has, -1));
        row.setOnClickListener(v -> open.run());
        row.setOnKeyListener((v, key, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                && (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT))
            {
                boolean next = key == KeyEvent.KEYCODE_DPAD_RIGHT;
                if (next != mark.on)
                {
                    mark.on = next;
                    mark.invalidateSelf();
                    changed.accept(next);
                }
                return true;
            }
            return false;
        });
        paint(row, false, -1);
        return row;
    }

    /**
     * The switch (RULES §3): at rest on = white track, dark knob; off = dark track, white knob. On a focused row
     * on = dark track, white knob; off = light grey track, dark knob.
     */
    static final class SwitchMark extends android.graphics.drawable.Drawable
    {
        boolean on;
        boolean focused;
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        SwitchMark(boolean on)
        {
            this.on = on;
        }

        @Override
        public void draw(android.graphics.Canvas canvas)
        {
            android.graphics.Rect b = getBounds();
            float r = b.height() / 2f;
            int track = focused ? (on ? SupernovaStyle.FOCUS_TEXT : 0xFFC4C9D2)
                : (on ? StandardSwitchDrawable.ON_TRACK : StandardSwitchDrawable.OFF);
            int knob = focused ? (on ? Color.WHITE : SupernovaStyle.FOCUS_TEXT)
                : (on ? StandardSwitchDrawable.KNOB_ON : Color.WHITE);
            paint.setColor(track);
            canvas.drawRoundRect(new android.graphics.RectF(b), r, r, paint);
            paint.setColor(knob);
            float inset = r * .22f;
            float cx = on ? b.right - r : b.left + r;
            canvas.drawCircle(cx, b.exactCenterY(), r - inset, paint);
        }

        @Override
        public void setAlpha(int alpha)
        {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter filter)
        {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity()
        {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }
    }

    /** Moves a picked-up row one place; returns false when it can go no further that way. */
    interface Mover
    {
        boolean step(View row, int direction);
    }

    private View moving;
    private Mover mover;

    /**
     * The row can be picked up with a long press of OK (it lifts, with ⌃⌄), moved with Up / Down, the page behind
     * following live, and put down with OK or Back (RULES §16; Customise Home "order with the move gesture").
     */
    void movable(View row, Mover how)
    {
        row.setOnLongClickListener(v -> {
            pickUp(row, how);
            return true;
        });
    }

    boolean moving()
    {
        return moving != null;
    }

    void pickUp(View row, Mover how)
    {
        moving = row;
        mover = how;
        TextView label = row.findViewWithTag("label");
        if (label != null && !label.getText().toString().endsWith("  ⌃⌄"))
        {
            label.setText(label.getText() + "  ⌃⌄");
        }
        row.setTranslationY(-dp(2));
        row.setElevation(dp(6));
    }

    private void putDown()
    {
        View row = moving;
        moving = null;
        mover = null;
        TextView label = row.findViewWithTag("label");
        if (label != null)
        {
            label.setText(label.getText().toString().replace("  ⌃⌄", ""));
        }
        row.setTranslationY(0);
        row.setElevation(0);
        row.requestFocus();
    }

    private boolean carry(int key, KeyEvent event)
    {
        if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
            {
                return true;
            }
            int direction = key == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1;
            int at = body.indexOfChild(moving);
            int to = at + direction;
            if (to >= 0 && to < body.getChildCount() && body.getChildAt(to).isFocusable()
                && mover.step(moving, direction))
            {
                body.removeViewAt(at);
                body.addView(moving, to);
                moving.requestFocus();
            }
            return true;
        }
        if (key == KeyEvent.KEYCODE_BACK || key == KeyEvent.KEYCODE_DPAD_CENTER || key == KeyEvent.KEYCODE_ENTER)
        {
            if (event.getAction() == KeyEvent.ACTION_UP)
            {
                putDown();
            }
            return true;
        }
        return true;
    }

    /**
     * A row whose buttons show only while it is focused (Customise Home, owner 21 Nov: "Move · Hide · Edit" on the
     * focused row; a hidden row shows "Show"). Left / Right picks a button, OK presses it; Up / Down move between
     * rows as usual. Returns the row.
     */
    View buttons(String tag, String icon, String label, String second, String[] names, IntConsumer pressed)
    {
        LinearLayout row = row(tag, icon, label, second);
        LinearLayout buttons = new LinearLayout(context);
        buttons.setTag("buttons");
        buttons.setGravity(Gravity.CENTER_VERTICAL);
        for (String name : names)
        {
            TextView button = new TextView(context);
            button.setText(name);
            button.setTag("button:" + name);
            button.setTextSize(13);
            button.setSingleLine(true);
            button.setIncludeFontPadding(false);
            button.setGravity(Gravity.CENTER);
            button.setPadding(dp(10), 0, dp(10), 0);
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-2, dp(23));
            size.leftMargin = dp(4);
            buttons.addView(button, size);
        }
        buttons.setVisibility(View.INVISIBLE);
        row.addView(buttons, new LinearLayout.LayoutParams(-2, -1));
        int[] picked = {0};
        buttons.setTag(R.id.panel_button_picked, picked);
        row.setOnFocusChangeListener((v, has) -> {
            if (!has)
            {
                picked[0] = 0;
            }
            paint(row, has, -1);
        });
        row.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
            {
                return false;
            }
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT)
            {
                int next = picked[0] + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                if (next >= 0 && next < names.length)
                {
                    picked[0] = next;
                    paint(row, true, -1);
                }
                return true;
            }
            return false;
        });
        row.setOnClickListener(v -> pressed.accept(picked[0]));
        paint(row, false, -1);
        return row;
    }

    /** Which button a buttons row has picked (for tests). */
    static int picked(View row)
    {
        LinearLayout buttons = row.findViewWithTag("buttons");
        return buttons == null ? -1 : ((int[]) buttons.getTag(R.id.panel_button_picked))[0];
    }

    /** The buttons show on the focused row only: the picked one black with white words, the others plain. */
    private void paintButtons(LinearLayout buttons, boolean focused, int ink)
    {
        buttons.setVisibility(focused ? View.VISIBLE : View.INVISIBLE);
        Object state = buttons.getTag(R.id.panel_button_picked);
        int picked = state instanceof int[] ? ((int[]) state)[0] : 0;
        for (int n = 0; n < buttons.getChildCount(); n++)
        {
            TextView button = (TextView) buttons.getChildAt(n);
            GradientDrawable fill = new GradientDrawable();
            fill.setCornerRadius(dp(7));
            fill.setColor(n == picked ? SupernovaStyle.FOCUS_TEXT : Color.TRANSPARENT);
            button.setBackground(fill);
            button.setTextColor(n == picked ? Color.WHITE : ink);
        }
    }

    /** A plain action row (e.g. Delete page at the foot). */
    View action(String tag, String icon, String label, Runnable run)
    {
        LinearLayout row = row(tag, icon, label);
        row.setOnFocusChangeListener((v, has) -> paint(row, has, -1));
        row.setOnClickListener(v -> run.run());
        paint(row, false, -1);
        return row;
    }

    /** Shows the panel floating on the right; the page behind stays in view, not dimmed (RULES §10). */
    void show(View focus)
    {
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        int gap = dp(16);
        body.measure(View.MeasureSpec.makeMeasureSpec(Math.round(width * WIDTH_SHARE), View.MeasureSpec.EXACTLY),
                     View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        WindowManager.LayoutParams at = window.getAttributes();
        at.gravity = Gravity.TOP | Gravity.RIGHT;
        at.x = gap;
        at.y = gap;
        at.width = Math.round(width * WIDTH_SHARE);
        at.height = Math.min(body.getMeasuredHeight(), height - 2 * gap);
        window.setAttributes(at);
        (focus != null ? focus : first()).requestFocus();
    }

    /** The panel's left edge on the screen, for the keyboard beside it (RULES §15). */
    int leftEdge()
    {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return width - dp(16) - Math.round(width * WIDTH_SHARE);
    }

    View first()
    {
        for (int i = 0; i < body.getChildCount(); i++)
        {
            if (body.getChildAt(i).isFocusable())
            {
                return body.getChildAt(i);
            }
        }
        return body;
    }

    void dismiss()
    {
        dialog.dismiss();
    }
}
