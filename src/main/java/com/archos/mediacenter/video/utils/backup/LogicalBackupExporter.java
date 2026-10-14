package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.*;
import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Real source-to-archive export. No legacy DB snapshot writer or content/account payload is reachable. */
public final class LogicalBackupExporter {
    private static final Set<BackupCategory> INVENTORY=EnumSet.of(BackupCategory.HOME,BackupCategory.WATCH_NEXT,
        BackupCategory.CONTINUE_CHOICES,BackupCategory.PREFERRED,BackupCategory.REVIEW,BackupCategory.PINNED,BackupCategory.HIDDEN,BackupCategory.PLAYLISTS);
    /** Unsupported selected categories fail before reading source or allocating a backup file; none is silently omitted. */
    public static Map<BackupCategory,JSONObject> collect(Context context,Set<BackupCategory> selected,BooleanSupplier cancel)throws IOException {
        LogicalBackupCatalog.cancelled(cancel);
        if(selected==null||selected.isEmpty())throw new IOException("Choose at least one backup category");
        EnumSet<BackupCategory> categories=EnumSet.copyOf(selected);
        if(!LogicalBackupArchive.registered().containsAll(categories))throw new IOException("Selected backup category is not implemented yet; no backup was created");
        try {
            LogicalBackupCatalog catalog=Collections.disjoint(categories,INVENTORY)?null:LogicalBackupCatalog.capture(context,cancel,categories.contains(BackupCategory.PLAYLISTS));
            // Allocate the private opaque origin before the consistency snapshot; this is never an archive setting.
            if(catalog!=null||categories.contains(BackupCategory.BOOKMARKS))new PortableTitleReferences(context,catalog==null?null:catalog.snapshot);
            var preferences=PreferenceManager.getDefaultSharedPreferences(context);Map<String,?> before=preferences.getAll();
            var health=context.getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0);Map<String,?> healthBefore=health.getAll();
            PortablePhysicalReferences physical=catalog==null?null:new PortablePhysicalReferences(context,catalog.physical);
            Map<String,ViewingHistory.Record> viewing=Collections.emptyMap();
            if(categories.contains(BackupCategory.HISTORY)||categories.contains(BackupCategory.POSITIONS)){
                ViewingHistory.awaitWrites();viewing=PortableViewingState.validateRecoverySnapshot(ViewingHistory.captureRecovery(context));
            }
            JSONObject bookmarks=categories.contains(BackupCategory.BOOKMARKS)?BookmarkStoreRecovery.capture(context):null;
            Set<String> locations=new TreeSet<>();
            if(categories.contains(BackupCategory.LOCATIONS)){if(categories.contains(BackupCategory.SOURCES))locations.addAll(PortableLibrarySources.locations(context));if(bookmarks!=null)locations.addAll(PortableBookmarks.locations(bookmarks));}
            Map<BackupCategory,JSONObject> payloads=new EnumMap<>(BackupCategory.class);
            for(BackupCategory category:categories) {
                LogicalBackupCatalog.cancelled(cancel);JSONObject payload;
                if(category.settings()!=null)payload=PortableSettingsRegistry.export(preferences,category.settings());
                else switch(category) {
                    case HOME:payload=HomeIntentCodec.export(context,catalog.snapshot);break;
                    case CUSTOM_PAGES:payload=PortableCustomPage.export(context);break;
                    case WATCH_NEXT:case CONTINUE_CHOICES:payload=PortableViewingChoices.export(context,catalog.snapshot,PortableViewingChoices.Kind.valueOf(category.name()));break;
                    case PREFERRED:payload=PortablePreferredVersions.export(context,physical);break;
                    case REVIEW:payload=PortableReviewNotes.export(context,physical);break;
                    case PINNED:payload=PortablePinnedTitles.export(context,catalog.pins);break;
                    case HIDDEN:payload=PortableHiddenItems.export(context,catalog.hidden,physical);break;
                    case HISTORY:case POSITIONS:payload=PortableViewingState.export(viewing,PortableViewingState.Kind.valueOf(category.name()));break;
                    case SOURCES:payload=PortableLibrarySources.export(context);break;
                    case BOOKMARKS:payload=PortableBookmarks.export(context,bookmarks);break;
                    case PLAYLISTS:payload=PortableSavedPlaylists.export(context,catalog.playlists);break;
                    case LOCATIONS:payload=PortableLocations.export(locations);break;
                    default:throw new IOException("Selected category has no source exporter");
                }
                payloads.put(category,payload);
            }
            if(!before.equals(preferences.getAll())||!healthBefore.equals(health.getAll()))throw new IOException("Configuration changed during backup; retry to capture a consistent selection");
            if(bookmarks!=null&&!bookmarks.toString().equals(BookmarkStoreRecovery.capture(context).toString()))throw new IOException("Bookmarks changed during backup; retry to capture a consistent selection");
            LogicalBackupCatalog.cancelled(cancel);return Collections.unmodifiableMap(payloads);
        }catch(JSONException|RuntimeException invalid){throw new IOException("Selected configuration failed logical backup validation; no backup was created",invalid);}
    }
    public static File preparePrivate(Context context,Set<BackupCategory> selected,BooleanSupplier cancel)throws IOException {
        Map<BackupCategory,JSONObject> payloads=collect(context,selected,cancel);
        File folder=new File(context.getCacheDir(),"supernova-logical-backups");
        if(!folder.isDirectory()&&!folder.mkdirs())throw new IOException("Cannot prepare private logical backup folder");
        File archive=new File(folder,"Supernova-"+UUID.randomUUID()+".zip");boolean complete=false;
        try{LogicalBackupArchive.writeCategories(archive,payloads,cancel);LogicalBackupCatalog.cancelled(cancel);complete=true;return archive;}
        finally{if(!complete&&archive.exists()&&!archive.delete())archive.deleteOnExit();}
    }
    private LogicalBackupExporter(){}
}
