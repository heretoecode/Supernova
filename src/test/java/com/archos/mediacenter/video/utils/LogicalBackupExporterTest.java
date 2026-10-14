package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LogicalBackupExporterTest {
    private Context app;private SharedPreferences prefs;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().putString("oauth_token","AUTH_CANARY").putString("smb_password","PASSWORD_CANARY").commit();app.deleteDatabase("media.db");app.deleteDatabase(ViewingHistory.DATABASE);}
    private void seed() {
        try(VideoOpenHelper helper=new VideoOpenHelper(app)) {
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,Archos_hiddenByUser,_data,bookmark,title) VALUES(10,10,3,1,'file:///excluded/PATH_CANARY.mkv',-2,'TITLE_CANARY'),(11,11,3,0,'file:///other/second.mkv',5555,'SECOND_CANARY'),(12,12,2,0,'file:///audio/AUDIO_CANARY.mp3',0,'AUDIO_CANARY')");
            db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie) VALUES(100,10,123,9,'MOVIE_CANARY'),(101,11,456,0,'OTHER_CANARY')");
            db.execSQL("INSERT INTO SHOW(_id,s_online_id,Nova_pinned,name_show,s_genres) VALUES(20,789,8,'SHOW_CANARY','Drama')");db.execSQL("UPDATE files SET ArchosMediaScraper_type=11,ArchosMediaScraper_id=100 WHERE _id=10");db.execSQL("UPDATE files SET ArchosMediaScraper_type=11,ArchosMediaScraper_id=101 WHERE _id=11");
        }
        prefs.edit().putBoolean("hide_watched",true).putBoolean("supernova_library_policy_initialized",true).putBoolean("supernova_onboarding_complete",true).putStringSet("supernova_library_roots",Set.of("file:///allowed")).commit();
    }
    private String contents(File file)throws Exception{StringBuilder result=new StringBuilder();try(ZipFile zip=new ZipFile(file)){var entries=zip.entries();while(entries.hasMoreElements()){var entry=entries.nextElement();try(InputStream input=zip.getInputStream(entry)){result.append(new String(input.readAllBytes(),StandardCharsets.UTF_8));}}}return result.toString();}
    @Test public void actualCatalogIncludesHiddenWatchedExcludedFilesAndOrphanShowsWithoutAudio()throws Exception {
        seed();var catalog=LogicalBackupCatalog.capture(app,()->false);assertEquals(2,catalog.physical.size());assertEquals(Set.of(10L,11L),new HashSet<>(catalog.physical.stream().map(x->x.getId()).toList()));
        assertEquals(2,catalog.snapshot.movies.size());assertEquals(1,catalog.snapshot.shows.size());assertEquals(789,catalog.snapshot.shows.get(0).onlineId);assertTrue(catalog.hidden.get(10L));assertEquals(3,catalog.pins.size());
        assertTrue(prefs.getBoolean("hide_watched",false));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void freshAllRegisteredCategoriesCreateVerifiedArchiveWithoutCreatingDatabaseStores()throws Exception {
        File archive=LogicalBackupExporter.preparePrivate(app,LogicalBackupArchive.registered(),()->false);assertTrue(archive.isFile());var read=LogicalBackupArchive.readCategories(archive,()->false);assertEquals(LogicalBackupArchive.registered(),read.keySet());assertEquals(24,read.size());
        assertFalse("Export must not create a media database",VideoOpenHelper.getDatabaseFile(app).exists());assertFalse("Export must not create a viewing database",app.getDatabasePath(ViewingHistory.DATABASE).exists());assertFalse(contents(archive).contains("CANARY"));try(ZipFile zip=new ZipFile(archive)){assertNotNull(zip.getEntry("RESTORE_INSTRUCTIONS.txt"));}
    }
    @Test public void sourceExportKeepsHiddenMembershipLogicalAndNeverCopiesMetadataArtworkAccountsOrLocationsByDefault()throws Exception {
        seed();JSONArray rows=new JSONArray().put(new JSONObject().put("id","custom:fixture").put("name","Fixture row").put("maximum",15).put("members",new JSONArray().put("v10").put("s20")));
        prefs.edit().putString("preview_home_rows41",rows.toString()).commit();var selected=EnumSet.of(BackupCategory.HOME,BackupCategory.PINNED,BackupCategory.HIDDEN,BackupCategory.SOURCES,BackupCategory.APPEARANCE);
        File archive=LogicalBackupExporter.preparePrivate(app,selected,()->false);var read=LogicalBackupArchive.readCategories(archive,()->false);assertEquals(selected,read.keySet());String text=contents(archive);
        for(String forbidden:List.of("PATH_CANARY","TITLE_CANARY","MOVIE_CANARY","SHOW_CANARY","AUTH_CANARY","PASSWORD_CANARY","file:///","smb://"))assertFalse(forbidden,text.contains(forbidden));
        JSONObject custom=read.get(BackupCategory.HOME).getJSONArray("rows").getJSONObject(0);assertEquals("custom:fixture",custom.getString("id"));assertEquals("[\"tmdb:movie:123\",\"tmdb:tv:789\"]",custom.getJSONArray("members").toString());
        assertEquals(1,read.get(BackupCategory.HIDDEN).getJSONArray("rows").length());assertEquals(2,read.get(BackupCategory.PINNED).getJSONArray("rows").length());assertFalse(read.containsKey(BackupCategory.LOCATIONS));
    }
    @Test public void locationsAreProducedOnlyWhenExplicitlySelectedAndExcludePutioOwnership()throws Exception {
        prefs.edit().putStringSet("supernova_library_roots",Set.of("file:///PRIVATE_FOLDER","putio:///ACCOUNT_CANARY")).commit();
        var no=LogicalBackupExporter.collect(app,Set.of(BackupCategory.SOURCES),()->false);assertFalse(no.toString().contains("PRIVATE_FOLDER"));assertFalse(no.toString().contains("ACCOUNT_CANARY"));
        var yes=LogicalBackupExporter.collect(app,Set.of(BackupCategory.SOURCES,BackupCategory.LOCATIONS),()->false);assertTrue(yes.get(BackupCategory.LOCATIONS).toString().contains("PRIVATE_FOLDER"));assertFalse(yes.toString().contains("ACCOUNT_CANARY"));
        assertTrue(LogicalBackupExporter.collect(app,Set.of(BackupCategory.LOCATIONS),()->false).get(BackupCategory.LOCATIONS).getJSONArray("rows").length()==0);
    }
    @Test public void absentUnsupportedOrUnsafeSelectionsFailBeforeAnyBackupFile()throws Exception {
        for(Set<BackupCategory> selection:List.of(Set.<BackupCategory>of(),Set.of(BackupCategory.MATCHES),Set.of(BackupCategory.APPEARANCE,BackupCategory.MATCHES)))try{LogicalBackupExporter.preparePrivate(app,selection,()->false);fail();}catch(IOException expected){}
        prefs.edit().putStringSet("supernova_library_roots",Set.of("smb://user:PASSWORD_CANARY@server/share")).commit();try{LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.SOURCES,BackupCategory.LOCATIONS),()->false);fail();}catch(IOException expected){assertFalse(expected.getMessage().contains("CANARY"));}
        assertFalse(new File(app.getCacheDir(),"supernova-logical-backups").exists());assertFalse(VideoOpenHelper.getDatabaseFile(app).exists());assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void cancelledSourceReadDoesNotCreateArchiveOrNewViewingStore()throws Exception {
        seed();try{LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.HOME),()->true);fail();}catch(InterruptedIOException expected){}
        assertFalse(new File(app.getCacheDir(),"supernova-logical-backups").exists());assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());
    }
    @Test public void settingsOnlyExportDoesNotOpenOrMigrateAnIncompatibleMediaStore()throws Exception {
        seed();try(var db=android.database.sqlite.SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READWRITE)){db.setVersion(900);}
        File file=LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.APPEARANCE),()->false);assertTrue(file.isFile());
        try{LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.PINNED),()->false);fail();}catch(IOException expected){}
        try(var db=android.database.sqlite.SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY)){assertEquals(900,db.getVersion());}
    }
    @Test public void productionPlaylistArchiveRoundtripContainsOnlyPublicDefinitionsAndRestoresLocalOnly()throws Exception {
        seed();try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(7,'Saved family',99887766,0),(8,'Deleted',11223344,2)");db.execSQL("INSERT INTO video_list_table(list_id,m_online_id,e_online_id,sync_status) VALUES(7,123,-1,0),(7,-1,456,0),(7,999,-1,2)");}
        File archive=LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.PLAYLISTS),()->false);String bytes=contents(archive);for(String excluded:List.of("99887766","11223344","PATH_CANARY","AUTH_CANARY","PASSWORD_CANARY","trakt_id","sync_status"))assertFalse(excluded,bytes.contains(excluded));
        var payload=LogicalBackupArchive.readCategories(archive,()->false);assertEquals(Set.of(BackupCategory.PLAYLISTS),payload.keySet());assertEquals(1,payload.get(BackupCategory.PLAYLISTS).getJSONArray("rows").length());String identity=payload.get(BackupCategory.PLAYLISTS).getJSONArray("rows").getJSONObject(0).getString("id");
        try(var imported=LogicalBackupImport.prepare(app,new FileInputStream(archive),()->false)){var transaction=new PreferenceRestoreTransaction(app);transaction.apply(transaction.prepare(imported.payloads(()->false),null),()->false);}
        try(VideoOpenHelper helper=new VideoOpenHelper(app);var rows=helper.getReadableDatabase().rawQuery("SELECT trakt_id,sync_status FROM list_table ORDER BY _id",null)){assertTrue(rows.moveToFirst());assertEquals(99887766,rows.getLong(0));assertTrue(rows.moveToNext());assertEquals(11223344,rows.getLong(0));assertTrue(rows.moveToNext());assertTrue(rows.isNull(0));assertEquals(4,rows.getInt(1));assertFalse(rows.moveToNext());}
        // Restoring onto the source device preserves its receiving account definition. The new local copy
        // receives its own opaque identity, so subsequent backups contain both without alias collisions.
        var exported=LogicalBackupExporter.collect(app,Set.of(BackupCategory.PLAYLISTS),()->false).get(BackupCategory.PLAYLISTS).getJSONArray("rows");assertEquals(2,exported.length());assertEquals(identity,exported.getJSONObject(0).getString("id"));assertNotEquals(identity,exported.getJSONObject(1).getString("id"));
        assertEquals(exported.getJSONObject(1).getString("id"),prefs.getString(PortableSavedPlaylists.referenceKey(9),null));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));assertTrue(archive.delete());
    }
    @Test public void selectedPlaylistExportOnNewDevicePreservesPendingWithoutCreatingMediaStore()throws Exception {
        var payload=new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","playlist:12345678-1234-1234-1234-123456789012:1").put("name","Pending family").put("members",new JSONArray().put("tmdb:movie:123"))));prefs.edit().putString(PortableSavedPlaylists.PENDING,payload.toString()).commit();
        File archive=LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.PLAYLISTS),()->false);assertEquals(payload.toString(),LogicalBackupArchive.readCategories(archive,()->false).get(BackupCategory.PLAYLISTS).toString());assertFalse(VideoOpenHelper.getDatabaseFile(app).exists());assertTrue(archive.delete());
    }
}
