package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import com.archos.mediacenter.video.leanback.PreviewMetadataCache;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, manifest = Config.NONE)
public class DetailsDataCTest
{
    private DetailsTechnical.File file(long id, long bytes)
    {
        return new DetailsTechnical.File(id, "indexed-revision-1", 3840, 2160, "HEVC", -1, null, 24000, 1001,
            12000000, List.of(new DetailsTechnical.Audio("EAC3", "5.1", "English")), List.of("English"),
            "Matroska", bytes, "NAS", "/Shows", 1000);
    }

    @Test public void exactFileSnapshotKeepsUnknownsAndRationalFrameRate()
    {
        var facts = DetailsTechnical.read(file(1, 100));
        assertEquals(1, facts.videoId());
        assertEquals("3840 × 2160", facts.resolution());
        assertEquals(-1, facts.bitDepth());
        assertEquals("Unknown", facts.hdr());
        assertEquals(23.976, facts.framesPerSecond(), .001);
        assertEquals(List.of("English"), facts.audioLanguages());
        assertEquals("5.1", facts.audio().get(0).channels());
        assertEquals(150, DetailsTechnical.averageEpisodeBytes(List.of(file(1, 100), file(1, 100), file(2, 200))));
        assertEquals(-1, DetailsTechnical.averageEpisodeBytes(List.of(file(1, 100), file(2, -1))));
        assertEquals(-1, DetailsTechnical.averageEpisodeBytes(List.of(file(1, Long.MAX_VALUE), file(2, 100))));
    }

    @Test public void indexedAdapterUsesOneRowAndDoesNotExposeSourceCredentials()
    {
        try (var row = new android.database.MatrixCursor(new String[] {
            "_id", "_data", "_size", "width", "height", "date_added", "Archos_videoBitRate"}))
        {
            row.addRow(new Object[] {1, "https://user:secret@nas.example/Shows/episode.mkv?token=private", 100,
                                     1920, 1080, 1000, 12000});
            row.moveToFirst();
            var facts = DetailsTechnical.read(IndexedTechnical.read(row, 1));
            assertEquals("nas.example", facts.connection());
            assertEquals("/Shows/", facts.folder());
            assertEquals(12000000, facts.bitrateBitsPerSecond());
            assertEquals(1000000, facts.addedUtcMs());
            assertEquals("Unknown", facts.container());
            assertEquals(-1, facts.framesPerSecond(), 0);
        }
    }

    @Test public void heroIdentitySurvivesEnrichmentAndCannotCrossTitleOrKind()
    {
        var handover = new HeroHandover("movie", 1, "https://image.tmdb.org/t/p/original/shown.jpg");
        assertEquals(handover.artworkIdentity(), handover.resolve("movie", 1, "different-default.jpg"));
        assertEquals("default", handover.resolve("tv", 1, "default"));
        assertEquals("default", handover.resolve("movie", 2, "default"));
    }

    @Test public void receptionIsCompleteOfflineAndRejectsMismatchedTmdb() throws Exception
    {
        var context = RuntimeEnvironment.getApplication();
        PreviewMetadataCache.load(context, "movie", 501, "", () -> new JSONObject().put("id", 501)
            .put("vote_average", 6.1).put("vote_count", 18500));
        var cached = DetailsReception.cached(context, "movie", 501);
        assertEquals("6.1", cached.tmdb().rating());
        assertEquals("18500", cached.tmdb().votes());
        assertEquals("Unknown", cached.imdb().rating());
        assertEquals("Unknown", cached.rottenTomatoes().rating());
        assertEquals("Unknown", cached.metacritic().rating());
        assertEquals("Unknown", cached.awards());
        assertEquals("Unknown", DetailsReception.read(999, new JSONObject().put("id", 501)
            .put("vote_average", 10), OmdbSupplement.Ratings.EMPTY).tmdb().rating());
    }
}
