package com.archos.mediacenter.video.leanback;

import android.view.View;
import android.view.ViewGroup;

/**
 * D4 (0.158.8): NOVA's browse rows still sit, hidden, under the Supernova pages on the main screen. Hiding them is
 * not enough: leanback asks its rows grid ({@code container_list}) for focus in code, and Android lets a view inside a
 * hidden parent take focus that way, so the remote went to a grid nobody could see (187 off-screen focus slips in the
 * 26 Nov diagnostics). Sealed content can never hold focus (RULES §3: no NOVA screen runs hidden).
 */
public final class HiddenNovaContent
{
    private HiddenNovaContent()
    {
    }

    public static void seal(View content)
    {
        content.setVisibility(View.GONE);
        content.setFocusable(false);
        if (content instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) content;
            group.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            View focused = group.findFocus();
            if (focused != null)
            {
                focused.clearFocus();
            }
        }
    }
}
