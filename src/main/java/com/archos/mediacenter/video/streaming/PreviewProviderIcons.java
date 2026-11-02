package com.archos.mediacenter.video.streaming;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import java.util.*;
import org.json.*;
import com.archos.mediacenter.video.R;

/** Shared authentic transparent marks. Missing assets use a semantic glyph, never an opaque tile. */
public final class PreviewProviderIcons {
 public static Map<String,String> catalogue(Context c){Map<String,String> result=new HashMap<>();try{JSONArray rows=new JSONArray(StreamingRepository.prefs(c).getString("streaming_catalogue_"+StreamingRepository.country(c),"[]"));for(int i=0;i<rows.length();i++){JSONObject p=rows.getJSONObject(i);result.put(p.getString("id"),p.optString("logo"));}}catch(JSONException ignored){}return result;}
 public static int resource(String name){
  String key=name==null?"":name.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+"," ");
  switch(key){
   case "netflix":case "netflix standard with ads":case "netflix basic with ads":return R.drawable.provider_netflix;
   case "apple tv":case "apple tv+":case "apple tv plus":case "apple tv store":return R.drawable.provider_appletv;
   case "paramount+":case "paramount plus":return R.drawable.provider_paramountplus;
   case "hbo":return R.drawable.provider_hbo;
   case "hbo max":return R.drawable.provider_hbomax;
   case "disney+":case "disney plus":return R.drawable.provider_disneyplus_symbol;
   case "amazon prime video":case "prime video":case "amazon prime video with ads":return R.drawable.provider_primevideo_symbol;
   case "britbox":return R.drawable.provider_britbox;
   case "mubi":return R.drawable.provider_mubi;
   case "crunchyroll":return R.drawable.provider_crunchyroll;
   default:return 0;
  }
 }
 private static StreamingRepository.Provider provider(Context c,String path){
  if(path==null||!path.matches("/[A-Za-z0-9._-]+"))return null;
  try{JSONArray rows=new JSONArray(StreamingRepository.prefs(c).getString("streaming_catalogue_"+StreamingRepository.country(c),"[]"));for(int i=0;i<rows.length();i++){JSONObject p=rows.getJSONObject(i);if(path.equals(p.optString("logo")))return new StreamingRepository.Provider(p.getInt("id"),p.getString("name"),path);}}catch(JSONException ignored){}return null;
 }
 public static void bind(Dialog dialog,int row,String path){bind(dialog,row,path,"provider.choice");}
 public static void bind(Dialog dialog,int row,String path,String surface){bindProvider(dialog,row,provider(dialog.getContext(),path),surface);}
 public static void bindProvider(Dialog dialog,int row,StreamingRepository.Provider provider,String surface){
  if(dialog.getWindow()==null)return;View found=dialog.getWindow().getDecorView().findViewWithTag(row);
  if(!(found instanceof ViewGroup)||((ViewGroup)found).getChildCount()==0)return;View child=((ViewGroup)found).getChildAt(0);if(child instanceof ImageView)bindProvider((ImageView)child,provider,0,surface);
 }
 public static void bind(ImageView icon,String path,long media,String surface){bindProvider(icon,provider(icon.getContext(),path),media,surface);}
 public static void bindProvider(ImageView icon,StreamingRepository.Provider provider,long media,String surface){
  com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(icon);icon.clearColorFilter();icon.setBackground(null);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
  int resource=provider==null?0:resource(provider.name);
  // Approved 2 Nov 2026: official single-colour symbol in a uniform tile; a monogram tile is the last resort.
  if(resource!=0)icon.setImageResource(resource);else icon.setImageDrawable(new com.archos.mediacenter.video.leanback.ProviderMonogram(provider==null?"":provider.name));
  if("provider.choice".equals(surface)&&icon.getLayoutParams()!=null){float d=icon.getResources().getDisplayMetrics().density;icon.getLayoutParams().width=Math.round(28*d);icon.getLayoutParams().height=Math.round(17*d);icon.requestLayout();}
 }
 private PreviewProviderIcons(){}
}
