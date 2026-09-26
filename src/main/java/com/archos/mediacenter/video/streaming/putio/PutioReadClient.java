package com.archos.mediacenter.video.streaming.putio;

import okhttp3.*;
import org.json.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** Narrow, read-only public API adapter. Original-quality playback continues through WebDAV. */
public final class PutioReadClient {
    private static final String ROOT="https://api.put.io/v2/";
    private final String token;
    private final OkHttpClient http;
    public static final class Unavailable extends IOException {
        public final PutioReconciliation.Failure reason;
        public final int status;
        Unavailable(PutioReconciliation.Failure reason,int status){super("put.io request unavailable ("+reason+", "+status+")");this.reason=reason;this.status=status;}
    }
    public static final class Item {
        public final long id,parentId,size;
        public final String name,type;
        Item(long id,long parentId,long size,String name,String type){this.id=id;this.parentId=parentId;this.size=size;this.name=name;this.type=type;}
        public boolean folder(){return "FOLDER".equals(type);}
        public boolean video(){return "VIDEO".equals(type)||"FILE".equals(type)&&name.toLowerCase(Locale.ROOT).matches(".*\\.(mkv|mp4|m4v|avi|mov|ts|m2ts|mpeg|mpg|webm|wmv|vob|iso)$");}
    }
    public static final class Page {
        public final List<Item> items;
        public final String cursor;
        public final long total;
        Page(List<Item> items,String cursor,long total){this.items=Collections.unmodifiableList(items);this.cursor=cursor;this.total=total;}
    }
    public PutioReadClient(String token){
        if(token==null||!token.matches("[!-~]{1,4096}"))throw new IllegalArgumentException("Missing valid put.io authorisation");
        this.token=token;
        http=new OkHttpClient.Builder().connectTimeout(10,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).callTimeout(30,TimeUnit.SECONDS)
                .followRedirects(false).followSslRedirects(false).build();
    }
    public Page list(long parentId,String cursor)throws Unavailable {
        if(parentId<0)throw new IllegalArgumentException("Only selected account folders are supported");
        HttpUrl url=HttpUrl.parse(ROOT+(cursor==null?"files/list":"files/list/continue")).newBuilder().addQueryParameter("per_page","200").build();
        Request.Builder request=new Request.Builder().header("Authorization","Token "+token);
        if(cursor==null)request.url(url.newBuilder().addQueryParameter("parent_id",String.valueOf(parentId)).addQueryParameter("total","1").build());
        else request.url(url).post(new FormBody.Builder().add("cursor",cursor).build());
        if(Thread.currentThread().isInterrupted())throw new Unavailable(PutioReconciliation.Failure.CANCELLED,0);
        try(Response response=http.newCall(request.build()).execute()){
            if(!response.isSuccessful())throw new Unavailable(response.code()==401||response.code()==403?PutioReconciliation.Failure.UNAUTHORISED:response.code()==429?PutioReconciliation.Failure.RATE_LIMITED:PutioReconciliation.Failure.OFFLINE,response.code());
            if(response.body()==null)throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,response.code());
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();InputStream input=response.body().byteStream();byte[] buffer=new byte[8192];int count;
            while((count=input.read(buffer))!=-1){
                if(Thread.currentThread().isInterrupted())throw new Unavailable(PutioReconciliation.Failure.CANCELLED,0);
                if(bytes.size()+count>8*1024*1024)throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
                bytes.write(buffer,0,count);
            }
            return parse(new JSONObject(bytes.toString("UTF-8")),parentId);
        }catch(Unavailable safe){throw safe;}
        catch(JSONException invalid){throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);}
        catch(IOException network){throw new Unavailable(Thread.currentThread().isInterrupted()?PutioReconciliation.Failure.CANCELLED:PutioReconciliation.Failure.OFFLINE,0);}
        // Never propagate request, response body, cursor or authorisation into diagnostics.
    }
    static Page parse(JSONObject response,long parentId)throws Unavailable {
        try{
            if(!"OK".equals(response.getString("status"))||!response.has("cursor"))throw new JSONException("Incomplete envelope");
            Object rawCursor=response.get("cursor");
            if(rawCursor!=JSONObject.NULL&&(!(rawCursor instanceof String)||((String)rawCursor).isEmpty()))throw new JSONException("Invalid continuation");
            JSONArray values=response.getJSONArray("files");List<Item> items=new ArrayList<>();Set<Long> seen=new HashSet<>();
            for(int n=0;n<values.length();n++){
                JSONObject value=values.getJSONObject(n);long id=value.getLong("id"),parent=value.getLong("parent_id"),size=value.getLong("size");
                String name=value.getString("name"),type=value.getString("file_type");
                if(id<=0||parent!=parentId||size<0||!seen.add(id)||name.isEmpty()||name.contains("/")||name.contains("\\")||name.indexOf('\0')>=0||name.equals(".")||name.equals(".."))throw new JSONException("Invalid file identity");
                items.add(new Item(id,parent,size,name,type));
            }
            long total=response.has("total")?response.getLong("total"):-1;
            if(total< -1||total>=0&&total<items.size())throw new JSONException("Invalid count");
            return new Page(items,rawCursor==JSONObject.NULL?null:(String)rawCursor,total);
        }catch(JSONException invalid){throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);}
    }
}
