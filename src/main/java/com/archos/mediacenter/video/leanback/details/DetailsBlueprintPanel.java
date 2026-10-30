package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;

/** FD-033 v3 text/geometry contract. Six-region composition; Reception may expose the read-only review reader. */
final class DetailsBlueprintPanel extends LinearLayout {
    private final Map<String,TextView> keyValues=new LinkedHashMap<>(),technicalValues=new LinkedHashMap<>();
    private TextView synopsisView;private LinearLayout upperRow,lowerRow;
    private static final String[][] KEY_FIELDS={{"Original Title","Original title","Series original title"},{"Also Known As","Alternative names"},{"Release Date","Release / first air date","Air date","Year"},{"Runtime","Runtime"},{"Genres","Genres"},{"Country","Countries"},{"Language","Language"},{"Production Company","Studio / Network"},{"Budget","Budget (TMDb)"},{"Revenue","Box office (TMDb)"}};
    private static final String[][] TECH_FIELDS={{"Video","Video","Resolution","Video codec","File formats"},{"Audio","Audio tracks","Audio"},{"Container","Container"},{"Size","File size","Library size"},{"Source","Source","Storage locations"},{"Added","Added"},{"Path","Location"}};
    void updateText(String synopsis,Map<String,String> facts,Map<String,String> technical){
        String value=synopsis==null||synopsis.trim().isEmpty()?"Unavailable":synopsis;
        if(!value.contentEquals(synopsisView.getText()))synopsisView.setText(value);
        updateValues(keyValues,KEY_FIELDS,facts);updateValues(technicalValues,TECH_FIELDS,technical);
    }
    private void updateValues(Map<String,TextView> bound,String[][] fields,Map<String,String> values){for(String[] field:fields){
        TextView view=bound.get(field[0]);String value=first(values,Arrays.copyOfRange(field,1,field.length));
        if(!value.contentEquals(view.getText())){view.setText(value);((View)view.getParent()).setContentDescription(field[0]+": "+value);}
    }}
    private void bindFacts(LinearLayout parent,String[][] fields,Map<String,String> values,Map<String,TextView> bound){for(String[] field:fields){fact(parent,field[0],first(values,Arrays.copyOfRange(field,1,field.length)));bound.put(field[0],(TextView)((LinearLayout)parent.getChildAt(parent.getChildCount()-1)).getChildAt(1));}}

    DetailsBlueprintPanel(Context c,String synopsis,Map<String,String> facts,Map<String,String> technical,Map<String,String> existingReception,View cast,View crew,OmdbSupplement.Ratings supplementary){
        this(c,synopsis,facts,technical,existingReception,cast,crew,supplementary,null);
    }
    DetailsBlueprintPanel(Context c,String synopsis,Map<String,String> facts,Map<String,String> technical,Map<String,String> existingReception,View cast,View crew,OmdbSupplement.Ratings supplementary,Runnable reviews){
        super(c);setOrientation(VERTICAL);setTag("details.blueprint.v3");setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);setPadding(dp(10),dp(8),dp(10),dp(8));
        LinearLayout upper=row(.56f),lower;upperRow=upper;
        LinearLayout description=region(upper,"Description",.378f);TextView copy=label(synopsis==null||synopsis.trim().isEmpty()?"Unavailable":synopsis,13,false);copy.setTag("details.full.synopsis");copy.setGravity(Gravity.CENTER);copy.setLineSpacing(dp(3),1);copy.setEllipsize(android.text.TextUtils.TruncateAt.END);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(copy,DetailsDimensions.sp(c,11),DetailsDimensions.sp(c,14),1,android.util.TypedValue.COMPLEX_UNIT_SP);description.addView(copy,new LayoutParams(-1,0,1));synopsisView=copy;copy.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{int lines=Math.max(1,(b-t-copy.getPaddingTop()-copy.getPaddingBottom())/Math.max(1,copy.getLineHeight()));if(copy.getMaxLines()!=lines)copy.setMaxLines(lines);});
        boolean suppliedData=false;for(int slot=0;slot<4;slot++)suppliedData|=!supplementary.value(slot).isEmpty();
        boolean hasReception=existingReception.containsKey("TMDB Rating")||existingReception.containsKey("Indexed Rating")||suppliedData;
        LinearLayout key=region(upper,"Key Information",hasReception?.328f:.622f);
        bindFacts(key,KEY_FIELDS,facts,keyValues);
        if(hasReception){
        LinearLayout reception=region(upper,"Reception",.294f);
        String existing=existingReception.containsKey("TMDB Rating")?"TMDb "+existingReception.get("TMDB Rating"):existingReception.containsKey("Indexed Rating")?"Indexed rating "+existingReception.get("Indexed Rating"):"";
        if(!existing.isEmpty()){
            TextView score=label(existing,16,true);score.setTag("details.reception.primary");score.setSingleLine(false);score.setMaxLines(2);score.setEllipsize(android.text.TextUtils.TruncateAt.END);reception.addView(score,new LayoutParams(-1,0,1));
            if(existingReception.containsKey("TMDb votes")){TextView votes=label(existingReception.get("TMDb votes")+" votes",11,false);votes.setTextColor(0xffb9c7d2);reception.addView(votes,new LayoutParams(-1,-2));}
        }
        if(suppliedData){TextView attribution=label("Supplementary data via OMDb",9,false);attribution.setSingleLine(true);auto(attribution,6,9);reception.addView(attribution,new LayoutParams(-1,dp(15)));}
        String[] providers={"IMDb","Rotten Tomatoes","Metacritic","Awards"};
        int shown=0;for(int n=0;n<providers.length;n++){
            if(supplementary.value(n).isEmpty())continue;
            if(shown++>0){rule(reception,false);if(getResources().getConfiguration().fontScale>1.2f){LayoutParams divider=(LayoutParams)reception.getChildAt(reception.getChildCount()-1).getLayoutParams();divider.topMargin=dp(2);divider.bottomMargin=dp(2);}}
            LinearLayout rating=new LinearLayout(c);rating.setGravity(Gravity.CENTER_VERTICAL);rating.setBaselineAligned(false);reception.addView(rating,new LayoutParams(-1,0,1));
            rating.setTag("details.reception.slot."+n);
            if(n==0||n==3)rating.addView(providerMark(n,providers[n]),new LayoutParams(0,-1,.34f));
            else{TextView provider=label(providers[n],11,true);provider.setGravity(Gravity.CENTER);provider.setMaxLines(2);provider.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(provider,6,11);rating.addView(provider,new LayoutParams(0,-1,.34f));}
            rating.addView(ratingValue(n,supplementary),new LayoutParams(0,-1,.66f));
        }
        if(reviews!=null){
            TextView heading=(TextView)reception.getChildAt(0);reception.removeView(heading);
            LinearLayout header=new LinearLayout(c);header.setGravity(Gravity.CENTER_VERTICAL);header.addView(heading,new LayoutParams(0,-1,1));
            TextView read=TvButton.create(c,"Read reviews",reviews,false);read.setTag("details.reviews");read.setCompoundDrawables(null,null,null,null);read.setMinimumHeight(dp(27));read.setTextSize(DetailsDimensions.sp(c,11));read.setPadding(dp(6),dp(2),dp(6),dp(2));header.addView(read,new LayoutParams(-2,-1));reception.addView(header,0,new LayoutParams(-1,dp(27)));
        }
        }
        rule(this,false);lower=row(.44f);lowerRow=lower;
        attach(region(lower,"Cast",.344f),cast);attach(region(lower,"Crew",.353f),crew);
        LinearLayout tech=region(lower,"Technical Information",.303f);
        bindFacts(tech,TECH_FIELDS,technical,technicalValues);
    }
    @Override protected void onMeasure(int widthSpec,int heightSpec){
        int available=MeasureSpec.getSize(heightSpec)-getPaddingTop()-getPaddingBottom()-dp(9);
        if(available>0){
            // Reserve the shared gap without shrinking accessible portraits at enlarged text.
            float people=Math.min(.55f,Math.max(.44f+dp(8)/(float)available,(DetailsPeopleGrid.minimumHeight(getContext())+dp(45))/(float)available));
            ((LayoutParams)lowerRow.getLayoutParams()).weight=people;((LayoutParams)upperRow.getLayoutParams()).weight=1f-people;
        }
        super.onMeasure(widthSpec,heightSpec);
    }
    private View providerMark(int row,String name){
        FrameLayout mark=new FrameLayout(getContext()){@Override protected void onMeasure(int w,int h){int width=MeasureSpec.getSize(w),height=MeasureSpec.getSize(h);int side=Math.min(dp(34),Math.min(width>0?width:dp(34),height>0?height:dp(34)));if(getChildCount()>0){getChildAt(0).getLayoutParams().width=side;getChildAt(0).getLayoutParams().height=side;}super.onMeasure(w,h);}};
        ImageView logo=new ImageView(getContext());if(row==0)logo.setImageResource(com.archos.mediacenter.video.R.drawable.imdb_logo);else{PreviewIcon trophy=new PreviewIcon("trophy");trophy.setColorFilter(new android.graphics.PorterDuffColorFilter(0xffe5a500,android.graphics.PorterDuff.Mode.SRC_IN));logo.setImageDrawable(trophy);}
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);logo.setContentDescription(name);mark.addView(logo,new FrameLayout.LayoutParams(dp(34),dp(34),Gravity.CENTER));return mark;
    }
    private View ratingValue(int row,OmdbSupplement.Ratings ratings){
        LinearLayout box=new LinearLayout(getContext());box.setOrientation(VERTICAL);String supplied=ratings.value(row);
        if(supplied.isEmpty()||row==3){TextView body=label(supplied.isEmpty()?(row==3?"Unavailable":"No rating available"):supplied,11,false);body.setGravity(Gravity.CENTER_VERTICAL);body.setMaxLines(row==3?3:2);body.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(body,6,11);box.addView(body,new LayoutParams(-1,-1));return box;}
        TextView score=label(supplied,16,true);score.setGravity(Gravity.CENTER_VERTICAL);score.setSingleLine(true);score.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(score,8,16);
        int unit=supplied.indexOf('/');if(unit<0&&supplied.endsWith("%"))unit=supplied.length()-1;
        if(unit>=0){android.text.SpannableString styled=new android.text.SpannableString(supplied);styled.setSpan(new android.text.style.RelativeSizeSpan(11f/16f),unit,supplied.length(),android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);styled.setSpan(new android.text.style.StyleSpan(Typeface.NORMAL),unit,supplied.length(),android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);score.setText(styled);}
        boolean support=row==0&&!ratings.imdbVotes.isEmpty();box.addView(score,new LayoutParams(-1,0,support?.6f:1f));
        if(support){TextView count=label(votes(ratings.imdbVotes),10,false);count.setTextColor(0xffb9c7d2);count.setGravity(Gravity.CENTER_VERTICAL);count.setSingleLine(true);count.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(count,6,10);box.addView(count,new LayoutParams(-1,0,.4f));}
        return box;
    }
    private void auto(TextView view,int min,int max){androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(view,DetailsDimensions.sp(getContext(),min),DetailsDimensions.sp(getContext(),max),1,android.util.TypedValue.COMPLEX_UNIT_SP);}
    private void attach(LinearLayout parent,View content){if(content.getParent() instanceof ViewGroup)((ViewGroup)content.getParent()).removeView(content);content.setVisibility(VISIBLE);content.setPadding(0,dp(8),0,0);parent.addView(content,new LayoutParams(-1,0,1));}
    private LinearLayout row(float weight){LinearLayout row=new LinearLayout(getContext());row.setBaselineAligned(false);addView(row,new LayoutParams(-1,0,weight));return row;}
    private LinearLayout region(LinearLayout row,String title,float weight){if(row.getChildCount()>0)rule(row,true);LinearLayout region=new LinearLayout(getContext());region.setOrientation(VERTICAL);region.setTag(title.equals("Key Information")||title.equals("Reception")||title.equals("Technical Information")?"semantic:details.panel."+title.toLowerCase(Locale.ROOT).replace(' ','.'):"details.region."+title);region.setPadding(dp(6),dp(4),dp(6),dp(4));row.addView(region,new LayoutParams(0,-1,weight));TextView heading=label(title,15,true);heading.setSingleLine(true);heading.setEllipsize(android.text.TextUtils.TruncateAt.END);region.addView(heading,new LayoutParams(-1,dp(27)));return region;}
    private TextView label(String text,int size,boolean bold){TextView view=SharedThreePanel.text(getContext(),text,size);view.setTextSize(size*DetailsDimensions.scale(getContext()));view.setTypeface(null,bold?Typeface.BOLD:Typeface.NORMAL);view.setIncludeFontPadding(false);view.setFocusable(false);return view;}
    private void fact(LinearLayout parent,String name,String value){LinearLayout row=new LinearLayout(getContext());row.setGravity(Gravity.CENTER_VERTICAL);TextView label=label(name,10,false),data=label(value,10,false);label.setTextColor(0xffb9c7d2);label.setSingleLine(true);label.setEllipsize(android.text.TextUtils.TruncateAt.END);data.setSingleLine(true);data.setEllipsize(name.equals("Path")?android.text.TextUtils.TruncateAt.MIDDLE:android.text.TextUtils.TruncateAt.END);row.setContentDescription(name+": "+value);row.addView(label,new LayoutParams(0,-2,.46f));LayoutParams valueSize=new LayoutParams(0,-2,.54f);valueSize.leftMargin=dp(10);row.addView(data,valueSize);for(TextView text:new TextView[]{label,data})androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(text,DetailsDimensions.sp(getContext(),8),DetailsDimensions.sp(getContext(),10),1,android.util.TypedValue.COMPLEX_UNIT_SP);parent.addView(row,new LayoutParams(-1,0,1));}
    private void rule(LinearLayout parent,boolean vertical){View rule=new View(getContext());rule.setTag("details.separator");rule.setBackgroundColor(0x447b8793);LayoutParams size=new LayoutParams(vertical?Math.max(1,dp(1)):-1,vertical?-1:Math.max(1,dp(1)));if(vertical)size.setMargins(dp(3),dp(6),dp(3),dp(6));else size.setMargins(dp(3),dp(4),dp(3),dp(4));parent.addView(rule,size);}
    private static String first(Map<String,String> fields,String... keys){for(String key:keys){String value=fields.get(key);if(value!=null&&!value.trim().isEmpty())return value;}return "Unknown";}
    static Map<String,String> facts(LinearLayout panel){Map<String,String> values=new LinkedHashMap<>();for(int i=0;i<panel.getChildCount();i++){View view=panel.getChildAt(i);if(view instanceof LinearLayout){LinearLayout row=(LinearLayout)view;if(row.getChildCount()==2&&row.getChildAt(0) instanceof TextView&&row.getChildAt(1) instanceof TextView)values.put(((TextView)row.getChildAt(0)).getText().toString(),((TextView)row.getChildAt(1)).getText().toString());}}return values;}
    private static String votes(String text){try{return SharedMetadata.votes(Long.parseLong(text.replace(",",""))).replace(".0k","k").replace(".0m","m");}catch(NumberFormatException invalid){return "";}}
    private int dp(int value){return DetailsDimensions.px(getContext(),value);}
}
