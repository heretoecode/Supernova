package com.archos.mediacenter.video.player;
import android.app.Activity;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.utils.introdb.IntroSegments;
/** In-player presentation over the existing next-episode engine; never fabricates completion. */
final class PreviewUpNext {
 private final Activity activity;private final android.os.Handler handler=new android.os.Handler(android.os.Looper.getMainLooper());
 private TextView skipPill;private long skipExpires;private View skipOrigin;
 private LinearLayout panel;private TextView countdown;private long episodeId=-1,deadline;private boolean dismissed;private long lastTick;private View previousFocus;private int consumeKeyUp=-1;
 PreviewUpNext(Activity activity){this.activity=activity;handler.post(tick);}
 static long trigger(long duration,IntroSegments segments){return Math.max(0,duration-5000);}

 private final Runnable tick=new Runnable(){public void run(){PlayerService service=PlayerService.sPlayerService;Player player=Player.sPlayer;long now=android.os.SystemClock.elapsedRealtime();segmentPill(service);if(service!=null&&player!=null&&service.getVideoInfo()!=null){long current=service.getVideoInfo().id;if(current!=episodeId){remove();episodeId=current;dismissed=false;}
   Episode next=service.previewAdjacentEpisode(1);long duration=player.getDuration(),position=player.getCurrentPosition();
   if(!dismissed&&next!=null&&service.previewAutoNextEnabled()&&duration>0&&position>=trigger(duration,service.getIntroSegments())){if(panel==null)show(next,service);if(!player.isPlaying()&&lastTick>0)deadline+=now-lastTick;long seconds=Math.max(0,(deadline-now+999)/1000);countdown.setText("UP NEXT  ·  Starting in "+seconds+"s");if(seconds==0){dismissed=true;remove();service.previewNavigateEpisode(1);}}
   else if(panel!=null){remove();}
  }lastTick=now;handler.postDelayed(this,500);}};
 private void show(Episode next,PlayerService service){deadline=android.os.SystemClock.elapsedRealtime()+5000;panel=new LinearLayout(activity);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(16),dp(12),dp(16),dp(12));android.graphics.drawable.GradientDrawable surface=PreviewDialog.surface(activity,false);surface.setColor(0xec08121b);surface.setCornerRadius(dp(10));panel.setBackground(surface);panel.setElevation(dp(8));countdown=text("Up Next",15);countdown.setTextColor(PreviewAccent.color(activity));panel.addView(countdown);if(next.getPictureUri()!=null){ImageView still=new ImageView(activity);still.setScaleType(ImageView.ScaleType.CENTER_CROP);LinearLayout.LayoutParams frame=new LinearLayout.LayoutParams(-1,dp(90));frame.topMargin=dp(8);panel.addView(still,frame);com.squareup.picasso.Picasso.get().load(next.getPictureUri()).resize(dp(283),dp(90)).centerCrop().networkPolicy(com.squareup.picasso.NetworkPolicy.OFFLINE).noFade().into(still);}TextView name=text("S"+next.getSeasonNumber()+" E"+next.getEpisodeNumber()+" · "+next.getEpisodeName(),14);name.setMaxLines(2);name.setPadding(0,dp(8),0,dp(8));panel.addView(name);LinearLayout actions=new LinearLayout(activity);for(String label:new String[]{"Play Now"}){TextView button=text(label,14);button.setFocusable(true);button.setBackground(PreviewDialog.focus(activity));button.setPadding(dp(12),dp(8),dp(12),dp(8));button.setOnClickListener(v->{dismissed=true;remove();if(label.equals("Play Now"))service.previewNavigateEpisode(1);else service.previewCancelAutoNext();});actions.addView(button);}panel.addView(actions);FrameLayout root=activity.findViewById(android.R.id.content);FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(dp(315),-2,Gravity.BOTTOM|Gravity.END);params.setMargins(dp(26),dp(20),dp(26),dp(24));root.addView(panel,params);}
 private void segmentPill(PlayerService service){
  PlayerService.SegmentPrompt prompt=service==null?null:service.segmentPrompt();
  if(prompt==null){removeSkip();return;}if(skipPill!=null&&skipExpires==prompt.expires)return;removeSkip();skipExpires=prompt.expires;
  skipPill=com.archos.mediacenter.video.leanback.SharedThreePanel.action(activity,"Skip "+PlayerService.introLabels(activity).get(prompt.type),()->{service.skipPrompt();removeSkip();});
  skipPill.setTag("playback.segment.skip");skipPill.setBackground(PreviewDialog.actionContainerFocus(activity));skipPill.setElevation(dp(4));
  FrameLayout root=activity.findViewById(android.R.id.content);FrameLayout.LayoutParams frame=new FrameLayout.LayoutParams(-2,dp(50),Gravity.END|Gravity.BOTTOM);frame.setMargins(dp(24),dp(20),dp(26),dp(196));root.addView(skipPill,frame);
 }
 private void removeSkip(){if(skipPill!=null){boolean restore=skipPill.hasFocus();if(skipPill.getParent() instanceof ViewGroup)((ViewGroup)skipPill.getParent()).removeView(skipPill);skipPill=null;if(restore&&skipOrigin!=null)skipOrigin.requestFocus();}skipOrigin=null;}
 boolean cancelFocused(){if(panel==null||!panel.hasFocus())return false;dismissed=true;remove();if(PlayerService.sPlayerService!=null)PlayerService.sPlayerService.previewCancelAutoNext();return true;}
 boolean handleKey(KeyEvent event,Runnable showHud){
  int key=event.getKeyCode();if(event.getAction()==KeyEvent.ACTION_UP&&key==consumeKeyUp){consumeKeyUp=-1;return true;}
  if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
  if(skipPill!=null){if(skipPill.hasFocus()){if(key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER){if(event.getRepeatCount()==0)skipPill.performClick();consumeKeyUp=key;return true;}if(key==KeyEvent.KEYCODE_DPAD_DOWN||key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT){if(skipOrigin!=null)skipOrigin.requestFocus();consumeKeyUp=key;return true;}if(key==KeyEvent.KEYCODE_BACK||key==KeyEvent.KEYCODE_DPAD_UP){consumeKeyUp=key;return true;}}else if(key==KeyEvent.KEYCODE_DPAD_DOWN&&activity.getCurrentFocus()!=null&&activity.getCurrentFocus().getId()==com.archos.mediacenter.video.R.id.preview_more){skipOrigin=activity.getCurrentFocus();skipPill.requestFocus();consumeKeyUp=key;return true;}}
  if(panel==null)return false;
  LinearLayout actions=(LinearLayout)panel.getChildAt(panel.getChildCount()-1);
  if(!panel.hasFocus()){if(key!=KeyEvent.KEYCODE_DPAD_UP)return false;previousFocus=activity.getCurrentFocus();actions.getChildAt(0).requestFocus();consumeKeyUp=key;return true;}
  if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT){int current=actions.indexOfChild(actions.findFocus());actions.getChildAt(Math.max(0,Math.min(actions.getChildCount()-1,current+(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1)))).requestFocus();}
  else if(key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER){if(event.getRepeatCount()==0&&actions.findFocus()!=null)actions.findFocus().performClick();}
  else if(key==KeyEvent.KEYCODE_DPAD_DOWN){if(previousFocus!=null)previousFocus.requestFocus();showHud.run();}
  else if(key==KeyEvent.KEYCODE_BACK){cancelFocused();}
  else if(key!=KeyEvent.KEYCODE_DPAD_UP)return false;
  consumeKeyUp=key;return true;
 }
 private TextView text(String value,int size){TextView v=new TextView(activity);v.setText(value);v.setTextSize(size);v.setTextColor(0xffe7eff5);return v;}
 private int dp(int value){return PreviewDialog.dp(activity,value);}
 private void remove(){boolean restore=panel!=null&&panel.hasFocus();if(panel!=null&&panel.getParent() instanceof ViewGroup)((ViewGroup)panel.getParent()).removeView(panel);panel=null;if(restore&&previousFocus!=null&&previousFocus.isAttachedToWindow())previousFocus.requestFocus();previousFocus=null;}
 void stop(){handler.removeCallbacksAndMessages(null);remove();removeSkip();}
}
