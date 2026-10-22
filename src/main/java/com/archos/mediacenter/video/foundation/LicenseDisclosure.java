package com.archos.mediacenter.video.foundation;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import org.json.*;
import java.nio.charset.StandardCharsets;

/** Shared offline disclosure for retained TV and non-TV licence entry activities. */
public final class LicenseDisclosure extends LinearLayout {
    private final LinearLayout catalogue;
    private final FrameLayout detail;
    private final java.util.concurrent.ExecutorService io=java.util.concurrent.Executors.newSingleThreadExecutor();
    private int generation;
    public LicenseDisclosure(Context context){
        super(context);setOrientation(HORIZONTAL);setPadding(PreviewDialog.dp(context,24),PreviewDialog.dp(context,20),PreviewDialog.dp(context,24),PreviewDialog.dp(context,20));setBackground(new PreviewUtilityBackground(context));
        ScrollView scroll=new ScrollView(context);catalogue=new LinearLayout(context);catalogue.setOrientation(VERTICAL);SharedThreePanel.heading(catalogue,"Open Source Licences");scroll.addView(catalogue);addView(scroll,new LayoutParams(0,-1,.34f));detail=new FrameLayout(context);LayoutParams right=new LayoutParams(0,-1,.66f);right.leftMargin=PreviewDialog.dp(context,20);addView(detail,right);
        io.execute(()->{try{JSONArray entries=new JSONObject(read("foundation/licences.json")).getJSONArray("components");post(()->{for(int i=0;i<entries.length();i++){JSONObject entry=entries.optJSONObject(i);if(entry==null)continue;TextView row=TvButton.create(context,entry.optString("name"),()->load(entry),false);catalogue.addView(row,new LayoutParams(-1,-2));SharedThreePanel.divider(catalogue);}if(catalogue.getChildCount()>1){catalogue.getChildAt(1).requestFocus();JSONObject first=entries.optJSONObject(0);if(first!=null)load(first);}});}catch(Exception error){post(()->SharedThreePanel.heading(catalogue,"Bundled notices unavailable"));}});
    }
    private String read(String asset)throws java.io.IOException{if(!asset.startsWith("foundation/")||asset.contains(".."))throw new java.io.IOException("Invalid notice asset");try(java.io.InputStream input=getContext().getAssets().open(asset)){return new String(input.readAllBytes(),StandardCharsets.UTF_8);}}
    private void load(JSONObject entry){int request=++generation;io.execute(()->{String text;try{text=entry.optString("name")+"\n"+entry.optString("version")+"\n"+entry.optString("licence")+"\n\n"+read(entry.optString("text_asset"));}catch(Exception unavailable){text="Bundled licence text unavailable";}String body=text;post(()->{if(request!=generation||!isAttachedToWindow())return;detail.removeAllViews();detail.addView(new BundledNoticeReader(getContext(),body,()->catalogue.requestFocus()),new FrameLayout.LayoutParams(-1,-1));});});}
    @Override protected void onDetachedFromWindow(){generation++;io.shutdownNow();super.onDetachedFromWindow();}
}
