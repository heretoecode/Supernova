package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * One hero, one text block (RULES §14d, owner 21 Nov 2026; `home-v3/tv/01`–`05`): the same layout on Home and every
 * Details page. The block is **anchored to the bottom** so the buttons never move whatever the text: buttons 56 px
 * above the card's foot, the two-line description 40 px above them, the facts line 16 px above that, an optional
 * title line ("Season 2 · Episode 8 · Title") above it, the logo 24 px above, and the next-episode label 22 px above
 * the logo. Text and buttons start 64 px in. Pages add their pieces in this order; only what sits in them differs.
 */
public final class HeroText
{
    /** At 1080p; dp here are half (the Shield's 1080p is 960 dp wide). */
    static final int LEFT_PX = 64;
    static final int BUTTONS_FROM_FOOT_PX = 56;
    static final int DESCRIPTION_ABOVE_BUTTONS_PX = 40;
    static final int FACTS_ABOVE_DESCRIPTION_PX = 16;
    static final int TITLE_LINE_ABOVE_FACTS_PX = 10;
    static final int LOGO_ABOVE_TEXT_PX = 24;

    public final LinearLayout copy;
    private final Context context;

    public HeroText(Context context, FrameLayout card)
    {
        this.context = context;
        copy = new LinearLayout(context);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setTag("hero.text");
        copy.setClipChildren(false);
        copy.setPadding(px(LEFT_PX), px(36), px(48), px(BUTTONS_FROM_FOOT_PX));
        card.addView(copy, new FrameLayout.LayoutParams(-1, -1));
        // Everything below this spacer is pushed to the foot: the block grows upwards, the buttons stay put.
        copy.addView(new View(context), new LinearLayout.LayoutParams(1, 0, 1));
    }

    private int px(int px1080)
    {
        return Math.round(px1080 / 2f * context.getResources().getDisplayMetrics().density);
    }

    /** The next-episode label, 22 px above the logo (RULES §14d). */
    public void label(View label, int heightPx)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-2, heightPx);
        size.bottomMargin = px(22);
        copy.addView(label, size);
    }

    /** The logo, or the name when there is no logo, inside its box. */
    public void logo(View logo, int widthPx, int heightPx)
    {
        copy.addView(logo, new LinearLayout.LayoutParams(widthPx, heightPx));
    }

    /** The optional title line ("Season 2 · Episode 8 · Title"), 24 px under the logo. */
    public void titleLine(View line)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
        size.topMargin = px(LOGO_ABOVE_TEXT_PX);
        copy.addView(line, size);
    }

    /** The facts line (Age rating · Year · Length · Genres); 24 px under the logo when there is no title line. */
    public void facts(View facts, boolean afterTitleLine)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
        size.topMargin = px(afterTitleLine ? TITLE_LINE_ABOVE_FACTS_PX : LOGO_ABOVE_TEXT_PX);
        copy.addView(facts, size);
    }

    /** Anything that belongs with the facts (badges, pills), directly under them. */
    public void withFacts(View extra)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
        size.topMargin = px(8);
        copy.addView(extra, size);
    }

    /** The two-line description, 16 px under the facts. */
    public void description(View description, int widthPx)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(widthPx, -2);
        size.topMargin = px(FACTS_ABOVE_DESCRIPTION_PX);
        copy.addView(description, size);
    }

    /** Something shown above the buttons only now and then (e.g. Match Metadata). */
    public void beforeButtons(View extra, int heightPx)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-2, heightPx);
        size.topMargin = px(16);
        copy.addView(extra, size);
    }

    /** The buttons, 40 px under the description and 56 px above the foot. */
    public void buttons(View buttons, int heightPx)
    {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, heightPx);
        size.topMargin = px(DESCRIPTION_ABOVE_BUTTONS_PX);
        copy.addView(buttons, size);
    }
}
