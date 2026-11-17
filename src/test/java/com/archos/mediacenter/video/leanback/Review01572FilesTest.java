package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.files.FilesPage;
import com.archos.mediacenter.video.leanback.files.FolderBrowser;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.time.Duration;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowDialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 0.157.2, Files (page sheet docs/design/pages/FILES.md; Definition of done 3 and 4): every action in the sheet is
 * pressed inside the real app frame, with realistic data (long names, several drives, an empty folder, a file that
 * is not a video), a render of where it leads is saved, and focus is checked never to land in the top bar unasked.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review01572FilesTest
{
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void everyFilesActionPressed() throws Exception
    {
        File drive = drive();
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .putBoolean("supernova_files_page", true)
            .commit();
        try
        {
            Picasso.get();
        }
        catch (IllegalStateException e)
        {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try
        {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            TopNavigation nav = new TopNavigation(host.get(), pages, pages::setTab, pages::atTop);
            host.get().setContentView(nav);
            pages.setArtworkListener(nav::setArtwork);
            pages.setDiscovery(new PreviewDiscovery());
            pages.setSnapshot(snapshot(drive));
            pages.setFiles(Arrays.asList(new Box(Box.ID.FOLDERS, "Internal storage", 0),
                                         new Box(Box.ID.USB, "Backup #2 (the big grey one in the cupboard)", 0,
                                                 drive.getPath()),
                                         new Box(Box.ID.USB, "Holiday stick", 0, drive.getPath() + "/Empty folder"),
                                         new Box(Box.ID.NETWORK, "Network", 0)));
            View filesTab = nav.findViewWithTag("semantic:topnav.network");
            assertNotNull(filesTab);
            filesTab.performClick();
            settle(nav);
            FilesPage page = Review0157RenderTest.find(nav, FilesPage.class);
            assertNotNull(page);
            PageTabs tabs = Review0157RenderTest.find(page, PageTabs.class);
            assertEquals("Files opens on This Device (F2)", FilesPage.THIS_DEVICE, tabs.openTab());

            // This Device: tab row, then the rows (F14, F19).
            tabs.tab(FilesPage.THIS_DEVICE).requestFocus();
            settle(nav);
            save(nav, "01572-files-01-this-device-tab");
            press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            assertInFiles(nav, page, "Down from the tab row");
            save(nav, "01572-files-02-this-device-rows");

            // Up from the tab row reaches the top bar (F1); Down comes back.
            press(host, KeyEvent.KEYCODE_DPAD_UP, nav);
            assertTrue("Up from content lands on the open tab (F17)", tabs.tab(FilesPage.THIS_DEVICE).isFocused());
            press(host, KeyEvent.KEYCODE_DPAD_UP, nav);
            assertFalse("Up from the tab row reaches the top bar (F1)", FilesPage.isInside(page, nav.findFocus()));
            save(nav, "01572-files-03-up-to-top-bar");
            press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            assertInFiles(nav, page, "Down from the top bar");

            // OK on the USB drive opens it in List view (F5): focus stays on the page (F9).
            View usb = byDescription(page, "Backup #2");
            assertNotNull("USB drive row", usb);
            usb.requestFocus();
            usb.performClick();
            waitForFolder(nav, page);
            assertInFiles(nav, page, "Opening a drive");
            save(nav, "01572-files-04-drive-list");

            // OK on a folder: opens below the same tab, focus on its first item, never the top bar (F9).
            View movies = page.findViewWithTag(FolderBrowser.itemTag("Movies"));
            assertNotNull(movies);
            movies.requestFocus();
            movies.performClick();
            waitForFolder(nav, page);
            assertInFiles(nav, page, "Opening a folder");
            save(nav, "01572-files-05-folder-list");

            // Long-press on a file: its menu beside it (F16).
            View film = page.findViewWithTag(FolderBrowser.itemTag("Conclave (2024) Director's Cut.mkv"));
            assertNotNull(film);
            film.requestFocus();
            settle(nav);
            film.performLongClick();
            settle(nav);
            saveWithMenu(nav, "01572-files-06-file-menu");
            dismissMenu(nav);

            // Back: up one level, focus on the folder we came from (sheet).
            press(host, KeyEvent.KEYCODE_BACK, nav);
            waitForFolder(nav, page);
            assertInFiles(nav, page, "Back up a level");
            View focus = nav.findFocus();
            assertEquals(FolderBrowser.itemTag("Movies"), focus.getTag());
            save(nav, "01572-files-07-back-to-drive");

            // Long-press on a folder: Open · Add to library · Scan this folder · Exclude.
            focus.performLongClick();
            settle(nav);
            saveWithMenu(nav, "01572-files-08-folder-menu");
            dismissMenu(nav);

            // Empty folder.
            View empty = page.findViewWithTag(FolderBrowser.itemTag("Empty folder"));
            empty.requestFocus();
            empty.performClick();
            waitForFolder(nav, page);
            assertInFiles(nav, page, "Opening an empty folder");
            save(nav, "01572-files-09-empty-folder");
            press(host, KeyEvent.KEYCODE_BACK, nav);
            waitForFolder(nav, page);

            // Grid / List: switches view, focus stays on the same item.
            View gridButton = byText(page, "Grid");
            assertNotNull(gridButton);
            page.findViewWithTag(FolderBrowser.itemTag("Movies")).requestFocus();
            gridButton.performClick();
            settle(nav);
            save(nav, "01572-files-10-drive-grid");
            View inGrid = page.findViewWithTag(FolderBrowser.itemTag("Movies"));
            inGrid.requestFocus();
            inGrid.performClick();
            waitForFolder(nav, page);
            save(nav, "01572-files-11-folder-grid");

            // Sort menu beside its button; Order follows the sort.
            View sortButton = byTextStart(page, "Sort:");
            sortButton.requestFocus();
            sortButton.performClick();
            settle(nav);
            saveWithMenu(nav, "01572-files-12-sort-menu");
            dismissMenu(nav);
            View order = byText(page, "A → Z");
            assertNotNull("Order button says A → Z for Name", order);
            order.performClick();
            settle(nav);
            assertNotNull(byText(page, "Z → A"));
            order = byText(page, "Z → A");
            order.performClick();
            byText(page, "List").performClick();
            settle(nav);

            // Moving along the tabs and back keeps the folder (F11); OK on the open tab resets it (F12).
            tabs.tab(FilesPage.THIS_DEVICE).requestFocus();
            settle(nav);
            tabs.tab(FilesPage.CONNECTIONS).requestFocus();
            settle(nav);
            save(nav, "01572-files-13-connections-tab");
            press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            save(nav, "01572-files-14-connections-rows");
            tabs.tab(FilesPage.THIS_DEVICE).requestFocus();
            settle(nav);
            assertNotNull("The folder is still open", page.findViewWithTag(FolderBrowser.itemTag(
                "Conclave (2024) Director's Cut.mkv")));
            tabs.tab(FilesPage.THIS_DEVICE).performClick();
            settle(nav);
            assertNotNull("OK on the open tab returns it to its start", byDescription(page, "Backup #2"));
            save(nav, "01572-files-15-tab-reset");

            // Library Care and Your Library.
            tabs.tab(FilesPage.LIBRARY_CARE).requestFocus();
            settle(nav);
            press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            save(nav, "01572-files-16-library-care");
            tabs.tab(FilesPage.YOUR_LIBRARY).requestFocus();
            settle(nav);
            save(nav, "01572-files-17-your-library");
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }

    /** A realistic drive: long names, nested shows, an empty folder, a file that is not a video. */
    private static File drive() throws Exception
    {
        File root = java.nio.file.Files.createTempDirectory("backup2").toFile();
        File movies = new File(root, "Movies");
        movies.mkdirs();
        String[] films = {"Conclave (2024) Director's Cut.mkv", "Flow.2024.2160p.HDR.mkv", "Anora (2024).mp4",
                          "The Brutalist (2024) [Remux 2160p HDR10 Atmos] Part One and Part Two.mkv",
                          "home-video-2019-08-14.mkv", "poster.jpg"};
        for (String film : films)
        {
            sized(new File(movies, film), film.endsWith(".jpg") ? 1 : 14);
        }
        File season = new File(root, "TV Shows/Slow Horses/Season 4");
        season.mkdirs();
        for (int episode = 1; episode <= 6; episode++)
        {
            sized(new File(season, "Slow.Horses.S04E0" + episode + ".mkv"), 2);
        }
        new File(root, "Empty folder").mkdirs();
        new File(root, "Documentaries about very long subjects that go on and on").mkdirs();
        sized(new File(root, "Nosferatu (2024).mkv"), 9);
        return root;
    }

    private static void sized(File file, int megabytes) throws Exception
    {
        try (RandomAccessFile out = new RandomAccessFile(file, "rw"))
        {
            out.setLength(megabytes * 1024L * 1024L);
        }
    }

    private static Snapshot snapshot(File drive)
    {
        Snapshot s = new Snapshot();
        String[][] films = {{"Movies/Conclave (2024) Director's Cut.mkv", "Conclave"},
                            {"Movies/Anora (2024).mp4", "Anora"},
                            {"Movies/Flow.2024.2160p.HDR.mkv", "Flow"},
                            {"Nosferatu (2024).mkv", "Nosferatu"}};
        for (int i = 0; i < films.length; i++)
        {
            Movie movie = new Movie(i + 1, drive.getPath() + "/" + films[i][0], films[i][1], i + 1, "A film.", 2024,
                                    7, "", null, 100000, 0, 0, 0, false, false, false, false, i + 1, i + 1, 3840,
                                    2160, "Atmos", null, null, null, 0, 1, 1000, 0);
            Entry entry = new Entry(movie, i + 1, 0, "Drama");
            s.movies.add(entry);
            s.recent.add(entry);
            s.technical.add(entry);
        }
        return s;
    }

    private static void assertInFiles(View nav, FilesPage page, String step)
    {
        View focus = nav.findFocus();
        assertNotNull(step + ": nothing focused", focus);
        assertTrue(step + ": focus jumped out of Files to " + focus, FilesPage.isInside(page, focus));
    }

    private static void press(ActivityController<TopNavigationTest.Host> host, int key, View nav)
    {
        host.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, key));
        host.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, key));
        settle(nav);
    }

    private static void waitForFolder(View nav, View page) throws Exception
    {
        for (int wait = 0; wait < 30; wait++)
        {
            Thread.sleep(40);
            settle(nav);
        }
    }

    private static View byDescription(View view, String start)
    {
        if (view.getContentDescription() != null && view.getContentDescription().toString().startsWith(start)
            && view.isFocusable())
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = byDescription(group.getChildAt(i), start);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    private static View byText(View view, String words)
    {
        if (view instanceof android.widget.TextView && words.equals(((android.widget.TextView) view).getText()
            .toString()) && view.isFocusable())
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = byText(group.getChildAt(i), words);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    private static View byTextStart(View view, String words)
    {
        if (view instanceof android.widget.TextView && ((android.widget.TextView) view).getText().toString()
            .startsWith(words) && view.isFocusable())
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = byTextStart(group.getChildAt(i), words);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    static void settle(View v)
    {
        for (int i = 0; i < 4; i++)
        {
            v.measure(View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
                      View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY));
            v.layout(0, 0, WIDTH, HEIGHT);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(120));
        }
    }

    private static void dismissMenu(View nav)
    {
        Dialog menu = ShadowDialog.getLatestDialog();
        if (menu != null && menu.isShowing())
        {
            menu.dismiss();
        }
        settle(nav);
    }

    static void save(View v, String name) throws Exception
    {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        v.draw(new Canvas(bitmap));
        write(bitmap, name);
    }

    /** The page with the open menu drawn where its window sits, as on the screen. */
    static void saveWithMenu(View v, String name) throws Exception
    {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        Dialog menu = ShadowDialog.getLatestDialog();
        assertNotNull("No menu opened", menu);
        assertTrue("The menu is not showing", menu.isShowing());
        View decor = menu.getWindow().getDecorView();
        WindowManager.LayoutParams at = menu.getWindow().getAttributes();
        int width = at.width > 0 ? at.width : WIDTH / 3;
        int height = at.height > 0 ? at.height : HEIGHT / 2;
        decor.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        decor.layout(0, 0, width, height);
        canvas.drawColor(0x59000000);
        canvas.save();
        canvas.translate(at.x, at.y);
        decor.draw(canvas);
        canvas.restore();
        write(bitmap, name);
    }

    private static void write(Bitmap bitmap, String name) throws Exception
    {
        File file = new File("build/reports/preview-ui/" + name + ".png");
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file))
        {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }
}
