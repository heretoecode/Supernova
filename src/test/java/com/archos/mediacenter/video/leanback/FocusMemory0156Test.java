package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Application;
import android.app.Dialog;
import android.view.View;
import androidx.preference.PreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;

/** 0.156 #43 "every page returns focus to where you left it" (Codex focus audit F3, F17). */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class FocusMemory0156Test
{
    @Test
    public void homeHeroKeepsItsTitleAcrossATabRoundTrip()
    {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            host.get().setContentView(pages);
            ReflectionHelpers.setField(pages, "featuredIndex", 2);
            pages.setTab(1);
            pages.setTab(0);
            assertEquals(2, (int) ReflectionHelpers.getField(pages, "featuredIndex"));
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void networkSectionReturnsToTheLastRowAfterTheTopBar()
    {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            var browser = new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());
            host.get().setContentView(browser);
            browser.networkWorkspace();
            ReflectionHelpers.callInstanceMethod(browser, "openLandingCard",
                ReflectionHelpers.ClassParameter.from(int.class, 4));
            PreviewPagesTest.layout(browser);
            // Only rows in the centre list are remembered; the side panels and top bar keep their own focus.
            android.view.ViewGroup centre = ReflectionHelpers.getField(browser, "items");
            java.util.List<View> targets = new java.util.ArrayList<>();
            for (View target : centre.getFocusables(View.FOCUS_FORWARD))
                if (target != centre && target.isShown() && target.isEnabled())
                    targets.add(target);
            org.junit.Assert.assertTrue(targets.size() > 1);
            View previous = targets.get(targets.size() - 1);
            org.junit.Assert.assertTrue(previous.requestFocus());
            browser.focusOverview();
            org.junit.Assert.assertSame(previous, browser.findFocus());
            browser.close();
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void detailsTabsReturnToTheirItemAndReadingPosition() throws Exception
    {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            var page = new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(
                host.get(), androidx.leanback.widget.ArrayObjectAdapter::new, action -> {
                }, () -> {
                }, uri -> {
                });
            host.get().setContentView(page);
            android.widget.LinearLayout extras = ReflectionHelpers.getField(page, "trailers");
            java.util.List<View> cards = ReflectionHelpers.getField(page, "extraCards");
            // The page reads these back as its own artwork cards, so the fixture must use the real card class.
            Class<?> cardClass = Class.forName("com.archos.mediacenter.video.leanback.details.PreviewLandscapeCard");
            for (int i = 0; i < 2; i++) {
                View card = (View) ReflectionHelpers.callConstructor(cardClass,
                    ReflectionHelpers.ClassParameter.from(android.content.Context.class, host.get()));
                extras.addView(card);
                cards.add(card);
            }
            show(page, "Extras", true);
            PreviewPagesTest.layout(page);
            org.junit.Assert.assertTrue(cards.get(1).requestFocus());
            show(page, "Details", false);
            show(page, "Extras", true);
            org.junit.Assert.assertSame(cards.get(1), page.findFocus());
            android.widget.ScrollView reading = ReflectionHelpers.getField(page, "reading");
            reading.getChildAt(0).setMinimumHeight(5000);
            show(page, "Details", false);
            PreviewPagesTest.layout(page);
            reading.scrollTo(0, 120);
            show(page, "Extras", false);
            show(page, "Details", false);
            assertEquals(120, reading.getScrollY());
        } finally {
            host.pause().stop().destroy();
        }
    }

    private static void show(Object page, String label, boolean enter)
    {
        ReflectionHelpers.callInstanceMethod(page, "showSection",
            ReflectionHelpers.ClassParameter.from(String.class, label),
            ReflectionHelpers.ClassParameter.from(boolean.class, enter));
    }

    @Test
    public void customiseHomeReopensOnTheRowYouLeft()
    {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreferenceManager.getDefaultSharedPreferences(host.get()).edit().clear().commit();
            PreviewHomeRows.customise(host.get(), () -> {
            });
            Dialog first = ShadowDialog.getLatestDialog();
            String second = new PreviewHomeRows(host.get()).rows.get(1).id;
            View row = first.getWindow().getDecorView().findViewWithTag(second);
            assertNotNull(row);
            row.requestFocus();
            first.dismiss();
            PreviewHomeRows.customise(host.get(), () -> {
            });
            Dialog again = ShadowDialog.getLatestDialog();
            assertEquals(second, String.valueOf(again.getCurrentFocus().getTag()));
            again.dismiss();
        } finally {
            host.pause().stop().destroy();
        }
    }
}
