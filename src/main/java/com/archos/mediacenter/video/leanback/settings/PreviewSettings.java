package com.archos.mediacenter.video.leanback.settings;
import android.view.*;
import android.widget.*;
import androidx.preference.*;
import java.util.*;
public final class PreviewSettings {
 private static final String[] NAMES={"General","Home & Discovery","Playback","Video & Audio","Subtitles","Library","Sources & Storage","Appearance","Trakt","Streaming","Integrations","Advanced","About"};
 public static void organise(PreferenceFragmentCompat fragment){
  PreferenceScreen root=fragment.getPreferenceScreen();if(root==null)return;
  Map<String,PreferenceCategory> categories=new LinkedHashMap<>();
  String[] keys={"preferences_about","category_leanback_user_interface","preview_playback","preferences_category_video","preview_subtitles","scraper_category","netshare_category","category_user_interface","trakt_category","streaming_category","preview_integrations","preferences_category_advanced_video","about_category"};
  for(int i=0;i<keys.length;i++){Preference p=root.findPreference(keys[i]);PreferenceCategory c=p instanceof PreferenceCategory?(PreferenceCategory)p:new PreferenceCategory(fragment.requireContext());if(p==null){c.setKey(keys[i]);root.addPreference(c);}c.setTitle(NAMES[i]);c.setOrder(i*100);categories.put(NAMES[i],c);}
  java.util.List<PreferenceCategory> old=new ArrayList<>();for(int i=0;i<root.getPreferenceCount();i++)if(root.getPreference(i) instanceof PreferenceCategory)old.add((PreferenceCategory)root.getPreference(i));
  for(PreferenceCategory c:old){String title=String.valueOf(c.getTitle()).toLowerCase(Locale.ROOT);if(!categories.containsValue(c)){PreferenceCategory dest=categories.get(title.contains("language")?"Subtitles":title.contains("torrent")?"Advanced":"Sources & Storage");while(c.getPreferenceCount()>0){Preference p=c.getPreference(0);c.removePreference(p);dest.addPreference(p);}c.setVisible(false);}}
  move(root,categories.get("General"),"ui_lang");move(root,categories.get("About"),"preferences_version");
  for(String key:new String[]{"playback_speed","audio_speed_audiotrack","resume","repeat_mode","playback_repeat_mode"})move(root,categories.get("Playback"),key);
  if(root.findPreference("remember_library_views")==null){SwitchPreferenceCompat remember=new SwitchPreferenceCompat(fragment.requireContext());remember.setKey("remember_library_views");remember.setTitle("Remember library view preferences");remember.setSummary("Keep Movies and TV Shows views, filters, sort and order separately");remember.setDefaultValue(true);categories.get("Library").addPreference(remember);}
  // The postponed integration must never appear to be ready for use.
  for(String name:new String[]{"Streaming","Integrations"}){PreferenceCategory c=categories.get(name);c.setEnabled(false);c.removeAll();Preference p=new Preference(fragment.requireContext());p.setTitle("Coming soon");p.setEnabled(false);c.addPreference(p);}
  if(root.findPreference("preview_updates")==null){Preference p=new Preference(fragment.requireContext());p.setKey("preview_updates");p.setTitle("Updates — Coming soon");p.setEnabled(false);categories.get("About").addPreference(p);}
  for(PreferenceCategory c:categories.values())c.setIconSpaceReserved(false);
 }
 private static void move(PreferenceScreen root,PreferenceCategory dest,String key){Preference p=root.findPreference(key);if(p==null||p.getParent()==dest)return;PreferenceGroup parent=p.getParent();if(parent!=null)parent.removePreference(p);dest.addPreference(p);}
 public static void sidebar(PreferenceFragmentCompat fragment){
  androidx.recyclerview.widget.RecyclerView list=fragment.getListView();ViewGroup parent=(ViewGroup)list.getParent();int index=parent.indexOfChild(list);ViewGroup.LayoutParams original=list.getLayoutParams();parent.removeView(list);
  LinearLayout split=new LinearLayout(fragment.requireContext());split.setBackgroundColor(0xee132638);ScrollView scroll=new ScrollView(fragment.requireContext());LinearLayout links=new LinearLayout(fragment.requireContext());links.setOrientation(1);scroll.addView(links);split.addView(scroll,new LinearLayout.LayoutParams(dp(fragment,185),-1));split.addView(list,new LinearLayout.LayoutParams(0,-1,1));parent.addView(split,index,original);
  PreferenceScreen root=fragment.getPreferenceScreen();for(String name:NAMES){PreferenceCategory target=null;for(int i=0;i<root.getPreferenceCount();i++){Preference p=root.getPreference(i);if(name.contentEquals(p.getTitle())){target=(PreferenceCategory)p;break;}}if(target==null)continue;final PreferenceCategory category=target;
   TextView button=new TextView(fragment.requireContext());button.setText(name);button.setTextSize(14);button.setTextColor(0xffb4cbe0);button.setPadding(dp(fragment,14),dp(fragment,12),dp(fragment,14),dp(fragment,12));button.setFocusable(true);button.setOnFocusChangeListener((v,f)->{v.setBackgroundColor(f?0x603f7897:0);button.setTextColor(f?0xff59d8ff:0xffb4cbe0);});button.setOnClickListener(v->{for(int j=0;j<root.getPreferenceCount();j++){Preference pref=root.getPreference(j);if(pref instanceof PreferenceCategory)pref.setVisible(pref==category);}list.scrollToPosition(0);list.post(()->list.requestFocus());});links.addView(button);
  }
  for(int j=0;j<root.getPreferenceCount();j++){Preference pref=root.getPreference(j);if(pref instanceof PreferenceCategory)pref.setVisible("General".contentEquals(pref.getTitle()));}
 }
 private static int dp(PreferenceFragmentCompat f,int v){return Math.round(v*f.getResources().getDisplayMetrics().density);}
}
