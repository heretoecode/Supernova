package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Shared national flags with explicit regional precedence and approved CLDR defaults. */
public final class PreviewLanguageIcon extends Drawable {
    private static final Set<String> COUNTRIES=new HashSet<>(Arrays.asList(Locale.getISOCountries()));
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PreviewIcon neutral=new PreviewIcon("language");
    private final String flag;
    private final String label;
    private final String region;
    private final android.content.Context context;
    private static final android.util.LruCache<String,Bitmap> FLAGS=new android.util.LruCache<>(32);
    public PreviewLanguageIcon(String code){this(com.archos.mediacenter.video.CustomApplication.getAppContext(),code);}
    public PreviewLanguageIcon(android.content.Context context,String code){
        this.context=context;
        String country=country(code);region=country;label=languageLabel(code);
        flag=country.isEmpty()?"":new String(Character.toChars(0x1f1e6+country.charAt(0)-'A'))+new String(Character.toChars(0x1f1e6+country.charAt(1)-'A'));
        paint.setColor(Color.WHITE);paint.setTextAlign(Paint.Align.CENTER);
    }
    /** Language identifiers are useful without assigning an ambiguous country flag. */
    static String languageLabel(String code){
        if(code==null||!code.matches("(?i)[a-z]{2,3}(?:[-_][a-z0-9]{2,8})*"))return "";
        String language=code.split("[-_]")[0].toLowerCase(Locale.ROOT);
        for(String iso:Locale.getISOLanguages()){
            if(iso.equals(language))return iso.toUpperCase(Locale.ROOT);
            try{if(new Locale(iso).getISO3Language().equals(language))return iso.toUpperCase(Locale.ROOT);}catch(MissingResourceException unavailable){}
        }
        return "";
    }
    static String country(String code){
        if(code==null||!code.matches("(?i)[a-z]{2,3}(?:[-_][a-z0-9]{2,8})*"))return "";
        Locale locale=Locale.forLanguageTag(code.replace('_','-'));String explicit=locale.getCountry().toUpperCase(Locale.ROOT);
        if(!explicit.isEmpty())return COUNTRIES.contains(explicit)?explicit:"";
        String language=languageLabel(code).toLowerCase(Locale.ROOT);if(language.isEmpty())return "";
        String standard=PreviewLanguageRegions.DEFAULTS.getOrDefault(language,"");return COUNTRIES.contains(standard)?standard:"";
    }
    @Override public void draw(Canvas canvas){
        Rect bounds=getBounds();
        if(!region.isEmpty()){
            Bitmap bitmap=FLAGS.get(region);
            if(bitmap==null){android.content.Context app=context;
                if(app!=null){int id=app.getResources().getIdentifier("preview_flag_"+region.toLowerCase(Locale.ROOT),"drawable",app.getPackageName());if(id!=0){bitmap=BitmapFactory.decodeResource(app.getResources(),id);if(bitmap!=null)FLAGS.put(region,bitmap);}}}
            if(bitmap!=null){float scale=Math.min(bounds.width()/(float)bitmap.getWidth(),bounds.height()/(float)bitmap.getHeight());float w=bitmap.getWidth()*scale,h=bitmap.getHeight()*scale;canvas.drawBitmap(bitmap,null,new RectF(bounds.exactCenterX()-w/2,bounds.exactCenterY()-h/2,bounds.exactCenterX()+w/2,bounds.exactCenterY()+h/2),paint);return;}
        }
        // Undefined/invalid metadata has no country. Never present a different language.
        neutral.setBounds(bounds);neutral.draw(canvas);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);neutral.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);neutral.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    public static void bind(Dialog dialog,int index,String code){
        if(dialog==null||dialog.getWindow()==null)return;
        View label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:"+index);
        if(label==null||!(label.getParent() instanceof ViewGroup))return;
        ViewGroup row=(ViewGroup)label.getParent();
        if(row.getChildCount()>0&&row.getChildAt(0) instanceof ImageView)((ImageView)row.getChildAt(0)).setImageDrawable(new PreviewLanguageIcon(dialog.getContext(),code));
    }
}
