package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewScanProgressTest {
    @Test public void unstartedAndCoalescedSourcesReleaseTheirProgressSlots(){
        PreviewScanProgress state=new PreviewScanProgress();
        state.accept("1","a","queued",0,0,0);state.accept("1","b","queued",0,0,0);
        state.accept("1","a","coalesced",0,0,0);state.accept("1","b","not_started",0,0,0);
        state.accept("1","b","metadata_skipped",0,0,0);state.accept("1","b","batch_failed",0,0,0);
        assertEquals(2,state.total(3));assertEquals(1,state.total(4));assertEquals(0,state.total(0));
    }
    @Test public void sourceCountsAccumulateWithoutCountingRepeatedCompletionTwice(){
        PreviewScanProgress state=new PreviewScanProgress();
        assertTrue(state.accept("1","a","complete",10,2,3));
        assertFalse(state.accept("1","b","started",0,0,0));
        assertEquals(12,state.liveChecked("b",2));
        state.accept("1","b","reconciled",5,1,1);state.accept("1","b","complete",5,1,1);state.accept("1","b","complete",5,1,1);
        assertEquals(15,state.total(0));assertEquals(3,state.total(1));assertEquals(4,state.total(2));assertEquals(2,state.total(3));
        state.accept("1","b","failed",5,1,1);assertEquals(2,state.total(3));assertEquals(1,state.total(4));
        state.accept("1","a","complete",10,2,3);assertEquals(1,state.total(4));
    }
    @Test public void newBatchResetsPreviousCountersAndNegativeCountsAreNotPresented(){
        PreviewScanProgress state=new PreviewScanProgress();state.accept("1","a","complete",10,2,3);
        assertTrue(state.accept("2","a","started",-1,-1,-1));assertEquals(0,state.total(0));assertEquals(0,state.total(1));assertEquals(0,state.total(3));
        state.clear();assertTrue(state.accept("2","a","started",0,0,0));
    }
}
