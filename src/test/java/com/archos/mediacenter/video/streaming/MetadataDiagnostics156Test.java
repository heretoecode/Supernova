package com.archos.mediacenter.video.streaming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Application;
import android.os.SystemClock;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.io.IOException;
import java.lang.reflect.Method;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class MetadataDiagnostics156Test
{
    @Test public void semanticFailureKeepsStatusSizeFirstParseReasonAndSanitizedCategory() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        try
        {
            try
            {
                StreamingRepository.parseMetadata("movie/42", "{}", 206, 2, "fixture");
                fail("Empty movie payload accepted");
            }
            catch (IOException expected)
            {
            }
            Object flight = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            Method snapshot = flight.getClass().getDeclaredMethod("snapshot", long.class);
            snapshot.setAccessible(true);
            String text = (String)snapshot.invoke(flight, SystemClock.elapsedRealtime());
            boolean found = false;
            for (String line : text.split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject record = new JSONObject(line);
                if (!record.optString("event").equals("metadata_semantic_failed"))
                    continue;
                found = true;
                assertEquals("movie/id", record.getString("endpoint_category"));
                assertEquals(206, record.getInt("http_status"));
                assertEquals(2, record.getInt("body_bytes"));
                assertTrue(record.getBoolean("suspicious_small_body"));
                assertFalse(record.getString("first_parse_error").isEmpty());
                assertTrue(record.getString("trace").contains("IOException"));
            }
            assertTrue(found);
            assertEquals("movie/id/external_ids", StreamingRepository.endpointCategory("/3/movie/42/external_ids"));
        }
        finally
        {
            Diagnostics.setEnabled(app, false);
        }
    }
}
