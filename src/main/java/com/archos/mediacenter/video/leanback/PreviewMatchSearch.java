package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import com.archos.mediascraper.*;
import java.util.*;
import java.util.function.Consumer;

/** Shared Find a Match presentation; native search and accepted-save handlers stay in the fragment. */
public final class PreviewMatchSearch extends LinearLayout {
    private final EditText input;
    private final PreviewKeyboard keyboard;
    private final LinearLayout rows;
    private final TextView status;
    private final List<BaseTags> rendered=new ArrayList<>();
    private final Consumer<BaseTags> choose;
    private final Consumer<String> search;
    private final Runnable query;
    public PreviewMatchSearch(Context context,String initial,Consumer<String> search,Consumer<BaseTags> choose){
        super(context);this.search=search;this.choose=choose;
        setOrientation(VERTICAL);setPadding(dp(32),dp(20),dp(32),dp(20));setBackground(new PreviewUtilityBackground(context));
        addView(text("Find a Match",24));TextView help=text("Search by title, TMDB ID or IMDb ID",14);help.setPadding(0,dp(6),0,dp(14));addView(help);
        LinearLayout columns=new LinearLayout(context);columns.setClipChildren(false);addView(columns,new LayoutParams(-1,0,1));
        LinearLayout left=new LinearLayout(context);left.setOrientation(VERTICAL);left.setClipChildren(false);columns.addView(left,new LayoutParams(dp(360),-1));
        input=new EditText(context);input.setTag("semantic:match.query");input.setSingleLine(true);input.setShowSoftInputOnFocus(false);input.setTextColor(Color.WHITE);input.setTextSize(17);input.setHint("Search by title or ID");input.setHintTextColor(0xffa9b9c8);input.setPadding(dp(12),0,dp(12),0);input.setBackground(PreviewDialog.surface(context,false));input.setText(initial);input.setSelection(input.length());left.addView(input,new LayoutParams(-1,dp(44)));
        keyboard=new PreviewKeyboard(context,input,this::focusResults);LayoutParams keys=new LayoutParams(-1,-2);keys.topMargin=dp(12);left.addView(keyboard,keys);keyboard.setLeaveDown(this::focusResults);
        LinearLayout right=new LinearLayout(context);right.setOrientation(VERTICAL);right.setClipChildren(false);LayoutParams resultArea=new LayoutParams(0,-1,1);resultArea.leftMargin=dp(32);columns.addView(right,resultArea);
        status=text("Searching…",13);status.setPadding(0,0,0,dp(10));right.addView(status);
        ScrollView scroll=new ScrollView(context);scroll.setClipChildren(false);scroll.setClipToPadding(false);rows=new LinearLayout(context);rows.setOrientation(VERTICAL);rows.setClipChildren(false);rows.setClipToPadding(false);scroll.addView(rows);right.addView(scroll,new LayoutParams(-1,0,1));
        input.setOnFocusChangeListener((v,focused)->{if(focused)post(keyboard::focusLastKey);});
        query=()->search.accept(input.getText().toString().trim());
        input.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}public void onTextChanged(CharSequence s,int start,int before,int count){removeCallbacks(query);postDelayed(query,350);}public void afterTextChanged(android.text.Editable value){}});
        input.setOnEditorActionListener((v,action,event)->{removeCallbacks(query);query.run();return true;});
        input.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_DOWN){keyboard.focusLastKey();return true;}return false;});
        post(keyboard::focusLastKey);
    }
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();post(keyboard::focusLastKey);}
    public void searching(){setResults(Collections.emptyList());status.setText("Searching…");}
    public void unavailable(){setResults(Collections.emptyList());status.setText("Search unavailable. Please try again.");}
    public void setResults(List<BaseTags> results){
        boolean append=results.size()>=rendered.size();for(int i=0;append&&i<rendered.size();i++)append=rendered.get(i)==results.get(i);
        if(!append){boolean focused=rows.hasFocus();rows.removeAllViews();rendered.clear();if(focused)keyboard.focusLastKey();}
        for(int i=rendered.size();i<results.size();i++){
            final int index=i;BaseTags tags=results.get(i);LinearLayout row=new LinearLayout(getContext());row.setOrientation(VERTICAL);row.setTag("semantic:match.result:"+i);row.setFocusable(true);row.setFocusableInTouchMode(true);row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);row.setPadding(dp(12),dp(10),dp(12),dp(10));row.setBackground(PreviewDialog.focus(getContext()));
            row.setOrientation(HORIZONTAL);
            ImageView poster=new ImageView(getContext());poster.setTag("semantic:match.poster:"+i);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);row.addView(poster,new LayoutParams(dp(60),dp(90)));
            LinearLayout copy=new LinearLayout(getContext());copy.setOrientation(VERTICAL);copy.setPadding(dp(12),0,0,0);row.addView(copy,new LayoutParams(0,-2,1));
            TextView title=text(label(tags),16);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);copy.addView(title);
            bindPoster(poster,tags);
            String plot=tags.getPlot();if(plot!=null&&!plot.trim().isEmpty()){TextView detail=text(plot,12);detail.setMaxLines(2);detail.setEllipsize(android.text.TextUtils.TruncateAt.END);detail.setPadding(0,dp(5),0,0);copy.addView(detail);}
            row.setContentDescription(label(tags));row.setOnClickListener(v->choose.accept(tags));
            row.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_LEFT){keyboard.focusLastKey();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){int next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<rows.getChildCount())rows.getChildAt(next).requestFocus();return true;}return false;});
            LayoutParams cell=new LayoutParams(-1,-2);cell.bottomMargin=dp(8);rows.addView(row,cell);rendered.add(tags);
        }
        status.setText(results.isEmpty()?"No matches":"Choose a match to review");
    }
    private void bindPoster(ImageView image,BaseTags tags){
        com.archos.mediascraper.ScraperImage poster=tags.getDefaultPoster();
        if(poster==null&&tags instanceof EpisodeTags&&((EpisodeTags)tags).getShowTags()!=null)poster=((EpisodeTags)tags).getShowTags().getDefaultPoster();
        image.setImageDrawable(new android.graphics.drawable.ColorDrawable(0xff20364a));
        image.setContentDescription("Poster for "+label(tags));
        if(poster==null)return;
        java.io.File file=poster.getLargeFileF();String url=poster.getThumbUrl();if(url==null||url.isEmpty())url=poster.getLargeUrl();
        android.net.Uri uri=file!=null&&file.isFile()?android.net.Uri.fromFile(file):url==null||url.isEmpty()?null:android.net.Uri.parse(url);
        if(uri==null)return;
        com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(image,uri,0,"matching.results","poster",com.squareup.picasso.Picasso.get().load(uri).resize(dp(60),dp(90)).centerInside().noFade(),true);
        image.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View v){}public void onViewDetachedFromWindow(View v){com.archos.mediacenter.video.diagnostics.ArtworkRequest.cancel(image);image.removeOnAttachStateChangeListener(this);}});
    }
    public void focusInput(){keyboard.focusLastKey();}
    public void refresh(){removeCallbacks(query);query.run();}
    public void parentSeries(String name,Runnable change){
        LinearLayout parent=new LinearLayout(getContext());parent.setGravity(Gravity.CENTER_VERTICAL);
        TextView label=text("Episodes in "+name+" · enter S1 E1, an episode title or ID",13);parent.addView(label,new LayoutParams(0,-2,1));
        TextView action=text("Change Series Match",14);action.setPadding(dp(10),dp(6),dp(10),dp(6));action.setFocusable(true);action.setFocusableInTouchMode(true);action.setBackground(PreviewDialog.focus(getContext()));action.setOnClickListener(view->change.run());action.setTag("semantic:match.change-series");parent.addView(action);
        addView(parent,2,new LayoutParams(-1,dp(40)));
    }
    private void focusResults(){if(rows.getChildCount()>0)rows.getChildAt(0).requestFocus();}
    public static String label(BaseTags tags){
        if(tags instanceof MovieTags){MovieTags movie=(MovieTags)tags;return movie.getTitle()+(movie.getYear()>0?" ("+movie.getYear()+")":"");}
        if(tags instanceof ShowTags)return ((ShowTags)tags).getTitle();
        if(tags instanceof EpisodeTags){EpisodeTags episode=(EpisodeTags)tags;return episode.getShowTitle()+" · S"+episode.getSeason()+" E"+episode.getEpisode();}
        return "Metadata match";
    }
    @Override protected void onDetachedFromWindow(){removeCallbacks(query);super.onDetachedFromWindow();}
    private TextView text(String value,int size){TextView view=new TextView(getContext());view.setText(value);view.setTextSize(size);view.setTextColor(Color.WHITE);return view;}
    private int dp(int size){return PreviewDialog.dp(getContext(),size);}
}
