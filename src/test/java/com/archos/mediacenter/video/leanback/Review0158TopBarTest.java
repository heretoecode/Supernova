package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 0.158 top bar (RULES §2 note; mock-ups `files-review-0157-1/08` and `09` A): no wordmark, every item a word, media
 * pages open as focus rests on them, tools wait for OK, the open page keeps a soft grey box, no underlines, one font.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0158TopBarTest
{
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
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void everyTopBarActionPressed() throws Exception
    {
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try
        {
            List<Integer> opened = new ArrayList<>();
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            TopNavigation nav = new TopNavigation(host.get(), pages, index -> {
                opened.add(index);
                pages.setTab(index);
            }, pages::atTop);
            host.get().setContentView(nav);
            pages.setArtworkListener(nav::setArtwork);
            pages.setDiscovery(new PreviewDiscovery());
            pages.setSnapshot(Review0158Fixture.snapshot());
            Review0158Fixture.settle(nav);

            assertNull("No SUPERNOVA wordmark", Review0158Fixture.anyText(nav, "SUPERNOVA"));
            TextView home = nav.findViewWithTag("semantic:topnav.home");
            TextView movies = nav.findViewWithTag("semantic:topnav.movies");
            TextView files = nav.findViewWithTag("semantic:topnav.network");
            TextView search = nav.findViewWithTag("semantic:topnav.search");
            TextView settings = nav.findViewWithTag("semantic:topnav.settings");
            TextView clock = nav.findViewWithTag("preview-default-clock");
            for (TextView tool : new TextView[] {files, search, settings})
            {
                assertFalse("Tools are words: " + tool.getText(), tool.getText().toString().isEmpty());
                for (Drawable icon : tool.getCompoundDrawables())
                {
                    assertNull("No icon on " + tool.getText(), icon);
                }
            }
            assertEquals("Search", search.getText().toString());
            assertEquals("Settings", settings.getText().toString());
            assertEquals("One font for words and clock", home.getTypeface(), clock.getTypeface());
            assertEquals("One size for words and clock", home.getTextSize(), clock.getTextSize(), 0.01f);
            assertTrue("The bar's first word starts at the bar's edge",
                       home.getLeft() < Review0158Fixture.WIDTH / 20);

            home.requestFocus();
            Review0158Fixture.settle(nav);
            assertFocusBox(home);
            Review0158Fixture.save(nav, "0158-top-01-home-focused");

            // Right onto Movies: it opens as focus rests there, without OK.
            Review0158Fixture.press(host, KeyEvent.KEYCODE_DPAD_RIGHT, nav);
            assertTrue(movies.isFocused());
            assertTrue("Movies opened on focus", opened.contains(1));
            assertFocusBox(movies);
            assertNull("Home has no box once Movies is open", home.getBackground());
            Review0158Fixture.save(nav, "0158-top-02-movies-open-on-focus");

            // Down into the page: Movies keeps a soft grey box with a faded word.
            Review0158Fixture.press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            assertFalse("Focus left the bar", movies.isFocused());
            assertOpenBox(movies);
            Review0158Fixture.save(nav, "0158-top-03-movies-open-focus-below");

            // Up, then along to Files quickly: nothing passed over opens, Files waits for OK.
            movies.requestFocus();
            Review0158Fixture.settle(nav);
            opened.clear();
            for (int i = 0; i < 3; i++)
            {
                host.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                host.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT));
            }
            Review0158Fixture.settle(nav);
            assertTrue("Focus on Files", files.isFocused());
            assertTrue("Passing over pages opens none of them, and Files waits for OK: " + opened,
                       opened.isEmpty());
            assertFocusBox(files);
            assertOpenBox(movies);
            Review0158Fixture.save(nav, "0158-top-04-files-focused-movies-open");

            // Search and Settings: words, the white box when focused.
            search.requestFocus();
            Review0158Fixture.settle(nav);
            assertFocusBox(search);
            settings.requestFocus();
            Review0158Fixture.settle(nav);
            assertFocusBox(settings);
            assertTrue("Settings waits for OK", opened.isEmpty());
            Review0158Fixture.save(nav, "0158-top-05-settings-focused");

            // OK on Files opens it.
            files.requestFocus();
            Review0158Fixture.settle(nav);
            files.performClick();
            Review0158Fixture.settle(nav);
            assertTrue("OK on Files opens Files", opened.contains(3));
            Review0158Fixture.save(nav, "0158-top-06-files-open");
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }

    private static void assertFocusBox(TextView tab)
    {
        Drawable background = tab.getBackground();
        assertTrue("White box on " + tab.getText(), background instanceof GradientDrawable);
        assertEquals(SupernovaStyle.FOCUS_FILL, ((GradientDrawable) background).getColor().getDefaultColor());
        assertEquals(SupernovaStyle.FOCUS_TEXT, tab.getCurrentTextColor());
    }

    private static void assertOpenBox(TextView tab)
    {
        Drawable background = tab.getBackground();
        assertTrue("Soft grey box on the open page " + tab.getText(), background instanceof GradientDrawable);
        assertEquals(TopNavigation.OPEN_PAGE_BOX, ((GradientDrawable) background).getColor().getDefaultColor());
        assertEquals(TopNavigation.OPEN_PAGE_INK, tab.getCurrentTextColor());
    }
}
