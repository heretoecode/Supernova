package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(org.robolectric.RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewNetworkSemanticsTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
 @org.junit.Before public void configuredLibraryFixture(){androidx.preference.PreferenceManager.getDefaultSharedPreferences(org.robolectric.RuntimeEnvironment.getApplication()).edit().putBoolean("supernova_onboarding_complete",true).commit();}

    @Test @Config(qualifiers="w960dp-h540dp-land-mdpi") public void actualPageUpTraversesCategoriesBeforeReturningToGlobalNavigation(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});
            // This fixture supplies an empty source inventory; asynchronous database discovery is a separate scan test.
            ReflectionHelpers.setField(pages,"sourcesLoading",true);
            TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);
            host.get().setContentView(nav);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setTab(3);nav.selectTab(3);PreviewPagesTest.layout(nav);
            View overview=nav.findViewWithTag("semantic:network.category.overview"),local=nav.findViewWithTag("semantic:network.category.local_storage"),network=nav.findViewWithTag("semantic:network.category.network_shares");
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();PreviewPagesTest.layout(nav);
            overview=nav.findViewWithTag("semantic:network.category.overview");local=nav.findViewWithTag("semantic:network.category.local_storage");network=nav.findViewWithTag("semantic:network.category.network_shares");
            assertSame("Entering Network must focus Overview without entering its context",overview,nav.findFocus());
            assertNotNull(overview);assertTrue(network.requestFocus());assertFalse(pages.atTop());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_UP));assertSame(local,nav.findFocus());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_UP));assertSame(overview,nav.findFocus());assertTrue(pages.atTop());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_RIGHT));assertFalse(pages.atTop());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_LEFT));assertSame(overview,nav.findFocus());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_UP));assertSame(nav.findViewWithTag("semantic:topnav.network"),nav.findFocus());
            pages.setTab(3);assertSame("Reselecting Network must enter Overview",overview,nav.findFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void actualWorkspaceKeepsContextActionsAndPrivateLocationsSemanticallyDistinct()throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            Shortcut source=new Shortcut(72,"https://private-host/private-path",null,"Private source") {
                public Uri getUri(){return Uri.parse(getFullPath());}
                public int getImage(){return 0;}
            };
            PreviewNetworkWorkspace workspace=new PreviewNetworkWorkspace(host.get(),
                    List.of(new Box(Box.ID.USB,"Private volume",0,"/private-volume")),
                    List.of(source),List.of(source),box->{},kind->{});
            host.get().setContentView(workspace);
            var browser=workspace.browser();
            workspace.findViewWithTag("semantic:network.category.network_shares").requestFocus();
            View sourceRow=workspace.findViewWithTag("semantic:network.item.source.72");assertNotNull(sourceRow);
            PreviewPagesTest.layout(workspace);sourceRow.requestFocus();
            LinearLayout actions=ReflectionHelpers.getField(browser,"actions");
            assertNotNull(PreviewPagesTest.findText(actions,"Remove from Library"));
            PreviewPagesTest.findText(actions,"Remove from Library").performClick();
            assertTrue(browser.selections().changed());assertFalse(browser.selections().included(source.getUri()));
            assertNotNull("A library selection never hides a browsing source",workspace.findViewWithTag("semantic:network.item.source.72"));
            assertNotNull(workspace.findViewWithTag("semantic:network.item.saved.72"));
            for(int i=0;i<actions.getChildCount();i++)assertFalse(String.valueOf(identity(actions.getChildAt(i))).contains("private"));

        } finally {host.pause().stop().destroy();}
    }
    @Test public void authenticationRecoveryKeepsTheSharedInlineFormAndOriginalFolder(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{var browser=new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());host.get().setContentView(browser);
            ReflectionHelpers.callInstanceMethod(browser,"launchCredentials",ReflectionHelpers.ClassParameter.from(Uri.class,Uri.parse("smb://NAS:445/Shows/Season%201")));
            assertNull(org.robolectric.Shadows.shadowOf(host.get()).getNextStartedActivity());
            String[] fields=ReflectionHelpers.getField(browser,"connection");assertEquals("NAS",fields[0]);assertEquals("445",fields[1]);assertEquals("/Shows/Season 1",fields[2]);assertEquals("",fields[4]);
            assertNotNull(PreviewPagesTest.findText(browser,"Password: "));assertNotNull(PreviewPagesTest.findText(browser,"Show Password: Off"));
        }finally{host.pause().stop().destroy();}
    }
    @Test public void onboardingCanSelectAndBrowseNestedFoldersWhileStagingAcrossDrives()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{var browser=new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());browser.onboarding(true);host.get().setContentView(browser);
            java.io.File root=new java.io.File(host.get().getFilesDir(),"drive-one"),child=new java.io.File(root,"Movies");assertTrue(child.mkdirs());Uri rootUri=Uri.fromFile(root),childUri=Uri.fromFile(child);
            ReflectionHelpers.setField(browser,"current",rootUri);var folder=org.mockito.Mockito.mock(com.archos.filecorelibrary.MetaFile2.class);org.mockito.Mockito.when(folder.isDirectory()).thenReturn(true);org.mockito.Mockito.when(folder.getUri()).thenReturn(childUri);org.mockito.Mockito.when(folder.getName()).thenReturn("Movies");
            java.util.List<com.archos.filecorelibrary.MetaFile2> files=ReflectionHelpers.getField(browser,"files");files.add(folder);ReflectionHelpers.callInstanceMethod(browser,"renderItems");PreviewPagesTest.layout(browser);
            View row=browser.findViewWithTag(childUri.toString());assertNotNull(row);row.performClick();assertTrue(browser.selections().included(childUri));View browse=PreviewPagesTest.findText(browser,"Browse Folder");assertNotNull(browse);browse.performClick();assertEquals(childUri,ReflectionHelpers.getField(browser,"current"));
            Uri other=Uri.fromFile(new java.io.File(host.get().getFilesDir(),"drive-two"));browser.open(other);Uri second=other.buildUpon().appendPath("TV Shows").build();browser.selections().include(second);assertTrue(browser.selections().included(second));assertEquals(2,browser.selections().roots.size());assertTrue(browser.selections().included(childUri));assertTrue(browser.selections().changed());assertFalse(androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).getStringSet("supernova_library_roots",Set.of()).contains(childUri.toString()));
            browser.selections().discard();assertFalse(browser.selections().included(childUri));assertFalse(browser.selections().included(second));
        }finally{host.pause().stop().destroy();}
    }
    @Test public void unconfirmedHomeCannotBeReplacedByUnexpectedIndexedMedia(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("supernova_onboarding_complete",false).commit();PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});ReflectionHelpers.setField(pages,"sourcesLoading",true);host.get().setContentView(pages);
            var snapshot=new PreviewLibraryLoader.Snapshot();var entry=new PreviewPagesTest().episode(1,0,false,0,1);entry.heroEligible=true;entry.backdrop=Uri.parse("file:///fixture/artwork");snapshot.featured.add(entry);snapshot.episodes.add(entry);snapshot.technical.add(entry);pages.setSnapshot(snapshot);PreviewPagesTest.layout(pages);
            assertNotNull(PreviewPagesTest.findText(pages,"Build Your Library"));assertNull(pages.findViewWithTag("semantic:featured.active"));assertNull(pages.findViewWithTag("semantic:network.category.network_shares"));assertNull("No network scan action during first setup",PreviewPagesTest.findText(pages,"Network Scanning"));assertFalse("Overview is retained for normal browsing but hidden during onboarding",pages.findViewWithTag("semantic:network.category.overview").isShown());
        }finally{host.pause().stop().destroy();}
    }
    private static Object identity(View view){return view.getTag(R.id.preview_diagnostic_semantic);}
}
