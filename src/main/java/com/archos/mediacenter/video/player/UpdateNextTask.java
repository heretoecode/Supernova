// Copyright 2017 Archos SA
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video.player;

import com.archos.mediacenter.video.diagnostics.DiagnosticRedactor;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;

import androidx.loader.content.CursorLoader;

import com.archos.environment.ArchosUtils;
import com.archos.filecorelibrary.FileComparator;
import com.archos.filecorelibrary.FileUtils;
import com.archos.filecorelibrary.ListingEngine;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.filecoreextension.UriUtils;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.NextEpisodeLoader;
import com.archos.mediaprovider.video.ListTables;
import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediascraper.BaseTags;
import com.archos.mediascraper.EpisodeTags;
import com.archos.mediascraper.MovieTags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.io.File;

/**
 * Created by alexandre on 24/04/15.
 */
public class UpdateNextTask
{

    private static final Logger log = LoggerFactory.getLogger(UpdateNextTask.class);

    private static String privateLocation(Object value)
    {
        return DiagnosticRedactor.clean(String.valueOf(value));
    }


    private final ArrayList<String> mRemoteUrlsList;
    private final int mRemotePosition;
    private final ContentResolver mResolver;
    private final long mPlaylistId;
    private final Video mVideo;
    private Uri mUri;
    private volatile Listener mListener;
    private final NextListingProcess listing = new NextListingProcess();
    private boolean executed;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "NextLookup");
        thread.setDaemon(true);
        return thread;
    });
    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean isCancelled = false;
    private Future<Result> mFuture;
    private final java.util.concurrent.CountDownLatch stopped = new java.util.concurrent.CountDownLatch(1);
    protected static class Result
    {
        public final Uri uri;
        public final long id;
        public Result(Uri uri, long id)
        {
            this.uri = uri;
            this.id = id;
        }
    }

    public static interface Listener {
        void onResult(Uri uri, long id);
    }

    public UpdateNextTask(ContentResolver resolver, Video video, Uri uri, ArrayList<String> remoteUrlsList,
                          int remotePosition, long playlistId)
    {
        if (log.isDebugEnabled())
            log.debug("UpdateNextTask: what is the next video after {}", privateLocation(uri));
        mResolver = resolver;
        mUri = uri;
        mRemoteUrlsList = remoteUrlsList;
        mRemotePosition = remotePosition;
        mListener = null;
        mPlaylistId = playlistId;
        mVideo = video;
    }

    public void setListener(Listener listener)
    {
        if (log.isDebugEnabled())
            log.debug("setListener");
        mListener = listener;
    }

    // SELECT _id,_data
    private final static String[] QUERY_COLUMNS_ID_DATA = {BaseColumns._ID, VideoStore.MediaColumns.DATA};
    // FROM video
    private final static Uri QUERY_VIDEO_TABLE = VideoStore.Video.Media.EXTERNAL_CONTENT_URI;
    // WHERE bucket_id=? AND _data > ?
    private final static String QUERY_WHERE_NEXT_VIDEO_IN_FOLDER = VideoStore.Video.VideoColumns.BUCKET_ID + "=? AND " +
                                                                   VideoStore.MediaColumns.DATA + ">? AND " +
                                                                   LoaderUtils.HIDE_USER_HIDDEN_FILTER;
    // WHERE bucket_id=?
    private final static String QUERY_WHERE_FIRST_VIDEO_IN_FOLDER =
        VideoStore.Video.VideoColumns.BUCKET_ID + "=? AND " + LoaderUtils.HIDE_USER_HIDDEN_FILTER;
    // ORDER BY data LIMIT 1: query() has no separate limit parameter.
    private final static String QUERY_ORDER_BY_DATA_LIMIT1 = VideoStore.MediaColumns.DATA + " LIMIT 1";

    static Cursor getNextInBucket(ContentResolver cr, int bucketId, String fullPathOfVideo)
    {
        if (log.isDebugEnabled())
            log.debug("getNextInBucket: bucketId {} for video {}", bucketId, privateLocation(fullPathOfVideo));
        return cr.query(QUERY_VIDEO_TABLE, QUERY_COLUMNS_ID_DATA, QUERY_WHERE_NEXT_VIDEO_IN_FOLDER,
                        new String[] {String.valueOf(bucketId), fullPathOfVideo}, QUERY_ORDER_BY_DATA_LIMIT1);
    }
    static Cursor getFirstInBucket(ContentResolver cr, int bucketId)
    {
        if (log.isDebugEnabled())
            log.debug("getFirstInBucket: {}", bucketId);
        return cr.query(QUERY_VIDEO_TABLE, QUERY_COLUMNS_ID_DATA, QUERY_WHERE_FIRST_VIDEO_IN_FOLDER,
                        new String[] {String.valueOf(bucketId)}, QUERY_ORDER_BY_DATA_LIMIT1);
    }
    private Result findEpisode(int episode, int season, long show)
    {
        if (log.isDebugEnabled())
            log.debug("findEpisode: look for episode {}", episode);
        String[] projection2 = new String[] {VideoStore.MediaColumns.DATA, VideoStore.Files.FileColumns._ID,
                                             VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE};
        String[] args2;
        if (episode >= 0)
            args2 = new String[] {String.valueOf(episode), String.valueOf(show), String.valueOf(season)};
        else
            args2 = new String[] {String.valueOf(show), String.valueOf(season)};
        String selection2 = (episode >= 0 ? VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE + ">=? AND " : "") +
                            VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID + " = ? AND " +
                            VideoStore.Video.VideoColumns.SCRAPER_E_SEASON + " = ?";
        selection2 += " AND " + LoaderUtils.HIDE_USER_HIDDEN_FILTER;
        Cursor cursor2 = mResolver.query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, projection2, selection2, args2,
                                         VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE);
        if (cursor2 != null)
        {
            if (cursor2.getCount() > 0)
            {
                cursor2.moveToFirst();
                int id = cursor2.getColumnIndex(VideoStore.Files.FileColumns._ID);
                int uri = cursor2.getColumnIndex(VideoStore.MediaColumns.DATA);
                Uri uri2 = Uri.parse(cursor2.getString(uri));
                long id2 = cursor2.getLong(id);
                int episodeColumn = cursor2.getColumnIndex(VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE);
                if (log.isDebugEnabled())
                    log.debug("findEpisode: found new episode {} {}",
                              (episodeColumn >= 0 ? cursor2.getInt(episodeColumn) : -1),
                              privateLocation(cursor2.getString(uri)));
                cursor2.close();
                return new Result(uri2, id2);
            }
            cursor2.close();
        }
        return null;
    }

    private static Uri canonical(Uri uri)
    {
        if (uri.getScheme() == null || "file".equalsIgnoreCase(uri.getScheme()))
        {
            return Uri.fromFile(new File(uri.getPath()));
        }
        return uri.normalizeScheme();
    }

    private Set<Uri> hiddenFiles()
    {
        Set<Uri> hidden = new HashSet<>();
        try (Cursor rows = mResolver.query(QUERY_VIDEO_TABLE, new String[] {VideoStore.MediaColumns.DATA},
                                           VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER + "=1", null, null))
        {
            if (rows != null)
            {
                while (rows.moveToNext())
                {
                listing.checkpoint();
                    if (!rows.isNull(0))
                    {
                        hidden.add(canonical(Uri.parse(rows.getString(0))));
                    }
                }
            }
        }
        return hidden;
    }

    public UpdateNextTask.Result run(boolean repeatFolder, boolean binge)
    {
        listing.enter();
        try
        {
            return runOwned(repeatFolder, binge);
        }
        finally
        {
            listing.close();
        }
    }

    private Result runOwned(boolean repeatFolder, boolean binge)
    {
        if (log.isDebugEnabled())
            log.debug("UpdateNextTask.Result: repeatFolder {}, binge {}", repeatFolder, binge);
        // reset to nothing
        Uri nextUri = null;
        long nextId = -1;

        Set<Uri> hidden = hiddenFiles();
        if (mRemoteUrlsList != null)
        {
            if (log.isDebugEnabled())
                log.debug("UpdateNextTask.Result: mRemoteUrlsList not null");
            int total = mRemoteUrlsList.size();
            for (int offset = 1; offset <= total; offset++)
            {
                listing.checkpoint();
                int next = mRemotePosition + offset;
                if (!repeatFolder && next >= total)
                {
                    break;
                }
                Uri candidate = Uri.parse(mRemoteUrlsList.get(next % total));
                if (!hidden.contains(canonical(candidate)))
                {
                    return new Result(candidate, -1);
                }
            }
            return null;
        }
        else
        {
            if (log.isDebugEnabled())
                log.debug("UpdateNextTask.Result: mRemoteUrlsList null, mPlaylistId {}, mVideo {}", mPlaylistId,
                          ((mVideo == null) ? "null" : privateLocation(mVideo.getFilePath())));
            if (mPlaylistId != -1 && mVideo != null)
            { //next in playlist
                BaseTags tags = mVideo.getFullScraperTags(ArchosUtils.getGlobalContext());
                long currentEpisodeId = tags instanceof EpisodeTags ? tags.getOnlineId() : -1;
                long currentMovieId = tags instanceof MovieTags ? tags.getOnlineId() : -1;

                // binge watch mode to find next episode
                if (binge && currentEpisodeId != -1)
                {
                    CursorLoader loader = new NextEpisodeLoader(ArchosUtils.getGlobalContext(), (EpisodeTags)tags);
                    Cursor c = loader.loadInBackground();
                    if (c.getCount() > 0)
                    {
                        c.moveToFirst();
                        int dataColumn = c.getColumnIndex(VideoStore.MediaColumns.DATA);
                        if (dataColumn >= 0)
                            nextUri = Uri.parse(c.getString(dataColumn));
                    }
                    c.close();
                    if (log.isDebugEnabled())
                        log.debug("UpdateNextTask.Result binge mode {}", privateLocation(nextUri));
                    if (nextUri != null)
                        return new Result(nextUri, nextId);
                    else
                        return null;
                }

                boolean useNextVideo = false;
                String selection =
                    "Select \n"
                    + "   v._id as _id,\n"
                    + "   v." + VideoStore.MediaColumns.DATA + " as " + VideoStore.MediaColumns.DATA + ", \n"
                    + "   vl." + VideoStore.VideoList.Columns.M_ONLINE_ID + " as " +
                    VideoStore.VideoList.Columns.M_ONLINE_ID + ", \n"
                    + "   vl." + VideoStore.VideoList.Columns.E_ONLINE_ID + " as " +
                    VideoStore.VideoList.Columns.E_ONLINE_ID + "   from \n"
                    + "   video v,\n"
                    + "   " + ListTables.VIDEO_LIST_TABLE + " vl \n"
                    + "WHERE\n"
                    + "        ("
                    + "("
                    + "v." + VideoStore.Video.VideoColumns.SCRAPER_M_ONLINE_ID + " = vl." +
                    VideoStore.VideoList.Columns.M_ONLINE_ID + " "
                    + " AND v." + VideoStore.Video.VideoColumns.SCRAPER_M_ONLINE_ID + " NOT NULL"
                    + " OR v." + VideoStore.Video.VideoColumns.SCRAPER_E_ONLINE_ID + " = vl." +
                    VideoStore.VideoList.Columns.E_ONLINE_ID + " "
                    + " AND v." + VideoStore.Video.VideoColumns.SCRAPER_E_ONLINE_ID + " NOT NULL) "
                    + " AND " + LoaderUtils.HIDE_USER_HIDDEN_FILTER + " AND vl." +
                    VideoStore.List.Columns.SYNC_STATUS + " != " + VideoStore.List.SyncStatus.STATUS_DELETED +
                    " AND vl." + VideoStore.VideoList.Columns.LIST_ID + " = ?)";
                try (Cursor cursor =
                         mResolver.query(VideoStore.RAW_QUERY, null, selection, new String[] {mPlaylistId + ""}, null))
                {
                    if (cursor != null && cursor.getCount() > 0)
                    {
                        if (log.isDebugEnabled())
                            log.debug("UpdateNextTask.Result: cursor not null");
                        int movieIdColumn = cursor.getColumnIndex(VideoStore.VideoList.Columns.M_ONLINE_ID);
                        int episodeIdColumn = cursor.getColumnIndex(VideoStore.VideoList.Columns.E_ONLINE_ID);
                        while (cursor.moveToNext())
                        {
                listing.checkpoint();
                            long episodeId = cursor.getLong(episodeIdColumn);
                            long movieId = cursor.getLong(movieIdColumn);
                            if (log.isDebugEnabled())
                                log.debug("UpdateNextTask.Result: episodeId {}, movieId {}", episodeId, movieId);
                            if (currentEpisodeId != -1 && currentEpisodeId == episodeId)
                            {
                                useNextVideo = true;
                            }
                            else if (currentMovieId != -1 && currentMovieId == movieId)
                            {
                                useNextVideo = true;
                            }
                            else if (useNextVideo)
                            { //previous was our video, this one is different, use Uri
                                int dataColumn = cursor.getColumnIndex(VideoStore.MediaColumns.DATA);
                                if (dataColumn >= 0)
                                {
                                    nextUri = Uri.parse(cursor.getString(dataColumn));
                                    return new Result(nextUri, nextId);
                                }
                            }
                        }
                    }
                    else
                    {
                        if (log.isDebugEnabled())
                            log.debug("UpdateNextTask.Result: cursor null");
                    }
                }
                if (log.isDebugEnabled())
                    log.debug("UpdateNextTask.Result: found nothing");
                return null;
            }
        }
        if (!UriUtils.isImplementedByFileCore(mUri))
        { // when we can't list folder
            if (log.isDebugEnabled())
                log.debug("UpdateNextTask.Result: scheme for {} not supported -> cannot list folder thus no next...",
                          privateLocation(mUri));
            return null;
        }
        /*
            If we don't have a list of files set by arguments, we need to create one
         */
        if (mUri != null)
        {
            if (log.isDebugEnabled())
                log.debug("UpdateNextTask.Result: trying to find next for mUri {}", privateLocation(mUri));

            // Only use episode logic for binge mode, not for folder modes
            if (binge)
            {
                String[] projection1 = new String[] {VideoStore.MediaColumns.DATA, VideoStore.Files.FileColumns._ID,
                                                     VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE,
                                                     VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID,
                                                     VideoStore.Video.VideoColumns.SCRAPER_E_SEASON};
                String[] args1 = new String[] {mUri.toString()};
                String selection1 = VideoStore.MediaColumns.DATA + "=?";
                //retrieve episode number
                Cursor cursor1 = mResolver.query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, projection1, selection1,
                                                 args1, VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE);
                if (cursor1 != null && cursor1.getCount() > 0)
                {
                    if (log.isDebugEnabled())
                        log.debug("UpdateNextTask.Result: found next episodes");

                    int episodeColumn = cursor1.getColumnIndex(VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE);
                    int seasonColumn = cursor1.getColumnIndex(VideoStore.Video.VideoColumns.SCRAPER_E_SEASON);
                    int showColumn = cursor1.getColumnIndex(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);

                    cursor1.moveToFirst();
                    int episode = cursor1.getInt(episodeColumn);
                    int season = cursor1.getInt(seasonColumn);
                    long show = cursor1.getLong(showColumn);
                    if (cursor1 != null)
                        cursor1.close();
                    if (show > 0 && season >= 0 && episode >= 0)
                    {
                        if (log.isDebugEnabled())
                            log.debug("current episode : {} {}", episode, privateLocation(mUri));
                        Result result = findEpisode(episode + 1, season, show);
                        if (result != null)
                            return result;
                        else
                        {
                            result = findEpisode(-1, season + 1, show);
                            if (result != null)
                                return result;
                        }
                    }
                }
                if (cursor1 != null)
                    cursor1.close();
            }
            if (!binge)
            {
                int bucketId = FileUtils.getBucketId(mUri);
                if (log.isDebugEnabled())
                    log.debug("UpdateNextTask.Result: trying to find for bucketId {}", bucketId);
                Cursor cursor;
                // 1. Try to find the next video in the database
                cursor = getNextInBucket(mResolver, bucketId, mUri.toString());
                if (cursor != null)
                {
                    try
                    {
                        if (cursor.moveToFirst())
                        {
                            int dataColumn = cursor.getColumnIndex(VideoStore.Files.FileColumns.DATA);
                            int idColumn = cursor.getColumnIndex(VideoStore.Files.FileColumns._ID);
                            if (dataColumn >= 0)
                                nextUri = Uri.parse(cursor.getString(dataColumn));
                            if (idColumn >= 0)
                                nextId = cursor.getLong(idColumn);
                            if (log.isDebugEnabled())
                                log.debug("updateNextVideo({}) - next via getNextInBucket(DB):{}", repeatFolder,
                                          privateLocation(nextUri));
                            return new Result(nextUri, nextId);
                        }
                        else
                        {
                            if (log.isDebugEnabled())
                                log.debug("updateNextVideo({}) - getNextInBucket empty cursor, will try filesystem",
                                          repeatFolder);
                        }
                    }
                    finally
                    {
                        cursor.close();
                    }
                }
                else
                {
                    if (log.isDebugEnabled())
                        log.debug("updateNextVideo({}) - getNextInBucket null cursor, will try filesystem",
                                  repeatFolder);
                }

                if (repeatFolder)
                {
                    // 2. Try to find the first video in that folder in the database
                    cursor = getFirstInBucket(mResolver, bucketId);
                    if (cursor != null)
                    {
                        try
                        {
                            if (cursor.moveToFirst())
                            {
                                int dataColumn = cursor.getColumnIndex(VideoStore.Files.FileColumns.DATA);
                                int idColumn = cursor.getColumnIndex(VideoStore.Files.FileColumns._ID);
                                if (dataColumn >= 0)
                                    nextUri = Uri.parse(cursor.getString(dataColumn));
                                if (idColumn >= 0)
                                    nextId = cursor.getLong(idColumn);
                                if (log.isDebugEnabled())
                                    log.debug("updateNextVideo({}) - next via getFirstInBucket(DB):{}", repeatFolder,
                                              privateLocation(nextUri));
                                return new Result(nextUri, nextId);
                            }
                            else if (log.isDebugEnabled())
                                log.debug("updateNextVideo({}) - getNextInBucket empty cursor!?", repeatFolder);
                        }
                        finally
                        {
                            cursor.close();
                        }
                    }
                    else if (log.isDebugEnabled())
                        log.debug("updateNextVideo({}) - getFirstInBucket null cursor!?", repeatFolder);
                }
                // 3. try to find the next file within the filesystem
                if (mUri.getScheme() == null)
                    mUri = Uri.parse("file://" + mUri.toString());
                Uri parentUri = FileUtils.getParentUrl(mUri);
                if (parentUri != null)
                {
                    try
                    {
                        List<MetaFile2> files = listing.list(ArchosUtils.getGlobalContext(), parentUri);
                        if (files != null && files.size() > 0)
                        {
                            List<MetaFile2> filteredList = new ArrayList<MetaFile2>();
                            //filter folder
                            for (int i = 0; i < files.size(); i++)
                            {
                listing.checkpoint();
                                if (files.get(i).isFile())
                                {
                                    String mimeType = files.get(i).getMimeType();
                                    if (mimeType != null && mimeType.startsWith("video/") &&
                                        (canonical(mUri).equals(canonical(files.get(i).getUri())) ||
                                         !hidden.contains(canonical(files.get(i).getUri()))))
                                        filteredList.add(files.get(i));
                                }
                            }
                            final Comparator<? super MetaFile2> comparator =
                                new FileComparator().selectFileComparator(ListingEngine.SortOrder.SORT_BY_NAME_ASC);
                            Collections.sort(filteredList, (left, right) -> {
                                listing.checkpoint();
                                return comparator.compare(left, right);
                            });
                            int total = filteredList.size();
                            int current = -1;
                            // try to find the current video in the list
                            for (int i = 0; i < filteredList.size(); i++)
                            {
                listing.checkpoint();
                                if (mUri.equals(filteredList.get(i).getUri()))
                                {
                                    current = i;
                                    break;
                                }
                            }
                            // if it is found
                            if (current != -1)
                            {
                                int next = current + 1;
                                // when repeat folder, just modulo
                                if (repeatFolder)
                                {
                                    nextUri = filteredList.get(next % total).getUri();
                                    if (hidden.contains(canonical(nextUri)))
                                    {
                                        return null;
                                    }
                                    if (log.isDebugEnabled())
                                        log.debug("updateNextVideo({}) - next via filesystem:{}", repeatFolder,
                                                  privateLocation(nextUri));
                                    return new Result(nextUri, nextId);
                                }
                                else if (next < total)
                                {
                                    // in no repeat more, check that next still in list
                                    nextUri = filteredList.get(next).getUri();
                                    if (log.isDebugEnabled())
                                        log.debug("updateNextVideo({}) - next via filesystem:{}", repeatFolder,
                                                  privateLocation(nextUri));
                                    return new Result(nextUri, nextId);
                                }
                                else if (log.isDebugEnabled())
                                    log.debug("updateNextVideo({}) - no next in filesystem, it's the last:{}",
                                              repeatFolder, privateLocation(mUri));
                            }
                            else if (log.isDebugEnabled())
                                log.debug("updateNextVideo({}) - could not find video in list:{}",
                                          repeatFolder, privateLocation(mUri));
                        }
                        else if (log.isDebugEnabled())
                            log.debug("updateNextVideo({}) - could not list files / empty dir:{}",
                                      repeatFolder, privateLocation(mUri));
                    }
                    catch (IOException e)
                    {
                        log.error("UpdateNextTask.Result: caught IOException ", DiagnosticRedactor.trace(e));
                    }
                }
                else if (log.isDebugEnabled())
                    log.debug("updateNextVideo({}) - no parent file:{}", repeatFolder, privateLocation(mUri));
            }
        }
        else if (log.isDebugEnabled())
            log.debug("updateNextVideo({}) - not a local file:{}", repeatFolder, privateLocation(mUri));

        if (log.isDebugEnabled())
            log.debug("updateNextVideo({}) - No next found", repeatFolder);
        return null;
    }

    public synchronized void execute(boolean repeatFolder, boolean binge)
    {
        if (isCancelled || executed)
            return;
        executed = true;
        java.util.concurrent.FutureTask<Result> submitted = new java.util.concurrent.FutureTask<>(() -> {
            try
            {
                Result result = run(repeatFolder, binge);
                if (!isCancelled && !listing.cancelled())
                    handler.post(() -> {
                        if (isCancelled || listing.cancelled())
                            return;
                        Listener listener = mListener;
                        if (listener != null)
                            listener.onResult(result == null ? null : result.uri, result == null ? -1 : result.id);
                    });
                return result;
            }
            catch (CancellationException obsolete)
            {
                return null;
            }
        });
        mFuture = submitted;
        executor.execute(() -> {
            try
            {
                submitted.run();
            }
            finally
            {
                listing.close();
                stopped.countDown();
                executor.shutdown();
            }
        });
    }

    boolean awaitStopped(long timeoutMs) throws InterruptedException
    {
        return stopped.await(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public synchronized void cancel(boolean mayInterruptIfRunning)
    {
        isCancelled = true;
        listing.cancel();
        if (mFuture != null)
            mFuture.cancel(mayInterruptIfRunning);
        executor.shutdown();
        if (!executed)
            stopped.countDown();
    }

    public Result get() throws ExecutionException, InterruptedException
    {
        try
        {
            return mFuture != null ? mFuture.get() : null;
        }
        catch (CancellationException e)
        {
            return null;
        }
    }
}
