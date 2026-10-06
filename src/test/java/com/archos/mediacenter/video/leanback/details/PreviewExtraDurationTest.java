package com.archos.mediacenter.video.leanback.details;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewExtraDurationTest {
    @Test public void formatsActualSecondsWithoutInventingUnknownDuration(){
        assertEquals("",PreviewExtraDuration.label(0));assertEquals("",PreviewExtraDuration.label(-1));
        assertEquals("2:15",PreviewExtraDuration.label(135));assertEquals("1:01:01",PreviewExtraDuration.label(3661));
    }
}
