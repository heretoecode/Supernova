package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Checks diagnostic storage bounds, complete-record rotation and cleanup after export failures. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class DiagnosticStorageTest
{
    private Application app;
    private File directory;

    @Before public void setup()
    {
        app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        directory = new File(app.getFilesDir(), "supernova-diagnostics");
        directory.mkdirs();
    }

    @After public void teardown() throws Exception
    {
        flush();
        Diagnostics.setEnabled(app, false);
    }

    private void flush() throws Exception
    {
        for (String name : List.of("IMPORTANT", "WORK"))
        {
            ThreadPoolExecutor executor = ReflectionHelpers.getStaticField(Diagnostics.class, name);
            executor.submit(() -> {}).get(10, TimeUnit.SECONDS);
        }
    }

    private void noSnapshots()
    {
        File[] files = app.getCacheDir().listFiles(f -> f.getName().startsWith("supernova-diagnostic-export-"));
        assertNotNull(files);
        assertEquals(0, files.length);
    }

    @Test public void productionWriteEnforcesGlobalCapAndPreservesUnownedFiles() throws Exception
    {
        flush();
        long now = System.currentTimeMillis();
        for (int day = 1; day <= 40; day++)
        {
            File file = new File(directory, String.format(Locale.ROOT, "important-202610%02d.jsonl", day));
            byte[] data = new byte[Diagnostics.LIMIT];
            Arrays.fill(data, (byte)' ');
            Files.write(file.toPath(), data);
            assertTrue(file.setLastModified(now - 40000 + day));
        }
        File unowned = new File(directory, "unrelated-private.txt");
        Files.writeString(unowned.toPath(), "UNOWNED_CANARY");
        Diagnostics.event("storage_probe", "value", 1);
        flush();
        long bytes = 0;
        for (File file : DiagnosticArchive.files(directory))
            bytes += file.length();
        assertTrue(bytes <= Diagnostics.STORAGE_LIMIT);
        assertEquals("UNOWNED_CANARY", Files.readString(unowned.toPath()));
        assertTrue(
            DiagnosticArchive.records(directory).stream().anyMatch(x -> x.optString("event").equals("storage_probe")));
    }

    @Test public void blockedZipDestinationDoesNotHoldRecorderWriteLockOrChangeItsSnapshot() throws Exception
    {
        Diagnostics.event("snapshot_before");
        flush();
        CountDownLatch writing = new CountDownLatch(1), release = new CountDownLatch(1);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        OutputStream slow = new OutputStream() {
            boolean first = true;
            @Override public void write(int value) throws IOException
            {
                write(new byte[] {(byte)value}, 0, 1);
            }
            @Override public void write(byte[] data, int start, int length) throws IOException
            {
                if (first)
                {
                    first = false;
                    writing.countDown();
                    try
                    {
                        if (!release.await(10, TimeUnit.SECONDS))
                            throw new IOException("Timed out test gate");
                    }
                    catch (InterruptedException interrupted)
                    {
                        throw new IOException(interrupted);
                    }
                }
                bytes.write(data, start, length);
            }
        };
        ExecutorService export = Executors.newSingleThreadExecutor();
        Future<?> result = export.submit(() -> {
            try
            {
                Diagnostics.export(app, slow);
            }
            catch (IOException failure)
            {
                throw new RuntimeException(failure);
            }
        });
        try
        {
            assertTrue(writing.await(10, TimeUnit.SECONDS));
            Diagnostics.event("snapshot_after");
            ThreadPoolExecutor work = ReflectionHelpers.getStaticField(Diagnostics.class, "WORK");
            work.submit(() -> {}).get(3, TimeUnit.SECONDS);
            assertTrue(DiagnosticArchive.records(directory).stream().anyMatch(
                x -> x.optString("event").equals("snapshot_after")));
            release.countDown();
            result.get(10, TimeUnit.SECONDS);
            String copiedEvents = "";
            try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes.toByteArray())))
            {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null)
                {
                    String text = new String(zip.readAllBytes(), StandardCharsets.UTF_8);
                    if (entry.getName().equals("events.jsonl"))
                        copiedEvents = text;
                }
            }
            assertTrue(copiedEvents.contains("snapshot_before"));
            assertFalse(copiedEvents.contains("snapshot_after"));
            noSnapshots();
        }
        finally
        {
            release.countDown();
            export.shutdownNow();
        }
    }

    @Test public void destinationFailureCleansOwnedSnapshotAndRetainsExistingLogsForRetry() throws Exception
    {
        Diagnostics.event("retryable_export_probe");
        flush();
        try
        {
            Diagnostics.export(app, new OutputStream() {
                @Override public void write(int value) throws IOException
                {
                    throw new IOException("Destination unavailable");
                }
            });
            fail();
        }
        catch (IOException expected)
        {
        }
        noSnapshots();
        assertTrue(DiagnosticArchive.records(directory).stream().anyMatch(
            x -> x.optString("event").equals("retryable_export_probe")));
        ByteArrayOutputStream retry = new ByteArrayOutputStream();
        Diagnostics.export(app, retry);
        assertTrue(retry.size() > 0);
        noSnapshots();
    }

    @Test public void oversizedProducerRecordIsDroppedRatherThanCreatingAnUnparseableOrOversizedFile() throws Exception
    {
        flush();
        long before = ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "DROPPED")).get();
        long writeErrors = ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "WRITE_ERRORS")).get();
        DiagnosticFlightRecorder recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
        int flightBytes = recorder.bytes();
        Object[] fields = new Object[40];
        for (int i = 0; i < fields.length; i += 2)
        {
            fields[i] = "field_" + (char)('a' + i / 2);
            // Below the former 1 MiB producer cap, but too large for a single retained file.
            fields[i + 1] = "x".repeat(Diagnostics.LIMIT / 8);
        }
        Diagnostics.event("oversized_probe", fields);
        flush();
        assertEquals(before + 1, ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "DROPPED")).get());
        assertEquals(writeErrors,
                     ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "WRITE_ERRORS")).get());
        assertEquals(flightBytes, recorder.bytes());
        Set<String> coverage = ReflectionHelpers.getStaticField(Diagnostics.class, "COVERAGE");
        assertFalse(coverage.contains("oversized_probe"));
        assertFalse(DiagnosticArchive.records(directory).stream().anyMatch(
            x -> x.optString("event").equals("oversized_probe")));
        for (File file : DiagnosticArchive.files(directory))
            assertTrue(file.length() <= Diagnostics.LIMIT);
    }

    @Test public void producerRetainsDetailedRecordsLargerThanTheOld32KiBLimit() throws Exception
    {
        flush();
        String detail = "x".repeat(80 * 1024);
        long before = ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "DROPPED")).get();
        long writeErrors = ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "WRITE_ERRORS")).get();
        Diagnostics.event("detailed_probe", "detail", detail);
        flush();
        JSONObject record = DiagnosticArchive.records(directory)
                                .stream()
                                .filter(row -> row.optString("event").equals("detailed_probe"))
                                .findFirst()
                                .orElseThrow();
        assertEquals(detail, record.getString("detail"));
        assertEquals(before, ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "DROPPED")).get());
        assertEquals(writeErrors,
                     ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "WRITE_ERRORS")).get());
        for (File file : DiagnosticArchive.files(directory))
            assertTrue(file.length() <= Diagnostics.LIMIT);
    }

    @Test public void producerMeasuresUtf8BytesRatherThanCharactersBeforeQueueing() throws Exception
    {
        flush();
        String detail = "\u20ac".repeat(Diagnostics.LIMIT / 2);
        assertTrue(detail.length() < Diagnostics.LIMIT);
        long before = ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "DROPPED")).get();
        long writeErrors = ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "WRITE_ERRORS")).get();
        Diagnostics.event("utf8_oversized_probe", "detail", detail);
        flush();
        assertEquals(before + 1, ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "DROPPED")).get());
        assertEquals(writeErrors,
                     ((AtomicLong)ReflectionHelpers.getStaticField(Diagnostics.class, "WRITE_ERRORS")).get());
        assertFalse(DiagnosticArchive.records(directory).stream().anyMatch(
            row -> row.optString("event").equals("utf8_oversized_probe")));
        for (File file : DiagnosticArchive.files(directory))
            assertTrue(file.length() <= Diagnostics.LIMIT);
    }

    @Test public void largeIncidentPayloadRotatesOnlyAtCompleteRecordBoundaries() throws Exception
    {
        flush();
        StringBuilder payload = new StringBuilder();
        for (int i = 0; i < 500; i++)
            payload.append(new JSONObject().put("event", "large_fixture").put("index", i).put("data", "x".repeat(600)))
                .append('\n');
        ReflectionHelpers.callStaticMethod(
            Diagnostics.class, "append", ReflectionHelpers.ClassParameter.from(File.class, directory),
            ReflectionHelpers.ClassParameter.from(String.class, "incident-manual-20261013.jsonl"),
            ReflectionHelpers.ClassParameter.from(int.class, 3),
            ReflectionHelpers.ClassParameter.from(byte[].class, payload.toString().getBytes(StandardCharsets.UTF_8)));
        int rows = 0;
        for (File file : DiagnosticArchive.files(directory))
            if (file.getName().startsWith("incident-manual-20261013"))
            {
                assertTrue(file.length() <= Diagnostics.LIMIT);
                for (String line : Files.readAllLines(file.toPath()))
                {
                    if (!line.isEmpty())
                    {
                        new JSONObject(line);
                        rows++;
                    }
                }
            }
        assertEquals(500, rows);
    }
}
