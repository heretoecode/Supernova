package com.archos.mediacenter.video.utils;

import android.app.Application;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.filecorelibrary.TransportReadObserver;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DiagnosticRestorePolicyTest {
    private Application app;private android.content.SharedPreferences prefs;private PreferenceRestoreTransaction transaction;
    private static class ProcessDeath extends Error {}
    @Before public void setup(){app=RuntimeEnvironment.getApplication();prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().putString("oauth_token","AUTH_CANARY").commit();Diagnostics.setEnabled(app,true);transaction=new PreferenceRestoreTransaction(app);}
    @After public void close(){Diagnostics.setEnabled(app,false);}
    private PreferenceRestoreTransaction.Plan selected(boolean enabled)throws Exception{var incoming=app.getSharedPreferences("incoming-diagnostic-fixture",0);incoming.edit().clear().putBoolean(Diagnostics.KEY,enabled).commit();return transaction.prepare(Map.of(BackupCategory.DIAGNOSTICS,PortableSettingsRegistry.export(incoming,PortableSettingsRegistry.Category.DIAGNOSTICS)),null);}
    private boolean observing(){InputStream original=new ByteArrayInputStream(new byte[0]);return original!=TransportReadObserver.observe(TransportReadObserver.Protocol.SMBJ,original);}
    @Test public void completedLoggingOffRestoreAppliesRuntimePolicyAndKeepsSavedLogsAndCredentials()throws Exception {
        File saved=new File(app.getFilesDir(),"supernova-diagnostics/user-kept.txt");saved.getParentFile().mkdirs();Files.writeString(saved.toPath(),"KEEP_SAVED_LOGS");assertTrue(observing());transaction.apply(selected(false),()->false);
        assertFalse(prefs.getBoolean(Diagnostics.KEY,true));assertFalse("Persisted logging-off choice must affect current recorder",observing());assertEquals("KEEP_SAVED_LOGS",Files.readString(saved.toPath()));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void loggingOffDecisionRecoveryAppliesRuntimePolicyOnlyAfterDurableCompletion()throws Exception {
        try{transaction.apply(selected(false),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,new PreferenceRestoreTransaction(app).recover());assertFalse(observing());assertFalse(prefs.getBoolean(Diagnostics.KEY,true));
    }
    @Test public void legacyReplacementReappliesSupportedDefaultWithoutInventingAbsentDiagnosticField()throws Exception {
        try(var helper=new com.archos.mediaprovider.video.VideoOpenHelper(app)){helper.getWritableDatabase();}
        Diagnostics.setEnabled(app,false);File archive=new File(HistoricalBackupFixture.export(app));assertFalse(observing());
        try(java.util.zip.ZipFile legacy=new java.util.zip.ZipFile(archive)){assertFalse(new String(legacy.getInputStream(legacy.getEntry("settings.json")).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8).contains(Diagnostics.KEY));}
        try(var incoming=BackupImportPreparation.prepare(app,new FileInputStream(archive),()->false)){assertTrue(incoming.isLegacy());incoming.restoreLegacy(app,()->false);}
        assertFalse(prefs.contains(Diagnostics.KEY));assertTrue(Diagnostics.defaultEnabled());assertTrue(observing());assertNull(prefs.getString("oauth_token",null));
    }
    @Test public void preDecisionRollbackKeepsOriginalRuntimePolicyAndUnselectedRestoreNeverCreatesDiagnosticPreference()throws Exception {
        try{transaction.apply(selected(false),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        assertTrue(observing());assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());assertTrue(observing());assertTrue(prefs.getBoolean(Diagnostics.KEY,false));
        prefs.edit().remove(Diagnostics.KEY).commit();transaction.apply(transaction.prepare(Map.of(BackupCategory.APPEARANCE,PortableSettingsRegistry.export(app.getSharedPreferences("empty-incoming-fixture",0),PortableSettingsRegistry.Category.APPEARANCE)),null),()->false);assertFalse(prefs.contains(Diagnostics.KEY));assertTrue(observing());assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
}
