package com.archos.mediacenter.video.player;

import android.content.ContentResolver;
import android.os.Environment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import com.archos.filecorelibrary.localstorage.ExternalSDFileWriter;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, application = android.app.Application.class, shadows = StorageWriteAccessTest.DeniedStorage.class)
public class StorageWriteAccessTest {
    // Model denied all-files access explicitly. SDK 30's unshadowed no-arg
    // framework method indexes an external-volume array absent in this fixture.
    @Implements(Environment.class)
    public static class DeniedStorage extends org.robolectric.shadows.ShadowEnvironment {
        @Implementation protected static boolean isExternalStorageManager() { return false; }
    }
    @Rule public TemporaryFolder temporary = new TemporaryFolder();
    @Test public void deniedWriteNeverMutatesMediaStore() throws Exception {
        ContentResolver resolver = mock(ContentResolver.class);
        ExternalSDFileWriter writer = new ExternalSDFileWriter(resolver, new File(temporary.getRoot(), "video.mkv"));
        try { writer.write(); fail("must reject without legacy access"); }
        catch (IOException expected) { }
        verifyNoInteractions(resolver);
    }
    @Test public void deniedMkdirNeverMutatesMediaStore() throws Exception {
        ContentResolver resolver = mock(ContentResolver.class);
        ExternalSDFileWriter writer = new ExternalSDFileWriter(resolver, new File(temporary.getRoot(), "folder"));
        assertFalse(writer.mkdir());
        verifyNoInteractions(resolver);
    }
    @Test public void deniedFolderIndexNeverMutatesMediaStore() throws Exception {
        ContentResolver resolver = mock(ContentResolver.class);
        ExternalSDFileWriter writer = new ExternalSDFileWriter(resolver, temporary.newFolder());
        writer.addFolderToDB();
        verifyNoInteractions(resolver);
    }
}
