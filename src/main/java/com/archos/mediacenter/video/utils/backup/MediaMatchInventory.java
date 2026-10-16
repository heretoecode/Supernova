package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.json.*;

/** Read-only receiving identification/ownership signature. Never creates stores or rewrites scraper pointers. */
public final class MediaMatchInventory {
    public static final class Owner {
        public final long videoId,type,localId;public final String guard;public final PortableMediaMatches.Match match;public final Long episodeProviderRaw;
        private Owner(long videoId,long type,long localId,String guard,PortableMediaMatches.Match match,Long episodeProviderRaw){this.episodeProviderRaw=episodeProviderRaw;this.videoId=videoId;this.type=type;this.localId=localId;this.guard=guard;this.match=match;}
    }
    public final Map<Long,Owner> owners;
    public final String signature;
    private MediaMatchInventory(Map<Long,Owner> owners)throws Exception {
        this.owners=Collections.unmodifiableMap(new LinkedHashMap<>(owners));JSONArray values=new JSONArray();for(var owner:owners.values())values.put(new JSONArray().put(owner.videoId).put(owner.type).put(owner.localId).put(owner.guard).put(owner.match==null?JSONObject.NULL:owner.match.identity).put(owner.match==null||owner.match.episodeProvider==null?JSONObject.NULL:owner.match.episodeProvider).put(owner.episodeProviderRaw==null?JSONObject.NULL:owner.episodeProviderRaw));signature=sha(values.toString());
    }
    private static String sha(String value)throws Exception{StringBuilder result=new StringBuilder();for(byte item:MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)))result.append(String.format(Locale.ROOT,"%02x",item&255));return result.toString();}
    public static String guard(long id,String uri)throws IOException{
        try{return sha(new JSONArray().put(id).put(uri==null?JSONObject.NULL:uri).toString());}catch(Exception invalid){throw new IOException("Receiving identification guard unavailable",invalid);}
    }
    private static long integer(Cursor rows,int column,long absent)throws IOException {
        if(rows.isNull(column))return absent;if(rows.getType(column)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid receiving identification integer");return rows.getLong(column);
    }
    public static MediaMatchInventory capture(Context context)throws IOException {
        File file=VideoOpenHelper.getDatabaseFile(context);Map<Long,Owner> owners=new LinkedHashMap<>();try {
            if(!file.exists())return new MediaMatchInventory(owners);
            try(SQLiteDatabase database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)){
                if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Receiving identification schema is incompatible");database.execSQL("SAVEPOINT supernova_match_inventory");try{
                    Map<Long,PortableMediaMatches.Match> matches=PortableMediaMatches.read(database);
                    try(Cursor rows=database.query(VideoOpenHelper.FILES_TABLE_NAME,new String[]{"_id","_data",VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE,VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_ID},"media_type=?",new String[]{"3"},null,null,"_id ASC")){
                        while(rows.moveToNext()){long id=integer(rows,0,0),type=integer(rows,2,0),local=integer(rows,3,0);if(id<=0||type< -1||local< -1||owners.size()>=100000||!rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_STRING)throw new IOException("Invalid receiving identification owner");String uri=rows.isNull(1)?null:rows.getString(1);if(uri!=null&&"putio".equalsIgnoreCase(android.net.Uri.parse(uri).getScheme()))continue;PortableMediaMatches.Match match=matches.get(id);Long episodeProviderRaw=null;if(match!=null){if(local<=0)throw new IOException("Receiving identification has no owned metadata");String table=type==com.archos.mediascraper.BaseTags.MOVIE?ScraperTables.MOVIE_TABLE_NAME:ScraperTables.EPISODE_TABLE_NAME;try(Cursor metadata=database.query(table,type==com.archos.mediascraper.BaseTags.TV_SHOW?new String[]{"video_id","e_online_id"}:new String[]{"video_id"},"_id=?",new String[]{String.valueOf(local)},null,null,null)){if(!metadata.moveToFirst()||metadata.getType(0)!=Cursor.FIELD_TYPE_INTEGER||metadata.getLong(0)!=id)throw new IOException("Receiving identification points outside its metadata owner");if(type==com.archos.mediascraper.BaseTags.TV_SHOW&&!metadata.isNull(1))episodeProviderRaw=integer(metadata,1,0);if(metadata.moveToNext())throw new IOException("Duplicate receiving metadata owner");}}if(owners.put(id,new Owner(id,type,local,guard(id,uri),match,episodeProviderRaw))!=null)throw new IOException("Duplicate receiving identification owner");}
                    }
                    return new MediaMatchInventory(owners);
                }finally{database.execSQL("RELEASE SAVEPOINT supernova_match_inventory");}
            }
        }catch(IOException failure){throw failure;}catch(Exception invalid){throw new IOException("Receiving identification inventory unavailable",invalid);}
    }
    public Collection<PortableMediaMatches.Target> targets()throws JSONException {
        List<PortableMediaMatches.Target> targets=new ArrayList<>();for(var owner:owners.values())if(owner.match!=null)targets.add(new PortableMediaMatches.Target(owner.localId,owner.videoId,owner.match));return Collections.unmodifiableList(targets);
    }
    public void validatePhysical(PortablePhysicalReferences physical)throws IOException {
        for(var owner:owners.values()){var video=physical.video(owner.videoId);String uri=video==null||video.getFileUri()==null?null:video.getFileUri().toString();if(video==null||!owner.guard.equals(guard(owner.videoId,uri)))throw new IOException("Receiving media changed before identification review");}
    }
}
