package com.archos.mediacenter.video.leanback.details;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import androidx.leanback.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import java.util.concurrent.*;

/** A season is a configuration of the shared Details Card, over real local episode inventory. */
public final class PreviewSeasonActivity extends LeanbackActivity {
    private PreviewMoviePage page;private Tvshow series;private Snapshot library;
    private final ExecutorService loader=Executors.newSingleThreadExecutor();private boolean closed;private int season;
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);season=getIntent().getIntExtra("season",-1);
        page=new PreviewMoviePage(this,ArrayObjectAdapter::new,a->{},()->{if(series!=null)VideoViewClickedListener.showTvshowDetails(this,series,new Presenter.ViewHolder(page));},uri->{});
        TopNavigation navigation=new TopNavigation(this,page,this::navigate,page::atTop);navigation.selectTab(2);setContentView(navigation);
        library=PreviewLibraryLoader.memoryCache();if(library!=null)bind(library);
        PreviewLibraryLoader source=new PreviewLibraryLoader(this);loader.execute(()->{try(Cursor ignored=source.loadInBackground()){Snapshot snapshot=source.snapshot;runOnUiThread(()->{if(!closed&&snapshot!=null)bind(snapshot);});}catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("season_library_load",failure);}});
    }
    private void bind(Snapshot snapshot){
        library=snapshot;long show=getIntent().getLongExtra("show",0);Entry identity=null;for(Entry entry:snapshot.shows)if(entry.show==show)identity=entry;
        if(identity==null||season<0){PreviewNotice.show(this,"This season is no longer in the library",false);finish();return;}
        series=(Tvshow)identity.media;List<Episode> episodes=new ArrayList<>();List<Entry> choices=new ArrayList<>();for(Entry entry:snapshot.episodes)if(entry.show==show&&((Episode)entry.media).getSeasonNumber()==season){episodes.add((Episode)entry.media);choices.add(entry);}
        Entry target=PreviewLibraryLoader.next(choices);if(target==null&&!choices.isEmpty())target=choices.get(0);Entry playable=target;
        page.setSnapshot(snapshot);page.bindSeason(series,season,episodes,()->{if(playable!=null)com.archos.mediacenter.video.utils.PlayUtils.startVideo(this,(Video)playable.media,com.archos.mediacenter.video.player.PlayerActivity.RESUME_FROM_LAST_POS,false,-1,null,-1);});
        if(!page.hasFocus())page.post(page::focusPrimary);
    }
    private void navigate(int tab){
        if(tab==4)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));
        else if(tab==5)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));
        else if(tab==6)startActivity(new Intent(this,CustomLibraryActivity.class));
        else{startActivity(new Intent(this,MainActivityLeanback.class).putExtra("preview_tab",tab).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish();}
    }
    @Override protected void onDestroy(){closed=true;loader.shutdownNow();super.onDestroy();}
}
