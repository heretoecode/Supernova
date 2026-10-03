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
    @Test @Config(qualifiers="w960dp-h540dp-land-mdpi") public void actualPageUpTraversesCategoriesBeforeReturningToGlobalNavigation(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});
            TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);
            host.get().setContentView(nav);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setTab(3);nav.selectTab(3);PreviewPagesTest.layout(nav);
            View overview=nav.findViewWithTag("semantic:network.category.overview"),local=nav.findViewWithTag("semantic:network.category.local_storage"),network=nav.findViewWithTag("semantic:network.category.network_shares");
            assertNotNull(overview);assertTrue(network.requestFocus());assertFalse(pages.atTop());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_UP));assertSame(local,nav.findFocus());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_UP));assertSame(overview,nav.findFocus());assertTrue(pages.atTop());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_RIGHT));assertFalse(pages.atTop());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_LEFT));assertSame(overview,nav.findFocus());
            nav.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_UP));assertSame(nav.findViewWithTag("semantic:topnav.network"),nav.findFocus());
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
            LinearLayout items=ReflectionHelpers.getField(workspace,"items"),context=ReflectionHelpers.getField(workspace,"context");
            assertEquals("semantic:network.item.scan_library",identity(items.getChildAt(0)));
            assertEquals("semantic:network.action.scan_library",identity(context.getChildAt(2)));
            assertTrue(items.getChildAt(1).getTag() instanceof Runnable);
            ((Runnable)items.getChildAt(1).getTag()).run();
            assertEquals("semantic:network.action.automatic",identity(context.getChildAt(1)));
            assertEquals("semantic:network.action.frequency",identity(context.getChildAt(2)));
            assertEquals("semantic:network.action.on_return",identity(context.getChildAt(3)));
            var show=PreviewNetworkWorkspace.class.getDeclaredMethod("show",String.class);show.setAccessible(true);
            show.invoke(workspace,"Network Shares");
            assertEquals("semantic:network.item.source.72",identity(items.getChildAt(0)));
            assertEquals("semantic:network.action.browse",identity(context.getChildAt(2)));
            assertEquals("semantic:network.action.scan_source",identity(context.getChildAt(3)));
            show.invoke(workspace,"Saved Locations");
            assertEquals("semantic:network.item.saved.72",identity(items.getChildAt(0)));
            assertEquals("semantic:network.action.add_library",identity(context.getChildAt(3)));
            show.invoke(workspace,"Local Storage");
            assertEquals("semantic:network.item.volume.usb.0",identity(items.getChildAt(0)));
            assertEquals("semantic:network.action.browse",identity(context.getChildAt(2)));
        } finally {host.pause().stop().destroy();}
    }
    private static Object identity(View view){return view.getTag(R.id.preview_diagnostic_semantic);}
}
