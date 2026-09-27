package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.filebrowsing.PreviewBrowserSurface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewProviderBrowserTest {
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void providerListingUsesSharedPanelsAndReadOnlyAction() throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            LinearLayout listing=new LinearLayout(host.get());listing.setOrientation(LinearLayout.VERTICAL);TextView row=new TextView(host.get());row.setText("Example.mkv");row.setTextColor(-1);row.setFocusable(true);row.setFocusableInTouchMode(true);listing.addView(row,new LinearLayout.LayoutParams(-1,44));
            int[] opened={0};PreviewBrowserSurface browser=new PreviewBrowserSurface(host.get(),listing,Uri.parse("putio://put.io/"),()->{});host.get().setContentView(browser);
            browser.providerItem("Example.mkv","Video · 1 GB",false,()->opened[0]++);PreviewPagesTest.layout(browser);
            assertNotNull(listing.getParent());assertNotNull(PreviewPagesTest.findText(browser,"File Information"));assertNull(PreviewPagesTest.findText(browser,"Delete"));
            row.requestFocus();browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));
            assertEquals("File Information",((TextView)browser.findFocus()).getText().toString());browser.findFocus().performClick();assertEquals(1,opened[0]);
            PreviewPagesTest.capture(browser,"putio-shared-browser");
        }finally{host.pause().stop().destroy();}
    }
}
