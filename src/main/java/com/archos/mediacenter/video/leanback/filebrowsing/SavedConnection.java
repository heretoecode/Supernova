package com.archos.mediacenter.video.leanback.filebrowsing;

import com.archos.filecorelibrary.connections.DraftConnectionTest;
import java.util.Locale;

/** Maps a saved connection's address scheme to the Add Connection form and the connection test. */
public final class SavedConnection
{
    private SavedConnection()
    {
    }

    /** Index into the form's protocol list (SMB, WebDAV HTTPS, WebDAV HTTP, SFTP, FTP, FTP TLS), or -1. */
    public static int protocolIndex(String scheme)
    {
        switch (scheme == null ? "" : scheme.toLowerCase(Locale.ROOT)) {
            case "smb":
            case "smbj":
                return 0;
            case "webdavs":
                return 1;
            case "webdav":
                return 2;
            case "sftp":
            case "sshj":
                return 3;
            case "ftp":
                return 4;
            case "ftps":
                return 5;
            default:
                return -1;
        }
    }

    public static DraftConnectionTest.Type type(int protocolIndex)
    {
        DraftConnectionTest.Type[] types = {
            DraftConnectionTest.Type.SMB, DraftConnectionTest.Type.WEBDAVS, DraftConnectionTest.Type.WEBDAV,
            DraftConnectionTest.Type.SFTP, DraftConnectionTest.Type.FTP, DraftConnectionTest.Type.FTPS
        };
        if (protocolIndex < 0 || protocolIndex >= types.length)
            return null;
        return types[protocolIndex];
    }
}
