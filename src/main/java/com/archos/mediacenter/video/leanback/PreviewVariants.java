package com.archos.mediacenter.video.leanback;

import com.archos.mediacenter.video.browser.adapters.object.Video;
import java.util.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;

/** Real indexed measurements only; unknown measurements sort after known values. */
public final class PreviewVariants {
    private static volatile android.content.Context policyContext;
    public static void initialize(android.content.Context context){policyContext=context.getApplicationContext();}
    public static final Comparator<Video> BEST_FIRST=Comparator.comparingInt(PreviewVariants::availabilityRank)
        .thenComparingInt(video->rangeCompatible(measuredRange(video))?0:1)
        .thenComparing(Comparator.comparingLong(PreviewVariants::quality).reversed())
        .thenComparing(Comparator.comparingLong((Video v)->v.getDurationMs()>0?Math.max(0,v.getSize())/v.getDurationMs():0).reversed()).thenComparingLong(Video::getId);
    private static int availabilityRank(Video video){android.content.Context c=policyContext;return c!=null&&LibraryHealth.state(c,video)!=LibraryHealth.State.AVAILABLE?1:0;}
    private static long quality(Video video){
        long pixels=(long)Math.max(0,video.getMeasuredWidth())*Math.max(0,video.getMeasuredHeight());int transfer=video.getMetadata()!=null&&video.getMetadata().getVideoTrack()!=null?video.getMetadata().getVideoTrack().colorTrc:0;
        long resolution=video.getMeasuredWidth()>=3840||video.getMeasuredHeight()>=2160?5:video.getMeasuredWidth()>=1728||video.getMeasuredHeight()>=1040?4:video.getMeasuredWidth()>=1200||video.getMeasuredHeight()>=720?3:pixels>0?2:0;
        String range=measuredRange(video);long bonus=range.equals("Dolby Vision")||range.equals("HDR10+")?2:range.equals("HDR (PQ)")||range.equals("HLG")?1:0;return (resolution*3+bonus)*100000000L+pixels;
    }
    private static String measuredRange(Video video){return video.getMetadata()!=null&&video.getMetadata().getVideoTrack()!=null?video.getMetadata().getVideoTrack().dynamicRange():video.getPreviewDynamicRange();}
    private static boolean rangeCompatible(String range){
        if(range.isEmpty())return true;android.content.Context context=policyContext;if(context==null||android.os.Build.VERSION.SDK_INT<24)return true;
        int wanted=range.equals("Dolby Vision")?android.view.Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION:range.equals("HDR10+")?android.view.Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS:range.equals("HLG")?android.view.Display.HdrCapabilities.HDR_TYPE_HLG:android.view.Display.HdrCapabilities.HDR_TYPE_HDR10;
        try{android.view.Display display=((android.view.WindowManager)context.getSystemService(android.content.Context.WINDOW_SERVICE)).getDefaultDisplay();if(display==null)return true;boolean displaySupports=false;for(int supported:display.getHdrCapabilities().getSupportedHdrTypes())if(supported==wanted)displaySupports=true;if(!displaySupports)return false;
            if(range.equals("Dolby Vision")){for(android.media.MediaCodecInfo codec:new android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS).getCodecInfos())if(!codec.isEncoder())for(String type:codec.getSupportedTypes())if("video/dolby-vision".equals(type))return true;return false;}return true;
        }catch(RuntimeException unavailable){return true;}
    }
    public static String choiceKey(Video video){if(video instanceof Movie&&((Movie)video).getOnlineId()>0)return "movie:"+((Movie)video).getOnlineId();if(video instanceof Episode&&((Episode)video).getOnlineId()>0)return "episode:"+((Episode)video).getOnlineId()+":"+((Episode)video).getSeasonNumber()+":"+((Episode)video).getEpisodeNumber();return "file:"+video.getFilePath();}
    public static void remember(android.content.Context context,Video video){initialize(context);androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString("preview_version_choice:"+choiceKey(video),video.getId()+"|"+com.archos.mediacenter.video.leanback.filebrowsing.BrowserSelection.canonical(video.getFileUri())).apply();}
    private static boolean manuallySelected(Video video){android.content.Context context=policyContext;if(context==null||availabilityRank(video)>0)return false;String selection=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("preview_version_choice:"+choiceKey(video),"");return selection.equals(video.getId()+"|"+com.archos.mediacenter.video.leanback.filebrowsing.BrowserSelection.canonical(video.getFileUri()));}
    public static Video resolve(android.content.Context context,Video requested){initialize(context);PreviewLibraryLoader.Snapshot snapshot=PreviewLibraryLoader.memoryCache();if(snapshot==null)return requested;List<Video> variants=new ArrayList<>();for(Entry entry:snapshot.technical)if(entry.media instanceof Video&&choiceKey((Video)entry.media).equals(choiceKey(requested)))variants.add((Video)entry.media);if(variants.isEmpty())return requested;Video chosen=variants.stream().filter(PreviewVariants::manuallySelected).findFirst().orElseGet(()->Collections.min(variants,BEST_FIRST));restoreTitleResume(variants,chosen);return chosen;}
    public static String label(Video video){
        int w=video.getMeasuredWidth(),h=video.getMeasuredHeight();
        String resolution=w>0&&h>0?w+" × "+h:"Resolution unavailable";
        return resolution+" · "+video.getFilenameNonCryptic();
    }
    /** Describe this physical encode using measured metadata, never the title or filename. */
    public static String fileBadge(android.content.Context context,Video video){
        if(video==null)return "";
        int width=video.getMeasuredWidth(),height=video.getMeasuredHeight();
        String resolution=width<=0||height<=0?"":width>=3840||height>=2160?"4K":width>=1728||height>=1040?"1080p":width>=1200||height>=720?"720p":width+" × "+height;
        String range=dynamicRange(context,video);
        return resolution.isEmpty()?range:range.isEmpty()?resolution:resolution+" · "+range;
    }
    /** Reconstruct title-level position from persisted file history after any loader refresh.
     * The caller supplies only physical variants of the same title; no database rows are rewritten. */
    public static void restoreTitleResume(List<? extends Video> variants,Video selected){
        if(selected==null||variants.size()<2)return;
        Video latest=null;
        for(Video candidate:variants)if(candidate.getLastPlayed()>0&&(latest==null||candidate.getLastPlayed()>latest.getLastPlayed()
                ||candidate.getLastPlayed()==latest.getLastPlayed()&&candidate.getId()<latest.getId()))latest=candidate;
        if(latest!=null){selected.setAutomaticResumeMs(latest.getResumeMs());if(latest.getRemoteResumeMs()>=0)selected.setRemoteResumeMs(latest.getRemoteResumeMs());}
    }
    /** Cached/indexed facts only: opening Versions never probes a network file. */
    public static String details(android.content.Context context, Video video) {
        List<String> facts = new ArrayList<>();
        if (video.getMeasuredWidth() > 0 && video.getMeasuredHeight() > 0)
            facts.add(video.getMeasuredWidth() + " × " + video.getMeasuredHeight());
        String range=dynamicRange(context,video);
        if(!range.isEmpty())facts.add(range);
        String codec = com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.format(video.getCalculatedVideoFormat());
        String audio = com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.format(video.getCalculatedBestAudioFormat());
        if(video.getMetadata()!=null){
            Set<String> tracks=new LinkedHashSet<>();
            for(int n=0;n<video.getMetadata().getAudioTrackNb();n++){
                com.archos.mediacenter.video.utils.VideoMetadata.AudioTrack track=video.getMetadata().getAudioTrack(n);
                if(track!=null){String label=com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.audioTrack(track.format,track.channels,0);if(!label.isEmpty())tracks.add(label);}
            }
            if(!tracks.isEmpty())audio=android.text.TextUtils.join(" / ",tracks);
        }
        if (!codec.isEmpty()) facts.add(codec);
        if (!audio.isEmpty()) facts.add(audio);
        if (video.getSize() > 0) facts.add(android.text.format.Formatter.formatShortFileSize(context, video.getSize()));
        return video.getFilenameNonCryptic() + (facts.isEmpty() ? "" : "\n" + android.text.TextUtils.join(" · ", facts))
                + "\n" + safeLocation(video.getFileUri());
    }
    /** Never render URI user-info, query credentials or fragments in the picker. */
    public static String dynamicRange(android.content.Context context,Video video){
        return dynamicRange(context,video,PreviewLibraryLoader.memoryCache());
    }
    static String dynamicRange(android.content.Context context,Video video,PreviewLibraryLoader.Snapshot snapshot){
        if(video.getMetadata()!=null&&video.getMetadata().getVideoTrack()!=null){
            com.archos.mediacenter.video.utils.VideoMetadata.VideoTrack track=video.getMetadata().getVideoTrack();String measured=track.dynamicRange();if(!measured.isEmpty()||track.colorTrc>0)return measured;
        }
        String measured=video.getPreviewDynamicRange();if(!measured.isEmpty())return measured;
        if(snapshot!=null)for(Entry entry:snapshot.technical){
            if(!(entry.media instanceof Video))continue;
            Video indexed=(Video)entry.media;
            if(indexed.getId()!=video.getId()||entry.bytes!=Math.max(0,video.getSize())||!Objects.equals(indexed.getFilePath(),video.getFilePath()))continue;
            String cached=context.getSharedPreferences("preview-technical-v1",0).getString("hdr:"+PreviewMetadata.key(entry),"");
            return Arrays.asList("HDR (PQ)","HLG","Dolby Vision","HDR10+").contains(cached)?cached:"";
        }
        return "";
    }
    public static String safeLocation(android.net.Uri uri) {
        if (uri == null) return "Location unavailable";
        String scheme = uri.getScheme(), path = uri.getPath();
        if (scheme == null || "file".equalsIgnoreCase(scheme)) return "Local storage · " + (path == null ? "" : path);
        String host = uri.getHost();
        return scheme.toUpperCase(Locale.ROOT) + " · " + (host == null ? "" : host) + (path == null ? "" : path);
    }
    public static String logicalKey(Entry e){
        if(e.media instanceof Episode){Episode ep=(Episode)e.media;if(ep.getOnlineId()>0)return choiceKey(ep);return "episode:"+e.show+":"+ep.getSeasonNumber()+":"+ep.getEpisodeNumber();}
        if(e.media instanceof Movie&&((Movie)e.media).getOnlineId()>0)return "movie:"+((Movie)e.media).getOnlineId();
        return e.key();
    }
    public static List<Entry> logicalChoices(List<Entry> source){
        Map<String,List<Entry>> groups=new LinkedHashMap<>();for(Entry e:source)groups.computeIfAbsent(logicalKey(e),key->new ArrayList<>()).add(e);
        List<Entry> result=new ArrayList<>();for(List<Entry> group:groups.values()){
            Entry best=group.stream().filter(e->manuallySelected((Video)e.media)).findFirst().orElseGet(()->Collections.min(group,(a,b)->BEST_FIRST.compare((Video)a.media,(Video)b.media)));
            Entry history=group.stream().filter(e->((Video)e.media).getResumeMs()!=0||((Video)e.media).isWatched()).max(Comparator.comparingLong(e->((Video)e.media).getLastPlayed())).orElse(null);
            if(history!=null&&group.size()>1){Video origin=(Video)history.media,target=(Video)best.media;target.setAutomaticResumeMs(PreviewSeriesJourney.completed(origin)?com.archos.mediacenter.video.player.PlayerActivity.LAST_POSITION_END:origin.getResumeMs());target.setRemoteResumeMs(origin.getRemoteResumeMs());best.playedAt=Math.max(best.playedAt,history.playedAt);}
            result.add(best);
        }return result;
    }
    /** Navigate distinct episodes, then select the deterministic best physical file. */
    public static Episode adjacentEpisode(List<Entry> source, long currentId, int direction) {
        Entry current = null;
        for (Entry entry : source) if (((Video)entry.media).getId() == currentId) { current = entry; break; }
        if (current == null || !(current.media instanceof Episode) || direction == 0) return null;
        Episode now = (Episode)current.media;
        Comparator<Episode> order = Comparator.comparingInt(Episode::getSeasonNumber).thenComparingInt(Episode::getEpisodeNumber);
        Episode selected = null;
        for (Entry entry : source) {
            if (entry.show != current.show || !(entry.media instanceof Episode)) continue;
            Episode candidate = (Episode)entry.media;
            int relative = order.compare(candidate, now);
            if (direction > 0 ? relative <= 0 : relative >= 0) continue;
            int comparison = selected == null ? 0 : order.compare(candidate, selected);
            if (selected == null || (direction > 0 ? comparison < 0 : comparison > 0)
                    || comparison == 0 && (manuallySelected(candidate)&&!manuallySelected(selected)||manuallySelected(candidate)==manuallySelected(selected)&&BEST_FIRST.compare(candidate, selected) < 0)) selected = candidate;
        }
        return selected;
    }
    private PreviewVariants(){}
}
