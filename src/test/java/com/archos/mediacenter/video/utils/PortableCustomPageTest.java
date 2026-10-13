package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.CustomLibraryPage;
import com.archos.mediacenter.video.utils.backup.PortableCustomPage;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableCustomPageTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    @Test public void completePageFiltersRoundTripAndFranchiseIsNotLegacyCollections()throws Exception{
        Context source=scope("source"),target=scope("target");CustomLibraryPage page=new CustomLibraryPage();page.name="Crime / Drama";page.icon="TV Shows";page.type="tv";page.genres.addAll(Set.of("Crime","Drama"));page.allGenres=true;page.watched="progress";page.year=2025;page.rating=7.5;page.language="eng";page.studio="Studio";page.collection="Provider franchise";page.view="list";page.sort="added";page.descending=true;assertTrue(page.save(source));
        JSONObject payload=PortableCustomPage.export(source);assertFalse(payload.getJSONObject("page").has("collection"));assertEquals("Provider franchise",payload.getJSONObject("page").getString("franchise"));
        var targetPrefs=PreferenceManager.getDefaultSharedPreferences(target);targetPrefs.edit().putString("auth_token","CANARY").putString("preview_library_1_genre","Comedy").commit();Map<String,?> before=targetPrefs.getAll();SharedPreferences.Editor staged=PortableCustomPage.replace(target,payload);assertEquals(before,targetPrefs.getAll());assertTrue(staged.commit());assertEquals(page.encode(),CustomLibraryPage.load(target).encode());assertEquals("CANARY",targetPrefs.getString("auth_token",""));assertEquals("Comedy",targetPrefs.getString("preview_library_1_genre",""));
        PortableCustomPage.replace(target,new JSONObject().put("schema",1).put("page",JSONObject.NULL)).commit();assertNull(CustomLibraryPage.load(target));assertTrue(targetPrefs.contains("auth_token"));assertEquals("Comedy",targetPrefs.getString("preview_library_1_genre",""));
    }
    @Test public void injectedCredentialLocationOrLegacyCollectionPayloadIsRejectedBeforeMutation()throws Exception{
        Context source=scope("source"),target=scope("target");CustomLibraryPage page=new CustomLibraryPage();assertTrue(page.save(source));JSONObject good=PortableCustomPage.export(source);var prefs=PreferenceManager.getDefaultSharedPreferences(target);Map<String,?> before=prefs.getAll();
        for(String key:List.of("credentials","user_collection","putio")){JSONObject bad=new JSONObject(good.toString());bad.getJSONObject("page").put(key,"CANARY");try{PortableCustomPage.replace(target,bad);fail();}catch(JSONException expected){}assertEquals(before,prefs.getAll());}
        JSONObject bad=new JSONObject(good.toString());bad.getJSONObject("page").put("name","smb://person:CANARY@host/share");try{PortableCustomPage.replace(target,bad);fail();}catch(JSONException expected){}assertEquals(before,prefs.getAll());
    }
}
