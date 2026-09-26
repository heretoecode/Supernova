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
