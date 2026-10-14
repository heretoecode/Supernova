package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PreviewLibraryRestoreRaceTest {
    private final CanonicalMatchProviderTest source=new CanonicalMatchProviderTest();
    @Before public void setup(){PreviewAsyncFixtures.offlineTransport();ReflectionHelpers.setStaticField(CustomApplication.class,"systemLocale",Locale.getDefault());ReflectionHelpers.setStaticField(CustomApplication.class,"mContext",RuntimeEnvironment.getApplication());ReflectionHelpers.setStaticField(CustomApplication.class,"log",org.slf4j.LoggerFactory.getLogger(CustomApplication.class));com.archos.environment.ArchosUtils.setGlobalContext(RuntimeEnvironment.getApplication());source.setup();}
    @After public void close()throws Exception{PreviewAsyncFixtures.drain();java.util.concurrent.ExecutorService writer=ReflectionHelpers.getStaticField(PreviewLibraryLoader.class,"cacheWriter");writer.submit(()->{}).get(10,java.util.concurrent.TimeUnit.SECONDS);PreviewLibraryLoader.cached=null;source.close();}
    @Test public void olderNativeQueryCannotRepublishItsSnapshotAfterSelectedPinRestore()throws Exception{
        Application app=RuntimeEnvironment.getApplication();DbHolder holder=ReflectionHelpers.getField(source,"holder");holder.get().execSQL("UPDATE files SET title='Native fixture',mime_type='video/x-matroska',duration=100000,date_added=1");boolean[] restore={true};
        ContentProvider provider=new ContentProvider(){
            public boolean onCreate(){return true;}public String getType(Uri uri){return null;}
            public Cursor query(Uri uri,String[] columns,String where,String[] args,String order){android.database.sqlite.SQLiteQueryBuilder query=new android.database.sqlite.SQLiteQueryBuilder();query.setTables(VideoOpenHelper.VIDEO_VIEW_NAME);Cursor rows=query.query(holder.get(),columns,where,args,null,null,order);rows.getCount();if(!Arrays.asList(columns).contains(VideoStore.Video.VideoColumns.DATE_ADDED))return rows;try{com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper mapper=new com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper();mapper.bindColumns(rows);assertTrue(rows.moveToFirst());assertNotNull(mapper.bind(rows));rows.moveToPosition(-1);}catch(RuntimeException invalidFixture){throw new AssertionError("Native fixture mapping failed",invalidFixture);}if(restore[0]){restore[0]=false;try{new PreferenceRestoreTransaction(app).apply(new PreferenceRestoreTransaction(app).prepare(Map.of(BackupCategory.PINNED,new JSONObject().put("schema",1).put("rows",new JSONArray())),null),()->false);}catch(Exception failure){rows.close();throw new IllegalStateException(failure);}}return rows;}
            public Uri insert(Uri uri,ContentValues values){throw new AssertionError();}public int update(Uri uri,ContentValues values,String where,String[] args){throw new AssertionError();}public int delete(Uri uri,String where,String[] args){throw new AssertionError();}
        };provider.attachInfo(app,null);ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY,provider);
        PreviewLibraryLoader stale=new PreviewLibraryLoader(app);try{Cursor rows=stale.loadInBackground();if(rows!=null)rows.close();fail("Expected cancellation: warning="+ReflectionHelpers.getField(stale,"loadWarning")+", cache="+PreviewLibraryLoader.memoryCache());}catch(android.os.OperationCanceledException expected){}assertNull(PreviewLibraryLoader.memoryCache());
        PreviewLibraryLoader fresh=new PreviewLibraryLoader(app);try(Cursor rows=fresh.loadInBackground()){assertNotNull(rows);assertNotNull(PreviewLibraryLoader.memoryCache());assertFalse(PreviewLibraryLoader.memoryCache().movies.isEmpty());assertTrue(PreviewLibraryLoader.memoryCache().movies.stream().noneMatch(entry->((com.archos.mediacenter.video.browser.adapters.object.Movie)entry.media).isPinned()));}
    }
}
