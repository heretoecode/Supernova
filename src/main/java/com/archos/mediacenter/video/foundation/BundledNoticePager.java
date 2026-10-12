package com.archos.mediacenter.video.foundation;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;

/** Every bundled notice remains reachable; Android never lays out a megabyte TextView. */
public final class BundledNoticePager extends LinearLayout {
    public static final int PAGE_CHARS=4000;
    private final List<String> pages;
    private final TextView text,counter,previous,next;
    private final ScrollView scroll;
    private int page;
    public BundledNoticePager(Context context,String notice,Runnable returnToCatalogue){
        super(context);setOrientation(VERTICAL);pages=pages(notice);
        LinearLayout commands=new LinearLayout(context);commands.setGravity(Gravity.CENTER_VERTICAL);
        previous=TvButton.create(context,"Previous",()->change(-1),false);next=TvButton.create(context,"Next",()->change(1),false);
        counter=SharedThreePanel.text(context,"",14);counter.setGravity(Gravity.CENTER);commands.addView(previous,new LayoutParams(-2,-2));commands.addView(counter,new LayoutParams(0,-2,1));commands.addView(next,new LayoutParams(-2,-2));addView(commands);
        scroll=new ScrollView(context);scroll.setFocusable(true);scroll.setFocusableInTouchMode(true);scroll.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);text=SharedThreePanel.text(context,"",14);text.setTextIsSelectable(false);text.setPadding(0,PreviewDialog.dp(context,10),0,PreviewDialog.dp(context,16));scroll.addView(text,new ScrollView.LayoutParams(-1,-2));addView(scroll,new LayoutParams(-1,Math.max(PreviewDialog.dp(context,120),TvLayout.safeHeight(context)/2)));
        scroll.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){returnToCatalogue.run();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(next.isEnabled())next.requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP&&scroll.getScrollY()==0){(previous.isEnabled()?previous:next).requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){scroll.scrollBy(0,PreviewDialog.dp(context,key==KeyEvent.KEYCODE_DPAD_UP?-60:60));return true;}return false;});
        for(TextView button:new TextView[]{previous,next})button.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_DOWN){scroll.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT&&v==previous){returnToCatalogue.run();return true;}return false;});
        render();
    }
    public static List<String> pages(String notice){
        List<String> pages=new ArrayList<>();String body=notice==null?"":notice;int start=0;
        while(start<body.length()){int end=Math.min(body.length(),start+PAGE_CHARS);if(end<body.length()&&Character.isHighSurrogate(body.charAt(end-1)))end--;pages.add(body.substring(start,end));start=end;}
        if(pages.isEmpty())pages.add("");return pages;
    }
    private void change(int direction){page=Math.max(0,Math.min(pages.size()-1,page+direction));render();if(!previous.isEnabled()&&previous.hasFocus()||!next.isEnabled()&&next.hasFocus())scroll.requestFocus();}
    private void render(){text.setText(pages.get(page));counter.setText((page+1)+" / "+pages.size());previous.setEnabled(page>0);next.setEnabled(page+1<pages.size());scroll.scrollTo(0,0);}
    public void focusReader(){scroll.requestFocus();}
}
