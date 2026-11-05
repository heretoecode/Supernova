package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;

public class TrackPreferenceTest
{
    @Test public void rematchesDescriptionsWhenIndexesChangeAndKeepsLanguageAheadOfCodec()
    {
        TrackPreference desired = new TrackPreference("eng", "E-AC3", "5.1", false, false);
        assertEquals(2, desired.bestMatch(List.of(new TrackPreference("de", "EAC3", "5.1", false, false),
                                                  new TrackPreference("en", "AAC", "2.0", false, false),
                                                  new TrackPreference("en", "EAC3", "5.1", false, false))));
        assertEquals(-1, desired.bestMatch(List.of(new TrackPreference("fr", "EAC3", "5.1", false, false))));
    }

    @Test public void forcedChoicePrefersForcedTrackAndNoneNeverBecomesATrack()
    {
        TrackPreference forced = new TrackPreference("en", "srt", "", true, false);
        assertEquals(1, forced.bestMatch(List.of(new TrackPreference("en", "srt", "", false, false), forced)));
        assertEquals(-1, new TrackPreference("", "", "", false, true).bestMatch(List.of(forced)));
    }
}
