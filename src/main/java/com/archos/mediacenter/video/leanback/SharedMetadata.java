package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.*;
import android.text.style.*;
import android.view.View;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.details.PreviewMediaInfo;
import com.archos.mediacenter.video.utils.VideoMetadata;
import org.json.*;
import java.util.*;

/** Shared, compact metadata. Descriptive provider facts never imply local file capabilities. */
public final class SharedMetadata {
    public static final class Facts implements java.io.Serializable {
        public String certificate="",year="",genres="",context="";
        public double rating; public long votes;
        public Facts copy(){Facts copy=new Facts();copy.certificate=certificate;copy.year=year;copy.genres=genres;copy.context=context;copy.rating=rating;copy.votes=votes;copy.technical.addAll(technical);return copy;}
        public final List<String> technical=new ArrayList<>();
    }
    public static String runtime(long minutes){return minutes<=0?"":(minutes>=60?minutes/60+"h":"")+(minutes%60>0?(minutes>=60?" ":"")+minutes%60+"m":"");}
    public static String count(int value,String singular){return value<=0?"":value+" "+singular+(value==1?"":"s");}
    public static String votes(long count){return count>=1000000?String.format(Locale.UK,"%.1fm votes",count/1000000d):count>=1000?String.format(Locale.UK,"%.1fk votes",count/1000d):count+" votes";}
    public static CharSequence primary(Facts f) {
        SpannableStringBuilder row=new SpannableStringBuilder();
        for(String value:new String[]{f.certificate,f.year,f.genres,f.context,f.rating>0&&f.rating<=10?String.format(Locale.UK,"TMDB %.1f/10",f.rating):"",f.rating>0&&f.rating<=10&&f.votes>0?votes(f.votes):""}){
            if(value==null||value.trim().isEmpty())continue;
            if(row.length()>0)row.append(" · ");int start=row.length();row.append(value.trim());
            if(value.equals(f.certificate))row.setSpan(new AgeBadge(),start,row.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(value.endsWith(" votes"))row.setSpan(new ForegroundColorSpan(0xffb8c5ce),start,row.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }return row;
    }
    public static void bind(TextView primary,LinearLayout badges,Facts facts,int limit) {
        primary.setText(primary(facts));primary.setVisibility(primary.length()==0?View.GONE:View.VISIBLE);
        primary.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));primary.setTextColor(Color.WHITE);primary.setIncludeFontPadding(false);
        primary.setShadowLayer(2,0,1,Color.BLACK);
        badges.removeAllViews();Set<String> seen=new LinkedHashSet<>(facts.technical);
        for(String value:seen){if(badges.getChildCount()>=limit)break;if(value==null||value.trim().isEmpty())continue;
            TextView badge=SharedThreePanel.text(primary.getContext(),value,12);badge.setSingleLine(true);badge.setIncludeFontPadding(false);badge.setPadding(dp(primary.getContext(),6),dp(primary.getContext(),3),dp(primary.getContext(),6),dp(primary.getContext(),3));
            GradientDrawable border=new GradientDrawable();border.setColor(0x60071522);border.setCornerRadius(dp(primary.getContext(),4));border.setStroke(dp(primary.getContext(),1),0xbbb8c5ce);badge.setBackground(border);
            LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-2,-2);size.rightMargin=dp(primary.getContext(),6);badges.addView(badge,size);
        }badges.setVisibility(badges.getChildCount()==0?View.GONE:View.VISIBLE);
    }
    /** Card policy is shared by Home and Details; the lower facts retain their full data. */
    public static Facts cardFacts(Facts original,boolean home){
        Facts facts=original.copy();facts.votes=0;
        Set<String> genres=PreviewGenres.parse(facts.genres);List<String> compact=new ArrayList<>(genres);
        facts.genres=android.text.TextUtils.join(", ",compact.subList(0,Math.min(3,compact.size())));
        if(home){facts.technical.clear();facts.context="";}
        return facts;
    }
    public static void bindCard(TextView primary,LinearLayout badges,Facts original,boolean home){
        Facts facts=cardFacts(original,home);
        primary.setTextSize(16*TvLayout.scale(primary.getContext()));primary.setSingleLine(true);
        bind(primary,badges,facts,5);
    }
    public static Facts entry(Context c,Entry entry,boolean home) {
        Facts facts=new Facts();if(entry==null)return facts;
        facts.year=entry.year()>0?String.valueOf(entry.year()):"";facts.genres=entry.genres.replace('|',',');
        boolean episode=entry.media instanceof Episode;
        facts.certificate=""; // The legacy index loses territory; only the country-scoped cache can supply an age badge.
        facts.rating=0; // Indexed/NFO scores do not retain a verified provider.
        String kind=entry.media instanceof Movie?"movie":"tv";String section=episode?"season/"+((Episode)entry.media).getSeasonNumber():"";
        JSONObject cached=entry.onlineId>0?PreviewMetadataCache.read(c,kind,entry.onlineId,section):null,data=cached==null?null:cached.optJSONObject("data");
        if(episode&&data!=null){JSONArray episodes=data.optJSONArray("episodes");data=null;if(episodes!=null)for(int n=0;n<episodes.length();n++){JSONObject candidate=episodes.optJSONObject(n);if(candidate!=null&&candidate.optInt("episode_number")==((Episode)entry.media).getEpisodeNumber()){data=candidate;break;}}}
        if(data!=null){if(data.optDouble("vote_average",0)>0)facts.rating=data.optDouble("vote_average");facts.votes=data.optLong("vote_count");}
        if(!episode&&entry.onlineId>0){JSONObject ratings=PreviewMetadataCache.read(c,kind,entry.onlineId,kind.equals("tv")?"content_ratings":"release_dates");String local=certificate(ratings==null?null:ratings.optJSONObject("data"),com.archos.mediacenter.video.streaming.StreamingRepository.country(c));if(!local.isEmpty())facts.certificate=local;}
        if(!home){if(entry.media instanceof Tvshow)facts.context=join(count(entry.seasons,"Season"),count(entry.episodes,"Episode"));else if(entry.media instanceof Video)facts.context=runtime(((Video)entry.media).getDurationMs()/60000);}
        if(entry.media instanceof Video)facts.technical.addAll(technical(c,(Video)entry.media));return facts;
    }
    public static List<String> technical(Context c,Video video) {
        List<String> badges=new ArrayList<>();if(LibraryHealth.state(c,video)!=LibraryHealth.State.AVAILABLE)return badges;int w=video.getMeasuredWidth(),h=video.getMeasuredHeight();
        if(w>0&&h>0)badges.add(w>=3840||h>=2160?"4K UHD":w>=1728||h>=1040?"FHD":w>=1200||h>=720?"HD":"SD");
        String hdr=PreviewVariants.dynamicRange(c,video);if(!hdr.isEmpty())badges.add(hdr);
        VideoMetadata data=video.getMetadata();Set<String> measuredAudio=new LinkedHashSet<>();if(data!=null)for(int n=0;n<data.getAudioTrackNb();n++){VideoMetadata.AudioTrack track=data.getAudioTrack(n);if(track!=null){String value=PreviewMediaInfo.audioTrack(track.format,track.channels,0);if(!value.isEmpty())measuredAudio.add(value);}}
        String audio=measuredAudio.isEmpty()?PreviewMediaInfo.format(video.getCalculatedBestAudioFormat()):TextUtils.join(" / ",measuredAudio);if(!audio.isEmpty())badges.add(audio);
        boolean subtitles=false,sdh=false,captions=false,ad=false;
        if(data!=null){for(int n=0;n<data.getSubtitleTrackNb();n++){VideoMetadata.SubtitleTrack track=data.getSubtitleTrack(n);if(track!=null){subtitles=true;sdh|=(track.disposition&128)!=0;captions|=(track.disposition&(1<<16))!=0;}}
            for(int n=0;n<data.getAudioTrackNb();n++){VideoMetadata.AudioTrack track=data.getAudioTrack(n);if(track!=null)ad|=(track.disposition&256)!=0;}}
        if(subtitles)badges.add(subtitleLabel(sdh,captions));if(ad)badges.add("AD");for(String local:com.archos.mediacenter.video.utils.SubtitleDiscovery.localBadges(c,video))if(!badges.contains(local)&&!(local.equals("Subtitles")&&(badges.contains("SDH")||badges.contains("Captions")||badges.contains("Captions/SDH"))))badges.add(local);return badges;
    }
    /** FFmpeg's CAPTIONS flag verifies caption text; it does not establish a closed-caption transport codec. */
    public static String subtitleLabel(boolean sdh,boolean captions){return captions?(sdh?"Captions/SDH":"Captions"):sdh?"SDH":"Subtitles";}
    public static String certificate(JSONObject data,String country){
        JSONArray regions=data==null?null:data.optJSONArray("results");if(regions==null)return "";
        for(int i=0;i<regions.length();i++){JSONObject region=regions.optJSONObject(i);if(region==null||!country.equals(region.optString("iso_3166_1")))continue;
            String rating=region.optString("rating");if(!rating.isEmpty())return rating;
            JSONArray releases=region.optJSONArray("release_dates");String fallback="";if(releases!=null)for(int n=0;n<releases.length();n++){JSONObject release=releases.optJSONObject(n);if(release==null)continue;String value=release.optString("certification");if(value.isEmpty())continue;if(release.optInt("type")==3)return value;if(fallback.isEmpty())fallback=value;}return fallback;
        }return "";
    }
    public static String join(String... values){List<String> present=new ArrayList<>();for(String value:values)if(value!=null&&!value.isEmpty())present.add(value);return TextUtils.join(" · ",present);}
    private static int dp(Context c,int n){return PreviewDialog.dp(c,n);}
    private static final class AgeBadge extends ReplacementSpan {
        public int getSize(Paint p,CharSequence text,int start,int end,Paint.FontMetricsInt fm){return Math.round(p.measureText(text,start,end)+p.getTextSize()*.6f);}
        public void draw(Canvas c,CharSequence text,int start,int end,float x,int top,int y,int bottom,Paint p){Paint outline=new Paint(p);outline.setStyle(Paint.Style.STROKE);outline.setStrokeWidth(1);float inset=p.getTextSize()*.25f,width=getSize(p,text,start,end,null);c.drawRoundRect(x,y+p.ascent()-inset,x+width,y+p.descent()+inset,3,3,outline);c.drawText(text,start,end,x+p.getTextSize()*.3f,y,p);}
    }
    private SharedMetadata(){}
}
