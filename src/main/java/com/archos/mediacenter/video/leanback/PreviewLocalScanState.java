package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.Intent;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.util.Arrays;

/** Native importer evidence, independent of which Preview page is attached. */
final class PreviewLocalScanState {
    private static String summary="";
    private static final java.util.Map<String,State> active=new java.util.LinkedHashMap<>();
    private static final java.util.LinkedHashSet<String> ended=new java.util.LinkedHashSet<>();
    private static final class State {final String operation=Diagnostics.operation("local_import");final long started=android.os.SystemClock.elapsedRealtime();}
    static synchronized void accept(Context context,Intent intent){
        String id=intent.getStringExtra("batch_id"),phase=intent.getStringExtra("phase"),mode=intent.getStringExtra("mode");
        if(id==null||!id.matches("[A-Za-z0-9-]{1,100}")||!Arrays.asList("full","incremental").contains(mode)
            ||!Arrays.asList("requested","queued","started","indexing","reconciled","metadata_queued","metadata_failed","metadata_skipped","complete","partial","failed","cancelled").contains(phase)||ended.contains(id))return;
        State state=active.get(id);if(state==null){
            if(active.size()>=32){String oldest=active.keySet().iterator().next();State stale=active.remove(oldest);Diagnostics.event("scan_trace_evicted","operation_id",stale.operation,"batch_id",oldest);}
            state=new State();active.put(id,state);
        }
        long checked=Math.max(0,intent.getLongExtra("checked",0)),added=Math.max(0,intent.getLongExtra("added",0)),updated=Math.max(0,intent.getLongExtra("updated",0)),elapsed=Math.max(0,intent.getLongExtra("elapsed_ms",0));
        boolean known=intent.getBooleanExtra("counts_known",false);
        String trigger=intent.getStringExtra("trigger");if(!Arrays.asList("startup","resume","content_change","android_scan").contains(trigger))trigger="native_import";
        Diagnostics.event("scan_"+phase,"operation_id",state.operation,"batch_id",id,"domain","local_import","trigger",trigger,"mode",mode,
            "count_kind","technical_rows_checked","checked",checked,"new",added,"updated",updated,"counts_known",known,"elapsed_ms",elapsed);
        summary="Local library · "+phase.replace('_',' ')+"\n"+checked+" media rows checked"+(known?" · "+added+" new · "+updated+" updated":" · new/updated counts unavailable")+" · "+elapsed/1000+" seconds";
        if(Arrays.asList("complete","partial","failed","cancelled").contains(phase)){
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString("preview_local_scan_result",summary).apply();
            Diagnostics.finishOperation(state.operation,"local_import",state.started);active.remove(id);ended.add(id);if(ended.size()>64)ended.remove(ended.iterator().next());
        }
    }
    static synchronized String status(Context context){return summary.isEmpty()?androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("preview_local_scan_result",""):summary;}
    private PreviewLocalScanState(){}
}
