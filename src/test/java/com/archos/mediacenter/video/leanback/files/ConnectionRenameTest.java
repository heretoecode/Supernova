package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/** M6 (owner, 26 Nov 2026): a connection renamed on its form shows the new name wherever it is named, at once. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ConnectionRenameTest
{
    @Test
    public void theNewNameIsFoundFromTheServerAndAnyFolderOnIt()
    {
        Context context = RuntimeEnvironment.getApplication();
        FilesPageData.rename(context, Uri.parse("sshj://seedbox.example:2222/downloads"), "Seedbox");
        FilesPageData.rename(context, Uri.parse("sshj://seedbox.example:2222/downloads"), "Garden seedbox");
        assertEquals("Garden seedbox", FilesPageData.givenName(context, Uri.parse("sftp://seedbox.example:2222/")));
        assertEquals("Garden seedbox",
                     FilesPageData.givenName(context, Uri.parse("sshj://seedbox.example:2222/tv/Show/S01E01.mkv")));
    }
}
