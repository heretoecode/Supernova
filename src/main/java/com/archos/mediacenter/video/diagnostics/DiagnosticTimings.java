package com.archos.mediacenter.video.diagnostics;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The diagnostics upgrade (BRIEF_0158_B › Diagnostics upgrade; owner, 20 Nov 2026): start-up time, page switches
 * inside the top bar and the NOVA pieces that still run, so 0.159's clean-out has real "before" numbers.
 */
public final class DiagnosticTimings
{
    private static boolean startupReported;
    private static volatile long warmStartAt;
    private static final Set<String> LEGACY_SEEN = Collections.synchronizedSet(new HashSet<>());

    private DiagnosticTimings()
    {
    }

    /** A warm start: the process was already running when the main screen was created again. */
    static void mainScreenCreated()
    {
        if (startupReported)
            warmStartAt = SystemClock.uptimeMillis();
    }

    /** Home is drawn (the start-up surface goes): launch to first frame, then to the first usable focus. */
    public static void homeReady(View page)
    {
        long now = SystemClock.uptimeMillis();
        boolean cold = !startupReported;
        long started = cold ? android.os.Process.getStartUptimeMillis() : warmStartAt;
        startupReported = true;
        if (started <= 0)
            return;
        Diagnostics.event("startup_ready", "start", cold ? "cold" : "warm", "first_frame_ms", now - started);
        if (page == null)
            return;
        View focused = page.findFocus();
        if (focused != null)
        {
            Diagnostics.event("startup_focus_ready", "start", cold ? "cold" : "warm", "first_focus_ms", now - started);
            return;
        }
        ViewTreeObserver.OnGlobalFocusChangeListener[] holder = {null};
        holder[0] = (from, to) -> {
            if (to == null)
                return;
            page.getViewTreeObserver().removeOnGlobalFocusChangeListener(holder[0]);
            Diagnostics.event("startup_focus_ready", "start", cold ? "cold" : "warm", "first_focus_ms",
                              SystemClock.uptimeMillis() - started);
        };
        page.getViewTreeObserver().addOnGlobalFocusChangeListener(holder[0]);
    }

    /** Home, Movies, TV Shows and own pages share one screen: each switch is timed to the page drawn. */
    public static void pageSwitch(View page, String from, String to)
    {
        if (page == null)
            return;
        long started = SystemClock.uptimeMillis();
        ViewTreeObserver.OnPreDrawListener[] holder = {null};
        holder[0] = () -> {
            page.getViewTreeObserver().removeOnPreDrawListener(holder[0]);
            page.post(() -> Diagnostics.event("page_switch", "from", from, "to", to, "drawn_ms",
                                              SystemClock.uptimeMillis() - started));
            return true;
        };
        page.getViewTreeObserver().addOnPreDrawListener(holder[0]);
        page.invalidate();
    }

    /**
     * A NOVA screen or piece that still starts (activities and fragments outside Supernova's own pages), once per
     * process with the page it ran on, so the clean-out works from a list.
     */
    public static void componentStarted(Object component, String page)
    {
        if (component == null)
            return;
        String name = component.getClass().getName();
        if (!legacy(name) || !LEGACY_SEEN.add(name + "@" + page))
            return;
        Diagnostics.event("legacy_component_started", "component", component.getClass().getSimpleName(), "kind",
                          component instanceof Activity ? "activity" : "fragment", "page", page);
    }

    /** NOVA's own classes: in com.archos but not one of Supernova's pages, panels or hosts. */
    static boolean legacy(String className)
    {
        if (!className.startsWith("com.archos."))
            return false;
        String simple = className.substring(className.lastIndexOf('.') + 1);
        if (simple.startsWith("Preview") || simple.startsWith("Supernova") || simple.startsWith("Foundation"))
            return false;
        for (String ours : new String[] {".leanback.files.", ".leanback.settings.Settings", ".leanback.search.",
                                         ".diagnostics.", ".streaming."})
            if (className.contains(ours))
                return false;
        return true;
    }
}
