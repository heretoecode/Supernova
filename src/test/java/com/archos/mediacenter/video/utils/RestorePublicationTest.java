package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.database.ContentObserver;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoStore;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class RestorePublicationTest {
    private final CombinedIndexedSourcesTest source=new CombinedIndexedSourcesTest();
    private CombinedViewingRestoreTest fixture;private File cache;private PreviewLibraryLoader.Snapshot original;
    private final AtomicInteger notified=new AtomicInteger();private ContentObserver observer;
    private static class ProcessDeath extends Error {}
    @Before public void setup()throws Exception{source.setup();fixture=source.fixture;cache=new File(fixture.app.getCacheDir(),"preview-library-v42");original=new PreviewLibraryLoader.Snapshot();PreviewLibraryLoader.cached=original;try(ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream(cache))){output.writeObject(original);}observer=new ContentObserver(null){@Override public void onChange(boolean self,android.net.Uri uri){assertFalse("Refresh follows journal cleanup",fixture.journal.exists());notified.incrementAndGet();}};fixture.app.getContentResolver().registerContentObserver(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,true,observer);}
    @After public void cleanup(){fixture.app.getContentResolver().unregisterContentObserver(observer);PreviewLibraryLoader.cached=null;}
    @Test public void selectedRestorePublishesOnceOnlyAfterAllParticipantsAndCompletionAreDurable()throws Exception{
        fixture.transaction.apply(fixture.transaction.prepare(source.all(),null,fixture.physical()),()->false,at->{assertSame(at.toString(),original,PreviewLibraryLoader.memoryCache());assertTrue(at.toString(),cache.exists());assertEquals(at.toString(),0,notified.get());});assertEquals(1,notified.get());assertNull(PreviewLibraryLoader.memoryCache());assertFalse(cache.exists());assertFalse(fixture.transaction.refreshFailed());source.assertNew();
    }
    @Test public void preDecisionRollbackAndCommittedStartupRecoveryPublishTheirActualFinalState()throws Exception{
        try{fixture.transaction.apply(fixture.transaction.prepare(source.all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}assertSame(original,PreviewLibraryLoader.memoryCache());assertEquals(0,notified.get());assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,fixture.transaction.recover());assertEquals(1,notified.get());assertNull(PreviewLibraryLoader.memoryCache());assertFalse(cache.exists());assertEquals(7,fixture.prefs.getInt("preview_accent41",0));
        original=new PreviewLibraryLoader.Snapshot();PreviewLibraryLoader.cached=original;try(ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream(cache))){output.writeObject(original);}try{fixture.transaction.apply(fixture.transaction.prepare(source.all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}assertSame(original,PreviewLibraryLoader.memoryCache());assertEquals(1,notified.get());PreferenceRestoreTransaction.recoverAtStartup(fixture.app);assertEquals(2,notified.get());assertNull(PreviewLibraryLoader.memoryCache());assertFalse(cache.exists());source.assertNew();
    }
}
