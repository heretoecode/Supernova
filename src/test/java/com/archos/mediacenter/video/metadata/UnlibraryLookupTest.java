package com.archos.mediacenter.video.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class UnlibraryLookupTest
{
    private static JSONObject film(long id, String title, String year) throws Exception
    {
        return new JSONObject().put("id", id).put("title", title).put("release_date", year + "-01-01")
            .put("poster_path", "/poster.jpg").put("backdrop_path", "/backdrop.jpg");
    }

    @Test
    public void validatedFixMatchTransportUsesCallerOwnedClient() throws Exception
    {
        android.content.Context app = org.robolectric.RuntimeEnvironment.getApplication();
        android.content.res.Resources resources = new android.content.res.Resources(
            app.getAssets(), app.getResources().getDisplayMetrics(), app.getResources().getConfiguration()) {
            @Override
            public String getString(int id)
            {
                return "fixture";
            }
        };
        android.content.Context context = new android.content.ContextWrapper(app) {
            @Override
            public android.content.res.Resources getResources()
            {
                return resources;
            }
        };
        AtomicInteger calls = new AtomicInteger();
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder().addInterceptor(chain -> {
            calls.incrementAndGet();
            assertEquals("/3/search/movie", chain.request().url().encodedPath());
            return new okhttp3.Response.Builder().request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1)
                .code(200).message("OK").body(okhttp3.ResponseBody.create(
                    "{\"page\":1,\"results\":[],\"total_pages\":0,\"total_results\":0}",
                    okhttp3.MediaType.get("application/json"))).build();
        }).build();
        JSONObject result = com.archos.mediacenter.video.streaming.StreamingRepository.matchSearch(
            context, "search/movie", java.util.Map.of("query", "Film"), client);
        assertEquals(0, result.getJSONArray("results").length());
        assertEquals(1, calls.get());
        org.junit.Assert.assertThrows(java.io.IOException.class,
            () -> com.archos.mediacenter.video.streaming.StreamingRepository.matchSearch(
                context, "search/person", java.util.Map.of(), client));
        assertEquals(1, calls.get());
    }

    @Test
    public void parserSuppliesFilmTitleAndYearWithoutPassingLocationsOrWritingLibrary() throws Exception
    {
        String[] databases = org.robolectric.RuntimeEnvironment.getApplication().databaseList();
        List<String> requests = new ArrayList<>();
        UnlibraryLookup.Result result = UnlibraryLookup.lookup(Uri.parse("smb://user:secret@host/Film.2020.mkv"),
            (path, parameters) -> {
                requests.add(path + parameters);
                return new JSONObject().put("results", new JSONArray().put(film(7, "Film", "2020")));
            });
        assertEquals(List.of("search/movie{query=Film}"), requests);
        org.junit.Assert.assertArrayEquals(databases,
            org.robolectric.RuntimeEnvironment.getApplication().databaseList());
        assertEquals(UnlibraryLookup.Kind.FILM, result.kind);
        assertEquals(UnlibraryLookup.Confidence.CONFIDENT, result.confidence);
        assertEquals(7, result.tmdbId);
        assertEquals("2020", result.year);
        assertEquals(-1, result.episode);
        assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", result.poster.toString());
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", result.backdrop.toString());
    }

    @Test
    public void episodeUsesShowIdentityAndKeepsParsedNumbers() throws Exception
    {
        UnlibraryLookup.Result result = UnlibraryLookup.lookup(Uri.parse("file:///Series.S02E03.mkv"),
            (path, parameters) -> {
                assertEquals("search/tv", path);
                assertEquals("Series", parameters.get("query"));
                return new JSONObject().put("results", new JSONArray().put(new JSONObject()
                    .put("id", 9).put("name", "Series").put("first_air_date", "2018-01-01")));
            });
        assertEquals(UnlibraryLookup.Kind.EPISODE, result.kind);
        assertEquals(2, result.season);
        assertEquals(3, result.episode);
        assertEquals(9, result.tmdbId);
    }

    @Test
    public void duplicateExactMatchesAndWrongYearRemainPossibleAndUnsafeImagesAreDropped() throws Exception
    {
        JSONObject row = film(7, "Film", "1999").put("poster_path", "https://user:secret@host/image");
        UnlibraryLookup.Result mismatch = UnlibraryLookup.lookup(Uri.parse("file:///Film.2020.mkv"),
            (path, parameters) -> new JSONObject().put("results", new JSONArray().put(row)));
        assertEquals(UnlibraryLookup.Confidence.POSSIBLE, mismatch.confidence);
        assertNull(mismatch.poster);
        JSONObject exact = film(8, "Film", "2020");
        UnlibraryLookup.Result ambiguous = UnlibraryLookup.lookup(Uri.parse("file:///Film.2020.mkv"),
            (path, parameters) -> new JSONObject().put("results", new JSONArray().put(exact).put(exact)));
        assertEquals(UnlibraryLookup.Confidence.POSSIBLE, ambiguous.confidence);
    }

    @Test
    public void emptyOfflineAndMalformedResponsesReturnNoneOnceOnCallbackExecutor() throws Exception
    {
        for (int failure = 0; failure < 3; failure++)
        {
            int mode = failure;
            CountDownLatch delivered = new CountDownLatch(1);
            AtomicReference<UnlibraryLookup.Result> result = new AtomicReference<>();
            List<Runnable> callbacks = new java.util.concurrent.CopyOnWriteArrayList<>();
            UnlibraryLookup.start(Uri.parse("file:///Film.mkv"), command -> {
                callbacks.add(command);
                delivered.countDown();
            }, result::set, job -> (path, parameters) -> {
                if (mode == 0)
                    throw new java.io.IOException("offline");
                return mode == 1 ? new JSONObject() : new JSONObject().put("results", new JSONArray());
            }, 2, TimeUnit.SECONDS);
            assertTrue(delivered.await(3, TimeUnit.SECONDS));
            assertNull(result.get());
            assertEquals(1, callbacks.size());
            callbacks.get(0).run();
            assertSame(UnlibraryLookup.NONE, result.get());
        }
    }

    @Test
    public void deadlineInterruptsWorkAndLateResultCannotDeliverTwice() throws Exception
    {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);
        CountDownLatch delivered = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        UnlibraryLookup.start(Uri.parse("file:///Film.mkv"), Runnable::run, result -> {
            assertSame(UnlibraryLookup.NONE, result);
            calls.incrementAndGet();
            delivered.countDown();
        }, job -> (path, parameters) -> {
            entered.countDown();
            try
            {
                new CountDownLatch(1).await();
            }
            catch (InterruptedException stopped)
            {
                interrupted.countDown();
            }
            return new JSONObject().put("results", new JSONArray());
        }, 500, TimeUnit.MILLISECONDS);
        assertTrue(entered.await(2, TimeUnit.SECONDS));
        assertTrue(delivered.await(2, TimeUnit.SECONDS));
        assertTrue(interrupted.await(2, TimeUnit.SECONDS));
        assertEquals(1, calls.get());
    }

    @Test
    public void cancellationCancelsTheActiveHttpCallAndInterruptsTheWorker() throws Exception
    {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);
        AtomicInteger callbacks = new AtomicInteger();
        okhttp3.Call call = new okhttp3.OkHttpClient().newCall(
            new okhttp3.Request.Builder().url("https://example.com/").build());
        UnlibraryLookup.Job job = UnlibraryLookup.start(Uri.parse("file:///Film.mkv"), Runnable::run,
            result -> callbacks.incrementAndGet(), owner -> (path, parameters) -> {
                java.lang.reflect.Method attach = owner.getClass().getDeclaredMethod("attach", okhttp3.Call.class);
                attach.setAccessible(true);
                attach.invoke(owner, call);
                entered.countDown();
                try
                {
                    new CountDownLatch(1).await();
                }
                catch (InterruptedException cancelled)
                {
                    interrupted.countDown();
                }
                return new JSONObject();
            }, 3, TimeUnit.SECONDS);
        assertTrue(entered.await(2, TimeUnit.SECONDS));
        job.cancel();
        assertTrue(call.isCanceled());
        assertTrue(interrupted.await(2, TimeUnit.SECONDS));
        assertEquals(0, callbacks.get());
    }

    @Test
    public void cancellationSuppressesAlreadyQueuedCallback() throws Exception
    {
        CountDownLatch queued = new CountDownLatch(1);
        AtomicReference<Runnable> callback = new AtomicReference<>();
        AtomicInteger calls = new AtomicInteger();
        UnlibraryLookup.Job job = UnlibraryLookup.start(null, command -> {
            callback.set(command);
            queued.countDown();
        }, result -> calls.incrementAndGet(), ignored -> (path, parameters) -> new JSONObject(),
            2, TimeUnit.SECONDS);
        assertTrue(queued.await(2, TimeUnit.SECONDS));
        assertFalse(job.isCancelled());
        job.cancel();
        callback.get().run();
        assertTrue(job.isCancelled());
        assertEquals(0, calls.get());
    }
}
