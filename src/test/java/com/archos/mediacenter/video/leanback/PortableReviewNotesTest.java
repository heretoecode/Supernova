package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.utils.backup.PortablePhysicalReferences;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableReviewNotesTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    private Movie movie(long id,long provider,String path){Movie movie=mock(Movie.class);when(movie.getId()).thenReturn(id);when(movie.getOnlineId()).thenReturn(provider);when(movie.getFilePath()).thenReturn(path);when(movie.getFileUri()).thenReturn(path==null?null:Uri.parse(path));when(movie.getName()).thenReturn("TITLE_CANARY");return movie;}
    private SharedPreferences health(Context context){return context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);}
    private void apply(Context context,PortableReviewNotes.Prepared prepared){var editor=health(context).edit();for(String key:prepared.keys)editor.remove(key);for(var entry:prepared.values.entrySet()){if(entry.getValue() instanceof Boolean)editor.putBoolean(entry.getKey(),(Boolean)entry.getValue());else editor.putString(entry.getKey(),(String)entry.getValue());}assertTrue(editor.commit());}
    @Test public void onlyUserAnnotationsExportAndExactPhysicalAndExpectedMatchRemapWithoutCacheChanges()throws Exception{
        Context source=scope("source"),target=scope("target");var original=movie(10,123,"file:///storage/Films/FILE_CANARY.mkv");var incoming=movie(700,123,"file:///storage/Films/FILE_CANARY.mkv");
        health(source).edit().putBoolean("incorrect:10",true).putString("offline:smb://PRIVATE_CANARY", "HOST_CANARY").putString("media:11","CACHE_CANARY").putString("problem:12","CACHE_CANARY").commit();
        JSONObject payload=PortableReviewNotes.export(source,new PortablePhysicalReferences(source,List.of(original)));assertFalse(payload.toString().contains("CANARY"));assertFalse(payload.toString().contains("smb:"));assertEquals(1,payload.getJSONArray("rows").length());
        health(target).edit().putBoolean("incorrect:99",true).putString("offline:source","KEEP_OFFLINE").putString("media:3","KEEP_CACHE").commit();Map<String,?> before=health(target).getAll();var prepared=PortableReviewNotes.prepare(target,payload,new PortablePhysicalReferences(target,List.of(incoming)));assertEquals(before,health(target).getAll());assertEquals(0,prepared.unresolved);apply(target,prepared);
        assertTrue(health(target).getBoolean("incorrect:700",false));assertFalse(health(target).contains("incorrect:99"));assertEquals("KEEP_OFFLINE",health(target).getString("offline:source",null));assertEquals("KEEP_CACHE",health(target).getString("media:3",null));
    }
    @Test public void changedMatchAndChangedPathStayPendingAndSurviveAnotherExport()throws Exception{
        Context source=scope("source"),target=scope("target");var original=movie(10,123,"file:///storage/fixture.mkv");health(source).edit().putBoolean("incorrect:10",true).commit();JSONObject payload=PortableReviewNotes.export(source,new PortablePhysicalReferences(source,List.of(original)));
        for(Movie incoming:List.of(movie(700,456,"file:///storage/fixture.mkv"),movie(700,123,"file:///new-storage/fixture.mkv"))){
            var references=new PortablePhysicalReferences(target,List.of(incoming));var prepared=PortableReviewNotes.prepare(target,payload,references);assertEquals(1,prepared.unresolved);apply(target,prepared);assertFalse(health(target).getBoolean("incorrect:700",false));assertEquals(payload.toString(),PortableReviewNotes.export(target,references).toString());
        }
    }
    @Test public void duplicatePhysicalRowsAndMissingSourceRecordsUseOpaquePendingReferencesInsteadOfGuessing()throws Exception{
        Context source=scope("source"),target=scope("target");var first=movie(10,123,"file:///storage/fixture.mkv");var second=movie(11,123,"file:///storage/fixture.mkv");var references=new PortablePhysicalReferences(source,List.of(first,second));assertTrue(references.reference(10).startsWith("unresolved:"));assertTrue(references.reference(11).startsWith("unresolved:"));assertNotEquals(references.reference(10),references.reference(11));assertNull(references.resolve(references.reference(10)));
        health(source).edit().putBoolean("incorrect:10",true).putBoolean("incorrect:11",false).putBoolean("incorrect:99",true).commit();JSONObject payload=PortableReviewNotes.export(source,references);assertEquals(3,payload.getJSONArray("rows").length());var prepared=PortableReviewNotes.prepare(target,payload,new PortablePhysicalReferences(target,List.of(movie(700,123,"file:///storage/fixture.mkv"))));assertEquals(3,prepared.unresolved);apply(target,prepared);assertEquals(3,PortableReviewNotes.export(target,new PortablePhysicalReferences(target,List.of())).getJSONArray("rows").length());
    }
    @Test public void credentialOrQueryBearingUrisStayOpaqueInsteadOfBindingAfterSecretRemoval()throws Exception{
        Context source=scope("source"),target=scope("target");
        for(String path:List.of("smb://person:AUTH_CANARY@nas/Films/FILE_CANARY.mkv","smb://person%3AAUTH_CANARY%40nas/Films/FILE_CANARY.mkv","https://host/media?id=1&token=AUTH_CANARY","https://host/media#token=AUTH_CANARY")){
            var references=new PortablePhysicalReferences(source,List.of(movie(10,123,path)));assertTrue(references.reference(10).startsWith("unresolved:"));health(source).edit().putBoolean("incorrect:10",true).commit();JSONObject payload=PortableReviewNotes.export(source,references);assertFalse(payload.toString().contains("CANARY"));assertEquals(1,PortableReviewNotes.prepare(target,payload,new PortablePhysicalReferences(target,List.of(movie(700,123,path)))).unresolved);
        }
    }
    @Test public void duplicateLocalIdsRejectInsteadOfBindingWhicheverPathWasReadFirst() {
        Context source=scope("source");
        try{new PortablePhysicalReferences(source,List.of(movie(10,123,"file:///storage/first.mkv"),movie(10,123,"file:///storage/second.mkv")));fail();}catch(IllegalArgumentException expected){}
    }
    @Test public void emptySelectedReviewClearsOnlyItsIntentAndInjectedCachesOrLocationsAreRejected()throws Exception{
        Context target=scope("target");health(target).edit().putBoolean("incorrect:10",true).putString("offline:source","KEEP").commit();JSONObject empty=new JSONObject().put("schema",1).put("rows",new JSONArray());apply(target,PortableReviewNotes.prepare(target,empty,new PortablePhysicalReferences(target,List.of())));assertFalse(health(target).contains("incorrect:10"));assertEquals("KEEP",health(target).getString("offline:source",null));
        for(JSONObject row:List.of(new JSONObject().put("physical","file:"+"a".repeat(64)).put("expected",JSONObject.NULL).put("incorrect",true).put("offline","PRIVATE"),new JSONObject().put("physical","smb://host/PRIVATE").put("expected",JSONObject.NULL).put("incorrect",true),new JSONObject().put("physical","file:"+"a".repeat(64)).put("expected","tmdb:movie:9999999999999999999").put("incorrect",true)))try{PortableReviewNotes.validate(new JSONObject().put("schema",1).put("rows",new JSONArray().put(row)));fail();}catch(JSONException expected){}
    }
}
