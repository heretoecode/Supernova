package com.archos.mediacenter.video.leanback.details;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/** On-demand, read-only TMDB reviews. Never shares the core Details loading task. */
final class DetailsReviews {
    interface Source { JSONObject load(int page)throws Exception; }
    static final class Review {
        final String author,body,date,url;final double rating;
        Review(String author,String body,String date,String url,double rating){this.author=author;this.body=body;this.date=date;this.url=url;this.rating=rating;}
    }
    static final class Page {
        final int number,total;final boolean capped;final List<Review> reviews;
        Page(int number,int total,List<Review> reviews){this.number=number;this.total=Math.min(5,total);this.capped=total>5;this.reviews=Collections.unmodifiableList(reviews);}
    }
    static Page parse(JSONObject json,int requested)throws IOException{
        JSONArray rows=json.optJSONArray("results");int page=json.optInt("page",0),total=json.optInt("total_pages",-1);
        if(rows==null||page!=requested||page<1||page>5||total<0)throw new IOException("Invalid review page");
        List<Review> values=new ArrayList<>();Set<String> seen=new HashSet<>();
        for(int n=0;n<Math.min(rows.length(),20);n++){
            JSONObject row=rows.optJSONObject(n);if(row==null)continue;
            String body=bounded(row.optString("content",""),12000);if(body.isEmpty())continue;
            String id=row.optString("id","");if(!id.isEmpty()&&!seen.add(id))continue;
            JSONObject author=row.optJSONObject("author_details");double score=author==null?Double.NaN:author.optDouble("rating",Double.NaN);
            if(score<0||score>10)score=Double.NaN;
            String name=bounded(row.optString("author",""),128);if(name.isEmpty())name="TMDB user";
            String date=row.optString("created_at","");date=date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T.*")?date.substring(0,10):"";
            // Construct provenance from a validated provider ID; never trust arbitrary response URLs.
            String url=id.matches("[a-fA-F0-9]{24}")?"https://www.themoviedb.org/review/"+id:"";
            values.add(new Review(name,body,date,url,score));
        }
        return new Page(page,total,values);
    }
    private static String bounded(String text,int limit){String value=text==null?"":text.trim();if(value.equals("null"))return "";if(value.length()<=limit)return value;int end=limit;if(Character.isHighSurrogate(value.charAt(end-1)))end--;return value.substring(0,end)+"\n[Review shortened]";}
    private static final ExecutorService IO=Executors.newFixedThreadPool(2);
    private final Context context;private final String kind;private final long id;private final Source source;
    private Dialog dialog;private LinearLayout content,controls;private TextView status;private ScrollView scroll;
    private Future<?> task;private int generation,page=1;private Page displayed;
    DetailsReviews(Context context,String kind,long id){this(context,kind,id,defaultSource(context,kind,id));}
    private static Source defaultSource(Context context,String kind,long id){Context app=context.getApplicationContext();return page->StreamingRepository.reviews(app,kind,id,page);}
    DetailsReviews(Context context,String kind,long id,Source source){this.context=context;this.kind=kind;this.id=id;this.source=source;}
    void show(){
        dialog=PreviewDialog.create(context,"reader");LinearLayout root=new LinearLayout(context);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(20),dp(16),dp(20),dp(16));root.setBackground(PreviewDialog.surface(context,false));
        root.addView(label("Reviews · TMDB",20));root.addView(label("User reviews · "+Locale.getDefault().getDisplayLanguage(),14));
        status=label("Loading reviews…",14);status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);root.addView(status);
        scroll=new ScrollView(context);scroll.setFocusable(true);content=new LinearLayout(context);content.setOrientation(LinearLayout.VERTICAL);scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        controls=new LinearLayout(context);controls.setGravity(Gravity.CENTER_VERTICAL);root.addView(controls);
        dialog.setContentView(root);dialog.setOnDismissListener(d->{generation++;if(task!=null)task.cancel(true);});dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);dialog.getWindow().setLayout(TvLayout.dialogWidth(context),Math.min(dp(430),context.getResources().getDisplayMetrics().heightPixels-dp(48)));
        request(1);
    }
    private TextView label(String value,int size){TextView view=SharedThreePanel.text(context,value,size);view.setPadding(0,dp(4),0,dp(6));return view;}
    private int dp(int value){return PreviewDialog.dp(context,value);}
    private void action(String name,Runnable run){TextView button=TvButton.create(context,name,run,false);controls.addView(button,new LinearLayout.LayoutParams(-2,-2));}
    private void buttons(boolean busy){
        controls.removeAllViews();
        if(!busy){action("Retry",()->request(page));if(displayed!=null&&displayed.number>1)action("Previous",()->request(displayed.number-1));if(displayed!=null&&displayed.number<displayed.total)action("Next",()->request(displayed.number+1));}
        action("Close",this::close);
    }
    private void request(int requested){
        page=requested;int ticket=++generation;if(task!=null)task.cancel(true);status.setText("Loading reviews…");buttons(true);controls.getChildAt(controls.getChildCount()-1).requestFocus();
        task=IO.submit(()->{try{
            Page ready=parse(source.load(requested),requested);
            boolean fresh=PreviewMetadataCache.fresh(context,kind,id,"reviews/page_"+requested);
            status.post(()->{if(ticket!=generation||!dialog.isShowing())return;displayed=ready;render();status.setText((ready.reviews.isEmpty()?"No written reviews available in this language.":"Page "+ready.number+" of "+Math.max(1,ready.total)+(ready.capped?" (first five pages)":""))+(fresh?"":" · Saved reviews; refresh unavailable or pending."));buttons(false);if(ready.reviews.isEmpty())controls.getChildAt(0).requestFocus();else scroll.requestFocus();});
        }catch(Exception error){status.post(()->{if(ticket!=generation||!dialog.isShowing())return;status.setText(displayed==null?"Reviews unavailable. Check your connection and retry.":"Could not load this page. Previously loaded reviews are retained.");buttons(false);controls.getChildAt(0).requestFocus();});}});
    }
    private void render(){
        content.removeAllViews();scroll.scrollTo(0,0);
        for(Review review:displayed.reviews){
            String heading=review.author+(review.date.isEmpty()?"":" · "+review.date)+(Double.isNaN(review.rating)?"":" · "+String.format(Locale.ROOT,"%.1f/10",review.rating));
            content.addView(label(heading,17));TextView body=label(review.body,16);body.setTextIsSelectable(false);content.addView(body);
            if(!review.url.isEmpty()){TextView link=TvButton.create(context,"View on TMDB",()->PreviewDialog.read(context,"TMDB review",review.url),false);link.setContentDescription("TMDB review source: "+review.url);content.addView(link);}
        }
    }
    void close(){generation++;if(task!=null)task.cancel(true);if(dialog!=null)dialog.dismiss();}
}
