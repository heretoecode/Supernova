package com.archos.mediacenter.video.foundation;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;

/** One continuous disclosure. Recycling bounds layout cost without hiding text behind pages. */
public final class BundledNoticeReader extends LinearLayout {
    static final int BLOCK_CHARS=2400;
    private final List<String> blocks;
    private final RecyclerView reader;
    private final LinearLayoutManager layout;
    private final TextView search;
    private String query="";
    private int match=-1;
    public BundledNoticeReader(Context context,String notice,Runnable returnToCatalogue){
        super(context);setOrientation(VERTICAL);blocks=blocks(notice);
        search=TvButton.create(context,"Find in notice",()->PreviewTextInput.showValidated(context,"Find in notice",query,120,value->null,value->{if(!query.equals(value))match=-1;query=value;findNext();}),false);
        addView(search,new LayoutParams(-1,-2));
        reader=new RecyclerView(context);reader.setTag("licence.continuous.reader");reader.setFocusable(true);reader.setFocusableInTouchMode(true);reader.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);reader.setItemAnimator(null);reader.setBackground(SharedThreePanel.accentFocus(context));
        layout=new LinearLayoutManager(context);reader.setLayoutManager(layout);reader.setAdapter(new NoticeAdapter());
        addView(reader,new LayoutParams(-1,Math.max(PreviewDialog.dp(context,120),TvLayout.safeHeight(context)/2)));
        reader.setOnKeyListener((v,key,event)->{
            if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){returnToCatalogue.run();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
            if(key==KeyEvent.KEYCODE_DPAD_UP&&!reader.canScrollVertically(-1)){search.requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){reader.smoothScrollBy(0,PreviewDialog.dp(context,key==KeyEvent.KEYCODE_DPAD_UP?-64:64));return true;}
            return false;
        });
        search.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_DOWN){focusReader();return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT){returnToCatalogue.run();return true;}return key==KeyEvent.KEYCODE_DPAD_RIGHT;});
    }
    /** Boundaries are invisible: concatenation reproduces every notice byte decoded as UTF-8. */
    public static List<String> blocks(String notice){
        List<String> result=new ArrayList<>();String body=notice==null?"":notice;int start=0;
        while(start<body.length()){
            int end=Math.min(body.length(),start+BLOCK_CHARS);
            if(end<body.length()){
                int newline=body.lastIndexOf('\n',end-1);
                if(newline>start+BLOCK_CHARS/2)end=newline+1;
                if(Character.isHighSurrogate(body.charAt(end-1)))end--;
            }
            result.add(body.substring(start,end));start=end;
        }
        if(result.isEmpty())result.add("");return Collections.unmodifiableList(result);
    }
    private void findNext(){
        if(query.isEmpty()){match=-1;search.setText("Find in notice");reader.getAdapter().notifyDataSetChanged();focusReader();return;}
        String body=android.text.TextUtils.join("",blocks).toLowerCase(Locale.ROOT);String needle=query.toLowerCase(Locale.ROOT);
        match=body.indexOf(needle,match<0?0:match+1);if(match<0)match=body.indexOf(needle);
        search.setText(match<0?"No matches · Find in notice":"Find again · "+query);
        if(match>=0){int offset=0;for(int i=0;i<blocks.size();i++){if(match<offset+blocks.get(i).length()){layout.scrollToPositionWithOffset(i,0);break;}offset+=blocks.get(i).length();}}
        reader.getAdapter().notifyDataSetChanged();focusReader();
    }
    private final class NoticeAdapter extends RecyclerView.Adapter<NoticeHolder>{
        @Override public NoticeHolder onCreateViewHolder(ViewGroup parent,int type){TextView text=SharedThreePanel.text(getContext(),"",14);text.setFocusable(false);text.setTextIsSelectable(false);text.setPadding(0,0,0,0);text.setLayoutParams(new RecyclerView.LayoutParams(-1,-2));return new NoticeHolder(text);}
        @Override public void onBindViewHolder(NoticeHolder holder,int position){
            String block=blocks.get(position);SpannableString text=new SpannableString(block);
            if(!query.isEmpty()){String lower=block.toLowerCase(Locale.ROOT),needle=query.toLowerCase(Locale.ROOT);int at=0;while((at=lower.indexOf(needle,at))>=0){text.setSpan(new BackgroundColorSpan(0xff245e85),at,Math.min(block.length(),at+needle.length()),0);at+=Math.max(1,needle.length());}}
            ((TextView)holder.itemView).setText(text);
        }
        @Override public int getItemCount(){return blocks.size();}
    }
    private static final class NoticeHolder extends RecyclerView.ViewHolder{NoticeHolder(View view){super(view);}}
    public void focusReader(){reader.requestFocus();}
}
