package com.archos.mediacenter.video.utils.backup;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Atomic selected-column SQLite primitive. Combined preference/database recovery is a separate layer. */
public final class HiddenItemReplacement {
    private static final String HIDDEN=VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER;
    private static final class State {
        final boolean hidden;
        final String guard,physical;
        State(boolean hidden,String guard,String physical){this.hidden=hidden;this.guard=guard;this.physical=physical;}
        @Override public boolean equals(Object other){return other instanceof State&&hidden==((State)other).hidden&&guard.equals(((State)other).guard);}
        @Override public int hashCode(){return Objects.hash(hidden,guard);}
    }
    /** These private precondition digests never become archive payloads or authentication state. */
    private static Map<Long,State> snapshot(SQLiteDatabase database)throws IOException {
        Map<Long,State> result=new LinkedHashMap<>();
        try(Cursor rows=database.query(VideoOpenHelper.FILES_TABLE_NAME,new String[]{"_id",HIDDEN,"_data",
                VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE,VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_ID},
                "media_type = ?",new String[]{"3"},null,null,"_id ASC")) {
            while(rows.moveToNext()) {
                long id=rows.getLong(0),hidden=rows.isNull(1)?0:rows.getLong(1);
                if(result.size()>=100000||rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||id<=0||
                        !rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_INTEGER||(hidden!=0&&hidden!=1)||
                        !rows.isNull(2)&&rows.getType(2)!=Cursor.FIELD_TYPE_STRING)
                    throw new IOException("Invalid hidden-item restore inventory");
                String path=rows.isNull(2)?null:rows.getString(2);JSONArray identity=new JSONArray().put(path==null?JSONObject.NULL:path);
                for(int column=3;column<5;column++) {
                    if(!rows.isNull(column)&&rows.getType(column)!=Cursor.FIELD_TYPE_INTEGER)
                        throw new IOException("Invalid indexed media relationship");
                    identity.put(rows.isNull(column)?JSONObject.NULL:rows.getLong(column));
                }
                byte[] digest=MessageDigest.getInstance("SHA-256").digest(identity.toString().getBytes(StandardCharsets.UTF_8));
                StringBuilder guard=new StringBuilder();for(byte value:digest)guard.append(String.format(Locale.ROOT,"%02x",value&255));
                result.put(id,new State(hidden==1,guard.toString(),PortablePhysicalReferences.fingerprint(path)));
            }
        }catch(RuntimeException|java.security.NoSuchAlgorithmException failure){throw new IOException("Cannot validate hidden-item restore inventory",failure);}
        return Collections.unmodifiableMap(result);
    }
    public static final class Plan {
        public final Map<Long,Boolean> hidden;
        public final String pending;
        public final int unresolved;
        private final SQLiteDatabase owner;
        private final Map<Long,State> before;
        private Plan(SQLiteDatabase owner,Map<Long,State> before,PortableHiddenItems.Prepared prepared) {
            this.owner=owner;this.before=before;hidden=prepared.hidden;pending=prepared.pending;unresolved=prepared.unresolved;
        }
    }
    /** Prepare without writes. Reject stale/incomplete matching inputs before accepting a bound physical row. */
    public static Plan prepare(SQLiteDatabase database,JSONObject payload,PortablePhysicalReferences references,
            boolean matchingValidated)throws IOException,JSONException {
        if(database.inTransaction())throw new IOException("Hidden-item preparation requires its own transaction");
        PortableHiddenItems.validate(payload);database.beginTransaction();try {
            Map<Long,State> before=snapshot(database);Map<Long,Boolean> receiving=new LinkedHashMap<>();Map<String,Integer> count=new HashMap<>();
            for(var entry:before.entrySet()) {
                receiving.put(entry.getKey(),entry.getValue().hidden);
                if(entry.getValue().physical!=null)count.merge(entry.getValue().physical,1,Integer::sum);
            }
            var prepared=PortableHiddenItems.prepare(payload,receiving,references,matchingValidated);
            for(var entry:prepared.hidden.entrySet())if(entry.getValue()) {
                State state=before.get(entry.getKey());String reference=references.reference(entry.getKey());
                if(state.physical==null||!state.physical.equals(reference)||count.get(state.physical)!=1)
                    throw new IOException("Physical matching changed before hidden-item restore preview");
            }
            return new Plan(database,before,prepared);
        }finally{database.endTransaction();}
    }
    public interface Observer { void afterWrite(int completed)throws IOException; }
    private static void cancelled(BooleanSupplier cancel)throws InterruptedIOException {
        if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Hidden-item replacement cancelled");
    }
    /** Pending preferences are not applied here. Notify consumers only after combined recovery is durable. */
    public static void replace(SQLiteDatabase database,Plan plan,BooleanSupplier cancel,Observer observer)throws IOException {
        if(plan.owner!=database)throw new IOException("Hidden-item restore plan belongs to another database");
        if(database.inTransaction())throw new IOException("Hidden-item replacement requires its own transaction");
        cancelled(cancel);database.beginTransaction();try {
            if(!snapshot(database).equals(plan.before))throw new IOException("Hidden-item inventory changed after restore preview");
            int completed=0;
            for(var entry:plan.hidden.entrySet()) {
                cancelled(cancel);if(entry.getValue()==plan.before.get(entry.getKey()).hidden)continue;
                ContentValues values=new ContentValues();values.put(HIDDEN,entry.getValue()?1:0);
                if(database.update(VideoOpenHelper.FILES_TABLE_NAME,values,"_id = ? AND media_type = ?",new String[]{Long.toString(entry.getKey()),"3"})!=1)
                    throw new IOException("Hidden-item target unavailable");
                observer.afterWrite(++completed);
            }
            cancelled(cancel);database.setTransactionSuccessful();
        }catch(RuntimeException failure){throw new IOException("Hidden-item replacement failed",failure);}
        finally{database.endTransaction();}
    }
    private HiddenItemReplacement(){}
}
