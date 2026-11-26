package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** D11 (0.158.8): a freeze report names the app's own methods and the call each one was waiting in. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class FreezeTraceTest
{
    @Test public void aFreezeTraceKeepsAppFramesAndTheCallTheyWaitIn()
    {
        StackTraceElement[] frames = {
            new StackTraceElement("java.lang.Object", "wait", "Object.java", 1),
            new StackTraceElement("android.database.sqlite.SQLiteConnection", "nativeExecute", "S.java", 2),
            new StackTraceElement("com.archos.mediacenter.video.leanback.details.Example", "bind", "E.java", 3),
            new StackTraceElement("android.view.View", "draw", "View.java", 4),
            new StackTraceElement("android.os.Looper", "loop", "Looper.java", 5),
        };
        String trace = Diagnostics.appFrames(frames);
        assertTrue(trace.contains("SQLiteConnection.nativeExecute"));
        assertTrue(trace.contains("details.Example.bind"));
        assertFalse("frames far from app code are left out", trace.contains("Looper.loop"));
    }
}
