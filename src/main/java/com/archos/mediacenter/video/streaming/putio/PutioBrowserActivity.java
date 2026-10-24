package com.archos.mediacenter.video.streaming.putio;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import com.archos.mediacenter.video.leanback.filebrowsing.PreviewBrowserSurface;

/** Standalone adapter; Network & Files embeds the same read-only provider controller. */
public final class PutioBrowserActivity extends ComponentActivity {
    private PutioBrowserController controller;
    @Override public void onCreate(Bundle state){super.onCreate(state);
        PreviewBrowserSurface surface=new PreviewBrowserSurface(this,null,android.net.Uri.parse("putio://put.io/"),()->{if(controller!=null)controller.options();});setContentView(surface);
        long folder=state==null?getIntent().getLongExtra("folder",0):state.getLong("folder",0);
        String title=state==null?getIntent().getStringExtra("folder_name"):state.getString("title","put.io");
        controller=new PutioBrowserController(this,surface.browser(),getIntent().getLongExtra("account",0),folder,title,this::finish,()->PutioAccountController.open(this));
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){public void handleOnBackPressed(){controller.back();}});
    }
    @Override protected void onSaveInstanceState(Bundle state){state.putLong("folder",controller.folder());state.putString("title",controller.title());super.onSaveInstanceState(state);}
    @Override protected void onDestroy(){if(controller!=null)controller.close();super.onDestroy();}
}
