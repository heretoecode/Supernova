package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** The report's health summary: memory per page and the other measures, over-limit lines first. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class DiagnosticHealthSummaryTest
{
    @Test public void overLimitLinesComeFirstWithMemoryPerPage() throws Exception
    {
        String text = DiagnosticHealthSummary.build(Arrays.asList(
            new JSONObject().put("event", "performance_trend").put("page", "player").put("pss_kb", 640 * 1024),
            new JSONObject().put("event", "performance_trend").put("page", "home").put("pss_kb", 380 * 1024),
            new JSONObject().put("event", "startup_ready").put("start", "cold").put("first_frame_ms", 2100),
            new JSONObject().put("event", "page_switch").put("from", "home").put("to", "tv").put("drawn_ms", 180),
            new JSONObject().put("event", "legacy_component_started").put("component", "MainFragment")
                .put("page", "MainActivityLeanback")));
        int player = text.indexOf("OVER  Memory on player: peak 640 MB");
        int home = text.indexOf("ok    Memory on home: peak 380 MB");
        assertTrue(text, player > 0 && home > player);
        assertTrue(text.contains("OVER  NOVA pieces that ran: 1: MainFragment (MainActivityLeanback)"));
        assertTrue(text.contains("ok    Cold start to Home: slowest 2100 ms"));
        assertTrue(text.contains("ok    Slowest page switch: home → tv, 180 ms"));
    }

    @Test public void onlyNovaClassesCountAsLeftovers()
    {
        assertTrue(DiagnosticTimings.legacy("com.archos.mediacenter.video.leanback.MainFragment"));
        assertTrue(!DiagnosticTimings.legacy("com.archos.mediacenter.video.leanback.PreviewPages"));
        assertTrue(!DiagnosticTimings.legacy("androidx.fragment.app.Fragment"));
    }
}
