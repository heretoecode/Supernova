package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;

public class AutomaticScanChoicesTest
{
    @Test public void allSupportedChoicesRoundTrip()
    {
        assertEquals(List.of(0, 3600000, 7200000, 21600000, 86400000), AutomaticScanChoices.PERIODS);
        for (int value : AutomaticScanChoices.PERIODS)
            assertEquals(value, AutomaticScanChoices.nearest(value));
    }

    @Test public void twiceDailyBecomesSixHourlyAndTiesChooseSlower()
    {
        assertEquals(6 * AutomaticScanChoices.HOUR, AutomaticScanChoices.nearest(12 * AutomaticScanChoices.HOUR));
        assertEquals(2 * AutomaticScanChoices.HOUR, AutomaticScanChoices.nearest(90 * 60000));
        assertEquals(0, AutomaticScanChoices.nearest(-1));
        assertEquals(AutomaticScanChoices.HOUR, AutomaticScanChoices.nearest(15 * 60000));
        assertEquals(24 * AutomaticScanChoices.HOUR, AutomaticScanChoices.nearest(Integer.MAX_VALUE));
    }
}
