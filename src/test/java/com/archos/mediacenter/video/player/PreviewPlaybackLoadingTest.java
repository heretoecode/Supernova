package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.leanback.PreviewAsyncFixtures;
import com.archos.mediacenter.video.leanback.PreviewMetadataCache;
import com.squareup.picasso.*;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewPlaybackLoadingTest {
    @Test
    public void actualLoadingViewUsesOnlyCachedProviderTaglineAndCollapsesMissingCopy() throws Exception {
        PreviewAsyncFixtures.offlineTransport();
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            try {
                Picasso.get();
            } catch (IllegalStateException absent) {
                Picasso.setSingletonInstance(new Picasso.Builder(host.get()).build());
            }
            long id = 987123;
            PreviewMetadataCache.load(host.get(), "tv", id, "",
                ()
                    -> new JSONObject()
                        .put("tagline", "Verified series tagline")
                        .put("overview", "Never substitute this synopsis"));
            PreviewPlaybackLoading loading = new PreviewPlaybackLoading(host.get(), new Intent());
            host.get().setContentView(loading);
            VideoDbInfo info =
                new VideoDbInfo();
            info.isShow = true;
            info.isScraped = true;
            info.scraperTitle = "Provider Series";
            info.scraperShowId = String.valueOf(id);
            info.scraperSeasonNr = 2;
            info.scraperEpisodeNr = 1;
            info.duration = 3600000;
            loading.bind(info, "Fallback");
            TextView copy = ReflectionHelpers.getField(loading, "episode");
            assertEquals("Verified series tagline", copy.getText().toString());
            assertEquals(View.VISIBLE, copy.getVisibility());
            info.scraperShowId = "987124";
            loading.bind(info, "Fallback");
            assertEquals("", copy.getText().toString());
            assertEquals(View.GONE, copy.getVisibility());
            loading.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
            loading.layout(0, 0, 960, 540);
            assertTrue(loading.getFocusables(View.FOCUS_FORWARD).isEmpty());
            loading.setVisibility(View.INVISIBLE);
            loading.begin();
            assertEquals(View.VISIBLE, loading.getVisibility());
            assertEquals(1f, loading.getAlpha(), 0);
        } finally {
            host.pause().stop().destroy();
            PreviewAsyncFixtures.drain();
        }
    }

    /** Owner decision 2 Nov 2026: no tint/gradient layer may sit over the preparing-playback artwork. */
    @Test
    public void artworkIsNotCoveredByAnyShadeLayer() {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            PreviewPlaybackLoading loading = new PreviewPlaybackLoading(host.get(), new Intent());
            assertEquals("only artwork and the text block", 2, loading.getChildCount());
            assertTrue(loading.getChildAt(0) instanceof ImageView);
            assertTrue(loading.getChildAt(1) instanceof LinearLayout);
            ImageView artwork = (ImageView) loading.getChildAt(0);
            assertNull("artwork must not be tinted", artwork.getColorFilter());
            assertEquals(1f, artwork.getAlpha(), 0f);
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void remoteArtworkIsStrictlyOfflineAndLocalArtworkUsesExistingFile() {
        Picasso picasso = mock(Picasso.class);
        RequestCreator request = mock(RequestCreator.class, RETURNS_SELF);
        Uri remote = Uri.parse("https://image.tmdb.org/t/p/w1280/backdrop.jpg");
        when(picasso.load(remote)).thenReturn(request);
        assertSame(request, PreviewPlaybackLoading.cachedRequest(picasso, remote));
        verify(request).networkPolicy(NetworkPolicy.OFFLINE);
        RequestCreator localRequest = mock(RequestCreator.class, RETURNS_SELF);
        Uri local = Uri.parse("file:///cached/backdrop.jpg");
        when(picasso.load(local)).thenReturn(localRequest);
        assertSame(localRequest, PreviewPlaybackLoading.cachedRequest(picasso, local));
        verify(localRequest, never()).networkPolicy(any(NetworkPolicy.class));
        assertNull(PreviewPlaybackLoading.cachedRequest(picasso, Uri.parse("smb://server/art.jpg")));
        assertNull(PreviewPlaybackLoading.cachedRequest(picasso, null));
    }
}
