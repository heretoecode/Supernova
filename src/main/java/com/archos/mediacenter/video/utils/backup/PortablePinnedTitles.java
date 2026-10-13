package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import com.archos.mediaprovider.video.ScraperStore;
import com.archos.mediaprovider.video.VideoStore;
import java.io.IOException;
import androidx.preference.PreferenceManager;
import java.util.*;
import org.json.*;

/** Scraper movie/show pin state and ordering only. No file rows, title text or legacy user Collections. */
public final class PortablePinnedTitles {
    public static final String PENDING="supernova_restore_pending_pins_v2";
    public enum Kind { MOVIE, TV }
    public static final class Title {
        public final Kind kind;public final long local,online,pinned;
        public Title(Kind kind,long local,long online,long pinned){if(kind==null||local<=0||online<0||pinned<0)throw new IllegalArgumentException("Invalid indexed title pin state");this.kind=kind;this.local=local;this.online=online;this.pinned=pinned;}
        private String logical(){return online>0?"tmdb:"+(kind==Kind.MOVIE?"movie:":"tv:")+online:null;}
        public String target(){return kind.name()+":"+local;}
    }
    private static boolean integer(Object value){return value instanceof Integer||value instanceof Long;}
    /** Narrow source projection: no metadata text, artwork, filenames or account relationships are read. */
    public static List<Title> read(Context context)throws IOException{
        List<Title> titles=new ArrayList<>();try{
            for(Kind kind:Kind.values()){
                String online=kind==Kind.MOVIE?ScraperStore.Movie.ONLINE_ID:ScraperStore.Show.ONLINE_ID;
                try(Cursor rows=context.getContentResolver().query(kind==Kind.MOVIE?ScraperStore.Movie.URI.ALL:ScraperStore.Show.URI.ALL,new String[]{"_id",online,VideoStore.Video.VideoColumns.NOVA_PINNED},null,null,"_id ASC")){
                    if(rows==null)throw new IOException("Indexed pin query unavailable");while(rows.moveToNext()){
                        if(titles.size()>=100000||rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||
                                !rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_INTEGER||
                                !rows.isNull(2)&&rows.getType(2)!=Cursor.FIELD_TYPE_INTEGER)
                            throw new IOException("Invalid indexed pin inventory");
                        titles.add(new Title(kind,rows.getLong(0),rows.isNull(1)?0:rows.getLong(1),rows.isNull(2)?0:rows.getLong(2)));
                    }
                }
            }
        }catch(RuntimeException invalid){throw new IOException("Indexed pin state could not be validated",invalid);}return Collections.unmodifiableList(titles);
    }
    public static void validate(JSONObject payload)throws JSONException{
        if(payload.length()!=2||!integer(payload.get("schema"))||payload.getLong("schema")!=1)throw new JSONException("Unsupported pin schema");JSONArray rows=payload.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many pinned titles");Set<String> owners=new HashSet<>();
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);if(row.length()!=4||!(row.get("source") instanceof String)||!row.getString("source").matches("[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}:(?:MOVIE|TV):[1-9][0-9]{0,18}")||!row.has("logical")||!integer(row.get("pinnedAt"))||row.getLong("pinnedAt")<=0)throw new JSONException("Invalid pin fields");
            String kind=row.getString("kind");if(!Arrays.asList("MOVIE","TV").contains(kind)||!row.getString("source").contains(":"+kind+":"))throw new JSONException("Invalid pin kind");
            String[] source=row.getString("source").split(":");try{Long.parseLong(source[2]);}catch(NumberFormatException invalid){throw new JSONException("Pin source identity exceeds supported range");}
            if(!row.isNull("logical")){if(!(row.get("logical") instanceof String)||!row.getString("logical").matches("tmdb:"+(kind.equals("MOVIE")?"movie:":"tv:")+"[1-9][0-9]{0,18}"))throw new JSONException("Invalid pin logical identity");try{Long.parseLong(row.getString("logical").split(":")[2]);}catch(NumberFormatException invalid){throw new JSONException("Pin logical identity exceeds supported range");}}
            if(!owners.add(row.getString("source")))throw new JSONException("Duplicate pin source");
        }
    }
    /** The caller supplies all scraper movie/show rows, including hidden and orphaned pinned records. */
    public static JSONObject export(Context context,Collection<Title> titles)throws JSONException{
        String origin=new PortableTitleReferences(context,null).origin();Map<String,JSONObject> rows=new LinkedHashMap<>();Set<String> seen=new HashSet<>();
        for(Title title:titles){if(!seen.add(title.target()))throw new JSONException("Duplicate indexed title row");if(title.pinned==0)continue;
            String source=origin+":"+title.target();rows.put(source,new JSONObject().put("source",source).put("kind",title.kind.name()).put("logical",title.logical()==null?JSONObject.NULL:title.logical()).put("pinnedAt",title.pinned));
        }
        String saved=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);if(saved!=null){JSONObject pending=new JSONObject(saved);validate(pending);JSONArray unknown=pending.getJSONArray("rows");for(int i=0;i<unknown.length();i++){JSONObject row=unknown.getJSONObject(i);rows.putIfAbsent(row.getString("source"),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    public static final class Prepared {
        public final Map<String,Long> pins;public final String pending;public final int unresolved;
        private Prepared(Map<String,Long> pins,JSONArray pending)throws JSONException{this.pins=Collections.unmodifiableMap(pins);this.pending=new JSONObject().put("schema",1).put("rows",pending).toString();unresolved=pending.length();}
    }
    /** No writes. Duplicate source or destination provider identities remain pending instead of merging pin times. */
    public static Prepared prepare(JSONObject payload,Collection<Title> receiving)throws JSONException{
        validate(payload);Map<String,List<Title>> available=new HashMap<>();Map<String,Long> pins=new LinkedHashMap<>();
        for(Title title:receiving){if(pins.put(title.target(),0L)!=null)throw new JSONException("Duplicate receiving title row");if(title.logical()!=null)available.computeIfAbsent(title.logical(),key->new ArrayList<>()).add(title);}
        JSONArray rows=payload.getJSONArray("rows"),pending=new JSONArray();Map<String,Integer> count=new HashMap<>();for(int i=0;i<rows.length();i++)if(!rows.getJSONObject(i).isNull("logical"))count.merge(rows.getJSONObject(i).getString("logical"),1,Integer::sum);
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);String logical=row.isNull("logical")?null:row.getString("logical");List<Title> targets=logical==null?null:available.get(logical);
            if(logical==null||count.get(logical)!=1||targets==null||targets.size()!=1){pending.put(new JSONObject(row.toString()));continue;}
            pins.put(targets.get(0).target(),row.getLong("pinnedAt"));
        }return new Prepared(pins,pending);
    }
    private PortablePinnedTitles(){}
}
