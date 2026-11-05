package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PlaybackEpisodesTest
{
    private Entry episode(int id, int number, boolean hidden)
    {
        Episode episode = mock(Episode.class);
        when(episode.getId()).thenReturn((long)id);
        when(episode.getEpisodeNumber()).thenReturn(number);
        when(episode.getSeasonNumber()).thenReturn(2);
        when(episode.getEpisodeName()).thenReturn("Episode " + number);
        when(episode.getFilePath()).thenReturn("/media/" + id + ".mkv");
        when(episode.getFileUri()).thenReturn(Uri.parse("file:///media/" + id + ".mkv"));
        when(episode.isUserHidden()).thenReturn(hidden);
        return new Entry(episode, 0, 123, "");
    }

    @Test public void cachedNavigationAndSeasonStripSkipHiddenEvenWhenLibraryShowsThem()
    {
        List<Entry> source = List.of(episode(1, 1, false), episode(2, 2, true), episode(3, 3, false));
        assertEquals(3, PlaybackEpisodes.adjacent(source, 1, 1).getId());
        List<PlaybackEpisodes.Item> items = PlaybackEpisodes.season(RuntimeEnvironment.getApplication(), source, 1);
        assertEquals(2, items.size());
        assertEquals("Episode 3", items.get(1).title);
        assertEquals(3, PlaybackEpisodes.adjacent(source, 2, 1).getId());
    }
}
