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
  ViewGroup reception=panel.findViewWithTag("semantic:details.panel.reception");int rules=0,attributed=0;for(int n=0;n<reception.getChildCount();n++){View row=reception.getChildAt(n);if("details.separator".equals(row.getTag()))rules++;if(String.valueOf(row.getTag()).startsWith("details.reception.slot.")){assertFalse(text(row).trim().isEmpty());attributed++;}}assertEquals(3,rules);assertEquals(4,attributed);assertTrue(text(reception).contains("Supplementary data via OMDb"));assertFalse(panel.isFocusable());assertTrue(text(reception).contains("TMDb 7.8 / 10"));assertTrue(text(reception).contains("123 votes"));assertTrue(text(reception).contains("Fixture nomination"));
 }
 @Test public void missingSupplementNeverBecomesZeroScores(){var c=RuntimeEnvironment.getApplication();DetailsBlueprintPanel panel=new DetailsBlueprintPanel(c,"Synopsis",java.util.Collections.emptyMap(),java.util.Collections.emptyMap(),java.util.Collections.emptyMap(),new LinearLayout(c),new LinearLayout(c),OmdbSupplement.Ratings.EMPTY);assertNull(panel.findViewWithTag("semantic:details.panel.reception"));}
 @Test public void primaryRatingIsProminentAndEmptyProvidersAreAbsent(){var c=RuntimeEnvironment.getApplication();DetailsBlueprintPanel panel=new DetailsBlueprintPanel(c,"Synopsis",java.util.Collections.emptyMap(),java.util.Collections.emptyMap(),java.util.Map.of("TMDB Rating","8.0 / 10","TMDb votes","406"),new LinearLayout(c),new LinearLayout(c),OmdbSupplement.Ratings.EMPTY);ViewGroup reception=panel.findViewWithTag("semantic:details.panel.reception");assertNotNull(reception);TextView score=panel.findViewWithTag("details.reception.primary");assertNotNull(score);assertTrue(score.getTextSize()>((TextView)reception.getChildAt(0)).getTextSize());assertTrue(text(reception).contains("406 votes"));assertFalse(text(reception).contains("No rating available"));for(int i=0;i<4;i++)assertNull(reception.findViewWithTag("details.reception.slot."+i));}

 private String text(View view){String out=view instanceof TextView?((TextView)view).getText().toString():"";if(view instanceof ViewGroup)for(int n=0;n<((ViewGroup)view).getChildCount();n++)out+="\n"+text(((ViewGroup)view).getChildAt(n));return out;}
}
