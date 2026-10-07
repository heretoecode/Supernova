package com.archos.mediacenter.video.leanback.details;

import android.app.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.archos.mediascraper.ScraperTrailer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Overlay geometry/end-callback wiring; does not claim real YouTube playback. */
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewTrailerTest {
 @Test public void centredAspectOverlayHasNoExtraControlsAndEndRestoresExactCard(){
  Activity host=Robolectric.buildActivity(Activity.class).setup().visible().get();Button card=new Button(host);card.setText("Launching trailer");card.setFocusableInTouchMode(true);host.setContentView(card);card.requestFocus();Shadows.shadowOf(host).setCurrentFocus(card);
  PreviewTrailer.show(host,new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,"Trailer","abcdefghijk","YouTube",""));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(dialog.isShowing());View decor=dialog.getWindow().getDecorView();WebView web=decor.findViewWithTag("semantic:trailer.video");assertNotNull(web);
  ViewGroup.LayoutParams size=web.getLayoutParams();assertTrue(Math.abs(size.width/(double)size.height-16d/9)<.01);assertTrue(Math.abs(size.width*size.height/(960d*540)-.60)<.02);assertEquals(-1,dialog.getWindow().getAttributes().width);assertEquals(-1,dialog.getWindow().getAttributes().height);assertFalse(text(decor).contains("Open YouTube"));assertFalse(text(decor).contains("Close"));
  WebResourceRequest ended=mock(WebResourceRequest.class);when(ended.getUrl()).thenReturn(android.net.Uri.parse("supernova://trailer-ended"));assertTrue(web.getWebViewClient().shouldOverrideUrlLoading(web,ended));Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertFalse(dialog.isShowing());assertTrue(card.hasFocus());host.finish();
 }
 private String text(View view){String result=view instanceof TextView?((TextView)view).getText().toString():"";if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)result+=text(((ViewGroup)view).getChildAt(i));return result;}
}
