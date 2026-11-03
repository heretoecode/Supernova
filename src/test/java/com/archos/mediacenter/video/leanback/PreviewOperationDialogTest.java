package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.os.Looper;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowToast;

/** Checks operation dialog cancellation, completion dismissal and focused failure notices. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewOperationDialogTest {
 @Before public void isolatePreviewTransport(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drainPreviewWorkers() throws Exception { PreviewAsyncFixtures.drain(); }
    @Test public void completionDismissalDoesNotCancelTheOperation(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();AtomicInteger cancelled=new AtomicInteger();
        Dialog dialog=PreviewOperationDialog.show(host,"Download Subtitles","Downloading…",cancelled::incrementAndGet);
        assertEquals("semantic:operation:cancel",dialog.getCurrentFocus().getTag());
        dialog.dismiss();assertEquals(0,cancelled.get());
    }
    @Test public void cancelButtonAndBackUseTheSameCancellationPath(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();AtomicInteger cancelled=new AtomicInteger();
        Dialog dialog=PreviewOperationDialog.show(host,"Download Subtitles","Searching…",cancelled::incrementAndGet);
        dialog.getCurrentFocus().performClick();Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(1,cancelled.get());assertFalse(dialog.isShowing());
        dialog=PreviewOperationDialog.show(host,"Download Subtitles","Searching…",cancelled::incrementAndGet);
        dialog.onBackPressed();Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(2,cancelled.get());assertFalse(dialog.isShowing());
    }
    @Test public void failureNoticeHasFocusedCloseWithoutCopyOrToast(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();AtomicInteger closed=new AtomicInteger();
        Dialog dialog=PreviewOperationDialog.notice(host,"Download Subtitles","No subtitles found.",closed::incrementAndGet);
        assertEquals("semantic:operation:close",dialog.getCurrentFocus().getTag());
        assertNull(PreviewPagesTest.findText(dialog.getWindow().getDecorView(),"Copy"));
        int before=ShadowToast.shownToastCount();dialog.getCurrentFocus().performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(1,closed.get());assertEquals(before,ShadowToast.shownToastCount());
    }
}
