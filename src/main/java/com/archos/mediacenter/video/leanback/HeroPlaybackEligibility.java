package com.archos.mediacenter.video.leanback;

import android.content.Context;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Process-local successful-output exclusion. No Play click, failure or new persisted history. */
public final class HeroPlaybackEligibility {
    private static final Set<String> ESTABLISHED = ConcurrentHashMap.newKeySet();
    private static final AtomicLong GENERATION = new AtomicLong();
    private HeroPlaybackEligibility() { }
    public static String identity(Entry entry) {
        if(entry.media instanceof Tvshow) return entry.onlineId>0?"tv:"+entry.onlineId:entry.key();
        if(entry.media instanceof Movie) return entry.onlineId>0?"movie:"+entry.onlineId:entry.key();
        return entry.key();
    }
    public static boolean consumed(Entry entry) { return ESTABLISHED.contains(identity(entry)); }
    public static long generation() { return GENERATION.get(); }
    public static void established(Context context,String identity) {
        if(identity==null||!identity.matches("(?:movie:|tv:|v|s)[1-9][0-9]*")||com.archos.mediacenter.video.player.PrivateMode.isActive())return;
        if(ESTABLISHED.add(identity)){
            GENERATION.incrementAndGet();
            context.getApplicationContext().sendBroadcast(new android.content.Intent(context.getPackageName()+".VIEWING_STATE_CHANGED").setPackage(context.getPackageName()));
        }
    }
}
