package com.archos.mediacenter.video.leanback.scrapping;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import com.archos.mediascraper.*;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Exercises the retained Leanback fragment lifecycle behind the Preview search surface. */
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewMatchFragmentTest {
    public static class Fixture extends ManualScrappingSearchFragment {
        @Override public void onCreate(Bundle state){super.onCreate(state);setInitialQuery("Example");}
        protected ScrapeSearchResult performSearch(String text){return new ScrapeSearchResult(Collections.emptyList(),true,ScrapeStatus.NOT_FOUND,null);}
        protected BaseTags getTagFromSearchResult(SearchResult result){return null;}
        protected void saveTagsAndFinish(BaseTags tags){throw new AssertionError("No explicit match accepted");}
        protected String getResultsHeaderText(){return "Results";}
        protected String getEmptyText(){return "No matches";}
        protected BaseTags getNfoTags(){return null;}
    }
    @Test public void previewViewSurvivesNativeSearchLifecycleWithoutStartingSpeech(){
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().putBoolean("try_new_ui",true).commit();
        org.robolectric.android.controller.ActivityController<FragmentActivity> host=Robolectric.buildActivity(FragmentActivity.class).setup();
        try{
            Fixture fragment=new Fixture();host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,fragment).commitNow();
            View root=fragment.requireView();root.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));root.layout(0,0,960,540);
            assertNotNull(root.findViewWithTag("semantic:match.query"));assertTrue(fragment.focusPreviewSearch());
            assertTrue(root.findViewWithTag("semantic:keyboard:T").hasFocus());
            fragment.onQueryTextSubmit("");
        }finally{host.pause().stop().destroy();}
    }
}
