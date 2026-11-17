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
    public void opensOnThisDeviceAndLibraryCareSaysNoneWithoutAnArrow()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        PageTabs tabs = findTabs(page);
        // Files opens on This Device every time (F2).
        assertEquals(FilesPage.THIS_DEVICE, tabs.openTab());
        LibraryCareCounts counts = LibraryCareCounts.count(Collections.emptyList(), false, video -> false);
        page.update(counts, 1808, System.currentTimeMillis() - 12 * 60_000L, false);
        tabs.select(FilesPage.LIBRARY_CARE);
        View first = page.findViewWithTag("files.row.first");
        assertNotNull(first);
        assertTrue(first.isFocusable());
        assertEquals("Missing files, None", String.valueOf(first.getContentDescription()));
        // A "None" row offers nothing: OK does nothing (F46).
        assertTrue(!first.hasOnClickListeners());
        // The tab row's right end is only the scan control (F18).
        assertEquals("Scan library", FilesPage.scanLabel(false, -1));
        assertEquals("Scanning · 42%", FilesPage.scanLabel(true, 42));
    }

    @Test
    public void scanOneSourceShowsAButtonPerSourceOnlyWhenFocused()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        java.util.List<FilesPage.Source> scanned = new java.util.ArrayList<>();
        java.util.List<FilesPage.Source> sources = java.util.List.of(
            new FilesPage.Source("This device", android.net.Uri.parse("file:///storage/Movies"), true),
            new FilesPage.Source("Media server", android.net.Uri.parse("smb://server/Films"), false));
        page.setSources(sources, scanned::add);
        findTabs(page).select(FilesPage.LIBRARY_CARE);
        View device = findByDescription(page, "This device, Scan one source");
        View server = findByDescription(page, "Media server, Scan one source");
        assertNotNull(server);
        // Nothing shown at rest (F48).
        assertEquals(View.GONE, device.getVisibility());
        server.performClick();
        assertEquals("Media server", scanned.get(0).label);
    }

    @Test
    public void connectionsShowTheLoginAndTheFourButtons()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FilesPage page = new FilesPage(activity);
        activity.setContentView(page);
        page.setConnections(java.util.List.of(new FilesPage.Connection(
            "Office PC", android.net.Uri.parse("sftp://office/videos"), false, 512, 0, "alex")), null);
        findTabs(page).select(FilesPage.CONNECTIONS);
        View first = page.findViewWithTag("files.row.first");
        assertNotNull(first);
        assertEquals("Office PC · alex, Not reachable", String.valueOf(first.getContentDescription()));
        for (String button : new String[] {"Open", "Edit", "Test", "Remove"})
        {
            assertNotNull(button, findByDescription(page, button + ", Office PC"));
        }
        assertNotNull(findByDescription(page, "Add a connection"));
    }

    private static View findByDescription(View view, String start)
    {
        if (view.getContentDescription() != null && view.getContentDescription().toString().startsWith(start))
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++)
            {
                View found = findByDescription(group.getChildAt(index), start);
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
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
