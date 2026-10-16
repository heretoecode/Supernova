package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.IOException;
import java.util.*;
import org.json.*;

/** User-hidden physical records only. The visible Library is never a sufficient export inventory. */
public final class PortableHiddenItems {
    public static final String PENDING="supernova_restore_pending_hidden_v2";
    /** Caller owns the database and its read snapshot. No writes or metadata/path columns are read here. */
    public static Map<Long,Boolean> read(SQLiteDatabase database)throws IOException {
        Map<Long,Boolean> result=new LinkedHashMap<>();
        try(Cursor rows=database.query(VideoOpenHelper.FILES_TABLE_NAME,
                new String[]{"_id",VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER},
                "media_type = ?",new String[]{"3"},null,null,"_id ASC")) {
            while(rows.moveToNext()) {
                long id=rows.getLong(0),value=rows.isNull(1)?0:rows.getLong(1);
                if(rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||id<=0||
                        !rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_INTEGER||
                        (value!=0&&value!=1)||result.size()>=100000)
                    throw new IOException("Invalid indexed hidden-item state");
                result.put(id,value==1);
            }
        }catch(RuntimeException invalid){throw new IOException("Hidden-item inventory unavailable",invalid);}
        return Collections.unmodifiableMap(result);
    }
    public static void validate(JSONObject payload)throws JSONException {
        Object schema=payload.get("schema");
        if(payload.length()!=2||!(schema instanceof Integer||schema instanceof Long)||((Number)schema).longValue()!=1)
            throw new JSONException("Unsupported hidden-item schema");
        JSONArray rows=payload.getJSONArray("rows");Set<String> seen=new HashSet<>();
        if(rows.length()>100000)throw new JSONException("Too many hidden items");
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);
            if(row.length()!=1||!(row.get("physical") instanceof String))throw new JSONException("Invalid hidden-item fields");
            String ref=row.getString("physical");PortablePhysicalReferences.validate(ref);
            if(!seen.add(ref))throw new JSONException("Duplicate hidden-item reference");
        }
    }
    public static JSONObject export(Context context,Map<Long,Boolean> indexed,PortablePhysicalReferences references)throws JSONException {
        Map<String,JSONObject> rows=new LinkedHashMap<>();
        for(var entry:indexed.entrySet()) {
            if(entry.getKey()==null||entry.getKey()<=0||entry.getValue()==null)throw new JSONException("Invalid indexed hidden-item state");
            if(entry.getValue()) {
                String ref=references.reference(entry.getKey());rows.put(ref,new JSONObject().put("physical",ref));
            }
        }
        String pending=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);
        if(pending!=null) {
            JSONObject saved=new JSONObject(pending);validate(saved);JSONArray unknown=saved.getJSONArray("rows");
            for(int i=0;i<unknown.length();i++){JSONObject row=unknown.getJSONObject(i);rows.putIfAbsent(row.getString("physical"),row);}
        }
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    public static final class Prepared {
        public final Map<Long,Boolean> hidden;
        public final String pending;
        public final int unresolved;
        private Prepared(Map<Long,Boolean> hidden,JSONArray unknown)throws JSONException {
            this.hidden=Collections.unmodifiableMap(hidden);
            pending=new JSONObject().put("schema",1).put("rows",unknown).toString();unresolved=unknown.length();
        }
    }
    /** Pure selected replacement. Physical binding requires the separately validated matching dependency. */
    public static Prepared prepare(JSONObject payload,Map<Long,Boolean> receiving,PortablePhysicalReferences references,
            boolean matchingValidated)throws JSONException {
        validate(payload);Map<Long,Boolean> values=new LinkedHashMap<>();
        for(var entry:receiving.entrySet()) {
            if(entry.getKey()==null||entry.getKey()<=0||entry.getValue()==null)throw new JSONException("Invalid receiving hidden-item state");
            values.put(entry.getKey(),false);
        }
        JSONArray unknown=new JSONArray(),rows=payload.getJSONArray("rows");
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);Video target=references.resolve(row.getString("physical"));
            if(!matchingValidated||target==null||!values.containsKey(target.getId())){unknown.put(new JSONObject(row.toString()));continue;}
            values.put(target.getId(),true);
        }
        return new Prepared(values,unknown);
    }
    private PortableHiddenItems(){}
}
