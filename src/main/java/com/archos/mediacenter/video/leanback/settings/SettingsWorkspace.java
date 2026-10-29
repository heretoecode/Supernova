package com.archos.mediacenter.video.leanback.settings;

import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.preference.*;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.foundation.FoundationAboutWorkspace;
import java.util.*;

/** New grid/category presentation. The preference hierarchy is a retained model, never the visible UI. */
final class SettingsWorkspace extends LinearLayout implements TopNavigation.ContentFocus {
    private final VideoSettingsFragment.PrefsFragment fragment;
    private final BackupSettingsController backup;
    private final SettingsRegistry registry;
    private final SharedPreferences prefs;
    private final FrameLayout stage;
    private final Map<String,Row> bindings=new LinkedHashMap<>();
    private final Map<Integer,String> sections=new HashMap<>();
    private final Map<String,String> anchors=new HashMap<>();
    private final List<View> cards=new ArrayList<>();
    private final List<TextView> railButtons=new ArrayList<>();
    private LinearLayout body,rail,right;
    private ScrollView rightScroll;
    private FoundationAboutWorkspace about;
    private RecyclerView.Adapter<?> observed;
    private int category=-1,gridSelection;
    private String section="";
    private boolean queued,disposed,subtitleEnabled;
    private View rowAnchor;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceObserver=(p,k)->schedule();
    private final RecyclerView.AdapterDataObserver observer=new RecyclerView.AdapterDataObserver(){
        @Override public void onChanged(){schedule();}
        @Override public void onItemRangeChanged(int start,int count){schedule();}
        @Override public void onItemRangeInserted(int start,int count){schedule();}
        @Override public void onItemRangeRemoved(int start,int count){schedule();}
    };
    static boolean enabled(Context c){return com.archos.mediacenter.video.BuildConfig.FOUNDATION||PreferenceManager.getDefaultSharedPreferences(c).getBoolean("try_new_ui",false);}
    SettingsWorkspace(VideoSettingsFragment.PrefsFragment fragment,BackupSettingsController backup,Bundle state){
        super(fragment.requireContext());this.fragment=fragment;this.backup=backup;registry=new SettingsRegistry(getContext());prefs=PreferenceManager.getDefaultSharedPreferences(getContext());
        setTag("settings.workspace");setOrientation(VERTICAL);setBackground(new PreviewUtilityBackground(getContext()));setPadding(dp(28),dp(20),dp(28),dp(20));setClipChildren(false);
        addView(text("Settings",28),new LayoutParams(-1,-2));
        stage=new FrameLayout(getContext());LayoutParams area=new LayoutParams(-1,0,1);area.topMargin=dp(4);addView(stage,area);
        prefs.registerOnSharedPreferenceChangeListener(preferenceObserver);
        post(()->{if(disposed)return;observed=fragment.getListView().getAdapter();if(observed!=null)observed.registerAdapterDataObserver(observer);});
        Bundle saved=state==null?null:state.getBundle("supernova_settings_workspace");
        int requested=saved==null?fragment.requireActivity().getIntent().getIntExtra("supernova_settings_category",-1):saved.getInt("category",-1);
        String requestedSection=saved==null?fragment.requireActivity().getIntent().getStringExtra("supernova_settings_section"):saved.getString("section");
        if(saved!=null){gridSelection=saved.getInt("grid",0);for(int i=0;i<8;i++)if(saved.containsKey("section_"+i))sections.put(i,saved.getString("section_"+i));String anchor=saved.getString("row");if(anchor!=null&&requested>=0)anchors.put(requested+":"+requestedSection,anchor);}
        String legacy=fragment.requireActivity().getIntent().getStringExtra("preview_settings_category");
        if(fragment.requireActivity().getIntent().getBooleanExtra("show_streaming_settings",false)||"Streaming".equals(legacy))requested=3;
        else if(legacy!=null){if(Arrays.asList("Playback","Video").contains(legacy))requested=0;else if(Arrays.asList("Audio","Subtitles").contains(legacy))requested=1;else if(Arrays.asList("Home","Library & Metadata","Library").contains(legacy))requested=2;else if(legacy.equals("Appearance"))requested=4;else if(Arrays.asList("Network","Integrations").contains(legacy))requested=5;else if(legacy.equals("Advanced")){requested=6;requestedSection="Diagnostics";}else if(legacy.equals("About"))requested=7;}
        fragment.requireActivity().getIntent().removeExtra("preview_settings_category");fragment.requireActivity().getIntent().removeExtra("show_streaming_settings");
        if(requested>=0&&requested<8)open(requested,requestedSection);else showGrid();
    }
    @Override public boolean focusFromNavigation(){
        if(category<0)return !cards.isEmpty()&&cards.get(Math.max(0,Math.min(7,gridSelection))).requestFocus();
        return selectedRail().requestFocus();
    }
    boolean atNavigationBoundary(){
        View focused=findFocus();
        if(category<0)return cards.indexOf(focused)>=0&&cards.indexOf(focused)<4;
        if(!railButtons.isEmpty()&&focused==railButtons.get(0))return true;
        return false;
    }
    private float scale(){return Math.max(.5f,Math.min(1.25f,Math.min(getResources().getConfiguration().screenWidthDp/960f,getResources().getConfiguration().screenHeightDp/540f)));}
    private int dp(float value){return Math.max(1,Math.round(value*scale()*getResources().getDisplayMetrics().density));}
    private TextView text(String value,int size){TextView v=SharedThreePanel.text(getContext(),value,size);v.setTextSize(size*scale());v.setIncludeFontPadding(false);return v;}
    private void schedule(){if(queued||disposed)return;queued=true;post(()->{queued=false;if(!disposed&&category>=0){if(section.equals("OpenSubtitles")&&subtitleEnabled!=prefs.getBoolean("supernova_integration_opensubtitles",true))select(section);else if(!special())renderRows();}});}
    private boolean special(){return category==7||section.equals("Backup & restore")||section.equals("OpenSubtitles");}
    private void clearPage(){if(backup.isVisible())backup.hide();if(about!=null){about.hide();about=null;}bindings.clear();railButtons.clear();stage.removeAllViews();}
    private void remember(){if(category<0)return;View focused=findFocus();if(focused!=null&&focused.getTag() instanceof String)anchors.put(category+":"+section,(String)focused.getTag());sections.put(category,section);}
    void showGrid(){
        remember();clearPage();category=-1;section="";cards.clear();fragment.requireActivity().getIntent().putExtra("supernova_settings_category",-1);
        ScrollView scroll=new ScrollView(getContext());scroll.setFillViewport(true);scroll.setClipChildren(false);scroll.setClipToPadding(false);scroll.setVerticalScrollBarEnabled(false);
        LinearLayout grid=new LinearLayout(getContext()){@Override protected void onMeasure(int w,int h){int available=MeasureSpec.getSize(h);int minimum=getResources().getConfiguration().fontScale>1.2f?dp(500*getResources().getConfiguration().fontScale):available;super.onMeasure(w,MeasureSpec.makeMeasureSpec(Math.max(available,minimum),MeasureSpec.EXACTLY));}};
        grid.setTag("settings.grid");grid.setOrientation(VERTICAL);grid.setClipChildren(false);scroll.addView(grid,new ScrollView.LayoutParams(-1,-1));stage.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
        for(int r=0;r<2;r++){
            LinearLayout line=new LinearLayout(getContext());line.setBaselineAligned(false);line.setClipChildren(false);LayoutParams rowSize=new LayoutParams(-1,0,1);if(r==0)rowSize.bottomMargin=dp(12);grid.addView(line,rowSize);
            for(int col=0;col<4;col++){
                final int index=r*4+col;LinearLayout card=new LinearLayout(getContext());card.setOrientation(VERTICAL);card.setGravity(Gravity.CENTER_VERTICAL);card.setPadding(dp(16),dp(14),dp(16),dp(14));card.setFocusable(true);card.setFocusableInTouchMode(true);card.setBackground(SharedThreePanel.control(getContext(),9));card.setTag("settings.grid."+index);card.setContentDescription(SettingsRegistry.CATEGORIES[index]+". "+SettingsRegistry.DESCRIPTIONS[index]);
                ImageView icon=new ImageView(getContext());icon.setImageDrawable(new PreviewIcon(SettingsRegistry.ICONS[index]));card.addView(icon,new LayoutParams(dp(30),dp(30)));
                TextView title=text(SettingsRegistry.CATEGORIES[index],20);title.setTypeface(null,android.graphics.Typeface.BOLD);LayoutParams titleSize=new LayoutParams(-1,-2);titleSize.topMargin=dp(10);card.addView(title,titleSize);
                TextView description=text(SettingsRegistry.DESCRIPTIONS[index],15);description.setTextColor(0xffbcc9d9);description.setLineSpacing(dp(2),1);LayoutParams descSize=new LayoutParams(-1,-2);descSize.topMargin=dp(6);card.addView(description,descSize);
                LayoutParams size=new LayoutParams(0,-1,1);if(col<3)size.rightMargin=dp(12);line.addView(card,size);cards.add(card);
                card.setOnClickListener(v->open(index,null));card.setOnFocusChangeListener((v,yes)->{if(yes)gridSelection=index;});
                card.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;int next=index;if(key==KeyEvent.KEYCODE_DPAD_LEFT&&index%4>0)next--;else if(key==KeyEvent.KEYCODE_DPAD_RIGHT&&index%4<3)next++;else if(key==KeyEvent.KEYCODE_DPAD_UP&&index>=4)next-=4;else if(key==KeyEvent.KEYCODE_DPAD_DOWN&&index<4)next+=4;else if(key!=KeyEvent.KEYCODE_DPAD_LEFT&&key!=KeyEvent.KEYCODE_DPAD_RIGHT&&key!=KeyEvent.KEYCODE_DPAD_UP&&key!=KeyEvent.KEYCODE_DPAD_DOWN)return false;cards.get(next).requestFocus();return true;});
            }
        }
        cards.get(Math.max(0,Math.min(7,gridSelection))).requestFocus();
    }
    void open(int index,String requestedSection){
        remember();clearPage();category=index;gridSelection=index;List<String> names=registry.sections(index);section=requestedSection!=null&&names.contains(requestedSection)?requestedSection:sections.getOrDefault(index,names.get(0));if(!names.contains(section))section=names.get(0);
        LinearLayout columns=new LinearLayout(getContext());columns.setTag("settings.category");columns.setBaselineAligned(false);stage.addView(columns,new FrameLayout.LayoutParams(-1,-1));
        ScrollView left=new ScrollView(getContext());left.setFillViewport(false);left.setVerticalScrollBarEnabled(false);rail=new PreviewFocusRail(getContext());rail.setOrientation(VERTICAL);rail.setPadding(dp(12),dp(12),dp(12),dp(12));left.addView(rail);LayoutParams leftSize=new LayoutParams(0,-1,.28f);leftSize.rightMargin=dp(12);columns.addView(left,leftSize);
        TextView identity=text(SettingsRegistry.CATEGORIES[index],22);identity.setTypeface(null,android.graphics.Typeface.BOLD);PreviewIcon.apply(identity,SettingsRegistry.ICONS[index],28);rail.addView(identity);TextView copy=text(SettingsRegistry.DESCRIPTIONS[index],15);copy.setTextColor(0xffbcc9d9);copy.setPadding(0,dp(10),0,dp(16));rail.addView(copy);
        right=new LinearLayout(getContext());right.setOrientation(VERTICAL);right.setPadding(dp(16),dp(12),dp(16),dp(12));columns.addView(right,new LayoutParams(0,-1,.72f));
        for(String name:names){TextView link=text(name,18);String explanation=subsectionDescription(name);android.text.SpannableString caption=new android.text.SpannableString(name+"\n"+explanation);caption.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),0,name.length(),0);caption.setSpan(new android.text.style.RelativeSizeSpan(.8f),name.length()+1,caption.length(),0);caption.setSpan(new android.text.style.ForegroundColorSpan(0xffbcc9d9),name.length()+1,caption.length(),0);link.setText(caption);PreviewIcon.apply(link,subsectionIcon(name),Math.round(26*scale()));link.setPadding(dp(12),dp(14),dp(12),dp(14));link.setMinHeight(dp(52));link.setFocusable(true);link.setFocusableInTouchMode(true);link.setBackground(SharedThreePanel.control(getContext(),8));link.setTag("settings.section."+name);LayoutParams size=new LayoutParams(-1,-2);size.bottomMargin=dp(6);rail.addView(link,size);railButtons.add(link);
            link.setOnFocusChangeListener((v,yes)->{if(yes&&!section.equals(name))select(name);});link.setOnClickListener(v->{if(!section.equals(name))select(name);enter();});
            link.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_RIGHT){enter();return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){int n=railButtons.indexOf(link)+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(n>=0&&n<railButtons.size())railButtons.get(n).requestFocus();return true;}return false;});
        }
        select(section);selectedRail().requestFocus();
    }
    private TextView selectedRail(){for(TextView v:railButtons)if(("settings.section."+section).equals(v.getTag()))return v;return railButtons.get(0);}
    private void select(String name){
        remember();if(backup.isVisible())backup.hide();if(about!=null){about.hide();about=null;}section=name;sections.put(category,name);bindings.clear();right.removeAllViews();
        fragment.requireActivity().getIntent().putExtra("supernova_settings_category",category).putExtra("supernova_settings_section",section);
        for(TextView v:railButtons){boolean selected=("settings.section."+section).equals(v.getTag());v.setSelected(selected);v.setTextColor(Color.WHITE);android.graphics.drawable.GradientDrawable surface=new android.graphics.drawable.GradientDrawable();surface.setColor(selected?0xbb10283b:0x5506111f);surface.setCornerRadius(dp(8));v.setBackground(surface);if(android.os.Build.VERSION.SDK_INT>=26)v.setDefaultFocusHighlightEnabled(false);if(selected){android.graphics.drawable.GradientDrawable outline=new android.graphics.drawable.GradientDrawable();outline.setColor(Color.TRANSPARENT);outline.setCornerRadius(dp(8));outline.setStroke(dp(1.5f),PreviewAccent.color(getContext()));v.setForeground(outline);}else v.setForeground(null);}
        if(category==7){LinearLayout shadowRail=new LinearLayout(getContext()),host=new LinearLayout(getContext());right.addView(host,new LayoutParams(-1,-1));about=new FoundationAboutWorkspace(getContext(),host,new View(getContext()),new View(getContext()),shadowRail);about.useSingleContent(()->selectedRail().requestFocus());TextView parent=text("App information",18);shadowRail.addView(parent);about.show(parent);int position=registry.sections(7).indexOf(section);if(position>0)about.openSection(position-1);return;}
        if(section.equals("Backup & restore")){backup.attachTwoColumn(right);backup.show(selectedRail());return;}
        TextView heading=text(section,24);heading.setTypeface(null,android.graphics.Typeface.BOLD);right.addView(heading);TextView description=text(sectionDescription(),15);description.setTextColor(0xffbcc9d9);description.setPadding(0,dp(6),0,dp(12));right.addView(description);
        rightScroll=new ScrollView(getContext());rightScroll.setFillViewport(false);rightScroll.setVerticalScrollBarEnabled(false);body=new LinearLayout(getContext());body.setOrientation(VERTICAL);rightScroll.addView(body);right.addView(rightScroll,new LayoutParams(-1,0,1));
        if(section.equals("OpenSubtitles")){subtitleEnabled=prefs.getBoolean("supernova_integration_opensubtitles",true);
            if(!prefs.getBoolean("supernova_integration_opensubtitles",true)){body.addView(text("OpenSubtitles is disabled. Enable it in Service integrations to use the account form.",16));return;}
            TextView information=text("",15);LinearLayout form=new LinearLayout(getContext());form.setOrientation(VERTICAL);body.addView(form);body.addView(information);new InlineOpenSubtitles(getContext(),form,information);return;
        }
        renderRows();
    }
    private String subsectionDescription(String name){
        switch(name){case "Audio output & tracks":return "Configure audio output, playback compatibility and preferred audio tracks.";case "Library & metadata":return "Choose metadata sources, identification and library display behaviour.";case "Interface & theme":return "Set interface language, theme and accent colours.";case "Streaming services":return "Choose your region, providers and streaming availability.";case "App information":return "Installed Supernova version and build details.";case "Release Notes":return "Changes and delivery history for Supernova builds.";case "Open-source Licences":return "Licences and notices for bundled components.";case "Credits & Acknowledgements":return "Contributors and services behind Supernova.";case "Technical Information":return "Device, decoder and runtime information.";case "Playback":return "Playback speed, external players and projector mode.";case "Video & Display":return "Refresh-rate switching, Dolby Vision and display behaviour.";case "Resume & Progress":return "Resume behaviour and playback progress settings.";case "Diagnostics":return "Local logging, problem references and report export.";case "Backup & restore":return "Export configuration or review a saved backup.";case "Service integrations":return "Enable services while retaining saved accounts.";case "Trakt":return "Account, scrobbling and supported synchronisation.";case "OpenSubtitles":return "Subtitle account and sign-in.";case "IntroDB":return "Intro, recap, preview and credit policies.";case "Home":return "Featured sources and the Home row editor.";case "Watch history":return "Recording and clearing viewing history.";case "Subtitles":return "Default display, languages and search preparation.";case "Network connections":return "Connection preferences and trusted server identities.";default:return name.toLowerCase(Locale.ROOT).contains("compatibility")?"Device-dependent advanced options.":category==7?"Read bundled Supernova information.":"Manage the existing "+name.toLowerCase(Locale.ROOT)+" options.";}
    }
    private String subsectionIcon(String name){String lower=name.toLowerCase(Locale.ROOT);return lower.contains("video")||lower.contains("display")?"tv":lower.contains("audio")?"audio":lower.contains("sub")?"subtitles":lower.contains("network")?"network":lower.contains("playback")?"play":lower.contains("resume")||lower.contains("history")?"history":lower.contains("home")?"home":lower.contains("backup")?"folder":lower.contains("integration")||Arrays.asList("Trakt","IntroDB").contains(name)?"settings.nodes":"info";}
    private String sectionDescription(){if(category==0&&section.equals("Playback"))return "Control general playback behaviour and how content starts, plays and ends.";if(section.toLowerCase(Locale.ROOT).contains("compatibility"))return "Existing device and advanced-mode options. Change these only when needed for your media or connection.";if(section.equalsIgnoreCase("Audio output & tracks"))return "Configure audio output, playback compatibility and preferred audio tracks.";return subsectionDescription(section);}
    private boolean integrationDisabled(){return section.equals("Trakt")&&!prefs.getBoolean("supernova_integration_trakt",true)||section.equals("IntroDB")&&!prefs.getBoolean(com.archos.mediacenter.video.player.SegmentSkippingPolicy.MASTER,true);}
    private void refreshStoredValues(){
        // External restores can update SharedPreferences while the native model stays alive.
        // Setters reconcile model/dependency state, but never replay change/action listeners.
        PreferenceScreen screen=fragment.getPreferenceScreen();
        for(SettingsRegistry.Entry entry:registry.entries){
            Preference p=entry.resolve(screen);if(p==null||!p.isPersistent()||p.getKey()==null||p.getPreferenceDataStore()!=null)continue;
            SharedPreferences store=p.getSharedPreferences();if(store==null||!store.contains(p.getKey()))continue;
            Object value=store.getAll().get(p.getKey());
            if(p instanceof TwoStatePreference&&value instanceof Boolean)((TwoStatePreference)p).setChecked((Boolean)value);
            else if(p instanceof ListPreference&&value instanceof String)((ListPreference)p).setValue((String)value);
            else if(p instanceof MultiSelectListPreference&&value instanceof Set&&!((MultiSelectListPreference)p).getValues().equals(value))((MultiSelectListPreference)p).setValues(new HashSet<>((Set<String>)value));
            else if(p instanceof EditTextPreference&&value instanceof String&&!Objects.equals(((EditTextPreference)p).getText(),value))((EditTextPreference)p).setText((String)value);
        }
        TwoStatePreference inverse=screen.findPreference("subtitles_hide_default"),positive=screen.findPreference("preview_subtitles_by_default");
        if(inverse!=null&&positive!=null){positive.setChecked(!inverse.isChecked());for(String key:new String[]{"favSubLang","subtitles_show_full_for_local_audio"}){Preference dependent=screen.findPreference(key);if(dependent!=null){dependent.setEnabled(!inverse.isChecked());dependent.setSelectable(!inverse.isChecked());}}}
    }
    private void renderRows(){
        if(body==null||special())return;refreshStoredValues();
        List<SettingsRegistry.Entry> candidates=new ArrayList<>();
        if(!integrationDisabled())for(SettingsRegistry.Entry e:registry.in(category,section)){Preference p=e.resolve(fragment.getPreferenceScreen());if(SettingsRegistry.visible(p,e.integration()))candidates.add(e);}
        List<String> ids=new ArrayList<>();for(SettingsRegistry.Entry e:candidates)ids.add(e.id);
        boolean same=ids.equals(new ArrayList<>(bindings.keySet()));if(same)for(SettingsRegistry.Entry e:candidates)if(bindings.get(e.id).preference!=e.resolve(fragment.getPreferenceScreen())){same=false;break;}
        if(!same||bindings.isEmpty()){
            View focused=body.findFocus();int focusedIndex=new ArrayList<>(bindings.values()).indexOf(focused);String anchor=focused==null?anchors.get(category+":"+section):String.valueOf(focused.getTag());Map<String,Row> previous=new LinkedHashMap<>(bindings);bindings.clear();body.removeAllViews();
            for(SettingsRegistry.Entry e:candidates){Preference p=e.resolve(fragment.getPreferenceScreen());Row row=previous.get(e.id);if(row==null||row.preference!=p)row=new Row(e,p);bindings.put(e.id,row);body.addView(row,new LayoutParams(-1,-2));}
            if(candidates.isEmpty())body.addView(text(integrationDisabled()?"This service is disabled. Enable it in Service integrations to use its options.":"No additional options apply to this device or account. Existing saved values are retained.",16));
            if(anchor!=null){View restore=body.findViewWithTag(anchor);if(focused!=null){if(restore!=null)restore.requestFocus();else if(!bindings.isEmpty()){List<Row> remaining=new ArrayList<>(bindings.values());remaining.get(Math.max(0,Math.min(focusedIndex,remaining.size()-1))).requestFocus();}else selectedRail().requestFocus();}}
        }
        for(Row row:bindings.values())row.refresh();
    }
    private final class Row extends LinearLayout {
        final SettingsRegistry.Entry entry;final Preference preference;final TextView title,description,value;final androidx.appcompat.widget.SwitchCompat toggle;
        // Intentional bridge to the pinned AndroidX 1.2.1 model: preserve its native click/dependency/action semantics.
        @android.annotation.SuppressLint("RestrictedApi")
        Row(SettingsRegistry.Entry entry,Preference preference){
            super(SettingsWorkspace.this.getContext());this.entry=entry;this.preference=preference;setOrientation(HORIZONTAL);setGravity(Gravity.CENTER_VERTICAL);setPadding(dp(14),dp(12),dp(14),dp(12));setMinimumHeight(Math.max(dp(64),Math.round(48*getResources().getDisplayMetrics().density)));setFocusable(true);setFocusableInTouchMode(true);setTag("settings.preference."+entry.actualKey(getContext()));com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(this,"settings:preference:"+entry.actualKey(getContext()));setBackground(SharedThreePanel.accentFocus(getContext()));SharedThreePanel.rowSeparator(this);
            LinearLayout copy=new LinearLayout(getContext());copy.setOrientation(VERTICAL);title=text("",19);title.setTypeface(null,android.graphics.Typeface.BOLD);description=text("",15);description.setTextColor(0xffbcc9d9);description.setPadding(0,dp(5),0,0);copy.addView(title);copy.addView(description);addView(copy,new LayoutParams(0,-2,1));
            value=text("",15);value.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);value.setPadding(dp(12),0,0,0);value.setMaxWidth(dp(170));addView(value,new LayoutParams(-2,-2));toggle=new androidx.appcompat.widget.SwitchCompat(getContext());toggle.setShowText(false);toggle.setTextOn("");toggle.setTextOff("");toggle.setFocusable(false);toggle.setClickable(false);toggle.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);LayoutParams toggleSize=new LayoutParams(dp(52),dp(48));toggleSize.leftMargin=dp(12);addView(toggle,toggleSize);PreviewSettings.styleSwitch(toggle);
            setOnClickListener(v->{if(!preference.isEnabled()||!preference.isSelectable())return;if(!PreviewPreferenceDialogs.show(fragment,preference))preference.performClick();refresh();schedule();});
            setOnFocusChangeListener((v,yes)->{if(yes){anchors.put(category+":"+section,String.valueOf(getTag()));rowAnchor=this;}});
            setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_LEFT){selectedRail().requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){List<Row> values=new ArrayList<>(bindings.values());int n=values.indexOf(this)+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(n>=0&&n<values.size())values.get(n).requestFocus();return true;}return false;});
        }
        void refresh(){
            title.setText(preference.getTitle());CharSequence summary=preference.getSummary();String help=summary==null?"":summary.toString();if(!preference.isEnabled())help+=(help.isEmpty()?"":"\n")+"Unavailable for the current device or account configuration.";description.setText(help);description.setVisibility(help.isEmpty()?GONE:VISIBLE);boolean state=preference instanceof TwoStatePreference;toggle.setVisibility(state?VISIBLE:GONE);value.setVisibility(state?GONE:VISIBLE);
            if(state)toggle.setChecked(((TwoStatePreference)preference).isChecked());else if(preference instanceof ListPreference){CharSequence selected=((ListPreference)preference).getEntry();value.setText(selected==null?"Not set":selected);}else if(preference instanceof MultiSelectListPreference)value.setText(((MultiSelectListPreference)preference).getValues().size()+" selected");else value.setText("›");
            setAlpha(preference.isEnabled()?1f:.55f);setContentDescription(title.getText()+". "+help+". "+(state?(toggle.isChecked()?"On":"Off"):value.getText()));
        }
    }
    void enter(){if(backup.isVisible()){backup.enter();return;}if(about!=null){about.enterContent();return;}String anchor=anchors.get(category+":"+section);View restore=anchor==null?null:right.findViewWithTag(anchor);if(restore!=null&&restore.isShown()&&restore.requestFocus())return;for(View v:right.getFocusables(FOCUS_FORWARD))if(v.isShown()&&v.requestFocus())return;}
    boolean back(){if(category<0)return false;if(backup.isVisible()&&backup.hasOperation())return backup.back();if(about!=null&&about.handleBack())return true;showGrid();return true;}
    void save(Bundle out){remember();Bundle state=new Bundle();state.putInt("category",category);state.putString("section",section);state.putInt("grid",gridSelection);state.putString("row",anchors.get(category+":"+section));for(Map.Entry<Integer,String> e:sections.entrySet())state.putString("section_"+e.getKey(),e.getValue());out.putBundle("supernova_settings_workspace",state);}
    @Override public boolean dispatchKeyEvent(KeyEvent event){if(category>=0&&event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT&&right.hasFocus()){if(about!=null&&about.handleBack())return true;selectedRail().requestFocus();return true;}if(backup.isVisible()&&backup.navigation(event))return true;return super.dispatchKeyEvent(event);}
    void dispose(){if(disposed)return;disposed=true;prefs.unregisterOnSharedPreferenceChangeListener(preferenceObserver);if(observed!=null)observed.unregisterAdapterDataObserver(observer);}
    @Override protected void onDetachedFromWindow(){dispose();super.onDetachedFromWindow();}
}
