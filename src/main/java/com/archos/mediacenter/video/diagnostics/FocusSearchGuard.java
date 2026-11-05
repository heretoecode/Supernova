package com.archos.mediacenter.video.diagnostics;

import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/** FocusFinder rejects stale focus from another subtree during panel replacement. */
public final class FocusSearchGuard
{
    public static View findNextFocus(ViewGroup scope, View focused, int direction)
    {
        if (scope == null)
        {
            return null;
        }
        if (focused != null)
        {
            ViewParent parent = focused.getParent();
            View cursor = focused;
            while (cursor != scope && parent instanceof View)
            {
                cursor = (View)parent;
                parent = cursor.getParent();
            }
            if (cursor != scope)
            {
                return null;
            }
        }
        try
        {
            return FocusFinder.getInstance().findNextFocus(scope, focused, direction);
        }
        catch (RuntimeException failure)
        {
            Diagnostics.error("focus_search_failed", failure);
            return null;
        }
    }

    private FocusSearchGuard()
    {
    }
}
