package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;

/** FD-033 v3 text/geometry contract. All six regions remain read-only and fit one panel. */
final class DetailsBlueprintPanel extends LinearLayout {
    DetailsBlueprintPanel(Context c,String synopsis,Map<String,String> facts,Map<String,String> technical,Map<String,String> existingReception,View cast,View crew,OmdbSupplement.Ratings supplementary){
        super(c);setOrientation(VERTICAL);setTag("details.blueprint.v3");setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);setPadding(dp(10),dp(8),dp(10),dp(8));
        LinearLayout upper=row(.56f),lower;
        LinearLayout description=region(upper,"Description",.378f);TextView copy=label(synopsis==null||synopsis.trim().isEmpty()?"Unavailable":synopsis,13,false);copy.setTag("details.full.synopsis");copy.setGravity(Gravity.CENTER);copy.setLineSpacing(dp(3),1);copy.setEllipsize(android.text.TextUtils.TruncateAt.END);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(copy,11,14,1,android.util.TypedValue.COMPLEX_UNIT_SP);description.addView(copy,new LayoutParams(-1,0,1));copy.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob)->{int lines=Math.max(1,(b-t-copy.getPaddingTop()-copy.getPaddingBottom())/Math.max(1,copy.getLineHeight()));if(copy.getMaxLines()!=lines)copy.setMaxLines(lines);});
        LinearLayout key=region(upper,"Key Information",.328f);
        String[][] fields={{"Original Title","Original title","Series original title"},{"Also Known As","Alternative names"},{"Release Date","Release / first air date","Air date","Year"},{"Runtime","Runtime"},{"Genres","Genres"},{"Country","Countries"},{"Language","Language"},{"Production Company","Studio / Network"},{"Budget","Budget (TMDb)"},{"Revenue","Box office (TMDb)"}};
        for(String[] field:fields)fact(key,field[0],first(facts,Arrays.copyOfRange(field,1,field.length)));
        LinearLayout reception=region(upper,"Reception",.294f);
        String existing=existingReception.containsKey("TMDB Rating")?"TMDb "+existingReception.get("TMDB Rating"):existingReception.containsKey("Indexed Rating")?"Indexed rating "+existingReception.get("Indexed Rating"):"";
        if(!existing.isEmpty()){if(existingReception.containsKey("TMDb votes"))existing+=" · "+existingReception.get("TMDb votes")+" votes";TextView prior=label(existing,10,false);prior.setTextColor(0xffb9c7d2);prior.setSingleLine(true);prior.setEllipsize(android.text.TextUtils.TruncateAt.END);prior.setContentDescription(existing);reception.addView(prior,new LayoutParams(-1,dp(17)));}
        String[] providers={"IMDb","Rotten Tomatoes","Metacritic","Awards"};
        for(int n=0;n<providers.length;n++){
            if(n>0)rule(reception,false);
            LinearLayout rating=new LinearLayout(c);rating.setGravity(Gravity.CENTER_VERTICAL);reception.addView(rating,new LayoutParams(-1,0,1));
            if(n==0){ImageView logo=new ImageView(c);logo.setImageResource(com.archos.mediacenter.video.R.drawable.imdb_logo);logo.setScaleType(ImageView.ScaleType.FIT_CENTER);logo.setContentDescription("IMDb");logo.setPadding(dp(3),dp(3),dp(7),dp(3));rating.addView(logo,new LayoutParams(0,dp(34),.34f));}else{TextView provider=label(providers[n],11,true);provider.setGravity(Gravity.CENTER);provider.setMaxLines(2);rating.addView(provider,new LayoutParams(0,-2,.34f));}
            String supplied=supplementary.value(n);TextView value=label(supplied.isEmpty()?(n==3?"Unavailable":"No rating available"):supplied+"\nvia OMDb"+(n==0&&!supplementary.imdbVotes.isEmpty()?" · "+supplementary.imdbVotes+" votes":""),11,false);value.setMaxLines(3);if(!supplied.isEmpty()&&n<3){android.text.SpannableString styled=new android.text.SpannableString(value.getText());int end=supplied.indexOf('/');if(end<0)end=supplied.length();styled.setSpan(new android.text.style.StyleSpan(Typeface.BOLD),0,end,android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);styled.setSpan(new android.text.style.RelativeSizeSpan(16f/11f),0,end,android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);value.setText(styled);}rating.addView(value,new LayoutParams(0,-2,.66f));
        }
        rule(this,false);lower=row(.44f);
        attach(region(lower,"Cast",.344f),cast);attach(region(lower,"Crew",.353f),crew);
        LinearLayout tech=region(lower,"Technical Information",.303f);
        String[][] techFields={{"Video","Video","Resolution","Video codec","File formats"},{"Audio","Audio tracks","Audio"},{"Container","Container"},{"Size","File size","Library size"},{"Source","Source","Storage locations"},{"Added","Added"},{"Path","Location"}};
        for(String[] field:techFields)fact(tech,field[0],first(technical,Arrays.copyOfRange(field,1,field.length)));
    }
    private void attach(LinearLayout parent,View content){if(content.getParent() instanceof ViewGroup)((ViewGroup)content.getParent()).removeView(content);content.setVisibility(VISIBLE);content.setPadding(0,dp(8),0,0);parent.addView(content,new LayoutParams(-1,0,1));}
    private LinearLayout row(float weight){LinearLayout row=new LinearLayout(getContext());row.setBaselineAligned(false);addView(row,new LayoutParams(-1,0,weight));return row;}
    private LinearLayout region(LinearLayout row,String title,float weight){if(row.getChildCount()>0)rule(row,true);LinearLayout region=new LinearLayout(getContext());region.setOrientation(VERTICAL);region.setTag(title.equals("Key Information")||title.equals("Reception")||title.equals("Technical Information")?"semantic:details.panel."+title.toLowerCase(Locale.ROOT).replace(' ','.'):"details.region."+title);region.setPadding(dp(6),dp(4),dp(6),dp(4));row.addView(region,new LayoutParams(0,-1,weight));TextView heading=label(title,15,true);heading.setSingleLine(true);heading.setEllipsize(android.text.TextUtils.TruncateAt.END);region.addView(heading,new LayoutParams(-1,dp(27)));return region;}
    private TextView label(String text,int size,boolean bold){TextView view=SharedThreePanel.text(getContext(),text,size);view.setTypeface(null,bold?Typeface.BOLD:Typeface.NORMAL);view.setIncludeFontPadding(false);view.setFocusable(false);return view;}
    private void fact(LinearLayout parent,String name,String value){LinearLayout row=new LinearLayout(getContext());row.setGravity(Gravity.CENTER_VERTICAL);TextView label=label(name,10,false),data=label(value,10,false);label.setTextColor(0xffb9c7d2);label.setSingleLine(true);label.setEllipsize(android.text.TextUtils.TruncateAt.END);data.setSingleLine(true);data.setEllipsize(name.equals("Path")?android.text.TextUtils.TruncateAt.MIDDLE:android.text.TextUtils.TruncateAt.END);row.setContentDescription(name+": "+value);row.addView(label,new LayoutParams(0,-2,.46f));LayoutParams valueSize=new LayoutParams(0,-2,.54f);valueSize.leftMargin=dp(10);row.addView(data,valueSize);parent.addView(row,new LayoutParams(-1,0,1));}
    private void rule(LinearLayout parent,boolean vertical){View rule=new View(getContext());rule.setTag("details.separator");rule.setBackgroundColor(0x447b8793);LayoutParams size=new LayoutParams(vertical?Math.max(1,dp(1)):-1,vertical?-1:Math.max(1,dp(1)));if(vertical)size.setMargins(dp(3),dp(6),dp(3),dp(6));else size.setMargins(dp(3),dp(4),dp(3),dp(4));parent.addView(rule,size);}
    private static String first(Map<String,String> fields,String... keys){for(String key:keys){String value=fields.get(key);if(value!=null&&!value.trim().isEmpty())return value;}return "Unknown";}
    static Map<String,String> facts(LinearLayout panel){Map<String,String> values=new LinkedHashMap<>();for(int i=0;i<panel.getChildCount();i++){View view=panel.getChildAt(i);if(view instanceof LinearLayout){LinearLayout row=(LinearLayout)view;if(row.getChildCount()==2&&row.getChildAt(0) instanceof TextView&&row.getChildAt(1) instanceof TextView)values.put(((TextView)row.getChildAt(0)).getText().toString(),((TextView)row.getChildAt(1)).getText().toString());}}return values;}
    private int dp(int value){return PreviewDialog.dp(getContext(),value);}
}
