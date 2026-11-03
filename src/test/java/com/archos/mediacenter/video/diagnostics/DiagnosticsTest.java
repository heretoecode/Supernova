package com.archos.mediacenter.video.diagnostics;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.preference.PreferenceManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class DiagnosticsTest {
    @Test
    public void independentProcessRolesCannotMarkEachOtherAsUnclean() {
        Application c = RuntimeEnvironment.getApplication();
        SharedPreferences main = Diagnostics.processSessionState(c, "app.supernova.player"),
                                          scanner = Diagnostics.processSessionState(c, "app.supernova.player:scanner");
        main.edit().clear().putString("process", "main-active").putBoolean("clean", false).commit();
        scanner.edit().clear().putString("process", "scanner-active").putBoolean("clean", true).commit();
        assertEquals("main-active", main.getString("process", ""));
        assertFalse(main.getBoolean("clean", true));
        assertEquals("scanner-active", scanner.getString("process", ""));
        assertTrue(scanner.getBoolean("clean", false));
        assertEquals(
            "main-active", Diagnostics.processSessionState(c, "app.supernova.player").getString("process", ""));
    }

    @Test
    public void payloadDurationCannotOverwriteEventClockOrIdentity() throws Exception {
        long before = SystemClock.elapsedRealtime();
        String line = org.robolectric.util.ReflectionHelpers.callStaticMethod(Diagnostics.class, "record",
            org.robolectric.util.ReflectionHelpers.ClassParameter.from(String.class, "artwork_ready"),
            org.robolectric.util.ReflectionHelpers.ClassParameter.from(Object[].class,
                new Object[] {
                    "elapsed_ms", 3, "utc_ms", 1, "sequence", -1, "event", "spoof", "process", "spoof", "pid",
                    -1, "exit_pid", 123, "session", "spoof", "app_session", "spoof", "last_operation", "spoof"
                }));
        var json = new org.json.JSONObject(line);
        assertTrue(json.getLong("elapsed_ms") >= before);
        assertEquals(3, json.getLong("duration_ms"));
        assertTrue(json.getLong("utc_ms") > 1);
        assertTrue(json.getLong("sequence") > 0);
        assertEquals("artwork_ready", json.getString("event"));
        assertNotEquals("spoof", json.getString("process"));
        assertEquals(Process.myPid(), json.getInt("pid"));
        assertEquals(123, json.getInt("exit_pid"));
        assertNotEquals("spoof", json.getString("session"));
        assertNotEquals("spoof", json.getString("app_session"));
        assertNotEquals("spoof", json.getString("last_operation"));
    }

    @Test
    public void temporaryLibraryFocusHolderIsNotClassifiedAsClippedControl() {
        Diagnostics.setEnabled(RuntimeEnvironment.getApplication(), true);
        try {
            var holder = org.mockito.Mockito.mock(com.archos.mediacenter.video.leanback.PreviewPages.class);
            Diagnostics.inspectFocusGeometry(holder);
            org.mockito.Mockito.verifyNoInteractions(holder);
        } finally {
            Diagnostics.setEnabled(RuntimeEnvironment.getApplication(), false);
        }
    }

    @Test
    public void postFailureWindowUsesEventTimeEvenWhenBothWritersAreDelayed() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        ExecutorService important =
            org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class, "IMPORTANT");
        ExecutorService routine =
            org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class, "WORK");
        CountDownLatch entered = new CountDownLatch(2),
                                            release = new CountDownLatch(1);
        try {
            important.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            routine.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            org.robolectric.util.ReflectionHelpers.setStaticField(Diagnostics.class, "freezeUntil", 0L);
            for (ExecutorService writer :
                new ExecutorService[] {
                    important, routine
                })
                writer.execute(() -> {
                    entered.countDown();
                    try {
                        release.await(5, TimeUnit.SECONDS);
                    } catch (InterruptedException stopped) {
                        Thread.currentThread().interrupt();
                    }
                });
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            long failureTime = SystemClock.elapsedRealtime();
            Diagnostics.event("playback_failed", "failure_category", "fixture");
            Diagnostics.event("fixture_immediately_after_failure");
            org.robolectric.shadows.ShadowSystemClock.advanceBy(Duration.ofSeconds(61));
            Diagnostics.event("fixture_outside_post_window");
            release.countDown();
            important.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            routine.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            StringBuilder retained = new StringBuilder();
            for (File file : new File(c.getFilesDir(), "supernova-diagnostics").listFiles())
                if (file.getName().startsWith("incident-auto-"))
                    retained.append(Files.readString(file.toPath()));
            assertTrue(retained.toString().contains("fixture_immediately_after_failure"));
            assertFalse(retained.toString().contains("fixture_outside_post_window"));
            long deadline = org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class, "freezeUntil");
            assertEquals(failureTime + 60000, deadline);
        } finally {
            release.countDown();
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void queuedIncidentPreservesThePreFailureRecorderWindow() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        ExecutorService writer =
            org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class, "IMPORTANT");
        CountDownLatch entered = new CountDownLatch(1),
                                            release = new CountDownLatch(1);
        try {
            writer.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            org.robolectric.util.ReflectionHelpers.setStaticField(Diagnostics.class, "freezeUntil", 0L);
            writer.execute(() -> {
                entered.countDown();
                try {
                    release.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException stopped) {
                    Thread.currentThread().interrupt();
                }
            });
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            Diagnostics.event("fixture_before_queued_failure");
            Diagnostics.event("playback_failed", "failure_category", "fixture");
            DiagnosticFlightRecorder recorder =
                org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            // Simulate full rotation while the protected writer is busy.
            recorder.clear();
            recorder.add(SystemClock.elapsedRealtime(), "{\"event\":\"later_fixture\"}\n");
            release.countDown();
            writer.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            String frozen = Files.readString(
                new File(c.getFilesDir(), "supernova-diagnostics/flight.jsonl").toPath());
            assertTrue(frozen.contains("fixture_before_queued_failure"));
            assertTrue(frozen.contains("playback_failed"));
            assertFalse(frozen.contains("later_fixture"));
            assertTrue(frozen.contains("writer_delay_ms"));
        } finally {
            release.countDown();
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void activityReturnRestoresItsOwnPageAndUnknownChildDoesNotInheritIt() {
        Activity library = Robolectric.buildActivity(Activity.class).setup().get(),
                             child = Robolectric.buildActivity(Activity.class).setup().get();
        Diagnostics.libraryState("movies", "list", "SIZE:descending", "Drama", "2024", "8", true);
        Diagnostics.pauseUi(library);
        Diagnostics.resumeUi(child);
        assertEquals("unknown", new Diagnostics.UiSnapshot().page);
        Diagnostics.uiState("playback", "player", "video", "none", "none", 42);
        Diagnostics.pauseUi(child);
        Diagnostics.resumeUi(library);
        Diagnostics.UiSnapshot restored = new Diagnostics.UiSnapshot();
        assertEquals("movies", restored.page);
        assertEquals("list", restored.mode);
        assertEquals("SIZE:descending", restored.sort);
        assertTrue(restored.filterDetails.contains("2024"));
        Diagnostics.pauseUi(library);
        Diagnostics.resumeUi(child);
        assertEquals("playback", new Diagnostics.UiSnapshot().page);
        assertEquals(42, new Diagnostics.UiSnapshot().media);
        Diagnostics.uiState("unknown", "none", "unknown", "unknown", "none", 0);
        library.finish();
        child.finish();
    }

    @Test
    public void detailsUnchangedSnapshotAvoidsRebuildAndReportsNoPrivateTitle() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        Diagnostics.setEnabled(activity, true);
        try {
            com.archos.mediacenter.video.leanback.details.PreviewMoviePage page =
                new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(
                    activity, ArrayObjectAdapter::new, a -> {
                    }, () -> {
                    }, uri -> {
                    });
            activity.setContentView(page);
            page.bindRemote(new org.json.JSONObject().put("title", "private-fixture-title"), "movie", 0);
            page.setSnapshot(new com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot());
            Object recorder = org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            Method snapshot = recorder.getClass().getDeclaredMethod("snapshot", long.class);
            snapshot.setAccessible(true);
            String evidence = (String) snapshot.invoke(recorder, SystemClock.elapsedRealtime());
            assertFalse(evidence.contains("private-fixture-title"));
            int count = 0;
            boolean indexed = false;
            for (String line : evidence.split("\n")) {
                if (line.isEmpty())
                    continue;
                org.json.JSONObject row = new org.json.JSONObject(line);
                if ("ui_rebuild".equals(row.optString("event"))
                    && "details.information".equals(row.optString("surface"))) {
                    assertEquals(++count, row.getInt("rebuild_count"));
                    assertFalse(row.getBoolean("adapter_recreated"));
                    assertEquals(1, row.getInt("items"));
                    indexed |= "indexed_snapshot".equals(row.getString("reason"));
                }
            }
            // PR #22 intentionally suppresses an identical information bind. The trace must agree.
            assertEquals(1, count);
            assertFalse(indexed);
        } finally {
            Diagnostics.setEnabled(activity, false);
            activity.finish();
        }
    }

    @Test
    public void nativeConfirmationTracksModalWithoutReplacingDismissOrDeleteCallbacks() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().visible().get();
        PreferenceManager.getDefaultSharedPreferences(activity)
            .edit()
            .putBoolean("try_new_ui", true)
            .commit();
        Button opener = new Button(activity);
        opener.setFocusableInTouchMode(true);
        activity.setContentView(opener);
        opener.requestFocus();
        Shadows.shadowOf(activity).setCurrentFocus(opener);
        Diagnostics.setEnabled(activity, true);
        int[] deleted = {
            0
        }, dismissed = {
            0
        };
        Dialog dialog = com.archos.mediacenter.video.leanback.PreviewDialog.confirmDelete(
            activity, "Fixture", "Private fixture text", () -> deleted[0]++);
        try {
            dialog.setOnDismissListener(d -> dismissed[0]++);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            String evidence = Diagnostics.incidentContext("operation_failed", new Diagnostics.UiSnapshot());
            assertEquals("native_dialog", new org.json.JSONObject(evidence).getString("modal_kinds"));
            assertFalse(evidence.contains("Private fixture text"));
            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertEquals(0, deleted[0]);
            assertEquals(1, dismissed[0]);
            assertTrue(opener.hasFocus());
            assertEquals(0,
                new org.json.JSONObject(Diagnostics.incidentContext("operation_failed", new Diagnostics.UiSnapshot()))
                    .getInt("modal_depth"));
        } finally {
            dialog.dismiss();
            Diagnostics.setEnabled(activity, false);
            activity.finish();
        }
    }

    @Test
    public void detailsReaderRetainsSemanticOpenerAndModalContext() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        com.archos.mediacenter.video.leanback.details.PreviewMoviePage page =
            new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(
                activity, ArrayObjectAdapter::new, a -> {
                }, () -> {
                }, uri -> {
                });
        activity.setContentView(page);
        View opener = page.findViewWithTag("action:More");
        assertNotNull(opener);
        opener.requestFocus();
        assertEquals("semantic:details.action.more",
            opener.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic));
        Diagnostics.setEnabled(activity, true);
        Dialog dialog = null;
        try {
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(page, "moreInfo");
            dialog = org.robolectric.shadows.ShadowDialog.getLatestDialog();
            assertNotNull(dialog);
            assertEquals("reader",
                new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed", new Diagnostics.UiSnapshot()))
                    .getString("modal_kinds"));
            dialog.dismiss();
            org.robolectric.Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertTrue(opener.hasFocus());
        } finally {
            if (dialog != null)
                dialog.dismiss();
            Diagnostics.setEnabled(activity, false);
            activity.finish();
        }
    }

    @Test
    public void trailerModalIsCapturedAndDismissalRestoresItsOpener() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        Diagnostics.setEnabled(activity, true);
        Button opener = new Button(activity);
        opener.setFocusableInTouchMode(true);
        activity.setContentView(opener);
        opener.requestFocus();
        Dialog dialog = null;
        try {
            com.archos.mediacenter.video.leanback.details.PreviewTrailer.show(activity,
                new com.archos.mediascraper.ScraperTrailer(
                    com.archos.mediascraper.ScraperTrailer.Type.SHOW_TRAILER, "Trailer", "abcdefghijk", "YouTube", ""));
            dialog = org.robolectric.shadows.ShadowDialog.getLatestDialog();
            assertNotNull(dialog);
            assertEquals("trailer",
                new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed", new Diagnostics.UiSnapshot()))
                    .getString("modal_kinds"));
            dialog.dismiss();
            org.robolectric.Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertTrue(opener.hasFocus());
        } finally {
            if (dialog != null)
                dialog.dismiss();
            Diagnostics.setEnabled(activity, false);
            activity.finish();
        }
    }

    @Test
    public void nestedDialogSnapshotRetainsKindsAfterDismissal() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        Diagnostics.setEnabled(activity, true);
        Dialog parent = com.archos.mediacenter.video.leanback.PreviewDialog.create(activity, "reader"),
                           child = com.archos.mediacenter.video.leanback.PreviewDialog.create(activity, "choice");
        try {
            parent.show();
            child.show();
            Diagnostics.UiSnapshot captured = new Diagnostics.UiSnapshot();
            child.dismiss();
            parent.dismiss();
            org.json.JSONObject row = new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed", captured));
            assertEquals(2, row.getInt("modal_depth"));
            assertEquals("reader,choice", row.getString("modal_kinds"));
            row = new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed", new Diagnostics.UiSnapshot()));
            assertEquals(0, row.getInt("modal_depth"));
            assertEquals("none", row.getString("modal_kinds"));
        } finally {
            child.dismiss();
            parent.dismiss();
            Diagnostics.setEnabled(activity, false);
            activity.finish();
        }
    }

    @Test
    public void librarySnapshotRetainsSafeFilterValuesAndExactSort() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        try {
            Diagnostics.libraryState("movies", "list", "SIZE:descending", "Drama|private-filename.mkv",
                "2024|2025|secret", "8|9|https://private.example", true);
            Diagnostics.UiSnapshot captured = new Diagnostics.UiSnapshot();
            Diagnostics.uiState("settings", "playback", "workspace", "none", "none", 0);
            String text = Diagnostics.incidentContext("artwork_failed", captured);
            org.json.JSONObject row = new org.json.JSONObject(text);
            assertEquals("SIZE:descending", row.getString("sort"));
            assertFalse(text.contains("private"));
            assertFalse(text.contains("secret"));
            assertFalse(text.contains("Drama"));
            org.json.JSONObject filters = new org.json.JSONObject(row.getString("filter_state"));
            assertEquals(2, filters.getInt("genre_count"));
            assertEquals(64, filters.getString("genre_selection_id").length());
            assertEquals("[2024,2025]", filters.getJSONArray("years").toString());
            assertEquals("[8,9]", filters.getJSONArray("provider_ids").toString());
            assertTrue(filters.getBoolean("unmatched"));
            assertEquals("{}",
                new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed", new Diagnostics.UiSnapshot()))
                    .getString("filter_state"));
        } finally {
            Diagnostics.uiState("unknown", "none", "unknown", "unknown", "none", 0);
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void delayedIncidentKeepsFailureTimeState() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        try {
            Diagnostics.uiState("movies", "library", "list", "2", "genre,year", 42);
            Diagnostics.modalDepth(2);
            Diagnostics.UiSnapshot captured = new Diagnostics.UiSnapshot();
            Diagnostics.uiState("settings", "playback", "workspace", "none", "none", 0);
            Diagnostics.modalDepth(0);
            org.json.JSONObject value =
                new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed", captured));
            assertEquals("movies", value.getString("page"));
            assertEquals("list", value.getString("view_mode"));
            assertEquals(42, value.getLong("media_id"));
            assertEquals(2, value.getInt("modal_depth"));
            assertEquals(captured.utc, value.getLong("failure_utc_ms"));
        } finally {
            Diagnostics.uiState("unknown", "none", "unknown", "unknown", "none", 0);
            Diagnostics.modalDepth(0);
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void delayedIncidentDoesNotAttachToALaterPlaybackSession() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        try {
            Diagnostics.beginPlayback(Uri.parse("file:///private/first.mkv"));
            Diagnostics.UiSnapshot captured = new Diagnostics.UiSnapshot();
            Diagnostics.endPlayback("user_back");
            Diagnostics.beginPlayback(Uri.parse("file:///private/second.mkv"));
            assertNotEquals(captured.playbackSession, new Diagnostics.UiSnapshot().playbackSession);
            org.robolectric.Shadows.shadowOf(Looper.getMainLooper())
                .idleFor(Duration.ofMillis(40));
            String evidence = Diagnostics.incidentContext("playback_failed", captured);
            org.json.JSONObject value = new org.json.JSONObject(evidence);
            assertEquals(captured.playbackSession, value.getString("session"));
            assertEquals(captured.foregroundSession, value.getString("app_session"));
            assertEquals(captured.operation, value.getString("last_operation"));
            assertEquals(captured.utc, value.getLong("failure_utc_ms"));
            assertTrue(value.getLong("resource_sample_delay_ms") >= 40);
            assertTrue(value.has("resource_sample_utc_ms"));
            assertFalse(evidence.contains("private"));
        } finally {
            Diagnostics.endPlayback("fixture_complete");
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void incidentSnapshotContainsStructuralUiStateWithoutRawPaths() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        try {
            Diagnostics.uiState("movies", "library", "list", "2", "genre,year", 42);
            Diagnostics.modalDepth(2);
            Method snapshot = Diagnostics.class.getDeclaredMethod("incidentContext", String.class);
            snapshot.setAccessible(true);
            org.json.JSONObject value = new org.json.JSONObject((String) snapshot.invoke(null, "artwork_failed"));
            assertEquals("movies", value.getString("page"));
            assertEquals("list", value.getString("view_mode"));
            assertEquals("genre,year", value.getString("active_filters"));
            assertEquals(42, value.getLong("media_id"));
            assertEquals(2, value.getInt("modal_depth"));
            Diagnostics.uiState(
                "https://private.example/token=secret", "/storage/private.mkv", "grid", "none", "none", -1);
            Diagnostics.modalDepth(-1);
            String safe = (String) snapshot.invoke(null, "artwork_failed");
            assertFalse(safe.contains("private"));
            assertFalse(safe.contains("secret"));
            value = new org.json.JSONObject(safe);
            assertEquals(0, value.getLong("media_id"));
            assertEquals(0, value.getInt("modal_depth"));
        } finally {
            Diagnostics.uiState("unknown", "none", "unknown", "unknown", "none", 0);
            Diagnostics.modalDepth(0);
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void repeatedFailuresKeepRawEventsAndAddCorrelatedCumulativeSummaries() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        try {
            Method freeze = Diagnostics.class.getDeclaredMethod("freeze", String.class);
            freeze.setAccessible(true);
            freeze.invoke(null, "manual_problem_marker");
            freeze.invoke(null, "artwork_failed");
            freeze.invoke(null, "artwork_failed");
            freeze.invoke(null, "artwork_failed");
            List<org.json.JSONObject> records =
                DiagnosticArchive.records(new File(c.getFilesDir(), "supernova-diagnostics"));
            int repeats = 0;
            boolean summary = false;
            String incident = null;
            for (org.json.JSONObject row : records) {
                if ("incident_repeated".equals(row.optString("event"))
                    && "artwork_failed".equals(row.optString("reason"))) {
                    repeats++;
                    if (incident == null)
                        incident = row.getString("incident_id");
                    assertEquals(incident, row.getString("incident_id"));
                }
                if ("incident_burst_summary".equals(row.optString("event")) && row.optLong("occurrences") == 2)
                    summary = true;
            }
            assertEquals(3, repeats);
            assertTrue(summary);
        } finally {
            Diagnostics.setEnabled(c, false);
        }
    }

    @Test
    public void generatedFocusControlsAreDistinctWithoutText() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        LinearLayout parent = new LinearLayout(c);
        TextView a = new TextView(c), b = new TextView(c);
        a.setText("private title");
        b.setText("private query");
        parent.addView(a);
        parent.addView(b);
        Method id = Diagnostics.class.getDeclaredMethod("viewId", View.class);
        id.setAccessible(true);
        String first = (String) id.invoke(null, a), second = (String) id.invoke(null, b);
        assertNotEquals(first, second);
        assertFalse(first.contains("private"));
        assertFalse(second.contains("private"));
    }

    @Test
    public void rotatedFilesStayBoundedAndSessionsRemainCorrelated() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(c, true);
        Method write = Diagnostics.class.getDeclaredMethod("write", String.class, String.class);
        write.setAccessible(true);
        String payload = new String(new char[4000]).replace('\0', 'x');
        for (int i = 0; i < 500; i++)
            write.invoke(null,
                "{\"session\":\"fixture-session\",\"event\":\"bounded_test\",\"detail\":\"" + payload + "\"}\n",
                "fixture-session");
        File[] files = new File(c.getFilesDir(), "supernova-diagnostics").listFiles();
        assertNotNull(files);
        // Protected daily/incident streams have a separate budget and may already exist.
        long routine = Arrays.stream(files)
                           .filter(file -> file.getName().matches("(?:events|playback)\\.jsonl(?:\\.[1-4])?"))
                           .count();
        assertTrue(routine <= 7);
        for (File file : files)
            assertTrue(file.length() <= Diagnostics.LIMIT);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Diagnostics.export(c, bytes);
        boolean playback = false;
        try (ZipInputStream zip =
                 new ZipInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals("playback.jsonl")) {
                    ByteArrayOutputStream content = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = zip.read(buffer)) != -1)
                        content.write(buffer, 0, count);
                    assertTrue(content.toString("UTF-8").contains("fixture-session"));
                    playback = true;
                }
            }
        }
        assertTrue(playback);
        Diagnostics.setEnabled(c, false);
    }

    @Test
    public void locationsAndInlineSecretsAreRemoved() {
        String value = Diagnostics.safe(
            "https://user:password@example/private?token=secret /storage/private.mkv token=abc Bearer xyz");
        assertFalse(value.contains("example"));
        assertFalse(value.contains("private"));
        assertFalse(value.contains("abc"));
        assertFalse(value.contains("xyz"));
    }

    @Test
    public void exceptionMessagesNeverEnterReport() {
        RuntimeException error = new RuntimeException("password=extremely-private");
        String trace = Diagnostics.trace(error);
        assertTrue(trace.contains("RuntimeException"));
        assertFalse(trace.contains("extremely-private"));
    }

    @Test
    public void defaultsOnAndExportRemainsAvailableAfterPersistedOptOut() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(c).edit().remove(Diagnostics.KEY).commit();
        Diagnostics.reloadPolicy(c);
        assertTrue(Diagnostics.enabled());
        Diagnostics.setEnabled(c, false);
        Diagnostics.reloadPolicy(c);
        assertFalse(Diagnostics.enabled());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Diagnostics.export(c, bytes);
        assertTrue(bytes.size() > 0);
        Set<String> names = new HashSet<>();
        try (ZipInputStream zip =
                 new ZipInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null)
                names.add(entry.getName());
        }
        assertTrue(names.contains("build-device.json"));
        assertTrue(names.contains("decoders.txt"));
        assertTrue(names.contains("README.txt"));
    }
}
