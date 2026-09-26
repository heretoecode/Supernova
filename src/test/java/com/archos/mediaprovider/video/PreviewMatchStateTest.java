package com.archos.mediaprovider.video;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import com.archos.mediaprovider.DbHolder;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Real provider and schema, exercising the metadata replacement path used by accepted matches. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewMatchStateTest {
    private DbHolder database;private ScraperProvider provider;
    @Before public void setup(){
        Application app=RuntimeEnvironment.getApplication();app.deleteDatabase("match-state.db");
        database=new DbHolder(new VideoOpenHelper(app,"match-state.db",VideoOpenHelper.getDatabaseVersion()));
        ScraperProvider.hookUriMatcher(new UriMatcher(UriMatcher.NO_MATCH));provider=new ScraperProvider(app,database);
        for(int id=1;id<=2;id++){
            ContentValues file=new ContentValues();file.put("_id",id);file.put("remote_id",id);file.put("_data","/movies/version-"+id+".mkv");
            file.put(VideoStore.Video.VideoColumns.BOOKMARK,id==1?12345:-2);file.put(VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED,900L+id);
            assertTrue(database.get().insertOrThrow(VideoOpenHelper.FILES_TABLE_NAME,null,file)>0);
        }
    }
    @After public void close(){if(database!=null)database.close();}
    @Test public void movieCorrectionKeepsBothPhysicalVersionsAndTheirHistory(){
        for(int mediaId=1;mediaId<=2;mediaId++){
            ContentValues tags=new ContentValues();tags.put(ScraperStore.Movie.VIDEO_ID,mediaId);tags.put(ScraperStore.Movie.NAME,"Original");
            assertNotNull(provider.insert(ScraperStore.Movie.URI.BASE,tags));tags.put(ScraperStore.Movie.NAME,"Corrected");
            assertNotNull(provider.insert(ScraperStore.Movie.URI.BASE,tags));
        }
        assertFiles();
    }
    @Test public void seriesReconciliationKeepsEachPhysicalEpisodeFileAndHistory(){
        ContentValues show=new ContentValues();show.put(ScraperStore.Show.NAME,"Original");show.put(ScraperStore.Show.ONLINE_ID,10);
        long oldId=ContentUris.parseId(provider.insert(ScraperStore.Show.URI.BASE,show));show.put(ScraperStore.Show.NAME,"Corrected");show.put(ScraperStore.Show.ONLINE_ID,20);
        long newId=ContentUris.parseId(provider.insert(ScraperStore.Show.URI.BASE,show));
        for(int mediaId=1;mediaId<=2;mediaId++){
            ContentValues tags=new ContentValues();tags.put(ScraperStore.Episode.VIDEO_ID,mediaId);tags.put(ScraperStore.Episode.SHOW,oldId);tags.put(ScraperStore.Episode.NAME,"Original episode");
            assertNotNull(provider.insert(ScraperStore.Episode.URI.BASE,tags));
        }
        for(int mediaId=1;mediaId<=2;mediaId++){
            ContentValues tags=new ContentValues();tags.put(ScraperStore.Episode.VIDEO_ID,mediaId);tags.put(ScraperStore.Episode.SHOW,newId);tags.put(ScraperStore.Episode.NAME,"Corrected episode");
            assertNotNull(provider.insert(ScraperStore.Episode.URI.BASE,tags));
        }
        assertFiles();
    }
    private void assertFiles(){
        try(Cursor files=database.get().query(VideoOpenHelper.FILES_TABLE_NAME,new String[]{"_id","remote_id","_data",VideoStore.Video.VideoColumns.BOOKMARK,VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED},null,null,null,null,"_id")){
            assertEquals(2,files.getCount());for(int id=1;id<=2;id++){assertTrue(files.moveToNext());assertEquals(id,files.getLong(0));assertEquals(id,files.getLong(1));assertEquals("/movies/version-"+id+".mkv",files.getString(2));assertEquals(id==1?12345:-2,files.getInt(3));assertEquals(900L+id,files.getLong(4));}
        }
    }
}
