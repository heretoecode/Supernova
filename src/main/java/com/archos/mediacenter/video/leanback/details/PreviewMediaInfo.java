package com.archos.mediacenter.video.leanback.details;
import org.json.*;
import java.util.*;
public final class PreviewMediaInfo {
 /** Retain measured channel labels; do not infer a speaker layout from a channel count. */
 public static String audioTrack(String codec,String channels,int sampleRate){
  List<String> facts=new ArrayList<>();String label=format(codec),channel=plain(channels==null?"":channels.trim());
  if(!label.isEmpty())facts.add(label);
  if(!channel.isEmpty()&&!channel.equals("0")&&!channel.equals("-1"))facts.add(channel.matches("[0-9]+")?channel+" channels":channel);
  if(sampleRate>0)facts.add(sampleRate+" Hz");
  return android.text.TextUtils.join(" · ",facts);
 }
 public static String format(String value){
  if(value==null||value.trim().isEmpty())return "";String raw=value.trim();
  if(raw.startsWith("{")){try{JSONObject o=new JSONObject(raw);JSONArray tracks=o.optJSONArray("audiotracks");LinkedHashSet<String> labels=new LinkedHashSet<>();if(tracks!=null){for(int i=0;i<tracks.length();i++){JSONObject t=tracks.optJSONObject(i);if(t==null)continue;String codec=plain(t.optString("format","")),channels=plain(t.optString("channels",""));if(!codec.isEmpty())labels.add(codec+(channels.isEmpty()?"":" · "+channels));}}else {String codec=plain(o.optString("format",o.optString("codec","")));if(!codec.isEmpty())labels.add(codec);}return android.text.TextUtils.join(" · ",labels);}catch(JSONException ignored){return "";}}
  return plain(raw);
 }
 private static String plain(String s){if(s==null||s.length()>70||s.matches(".*[{}\\[\\]\"=:].*")||s.equalsIgnoreCase("unknown")||s.equalsIgnoreCase("null"))return "";return s;}
}
