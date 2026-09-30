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
    public static final class Account {
        public final long id,usedBytes,totalBytes;public final String username,status;
        Account(long id,String username,String status,long used,long total){this.id=id;this.username=username;this.status=status;usedBytes=used;totalBytes=total;}
    }
    public Account account()throws Unavailable {return parseAccount(request(new Request.Builder().url(ROOT+"account/info").header("Authorization","Token "+token).build(),"account_info"));}
    static Account parseAccount(JSONObject response)throws Unavailable {
        try{
            if(!"OK".equals(response.getString("status")))throw new JSONException("Invalid envelope");
            JSONObject info=response.getJSONObject("info"),disk=info.getJSONObject("disk");long id=info.getLong("user_id"),used=disk.getLong("used"),total=disk.getLong("size");String name=info.getString("username"),status=info.getString("account_status");
            if(id<=0||used<0||total<0||name.isEmpty()||name.length()>256||!Arrays.asList("active","inactive","stranger").contains(status))throw new JSONException("Invalid account");
            return new Account(id,name,status,used,total);
        }catch(JSONException invalid){throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);}
    }
    public Page list(long parentId,String cursor)throws Unavailable {
        if(parentId<0)throw new IllegalArgumentException("Only selected account folders are supported");
        HttpUrl url=HttpUrl.parse(ROOT+(cursor==null?"files/list":"files/list/continue")).newBuilder().addQueryParameter("per_page","200").build();
        Request.Builder request=new Request.Builder().header("Authorization","Token "+token);
        if(cursor==null)request.url(url.newBuilder().addQueryParameter("parent_id",String.valueOf(parentId)).addQueryParameter("total","1").build());
        else request.url(url).post(new FormBody.Builder().add("cursor",cursor).build());
        return parse(request(request.build(),cursor==null?"folder_list":"folder_continue"),parentId);
    }
    public Page search(String query,String cursor)throws Unavailable {
        if(query==null||query.trim().isEmpty()||query.length()>256)throw new IllegalArgumentException("Enter a search query");
        HttpUrl.Builder url=HttpUrl.parse(ROOT+(cursor==null?"files/search":"files/search/continue")).newBuilder().addQueryParameter("per_page","200");
        Request.Builder request=new Request.Builder().header("Authorization","Token "+token);
        if(cursor==null)request.url(url.addQueryParameter("query",query).build());
        else request.url(url.build()).post(new FormBody.Builder().add("cursor",cursor).build());
        return parseSearch(request(request.build(),cursor==null?"search":"search_continue"));
    }
    static Page parseSearch(JSONObject response)throws Unavailable {
        Page page=parse(response,-1);
        if(page.total<0)throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        return page;
    }
    public static final class FileInfo {
        public final Item item;public final String description;
        FileInfo(Item item,String description){this.item=item;this.description=description;}
    }
    public FileInfo file(long id)throws Unavailable {
        if(id<=0)throw new IllegalArgumentException("Missing file identity");
        HttpUrl url=HttpUrl.parse(ROOT+"files/"+id).newBuilder().addQueryParameter("media_info","1").build();
        return parseFile(request(new Request.Builder().url(url).header("Authorization","Token "+token).build(),"file_info"),id);
    }
    static FileInfo parseFile(JSONObject response,long expectedId)throws Unavailable {
        try {
            if(!"OK".equals(response.getString("status")))throw new JSONException("Invalid envelope");
            JSONObject value=response.getJSONObject("file");Item item=parseItem(value,-1);
            if(item.id!=expectedId)throw new JSONException("Wrong file identity");
            StringBuilder text=new StringBuilder();appendText(text,"Type",value,"content_type");
            appendText(text,"Created",value,"created_at");appendText(text,"Updated",value,"updated_at");
            JSONObject media=value.optJSONObject("media_info");
            if(media!=null){JSONObject format=media.optJSONObject("format");
                if(format!=null){appendText(text,"Container",format,"name");appendNumber(text,"Duration (seconds)",format,"duration");appendNumber(text,"Bit rate (bits/s)",format,"bit_rate");}
                JSONArray streams=media.optJSONArray("streams");
                if(streams!=null){if(streams.length()>256)throw new JSONException("Too many streams");
                    for(int i=0;i<streams.length();i++){JSONObject stream=streams.getJSONObject(i);text.append("\nStream ").append(i+1).append('\n');
                        appendText(text,"Type",stream,"codec_type");appendText(text,"Codec",stream,"codec_name");appendText(text,"Profile",stream,"profile");
                        appendNumber(text,"Width",stream,"width");appendNumber(text,"Height",stream,"height");appendNumber(text,"Channels",stream,"channels");}
                }
            }
            return new FileInfo(item,text.toString().trim());
        }catch(JSONException invalid){throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);}
    }
    private static void appendText(StringBuilder text,String label,JSONObject value,String key)throws JSONException {
        if(!value.has(key)||value.isNull(key))return;Object raw=value.get(key);
        if(!(raw instanceof String)||((String)raw).length()>256||((String)raw).matches(".*[\\p{Cntrl}].*"))throw new JSONException("Invalid display field");
        if(!((String)raw).isEmpty())text.append(label).append(": ").append(raw).append('\n');
    }
    private static void appendNumber(StringBuilder text,String label,JSONObject value,String key)throws JSONException {
        if(!value.has(key)||value.isNull(key))return;Object raw=value.get(key);
        if(!(raw instanceof Number)||!Double.isFinite(((Number)raw).doubleValue())||((Number)raw).doubleValue()<0)throw new JSONException("Invalid media value");
        text.append(label).append(": ").append(raw).append('\n');
    }
    private JSONObject request(Request request,String kind)throws Unavailable {
        String operation=com.archos.mediacenter.video.diagnostics.Diagnostics.operation("putio_"+kind);
        long started=android.os.SystemClock.elapsedRealtime();int status=0;String outcome="cancelled";
        try{
        if(Thread.currentThread().isInterrupted())throw new Unavailable(PutioReconciliation.Failure.CANCELLED,0);
        try(Response response=http.newCall(request).execute()){
            status=response.code();
            if(!response.isSuccessful())throw new Unavailable(response.code()==401||response.code()==403?PutioReconciliation.Failure.UNAUTHORISED:response.code()==429?PutioReconciliation.Failure.RATE_LIMITED:PutioReconciliation.Failure.OFFLINE,response.code());
            if(response.body()==null)throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,response.code());
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();InputStream input=response.body().byteStream();byte[] buffer=new byte[8192];int count;
            while((count=input.read(buffer))!=-1){
                if(Thread.currentThread().isInterrupted())throw new Unavailable(PutioReconciliation.Failure.CANCELLED,0);
                if(bytes.size()+count>8*1024*1024)throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
                bytes.write(buffer,0,count);
            }
            JSONObject result=new JSONObject(bytes.toString("UTF-8"));outcome="transport_complete";return result;
        }catch(Unavailable safe){outcome=safe.reason.name();throw safe;}
        catch(JSONException invalid){outcome="INVALID_PAGE";throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);}
        catch(IOException network){outcome=Thread.currentThread().isInterrupted()?"CANCELLED":"OFFLINE";throw new Unavailable(Thread.currentThread().isInterrupted()?PutioReconciliation.Failure.CANCELLED:PutioReconciliation.Failure.OFFLINE,0);}
        }finally{
            com.archos.mediacenter.video.diagnostics.Diagnostics.event("transport_complete".equals(outcome)?"provider_request_complete":"provider_request_failed",
                    "operation_id",operation,"provider","putio","operation_type",kind,"status",status,"outcome",outcome,
                    "duration_ms",android.os.SystemClock.elapsedRealtime()-started,"retry_number",0,"connectivity",com.archos.mediacenter.video.diagnostics.Diagnostics.connectivity());
            com.archos.mediacenter.video.diagnostics.Diagnostics.finishOperation(operation,"putio_"+kind,started);
        }
        // Never propagate request, response body, cursor or authorisation into diagnostics.
    }
    static Page parse(JSONObject response,long parentId)throws Unavailable {
        try{
            if(!"OK".equals(response.getString("status"))||!response.has("cursor"))throw new JSONException("Incomplete envelope");
            Object rawCursor=response.get("cursor");
            if(rawCursor!=JSONObject.NULL&&(!(rawCursor instanceof String)||((String)rawCursor).isEmpty()))throw new JSONException("Invalid continuation");
            JSONArray values=response.getJSONArray("files");List<Item> items=new ArrayList<>();Set<Long> seen=new HashSet<>();
            for(int n=0;n<values.length();n++){
                Item item=parseItem(values.getJSONObject(n),parentId);
                if(!seen.add(item.id))throw new JSONException("Duplicate file identity");
                items.add(item);
            }
            long total=response.has("total")?response.getLong("total"):-1;
            if(total< -1||total>=0&&total<items.size())throw new JSONException("Invalid count");
            return new Page(items,rawCursor==JSONObject.NULL?null:(String)rawCursor,total);
        }catch(JSONException invalid){throw new Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);}
    }
    private static Item parseItem(JSONObject value,long parentId)throws JSONException {
        long id=value.getLong("id"),parent=value.getLong("parent_id"),size=value.getLong("size");
        String name=value.getString("name"),type=value.getString("file_type");
        if(id<=0||parent<0||parentId>=0&&parent!=parentId||size<0||name.isEmpty()||name.contains("/")||name.contains("\\")||name.indexOf('\0')>=0||name.equals(".")||name.equals(".."))throw new JSONException("Invalid file identity");
        return new Item(id,parent,size,name,type);
    }
}
