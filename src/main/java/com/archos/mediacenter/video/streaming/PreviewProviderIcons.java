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
 public static void bind(Dialog dialog,int row,String path){if(dialog.getWindow()==null||path==null||!path.matches("/[A-Za-z0-9._-]+"))return;View found=dialog.getWindow().getDecorView().findViewWithTag(row);if(!(found instanceof ViewGroup))return;View icon=((ViewGroup)found).getChildAt(0);if(icon instanceof ImageView)Picasso.get().load("https://image.tmdb.org/t/p/w92"+path).fit().centerInside().into((ImageView)icon);}
 private PreviewProviderIcons(){}
}
