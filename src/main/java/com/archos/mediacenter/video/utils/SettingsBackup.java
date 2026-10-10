package com.archos.mediacenter.video.utils;
import android.content.SharedPreferences;
import org.json.*;
import java.util.*;
public final class SettingsBackup {
    public static String encodePortable(SharedPreferences prefs,boolean named) throws JSONException {return encode(prefs,true,named);}
    public static String encode(SharedPreferences prefs) throws JSONException {return encode(prefs,false,false);}
    private static String encode(SharedPreferences prefs,boolean portable,boolean named) throws JSONException {
        JSONObject root = new JSONObject();
        for (Map.Entry<String,?> entry : prefs.getAll().entrySet()) {
            if(portable&&((named&&BackupPrivacy.secretKey(entry.getKey()))||(!named&&!BackupPrivacy.setting(entry.getKey()))))continue;
            Object v = portable?BackupPrivacy.clean(entry.getValue()):entry.getValue(); JSONObject item = new JSONObject();
            String type = v instanceof Boolean ? "boolean" : v instanceof Integer ? "int" : v instanceof Long ? "long"
                : v instanceof Float ? "float" : v instanceof Set ? "set" : "string";
            item.put("type", type);
            item.put("value", v instanceof Set ? new JSONArray((Set<?>)v) : v);
            root.put(entry.getKey(),item);
        }
        return root.toString();
    }
    public static SharedPreferences.Editor decodePortable(SharedPreferences prefs,String json,boolean named)throws JSONException{
        JSONObject root=new JSONObject(json);for(Iterator<String> keys=root.keys();keys.hasNext();){String key=keys.next();if(named?BackupPrivacy.secretKey(key):!BackupPrivacy.setting(key))throw new JSONException("Unsupported portable setting");JSONObject item=root.getJSONObject(key);Object value=item.get("value");
            if(value instanceof String&&!value.equals(BackupPrivacy.cleanText((String)value)))throw new JSONException("Secret-bearing location rejected");
            if(value instanceof JSONArray){JSONArray array=(JSONArray)value;for(int i=0;i<array.length();i++)if(!array.getString(i).equals(BackupPrivacy.cleanText(array.getString(i))))throw new JSONException("Secret-bearing location rejected");}
        }return decode(prefs,json);
    }
    public static SharedPreferences.Editor decode(SharedPreferences prefs, String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        SharedPreferences.Editor editor = prefs.edit().clear();
        for (Iterator<String> it = root.keys(); it.hasNext();) {
            String key = it.next(); JSONObject item = root.getJSONObject(key);
            switch (item.getString("type")) {
                case "boolean": editor.putBoolean(key,item.getBoolean("value")); break;
                case "int": editor.putInt(key,item.getInt("value")); break;
                case "long": editor.putLong(key,item.getLong("value")); break;
                case "float": editor.putFloat(key,(float)item.getDouble("value")); break;
                case "string": editor.putString(key,item.getString("value")); break;
                case "set":
                    Set<String> values = new HashSet<>(); JSONArray array = item.getJSONArray("value");
                    for(int i=0;i<array.length();i++) values.add(array.getString(i));
                    editor.putStringSet(key,values); break;
                default: throw new JSONException("Unsupported setting type");
            }
        }
        return editor;
    }
    private SettingsBackup() {}
}
