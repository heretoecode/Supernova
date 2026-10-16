package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.foundation.FoundationAboutWorkspace;
import com.archos.mediacenter.video.leanback.PreviewAsyncFixtures;
import com.archos.mediacenter.video.leanback.SharedThreePanel;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Actual production preference hierarchy and root D-pad dispatch, including reverse entry. */
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class, sdk=28, qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class SettingsShieldNavigationTest {
    private ActivityController<TopNavigationTest.Host> host;
    private VideoSettingsFragment.PrefsFragment settings;
    private View root;

    @Before public void install() {
        ReflectionHelpers.setStaticField(androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.class,"_instance",null);
        PreviewAsyncFixtures.offlineTransport();
        Application app=RuntimeEnvironment.getApplication();
        ReflectionHelpers.setStaticField(CustomApplication.class,"systemLocale",java.util.Locale.getDefault());
        ReflectionHelpers.setStaticField(CustomApplication.class,"mContext",app);
        ReflectionHelpers.setStaticField(CustomApplication.class,"log",org.slf4j.LoggerFactory.getLogger(CustomApplication.class));
        com.archos.environment.ArchosUtils.setGlobalContext(app);
        PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putBoolean("try_new_ui",true).commit();
        try { com.squareup.picasso.Picasso.get(); }
        catch (IllegalStateException absent) { com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(app).build()); }
        host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        settings=new VideoSettingsFragment.PrefsFragment();
        host.get().getSupportFragmentManager().beginTransaction().replace(android.R.id.content,settings).commitNow();
        root=settings.requireView();
        settle();
    }

    @After public void close() throws Exception {
        if(host!=null)host.pause().stop().destroy();
        PreviewAsyncFixtures.drain();
    }

    private void settle() {
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        int width=root.getResources().getConfiguration().screenWidthDp;
        int height=root.getResources().getConfiguration().screenHeightDp;
        root.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));
        root.layout(0,0,width,height);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(250));
    }

    private View semantic(String value) {
        View found=root.findViewWithTag("semantic:settings:"+value);
        assertNotNull(value,found);
        return found;
    }

    private void key(int code) {
        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));
        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,code));
        settle();
    }

    private void expandAdvanced() {
        View advanced=semantic("category:Advanced");
        assertTrue(advanced.requestFocus());
        advanced.performClick();
        settle();
    }

    private <T> T findType(View view,Class<T> type) {
        if(type.isInstance(view))return type.cast(view);
        if(view instanceof ViewGroup)for(int n=0;n<((ViewGroup)view).getChildCount();n++) {
            T found=findType(((ViewGroup)view).getChildAt(n),type);
            if(found!=null)return found;
        }
        return null;
    }

    private void capture(String name) {
        try {
            java.io.File output=new java.io.File("build/reports/preview-ui/"+name+".png");output.getParentFile().mkdirs();
            android.graphics.Bitmap bitmap=android.graphics.Bitmap.createBitmap(root.getWidth(),root.getHeight(),android.graphics.Bitmap.Config.ARGB_8888);
            root.draw(new android.graphics.Canvas(bitmap));
            try(java.io.FileOutputStream file=new java.io.FileOutputStream(output)){assertTrue(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,file));}
        } catch(java.io.IOException failed){throw new AssertionError(failed);}
    }

    @Test public void upwardEntryIntoDiagnosticsRestoresItsRootAndHidesAboutWorkspace() {
        expandAdvanced();
        View diagnostics=semantic("section:preview_diagnostics_group");
        assertTrue(semantic("category:About").requestFocus());
        settle();
        assertEquals(View.VISIBLE,findType(root,FoundationAboutWorkspace.class).getVisibility());
        key(KeyEvent.KEYCODE_DPAD_UP);
        assertSame("Up from About must enter the last expanded Advanced section",diagnostics,root.findFocus());
        assertTrue("The Advanced ancestor must be visible on reverse entry",settings.findPreference("preferences_category_advanced_video").isVisible());
        assertEquals("About must relinquish its extra workspace",View.GONE,findType(root,FoundationAboutWorkspace.class).getVisibility());
        assertTrue(settings.getListView().getAdapter().getItemCount()>0);
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue("Right must reach a real diagnostic control",settings.getListView().hasFocus());capture("0139-shield-repair-diagnostics-upward");
    }

    @Test public void diagnosticLoggingLevelAndExportAreReachableByRootKeyDispatch() {
        expandAdvanced();
        View diagnostics=semantic("section:preview_diagnostics_group");
        assertTrue(diagnostics.requestFocus());
        settle();
        assertNotNull("Diagnostic Level must retain its established preference key",settings.findPreference(Diagnostics.LEVEL));
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        java.util.Set<String> reached=new java.util.HashSet<>();
        for(int n=0;n<10;n++) {
            View focused=root.findFocus();
            if(focused!=null&&focused.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic)!=null)reached.add(String.valueOf(focused.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic)));
            key(KeyEvent.KEYCODE_DPAD_DOWN);
        }
        for(String pref:new String[]{Diagnostics.KEY,Diagnostics.LEVEL,"export_diagnostic_report"}) {
            boolean seen=reached.stream().anyMatch(tag->tag.contains(pref));
            assertTrue("D-pad must reach "+pref+"; saw "+reached,seen);
        }
    }

    @Test public void diagnosticLevelDefaultsToCurrentDetailedBehaviorAndRespondsToChanges() {
        Application app=RuntimeEnvironment.getApplication();
        Thread.UncaughtExceptionHandler previous=Thread.getDefaultUncaughtExceptionHandler();
        try {
            Diagnostics.install(app);
            assertTrue((Boolean)ReflectionHelpers.getStaticField(Diagnostics.class,"qa"));
            androidx.preference.ListPreference level=settings.findPreference(Diagnostics.LEVEL);
            assertNotNull(level);
            assertEquals("qa",level.getValue());
            level.setValue("normal");settle();
            assertFalse((Boolean)ReflectionHelpers.getStaticField(Diagnostics.class,"qa"));
            level.setValue("qa");settle();
            assertTrue((Boolean)ReflectionHelpers.getStaticField(Diagnostics.class,"qa"));
        } finally {Diagnostics.setEnabled(app,false);Thread.setDefaultUncaughtExceptionHandler(previous);}
    }

    @Test @Config(qualifiers="w640dp-h360dp-land-mdpi") public void compactBackupKeepsAllThreePanelsWithinThePhysicalViewport() {
        backupLandingKeepsThreePanelsAndTwoVerticalActions();
        for(String tag:new String[]{"backup.action.export","backup.action.restore","backup.action.description"}) {
            View view=root.findViewWithTag(tag);int[] xy=new int[2];view.getLocationOnScreen(xy);
            assertTrue(tag,xy[0]>=0&&xy[0]+view.getWidth()<=root.getWidth());
            assertTrue(tag,view.getWidth()>0&&view.getHeight()>0);
        }
    }

    @Test public void backupLandingKeepsThreePanelsAndTwoVerticalActions() {
        assertTrue(PreviewSettings.enterBackup(settings));
        settle();
        SharedThreePanel split=findType(root,SharedThreePanel.class);
        for(int n=0;n<3;n++)assertEquals("Backup landing panel "+n,View.VISIBLE,split.getChildAt(n).getVisibility());
        View export=root.findViewWithTag("backup.action.export");
        View restore=root.findViewWithTag("backup.action.restore");
        assertNotNull("Centre Export Backup action",export);
        assertNotNull("Centre Restore Backup action",restore);
        assertSame(export.getParent(),restore.getParent());
        assertTrue(export.getBottom()<=restore.getTop());
        assertNotNull("0.140 exposes export categories directly in the vertical landing list",root.findViewWithTag("backup.category.APPEARANCE"));
        assertTrue(export.requestFocus());
        key(KeyEvent.KEYCODE_DPAD_DOWN);
        assertSame(restore,root.findFocus());
        TextView description=root.findViewWithTag("backup.action.description");
        assertTrue(description.getText().toString().startsWith("Restore Backup"));
        key(KeyEvent.KEYCODE_DPAD_RIGHT);assertFalse("Right enters the description pane",restore.hasFocus());key(KeyEvent.KEYCODE_DPAD_LEFT);assertSame("Left restores the selected backup action",restore,root.findFocus());
        restore.performClick();settle();
        assertEquals(android.content.Intent.ACTION_OPEN_DOCUMENT,Shadows.shadowOf(host.get()).getNextStartedActivityForResult().intent.getAction());
        capture("0139-shield-repair-backup-landing");
    }
}
