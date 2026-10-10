package com.archos.mediacenter.video.foundation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.StatFs;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewAccent;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** About alone uses the approved four-child rail and flexible centre/right panels. */
public final class FoundationAboutWorkspace extends LinearLayout {
    public static final String[] SECTIONS = {"Release Notes", "Open-source Licences", "Credits & Acknowledgements", "Technical Information"};
    private final View normalMiddle, normalHelp;
    private final LinearLayout rail;
    private final List<TextView> navigation = new ArrayList<>();
    private final Map<String,Integer> selections = new HashMap<>();
    private final Set<String> expanded = new HashSet<>();
    private final List<TextView> rows = new ArrayList<>();
    private final List<Runnable> rowSelections = new ArrayList<>();
    private final ScrollView catalogue, details;
    private final LinearLayout catalogueBody, detailsBody;
    private TextView parentButton, selectedNavigation;
    private String section = "About";
    private int selectedIndex, requestGeneration;
    private ExecutorService reader;
    private boolean licencesOpened;
    private final Map<String,String> legalCache = new java.util.LinkedHashMap<String,String>() { protected boolean removeEldestEntry(Map.Entry<String,String> entry) { return size()>4; } };

    public FoundationAboutWorkspace(PreferenceFragmentCompat fragment, LinearLayout split,
                                    View middle, View help, LinearLayout links) {
        this(fragment.requireContext(),split,middle,help,links);
    }
    public FoundationAboutWorkspace(Context context, LinearLayout split,View middle,View help,LinearLayout links) {
        super(context);
        normalMiddle=middle; normalHelp=help; rail=links;
        setOrientation(HORIZONTAL); setVisibility(GONE); setPadding(dp(12),0,0,0);
        catalogueBody=column(); detailsBody=column();
        catalogue=scroll(catalogueBody); details=scroll(detailsBody);
        com.archos.mediacenter.video.leanback.SharedThreePanel.decorate(catalogue);com.archos.mediacenter.video.leanback.SharedThreePanel.decorate(details);
        addView(catalogue,new LayoutParams(0,-1,.44f));
        LayoutParams right=new LayoutParams(0,-1,.56f); right.leftMargin=dp(20); addView(details,right);
        split.addView(this,new LayoutParams(0,-1,.72f));
    }
    private LinearLayout column(){LinearLayout v=new LinearLayout(getContext());v.setOrientation(VERTICAL);return v;}
    private ScrollView scroll(LinearLayout body){ScrollView v=new ScrollView(getContext());v.setFillViewport(false);v.setClipToPadding(false);v.addView(body,new ScrollView.LayoutParams(-1,-2));return v;}
    private TextView text(String value,int size){TextView v=new TextView(getContext());v.setText(value);v.setTextColor(Color.WHITE);v.setTextSize(size);v.setLineSpacing(dp(3),1);return v;}
    private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
    public void show(TextView aboutButton) {
        parentButton=aboutButton;
        if(navigation.isEmpty()) for(int index=0;index<SECTIONS.length;index++) {
            final String name=SECTIONS[index]; final int position=index;
            TextView child=text(name,18); child.setPadding(dp(24),0,dp(6),0);child.setGravity(Gravity.CENTER_VERTICAL);
            child.setFocusable(true);child.setTag("semantic:settings:about:"+name);child.setBackground(PreviewDialog.focus(getContext()));com.archos.mediacenter.video.leanback.SharedThreePanel.rowSeparator(child);
            rail.addView(child,new LayoutParams(-1,dp(44)));navigation.add(child);
            child.setOnFocusChangeListener((v,focused)->{if(focused)select(name,child);});
            child.setOnClickListener(v->{child.requestFocus();select(name,child);enterContentInternal();});
            child.setOnKeyListener((v,key,event)->{
                if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
                if(key==KeyEvent.KEYCODE_DPAD_RIGHT){enterContentInternal(false);return true;}
                if(key==KeyEvent.KEYCODE_DPAD_LEFT){parentButton.requestFocus();return true;}
                if(key==KeyEvent.KEYCODE_DPAD_UP){(position==0?parentButton:navigation.get(position-1)).requestFocus();return true;}
                if(key==KeyEvent.KEYCODE_DPAD_DOWN){if(position+1<navigation.size())navigation.get(position+1).requestFocus();return true;}
                return false;
            });
        }
        for(TextView child:navigation)child.setVisibility(VISIBLE);
        normalMiddle.setVisibility(GONE);normalHelp.setVisibility(GONE);setVisibility(VISIBLE);
        select("About",parentButton);
    }
    public void hide(){requestGeneration++;licencesOpened=false;for(TextView child:navigation)child.setVisibility(GONE);setVisibility(GONE);normalMiddle.setVisibility(VISIBLE);normalHelp.setVisibility(VISIBLE);}
    public void collapseNavigation(){for(TextView child:navigation)child.setVisibility(GONE);}
    public void enterContent(){enterContentInternal();}
    public void enter(){if(selectedNavigation==parentButton&&!navigation.isEmpty())navigation.get(0).requestFocus();else if(selectedNavigation!=null)selectedNavigation.requestFocus();}
    public void openSection(int index){if(index>=0&&index<navigation.size()){navigation.get(index).requestFocus();select(SECTIONS[index],navigation.get(index));}}
    public boolean navigationFocused(){for(TextView v:navigation)if(v.hasFocus())return true;return false;}
    public boolean handleBack(){
        if(details.hasFocus()){focusSelectedRow();return true;}
        if(catalogue.hasFocus()){selectedNavigation.requestFocus();return true;}
        if(navigationFocused()){parentButton.requestFocus();return true;}
        return false;
    }
    private void select(String name,TextView child){
        if(selectedNavigation!=null)selectedNavigation.setTextColor(Color.WHITE);
        selectedNavigation=child; child.setTextColor(Color.WHITE);
        if(name.equals(section)&&(catalogueBody.getChildCount()>0||detailsBody.getChildCount()>0))return;
        section=name;selectedIndex=selections.getOrDefault(name,0);requestGeneration++;
        catalogueBody.removeAllViews();detailsBody.removeAllViews();detailsBody.setGravity(Gravity.TOP);detailsBody.setPadding(0,0,0,0);((LayoutParams)details.getLayoutParams()).weight=.56f;rows.clear();rowSelections.clear();
        catalogue.setVisibility(VISIBLE);details.setVisibility(VISIBLE);
        try {
            if(name.equals("About"))appInformation();
            else if(name.equals(SECTIONS[0]))releases();
            else if(name.equals(SECTIONS[1])) { licencesOpened=false; showDetails("Open-source Licences", "Press OK to open the locally bundled software licences and notices.", null, null); }
            else if(name.equals(SECTIONS[2]))credits();
            else technical();
        } catch(Exception unavailable){showDetails("Information unavailable","This build could not read the bundled information.",null,null);}
        if(!rows.isEmpty()){selectedIndex=Math.min(selectedIndex,rows.size()-1);rowSelections.get(selectedIndex).run();}
    }
    private void enterContentInternal(){enterContentInternal(true);}
    private void enterContentInternal(boolean confirmed){if(section.equals(SECTIONS[1])&&!licencesOpened&&confirmed){licencesOpened=true;loadLicences();return;}if(!rows.isEmpty())focusSelectedRow();else for(int i=0;i<detailsBody.getChildCount();i++)if(detailsBody.getChildAt(i).isFocusable()){detailsBody.getChildAt(i).requestFocus();break;}}
    private void focusSelectedRow(){if(!rows.isEmpty())rows.get(Math.min(selectedIndex,rows.size()-1)).requestFocus();else selectedNavigation.requestFocus();}
    private TextView row(String label,Runnable select,Runnable activate){
        final int index=rows.size();TextView row=text(label,14);row.setPadding(dp(10),dp(10),dp(8),dp(10));
        row.setFocusable(true);row.setBackground(PreviewDialog.focus(getContext()));row.setTag("semantic:about:row:"+section+":"+index);
        catalogueBody.addView(row,new LayoutParams(-1,-2));rows.add(row);
        Runnable selected=()->{selectedIndex=index;selections.put(section,index);select.run();};rowSelections.add(selected);
        row.setOnFocusChangeListener((v,focused)->{if(focused)selected.run();});
        row.setOnClickListener(v->{selected.run();if(activate!=null)activate.run();});
        row.setOnKeyListener((v,key,event)->{
            if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){selectedNavigation.requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(detailsBody.getChildCount()>0)detailsBody.getChildAt(0).requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){int next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<rows.size())rows.get(next).requestFocus();return true;}
            return false;
        });
        com.archos.mediacenter.video.leanback.SharedThreePanel.divider(catalogueBody);
        return row;
    }
    private void appInformation(){
        catalogue.setVisibility(GONE);details.setFillViewport(true);((LayoutParams)details.getLayoutParams()).weight=1f;
        detailsBody.setGravity(Gravity.CENTER);detailsBody.setPadding(dp(20),dp(28),dp(20),dp(20));
        ImageView logo=new ImageView(getContext());logo.setImageResource(R.drawable.foundation_ring);logo.setContentDescription("Supernova double-ring logo");
        detailsBody.addView(logo,new LayoutParams(dp(116),dp(116)));
        TextView wordmark=text("SUPERNOVA",28);wordmark.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));wordmark.setGravity(Gravity.CENTER);
        detailsBody.addView(wordmark,new LayoutParams(-1,-2));
        TextView info=text("Version "+BuildConfig.VERSION_NAME+"\n\nBased on Nova Video Player 6.4.64\n\nApplication: "+BuildConfig.APPLICATION_ID+"\nBuilt (UTC): "+BuildConfig.PREVIEW_BUILD_UTC,16);
        info.setPadding(0,dp(20),0,0);info.setGravity(Gravity.START);info.setFocusable(true);
        detailsBody.addView(info,new LayoutParams(-2,-2));
        wordmark.setFocusable(true);wordmark.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_LEFT){selectedNavigation.requestFocus();return true;}return false;});
        info.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_LEFT){selectedNavigation.requestFocus();return true;}return false;});
    }
    private void releases() throws Exception {
        JSONObject history=readJson("foundation/release-history.json");
        JSONObject current=history.getJSONObject("current");
        releaseRow(current,true);
        JSONArray old=history.getJSONArray("history");for(int i=0;i<old.length();i++)releaseRow(old.getJSONObject(i),false);
    }
    private void releaseRow(JSONObject entry,boolean current) throws Exception {
        String version=current?BuildConfig.VERSION_NAME:entry.getString("version");
        String label=version+" · "+(current?"Foundation · current":entry.optString("kind","Development build"));
        String date=current?BuildConfig.PREVIEW_BUILD_UTC:entry.optString("date","Date unavailable");
        String notes=notes(entry.optJSONArray("notes"));
        String details=notes+(current?"":"\n\nRetrospective build label; the original APK's embedded version was not changed.");
        TextView row=row(label+"\n"+date,()->showDetails(version,details,null,null),null);
        final String heading=label+"\n"+date;
        if(current){expanded.add(version);row.setText(heading);}
        else {
            row.setText("▸ "+heading);
            row.setOnClickListener(v->{selectedIndex=rows.indexOf(row);selections.put(section,selectedIndex);showDetails(version,details,null,null);
                if(expanded.contains(version))expanded.remove(version);else expanded.add(version);
                row.setText((expanded.contains(version)?"▾ ":"▸ ")+heading);});
            if(expanded.contains(version))row.setText("▾ "+heading);
        }
    }
    private String notes(JSONArray notes){StringBuilder out=new StringBuilder();if(notes!=null)for(int i=0;i<notes.length();i++)out.append("• ").append(notes.optString(i)).append('\n');return out.toString().trim();}
    private ExecutorService reader() {
        if(reader==null||reader.isShutdown())reader=new java.util.concurrent.ThreadPoolExecutor(1,1,0,java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.ArrayBlockingQueue<>(2),r->{Thread t=new Thread(r,"SupernovaLegalReader");t.setDaemon(true);return t;},new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy());
        return reader;
    }
    private void loadLicences() {
        final int generation=++requestGeneration;
        showDetails("Open-source Licences","Loading locally bundled catalogue…",null,null);
        reader().execute(()->{
            try {
                JSONArray components=readJson("foundation/licences.json").getJSONArray("components");
                post(()->{
                    if(generation!=requestGeneration||!isAttachedToWindow()||!section.equals(SECTIONS[1]))return;
                    catalogueBody.removeAllViews();com.archos.mediacenter.video.leanback.SharedThreePanel.heading(catalogueBody,"Components");rows.clear();rowSelections.clear();
                    for(int i=0;i<components.length();i++){
                        JSONObject component=components.optJSONObject(i);if(component==null)continue;
                        String name=component.optString("name"),version=component.optString("version","Version unavailable");
                        row(name+"\n"+component.optString("role","Software component"),()->loadLegal(component,name+" · "+version),null);
                    }
                    if(!rows.isEmpty())focusSelectedRow();
                });
            } catch(Exception error){post(()->{if(generation==requestGeneration)showDetails("Information unavailable","This build could not read the bundled licence catalogue.",null,null);});}
        });
    }
    private void loadLegal(JSONObject component,String name) {
        final int generation=++requestGeneration;String asset=component.optString("text_asset");
        String cached=legalCache.get(asset);
        if(cached!=null){showDetails(name,cached,component.optString("url"),null);return;}
        showDetails(name,component.optString("licence")+"\n\nLoading locally bundled licence and notices…",null,null);
        reader().execute(()->{
            String legal;try{legal=readText(asset);}catch(Exception unavailable){legal="Bundled licence text unavailable. This is a build-conformance failure.";}
            final String body=component.optString("licence")+"\n\n"+legal;
            post(()->{if(generation==requestGeneration&&isAttachedToWindow()){legalCache.put(asset,body);showDetails(name,body,component.optString("url"),null);}});
        });
    }
    private void credits() throws Exception {
        JSONArray entries=readJson("foundation/credits.json").getJSONArray("entries");String previous="";
        for(int i=0;i<entries.length();i++){
            JSONObject entry=entries.getJSONObject(i);String group=entry.getString("group");
            if(!group.equals(previous)){TextView heading=text(group,15);heading.setPadding(dp(10),dp(14),0,dp(4));heading.setFocusable(false);catalogueBody.addView(heading);previous=group;}
            row(entry.getString("name")+"\n"+entry.getString("role"),()->showDetails(entry.optString("name"),entry.optString("text"),entry.optString("url"),entry.optString("logo")),null);
        }
    }
    private void technical(){
        row("Application & Build",()->showDetails("Application & Build","Version: "+BuildConfig.VERSION_NAME+"\nVersion code: "+BuildConfig.VERSION_CODE+"\nApplication ID: "+BuildConfig.APPLICATION_ID+"\nBuild type: "+BuildConfig.BUILD_TYPE+"\nSource: "+BuildConfig.PREVIEW_GIT_SHA+"\nBuilt (UTC): "+BuildConfig.PREVIEW_BUILD_UTC+"\nUpstream: Nova Video Player 6.4.64",null,null),null);
        row("Device & Android TV",()->showDetails("Device & Android TV","Manufacturer: "+Build.MANUFACTURER+"\nModel: "+Build.MODEL+"\nAndroid: "+Build.VERSION.RELEASE+"\nAPI: "+Build.VERSION.SDK_INT+"\nDevice ABIs: "+android.text.TextUtils.join(", ",Build.SUPPORTED_ABIS),null,null),null);
        row("Playback & Media Capabilities",()->{
            Set<String> types=new java.util.TreeSet<>();try{for(android.media.MediaCodecInfo codec:new android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS).getCodecInfos())if(!codec.isEncoder())java.util.Collections.addAll(types,codec.getSupportedTypes());}catch(RuntimeException unavailable){types.clear();}
            showDetails("Playback & Media Capabilities","Android-reported decoder types:\n"+(types.isEmpty()?"Unavailable":android.text.TextUtils.join("\n",types))+"\n\nReported support is not a playback guarantee. File-specific information remains in the existing media Technical Information view.",null,null);
        },null);
        row("Library & Storage",()->{
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot snapshot=com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
            String count=snapshot==null?"Unavailable":String.valueOf(snapshot.technical.size());
            String free="Unavailable",capacity="Unavailable";
            try { StatFs fs=new StatFs(getContext().getFilesDir().getAbsolutePath());free=android.text.format.Formatter.formatFileSize(getContext(),fs.getAvailableBytes());capacity=android.text.format.Formatter.formatFileSize(getContext(),fs.getTotalBytes()); } catch(RuntimeException unavailable) { /* Keep this read-only view usable when storage is unavailable. */ }
            showDetails("Library & Storage","Cached library files: "+count+"\nApp filesystem free: "+free+"\nApp filesystem capacity: "+capacity+"\n\nCached count is not an authoritative full-library scan. Network paths and account details are not displayed.",null,null);
        },null);
        row("Diagnostics",()->showDetails("Diagnostics","Optional diagnostic logging: "+(com.archos.mediacenter.video.diagnostics.Diagnostics.enabled()?"On":"Off")+"\n\nUse the existing Settings controls for logging and export. No new diagnostic viewer or upload is added.",null,null),null);
    }
    private void showDetails(String heading,String body,String url,String logo){
        ((LayoutParams)details.getLayoutParams()).weight=.56f;details.setFillViewport(false);detailsBody.setGravity(Gravity.TOP);detailsBody.setPadding(dp(8),dp(8),dp(8),dp(16));
        boolean restore=details.hasFocus();detailsBody.removeAllViews();TextView title=text(heading,20);title.setFocusable(true);detailsBody.addView(title);
        if("tmdb".equals(logo)){
            int id=R.drawable.tmdb_banner;
            if(id!=0){ImageView image=new ImageView(getContext());image.setImageResource(id);image.setContentDescription("TMDB");detailsBody.addView(image,new LayoutParams(dp(160),dp(48)));}
        }
        if(url!=null&&!url.isEmpty()){
            ImageView image=new ImageView(getContext());image.setContentDescription("QR code for the official "+heading+" website");detailsBody.addView(image,new LayoutParams(dp(124),dp(124)));
            final int generation=requestGeneration;
            reader().execute(()->{Bitmap bitmap=qr(url);post(()->{if(generation==requestGeneration&&image.getParent()==detailsBody&&isAttachedToWindow())image.setImageBitmap(bitmap);else if(bitmap!=null)bitmap.recycle();});});
        }
        TextView text=text(body,14);if(section.equals(SECTIONS[1]))com.archos.mediacenter.video.leanback.PreviewIcon.apply(text,"info",16);text.setFocusable(true);text.setPadding(0,dp(12),0,0);detailsBody.addView(text);
        View.OnKeyListener keys=(v,key,event)->{
            if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){focusSelectedRow();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){details.smoothScrollBy(0,dp(key==KeyEvent.KEYCODE_DPAD_UP?-48:48));return true;}
            return false;
        };
        title.setOnKeyListener(keys);text.setOnKeyListener(keys);details.scrollTo(0,0);details.requestLayout();if(restore)title.requestFocus();
    }
    private Bitmap qr(String url){
        try {
            java.net.URI parsed=java.net.URI.create(url);
            if(!"https".equals(parsed.getScheme())||parsed.getHost()==null||parsed.getUserInfo()!=null||parsed.getQuery()!=null||parsed.getFragment()!=null)return null;
            BitMatrix matrix=new QRCodeWriter().encode(url,BarcodeFormat.QR_CODE,256,256,java.util.Collections.singletonMap(EncodeHintType.MARGIN,4));
            Bitmap bitmap=Bitmap.createBitmap(256,256,Bitmap.Config.ARGB_8888);
            int[] pixels=new int[256*256];for(int y=0;y<256;y++)for(int x=0;x<256;x++)pixels[y*256+x]=matrix.get(x,y)?Color.BLACK:Color.WHITE;
            bitmap.setPixels(pixels,0,256,0,0,256,256);return bitmap;
        }catch(Exception invalid){return null;}
    }
    private JSONObject readJson(String asset) throws Exception{return new JSONObject(readText(asset));}
    private String readText(String asset) throws Exception {
        if(!asset.startsWith("foundation/")||asset.contains(".."))throw new IllegalArgumentException();
        try(InputStream in=getContext().getAssets().open(asset);ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
            byte[] buffer=new byte[4096];int n;while((n=in.read(buffer))!=-1){if(bytes.size()+n>1024*1024)throw new java.io.IOException("Bundled information exceeds the reader limit");bytes.write(buffer,0,n);}return bytes.toString(StandardCharsets.UTF_8.name());
        }
    }
    @Override protected void onDetachedFromWindow(){requestGeneration++;if(reader!=null)reader.shutdownNow();super.onDetachedFromWindow();}
}
