package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediascraper.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import okhttp3.*;
import org.json.*;
import com.archos.mediacenter.video.diagnostics.Diagnostics;

/** Genuine TMDb title logos. All lookup/decode is off the UI thread; playback only reads cache.
 * Text remains the accessibility/fallback title and fixes layout geometry even when artwork loads. */
public final class OfficialTitleArtwork {
 private static final ExecutorService IO=Executors.newFixedThreadPool(2);
 private static final OkHttpClient HTTP=new OkHttpClient.Builder().connectTimeout(5,TimeUnit.SECONDS).readTimeout(7,TimeUnit.SECONDS).callTimeout(10,TimeUnit.SECONDS).build();
 private static final Map<TextView,String> BOUND=new WeakHashMap<>();
 private static final Map<TextView,Boolean> READY=new WeakHashMap<>();
 private static final Map<TextView,com.archos.mediacenter.video.diagnostics.ArtworkTrace> TRACES=new WeakHashMap<>();
 private static final android.util.LruCache<String,Bitmap> ALIASES=new android.util.LruCache<>(12);
 public static boolean readyForFirstFrame(TextView view){return !Boolean.FALSE.equals(READY.get(view));}
 public static void bindRemote(TextView view,String kind,long id){
  if(id<=0||!("movie".equals(kind)||"tv".equals(kind))){clear(view);return;}
  String key=kind+"/"+id;request(view,key,false,()->key,null);
 }
 private static final android.util.LruCache<String,Bitmap> MEMORY=new android.util.LruCache<>(12);
 public static void bind(TextView view,Base media,boolean cachedOnly){
  bind(view,media,cachedOnly,null);
 }
 public static void bind(TextView view,Base media,boolean cachedOnly,Runnable changed){
  if(media==null){clear(view);return;}String identity=media instanceof Tvshow?"show-local:"+((Tvshow)media).getTvshowId():media instanceof Video?"video-local:"+((Video)media).getId():media.getName();
  request(view,identity,cachedOnly,()->{BaseTags tags=media.getFullScraperTags(view.getContext().getApplicationContext());if(tags instanceof EpisodeTags)tags=((EpisodeTags)tags).getShowTags();return tags==null||tags.getOnlineId()<=0?null:(media instanceof Movie?"movie/":"tv/")+tags.getOnlineId();},changed);
 }
 public static void bind(TextView view,VideoDbInfo info){
  String id=info==null?null:info.isShow?info.scraperShowId:info.scraperMovieId;
  if(id==null||!id.matches("[1-9][0-9]*")){clear(view);return;}String key=(info.isShow?"tv/":"movie/")+id;request(view,key,true,()->key,null);
 }
 private static void clear(TextView view){com.archos.mediacenter.video.diagnostics.ArtworkTrace trace=TRACES.remove(view);if(trace!=null)trace.cancelled();BOUND.remove(view);READY.put(view,true);view.setForeground(null);view.setTextColor(0xffffffff);}
 private static void request(TextView view,String identity,boolean cachedOnly,Callable<String> resolve,Runnable changed){
  if(identity.equals(BOUND.get(view)))return;clear(view);BOUND.put(view,identity);
  long media=0;if(identity.matches("(?:video|show)-local:[0-9]+"))try{media=Long.parseLong(identity.substring(identity.indexOf(':')+1));}catch(NumberFormatException ignored){}
  if(identity.matches("(?:movie|tv)/[0-9]+"))try{media=Long.parseLong(identity.substring(identity.indexOf('/')+1));}catch(NumberFormatException ignored){}
  com.archos.mediacenter.video.diagnostics.ArtworkTrace trace=new com.archos.mediacenter.video.diagnostics.ArtworkTrace(media,cachedOnly?"playback_loading":com.archos.mediacenter.video.diagnostics.Diagnostics.artworkSurface(),"title_logo",cachedOnly?"cache_only":"tmdb");TRACES.put(view,trace);
  String alias=identity+":"+Locale.getDefault().getLanguage();Bitmap existing=ALIASES.get(alias);
  if(existing!=null){view.setForeground(new Logo(existing,"semantic:featured.title".equals(view.getTag())));view.setTextColor(Color.TRANSPARENT);READY.put(view,true);trace.ready("memory_alias");if(changed!=null)changed.run();return;}
  READY.put(view,false);
  Context app=view.getContext().getApplicationContext();WeakReference<TextView> weak=new WeakReference<>(view);
  IO.execute(()->{boolean submitted=false;String cacheLayer="memory";try{
   String key=resolve.call();if(key==null)return;String language=Locale.getDefault().getLanguage();String diskKey=key.replace('/','_')+"_"+language;
   Bitmap bitmap=MEMORY.get(diskKey);File directory=new File(app.getCacheDir(),"official-title-artwork"),file=new File(directory,diskKey+".png");
   if(bitmap==null&&file.isFile()){bitmap=BitmapFactory.decodeFile(file.getPath());cacheLayer="disk";}
   if(bitmap==null&&!cachedOnly){android.content.SharedPreferences cache=app.getSharedPreferences("preview_title_logos",0);long last=cache.getLong(diskKey,0);
    cacheLayer="network";
    if(System.currentTimeMillis()-last<86400000L)return;
    android.net.Uri uri=android.net.Uri.parse("https://api.themoviedb.org/3/"+key+"/images").buildUpon().appendQueryParameter("api_key",app.getString(com.archos.medialib.R.string.tmdb_api_key)).appendQueryParameter("include_image_language",language+",en,null").build();
    JSONObject result=new JSONObject(new String(download(uri.toString(),2*1024*1024,trace.operationId(),"logo_metadata"),java.nio.charset.StandardCharsets.UTF_8));
    if(result.optLong("id",-1)!=Long.parseLong(key.substring(key.indexOf('/')+1))||result.optJSONArray("logos")==null)throw new IOException("Invalid artwork metadata");
    String path=select(result.optJSONArray("logos"),language);cache.edit().putLong(diskKey,System.currentTimeMillis()).apply();
    if(!path.isEmpty()){byte[] bytes=download("https://image.tmdb.org/t/p/w500"+path,4*1024*1024,trace.operationId(),"logo_image");BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);if(options.outWidth<=0||options.outHeight<=0||options.outWidth>4096||options.outHeight>4096)throw new IOException("Invalid artwork dimensions");bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);if(bitmap==null)throw new IOException("Invalid artwork bitmap");if(bitmap!=null){directory.mkdirs();android.util.AtomicFile atomic=new android.util.AtomicFile(file);FileOutputStream out=null;try{out=atomic.startWrite();out.write(bytes);atomic.finishWrite(out);}catch(IOException e){atomic.failWrite(out);}}}
   }
   if(bitmap==null)return;bitmap=visibleArtwork(bitmap);MEMORY.put(diskKey,bitmap);ALIASES.put(alias,bitmap);final Bitmap ready=bitmap;final String origin=cacheLayer;TextView target=weak.get();if(target!=null)submitted=target.post(()->{TextView current=weak.get();if(current==null||!identity.equals(BOUND.get(current))){trace.cancelled();return;}current.setForeground(new Logo(ready,"semantic:featured.title".equals(current.getTag())));current.setTextColor(Color.TRANSPARENT);trace.ready(origin);if(changed!=null)changed.run();});
  }catch(Exception unavailable){trace.failed(unavailable.getClass().getSimpleName(),true);}
  finally{if(!submitted)trace.fallback("no_cached_or_available_logo");TextView target=weak.get();if(target!=null)target.post(()->{TextView current=weak.get();if(current!=null&&identity.equals(BOUND.get(current)))READY.put(current,true);});}});
 }
 static int synopsisWidth(int viewportWidth,float visibleLogoWidth){return Math.round(Math.max(viewportWidth*.25f,Math.min(viewportWidth*.32f,visibleLogoWidth*.9f)));}
 public static int synopsisWidth(TextView title,int viewportWidth){
  int width=title.getWidth()>0?title.getWidth():title.getLayoutParams().width,height=title.getHeight()>0?title.getHeight():title.getLayoutParams().height;
  float visible=Math.min(width,title.getPaint().measureText(title.getText().toString()));
  if(title.getForeground() instanceof Logo){Bitmap image=((Logo)title.getForeground()).bitmap;visible=image.getWidth()*Math.min(width/(float)image.getWidth(),height/(float)image.getHeight());}
  return synopsisWidth(viewportWidth,visible);
 }
 static String select(JSONArray logos,String language)throws JSONException{String chosen="";double best=-1;if(logos==null)return chosen;for(int i=0;i<logos.length();i++){JSONObject logo=logos.getJSONObject(i);String path=logo.optString("file_path"),lang=logo.optString("iso_639_1");if(!path.matches("/[A-Za-z0-9._-]+\\.png"))continue;double score=(language.equals(lang)?30:"en".equals(lang)?20:lang.isEmpty()||"null".equals(lang)?10:0)+Math.min(9,logo.optDouble("vote_average",0));if(score>best){best=score;chosen=path;}}return chosen;}
 private static byte[] download(String url,int limit,String parent,String kind)throws IOException{
  return download(HTTP,url,limit,parent,kind);
 }
 static byte[] download(OkHttpClient client,String url,int limit,String parent,String kind)throws IOException{
  String operation=Diagnostics.operation("artwork_network"),outcome="transport_failed";long started=android.os.SystemClock.elapsedRealtime();int status=0;
  try(Response response=client.newCall(new Request.Builder().url(url).build()).execute()){
   status=response.code();if(!response.isSuccessful()){outcome="http_error";throw new IOException("Artwork unavailable");}
   if(response.body()==null){outcome="empty_body";throw new IOException("Artwork unavailable");}
   try(InputStream in=response.body().byteStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){
    byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1){if(Thread.currentThread().isInterrupted()){outcome="cancelled";throw new IOException("Artwork cancelled");}if(out.size()+n>limit){outcome="size_limit";throw new IOException("Artwork too large");}out.write(b,0,n);}
    outcome="transport_complete";return out.toByteArray();
   }
  }finally{
   Diagnostics.event("transport_complete".equals(outcome)?"provider_request_complete":"provider_request_failed","operation_id",operation,"parent_operation_id",parent,
       "provider","tmdb","operation_type",kind,"status",status,"outcome",outcome,"duration_ms",android.os.SystemClock.elapsedRealtime()-started,"retry_number",0,"connectivity",Diagnostics.connectivity());
   Diagnostics.finishOperation(operation,"artwork_network",started);
  }
 }
 /** Decode-worker only. Ignore near-transparent compression/shadow pixels when finding ink. */
 static Bitmap visibleArtwork(Bitmap bitmap){
  if(!bitmap.hasAlpha())return bitmap;
  int w=bitmap.getWidth(),h=bitmap.getHeight(),left=w,top=h,right=-1,bottom=-1;int[] row=new int[w];
  for(int y=0;y<h;y++){bitmap.getPixels(row,0,w,0,y,w,1);for(int x=0;x<w;x++)if((row[x]>>>24)>=24){left=Math.min(left,x);right=Math.max(right,x);top=Math.min(top,y);bottom=y;}}
  if(right<left)return bitmap;
  int pad=Math.max(1,Math.round(Math.max(right-left,bottom-top)*.015f));left=Math.max(0,left-pad);top=Math.max(0,top-pad);right=Math.min(w-1,right+pad);bottom=Math.min(h-1,bottom+pad);
  return left==0&&top==0&&right==w-1&&bottom==h-1?bitmap:Bitmap.createBitmap(bitmap,left,top,right-left+1,bottom-top+1);
 }
 private static final class Logo extends Drawable{private final Bitmap bitmap;private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);private final boolean contrast;Logo(Bitmap bitmap){this(bitmap,false);}Logo(Bitmap bitmap,boolean contrast){this.bitmap=bitmap;this.contrast=contrast;}public void draw(Canvas canvas){Rect b=getBounds();float scale=Math.min(b.width()/(float)bitmap.getWidth(),b.height()/(float)bitmap.getHeight());float width=bitmap.getWidth()*scale,height=bitmap.getHeight()*scale;if(contrast){Paint shadow=new Paint(paint);shadow.setColorFilter(new PorterDuffColorFilter(Color.BLACK,PorterDuff.Mode.SRC_IN));shadow.setAlpha(180);for(int[] offset:new int[][]{{-2,0},{2,0},{0,-2},{0,2}})canvas.drawBitmap(bitmap,null,new RectF(b.left+offset[0],b.top+offset[1],b.left+width+offset[0],b.top+height+offset[1]),shadow);}canvas.drawBitmap(bitmap,null,new RectF(b.left,b.top,b.left+width,b.top+height),paint);}public void setAlpha(int alpha){paint.setAlpha(alpha);}public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}public int getOpacity(){return PixelFormat.TRANSLUCENT;}}
 private OfficialTitleArtwork(){}
}
