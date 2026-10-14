package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import com.archos.mediacenter.video.utils.SafeBackup;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.zip.*;
import org.json.*;

/** One bounded provider snapshot, dispatched to the independent logical and historical readers. */
public final class BackupImportPreparation implements AutoCloseable {
    private static final long MAX_ARCHIVE=4L*1024*1024*1024;
    private final Context context;
    private final LogicalBackupImport.Prepared logical;
    private final File legacy;
    private final byte[] legacySignature;
    private BackupImportPreparation(Context context,LogicalBackupImport.Prepared logical,File legacy)throws Exception {
        this.context=context.getApplicationContext();this.logical=logical;this.legacy=legacy;legacySignature=legacy==null?null:signature(legacy);
    }
    public boolean isLegacy(){return legacy!=null;}
    public Set<BackupCategory> categories(){return logical==null?Collections.emptySet():logical.categories();}
    public Map<BackupCategory,JSONObject> payloads(BooleanSupplier cancel)throws IOException {
        if(logical==null)throw new IOException("Legacy backups replace the whole supported library, not selected logical categories");return logical.payloads(cancel);
    }
    private static void cancelled(BooleanSupplier cancel)throws InterruptedIOException {
        if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Backup import cancelled");
    }
    public static BackupImportPreparation prepare(Context context,InputStream source,BooleanSupplier cancel)throws Exception {
        if(source==null)throw new IOException("Backup unavailable");File copied=null,stage=null;LogicalBackupImport.Prepared logical=null;
        try {
            try(InputStream input=source){cancelled(cancel);copied=File.createTempFile("supernova-format-",".part",context.getCacheDir());try(FileOutputStream output=new FileOutputStream(copied)){
                byte[] buffer=new byte[65536];long count=0;int n;while((n=input.read(buffer))!=-1){cancelled(cancel);count+=n;if(count>MAX_ARCHIVE)throw new IOException("Backup archive exceeds supported size");output.write(buffer,0,n);}if(count==0)throw new IOException("Backup archive is empty");output.flush();output.getFD().sync();
            }}
            String format;
            try(ZipFile zip=new ZipFile(copied)){Set<String> names=new HashSet<>();for(var entries=zip.entries();entries.hasMoreElements();){var entry=entries.nextElement();cancelled(cancel);if(names.size()>=100000||!names.add(entry.getName()))throw new IOException("Duplicate or excessive backup entries");}
                ZipEntry manifest=zip.getEntry("manifest.json");if(manifest==null||manifest.isDirectory())throw new IOException("Backup manifest is missing");try(InputStream input=zip.getInputStream(manifest);ByteArrayOutputStream output=new ByteArrayOutputStream()){byte[] buffer=new byte[8192];int n;while((n=input.read(buffer))!=-1){cancelled(cancel);if(output.size()+n>4194304)throw new IOException("Backup manifest exceeds supported size");output.write(buffer,0,n);}JSONObject value=new JSONObject(output.toString(StandardCharsets.UTF_8.name()));if(!"Supernova".equals(value.getString("format")))throw new IOException("Unsupported backup origin");format=value.getString("formatVersion");}
            }
            cancelled(cancel);
            if(LogicalBackupArchive.FORMAT.equals(format))logical=LogicalBackupImport.prepare(context,new FileInputStream(copied),cancel);
            else if("1.0".equals(format))stage=SafeBackup.stage(context,new FileInputStream(copied),cancel);
            else throw new IOException("Unsupported backup format");
            cancelled(cancel);BackupImportPreparation result=new BackupImportPreparation(context,logical,stage);if(!copied.delete())throw new IOException("Private format snapshot cleanup is pending");return result;
        }catch(Exception failure){if(logical!=null)try{logical.close();}catch(IOException cleanup){failure.addSuppressed(cleanup);}if(stage!=null)try{SafeBackup.discardStage(context,stage);}catch(IOException cleanup){failure.addSuppressed(cleanup); }if(copied!=null&&copied.exists()&&!copied.delete())failure.addSuppressed(new IOException("Private format snapshot cleanup is pending"));throw failure;}
    }
    private static byte[] signature(File root)throws Exception {
        MessageDigest digest=MessageDigest.getInstance("SHA-256");List<File> files=new ArrayList<>();inventory(root,files);files.sort(Comparator.comparing(File::getPath));byte[] buffer=new byte[65536];
        for(File file:files){String name=root.toPath().relativize(file.toPath()).toString();digest.update(name.getBytes(StandardCharsets.UTF_8));digest.update((byte)0);digest.update(Long.toString(file.length()).getBytes(StandardCharsets.UTF_8));digest.update((byte)0);try(InputStream input=new FileInputStream(file)){int n;while((n=input.read(buffer))!=-1)digest.update(buffer,0,n);}}return digest.digest();
    }
    private static void inventory(File root,List<File> files)throws IOException {
        File[] children=root.listFiles();if(children==null)throw new IOException("Private legacy review is unavailable");for(File file:children){if(java.nio.file.Files.isSymbolicLink(file.toPath()))throw new IOException("Invalid private legacy review");if(file.isDirectory())inventory(file,files);else files.add(file);if(files.size()>100000)throw new IOException("Private legacy review exceeds supported size");}
    }
    /** Cancellation is checked before the historical reader's coordinated replacement begins. */
    public void restoreLegacy(Context context,BooleanSupplier cancel)throws Exception {
        if(legacy==null)throw new IOException("Not a legacy backup");cancelled(cancel);if(!MessageDigest.isEqual(legacySignature,signature(legacy)))throw new IOException("Private legacy review changed; select the backup again");SafeBackup.validateStage(context,legacy);cancelled(cancel);SafeBackup.restore(context,legacy);
    }
    @Override public void close()throws IOException {
        if(logical!=null)logical.close();if(legacy!=null)SafeBackup.discardStage(context,legacy);
    }
}
