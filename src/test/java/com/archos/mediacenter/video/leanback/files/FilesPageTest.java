package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import com.archos.mediacenter.video.leanback.PageTabs;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class FilesPageTest
{
    @Test
    public void opensOnLibraryCareWithCountsSummaryAndBadge()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        LibraryCareCounts counts = LibraryCareCounts.count(Collections.emptyList(), false, video -> false);
        page.update(counts, 1808, System.currentTimeMillis() - 12 * 60_000L, false);
        View first = page.findViewWithTag("files.row.first");
        assertNotNull(first);
        assertTrue(first.isFocusable());
        assertTrue(String.valueOf(first.getContentDescription()).startsWith("Missing files"));
        PageTabs tabs = findTabs(page);
        assertEquals(FilesPage.LIBRARY_CARE, tabs.openTab());
    }

    private static PageTabs findTabs(View view)
    {
        if (view instanceof PageTabs)
        {
            return (PageTabs) view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++)
            {
                PageTabs found = findTabs(group.getChildAt(index));
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }
}
