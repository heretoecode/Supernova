package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.utils.backup.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class VerifiedBackupPublicationTest {
    private File folder,archive;
    private static class Provider implements VerifiedBackupPublication.Destination {
        ByteArrayOutputStream data=new ByteArrayOutputStream();boolean closed,opened,removed,own=true;int corruption;boolean failClose;
        public OutputStream openWrite(){opened=true;return new FilterOutputStream(data){@Override public void close()throws IOException{closed=true;super.close();if(failClose)throw new IOException("close failed");}};}
        public InputStream openRead()throws IOException{if(!closed)throw new IOException("Read before close");byte[] bytes=data.toByteArray();if(corruption==1)bytes[0]^=1;if(corruption==2)bytes=Arrays.copyOf(bytes,bytes.length-1);if(corruption==3)bytes=Arrays.copyOf(bytes,bytes.length+1);if(corruption==4)throw new IOException("readback not available");return new ByteArrayInputStream(bytes);}
        public boolean removeIncomplete(){if(own){removed=true;data.reset();return true;}return false;}
    }
    @Before public void setup()throws Exception{folder=Files.createTempDirectory("publication-test").toFile();archive=new File(folder,"private.zip");LogicalBackupArchive.writeCategories(archive,Map.of(BackupCategory.CUSTOM_PAGES,new JSONObject().put("schema",1).put("page",JSONObject.NULL)),()->false);}
    @After public void cleanup(){for(File file:folder.listFiles())file.delete();folder.delete();}
    @Test public void prepareBeforePickerAndClosedExactReadbackAreRequiredForCompletion()throws Exception{
        var prepared=VerifiedBackupPublication.prepare(archive,()->false);Provider provider=new Provider();assertFalse(provider.opened);
        long published=VerifiedBackupPublication.publish(prepared,provider,()->false);assertEquals(archive.length(),published);assertTrue(provider.closed);assertFalse(provider.removed);assertArrayEquals(Files.readAllBytes(archive.toPath()),provider.data.toByteArray());assertTrue(prepared.privateArchive().exists());
    }
    @Test public void changedTruncatedTrailingUnreadableAndFailedCloseCopiesNeverReportSuccess()throws Exception{
        var prepared=VerifiedBackupPublication.prepare(archive,()->false);byte[] original=Files.readAllBytes(archive.toPath());
        for(int corruption:List.of(1,2,3,4,5)){
            Provider provider=new Provider();provider.corruption=corruption;provider.failClose=corruption==5;
            try{VerifiedBackupPublication.publish(prepared,provider,()->false);fail();}catch(VerifiedBackupPublication.Failure expected){assertEquals(VerifiedBackupPublication.Cleanup.REMOVED,expected.cleanup);assertFalse(expected.cancelled);}
            assertTrue(provider.removed);assertArrayEquals(original,Files.readAllBytes(archive.toPath()));
        }
    }
    @Test public void providerCannotDeletePreexistingDocumentAndFailureRetainsVerifiedPrivateArchive()throws Exception{
        Provider provider=new Provider();provider.own=false;provider.corruption=1;
        try{VerifiedBackupPublication.publish(VerifiedBackupPublication.prepare(archive,()->false),provider,()->false);fail();}catch(VerifiedBackupPublication.Failure expected){assertEquals(VerifiedBackupPublication.Cleanup.LEFT_UNVERIFIED,expected.cleanup);}
        assertFalse(provider.removed);assertTrue(provider.data.size()>0);assertTrue(archive.exists());LogicalBackupArchive.readCategories(archive,()->false);
    }
    @Test public void cancellationBeforeOpenAndDuringCopyDoesNotPublishOrLoseRecoveryFile()throws Exception{
        var prepared=VerifiedBackupPublication.prepare(archive,()->false);Provider never=new Provider();
        try{VerifiedBackupPublication.publish(prepared,never,()->true);fail();}catch(VerifiedBackupPublication.Failure expected){assertTrue(expected.cancelled);assertEquals(VerifiedBackupPublication.Cleanup.NOT_NEEDED,expected.cleanup);}assertFalse(never.opened);
        Provider started=new Provider();try{VerifiedBackupPublication.publish(prepared,started,()->started.opened);fail();}catch(VerifiedBackupPublication.Failure expected){assertTrue(expected.cancelled);assertEquals(VerifiedBackupPublication.Cleanup.REMOVED,expected.cleanup);}assertTrue(started.removed);assertTrue(archive.exists());
    }
    @Test public void alteredPrivateArchiveAndInvalidArchiveRejectBeforeDestinationOpen()throws Exception{
        var prepared=VerifiedBackupPublication.prepare(archive,()->false);byte[] changed=Files.readAllBytes(archive.toPath());changed[0]^=1;Files.write(archive.toPath(),changed);Provider provider=new Provider();
        try{VerifiedBackupPublication.publish(prepared,provider,()->false);fail();}catch(VerifiedBackupPublication.Failure expected){assertEquals(VerifiedBackupPublication.Cleanup.NOT_NEEDED,expected.cleanup);}assertFalse(provider.opened);
        Files.writeString(archive.toPath(),"not a backup");try{VerifiedBackupPublication.prepare(archive,()->false);fail();}catch(IOException expected){}assertFalse(provider.opened);
    }
}
