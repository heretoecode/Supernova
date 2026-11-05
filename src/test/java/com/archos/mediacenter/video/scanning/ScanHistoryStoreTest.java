package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class ScanHistoryStoreTest
{
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    private ScanProgress row(int index, ScanProgress.Failure failure)
    {
        return new ScanProgress("job-" + index, "source", "USB drive Backup #1", 50, -1, 3, 1, 2, 1000 + index, 420,
                                false, false, failure);
    }

    @Test public void keepsTenNewestFinishedSourcesAcrossProcessRestart() throws Exception
    {
        File location = new File(temporary.getRoot(), "scans");
        ScanHistoryStore store = new ScanHistoryStore(location);
        store.load();
        assertTrue(store.recent().isEmpty());
        for (int i = 0; i < 14; i++)
            store.append(row(i, i == 13 ? ScanProgress.Failure.CANCELLED : ScanProgress.Failure.NONE));
        List<ScanProgress> published = store.recent();
        ScanHistoryStore restarted = new ScanHistoryStore(location);
        restarted.load();
        assertEquals(10, restarted.recent().size());
        assertEquals("job-13", restarted.recent().get(0).batchId);
        assertEquals("job-4", restarted.recent().get(9).batchId);
        ScanProgress item = restarted.recent().get(0);
        assertEquals(1013, item.startedUtcMs);
        assertEquals(420, item.durationMs);
        assertEquals(3, item.added);
        assertEquals(1, item.removed);
        assertEquals(2, item.updated);
        assertEquals(-1, item.total);
        assertEquals("Scan stopped", item.failureReason());
        assertThrows(UnsupportedOperationException.class, () -> published.clear());
        store.append(row(14, ScanProgress.Failure.NONE));
        assertEquals("job-13", published.get(0).batchId);
    }

    @Test public void corruptHistoryIsReportedAndDoesNotPublishPartialData() throws Exception
    {
        File location = temporary.newFile();
        ScanHistoryStore store = new ScanHistoryStore(location);
        store.append(row(1, ScanProgress.Failure.NONE));
        try (FileOutputStream corrupt = new FileOutputStream(location))
        {
            corrupt.write(new byte[] {0, 0, 0, 99});
        }
        assertThrows(IOException.class, store::load);
        assertEquals("job-1", store.recent().get(0).batchId);
    }
}
