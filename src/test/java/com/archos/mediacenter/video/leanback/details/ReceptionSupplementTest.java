package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class ReceptionSupplementTest {
 @Test public void fourStableSourcesRetainThreeRulesAndAttribution()throws Exception{
  var c=RuntimeEnvironment.getApplication();var ratings=OmdbSupplement.parse(new JSONObject("{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'7.7',Metascore:'62',Awards:'Fixture nomination',Ratings:[{Source:'Rotten Tomatoes',Value:'77%'}]}"),"tt0089218","movie");
  DetailsBlueprintPanel panel=new DetailsBlueprintPanel(c,"Synopsis",java.util.Collections.emptyMap(),java.util.Collections.emptyMap(),java.util.Map.of("TMDB Rating","7.8 / 10","TMDb votes","123"),new LinearLayout(c),new LinearLayout(c),ratings);
  ViewGroup reception=panel.findViewWithTag("semantic:details.panel.reception");int rules=0,attributed=0;for(int n=0;n<reception.getChildCount();n++){View row=reception.getChildAt(n);if("details.separator".equals(row.getTag()))rules++;if(row instanceof LinearLayout){ViewGroup group=(ViewGroup)row;if(group.getChildCount()==2&&group.getChildAt(1) instanceof TextView){assertTrue(((TextView)group.getChildAt(1)).getText().toString().contains("via OMDb"));attributed++;}}}assertEquals(3,rules);assertEquals(4,attributed);assertFalse(panel.isFocusable());assertTrue(text(reception).contains("TMDb 7.8 / 10 · 123 votes"));assertTrue(text(reception).contains("Fixture nomination"));
 }
 @Test public void missingSupplementNeverBecomesZeroScores(){var c=RuntimeEnvironment.getApplication();DetailsBlueprintPanel panel=new DetailsBlueprintPanel(c,"Synopsis",java.util.Collections.emptyMap(),java.util.Collections.emptyMap(),java.util.Collections.emptyMap(),new LinearLayout(c),new LinearLayout(c),OmdbSupplement.Ratings.EMPTY);String reception=text(panel.findViewWithTag("semantic:details.panel.reception"));assertEquals(3,reception.split("No rating available",-1).length-1);assertTrue(reception.contains("Unavailable"));assertFalse(reception.contains("via OMDb"));assertFalse(reception.contains("0/10"));}
 private String text(View view){String out=view instanceof TextView?((TextView)view).getText().toString():"";if(view instanceof ViewGroup)for(int n=0;n<((ViewGroup)view).getChildCount();n++)out+="\n"+text(((ViewGroup)view).getChildAt(n));return out;}
}
