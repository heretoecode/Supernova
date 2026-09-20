package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.graphics.*;
import android.view.*;
import android.widget.ImageView;

/** Small, transient frosted snapshot for TV APIs predating RenderEffect. No persistent bitmap. */
public final class PreviewFrost {
    public static Runnable show(Activity activity){
        ViewGroup root=(ViewGroup)activity.getWindow().getDecorView();
        if(root.getWidth()==0||root.getHeight()==0)return ()->{};
        int width=160,height=Math.max(1,Math.round(width*root.getHeight()/(float)root.getWidth()));
        Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);canvas.scale(width/(float)root.getWidth(),height/(float)root.getHeight());root.draw(canvas);
        int[] pixels=new int[width*height],blurred=new int[pixels.length];bitmap.getPixels(pixels,0,width,0,0,width,height);
        for(int pass=0;pass<3;pass++){
            for(int y=0;y<height;y++)for(int x=0;x<width;x++){
                int r=0,g=0,b=0,n=0;
                for(int dy=-1;dy<=1;dy++)for(int dx=-1;dx<=1;dx++){int xx=Math.max(0,Math.min(width-1,x+dx)),yy=Math.max(0,Math.min(height-1,y+dy)),p=pixels[yy*width+xx];r+=Color.red(p);g+=Color.green(p);b+=Color.blue(p);n++;}
                blurred[y*width+x]=Color.rgb(r/n,g/n,b/n);
            }int[] swap=pixels;pixels=blurred;blurred=swap;
        }
        bitmap.setPixels(pixels,0,width,0,0,width,height);
        ImageView frost=new ImageView(activity);frost.setScaleType(ImageView.ScaleType.FIT_XY);frost.setImageBitmap(bitmap);frost.setFocusable(false);frost.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        root.addView(frost,new ViewGroup.LayoutParams(-1,-1));
        return ()->{root.removeView(frost);frost.setImageDrawable(null);};
    }
    private PreviewFrost(){}
}
