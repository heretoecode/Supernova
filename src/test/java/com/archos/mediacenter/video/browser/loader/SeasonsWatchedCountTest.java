package com.archos.mediacenter.video.browser.loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediacenter.video.player.PlayerActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class SeasonsWatchedCountTest {
    @Test public void wholeShowCountDistinguishesSeasonAndEpisodeWhileDeduplicatingVersions(){
        try(SQLiteDatabase db=SQLiteDatabase.create(null)){
            String season=VideoStore.Video.VideoColumns.SCRAPER_E_SEASON,episode=VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE,bookmark=VideoStore.Video.VideoColumns.BOOKMARK;
            db.execSQL("CREATE TABLE media ("+season+" INTEGER,"+episode+" INTEGER,"+bookmark+" INTEGER)");
            db.execSQL("INSERT INTO media VALUES (1,1,?),(1,1,?),(2,1,?),(1,2,0)",new Object[]{PlayerActivity.LAST_POSITION_END,PlayerActivity.LAST_POSITION_END,PlayerActivity.LAST_POSITION_END});
            try(Cursor cursor=db.rawQuery("SELECT "+SeasonsLoader.watchedCountProjection(true)+" FROM media",null)){
                assertTrue(cursor.moveToFirst());assertEquals(2,cursor.getInt(0));
            }
        }
    }
    @Test public void twoWatchedEncodesDoNotMarkAnotherEpisodeWatched(){
        try(SQLiteDatabase db=SQLiteDatabase.create(null)){
            String episode=VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE,bookmark=VideoStore.Video.VideoColumns.BOOKMARK;
            db.execSQL("CREATE TABLE media ("+episode+" INTEGER,"+bookmark+" INTEGER)");
            db.execSQL("INSERT INTO media VALUES (1,?),(1,?),(2,0)",new Object[]{PlayerActivity.LAST_POSITION_END,PlayerActivity.LAST_POSITION_END});
            try(Cursor cursor=db.rawQuery("SELECT COUNT(DISTINCT "+episode+"),"+SeasonsLoader.watchedCountProjection()+" FROM media",null)){
                assertTrue(cursor.moveToFirst());assertEquals(2,cursor.getInt(0));assertEquals(1,cursor.getInt(1));
            }
        }
    }
}
