package com.archos.mediacenter.video.utils;

import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/** Intent order and conservative client pacing, independent of provider download quotas. */
public final class SubtitleDiscoveryPolicy {
    public static final int MANUAL=0,PLAYBACK=1,CONTINUING=2,DETAILS=3,HERO=4,SEASON=5;
    public static final long SPACING=30000,POSITIVE_TTL=24*60*60*1000L,NEGATIVE_TTL=6*60*60*1000L;
    public static final class Job {public final String key;public final OpenSubtitlesQueryParams query;public final String languages;public int priority;final long order;Job(String key,OpenSubtitlesQueryParams query,String languages,int priority,long order){this.key=key;this.query=query;this.languages=languages;this.priority=priority;this.order=order;}}
    public static final class Queue {
        private final Map<String,Job> jobs=new LinkedHashMap<>();private long sequence;
        public synchronized void offer(String key,OpenSubtitlesQueryParams query,String languages,int priority){Job old=jobs.get(key);if(old!=null){old.priority=Math.min(old.priority,priority);return;}jobs.put(key,new Job(key,query,languages,priority,sequence++));}
        public synchronized Job take(){Job next=jobs.values().stream().min(Comparator.comparingInt((Job j)->j.priority).thenComparingLong(j->j.order)).orElse(null);if(next!=null)jobs.remove(next.key);return next;}
        public synchronized void remove(String key){jobs.remove(key);}
        public synchronized void clearSpeculation(){jobs.values().removeIf(j->j.priority>=HERO);}
        public synchronized int size(){return jobs.size();}
    }
    public static List<Entry> remainingSeason(android.content.Context context,Entry playing,List<Entry> source){
        if(!(playing.media instanceof Episode))return Collections.emptyList();Episode selected=(Episode)playing.media;List<Entry> season=new ArrayList<>();
        PreviewVariants.initialize(context);
        for(Entry entry:PreviewVariants.logicalChoices(source))if(entry.media instanceof Episode){Episode episode=(Episode)entry.media;if(entry.show==playing.show&&episode.getSeasonNumber()==selected.getSeasonNumber()&&episode.getEpisodeNumber()>selected.getEpisodeNumber()&&!PreviewSeriesJourney.completed(episode)&&LibraryHealth.state(context,episode)==LibraryHealth.State.AVAILABLE)season.add(entry);}
        season.sort(Comparator.comparingInt(e->((Episode)e.media).getEpisodeNumber()));return season;
    }
    public static long retryAfter(String value,long now){
        if(value==null)return 0;try{return now+Math.max(0,Long.parseLong(value.trim()))*1000;}catch(NumberFormatException date){try{return ZonedDateTime.parse(value,DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli();}catch(Exception invalid){return 0;}}
    }
    public static long backoff(long now,int failures,long supplied){return Math.max(supplied,now+Math.min(6*60*60*1000L,60000L*(1L<<Math.min(8,Math.max(0,failures)))));}
    private SubtitleDiscoveryPolicy(){}
}
