package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import androidx.preference.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.*;
import org.json.*;

/** Audit IDs are stable; runtime values and capability gates remain owned by native preferences. */
final class SettingsRegistry {
    static final String[] CATEGORIES={"Playback & Video","Audio & Subtitles","Library & Home","Streaming & Providers","Appearance","Connections & Integrations","System & Maintenance","About Supernova"};
    static final String[] DESCRIPTIONS={"Control how content plays, resumes and looks on screen.","Configure audio, subtitles and related playback options.","Manage your libraries, metadata and Home screen displays.","Set up and manage streaming providers and availability preferences.","Customise the look and feel of Supernova.","Manage external connections, accounts and integrations.","Diagnostics, backup and other system options.","Version information, credits and legal details."};
    static final String[] ICONS={"play","audio","film","cloud","tv","settings.nodes","settings.gear","info"};
    static final class Entry {
        final String id,key,category,section,disposition;
        Entry(JSONObject value){id=value.optString("id");key=value.optString("key");category=value.optString("destination");section=value.optString("subsection");disposition=value.optString("disposition");}
        boolean nested(){return id.startsWith("S")&&id.compareTo("S140")>=0;}
        boolean excluded(){return category.equals("REVIEW / NON-EXPOSED")||Arrays.asList("S052","S070","S091","S106","S107").contains(id);}
        boolean integration(){return Arrays.asList("S108","S109","S110").contains(id);}
        String actualKey(Context context){return key.replace("{country}",StreamingRepository.country(context));}
        Preference resolve(PreferenceGroup root){String actual=actualKey(root.getContext());return key.equals("(JustWatch)")?findTitle(root,"JustWatch"):root.findPreference(actual);}
    }
    final List<Entry> entries=new ArrayList<>();
    SettingsRegistry(Context context){
        Set<String> ids=new HashSet<>();
        read(context,"foundation/settings-map.json",ids);
        if(entries.size()!=165)throw new IllegalStateException("Incomplete baseline settings definition map");
        read(context,"foundation/settings-map-0146.json",ids);
        if(entries.size()!=170)throw new IllegalStateException("Incomplete native subtitle settings extension");
    }
    private void read(Context context,String asset,Set<String> ids){
        try(java.io.InputStream stream=context.getAssets().open(asset)){
            java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();byte[] buffer=new byte[4096];int n;while((n=stream.read(buffer))!=-1)bytes.write(buffer,0,n);
            JSONArray rows=new JSONObject(bytes.toString("UTF-8")).getJSONArray("rows");
            for(int i=0;i<rows.length();i++){Entry e=new Entry(rows.getJSONObject(i));if(e.id.isEmpty()||e.key.isEmpty()||!ids.add(e.id))throw new IllegalStateException("Invalid or duplicate settings definition");entries.add(e);}
        }catch(Exception error){throw new IllegalStateException("Settings definition map unavailable: "+asset,error);}
    }

    List<String> sections(int category){
        if(category==7)return Arrays.asList("App information","Release Notes","Open-source Licences","Credits & Acknowledgements","Technical Information");
        LinkedHashSet<String> values=new LinkedHashSet<>();
        if(category==0)values.addAll(Arrays.asList("Playback","Video & Display","Resume & Progress"));
        for(Entry e:entries)if(e.category.equals(CATEGORIES[category])&&!e.excluded())values.add(e.nested()||e.id.equals("S086")||e.id.equals("S087")?"Backup & restore":e.section);
        return new ArrayList<>(values);
    }
    List<Entry> in(int category,String section){List<Entry> values=new ArrayList<>();for(Entry e:entries)if(e.category.equals(CATEGORIES[category])&&e.section.equals(section)&&!e.excluded()&&!e.nested())values.add(e);return values;}
    private static Preference findTitle(PreferenceGroup group,String title){for(int i=0;i<group.getPreferenceCount();i++){Preference p=group.getPreference(i);if(title.contentEquals(p.getTitle()))return p;if(p instanceof PreferenceGroup){Preference match=findTitle((PreferenceGroup)p,title);if(match!=null)return match;}}return null;}
    static boolean visible(Preference preference,boolean mirror){
        if(preference==null)return false;if(mirror)return true;
        for(Preference p=preference;p!=null;p=p.getParent())if(!p.isVisible())return false;
        return true;
    }
}
