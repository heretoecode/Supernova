package com.archos.mediacenter.video.leanback.details;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.archos.mediascraper.ScraperTrailer;

/** Official YouTube iframe over a bounded snapshot of Details, with exact opener restoration. */
public final class PreviewTrailer {
    public static void show(Activity activity,ScraperTrailer trailer){
        if(!"YouTube".equals(trailer.mSite)||trailer.mVideoKey==null||!trailer.mVideoKey.matches("[A-Za-z0-9_-]{11}")){
            Toast.makeText(activity,"This trailer format is not supported",Toast.LENGTH_LONG).show();return;
        }
        final View opener=activity.getCurrentFocus();WebView web;
        try{web=new WebView(activity);}catch(RuntimeException unavailable){Toast.makeText(activity,"In-app trailers are unavailable on this device",Toast.LENGTH_LONG).show();return;}
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        Dialog dialog=com.archos.mediacenter.video.leanback.PreviewDialog.create(activity,"trailer");dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FrameLayout overlay=new FrameLayout(activity);overlay.setTag("semantic:trailer.overlay");
        View source=activity.getWindow().getDecorView();int width=activity.getResources().getDisplayMetrics().widthPixels,height=activity.getResources().getDisplayMetrics().heightPixels;
        Bitmap snapshot=Bitmap.createBitmap(Math.max(1,width/12),Math.max(1,height/12),Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(snapshot);canvas.scale(snapshot.getWidth()/(float)Math.max(1,source.getWidth()),snapshot.getHeight()/(float)Math.max(1,source.getHeight()));source.draw(canvas);
        blur(snapshot);ImageView behind=new ImageView(activity);behind.setImageBitmap(snapshot);behind.setScaleType(ImageView.ScaleType.FIT_XY);overlay.addView(behind,new FrameLayout.LayoutParams(-1,-1));
        View dim=new View(activity);dim.setBackgroundColor(0xb0000000);overlay.addView(dim,new FrameLayout.LayoutParams(-1,-1));
        int videoWidth=Math.min(Math.round(width*.775f),Math.round(height*.775f*16/9));int videoHeight=Math.round(videoWidth*9/16f);
        web.setBackgroundColor(Color.BLACK);web.setTag("semantic:trailer.video");overlay.addView(web,new FrameLayout.LayoutParams(videoWidth,videoHeight,Gravity.CENTER));dialog.setContentView(overlay);
        web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){if("supernova".equals(request.getUrl().getScheme())&&"trailer-ended".equals(request.getUrl().getHost())){activity.runOnUiThread(dialog::dismiss);return true;}String host=request.getUrl().getHost();return host==null||!(host.equals("www.youtube.com")||host.equals("www.youtube-nocookie.com"));}});
        dialog.setOnKeyListener((d,key,event)->{if(key==KeyEvent.KEYCODE_BACK){if(event.getAction()==KeyEvent.ACTION_UP)dialog.dismiss();return true;}return false;});
        dialog.setOnDismissListener(d->{web.stopLoading();web.loadUrl("about:blank");web.onPause();overlay.removeView(web);web.destroy();behind.setImageDrawable(null);snapshot.recycle();if(opener!=null&&opener.isAttachedToWindow()){opener.requestFocus();opener.post(opener::requestFocus);}});
        dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);dialog.getWindow().setDimAmount(0);dialog.getWindow().setLayout(-1,-1);web.requestFocus();overlay.setAlpha(0);overlay.animate().alpha(1).setDuration(180).start();
        web.loadDataWithBaseURL("https://www.youtube-nocookie.com/","<html><body style='margin:0;background:#000;overflow:hidden'><div id='player'></div><script src='https://www.youtube.com/iframe_api'></script><script>function onYouTubeIframeAPIReady(){new YT.Player('player',{width:'100%',height:'100%',videoId:'"+trailer.mVideoKey+"',host:'https://www.youtube-nocookie.com',playerVars:{autoplay:1,playsinline:1,controls:1,rel:0},events:{onStateChange:function(e){if(e.data===0)location.href='supernova://trailer-ended';}}});}</script></body></html>","text/html","UTF-8",null);
    }
    private static void blur(Bitmap bitmap){int w=bitmap.getWidth(),h=bitmap.getHeight();int[] input=new int[w*h],output=new int[w*h];bitmap.getPixels(input,0,w,0,0,w,h);for(int pass=0;pass<3;pass++){for(int y=0;y<h;y++)for(int x=0;x<w;x++){int r=0,g=0,b=0,n=0;for(int dy=-2;dy<=2;dy++)for(int dx=-2;dx<=2;dx++){int colour=input[Math.max(0,Math.min(h-1,y+dy))*w+Math.max(0,Math.min(w-1,x+dx))];r+=(colour>>16)&255;g+=(colour>>8)&255;b+=colour&255;n++;}output[y*w+x]=0xff000000|((r/n)<<16)|((g/n)<<8)|(b/n);}int[] swap=input;input=output;output=swap;}bitmap.setPixels(input,0,w,0,0,w,h);}
}
