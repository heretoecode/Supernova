package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediaprovider.video.NetworkAutoRefresh;
import com.archos.mediaprovider.video.NetworkScannerUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** TV controls for the existing indexed-source scheduler. */
public final class PreviewNetworkScanning {
    public static void show(Context c) {
        SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(c);
        boolean automatic=p.getInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD,0)>0;
        boolean onReturn=p.getBoolean("auto_rescan_on_app_restart",true);
        PreviewDialog.choose(c,"Network Scanning",new String[]{
                "Automatic scanning: "+(automatic?"On":"Off"),
                "Scan interval", "Scan on app return: "+(onReturn?"On":"Off"),
                "Sources included", "Scan now"},0,n->{
            if(n==0){int period=automatic?0:p.getInt("preview_scan_last_period",900000);setPeriod(c,p,period);show(c);}
            else if(n==1){int[] periods={900000,1800000,3600000,21600000,86400000};PreviewDialog.choose(c,"Scan interval",new String[]{"15 minutes","30 minutes","1 hour","6 hours","24 hours"},-1,i->{setPeriod(c,p,periods[i]);show(c);});}
            else if(n==2){p.edit().putBoolean("auto_rescan_on_app_restart",!onReturn).apply();show(c);}
            else if(n==3)sources(c);
            else PreviewLibraryScan.request(c);
        });
    }
    private static void setPeriod(Context c,SharedPreferences p,int period){
        SharedPreferences.Editor edit=p.edit().putInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD,period);
        if(period>0)edit.putInt("preview_scan_last_period",period);edit.apply();
        NetworkScannerUtil.scheduleNewRescan(c,0,period,true);
    }
    private static void sources(Context c){
        List<String> names=new ArrayList<>();Set<Integer> selected=new HashSet<>();
        try(Cursor cursor=ShortcutDbAdapter.VIDEO.getAllShortcuts(c,null,null)){
            if(cursor!=null)while(cursor.moveToNext()){if(cursor.getInt(5)==1)selected.add(names.size());names.add(cursor.getString(3));}
        }
        if(names.isEmpty()){PreviewDialog.read(c,"Sources included","Add an indexed folder from Library Sources to include it in automatic scanning.");return;}
        final android.app.Dialog[] dialog=new android.app.Dialog[1];
        dialog[0]=PreviewDialog.choose(c,"Sources included",names.toArray(new String[0]),0,selected,false,n->{
            boolean enabled=!selected.contains(n);if(enabled)selected.add(n);else selected.remove(n);
            ShortcutDbAdapter.VIDEO.setRescanShortcut(c,enabled,names.get(n));PreviewDialog.updateChecks(dialog[0],selected);
        });
    }
    private PreviewNetworkScanning(){}
}
