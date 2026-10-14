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
import com.archos.mediacenter.video.leanback.HomeIntentCodec;
import com.archos.mediacenter.video.leanback.PortableViewingChoices;
import com.archos.mediacenter.video.leanback.PortablePreferredVersions;
import com.archos.mediacenter.video.leanback.PortableReviewNotes;
import com.archos.mediacenter.video.leanback.PortableViewingState;
import com.archos.mediacenter.video.utils.backup.PortableSettingsRegistry.Category;

/** Logical2.0 archive foundation. Only positively registered codecs may write or read payloads. */
public final class LogicalBackupArchive {
    public static final String FORMAT="2.0";
    private static final int MAX_ENTRY=4*1024*1024,MAX_TOTAL=20*1024*1024;
    public static final String INSTRUCTIONS="SUPERNOVA LOGICAL BACKUP FORMAT 2.0\n\n"
        +"This archive contains only the categories listed in manifest.json. Currently registered categories are settings, Home rows, the custom Library page, Watch Next, Continue Watching choices, preferred versions, user review notes, pinned titles, hidden items, watch history, unfinished playback positions, Library sources, browser favourites, saved playlists and optional filenames and locations. It contains no media, artwork, subtitle files, credentials, accounts, permissions or diagnostic recordings.\n"
        +"Keep this unencrypted archive private: language, provider and preference choices can reveal personal interests.\n\n"
        +"Before restoring, make a backup of the destination. A compatible reader must validate every archive entry and category schema before offering a restore preview. Select the categories you want to replace and confirm the preview. Selected categories replace their corresponding configuration; a selected empty category clears that configuration. Missing or unselected categories leave the destination unchanged. Home rows never replace independently owned Watch Next membership.\n"
        +"Reconnect storage and services and grant Android permissions separately. Home membership uses exact logical title identities; unresolved members remain pending, without guessing titles or recreating locations. Watch history and unfinished positions are independent: excluding history loses restored watched states and viewing times; excluding positions loses unfinished offsets. These categories replace the identity-based viewing store, not legacy file/database markers. Media identification is not registered in this staged format. Device-specific audio/video choices may need adjustment. Portability, permissions and hardware behavior are not guaranteed.\n"
        +"Preferred physical versions require validated media matching. They remain pending when that dependency is not ready; restoring them never enables filenames or locations automatically. Review notes do not include system health caches. Physical location fingerprints do not authenticate storage/accounts or prove media contents; ambiguous, credential/query-bearing, missing and differently matched records remain unresolved.\n"
        +"Pinned movie/show identities and ordering are restored only when uniquely matched. Hidden items remain pending until physical matching is validated. Missing or ambiguous references are kept for later reconciliation. These categories never recreate Put.io associations or legacy user Collections.\n"
        +"Library sources contain location references for selected roots and exclusions, never access credentials. Only the separately selected filenames and locations category contains locator text. Locations can reveal filenames, folders, hosts and shares; it is optional and off by default. Excluding locations leaves unknown sources pending rather than creating guessed roots. Select locations explicitly at restore to recreate the included source paths; sign in, grant permissions and finish source setup separately. Existing Put.io source ownership remains outside selected replacement. Unsafe, authenticated or query-bearing locations are rejected rather than stripped or silently changed.\n"
        +"Cancel before confirming to leave the destination unchanged. Do not use incomplete files. Export succeeds only after the closed archive and any destination copy pass verification. Interrupted restoration requires recovery by the reader's restore journal; do not assume every failure can roll back external services.\n"
        +"Saved playlists contain names and TMDB movie/episode membership only. Restored definitions remain local-only and never recreate Trakt account associations or trigger uploads. Selected replacement clears only local playlist definitions; receiving account-owned definitions are preserved. Unknown title membership becomes usable when indexed; definitions on an uninitialized device remain pending until normal media-store setup.\n"
        +"Browser favourites restore only to exact configured locations or locations explicitly selected from this archive. Unknown favourites remain pending. Indexed folders, FTP credentials and Put.io associations are unchanged.\n"
        +"Legacy Supernova1.0 archives require their dedicated database/schema reader and may contain paths and artwork. The logical workspace does not convert these archives. The older whole-library restore route has different replacement semantics and clears prior default/service authentication; reconnect accounts afterward. New logical selected restoration leaves existing receiving credentials unchanged. No new1.0 whole-library backups are created. Unknown future formats and category schemas must be rejected.\n";
    private static void cancelled(BooleanSupplier cancel)throws IOException{if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Backup cancelled");}
    private static byte[] bytes(String text){return text.getBytes(StandardCharsets.UTF_8);}
    private static String text(byte[] data)throws IOException{try{return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data)).toString();}catch(CharacterCodingException e){throw new IOException("Invalid backup text",e);}}
    private static String hash(byte[] data)throws IOException{try{StringBuilder result=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(data))result.append(String.format(Locale.ROOT,"%02x",b&255));return result.toString();}catch(Exception e){throw new IOException("Cannot verify backup digest",e);}}
    private static void entry(ZipOutputStream zip,String name,byte[] data)throws IOException{zip.putNextEntry(new ZipEntry(name));zip.write(data);zip.closeEntry();}
    public static Set<BackupCategory> registered(){EnumSet<BackupCategory> result=EnumSet.of(BackupCategory.HOME,BackupCategory.CUSTOM_PAGES,BackupCategory.WATCH_NEXT,BackupCategory.CONTINUE_CHOICES,BackupCategory.PREFERRED,BackupCategory.REVIEW,BackupCategory.HISTORY,BackupCategory.POSITIONS,BackupCategory.PINNED,BackupCategory.HIDDEN,BackupCategory.SOURCES,BackupCategory.BOOKMARKS,BackupCategory.PLAYLISTS,BackupCategory.LOCATIONS);for(BackupCategory category:BackupCategory.values())if(category.settings()!=null)result.add(category);return Collections.unmodifiableSet(result);}
    private static void validate(BackupCategory category,JSONObject payload)throws JSONException{
        if(category.settings()!=null)PortableSettingsRegistry.validate(payload,category.settings());
        else if(category==BackupCategory.HOME)HomeIntentCodec.validate(payload);
        else if(category==BackupCategory.CUSTOM_PAGES)PortableCustomPage.validate(payload);
        else if(category==BackupCategory.WATCH_NEXT||category==BackupCategory.CONTINUE_CHOICES)PortableViewingChoices.validate(payload,PortableViewingChoices.Kind.valueOf(category.name()));
        else if(category==BackupCategory.PREFERRED)PortablePreferredVersions.validate(payload);
        else if(category==BackupCategory.REVIEW)PortableReviewNotes.validate(payload);
        else if(category==BackupCategory.HISTORY||category==BackupCategory.POSITIONS)PortableViewingState.validate(payload,PortableViewingState.Kind.valueOf(category.name()));
        else if(category==BackupCategory.PINNED)PortablePinnedTitles.validate(payload);
        else if(category==BackupCategory.HIDDEN)PortableHiddenItems.validate(payload);
        else if(category==BackupCategory.SOURCES)PortableLibrarySources.validate(payload);
        else if(category==BackupCategory.BOOKMARKS)PortableBookmarks.validate(payload);
        else if(category==BackupCategory.PLAYLISTS)PortableSavedPlaylists.validate(payload);
        else if(category==BackupCategory.LOCATIONS)PortableLocations.validate(payload);
        else throw new JSONException("Category codec is not registered");
    }
    private static boolean integer(Object value){return value instanceof Integer||value instanceof Long;}
    private static boolean supportedEntry(String name){
        if(name.equals("manifest.json")||name.equals("RESTORE_INSTRUCTIONS.txt"))return true;
        if(!name.matches("categories/[A-Z_]+\\.json"))return false;
        try{return registered().contains(BackupCategory.valueOf(name.substring(11,name.length()-5)));}catch(IllegalArgumentException e){return false;}
    }
    /** Creates a private verified file. Caller separately owns SAF publication and restore journaling. */
    public static void write(File destination,Map<Category,JSONObject> payloads,BooleanSupplier cancel)throws IOException{
        Map<BackupCategory,JSONObject> converted=new EnumMap<>(BackupCategory.class);for(Map.Entry<Category,JSONObject> item:payloads.entrySet())converted.put(BackupCategory.valueOf(item.getKey().name()),item.getValue());writeCategories(destination,converted,cancel);
    }
    public static void writeCategories(File destination,Map<BackupCategory,JSONObject> payloads,BooleanSupplier cancel)throws IOException{
        cancelled(cancel);if(destination.exists())throw new IOException("Backup destination already exists");
        File parent=destination.getAbsoluteFile().getParentFile();if(!parent.isDirectory()&&!parent.mkdirs())throw new IOException("Cannot prepare private backup folder");
        if(payloads.isEmpty())throw new IOException("Choose at least one backup category");
        if(!registered().containsAll(payloads.keySet()))throw new IOException("Backup category codec is not registered");
        Map<BackupCategory,byte[]> data=new EnumMap<>(BackupCategory.class);JSONObject manifest=new JSONObject();JSONArray categories=new JSONArray();
        try{for(BackupCategory category:BackupCategory.values())if(payloads.containsKey(category)){
                cancelled(cancel);if(payloads.get(category)==null)throw new JSONException("Missing selected category");JSONObject payload=new JSONObject(payloads.get(category).toString());validate(category,payload);
                byte[] content=bytes(payload.toString());if(content.length>MAX_ENTRY)throw new IOException("Backup category exceeds size limit");data.put(category,content);
                categories.put(new JSONObject().put("id",category.name()).put("schema",payload.getInt("schema")).put("size",content.length).put("sha256",hash(content)));
            }
            manifest.put("format","Supernova").put("formatVersion",FORMAT).put("applicationVersion",BuildConfig.VERSION_NAME).put("createdUtc",System.currentTimeMillis()).put("credentialsIncluded",false).put("mediaIncluded",false).put("categories",categories);
        }catch(JSONException e){throw new IOException("Invalid logical backup category",e);}
        File temporary=File.createTempFile("supernova-backup-",".part",parent);boolean published=false;
        try{try(FileOutputStream file=new FileOutputStream(temporary);ZipOutputStream zip=new ZipOutputStream(new BufferedOutputStream(file))){
                entry(zip,"manifest.json",bytes(manifest.toString()));entry(zip,"RESTORE_INSTRUCTIONS.txt",bytes(INSTRUCTIONS));
                for(Map.Entry<BackupCategory,byte[]> item:data.entrySet()){cancelled(cancel);entry(zip,"categories/"+item.getKey().name()+".json",item.getValue());}
                zip.finish();zip.flush();file.getFD().sync();
            }
            readCategories(temporary,cancel);cancelled(cancel);
            if(destination.exists()||!temporary.renameTo(destination))throw new IOException("Cannot publish private verified backup");published=true;
        }finally{if(!published&&temporary.exists()&&!temporary.delete())temporary.deleteOnExit();}
    }
    /** Validate all entries before returning any selected payload. No destination state is changed. */
    public static Map<Category,JSONObject> read(File file,BooleanSupplier cancel)throws IOException{
        Map<Category,JSONObject> result=new EnumMap<>(Category.class);for(Map.Entry<BackupCategory,JSONObject> item:readCategories(file,cancel).entrySet()){
            if(item.getKey().settings()==null)throw new IOException("Archive contains a non-settings category; use the category-aware reader");result.put(item.getKey().settings(),item.getValue());
        }return Collections.unmodifiableMap(result);
    }
    public static Map<BackupCategory,JSONObject> readCategories(File file,BooleanSupplier cancel)throws IOException{
        Map<String,byte[]> contents=new HashMap<>();long total=0;
        try(ZipFile zip=new ZipFile(file)){Enumeration<? extends ZipEntry> entries=zip.entries();
            while(entries.hasMoreElements()){cancelled(cancel);ZipEntry entry=entries.nextElement();String name=entry.getName();
                if(contents.size()>=registered().size()+2||entry.isDirectory()||contents.containsKey(name)||!supportedEntry(name))throw new IOException("Unsupported or duplicate logical backup entry");
                if(entry.getSize()<=0||entry.getSize()>MAX_ENTRY)throw new IOException("Backup entry exceeds limits");
                CRC32 crc=new CRC32();ByteArrayOutputStream output=new ByteArrayOutputStream();byte[] buffer=new byte[8192];
                try(InputStream input=zip.getInputStream(entry)){int n;while((n=input.read(buffer))!=-1){cancelled(cancel);total+=n;if(total>MAX_TOTAL||output.size()+n>MAX_ENTRY)throw new IOException("Backup exceeds size limits");output.write(buffer,0,n);crc.update(buffer,0,n);}}
                if(output.size()!=entry.getSize()||crc.getValue()!=entry.getCrc())throw new IOException("Backup entry verification failed");contents.put(name,output.toByteArray());
            }
        }
        if(!contents.containsKey("manifest.json")||!contents.containsKey("RESTORE_INSTRUCTIONS.txt"))throw new IOException("Backup manifest or instructions missing");
        text(contents.get("RESTORE_INSTRUCTIONS.txt"));Map<BackupCategory,JSONObject> result=new EnumMap<>(BackupCategory.class);
        try{JSONObject manifest=new JSONObject(text(contents.get("manifest.json")));
            if(manifest.length()!=7||!"Supernova".equals(manifest.get("format"))||!FORMAT.equals(manifest.get("formatVersion"))||!Boolean.FALSE.equals(manifest.get("credentialsIncluded"))||!Boolean.FALSE.equals(manifest.get("mediaIncluded"))||!(manifest.get("applicationVersion") instanceof String)||!manifest.getString("applicationVersion").matches("0\\.[1-9][0-9]*")||!integer(manifest.get("createdUtc"))||manifest.getLong("createdUtc")<=0)throw new IOException("Unsupported logical backup manifest");
            JSONArray categories=manifest.getJSONArray("categories");if(categories.length()==0)throw new IOException("Backup contains no categories");
            for(int i=0;i<categories.length();i++){cancelled(cancel);JSONObject item=categories.getJSONObject(i);if(!(item.get("id") instanceof String))throw new IOException("Category identity must be text");BackupCategory category=BackupCategory.valueOf(item.getString("id"));
                if(item.length()!=4||!registered().contains(category)||!integer(item.get("schema"))||item.getLong("schema")!=PortableSettingsRegistry.SCHEMA&&!(category==BackupCategory.SOURCES&&item.getLong("schema")==2)||result.containsKey(category))throw new IOException("Unsupported or duplicate category schema");
                byte[] content=contents.get("categories/"+category.name()+".json");if(content==null||!integer(item.get("size"))||item.getLong("size")!=content.length||!hash(content).equals(item.get("sha256")))throw new IOException("Missing or changed backup category");
                JSONObject payload=new JSONObject(text(content));validate(category,payload);if(payload.getLong("schema")!=item.getLong("schema"))throw new IOException("Category schema does not match its manifest");result.put(category,payload);
            }
            if(contents.size()!=result.size()+2)throw new IOException("Unlisted backup payload rejected");
        }catch(JSONException|IllegalArgumentException e){throw new IOException("Invalid logical backup schema",e);}
        return Collections.unmodifiableMap(result);
    }
    private LogicalBackupArchive(){}
}
