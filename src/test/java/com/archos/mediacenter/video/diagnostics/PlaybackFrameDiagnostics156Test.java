package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.os.SystemClock;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PlaybackFrameDiagnostics156Test
{
    @Test public void seekFrameRequiresPairedCallbackAndCurrentBackendAndIsEmittedOnlyOnce() throws Exception
    {
        Diagnostics.setEnabled(RuntimeEnvironment.getApplication(), true);
        Diagnostics.clearAll(RuntimeEnvironment.getApplication());
        try
        {
            PlaybackDiagnosticState state = new PlaybackDiagnosticState();
            Object backend = new Object();
            Diagnostics.PlaybackAttempt open = state.opening();
            state.bind(backend, open, 0);
            state.seeking(5000, 0);
            state.frame(backend, 0);
            state.seekCallback(backend, 0);
            state.allSeeksComplete(backend, 0);
            state.frame(new Object(), 0);
            state.frame(backend, 0);
            state.frame(backend, 0);
            state.seeking(10000, 0);
            state.seeking(15000, 0);
            state.seekCallback(backend, 0);
            state.allSeeksComplete(backend, 0);
            state.frame(backend, 0);
            DiagnosticFlightRecorder flight = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            int found = 0;
            for (String line : flight.snapshot(SystemClock.elapsedRealtime()).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject record = new JSONObject(line);
                if (!record.optString("event").equals("seek_first_frame"))
                    continue;
                found++;
                assertEquals(5000, record.getInt("target_ms"));
                assertEquals("PROXY", record.getString("measurement_quality"));
            }
            assertEquals(1, found);
        }
        finally
        {
            Diagnostics.setEnabled(RuntimeEnvironment.getApplication(), false);
        }
    }
}
