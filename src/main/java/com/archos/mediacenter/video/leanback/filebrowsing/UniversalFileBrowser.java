package com.archos.mediacenter.video.leanback.filebrowsing;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.*;
import com.archos.filecorelibrary.ListingEngine;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.filecoreextension.upnp2.ListingEngineFactoryWithUpnp;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.VideosInFolderLoader;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import com.archos.mediacenter.video.leanback.details.VideoDetailsActivity;
import com.archos.mediacenter.video.leanback.details.VideoDetailsFragment;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import com.archos.mediacenter.video.utils.VideoUtils;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** The TV browser used by Home, Network & Files, protocol activities and provider hosts.
 * It lists through the existing FileCore engine; it never scans or deletes physical files itself. */
public final class UniversalFileBrowser extends LinearLayout {
    public static final String ALL_FILES = "preference_display_all_files";
    private static final String GRID = "supernova_browser_grid", SORT = "supernova_browser_sort";
    private final SharedPreferences prefs;
    private final BrowserSelection selection;
    private final LinearLayout left, centre, right, toolbar, crumbs, items, actions;
    private final FrameLayout itemScroll;private final ScrollView informationScroll, contextScroll;
    private final PreviewFocusRecycler fileList;private final GridLayoutManager fileLayout;private final FileAdapter fileAdapter=new FileAdapter();
    private final Map<String,int[]> listPositions=new HashMap<>();private String sourceFingerprint="";
    private final HorizontalScrollView breadcrumbScroll;
    private final TextView information, heading, status, overview;private final LinearLayout facts;private final TextView[] factValues=new TextView[4];private ScrollView addressScroll;
    private final ImageView poster;private final ProgressBar loading;
    private final List<TextView> locations = new ArrayList<>();
    private final Map<String,String> rememberedItems = new HashMap<>();
    private final Map<String,Integer> rememberedScroll = new HashMap<>();
    private final Map<String,Video> indexed = new HashMap<>();
    private final Map<String,Shortcut> savedSources=new HashMap<>();
    private final ExecutorService metadata = Executors.newSingleThreadExecutor(r->{Thread thread=new Thread(r,"SupernovaBrowserMetadata");thread.setDaemon(true);return thread;});
    private List<MetaFile2> files = new ArrayList<>();
    private Uri current, selectedUri;
    private MetaFile2 selectedFile;
    private View leftAnchor, centreAnchor;
    private ListingEngine engine;
    private int generation;
    private boolean closed, listingFailed;private TextView scanOverview;private final android.os.Handler scanHandler=new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable scanRefresh=new Runnable(){public void run(){if(scanOverview!=null&&isAttachedToWindow())scanOverview.setText(PreviewLibraryScan.overview(getContext()));if(isAttachedToWindow())scanHandler.postDelayed(this,1000);}};
    private Consumer<String> networkAction = ignored -> {};
    private Consumer<Exception> credentials;
    private Consumer<Uri> locationChanged = ignored -> {};
    private View suppliedDock;
    private Consumer<Uri> directoryChoice;
    private boolean legacyRootsReady,legacyRootsLoading;
    private boolean onboarding,configurationActive;
    private SharedThreePanel panels;private TextView localParent,healthParent;private final List<TextView> localChildren=new ArrayList<>(),healthChildren=new ArrayList<>();private boolean localExpanded,healthExpanded;private LibraryHealthPanel healthPanel;private com.archos.filecorelibrary.samba.SambaDiscovery discovery;private com.archos.filecorelibrary.samba.SambaDiscovery.Listener discoveryListener;private com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager.Listener upnpListener;private final LinkedHashMap<String,String> discovered=new LinkedHashMap<>();private final String[] connection=new String[]{"","","/","","",""};private int protocol;private boolean savePassword=true,showPassword;private final List<Shortcut> shares=new ArrayList<>();private ScrollView inlineScroll;private com.archos.mediacenter.video.streaming.putio.PutioAccountController putioController;
    public void onboarding(boolean value){if(onboarding==value)return;onboarding=value;sourceFingerprint="";if(value){remember();current=null;stopListing();overview.setVisibility(GONE);((TextView)left.getChildAt(0)).setText("Local Storage");toolbar.setVisibility(GONE);breadcrumbScroll.setVisibility(GONE);centreHeading("Choose Your Media Folders");status.setText("Choose a local drive to browse its folders.");facts.setVisibility(GONE);poster.setVisibility(GONE);heading.setText("Choose Your Media Folders");information.setText("Select folder checkboxes to include media. Use Browse Folder to choose folders inside a selection. You can select folders across your drives before choosing Save & Finish. Scanning begins only after confirmation.");actions.removeAllViews();}else{overview.setVisibility(VISIBLE);((TextView)left.getChildAt(0)).setText("Locations");}}

    public UniversalFileBrowser(Context c) {
        super(c); setOrientation(VERTICAL); setClipChildren(false); setClipToPadding(false);
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
        selection = new BrowserSelection(prefs.getStringSet(BrowserSelection.ROOTS, Collections.emptySet()),
                prefs.getStringSet(BrowserSelection.EXCLUSIONS, Collections.emptySet()));
        seedLegacyRoots();
        left = column(); centre = column(); right = column(); toolbar = new LinearLayout(c);
        panels = new SharedThreePanel(c);
        ScrollView leftScroll = new ScrollView(c); leftScroll.addView(left);
        contextScroll = new ScrollView(c); contextScroll.setFillViewport(true); contextScroll.addView(right);
        panels.panels(leftScroll, centre, contextScroll);panels.widths(.28f,.44f,.28f); addView(panels, new LayoutParams(-1,-1));
        SharedThreePanel.heading(left,"Locations");
        overview = location("Overview", this::showOverview); leftAnchor = overview;
        overview.setTag("semantic:network.category.overview");
        TextView listMode = action("List / Grid", this::toggleGrid);listMode.setTag("browser.view");
        TextView sort = action("Sort", this::chooseSort);
        androidx.appcompat.widget.SwitchCompat all=new androidx.appcompat.widget.SwitchCompat(c);all.setShowText(false);all.setTextOn("ON");all.setTextOff("OFF");all.setText("All Files");all.setTextColor(-1);all.setFocusable(true);all.setFocusableInTouchMode(true);all.setBackground(SharedThreePanel.focus(c));all.setChecked(prefs.getBoolean(ALL_FILES,false));all.setThumbTintList(android.content.res.ColorStateList.valueOf(-1));all.setTrackTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{PreviewAccent.color(c),0xff59616b}));all.setOnCheckedChangeListener((button,checked)->prefs.edit().putBoolean(ALL_FILES,checked).apply());
        all.setTag("browser.all_files");
        for(TextView button : new TextView[]{listMode,sort,all}) {
            button.setTextSize(14); button.setGravity(android.view.Gravity.CENTER);
            button.setBackground(SharedThreePanel.control(c,25));
            LayoutParams size=new LayoutParams(0,dp(40),1);size.leftMargin=dp(3);size.rightMargin=dp(3);
            toolbar.addView(button,size);
        }
        centre.addView(toolbar,new LayoutParams(-1,-2));
        crumbs = new LinearLayout(c); breadcrumbScroll = new HorizontalScrollView(c);
        breadcrumbScroll.setHorizontalScrollBarEnabled(false); breadcrumbScroll.addView(crumbs);
        centre.addView(breadcrumbScroll,new LayoutParams(-1,dp(50)));
        status = SharedThreePanel.text(c,"",15); status.setMinHeight(dp(26));LinearLayout progress=new LinearLayout(c);progress.setGravity(android.view.Gravity.CENTER_VERTICAL);loading=new ProgressBar(c);loading.setIndeterminateDrawable(new com.archos.mediacenter.video.foundation.DoubleRingDrawable());loading.setVisibility(INVISIBLE);progress.addView(loading,new LayoutParams(dp(26),dp(26)));progress.addView(status,new LayoutParams(0,-2,1));centre.addView(progress);
        items = column();itemScroll=new FrameLayout(c);itemScroll.addView(items,new FrameLayout.LayoutParams(-1,-1));
        fileList=new PreviewFocusRecycler(c);fileLayout=new GridLayoutManager(c,prefs.getBoolean(GRID,false)?2:1);fileList.setLayoutManager(fileLayout);fileList.setAdapter(fileAdapter);fileList.setItemAnimator(null);fileList.setClipToPadding(false); centre.addView(itemScroll,new LayoutParams(-1,0,1));
        SharedThreePanel.heading(right,"Information");heading = SharedThreePanel.text(c,"Overview",21); right.addView(heading);SharedThreePanel.divider(right);
        poster=new ImageView(c);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);poster.setVisibility(GONE);right.addView(poster,new LayoutParams(-1,dp(120)));
        facts=column();String[] labels={"Name","Type","Address","Library status"};for(int i=0;i<labels.length;i++){LinearLayout row=new LinearLayout(c);row.setGravity(android.view.Gravity.TOP);TextView label=SharedThreePanel.text(c,labels[i],16);label.setTextColor(0xffb7bdc6);row.addView(label,new LayoutParams(dp(92),-2));factValues[i]=SharedThreePanel.text(c,"",16);ScrollView value=new ScrollView(c);if(i==2){addressScroll=value;value.setTag("semantic:network.address");value.setBackground(SharedThreePanel.focus(c));value.setFocusableInTouchMode(true);}value.setFocusable(i==2);value.setFocusableInTouchMode(i==2);value.addView(factValues[i]);row.addView(value,new LayoutParams(0,dp(i==2?60:36),1));facts.addView(row,new LayoutParams(-1,-2));}facts.setVisibility(GONE);right.addView(facts,new LayoutParams(-1,-2));
        information = SharedThreePanel.text(c,"",16);
        informationScroll = new ScrollView(c) {
            @Override protected void onSizeChanged(int width,int height,int oldWidth,int oldHeight){super.onSizeChanged(width,height,oldWidth,oldHeight);setClipBounds(new android.graphics.Rect(0,0,width,height));}
            @Override protected void onMeasure(int width, int height) {
                super.onMeasure(width, MeasureSpec.makeMeasureSpec(dp(144), MeasureSpec.AT_MOST));
            }
        };
        informationScroll.setFocusable(true); informationScroll.setFocusableInTouchMode(true);
        informationScroll.setBackground(SharedThreePanel.focus(c)); informationScroll.setOnFocusChangeListener((view,focused)->{if(focused)revealContext(view);}); informationScroll.addView(information);
        information.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence text,int start,int count,int after) {}
            public void onTextChanged(CharSequence text,int start,int before,int count) {}
            public void afterTextChanged(android.text.Editable text) {
                boolean readable=!text.toString().trim().isEmpty();
                informationScroll.setFocusable(readable); informationScroll.setFocusableInTouchMode(readable);
            }
        });
        right.addView(informationScroll,new LayoutParams(-1,-2));
        actions = column();ScrollView actionScroll=new ScrollView(c);actionScroll.addView(actions);right.addView(actionScroll,new LayoutParams(-1,0,1));
        refreshControls(); showOverview();
    }

    private void seedLegacyRoots(){
        legacyRootsReady=prefs.getBoolean("supernova_library_policy_initialized",false);
        if(legacyRootsReady)return;
        // Only actual indexed inventory is migrated; enumerating a drive never includes it.
        legacyRootsReady=prefs.contains("supernova_onboarding_complete");
    }

    private void readHistoricalRoots(){
        if(legacyRootsReady||legacyRootsLoading)return;
        legacyRootsLoading=true;
        metadata.execute(()->{
            try {Set<Uri> roots=com.archos.mediaprovider.video.SupernovaLibraryPolicy.historicalLocalRoots(getContext().getApplicationContext());
                post(()->{if(!closed){selection.seedExisting(roots);legacyRootsReady=true;legacyRootsLoading=false;}});
            }catch(RuntimeException failure){post(()->legacyRootsLoading=false);com.archos.mediacenter.video.diagnostics.Diagnostics.error("browser_legacy_roots",failure);}
        });
    }
    private void scanControls(){
        requestExit(()->{
            toolbar.setVisibility(GONE);breadcrumbScroll.setVisibility(GONE);remember();current=null;stopListing();items.removeAllViews();crumbs.removeAllViews();scanOverview=SharedThreePanel.text(getContext(),PreviewLibraryScan.overview(getContext()),16);scanOverview.setFocusable(true);scanOverview.setFocusableInTouchMode(true);scanOverview.setBackground(SharedThreePanel.focus(getContext()));items.addView(scanOverview);
            facts.setVisibility(GONE);heading.setText("Network Scanning");information.setText("Automatic checks use the existing scanner. Scans continue when you leave this page.");actions.removeAllViews();
            int period=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(getContext());
            addAction("Automatic: "+(period>0?"On":"Off"),()->{if(period>0)prefs.edit().putInt("preview_scan_frequency",period).apply();com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(getContext(),0,period>0?0:prefs.getInt("preview_scan_frequency",3600000),true);scanControls();actions.getChildAt(0).requestFocus();});
            addAction("Frequency: "+Math.max(15,(period>0?period:prefs.getInt("preview_scan_frequency",3600000))/60000)+" minutes",()->PreviewDialog.choose(getContext(),"Frequency",new String[]{"15 minutes","30 minutes","1 hour","6 hours","24 hours"},Arrays.asList(900000,1800000,3600000,21600000,86400000).indexOf(period>0?period:prefs.getInt("preview_scan_frequency",3600000)),n->{int value=new int[]{900000,1800000,3600000,21600000,86400000}[n];prefs.edit().putInt("preview_scan_frequency",value).apply();if(period>0)com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(getContext(),0,value,true);scanControls();actions.getChildAt(1).requestFocus();}));
            addAction("On open / return: "+(prefs.getBoolean("auto_rescan_on_app_restart",true)?"On":"Off"),()->{prefs.edit().putBoolean("auto_rescan_on_app_restart",!prefs.getBoolean("auto_rescan_on_app_restart",true)).apply();scanControls();actions.getChildAt(2).requestFocus();});
            addAction("Sources Included",()->PreviewNetworkScanning.sources(getContext()));addAction("Scan Now",()->PreviewLibraryScan.requestNetwork(getContext()));
        });
    }

    public void credentials(Consumer<Exception> handler, Consumer<Uri> changed) {
        credentials=handler; locationChanged=changed;
    }
    public BrowserSelection selections() { return selection; }
    /** Local directory result contract, sharing listing, breadcrumbs, focus and geometry. */
    public void directoryPicker(Consumer<Uri> selected){directoryChoice=selected;legacyRootsReady=true;toolbar.findViewWithTag("browser.all_files").setVisibility(GONE);}
    public Uri location() { return current; }
    public boolean atTop() { return !locations.isEmpty()&&locations.get(0).hasFocus(); }
    public boolean focusOverview() { return !locations.isEmpty()&&locations.get(0).requestFocus(); }
    /** Providers keep their account API and item adapter, within the same panel shell. */
    public void providerDock(View dock,Uri uri) {
        suppliedDock=dock;current=uri;
        if(dock.getParent() instanceof ViewGroup)((ViewGroup)dock.getParent()).removeView(dock);
        centre.removeView(itemScroll);centre.addView(dock,new LayoutParams(-1,0,1));
        toolbar.setVisibility(GONE);crumbs.removeAllViews();
        TextView provider=location(uri.getHost()==null?"Provider":uri.getHost(),()->{});provider.setOnClickListener(view->dock.requestFocus());leftAnchor=provider;
    }
    /** API-backed ancestry uses the same carousel, with provider callbacks rather than a transport engine. */
    public void providerPath(List<String> labels,List<Runnable> navigate) {
        if(suppliedDock==null||labels.size()!=navigate.size())throw new IllegalArgumentException("Provider ancestry mismatch");
        crumbs.removeAllViews();
        for(int n=0;n<labels.size();n++){
            TextView segment=action(labels.get(n),navigate.get(n));segment.setSingleLine(true);segment.setTag("provider.crumb."+n);
            crumbs.addView(segment,new LayoutParams(-2,dp(48)));
            if(n<labels.size()-1)crumbs.addView(SharedThreePanel.text(getContext()," › ",18));
        }
        breadcrumbScroll.post(()->breadcrumbScroll.fullScroll(FOCUS_RIGHT));
    }
    public void focusVideo(Video video){
        facts.setVisibility(GONE);heading.setText(video.getName());information.setText(video.getFilenameNonCryptic());
        if(video.getPosterUri()!=null){poster.setVisibility(VISIBLE);com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(poster,video.getPosterUri(),video.getId(),"network.browser","poster",com.squareup.picasso.Picasso.get().load(video.getPosterUri()).resize(dp(120),dp(120)).centerInside());}
    }
    public void providerInformation(String title,String details,boolean folder,Runnable open) {
        com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(poster);poster.setVisibility(GONE);poster.setImageDrawable(null);
        facts.setVisibility(GONE);heading.setText(title);information.setText(details);actions.removeAllViews();
        if(!folder)addAction("File Information",open);
    }

    /** Source updates preserve the open folder, transaction and exact centre selection. */
    public void locations(List<Box> volumes,List<Shortcut> sources,List<Shortcut> saved,Consumer<String> network) {
        networkAction=network;
        StringBuilder inventory=new StringBuilder();for(Box box:volumes)inventory.append(box.getBoxId()).append(box.getPath()).append(box.getName());for(Shortcut source:sources)inventory.append(source.getId()).append(source.getUri()).append(source.getName());for(Shortcut source:saved)inventory.append(source.getId()).append(source.getUri()).append(source.getName());
        if((onboarding+inventory.toString()).equals(sourceFingerprint)&&left.getChildCount()>2)return;sourceFingerprint=onboarding+inventory.toString();
        Object anchorTag=leftAnchor==null?null:leftAnchor.getTag();boolean leftFocused=left.hasFocus();
        shares.clear();Set<String> knownShares=new HashSet<>();for(Shortcut source:sources)if(knownShares.add(BrowserSelection.canonical(source.getUri())))shares.add(source);for(Shortcut source:saved)if(knownShares.add(BrowserSelection.canonical(source.getUri())))shares.add(source);if(!onboarding&&!prefs.getBoolean("supernova_library_policy_initialized",false)){List<Uri> existing=new ArrayList<>();for(Shortcut source:sources)existing.add(source.getUri());selection.seedExisting(existing);}savedSources.clear();for(Shortcut source:saved)savedSources.put(BrowserSelection.canonical(source.getUri()),source);
        for(int i=left.getChildCount()-1;i>2;i--)left.removeViewAt(i);
        locations.clear();localChildren.clear();healthChildren.clear();if(!onboarding)locations.add(overview);
        if(!onboarding){localParent=location("Local Storage  ›",()->command("Local Storage","Expand Local Storage to choose a drive. Opening a drive does not add it to the library.","Expand",()->toggleLocal()));localParent.setTag("semantic:network.category.local_storage");localParent.setOnClickListener(v->toggleLocal());}
        for(Box box:volumes) if(box.getBoxId()==Box.ID.FOLDERS||box.getBoxId()==Box.ID.USB
                ||box.getBoxId()==Box.ID.SDCARD||box.getBoxId()==Box.ID.OTHER) {
            String path=box.getPath();
            Uri uri=Uri.fromFile(new java.io.File(path==null?android.os.Environment.getExternalStorageDirectory().getPath():path));
            TextView drive=location(box.getBoxId()==Box.ID.FOLDERS?"Internal Storage":box.getName(),()->changeSource(uri));drive.setTag("storage:"+uri);localChildren.add(drive);if(!onboarding){drive.setPadding(dp(24),0,dp(8),0);drive.setVisibility(localExpanded?VISIBLE:GONE);}
        }
        if(directoryChoice!=null||onboarding){if(!locations.isEmpty()&&(leftAnchor==overview||leftAnchor==null||leftAnchor.getParent()==null)){leftAnchor=locations.get(0);locations.get(0).performClick();}return;}
        TextView networkHeader=location("Network Shares",this::networkShares);networkHeader.setTag("semantic:network.category.network_shares");

        location("Add Network Source",this::connectionForm);
        location("Discover Devices",this::discoverDevices);
        location("Cloud Services",this::cloud);
        location("Network Scanning",this::scanControls);
        healthParent=location("Library Health  ›",()->health(-1));healthParent.setTag("semantic:network.category.library_health");healthParent.setOnClickListener(v->toggleHealth());for(int n=0;n<LibraryHealthPanel.NAMES.length;n++){final int category=n;TextView child=location(LibraryHealthPanel.NAMES[n],()->health(category));child.setPadding(dp(24),0,dp(8),0);child.setVisibility(healthExpanded?VISIBLE:GONE);child.setTag("semantic:network.health."+n);healthChildren.add(child);}
        if(!saved.isEmpty()) left.addView(SharedThreePanel.text(getContext(),"Saved Locations",20));
        for(Shortcut source:saved) {TextView row=location(source.getName(),()->changeSource(source.getUri()));row.setTag("semantic:network.item.saved."+source.getId());}
        if(leftAnchor!=null&&leftAnchor.getParent()==null){View restored=anchorTag==null?null:left.findViewWithTag(anchorTag);leftAnchor=restored==null?overview:restored;if(leftFocused)leftAnchor.requestFocus();}
    }

    private void centreScrolling(boolean value){if(inlineScroll==null)inlineScroll=new ScrollView(getContext());if(value&&items.getParent()==inlineScroll||!value&&items.getParent()==itemScroll)return;if(items.getParent() instanceof ViewGroup)((ViewGroup)items.getParent()).removeView(items);itemScroll.removeAllViews();if(value){inlineScroll.addView(items,new ScrollView.LayoutParams(-1,-2));itemScroll.addView(inlineScroll,new FrameLayout.LayoutParams(-1,-1));}else itemScroll.addView(items,new FrameLayout.LayoutParams(-1,-1));}
    private void networkShares(){command("Network Shares","Choose a saved connection, then browse or manage the selected media folder.","Add Network Source",this::connectionForm);centreHeading("Network Shares");if(shares.isEmpty())items.addView(SharedThreePanel.text(getContext(),"No saved shares",16));for(Shortcut source:shares){TextView row=action(source.getName(),()->open(source.getUri()));row.setTag("semantic:network.item.source."+source.getId());row.setOnFocusChangeListener((v,yes)->{if(yes){centreAnchor=v;showInformation(source.getUri(),null);}});items.addView(row);SharedThreePanel.divider(items);}centreAction("Add Network Source",this::connectionForm);}
    private void toggleLocal(){localExpanded=!localExpanded;if(localExpanded&&healthExpanded)toggleHealth();localParent.setText("Local Storage  "+(localExpanded?"⌄":"›"));for(TextView child:localChildren)child.setVisibility(localExpanded?VISIBLE:GONE);localParent.requestFocus();}
    private void toggleHealth(){healthExpanded=!healthExpanded;if(healthExpanded&&localExpanded)toggleLocal();healthParent.setText("Library Health  "+(healthExpanded?"⌄":"›"));for(TextView child:healthChildren)child.setVisibility(healthExpanded?VISIBLE:GONE);healthParent.requestFocus();}
    private void health(int category){centreScrolling(true);stopDiscovery();remember();current=null;stopListing();toolbar.setVisibility(GONE);breadcrumbScroll.setVisibility(GONE);status.setText("");facts.setVisibility(GONE);poster.setVisibility(GONE);information.setText("");heading.setText("Library Health");healthPanel=new LibraryHealthPanel(getContext(),items,actions,PreviewLibraryLoader.memoryCache());healthPanel.show(category);}
    private void centreHeading(String title){centreScrolling(true);items.setTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic,null);items.removeAllViews();SharedThreePanel.heading(items,title);}
    private void centreAction(String label,Runnable run){TextView row=action(label,run);items.addView(row,new LayoutParams(-1,dp(42)));SharedThreePanel.divider(items);}
    private void cloud(){putio();}
    private void putio(){command("put.io","Enabled state is independent of account connection. Disabling retains credentials and library associations. Registered OAuth client configuration is required for a new connection.","Account / Connection",()->{if(putioController!=null)putioController.close();putioController=com.archos.mediacenter.video.streaming.putio.PutioAccountController.attach(getContext(),items);});centreHeading("put.io");boolean enabled=prefs.getBoolean("supernova_putio_enabled",true);centreAction("Enabled: "+(enabled?"On":"Off"),()->{prefs.edit().putBoolean("supernova_putio_enabled",!enabled).apply();putio();});if(enabled){centreAction("Account / Connection",()->{if(putioController!=null)putioController.close();putioController=com.archos.mediacenter.video.streaming.putio.PutioAccountController.attach(getContext(),items);});}else{actions.removeAllViews();information.setText("Disabled. Saved account connection and library associations are retained.");}}

    private void connectionForm(){stopDiscovery();command("Add Network Source","Connect, browse to the media folder, then explicitly add it to the library. Passwords stay on this device.","Connect",this::connect);centreHeading("Add Network Source");com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(items,"private.credentials");String[] names={"SMB","WebDAV HTTPS","WebDAV HTTP","SFTP","FTP","FTP TLS"};centreAction("Protocol: "+names[protocol],()->{centreHeading("Protocol");for(int n=0;n<names.length;n++){final int value=n;centreAction((protocol==n?"✓  ":"○  ")+names[n],()->{protocol=value;connectionForm();});}focusCentre();});String[] labels={"Server / Address","Port","Path","Username","Password","SMB Domain"};for(int n=0;n<labels.length;n++){if(n==5&&protocol!=0)continue;final int field=n;String value=n==4&&!showPassword&&!connection[n].isEmpty()?"••••••":connection[n];centreAction(labels[n]+": "+value,()->{if(field==4)PreviewTextInput.showSecret(getContext(),labels[field],connection[field],text->{connection[field]=text;connectionForm();focusCentre();});else PreviewTextInput.showValidated(getContext(),labels[field],connection[field],512,text->field==1&&!text.isEmpty()&&!text.matches("[0-9]{1,5}")?"Enter a port from 1 to 65535":null,text->{connection[field]=text;connectionForm();focusCentre();});});}centreAction("Save Password: "+(savePassword?"On":"Off"),()->{savePassword=!savePassword;connectionForm();});centreAction("Show Password: "+(showPassword?"On":"Off"),()->{showPassword=!showPassword;connectionForm();});}
    private void connect(){try{String address=connection[0].trim();if(address.isEmpty()||address.contains("/")||address.contains("@")||address.contains("?"))throw new IllegalArgumentException("Enter a server name or IP address without a protocol or credentials.");int port=connection[1].isEmpty()?-1:Integer.parseInt(connection[1]);if(port!=-1&&(port<1||port>65535))throw new IllegalArgumentException("Enter a port from 1 to 65535.");String scheme=new String[]{prefs.getBoolean("pref_smbj",true)?"smbj":"smb","webdavs","webdav",prefs.getBoolean("pref_sshj",true)?"sshj":"sftp","ftp","ftps"}[protocol];String authority=address+(port<0?"":":"+port);Uri uri=new Uri.Builder().scheme(scheme).encodedAuthority(authority).path(connection[2].startsWith("/")?connection[2]:"/"+connection[2]).build();com.archos.filecorelibrary.samba.NetworkCredentialsDatabase database=com.archos.filecorelibrary.samba.NetworkCredentialsDatabase.getInstance();com.archos.filecorelibrary.samba.NetworkCredentialsDatabase.Credential credential=new com.archos.filecorelibrary.samba.NetworkCredentialsDatabase.Credential(connection[3],connection[4],uri.toString(),connection[5],!savePassword);if(savePassword)database.saveCredential(credential);database.addCredential(credential);connection[4]="";showPassword=false;open(uri);focusCentre();}catch(IllegalArgumentException failure){status.setText(failure.getMessage());}}
    private void discoverDevices(){stopDiscovery();command("Discover Devices","SMB computers / NAS and DLNA / UPnP media servers. FTP and SFTP use direct connections.","Refresh",this::discoverDevices);centreHeading("Discover Devices");discovered.clear();status.setText("Discovering…");try{discovery=new com.archos.filecorelibrary.samba.SambaDiscovery(getContext());discoveryListener=new com.archos.filecorelibrary.samba.SambaDiscovery.Listener(){public void onDiscoveryStart(){}public void onDiscoveryEnd(){if(!closed)status.setText(discovered.size()+" devices");}public void onDiscoveryFatalError(){if(!closed)status.setText("SMB discovery unavailable. Direct connections remain available.");}public void onDiscoveryUpdate(List<com.archos.filecorelibrary.samba.Workgroup> groups){for(com.archos.filecorelibrary.samba.Workgroup group:groups)for(com.archos.filecorelibrary.samba.Share share:group.getShares())discovered.put(share.getAddress(),share.getDisplayName());renderDiscovered();}};discovery.addListener(discoveryListener);discovery.start();com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager manager=com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager.getSingleton(getContext());upnpListener=devices->{for(org.jupnp.model.meta.Device device:devices){Uri uri=com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager.getDeviceUri(device);discovered.put(uri.toString(),com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager.getDeviceFriendlyName(device));}renderDiscovered();};manager.addListener(upnpListener);manager.start();}catch(RuntimeException|LinkageError failure){status.setText("Device discovery unavailable. Direct connections remain available.");}}
    private void renderDiscovered(){if(closed||discovery==null)return;View focused=items.findFocus();Object tag=focused==null?null:focused.getTag();centreHeading("Discover Devices");for(Map.Entry<String,String> device:discovered.entrySet()){Uri uri=Uri.parse(device.getKey());centreAction(device.getValue(),()->open(uri));items.getChildAt(items.getChildCount()-2).setTag(device.getKey());}if(tag!=null){View target=items.findViewWithTag(tag);if(target!=null)target.requestFocus();}}
    private void stopDiscovery(){if(putioController!=null){putioController.close();putioController=null;}if(discovery!=null){if(discoveryListener!=null)discovery.removeListener(discoveryListener);discovery.abort();discovery=null;}if(upnpListener!=null){com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager.getSingleton(getContext()).removeListener(upnpListener);upnpListener=null;}}
    private void changeSource(Uri uri){
        open(uri);
    }
    private void command(String title,String description,String label,Runnable action){
        stopDiscovery();centreScrolling(true);toolbar.setVisibility(GONE);breadcrumbScroll.setVisibility(GONE);remember();current=null;stopListing();items.removeAllViews();crumbs.removeAllViews();status.setText("");facts.setVisibility(GONE);heading.setText(title);information.setText(description);actions.removeAllViews();addAction(label,action);
    }
    private TextView location(String name,Runnable show) {
        TextView row=action(name,show);SharedThreePanel.rowSeparator(row); locations.add(row); left.addView(row,new LayoutParams(-1,dp(50)));
        PreviewIcon.apply(row,"storage",22);
        row.setSingleLine(true);row.setEllipsize(android.text.TextUtils.TruncateAt.END);row.setOnFocusChangeListener((v,focused)->{if(focused){
            String tag=String.valueOf(row.getTag());boolean folderNavigation=tag.startsWith("storage:")||tag.equals("semantic:network.category.local_storage");
            if(!onboarding&&configurationActive&&selection.changed()&&!folderNavigation&&leftAnchor!=row){View previous=leftAnchor;if(previous!=null)previous.requestFocus();requestExit(()->{configurationActive=false;remember();current=null;row.requestFocus();});return;}
            leftAnchor=row;centreAnchor=null;show.run();}});
        return row;
    }
    private void showOverview() {
        if(current!=null&&selection.changed()) { // Category changes leave the browser section.
            requestExit(()->{current=null;showOverview();}); return;
        }
        toolbar.setVisibility(GONE);breadcrumbScroll.setVisibility(GONE);panels.widths(.28f,.44f,.28f);remember(); current=null; stopListing(); indexed.clear(); files.clear(); items.removeAllViews(); crumbs.removeAllViews();
        facts.setVisibility(GONE);heading.setText("Overview"); information.setText("Choose a storage device, network share or saved location.\n\nLibrary changes remain unsaved until Save Changes & Scan. Scans continue in the background.");
        status.setText(PreviewLibraryScan.libraryStatus(getContext())); actions.removeAllViews();
        if(directoryChoice!=null){heading.setText("Choose a Folder");information.setText("Browse local storage and choose a writable folder. This changes the download destination and keeps your library unchanged.");status.setText("");return;}
        addAction("Scan Library",()->PreviewLibraryScan.request(getContext()));
        addAction("Network Scanning",this::scanControls);
        addAction("Library Health",()->health(-1));
        scanOverview=SharedThreePanel.text(getContext(),PreviewLibraryScan.overview(getContext()),16);scanOverview.setFocusable(true);scanOverview.setFocusableInTouchMode(true);scanOverview.setBackground(SharedThreePanel.focus(getContext()));items.addView(scanOverview,new LayoutParams(-1,-2));
        items.addView(SharedThreePanel.text(getContext(),"Select a location to browse its files and folders.",16));
    }

    public void open(Uri uri) {
        if(closed||uri==null)return;if(onboarding&&!"file".equalsIgnoreCase(uri.getScheme())){status.setText("Choose local folders during initial setup.");return;}configurationActive=true;
        if(current!=null&&current.equals(uri)&&!listingFailed)return;
        stopDiscovery();centreScrolling(false);remember(); current=uri; selectedFile=null; selectedUri=null; indexed.clear();
        toolbar.setVisibility(onboarding?GONE:VISIBLE);breadcrumbScroll.setVisibility(VISIBLE);panels.widths(.28f,.44f,.28f);locationChanged.accept(uri); breadcrumbs(); showInformation(uri,null); reload();
    }
    private void remember() {
        if(current==null)return;
        int first=fileLayout.findFirstVisibleItemPosition();View top=fileLayout.findViewByPosition(first);listPositions.put(current.toString(),new int[]{Math.max(0,first),top==null?0:top.getTop()});
        if(selectedUri!=null)rememberedItems.put(current.toString(),selectedUri.toString());
    }
    private void reload() {
        if(current==null||suppliedDock!=null)return;
        stopListing(); listingFailed=false; int token=++generation; Uri target=current;
        status.setText("Loading…");loading.setVisibility(VISIBLE);
        try {
            engine=ListingEngineFactoryWithUpnp.getListingEngineForUrl(getContext(),target);
            if(!VideoPreferencesCommon.PreferenceHelper.shouldDisplayAllFiles(getContext()))engine.setFilter(VideoUtils.getVideoFilterMimeTypes(),null);
            engine.setSortOrder(sortOrder()); engine.setListingTimeOut(30000);
            engine.setListener(new ListingEngine.Listener() {
                private boolean valid(){return !closed&&token==generation&&target.equals(current);}
                public void onListingStart(){}
                public void onListingUpdate(List<? extends MetaFile2> result){if(valid()){files=new ArrayList<>();for(MetaFile2 file:result)if(directoryChoice==null&&!onboarding||file.isDirectory())files.add(file);renderItems();}}
                public void onListingEnd(){if(valid()){loading.setVisibility(INVISIBLE);if(!listingFailed)status.setText(files.size()+" items");}}
                public void onListingTimeOut(){if(valid())failed("Connection timed out. Your library records and playback progress are retained.");}
                public void onCredentialRequired(Exception e){if(valid()){failed("Authentication required");addAction("Connect",()->launchCredentials(target));}}
                public void onListingFatalError(Exception e,ListingEngine.ErrorEnum code){if(valid())failed(getContext().getString(ListingEngine.getErrorStringResId(code))+". Library records are retained.");}
                public void onListingFileInfoUpdate(Uri uri,MetaFile2 file){if(valid()&&uri.equals(selectedUri))showInformation(uri,file);}
            }); engine.start();
            if(directoryChoice==null&&!onboarding)metadata.execute(()->loadIndexed(target,token));
        } catch(RuntimeException failure){failed("This location could not be opened. Your library records are retained.");}
    }
    private void loadIndexed(Uri target,int token) {
        Map<String,Video> found=new HashMap<>();
        try(Cursor cursor=new VideosInFolderLoader(getContext().getApplicationContext(),target.toString()).loadInBackground()) {
            if(cursor!=null){VideoCursorMapper mapper=new VideoCursorMapper();mapper.publicBindColumns(cursor);
                while(cursor.moveToNext()){Video video=(Video)mapper.publicBind(cursor);found.put(video.getFileUri().toString(),video);}}
        } catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("browser_metadata_query",failure);}
        post(()->{if(!closed&&token==generation&&target.equals(current)){indexed.putAll(found);if(selectedUri!=null)showInformation(selectedUri,selectedFile);}});
    }
    private void failed(String message){loading.setVisibility(INVISIBLE);listingFailed=true;status.setText(message);addAction("Retry",()->{listingFailed=true;reload();});}
    private void launchCredentials(Uri target){
        String scheme=target.getScheme();String[] protocols={"smb","https","http","sftp","ftp","ftps"};
        int selected=-1;for(int n=0;n<protocols.length;n++)if(protocols[n].equalsIgnoreCase(scheme)){selected=n;break;}
        if(selected<0){status.setText("This source uses its existing account connection controls.");return;}
        protocol=selected;connection[0]=target.getHost()==null?"":target.getHost();connection[1]=target.getPort()>0?String.valueOf(target.getPort()):"";
        connection[2]=target.getPath()==null?"/":target.getPath();connection[3]="";connection[4]="";connection[5]="";showPassword=false;
        connectionForm();focusCentre();
    }
    private void stopListing(){loading.setVisibility(INVISIBLE);generation++;if(engine!=null){engine.setListener(null);engine.abort();engine=null;}}

    public static List<Uri> ancestors(Uri uri) {
        List<Uri> result=new ArrayList<>();
        Uri root=uri.buildUpon().clearQuery().fragment(null).path("/").build(); result.add(root);
        String path="";for(String segment:uri.getPathSegments()){path+="/"+segment;result.add(root.buildUpon().path(path).build());}
        return result;
    }
    private void breadcrumbs() {
        crumbs.removeAllViews(); if(current==null)return;
        List<Uri> trail=ancestors(current);
        for(int i=0;i<trail.size();i++) {
            Uri ancestor=trail.get(i); String title=i==0?(current.getHost()==null?"Internal Storage":current.getHost()):ancestor.getLastPathSegment();
            TextView segment=action(title,()->open(ancestor));segment.setSingleLine(true);
            segment.setOnFocusChangeListener((v,focused)->{if(focused){centreAnchor=v;v.requestRectangleOnScreen(new android.graphics.Rect(0,0,v.getWidth(),v.getHeight()),false);}});
            crumbs.addView(segment,new LayoutParams(-2,dp(48)));
            if(i<trail.size()-1)crumbs.addView(SharedThreePanel.text(getContext()," › ",18));
        }
        breadcrumbScroll.post(()->breadcrumbScroll.fullScroll(FOCUS_RIGHT));
    }
    private void renderItems() {
        if(current==null)return;
        boolean hadFocus=items.hasFocus();String identity=selectedUri==null?rememberedItems.get(current.toString()):selectedUri.toString();
        if(fileList.getParent()!=items){if(fileList.getParent() instanceof ViewGroup)((ViewGroup)fileList.getParent()).removeView(fileList);items.removeAllViews();items.addView(fileList,new LayoutParams(-1,-1));}
        fileLayout.setSpanCount(prefs.getBoolean(GRID,false)?2:1);fileAdapter.notifyDataSetChanged();
        int[] position=listPositions.get(current.toString());if(position!=null)fileLayout.scrollToPositionWithOffset(position[0],position[1]);
        if(hadFocus&&identity!=null){for(int i=0;i<files.size();i++)if(identity.equals(files.get(i).getUri().toString())){final int target=i;fileList.post(()->{RecyclerView.ViewHolder row=fileList.findViewHolderForAdapterPosition(target);if(row!=null){centreAnchor=row.itemView;row.itemView.requestFocus();}else{fileList.scrollToPosition(target);fileList.post(()->{RecyclerView.ViewHolder visible=fileList.findViewHolderForAdapterPosition(target);if(visible!=null){centreAnchor=visible.itemView;visible.itemView.requestFocus();}});}});break;}}
        if(files.isEmpty()&&!listingFailed)status.setText("No matching files or folders");
    }
    private TextView fileRow(){TextView row=action("",()->{});SharedThreePanel.rowSeparator(row);return row;}
    private final class FileAdapter extends RecyclerView.Adapter<FileRow> {
        @Override public FileRow onCreateViewHolder(ViewGroup parent,int type){return new FileRow(fileRow());}
        @Override public void onBindViewHolder(FileRow holder,int position){MetaFile2 file=files.get(position);TextView row=(TextView)holder.itemView;row.setText(file.getName());PreviewIcon.apply(row,file.isDirectory()?"folder":"Files",22);if(onboarding&&file.isDirectory()){PreviewIcon check=new PreviewIcon(selection.included(file.getUri())?"checkbox checked":"checkbox");check.setBounds(0,0,dp(22),dp(22));row.setCompoundDrawables(row.getCompoundDrawables()[0],null,check,null);}row.setAccessibilityDelegate(new View.AccessibilityDelegate(){@Override public void onInitializeAccessibilityNodeInfo(View host,android.view.accessibility.AccessibilityNodeInfo info){super.onInitializeAccessibilityNodeInfo(host,info);if(onboarding&&file.isDirectory()){info.setCheckable(true);info.setChecked(selection.included(file.getUri()));}}});row.setTag(file.getUri().toString());boolean grid=prefs.getBoolean(GRID,false);row.setMaxLines(grid?3:1);row.setLayoutParams(new RecyclerView.LayoutParams(-1,dp(grid?100:50)));row.setOnClickListener(v->{if(onboarding&&file.isDirectory()){if(selection.included(file.getUri())){if(selection.roots.contains(BrowserSelection.canonical(file.getUri())))selection.remove(file.getUri());else selection.exclude(file.getUri());}else selection.include(file.getUri());fileAdapter.notifyDataSetChanged();showInformation(file.getUri(),file);}else activate(file);});row.setOnFocusChangeListener((v,yes)->{if(yes){centreAnchor=row;selectedUri=file.getUri();selectedFile=file;showInformation(selectedUri,file);}});}
        @Override public int getItemCount(){return files.size();}
    }
    private static final class FileRow extends RecyclerView.ViewHolder{FileRow(View view){super(view);}}
    private void activate(MetaFile2 file) {
        if(file.isDirectory()){open(file.getUri());return;}
        Video video=indexed.get(file.getUri().toString());
        if(video!=null)getContext().startActivity(new Intent(getContext(),VideoDetailsActivity.class).putExtra(VideoDetailsFragment.EXTRA_VIDEO,video));
        else if(getContext() instanceof Activity)PlayUtils.openAnyFile(file,(Activity)getContext());
    }
    private void showInformation(Uri uri,MetaFile2 file) {
        View previous=actions.findFocus();int actionIndex=previous==null?-1:actions.indexOfChild(previous);actions.removeAllViews();boolean folder=file==null||file.isDirectory();
        heading.setText("Information");
        facts.setVisibility(VISIBLE);factValues[0].setText(file==null?(uri.getLastPathSegment()==null?"Storage":uri.getLastPathSegment()):file.getName());factValues[1].setText(folder?"Folder":"File");factValues[2].setText(BrowserSelection.canonical(uri));factValues[3].setText(directoryChoice!=null?"Directory selection":selection.included(uri)?"Included":selection.exclusions.contains(BrowserSelection.canonical(uri))?"Excluded":"Not included");
        StringBuilder body=new StringBuilder();
        if(file!=null&&file.length()>=0&&!folder)body.append("\n\nSize\n").append(android.text.format.Formatter.formatFileSize(getContext(),file.length()));
        Video video=indexed.get(uri.toString());
        if(video!=null){
            body.append("\n\nAvailability\n").append(LibraryHealth.message(LibraryHealth.state(getContext(),video)));
            int width=video.getMeasuredWidth(),height=video.getMeasuredHeight();if(width>0&&height>0)body.append("\n\nResolution\n").append(width).append(" × ").append(height);
            String codec=com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.format(video.getCalculatedVideoFormat());if(!codec.isEmpty())body.append("\n\nVideo codec\n").append(codec);
            String audio=com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.format(video.getCalculatedBestAudioFormat());if(!audio.isEmpty())body.append("\n\nAudio\n").append(audio);
        }
        if(video!=null&&video.getDurationMs()>0)body.append("\n\nDuration\n").append(video.getDurationMs()/60000).append(" min");
        information.setText(body);informationScroll.scrollTo(0,0);if(actionIndex<0)contextScroll.scrollTo(0,0);
        if(directoryChoice!=null){
            boolean writable="file".equals(uri.getScheme())&&new java.io.File(uri.getPath()).isDirectory()&&new java.io.File(uri.getPath()).canWrite();
            if(writable)addAction("Choose This Folder",()->{java.io.File chosen=new java.io.File(uri.getPath());if(chosen.isDirectory()&&chosen.canWrite())directoryChoice.accept(uri);});
            else information.append("\n\nThis folder is not writable. Choose another location.");
            return;
        }
        if(onboarding&&folder&&file!=null)addAction("Browse Folder",()->open(uri));
        if(selection.included(uri))addAction(selection.roots.contains(BrowserSelection.canonical(uri))?"Remove from Library":"Exclude from Library",()->{if(selection.roots.contains(BrowserSelection.canonical(uri)))selection.remove(uri);else selection.exclude(uri);fileAdapter.notifyDataSetChanged();showInformation(uri,file);});
        else addAction("Add to Library",()->{selection.include(uri);fileAdapter.notifyDataSetChanged();showInformation(uri,file);});
        if(selection.exclusions.contains(BrowserSelection.canonical(uri)))addAction("Restore inclusion",()->{selection.restore(uri);showInformation(uri,file);});
        if(folder&&!onboarding)addAction("Save Location",()->PreviewFolderActions.save(getContext(),uri,file==null?String.valueOf(uri.getLastPathSegment()):file.getName()));
        if(savedSources.containsKey(BrowserSelection.canonical(uri)))addAction("Remove Saved Location",()->PreviewDialog.choose(getContext(),"Remove saved location? Library records and media files are kept.",new String[]{"Cancel","Remove"},0,n->{if(n!=1)return;int removed;
            try{removed=com.archos.mediacenter.video.browser.ShortcutDb.STATIC.removeShortcut(getContext(),uri);}catch(RuntimeException failure){removed=0;}
            if(removed<=0){PreviewDialog.read(getContext(),"Removal not confirmed","The saved location remains visible. Retry after refreshing sources.");return;}
            Shortcut source=savedSources.remove(BrowserSelection.canonical(uri));View row=left.findViewWithTag("semantic:network.item.saved."+source.getId());if(row!=null){locations.remove(row);left.removeView(row);if(leftAnchor==row)leftAnchor=overview;}showInformation(uri,file);
        }));
        if(video!=null)addAction("View Details",()->getContext().startActivity(new Intent(getContext(),VideoDetailsActivity.class).putExtra(VideoDetailsFragment.EXTRA_VIDEO,video)));
        if(onboarding||selection.changed())addAction(onboarding?"Save & Finish":"Save Changes & Scan",()->apply(null));if(actionIndex>=0&&actions.getChildCount()>0)actions.getChildAt(Math.min(actionIndex,actions.getChildCount()-1)).requestFocus();
    }
    private void apply(Runnable after) {
        if(!legacyRootsReady){readHistoricalRoots();PreviewDialog.read(getContext(),"Library changes pending","Existing library locations must finish loading before changes can be applied. Keep editing and retry.");return;}
        Set<String> oldRoots=new LinkedHashSet<>(prefs.getStringSet(BrowserSelection.ROOTS,Collections.emptySet())),oldExclusions=new LinkedHashSet<>(prefs.getStringSet(BrowserSelection.EXCLUSIONS,Collections.emptySet()));boolean initialized=prefs.getBoolean("supernova_library_policy_initialized",false),setupComplete=prefs.getBoolean("supernova_onboarding_complete",false);
        try {
            boolean written=ShortcutDbAdapter.VIDEO.applySupernovaRoots(getContext(),selection.added(),selection.removed(),()->prefs.edit().putStringSet(BrowserSelection.ROOTS,new LinkedHashSet<>(selection.roots)).putStringSet(BrowserSelection.EXCLUSIONS,new LinkedHashSet<>(selection.exclusions)).putBoolean("supernova_library_policy_initialized",true).putBoolean("supernova_onboarding_complete",true).commit());
            if(!written)throw new IllegalStateException("Changes were not saved");selection.applied();
            PreviewLibraryScan.requestAfterChanges(getContext());
            if(after!=null)after.run();else if(current!=null)showInformation(selectedUri==null?current:selectedUri,selectedFile);
        }catch(RuntimeException failure){prefs.edit().putStringSet(BrowserSelection.ROOTS,oldRoots).putStringSet(BrowserSelection.EXCLUSIONS,oldExclusions).putBoolean("supernova_library_policy_initialized",initialized).putBoolean("supernova_onboarding_complete",setupComplete).commit();PreviewDialog.read(getContext(),"Changes not saved","Keep editing and retry. No media files have been deleted.");}
    }
    public void requestExit(Runnable leave) {
        if(!selection.changed()){leave.run();return;}
        Dialog dialog=PreviewDialog.create(getContext(),"review");
        View opener=getRootView().findFocus();boolean[] leaving={false};dialog.setOnDismissListener(ignored->{if(!leaving[0]){if(opener!=null&&opener.isAttachedToWindow())opener.requestFocus();else if(centreAnchor!=null&&centreAnchor.isAttachedToWindow())centreAnchor.requestFocus();else if(leftAnchor!=null)leftAnchor.requestFocus();}});
        LinearLayout panel=column();panel.setPadding(dp(24),dp(20),dp(24),dp(20));SharedThreePanel.decorate(panel);
        panel.addView(SharedThreePanel.text(getContext(),"Unsaved library changes",22));
        ScrollView scroll=new ScrollView(getContext());TextView description=SharedThreePanel.text(getContext(),reviewText(),16);scroll.addView(description);panel.addView(scroll,new LayoutParams(-1,0,1));
        panel.addView(action("Discard Changes",()->{selection.discard();leaving[0]=true;dialog.dismiss();leave.run();}));
        TextView keep=action("Continue Editing",dialog::dismiss);panel.addView(keep);
        panel.addView(action("Save Changes & Scan",()->apply(()->{leaving[0]=true;dialog.dismiss();leave.run();})));
        dialog.setContentView(panel);dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setDimAmount(.35f);dialog.getWindow().setLayout(Math.min(dp(720),getResources().getDisplayMetrics().widthPixels-dp(64)),Math.min(dp(440),getResources().getDisplayMetrics().heightPixels-dp(64)));keep.requestFocus();
    }
    private String reviewText() {
        StringBuilder text=new StringBuilder("These changes have not been saved or scanned. Removal affects the library index only; physical files are kept.\n");
        appendGroup(text,"Added",selection.added());appendGroup(text,"Excluded",selection.excluded());appendGroup(text,"Removed previously indexed locations",selection.removed());appendGroup(text,"Restored",selection.restored());return text.toString();
    }
    private static void appendGroup(StringBuilder text,String heading,List<String> entries){if(!entries.isEmpty()){text.append("\n").append(heading).append("\n");for(String entry:entries)text.append(entry).append("\n");}}
    /** Back consumes folder ancestry before offering to leave. */
    public boolean back(Runnable leave) {
        if(current!=null){List<Uri> trail=ancestors(current);if(trail.size()>1){open(trail.get(trail.size()-2));return true;}}
        requestExit(leave);return true;
    }
    private void focusCentre(){
        if(centreAnchor!=null&&centreAnchor.isShown()&&centreAnchor.requestFocus())return;
        if(toolbar.getVisibility()==VISIBLE){toolbar.getChildAt(0).requestFocus();return;}
        for(int n=0;n<items.getChildCount();n++){View item=items.getChildAt(n);if(item.isShown()&&item.isFocusable()&&item.requestFocus())return;}
        if(current!=null&&fileAdapter.getItemCount()>0){fileList.post(()->{RecyclerView.ViewHolder first=fileList.findViewHolderForAdapterPosition(0);if(first!=null)first.itemView.requestFocus();});return;}
        if(actions.getChildCount()>0)actions.getChildAt(0).requestFocus();
    }
    private void toggleGrid(){remember();prefs.edit().putBoolean(GRID,!prefs.getBoolean(GRID,false)).apply();}
    private void chooseSort(){PreviewDialog.choose(getContext(),"Sort",new String[]{"Name ↑","Name ↓","Date ↑","Date ↓","Size ↑","Size ↓"},prefs.getInt(SORT,0),n->prefs.edit().putInt(SORT,n).apply());}
    private ListingEngine.SortOrder sortOrder(){return new ListingEngine.SortOrder[]{ListingEngine.SortOrder.SORT_BY_NAME_ASC,ListingEngine.SortOrder.SORT_BY_NAME_DESC,ListingEngine.SortOrder.SORT_BY_DATE_ASC,ListingEngine.SortOrder.SORT_BY_DATE_DESC,ListingEngine.SortOrder.SORT_BY_SIZE_ASC,ListingEngine.SortOrder.SORT_BY_SIZE_DESC}[Math.max(0,Math.min(5,prefs.getInt(SORT,0)))];}
    private void refreshControls(){TextView view=toolbar.findViewWithTag("browser.view");view.setText(prefs.getBoolean(GRID,false)?"List View":"Grid View");androidx.appcompat.widget.SwitchCompat all=toolbar.findViewWithTag("browser.all_files");all.setChecked(prefs.getBoolean(ALL_FILES,false));}
    private final SharedPreferences.OnSharedPreferenceChangeListener changes=(preferences,key)->{if(ALL_FILES.equals(key)||SORT.equals(key)){refreshControls();remember();reload();}else if(GRID.equals(key)){refreshControls();renderItems();}};
    private TextView action(String label,Runnable run){return SharedThreePanel.action(getContext(),label,run);}
    private void revealContext(View target) {
        contextScroll.post(()->{
            if(!target.hasFocus())return;
            int[] control=new int[2], viewport=new int[2];
            target.getLocationInWindow(control);contextScroll.getLocationInWindow(viewport);
            int top=viewport[1]+contextScroll.getPaddingTop();
            int bottom=viewport[1]+contextScroll.getHeight()-contextScroll.getPaddingBottom();
            int delta=control[1]<top?control[1]-top:control[1]+target.getHeight()>bottom?control[1]+target.getHeight()-bottom:0;
            contextScroll.scrollBy(0,delta);
        });
    }
    private void addAction(String label,Runnable run){TextView row=action(label,run);row.setOnFocusChangeListener((view,focused)->{if(focused)revealContext(view);});com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(row,"network.action."+label.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","_"));PreviewIcon.apply(row,label.contains("Library")?"Library":label.contains("Scan")?"refresh":"settings",22);actions.addView(row,new LayoutParams(-1,dp(50)));}
    private LinearLayout column(){LinearLayout layout=new LinearLayout(getContext());layout.setOrientation(VERTICAL);layout.setClipChildren(false);return layout;}
    private int dp(int value){return SharedThreePanel.dp(getContext(),value);}
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();readHistoricalRoots();scanHandler.post(scanRefresh);prefs.registerOnSharedPreferenceChangeListener(changes);if(current!=null&&suppliedDock==null)reload();}
    @Override protected void onDetachedFromWindow(){stopDiscovery();scanHandler.removeCallbacks(scanRefresh);prefs.unregisterOnSharedPreferenceChangeListener(changes);com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(poster);stopListing();super.onDetachedFromWindow();}
    public void close(){scanHandler.removeCallbacksAndMessages(null);closed=true;stopDiscovery();stopListing();metadata.shutdownNow();}
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction()!=KeyEvent.ACTION_DOWN)return super.dispatchKeyEvent(event);
        int key=event.getKeyCode();View focused=findFocus();
        if(left.hasFocus()) {
            if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(suppliedDock!=null)suppliedDock.requestFocus();else focusCentre();return true;}
            List<TextView> visible=new ArrayList<>();for(TextView row:locations)if(row.getVisibility()==VISIBLE)visible.add(row);int position=visible.indexOf(focused);if(position>=0&&(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN)){int next=position+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next<0&&key==KeyEvent.KEYCODE_DPAD_UP)return super.dispatchKeyEvent(event);if(next>=0&&next<visible.size())visible.get(next).requestFocus();return true;}
        }
        if(centre.hasFocus()) {
            centreAnchor=focused;
            if(onboarding&&fileList.hasFocus()&&key==KeyEvent.KEYCODE_DPAD_RIGHT&&selectedFile!=null&&selectedFile.isDirectory()){activate(selectedFile);return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){View next=android.view.FocusFinder.getInstance().findNextFocus(centre,focused,key==KeyEvent.KEYCODE_DPAD_UP?FOCUS_UP:FOCUS_DOWN);if(next!=null&&next!=focused)next.requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT) {
                ViewGroup horizontal=toolbar.hasFocus()?toolbar:crumbs.hasFocus()?crumbs:suppliedDock instanceof ViewGroup?(ViewGroup)suppliedDock:fileList.hasFocus()?fileList:centre;
                View next=android.view.FocusFinder.getInstance().findNextFocus(horizontal,focused,key==KeyEvent.KEYCODE_DPAD_LEFT?FOCUS_LEFT:FOCUS_RIGHT);
                if(next!=null&&next!=focused){int[] a=new int[2],b=new int[2];focused.getLocationInWindow(a);next.getLocationInWindow(b);if(key==KeyEvent.KEYCODE_DPAD_LEFT?b[0]<a[0]:b[0]>a[0]){next.requestFocus();return true;}}
                if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(leftAnchor!=null)leftAnchor.requestFocus();}
                else if(actions.getChildCount()>0)actions.getChildAt(0).requestFocus();return true;
            }
        }
        if(right.hasFocus()) {
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(centreAnchor==null||!centreAnchor.requestFocus())focusCentre();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){
                if(addressScroll.hasFocus()){int direction=key==KeyEvent.KEYCODE_DPAD_UP?-1:1;if(addressScroll.canScrollVertically(direction))addressScroll.scrollBy(0,direction*dp(48));else if(direction>0){if(information.getText().length()>0)informationScroll.requestFocus();else if(actions.getChildCount()>0)actions.getChildAt(0).requestFocus();}return true;}
                if(informationScroll.hasFocus()) {
                    int direction=key==KeyEvent.KEYCODE_DPAD_UP?-1:1;
                    if(informationScroll.canScrollVertically(direction))informationScroll.scrollBy(0,direction*dp(60));
                    else if(direction>0&&actions.getChildCount()>0)actions.getChildAt(0).requestFocus();
                    return true;
                }
                int index=actions.indexOfChild(focused),direction=key==KeyEvent.KEYCODE_DPAD_UP?-1:1,next=index+direction;while(next>=0&&next<actions.getChildCount()&&!actions.getChildAt(next).isFocusable())next+=direction;
                if(next>=0&&next<actions.getChildCount())actions.getChildAt(next).requestFocus();
                else if(index==0&&key==KeyEvent.KEYCODE_DPAD_UP){if(information.getText().length()>0)informationScroll.requestFocus();else if(facts.getVisibility()==VISIBLE&&(addressScroll.canScrollVertically(1)||addressScroll.getScrollY()>0))addressScroll.requestFocus();}
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
