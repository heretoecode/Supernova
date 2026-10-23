package com.archos.mediacenter.video.player;

import com.archos.medialib.MediaMetadata;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlaybackChaptersTest {
    private MediaMetadata data(long... starts){MediaMetadata data=mock(MediaMetadata.class);when(data.has(PlaybackChapters.COUNT)).thenReturn(true);when(data.getInt(PlaybackChapters.COUNT)).thenReturn(starts.length);for(int i=0;i<starts.length;i++){int key=PlaybackChapters.BASE+i*PlaybackChapters.STRIDE;when(data.has(key)).thenReturn(true);when(data.getLong(key)).thenReturn(starts[i]);when(data.has(key+1)).thenReturn(true);when(data.getString(key+1)).thenReturn("Chapter "+i);}return data;}
    @Test public void embeddedStartsRetainExactRelativeMillisecondsAndTitles(){List<PlaybackChapters.Chapter> chapters=PlaybackChapters.read(data(0,1234,5000),10000);assertEquals(3,chapters.size());assertEquals(1234,chapters.get(1).startMs);assertEquals("Chapter 1",PlaybackChapters.titleAt(chapters,4999));assertEquals("Chapter 2",PlaybackChapters.titleAt(chapters,5000));}
    @Test public void absentUnknownDurationMalformedAndUnorderedDataNeverCreateMarkers(){assertTrue(PlaybackChapters.read(mock(MediaMetadata.class),10000).isEmpty());assertTrue(PlaybackChapters.read(data(0,1000),0).isEmpty());for(long[] starts:new long[][]{{-1,1000},{0,0},{2000,1000},{0,10000},{Long.MAX_VALUE}})assertTrue(PlaybackChapters.read(data(starts),10000).isEmpty());MediaMetadata huge=data(0);when(huge.getInt(PlaybackChapters.COUNT)).thenReturn(257);assertTrue(PlaybackChapters.read(huge,10000).isEmpty());}
    @Test public void nonzeroFirstChapterDoesNotInventAnOpeningLabel(){List<PlaybackChapters.Chapter> chapters=PlaybackChapters.read(data(1000,2000),5000);assertEquals("",PlaybackChapters.titleAt(chapters,999));assertEquals("Chapter 0",PlaybackChapters.titleAt(chapters,1000));}
}
