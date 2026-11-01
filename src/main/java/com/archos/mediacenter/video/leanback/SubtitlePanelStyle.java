package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;

/** Presentation tokens shared only by the approved playback subtitle surfaces. */
public final class SubtitlePanelStyle {
    public final Context context;
    public final float scale;
    public final int width,height;
    public SubtitlePanelStyle(Context c,boolean main){
        context=c;int safeH=Math.min(TvLayout.safeHeight(c),Math.round(c.getResources().getDisplayMetrics().heightPixels*.9f));
        width=Math.min(Math.round(TvLayout.safeWidth(c)*.85f),Math.round(safeH*(main?1067f/945f:1115f/765f)));
        height=main?safeH:Math.min(safeH,Math.round(width*765f/1115f));scale=width/(main?1067f:1115f);
    }
    public int px(float reference){return Math.max(1,Math.round(reference*scale));}
    public TextView text(String label,int size){TextView v=new TextView(context);v.setText(label);v.setTextColor(Color.WHITE);v.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,size*scale*context.getResources().getConfiguration().fontScale);v.setIncludeFontPadding(false);return v;}
    public GradientDrawable surface(int color,int stroke,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(px(radius));if(stroke!=0)d.setStroke(px(2),stroke);return d;}
    public Drawable panelSurface(){return new Glow(0xf5050b14,0xff4f8fcb,36,2,6);}
    public Drawable glow(int radius){return new Glow(0xcc0b2a4a,0xff2fb6ff,radius,3,8);}
    private final class Glow extends Drawable {
        final int fill,border,radius,stroke,blur;final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        Glow(int fill,int border,int radius,int stroke,int blur){this.fill=fill;this.border=border;this.radius=radius;this.stroke=stroke;this.blur=blur;}
        public void draw(Canvas canvas){RectF rect=new RectF(getBounds());rect.inset(px(3),px(3));paint.setStyle(Paint.Style.FILL);paint.setColor(fill);paint.setShadowLayer(px(blur),0,0,(border&0x00ffffff)|0x99000000);canvas.drawRoundRect(rect,px(radius),px(radius),paint);paint.clearShadowLayer();paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(px(stroke));paint.setColor(border);canvas.drawRoundRect(rect,px(radius),px(radius),paint);}
        public void setAlpha(int a){paint.setAlpha(a);invalidateSelf();}public void setColorFilter(ColorFilter f){paint.setColorFilter(f);invalidateSelf();}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    }
    public void scrollbar(ScrollView view){view.setScrollBarSize(px(6));if(android.os.Build.VERSION.SDK_INT>=29){view.setVerticalScrollbarThumbDrawable(surface(0xb36e7f90,0,3));view.setVerticalScrollbarTrackDrawable(new ColorDrawable(Color.TRANSPARENT));}}
    public Drawable focus(){StateListDrawable states=new StateListDrawable();states.addState(new int[]{android.R.attr.state_focused},glow(22));states.addState(new int[]{},surface(Color.TRANSPARENT,0,22));return states;}
    public LinearLayout panel(){LinearLayout panel=new LinearLayout(context);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(px(44),px(24),px(44),px(30));panel.setBackground(panelSurface());panel.setLayerType(View.LAYER_TYPE_SOFTWARE,null);panel.setClipToPadding(false);return panel;}
    public void rule(LinearLayout parent){View line=new View(context);line.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);line.setBackgroundColor(0xb32a4a6b);parent.addView(line,new LinearLayout.LayoutParams(-1,px(1)));}
    public ImageView icon(String kind,int size){ImageView image=new ImageView(context);image.setImageDrawable(new Icon(kind));image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setLayoutParams(new LinearLayout.LayoutParams(px(size),px(size)));return image;}
    public LinearLayout row(String tag){LinearLayout row=new LinearLayout(context);row.setGravity(Gravity.CENTER_VERTICAL);row.setMinimumHeight(Math.round(px(60)*context.getResources().getConfiguration().fontScale));row.setPadding(px(12),px(6),px(12),px(6));row.setTag(tag);row.setFocusable(true);row.setFocusableInTouchMode(true);row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);row.setBackground(focus());row.setLayerType(View.LAYER_TYPE_SOFTWARE,null);return row;}
    public void show(Dialog dialog,View panel,int desiredHeight){dialog.setContentView(panel);dialog.show();Window window=dialog.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);window.setDimAmount(.32f);window.setGravity(Gravity.CENTER);window.setLayout(width,Math.min(height,desiredHeight));}
    public TextView header(LinearLayout panel,String title,Runnable back){TextView v=text("‹   "+title,44);v.setTypeface(null,Typeface.BOLD);v.setFocusable(true);v.setFocusableInTouchMode(true);v.setBackground(focus());v.setPadding(px(4),px(4),px(4),px(22));v.setContentDescription("Back, "+title);v.setOnClickListener(w->back.run());panel.addView(v,new LinearLayout.LayoutParams(-1,-2));return v;}
    /** Thin white semantic marks; country flags are rendered separately from bundled assets. */
    public static final class Icon extends Drawable {
        private final String kind;private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        public Icon(String kind){this.kind=kind;p.setColor(Color.WHITE);p.setStrokeWidth(1.5f);p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);}
        private void line(Canvas c,float... xy){Path path=new Path();path.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)path.lineTo(xy[i],xy[i+1]);c.drawPath(path,p);}
        @Override public void draw(Canvas c){c.save();c.translate(getBounds().left,getBounds().top);c.scale(getBounds().width()/24f,getBounds().height()/24f);
            switch(kind){
                case "cc":c.drawRoundRect(1,5,23,19,2,2,p);p.setStyle(Paint.Style.FILL);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(10);c.drawText("CC",4,16,p);p.setStyle(Paint.Style.STROKE);break;
                case "size":case "outline":p.setStyle(Paint.Style.FILL);p.setTextSize(15);p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));c.drawText(kind.equals("size")?"Aa":"A",3,18,p);p.setStyle(Paint.Style.STROKE);if(kind.equals("outline")){p.setPathEffect(new DashPathEffect(new float[]{2,2},0));c.drawRect(1,2,23,23,p);p.setPathEffect(null);}break;
                case "palette":c.drawOval(2,2,22,22,p);for(float[] xy:new float[][]{{7,8},{12,5},{18,8},{7,15}})c.drawCircle(xy[0],xy[1],1,p);break;
                case "position":line(c,12,2,12,22);line(c,6,8,12,2,18,8);line(c,6,16,12,22,18,16);break;
                case "background":c.drawRect(2,4,22,21,p);for(int i=0;i<24;i+=5)line(c,Math.max(2,i-12),Math.min(21,4+i),Math.min(22,i+4),Math.max(4,i-14));break;
                case "opacity":line(c,12,2,3,15,4,20,9,23,15,23,20,19,20,14,12,2);break;
                case "download":line(c,12,2,12,17);line(c,6,11,12,17,18,11);line(c,3,17,3,22,21,22,21,17);break;
                case "check":line(c,3,12,9,18,21,5);break;
                default:PreviewIcon icon=new PreviewIcon(kind);icon.setBounds(0,0,24,24);icon.draw(c);break;
            }c.restore();
        }
        @Override public void setAlpha(int a){p.setAlpha(a);invalidateSelf();}@Override public void setColorFilter(ColorFilter f){p.setColorFilter(f);invalidateSelf();}@Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    }
}
