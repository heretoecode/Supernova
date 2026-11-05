package com.archos.mediacenter.video.utils.backup;

import com.archos.mediacenter.video.diagnostics.DiagnosticBackupState;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/** Nested archive operations retain the enclosing export/import's operation and exact last stage. */
final class BackupDiagnostics
{
    interface Work<T>
    {
        T run() throws IOException;
    }
    private static final ThreadLocal<DiagnosticBackupState> CURRENT = new ThreadLocal<>();

    static <T> T run(String direction, String initial, Work<T> work) throws IOException
    {
        if (CURRENT.get() != null)
            return work.run();
        String operation = Diagnostics.operation("backup");
        long started = android.os.SystemClock.elapsedRealtime();
        DiagnosticBackupState state = new DiagnosticBackupState(operation, direction, Diagnostics::event);
        CURRENT.set(state);
        stage(initial, null, 0, 0);
        try (Diagnostics.OperationScope ignored = Diagnostics.operationScope(operation))
        {
            T result = work.run();
            state.complete();
            return result;
        }
        catch (IOException | RuntimeException failure)
        {
            state.failure(failure);
            throw failure;
        }
        finally
        {
            CURRENT.remove();
            Diagnostics.finishOperation(operation, "backup", started);
        }
    }

    static void stage(String name, BackupCategory category, long rows, long bytes)
    {
        DiagnosticBackupState state = CURRENT.get();
        if (state != null)
            state.stage(name, category == null ? "none" : category.name(), rows, bytes);
    }

    static void payload(String name, BackupCategory category, JSONObject value)
    {
        stage(name, category, DiagnosticBackupState.rows(value),
              value == null ? 0 : value.toString().getBytes(StandardCharsets.UTF_8).length);
    }

    private BackupDiagnostics()
    {
    }
}
