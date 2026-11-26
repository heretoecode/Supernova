package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.squareup.picasso.Picasso;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowDialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 0.158 library pages (page sheet docs/design/pages/LIBRARY.md; Definition of done 3 and 4): every action in the
 * sheet's table is pressed with realistic data, a render of where it leads is saved, and focus never lands in the
 * top bar unasked.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0158LibraryTest
{
    /** The test thread's stack, caught by a watchdog after 20 s, so a hang shows where it is. */
    private static volatile String caught = "";

    @Before
    public void watchdog()
    {
        Thread test = Thread.currentThread();
        caught = "";
        Thread dog = new Thread(() -> {
            try
            {
                Thread.sleep(20000);
            }
            catch (InterruptedException stop)
            {
                return;
            }
            caught = compact(test.getStackTrace());
        }, "render-watchdog");
        dog.setDaemon(true);
        dog.start();
    }

    /** "Class.method:line" for the app's own frames, outermost last. */
    static String compact(StackTraceElement[] frames)
    {
        StringBuilder out = new StringBuilder();
        for (StackTraceElement frame : frames)
        {
            if (!frame.getClassName().startsWith("com.archos"))
            {
                continue;
            }
            String name = frame.getClassName().substring(frame.getClassName().lastIndexOf('.') + 1);
            out.append(name).append('.').append(frame.getMethodName()).append(':').append(frame.getLineNumber())
                .append(" < ");
        }
        return out.toString();
    }

    /** Runs a test body; a hang that ends in running out of memory fails with where it was. */
    private static void guarded(Body body) throws Exception
    {
        try
        {
            body.run();
        }
        catch (OutOfMemoryError lost)
        {
            String at = compact(lost.getStackTrace());
            lost = null;
            System.gc();
            throw new AssertionError("HANG " + caught + " || OOM " + at);
        }
    }

    interface Body
    {
        void run() throws Exception;
    }

    @Before
    public void picasso()
    {
        try
        {
            Picasso.get();
        }
        catch (IllegalStateException e)
        {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().clear().commit();
    }

    private static final class Page
    {
        ActivityController<TopNavigationTest.Host> host;
        PreviewPages pages;
        TopNavigation nav;
    }

    private static Page open(Snapshot snapshot, String tab)
    {
        Page page = new Page();
        page.host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        page.pages = new PreviewPages(page.host.get(), (holder, item) -> {
        });
        page.nav = new TopNavigation(page.host.get(), page.pages, page.pages::setTab, page.pages::atTop);
        page.host.get().setContentView(page.nav);
        page.pages.setDiscovery(new PreviewDiscovery());
        page.pages.setSnapshot(snapshot);
        Review0158Fixture.settle(page.nav);
        page.nav.findViewWithTag("semantic:topnav." + tab).performClick();
        Review0158Fixture.settle(page.nav);
        return page;
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void everyMoviesActionPressed() throws Exception
    {
        guarded(() -> {
            Page page = open(Review0158Fixture.snapshot(), "movies");
            try
            {
                TopNavigation nav = page.nav;
                assertNull("No statistics row or page title", Review0158Fixture.anyText(page.pages, "Movie library"));
                View movies = nav.findViewWithTag("semantic:topnav.movies");
                movies.requestFocus();
                Review0158Fixture.settle(nav);
                Review0158Fixture.save(nav, "0158-lib-01-movies-grid");

                // Down from the top bar: the toolbar's first button; then the first poster.
                Review0158Fixture.press(page.host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
                View filters = nav.findFocus();
                assertNotNull(filters);
                assertEquals("Down lands on Filters", "control:0", filters.getTag());
                Review0158Fixture.save(nav, "0158-lib-02-toolbar-focus");
                Review0158Fixture.press(page.host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
                View poster = nav.findFocus();
                assertTrue("Then the first poster", poster != null && poster.getTag() instanceof String
                    && ((String) poster.getTag()).length() > 0 && !((String) poster.getTag()).startsWith("control:"));
                Review0158Fixture.save(nav, "0158-lib-03-poster-focused");

                // Filters: menu under the button; Watched list on top; a choice applies live.
                filters.performClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-04-filters-menu");
                Dialog filtersMenu = ShadowDialog.getLatestDialog();
                clickRow(filtersMenu, "Watched");
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-05-filters-watched");
                clickRow(ShadowDialog.getLatestDialog(), "Watched ·");
                Review0158Fixture.settle(nav);
                assertNull("Unwatched films filtered out", Review0158Fixture.anyText(page.pages, "Conclave"));
                clickRow(filtersMenu, "Genre");
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-06-filters-genres");
                Review0158Fixture.dismissDialog(nav);
                clickRow(filtersMenu, "Year");
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-07-filters-year");
                Review0158Fixture.dismissDialog(nav);
                clickRow(filtersMenu, "Clear filters");
                Review0158Fixture.settle(nav);
                assertNotNull("Clear filters brings everything back", Review0158Fixture.anyText(page.pages, "Conclave"));

                // Sort (final tv/48): grouped menu; "Size" on every page; the Order button follows (Largest first).
                View sort = Review0158Fixture.byTextStart(page.pages, "Sort:");
                assertNotNull(sort);
                sort.requestFocus();
                sort.performClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-08-sort-menu");
                assertNull("No Resolution sort any more", rowLabel("Resolution"));
                clickRow(ShadowDialog.getLatestDialog(), "Size");
                Review0158Fixture.settle(nav);
                assertNotNull(Review0158Fixture.byText(page.pages, "Sort: Size"));
                View order = Review0158Fixture.byText(page.pages, "Largest first");
                assertNotNull("Order follows the sort", order);
                Review0158Fixture.save(nav, "0158-lib-09-sorted-by-size");
                order.performClick();
                Review0158Fixture.settle(nav);
                assertNotNull(Review0158Fixture.byText(page.pages, "Smallest first"));

                // List view, Columns (menu, pick up and move), Grid view.
                View list = Review0158Fixture.byText(page.pages, "List view");
                list.performClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.save(nav, "0158-lib-10-list-view");
                View columns = Review0158Fixture.byText(page.pages, "Columns");
                assertNotNull("Columns appears in list view", columns);
                columns.performClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-11-columns-menu");
                clickRow(ShadowDialog.getLatestDialog(), "Running time");
                Review0158Fixture.settle(nav);
                Dialog moving = ShadowDialog.getLatestDialog();
                moving.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-12-columns-moving");
                Review0158Fixture.dismissDialog(nav);
                Review0158Fixture.byText(page.pages, "Grid view").performClick();
                Review0158Fixture.settle(nav);

                // Customise page: the side panel on the right (Movies: How To Show It).
                View customise = Review0158Fixture.byText(page.pages, "Customise page");
                assertNotNull(customise);
                customise.performClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-13-customise-movies");
                Review0158Fixture.dismissDialog(nav);

                // Long-press on a poster: its menu beside it.
                View started = Review0158Fixture.byDescription(page.pages, "Anora");
                assertNotNull("A started film's poster", started);
                started.requestFocus();
                Review0158Fixture.settle(nav);
                started.performLongClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-14-movie-long-press");
                assertNotNull("Resume for a started film", rowLabel("Resume"));
                assertNull("No More info", rowLabel("More info"));
                Review0158Fixture.dismissDialog(nav);
                View after = nav.findFocus();
                assertFalse("Focus never jumps to the top bar", after != null && after.getTag() instanceof String
                    && ((String) after.getTag()).startsWith("semantic:topnav"));
            }
            finally
            {
                page.host.pause().stop().destroy();
            }
        });
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void tvShowsCaptionsAndLongPress() throws Exception
    {
        guarded(() -> {
            Page page = open(Review0158Fixture.snapshot(), "tv");
            try
            {
                TopNavigation nav = page.nav;
                assertNotNull("Not started: seasons and episodes", Review0158Fixture.anyText(page.pages, "seasons ·"));
                assertNotNull("One season: just episodes", Review0158Fixture.anyText(page.pages, "10 episodes"));
                assertNotNull("Started: next episode and how many left",
                              Review0158Fixture.anyText(page.pages, "S2 E3 · "));
                Review0158Fixture.save(nav, "0158-lib-20-tv-shows");
                View show = Review0158Fixture.byDescription(page.pages, "Slow Horses");
                assertNotNull(show);
                show.requestFocus();
                Review0158Fixture.settle(nav);
                show.performLongClick();
                Review0158Fixture.settle(nav);
                Review0158Fixture.saveWithDialog(nav, "0158-lib-21-tv-long-press");
                assertNotNull(rowLabel("Resume S2 E3"));
                assertNotNull(rowLabel("Mark show as watched"));
                assertNotNull(rowLabel("Delete all episode files"));
                Review0158Fixture.dismissDialog(nav);
            }
            finally
            {
                page.host.pause().stop().destroy();
            }
        });
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void emptyPageHasNoToolbar() throws Exception
    {
        guarded(() -> {
            Page page = open(new Snapshot(), "movies");
            try
            {
                Review0158Fixture.settle(page.nav);
                assertNull("No toolbar on an empty page", Review0158Fixture.byText(page.pages, "Customise page"));
                Review0158Fixture.save(page.nav, "0158-lib-30-empty-movies");
            }
            finally
            {
                page.host.pause().stop().destroy();
            }
        });
    }

    /** Clicks the first row of the latest menu whose label starts with the words. */
    private static void clickRow(Dialog menu, String words)
    {
        View row = find(menu.getWindow().getDecorView(), words);
        assertNotNull("Menu row " + words, row);
        ((View) row.getParent()).performClick();
    }

    private static View rowLabel(String words)
    {
        Dialog menu = ShadowDialog.getLatestDialog();
        assertNotNull(menu);
        return find(menu.getWindow().getDecorView(), words);
    }

    private static View find(View view, String words)
    {
        if (view instanceof TextView && view.getTag() instanceof String
            && ((String) view.getTag()).startsWith("preview-label:")
            && ((TextView) view).getText().toString().startsWith(words))
        {
            return view;
        }
        if (view instanceof android.view.ViewGroup)
        {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = find(group.getChildAt(i), words);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }
}
