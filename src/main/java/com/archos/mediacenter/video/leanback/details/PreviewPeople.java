package com.archos.mediacenter.video.leanback.details;
import android.content.Context;
import android.widget.ImageView;
import com.archos.mediascraper.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.json.*;
import java.util.*;
import java.util.concurrent.*;
import com.squareup.picasso.Picasso;
final class PreviewPeople {
 private static final ExecutorService worker=Executors.newSingleThreadExecutor();
 static void load(Context context,BaseTags tags,Map<String,ImageView> targets){
  if(tags==null||targets.isEmpty())return;BaseTags source=tags instanceof EpisodeTags?((EpisodeTags)tags).getShowTags():tags;if(source==null||source.getOnlineId()<=0)return;
  String key=(source instanceof MovieTags?"movie:":"tv:")+source.getOnlineId();android.content.SharedPreferences cache=context.getSharedPreferences("preview_people",0);
  for(Map.Entry<String,ImageView> e:targets.entrySet()){String path=cache.getString(key+":"+e.getKey(),null);if(path!=null)image(e.getValue(),path,source.getOnlineId());}
  if(System.currentTimeMillis()-cache.getLong(key+":updated",0)<7L*86400000)return;
  worker.execute(()->{try{
   JSONObject credits=StreamingRepository.metadata(context,source instanceof MovieTags?"movie":"tv",source.getOnlineId(),"credits");
   Map<String,String> paths=new HashMap<>();
   for(String section:new String[]{"cast","crew"}){JSONArray values=credits.optJSONArray(section);if(values==null)continue;
    for(int n=0;n<values.length();n++){JSONObject person=values.optJSONObject(n);if(person==null)continue;String name=person.optString("name"),path=person.optString("profile_path");if(!name.isEmpty()&&path.matches("/[A-Za-z0-9._-]+"))paths.put(name,path);}}
   android.content.SharedPreferences.Editor save=cache.edit();for(Map.Entry<String,String> e:paths.entrySet())save.putString(key+":"+e.getKey(),e.getValue());save.putLong(key+":updated",System.currentTimeMillis()).apply();
   for(Map.Entry<String,ImageView> e:targets.entrySet()){String path=paths.get(e.getKey());if(path!=null)e.getValue().post(()->{if(e.getValue().isAttachedToWindow())image(e.getValue(),path,source.getOnlineId());});}
  }catch(Exception unavailable){/* Keep cached portrait or neutral silhouette. */}});
 }
 private static void image(ImageView view,String path,long media){if(!path.matches("/[A-Za-z0-9._-]+"))return;android.net.Uri uri=android.net.Uri.parse("https://image.tmdb.org/t/p/w185"+path);com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(view,uri,media,"details.people","portrait",Picasso.get().load(uri).fit().centerCrop().placeholder(com.archos.mediacenter.video.R.drawable.preview_person).error(com.archos.mediacenter.video.R.drawable.preview_person),true);}
}
