package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.squareup.picasso.Picasso;

/** Shared landscape discovery/episode card with clean artwork and boundary focus. */
final class PreviewLandscapeCard extends LinearLayout {
    final ImageView image,availability;final TextView title,metadata;final ProgressBar progress;
    PreviewLandscapeCard(Context c){super(c);setOrientation(VERTICAL);setFocusable(true);setFocusableInTouchMode(true);setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);setClipChildren(false);setClipToPadding(false);setBackground(PreviewDialog.focus(c));
        FrameLayout art=new FrameLayout(c);addView(art,new LayoutParams(-1,dp(102)));image=new ImageView(c);image.setScaleType(ImageView.ScaleType.CENTER_CROP);art.addView(image,new FrameLayout.LayoutParams(-1,-1));
        availability=new ImageView(c);availability.setImageDrawable(new android.graphics.drawable.Drawable(){final android.graphics.Paint p=new android.graphics.Paint(3);public void draw(android.graphics.Canvas canvas){p.setColor(0xcfffffff);android.graphics.Rect b=getBounds();android.graphics.Path path=new android.graphics.Path();path.moveTo(b.left+b.width()*.2f,b.top);path.lineTo(b.right,b.centerY());path.lineTo(b.left+b.width()*.2f,b.bottom);path.close();canvas.drawPath(path,p);}public void setAlpha(int a){p.setAlpha(a);}public void setColorFilter(android.graphics.ColorFilter f){p.setColorFilter(f);}public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}});availability.setVisibility(INVISIBLE);art.addView(availability,new FrameLayout.LayoutParams(dp(28),dp(28),Gravity.CENTER));
        progress=new ProgressBar(c,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(100);progress.setProgressTintList(android.content.res.ColorStateList.valueOf(PreviewAccent.color(c)));progress.setVisibility(GONE);art.addView(progress,new FrameLayout.LayoutParams(-1,dp(3),Gravity.BOTTOM));
        title=text(13);metadata=text(11);title.setPadding(dp(4),dp(5),dp(4),0);metadata.setPadding(dp(4),0,dp(4),dp(4));addView(title);addView(metadata);
        setOnFocusChangeListener((v,focus)->{availability.setVisibility(Boolean.TRUE.equals(availability.getTag())&&focus?VISIBLE:INVISIBLE);animate().scaleX(focus?1.04f:1f).scaleY(focus?1.04f:1f).setDuration(120).start();});
    }
    void bind(String name,String detail,Uri art,boolean localIndicator){title.setText(name);metadata.setText(detail);metadata.setVisibility(detail==null||detail.isEmpty()?GONE:VISIBLE);availability.setTag(localIndicator);availability.setVisibility(localIndicator&&hasFocus()?VISIBLE:INVISIBLE);Picasso.get().cancelRequest(image);Picasso.get().cancelRequest(availability);image.setImageDrawable(null);if(art!=null)Picasso.get().load(art).resize(dp(360),dp(204)).centerCrop().noFade().into(image);setContentDescription(name+(detail==null?"":" · "+detail));}
    void release(){Picasso.get().cancelRequest(image);Picasso.get().cancelRequest(availability);image.setImageDrawable(null);}
    private TextView text(int size){TextView text=new TextView(getContext());text.setTextSize(size);text.setTextColor(0xffe1e9ef);text.setSingleLine(true);text.setEllipsize(android.text.TextUtils.TruncateAt.END);text.setTypeface(android.graphics.Typeface.create("sans-serif-light",0));return text;}
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
