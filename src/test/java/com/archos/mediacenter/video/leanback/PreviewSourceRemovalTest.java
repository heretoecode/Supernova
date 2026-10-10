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
            PreviewPagesTest.layout(workspace);
            View sourceRow=workspace.findViewWithTag("semantic:network.item."+(bookmark?"saved.":"source.")+source.getId());assertNotNull(sourceRow);sourceRow.requestFocus();
            var browser=workspace.browser();
            if(bookmark){assertFalse(browser.selections().changed());PreviewPagesTest.findText(browser,"Remove Saved Location").performClick();android.app.Dialog confirm=ShadowDialog.getLatestDialog();View label=confirm.getWindow().getDecorView().findViewWithTag("preview-label:1");((View)label.getParent()).performClick();android.app.Dialog error=ShadowDialog.getLatestDialog();assertNotSame(confirm,error);assertTrue(error.isShowing());assertFalse(browser.selections().changed());error.dismiss();}
            else {
                PreviewPagesTest.findText(browser,"Remove from Library").performClick();assertTrue(browser.selections().changed());
                PreviewPagesTest.findText(browser,"Apply & Scan").performClick();android.app.Dialog error=ShadowDialog.getLatestDialog();assertTrue(error.isShowing());
                assertTrue("An unconfirmed commit remains editable",browser.selections().changed());error.dismiss();
            }
            assertEquals(1,rows.size());assertSame(source,rows.get(0));assertNotNull(workspace.findViewWithTag("semantic:network.item."+(bookmark?"saved.":"source.")+source.getId()));browser.close();

        }finally{host.pause().stop().destroy();}
    }
}
