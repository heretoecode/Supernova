package com.archos.mediacenter.video.leanback;
import android.app.Application;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class VersionPreferenceTest {
 private Movie movie(long id,int width){return new Movie(id,"/storage/v"+id+".mkv","Film",1,"Plot",2024,7,"",null,100000,0,0,0,false,false,false,false,550,0,width,1080,null,null,null,null,0,1,100000,0);}
 @Test public void manualVersionPersistsAcrossSnapshotRebuildsAndOverridesQuality(){var c=RuntimeEnvironment.getApplication();Movie lower=movie(1,1920),higher=movie(2,3840);List<Entry> entries=Arrays.asList(new Entry(lower,0,0,""),new Entry(higher,0,0,""));PreviewVariants.initialize(c);assertEquals(2,((Video)PreviewVariants.logicalChoices(entries).get(0).media).getId());PreviewVariants.remember(c,lower);assertEquals(1,((Video)PreviewVariants.logicalChoices(entries).get(0).media).getId());PreviewVariants.initialize(c);assertEquals(1,((Video)PreviewVariants.logicalChoices(entries).get(0).media).getId());}
 @Test public void unavailableManualChoiceUsesAvailableEncodeWithoutDeletingPreference(){var c=RuntimeEnvironment.getApplication();Movie lower=movie(1,1920),higher=movie(2,3840);PreviewVariants.remember(c,lower);com.archos.mediaprovider.video.SupernovaLibraryPolicy.availability(c,1,lower.getFileUri(),true);assertEquals(2,((Video)PreviewVariants.logicalChoices(Arrays.asList(new Entry(lower,0,0,""),new Entry(higher,0,0,""))).get(0).media).getId());assertTrue(androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).contains("preview_version_choice:movie:550"));}
}
