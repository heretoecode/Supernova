package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import androidx.preference.PreferenceManager;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class DiagnosticsControls156Test
{
    @Test public void oldNormalLevelKeepsDetailAndClearCannotBeUndoneByQueuedOldEvents() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(app).edit().putString(Diagnostics.LEVEL, "normal").commit();
        Diagnostics.setEnabled(app, true);
        ExecutorService writer = ReflectionHelpers.getStaticField(Diagnostics.class, "WORK");
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        writer.submit(() -> {
            entered.countDown();
            try
            {
                release.await(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException interrupted)
            {
                Thread.currentThread().interrupt();
            }
        });
        assertTrue(entered.await(5, TimeUnit.SECONDS));
        Diagnostics.event("artwork_OLD_CLEAR_CANARY");
        try
        {
            Diagnostics.clearAll(app);
        }
        finally
        {
            release.countDown();
        }
        writer.submit(() -> {}).get(5, TimeUnit.SECONDS);
        Diagnostics.event("artwork_NEW_DETAIL_CANARY");
        writer.submit(() -> {}).get(5, TimeUnit.SECONDS);
        StringBuilder text = new StringBuilder();
        File directory = new File(app.getFilesDir(), "supernova-diagnostics");
        for (File file : DiagnosticArchive.files(directory))
        {
            text.append(Files.readString(file.toPath()));
        }
        assertFalse(text.toString().contains("OLD_CLEAR_CANARY"));
        assertTrue(text.toString().contains("NEW_DETAIL_CANARY"));
        assertTrue(Diagnostics.enabled());
        Diagnostics.setEnabled(app, false);
    }
}
