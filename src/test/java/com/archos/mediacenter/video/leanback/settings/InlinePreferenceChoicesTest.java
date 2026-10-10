package com.archos.mediacenter.video.leanback.settings;
import android.app.Application;
import android.view.View;
import android.widget.*;
import androidx.preference.*;
import com.archos.mediacenter.video.leanback.TopNavigationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import java.util.Set;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class InlinePreferenceChoicesTest {
 @Test public void providerMembershipPersistsImmediatelyAndFocusStaysOnNextRow(){
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   LinearLayout target=new LinearLayout(host.get());target.setOrientation(LinearLayout.VERTICAL);host.get().setContentView(target);
   PreferenceManager manager=new PreferenceManager(host.get());PreferenceScreen screen=manager.createPreferenceScreen(host.get());MultiSelectListPreference pref=new MultiSelectListPreference(host.get());pref.setKey("streaming_providers_fixture");pref.setEntries(new String[]{"Zulu","Alpha","Beta"});pref.setEntryValues(new String[]{"z","a","b"});screen.addPreference(pref);pref.setValues(Set.of());
   InlinePreferenceChoices.show(target,pref);target.measure(View.MeasureSpec.makeMeasureSpec(400,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(600,View.MeasureSpec.EXACTLY));target.layout(0,0,400,600);
   assertEquals("Selected Providers",((TextView)target.getChildAt(0)).getText().toString());target.findViewWithTag("a").performClick();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(Set.of("a"),pref.getValues());assertTrue(target.findViewWithTag("b").hasFocus());assertEquals(Set.of("a"),PreferenceManager.getDefaultSharedPreferences(host.get()).getStringSet(pref.getKey(),Set.of()));
   target.findViewWithTag("a").performClick();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertTrue(pref.getValues().isEmpty());assertTrue(target.findViewWithTag("b").hasFocus());assertEquals("Selected Providers",((TextView)target.getChildAt(0)).getText().toString());
  }finally{host.pause().stop().destroy();}
 }
 @Test public void listOptionsKeepRealNativeValuesAndChangeListenerVeto(){
  var app=RuntimeEnvironment.getApplication();PreferenceManager manager=new PreferenceManager(app);PreferenceScreen screen=manager.createPreferenceScreen(app);ListPreference pref=new ListPreference(app);pref.setKey("fixture_modes");pref.setEntries(new String[]{"Play Normally","Show Skip (5 seconds)","Auto Skip","Smart"});pref.setEntryValues(new String[]{"NORMAL","PROMPT","AUTO","SMART"});screen.addPreference(pref);pref.setValue("PROMPT");LinearLayout target=new LinearLayout(app);InlinePreferenceChoices.show(target,pref);target.findViewWithTag("SMART").performClick();assertEquals("SMART",pref.getValue());pref.setOnPreferenceChangeListener((p,v)->false);target.findViewWithTag("AUTO").performClick();assertEquals("SMART",pref.getValue());
 }
}
