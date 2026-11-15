package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import com.archos.mediacenter.video.metadata.WrongMatches;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = {23, 30}, manifest = Config.NONE)
public class WrongMatchesTest
{
    @Test public void adapterIncludesPutioAndKeepsTitleKindsSeparate() throws Exception
    {
        try (var rows = new android.database.MatrixCursor(
            com.archos.mediacenter.video.metadata.WrongMatchInventory.projection()))
        {
            rows.addRow(new Object[] {1, "putio://1", 12, 100, null, 10});
            rows.addRow(new Object[] {2, "/Movies/file", 11, 101, 10, null});
            var inventory = com.archos.mediacenter.video.metadata.WrongMatchInventory.read(rows, () -> false);
            assertEquals(2, inventory.size());
            assertEquals("tv", inventory.get(0).kind());
            assertEquals("movie", inventory.get(1).kind());
            assertTrue(!inventory.get(0).matchRevision().contains("putio"));
        }
    }

    @Test public void wholeSeriesPersistsAndOnlyCommittedOrChangedMatchesClear() throws Exception
    {
        var preferences = RuntimeEnvironment.getApplication().getSharedPreferences("wrong-test", 0);
        preferences.edit().clear().commit();
        var store = new WrongMatches(preferences);
        var episode1 = new WrongMatches.Member(1, "tv", 10, "physical:1:match:10:1:1");
        var episode2 = new WrongMatches.Member(2, "tv", 10, "physical:2:match:10:1:2");
        var movie = new WrongMatches.Member(3, "movie", 10, "physical:3:match:10");
        var inventory = List.of(episode1, episode2, movie);
        var selection = store.mark("tv", 10, inventory);
        assertEquals(List.of(1L, 2L), selection.videoIds());
        assertEquals(1, selection.category());
        assertEquals(List.of(1L, 2L), new WrongMatches(preferences).read(inventory));
        assertTrue(preferences.getBoolean("incorrect:1", false));
        store.applied(1);
        assertEquals(List.of(2L), store.read(inventory));
        var changed = new WrongMatches.Member(2, "tv", 11, "physical:2:match:11:1:2");
        assertTrue(store.read(List.of(episode1, changed, movie)).isEmpty());
        store.mark("movie", 10, inventory);
        assertEquals(List.of(3L), store.read(inventory));
        assertTrue(store.read(List.of(episode1, episode2)).isEmpty());
    }
}
