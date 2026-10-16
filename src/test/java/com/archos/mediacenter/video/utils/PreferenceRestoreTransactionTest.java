package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediacenter.video.leanback.CustomLibraryPage;
import com.archos.mediacenter.video.leanback.PortableReviewNotes;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import android.net.Uri;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreferenceRestoreTransactionTest {
    private Application app;private SharedPreferences prefs;private PreferenceRestoreTransaction transaction;private File journal;
    private static class ProcessDeath extends Error {}
    @Before public void setup()throws Exception{
        app=RuntimeEnvironment.getApplication();prefs=PreferenceManager.getDefaultSharedPreferences(app);
        transaction=new PreferenceRestoreTransaction(app);journal=new File(app.getFilesDir(),"supernova-restore/preferences-v1.json");
        transaction.recover();prefs.edit().clear().putInt("preview_accent41",7).putBoolean("enable_sponsor",true).putBoolean("supernova_diagnostic_logging",true).putString("oauth_token","AUTH_CANARY").putString("unselected_custom","KEEP").commit();
    }
    private JSONObject appearance()throws Exception{
        return new JSONObject().put("schema",1).put("category","APPEARANCE").put("values",new JSONObject().put("preview_accent41",new JSONObject().put("type","INT").put("value",19)));
    }
    private Map<BackupCategory,JSONObject> selected()throws Exception{return new EnumMap<>(Map.of(BackupCategory.APPEARANCE,appearance()));}
    private void untouched(){assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));assertEquals("KEEP",prefs.getString("unselected_custom",null));assertTrue(prefs.getBoolean("supernova_diagnostic_logging",false));}
    @Test public void selectedReplacementClearsOnlyOwnedKeysAndPreservesMissingCategories()throws Exception{
        var plan=transaction.prepare(selected(),null);assertEquals(7,prefs.getInt("preview_accent41",0));assertEquals(Set.of(BackupCategory.APPEARANCE),plan.categories);
        transaction.apply(plan,()->false);assertEquals(19,prefs.getInt("preview_accent41",0));assertFalse(prefs.contains("enable_sponsor"));untouched();assertFalse(journal.exists());assertEquals(PreferenceRestoreTransaction.Recovery.NONE,transaction.recover());
        JSONObject empty=new JSONObject().put("schema",1).put("category","APPEARANCE").put("values",new JSONObject());
        transaction.apply(transaction.prepare(Map.of(BackupCategory.APPEARANCE,empty),null),()->false);assertFalse(prefs.contains("preview_accent41"));untouched();
    }
    @Test public void processDeathBeforeAndAfterPreferenceCommitRecoversExactOriginals()throws Exception{
        for(var checkpoint:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            var plan=transaction.prepare(selected(),null);
            try{transaction.apply(plan,()->false,at->{if(at==checkpoint)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            assertTrue(journal.exists());assertFalse(Files.readString(journal.toPath()).contains("AUTH_CANARY"));
            assertEquals(checkpoint==PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE?7:19,prefs.getInt("preview_accent41",0));
            assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());
            assertEquals(7,prefs.getInt("preview_accent41",0));assertTrue(prefs.getBoolean("enable_sponsor",false));untouched();assertFalse(journal.exists());
        }
    }
    @Test public void durableCompletionSurvivesProcessDeathAndLateCancellation()throws Exception{
        try{transaction.apply(transaction.prepare(selected(),null),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMPLETION_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,new PreferenceRestoreTransaction(app).recover());assertEquals(19,prefs.getInt("preview_accent41",0));untouched();
        final boolean[] late={false};transaction.apply(transaction.prepare(selected(),null),()->late[0],at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMPLETION_DURABLE)late[0]=true;});assertFalse(journal.exists());
    }
    @Test public void cancellationAndFailureBeforeCompletionRollBackWithoutTouchingAuth()throws Exception{
        final boolean[] cancel={false};
        try{transaction.apply(transaction.prepare(selected(),null),()->cancel[0],at->{if(at==PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)cancel[0]=true;});fail();}catch(InterruptedIOException expected){}
        assertEquals(7,prefs.getInt("preview_accent41",0));assertTrue(prefs.getBoolean("enable_sponsor",false));untouched();assertFalse(journal.exists());
        try{transaction.apply(transaction.prepare(selected(),null),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)throw new IOException("fixture failure");});fail();}catch(IOException expected){}
        assertEquals(7,prefs.getInt("preview_accent41",0));untouched();assertFalse(journal.exists());
    }
    @Test public void stalePreviewOrPendingRecoveryCannotOverwriteLaterIntent()throws Exception{
        var stale=transaction.prepare(selected(),null);prefs.edit().putInt("preview_accent41",42).commit();
        try{transaction.apply(stale,()->false);fail();}catch(IOException expected){}assertEquals(42,prefs.getInt("preview_accent41",0));assertFalse(journal.exists());
        var plan=transaction.prepare(selected(),null);
        try{transaction.apply(plan,()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        byte[] original=Files.readAllBytes(journal.toPath());try{transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertArrayEquals(original,Files.readAllBytes(journal.toPath()));transaction.recover();assertEquals(42,prefs.getInt("preview_accent41",0));
    }
    @Test public void invalidOrUnregisteredCategoryIsRejectedBeforeJournalAndEmptyPageIsIndependent()throws Exception{
        Map<BackupCategory,JSONObject> mixed=selected();mixed.put(BackupCategory.HISTORY,new JSONObject());
        try{transaction.prepare(mixed,null);fail();}catch(JSONException expected){}assertEquals(7,prefs.getInt("preview_accent41",0));assertFalse(journal.exists());
        mixed=selected();mixed.put(BackupCategory.DIAGNOSTICS,new JSONObject().put("schema",1).put("category","DIAGNOSTICS").put("values",new JSONObject().put("oauth_token",new JSONObject().put("type","CHOICE").put("value","CANARY"))));
        try{transaction.prepare(mixed,null);fail();}catch(JSONException expected){}assertFalse(journal.exists());untouched();
        prefs.edit().putString(CustomLibraryPage.KEY,"existing page").commit();
        transaction.apply(transaction.prepare(Map.of(BackupCategory.CUSTOM_PAGES,new JSONObject().put("schema",1).put("page",JSONObject.NULL)),null),()->false);
        assertFalse(prefs.contains(CustomLibraryPage.KEY));assertEquals(7,prefs.getInt("preview_accent41",0));untouched();
    }
    @Test public void corruptJournalDoesNotPartiallyRecoverOrDeleteEvidence()throws Exception{
        try{transaction.apply(transaction.prepare(selected(),null),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        JSONObject record=new JSONObject(Files.readString(journal.toPath()));record.getJSONArray("values").put(new JSONObject().put("key","oauth_token").put("present",true).put("type","string").put("value","OVERWRITE"));Files.writeString(journal.toPath(),record.toString());
        try{transaction.recover();fail();}catch(IOException expected){}assertTrue(journal.exists());assertEquals(19,prefs.getInt("preview_accent41",0));untouched();
        journal.delete();
    }
    private Movie movie(long id){Movie video=mock(Movie.class);when(video.getId()).thenReturn(id);when(video.getOnlineId()).thenReturn(123L);when(video.getFileUri()).thenReturn(Uri.parse("file:///storage/fixture.mkv"));when(video.getFilePath()).thenReturn("file:///storage/fixture.mkv");return video;}
    @Test public void twoStoreProcessDeathAtEachCommitRecoversOriginalsWithoutHealthCacheOrAuthInJournal()throws Exception{
        var health=app.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);var physical=new PortablePhysicalReferences(app,List.of(movie(10)));String ref=physical.reference(10);
        JSONObject review=new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("physical",ref).put("expected","tmdb:movie:123").put("incorrect",true)));
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE)){
            health.edit().putBoolean("incorrect:10",false).putBoolean("incorrect:99",true).putString("offline:source","PATH_CANARY").putString("media:10","CACHE_CANARY").commit();Map<BackupCategory,JSONObject> selected=selected();selected.put(BackupCategory.REVIEW,review);var plan=transaction.prepare(selected,null,physical);
            try{transaction.apply(plan,()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            assertEquals(19,prefs.getInt("preview_accent41",0));assertEquals(boundary==PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,health.getBoolean("incorrect:10",false));String record=Files.readString(journal.toPath());assertFalse(record.contains("AUTH_CANARY"));assertFalse(record.contains("PATH_CANARY"));assertFalse(record.contains("CACHE_CANARY"));
            assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());assertEquals(7,prefs.getInt("preview_accent41",0));assertFalse(health.getBoolean("incorrect:10",true));assertTrue(health.getBoolean("incorrect:99",false));assertEquals("PATH_CANARY",health.getString("offline:source",null));untouched();
        }
    }
    @Test public void durableTwoStoreCompletionAndPresentEmptyReviewAreIndependentOfDefaultPreferences()throws Exception{
        var health=app.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);health.edit().putBoolean("incorrect:10",true).putString("offline:source","KEEP").commit();JSONObject empty=new JSONObject().put("schema",1).put("rows",new JSONArray());Map<BackupCategory,JSONObject> selected=selected();selected.put(BackupCategory.REVIEW,empty);var plan=transaction.prepare(selected,null,new PortablePhysicalReferences(app,List.of()));
        try{transaction.apply(plan,()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMPLETION_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,transaction.recover());assertEquals(19,prefs.getInt("preview_accent41",0));assertFalse(health.contains("incorrect:10"));assertEquals("KEEP",health.getString("offline:source",null));untouched();
    }
    @Test public void versionOnePrivateRecoveryRemainsReadableWithoutTouchingNamedStores()throws Exception{
        var health=app.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);health.edit().putBoolean("incorrect:10",true).commit();prefs.edit().putInt("preview_accent41",19).commit();journal.getParentFile().mkdirs();Files.writeString(journal.toPath(),new JSONObject().put("schema",1).put("complete",false).put("values",new JSONArray().put(new JSONObject().put("key","preview_accent41").put("present",true).put("type","int").put("value",7))).toString());
        assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,transaction.recover());assertEquals(7,prefs.getInt("preview_accent41",0));assertTrue(health.getBoolean("incorrect:10",false));untouched();
    }
    @Test public void changedSupportedDensityRequiresReceivingConfirmationAndRecoveryPreservesOldConfirmation()throws Exception{
        prefs.edit().putInt("user_defined_density",160).putBoolean("user_defined_density_confirmed",true).commit();JSONObject incoming=appearance();incoming.getJSONObject("values").put("user_defined_density",new JSONObject().put("type","INT").put("value",240));var plan=transaction.prepare(Map.of(BackupCategory.APPEARANCE,incoming),null);assertEquals(Set.of("user_defined_density"),plan.receivingChecks);assertTrue(prefs.getBoolean("user_defined_density_confirmed",false));
        try{transaction.apply(plan,()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}assertEquals(240,prefs.getInt("user_defined_density",0));assertFalse(prefs.getBoolean("user_defined_density_confirmed",true));transaction.recover();assertEquals(160,prefs.getInt("user_defined_density",0));assertTrue(prefs.getBoolean("user_defined_density_confirmed",false));
        transaction.apply(transaction.prepare(Map.of(BackupCategory.APPEARANCE,incoming),null),()->false);assertFalse(prefs.getBoolean("user_defined_density_confirmed",true));
        for(int unsupported:new int[]{0,1,159,2147483647}){incoming.getJSONObject("values").getJSONObject("user_defined_density").put("value",unsupported);try{transaction.prepare(Map.of(BackupCategory.APPEARANCE,incoming),null);fail();}catch(JSONException expected){}}
        assertEquals(240,prefs.getInt("user_defined_density",0));untouched();
    }
}
