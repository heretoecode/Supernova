package com.archos.mediacenter.video.foundation;

/** Literal APK counter, deliberately separate from NOVA's semantic/date version. */
public final class FoundationVersion {
    private FoundationVersion() {}
    public static int[] array(String version) {
        if(version==null || !version.matches("0\\.[1-9][0-9]*"))throw new IllegalArgumentException("Malformed Foundation counter");
        return new int[]{0,Integer.parseInt(version.substring(2)),0,0,0,0,0,0};
    }
}
