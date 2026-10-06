package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewTechnicalEnrichmentTest {
    private Entry entry(long id){return new Entry(new Video(id,"/storage/file-"+id+".mkv","File",null,10000,0,0,0,false,false,false,false,0,1000),0,0,"");}
    @Test public void homePriorityStillIncludesEveryPhysicalVersion(){
        PreviewLibraryLoader.Snapshot snapshot=new PreviewLibraryLoader.Snapshot();Entry home=entry(1),visible=entry(2),alternate=entry(3);
        snapshot.continuingMovies.add(home);snapshot.movies.add(visible);snapshot.technical.addAll(Arrays.asList(home,visible,alternate));
        List<Entry> candidates=PreviewMetadata.candidates(snapshot,0);
        assertSame(home,candidates.get(0));assertTrue(candidates.contains(alternate));assertTrue(candidates.indexOf(visible)<candidates.indexOf(alternate));
    }
    @Test public void cachedHdrSurvivesEntryRecreationButNotChangedFileFingerprint(){
        Application app=RuntimeEnvironment.getApplication();Entry original=entry(7);original.modified=50;
        app.getSharedPreferences("preview-technical-v1",0).edit().clear().putString("hdr:"+PreviewMetadata.key(original),"HLG").commit();
        Entry reloaded=entry(7);reloaded.modified=50;PreviewMetadata.hydrate(app,Arrays.asList(reloaded));assertEquals("HLG",reloaded.hdr);
        Entry replaced=entry(7);replaced.modified=51;PreviewMetadata.hydrate(app,Arrays.asList(replaced));assertEquals("",replaced.hdr);
        Entry resized=entry(7);resized.modified=50;resized.bytes=2000;PreviewMetadata.hydrate(app,Arrays.asList(resized));assertEquals("",resized.hdr);
    }
    @Test public void detailsReadsOnlyTheCurrentIndexedFingerprint(){
        Application app=RuntimeEnvironment.getApplication();Entry entry=entry(17);entry.modified=100;
        PreviewLibraryLoader.Snapshot snapshot=new PreviewLibraryLoader.Snapshot();snapshot.technical.add(entry);
        app.getSharedPreferences("preview-technical-v1",0).edit().clear().putString("hdr:"+PreviewMetadata.key(entry),"HLG").commit();
        Video reloaded=(Video)entry(17).media;
        assertEquals("HLG",PreviewVariants.dynamicRange(app,reloaded,snapshot));
        entry.modified=101;assertEquals("",PreviewVariants.dynamicRange(app,reloaded,snapshot));
        assertEquals("",PreviewVariants.dynamicRange(app,reloaded,null));
        assertNull(reloaded.getMetadata());
    }
}
