package com.archos.mediacenter.video.leanback;

import com.archos.mediacenter.video.browser.adapters.object.Video;
import java.util.Comparator;

/** Real indexed measurements only; unknown measurements sort after known values. */
public final class PreviewVariants {
    public static final Comparator<Video> BEST_FIRST=Comparator
        .comparingLong((Video v)->(long)Math.max(0,v.getMeasuredWidth())*Math.max(0,v.getMeasuredHeight())).reversed()
        .thenComparing(Comparator.comparingLong((Video v)->v.getDurationMs()>0?Math.max(0,v.getSize())/v.getDurationMs():0).reversed())
        .thenComparingLong(Video::getId);
    public static String label(Video video){
        int w=video.getMeasuredWidth(),h=video.getMeasuredHeight();
        String resolution=w>0&&h>0?w+" × "+h:"Resolution unavailable";
        return resolution+" · "+video.getFilenameNonCryptic();
    }
    private PreviewVariants(){}
}
