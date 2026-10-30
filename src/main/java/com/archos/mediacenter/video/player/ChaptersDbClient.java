package com.archos.mediacenter.video.player;

import android.content.Context;
import android.util.AtomicFile;
import org.json.*;
import java.io.*;
import okhttp3.*;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** Published ChaptersDB v1 GET contract. ID matching and bounds are necessary, not edition proof. */
public final class ChaptersDbClient {
    interface Source { JSONObject get(String route)throws Exception; }
    public enum Status { EMPTY, MISSING_ID, IDENTITY_MISMATCH, UNVERIFIED_ALIGNMENT }
    public static final class ChapterSet {
        public final String id,note;
        public final List<PlaybackChapters.Chapter> chapters;
        ChapterSet(String id,String note,List<PlaybackChapters.Chapter> chapters){this.id=id;this.note=note;this.chapters=Collections.unmodifiableList(chapters);}
    }
    public static final class Result {
        public final Status status;public final List<ChapterSet> candidates;
        Result(Status status,List<ChapterSet> candidates){this.status=status;this.candidates=Collections.unmodifiableList(new ArrayList<>(candidates));}
        /** v1 supplies no cut runtime or edition identity. Never promote bounds/rank to proof. */
        public List<PlaybackChapters.Chapter> verifiedChapters(){return Collections.emptyList();}
    }
    private final Source source;
    public ChaptersDbClient(Context context){this(new HttpSource(context.getApplicationContext()));}
    ChaptersDbClient(Source source){this.source=source;}
    static boolean imdb(String value){return value!=null&&value.matches("tt[0-9]{7,10}");}
    private static boolean uuid(String value){return value!=null&&value.matches("[a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}");}
    static Result empty(Status status){return new Result(status,Collections.emptyList());}
    public Result movie(String imdbId,long duration)throws Exception{
        if(!imdb(imdbId)||duration<=0)return empty(Status.MISSING_ID);
        JSONObject show=source.get("show/"+imdbId);if(show.optString("id").isEmpty())return empty(Status.EMPTY);
        if(!"movie".equals(show.optString("type"))||!imdbId.equals(show.optString("imdbId")))return empty(Status.IDENTITY_MISMATCH);
        return sets(show.optJSONArray("chapters"),duration);
    }
    public Result episode(String seriesImdb,String episodeTvdb,int season,int number,long duration)throws Exception{
        if(!imdb(seriesImdb)||episodeTvdb==null||!episodeTvdb.matches("[1-9][0-9]{0,11}")||season<0||season>9999||number<1||number>9999||duration<=0)return empty(Status.MISSING_ID);
        JSONObject show=source.get("show/"+seriesImdb),episode=source.get("episode/"+episodeTvdb);
        if(show.optString("id").isEmpty()||episode.optString("id").isEmpty())return empty(Status.EMPTY);
        JSONObject parent=episode.optJSONObject("show");
        // Require both provider identifiers and the parent relation. Never join by title/ordinal alone.
        if(!"tv".equals(show.optString("type"))||!seriesImdb.equals(show.optString("imdbId"))||!episodeTvdb.equals(episode.optString("tvdbId"))||parent==null||!show.optString("id").equals(parent.optString("id")))return empty(Status.IDENTITY_MISMATCH);
        java.util.regex.Matcher ordinal=java.util.regex.Pattern.compile("^S([0-9]{1,4})E([0-9]{1,4})\\s*[-:]\\s*.+$").matcher(episode.optString("title"));
        if(!ordinal.matches()||Integer.parseInt(ordinal.group(1))!=season||Integer.parseInt(ordinal.group(2))!=number||!uuid(episode.optString("id")))return empty(Status.IDENTITY_MISMATCH);
        JSONObject payload=source.get("chapters/"+episode.getString("id"));
        if(!episode.getString("id").equals(payload.optString("episodeId")))return empty(Status.IDENTITY_MISMATCH);
        return sets(payload.optJSONArray("chapters"),duration);
    }
    static Result sets(JSONArray sets,long duration){
        if(sets==null||sets.length()==0||duration<=0)return empty(Status.EMPTY);
        if(sets.length()>32)return empty(Status.IDENTITY_MISMATCH);
        List<ChapterSet> valid=new ArrayList<>();Set<String> seen=new HashSet<>();
        for(int i=0;i<sets.length();i++){
            JSONObject set=sets.optJSONObject(i);if(set==null||!uuid(set.optString("id"))||!seen.add(set.optString("id")))continue;
            JSONArray entries=set.optJSONArray("entries");if(entries==null||entries.length()<1||entries.length()>256)continue;
            TreeMap<Long,String> sorted=new TreeMap<>();boolean malformed=false;
            for(int n=0;n<entries.length();n++){
                JSONObject entry=entries.optJSONObject(n);long at=entry==null?-1:timestamp(entry.optString("time"));
                if(at<0||at>=duration){malformed=true;break;}
                String name=entry.optString("name").replaceAll("[\\p{Cntrl}]"," ").trim();if(name.length()>160)name=name.substring(0,160);
                String previous=sorted.put(at,name);if(previous!=null&&!previous.equals(name)){malformed=true;break;}
            }
            if(malformed)continue;List<PlaybackChapters.Chapter> chapters=new ArrayList<>();for(Map.Entry<Long,String> chapter:sorted.entrySet())chapters.add(new PlaybackChapters.Chapter(chapter.getKey(),chapter.getValue()));
            String note=set.isNull("note")?"":set.optString("note");if(note.length()>1000)note=note.substring(0,1000);
            valid.add(new ChapterSet(set.optString("id"),note,chapters));
        }
        return valid.isEmpty()?empty(Status.EMPTY):new Result(Status.UNVERIFIED_ALIGNMENT,valid);
    }
    static long timestamp(String text){
        if(text==null||!text.matches("[0-9]{2,3}:[0-5][0-9]:[0-5][0-9](?:\\.[0-9]{1,3})?"))return -1;
        try{String[] parts=text.split("[:.]");long ms=(Long.parseLong(parts[0])*3600+Long.parseLong(parts[1])*60+Long.parseLong(parts[2]))*1000;if(parts.length==4)ms+=Long.parseLong((parts[3]+"00").substring(0,3));return ms;}catch(NumberFormatException malformed){return -1;}
    }
    /** Private bounded cache, no authentication, no title/path query, no redirects or retry storm. */
    static final class HttpSource implements Source {
        private static final int MAX_BYTES=1024*1024;
        private static final long DAY=24L*60*60*1000;
        private static long nextRequestAt,backoffUntil;
        private final File directory;private final OkHttpClient http;
        HttpSource(Context context){this(context,com.archos.mediacenter.video.streaming.StreamingRepository.readOnlyNetworkClient().newBuilder().connectTimeout(5,TimeUnit.SECONDS).readTimeout(5,TimeUnit.SECONDS).callTimeout(10,TimeUnit.SECONDS).followRedirects(false).followSslRedirects(false).build());}
        HttpSource(Context context,OkHttpClient http){directory=new File(context.getCacheDir(),"chaptersdb-v1");this.http=http;}
        private File file(String route)throws Exception{byte[] hash=MessageDigest.getInstance("SHA-256").digest(route.getBytes(StandardCharsets.UTF_8));StringBuilder name=new StringBuilder();for(byte value:hash)name.append(String.format(Locale.ROOT,"%02x",value&255));return new File(directory,name+".json");}
        private JSONObject cached(File file){if(!file.isFile()||file.length()>MAX_BYTES)return null;try(InputStream in=new AtomicFile(file).openRead()){return new JSONObject(read(in));}catch(Exception ignored){return null;}}
        static String read(InputStream in)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1){if(out.size()+n>MAX_BYTES)throw new IOException("Chapter response limit");out.write(b,0,n);}return out.toString("UTF-8");}
        @Override public JSONObject get(String route)throws Exception{
            if(!route.matches("(?:show|episode|chapters)/[A-Za-z0-9-]{1,80}"))throw new IOException("Invalid chapter route");
            File target=file(route);JSONObject stale=cached(target);long now=System.currentTimeMillis();
            if(stale!=null&&now-stale.optLong("at")>=0&&now-stale.optLong("at")<(stale.optInt("status")==404?60*60*1000:DAY))return stale.getJSONObject("data");
            try{
                synchronized(HttpSource.class){if(now<backoffUntil)throw new IOException("Chapter provider backoff");long wait=nextRequestAt-now;if(wait>0)Thread.sleep(Math.min(wait,1000));if(Thread.currentThread().isInterrupted())throw new InterruptedIOException();nextRequestAt=System.currentTimeMillis()+1000;}
                Request request=new Request.Builder().url("https://chaptersdb.com/api/v1/"+route).header("Accept","application/json").header("User-Agent","Supernova/ChaptersDB-v1").get().build();
                JSONObject data;int status;
                try(Response response=http.newCall(request).execute()){status=response.code();if(status==429){long seconds=900;try{seconds=Math.max(60,Math.min(86400,Long.parseLong(response.header("Retry-After"))));}catch(Exception ignored){}synchronized(HttpSource.class){backoffUntil=System.currentTimeMillis()+seconds*1000;}throw new IOException("Chapter provider rate limited");}if(status==404)data=new JSONObject();else{ResponseBody body=response.body();if(status!=200||body==null||body.contentType()==null||!body.contentType().toString().toLowerCase(Locale.ROOT).contains("application/json"))throw new IOException("Chapter provider response unavailable");data=new JSONObject(read(body.byteStream()));}}
                JSONObject envelope=new JSONObject().put("at",System.currentTimeMillis()).put("status",status).put("data",data);write(target,envelope);return data;
            }catch(InterruptedException cancelled){Thread.currentThread().interrupt();throw new InterruptedIOException();}catch(Exception unavailable){if(Thread.currentThread().isInterrupted())throw new InterruptedIOException();if(stale!=null&&now-stale.optLong("at")>=0&&now-stale.optLong("at")<7*DAY)return stale.getJSONObject("data");throw unavailable;}
        }
        private void write(File target,JSONObject data){try{directory.mkdirs();AtomicFile atomic=new AtomicFile(target);FileOutputStream out=null;try{out=atomic.startWrite();byte[] bytes=data.toString().getBytes(StandardCharsets.UTF_8);if(bytes.length>MAX_BYTES)throw new IOException("Chapter cache limit");out.write(bytes);atomic.finishWrite(out);}catch(Exception error){if(out!=null)atomic.failWrite(out);}File[] files=directory.listFiles((dir,name)->name.endsWith(".json"));if(files!=null){Arrays.sort(files,Comparator.comparingLong(File::lastModified));long bytes=0;for(File file:files)bytes+=file.length();for(int i=0;i<files.length&&(files.length-i>64||bytes>8L*MAX_BYTES);i++){long size=files[i].length();if(files[i].delete())bytes-=size;}}}catch(Exception ignored){/* Cache failure never interrupts playback. */}}
    }
}
