package com.archos.mediacenter.video.diagnostics;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Item 9 (owner, 26 Nov 2026): diagnostics record why, not just what. Every failure carries its reason; where it
 * truly isn't known the record says so and keeps what was attempted and the last thing that worked. The summary lists
 * the top reasons. A failure event written in the code without a reason fails the build.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DiagnosticReasonTest
{
    @Test
    public void aCaughtExceptionIsItsReason() throws Exception
    {
        JSONObject record = new JSONObject();
        record.put("failure_class", "java.io.IOException");
        record.put("message", "Server refused");
        Diagnostics.reason(record, "delete_failed");
        assertEquals("IOException: Server refused", record.getString("reason"));
    }

    @Test
    public void anUnknownReasonSaysSoAndKeepsWhatWasTried() throws Exception
    {
        JSONObject record = new JSONObject();
        record.put("failure_class", "not_supplied");
        record.put("duration_ms", 4200);
        Diagnostics.reason(record, "scan_failed");
        assertEquals("not_known", record.getString("reason"));
        assertEquals("scan_failed", record.getString("attempted"));
        assertEquals(4200, record.getInt("waited_ms"));
        assertTrue(record.has("last_worked"));
    }

    @Test
    public void aGivenReasonStays() throws Exception
    {
        JSONObject record = new JSONObject();
        record.put("reason", "not_found");
        Diagnostics.reason(record, "delete_failed");
        assertEquals("not_found", record.getString("reason"));
    }

    @Test
    public void summaryNamesTheReasonAndTheServersAnswer() throws Exception
    {
        JSONObject record = new JSONObject();
        record.put("event", "delete_failed");
        record.put("reason", "refused");
        record.put("http_status", 403);
        assertEquals("Delete failed ×: refused (403)", DiagnosticHealthSummary.reasonLine(record));
        List<JSONObject> records = new ArrayList<>();
        for (int i = 0; i < 6; i++)
        {
            JSONObject copy = new JSONObject(record.toString());
            copy.put("severity", "ERROR");
            records.add(copy);
        }
        String summary = DiagnosticHealthSummary.build(records);
        assertTrue(summary, summary.contains("Delete failed ×6: refused (403)"));
    }

    /** D15: one action writes one event; the same event heard twice at once is kept once. */
    @Test
    public void oneActionWritesOneEvent()
    {
        android.content.Context app = org.robolectric.RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        try
        {
            int before = Diagnostics.DUPLICATES.get();
            Diagnostics.event("focus_moved_test", "target", "card", "row", 2);
            Diagnostics.event("focus_moved_test", "target", "card", "row", 2);
            assertEquals(before + 1, Diagnostics.DUPLICATES.get());
            Diagnostics.event("focus_moved_test", "target", "card", "row", 3);
            assertEquals("A different event is recorded", before + 1, Diagnostics.DUPLICATES.get());
        }
        finally
        {
            Diagnostics.setEnabled(app, false);
        }
    }

    /** Every failure written with Diagnostics.event names its reason (Diagnostics.error carries the exception). */
    @Test
    public void everyFailureEventInTheCodeHasAReason() throws Exception
    {
        Pattern call = Pattern.compile("Diagnostics\\.event\\(\\s*\"([a-z_0-9]+)\"");
        List<String> missing = new ArrayList<>();
        Path root = Paths.get("src/main/java");
        try (Stream<Path> files = Files.walk(root))
        {
            for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".java"))::iterator)
            {
                String source = new String(Files.readAllBytes(file), java.nio.charset.StandardCharsets.UTF_8);
                Matcher m = call.matcher(source);
                while (m.find())
                {
                    String name = m.group(1);
                    if (!"ERROR".equals(Diagnostics.severity(name)) && !"FATAL".equals(Diagnostics.severity(name)))
                        continue;
                    int depth = 1;
                    int i = m.end();
                    while (depth > 0 && i < source.length())
                    {
                        char c = source.charAt(i++);
                        depth += c == '(' ? 1 : c == ')' ? -1 : 0;
                    }
                    if (!source.substring(m.start(), i).contains("\"reason\""))
                        missing.add(root.relativize(file) + ": " + name);
                }
            }
        }
        assertEquals("Failures recorded without a reason (item 9): " + missing, 0, missing.size());
    }
}
