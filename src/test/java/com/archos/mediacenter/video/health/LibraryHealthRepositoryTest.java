package com.archos.mediacenter.video.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import com.archos.mediacenter.video.utils.CanonicalMatchProviderTest;
import com.archos.mediaprovider.video.LibraryHealthEvidence;
import com.archos.mediaprovider.video.PersonalVideoActions;
import com.archos.mediaprovider.video.MissingMediaActions;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.EOFException;
import java.io.File;
import java.net.SocketTimeoutException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LibraryHealthRepositoryTest
{
    private final CanonicalMatchProviderTest fixture = new CanonicalMatchProviderTest();
    private Application app;

    @Before public void setup()
    {
        fixture.setup();
        app = RuntimeEnvironment.getApplication();
        new File(app.getNoBackupFilesDir(), "library-health-v1.db").delete();
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            helper.getWritableDatabase().execSQL("UPDATE files SET _size=123");
            helper.getWritableDatabase().execSQL(
                "UPDATE files SET ArchosMediaScraper_id=-2,ArchosMediaScraper_type=-1 WHERE _id=11");
        }
    }

    @After public void close()
    {
        fixture.close();
    }

    @Test public void recordsKnownReasonsAndKeepsUnknownFailuresHonest()
    {
        assertEquals(LibraryHealthEvidence.Reason.TIMED_OUT,
                     LibraryHealthEvidence.readReason(new SocketTimeoutException()));
        assertEquals(LibraryHealthEvidence.Reason.DAMAGED,
                     LibraryHealthEvidence.readReason(new EOFException()));
        assertEquals(LibraryHealthEvidence.Reason.UNSUPPORTED,
                     LibraryHealthEvidence.readReason(new UnsupportedOperationException()));
        assertEquals(LibraryHealthEvidence.Reason.UNKNOWN,
                     LibraryHealthEvidence.readReason(new java.io.IOException("Private source")));
    }

    @Test public void eachKindHasItsOwnRowsAndCandidateCountsSurviveReopening() throws Exception
    {
        LibraryHealthEvidence.readFailure(app, "file:///one.mkv", new SocketTimeoutException());
        LibraryHealthEvidence.record(app, "file:///other.mkv", LibraryHealthEvidence.Kind.NOT_MATCHED,
                                     LibraryHealthEvidence.Reason.POSSIBLE, 3, false);
        var unreadable = LibraryHealthRepository.read(app, LibraryHealthEvidence.Kind.COULD_NOT_READ, () -> false);
        var unmatched = LibraryHealthRepository.read(app, LibraryHealthEvidence.Kind.NOT_MATCHED, () -> false);
        assertEquals(1, unreadable.size());
        assertEquals(10, unreadable.get(0).videoId);
        assertEquals(LibraryHealthEvidence.Reason.TIMED_OUT, unreadable.get(0).reason);
        assertEquals(1, unmatched.size());
        assertEquals(3, unmatched.get(0).candidates);
        assertEquals(LibraryHealthEvidence.Reason.POSSIBLE, unmatched.get(0).reason);
        assertTrue(LibraryHealthRepository.tryAgain(app, unreadable.get(0), () -> false));
        assertFalse(LibraryHealthRepository.tryAgain(app, unmatched.get(0), () -> false));
        LibraryHealthEvidence.clear(app, "file:///one.mkv", LibraryHealthEvidence.Kind.COULD_NOT_READ);
        assertTrue(LibraryHealthRepository.read(app, LibraryHealthEvidence.Kind.COULD_NOT_READ, () -> false).isEmpty());
    }

    @Test public void deletedReplacedAndPersonalRowsCannotStayActionable() throws Exception
    {
        LibraryHealthEvidence.readFailure(app, "file:///one.mkv", new EOFException());
        var old = LibraryHealthRepository.read(app, LibraryHealthEvidence.Kind.COULD_NOT_READ, () -> false).get(0);
        var undo = PersonalVideoActions.set(app, new MissingMediaActions.StoredFile(10, "file:///one.mkv", 123), true);
        assertTrue(LibraryHealthRepository.read(app, LibraryHealthEvidence.Kind.COULD_NOT_READ, () -> false).isEmpty());
        PersonalVideoActions.undo(app, undo);
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            helper.getWritableDatabase().execSQL("UPDATE files SET _data='file:///replacement.mkv' WHERE _id=10");
        }
        assertFalse(LibraryHealthRepository.tryAgain(app, old, () -> false));
        assertTrue(LibraryHealthRepository.read(app, LibraryHealthEvidence.Kind.COULD_NOT_READ, () -> false).isEmpty());
    }
}
