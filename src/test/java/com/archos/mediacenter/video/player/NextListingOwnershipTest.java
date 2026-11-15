package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.ContentResolver;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import com.archos.filecorelibrary.MetaFile2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowProcess;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class NextListingOwnershipTest
{
    private static MetaFile2 file(String name, String uri, String mime)
    {
        MetaFile2 file = mock(MetaFile2.class);
        when(file.getName()).thenReturn(name);
        when(file.getUri()).thenReturn(Uri.parse(uri));
        when(file.isFile()).thenReturn(true);
        when(file.getMimeType()).thenReturn(mime);
        return file;
    }

    @Test public void wirePreservesExactUriNameMimeAndStableNameOrderingAndRejectsTruncatedResults() throws Exception
    {
        List<MetaFile2> files = List.of(file("B.mkv", "webdav://host/A%20B/B.mkv", "video/x-matroska"),
                                      file("a.mkv", "upnp://42/object%2F1", "video/other"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NextListingWire.write(new DataOutputStream(bytes), files);
        var restored = NextListingWire.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())),
                                            () -> false);
        assertEquals(files.get(0).getUri(), restored.get(0).getUri());
        assertEquals(files.get(1).getName(), restored.get(1).getName());
        assertEquals(files.get(1).getMimeType(), restored.get(1).getMimeType());
        assertTrue(restored.get(0).isFile());
        var ordered = new ArrayList<>(files);
        var comparator = new com.archos.filecorelibrary.FileComparator().selectFileComparator(
            com.archos.filecorelibrary.ListingEngine.SortOrder.SORT_BY_NAME_ASC);
        ordered.sort(comparator);
        restored.sort(comparator);
        assertEquals(ordered.get(0).getUri(), restored.get(0).getUri());
        assertEquals(ordered.get(1).getUri(), restored.get(1).getUri());
        byte[] truncated = java.util.Arrays.copyOf(bytes.toByteArray(), bytes.size() - 2);
        assertThrows(java.io.EOFException.class, () -> NextListingWire.read(
            new DataInputStream(new ByteArrayInputStream(truncated)), () -> false));
        assertThrows(java.io.InterruptedIOException.class, () -> NextListingWire.read(
            new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())), () -> true));
    }

    @Test public void statAndHealthListingRetainMeasuredSizeAndUnknownNeverBecomesZero() throws Exception
    {
        MetaFile2 known = file("known.mkv", "sftp://host/known.mkv", "video/other");
        MetaFile2 unknown = file("unknown.mkv", "smb://host/unknown.mkv", "video/other");
        when(known.length()).thenReturn(123L);
        when(unknown.length()).thenReturn(-1L);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NextListingWire.write(new DataOutputStream(bytes), List.of(known, unknown), true);
        var restored = NextListingWire.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())),
                                            () -> false);
        assertEquals(123, restored.get(0).length());
        assertEquals(-1, restored.get(1).length());
    }

    @Test public void cancelFalseBeforeExecutePreventsFactoryAndDatabaseWorkAndNoCallbackCanArrive() throws Exception
    {
        for (int i = 0; i < 100; i++)
        {
            ContentResolver resolver = mock(ContentResolver.class);
            UpdateNextTask task = new UpdateNextTask(resolver, null, Uri.parse("sftp://private/one.mkv"), null, 0, -1);
            List<Uri> results = new ArrayList<>();
            task.setListener((uri, id) -> results.add(uri));
            task.cancel(false);
            task.execute(false, false);
            assertTrue(task.awaitStopped(1000));
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertTrue(results.isEmpty());
            verifyNoInteractions(resolver);
        }
    }

    @Test public void completedLookupQueuedOnMainIsStillFencedByCancelFalse() throws Exception
    {
        ContentResolver resolver = mock(ContentResolver.class);
        when(resolver.query(any(Uri.class), any(String[].class), nullable(String.class), nullable(String[].class),
                            nullable(String.class))).thenReturn(new MatrixCursor(new String[] {"_data"}));
        UpdateNextTask task = new UpdateNextTask(resolver, null, Uri.parse("file:///one.mkv"),
            new ArrayList<>(List.of("file:///one.mkv", "file:///two.mkv")), 0, -1);
        List<Uri> results = new ArrayList<>();
        task.setListener((uri, id) -> results.add(uri));
        task.execute(false, false);
        assertTrue(task.awaitStopped(3000));
        task.cancel(false);
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        assertTrue(results.isEmpty());
    }

    @Test public void binderCancellationDoesNotNeedMainQueueAndAnOldTokenCannotStopTheCurrentOwner() throws Exception
    {
        NextListingService service = new NextListingService();
        ReflectionHelpers.setField(service, "token", "current");
        IBinder binder = service.onBind(null);
        ShadowProcess.clearKilledProcesses();
        transactStop(binder, "old");
        assertFalse(ShadowProcess.wasKilled(Process.myPid()));
        CountDownLatch stopped = new CountDownLatch(1);
        new Thread(() -> {
            try
            {
                transactStop(binder, "current");
            }
            catch (Exception failure)
            {
                throw new AssertionError(failure);
            }
            finally
            {
                stopped.countDown();
            }
        }, "cancel-without-main").start();
        assertTrue(stopped.await(2, TimeUnit.SECONDS));
        assertTrue(ShadowProcess.wasKilled(Process.myPid()));
    }

    private static void transactStop(IBinder binder, String token) throws Exception
    {
        Parcel data = Parcel.obtain();
        try
        {
            data.writeInterfaceToken(NextListingService.DESCRIPTOR);
            Bundle request = new Bundle();
            request.putString("token", token);
            data.writeBundle(request);
            binder.transact(NextListingService.STOP, data, null, IBinder.FLAG_ONEWAY);
        }
        finally
        {
            data.recycle();
        }
    }
}
