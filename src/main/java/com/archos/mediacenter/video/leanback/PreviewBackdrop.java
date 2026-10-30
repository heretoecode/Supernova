package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.squareup.picasso.*;

/** One bounded artwork decode shared visually by the header and its navigation. */
public final class PreviewBackdrop extends Drawable implements Target {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final float density;private final Context context;
    private Bitmap bitmap, previous;
    private final android.os.Handler handler=new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable pending;private long fadeStart;
    private Uri uri;
    private boolean loading;
    private Target activeTarget;
    private long requestedAt;
    private long requestSequence;
    private com.archos.mediacenter.video.diagnostics.ArtworkTrace trace;
    private static long indexedMedia(android.net.Uri uri){
        PreviewLibraryLoader.Snapshot snapshot=PreviewLibraryLoader.memoryCache();if(snapshot==null||uri==null)return 0;
        long found=0;for(PreviewLibraryLoader.Entry entry:snapshot.technical)if(uri.equals(entry.backdrop)&&entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Video){
            long id=((com.archos.mediacenter.video.browser.adapters.object.Video)entry.media).getId();if(found!=0&&found!=id)return 0;found=id;
        }return found;
    }
    private int motionDirection,pendingDirection;
    private boolean foundationHome;
    private boolean strictLibraryArtwork;private int pageIdentity=-1;
    public void setPage(int page){boolean library=page==1||page==2||page==6;if(library&&page!=pageIdentity){release();invalidateSelf();}pageIdentity=page;strictLibraryArtwork=library;}
    private PreviewUtilityBackground foundationBackground;
    public void setFoundationHome(boolean value){foundationHome=value;if(value&&foundationBackground==null)foundationBackground=new PreviewUtilityBackground(context);invalidateSelf();}
    public void setMotionDirection(int direction){pendingDirection=Integer.signum(direction);}
    public boolean readyForFirstFrame(){return !loading;}
    public PreviewBackdrop(Context c) { context=c;density=c.getResources().getDisplayMetrics().density; }
    public void load(Uri next) {
        if(strictLibraryArtwork)return;
        if(java.util.Objects.equals(uri,next)){pendingDirection=0;return;}
        if(activeTarget!=null)Picasso.get().cancelRequest(activeTarget);activeTarget=null;Picasso.get().cancelRequest(this);if(pending!=null)handler.removeCallbacks(pending);uri=next;loading=next!=null;requestedAt=android.os.SystemClock.uptimeMillis();requestSequence++;
        if(trace!=null)trace.cancelled();trace=next==null?null:new com.archos.mediacenter.video.diagnostics.ArtworkTrace(indexedMedia(next),com.archos.mediacenter.video.diagnostics.Diagnostics.artworkSurface(),"backdrop",com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(next));
        pending=()->{if(next==null){previous=bitmap;bitmap=null;fadeStart=android.os.SystemClock.uptimeMillis();invalidateSelf();}else {final long sequence=requestSequence;activeTarget=new Target(){
            public void onBitmapLoaded(Bitmap image,Picasso.LoadedFrom from){if(sequence==requestSequence)PreviewBackdrop.this.onBitmapLoaded(image,from);}
            public void onBitmapFailed(Exception failure,Drawable fallback){if(sequence==requestSequence)PreviewBackdrop.this.onBitmapFailed(failure,fallback);}
            public void onPrepareLoad(Drawable placeholder){}
        };Picasso.get().load(next).resize(1600,900).centerInside().noFade().into(activeTarget);}};pending.run();
    }
    public void release() { loading=false;requestSequence++;if(activeTarget!=null)Picasso.get().cancelRequest(activeTarget);activeTarget=null;if(trace!=null)trace.cancelled();handler.removeCallbacksAndMessages(null);if(uri!=null)Picasso.get().cancelRequest(this); bitmap=previous=null; uri=null; }
    @Override public void draw(Canvas canvas) {
        Rect b=getBounds(); float h=Math.min(b.height(),420*density);
        if(strictLibraryArtwork){if(foundationBackground==null)foundationBackground=new PreviewUtilityBackground(context);foundationBackground.setBounds(b);foundationBackground.draw(canvas);return;}
        if(foundationHome){foundationBackground.setBounds(b);foundationBackground.draw(canvas);if(bitmap==null&&previous==null)return;}else canvas.drawColor(0xff0b1b2a);if(!foundationHome&&bitmap==null&&previous==null){android.graphics.drawable.GradientDrawable utility=PreviewAccent.utility(context);utility.setBounds(b);utility.draw(canvas);}
        paint.setShader(null);
        float fade=Math.min(1f,(android.os.SystemClock.uptimeMillis()-fadeStart)/180f);
        canvas.save();canvas.translate(-motionDirection*20*density*fade,0);drawImage(canvas,previous,b.width(),h,1-fade);canvas.restore();
        canvas.save();canvas.translate(motionDirection*20*density*(1-fade),0);drawImage(canvas,bitmap,b.width(),h,fade);canvas.restore();
        paint.setAlpha(255);if(fade<1)invalidateSelf();else {previous=null;motionDirection=0;}
        paint.setShader(new LinearGradient(0,0,b.width(),0,new int[]{0xf00b1b2a,0x700b1b2a,0x250b1b2a},null,Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,b.width(),h,paint);
        paint.setShader(new LinearGradient(0,0,0,h,new int[]{0x800b1b2a,0x100b1b2a,0xff0b1b2a},new float[]{0,.40f,1},Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,b.width(),h,paint);paint.setShader(null);
    }
    private void drawImage(Canvas canvas,Bitmap image,float w,float h,float alpha){if(image==null||image.isRecycled()||image.getWidth()<image.getHeight())return;float scale=Math.max(w/image.getWidth(),h/image.getHeight());paint.setAlpha((int)(255*alpha));canvas.save();canvas.clipRect(0,0,w,h);canvas.drawBitmap(image,null,new RectF(w-image.getWidth()*scale,0,w,image.getHeight()*scale),paint);canvas.restore();}
    @Override public void onBitmapLoaded(Bitmap b,Picasso.LoadedFrom from){
        if(strictLibraryArtwork)return;
        loading=false;if(b.getWidth()<b.getHeight()){onBitmapFailed(null,null);return;}
        motionDirection=pendingDirection;pendingDirection=0;
        if(b!=bitmap){previous=bitmap;bitmap=b;fadeStart=previous==null?0:android.os.SystemClock.uptimeMillis();}
        if(trace!=null)trace.ready(String.valueOf(from));
        invalidateSelf();
    }
    @Override public void onBitmapFailed(Exception e,Drawable d){
        loading=false;pendingDirection=0;
        // A failed replacement must not turn the already displayed Home artwork into a blank frame.
        if(strictLibraryArtwork){bitmap=null;previous=null;}else if(bitmap==null)bitmap=previous;previous=null;fadeStart=0;motionDirection=0;
        if(trace!=null)trace.failed(e==null?"invalid_aspect":e.getClass().getSimpleName(),bitmap!=null);
        invalidateSelf();
    }
    @Override public void onPrepareLoad(Drawable d){}
    @Override public void setAlpha(int alpha){}
    @Override public void setColorFilter(ColorFilter f){}
    @Override public int getOpacity(){return PixelFormat.OPAQUE;}
}
