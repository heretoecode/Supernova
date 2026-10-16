package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.squareup.picasso.Picasso;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;

/** Shared landscape discovery/episode card with clean artwork and boundary focus. */
final class PreviewLandscapeCard extends LinearLayout {
    private final android.graphics.drawable.Drawable playGlyph;
    final ImageView image,availability;final TextView title,metadata;final ProgressBar progress;
    private final com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.Card visual;
    PreviewLandscapeCard(Context c){super(c);setOrientation(VERTICAL);setFocusable(true);setFocusableInTouchMode(true);setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);setClipChildren(false);setClipToPadding(false);
        visual=new com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.Card(c,com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.Style.CONTINUE);visual.setFocusable(false);visual.setFocusableInTouchMode(false);visual.setLandscapeAspect(16f/9);addView(visual,new LayoutParams(-1,-1));image=visual.image;title=visual.titleView();metadata=visual.subtitleView();progress=visual.progressView();
        FrameLayout body=(FrameLayout)visual.getChildAt(0);availability=new ImageView(c);availability.setImageDrawable(new PreviewIcon("play"));playGlyph=availability.getDrawable();availability.setVisibility(INVISIBLE);body.addView(availability,new FrameLayout.LayoutParams(dp(28),dp(28),Gravity.CENTER));
        setOnFocusChangeListener((v,focus)->{visual.setPresentationFocus(focus);availability.setVisibility(Boolean.TRUE.equals(availability.getTag())&&focus?VISIBLE:INVISIBLE);});
    }
    @Override protected void onMeasure(int widthSpec,int heightSpec){int width=MeasureSpec.getSize(widthSpec);visual.getChildAt(0).getLayoutParams().height=Math.round(width*9f/16);super.onMeasure(widthSpec,heightSpec);}
    void bind(String name,String detail,Uri art,boolean localIndicator){bind(name,detail,art,localIndicator,0,"details","landscape");}
    void bind(String name,String detail,Uri art,boolean localIndicator,long media,String surface,String type){title.setText(name);metadata.setText(detail);metadata.setVisibility(detail==null||detail.isEmpty()?GONE:VISIBLE);availability.setTag(localIndicator);availability.setVisibility(localIndicator&&hasFocus()?VISIBLE:INVISIBLE);ArtworkRequest.cancel(image);ArtworkRequest.cancel(availability);availability.setImageDrawable(playGlyph);availability.clearColorFilter();availability.setImageAlpha(255);image.setImageDrawable(null);if(art!=null)ArtworkRequest.load(image,art,media,surface,type,Picasso.get().load(art).resize(dp(352),dp(198)).centerCrop().noFade());setContentDescription(name+(detail==null?"":" · "+detail));}
    void release(){ArtworkRequest.cancel(image);ArtworkRequest.cancel(availability);image.setImageDrawable(null);}
    private TextView text(int size){TextView text=new TextView(getContext());text.setTextSize(size);text.setTextColor(0xffe1e9ef);text.setSingleLine(true);text.setEllipsize(android.text.TextUtils.TruncateAt.END);text.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));return text;}
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
