package com.archos.mediacenter.video.leanback.details;
import android.app.Application;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DetailsEnrichmentDeliveryTest {
 @Test public void sameFrameSectionsMergeAndObsoleteTitleCannotOverwriteThem()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},u->{});host.get().setContentView(page);
   PreviewDetailsData.Result first=new PreviewDetailsData.Result();first.details=new JSONObject("{title:'Current'}");PreviewDetailsData.Result second=new PreviewDetailsData.Result();second.credits=new JSONObject("{cast:[]}");
   enqueue(page,first,0);enqueue(page,second,0);PreviewDetailsData.Result pending=ReflectionHelpers.getField(page,"pendingEnrichment");assertEquals("Current",pending.details.getString("title"));assertNotNull(pending.credits);assertNull(ReflectionHelpers.getField(page,"enrichment"));
   PreviewDetailsData.Result old=new PreviewDetailsData.Result();old.details=new JSONObject("{title:'Old'}");enqueue(page,old,-1);pending=ReflectionHelpers.getField(page,"pendingEnrichment");assertEquals("Current",pending.details.getString("title"));
   host.pause().stop().destroy();assertNull(ReflectionHelpers.getField(page,"pendingEnrichment"));
  }finally{if(!host.get().isDestroyed())host.pause().stop().destroy();}
 }
 private void enqueue(PreviewMoviePage page,PreviewDetailsData.Result result,int generation){ReflectionHelpers.callInstanceMethod(page,"enqueueEnrichment",ReflectionHelpers.ClassParameter.from(PreviewDetailsData.Result.class,result),ReflectionHelpers.ClassParameter.from(int.class,generation));}
}
