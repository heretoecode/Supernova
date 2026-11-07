package com.archos.mediacenter.video.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** One gapless, playback-relative timeline. Intro markers take precedence over ordinary chapters. */
public final class PlaybackParts
{
    public enum Kind
    {
        CHAPTER,
        EPISODE,
        FILM,
        RECAP,
        INTRO,
        PREVIEW,
        CREDITS
    }

    public static final class Part
    {
        public final String name;
        public final Kind kind;
        public final long startMs;
        public final long endMs;

        public Part(String name, Kind kind, long startMs, long endMs)
        {
            if (kind == null || startMs < 0 || endMs <= startMs)
            {
                throw new IllegalArgumentException("Invalid playback part");
            }
            this.name = name == null ? "" : name;
            this.kind = kind;
            this.startMs = startMs;
            this.endMs = endMs;
        }
    }

    private final List<Part> parts;

    private PlaybackParts(List<Part> parts)
    {
        this.parts = Collections.unmodifiableList(parts);
    }

    public List<Part> parts()
    {
        return parts;
    }

    /** Proposed display data: renumber only generic chapter labels after merging; retain authored names. */
    public List<Part> numberedChapters()
    {
        List<Part> result = new ArrayList<>();
        int chapter = 0;
        for (Part part : parts) {
            String name = part.name;
            if (part.kind == Kind.CHAPTER) {
                chapter++;
                if (name.isEmpty() || name.matches("(?i)chapter\\s*0*[0-9]+"))
                    name = "Chapter " + chapter;
            }
            result.add(new Part(name, part.kind, part.startMs, part.endMs));
        }
        return Collections.unmodifiableList(result);
    }

    public PlaybackParts numbered()
    {
        return new PlaybackParts(numberedChapters());
    }

    /** Half-open intervals; an end boundary belongs to the following part. */
    public Part partAt(long positionMs)
    {
        for (Part part : parts)
        {
            if (positionMs >= part.startMs && positionMs < part.endMs)
            {
                return part;
            }
        }
        return null;
    }

    public static PlaybackParts merge(List<Part> embedded, List<Part> chaptersDb, List<Part> introDb, long durationMs,
                                      boolean episode)
    {
        if (durationMs <= 0)
        {
            return new PlaybackParts(new ArrayList<>());
        }
        List<Part> markers = new ArrayList<>();
        for (Part part : introDb)
        {
            if (part.startMs < durationMs && part.kind != Kind.CHAPTER && part.kind != Kind.EPISODE &&
                part.kind != Kind.FILM)
            {
                markers.add(new Part(part.name, part.kind, part.startMs, Math.min(durationMs, part.endMs)));
            }
        }
        markers.sort(Comparator.comparingLong((Part part) -> part.startMs)
                         .thenComparingInt(part -> part.kind.ordinal())
                         .thenComparingLong(part -> part.endMs));
        TreeSet<Long> markerEdges = new TreeSet<>();
        for (Part marker : markers)
        {
            markerEdges.add(marker.startMs);
            markerEdges.add(marker.endMs);
        }
        TreeMap<Long, Part> chapters = new TreeMap<>();
        // At a duplicate chapter start the file's own name wins.
        List<Part> sources = new ArrayList<>(chaptersDb);
        sources.addAll(embedded);
        for (Part chapter : sources)
        {
            if (chapter.startMs >= durationMs)
            {
                continue;
            }
            long edge = snap(chapter.startMs, markerEdges);
            long end = snap(Math.min(durationMs, chapter.endMs), markerEdges);
            boolean replaced = false;
            for (Part marker : markers)
            {
                if (edge >= marker.startMs && end <= marker.endMs)
                {
                    replaced = true;
                    break;
                }
            }
            if (!replaced && edge < durationMs)
            {
                chapters.put(edge, chapter);
            }
        }
        TreeSet<Long> edges = new TreeSet<>(markerEdges);
        edges.add(0L);
        edges.add(durationMs);
        for (long edge : chapters.keySet())
        {
            if (markerAt(markers, edge) == null)
            {
                edges.add(edge);
            }
        }
        List<Long> ordered = new ArrayList<>(edges);
        List<Part> result = new ArrayList<>();
        for (int i = 0; i + 1 < ordered.size(); i++)
        {
            long start = ordered.get(i);
            long end = ordered.get(i + 1);
            Part marker = markerAt(markers, start);
            Map.Entry<Long, Part> chapter = chapters.floorEntry(start);
            Kind kind = marker != null    ? marker.kind
                        : chapter != null ? Kind.CHAPTER
                        : episode         ? Kind.EPISODE
                                          : Kind.FILM;
            String name = marker != null    ? marker.name
                          : chapter != null ? chapter.getValue().name
                          : episode         ? "Episode"
                                            : "Film";
            Part next = new Part(name, kind, start, end);
            if (!result.isEmpty() && marker != null)
            {
                Part previous = result.get(result.size() - 1);
                if (previous.kind == next.kind && previous.name.equals(next.name))
                {
                    result.set(result.size() - 1, new Part(next.name, next.kind, previous.startMs, end));
                    continue;
                }
            }
            result.add(next);
        }
        return new PlaybackParts(result);
    }

    private static long snap(long edge, TreeSet<Long> markers)
    {
        Long before = markers.floor(edge);
        Long after = markers.ceiling(edge);
        long closest = before == null ? edge : before;
        if (after != null && (before == null || after - edge < edge - before))
        {
            closest = after;
        }
        return Math.abs(edge - closest) <= 5000 ? closest : edge;
    }

    private static Part markerAt(List<Part> markers, long position)
    {
        for (Part marker : markers)
        {
            if (position >= marker.startMs && position < marker.endMs)
            {
                return marker;
            }
        }
        return null;
    }
}
