package com.archos.mediacenter.video.diagnostics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Redaction is shared by messages, complete exception chains, settings and the report. */
public final class DiagnosticRedactor
{
    private static final Pattern SECRET =
        Pattern.compile("(?i)password|passwd|token|secret|api[_-]?key|authorization|cookie|credential|username|login");
    private static final Pattern LOCATION = Pattern.compile("(?i)(?<![a-z0-9+.-])[a-z0-9+.-]++://[^\\s\\\"'<>]+");
    private static final Pattern ASSIGNMENT =
        Pattern.compile("(?i)(?<![a-z0-9_.-])([\\\"']?([a-z0-9_.-]++)[\\\"']?\\s*[:=]\\s*)");
    private static final Pattern HEADER = Pattern.compile(
        "(?im)(authorization\\s*[:=]\\s*)(?:bearer|basic)\\s+[^\\r\\n,;]+|\\b(?:bearer|basic)\\s+[A-Za-z0-9+/=_~.-]+");
    private static final int MAX = 256 * 1024;

    private DiagnosticRedactor()
    {
    }

    public static String clean(String value)
    {
        if (value == null)
            return "";
        String clean =
            LOCATION.matcher(value).replaceAll("[location]").replaceAll("(?m)(?:^|(?<=\\s))/[^\\s]+", "[path]");
        clean = HEADER.matcher(clean).replaceAll("[redacted]");
        clean = redactAssignments(clean);
        return clean.length() > MAX ? clean.substring(0, MAX) + "\n[diagnostic text truncated at 256 KiB]" : clean;
    }

    /** Scan each key/value once; long words and quoted values must not trigger regex backtracking. */
    private static String redactAssignments(String value)
    {
        Matcher assignment = ASSIGNMENT.matcher(value);
        StringBuilder clean = new StringBuilder(value.length());
        int copied = 0;
        while (assignment.find())
        {
            if (!SECRET.matcher(assignment.group(2)).find())
                continue;
            int start = assignment.end();
            if (start == value.length())
                continue;
            int end = start;
            char quote = value.charAt(start);
            boolean quoted = quote == '\"' || quote == '\'';
            if (quoted)
            {
                end++;
                while (end < value.length())
                {
                    char next = value.charAt(end++);
                    if (next == '\\' && end < value.length())
                        end++;
                    else if (next == quote)
                        break;
                }
            }
            else
            {
                while (end < value.length() && !Character.isWhitespace(value.charAt(end)) &&
                       ",;\"'}".indexOf(value.charAt(end)) < 0)
                    end++;
            }
            clean.append(value, copied, start).append(quoted ? "\"[redacted]\"" : "[redacted]");
            copied = end;
            assignment.region(end, value.length());
        }
        return clean.append(value, copied, value.length()).toString();
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
