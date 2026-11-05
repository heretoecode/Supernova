package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediacenter.video.player.PlayerService;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediacenter.video.utils.SettingsBackup;
import com.archos.filecorelibrary.sftp.SftpHostTrust;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.*;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.util.ReflectionHelpers;

/** Checks playback version choices, settings replacement, SFTP trust and artwork fallback behavior. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Preview415Test {
 @Before public void isolatePreviewTransport(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drainPreviewWorkers() throws Exception { PreviewAsyncFixtures.drain(); }
    private Video movie(long id,int width,int resume){return new Movie(id,"/storage/version-"+id+".mkv","Film",1,"Plot",2024,7,"",null,100000,resume,0,0,false,false,false,false,1,resume>0?50:0,width,1080,null,null,null,null,0,1,900000,0);}
    private Entry episode(long id,int season,int number,int width){return new Entry(new Episode(id,7,season,number,"Episode",0,0,"","","Show","/storage/v"+id+".mkv",null,null,100000,0,0,0,false,false,false,false,1,0,width,1080,null,null,null,null,0,1,1000),0,7,"");}
    @Test public void automaticVersionResumeReachesLaunchBoundaryButDoesNotOverrideExplicitChoice(){
        Video old=movie(1,1920,33000),best=movie(2,3840,0);
        Video selected=(Video)PreviewVariants.logicalChoices(Arrays.asList(new Entry(old,0,0,""),new Entry(best,0,0,""))).get(0).media;
        assertSame(best,selected);
        assertEquals(33000,PlayUtils.resolveAutomaticResume(selected,PlayerService.RESUME_FROM_LAST_POS,-1));
        assertEquals(12000,PlayUtils.resolveAutomaticResume(selected,PlayerService.RESUME_FROM_LAST_POS,12000));
        assertEquals(-1,PlayUtils.resolveAutomaticResume(selected,PlayerService.RESUME_NO,-1));
        assertEquals(-1,PlayUtils.resolveAutomaticResume(selected,PlayerService.RESUME_FROM_REMOTE_POS,-1));
        assertEquals(-1,PlayUtils.resolveAutomaticResume(movie(2,3840,0),PlayerService.RESUME_FROM_LAST_POS,-1));
    }
    @Test public void nextEpisodeIgnoresDuplicateEncodesAndChoosesBestNextVersion(){
        List<Entry> files=Arrays.asList(episode(1,1,1,1920),episode(2,1,1,3840),episode(3,1,2,1920),episode(4,1,2,3840),episode(5,2,1,1920));
        assertEquals(4,PreviewVariants.adjacentEpisode(files,1,1).getId());
        assertEquals(4,PreviewVariants.adjacentEpisode(files,2,1).getId());
        assertEquals(5,PreviewVariants.adjacentEpisode(files,4,1).getId());
        assertEquals(2,PreviewVariants.adjacentEpisode(files,4,-1).getId());
        assertNull(PreviewVariants.adjacentEpisode(files,5,1));
    }
    @Test public void providerSeekTargetsCannotStandInForCompletion(){
        assertTrue(PlayerService.isSafeAutoSkipTarget(10000,100000));
        assertFalse(PlayerService.isSafeAutoSkipTarget(100000,100000));
        assertFalse(PlayerService.isSafeAutoSkipTarget(Long.MAX_VALUE,100000));
        assertFalse(PlayerService.isSafeAutoSkipTarget(10000,-1));
    }
    @Test public void replacingSettingsRemovesStaleKeysAndRejectsInvalidInputBeforeCommit()throws Exception{
        SharedPreferences prefs=RuntimeEnvironment.getApplication().getSharedPreferences("replacement",0);
        prefs.edit().putBoolean("old",true).commit();
        SettingsBackup.decode(prefs,"{\"new\":{\"type\":\"int\",\"value\":7}}").commit();
        assertFalse(prefs.contains("old"));assertEquals(7,prefs.getInt("new",0));
        try{SettingsBackup.decode(prefs,"{\"bad\":{\"type\":\"unknown\"}}");fail();}catch(JSONException expected){}
        assertEquals(7,prefs.getInt("new",0));
    }
    @Test public void sftpTrustInitialisesBeforeApplicationContextExists(){
        Context early=new ContextWrapper(RuntimeEnvironment.getApplication()){
            @Override public Context getApplicationContext(){return null;}
        };
        SftpHostTrust.initialise(early);
        assertTrue(SftpHostTrust.verify("early-start",22,new byte[]{4,1,5}));
    }
    @Test public void sftpIdentityPersistsAndRejectsChangedKeyAcrossInitialisation(){
        Context context=RuntimeEnvironment.getApplication();SftpHostTrust.initialise(context);
        assertTrue(SftpHostTrust.verify("NAS",22,new byte[]{1,2,3}));
        SftpHostTrust.initialise(context);
        assertTrue(SftpHostTrust.verify("nas",22,new byte[]{1,2,3}));
        assertFalse(SftpHostTrust.verify("nas",22,new byte[]{9,8,7}));
        assertTrue(SftpHostTrust.verify("nas",2222,new byte[]{9,8,7}));
        assertTrue(SftpHostTrust.forget("nas:22"));
        assertTrue(SftpHostTrust.verify("nas",22,new byte[]{9,8,7}));
    }
    @Test public void sourceInclusionUsesIdentityDespiteDuplicateNames(){
        Context context=RuntimeEnvironment.getApplication();
        ShortcutDbAdapter adapter=ShortcutDbAdapter.VIDEO;
        ShortcutDbAdapter.Shortcut first=new ShortcutDbAdapter.Shortcut("Films","smb://one/films");
        ShortcutDbAdapter.Shortcut second=new ShortcutDbAdapter.Shortcut("Films","smb://two/films");
        assertTrue(adapter.addShortcut(context,first));assertTrue(adapter.addShortcut(context,second));
        long selected=adapter.isShortcut(context,"smb://one/films");
        adapter.setRescanShortcut(context,true,"Films");adapter.setRescanShortcutById(context,false,selected);
        try(Cursor cursor=adapter.getAllShortcuts(context,null,null)){
            assertEquals(2,cursor.getCount());while(cursor.moveToNext())assertEquals(cursor.getLong(0)==selected?0:1,cursor.getInt(5));
        }
    }
    @Test public void detailsWithoutAnAttachedLegacyImageNeedNoTransition(){
        Activity activity=Robolectric.buildActivity(Activity.class).setup().get();
        assertNull(ListingFragment.detailsTransition(activity,null));
        assertNull(ListingFragment.detailsTransition(activity,new View(activity)));
        activity.finish();
    }
    @Test public void modernLibraryPreferencesReachFilterAndTitleSort(){
        Context context=RuntimeEnvironment.getApplication();SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        Entry entry=new Entry(movie(9,1920,PlayerActivity.LAST_POSITION_END),0,0,"");entry.sortTitle="Film, The";
        prefs.edit().putBoolean("sort_ignore_articles",true).putBoolean("hide_watched",true).commit();
        assertEquals("Film, The",PreviewPages.titleForSort(context,entry));assertTrue(PreviewPages.hiddenByWatchedPreference(context,entry));
        prefs.edit().putBoolean("sort_ignore_articles",false).putBoolean("hide_watched",false).commit();
        assertEquals("Film",PreviewPages.titleForSort(context,entry));assertFalse(PreviewPages.hiddenByWatchedPreference(context,entry));
    }
    @Test public void backdropFailureRetainsDisplayedArtworkWithoutStaleCrossfade(){
        PreviewBackdrop backdrop=new PreviewBackdrop(RuntimeEnvironment.getApplication());
        Bitmap image=Bitmap.createBitmap(16,9,Bitmap.Config.ARGB_8888);
        backdrop.onBitmapLoaded(image,Picasso.LoadedFrom.MEMORY);
        backdrop.onBitmapFailed(new IOException(),null);
        assertSame(image,ReflectionHelpers.getField(backdrop,"bitmap"));
        assertNull(ReflectionHelpers.getField(backdrop,"previous"));
    }
}
