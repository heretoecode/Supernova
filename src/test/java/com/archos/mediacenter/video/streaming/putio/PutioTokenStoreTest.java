package com.archos.mediacenter.video.streaming.putio;

import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.spec.SecretKeySpec;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioTokenStoreTest {
    @Rule public TemporaryFolder temporary=new TemporaryFolder();
    private final SecretKeySpec fixtureKey=new SecretKeySpec(new byte[32],"AES");
    @Test public void roundTripIsEncryptedAndWritesUseFreshIv()throws Exception{
        File file=new File(temporary.getRoot(),"oauth.bin");PutioTokenStore store=new PutioTokenStore(file,create->fixtureKey);
        assertNull(store.read());String token="fixture-authorisation-only";store.save(token);byte[] first=Files.readAllBytes(file.toPath());
        assertFalse(new String(first,StandardCharsets.ISO_8859_1).contains(token));assertEquals(token,store.read());
        store.save(token);assertFalse(java.util.Arrays.equals(first,Files.readAllBytes(file.toPath())));assertEquals(token,store.read());
        File unrelated=temporary.newFile("library.db");store.clear();assertNull(store.read());assertTrue(unrelated.exists());
    }
    @Test public void tamperingFailsClosedWithoutDeletingEvidence()throws Exception{
        File file=new File(temporary.getRoot(),"oauth.bin");PutioTokenStore store=new PutioTokenStore(file,create->fixtureKey);store.save("fixture-only");
        byte[] encrypted=Files.readAllBytes(file.toPath());encrypted[encrypted.length-1]^=1;Files.write(file.toPath(),encrypted);
        try{store.read();fail("Tampered envelope accepted");}catch(IOException expected){assertFalse(expected.getMessage().contains("fixture"));}
        assertArrayEquals(encrypted,Files.readAllBytes(file.toPath()));
    }
    @Test public void missingDeviceKeyDoesNotCreateAReplacementOnRead()throws Exception{
        File file=new File(temporary.getRoot(),"oauth.bin");new PutioTokenStore(file,create->fixtureKey).save("fixture-only");
        AtomicBoolean generated=new AtomicBoolean();PutioTokenStore store=new PutioTokenStore(file,create->{generated.set(create);throw new IOException("private detail");});
        try{store.read();fail("Unavailable device key accepted");}catch(IOException expected){assertFalse(expected.getMessage().contains("private"));}
        assertFalse(generated.get());assertTrue(file.exists());
    }
    @Test public void invalidCredentialNeverReplacesUsableEnvelope()throws Exception{
        File file=new File(temporary.getRoot(),"oauth.bin");PutioTokenStore store=new PutioTokenStore(file,create->fixtureKey);store.save("fixture-only");
        try{store.save("invalid\nprivate");fail("Control character accepted");}catch(IOException expected){assertFalse(expected.getMessage().contains("private"));}
        assertEquals("fixture-only",store.read());
    }
}
