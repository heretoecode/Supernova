package com.archos.mediacenter.video.utils;

import android.content.*;
import android.database.Cursor;
import androidx.preference.PreferenceManager;
import com.archos.mediascraper.*;
import com.archos.mediaprovider.video.VideoStore;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.*;

/** Portable configuration complements the existing database snapshot; downloadable caches are excluded. */
public final class MigrationBackup {
 private static final Set<String> CACHES=new HashSet<>(Arrays.asList("preview_people","preview_tv_trailers","preview_discovery","preview_title_logos","player"));
 public static String settings(Context c)throws JSONException{
  JSONObject result=new JSONObject();File folder=new File(c.getApplicationInfo().dataDir,"shared_prefs");File[] files=folder.listFiles();
  if(files!=null)for(File file:files){String name=file.getName();if(!name.endsWith(".xml"))continue;name=name.substring(0,name.length()-4);if(!name.matches("[A-Za-z0-9_.-]+")||CACHES.contains(name)||name.equals(c.getPackageName()+"_preferences"))continue;result.put(name,new JSONObject(SettingsBackup.encode(c.getSharedPreferences(name,0))));}
  return result.toString();
 }
 public static Map<String,String> validateSettings(Context c,String json)throws JSONException{
  Map<String,String> result=new LinkedHashMap<>();JSONObject root=new JSONObject(json);Iterator<String> names=root.keys();while(names.hasNext()){String name=names.next();if(!name.matches("[A-Za-z0-9_.-]+")||CACHES.contains(name))throw new JSONException("Unsupported preference file");String data=root.getJSONObject(name).toString();SettingsBackup.decode(c.getSharedPreferences(name,0),data);result.put(name,data);}return result;
 }
 /** Gather before taking the media-database snapshot lock, to avoid provider re-entry deadlocks. */
 public static Set<String> reproducibleFiles(Context c)throws IOException{
  Set<String> result=new HashSet<>(),queries=new HashSet<>();
  for(ScraperImage.Type type:ScraperImage.Type.values()){
   if(!queries.add(type.baseUri+":"+type.largeFileColumn))continue;
   try(Cursor cursor=c.getContentResolver().query(type.baseUri,new String[]{type.largeFileColumn,type.largeUrlColumn,type.thumbFileColumn,type.thumbUrlColumn},null,null,null)){
    if(cursor!=null)while(cursor.moveToNext())for(int offset:new int[]{0,2}){String path=cursor.getString(offset),url=cursor.getString(offset+1);if(path!=null&&url!=null&&(url.startsWith("https://")||url.startsWith("http://")))result.add(new File(path).getCanonicalPath());}
   }catch(RuntimeException failure){throw new IOException("Could not distinguish downloaded artwork from personal artwork",failure);}
  }
  return result;
 }
 private static final AtomicBoolean RESTORING=new AtomicBoolean();
 public static void resumeArtwork(Context context){
  Context c=context.getApplicationContext();SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(c);
  if(!p.getBoolean("preview_restore_artwork_pending",false)||!RESTORING.compareAndSet(false,true))return;
  new Thread(()->{boolean complete=true;Set<String> seen=new HashSet<>();try{
   com.archos.mediaprovider.video.NetworkAutoRefresh.forceRescan(c);
   String id=VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_ID,type=VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE;
   try(Cursor cursor=c.getContentResolver().query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,new String[]{id,type},id+" > 0",null,null)){
    if(cursor!=null)while(cursor.moveToNext()){
     if(!com.archos.environment.NetworkState.isNetworkConnected(c)){complete=false;break;}
     long scraper=cursor.getLong(0);int kind=cursor.getInt(1);if(!seen.add(kind+":"+scraper))continue;
     BaseTags tags=kind==BaseTags.MOVIE?TagsFactory.buildMovieTags(c,scraper):TagsFactory.buildEpisodeTags(c,scraper);
     if(tags==null)continue;if(tags instanceof EpisodeTags){ShowTags show=((EpisodeTags)tags).getShowTags();if(show!=null&&seen.add("show:"+show.getOnlineId()))complete=download(show,c)&&complete;}
     complete=download(tags,c)&&complete;
    }
   }
   if(complete)p.edit().remove("preview_restore_artwork_pending").apply();
  }catch(Exception failure){android.util.Log.w("SupernovaBackup","Artwork restoration will retry on return",failure);}finally{RESTORING.set(false);}},"supernova-restore-artwork").start();
 }
 private static boolean download(BaseTags tags,Context c){boolean ok=true;for(ScraperImage image:new ScraperImage[]{tags.getDefaultPoster(),tags.getDefaultBackdrop()})if(image!=null&&image.getLargeUrl()!=null&&(image.getLargeFileF()==null||!image.getLargeFileF().isFile()))ok=image.download(c)&&ok;return ok;}
 private MigrationBackup(){}
}
