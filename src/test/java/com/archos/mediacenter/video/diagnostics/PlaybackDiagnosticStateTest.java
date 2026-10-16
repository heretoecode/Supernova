package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import android.net.Uri;
import com.archos.mediacenter.video.player.Player;
import com.archos.medialib.IMediaPlayer;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static com.archos.mediacenter.video.diagnostics.Diagnostics.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PlaybackDiagnosticStateTest {
    private Application app;private PlaybackDiagnosticState state;private Object backend;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,false);Diagnostics.setEnabled(app,true);Diagnostics.beginPlayback(Uri.parse("smb://user:AUTH_CANARY@host/FILE_CANARY?token=QUERY_CANARY"));state=new PlaybackDiagnosticState();backend=new Object();}
    @After public void teardown(){state.close(PlaybackOutcome.CANCELLED);Diagnostics.endPlayback("test");Diagnostics.setEnabled(app,false);}
    private List<JSONObject> rows()throws Exception {
        DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");List<JSONObject> rows=new ArrayList<>();
        for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n"))if(!line.isEmpty())rows.add(new JSONObject(line));return rows;
    }
    private List<JSONObject> attempts()throws Exception {List<JSONObject> result=new ArrayList<>();for(JSONObject row:rows())if(row.getString("event").equals("playback_attempt"))result.add(row);return result;}
    private PlaybackAttempt open(){PlaybackAttempt open=state.opening();state.bind(backend,open,0);state.submitted(open,0);return open;}
    @Test public void preparedIsNotOutputAndFirstOutputCompletesOnlyOnceWithCallbackQuality()throws Exception {
        PlaybackAttempt open=open();state.prepared(backend,0);assertEquals(4,attempts().size());
        assertFalse(attempts().stream().anyMatch(x->x.optString("outcome").equals("COMPLETED")));
        state.output(backend,0);state.output(backend,0);List<JSONObject> records=attempts();assertEquals(6,records.size());JSONObject terminal=records.get(5);
        assertEquals(open.id,terminal.getString("attempt_id"));assertEquals("COMPLETED",terminal.getString("outcome"));assertEquals("CALLBACK",terminal.getString("measurement_quality"));assertEquals(-1,terminal.getLong("achieved_ms"));
        state.close(PlaybackOutcome.CANCELLED);assertEquals(6,attempts().size());
    }
    @Test public void supersededOpenCannotBindOrFinishItsReplacementAndOldBackendCallbacksAreIgnored()throws Exception {
        PlaybackAttempt old=open(),replacement=state.opening();Object next=new Object();state.bind(next,replacement,1);int count=attempts().size();
        state.bind(backend,old,0);state.submitted(old,0);state.failed(old);state.prepared(backend,0);state.output(backend,0);state.failed(backend);assertEquals(count,attempts().size());
        state.prepared(next,1);state.output(next,1);assertEquals("COMPLETED",attempts().get(attempts().size()-1).getString("outcome"));assertEquals(replacement.id,attempts().get(attempts().size()-1).getString("attempt_id"));
    }
    @Test public void sequentialSeekHasASeparateTargetAndParentButNoInventedPresentationClock()throws Exception {
        PlaybackAttempt open=open();state.seeking(7000,0);state.seekCallback(backend,0);state.allSeeksComplete(backend,0);
        List<JSONObject> records=attempts();JSONObject seek=records.get(records.size()-1);assertEquals("SEEK",seek.getString("operation"));assertEquals(open.id,seek.getString("parent_attempt_id"));assertEquals(7000,seek.getLong("target_ms"));assertEquals(-1,seek.getLong("achieved_ms"));assertEquals("COMPLETED",seek.getString("outcome"));assertEquals("CALLBACK",seek.getString("measurement_quality"));assertNotEquals(open.id,seek.getString("attempt_id"));
        state.seeking(3000,0);state.seekCallback(backend,0);state.allSeeksComplete(backend,0);assertNotEquals(seek.getString("attempt_id"),attempts().get(attempts().size()-1).getString("attempt_id"));
    }
    @Test public void overlappingSeeksAreSupersededOrUnattributedRatherThanFalselyPaired()throws Exception {
        open();state.seeking(1000,0);state.seeking(5000,0);state.seekCallback(backend,0);state.allSeeksComplete(backend,0);
        List<JSONObject> seeks=new ArrayList<>();for(JSONObject row:attempts())if(row.getString("operation").equals("SEEK"))seeks.add(row);
        assertEquals(4,seeks.size());assertEquals("SUPERSEDED",seeks.get(1).getString("outcome"));assertEquals("UNATTRIBUTED",seeks.get(3).getString("outcome"));
        JSONObject callback=rows().stream().filter(x->x.optString("event").equals("seek_callback_unattributed")).findFirst().orElseThrow();assertFalse(callback.has("attempt_id"));assertEquals(-1,callback.getLong("achieved_ms"));
        state.seeking(9000,0);state.seekCallback(backend,0);state.allSeeksComplete(backend,0);assertEquals("COMPLETED",attempts().get(attempts().size()-1).getString("outcome"));
    }
    @Test public void aggregateWithoutPairedCallbackAndCancellationDoNotInventSuccessfulSeeks()throws Exception {
        open();state.seeking(2000,0);state.allSeeksComplete(backend,0);assertEquals("UNATTRIBUTED",attempts().get(attempts().size()-1).getString("outcome"));
        state.seeking(5000,0);state.close(PlaybackOutcome.CANCELLED);assertTrue(attempts().stream().anyMatch(x->x.optString("operation").equals("SEEK")&&x.optString("outcome").equals("CANCELLED")));assertFalse(attempts().stream().anyMatch(x->x.optString("outcome").equals("COMPLETED")));
    }
    @Test public void laterPlaybackSessionDoesNotRewriteCapturedAttemptSession()throws Exception {
        PlaybackAttempt open=open();Diagnostics.endPlayback("changed");Diagnostics.beginPlayback(Uri.parse("file:///new"));state.prepared(backend,0);
        JSONObject row=attempts().get(attempts().size()-1);assertEquals(open.playbackSession,row.getString("playback_session"));assertNotEquals(row.getString("session"),row.getString("playback_session"));
    }
    @Test public void actualPlayerSeekAndOutputCallbacksFeedTheExistingRecorder()throws Exception {
        Player player=mock(Player.class,CALLS_REAL_METHODS);IMediaPlayer nativeBackend=mock(IMediaPlayer.class);Player.Listener listener=mock(Player.Listener.class);
        ReflectionHelpers.setField(player,"playbackDiagnostics",state);ReflectionHelpers.setField(player,"mMediaPlayer",nativeBackend);ReflectionHelpers.setField(player,"mPlayerListener",listener);ReflectionHelpers.setField(player,"mCurrentState",5);
        PlaybackAttempt open=state.opening();state.bind(nativeBackend,open,0);player.seekTo(4500);verify(nativeBackend).seekTo(4500);player.onSeekComplete(nativeBackend);player.onAllSeekComplete(nativeBackend);verify(listener).onSeekStart(4500);verify(listener).onSeekComplete();
        assertTrue(attempts().stream().anyMatch(x->x.optString("operation").equals("SEEK")&&x.optString("outcome").equals("COMPLETED")));
        player.onInfo(nativeBackend,3,0);verify(listener).onPlaybackEstablished();assertTrue(attempts().stream().anyMatch(x->x.optString("boundary").equals("FIRST_OUTPUT")));
    }
    private void flush()throws Exception {
        for(String executor:List.of("IMPORTANT","WORK")){ThreadPoolExecutor work=ReflectionHelpers.getStaticField(Diagnostics.class,executor);work.submit(()->{}).get(10,TimeUnit.SECONDS);}
    }
    @Test public void productionDiagnosticZipContainsTypedEvidenceAndNoSourceOrExceptionMessageCanaries()throws Exception {
        open();state.prepared(backend,0);state.output(backend,0);Diagnostics.error("test_failed",new IOException("EXCEPTION_CANARY password=SECRET_CANARY"));flush();
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();Diagnostics.export(app,bytes);Map<String,String> entries=new HashMap<>();try(ZipInputStream zip=new ZipInputStream(new ByteArrayInputStream(bytes.toByteArray()))){ZipEntry entry;while((entry=zip.getNextEntry())!=null)entries.put(entry.getName(),new String(zip.readAllBytes(),StandardCharsets.UTF_8));}
        String exported=String.join("\n",entries.values());for(String canary:List.of("AUTH_CANARY","FILE_CANARY","QUERY_CANARY","EXCEPTION_CANARY","SECRET_CANARY"))assertFalse(canary,exported.contains(canary));
        assertTrue(exported.contains("playback_attempt"));assertTrue(exported.contains("measurement_quality"));assertTrue(exported.contains("CALLBACK"));assertTrue(entries.get("coverage.txt").contains("PARTIAL"));assertTrue(entries.get("README.txt").contains("Logging defaults to on"));assertTrue(Diagnostics.important("playback_attempt"));
    }
    @Test public void disabledLoggingProducesNoAttemptRecords(){Diagnostics.setEnabled(app,false);open();state.prepared(backend,0);state.output(backend,0);DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");assertEquals(0,recorder.bytes());}
}
