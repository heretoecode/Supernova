package com.archos.mediacenter.video.utils;

import android.content.Context;
import android.util.AtomicFile;
import androidx.preference.PreferenceManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.json.*;

/** Durable rollback intent, written before any live database/pref replacement. */
public final class RestoreJournal {
    private static AtomicFile file(Context c){return new AtomicFile(new File(c.getFilesDir(),"pending-restore.json"));}
    static void write(Context c,JSONObject state)throws Exception{
        AtomicFile journal=file(c);FileOutputStream out=null;
        try {out=journal.startWrite();out.write(state.toString().getBytes(StandardCharsets.UTF_8));journal.finishWrite(out);}
        catch(Exception failure){if(out!=null)journal.failWrite(out);throw failure;}
    }
    static JSONObject prepare(Context c,JSONArray files,String settings,JSONObject named)throws Exception{
        JSONObject state=new JSONObject().put("files",files).put("settings",settings).put("named",named).put("committed",false);
        write(c,state);return state;
    }
    static void commit(Context c,JSONObject state)throws Exception{state.put("committed",true);write(c,state);}
    /** Called before ContentProviders open databases. Recovery is idempotent across another interruption. */
    public static void recover(Context c)throws Exception{
        AtomicFile journal=file(c);JSONObject state;
        try(InputStream in=journal.openRead();ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
            byte[] buffer=new byte[8192];int n;while((n=in.read(buffer))!=-1){if(bytes.size()+n>16777216)throw new IOException("Restore journal exceeds limit");bytes.write(buffer,0,n);}
            state=new JSONObject(bytes.toString("UTF-8"));
        }catch(FileNotFoundException absent){return;}
        boolean committed=state.getBoolean("committed");JSONArray files=state.getJSONArray("files");
        if(!committed){
            for(int i=files.length()-1;i>=0;i--){JSONObject entry=files.getJSONObject(i);File live=new File(entry.getString("live")),old=new File(entry.getString("old"));
                if(old.exists()){erase(live);if(!old.renameTo(live))throw new IOException("Cannot recover interrupted restore");}
                else if(!entry.getBoolean("existed"))erase(live);
            }
            if(!SettingsBackup.decode(PreferenceManager.getDefaultSharedPreferences(c),state.getString("settings")).commit())throw new IOException("Cannot recover preferences");
            JSONObject named=state.getJSONObject("named");for(Iterator<String> keys=named.keys();keys.hasNext();){String key=keys.next();if(!SettingsBackup.decode(c.getSharedPreferences(key,0),named.getString(key)).commit())throw new IOException("Cannot recover account preferences");}
        }
        for(int i=0;i<files.length();i++){JSONObject entry=files.getJSONObject(i);erase(new File(entry.getString("fresh")));erase(new File(entry.getString("old")));}
        journal.delete();
    }
    private static void erase(File file)throws IOException{
        if(file.isDirectory()){File[] children=file.listFiles();if(children==null)throw new IOException("Cannot inspect restore recovery files");for(File child:children)erase(child);}
        if(file.exists()&&!file.delete())throw new IOException("Cannot remove restore recovery file");
    }
    private RestoreJournal(){}
}
