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
  for(String key:new String[]{"subtitles_credentials","subtitles_hide_default","favSubLang","languages_list","codepage"})move(root,categories.get("Subtitles"),key);
  for(String key:new String[]{"display_resume_box","hide_controls_on_pause","player_projector_mode_key"})move(root,categories.get("Playback"),key);
  move(root,categories.get("Library"),"rescan_storage");
  Preference fullScan=root.findPreference("rescan_storage");if(fullScan!=null){fullScan.setTitle("Full Library Scan");fullScan.setSummary("Scan local storage and indexed network folders; retry unmatched descriptions");}
  // Only the inert UI placeholder is removed. No updater or saved preference existed.
  Preference updates=root.findPreference("preview_updates");if(updates!=null&&updates.getParent()!=null)updates.getParent().removePreference(updates);
  for(String key:new String[]{"preferences_torrent_path","preferences_torrent_blocklist"}){Preference pref=root.findPreference(key);if(pref!=null){pref.setEnabled(false);pref.setSelectable(false);pref.setSummary("BitTorrent controls are unavailable in this Preview — saved settings preserved");}}
  for(String key:new String[]{"preferences_video_os","preferences_video_tmdb","preferences_video_trakt"}){Preference attribution=root.findPreference(key);if(attribution!=null){attribution.setEnabled(false);attribution.setSelectable(false);}}
  PreviewBuildInfo.install(fragment,categories.get("About"));
  auditPresentation(root);
  style(root);

 }
 public static void auditPresentation(PreferenceGroup root){
  for(String key:new String[]{"smart_recently_rows","separate_anime_movie_show","show_last_added_row","show_last_played_row","show_watching_up_next_row","show_all_movies_row","show_all_tv_shows_row","show_all_animes_row","show_documentaries","hide_trailer_row","show_by_rating"}){Preference pref=root.findPreference(key);if(pref!=null){pref.setEnabled(false);pref.setSelectable(false);pref.setSummary("Legacy interface only — saved value preserved");}}
  style(root);
 }
 public static void style(PreferenceGroup group){for(int i=0;i<group.getPreferenceCount();i++){Preference p=group.getPreference(i);p.setIconSpaceReserved(false);p.setLayoutResource(p instanceof PreferenceCategory?R.layout.preview_preference_category:R.layout.preview_preference);if(!(p instanceof PreferenceCategory)){PreviewIcon icon=new PreviewIcon(String.valueOf(p.getTitle()));int[] colours={0xff65cffa,0xffb999f1,0xff67dca5,0xffffcc79};icon.setColorFilter(new android.graphics.PorterDuffColorFilter(colours[Math.floorMod(String.valueOf(p.getKey()).hashCode(),colours.length)],android.graphics.PorterDuff.Mode.SRC_IN));p.setIcon(icon);}if(p instanceof CheckBoxPreference)p.setWidgetLayoutResource(R.layout.preview_preference_checkbox);if(p instanceof PreferenceGroup)style((PreferenceGroup)p);}}
 // Intentional extension of the pinned AndroidX 1.2.1 preference adapter to preserve native preference semantics. Recheck on AndroidX upgrades.
 @android.annotation.SuppressLint("RestrictedApi")
 public static androidx.recyclerview.widget.RecyclerView.Adapter<?> adapter(PreferenceScreen root){return new PreferenceGroupAdapter(root){@Override public void onBindViewHolder(PreferenceViewHolder holder,int position){super.onBindViewHolder(holder,position);Preference p=getItem(position);boolean category=p instanceof PreferenceCategory;holder.itemView.setBackground(category?null:PreviewDialog.surface(holder.itemView.getContext(),false));holder.itemView.setForeground(category?null:PreviewDialog.focus(holder.itemView.getContext()));holder.itemView.setFocusable(!category&&p.isEnabled()&&p.isSelectable());holder.itemView.setAlpha(category||p.isEnabled()?1f:.38f);androidx.recyclerview.widget.RecyclerView.LayoutParams lp=new androidx.recyclerview.widget.RecyclerView.LayoutParams(-1,category?-2:PreviewDialog.dp(holder.itemView.getContext(),86));lp.setMargins(0,0,PreviewDialog.dp(holder.itemView.getContext(),10),PreviewDialog.dp(holder.itemView.getContext(),10));holder.itemView.setLayoutParams(lp);android.view.View widget=holder.findViewById(android.R.id.widget_frame);if(widget!=null&&!(p instanceof androidx.preference.TwoStatePreference)){android.view.ViewGroup.LayoutParams wp=widget.getLayoutParams();wp.width=PreviewDialog.dp(holder.itemView.getContext(),48);wp.height=PreviewDialog.dp(holder.itemView.getContext(),30);widget.setLayoutParams(wp);}holder.setDividerAllowedAbove(false);holder.setDividerAllowedBelow(false);}};}
 // Intentional extension of the pinned AndroidX 1.2.1 preference adapter to preserve native preference semantics. Recheck on AndroidX upgrades.
 @android.annotation.SuppressLint("RestrictedApi")
 public static androidx.recyclerview.widget.RecyclerView grid(android.content.Context c){
  com.archos.mediacenter.video.leanback.PreviewFocusRecycler list=new com.archos.mediacenter.video.leanback.PreviewFocusRecycler(c){
   @Override protected boolean focusablePosition(int p){if(!(getAdapter() instanceof PreferenceGroupAdapter))return false;Preference item=((PreferenceGroupAdapter)getAdapter()).getItem(p);return item!=null&&!(item instanceof PreferenceCategory)&&item.isEnabled()&&item.isSelectable();}
   @Override public View focusSearch(View focused,int direction){View item=findContainingItemView(focused);int pos=item==null?-1:getChildAdapterPosition(item);androidx.recyclerview.widget.GridLayoutManager lm=(androidx.recyclerview.widget.GridLayoutManager)getLayoutManager();if(direction==View.FOCUS_LEFT&&pos>=0&&lm.getSpanSizeLookup().getSpanIndex(pos,3)==0&&getTag() instanceof View)return (View)getTag();return super.focusSearch(focused,direction);}
  };
  androidx.recyclerview.widget.GridLayoutManager layout=new androidx.recyclerview.widget.GridLayoutManager(c,3);layout.setSpanSizeLookup(new androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup(){public int getSpanSize(int position){if(!(list.getAdapter() instanceof PreferenceGroupAdapter))return 1;return ((PreferenceGroupAdapter)list.getAdapter()).getItem(position) instanceof PreferenceCategory?3:1;}});list.setLayoutManager(layout);list.setClipToPadding(false);return list;
 }
 private static void move(PreferenceScreen root,PreferenceCategory dest,String key){Preference p=root.findPreference(key);if(p==null||p.getParent()==dest)return;PreferenceGroup parent=p.getParent();if(parent!=null)parent.removePreference(p);dest.addPreference(p);}
 public static void sidebar(PreferenceFragmentCompat fragment){
  androidx.recyclerview.widget.RecyclerView list=fragment.getListView();ViewGroup parent=(ViewGroup)list.getParent();int index=parent.indexOfChild(list);ViewGroup.LayoutParams original=list.getLayoutParams();parent.removeView(list);
  LinearLayout split=new LinearLayout(fragment.requireContext());split.setPadding(0,dp(fragment,12),0,0);split.setBackgroundColor(0xff0b1b29);ScrollView scroll=new ScrollView(fragment.requireContext());scroll.setVerticalScrollBarEnabled(false);LinearLayout links=new LinearLayout(fragment.requireContext());links.setOrientation(android.widget.LinearLayout.VERTICAL);scroll.addView(links);LinearLayout.LayoutParams rail=new LinearLayout.LayoutParams(dp(fragment,178),-1);rail.rightMargin=dp(fragment,20);split.addView(scroll,rail);list.setBackgroundColor(android.graphics.Color.TRANSPARENT);split.addView(list,new LinearLayout.LayoutParams(0,-1,1));parent.addView(split,index,original);
  PreferenceScreen root=fragment.getPreferenceScreen();final TextView[] selected={null};android.content.SharedPreferences state=PreferenceManager.getDefaultSharedPreferences(fragment.requireContext());String requested=fragment.requireActivity().getIntent().getBooleanExtra("show_streaming_settings",false)?"Streaming":state.getString("preview_settings_category","General");fragment.requireActivity().getIntent().removeExtra("show_streaming_settings");TextView initial=null;
  for(String name:NAMES){PreferenceCategory target=null;for(int i=0;i<root.getPreferenceCount();i++){Preference p=root.getPreference(i);if(p instanceof PreferenceCategory&&((PreferenceCategory)p).getPreferenceCount()>0&&name.contentEquals(p.getTitle())){target=(PreferenceCategory)p;break;}}if(target==null||target.getPreferenceCount()==0)continue;final PreferenceCategory category=target;
   TextView button=new TextView(fragment.requireContext());button.setText(name);button.setTextSize(13);button.setTextColor(0xffb4cbe0);button.setGravity(Gravity.CENTER_VERTICAL);button.setPadding(dp(fragment,10),0,dp(fragment,10),0);PreviewIcon.apply(button,name,17);button.setFocusable(true);button.setBackground(PreviewDialog.focus(fragment.requireContext()));
   Runnable select=()->{if(selected[0]==button)return;if(selected[0]!=null)selected[0].setTextColor(0xffb4cbe0);selected[0]=button;button.setTextColor(0xff59d8ff);list.setTag(button);for(int k=0;k<root.getPreferenceCount();k++){Preference pref=root.getPreference(k);if(pref instanceof PreferenceCategory)pref.setVisible(pref==category);}state.edit().putString("preview_settings_category",name).apply();list.scrollToPosition(0);};
   button.setOnFocusChangeListener((v,focused)->{if(focused)select.run();});button.setOnClickListener(v->{select.run();list.post(()->list.requestFocus());});button.setOnKeyListener((v,key,event)->{if(key==KeyEvent.KEYCODE_DPAD_RIGHT&&event.getAction()==KeyEvent.ACTION_DOWN){list.requestFocus();return true;}return false;});links.addView(button,new LinearLayout.LayoutParams(-1,dp(fragment,33)));if(initial==null||name.equals(requested))initial=button;
  }
  if(initial!=null){TextView first=initial;first.post(first::requestFocus);}
 }
 private static int dp(PreferenceFragmentCompat f,int v){return Math.round(v*f.getResources().getDisplayMetrics().density);}
}
