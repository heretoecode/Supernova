package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.details.PreviewArtworkPanel;
import com.archos.mediacenter.video.leanback.details.PreviewMoviePage;
import com.archos.mediacenter.video.leanback.details.VideoActionAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowDialog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 0.157.2, Details (page sheet docs/design/pages/DETAILS.md; Definition of done 3): the More menu and what its items
 * open, pressed in the real page with renders saved.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review01572DetailsTest
{
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void moreMenuAndWhereItLeads() throws Exception
    {
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
            ArrayObjectAdapter actions = new ArrayObjectAdapter();
            actions.add(new Action(VideoActionAdapter.ACTION_RESUME, "Resume"));
            actions.add(new Action(VideoActionAdapter.ACTION_MARK_AS_WATCHED, "Mark Watched"));
            actions.add(new Action(VideoActionAdapter.ACTION_UNINDEX, "Remove From Library"));
            actions.add(new Action(VideoActionAdapter.ACTION_DELETE, "Delete"));
            List<Long> pressed = new ArrayList<>();
            PreviewMoviePage page = new PreviewMoviePage(host.get(), () -> actions, a -> pressed.add(a.getId()),
                                                         () -> { }, uri -> { });
            TopNavigation nav = new TopNavigation(host.get(), page, i -> { }, page::atTop);
            nav.selectTab(1);
            host.get().setContentView(nav);
            Movie movie = new Movie(1, "smb://server/movies/The Last Crossing (2025).mkv", "The Last Crossing", 1,
                                    "A ferry captain must decide who crosses the border on the last night before "
                                        + "it closes for good.", 2025, 7.6f, "15", null, 7860000, 1200000, 0, 0,
                                    false, false, false, false, 550, 0, 3840, 2160, "Atmos", "HEVC", null, null, 0,
                                    1, 1000, 0);
            page.bind(movie);
            Review01572FilesTest.settle(nav);
            View more = page.findViewWithTag("action:More");
            assertNotNull(more);
            more.requestFocus();
            more.performClick();
            Review01572FilesTest.settle(nav);
            Review01572FilesTest.saveWithMenu(nav, "01572-details-01-more-menu");
            Dialog menu = ShadowDialog.getLatestDialog();
            List<String> labels = labels(menu);
            // The sheet's items, in its wording (D2): no "Add to Row", no Title Case items.
            for (String item : new String[] {"Restart movie", "Mark as watched", "Fix match", "Remove from library",
                                             "Subtitles", "Artwork", "Full description", "Delete file",
                                             "TMDB page QR code"})
            {
                assertTrue(item + " in " + labels, labels.contains(item));
            }
            assertFalse(labels.contains("Add to Row"));
            press(menu, labels.indexOf("Full description"));
            Review01572FilesTest.settle(nav);
            Review01572FilesTest.saveWithMenu(nav, "01572-details-02-full-description");
            ShadowDialog.getLatestDialog().dismiss();
            more.performClick();
            Review01572FilesTest.settle(nav);
            menu = ShadowDialog.getLatestDialog();
            press(menu, labels(menu).indexOf("TMDB page QR code"));
            Review01572FilesTest.settle(nav);
            Review01572FilesTest.saveWithMenu(nav, "01572-details-03-tmdb-page");
            ShadowDialog.getLatestDialog().dismiss();
            more.performClick();
            Review01572FilesTest.settle(nav);
            menu = ShadowDialog.getLatestDialog();
            press(menu, labels(menu).indexOf("Mark as watched"));
            assertTrue("Mark as watched reaches the page's action", pressed.contains(
                (long) VideoActionAdapter.ACTION_MARK_AS_WATCHED));
            PreviewArtworkPanel.show(host.get(), Collections.emptyList(), null, Collections.emptyList(), null,
                                     uri -> { }, (image, poster, done) -> done.accept(true));
            Review01572FilesTest.settle(nav);
            Review01572FilesTest.saveWithMenu(nav, "01572-details-04-artwork-panel");
            ShadowDialog.getLatestDialog().dismiss();

            // More › Subtitles (DETAILS.md tv/08): the side panel, Built In · Subtitle Files · Online.
            List<String> opened = new ArrayList<>();
            page.setSubtitleActions(() -> opened.add("files"), () -> opened.add("online"));
            more.performClick();
            Review01572FilesTest.settle(nav);
            menu = ShadowDialog.getLatestDialog();
            press(menu, labels(menu).indexOf("Subtitles"));
            Review01572FilesTest.settle(nav);
            Review01572FilesTest.saveWithMenu(nav, "01572-details-05-subtitles-panel");
            View online = ShadowDialog.getLatestDialog().getWindow().getDecorView()
                .findViewWithTag("subtitles.online");
            assertNotNull("Find subtitles online in the panel", online);
            online.performClick();
            assertTrue("Find subtitles online opens the search", opened.contains("online"));
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }

    private static List<String> labels(Dialog menu)
    {
        List<String> labels = new ArrayList<>();
        for (int index = 0; index < 40; index++)
        {
            View label = menu.getWindow().getDecorView().findViewWithTag("preview-label:" + index);
            labels.add(label instanceof TextView ? ((TextView) label).getText().toString() : "");
        }
        return labels;
    }

    private static void press(Dialog menu, int index)
    {
        assertTrue("No such item", index >= 0);
        View label = menu.getWindow().getDecorView().findViewWithTag("preview-label:" + index);
        ((View) label.getParent()).performClick();
    }
}
