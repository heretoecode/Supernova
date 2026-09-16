package com.archos.mediacenter.video.leanback.settings;
import android.view.*;
import android.widget.*;
import androidx.preference.*;
import java.util.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.PreviewDialog;
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
  for(String name:new String[]{"Streaming","Integrations"}){PreferenceCategory c=categories.get(name);c.setEnabled(false);if(c.getPreferenceCount()==0){Preference p=new Preference(fragment.requireContext());p.setTitle("Coming soon");p.setEnabled(false);c.addPreference(p);}}
  if(root.findPreference("preview_updates")==null){Preference p=new Preference(fragment.requireContext());p.setKey("preview_updates");p.setTitle("Updates — Coming soon");p.setEnabled(false);categories.get("About").addPreference(p);}
  auditPresentation(root);
  style(root);

 }
 public static void auditPresentation(PreferenceGroup root){
  for(String key:new String[]{"smart_recently_rows","separate_anime_movie_show","show_last_added_row","show_last_played_row","show_watching_up_next_row","show_all_movies_row","show_all_tv_shows_row","show_all_animes_row","show_documentaries","hide_trailer_row","show_by_rating"}){Preference pref=root.findPreference(key);if(pref!=null){pref.setEnabled(false);pref.setSelectable(false);pref.setSummary("Legacy interface only — saved value preserved");}}
  style(root);
 }
 private static void style(PreferenceGroup group){for(int i=0;i<group.getPreferenceCount();i++){Preference p=group.getPreference(i);p.setIconSpaceReserved(false);p.setLayoutResource(p instanceof PreferenceCategory?R.layout.preview_preference_category:R.layout.preview_preference);if(p instanceof CheckBoxPreference)p.setWidgetLayoutResource(R.layout.preview_preference_checkbox);if(p instanceof PreferenceGroup)style((PreferenceGroup)p);}}
 public static androidx.recyclerview.widget.RecyclerView.Adapter<?> adapter(PreferenceScreen root){return new PreferenceGroupAdapter(root){@Override public void onBindViewHolder(PreferenceViewHolder holder,int position){super.onBindViewHolder(holder,position);Preference p=getItem(position);boolean category=p instanceof PreferenceCategory;holder.itemView.setBackground(category?null:PreviewDialog.focus(holder.itemView.getContext()));holder.itemView.setFocusable(!category&&p.isEnabled()&&p.isSelectable());holder.itemView.setAlpha(category||p.isEnabled()?1f:.38f);holder.setDividerAllowedAbove(false);holder.setDividerAllowedBelow(false);}};}
 private static void move(PreferenceScreen root,PreferenceCategory dest,String key){Preference p=root.findPreference(key);if(p==null||p.getParent()==dest)return;PreferenceGroup parent=p.getParent();if(parent!=null)parent.removePreference(p);dest.addPreference(p);}
 public static void sidebar(PreferenceFragmentCompat fragment){
  androidx.recyclerview.widget.RecyclerView list=fragment.getListView();ViewGroup parent=(ViewGroup)list.getParent();int index=parent.indexOfChild(list);ViewGroup.LayoutParams original=list.getLayoutParams();parent.removeView(list);
  LinearLayout split=new LinearLayout(fragment.requireContext());split.setPadding(0,dp(fragment,12),0,0);split.setBackgroundColor(0xff0b1b29);ScrollView scroll=new ScrollView(fragment.requireContext());scroll.setVerticalScrollBarEnabled(false);LinearLayout links=new LinearLayout(fragment.requireContext());links.setOrientation(1);scroll.addView(links);LinearLayout.LayoutParams rail=new LinearLayout.LayoutParams(dp(fragment,192),-1);rail.rightMargin=dp(fragment,24);split.addView(scroll,rail);list.setBackground(PreviewDialog.surface(fragment.requireContext(),false));split.addView(list,new LinearLayout.LayoutParams(0,-1,1));parent.addView(split,index,original);
  PreferenceScreen root=fragment.getPreferenceScreen();final TextView[] selected={null};for(String name:NAMES){PreferenceCategory target=null;for(int i=0;i<root.getPreferenceCount();i++){Preference p=root.getPreference(i);if(name.contentEquals(p.getTitle())){target=(PreferenceCategory)p;break;}}if(target==null)continue;final PreferenceCategory category=target;
   TextView button=new TextView(fragment.requireContext());button.setText(name);button.setTextSize(13);button.setTextColor(0xffb4cbe0);button.setGravity(Gravity.CENTER_VERTICAL);button.setPadding(dp(fragment,10),0,dp(fragment,10),0);PreviewIcon.apply(button,name,17);button.setFocusable(true);button.setBackground(PreviewDialog.focus(fragment.requireContext()));button.setOnClickListener(v->{if(selected[0]!=null){selected[0].setTextColor(0xffb4cbe0);selected[0].setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(0xffb4cbe0));}selected[0]=button;button.setTextColor(0xff59d8ff);button.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(0xff59d8ff));for(int k=0;k<root.getPreferenceCount();k++){Preference pref=root.getPreference(k);if(pref instanceof PreferenceCategory)pref.setVisible(pref==category);}list.scrollToPosition(0);list.post(()->list.requestFocus());});links.addView(button,new LinearLayout.LayoutParams(-1,dp(fragment,33)));if(selected[0]==null){selected[0]=button;button.setTextColor(0xff59d8ff);}
  }
  for(int k=0;k<root.getPreferenceCount();k++){Preference pref=root.getPreference(k);if(pref instanceof PreferenceCategory)pref.setVisible("General".contentEquals(pref.getTitle()));}
  if(selected[0]!=null)selected[0].post(()->selected[0].requestFocus());
 }
 private static int dp(PreferenceFragmentCompat f,int v){return Math.round(v*f.getResources().getDisplayMetrics().density);}
}
