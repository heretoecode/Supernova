package com.archos.mediacenter.video.leanback.filebrowsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.localstorage.JavaFile2;
import com.archos.mediacenter.video.leanback.PreviewAsyncFixtures;
import com.archos.mediacenter.video.leanback.PreviewPagesTest;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import com.archos.mediacenter.video.leanback.adapter.object.Box;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.util.ReflectionHelpers;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Network & Files › Local Storage, 0.154 design (docs/design/proposals/local-storage/14–21). */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, qualifiers = "w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class LocalStorageViewTest {
    @Rule public TemporaryFolder temp = new TemporaryFolder();
    private ActivityController<TopNavigationTest.Host> host;
    private LocalStorageView view;
    private File drive;
    private int changes;

    @Before
    public void setup() throws Exception {
        PreviewAsyncFixtures.offlineTransport();
        host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(host.get());
        drive = temp.newFolder("Backup2");
        BrowserSelection selection = new BrowserSelection(Collections.emptySet(), Collections.emptySet());
        view = new LocalStorageView(host.get(), prefs, selection, () -> changes++);
        FrameLayout root = new FrameLayout(host.get());
        root.setBackgroundColor(0xFF0A1650);
        root.setPadding(56, 112, 56, 40);
        root.addView(view, new FrameLayout.LayoutParams(-1, -1));
        host.get().setContentView(root);
        view.setVolumes(List.of(new Box(Box.ID.FOLDERS, "Internal", 0, temp.getRoot().getPath() + "/internal"),
                                new Box(Box.ID.USB, "Backup #2", 0, drive.getPath())));
        settle();
    }

    @After
    public void close() throws Exception {
        view.close();
        host.pause().stop().destroy();
        PreviewAsyncFixtures.drain();
    }

    private void settle() {
        for (int i = 0; i < 6; i++) {
            View root = view.getRootView();
            root.measure(View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                         View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY));
            root.layout(0, 0, 1920, 1080);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(60));
        }
    }

    private String text(String tag) {
        View v = view.findViewWithTag(tag);
        return v instanceof TextView ? ((TextView)v).getText().toString() : null;
    }

    private static List<String> pills(View root) {
        List<String> found = new ArrayList<>();
        collect(root.findViewWithTag("local.pills"), found);
        return found;
    }

    private static void collect(View v, List<String> out) {
        if (v instanceof TextView)
            out.add(((TextView)v).getText().toString());
        else if (v instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)v).getChildCount(); i++)
                collect(((ViewGroup)v).getChildAt(i), out);
    }

    /** Seeds a folder listing as if it had been listed earlier this session (opens instantly from memory). */
    private Uri seed(File folder, String... names) throws Exception {
        List<MetaFile2> files = new ArrayList<>();
        for (String name : names) {
            File f = new File(folder, name);
            if (name.contains("."))
                assertTrue(f.createNewFile() || f.exists());
            else
                assertTrue(f.mkdirs() || f.isDirectory());
            files.add(new JavaFile2(f));
        }
        Uri uri = Uri.fromFile(folder);
        Map<String, List<MetaFile2>> listings = ReflectionHelpers.getField(view, "listings");
        listings.put(uri.toString(), files);
        Map<String, Map<String, Object>> index = ReflectionHelpers.getField(view, "indexedByFolder");
        index.put(uri.toString(), Collections.emptyMap());
        return uri;
    }

    @Test
    public void driveListShowsFriendlyNamesNoRawPathsAndNoAllFiles() throws Exception {
        assertEquals("Local Storage", text("local.title"));
        View usb = view.findViewWithTag("storage:" + Uri.fromFile(drive));
        assertNotNull(usb);
        List<String> rows = new ArrayList<>();
        collect(view.findViewWithTag("local.list"), rows);
        assertTrue(rows.toString(), rows.contains("Backup #2"));
        assertTrue(rows.toString(), rows.contains("Internal storage"));
        for (String row : rows) {
            assertFalse("no raw paths: " + row, row.startsWith("/"));
            assertFalse("All Files is no longer a place", row.contains("All Files"));
        }
        assertEquals(List.of("2 drives"), pills(view));
        assertTrue(usb.isFocusable());
        PreviewPagesTest.capture(view.getRootView(), "0154-local-drives");
    }

    @Test
    public void folderHeaderHasPathChipsTitleAndPillsAndRowsHaveTwoLines() throws Exception {
        File tv = new File(drive, "TV/1883 - The Bass Reeves Story");
        assertTrue(tv.mkdirs());
        Uri folder = seed(tv, "Lawmen - Bass Reeves - S01E01 - PART I.mkv", "Lawmen - Bass Reeves - S01E02 - PART II.mkv",
                          "Extras");
        view.open(folder);
        settle();
        assertEquals("1883 - The Bass Reeves Story", text("local.title"));
        assertEquals("Local Storage", text("local.chip.0"));
        assertEquals("Backup #2", text("local.chip.1"));
        assertEquals("TV", text("local.chip.2"));
        assertTrue(pills(view).toString(), pills(view).contains("2 videos"));
        assertTrue(pills(view).toString(), pills(view).contains("1 folder"));
        View first = view.findViewWithTag(Uri.fromFile(new File(tv, "Lawmen - Bass Reeves - S01E01 - PART I.mkv"))
                                              .toString());
        assertNotNull(first);
        List<String> lines = new ArrayList<>();
        collect(first, lines);
        assertTrue("name without extension: " + lines, lines.contains("Lawmen - Bass Reeves - S01E01 - PART I"));
        assertNull("nothing loading-related for a remembered folder", view.findViewWithTag("local.placeholder.first"));
        PreviewPagesTest.capture(view.getRootView(), "0154-local-folder");
    }

    @Test
    public void focusedRowShowsItsButtonsRightMovesOntoThemAndBackReturns() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        Uri folder = seed(movies, "Film.mkv", "Other.mkv");
        view.open(folder);
        settle();
        View row = view.findViewWithTag(Uri.fromFile(new File(movies, "Film.mkv")).toString());
        assertTrue(row.requestFocus());
        settle();
        View play = row.findViewWithTag("local.row.action.play");
        assertNotNull(play);
        assertTrue("buttons show on the focused row", play.isShown());
        View other = view.findViewWithTag(Uri.fromFile(new File(movies, "Other.mkv")).toString());
        assertFalse("only the focused row shows buttons", other.findViewWithTag("local.row.action.play").isShown());
        row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        settle();
        assertTrue(play.hasFocus());
        assertTrue("row keeps a faint fill while a button has focus", row.isActivated());
        PreviewPagesTest.capture(view.getRootView(), "0154-local-row-buttons");
        play.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
        settle();
        assertTrue(row.hasFocus());
    }

    @Test
    public void aFileNotInTheLibraryOffersDetailsThatLooksItUpWithoutAddingIt() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        Uri folder = seed(movies, "Arrival.2016.1080p.mkv");
        view.open(folder);
        settle();
        View row = view.findViewWithTag(Uri.fromFile(new File(movies, "Arrival.2016.1080p.mkv")).toString());
        assertTrue(row.requestFocus());
        settle();
        View details = row.findViewWithTag("local.row.action.details");
        assertNotNull("a file outside the library still has Details", details);
        details.performClick();
        settle();
        android.app.Dialog page = org.robolectric.shadows.ShadowDialog.getLatestDialog();
        assertNotNull(page);
        assertTrue(page.isShowing());
        View title = page.getWindow().getDecorView().findViewWithTag("unlibrary.title");
        assertNotNull(title);
        assertNotNull(page.getWindow().getDecorView().findViewWithTag("unlibrary.action.library"));
        assertEquals("opening Details adds nothing", 0, changes);
        page.dismiss();
    }

    @Test
    public void aReloadedRowGivesFocusBackToTheButtonYouUsed() throws Exception {
        Uri folder = seed(drive, "Film.mkv");
        view.open(folder);
        settle();
        View row = view.findViewWithTag(Uri.fromFile(new File(drive, "Film.mkv")).toString());
        assertNotNull(row);
        assertTrue(row.requestFocus());
        settle();
        View action = row.findViewWithTag("local.row.action.play");
        assertTrue(action.requestFocus());
        ReflectionHelpers.callInstanceMethod(view, "rememberFocus");
        ReflectionHelpers.callInstanceMethod(view, "restoreFocus");
        settle();
        assertTrue(action.hasFocus());
    }

    @Test
    public void backGoesUpOneFolderThenToTheDrivesWithFocusOnWhereYouCameFrom() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        seed(drive, "Movies");
        Uri folder = seed(movies, "Film.mkv");
        view.open(folder);
        settle();
        assertTrue(view.back());
        settle();
        assertEquals(Uri.fromFile(drive), view.location());
        assertEquals("Backup #2", text("local.title"));
        View came = view.findFocus();
        assertNotNull(came);
        assertEquals(Uri.fromFile(movies).toString(), came.getTag());
        assertTrue(view.back());
        settle();
        assertNull(view.location());
        assertEquals("storage:" + Uri.fromFile(drive), view.findFocus().getTag());
        assertFalse("at the drive list Back leaves the page", view.back());
    }

    @Test
    public void addingAFolderToTheLibraryMarksItAndTellsTheHost() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        Uri top = seed(drive, "Movies");
        view.open(top);
        settle();
        View row = view.findViewWithTag(Uri.fromFile(movies).toString());
        assertTrue(row.requestFocus());
        settle();
        row.findViewWithTag("local.row.action.add").performClick();
        settle();
        assertEquals(1, changes);
        View again = view.findViewWithTag(Uri.fromFile(movies).toString());
        List<String> lines = new ArrayList<>();
        collect(again, lines);
        assertTrue(lines.toString(), lines.contains("Folder · Library folder"));
    }

    @Test
    public void secondLineFactsAndResumeText() {
        assertEquals("Resume at 19:42 (41%)", LocalStorageView.resumeText(1182000, 2880000));
        assertEquals("Resume at 1:02:03", LocalStorageView.resumeText(3723000, 0));
        assertEquals("1080p", LocalStorageView.resolution(1920, 800));
        assertEquals("4K", LocalStorageView.resolution(3840, 1600));
        assertEquals("", LocalStorageView.resolution(0, 0));
        assertEquals("Film.2024", LocalStorageView.withoutExtension("Film.2024.mkv"));
        assertEquals("312 MB", LocalStorageView.size(312_000_000L));
        assertEquals("1.9 GB", LocalStorageView.size(1_916_000_000L));
    }

    @Test
    public void systemFoldersHiddenUnlessAllFiles() {
        assertTrue(LocalStorageView.isSystemFolder("LOST.DIR"));
        assertTrue(LocalStorageView.isSystemFolder("Android"));
        assertTrue(LocalStorageView.isSystemFolder(".thumbnails"));
        assertFalse(LocalStorageView.isSystemFolder("Movies"));
    }

    /** Owner video 4 Nov 2026 (notes #30): Home in the top bar lit up while a new folder loaded. */
    @Test
    public void focusStaysInThePageWhileANewFolderLoads() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        for (int i = 0; i < 4; i++)
            assertTrue(new File(movies, "Film " + i + ".mkv").createNewFile());
        View usb = view.findViewWithTag("storage:" + Uri.fromFile(drive));
        assertTrue(usb.requestFocus());
        settle();
        view.open(Uri.fromFile(movies)); // not seeded: listed for the first time, so the list is swapped
        for (int step = 0; step < 40; step++) {
            View focused = view.getRootView().findFocus();
            assertNotNull("something keeps focus at step " + step, focused);
            assertTrue("focus never leaves Local Storage (step " + step + ", on " + focused.getTag() + ")",
                       view.hasFocus());
            layoutOnce();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(25));
            PreviewAsyncFixtures.drain();
        }
        settle();
        View focused = view.findFocus();
        assertNotNull(focused);
        assertTrue("focus ends on a real row, not the holder: " + focused.getTag(),
                   Uri.decode(String.valueOf(focused.getTag())).contains("Film "));
        assertFalse("the holder gives focus back and is not focusable afterwards",
                    view.findViewWithTag("local.focus_holder").isFocusable());
    }

    /** Owner video 4 Nov 2026 (notes #31): the header jumped up when switching All files / Videos only. */
    @Test
    public void headerKeepsItsHeightWhenSwitchingAllFilesAndVideosOnly() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        Uri folder = seed(movies, "Film.mkv", "Other.mkv", "Extras");
        view.open(folder);
        settle();
        View header = view.findViewWithTag("local.header");
        int before = header.getHeight();
        List<String> pillsBefore = pills(view);
        assertTrue(before > 0);
        view.findViewWithTag("local.filter").performClick();
        layoutOnce(); // the very next frame, before the new listing arrives
        assertEquals("header height right after the switch", before, header.getHeight());
        assertEquals("old counts stay until the new ones are ready", pillsBefore, pills(view));
        settle();
        assertEquals("header height once the new listing is in", before, header.getHeight());
    }

    /** Owner-approved (notes #11, option A): every row is tagged; "Not added" when outside the library. */
    @Test
    public void everyRowShowsItsLibraryStatus() throws Exception {
        File movies = new File(drive, "Movies");
        assertTrue(movies.mkdirs());
        Uri folder = seed(movies, "Film.mkv", "Extras");
        view.open(folder);
        settle();
        View film = view.findViewWithTag(Uri.fromFile(new File(movies, "Film.mkv")).toString());
        TextView tag = film.findViewWithTag("local.row.status");
        assertEquals(View.VISIBLE, tag.getVisibility());
        assertEquals("Not added", tag.getText().toString());
        assertEquals("no watched pill for a file outside the library", View.GONE,
                     film.findViewWithTag("local.row.watched").getVisibility());
    }

    @Test
    public void watchedPillFillsWithProgressAndIsSolidWhenWatched() {
        android.graphics.Bitmap image = android.graphics.Bitmap.createBitmap(100, 20,
                                                                             android.graphics.Bitmap.Config.ARGB_8888);
        LocalStorageView.WatchPill progress = new LocalStorageView.WatchPill(0, .4f, false);
        progress.setBounds(0, 0, 100, 20);
        progress.draw(new android.graphics.Canvas(image));
        assertEquals("watched part is blue", 0xFF3A7BD5, image.getPixel(20, 10));
        assertEquals("the rest is dark", 0xFF1A2A44, image.getPixel(80, 10));
        image.eraseColor(0);
        LocalStorageView.WatchPill done = new LocalStorageView.WatchPill(0, 1f, true);
        done.setBounds(0, 0, 100, 20);
        done.draw(new android.graphics.Canvas(image));
        assertEquals(0xFFF4F6FB, image.getPixel(50, 10));
        image.recycle();
    }

    private void layoutOnce() {
        View root = view.getRootView();
        root.measure(View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                     View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, 1920, 1080);
    }
}
