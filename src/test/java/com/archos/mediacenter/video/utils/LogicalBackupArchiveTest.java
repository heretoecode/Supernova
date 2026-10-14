package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediacenter.video.utils.backup.PortableSettingsRegistry.Category;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class LogicalBackupArchiveTest {
    private File dir;
    @Before public void folder()throws Exception{dir=Files.createTempDirectory("logical-backup-test").toFile();}
    @After public void remove(){if(dir!=null){for(File child:dir.listFiles())child.delete();dir.delete();}}
    private Map<Category,JSONObject> settings()throws Exception{
        var prefs=RuntimeEnvironment.getApplication().getSharedPreferences("fixture",0);prefs.edit().putBoolean("supernova_diagnostic_logging",false).putInt("preview_accent41",-1).putString("oauth_token","CANARY").commit();
        Map<Category,JSONObject> payloads=new EnumMap<>(Category.class);payloads.put(Category.DIAGNOSTICS,PortableSettingsRegistry.export(prefs,Category.DIAGNOSTICS));payloads.put(Category.APPEARANCE,PortableSettingsRegistry.export(prefs,Category.APPEARANCE));return payloads;
    }
    @Test public void closedVerifiedArchiveRoundTripsOnlySelectedCategoriesWithoutDatabaseOrSecrets()throws Exception{
        File target=new File(dir,"verified.zip");LogicalBackupArchive.write(target,settings(),()->false);Map<Category,JSONObject> result=LogicalBackupArchive.read(target,()->false);assertEquals(settings().keySet(),result.keySet());
        assertFalse(result.containsKey(Category.SUBTITLES));assertFalse(result.get(Category.DIAGNOSTICS).getJSONObject("values").getJSONObject("supernova_diagnostic_logging").getBoolean("value"));
        try(ZipFile zip=new ZipFile(target)){assertEquals(4,zip.size());assertNull(zip.getEntry("media.db"));assertNull(zip.getEntry("credentials_db"));assertNull(zip.getEntry("user_collection"));for(var entries=zip.entries();entries.hasMoreElements();){var item=entries.nextElement();try(InputStream input=zip.getInputStream(item)){assertFalse(new String(input.readAllBytes(),StandardCharsets.UTF_8).contains("CANARY"));}}}
        byte[] before=Files.readAllBytes(target.toPath());try{LogicalBackupArchive.write(target,settings(),()->false);fail();}catch(IOException expected){}assertArrayEquals(before,Files.readAllBytes(target.toPath()));
    }
    @Test public void cancellationAfterGenerationStartsLeavesNeitherPublishedNorPartialFile()throws Exception{
        AtomicInteger calls=new AtomicInteger();File target=new File(dir,"cancelled.zip");try{LogicalBackupArchive.write(target,settings(),()->calls.incrementAndGet()>=5);fail();}catch(InterruptedIOException expected){}
        assertFalse(target.exists());assertEquals(0,dir.listFiles().length);
    }
    private void rewritten(File source,File target,String extra,String replaceName,String replaceValue)throws Exception{
        try(ZipFile zip=new ZipFile(source);ZipOutputStream output=new ZipOutputStream(new FileOutputStream(target))){for(var entries=zip.entries();entries.hasMoreElements();){ZipEntry item=entries.nextElement();output.putNextEntry(new ZipEntry(item.getName()));if(item.getName().equals(replaceName))output.write(replaceValue.getBytes(StandardCharsets.UTF_8));else try(InputStream input=zip.getInputStream(item)){input.transferTo(output);}output.closeEntry();}if(extra!=null){output.putNextEntry(new ZipEntry(extra));output.write("CANARY".getBytes(StandardCharsets.UTF_8));output.closeEntry();}}
    }
    @Test public void addedMediaAndTraversalAndUnlistedCategoriesAreRejected()throws Exception{
        File original=new File(dir,"original.zip");LogicalBackupArchive.write(original,settings(),()->false);
        for(String name:List.of("movie.mkv","credentials_db","../escape","categories/PUTIO.json","scraper_posters/image.jpg","categories/SUBTITLES.json")){File altered=new File(dir,"altered.zip");rewritten(original,altered,name,null,null);try{LogicalBackupArchive.read(altered,()->false);fail(name);}catch(IOException expected){}}
    }
    @Test public void changedPayloadCannotPassCrcAloneAndUnknownFormatsCannotRestore()throws Exception{
        File original=new File(dir,"original.zip"),altered=new File(dir,"altered.zip");LogicalBackupArchive.write(original,settings(),()->false);
        JSONObject changed=settings().get(Category.DIAGNOSTICS);changed.getJSONObject("values").getJSONObject("supernova_diagnostic_logging").put("value",true);rewritten(original,altered,null,"categories/DIAGNOSTICS.json",changed.toString());try{LogicalBackupArchive.read(altered,()->false);fail();}catch(IOException expected){}
        String manifest;try(ZipFile zip=new ZipFile(original);InputStream input=zip.getInputStream(zip.getEntry("manifest.json"))){manifest=new String(input.readAllBytes(),StandardCharsets.UTF_8);}
        rewritten(original,altered,null,"manifest.json",new JSONObject(manifest).put("formatVersion","999.0").toString());try{LogicalBackupArchive.read(altered,()->false);fail();}catch(IOException expected){}
    }
    @Test public void categoryAwareArchivePreservesPresentEmptyHomeAndPageWithoutInventingOtherCodecs()throws Exception{
        Map<BackupCategory,JSONObject> payloads=new EnumMap<>(BackupCategory.class);
        payloads.put(BackupCategory.HOME,new JSONObject().put("schema",1).put("rows",new JSONArray()));
        payloads.put(BackupCategory.CUSTOM_PAGES,new JSONObject().put("schema",1).put("page",JSONObject.NULL));
        payloads.put(BackupCategory.APPEARANCE,settings().get(Category.APPEARANCE));
        File archive=new File(dir,"logical-intent.zip");LogicalBackupArchive.writeCategories(archive,payloads,()->false);
        Map<BackupCategory,JSONObject> read=LogicalBackupArchive.readCategories(archive,()->false);
        assertEquals(payloads.keySet(),read.keySet());assertEquals(0,read.get(BackupCategory.HOME).getJSONArray("rows").length());assertTrue(read.get(BackupCategory.CUSTOM_PAGES).isNull("page"));
        assertFalse(read.containsKey(BackupCategory.WATCH_NEXT));assertEquals(24,LogicalBackupArchive.registered().size());
        try{LogicalBackupArchive.read(archive,()->false);fail("settings-only reader must not silently drop Home");}catch(IOException expected){}
        payloads.put(BackupCategory.HISTORY,new JSONObject());File unsupported=new File(dir,"unsupported.zip");
        try{LogicalBackupArchive.writeCategories(unsupported,payloads,()->false);fail();}catch(IOException expected){}assertFalse(unsupported.exists());
    }
    @Test public void manifestAndPayloadSchemasCannotCoerceFractionalOrStringVersions()throws Exception{
        File original=new File(dir,"original.zip"),altered=new File(dir,"altered.zip");LogicalBackupArchive.write(original,settings(),()->false);
        String raw;try(ZipFile zip=new ZipFile(original);InputStream input=zip.getInputStream(zip.getEntry("manifest.json"))){raw=new String(input.readAllBytes(),StandardCharsets.UTF_8);}
        for(Object invalid:List.of(1.5,"1",4294967297L)){
            JSONObject manifest=new JSONObject(raw);manifest.getJSONArray("categories").getJSONObject(0).put("schema",invalid);
            rewritten(original,altered,null,"manifest.json",manifest.toString());try{LogicalBackupArchive.readCategories(altered,()->false);fail("coerced manifest schema");}catch(IOException expected){}
            Map<Category,JSONObject> payloads=settings();payloads.get(Category.APPEARANCE).put("schema",invalid);
            File bad=new File(dir,"bad-schema.zip");try{LogicalBackupArchive.write(bad,payloads,()->false);fail("coerced payload schema");}catch(IOException expected){}assertFalse(bad.exists());
        }
    }
}
