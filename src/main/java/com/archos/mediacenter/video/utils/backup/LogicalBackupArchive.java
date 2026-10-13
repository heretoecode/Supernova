package com.archos.mediacenter.video.utils.backup;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.zip.*;
import org.json.*;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.utils.backup.PortableSettingsRegistry.Category;

/** Logical2.0 archive foundation. Current codecs cover settings; other categories remain unregistered. */
public final class LogicalBackupArchive {
    public static final String FORMAT="2.0";
    private static final int MAX_ENTRY=4*1024*1024,MAX_TOTAL=20*1024*1024;
    public static final String INSTRUCTIONS="SUPERNOVA LOGICAL BACKUP FORMAT 2.0\n\n"
        +"This settings archive contains only the settings categories listed in manifest.json. It contains no media, artwork, subtitles, credentials, accounts, permissions or diagnostic recordings.\n"
        +"Keep this unencrypted archive private: language, provider and preference choices can reveal personal interests.\n\n"
        +"Before restoring, make a backup of the destination. A compatible reader must validate every archive entry and category schema before offering a restore preview. Select the categories you want to replace and confirm the preview. Selected categories replace their corresponding settings; a selected empty category clears those settings. Missing or unselected categories leave the destination unchanged.\n"
        +"Reconnect storage and services and grant Android permissions separately. This settings-only archive does not contain media identification, Home membership or viewing progress. Device-specific audio/video choices may need adjustment. Portability, permissions and hardware behavior are not guaranteed.\n"
        +"Cancel before confirming to leave the destination unchanged. Do not use incomplete files. Export succeeds only after the closed archive and any destination copy pass verification. Interrupted restoration requires recovery by the reader's restore journal; do not assume every failure can roll back external services.\n"
        +"Legacy Supernova1.0 archives require their dedicated database/schema reader and may contain paths and artwork. They are not converted by this archive foundation. Unknown future formats and category schemas must be rejected.\n";
    private static void cancelled(BooleanSupplier cancel)throws IOException{if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Backup cancelled");}
    private static byte[] bytes(String text){return text.getBytes(StandardCharsets.UTF_8);}
    private static String text(byte[] data)throws IOException{try{return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data)).toString();}catch(CharacterCodingException e){throw new IOException("Invalid backup text",e);}}
    private static String hash(byte[] data)throws IOException{try{StringBuilder result=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(data))result.append(String.format(Locale.ROOT,"%02x",b&255));return result.toString();}catch(Exception e){throw new IOException("Cannot verify backup digest",e);}}
    private static void entry(ZipOutputStream zip,String name,byte[] data)throws IOException{zip.putNextEntry(new ZipEntry(name));zip.write(data);zip.closeEntry();}
    /** Creates a private verified file. Caller separately owns SAF publication and restore journaling. */
    public static void write(File destination,Map<Category,JSONObject> payloads,BooleanSupplier cancel)throws IOException{
        cancelled(cancel);if(destination.exists())throw new IOException("Backup destination already exists");
        File parent=destination.getAbsoluteFile().getParentFile();if(!parent.isDirectory()&&!parent.mkdirs())throw new IOException("Cannot prepare private backup folder");
        if(payloads.isEmpty())throw new IOException("Choose at least one backup category");
        Map<Category,byte[]> data=new EnumMap<>(Category.class);JSONObject manifest=new JSONObject();JSONArray categories=new JSONArray();
        try{for(Category category:Category.values())if(payloads.containsKey(category)){
                cancelled(cancel);JSONObject payload=new JSONObject(payloads.get(category).toString());PortableSettingsRegistry.validate(payload,category);
                byte[] content=bytes(payload.toString());if(content.length>MAX_ENTRY)throw new IOException("Backup category exceeds size limit");data.put(category,content);
                categories.put(new JSONObject().put("id",category.name()).put("schema",PortableSettingsRegistry.SCHEMA).put("size",content.length).put("sha256",hash(content)));
            }
            manifest.put("format","Supernova").put("formatVersion",FORMAT).put("applicationVersion",BuildConfig.VERSION_NAME).put("createdUtc",System.currentTimeMillis()).put("credentialsIncluded",false).put("mediaIncluded",false).put("categories",categories);
        }catch(JSONException e){throw new IOException("Invalid logical backup category",e);}
        File temporary=File.createTempFile("supernova-backup-",".part",parent);boolean published=false;
        try{try(FileOutputStream file=new FileOutputStream(temporary);ZipOutputStream zip=new ZipOutputStream(new BufferedOutputStream(file))){
                entry(zip,"manifest.json",bytes(manifest.toString()));entry(zip,"RESTORE_INSTRUCTIONS.txt",bytes(INSTRUCTIONS));
                for(Map.Entry<Category,byte[]> item:data.entrySet()){cancelled(cancel);entry(zip,"categories/"+item.getKey().name()+".json",item.getValue());}
                zip.finish();zip.flush();file.getFD().sync();
            }
            read(temporary,cancel);cancelled(cancel);
            if(destination.exists()||!temporary.renameTo(destination))throw new IOException("Cannot publish private verified backup");published=true;
        }finally{if(!published&&temporary.exists()&&!temporary.delete())temporary.deleteOnExit();}
    }
    /** Validate all entries before returning any selected payload. No destination state is changed. */
    public static Map<Category,JSONObject> read(File file,BooleanSupplier cancel)throws IOException{
        Map<String,byte[]> contents=new HashMap<>();long total=0;
        try(ZipFile zip=new ZipFile(file)){Enumeration<? extends ZipEntry> entries=zip.entries();
            while(entries.hasMoreElements()){cancelled(cancel);ZipEntry entry=entries.nextElement();String name=entry.getName();
                if(contents.size()>=Category.values().length+2||entry.isDirectory()||contents.containsKey(name)||!(name.equals("manifest.json")||name.equals("RESTORE_INSTRUCTIONS.txt")||name.matches("categories/(?:APPEARANCE|AUDIO|DIAGNOSTICS|INTEGRATIONS|LIBRARY|NETWORK|PLAYBACK|STREAMING|SUBTITLES|VIDEO)\\.json")))throw new IOException("Unsupported or duplicate logical backup entry");
                if(entry.getSize()<=0||entry.getSize()>MAX_ENTRY)throw new IOException("Backup entry exceeds limits");
                CRC32 crc=new CRC32();ByteArrayOutputStream output=new ByteArrayOutputStream();byte[] buffer=new byte[8192];
                try(InputStream input=zip.getInputStream(entry)){int n;while((n=input.read(buffer))!=-1){cancelled(cancel);total+=n;if(total>MAX_TOTAL||output.size()+n>MAX_ENTRY)throw new IOException("Backup exceeds size limits");output.write(buffer,0,n);crc.update(buffer,0,n);}}
                if(output.size()!=entry.getSize()||crc.getValue()!=entry.getCrc())throw new IOException("Backup entry verification failed");contents.put(name,output.toByteArray());
            }
        }
        if(!contents.containsKey("manifest.json")||!contents.containsKey("RESTORE_INSTRUCTIONS.txt"))throw new IOException("Backup manifest or instructions missing");
        text(contents.get("RESTORE_INSTRUCTIONS.txt"));Map<Category,JSONObject> result=new EnumMap<>(Category.class);
        try{JSONObject manifest=new JSONObject(text(contents.get("manifest.json")));
            if(manifest.length()!=7||!"Supernova".equals(manifest.getString("format"))||!FORMAT.equals(manifest.getString("formatVersion"))||!Boolean.FALSE.equals(manifest.get("credentialsIncluded"))||!Boolean.FALSE.equals(manifest.get("mediaIncluded"))||!manifest.getString("applicationVersion").matches("0\\.[1-9][0-9]*")||manifest.getLong("createdUtc")<=0)throw new IOException("Unsupported logical backup manifest");
            JSONArray categories=manifest.getJSONArray("categories");if(categories.length()==0)throw new IOException("Backup contains no categories");
            for(int i=0;i<categories.length();i++){cancelled(cancel);JSONObject item=categories.getJSONObject(i);Category category=Category.valueOf(item.getString("id"));
                if(item.length()!=4||item.getInt("schema")!=PortableSettingsRegistry.SCHEMA||result.containsKey(category))throw new IOException("Unsupported or duplicate category schema");
                byte[] content=contents.get("categories/"+category.name()+".json");if(content==null||item.getLong("size")!=content.length||!hash(content).equals(item.getString("sha256")))throw new IOException("Missing or changed backup category");
                JSONObject payload=new JSONObject(text(content));PortableSettingsRegistry.validate(payload,category);result.put(category,payload);
            }
            if(contents.size()!=result.size()+2)throw new IOException("Unlisted backup payload rejected");
        }catch(JSONException|IllegalArgumentException e){throw new IOException("Invalid logical backup schema",e);}
        return Collections.unmodifiableMap(result);
    }
    private LogicalBackupArchive(){}
}
