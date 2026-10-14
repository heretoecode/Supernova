package com.archos.mediacenter.video.leanback;

import android.content.Context;
import java.io.IOException;
import java.util.*;
import org.json.*;

/** Logical history and unfinished positions have separate positive schemas despite sharing a live table. */
public final class PortableViewingState {
    public enum Kind { HISTORY, POSITIONS }
    private static long integer(Object value,long minimum,long maximum)throws JSONException{
        if(!(value instanceof Integer||value instanceof Long))throw new JSONException("Integer viewing state required");long result=((Number)value).longValue();if(result<minimum||result>maximum)throw new JSONException("Viewing state outside supported range");return result;
    }
    private static void identity(String key)throws JSONException{
        if(key.matches("file:[a-f0-9]{64}"))return;
        if(!key.matches("tmdb:(?:movie:[1-9][0-9]{0,18}|tv:[1-9][0-9]{0,18}:(?:0|[1-9][0-9]{0,9}):[1-9][0-9]{0,9})"))throw new JSONException("Unsupported viewing identity");
        String[] parts=key.split(":");try{if(Long.parseLong(parts[2])<=0)throw new NumberFormatException();if(parts.length==5&&(Integer.parseInt(parts[3])<0||Integer.parseInt(parts[4])<=0))throw new NumberFormatException();}catch(NumberFormatException e){throw new JSONException("Viewing identity outside supported range");}
    }
    private static JSONObject payload(Kind kind,JSONArray rows)throws JSONException{return new JSONObject().put("schema",1).put("kind",kind.name()).put("rows",rows);}
    public static JSONObject export(Context context,Kind kind)throws IOException,JSONException{ViewingHistory.awaitWrites();return export(ViewingHistory.read(context),kind);}
    public static JSONObject export(Map<String,ViewingHistory.Record> records,Kind kind)throws JSONException{
        JSONArray rows=new JSONArray();for(String key:new TreeSet<>(records.keySet())){
            identity(key);ViewingHistory.Record value=records.get(key);if(value==null||value.position< -2||value.duration<0||value.played<0||value.completedAt<0)throw new JSONException("Saved viewing record needs review");
            if(kind==Kind.HISTORY){if(!value.completed&&!value.started&&value.played==0&&value.completedAt==0)continue;rows.put(new JSONObject().put("identity",key).put("completed",value.completed).put("started",value.started).put("played",value.played).put("completedAt",value.completedAt));}
            else if(value.position>0)rows.put(new JSONObject().put("identity",key).put("position",value.position).put("duration",value.duration));
        }JSONObject result=payload(kind,rows);validate(result,kind);return result;
    }
    public static Map<String,ViewingHistory.Record> validate(JSONObject payload,Kind kind)throws JSONException{
        if(payload.length()!=3||integer(payload.get("schema"),1,1)!=1||!kind.name().equals(payload.get("kind")))throw new JSONException("Unsupported viewing state schema");
        JSONArray rows=payload.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many viewing records");Map<String,ViewingHistory.Record> result=new LinkedHashMap<>();
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);if(!(row.get("identity") instanceof String))throw new JSONException("Viewing identity must be text");String key=row.getString("identity");identity(key);ViewingHistory.Record record;
            if(kind==Kind.HISTORY){
                if(row.length()!=5||!(row.get("completed") instanceof Boolean)||!(row.get("started") instanceof Boolean))throw new JSONException("Invalid history fields");
                record=new ViewingHistory.Record(row.getBoolean("completed"),row.getBoolean("started"),0,0,integer(row.get("played"),0,Long.MAX_VALUE),integer(row.get("completedAt"),0,Long.MAX_VALUE));
            }else{
                if(row.length()!=3)throw new JSONException("Invalid position fields");int position=(int)integer(row.get("position"),1,Integer.MAX_VALUE),duration=(int)integer(row.get("duration"),0,Integer.MAX_VALUE);
                record=new ViewingHistory.Record(false,false,position,duration,0,0);
            }if(result.put(key,record)!=null)throw new JSONException("Duplicate viewing identity");
        }return Collections.unmodifiableMap(result);
    }
    /** Null means unselected. Present-empty clears only the selected columns. Returns staged values, no writes. */
    public static Map<String,ViewingHistory.Record> prepare(Map<String,ViewingHistory.Record> existing,JSONObject history,JSONObject positions)throws JSONException{
        if(history==null&&positions==null)throw new JSONException("No viewing category selected");
        Map<String,ViewingHistory.Record> historyRows=history==null?Collections.emptyMap():validate(history,Kind.HISTORY),positionRows=positions==null?Collections.emptyMap():validate(positions,Kind.POSITIONS);
        Set<String> keys=new TreeSet<>(existing.keySet());keys.addAll(historyRows.keySet());keys.addAll(positionRows.keySet());Map<String,ViewingHistory.Record> result=new LinkedHashMap<>();
        for(String key:keys){
            identity(key);ViewingHistory.Record old=existing.get(key);if(old==null)old=new ViewingHistory.Record(false,false,0,0,0,0);
            ViewingHistory.Record h=historyRows.get(key),p=positionRows.get(key);
            boolean completed=history==null?old.completed:h!=null&&h.completed,started=history==null?old.started:h!=null&&h.started;
            long played=history==null?old.played:h==null?0:h.played,completedAt=history==null?old.completedAt:h==null?0:h.completedAt;
            int position=positions==null?old.position:p==null?(history==null&&old.position<0?old.position:0):p.position;
            int duration=positions==null?old.duration:p==null?0:p.duration;
            // Negative legacy bookmarks are completion markers, not unfinished offsets.
            if(history!=null&&position<0)position=0;
            result.put(key,new ViewingHistory.Record(completed,started,position,duration,played,completedAt));
        }return Collections.unmodifiableMap(result);
    }
    /** App-private before/after images, never accepted as a public category payload. */
    public static JSONObject recoverySnapshot(Map<String,ViewingHistory.Record> records)throws JSONException {
        JSONArray rows=new JSONArray();for(String key:new TreeSet<>(records.keySet())) {
            ViewingHistory.Record value=records.get(key);rows.put(new JSONObject().put("identity",key)
                    .put("completed",value.completed).put("started",value.started).put("position",value.position)
                    .put("duration",value.duration).put("played",value.played).put("completedAt",value.completedAt));
        }
        JSONObject snapshot=new JSONObject().put("schema",1).put("rows",rows);validateRecoverySnapshot(snapshot);return snapshot;
    }
    public static Map<String,ViewingHistory.Record> validateRecoverySnapshot(JSONObject snapshot)throws JSONException {
        if(snapshot.length()!=2||integer(snapshot.get("schema"),1,1)!=1)throw new JSONException("Unsupported viewing recovery schema");
        JSONArray rows=snapshot.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many viewing recovery records");
        Map<String,ViewingHistory.Record> result=new LinkedHashMap<>();for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);if(row.length()!=7||!(row.get("identity") instanceof String)||
                    !(row.get("completed") instanceof Boolean)||!(row.get("started") instanceof Boolean))throw new JSONException("Invalid viewing recovery fields");
            String key=row.getString("identity");identity(key);
            ViewingHistory.Record value=new ViewingHistory.Record(row.getBoolean("completed"),row.getBoolean("started"),
                    (int)integer(row.get("position"),-2,Integer.MAX_VALUE),(int)integer(row.get("duration"),0,Integer.MAX_VALUE),
                    integer(row.get("played"),0,Long.MAX_VALUE),integer(row.get("completedAt"),0,Long.MAX_VALUE));
            if(result.put(key,value)!=null)throw new JSONException("Duplicate viewing recovery identity");
        }return Collections.unmodifiableMap(result);
    }
    /** Roll back only selected columns; later unselected intent is preserved, including newly created records. */
    public static JSONObject recoverSelected(Map<String,ViewingHistory.Record> current,JSONObject before,boolean history,boolean positions)throws JSONException {
        if(!history&&!positions)throw new JSONException("No viewing recovery category selected");
        Map<String,ViewingHistory.Record> originals=validateRecoverySnapshot(before),result=new LinkedHashMap<>();
        Set<String> keys=new TreeSet<>(current.keySet());keys.addAll(originals.keySet());
        for(String key:keys) {
            ViewingHistory.Record now=current.get(key),old=originals.get(key);if(now==null)now=new ViewingHistory.Record(false,false,0,0,0,0);
            boolean original=old!=null;if(old==null)old=new ViewingHistory.Record(false,false,0,0,0,0);
            int position=positions?old.position:history&&now.position<=0&&(old.position<0||now.position<0)?old.position:now.position;
            ViewingHistory.Record value=new ViewingHistory.Record(history?old.completed:now.completed,history?old.started:now.started,
                    position,positions?old.duration:now.duration,history?old.played:now.played,history?old.completedAt:now.completedAt);
            if(original||value.completed||value.started||value.position!=0||value.duration!=0||value.played!=0||value.completedAt!=0)result.put(key,value);
        }return recoverySnapshot(result);
    }
    /** Native receiving state copied into new rows is not a later independent write during rollback. */
    public static JSONObject recoverSelected(Map<String,ViewingHistory.Record> current,JSONObject before,JSONObject introduced,boolean history,boolean positions)throws JSONException {
        Map<String,ViewingHistory.Record> copies=validateRecoverySnapshot(introduced),originals=validateRecoverySnapshot(before),filtered=new LinkedHashMap<>(current);
        for(var entry:copies.entrySet()){if(originals.containsKey(entry.getKey()))throw new JSONException("Introduced viewing record conflicts with originals");ViewingHistory.Record now=current.get(entry.getKey()),copied=entry.getValue();if(now==null)continue;boolean same=history&&!positions?now.position==copied.position&&now.duration==copied.duration:positions&&!history?now.completed==copied.completed&&now.started==copied.started&&now.played==copied.played&&now.completedAt==copied.completedAt:false;if(same)filtered.remove(entry.getKey());}
        return recoverSelected(filtered,before,history,positions);
    }
    private PortableViewingState(){}
}
