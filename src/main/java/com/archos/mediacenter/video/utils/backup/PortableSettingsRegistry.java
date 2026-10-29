package com.archos.mediacenter.video.utils.backup;

import android.content.SharedPreferences;
import java.util.*;
import org.json.*;

/** Versioned positive settings ownership; no cache, arbitrary store, path or authentication export. */
public final class PortableSettingsRegistry {
    public static final int SCHEMA=1;
    public enum Category { APPEARANCE, AUDIO, DIAGNOSTICS, INTEGRATIONS, LIBRARY, NETWORK, PLAYBACK, STREAMING, SUBTITLES, VIDEO }
    public enum Type { BOOLEAN, INT, FLOAT, CHOICE, LANGUAGE, LANGUAGES, COUNTRY, PROVIDER, PROVIDERS, COLUMNS }
    public static final class Rule {
        public final Category category;public final Type type;
        private Rule(Category category,Type type){this.category=category;this.type=type;}
    }
    private static final Map<String,Rule> RULES=new LinkedHashMap<>();
    private static void rules(Category category,Type type,String keys){for(String key:keys.split(" ")){if(RULES.put(key,new Rule(category,type))!=null)throw new IllegalStateException("Overlapping category ownership");}}
    static {
        rules(Category.APPEARANCE,Type.INT,"preview_accent41 user_defined_density");
        rules(Category.APPEARANCE,Type.BOOLEAN,"enable_sponsor");
        rules(Category.APPEARANCE,Type.LANGUAGE,"ui_lang");
        rules(Category.APPEARANCE,Type.CHOICE,"app_theme");
        rules(Category.AUDIO,Type.BOOLEAN,"force_passthrough audio_speed_audiotrack enable_dynamic_audio_delay player_spatialization_enabled disable_downmix enable_downmix_androidtv prefer_original_audio_track");
        rules(Category.AUDIO,Type.CHOICE,"audio_interface_choice audio_decoder_choice force_audio_passthrough_multiple supernova_pcm_speed_engine");
        rules(Category.AUDIO,Type.LANGUAGE,"favAudioLang");
        rules(Category.AUDIO,Type.FLOAT,"save_audio_speed_setting_pref_key");
        rules(Category.AUDIO,Type.INT,"pref_audio_filt_int_key");
        rules(Category.AUDIO,Type.BOOLEAN,"pref_audio_filt_night_int_key");
        rules(Category.DIAGNOSTICS,Type.BOOLEAN,"supernova_diagnostic_logging");
        rules(Category.INTEGRATIONS,Type.BOOLEAN,"supernova_integration_trakt supernova_integration_opensubtitles supernova_segment_enabled introdb_enabled trakt_sync_collection trakt_live_scrobbling trakt_sync_resume");
        rules(Category.INTEGRATIONS,Type.CHOICE,"supernova_segment_intro supernova_segment_recap supernova_segment_preview supernova_segment_outro supernova_segment_credits");
        rules(Category.LIBRARY,Type.BOOLEAN,"hide_watched sort_ignore_articles remember_library_views enable_auto_scrap_key network_nfo_parse scrape_from_database_key nfo_export_auto enable_adult_scrap_key smart_recently_rows hide_trailer_row show_by_rating separate_anime_movie_show show_last_added_row show_last_played_row show_watching_up_next_row show_all_movies_row show_documentaries show_all_tv_shows_row show_all_animes_row");
        rules(Category.LIBRARY,Type.LANGUAGE,"favScraperLang");
        rules(Category.LIBRARY,Type.INT,"PREF_ALL_ANIMESHOWS_DISPLAY_MODE PREF_ALL_ANIMES_DISPLAY_MODE PREF_ALL_MOVIES_DISPLAY_MODE PREF_ALL_TVSHOWS_DISPLAY_MODE PREF_ANIME_COLLECTION_DISPLAY_MODE PREF_LISTING_DISPLAY_MODE PREF_MOVIE_COLLECTION_DISPLAY_MODE PREF_NON_SCRAPED_VIDEOS_DISPLAY_MODE");
        rules(Category.LIBRARY,Type.CHOICE,"preferences_movie_sort_order preferences_tv_show_sort_order preferences_animes_sort_order");
        rules(Category.NETWORK,Type.BOOLEAN,"pref_smbj pref_smbv2 pref_smb_resolv pref_smb_disable_tcp_discovery pref_smb_disable_udp_discovery pref_smb_disable_mdns_discovery pref_sshj pref_create_remote_thumbs network_bookmarks pref_network_prefer_vpn vpn_mobile auto_rescan_on_app_restart preference_display_all_files");
        rules(Category.NETWORK,Type.INT,"preview_scan_frequency auto_rescan_period auto_rescan_starting_time");
        rules(Category.PLAYBACK,Type.BOOLEAN,"display_resume_box hide_controls_on_pause make_time_negative playback_speed allow_3rd_party_player reset_brightness_on_start supernova_track_viewing_history supernova_save_resume PREF_PRIVATE_MODE preview_featured_recent preview_featured_trending preview_featured_popular");
        rules(Category.PLAYBACK,Type.INT,"brightness_saved pref_play_mode_int_key notifications_mode");
        rules(Category.PLAYBACK,Type.BOOLEAN,"pref_lock_rotation");
        rules(Category.STREAMING,Type.BOOLEAN,"streaming_enabled");
        rules(Category.STREAMING,Type.COUNTRY,"streaming_country");
        rules(Category.SUBTITLES,Type.BOOLEAN,"subtitles_hide_default subtitles_show_full_for_local_audio subtitle_use_margins pref_play_subtitle_outline_key subtitle_background supernova_subtitle_discovery supernova_subtitle_season_prefetch");
        rules(Category.SUBTITLES,Type.INT,"pref_play_subtitle_color_key pref_play_subtitle_size_key pref_play_subtitle_vpos_key subtitle_bg_opacity");
        rules(Category.SUBTITLES,Type.LANGUAGE,"favSubLang");
        rules(Category.SUBTITLES,Type.LANGUAGES,"languages_list");
        rules(Category.SUBTITLES,Type.CHOICE,"codepage");
        rules(Category.VIDEO,Type.BOOLEAN,"force_software_decoding enable_cutout_mode_short_edges enable_cutout_both_sidesx activate_tv_switch player_projector_mode_key preferences_advanced_video_enabled");
        rules(Category.VIDEO,Type.CHOICE,"dec_choice parser_sync_mode enable_tv_refreshrate_switch_mode dolby_vision_mode stream_buffer_size stream_max_iframe_size player_pref_format_key player_pref_auto_format_key");
    }
    private static final Map<String,Set<String>> CHOICES=new HashMap<>();
    private static void choices(String key,String values){CHOICES.put(key,new HashSet<>(Arrays.asList(values.split("\\|",-1))));}
    static {
        choices("supernova_pcm_speed_engine","2|3");choices("app_theme","blue|black|slate");choices("dec_choice","0|4|1|-1");
        choices("audio_interface_choice","0|1|2");choices("audio_decoder_choice","0|1|2");
        choices("force_audio_passthrough_multiple","0|1|2|3");choices("parser_sync_mode","0|1");
        choices("enable_tv_refreshrate_switch_mode","0|1|2");choices("dolby_vision_mode","auto|off|force");
        choices("codepage","0|1250|1251|1252|28606|1253|1254|1255|1256|1257|1258|874|936|950|932|949");
    }
    public static Map<String,Rule> exactRules(){return Collections.unmodifiableMap(RULES);}
    public static Rule rule(String key){
        Rule exact=RULES.get(key);if(exact!=null)return exact;
        if(key.matches("streaming_(?:providers|preferred)_[A-Z]{2}")&&countries().contains(key.substring(key.length()-2)))return new Rule(Category.STREAMING,key.startsWith("streaming_providers_")?Type.PROVIDERS:Type.PROVIDER);
        if(key.matches("preview_columns_(?:movies|tv|custom)_(?:order|shown|sort|ascending)"))return new Rule(Category.LIBRARY,key.endsWith("ascending")?Type.BOOLEAN:Type.COLUMNS);
        if(key.matches("preview_library_[12]_(?:sort|year)"))return new Rule(Category.LIBRARY,Type.INT);
        if(key.matches("preview_library_[12]_(?:ascending|list)"))return new Rule(Category.LIBRARY,Type.BOOLEAN);
        if(key.matches("preview_library_[12]_(?:genre|years|providers)")||key.matches("preview_custom_(?:genres|years|providers)"))return new Rule(Category.LIBRARY,Type.CHOICE);
        if(key.matches("supernova_browser_(?:sort|grid)"))return new Rule(Category.LIBRARY,key.endsWith("grid")?Type.BOOLEAN:Type.CHOICE);
        return null;
    }
    private static Set<String> countries(){return new HashSet<>(Arrays.asList(Locale.getISOCountries()));}
    private static boolean numeric(Object value){return value instanceof Integer||value instanceof Long;}
    private static boolean string(Object value,String pattern,int max){return value instanceof String&&((String)value).length()<=max&&((String)value).matches(pattern);}
    private static boolean valid(String key,Rule rule,Object value){
        switch(rule.type){
            case BOOLEAN:return value instanceof Boolean;
            case INT:return numeric(value)&&((Number)value).longValue()>=Integer.MIN_VALUE&&((Number)value).longValue()<=Integer.MAX_VALUE&&(!key.equals("user_defined_density")||Arrays.asList(-1L,160L,240L,320L,480L).contains(((Number)value).longValue()));
            case FLOAT:return value instanceof Number&&Float.isFinite(((Number)value).floatValue())&&((Number)value).doubleValue()>0&&((Number)value).doubleValue()<=4;
            case COUNTRY:return value instanceof String&&countries().contains(value);
            case PROVIDER:return string(value,"(?:|[1-9][0-9]{0,8})",9);
            case PROVIDERS:case LANGUAGES:
                if(!(value instanceof Set))return false;
                for(Object item:(Set<?>)value)if(!string(item,rule.type==Type.PROVIDERS?"[1-9][0-9]{0,8}":"(?:system|none|[A-Za-z]{2,3}(?:[-_][A-Za-z0-9]{2,8})*)",64))return false;
                return ((Set<?>)value).size()<=300;
            case LANGUAGE:return string(value,"(?:|system|none|auto|[A-Za-z]{2,3}(?:[-_][A-Za-z0-9]{2,8})*)",64);
            case COLUMNS:return string(value,"(?:|(?:TITLE|YEAR|SEASONS|EPISODES|DURATION|RESOLUTION|HDR|AUDIO|SIZE|AVERAGE|ADDED|MODIFIED|CODEC|BITRATE|CONTAINER|PATH)(?:,(?:TITLE|YEAR|SEASONS|EPISODES|DURATION|RESOLUTION|HDR|AUDIO|SIZE|AVERAGE|ADDED|MODIFIED|CODEC|BITRATE|CONTAINER|PATH))*)",500);
            case CHOICE:
                Set<String> choices=CHOICES.get(key);if(choices!=null)return value instanceof String&&choices.contains(value);
                if(key.startsWith("supernova_segment_"))return string(value,"NORMAL|PROMPT|AUTO|SMART",6);
                if(key.equals("stream_buffer_size")||key.equals("stream_max_iframe_size"))return string(value,"[0-9]{1,4}",4);
                return string(value,"[A-Za-z0-9_. ,|+-]{0,500}",500);
            default:return false;
        }
    }
    /** Stored values are intent. Never omit an explicitly stored value merely equal to a default. */
    public static JSONObject export(SharedPreferences prefs,Category category)throws JSONException{
        JSONObject data=new JSONObject();for(Map.Entry<String,?> entry:prefs.getAll().entrySet()){
            Rule rule=rule(entry.getKey());if(rule==null||rule.category!=category)continue;
            if(!valid(entry.getKey(),rule,entry.getValue()))throw new JSONException("Unsupported saved setting value: "+entry.getKey());
            Object value=entry.getValue();data.put(entry.getKey(),new JSONObject().put("type",rule.type.name()).put("value",value instanceof Set?new JSONArray(new TreeSet<>((Set<String>)value)):value));
        }
        return new JSONObject().put("schema",SCHEMA).put("category",category.name()).put("values",data);
    }
    public static Map<String,Object> validate(JSONObject payload,Category category)throws JSONException{
        if(payload.length()!=3||!numeric(payload.get("schema"))||((Number)payload.get("schema")).longValue()!=SCHEMA||!(payload.get("category") instanceof String)||!payload.getString("category").equals(category.name()))throw new JSONException("Unsupported settings schema or category");
        JSONObject data=payload.getJSONObject("values");Map<String,Object> result=new LinkedHashMap<>();
        for(Iterator<String> keys=data.keys();keys.hasNext();){String key=keys.next();Rule rule=rule(key);JSONObject item=data.getJSONObject(key);
            if(rule==null||rule.category!=category||item.length()!=2||!rule.type.name().equals(item.getString("type")))throw new JSONException("Unsupported category-owned setting");
            Object value=item.get("value");if(rule.type==Type.PROVIDERS||rule.type==Type.LANGUAGES){if(!(value instanceof JSONArray))throw new JSONException("Setting set required");Set<String> items=new LinkedHashSet<>();JSONArray array=(JSONArray)value;for(int i=0;i<array.length();i++){Object v=array.get(i);if(!(v instanceof String)||!items.add((String)v))throw new JSONException("Duplicate or invalid setting set");}value=items;}
            if(!valid(key,rule,value))throw new JSONException("Invalid portable setting value");result.put(key,value);
        }return result;
    }
    /** Returns an uncommitted category replacement. External restore journal owns commit/rollback. */
    public static SharedPreferences.Editor replace(SharedPreferences prefs,Category category,JSONObject payload)throws JSONException{
        Map<String,Object> data=validate(payload,category);SharedPreferences.Editor editor=prefs.edit();
        for(String key:prefs.getAll().keySet()){Rule rule=rule(key);if(rule!=null&&rule.category==category)editor.remove(key);}
        for(Map.Entry<String,Object> item:data.entrySet()){Object value=item.getValue();String key=item.getKey();switch(rule(key).type){
            case BOOLEAN:editor.putBoolean(key,(Boolean)value);break;case INT:editor.putInt(key,((Number)value).intValue());break;case FLOAT:editor.putFloat(key,((Number)value).floatValue());break;
            case LANGUAGES:case PROVIDERS:editor.putStringSet(key,(Set<String>)value);break;default:editor.putString(key,(String)value);
        }}if(data.containsKey("user_defined_density")&&!Objects.equals(prefs.getAll().get("user_defined_density"),((Number)data.get("user_defined_density")).intValue()))editor.putBoolean("user_defined_density_confirmed",false);return editor;
    }
    private PortableSettingsRegistry(){}
}
