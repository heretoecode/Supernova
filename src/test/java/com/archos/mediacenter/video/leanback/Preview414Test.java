package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Preview414Test {
    private Video movie(long id,int width,int height,long bytes){return new Movie(id,"/storage/version-"+id+".mkv","Film",1,"Plot",2024,7,"",null,100000,0,0,0,false,false,false,false,1,0,width,height,null,null,null,null,0,1,bytes,0);}
    @Test public void qualitySelectionUsesIndexedResolutionThenBitrateThenStableIdentity(){
        Video unknown=movie(1,0,0,999999),hd=movie(2,1920,1080,500000),uhdLow=movie(3,3840,2160,600000),uhdHigh=movie(4,3840,2160,900000),tie=movie(5,3840,2160,900000);
        List<Video> choices=new ArrayList<>(Arrays.asList(unknown,hd,tie,uhdLow,uhdHigh));choices.sort(PreviewVariants.BEST_FIRST);
        assertEquals(Arrays.asList(uhdHigh,tie,uhdLow,hd,unknown),choices);
        assertTrue(PreviewVariants.label(unknown).contains("version-1.mkv"));
    }
    @Test public void logicalMovieChoiceKeepsResumeFromAnotherEncode(){
        Video hd=movie(2,1920,1080,500000),uhd=movie(3,3840,2160,900000);hd.setResumeMs(33000);
        List<Entry> selected=PreviewVariants.logicalChoices(Arrays.asList(new Entry(hd,0,0,""),new Entry(uhd,0,0,"")));
        assertEquals(1,selected.size());assertSame(uhd,selected.get(0).media);assertEquals(33000,uhd.getResumeMs());
    }
    @Test public void physicalVariantsDoNotInflateShowEpisodeCount(){
        List<Entry> files=new ArrayList<>();for(int i=1;i<=2;i++)files.add(new Entry(new Episode(i,7,1,1,"Episode",0,0,"","","Show","/storage/v"+i+".mkv",null,null,100000,0,0,0,false,false,false,false,1,0,1920,1080,null,null,null,null,0,1,1000),0,7,""));
        Entry show=new Entry(new Tvshow(7,"Show",null,1,1,0,"/show"),0,7,"");Snapshot s=PreviewLibraryLoader.build(files,Collections.singletonList(show));assertEquals(1,s.shows.get(0).episodes);assertEquals(2,s.shows.get(0).files);
    }
}
