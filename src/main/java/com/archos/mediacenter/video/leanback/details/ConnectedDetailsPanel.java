package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.LinearLayout;

/** Shared, stable section frame. Selection belongs to the tab; focus remains on a control. */
final class ConnectedDetailsPanel extends LinearLayout {
    private final Paint border=new Paint(Paint.ANTI_ALIAS_FLAG);
    private View selected;
    ConnectedDetailsPanel(Context context){
        super(context);setOrientation(VERTICAL);setClipChildren(false);setClipToPadding(false);
        setTag("details.connected.frame");
        border.setStyle(Paint.Style.STROKE);border.setStrokeWidth(context.getResources().getDisplayMetrics().density);
    }
    void selected(View tab){selected=tab;invalidate();}
    @Override protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        if(selected==null||selected.getParent()==null)return;
        float inset=border.getStrokeWidth(),top=((View)selected.getParent()).getBottom();
        float left=selected.getLeft()+inset,right=selected.getRight()-inset;
        Path path=new Path();path.moveTo(inset,top);path.lineTo(left,top);
        path.lineTo(left,inset);path.lineTo(right,inset);path.lineTo(right,top);
        path.lineTo(getWidth()-inset,top);path.lineTo(getWidth()-inset,getHeight()-inset);
        path.lineTo(inset,getHeight()-inset);path.close();
        border.setColor(0x99ffffff);canvas.drawPath(path,border);
    }
}
