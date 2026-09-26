package com.archos.mediacenter.video.player;

/** Display-only formatting; native picker bounds, increments and playback callbacks are unchanged. */
public final class PreviewAdjustmentValue {
    public static boolean enabled(android.content.Context context){return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean("try_new_ui",false);}
    public static String speed(float value){return String.format(java.util.Locale.ROOT,"%.2f×",value);}
    public static String delay(int milliseconds){
        if(milliseconds==0)return "0 ms";
        long magnitude=Math.abs((long)milliseconds);String sign=milliseconds<0?"−":"+";
        return magnitude<1000?sign+magnitude+" ms":sign+java.math.BigDecimal.valueOf(magnitude,3).stripTrailingZeros().toPlainString()+" s";
    }
    private PreviewAdjustmentValue(){}
}
