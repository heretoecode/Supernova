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
    @Test public void actualLoadingViewUsesOnlyCachedProviderTaglineAndCollapsesMissingCopy()throws Exception{
        com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();
        var host=Robolectric.buildActivity(android.app.Activity.class).setup().visible();try{
            try{Picasso.get();}catch(IllegalStateException absent){Picasso.setSingletonInstance(new Picasso.Builder(host.get()).build());}
            long id=987123;com.archos.mediacenter.video.leanback.PreviewMetadataCache.load(host.get(),"tv",id,"",()->new org.json.JSONObject().put("tagline","Verified series tagline").put("overview","Never substitute this synopsis"));
            PreviewPlaybackLoading loading=new PreviewPlaybackLoading(host.get(),new android.content.Intent());host.get().setContentView(loading);
            com.archos.mediacenter.utils.videodb.VideoDbInfo info=new com.archos.mediacenter.utils.videodb.VideoDbInfo();info.isShow=true;info.isScraped=true;info.scraperTitle="Provider Series";info.scraperShowId=String.valueOf(id);info.scraperSeasonNr=2;info.scraperEpisodeNr=1;info.duration=3600000;
            loading.bind(info,"Fallback");android.widget.TextView copy=org.robolectric.util.ReflectionHelpers.getField(loading,"episode");assertEquals("Verified series tagline",copy.getText().toString());assertEquals(android.view.View.VISIBLE,copy.getVisibility());
            info.scraperShowId="987124";loading.bind(info,"Fallback");assertEquals("",copy.getText().toString());assertEquals(android.view.View.GONE,copy.getVisibility());loading.measure(android.view.View.MeasureSpec.makeMeasureSpec(960,android.view.View.MeasureSpec.EXACTLY),android.view.View.MeasureSpec.makeMeasureSpec(540,android.view.View.MeasureSpec.EXACTLY));loading.layout(0,0,960,540);assertTrue(loading.getFocusables(android.view.View.FOCUS_FORWARD).isEmpty());loading.setVisibility(android.view.View.INVISIBLE);loading.begin();assertEquals(android.view.View.VISIBLE,loading.getVisibility());assertEquals(1f,loading.getAlpha(),0);
        }finally{host.pause().stop().destroy();com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain();}
    }
    @Test public void remoteArtworkIsStrictlyOfflineAndLocalArtworkUsesExistingFile(){
        Picasso picasso=mock(Picasso.class);RequestCreator request=mock(RequestCreator.class,RETURNS_SELF);
        Uri remote=Uri.parse("https://image.tmdb.org/t/p/w1280/backdrop.jpg");when(picasso.load(remote)).thenReturn(request);
        assertSame(request,PreviewPlaybackLoading.cachedRequest(picasso,remote));verify(request).networkPolicy(NetworkPolicy.OFFLINE);
        RequestCreator localRequest=mock(RequestCreator.class,RETURNS_SELF);Uri local=Uri.parse("file:///cached/backdrop.jpg");when(picasso.load(local)).thenReturn(localRequest);
        assertSame(localRequest,PreviewPlaybackLoading.cachedRequest(picasso,local));verify(localRequest,never()).networkPolicy(any(NetworkPolicy.class));
        assertNull(PreviewPlaybackLoading.cachedRequest(picasso,Uri.parse("smb://server/art.jpg")));assertNull(PreviewPlaybackLoading.cachedRequest(picasso,null));
    }
}
