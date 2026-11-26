package com.archos.mediacenter.video.leanback.files;

import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Video;

import java.util.List;
import java.util.function.Predicate;

/** Counts for Library Care's "Library health" rows, from the library snapshot. Main-thread safe (no I/O). */
public final class LibraryCareCounts
{
    public final int missing;
    public final int notMatched;
    /** M5: new titles still being looked up ("Matching N new titles"). */
    public int pending;

    private LibraryCareCounts(int missing, int notMatched)
    {
        this.missing = missing;
        this.notMatched = notMatched;
    }

    /**
     * @param media     every library item
     * @param importing while an import runs, unmatched videos are not counted yet (they may still match)
     * @param missingFile whether a video's file can no longer be found
     */
    public static LibraryCareCounts count(List<Base> media, boolean importing, Predicate<Video> missingFile)
    {
        return count(media, importing, missingFile, java.util.Collections.emptySet());
    }

    /**
     * M5: a video whose look-up hasn't ended ({@code pending}) is not counted as Not matched; it is counted in
     * {@link #pending} for the "Matching N new titles" line.
     */
    public static LibraryCareCounts count(List<Base> media, boolean importing, Predicate<Video> missingFile,
                                          java.util.Set<Long> pending)
    {
        int waiting = 0;
        int missing = 0;
        int notMatched = 0;
        for (Base item : media)
        {
            if (!(item instanceof Video))
            {
                continue;
            }
            Video video = (Video) item;
            if (missingFile.test(video))
            {
                missing++;
            }
            boolean matched = video instanceof Movie || video instanceof Episode;
            if (!matched && pending.contains(video.getId()))
            {
                waiting++;
                continue;
            }
            if (!importing && !matched)
            {
                notMatched++;
            }
        }
        LibraryCareCounts counts = new LibraryCareCounts(missing, notMatched);
        counts.pending = waiting;
        return counts;
    }

    public int toCheck()
    {
        return missing + notMatched;
    }

    /** "3 files", "1 video", "None". */
    public static String pill(int count, String one, String many)
    {
        if (count <= 0)
        {
            return "None";
        }
        return count + " " + (count == 1 ? one : many);
    }
}
