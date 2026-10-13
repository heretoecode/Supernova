package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AtomicFile;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.CustomLibraryPage;
import com.archos.mediacenter.video.leanback.HomeIntentCodec;
import com.archos.mediacenter.video.leanback.PortableViewingChoices;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private recovery for the currently registered preference codecs. No database or external service writes. */
public final class PreferenceRestoreTransaction {
    private static final String HOME="preview_home_rows41";
    private static final int MAX_JOURNAL=8*1024*1024;
    private static final Object LOCK=new Object();
    public enum Recovery { NONE, ROLLED_BACK, COMPLETED }
    public enum Checkpoint { JOURNAL_DURABLE, PREFERENCES_DURABLE, COMPLETION_DURABLE }
    public interface Observer { void reached(Checkpoint checkpoint)throws IOException; }
    public static final class Plan {
        private final SharedPreferences owner;
        private final Map<String,Object> before,after;
        private final Set<String> keys;
        public final Set<BackupCategory> categories;
        public final int unresolved;
        private Plan(SharedPreferences owner,Map<String,Object> before,Map<String,Object> after,Set<String> keys,Set<BackupCategory> categories,int unresolved){
            this.owner=owner;
            this.before=copy(before);this.after=copy(after);this.keys=Collections.unmodifiableSet(new LinkedHashSet<>(keys));
            this.categories=Collections.unmodifiableSet(EnumSet.copyOf(categories));this.unresolved=unresolved;
        }
    }
    private final SharedPreferences prefs;
    private final Context destination;
    private final AtomicFile journal;
    public PreferenceRestoreTransaction(Context context){
        destination=context.getApplicationContext();
        prefs=PreferenceManager.getDefaultSharedPreferences(context);
        journal=new AtomicFile(new File(context.getFilesDir(),"supernova-restore/preferences-v1.json"));
    }
    private static Map<String,Object> copy(Map<String,?> source){
        Map<String,Object> result=new LinkedHashMap<>();for(Map.Entry<String,?> item:source.entrySet()){
            Object value=item.getValue();result.put(item.getKey(),value instanceof Set?Collections.unmodifiableSet(new LinkedHashSet<>((Set<?>)value)):value);
        }return Collections.unmodifiableMap(result);
    }
    /** All selected payloads must be present and valid before any mutation or journal creation. */
    public Plan prepare(Map<BackupCategory,JSONObject> selected,Snapshot snapshot)throws JSONException{
        if(selected.isEmpty())throw new JSONException("Select at least one present category");
        Map<String,?> current=prefs.getAll();Map<String,Object> after=new LinkedHashMap<>();Set<String> keys=new LinkedHashSet<>();int unresolved=0;
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
            }else throw new JSONException("Category restore codec not registered: "+category.name());
        }
        Map<String,Object> before=new LinkedHashMap<>();for(String key:keys)if(current.containsKey(key))before.put(key,current.get(key));
        return new Plan(prefs,before,after,keys,selected.keySet(),unresolved);
    }
    private static void cancelled(BooleanSupplier cancel)throws InterruptedIOException{
        if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Restore cancelled before completion");
    }
    public void apply(Plan plan,BooleanSupplier cancel)throws IOException{apply(plan,cancel,checkpoint->{});}
    /** One synchronous preference commit. An interrupted, uncompleted transaction recovers to its originals. */
    public void apply(Plan plan,BooleanSupplier cancel,Observer observer)throws IOException{
        synchronized(LOCK){
            if(plan.owner!=prefs)throw new IOException("Restore plan belongs to another configuration store");
            if(journal.getBaseFile().exists()||new File(journal.getBaseFile()+".bak").exists())throw new IOException("Recover the previous restore before applying another");
            cancelled(cancel);Map<String,?> current=prefs.getAll();
            for(String key:plan.keys)if(current.containsKey(key)!=plan.before.containsKey(key)||!Objects.equals(current.get(key),plan.before.get(key)))throw new IOException("Selected configuration changed after restore preview; review it again");
            JSONObject record;
            try{record=record(plan,false);}catch(JSONException e){throw new IOException("Cannot prepare private restore recovery",e);}
            save(record);
            try{
                observer.reached(Checkpoint.JOURNAL_DURABLE);cancelled(cancel);
                replace(plan.keys,plan.after);observer.reached(Checkpoint.PREFERENCES_DURABLE);cancelled(cancel);
                try{record.put("complete",true);}catch(JSONException e){throw new IOException(e);}
                save(record);
            }catch(IOException|RuntimeException failure){
                try{recover();}catch(IOException recovery){failure.addSuppressed(recovery);}
                throw failure;
            }
            // Durable completion wins over late cancellation. Recovery must retain the new values now.
            observer.reached(Checkpoint.COMPLETION_DURABLE);removeJournal();
        }
    }
    private void replace(Set<String> keys,Map<String,Object> values)throws IOException{
        SharedPreferences.Editor edit=prefs.edit();for(String key:keys)edit.remove(key);
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
    private static boolean owned(String key){return PortableSettingsRegistry.rule(key)!=null||key.equals(HOME)||key.equals(HomeIntentCodec.PENDING)||key.equals(CustomLibraryPage.KEY)||key.equals(PortableViewingChoices.QUEUE_PENDING)||key.equals(PortableViewingChoices.DISMISS_PENDING)||key.matches("preview_cw_dismiss:[vs][1-9][0-9]*");}
    private static JSONObject record(Plan plan,boolean complete)throws JSONException,IOException{
        JSONArray values=new JSONArray();for(String key:plan.keys){
            if(!owned(key))throw new IOException("Unregistered restore key");
            JSONObject value=new JSONObject().put("key",key).put("present",plan.before.containsKey(key));
            if(plan.before.containsKey(key)){
                Object original=plan.before.get(key);String type=original instanceof Boolean?"boolean":original instanceof Integer?"int":original instanceof Long?"long":original instanceof Float?"float":original instanceof String?"string":original instanceof Set?"set":null;
                if(type==null)throw new IOException("Unsupported original configuration value");
                value.put("type",type).put("value",original instanceof Set?new JSONArray((Set<?>)original):original);
            }values.put(value);
        }
        return new JSONObject().put("schema",1).put("complete",complete).put("values",values);
    }
    private void save(JSONObject record)throws IOException{
        byte[] data=record.toString().getBytes(StandardCharsets.UTF_8);if(data.length>MAX_JOURNAL)throw new IOException("Private recovery exceeds storage limit");
        File folder=journal.getBaseFile().getParentFile();if(!folder.isDirectory()&&!folder.mkdirs())throw new IOException("Cannot create private recovery folder");
        FileOutputStream output=journal.startWrite();try{output.write(data);journal.finishWrite(output);}catch(IOException|RuntimeException e){journal.failWrite(output);throw e;}
    }
    public Recovery recover()throws IOException{
        synchronized(LOCK){
            if(!journal.getBaseFile().exists()&&!new File(journal.getBaseFile()+".bak").exists())return Recovery.NONE;
            Set<String> keys=new LinkedHashSet<>();Map<String,Object> originals=new LinkedHashMap<>();boolean complete;
            try(InputStream input=journal.openRead()){
                ByteArrayOutputStream output=new ByteArrayOutputStream();byte[] buffer=new byte[8192];int n;
                while((n=input.read(buffer))!=-1){if(output.size()+n>MAX_JOURNAL)throw new IOException("Private recovery exceeds storage limit");output.write(buffer,0,n);}
                JSONObject record=new JSONObject(new String(output.toByteArray(),StandardCharsets.UTF_8));
                if(record.length()!=3||!(record.get("schema") instanceof Integer)||record.getInt("schema")!=1||!(record.get("complete") instanceof Boolean))throw new JSONException("Unknown recovery schema");
                complete=record.getBoolean("complete");JSONArray values=record.getJSONArray("values");if(values.length()>10000)throw new JSONException("Too many recovery keys");
                for(int i=0;i<values.length();i++){
                    JSONObject entry=values.getJSONObject(i);String key=entry.getString("key");if(!owned(key)||!keys.add(key)||!(entry.get("present") instanceof Boolean))throw new JSONException("Invalid recovery key");
                    boolean present=entry.getBoolean("present");if(entry.length()!=(present?4:2))throw new JSONException("Invalid recovery entry");if(!present)continue;
                    Object value=entry.get("value");switch(entry.getString("type")){
                        case "boolean":if(!(value instanceof Boolean))throw new JSONException("Recovery Boolean expected");break;
                        case "int":if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<Integer.MIN_VALUE||((Number)value).longValue()>Integer.MAX_VALUE)throw new JSONException("Recovery int expected");value=((Number)value).intValue();break;
                        case "long":if(!(value instanceof Integer||value instanceof Long))throw new JSONException("Recovery long expected");value=((Number)value).longValue();break;
                        case "float":if(!(value instanceof Number)||!Float.isFinite(((Number)value).floatValue()))throw new JSONException("Recovery float expected");value=((Number)value).floatValue();break;
                        case "string":if(!(value instanceof String))throw new JSONException("Recovery string expected");break;
                        case "set":if(!(value instanceof JSONArray))throw new JSONException("Recovery set expected");Set<String> set=new LinkedHashSet<>();JSONArray array=(JSONArray)value;if(array.length()>10000)throw new JSONException("Recovery set too large");for(int k=0;k<array.length();k++){if(!(array.get(k) instanceof String)||!set.add(array.getString(k)))throw new JSONException("Invalid recovery set member");}value=set;break;
                        default:throw new JSONException("Unknown recovery value type");
                    }originals.put(key,value);
                }
            }catch(JSONException e){throw new IOException("Private restore recovery is invalid; originals retained for inspection",e);}
            if(!complete)replace(keys,originals);removeJournal();return complete?Recovery.COMPLETED:Recovery.ROLLED_BACK;
        }
    }
    private void removeJournal()throws IOException{
        journal.delete();if(journal.getBaseFile().exists()||new File(journal.getBaseFile()+".bak").exists())throw new IOException("Restore recovery cleanup is pending");
    }
}
