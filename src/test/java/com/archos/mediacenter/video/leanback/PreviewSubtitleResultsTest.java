package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.View;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSubtitleResultsTest {
    @Test public void evenSingleResultNeedsExplicitDownloadAndBackPreservesSelection(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();OpenSubtitlesSearchResult result=new OpenSubtitlesSearchResult("42","Example.en.srt","English");result.setMoviehashMatch(true);
        AtomicReference<OpenSubtitlesSearchResult> downloaded=new AtomicReference<>();Dialog parent=PreviewSubtitleResults.show(host,Collections.singletonList(result),downloaded::set);
        View row=(View)parent.getWindow().getDecorView().findViewWithTag("preview-label:0").getParent();row.performClick();Dialog review=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertNull(downloaded.get());
        assertNotNull(PreviewPagesTest.findText(review.getWindow().getDecorView(),"Source: OpenSubtitles"));review.dismiss();assertTrue(parent.isShowing());assertNull(downloaded.get());
        row.performClick();review=org.robolectric.shadows.ShadowDialog.getLatestDialog();View confirm=exact(review.getWindow().getDecorView(),"Download");assertNotNull(confirm);confirm.performClick();
        assertSame(result,downloaded.get());assertFalse(parent.isShowing());
    }
    private static View exact(View view,String text){if(view instanceof android.widget.TextView&&text.contentEquals(((android.widget.TextView)view).getText()))return view;if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++){View match=exact(((android.view.ViewGroup)view).getChildAt(i),text);if(match!=null)return match;}return null;}
}
