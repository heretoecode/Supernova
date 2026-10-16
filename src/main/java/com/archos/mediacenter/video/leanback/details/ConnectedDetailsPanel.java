package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.leanback.PreviewAccent;

/** One compound perimeter and fill, with an open join beneath the selected tab. */
final class ConnectedDetailsPanel extends LinearLayout {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path=new Path();private View selected;
    ConnectedDetailsPanel(Context context){super(context);setOrientation(VERTICAL);setClipChildren(false);setClipToPadding(false);setWillNotDraw(false);setTag("details.connected.frame");}
    void selected(View tab){selected=tab;invalidate();}
    private void outline(){
        path.reset();if(selected==null||selected.getParent()==null)return;
        float stroke=getResources().getDisplayMetrics().density,inset=stroke/2,top=((View)selected.getParent()).getBottom(),left=selected.getLeft()+inset,right=selected.getRight()-inset,w=getWidth()-inset,h=getHeight()-inset,r=4*stroke;
        path.moveTo(inset,top);if(left>inset)path.lineTo(left,top);
        path.lineTo(left,inset+r);path.quadTo(left,inset,left+r,inset);path.lineTo(right-r,inset);path.quadTo(right,inset,right,inset+r);path.lineTo(right,top);path.lineTo(w-r,top);path.quadTo(w,top,w,top+r);path.lineTo(w,h-r);path.quadTo(w,h,w-r,h);path.lineTo(inset+r,h);path.quadTo(inset,h,inset,h-r);path.close();
    }
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);outline();paint.setStyle(Paint.Style.FILL);paint.setColor(0xed0b1622);canvas.drawPath(path,paint);}
    @Override protected void dispatchDraw(Canvas canvas){super.dispatchDraw(canvas);outline();paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(getResources().getDisplayMetrics().density);paint.setColor(PreviewAccent.color(getContext(),Color.WHITE));canvas.drawPath(path,paint);}
}
