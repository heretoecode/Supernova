package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.view.*;
import android.widget.*;

/** Eight flat children, four columns and two rows, with no independent focus or scrolling. */
final class DetailsPeopleGrid extends LinearLayout {
    DetailsPeopleGrid(Context c){super(c);setOrientation(VERTICAL);setFocusable(false);setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);}
    @Override protected void onMeasure(int widthSpec,int heightSpec){
        int width=MeasureSpec.getSize(widthSpec),height=MeasureSpec.getSize(heightSpec);
        if(MeasureSpec.getMode(heightSpec)==MeasureSpec.UNSPECIFIED)height=DetailsDimensions.px(getContext(),140);
        int cellWidth=Math.max(1,width/4),cellHeight=Math.max(1,height/2);
        for(int i=0;i<getChildCount();i++){
            View child=getChildAt(i);
            if(child instanceof android.view.ViewGroup){
                android.view.ViewGroup group=(android.view.ViewGroup)child;int captions=0;
                for(int n=1;n<group.getChildCount();n++)if(group.getChildAt(n) instanceof TextView){TextView label=(TextView)group.getChildAt(n);label.setTextSize(9*DetailsDimensions.scale(getContext()));int line=Math.max(DetailsDimensions.px(getContext(),12),(int)Math.ceil(label.getPaint().getFontMetrics().descent-label.getPaint().getFontMetrics().ascent));label.getLayoutParams().height=line;captions+=line;}
                if(group.getChildCount()>0&&group.getChildAt(0) instanceof ImageView){int portrait=Math.min(DetailsDimensions.px(getContext(),34),Math.max(1,cellHeight-captions));group.getChildAt(0).getLayoutParams().width=portrait;group.getChildAt(0).getLayoutParams().height=portrait;}
            }
            child.measure(MeasureSpec.makeMeasureSpec(cellWidth,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(cellHeight,MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width,height);
    }
    @Override protected void onLayout(boolean changed,int l,int t,int r,int b){int w=getWidth()/4,h=getHeight()/2;for(int i=0;i<getChildCount();i++)getChildAt(i).layout(i%4*w,i/4*h,(i%4+1)*w,(i/4+1)*h);}
}
