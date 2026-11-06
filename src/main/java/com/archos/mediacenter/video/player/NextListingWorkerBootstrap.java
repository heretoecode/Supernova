package com.archos.mediacenter.video.player;

import android.content.Context;

/** Minimal context setup for the private listing process; no library scans, playback or shared discovery there. */
public final class NextListingWorkerBootstrap
{
    public static boolean install(Context context)
    {
        return NextListingProcess.bootstrap(context);
    }

    private NextListingWorkerBootstrap()
    {
    }
}
