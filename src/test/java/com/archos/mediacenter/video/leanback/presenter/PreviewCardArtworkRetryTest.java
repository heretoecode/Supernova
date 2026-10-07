package com.archos.mediacenter.video.leanback.presenter;

import android.app.Application;
import android.net.Uri;
import android.widget.FrameLayout;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.squareup.picasso.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewCardArtworkRetryTest {
    @Test @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    public void artworkPixelsStayInsideRoundedBody(){
        PreviewCardPresenter.Card card=new PreviewCardPresenter.Card(RuntimeEnvironment.getApplication(),PreviewCardPresenter.Style.CONTINUE);
        card.image.setImageDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.RED));
        card.measure(android.view.View.MeasureSpec.makeMeasureSpec(172,android.view.View.MeasureSpec.EXACTLY),android.view.View.MeasureSpec.makeMeasureSpec(105,android.view.View.MeasureSpec.EXACTLY));card.layout(0,0,172,105);
        android.view.View body=card.getChildAt(0);android.graphics.Bitmap pixels=android.graphics.Bitmap.createBitmap(172,105,android.graphics.Bitmap.Config.ARGB_8888);body.draw(new android.graphics.Canvas(pixels));
        assertNotEquals(android.graphics.Color.RED,pixels.getPixel(0,0));assertNotEquals(android.graphics.Color.RED,pixels.getPixel(171,0));
        assertEquals(android.graphics.Color.RED,pixels.getPixel(86,8));pixels.recycle();
    }
    @Test @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    public void focusedArtworkAndBoundaryEnlargeTogetherInRenderedPixels()throws Exception{
        android.app.Activity host=Robolectric.buildActivity(android.app.Activity.class).setup().visible().get();FrameLayout root=new FrameLayout(host);root.setClipChildren(false);root.setFocusableInTouchMode(true);root.setBackgroundColor(android.graphics.Color.BLACK);
        PreviewCardPresenter.Card card=new PreviewCardPresenter.Card(host,PreviewCardPresenter.Style.CONTINUE);card.image.setImageDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.RED));FrameLayout.LayoutParams size=new FrameLayout.LayoutParams(172,105);size.leftMargin=60;size.topMargin=50;root.addView(card,size);host.setContentView(root);
        root.measure(android.view.View.MeasureSpec.makeMeasureSpec(320,android.view.View.MeasureSpec.EXACTLY),android.view.View.MeasureSpec.makeMeasureSpec(220,android.view.View.MeasureSpec.EXACTLY));root.layout(0,0,320,220);root.requestFocus();Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(200));
        android.graphics.Bitmap before=android.graphics.Bitmap.createBitmap(320,220,android.graphics.Bitmap.Config.ARGB_8888);root.draw(new android.graphics.Canvas(before));
        card.requestFocus();Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(200));android.graphics.Bitmap after=android.graphics.Bitmap.createBitmap(320,220,android.graphics.Bitmap.Config.ARGB_8888);root.draw(new android.graphics.Canvas(after));
        int[] old=redBounds(before,85),now=redBounds(after,85);assertTrue("Artwork left edge enlarges with the unit",now[0]<old[0]-3);assertTrue("Artwork right edge enlarges with the unit",now[1]>old[1]+3);
        boolean leftGlow=false,rightGlow=false;for(int x=now[0]-7;x<now[0];x++){int c=after.getPixel(x,85);leftGlow|=android.graphics.Color.blue(c)>android.graphics.Color.red(c)+10;}for(int x=now[1]+1;x<now[1]+8;x++){int c=after.getPixel(x,85);rightGlow|=android.graphics.Color.blue(c)>android.graphics.Color.red(c)+10;}assertTrue("Boundary/glow follows enlarged artwork on both sides",leftGlow&&rightGlow);
        java.io.File file=new java.io.File("build/reports/preview-ui/focused-card-unit.png");file.getParentFile().mkdirs();try(java.io.FileOutputStream out=new java.io.FileOutputStream(file)){after.compress(android.graphics.Bitmap.CompressFormat.PNG,100,out);}before.recycle();after.recycle();host.finish();
    }
    private int[] redBounds(android.graphics.Bitmap bitmap,int y){int first=bitmap.getWidth(),last=-1;for(int x=0;x<bitmap.getWidth();x++){int c=bitmap.getPixel(x,y);if(android.graphics.Color.red(c)>200&&android.graphics.Color.green(c)<80&&android.graphics.Color.blue(c)<80){first=Math.min(first,x);last=x;}}assertTrue("Rendered artwork exists",last>first);return new int[]{first,last};}
    @Test public void failedRebindsBackOffAndLateCallbacksCannotCompleteNewIdentity(){
        Picasso previous=ReflectionHelpers.getStaticField(Picasso.class,"singleton");Picasso loader=mock(Picasso.class);
        RequestCreator request=mock(RequestCreator.class,RETURNS_SELF);when(loader.load(any(Uri.class))).thenReturn(request);
        ReflectionHelpers.setStaticField(Picasso.class,"singleton",loader);
        PreviewCardPresenter presenter=new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
        Presenter.ViewHolder holder=presenter.onCreateViewHolder(new FrameLayout(RuntimeEnvironment.getApplication()));
        try{
            Video first=media(91),second=media(92);presenter.onBindViewHolder(holder,first);
            ArgumentCaptor<Callback> callbacks=ArgumentCaptor.forClass(Callback.class);verify(request).into(any(android.widget.ImageView.class),callbacks.capture());
            Callback old=callbacks.getValue();old.onError(new java.io.IOException());
            for(int i=0;i<20;i++)presenter.onBindViewHolder(holder,first);
            verify(loader,times(1)).load(any(Uri.class));
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(1001));presenter.onBindViewHolder(holder,first);
            verify(loader,times(2)).load(any(Uri.class));assertFalse(((PreviewCardPresenter.Card)holder.view).artworkReady);
            presenter.onBindViewHolder(holder,second);old.onSuccess();assertFalse(((PreviewCardPresenter.Card)holder.view).artworkReady);
            verify(loader,times(3)).load(any(Uri.class));
            ArgumentCaptor<Callback> all=ArgumentCaptor.forClass(Callback.class);verify(request,times(3)).into(any(android.widget.ImageView.class),all.capture());all.getValue().onError(new java.io.IOException());
            Uri corrected=Uri.parse("https://example.org/corrected.jpg");when(second.getPosterUri()).thenReturn(corrected);presenter.onBindViewHolder(holder,second);verify(loader).load(corrected);
        }finally{presenter.onUnbindViewHolder(holder);ReflectionHelpers.setStaticField(Picasso.class,"singleton",previous);}
    }
    private Video media(long id){Video video=mock(Video.class);when(video.getId()).thenReturn(id);when(video.getName()).thenReturn("Title");when(video.getPosterUri()).thenReturn(Uri.parse("https://example.org/"+id+".jpg"));return video;}
}
