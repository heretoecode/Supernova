package com.archos.mediacenter.video.leanback;

import java.util.*;

/** Per-batch source counters: repeated lifecycle notifications never double-count a source. */
final class PreviewScanProgress {
    private final Map<String,long[]> sources=new HashMap<>();
    String batch="";
    boolean accept(String batch,String source,String phase,int checked,int added,int updated){
        boolean fresh=!Objects.equals(this.batch,batch);
        if(fresh){sources.clear();this.batch=batch;}
        long[] previous=sources.get(source);
        long terminal="complete".equals(phase)||"failed".equals(phase)||"coalesced".equals(phase)||"not_started".equals(phase)?1:previous==null?0:previous[3];
        long failure="failed".equals(phase)||"partial".equals(phase)||"not_started".equals(phase)?1:previous==null?0:previous[4];
        sources.put(source,new long[]{Math.max(0,checked),Math.max(0,added),Math.max(0,updated),terminal,failure});
        return fresh;
    }
    long total(int column){long count=0;for(long[] source:sources.values())count+=source[column];return count;}
    long liveChecked(String source,long current){long[] value=sources.get(source);return total(0)-(value==null?0:value[0])+Math.max(0,current);}
    void clear(){batch="";sources.clear();}
}
