package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.files.FilesPage;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
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

import static org.junit.Assert.assertNotNull;

/** Renders the real screens named in docs/0.157/REVIEW_0157.md, at 1080p, inside the real app frame. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0157RenderTest {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void filesPageInTheAppFrame() throws Exception {
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .putBoolean("supernova_files_page", true)
            .commit();
        try {
            Picasso.get();
        } catch (IllegalStateException e) {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            TopNavigation nav = new TopNavigation(host.get(), pages, pages::setTab, pages::atTop);
            host.get().setContentView(nav);
            pages.setArtworkListener(nav::setArtwork);
            pages.setDiscovery(new PreviewDiscovery());
            pages.setSnapshot(snapshot());
            pages.setFiles(Arrays.asList(new Box(Box.ID.FOLDERS, "Internal storage", 0),
                                         new Box(Box.ID.USB, "Backup drive", 0, "/test"),
                                         new Box(Box.ID.NETWORK, "Network", 0)));
            View filesTab = nav.findViewWithTag("semantic:topnav.network");
            assertNotNull(filesTab);
            filesTab.performClick();
            settle(nav);
            filesTab.requestFocus();
            settle(nav);
            save(nav, "0157-files-topbar-focused");
            FilesPage page = find(nav, FilesPage.class);
            assertNotNull("Files page is not in the app frame", page);
            PageTabs tabs = find(page, PageTabs.class);
            assertNotNull(tabs);
            for (int tab = 0; tab < FilesPage.TABS.length; tab++) {
                tabs.select(tab);
                settle(nav);
                tabs.tab(tab).requestFocus();
                settle(nav);
                save(nav, "0157-files-tab-" + tab + "-focused");
                View first = page.findViewWithTag("files.row.first");
                if (first != null) {
                    first.requestFocus();
                    settle(nav);
                }
                save(nav, "0157-files-tab-" + tab + "-content");
            }
        } finally {
            host.pause().stop().destroy();
        }
    }

    private static Snapshot snapshot() {
        Snapshot s = new Snapshot();
        for (int i = 1; i <= 6; i++) {
            Movie movie = new Movie(i, "/storage/usb/Film " + i + ".mkv", "Film " + i, i, "A film.", 2024, 7, "",
                                    null, 100000, 0, 0, 0, false, false, false, false, i, i, 3840, 2160, "Atmos",
                                    null, null, null, 0, 1, 1000, 0);
            Entry entry = new Entry(movie, i, 0, "Drama");
            s.movies.add(entry);
            s.recent.add(entry);
            s.technical.add(entry);
        }
        return s;
    }

    static void settle(View v) {
        for (int i = 0; i < 4; i++) {
            v.measure(View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
                      View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY));
            v.layout(0, 0, WIDTH, HEIGHT);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(120));
        }
    }

    static <T extends View> T find(View v, Class<T> kind) {
        if (kind.isInstance(v))
            return kind.cast(v);
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)v;
            for (int i = 0; i < group.getChildCount(); i++) {
                T match = find(group.getChildAt(i), kind);
                if (match != null)
                    return match;
            }
        }
        return null;
    }

    static void save(View v, String name) throws Exception {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        v.draw(new Canvas(bitmap));
        File file = new File("build/reports/preview-ui/" + name + ".png");
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }
}
