package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.View;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.search.PreviewSearch;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** 0.158 Search before typing (page sheet SEARCH.md; `search-flat/17`): recent searches, Clear history, tips. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0158SearchTest
{
    @Before
    public void setUp()
    {
        try
        {
            Picasso.get();
        }
        catch (IllegalStateException e)
        {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .clear()
            .putString("preview_search_recent", "Slow Horses\nTidelands of the Northern Coast\nThe Bear\nChristmas")
            .commit();
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void beforeTypingAndClearHistory() throws Exception
    {
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try
        {
            PreviewSearch search = new PreviewSearch(host.get(), 0, null);
            TopNavigation nav = new TopNavigation(host.get(), search, n -> {
            }, () -> true);
            nav.selectTab(5);
            host.get().setContentView(nav);
            Review0158Fixture.settle(nav);
            assertNotNull("Recent searches as rows", Review0158Fixture.byText(search, "Slow Horses"));
            assertNotNull(Review0158Fixture.anyText(search, "What you can search"));
            Review0158Fixture.save(nav, "0158-search-01-before-typing");
            View clear = search.findViewWithTag("search.recent.clear");
            assertNotNull("Clear history", clear);
            clear.requestFocus();
            Review0158Fixture.settle(nav);
            Review0158Fixture.save(nav, "0158-search-02-clear-history-focused");
            clear.performClick();
            Review0158Fixture.settle(nav);
            assertNull("History cleared at once", Review0158Fixture.byText(search, "Slow Horses"));
            Review0158Fixture.saveWithDialog(nav, "0158-search-03-cleared-with-undo");
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }
}
