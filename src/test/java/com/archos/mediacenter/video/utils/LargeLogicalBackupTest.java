package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import android.app.Application;
import android.content.Context;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import com.archos.mediacenter.video.utils.backup.LogicalBackupArchive;
import com.archos.mediacenter.video.utils.backup.LogicalBackupImport;
import com.archos.mediacenter.video.utils.backup.VerifiedBackupPublication;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
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
}
