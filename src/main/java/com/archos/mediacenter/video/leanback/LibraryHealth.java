package com.archos.mediacenter.video.leanback;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.*;
/** Availability is separate from IDs, metadata, bookmarks, watched state and row membership. */
public final class LibraryHealth {
 public enum State { AVAILABLE, SOURCE_OFFLINE, MISSING }
 public static State state(Context c,Video video){
  SharedPreferences health=c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,Context.MODE_PRIVATE);
  String path=video.getFileUri()==null?"":SupernovaLibraryPolicy.canonical(video.getFileUri());
  for(String key:health.getAll().keySet())if(key.startsWith("offline:")&&SupernovaLibraryPolicy.contains(key.substring(8),path))return State.SOURCE_OFFLINE;
  return health.contains("media:"+video.getId())?State.MISSING:State.AVAILABLE;
 }
 public static String message(State state){return state==State.SOURCE_OFFLINE?"Source offline":state==State.MISSING?"Missing media":"Available";}
 public static String report(Context c){
  SharedPreferences health=c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,Context.MODE_PRIVATE);List<String> offline=new ArrayList<>(),missing=new ArrayList<>();
  for(Map.Entry<String,?> entry:health.getAll().entrySet())if(entry.getKey().startsWith("offline:"))offline.add(entry.getKey().substring(8));else if(entry.getKey().startsWith("media:"))missing.add(String.valueOf(entry.getValue()));
  Collections.sort(offline);Collections.sort(missing);
  StringBuilder text=new StringBuilder("Library records, metadata, watched state and playback progress are retained when storage is unavailable. Reconnect the source and scan to reconcile the same IDs.\n\nSources offline: ").append(offline.size());
  for(String root:offline)text.append("\n").append(root);text.append("\n\nConfirmed missing: ").append(missing.size());for(String path:missing)text.append("\n").append(path);return text.toString();
 }
 public static boolean needsAttention(Context c,PreviewLibraryLoader.Snapshot snapshot){
  SharedPreferences health=c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);if(!health.getAll().isEmpty())return true;
  if(com.archos.mediaprovider.ImportState.VIDEO.isInitialImport()||com.archos.mediaprovider.ImportState.VIDEO.isRegularImport()||com.archos.mediaprovider.video.LoaderUtils.getScrapeInProgress())return false;
  if(!snapshot.unmatched.isEmpty())return true;for(PreviewLibraryLoader.Entry entry:snapshot.technical)if(entry.media instanceof Video){Video video=(Video)entry.media;if((video instanceof com.archos.mediacenter.video.browser.adapters.object.Movie||video instanceof com.archos.mediacenter.video.browser.adapters.object.Episode)&&(video.getPosterUri()==null||LibraryHealthActivity.plot(video).trim().isEmpty()))return true;}return false;
 }
 private LibraryHealth(){}
}
