package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.browser.loader.VideoLoader;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediaprovider.video.*;
import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;

/** Worker-only identity inventory. Never uses visible-library filters, caches or history reconciliation. */
public final class LogicalBackupCatalog {
    public final Snapshot snapshot;
    public final List<Video> physical;
    public final List<PortablePinnedTitles.Title> pins;
    public final Map<Long,Boolean> hidden;
    public final Map<Long,PortableSavedPlaylists.Definition> playlists;
    public final Map<Long,PortableTitlePlayback.State> titlePlayback;
    public final Map<Long,PortableMediaMatches.Match> matches;
    private LogicalBackupCatalog(Snapshot snapshot,List<Video> physical,List<PortablePinnedTitles.Title> pins,Map<Long,Boolean> hidden,Map<Long,PortableSavedPlaylists.Definition> playlists,Map<Long,PortableTitlePlayback.State> titlePlayback,Map<Long,PortableMediaMatches.Match> matches){
        this.snapshot=snapshot;this.physical=Collections.unmodifiableList(physical);this.pins=pins;this.hidden=hidden;this.playlists=playlists;this.titlePlayback=titlePlayback;this.matches=matches;
    }
    static void cancelled(BooleanSupplier cancel)throws InterruptedIOException{
        if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Logical backup cancelled");
    }
    public static LogicalBackupCatalog capture(Context context,BooleanSupplier cancel)throws IOException {
        return capture(context,cancel,false);
    }
    public static LogicalBackupCatalog capture(Context context,BooleanSupplier cancel,boolean playlists)throws IOException {
        return capture(context,cancel,playlists,false);
    }
    public static LogicalBackupCatalog capture(Context context,BooleanSupplier cancel,boolean playlists,boolean titlePlayback)throws IOException {
        return capture(context,cancel,playlists,titlePlayback,false);
    }
    public static LogicalBackupCatalog capture(Context context,BooleanSupplier cancel,boolean playlists,boolean titlePlayback,boolean matches)throws IOException {
        cancelled(cancel);Snapshot snapshot=new Snapshot();List<Video> physical=new ArrayList<>();File file=VideoOpenHelper.getDatabaseFile(context);
        if(!file.exists())return new LogicalBackupCatalog(snapshot,physical,Collections.emptyList(),Collections.emptyMap(),Collections.emptyMap(),Collections.emptyMap(),Collections.emptyMap());
        try(SQLiteDatabase database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)) {
            if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Indexed media schema is not compatible with logical backup");
            // One SQLite read snapshot across files, show identities, pins and hidden state.
            database.execSQL("SAVEPOINT supernova_backup_read");try {
                Set<String> columns=new LinkedHashSet<>(Arrays.asList(VideoLoader.mProjection));
                columns.addAll(List.of(VideoStore.Video.VideoColumns.DATE_ADDED,VideoStore.Video.VideoColumns.SCRAPER_M_GENRES,
                    VideoStore.Video.VideoColumns.SCRAPER_S_GENRES,android.provider.MediaStore.Video.VideoColumns.WIDTH,
                    android.provider.MediaStore.Video.VideoColumns.HEIGHT,VideoStore.Video.VideoColumns.ARCHOS_CALCULATED_BEST_AUDIOTRACK_FORMAT,
                    VideoStore.Video.VideoColumns.ARCHOS_CALCULATED_VIDEO_FORMAT));
                Set<Long> seen=new HashSet<>();Map<Long,Long> showAdded=new HashMap<>();
                try(Cursor rows=database.query(VideoOpenHelper.VIDEO_VIEW_NAME,columns.toArray(new String[0]),null,null,null,null,"_id ASC")) {
                    VideoCursorMapper mapper=new VideoCursorMapper();mapper.bindColumns(rows);
                    int added=rows.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.DATE_ADDED),show=rows.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);
                    int movieGenres=rows.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_M_GENRES),showGenres=rows.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_S_GENRES),showOnline=rows.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_S_ONLINE_ID);
                    while(rows.moveToNext()) {
                        cancelled(cancel);Video video=(Video)mapper.bind(rows);if(video.getId()<=0||!seen.add(video.getId())||physical.size()>=100000)throw new IOException("Indexed physical inventory is ambiguous or exceeds supported limits");
                        physical.add(video);Entry entry=new Entry(video,rows.isNull(added)?0:rows.getLong(added),rows.isNull(show)?0:rows.getLong(show),rows.getString(video instanceof Episode?showGenres:movieGenres));
                        snapshot.technical.add(entry);
                        if(video instanceof Movie)snapshot.movies.add(entry);else if(video instanceof Episode){entry.onlineId=rows.isNull(showOnline)?0:rows.getLong(showOnline);snapshot.episodes.add(entry);showAdded.merge(entry.show,entry.added,Math::max);}else snapshot.unmatched.add(entry);
                    }
                }
                // Include orphaned logical shows too: a saved reference need not have a currently available file.
                try(Cursor rows=database.query(ScraperTables.SHOW_TABLE_NAME,new String[]{"_id",ScraperStore.Show.ONLINE_ID,ScraperStore.Show.NAME,"s_genres"},null,null,null,null,"_id ASC")) {
                    while(rows.moveToNext()) {
                        cancelled(cancel);if(snapshot.shows.size()>=100000||rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||rows.getLong(0)<=0)throw new IOException("Indexed show identity is invalid");
                        long id=rows.getLong(0);Tvshow media=new Tvshow(id,rows.getString(2),null,0,0,0,false,false,null,null,null,0,0,null,0,null);
                        Entry entry=new Entry(media,showAdded.getOrDefault(id,0L),id,rows.getString(3));entry.onlineId=rows.isNull(1)?0:rows.getLong(1);snapshot.shows.add(entry);
                    }
                }
                cancelled(cancel);List<PortablePinnedTitles.Title> pins=PortablePinnedTitles.read(database);Map<Long,Boolean> hidden=PortableHiddenItems.read(database);
                return new LogicalBackupCatalog(snapshot,physical,pins,hidden,playlists?PortableSavedPlaylists.read(database):Collections.emptyMap(),titlePlayback?PortableTitlePlayback.read(database):Collections.emptyMap(),matches?PortableMediaMatches.read(database):Collections.emptyMap());
            }finally{database.execSQL("RELEASE SAVEPOINT supernova_backup_read");}
        }catch(RuntimeException invalid){throw new IOException("Complete indexed identity inventory is unavailable; backup was not created",invalid);}
    }
}
