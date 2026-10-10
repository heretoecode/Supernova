package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewEnrichmentQueueTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
    @Test public void oldCountryOrLanguageJobsCannotRunUnderCurrentScope()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper=(SQLiteOpenHelper)ReflectionHelpers.callConstructor(Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),ReflectionHelpers.ClassParameter.from(Context.class,context));
        try{
            SQLiteDatabase db=helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('movie:1:en:US:true','movie',1,0,3,0,0,0)");
            db.execSQL("INSERT INTO jobs VALUES('movie:2:en:IE:true','movie',2,40,1,0,0,0)");
            try(Cursor row=PreviewEnrichmentQueue.nextJob(db,"en:IE:true",1)){assertTrue(row.moveToFirst());assertEquals(2,row.getLong(2));}
            try(Cursor row=PreviewEnrichmentQueue.nextJob(db,"fr:IE:true",1)){assertFalse(row.moveToFirst());}
            try(Cursor row=PreviewEnrichmentQueue.nextJob(db,"en:US:true",1)){assertTrue(row.moveToFirst());assertEquals(1,row.getLong(2));assertEquals(3,row.getInt(3));}
        }finally{helper.close();}
    }
    @Test public void horizontalViewportUsesScrolledCardsRatherThanFirstSix(){
        androidx.recyclerview.widget.RecyclerView rail=org.mockito.Mockito.mock(androidx.recyclerview.widget.RecyclerView.class);
        org.mockito.Mockito.when(rail.getWidth()).thenReturn(300);
        org.mockito.Mockito.when(rail.getChildCount()).thenReturn(3);
        java.util.List<PreviewLibraryLoader.Entry> entries=new java.util.ArrayList<>(),visible=new java.util.ArrayList<>(),next=new java.util.ArrayList<>();
        for(int i=0;i<20;i++)entries.add(new PreviewLibraryLoader.Entry(org.mockito.Mockito.mock(com.archos.mediacenter.video.browser.adapters.object.Movie.class),0,0,""));
        for(int i=0;i<3;i++){
            android.view.View child=org.mockito.Mockito.mock(android.view.View.class);
            org.mockito.Mockito.when(rail.getChildAt(i)).thenReturn(child);org.mockito.Mockito.when(rail.getChildAdapterPosition(child)).thenReturn(i+7);
            org.mockito.Mockito.when(child.getLeft()).thenReturn(i*100-100);org.mockito.Mockito.when(child.getRight()).thenReturn(i*100);
        }
        PreviewPages.collectVisibleRail(rail,entries,visible,next);
        assertEquals(java.util.Arrays.asList(entries.get(8),entries.get(9)),visible);
        assertEquals(entries.subList(10,16),next);
    }
    @Test public void replacingViewportRestoresPagePriorityWithoutLosingForegroundOrProgress()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper=(SQLiteOpenHelper)ReflectionHelpers.callConstructor(Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),ReflectionHelpers.ClassParameter.from(Context.class,context));
        try{
            SQLiteDatabase db=helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('movie:1:test','movie',1,10,3,1234,0,2)");
            db.execSQL("INSERT INTO jobs VALUES('movie:2:test','movie',2,0,4,0,0,0)");
            db.execSQL("INSERT INTO jobs VALUES('tv:3:test','tv',3,10,2,0,0,0)");
            java.util.Map<String,Integer> baseline=new java.util.HashMap<>();baseline.put("movie:1:test",PreviewEnrichmentQueue.CURRENT_PAGE);baseline.put("movie:2:test",PreviewEnrichmentQueue.CURRENT_PAGE);
            PreviewEnrichmentQueue.restorePagePriorities(db,baseline);
            try(Cursor cursor=db.rawQuery("SELECT priority,stage,next_at,season_cursor FROM jobs ORDER BY identity",null)){
                assertTrue(cursor.moveToNext());assertEquals(PreviewEnrichmentQueue.CURRENT_PAGE,cursor.getInt(0));assertEquals(3,cursor.getInt(1));assertEquals(1234,cursor.getLong(2));assertEquals(2,cursor.getInt(3));
                assertTrue(cursor.moveToNext());assertEquals(PreviewEnrichmentQueue.FOREGROUND,cursor.getInt(0));assertEquals(4,cursor.getInt(1));
                assertTrue(cursor.moveToNext());assertEquals(PreviewEnrichmentQueue.BACKGROUND,cursor.getInt(0));
            }
        }finally{helper.close();}
    }
    @Test public void episodePriorityTargetsParentSeriesAndKeepsPackageProgress()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper=(SQLiteOpenHelper)ReflectionHelpers.callConstructor(Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),ReflectionHelpers.ClassParameter.from(Context.class,context));
        try{
            SQLiteDatabase db=helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('tv:42:test','tv',42,40,3,0,0,2)");
            com.archos.mediacenter.video.browser.adapters.object.Episode episode=org.mockito.Mockito.mock(com.archos.mediacenter.video.browser.adapters.object.Episode.class);
            org.mockito.Mockito.when(episode.getOnlineId()).thenReturn(999L);
            PreviewLibraryLoader.Entry entry=new PreviewLibraryLoader.Entry(episode,0,7,"");entry.onlineId=42;
            ReflectionHelpers.callStaticMethod(PreviewEnrichmentQueue.class,"offerEntry",ReflectionHelpers.ClassParameter.from(SQLiteDatabase.class,db),ReflectionHelpers.ClassParameter.from(PreviewLibraryLoader.Entry.class,entry),ReflectionHelpers.ClassParameter.from(int.class,PreviewEnrichmentQueue.HOME),ReflectionHelpers.ClassParameter.from(String.class,"test"));
            try(Cursor cursor=db.rawQuery("SELECT media,priority,stage,season_cursor FROM jobs",null)){
                assertEquals(1,cursor.getCount());assertTrue(cursor.moveToFirst());assertEquals(42,cursor.getLong(0));assertEquals(PreviewEnrichmentQueue.HOME,cursor.getInt(1));assertEquals(3,cursor.getInt(2));assertEquals(2,cursor.getInt(3));
            }
        }finally{helper.close();}
    }
    @Test public void seasonQueueUpgradePreservesPendingJobs()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        try(SQLiteDatabase db=context.openOrCreateDatabase("preview-enrichment.db",0,null)){
            db.execSQL("CREATE TABLE jobs(identity TEXT PRIMARY KEY,kind TEXT NOT NULL,media INTEGER NOT NULL,priority INTEGER NOT NULL,stage INTEGER NOT NULL,next_at INTEGER NOT NULL,completed_at INTEGER NOT NULL)");
            db.execSQL("INSERT INTO jobs VALUES('tv:42','tv',42,10,3,1234,0)");db.setVersion(1);
        }
        SQLiteOpenHelper helper=(SQLiteOpenHelper)ReflectionHelpers.callConstructor(Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),ReflectionHelpers.ClassParameter.from(Context.class,context));
        try(Cursor cursor=helper.getWritableDatabase().rawQuery("SELECT identity,stage,next_at,season_cursor FROM jobs",null)){
            assertTrue(cursor.moveToFirst());assertEquals("tv:42",cursor.getString(0));assertEquals(3,cursor.getInt(1));assertEquals(1234,cursor.getLong(2));assertEquals(0,cursor.getInt(3));
        }finally{helper.close();}
    }
}
