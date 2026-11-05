package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.squareup.picasso.Picasso;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import java.time.Duration;

/** 0.154 walk-through fixes, checked on the real Home page. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova154UiTest {
    @Before
    public void setup() {
        PreviewAsyncFixtures.offlineTransport();
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
        try {
            Picasso.get();
        } catch (IllegalStateException ignored) {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
    }

    @After
    public void drain() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    static Snapshot source(int count) {
        Snapshot s = new Snapshot();
        for (int i = 1; i <= count; i++) {
            Movie m = new Movie(i, "/fixture/" + i, "Film " + i, i, "Fixture synopsis", 2026, 7, "", null, 100000, 0,
                                0, 0, false, false, false, false, i, i, 1920, 1080, "AAC", null, null, null, 0, 1,
                                1000, 0);
            Entry e = new Entry(m, i, 0, "Drama");
            e.heroEligible = true;
            s.movies.add(e);
            s.technical.add(e);
            s.recent.add(e);
            s.featured.add(e);
        }
        return s;
    }

    static void settle(View root) {
        for (int i = 0; i < 6; i++) {
            PreviewPagesTest.layout(root);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(50));
        }
    }

    @Test
    public void focusedHeroMoreInfoIconIsBlackOnTheWhiteBox() throws Exception {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages pages = new PreviewPages(host.get(), (h, item) -> {});
            host.get().setContentView(pages);
            pages.setSnapshot(source(4));
            settle(pages);
            TextView info = pages.findViewWithTag("hero:info");
            assertNotNull(info);
            assertTrue(info.requestFocus());
            settle(pages);
            PreviewPagesTest.capture(pages, "0154-hero-more-info-focused");
            assertEquals(SupernovaStyle.FOCUS_TEXT, info.getCurrentTextColor());
            Drawable icon = info.getCompoundDrawablesRelative()[0];
            if (icon == null)
                icon = info.getCompoundDrawables()[0];
            assertNotNull("More Info has its icon", icon);
            assertTrue("icon follows the button state, got " + icon.getClass().getName(),
                       icon instanceof SupernovaStyle.StateIcon);
            assertEquals("icon black on the white box", SupernovaStyle.FOCUS_TEXT,
                         ((SupernovaStyle.StateIcon) icon).colour());
        } finally {
            host.pause().stop().destroy();
        }
    }
}
