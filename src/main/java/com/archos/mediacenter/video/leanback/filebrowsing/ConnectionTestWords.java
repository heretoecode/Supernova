package com.archos.mediacenter.video.leanback.filebrowsing;

import com.archos.filecorelibrary.connections.DraftConnectionTest;

/** Plain words for a Test connection result (mock-ups add-connection/03 and 04). */
public final class ConnectionTestWords
{
    private ConnectionTestWords()
    {
    }

    public static String message(boolean cancelled, DraftConnectionTest.Field field, String reason, int folders,
                                 int videos)
    {
        if (cancelled)
            return "Test stopped.";
        if (field == null || field == DraftConnectionTest.Field.NONE)
            return "Connected · " + count(folders, "folder") + ", " + count(videos, "video") + " found.";
        String detail = reason == null || reason.isEmpty() ? "" : " " + sentence(reason);
        return check(field) + detail;
    }

    private static String check(DraftConnectionTest.Field field)
    {
        switch (field) {
            case PORT:
                return "Check the port.";
            case SIGN_IN:
                return "Check the user name and password.";
            case FOLDER:
                return "Check the folder.";
            case CERTIFICATE:
                return "The server's certificate was not accepted.";
            default:
                return "Check the address.";
        }
    }

    static String count(int n, String word)
    {
        return n + " " + word + (n == 1 ? "" : "s");
    }

    private static String sentence(String reason)
    {
        String trimmed = reason.trim();
        if (trimmed.endsWith(".") || trimmed.endsWith("!") || trimmed.endsWith("?"))
            return trimmed;
        return trimmed + ".";
    }
}
