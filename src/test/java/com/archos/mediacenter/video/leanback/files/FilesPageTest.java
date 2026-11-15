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

    @Test
    public void scanOneSourceOffersAButtonPerSource()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        java.util.List<FilesPage.Source> scanned = new java.util.ArrayList<>();
        java.util.List<FilesPage.Source> sources = java.util.List.of(
            new FilesPage.Source("This device", android.net.Uri.parse("file:///storage/Movies"), true),
            new FilesPage.Source("Media server", android.net.Uri.parse("smb://server/Films"), false));
        page.setSources(sources, scanned::add);
        android.view.ViewGroup row = page.findViewWithTag("files.scan.one");
        assertNotNull(row);
        View second = null;
        int buttons = 0;
        for (int index = 0; index < row.getChildCount(); index++)
        {
            View child = row.getChildAt(index);
            if (child.isFocusable() && child instanceof android.widget.TextView)
            {
                buttons++;
                second = child;
            }
        }
        assertEquals(2, buttons);
        second.performClick();
        assertEquals("Media server", scanned.get(0).label);
    }

    @Test
    public void connectionsTabListsConnectionsThenAdd()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        java.util.List<String> done = new java.util.ArrayList<>();
        FilesPage.ConnectionActions actions = new FilesPage.ConnectionActions()
        {
            @Override
            public void open(FilesPage.Connection connection)
            {
                done.add("open");
            }
            @Override
            public void tryAgain(FilesPage.Connection connection)
            {
                done.add("again");
            }
            @Override
            public void edit(FilesPage.Connection connection)
            {
                done.add("edit");
            }
            @Override
            public void remove(FilesPage.Connection connection)
            {
                done.add("remove");
            }
            @Override
            public void add()
            {
                done.add("add");
            }
        };
        page.setConnections(java.util.List.of(new FilesPage.Connection(
            "Office PC", android.net.Uri.parse("sftp://office/videos"), false, 0, 0)), actions);
        findTabs(page).select(FilesPage.CONNECTIONS);
        View first = page.findViewWithTag("files.row.first");
        assertNotNull(first);
        assertTrue(String.valueOf(first.getContentDescription()).contains("Not reachable"));
        first.requestFocus();
        View focused = first.findFocus();
        assertNotNull(focused);
        focused.performClick();
        assertEquals("again", done.get(0));
    }

    @Test
    public void thisDeviceListsStorageAndOpensIt()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        java.util.List<String> opened = new java.util.ArrayList<>();
        page.setDevices(java.util.List.of(
            new FilesPage.Device("This device", "", false, 310, 14_200_000_000L, 32_000_000_000L),
            new FilesPage.Device("Backup drive", "USB drive", true, 1203, 1_100_000_000_000L, 2_000_000_000_000L)),
            device -> opened.add(device.name));
        findTabs(page).select(FilesPage.THIS_DEVICE);
        View first = page.findViewWithTag("files.row.first");
        assertNotNull(first);
        first.performClick();
        assertEquals("This device", opened.get(0));
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
