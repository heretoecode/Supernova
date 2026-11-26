package com.archos.mediacenter.video.leanback;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Season;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.DbUtils;
import com.archos.mediaprovider.video.VideoStore;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * What a library poster's long-press menu does (page sheet LIBRARY.md): mark as watched, remove from the library and
 * find the files to delete, for a film or a whole show. Works on the library database only; the page shows the
 * Undo notice or the delete warning.
 */
public final class LibraryActions
{
    private static final String SHOW_VIDEOS =
        "_id IN (SELECT video_id FROM episode e JOIN show s ON e.show_episode=s._id WHERE s._id=?)";

    private LibraryActions()
    {
    }

    /** Marks a film, or every episode of a show season by season (so Trakt hears of it), as watched or not. */
    public static void markWatched(Context context, Object media, boolean watched)
    {
        if (media instanceof Video)
        {
            if (watched)
            {
                DbUtils.markAsRead(context, (Video) media);
            }
            else
            {
                DbUtils.markAsNotRead(context, (Video) media);
            }
            return;
        }
        if (!(media instanceof Tvshow))
        {
            return;
        }
        Tvshow show = (Tvshow) media;
        for (int number : seasons(context, show.getTvshowId()))
        {
            Season season = new Season(show.getTvshowId(), show.getName(), show.getPosterUri(), number, 0, 0);
            if (watched)
            {
                DbUtils.markAsRead(context, season);
            }
            else
            {
                DbUtils.markAsNotRead(context, season);
            }
        }
    }

    /** Hides a film or a whole show from the library (the file stays); {@code false} brings it back (Undo). */
    public static void removeFromLibrary(Context context, Object media, boolean removed)
    {
        if (media instanceof Video)
        {
            if (removed)
            {
                DbUtils.markAsHiddenByUser(context, (Video) media);
            }
            else
            {
                DbUtils.markAsNotHiddenByUser(context, (Video) media);
            }
            return;
        }
        if (media instanceof Tvshow)
        {
            ContentValues values = new ContentValues();
            values.put(VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER, removed ? 1 : 0);
            context.getContentResolver().update(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, values, SHOW_VIDEOS,
                                                new String[] {Long.toString(((Tvshow) media).getTvshowId())});
        }
    }

    /** The files a delete would remove: the film's file, or every episode file of the show. */
    public static List<Uri> files(Context context, Object media)
    {
        List<Uri> result = new ArrayList<>();
        if (media instanceof Movie && ((Movie) media).getFileUri() != null)
        {
            result.add(((Movie) media).getFileUri());
            return result;
        }
        if (!(media instanceof Tvshow))
        {
            return result;
        }
        String[] projection = {VideoStore.MediaColumns.DATA};
        try (Cursor cursor = context.getContentResolver().query(
                 VideoStore.Video.Media.EXTERNAL_CONTENT_URI, projection, SHOW_VIDEOS,
                 new String[] {Long.toString(((Tvshow) media).getTvshowId())}, null))
        {
            while (cursor != null && cursor.moveToNext())
            {
                String path = cursor.getString(0);
                if (path != null && !path.isEmpty())
                {
                    result.add(path.contains("://") ? Uri.parse(path) : Uri.fromFile(new java.io.File(path)));
                }
            }
        }
        catch (RuntimeException unavailable)
        {
            return result;
        }
        return result;
    }

    private static TreeSet<Integer> seasons(Context context, long show)
    {
        TreeSet<Integer> numbers = new TreeSet<>();
        String[] projection = {VideoStore.Video.VideoColumns.SCRAPER_E_SEASON};
        try (Cursor cursor = context.getContentResolver().query(
                 VideoStore.Video.Media.EXTERNAL_CONTENT_URI, projection, SHOW_VIDEOS,
                 new String[] {Long.toString(show)}, null))
        {
            while (cursor != null && cursor.moveToNext())
            {
                numbers.add(cursor.getInt(0));
            }
        }
        catch (RuntimeException unavailable)
        {
            return numbers;
        }
        return numbers;
    }

    /**
     * Delete file(s) from a long-press menu: the delete warning, "Keep file" focused first (RULES §7b); then the files
     * go and {@code changed} runs so the page refreshes.
     */
    public static void confirmDelete(Context context, Object media, String name, Runnable changed)
    {
        boolean show = media instanceof Tvshow;
        PreviewDialog.warning(
            context, show ? "Delete every episode of " + name + "?" : "Delete " + name + "?",
            show ? "Every episode file is deleted from its drive or server. This can't be undone."
                 : "The file is deleted from its drive or server. This can't be undone.",
            show ? "Keep files" : "Keep file", show ? "Delete all episode files" : "Delete file",
            () -> delete(context, files(context, media), changed));
    }

    private static void delete(Context context, List<Uri> files, Runnable changed)
    {
        if (files.isEmpty() || !(context instanceof android.app.Activity))
        {
            return;
        }
        com.archos.mediacenter.video.browser.Delete delete = new com.archos.mediacenter.video.browser.Delete(
            new com.archos.mediacenter.video.browser.Delete.DeleteListener()
            {
                public void onVideoFileRemoved(Uri videoFile, boolean askForFolderRemoval, Uri folderToDelete)
                {
                    changed.run();
                }

                public void onDeleteVideoFailed(Uri videoFile)
                {
                    PreviewNotice.show(context, com.archos.mediacenter.video.browser.Delete.failureMessage(videoFile),
                                       true);
                }

                public void onFolderRemoved(Uri folder)
                {
                }

                public void onDeleteSuccess()
                {
                    changed.run();
                }
            }, context);
        if (files.size() == 1)
        {
            delete.startDeleteProcess(files.get(0));
        }
        else
        {
            delete.startMultipleDeleteProcess(files);
        }
    }
}
