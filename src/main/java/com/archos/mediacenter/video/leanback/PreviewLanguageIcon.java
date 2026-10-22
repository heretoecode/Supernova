package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import java.util.*;

/** A country flag only for an explicitly regional locale; never infer a country from a language. */
public final class PreviewLanguageIcon extends Drawable {
    private static final Set<String> COUNTRIES=new HashSet<>(Arrays.asList(Locale.getISOCountries()));
    private static final Set<String> LANGUAGES=new HashSet<>();
    static {for(String language:Locale.getISOLanguages()){LANGUAGES.add(language);try{LANGUAGES.add(new Locale(language).getISO3Language());}catch(MissingResourceException ignored){}}}
    private static final android.util.LruCache<String,Bitmap> FLAGS=new android.util.LruCache<>(32);
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final PreviewIcon neutral=new PreviewIcon("language");
    private final Bitmap flag;
    /** Contextless callers retain the neutral safe fallback. App consumers use bundled resources. */
    public PreviewLanguageIcon(String code){this(null,code);}
    public PreviewLanguageIcon(android.content.Context context,String code){
        String country=country(code);Bitmap bitmap=null;
        if(context!=null&&!country.isEmpty()){
            synchronized(FLAGS){bitmap=FLAGS.get(country);if(bitmap==null){int id=context.getResources().getIdentifier("region_"+country.toLowerCase(Locale.ROOT),"drawable",context.getPackageName());if(id!=0){bitmap=BitmapFactory.decodeResource(context.getResources(),id);if(bitmap!=null)FLAGS.put(country,bitmap);}}}
        }
        flag=bitmap;
    }
    static String country(String code){
        if(code==null||"zh-ca".equalsIgnoreCase(code)||!code.matches("(?i)[a-z]{2,3}(?:[-_][a-z0-9]{2,8})+"))return "";
        Locale locale=Locale.forLanguageTag(code.replace('_','-'));
        String country=locale.getCountry().toUpperCase(Locale.ROOT);
        return LANGUAGES.contains(locale.getLanguage())&&COUNTRIES.contains(country)?country:"";
    }
    @Override public void draw(Canvas canvas){
        Rect bounds=getBounds();
        if(flag==null){neutral.setBounds(bounds);neutral.draw(canvas);return;}
        canvas.drawBitmap(flag,null,bounds,paint);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);neutral.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){neutral.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    /** OpenSubtitles uses zh-ca for Cantonese, not a reliable Canadian region. */
    public static String providerCode(String code){return "zh-ca".equalsIgnoreCase(code)?"yue":code;}
    public static void apply(TextView text,String code){
        PreviewLanguageIcon icon=new PreviewLanguageIcon(text.getContext(),code);int size=PreviewDialog.dp(text.getContext(),20);icon.setBounds(0,0,size,size);
        android.graphics.drawable.Drawable[] existing=text.getCompoundDrawables();text.setCompoundDrawables(icon,existing[1],existing[2],existing[3]);text.setCompoundDrawablePadding(PreviewDialog.dp(text.getContext(),8));
    }
    public static void bind(Dialog dialog,int index,String code){
        if(dialog==null||dialog.getWindow()==null)return;
        View label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:"+index);
        if(label==null||!(label.getParent() instanceof ViewGroup))return;
        ViewGroup row=(ViewGroup)label.getParent();
        if(row.getChildCount()>0&&row.getChildAt(0) instanceof ImageView)((ImageView)row.getChildAt(0)).setImageDrawable(new PreviewLanguageIcon(row.getContext(),code));
    }
}
