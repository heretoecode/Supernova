package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.archos.filecorelibrary.FileEditor;
import com.archos.filecorelibrary.FileEditorFactory;
import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.ScraperStore;
import com.archos.mediaprovider.video.VideoStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;
import org.slf4j.LoggerFactory;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PosterLogPrivacy157Test
{
    @Test public void failedPosterChecksStillClearTheCacheWithoutLoggingThePathTitleOrException() throws Exception
    {
        Logger logger = (Logger)LoggerFactory.getLogger(UnavailablePosterBroadcastReceiver.class);
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        logger.addAppender(captured);
        logger.setLevel(Level.DEBUG);
        Context context = mock(Context.class);
        ContentResolver resolver = mock(ContentResolver.class);
        when(context.getContentResolver()).thenReturn(resolver);
        Uri original = Uri.parse("smb://PRIVATE_USER:PRIVATE_PASSWORD@PRIVATE_HOST/PRIVATE_PATH");
        MatrixCursor row = new MatrixCursor(new String[] {
            "cover", VideoStore.Video.VideoColumns.TITLE, VideoStore.Video.VideoColumns.SCRAPER_EPISODE_ID,
            VideoStore.Video.VideoColumns.SCRAPER_MOVIE_ID, VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE
        });
        row.addRow(new Object[] {original.toString(), "PRIVATE_TITLE", -1L, 42L, 0});
        when(resolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), isNull()))
                .thenReturn(row);
        FileEditor editor = mock(FileEditor.class);
        when(editor.exists()).thenThrow(new RuntimeException("PRIVATE_EXCEPTION"));
        try (var factories = mockStatic(FileEditorFactory.class); var policy = mockStatic(LoaderUtils.class))
        {
            factories.when(() -> FileEditorFactory.getFileEditorForUrl(original, null)).thenReturn(editor);
            ReflectionHelpers.callStaticMethod(UnavailablePosterBroadcastReceiver.class, "checkPoster",
                    ClassParameter.from(Context.class, context), ClassParameter.from(long.class, 123L));
            ArgumentCaptor<ContentValues> values = ArgumentCaptor.forClass(ContentValues.class);
            verify(resolver).update(eq(ContentUris.withAppendedId(ScraperStore.Movie.URI.ID, 42)), values.capture(),
                                    isNull(), isNull());
            assertEquals(Long.valueOf(-1), values.getValue().getAsLong(ScraperStore.Movie.POSTER_ID));
            assertNull(values.getValue().get(ScraperStore.Movie.COVER));
            assertFalse(captured.list.isEmpty());
            for (ILoggingEvent event : captured.list)
            {
                assertFalse(event.getFormattedMessage().contains("PRIVATE_"));
                if (event.getArgumentArray() != null)
                    for (Object argument : event.getArgumentArray())
                        assertFalse(String.valueOf(argument).contains("PRIVATE_"));
                assertNull(event.getThrowableProxy());
            }
        }
        finally
        {
            logger.detachAppender(captured);
            logger.setLevel(previous);
            captured.stop();
        }
    }
}
