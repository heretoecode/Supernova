package com.archos.mediacenter.video.streaming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.Context;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class DiscoveryGatewayTest
{
    @Test public void similarUsesAResultsEnvelopeRatherThanTheSourceTitleId() throws Exception
    {
        StreamingRepository.validateMetadataResponse("movie/12/similar", new JSONObject("{results:[{id:24}]}"));
        assertThrows(IOException.class,
            () -> StreamingRepository.validateMetadataResponse("movie/12/similar", new JSONObject("{id:12}")));
    }

    @Test public void traktUsesOnlyTheFixedOriginAndNeverAnAccountToken() throws Exception
    {
        Context context = mock(Context.class);
        when(context.getString(anyInt())).thenReturn("TEST_APPLICATION_KEY");
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            assertEquals("api.trakt.tv", chain.request().url().host());
            assertEquals("https", chain.request().url().scheme());
            assertEquals("2", chain.request().header("trakt-api-version"));
            assertEquals("TEST_APPLICATION_KEY", chain.request().header("trakt-api-key"));
            assertNull(chain.request().header("Authorization"));
            return new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body(ResponseBody.create(MediaType.parse("application/json"),
                    "[{ids:{tmdb:42}}]")).build();
        }).build();
        assertEquals(1, StreamingRepository.traktDiscovery(context, "movies/dune/related", client).length());
        assertThrows(IOException.class, () -> StreamingRepository.traktDiscovery(context,
            "movies/../../users/private", client));
        assertThrows(IOException.class, () -> StreamingRepository.traktDiscovery(context,
            "https://elsewhere.invalid/movies/dune", client));
    }

    @Test public void traktResponsesAreBoundedBeforeJsonParsing() throws Exception
    {
        Context context = mock(Context.class);
        when(context.getString(anyInt())).thenReturn("TEST_APPLICATION_KEY");
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain ->
            new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200).message("OK")
                .body(ResponseBody.create(MediaType.parse("application/json"), "x".repeat(2 * 1024 * 1024 + 1)))
                .build()).build();
        assertThrows(IOException.class,
            () -> StreamingRepository.traktDiscovery(context, "movies/dune/related", client));
    }
}
