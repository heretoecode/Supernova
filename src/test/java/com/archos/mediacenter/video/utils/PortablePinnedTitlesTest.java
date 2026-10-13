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
            var db=database.get();db.execSQL("INSERT INTO MOVIE(_id,m_online_id,Nova_pinned,name_movie) VALUES(10,123,400,'TITLE_CANARY')");db.execSQL("INSERT INTO MOVIE(_id,m_online_id,Nova_pinned,name_movie) VALUES(11,456,0,'UNPINNED_CANARY')");db.execSQL("INSERT INTO SHOW(_id,s_online_id,Nova_pinned,name_show) VALUES(20,789,800,'SHOW_CANARY')");
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
}
