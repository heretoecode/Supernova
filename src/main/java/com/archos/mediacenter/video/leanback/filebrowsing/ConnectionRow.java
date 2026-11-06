package com.archos.mediacenter.video.leanback.filebrowsing;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;

/**
 * A saved connection on Network Shares (mock-up network-shares/02): name and a plain line, and the focused row shows
 * its buttons (Open, Edit, Test, Remove). OK on the row opens it; Right moves onto the buttons, Left or Back returns.
 */
final class ConnectionRow extends LinearLayout
{
    final TextView title;
    final TextView line;
    final LinearLayout buttons;
    private Runnable onFocus;

    ConnectionRow(Context c, String name, String detail, Runnable open)
    {
        super(c);
        setGravity(Gravity.CENTER_VERTICAL);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        setBackground(SupernovaStyle.rowWithActions(c));
        setPadding(dp(14), dp(10), dp(14), dp(10));
        setMinimumHeight(dp(58));
        LinearLayout two = new LinearLayout(c);
        two.setOrientation(VERTICAL);
        title = text(name, 17, SupernovaStyle.TEXT);
        title.setTypeface(SupernovaStyle.LABEL);
        line = text(detail, 13, SupernovaStyle.TEXT_SECONDARY);
        two.addView(title);
        two.addView(line);
        // A click on the words is a click on the row (touch and tests); the remote only focuses the row.
        title.setOnClickListener(v -> performClick());
        line.setOnClickListener(v -> performClick());
        addView(two, new LayoutParams(0, -2, 1));
        buttons = new LinearLayout(c);
        buttons.setVisibility(GONE);
        addView(buttons, new LayoutParams(-2, -2));
        setContentDescription(name + ". " + detail);
        setOnClickListener(v -> open.run());
        setOnFocusChangeListener((v, focused) -> {
            if (focused && onFocus != null)
                onFocus.run();
            refresh();
        });
        setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN || key != KeyEvent.KEYCODE_DPAD_RIGHT)
                return false;
            if (buttons.getChildCount() == 0)
                return false;
            buttons.setVisibility(VISIBLE);
            buttons.getChildAt(0).requestFocus();
            return true;
        });
    }

    void setOnFocusGained(Runnable run)
    {
        onFocus = run;
    }

    void addButton(String icon, String label, Runnable run)
    {
        TextView view = new TextView(getContext());
        view.setText(label);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        PreviewIcon.apply(view, icon, 15);
        SupernovaStyle.styleButton(view, SupernovaStyle.Surface.PANEL, false);
        view.setTag("network.connection.action." + label.toLowerCase(java.util.Locale.ROOT));
        view.setOnClickListener(v -> run.run());
        final int index = buttons.getChildCount();
        view.setOnKeyListener((v, key, event) -> buttonKey(index, key, event));
        view.setOnFocusChangeListener((v, focused) -> refresh());
        LayoutParams size = new LayoutParams(-2, dp(34));
        size.leftMargin = dp(8);
        buttons.addView(view, size);
    }

    private boolean buttonKey(int index, int key, KeyEvent event)
    {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (key == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (index == 0)
                requestFocus();
            else
                buttons.getChildAt(index - 1).requestFocus();
            return true;
        }
        if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (index + 1 < buttons.getChildCount())
                buttons.getChildAt(index + 1).requestFocus();
            return true;
        }
        if (key == KeyEvent.KEYCODE_BACK || key == KeyEvent.KEYCODE_ESCAPE) {
            requestFocus();
            return true;
        }
        return false;
    }

    /** Buttons show while the row or one of its buttons has focus; the row is lit while a button has it. */
    private void refresh()
    {
        post(() -> {
            boolean inside = hasFocus();
            buttons.setVisibility(inside && buttons.getChildCount() > 0 ? VISIBLE : GONE);
            View focused = findFocus();
            setActivated(inside && focused != this);
        });
    }

    private TextView text(String value, float sp, int colour)
    {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(colour);
        view.setSingleLine(true);
        view.setEllipsize(TextUtils.TruncateAt.END);
        return view;
    }

    private int dp(int value)
    {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
