package com.archos.mediacenter.video.scanning;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.filecorelibrary.connections.DraftConnectionTest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Saved connections reuse the isolated Add Connection probe. Put.io remains owned by PutioSyncScheduler.status(). */
public final class SavedConnectionReachability
{
    private static final ExecutorService WORK = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "SavedConnectionReachability");
        thread.setDaemon(true);
        return thread;
    });

    /** Worker-only construction (preference load). Use opaque saved row IDs, never credential-bearing addresses. */
    public static ConnectionReachability create(Context context, Map<String, DraftConnectionTest.Draft> drafts)
    {
        Context app = context.getApplicationContext();
        Map<String, DraftConnectionTest.Draft> connections = Map.copyOf(drafts);
        for (String id : connections.keySet())
            if (!id.matches("[A-Za-z0-9-]{1,100}"))
                throw new IllegalArgumentException("Opaque saved connection ID required");
        SharedPreferences prefs = app.getSharedPreferences("connection_last_seen_v1", Context.MODE_PRIVATE);
        Map<String, ConnectionReachability.Status> saved = new LinkedHashMap<>();
        for (String id : connections.keySet())
            saved.put(id, new ConnectionReachability.Status(false, prefs.getLong(id, 0), false));
        return new ConnectionReachability(WORK, (id, done) -> {
            DraftConnectionTest.Draft draft = connections.get(id);
            if (draft == null)
                throw new IllegalArgumentException("Connection no longer saved");
            return DraftConnectionTest.start(app, draft, Runnable::run, result -> done.accept(result.succeeded()));
        }, saved, snapshot -> {
            SharedPreferences.Editor edit = prefs.edit();
            snapshot.forEach((id, status) -> edit.putLong(id, status.lastSeenUtcMs()));
            edit.apply();
        });
    }

    private SavedConnectionReachability()
    {
    }
}
