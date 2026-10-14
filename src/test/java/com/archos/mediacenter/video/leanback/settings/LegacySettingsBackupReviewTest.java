package com.archos.mediacenter.video.leanback.settings;


import android.app.Application;

import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import androidx.preference.PreferenceManager;

import com.archos.mediacenter.video.CustomApplication;

import com.archos.mediacenter.video.leanback.*;

import com.archos.mediacenter.video.utils.*;

import com.archos.mediacenter.video.utils.backup.*;

import java.io.*;

import java.util.*;

import org.json.*;

import org.junit.*;

import org.junit.runner.RunWith;

import org.robolectric.*;

import org.robolectric.annotation.*;

import org.robolectric.util.ReflectionHelpers;

import static org.junit.Assert.*;


@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi") @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LegacySettingsBackupReviewTest {
    @Before public void setup(){PreviewAsyncFixtures.offlineTransport();
ReflectionHelpers.setStaticField(CustomApplication.class,"systemLocale",Locale.getDefault());
com.archos.environment.ArchosUtils.setGlobalContext(RuntimeEnvironment.getApplication());
ReflectionHelpers.setStaticField(CustomApplication.class,"log",org.slf4j.LoggerFactory.getLogger(CustomApplication.class));
ReflectionHelpers.setStaticField(CustomApplication.class,"mContext",RuntimeEnvironment.getApplication());
PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().clear().putBoolean("try_new_ui",false).putInt("preview_accent41",7).putString("oauth_token","AUTH_CANARY").commit();
try{com.squareup.picasso.Picasso.get();
}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());
}}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();
}
    private BackupReview review(VideoSettingsFragment.PrefsFragment fragment){VideoPreferencesCommon common=ReflectionHelpers.getField(fragment,"mPreferencesCommon");
return ReflectionHelpers.getField(common,"backupReview");
}
    private AlertDialog dialog(BackupReview review){return ReflectionHelpers.getField(review,"dialog");
}
    private void await(BackupOperationModel model,BackupOperationModel.Phase expected)throws Exception{long end=System.nanoTime()+java.util.concurrent.TimeUnit.SECONDS.toNanos(15);
while(model.isBusy()){Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
if(System.nanoTime()>end)throw new AssertionError("worker timeout");
Thread.sleep(10);
}Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
assertEquals(model.current().message,expected,model.current().phase);
}
    private File archive(android.content.Context context)throws Exception{File file=new File(context.getCacheDir(),"dialog-review-source.zip");
JSONObject appearance=new JSONObject().put("schema",1).put("category","APPEARANCE").put("values",new JSONObject().put("preview_accent41",new JSONObject().put("type","INT").put("value",19)));
JSONObject audio=new JSONObject().put("schema",1).put("category","AUDIO").put("values",new JSONObject());
JSONObject locations=new JSONObject().put("schema",1).put("rows",new JSONArray());
LogicalBackupArchive.writeCategories(file,Map.of(BackupCategory.APPEARANCE,appearance,BackupCategory.AUDIO,audio,BackupCategory.LOCATIONS,locations),()->false);
return file;
}
    private VideoSettingsFragment.PrefsFragment install(TopNavigationTest.Host host){var fragment=new VideoSettingsFragment.PrefsFragment();
host.getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commitNow();
Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
assertNotNull(review(fragment));
assertNull(fragment.requireView().findViewWithTag("settings.backup.workspace"));
return fragment;
}
    @Test public void actualOlderSettingsLogicalReviewSurvivesRecreationAndRequiresSelectedConfirmation()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
try{var fragment=install(host.get());
BackupReview review=review(fragment);
BackupOperationModel model=new androidx.lifecycle.ViewModelProvider(host.get()).get(BackupOperationModel.class);
File source=archive(host.get());
review.open(Uri.fromFile(source));
await(model,BackupOperationModel.Phase.REVIEW);
AlertDialog choices=dialog(review);
assertEquals(3,choices.getListView().getCount());
assertFalse(choices.getListView().isItemChecked(2));
choices.getListView().performItemClick(choices.getListView().getChildAt(1),1,1);
host.recreate();
Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
fragment=(VideoSettingsFragment.PrefsFragment)host.get().getSupportFragmentManager().findFragmentById(android.R.id.content);
review=review(fragment);
assertSame(model,new androidx.lifecycle.ViewModelProvider(host.get()).get(BackupOperationModel.class));
assertFalse(dialog(review).getListView().isItemChecked(1));
assertEquals(7,PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getInt("preview_accent41",0));
dialog(review).getButton(AlertDialog.BUTTON_POSITIVE).performClick();
await(model,BackupOperationModel.Phase.RESTORE_PREVIEW);
assertEquals(Set.of(BackupCategory.APPEARANCE),model.current().categories);
assertEquals(7,PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getInt("preview_accent41",0));
dialog(review).getButton(AlertDialog.BUTTON_POSITIVE).performClick();
await(model,BackupOperationModel.Phase.RESTORED);
assertEquals("activity="+host.get().getPackageName()+", model="+model.getApplication().getPackageName()+", appAccent="+PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getInt("preview_accent41",0),19,PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getInt("preview_accent41",0));
assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getString("oauth_token",null));
assertTrue(source.exists());
assertEquals(0,new File(host.get().getCacheDir(),"supernova-logical-restore").listFiles().length);

        }finally{host.pause().stop().destroy();
}
    }
    @Test public void locationsRequireExplicitPrivacyConsentAndCancellingReviewLeavesLiveState()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
try{var fragment=install(host.get());
BackupReview review=review(fragment);
BackupOperationModel model=new androidx.lifecycle.ViewModelProvider(host.get()).get(BackupOperationModel.class);
review.open(Uri.fromFile(archive(host.get())));
await(model,BackupOperationModel.Phase.REVIEW);
AlertDialog choices=dialog(review);
choices.getListView().performItemClick(choices.getListView().getChildAt(2),2,2);
assertFalse(choices.getListView().isItemChecked(2));
AlertDialog privacy=ReflectionHelpers.getField(review,"privacy");
assertTrue("privacy dialog visible",privacy.isShowing());
privacy.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
assertFalse(choices.getListView().isItemChecked(2));
choices.getListView().performItemClick(choices.getListView().getChildAt(2),2,2);
privacy=ReflectionHelpers.getField(review,"privacy");
privacy.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
assertTrue("location consent checked",choices.getListView().isItemChecked(2));
choices.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
await(model,BackupOperationModel.Phase.IDLE);
assertEquals(7,PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getInt("preview_accent41",0));
assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getString("oauth_token",null));

        }finally{host.pause().stop().destroy();
}
    }
    @Test public void historicalArchiveOffersSeparateWholeLibraryConfirmationAndCancelRetainsAuthentication()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
try{var fragment=install(host.get());
try(var helper=new com.archos.mediaprovider.video.VideoOpenHelper(host.get())){helper.getWritableDatabase().execSQL("INSERT INTO files(_id,media_type,_data) VALUES(77,3,'file:///fixture.mkv')");
}File source=new File(HistoricalBackupFixture.export(host.get()));
BackupReview review=review(fragment);
BackupOperationModel model=new androidx.lifecycle.ViewModelProvider(host.get()).get(BackupOperationModel.class);
review.open(Uri.fromFile(source));
await(model,BackupOperationModel.Phase.LEGACY_PREVIEW);
assertNull(dialog(review).getListView());
assertEquals("Restore legacy backup",dialog(review).getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());
assertTrue(model.current().message.contains("authentication is cleared"));
dialog(review).getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
await(model,BackupOperationModel.Phase.IDLE);
assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(model.getApplication()).getString("oauth_token",null));
assertTrue(source.exists());

        }finally{host.pause().stop().destroy();
}
    }
}
