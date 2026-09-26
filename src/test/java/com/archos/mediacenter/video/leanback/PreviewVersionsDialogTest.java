package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewVersionsDialogTest {
    private Video movie(long id) {
        return new Movie(id,"/storage/version-"+id+".mkv","Film",1,"Plot",2024,7,"",null,100000,0,0,0,false,false,false,false,1,0,1920,1080,null,null,null,null,0,1,500000,0);
    }
    @Test public void selectingVersionKeepsDialogOpenAndCurrentIndependentOfFocus() {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        Video first = movie(1), second = movie(2);
        AtomicReference<Video> selected = new AtomicReference<>(first);
        Dialog dialog = PreviewVersionsDialog.show(activity, Arrays.asList(first,second), first, selected::set);
        View a = dialog.findViewById(android.R.id.content).findViewWithTag("version:1");
        View b = dialog.findViewById(android.R.id.content).findViewWithTag("version:2");
        b.requestFocus();
        assertTrue(a.getContentDescription().toString().endsWith(", Current"));
        assertFalse(b.getContentDescription().toString().endsWith(", Current"));
        b.performClick();
        assertTrue(dialog.isShowing());
        assertSame(second, selected.get());
        assertFalse(a.getContentDescription().toString().endsWith(", Current"));
        assertTrue(b.getContentDescription().toString().endsWith(", Current"));
        assertTrue(b.hasFocus());
        dialog.dismiss();
    }
    @Test public void locationOmitsAuthenticationAndQueryMaterial() {
        assertEquals("HTTPS · example.com/media/film.mkv", PreviewVariants.safeLocation(Uri.parse("https://user:password@example.com/media/film.mkv?token=secret#private")));
        assertEquals("Local storage · /storage/film.mkv", PreviewVariants.safeLocation(Uri.parse("file:///storage/film.mkv")));
    }
}
