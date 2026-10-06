package com.archos.mediacenter.video.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.archos.mediacenter.video.R;

/** Four controls, with the transport pinned to the physical centre independently of labels. */
public final class PreviewTransport extends FrameLayout {
    public PreviewTransport(Context context,AttributeSet attrs){super(context,attrs);setClipChildren(false);}
    @Override protected void onLayout(boolean changed,int left,int top,int right,int bottom){
        super.onLayout(changed,left,top,right,bottom);
        float density=getResources().getDisplayMetrics().density;
        float spacing=Math.min(104*density,(getWidth()-88*density)/4f);
        for(int i=0;i<getChildCount();i++){
            View group=getChildAt(i);if(group.getVisibility()==GONE)continue;
            int offset=group.findViewById(R.id.preview_subtitles)!=null?-2:group.findViewById(R.id.preview_audio)!=null?-1:group.findViewById(R.id.pause)!=null?0:1;
            int x=Math.round(getWidth()/2f+offset*spacing-group.getMeasuredWidth()/2f);
            group.layout(x,0,x+group.getMeasuredWidth(),getHeight());
        }
    }
}
