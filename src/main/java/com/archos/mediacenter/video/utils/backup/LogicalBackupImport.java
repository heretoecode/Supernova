package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import java.io.*;
import java.security.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private format2.0 preparation for a restore preview. Never opens or changes a live state store. */
public final class LogicalBackupImport {
    private static final long MAX_COMPRESSED=24L*1024*1024;
    private static final class Cancelled extends InterruptedIOException { Cancelled(){super("Backup import cancelled");} }
    public static final class Failure extends IOException {
        public final boolean cancelled,cleanupPending;
        private Failure(Throwable cause,boolean cleanupPending){super("Backup could not be prepared for restore review",cause);this.cancelled=cause instanceof Cancelled;this.cleanupPending=cleanupPending;}
    }
    private static boolean remove(File file){if(file==null)return true;try{return !file.exists()||file.delete();}catch(RuntimeException failure){return false;}}
    private static void cancelled(BooleanSupplier cancel)throws InterruptedIOException{if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new Cancelled();}
    private static byte[] digest(File file,BooleanSupplier cancel)throws IOException{
        try(InputStream input=new FileInputStream(file)){
            MessageDigest digest=MessageDigest.getInstance("SHA-256");byte[] buffer=new byte[65536];int n;long size=0;
            while((n=input.read(buffer))!=-1){cancelled(cancel);size+=n;if(size>MAX_COMPRESSED)throw new IOException("Backup archive exceeds supported size");digest.update(buffer,0,n);}return digest.digest();
        }catch(NoSuchAlgorithmException e){throw new IOException("Backup verification unavailable",e);}
    }
    public static final class Prepared implements AutoCloseable {
        private final File archive;private final byte[] digest;private final Map<BackupCategory,String> validated;
        private boolean closed;
        public final long bytes;
        private Prepared(File file,byte[] digest,Map<BackupCategory,JSONObject> categories){
            archive=file;this.digest=digest;bytes=file.length();Map<BackupCategory,String> copy=new EnumMap<>(BackupCategory.class);for(Map.Entry<BackupCategory,JSONObject> entry:categories.entrySet())copy.put(entry.getKey(),entry.getValue().toString());validated=Collections.unmodifiableMap(copy);
        }
        public Set<BackupCategory> categories(){return validated.keySet();}
        /** Review and confirm use independent payload objects; accidental UI mutation cannot change the snapshot. */
        public synchronized Map<BackupCategory,JSONObject> payloads(BooleanSupplier cancel)throws IOException{
            if(closed)throw new IOException("Restore preview has closed");cancelled(cancel);
            if(archive.length()!=bytes||!MessageDigest.isEqual(digest,LogicalBackupImport.digest(archive,cancel)))throw new IOException("Private restore archive changed; select and review it again");
            Map<BackupCategory,JSONObject> result=new EnumMap<>(BackupCategory.class);
            try{for(Map.Entry<BackupCategory,String> entry:validated.entrySet()){cancelled(cancel);result.put(entry.getKey(),new JSONObject(entry.getValue()));}}catch(JSONException e){throw new IOException("Validated restore snapshot unavailable",e);}
            return Collections.unmodifiableMap(result);
        }
        @Override public synchronized void close()throws IOException{
            if(closed)return;if(archive.exists()&&!archive.delete())throw new IOException("Private restore preview cleanup is pending");closed=true;
        }
    }
    /** Takes ownership of the input stream, including on cancellation and validation failure. */
    public static Prepared prepare(Context context,InputStream source,BooleanSupplier cancel)throws IOException{
        if(source==null)throw new IOException("Document provider did not open the backup");
        File temporary=null,verified=null;
        try(InputStream input=source){
            cancelled(cancel);File folder=new File(context.getCacheDir(),"supernova-logical-restore");if(!folder.isDirectory()&&!folder.mkdirs())throw new IOException("Cannot prepare private restore folder");
            temporary=File.createTempFile("import-",".part",folder);
            try(FileOutputStream output=new FileOutputStream(temporary)){
                byte[] buffer=new byte[65536];long size=0;int n;while((n=input.read(buffer))!=-1){cancelled(cancel);size+=n;if(size>MAX_COMPRESSED)throw new IOException("Backup archive exceeds supported size");output.write(buffer,0,n);}
                if(size==0)throw new IOException("Backup archive is empty");output.flush();output.getFD().sync();
            }
            // Explicit close is before validation/retention, so provider-close failure cannot return a usable preview.
        }catch(IOException|RuntimeException failure){throw new Failure(failure,!remove(temporary));}
        try{
            Map<BackupCategory,JSONObject> categories=LogicalBackupArchive.readCategories(temporary,cancel);byte[] hash=digest(temporary,cancel);cancelled(cancel);
            verified=new File(temporary.getParentFile(),temporary.getName().replace(".part",".verified.zip"));if(verified.exists()||!temporary.renameTo(verified))throw new IOException("Cannot retain private verified restore archive");
            return new Prepared(verified,hash,categories);
        }catch(IOException|RuntimeException failure){boolean originalRemoved=remove(temporary),verifiedRemoved=remove(verified);throw new Failure(failure,!originalRemoved||!verifiedRemoved);}
    }
    private LogicalBackupImport(){}
}
