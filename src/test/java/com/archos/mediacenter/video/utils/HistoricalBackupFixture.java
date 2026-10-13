// Copyright 2025 Courville Software
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video.utils;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.*;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.VideoDb;
import com.archos.mediascraper.MediaScraper;
import java.io.*;
import java.util.zip.*;
/** Test-only historical1.0 fixture writer. Not packaged or reachable by application export. */
final class HistoricalBackupFixture extends ContextWrapper {
    private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(HistoricalBackupFixture.class);
    private static final String DATABASE_NAME="media.db",CREDENTIALS_DB_NAME="credentials_db",SHORTCUTS_DB_NAME="shortcuts_db",SHORTCUTS2_DB_NAME="shortcuts2_db",VERSION_FILE="db_version.txt";
    private static final int BUFFER_SIZE=8192;
    private HistoricalBackupFixture(Context context){super(context);}
    static String export(Context context)throws IOException{return new HistoricalBackupFixture(context).exportMediaLibrary();}
    private void addPrivateDatabaseToZip(java.util.zip.ZipOutputStream zip,File original,String name)throws IOException{
        File clean=BackupPrivacy.database(original,getCacheDir());try{BackupPrivacy.applyViewingSelection(this,clean);addFileToZip(zip,clean,name);}finally{clean.delete();}
    }
    private String exportMediaLibrary() throws IOException {
        java.util.Set<String> reproducible=MigrationBackup.reproducibleFiles(this);
        DbHolder holder = VideoDb.getHolder(this);
        holder.get(); // Ensure even an empty library has its schema before snapshotting.
        synchronized(com.archos.mediaprovider.video.ProviderDiscoveryGate.LOCK){
            holder.lockExclusive();
            try {holder.close();return exportLockedLibrary(reproducible);}
            finally {holder.unlockExclusive();}
        }
    }

    private String exportLockedLibrary(java.util.Set<String> reproducible) throws IOException {
        if (log.isDebugEnabled()) log.debug("exportMediaLibrary: starting full library export");

        // Get export directory
        File exportDir = new File(getCacheDir(),"historical-backup-fixtures");exportDir.mkdirs();
        if (exportDir == null) {
            exportDir = new File(getFilesDir(),"backups");
        }

        // Flush database WAL to ensure consistency
        if (log.isDebugEnabled()) log.debug("exportMediaLibrary: flushing database WAL");
        flushDatabaseWAL();

        // Create export zip file (fixed name, will overwrite previous backup)
        File zipFile = new File(exportDir, "nova-backup-" + System.currentTimeMillis() + ".zip");

        // Delete old backup if it exists
        if (zipFile.exists()) {
            if (log.isDebugEnabled()) log.debug("exportMediaLibrary: deleting existing backup file: {}", zipFile.getAbsolutePath());
            if (!zipFile.delete()) {
                log.warn("exportMediaLibrary: failed to delete existing backup file");
            }
        }

        if (log.isDebugEnabled()) log.debug("exportMediaLibrary: creating new backup file: {}", zipFile.getAbsolutePath());

        VerifiedBackup.write(zipFile, zos -> {
            zos.putNextEntry(new ZipEntry("manifest.json"));zos.write(BackupFormat.manifest().getBytes(java.nio.charset.StandardCharsets.UTF_8));zos.closeEntry();
            zos.putNextEntry(new ZipEntry("RESTORE_INSTRUCTIONS.txt"));zos.write(BackupFormat.instructions().getBytes(java.nio.charset.StandardCharsets.UTF_8));zos.closeEntry();
            try {
                byte[] settings = BackupPrivacy.selectedSettings(this,SettingsBackup.encodePortable(androidx.preference.PreferenceManager.getDefaultSharedPreferences(this),false))
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);
                zos.putNextEntry(new ZipEntry("settings.json")); zos.write(settings); zos.closeEntry();
                zos.putNextEntry(new ZipEntry("named_preferences.json"));zos.write(MigrationBackup.settings(this).getBytes(java.nio.charset.StandardCharsets.UTF_8));zos.closeEntry();
            } catch (org.json.JSONException e) { throw new IOException("Could not export settings", e); }
            File historyFile=new File(getCacheDir(),"history-export-"+java.util.UUID.randomUUID());
            try{com.archos.mediacenter.video.leanback.ViewingHistory.export(this,historyFile);zos.putNextEntry(new ZipEntry(com.archos.mediacenter.video.leanback.ViewingHistory.DATABASE));try(java.io.InputStream history=new java.io.FileInputStream(historyFile)){byte[] bytes=new byte[65536];int count;while((count=history.read(bytes))!=-1)zos.write(bytes,0,count);}zos.closeEntry();}finally{historyFile.delete();}
            // Export database version first
            int dbVersion = VideoOpenHelper.getDatabaseVersion();
            if (log.isDebugEnabled()) log.debug("exportMediaLibrary: adding database version={}", dbVersion);
            addVersionToZip(zos, dbVersion);

            // Export media database
            File dbFile = getDatabasePath(DATABASE_NAME);
            if (dbFile.exists()) {
                if (log.isDebugEnabled()) log.debug("exportMediaLibrary: exporting media database");
                addPrivateDatabaseToZip(zos,dbFile,DATABASE_NAME);
            } else {
                throw new IOException("Media database is unavailable; backup was not created");
            }

            // Export shortcuts database (network shortcuts/bookmarks)
            File shortcutsDbFile = getDatabasePath(SHORTCUTS_DB_NAME);
            if (shortcutsDbFile.exists()) {
                if (log.isDebugEnabled()) log.debug("exportMediaLibrary: exporting shortcuts database");
                addPrivateDatabaseToZip(zos,shortcutsDbFile,SHORTCUTS_DB_NAME);
            } else {
                if (log.isDebugEnabled()) log.debug("exportMediaLibrary: shortcuts database not found (no saved shortcuts)");
            }

            // Export shortcuts2 database (newer network shortcuts/bookmarks)
            File shortcuts2DbFile = getDatabasePath(SHORTCUTS2_DB_NAME);
            if (shortcuts2DbFile.exists()) {
                if (log.isDebugEnabled()) log.debug("exportMediaLibrary: exporting shortcuts2 database");
                addPrivateDatabaseToZip(zos,shortcuts2DbFile,SHORTCUTS2_DB_NAME);
            } else {
                if (log.isDebugEnabled()) log.debug("exportMediaLibrary: shortcuts2 database not found (no saved shortcuts)");
            }

            File associations=getDatabasePath("putio-associations.db");if(associations.isFile())addPrivateDatabaseToZip(zos,associations,"putio-associations.db");

            // Export poster directory
            File posterDir = MediaScraper.getPosterDirectory(this);
            if (posterDir.exists()) {
                addPersonalArtworkToZip(zos, posterDir, "scraper_posters",reproducible);
            }

            // Export backdrop directory
            File backdropDir = MediaScraper.getBackdropDirectory(this);
            if (backdropDir.exists()) {
                addPersonalArtworkToZip(zos, backdropDir, "scraper_backdrops",reproducible);
            }

            // Episode stills are reproducible scraper output, not user-created artwork.
        });

        if (log.isDebugEnabled()) log.debug("exportMediaLibrary: export completed to {}", zipFile.getAbsolutePath());
        return zipFile.getAbsolutePath();
    }

    private void flushDatabaseWAL() {
        try {
            for(String database:new String[]{DATABASE_NAME,CREDENTIALS_DB_NAME,SHORTCUTS_DB_NAME,SHORTCUTS2_DB_NAME,"putio-associations.db"}){File dbFile = getDatabasePath(database);
            if (dbFile.exists()) {
                if (log.isDebugEnabled()) log.debug("flushDatabaseWAL: opening database for WAL checkpoint");
                try(SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null,
                        SQLiteDatabase.OPEN_READWRITE)){
                // Checkpoint WAL to main database file
                if (log.isDebugEnabled()) log.debug("flushDatabaseWAL: executing PRAGMA wal_checkpoint(FULL)");
                try (android.database.Cursor checkpoint = db.rawQuery("PRAGMA wal_checkpoint(FULL)", null)) {
                    if (!checkpoint.moveToFirst() || checkpoint.getInt(0) != 0) throw new IllegalStateException("Database is busy");
                }
                }
                if (log.isDebugEnabled()) log.debug("flushDatabaseWAL: database WAL flushed successfully");
            }}
        } catch (Exception e) {
            throw new IllegalStateException("Cannot make a consistent database backup", e);
        }
    }

    private void addVersionToZip(ZipOutputStream zos, int version) throws IOException {
        if (log.isDebugEnabled()) log.debug("addVersionToZip: adding version={}", version);

        ZipEntry zipEntry = new ZipEntry(VERSION_FILE);
        zos.putNextEntry(zipEntry);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos));
        writer.write(String.valueOf(version));
        writer.newLine();
        writer.flush();

        zos.closeEntry();
    }

    private void addFileToZip(ZipOutputStream zos, File file, String zipEntryName) throws IOException {
        if (log.isDebugEnabled()) log.debug("addFileToZip: {}", zipEntryName);

        ZipEntry zipEntry = new ZipEntry(zipEntryName);
        zos.putNextEntry(zipEntry);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        }

        zos.closeEntry();
    }

    private void addPersonalArtworkToZip(ZipOutputStream zip,File dir,String prefix,java.util.Set<String> reproducible)throws IOException{File[] files=dir.listFiles();if(files==null)throw new IOException("Cannot list personal artwork");for(File file:files)if(file.isDirectory())addPersonalArtworkToZip(zip,file,prefix+"/"+file.getName(),reproducible);else if(!reproducible.contains(file.getCanonicalPath()))addFileToZip(zip,file,prefix+"/"+file.getName());}

    private void addDirectoryToZip(ZipOutputStream zos, File dir, String zipDirName) throws IOException {
        if (log.isDebugEnabled()) log.debug("addDirectoryToZip: {}", zipDirName);

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String zipEntryName = zipDirName + "/" + file.getName();

            if (file.isDirectory()) {
                addDirectoryToZip(zos, file, zipEntryName);
            } else {
                addFileToZip(zos, file, zipEntryName);
            }
        }
    }

}
