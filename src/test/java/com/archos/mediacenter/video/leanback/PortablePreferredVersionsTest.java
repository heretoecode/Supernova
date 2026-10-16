package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.utils.backup.PortablePhysicalReferences;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortablePreferredVersionsTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    private Movie movie(long id,long provider,String path){Movie movie=mock(Movie.class);when(movie.getId()).thenReturn(id);when(movie.getOnlineId()).thenReturn(provider);when(movie.getFilePath()).thenReturn(path);when(movie.getFileUri()).thenReturn(Uri.parse(path));when(movie.getName()).thenReturn("Fixture");return movie;}
    private void apply(Context context,PortablePreferredVersions.Prepared prepared){var editor=PreferenceManager.getDefaultSharedPreferences(context).edit();for(String key:prepared.keys)editor.remove(key);for(var entry:prepared.values.entrySet())editor.putString(entry.getKey(),(String)entry.getValue());assertTrue(editor.commit());}
    @Test public void actualManualChoiceRoundTripsOnlyWithValidatedMatchingAndPreservesUnrelatedState()throws Exception{
        Context source=scope("source"),target=scope("target");var original=movie(10,123,"file:///storage/FILE_CANARY.mkv");PreviewVariants.remember(source,original);JSONObject payload=PortablePreferredVersions.export(source,new PortablePhysicalReferences(source,List.of(original)));assertFalse(payload.toString().contains("CANARY"));assertFalse(payload.toString().contains("/storage"));
        var incoming=movie(700,123,"file:///storage/FILE_CANARY.mkv");var references=new PortablePhysicalReferences(target,List.of(incoming));var prefs=PreferenceManager.getDefaultSharedPreferences(target);prefs.edit().putString("oauth_token","AUTH_CANARY").putInt("resume_canary",333).commit();Map<String,?> before=prefs.getAll();
        var blocked=PortablePreferredVersions.prepare(target,payload,references);assertEquals(1,blocked.unresolved);assertFalse(blocked.values.containsKey("preview_version_choice:movie:123"));assertEquals(before,prefs.getAll());
        var prepared=PortablePreferredVersions.prepare(target,payload,references,true);assertEquals(0,prepared.unresolved);apply(target,prepared);assertEquals("700|file:///storage/FILE_CANARY.mkv",prefs.getString("preview_version_choice:movie:123",null));assertSame(incoming,PreviewVariants.choose(target,List.of(movie(900,123,"file:///storage/other.mkv"),incoming)));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));assertEquals(333,prefs.getInt("resume_canary",0));
    }
    @Test public void changedMatchPathAndCredentialBearingUriRetainPendingIntentWithoutGuessing()throws Exception{
        Context source=scope("source"),target=scope("target");var original=movie(10,123,"file:///storage/original.mkv");PreviewVariants.remember(source,original);JSONObject payload=PortablePreferredVersions.export(source,new PortablePhysicalReferences(source,List.of(original)));
        for(Movie incoming:List.of(movie(700,456,"file:///storage/original.mkv"),movie(700,123,"file:///new-storage/original.mkv"))){
            var references=new PortablePhysicalReferences(target,List.of(incoming));var prepared=PortablePreferredVersions.prepare(target,payload,references,true);assertEquals(1,prepared.unresolved);apply(target,prepared);assertEquals(payload.toString(),PortablePreferredVersions.export(target,references).toString());assertFalse(PreferenceManager.getDefaultSharedPreferences(target).contains("preview_version_choice:movie:123"));
        }
        var secret=movie(11,456,"smb://person:AUTH_CANARY@host/FILE_CANARY.mkv?token=AUTH_CANARY");PreviewVariants.remember(source,secret);JSONObject privatePayload=PortablePreferredVersions.export(source,new PortablePhysicalReferences(source,List.of(original,secret)));assertFalse(privatePayload.toString().contains("CANARY"));assertTrue(privatePayload.toString().contains("unresolved:"));
    }
    @Test public void newManualChoiceSupersedesOnlyItsOwnPendingParent()throws Exception{
        Context source=scope("source"),target=scope("target");var original=movie(10,123,"file:///storage/a.mkv");var other=movie(11,456,"file:///storage/b.mkv");PreviewVariants.remember(source,original);PreviewVariants.remember(source,other);JSONObject payload=PortablePreferredVersions.export(source,new PortablePhysicalReferences(source,List.of(original,other)));apply(target,PortablePreferredVersions.prepare(target,payload,new PortablePhysicalReferences(target,List.of())));
        var replacement=movie(700,123,"file:///new-storage/new-version.mkv");PreviewVariants.remember(target,replacement);JSONObject pending=new JSONObject(PreferenceManager.getDefaultSharedPreferences(target).getString(PortablePreferredVersions.PENDING,null));assertEquals(1,pending.getJSONArray("rows").length());assertEquals("tmdb:movie:456",pending.getJSONArray("rows").getJSONObject(0).getString("expected"));
        JSONObject again=PortablePreferredVersions.export(target,new PortablePhysicalReferences(target,List.of(replacement)));assertEquals(2,again.getJSONArray("rows").length());assertFalse(again.toString().contains(payload.getJSONArray("rows").getJSONObject(0).getString("physical"))&&payload.getJSONArray("rows").getJSONObject(0).getString("expected").equals("tmdb:movie:123"));
    }
    @Test public void presentEmptyClearsOnlyVersionChoicesAndConflictingOrInjectedPayloadsReject()throws Exception{
        Context target=scope("target");var prefs=PreferenceManager.getDefaultSharedPreferences(target);prefs.edit().putString("preview_version_choice:movie:123","10|file:///storage/a.mkv").putString("sort_canary","KEEP").commit();JSONObject empty=new JSONObject().put("schema",1).put("rows",new JSONArray());apply(target,PortablePreferredVersions.prepare(target,empty,new PortablePhysicalReferences(target,List.of())));assertFalse(prefs.contains("preview_version_choice:movie:123"));assertEquals("KEEP",prefs.getString("sort_canary",null));
        JSONObject first=new JSONObject().put("physical","file:"+"a".repeat(64)).put("expected","tmdb:movie:123"),second=new JSONObject().put("physical","file:"+"b".repeat(64)).put("expected","tmdb:movie:123");
        for(JSONObject bad:List.of(new JSONObject().put("schema",1).put("rows",new JSONArray().put(first).put(second)),new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject(first.toString()).put("uri","smb://AUTH_CANARY@host/private"))),new JSONObject().put("schema",1.5).put("rows",new JSONArray())))try{PortablePreferredVersions.validate(bad);fail();}catch(JSONException expected){}
    }
}
