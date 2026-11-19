package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.leanback.PageTabs;

/**
 * Files' tab row as it stands on the Fix match page (fix-match-from-details/v2): Library Care open, a faint line under
 * the row. Shown, not used: the remote stays on the search and the matches.
 */
public final class FilesTabsPicture extends LinearLayout
{
    public FilesTabsPicture(Context context)
    {
        super(context);
        setOrientation(VERTICAL);
        setFocusable(false);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        PageTabs tabs = new PageTabs(context, FilesPage.TABS, FilesPage.TAB_ICONS, index -> {
        });
        tabs.select(FilesPage.LIBRARY_CARE);
        for (int i = 0; i < FilesPage.TABS.length; i++)
        {
            tabs.tab(i).setFocusable(false);
        }
        addView(tabs, new LayoutParams(-2, -2));
        View line = new View(context);
        line.setBackgroundColor(FilesPage.LINE);
        addView(line, new LayoutParams(-1, Math.max(1, Math.round(getResources().getDisplayMetrics().density))));
    }
}
