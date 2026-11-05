package com.archos.mediacenter.video.player;

import java.util.HashSet;
import java.util.Set;

/** Independent presentation reasons compose without losing the selected subtitle track. */
final class SubtitlePresentationGate
{
    private final Set<String> owners = new HashSet<>();

    synchronized void set(String owner, boolean suppressed)
    {
        if (suppressed)
        {
            owners.add(owner);
        }
        else
        {
            owners.remove(owner);
        }
    }

    synchronized int track(int selected, int noneIndex)
    {
        return owners.isEmpty() ? selected : noneIndex;
    }
}
