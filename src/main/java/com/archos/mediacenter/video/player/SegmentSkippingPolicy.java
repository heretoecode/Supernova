package com.archos.mediacenter.video.player;
import android.content.SharedPreferences;
import com.archos.mediacenter.utils.introdb.IntroSegments;
/** Independent policies operate only on supported provider intervals; uncertain material plays normally. */
public final class SegmentSkippingPolicy {
 public static final String MASTER="supernova_segment_enabled";
 public enum Mode { NORMAL, PROMPT, AUTO, SMART }
 public static String key(IntroSegments.Type type){return "supernova_segment_"+type.name().toLowerCase(java.util.Locale.ROOT);}
 public static Mode defaultMode(IntroSegments.Type type){switch(type){case INTRO:return Mode.PROMPT;case RECAP:case PREVIEW:return Mode.SMART;default:return Mode.AUTO;}}
 public static Mode mode(SharedPreferences prefs,IntroSegments.Type type){try{return Mode.valueOf(prefs.getString(key(type),defaultMode(type).name()));}catch(IllegalArgumentException invalid){return defaultMode(type);}}
 public static boolean auto(Mode mode,IntroSegments.Type type,boolean bingeArrival){if(mode==Mode.AUTO)return true;return mode==Mode.SMART&&type==IntroSegments.Type.RECAP&&bingeArrival;}
 public static boolean safe(IntroSegments.Type type,IntroSegments.Segment segment,long position,int duration){
  // No open-ended marker or fabricated EOF. A concrete boundary before the end retains post-credit material.
  if(segment.startMs==null||segment.endMs==null||segment.startMs<0||segment.endMs<=segment.startMs||segment.endMs<=position||duration<=0)return false;
  return PlayerService.isSafeAutoSkipTarget(segment.endMs,duration);
 }
 private SegmentSkippingPolicy(){}
}
