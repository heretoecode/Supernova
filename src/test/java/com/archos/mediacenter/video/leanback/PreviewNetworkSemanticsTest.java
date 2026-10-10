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
    private static Object identity(View view){return view.getTag(R.id.preview_diagnostic_semantic);}
}
