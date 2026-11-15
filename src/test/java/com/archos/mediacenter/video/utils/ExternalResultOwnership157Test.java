package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.archos.mediacenter.utils.videodb.IndexHelper;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class ExternalResultOwnership157Test
{
    @Test public void pendingAndLateLookupsCannotWriteThePreviousVideosState()
    {
        Context app = RuntimeEnvironment.getApplication();
        Uri a = Uri.parse("file:///a.mkv");
        Uri b = Uri.parse("file:///b.mkv");
        List<IndexHelper.Listener> callbacks = new ArrayList<>();
        try (var helpers = mockConstruction(IndexHelper.class))
        {
            ExternalPlayerResultListener listener = new ExternalPlayerResultListener();
            listener.init(app, a, a, null);
            listener.init(app, b, b, null);
            for (IndexHelper helper : helpers.constructed())
            {
                ArgumentCaptor<IndexHelper.Listener> capture = ArgumentCaptor.forClass(IndexHelper.Listener.class);
                verify(helper).requestVideoDb(any(), eq(-1L), eq(null), capture.capture(), eq(false), eq(true));
                callbacks.add(capture.getValue());
            }
            verify(helpers.constructed().get(0)).abort();
            VideoDbInfo old = new VideoDbInfo(a);
            old.id = 10;
            old.duration = 9000;
            callbacks.get(0).onVideoDb(old, null);
            Intent result = new Intent().putExtra("position", 1000);
            listener.onActivityResult(1, Activity.RESULT_OK, result);
            verify(helpers.constructed().get(1), never()).writeVideoInfo(any(), anyBoolean());
            VideoDbInfo current = new VideoDbInfo(b);
            current.id = 20;
            current.duration = 20000;
            callbacks.get(1).onVideoDb(current, null);
            callbacks.get(0).onVideoDb(old, null);
            listener.onActivityResult(1, Activity.RESULT_OK, result);
            verify(helpers.constructed().get(1)).writeVideoInfo(current, true);
            assertEquals(1000, current.resume);
            assertEquals(-1, old.resume);
        }
    }

    @Test public void durationFromAnEarlierExternalLaunchCannotReplaceTheCurrentDuration()
    {
        Context app = RuntimeEnvironment.getApplication();
        Uri uri = Uri.parse("file:///one.mkv");
        try (var helpers = mockConstruction(IndexHelper.class))
        {
            ExternalPlayerResultListener listener = new ExternalPlayerResultListener();
            VideoDbInfo first = new VideoDbInfo(uri);
            first.duration = 10000;
            listener.init(app, uri, uri, first);
            listener.onActivityResult(1, Activity.RESULT_OK,
                                     new Intent().putExtra("position", 100).putExtra("duration", 9000));
            VideoDbInfo next = new VideoDbInfo(uri);
            next.duration = 20000;
            listener.init(app, uri, uri, next);
            listener.onActivityResult(1, Activity.RESULT_OK, new Intent().putExtra("position", 500));
            verify(helpers.constructed().get(1)).writeVideoInfo(next, true);
            assertEquals(20000, next.duration);
        }
    }
}
