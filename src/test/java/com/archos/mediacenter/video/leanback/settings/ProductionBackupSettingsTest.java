package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.view.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class ProductionBackupSettingsTest {
    @Before public void setup(){PreviewAsyncFixtures.offlineTransport();ReflectionHelpers.setStaticField(CustomApplication.class,"systemLocale",java.util.Locale.getDefault());com.archos.mediacenter.utils.ArchosUtils.setGlobalContext(RuntimeEnvironment.getApplication());ReflectionHelpers.setStaticField(CustomApplication.class,"log",org.slf4j.LoggerFactory.getLogger(CustomApplication.class));ReflectionHelpers.setStaticField(CustomApplication.class,"mContext",RuntimeEnvironment.getApplication());PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().clear().putBoolean("try_new_ui",true).commit();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    private void layout(View root){root.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));root.layout(0,0,960,540);}
    private View find(View root,String label){if(root instanceof android.widget.TextView&&label.contentEquals(((android.widget.TextView)root).getText()))return root;if(root instanceof ViewGroup)for(int i=0;i<((ViewGroup)root).getChildCount();i++){View found=find(((ViewGroup)root).getChildAt(i),label);if(found!=null)return found;}return null;}
    @Test public void actualPreferencesFragmentBuildsFullSidebarAndAllBackupCategoriesAndFlatPages()throws Throwable{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();Throwable failure=null;try{var fragment=new VideoSettingsFragment.PrefsFragment();host.get().getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commitNow();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();View root=fragment.requireView();layout(root);View advanced=find(root,"Advanced");assertNotNull(advanced);advanced.performClick();layout(root);View opener=find(root,"Backup & Restore");assertNotNull(opener);opener.performClick();layout(root);View frame=root.findViewWithTag("settings.backup.workspace");assertNotNull(frame);assertEquals(View.VISIBLE,frame.getVisibility());for(BackupCategory category:BackupCategory.values()){View option=root.findViewWithTag("backup.category."+category.name());assertNotNull(category.name(),option);assertTrue(category.name(),option.getWidth()>0&&option.getHeight()>0);assertTrue(category.name(),option.getBottom()<=((View)option.getParent()).getHeight());}View details=root.findViewWithTag("backup.details");details.requestFocus();details.performClick();layout(root);assertNotNull(root.findViewWithTag("backup.details.page"));var controller=(BackupSettingsController)ReflectionHelpers.getField(fragment,"backupController");assertTrue(controller.back());layout(root);assertTrue(root.findViewWithTag("backup.details").hasFocus());assertTrue(controller.back());assertTrue(opener.hasFocus());
        }catch(Throwable problem){failure=problem;throw problem;}finally{try{host.pause().stop().destroy();}catch(Throwable cleanup){if(failure!=null)failure.addSuppressed(cleanup);else throw cleanup;}}
    }
}
