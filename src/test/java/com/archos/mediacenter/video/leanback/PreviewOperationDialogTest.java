package com.archos.mediacenter.video.leanback;

import android.app.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewOperationDialogTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
    @Test public void completionDismissalDoesNotCancelTheOperation(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();AtomicInteger cancelled=new AtomicInteger();
        Dialog dialog=PreviewOperationDialog.show(host,"Download Subtitles","Downloading…",cancelled::incrementAndGet);
        assertEquals("semantic:operation:cancel",dialog.getCurrentFocus().getTag());
        dialog.dismiss();assertEquals(0,cancelled.get());
    }
    @Test public void cancelButtonAndBackUseTheSameCancellationPath(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();AtomicInteger cancelled=new AtomicInteger();
        Dialog dialog=PreviewOperationDialog.show(host,"Download Subtitles","Searching…",cancelled::incrementAndGet);
        dialog.getCurrentFocus().performClick();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(1,cancelled.get());assertFalse(dialog.isShowing());
        dialog=PreviewOperationDialog.show(host,"Download Subtitles","Searching…",cancelled::incrementAndGet);
        dialog.onBackPressed();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(2,cancelled.get());assertFalse(dialog.isShowing());
    }
    @Test public void failureNoticeHasFocusedCloseWithoutCopyOrToast(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();AtomicInteger closed=new AtomicInteger();
        Dialog dialog=PreviewOperationDialog.notice(host,"Download Subtitles","No subtitles found.",closed::incrementAndGet);
        assertEquals("semantic:operation:close",dialog.getCurrentFocus().getTag());
        assertNull(PreviewPagesTest.findText(dialog.getWindow().getDecorView(),"Copy"));
        int before=org.robolectric.shadows.ShadowToast.shownToastCount();dialog.getCurrentFocus().performClick();
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(1,closed.get());assertEquals(before,org.robolectric.shadows.ShadowToast.shownToastCount());
    }
}
