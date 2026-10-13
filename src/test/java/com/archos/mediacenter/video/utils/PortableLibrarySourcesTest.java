package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableLibrarySourcesTest {
    private Context app;private SharedPreferences prefs;private File journal;
    private static final String ROOT="smb://nas.example/SHARE_CANARY/",EXCLUSION="file:///storage/FOLDER_CANARY";
    private static final class Death extends Error{}
    @Before public void setup() {
        app=RuntimeEnvironment.getApplication();prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().commit();
        prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of(ROOT,"putio:///KEEP_OWNERSHIP"))
            .putStringSet(PortableLibrarySources.EXCLUSIONS,Set.of(EXCLUSION)).putString("oauth_token","AUTH_CANARY")
            .putBoolean("supernova_onboarding_complete",false).putBoolean("supernova_library_policy_initialized",true).commit();
        journal=new File(app.getFilesDir(),"supernova-restore/preferences-v1.json");journal.delete();new File(journal+".bak").delete();
    }
    private JSONObject empty()throws Exception{return new JSONObject().put("schema",1).put("roots",new JSONArray()).put("exclusions",new JSONArray());}
    @Test public void exportWithoutLocationsContainsOnlyReferencesAndExcludesPutioAndAccessMaterial()throws Exception {
        JSONObject sources=PortableLibrarySources.export(app);String text=sources.toString();
        for(String secret:List.of("CANARY","smb:","file:","putio","OWNERSHIP","oauth_token"))assertFalse(text.contains(secret));
        assertEquals(1,sources.getJSONArray("roots").length());assertEquals(1,sources.getJSONArray("exclusions").length());
        assertEquals(Set.of(ROOT,EXCLUSION),PortableLibrarySources.locations(app));
        File archive=new File(app.getCacheDir(),"private-sources.zip");LogicalBackupArchive.writeCategories(archive,Map.of(BackupCategory.SOURCES,sources),()->false);
        assertEquals(sources.toString(),LogicalBackupArchive.readCategories(archive,()->false).get(BackupCategory.SOURCES).toString());assertFalse(LogicalBackupArchive.readCategories(archive,()->false).containsKey(BackupCategory.LOCATIONS));
    }
    @Test public void unknownPathsRemainDurablePendingAndCanBeBackedUpAgainWithoutGuessing()throws Exception {
        JSONObject source=PortableLibrarySources.export(app);prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of("putio:///KEEP_OWNERSHIP")).putStringSet(PortableLibrarySources.EXCLUSIONS,Set.of()).commit();
        var transaction=new PreferenceRestoreTransaction(app);var plan=transaction.prepare(Map.of(BackupCategory.SOURCES,source),null);assertEquals(2,plan.unresolved);transaction.apply(plan,()->false);
        assertEquals(Set.of("putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));assertEquals(Set.of(),prefs.getStringSet(PortableLibrarySources.EXCLUSIONS,null));
        assertEquals(source.toString(),PortableLibrarySources.export(app).toString());assertFalse(prefs.getBoolean("supernova_onboarding_complete",true));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void explicitSelectedLocationsRecreateSourcesButDoNotGrantPermissionsOrActivateSetup()throws Exception {
        JSONObject source=PortableLibrarySources.export(app),locations=PortableLocations.export(PortableLibrarySources.locations(app));
        prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of("putio:///KEEP_OWNERSHIP")).putStringSet(PortableLibrarySources.EXCLUSIONS,Set.of()).commit();
        File archive=new File(app.getCacheDir(),"with-locations.zip");LogicalBackupArchive.writeCategories(archive,Map.of(BackupCategory.SOURCES,source,BackupCategory.LOCATIONS,locations),()->false);
        var read=LogicalBackupArchive.readCategories(archive,()->false);var transaction=new PreferenceRestoreTransaction(app);var plan=transaction.prepare(read,null);assertEquals(0,plan.unresolved);transaction.apply(plan,()->false);
        assertEquals(Set.of(ROOT,"putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));assertEquals(Set.of(EXCLUSION),prefs.getStringSet(PortableLibrarySources.EXCLUSIONS,null));
        assertFalse(prefs.getBoolean("supernova_onboarding_complete",true));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void exactAlreadyConfiguredSourcesResolveWithoutOptingIntoLocatorText()throws Exception {
        var transaction=new PreferenceRestoreTransaction(app);var plan=transaction.prepare(Map.of(BackupCategory.SOURCES,PortableLibrarySources.export(app)),null);assertEquals(0,plan.unresolved);transaction.apply(plan,()->false);
        assertEquals(Set.of(ROOT,"putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));
    }
    @Test public void presentEmptySourcesClearOnlyOwnedGenericChoicesAndOmittedCategoryDoesNothing()throws Exception {
        var transaction=new PreferenceRestoreTransaction(app);transaction.apply(transaction.prepare(Map.of(BackupCategory.LOCATIONS,PortableLocations.export(List.of())),null),()->false);
        assertEquals(Set.of(ROOT,"putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));
        transaction.apply(transaction.prepare(Map.of(BackupCategory.SOURCES,empty()),null),()->false);
        assertEquals(Set.of("putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));assertTrue(prefs.getStringSet(PortableLibrarySources.EXCLUSIONS,null).isEmpty());assertTrue(prefs.getBoolean("supernova_library_policy_initialized",false));
    }
    @Test public void crashAtUnfinishedPreferenceBoundariesRecoversOriginalSourcesAndAuthentication()throws Exception {
        var transaction=new PreferenceRestoreTransaction(app);for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)) {
            try{transaction.apply(transaction.prepare(Map.of(BackupCategory.SOURCES,empty()),null),()->false,at->{if(at==boundary)throw new Death();});fail();}catch(Death expected){}
            assertFalse(Files.readString(journal.toPath()).contains("AUTH_CANARY"));assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());
            assertEquals(Set.of(ROOT,"putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));assertEquals(Set.of(EXCLUSION),prefs.getStringSet(PortableLibrarySources.EXCLUSIONS,null));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
        }
    }
    @Test public void completeSourceReplacementWinsOverLateCancellation()throws Exception {
        var transaction=new PreferenceRestoreTransaction(app);try{transaction.apply(transaction.prepare(Map.of(BackupCategory.SOURCES,empty()),null),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMPLETION_DURABLE)throw new Death();});fail();}catch(Death expected){}
        assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,transaction.recover());assertEquals(Set.of("putio:///KEEP_OWNERSHIP"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));
    }
    @Test public void staleSourcePreviewCannotReplaceLaterManualSelection()throws Exception {
        var transaction=new PreferenceRestoreTransaction(app);var plan=transaction.prepare(Map.of(BackupCategory.SOURCES,empty()),null);prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of("file:///later")).commit();
        try{transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertFalse(journal.exists());assertEquals(Set.of("file:///later"),prefs.getStringSet(PortableLibrarySources.ROOTS,null));
    }
    @Test public void positiveSchemasRejectExtraFieldsWrongReferencesAndConflictingLocations()throws Exception {
        JSONObject sources=PortableLibrarySources.export(app);sources.put("account","CANARY");try{PortableLibrarySources.validate(sources);fail();}catch(JSONException expected){}
        JSONObject locations=PortableLocations.export(List.of(ROOT));locations.getJSONArray("rows").getJSONObject(0).put("uri","smb://other.example/share");try{PortableLocations.validate(locations);fail();}catch(JSONException expected){}
        sources=empty();sources.getJSONArray("roots").put(ROOT);try{PortableLibrarySources.validate(sources);fail();}catch(JSONException expected){}
        locations=PortableLocations.export(List.of(ROOT));locations.getJSONArray("rows").put(locations.getJSONArray("rows").getJSONObject(0));try{PortableLocations.validate(locations);fail();}catch(JSONException expected){}
    }
    @Test public void credentialOrQueryBearingLocationsRejectBeforeExportAndErrorsDoNotEchoThem()throws Exception {
        for(String unsafe:List.of("smb://user:AUTH_CANARY@server/share","smb://user%40server/share","https://server/file?token=AUTH_CANARY","https://server/file#AUTH_CANARY","putio:///account/123","file:///../secret","file:///path%0aAUTH_CANARY","file:///path%5cAUTH_CANARY","relative/path","https://server:bad/path")) {
            try{PortableLocations.locator(unsafe);fail(unsafe);}catch(JSONException expected){assertFalse(expected.getMessage().contains("CANARY"));}
        }
        prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of("smb://user:AUTH_CANARY@server/share")).commit();try{PortableLibrarySources.export(app);fail();}catch(JSONException expected){}assertFalse(journal.exists());
    }
    @Test public void localSpacesDotSegmentsAndHostCaseHaveStableLocationIdentity()throws Exception {
        assertEquals("file:///storage/Media%20Files",PortableLocations.locator("/storage/Media Files"));
        assertEquals(PortableLocations.reference("smb://NAS.EXAMPLE/a/./b/../c"),PortableLocations.reference("smb://nas.example/a/c"));
        assertEquals(1,PortableLocations.validate(PortableLocations.export(List.of("smb://NAS.EXAMPLE/a/./c","smb://nas.example/a/c"))).size());
    }
    @Test public void supportedWebdavRoutesRoundtripButLossyUnicodeEncodedSeparatorsAndInvalidPortsReject()throws Exception {
        for(String scheme:List.of("webdav","webdavs","dav","davs")){String uri=scheme+"://nas.example:8443/Media";assertEquals(uri,PortableLocations.validate(PortableLocations.export(List.of(uri))).values().iterator().next());}
        for(String unsafe:List.of("file:///a%2Fb","smb://nas.example:65536/share","smb://nas.example:0/share",new String(new char[]{'/','x','\ud800'})))try{PortableLocations.locator(unsafe);fail();}catch(JSONException expected){}
    }
}
