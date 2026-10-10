package com.archos.mediacenter.video.utils;
import com.archos.mediacenter.video.BuildConfig;
import java.io.IOException;
import org.json.*;
public final class BackupFormat {
    private static final String[] CATEGORIES={"library","viewing history","settings","sources","custom rows","personal artwork"};
    public static String manifest()throws IOException{
        try{return new JSONObject().put("format","Supernova").put("formatVersion","1.0").put("applicationVersion",BuildConfig.VERSION_NAME).put("createdUtc",System.currentTimeMillis()).put("credentialsIncluded",false).put("categories",new JSONArray(CATEGORIES)).toString(2);}catch(JSONException error){throw new IOException(error);}
    }
    public static void validate(String json)throws IOException{
        try{JSONObject root=new JSONObject(json);if(!"Supernova".equals(root.getString("format"))||!"1.0".equals(root.getString("formatVersion"))||root.getBoolean("credentialsIncluded"))throw new IOException("Unsupported or unsafe backup format");if(!root.getString("applicationVersion").matches("0\\.[1-9][0-9]*")||root.getLong("createdUtc")<=0)throw new IOException("Invalid backup origin");JSONArray categories=root.getJSONArray("categories");java.util.Set<String> actual=new java.util.HashSet<>();for(int i=0;i<categories.length();i++)if(!actual.add(categories.getString(i)))throw new IOException("Duplicate backup category");if(!actual.equals(new java.util.HashSet<>(java.util.Arrays.asList(CATEGORIES))))throw new IOException("Unsupported backup categories");}catch(JSONException error){throw new IOException("Invalid backup manifest",error);}
    }
    public static String instructions(){return "SUPERNOVA BACKUP FORMAT 1.0\nCreated by Supernova "+BuildConfig.VERSION_NAME+"\n\n1. Install a compatible Supernova version.\n2. Open Settings > Advanced > Restore Backup and select this ZIP in Android Files.\n3. Review the backup information, then choose Restore. Existing library and supported settings are replaced together.\n4. Grant storage permissions again where required. Reconnect protected network shares, Trakt, put.io and OpenSubtitles. Credentials, passwords, tokens and sessions are excluded.\n5. Missing storage does not delete your library or viewing progress. Artwork is fetched in the background using retained provider IDs.\n\nThis archive is unencrypted and contains private library, path and viewing-history information. Keep it private.\nLegacy NOVA and pre-format-1.0 backups are unsupported.\n";}
    private BackupFormat(){}
}
