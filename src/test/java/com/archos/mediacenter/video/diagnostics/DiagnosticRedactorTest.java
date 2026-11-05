package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Full-detail inputs must remain cheap to sanitise, including hostile long keys and values. */
public class DiagnosticRedactorTest
{
    @Test(timeout = 3000) public void longUnbrokenDiagnosticTextIsPreservedWithoutBacktracking()
    {
        String text = "x".repeat(80 * 1024);
        assertEquals(text, DiagnosticRedactor.clean(text));
        String repeatedKeys = "token".repeat(16 * 1024);
        assertEquals(repeatedKeys, DiagnosticRedactor.clean(repeatedKeys));
    }

    @Test(timeout = 3000) public void longQuotedSecretsAndQualifiedKeysAreRedactedWithoutRecursion()
    {
        String key = "x".repeat(80 * 1024) + ".password";
        String clean = DiagnosticRedactor.clean(key + "=\""
                                                + "CANARY".repeat(16 * 1024) + "\" remaining=42");
        assertEquals(key + "=\"[redacted]\" remaining=42", clean);
        assertFalse(clean.contains("CANARY"));
    }

    @Test public void escapedQuotesAndNestedSecretAssignmentsDoNotLeakValues()
    {
        String clean = DiagnosticRedactor.clean("{\"settings\":{\"api_key\":\"first\\\"SECOND_CANARY\","
                                                + "\"count\":2},\"message\":\"failed password=THIRD_CANARY\"}");
        assertFalse(clean.contains("first"));
        assertFalse(clean.contains("SECOND_CANARY"));
        assertFalse(clean.contains("THIRD_CANARY"));
        assertTrue(clean.contains("\"count\":2"));
        assertTrue(clean.contains("failed"));
    }

    @Test public void bearerHeadersAndUnterminatedQuotedSecretsRemainPrivate()
    {
        String clean = DiagnosticRedactor.clean("Authorization: Bearer HEADER_CANARY\npassword='UNCLOSED_CANARY");
        assertFalse(clean.contains("HEADER_CANARY"));
        assertFalse(clean.contains("UNCLOSED_CANARY"));
        assertTrue(clean.contains("[redacted]"));
    }

    @Test public void locationsAreRemovedEvenAfterNumericPrefixes()
    {
        String clean = DiagnosticRedactor.clean("failed 42https://user:AUTH_CANARY@host/FILE_CANARY"
                                                + "?token=QUERY_CANARY /storage/PATH_CANARY");
        for (String canary : new String[] {"AUTH_CANARY", "FILE_CANARY", "QUERY_CANARY", "PATH_CANARY"})
            assertFalse(clean.contains(canary));
        assertTrue(clean.contains("failed"));
    }
}
