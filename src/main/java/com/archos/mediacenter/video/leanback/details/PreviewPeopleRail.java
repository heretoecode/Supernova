package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.leanback.PreviewDialog;

/** Whole portrait columns with space for the shared focus enlargement. */
final class PreviewPeopleRail extends HorizontalScrollView {
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
            int count=Math.max(1,available/PreviewDialog.dp(getContext(),106)),stride=available/count;
            for(int n=0;n<row.getChildCount();n++){
                View card=row.getChildAt(n);LinearLayout.LayoutParams size=(LinearLayout.LayoutParams)card.getLayoutParams();
                size.width=Math.max(1,stride-size.leftMargin-size.rightMargin);card.setLayoutParams(size);
            }
        }
        super.onMeasure(widthSpec,heightSpec);
    }
}
