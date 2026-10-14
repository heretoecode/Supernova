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
    public enum Checkpoint { JOURNAL_DURABLE, DEFAULT_DURABLE, HEALTH_DURABLE, VIEWING_DURABLE, MEDIA_PREPARED, BOOKMARKS_PREPARED, SOURCES_PREPARED, PREFERENCES_DURABLE, COMMIT_MARKER_DURABLE, MEDIA_DURABLE, BOOKMARKS_DURABLE, SOURCES_DURABLE, COMPLETION_DURABLE }
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
        private final String bookmarksBefore,bookmarksAfter;
        private final String sourcesBefore,sourcesAfter;
        private final boolean viewingExisted,historySelected,positionsSelected;
        private Plan(SharedPreferences owner,Map<String,Object> before,Map<String,Object> after,Set<String> keys,Map<String,Object> healthBefore,Map<String,Object> healthAfter,Set<String> healthKeys,Set<BackupCategory> categories,int unresolved,String viewingBefore,String viewingAfter,boolean viewingExisted,String mediaBefore,String mediaAfter,String bookmarksBefore,String bookmarksAfter,String sourcesBefore,String sourcesAfter){
            this.owner=owner;
            this.before=copy(before);this.after=copy(after);this.keys=Collections.unmodifiableSet(new LinkedHashSet<>(keys));
            this.healthBefore=copy(healthBefore);this.healthAfter=copy(healthAfter);this.healthKeys=Collections.unmodifiableSet(new LinkedHashSet<>(healthKeys));
            this.categories=Collections.unmodifiableSet(EnumSet.copyOf(categories));this.unresolved=unresolved;
            this.receivingChecks=after.containsKey("user_defined_density_confirmed")?Collections.singleton("user_defined_density"):Collections.emptySet();
            this.viewingBefore=viewingBefore;this.viewingAfter=viewingAfter;this.viewingExisted=viewingExisted;
            historySelected=categories.contains(BackupCategory.HISTORY);positionsSelected=categories.contains(BackupCategory.POSITIONS);
            this.mediaBefore=mediaBefore;this.mediaAfter=mediaAfter;this.bookmarksBefore=bookmarksBefore;this.bookmarksAfter=bookmarksAfter;
            this.sourcesBefore=sourcesBefore;this.sourcesAfter=sourcesAfter;
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
    /** Worker-side reconciliation after normal provider initialization; never creates a media store. */
    public void reconcilePlaylists()throws IOException {
        if(!prefs.contains(PortableSavedPlaylists.PENDING))return;
        ViewingHistory.awaitWrites();synchronized(ViewingHistory.LOCK){synchronized(LOCK){
            Map<String,?> originalPrefs=prefs.getAll();Object saved=originalPrefs.get(PortableSavedPlaylists.PENDING);if(saved==null)return;if(!(saved instanceof String))throw new IOException("Pending playlist configuration is invalid");String pending=(String)saved;
            File file=com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(destination);if(!file.exists())return;
            try {
                JSONObject payload=new JSONObject(pending);PortableSavedPlaylists.validate(payload);if(payload.getJSONArray("rows").length()==0)return;
                JSONObject before;Map<Long,PortableSavedPlaylists.Definition> local=new LinkedHashMap<>();
                try(var database=android.database.sqlite.SQLiteDatabase.openDatabase(file.getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY)){
                    database.execSQL("SAVEPOINT supernova_playlist_pending_read");try{
                        before=PlaylistStateRecovery.capture(database);var inventory=PortableSavedPlaylists.read(database);JSONArray definitions=before.getJSONArray("definitions");
                        for(int i=0;i<definitions.length();i++){JSONObject row=definitions.getJSONObject(i);long id=row.getLong("id");if(row.isNull("trakt")&&inventory.containsKey(id))local.put(id,inventory.get(id));}
                    }finally{database.execSQL("RELEASE SAVEPOINT supernova_playlist_pending_read");}
                }
                // Live local definitions win over pending identities; receiving Trakt rows are preserved separately.
                Plan plan=prepare(Collections.singletonMap(BackupCategory.PLAYLISTS,PortableSavedPlaylists.export(destination,local)),null);
                Map<String,?> current=prefs.getAll();Set<String> checked=new HashSet<>(originalPrefs.keySet());checked.addAll(current.keySet());for(String key:checked)if(PortableSavedPlaylists.owns(key)&&!Objects.equals(originalPrefs.get(key),current.get(key)))throw new IOException("Playlist references changed during reconciliation; retry after refresh");
                if(!pending.equals(plan.before.get(PortableSavedPlaylists.PENDING))||!before.toString().equals(new JSONObject(plan.mediaBefore).getJSONObject("playlists").toString()))throw new IOException("Playlist state changed during reconciliation; retry after refresh");
                apply(plan,()->false);
            }catch(JSONException|RuntimeException invalid){throw new IOException("Pending saved playlists could not be reconciled safely",invalid);}
        }}
    }
    /** Receiving source setup can resolve saved references; archive locators are never enabled here. */
    public void reconcileSources()throws IOException {
        if(!prefs.contains(PortableLibrarySources.PENDING)&&!prefs.contains(PortableIndexedSources.PENDING))return;
        ViewingHistory.awaitWrites();synchronized(ViewingHistory.LOCK){synchronized(LOCK){
            Map<String,?> original=prefs.getAll();boolean pending=false;
            try {
                for(String key:List.of(PortableLibrarySources.PENDING,PortableIndexedSources.PENDING)){Object value=original.get(key);if(value==null)continue;if(!(value instanceof String))throw new JSONException("Invalid pending source configuration");JSONObject payload=new JSONObject((String)value);if(key.equals(PortableLibrarySources.PENDING)){PortableLibrarySources.validate(payload);pending|=payload.getJSONArray("roots").length()!=0||payload.getJSONArray("exclusions").length()!=0;}else{PortableIndexedSources.validate(payload);pending|=payload.getJSONArray("rows").length()!=0;}}
                if(!pending)return;JSONObject before=IndexedSourceStoreRecovery.capture(destination),payload=PortableLibrarySources.export(destination,before);
                Plan plan=prepare(Collections.singletonMap(BackupCategory.SOURCES,payload),null);
                Map<String,?> current=prefs.getAll();Set<String> checked=new HashSet<>(original.keySet());checked.addAll(current.keySet());for(String key:checked)if(PortableLibrarySources.owns(key)&&!Objects.equals(original.get(key),current.get(key)))throw new IOException("Receiving sources changed during reconciliation; retry after refresh");
                if(!before.toString().equals(plan.sourcesBefore))throw new IOException("Indexed sources changed during reconciliation; retry after refresh");
                // No resolved values means no transaction or write on each Library refresh.
                if(before.toString().equals(plan.sourcesAfter)){boolean changed=false;for(String key:plan.keys)changed|=!Objects.equals(plan.before.get(key),plan.after.get(key));if(!changed)return;}
                apply(plan,()->false);
            }catch(JSONException|RuntimeException invalid){throw new IOException("Pending sources could not be reconciled safely",invalid);}
        }}
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
        JSONObject mediaBefore=null,mediaAfter=null,bookmarksBefore=null,bookmarksAfter=null,sourcesBefore=null,sourcesAfter=null;
        if(selected.containsKey(BackupCategory.PINNED)||selected.containsKey(BackupCategory.HIDDEN)||selected.containsKey(BackupCategory.PLAYLISTS)) {
            mediaBefore=MediaStateRecovery.capture(destination,selected.containsKey(BackupCategory.PINNED),selected.containsKey(BackupCategory.HIDDEN),selected.containsKey(BackupCategory.PLAYLISTS));mediaAfter=new JSONObject(mediaBefore.toString());
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
            }else if(category==BackupCategory.PLAYLISTS){
                var playlists=PlaylistStateRecovery.prepare(mediaBefore.getJSONObject("playlists"),item.getValue(),mediaBefore.getBoolean("exists"));mediaAfter=MediaStateRecovery.playlistAfter(mediaAfter,playlists);
                JSONArray originals=mediaBefore.getJSONObject("playlists").getJSONArray("definitions");
                String origin=PortableTitleReferences.existingOrigin(current);Set<String> preserved=new HashSet<>();
                for(int i=0;i<originals.length();i++){JSONObject row=originals.getJSONObject(i);String key=PortableSavedPlaylists.referenceKey(row.getLong("id"));if(row.isNull("trakt"))keys.add(key);else{Object value=current.get(key);if(value==null&&origin==null)continue;String identity=value==null?"playlist:"+origin+":"+row.getLong("id"):value instanceof String?(String)value:null;PortableSavedPlaylists.identity(identity);preserved.add(identity);}}
                String replacementOrigin=origin==null?UUID.randomUUID().toString():origin;
                for(var reference:playlists.references.entrySet()){String key=PortableSavedPlaylists.referenceKey(reference.getKey());String identity=reference.getValue();if(preserved.contains(identity))identity="playlist:"+replacementOrigin+":"+reference.getKey();keys.add(key);after.put(key,identity);}
                keys.add(PortableSavedPlaylists.PENDING);after.put(PortableSavedPlaylists.PENDING,playlists.pending);unresolved+=playlists.unresolved;
            }else if(category==BackupCategory.SOURCES){
                PortableLibrarySources.validate(item.getValue());if(item.getValue().getInt("schema")==2)sourcesBefore=IndexedSourceStoreRecovery.capture(destination);
                var sources=PortableLibrarySources.prepare(current,item.getValue(),selected.get(BackupCategory.LOCATIONS),sourcesBefore==null?Collections.emptySet():PortableIndexedSources.configuredLocations(sourcesBefore));
                keys.addAll(sources.keys);after.putAll(sources.values);unresolved+=sources.unresolved;
                if(sourcesBefore!=null){var indexed=PortableIndexedSources.prepare(sourcesBefore,item.getValue().getJSONObject("indexed"),selected.get(BackupCategory.LOCATIONS),PortableLibrarySources.configuredRoots(current));sourcesAfter=indexed.after;keys.add(PortableIndexedSources.PENDING);after.put(PortableIndexedSources.PENDING,indexed.pending);unresolved+=indexed.unresolved;}
            }else if(category==BackupCategory.BOOKMARKS){
                bookmarksBefore=BookmarkStoreRecovery.capture(destination);
                var bookmarks=PortableBookmarks.prepare(bookmarksBefore,item.getValue(),selected.get(BackupCategory.LOCATIONS));bookmarksAfter=bookmarks.after;
                keys.add(PortableBookmarks.PENDING);after.put(PortableBookmarks.PENDING,bookmarks.pending);unresolved+=bookmarks.unresolved;
            }else if(category==BackupCategory.LOCATIONS){
                // This privacy modifier supplies locators to selected owners; it never grants storage access.
                PortableLocations.validate(item.getValue());
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
                mediaBefore==null?null:mediaBefore.toString(),mediaAfter==null?null:mediaAfter.toString(),bookmarksBefore==null?null:bookmarksBefore.toString(),bookmarksAfter==null?null:bookmarksAfter.toString(),sourcesBefore==null?null:sourcesBefore.toString(),sourcesAfter==null?null:sourcesAfter.toString());
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
            if(plan.bookmarksBefore!=null)try{if(!BookmarkStoreRecovery.capture(destination).toString().equals(plan.bookmarksBefore))throw new IOException("Bookmarks changed after restore preview; review again");}catch(JSONException invalid){throw new IOException("Bookmark restore preview is invalid",invalid);}
            if(plan.sourcesBefore!=null)try{if(!IndexedSourceStoreRecovery.capture(destination).toString().equals(plan.sourcesBefore))throw new IOException("Indexed sources changed after preview; review again");}catch(JSONException invalid){throw new IOException("Indexed-source preview is invalid",invalid);}
            JSONObject record;
            try{record=record(plan,false);}catch(JSONException e){throw new IOException("Cannot prepare private restore recovery",e);}
            MediaStateRecovery.Session media=null;BookmarkStoreRecovery.Session bookmarks=null;
            IndexedSourceStoreRecovery.Session sources=null;
            try{
                if(plan.mediaBefore!=null)try{media=MediaStateRecovery.begin(destination,new JSONObject(plan.mediaBefore),false);}catch(JSONException invalid){throw new IOException("Media restore preview is invalid",invalid);}
                if(plan.bookmarksBefore!=null)try{if(new JSONObject(plan.bookmarksBefore).getBoolean("exists"))bookmarks=BookmarkStoreRecovery.begin(destination,new JSONObject(plan.bookmarksBefore),new JSONObject(plan.bookmarksAfter));}catch(JSONException invalid){throw new IOException("Bookmark restore preview is invalid",invalid);}
                if(plan.sourcesBefore!=null)try{if(new JSONObject(plan.sourcesBefore).getBoolean("exists"))sources=IndexedSourceStoreRecovery.begin(destination,new JSONObject(plan.sourcesBefore),new JSONObject(plan.sourcesAfter));}catch(JSONException invalid){throw new IOException("Indexed-source preview is invalid",invalid);}
                save(record);
                observer.reached(Checkpoint.JOURNAL_DURABLE);cancelled(cancel);
                if(plan.bookmarksBefore!=null&&bookmarks==null)try{bookmarks=BookmarkStoreRecovery.begin(destination,new JSONObject(plan.bookmarksBefore),new JSONObject(plan.bookmarksAfter));}catch(JSONException invalid){throw new IOException("Bookmark restore preview is invalid",invalid);}
                if(plan.sourcesBefore!=null&&sources==null)try{sources=IndexedSourceStoreRecovery.begin(destination,new JSONObject(plan.sourcesBefore),new JSONObject(plan.sourcesAfter));}catch(JSONException invalid){throw new IOException("Indexed-source preview is invalid",invalid);}
                if(!plan.keys.isEmpty()){replace(prefs,plan.keys,plan.after);observer.reached(Checkpoint.DEFAULT_DURABLE);cancelled(cancel);}
                if(!plan.healthKeys.isEmpty()){replace(health,plan.healthKeys,plan.healthAfter);observer.reached(Checkpoint.HEALTH_DURABLE);cancelled(cancel);}
                if(plan.viewingAfter!=null){
                    try{ViewingHistory.replaceRecovery(destination,new JSONObject(plan.viewingAfter),cancel);}catch(JSONException invalid){throw new IOException("Viewing restore values are invalid",invalid);}
                    observer.reached(Checkpoint.VIEWING_DURABLE);cancelled(cancel);
                }
                if(media!=null){try{media.write(new JSONObject(plan.mediaAfter),cancel);}catch(JSONException invalid){throw new IOException("Media restore values are invalid",invalid);}
                    observer.reached(Checkpoint.MEDIA_PREPARED);cancelled(cancel);}
                if(bookmarks!=null){try{bookmarks.write(new JSONObject(plan.bookmarksAfter),cancel);}catch(JSONException invalid){throw new IOException("Bookmark restore values are invalid",invalid);}
                    observer.reached(Checkpoint.BOOKMARKS_PREPARED);cancelled(cancel);}
                if(sources!=null){try{sources.write(new JSONObject(plan.sourcesAfter),cancel);}catch(JSONException invalid){throw new IOException("Indexed-source values are invalid",invalid);}
                    observer.reached(Checkpoint.SOURCES_PREPARED);cancelled(cancel);}
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
                if(bookmarks!=null){
                    bookmarks.commit();bookmarks.close();bookmarks=null;
                    try{record.getJSONObject("bookmarks").put("committed",true);}catch(JSONException invalid){throw new IOException("Cannot acknowledge durable bookmarks",invalid);}
                    save(record);observer.reached(Checkpoint.BOOKMARKS_DURABLE);
                }
                if(sources!=null){sources.commit();sources.close();sources=null;try{record.getJSONObject("sources").put("committed",true);}catch(JSONException invalid){throw new IOException("Cannot acknowledge durable indexed sources",invalid);}save(record);observer.reached(Checkpoint.SOURCES_DURABLE);}
            }catch(IOException|RuntimeException failure){
                boolean interrupted=Thread.interrupted();Recovery outcome=Recovery.NONE;boolean required=false;
                try {
                    if(media!=null){try{media.close();}catch(RuntimeException close){failure.addSuppressed(close);}media=null;}
                    if(bookmarks!=null){try{bookmarks.close();}catch(RuntimeException close){failure.addSuppressed(close);}bookmarks=null;}
                    if(sources!=null){try{sources.close();}catch(RuntimeException close){failure.addSuppressed(close);}sources=null;}
                    try{outcome=recover();}catch(IOException recovery){failure.addSuppressed(recovery);required=true;}
                }finally{if(interrupted)Thread.currentThread().interrupt();}
                if(failure instanceof InterruptedIOException||interrupted)throw new Cancelled(failure,outcome,required);
                throw new Failure(failure,outcome,required);
            }finally{try{if(media!=null)media.close();}finally{try{if(bookmarks!=null)bookmarks.close();}finally{if(sources!=null)sources.close();}}}
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
    private static boolean owned(String key){return PortableSettingsRegistry.rule(key)!=null||key.equals("user_defined_density_confirmed")||key.equals(HOME)||key.equals(HomeIntentCodec.PENDING)||key.equals(CustomLibraryPage.KEY)||key.equals(PortableViewingChoices.QUEUE_PENDING)||key.equals(PortableViewingChoices.DISMISS_PENDING)||key.equals(PortablePreferredVersions.PENDING)||key.equals(PortablePinnedTitles.PENDING)||key.equals(PortableHiddenItems.PENDING)||key.equals(PortableBookmarks.PENDING)||PortablePreferredVersions.ownsKey(key)||PortableLibrarySources.owns(key)||PortableSavedPlaylists.owns(key)||key.matches("preview_cw_dismiss:[vs][1-9][0-9]*");}
    private static boolean healthOwned(String key){return key.equals(PortableReviewNotes.PENDING)||key.matches("incorrect:[1-9][0-9]{0,18}");}
    private static JSONObject record(Plan plan,boolean complete)throws JSONException,IOException{
        JSONArray values=new JSONArray();recordValues(values,"DEFAULT",plan.keys,plan.before);recordValues(values,"HEALTH",plan.healthKeys,plan.healthBefore);
        JSONObject record=new JSONObject().put("schema",plan.sourcesBefore!=null?7:plan.bookmarksBefore!=null?6:plan.mediaBefore!=null?5:plan.viewingBefore==null?2:3).put("complete",complete).put("values",values);
        if(plan.viewingBefore!=null)record.put("viewing",new JSONObject().put("before",new JSONObject(plan.viewingBefore))
                .put("existed",plan.viewingExisted).put("history",plan.historySelected).put("positions",plan.positionsSelected));
        if(plan.mediaBefore!=null){
            if(plan.viewingBefore==null)record.put("viewing",JSONObject.NULL);
            JSONObject before=new JSONObject(plan.mediaBefore),after=new JSONObject(plan.mediaAfter);MediaStateRecovery.validatePair(before,after);
            record.put("media",new JSONObject().put("before",before).put("after",after).put("committed",false));
        }
        if(plan.bookmarksBefore!=null){
            if(plan.viewingBefore==null)record.put("viewing",JSONObject.NULL);
            if(plan.mediaBefore==null)record.put("media",JSONObject.NULL);
            JSONObject before=new JSONObject(plan.bookmarksBefore),after=new JSONObject(plan.bookmarksAfter);BookmarkStoreRecovery.validatePair(before,after);
            record.put("bookmarks",new JSONObject().put("before",before).put("after",after).put("committed",false));
        }
        if(plan.sourcesBefore!=null){if(plan.viewingBefore==null)record.put("viewing",JSONObject.NULL);if(plan.mediaBefore==null)record.put("media",JSONObject.NULL);if(plan.bookmarksBefore==null)record.put("bookmarks",JSONObject.NULL);JSONObject before=new JSONObject(plan.sourcesBefore),after=new JSONObject(plan.sourcesAfter);IndexedSourceStoreRecovery.validatePair(before,after);record.put("sources",new JSONObject().put("before",before).put("after",after).put("committed",false));}
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
            Set<String> keys=new LinkedHashSet<>(),healthKeys=new LinkedHashSet<>();Map<String,Object> originals=new LinkedHashMap<>(),healthOriginals=new LinkedHashMap<>();boolean complete;JSONObject viewing=null,media=null,bookmarks=null,sources=null;
            try(InputStream input=journal.openRead()){
                ByteArrayOutputStream output=new ByteArrayOutputStream();byte[] buffer=new byte[8192];int n;
                while((n=input.read(buffer))!=-1){if(output.size()+n>MAX_JOURNAL)throw new IOException("Private recovery exceeds storage limit");output.write(buffer,0,n);}
                JSONObject record=new JSONObject(new String(output.toByteArray(),StandardCharsets.UTF_8));
                if(!(record.get("schema") instanceof Integer)||!Arrays.asList(1,2,3,4,5,6,7).contains(record.getInt("schema"))||!(record.get("complete") instanceof Boolean))throw new JSONException("Unknown recovery schema");int schema=record.getInt("schema");
                if(record.length()!=(schema==7?7:schema==6?6:schema>=4?5:schema==3?4:3))throw new JSONException("Unknown recovery fields");
                if(schema>=4&&!(schema>=6&&record.isNull("media"))) {
                    media=record.getJSONObject("media");if(media.length()!=(schema>=5?3:2))throw new JSONException("Invalid media recovery fields");
                    if(schema>=5&&(!(media.get("committed") instanceof Boolean)||media.getBoolean("committed")&&!record.getBoolean("complete")))throw new JSONException("Invalid durable media acknowledgement");
                    MediaStateRecovery.validatePair(media.getJSONObject("before"),media.getJSONObject("after"));
                }
                if(schema>=6&&!(schema==7&&record.isNull("bookmarks"))){
                    bookmarks=record.getJSONObject("bookmarks");if(bookmarks.length()!=3||!(bookmarks.get("committed") instanceof Boolean)||bookmarks.getBoolean("committed")&&!record.getBoolean("complete"))throw new JSONException("Invalid durable bookmark acknowledgement");
                    BookmarkStoreRecovery.validatePair(bookmarks.getJSONObject("before"),bookmarks.getJSONObject("after"));
                }
                if(schema==7){sources=record.getJSONObject("sources");if(sources.length()!=3||!(sources.get("committed") instanceof Boolean)||sources.getBoolean("committed")&&!record.getBoolean("complete"))throw new JSONException("Invalid durable indexed-source acknowledgement");IndexedSourceStoreRecovery.validatePair(sources.getJSONObject("before"),sources.getJSONObject("after"));}
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
            if(bookmarks!=null&&!Boolean.TRUE.equals(bookmarks.opt("committed")))try{BookmarkStoreRecovery.recover(destination,bookmarks.getJSONObject("before"),bookmarks.getJSONObject("after"),complete);}
                catch(JSONException invalid){throw new IOException("Bookmark recovery values are invalid",invalid);}
            if(sources!=null&&!Boolean.TRUE.equals(sources.opt("committed")))try{IndexedSourceStoreRecovery.recover(destination,sources.getJSONObject("before"),sources.getJSONObject("after"),complete);}
                catch(JSONException invalid){throw new IOException("Indexed-source recovery values are invalid",invalid);}
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
