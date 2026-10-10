package com.archos.mediacenter.video.utils;
import android.net.Uri;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.io.*;
import java.util.*;

/** Explicit known-choice allowlist plus value sanitisation. Recovery journals use raw private state. */
public final class BackupPrivacy {
    private static final Set<String> CHOICES=new HashSet<>(Arrays.asList(
            "about_category",
            "account",
            "account_status",
            "activate_tv_switch",
            "allow_3rd_party_player",
            "always_leanback_on_tv_key",
            "app_theme",
            "app_updated",
            "artwork_failures_retained",
            "artwork_requests_retained",
            "audio_decoder_choice",
            "audio_interface_choice",
            "audio_speed_audiotrack",
            "auto_rescan_on_app_restart",
            "brightness_saved",
            "category_leanback_user_interface",
            "category_user_interface",
            "channels",
            "charts",
            "clean",
            "code",
            "codepage",
            "collectionWatched",
            "committed",
            "current_versionCode",
            "current_versionName",
            "dec_choice",
            "disable_downmix",
            "display_resume_box",
            "dolby_vision_mode",
            "duration_status",
            "elapsed_ms",
            "enable_adult_scrap_key",
            "enable_android_frame_timing",
            "enable_auto_scrap_key",
            "enable_cutout_both_sidesx",
            "enable_cutout_mode_short_edges",
            "enable_downmix_androidtv",
            "enable_dynamic_audio_delay",
            "enable_sponsor",
            "enable_tv_refreshrate_switch_mode",
            "ended_elapsed_ms",
            "event_count",
            "existed",
            "favAudioLang",
            "favScraperLang",
            "favSubLang",
            "file_type",
            "filter_string",
            "first_sequence",
            "first_utc_ms",
            "folder",
            "force_audio_passthrough",
            "force_audio_passthrough_multiple",
            "force_passthrough",
            "force_software_decoding",
            "format",
            "fresh",
            "group",
            "hide_controls_on_pause",
            "hide_trailer_row",
            "hide_watched",
            "id",
            "ids",
            "isRunning",
            "key",
            "languages_list",
            "last_operation",
            "last_sequence",
            "last_utc_ms",
            "lastintent",
            "latest_manual_category",
            "latest_manual_reference",
            "latest_manual_time",
            "live",
            "mDisplayWorkgroupSeparator",
            "mSelectedFolder",
            "make_time_negative",
            "mark_slate_applied",
            "name",
            "netshare_category",
            "network_bookmarks",
            "nova_version",
            "oauth_token",
            "old",
            "parent_id",
            "parent_operation_id",
            "parser_sync_mode",
            "path",
            "pid",
            "playback_speed",
            "player_projector_mode_key",
            "player_spatialization_enabled",
            "poster_path",
            "pref_create_remote_thumbs",
            "pref_network_prefer_vpn",
            "pref_smb_disable_mdns_discovery",
            "pref_smb_disable_tcp_discovery",
            "pref_smb_disable_udp_discovery",
            "pref_smb_resolv",
            "pref_smbj",
            "pref_smbv2",
            "pref_sshj",
            "prefer_original_audio_track",
            "preference_display_all_files",
            "preferences_about",
            "preferences_animes_sort_order",
            "preferences_category_advanced_video",
            "preferences_category_video",
            "preferences_movie_sort_order",
            "preferences_torrent_blocklist",
            "preferences_torrent_path",
            "preferences_tv_show_sort_order",
            "preferences_version",
            "preferences_video_advanced_quit",
            "preferences_video_licences",
            "preferences_video_os",
            "preferences_video_tmdb",
            "preferences_video_trakt",
            "preview_accent41",
            "preview_featured_popular",
            "preview_featured_recent",
            "preview_featured_trending",
            "preview_home_rows41",
            "preview_local_scan_result",
            "preview_restore_artwork_pending",
            "preview_scan_frequency",
            "preview_scan_result",
            "preview_scan_result_time",
            "preview_search_key",
            "preview_search_query",
            "preview_search_results_focused",
            "preview_search_selected",
            "previous_versionCode",
            "previous_versionName",
            "process",
            "providerId",
            "provider_id",
            "provider_name",
            "remember_library_views",
            "rescan_storage",
            "reset_brightness_on_start",
            "reset_last_played_row",
            "role",
            "save_audio_speed_setting_pref_key",
            "scans_requested_retained",
            "scrape_from_database_key",
            "scraper_category",
            "season_number",
            "separate_anime_movie_show",
            "settings",
            "share_folders",
            "showWatched",
            "show_all_animes_row",
            "show_all_movies_row",
            "show_all_tv_shows_row",
            "show_by_rating",
            "show_documentaries",
            "show_last_added_row",
            "show_last_played_row",
            "show_watching_up_next_row",
            "size",
            "smart_recently_rows",
            "sort",
            "sort_ignore_articles",
            "started_elapsed_ms",
            "status",
            "stereo_mode",
            "stream_buffer_size",
            "stream_max_iframe_size",
            "streaming_catalogue_",
            "streaming_category",
            "streaming_enabled",
            "streaming_country",
            "subcategoryName",
            "subtitles_credentials",
            "subtitles_hide_default",
            "supernova_library_policy_initialized",
            "supernova_library_roots",
            "supernova_library_policy_initialized",
            "supernova_library_exclusions",
            "supernova_browser_grid",
            "supernova_browser_sort",
            "supernova_onboarding_complete", "supernova_history_migrated", "supernova_history_cleared", "supernova_track_viewing_history", "supernova_save_resume", "supernova_backup_history", "supernova_backup_resume", "supernova_putio_enabled", "supernova_integration_trakt", "supernova_integration_opensubtitles",
            "text_asset",
            "time",
            "title",
            "total",
            "trakt_category",
            "trakt_force_pull",
            "trakt_force_push",
            "trakt_getfull",
            "trakt_last_sync",
            "trakt_live_scrobbling",
            "trakt_signin",
            "trakt_sync_resume",
            "trakt_wipe",
            "try_new_ui",
            "type",
            "ui_lang",
            "ui_zoom",
            "uimode",
            "uimode_leanback",
            "used",
            "user_id",
            "user_paused_video",
            "username",
            "value",
            "version"));
    private static final Set<String> NAMED=new HashSet<>(Arrays.asList("putio-library-selection-v1","provider-discovery-ownership-v1","supernova_library_health_v1"));
    public static boolean named(String name){return NAMED.contains(name);}
    public static boolean namedSetting(String name,String key){
        if(secretKey(key)||!key.equals(cleanText(key)))return false;
        if("putio-library-selection-v1".equals(name))return key.equals("account")||key.matches("(name|kind|sync|status):[0-9]+:[0-9]+")||key.matches("root:[0-9]+:[0-9]+:(source|path)");
        if("provider-discovery-ownership-v1".equals(name))return key.matches("putio(?:-retired)?:[0-9]+:[a-f0-9]+");
        if("supernova_library_health_v1".equals(name))return key.matches("(media|incorrect):[0-9]+")||key.startsWith("offline:")||key.startsWith("problem:");
        return false;
    }
    public static boolean secretKey(String key){return key.toLowerCase(Locale.ROOT).matches(".*(password|passwd|credential|token|cookie|authorization|secret|session|api.?key|access.?key).* ".trim());}
    public static boolean setting(String key){
        if(!key.equals(cleanText(key))||secretKey(key)||Arrays.asList("try_new_ui","uimode","uimode_leanback","theme","username","user_id","account","account_status","trakt_signin","network_bookmarks").contains(key))return false;
        return CHOICES.contains(key)||key.matches("streaming_(providers|preferred)_[A-Z]{2}")||key.startsWith("preview_columns_")||key.startsWith("preview_source_kind:")||key.startsWith("preview_cw_dismiss:")||key.startsWith("preview_version_choice:")||key.startsWith("preview_library_")||key.startsWith("supernova_custom_library_")||key.startsWith("preview_journey41:")||key.startsWith("supernova_viewed_")||key.startsWith("supernova_segment_");
    }
    public static Object clean(Object value){
        if(value instanceof Set){Set<String> result=new LinkedHashSet<>();for(Object item:(Set<?>)value)result.add((String)clean(String.valueOf(item)));return result;}
        if(!(value instanceof String))return value;
        String text=(String)value,safe=cleanText(text);
        try {
            Object json=text.trim().startsWith("{")?new org.json.JSONObject(safe):text.trim().startsWith("[")?new org.json.JSONArray(safe):null;
            if(json!=null&&cleanJson(json))return json.toString();
        }catch(org.json.JSONException plainText){/* Ordinary labels are not JSON documents. */}
        return safe;
    }
    private static boolean cleanJson(Object json)throws org.json.JSONException {
        boolean changed=false;
        if(json instanceof org.json.JSONObject){org.json.JSONObject object=(org.json.JSONObject)json;List<String> remove=new ArrayList<>();
            for(Iterator<String> keys=object.keys();keys.hasNext();){String key=keys.next();if(secretKey(key)||!key.equals(cleanText(key)))remove.add(key);else changed|=cleanJson(object.get(key));}
            for(String key:remove)object.remove(key);changed|=!remove.isEmpty();
        }else if(json instanceof org.json.JSONArray){org.json.JSONArray array=(org.json.JSONArray)json;for(int n=0;n<array.length();n++)changed|=cleanJson(array.get(n));}
        return changed;
    }
    public static String cleanText(String value){
        if(value==null)return null;
        java.util.regex.Matcher urls=java.util.regex.Pattern.compile("[A-Za-z][A-Za-z0-9+.-]*://[^\\s\"<>]+").matcher(value);
        StringBuffer result=new StringBuffer();while(urls.find()){
            Uri uri=Uri.parse(urls.group());Uri.Builder safe=uri.buildUpon().clearQuery().fragment(null);
            if(uri.getHost()!=null)safe.encodedAuthority(uri.getHost()+(uri.getPort()<0?"":":"+uri.getPort()));
            urls.appendReplacement(result,java.util.regex.Matcher.quoteReplacement(safe.build().toString()));
        }urls.appendTail(result);return result.toString();
    }
    /** Scrub a private copy and VACUUM it so removed bytes cannot survive in SQLite free pages. */
    public static File database(File original,File folder)throws IOException{
        File copy=new File(folder,original.getName()+"-"+UUID.randomUUID());SafeBackup.copy(original,copy);
        try(SQLiteDatabase db=SQLiteDatabase.openDatabase(copy.getPath(),null,SQLiteDatabase.OPEN_READWRITE)){
            List<String> tables=new ArrayList<>();try(Cursor cursor=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",null)){while(cursor.moveToNext())tables.add(cursor.getString(0));}
            db.beginTransaction();try{
                for(String table:tables){String tableName=quote(table);List<String> columns=new ArrayList<>();
                    try(Cursor info=db.rawQuery("PRAGMA table_info("+tableName+")",null)){while(info.moveToNext())columns.add(info.getString(1));}
                    for(String column:columns){String col=quote(column);
                        if(secretKey(column)){db.execSQL("UPDATE "+tableName+" SET "+col+"=NULL");continue;}
                        try(Cursor rows=db.rawQuery("SELECT rowid,"+col+" FROM "+tableName+" WHERE typeof("+col+")='text' AND instr("+col+",'://')>0",null)){
                            while(rows.moveToNext()){String text=rows.getString(1),safe=cleanText(text);if(!text.equals(safe)){ContentValues values=new ContentValues();values.put(column,safe);db.update(table,values,"rowid=?",new String[]{Long.toString(rows.getLong(0))});}}
                        }
                    }
                }db.setTransactionSuccessful();
            }finally{db.endTransaction();}db.execSQL("VACUUM");
        }catch(RuntimeException error){copy.delete();throw new IOException("Cannot create secret-free database snapshot",error);}return copy;
    }
    public static void validateDatabase(File file)throws IOException{
        try(SQLiteDatabase db=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)){
            List<String> tables=new ArrayList<>();try(Cursor c=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",null)){while(c.moveToNext())tables.add(c.getString(0));}
            for(String table:tables){List<String> columns=new ArrayList<>();try(Cursor c=db.rawQuery("PRAGMA table_info("+quote(table)+")",null)){while(c.moveToNext())columns.add(c.getString(1));}
                for(String column:columns){String col=quote(column);if(secretKey(column)){try(Cursor c=db.rawQuery("SELECT 1 FROM "+quote(table)+" WHERE "+col+" IS NOT NULL AND "+col+"!='' LIMIT 1",null)){if(c.moveToFirst())throw new IOException("Backup contains authentication data");}}
                    else try(Cursor c=db.rawQuery("SELECT "+col+" FROM "+quote(table)+" WHERE typeof("+col+")='text' AND instr("+col+",'://')>0",null)){while(c.moveToNext()){String text=c.getString(0);if(!text.equals(cleanText(text)))throw new IOException("Backup contains a private authenticated address");}}
                }
            }
        }catch(RuntimeException error){throw new IOException("Backup database cannot be validated",error);}
    }
    public static String selectedSettings(android.content.Context context,String json)throws org.json.JSONException{org.json.JSONObject data=new org.json.JSONObject(json);android.content.SharedPreferences p=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);boolean history=p.getBoolean("supernova_backup_history",true),resume=p.getBoolean("supernova_backup_resume",true);List<String> remove=new ArrayList<>();for(Iterator<String> keys=data.keys();keys.hasNext();){String key=keys.next();if(!history&&(key.equals("supernova_history_cleared")||key.startsWith("preview_journey41:")||key.startsWith("supernova_viewed_")||key.equals("PREFERENCE_LAST_TIME_VIDEO_PLAYED_UTC")||key.startsWith("preview_cw_dismiss:")))remove.add(key);}for(String key:remove)data.remove(key);return data.toString();}
    public static void applyViewingSelection(android.content.Context context,File copy)throws IOException{android.content.SharedPreferences p=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);boolean history=p.getBoolean("supernova_backup_history",true),resume=p.getBoolean("supernova_backup_resume",true);if(history&&resume)return;try(SQLiteDatabase db=SQLiteDatabase.openDatabase(copy.getPath(),null,SQLiteDatabase.OPEN_READWRITE)){List<String> tables=new ArrayList<>();try(Cursor rows=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",null)){while(rows.moveToNext())tables.add(rows.getString(0));}for(String table:tables){try(Cursor columns=db.rawQuery("PRAGMA table_info("+quote(table)+")",null)){while(columns.moveToNext()){String name=columns.getString(1);if(!history&&(name.equals("Archos_traktSeen")||name.equals("Archos_lastTimePlayed"))||!resume&&(name.equals("bookmark")||name.equals("Archos_bookmark")||name.equals("Archos_traktResume")))db.execSQL("UPDATE "+quote(table)+" SET "+quote(name)+"=0");else if(!history&&name.equals("bookmark"))db.execSQL("UPDATE "+quote(table)+" SET "+quote(name)+"=0 WHERE "+quote(name)+"<0");}}}db.execSQL("VACUUM");}catch(RuntimeException failure){throw new IOException("Could not apply viewing-data backup choices",failure);}}
    private static String quote(String identifier){return "\""+identifier.replace("\"","\"\"")+"\"";}
    private BackupPrivacy(){}
}
