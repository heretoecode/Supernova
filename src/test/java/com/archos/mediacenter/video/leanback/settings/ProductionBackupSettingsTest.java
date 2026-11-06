package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Looper;
import android.view.*;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import com.archos.environment.ArchosUtils;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.*;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/** Checks backup Settings recreation preserves layout, private reviews and temporary category selections. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class ProductionBackupSettingsTest {
    @Before
    public void setup() {
        // AndroidX caches its first process Application; Robolectric creates another between cases.
        // Keep real ViewModel retention within each recreation, without borrowing a previous sandbox's store.
        ReflectionHelpers.setStaticField(
            ViewModelProvider.AndroidViewModelFactory.class, "_instance", null);
        PreviewAsyncFixtures.offlineTransport();
        ReflectionHelpers.setStaticField(CustomApplication.class, "systemLocale", Locale.getDefault());
        ArchosUtils.setGlobalContext(RuntimeEnvironment.getApplication());
        ReflectionHelpers.setStaticField(
            CustomApplication.class, "log", LoggerFactory.getLogger(CustomApplication.class));
        ReflectionHelpers.setStaticField(CustomApplication.class, "mContext", RuntimeEnvironment.getApplication());
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .clear()
            .putBoolean("try_new_ui", true)
            .commit();
        try {
            Picasso.get();
        } catch (IllegalStateException missing) {
            Picasso.setSingletonInstance(
                new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
    }

    private void layout(View root) {
        var config = root.getResources().getConfiguration();
        float density = root.getResources().getDisplayMetrics().density;
        int w = Math.round(config.screenWidthDp * density), h = Math.round(config.screenHeightDp * density);
        root.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, w, h);
    }

    private View find(View root, String label) {
        if (root instanceof TextView && label.contentEquals(((TextView) root).getText()))
            return root;
        if (root instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                View found = find(((ViewGroup) root).getChildAt(i), label);
                if (found != null)
                    return found;
            }
        return null;
    }

    @After
    public void drain() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    private VideoSettingsFragment.PrefsFragment install(TopNavigationTest.Host host) {
        var fragment = new VideoSettingsFragment.PrefsFragment();
        host.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commitNow();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        layout(fragment.requireView());
        assertTrue(PreviewSettings.enterBackup(fragment));
        layout(fragment.requireView());
        return fragment;
    }

    private BackupSettingsController controller(VideoSettingsFragment.PrefsFragment fragment) {
        return ReflectionHelpers.getField(fragment, "backupController");
    }

    private View operation(VideoSettingsFragment.PrefsFragment fragment) {
        View frame = ReflectionHelpers.getField(controller(fragment), "frame");
        int w = TvLayout.safeWidth(frame.getContext()),
            h = TvLayout.px(frame.getContext(), TvLayout.height(frame.getContext()) - 32);
        frame.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
        frame.layout(0, 0, w, h);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        return frame;
    }

    private void export(VideoSettingsFragment.PrefsFragment fragment) {
        fragment.requireView().findViewWithTag("backup.action.export").performClick();
        operation(fragment);
    }

    private int railWidth(View root) {
        View rail = root.findViewWithTag("settings.section.Backup & restore");
        return ((View) rail.getParent()).getWidth();
    }

    private void capture(View root, String name) throws Exception {
        if (root.getWidth() != 960)
            name += "-" + root.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(
            root.getWidth(), root.getHeight(), Bitmap.Config.ARGB_8888);
        root.getViewTreeObserver().dispatchOnPreDraw();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        root.draw(new Canvas(bitmap));
        File folder = new File("build/reports/preview-ui");
        folder.mkdirs();
        try (var output = new FileOutputStream(new File(folder, name + ".png"))) {
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output));
        }
    }

    private void assertBackupOpenerVisible(View root) {
        View child = root.findViewWithTag("settings.section.Backup & restore");
        assertNotNull(child);
        Rect visible = new Rect();
        ViewParent parent = child.getParent();
        while (parent instanceof View && !(parent instanceof ScrollView))
            parent = parent.getParent();
        ScrollView scroll = (ScrollView) parent;
        Rect target = new Rect(0, 0, child.getWidth(), child.getHeight());
        ((ViewGroup) scroll.getChildAt(0)).offsetDescendantRectToMyCoords(child, target);
        String metrics = "visible=" + visible + ", target=" + target + ", scrollY=" + scroll.getScrollY()
            + ", scrollHeight=" + scroll.getHeight() + ", padding=" + scroll.getPaddingTop() + "/"
            + scroll.getPaddingBottom() + ", contentHeight=" + scroll.getChildAt(0).getHeight() + ", childTop="
            + child.getTop() + ", translationY=" + child.getTranslationY() + ", attached=" + child.isAttachedToWindow();
        assertTrue(metrics, child.getGlobalVisibleRect(visible));
        assertEquals(metrics + ", finalVisible=" + visible + " of height " + child.getHeight(), child.getHeight(),
            visible.height());
    }

    private void await(BackupOperationModel model, BackupOperationModel.Phase expected) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
        while (model.isBusy()) {
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            if (System.nanoTime() > deadline)
                throw new AssertionError("Backup worker did not finish");
            Thread.sleep(10);
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(model.current().message, expected, model.current().phase);
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land-mdpi")
    public void compactRecreationKeepsFlatDetailsAndSidebar() throws Throwable {
        recreatedSettingsKeepsFlatDetailsAndMeasuredPermanentSidebar();
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land-mdpi")
    public void compactRecreationKeepsRestoreSelectionAndPrivacy() throws Throwable {
        recreationKeepsPrivateReviewAndTemporaryCategoryOptOutUntilExplicitConfirmation();
    }

    @Test
    public void recreatedSettingsKeepsFlatDetailsAndMeasuredPermanentSidebar() throws Throwable {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            var fragment = install(host.get());
            View root = fragment.requireView();
            int rail = railWidth(root);
            assertTrue(rail > 0);
            root.findViewWithTag("backup.details").performClick();
            assertNotNull(operation(fragment).findViewWithTag("backup.details.page"));
            host.recreate();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            fragment = (VideoSettingsFragment.PrefsFragment) host.get().getSupportFragmentManager().findFragmentById(
                android.R.id.content);
            root = fragment.requireView();
            layout(root);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            layout(root);
            assertEquals(rail, railWidth(root));
            assertNotNull(operation(fragment).findViewWithTag("backup.details.page"));
            assertTrue(controller(fragment).back());
            layout(root);
            assertTrue(root.findViewWithTag("backup.details").hasFocus());
            assertTrue(controller(fragment).back());
            assertTrue(root.findViewWithTag("settings.section.Backup & restore").hasFocus());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void recreationKeepsPrivateReviewAndTemporaryCategoryOptOutUntilExplicitConfirmation() throws Throwable {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        Throwable failure = null;
        try {
            var fragment = install(host.get());
            BackupOperationModel model =
                new ViewModelProvider(host.get()).get(BackupOperationModel.class);
            assertSame("Fixture model must belong to the current process Application before recreation",
                host.get().getApplication(), model.getApplication());
            var prefs = PreferenceManager.getDefaultSharedPreferences(host.get());
            prefs.edit().putInt("preview_accent41", 7).putString("oauth_token", "AUTH_CANARY").commit();
            File source = new File(host.get().getCacheDir(), "review-source.zip");
            JSONObject appearance =
                                    new JSONObject()
                                        .put("schema", 1)
                                        .put("category", "APPEARANCE")
                                        .put("values",
                                            new JSONObject().put("preview_accent41",
                                                new JSONObject().put("type", "INT").put("value", 19))),
                                audio = new JSONObject()
                                            .put("schema", 1)
                                            .put("category", "AUDIO")
                                            .put("values", new JSONObject());
            LogicalBackupArchive.writeCategories(
                source, Map.of(BackupCategory.APPEARANCE, appearance, BackupCategory.AUDIO, audio), () -> false);
            model.open(new FileInputStream(source));
            await(model, BackupOperationModel.Phase.REVIEW);
            layout(fragment.requireView());
            var before = prefs.getAll();
            operation(fragment).findViewWithTag("backup.category.AUDIO").performClick();
            assertEquals(before, prefs.getAll());
            host.recreate();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            fragment = (VideoSettingsFragment.PrefsFragment) host.get().getSupportFragmentManager().findFragmentById(
                android.R.id.content);
            layout(fragment.requireView());
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            layout(fragment.requireView());
            assertSame(model, new ViewModelProvider(host.get()).get(BackupOperationModel.class));
            BackupSettingsController controller = ReflectionHelpers.getField(fragment, "backupController");
            BackupWorkspace workspace = ReflectionHelpers.getField(controller, "workspace");
            assertEquals(Set.of(BackupCategory.APPEARANCE), workspace.selection());
            assertEquals(7, prefs.getInt("preview_accent41", 0));
            operation(fragment).findViewWithTag("backup.create").performClick();
            await(model, BackupOperationModel.Phase.RESTORE_PREVIEW);
            assertEquals(Set.of(BackupCategory.APPEARANCE), model.current().categories);
            assertEquals(7, prefs.getInt("preview_accent41", 0));
            operation(fragment).findViewWithTag("backup.operation:Restore selected categories").performClick();
            await(model, BackupOperationModel.Phase.RESTORED);
            assertSame(host.get().getApplication(), model.getApplication());
            assertEquals(19, prefs.getInt("preview_accent41", 0));
            assertEquals("AUTH_CANARY", prefs.getString("oauth_token", null));
            assertTrue(source.exists());
        } catch (Throwable problem) {
            failure = problem;
            throw problem;
        } finally {
            try {
                host.pause().stop().destroy();
            } catch (Throwable cleanup) {
                if (failure != null)
                    failure.addSuppressed(cleanup);
                else
                    throw cleanup;
            }
        }
    }

    @Test
    public void actualPreferencesFragmentBuildsFullSidebarAndAllBackupCategoriesAndFlatPages() throws Throwable {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            // The parts list shows only for Custom; a fresh install starts on Everything.
            com.archos.mediacenter.video.utils.backup.BackupPresets.select(
                PreferenceManager.getDefaultSharedPreferences(host.get()),
                com.archos.mediacenter.video.utils.backup.BackupSelection.Preset.CUSTOM);
            var fragment = install(host.get());
            View root = fragment.requireView();
            assertNotNull(root.findViewWithTag("settings.section.Backup & restore"));
            for (BackupCategory category : BackupCategory.values()) {
                View option = root.findViewWithTag("backup.category." + category.name());
                assertNotNull(category.name(), option);
                option.requestFocus();
                layout(root);
                Rect visible = new Rect();
                assertTrue(category.name(), option.getGlobalVisibleRect(visible));
                assertEquals(category.name(), option.getHeight(), visible.height());
            }
            View details = root.findViewWithTag("backup.details");
            details.requestFocus();
            details.performClick();
            assertNotNull(operation(fragment).findViewWithTag("backup.details.page"));
            assertTrue(controller(fragment).back());
            layout(root);
            assertTrue(details.hasFocus());
            assertTrue(controller(fragment).back());
            assertTrue(root.findViewWithTag("settings.section.Backup & restore").hasFocus());
        } finally {
            host.pause().stop().destroy();
        }
    }
}
