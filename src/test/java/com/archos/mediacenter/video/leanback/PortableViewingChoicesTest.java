package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.utils.backup.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableViewingChoicesTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    private Entry movie(long local,long online){Movie video=mock(Movie.class);when(video.getId()).thenReturn(local);when(video.getOnlineId()).thenReturn(online);when(video.getFilePath()).thenReturn("smb://person:AUTH_CANARY@host/private/FILE_CANARY.mkv");return new Entry(video,0,0,"");}
    private PreviewHomeRows.Row queue(PreviewHomeRows home){return home.rows.stream().filter(r->r.id.equals("watchnext")).findFirst().get();}
    private JSONObject choices(String kind,JSONArray items)throws Exception{return new JSONObject().put("schema",1).put("kind",kind).put("choices",items);}
    @Test public void queueReplacementPreservesHomeAndViewingStateWhilePendingMembershipSurvivesReexport()throws Exception{
        Context source=scope("source"),target=scope("target");PreviewHomeRows home=new PreviewHomeRows(source);queue(home).members.addAll(List.of("v10","v11"));home.save();
        Snapshot old=new Snapshot();old.movies.add(movie(10,123));JSONObject payload=PortableViewingChoices.export(source,old,PortableViewingChoices.Kind.WATCH_NEXT);assertFalse(payload.toString().contains("CANARY"));assertFalse(payload.toString().contains("smb:"));
        PreviewHomeRows destination=new PreviewHomeRows(target);destination.rows.add(0,new PreviewHomeRows.Row("custom:keep","Keep Home"));queue(destination).members.add("s99");queue(destination).visible=false;destination.save();
        var prefs=PreferenceManager.getDefaultSharedPreferences(target);prefs.edit().putLong("preview_cw_dismiss:v5",42).putString("history_canary","KEEP").putInt("resume_canary",2345).putString("oauth_token","AUTH_CANARY").commit();Map<String,?> before=prefs.getAll();
        Snapshot current=new Snapshot();current.movies.add(movie(700,123));var transaction=new PreferenceRestoreTransaction(target);var plan=transaction.prepare(Map.of(BackupCategory.WATCH_NEXT,payload),current);assertEquals(before,prefs.getAll());assertEquals(1,plan.unresolved);transaction.apply(plan,()->false);
        PreviewHomeRows restored=new PreviewHomeRows(target);assertEquals("custom:keep",restored.rows.get(0).id);assertFalse(queue(restored).visible);assertEquals(Set.of("v700"),queue(restored).members);
        assertEquals(42,prefs.getLong("preview_cw_dismiss:v5",0));assertEquals("KEEP",prefs.getString("history_canary",null));assertEquals(2345,prefs.getInt("resume_canary",0));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
        JSONObject again=PortableViewingChoices.export(target,current,PortableViewingChoices.Kind.WATCH_NEXT);assertEquals(2,again.getJSONArray("choices").length());assertTrue(again.toString().contains("unresolved:"));
        JSONObject empty=choices("WATCH_NEXT",new JSONArray());transaction.apply(transaction.prepare(Map.of(BackupCategory.WATCH_NEXT,empty),current),()->false);assertTrue(queue(new PreviewHomeRows(target)).members.isEmpty());assertEquals("custom:keep",new PreviewHomeRows(target).rows.get(0).id);assertEquals(0,PortableViewingChoices.export(target,current,PortableViewingChoices.Kind.WATCH_NEXT).getJSONArray("choices").length());
    }
    @Test public void homeAndQueueRestoreAreIndependentAndComposeRegardlessOfSelectionMapOrder()throws Exception{
        Context source=scope("source"),target=scope("target");PreviewHomeRows sourceRows=new PreviewHomeRows(source);sourceRows.rows.add(0,new PreviewHomeRows.Row("custom:new","New Home"));sourceRows.save();JSONObject home=HomeIntentCodec.export(source,null);
        PreviewHomeRows receiving=new PreviewHomeRows(target);queue(receiving).members.add("v99");receiving.save();Snapshot current=new Snapshot();current.movies.add(movie(700,123));
        JSONObject queue=choices("WATCH_NEXT",new JSONArray().put("tmdb:movie:123"));Map<BackupCategory,JSONObject> selected=new LinkedHashMap<>();selected.put(BackupCategory.WATCH_NEXT,queue);selected.put(BackupCategory.HOME,home);
        var transaction=new PreferenceRestoreTransaction(target);transaction.apply(transaction.prepare(selected,current),()->false);PreviewHomeRows restored=new PreviewHomeRows(target);assertEquals("custom:new",restored.rows.get(0).id);assertEquals(Set.of("v700"),queue(restored).members);
        transaction.apply(transaction.prepare(Map.of(BackupCategory.HOME,new JSONObject().put("schema",1).put("rows",new JSONArray())),current),()->false);assertEquals(Set.of("v700"),queue(new PreviewHomeRows(target)).members);
    }
    @Test public void continueDismissalsRemapWithoutHistoryOrPositionWritesAndEmptyClearsOnlyDismissals()throws Exception{
        Context source=scope("source"),target=scope("target");var sourcePrefs=PreferenceManager.getDefaultSharedPreferences(source);sourcePrefs.edit().putLong("preview_cw_dismiss:v10",12345).putLong("preview_cw_dismiss:v11",54321).putString("oauth_token","AUTH_CANARY").commit();Snapshot old=new Snapshot();old.movies.add(movie(10,321));JSONObject payload=PortableViewingChoices.export(source,old,PortableViewingChoices.Kind.CONTINUE_CHOICES);assertFalse(payload.toString().contains("CANARY"));
        var prefs=PreferenceManager.getDefaultSharedPreferences(target);prefs.edit().putLong("preview_cw_dismiss:v99",5).putInt("resume_canary",123).putBoolean("watched_canary",true).commit();PreviewHomeRows home=new PreviewHomeRows(target);queue(home).members.add("v77");home.save();String savedHome=prefs.getString("preview_home_rows41",null);
        Snapshot current=new Snapshot();current.movies.add(movie(700,321));var transaction=new PreferenceRestoreTransaction(target);var plan=transaction.prepare(Map.of(BackupCategory.CONTINUE_CHOICES,payload),current);assertEquals(1,plan.unresolved);transaction.apply(plan,()->false);
        assertFalse(prefs.contains("preview_cw_dismiss:v99"));assertEquals(12345,prefs.getLong("preview_cw_dismiss:v700",0));assertEquals(savedHome,prefs.getString("preview_home_rows41",null));assertEquals(123,prefs.getInt("resume_canary",0));assertTrue(prefs.getBoolean("watched_canary",false));
        assertEquals(2,PortableViewingChoices.export(target,current,PortableViewingChoices.Kind.CONTINUE_CHOICES).getJSONArray("choices").length());
        transaction.apply(transaction.prepare(Map.of(BackupCategory.CONTINUE_CHOICES,choices("CONTINUE_CHOICES",new JSONArray())),current),()->false);assertFalse(prefs.contains("preview_cw_dismiss:v700"));assertEquals(savedHome,prefs.getString("preview_home_rows41",null));assertEquals(0,PortableViewingChoices.export(target,current,PortableViewingChoices.Kind.CONTINUE_CHOICES).getJSONArray("choices").length());
    }
    @Test public void unknownReferencesRemainPendingUntilExactProviderIdentityAppearsAndUnsafeChoicesAreRejected()throws Exception{
        Context target=scope("target");JSONObject payload=choices("WATCH_NEXT",new JSONArray().put("tmdb:movie:999"));var transaction=new PreferenceRestoreTransaction(target);
        transaction.apply(transaction.prepare(Map.of(BackupCategory.WATCH_NEXT,payload),new Snapshot()),()->false);assertTrue(queue(new PreviewHomeRows(target)).members.isEmpty());JSONObject pending=PortableViewingChoices.export(target,new Snapshot(),PortableViewingChoices.Kind.WATCH_NEXT);assertEquals(payload.toString(),pending.toString());
        Snapshot populated=new Snapshot();populated.movies.add(movie(88,999));transaction.apply(transaction.prepare(Map.of(BackupCategory.WATCH_NEXT,pending),populated),()->false);assertEquals(Set.of("v88"),queue(new PreviewHomeRows(target)).members);
        for(JSONObject bad:List.of(choices("WATCH_NEXT",new JSONArray().put("smb://host/FILE_CANARY")),choices("WATCH_NEXT",new JSONArray().put("tmdb:movie:999").put("tmdb:movie:999")),choices("CONTINUE_CHOICES",new JSONArray().put(new JSONObject().put("ref","tmdb:movie:999").put("dismissedAt",1.5)))))try{PortableViewingChoices.validate(bad,bad.getString("kind").equals("WATCH_NEXT")?PortableViewingChoices.Kind.WATCH_NEXT:PortableViewingChoices.Kind.CONTINUE_CHOICES);fail();}catch(JSONException expected){}
    }
}
