package com.archos.mediacenter.video.player;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import java.util.*;

/** Read-only accurate boundaries; transport and D-pad focus remain owned by the real slider. */
public final class ChapterMarkers extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<PlaybackChapters.Chapter> chapters=Collections.emptyList();
    private long duration;
    public ChapterMarkers(Context context,AttributeSet attributes){super(context,attributes);setFocusable(false);setClickable(false);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);paint.setColor(0xffecf4fa);}
    public void bind(List<PlaybackChapters.Chapter> chapters,long duration,int left,int right){this.chapters=chapters==null?Collections.emptyList():chapters;this.duration=duration;setPadding(left,0,right,0);setVisibility(duration>0&&!this.chapters.isEmpty()?VISIBLE:GONE);invalidate();}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);if(duration<=0)return;float width=getWidth()-getPaddingLeft()-getPaddingRight(),middle=getHeight()/2f,half=3*getResources().getDisplayMetrics().density;paint.setStrokeWidth(Math.max(1,getResources().getDisplayMetrics().density));for(PlaybackChapters.Chapter chapter:chapters)if(chapter.startMs>0&&chapter.startMs<duration){float x=getPaddingLeft()+width*chapter.startMs/duration;canvas.drawLine(x,middle-half,x,middle+half,paint);}}
}
