package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class HistoryMigration157Test
{
    @Test public void firstInventoryMigrationCannotOverwriteAPlaybackWriteAfterItsInitialRead() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        app.deleteDatabase(ViewingHistory.DATABASE);
        PreferenceManager.getDefaultSharedPreferences(app).edit().clear().commit();
        Video legacy = mock(Video.class);
        when(legacy.getFilePath()).thenReturn("file:///history-race.mkv");
        when(legacy.getId()).thenReturn(31L);
        when(legacy.getLastPlayed()).thenReturn(100L);
        when(legacy.getDurationMs()).thenReturn(10000);
        CountDownLatch inventoryRead = new CountDownLatch(1);
        CountDownLatch continueMigration = new CountDownLatch(1);
        when(legacy.getResumeMs()).thenAnswer(call -> {
            inventoryRead.countDown();
            assertTrue(continueMigration.await(3, TimeUnit.SECONDS));
            return 100;
        });
        PreviewLibraryLoader.Entry entry = new PreviewLibraryLoader.Entry(legacy, 0, 0, "");
        var worker = Executors.newSingleThreadExecutor();
        try
        {
            var migration = worker.submit(() -> ViewingHistory.reconcile(app, List.of(entry)));
            assertTrue(inventoryRead.await(3, TimeUnit.SECONDS));
            VideoDbInfo current = new VideoDbInfo(Uri.parse("file:///history-race.mkv"));
            current.resume = 777;
            current.duration = 10000;
            current.lastTimePlayed = 200;
            ViewingHistory.record(app, current, false);
            ViewingHistory.awaitWrites();
            continueMigration.countDown();
            migration.get(3, TimeUnit.SECONDS);
            ViewingHistory.Record saved = ViewingHistory.read(app).get(ViewingHistory.identity(entry));
            assertEquals(777, saved.position);
            assertEquals(200, saved.played);
            assertEquals(10000, saved.duration);
        }
        finally
        {
            continueMigration.countDown();
            worker.shutdownNow();
            ViewingHistory.awaitWrites();
            app.deleteDatabase(ViewingHistory.DATABASE);
        }
    }
}
