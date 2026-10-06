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
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PreviewIcon neutral=new PreviewIcon("language");
    private final String flag;
    private final String label;
    public PreviewLanguageIcon(String code){
        String country=country(code);label=languageLabel(code);
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
        if(code==null||!code.matches("(?i)[a-z]{2,3}(?:[-_][a-z0-9]{2,8})+"))return "";
        Locale locale=Locale.forLanguageTag(code.replace('_','-'));
        String country=locale.getCountry().toUpperCase(Locale.ROOT);
        return COUNTRIES.contains(country)?country:"";
    }
    @Override public void draw(Canvas canvas){
        Rect bounds=getBounds();
        // Shield/system font availability varies. Missing flag glyphs use the neutral icon,
        // never two regional-indicator letters or an invented substitute flag.
        if(flag.isEmpty()||android.os.Build.VERSION.SDK_INT<23||!paint.hasGlyph(flag)){
            if(label.isEmpty()){neutral.setBounds(bounds);neutral.draw(canvas);}else{
                paint.setTextSize(bounds.height()*.42f);paint.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));
                Paint.FontMetrics metrics=paint.getFontMetrics();canvas.drawText(label,bounds.exactCenterX(),bounds.exactCenterY()-(metrics.ascent+metrics.descent)/2,paint);
            }return;
        }
        paint.setTextSize(bounds.height()*.85f);Paint.FontMetrics metrics=paint.getFontMetrics();
        canvas.drawText(flag,bounds.exactCenterX(),bounds.exactCenterY()-(metrics.ascent+metrics.descent)/2,paint);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);neutral.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);neutral.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    public static void bind(Dialog dialog,int index,String code){
        if(dialog==null||dialog.getWindow()==null)return;
        View label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:"+index);
        if(label==null||!(label.getParent() instanceof ViewGroup))return;
        ViewGroup row=(ViewGroup)label.getParent();
        if(row.getChildCount()>0&&row.getChildAt(0) instanceof ImageView)((ImageView)row.getChildAt(0)).setImageDrawable(new PreviewLanguageIcon(code));
    }
}
