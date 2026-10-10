package com.archos.mediacenter.video.streaming;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.*;
import org.json.*;

/** Genuine catalogue artwork; never substitutes another service's logo. */
public final class PreviewProviderIcons {
 public static Map<String,String> catalogue(Context c){Map<String,String> result=new HashMap<>();try{JSONArray rows=new JSONArray(StreamingRepository.prefs(c).getString("streaming_catalogue_"+StreamingRepository.country(c),"[]"));for(int i=0;i<rows.length();i++){JSONObject p=rows.getJSONObject(i);result.put(p.getString("id"),p.optString("logo"));}}catch(Exception ignored){}return result;}
 public static void bind(Dialog dialog,int row,String path){bind(dialog,row,path,"provider.choice");}
 public static void bind(Dialog dialog,int row,String path,String surface){
  if(dialog.getWindow()==null)return;
  View found=dialog.getWindow().getDecorView().findViewWithTag(row);
  if(!(found instanceof ViewGroup)||((ViewGroup)found).getChildCount()==0)return;
  View child=((ViewGroup)found).getChildAt(0);if(!(child instanceof ImageView))return;
  ImageView icon=(ImageView)child;
  bind(icon,path,0,"library.filters".equals(surface)?"library.filters":"provider.choice");
 }
 /** My Providers uses the catalogue's colours; action and availability marks remain monochrome. */
 public static void bind(ImageView icon,String path,long media,String surface){
  com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(icon);
  // Desaturate rather than replacing every opaque pixel with white: catalogue
  // logos can have opaque backgrounds whose internal brand shape must survive.
  if("provider.choice".equals(surface))icon.clearColorFilter();
  else {android.graphics.ColorMatrix monochrome=new android.graphics.ColorMatrix();monochrome.setSaturation(0);
   icon.setColorFilter(new android.graphics.ColorMatrixColorFilter(monochrome));}
  android.graphics.drawable.Drawable fallback=new com.archos.mediacenter.video.leanback.PreviewIcon("streaming");
  icon.setImageDrawable(fallback);
  if(path==null||!path.matches("/[A-Za-z0-9._-]+"))return;
  android.net.Uri uri=android.net.Uri.parse("https://image.tmdb.org/t/p/w92"+path);
  com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(icon,uri,media,
    surface,"provider_logo",
    Picasso.get().load(uri).fit().centerInside().placeholder(fallback).error(fallback),true);
  icon.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){
   public void onViewAttachedToWindow(View view){}
   public void onViewDetachedFromWindow(View view){com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(icon);icon.removeOnAttachStateChangeListener(this);}
  });
 }
 private PreviewProviderIcons(){}
}
