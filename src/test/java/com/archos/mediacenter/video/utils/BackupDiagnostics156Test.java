package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Application;
import android.os.SystemClock;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import com.archos.mediacenter.video.utils.backup.LogicalBackupArchive;
import com.archos.mediacenter.video.utils.backup.VerifiedBackupPublication;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class BackupDiagnostics156Test
{
    @Test public void destinationCloseFailureRetainsCopyStageBytesAndActualCauseInReport() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        Diagnostics.clearAll(app);
        File folder = Files.createTempDirectory("backup-stages").toFile();
        File archive = new File(folder, "archive.zip");
        try
        {
            LogicalBackupArchive.writeCategories(
                archive,
                Map.of(BackupCategory.CUSTOM_PAGES, new JSONObject().put("schema", 1).put("page", JSONObject.NULL)),
                () -> false);
            VerifiedBackupPublication.Prepared prepared = VerifiedBackupPublication.prepare(archive, () -> false);
            try
            {
                VerifiedBackupPublication.publish(prepared, new VerifiedBackupPublication.Destination() {
                    @Override public OutputStream openWrite()
                    {
                        return new ByteArrayOutputStream() {
                            @Override public void close() throws IOException
                            {
                                throw new IOException("Provider close refused");
                            }
                        };
                    }
                    @Override public InputStream openRead()
                    {
                        throw new AssertionError("Readback after failed close");
                    }
                    @Override public boolean removeIncomplete()
                    {
                        return true;
                    }
                }, () -> false);
                fail("Close failure reported success");
            }
            catch (VerifiedBackupPublication.Failure expected)
            {
            }
            Object flight = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            Method snapshot = flight.getClass().getDeclaredMethod("snapshot", long.class);
            snapshot.setAccessible(true);
            boolean found = false;
            for (String line : ((String)snapshot.invoke(flight, SystemClock.elapsedRealtime())).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject record = new JSONObject(line);
                if (!record.optString("event").equals("backup_failed"))
                    continue;
                found = true;
                assertEquals("copy", record.getString("stage"));
                assertEquals(prepared.bytes, record.getLong("bytes"));
                assertTrue(record.getString("trace").contains("Provider close refused"));
            }
            assertTrue(found);
            assertTrue(archive.isFile());
        }
        finally
        {
            archive.delete();
            folder.delete();
            Diagnostics.setEnabled(app, false);
        }
    }
}
