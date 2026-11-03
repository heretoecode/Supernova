package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class PreviewSourceRemovalTest {
    @Before
    public void isolatePreviewTransport() {
        PreviewAsyncFixtures.offlineTransport();
    }

    @After
    public void drainPreviewWorkers() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    @Before
    public void configuredLibraryFixture() {
        PreferenceManager
            .getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
    }

    @Test
    public void unconfirmedIndexedSourceRemovalDoesNotHideTheRow() throws Exception {
        check(false);
    }

    @Test
    public void unconfirmedBookmarkRemovalDoesNotHideTheRow() throws Exception {
        check(true);
    }

    private void check(boolean bookmark) throws Exception {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            // This displayed stale row deliberately has no corresponding database
            // record. The real database deletion returns zero, not success.
            Shortcut source = new Shortcut(987654, "https://example.invalid/absent", null, "Fixture") {
                @Override
                public Uri getUri() {
                    return Uri.parse(getFullPath());
                }
                @Override
                public int getImage() {
                    return 0;
                }
            };
            List<Shortcut> rows = new ArrayList<>();
            rows.add(source);
            PreviewNetworkWorkspace workspace = new PreviewNetworkWorkspace(host.get(),
                Collections.emptyList(), bookmark ? new ArrayList<>() : rows,
                bookmark ? rows : new ArrayList<>(), box -> {
                }, kind -> {
                });
            host.get().setContentView(workspace);
            PreviewPagesTest.layout(workspace);
            workspace.findViewWithTag("network.card.1").requestFocus();
            workspace.findViewWithTag("network.card.1").performClick();
            View sourceRow = workspace.findViewWithTag("semantic:network.item.source." + source.getId());
            assertNotNull(sourceRow);
            sourceRow.requestFocus();
            sourceRow.performClick();
            var browser = workspace.browser();
            if (bookmark) {
                assertFalse(browser.selections().changed());
                PreviewPagesTest.findText(browser, "Remove Saved Location").performClick();
                Dialog confirm = ShadowDialog.getLatestDialog();
                View label = confirm.getWindow().getDecorView().findViewWithTag("preview-label:1");
                ((View) label.getParent()).performClick();
                Dialog error = ShadowDialog.getLatestDialog();
                assertNotSame(confirm, error);
                assertTrue(error.isShowing());
                assertFalse(browser.selections().changed());
                error.dismiss();
            } else {
                PreviewPagesTest.findText(browser, "Remove from Library").performClick();
                assertTrue(browser.selections().changed());
                PreviewPagesTest.findText(browser, "Save Changes & Scan").performClick();
                Dialog error = ShadowDialog.getLatestDialog();
                assertTrue(error.isShowing());
                assertTrue("An unconfirmed commit remains editable", browser.selections().changed());
                error.dismiss();
            }
            assertEquals(1, rows.size());
            assertSame(source, rows.get(0));
            assertEquals(source.getUri(), browser.location());
            ReflectionHelpers.callInstanceMethod(browser, "networkShares");
            PreviewPagesTest.layout(workspace);
            assertNotNull(workspace.findViewWithTag("semantic:network.item.source." + source.getId()));
            browser.close();

        } finally {
            host.pause().stop().destroy();
        }
    }
}
