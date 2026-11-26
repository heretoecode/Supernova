package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** D4 (0.158.8): nothing inside NOVA's hidden browse rows can hold focus on the main screen. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class HiddenNovaContentTest
{
    @Test public void aViewInsideTheHiddenRowsCanNeverTakeFocus()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        FrameLayout pages = new FrameLayout(activity);
        FrameLayout nova = new FrameLayout(activity);
        Button hiddenGridCell = new Button(activity);
        hiddenGridCell.setFocusable(true);
        nova.addView(hiddenGridCell);
        Button supernova = new Button(activity);
        supernova.setFocusable(true);
        pages.addView(nova);
        pages.addView(supernova);
        activity.setContentView(pages);

        assertTrue("without the seal a hidden child takes focus in code", hiddenGridCell.requestFocus());
        HiddenNovaContent.seal(nova);

        assertFalse(hiddenGridCell.isFocused());
        assertFalse(hiddenGridCell.requestFocus());
        assertTrue(supernova.requestFocus());
        assertTrue(View.GONE == nova.getVisibility());
    }
}
