package com.archos.mediacenter.video.health;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediaprovider.video.LibraryHealthEvidence;
import com.archos.mediaprovider.video.PersonalVideoActions;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/** Worker-only per-kind health inventory. Deleted, replaced and personal files cannot retain actionable rows. */
public final class LibraryHealthRepository
{
    public static final class Row
    {
        public final long videoId;
        public final String title;
        public final LibraryHealthEvidence.Kind kind;
        public final LibraryHealthEvidence.Reason reason;
        public final int candidates;
        public final boolean moreCandidates;
        public final long checkedUtcMs;
        private final String path;

        private Row(long id, String title, String path, LibraryHealthEvidence.Evidence evidence)
        {
            videoId = id;
            this.title = title;
            this.path = path;
            kind = evidence.kind();
            reason = evidence.reason();
            candidates = evidence.candidates();
            moreCandidates = evidence.more();
            checkedUtcMs = evidence.checkedUtcMs();
        }
    }

    public static List<Row> read(Context context, LibraryHealthEvidence.Kind kind, BooleanSupplier cancelled)
        throws Exception
    {
        Map<String, LibraryHealthEvidence.Evidence> issues = new HashMap<>();
        try (LibraryHealthEvidence store = new LibraryHealthEvidence(context))
        {
            for (var evidence : store.read(kind))
                issues.put(evidence.physical(), evidence);
        }
        List<Row> result = new ArrayList<>();
        try (VideoOpenHelper helper = new VideoOpenHelper(context);
             Cursor files = helper.getReadableDatabase().query("files",
                 new String[] {"_id", "_data", "_display_name", "ArchosMediaScraper_id"},
                 "media_type=3 AND " + PersonalVideoActions.VISIBLE,
                 null, null, null, "_id"))
        {
            while (files.moveToNext())
            {
                if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                    throw new InterruptedIOException("Health inventory stopped");
                String path = files.getString(1);
                var evidence = issues.get(LibraryHealthEvidence.physical(path));
                if (kind == LibraryHealthEvidence.Kind.NOT_MATCHED && files.getLong(3) > 0)
                    continue;
                if (kind == LibraryHealthEvidence.Kind.NOT_MATCHED && evidence == null)
                    evidence = new LibraryHealthEvidence.Evidence("", kind,
                        LibraryHealthEvidence.Reason.NO_MATCH, 0, false, 0);
                if (evidence != null)
                    result.add(new Row(files.getLong(0), files.getString(2), path, evidence));
            }
        }
        return List.copyOf(result);
    }

    /** Request existing single-file indexing; evidence remains until a real success or new failure arrives. */
    public static boolean tryAgain(Context context, Row expected, BooleanSupplier cancelled) throws Exception
    {
        if (expected.kind != LibraryHealthEvidence.Kind.COULD_NOT_READ || cancelled.getAsBoolean())
            return false;
        try (VideoOpenHelper helper = new VideoOpenHelper(context);
             Cursor row = helper.getReadableDatabase().query("files", new String[] {"_data"},
                 "_id=? AND " + PersonalVideoActions.VISIBLE,
                 new String[] {Long.toString(expected.videoId)}, null, null, null))
        {
            if (!row.moveToFirst() || !expected.path.equals(row.getString(0)) || cancelled.getAsBoolean())
                return false;
        }
        VideoStore.requestIndexing(Uri.parse(expected.path), context, false);
        return true;
    }

    private LibraryHealthRepository()
    {
    }
}
