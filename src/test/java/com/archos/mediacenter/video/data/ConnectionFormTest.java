package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

public class ConnectionFormTest
{
    @Test public void switchesUseTheActualEncryptedTransportAndCorrectDefaultPorts()
    {
        assertEquals(new ConnectionForm.Transport("webdavs", 443, true),
            ConnectionForm.transport(ConnectionForm.Kind.WEBDAV, true, 80, false));
        assertEquals(new ConnectionForm.Transport("webdav", 80, false),
            ConnectionForm.transport(ConnectionForm.Kind.WEBDAV, false, 443, true));
        assertEquals(new ConnectionForm.Transport("ftps", 21, true),
            ConnectionForm.transport(ConnectionForm.Kind.FTP, true, 0, false));
        assertEquals(new ConnectionForm.Transport("sftp", 22, true),
            ConnectionForm.transport(ConnectionForm.Kind.SFTP, false, 0, true));
    }

    @Test public void customPortsSurviveTheSwitchAndInvalidPortsAreRejected()
    {
        assertEquals(8443, ConnectionForm.transport(ConnectionForm.Kind.WEBDAV, false, 8443, true).port());
        assertThrows(IllegalArgumentException.class,
            () -> ConnectionForm.transport(ConnectionForm.Kind.FTP, true, 65536, false));
    }
}
