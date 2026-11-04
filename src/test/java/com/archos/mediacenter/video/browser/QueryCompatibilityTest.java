package com.archos.mediacenter.video.browser;

import android.app.Application;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.archos.mediacenter.video.info.VideoInfoContract;
import com.archos.mediacenter.video.leanback.details.VideoDetailsActivity;
import com.archos.mediacenter.video.leanback.search.SearchIntentContract;
import com.archos.mediacenter.video.leanback.search.VideoSearchActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class QueryCompatibilityTest {
    @Test
    public void searchAndViewForwardAllLaunchDataAndLeaveNoCompatibilityHostOnBackStack() {
        Context context = RuntimeEnvironment.getApplication();
        Uri uri = Uri.parse("content://com.archos.media/external/video/media/42");
        for (String action : new String[] {Intent.ACTION_SEARCH, Intent.ACTION_VIEW}) {
            Intent origin = new Intent(action).setDataAndType(uri, "video/mp4")
                    .putExtra(SearchManager.QUERY, "fixture").putExtra("custom", 17)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            origin.setClipData(ClipData.newRawUri("video", uri));
            try (var host = Robolectric.buildActivity(QueryBrowserActivityVideo.class, origin).create()) {
                Intent routed = shadowOf(host.get()).getNextStartedActivity();
                assertEquals(VideoSearchActivity.class.getName(), routed.getComponent().getClassName());
                assertEquals(action, routed.getAction());
                assertEquals(uri, routed.getData());
                assertEquals("video/mp4", routed.getType());
                assertEquals("fixture", SearchIntentContract.query(routed));
                assertEquals(17, routed.getIntExtra("custom", -1));
                assertEquals(origin.getFlags(), routed.getFlags());
                assertEquals(uri, routed.getClipData().getItemAt(0).getUri());
                assertTrue(host.get().isFinishing());
            }
            Intent details = SearchIntentContract.detailsIntent(context, origin);
            assertEquals(VideoDetailsActivity.class.getName(), details.getComponent().getClassName());
            assertEquals(42L, details.getLongExtra(VideoInfoContract.EXTRA_VIDEO_ID, -1));
            assertEquals(uri, details.getData());
            assertEquals(origin.getFlags(), details.getFlags());
            assertEquals(uri, details.getClipData().getItemAt(0).getUri());
            assertNull(origin.getComponent());
        }
    }

    @Test
    public void emptyQueryAndNonIndexedViewDoNotRequireAnIntegerMediaId() {
        Context context = RuntimeEnvironment.getApplication();
        assertEquals("", SearchIntentContract.query(new Intent(Intent.ACTION_SEARCH)));
        assertEquals("", SearchIntentContract.query(new Intent().putExtra(SearchManager.QUERY, "")));
        for (String uri : new String[] {"smb://server/share/movie.mkv", "content://other.provider/movie/abc"}) {
            Intent details = SearchIntentContract.detailsIntent(context,
                    new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            assertEquals(Uri.parse(uri), details.getData());
            assertFalse(details.hasExtra(VideoInfoContract.EXTRA_VIDEO_ID));
        }
    }
}
