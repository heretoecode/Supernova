package com.archos.mediacenter.video.player;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** All the text inside a view, one line per TextView, for strip assertions. */
final class PlayerPartsStripTestText
{
    static String of(View view)
    {
        if (view instanceof TextView)
            return ((TextView)view).getText() + "\n";
        StringBuilder all = new StringBuilder();
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                all.append(of(((ViewGroup)view).getChildAt(i)));
        return all.toString();
    }

    private PlayerPartsStripTestText()
    {
    }
}
