package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.view.*;
import android.widget.TextView;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.squareup.picasso.Picasso;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class LibraryStatisticsLayoutTest {
    @Before
    public void setup() {
        PreviewAsyncFixtures.offlineTransport();
        try {
            Picasso.get();
        } catch (IllegalStateException absent) {
            Picasso.setSingletonInstance(
                new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
    }

    @After
    public void drain() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    @Test
    public void fullHdPanelAlignsToLibraryAndHasEqualReferenceGaps() throws Exception {
        verify();
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land-xhdpi")
    public void hdPanelFitsAndKeepsFocusOnControls() throws Exception {
        verify();
    }

    @Test
    @Config(qualifiers = "w960dp-h540dp-land-xxxhdpi")
    public void ultraHdPanelFitsAndKeepsFocusOnControls() throws Exception {
        verify();
    }

    private void assertWholeMetricText(View view) {
        if (view instanceof TextView) {
            TextView text = (TextView)view;
            assertNotNull(text.getText().toString(), text.getLayout());
            for (int line = 0; line < text.getLineCount(); line++) {
                assertEquals("Complete metric: " + text.getText(), 0, text.getLayout().getEllipsisCount(line));
                assertTrue("Metric width: " + text.getText(),
                           text.getLayout().getLineWidth(line) <=
                               text.getWidth() - text.getCompoundPaddingLeft() - text.getCompoundPaddingRight() + 1);
            }
            assertTrue("Metric height: " + text.getText(),
                       text.getLayout().getLineBottom(text.getLineCount() - 1) <= text.getHeight());
        }
        if (view instanceof ViewGroup)
            for (int n = 0; n < ((ViewGroup)view).getChildCount(); n++)
                assertWholeMetricText(((ViewGroup)view).getChildAt(n));
    }

    private void assertValuesCentred(ViewGroup panel) {
        for (int r = 0; r < panel.getChildCount(); r++)
            if (panel.getChildAt(r) instanceof ViewGroup) {
                ViewGroup row = (ViewGroup)panel.getChildAt(r);
                for (int c = 0; c < row.getChildCount(); c++)
                    if (row.getChildAt(c) instanceof ViewGroup) {
                        ViewGroup cell = (ViewGroup)row.getChildAt(c);
                        TextView value = (TextView)cell.getChildAt(1);
                        assertEquals(Gravity.CENTER_VERTICAL, value.getGravity() & Gravity.VERTICAL_GRAVITY_MASK);
                        Paint.FontMetrics font = value.getPaint().getFontMetrics();
                        float middle = value.getBaseline() + (font.ascent + font.descent) / 2;
                        assertEquals(value.getText().toString(), value.getHeight() / 2f, middle, 2f);
                    }
            }
    }

    @Test
    public void televisionStatisticsRemainVisibleAfterNavigationAndFiltersFocus() throws Exception {
        verify(2);
    }

    private void verify() throws Exception {
        verify(1);
    }

    private void verify(int tab) throws Exception {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {});
            TopNavigation nav = new TopNavigation(host.get(), pages, pages::setTab, pages::atTop);
            host.get().setContentView(nav);
            Snapshot snapshot = new Snapshot();
            for (int i = 1; i <= 12; i++) {
                Movie movie = new Movie(i, "/fixture/film" + i, "Film " + i, i, "Fixture synopsis", 2026, 7, "", null,
                                        100000, i == 1 ? 10000 : 0, 0, 0, false, false, false, false, i, i, 1920, 1080,
                                        "AAC", null, null, null, 0, 1, 1000, 0);
                Entry entry = new Entry(movie, System.currentTimeMillis() / 1000, 0, "Drama");
                snapshot.movies.add(entry);
                snapshot.technical.add(entry);
            }
            pages.setSnapshot(snapshot);
            pages.setTab(tab);
            nav.selectTab(tab);
            var config = nav.getResources().getConfiguration();
            float density = nav.getResources().getDisplayMetrics().density;
            int width = Math.round(config.screenWidthDp * density), height =
                                                                        Math.round(config.screenHeightDp * density);
            for (int n = 0; n < 3; n++) {
                nav.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
                nav.layout(0, 0, width, height);
                Shadows.shadowOf(Looper.getMainLooper()).idle();
            }
            View panel = nav.findViewWithTag("library.statistics"), filters = nav.findViewWithTag("control:0");
            assertNotNull(panel);
            assertNotNull(filters);
            Rect panelBounds = new Rect(), controlBounds = new Rect();
            assertTrue(panel.getGlobalVisibleRect(panelBounds));
            assertTrue(filters.getGlobalVisibleRect(controlBounds));
            int gap = Math.round(width * 16f / 1920);
            assertEquals(
                "Top reference gap", gap,
                panelBounds.top -
                    nav.getChildAt(0)
                        .getBottom()); /* Owner 2 Nov 2026: the toolbar sits midway between the statistics and the separator (was a fixed 16px reference gap). */
            View divider = nav.findViewWithTag("library.toolbar.divider");
            assertNotNull(divider);
            Rect dividerBounds = new Rect();
            divider.getGlobalVisibleRect(dividerBounds);
            int above = controlBounds.top - panelBounds.bottom, below = dividerBounds.top - controlBounds.bottom;
            assertTrue("Toolbar centred: " + above + "px above vs " + below + "px below",
                       above >= gap && Math.abs(above - below) <= Math.round(4 * density));
            assertFalse(panel.isFocusable());
            assertWholeMetricText(panel);
            assertValuesCentred((ViewGroup)panel);
            assertTrue(filters.requestFocus());
            assertSame(filters, nav.findFocus());
            List<View> focusChanges = new ArrayList<>();
            nav.getViewTreeObserver().addOnGlobalFocusChangeListener((old, next) -> focusChanges.add(next));
            pages.setSnapshot(snapshot);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertTrue("No-op refresh keeps the actual Filters view", filters.hasFocus());
            assertTrue("No transient page focus during no-op refresh", focusChanges.isEmpty());
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(100));
            assertTrue(panel.getGlobalVisibleRect(panelBounds));
            assertEquals("Statistics stay below navigation after focus", gap,
                         panelBounds.top - nav.getChildAt(0).getBottom());
            PreviewPagesTest.capture(nav, "0140-library-statistics-" + width);
        } finally {
            host.pause().stop().destroy();
        }
    }
}
