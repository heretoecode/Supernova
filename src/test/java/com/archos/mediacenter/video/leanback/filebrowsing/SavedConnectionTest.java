package com.archos.mediacenter.video.leanback.filebrowsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.archos.filecorelibrary.connections.DraftConnectionTest.Type;
import org.junit.Test;

public class SavedConnectionTest
{
    @Test
    public void everySavedSchemeMapsToTheFormAndTheTest()
    {
        String[] schemes = {"smb", "smbj", "webdavs", "webdav", "sftp", "sshj", "ftp", "ftps"};
        int[] index = {0, 0, 1, 2, 3, 3, 4, 5};
        Type[] types = {Type.SMB, Type.SMB, Type.WEBDAVS, Type.WEBDAV, Type.SFTP, Type.SFTP, Type.FTP, Type.FTPS};
        for (int i = 0; i < schemes.length; i++) {
            assertEquals(schemes[i], index[i], SavedConnection.protocolIndex(schemes[i]));
            assertEquals(schemes[i], types[i], SavedConnection.type(SavedConnection.protocolIndex(schemes[i])));
        }
        assertEquals(1, SavedConnection.protocolIndex("WEBDAVS"));
    }

    @Test
    public void otherSchemesHaveNoFormOrTest()
    {
        assertEquals(-1, SavedConnection.protocolIndex("upnp"));
        assertEquals(-1, SavedConnection.protocolIndex(null));
        assertNull(SavedConnection.type(-1));
        assertNull(SavedConnection.type(6));
    }
}
