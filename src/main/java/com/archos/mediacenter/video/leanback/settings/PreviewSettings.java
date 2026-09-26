package com.archos.mediacenter.video.leanback.settings;
import android.view.*;
import android.widget.*;
import androidx.preference.*;
import java.util.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewHomeRows;
import com.archos.mediacenter.video.leanback.PreviewAccent;
public final class PreviewSettings {
 private static final java.util.WeakHashMap<android.content.Context,java.lang.ref.WeakReference<TextView>> HELP=new java.util.WeakHashMap<>();
 private static final String[] ORIGINAL_NAMES={"General","Home & Discovery","Playback","Video & Audio","Subtitles","Library","Sources & Storage","Appearance","Trakt","Streaming","Integrations","Advanced","About","Legacy"};
 public static void organise(PreferenceFragmentCompat fragment){
  PreferenceScreen root=fragment.getPreferenceScreen();if(root==null)return;
  Map<String,PreferenceCategory> categories=new LinkedHashMap<>();
  String[] keys={"preferences_about","category_leanback_user_interface","preview_playback","preferences_category_video","preview_subtitles","scraper_category","netshare_category","category_user_interface","trakt_category","streaming_category","preview_integrations","preferences_category_advanced_video","about_category","preview_legacy"};
  for(int i=0;i<keys.length;i++){Preference p=root.findPreference(keys[i]);PreferenceCategory c=p instanceof PreferenceCategory?(PreferenceCategory)p:new PreferenceCategory(fragment.requireContext());if(p==null){c.setKey(keys[i]);root.addPreference(c);}c.setTitle(ORIGINAL_NAMES[i]);c.setOrder(i*100);categories.put(ORIGINAL_NAMES[i],c);}
  java.util.List<PreferenceCategory> old=new ArrayList<>();for(int i=0;i<root.getPreferenceCount();i++)if(root.getPreference(i) instanceof PreferenceCategory)old.add((PreferenceCategory)root.getPreference(i));
  for(PreferenceCategory c:old){String title=String.valueOf(c.getTitle()).toLowerCase(Locale.ROOT);if(!categories.containsValue(c)){PreferenceCategory dest=categories.get(title.contains("language")?"Subtitles":title.contains("torrent")?"Advanced":"Sources & Storage");while(c.getPreferenceCount()>0){Preference p=c.getPreference(0);c.removePreference(p);dest.addPreference(p);}c.setVisible(false);}}
  if(root.findPreference("preview_sftp_trust")==null){
   Preference trust=new Preference(fragment.requireContext());trust.setKey("preview_sftp_trust");trust.setTitle("SFTP server identities");
   trust.setSummary("First connection stores a server identity. Changed identities are rejected. Only reset after verifying a legitimate server key change.");
   trust.setOnPreferenceClickListener(p->{String[] hosts=com.archos.filecorelibrary.sftp.SftpHostTrust.hosts();
    if(hosts.length==0){PreviewDialog.read(fragment.requireContext(),"SFTP server identities","No identities recorded yet. First-use trust does not protect an already compromised first connection.");return true;}
    PreviewDialog.choose(fragment.requireContext(),"Recorded SFTP servers",hosts,-1,n->PreviewDialog.choose(fragment.requireContext(),"Reset this server identity only after independently verifying its new key",new String[]{"Cancel","Reset identity for "+hosts[n]},0,i->{if(i==1)com.archos.filecorelibrary.sftp.SftpHostTrust.forget(hosts[n]);}));return true;});
   categories.get("Sources & Storage").addPreference(trust);
  }
  move(root,categories.get("Appearance"),"ui_lang");move(root,categories.get("About"),"preferences_version");
  for(String key:new String[]{"playback_speed","audio_speed_audiotrack","resume","repeat_mode","playback_repeat_mode"})move(root,categories.get("Playback"),key);
  if(root.findPreference("remember_library_views")==null){SwitchPreferenceCompat remember=new SwitchPreferenceCompat(fragment.requireContext());remember.setKey("remember_library_views");remember.setTitle("Remember library view preferences");remember.setSummary("Keep Movies and TV Shows views, filters, sort and order separately");remember.setDefaultValue(true);categories.get("Library").addPreference(remember);}
  for(String key:new String[]{"subtitles_credentials","subtitles_hide_default","favSubLang","languages_list","codepage"})move(root,categories.get("Subtitles"),key);
  for(String key:new String[]{"display_resume_box","hide_controls_on_pause","player_projector_mode_key"})move(root,categories.get("Playback"),key);
  move(root,categories.get("Library"),"rescan_storage");
  Preference fullScan=root.findPreference("rescan_storage");if(fullScan!=null){fullScan.setTitle("Full Library Scan");fullScan.setSummary("Scan local storage and indexed network folders; retry unmatched descriptions");}
  SwitchPreferenceCompat automatic=new SwitchPreferenceCompat(fragment.requireContext());automatic.setKey("auto_rescan_on_app_restart");automatic.setTitle("Scan network library on return");automatic.setSummary("Check indexed sources, including WebDAV, when Supernova opens or returns to the foreground");automatic.setDefaultValue(true);categories.get("Library").addPreference(automatic);
  ListPreference interval=new ListPreference(fragment.requireContext());interval.setKey("preview_scan_interval_control");interval.setPersistent(false);interval.setTitle("Automatic network scan interval");interval.setEntries(new String[]{"Off","15 minutes","30 minutes","1 hour","6 hours","24 hours"});interval.setEntryValues(new String[]{"0","900000","1800000","3600000","21600000","86400000"});int savedPeriod=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(fragment.requireContext());interval.setValue(String.valueOf(savedPeriod));interval.setSummaryProvider(p->{int period=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(fragment.requireContext());return period<=0?"Off":period/60000+" minutes · Android may defer background jobs";});interval.setOnPreferenceChangeListener((p,value)->{com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(fragment.requireContext(),0,Integer.parseInt(value.toString()),true);return true;});categories.get("Library").addPreference(interval);
  // Only the inert UI placeholder is removed. No updater or saved preference existed.
  Preference updates=root.findPreference("preview_updates");if(updates!=null&&updates.getParent()!=null)updates.getParent().removePreference(updates);
  // Existing torrent preferences own working onClick handlers and retain their folder picker.
  for(String key:new String[]{"preferences_video_os","preferences_video_tmdb","preferences_video_trakt"}){Preference attribution=root.findPreference(key);if(attribution!=null){attribution.setEnabled(false);attribution.setSelectable(false);}}
  for(String key:new String[]{"always_leanback_on_tv_key","reset_last_played_row","reset_last_played_section","preferences_movie_sort_order","preferences_tv_show_sort_order","preferences_animes_sort_order","try_new_ui","smart_recently_rows","separate_anime_movie_show","show_last_added_row","show_last_played_row","show_watching_up_next_row","show_all_movies_row","show_all_tv_shows_row","show_all_animes_row","show_documentaries","hide_trailer_row","show_by_rating","app_theme"}){
   Preference p=root.findPreference(key);if(p!=null){move(root,categories.get("Legacy"),key);p.setEnabled(false);p.setSelectable(false);p.setSummary(key.equals("player_projector_mode_key")?"Legacy: aligns video and effects to the top of the screen. Existing value is preserved; presentation control retired pending device QA.":key.equals("app_theme")?"Legacy theme retained for classic surfaces. Use Accent & Colour in Appearance.":"Deprecated in this interface — underlying behaviour and saved value preserved");}
  }
  Preference accent=new Preference(fragment.requireContext());accent.setKey("preview_accent_picker");accent.setTitle("Accent & Colour");accent.setSummary("Focus, selected controls and subtle utility gradients");accent.setOnPreferenceClickListener(p->{PreviewAccent.choose(fragment.requireContext(),()->fragment.requireActivity().recreate());return true;});categories.get("Appearance").addPreference(accent);
  Preference rows=new Preference(fragment.requireContext());rows.setKey("preview_home_editor");rows.setTitle("Home rows");rows.setSummary("Create, rename, hide and reorder rows; manage Watch Next");rows.setOnPreferenceClickListener(p->{PreviewHomeRows.customise(fragment.requireContext(),()->{});return true;});categories.get("Home & Discovery").addPreference(rows);
  Preference clear=new Preference(fragment.requireContext());clear.setKey("preview_clear_watch_next");clear.setTitle("Clear Watch Next");clear.setSummary("Remove all Watch Next titles after confirmation. Library and playback history are kept.");clear.setOnPreferenceClickListener(p->{PreviewHomeRows.clearWatchNext(fragment.requireContext());return true;});categories.get("Home & Discovery").addPreference(clear);
  String[] sources={"recent","trending","popular"},sourceNames={"Featured: Recently Added","Featured: Trakt Trending","Featured: Trakt Popular"};for(int n=0;n<sources.length;n++){SwitchPreferenceCompat enabled=new SwitchPreferenceCompat(fragment.requireContext());enabled.setKey("preview_featured_"+sources[n]);enabled.setTitle(sourceNames[n]);enabled.setSummary("Select from matching local-library titles; offline local fallback");enabled.setDefaultValue(true);categories.get("Home & Discovery").addPreference(enabled);}
  SwitchPreferenceCompat diagnostics=new SwitchPreferenceCompat(fragment.requireContext());diagnostics.setKey(com.archos.mediacenter.video.diagnostics.Diagnostics.KEY);diagnostics.setTitle("Diagnostic Logging");diagnostics.setSummary("Off by default. Bounded, privacy-filtered logs for development and Shield QA.");diagnostics.setDefaultValue(false);diagnostics.setOnPreferenceChangeListener((p,value)->{com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(fragment.requireContext(),(Boolean)value);return true;});categories.get("Advanced").addPreference(diagnostics);
  ListPreference level=new ListPreference(fragment.requireContext());level.setKey(com.archos.mediacenter.video.diagnostics.Diagnostics.LEVEL);level.setTitle("Diagnostic detail");level.setEntries(new String[]{"Normal","QA / Soak"});level.setEntryValues(new String[]{"normal","qa"});level.setDefaultValue("normal");level.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());categories.get("Advanced").addPreference(level);
  for(String label:new String[]{"Issues Digest","Report a Problem"}){Preference item=new Preference(fragment.requireContext());item.setKey("preview_diagnostic_"+label);item.setTitle(label);item.setOnPreferenceClickListener(p->{if(label.equals("Issues Digest"))com.archos.mediacenter.video.diagnostics.Diagnostics.showDigest(fragment.requireContext());else com.archos.mediacenter.video.diagnostics.Diagnostics.reportProblem(fragment.requireContext());return true;});categories.get("Advanced").addPreference(item);}
  Preference export=new Preference(fragment.requireContext());export.setKey("export_diagnostic_report");export.setTitle("Export Diagnostic Report");export.setSummary("Save recent logs and safe device/build information as a ZIP. Choose a destination using Android Files.");export.setOnPreferenceClickListener(p->{fragment.startActivity(new android.content.Intent(fragment.requireContext(),com.archos.mediacenter.video.diagnostics.DiagnosticExportActivity.class));return true;});categories.get("Advanced").addPreference(export);
  PreviewBuildInfo.install(fragment,categories.get("About"));
  consolidate(fragment,root,categories);
  auditPresentation(root);
  style(root);

 }
 private static final String[] NAMES={"Playback","Video","Audio","Subtitles","Library & Metadata","Home","Appearance","Streaming","Network","Integrations","Advanced","About"};
 private static void consolidate(PreferenceFragmentCompat fragment,PreferenceScreen root,Map<String,PreferenceCategory> categories){
  PreferenceCategory video=categories.get("Video & Audio");video.setTitle("Video");
  categories.get("Sources & Storage").setTitle("Network");
  categories.get("Library").setTitle("Library & Metadata");
  categories.get("Home & Discovery").setTitle("Home");
  PreferenceCategory audio=new PreferenceCategory(fragment.requireContext());audio.setKey("preview_audio");audio.setTitle("Audio");root.addPreference(audio);
  categories.put("Audio",audio);
  PreferenceCategory general=categories.get("General");
  while(general.getPreferenceCount()>0){Preference p=general.getPreference(0);general.removePreference(p);categories.get("About").addPreference(p);}general.setVisible(false);
  for(String key:new String[]{"favAudioLang","prefer_original_audio_track","force_audio_passthrough_multiple","player_spatialization_enabled"})move(root,audio,key);
  for(String key:new String[]{"allow_3rd_party_player","network_bookmarks",fragment.getString(R.string.reset_brightness_on_start_key)})move(root,categories.get("Playback"),key);
  for(String key:new String[]{"hide_watched","sort_ignore_articles","preference_display_all_files","pref_create_remote_thumbs"})move(root,categories.get("Library"),key);
  PreferenceCategory advanced=categories.get("Advanced");
  String[][] compatibility={
   {"Video Compatibility","force_software_decoding","dec_choice","parser_sync_mode","enable_cutout_mode_short_edges","enable_cutout_both_sidesx","stream_max_iframe_size"},
   {"Audio Compatibility","audio_interface_choice","audio_decoder_choice","force_passthrough","audio_speed_audiotrack","enable_dynamic_audio_delay","disable_downmix","enable_downmix_androidtv"},
   {"Network Compatibility","pref_smbj","pref_smbv2","pref_smb_resolv","pref_smb_disable_tcp_discovery","pref_smb_disable_udp_discovery","pref_smb_disable_mdns_discovery","pref_sshj","stream_buffer_size"},
   {"Subtitle Compatibility","codepage"}};
  for(String[] group:compatibility){PreferenceCategory section=new PreferenceCategory(fragment.requireContext());section.setKey("preview_compat_"+group[0]);section.setTitle(group[0]);advanced.addPreference(section);for(int i=1;i<group.length;i++)move(root,section,group[i]);}
  PreferenceCategory integrations=categories.get("Integrations"),trakt=categories.get("Trakt");root.removePreference(trakt);integrations.addPreference(trakt);
  PreferenceCategory subtitles=new PreferenceCategory(fragment.requireContext());subtitles.setKey("preview_opensubtitles");subtitles.setTitle("OpenSubtitles");integrations.addPreference(subtitles);move(root,subtitles,"subtitles_credentials");
  PreferenceCategory intro=new PreferenceCategory(fragment.requireContext());intro.setKey("preview_introdb");intro.setTitle("IntroDB");integrations.addPreference(intro);
  SwitchPreferenceCompat skip=new SwitchPreferenceCompat(fragment.requireContext());skip.setKey(com.archos.mediacenter.video.player.PlayerService.KEY_INTRODB_ENABLED);skip.setTitle("Automatic segment skipping");skip.setSummary("Use IntroDB segment timings with the existing playback-mode rules");skip.setDefaultValue(com.archos.mediacenter.video.player.PlayerService.DEFAULT_INTRODB_ENABLED);intro.addPreference(skip);
  Preference hidden=root.findPreference("subtitles_hide_default");
  if(hidden instanceof TwoStatePreference){TwoStatePreference original=(TwoStatePreference)hidden;original.setVisible(false);SwitchPreferenceCompat positive=new SwitchPreferenceCompat(fragment.requireContext());positive.setKey("preview_subtitles_by_default");positive.setTitle("Subtitles by Default");positive.setPersistent(false);positive.setChecked(!original.isChecked());positive.setOnPreferenceChangeListener((p,value)->{original.setChecked(!(Boolean)value);return true;});categories.get("Subtitles").addPreference(positive);}
  // Source scheduling has one home, backed by NetworkAutoRefresh, in Network & Files.
  for(String key:new String[]{"rescan_storage","auto_rescan_on_app_restart","preview_scan_interval_control","share_folders","preferences_torrent_path","preferences_torrent_blocklist","uimode","uimode_leanback"}){Preference p=root.findPreference(key);if(p!=null)p.setVisible(false);}
  categories.get("Legacy").setVisible(false);
 }
 private static String contextFor(Preference p){String text=String.valueOf(p.getTitle())+"\n\n"+(p.getSummary()==null?"Select to change this setting.":p.getSummary());
  if(p instanceof ListPreference){ListPreference list=(ListPreference)p;CharSequence[] entries=list.getEntries(),values=list.getEntryValues();if(entries!=null&&values!=null){text+="\n";for(int i=0;i<Math.min(entries.length,6);i++)text+="\n"+(values[i].toString().equals(list.getValue())?"●  ":"○  ")+entries[i];}}
  else if(p instanceof TwoStatePreference)text+="\n\nCurrent value: "+(((TwoStatePreference)p).isChecked()?"On":"Off");
  if(String.valueOf(p.getKey()).toLowerCase(Locale.ROOT).contains("sub"))text+="\n\nSubtitle preview\nThe next chapter begins.";
  return text;
 }
 private static void focusFirst(androidx.recyclerview.widget.RecyclerView list){list.post(new Runnable(){int attempts;public void run(){if(!list.isAttachedToWindow())return;if(list.hasPendingAdapterUpdates()){if(attempts++<16)list.postOnAnimation(this);return;}for(int i=0;i<list.getChildCount();i++){View child=list.getChildAt(i);if(child.isFocusable()&&child.isEnabled()&&child.requestFocus())return;}if(attempts++<16)list.postOnAnimation(this);}});}
 public static void auditPresentation(PreferenceGroup root){
  for(String key:new String[]{"smart_recently_rows","separate_anime_movie_show","show_last_added_row","show_last_played_row","show_watching_up_next_row","show_all_movies_row","show_all_tv_shows_row","show_all_animes_row","show_documentaries","hide_trailer_row","show_by_rating"}){Preference pref=root.findPreference(key);if(pref!=null){pref.setEnabled(false);pref.setSelectable(false);pref.setSummary("Legacy interface only — saved value preserved");}}
  style(root);
 }
 public static void style(PreferenceGroup group){for(int i=0;i<group.getPreferenceCount();i++){Preference p=group.getPreference(i);p.setIconSpaceReserved(false);p.setLayoutResource(p instanceof PreferenceCategory?R.layout.preview_preference_category:R.layout.preview_preference);if(!(p instanceof PreferenceCategory)){PreviewIcon icon=new PreviewIcon(String.valueOf(p.getTitle()));p.setIcon(icon);}if(p instanceof CheckBoxPreference)p.setWidgetLayoutResource(R.layout.preview_preference_checkbox);if(p instanceof PreferenceGroup)style((PreferenceGroup)p);}}
 // Intentional extension of the pinned AndroidX 1.2.1 preference adapter to preserve native preference semantics. Recheck on AndroidX upgrades.
 @android.annotation.SuppressLint("RestrictedApi")
 private static TextView helpFor(android.content.Context context){java.lang.ref.WeakReference<TextView> ref=HELP.get(context);return ref==null?null:ref.get();}
 public static androidx.recyclerview.widget.RecyclerView.Adapter<?> adapter(PreferenceScreen root){return new PreferenceGroupAdapter(root){@Override public void onBindViewHolder(PreferenceViewHolder holder,int position){super.onBindViewHolder(holder,position);Preference p=getItem(position);boolean category=p instanceof PreferenceCategory;holder.itemView.setBackgroundColor(category?android.graphics.Color.TRANSPARENT:0x280e2332);holder.itemView.setOnFocusChangeListener((v,focused)->{if(focused){TextView help=helpFor(activityContext(v.getContext()));if(help!=null)help.setText(contextFor(p));}});holder.itemView.setForeground(category?null:PreviewDialog.focus(holder.itemView.getContext()));holder.itemView.setFocusable(!category&&p.isEnabled()&&p.isSelectable());holder.itemView.setFocusableInTouchMode(!category&&p.isEnabled()&&p.isSelectable());if(holder.itemView.hasFocus()){TextView help=helpFor(activityContext(holder.itemView.getContext()));if(help!=null)help.setText(contextFor(p));}holder.itemView.setAlpha(category||p.isEnabled()?1f:.38f);androidx.recyclerview.widget.RecyclerView.LayoutParams lp=new androidx.recyclerview.widget.RecyclerView.LayoutParams(-1,category?-2:PreviewDialog.dp(holder.itemView.getContext(),54));lp.setMargins(0,0,PreviewDialog.dp(holder.itemView.getContext(),0),PreviewDialog.dp(holder.itemView.getContext(),4));holder.itemView.setLayoutParams(lp);android.view.View widget=holder.findViewById(android.R.id.widget_frame);if(widget!=null&&!(p instanceof androidx.preference.TwoStatePreference)){android.view.ViewGroup.LayoutParams wp=widget.getLayoutParams();wp.width=PreviewDialog.dp(holder.itemView.getContext(),48);wp.height=PreviewDialog.dp(holder.itemView.getContext(),30);widget.setLayoutParams(wp);fitBadge(widget);}tintSwitches(holder.itemView);holder.setDividerAllowedAbove(false);holder.setDividerAllowedBelow(false);}};}
 // Intentional extension of the pinned AndroidX 1.2.1 preference adapter to preserve native preference semantics. Recheck on AndroidX upgrades.
 @android.annotation.SuppressLint("RestrictedApi")
 public static androidx.recyclerview.widget.RecyclerView grid(android.content.Context c){
  com.archos.mediacenter.video.leanback.PreviewFocusRecycler list=new com.archos.mediacenter.video.leanback.PreviewFocusRecycler(c){
   @Override protected boolean locksHorizontalEdges(){return false;}
   @Override protected boolean focusablePosition(int p){if(!(getAdapter() instanceof PreferenceGroupAdapter))return false;Preference item=((PreferenceGroupAdapter)getAdapter()).getItem(p);return item!=null&&!(item instanceof PreferenceCategory)&&item.isEnabled()&&item.isSelectable();}
   @Override public View focusSearch(View focused,int direction){View item=findContainingItemView(focused);int pos=item==null?-1:getChildAdapterPosition(item);androidx.recyclerview.widget.GridLayoutManager lm=(androidx.recyclerview.widget.GridLayoutManager)getLayoutManager();if(direction==View.FOCUS_LEFT&&pos>=0&&lm.getSpanSizeLookup().getSpanIndex(pos,1)==0&&getTag() instanceof View)return (View)getTag();return super.focusSearch(focused,direction);}
  };
  androidx.recyclerview.widget.GridLayoutManager layout=new androidx.recyclerview.widget.GridLayoutManager(c,1);layout.setSpanSizeLookup(new androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup(){public int getSpanSize(int position){if(!(list.getAdapter() instanceof PreferenceGroupAdapter))return 1;return ((PreferenceGroupAdapter)list.getAdapter()).getItem(position) instanceof PreferenceCategory?1:1;}});list.setLayoutManager(layout);list.setClipToPadding(false);return list;
 }
 private static void tintSwitches(View view){int colour=PreviewAccent.color(view.getContext());android.content.res.ColorStateList tint=new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{colour,0xff738493});if(view instanceof androidx.appcompat.widget.SwitchCompat){((androidx.appcompat.widget.SwitchCompat)view).setThumbTintList(tint);((androidx.appcompat.widget.SwitchCompat)view).setTrackTintList(tint.withAlpha(90));}else if(view instanceof android.widget.CompoundButton)((android.widget.CompoundButton)view).setButtonTintList(tint);if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)tintSwitches(((ViewGroup)view).getChildAt(i));}
 private static void fitBadge(View view){if(view instanceof ImageView){ImageView image=(ImageView)view;image.setScaleType(ImageView.ScaleType.FIT_CENTER);ViewGroup.LayoutParams size=image.getLayoutParams();size.width=ViewGroup.LayoutParams.MATCH_PARENT;size.height=ViewGroup.LayoutParams.MATCH_PARENT;image.setLayoutParams(size);}else if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++)fitBadge(group.getChildAt(i));}}
 private static void move(PreferenceScreen root,PreferenceCategory dest,String key){Preference p=root.findPreference(key);if(p==null||p.getParent()==dest)return;PreferenceGroup parent=p.getParent();if(parent!=null)parent.removePreference(p);dest.addPreference(p);}
 public static void sidebar(PreferenceFragmentCompat fragment) {
  android.content.Context c = fragment.requireContext();
  androidx.recyclerview.widget.RecyclerView list = fragment.getListView();
  ViewGroup parent = (ViewGroup) list.getParent();
  int index = parent.indexOfChild(list);
  ViewGroup.LayoutParams original = list.getLayoutParams();
  parent.removeView(list);
  LinearLayout split = new LinearLayout(c);
  split.setPadding(0, dp(fragment, 12), 0, dp(fragment, 8));
  split.setClipChildren(false);
  LinearLayout links = new LinearLayout(c);
  links.setOrientation(LinearLayout.VERTICAL);
  links.setClipChildren(false);
  LinearLayout.LayoutParams rail = new LinearLayout.LayoutParams(0, -1, .23f);
  rail.rightMargin = dp(fragment, 12);
  split.addView(links, rail);
  LinearLayout middle = new LinearLayout(c);
  middle.setOrientation(LinearLayout.VERTICAL);
  middle.setClipChildren(false);
  LinearLayout children = new LinearLayout(c);
  children.setOrientation(LinearLayout.VERTICAL);
  children.setClipChildren(false);
  middle.addView(children, new LinearLayout.LayoutParams(-1, -2));
  middle.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
  split.addView(middle, new LinearLayout.LayoutParams(0, -1, .44f));
  TextView help = new TextView(c);
  help.setTextSize(14); help.setTextColor(0xffe1e9ef);
  help.setLineSpacing(dp(fragment, 4), 1);
  help.setPadding(dp(fragment, 16), dp(fragment, 12), dp(fragment, 12), dp(fragment, 12));
  ScrollView helpScroll = new ScrollView(c);
  helpScroll.addView(help); helpScroll.setFocusable(false);
  helpScroll.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
  LinearLayout.LayoutParams helpSize = new LinearLayout.LayoutParams(0, -1, .33f);
  helpSize.leftMargin = dp(fragment, 12);
  split.addView(helpScroll, helpSize);
  HELP.put(activityContext(c), new java.lang.ref.WeakReference<>(help));
  parent.addView(split, index, original);
  PreferenceScreen root = fragment.getPreferenceScreen();
  Map<Preference, Boolean> visibility = new IdentityHashMap<>();
  for (int i = 0; i < root.getPreferenceCount(); i++) {
   Preference p = root.getPreference(i);
   if (p instanceof PreferenceGroup) {
    PreferenceGroup group = (PreferenceGroup) p;
    for (int k = 0; k < group.getPreferenceCount(); k++)
     visibility.put(group.getPreference(k), group.getPreference(k).isVisible());
   }
  }
  final TextView[] selectedRail = {null}, childOpener = {null};
  final Runnable[] returnToCategory = {null};
  List<TextView> railButtons = new ArrayList<>();
  boolean streaming = fragment.requireActivity().getIntent().getBooleanExtra("show_streaming_settings", false);
  fragment.requireActivity().getIntent().removeExtra("show_streaming_settings");
  TextView initial = null;
  for (String name : NAMES) {
   PreferenceCategory found = null;
   for (int i = 0; i < root.getPreferenceCount(); i++)
    if (root.getPreference(i) instanceof PreferenceCategory && name.contentEquals(root.getPreference(i).getTitle()))
     found = (PreferenceCategory) root.getPreference(i);
   final PreferenceCategory category = found;
   TextView button = sidebarButton(c, name, false);
   button.setTag("semantic:settings:category:" + name);
   links.addView(button, new LinearLayout.LayoutParams(-1, 0, 1));
   railButtons.add(button);
   Runnable showCategory = () -> {
    children.removeAllViews(); childOpener[0] = null;
    selectedRail[0] = button; list.setTag(button);
    for (int i = 0; i < root.getPreferenceCount(); i++) root.getPreference(i).setVisible(root.getPreference(i) == category);
    if (category != null) for (int k = 0; k < category.getPreferenceCount(); k++) {
     Preference p = category.getPreference(k);
     p.setVisible(Boolean.TRUE.equals(visibility.get(p)) && !(p instanceof PreferenceCategory));
    }
    help.setText(name + "\n\nPress OK or Right to enter this category.");
    list.scrollToPosition(0);
   };
   Runnable enter = () -> {
    showCategory.run();
    if (category != null) for (int k = 0; k < category.getPreferenceCount(); k++) {
     Preference p = category.getPreference(k);
     if (!(p instanceof PreferenceCategory) || !Boolean.TRUE.equals(visibility.get(p))) continue;
     TextView child = sidebarButton(c, String.valueOf(p.getTitle()), false);
     child.setTag("semantic:settings:section:" + p.getKey());
     children.addView(child, new LinearLayout.LayoutParams(-1, dp(fragment, 40)));
     Runnable openChild = () -> {
      childOpener[0] = child; children.setVisibility(View.GONE); list.setTag(child);
      for (int n = 0; n < category.getPreferenceCount(); n++) category.getPreference(n).setVisible(category.getPreference(n) == p);
      help.setText(String.valueOf(p.getTitle())); list.scrollToPosition(0); focusFirst(list);
     };
     child.setOnClickListener(v -> openChild.run());
     child.setOnKeyListener((v, key, event) -> {
      if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
      if (key == KeyEvent.KEYCODE_DPAD_RIGHT) { openChild.run(); return true; }
      if (key == KeyEvent.KEYCODE_DPAD_LEFT) { button.requestFocus(); return true; }
      return false;
     });
    }
    children.setVisibility(View.VISIBLE);
    if (children.getChildCount() > 0) children.getChildAt(0).requestFocus(); else focusFirst(list);
   };
   button.setOnFocusChangeListener((v, focused) -> { if (focused) { showCategory.run(); returnToCategory[0] = enter; } });
   button.setOnClickListener(v -> enter.run());
   button.setOnKeyListener((v, key, event) -> {
    if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
    if (key == KeyEvent.KEYCODE_DPAD_RIGHT) { enter.run(); return true; }
    if (key == KeyEvent.KEYCODE_DPAD_LEFT) return true;
    int position = railButtons.indexOf(button);
    if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
     if (position + 1 < railButtons.size()) railButtons.get(position + 1).requestFocus();
     return true;
    }
    if (key == KeyEvent.KEYCODE_DPAD_UP) {
     if (position > 0) railButtons.get(position - 1).requestFocus();
     else { View ancestor = split; while (ancestor.getParent() instanceof View) { ancestor = (View) ancestor.getParent(); if (ancestor instanceof com.archos.mediacenter.video.leanback.TopNavigation) { ((com.archos.mediacenter.video.leanback.TopNavigation) ancestor).focusNavigation(); break; } } }
     return true;
    }
    return false;
   });
   if (initial == null || streaming && name.equals("Streaming")) initial = button;
  }
  androidx.activity.OnBackPressedCallback back = new androidx.activity.OnBackPressedCallback(false) {
   @Override public void handleOnBackPressed() {
    if (childOpener[0] != null && returnToCategory[0] != null) {
     String label = childOpener[0].getText().toString(); returnToCategory[0].run();
     for (int i = 0; i < children.getChildCount(); i++) {
      TextView child = (TextView) children.getChildAt(i);
      if (label.contentEquals(child.getText())) { child.requestFocus(); break; }
     }
    } else if (selectedRail[0] != null) selectedRail[0].requestFocus();
   }
  };
  fragment.requireActivity().getOnBackPressedDispatcher().addCallback(fragment.getViewLifecycleOwner(), back);
  split.getViewTreeObserver().addOnGlobalFocusChangeListener((oldView, newView) -> back.setEnabled(middle.hasFocus()));
  if (initial != null) { TextView first = initial; first.post(first::requestFocus); }
 }
 private static TextView sidebarButton(android.content.Context c,String name,boolean child){TextView button=new TextView(c);button.setText(name);button.setTextSize(13);button.setTextColor(android.graphics.Color.WHITE);button.setGravity(Gravity.CENTER_VERTICAL);button.setPadding(PreviewDialog.dp(c,child?24:10),0,PreviewDialog.dp(c,10),0);PreviewIcon.apply(button,name,17);button.setFocusable(true);button.setFocusableInTouchMode(true);button.setBackground(PreviewDialog.focus(c));return button;}
 private static android.content.Context activityContext(android.content.Context c){while(c instanceof android.content.ContextWrapper&&!(c instanceof android.app.Activity))c=((android.content.ContextWrapper)c).getBaseContext();return c;}
 private static int dp(PreferenceFragmentCompat f,int v){return Math.round(v*f.getResources().getDisplayMetrics().density);}
}
