package com.archos.mediacenter.video.foundation;

import org.junit.Test;
import static org.junit.Assert.*;

public class FoundationVersionTest {
    @Test public void literalCountersNeverRoundOrBecomeNovaSemanticVersions() {
        assertEquals(9,FoundationVersion.array("0.9")[1]);
        assertEquals(10,FoundationVersion.array("0.10")[1]);
        assertEquals(133,FoundationVersion.array("0.133")[1]);
        assertEquals(0,FoundationVersion.array("0.133")[0]);
        assertEquals(8,FoundationVersion.array("0.133").length);
    }
    @Test public void invalidCountersFailWithoutSilentlyChangingIdentity() {
        for(String version:new String[]{"0.01","0.0","0.1.0","6.4.64",null}) {
            try { FoundationVersion.array(version);fail("Accepted invalid counter"); }catch(IllegalArgumentException expected){}
        }
    }
}
