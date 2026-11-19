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

/** Checks custom page statistics, toolbar placement and focus on initial entry. */
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
            // 0.156: the custom page drives the same charcoal fade as Home, from its real scroll distance.
            java.util.function.IntConsumer listener = ReflectionHelpers.getField(pages, "scrollOffsetListener");
            assertNotNull("custom page reports its scroll to the top bar", listener);
            listener.accept(10_000);
            assertEquals(255, navigation.fadeAlpha());
            listener.accept(0);
            assertEquals(0, navigation.fadeAlpha());
            PreviewPagesTest.capture(root, "custom-scrolled-under-top-bar");
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
            // 0.158: no statistics row on library pages (LIBRARY.md).
            assertNull(root.findViewWithTag("library.statistics"));
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

    // The old editor's tests went with it in 0.158; Customise page is pressed in Review0158LibraryTest.

    // Wait for the real initial snapshot before installing this test's indexed-media fixture.
    // Otherwise its posted completion can replace the fixture while layout drains the looper.
    private static void finishInitialLibraryLoad(CustomLibraryActivity activity) throws Exception {
        ExecutorService worker = ReflectionHelpers.getField(activity, "worker");
        worker.submit(() -> {}).get(15, TimeUnit.SECONDS);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }
}
