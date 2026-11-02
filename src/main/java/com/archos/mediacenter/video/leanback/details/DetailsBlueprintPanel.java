package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;

/** FD-033 v3 text/geometry contract. Six-region composition; Reception contains ratings and awards only. */
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
        boolean suppliedData=false;for(int slot=0;slot<4;slot++)suppliedData|=supplementary.hasData(slot);
        boolean hasReception=existingReception.containsKey("TMDB Rating")||existingReception.containsKey("Indexed Rating")||suppliedData;
        LinearLayout key=region(upper,"Key Information",.328f);
        bindFacts(key,KEY_FIELDS,facts,keyValues);
        {
        LinearLayout reception=region(upper,"Reception",.294f);
        // Owner decision 2 Nov 2026: show every rating that has real data, TMDb included, as
        // stacked rows (logo · score · caption directly beneath). Order: IMDb, Rotten Tomatoes,
        // Metacritic, TMDb, Awards. Rows without data are omitted, never shown empty.
        tmdbScore=existingReception.containsKey("TMDB Rating")?existingReception.get("TMDB Rating").replace(" / ","/"):"";
        tmdbVotes=existingReception.getOrDefault("TMDb votes","");
        if(tmdbScore.isEmpty()&&existingReception.containsKey("Indexed Rating")&&!suppliedData){
            TextView score=label("Indexed rating "+existingReception.get("Indexed Rating"),16,true);score.setTag("details.reception.primary");score.setSingleLine(true);score.setEllipsize(android.text.TextUtils.TruncateAt.END);reception.addView(score,new LayoutParams(-1,-2));
        }
        if(!hasReception){TextView unavailable=label("Ratings unavailable",14,false);unavailable.setTag("details.reception.unavailable");reception.addView(unavailable,new LayoutParams(-1,0,1));}
        if(suppliedData)reception.setContentDescription("Reception. Supplementary data via OMDb");
        String[] providers={"IMDb","Rotten Tomatoes","Metacritic","Awards","TMDb"};
        int[] order={0,1,2,TMDB_ROW,3};
        int shown=0;for(int n:order){
            if(n==TMDB_ROW?tmdbScore.isEmpty():!supplementary.hasData(n))continue;
            if(shown++>0){rule(reception,false);LayoutParams divider=(LayoutParams)reception.getChildAt(reception.getChildCount()-1).getLayoutParams();divider.leftMargin=dp(38);if(getResources().getConfiguration().fontScale>1.2f){divider.topMargin=dp(2);divider.bottomMargin=dp(2);}}
            LinearLayout rating=new LinearLayout(c);rating.setGravity(Gravity.CENTER_VERTICAL);rating.setBaselineAligned(false);reception.addView(rating,new LayoutParams(-1,0,n==3?(getResources().getConfiguration().fontScale>1.2f?1.8f:1.25f):1));
            rating.setTag("details.reception.slot."+n);
            rating.addView(providerMark(n,providers[n]),new LayoutParams(0,-1,.25f));
            rating.addView(ratingValue(n,supplementary),new LayoutParams(0,-1,.75f));
        }
        // Keep each row the height it has in a full panel: with fewer than four rows, pad the
        // remainder instead of stretching rows (which pushed captions away from their scores).
        if(shown>0&&shown<4){View spacer=new View(c);spacer.setTag("details.reception.spacer");reception.addView(spacer,new LayoutParams(-1,0,4-shown));}

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
    /** Reception row index for TMDb (rows 0-3 are the OMDb sources and awards). */
    static final int TMDB_ROW=4;
    private String tmdbScore="",tmdbVotes="";
    /** TMDb mark: a rounded tile in TMDb's own green-to-blue brand colours, matching the IMDb tile. */
    private View tmdbMark(){
        TextView tile=new TextView(getContext());tile.setText("TMDB");tile.setTypeface(null,Typeface.BOLD);tile.setTextColor(0xff0d253f);tile.setGravity(Gravity.CENTER);tile.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,dp(9));tile.setIncludeFontPadding(false);tile.setSingleLine(true);
        android.graphics.drawable.GradientDrawable background=new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xff90cea1,0xff01b4e4});background.setCornerRadius(dp(2));tile.setBackground(background);
        return tile;
    }
    private View providerMark(int row,String name){
        FrameLayout mark=new FrameLayout(getContext()){@Override protected void onMeasure(int w,int h){int width=MeasureSpec.getSize(w),height=MeasureSpec.getSize(h);int side=Math.min(dp(34),Math.min(width>0?width:dp(34),height>0?height:dp(34)));if(getChildCount()>0){getChildAt(0).getLayoutParams().width=side;getChildAt(0).getLayoutParams().height=side;}super.onMeasure(w,h);}};
        if(row==TMDB_ROW){View tile=tmdbMark();tile.setTag("details.reception.logo."+row);tile.setContentDescription(name);FrameLayout.LayoutParams size=new FrameLayout.LayoutParams(dp(34),dp(34),Gravity.START|Gravity.CENTER_VERTICAL);size.leftMargin=dp(4);mark.addView(tile,size);return mark;}
        ImageView logo=new ImageView(getContext());logo.setTag("details.reception.logo."+row);
        if(row==0){logo.setImageResource(com.archos.mediacenter.video.R.drawable.imdb_logo);android.graphics.drawable.GradientDrawable tile=new android.graphics.drawable.GradientDrawable();tile.setColor(0xffeead3d);tile.setCornerRadius(dp(2));logo.setBackground(tile);logo.setPadding(dp(1),dp(1),dp(1),dp(1));logo.setColorFilter(new android.graphics.ColorMatrixColorFilter(new android.graphics.ColorMatrix(new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,1,0})));}
        else if(row==1)logo.setImageResource(com.archos.mediacenter.video.R.drawable.reception_rotten_tomatoes);
        else if(row==2)logo.setImageResource(com.archos.mediacenter.video.R.drawable.reception_metacritic);
        else logo.setImageResource(com.archos.mediacenter.video.R.drawable.reception_awards);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);logo.setContentDescription(name);FrameLayout.LayoutParams size=new FrameLayout.LayoutParams(dp(34),dp(34),Gravity.START|Gravity.CENTER_VERTICAL);size.leftMargin=dp(4);mark.addView(logo,size);return mark;
    }
    private View ratingValue(int row,OmdbSupplement.Ratings ratings){
        LinearLayout box=new LinearLayout(getContext());box.setOrientation(VERTICAL);String supplied=row==TMDB_ROW?tmdbScore:ratings.value(row);
        if(row==3){int stop=supplied.indexOf(". ");String headline=stop<0?supplied:supplied.substring(0,stop),details=stop<0?"":supplied.substring(stop+2);
            TextView summary=label(headline,12,false);summary.setTag("details.reception.awards.summary");summary.setGravity(Gravity.CENTER_VERTICAL);summary.setMaxLines(2);summary.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(summary,6,12);box.addView(summary,new LayoutParams(-1,0,1));
            if(!details.isEmpty()){TextView body=label(details,11,false);body.setTag("details.reception.awards.details");body.setLineSpacing(0,1);body.setTextColor(0xffc7c4ce);body.setMaxLines(getResources().getConfiguration().fontScale>1.2f?3:2);body.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(body,6,11);box.addView(body,new LayoutParams(-1,0,getResources().getConfiguration().fontScale>1.2f?2.5f:2f));}return box;
        }
        TextView score=label(supplied.isEmpty()?"Rating unavailable":supplied,16,false);score.setTag("details.reception.score."+row);score.setGravity(Gravity.CENTER_VERTICAL);score.setSingleLine(true);score.setHorizontallyScrolling(false);score.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(score,8,16);
        int unit=supplied.indexOf('/');if(unit<0&&supplied.endsWith("%"))unit=supplied.length()-1;
        if(unit>=0){android.text.SpannableString styled=new android.text.SpannableString(supplied);styled.setSpan(new android.text.style.RelativeSizeSpan(.85f),unit,supplied.length(),android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);score.setText(styled);}
        box.addView(score,new LayoutParams(-1,0,1f));
        // Captions sit directly beneath their score. Counts appear only when genuinely supplied;
        // otherwise the caption names the measure (owner decision 2 Nov 2026: no "unavailable" noise).
        String caption;
        if(row==0)caption=ratings.imdbVotes.isEmpty()?"IMDb rating":votes(ratings.imdbVotes);
        else if(row==1)caption=ratings.tomatoReviews.isEmpty()?"Tomatometer":"Tomatometer ("+ratings.tomatoReviews+" reviews)";
        else if(row==2)caption=ratings.metacriticReviews.isEmpty()?"Metascore":"Metascore ("+ratings.metacriticReviews+" reviews)";
        else caption=tmdbVotes.isEmpty()?"TMDb user score":tmdbVotes+" votes";
        TextView count=label(caption,11,false);count.setTag("details.reception.count."+row);count.setTextColor(0xffc7c4ce);count.setGravity(Gravity.CENTER_VERTICAL);count.setSingleLine(true);count.setHorizontallyScrolling(false);count.setEllipsize(android.text.TextUtils.TruncateAt.END);auto(count,6,11);box.addView(count,new LayoutParams(-1,0,.7f));return box;
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
    private static String votes(String text){try{return SharedMetadata.votes(Long.parseLong(text.replace(",",""))).replace(".0k","k").replace(".0m","m").replace("k","K").replace("m","M");}catch(NumberFormatException invalid){return "";}}
    private int dp(int value){return DetailsDimensions.px(getContext(),value);}
}
