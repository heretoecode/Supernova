package com.archos.mediacenter.video.diagnostics;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ApplicationExitInfo;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import androidx.preference.PreferenceManager;
import com.archos.filecorelibrary.TransportReadObserver;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewPages;
import com.google.zxing.WriterException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Bounded structured instrumentation. Never pass user text, headers or credentials here. */
public final class Diagnostics
{
    public static final String LEVEL = "supernova_diagnostic_level";
    private static final AtomicLong DROPPED = new AtomicLong(), WRITE_ERRORS = new AtomicLong(),
                                    SEQUENCE = new AtomicLong();
    private static final DiagnosticFlightRecorder FLIGHT = new DiagnosticFlightRecorder(252 * 1024, 60000);
    private static String activeIncident;
    // Producer-time routing survives delayed writers and later manual/automatic windows.
    private static long captureUntil;
    private static String captureIncident;
    private static DiagnosticFlightRecorder.Capture captureFlight;
    private static final AtomicLong ROUTINE_REJECTED = new AtomicLong(), IMPORTANT_REJECTED = new AtomicLong();
    private static final Map<String, Long> incidentCounts = new HashMap<>();
    private static String incidentId = "";
    private static long storageGeneration;
    private static volatile long freezeUntil, mainAck = SystemClock.elapsedRealtime();
    private static volatile int foreground;
    private static final Set<String> COVERAGE = java.util.Collections.synchronizedSet(new TreeSet<>());
    private static final ScheduledExecutorService HEARTBEAT = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "SupernovaHeartbeat");
        t.setDaemon(true);
        return t;
    });
    private static boolean heartbeatInstalled;
    public static final String KEY = "supernova_diagnostic_logging";
    static final int LIMIT = 256 * 1024, ROTATIONS = 4;
    static final int STORAGE_LIMIT = 32 * 1024 * 1024;
    private static final AtomicLong RETENTION_PRUNED_BYTES = new AtomicLong();
    private static final Object LOCK = new Object();
    private static final Object EXPORT_LOCK = new Object();
    private static final ThreadPoolExecutor WORK =
        new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(8192),
                               r
                               -> {
                                   Thread t = new Thread(r, "SupernovaDiagnostics");
                                   t.setDaemon(true);
                                   return t;
                               },
                               (task, executor) -> {
                                   DROPPED.incrementAndGet();
                                   ROUTINE_REJECTED.incrementAndGet();
                                   countRejected(task);
                               });
    // Routine focus/artwork traffic cannot occupy the important-event queue.
    private static final ThreadPoolExecutor IMPORTANT =
        new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2048),
                               r
                               -> {
                                   Thread t = new Thread(r, "SupernovaImportantDiagnostics");
                                   t.setDaemon(true);
                                   return t;
                               },
                               (task, executor) -> {
                                   DROPPED.incrementAndGet();
                                   IMPORTANT_REJECTED.incrementAndGet();
                                   countRejected(task);
                               });
    private static final java.util.concurrent.ConcurrentHashMap<String, AtomicLong> DROPPED_TYPES =
        new java.util.concurrent.ConcurrentHashMap<>();

    private static final class NamedTask implements Runnable
    {
        private final String event;
        private final Runnable action;
        NamedTask(String event, Runnable action)
        {
            this.event = event;
            this.action = action;
        }
        @Override public void run()
        {
            action.run();
        }
    }

    private static void countRejected(Runnable task)
    {
        String name = task instanceof NamedTask ? ((NamedTask)task).event : "recorder_internal";
        DROPPED_TYPES.computeIfAbsent(name, ignored -> new AtomicLong()).incrementAndGet();
    }

    private static String droppedTypes()
    {
        Map<String, Long> counts = new TreeMap<>();
        DROPPED_TYPES.forEach((name, count) -> counts.put(name, count.get()));
        return new JSONObject(counts).toString();
    }

    private static volatile Context context;
    private static volatile boolean enabled;
    private static volatile String playback = "";
    private static volatile String appSession = "";
    private static final String PROCESS = UUID.randomUUID().toString();
    private static long focusAt;
    private static volatile String uiScreen = "unknown", uiFocus = "none", uiPage = "unknown", uiCategory = "none",
                                   uiMode = "unknown", uiSort = "unknown", uiFilters = "none";
    private static volatile long uiMedia;
    private static volatile String uiFilterDetails = "{}";
    private static volatile int uiModals;
    private static volatile String uiModalKinds = "none";

    /** Immutable failure-time state; worker queue delays must not change its meaning. */
    static final class UiSnapshot
    {
        final String screen, focus, page, category, mode, sort, filters, filterDetails, modalKinds, playbackSession,
            foregroundSession, operation;
        final long media, utc, elapsed;
        final int modals, foregroundCount;

        UiSnapshot()
        {
            synchronized (Diagnostics.class)
            {
                screen = uiScreen;
                focus = uiFocus;
                page = uiPage;
                category = uiCategory;
                mode = uiMode;
                sort = uiSort;
                filters = uiFilters;
                filterDetails = uiFilterDetails;
                modalKinds = uiModalKinds;
                media = uiMedia;
                utc = System.currentTimeMillis();
                elapsed = SystemClock.elapsedRealtime();
                modals = uiModals;
                foregroundCount = foreground;
                playbackSession = playback;
                foregroundSession = appSession;
                operation = lastOperation;
            }
        }
    }

    private static final Map<Activity, UiSnapshot> ACTIVITY_UI = new java.util.WeakHashMap<>();

    static synchronized void pauseUi(Activity activity)
    {
        ACTIVITY_UI.put(activity, new UiSnapshot());
        // A new activity without a Preview surface must not inherit the old page.
        uiState("unknown", "none", "unknown", "unknown", "none", 0);
        uiFocus = "none";
    }

    static synchronized void resumeUi(Activity activity)
    {
        UiSnapshot saved = ACTIVITY_UI.get(activity);
        if (saved != null && "unknown".equals(uiPage))
        {
            uiPage = saved.page;
            uiCategory = saved.category;
            uiMode = saved.mode;
            uiSort = saved.sort;
            uiFilters = saved.filters;
            uiFilterDetails = saved.filterDetails;
            uiMedia = saved.media;
        }
        uiScreen = activity.getClass().getSimpleName();
        uiFocus = viewId(activity.getCurrentFocus());
    }

    /** Only structural, developer-owned labels belong here; never titles, queries or paths. */
    public static synchronized void uiState(String page, String category, String mode, String sort, String filters,
                                            long media)
    {
        uiPage = uiLabel(page);
        uiCategory = uiLabel(category);
        uiMode = uiLabel(mode);
        uiSort = uiLabel(sort);
        uiFilters = uiLabel(filters);
        uiFilterDetails = "{}";
        uiMedia = Math.max(0, media);
    }

    /** Numeric filter values only; arbitrary genre metadata is represented by count and digest. */
    public static synchronized void libraryState(String page, String mode, String sort, String genres, String years,
                                                 String providers, boolean unmatched)
    {
        uiState(page, "library", mode, sort,
                (genres.isEmpty() ? "" : "genre,") + (years.isEmpty() ? "" : "year,") +
                    (providers.isEmpty() ? "" : "provider,") + (unmatched ? "unmatched" : ""),
                0);
        try
        {
            java.util.Set<String> genreSet = new java.util.TreeSet<>();
            for (String genre : genres.split("\\|"))
                if (!genre.trim().isEmpty())
                    genreSet.add(genre.trim());
            String digest = "none";
            if (!genreSet.isEmpty())
            {
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(
                    TextUtils.join("|", genreSet).getBytes(StandardCharsets.UTF_8));
                StringBuilder value = new StringBuilder();
                for (byte b : hash)
                    value.append(String.format(java.util.Locale.ROOT, "%02x", b));
                digest = value.toString();
            }
            uiFilterDetails = new JSONObject()
                                  .put("genre_count", genreSet.size())
                                  .put("genre_selection_id", digest)
                                  .put("years", numericFilters(years))
                                  .put("provider_ids", numericFilters(providers))
                                  .put("unmatched", unmatched)
                                  .toString();
        }
        catch (Exception unavailable)
        {
            uiFilterDetails = "{}";
        }
    }

    private static JSONArray numericFilters(String values)
    {
        JSONArray result = new JSONArray();
        for (String value : values.split("\\|"))
            if (value.matches("[0-9]{1,9}") && result.length() < 100)
                result.put(Long.parseLong(value));
        return result;
    }

    static String uiLabel(String value)
    {
        return value == null || value.isEmpty()      ? "none"
        : value.matches("[A-Za-z0-9_.:,& -]{1,100}") ? value
                                                     : "unknown";
    }

    public static synchronized void modalDepth(int depth)
    {
        modalState(depth, depth > 0 ? "unknown" : "none");
    }

    public static synchronized void modalState(int depth, String kinds)
    {
        uiModals = Math.max(0, depth);
        uiModalKinds = uiModals == 0 ? "none" : uiLabel(kinds);
    }

    public static void focusedMedia(long id)
    {
        uiMedia = Math.max(0, id);
    }

    public static String artworkSurface()
    {
        return uiPage;
    }

    public static long artworkMedia()
    {
        return uiMedia;
    }

    /** Coarse connectivity only: no SSID, address, interface or network name. */
    public static String connectivity()
    {
        if (context == null)
            return "unknown";
        try
        {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo active = manager == null ? null : manager.getActiveNetworkInfo();
            return active == null ? "disconnected" : active.isConnected() ? "connected" : "disconnected";
        }
        catch (RuntimeException unavailable)
        {
            return "unknown";
        }
    }

    private static volatile String lastOperation = "startup";
    private static SharedPreferences sessionState;
    private static final Map<Activity, Long> SCREEN_OPENED = new WeakHashMap<>();
    private static final Map<Activity, DiagnosticFrameSampler> FRAME_SAMPLERS = new WeakHashMap<>();
    private static final Map<Activity, ViewTreeObserver.OnGlobalFocusChangeListener> FOCUS = new WeakHashMap<>();

    static SharedPreferences processSessionState(Context c, String processName)
    {
        return c.getSharedPreferences("supernova_diagnostic_session_" + processName.replaceAll("[^a-zA-Z0-9._-]", "_"),
                                      Context.MODE_PRIVATE);
    }

    private static String currentProcessName(Context c)
    {
        if (Build.VERSION.SDK_INT >= 28)
        {
            String name = Application.getProcessName();
            if (name != null && !name.isEmpty())
                return name;
        }
        android.app.ActivityManager manager = (android.app.ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
        {
            java.util.List<android.app.ActivityManager.RunningAppProcessInfo> processes =
                manager.getRunningAppProcesses();
            if (processes != null)
                for (android.app.ActivityManager.RunningAppProcessInfo process : processes)
                    if (process.pid == Process.myPid())
                        return process.processName;
        }
        // Unknown roles must not overwrite another process's crash/clean marker.
        return "unknown_" + Process.myPid();
    }

    public static void install(Application app)
    {
        context = app.getApplicationContext();
        enabled = PreferenceManager.getDefaultSharedPreferences(app).getBoolean(KEY, defaultEnabled());
        installTransportReads();
        PreferenceManager.getDefaultSharedPreferences(app).getString(LEVEL, "qa");
        if (!heartbeatInstalled)
        {
            heartbeatInstalled = true;
            HEARTBEAT.scheduleWithFixedDelay(Diagnostics::heartbeat, 10, 10, TimeUnit.SECONDS);
        }
        PreferenceManager.getDefaultSharedPreferences(app).registerOnSharedPreferenceChangeListener(CONFIG);
        sessionState = processSessionState(app, currentProcessName(app));
        if (enabled)
        {
            String previousProcess = sessionState.getString("process", "");
            if (!previousProcess.isEmpty() && !sessionState.getBoolean("clean", true))
                event("previous_session_without_clean_marker", "previous_process", previousProcess, "previous_pid",
                      sessionState.getInt("pid", 0), "previous_operation",
                      sessionState.getString("last_operation", "unknown"), "previous_screen",
                      sessionState.getString("last_screen", "unknown"), "previous_focus",
                      sessionState.getString("last_focus", "unknown"));
            sessionState.edit()
                .putString("process", PROCESS)
                .putInt("pid", Process.myPid())
                .putBoolean("clean", false)
                .apply();
        }
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            try
            {
                if (enabled)
                {
                    System.err.println("Supernova uncaught exception: " + trace(error));
                    String line = record("uncaught_exception", "thread", thread.getName(), "trace", trace(error));
                    FLIGHT.add(SystemClock.elapsedRealtime(), line);
                    write(line, playback);
                    writeImportant(line);
                    freeze("uncaught_exception");
                }
            }
            finally
            {
                if (previous != null)
                    previous.uncaughtException(thread, error);
                else
                {
                    Process.killProcess(Process.myPid());
                    System.exit(10);
                }
            }
        });
        app.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override public void onTrimMemory(int level)
            {
                event("memory_trim", "level", level, "heap_bytes",
                      Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), "native_heap_bytes",
                      Debug.getNativeHeapAllocatedSize());
            }
            @Override public void onLowMemory()
            {
                event("memory_low");
            }
            @Override public void onConfigurationChanged(Configuration configuration)
            {
            }
        });
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            private void life(Activity a, String state)
            {
                event("lifecycle", "screen", a.getClass().getSimpleName(), "state", state);
            }
            public void onActivityCreated(Activity a, Bundle b)
            {
                SCREEN_OPENED.put(a, SystemClock.elapsedRealtime());
                life(a, "created");
            }
            public void onActivityStarted(Activity a)
            {
                if (foreground++ == 0)
                {
                    appSession = UUID.randomUUID().toString();
                    event("app_session_begin");
                }
                life(a, "started");
            }
            public void onActivityResumed(Activity a)
            {
                mainAck = SystemClock.elapsedRealtime();
                resumeUi(a);
                life(a, "resumed");
                Long opened = SCREEN_OPENED.remove(a);
                event("screen_open", "screen", a.getClass().getSimpleName(), "open_ms",
                      opened == null ? -1 : SystemClock.elapsedRealtime() - opened, "measurement",
                      "created_to_resumed");
                DiagnosticFrameSampler previousSampler = FRAME_SAMPLERS.remove(a);
                if (previousSampler != null)
                    previousSampler.close();
                if (enabled)
                    FRAME_SAMPLERS.put(a, new DiagnosticFrameSampler(a.getClass().getSimpleName()));
                if (enabled && sessionState != null)
                    sessionState.edit().putBoolean("clean", false).apply();
                ViewTreeObserver.OnGlobalFocusChangeListener listener = (old, next) ->
                {
                    uiFocus = viewId(next);
                    if (enabled && sessionState != null)
                        sessionState.edit()
                            .putString("last_screen", a.getClass().getSimpleName())
                            .putString("last_focus", uiFocus)
                            .apply();
                    long now = SystemClock.elapsedRealtime();
                    if (!enabled || now - focusAt < 100)
                        return;
                    focusAt = now;
                    event("focus", "screen", a.getClass().getSimpleName(), "from", viewId(old), "to", viewId(next));
                    if (next != null)
                        next.postDelayed(() -> inspectFocusGeometry(next), 250);
                };
                FOCUS.put(a, listener);
                a.getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(listener);
            }
            public void onActivityPaused(Activity a)
            {
                DiagnosticFrameSampler sampler = FRAME_SAMPLERS.remove(a);
                if (sampler != null)
                    sampler.close();
                life(a, "paused");
                pauseUi(a);
                ViewTreeObserver.OnGlobalFocusChangeListener l = FOCUS.remove(a);
                if (l != null)
                    a.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalFocusChangeListener(l);
            }
            public void onActivityStopped(Activity a)
            {
                foreground = Math.max(0, foreground - 1);
                life(a, "stopped");
                if (foreground == 0)
                {
                    event("app_session_end");
                    appSession = "";
                    if (enabled && sessionState != null)
                    {
                        event("session_backgrounded");
                        sessionState.edit().putBoolean("clean", true).apply();
                    }
                }
            }
            public void onActivitySaveInstanceState(Activity a, Bundle b)
            {
            }
            public void onActivityDestroyed(Activity a)
            {
                life(a, "destroyed");
                if (enabled && a.isTaskRoot() && a.isFinishing() && sessionState != null)
                {
                    event("session_clean_shutdown");
                    sessionState.edit().putBoolean("clean", true).apply();
                }
            }
        });
        if (enabled)
            HEARTBEAT.execute(Diagnostics::previousExitEvidence);
        event("startup", "default_logging", defaultEnabled(), "version", BuildConfig.VERSION_NAME, "sdk",
              Build.VERSION.SDK_INT);
    }

    private static String viewId(View view)
    {
        if (view == null)
            return "none";
        for (View ancestor = view; ancestor != null;
             ancestor = ancestor.getParent() instanceof View ? (View)ancestor.getParent() : null)
        {
            Object mark = ancestor.getTag(R.id.preview_diagnostic_semantic);
            if ("semantic:private.credential".equals(mark))
                return "semantic:private.credential";
        }
        Object semantic = view.getTag(R.id.preview_diagnostic_semantic);
        if (!(semantic instanceof String))
            semantic = view.getTag();
        if (semantic instanceof String && ((String)semantic).matches("semantic:[A-Za-z0-9 _:.-]{1,100}"))
            return (String)semantic;
        if (semantic instanceof String && ((String)semantic).matches("[vs][0-9]+"))
            return "semantic:media." + semantic;
        String id = "generated";
        if (view.getId() != View.NO_ID)
            try
            {
                id = view.getResources().getResourceEntryName(view.getId());
            }
            catch (Resources.NotFoundException ignored)
            {
            }
        // Programmatic TV controls often have no resource ID. Structural child
        // positions distinguish them without recording titles or user-entered text.
        StringBuilder position = new StringBuilder();
        View cursor = view;
        for (int depth = 0; depth < 4 && cursor.getParent() instanceof ViewGroup; depth++)
        {
            ViewGroup parent = (ViewGroup)cursor.getParent();
            position.insert(0, "/" + parent.indexOfChild(cursor));
            cursor = parent;
        }
        return view.getClass().getSimpleName() + ":" + id + position;
    }

    public static boolean defaultEnabled()
    {
        return true;
    }

    public static boolean enabled()
    {
        return enabled;
    }

    private static final java.util.Map<View, Integer> UI_REBUILDS = new java.util.WeakHashMap<>();

    public static void uiRebuild(View view, String surface, String reason, int before, int after, boolean recreated)
    {
        if (!enabled)
            return;
        int count;
        synchronized (UI_REBUILDS)
        {
            count = UI_REBUILDS.getOrDefault(view, 0) + 1;
            UI_REBUILDS.put(view, count);
        }
        event("ui_rebuild", "surface", uiLabel(surface), "reason", uiLabel(reason), "view_instance",
              System.identityHashCode(view), "rebuild_count", count, "previous_items", Math.max(0, before), "items",
              Math.max(0, after), "adapter_recreated", recreated);
    }

    public static void semantic(View view, String identity)
    {
        if (view != null && identity != null && identity.matches("[A-Za-z0-9_.:-]{1,90}"))
            view.setTag(R.id.preview_diagnostic_semantic, "semantic:" + identity);
    }

    public static String focusEntry(View opener, String flow)
    {
        String token = PROCESS + ":focus:" + SEQUENCE.incrementAndGet();
        event("focus_entry", "operation_id", token, "flow", uiLabel(flow), "opener", viewId(opener), "screen",
              uiScreen);
        return token;
    }

    public static void focusRestored(View requested, View restored, boolean fallback, boolean success)
    {
        focusRestored("", requested, restored, fallback, success);
    }

    public static void focusRestored(String token, View requested, View restored, boolean fallback, boolean success)
    {
        if (!success || fallback)
            event("focus_anomaly_suspected", "kind", "restoration_fallback", "screen", uiScreen, "success", success);
        event("focus_restoration", "operation_id", token, "screen", uiScreen, "requested", viewId(requested),
              "restored", viewId(restored), "fallback", fallback, "success", success);
    }

    private static int unhandledNavigation;

    public static void navigation(View previous, View next, int key, boolean consumed)
    {
        if (!enabled || key < KeyEvent.KEYCODE_DPAD_UP || key > KeyEvent.KEYCODE_DPAD_CENTER)
            return;
        event("focus_navigation", "screen", uiScreen, "input", key, "from", viewId(previous), "to", viewId(next),
              "consumed", consumed, "edge_held", previous == next && consumed);
        if (next == null)
        {
            event("focus_anomaly_suspected", "kind", "missing_focus", "screen", uiScreen, "input", key);
            return;
        }
        Rect bounds = new Rect();
        boolean visible = next.getGlobalVisibleRect(bounds);
        if (!visible || bounds.width() <= 0 || bounds.height() <= 0)
            event("focus_anomaly_suspected", "kind", "offscreen_focus", "screen", uiScreen, "target", viewId(next));
        if (previous == next && !consumed && key != KeyEvent.KEYCODE_DPAD_CENTER)
        {
            if (++unhandledNavigation == 4)
                event("focus_anomaly_suspected", "kind", "repeated_unhandled_navigation", "screen", uiScreen, "target",
                      viewId(next));
        }
        else
            unhandledNavigation = 0;
    }

    static void inspectFocusGeometry(View target)
    {
        if (!enabled || target instanceof PreviewPages || !target.isAttachedToWindow() || !target.hasFocus() ||
            target.getWidth() == 0 || target.getHeight() == 0)
            return;
        Rect visible = new Rect();
        if (!target.getGlobalVisibleRect(visible))
            return;
        // Sample after focus scrolling settles. Small rounding and deliberate card zoom are ignored.
        if (visible.width() < target.getWidth() * .75f || visible.height() < target.getHeight() * .75f)
            event("focus_anomaly_suspected", "kind", "focused_control_clipped", "target", viewId(target),
                  "visible_width", visible.width(), "visible_height", visible.height(), "control_width",
                  target.getWidth(), "control_height", target.getHeight(), "evidence", "geometry_heuristic");
        View header = target.getRootView().findViewWithTag("semantic:topnav.home");
        if (header != null && !viewId(target).startsWith("semantic:topnav."))
        {
            Rect navigation = new Rect();
            if (header.getGlobalVisibleRect(navigation) && visible.top < navigation.bottom &&
                visible.bottom > navigation.top)
                event("focus_anomaly_suspected", "kind", "navigation_overlap", "target", viewId(target), "evidence",
                      "geometry_heuristic");
        }
    }

    public static void setEnabled(Context c, boolean value)
    {
        context = c.getApplicationContext();
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(KEY, value).apply();
        activatePolicy(value);
    }

    /** Re-read durable configuration without inventing a preference for an unselected domain. */
    public static void reloadPolicy(Context c)
    {
        Object saved = PreferenceManager.getDefaultSharedPreferences(c).getAll().get(KEY);
        boolean desired = saved == null ? defaultEnabled() : Boolean.TRUE.equals(saved);
        if (desired != enabled)
        {
            context = c.getApplicationContext();
            activatePolicy(desired);
        }
    }

    private static void activatePolicy(boolean value)
    {
        synchronized (LOCK)
        {
            enabled = value;
        }
        installTransportReads();
        new Handler(Looper.getMainLooper()).post(() -> {
            for (DiagnosticFrameSampler sampler : FRAME_SAMPLERS.values())
                sampler.close();
            FRAME_SAMPLERS.clear();
            if (enabled)
                for (Activity activity : FOCUS.keySet())
                    FRAME_SAMPLERS.put(activity, new DiagnosticFrameSampler(activity.getClass().getSimpleName()));
        });
        mainAck = SystemClock.elapsedRealtime();
        if (!value)
        {
            FLIGHT.clear();
            captureUntil = 0;
            captureIncident = null;
            captureFlight = null;
        }
        if (sessionState != null)
            sessionState.edit()
                .putString("process", PROCESS)
                .putInt("pid", Process.myPid())
                .putBoolean("clean", !value)
                .apply();
        if (value)
            event("logging_enabled", "default", defaultEnabled() ? "on" : "off");
    }

    public static void disable(Context c, boolean deleteSaved)
    {
        setEnabled(c, false);
        if (deleteSaved)
            synchronized (LOCK)
            {
                removeLogs(directory());
                activeIncident = null;
                incidentCounts.clear();
            }
    }

    /** Clear saved evidence while retaining the logging on/off preference. Queued old events are fenced. */
    public static synchronized void clearAll(Context caller) throws IOException
    {
        context = caller.getApplicationContext();
        synchronized (LOCK)
        {
            storageGeneration++;
            File directory = directory();
            for (File file : DiagnosticArchive.files(directory))
            {
                if (file.exists() && !file.delete())
                    throw new IOException("Diagnostic cleanup unavailable");
            }
            FLIGHT.clear();
            COVERAGE.clear();
            DROPPED_TYPES.clear();
            DROPPED.set(0);
            WRITE_ERRORS.set(0);
            ROUTINE_REJECTED.set(0);
            IMPORTANT_REJECTED.set(0);
            RETENTION_PRUNED_BYTES.set(0);
            incidentCounts.clear();
            activeIncident = null;
            incidentId = "";
            captureUntil = freezeUntil = 0;
            captureIncident = null;
            captureFlight = null;
            caller.getSharedPreferences("supernova_diagnostic_session", Context.MODE_PRIVATE)
                .edit()
                .remove("latest_manual_reference")
                .remove("latest_manual_category")
                .remove("latest_manual_time")
                .apply();
        }
    }

    private static void removeLogs(File file)
    {
        File[] children = file.listFiles();
        if (children != null)
            for (File child : children)
                removeLogs(child);
        file.delete();
    }

    public static void chooseDisable(Context c, Consumer<Boolean> changed)
    {
        PreviewDialog.choose(c, "Turn diagnostic logging off?",
                             new String[] {"Cancel", "Keep saved logs", "Delete saved logs"}, 0, n -> {
                                 if (n == 0)
                                     return;
                                 disable(c, n == 2);
                                 changed.accept(false);
                             });
    }

    public static void beginPlayback(Uri source)
    {
        playback = UUID.randomUUID().toString();
        event("playback_begin", "source", sourceType(source));
    }

    public static void endPlayback(String reason)
    {
        event("playback_end", "reason", reason);
        playback = "";
    }

    public static String sourceType(Uri uri)
    {
        if (uri == null)
            return "unknown";
        String scheme = uri.getScheme();
        if (scheme == null || "file".equalsIgnoreCase(scheme))
            return "local";
        String lower = scheme.toLowerCase(Locale.ROOT);
        return Arrays
                   .asList("content", "smb", "smbj", "sshj", "sftp", "webdav", "webdavs", "dav", "davs", "http",
                           "https", "ftp", "ftps", "upnp")
                   .contains(lower)
            ? lower
            : "other";
    }

    private static void installTransportReads()
    {
        TransportReadObserver.install(enabled ? protocol -> {
            if (!enabled)
                return null;
            final String session = playback, id = PROCESS + ":read:" + SEQUENCE.incrementAndGet();
            DiagnosticTransportMetrics metrics =
                new DiagnosticTransportMetrics(id, session, protocol.name(), Diagnostics::event);
            return (boundary, bytes, calls, nanos,
                    elapsed) -> metrics.boundary(boundary.name(), bytes, calls, nanos, elapsed);
        } : null);
    }

    public enum PlaybackOperation
    {
        OPEN,
        SEEK
    }

    public enum PlaybackBoundary
    {
        REQUESTED,
        BACKEND_CREATED,
        PREPARE_SUBMITTED,
        PREPARED,
        FIRST_OUTPUT,
        SEEK_CALLBACK,
        TERMINAL
    }

    public enum MeasurementQuality
    {
        UNKNOWN,
        CALLBACK,
        PROXY,
        NATIVE,
        PRESENTATION
    }

    public enum PlaybackOutcome
    {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED,
        SUPERSEDED,
        UNATTRIBUTED
    }

    /** Producer-time identity. No arbitrary text, native timestamps or media address is accepted. */
    public static final class PlaybackAttempt
    {
        public final String id, parent, playbackSession;
        public final PlaybackOperation operation;
        public final long started, target;
        private boolean terminal;

        private PlaybackAttempt(PlaybackOperation operation, PlaybackAttempt parent, long target)
        {
            this.operation = Objects.requireNonNull(operation);
            this.parent = parent == null ? "" : parent.id;
            playbackSession = playback;
            id = PROCESS + ":playback:" + SEQUENCE.incrementAndGet();
            started = SystemClock.elapsedRealtime();
            this.target = Math.max(-1, target);
        }
    }

    public static PlaybackAttempt playbackAttempt(PlaybackOperation operation, PlaybackAttempt parent, long target)
    {
        PlaybackAttempt attempt = new PlaybackAttempt(operation, parent, target);
        playbackBoundary(attempt, PlaybackBoundary.REQUESTED, PlaybackOutcome.PENDING, MeasurementQuality.UNKNOWN, -1,
                         -1);
        return attempt;
    }

    public static void playbackBoundary(PlaybackAttempt attempt, PlaybackBoundary boundary, PlaybackOutcome outcome,
                                        MeasurementQuality quality, long achieved, int backend)
    {
        if (attempt == null || boundary == null || outcome == null || quality == null)
            return;
        synchronized (attempt)
        {
            if (attempt.terminal)
                return;
            if (boundary == PlaybackBoundary.TERMINAL)
                attempt.terminal = true;
            event("playback_attempt", "schema", 1, "attempt_id", attempt.id, "parent_attempt_id", attempt.parent,
                  "playback_session", attempt.playbackSession, "operation", attempt.operation.name(), "boundary",
                  boundary.name(), "outcome", outcome.name(), "measurement_quality", quality.name(), "target_ms",
                  attempt.target, "achieved_ms", Math.max(-1, achieved), "backend", backend, "attempt_elapsed_ms",
                  Math.max(0, SystemClock.elapsedRealtime() - attempt.started));
        }
    }

    public static void unpairedSeekCallback(PlaybackAttempt open, int backend)
    {
        event("seek_callback_unattributed", "schema", 1, "parent_attempt_id", open == null ? "" : open.id,
              "playback_session", open == null ? "" : open.playbackSession, "outcome",
              PlaybackOutcome.UNATTRIBUTED.name(), "measurement_quality", MeasurementQuality.CALLBACK.name(),
              "achieved_ms", -1, "backend", backend);
    }

    public static synchronized void event(String event, Object... fields)
    {
        if (!enabled || context == null)
            return;
        try
        {
            String line = record(event, fields), session = playback;
            long now = SystemClock.elapsedRealtime();
            FLIGHT.add(now, line);
            if (COVERAGE.size() < 256)
                COVERAGE.add(event);
            boolean detail =
                event.startsWith("focus") || event.startsWith("artwork_") || event.equals("home_page_render");
            if (!detail && !event.startsWith("checkpoint") && !event.equals("scanner_state"))
            {
                lastOperation = safe(event);
                if (sessionState != null)
                    sessionState.edit().putString("last_operation", lastOperation).apply();
            }
            boolean anomaly = event.contains("error") || event.contains("failed") || event.contains("exception") ||
                              event.equals("manual_problem_marker") || event.equals("main_thread_stall_suspected") ||
                              event.equals("focus_anomaly_suspected");
            UiSnapshot failureState = anomaly ? new UiSnapshot() : null;
            if (anomaly && (event.equals("manual_problem_marker") || now >= captureUntil))
            {
                captureFlight = FLIGHT.capture(now);
                captureUntil = now + 60000;
                captureIncident = incidentFile(event.equals("manual_problem_marker"), failureState.utc);
            }
            // Repeated incidents retain the producer-time window without copying its record array again.
            DiagnosticFlightRecorder.Capture retainedCapture = captureFlight;
            DiagnosticFlightRecorder.Capture failureFlight =
                anomaly ? (retainedCapture == null ? FLIGHT.capture(now) : retainedCapture) : null;
            final long generation = storageGeneration;
            final String postTarget = !anomaly && now < captureUntil ? captureIncident : null;
            if (important(event) || anomaly)
                IMPORTANT.execute(new NamedTask(event, () -> {
                    synchronized (Diagnostics.class)
                    {
                        if (!enabled || generation != storageGeneration)
                            return;
                        writeImportant(line);
                        if (anomaly)
                            freeze(event, failureState, failureFlight);
                    }
                }));
            WORK.execute(new NamedTask(event, () -> {
                synchronized (LOCK)
                {
                    if (!enabled || generation != storageGeneration)
                        return;
                    write(line, session);
                    if (postTarget != null)
                        appendPostFlight(postTarget, line);
                }
            }));
        }
        catch (RuntimeException ignored)
        {
            DROPPED.incrementAndGet();
        }
    }

    static boolean important(String event)
    {
        return event.contains("failed") || event.contains("error") || event.contains("exception") ||
            event.contains("UNCLEAN") || event.equals("manual_problem_marker") ||
            event.equals("main_thread_stall_suspected") || event.equals("focus_anomaly_suspected") ||
            event.startsWith("session_") || event.startsWith("app_session_") || event.equals("startup") ||
            event.equals("playback_begin") || event.equals("playback_end") || event.equals("playback_attempt") ||
            event.equals("seek_callback_unattributed") || event.equals("scan_started") ||
            event.equals("scan_complete") || event.equals("scan_partial") || event.equals("incident_capture");
    }

    static String severity(String event)
    {
        if (event.equals("uncaught_exception"))
            return "FATAL";
        if (event.contains("failed") || event.contains("error") || event.contains("exception"))
            return "ERROR";
        if (event.contains("UNCLEAN") || event.equals("main_thread_stall_suspected") ||
            event.equals("focus_anomaly_suspected") || event.equals("scan_partial") || event.contains("retry"))
            return "WARNING";
        return "INFO";
    }

    private static void writeImportant(String line)
    {
        synchronized (LOCK)
        {
            try
            {
                if (!enabled)
                    return;
                File dir = directory();
                if (!dir.isDirectory() && !dir.mkdirs())
                    return;
                SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd", Locale.ROOT);
                day.setTimeZone(TimeZone.getTimeZone("UTC"));
                append(dir, "important-" + day.format(new Date()) + ".jsonl", 3, line.getBytes(StandardCharsets.UTF_8));
                long oldest = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
                for (File file : DiagnosticArchive.files(dir))
                    if ((file.getName().startsWith("important-") || file.getName().startsWith("incident-")) &&
                        file.lastModified() < oldest && !file.delete())
                        WRITE_ERRORS.incrementAndGet();
            }
            catch (IOException failure)
            {
                WRITE_ERRORS.incrementAndGet();
            }
        }
    }

    private static final SharedPreferences.OnSharedPreferenceChangeListener CONFIG = (prefs, key) ->
    {
        if (LEVEL.equals(key))
            prefs.getString(LEVEL, "qa");
        if (key != null && !key.startsWith("supernova_diagnostic_session"))
            event("configuration_changed", "setting", key, "value",
                  DiagnosticRedactor.settingValue(key, prefs.getAll().get(key)));
    };
    private static final ThreadLocal<String> OPERATION_PARENT = new ThreadLocal<>();

    public static final class OperationScope implements AutoCloseable
    {
        private final String previous = OPERATION_PARENT.get();

        private OperationScope(String operation)
        {
            OPERATION_PARENT.set(operation);
        }

        @Override public void close()
        {
            if (previous == null)
                OPERATION_PARENT.remove();
            else
                OPERATION_PARENT.set(previous);
        }
    }

    /** Synchronous worker scope only; never propagates into unrelated executor work. */
    public static OperationScope operationScope(String operation)
    {
        return new OperationScope(operation);
    }

    public static String operation(String kind)
    {
        String id = PROCESS + ":" + SEQUENCE.incrementAndGet();
        event("artwork".equals(kind) ? "artwork_operation_begin" : "operation_begin", "operation_id", id, "kind",
              kind, "parent_operation_id", OPERATION_PARENT.get() == null ? "" : OPERATION_PARENT.get());
        return id;
    }

    public static void finishOperation(String id, String kind, long started)
    {
        event("operation_end", "operation_id", id, "kind", kind, "latency_ms", SystemClock.elapsedRealtime() - started);
    }

    private static long lastStall, lastCpu = Process.getElapsedCpuTime();
    private static int trendSamples;
    private static long heapMin = Long.MAX_VALUE, heapMax, heapTotal, nativeMax;

    private static void heartbeat()
    {
        try
        {
            if (!enabled || context == null)
                return;
            long now = SystemClock.elapsedRealtime();
            if (foreground > 0 && now - mainAck > 15000 && now - lastStall > 60000)
            {
                lastStall = now;
                Thread main = Looper.getMainLooper().getThread();
                event("main_thread_stall_suspected", "unresponsive_ms", now - mainAck, "screen", uiScreen, "focus",
                      uiFocus, "trace", stack(main.getStackTrace()), "evidence", "watchdog_heuristic");
            }
            new Handler(Looper.getMainLooper()).post(() -> mainAck = SystemClock.elapsedRealtime());
            Runtime runtime = Runtime.getRuntime();
            long heap = runtime.totalMemory() - runtime.freeMemory(), nativeHeap = Debug.getNativeHeapAllocatedSize(),
                 cpu = Process.getElapsedCpuTime();
            heapMin = Math.min(heapMin, heap);
            heapMax = Math.max(heapMax, heap);
            heapTotal += heap;
            nativeMax = Math.max(nativeMax, nativeHeap);
            trendSamples++;
            event("heartbeat", "foreground", foreground, "heap_used", heap, "heap_max", runtime.maxMemory(),
                  "native_heap", nativeHeap, "cpu_delta_ms", Math.max(0, cpu - lastCpu), "queue_depth",
                  WORK.getQueue().size(), "dropped", DROPPED.get(), "routine_queue_rejected", ROUTINE_REJECTED.get(),
                  "important_queue_rejected", IMPORTANT_REJECTED.get(), "write_errors", WRITE_ERRORS.get(),
                  "retention_pruned_bytes", RETENTION_PRUNED_BYTES.get(), "flight_bytes", FLIGHT.bytes(), "level",
                  "FULL");
            lastCpu = cpu;
            if (trendSamples >= 6)
            {
                Debug.MemoryInfo memory = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memory);
                event("performance_trend", "samples", trendSamples, "heap_min", heapMin, "heap_max", heapMax,
                      "heap_average", heapTotal / trendSamples, "native_max", nativeMax, "pss_kb", memory.getTotalPss(),
                      "gc_count", Debug.getRuntimeStat("art.gc.gc-count"), "classification",
                      "observed_trend_not_leak_diagnosis");
                heapMin = Long.MAX_VALUE;
                heapMax = heapTotal = nativeMax = 0;
                trendSamples = 0;
            }
        }
        catch (RuntimeException ignored)
        {
            WRITE_ERRORS.incrementAndGet();
        }
    }

    private static String stack(StackTraceElement[] frames)
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Math.min(40, frames.length); i++)
            out.append(frames[i].getClassName())
                .append('.')
                .append(frames[i].getMethodName())
                .append(':')
                .append(frames[i].getLineNumber())
                .append('\n');
        return out.toString();
    }

    static String exitEventName(int reason)
    {
        if (reason == ApplicationExitInfo.REASON_CRASH || reason == ApplicationExitInfo.REASON_CRASH_NATIVE ||
            reason == ApplicationExitInfo.REASON_ANR)
            return "previous_session_unclean_exit";
        if (reason == ApplicationExitInfo.REASON_LOW_MEMORY)
            return "android_process_exit_low_memory";
        if (reason == ApplicationExitInfo.REASON_USER_REQUESTED || reason == ApplicationExitInfo.REASON_USER_STOPPED)
            return "android_process_exit_user_stop";
        return "android_process_exit";
    }

    private static void previousExitEvidence()
    {
        if (!enabled || Build.VERSION.SDK_INT < 30)
            return;
        try
        {
            android.app.ActivityManager manager =
                (android.app.ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            long since = sessionState.getLong("exit_evidence_time", 0), latest = since;
            for (ApplicationExitInfo info : manager.getHistoricalProcessExitReasons(context.getPackageName(), 0, 4))
            {
                if (info.getTimestamp() <= since)
                    continue;
                latest = Math.max(latest, info.getTimestamp());
                event(exitEventName(info.getReason()), "reason", info.getReason(), "status", info.getStatus(),
                      "importance", info.getImportance(), "pss_kb", info.getPss(), "rss_kb", info.getRss(),
                      "exit_utc_ms", info.getTimestamp(), "exit_pid", info.getPid(), "evidence", "android_os",
                      "description", info.getDescription(), "process_name", info.getProcessName());
                // OS ANR/native trace streams may contain file paths and account data. Export bounded method frames only.
                if (info.getReason() == ApplicationExitInfo.REASON_ANR)
                    try (java.io.InputStream input = info.getTraceInputStream())
                    {
                        if (input != null)
                        {
                            BufferedReader reader =
                                new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                            String line;
                            StringBuilder frames = new StringBuilder();
                            int bytes = 0, count = 0;
                            while ((line = reader.readLine()) != null && bytes < 65536 && count < 40)
                            {
                                bytes += line.length();
                                String trimmed = line.trim();
                                if (trimmed.matches("at [A-Za-z0-9_.$]+\\([^)]*\\)"))
                                {
                                    frames.append(trimmed.replaceAll("\\([^)]*\\)", "(location withheld)"))
                                        .append('\n');
                                    count++;
                                }
                            }
                            event("android_anr_stack", "trace", frames.toString(), "frames", count, "evidence",
                                  "android_os");
                        }
                    }
                    catch (java.io.IOException unavailable)
                    {
                        event("android_exit_trace_unavailable");
                    }
            }
            sessionState.edit().putLong("exit_evidence_time", latest).apply();
        }
        catch (RuntimeException unavailable)
        {
            error("android_exit_evidence_unavailable", unavailable);
        }
    }

    private static synchronized void freeze(String reason)
    {
        freeze(reason, new UiSnapshot(), FLIGHT.capture(SystemClock.elapsedRealtime()));
    }

    private static synchronized void freeze(String reason, UiSnapshot state, DiagnosticFlightRecorder.Capture flight)
    {
        if (context == null)
            return;
        boolean manual = reason.equals("manual_problem_marker");
        if (!manual && state.elapsed < freezeUntil)
        {
            long count = incidentCounts.containsKey(reason) ? incidentCounts.get(reason) + 1 : 1;
            incidentCounts.put(reason, count);
            appendFlight(recordWithContext("incident_repeated", state.playbackSession, state.foregroundSession,
                                           state.operation, "reason", reason, "incident_id", incidentId, "occurrences",
                                           count, "failure_utc_ms", state.utc));
            // Cumulative power-of-two summaries bound noise without dropping the underlying events.
            if ((count & (count - 1)) == 0)
                writeImportant(recordWithContext(
                    "incident_burst_summary", state.playbackSession, state.foregroundSession, state.operation, "reason",
                    reason, "incident_id", incidentId, "occurrences", count, "count_kind", "cumulative"));
            return;
        }
        incidentId = PROCESS + ":incident:" + SEQUENCE.incrementAndGet();
        incidentCounts.clear();
        incidentCounts.put(reason, 1L);
        String incident = incidentContext(reason, state);
        writeImportant(incident);
        String retained = flight.text();
        String snapshot = recordWithContext(
            "flight_snapshot", state.playbackSession, state.foregroundSession, state.operation, "reason", reason,
            "completeness", retained.isEmpty() ? "RECOVERY_DATA_MISSING" : "PARTIAL", "dropped", DROPPED.get(),
            "write_errors", WRITE_ERRORS.get(), "flight_evicted", flight.evicted, "post_window_until_elapsed_ms",
            state.elapsed + 60000, "captured_elapsed_ms", flight.elapsed, "writer_delay_ms",
            Math.max(0, SystemClock.elapsedRealtime() - flight.elapsed));
        synchronized (LOCK)
        {
            try
            {
                if (!enabled)
                    return;
                File dir = directory();
                if (!dir.isDirectory() && !dir.mkdirs())
                {
                    WRITE_ERRORS.incrementAndGet();
                    return;
                }
                activeIncident = incidentFile(manual, state.utc);
                // Append protected copies instead of replacing the previous incident window.
                // Manual windows have a separate daily budget from automatic failures.
                append(dir, activeIncident, 3, (incident + snapshot + retained).getBytes(StandardCharsets.UTF_8));
                AtomicFile file = new AtomicFile(new File(dir, "flight.jsonl"));
                FileOutputStream out = null;
                try
                {
                    out = file.startWrite();
                    out.write((snapshot + retained).getBytes(StandardCharsets.UTF_8));
                    file.finishWrite(out);
                }
                catch (Exception error)
                {
                    if (out != null)
                        file.failWrite(out);
                    WRITE_ERRORS.incrementAndGet();
                }
                freezeUntil = state.elapsed + 60000;
            }
            catch (IOException | RuntimeException error)
            {
                WRITE_ERRORS.incrementAndGet();
            }
        }
    }

    private static String incidentContext(String reason)
    {
        return incidentContext(reason, new UiSnapshot());
    }

    static String incidentContext(String reason, UiSnapshot state)
    {
        long sampledUtc = System.currentTimeMillis(), sampledElapsed = SystemClock.elapsedRealtime();
        long available = -1;
        boolean lowMemory = false;
        try
        {
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            if (manager != null)
            {
                ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
                manager.getMemoryInfo(memory);
                available = memory.availMem;
                lowMemory = memory.lowMemory;
            }
        }
        catch (RuntimeException ignored)
        {
            /* Missing resource data must not prevent incident capture. */
        }
        Runtime runtime = Runtime.getRuntime();
        return recordWithContext(
            "incident_capture", state.playbackSession, state.foregroundSession, state.operation, "reason", reason,
            "incident_id", incidentId, "foreground", state.foregroundCount, "failure_utc_ms", state.utc,
            "resource_sample_utc_ms", sampledUtc, "resource_sample_delay_ms",
            Math.max(0, sampledElapsed - state.elapsed), "screen", state.screen, "page", state.page, "category",
            state.category, "focused_control", state.focus, "view_mode", state.mode, "sort", state.sort,
            "active_filters", state.filters, "filter_state", state.filterDetails, "media_id", state.media,
            "modal_depth", state.modals, "modal_kinds", state.modalKinds, "heap_used",
            runtime.totalMemory() - runtime.freeMemory(), "heap_max", runtime.maxMemory(), "native_heap",
            Debug.getNativeHeapAllocatedSize(), "available_memory", available, "low_memory", lowMemory, "storage_free",
            context.getFilesDir().getUsableSpace(), "thread_count", Thread.activeCount());
    }

    private static String incidentFile(boolean manual, long utc)
    {
        SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd", Locale.ROOT);
        day.setTimeZone(TimeZone.getTimeZone("UTC"));
        return "incident-" + (manual ? "manual-" : "auto-") + day.format(new Date(utc)) + ".jsonl";
    }

    private static void appendPostFlight(String target, String line)
    {
        synchronized (LOCK)
        {
            try
            {
                File dir = directory();
                if (!dir.isDirectory() && !dir.mkdirs())
                {
                    WRITE_ERRORS.incrementAndGet();
                    return;
                }
                // The protected daily stream is append-only. The latest flight file may be replaced
                // by a delayed pre-failure snapshot, so it is not the post-window authority.
                append(dir, target, 3, line.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException error)
            {
                WRITE_ERRORS.incrementAndGet();
            }
        }
    }

    private static void appendFlight(String line)
    {
        synchronized (LOCK)
        {
            try
            {
                byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                append(directory(), "flight.jsonl", 1, bytes);
                if (activeIncident != null)
                    append(directory(), activeIncident, 3, bytes);
            }
            catch (IOException error)
            {
                WRITE_ERRORS.incrementAndGet();
            }
        }
    }

    public static void reportProblem(Context c)
    {
        if (!enabled)
        {
            PreviewDialog.read(c, "Diagnostic logging is off",
                               "Enable Diagnostic Logging in Advanced settings before recording a problem.");
            return;
        }
        String[] categories = {"Playback", "UI", "Network", "Other"};
        PreviewDialog.choose(c, "Report a Problem", categories, -1, n -> {
            String id = PROCESS.substring(0, 8) + "-" + SEQUENCE.incrementAndGet();
            long time = System.currentTimeMillis();
            event("manual_problem_marker", "category", categories[n], "marker_id", id, "reported_at", time);
            c.getSharedPreferences("supernova_diagnostic_session", Context.MODE_PRIVATE)
                .edit()
                .putString("latest_manual_reference", id)
                .putString("latest_manual_category", categories[n])
                .putLong("latest_manual_time", time)
                .apply();
            showReference(c, "Problem recorded", id, categories[n], time);
        });
    }

    public static void showDigest(Context c)
    {
        WORK.execute(() -> {
            String digest = summary();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (c instanceof Activity && (((Activity)c).isFinishing() || ((Activity)c).isDestroyed()))
                    return;
                PreviewDialog.read(c, "Issues Digest", digest);
            });
        });
    }

    public static void showLatestReference(Context c)
    {
        SharedPreferences saved = c.getSharedPreferences("supernova_diagnostic_session", Context.MODE_PRIVATE);
        String id = saved.getString("latest_manual_reference", "");
        if (id.isEmpty())
        {
            PreviewDialog.read(c, "Latest Reference", "No problem has been recorded yet.");
            return;
        }
        showReference(c, "Latest Reference", id, saved.getString("latest_manual_category", ""),
                      saved.getLong("latest_manual_time", 0));
    }

    private static void showReference(Context c, String title, String id, String category, long time)
    {
        try
        {
            DiagnosticReference.payload(id, category, time);
        }
        catch (IllegalArgumentException invalid)
        {
            PreviewDialog.read(c, title,
                               "The saved reference is unavailable. Record a new problem to create a reference.");
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.UK);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String confirmation = "Reference: " + id + "\nCategory: " + category +
                              "\nTime: " + format.format(new Date(time)) +
                              ("\n\nUse this reference with your screenshot or video. Export Diagnostic Report to "
                               + "preserve the evidence.");
        Bitmap qr = null;
        try
        {
            qr = DiagnosticReference.bitmap(id, category, time, 512);
        }
        catch (WriterException | RuntimeException failure)
        {
            confirmation += "\nQR unavailable; the readable reference remains valid.";
        }
        PreviewDialog.read(c, title, confirmation, qr);
    }

    public static void error(String event, Throwable error, Object... fields)
    {
        Object[] all = java.util.Arrays.copyOf(fields, fields.length + 8);
        all[fields.length] = "failure_class";
        all[fields.length + 1] = error == null ? "not_supplied" : error.getClass().getName();
        all[fields.length + 2] = "message";
        all[fields.length + 3] = error == null ? event : error.getMessage();
        all[fields.length + 4] = "trace";
        all[fields.length + 5] = trace(error);
        all[fields.length + 6] = "trace_origin";
        all[fields.length + 7] = "caught_exception";
        event(event, all);
    }

    static String trace(Throwable error)
    {
        return DiagnosticRedactor.trace(error);
    }

    static String safe(String value)
    {
        return DiagnosticRedactor.clean(value);
    }

    private static String record(String event, Object... fields)
    {
        return recordWithContext(event, playback, appSession, lastOperation, fields);
    }

    /** Only captured internal snapshots may supply a deferred event's session context. */
    private static String recordWithContext(String event, String capturedSession, String capturedAppSession,
                                            String capturedOperation, Object... fields)
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put("utc_ms", System.currentTimeMillis());
            json.put("elapsed_ms", SystemClock.elapsedRealtime());
            json.put("severity", severity(event));
            json.put("sequence", SEQUENCE.incrementAndGet());
            json.put("pid", Process.myPid());
            json.put("last_operation", capturedOperation);
            json.put("process", PROCESS);
            json.put("session", capturedSession);
            json.put("app_session", capturedAppSession);
            json.put("event", safe(event));
            for (int i = 0; i + 1 < fields.length; i += 2)
            {
                String key = String.valueOf(fields[i]);
                if (!key.matches("[a-z_]{1,48}") ||
                    key.matches(".*(password|credential|token|secret|header|cookie|path|url|uri|api_key).*"))
                    continue;
                if ("elapsed_ms".equals(key))
                    key = "duration_ms";
                else if (java.util.Arrays
                             .asList("utc_ms", "severity", "sequence", "pid", "last_operation", "process", "session",
                                     "app_session", "event")
                             .contains(key))
                    continue;
                Object value = fields[i + 1];
                json.put(key,
                         value instanceof Number || value instanceof Boolean ? value : safe(String.valueOf(value)));
            }
            if ("ERROR".equals(json.optString("severity")) || "FATAL".equals(json.optString("severity")))
            {
                if (!json.has("message"))
                    json.put("message", safe(event));
                if (!json.has("failure_class"))
                    json.put("failure_class", "not_supplied");
                if (!json.has("trace"))
                {
                    json.put("trace", trace(new IllegalStateException("Failure reported at " + event)));
                    json.put("trace_origin", "reporting_call_site");
                }
            }
            if ("heartbeat".equals(event))
                json.put("dropped_by_event_type", droppedTypes());
            String line = json.toString() + "\n";
            if (line.getBytes(StandardCharsets.UTF_8).length > 1024 * 1024)
            {
                DROPPED.incrementAndGet();
                return "";
            }
            return line;
        }
        catch (Exception ignored)
        {
            DROPPED.incrementAndGet();
            return "";
        }
    }

    private static File directory()
    {
        return new File(context.getFilesDir(), "supernova-diagnostics");
    }

    private static void rotate(File dir, String name, int copies, int incoming) throws IOException
    {
        File active = new File(dir, name);
        if (active.length() + incoming <= LIMIT)
            return;
        File oldest = new File(dir, name + "." + copies);
        if (oldest.exists() && !oldest.delete())
            throw new IOException("rotation unavailable");
        for (int i = copies - 1; i >= 0; i--)
        {
            File from = new File(dir, name + (i == 0 ? "" : "." + i));
            if (from.exists() && !from.renameTo(new File(dir, name + "." + (i + 1))))
                throw new IOException("rotation unavailable");
        }
    }

    private static void append(File dir, String name, int copies, byte[] bytes) throws IOException
    {
        if (bytes.length > LIMIT)
        {
            int start = 0;
            while (start < bytes.length)
            {
                int end = Math.min(bytes.length, start + LIMIT);
                if (end < bytes.length)
                    while (end > start && bytes[end - 1] != '\n')
                        end--;
                if (end == start)
                    throw new IOException("Diagnostic record exceeds retention limit");
                append(dir, name, copies, Arrays.copyOfRange(bytes, start, end));
                start = end;
            }
            return;
        }
        trimStorage(dir, STORAGE_LIMIT - bytes.length);
        rotate(dir, name, copies, bytes.length);
        try (FileOutputStream stream = new FileOutputStream(new File(dir, name), true))
        {
            stream.write(bytes);
        }
    }

    /** Only recorder-owned files are eligible for eviction. A failed eviction prevents further growth. */
    private static void trimStorage(File dir, long budget) throws IOException
    {
        List<RetentionSnapshot.Entry> files = RetentionSnapshot.oldestFirst(DiagnosticArchive.files(dir));
        long total = 0;
        for (RetentionSnapshot.Entry entry : files)
            total += entry.bytes;
        for (RetentionSnapshot.Entry entry : files)
        {
            if (total <= budget)
                return;
            long length = entry.bytes;
            if (!entry.file.delete())
                throw new IOException("Diagnostic retention cleanup unavailable");
            total -= length;
            RETENTION_PRUNED_BYTES.addAndGet(length);
        }
        if (total > budget)
            throw new IOException("Diagnostic storage exceeds retention limit");
    }

    /** Bounded immutable copy; parsing, capability queries and ZIP compression happen after releasing LOCK. */
    private static File exportSnapshot(Context c) throws IOException
    {
        File snapshot = new File(c.getCacheDir(), "supernova-diagnostic-export-" + UUID.randomUUID());
        if (!snapshot.mkdir())
            throw new IOException("Cannot prepare diagnostic snapshot");
        boolean copied = false;
        try
        {
            synchronized (LOCK)
            {
                File dir = directory();
                trimStorage(dir, STORAGE_LIMIT);
                for (File file : DiagnosticArchive.files(dir))
                    try (InputStream input = new FileInputStream(file);
                         OutputStream output = new FileOutputStream(new File(snapshot, file.getName())))
                    {
                        byte[] buffer = new byte[8192];
                        int read, total = 0;
                        while (total < LIMIT &&
                               (read = input.read(buffer, 0, Math.min(buffer.length, LIMIT - total))) != -1)
                        {
                            output.write(buffer, 0, read);
                            total += read;
                        }
                    }
            }
            copied = true;
            return snapshot;
        }
        finally
        {
            if (!copied)
                removeLogs(snapshot);
        }
    }

    private static void write(String line, String session)
    {
        if (context == null || line.isEmpty())
            return;
        synchronized (LOCK)
        {
            try
            {
                if (!enabled)
                    return;
                File dir = directory();
                if (!dir.isDirectory() && !dir.mkdirs())
                {
                    WRITE_ERRORS.incrementAndGet();
                    return;
                }
                byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                append(dir, "events.jsonl", ROTATIONS, bytes);
                if (!session.isEmpty())
                    append(dir, "playback.jsonl", 1, bytes);
            }
            catch (Exception ignored)
            {
                WRITE_ERRORS.incrementAndGet(); /* Instrumentation must not affect playback or storage operations. */
            }
        }
    }

    /** Called on a worker. Only our bounded, sanitised files and allow-listed device fields are exported. */
    public static void export(Context c, OutputStream destination) throws IOException
    {
        context = c.getApplicationContext();
        synchronized (EXPORT_LOCK)
        {
            File snapshot = exportSnapshot(c);
            try (ZipOutputStream zip = new ZipOutputStream(destination))
            {
                JSONObject info = new JSONObject();
                try
                {
                    info.put("package", c.getPackageName());
                    info.put("version_name", BuildConfig.VERSION_NAME);
                    info.put("version_code", BuildConfig.VERSION_CODE);
                    info.put("source_commit", BuildConfig.PREVIEW_GIT_SHA);
                    info.put("build_utc", BuildConfig.PREVIEW_BUILD_UTC);
                    info.put("manufacturer", safe(Build.MANUFACTURER));
                    info.put("model", safe(Build.MODEL));
                    info.put("android_release", safe(Build.VERSION.RELEASE));
                    info.put("sdk", Build.VERSION.SDK_INT);
                    info.put("abis", Arrays.toString(Build.SUPPORTED_ABIS));
                    info.put("logging_enabled", enabled);
                    info.put("utc_ms", System.currentTimeMillis());
                }
                catch (JSONException failure)
                {
                    throw new IOException(failure);
                }
                entry(zip, "build-device.json", info.toString(2).getBytes(StandardCharsets.UTF_8));
                StringBuilder decoders = new StringBuilder();
                try
                {
                    for (MediaCodecInfo codec : new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos())
                        if (!codec.isEncoder())
                            decoders.append(safe(codec.getName()))
                                .append(" ")
                                .append(Arrays.toString(codec.getSupportedTypes()))
                                .append('\n');
                }
                catch (RuntimeException failure)
                {
                    decoders.append("Capability enumeration unavailable: ").append(failure.getClass().getSimpleName());
                }
                entry(zip, "decoders.txt", decoders.toString().getBytes(StandardCharsets.UTF_8));
                entry(zip, "README.txt",
                      ("Supernova opt-in diagnostic report\nStructured UTC and monotonic timestamps; process and "
                       + "playback session IDs.\nLogging defaults to " + (defaultEnabled() ? "on" : "off") +
                       (". Disabling stops new events, not export of existing bounded logs.\nException messages and "
                        + "stack traces are retained after credential/location redaction. "
                        + "Secret setting values and credentials are excluded.\nRoutine streams "
                        + "are size-bounded. Important events use a separate queue and up to seven-day, daily "
                        + "size-bounded retention; all recorder-owned files share a 32 MiB cap. Oldest files may be "
                        + "removed sooner. Copies are deduplicated by process/sequence in the summaries.\nNo native "
                        + "decoder A/V clock precision or physical Shield playback success is implied.\n"))
                          .getBytes(StandardCharsets.UTF_8));
                entry(zip, "summary.txt", summary(snapshot).getBytes(StandardCharsets.UTF_8));
                entry(zip, "manifest.json",
                      DiagnosticArchive.manifest(snapshot).toString(2).getBytes(StandardCharsets.UTF_8));
                String coverage;
                synchronized (COVERAGE)
                {
                    coverage = COVERAGE.toString();
                }
                entry(zip, "coverage.txt",
                      ("Completeness: PARTIAL\nObserved event types: " + coverage +
                       ("\nNative decoder and every direct network transport are not fully observable. No "
                        + "app-quality score.\nDropped: ") +
                       DROPPED.get() + "; write errors: " + WRITE_ERRORS.get() + "; queued: " + WORK.getQueue().size() +
                       "; retention-pruned bytes: " + RETENTION_PRUNED_BYTES.get() + "\n")
                          .getBytes(StandardCharsets.UTF_8));
                for (File file : DiagnosticArchive.files(snapshot))
                {
                    String name = file.getName();
                    zip.putNextEntry(new ZipEntry(name));
                    try (InputStream in = new FileInputStream(file))
                    {
                        byte[] buffer = new byte[8192];
                        int read, total = 0;
                        while (total < LIMIT &&
                               (read = in.read(buffer, 0, Math.min(buffer.length, LIMIT - total))) != -1)
                        {
                            zip.write(buffer, 0, read);
                            total += read;
                        }
                    }
                    zip.closeEntry();
                }
            }
            catch (JSONException failure)
            {
                throw new IOException(failure);
            }
            finally
            {
                removeLogs(snapshot);
            }
        }
    }

    private static String summary()
    {
        if (context == null)
            return "No diagnostic context available.";
        try
        {
            File snapshot = exportSnapshot(context);
            try
            {
                return summary(snapshot);
            }
            finally
            {
                removeLogs(snapshot);
            }
        }
        catch (IOException unavailable)
        {
            return "Diagnostic snapshot unavailable. Existing logs are retained; retry later.";
        }
    }

    private static String summary(File snapshot)
    {
        Map<String, Integer> counts = new TreeMap<>();
        Set<String> sessions = new HashSet<>();
        ArrayDeque<String> timeline = new ArrayDeque<>();
        Map<String, Long> retainedDrops = new HashMap<>();
        long first = Long.MAX_VALUE, last = 0;
        List<JSONObject> retainedRecords = DiagnosticArchive.records(snapshot);
        for (JSONObject record : retainedRecords)
        {
            long time = record.optLong("utc_ms");
            if (time > 0)
            {
                first = Math.min(first, time);
                last = Math.max(last, time);
            }
            String process = record.optString("process");
            retainedDrops.put(process, Math.max(retainedDrops.getOrDefault(process, 0L), record.optLong("dropped")));
            String event = record.optString("event");
            counts.put(event, counts.getOrDefault(event, 0) + 1);
            String id = record.optString("session");
            if (!id.isEmpty())
                sessions.add(id);
            if (important(event) || event.equals("seek_complete"))
            {
                timeline.add(record.optLong("utc_ms") + " " + safe(event) + " session=" + id + " operation=" +
                             safe(record.optString("operation_id")) + " outcome=" + safe(record.optString("reason")) +
                             " reference=" + safe(record.optString("marker_id")));
                while (timeline.size() > 80)
                    timeline.removeFirst();
            }
        }
        long droppedTotal = 0;
        for (long value : retainedDrops.values())
            droppedTotal += value;
        String retained =
            "Retained evidence interval (UTC ms): " + (first == Long.MAX_VALUE ? 0 : first) + " to " + last +
            " (not a complete session duration)\nLaunches retained: " + counts.getOrDefault("startup", 0) +
            "; clean shutdowns: " + counts.getOrDefault("session_clean_shutdown", 0) +
            "; OS-confirmed crash/ANR/native exits: " + counts.getOrDefault("previous_session_unclean_exit", 0) +
            "\nManual reports: " + counts.getOrDefault("manual_problem_marker", 0) +
            "; incident captures: " + counts.getOrDefault("incident_capture", 0) +
            "\nDropped events (sum of retained per-process maxima): " + droppedTotal + "\n";
        String linked = "";
        try
        {
            linked = DiagnosticArchive.linkedSummary(snapshot);
        }
        catch (JSONException incomplete)
        {
            linked = "\nLinked summary unavailable; raw evidence retained.\n";
        }
        return linked +
            "Supernova diagnostic summary\nCompleteness: PARTIAL (native/direct transport coverage is incomplete)\n" +
            retained + "Current logger dropped: " + DROPPED.get() + "; write errors: " + WRITE_ERRORS.get() +
            "\nBuild: " + BuildConfig.VERSION_NAME + "\nSource: " + BuildConfig.PREVIEW_GIT_SHA +
            "\nPlayback sessions retained: " + sessions.size() + "\nEvent counts: " + counts +
            "\nRecent significant events (UTC milliseconds):\n" + TextUtils.join("\n", timeline) +
            ("\nUnclean exits require OS crash/ANR/native-crash evidence. Missing clean markers are recorded "
             + "separately. ") +
            "Native A/V drift and decoder dropped-frame counters are unavailable in the current Java bridge.\n"
            + "Dropped by event type in this process: " + droppedTypes() + "\n" +
            DiagnosticExpandedSummary.build(retainedRecords);
    }

    private static void entry(ZipOutputStream zip, String name, byte[] bytes) throws IOException
    {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(bytes);
        zip.closeEntry();
    }

    private Diagnostics()
    {
    }
}
