package com.archos.mediacenter.video.leanback;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.util.*;
import org.json.JSONException;

/** Pure legacy viewing inventory. Never migrates live stores or imports account/folder ownership. */
public final class LegacyViewingSnapshot {
    public static Map<String,ViewingHistory.Record> merge(Context context,Map<String,ViewingHistory.Record> structured,Snapshot snapshot)throws JSONException {
        var preferences=PreferenceManager.getDefaultSharedPreferences(context);boolean historyCleared=preferences.getBoolean("supernova_history_cleared",false),positionsCleared=preferences.getBoolean("supernova_positions_replaced_v2",false);
        Map<String,ViewingHistory.Record> legacy=new LinkedHashMap<>();Set<String> excluded=new HashSet<>();List<Entry> entries=new ArrayList<>(snapshot.movies);entries.addAll(snapshot.episodes);entries.addAll(snapshot.unmatched);
        for(Entry entry:entries){if(!(entry.media instanceof Video))throw new JSONException("Invalid legacy viewing inventory");Video video=(Video)entry.media;String key=ViewingHistory.identity(entry);if(key==null)continue;boolean putio=video.getFileUri()!=null&&"putio".equalsIgnoreCase(video.getFileUri().getScheme());if(putio){if(key.startsWith("file:"))excluded.add(key);continue;}
            long nativePlayed=video.getLastPlayed();int nativePosition=video.getResumeMs(),duration=video.getDurationMs();if(nativePlayed<0||nativePosition< -2||duration<0)throw new JSONException("Legacy viewing values need review");boolean complete=!historyCleared&&(video.isWatched()||nativePosition==com.archos.mediacenter.video.player.PlayerActivity.LAST_POSITION_END);long played;
            // PlayerService, ViewingHistory and series activity use epoch seconds, never milliseconds.
            if(nativePlayed>Long.MAX_VALUE/1000L)throw new JSONException("Legacy viewing time exceeds supported range");played=historyCleared?0:nativePlayed;
            // Legacy last-played time does not prove when completion occurred.
            int position=positionsCleared?0:Math.max(0,nativePosition);boolean started=!historyCleared&&(complete||nativePlayed>0||nativePosition>0);long completedAt=0;ViewingHistory.Record previous=legacy.get(key);
            if(!complete&&!started&&played==0&&position==0)continue;
            if(previous==null)legacy.put(key,new ViewingHistory.Record(complete,started,position,position>0?duration:0,played,completedAt));else{
                boolean newer=played>previous.played||played==previous.played&&position>previous.position;legacy.put(key,new ViewingHistory.Record(complete||previous.completed,started||previous.started,newer?position:previous.position,newer&&position>0?duration:previous.duration,Math.max(played,previous.played),Math.max(completedAt,previous.completedAt)));
            }
        }
        Map<String,ViewingHistory.Record> result=new LinkedHashMap<>(legacy);for(var record:structured.entrySet())if(!excluded.contains(record.getKey()))result.put(record.getKey(),record.getValue());PortableViewingState.validateRecoverySnapshot(PortableViewingState.recoverySnapshot(result));return Collections.unmodifiableMap(result);
    }
    private LegacyViewingSnapshot(){}
}
