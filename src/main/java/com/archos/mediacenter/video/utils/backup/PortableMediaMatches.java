package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediaprovider.video.*;
import com.archos.mediascraper.BaseTags;
import java.io.IOException;
import java.util.*;
import org.json.*;

/** Provider identification only: no scraped fields, paths, artwork, Put.io mappings or credentials. */
public final class PortableMediaMatches {
    public static final String PENDING="supernova_restore_pending_matches_v2";
    private static long integer(Object value,long minimum,long maximum)throws JSONException {
        if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<minimum||((Number)value).longValue()>maximum)throw new JSONException("Invalid media-match integer");return ((Number)value).longValue();
    }
    public static final class Match {
        public final String identity;public final Long episodeProvider;
        public Match(String identity,Long episodeProvider)throws JSONException {
            if(identity==null||!identity.matches("tmdb:(?:movie:[1-9][0-9]{0,9}|tv:[1-9][0-9]{0,9}:[0-9]{1,10}:[1-9][0-9]{0,9})"))throw new JSONException("Unsupported media-match identity");
            String[] parts=identity.split(":");for(int i=2;i<parts.length;i++)try{if(Long.parseLong(parts[i])>Integer.MAX_VALUE)throw new NumberFormatException();}catch(NumberFormatException invalid){throw new JSONException("Media-match identity exceeds receiving API");}
            if(episodeProvider!=null&&(identity.startsWith("tmdb:movie:")||episodeProvider<=0||episodeProvider>Integer.MAX_VALUE))throw new JSONException("Invalid episode provider identity");
            this.identity=identity;this.episodeProvider=episodeProvider;
        }
        private boolean same(Match other){return other!=null&&identity.equals(other.identity)&&Objects.equals(episodeProvider,other.episodeProvider);}
    }
    private static Long number(Cursor rows,int column)throws IOException {
        if(rows.isNull(column))return null;if(rows.getType(column)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid native match integer");return rows.getLong(column);
    }
    /** Within the caller's unfiltered SQLite read snapshot, without source labels or locators. */
    public static Map<Long,Match> read(SQLiteDatabase database)throws IOException {
        if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Media-match schema is incompatible");Map<Long,Match> matches=new LinkedHashMap<>();
        String[] projection={"_id",VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE,VideoStore.Video.VideoColumns.SCRAPER_M_ONLINE_ID,VideoStore.Video.VideoColumns.SCRAPER_S_ONLINE_ID,VideoStore.Video.VideoColumns.SCRAPER_E_SEASON,VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE,VideoStore.Video.VideoColumns.SCRAPER_E_ONLINE_ID};
        try(Cursor rows=database.query(VideoOpenHelper.VIDEO_VIEW_NAME,projection,"(_data IS NULL OR _data NOT LIKE ?)",new String[]{"putio:%"},null,null,"_id ASC")) {
            while(rows.moveToNext()){
                Long id=number(rows,0),type=number(rows,1);if(id==null||id<=0)throw new IOException("Invalid indexed match owner");if(type==null||type<=0)continue;Match value;
                if(type==BaseTags.MOVIE){Long provider=number(rows,2);if(provider==null||provider<=0)throw new IOException("Identified movie has no durable provider identity");value=new Match("tmdb:movie:"+provider,null);}
                else if(type==BaseTags.TV_SHOW){Long show=number(rows,3),season=number(rows,4),episode=number(rows,5),provider=number(rows,6);if(show==null||show<=0||season==null||season<0||episode==null||episode<=0)throw new IOException("Identified episode has no durable provider identity");value=new Match("tmdb:tv:"+show+":"+season+":"+episode,provider==null||provider<=0?null:provider);}
                else throw new IOException("Unknown indexed media-match kind");
                if(matches.size()>=100000||matches.put(id,value)!=null)throw new IOException("Ambiguous or excessive indexed matches");
            }
        }catch(JSONException|RuntimeException invalid){throw new IOException("Media identification cannot be exported safely",invalid);}return Collections.unmodifiableMap(matches);
    }
    private static Match match(JSONObject row)throws JSONException {
        if(!(row.get("identity") instanceof String)||!row.has("episodeProvider"))throw new JSONException("Invalid media-match fields");return new Match(row.getString("identity"),row.isNull("episodeProvider")?null:integer(row.get("episodeProvider"),1,Integer.MAX_VALUE));
    }
    public static void validate(JSONObject payload)throws JSONException {
        if(payload.length()!=2||integer(payload.get("schema"),1,1)!=1)throw new JSONException("Unsupported media-match schema");JSONArray rows=payload.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many media matches");Set<String> seen=new HashSet<>();
        for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);if(row.length()!=3||!(row.get("physical") instanceof String))throw new JSONException("Invalid media-match fields");PortablePhysicalReferences.validate(row.getString("physical"));if(!seen.add(row.getString("physical")))throw new JSONException("Duplicate media-match owner");match(row);}
    }
    public static JSONObject export(Context context,Map<Long,Match> indexed,PortablePhysicalReferences physical)throws JSONException {
        Map<String,JSONObject> rows=new LinkedHashMap<>();for(var item:indexed.entrySet()){if(item.getKey()==null||item.getKey()<=0||item.getValue()==null)throw new JSONException("Invalid match source ownership");Video video=physical.video(item.getKey());if(video==null)throw new JSONException("Match source is outside the physical inventory");if(video.getFileUri()!=null&&"putio".equalsIgnoreCase(video.getFileUri().getScheme()))continue;String ref=physical.reference(item.getKey());Match value=item.getValue();rows.put(ref,new JSONObject().put("physical",ref).put("identity",value.identity).put("episodeProvider",value.episodeProvider==null?JSONObject.NULL:value.episodeProvider));}
        String saved=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);if(saved!=null){JSONObject pending=new JSONObject(saved);validate(pending);JSONArray values=pending.getJSONArray("rows");for(int i=0;i<values.length();i++){JSONObject row=values.getJSONObject(i);rows.putIfAbsent(row.getString("physical"),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    public static final class Target {
        public final long localId,receivingVideoId;public final Match match;
        public Target(long localId,long receivingVideoId,Match match)throws JSONException{if(localId<=0||receivingVideoId<=0||match==null)throw new JSONException("Invalid receiving match target");this.localId=localId;this.receivingVideoId=receivingVideoId;this.match=match;}
    }
    public static final class Prepared {
        public final Map<Long,Target> associations;public final String pending;public final int unresolved;
        private Prepared(Map<Long,Target> associations,JSONArray pending)throws JSONException {this.associations=Collections.unmodifiableMap(associations);this.pending=new JSONObject().put("schema",1).put("rows",pending).toString();unresolved=pending.length();}
    }
    /** Physical ambiguity and missing/duplicate native provider rows remain pending; never choose by title. */
    public static Prepared prepare(JSONObject payload,Collection<Long> receivingFiles,Collection<Target> targets,PortablePhysicalReferences physical)throws JSONException {
        validate(payload);Set<Long> files=new HashSet<>();for(Long id:receivingFiles)if(id==null||id<=0||!files.add(id))throw new JSONException("Invalid receiving physical scope");Map<String,List<Target>> providers=new HashMap<>();for(Target target:targets)if(target==null)throw new JSONException("Missing receiving match target");else providers.computeIfAbsent(target.match.identity,key->new ArrayList<>()).add(target);
        Map<Long,Target> result=new LinkedHashMap<>();JSONArray pending=new JSONArray(),rows=payload.getJSONArray("rows");for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);Match incoming=match(row);Video video=physical.resolve(row.getString("physical"));List<Target> candidates=new ArrayList<>();if(video!=null)for(Target candidate:providers.getOrDefault(incoming.identity,Collections.emptyList()))if(candidate.receivingVideoId==video.getId()&&incoming.same(candidate.match))candidates.add(candidate);Target target=candidates.size()==1?candidates.get(0):null;
            if(video==null||!files.contains(video.getId())||target==null){pending.put(new JSONObject(row.toString()));continue;}result.put(video.getId(),target);
        }return new Prepared(result,pending);
    }
    private PortableMediaMatches(){}
}
