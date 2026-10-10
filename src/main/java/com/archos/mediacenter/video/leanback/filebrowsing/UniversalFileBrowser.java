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
    private final FrameLayout itemScroll;private final ScrollView informationScroll;
    private final PreviewFocusRecycler fileList;private final GridLayoutManager fileLayout;private final FileAdapter fileAdapter=new FileAdapter();
    private final Map<String,int[]> listPositions=new HashMap<>();private String sourceFingerprint="";
    private final HorizontalScrollView breadcrumbScroll;
    private final TextView information, heading, status, overview;private final LinearLayout facts;private final TextView[] factValues=new TextView[4];
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

    public UniversalFileBrowser(Context c) {
        super(c); setOrientation(VERTICAL); setClipChildren(false); setClipToPadding(false);
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
        selection = new BrowserSelection(prefs.getStringSet(BrowserSelection.ROOTS, Collections.emptySet()),
                prefs.getStringSet(BrowserSelection.EXCLUSIONS, Collections.emptySet()));
        seedLegacyRoots();
        left = column(); centre = column(); right = column(); toolbar = new LinearLayout(c);
        SharedThreePanel panels = new SharedThreePanel(c);
        ScrollView leftScroll = new ScrollView(c); leftScroll.addView(left);
        panels.panels(leftScroll, centre, right); addView(panels, new LayoutParams(-1,-1));
        left.addView(SharedThreePanel.text(c,"Locations",21));
        overview = location("Overview", this::showOverview); leftAnchor = overview;
        overview.setTag("semantic:network.category.overview");
        TextView listMode = action("List / Grid", this::toggleGrid);listMode.setTag("browser.view");
        TextView sort = action("Sort", this::chooseSort);
        androidx.appcompat.widget.SwitchCompat all=new androidx.appcompat.widget.SwitchCompat(c);all.setShowText(false);all.setTextOn("ON");all.setTextOff("OFF");all.setText("All Files");all.setTextColor(-1);all.setFocusable(true);all.setFocusableInTouchMode(true);all.setBackground(SharedThreePanel.focus(c));all.setChecked(prefs.getBoolean(ALL_FILES,false));all.setThumbTintList(android.content.res.ColorStateList.valueOf(-1));all.setTrackTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{PreviewAccent.color(c),0xff59616b}));all.setOnCheckedChangeListener((button,checked)->prefs.edit().putBoolean(ALL_FILES,checked).apply());
        all.setTag("browser.all_files");
        for(TextView button : new TextView[]{listMode,sort,all}) {
            button.setTextSize(17); button.setGravity(android.view.Gravity.CENTER);
            toolbar.addView(button,new LayoutParams(0,dp(50),1));
        }
        centre.addView(toolbar,new LayoutParams(-1,-2));
        crumbs = new LinearLayout(c); breadcrumbScroll = new HorizontalScrollView(c);
        breadcrumbScroll.setHorizontalScrollBarEnabled(false); breadcrumbScroll.addView(crumbs);
        centre.addView(breadcrumbScroll,new LayoutParams(-1,dp(50)));
        status = SharedThreePanel.text(c,"",15); status.setMinHeight(dp(26));LinearLayout progress=new LinearLayout(c);progress.setGravity(android.view.Gravity.CENTER_VERTICAL);loading=new ProgressBar(c);loading.setIndeterminateDrawable(new com.archos.mediacenter.video.foundation.DoubleRingDrawable());loading.setVisibility(GONE);progress.addView(loading,new LayoutParams(dp(26),dp(26)));progress.addView(status,new LayoutParams(0,-2,1));centre.addView(progress);
        items = column();itemScroll=new FrameLayout(c);itemScroll.addView(items,new FrameLayout.LayoutParams(-1,-1));
        fileList=new PreviewFocusRecycler(c);fileLayout=new GridLayoutManager(c,prefs.getBoolean(GRID,false)?2:1);fileList.setLayoutManager(fileLayout);fileList.setAdapter(fileAdapter);fileList.setItemAnimator(null);fileList.setClipToPadding(false); centre.addView(itemScroll,new LayoutParams(-1,0,1));
        heading = SharedThreePanel.text(c,"Overview",21); right.addView(heading);
        poster=new ImageView(c);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);poster.setVisibility(GONE);right.addView(poster,new LayoutParams(-1,dp(120)));
        facts=column();String[] labels={"Name","Type","Address","Library status"};for(int i=0;i<labels.length;i++){LinearLayout row=new LinearLayout(c);row.setGravity(android.view.Gravity.TOP);TextView label=SharedThreePanel.text(c,labels[i],16);label.setTextColor(0xffb7bdc6);row.addView(label,new LayoutParams(dp(92),-2));factValues[i]=SharedThreePanel.text(c,"",16);ScrollView value=new ScrollView(c);value.setFocusable(false);value.setFocusableInTouchMode(false);value.addView(factValues[i]);row.addView(value,new LayoutParams(0,dp(i==2?60:36),1));facts.addView(row,new LayoutParams(-1,-2));}facts.setVisibility(GONE);right.addView(facts,new LayoutParams(-1,-2));
        information = SharedThreePanel.text(c,"",16);
        informationScroll = new ScrollView(c); informationScroll.setFocusable(false);informationScroll.setFocusableInTouchMode(false);informationScroll.addView(information);
        right.addView(informationScroll,new LayoutParams(-1,0,1));
        actions = column();ScrollView actionScroll=new ScrollView(c);actionScroll.addView(actions);right.addView(actionScroll,new LayoutParams(-1,0,1));
        refreshControls(); showOverview();
    }

    private void seedLegacyRoots(){
        legacyRootsReady=prefs.getBoolean("supernova_library_policy_initialized",false);
        if(legacyRootsReady)return;
        List<Uri> roots=new ArrayList<>();roots.add(Uri.fromFile(android.os.Environment.getExternalStorageDirectory()));
        com.archos.filecorelibrary.ExtStorageManager storage=com.archos.filecorelibrary.ExtStorageManager.getExtStorageManager();
        if(storage.hasExtStorage()){for(String path:storage.getExtSdcards())roots.add(Uri.fromFile(new java.io.File(path)));for(String path:storage.getExtUsbStorages())roots.add(Uri.fromFile(new java.io.File(path)));for(String path:storage.getExtOtherStorages())roots.add(Uri.fromFile(new java.io.File(path)));}
        synchronized(ShortcutDbAdapter.VIDEO){try(Cursor cursor=ShortcutDbAdapter.VIDEO.getAllShortcuts(getContext(),null,null)){if(cursor!=null)while(cursor.moveToNext())roots.add(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ShortcutDbAdapter.KEY_PATH))));}}
        selection.seedExisting(roots);
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
            remember();current=null;stopListing();items.removeAllViews();crumbs.removeAllViews();scanOverview=SharedThreePanel.text(getContext(),PreviewLibraryScan.overview(getContext()),16);items.addView(scanOverview);
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
    public boolean atTop() { return overview.hasFocus(); }
    public boolean focusOverview() { return overview.requestFocus(); }
    /** Providers keep their account API and item adapter, within the same panel shell. */
    public void providerDock(View dock,Uri uri) {
        suppliedDock=dock;current=uri;
        if(dock.getParent() instanceof ViewGroup)((ViewGroup)dock.getParent()).removeView(dock);
        centre.removeView(itemScroll);centre.addView(dock,new LayoutParams(-1,0,1));
        toolbar.setVisibility(GONE);breadcrumbs();
        TextView provider=location(uri.getHost()==null?"Provider":uri.getHost(),()->{});provider.setOnClickListener(view->dock.requestFocus());leftAnchor=provider;
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
        if(inventory.toString().equals(sourceFingerprint)&&left.getChildCount()>2)return;sourceFingerprint=inventory.toString();
        Object anchorTag=leftAnchor==null?null:leftAnchor.getTag();boolean leftFocused=left.hasFocus();
        savedSources.clear();for(Shortcut source:saved)savedSources.put(BrowserSelection.canonical(source.getUri()),source);
        if(!prefs.getBoolean("supernova_library_policy_initialized",false)) {
            List<Uri> existing=new ArrayList<>();for(Shortcut source:sources)existing.add(source.getUri());
            for(Box volume:volumes){if(volume.getBoxId()==Box.ID.FOLDERS||volume.getBoxId()==Box.ID.USB||volume.getBoxId()==Box.ID.SDCARD||volume.getBoxId()==Box.ID.OTHER){String path=volume.getPath();existing.add(Uri.fromFile(new java.io.File(path==null?android.os.Environment.getExternalStorageDirectory().getPath():path)));}}
            selection.seedExisting(existing);
        }
        for(int i=left.getChildCount()-1;i>1;i--)left.removeViewAt(i);
        locations.clear(); locations.add(overview);
        TextView local=location("Local Storage",()->open(Uri.fromFile(android.os.Environment.getExternalStorageDirectory())));local.setTag("semantic:network.category.local_storage");
        for(Box box:volumes) if(box.getBoxId()==Box.ID.FOLDERS||box.getBoxId()==Box.ID.USB
                ||box.getBoxId()==Box.ID.SDCARD||box.getBoxId()==Box.ID.OTHER) {
            String path=box.getPath();
            Uri uri=Uri.fromFile(new java.io.File(path==null?android.os.Environment.getExternalStorageDirectory().getPath():path));
            location(box.getBoxId()==Box.ID.FOLDERS?"Internal Storage":box.getName(),()->changeSource(uri));
        }
        if(directoryChoice!=null)return;
        TextView networkHeader=location("Network Shares",()->command("Network Shares","Choose a saved share, or connect a new source.","Connect",()->networkAction.accept("add")));networkHeader.setTag("semantic:network.category.network_shares");
        for(Shortcut source:sources) {TextView row=location(source.getName(),()->changeSource(source.getUri()));row.setTag("semantic:network.item.source."+source.getId());}
        location("Add Network Source",()->command("Add Network Source","Connect using SMB, WebDAV, SFTP, FTP or FTP over TLS.","Connect",()->networkAction.accept("add")));
        location("Discover Devices",()->command("Discover Devices","Find computers and NAS or DLNA media servers.","Computers & NAS",()->networkAction.accept("smb")));
        location("Cloud Services",()->command("put.io","Native account management and original-quality playback through your WebDAV source.","Account / Connection",()->com.archos.mediacenter.video.streaming.putio.PutioAccountController.open(getContext())));
        location("Network Scanning",this::scanControls);
        location("Library Health",()->command("Library Health",LibraryHealth.report(getContext()),"Review Issues",()->getContext().startActivity(new Intent(getContext(),LibraryHealthActivity.class))));
        if(!saved.isEmpty()) left.addView(SharedThreePanel.text(getContext(),"Saved Locations",20));
        for(Shortcut source:saved) {TextView row=location(source.getName(),()->changeSource(source.getUri()));row.setTag("semantic:network.item.saved."+source.getId());}
        if(leftAnchor!=null&&leftAnchor.getParent()==null){View restored=anchorTag==null?null:left.findViewWithTag(anchorTag);leftAnchor=restored==null?overview:restored;if(leftFocused)leftAnchor.requestFocus();}
    }

    private void changeSource(Uri uri){
        if(current!=null&&!BrowserSelection.contains(BrowserSelection.canonical(current),BrowserSelection.canonical(uri))
                &&!BrowserSelection.contains(BrowserSelection.canonical(uri),BrowserSelection.canonical(current))&&selection.changed())requestExit(()->open(uri));else open(uri);
    }
    private void command(String title,String description,String label,Runnable action){
        requestExit(()->{remember();current=null;stopListing();items.removeAllViews();crumbs.removeAllViews();status.setText("");facts.setVisibility(GONE);heading.setText(title);information.setText(description);actions.removeAllViews();addAction(label,action);});
    }
    private TextView location(String name,Runnable show) {
        TextView row=action(name,show); locations.add(row); left.addView(row,new LayoutParams(-1,dp(50)));
        PreviewIcon.apply(row,"storage",22);
        row.setOnFocusChangeListener((v,focused)->{if(focused){leftAnchor=row;show.run();}});
        return row;
    }
    private void showOverview() {
        if(current!=null&&selection.changed()) { // Category changes leave the browser section.
            requestExit(()->{current=null;showOverview();}); return;
        }
        remember(); current=null; stopListing(); indexed.clear(); files.clear(); items.removeAllViews(); crumbs.removeAllViews();
        facts.setVisibility(GONE);heading.setText("Overview"); information.setText("Choose a storage device, network share or saved location.\n\nLibrary changes remain unsaved until Apply & Scan. Scans continue in the background.");
        status.setText(PreviewLibraryScan.libraryStatus(getContext())); actions.removeAllViews();
        if(directoryChoice!=null){heading.setText("Choose a Folder");information.setText("Browse local storage and choose a writable folder. This changes the download destination and keeps your library unchanged.");status.setText("");return;}
        addAction("Scan Library",()->PreviewLibraryScan.request(getContext()));
        addAction("Network Scanning",this::scanControls);
        addAction("Library Health",()->getContext().startActivity(new Intent(getContext(),LibraryHealthActivity.class)));
        scanOverview=SharedThreePanel.text(getContext(),PreviewLibraryScan.overview(getContext()),16);items.addView(scanOverview,new LayoutParams(-1,-2));
        items.addView(SharedThreePanel.text(getContext(),"Select a location to browse its files and folders.",16));
    }

    public void open(Uri uri) {
        if(closed||uri==null)return;
        if(current!=null&&current.equals(uri)&&!listingFailed)return;
        remember(); current=uri; selectedFile=null; selectedUri=null; indexed.clear();
        locationChanged.accept(uri); breadcrumbs(); showInformation(uri,null); reload();
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
                public void onListingUpdate(List<? extends MetaFile2> result){if(valid()){files=new ArrayList<>();for(MetaFile2 file:result)if(directoryChoice==null||file.isDirectory())files.add(file);renderItems();}}
                public void onListingEnd(){if(valid()){loading.setVisibility(GONE);if(!listingFailed)status.setText(files.size()+" items");}}
                public void onListingTimeOut(){if(valid())failed("Connection timed out. Your library records and playback progress are retained.");}
                public void onCredentialRequired(Exception e){if(valid()){failed("Authentication required");if(credentials!=null)credentials.accept(e);else addAction("Connect",()->launchCredentials(target));}}
                public void onListingFatalError(Exception e,ListingEngine.ErrorEnum code){if(valid())failed(getContext().getString(ListingEngine.getErrorStringResId(code))+". Library records are retained.");}
                public void onListingFileInfoUpdate(Uri uri,MetaFile2 file){if(valid()&&uri.equals(selectedUri))showInformation(uri,file);}
            }); engine.start();
            if(directoryChoice==null)metadata.execute(()->loadIndexed(target,token));
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
    private void failed(String message){loading.setVisibility(GONE);listingFailed=true;status.setText(message);addAction("Retry",()->{listingFailed=true;reload();});}
    private void launchCredentials(Uri target){getContext().startActivity(new Intent(getContext(),ListingActivity.getActivityForUri(target)).putExtra(ListingActivity.EXTRA_ROOT_URI,target));}
    private void stopListing(){loading.setVisibility(GONE);generation++;if(engine!=null){engine.setListener(null);engine.abort();engine=null;}}

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
    private final class FileAdapter extends RecyclerView.Adapter<FileRow> {
        @Override public FileRow onCreateViewHolder(ViewGroup parent,int type){return new FileRow(action("",()->{}));}
        @Override public void onBindViewHolder(FileRow holder,int position){MetaFile2 file=files.get(position);TextView row=(TextView)holder.itemView;row.setText(file.getName());PreviewIcon.apply(row,file.isDirectory()?"folder":"Files",22);row.setTag(file.getUri().toString());boolean grid=prefs.getBoolean(GRID,false);row.setMaxLines(grid?3:1);row.setLayoutParams(new RecyclerView.LayoutParams(-1,dp(grid?100:50)));row.setOnClickListener(v->activate(file));row.setOnFocusChangeListener((v,yes)->{if(yes){centreAnchor=row;selectedUri=file.getUri();selectedFile=file;showInformation(selectedUri,file);}});}
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
        information.setText(body);informationScroll.scrollTo(0,0);
        if(directoryChoice!=null){
            boolean writable="file".equals(uri.getScheme())&&new java.io.File(uri.getPath()).isDirectory()&&new java.io.File(uri.getPath()).canWrite();
            if(writable)addAction("Choose This Folder",()->{java.io.File chosen=new java.io.File(uri.getPath());if(chosen.isDirectory()&&chosen.canWrite())directoryChoice.accept(uri);});
            else information.append("\n\nThis folder is not writable. Choose another location.");
            return;
        }
        if(selection.included(uri))addAction(selection.roots.contains(BrowserSelection.canonical(uri))?"Remove from Library":"Exclude from Library",()->{if(selection.roots.contains(BrowserSelection.canonical(uri)))selection.remove(uri);else selection.exclude(uri);showInformation(uri,file);});
        else addAction("Add to Library",()->{selection.include(uri);showInformation(uri,file);});
        if(selection.exclusions.contains(BrowserSelection.canonical(uri)))addAction("Restore inclusion",()->{selection.restore(uri);showInformation(uri,file);});
        if(folder)addAction("Save Location",()->PreviewFolderActions.save(getContext(),uri,file==null?String.valueOf(uri.getLastPathSegment()):file.getName()));
        if(savedSources.containsKey(BrowserSelection.canonical(uri)))addAction("Remove Saved Location",()->PreviewDialog.choose(getContext(),"Remove saved location? Library records and media files are kept.",new String[]{"Cancel","Remove"},0,n->{if(n!=1)return;int removed;
            try{removed=com.archos.mediacenter.video.browser.ShortcutDb.STATIC.removeShortcut(getContext(),uri);}catch(RuntimeException failure){removed=0;}
            if(removed<=0){PreviewDialog.read(getContext(),"Removal not confirmed","The saved location remains visible. Retry after refreshing sources.");return;}
            Shortcut source=savedSources.remove(BrowserSelection.canonical(uri));View row=left.findViewWithTag("semantic:network.item.saved."+source.getId());if(row!=null){locations.remove(row);left.removeView(row);if(leftAnchor==row)leftAnchor=overview;}showInformation(uri,file);
        }));
        if(video!=null)addAction("View Details",()->getContext().startActivity(new Intent(getContext(),VideoDetailsActivity.class).putExtra(VideoDetailsFragment.EXTRA_VIDEO,video)));
        if(selection.changed())addAction("Apply & Scan",()->apply(null));if(actionIndex>=0&&actions.getChildCount()>0)actions.getChildAt(Math.min(actionIndex,actions.getChildCount()-1)).requestFocus();
    }
    private void apply(Runnable after) {
        if(!legacyRootsReady){readHistoricalRoots();PreviewDialog.read(getContext(),"Library changes pending","Existing library locations must finish loading before changes can be applied. Keep editing and retry.");return;}
        Set<String> oldRoots=new LinkedHashSet<>(prefs.getStringSet(BrowserSelection.ROOTS,Collections.emptySet())),oldExclusions=new LinkedHashSet<>(prefs.getStringSet(BrowserSelection.EXCLUSIONS,Collections.emptySet()));boolean initialized=prefs.getBoolean("supernova_library_policy_initialized",false);
        try {
            boolean written=ShortcutDbAdapter.VIDEO.applySupernovaRoots(getContext(),selection.added(),selection.removed(),()->prefs.edit().putStringSet(BrowserSelection.ROOTS,new LinkedHashSet<>(selection.roots)).putStringSet(BrowserSelection.EXCLUSIONS,new LinkedHashSet<>(selection.exclusions)).putBoolean("supernova_library_policy_initialized",true).commit());
            if(!written)throw new IllegalStateException("Changes were not saved");selection.applied();
            PreviewLibraryScan.requestAfterChanges(getContext());
            if(after!=null)after.run();else if(current!=null)showInformation(selectedUri==null?current:selectedUri,selectedFile);
        }catch(RuntimeException failure){prefs.edit().putStringSet(BrowserSelection.ROOTS,oldRoots).putStringSet(BrowserSelection.EXCLUSIONS,oldExclusions).putBoolean("supernova_library_policy_initialized",initialized).commit();PreviewDialog.read(getContext(),"Changes not saved","Keep editing and retry. No media files have been deleted.");}
    }
    public void requestExit(Runnable leave) {
        if(!selection.changed()){leave.run();return;}
        Dialog dialog=PreviewDialog.create(getContext(),"review");
        View opener=findFocus();boolean[] leaving={false};dialog.setOnDismissListener(ignored->{if(!leaving[0]){if(opener!=null&&opener.isAttachedToWindow())opener.requestFocus();else if(centreAnchor!=null&&centreAnchor.isAttachedToWindow())centreAnchor.requestFocus();else if(leftAnchor!=null)leftAnchor.requestFocus();}});
        LinearLayout panel=column();panel.setPadding(dp(24),dp(20),dp(24),dp(20));SharedThreePanel.decorate(panel);
        panel.addView(SharedThreePanel.text(getContext(),"Unsaved library changes",22));
        ScrollView scroll=new ScrollView(getContext());TextView description=SharedThreePanel.text(getContext(),reviewText(),16);scroll.addView(description);panel.addView(scroll,new LayoutParams(-1,0,1));
        panel.addView(action("Discard Changes",()->{selection.discard();leaving[0]=true;dialog.dismiss();leave.run();}));
        TextView keep=action("Keep Editing",dialog::dismiss);panel.addView(keep);
        panel.addView(action("Apply & Scan",()->apply(()->{leaving[0]=true;dialog.dismiss();leave.run();})));
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
    private void toggleGrid(){remember();prefs.edit().putBoolean(GRID,!prefs.getBoolean(GRID,false)).apply();}
    private void chooseSort(){PreviewDialog.choose(getContext(),"Sort",new String[]{"Name ↑","Name ↓","Date ↑","Date ↓","Size ↑","Size ↓"},prefs.getInt(SORT,0),n->prefs.edit().putInt(SORT,n).apply());}
    private ListingEngine.SortOrder sortOrder(){return new ListingEngine.SortOrder[]{ListingEngine.SortOrder.SORT_BY_NAME_ASC,ListingEngine.SortOrder.SORT_BY_NAME_DESC,ListingEngine.SortOrder.SORT_BY_DATE_ASC,ListingEngine.SortOrder.SORT_BY_DATE_DESC,ListingEngine.SortOrder.SORT_BY_SIZE_ASC,ListingEngine.SortOrder.SORT_BY_SIZE_DESC}[Math.max(0,Math.min(5,prefs.getInt(SORT,0)))];}
    private void refreshControls(){TextView view=toolbar.findViewWithTag("browser.view");view.setText(prefs.getBoolean(GRID,false)?"List View":"Grid View");androidx.appcompat.widget.SwitchCompat all=toolbar.findViewWithTag("browser.all_files");all.setChecked(prefs.getBoolean(ALL_FILES,false));}
    private final SharedPreferences.OnSharedPreferenceChangeListener changes=(preferences,key)->{if(ALL_FILES.equals(key)||SORT.equals(key)){refreshControls();remember();reload();}else if(GRID.equals(key)){refreshControls();renderItems();}};
    private TextView action(String label,Runnable run){return SharedThreePanel.action(getContext(),label,run);}
    private void addAction(String label,Runnable run){TextView row=action(label,run);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(row,"network.action."+label.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","_"));PreviewIcon.apply(row,label.contains("Library")?"Library":label.contains("Scan")?"refresh":"settings",22);actions.addView(row,new LayoutParams(-1,dp(50)));}
    private LinearLayout column(){LinearLayout layout=new LinearLayout(getContext());layout.setOrientation(VERTICAL);layout.setClipChildren(false);return layout;}
    private int dp(int value){return SharedThreePanel.dp(getContext(),value);}
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();readHistoricalRoots();scanHandler.post(scanRefresh);prefs.registerOnSharedPreferenceChangeListener(changes);if(current!=null&&suppliedDock==null)reload();}
    @Override protected void onDetachedFromWindow(){scanHandler.removeCallbacks(scanRefresh);prefs.unregisterOnSharedPreferenceChangeListener(changes);com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(poster);stopListing();super.onDetachedFromWindow();}
    public void close(){scanHandler.removeCallbacksAndMessages(null);closed=true;stopListing();metadata.shutdownNow();}
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction()!=KeyEvent.ACTION_DOWN)return super.dispatchKeyEvent(event);
        int key=event.getKeyCode();View focused=findFocus();
        if(left.hasFocus()) {
            if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(suppliedDock!=null)suppliedDock.requestFocus();else toolbar.getChildAt(0).requestFocus();return true;}
            int position=locations.indexOf(focused);if(position>=0&&(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN)){int next=position+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<locations.size())locations.get(next).requestFocus();return true;}
        }
        if(centre.hasFocus()) {
            centreAnchor=focused;
            if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT) {
                ViewGroup horizontal=toolbar.hasFocus()?toolbar:crumbs.hasFocus()?crumbs:suppliedDock instanceof ViewGroup?(ViewGroup)suppliedDock:fileList;
                View next=android.view.FocusFinder.getInstance().findNextFocus(horizontal,focused,key==KeyEvent.KEYCODE_DPAD_LEFT?FOCUS_LEFT:FOCUS_RIGHT);
                if(next!=null&&next!=focused){int[] a=new int[2],b=new int[2];focused.getLocationInWindow(a);next.getLocationInWindow(b);if(key==KeyEvent.KEYCODE_DPAD_LEFT?b[0]<a[0]:b[0]>a[0]){next.requestFocus();return true;}}
                if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(leftAnchor!=null)leftAnchor.requestFocus();}
                else if(actions.getChildCount()>0)actions.getChildAt(0).requestFocus();return true;
            }
        }
        if(right.hasFocus()) {
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(centreAnchor==null||!centreAnchor.requestFocus())toolbar.getChildAt(0).requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){int index=actions.indexOfChild(focused),next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<actions.getChildCount())actions.getChildAt(next).requestFocus();return true;}
        }
        return super.dispatchKeyEvent(event);
    }
}
