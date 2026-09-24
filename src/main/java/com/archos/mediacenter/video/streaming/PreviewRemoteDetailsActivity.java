package com.archos.mediacenter.video.streaming;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.details.PreviewMoviePage;
import java.util.concurrent.Future;

/** Provider-only discovery opens a Supernova Details page without creating a fake local file. */
public final class PreviewRemoteDetailsActivity extends Activity {
    private Future<?> work;private TopNavigation navigation;private PreviewMoviePage page;
    @Override public void onCreate(Bundle state){super.onCreate(state);String kind=getIntent().getStringExtra("kind");long id=getIntent().getLongExtra("tmdb_id",0);if(id<=0||!("movie".equals(kind)||"tv".equals(kind))){finish();return;}
        ArrayObjectAdapter actions=new ArrayObjectAdapter(new StreamingActionPresenter());
        page=new PreviewMoviePage(this,()->actions,a->StreamingActions.onClick(a),()->{},uri->{if(navigation!=null)navigation.setArtwork(uri);});
        navigation=new TopNavigation(this,page,index->{Intent intent;if(index==4)intent=new Intent(this,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class);else if(index==5)intent=new Intent(this,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class);else intent=new Intent(this,com.archos.mediacenter.video.leanback.MainActivityLeanback.class).putExtra("preview_tab",index);startActivity(intent);},page::atTop);navigation.selectTab(kind.equals("movie")?1:2);setContentView(navigation);
        work=StreamingRepository.IO.submit(()->{try{org.json.JSONObject details=StreamingRepository.metadata(getApplicationContext(),kind,id,"");StreamingRepository.Availability availability=StreamingRepository.load(getApplicationContext(),kind,id,StreamingRepository.country(this));java.util.List<StreamingRepository.Offer> offers=StreamingRepository.prefs(this).getBoolean(StreamingRepository.ENABLED,false)?StreamingRepository.filter(availability,StreamingRepository.selected(this),StreamingRepository.preferred(this)):java.util.Collections.emptyList();runOnUiThread(()->{if(isFinishing()||isDestroyed())return;for(StreamingRepository.Offer offer:offers)actions.add(new StreamingActionPresenter.LogoAction(offer.provider,()->open(kind,id,offer,availability)));page.bindRemote(details,kind,id);page.setSnapshot(PreviewLibraryLoader.memoryCache());page.focusPrimary();if(offers.isEmpty())PreviewNotice.show(this,"No availability on your configured services",false);});}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("remote_details_unavailable",error);runOnUiThread(()->{if(!isFinishing())PreviewDialog.read(this,"Details unavailable","Please try again when the metadata service is available.");});}});
    }
    private void open(String kind,long id,StreamingRepository.Offer offer,StreamingRepository.Availability availability){work=StreamingRepository.IO.submit(()->{String link=StreamingRepository.titleLink(getApplicationContext(),kind,id,StreamingRepository.country(this),availability,offer.provider.id);String resolved=StreamingRepository.resolveTitleUrl(link);runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())StreamingActions.openWeb(this,StreamingRepository.safeWebUrl(resolved)?resolved:availability.watchUrl);});});}
    @Override public void onDestroy(){if(work!=null)work.cancel(true);super.onDestroy();}
}
