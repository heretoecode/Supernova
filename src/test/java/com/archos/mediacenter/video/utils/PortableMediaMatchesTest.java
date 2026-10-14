package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PortableMediaMatchesTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();}
    private Video video(long id,String uri){Video video=mock(Video.class);when(video.getId()).thenReturn(id);when(video.getFileUri()).thenReturn(Uri.parse(uri));return video;}
    private PortablePhysicalReferences physical(Video... videos){return new PortablePhysicalReferences(app,List.of(videos));}
    private JSONObject payload(PortablePhysicalReferences physical)throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("physical",physical.reference(10)).put("identity","tmdb:movie:123").put("episodeProvider",JSONObject.NULL)));}
    private void seed(){try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();
        db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,title,ArchosMediaScraper_type,ArchosMediaScraper_id,Archos_hiddenByUser,bookmark) VALUES(10,10,3,'file:///PRIVATE_PATH.mkv','PRIVATE_TITLE',11,100,1,-2),(11,11,3,'file:///episode.mkv','PRIVATE_EPISODE',12,101,0,4444),(12,12,3,'putio:///ACCOUNT_CANARY/file','PRIVATE_REMOTE',11,102,0,0),(13,13,3,'file:///unmatched.mkv','UNKNOWN',NULL,NULL,0,0)");
        db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie,plot_movie) VALUES(100,10,123,'PRIVATE_MOVIE','PRIVATE_PLOT'),(102,12,987654,'PRIVATE_REMOTE','PRIVATE_REMOTE_PLOT')");
        db.execSQL("INSERT INTO SHOW(_id,s_online_id,name_show) VALUES(20,456,'PRIVATE_SHOW')");db.execSQL("INSERT INTO EPISODE(_id,video_id,show_episode,season_episode,number_episode,e_online_id,name_episode) VALUES(101,11,20,0,2,789,'PRIVATE_EPISODE')");
    }}
    @Test public void nativeSourceIncludesHiddenWatchedMovieAndSpecialEpisodeButExcludesPutioAndUnmatched()throws Exception{
        seed();try(VideoOpenHelper helper=new VideoOpenHelper(app)){var indexed=PortableMediaMatches.read(helper.getReadableDatabase());assertEquals(Set.of(10L,11L),indexed.keySet());assertEquals("tmdb:movie:123",indexed.get(10L).identity);assertNull(indexed.get(10L).episodeProvider);assertEquals("tmdb:tv:456:0:2",indexed.get(11L).identity);assertEquals(Long.valueOf(789),indexed.get(11L).episodeProvider);
            var payload=PortableMediaMatches.export(app,indexed,physical(video(10,"file:///PRIVATE_PATH.mkv"),video(11,"file:///episode.mkv")));String text=payload.toString();for(String excluded:List.of("PRIVATE","AUTH_CANARY","ACCOUNT_CANARY","plot","title","987654","_data","trakt"))assertFalse(excluded,text.contains(excluded));PortableMediaMatches.validate(payload);
        }
    }
    @Test public void readerRejectsDanglingWrongKindAndReceivingApiOverflowWithoutChangingNativeRows()throws Exception{
        seed();for(String mutation:List.of("UPDATE files SET ArchosMediaScraper_id=999 WHERE _id=10","UPDATE files SET ArchosMediaScraper_type=99 WHERE _id=10","UPDATE MOVIE SET m_online_id=2147483648 WHERE _id=100")){
            app.deleteDatabase("media.db");seed();try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL(mutation);try{PortableMediaMatches.read(db);fail();}catch(IOException expected){}try(var rows=db.rawQuery("SELECT bookmark,_data FROM files WHERE _id=10",null)){assertTrue(rows.moveToFirst());assertEquals(-2,rows.getInt(0));assertEquals("file:///PRIVATE_PATH.mkv",rows.getString(1));}}
        }
    }
    @Test public void exactFileAndUniqueProviderTargetPrepareWithoutGuessingOrExportingMetadata()throws Exception{
        var references=physical(video(10,"file:///PRIVATE_PATH.mkv"));var target=new PortableMediaMatches.Target(100,10,new PortableMediaMatches.Match("tmdb:movie:123",null));var prepared=PortableMediaMatches.prepare(payload(references),List.of(10L),List.of(target),references);assertEquals(0,prepared.unresolved);assertSame(target,prepared.associations.get(10L));assertFalse(prepared.pending.contains("PRIVATE"));assertFalse(app.getDatabasePath("media.db").exists());
    }
    @Test public void differentLocationDuplicateFilesMissingOrDuplicateMetadataAndProviderMismatchStayPending()throws Exception{
        var source=physical(video(10,"file:///source.mkv"));JSONObject payload=payload(source);var match=new PortableMediaMatches.Match("tmdb:movie:123",null);var target=new PortableMediaMatches.Target(100,10,match);
        for(var references:List.of(physical(video(10,"file:///different.mkv")),physical(video(10,"file:///source.mkv"),video(11,"file:///source.mkv")))){var prepared=PortableMediaMatches.prepare(payload,List.of(10L,11L),List.of(target),references);assertEquals(1,prepared.unresolved);assertTrue(prepared.associations.isEmpty());}
        for(List<PortableMediaMatches.Target> targets:List.of(List.<PortableMediaMatches.Target>of(),List.of(target,new PortableMediaMatches.Target(101,10,match)),List.of(new PortableMediaMatches.Target(102,10,new PortableMediaMatches.Match("tmdb:movie:456",null))))){var prepared=PortableMediaMatches.prepare(payload,List.of(10L),targets,source);assertEquals(1,prepared.unresolved);assertTrue(prepared.associations.isEmpty());}
    }
    @Test public void pendingKeepsOpaqueIdentityAndAuthenticatedLocationsNeverBindAfterStrippingSecrets()throws Exception{
        var references=physical(video(10,"smb://user:AUTH_CANARY@nas/share/PRIVATE.mkv"));JSONObject payload=payload(references);assertTrue(payload.getJSONArray("rows").getJSONObject(0).getString("physical").startsWith("unresolved:"));assertFalse(payload.toString().contains("CANARY"));assertFalse(payload.toString().contains("PRIVATE"));
        var prepared=PortableMediaMatches.prepare(payload,List.of(10L),List.of(new PortableMediaMatches.Target(100,10,new PortableMediaMatches.Match("tmdb:movie:123",null))),references);assertEquals(1,prepared.unresolved);PreferenceManager.getDefaultSharedPreferences(app).edit().putString(PortableMediaMatches.PENDING,prepared.pending).commit();assertEquals(payload.toString(),PortableMediaMatches.export(app,Collections.emptyMap(),references).toString());
    }
    @Test public void identicalProviderInAnotherFileNeverTransfersNativeMetadataOwnership()throws Exception{
        var references=physical(video(10,"file:///fixture.mkv"));var match=new PortableMediaMatches.Match("tmdb:movie:123",null);var other=new PortableMediaMatches.Target(101,11,match);
        var missing=PortableMediaMatches.prepare(payload(references),List.of(10L),List.of(other),references);assertEquals(1,missing.unresolved);assertTrue(missing.associations.isEmpty());
        var owned=new PortableMediaMatches.Target(100,10,match);var valid=PortableMediaMatches.prepare(payload(references),List.of(10L),List.of(other,owned),references);assertEquals(0,valid.unresolved);assertSame(owned,valid.associations.get(10L));
    }
    @Test public void strictSchemaRejectsPathFieldsDuplicateRecordsCoercionAndConflictingEpisodeIds()throws Exception{
        var references=physical(video(10,"file:///fixture.mkv"));for(String extra:List.of("_data","path","trakt_id","plot","credentials")){JSONObject payload=payload(references);payload.getJSONArray("rows").getJSONObject(0).put(extra,"PRIVATE_CANARY");try{PortableMediaMatches.validate(payload);fail();}catch(JSONException expected){}}
        JSONObject payload=payload(references);payload.getJSONArray("rows").put(payload.getJSONArray("rows").getJSONObject(0));try{PortableMediaMatches.validate(payload);fail();}catch(JSONException expected){}
        for(String identity:List.of("tmdb:tv:1:0:0","tmdb:movie:2147483648","file:///PRIVATE.mkv","tmdb:tv:1:-1:2"))try{new PortableMediaMatches.Match(identity,null);fail();}catch(JSONException expected){}
        try{new PortableMediaMatches.Match("tmdb:movie:1",2L);fail();}catch(JSONException expected){}
        payload=payload(references);payload.put("schema","1");try{PortableMediaMatches.validate(payload);fail();}catch(JSONException expected){}
    }
    @Test public void explicitEmptyAndPutioExclusionKeepCodecUnregisteredUntilNativeRecoveryIntegration()throws Exception{
        var references=physical(video(10,"putio:///ACCOUNT_CANARY/file"));assertEquals(0,PortableMediaMatches.export(app,Map.of(10L,new PortableMediaMatches.Match("tmdb:movie:1",null)),references).getJSONArray("rows").length());
        var prepared=PortableMediaMatches.prepare(new JSONObject().put("schema",1).put("rows",new JSONArray()),List.of(10L),Collections.emptyList(),references);assertTrue(prepared.associations.isEmpty());assertEquals(0,prepared.unresolved);assertFalse(LogicalBackupArchive.registered().contains(BackupCategory.MATCHES));
    }
}
