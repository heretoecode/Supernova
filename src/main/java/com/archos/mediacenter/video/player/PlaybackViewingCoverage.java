package com.archos.mediacenter.video.player;
import java.util.*;
import org.json.*;
/** Union of actually traversed playback intervals. Seeks and repeated intervals add no credit. */
public final class PlaybackViewingCoverage {
 private final TreeMap<Long,Long> ranges=new TreeMap<>();
 public void add(long start,long end){
  if(start<0||end<=start)return;Map.Entry<Long,Long> before=ranges.floorEntry(start);
  if(before!=null&&before.getValue()>=start){start=before.getKey();end=Math.max(end,before.getValue());ranges.remove(before.getKey());}
  Map.Entry<Long,Long> after;while((after=ranges.ceilingEntry(start))!=null&&after.getKey()<=end){end=Math.max(end,after.getValue());ranges.remove(after.getKey());}ranges.put(start,end);
 }
 public long viewed(){long total=0;for(Map.Entry<Long,Long> range:ranges.entrySet())total+=range.getValue()-range.getKey();return total;}
 public boolean eligible(long duration){return duration>0&&viewed()>=duration-duration/10;}
 public String encode(){JSONArray json=new JSONArray();for(Map.Entry<Long,Long> range:ranges.entrySet())json.put(new JSONArray(Arrays.asList(range.getKey(),range.getValue())));return json.toString();}
 public static PlaybackViewingCoverage decode(String value){PlaybackViewingCoverage coverage=new PlaybackViewingCoverage();try{JSONArray rows=new JSONArray(value);if(rows.length()>10000)return coverage;for(int i=0;i<rows.length();i++){JSONArray row=rows.getJSONArray(i);coverage.add(row.getLong(0),row.getLong(1));}}catch(JSONException ignored){}return coverage;}
}
