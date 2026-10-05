package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSourceRemovalTest {
    @Test public void unconfirmedIndexedSourceRemovalDoesNotHideTheRow()throws Exception{check(false);}
    @Test public void unconfirmedBookmarkRemovalDoesNotHideTheRow()throws Exception{check(true);}
    private void check(boolean bookmark)throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            // This displayed stale row deliberately has no corresponding database
            // record. The real database deletion returns zero, not success.
            Shortcut source=new Shortcut(987654,"https://example.invalid/absent",null,"Fixture"){
                public Uri getUri(){return Uri.parse(getFullPath());}
                public int getImage(){return 0;}
            };
            java.util.List<Shortcut> rows=new java.util.ArrayList<>();rows.add(source);
            PreviewNetworkWorkspace workspace=new PreviewNetworkWorkspace(host.get(),java.util.Collections.emptyList(),
                bookmark?new java.util.ArrayList<>():rows,bookmark?rows:new java.util.ArrayList<>(),box->{},kind->{});
            host.get().setContentView(workspace);
            var show=PreviewNetworkWorkspace.class.getDeclaredMethod("show",String.class);show.setAccessible(true);show.invoke(workspace,bookmark?"Saved Locations":"Network Shares");
            LinearLayout context=ReflectionHelpers.getField(workspace,"context"),items=ReflectionHelpers.getField(workspace,"items");
            context.getChildAt(4).performClick();
            android.app.Dialog confirm=ShadowDialog.getLatestDialog();
            View label=confirm.getWindow().getDecorView().findViewWithTag("preview-label:1");assertNotNull(label);((View)label.getParent()).performClick();
            android.app.Dialog error=ShadowDialog.getLatestDialog();assertNotSame(confirm,error);assertTrue(error.isShowing());
            assertEquals(1,rows.size());assertSame(source,rows.get(0));
            assertEquals("semantic:network.item."+(bookmark?"saved.":"source.")+source.getId(),items.getChildAt(0).getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic));
            error.dismiss();
        }finally{host.pause().stop().destroy();}
    }
}
