package com.archos.mediacenter.video.player;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewSeekPolicyTest {
    @Test public void exactPressGroupsAndMaximum(){PreviewSeekPolicy p=new PreviewSeekPolicy();int[] expected={10000,10000,10000,30000,30000,30000,60000,60000,60000,120000,120000};for(int n=0;n<expected.length;n++)assertEquals(expected[n],p.next(1,n*200));}
    @Test public void pauseResetsAcceleration(){PreviewSeekPolicy p=new PreviewSeekPolicy();for(int n=0;n<5;n++)p.next(1,n*100);assertEquals(10000,p.next(1,1650));}
    @Test public void directionChangeResetsAcceleration(){PreviewSeekPolicy p=new PreviewSeekPolicy();for(int n=0;n<8;n++)p.next(1,n*100);assertEquals(-10000,p.next(-1,800));}
    @Test public void shortPauseKeepsAcceleration(){PreviewSeekPolicy p=new PreviewSeekPolicy();p.next(1,0);p.next(1,100);p.next(1,200);assertEquals(30000,p.next(1,1400));}
    @Test public void boundsDoNotOverflowOrPassEnd(){assertEquals(0,PreviewSeekPolicy.position(5000,-10000,100000));assertEquals(98000,PreviewSeekPolicy.position(95000,120000,100000));assertEquals(Integer.MAX_VALUE-2000,PreviewSeekPolicy.position(Integer.MAX_VALUE-3000,120000,Integer.MAX_VALUE));}
    @Test public void explicitResetStartsNewPlaybackSequence(){PreviewSeekPolicy p=new PreviewSeekPolicy();for(int n=0;n<8;n++)p.next(1,n*100);p.reset();assertEquals(10000,p.next(1,800));}
}
