package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;

/** Persist terminal scan evidence, never infer a completion timestamp from page refresh. */
final class LibraryScanRecord {
    static void record(Context context,String domain,String phase){
        SharedPreferences.Editor edit=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit();
        if(phase.equals("complete")||phase.equals("batch_complete"))edit.putLong("library_scan_completed_"+domain,System.currentTimeMillis());
        if(phase.equals("complete")||phase.equals("batch_complete")||phase.equals("partial")||phase.equals("failed")||phase.equals("batch_failed")||phase.equals("cancelled"))edit.putString("library_scan_terminal_"+domain,phase);
        edit.apply();
    }
    static long completed(Context c){SharedPreferences p=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);return Math.max(p.getLong("library_scan_completed_local",0),p.getLong("library_scan_completed_network",0));}
    static boolean problems(Context c){SharedPreferences p=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);for(String domain:new String[]{"local","network"}){String phase=p.getString("library_scan_terminal_"+domain,"");if(phase.equals("partial")||phase.equals("failed")||phase.equals("batch_failed"))return true;}return false;}
    private LibraryScanRecord(){}
}
