package com.archos.mediacenter.video.diagnostics;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryColumns;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediacenter.video.leanback.filebrowsing.PreviewBrowserSurface;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.IOException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

/** Checks artwork request correlation, cancellation and privacy when library items are rebound. */
@RunWith(RobolectricTestRunner.class) @Config(application = Application.class, sdk = 28) public class ArtworkRequestTest
{
    @Before public void initialisePicasso()
    {
        try
        {
            Picasso.get();
        }
        catch (IllegalStateException uninitialised)
        {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
    }
    @Test public void rebindTerminatesOldRequestAndDoesNotLeakSourceOrFailureMessage() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        ImageView view = new ImageView(app);
        try
        {
            RequestCreator first = mock(RequestCreator.class), second = mock(RequestCreator.class);
            ArtworkRequest.load(view, Uri.parse("https://private.example/art.jpg?token=secret"), 78123,
                                "details.extras", "video_thumbnail", first);
            ArgumentCaptor<Callback> oldCallback = ArgumentCaptor.forClass(Callback.class);
            verify(first).into(eq(view), oldCallback.capture());
            ArtworkRequest.load(view, Uri.parse("file:///private/title.jpg"), 78124, "details.people", "portrait",
                                second, true);
            ArgumentCaptor<Callback> callback = ArgumentCaptor.forClass(Callback.class);
            verify(second).into(eq(view), callback.capture());
            oldCallback.getValue().onSuccess();
            callback.getValue().onError(new IOException("secret private failure"));
            DiagnosticFlightRecorder recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            int cancelled = 0, failed = 0, late = 0;
            for (String line : recorder.snapshot(SystemClock.elapsedRealtime()).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject row = new JSONObject(line);
                long media = row.optLong("media_id");
                if (media != 78123 && media != 78124)
                    continue;
                assertFalse(line.contains("private"));
                assertFalse(line.contains("secret"));
                assertFalse(line.contains("art.jpg"));
                String event = row.optString("event");
                if (media == 78123 && event.equals("artwork_cancelled"))
                    cancelled++;
                if (media == 78123 && event.equals("artwork_ready"))
                    late++;
                if (media == 78124 && event.equals("artwork_failed"))
                {
                    failed++;
                    assertTrue(row.getBoolean("fallback_succeeded"));
                    assertEquals("local", row.getString("source"));
                }
            }
            assertEquals(1, cancelled);
            assertEquals(1, failed);
            assertEquals(0, late);
        }
        finally
        {
            ArtworkRequest.cancel(view);
            Diagnostics.setEnabled(app, false);
        }
    }
    @Test public void actualLibraryListBindingCorrelatesAndCancelsPrivateArtwork() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        PreviewLibraryColumns columns = new PreviewLibraryColumns(app, false);
        LinearLayout row = columns.newRow();
        try
        {
            Video media = mock(Video.class);
            when(media.getId()).thenReturn(78125L);
            when(media.getName()).thenReturn("private-title");
            when(media.getFilenameNonCryptic()).thenReturn("private-file.mkv");
            when(media.getFilePath()).thenReturn("/private/path.mkv");
            when(media.getPosterUri()).thenReturn(Uri.parse("file:///private/poster.jpg"));
            columns.bind(row, new PreviewLibraryLoader.Entry(media, 0, 0, ""));
            columns.clear(row);
            columns.clear(row);
            assertRoute(78125, "library.list");
        }
        finally
        {
            columns.clear(row);
            Diagnostics.setEnabled(app, false);
        }
    }
    @Test public void actualBrowserSelectionTerminatesPosterOnProviderTransition() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try
        {
            var browser = new PreviewBrowserSurface(host.get(), new LinearLayout(host.get()),
                                                    Uri.parse("file:///private/"), () -> {});
            host.get().setContentView(browser);
            Video media = mock(Video.class);
            when(media.getId()).thenReturn(78126L);
            when(media.getName()).thenReturn("private-title");
            when(media.getFilenameNonCryptic()).thenReturn("private-file.mkv");
            when(media.getPosterUri()).thenReturn(Uri.parse("file:///private/poster.jpg"));
            ReflectionHelpers.callInstanceMethod(
                browser, "focusItem", ReflectionHelpers.ClassParameter.from(Object.class, media),
                ReflectionHelpers.ClassParameter.from(Runnable.class, (Runnable)() -> {}));
            browser.providerItem("private-title", "private-details", false, () -> {});
            browser.providerItem("private-title", "private-details", false, () -> {});
            assertRoute(78126, "network.browser");
        }
        finally
        {
            host.pause().stop().destroy();
            Diagnostics.setEnabled(app, false);
        }
    }
    private void assertRoute(long media, String surface) throws Exception
    {
        DiagnosticFlightRecorder recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
        int requested = 0, cancelled = 0;
        String operation = null;
        String evidence = recorder.snapshot(SystemClock.elapsedRealtime());
        for (String line : evidence.split("\n"))
        {
            if (line.isEmpty())
                continue;
            JSONObject row = new JSONObject(line);
            if (row.optLong("media_id") != media)
                continue;
            assertFalse(line.contains("private"));
            assertEquals(surface, row.getString("surface"));
            assertEquals("local", row.getString("source"));
            if (operation == null)
                operation = row.getString("operation_id");
            assertEquals(operation, row.getString("operation_id"));
            if ("artwork_request".equals(row.optString("event")))
                requested++;
            if ("artwork_cancelled".equals(row.optString("event")))
                cancelled++;
        }
        assertNotNull(operation);
        assertEquals(1, requested);
        assertEquals(1, cancelled);
        int ended = 0;
        for (String line : evidence.split("\n"))
        {
            if (line.isEmpty())
                continue;
            JSONObject row = new JSONObject(line);
            if (operation.equals(row.optString("operation_id")) && "operation_end".equals(row.optString("event")))
                ended++;
        }
        assertEquals(1, ended);
    }
}
