package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * The one tab row for pages with tabs (Files first, then Details; RULES §3–4): plain words with icons; the focused
 * tab is the white box; the open tab has a bright underline while the remote is in its content and a dull one
 * otherwise.
 * Moving along the tabs switches the page at once; Down goes to the open tab's content.
 */
public final class PageTabs extends LinearLayout
{
    public enum Underline
    {
        NONE,
        DULL,
        BRIGHT
    }

    static final int BRIGHT_LINE = 0xFFFFFFFF;
    static final int DULL_LINE = 0x59FFFFFF;

    private final List<LinearLayout> tabs = new ArrayList<>();
    private final List<TextView> labels = new ArrayList<>();
    private final List<View> lines = new ArrayList<>();
    private final List<TextView> badges = new ArrayList<>();
    private final IntConsumer opened;
    private Runnable down;
    private IntConsumer reselected = index -> { };
    private int open;

    public PageTabs(Context context, String[] names, String[] icons, IntConsumer opened)
    {
        super(context);
        this.opened = opened;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClipChildren(false);
        setClipToPadding(false);
        for (int index = 0; index < names.length; index++)
        {
            addTab(context, index, names[index], icons[index]);
        }
        refresh();
    }

    /** Which underline a tab shows (RULES §3): only the open tab has one, bright while the remote is below it. */
    public static Underline underline(boolean isOpen, boolean focused, boolean focusInContent)
    {
        if (!isOpen || focused)
        {
            return Underline.NONE;
        }
        return focusInContent ? Underline.BRIGHT : Underline.DULL;
    }

    private void addTab(Context context, int index, String name, String icon)
    {
        LinearLayout tab = new LinearLayout(context);
        tab.setOrientation(VERTICAL);
        tab.setGravity(Gravity.CENTER_HORIZONTAL);
        tab.setFocusable(true);
        tab.setFocusableInTouchMode(true);
        tab.setTag("semantic:tabs." + index);
        tab.setContentDescription(name);
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = SupernovaStyle.dp(context, 12);
        row.setPadding(pad, SupernovaStyle.dp(context, 6), pad, SupernovaStyle.dp(context, 6));
        TextView label = new TextView(context);
        label.setText(name);
        label.setTextSize(18);
        label.setSingleLine(true);
        label.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        PreviewIcon.apply(label, icon, 16);
        row.addView(label);
        TextView badge = new TextView(context);
        badge.setTextSize(11);
        badge.setGravity(Gravity.CENTER);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setVisibility(GONE);
        LayoutParams badgeSize = new LayoutParams(SupernovaStyle.dp(context, 18), SupernovaStyle.dp(context, 18));
        badgeSize.leftMargin = SupernovaStyle.dp(context, 6);
        row.addView(badge, badgeSize);
        tab.addView(row);
        View line = new TabLine(context);
        tab.addView(line, new LayoutParams(-1, Math.max(2, SupernovaStyle.dp(context, 1.5f))));
        tab.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus && open != index)
            {
                select(index);
            }
            refresh();
        });
        // OK on the open tab takes it back to its start (F12); OK on another tab opens it.
        tab.setOnClickListener(view -> {
            if (open == index)
            {
                reselected.accept(index);
            }
            else
            {
                select(index);
            }
        });
        tab.setOnKeyListener((view, key, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_DPAD_DOWN && down != null)
            {
                down.run();
                return true;
            }
            return false;
        });
        LayoutParams size = new LayoutParams(-2, -2);
        size.rightMargin = SupernovaStyle.dp(context, 8);
        addView(tab, size);
        tabs.add(tab);
        labels.add(label);
        lines.add(line);
        badges.add(badge);
    }

    /**
     * The underline under a tab. A plain full-width View would take all the width on offer inside the tab's
     * wrap-content column and push the other tabs off the row (0.157 Shield review #19); this one only takes the
     * width its tab finally gives it.
     */
    static final class TabLine extends View
    {
        TabLine(Context context)
        {
            super(context);
        }

        @Override
        protected void onMeasure(int widthSpec, int heightSpec)
        {
            boolean exact = MeasureSpec.getMode(widthSpec) == MeasureSpec.EXACTLY;
            int width = exact ? MeasureSpec.getSize(widthSpec) : 0;
            setMeasuredDimension(width, getDefaultSize(getSuggestedMinimumHeight(), heightSpec));
        }
    }

    /** What OK on the tab that is already open does: return it to its start (F12). */
    public void setReselected(IntConsumer action)
    {
        reselected = action;
    }

    /**
     * Up into the tab row lands on the open tab, never the tab above the focus (F17, RULES §4): from outside the
     * row only the open tab is offered to the focus search; inside the row every tab is.
     */
    @Override
    public void addFocusables(java.util.ArrayList<View> views, int direction, int focusableMode)
    {
        View focus = getRootView() == null ? null : getRootView().findFocus();
        boolean fromOutside = focus != null && !isAncestor(this, focus);
        if (fromOutside && direction == View.FOCUS_UP && open >= 0 && open < tabs.size())
        {
            views.add(tabs.get(open));
            return;
        }
        super.addFocusables(views, direction, focusableMode);
    }

    /** Where Down goes from a tab: the open tab's first row. */
    public void setDown(Runnable action)
    {
        down = action;
    }

    public int openTab()
    {
        return open;
    }

    public void select(int index)
    {
        if (index < 0 || index >= tabs.size())
        {
            return;
        }
        open = index;
        refresh();
        opened.accept(index);
    }

    /** A count on a tab when something there needs a look; 0 hides it. */
    public void setBadge(int index, int count)
    {
        TextView badge = badges.get(index);
        badge.setText(count > 99 ? "99+" : String.valueOf(count));
        badge.setVisibility(count > 0 ? VISIBLE : GONE);
        refresh();
    }

    public View tab(int index)
    {
        return tabs.get(index);
    }

    /** Call when focus moves anywhere on the page, so the underline follows the remote. */
    public void refresh()
    {
        View focus = getRootView() == null ? null : getRootView().findFocus();
        boolean inRow = focus != null && isAncestor(this, focus);
        boolean inContent = focus != null && !inRow;
        for (int index = 0; index < tabs.size(); index++)
        {
            boolean focused = tabs.get(index).hasFocus();
            Underline style = underline(index == open, focused, inContent);
            lines.get(index).setBackgroundColor(style == Underline.BRIGHT ? BRIGHT_LINE
                : style == Underline.DULL ? DULL_LINE : Color.TRANSPARENT);
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            labels.get(index).setTextColor(ink);
            SupernovaStyle.inkIcons(labels.get(index), ink);
            TextView badge = badges.get(index);
            badge.setTextColor(focused ? Color.WHITE : SupernovaStyle.FOCUS_TEXT);
            badge.setBackground(circle(focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE));
            View box = (View) labels.get(index).getParent();
            box.setBackground(focused ? rounded(SupernovaStyle.FOCUS_FILL) : null);
        }
    }

    private static boolean isAncestor(View parent, View child)
    {
        for (View view = child; view != null; view = view.getParent() instanceof View ? (View) view.getParent() : null)
        {
            if (view == parent)
            {
                return true;
            }
        }
        return false;
    }

    private GradientDrawable rounded(int colour)
    {
        GradientDrawable box = new GradientDrawable();
        box.setColor(colour);
        box.setCornerRadius(SupernovaStyle.dp(getContext(), 8));
        return box;
    }

    private static GradientDrawable circle(int colour)
    {
        GradientDrawable dot = new GradientDrawable();
        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(colour);
        return dot;
    }
}
