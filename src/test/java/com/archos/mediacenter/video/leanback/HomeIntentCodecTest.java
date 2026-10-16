package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class HomeIntentCodecTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    private Entry movie(long local,long provider){Movie movie=mock(Movie.class);when(movie.getId()).thenReturn(local);when(movie.getOnlineId()).thenReturn(provider);when(movie.getFilePath()).thenReturn("smb://person:CANARY@host/private/MEDIA-CANARY.mkv?token=CANARY");return new Entry(movie,local,0,"Drama");}
    @Test public void exactProviderMembershipRemapsAndMissingReferencesSurviveAnotherBackup()throws Exception{
        Context source=scope("source"),destination=scope("destination");PreviewHomeRows home=new PreviewHomeRows(source);PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:portable","Portable");row.members.add("v10");row.members.add("v11");row.movies=true;row.tv=false;row.visible=false;row.matchAll=true;row.icon="Drama";row.maximum=20;home.rows.add(0,row);home.save();
        Snapshot oldLibrary=new Snapshot();oldLibrary.movies.add(movie(10,123));JSONObject payload=HomeIntentCodec.export(source,oldLibrary);assertFalse(payload.toString().contains("CANARY"));assertFalse(payload.toString().contains("smb://"));assertFalse(payload.toString().contains("MEDIA-CANARY"));
        PreviewHomeRows receiving=new PreviewHomeRows(destination);for(var r:receiving.rows)if(r.id.equals("watchnext"))r.members.add("v99");receiving.save();Map<String,?> before=PreferenceManager.getDefaultSharedPreferences(destination).getAll();
        Snapshot newLibrary=new Snapshot();newLibrary.movies.add(movie(700,123));HomeIntentCodec.Prepared prepared=HomeIntentCodec.prepare(destination,payload,newLibrary);assertEquals(before,PreferenceManager.getDefaultSharedPreferences(destination).getAll());assertEquals(1,prepared.unresolved);
        PreferenceManager.getDefaultSharedPreferences(destination).edit().putString("preview_home_rows41",prepared.rows).putString(HomeIntentCodec.PENDING,prepared.pending).commit();
        PreviewHomeRows restored=new PreviewHomeRows(destination);assertEquals("custom:portable",restored.rows.get(0).id);assertEquals(Set.of("v700"),restored.rows.get(0).members);assertFalse(restored.rows.get(0).visible);assertTrue(restored.rows.get(0).matchAll);assertEquals(20,restored.rows.get(0).maximum);assertEquals("Drama",restored.rows.get(0).icon);
        assertEquals(Set.of("v99"),restored.rows.stream().filter(r->r.id.equals("watchnext")).findFirst().get().members);
        JSONObject again=HomeIntentCodec.export(destination,newLibrary);JSONArray refs=again.getJSONArray("rows").getJSONObject(0).getJSONArray("members");assertEquals(2,refs.length());assertTrue(refs.toString().contains("unresolved:"));assertTrue(refs.toString().contains("tmdb:movie:123"));
    }
    @Test public void homeArchiveNeverOwnsWatchNextMembershipAndPresentEmptyDoesNotClearIt()throws Exception{
        Context source=scope("source"),destination=scope("destination");PreviewHomeRows home=new PreviewHomeRows(source);for(var row:home.rows)if(row.id.equals("watchnext"))row.members.add("v5");home.save();
        JSONObject payload=HomeIntentCodec.export(source,null);for(int i=0;i<payload.getJSONArray("rows").length();i++){JSONObject row=payload.getJSONArray("rows").getJSONObject(i);if(row.getString("id").equals("watchnext"))assertEquals(0,row.getJSONArray("members").length());}
        PreviewHomeRows target=new PreviewHomeRows(destination);for(var row:target.rows)if(row.id.equals("watchnext"))row.members.add("s8");target.save();HomeIntentCodec.Prepared empty=HomeIntentCodec.prepare(destination,new JSONObject().put("schema",1).put("rows",new JSONArray()),null);assertTrue(empty.rows.contains("s8"));assertEquals(0,empty.unresolved);
    }
    @Test public void unsafeUserLabelOrInjectedMembershipBlocksExportOrPreparation()throws Exception{
        Context source=scope("source");PreviewHomeRows home=new PreviewHomeRows(source);home.rows.add(new PreviewHomeRows.Row("custom:test","smb://person:CANARY@host/private"));home.save();try{HomeIntentCodec.export(source,null);fail();}catch(JSONException expected){}
        home.rows.remove(home.rows.size()-1);home.save();JSONObject payload=HomeIntentCodec.export(source,null);payload.getJSONArray("rows").getJSONObject(0).getJSONArray("members").put("file:///private/CANARY");try{HomeIntentCodec.prepare(scope("target"),payload,null);fail();}catch(JSONException expected){}
    }
    @Test public void customOnlyDefinitionsCannotClearUnselectedQueueAndSemanticGenreSlashIsPortable()throws Exception{
        Context source=scope("source"),target=scope("target");PreviewHomeRows home=new PreviewHomeRows(source);PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:crime","Crime / Drama");row.genreRule="Crime / Drama";home.rows.add(0,row);home.save();
        JSONObject exported=HomeIntentCodec.export(source,null);JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray().put(exported.getJSONArray("rows").getJSONObject(0)));
        PreviewHomeRows destination=new PreviewHomeRows(target);for(var current:destination.rows)if(current.id.equals("watchnext"))current.members.add("v99");destination.save();
        HomeIntentCodec.Prepared prepared=HomeIntentCodec.prepare(target,payload,null);PreferenceManager.getDefaultSharedPreferences(target).edit().putString("preview_home_rows41",prepared.rows).commit();PreviewHomeRows restored=new PreviewHomeRows(target);
        assertEquals("Crime / Drama",restored.rows.get(0).genreRule);assertEquals(Set.of("v99"),restored.rows.stream().filter(current->current.id.equals("watchnext")).findFirst().get().members);
        payload.getJSONArray("rows").getJSONObject(0).put("genres","/private/path");try{HomeIntentCodec.validate(payload);fail();}catch(JSONException expected){}
    }
}
