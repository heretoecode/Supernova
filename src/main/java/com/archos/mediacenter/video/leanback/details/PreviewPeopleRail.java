package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.leanback.PreviewDialog;

/** Whole portrait columns with space for the shared focus enlargement. */
final class PreviewPeopleRail extends HorizontalScrollView {
    private int visibleCards=1;
    PreviewPeopleRail(Context context){
        super(context);setHorizontalScrollBarEnabled(false);setSmoothScrollingEnabled(false);
        setHorizontalFadingEdgeEnabled(true);setFadingEdgeLength(PreviewDialog.dp(context,16));
        setPadding(PreviewDialog.dp(context,8),PreviewDialog.dp(context,8),PreviewDialog.dp(context,8),PreviewDialog.dp(context,8));
        setClipToPadding(false);
    }
    @Override protected void onMeasure(int widthSpec,int heightSpec){
        int available=MeasureSpec.getSize(widthSpec)-getPaddingLeft()-getPaddingRight();
        if(available>0&&getChildCount()>0&&getChildAt(0) instanceof LinearLayout){
            LinearLayout row=(LinearLayout)getChildAt(0);row.setClipChildren(false);
            int count=Math.max(1,available/PreviewDialog.dp(getContext(),106)),stride=available/count,remainder=available%count;
            visibleCards=count;
            for(int n=0;n<row.getChildCount();n++){
                View card=row.getChildAt(n);LinearLayout.LayoutParams size=(LinearLayout.LayoutParams)card.getLayoutParams();
                size.width=Math.max(1,stride+(n%count<remainder?1:0)-size.leftMargin-size.rightMargin);card.setLayoutParams(size);
            }
        }
        super.onMeasure(widthSpec,heightSpec);
    }
    @Override public void requestChildFocus(View child,View focused){
        int previous=getScrollX();super.requestChildFocus(child,focused);
        if(getChildCount()==0||!(getChildAt(0) instanceof LinearLayout))return;
        LinearLayout row=(LinearLayout)getChildAt(0);View card=focused;
        while(card!=null&&card.getParent()!=row)card=card.getParent() instanceof View?(View)card.getParent():null;
        if(card==null||card.getWidth()==0)return;
        int first=0;
        while(first+1<row.getChildCount()&&row.getChildAt(first+1).getLeft()<=previous)first++;
        int index=row.indexOfChild(card);
        if(index<first)first=index;else if(index>=first+visibleCards)first=index-visibleCards+1;
        first=Math.max(0,Math.min(first,Math.max(0,row.getChildCount()-visibleCards)));
        scrollTo(row.getChildAt(first).getLeft(),0);
    }
}
