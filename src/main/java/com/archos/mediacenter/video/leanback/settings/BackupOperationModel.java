package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.net.Uri;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.archos.mediacenter.video.utils.backup.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/** Activity-owned operations survive view recreation and never keep an Activity, View or provider stream. */
public final class BackupOperationModel extends AndroidViewModel {
    public enum Phase { IDLE, PREPARING_EXPORT, EXPORT_READY, DESTINATION_PENDING, PUBLISHING, EXPORTED, IMPORTING, REVIEW,
        PREPARING_RESTORE, RESTORE_PREVIEW, LEGACY_PREVIEW, RESTORING_LEGACY, RESTORING, RESTORED, CLOSING, FAILED, CANCELLED }
    public static final class State {
        public final Phase phase;public final String message;
        public final Set<BackupCategory> categories;public final int unresolved;
        public final boolean retryExport,recoveryRequired,receivingChecks;
        private State(Phase phase,String message,Set<BackupCategory> categories,int unresolved,boolean retryExport,boolean recoveryRequired,boolean receivingChecks){
            this.phase=phase;this.message=message;this.categories=Collections.unmodifiableSet(new LinkedHashSet<>(categories));
            this.unresolved=unresolved;this.retryExport=retryExport;this.recoveryRequired=recoveryRequired;this.receivingChecks=receivingChecks;
        }
    }
    private final Executor executor;
    private final MutableLiveData<State> updates=new MutableLiveData<>();
    private volatile State latest;
    private volatile boolean busy,cleared;
    private volatile AtomicBoolean cancellation=new AtomicBoolean();
    private VerifiedBackupPublication.Prepared export;
    private BackupImportPreparation imported;
    private PreferenceRestoreTransaction.Plan restorePlan;
    private Set<BackupCategory> exportCategories=Collections.emptySet();
    public BackupOperationModel(Application application){this(application,Executors.newSingleThreadExecutor(r->{Thread worker=new Thread(r,"supernova-backup");worker.setDaemon(true);return worker;}));}
    BackupOperationModel(Application application,Executor executor){super(application);this.executor=executor;state(Phase.IDLE,"",Collections.emptySet(),0,false);}
    public LiveData<State> states(){return updates;}
    public State current(){return latest;}
    public boolean isBusy(){return busy;}
    public void cancel(){cancellation.set(true);}
    private void state(Phase phase,String message,Set<BackupCategory> categories,int unresolved,boolean recoveryRequired){
        latest=new State(phase,message,categories,unresolved,export!=null,recoveryRequired,restorePlan!=null&&!restorePlan.receivingChecks.isEmpty());if(!busy)updates.postValue(latest);
    }
    private interface Work {void run(AtomicBoolean cancelled)throws Exception;}
    private synchronized void work(Phase phase,String message,Work work){
        if(busy||cleared)throw new IllegalStateException("Another backup operation is active");busy=true;AtomicBoolean token=new AtomicBoolean();cancellation=token;
        state(phase,message,imported==null?exportCategories:imported.categories(),0,false);updates.postValue(latest);
        try{executor.execute(()->{
            try{work.run(token);}catch(Exception failure){
                boolean interrupted=failure instanceof InterruptedIOException||failure instanceof VerifiedBackupPublication.Failure&&((VerifiedBackupPublication.Failure)failure).cancelled||failure instanceof LogicalBackupImport.Failure&&((LogicalBackupImport.Failure)failure).cancelled;
                boolean recovery=failure instanceof PreferenceRestoreTransaction.Failure&&((PreferenceRestoreTransaction.Failure)failure).recoveryRequired||failure instanceof PreferenceRestoreTransaction.Cancelled&&((PreferenceRestoreTransaction.Cancelled)failure).recoveryRequired;
                String text=failure instanceof PreferenceRestoreTransaction.Failure||failure instanceof PreferenceRestoreTransaction.Cancelled?failure.getMessage():
                    failure instanceof VerifiedBackupPublication.Failure?"Destination was not verified. The private backup is retained for retry."+
                        (((VerifiedBackupPublication.Failure)failure).cleanup==VerifiedBackupPublication.Cleanup.LEFT_UNVERIFIED?" An incomplete destination file may remain.":""):
                    phase==Phase.PREPARING_EXPORT?"Backup could not be prepared. Existing backups and application data were retained.":
                    phase==Phase.IMPORTING?"Backup could not be validated. No settings or library state were changed.":"Operation could not finish. Review the current state before retrying.";
                state(interrupted?Phase.CANCELLED:Phase.FAILED,text,imported==null?exportCategories:imported.categories(),0,recovery);
            }finally{busy=false;if(cleared)cleanupAfterClear();updates.postValue(latest);}
        });}catch(RuntimeException rejected){busy=false;state(Phase.FAILED,"Backup worker is unavailable.",Collections.emptySet(),0,false);throw rejected;}
    }
    public void create(Set<BackupCategory> selected){
        Set<BackupCategory> copy=Collections.unmodifiableSet(new LinkedHashSet<>(selected));
        work(Phase.PREPARING_EXPORT,"Preparing and verifying backup…",token->{
            File file=LogicalBackupExporter.preparePrivate(getApplication(),copy,token::get);VerifiedBackupPublication.Prepared prepared;
            try{prepared=VerifiedBackupPublication.prepare(file,token::get);}catch(Exception invalid){if(!file.delete())invalid.addSuppressed(new IOException("Private incomplete backup cleanup pending"));throw invalid;}
            export=prepared;exportCategories=copy;prunePrivateExports(file);state(Phase.EXPORT_READY,"Backup verified. Choose where to save it.",copy,0,false);
        });
    }
    public void destinationPending(){if(export==null||busy)throw new IllegalStateException("Verified backup unavailable");state(Phase.DESTINATION_PENDING,"Choose a destination in the Android file picker.",exportCategories,0,false);}
    public void destinationCancelled(){state(Phase.CANCELLED,"Save cancelled. The verified private backup is retained for retry.",exportCategories,0,false);}
    /** ACTION_CREATE_DOCUMENT can still select an existing document; never infer safe deletion ownership. */
    public void publish(Uri document){publish(VerifiedBackupPublication.saf(getApplication().getContentResolver(),document,false));}
    void publish(VerifiedBackupPublication.Destination destination){
        if(export==null)throw new IllegalStateException("Prepare a verified backup first");VerifiedBackupPublication.Prepared prepared=export;
        work(Phase.PUBLISHING,"Saving and reading back the backup…",token->{VerifiedBackupPublication.publish(prepared,destination,token::get);state(Phase.EXPORTED,"Backup saved and verified.",exportCategories,0,false);});
    }
    public void open(Uri document){work(Phase.IMPORTING,"Copying and validating backup…",token->{
        closeImported();restorePlan=null;imported=BackupImportPreparation.prepare(getApplication(),getApplication().getContentResolver().openInputStream(document),token::get);
        reviewImported();
    });}
    void open(InputStream source){work(Phase.IMPORTING,"Copying and validating backup…",token->{closeImported();restorePlan=null;imported=BackupImportPreparation.prepare(getApplication(),source,token::get);reviewImported();});}
    private void reviewImported(){
        state(imported.isLegacy()?Phase.LEGACY_PREVIEW:Phase.REVIEW,imported.isLegacy()?"Legacy format1.0: replaces the whole supported library and settings together, including retained artwork and source locations. Receiving authentication is cleared; reconnect protected services and shares. Logical category selection does not apply. Make a destination backup before continuing.":"Select the present categories to replace. Missing categories remain unchanged.",imported.categories(),0,false);
    }
    public void confirmLegacy(){
        if(latest.phase!=Phase.LEGACY_PREVIEW||imported==null||!imported.isLegacy())throw new IllegalStateException("Review legacy replacement before confirming");
        work(Phase.RESTORING_LEGACY,"Restoring legacy library and settings. The coordinated replacement must finish before cancellation or exit.",token->{imported.restoreLegacy(getApplication(),token::get);restorePlan=null;String message="Legacy supported library and settings restored. Reconnect protected accounts and sources and verify the library.";try{closeImported();}catch(IOException cleanup){message+=" Private review cleanup is pending.";}state(Phase.RESTORED,message,Collections.emptySet(),0,false);});
    }
    public void preview(Set<BackupCategory> selected){
        Set<BackupCategory> copy=new LinkedHashSet<>(selected);
        work(Phase.PREPARING_RESTORE,"Checking selected replacement and receiving identities…",token->{
            if(imported==null||copy.isEmpty()||!imported.categories().containsAll(copy))throw new IOException("Select only present categories");
            Map<BackupCategory,JSONObject> payloads=new EnumMap<>(BackupCategory.class);for(var item:imported.payloads(token::get).entrySet())if(copy.contains(item.getKey()))payloads.put(item.getKey(),item.getValue());
            boolean needsInventory=!Collections.disjoint(copy,EnumSet.of(BackupCategory.HOME,BackupCategory.WATCH_NEXT,BackupCategory.CONTINUE_CHOICES,BackupCategory.PREFERRED,BackupCategory.REVIEW,BackupCategory.HIDDEN,BackupCategory.TITLE_PLAYBACK));
            LogicalBackupCatalog catalog=needsInventory?LogicalBackupCatalog.capture(getApplication(),token::get):null;
            PreferenceRestoreTransaction transaction=new PreferenceRestoreTransaction(getApplication());restorePlan=transaction.prepare(payloads,catalog==null?new com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot():catalog.snapshot,new PortablePhysicalReferences(getApplication(),catalog==null?Collections.emptyList():catalog.physical));
            if(token.get())throw new InterruptedIOException("Restore preview cancelled");
            state(Phase.RESTORE_PREVIEW,"Selected categories replace existing state. Unresolved references remain pending. Reconnect storage and services separately.",copy,restorePlan.unresolved,false);
        });
    }
    public void confirm(){
        if(latest.phase!=Phase.RESTORE_PREVIEW||restorePlan==null)throw new IllegalStateException("Review replacement before confirming");PreferenceRestoreTransaction.Plan plan=restorePlan;
        work(Phase.RESTORING,"Restoring selected categories…",token->{new PreferenceRestoreTransaction(getApplication()).apply(plan,token::get);restorePlan=null;
            String message="Selected state restored. Reconnect sources and services, then scan and check unresolved items.";try{closeImported();}catch(IOException cleanup){message+=" Private review cleanup is pending.";}state(Phase.RESTORED,message,plan.categories,plan.unresolved,false);});
    }
    public void closeReview(){work(Phase.CLOSING,"Closing restore review…",token->{closeImported();restorePlan=null;state(Phase.IDLE,"",Collections.emptySet(),0,false);});}
    private void closeImported()throws IOException{if(imported!=null){imported.close();imported=null;}}
    private void cleanupAfterClear(){try{closeImported();}catch(IOException cleanup){state(Phase.FAILED,"Private restore preview cleanup is pending.",Collections.emptySet(),0,false);}}
    /** At most three app-owned archives, each independently bounded to24MiB. Never touches user documents. */
    private void prunePrivateExports(File keep)throws IOException {
        File[] files=keep.getParentFile().listFiles(file->file.isFile()&&file.getName().matches("Supernova-[a-f0-9-]{36}\\.zip"));if(files==null)throw new IOException("Private backup retention unavailable");
        Arrays.sort(files,Comparator.comparingLong(File::lastModified).reversed());int retained=1;
        for(File file:files)if(!file.equals(keep)){if(retained++>=3&&!file.delete())throw new IOException("Private backup retention cleanup pending");}
    }
    @Override protected void onCleared(){cleared=true;cancel();if(executor instanceof ExecutorService)((ExecutorService)executor).shutdown();if(!busy)cleanupAfterClear();}
}
