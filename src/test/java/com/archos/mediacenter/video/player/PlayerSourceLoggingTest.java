package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.ContentResolver;
import android.database.MatrixCursor;
import com.archos.mediaprovider.video.VideoStore;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.utils.CodecDiscovery;
import com.archos.medialib.IMediaPlayer;
import com.archos.medialib.MediaFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

/** DP-08: exercise both real source log sites and the actual source worker. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, manifest = Config.NONE, sdk = 30)
@LooperMode(LooperMode.Mode.PAUSED)
public class PlayerSourceLoggingTest {
    @Test
    public void debugAndInfoLogsContainOnlyBoundedProtocolAndOutcome() throws Exception {
        assertSourcesRemainPrivate(Level.DEBUG);
    }

    @Test
    public void normalInfoLoggingCannotDiscloseTheSourceEither() throws Exception {
        assertSourcesRemainPrivate(Level.INFO);
    }

    private void assertSourcesRemainPrivate(Level level) throws Exception {
        String[][] fixtures = {
            {"https://private-user:private-password@private-host.invalid/private-title.mkv", "https"},
            {"https://private-host.invalid/private-title.mkv?X-Amz-Signature=private-signature&token=private-token", "https"},
            {"https://private-host.invalid/private-title.mkv#private-fragment", "https"},
            {"file:///storage/emulated/0/Private%20Movies/private-title.mkv", "local"},
            {"/storage/private-title.mkv", "local"},
            {"PRIVATE-SCHEME://private-host.invalid/private-title.mkv", "other"}
        };
        Logger logger = (Logger) LoggerFactory.getLogger(Player.class);
        Level previousLevel = logger.getLevel();
        int previousDoViMode = CodecDiscovery.getDoViMode();
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        logger.addAppender(captured);
        logger.setLevel(level);
        try {
            for (String[] fixture : fixtures) {
                captured.list.clear();
                openSourceUnchanged(Uri.parse(fixture[0]));
                List<String> messages = captured.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
                assertEquals(level == Level.DEBUG ? 2 : 1, messages.size());
                if (level == Level.DEBUG) {
                    assertEquals("setVideoURI protocol=" + fixture[1] + " outcome=received", messages.get(0));
                }
                assertEquals("openVideo protocol=" + fixture[1] + " outcome=attempt",
                        messages.get(messages.size() - 1));
                for (ILoggingEvent event : captured.list) {
                    assertFalse(event.getFormattedMessage().contains("private"));
                    assertFalse(event.getFormattedMessage().contains("PRIVATE"));
                    assertFalse(event.getFormattedMessage().contains(fixture[0]));
                    // Redaction must happen before handing arguments to any appender.
                    assertEquals(1, event.getArgumentArray().length);
                    assertEquals(fixture[1], event.getArgumentArray()[0]);
                }
            }
        } finally {
            logger.detachAppender(captured);
            captured.stop();
            logger.setLevel(previousLevel);
            CodecDiscovery.setDoViMode(previousDoViMode);
        }
    }

    @Test public void providerOwnershipResolutionRunsOnTheSourceWorkerAndKeepsTheOriginalOpenUri() throws Exception {
        Context app = RuntimeEnvironment.getApplication();
        ContentResolver resolver = mock(ContentResolver.class);
        AtomicReference<Thread> queried = new AtomicReference<>();
        when(resolver.query(any(Uri.class), any(String[].class), nullable(String.class), nullable(String[].class),
                            nullable(String.class))).thenAnswer(call -> {
            queried.set(Thread.currentThread());
            MatrixCursor row = new MatrixCursor(new String[] {"_data"});
            row.addRow(new Object[] {"smb://nas/relocated.mkv"});
            return row;
        });
        Context context = new ContextWrapper(app) {
            @Override public ContentResolver getContentResolver() {
                return resolver;
            }
        };
        openSourceUnchanged(Uri.withAppendedPath(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, "31"), context);
        assertTrue(queried.get() != null);
        assertFalse(Thread.currentThread() == queried.get());
    }

    private void openSourceUnchanged(Uri uri) throws Exception {
        openSourceUnchanged(uri, RuntimeEnvironment.getApplication());
    }

    private void openSourceUnchanged(Uri uri, Context context) throws Exception {
        // Avoid constructing the JNI renderer; keep setVideoURI/openVideo and the
        // source worker real, mocking only the prior session and native boundary.
        Player player = mock(Player.class, CALLS_REAL_METHODS);
        ReflectionHelpers.setField(player, "seekLoading", new SeekLoadingSignal());
        IMediaPlayer media = mock(IMediaPlayer.class);
        Handler handler = new Handler(Looper.getMainLooper());
        ReflectionHelpers.setField(player, "mContext", context);
        ReflectionHelpers.setField(player, "mHandler", handler);
        ReflectionHelpers.setField(player, "mPreparedAsync", (Runnable) () -> { });
        ReflectionHelpers.setField(player, "mRefreshRateCheckerAsync", (Runnable) () -> { });
        ReflectionHelpers.setField(player, "mSurfaceHolder", mock(SurfaceHolder.class));
        doNothing().when(player).stopPlayback();
        Map<String, String> headers = Map.of("Authorization", "Bearer private-header-token");
        CountDownLatch sourceAttempted = new CountDownLatch(1);
        AtomicReference<Thread> sourceThread = new AtomicReference<>();
        doAnswer(invocation -> {
            sourceThread.set(Thread.currentThread());
            sourceAttempted.countDown();
            return null;
        }).when(media).setDataSource(eq(context), eq(uri), any(Map.class));
        try (MockedStatic<MediaFactory> factory = mockStatic(MediaFactory.class);
             MockedStatic<CustomApplication> application = mockStatic(CustomApplication.class)) {
            factory.when(() -> MediaFactory.createPlayer(context, false)).thenReturn(media);
            player.setVideoURI(uri, headers);
            assertTrue("Real source worker must reach setDataSource", sourceAttempted.await(3, TimeUnit.SECONDS));
            sourceThread.get().join(3000);
            assertFalse("Source worker must finish", sourceThread.get().isAlive());
            verify(media).setDataSource(context, uri, headers);
            factory.verify(() -> MediaFactory.createPlayer(context, false));
        } finally {
            // Preparation/rendering is outside this test; remove its queued work.
            handler.removeCallbacksAndMessages(null);
        }
    }
}
