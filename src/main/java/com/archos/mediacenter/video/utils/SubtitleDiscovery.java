package com.archos.mediacenter.video.utils;

import android.content.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.json.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/** Private persistent discovery cache. Preparing availability never downloads or claims a local track. */
public final class SubtitleDiscovery {
    public static final String ENABLED="supernova_subtitle_discovery",PREFETCH="supernova_subtitle_season_prefetch";
    private static final Object REQUEST_LOCK=new Object();
    private static final SubtitleDiscoveryPolicy.Queue QUEUE=new SubtitleDiscoveryPolicy.Queue();
    private static final ScheduledExecutorService WORK=Executors.newSingleThreadScheduledExecutor(r->{Thread t=new Thread(r,"SupernovaSubtitleDiscovery");t.setDaemon(true);return t;});
    private static ScheduledFuture<?> scheduled;private static String playingSeason="";
    public interface Provider {ArrayList<OpenSubtitlesSearchResult> search(Context c,OpenSubtitlesQueryParams query,String languages)throws IOException;}
    private static Provider provider=(c,query,languages)->{
        OpenSubtitlesApiHelper.getInstance();int resource=c.getResources().getIdentifier("opensubtitles_api_key","string",c.getPackageName());
        if(resource==0||c.getString(resource).trim().isEmpty())throw new IOException("Subtitle API configuration unavailable");
        OpenSubtitlesApiHelper.initializeDiscovery(c.getString(resource));return OpenSubtitlesApiHelper.searchSubtitle(query,languages);
    };
    public static void setProviderForTests(Provider replacement){provider=replacement;}
    private static SharedPreferences state(Context c){return c.getSharedPreferences("supernova-subtitle-discovery-v1",Context.MODE_PRIVATE);}
    private static boolean enabled(Context c){SharedPreferences prefs=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);return prefs.getBoolean(ENABLED,false)&&prefs.getBoolean("supernova_integration_opensubtitles",true)&&!com.archos.mediacenter.video.player.PrivateMode.isActive();}
    public static String languages(Context c){Set<String> requested=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getStringSet("languages_list",Collections.emptySet());TreeSet<String> values=new TreeSet<>();for(String language:requested)if(language!=null&&!language.isEmpty())values.add(language.toLowerCase(Locale.ROOT));if(values.isEmpty())values.add(Locale.getDefault().getLanguage());return android.text.TextUtils.join(",",values);}
    public static String key(OpenSubtitlesQueryParams query,String languages){
        String title=query.getTmdbId()!=null?"tmdb:"+query.getTmdbId():query.getImdbId()!=null?"imdb:"+query.getImdbId():"name:"+query.getFileName();String source=title+":"+query.isShow()+":"+query.getSeasonNumber()+":"+query.getEpisodeNumber()+":"+new TreeSet<>(Arrays.asList(languages.split(",")))+":"+(query.getFileHash()==null?"":query.getFileHash());
        try{byte[] hash=java.security.MessageDigest.getInstance("SHA-256").digest(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));StringBuilder result=new StringBuilder();for(byte value:hash)result.append(String.format(Locale.ROOT,"%02x",value));return result.toString();}catch(Exception impossible){throw new IllegalStateException(impossible);}
    }
    public static OpenSubtitlesQueryParams query(Entry entry){
        if(!(entry.media instanceof Video))return null;Video video=(Video)entry.media;OpenSubtitlesQueryParams query=new OpenSubtitlesQueryParams();query.setFileName(video instanceof Episode?((Episode)video).getShowName():video.getName());query.setFileLength(Math.max(0,video.getSize()));if(entry.onlineId>0)query.setTmdbId(String.valueOf(entry.onlineId));
        if(video instanceof Episode){Episode episode=(Episode)video;query.setIsShow(true);query.setSeasonNumber(episode.getSeasonNumber());query.setEpisodeNumber(episode.getEpisodeNumber());query.setShowTitle(episode.getShowName());}return query;
    }
    public static void offer(Context c,Entry entry,int priority){
        if(entry==null||!enabled(c)||!(entry.media instanceof Video)||LibraryHealth.state(c,(Video)entry.media)!=LibraryHealth.State.AVAILABLE)return;String languages=languages(c);if(localMatches(c,(Video)entry.media,languages))return;OpenSubtitlesQueryParams query=query(entry);if(query==null||query.getTmdbId()==null)return; // Speculation requires verified provider identity.
        String key=key(query,languages);Context app=c.getApplicationContext();WORK.execute(()->{if(!enabled(app)||cached(app,key,System.currentTimeMillis())!=null)return;QUEUE.offer(key,query,languages,priority);schedule(app,0);});
    }
    private static Entry target(Context c,Entry title,Snapshot library){if(title==null||title.media instanceof Video)return title;if(library==null)return null;List<Entry> episodes=new ArrayList<>();for(Entry episode:library.episodes)if(episode.show==title.show)episodes.add(episode);return PreviewSeriesJourney.select(c,episodes).episode;}
    public static void hero(Context c,Entry title,Snapshot library){offer(c,target(c,title,library),SubtitleDiscoveryPolicy.HERO);}
    public static void home(Context c,Snapshot library){if(!enabled(c)||library==null)return;List<Entry> continuing=new ArrayList<>(library.continuingMovies);continuing.addAll(library.continuingShows);continuing.sort(Comparator.comparingLong((Entry e)->e.playedAt).reversed());for(int n=0;n<Math.min(15,continuing.size());n++)offer(c,continuing.get(n),SubtitleDiscoveryPolicy.CONTINUING);PreviewHomeRows rows=new PreviewHomeRows(c);for(PreviewHomeRows.Row row:rows.rows)if(row.id.equals("watchnext")&&row.visible)for(Entry title:rows.members(row,library))offer(c,target(c,title,library),SubtitleDiscoveryPolicy.CONTINUING);}
    public static void details(Context c,Base media,Snapshot library){if(!enabled(c)||library==null||media==null)return;for(Entry entry:media instanceof Tvshow?library.shows:library.technical)if(entry.media instanceof Video&&media instanceof Video&&((Video)entry.media).getId()==((Video)media).getId()||entry.media instanceof Tvshow&&media instanceof Tvshow&&entry.show==((Tvshow)media).getTvshowId()){offer(c,target(c,entry,library),SubtitleDiscoveryPolicy.DETAILS);break;}}
    public static void playback(Context c,Video video,Snapshot library){
        if(!enabled(c)||video==null)return;Entry playing=null;if(library!=null)for(Entry entry:library.technical)if(((Video)entry.media).getId()==video.getId()){playing=entry;break;}if(playing==null)playing=new Entry(video,0,0,"");
        Entry selected=playing;WORK.execute(()->{String season=SubtitleDiscoveryPolicy.intentScope(selected);if(!season.equals(playingSeason)){QUEUE.clearSpeculation();playingSeason=season;}});offer(c,playing,SubtitleDiscoveryPolicy.PLAYBACK);
        if(library!=null&&androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean(PREFETCH,true))for(Entry entry:SubtitleDiscoveryPolicy.remainingSeason(c,playing,library.episodes))offer(c,entry,SubtitleDiscoveryPolicy.SEASON);
    }
    private static void schedule(Context app,long delay){if(scheduled!=null&&!scheduled.isDone()){if(delay>0)return;scheduled.cancel(false);}scheduled=WORK.schedule(()->drain(app),Math.max(0,delay),TimeUnit.MILLISECONDS);}
    private static void drain(Context app){
        if(!enabled(app))return;long now=System.currentTimeMillis();SharedPreferences state=state(app);long due=Math.max(state.getLong("provider_until",0),state.getLong("last_request",0)+SubtitleDiscoveryPolicy.SPACING);
        if(due>now){scheduled=null;schedule(app,due-now);return;}SubtitleDiscoveryPolicy.Job job=QUEUE.take();if(job==null)return;
        if(!job.languages.equals(languages(app))){scheduled=null;if(QUEUE.size()>0)schedule(app,0);return;}
        try{synchronized(REQUEST_LOCK){if(cached(app,job.key,System.currentTimeMillis())==null)fetch(app,job.query,job.languages,job.key);}}catch(IOException unavailable){/* Persistent provider backoff retains this intent without a burst. */QUEUE.offer(job.key,job.query,job.languages,job.priority);}
        scheduled=null;if(QUEUE.size()>0)schedule(app,SubtitleDiscoveryPolicy.SPACING);
    }
    public static ArrayList<OpenSubtitlesSearchResult> manual(Context c,OpenSubtitlesQueryParams query,String languages)throws IOException{
        if(query==null)throw new IOException("Subtitle identity unavailable");if(!androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean("supernova_integration_opensubtitles",true))throw new IOException("Subtitle integration disabled");if(com.archos.mediacenter.video.player.PrivateMode.isActive())return OpenSubtitlesApiHelper.searchSubtitle(query,languages);String key=key(query,languages);QUEUE.remove(key);
        synchronized(REQUEST_LOCK){ArrayList<OpenSubtitlesSearchResult> cache=cached(c,key,System.currentTimeMillis());if(cache!=null){OpenSubtitlesApiHelper.cachedDiscoverySucceeded();return cache;}if(state(c).getLong("provider_until",0)>System.currentTimeMillis())throw new IOException("Subtitle provider is temporarily rate limited");return fetch(c,query,languages,key);}
    }
    private static ArrayList<OpenSubtitlesSearchResult> fetch(Context c,OpenSubtitlesQueryParams query,String languages,String key)throws IOException{
        SharedPreferences state=state(c);long now=System.currentTimeMillis();state.edit().putLong("last_request",now).commit();
        try{synchronized(OpenSubtitlesApiHelper.class){ArrayList<OpenSubtitlesSearchResult> results=provider.search(c,query,languages);int code=OpenSubtitlesApiHelper.getLastQueryResult();long providerUntil=OpenSubtitlesApiHelper.discoveryBlockedUntil();if(providerUntil>now)state.edit().putLong("provider_until",providerUntil).commit();
            if(code!=OpenSubtitlesApiHelper.RESULT_CODE_OK||results==null)throw new IOException("Subtitle discovery unavailable");JSONArray values=new JSONArray();for(OpenSubtitlesSearchResult result:results)values.put(new JSONObject().put("id",result.getId()).put("file",result.getFileId()).put("name",result.getFileName()).put("language",result.getLanguage()).put("release",result.getRelease()).put("movie",result.getMovieName()).put("parent",result.getParentTitle()).put("sdh",result.getHearingImpaired()).put("hash",Boolean.TRUE.equals(result.getMoviehashMatch())).put("season",result.getSeasonNumber()).put("episode",result.getEpisodeNumber()).put("type",result.getFeatureType()));
            state.edit().putString("cache:"+key,new JSONObject().put("expires",now+(results.isEmpty()?SubtitleDiscoveryPolicy.NEGATIVE_TTL:SubtitleDiscoveryPolicy.POSITIVE_TTL)).put("values",values).toString()).putInt("failures",0).commit();return results;}
        }catch(IOException|JSONException failure){int failures=state.getInt("failures",0)+1;state.edit().putInt("failures",failures).putLong("provider_until",SubtitleDiscoveryPolicy.backoff(now,failures,Math.max(state.getLong("provider_until",0),OpenSubtitlesApiHelper.discoveryBlockedUntil()))).commit();throw new IOException("Subtitle discovery postponed",failure);}
    }
    private static ArrayList<OpenSubtitlesSearchResult> cached(Context c,String key,long now){try{JSONObject cache=new JSONObject(state(c).getString("cache:"+key,"{}"));if(cache.optLong("expires")<=now)return null;JSONArray values=cache.optJSONArray("values");if(values==null)return null;ArrayList<OpenSubtitlesSearchResult> results=new ArrayList<>();for(int i=0;i<values.length();i++){JSONObject value=values.getJSONObject(i);OpenSubtitlesSearchResult result=new OpenSubtitlesSearchResult(value.optString("file"),value.optString("name"),value.optString("language"));result.setId(value.optString("id"));result.setRelease(value.optString("release"));result.setMovieName(value.optString("movie"));result.setParentTitle(value.optString("parent"));if(value.has("sdh"))result.setHearingImpaired(value.optBoolean("sdh"));result.setMoviehashMatch(value.optBoolean("hash"));result.setSeasonNumber(value.optInt("season"));result.setEpisodeNumber(value.optInt("episode"));result.setFeatureType(value.optString("type"));results.add(result);}return results;}catch(JSONException invalid){return null;}}
    public static String fileKey(Video video){OpenSubtitlesQueryParams query=new OpenSubtitlesQueryParams(video.getFilePath()+":"+video.getSize()+":"+video.getDurationMs()+":"+(video.getFileUri()!=null&&"file".equals(video.getFileUri().getScheme())?new File(video.getFileUri().getPath()).lastModified():0),null);return key(query,"");}
    public static void recordLocal(Context c,Video video,VideoMetadata metadata){
        Set<String> languages=new TreeSet<>();boolean subtitles=false,sdh=false,captions=false,ad=false;if(metadata!=null){for(int n=0;n<metadata.getSubtitleTrackNb();n++){VideoMetadata.SubtitleTrack track=metadata.getSubtitleTrack(n);if(track!=null){subtitles=true;if(track.language!=null)languages.add(normalise(track.language));sdh|=(track.disposition&128)!=0;captions|=(track.disposition&(1<<16))!=0;}}for(int n=0;n<metadata.getAudioTrackNb();n++){VideoMetadata.AudioTrack track=metadata.getAudioTrack(n);if(track!=null)ad|=(track.disposition&256)!=0;}}
        android.net.Uri uri=video.getFileUri();if(uri!=null&&"file".equals(uri.getScheme())){File media=new File(uri.getPath());String name=media.getName();int dot=name.lastIndexOf('.');String base=dot>0?name.substring(0,dot):name;File parent=media.getParentFile();File[] files=parent==null?null:parent.listFiles();if(files!=null)for(File file:files){String sidecar=file.getName();if(file.isFile()&&(sidecar.equals(base+".srt")||sidecar.equals(base+".vtt")||sidecar.startsWith(base+".")&&sidecar.matches(".*\\.(srt|vtt|ass|ssa|sub|idx)$"))){subtitles=true;String rest=sidecar.substring(Math.min(sidecar.length(),base.length()+1));int extension=rest.indexOf('.');if(extension>0){String language=rest.substring(0,extension);if(language.matches("[A-Za-z]{2,3}"))languages.add(normalise(language));}}}}
        state(c).edit().putStringSet("local:"+fileKey(video),languages).putBoolean("subs:"+fileKey(video),subtitles).putBoolean("sdh:"+fileKey(video),sdh).putBoolean("captions:"+fileKey(video),captions).putBoolean("ad:"+fileKey(video),ad).commit();
    }
    private static String normalise(String language){String value=language.toLowerCase(Locale.ROOT);for(String iso:Locale.getISOLanguages())try{if(new Locale(iso).getISO3Language().equals(value))return iso;}catch(Exception unknown){}return value;}
    public static boolean localMatches(Context c,Video video,String requested){Set<String> available=state(c).getStringSet("local:"+fileKey(video),Collections.emptySet());return available.containsAll(Arrays.asList(requested.split(",")));}
    public static List<String> localBadges(Context c,Video video){List<String> badges=new ArrayList<>();SharedPreferences state=state(c);String key=fileKey(video);if(state.getBoolean("subs:"+key,false))badges.add(SharedMetadata.subtitleLabel(state.getBoolean("sdh:"+key,false),state.getBoolean("captions:"+key,false)));if(state.getBoolean("ad:"+key,false))badges.add("AD");return badges;}
    private SubtitleDiscovery(){}
}
