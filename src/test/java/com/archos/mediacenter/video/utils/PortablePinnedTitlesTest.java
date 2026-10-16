package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.DbHolder;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediaprovider.video.ScraperProvider;
import com.archos.mediaprovider.video.ScraperStore;
import org.robolectric.shadows.ShadowContentResolver;
import com.archos.mediacenter.video.utils.backup.PortablePinnedTitles;
import com.archos.mediacenter.video.utils.backup.PortablePinnedTitles.*;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortablePinnedTitlesTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    @Test public void kindAndTimestampRemapWithoutFilenamesOrMovieCollectionsAndEmptyClearsOnlyPins()throws Exception{
        Context source=scope("source");JSONObject payload=PortablePinnedTitles.export(source,List.of(new Title(Kind.MOVIE,10,123,400),new Title(Kind.TV,20,123,800),new Title(Kind.MOVIE,30,456,0)));assertEquals(2,payload.getJSONArray("rows").length());assertFalse(payload.toString().contains("collection"));assertFalse(payload.toString().contains("filename"));
        List<Title> receiving=List.of(new Title(Kind.MOVIE,700,123,9),new Title(Kind.TV,800,123,8),new Title(Kind.MOVIE,900,456,7));var prepared=PortablePinnedTitles.prepare(payload,receiving);assertEquals(0,prepared.unresolved);assertEquals(Long.valueOf(400),prepared.pins.get("MOVIE:700"));assertEquals(Long.valueOf(800),prepared.pins.get("TV:800"));assertEquals(Long.valueOf(0),prepared.pins.get("MOVIE:900"));assertEquals(9,receiving.get(0).pinned);
        var empty=PortablePinnedTitles.prepare(new JSONObject().put("schema",1).put("rows",new JSONArray()),receiving);assertEquals(Set.of(0L),new HashSet<>(empty.pins.values()));assertEquals(0,empty.unresolved);
    }
    @Test public void unknownAndDuplicateIdentitiesRemainDistinctPendingRecordsAcrossAnotherExport()throws Exception{
        Context source=scope("source"),target=scope("target");JSONObject payload=PortablePinnedTitles.export(source,List.of(new Title(Kind.MOVIE,10,123,400),new Title(Kind.MOVIE,11,123,800),new Title(Kind.TV,20,0,1000)));
        var prepared=PortablePinnedTitles.prepare(payload,List.of(new Title(Kind.MOVIE,700,123,9)));assertEquals(3,prepared.unresolved);assertEquals(Long.valueOf(0),prepared.pins.get("MOVIE:700"));PreferenceManager.getDefaultSharedPreferences(target).edit().putString(PortablePinnedTitles.PENDING,prepared.pending).commit();assertEquals(3,PortablePinnedTitles.export(target,List.of()).getJSONArray("rows").length());
        JSONObject single=PortablePinnedTitles.export(scope("single"),List.of(new Title(Kind.MOVIE,1,123,4)));assertEquals(1,PortablePinnedTitles.prepare(single,List.of(new Title(Kind.MOVIE,70,123,0),new Title(Kind.MOVIE,71,123,0))).unresolved);
    }
    @Test public void injectedCredentialsLocationsAndFractionalTimesCannotPrepare()throws Exception{
        JSONObject payload=PortablePinnedTitles.export(scope("source"),List.of(new Title(Kind.MOVIE,1,123,4)));
        for(String field:List.of("credentials","filename","user_collection")){JSONObject bad=new JSONObject(payload.toString());bad.getJSONArray("rows").getJSONObject(0).put(field,"CANARY");try{PortablePinnedTitles.validate(bad);fail();}catch(JSONException expected){}}
        for(Object time:List.of(1.5,"4",0,-1)){JSONObject bad=new JSONObject(payload.toString());bad.getJSONArray("rows").getJSONObject(0).put("pinnedAt",time);try{PortablePinnedTitles.prepare(bad,List.of());fail();}catch(JSONException expected){}}
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void actualUnfilteredProviderProjectionIncludesOrphanPinsWithoutReadingMetadataText()throws Exception{
        Context context=RuntimeEnvironment.getApplication();DbHolder database=new DbHolder(new VideoOpenHelper(context));try{
            var db=database.get();
            // Historic orphan scraper records are possible in an imported database. Only the fixture disables FK checks.
            db.execSQL("PRAGMA foreign_keys=OFF");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie) VALUES(10,100,123,400,'TITLE_CANARY')");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie) VALUES(11,101,456,0,'UNPINNED_CANARY')");db.execSQL("INSERT INTO SHOW(_id,s_online_id,Nova_pinned,name_show) VALUES(20,789,800,'SHOW_CANARY')");
        ScraperProvider.hookUriMatcher(new UriMatcher(UriMatcher.NO_MATCH));
        ScraperProvider scraper=new ScraperProvider(context,database);
        ContentProvider provider=new ContentProvider(){
            public boolean onCreate(){return true;}public String getType(Uri uri){return null;}
            public Cursor query(Uri uri,String[] columns,String where,String[] args,String order){return scraper.query(uri,columns,where,args,order);}
            public Uri insert(Uri uri,ContentValues values){throw new AssertionError("Backup inventory is read only");}
            public int update(Uri uri,ContentValues values,String where,String[] args){throw new AssertionError("Backup inventory is read only");}
            public int delete(Uri uri,String where,String[] args){throw new AssertionError("Backup inventory is read only");}
        };
        provider.attachInfo(context,null);ShadowContentResolver.registerProviderInternal(ScraperStore.AUTHORITY,provider);
        List<Title> titles=PortablePinnedTitles.read(context);assertEquals(3,titles.size());JSONObject payload=PortablePinnedTitles.export(context,titles);assertEquals(2,payload.getJSONArray("rows").length());assertFalse(payload.toString().contains("CANARY"));assertTrue(payload.toString().contains("tmdb:movie:123"));assertTrue(payload.toString().contains("tmdb:tv:789"));
        db.execSQL("UPDATE MOVIE SET Nova_pinned=1.5 WHERE _id=10");try{PortablePinnedTitles.read(context);fail();}catch(java.io.IOException expected){}
        }finally{database.close();}
    }
    private void seed(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("INSERT INTO files(_id,remote_id,media_type,bookmark,Archos_hiddenByUser) VALUES(100,100,3,4444,1),(101,101,3,5555,0)");
        db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie) VALUES(10,100,123,9,'KEEP_MOVIE'),(11,101,456,7,'KEEP_OTHER')");
        db.execSQL("INSERT INTO SHOW(_id,s_online_id,Nova_pinned,name_show) VALUES(20,789,8,'KEEP_SHOW')");
    }
    private PortablePinnedTitles.Prepared replacement(Context context,android.database.sqlite.SQLiteDatabase db)throws Exception {
        JSONObject payload=PortablePinnedTitles.export(scope("replacement-source"),List.of(new Title(Kind.MOVIE,1,123,400),new Title(Kind.TV,2,789,800)));
        return PortablePinnedTitles.prepare(payload,PortablePinnedTitles.read(db));
    }
    private Map<String,Long> pins(android.database.sqlite.SQLiteDatabase db)throws Exception {
        Map<String,Long> result=new LinkedHashMap<>();for(Title title:PortablePinnedTitles.read(db))result.put(title.target(),title.pinned);return result;
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void sqliteReplacementUpdatesOnlyPinsAndPreservesFileProgressHiddenStateAndMetadata()throws Exception {
        Context context=RuntimeEnvironment.getApplication();try(VideoOpenHelper helper=new VideoOpenHelper(context)) {
            var db=helper.getWritableDatabase();seed(db);var prepared=replacement(context,db);
            db.execSQL("UPDATE MOVIE SET name_movie='NEW_METADATA' WHERE _id=10");
            PortablePinnedTitles.replace(db,prepared,()->false,count->{});
            assertEquals(Map.of("MOVIE:10",400L,"MOVIE:11",0L,"TV:20",800L),pins(db));
            try(var rows=db.rawQuery("SELECT bookmark,Archos_hiddenByUser FROM files WHERE _id=100",null)){assertTrue(rows.moveToFirst());assertEquals(4444,rows.getInt(0));assertEquals(1,rows.getInt(1));}
            try(var rows=db.rawQuery("SELECT name_movie,m_online_id,video_id FROM MOVIE WHERE _id=10",null)){assertTrue(rows.moveToFirst());assertEquals("NEW_METADATA",rows.getString(0));assertEquals(123,rows.getLong(1));assertEquals(100,rows.getLong(2));}
        }
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void cancellationAndFailureAfterFirstWriteRollBackEveryPin()throws Exception {
        Context context=RuntimeEnvironment.getApplication();try(VideoOpenHelper helper=new VideoOpenHelper(context)) {
            var db=helper.getWritableDatabase();seed(db);Map<String,Long> before=pins(db);
            try{PortablePinnedTitles.replace(db,replacement(context,db),()->true,count->{});fail();}catch(java.io.InterruptedIOException expected){}
            assertEquals(before,pins(db));
            try{PortablePinnedTitles.replace(db,replacement(context,db),()->false,count->{throw new java.io.IOException("Injected write checkpoint failure");});fail();}catch(java.io.IOException expected){}
            assertEquals(before,pins(db));
            var cancelled=new java.util.concurrent.atomic.AtomicBoolean(false);
            try{PortablePinnedTitles.replace(db,replacement(context,db),cancelled::get,count->cancelled.set(true));fail();}catch(java.io.InterruptedIOException expected){}
            assertEquals(before,pins(db));assertFalse(db.inTransaction());
        }
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void changedProviderPinOrInventoryRejectsStalePreviewBeforeAnyWrites()throws Exception {
        Context context=RuntimeEnvironment.getApplication();try(VideoOpenHelper helper=new VideoOpenHelper(context)) {
            var db=helper.getWritableDatabase();seed(db);
            for(String change:List.of("UPDATE MOVIE SET m_online_id=999 WHERE _id=10","UPDATE MOVIE SET Nova_pinned=12 WHERE _id=10","INSERT INTO SHOW(_id,s_online_id,Nova_pinned,name_show) VALUES(21,999,1,'NEW_SHOW')")) {
                var prepared=replacement(context,db);db.execSQL(change);Map<String,Long> afterUserChange=pins(db);
                try{PortablePinnedTitles.replace(db,prepared,()->false,count->{fail("Stale preview must not write");});fail();}catch(java.io.IOException expected){}
                assertEquals(afterUserChange,pins(db));
            }
        }
    }
}
