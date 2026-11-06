package com.archos.mediacenter.video.scanning;

import java.util.List;

/** Cadence values in the existing millisecond preference. A tie migrates to the slower cadence. */
public final class AutomaticScanChoices
{
    public static final int HOUR = 60 * 60 * 1000;
    public static final List<Integer> PERIODS = List.of(0, HOUR, 2 * HOUR, 6 * HOUR, 24 * HOUR);

    public static int nearest(int oldPeriod)
    {
        if (oldPeriod <= 0)
            return 0;
        int best = HOUR;
        for (int period : PERIODS)
        {
            if (period > 0 && Math.abs((long)oldPeriod - period) <= Math.abs((long)oldPeriod - best))
                best = period;
        }
        return best;
    }

    private AutomaticScanChoices()
    {
    }
}
