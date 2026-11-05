package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/** Checks the Chapters · Episodes strip: what shows, the remote path through it, and what OK does. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PlayerPartsStripTest
{
    private static final class Fixture implements PlayerPartsStrip.Source
    {
        PlaybackParts parts;
        List<PlaybackEpisodes.Item> episodes = new ArrayList<>();
        long position;
        long current = 10;
        long seekedTo = -1;
        PlaybackEpisodes.Item played;

        @Override
        public PlaybackParts parts()
        {
            return parts;
        }

        @Override
        public List<PlaybackEpisodes.Item> episodes()
        {
            return episodes;
        }

        @Override
        public long positionMs()
        {
            return position;
        }

        @Override
        public long currentEpisodeId()
        {
            return current;
        }

        @Override
        public PartPictures.FrameSource frames()
        {
            return null;
        }

        @Override
        public void seekTo(long positionMs)
        {
            seekedTo = positionMs;
        }

        @Override
        public void playEpisode(PlaybackEpisodes.Item item)
        {
            played = item;
        }
    }

    private static PlaybackParts parts()
    {
        List<PlaybackParts.Part> chapters = Arrays.asList(
            new PlaybackParts.Part("Chapter 1", PlaybackParts.Kind.CHAPTER, 0, 600000),
            new PlaybackParts.Part("Chapter 2", PlaybackParts.Kind.CHAPTER, 600000, 1200000),
            new PlaybackParts.Part("Chapter 3", PlaybackParts.Kind.CHAPTER, 1200000, 1800000));
        return PlaybackParts.merge(chapters, Collections.emptyList(), Collections.emptyList(), 1800000, true);
    }

    private static PlaybackEpisodes.Item episode(long id, int number, boolean watched)
    {
        Episode episode = mock(Episode.class);
        when(episode.getId()).thenReturn(id);
        when(episode.getEpisodeNumber()).thenReturn(number);
        when(episode.getEpisodeName()).thenReturn("Name " + number);
        when(episode.getFileUri()).thenReturn(Uri.parse("file:///e" + number + ".mkv"));
        when(episode.getDurationMs()).thenReturn(43 * 60000);
        when(episode.isWatched()).thenReturn(watched);
        return new PlaybackEpisodes.Item(episode);
    }

    private static final class Host
    {
        ActivityController<Activity> controller;
        View bar;
        long[] lit = {-1, -1};
        PlayerPartsStrip strip;
    }

    private static Host host(Fixture fixture)
    {
        Host host = new Host();
        host.controller = Robolectric.buildActivity(Activity.class).setup();
        Activity activity = host.controller.get();
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        host.bar = new TextView(activity);
        host.bar.setFocusable(true);
        host.bar.setFocusableInTouchMode(true);
        root.addView(host.bar);
        host.strip = new PlayerPartsStrip(activity, fixture, host.bar, (start, end) -> {
            host.lit[0] = start;
            host.lit[1] = end;
        }, () -> {}, Collections.emptyList());
        root.addView(host.strip.view());
        activity.setContentView(root);
        host.bar.requestFocus();
        return host;
    }

    @Test
    public void nothingToShowMeansNoButtonsAndDownDoesNothing()
    {
        Fixture fixture = new Fixture();
        Host host = host(fixture);
        try {
            assertEquals(View.GONE, host.strip.view().getVisibility());
            assertFalse(host.strip.open());
            assertTrue(host.bar.hasFocus());
        } finally {
            host.controller.pause().stop().destroy();
        }
    }

    @Test
    public void downOpensChaptersThenCardsAndOkJumpsToThePart()
    {
        Fixture fixture = new Fixture();
        fixture.parts = parts();
        fixture.position = 700000;
        Host host = host(fixture);
        try {
            assertEquals(View.VISIBLE, host.strip.tabView(PlayerPartsStrip.CHAPTERS).getVisibility());
            assertEquals(View.GONE, host.strip.tabView(PlayerPartsStrip.EPISODES).getVisibility());
            assertTrue(host.strip.open());
            assertTrue(host.strip.expanded());
            assertTrue(host.strip.tabView(PlayerPartsStrip.CHAPTERS).hasFocus());
            assertEquals(3, host.strip.cardViews().size());
            host.strip.vertical(false, host.strip.tabView(PlayerPartsStrip.CHAPTERS));
            View playing = host.strip.cardViews().get(1);
            assertTrue("Down lands on the part that is playing", playing.hasFocus());
            assertEquals(600000, host.lit[0]);
            assertEquals(1200000, host.lit[1]);
            assertTrue(PlayerPartsStripTestText.of(playing).contains("Playing · from 10:00"));
            assertTrue(PlayerPartsStripTestText.of(host.strip.cardViews().get(2)).contains("From 20:00"));
            playing.performClick();
            assertEquals(600000, fixture.seekedTo);
            assertFalse(host.strip.expanded());
            assertTrue(host.bar.hasFocus());
        } finally {
            host.controller.pause().stop().destroy();
        }
    }

    @Test
    public void episodesOpenAtTheOnePlayingAndUpUpReturnsToTheBar()
    {
        Fixture fixture = new Fixture();
        fixture.episodes.add(episode(9, 9, true));
        fixture.episodes.add(episode(10, 10, false));
        fixture.episodes.add(episode(11, 11, false));
        Host host = host(fixture);
        try {
            assertTrue(host.strip.open());
            assertEquals(PlayerPartsStrip.EPISODES, host.strip.mode());
            host.strip.vertical(false, host.strip.tabView(PlayerPartsStrip.EPISODES));
            View playing = host.strip.cardViews().get(1);
            assertTrue(playing.hasFocus());
            assertTrue(PlayerPartsStripTestText.of(playing).contains("Episode 10"));
            assertTrue(PlayerPartsStripTestText.of(playing).contains("Playing · 43 min"));
            assertTrue(PlayerPartsStripTestText.of(host.strip.cardViews().get(0)).contains("Watched · 43 min"));
            host.strip.vertical(true, playing);
            assertTrue(host.strip.tabView(PlayerPartsStrip.EPISODES).hasFocus());
            host.strip.vertical(true, host.strip.tabView(PlayerPartsStrip.EPISODES));
            assertTrue(host.bar.hasFocus());
            assertFalse(host.strip.expanded());
            assertTrue(host.strip.open());
            host.strip.vertical(false, host.strip.tabView(PlayerPartsStrip.EPISODES));
            host.strip.cardViews().get(2).performClick();
            assertEquals(11, fixture.played.id);
        } finally {
            host.controller.pause().stop().destroy();
        }
    }

    @Test
    public void backClosesTheRowAndReturnsToTheBar()
    {
        Fixture fixture = new Fixture();
        fixture.parts = parts();
        Host host = host(fixture);
        try {
            assertFalse(host.strip.back());
            assertTrue(host.strip.open());
            assertTrue(host.strip.back());
            assertFalse(host.strip.expanded());
            assertTrue(host.bar.hasFocus());
            assertEquals(-1, host.lit[0]);
        } finally {
            host.controller.pause().stop().destroy();
        }
    }

    @Test
    public void timesReadLikeTheMockUps()
    {
        assertEquals("0:52", PlayerPartsStrip.time(52000));
        assertEquals("11:40", PlayerPartsStrip.time(700000));
        assertEquals("1:02:05", PlayerPartsStrip.time(3725000));
    }
}
