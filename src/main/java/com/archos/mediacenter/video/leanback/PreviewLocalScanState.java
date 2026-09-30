package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.Intent;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.util.Arrays;

/** Native importer evidence, independent of which Preview page is attached. */
final class PreviewLocalScanState {
    private static String batch="",operation="",summary="";
    private static long started;
    static synchronized void accept(Context context,Intent intent){
        String id=intent.getStringExtra("batch_id"),phase=intent.getStringExtra("phase"),mode=intent.getStringExtra("mode");
        if(id==null||!id.matches("[A-Za-z0-9-]{1,100}")||!Arrays.asList("full","incremental").contains(mode)
            ||!Arrays.asList("started","indexing","reconciled","metadata_queued","metadata_failed","metadata_skipped","complete","partial","failed","cancelled").contains(phase))return;
        if(!id.equals(batch)){batch=id;operation=Diagnostics.operation("local_import");started=android.os.SystemClock.elapsedRealtime();}
        long checked=Math.max(0,intent.getLongExtra("checked",0)),added=Math.max(0,intent.getLongExtra("added",0)),updated=Math.max(0,intent.getLongExtra("updated",0)),elapsed=Math.max(0,intent.getLongExtra("elapsed_ms",0));
        boolean known=intent.getBooleanExtra("counts_known",false);
        Diagnostics.event("scan_"+phase,"operation_id",operation,"batch_id",batch,"domain","local_import","trigger","native_import","mode",mode,
            "count_kind","technical_rows_checked","checked",checked,"new",added,"updated",updated,"counts_known",known,"elapsed_ms",elapsed);
        summary="Local library · "+phase.replace('_',' ')+"\n"+checked+" media rows checked"+(known?" · "+added+" new · "+updated+" updated":" · new/updated counts unavailable")+" · "+elapsed/1000+" seconds";
        if(Arrays.asList("complete","partial","failed","cancelled").contains(phase)){
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString("preview_local_scan_result",summary).apply();
            Diagnostics.finishOperation(operation,"local_import",started);
        }
    }
    static synchronized String status(Context context){return summary.isEmpty()?androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("preview_local_scan_result",""):summary;}
    private PreviewLocalScanState(){}
}
