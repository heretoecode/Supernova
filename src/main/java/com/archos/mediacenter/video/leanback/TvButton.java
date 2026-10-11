package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

/** One proportional size family for app-owned actions and functional library controls. */
public final class TvButton {
 private TvButton(){}
 public static TextView create(Context c,String label,Runnable action,boolean functional){TextView view=SharedThreePanel.text(c,label,13);style(view,functional);view.setFocusable(true);view.setFocusableInTouchMode(true);view.setClickable(true);view.setOnClickListener(v->action.run());return view;}
 public static void style(TextView view,boolean functional){Context c=view.getContext();float ratio=functional?1.12f:1f;view.setTextSize(13*ratio*TvLayout.scale(c));view.setGravity(Gravity.CENTER);view.setMinimumHeight(TvLayout.dimension(c,44*ratio));int padding=TvLayout.dimension(c,16*ratio);view.setPadding(padding,TvLayout.dimension(c,8*ratio),padding,TvLayout.dimension(c,8*ratio));view.setBackground(SharedThreePanel.control(c,functional?11:10));PreviewIcon.apply(view,view.getText().toString(),Math.round(18*ratio*TvLayout.scale(c)));view.setCompoundDrawablePadding(TvLayout.dimension(c,8*ratio));view.setTextColor(android.graphics.Color.WHITE);view.setSingleLine(true);view.setEllipsize(android.text.TextUtils.TruncateAt.END);}
}
