package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.os.Looper;
import android.widget.FrameLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewDialogPushBackTest
{
    @Test
    public void thePanelBehindAListIsDarkenedUntilTheListCloses()
    {
        ActivityController<Activity> host = Robolectric.buildActivity(Activity.class).setup().visible();
        try
        {
            Activity activity = host.get();
            activity.setContentView(new FrameLayout(activity));
            Dialog panel = PreviewDialog.choose(activity, "Panel", new String[] {"One", "Two"}, 0, index -> { });
            Dialog list = PreviewDialog.choose(activity, "List", new String[] {"A", "B"}, 0, index -> { });
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertNotNull(panel.getWindow().getDecorView().getForeground());
            assertNull(list.getWindow().getDecorView().getForeground());
            list.dismiss();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertNull(panel.getWindow().getDecorView().getForeground());
            panel.dismiss();
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }
}
