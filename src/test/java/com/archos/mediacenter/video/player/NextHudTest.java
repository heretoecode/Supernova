package com.archos.mediacenter.video.player;

import android.app.*;
import android.view.*;
import android.widget.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.R;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Actual HUD layout, timing and focus contracts. No physical playback claim. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-xhdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextHudTest {
 @Test public void ordinaryPlaybackSwitchHasNoRedundantStateText(){
  ReflectionHelpers.setStaticField(PreviewPlaybackMenus.class,"owner",host.get());
  androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putInt("preview_accent41",android.graphics.Color.WHITE).commit();
  try{for(boolean on:new boolean[]{false,true}){
   TextView row=new TextView(host.get());
   ReflectionHelpers.callStaticMethod(PreviewPlaybackMenus.class,"switchState",ReflectionHelpers.ClassParameter.from(TextView.class,row),ReflectionHelpers.ClassParameter.from(String.class,"Night mode"),ReflectionHelpers.ClassParameter.from(boolean.class,on));
   assertEquals("Night mode",row.getText().toString());assertEquals("Night mode, "+(on?"On":"Off"),row.getContentDescription().toString());assertNotNull(row.getCompoundDrawables()[2]);assertEquals(1f,row.getAlpha(),0f);
   row.setEnabled(false);ReflectionHelpers.callStaticMethod(PreviewPlaybackMenus.class,"switchState",ReflectionHelpers.ClassParameter.from(TextView.class,row),ReflectionHelpers.ClassParameter.from(String.class,"Night mode"),ReflectionHelpers.ClassParameter.from(boolean.class,on));assertEquals(.4f,row.getAlpha(),0f);
  }}finally{ReflectionHelpers.setStaticField(PreviewPlaybackMenus.class,"owner",null);}
 }
 @Rule public org.junit.rules.TestName testName=new org.junit.rules.TestName();
 private Player previous,player;private PlayerService previousService;private org.robolectric.android.controller.ActivityController<Activity> host;private View root;private PlayerController controller;
 @Before public void setup(){RuntimeEnvironment.setFontScale(testName.getMethodName().contains("LargeText")?1.5f:1f);previous=Player.sPlayer;previousService=PlayerService.sPlayerService;PlayerService.sPlayerService=null;player=mock(Player.class);Player.sPlayer=player;when(player.getDuration()).thenReturn(7200000);when(player.getCurrentPosition()).thenReturn(1800000);when(player.isPlaying()).thenReturn(true);host=Robolectric.buildActivity(Activity.class).setup().visible();Activity a=host.get();PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean("try_new_ui",true).putString("uimode","2").commit();root=LayoutInflater.from(a).inflate(R.layout.player_controller_experimental,null);a.setContentView(root);root.findViewById(R.id.control_bar).setVisibility(View.VISIBLE);((ProgressBar)root.findViewById(R.id.seek_progress)).setMax(1000);((ProgressBar)root.findViewById(R.id.seek_progress)).setProgress(250);controller=mock(PlayerController.class,CALLS_REAL_METHODS);ReflectionHelpers.setField(controller,"mContext",a);ReflectionHelpers.setField(controller,"mControllerViewLeft",root);ReflectionHelpers.setField(controller,"mControlBar",root.findViewById(R.id.control_bar));ReflectionHelpers.setField(controller,"mProgress",root.findViewById(R.id.seek_progress));ReflectionHelpers.setField(controller,"mPauseButton",root.findViewById(R.id.pause));ReflectionHelpers.setField(controller,"mCurrentTime",root.findViewById(R.id.time_current));ReflectionHelpers.setField(controller,"mEndTime",root.findViewById(R.id.time));ReflectionHelpers.setField(controller,"mClock",root.findViewById(R.id.clock));ReflectionHelpers.setField(controller,"mNextSeek",-1);ReflectionHelpers.callInstanceMethod(controller,"configurePreviewTransport");}
 @After public void close(){Player.sPlayer=previous;PlayerService.sPlayerService=previousService;host.pause().stop().destroy();RuntimeEnvironment.setFontScale(1f);}
 private void layout(){var c=root.getResources().getConfiguration();float d=root.getResources().getDisplayMetrics().density;for(int i=0;i<3;i++){int w=Math.round(c.screenWidthDp*d),h=Math.round(c.screenHeightDp*d);root.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));root.layout(0,0,w,h);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}}
 private int centre(View v){int[] p=new int[2];v.getLocationInWindow(p);return p[1]+v.getHeight()/2;}
 private void render()throws Exception{ReflectionHelpers.callInstanceMethod(controller,"updatePausePlay");controller.updateClock();((TextView)root.findViewById(R.id.preview_playback_title)).setText("Source title fallback");root.findViewById(R.id.preview_playback_title).setVisibility(View.VISIBLE);layout();View seek=root.findViewById(R.id.seek_progress);assertTrue("Flexible seek retains usable width",seek.getWidth()>40*root.getResources().getDisplayMetrics().density);for(int id:new int[]{R.id.preview_pause_group,R.id.time,R.id.preview_end_time,R.id.preview_more_group})assertEquals("Shared transport centre "+id,centre(seek),centre(root.findViewById(id)),2);TextView total=root.findViewById(R.id.time),finish=root.findViewById(R.id.preview_end_time);assertEquals("02:00:00",total.getText().toString());assertTrue("Finishing time has distinct separation",finish.getLeft()-total.getRight()>=24*root.getResources().getDisplayMetrics().density);assertTrue(finish.getText().toString().startsWith("Ends at "));for(TextView label:new TextView[]{total,finish})assertTrue(label.getLayout().getLineWidth(0)<=label.getWidth()+1);root.findViewById(R.id.preview_more).requestFocus();ReflectionHelpers.callInstanceMethod(controller,"updatePreviewScrubber");assertEquals(View.VISIBLE,root.findViewById(R.id.preview_scrub_time).getVisibility());TextView bubble=root.findViewById(R.id.preview_scrub_time);assertEquals("00:30:00",bubble.getText().toString());assertNotNull(bubble.getLayout());assertEquals("Timestamp must actually fit its painted text layout",0,bubble.getLayout().getEllipsisCount(0));assertTrue(bubble.getWidth()>bubble.getCompoundPaddingLeft()+bubble.getCompoundPaddingRight());android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(root.getWidth(),root.getHeight(),android.graphics.Bitmap.Config.ARGB_8888);root.draw(new android.graphics.Canvas(image));java.io.File out=new java.io.File("build/reports/preview-ui/next-hud-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale+".png");out.getParentFile().mkdirs();try(java.io.FileOutputStream f=new java.io.FileOutputStream(out)){image.compress(android.graphics.Bitmap.CompressFormat.PNG,100,f);}image.recycle();}
 @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void rowAt720p()throws Exception{render();}
 @Test public void rowAt1080p()throws Exception{render();}
 @Test @Config(qualifiers="w960dp-h540dp-land-xxxhdpi") public void rowAt4k()throws Exception{render();}
 @Test public void rowAtLargeText()throws Exception{render();}
 @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void rowAt720pLargeTextWithChapters()throws Exception{
  TextView title=root.findViewById(R.id.preview_playback_title);title.setText("A long actual-title fallback for the constrained viewport");title.setVisibility(View.VISIBLE);TextView episode=root.findViewById(R.id.preview_playback_episode);episode.setText("S01 E01 · Indexed episode metadata");episode.setVisibility(View.VISIBLE);com.archos.mediacenter.video.utils.VideoMetadata metadata=mock(com.archos.mediacenter.video.utils.VideoMetadata.class);when(metadata.getChapters()).thenReturn(java.util.List.of(new PlaybackChapters.Chapter(0,"A long embedded opening chapter name")));when(player.getVideoMetadata()).thenReturn(metadata);when(player.getCurrentPosition()).thenReturn(0);((ProgressBar)root.findViewById(R.id.seek_progress)).setProgress(0);controller.updateClock();layout();ReflectionHelpers.callInstanceMethod(controller,"updatePreviewScrubber");layout();View bubble=root.findViewById(R.id.preview_scrub_time);int[] ep=new int[2],tip=new int[2];episode.getLocationInWindow(ep);bubble.getLocationInWindow(tip);assertTrue("Title metadata stays above the chapter bubble",ep[1]+episode.getHeight()<=tip[1]);assertEquals(0,((TextView)bubble).getLayout().getEllipsisCount(0));assertTrue("Fallback title fits its measured box",title.getLayout().getLineBottom(title.getLineCount()-1)<=title.getHeight());
 }

 /** Owner report 2 Nov 2026: the controls appeared low on the first press and jumped up on the next. */
 @Test public void controlsStartAtTheirFinalBottomInset() {
  View bar = root.findViewById(R.id.control_bar);
  int initial = ((ViewGroup.MarginLayoutParams) bar.getLayoutParams()).bottomMargin;
  assertTrue("inset present before the first show", initial > 0);
  layout();
  ReflectionHelpers.callInstanceMethod(controller, "updatePreviewScrubber");
  controller.updateClock();
  layout();
  assertEquals("no jump after the first update", initial, ((ViewGroup.MarginLayoutParams) bar.getLayoutParams()).bottomMargin);
 }

 /** Owner decision 2 Nov 2026: Play/Pause and Settings are rounded rectangles (corner 12), not a circle and a pill. */
 @Test public void playPauseAndSettingsAreRoundedRectangles() {
  ReflectionHelpers.callInstanceMethod(controller, "configurePreviewTransport");
  float density = root.getResources().getDisplayMetrics().density;
  for (int id : new int[]{R.id.preview_pause_group, R.id.preview_more_group}) {
   android.graphics.drawable.Drawable background = root.findViewById(id).getBackground();
   assertNotNull(background);
   android.graphics.drawable.Drawable face = background instanceof android.graphics.drawable.StateListDrawable ? background.getCurrent() : background;
   assertTrue("shared rounded-rectangle button", face instanceof com.archos.mediacenter.video.leanback.SupernovaStyle.Box);
   assertEquals(12f * density, ((com.archos.mediacenter.video.leanback.SupernovaStyle.Box) face).radius(), 0.5f);
  }
 }

 /** Owner decision 2 Nov 2026: "01:02:26 · Ends at 12:05" with a middle dot that follows Ends at visibility. */
 @Test public void middleDotSeparatesTotalRuntimeFromEndsAt() {
  controller.updateClock();
  layout();
  TextView total = root.findViewById(R.id.time);
  TextView dot = root.findViewById(R.id.preview_end_separator);
  TextView finish = root.findViewById(R.id.preview_end_time);
  assertEquals("·", dot.getText().toString());
  assertEquals(View.VISIBLE, dot.getVisibility());
  assertTrue("dot sits after total runtime", dot.getLeft() >= total.getRight());
  assertTrue("dot sits before Ends at", dot.getRight() <= finish.getLeft());
  assertEquals("dot shares the transport centre line", centre(finish), centre(dot), 2);
  when(player.getDuration()).thenReturn(0);
  controller.updateClock();
  assertEquals(View.GONE, finish.getVisibility());
  assertEquals("no orphan dot without Ends at", View.GONE, dot.getVisibility());
 }
 @Test public void pausedKnownDurationKeepsFinishAndUnknownDurationHidesIt(){controller.updateClock();when(player.isPlaying()).thenReturn(false);ReflectionHelpers.callInstanceMethod(controller,"updatePausePlay");assertEquals(View.VISIBLE,root.findViewById(R.id.preview_end_time).getVisibility());assertTrue(((TextView)root.findViewById(R.id.preview_end_time)).getText().toString().startsWith("Ends at "));assertEquals("02:00:00",((TextView)root.findViewById(R.id.time)).getText());when(player.isPlaying()).thenReturn(true);when(player.getDuration()).thenReturn(0);controller.updateClock();assertEquals("—",((TextView)root.findViewById(R.id.time)).getText());assertEquals(View.GONE,root.findViewById(R.id.preview_end_time).getVisibility());}
 @Test public void actualControllerVerticalEventsReturnToSeekWithoutCyclingButtons(){layout();ReflectionHelpers.setField(controller,"mControlBarShowing",true);ReflectionHelpers.setField(controller,"mLastTouchEventTime",-1L);ReflectionHelpers.setField(controller,"mHandler",mock(android.os.Handler.class));View seek=root.findViewById(R.id.seek_progress),settings=root.findViewById(R.id.preview_more),pause=root.findViewById(R.id.pause);seek.requestFocus();controller.onKey(KeyEvent.KEYCODE_DPAD_UP,new KeyEvent(100,100,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP,0));assertTrue("Seek Up reaches Settings in the actual controller",settings.hasFocus());controller.onKey(KeyEvent.KEYCODE_DPAD_DOWN,new KeyEvent(101,101,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,0));assertTrue("Settings Down returns to seek",seek.hasFocus());controller.onKey(KeyEvent.KEYCODE_DPAD_DOWN,new KeyEvent(102,102,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,0));assertTrue("Seek Down reaches Play/Pause",pause.hasFocus());controller.onKey(KeyEvent.KEYCODE_DPAD_UP,new KeyEvent(103,103,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP,0));assertTrue("Play/Pause Up returns to seek",seek.hasFocus());}
 @Test public void chapterBubbleTracksCandidateAndStaysInsideSeekAtBothEnds(){com.archos.mediacenter.video.utils.VideoMetadata metadata=mock(com.archos.mediacenter.video.utils.VideoMetadata.class);when(metadata.getChapters()).thenReturn(java.util.List.of(new PlaybackChapters.Chapter(0,"Opening"),new PlaybackChapters.Chapter(6000000,"Final sequence with a long real chapter label")));when(player.getVideoMetadata()).thenReturn(metadata);controller.updateClock();layout();for(int at:new int[]{0,7199999}){ReflectionHelpers.setField(controller,"mNextSeek",at);((ProgressBar)root.findViewById(R.id.seek_progress)).setProgress((int)(at*1000L/7200000));ReflectionHelpers.callInstanceMethod(controller,"updatePreviewScrubber");layout();TextView bubble=root.findViewById(R.id.preview_scrub_time);View seek=root.findViewById(R.id.seek_progress);assertTrue(bubble.getX()>=0);assertTrue(bubble.getX()+bubble.getMeasuredWidth()<=seek.getWidth()+1);assertTrue(bubble.getText().toString().startsWith(PreviewHudTiming.timestamp(at)));}assertTrue(((TextView)root.findViewById(R.id.preview_scrub_time)).getText().toString().contains("Final sequence"));}
 @Test public void graphKeepsSeekingSeparateFromButtonReachability(){assertEquals(R.id.preview_more,root.findViewById(R.id.seek_progress).getNextFocusUpId());assertEquals(R.id.pause,root.findViewById(R.id.seek_progress).getNextFocusDownId());assertEquals(R.id.seek_progress,root.findViewById(R.id.pause).getNextFocusUpId());assertEquals(R.id.seek_progress,root.findViewById(R.id.preview_more).getNextFocusDownId());assertEquals(R.id.preview_more,root.findViewById(R.id.preview_more).getNextFocusRightId());}
}
