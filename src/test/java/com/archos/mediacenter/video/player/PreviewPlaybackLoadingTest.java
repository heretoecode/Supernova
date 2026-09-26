package com.archos.mediacenter.video.player;

import android.net.Uri;
import com.squareup.picasso.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewPlaybackLoadingTest {
    @Test public void remoteArtworkIsStrictlyOfflineAndLocalArtworkUsesExistingFile(){
        Picasso picasso=mock(Picasso.class);RequestCreator request=mock(RequestCreator.class,RETURNS_SELF);
        Uri remote=Uri.parse("https://image.tmdb.org/t/p/w1280/backdrop.jpg");when(picasso.load(remote)).thenReturn(request);
        assertSame(request,PreviewPlaybackLoading.cachedRequest(picasso,remote));verify(request).networkPolicy(NetworkPolicy.OFFLINE);
        RequestCreator localRequest=mock(RequestCreator.class,RETURNS_SELF);Uri local=Uri.parse("file:///cached/backdrop.jpg");when(picasso.load(local)).thenReturn(localRequest);
        assertSame(localRequest,PreviewPlaybackLoading.cachedRequest(picasso,local));verify(localRequest,never()).networkPolicy(any(NetworkPolicy.class));
        assertNull(PreviewPlaybackLoading.cachedRequest(picasso,Uri.parse("smb://server/art.jpg")));assertNull(PreviewPlaybackLoading.cachedRequest(picasso,null));
    }
}
