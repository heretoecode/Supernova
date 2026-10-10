package com.archos.mediacenter.video.utils;
import android.app.Activity;
import android.net.Uri;
import android.database.Cursor;
import android.provider.OpenableColumns;
import java.io.*;
import java.util.Date;
import java.util.function.Consumer;
import org.json.JSONObject;

/** Confirm the exact validated private snapshot, never an archive which can change after review. */
public final class BackupReview {
 public static void show(Activity activity,Uri source,Consumer<String> restore){
  androidx.appcompat.app.AlertDialog checking=new androidx.appcompat.app.AlertDialog.Builder(activity).setTitle("Checking backup").setMessage("Validating the archive before making any changes…").setCancelable(false).create();checking.show();
  Thread worker=new Thread(()->{File staged=null;String filename="Selected backup";try{
   try(Cursor name=activity.getContentResolver().query(source,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null)){if(name!=null&&name.moveToFirst())filename=name.getString(0);}
   try(InputStream input=activity.getContentResolver().openInputStream(source)){if(input==null)throw new IOException("Backup unavailable");staged=SafeBackup.stage(activity,input);}
   JSONObject manifest=new JSONObject(SafeBackup.read(new File(staged,"manifest.json")));File reviewed=staged;
   String message="File: "+filename+"\nCreated: "+java.text.DateFormat.getDateTimeInstance().format(new Date(manifest.getLong("createdUtc")))+"\nSupernova version: "+manifest.getString("applicationVersion")+"\nFormat: "+manifest.getString("formatVersion")+"\nIncluded: "+manifest.getJSONArray("categories").toString()+"\n\nThe archive passed format, database and privacy validation. Restore replaces the library and supported settings together. A recovery backup is kept. Storage permissions and protected accounts may need to be connected again.";
   activity.runOnUiThread(()->{checking.dismiss();if(activity.isFinishing()){SafeBackup.remove(reviewed);return;}androidx.appcompat.app.AlertDialog confirm=new androidx.appcompat.app.AlertDialog.Builder(activity).setTitle("Restore Supernova backup?").setMessage(message).setNegativeButton(android.R.string.cancel,(dialog,which)->SafeBackup.remove(reviewed)).setPositiveButton("Restore",(dialog,which)->restore.accept("supernova-stage:"+reviewed.getName())).create();confirm.setOnCancelListener(dialog->SafeBackup.remove(reviewed));confirm.show();});
  }catch(Exception error){if(staged!=null)SafeBackup.remove(staged);String reason=error.getMessage();activity.runOnUiThread(()->{checking.dismiss();if(!activity.isFinishing())new androidx.appcompat.app.AlertDialog.Builder(activity).setTitle("Backup cannot be restored").setMessage(reason==null?"The archive is invalid or incompatible. Your library is unchanged.":reason+"\n\nYour library is unchanged.").setPositiveButton(android.R.string.ok,null).show();});}},"SupernovaBackupValidation");worker.setDaemon(true);worker.start();
 }
 private BackupReview(){}
}
