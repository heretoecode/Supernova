package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SubtitleFontBoundary157Test
{
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void providerDisplayNamesCannotEscapeTheFontStore() throws Exception
    {
        File files = temporary.newFolder("files");
        File outside = new File(files, "escape.ttf");
        Files.writeString(outside.toPath(), "retained");
        Context context = mock(Context.class);
        ContentResolver resolver = mock(ContentResolver.class);
        when(context.getFilesDir()).thenReturn(files);
        when(context.getContentResolver()).thenReturn(resolver);
        MatrixCursor rows = new MatrixCursor(new String[] {
            DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE, DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        });
        for (String name : new String[] {"../escape.ttf", "..\\escape.ttf", outside.getAbsolutePath(), "bad\0.ttf"})
            rows.addRow(new Object[] {name, name, "font/ttf", 4L, 1L});
        rows.addRow(new Object[] {"valid", "Écriture.ttf", "font/ttf", 4L, 1L});
        when(resolver.query(any(Uri.class), any(String[].class), isNull(), isNull(), isNull())).thenReturn(rows);
        Uri tree = Uri.parse("content://test.fonts/tree/fonts");
        Uri valid = DocumentsContract.buildDocumentUriUsingTree(tree, "valid");
        when(resolver.openInputStream(valid)).thenReturn(new ByteArrayInputStream("font".getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, SubtitleFontsFolderSync.syncFromTree(context, tree));
        verify(resolver).openInputStream(valid);
        assertEquals("retained", Files.readString(outside.toPath()));
        assertEquals("font", Files.readString(new File(files, "subtitle_fonts/Écriture.ttf").toPath()));
        assertFalse(new File(files, "subtitle_fonts/..\\escape.ttf").exists());
    }
}
