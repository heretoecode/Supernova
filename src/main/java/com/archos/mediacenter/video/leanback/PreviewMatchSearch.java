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
        input=new EditText(context);input.setTag("semantic:match.query");input.setSingleLine(true);input.setShowSoftInputOnFocus(false);input.setTextColor(Color.WHITE);input.setTextSize(17);input.setBackground(PreviewDialog.focus(context));input.setText(initial);input.setSelection(input.length());left.addView(input,new LayoutParams(-1,dp(44)));
        keyboard=new PreviewKeyboard(context,input,this::focusResults);LayoutParams keys=new LayoutParams(-1,-2);keys.topMargin=dp(12);left.addView(keyboard,keys);keyboard.setLeaveDown(this::focusResults);
        LinearLayout right=new LinearLayout(context);right.setOrientation(VERTICAL);right.setClipChildren(false);LayoutParams resultArea=new LayoutParams(0,-1,1);resultArea.leftMargin=dp(32);columns.addView(right,resultArea);
        status=text("Searching…",13);status.setPadding(0,0,0,dp(10));right.addView(status);
        ScrollView scroll=new ScrollView(context);scroll.setClipChildren(false);scroll.setClipToPadding(false);rows=new LinearLayout(context);rows.setOrientation(VERTICAL);rows.setClipChildren(false);rows.setClipToPadding(false);scroll.addView(rows);right.addView(scroll,new LayoutParams(-1,0,1));
        query=()->search.accept(input.getText().toString().trim());
        input.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}public void onTextChanged(CharSequence s,int start,int before,int count){removeCallbacks(query);postDelayed(query,350);}public void afterTextChanged(android.text.Editable value){}});
        input.setOnEditorActionListener((v,action,event)->{removeCallbacks(query);query.run();return true;});
        input.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_DOWN){keyboard.focusLastKey();return true;}return false;});
        post(keyboard::focusLastKey);
    }
    public void searching(){setResults(Collections.emptyList());status.setText("Searching…");}
    public void unavailable(){setResults(Collections.emptyList());status.setText("Search unavailable. Please try again.");}
    public void setResults(List<BaseTags> results){
        boolean append=results.size()>=rendered.size();for(int i=0;append&&i<rendered.size();i++)append=rendered.get(i)==results.get(i);
        if(!append){boolean focused=rows.hasFocus();rows.removeAllViews();rendered.clear();if(focused)keyboard.focusLastKey();}
        for(int i=rendered.size();i<results.size();i++){
            final int index=i;BaseTags tags=results.get(i);LinearLayout row=new LinearLayout(getContext());row.setOrientation(VERTICAL);row.setTag("semantic:match.result:"+i);row.setFocusable(true);row.setFocusableInTouchMode(true);row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);row.setPadding(dp(12),dp(10),dp(12),dp(10));row.setBackground(PreviewDialog.focus(getContext()));
            TextView title=text(label(tags),16);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);row.addView(title);
            String plot=tags.getPlot();if(plot!=null&&!plot.trim().isEmpty()){TextView detail=text(plot,12);detail.setMaxLines(2);detail.setEllipsize(android.text.TextUtils.TruncateAt.END);detail.setPadding(0,dp(5),0,0);row.addView(detail);}
            row.setContentDescription(label(tags));row.setOnClickListener(v->choose.accept(tags));
            row.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_LEFT){keyboard.focusLastKey();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){int next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<rows.getChildCount())rows.getChildAt(next).requestFocus();return true;}return false;});
            LayoutParams cell=new LayoutParams(-1,-2);cell.bottomMargin=dp(8);rows.addView(row,cell);rendered.add(tags);
        }
        status.setText(results.isEmpty()?"No matches":"Choose a match to review");
    }
    public void focusInput(){keyboard.focusLastKey();}
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
