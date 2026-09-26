package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediaprovider.video.NetworkAutoRefresh;
import com.archos.mediaprovider.video.NetworkScannerUtil;
import java.util.*;

/** Presentation for the existing scheduler and per-source rescan flags. */
public final class PreviewNetworkScanning {
    public static String lastResult(Context c){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(c);
        long time=prefs.getLong(NetworkAutoRefresh.AUTO_RESCAN_LAST_SCAN,0);
        if(time==0)return "No recorded network scan";
        int error=NetworkAutoRefresh.getLastError(c);
        String result=error==NetworkAutoRefresh.AUTO_RESCAN_ERROR_NO_WIFI?"Network unavailable":
            error==NetworkAutoRefresh.AUTO_RESCAN_ERROR_UNABLE_TO_REACH_HOST?"Source unreachable":"Scan recorded";
        return android.text.format.DateFormat.getDateFormat(c).format(new Date(time))+" · "+
            android.text.format.DateFormat.getTimeFormat(c).format(new Date(time))+"\n"+result;
    }
    public static void show(Context c){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(c);
        int period=NetworkAutoRefresh.getRescanPeriod(c);
        String[] labels={"Automatic Network Scanning: "+(period>0?"On":"Off"),
            "Frequency: "+Math.max(15,(period>0?period:prefs.getInt("preview_scan_frequency",3600000))/60000)+" minutes",
            "Scan When Supernova Opens or Returns: "+(prefs.getBoolean("auto_rescan_on_app_restart",true)?"On":"Off"),
            "Sources Included","Last Scan / Result","Scan Network Sources Now"};
        PreviewDialog.choose(c,"Network Scanning",labels,0,n->{
            switch(n){
                case 0:if(period>0)prefs.edit().putInt("preview_scan_frequency",period).apply();
                    NetworkScannerUtil.scheduleNewRescan(c,0,period>0?0:prefs.getInt("preview_scan_frequency",3600000),true);show(c);break;
                case 1:PreviewDialog.choose(c,"Scan Frequency",new String[]{"15 minutes","30 minutes","1 hour","6 hours","24 hours"},-1,i->{int value=new int[]{900000,1800000,3600000,21600000,86400000}[i];prefs.edit().putInt("preview_scan_frequency",value).apply();if(period>0)NetworkScannerUtil.scheduleNewRescan(c,0,value,true);show(c);});break;
                case 2:prefs.edit().putBoolean("auto_rescan_on_app_restart",!prefs.getBoolean("auto_rescan_on_app_restart",true)).apply();show(c);break;
                case 3:sources(c);break;
                case 4:PreviewDialog.read(c,"Last Scan / Result",lastResult(c));break;
                case 5:PreviewLibraryScan.requestNetwork(c);break;
            }
        });
    }
    public static void sources(Context c){
        List<String> names=new ArrayList<>();List<Long> ids=new ArrayList<>();Set<Integer> checked=new HashSet<>();
        try(Cursor cursor=ShortcutDbAdapter.VIDEO.getAllShortcuts(c,null,null)){
            if(cursor!=null)while(cursor.moveToNext()){if(cursor.getInt(5)==1)checked.add(names.size());names.add(cursor.getString(3));ids.add(cursor.getLong(0));}
        }
        if(names.isEmpty()){PreviewDialog.read(c,"Sources Included","Add a Library Source before choosing sources for automatic scanning.");return;}
        android.app.Dialog[] dialog={null};dialog[0]=PreviewDialog.choose(c,"Sources Included",names.toArray(new String[0]),0,checked,false,n->{boolean include=!checked.contains(n);ShortcutDbAdapter.VIDEO.setRescanShortcutById(c,include,ids.get(n));if(include)checked.add(n);else checked.remove(n);PreviewDialog.updateChecks(dialog[0],checked);});
    }
}
