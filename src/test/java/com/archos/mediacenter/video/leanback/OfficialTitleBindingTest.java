package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.graphics.*;
import android.widget.TextView;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediascraper.MovieTags;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class OfficialTitleBindingTest {
    private org.robolectric.android.controller.ActivityController<Activity> host;
    private Locale previous;private TextView title;private int captures;
    @Rule public org.junit.rules.TestName name=new org.junit.rules.TestName();
    @Before public void setup()throws Exception {
        previous=Locale.getDefault();Locale.setDefault(Locale.US);settle();
        for(String cache:List.of("ALIASES","MEMORY","PREFETCHED")){android.util.LruCache<?,?> value=ReflectionHelpers.getStaticField(OfficialTitleArtwork.class,cache);value.evictAll();}
        for(String map:List.of("BOUND","READY","TRACES")){Map<?,?> value=ReflectionHelpers.getStaticField(OfficialTitleArtwork.class,map);value.clear();}
        host=Robolectric.buildActivity(Activity.class).setup().visible();title=new TextView(host.get());title.setText("Official artwork fallback title");host.get().setContentView(title);title.measure(android.view.View.MeasureSpec.makeMeasureSpec(240,android.view.View.MeasureSpec.EXACTLY),android.view.View.MeasureSpec.makeMeasureSpec(80,android.view.View.MeasureSpec.EXACTLY));title.layout(0,0,240,80);
    }
    @After public void teardown()throws Exception{settle();host.pause().stop().destroy();Locale.setDefault(previous);}
    private void settle()throws Exception {
        ThreadPoolExecutor io=ReflectionHelpers.getStaticField(OfficialTitleArtwork.class,"IO");long until=System.nanoTime()+TimeUnit.SECONDS.toNanos(5);
        while(io.getActiveCount()!=0||!io.getQueue().isEmpty()){if(System.nanoTime()>until)fail("Offline artwork worker did not finish");Thread.sleep(2);}
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
    }
    private Movie movie(long local,long provider){Movie movie=mock(Movie.class);MovieTags tags=new MovieTags();tags.setOnlineId(provider);when(movie.getId()).thenReturn(local);when(movie.getOnlineId()).thenReturn(provider);when(movie.getFullScraperTags(any())).thenReturn(tags);return movie;}
    private void disk(String kind,long id,String language,int color)throws Exception {
        File directory=new File(host.get().getCacheDir(),"official-title-artwork");directory.mkdirs();Bitmap bitmap=Bitmap.createBitmap(24,8,Bitmap.Config.ARGB_8888);bitmap.eraseColor(color);
        try(FileOutputStream stream=new FileOutputStream(new File(directory,kind+"_"+id+"_"+language+".png"))){assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,stream));}bitmap.recycle();
    }
    private int pixel()throws IOException{Bitmap bitmap=Bitmap.createBitmap(240,80,Bitmap.Config.ARGB_8888);title.draw(new Canvas(bitmap));int color=bitmap.getPixel(120,40);File output=new File("build/reports/preview-ui/official-title-"+name.getMethodName()+"-"+(++captures)+".png");output.getParentFile().mkdirs();try(FileOutputStream stream=new FileOutputStream(output)){assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,stream));}bitmap.recycle();return color;}
    @Test public void changedMovieMatchOnTheSameLocalRowRebindsTheCorrectProviderLogo()throws Exception {
        disk("movie",123,"en",Color.RED);disk("movie",456,"en",Color.GREEN);
        OfficialTitleArtwork.bind(title,movie(10,123),true);settle();assertEquals(Color.RED,pixel());
        OfficialTitleArtwork.bind(title,movie(10,456),true);settle();assertEquals(Color.GREEN,pixel());assertTrue(OfficialTitleArtwork.readyForFirstFrame(title));
    }
    @Test public void localeChangeRebindsWithoutKeepingThePreviousLanguageAlias()throws Exception {
        disk("movie",123,"en",Color.RED);disk("movie",123,"fr",Color.BLUE);Movie movie=movie(10,123);
        OfficialTitleArtwork.bind(title,movie,true);settle();assertEquals(Color.RED,pixel());Locale.setDefault(Locale.FRANCE);
        OfficialTitleArtwork.bind(title,movie,true);settle();assertEquals(Color.BLUE,pixel());
    }
    @Test public void stableTransitionFallsBackToWhiteTextWhenTheNewProviderHasNoCachedLogo()throws Exception {
        disk("movie",123,"en",Color.RED);OfficialTitleArtwork.bind(title,movie(10,123),true);settle();assertEquals(Color.RED,pixel());
        OfficialTitleArtwork.bindStable(title,movie(10,456));assertNull(title.getForeground());assertEquals(Color.WHITE,title.getCurrentTextColor());assertTrue(OfficialTitleArtwork.readyForFirstFrame(title));
    }
    @Test public void fullyTransparentCachedLogoRetainsReadableFallbackInsteadOfAnInvisibleTitle()throws Exception {
        disk("movie",123,"en",Color.TRANSPARENT);OfficialTitleArtwork.bind(title,movie(10,123),true);settle();assertNull(title.getForeground());assertEquals(Color.WHITE,title.getCurrentTextColor());assertTrue(OfficialTitleArtwork.readyForFirstFrame(title));
    }
    @Test public void bindingCapturesProviderOnceWhenSourceMetadataChangesDuringResolution()throws Exception {
        disk("tv",123,"en",Color.RED);disk("tv",456,"en",Color.GREEN);OfficialTitleArtwork.bindRemote(title,"tv",123);settle();OfficialTitleArtwork.bindRemote(title,"tv",456);settle();
        com.archos.mediacenter.video.browser.adapters.object.Tvshow show=mock(com.archos.mediacenter.video.browser.adapters.object.Tvshow.class);
        com.archos.mediascraper.ShowTags first=new com.archos.mediascraper.ShowTags(),later=new com.archos.mediascraper.ShowTags();first.setOnlineId(123);later.setOnlineId(456);
        when(show.getShowTags()).thenReturn(first,later);OfficialTitleArtwork.bind(title,show,true);settle();assertEquals(Color.RED,pixel());
    }
    @Test public void movieAndTvWithTheSameProviderNumberKeepDistinctCacheEntriesAndArtworkPixels()throws Exception {
        disk("movie",123,"en",Color.RED);disk("tv",123,"en",Color.GREEN);OfficialTitleArtwork.bindRemote(title,"movie",123);settle();assertEquals(Color.RED,pixel());
        OfficialTitleArtwork.bindRemote(title,"tv",123);settle();assertEquals(Color.GREEN,pixel());assertEquals("Official artwork fallback title",title.getText().toString());
    }
    @Test public void sameProviderCanLoadNewlyAvailableDiskLogoAfterCompletedCacheOnlyMiss()throws Exception {
        Movie media=movie(10,123);OfficialTitleArtwork.bind(title,media,true);settle();assertNull(title.getForeground());assertTrue(OfficialTitleArtwork.readyForFirstFrame(title));
        disk("movie",123,"en",Color.RED);OfficialTitleArtwork.bind(title,media,true);settle();assertEquals(Color.RED,pixel());
    }
    @Test public void failedImageDownloadDoesNotNegativeCacheTheProviderAndSameViewCanRetry()throws Exception {
        okhttp3.OkHttpClient client=ReflectionHelpers.getStaticField(OfficialTitleArtwork.class,"HTTP");var old=client.interceptors();java.util.concurrent.atomic.AtomicInteger images=new java.util.concurrent.atomic.AtomicInteger();
        Bitmap bitmap=Bitmap.createBitmap(24,8,Bitmap.Config.ARGB_8888);bitmap.eraseColor(Color.BLUE);ByteArrayOutputStream bytes=new ByteArrayOutputStream();assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes));bitmap.recycle();
        ReflectionHelpers.setField(client,"interceptors",List.<okhttp3.Interceptor>of(chain->{String path=chain.request().url().encodedPath();if(path.endsWith("/images"))return new okhttp3.Response.Builder().request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(200).message("OK").body(okhttp3.ResponseBody.create("{\"id\":123,\"logos\":[{\"file_path\":\"/fixture.png\",\"iso_639_1\":\"en\"}]}",okhttp3.MediaType.get("application/json"))).build();if(images.incrementAndGet()==1)throw new IOException("Synthetic image failure");return new okhttp3.Response.Builder().request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(200).message("OK").body(okhttp3.ResponseBody.create(bytes.toByteArray(),okhttp3.MediaType.get("image/png"))).build();}));
        try{OfficialTitleArtwork.bindRemote(title,"movie",123);settle();assertNull(title.getForeground());assertFalse(host.get().getSharedPreferences("preview_title_logos",0).contains("movie_123_en"));OfficialTitleArtwork.bindRemote(title,"movie",123);settle();assertEquals(2,images.get());assertEquals(Color.BLUE,pixel());}
        finally{settle();ReflectionHelpers.setField(client,"interceptors",old);}
    }
    @Test public void verifiedAbsentLogoUsesNegativeCacheUntilExpiryAndPlaybackNeverRequestsNetwork()throws Exception {
        okhttp3.OkHttpClient client=ReflectionHelpers.getStaticField(OfficialTitleArtwork.class,"HTTP");var old=client.interceptors();java.util.concurrent.atomic.AtomicInteger requests=new java.util.concurrent.atomic.AtomicInteger();
        ReflectionHelpers.setField(client,"interceptors",List.<okhttp3.Interceptor>of(chain->{requests.incrementAndGet();return new okhttp3.Response.Builder().request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(200).message("OK").body(okhttp3.ResponseBody.create("{\"id\":123,\"logos\":[]}",okhttp3.MediaType.get("application/json"))).build();}));
        try{OfficialTitleArtwork.bindRemote(title,"movie",123);settle();OfficialTitleArtwork.bindRemote(title,"movie",123);settle();assertEquals(1,requests.get());assertEquals(Color.WHITE,title.getCurrentTextColor());host.get().getSharedPreferences("preview_title_logos",0).edit().putLong("movie_123_en",System.currentTimeMillis()-86400001L).commit();OfficialTitleArtwork.bindRemote(title,"movie",123);settle();assertEquals(2,requests.get());OfficialTitleArtwork.bind(title,movie(10,456),true);settle();assertEquals(2,requests.get());assertNull(title.getForeground());}
        finally{settle();ReflectionHelpers.setField(client,"interceptors",old);}
    }

}
