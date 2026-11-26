package com.archos.mediacenter.video.leanback.files;

import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** FILES.md browsing: the folder menu offers only what applies; folder lines carry their size. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FolderMenuTest
{
    private static Episode episode(long id, String show)
    {
        return new Episode(id, id, 1, (int) id, "Part " + id, 0, 0, "", "", show, "/storage/TV/" + show + "/e" + id
            + ".mkv", null, null, 0, 0, 0, 0, false, false, false, false, 1, 0, 0, 0, null, null, null, null, 0, 1,
            2_000_000_000L);
    }

    @Test
    public void folderMenuOffersOnlyWhatApplies()
    {
        assertEquals(Arrays.asList("Open", "Add to library", "Exclude"), FolderBrowser.menuItems(true, false, false,
                                                                                              false));
        assertEquals(Arrays.asList("Open", "Scan this folder", "Fix match", "Exclude"),
                     FolderBrowser.menuItems(true, true, false, true));
    }

    @Test
    public void fixMatchOnlyOnAFolderHoldingOneTitle()
    {
        List<Base> oneShow = Arrays.asList(episode(1, "Tidelands"), episode(2, "Tidelands"));
        List<Base> twoShows = Arrays.asList(episode(1, "Tidelands"), episode(2, "Harbour Lights"));
        assertTrue(FolderBrowser.oneTitle(oneShow));
        assertFalse(FolderBrowser.oneTitle(twoShows));
    }

    @Test
    public void folderLineCarriesItsSize()
    {
        assertEquals("Folder · 31 videos · 48.6 GB", FolderBrowser.folderLine(31, "48.6 GB"));
        assertEquals("Folder", FolderBrowser.folderLine(0, ""));
    }
}
