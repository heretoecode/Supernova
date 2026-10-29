package com.archos.mediacenter.video.player;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.leanback.PreviewMetadataCache;
import com.archos.mediacenter.video.leanback.SharedMetadata;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.json.JSONObject;

/** Passive FD-001 overlay, using only territory-scoped cached provider classification. */
final class PlaybackAgeOverlay extends View {
    static final long FADE_MS=500,DWELL_MS=10000;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SurfaceController surface;
    private final Runnable fade=this::fadeOut;
    private String identity="",classification="",territory="";private boolean series,output,consumed;private int generation;private long outputAt;
    PlaybackAgeOverlay(Context c,SurfaceController surface){super(c);this.surface=surface;setFocusable(false);setClickable(false);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);setVisibility(GONE);setTag("playback.age.classification");}
    void bind(VideoDbInfo info){
        String id=info==null?null:info.isShow?info.scraperShowId:info.scraperMovieId;
        String key=info==null?"":info.id+":"+info.uri+":"+info.isShow+":"+id+":"+(PlayerService.sPlayerService==null?0:PlayerService.sPlayerService.playbackLaunchGeneration());
        if(key.equals(identity))return;identity=key;int expected=++generation;animate().cancel();setVisibility(GONE);classification="";consumed=false;output=false;removeCallbacks(fade);
        if(info==null||id==null||!id.matches("[1-9][0-9]{0,17}"))return;
        series=info.isShow;territory=StreamingRepository.country(getContext());String country=territory,kind=series?"tv":"movie";long providerId;try{providerId=Long.parseLong(id);}catch(NumberFormatException invalid){return;}
        StreamingRepository.IO.submit(()->{JSONObject cached=PreviewMetadataCache.read(getContext(),kind,providerId,kind.equals("tv")?"content_ratings":"release_dates");String value=certificate(cached==null?null:cached.optJSONObject("data"),country);post(()->{if(expected!=generation)return;classification=valid(value)?value:"";showIfReady();});});
    }
    static String certificate(JSONObject data,String country){
        org.json.JSONArray regions=data==null?null:data.optJSONArray("results");
        if(regions==null||country==null||country.isEmpty())return "";
        java.util.Set<String> primary=new java.util.HashSet<>(),fallback=new java.util.HashSet<>();
        for(int n=0;n<regions.length();n++){
            JSONObject region=regions.optJSONObject(n);if(region==null||!country.equalsIgnoreCase(region.optString("iso_3166_1")))continue;
            String rating=region.optString("rating").trim();if(valid(rating))primary.add(rating);
            org.json.JSONArray releases=region.optJSONArray("release_dates");if(releases==null)continue;
            for(int i=0;i<releases.length();i++){JSONObject release=releases.optJSONObject(i);if(release==null)continue;String value=release.optString("certification").trim();if(!valid(value))continue;if(release.optInt("type")==3)primary.add(value);else fallback.add(value);}
        }
        java.util.Set<String> chosen=primary.isEmpty()?fallback:primary;
        return chosen.size()==1?chosen.iterator().next():"";
    }
    static boolean valid(String value){if(value==null)return false;String text=value.trim();return text.matches("[A-Za-z0-9][A-Za-z0-9 +/.-]{0,23}")&&!text.toUpperCase(java.util.Locale.ROOT).matches("N/?A|NR|UNRATED|NOT(?: [A-Z]+)* RATED|UNKNOWN|NULL|NONE|TBC|TBD|NO RATING AVAILABLE");}
    void playbackStarted(){if(!output)outputAt=android.os.SystemClock.uptimeMillis();output=true;showIfReady();}
    private void showIfReady(){
        if(!output||consumed||classification.isEmpty()||!isAttachedToWindow())return;
        long remaining=DWELL_MS-(android.os.SystemClock.uptimeMillis()-outputAt);
        consumed=true;if(remaining<=0)return;
        setAlpha(1);setVisibility(VISIBLE);bringToFront();postDelayed(fade,remaining);invalidate();
    }
    private void fadeOut(){animate().alpha(0).setDuration(FADE_MS).withEndAction(()->setVisibility(GONE)).start();}
    void stop(){generation++;animate().cancel();removeCallbacks(fade);setVisibility(GONE);consumed=true;}
    @Override protected void onDetachedFromWindow(){stop();super.onDetachedFromWindow();}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);if(classification.isEmpty())return;float density=getResources().getDisplayMetrics().scaledDensity;int videoW=surface==null?getWidth():Math.min(getWidth(),surface.getViewWidth()),videoH=surface==null?getHeight():Math.min(getHeight(),surface.getViewHeight());if(videoW<=0)videoW=getWidth();if(videoH<=0)videoH=getHeight();float x=(getWidth()-videoW)/2f+videoW*.05f,y=(getHeight()-videoH)/2f+videoH*.08f;paint.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));paint.setTextSize(27*density);float baseline=y-paint.ascent();paint.setStyle(Paint.Style.FILL);paint.setColor(0xffde2438);canvas.drawRect(x,y,x+2*density,baseline+paint.descent(),paint);x+=12*density;paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(.8f*density);paint.setColor(Color.BLACK);canvas.drawText(classification,x,baseline,paint);paint.setStyle(Paint.Style.FILL);paint.setColor(Color.WHITE);canvas.drawText(classification,x,baseline,paint);}
}
