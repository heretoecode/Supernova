package com.archos.mediacenter.video.diagnostics;

import java.util.function.BiConsumer;
import org.json.JSONArray;
import org.json.JSONObject;

/** One worker's stage context, including failures before an archive/destination exists. */
public final class DiagnosticBackupState
{
    private final String operation;
    private final String direction;
    private final BiConsumer<String, Object[]> sink;
    private String stage = "collect", category = "none";
    private long rows, bytes;

    public DiagnosticBackupState(String operation, String direction, BiConsumer<String, Object[]> sink)
    {
        this.operation = operation;
        this.direction = direction;
        this.sink = sink;
        emit("backup_started");
    }

    public void stage(String stage, String category, long rows, long bytes)
    {
        this.stage = stage;
        this.category = category;
        this.rows = rows;
        this.bytes = bytes;
        emit("backup_stage");
    }

    public void failure(Throwable failure)
    {
        Throwable reason = failure;
        java.util.Set<Throwable> seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        while (reason.getCause() != null && seen.add(reason.getCause()))
            reason = reason.getCause();
        sink.accept("backup_failed", new Object[] {"operation_id",  operation,
                                                   "direction",     direction,
                                                   "stage",         stage,
                                                   "category",      category,
                                                   "rows",          rows,
                                                   "bytes",         bytes,
                                                   "failure_class", reason.getClass().getName(),
                                                   "message",       reason.getMessage(),
                                                   "outer_message", failure.getMessage(),
                                                   "trace",         DiagnosticRedactor.trace(failure),
                                                   "trace_origin",  "caught_exception"});
    }

    public void complete()
    {
        emit("backup_complete");
    }

    private void emit(String event)
    {
        sink.accept(event, new Object[] {"operation_id", operation, "direction", direction, "stage", stage, "category",
                                         category, "rows", rows, "bytes", bytes});
    }

    public static long rows(JSONObject payload)
    {
        if (payload == null)
            return 0;
        for (String key : new String[] {"rows", "items", "entries", "values"})
        {
            Object value = payload.opt(key);
            if (value instanceof JSONArray)
                return ((JSONArray)value).length();
            if (value instanceof JSONObject)
                return ((JSONObject)value).length();
        }
        return 0;
    }
}
