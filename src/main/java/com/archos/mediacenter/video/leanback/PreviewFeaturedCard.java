package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.squareup.picasso.Picasso;
import java.util.*;

/** Fixed exposed-card composition from HOME §11. Neighbours are affordances, never focus peers. */
final class PreviewFeaturedCard extends FrameLayout {
    PreviewFeaturedCard(Context c,List<Entry> entries,int index,Runnable info,Runnable play){
        super(c);setClipChildren(false);setClipToPadding(false);setTag("semantic:home.featured");
        int current=entries.isEmpty()?0:Math.floorMod(index,entries.size());Entry entry=entries.isEmpty()?null:entries.get(current);
        for(int direction:new int[]{-1,1})if(entries.size()>1){
            ImageView neighbour=art(c,entries.get(Math.floorMod(current+direction,entries.size())));
            neighbour.setAlpha(1f);neighbour.setTag(direction<0?"semantic:featured.previous":"semantic:featured.next");
            LayoutParams size=new LayoutParams(-1,-1);size.gravity=direction<0?Gravity.LEFT:Gravity.RIGHT;addView(neighbour,size);
        }
        CinematicCard card=new CinematicCard(c);card.setTag("semantic:featured.active");rounded(card,c);card.setElevation(dp(8));
        addView(card,new LayoutParams(-1,-1,Gravity.CENTER));card.addView(art(c,entry),new LayoutParams(-1,-1));

        LinearLayout copy=new LinearLayout(c);copy.setOrientation(LinearLayout.VERTICAL);copy.setTag("featured.copy");copy.setPadding(dp(24),dp(22),dp(18),dp(18));card.addView(copy,new LayoutParams(-1,-1));
        TextView title=text(entry==null?"":PreviewPages.displayName(entry),30);title.setTag("semantic:featured.title");title.setSingleLine(true);title.setShadowLayer(dp(2),0,dp(1),Color.BLACK);title.setTypeface(null,android.graphics.Typeface.BOLD);copy.addView(title,new LinearLayout.LayoutParams(-1,dp(86)));if(entry!=null)OfficialTitleArtwork.bindStable(title,entry.media);else title.setForeground(new HomePlaceholder.Skeleton());
        TextView metadata=text("",18);metadata.setMaxLines(2);metadata.setTag("semantic:featured.metadata");metadata.setPadding(0,dp(4),0,dp(4));LinearLayout.LayoutParams metadataSize=new LinearLayout.LayoutParams(-1,-2);metadataSize.topMargin=dp(18);copy.addView(metadata,metadataSize);
        LinearLayout badges=new LinearLayout(c);LinearLayout.LayoutParams badgeSize=new LinearLayout.LayoutParams(-1,-2);badgeSize.topMargin=dp(8);copy.addView(badges,badgeSize);SharedMetadata.bindCard(metadata,badges,entry!=null&&entry.homeMetadata!=null?entry.homeMetadata:SharedMetadata.entry(c,entry,true),true);
        String plot=entry==null?"":entry.media instanceof Tvshow?((Tvshow)entry.media).getPlot():entry.media instanceof Video?((Video)entry.media).getDescriptionBody():"";
        String tagline=entry==null?"":entry.tagline;
        if(tagline!=null&&!tagline.trim().isEmpty())plot=tagline;
        if(plot==null)plot="";
        TextView synopsis=text(plot,16);synopsis.setSingleLine(true);if(plot.trim().isEmpty())synopsis.setVisibility(GONE);synopsis.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));synopsis.setShadowLayer(dp(2),0,dp(1),Color.BLACK);synopsis.setTag("semantic:featured.synopsis");LinearLayout.LayoutParams synopsisSize=new LinearLayout.LayoutParams(-1,dp(26));synopsisSize.topMargin=dp(12);copy.addView(synopsis,synopsisSize);copy.addView(new View(c),new LinearLayout.LayoutParams(1,0,1));
        LinearLayout actions=new LinearLayout(c);actions.setClipChildren(false);copy.addView(actions,new LinearLayout.LayoutParams(-1,dp(44)));
        TextView more=action("More Info",info);more.setTag("hero:info");actions.addView(more,new LinearLayout.LayoutParams(-2,-1));
    }
    @Override protected void onMeasure(int w,int h){
        int width=MeasureSpec.getSize(w);for(int i=0;i<getChildCount();i++){View child=getChildAt(i);LayoutParams p=(LayoutParams)child.getLayoutParams();boolean active="semantic:featured.active".equals(child.getTag());p.width=Math.round(width*(active?.86f:.86f));p.height=-1;p.gravity=(active?Gravity.CENTER:p.gravity|Gravity.CENTER_VERTICAL);if(!active)child.setTranslationX((p.gravity&Gravity.HORIZONTAL_GRAVITY_MASK)==Gravity.LEFT?-width*.80f:width*.80f);}
        View active=findViewWithTag("semantic:featured.active");if(active instanceof ViewGroup){View copy=active.findViewWithTag("featured.copy");copy.getLayoutParams().width=Math.round(width*.86f*.55f);}
        super.onMeasure(w,h);
    }
    private ImageView art(Context c,Entry e){
        android.net.Uri uri=e==null?null:e.backdrop!=null?e.backdrop:e.media.getPosterUri();boolean backdrop=e!=null&&e.backdrop!=null;
        ImageView image=new androidx.appcompat.widget.AppCompatImageView(c){
            private boolean detached;
            @Override protected void onAttachedToWindow(){super.onAttachedToWindow();if(detached&&getDrawable()==null&&uri!=null)loadArtwork(this,uri,backdrop);detached=false;}
            @Override protected void onDetachedFromWindow(){detached=true;super.onDetachedFromWindow();}
            @Override protected void onDraw(android.graphics.Canvas canvas){int saved=canvas.save();android.graphics.Path mask=new android.graphics.Path();mask.addRoundRect(new android.graphics.RectF(0,0,getWidth(),getHeight()),dp(12),dp(12),android.graphics.Path.Direction.CW);canvas.clipPath(mask);super.onDraw(canvas);canvas.restoreToCount(saved);}
        };image.setScaleType(backdrop?ImageView.ScaleType.CENTER_CROP:ImageView.ScaleType.FIT_CENTER);rounded(image,c);if(uri!=null)loadArtwork(image,uri,backdrop);return image;
    }
    private void loadArtwork(ImageView image,android.net.Uri uri,boolean backdrop){com.squareup.picasso.RequestCreator request=Picasso.get().load(uri).resize(dp(960),dp(540)).noFade();if(backdrop)request.centerCrop();else request.centerInside();com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(image,uri,0,"home.featured",backdrop?"backdrop":"poster",request,true);}
    void releaseArtwork(){cancel(this);}
    private void rounded(View v,Context c){GradientDrawable bg=new GradientDrawable();bg.setColor(0xff07131f);bg.setCornerRadius(dp(12));v.setBackground(bg);v.setClipToOutline(true);}
    private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextSize(size);t.setTextColor(Color.WHITE);t.setIncludeFontPadding(false);t.setShadowLayer(dp(3),0,dp(1),Color.BLACK);t.setEllipsize(android.text.TextUtils.TruncateAt.END);return t;}
    private TextView action(String label,Runnable run){return TvButton.create(getContext(),label,run,false);}
    @Override protected void onDetachedFromWindow(){cancel(this);super.onDetachedFromWindow();}
    private void cancel(View v){if(v instanceof ImageView)com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel((ImageView)v);if(v instanceof ViewGroup)for(int i=0;i<((ViewGroup)v).getChildCount();i++)cancel(((ViewGroup)v).getChildAt(i));}
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
