package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AtomicFile;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.CustomLibraryPage;
import com.archos.mediacenter.video.leanback.HomeIntentCodec;
import com.archos.mediacenter.video.leanback.PortableViewingChoices;
import com.archos.mediacenter.video.leanback.PortableReviewNotes;
import com.archos.mediacenter.video.leanback.PortablePreferredVersions;
import com.archos.mediacenter.video.leanback.PortableViewingState;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private combined recovery for preferences and identity-based viewing state. No external service writes. */
public final class PreferenceRestoreTransaction {
    private static final String HOME="preview_home_rows41";
    private static final int MAX_JOURNAL=32*1024*1024;
    private static final Object LOCK=new Object();
    public enum Recovery { NONE, ROLLED_BACK, COMPLETED }
    public enum Checkpoint { JOURNAL_DURABLE, DEFAULT_DURABLE, HEALTH_DURABLE, VIEWING_DURABLE, MEDIA_PREPARED, PREFERENCES_DURABLE, COMMIT_MARKER_DURABLE, MEDIA_DURABLE, COMPLETION_DURABLE }
    public interface Observer { void reached(Checkpoint checkpoint)throws IOException; }
    public static final class Failure extends IOException {
        public final Recovery outcome;
        public final boolean recoveryRequired;
        private Failure(Throwable cause,Recovery outcome,boolean recoveryRequired) {
            super(recoveryRequired?"Restoration requires recovery; do not assume originals were restored":
                    outcome==Recovery.COMPLETED?"Restored state is committed; finalization was interrupted":
                    outcome==Recovery.ROLLED_BACK?"Restoration was interrupted; original selected state was recovered":
                    "Restoration was interrupted before changes",cause);
            this.outcome=outcome;this.recoveryRequired=recoveryRequired;
        }
    }
    public static final class Cancelled extends InterruptedIOException {
        public final Recovery outcome;
        public final boolean recoveryRequired;
        private Cancelled(Throwable cause,Recovery outcome,boolean recoveryRequired) {
            super(recoveryRequired?"Cancelled restoration requires recovery":outcome==Recovery.COMPLETED?
                    "Cancellation arrived after restoration committed":"Restoration cancelled; original selected state was recovered");
            initCause(cause);this.outcome=outcome;this.recoveryRequired=recoveryRequired;
        }
    }
    public static final class Plan {
        private final SharedPreferences owner;
        private final Map<String,Object> before,after;
        private final Set<String> keys;
        private final Map<String,Object> healthBefore,healthAfter;
        private final Set<String> healthKeys;
        public final Set<BackupCategory> categories;
        public final int unresolved;
        public final Set<String> receivingChecks;
        private final String viewingBefore,viewingAfter;
        private final String mediaBefore,mediaAfter;
        private final boolean viewingExisted,historySelected,positionsSelected;
        private Plan(SharedPreferences owner,Map<String,Object> before,Map<String,Object> after,Set<String> keys,Map<String,Object> healthBefore,Map<String,Object> healthAfter,Set<String> healthKeys,Set<BackupCategory> categories,int unresolved,String viewingBefore,String viewingAfter,boolean viewingExisted,String mediaBefore,String mediaAfter){
            this.owner=owner;
            this.before=copy(before);this.after=copy(after);this.keys=Collections.unmodifiableSet(new LinkedHashSet<>(keys));
            this.healthBefore=copy(healthBefore);this.healthAfter=copy(healthAfter);this.healthKeys=Collections.unmodifiableSet(new LinkedHashSet<>(healthKeys));
            this.categories=Collections.unmodifiableSet(EnumSet.copyOf(categories));this.unresolved=unresolved;
            this.receivingChecks=after.containsKey("user_defined_density_confirmed")?Collections.singleton("user_defined_density"):Collections.emptySet();
            this.viewingBefore=viewingBefore;this.viewingAfter=viewingAfter;this.viewingExisted=viewingExisted;
            historySelected=categories.contains(BackupCategory.HISTORY);positionsSelected=categories.contains(BackupCategory.POSITIONS);
            this.mediaBefore=mediaBefore;this.mediaAfter=mediaAfter;
        }
    }
    private final SharedPreferences prefs;
    private final SharedPreferences health;
    private final Context destination;
    private final AtomicFile journal;
    public PreferenceRestoreTransaction(Context context){
        Context application=context.getApplicationContext();destination=application==null?context:application;
        prefs=PreferenceManager.getDefaultSharedPreferences(context);
        health=context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);
        journal=new AtomicFile(new File(context.getFilesDir(),"supernova-restore/preferences-v1.json"));
    }
    /** Application attachment runs this before providers, services, playback writers and settings initialize. */
    public static void recoverAtStartup(Context context)throws IOException {
        File file=new File(context.getFilesDir(),"supernova-restore/preferences-v1.json");
        if(file.exists()||new File(file+".bak").exists())new PreferenceRestoreTransaction(context).recover();
    }
    private static Map<String,Object> copy(Map<String,?> source){
        Map<String,Object> result=new LinkedHashMap<>();for(Map.Entry<String,?> item:source.entrySet()){
            Object value=item.getValue();result.put(item.getKey(),value instanceof Set?Collections.unmodifiableSet(new LinkedHashSet<>((Set<?>)value)):value);
        }return Collections.unmodifiableMap(result);
    }
    /** All selected payloads must be present and valid before any mutation or journal creation. */
    public Plan prepare(Map<BackupCategory,JSONObject> selected,Snapshot snapshot)throws JSONException,IOException{
        return prepare(selected,snapshot,null);
    }
    public Plan prepare(Map<BackupCategory,JSONObject> selected,Snapshot snapshot,PortablePhysicalReferences physical)throws JSONException,IOException{
        if(selected.isEmpty())throw new JSONException("Select at least one present category");
        Map<String,?> current=prefs.getAll();Map<String,Object> after=new LinkedHashMap<>();Set<String> keys=new LinkedHashSet<>();int unresolved=0;
        Map<String,Object> healthAfter=new LinkedHashMap<>();Set<String> healthKeys=new LinkedHashSet<>();
        JSONObject mediaBefore=null,mediaAfter=null;
        if(selected.containsKey(BackupCategory.PINNED)||selected.containsKey(BackupCategory.HIDDEN)) {
            mediaBefore=MediaStateRecovery.capture(destination,selected.containsKey(BackupCategory.PINNED),selected.containsKey(BackupCategory.HIDDEN));mediaAfter=new JSONObject(mediaBefore.toString());
        }
        Map<BackupCategory,JSONObject> ordered=new EnumMap<>(BackupCategory.class);
        for(Map.Entry<BackupCategory,JSONObject> item:selected.entrySet()){if(item.getKey()==null||item.getValue()==null)throw new JSONException("Missing selected category");ordered.put(item.getKey(),item.getValue());}
        for(Map.Entry<BackupCategory,JSONObject> item:ordered.entrySet()){
            BackupCategory category=item.getKey();if(category==null||item.getValue()==null)throw new JSONException("Missing selected category");
            PortableSettingsRegistry.Category settings=category.settings();
            if(settings!=null){
                Map<String,Object> values=PortableSettingsRegistry.validate(item.getValue(),settings);
                for(String key:current.keySet()){PortableSettingsRegistry.Rule rule=PortableSettingsRegistry.rule(key);if(rule!=null&&rule.category==settings)keys.add(key);}
                for(Map.Entry<String,Object> value:values.entrySet()){
                    Object normalized=value.getValue();PortableSettingsRegistry.Type type=PortableSettingsRegistry.rule(value.getKey()).type;
                    if(type==PortableSettingsRegistry.Type.INT)normalized=((Number)normalized).intValue();
                    if(type==PortableSettingsRegistry.Type.FLOAT)normalized=((Number)normalized).floatValue();
                    keys.add(value.getKey());after.put(value.getKey(),normalized);
                }
            }else if(category==BackupCategory.HOME){
                if(snapshot==null)throw new JSONException("Home restoration needs a destination identity snapshot");
                HomeIntentCodec.Prepared home=HomeIntentCodec.prepare(destination,item.getValue(),snapshot);
                keys.add(HOME);keys.add(HomeIntentCodec.PENDING);after.put(HOME,home.rows);after.put(HomeIntentCodec.PENDING,home.pending);unresolved+=home.unresolved;
            }else if(category==BackupCategory.CUSTOM_PAGES){
                PortableCustomPage.validate(item.getValue());keys.add(CustomLibraryPage.KEY);
                if(!item.getValue().isNull("page")){
                    JSONObject page=new JSONObject(item.getValue().getJSONObject("page").toString());page.put("collection",page.getString("franchise"));page.remove("franchise");after.put(CustomLibraryPage.KEY,page.toString());
                }
            }else if(category==BackupCategory.WATCH_NEXT||category==BackupCategory.CONTINUE_CHOICES){
                PortableViewingChoices.Kind kind=PortableViewingChoices.Kind.valueOf(category.name());
                PortableViewingChoices.Prepared choices=PortableViewingChoices.prepare(destination,item.getValue(),snapshot,kind,(String)after.get(HOME));
                keys.addAll(choices.keys);after.putAll(choices.values);unresolved+=choices.unresolved;
            }else if(category==BackupCategory.PREFERRED){
                if(physical==null)throw new JSONException("Preferred versions need an unfiltered physical identity snapshot");
                PortablePreferredVersions.Prepared versions=PortablePreferredVersions.prepare(destination,item.getValue(),physical);
                keys.addAll(versions.keys);after.putAll(versions.values);unresolved+=versions.unresolved;
            }else if(category==BackupCategory.REVIEW){
                if(physical==null)throw new JSONException("Review notes need an unfiltered physical identity snapshot");
                PortableReviewNotes.Prepared notes=PortableReviewNotes.prepare(destination,item.getValue(),physical);
                healthKeys.addAll(notes.keys);healthAfter.putAll(notes.values);unresolved+=notes.unresolved;
            }else if(category==BackupCategory.HISTORY||category==BackupCategory.POSITIONS){
                PortableViewingState.validate(item.getValue(),PortableViewingState.Kind.valueOf(category.name()));
            }else if(category==BackupCategory.PINNED){
                var pins=PortablePinnedTitles.prepare(item.getValue(),MediaStateRecovery.pinInventory(mediaBefore));mediaAfter=MediaStateRecovery.pinAfter(mediaAfter,pins);
                keys.add(PortablePinnedTitles.PENDING);after.put(PortablePinnedTitles.PENDING,pins.pending);unresolved+=pins.unresolved;
            }else if(category==BackupCategory.HIDDEN){
                var references=physical==null?new PortablePhysicalReferences(destination,Collections.emptyList()):physical;
                var hidden=PortableHiddenItems.prepare(item.getValue(),MediaStateRecovery.hiddenInventory(mediaBefore),references,false);mediaAfter=MediaStateRecovery.hiddenAfter(mediaAfter,hidden);
                keys.add(PortableHiddenItems.PENDING);after.put(PortableHiddenItems.PENDING,hidden.pending);unresolved+=hidden.unresolved;
            }else throw new JSONException("Category restore codec not registered: "+category.name());
        }
        if(after.containsKey("user_defined_density")&&!Objects.equals(current.get("user_defined_density"),after.get("user_defined_density"))){keys.add("user_defined_density_confirmed");after.put("user_defined_density_confirmed",false);}
        Map<String,Object> before=new LinkedHashMap<>();for(String key:keys)if(current.containsKey(key))before.put(key,current.get(key));
        Map<String,?> healthCurrent=health.getAll();Map<String,Object> healthBefore=new LinkedHashMap<>();for(String key:healthKeys)if(healthCurrent.containsKey(key))healthBefore.put(key,healthCurrent.get(key));
        String viewingBefore=null,viewingAfter=null;boolean viewingExisted=false;
        if(selected.containsKey(BackupCategory.HISTORY)||selected.containsKey(BackupCategory.POSITIONS)) {
            ViewingHistory.awaitWrites();synchronized(ViewingHistory.LOCK) {
                viewingExisted=destination.getDatabasePath(ViewingHistory.DATABASE).exists();JSONObject original=ViewingHistory.captureRecovery(destination);
                viewingBefore=original.toString();viewingAfter=PortableViewingState.recoverySnapshot(PortableViewingState.prepare(
                        PortableViewingState.validateRecoverySnapshot(original),selected.get(BackupCategory.HISTORY),selected.get(BackupCategory.POSITIONS))).toString();
            }
        }
        return new Plan(prefs,before,after,keys,healthBefore,healthAfter,healthKeys,selected.keySet(),unresolved,viewingBefore,viewingAfter,viewingExisted,
                mediaBefore==null?null:mediaBefore.toString(),mediaAfter==null?null:mediaAfter.toString());
    }
    private static void cancelled(BooleanSupplier cancel)throws InterruptedIOException{
        if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Restore cancelled before completion");
    }
    public void apply(Plan plan,BooleanSupplier cancel)throws IOException{apply(plan,cancel,checkpoint->{});}
    /** One synchronous preference commit. An interrupted, uncompleted transaction recovers to its originals. */
    public void apply(Plan plan,BooleanSupplier cancel,Observer observer)throws IOException{
        if(!Thread.holdsLock(ViewingHistory.LOCK))ViewingHistory.awaitWrites();
        synchronized(ViewingHistory.LOCK){applyLocked(plan,cancel,observer);}
    }
    private void applyLocked(Plan plan,BooleanSupplier cancel,Observer observer)throws IOException{
        synchronized(LOCK){
            if(plan.owner!=prefs)throw new IOException("Restore plan belongs to another configuration store");
            if(journal.getBaseFile().exists()||new File(journal.getBaseFile()+".bak").exists())throw new IOException("Recover the previous restore before applying another");
            cancelled(cancel);Map<String,?> current=prefs.getAll();
            for(String key:plan.keys)if(current.containsKey(key)!=plan.before.containsKey(key)||!Objects.equals(current.get(key),plan.before.get(key)))throw new IOException("Selected configuration changed after restore preview; review it again");
            Map<String,?> healthCurrent=health.getAll();for(String key:plan.healthKeys)if(healthCurrent.containsKey(key)!=plan.healthBefore.containsKey(key)||!Objects.equals(healthCurrent.get(key),plan.healthBefore.get(key)))throw new IOException("Selected review notes changed after restore preview; review it again");
            if(plan.viewingBefore!=null)try {
                if(destination.getDatabasePath(ViewingHistory.DATABASE).exists()!=plan.viewingExisted||
                        !ViewingHistory.captureRecovery(destination).toString().equals(plan.viewingBefore))throw new IOException("Viewing state changed after restore preview; review it again");
            }catch(JSONException invalid){throw new IOException("Viewing restore preview is invalid",invalid);}
            JSONObject record;
            try{record=record(plan,false);}catch(JSONException e){throw new IOException("Cannot prepare private restore recovery",e);}
            MediaStateRecovery.Session media=null;
            try{
                if(plan.mediaBefore!=null)try{media=MediaStateRecovery.begin(destination,new JSONObject(plan.mediaBefore),false);}catch(JSONException invalid){throw new IOException("Media restore preview is invalid",invalid);}
                save(record);
                observer.reached(Checkpoint.JOURNAL_DURABLE);cancelled(cancel);
                if(!plan.keys.isEmpty()){replace(prefs,plan.keys,plan.after);observer.reached(Checkpoint.DEFAULT_DURABLE);cancelled(cancel);}
                if(!plan.healthKeys.isEmpty()){replace(health,plan.healthKeys,plan.healthAfter);observer.reached(Checkpoint.HEALTH_DURABLE);cancelled(cancel);}
                if(plan.viewingAfter!=null){
                    try{ViewingHistory.replaceRecovery(destination,new JSONObject(plan.viewingAfter),cancel);}catch(JSONException invalid){throw new IOException("Viewing restore values are invalid",invalid);}
                    observer.reached(Checkpoint.VIEWING_DURABLE);cancelled(cancel);
                }
                if(media!=null){try{media.write(new JSONObject(plan.mediaAfter),cancel);}catch(JSONException invalid){throw new IOException("Media restore values are invalid",invalid);}
                    observer.reached(Checkpoint.MEDIA_PREPARED);cancelled(cancel);}
                observer.reached(Checkpoint.PREFERENCES_DURABLE);cancelled(cancel);
                try{record.put("complete",true);}catch(JSONException e){throw new IOException(e);}
                save(record);
                // The durable decision includes media redo data. Late cancellation cannot undo it.
                observer.reached(Checkpoint.COMMIT_MARKER_DURABLE);
                if(media!=null){
                    media.commit();media.close();media=null;
                    try{record.getJSONObject("media").put("committed",true);}catch(JSONException invalid){throw new IOException("Cannot acknowledge durable media state",invalid);}
                    save(record);observer.reached(Checkpoint.MEDIA_DURABLE);
                }
            }catch(IOException|RuntimeException failure){
                boolean interrupted=Thread.interrupted();Recovery outcome=Recovery.NONE;boolean required=false;
                try {
                    if(media!=null){try{media.close();}catch(RuntimeException close){failure.addSuppressed(close);}media=null;}
                    try{outcome=recover();}catch(IOException recovery){failure.addSuppressed(recovery);required=true;}
                }finally{if(interrupted)Thread.currentThread().interrupt();}
                if(failure instanceof InterruptedIOException||interrupted)throw new Cancelled(failure,outcome,required);
                throw new Failure(failure,outcome,required);
            }finally{if(media!=null)media.close();}
            // Durable completion wins over late cancellation. Recovery must retain the new values now.
            observer.reached(Checkpoint.COMPLETION_DURABLE);removeJournal();
        }
    }
    private void replace(SharedPreferences destination,Set<String> keys,Map<String,Object> values)throws IOException{
        SharedPreferences.Editor edit=destination.edit();for(String key:keys)edit.remove(key);
        for(Map.Entry<String,Object> item:values.entrySet()){
            String key=item.getKey();Object value=item.getValue();
            if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);
            else if(value instanceof Integer)edit.putInt(key,(Integer)value);
            else if(value instanceof Long)edit.putLong(key,(Long)value);
            else if(value instanceof Float)edit.putFloat(key,(Float)value);
            else if(value instanceof String)edit.putString(key,(String)value);
            else if(value instanceof Set)edit.putStringSet(key,(Set<String>)value);
            else throw new IOException("Unsupported private recovery value");
        }
        if(!edit.commit())throw new IOException("Configuration write failed; private recovery is required");
    }
    private static boolean owned(String key){return PortableSettingsRegistry.rule(key)!=null||key.equals("user_defined_density_confirmed")||key.equals(HOME)||key.equals(HomeIntentCodec.PENDING)||key.equals(CustomLibraryPage.KEY)||key.equals(PortableViewingChoices.QUEUE_PENDING)||key.equals(PortableViewingChoices.DISMISS_PENDING)||key.equals(PortablePreferredVersions.PENDING)||key.equals(PortablePinnedTitles.PENDING)||key.equals(PortableHiddenItems.PENDING)||PortablePreferredVersions.ownsKey(key)||key.matches("preview_cw_dismiss:[vs][1-9][0-9]*");}
    private static boolean healthOwned(String key){return key.equals(PortableReviewNotes.PENDING)||key.matches("incorrect:[1-9][0-9]{0,18}");}
    private static JSONObject record(Plan plan,boolean complete)throws JSONException,IOException{
        JSONArray values=new JSONArray();recordValues(values,"DEFAULT",plan.keys,plan.before);recordValues(values,"HEALTH",plan.healthKeys,plan.healthBefore);
        JSONObject record=new JSONObject().put("schema",plan.mediaBefore!=null?5:plan.viewingBefore==null?2:3).put("complete",complete).put("values",values);
        if(plan.viewingBefore!=null)record.put("viewing",new JSONObject().put("before",new JSONObject(plan.viewingBefore))
                .put("existed",plan.viewingExisted).put("history",plan.historySelected).put("positions",plan.positionsSelected));
        if(plan.mediaBefore!=null){
            if(plan.viewingBefore==null)record.put("viewing",JSONObject.NULL);
            JSONObject before=new JSONObject(plan.mediaBefore),after=new JSONObject(plan.mediaAfter);MediaStateRecovery.validatePair(before,after);
            record.put("media",new JSONObject().put("before",before).put("after",after).put("committed",false));
        }
        return record;
    }
    private static void recordValues(JSONArray values,String store,Set<String> keys,Map<String,Object> originals)throws JSONException,IOException{
        for(String key:keys){if(!(store.equals("DEFAULT")?owned(key):healthOwned(key)))throw new IOException("Unregistered restore key");
            JSONObject value=new JSONObject().put("store",store).put("key",key).put("present",originals.containsKey(key));
            if(originals.containsKey(key)){Object original=originals.get(key);String type=original instanceof Boolean?"boolean":original instanceof Integer?"int":original instanceof Long?"long":original instanceof Float?"float":original instanceof String?"string":original instanceof Set?"set":null;
                if(type==null||original instanceof Float&&!Float.isFinite((Float)original)||original instanceof Set&&(((Set<?>)original).size()>10000||((Set<?>)original).stream().anyMatch(item->!(item instanceof String))))throw new IOException("Unsupported original configuration value");value.put("type",type).put("value",original instanceof Set?new JSONArray((Set<?>)original):original);
            }values.put(value);
        }
    }
    private void save(JSONObject record)throws IOException{
        byte[] data=record.toString().getBytes(StandardCharsets.UTF_8);if(data.length>MAX_JOURNAL)throw new IOException("Private recovery exceeds storage limit");
        File folder=journal.getBaseFile().getParentFile();if(!folder.isDirectory()&&!folder.mkdirs())throw new IOException("Cannot create private recovery folder");
        FileOutputStream output=journal.startWrite();try{output.write(data);output.getFD().sync();journal.finishWrite(output);}catch(IOException|RuntimeException e){journal.failWrite(output);throw e;}
    }
    public Recovery recover()throws IOException{
        if(!Thread.holdsLock(ViewingHistory.LOCK))ViewingHistory.awaitWrites();
        synchronized(ViewingHistory.LOCK){return recoverLocked();}
    }
    private Recovery recoverLocked()throws IOException{
        synchronized(LOCK){
            if(!journal.getBaseFile().exists()&&!new File(journal.getBaseFile()+".bak").exists())return Recovery.NONE;
            Set<String> keys=new LinkedHashSet<>(),healthKeys=new LinkedHashSet<>();Map<String,Object> originals=new LinkedHashMap<>(),healthOriginals=new LinkedHashMap<>();boolean complete;JSONObject viewing=null,media=null;
            try(InputStream input=journal.openRead()){
                ByteArrayOutputStream output=new ByteArrayOutputStream();byte[] buffer=new byte[8192];int n;
                while((n=input.read(buffer))!=-1){if(output.size()+n>MAX_JOURNAL)throw new IOException("Private recovery exceeds storage limit");output.write(buffer,0,n);}
                JSONObject record=new JSONObject(new String(output.toByteArray(),StandardCharsets.UTF_8));
                if(!(record.get("schema") instanceof Integer)||!Arrays.asList(1,2,3,4,5).contains(record.getInt("schema"))||!(record.get("complete") instanceof Boolean))throw new JSONException("Unknown recovery schema");int schema=record.getInt("schema");
                if(record.length()!=(schema>=4?5:schema==3?4:3))throw new JSONException("Unknown recovery fields");
                if(schema>=4) {
                    media=record.getJSONObject("media");if(media.length()!=(schema==5?3:2))throw new JSONException("Invalid media recovery fields");
                    if(schema==5&&(!(media.get("committed") instanceof Boolean)||media.getBoolean("committed")&&!record.getBoolean("complete")))throw new JSONException("Invalid durable media acknowledgement");
                    MediaStateRecovery.validatePair(media.getJSONObject("before"),media.getJSONObject("after"));
                }
                if(schema>=3&&!(schema>=4&&record.isNull("viewing"))) {
                    viewing=record.getJSONObject("viewing");if(viewing.length()!=4||!(viewing.get("existed") instanceof Boolean)||
                            !(viewing.get("history") instanceof Boolean)||!(viewing.get("positions") instanceof Boolean)||
                            !viewing.getBoolean("history")&&!viewing.getBoolean("positions"))throw new JSONException("Invalid viewing recovery scope");
                    PortableViewingState.validateRecoverySnapshot(viewing.getJSONObject("before"));
                    if(!viewing.getBoolean("existed")&&viewing.getJSONObject("before").getJSONArray("rows").length()!=0)throw new JSONException("Invalid absent viewing store image");
                }
                complete=record.getBoolean("complete");JSONArray values=record.getJSONArray("values");if(values.length()>100000)throw new JSONException("Too many recovery keys");
                for(int i=0;i<values.length();i++){
                    JSONObject entry=values.getJSONObject(i);String key=entry.getString("key"),store=schema==1?"DEFAULT":entry.getString("store");if(!Arrays.asList("DEFAULT","HEALTH").contains(store))throw new JSONException("Unknown recovery store");boolean named=store.equals("HEALTH");Set<String> selectedKeys=named?healthKeys:keys;
                    if(!(named?healthOwned(key):owned(key))||!selectedKeys.add(key)||!(entry.get("present") instanceof Boolean))throw new JSONException("Invalid recovery key");
                    boolean present=entry.getBoolean("present");if(entry.length()!=(present?4:2)+(schema>=2?1:0))throw new JSONException("Invalid recovery entry");if(!present)continue;
                    Object value=entry.get("value");switch(entry.getString("type")){
                        case "boolean":if(!(value instanceof Boolean))throw new JSONException("Recovery Boolean expected");break;
                        case "int":if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<Integer.MIN_VALUE||((Number)value).longValue()>Integer.MAX_VALUE)throw new JSONException("Recovery int expected");value=((Number)value).intValue();break;
                        case "long":if(!(value instanceof Integer||value instanceof Long))throw new JSONException("Recovery long expected");value=((Number)value).longValue();break;
                        case "float":if(!(value instanceof Number)||!Float.isFinite(((Number)value).floatValue()))throw new JSONException("Recovery float expected");value=((Number)value).floatValue();break;
                        case "string":if(!(value instanceof String))throw new JSONException("Recovery string expected");break;
                        case "set":if(!(value instanceof JSONArray))throw new JSONException("Recovery set expected");Set<String> set=new LinkedHashSet<>();JSONArray array=(JSONArray)value;if(array.length()>10000)throw new JSONException("Recovery set too large");for(int k=0;k<array.length();k++){if(!(array.get(k) instanceof String)||!set.add(array.getString(k)))throw new JSONException("Invalid recovery set member");}value=set;break;
                        default:throw new JSONException("Unknown recovery value type");
                    }(named?healthOriginals:originals).put(key,value);
                }
            }catch(JSONException e){throw new IOException("Private restore recovery is invalid; originals retained for inspection",e);}
            if(media!=null&&!Boolean.TRUE.equals(media.opt("committed")))try{MediaStateRecovery.recover(destination,media.getJSONObject("before"),media.getJSONObject("after"),complete);}
                catch(JSONException invalid){throw new IOException("Media recovery values are invalid",invalid);}
            if(!complete){
                if(viewing!=null)try {
                    JSONObject restored=PortableViewingState.recoverSelected(PortableViewingState.validateRecoverySnapshot(ViewingHistory.captureRecovery(destination)),
                            viewing.getJSONObject("before"),viewing.getBoolean("history"),viewing.getBoolean("positions"));
                    if(!viewing.getBoolean("existed")&&restored.getJSONArray("rows").length()==0) {
                        if(destination.getDatabasePath(ViewingHistory.DATABASE).exists()&&!destination.deleteDatabase(ViewingHistory.DATABASE))throw new IOException("New viewing store rollback cleanup is pending");
                    }else ViewingHistory.replaceRecovery(destination,restored,()->false);
                }catch(JSONException invalid){throw new IOException("Viewing recovery values are invalid",invalid);}
                if(!keys.isEmpty())replace(prefs,keys,originals);if(!healthKeys.isEmpty())replace(health,healthKeys,healthOriginals);
            }removeJournal();return complete?Recovery.COMPLETED:Recovery.ROLLED_BACK;
        }
    }
    private void removeJournal()throws IOException{
        journal.delete();if(journal.getBaseFile().exists()||new File(journal.getBaseFile()+".bak").exists())throw new IOException("Restore recovery cleanup is pending");
    }
}
