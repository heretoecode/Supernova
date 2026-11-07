package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.OperationCanceledException;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.VideoLoader;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediaprovider.video.PersonalVideoActions;
import com.archos.mediaprovider.video.VideoStore.Video.VideoColumns;
import com.archos.mediascraper.BaseTags;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PlayerEpisodesBTest
{
    private SQLiteDatabase db;
    private ContentResolver resolver;
    private Context context;
    private PlayerService service;

    @Before public void prepare()
    {
        db = SQLiteDatabase.create(null);
        java.util.Set<String> names = new java.util.LinkedHashSet<>();
        for (String projection : VideoLoader.mProjection) {
            java.util.regex.Matcher identifier = java.util.regex.Pattern.compile("[A-Za-z_][A-Za-z_0-9]*")
                    .matcher(projection);
            while (identifier.find()) {
                String name = identifier.group();
                if (!name.equalsIgnoreCase("coalesce") && !name.equalsIgnoreCase("as"))
                    names.add(name);
            }
        }
        String columns = names.stream().map(name -> "\"" + name + "\" NUMERIC")
                .collect(Collectors.joining(","));
        db.execSQL("CREATE TABLE video (" + columns + "," + PersonalVideoActions.COLUMN + " INTEGER)");
        resolver = mock(ContentResolver.class);
        context = mock(Context.class);
        when(context.getContentResolver()).thenReturn(resolver);
        when(context.getApplicationContext()).thenReturn(RuntimeEnvironment.getApplication());
        when(context.getSharedPreferences(anyString(), anyInt()))
                .thenAnswer(call -> RuntimeEnvironment.getApplication()
                        .getSharedPreferences(call.getArgument(0), call.getArgument(1)));
        when(resolver.query(any(Uri.class), any(String[].class), anyString(), any(String[].class), isNull(),
                            any(CancellationSignal.class)))
                .thenAnswer(call -> db.query("video", call.getArgument(1), call.getArgument(2), call.getArgument(3),
                        null, null, null));
        add(1, 1, 123, false, false);
        add(2, 2, 123, false, false);
        add(3, 3, 123, true, false);
        add(4, 4, 123, false, true);
        add(5, 1, 456, false, false);
        add(6, 2, 456, false, false);
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
                .edit().putBoolean("supernova_onboarding_complete", true).commit();
        PreviewLibraryLoader.cached = null;
    }

    private void add(long id, int episode, long series, boolean personal, boolean hidden)
    {
        ContentValues values = new ContentValues();
        values.put(VideoColumns._ID, id);
        values.put(VideoColumns.DATA, "/storage/b-episode-" + id + ".mkv");
        values.put(VideoColumns.TITLE, "Episode " + episode);
        values.put(VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE, BaseTags.TV_SHOW);
        values.put(VideoColumns.SCRAPER_EPISODE_ID, id);
        values.put(VideoColumns.SCRAPER_E_SEASON, 1);
        values.put(VideoColumns.SCRAPER_E_EPISODE, episode);
        values.put(VideoColumns.SCRAPER_E_NAME, "Episode " + episode);
        values.put(VideoColumns.SCRAPER_SHOW_ID, series);
        values.put(VideoColumns.SCRAPER_S_ONLINE_ID, series);
        values.put(VideoColumns.SCRAPER_ONLINE_ID, series);
        values.put(PersonalVideoActions.COLUMN, personal ? 1 : 0);
        values.put(VideoColumns.ARCHOS_HIDDEN_BY_USER, hidden ? 1 : 0);
        db.insertOrThrow("video", null, values);
    }

    @After public void cleanup() throws Exception
    {
        if (service != null) {
            try {
                java.lang.reflect.Field field = PlayerService.class.getDeclaredField("playerEpisodeWorker");
                field.setAccessible(true);
                ExecutorService worker = (ExecutorService)field.get(service);
                worker.shutdownNow();
                assertTrue(worker.awaitTermination(3, TimeUnit.SECONDS));
            } catch (NoSuchFieldException legacyControl) {
                // The previous implementation has no owned worker; useful for the negative control.
            }
        }
        db.close();
        PreviewLibraryLoader.cached = null;
    }

    @Test public void exactSeriesQueryRetainsPhysicalIdsAndFiltersPersonalBeforeSeasonProjection()
    {
        var rows = PlayerEpisodeRepository.load(context, "123", new CancellationSignal());
        assertEquals(List.of(1L, 2L, 4L), rows.stream().map(e -> ((Video)e.media).getId()).toList());
        assertEquals(List.of(1L, 2L), PlaybackEpisodes.season(RuntimeEnvironment.getApplication(), rows, 1)
                .stream().map(e -> e.id).toList());
    }

    @Test public void cancellationBeforeAdmissionDoesNotTouchTheProvider()
    {
        CancellationSignal cancelled = new CancellationSignal();
        cancelled.cancel();
        assertThrows(OperationCanceledException.class, () -> PlayerEpisodeRepository.load(context, "123", cancelled));
        verifyNoInteractions(resolver);
    }

    @Test public void serviceWorksWithoutHomeSnapshotAndLatePreviousSourceCannotReplaceTheNewSeason() throws Exception
    {
        service = spy(new PlayerService());
        ReflectionHelpers.callInstanceMethod(service, "attachBaseContext",
                ClassParameter.from(Context.class, RuntimeEnvironment.getApplication()));
        when(service.getContentResolver()).thenReturn(resolver);
        ReflectionHelpers.setField(service, "mHandler", new Handler(Looper.getMainLooper()));
        info(1, "123", 1);
        service.previewSeasonEpisodes();
        java.util.concurrent.Future<?> first = ReflectionHelpers.getField(service, "playerEpisodeWork");
        first.get(3, TimeUnit.SECONDS);
        // The first publish is queued, while a different source becomes current.
        info(5, "456", 2);
        assertEquals(0, service.previewSeasonEpisodes().size());
        java.util.concurrent.Future<?> next = ReflectionHelpers.getField(service, "playerEpisodeWork");
        next.get(3, TimeUnit.SECONDS);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(List.of(5L, 6L), service.previewSeasonEpisodes().stream().map(e -> e.id).toList());
        PreviewLibraryLoader.cached = new PreviewLibraryLoader.Snapshot();
        assertEquals(List.of(5L, 6L), service.previewSeasonEpisodes().stream().map(e -> e.id).toList());
    }

    @Test public void emptyHomeCacheDoesNotPreventTheServiceFromSupplyingSeasonEpisodes() throws Exception
    {
        service = spy(new PlayerService());
        ReflectionHelpers.callInstanceMethod(service, "attachBaseContext",
                ClassParameter.from(Context.class, RuntimeEnvironment.getApplication()));
        when(service.getContentResolver()).thenReturn(resolver);
        ReflectionHelpers.setField(service, "mHandler", new Handler(Looper.getMainLooper()));
        info(1, "123", 1);
        long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
        List<PlaybackEpisodes.Item> rows;
        do {
            rows = service.previewSeasonEpisodes();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            if (rows.size() == 2)
                break;
            Thread.sleep(5);
        } while (System.nanoTime() < end);
        assertEquals(2, rows.size());
    }

    @Test public void authoritativeEmptySeriesDoesNotFallBackToAnOldHomeSnapshot() throws Exception
    {
        service = spy(new PlayerService());
        ReflectionHelpers.callInstanceMethod(service, "attachBaseContext",
                ClassParameter.from(Context.class, RuntimeEnvironment.getApplication()));
        when(service.getContentResolver()).thenReturn(resolver);
        ReflectionHelpers.setField(service, "mHandler", new Handler(Looper.getMainLooper()));
        PreviewLibraryLoader.Snapshot old = new PreviewLibraryLoader.Snapshot();
        old.episodes.addAll(PlayerEpisodeRepository.load(context, "123", new CancellationSignal()));
        PreviewLibraryLoader.cached = old;
        info(1, "789", 1);
        assertEquals(0, service.previewSeasonEpisodes().size());
        ((java.util.concurrent.Future<?>)ReflectionHelpers.getField(service, "playerEpisodeWork"))
                .get(3, TimeUnit.SECONDS);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(0, service.previewSeasonEpisodes().size());
    }

    @Test public void firstMainThreadProjectionDoesNotEnumerateCapabilities()
            throws Exception
    {
        Context guarded = new android.content.ContextWrapper(RuntimeEnvironment.getApplication()) {
            @Override public Context getApplicationContext()
            {
                return this;
            }
            @Override public ContentResolver getContentResolver()
            {
                return resolver;
            }
            @Override public Object getSystemService(String name)
            {
                if (Looper.myLooper() == Looper.getMainLooper())
                    throw new AssertionError("Capability enumeration on main");
                return super.getSystemService(name);
            }
        };
        ExecutorService worker = java.util.concurrent.Executors.newSingleThreadExecutor();
        try {
            var rows = worker.submit(() -> PlayerEpisodeRepository.load(guarded, "123", new CancellationSignal()))
                    .get(3, TimeUnit.SECONDS);
            assertEquals(2, PlaybackEpisodes.season(guarded, rows, 1).size());
        } finally {
            worker.shutdownNow();
            assertTrue(worker.awaitTermination(3, TimeUnit.SECONDS));
        }
    }

    private void info(long id, String series, int generation)
    {
        VideoDbInfo info = new VideoDbInfo(Uri.parse("file:///storage/" + id));
        info.isShow = true;
        info.id = id;
        info.scraperShowId = series;
        ReflectionHelpers.setField(service, "mVideoInfo", info);
        ReflectionHelpers.setField(service, "mSourceRequestGeneration", generation);
    }
}
