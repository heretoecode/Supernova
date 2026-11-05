package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertArrayEquals;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediacenter.video.utils.backup.LogicalBackupExporter;
import java.util.EnumSet;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import com.archos.mediacenter.video.utils.backup.LogicalBackupArchive;
import com.archos.mediacenter.video.utils.backup.LogicalBackupImport;
import com.archos.mediacenter.video.utils.backup.VerifiedBackupPublication;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LargeLogicalBackupTest
{
    @Test public void sixtyOneThousandMatchesSurviveClosedVerificationPublicationAndRestorePreview() throws Exception
    {
        Context context = RuntimeEnvironment.getApplication();
        JSONArray rows = new JSONArray();
        for (int i = 1; i <= 61050; i++)
        {
            rows.put(new JSONObject()
                         .put("physical", String.format("file:%064x", i))
                         .put("identity", "tmdb:movie:" + i)
                         .put("episodeProvider", JSONObject.NULL));
        }
        JSONObject payload = new JSONObject().put("schema", 1).put("rows", rows);
        assertTrue(payload.toString().getBytes(StandardCharsets.UTF_8).length > 4 * 1024 * 1024);
        File directory = Files.createTempDirectory(context.getCacheDir().toPath(), "large-backup").toFile();
        File archive = new File(directory, "large.zip");
        try
        {
            LogicalBackupArchive.writeCategories(archive, Map.of(BackupCategory.MATCHES, payload), () -> false);
            VerifiedBackupPublication.Prepared prepared = VerifiedBackupPublication.prepare(archive, () -> false);
            assertTrue(prepared.bytes > 0);
            try (LogicalBackupImport.Prepared imported =
                     LogicalBackupImport.prepare(context, new FileInputStream(archive), () -> false))
            {
                JSONArray restored = imported.payloads(() -> false).get(BackupCategory.MATCHES).getJSONArray("rows");
                assertEquals(61050, restored.length());
                assertEquals(rows.getJSONObject(0).toString(), restored.getJSONObject(0).toString());
                assertEquals(rows.getJSONObject(61049).toString(), restored.getJSONObject(61049).toString());
            }
            assertTrue(archive.isFile());
            assertEquals(1, directory.listFiles().length);
        }
        finally
        {
            archive.delete();
            directory.delete();
        }
        assertFalse(directory.exists());
    }
    @Test public void actualExporterRetainsEveryMatchAndPreviousBackupForOwnersDefaultSelection() throws Exception
    {
        Context context = RuntimeEnvironment.getApplication();
        context.deleteDatabase("media.db");
        try (VideoOpenHelper helper = new VideoOpenHelper(context))
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.beginTransaction();
            try (SQLiteStatement file = db.compileStatement(
                     "INSERT INTO files"
                     + "(_id,remote_id,media_type,Archos_hiddenByUser,_data,bookmark,title,"
                     + "ArchosMediaScraper_type,ArchosMediaScraper_id) VALUES(?,?,3,0,?,0,'fixture',11,?)");
                 SQLiteStatement movie = db.compileStatement(
                     "INSERT INTO MOVIE"
                     + "(_id,video_id,m_online_id,Nova_pinned,name_movie) VALUES(?,?,?,0,'fixture')"))
            {
                for (int i = 1; i <= 61050; i++)
                {
                    file.bindLong(1, i);
                    file.bindLong(2, i);
                    file.bindString(3, i % 2 == 0 ? "/storage/USB/" + i + ".mkv"
                                                  : "webdavs://media.example/films/" + i + ".mkv");
                    file.bindLong(4, i);
                    file.executeInsert();
                    movie.bindLong(1, i);
                    movie.bindLong(2, i);
                    movie.bindLong(3, i);
                    movie.executeInsert();
                }
                db.setTransactionSuccessful();
            }
            finally
            {
                db.endTransaction();
            }
        }
        EnumSet<BackupCategory> selected = EnumSet.noneOf(BackupCategory.class);
        for (BackupCategory category : BackupCategory.values())
        {
            if (category.defaultIncluded)
            {
                selected.add(category);
            }
        }
        File previous = new File(context.getCacheDir(), "previous-backup.zip");
        byte[] original = {12, 34, 56};
        Files.write(previous.toPath(), original);
        File archive = LogicalBackupExporter.preparePrivate(context, selected, () -> false);
        try
        {
            VerifiedBackupPublication.prepare(archive, () -> false);
            Map<BackupCategory, JSONObject> restored = LogicalBackupArchive.readCategories(archive, () -> false);
            assertEquals(selected, restored.keySet());
            JSONArray matches = restored.get(BackupCategory.MATCHES).getJSONArray("rows");
            assertEquals(61050, matches.length());
            Set<String> identities = new HashSet<>();
            for (int i = 0; i < matches.length(); i++)
                assertTrue(identities.add(matches.getJSONObject(i).getString("identity")));
            for (int i = 1; i <= 61050; i++)
                assertTrue(identities.contains("tmdb:movie:" + i));
            assertArrayEquals(original, Files.readAllBytes(previous.toPath()));
        }
        finally
        {
            archive.delete();
            previous.delete();
        }
    }
}
