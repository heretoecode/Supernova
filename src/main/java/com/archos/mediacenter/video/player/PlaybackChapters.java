package com.archos.mediacenter.video.player;

import com.archos.medialib.MediaMetadata;
import java.io.Serializable;
import java.util.*;

/** Paired with avos-supernova141-chapters.patch. Only embedded playback-relative starts. */
public final class PlaybackChapters {
    public static final int COUNT=9003, BASE=40000, STRIDE=2;
    public static final class Chapter implements Serializable {
        private static final long serialVersionUID=1L;
        public final long startMs;
        public final String title;
        public Chapter(long startMs,String title){this.startMs=startMs;this.title=title==null?"":title.replaceAll("[\\p{Cntrl}]"," ").trim();}
    }
    public static List<Chapter> read(MediaMetadata data,long duration){
        if(data==null||duration<=0||!data.has(COUNT))return Collections.emptyList();
        try{
            int count=data.getInt(COUNT);if(count<1||count>256)return Collections.emptyList();
            List<Chapter> result=new ArrayList<>();long previous=-1;
            for(int i=0;i<count;i++){
                int key=BASE+i*STRIDE;if(!data.has(key))return Collections.emptyList();long start=data.getLong(key);
                if(start<0||start>=duration||start<=previous)return Collections.emptyList();
                String title=data.has(key+1)?data.getString(key+1):"";if(title!=null&&title.length()>160)title=title.substring(0,160);
                result.add(new Chapter(start,title));previous=start;
            }
            return Collections.unmodifiableList(result);
        }catch(RuntimeException invalid){return Collections.emptyList();}
    }
    public static String titleAt(List<Chapter> chapters,long position){String title="";if(chapters!=null)for(Chapter chapter:chapters){if(chapter.startMs>position)break;title=chapter.title;}return title;}
    private PlaybackChapters(){}
}
