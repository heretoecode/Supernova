package com.archos.mediacenter.video.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import com.archos.mediacenter.video.utils.CanonicalMatchProviderTest;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediascraper.MovieTags;
import com.archos.mediascraper.ScrapeDetailResult;
import com.archos.mediascraper.ScrapeStatus;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class FixMatchRepositoryTest
{
    @Test public void exactIdCandidateAppliesOwnedMetadataWithoutChangingWatchedOrProgress() throws Exception
    {
        CanonicalMatchProviderTest fixture = new CanonicalMatchProviderTest();
        fixture.setup();
        Application app = RuntimeEnvironment.getApplication();
        try
        {
            FixMatchRepository repository = new FixMatchRepository(
                app,
                (path, query)
                    -> new JSONObject("{id:789,title:'Fresh',release_date:'2024-01-01',poster_path:'/poster.jpg'}"),
                (context, requested, file) -> {
                    MovieTags tags = new MovieTags();
                    tags.setOnlineId(789);
                    tags.setTitle("Fresh");
                    tags.setTitleLanguage("en");
                    tags.setPlot("New details");
                    return new ScrapeDetailResult(tags, true, null, ScrapeStatus.OKAY, null);
                });
            List<FixMatchRepository.Candidate> result = repository.search("tmdb:movie:789", "all", "movie", 789);
            assertEquals(1, result.size());
            assertTrue(result.get(0).currentMatch);
            assertEquals("2024", result.get(0).year);
            repository.apply(10, result.get(0), -1, -1, () -> false);
            try (
                VideoOpenHelper helper = new VideoOpenHelper(app);
                var rows = helper.getReadableDatabase().rawQuery(
                    "SELECT bookmark,Archos_lastTimePlayed,Archos_traktSeen,m_online_id FROM video WHERE _id=10", null))
            {
                assertTrue(rows.moveToFirst());
                assertEquals(4444, rows.getLong(0));
                assertEquals(100, rows.getLong(1));
                assertEquals(1, rows.getLong(2));
                assertEquals(789, rows.getLong(3));
            }
        }
        finally
        {
            fixture.close();
        }
    }
}
