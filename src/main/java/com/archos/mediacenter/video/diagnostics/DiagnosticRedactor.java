package com.archos.mediacenter.video.diagnostics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Pattern;

/** Redaction is shared by messages, complete exception chains, settings and the report. */
public final class DiagnosticRedactor
{
    private static final String SECRET = "[a-z0-9_.-]*(?:password|passwd|token|secret|api[_-]?key|authorization|"
                                         + "cookie|credential|username|login)[a-z0-9_.-]*";
    private static final Pattern QUOTED =
        Pattern.compile("(?i)([\\\"']?(?:" + SECRET + ")[\\\"']?\\s*[:=]\\s*)([\\\"'])(?:\\\\.|(?!\\2)[\\s\\S])*\\2");
    private static final Pattern HEADER = Pattern.compile(
        "(?im)(authorization\\s*[:=]\\s*)(?:bearer|basic)\\s+[^\\r\\n,;]+|\\b(?:bearer|basic)\\s+[A-Za-z0-9+/=_~.-]+");
    private static final Pattern PAIR =
        Pattern.compile("(?i)([\\\"']?(?:" + SECRET + ")[\\\"']?\\s*[:=]\\s*)[^\\s,;\\\"'}]+");
    private static final int MAX = 256 * 1024;

    private DiagnosticRedactor()
    {
    }

    public static String clean(String value)
    {
        if (value == null)
            return "";
        String clean = value.replaceAll("(?i)[a-z][a-z0-9+.-]*://[^\\s\\\"'<>]+", "[location]")
                           .replaceAll("(?m)(?:^|(?<=\\s))/[^\\s]+", "[path]");
        clean = QUOTED.matcher(clean).replaceAll("$1\"[redacted]\"");
        clean = HEADER.matcher(clean).replaceAll("[redacted]");
        clean = PAIR.matcher(clean).replaceAll("$1[redacted]");
        return clean.length() > MAX ? clean.substring(0, MAX) + "\n[diagnostic text truncated at 256 KiB]" : clean;
    }

    public static String trace(Throwable failure)
    {
        if (failure == null)
            return "";
        StringWriter output = new StringWriter();
        failure.printStackTrace(new PrintWriter(output));
        return clean(output.toString());
    }

    public static boolean secretSetting(String key)
    {
        return key == null || key.toLowerCase(Locale.ROOT)
                                  .matches(".*(?:password|passwd|token|secret|api.?key|authorization|cookie|"
                                           + "credential|username|login|account).*");
    }

    public static Object settingValue(String key, Object value)
    {
        if (secretSetting(key))
            return "[redacted]";
        return value instanceof Number || value instanceof Boolean ? value : clean(String.valueOf(value));
    }
}
