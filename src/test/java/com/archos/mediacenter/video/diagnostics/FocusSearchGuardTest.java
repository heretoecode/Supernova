package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import android.app.Application;
import android.view.FocusFinder;
import android.view.View;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class FocusSearchGuardTest
{
    @Test
    public void ignoresForeignAndDetachedFocusAndKeepsNormalFinderResult()
    {
        Application app = RuntimeEnvironment.getApplication();
        LinearLayout scope = new LinearLayout(app);
        View first = new View(app);
        View second = new View(app);
        first.setFocusable(true);
        second.setFocusable(true);
        scope.addView(first);
        scope.addView(second);
        scope.layout(0, 0, 500, 500);
        first.layout(0, 0, 100, 100);
        second.layout(0, 150, 100, 250);
        assertSame(FocusFinder.getInstance().findNextFocus(scope, first, View.FOCUS_DOWN),
            FocusSearchGuard.findNextFocus(scope, first, View.FOCUS_DOWN));
        assertNull(FocusSearchGuard.findNextFocus(scope, new View(app), View.FOCUS_DOWN));
        scope.removeView(first);
        assertNull(FocusSearchGuard.findNextFocus(scope, first, View.FOCUS_DOWN));
        assertNull(FocusSearchGuard.findNextFocus(null, second, View.FOCUS_DOWN));
        assertSame(FocusFinder.getInstance().findNextFocus(scope, null, View.FOCUS_FORWARD),
            FocusSearchGuard.findNextFocus(scope, null, View.FOCUS_FORWARD));
    }
}
