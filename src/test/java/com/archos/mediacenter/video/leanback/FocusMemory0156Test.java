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
            java.util.List<View> targets = browser.getFocusables(View.FOCUS_FORWARD);
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
