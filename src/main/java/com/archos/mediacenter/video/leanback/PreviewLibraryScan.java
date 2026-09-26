package com.archos.mediacenter.video.leanback;

import android.content.*;
import android.media.MediaScannerConnection;
import android.os.*;
import com.archos.filecorelibrary.ExtStorageManager;
import com.archos.mediaprovider.video.*;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.util.*;

/** One manual/foreground scheduler entry, with lifecycle independent of any particular page. */
public final class PreviewLibraryScan {
    private static long requestedAt;
    private static boolean installed;
    private static String operation = "", phase = "";
    private static int checked, added, updated, completed;
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    public static synchronized void install(Context context) {
        if(installed)return;installed=true;Context app=context.getApplicationContext();
        BroadcastReceiver receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent intent){
            String next=intent.getStringExtra("phase");if(next==null)return;
            synchronized(PreviewLibraryScan.class){
                if(operation.isEmpty()){operation=Diagnostics.operation("library_scan");requestedAt=SystemClock.elapsedRealtime();}
                phase=next;checked=intent.getIntExtra("checked",checked);added=intent.getIntExtra("added",added);updated=intent.getIntExtra("updated",updated);
                if(next.equals("complete")||next.equals("failed"))completed++;
                Diagnostics.event("scan_"+next,"operation_id",operation,"source_id",intent.getStringExtra("source_id"),"checked",checked,"new",added,"updated",updated,"sources_completed",completed,"elapsed_ms",SystemClock.elapsedRealtime()-requestedAt);
                if(next.equals("complete")||next.equals("failed"))androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putString("preview_scan_result",phase+" · "+checked+" checked · "+added+" new · "+updated+" updated").putLong("preview_scan_result_time",System.currentTimeMillis()).apply();
            }
        }};
        IntentFilter filter=new IntentFilter(app.getPackageName()+".SCAN_LIFECYCLE");
        if(Build.VERSION.SDK_INT>=33)app.registerReceiver(receiver,filter,Context.RECEIVER_NOT_EXPORTED);else app.registerReceiver(receiver,filter);
    }
    public static synchronized void request(Context context){
        install(context);Context app=context.getApplicationContext();
        LinkedHashSet<String> roots=new LinkedHashSet<>();roots.add(Environment.getExternalStorageDirectory().getAbsolutePath());ExtStorageManager storage=ExtStorageManager.getExtStorageManager();if(storage.hasExtStorage()){roots.addAll(storage.getExtSdcards());roots.addAll(storage.getExtUsbStorages());roots.addAll(storage.getExtOtherStorages());}
        MediaScannerConnection.scanFile(app,roots.toArray(new String[0]),null,null);
        requestNetwork(app);
    }
    public static synchronized void requestNetwork(Context context){
        install(context);
        // Do not confuse metadata/import activity with an active network traversal.
        boolean busy=NetworkScannerServiceVideo.isScannerAlive()||com.archos.mediascraper.AutoScrapeService.getNetworkScanCount()>0;
        if(busy){Diagnostics.event("scan_request_coalesced","operation_id",operation,"trigger","manual");return;}
        requestedAt=SystemClock.elapsedRealtime();operation=Diagnostics.operation("library_scan");phase="queued";checked=added=updated=completed=0;
        Diagnostics.event("scan_requested","operation_id",operation,"trigger","manual","scheduler","indexed_sources");
        NetworkAutoRefresh.forceRescan(context.getApplicationContext());
        final String request=operation;
        MAIN.postDelayed(()->{synchronized(PreviewLibraryScan.class){if(request.equals(operation)&&phase.equals("queued")){phase="not_started";Diagnostics.event("scan_not_started","operation_id",operation,"scheduler_error",NetworkAutoRefresh.getLastError(context));}}},15000);
    }
    public static synchronized String status(Context c){
        if(NetworkScannerServiceVideo.isScannerAlive())return "Scanning indexed sources · "+NetworkScannerServiceVideo.getFilesFoundCount()+" checked\n"+added+" new · "+updated+" updated · "+completed+" sources completed\n"+Math.max(0,(SystemClock.elapsedRealtime()-requestedAt)/1000)+" seconds";
        if(phase.equals("queued"))return "Scan requested · waiting for the scanner";
        if(phase.equals("not_started"))return "The requested scan has not started. Check the source selection and connectivity.";
        android.content.SharedPreferences prefs=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);String result=prefs.getString("preview_scan_result","");
        return result.isEmpty()?PreviewNetworkScanning.lastResult(c):result;
    }
    private PreviewLibraryScan(){}
}
