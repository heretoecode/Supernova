package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import com.archos.mediacenter.video.utils.backup.CanonicalMatchExecutor;
import com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial;
import com.archos.mediacenter.video.utils.backup.CanonicalNativeRecovery;
import com.archos.mediacenter.video.utils.backup.MediaMatchInventory;
import com.archos.mediacenter.video.utils.backup.MediaStateRecovery;
import com.archos.mediacenter.video.utils.backup.PortableMediaMatches;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import com.archos.mediascraper.MatchRestoreOperations;
import com.archos.mediascraper.MovieTags;
import com.archos.mediascraper.EpisodeTags;
import com.archos.mediascraper.OwnedCanonicalBatch;
import com.archos.mediascraper.ScrapeDetailResult;
import com.archos.mediascraper.ScrapeStatus;
import com.archos.mediascraper.ShowTags;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowContentResolver;
import static org.junit.Assert.*;
import org.robolectric.util.ReflectionHelpers;

/** Existing canonical tags/provider behavior, not a new match-restoration implementation. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class CanonicalMatchProviderTest {
    private Application app;
    private DbHolder holder;

    @Before
    public void setup() {
        app = RuntimeEnvironment.getApplication();
        app.deleteDatabase("media.db");
        holder = new DbHolder(new VideoOpenHelper(app));
        ScraperProvider.hookUriMatcher(new UriMatcher(UriMatcher.NO_MATCH));
        ScraperProvider scraper = new ScraperProvider(app, holder);
        ContentProvider provider = new ContentProvider() {
            @Override
            public boolean onCreate() {
                return true;
            }
            @Override
            public String getType(Uri uri) {
                return null;
            }
            @Override
            public Cursor query(Uri uri, String[] columns, String where, String[] args, String order) {
                return uri.getAuthority().equals(ScraperStore.AUTHORITY)
                    ? scraper.query(uri, columns, where, args, order)
                    : holder.get().query(
                          VideoOpenHelper.VIDEO_VIEW_NAME, columns, itemSelection(uri, where), args, null, null, order);
            }
            @Override
            public Uri insert(Uri uri, ContentValues values) {
                return scraper.insert(uri, values);
            }
            @Override
            public int update(Uri uri, ContentValues values, String where, String[] args) {
                return uri.getAuthority().equals(ScraperStore.AUTHORITY)
                    ? scraper.update(uri, values, where, args)
                    : holder.get().update("files", values, where, args);
            }
            @Override
            public int delete(Uri uri, String where, String[] args) {
                return scraper.delete(uri, where, args);
            }
        };
        provider.attachInfo(app, null);
        ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY, provider);
        ShadowContentResolver.registerProviderInternal(ScraperStore.AUTHORITY, provider);
        var db = holder.get();
        db.setForeignKeyConstraintsEnabled(true);
        db.execSQL(
            "INSERT INTO files(_id,remote_id,media_type,_data,ArchosMediaScraper_type,ArchosMediaScraper_id,bookmark,Archos_lastTimePlayed,Archos_traktSeen) VALUES(10,10,3,'file:///one.mkv',11,100,4444,100,1),(11,11,3,'file:///other.mkv',11,200,5555,200,1)");
        db.execSQL(
            "INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie,plot_movie) VALUES(100,10,123,9,'Original title','Original facts'),(200,11,456,8,'Other title','Other facts')");
        db.execSQL(
            "INSERT INTO user_collection(_id,name,sort_name,position,hidden) VALUES(7,'Receiving legacy collection','Receiving legacy collection',2,0)");
        db.execSQL("INSERT INTO user_collection_item(_id,collection_id,media_type,media_id) VALUES(9,7,1,100)");
    }

    @After
    public void close() {
        holder.close();
    }

    private static String itemSelection(Uri uri, String where) {
        String last = uri.getLastPathSegment();
        if (last != null && last.matches("[0-9]+"))
            return "_id=" + Long.parseLong(last) + (where == null ? "" : " AND (" + where + ")");
        return where;
    }

    private MovieTags tags() {
        MovieTags tags = new MovieTags();
        tags.setOnlineId(789);
        tags.setTitle("Fresh canonical title");
        tags.setPlot("Fresh canonical facts");
        tags.setTitleLanguage("en");
        tags.addActorIfAbsent("Fresh actor", "Role");
        tags.addGenreIfAbsent("Comedy");
        return tags;
    }

    @Test
    public void canonicalProviderChangesJoinedFactsAndOuterRollbackRestoresOriginalPinAndReceivingCollections() {
        var db = holder.get();
        db.beginTransaction();
        try {
            long id = tags().save(app, 10);
            assertTrue(id > 0);
            assertNotEquals(100, id);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "m_online_id", "m_name", "m_plot", "bookmark", "Archos_lastTimePlayed", "Archos_traktSeen"
                     },
                     "_id=10", null, null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(789, row.getLong(0));
                assertEquals("Fresh canonical title", row.getString(1));
                assertEquals("Fresh canonical facts", row.getString(2));
                assertEquals(4444, row.getLong(3));
                assertEquals(100, row.getLong(4));
                assertEquals(1, row.getLong(5));
            }
            try (var row = db.rawQuery("SELECT Nova_pinned FROM MOVIE WHERE video_id=10", null)) {
                assertTrue(row.moveToFirst());
                assertTrue("Canonical save does not preserve a receiving user pin by itself",
                    row.isNull(0) || row.getLong(0) == 0);
            }
            // No FK maps membership to replaced metadata. Exact rows surviving does not prove membership meaning.
            try (
                var row = db.rawQuery(
                    "SELECT i.media_id,m._id FROM user_collection_item i LEFT JOIN MOVIE m ON m._id=i.media_id WHERE i._id=9",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(100, row.getLong(0));
                assertTrue(row.isNull(1));
            }
            try (var row =
                     db.rawQuery("SELECT video_id,m_online_id,Nova_pinned,name_movie FROM MOVIE WHERE _id=200", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(11, row.getLong(0));
                assertEquals(456, row.getLong(1));
                assertEquals(8, row.getLong(2));
                assertEquals("Other title", row.getString(3));
            }
        } finally {
            db.endTransaction();
        }
        try (var row = db.rawQuery(
                 "SELECT video_id,m_online_id,Nova_pinned,name_movie,plot_movie FROM MOVIE WHERE _id=100", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(10, row.getLong(0));
            assertEquals(123, row.getLong(1));
            assertEquals(9, row.getLong(2));
            assertEquals("Original title", row.getString(3));
            assertEquals("Original facts", row.getString(4));
        }
        try (var row = db.rawQuery("SELECT name,sort_name,position,hidden FROM user_collection WHERE _id=7", null)) {
            assertTrue(row.moveToFirst());
            assertEquals("Receiving legacy collection", row.getString(0));
            assertEquals("Receiving legacy collection", row.getString(1));
            assertEquals(2, row.getLong(2));
            assertEquals(0, row.getLong(3));
        }
        try (var row =
                 db.rawQuery("SELECT collection_id,media_type,media_id FROM user_collection_item WHERE _id=9", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(7, row.getLong(0));
            assertEquals(ScraperStore.UserCollectionItems.MEDIA_TYPE_MOVIE, row.getLong(1));
            assertEquals(100, row.getLong(2));
        }
    }

    @Test
    public void guardedCanonicalReplacementRetainsNativeOwnerPinAndCollectionReferenceAndRollsBackTogether() {
        var db = holder.get();
        for (boolean recursive : new boolean[] {
            false, true
        }) {
            db.execSQL("PRAGMA recursive_triggers=" + (recursive ? 1 : 0));
            db.beginTransaction();
            try {
                assertEquals(100, tags().savePreservingOwnedIdentity(app, 10, 100, 123, 9L));
                try (
                    var row = db.rawQuery(
                        "SELECT m._id,m.video_id,m.m_online_id,m.Nova_pinned,m.name_movie,m.plot_movie,f.bookmark,f.Archos_lastTimePlayed,f.Archos_traktSeen FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",
                        null)) {
                    assertTrue(row.moveToFirst());
                    assertEquals(100, row.getLong(0));
                    assertEquals(10, row.getLong(1));
                    assertEquals(789, row.getLong(2));
                    assertEquals(9, row.getLong(3));
                    assertEquals("Fresh canonical title", row.getString(4));
                    assertEquals("Fresh canonical facts", row.getString(5));
                    assertEquals(4444, row.getLong(6));
                    assertEquals(100, row.getLong(7));
                    assertEquals(1, row.getLong(8));
                }
                try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME, new String[] {
                    "m_actors", "m_genres"
                },
                         "_id=10", null, null, null, null)) {
                    assertTrue(row.moveToFirst());
                    assertTrue(row.getString(0).contains("Fresh actor"));
                    assertTrue(row.getString(1).contains("Comedy"));
                }
                try (
                    var row = db.rawQuery(
                        "SELECT i.media_id,m.video_id FROM user_collection_item i JOIN MOVIE m ON m._id=i.media_id WHERE i._id=9",
                        null)) {
                    assertTrue(row.moveToFirst());
                    assertEquals(100, row.getLong(0));
                    assertEquals(10, row.getLong(1));
                }
            } finally {
                db.endTransaction();
            }
            try (var row = db.rawQuery("SELECT m_online_id,Nova_pinned,name_movie FROM MOVIE WHERE _id=100", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(123, row.getLong(0));
                assertEquals(9, row.getLong(1));
                assertEquals("Original title", row.getString(2));
            }
        }
    }

    @Test
    public void guardedBatchRefusesChangedPinOrForeignMetadataOwnerBeforeReplacement() {
        var db = holder.get();
        db.beginTransaction();
        try {
            assertEquals(-1, tags().savePreservingOwnedIdentity(app, 10, 100, 123, 7L));
            assertEquals(-1, tags().savePreservingOwnedIdentity(app, 10, 200, 456, 8L));
            try (var row = db.rawQuery("SELECT m_online_id,Nova_pinned,name_movie FROM MOVIE WHERE _id=100", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(123, row.getLong(0));
                assertEquals(9, row.getLong(1));
                assertEquals("Original title", row.getString(2));
            }
            try (var row = db.rawQuery("SELECT m_online_id,Nova_pinned,video_id FROM MOVIE WHERE _id=200", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(456, row.getLong(0));
                assertEquals(8, row.getLong(1));
                assertEquals(11, row.getLong(2));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void ownedReplacementPreservesNullPinAndDoesNotFillUnselectedFileDuration() {
        var db = holder.get();
        db.execSQL("UPDATE MOVIE SET Nova_pinned=NULL WHERE _id=100");
        db.execSQL("UPDATE files SET duration=0 WHERE _id=10");
        MovieTags canonical = tags();
        canonical.setRuntime(5, TimeUnit.MINUTES);
        db.beginTransaction();
        try {
            assertEquals(100, canonical.savePreservingOwnedIdentity(app, 10, 100, 123, null));
            try (
                var row = db.rawQuery(
                    "SELECT m.Nova_pinned,f.duration,f.bookmark,f.Archos_lastTimePlayed FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",
                    null)) {
                assertTrue(row.moveToFirst());
                assertTrue(row.isNull(0));
                assertEquals(0, row.getLong(1));
                assertEquals(4444, row.getLong(2));
                assertEquals(100, row.getLong(3));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void changedFileKindRefusesReplacementAndDirectOwnedInsertRequiresTransaction() {
        var db = holder.get();
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=12 WHERE _id=10");
        db.beginTransaction();
        try {
            assertEquals(-1, tags().savePreservingOwnedIdentity(app, 10, 100, 123, 9L));
            try (var row = db.rawQuery("SELECT m_online_id,name_movie FROM MOVIE WHERE _id=100", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(123, row.getLong(0));
                assertEquals("Original title", row.getString(1));
            }
        } finally {
            db.endTransaction();
        }
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=11 WHERE _id=10");
        try {
            tags().savePreservingOwnedIdentity(app, 10, 100, 123, 9L);
            fail("Uncoordinated owned replacement accepted");
        } catch (IllegalStateException expected) {
        }
        try (var row = db.rawQuery("SELECT m_online_id,Nova_pinned FROM MOVIE WHERE _id=100", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(123, row.getLong(0));
            assertEquals(9, row.getLong(1));
        }
    }

    private void episodeFixture() {
        var db = holder.get();
        db.execSQL(
            "INSERT INTO files(_id,remote_id,media_type,_data,bookmark,duration,Archos_lastTimePlayed,Archos_traktSeen) VALUES(20,20,3,'file:///episode.mkv',6789,0,100,1)");
        db.execSQL("INSERT INTO SHOW(_id,s_online_id,name_show,Nova_pinned) VALUES(300,666,'Receiving parent',9)");
        db.execSQL(
            "INSERT INTO EPISODE(_id,video_id,e_online_id,show_episode,season_episode,number_episode,name_episode,plot_episode,cover_episode,picture_episode) VALUES(400,20,555,300,1,1,'Old episode','Old facts','old-cover','old-picture')");
        db.execSQL("INSERT INTO user_collection_item(_id,collection_id,media_type,media_id) VALUES(10,7,2,300)");
        db.execSQL(
            "INSERT INTO V_GUESTS(episode_v_guests,name_v_guests,role_v_guests) VALUES(400,'Old guest','Old role')");
    }

    private EpisodeTags episodeTags(long showProvider) {
        ShowTags show = new ShowTags();
        show.setOnlineId(showProvider);
        show.setTitle("Fetched parent");
        EpisodeTags episode = new EpisodeTags(show, 2, 3);
        episode.setOnlineId(777);
        episode.setTitle("Fresh episode");
        episode.setPlot("Fresh episode facts");
        episode.addActorIfAbsent("Fresh guest", "Fresh role");
        episode.setRuntime(5, TimeUnit.MINUTES);
        return episode;
    }

    @Test
    public void ownedEpisodeUpdatesCanonicalFactsWithoutDeletingItsLastParentOrCollectionAndRollsBack() {
        episodeFixture();
        var db = holder.get();
        for (boolean recursive : new boolean[] {
            false, true
        }) {
            db.execSQL("PRAGMA recursive_triggers=" + (recursive ? 1 : 0));
            db.beginTransaction();
            try {
                assertEquals(400, episodeTags(666).savePreservingOwnedIdentity(app, 20, 400, 555, 300, 666, 9L));
                try (
                    var row = db.rawQuery(
                        "SELECT e._id,e.e_online_id,e.name_episode,e.plot_episode,e.season_episode,e.number_episode,e.cover_episode,e.picture_episode,s._id,s.s_online_id,s.name_show,s.Nova_pinned,f.bookmark,f.duration,f.Archos_lastTimePlayed,f.Archos_traktSeen,f.ArchosMediaScraper_id FROM EPISODE e JOIN SHOW s ON s._id=e.show_episode JOIN files f ON f._id=e.video_id WHERE e._id=400",
                        null)) {
                    assertTrue(row.moveToFirst());
                    assertEquals(400, row.getLong(0));
                    assertEquals(777, row.getLong(1));
                    assertEquals("Fresh episode", row.getString(2));
                    assertEquals("Fresh episode facts", row.getString(3));
                    assertEquals(2, row.getLong(4));
                    assertEquals(3, row.getLong(5));
                    assertTrue(row.isNull(6));
                    assertTrue(row.isNull(7));
                    assertEquals(300, row.getLong(8));
                    assertEquals(666, row.getLong(9));
                    assertEquals("Receiving parent", row.getString(10));
                    assertEquals(9, row.getLong(11));
                    assertEquals(6789, row.getLong(12));
                    assertEquals(0, row.getLong(13));
                    assertEquals(100, row.getLong(14));
                    assertEquals(1, row.getLong(15));
                    assertEquals(400, row.getLong(16));
                }
                try (var row = db.rawQuery(
                         "SELECT name_v_guests,role_v_guests FROM V_GUESTS WHERE episode_v_guests=400", null)) {
                    assertTrue(row.moveToFirst());
                    assertEquals("Fresh guest", row.getString(0));
                    assertEquals("Fresh role", row.getString(1));
                    assertFalse(row.moveToNext());
                }
                try (
                    var row = db.rawQuery(
                        "SELECT s._id,s.Nova_pinned FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id WHERE i._id=10",
                        null)) {
                    assertTrue(row.moveToFirst());
                    assertEquals(300, row.getLong(0));
                    assertEquals(9, row.getLong(1));
                }
                try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
                    assertFalse(row.moveToFirst());
                }
            } finally {
                db.endTransaction();
            }
            try (var row =
                     db.rawQuery("SELECT e_online_id,name_episode,cover_episode FROM EPISODE WHERE _id=400", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(555, row.getLong(0));
                assertEquals("Old episode", row.getString(1));
                assertEquals("old-cover", row.getString(2));
            }
            try (var row = db.rawQuery("SELECT name_v_guests FROM V_GUESTS WHERE episode_v_guests=400", null)) {
                assertTrue(row.moveToFirst());
                assertEquals("Old guest", row.getString(0));
                assertFalse(row.moveToNext());
            }
        }
    }

    @Test
    public void ownedEpisodeRejectsChangedParentPinProviderAndUncoordinatedSave() {
        episodeFixture();
        var db = holder.get();
        try {
            episodeTags(999).savePreservingOwnedIdentity(app, 20, 400, 555, 300, 666, 9L);
            fail("Different parent accepted");
        } catch (IllegalArgumentException expected) {
        }
        db.beginTransaction();
        try {
            assertEquals(-1, episodeTags(666).savePreservingOwnedIdentity(app, 20, 400, 555, 300, 666, 7L));
            assertEquals(-1, episodeTags(666).savePreservingOwnedIdentity(app, 20, 400, 999, 300, 666, 9L));
        } finally {
            db.endTransaction();
        }
        try {
            episodeTags(666).savePreservingOwnedIdentity(app, 20, 400, 555, 300, 666, 9L);
            fail("Missing native transaction accepted");
        } catch (IllegalStateException expected) {
        }
        try (
            var row = db.rawQuery(
                "SELECT e.e_online_id,s.Nova_pinned FROM EPISODE e JOIN SHOW s ON s._id=e.show_episode WHERE e._id=400",
                null)) {
            assertTrue(row.moveToFirst());
            assertEquals(555, row.getLong(0));
            assertEquals(9, row.getLong(1));
        }
    }

    @Test
    public void verifiedMaterialPreparesImmutableOperationsWithoutPublishingOrWritingNativeState() throws Exception {
        var requested =
            new PortableMediaMatches.Match("tmdb:movie:789", null);
        var inventory = MediaMatchInventory.capture(app);
        MovieTags canonical = tags();
        var batch = CanonicalMatchMaterial.prepare(app,
            inventory.owners.get(10L), requested, Uri.parse("file:///one.mkv"), 0, 9L,
            (context, match, file)
                -> new ScrapeDetailResult(
                    canonical, true, null, ScrapeStatus.OKAY, null));
        canonical.setTitle("Later mutable provider object");
        try (var row = holder.get().rawQuery("SELECT m_online_id,name_movie FROM MOVIE WHERE _id=100", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(123, row.getLong(0));
            assertEquals("Original title", row.getString(1));
        }
        try {
            batch.operations.clear();
            fail("Mutable canonical operation list");
        } catch (UnsupportedOperationException expected) {
        }
        var db = holder.get();
        db.beginTransaction();
        try {
            app.getContentResolver().applyBatch(ScraperStore.AUTHORITY, new ArrayList<>(batch.operations));
            try (var row = db.rawQuery("SELECT m_online_id,name_movie,Nova_pinned FROM MOVIE WHERE _id=100", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(789, row.getLong(0));
                assertEquals("Fresh canonical title", row.getString(1));
                assertEquals(9, row.getLong(2));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void providerMaterialMustMatchExactRequestedMovieAndEpisodeIdentity() throws Exception {
        var movie = new PortableMediaMatches.Match("tmdb:movie:789", null);
        var episode = new PortableMediaMatches.Match("tmdb:tv:666:2:3", 777L);
        var okay = ScrapeStatus.OKAY;
        assertNotNull(CanonicalMatchMaterial.verify(
            movie, new ScrapeDetailResult(tags(), true, null, okay, null)));
        assertNotNull(CanonicalMatchMaterial.verify(
            episode, new ScrapeDetailResult(episodeTags(666), false, null, okay, null)));
        for (var wrong : new ScrapeDetailResult[] {
            null,
                 new ScrapeDetailResult(
                     tags(), true, null, ScrapeStatus.ERROR, null),
                 new ScrapeDetailResult(episodeTags(999), false, null, okay, null),
                 new ScrapeDetailResult(tags(), true, null, okay, null)
        }) {
            try {
                CanonicalMatchMaterial.verify(episode, wrong);
                fail("Wrong provider material accepted");
            } catch (IOException expected) {
            }
        }
        var changed = episodeTags(666);
        changed.setOnlineId(778);
        try {
            CanonicalMatchMaterial.verify(
                episode, new ScrapeDetailResult(changed, false, null, okay, null));
            fail("Wrong episode provider accepted");
        } catch (IOException expected) {
        }
    }

    @Test
    public void canonicalExecutorUsesCallerTransactionAndDoesNotPublishBeforeCommit() throws Exception {
        var db = holder.get();
        var batch = tags().prepareOwnedRestore(app, 10, 100, 123, 9L);
        int[] notifications = {
            0
        };
        ContentObserver observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean self, Uri uri) {
                notifications[0]++;
            }
        };
        app.getContentResolver().registerContentObserver(ScraperStore.ALL_CONTENT_URI, true, observer);
        try {
            try {
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
                fail("Missing transaction accepted");
            } catch (IOException expected) {
            }
            db.beginTransaction();
            try {
                assertEquals(100,
                    CanonicalMatchExecutor.apply(
                        app, db, batch, () -> false));
                assertEquals(0, notifications[0]);
                try (var row = db.rawQuery("SELECT m_online_id,Nova_pinned FROM MOVIE WHERE _id=100", null)) {
                    assertTrue(row.moveToFirst());
                    assertEquals(789, row.getLong(0));
                    assertEquals(9, row.getLong(1));
                }
            } finally {
                db.endTransaction();
            }
            try (var row = db.rawQuery("SELECT m_online_id FROM MOVIE WHERE _id=100", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(123, row.getLong(0));
            }
        } finally {
            app.getContentResolver().unregisterContentObserver(observer);
        }
    }

    @Test
    public void canonicalExecutorCancellationAfterMutationRollsBackParentAndEntireGraph() throws Exception {
        episodeFixture();
        var db = holder.get();
        var batch = episodeTags(666).prepareOwnedRestore(app, 20, 400, 555, 300, 666, 9L);
        int[] operations = {
            0
        };
        db.beginTransaction();
        try {
            try {
                CanonicalMatchExecutor.apply(
                    app, db, batch, () -> ++operations[0] > 4);
                fail("Cancellation ignored");
            } catch (InterruptedIOException expected) {
            }
        } finally {
            db.endTransaction();
        }
        try (
            var row = db.rawQuery(
                "SELECT e.e_online_id,e.name_episode,s.Nova_pinned FROM EPISODE e JOIN SHOW s ON s._id=e.show_episode WHERE e._id=400",
                null)) {
            assertTrue(row.moveToFirst());
            assertEquals(555, row.getLong(0));
            assertEquals("Old episode", row.getString(1));
            assertEquals(9, row.getLong(2));
        }
        try (var row = db.rawQuery("SELECT name_v_guests FROM V_GUESTS WHERE episode_v_guests=400", null)) {
            assertTrue(row.moveToFirst());
            assertEquals("Old guest", row.getString(0));
            assertFalse(row.moveToNext());
        }
    }

    @Test
    public void privateCanonicalRedoReplaysOnlyOriginalOrCommittedStateAndPreservesLaterReceivingChoices()
        throws Exception {
        var db = holder.get();
        var batch = tags().prepareOwnedRestore(app, 10, 100, 123, 9L);
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(10L), List.of(batch), () -> false);
            CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            CanonicalNativeRecovery.seal(db, record, () -> false);
        } finally {
            db.endTransaction();
        }
        record
        = new JSONObject(record.toString());
        assertFalse(record.toString().contains("file:///"));
        assertFalse(record.toString().contains("Original facts"));
        db.execSQL("UPDATE files SET bookmark=9999 WHERE _id=10");
        db.beginTransaction();
        try {
            assertFalse(CanonicalNativeRecovery.replay(
                app, db, record, false, () -> false));
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, record, true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
            assertFalse(CanonicalNativeRecovery.replay(
                app, db, record, true, () -> false));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        try (
            var row = db.rawQuery(
                "SELECT m.m_online_id,m.Nova_pinned,f.bookmark FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",
                null)) {
            assertTrue(row.moveToFirst());
            assertEquals(789, row.getLong(0));
            assertEquals(9, row.getLong(1));
            assertEquals(9999, row.getLong(2));
        }
        db.execSQL("UPDATE MOVIE SET name_movie='Later receiving edit' WHERE _id=200");
        db.beginTransaction();
        try {
            try {
                CanonicalNativeRecovery.replay(
                    app, db, record, true, () -> false);
                fail("Later receiving graph overwritten");
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("independently"));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void privateEpisodeRedoPreservesLastParentAndRefusesCorruptOrIncompatibleRecoveryPayload() throws Exception {
        episodeFixture();
        var db = holder.get();
        var batch = episodeTags(666).prepareOwnedRestore(app, 20, 400, 555, 300, 666, 9L);
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(20L), List.of(batch), () -> false);
            CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            CanonicalNativeRecovery.seal(db, record, () -> false);
        } finally {
            db.endTransaction();
        }
        for (String field : new String[] {
            "sha256", "sdk", "primary", "schema"
        }) {
            var corrupt = new JSONObject(record.getJSONArray("batches").getJSONObject(0).toString());
            corrupt.put(field,
                field.equals("sha256") ? "0".repeat(64)
                    : field.equals("sdk") ? Build.VERSION.SDK_INT + 1
                    : field.equals("primary") ? Long.MAX_VALUE
                                              : "1");
            try {
                OwnedCanonicalBatch.decodeRecovery(corrupt);
                fail("Corrupt private redo accepted: " + field);
            } catch (JSONException expected) {
            }
        }
        db.beginTransaction();
        try {
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, new JSONObject(record.toString()), true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
            try (
                var row = db.rawQuery(
                    "SELECT s._id,s.Nova_pinned FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id WHERE i._id=10",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(300, row.getLong(0));
                assertEquals(9, row.getLong(1));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void canonicalAndSelectedPinHiddenRecoveryShareOneAtomicNativeParticipant() throws Exception {
        var before = MediaStateRecovery.capture(app, true, true, true);
        var after = new JSONObject(before.toString());
        for (int i = 0; i < after.getJSONArray("pins").length(); i++) {
            var pin = after.getJSONArray("pins").getJSONObject(i);
            if (pin.getLong("id") == 100)
                pin.put("value", 0);
        }
        for (int i = 0; i < after.getJSONArray("hidden").length(); i++) {
            var hidden = after.getJSONArray("hidden").getJSONObject(i);
            if (hidden.getLong("id") == 11)
                hidden.put("value", true);
        }
        var batch = tags().prepareOwnedRestore(app, 10, 100, 123, 9L);
        JSONObject canonical;
        try (var session = MediaStateRecovery.begin(app, before, false)) {
            canonical = session.stageCanonical(List.of(10L), List.of(batch), () -> false);
            session.write(after, () -> false);
            session.sealCanonical(canonical, () -> false);
        }
        // A pre-decision death rolls back the entire SQLite transaction; recovery retains originals.
        MediaStateRecovery.recoverCanonical(
            app, before, after, canonical, false);
        try (var row = holder.get().rawQuery("SELECT m_online_id,Nova_pinned FROM MOVIE WHERE _id=100", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(123, row.getLong(0));
            assertEquals(9, row.getLong(1));
        }
        // Durable decision before native commit replays canonical metadata and selected native values together.
        MediaStateRecovery.recoverCanonical(
            app, before, after, new JSONObject(canonical.toString()), true);
        try (var row = holder.get().rawQuery("SELECT m_online_id,Nova_pinned FROM MOVIE WHERE _id=100", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(789, row.getLong(0));
            assertEquals(0, row.getLong(1));
        }
        try (var row = holder.get().rawQuery("SELECT Archos_hiddenByUser,bookmark FROM files WHERE _id=11", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(1, row.getLong(0));
            assertEquals(5555, row.getLong(1));
        }
        // A commit before acknowledgement is recognized, and a later unselected bookmark remains exact.
        holder.get().execSQL("UPDATE files SET bookmark=8888 WHERE _id=10");
        MediaStateRecovery.recoverCanonical(
            app, before, after, canonical, true);
        try (var row = holder.get().rawQuery("SELECT bookmark FROM files WHERE _id=10", null)) {
            assertTrue(row.moveToFirst());
            assertEquals(8888, row.getLong(0));
        }
    }

    @Test
    public void verifiedEpisodeReparentingRetainsBothShowPinsAndOldCollectionAcrossRedo() throws Exception {
        episodeFixture();
        var db = holder.get();
        db.execSQL(
            "INSERT INTO SHOW(_id,s_online_id,name_show,Nova_pinned) VALUES(350,999,'Receiving target parent',8)");
        var inventory = MediaMatchInventory.capture(app);
        var request = new PortableMediaMatches.Match("tmdb:tv:999:2:3", 777L);
        var batch = CanonicalMatchMaterial.prepare(app,
            inventory.owners.get(20L), request, Uri.parse("file:///episode.mkv"), 300, 9L,
            (context, requested, file)
                -> new ScrapeDetailResult(
                    episodeTags(999), false, null, ScrapeStatus.OKAY, null));
        assertEquals(4, batch.primaryOperation);
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(20L), List.of(batch), () -> false);
            CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            CanonicalNativeRecovery.seal(db, record, () -> false);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "s_id", "s_online_id", "e_online_id", "e_name", "Nova_pinned"
                     }, "_id=20", null, null,
                     null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(350, row.getLong(0));
                assertEquals(999, row.getLong(1));
                assertEquals(777, row.getLong(2));
                assertEquals("Fresh episode", row.getString(3));
                assertEquals(8, row.getLong(4));
            }
            try (
                var row = db.rawQuery(
                    "SELECT s.Nova_pinned,s.s_online_id FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id WHERE i._id=10",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(9, row.getLong(0));
                assertEquals(666, row.getLong(1));
            }
        } finally {
            db.endTransaction();
        }
        db.beginTransaction();
        try {
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, new JSONObject(record.toString()), true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
            try (var row = db.rawQuery("SELECT s_online_id,Nova_pinned FROM SHOW WHERE _id=300", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(666, row.getLong(0));
                assertEquals(9, row.getLong(1));
            }
            try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
                assertFalse(row.moveToFirst());
            }
        } finally {
            db.endTransaction();
        }
        db.execSQL("UPDATE SHOW SET Nova_pinned=7 WHERE _id=350");
        db.beginTransaction();
        try {
            try {
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
                fail("Stale receiving target pin accepted");
            } catch (IOException expected) {
            }
            try (var row = db.rawQuery("SELECT show_episode,e_online_id FROM EPISODE WHERE _id=400", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(300, row.getLong(0));
                assertEquals(555, row.getLong(1));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void clearingIdentificationHidesEveryJoinedFactWithoutDeletingUnselectedPinsCollectionsOrViewingState()
        throws Exception {
        episodeFixture();
        var db = holder.get();
        var movie = MatchRestoreOperations.prepareClear(10, 11, 100, 123L);
        var episode = MatchRestoreOperations.prepareClear(20, 12, 400, 555L);
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(10L, 20L), List.of(movie, episode), () -> false);
            CanonicalMatchExecutor.apply(app, db, movie, () -> false);
            CanonicalMatchExecutor.apply(app, db, episode, () -> false);
            CanonicalNativeRecovery.seal(db, record, () -> false);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "ArchosMediaScraper_type", "m_online_id", "m_name", "m_plot", "s_id", "s_name",
                         "e_name", "e_plot", "Nova_pinned"
                     },
                     "_id IN(10,20)", null, null, null, "_id")) {
                assertEquals(2, row.getCount());
                while (row.moveToNext()) {
                    assertEquals(0, row.getLong(0));
                    for (int i = 1; i < row.getColumnCount(); i++)
                        assertTrue("Cleared joined field " + row.getColumnName(i), row.isNull(i));
                }
            }
            try (
                var row = db.rawQuery(
                    "SELECT m.m_online_id,m.name_movie,m.Nova_pinned,f.ArchosMediaScraper_id,f.bookmark FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(123, row.getLong(0));
                assertEquals("Original title", row.getString(1));
                assertEquals(9, row.getLong(2));
                assertEquals(100, row.getLong(3));
                assertEquals(4444, row.getLong(4));
            }
            try (
                var row = db.rawQuery(
                    "SELECT s.s_online_id,s.Nova_pinned,e.e_online_id FROM SHOW s JOIN EPISODE e ON e.show_episode=s._id WHERE e._id=400",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(666, row.getLong(0));
                assertEquals(9, row.getLong(1));
                assertEquals(555, row.getLong(2));
            }
            try (
                var row = db.rawQuery(
                    "SELECT COUNT(*) FROM user_collection_item i LEFT JOIN MOVIE m ON i.media_type=1 AND m._id=i.media_id LEFT JOIN SHOW s ON i.media_type=2 AND s._id=i.media_id WHERE m._id IS NOT NULL OR s._id IS NOT NULL",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(2, row.getLong(0));
            }
        } finally {
            db.endTransaction();
        }
        db.beginTransaction();
        try {
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, new JSONObject(record.toString()), true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
            try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
                assertFalse(row.moveToFirst());
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void schema62UpgradeChangesOnlyTheJoinedViewAndPreservesSignedBaselineData() throws Exception {
        var db = holder.get();
        String historical =
            ReflectionHelpers.getStaticField(VideoOpenHelper.class, "CREATE_VIDEO_VIEW_V60");
        db.execSQL("DROP VIEW video");
        db.execSQL(historical);
        db.setVersion(62);
        holder.close();
        db = holder.get();
        assertEquals(63, db.getVersion());
        try (
            var row = db.rawQuery(
                "SELECT m.m_online_id,m.name_movie,m.plot_movie,m.Nova_pinned,f.bookmark,f.Archos_lastTimePlayed,f.Archos_traktSeen FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",
                null)) {
            assertTrue(row.moveToFirst());
            assertEquals(123, row.getLong(0));
            assertEquals("Original title", row.getString(1));
            assertEquals("Original facts", row.getString(2));
            assertEquals(9, row.getLong(3));
            assertEquals(4444, row.getLong(4));
            assertEquals(100, row.getLong(5));
            assertEquals(1, row.getLong(6));
        }
        try (
            var row = db.rawQuery(
                "SELECT i.media_id,m.video_id FROM user_collection_item i JOIN MOVIE m ON m._id=i.media_id WHERE i._id=9",
                null)) {
            assertTrue(row.moveToFirst());
            assertEquals(100, row.getLong(0));
            assertEquals(10, row.getLong(1));
        }
        try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
            assertFalse(row.moveToFirst());
        }
    }

    @Test
    public void clearedMovieCanBeCanonicallyRestoredUsingRetainedOwnershipAndPin() throws Exception {
        var db = holder.get();
        db.beginTransaction();
        try {
            CanonicalMatchExecutor.apply(
                app, db, MatchRestoreOperations.prepareClear(10, 11, 100, 123L), () -> false);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        var owner = MediaMatchInventory.capture(app).owners.get(10L);
        assertNull(owner.match);
        var request = new PortableMediaMatches.Match("tmdb:movie:789", null);
        var batch = CanonicalMatchMaterial.prepare(app, owner, request,
            Uri.parse("file:///one.mkv"), 0, null,
            (context, requested, file)
                -> new ScrapeDetailResult(
                    tags(), true, null, ScrapeStatus.OKAY, null));
        db.beginTransaction();
        try {
            assertEquals(100,
                CanonicalMatchExecutor.apply(app, db, batch, () -> false));
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "ArchosMediaScraper_type", "m_online_id", "m_name", "Nova_pinned"
                     }, "_id=10", null,
                     null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(11, row.getLong(0));
                assertEquals(789, row.getLong(1));
                assertEquals("Fresh canonical title", row.getString(2));
                assertEquals(9, row.getLong(3));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void episodeToRetainedMovieSwitchKeepsOldEpisodeShowPinCollectionAndViewingState() throws Exception {
        episodeFixture();
        var db = holder.get();
        // Seed a retained owner without first deleting the episode through native link cleanup.
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=0 WHERE _id=20");
        db.execSQL(
            "INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie,Nova_pinned) VALUES(450,20,888,'Retained receiving movie',7)");
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=0 WHERE _id=20");
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=12,ArchosMediaScraper_id=400 WHERE _id=20");
        var owner = MediaMatchInventory.capture(app).owners.get(20L);
        assertEquals(12, owner.type);
        var request = new PortableMediaMatches.Match("tmdb:movie:789", null);
        var batch = CanonicalMatchMaterial.prepare(app, owner, request,
            Uri.parse("file:///episode.mkv"), 0, null,
            (context, requested, file)
                -> new ScrapeDetailResult(
                    tags(), true, null, ScrapeStatus.OKAY, null));
        db.beginTransaction();
        try {
            CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "ArchosMediaScraper_type", "m_online_id", "m_name", "e_name", "s_id", "Nova_pinned",
                         "bookmark"
                     },
                     "_id=20", null, null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(11, row.getLong(0));
                assertEquals(789, row.getLong(1));
                assertEquals("Fresh canonical title", row.getString(2));
                assertTrue(row.isNull(3));
                assertTrue(row.isNull(4));
                assertEquals(7, row.getLong(5));
                assertEquals(6789, row.getLong(6));
            }
            try (
                var row = db.rawQuery(
                    "SELECT s.s_online_id,s.Nova_pinned,e.e_online_id FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id JOIN EPISODE e ON e.show_episode=s._id WHERE i._id=10",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(666, row.getLong(0));
                assertEquals(9, row.getLong(1));
                assertEquals(555, row.getLong(2));
            }
            try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
                assertFalse(row.moveToFirst());
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void receivingEpisodeChangeWithoutPointerChangeIsRefusedAtTheNativeMutationBoundary() throws Exception {
        episodeFixture();
        var db = holder.get();
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=0 WHERE _id=20");
        db.execSQL(
            "INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie,Nova_pinned) VALUES(450,20,888,'Retained receiving movie',7)");
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=0 WHERE _id=20");
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=12,ArchosMediaScraper_id=400 WHERE _id=20");
        var owner = MediaMatchInventory.capture(app).owners.get(20L);
        var request = new PortableMediaMatches.Match("tmdb:movie:789", null);
        var batch = CanonicalMatchMaterial.prepare(app, owner, request,
            Uri.parse("file:///episode.mkv"), 0, null,
            (context, requested, file)
                -> new ScrapeDetailResult(
                    tags(), true, null, ScrapeStatus.OKAY, null));
        db.beginTransaction();
        try {
            db.execSQL("UPDATE EPISODE SET season_episode=5 WHERE _id=400");
            try {
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
                fail("Changed receiving identification overwritten");
            } catch (IOException expected) {
            }
            try (
                var row = db.rawQuery(
                    "SELECT f.ArchosMediaScraper_type,f.ArchosMediaScraper_id,e.season_episode,m.m_online_id FROM files f JOIN EPISODE e ON e.video_id=f._id JOIN MOVIE m ON m.video_id=f._id WHERE f._id=20",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(12, row.getLong(0));
                assertEquals(400, row.getLong(1));
                assertEquals(5, row.getLong(2));
                assertEquals(888, row.getLong(3));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void
    verifiedNewMovieMetadataAttachesOnlyToItsIndexedFileAndReplaysWithoutUnselectedDurationOrHistoryChanges()
        throws Exception {
        var db = holder.get();
        db.execSQL(
            "INSERT INTO files(_id,remote_id,media_type,_data,duration,bookmark,Archos_lastTimePlayed) VALUES(20,20,3,'file:///unmatched.mkv',0,8888,1234)");
        var owner = MediaMatchInventory.capture(app).owners.get(20L);
        assertNull(owner.match);
        var request = new PortableMediaMatches.Match("tmdb:movie:789", null);
        MovieTags material = tags();
        material.setRuntime(5, TimeUnit.MINUTES);
        var batch = CanonicalMatchMaterial.prepare(app, owner, request,
            Uri.parse("file:///unmatched.mkv"), 0, null,
            (context, requested, file)
                -> new ScrapeDetailResult(
                    material, true, null, ScrapeStatus.OKAY, null));
        assertEquals(3, batch.primaryOperation);
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(20L), List.of(batch), () -> false);
            assertEquals(201,
                CanonicalMatchExecutor.apply(app, db, batch, () -> false));
            CanonicalNativeRecovery.seal(db, record, () -> false);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "m_id", "m_online_id", "m_name", "m_plot", "Nova_pinned", "duration", "bookmark",
                         "Archos_lastTimePlayed"
                     },
                     "_id=20", null, null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(201, row.getLong(0));
                assertEquals(789, row.getLong(1));
                assertEquals("Fresh canonical title", row.getString(2));
                assertEquals("Fresh canonical facts", row.getString(3));
                assertTrue(row.isNull(4));
                assertEquals(0, row.getLong(5));
                assertEquals(8888, row.getLong(6));
                assertEquals(1234, row.getLong(7));
            }
        } finally {
            db.endTransaction();
        }
        db.beginTransaction();
        try {
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, new JSONObject(record.toString()), true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
            try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
                assertFalse(row.moveToFirst());
            }
        } finally {
            db.endTransaction();
        }
        // A new receiving metadata owner in the reserved slot must not be replaced.
        db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data) VALUES(30,30,3,'file:///other-new.mkv')");
        db.execSQL(
            "INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie,Nova_pinned) VALUES(201,30,999,'Other receiving movie',7)");
        db.beginTransaction();
        try {
            try {
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
                fail("Occupied reserved identity replaced");
            } catch (IOException expected) {
            }
            try (var row = db.rawQuery("SELECT m_online_id,Nova_pinned,video_id FROM MOVIE WHERE _id=201", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(999, row.getLong(0));
                assertEquals(7, row.getLong(1));
                assertEquals(30, row.getLong(2));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void episodeToNewMovieMetadataKeepsOriginalEpisodeAndItsLastParentPinnedCollectionOwner() throws Exception {
        episodeFixture();
        var db = holder.get();
        var owner = MediaMatchInventory.capture(app).owners.get(20L);
        var request = new PortableMediaMatches.Match("tmdb:movie:789", null);
        var batch = CanonicalMatchMaterial.prepare(app, owner, request,
            Uri.parse("file:///episode.mkv"), 0, null,
            (context, requested, file)
                -> new ScrapeDetailResult(
                    tags(), true, null, ScrapeStatus.OKAY, null));
        db.beginTransaction();
        try {
            assertEquals(201,
                CanonicalMatchExecutor.apply(app, db, batch, () -> false));
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "ArchosMediaScraper_type", "m_online_id", "e_name", "s_id", "bookmark"
                     }, "_id=20",
                     null, null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(11, row.getLong(0));
                assertEquals(789, row.getLong(1));
                assertTrue(row.isNull(2));
                assertTrue(row.isNull(3));
                assertEquals(6789, row.getLong(4));
            }
            try (
                var row = db.rawQuery(
                    "SELECT s.s_online_id,s.Nova_pinned,e.e_online_id FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id JOIN EPISODE e ON e.show_episode=s._id WHERE i._id=10",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(666, row.getLong(0));
                assertEquals(9, row.getLong(1));
                assertEquals(555, row.getLong(2));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void movieToNewEpisodeKeepsMoviePinCollectionAndFileStateWithAnExistingCanonicalParent() throws Exception {
        var db = holder.get();
        db.execSQL("INSERT INTO SHOW(_id,s_online_id,name_show,Nova_pinned) VALUES(300,666,'Receiving series',8)");
        var owner = MediaMatchInventory.capture(app).owners.get(10L);
        var request = new PortableMediaMatches.Match("tmdb:tv:666:2:3", 777L);
        var batches = new CanonicalMatchMaterial.Preparation(app).prepare(
            owner, request, Uri.parse("file:///one.mkv"),
            (context, requested, file)
                -> new ScrapeDetailResult(
                    episodeTags(666), false, null, ScrapeStatus.OKAY, null));
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(10L), batches, () -> false);
            for (var batch : batches)
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            CanonicalNativeRecovery.seal(db, record, () -> false);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "ArchosMediaScraper_type", "m_name", "e_online_id", "e_name", "s_online_id",
                         "Nova_pinned", "bookmark", "Archos_lastTimePlayed"
                     },
                     "_id=10", null, null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(12, row.getLong(0));
                assertTrue(row.isNull(1));
                assertEquals(777, row.getLong(2));
                assertEquals("Fresh episode", row.getString(3));
                assertEquals(666, row.getLong(4));
                assertEquals(8, row.getLong(5));
                assertEquals(4444, row.getLong(6));
                assertEquals(100, row.getLong(7));
            }
            try (
                var row = db.rawQuery(
                    "SELECT m.m_online_id,m.Nova_pinned FROM user_collection_item i JOIN MOVIE m ON m._id=i.media_id WHERE i._id=9",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(123, row.getLong(0));
                assertEquals(9, row.getLong(1));
            }
        } finally {
            db.endTransaction();
        }
        db.beginTransaction();
        try {
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, new JSONObject(record.toString()), true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void retainedEpisodeReidentificationClearsStaleAiredDateAndSupportsUnknownRawEpisodeProvider()
        throws Exception {
        episodeFixture();
        var db = holder.get();
        db.execSQL("UPDATE EPISODE SET e_online_id=0,aired_episode=123456 WHERE _id=400");
        var identified = MediaMatchInventory.capture(app).owners.get(20L);
        assertNull(identified.match.episodeProvider);
        assertEquals(Long.valueOf(0), identified.episodeProviderRaw);
        db.beginTransaction();
        try {
            CanonicalMatchExecutor.apply(app, db,
                CanonicalMatchMaterial.guard(
                    MatchRestoreOperations.prepareClear(
                        20, 12, 400, identified.episodeProviderRaw),
                    identified),
                () -> false);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        var owner = MediaMatchInventory.capture(app).owners.get(20L);
        var request = new PortableMediaMatches.Match("tmdb:tv:666:2:3", 777L);
        var batches = new CanonicalMatchMaterial.Preparation(app).prepare(
            owner, request, Uri.parse("file:///episode.mkv"),
            (context, requested, file)
                -> new ScrapeDetailResult(
                    episodeTags(666), false, null, ScrapeStatus.OKAY, null));
        db.beginTransaction();
        try {
            for (var batch : batches)
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "ArchosMediaScraper_type", "e_online_id", "e_aired", "e_name", "s_online_id", "Nova_pinned"
                     },
                     "_id=20", null, null, null, null)) {
                assertTrue(row.moveToFirst());
                assertEquals(12, row.getLong(0));
                assertEquals(777, row.getLong(1));
                assertTrue(row.isNull(2));
                assertEquals("Fresh episode", row.getString(3));
                assertEquals(666, row.getLong(4));
                assertEquals(9, row.getLong(5));
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void preparationReservesDistinctNewMovieIdsAcrossMultiplePhysicalTargets() throws Exception {
        var db = holder.get();
        db.execSQL(
            "INSERT INTO files(_id,remote_id,media_type,_data) VALUES(20,20,3,'file:///new-one.mkv'),(21,21,3,'file:///new-two.mkv')");
        var inventory = MediaMatchInventory.capture(app);
        var request = new PortableMediaMatches.Match("tmdb:movie:789", null);
        var preparation = new CanonicalMatchMaterial.Preparation(app);
        List<OwnedCanonicalBatch> batches = new ArrayList<>();
        for (long id : new long[] {
            20, 21
        })
            batches.addAll(preparation.prepare(inventory.owners.get(id), request,
                Uri.parse(id == 20 ? "file:///new-one.mkv" : "file:///new-two.mkv"),
                (context, requested, file)
                    -> new ScrapeDetailResult(
                        tags(), true, null, ScrapeStatus.OKAY, null)));
        db.beginTransaction();
        try {
            for (var batch : batches)
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            try (var row = db.rawQuery(
                     "SELECT _id,video_id,m_online_id FROM MOVIE WHERE video_id IN(20,21) ORDER BY video_id", null)) {
                assertEquals(2, row.getCount());
                assertTrue(row.moveToFirst());
                assertEquals(201, row.getLong(0));
                assertEquals(20, row.getLong(1));
                assertEquals(789, row.getLong(2));
                assertTrue(row.moveToNext());
                assertEquals(202, row.getLong(0));
                assertEquals(21, row.getLong(1));
                assertEquals(789, row.getLong(2));
            }
        } finally {
            db.endTransaction();
        }
    }

    private EpisodeTags newParentEpisode(int number) {
        ShowTags show = new ShowTags();
        show.setOnlineId(999);
        show.setTitle("Fresh canonical parent");
        show.setPlot("Fresh canonical parent facts");
        show.addActorIfAbsent("Parent actor", "Parent role");
        show.addGenreIfAbsent("Drama");
        EpisodeTags episode = new EpisodeTags(show, 2, number);
        episode.setOnlineId(770 + number);
        episode.setTitle("Fresh episode " + number);
        episode.setPlot("Fresh episode facts " + number);
        episode.addActorIfAbsent("Episode guest", "Guest role");
        return episode;
    }

    @Test
    public void newCanonicalParentAndMultipleEpisodesShareOneReservedParentAndReproduceExactRedo() throws Exception {
        var db = holder.get();
        db.execSQL(
            "INSERT INTO files(_id,remote_id,media_type,_data,bookmark) VALUES(20,20,3,'file:///new-episode-one.mkv',4321),(21,21,3,'file:///new-episode-two.mkv',5432)");
        var inventory = MediaMatchInventory.capture(app);
        var preparation = new CanonicalMatchMaterial.Preparation(app);
        List<OwnedCanonicalBatch> batches = new ArrayList<>();
        for (long id : new long[] {
            20, 21
        }) {
            int number = (int) (id - 17);
            var request = new PortableMediaMatches.Match(
                "tmdb:tv:999:2:" + number, 770L + number);
            batches.addAll(preparation.prepare(inventory.owners.get(id), request,
                Uri.parse(id == 20 ? "file:///new-episode-one.mkv" : "file:///new-episode-two.mkv"),
                (context, requested, file)
                    -> new ScrapeDetailResult(
                        newParentEpisode(number), false, null, ScrapeStatus.OKAY, null)));
        }
        assertEquals(3, batches.size());
        JSONObject record;
        db.beginTransaction();
        try {
            record
            = CanonicalNativeRecovery.begin(
                db, List.of(20L, 21L), batches, () -> false);
            for (var batch : batches)
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            CanonicalNativeRecovery.seal(db, record, () -> false);
            try (var row = db.rawQuery(
                     "SELECT _id,s_online_id,name_show,plot_show,s_actors,s_genres,Nova_pinned FROM SHOW", null)) {
                assertEquals(1, row.getCount());
                assertTrue(row.moveToFirst());
                assertEquals(1, row.getLong(0));
                assertEquals(999, row.getLong(1));
                assertEquals("Fresh canonical parent", row.getString(2));
                assertEquals("Fresh canonical parent facts", row.getString(3));
                assertTrue(row.getString(4).contains("Parent actor"));
                assertTrue(row.getString(5).contains("Drama"));
                assertTrue(row.isNull(6));
            }
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "_id", "e_online_id", "e_name", "s_online_id", "s_name", "bookmark"
                     },
                     "_id IN(20,21)", null, null, null, "_id")) {
                assertEquals(2, row.getCount());
                assertTrue(row.moveToFirst());
                assertEquals(773, row.getLong(1));
                assertEquals("Fresh episode 3", row.getString(2));
                assertEquals(999, row.getLong(3));
                assertEquals("Fresh canonical parent", row.getString(4));
                assertEquals(4321, row.getLong(5));
                assertTrue(row.moveToNext());
                assertEquals(774, row.getLong(1));
                assertEquals("Fresh episode 4", row.getString(2));
                assertEquals(5432, row.getLong(5));
            }
            try (var row =
                     db.rawQuery("SELECT COUNT(*) FROM V_PLAYS_SHOW WHERE name_v_plays_show='Parent actor'", null)) {
                assertTrue(row.moveToFirst());
                assertEquals(1, row.getLong(0));
            }
        } finally {
            db.endTransaction();
        }
        db.beginTransaction();
        try {
            assertTrue(CanonicalNativeRecovery.replay(
                app, db, new JSONObject(record.toString()), true, () -> false));
            CanonicalNativeRecovery.verifyComplete(db, record, () -> false);
            try (var row = db.rawQuery("PRAGMA foreign_key_check", null)) {
                assertFalse(row.moveToFirst());
            }
        } finally {
            db.endTransaction();
        }
    }

    @Test
    public void movingEpisodeToNewCanonicalParentRetainsOldShowPinAndCollection() throws Exception {
        episodeFixture();
        var db = holder.get();
        var owner = MediaMatchInventory.capture(app).owners.get(20L);
        var request = new PortableMediaMatches.Match("tmdb:tv:999:2:3", 773L);
        var batches = new CanonicalMatchMaterial.Preparation(app).prepare(
            owner, request, Uri.parse("file:///episode.mkv"),
            (context, requested, file)
                -> new ScrapeDetailResult(
                    newParentEpisode(3), false, null, ScrapeStatus.OKAY, null));
        assertEquals(2, batches.size());
        db.beginTransaction();
        try {
            for (var batch : batches)
                CanonicalMatchExecutor.apply(app, db, batch, () -> false);
            try (
                var row = db.rawQuery(
                    "SELECT s.s_online_id,s.Nova_pinned FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id WHERE i._id=10",
                    null)) {
                assertTrue(row.moveToFirst());
                assertEquals(666, row.getLong(0));
                assertEquals(9, row.getLong(1));
            }
            try (var row = db.query(VideoOpenHelper.VIDEO_VIEW_NAME,
                     new String[] {
                         "s_online_id", "e_online_id", "e_name", "bookmark"
                     }, "_id=20", null, null, null,
                     null)) {
                assertTrue(row.moveToFirst());
                assertEquals(999, row.getLong(0));
                assertEquals(773, row.getLong(1));
                assertEquals("Fresh episode 3", row.getString(2));
                assertEquals(6789, row.getLong(3));
            }
        } finally {
            db.endTransaction();
        }
    }
}
