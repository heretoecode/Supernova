package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import com.archos.mediaprovider.video.VideoOpenHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

/** Proves the native side effects the selected-match participant must journal before integration. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class MediaMatchDependencyTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,ArchosMediaScraper_type,ArchosMediaScraper_id,bookmark) VALUES(10,10,3,'file:///one.mkv',11,100,4444),(11,11,3,'file:///two.mkv',11,200,5555)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie,plot_movie) VALUES(100,10,123,9,'Original title','Original facts'),(200,11,456,8,'Other title','Other facts')");}}
    @Test public void fileLinkRewriteDeletesOldProviderMetadataAndCannotBorrowAnotherFilesNativeJoin()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.setForeignKeyConstraintsEnabled(true);db.beginTransaction();try{
            db.execSQL("UPDATE files SET ArchosMediaScraper_id=200 WHERE _id=10");try(var rows=db.rawQuery("SELECT COUNT(*) FROM MOVIE WHERE _id=100",null)){assertTrue(rows.moveToFirst());assertEquals(0,rows.getInt(0));}
            try(var rows=db.query(VideoOpenHelper.VIDEO_VIEW_NAME,new String[]{"m_online_id","bookmark"},"_id=10",null,null,null,null)){assertTrue(rows.moveToFirst());assertTrue(rows.isNull(0));assertEquals(4444,rows.getInt(1));}
            try(var rows=db.rawQuery("SELECT video_id,m_online_id,Nova_pinned FROM MOVIE WHERE _id=200",null)){assertTrue(rows.moveToFirst());assertEquals(11,rows.getInt(0));assertEquals(456,rows.getInt(1));assertEquals(8,rows.getInt(2));}
        }finally{db.endTransaction();}
        try(var rows=db.rawQuery("SELECT video_id,m_online_id,Nova_pinned,name_movie,plot_movie FROM MOVIE WHERE _id=100",null)){assertTrue(rows.moveToFirst());assertEquals(10,rows.getInt(0));assertEquals(123,rows.getInt(1));assertEquals(9,rows.getInt(2));assertEquals("Original title",rows.getString(3));assertEquals("Original facts",rows.getString(4));}
        }
    }
    @Test public void changingOnlyProviderIdentityLeavesStaleScrapedFactsAndIsNotACompleteRestoration()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.beginTransaction();try{db.execSQL("UPDATE MOVIE SET m_online_id=789 WHERE _id=100");try(var rows=db.query(VideoOpenHelper.VIDEO_VIEW_NAME,new String[]{"m_online_id","m_name","m_plot"},"_id=10",null,null,null,null)){assertTrue(rows.moveToFirst());assertEquals(789,rows.getInt(0));assertEquals("Original title",rows.getString(1));assertEquals("Original facts",rows.getString(2));}}finally{db.endTransaction();}}
    }
    @Test public void episodeLinkRewriteDeletesItsLastParentShowPinAndStrandsReceivingShowCollectionReference(){
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.setForeignKeyConstraintsEnabled(true);db.beginTransaction();try{
            db.delete("MOVIE",null,null);
            android.content.ContentValues first=new android.content.ContentValues();first.put("_id",1);first.put(com.archos.mediaprovider.video.ScraperStore.Show.ONLINE_ID,123);first.put(com.archos.mediaprovider.video.ScraperStore.Show.NAME,"Original show");first.put("Nova_pinned",9);db.insertOrThrow("SHOW",null,first);
            android.content.ContentValues other=new android.content.ContentValues(first);other.put("_id",2);other.put(com.archos.mediaprovider.video.ScraperStore.Show.ONLINE_ID,456);other.put(com.archos.mediaprovider.video.ScraperStore.Show.NAME,"Other show");other.put("Nova_pinned",8);db.insertOrThrow("SHOW",null,other);
            for(int index=0;index<2;index++){android.content.ContentValues episode=new android.content.ContentValues();episode.put("_id",300+index);episode.put(com.archos.mediaprovider.video.ScraperStore.Episode.VIDEO_ID,10+index);episode.put(com.archos.mediaprovider.video.ScraperStore.Episode.SHOW,1+index);episode.put(com.archos.mediaprovider.video.ScraperStore.Episode.SEASON,1);episode.put(com.archos.mediaprovider.video.ScraperStore.Episode.NUMBER,1);db.insertOrThrow("EPISODE",null,episode);}
            db.execSQL("INSERT INTO user_collection(_id,name,sort_name,position,hidden) VALUES(7,'Receiving shows','Receiving shows',2,0)");db.execSQL("INSERT INTO user_collection_item(_id,collection_id,media_type,media_id) VALUES(9,7,2,1)");
            db.execSQL("UPDATE files SET ArchosMediaScraper_id=301 WHERE _id=10");
            try(var row=db.rawQuery("SELECT COUNT(*) FROM SHOW WHERE _id=1",null)){assertTrue(row.moveToFirst());assertEquals(0,row.getLong(0));}
            try(var row=db.rawQuery("SELECT i.media_id,s._id FROM user_collection_item i LEFT JOIN SHOW s ON s._id=i.media_id WHERE i._id=9",null)){assertTrue(row.moveToFirst());assertEquals(1,row.getLong(0));assertTrue(row.isNull(1));}
            try(var row=db.rawQuery("SELECT s_online_id,Nova_pinned FROM SHOW WHERE _id=2",null)){assertTrue(row.moveToFirst());assertEquals(456,row.getLong(0));assertEquals(8,row.getLong(1));}
            try(var row=db.rawQuery("SELECT bookmark FROM files WHERE _id=10",null)){assertTrue(row.moveToFirst());assertEquals(4444,row.getLong(0));}
        }finally{db.endTransaction();}}
    }

}
