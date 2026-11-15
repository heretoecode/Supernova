package com.archos.mediacenter.video.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.content.Context;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class AgeCertificatesBTest
{
    @Test public void gbTheatricalCertificateWinsAndReleaseNoteIsNotAdvice() throws Exception
    {
        JSONObject data = new JSONObject("{results:[{iso_3166_1:'US',rating:'R'},"
                + "{iso_3166_1:'GB',release_dates:[{type:4,certification:'15'},"
                + "{type:3,certification:'12A',note:'Premiere'}]}]}");
        var value = AgeCertificates.parse(data, false);
        assertEquals("12A", value.rating());
        assertEquals("", value.reasons());
        assertNull(AgeCertificates.parse(new JSONObject("{results:[{iso_3166_1:'US',rating:'R'}]}"), true));
    }

    @Test public void offlineTitleAdviceSurvivesLocaleChangeAndEpisodeAdviceOverridesSeries() throws Exception
    {
        Context app = RuntimeEnvironment.getApplication();
        JSONObject data = new JSONObject("{id:22,results:[{iso_3166_1:'GB',rating:'15',"
                + "descriptors:['strong language']}]} ");
        AgeCertificates.remember(app, "tv", 22, data);
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            var fallback = AgeCertificates.read(app, "tv", 22, 1, 2);
            assertTrue(fallback.seriesFallback());
            assertEquals("strong language", fallback.reasons());
            AgeCertificates.verifiedAdvice(app, "tv", 22, 1, 2,
                    new AgeCertificates.Certificate("12", "moderate violence", false));
            var episode = AgeCertificates.read(app, "tv", 22, 1, 2);
            assertFalse(episode.seriesFallback());
            assertEquals("12", episode.rating());
            assertEquals("15", AgeCertificates.read(app, "tv", 22, 1, 3).rating());
            assertNull(AgeCertificates.read(app, "movie", 22, -1, -1));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test public void refreshCannotImportOtherTitleOrEraseKnownAdviceWithAnEmptyDescriptor() throws Exception
    {
        Context app = RuntimeEnvironment.getApplication();
        AgeCertificates.verifiedAdvice(app, "movie", 7, -1, -1,
                new AgeCertificates.Certificate("15", "strong violence", false));
        JSONObject correct = new JSONObject("{id:7,results:[{iso_3166_1:'GB',"
                + "release_dates:[{type:3,certification:'15'}]}]}");
        AgeCertificates.remember(app, "movie", 7, correct);
        assertEquals("strong violence", AgeCertificates.read(app, "movie", 7, -1, -1).reasons());
        assertThrows(IllegalArgumentException.class, () -> AgeCertificates.remember(app, "movie", 8, correct));
        assertNull(AgeCertificates.read(app, "movie", 8, -1, -1));
        AgeCertificates.remember(app, "movie", 7, new JSONObject().put("id", 7).put("results", new JSONArray()));
        assertNull(AgeCertificates.read(app, "movie", 7, -1, -1));
    }
}
