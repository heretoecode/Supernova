package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.os.Bundle;

import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediascraper.ScrapeDetailResult;
import com.archos.mediascraper.ScrapeSearchResult;
import com.archos.mediascraper.Scraper;
import com.archos.mediascraper.SearchResult;
import com.archos.mediascraper.preprocess.SearchInfo;
import com.archos.mediascraper.preprocess.SearchPreprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

/**
 * Check matches (owner, 12 Nov 2026; Files › Library Care): every film is looked up again from its file name and the
 * owner sees the changes first; nothing changes until Accept. Films first; TV shows to follow.
 */
public final class MatchCheck
{
    /** One suggested change: the film as matched now, and the match its file name finds today. */
    public static final class Change
    {
        public final Movie movie;
        public final String newTitle;
        public final int newId;
        final SearchResult result;

        Change(Movie movie, String newTitle, int newId, SearchResult result)
        {
            this.movie = movie;
            this.newTitle = newTitle;
            this.newId = newId;
            this.result = result;
        }
    }

    private MatchCheck()
    {
    }

    /** A change is suggested only when today's best match is a different title (unmatched films are not changes). */
    static boolean suggests(long currentId, int foundId)
    {
        return currentId > 0 && foundId > 0 && currentId != foundId;
    }

    /** Worker thread only. Reports progress as films are checked; stops early when cancelled. */
    public static List<Change> run(Context context, List<Base> media, IntConsumer checked, BooleanSupplier cancelled)
    {
        Scraper scraper = new Scraper(context.getApplicationContext());
        List<Change> changes = new ArrayList<>();
        int count = 0;
        for (Base item : media)
        {
            if (cancelled.getAsBoolean())
            {
                break;
            }
            if (!(item instanceof Movie))
            {
                continue;
            }
            Movie movie = (Movie) item;
            count++;
            checked.accept(count);
            if (movie.getFileUri() == null || movie.getOnlineId() <= 0)
            {
                continue;
            }
            try
            {
                SearchInfo info = SearchPreprocessor.instance().parseFileBased(movie.getFileUri(), movie.getFileUri());
                ScrapeSearchResult found = scraper.getBestMatches(info, 1);
                if (found == null || !found.isOkay() || found.results == null || found.results.isEmpty())
                {
                    continue;
                }
                SearchResult best = found.results.get(0);
                if (suggests(movie.getOnlineId(), best.getId()))
                {
                    changes.add(new Change(movie, best.getTitle(), best.getId(), best));
                }
            }
            catch (RuntimeException lookupFailed)
            {
                // One film that cannot be looked up does not stop the check.
            }
        }
        return changes;
    }

    /** Worker thread only: applies a change (the new match's details are fetched and saved for that film). */
    public static boolean accept(Context context, Change change)
    {
        try
        {
            ScrapeDetailResult details = Scraper.getDetails(change.result, new Bundle());
            if (details == null || !details.isOkay() || details.tag == null)
            {
                return false;
            }
            return details.tag.save(context.getApplicationContext(), change.movie.getId()) >= 0;
        }
        catch (RuntimeException saveFailed)
        {
            return false;
        }
    }
}
