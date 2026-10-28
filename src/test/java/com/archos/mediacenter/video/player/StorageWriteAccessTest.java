package com.archos.mediacenter.video.player;

import android.content.ContentResolver;
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
@Config(sdk = 30, application = android.app.Application.class)
public class StorageWriteAccessTest {
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
