# 07-diagnostics-backup — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-a6617d04e841 · DiagnosticArchive.linkedSummary

[Source line 130](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticArchive.java#L130)

```java
for(String field:new String[]{"app_session_summaries","playback_session_summaries","operation_summaries"}){
```

## SITE-53cf7bcbeb71 · DiagnosticFlightRecorder.class scope

[Source line 15](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFlightRecorder.java#L15)

```java
String text(){StringBuilder result=new StringBuilder();for(Record row:records)result.append(row.line);return result.toString();}
```

## SITE-960fbb9ebbf7 · DiagnosticFlightRecorder.snapshot

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFlightRecorder.java#L20)

```java
public String snapshot(long elapsed){return capture(elapsed).text();}
```

## SITE-4a272a083de9 · Diagnostics.chooseDisable

[Source line 217](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L217)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.choose(c,"Turn diagnostic logging off?",new String[]{"Cancel","Keep saved logs","Delete saved logs"},0,n->{if(n==0)return;disable(c,n==2);changed.accept(false);});
```

## SITE-8cfd0386184f · Diagnostics.freeze

[Source line 328](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L328)

```java
String retained=flight.text();
```

## SITE-15940e0104c2 · Diagnostics.reportProblem

[Source line 380](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L380)

```java
if(!enabled){com.archos.mediacenter.video.leanback.PreviewDialog.read(c,"Diagnostic logging is off","Enable Diagnostic Logging in Advanced settings before recording a problem.");return;}
```

## SITE-d1187049ed0d · Diagnostics.reportProblem

[Source line 381](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L381)

```java
String[] categories={"Playback","UI","Network","Other"};
```

## SITE-1498d02e39c4 · Diagnostics.reportProblem

[Source line 382](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L382)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.choose(c,"Report a Problem",categories,-1,n->{
```

## SITE-cc46eb5cf2dc · Diagnostics.showDigest

[Source line 390](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L390)

```java
public static void showDigest(Context c){WORK.execute(()->{String digest=summary();new android.os.Handler(android.os.Looper.getMainLooper()).post(()->{if(c instanceof Activity&&(((Activity)c).isFinishing()||((Activity)c).isDestroyed()))return;com.archos.mediacenter.video.leanback.PreviewDialog.read(c,"Issues Digest",digest);});});}
```

## SITE-e7b785fda7c3 · Diagnostics.showLatestReference

[Source line 394](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L394)

```java
if(id.isEmpty()){com.archos.mediacenter.video.leanback.PreviewDialog.read(c,"Latest Reference","No problem has been recorded yet.");return;}
```

## SITE-24fb857f24ff · Diagnostics.showReference

[Source line 399](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L399)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.read(c,title,"The saved reference is unavailable. Record a new problem to create a reference.");return;
```

## SITE-a92fa4e58843 · Diagnostics.showReference

[Source line 406](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L406)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.read(c,title,confirmation,qr);
```

## SITE-f6f4df2ce2e6 · BackupFormat.class scope

[Source line 6](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/BackupFormat.java#L6)

```java
private static final String[] CATEGORIES={"library","viewing history","settings","sources","custom rows","personal artwork"};
```

## SITE-ef251d6380ad · BackupPrivacy.database

[Source line 273](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/BackupPrivacy.java#L273)

```java
while(rows.moveToNext()){String text=rows.getString(1),safe=cleanText(text);if(!text.equals(safe)){ContentValues values=new ContentValues();values.put(column,safe);db.update(table,values,"rowid=?",new String[]{Long.toString(rows.getLong(0))});}}
```

## SITE-a8d473896bfe · BackupReview.show

[Source line 14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/BackupReview.java#L14)

```java
androidx.appcompat.app.AlertDialog checking=new androidx.appcompat.app.AlertDialog.Builder(activity).setTitle("Checking backup").setMessage("Validating the archive before making any changes…").setCancelable(false).create();checking.show();
```

## SITE-b98141af5d0e · BackupReview.show

[Source line 16](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/BackupReview.java#L16)

```java
try(Cursor name=activity.getContentResolver().query(source,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null)){if(name!=null&&name.moveToFirst())filename=name.getString(0);}
```

## SITE-aae2742ef3ee · BackupReview.show

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/BackupReview.java#L20)

```java
activity.runOnUiThread(()->{checking.dismiss();if(activity.isFinishing()){SafeBackup.remove(reviewed);return;}androidx.appcompat.app.AlertDialog confirm=new androidx.appcompat.app.AlertDialog.Builder(activity).setTitle("Restore Supernova backup?").setMessage(message).setNegativeButton(android.R.string.cancel,(dialog,which)->SafeBackup.remove(reviewed)).setPositiveButton("Restore",(dialog,which)->restore.accept("supernova-stage:"+reviewed.getName())).create();confirm.setOnCancelListener(dialog->SafeBackup.remove(reviewed));confirm.show();});
```

Resolved English: `{"cancel": "[external/unresolved]"}`

## SITE-f5b07aac5fe8 · BackupReview.show

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/BackupReview.java#L21)

```java
}catch(Exception error){if(staged!=null)SafeBackup.remove(staged);String reason=error.getMessage();activity.runOnUiThread(()->{checking.dismiss();if(!activity.isFinishing())new androidx.appcompat.app.AlertDialog.Builder(activity).setTitle("Backup cannot be restored").setMessage(reason==null?"The archive is invalid or incompatible. Your library is unchanged.":reason+"\n\nYour library is unchanged.").setPositiveButton(android.R.string.ok,null).show();});}},"SupernovaBackupValidation");worker.setDaemon(true);worker.start();
```

Resolved English: `{"ok": "[external/unresolved]"}`

## SITE-201d7199dc99 · MediaLibraryBackupService.startExport

[Source line 153](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java#L153)

```java
try(android.database.Cursor document=getContentResolver().query(published[0],new String[]{android.provider.OpenableColumns.DISPLAY_NAME},null,null,null)){if(document!=null&&document.moveToFirst()){String chosen=document.getString(0);if(chosen!=null)name=chosen.replaceFirst("\\.in-progress(?:\\.zip)?$","");}}
```

## SITE-01ec26de68f3 · MediaLibraryBackupService.startExport

[Source line 157](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java#L157)

```java
exportPath = renamed?name:"selected location (verified archive; this provider cannot rename the .in-progress file — rename it to .zip)";
```

## SITE-9baa70638b00 · MediaLibraryBackupService.flushDatabaseWAL

[Source line 336](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java#L336)

```java
for(String database:new String[]{DATABASE_NAME,CREDENTIALS_DB_NAME,SHORTCUTS_DB_NAME,SHORTCUTS2_DB_NAME,"putio-associations.db"}){File dbFile = getDatabasePath(database);
```

## SITE-b4417c8353ae · MigrationBackup.reproducibleFiles

[Source line 29](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MigrationBackup.java#L29)

```java
try(Cursor cursor=c.getContentResolver().query(type.baseUri,new String[]{type.largeFileColumn,type.largeUrlColumn,type.thumbFileColumn,type.thumbUrlColumn},null,null,null)){
```

## SITE-f4c68a1df874 · MigrationBackup.resumeArtwork

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MigrationBackup.java#L42)

```java
try(Cursor cursor=c.getContentResolver().query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,new String[]{id,type},id+" > 0",null,null)){
```

## SITE-d54989681f2f · MigrationBackup.downloadEpisodePicture

[Source line 70](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MigrationBackup.java#L70)

```java
c.getContentResolver().update(com.archos.mediaprovider.video.ScraperStore.Episode.URI.BASE,values,com.archos.mediaprovider.video.ScraperStore.Episode.ID+"=?",new String[]{Long.toString(tags.getId())});
```

## SITE-f7802f4efa4b · SafeBackup.validateStage

[Source line 78](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java#L78)

```java
for(String database:new String[]{"media.db","shortcuts_db","shortcuts2_db","putio-associations.db"}){File file=new File(stage,database);if(file.isFile())BackupPrivacy.validateDatabase(file);}
```

## SITE-ec8975af78fc · SafeBackup.restoreLocked

[Source line 127](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java#L127)

```java
for(String name:new String[]{"supernova-viewing-history.db","media.db","credentials_db","shortcuts_db","shortcuts2_db","putio-associations.db"}) {
```

## SITE-2e6fcf52827b · VerifiedBackup.validate

[Source line 22](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/VerifiedBackup.java#L22)

```java
for(String required:new String[]{"manifest.json","RESTORE_INSTRUCTIONS.txt","media.db","db_version.txt","settings.json"}){ZipEntry item=zip.getEntry(required);if(item==null||item.getSize()<=0)throw new IOException("Backup is missing "+required);}
```
