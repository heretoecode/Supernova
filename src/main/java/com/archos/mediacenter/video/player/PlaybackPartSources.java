package com.archos.mediacenter.video.player;

import com.archos.mediacenter.utils.introdb.IntroSegments;
import java.util.ArrayList;
import java.util.List;

/** Adapts validated file/ChaptersDB starts and normalized IntroDB ranges into the shared timeline. */
public final class PlaybackPartSources
{
    public static PlaybackParts merge(List<PlaybackChapters.Chapter> embedded,
                                      List<PlaybackChapters.Chapter> chaptersDb, IntroSegments introDb, long durationMs,
                                      boolean episode)
    {
        List<PlaybackParts.Part> markers = new ArrayList<>();
        if (introDb != null && durationMs > 0)
        {
            for (IntroSegments.Type type : IntroSegments.Type.values())
            {
                PlaybackParts.Kind kind = type == IntroSegments.Type.OUTRO ? PlaybackParts.Kind.CREDITS
                                                                           : PlaybackParts.Kind.valueOf(type.name());
                String name = kind.name().substring(0, 1) + kind.name().substring(1).toLowerCase(java.util.Locale.ROOT);
                for (IntroSegments.Segment segment : introDb.get(type))
                {
                    long start = Math.max(0, segment.startMs == null ? 0 : segment.startMs);
                    long end = Math.min(durationMs, segment.endMs == null ? durationMs : segment.endMs);
                    if (start < end)
                    {
                        markers.add(new PlaybackParts.Part(name, kind, start, end));
                    }
                }
            }
        }
        return PlaybackParts.merge(chapters(embedded, durationMs), chapters(chaptersDb, durationMs), markers,
                                   durationMs, episode);
    }

    private static List<PlaybackParts.Part> chapters(List<PlaybackChapters.Chapter> source, long duration)
    {
        List<PlaybackParts.Part> result = new ArrayList<>();
        if (source != null)
        {
            List<PlaybackChapters.Chapter> ordered = new ArrayList<>(source);
            ordered.sort(java.util.Comparator.comparingLong(chapter -> chapter.startMs));
            for (int i = 0; i < ordered.size(); i++)
            {
                PlaybackChapters.Chapter chapter = ordered.get(i);
                long end = i + 1 < ordered.size() ? Math.min(duration, ordered.get(i + 1).startMs) : duration;
                if (chapter.startMs >= 0 && chapter.startMs < end)
                {
                    result.add(new PlaybackParts.Part(chapter.title, PlaybackParts.Kind.CHAPTER, chapter.startMs, end));
                }
            }
        }
        return result;
    }

    private PlaybackPartSources()
    {
    }
}
