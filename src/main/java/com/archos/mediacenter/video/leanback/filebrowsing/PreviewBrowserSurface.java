package com.archos.mediacenter.video.leanback.filebrowsing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import androidx.leanback.widget.BrowseFrameLayout;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.*;
import java.util.function.Consumer;

/** Activity adapter for the universal browser. Protocol credentials stay in their native host. */
public final class PreviewBrowserSurface extends BrowseFrameLayout {
    private final UniversalFileBrowser browser;
    PreviewBrowserSurface(Activity activity,View legacy,Uri uri,View commands,Runnable options) {
        this(activity,null,uri,options);
    }
    public PreviewBrowserSurface(Activity activity,View providerContent,Uri uri,Runnable options) {
        super(activity);setId(R.id.grid_frame);setTag("preview-browser");
        browser=new UniversalFileBrowser(activity);browser.setPadding(dp(24),dp(14),dp(24),dp(16));
        if(providerContent!=null)browser.providerDock(providerContent,uri);
        TopNavigation navigation=new TopNavigation(activity,browser,index->navigate(activity,index),browser::atTop);
        navigation.selectTab(3);addView(navigation,new FrameLayout.LayoutParams(-1,-1));
    }
    public UniversalFileBrowser browser(){return browser;}
    void credentials(Consumer<Exception> handler,Consumer<Uri> changed){browser.credentials(handler,changed);}
    void open(Uri uri){browser.open(uri);}
    void focusItem(Object item,Runnable action){if(item instanceof com.archos.mediacenter.video.browser.adapters.object.Video)browser.focusVideo((com.archos.mediacenter.video.browser.adapters.object.Video)item);}
    public void providerItem(String title,String details,boolean folder,Runnable action){browser.providerInformation(title,details,folder,action);}
    private void navigate(Activity activity,int index) {
        if(index==4)activity.startActivity(new Intent(activity,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));
        else if(index==5)activity.startActivity(new Intent(activity,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));
        else {activity.startActivity(new Intent(activity,MainActivityLeanback.class).putExtra("preview_tab",index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));activity.finish();}
    }
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
