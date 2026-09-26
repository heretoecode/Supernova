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
        rail = column(); items = column(); context = column();
        addView(rail, new LayoutParams(0, -1, .23f));
        ScrollView middle = new ScrollView(c); middle.setClipChildren(false); middle.addView(items);
        LayoutParams middleSize = new LayoutParams(0, -1, .45f); middleSize.setMargins(dp(12), 0, dp(12), 0); addView(middle, middleSize);
        ScrollView right = new ScrollView(c); right.setClipChildren(false); right.addView(context);
        addView(right, new LayoutParams(0, -1, .32f));
        for (String name : new String[]{"Overview", "Local Storage", "Network Shares", "Cloud Services", "Saved Locations"}) {
            TextView section = control(name, () -> items.requestFocus());
            section.setTag("network:" + name); sections.add(section); rail.addView(section, new LayoutParams(-1, dp(42)));
            section.setOnFocusChangeListener((v, focused) -> { if (focused) { selectedSection = section; show(name); } });
        }
        selectedSection = sections.get(0); show("Overview");
    }
    private void show(String name) {
        if (name.equals(area)) return;
        area = name; items.removeAllViews(); context.removeAllViews(); selectedItem = null;
        if (name.equals("Overview")) {
            item("Scan Library", () -> { heading("Scan Library"); description("Check local storage and indexed network sources. Progress continues when you leave this page."); action("Scan Library", () -> PreviewLibraryScan.request(getContext())); status(); });
            item("Network Scanning", this::scanControls);
        } else if (name.equals("Local Storage")) {
            for (Box box : volumes) if (box.getBoxId() == Box.ID.FOLDERS || box.getBoxId() == Box.ID.USB || box.getBoxId() == Box.ID.SDCARD || box.getBoxId() == Box.ID.OTHER) {
                String title = box.getBoxId() == Box.ID.FOLDERS ? "Internal Storage" : box.getName();
                item(title, () -> { heading(title); String path = box.getPath(); if (path == null) path = android.os.Environment.getExternalStorageDirectory().getPath();
                    java.io.File file = new java.io.File(path); description(path + "\n\n" + (file.canRead() ? "Available" : "Unavailable") + "\n" + android.text.format.Formatter.formatFileSize(getContext(), file.getUsableSpace()) + " free of " + android.text.format.Formatter.formatFileSize(getContext(), file.getTotalSpace())); action("Browse", () -> browseVolume.accept(box)); });
            }
            if (items.getChildCount() == 0) items.addView(label("No storage volumes are currently available", 14));
        } else if (name.equals("Network Shares")) {
            for (Shortcut source : sources) source(source, true);
            item("Add Network Source", () -> { heading("Add Network Source"); description("SMB · WebDAV (HTTPS/HTTP) · SFTP · FTP · FTP over TLS\n\nConnect, then choose a Movies or TV Shows folder."); action("Connect", () -> browseNetwork.accept("add")); });
            item("Discover Devices", () -> { heading("Discover Devices"); description("Discover computers/NAS using SMB and media servers using DLNA/UPnP. FTP and SFTP require a server address."); action("Computers & NAS", () -> browseNetwork.accept("smb")); action("Media Servers", () -> browseNetwork.accept("upnp")); });
        } else if (name.equals("Saved Locations")) {
            if (saved.isEmpty()) items.addView(label("No saved locations. Use Add to Saved Locations while browsing a folder.", 14));
            for (Shortcut source : saved) source(source, false);
        } else {
            item("put.io", () -> { heading("put.io"); description("Native account linking requires the registered Supernova OAuth client configuration. Existing WebDAV library access and playback remain available."); });
            for (String provider : new String[]{"Google Drive", "OneDrive", "Dropbox"}) {
                TextView unavailable = label(provider + " · Coming soon", 14); unavailable.setPadding(dp(10), dp(14), dp(10), dp(14)); unavailable.setAlpha(.5f); items.addView(unavailable);
            }
        }
        if (items.getChildCount() > 0 && items.getChildAt(0).isFocusable()) {
            // Populate context without entering the middle panel.
            Object action = items.getChildAt(0).getTag(); if (action instanceof Runnable) ((Runnable) action).run();
        }
    }
    private void source(Shortcut source, boolean indexed) {
        item(source.getName(), () -> {
            heading(source.getName()); description((indexed ? "Library source" : "Saved browsing location") + "\n" + protocol(source.getUri()) + "\n" + source.getUri().getHost() + "\n" + source.getUri().getPath());
            action("Browse", () -> getContext().startActivity(new Intent(getContext(), ListingActivity.getActivityForUri(source.getUri())).putExtra(ListingActivity.EXTRA_ROOT_URI, source.getUri()).putExtra(ListingActivity.EXTRA_ROOT_NAME, source.getName())));
            if (indexed) action("Scan Source", () -> NetworkScanner.scanVideos(getContext(), source.getUri()));
            else action("Add to Library", () -> PreviewFolderActions.chooseLibrary(getContext(), source.getUri(), source.getName()));
            action(indexed ? "Remove from Library" : "Remove Saved Location", () -> PreviewDialog.choose(getContext(), "Remove this " + (indexed ? "library source" : "saved location") + "? Media files will be kept.", new String[]{"Cancel", "Remove"}, 0, n -> {
                if (n != 1) return;
                if (indexed) { if (ShortcutDbAdapter.VIDEO.deleteShortcut(getContext(), source.getId())) NetworkScanner.removeIndexedVideos(getContext(), source.getUri()); sources.remove(source); }
                else { ShortcutDb.STATIC.removeShortcut(getContext(), source.getUri()); saved.remove(source); }
                String previous = area; area = ""; selectedSection.requestFocus(); show(previous);
            }));
        });
    }
    private void scanControls() {
        heading("Network Scanning");
        Context c = getContext(); android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);
        int period = com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(c);
        action("Automatic: " + (period > 0 ? "On" : "Off"), () -> { if (period > 0) prefs.edit().putInt("preview_scan_frequency", period).apply(); com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(c, 0, period > 0 ? 0 : prefs.getInt("preview_scan_frequency", 3600000), true); refreshContext(0); });
        action("Frequency: " + Math.max(15, (period > 0 ? period : prefs.getInt("preview_scan_frequency", 3600000)) / 60000) + " minutes", () -> PreviewDialog.choose(c, "Frequency", new String[]{"15 minutes", "30 minutes", "1 hour", "6 hours", "24 hours"}, -1, n -> { int value = new int[]{900000,1800000,3600000,21600000,86400000}[n]; prefs.edit().putInt("preview_scan_frequency", value).apply(); if(period > 0) com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(c,0,value,true); refreshContext(1); }));
        action("On open / return: " + (prefs.getBoolean("auto_rescan_on_app_restart",true) ? "On" : "Off"), () -> { prefs.edit().putBoolean("auto_rescan_on_app_restart", !prefs.getBoolean("auto_rescan_on_app_restart",true)).apply(); refreshContext(2); });
        action("Sources Included", () -> PreviewNetworkScanning.sources(c));
        status(); action("Scan Now", () -> PreviewLibraryScan.requestNetwork(c));
    }
    private void refreshContext(int button) { context.removeAllViews(); scanControls(); context.getChildAt(button + 1).requestFocus(); }
    private void status() { TextView status = label(PreviewNetworkScanning.lastResult(getContext()),12); context.addView(status); status.post(new Runnable(){public void run(){if(!status.isAttachedToWindow())return;status.setText(PreviewLibraryScan.status(getContext()));status.postDelayed(this,1000);}}); }
    private void item(String title, Runnable update) {
        Runnable show = () -> { context.removeAllViews(); update.run(); };
        TextView row = control(title, () -> context.requestFocus()); row.setTag(show);
        row.setOnFocusChangeListener((v, focused) -> { if(focused){selectedItem=v;show.run();} }); items.addView(row,new LayoutParams(-1,dp(44)));
    }
    private void heading(String title) { context.addView(label(title,19)); }
    private void description(String text) { TextView label=label(text,13);label.setPadding(0,dp(12),0,dp(12));context.addView(label); }
    private void action(String label, Runnable action) { context.addView(control(label,action),new LayoutParams(-1,dp(42))); }
    private TextView control(String label, Runnable action) { TextView row=label(label,14);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(8),0,dp(8),0);row.setFocusable(true);row.setFocusableInTouchMode(true);row.setBackground(PreviewDialog.focus(getContext()));row.setOnClickListener(v->action.run());return row; }
    private TextView label(String value,int size){TextView label=new TextView(getContext());label.setText(value);label.setTextSize(size);label.setTextColor(-1);return label;}
    private LinearLayout column(){LinearLayout column=new LinearLayout(getContext());column.setOrientation(VERTICAL);column.setClipChildren(false);column.setClipToPadding(false);return column;}
    private int dp(int value){return PreviewDialog.dp(getContext(),value);}
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
