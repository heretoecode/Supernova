package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Preview413Test {
    private Entry movie(long id,String path,long bytes){
        return new Entry(new Movie(id,path,"Film",id,"Plot",2024,7,"",null,100000,0,0,0,false,false,false,false,id,0,1920,1080,null,null,null,null,0,1,bytes,0),0,0,"");
    }
    @Test public void indexedDatabaseUriMustNotClassifyNetworkAsLocal(){
        Snapshot s=new Snapshot();Entry local=movie(1,"/storage/movies/local.mkv",1024),dav=movie(2,"webdavs://server/movies/remote.mkv",2048),smb=movie(3,"smb://server/movies/remote.mkv",4096);
        assertEquals("content",((Video)dav.media).getUri().getScheme());assertEquals("webdavs",((Video)dav.media).getFileUri().getScheme());
        s.movies.add(local);s.movies.add(dav);s.movies.add(smb);
        String text=PreviewLibrarySummary.describe(RuntimeEnvironment.getApplication(),s,false);
        assertTrue(text.startsWith("3 Movies"));assertTrue(text.contains("Local Storage    1 movies"));assertTrue(text.contains("Network / WebDAV    2 movies"));
        assertTrue(text.contains(android.text.format.Formatter.formatShortFileSize(RuntimeEnvironment.getApplication(),7168)));
        assertTrue(text.contains(android.text.format.Formatter.formatShortFileSize(RuntimeEnvironment.getApplication(),6144)));
    }
    @Test public void mixedEpisodeSourcesCountDistinctShowsAndSumIndexedBytes(){
        Snapshot s=new Snapshot();Entry local=episode(1,7,"/storage/a.mkv"),network=episode(2,7,"https://server/dav/b.mkv"),other=episode(3,8,"webdav://server/c.mkv");
        s.episodes.add(local);s.episodes.add(network);s.episodes.add(other);
        String text=PreviewLibrarySummary.describe(RuntimeEnvironment.getApplication(),s,true);
        assertTrue(text.contains("Local Storage    1 shows"));assertTrue(text.contains("Network / WebDAV    2 shows"));assertTrue(text.contains("Shows spanning sources"));
        assertTrue(text.contains(android.text.format.Formatter.formatShortFileSize(RuntimeEnvironment.getApplication(),3000)));
    }
    private Entry episode(long id,long show,String path){return new Entry(new Episode(id,(int)id,1,(int)id,"Episode",0,0,"","","Show",path,null,null,100000,0,0,0,false,false,false,false,1,0,1920,1080,null,null,null,null,0,1,1000),0,show,"");}
}
