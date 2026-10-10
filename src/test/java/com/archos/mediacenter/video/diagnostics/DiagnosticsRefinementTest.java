package com.archos.mediacenter.video.diagnostics;

import android.app.*;
import android.os.Looper;
import android.view.*;
import android.widget.Button;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.BuildConfig;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DiagnosticsRefinementTest {
 private String flight(){DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");return recorder.snapshot(android.os.SystemClock.elapsedRealtime());}
 @After public void stop(){Diagnostics.setEnabled(RuntimeEnvironment.getApplication(),false);}
 @Test public void qaDefaultSurvivesClearDataAndExplicitOptOutSurvivesReinstall(){
  Application app=RuntimeEnvironment.getApplication();var prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().commit();
  Thread.UncaughtExceptionHandler previous=Thread.getDefaultUncaughtExceptionHandler();
  try{Diagnostics.install(app);assertEquals(BuildConfig.DEVELOPMENT_DIAGNOSTICS,Diagnostics.enabled());Diagnostics.setEnabled(app,false);Diagnostics.install(app);assertFalse(Diagnostics.enabled());prefs.edit().remove(Diagnostics.KEY).commit();Diagnostics.install(app);assertEquals(BuildConfig.DEVELOPMENT_DIAGNOSTICS,Diagnostics.enabled());}
  finally{Thread.setDefaultUncaughtExceptionHandler(previous);}
 }
 @Test public void watchdogAndAggregateTrendsAreClassifiedAsHeuristicsWithBoundedStacks(){
  Diagnostics.setEnabled(RuntimeEnvironment.getApplication(),true);
  ReflectionHelpers.setStaticField(Diagnostics.class,"foreground",1);ReflectionHelpers.setStaticField(Diagnostics.class,"mainAck",0L);ReflectionHelpers.setStaticField(Diagnostics.class,"lastStall",0L);ReflectionHelpers.setStaticField(Diagnostics.class,"trendSamples",0);
  org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofSeconds(61));
  for(int sample=0;sample<6;sample++)ReflectionHelpers.callStaticMethod(Diagnostics.class,"heartbeat");
  String evidence=flight();assertTrue(evidence.contains("main_thread_stall_suspected"));assertTrue(evidence.contains("watchdog_heuristic"));assertTrue(evidence.contains("performance_trend"));assertTrue(evidence.contains("observed_trend_not_leak_diagnosis"));assertTrue(evidence.contains("\"samples\":6"));
  ReflectionHelpers.setStaticField(Diagnostics.class,"foreground",0);Shadows.shadowOf(Looper.getMainLooper()).idle();
 }
 @Test public void intentionalHeldBoundaryIsDistinctFromRepeatedUnhandledNavigation(){
  var host=Robolectric.buildActivity(Activity.class).setup().visible();
  try{Button button=new Button(host.get());button.setFocusableInTouchMode(true);host.get().setContentView(button);Shadows.shadowOf(Looper.getMainLooper()).idle();button.measure(View.MeasureSpec.makeMeasureSpec(200,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(50,View.MeasureSpec.EXACTLY));button.layout(0,0,200,50);button.requestFocus();Diagnostics.setEnabled(host.get(),true);ReflectionHelpers.setStaticField(Diagnostics.class,"unhandledNavigation",0);
   for(int n=0;n<4;n++)Diagnostics.navigation(button,button,KeyEvent.KEYCODE_DPAD_DOWN,true);assertFalse(flight().contains("repeated_unhandled_navigation"));
   for(int n=0;n<4;n++)Diagnostics.navigation(button,button,KeyEvent.KEYCODE_DPAD_DOWN,false);assertTrue(flight().contains("repeated_unhandled_navigation"));
  }finally{host.pause().stop().destroy();}
 }
}
