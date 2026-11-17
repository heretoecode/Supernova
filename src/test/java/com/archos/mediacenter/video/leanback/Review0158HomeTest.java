package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import androidx.preference.PreferenceManager;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 0.158 Home (page sheet docs/design/pages/HOME.md): row cards with the caption box under the picture, Move and
 * Hide beside the heading, Customise Home and a row's settings on top of it, each pressed and rendered.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0158HomeTest
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
            .putBoolean("supernova_history_migrated", true)
            .putBoolean("supernova_onboarding_complete", true)
            .putBoolean("supernova_welcome_dismissed", true)
            .commit();
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void everyHomeActionPressed() throws Exception
    {
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try
        {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            TopNavigation nav = new TopNavigation(host.get(), pages, pages::setTab, pages::atTop);
            host.get().setContentView(nav);
            pages.setDiscovery(new PreviewDiscovery());
            pages.setSnapshot(Review0158Fixture.snapshot());
            Review0158Fixture.settle(nav);
            Review0158Fixture.save(nav, "0158-home-01-rest");

            // A row's first card, then Up to its heading: Move and Hide appear beside it.
            View card = Review0158Fixture.byDescription(pages, "Slow Horses");
            assertNotNull("Continue Watching card", card);
            card.requestFocus();
            Review0158Fixture.settle(nav);
            assertNotNull("Caption under the card: S2 E3 · … left",
                          Review0158Fixture.anyText(pages, "S2 E3 · "));
            Review0158Fixture.save(nav, "0158-home-02-card-focused");
            View move = pages.findViewWithTag("home.row.move");
            assertNotNull("Move beside the heading", move);
            move.requestFocus();
            Review0158Fixture.settle(nav);
            Review0158Fixture.save(nav, "0158-home-03-heading-move-hide");
            move.performClick();
            Review0158Fixture.settle(nav);
            Review0158Fixture.press(host, KeyEvent.KEYCODE_DPAD_DOWN, nav);
            Review0158Fixture.save(nav, "0158-home-04-moving");
            Review0158Fixture.press(host, KeyEvent.KEYCODE_DPAD_CENTER, nav);
            View focus = nav.findFocus();
            assertFalse("Focus stays on the row, never the top bar", focus != null
                && focus.getTag() instanceof String && ((String) focus.getTag()).startsWith("semantic:topnav"));

            // Customise Home, Built-In Rows, Add a row on top.
            PreviewHomeRows.customise(host.get(), () -> {
            });
            Review0158Fixture.settle(nav);
            Review0158Fixture.saveWithDialog(nav, "0158-home-05-customise-home");
            Dialog panel = ShadowDialog.getLatestDialog();
            View add = panel.getWindow().getDecorView().findViewWithTag("home.customise.add");
            assertNotNull("Add a row", add);
            add.performClick();
            Review0158Fixture.settle(nav);
            Review0158Fixture.saveWithDialog(nav, "0158-home-06-add-a-row");
            Dialog settings = ShadowDialog.getLatestDialog();
            assertTrue(settings.isShowing());
            assertNotNull(settings.getWindow().getDecorView().findViewWithTag("home.row.maximum"));
            View icon = settings.getWindow().getDecorView().findViewWithTag("home.row.icon");
            icon.performClick();
            Review0158Fixture.settle(nav);
            Review0158Fixture.saveWithDialog(nav, "0158-home-07-icon-picker");
            Review0158Fixture.dismissDialog(nav);
            settings.dismiss();
            Review0158Fixture.dismissDialog(nav);
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }
}
