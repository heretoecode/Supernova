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
    private static String operation = "", phase = "", trigger="unknown",sourceLocation="",sourceId="";
    private static long checked, added, updated, completed;
    private static final PreviewScanProgress progress=new PreviewScanProgress();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    public static synchronized void install(Context context) {
        if(installed)return;installed=true;Context app=context.getApplicationContext();
        BroadcastReceiver receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent intent){
            String next=intent.getStringExtra("phase"),batch=intent.getStringExtra("batch_id"),source=intent.getStringExtra("source_id");
            if(!Arrays.asList("started","reconciled","partial","complete","failed").contains(next)||batch==null||!batch.matches("[A-Za-z0-9-]{1,100}")||source==null||!source.matches("[a-f0-9]{24}"))return;
            synchronized(PreviewLibraryScan.class){
                boolean fresh=progress.accept(batch,source,next,intent.getIntExtra("checked",0),intent.getIntExtra("added",0),intent.getIntExtra("updated",0));
                if(operation.isEmpty()||fresh&&!phase.equals("queued")){operation=Diagnostics.operation("library_scan");requestedAt=SystemClock.elapsedRealtime();trigger="native_scheduler";}
                sourceId=source;String location=intent.getStringExtra("source_location");sourceLocation=location==null?"":location.replaceAll("[\\p{Cntrl}]","");
                if(sourceLocation.length()>180)sourceLocation=sourceLocation.substring(0,177)+"…";
                phase=next;checked=progress.total(0);added=progress.total(1);updated=progress.total(2);completed=progress.total(3);
                Diagnostics.event("scan_"+next,"operation_id",operation,"batch_id",batch,"trigger",trigger,"source_id",source,"checked",checked,"new",added,"updated",updated,"sources_completed",completed,"sources_failed",progress.total(4),"elapsed_ms",SystemClock.elapsedRealtime()-requestedAt);
                if(next.equals("complete")||next.equals("failed"))androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putString("preview_scan_result",(progress.total(4)>0?"Partial / failed sources":phase)+" · "+checked+" checked · "+added+" new · "+updated+" updated").putLong("preview_scan_result_time",System.currentTimeMillis()).apply();
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
    public static synchronized void requestNetwork(Context context){requestNetwork(context,"manual");}
    public static synchronized void requestNetwork(Context context,String requestedTrigger){
        install(context);
        String origin=Arrays.asList("manual","startup","resume","scheduled").contains(requestedTrigger)?requestedTrigger:"unknown";
        com.archos.mediacenter.video.streaming.putio.PutioSyncScheduler.request(context,origin);
        // Do not confuse metadata/import activity with an active network traversal.
        boolean busy=NetworkScannerServiceVideo.isScannerAlive()||com.archos.mediascraper.AutoScrapeService.getNetworkScanCount()>0;
        if(busy){Diagnostics.event("scan_request_coalesced","operation_id",operation,"trigger",origin);return;}
        requestedAt=SystemClock.elapsedRealtime();operation=Diagnostics.operation("library_scan");phase="queued";trigger=origin;checked=added=updated=completed=0;progress.clear();sourceLocation=sourceId="";
        Diagnostics.event("scan_requested","operation_id",operation,"trigger",trigger,"scheduler","indexed_sources");
        NetworkAutoRefresh.forceRescan(context.getApplicationContext());
        final String request=operation;
        MAIN.postDelayed(()->{synchronized(PreviewLibraryScan.class){if(request.equals(operation)&&phase.equals("queued")){phase="not_started";Diagnostics.event("scan_not_started","operation_id",operation,"scheduler_error",NetworkAutoRefresh.getLastError(context));}}},15000);
    }
    public static synchronized String status(Context c){
        if(NetworkScannerServiceVideo.isScannerAlive())return "Scanning indexed sources · "+phase+(sourceLocation.isEmpty()?"":"\n"+sourceLocation)+"\n"+(phase.equals("started")?progress.liveChecked(sourceId,NetworkScannerServiceVideo.getFilesFoundCount()):checked)+" checked · "+added+" new · "+updated+" updated\n"+completed+" sources completed · "+Math.max(0,(SystemClock.elapsedRealtime()-requestedAt)/1000)+" seconds";
        if(phase.equals("queued"))return "Scan requested · waiting for the scanner";
        if(phase.equals("not_started"))return "The requested scan has not started. Check the source selection and connectivity.";
        android.content.SharedPreferences prefs=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);String result=prefs.getString("preview_scan_result","");
        return result.isEmpty()?PreviewNetworkScanning.lastResult(c):result;
    }
    private PreviewLibraryScan(){}
}
