package com.archos.mediacenter.video.leanback;
import android.graphics.Rect;
import android.os.Looper;
import android.view.*;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.CustomApplication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = CustomApplication.class, sdk = 28,
        qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextQaWizardTest {
    @Before
    public void offline() {
        Shadows.shadowOf(RuntimeEnvironment.getApplication())
            .grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
        PreviewAsyncFixtures.offlineTransport();
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .putBoolean("try_new_ui", true)
            .putBoolean("supernova_onboarding_complete", true)
            .remove(CustomLibraryPage.KEY)
            .commit();
    }

    @After
    public void drain() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    /** Owner report 2 Nov 2026: Movies/TV blur content scrolling under the top bar; the custom page showed a hard band instead. */
    @Test
    public void customPageBlursContentScrollingUnderTheTopBar() throws Exception {
        CustomLibraryPage page = new CustomLibraryPage();
        page.name = "Documentaries";
        assertTrue(page.save(RuntimeEnvironment.getApplication()));
        var host = Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
        try {
            View root = host.get().findViewById(android.R.id.content);
            PreviewPagesTest.layout(root);
            TopNavigation navigation = ReflectionHelpers.getField(host.get(), "navigation");
            assertFalse("not scrolled at the top", (boolean)ReflectionHelpers.getField(navigation, "scrolled"));
            Object workspace = ReflectionHelpers.getField(host.get(), "workspace");
            PreviewPages pages = ReflectionHelpers.getField(workspace, "sharedLibrary");
            RecyclerView list =
                (RecyclerView)pages.getChildAt(0);
            list.scrollBy(0, Math.round(160 * root.getResources().getDisplayMetrics().density));
            PreviewPagesTest.layout(root);
            if (list.canScrollVertically(-1)) {
                assertTrue("top bar switches to its blurred scrolled state",
                           (boolean)ReflectionHelpers.getField(navigation, "scrolled"));
            }
            // Deterministic wiring check, independent of how much fixture content there is to scroll.
            Consumer<Boolean> listener = ReflectionHelpers.getField(pages, "scrollListener");
            assertNotNull("custom page reports scrolling to the top bar", listener);
            listener.accept(true);
            assertTrue((boolean)ReflectionHelpers.getField(navigation, "scrolled"));
            listener.accept(false);
            assertFalse((boolean)ReflectionHelpers.getField(navigation, "scrolled"));
            PreviewPagesTest.capture(root, "custom-scrolled-under-top-bar");
        } finally {
            host.pause().stop().destroy();
        }
    }

    /** Owner 2 Nov 2026: the toolbar sits centred between the statistics and the separator on every library page (shared code). */
    @Test
    public void libraryToolbarIsCentredBetweenStatisticsAndSeparator() throws Exception {
        CustomLibraryPage page = new CustomLibraryPage();
        page.name = "Documentaries";
        assertTrue(page.save(RuntimeEnvironment.getApplication()));
        var host = Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
        try {
            View root = host.get().findViewById(android.R.id.content);
            PreviewPagesTest.layout(root);
            View stats = root.findViewWithTag("library.statistics"),
                 filters = root.findViewWithTag("custom.control.filters"),
                 divider = root.findViewWithTag("library.toolbar.divider");
            assertNotNull(stats);
            assertNotNull(filters);
            assertNotNull(divider);
            int[] s = new int[2], f = new int[2], d = new int[2];
            stats.getLocationInWindow(s);
            filters.getLocationInWindow(f);
            divider.getLocationInWindow(d);
            int above = f[1] - (s[1] + stats.getHeight()), below = d[1] - (f[1] + filters.getHeight());
            float density = root.getResources().getDisplayMetrics().density;
            assertTrue("gap above " + above + "px vs below " + below + "px",
                       Math.abs(above - below) <= Math.round(2 * density));
            PreviewPagesTest.capture(root, "library-toolbar-centred");
        } finally {
            host.pause().stop().destroy();
        }
    }

    /** Owner report 2 Nov 2026: opening Documentaries left focus invisible. Like Movies/TV, it must sit on the page's own tab. */
    @Test
    public void openingCustomPageKeepsVisibleFocusOnItsOwnTab() {
        CustomLibraryPage page = new CustomLibraryPage();
        page.name = "Documentaries";
        assertTrue(page.save(RuntimeEnvironment.getApplication()));
        var host = Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
        try {
            View root = host.get().findViewById(android.R.id.content);
            PreviewPagesTest.layout(root);
            View tab = root.findViewWithTag("semantic:topnav.custom");
            assertNotNull(tab);
            assertSame("focus starts on the Documentaries tab", tab, root.findFocus());
            assertTrue(tab.isSelected());
            View statistics = root.findViewWithTag("library.statistics");
            Rect visible = new Rect();
            assertNotNull(statistics);
            assertTrue("statistics still visible without scrolling", statistics.getGlobalVisibleRect(visible));
            root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
            root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN));
            PreviewPagesTest.layout(root);
            Object workspace = ReflectionHelpers.getField(host.get(), "workspace");
            PreviewPages pages = ReflectionHelpers.getField(workspace, "sharedLibrary");
            assertTrue("DOWN enters the page", pages.hasFocus());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void savedCustomPageShowsEntireStatisticsOnFirstEntryWithoutScrolling() throws Exception {
        CustomLibraryPage page = new CustomLibraryPage();
        page.name = "QA library";
        assertTrue(page.save(RuntimeEnvironment.getApplication()));
        var host = Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
        try {
            View root = host.get().findViewById(android.R.id.content);
            PreviewPagesTest.layout(root);
            View statistics = root.findViewWithTag("library.statistics");
            assertNotNull(statistics);
            Rect visible = new Rect();
            assertTrue(statistics.getGlobalVisibleRect(visible));
            assertEquals(statistics.getHeight(), visible.height());
            assertTrue(visible.height() > 0);
            Object workspace = ReflectionHelpers.getField(host.get(), "workspace");
            PreviewPages pages = ReflectionHelpers.getField(workspace, "sharedLibrary");
            View parent = (View)pages.getParent();
            final int[] detaches = {0};
            pages.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View v) {
                }
                public void onViewDetachedFromWindow(View v) {
                    detaches[0]++;
                }
            });
            host.get().applySnapshot(new PreviewLibraryLoader.Snapshot());
            PreviewPagesTest.layout(root);
            assertSame(parent, pages.getParent());
            assertEquals(0, detaches[0]);
            statistics = root.findViewWithTag("library.statistics");
            assertTrue(statistics.getGlobalVisibleRect(visible));
            assertEquals(statistics.getHeight(), visible.height());
            PreviewPagesTest.capture(root, "shield-custom-first-entry-software-only");
        } finally {
            host.pause().stop().destroy();
        }
    }

    // The four-section editor tests were replaced by EditPagePanelTest (approved side panel, 2 Nov 2026).

    // Wait for the real initial snapshot before installing this test's indexed-media fixture.
    // Otherwise its posted completion can replace the fixture while layout drains the looper.
    private static void finishInitialLibraryLoad(CustomLibraryActivity activity) throws Exception {
        ExecutorService worker = ReflectionHelpers.getField(activity, "worker");
        worker.submit(() -> {}).get(15, TimeUnit.SECONDS);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }
}
