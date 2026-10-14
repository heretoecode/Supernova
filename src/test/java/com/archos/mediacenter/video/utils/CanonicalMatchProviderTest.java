package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import com.archos.mediascraper.MovieTags;
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

}
