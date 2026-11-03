package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Checks reception scores, counts, logos and attribution preserve genuinely supplied data. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class ReceptionSupplementTest {
    @Test
    public void genuineReviewCountSurvivesMissingScoreWithoutInventingRating() {
        var c = RuntimeEnvironment.getApplication();
        var ratings = OmdbSupplement.Ratings.EMPTY.withReviewCounts("3", "4");
        DetailsBlueprintPanel panel = new DetailsBlueprintPanel(c, "Synopsis", Map.of(), Map.of(),
            Map.of(), new LinearLayout(c), new LinearLayout(c), ratings);
        assertNotNull(panel.findViewWithTag("details.reception.logo.1"));
        assertTrue(text(panel).contains("Rating unavailable"));
        assertTrue(text(panel).contains("Tomatometer (3 reviews)"));
        assertTrue(text(panel).contains("Metascore (4 reviews)"));
        assertFalse(text(panel).contains("0/10"));
    }

    @Test
    public void originalReceptionHasRealLogosCaptionsAndCountsBelowScores() throws Exception {
        var c = RuntimeEnvironment.getApplication();
        var ratings =
            OmdbSupplement
                .parse(
                    new JSONObject(
                        "{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'7.7',imdbVotes:'447,000',Metascore:'62',Awards:'4 wins & 8 nominations. Award detail',Ratings:[{Source:'Rotten Tomatoes',Value:'77%'}]}"),
                    "tt0089218", "movie")
                .withReviewCounts("338", "20");
        DetailsBlueprintPanel panel = new DetailsBlueprintPanel(c, "Synopsis", Map.of(), Map.of(),
            Map.of(), new LinearLayout(c), new LinearLayout(c), ratings);
        for (int n = 0; n < 4; n++) {
            ImageView logo = panel.findViewWithTag("details.reception.logo." + n);
            assertNotNull(logo);
            assertNotNull(logo.getDrawable());
            assertFalse(logo.isFocusable());
        }
        for (int n = 0; n < 3; n++) {
            TextView score = panel.findViewWithTag("details.reception.score." + n),
                     count = panel.findViewWithTag("details.reception.count." + n);
            ViewGroup group = (ViewGroup) score.getParent();
            assertSame(group, count.getParent());
            assertTrue(group.indexOfChild(count) > group.indexOfChild(score));
        }
        assertTrue(text(panel).contains("447K votes"));
        assertTrue(text(panel).contains("Tomatometer (338 reviews)"));
        assertTrue(text(panel).contains("Metascore (20 reviews)"));
        assertTrue(text(panel).contains("Award detail"));
        assertNull(panel.findViewWithTag("details.reviews"));
        var updated = ratings.withReviewCounts("339", "21");
        assertNotEquals(ratings.countsSignature(), updated.countsSignature());
    }

    /** Owner decision 2 Nov 2026: TMDb is shown alongside the OMDb sources (previously hidden). */
    @Test
    public void allSourcesIncludingTmdbStackWithRulesAndAttribution() throws Exception {
        var c = RuntimeEnvironment.getApplication();
        var ratings = OmdbSupplement.parse(
            new JSONObject(
                "{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'7.7',Metascore:'62',Awards:'Fixture nomination',Ratings:[{Source:'Rotten Tomatoes',Value:'77%'}]}"),
            "tt0089218", "movie");
        DetailsBlueprintPanel panel = new DetailsBlueprintPanel(c, "Synopsis", Collections.emptyMap(),
            Collections.emptyMap(), Map.of("TMDB Rating", "7.8 / 10", "TMDb votes", "123"),
            new LinearLayout(c), new LinearLayout(c), ratings);
        ViewGroup reception = panel.findViewWithTag("semantic:details.panel.reception");
        int rules = 0;
        List<String> slots = new ArrayList<>();
        for (int n = 0; n < reception.getChildCount(); n++) {
            View row = reception.getChildAt(n);
            if ("details.separator".equals(row.getTag()))
                rules++;
            if (String.valueOf(row.getTag()).startsWith("details.reception.slot.")) {
                assertFalse(text(row).trim().isEmpty());
                slots.add(String.valueOf(row.getTag()));
            }
        }
        assertEquals("IMDb, Rotten Tomatoes, Metacritic, TMDb, Awards",
            List.of("details.reception.slot.0", "details.reception.slot.1", "details.reception.slot.2",
                "details.reception.slot.4", "details.reception.slot.3"),
            slots);
        assertEquals(4, rules);
        assertTrue(reception.getContentDescription().toString().contains("via OMDb"));
        assertFalse(panel.isFocusable());
        assertNull(panel.findViewWithTag("details.reception.primary"));
        assertTrue(text(reception).contains("7.8/10"));
        assertTrue(text(reception).contains("123 votes"));
        assertTrue(text(reception).contains("Fixture nomination"));
        assertNull("no spacer when the panel is full", panel.findViewWithTag("details.reception.spacer"));
        assertTrue("missing counts name the measure", text(reception).contains("Tomatometer"));
        assertFalse(text(reception).contains("unavailable"));
    }

    @Test
    public void missingSupplementNeverBecomesZeroScores() {
        var c = RuntimeEnvironment.getApplication();
        DetailsBlueprintPanel panel = new DetailsBlueprintPanel(c, "Synopsis", Collections.emptyMap(),
            Collections.emptyMap(), Collections.emptyMap(), new LinearLayout(c),
            new LinearLayout(c), OmdbSupplement.Ratings.EMPTY);
        assertNotNull(panel.findViewWithTag("semantic:details.panel.reception"));
        assertTrue(text(panel).contains("Ratings unavailable"));
        assertFalse(text(panel).contains("Reviews unavailable"));
    }

    /** Owner Shield photo 2 Nov 2026: with TMDb only, "15,820 votes" drifted to the panel floor. Votes must sit directly beneath the score. */
    @Test
    public void tmdbOnlyRowKeepsVotesDirectlyBeneathScore() {
        var c = RuntimeEnvironment.getApplication();
        DetailsBlueprintPanel panel = new DetailsBlueprintPanel(c, "Synopsis", Collections.emptyMap(),
            Collections.emptyMap(), Map.of("TMDB Rating", "7.8 / 10", "TMDb votes", "15,820"),
            new LinearLayout(c), new LinearLayout(c), OmdbSupplement.Ratings.EMPTY);
        ViewGroup reception = panel.findViewWithTag("semantic:details.panel.reception");
        assertNotNull(reception);
        assertNotNull(panel.findViewWithTag("details.reception.slot.4"));
        assertNotNull(panel.findViewWithTag("details.reception.logo.4"));
        TextView score = panel.findViewWithTag("details.reception.score.4"),
                 count = panel.findViewWithTag("details.reception.count.4");
        assertEquals("7.8/10", score.getText().toString());
        assertEquals("15,820 votes", count.getText().toString());
        ViewGroup group = (ViewGroup) score.getParent();
        assertSame(group, count.getParent());
        assertEquals(group.indexOfChild(score) + 1, group.indexOfChild(count));
        for (int i = 0; i < 4; i++)
            assertNull(reception.findViewWithTag("details.reception.slot." + i));
        assertNotNull("a lone row is padded, not stretched", panel.findViewWithTag("details.reception.spacer"));
        int w = View.MeasureSpec.makeMeasureSpec(1200, View.MeasureSpec.EXACTLY),
            h = View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY);
        panel.measure(w, h);
        panel.layout(0, 0, 1200, 600);
        View slot = panel.findViewWithTag("details.reception.slot.4");
        assertTrue(
            "row keeps a quarter-panel height, not the whole panel", slot.getHeight() < reception.getHeight() / 2);
        assertTrue("votes sit close beneath the score", count.getTop() - score.getBottom() < score.getHeight());
    }

    @Test
    public void reviewsAreAbsentEvenWhenLegacyCallerSuppliesReader() {
        var c = RuntimeEnvironment.getApplication();
        int[] clicks = {
            0
        };
        DetailsBlueprintPanel panel =
            new DetailsBlueprintPanel(c, "Synopsis", Map.of(), Map.of(), Map.of(),
                new LinearLayout(c), new LinearLayout(c), OmdbSupplement.Ratings.EMPTY, () -> clicks[0]++);
        assertNotNull(panel.findViewWithTag("details.reception.unavailable"));
        assertNull(panel.findViewWithTag("details.reviews"));
        assertEquals(0, clicks[0]);
    }

    private String text(View view) {
        String out = view instanceof TextView ? ((TextView) view).getText().toString() : "";
        if (view instanceof ViewGroup)
            for (int n = 0; n < ((ViewGroup) view).getChildCount(); n++)
                out += "\n" + text(((ViewGroup) view).getChildAt(n));
        return out;
    }
}
