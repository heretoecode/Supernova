package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.leanback.search.PreviewSearch;
import com.archos.mediacenter.video.leanback.search.PreviewSearchTestAccess;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
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
import org.robolectric.shadows.ShadowDialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Search to its page sheet (SEARCH.md, `search-v6/tv/01`–`11`): the landing with and without history, Right from the
 * keyboard, OK on a genre, results with the progress badge and the Resume pill, the streaming loading row, the
 * nothing-found panel, the long-press menu, Voice's "Listening…". Every action pressed and rendered.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0158cSearchTest
{
    static final String[][] GENRES = {
        {"Drama", "42", ""}, {"Crime", "31", ""}, {"Science Fiction", "24", ""}, {"Documentary", "19", ""},
        {"Comedy", "15", ""}, {"Thriller", "12", ""}, {"Action & Adventure", "9", ""}, {"Animation", "4", ""}};
    static final String[][] PEOPLE = {
        {"Mara Ellison", "6", ""}, {"Avery Lindqvist", "5", ""}, {"Sam Okoro", "5", ""},
        {"Jo Hart", "4", ""}, {"Priya Raman-Castellanos", "4", ""}, {"Theo", "3", ""}};

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
            .putString("preview_search_recent", "Tidelands of the Northern Coast\nSlow Burn\nThe Long Night\nOld")
            .commit();
    }

    private static Movie movie(long id, String name, int year, int resume, boolean watched)
    {
        Movie movie = new Movie(id, "/storage/Films/" + name + ".mkv", name, id, "", year, 0, "", null, 6720000,
                                resume, 0, 0, watched, false, false, false, id, 0, 1920, 1080, null, null, null,
                                null, 0, 1, 1000, 0);
        return movie;
    }

    private static Episode episode(long id, String show, int season, int number, boolean watched)
    {
        return new Episode(id, 7, season, number, "The Lantern Keeper", 0, 0, "", "", show,
                           "/storage/TV/" + show + "/ep.mkv", null, null, 3120000, 0, 0, 0, watched, false, false,
                           false, 1, 100, 1920, 1080, null, null, null, null, 0, 1, 1000);
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void everyActionOnTheSheet() throws Exception
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
            PreviewSearchTestAccess.landing(search, GENRES, PEOPLE);
            Review0158Fixture.settle(nav);

            // tv/09: with history, recent searches (three), one row of genres, the six people.
            assertNotNull(Review0158Fixture.byText(search, "Tidelands of the Northern Coast"));
            assertNull("Only the last three", Review0158Fixture.byText(search, "Old"));
            assertNotNull(Review0158Fixture.anyText(search, "Genres in your library"));
            assertNotNull(Review0158Fixture.anyText(search, "People in your library"));
            assertNotNull(search.findViewWithTag("search.genre.Documentary"));
            assertNull("One row of genres with history", search.findViewWithTag("search.genre.Comedy"));
            assertNotNull(search.findViewWithTag("search.person.Theo"));
            assertNotNull(Review0158Fixture.anyText(search, "42 titles"));
            assertNotNull(Review0158Fixture.anyText(search, "What you can search"));
            Review0158Fixture.save(nav, "0158c-search-01-landing-history");

            // Right from the keyboard's last column: the first item on the right (a recent search).
            search.focusQuery();
            Review0158Fixture.settle(nav);
            View key = search.findFocus();
            assertNotNull(key);
            for (int i = 0; i < 12 && !search.findViewWithTag("search.recent.Tidelands of the Northern Coast")
                                            .hasFocus(); i++)
            {
                Review0158Fixture.press(host, KeyEvent.KEYCODE_DPAD_RIGHT, nav);
            }
            assertTrue("Right reaches the first recent search",
                       search.findViewWithTag("search.recent.Tidelands of the Northern Coast").hasFocus());
            Review0158Fixture.save(nav, "0158c-search-02-right-to-recent");

            // Clear history: cleared at once, two rows of genres, the Undo notice (tv/05).
            View clear = search.findViewWithTag("search.recent.clear");
            clear.performClick();
            Review0158Fixture.settle(nav);
            assertNotNull("Two rows of genres without history", search.findViewWithTag("search.genre.Comedy"));
            Review0158Fixture.save(nav, "0158c-search-03-landing-no-history");

            // A genre card focused: it grows with the ring, never a box.
            View drama = search.findViewWithTag("search.genre.Drama");
            assertTrue(drama.requestFocus());
            Review0158Fixture.settle(nav);
            Review0158Fixture.save(nav, "0158c-search-04-genre-focused");

            // OK on a genre: fills the field as if typed (tv/11).
            drama.performClick();
            Review0158Fixture.settle(nav);
            EditText field = field(search);
            assertEquals("Drama", field.getText().toString());

            // Results: a resumed film (badge, Resume pill), a watched episode (tick, no pill), a show, a person match.
            Tvshow show = new Tvshow(7, "Tidelands of the Northern Coast", null, 4, 40, 12, "/storage/TV/Tidelands");
            Movie resumed = movie(11, "The Glass Orchard", 2025, 2400000, false);
            Movie fresh = movie(12, "Harbour Lights", 2019, 0, false);
            Episode seen = episode(21, "Tidelands of the Northern Coast", 2, 3, true);
            List<Object> media = Arrays.asList(show, resumed, fresh, seen);
            PreviewSearchTestAccess.results(search, "tide", media, Arrays.asList("", "", "Mara Ellison · director"));
            Review0158Fixture.settle(nav);
            assertNotNull("Year first on the second line", Review0158Fixture.anyText(search, "2025 · Movie · 1 h 52 min"));
            assertNotNull(Review0158Fixture.anyText(search, "▶ Resume"));
            assertNull("No Watched pills", Review0158Fixture.anyText(search, "✓ Watched"));
            assertNull("No Unwatched pills", Review0158Fixture.anyText(search, "Unwatched"));
            assertTrue("The resumed film's thumbnail has the badge", badgeShows(search, "The Glass Orchard"));
            assertFalse("A film not started has none", badgeShows(search, "Harbour Lights"));
            Review0158Fixture.save(nav, "0158c-search-05-results-badges");

            // Still fetching (tv/04): the streaming heading and the loading row.
            PreviewSearchTestAccess.loading(search, PreviewSearchTestAccess.heading("The Glass Orchard", false));
            Review0158Fixture.settle(nav);
            assertNotNull(Review0158Fixture.anyText(search, "Looking on your services"));
            Review0158Fixture.save(nav, "0158c-search-06-streaming-loading");

            // Long-press on a library result: the library long-press menu beside the row (tv/03).
            View row = rowFor(search, "The Glass Orchard");
            assertTrue(row.requestFocus());
            row.performLongClick();
            Review0158Fixture.settle(nav);
            assertNotNull(menuLabel("Resume · "));
            assertNotNull(menuLabel("Play from the start"));
            assertNotNull(menuLabel("Mark as watched"));
            assertNotNull(menuLabel("Fix match"));
            assertNotNull(menuLabel("Remove from library"));
            assertNotNull(menuLabel("Delete file"));
            assertNull("No More info", menuLabel("More info"));
            Review0158Fixture.saveWithDialog(nav, "0158c-search-07-long-press-menu");
            Review0158Fixture.dismissDialog(nav);

            // Nothing in the library (tv/06): centred panel, focus stays on the keyboard.
            search.focusQuery();
            PreviewSearchTestAccess.results(search, "terminus", Arrays.asList(), null);
            Review0158Fixture.settle(nav);
            View nothing = search.findViewWithTag("search.nothing");
            assertEquals(View.VISIBLE, nothing.getVisibility());
            assertNotNull(Review0158Fixture.anyText(search, "Nothing in your library matches “terminus”"));
            assertTrue("Focus stays on the keyboard", search.findFocus() != null
                && String.valueOf(search.findFocus().getTag()).contains("keyboard"));
            Review0158Fixture.save(nav, "0158c-search-08-nothing-found");

            // Voice (tv/07): "Listening…" in the field with the microphone and a soft ring.
            PreviewSearchTestAccess.listening(search, true);
            Review0158Fixture.settle(nav);
            assertEquals("Listening…", field.getHint().toString());
            assertNotNull(search.findViewWithTag("search.field.listening"));
            Review0158Fixture.save(nav, "0158c-search-09-voice-listening");
            PreviewSearchTestAccess.listening(search, false);
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void wordsFromTheSheet()
    {
        String[] on = PreviewSearchTestAccess.nothingWords("terminus", true);
        assertEquals("Nothing in your library matches “terminus”", on[0]);
        assertEquals("Search looks only in your library.\nTitles you can stream show up under what it finds.", on[1]);
        assertEquals("Search looks only in your library.", PreviewSearchTestAccess.nothingWords("x", false)[1]);
        assertEquals("Stream more from the world of Tidelands", PreviewSearchTestAccess.heading("Tidelands", false));
        assertEquals("Stream more related to Our Blue Planet II",
                     PreviewSearchTestAccess.heading("Our Blue Planet II", true));
    }

    private static EditText field(View root)
    {
        if (root instanceof EditText)
        {
            return (EditText) root;
        }
        if (root instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++)
            {
                EditText found = field(((ViewGroup) root).getChildAt(i));
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }

    private static View rowFor(PreviewSearch search, String title)
    {
        View text = textStarting(search, title);
        assertNotNull(title, text);
        View row = text;
        while (row != null && !(row.getParent() instanceof androidx.recyclerview.widget.RecyclerView))
        {
            row = (View) row.getParent();
        }
        assertNotNull(row);
        return row;
    }

    private static View textStarting(View view, String words)
    {
        if (view instanceof TextView && view.isShown() && ((TextView) view).getText().toString().startsWith(words))
        {
            return view;
        }
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                View found = textStarting(((ViewGroup) view).getChildAt(i), words);
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean badgeShows(PreviewSearch search, String title)
    {
        ViewGroup row = (ViewGroup) rowFor(search, title);
        FrameLayout thumb = (FrameLayout) row.getChildAt(0);
        View badge = thumb.findViewWithTag("progress.badge");
        return badge != null && badge.getVisibility() == View.VISIBLE;
    }

    private static View menuLabel(String words)
    {
        Dialog menu = ShadowDialog.getLatestDialog();
        assertNotNull(menu);
        return find(menu.getWindow().getDecorView(), words);
    }

    private static View find(View view, String words)
    {
        if (view instanceof TextView && ((TextView) view).getText().toString().startsWith(words))
        {
            return view;
        }
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                View found = find(((ViewGroup) view).getChildAt(i), words);
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }
}
