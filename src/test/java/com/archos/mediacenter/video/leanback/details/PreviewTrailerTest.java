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
@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewTrailerTest {
 @Test public void centredAspectOverlayHasNoExtraControlsAndEndRestoresExactCard()throws Exception{
  Activity host=Robolectric.buildActivity(Activity.class).setup().visible().get();Button card=new Button(host);card.setText("Launching trailer");card.setFocusableInTouchMode(true);LinearLayout details=new LinearLayout(host);details.setOrientation(LinearLayout.VERTICAL);details.setBackgroundColor(android.graphics.Color.WHITE);TextView header=new TextView(host);header.setText("SUPERNOVA   Home   Movies   TV Shows   Network & Files");header.setTextColor(android.graphics.Color.BLACK);details.addView(header,new LinearLayout.LayoutParams(-1,52));details.addView(card,new LinearLayout.LayoutParams(-1,-1));host.setContentView(details);layout(host.getWindow().getDecorView());card.requestFocus();Shadows.shadowOf(host).setCurrentFocus(card);
  PreviewTrailer.show(host,new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,"Trailer","abcdefghijk","YouTube",""));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(dialog.isShowing());View decor=dialog.getWindow().getDecorView();WebView web=decor.findViewWithTag("semantic:trailer.video");assertNotNull(web);
  ViewGroup.LayoutParams size=web.getLayoutParams();assertTrue(Math.abs(size.width/(double)size.height-16d/9)<.01);assertTrue(Math.abs(size.width*size.height/(960d*540)-.60)<.02);assertEquals(-1,dialog.getWindow().getAttributes().width);assertEquals(-1,dialog.getWindow().getAttributes().height);assertFalse(text(decor).contains("Open YouTube"));assertFalse(text(decor).contains("Close"));
  Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(250));layout(decor);View overlay=decor.findViewWithTag("semantic:trailer.overlay");assertNotNull(overlay);assertEquals(overlay.getWidth()/2f,web.getLeft()+web.getWidth()/2f,1f);assertEquals(overlay.getHeight()/2f,web.getTop()+web.getHeight()/2f,1f);
  android.graphics.Bitmap rendered=android.graphics.Bitmap.createBitmap(960,540,android.graphics.Bitmap.Config.ARGB_8888);decor.draw(new android.graphics.Canvas(rendered));assertTrue("Details/header background must be strongly dimmed",android.graphics.Color.red(rendered.getPixel(900,30))<100);java.io.File output=new java.io.File("build/reports/preview-ui/trailer-corrective-overlay.png");output.getParentFile().mkdirs();try(java.io.FileOutputStream stream=new java.io.FileOutputStream(output)){rendered.compress(android.graphics.Bitmap.CompressFormat.PNG,100,stream);}rendered.recycle();
  WebResourceRequest ended=mock(WebResourceRequest.class);when(ended.getUrl()).thenReturn(android.net.Uri.parse("supernova://trailer-ended"));assertTrue(web.getWebViewClient().shouldOverrideUrlLoading(web,ended));Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertFalse(dialog.isShowing());assertTrue(card.hasFocus());host.finish();
 }
 private void layout(View view){view.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));view.layout(0,0,960,540);}
 private String text(View view){String result=view instanceof TextView?((TextView)view).getText().toString():"";if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)result+=text(((ViewGroup)view).getChildAt(i));return result;}
}
