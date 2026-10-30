package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;

/** Page-scoped logical title counts; physical indexed files supply storage totals. No source I/O. */
public final class LibraryStatistics {
    public int total,unwatched,inProgress,addedThisMonth,localTitles,networkTitles,unknownLocal,unknownNetwork;
    public long localBytes,networkBytes;
    public static LibraryStatistics calculate(Snapshot snapshot,List<Entry> selected,long now,TimeZone zone){
        LibraryStatistics result=new LibraryStatistics();Map<String,List<Entry>> titles=new LinkedHashMap<>();
        for(Entry title:selected)titles.put(title.media instanceof Tvshow?"show:"+title.show:PreviewVariants.logicalKey(title),new ArrayList<>());
        List<Entry> physical=snapshot.technical.isEmpty()?new ArrayList<>(snapshot.movies):snapshot.technical;
        if(snapshot.technical.isEmpty())physical.addAll(snapshot.episodes);
        Set<Long> seen=new HashSet<>();
        for(Entry file:physical){if(!(file.media instanceof Video)||!seen.add(((Video)file.media).getId()))continue;
            String key=file.media instanceof Episode?"show:"+file.show:PreviewVariants.logicalKey(file);
            if(titles.containsKey(key))titles.get(key).add(file);
        }
        Calendar first=Calendar.getInstance(zone);first.setTimeInMillis(now);first.set(Calendar.DAY_OF_MONTH,1);first.set(Calendar.HOUR_OF_DAY,0);first.set(Calendar.MINUTE,0);first.set(Calendar.SECOND,0);first.set(Calendar.MILLISECOND,0);
        long month=first.getTimeInMillis()/1000,seconds=now/1000;
        result.total=titles.size();
        for(Map.Entry<String,List<Entry>> title:titles.entrySet()){
            boolean local=false,network=false,started=false;long added=Long.MAX_VALUE;
            Map<String,Boolean> completed=new HashMap<>(),resumes=new HashMap<>();
            for(Entry file:title.getValue()){
                Video video=(Video)file.media;boolean done=PreviewSeriesJourney.completed(video);
                String logical=PreviewVariants.logicalKey(file);completed.put(logical,completed.getOrDefault(logical,false)||done);
                started|=done||video.isWatched()||video.getResumeMs()>0;resumes.put(logical,resumes.getOrDefault(logical,false)||!done&&video.getResumeMs()>0);
                if(file.added>0)added=Math.min(added,file.added);
                Uri uri=video.getFileUri();String scheme=uri==null?null:uri.getScheme();
                boolean remote=scheme!=null&&!scheme.equalsIgnoreCase("file")&&!scheme.equalsIgnoreCase("content");
                if(remote){network=true;result.networkBytes+=Math.max(0,file.bytes);if(file.knownSizes==0)result.unknownNetwork++;}
                else{local=true;result.localBytes+=Math.max(0,file.bytes);if(file.knownSizes==0)result.unknownLocal++;}
            }
            if(local)result.localTitles++;if(network)result.networkTitles++;
            if(!title.getValue().isEmpty()&&!started)result.unwatched++;
            for(String logical:completed.keySet())if(!completed.get(logical)&&resumes.getOrDefault(logical,false)){result.inProgress++;break;}
            if(added>=month&&added<=seconds)result.addedThisMonth++;
        }
        return result;
    }
    public String signature(){return total+":"+unwatched+":"+inProgress+":"+addedThisMonth+":"+localTitles+":"+networkTitles+":"+unknownLocal+":"+unknownNetwork+":"+localBytes+":"+networkBytes;}
    public String share(int count){return String.format(Locale.getDefault(),"%.0f%% of library",total==0?0d:100d*count/total);}
    private LibraryStatistics(){}
}
