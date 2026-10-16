package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.view.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class BackupDirectOptionsTest {
    @Before public void setup(){new ProductionBackupSettingsTest().setup();}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}

    @Test public void mainPageExposesPersistentOptionsWithoutStartingExport(){var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{var fragment=new VideoSettingsFragment.PrefsFragment();host.get().getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commitNow();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();View root=fragment.requireView();root.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));root.layout(0,0,960,540);assertTrue(PreviewSettings.enterBackup(fragment));for(BackupCategory category:BackupCategory.ordered())assertNotNull(category.name(),root.findViewWithTag("backup.category."+category.name()));var model=new androidx.lifecycle.ViewModelProvider(host.get()).get(BackupOperationModel.class);assertEquals(BackupOperationModel.Phase.IDLE,model.current().phase);View choice=root.findViewWithTag("backup.category.APPEARANCE");boolean before=PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean(BackupCategory.APPEARANCE.preference(),BackupCategory.APPEARANCE.defaultIncluded);choice.requestFocus();choice.performClick();assertEquals(!before,PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean(BackupCategory.APPEARANCE.preference(),before));assertEquals(BackupOperationModel.Phase.IDLE,model.current().phase);assertNotNull(root.findViewWithTag("backup.action.export"));assertNotNull(root.findViewWithTag("backup.action.restore"));}finally{host.pause().stop().destroy();}}
}
