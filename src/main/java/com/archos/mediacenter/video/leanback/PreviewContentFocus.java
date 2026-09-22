package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.IdentityHashMap;
import java.util.Map;

/** Focus belongs to a control's content. Artwork retains its separate perimeter treatment. */
final class PreviewContentFocus extends StateListDrawable {
    private final int accent;
    private final float radius;
    private final Map<TextView,ColorStateList> textColours = new IdentityHashMap<>();
    private final Map<ImageView,ColorStateList> iconColours = new IdentityHashMap<>();
    private boolean focused;
    PreviewContentFocus(Context context) {
        accent=PreviewAccent.color(context);
        radius=3*context.getResources().getDisplayMetrics().density;
    }
    @Override public boolean isStateful(){return true;}
    @Override protected boolean onStateChange(int[] state){
        boolean next=false;for(int value:state)if(value==android.R.attr.state_focused)next=true;
        focused=next;apply();return true;
    }
    @Override public void draw(Canvas canvas){apply();}
    private void apply(){
        Drawable.Callback callback=getCallback();
        while(callback instanceof Drawable)callback=((Drawable)callback).getCallback();
        if(!(callback instanceof View))return;
        if(focused)highlight((View)callback);
        else {
            for(Map.Entry<TextView,ColorStateList> entry:textColours.entrySet()){
                entry.getKey().setTextColor(entry.getValue());entry.getKey().setShadowLayer(0,0,0,0);
            }
            for(Map.Entry<ImageView,ColorStateList> entry:iconColours.entrySet())entry.getKey().setImageTintList(entry.getValue());
            textColours.clear();iconColours.clear();
        }
    }
    private void highlight(View view){
        if(view instanceof TextView&&!textColours.containsKey(view)){
            TextView text=(TextView)view;textColours.put(text,text.getTextColors());
            text.setTextColor(accent);text.setShadowLayer(radius,0,0,accent);
        }else if(view instanceof ImageView&&!iconColours.containsKey(view)){
            ImageView icon=(ImageView)view;iconColours.put(icon,icon.getImageTintList());
            icon.setImageTintList(ColorStateList.valueOf(accent));
        }
        if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)highlight(((ViewGroup)view).getChildAt(i));
    }
}
