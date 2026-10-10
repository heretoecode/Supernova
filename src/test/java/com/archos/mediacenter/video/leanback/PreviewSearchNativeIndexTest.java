package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import com.archos.mediacenter.video.leanback.search.PreviewSearch;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Actual native SQLite views/projections and the real async search path, with in-process provider transport. */
@org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewSearchNativeIndexTest {
    @Test public void originalTitleCastAndStudioComeFromTheNativeIndex()throws Exception {
        Application app=RuntimeEnvironment.getApplication();
        DbHolder database=new DbHolder(new VideoOpenHelper(app));
        ScraperProvider.hookUriMatcher(new UriMatcher(UriMatcher.NO_MATCH));ScraperProvider scraper=new ScraperProvider(app,database);
        ContentProvider provider=new ContentProvider(){
            public boolean onCreate(){return true;}public String getType(Uri uri){return null;}
            public Cursor query(Uri uri,String[] columns,String where,String[] args,String order){
                if(uri.getAuthority().equals(ScraperStore.AUTHORITY))return scraper.query(uri,columns,where,args,order);
                return database.get().query(VideoOpenHelper.VIDEO_VIEW_NAME,columns,where,args,null,null,order);
            }
            public Uri insert(Uri uri,ContentValues values){throw new AssertionError("Search must be read only");}
            public int update(Uri uri,ContentValues values,String where,String[] args){throw new AssertionError("Search must be read only");}
            public int delete(Uri uri,String where,String[] args){throw new AssertionError("Search must be read only");}
        };
        provider.attachInfo(app,null);ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY,provider);ShadowContentResolver.registerProviderInternal(ScraperStore.AUTHORITY,provider);
        ContentValues file=new ContentValues();file.put("_id",77);file.put("remote_id",77);file.put("_data","file:///Movies/sample.mkv");file.put("title","Sample filename");file.put("mime_type","video/x-matroska");file.put("duration",100000);file.put("media_type",3);file.put(VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE,com.archos.mediascraper.BaseTags.MOVIE);file.put(VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER,0);
        database.get().insertOrThrow("files",null,file);long id=77;ContentValues tags=new ContentValues();tags.put(ScraperStore.Movie.VIDEO_ID,id);tags.put(ScraperStore.Movie.ONLINE_ID,550);tags.put(ScraperStore.Movie.NAME,"The Local Film");tags.put(ScraperStore.Movie.ORIGINAL_TITLE,"Le Film Original");tags.put(ScraperStore.Movie.ACTORS_FORMATTED,"Actual Actor");tags.put(ScraperStore.Movie.STUDIOS_FORMATTED,"Actual Studio");assertNotNull("Native movie insert",scraper.insert(ScraperStore.Movie.URI.BASE,tags));
        try(Cursor indexed=database.get().rawQuery("SELECT count(*) FROM video",null)){assertTrue(indexed.moveToFirst());assertEquals(1,indexed.getInt(0));}
        try(Cursor original=app.getContentResolver().query(ScraperStore.Movie.URI.ALL,new String[]{"_id",ScraperStore.Movie.ORIGINAL_TITLE},null,null,null)){assertNotNull(original);assertTrue("Native original title row",original.moveToFirst());assertEquals("Le Film Original",original.getString(1));}
        com.archos.mediacenter.video.browser.loader.SearchVideoLoader all=new com.archos.mediacenter.video.browser.loader.SearchVideoLoader(app);all.setQuery("");
        try(Cursor nativeFiles=app.getContentResolver().query(all.getUri(),all.getProjection(),all.getSelection(),all.getSelectionArgs(),all.getSortOrder())){assertNotNull(nativeFiles);assertTrue("Native search loader row",nativeFiles.moveToFirst());var mapper=new com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper();mapper.bindColumns(nativeFiles);assertTrue("Native mapped movie",mapper.bind(nativeFiles) instanceof com.archos.mediacenter.video.browser.adapters.object.Movie);}
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();PreviewSearch search=new PreviewSearch(host.get(),0,null);
        try {host.get().setContentView(search);PreviewPagesTest.layout(search);assertTrue(search.isAttachedToWindow());
            for(String query:new String[]{"film original","actual actor","actual studio"}) {
                search.acceptVoice(query);Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(300));
                long until=System.nanoTime()+3_000_000_000L;java.util.List<?> results;
                do {Thread.sleep(20);Shadows.shadowOf(Looper.getMainLooper()).idle();results=ReflectionHelpers.getField(search,"items");}while(results.isEmpty()&&System.nanoTime()<until);
                android.widget.TextView status=ReflectionHelpers.getField(search,"status");assertEquals("Native indexed query: "+query+" ("+status.getText()+")",1,results.size());
                Object result=results.get(0);com.archos.mediacenter.video.browser.adapters.object.Video video=ReflectionHelpers.getField(result,"video");assertEquals(id,video.getId());
                search.acceptVoice("");Shadows.shadowOf(Looper.getMainLooper()).idle();
            }
        } finally {host.pause().stop().destroy();database.close();}
    }
}
