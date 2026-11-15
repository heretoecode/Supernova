package com.archos.mediacenter.video.data;

/** Form transport choices. Port changes follow defaults only until the owner enters a custom port. */
public final class ConnectionForm
{
    public enum Kind
    {
        SMB, WEBDAV, SFTP, FTP
    }

    public record Transport(String scheme, int port, boolean encrypted)
    {
    }

    public static Transport transport(Kind kind, boolean encryption, int previousPort, boolean previousEncryption)
    {
        int oldDefault = port(kind, previousEncryption);
        int selected = previousPort <= 0 || previousPort == oldDefault ? port(kind, encryption) : previousPort;
        if (selected > 65535)
            throw new IllegalArgumentException("Invalid connection port");
        return new Transport(switch (kind)
        {
            case SMB -> "smb";
            case SFTP -> "sftp";
            case FTP -> encryption ? "ftps" : "ftp";
            case WEBDAV -> encryption ? "webdavs" : "webdav";
        }, selected, kind == Kind.SFTP || kind != Kind.SMB && encryption);
    }

    private static int port(Kind kind, boolean encryption)
    {
        return switch (kind)
        {
            case SMB -> 445;
            case SFTP -> 22;
            case FTP -> 21; // The existing FTPS transport uses explicit TLS (AUTH TLS), not implicit port 990.
            case WEBDAV -> encryption ? 443 : 80;
        };
    }

    private ConnectionForm()
    {
    }
}
