package com.archos.mediacenter.video.leanback;

import android.content.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.browser.ShortcutDb;
import com.archos.mediaprovider.NetworkScanner;
import java.util.*;
import java.util.function.*;

/** Shared source workspace: location, items, then context. Focus does not open a second screen. */
public final class PreviewNetworkWorkspace extends LinearLayout {
    private final LinearLayout rail, items, context;
    private final List<TextView> sections = new ArrayList<>();
    private TextView selectedSection;
    private View selectedItem;
    private String area = "";
    private final List<Box> volumes;
    private final List<Shortcut> sources, saved;
    private final Consumer<Box> browseVolume;
    private final Consumer<String> browseNetwork;

    public PreviewNetworkWorkspace(Context c, List<Box> volumes, List<Shortcut> sources,
            List<Shortcut> saved, Consumer<Box> browseVolume, Consumer<String> browseNetwork) {
        super(c); this.volumes = volumes; this.sources = sources; this.saved = saved;
        this.browseVolume = browseVolume; this.browseNetwork = browseNetwork;
        setClipChildren(false); setClipToPadding(false);
        rail = new PreviewFocusRail(c);rail.setOrientation(VERTICAL); items = column(); context = column();
        addView(rail, new LayoutParams(0, -1, .23f));
        ScrollView middle = new ScrollView(c); middle.setClipChildren(false); middle.addView(items);
        LayoutParams middleSize = new LayoutParams(0, -1, .45f); middleSize.setMargins(dp(12), 0, dp(12), 0); addView(middle, middleSize);
        ScrollView right = new ScrollView(c); right.setClipChildren(false); right.addView(context);
        addView(right, new LayoutParams(0, -1, .32f));
        for (String name : new String[]{"Overview", "Local Storage", "Network Shares", "Cloud Services", "Saved Locations"}) {
            TextView section = control(name, () -> items.requestFocus());
            section.setBackground(null);section.setTag("semantic:network.category." + name.toLowerCase(java.util.Locale.ROOT).replace(' ', '_')); sections.add(section); rail.addView(section, new LayoutParams(-1, dp(42)));
            section.setOnFocusChangeListener((v, focused) -> { if (focused) { selectedSection = section; show(name); } });
        }
        selectedSection = sections.get(0); show("Overview");
    }
    private void show(String name) {
        if (name.equals(area)) return;
        int previous=items.getChildCount();String reason=area.isEmpty()?"initial":"category_changed";
        area = name; items.removeAllViews(); context.removeAllViews(); selectedItem = null;
        com.archos.mediacenter.video.diagnostics.Diagnostics.uiState("network",name,"workspace","none","none",0);
        if (name.equals("Overview")) {
            item("scan_library", "Scan Library", () -> { heading("Scan Library"); description("Check local storage and indexed network sources. Progress continues when you leave this page."); action("scan_library", "Scan Library", () -> PreviewLibraryScan.request(getContext())); status(true); });
            item("network_scanning", "Network Scanning", this::scanControls);
        } else if (name.equals("Local Storage")) {
            for (Box box : volumes) if (box.getBoxId() == Box.ID.FOLDERS || box.getBoxId() == Box.ID.USB || box.getBoxId() == Box.ID.SDCARD || box.getBoxId() == Box.ID.OTHER) {
                String title = box.getBoxId() == Box.ID.FOLDERS ? "Internal Storage" : box.getName();
                item("volume."+box.getBoxId().name().toLowerCase(Locale.ROOT)+"."+volumes.indexOf(box), title, () -> { heading(title); String path = box.getPath(); if (path == null) path = android.os.Environment.getExternalStorageDirectory().getPath();
                    java.io.File file = new java.io.File(path); description(path + "\n\n" + (file.canRead() ? "Available" : "Unavailable") + "\n" + android.text.format.Formatter.formatFileSize(getContext(), file.getUsableSpace()) + " free of " + android.text.format.Formatter.formatFileSize(getContext(), file.getTotalSpace())); action("browse", "Browse", () -> browseVolume.accept(box)); });
            }
            if (items.getChildCount() == 0) items.addView(label("No storage volumes are currently available", 14));
        } else if (name.equals("Network Shares")) {
            for (Shortcut source : sources) source(source, true);
            item("add_source", "Add Network Source", () -> { heading("Add Network Source"); description("SMB · WebDAV (HTTPS/HTTP) · SFTP · FTP · FTP over TLS\n\nConnect, then choose a Movies or TV Shows folder."); action("connect", "Connect", () -> browseNetwork.accept("add")); });
            item("discover", "Discover Devices", () -> { heading("Discover Devices"); description("Discover computers/NAS using SMB and media servers using DLNA/UPnP. FTP and SFTP require a server address."); action("discover_smb", "Computers & NAS", () -> browseNetwork.accept("smb")); action("discover_upnp", "Media Servers", () -> browseNetwork.accept("upnp")); });
        } else if (name.equals("Saved Locations")) {
            if (saved.isEmpty()) items.addView(label("No saved locations. Use Add to Saved Locations while browsing a folder.", 14));
            for (Shortcut source : saved) source(source, false);
        } else {
            item("putio", "put.io", () -> { heading("put.io"); description("Connect your put.io account for native account and library management. Original-quality playback uses your WebDAV source."); action("putio_account", "Account / Connection", () -> com.archos.mediacenter.video.streaming.putio.PutioAccountController.open(getContext())); });
            for (String provider : new String[]{"Google Drive", "OneDrive", "Dropbox"}) {
                TextView unavailable = label(provider + " · Coming soon", 14); unavailable.setPadding(dp(10), dp(14), dp(10), dp(14)); unavailable.setAlpha(.5f); items.addView(unavailable);
            }
        }
        if (items.getChildCount() > 0 && items.getChildAt(0).isFocusable()) {
            // Populate context without entering the middle panel.
            Object action = items.getChildAt(0).getTag(); if (action instanceof Runnable) ((Runnable) action).run();
        }
        com.archos.mediacenter.video.diagnostics.Diagnostics.uiRebuild(items,"network.items",reason,previous,items.getChildCount(),false);
    }
    private void source(Shortcut source, boolean indexed) {
        item((indexed?"source.":"saved.")+source.getId(), source.getName(), () -> {
            heading(source.getName()); description((indexed ? "Library source" : "Saved browsing location") + "\n" + protocol(source.getUri()) + "\n" + source.getUri().getHost() + "\n" + source.getUri().getPath());
            action("browse", "Browse", () -> getContext().startActivity(new Intent(getContext(), ListingActivity.getActivityForUri(source.getUri())).putExtra(ListingActivity.EXTRA_ROOT_URI, source.getUri()).putExtra(ListingActivity.EXTRA_ROOT_NAME, source.getName())));
            if (indexed) action("scan_source", "Scan Source", () -> NetworkScanner.scanVideos(getContext(), source.getUri()));
            else action("add_library", "Add to Library", () -> PreviewFolderActions.chooseLibrary(getContext(), source.getUri(), source.getName()));
            action("remove_location", indexed ? "Remove from Library" : "Remove Saved Location", () -> PreviewDialog.choose(getContext(), "Remove this " + (indexed ? "library source" : "saved location") + "? Media files will be kept.", new String[]{"Cancel", "Remove"}, 0, n -> {
                if (n != 1) return;
                boolean removed;
                try { removed=indexed?ShortcutDbAdapter.VIDEO.deleteShortcut(getContext(), source.getId()):ShortcutDb.STATIC.removeShortcut(getContext(), source.getUri())>0; }
                catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("source_removal_failed",failure);removed=false;}
                if(!removed){
                    com.archos.mediacenter.video.diagnostics.Diagnostics.event("source_removal_failed","domain",indexed?"library_source":"saved_location","failure_category","not_confirmed");
                    PreviewDialog.read(getContext(),"Removal not confirmed","The location could not be confirmed as removed. It remains visible; refresh the library or export diagnostics before retrying. Media files have not been deleted.");
                    return;
                }
                if (indexed) { NetworkScanner.removeIndexedVideos(getContext(), source.getUri()); sources.remove(source); }
                else saved.remove(source);
                String previous = area; area = ""; selectedSection.requestFocus(); show(previous);
            }));
        });
    }
    private void scanControls() {
        heading("Network Scanning");
        Context c = getContext(); android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);
        int period = com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(c);
        action("automatic", "Automatic: " + (period > 0 ? "On" : "Off"), () -> { if (period > 0) prefs.edit().putInt("preview_scan_frequency", period).apply(); com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(c, 0, period > 0 ? 0 : prefs.getInt("preview_scan_frequency", 3600000), true); refreshContext(0); });
        action("frequency", "Frequency: " + Math.max(15, (period > 0 ? period : prefs.getInt("preview_scan_frequency", 3600000)) / 60000) + " minutes", () -> PreviewDialog.choose(c, "Frequency", new String[]{"15 minutes", "30 minutes", "1 hour", "6 hours", "24 hours"}, java.util.Arrays.asList(900000,1800000,3600000,21600000,86400000).indexOf(period>0?period:prefs.getInt("preview_scan_frequency",3600000)), n -> { int value = new int[]{900000,1800000,3600000,21600000,86400000}[n]; prefs.edit().putInt("preview_scan_frequency", value).apply(); if(period > 0) com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(c,0,value,true); refreshContext(1); }));
        action("on_return", "On open / return: " + (prefs.getBoolean("auto_rescan_on_app_restart",true) ? "On" : "Off"), () -> { prefs.edit().putBoolean("auto_rescan_on_app_restart", !prefs.getBoolean("auto_rescan_on_app_restart",true)).apply(); refreshContext(2); });
        action("sources_included", "Sources Included", () -> PreviewNetworkScanning.sources(c));
        status(false); action("scan_now", "Scan Now", () -> PreviewLibraryScan.requestNetwork(c));
    }
    private void refreshContext(int button) { context.removeAllViews(); scanControls(); context.getChildAt(button + 1).requestFocus(); }
    private void status(boolean library) { TextView status = label(scanStatus(getContext(),library),12); context.addView(status); status.post(new Runnable(){public void run(){if(!status.isAttachedToWindow())return;status.setText(scanStatus(getContext(),library));status.postDelayed(this,1000);}}); }
    static String scanStatus(Context context,boolean library){return library?PreviewLibraryScan.libraryStatus(context):PreviewLibraryScan.status(context);}
    private void item(String identity, String title, Runnable update) {
        Runnable show = () -> { int previous=context.getChildCount();context.removeAllViews(); update.run();com.archos.mediacenter.video.diagnostics.Diagnostics.uiRebuild(context,"network.context","selection_changed",previous,context.getChildCount(),false); };
        TextView row = control(title, () -> context.requestFocus()); row.setTag(show); com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(row,"network.item."+identity);
        row.setOnFocusChangeListener((v, focused) -> { if(focused){selectedItem=v;show.run();} }); items.addView(row,new LayoutParams(-1,dp(44)));
    }
    private void heading(String title) { context.addView(label(title,19)); }
    private void description(String text) { TextView label=label(text,13);label.setPadding(0,dp(12),0,dp(12));context.addView(label); }
    private void action(String identity, String label, Runnable action) { TextView row=control(label,action);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(row,"network.action."+identity);context.addView(row,new LayoutParams(-1,dp(42))); }
    private TextView control(String label, Runnable action) { TextView row=label(label,14);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(8),0,dp(8),0);row.setFocusable(true);row.setFocusableInTouchMode(true);row.setBackground(PreviewDialog.focus(getContext()));row.setOnClickListener(v->action.run());return row; }
    private TextView label(String value,int size){TextView label=new TextView(getContext());label.setText(value);label.setTextSize(size);label.setTextColor(-1);return label;}
    private LinearLayout column(){LinearLayout column=new LinearLayout(getContext());column.setOrientation(VERTICAL);column.setClipChildren(false);column.setClipToPadding(false);return column;}
    private int dp(int value){return PreviewDialog.dp(getContext(),value);}
    public boolean atTop(){return sections.get(0).hasFocus();}
    public static String protocol(Uri uri){String scheme=uri.getScheme();if(scheme==null)return "Local storage";switch(scheme.toLowerCase(Locale.ROOT)){case "file":return "Local storage";case "https":case "davs":return "WebDAV · HTTPS";case "http":case "dav":return "WebDAV · HTTP";case "smb":return "SMB";case "sftp":return "SFTP";case "ftps":return "FTP over TLS";case "ftp":return "FTP";default:return scheme.toUpperCase(Locale.ROOT);}}
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){int key=event.getKeyCode();
            if(rail.hasFocus()) {int index=sections.indexOf(findFocus());if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;if(key==KeyEvent.KEYCODE_DPAD_RIGHT){items.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_DOWN){if(index>=0&&index+1<sections.size())sections.get(index+1).requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_UP&&index>0){sections.get(index-1).requestFocus();return true;}}
            if(items.hasFocus()&&key==KeyEvent.KEYCODE_DPAD_LEFT){selectedSection.requestFocus();return true;}
            if(items.hasFocus()&&key==KeyEvent.KEYCODE_DPAD_RIGHT){context.requestFocus();return true;}
            if(context.hasFocus()&&key==KeyEvent.KEYCODE_DPAD_LEFT){if(selectedItem!=null)selectedItem.requestFocus();else items.requestFocus();return true;}
            if(context.hasFocus()&&key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
