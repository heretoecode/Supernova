package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import com.archos.mediascraper.MovieTags;
import com.archos.mediascraper.EpisodeTags;
import com.archos.mediascraper.ShowTags;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowContentResolver;
import static org.junit.Assert.*;

/** Existing canonical tags/provider behavior, not a new match-restoration implementation. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class CanonicalMatchProviderTest {
    private Application app;private DbHolder holder;
    @Before public void setup(){
        app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");holder=new DbHolder(new VideoOpenHelper(app));
        ScraperProvider.hookUriMatcher(new UriMatcher(UriMatcher.NO_MATCH));ScraperProvider scraper=new ScraperProvider(app,holder);
        ContentProvider provider=new ContentProvider(){
            public boolean onCreate(){return true;}public String getType(Uri uri){return null;}
            public Cursor query(Uri uri,String[] columns,String where,String[] args,String order){return uri.getAuthority().equals(ScraperStore.AUTHORITY)?scraper.query(uri,columns,where,args,order):holder.get().query(VideoOpenHelper.VIDEO_VIEW_NAME,columns,itemSelection(uri,where),args,null,null,order);}
            public Uri insert(Uri uri,ContentValues values){return scraper.insert(uri,values);}
            public int update(Uri uri,ContentValues values,String where,String[] args){return uri.getAuthority().equals(ScraperStore.AUTHORITY)?scraper.update(uri,values,where,args):holder.get().update("files",values,where,args);}
            public int delete(Uri uri,String where,String[] args){return scraper.delete(uri,where,args);}
        };
        provider.attachInfo(app,null);ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY,provider);ShadowContentResolver.registerProviderInternal(ScraperStore.AUTHORITY,provider);
        var db=holder.get();db.setForeignKeyConstraintsEnabled(true);
        db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,ArchosMediaScraper_type,ArchosMediaScraper_id,bookmark,Archos_lastTimePlayed,Archos_traktSeen) VALUES(10,10,3,'file:///one.mkv',11,100,4444,100,1),(11,11,3,'file:///other.mkv',11,200,5555,200,1)");
        db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie,plot_movie) VALUES(100,10,123,9,'Original title','Original facts'),(200,11,456,8,'Other title','Other facts')");
        db.execSQL("INSERT INTO user_collection(_id,name,sort_name,position,hidden) VALUES(7,'Receiving legacy collection','Receiving legacy collection',2,0)");
        db.execSQL("INSERT INTO user_collection_item(_id,collection_id,media_type,media_id) VALUES(9,7,1,100)");
    }
    @After public void close(){holder.close();}
    private static String itemSelection(Uri uri,String where){String last=uri.getLastPathSegment();if(last!=null&&last.matches("[0-9]+"))return "_id="+Long.parseLong(last)+(where==null?"":" AND ("+where+")");return where;}
    private MovieTags tags(){MovieTags tags=new MovieTags();tags.setOnlineId(789);tags.setTitle("Fresh canonical title");tags.setPlot("Fresh canonical facts");tags.setTitleLanguage("en");tags.addActorIfAbsent("Fresh actor","Role");tags.addGenreIfAbsent("Comedy");return tags;}
    @Test public void canonicalProviderChangesJoinedFactsAndOuterRollbackRestoresOriginalPinAndReceivingCollections(){
        var db=holder.get();db.beginTransaction();try{
            long id=tags().save(app,10);assertTrue(id>0);assertNotEquals(100,id);
            try(var row=db.query(VideoOpenHelper.VIDEO_VIEW_NAME,new String[]{"m_online_id","m_name","m_plot","bookmark","Archos_lastTimePlayed","Archos_traktSeen"},"_id=10",null,null,null,null)){assertTrue(row.moveToFirst());assertEquals(789,row.getLong(0));assertEquals("Fresh canonical title",row.getString(1));assertEquals("Fresh canonical facts",row.getString(2));assertEquals(4444,row.getLong(3));assertEquals(100,row.getLong(4));assertEquals(1,row.getLong(5));}
            try(var row=db.rawQuery("SELECT Nova_pinned FROM MOVIE WHERE video_id=10",null)){assertTrue(row.moveToFirst());assertTrue("Canonical save does not preserve a receiving user pin by itself",row.isNull(0)||row.getLong(0)==0);}
            // No FK maps membership to replaced metadata. Exact rows surviving does not prove membership meaning.
            try(var row=db.rawQuery("SELECT i.media_id,m._id FROM user_collection_item i LEFT JOIN MOVIE m ON m._id=i.media_id WHERE i._id=9",null)){assertTrue(row.moveToFirst());assertEquals(100,row.getLong(0));assertTrue(row.isNull(1));}
            try(var row=db.rawQuery("SELECT video_id,m_online_id,Nova_pinned,name_movie FROM MOVIE WHERE _id=200",null)){assertTrue(row.moveToFirst());assertEquals(11,row.getLong(0));assertEquals(456,row.getLong(1));assertEquals(8,row.getLong(2));assertEquals("Other title",row.getString(3));}
        }finally{db.endTransaction();}
        try(var row=db.rawQuery("SELECT video_id,m_online_id,Nova_pinned,name_movie,plot_movie FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(10,row.getLong(0));assertEquals(123,row.getLong(1));assertEquals(9,row.getLong(2));assertEquals("Original title",row.getString(3));assertEquals("Original facts",row.getString(4));}
        try(var row=db.rawQuery("SELECT name,sort_name,position,hidden FROM user_collection WHERE _id=7",null)){assertTrue(row.moveToFirst());assertEquals("Receiving legacy collection",row.getString(0));assertEquals("Receiving legacy collection",row.getString(1));assertEquals(2,row.getLong(2));assertEquals(0,row.getLong(3));}
        try(var row=db.rawQuery("SELECT collection_id,media_type,media_id FROM user_collection_item WHERE _id=9",null)){assertTrue(row.moveToFirst());assertEquals(7,row.getLong(0));assertEquals(ScraperStore.UserCollectionItems.MEDIA_TYPE_MOVIE,row.getLong(1));assertEquals(100,row.getLong(2));}
    }
    @Test public void guardedCanonicalReplacementRetainsNativeOwnerPinAndCollectionReferenceAndRollsBackTogether(){
        var db=holder.get();for(boolean recursive:new boolean[]{false,true}){db.execSQL("PRAGMA recursive_triggers="+(recursive?1:0));db.beginTransaction();try{
            assertEquals(100,tags().savePreservingOwnedIdentity(app,10,100,123,9L));
            try(var row=db.rawQuery("SELECT m._id,m.video_id,m.m_online_id,m.Nova_pinned,m.name_movie,m.plot_movie,f.bookmark,f.Archos_lastTimePlayed,f.Archos_traktSeen FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",null)){assertTrue(row.moveToFirst());assertEquals(100,row.getLong(0));assertEquals(10,row.getLong(1));assertEquals(789,row.getLong(2));assertEquals(9,row.getLong(3));assertEquals("Fresh canonical title",row.getString(4));assertEquals("Fresh canonical facts",row.getString(5));assertEquals(4444,row.getLong(6));assertEquals(100,row.getLong(7));assertEquals(1,row.getLong(8));}
            try(var row=db.query(VideoOpenHelper.VIDEO_VIEW_NAME,new String[]{"m_actors","m_genres"},"_id=10",null,null,null,null)){assertTrue(row.moveToFirst());assertTrue(row.getString(0).contains("Fresh actor"));assertTrue(row.getString(1).contains("Comedy"));}
            try(var row=db.rawQuery("SELECT i.media_id,m.video_id FROM user_collection_item i JOIN MOVIE m ON m._id=i.media_id WHERE i._id=9",null)){assertTrue(row.moveToFirst());assertEquals(100,row.getLong(0));assertEquals(10,row.getLong(1));}
        }finally{db.endTransaction();}try(var row=db.rawQuery("SELECT m_online_id,Nova_pinned,name_movie FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(123,row.getLong(0));assertEquals(9,row.getLong(1));assertEquals("Original title",row.getString(2));}}
    }
    @Test public void guardedBatchRefusesChangedPinOrForeignMetadataOwnerBeforeReplacement(){
        var db=holder.get();db.beginTransaction();try{assertEquals(-1,tags().savePreservingOwnedIdentity(app,10,100,123,7L));assertEquals(-1,tags().savePreservingOwnedIdentity(app,10,200,456,8L));try(var row=db.rawQuery("SELECT m_online_id,Nova_pinned,name_movie FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(123,row.getLong(0));assertEquals(9,row.getLong(1));assertEquals("Original title",row.getString(2));}try(var row=db.rawQuery("SELECT m_online_id,Nova_pinned,video_id FROM MOVIE WHERE _id=200",null)){assertTrue(row.moveToFirst());assertEquals(456,row.getLong(0));assertEquals(8,row.getLong(1));assertEquals(11,row.getLong(2));}}finally{db.endTransaction();}
    }

    @Test public void ownedReplacementPreservesNullPinAndDoesNotFillUnselectedFileDuration(){
        var db=holder.get();db.execSQL("UPDATE MOVIE SET Nova_pinned=NULL WHERE _id=100");db.execSQL("UPDATE files SET duration=0 WHERE _id=10");MovieTags canonical=tags();canonical.setRuntime(5,java.util.concurrent.TimeUnit.MINUTES);db.beginTransaction();try{assertEquals(100,canonical.savePreservingOwnedIdentity(app,10,100,123,null));try(var row=db.rawQuery("SELECT m.Nova_pinned,f.duration,f.bookmark,f.Archos_lastTimePlayed FROM MOVIE m JOIN files f ON f._id=m.video_id WHERE m._id=100",null)){assertTrue(row.moveToFirst());assertTrue(row.isNull(0));assertEquals(0,row.getLong(1));assertEquals(4444,row.getLong(2));assertEquals(100,row.getLong(3));}}finally{db.endTransaction();}
    }
    @Test public void changedFileKindRefusesReplacementAndDirectOwnedInsertRequiresTransaction(){
        var db=holder.get();db.execSQL("UPDATE files SET ArchosMediaScraper_type=12 WHERE _id=10");db.beginTransaction();try{assertEquals(-1,tags().savePreservingOwnedIdentity(app,10,100,123,9L));try(var row=db.rawQuery("SELECT m_online_id,name_movie FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(123,row.getLong(0));assertEquals("Original title",row.getString(1));}}finally{db.endTransaction();}
        db.execSQL("UPDATE files SET ArchosMediaScraper_type=11 WHERE _id=10");try{tags().savePreservingOwnedIdentity(app,10,100,123,9L);fail("Uncoordinated owned replacement accepted");}catch(IllegalStateException expected){}
        try(var row=db.rawQuery("SELECT m_online_id,Nova_pinned FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(123,row.getLong(0));assertEquals(9,row.getLong(1));}
    }

    private void episodeFixture() {
        var db=holder.get();
        db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,bookmark,duration,Archos_lastTimePlayed,Archos_traktSeen) VALUES(20,20,3,'file:///episode.mkv',6789,0,100,1)");
        db.execSQL("INSERT INTO SHOW(_id,s_online_id,name_show,Nova_pinned) VALUES(300,666,'Receiving parent',9)");
        db.execSQL("INSERT INTO EPISODE(_id,video_id,e_online_id,show_episode,season_episode,number_episode,name_episode,plot_episode,cover_episode,picture_episode) VALUES(400,20,555,300,1,1,'Old episode','Old facts','old-cover','old-picture')");
        db.execSQL("INSERT INTO user_collection_item(_id,collection_id,media_type,media_id) VALUES(10,7,2,300)");
        db.execSQL("INSERT INTO V_GUESTS(episode_v_guests,name_v_guests,role_v_guests) VALUES(400,'Old guest','Old role')");
    }
    private EpisodeTags episodeTags(long showProvider) {
        ShowTags show=new ShowTags();show.setOnlineId(showProvider);show.setTitle("Fetched parent");
        EpisodeTags episode=new EpisodeTags(show,2,3);episode.setOnlineId(777);episode.setTitle("Fresh episode");episode.setPlot("Fresh episode facts");episode.addActorIfAbsent("Fresh guest","Fresh role");episode.setRuntime(5,java.util.concurrent.TimeUnit.MINUTES);return episode;
    }
    @Test public void ownedEpisodeUpdatesCanonicalFactsWithoutDeletingItsLastParentOrCollectionAndRollsBack() {
        episodeFixture();var db=holder.get();
        for(boolean recursive:new boolean[]{false,true}) {
            db.execSQL("PRAGMA recursive_triggers="+(recursive?1:0));db.beginTransaction();try {
                assertEquals(400,episodeTags(666).savePreservingOwnedIdentity(app,20,400,555,300,666,9L));
                try(var row=db.rawQuery("SELECT e._id,e.e_online_id,e.name_episode,e.plot_episode,e.season_episode,e.number_episode,e.cover_episode,e.picture_episode,s._id,s.s_online_id,s.name_show,s.Nova_pinned,f.bookmark,f.duration,f.Archos_lastTimePlayed,f.Archos_traktSeen,f.ArchosMediaScraper_id FROM EPISODE e JOIN SHOW s ON s._id=e.show_episode JOIN files f ON f._id=e.video_id WHERE e._id=400",null)) {
                    assertTrue(row.moveToFirst());assertEquals(400,row.getLong(0));assertEquals(777,row.getLong(1));assertEquals("Fresh episode",row.getString(2));assertEquals("Fresh episode facts",row.getString(3));assertEquals(2,row.getLong(4));assertEquals(3,row.getLong(5));assertTrue(row.isNull(6));assertTrue(row.isNull(7));assertEquals(300,row.getLong(8));assertEquals(666,row.getLong(9));assertEquals("Receiving parent",row.getString(10));assertEquals(9,row.getLong(11));assertEquals(6789,row.getLong(12));assertEquals(0,row.getLong(13));assertEquals(100,row.getLong(14));assertEquals(1,row.getLong(15));assertEquals(400,row.getLong(16));
                }
                try(var row=db.rawQuery("SELECT name_v_guests,role_v_guests FROM V_GUESTS WHERE episode_v_guests=400",null)){assertTrue(row.moveToFirst());assertEquals("Fresh guest",row.getString(0));assertEquals("Fresh role",row.getString(1));assertFalse(row.moveToNext());}
                try(var row=db.rawQuery("SELECT s._id,s.Nova_pinned FROM user_collection_item i JOIN SHOW s ON s._id=i.media_id WHERE i._id=10",null)){assertTrue(row.moveToFirst());assertEquals(300,row.getLong(0));assertEquals(9,row.getLong(1));}
                try(var row=db.rawQuery("PRAGMA foreign_key_check",null)){assertFalse(row.moveToFirst());}
            } finally {db.endTransaction();}
            try(var row=db.rawQuery("SELECT e_online_id,name_episode,cover_episode FROM EPISODE WHERE _id=400",null)){assertTrue(row.moveToFirst());assertEquals(555,row.getLong(0));assertEquals("Old episode",row.getString(1));assertEquals("old-cover",row.getString(2));}
            try(var row=db.rawQuery("SELECT name_v_guests FROM V_GUESTS WHERE episode_v_guests=400",null)){assertTrue(row.moveToFirst());assertEquals("Old guest",row.getString(0));assertFalse(row.moveToNext());}
        }
    }
    @Test public void ownedEpisodeRejectsChangedParentPinProviderAndUncoordinatedSave() {
        episodeFixture();var db=holder.get();
        try {episodeTags(999).savePreservingOwnedIdentity(app,20,400,555,300,666,9L);fail("Different parent accepted");}catch(IllegalArgumentException expected){}
        db.beginTransaction();try {
            assertEquals(-1,episodeTags(666).savePreservingOwnedIdentity(app,20,400,555,300,666,7L));
            assertEquals(-1,episodeTags(666).savePreservingOwnedIdentity(app,20,400,999,300,666,9L));
        }finally{db.endTransaction();}
        try {episodeTags(666).savePreservingOwnedIdentity(app,20,400,555,300,666,9L);fail("Missing native transaction accepted");}catch(IllegalStateException expected){}
        try(var row=db.rawQuery("SELECT e.e_online_id,s.Nova_pinned FROM EPISODE e JOIN SHOW s ON s._id=e.show_episode WHERE e._id=400",null)){assertTrue(row.moveToFirst());assertEquals(555,row.getLong(0));assertEquals(9,row.getLong(1));}
    }

    @Test public void verifiedMaterialPreparesImmutableOperationsWithoutPublishingOrWritingNativeState()throws Exception {
        var requested=new com.archos.mediacenter.video.utils.backup.PortableMediaMatches.Match("tmdb:movie:789",null);
        var inventory=com.archos.mediacenter.video.utils.backup.MediaMatchInventory.capture(app);MovieTags canonical=tags();
        var batch=com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial.prepare(app,inventory.owners.get(10L),requested,Uri.parse("file:///one.mkv"),0,9L,(context,match,file)->new com.archos.mediascraper.ScrapeDetailResult(canonical,true,null,com.archos.mediascraper.ScrapeStatus.OKAY,null));
        canonical.setTitle("Later mutable provider object");
        try(var row=holder.get().rawQuery("SELECT m_online_id,name_movie FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(123,row.getLong(0));assertEquals("Original title",row.getString(1));}
        try{batch.operations.clear();fail("Mutable canonical operation list");}catch(UnsupportedOperationException expected){}
        var db=holder.get();db.beginTransaction();try {
            app.getContentResolver().applyBatch(ScraperStore.AUTHORITY,new java.util.ArrayList<>(batch.operations));
            try(var row=db.rawQuery("SELECT m_online_id,name_movie,Nova_pinned FROM MOVIE WHERE _id=100",null)){assertTrue(row.moveToFirst());assertEquals(789,row.getLong(0));assertEquals("Fresh canonical title",row.getString(1));assertEquals(9,row.getLong(2));}
        }finally{db.endTransaction();}
    }
    @Test public void providerMaterialMustMatchExactRequestedMovieAndEpisodeIdentity()throws Exception {
        var movie=new com.archos.mediacenter.video.utils.backup.PortableMediaMatches.Match("tmdb:movie:789",null);
        var episode=new com.archos.mediacenter.video.utils.backup.PortableMediaMatches.Match("tmdb:tv:666:2:3",777L);
        var okay=com.archos.mediascraper.ScrapeStatus.OKAY;
        assertNotNull(com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial.verify(movie,new com.archos.mediascraper.ScrapeDetailResult(tags(),true,null,okay,null)));
        assertNotNull(com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial.verify(episode,new com.archos.mediascraper.ScrapeDetailResult(episodeTags(666),false,null,okay,null)));
        for(var wrong:new com.archos.mediascraper.ScrapeDetailResult[]{null,new com.archos.mediascraper.ScrapeDetailResult(tags(),true,null,com.archos.mediascraper.ScrapeStatus.ERROR,null),new com.archos.mediascraper.ScrapeDetailResult(episodeTags(999),false,null,okay,null),new com.archos.mediascraper.ScrapeDetailResult(tags(),true,null,okay,null)}) {
            try{com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial.verify(episode,wrong);fail("Wrong provider material accepted");}catch(java.io.IOException expected){}
        }
        var changed=episodeTags(666);changed.setOnlineId(778);
        try{com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial.verify(episode,new com.archos.mediascraper.ScrapeDetailResult(changed,false,null,okay,null));fail("Wrong episode provider accepted");}catch(java.io.IOException expected){}
    }

}
