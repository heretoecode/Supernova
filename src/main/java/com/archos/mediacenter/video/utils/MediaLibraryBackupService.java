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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.VideoDb;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediascraper.MediaScraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MediaLibraryBackupService extends Service {
    private static final Logger log = LoggerFactory.getLogger(MediaLibraryBackupService.class);

    public static final String ACTION_EXPORT = "export_library";
    public static final String ACTION_IMPORT = "import_library";
    public static final String EXTRA_IMPORT_FILE = "import_file";
    public static final String EXTRA_EXPORT_URI = "export_uri";
    private String exportUri;

    private static final int NOTIFICATION_ID = 7;
    private static final String NOTIF_CHANNEL_ID = "MediaLibraryBackup_id";
    private static final String NOTIF_CHANNEL_NAME = "Media Library Backup";
    private static final String NOTIF_CHANNEL_DESCR = "Media Library Backup Service";

    private static final String DATABASE_NAME = "media.db";
    private static final String CREDENTIALS_DB_NAME = "credentials_db";
    private static final String SHORTCUTS_DB_NAME = "shortcuts_db";
    private static final String SHORTCUTS2_DB_NAME = "shortcuts2_db";
    private static final String VERSION_FILE = "db_version.txt";
    private static final String BACKUP_FILENAME = "backup.zip";
    private static final int BUFFER_SIZE = 8192;

    private NotificationManager nm;
    private NotificationCompat.Builder nb;
    private Thread mThread;

    @Override
    public void onCreate() {
        super.onCreate();
        if (log.isDebugEnabled()) log.debug("onCreate");

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(NOTIF_CHANNEL_ID, NOTIF_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            nc.setDescription(NOTIF_CHANNEL_DESCR);
            nc.setSound(null, null);
            nc.enableLights(false);
            nc.enableVibration(false);
            nm.createNotificationChannel(nc);
        }

        nb = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.nova_notification)
                .setContentTitle(getString(R.string.media_library_export_in_progress))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (log.isDebugEnabled()) log.debug("onStartCommand: intent={}", intent);

        androidx.core.app.ServiceCompat.startForeground(this, NOTIFICATION_ID, nb.build(),
                android.os.Build.VERSION.SDK_INT >= 29 ? android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC : 0);
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_EXPORT.equals(action)) {
                startExport(intent.getStringExtra(EXTRA_EXPORT_URI));
            } else if (ACTION_IMPORT.equals(action)) {
                String importFile = intent.getStringExtra(EXTRA_IMPORT_FILE);
                startImport(importFile);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startExport(final String destination) {
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("backup_export_requested");
        if (mThread != null && mThread.isAlive()) {
            log.warn("startExport: export already in progress");
            return;
        }

        mThread = new Thread(() -> {
            android.net.Uri[] published={destination==null?null:android.net.Uri.parse(destination)};
            try {
                nb.setContentTitle(getString(R.string.media_library_export_in_progress));
                nm.notify(NOTIFICATION_ID, nb.build());

                String exportPath = exportMediaLibrary();
                if (destination != null) {
                    try (java.io.InputStream in = new FileInputStream(exportPath);
                         java.io.OutputStream out = getContentResolver().openOutputStream(published[0], "w")) {
                        if (out == null) throw new IOException("Destination unavailable");
                        byte[] data = new byte[8192]; int n; long written=0;while ((n=in.read(data))!=-1){out.write(data,0,n);written+=n;}out.flush();if(written==0||written!=new File(exportPath).length())throw new IOException("Backup destination copy was incomplete");
                    }
                    java.security.MessageDigest expected=java.security.MessageDigest.getInstance("SHA-256"),actual=java.security.MessageDigest.getInstance("SHA-256");
                    byte[] verification=new byte[8192];try(java.io.InputStream local=new FileInputStream(exportPath)){int n;while((n=local.read(verification))!=-1)expected.update(verification,0,n);}
                    try(java.io.InputStream saved=getContentResolver().openInputStream(published[0])){if(saved==null)throw new IOException("Cannot verify the selected backup location");int n;while((n=saved.read(verification))!=-1)actual.update(verification,0,n);}
                    if(!java.util.Arrays.equals(expected.digest(),actual.digest()))throw new IOException("The selected backup location did not retain a complete copy");
                    String name="nova-backup-"+System.currentTimeMillis()+".zip";
                    try(android.database.Cursor document=getContentResolver().query(published[0],new String[]{android.provider.OpenableColumns.DISPLAY_NAME},null,null,null)){if(document!=null&&document.moveToFirst()){String chosen=document.getString(0);if(chosen!=null)name=chosen.replaceFirst("\\.in-progress(?:\\.zip)?$","");}}
                    if(!name.endsWith(".zip"))name+=".zip";
                    boolean renamed=false;
                    try{android.net.Uri complete=android.provider.DocumentsContract.renameDocument(getContentResolver(),published[0],name);if(complete!=null){published[0]=complete;renamed=true;}}catch(Exception unsupported){log.info("Backup verified; destination does not support rename");}
                    exportPath = renamed?name:"selected location (verified archive; this provider cannot rename the .in-progress file — rename it to .zip)";
                }

                showToast(getString(R.string.media_library_export_success, exportPath));
                com.archos.mediacenter.video.diagnostics.Diagnostics.event("backup_export_verified");
                nm.cancel(NOTIFICATION_ID);
            } catch (Exception e) {
                log.error("startExport: error exporting media library", e);
                com.archos.mediacenter.video.diagnostics.Diagnostics.error("backup_export_failed",e);
                // A picker may select an existing document; never infer deletion ownership.
                if(destination!=null)showToast("The destination was not verified. An incomplete file may remain; existing documents were not deleted.");
                showToast(getString(R.string.media_library_export_error)+" · "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
            } finally {
                ServiceCompat.stopForeground(MediaLibraryBackupService.this, ServiceCompat.STOP_FOREGROUND_REMOVE);
                stopSelf();
            }
        });
        mThread.start();
    }

    private void startImport(String importFilePath) {
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("backup_restore_requested");
        if (mThread != null && mThread.isAlive()) {
            log.warn("startImport: import already in progress");
            return;
        }

        mThread = new Thread(() -> {
            try {
                nb.setContentTitle(getString(R.string.media_library_import_in_progress));
                nm.notify(NOTIFICATION_ID, nb.build());

                importMediaLibrary(importFilePath);
                com.archos.mediacenter.video.diagnostics.Diagnostics.event("backup_restore_completed");

                nm.cancel(NOTIFICATION_ID);
                showToast(getString(R.string.media_library_import_success));

                // Wait 2 seconds for user to see success message, then restart app
                if (log.isDebugEnabled()) log.debug("startImport: waiting 2 seconds before restarting app");
                Thread.sleep(2000);

                restartApplication();
            } catch (Exception e) {
                log.error("startImport: error importing media library", e);
                com.archos.mediacenter.video.diagnostics.Diagnostics.error("backup_restore_failed",e);
                showToast(getString(R.string.media_library_import_error));
            } finally {
                ServiceCompat.stopForeground(MediaLibraryBackupService.this, ServiceCompat.STOP_FOREGROUND_REMOVE);
                stopSelf();
            }
        });
        mThread.start();
    }

    private String exportMediaLibrary() throws IOException {
        java.util.Set<com.archos.mediacenter.video.utils.backup.BackupCategory> selected=java.util.EnumSet.noneOf(com.archos.mediacenter.video.utils.backup.BackupCategory.class);
        var preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        for(var category:com.archos.mediacenter.video.utils.backup.BackupCategory.values()) {
            boolean fallback=category==com.archos.mediacenter.video.utils.backup.BackupCategory.HISTORY?preferences.getBoolean("supernova_backup_history",true):
                category==com.archos.mediacenter.video.utils.backup.BackupCategory.POSITIONS?preferences.getBoolean("supernova_backup_resume",true):category.defaultIncluded;
            if(preferences.getBoolean(category.preference(),fallback))selected.add(category);
        }
        return com.archos.mediacenter.video.utils.backup.LogicalBackupExporter.preparePrivate(this,selected,()->Thread.currentThread().isInterrupted()).getAbsolutePath();
    }

    private void importMediaLibrary(String path) throws Exception {
        if(path==null||path.isEmpty()) throw new IOException("Select a backup file");
        File stage;
        if(path.startsWith("supernova-stage:")){
            String token=path.substring("supernova-stage:".length());if(!token.matches("restore-[a-f0-9-]{36}"))throw new IOException("Invalid restore reference");stage=new File(getCacheDir(),token);SafeBackup.validateStage(this,stage);
        }else{
            java.io.InputStream input=path.startsWith("content:")?getContentResolver().openInputStream(android.net.Uri.parse(path)):new FileInputStream(path);
            if(input==null)throw new IOException("Backup unavailable");try(java.io.InputStream in=input){stage=SafeBackup.stage(this,in);}
        }
        try {
            // SafeBackup prepares its durable private originals before replacement.
            // Never create a new whole-library1.0 archive as a side effect of import.
            SafeBackup.restore(this,stage);
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("preview_restore_artwork_pending",true).commit();
        } finally { SafeBackup.remove(stage); }
    }

    private void cleanupExistingData() {
        if (log.isDebugEnabled()) log.debug("cleanupExistingData: DELETING all existing media library data");

        // Close database helper and connections before deleting files to release locks and files
        if (log.isDebugEnabled()) log.debug("cleanupExistingData: closing database connections");
        VideoDb.getHolder(this).close();
        ShortcutDbAdapter.VIDEO.close();

        // Delete media database files
        File dbFile = getDatabasePath(DATABASE_NAME);
        File dbWalFile = new File(dbFile.getAbsolutePath() + "-wal");
        File dbShmFile = new File(dbFile.getAbsolutePath() + "-shm");

        if (dbFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", dbFile.getAbsolutePath());
            dbFile.delete();
        }
        if (dbWalFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", dbWalFile.getAbsolutePath());
            dbWalFile.delete();
        }
        if (dbShmFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", dbShmFile.getAbsolutePath());
            dbShmFile.delete();
        }

        // Delete credentials database files
        File credentialsDbFile = getDatabasePath(CREDENTIALS_DB_NAME);
        File credentialsDbWalFile = new File(credentialsDbFile.getAbsolutePath() + "-wal");
        File credentialsDbShmFile = new File(credentialsDbFile.getAbsolutePath() + "-shm");

        if (credentialsDbFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", credentialsDbFile.getAbsolutePath());
            credentialsDbFile.delete();
        }
        if (credentialsDbWalFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", credentialsDbWalFile.getAbsolutePath());
            credentialsDbWalFile.delete();
        }
        if (credentialsDbShmFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", credentialsDbShmFile.getAbsolutePath());
            credentialsDbShmFile.delete();
        }

        // Delete shortcuts database files
        File shortcutsDbFile = getDatabasePath(SHORTCUTS_DB_NAME);
        File shortcutsDbWalFile = new File(shortcutsDbFile.getAbsolutePath() + "-wal");
        File shortcutsDbShmFile = new File(shortcutsDbFile.getAbsolutePath() + "-shm");

        if (shortcutsDbFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", shortcutsDbFile.getAbsolutePath());
            shortcutsDbFile.delete();
        }
        if (shortcutsDbWalFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", shortcutsDbWalFile.getAbsolutePath());
            shortcutsDbWalFile.delete();
        }
        if (shortcutsDbShmFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", shortcutsDbShmFile.getAbsolutePath());
            shortcutsDbShmFile.delete();
        }

        // Delete shortcuts2 database files
        File shortcuts2DbFile = getDatabasePath(SHORTCUTS2_DB_NAME);
        File shortcuts2DbWalFile = new File(shortcuts2DbFile.getAbsolutePath() + "-wal");
        File shortcuts2DbShmFile = new File(shortcuts2DbFile.getAbsolutePath() + "-shm");

        if (shortcuts2DbFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", shortcuts2DbFile.getAbsolutePath());
            shortcuts2DbFile.delete();
        }
        if (shortcuts2DbWalFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", shortcuts2DbWalFile.getAbsolutePath());
            shortcuts2DbWalFile.delete();
        }
        if (shortcuts2DbShmFile.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting {}", shortcuts2DbShmFile.getAbsolutePath());
            shortcuts2DbShmFile.delete();
        }

        // Delete all poster files
        File posterDir = MediaScraper.getPosterDirectory(this);
        if (posterDir.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting all files in {}", posterDir.getAbsolutePath());
            deleteDirectoryContents(posterDir);
        }

        // Delete all backdrop files
        File backdropDir = MediaScraper.getBackdropDirectory(this);
        if (backdropDir.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting all files in {}", backdropDir.getAbsolutePath());
            deleteDirectoryContents(backdropDir);
        }

        // Delete all picture files
        File pictureDir = MediaScraper.getPictureDirectory(this);
        if (pictureDir.exists()) {
            if (log.isDebugEnabled()) log.debug("cleanupExistingData: deleting all files in {}", pictureDir.getAbsolutePath());
            deleteDirectoryContents(pictureDir);
        }

        if (log.isDebugEnabled()) log.debug("cleanupExistingData: cleanup completed");
    }

    private void deleteDirectoryContents(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectoryContents(file);
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    private void showToast(final String message) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                Toast.makeText(MediaLibraryBackupService.this, message, Toast.LENGTH_LONG).show()
        );
    }

    private void restartApplication() {
        if (log.isDebugEnabled()) log.debug("restartApplication: restarting app after import");

        try {
            // Get the main activity intent
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Schedule app restart after a short delay
                int pendingIntentFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE;

                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        pendingIntentFlags
                );

                android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(
                            android.app.AlarmManager.RTC,
                            System.currentTimeMillis() + 500, // 500ms delay
                            pendingIntent
                    );
                }

                if (log.isDebugEnabled()) log.debug("restartApplication: app restart scheduled, exiting process");

                // Exit the current process
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("restartApplication: failed to restart app", e);
        }
    }

    @Override
    public void onDestroy() {
        if (log.isDebugEnabled()) log.debug("onDestroy");
        super.onDestroy();
    }
}
