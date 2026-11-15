package com.archos.mediacenter.video.data;

import android.content.Context;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import okhttp3.OkHttpClient;
import org.json.JSONObject;

/** Worker adapter; caller owns the HTTP client, its cancellation dispatcher and per-call deadline. */
public final class LibrarySearchServices implements LibrarySearchMetadata.Transport
{
    private final Context context;
    private final OkHttpClient client;

    public LibrarySearchServices(Context context, OkHttpClient client)
    {
        this.context = context.getApplicationContext();
        this.client = client.newBuilder().dispatcher(new okhttp3.Dispatcher()).build();
    }

    @Override public List<Long> familyLinks(long tvId, BooleanSupplier cancelled) throws Exception
    {
        return SeriesFamilyLinks.load(tvId, client, cancelled);
    }

    @Override public void cancel()
    {
        client.dispatcher().cancelAll();
    }

    @Override public JSONObject request(String path, Map<String, String> parameters) throws Exception
    {
        return StreamingRepository.libraryDiscovery(context, path, parameters, client);
    }

    @Override public List<String> services(LibrarySearch.Identity title, BooleanSupplier cancelled) throws Exception
    {
        if (cancelled.getAsBoolean() || !StreamingRepository.prefs(context)
            .getBoolean(StreamingRepository.ENABLED, false))
            return List.of();
        var availability = StreamingRepository.load(context, title.kind(), title.id(),
                                                    StreamingRepository.country(context), -1, client);
        if (cancelled.getAsBoolean() || !StreamingRepository.prefs(context)
            .getBoolean(StreamingRepository.ENABLED, false))
            return List.of();
        java.util.Set<String> services = new java.util.LinkedHashSet<>();
        for (var offer : StreamingRepository.filter(availability, StreamingRepository.selected(context),
                                                    StreamingRepository.preferred(context)))
            services.add(offer.provider.name);
        return List.copyOf(services);
    }
}
