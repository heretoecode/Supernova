package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Preview412Test {
 @Test public void sparseLogoUsesVisibleBoundsAndRetainsAspect(){Bitmap source=Bitmap.createBitmap(500,300,Bitmap.Config.ARGB_8888);for(int y=130;y<170;y++)for(int x=200;x<300;x++)source.setPixel(x,y,Color.WHITE);Bitmap result=OfficialTitleArtwork.visibleArtwork(source);assertTrue(result.getWidth()<110);assertTrue(result.getHeight()<50);assertEquals(Color.WHITE,result.getPixel(result.getWidth()/2,result.getHeight()/2));}
 @Test public void emptyLogoDoesNotCreateInvalidCrop(){Bitmap source=Bitmap.createBitmap(30,20,Bitmap.Config.ARGB_8888);assertSame(source,OfficialTitleArtwork.visibleArtwork(source));}
 @Test public void providerFilterRequiresFreshCountrySpecificEvidence(){Application c=RuntimeEnvironment.getApplication();android.content.SharedPreferences p=StreamingRepository.prefs(c);p.edit().clear().putString(StreamingRepository.COUNTRY,"IE").commit();assertFalse(StreamingRepository.knownOn(c,"movie",42,"8"));p.edit().putStringSet("streaming_known:movie:42:IE:-1",java.util.Collections.singleton("8")).putLong("streaming_known_at:movie:42:IE:-1",System.currentTimeMillis()).commit();assertTrue(StreamingRepository.knownOn(c,"movie",42,"8"));assertFalse(StreamingRepository.knownOn(c,"tv",42,"8"));p.edit().putString(StreamingRepository.COUNTRY,"GB").commit();assertFalse(StreamingRepository.knownOn(c,"movie",42,"8"));}
 @Test public void emptySummaryHasStableLocalNetworkHierarchy(){String summary=PreviewLibrarySummary.describe(RuntimeEnvironment.getApplication(),new PreviewLibraryLoader.Snapshot(),false);assertTrue(summary.startsWith("0 Movies\nTotal Size"));assertTrue(summary.contains("Local Storage    0 movies"));assertTrue(summary.contains("Network / WebDAV    0 movies"));}
}
