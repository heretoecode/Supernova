package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.AtomicFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;

/** Device-local OAuth encryption only. Never reads or changes the APK signing identity. */
public final class PutioTokenStore {
    private static final String ALIAS="supernova.putio.oauth.aes.v1";
    private static final byte[] AAD="supernova-putio-oauth-v1".getBytes(StandardCharsets.US_ASCII);
    private static final Object LOCK=new Object();
    interface Keys {SecretKey get(boolean create)throws Exception;}
    private final AtomicFile file;
    private final Keys keys;

    public PutioTokenStore(Context context){
        this(new File(context.getNoBackupFilesDir(),"putio-oauth-v1.bin"),PutioTokenStore::deviceKey);
    }
    PutioTokenStore(File target,Keys keys){file=new AtomicFile(target);this.keys=keys;}
    private static SecretKey deviceKey(boolean create)throws Exception{
        if(Build.VERSION.SDK_INT<23)throw new GeneralSecurityException("Secure provider storage unavailable");
        KeyStore store=KeyStore.getInstance("AndroidKeyStore");store.load(null);
        if(store.containsAlias(ALIAS))return (SecretKey)store.getKey(ALIAS,null);
        if(!create)throw new GeneralSecurityException("Provider credential requires reconnect");
        KeyGenerator generator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        generator.init(new KeyGenParameterSpec.Builder(ALIAS,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256).setRandomizedEncryptionRequired(true).build());
        return generator.generateKey();
    }
    private static void validate(String value)throws IOException{
        if(value==null||!value.matches("[!-~]{1,4096}"))throw new IOException("Invalid provider credential");
    }
    public void save(String token)throws IOException{
        validate(token);
        synchronized(LOCK){byte[] plain=token.getBytes(StandardCharsets.US_ASCII);FileOutputStream output=null;
            try{
                Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,keys.get(true));cipher.updateAAD(AAD);
                byte[] iv=cipher.getIV();if(iv.length!=12)throw new GeneralSecurityException("Unsupported IV size");
                byte[] encrypted=cipher.doFinal(plain);
                File parent=file.getBaseFile().getParentFile();if(!parent.isDirectory()&&!parent.mkdirs())throw new IOException("Storage unavailable");
                output=file.startWrite();output.write(1);output.write(iv);output.write(encrypted);file.finishWrite(output);
            }catch(Exception failure){if(output!=null)file.failWrite(output);throw new IOException("Unable to preserve provider credential securely");}
            finally{Arrays.fill(plain,(byte)0);}
        }
    }
    /** Missing is disconnected; corrupt/missing-device-key is reconnect, never plaintext fallback. */
    public String read()throws IOException{
        synchronized(LOCK){byte[] plain=null;
            try(InputStream input=file.openRead();ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
                byte[] buffer=new byte[1024];int count;
                while((count=input.read(buffer))!=-1){if(bytes.size()+count>8192)throw new IOException("Invalid encrypted envelope");bytes.write(buffer,0,count);}
                byte[] envelope=bytes.toByteArray();if(envelope.length<30||envelope[0]!=1)throw new IOException("Invalid encrypted envelope");
                Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,keys.get(false),new GCMParameterSpec(128,envelope,1,12));cipher.updateAAD(AAD);
                plain=cipher.doFinal(envelope,13,envelope.length-13);
                String token=new String(plain,StandardCharsets.US_ASCII);validate(token);return token;
            }catch(FileNotFoundException missing){if(file.getBaseFile().exists())throw new IOException("Provider credential unavailable; reconnect required");return null;}
            catch(Exception failure){throw new IOException("Provider credential unavailable; reconnect required");}
            finally{if(plain!=null)Arrays.fill(plain,(byte)0);}
        }
    }
    /** Disconnect deletes this provider's encrypted credential, never library records or media. */
    public void clear(){synchronized(LOCK){file.delete();}}
}
