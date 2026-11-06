package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.view.KeyEvent;
import android.view.View;
import com.archos.mediascraper.MovieTags;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** 0.156 #40: Fix match is a side panel; the keyboard folds away in the results and returns on Up. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class PreviewMatchPanelTest
{
    @Before
    public void isolatePreviewTransport()
    {
        PreviewAsyncFixtures.offlineTransport();
    }

    @After
    public void drainPreviewWorkers() throws Exception
    {
        PreviewAsyncFixtures.drain();
    }

    @Test
    public void keyboardFoldsAwayInResultsAndReturnsOnUp()
    {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        PreviewMatchSearch page = new PreviewMatchSearch(host, "Pressure", q -> {
        }, t -> {
        });
        host.setContentView(page);
        page.setSubject("Pressure.2026.2160p.mkv");
        assertNotNull(PreviewPagesTest.findText(page, "Fix match · File: Pressure.2026.2160p.mkv"));
        page.setResults(Arrays.asList(movie("Pressure", 2026), movie("Pressure", 2015)));
        PreviewPagesTest.layout(page);
        View keyboard = page.findViewWithTag("semantic:keyboard:T");
        View first = page.findViewWithTag("semantic:match.result:0");
        assertTrue(first.requestFocus());
        PreviewPagesTest.layout(page);
        assertEquals(View.GONE, ((View) keyboardRoot(keyboard)).getVisibility());
        first.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
        PreviewPagesTest.layout(page);
        assertEquals(View.VISIBLE, ((View) keyboardRoot(keyboard)).getVisibility());
        assertTrue(page.findFocus() != null && page.findFocus() != first);
    }

    @Test
    public void resultsShowYearAndKindAndMarkTheCurrentMatch()
    {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        PreviewMatchSearch page = new PreviewMatchSearch(host, "Pressure", q -> {
        }, t -> {
        });
        host.setContentView(page);
        MovieTags current = movie("Pressure", 2026);
        current.setOnlineId(1234);
        MovieTags other = movie("Pressure", 2015);
        other.setOnlineId(99);
        page.setCurrentOnlineId(1234);
        page.setResults(Arrays.asList(current, other));
        PreviewPagesTest.layout(page);
        assertNotNull(PreviewPagesTest.findText(page, "2026 · Film"));
        assertNotNull(PreviewPagesTest.findText(page, "Matches on TMDB"));
        View first = page.findViewWithTag("semantic:match.result:0");
        View second = page.findViewWithTag("semantic:match.result:1");
        assertNotNull(first.findViewWithTag("semantic:match.current"));
        assertEquals(null, second.findViewWithTag("semantic:match.current"));
    }

    @Test
    public void sidePanelIsNarrowAndOnTheRight()
    {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        PreviewMatchSearch.asSidePanel(host);
        assertEquals(PreviewMatchSearch.PANEL_WIDTH_DP, host.getWindow().getAttributes().width);
        assertEquals(android.view.Gravity.END | android.view.Gravity.FILL_VERTICAL,
                     host.getWindow().getAttributes().gravity);
    }

    private static Object keyboardRoot(View key)
    {
        for (View at = key; at != null; at = at.getParent() instanceof View ? (View) at.getParent() : null)
            if (at instanceof PreviewKeyboard)
                return at;
        throw new AssertionError("Key outside the keyboard");
    }

    private static MovieTags movie(String title, int year)
    {
        MovieTags tags = new MovieTags();
        tags.setTitle(title);
        tags.setYear(year);
        return tags;
    }
}
