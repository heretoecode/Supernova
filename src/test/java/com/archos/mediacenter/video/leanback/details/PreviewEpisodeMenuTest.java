package com.archos.mediacenter.video.leanback.details;

import android.app.Application;

import com.archos.mediacenter.video.browser.adapters.object.Episode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Deleting an episode with earlier ones in the library (owner, 18 Nov 2026; OWNER_NOTES §2). */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewEpisodeMenuTest {
    private static Episode episode(int season, int number) {
        return new Episode(season * 100 + number, 7, season, number, "Made-up Episode " + number, 0, 0, "", "",
                           "A Long Made-up Series Name", "/TV/s" + season + "e" + number + ".mkv", null, null, 1800000,
                           0, 0, 0, false, false, false, false, 1, 0, 1920, 1080, null, null, null, null, 0, 1, 1000);
    }

    @Test
    public void earlierEpisodesOfTheSameSeasonOnly() {
        List<Episode> season = new ArrayList<>();
        season.add(episode(1, 2));
        season.add(episode(1, 1));
        season.add(episode(1, 4));
        season.add(episode(2, 1));
        Episode third = episode(1, 3);
        season.add(third);
        List<Episode> earlier = PreviewEpisodeMenu.earlier(third, season);
        assertEquals(2, earlier.size());
        assertEquals(1, earlier.get(0).getEpisodeNumber());
        assertEquals(2, earlier.get(1).getEpisodeNumber());
        assertEquals("1 and 2", PreviewEpisodeMenu.numbers(earlier));
        assertTrue(PreviewEpisodeMenu.earlier(episode(1, 1), season).isEmpty());
    }

    @Test
    public void wording() {
        assertEquals("Delete both", PreviewEpisodeMenu.deleteAll(2));
        assertEquals("Delete all three", PreviewEpisodeMenu.deleteAll(3));
        assertEquals("Delete all 14", PreviewEpisodeMenu.deleteAll(14));
        List<Episode> three = new ArrayList<>();
        three.add(episode(1, 1));
        three.add(episode(1, 2));
        three.add(episode(1, 5));
        assertEquals("1, 2 and 5", PreviewEpisodeMenu.numbers(three));
        assertEquals("Episode 3 · Made-up Episode 3", PreviewEpisodeMenu.name(episode(1, 3)));
    }
}
