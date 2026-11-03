package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;
import com.archos.environment.ArchosUtils;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.foundation.FoundationAboutWorkspace;
import com.archos.mediacenter.video.leanback.PreviewAsyncFixtures;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/** Actual production preference hierarchy and root D-pad dispatch, including reverse entry. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class SettingsShieldNavigationTest {
    private ActivityController<TopNavigationTest.Host> host;
    private VideoSettingsFragment.PrefsFragment settings;
    private View root;

    @Before
    public void install() {
        ReflectionHelpers.setStaticField(
            ViewModelProvider.AndroidViewModelFactory.class, "_instance", null);
        PreviewAsyncFixtures.offlineTransport();
        Application app = RuntimeEnvironment.getApplication();
        ReflectionHelpers.setStaticField(CustomApplication.class, "systemLocale", Locale.getDefault());
        ReflectionHelpers.setStaticField(CustomApplication.class, "mContext", app);
        ReflectionHelpers.setStaticField(
            CustomApplication.class, "log", LoggerFactory.getLogger(CustomApplication.class));
        ArchosUtils.setGlobalContext(app);
        PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putBoolean("try_new_ui", true).commit();
        try {
            Picasso.get();
        } catch (IllegalStateException absent) {
            Picasso.setSingletonInstance(new Picasso.Builder(app).build());
        }
        host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        settings = new VideoSettingsFragment.PrefsFragment();
        host.get().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, settings).commitNow();
        root = settings.requireView();
        settle();
    }

    @After
    public void close() throws Exception {
        if (host != null)
            host.pause().stop().destroy();
        PreviewAsyncFixtures.drain();
    }

    private void settle() {
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        int width = root.getResources().getConfiguration().screenWidthDp;
        int height = root.getResources().getConfiguration().screenHeightDp;
        root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, width, height);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(250));
    }

    private View semantic(String value) {
        View found = root.findViewWithTag("semantic:settings:" + value);
        assertNotNull(value, found);
        return found;
    }

    private void key(int code) {
        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, code));
        settle();
    }

    private void openDiagnostics() {
        settings.workspace.open(6, "Diagnostics");
        settle();
    }

    private <T> T findType(View view, Class<T> type) {
        if (type.isInstance(view))
            return type.cast(view);
        if (view instanceof ViewGroup)
            for (int n = 0; n < ((ViewGroup) view).getChildCount(); n++) {
                T found = findType(((ViewGroup) view).getChildAt(n), type);
                if (found != null)
                    return found;
            }
        return null;
    }

    private void capture(String name) {
        try {
            File output = new File("build/reports/preview-ui/" + name + ".png");
            output.getParentFile().mkdirs();
            Bitmap bitmap = Bitmap.createBitmap(
                root.getWidth(), root.getHeight(), Bitmap.Config.ARGB_8888);
            root.draw(new Canvas(bitmap));
            try (FileOutputStream file = new FileOutputStream(output)) {
                assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, file));
            }
        } catch (IOException failed) {
            throw new AssertionError(failed);
        }
    }

    @Test
    public void aboutReturnsToGridAndDiagnosticsUsesOnlyItsTwoColumns() {
        settings.workspace.open(7, null);
        settle();
        assertNotNull(findType(root, FoundationAboutWorkspace.class));
        assertTrue(settings.workspace.back());
        settle();
        openDiagnostics();
        assertNull(findType(root, FoundationAboutWorkspace.class));
        assertEquals(2, ((ViewGroup) root.findViewWithTag("settings.category")).getChildCount());
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue(String.valueOf(root.findFocus().getTag()).startsWith("settings.preference."));
        capture("0143-diagnostics");
    }

    @Test
    public void diagnosticLoggingLevelAndExportAreReachableByRootKeyDispatch() {
        openDiagnostics();
        View diagnostics = root.findViewWithTag("settings.section.Diagnostics");
        assertTrue(diagnostics.requestFocus());
        settle();
        assertNotNull(
            "Diagnostic Level must retain its established preference key", settings.findPreference(Diagnostics.LEVEL));
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        Set<String> reached = new HashSet<>();
        for (int n = 0; n < 10; n++) {
            View focused = root.findFocus();
            if (focused != null
                && focused.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic) != null)
                reached.add(
                    String.valueOf(focused.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic)));
            key(KeyEvent.KEYCODE_DPAD_DOWN);
        }
        for (String pref : new String[] {
            Diagnostics.KEY, Diagnostics.LEVEL, "export_diagnostic_report"
        }) {
            boolean seen = reached.stream().anyMatch(tag -> tag.contains(pref));
            assertTrue("D-pad must reach " + pref + "; saw " + reached, seen);
        }
    }

    @Test
    public void diagnosticLevelDefaultsToCurrentDetailedBehaviorAndRespondsToChanges() {
        Application app = RuntimeEnvironment.getApplication();
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        try {
            Diagnostics.install(app);
            assertTrue((Boolean) ReflectionHelpers.getStaticField(Diagnostics.class, "qa"));
            ListPreference level = settings.findPreference(Diagnostics.LEVEL);
            assertNotNull(level);
            assertEquals("qa", level.getValue());
            level.setValue("normal");
            settle();
            assertFalse((Boolean) ReflectionHelpers.getStaticField(Diagnostics.class, "qa"));
            level.setValue("qa");
            settle();
            assertTrue((Boolean) ReflectionHelpers.getStaticField(Diagnostics.class, "qa"));
        } finally {
            Diagnostics.setEnabled(app, false);
            Thread.setDefaultUncaughtExceptionHandler(previous);
        }
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land-mdpi")
    public void compactBackupKeepsTwoColumnsWithinThePhysicalViewport() {
        backupLandingKeepsTwoColumnsAndTwoVerticalActions();
        for (String tag : new String[] {
            "backup.action.export", "backup.action.restore", "backup.action.description"
        }) {
            View view = root.findViewWithTag(tag);
            int[] xy = new int[2];
            view.getLocationOnScreen(xy);
            assertTrue(tag, xy[0] >= 0 && xy[0] + view.getWidth() <= root.getWidth());
            assertTrue(tag, view.getWidth() > 0 && view.getHeight() > 0);
        }
    }

    @Test
    public void backupLandingKeepsTwoColumnsAndTwoVerticalActions() {
        assertTrue(PreviewSettings.enterBackup(settings));
        settle();
        ViewGroup split = root.findViewWithTag("settings.category");
        assertEquals(2, split.getChildCount());
        for (int n = 0; n < 2; n++)
            assertEquals(View.VISIBLE, split.getChildAt(n).getVisibility());
        View export = root.findViewWithTag("backup.action.export");
        View restore = root.findViewWithTag("backup.action.restore");
        assertNotNull("Centre Export Backup action", export);
        assertNotNull("Centre Restore Backup action", restore);
        assertSame(export.getParent(), restore.getParent());
        assertTrue(export.getBottom() <= restore.getTop());
        assertNotNull("0.140 exposes export categories directly in the vertical landing list",
            root.findViewWithTag("backup.category.APPEARANCE"));
        assertTrue(export.requestFocus());
        key(KeyEvent.KEYCODE_DPAD_DOWN);
        assertSame(restore, root.findFocus());
        TextView description = root.findViewWithTag("backup.action.description");
        assertTrue(description.getText().toString().contains("Backups are unencrypted"));
        assertTrue(((TextView) restore).getText().toString().contains("Review the available categories"));
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue("Right stays in the content column", restore.hasFocus());
        key(KeyEvent.KEYCODE_DPAD_LEFT);
        assertTrue(root.findViewWithTag("settings.section.Backup & restore").hasFocus());
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertSame("Right restores the selected backup action", restore, root.findFocus());
        restore.performClick();
        settle();
        assertEquals(Intent.ACTION_OPEN_DOCUMENT,
            Shadows.shadowOf(host.get()).getNextStartedActivityForResult().intent.getAction());
        capture("0139-shield-repair-backup-landing");
    }
}
