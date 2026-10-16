package com.archos.mediacenter.video.metadata;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.json.*;

/** Exact-ID supplementary ratings only. Production access is deliberately denied pending entitlement. */
public final class OmdbSupplement {
    /** A future binding must use an authorised per-user encrypted key or approved backend, never an APK secret. */
    public interface Access {
        boolean permitted();
        /** Must atomically enforce the account's remaining daily budget; false means no request. */
        boolean acquireRequest();
        String apiKey();
    }
    public interface Transport { JSONObject get(HttpUrl url) throws Exception; }
    public static final class Ratings {
        public static final Ratings EMPTY=new Ratings("","","","","");
        public final String imdb,rottenTomatoes,metacritic,awards,imdbVotes;
        private Ratings(String imdb,String rottenTomatoes,String metacritic,String awards,String votes){this.imdb=imdb;this.rottenTomatoes=rottenTomatoes;this.metacritic=metacritic;this.awards=awards;this.imdbVotes=votes;}
        public String value(int row){return row==0?imdb:row==1?rottenTomatoes:row==2?metacritic:awards;}
    }
    private static final Access DENIED=new Access(){public boolean permitted(){return false;}public boolean acquireRequest(){return false;}public String apiKey(){return "";}};
    public static final OmdbSupplement PRODUCTION=new OmdbSupplement(DENIED,new HttpsTransport());
    private final Access access;private final Transport transport;
    public OmdbSupplement(Access access,Transport transport){this.access=access;this.transport=transport;}
    public boolean enabled(){return access.permitted();}
    /** Call on the existing metadata worker. No title search, logging of URLs/keys, or persistent cache. */
    public Ratings load(String imdbId,String kind){
        if(!access.permitted()||imdbId==null||!imdbId.matches("tt[0-9]{7,10}")||!("movie".equals(kind)||"series".equals(kind)||"episode".equals(kind)))return Ratings.EMPTY;
        String key=access.apiKey();if(key==null||!key.matches("[A-Za-z0-9]{8,64}")||!access.acquireRequest())return Ratings.EMPTY;
        HttpUrl url=HttpUrl.parse("https://www.omdbapi.com/").newBuilder().addQueryParameter("apikey",key).addQueryParameter("i",imdbId).addQueryParameter("type",kind).addQueryParameter("r","json").build();
        try{return parse(transport.get(url),imdbId,kind);}catch(Exception unavailable){return Ratings.EMPTY;}
    }
    public static Ratings parse(JSONObject data,String imdbId,String kind){
        if(data==null||!"True".equalsIgnoreCase(data.optString("Response"))||!imdbId.equals(data.optString("imdbID"))||!kind.equals(data.optString("Type")))return Ratings.EMPTY;
        String imdb=score(data.optString("imdbRating"),10,"/10"),rt="",mc=score(data.optString("Metascore"),100,"/100");
        JSONArray rows=data.optJSONArray("Ratings");if(rows!=null)for(int n=0;n<Math.min(rows.length(),12);n++){
            JSONObject row=rows.optJSONObject(n);if(row==null)continue;String source=row.optString("Source"),value=row.optString("Value");
            if("Internet Movie Database".equals(source)&&value.endsWith("/10"))imdb=score(value.substring(0,value.length()-3),10,"/10");
            else if("Rotten Tomatoes".equals(source)&&value.endsWith("%"))rt=score(value.substring(0,value.length()-1),100,"%");
            else if("Metacritic".equals(source)&&value.endsWith("/100"))mc=score(value.substring(0,value.length()-4),100,"/100");
        }
        String votes=clean(data.optString("imdbVotes"),32);if(!votes.matches("[0-9]{1,12}|[0-9]{1,3}(,[0-9]{3}){1,3}"))votes="";
        return new Ratings(imdb,rt,mc,clean(data.optString("Awards"),512),votes);
    }
    private static String score(String text,int maximum,String suffix){try{if(!text.matches("[0-9]{1,3}(\\.[0-9]{1,2})?"))return "";double value=Double.parseDouble(text);return value>=0&&value<=maximum?text+suffix:"";}catch(Exception invalid){return "";}}
    private static String clean(String value,int limit){if(value==null)return "";value=value.trim();return value.isEmpty()||value.equalsIgnoreCase("N/A")||value.equalsIgnoreCase("null")?"":value.replaceAll("[\\p{Cntrl}]"," ").substring(0,Math.min(limit,value.length()));}
    private static final class HttpsTransport implements Transport {
        private final OkHttpClient http=new OkHttpClient.Builder().connectTimeout(5,TimeUnit.SECONDS).readTimeout(8,TimeUnit.SECONDS).callTimeout(10,TimeUnit.SECONDS).followRedirects(false).followSslRedirects(false).build();
        public JSONObject get(HttpUrl url)throws Exception{
            try(Response response=http.newCall(new Request.Builder().url(url).header("Accept","application/json").build()).execute()){
                if(!response.isSuccessful()||response.body()==null||response.body().contentLength()>65536)throw new IOException("Supplement unavailable");
                try(java.io.InputStream in=response.body().byteStream();java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()){
                    byte[] buffer=new byte[4096];int length;while((length=in.read(buffer))!=-1){if(out.size()+length>65536)throw new IOException("Supplement too large");out.write(buffer,0,length);}return new JSONObject(out.toString("UTF-8"));
                }
            }
        }
    }
}
