package com.archos.mediacenter.video.scanning;

import com.archos.mediacenter.video.leanback.files.FilesPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/** Scan library narrates every step, never a bare "Scanning" (FILES.md, `files-v2/tv/23`). */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ScanNarrationTest
{
    private static ScanProgress p(long checked, long total, long added, long removed, boolean running,
                                  ScanProgress.Failure failure)
    {
        return new ScanProgress("b", "s", "Seedbox", checked, total, added, removed, 0, 0, 1000, running, false,
                                failure);
    }

    @Test
    public void everyStepInWords()
    {
        assertEquals("Connecting to Seedbox…", FilesPage.narrate(p(0, 0, 0, 0, true, ScanProgress.Failure.NONE)));
        assertEquals("Looking in Seedbox · 42%",
                     FilesPage.narrate(p(42, 100, 0, 0, true, ScanProgress.Failure.NONE)));
        assertEquals("Found 3 new videos", FilesPage.narrate(p(100, 100, 3, 0, true, ScanProgress.Failure.NONE)));
        assertEquals("Done · 3 new · 1 missing",
                     FilesPage.narrate(p(100, 100, 3, 1, false, ScanProgress.Failure.NONE)));
        assertEquals("Couldn't connect to Seedbox",
                     FilesPage.narrate(p(0, 0, 0, 0, false, ScanProgress.Failure.UNAVAILABLE)));
    }
}
