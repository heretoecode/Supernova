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
     * Delete file(s) from a long-press menu or Details: the delete warning (LIBRARY.md, RULES §7b) with the film's
     * versions or the show's episodes, Clean Up, "Keep" focused first; then the chosen files go and {@code changed}
     * runs so the page refreshes.
     */
    public static void confirmDelete(Context context, Object media, String name, Runnable changed)
    {
        boolean show = media instanceof Tvshow;
        List<DeleteWarning.File> files = warningFiles(context, media);
        if (files.isEmpty())
        {
            for (Uri uri : files(context, media))
            {
                files.add(new DeleteWarning.File(uri, uri.getLastPathSegment(), "",
                                                 com.archos.mediacenter.video.browser.Delete.serverName(uri), 0));
            }
        }
        if (files.isEmpty())
        {
            return;
        }
        boolean trakt = com.archos.mediacenter.utils.trakt.Trakt.isTraktV2Enabled(
            context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context));
        DeleteWarning.show(context, name, show, files, trakt, (chosen, history, fromTrakt, emptyFolder) -> {
            // Clean Up: clearing the viewing history marks the title unwatched; when Trakt is connected that change
            // also leaves Trakt's history (the app's usual sync), so the Trakt switch does the same here.
            if (history || fromTrakt)
            {
                markWatched(context, media, false);
            }
            delete(context, chosen, changed, emptyFolder);
        });
    }

    /** The files the warning lists: every version of a film (same TMDB title), or every episode file of a show. */
    static List<DeleteWarning.File> warningFiles(Context context, Object media)
    {
        List<DeleteWarning.File> result = new ArrayList<>();
        String selection;
        String[] args;
        if (media instanceof Movie)
        {
            long online = ((Movie) media).getOnlineId();
            if (online <= 0)
            {
                result.add(file(context, (Movie) media));
                return result;
            }
            selection = VideoStore.Video.VideoColumns.SCRAPER_M_ONLINE_ID + "=?";
            args = new String[] {Long.toString(online)};
        }
        else if (media instanceof Tvshow)
        {
            selection = VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID + "=?";
            args = new String[] {Long.toString(((Tvshow) media).getTvshowId())};
        }
        else if (media instanceof Video)
        {
            result.add(file(context, (Video) media));
            return result;
        }
        else
        {
            return result;
        }
        com.archos.mediacenter.video.browser.loader.EpisodesLoader loader =
            new com.archos.mediacenter.video.browser.loader.EpisodesLoader(context, 0, -1, false);
        String sort = media instanceof Tvshow ? VideoStore.Video.VideoColumns.SCRAPER_E_SEASON + ", "
                                                    + VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE
                                              : null;
        try (Cursor cursor = context.getContentResolver().query(loader.getUri(), loader.getProjection(), selection,
                                                                args, sort))
        {
            if (cursor == null)
            {
                return result;
            }
            com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper mapper =
                new com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper();
            mapper.bindColumns(cursor);
            while (cursor.moveToNext())
            {
                Object item = mapper.bind(cursor);
                if (item instanceof Video && ((Video) item).getFileUri() != null)
                {
                    result.add(file(context, (Video) item));
                }
            }
        }
        catch (RuntimeException unavailable)
        {
            return result;
        }
        return result;
    }

    private static DeleteWarning.File file(Context context, Video video)
    {
        Uri uri = video.getFileUri();
        DeleteWarning.File file = new DeleteWarning.File(
            uri, video.getFilenameNonCryptic(), PreviewVariants.fileBadge(context, video),
            com.archos.mediacenter.video.browser.Delete.serverName(uri), video.getSize());
        if (video instanceof com.archos.mediacenter.video.browser.adapters.object.Episode)
        {
            com.archos.mediacenter.video.browser.adapters.object.Episode e =
                (com.archos.mediacenter.video.browser.adapters.object.Episode) video;
            file.season = e.getSeasonNumber();
            String name = e.getEpisodeName() == null || e.getEpisodeName().isEmpty() ? ""
                                                                                    : " · " + e.getEpisodeName();
            file.episode = "S" + e.getSeasonNumber() + " E" + e.getEpisodeNumber() + name;
        }
        return file;
    }

    /** A folder on this device with nothing at all left in it. Remote folders are left alone. */
    static boolean emptyLocalFolder(Uri folder)
    {
        if (!"file".equals(folder.getScheme()) || folder.getPath() == null)
        {
            return false;
        }
        String[] left = new java.io.File(folder.getPath()).list();
        return left != null && left.length == 0;
    }

    private static void delete(Context context, List<Uri> files, Runnable changed, boolean emptyFolder)
    {
        if (files.isEmpty() || !(context instanceof android.app.Activity))
        {
            return;
        }
        com.archos.mediacenter.video.browser.Delete[] holder = {null};
        com.archos.mediacenter.video.browser.Delete delete = new com.archos.mediacenter.video.browser.Delete(
            new com.archos.mediacenter.video.browser.Delete.DeleteListener()
            {
                public void onVideoFileRemoved(Uri videoFile, boolean askForFolderRemoval, Uri folderToDelete)
                {
                    // Clean Up › Delete the folder if it's left empty: only when nothing at all is left in it.
                    if (emptyFolder && folderToDelete != null && emptyLocalFolder(folderToDelete))
                    {
                        holder[0].deleteFolder(folderToDelete);
                    }
                    if (com.archos.mediacenter.video.browser.Delete.wasAlreadyGone(videoFile))
                    {
                        // M2: the delete warning's wording (RULES §7b).
                        PreviewNotice.show(context, "This file was already gone from "
                                           + com.archos.mediacenter.video.browser.Delete.serverName(videoFile)
                                           + ", so it's been removed from your library.", false);
                    }
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
        holder[0] = delete;
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
