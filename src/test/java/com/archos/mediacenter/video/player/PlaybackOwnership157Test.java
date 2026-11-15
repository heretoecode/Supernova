package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.filecoreextension.upnp2.StreamUriFinder;
import com.archos.mediacenter.utils.introdb.IntroSegments;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.subtitlesmanager.SubtitleManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PlaybackOwnership157Test
{
    private PlayerService service()
    {
        PlayerService service = spy(new PlayerService());
        Context app = RuntimeEnvironment.getApplication();
        ReflectionHelpers.callInstanceMethod(service, "attachBaseContext", ClassParameter.from(Context.class, app));
        ReflectionHelpers.setField(service, "mPlayer", mock(Player.class));
        ReflectionHelpers.setField(service, "mHandler", new Handler(Looper.getMainLooper()));
        ReflectionHelpers.setField(service, "mPreferences", PreferenceManager.getDefaultSharedPreferences(app));
        ReflectionHelpers.setField(service, "mUri", Uri.parse("smb://host/one.mkv"));
        ReflectionHelpers.setField(service, "mIntent", new Intent());
        return service;
    }

    @Test public void singleModeCancelAndStopWithoutAnIndexOwnerCannotAcceptAnOldNextResult() throws Exception
    {
        PlayerService service = service();
        AtomicReference<UpdateNextTask.Listener> callback = new AtomicReference<>();
        try (var construction = mockConstruction(UpdateNextTask.class, (task, context) -> {
            doAnswer(call -> {
                if (call.getArgument(0) != null)
                    callback.set(call.getArgument(0));
                return null;
            }).when(task).setListener(any());
        }))
        {
            service.setPlayMode(1, false);
            UpdateNextTask first = construction.constructed().get(0);
            UpdateNextTask.Listener old = callback.get();
            service.setPlayMode(0, false);
            verify(first).cancel(false);
            old.onResult(Uri.parse("smb://host/obsolete.mkv"), 77);
            assertNull(ReflectionHelpers.getField(service, "mNextUri"));
            service.setPlayMode(1, false);
            UpdateNextTask second = construction.constructed().get(1);
            service.previewCancelAutoNext();
            verify(second).cancel(false);
            service.setPlayMode(1, false);
            UpdateNextTask third = construction.constructed().get(2);
            service.stopAndSaveVideoState();
            verify(third).cancel(false);
            assertNull(ReflectionHelpers.getField(service, "mUpdateNextTask"));
        }
    }

    @Test public void aLateUpnpResolutionCannotReplaceANewerSourceOrCallThePlayer() throws Exception
    {
        PlayerService service = service();
        Uri old = Uri.parse("upnp://12/old");
        Uri current = Uri.parse("smb://host/new.mkv");
        ReflectionHelpers.setField(service, "mUri", old);
        ReflectionHelpers.setField(service, "mStreamingUri", old);
        try (var construction = mockConstruction(StreamUriFinder.class))
        {
            ReflectionHelpers.callInstanceMethod(service, "onDataUriOK");
            StreamUriFinder finder = construction.constructed().get(0);
            ArgumentCaptor<StreamUriFinder.Listener> listener = ArgumentCaptor.forClass(StreamUriFinder.Listener.class);
            verify(finder).setListener(listener.capture());
            ReflectionHelpers.setField(service, "mUri", current);
            ReflectionHelpers.setField(service, "mStreamingUri", current);
            ReflectionHelpers.setField(service, "mSourceRequestGeneration", 2);
            listener.getValue().onUriFound(Uri.parse("http://old-server/old.mkv"));
            assertEquals(current, service.getStreamingUri());
            Player player = ReflectionHelpers.getField(service, "mPlayer");
            verifyNoInteractions(player);
        }
    }

    @Test public void aSubtitleCallbackCannotReleaseTheNextSourcesReadinessLatchEvenForTheSameUri() throws Exception
    {
        PlayerService service = service();
        List<SubtitleManager.Listener> callbacks = new ArrayList<>();
        try (var construction = mockConstruction(SubtitleManager.class, (manager, call) ->
            callbacks.add((SubtitleManager.Listener)call.arguments().get(1))))
        {
            ReflectionHelpers.callInstanceMethod(service, "prepareSubs");
            CountDownLatch old = ReflectionHelpers.getField(service, "mSubtitlesReadyLatch");
            ReflectionHelpers.callInstanceMethod(service, "invalidateSubtitlePreparation");
            assertEquals(0, old.getCount());
            ReflectionHelpers.callInstanceMethod(service, "prepareSubs");
            CountDownLatch current = ReflectionHelpers.getField(service, "mSubtitlesReadyLatch");
            callbacks.get(0).onNoSubtitlesFound(Uri.parse("smb://host/one.mkv"));
            assertEquals(1, current.getCount());
            assertTrue(ReflectionHelpers.getField(service, "mIsPreparingSubs"));
            callbacks.get(1).onNoSubtitlesFound(Uri.parse("smb://host/one.mkv"));
            assertEquals(0, current.getCount());
            assertFalse(ReflectionHelpers.getField(service, "mIsPreparingSubs"));
        }
    }

    @Test public void cachedSegmentsFromAnotherVideoDoNotSuppressFetchingTheCurrentVideo() throws Exception
    {
        PlayerService service = service();
        Uri current = Uri.parse("smb://host/current.mkv");
        VideoDbInfo info = new VideoDbInfo(current);
        info.isScraped = true;
        info.scraperMovieId = "123";
        info.duration = 100000;
        ReflectionHelpers.setField(service, "mVideoInfo", info);
        ReflectionHelpers.setField(service, "mUri", current);
        ReflectionHelpers.setField(service, "mIntroDbFetchedUri", Uri.parse("smb://host/old.mkv"));
        ReflectionHelpers.setField(service, "mIntroSegments", mock(IntroSegments.class));
        IntroSegments expected = mock(IntroSegments.class);
        when(expected.get(any(IntroSegments.Type.class))).thenReturn(List.of());
        CountDownLatch fetched = new CountDownLatch(1);
        doAnswer(call -> {
            fetched.countDown();
            return expected;
        }).when(service).fetchIntroSegments(any());
        service.fetchIntroDbIfNeeded();
        assertTrue(fetched.await(2, TimeUnit.SECONDS));
        long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (service.getIntroSegments() != expected && System.nanoTime() < end)
        {
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            Thread.sleep(5);
        }
        assertSame(expected, service.getIntroSegments());
        verify(service).fetchIntroSegments(any());
    }

    @Test public void aPreviousVisitToTheSameUriCannotPublishSegmentsIntoANewerSession() throws Exception
    {
        PlayerService service = service();
        Uri uri = Uri.parse("smb://host/one.mkv");
        VideoDbInfo info = new VideoDbInfo(uri);
        info.isScraped = true;
        info.scraperMovieId = "123";
        ReflectionHelpers.setField(service, "mVideoInfo", info);
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch returned = new CountDownLatch(1);
        IntroSegments obsolete = mock(IntroSegments.class);
        when(obsolete.get(any(IntroSegments.Type.class))).thenReturn(List.of());
        doAnswer(call -> {
            entered.countDown();
            assertTrue(release.await(2, TimeUnit.SECONDS));
            returned.countDown();
            return obsolete;
        }).when(service).fetchIntroSegments(any());
        service.fetchIntroDbIfNeeded();
        try
        {
            assertTrue(entered.await(2, TimeUnit.SECONDS));
            ReflectionHelpers.setField(service, "mSourceRequestGeneration", 2);
            release.countDown();
            assertTrue(returned.await(2, TimeUnit.SECONDS));
            long end = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(200);
            while (System.nanoTime() < end)
            {
                Shadows.shadowOf(Looper.getMainLooper()).idle();
                Thread.sleep(5);
            }
            assertNull(service.getIntroSegments());
        }
        finally
        {
            release.countDown();
        }
    }
}
