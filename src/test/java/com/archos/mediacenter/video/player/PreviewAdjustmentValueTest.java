package com.archos.mediacenter.video.player;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewAdjustmentValueTest {
    @Test public void valuesUseExplicitSignsUnitsAndUnambiguousSpeed(){
        assertEquals("1.00×",PreviewAdjustmentValue.speed(1f));assertEquals("0.75×",PreviewAdjustmentValue.speed(.75f));
        assertEquals("0 ms",PreviewAdjustmentValue.delay(0));assertEquals("+250 ms",PreviewAdjustmentValue.delay(250));
        assertEquals("−500 ms",PreviewAdjustmentValue.delay(-500));assertEquals("+1.5 s",PreviewAdjustmentValue.delay(1500));
        assertEquals("−1.05 s",PreviewAdjustmentValue.delay(-1050));assertEquals("−2147483.648 s",PreviewAdjustmentValue.delay(Integer.MIN_VALUE));
    }
}
