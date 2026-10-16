package com.archos.mediacenter.video.utils;

import android.app.Application;
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
public class LogicalBackupImportTest {
    private Application app;private byte[] archive;
    @Before public void source()throws Exception{
        app=RuntimeEnvironment.getApplication();File folder=Files.createTempDirectory("import-source").toFile(),file=new File(folder,"source.zip");
        LogicalBackupArchive.writeCategories(file,Map.of(BackupCategory.CUSTOM_PAGES,new JSONObject().put("schema",1).put("page",JSONObject.NULL)),()->false);archive=Files.readAllBytes(file.toPath());file.delete();folder.delete();PreferenceManager.getDefaultSharedPreferences(app).edit().putString("oauth_token","AUTH_CANARY").putString("home_canary","KEEP").commit();
    }
    private File[] retained(){File folder=new File(app.getCacheDir(),"supernova-logical-restore");return folder.isDirectory()?folder.listFiles():new File[0];}
    @Test public void privateValidationPrecedesReviewAndPayloadCopiesCannotMutateConfirmedSnapshot()throws Exception{
        Map<String,?> before=PreferenceManager.getDefaultSharedPreferences(app).getAll();
        try(var prepared=LogicalBackupImport.prepare(app,new ByteArrayInputStream(archive),()->false)){
            assertEquals(Set.of(BackupCategory.CUSTOM_PAGES),prepared.categories());assertEquals(archive.length,prepared.bytes);assertEquals(1,retained().length);assertTrue(retained()[0].getName().endsWith(".verified.zip"));
            prepared.payloads(()->false).get(BackupCategory.CUSTOM_PAGES).put("credentials","INJECTED");assertFalse(prepared.payloads(()->false).get(BackupCategory.CUSTOM_PAGES).has("credentials"));assertEquals(before,PreferenceManager.getDefaultSharedPreferences(app).getAll());
        }assertEquals(0,retained().length);assertEquals(before,PreferenceManager.getDefaultSharedPreferences(app).getAll());
    }
    @Test public void malformedEmptyAndTruncatedStreamsCannotCreateUsablePreview()throws Exception{
        for(byte[] invalid:List.of(new byte[0],"not a backup".getBytes(),Arrays.copyOf(archive,archive.length/2)))try{LogicalBackupImport.prepare(app,new ByteArrayInputStream(invalid),()->false);fail();}catch(IOException expected){assertEquals(0,retained().length);}
        assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));
    }
    @Test public void cancellationAndProviderCloseFailureCloseStreamAndRemovePrivatePartials()throws Exception{
        final boolean[] closed={false};InputStream input=new ByteArrayInputStream(archive){@Override public void close(){closed[0]=true;}};
        try{LogicalBackupImport.prepare(app,input,()->true);fail();}catch(LogicalBackupImport.Failure expected){assertTrue(expected.cancelled);assertFalse(expected.cleanupPending);}assertTrue(closed[0]);assertEquals(0,retained().length);
        InputStream badClose=new ByteArrayInputStream(archive){@Override public void close()throws IOException{throw new IOException("provider close failure");}};
        try{LogicalBackupImport.prepare(app,badClose,()->false);fail();}catch(IOException expected){}assertEquals(0,retained().length);
    }
    @Test public void changedPrivateArchiveCannotBeUsedAfterPreviewAndClosedPreviewCannotRestore()throws Exception{
        var prepared=LogicalBackupImport.prepare(app,new ByteArrayInputStream(archive),()->false);byte[] changed=archive.clone();changed[0]^=1;Files.write(retained()[0].toPath(),changed);
        try{prepared.payloads(()->false);fail();}catch(IOException expected){}prepared.close();prepared.close();assertEquals(0,retained().length);try{prepared.payloads(()->false);fail();}catch(IOException expected){}
    }
    @Test public void boundedCopyRejectsOversizeInputBeforeZipParsingAndLeavesNoPartial()throws Exception{
        InputStream huge=new InputStream(){long remaining=24L*1024*1024+1;public int read(){return remaining-->0?0:-1;}public int read(byte[] b,int offset,int length){if(remaining==0)return -1;int n=(int)Math.min(length,remaining);Arrays.fill(b,offset,offset+n,(byte)0);remaining-=n;return n;}};
        try{LogicalBackupImport.prepare(app,huge,()->false);fail();}catch(IOException expected){}assertEquals(0,retained().length);
    }
}
