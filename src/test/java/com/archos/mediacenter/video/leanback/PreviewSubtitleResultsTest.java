package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class PreviewSubtitleResultsTest {
    @Before
    public void isolatePreviewTransport() {
        PreviewAsyncFixtures.offlineTransport();
    }

    @After
    public void drainPreviewWorkers() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    @Test
    public void evenSingleResultNeedsExplicitDownloadAndBackPreservesSelection() {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        OpenSubtitlesSearchResult result = new OpenSubtitlesSearchResult("42", "Example.en.srt", "English");
        result.setMoviehashMatch(true);
        AtomicReference<OpenSubtitlesSearchResult> downloaded = new AtomicReference<>();
        Dialog parent = PreviewSubtitleResults.show(host, Collections.singletonList(result), downloaded::set);
        View row = (View) parent.getWindow().getDecorView().findViewWithTag("preview-label:0").getParent();
        row.performClick();
        Dialog review = ShadowDialog.getLatestDialog();
        assertNull(downloaded.get());
        assertNotNull(PreviewPagesTest.findText(review.getWindow().getDecorView(), "Source: OpenSubtitles"));
        review.dismiss();
        assertTrue(parent.isShowing());
        assertNull(downloaded.get());
        row.performClick();
        review = ShadowDialog.getLatestDialog();
        View confirm = exact(review.getWindow().getDecorView(), "Download");
        assertNotNull(confirm);
        confirm.performClick();
        assertSame(result, downloaded.get());
        assertFalse(parent.isShowing());
    }

    private static View exact(View view, String text) {
        if (view instanceof TextView && text.contentEquals(((TextView) view).getText()))
            return view;
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View match = exact(((ViewGroup) view).getChildAt(i), text);
                if (match != null)
                    return match;
            }
        return null;
    }

    @Test
    public void approvedPlaybackRouteDoesNotReplaceTheDefaultSubtitleDialog() {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        android.app.Dialog approved = PreviewSubtitleResults.showPlayback(host,
            List.of(new OpenSubtitlesSearchResult("fixture", "Fixture subtitle.srt", "en-GB")), r -> {
            });
        Assert.assertNotNull(approved.getWindow().getDecorView().findViewWithTag("subtitles.download"));
        Assert.assertNotNull(approved.getWindow().getDecorView().findViewWithTag("subtitles.result.0"));
        approved.dismiss();
    }
}
