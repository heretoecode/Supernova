package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;

/** One remote keyboard for Search and row naming, with a permanent number row. */
public final class PreviewKeyboard extends LinearLayout {
    public static final String[] ROWS={"1234567890","QWERTYUIOP","ASDFGHJKL","ZXCVBNM"};
    public PreviewKeyboard(Context context, EditText input, Runnable leaveRight) {
        super(context);setOrientation(VERTICAL);setClipChildren(false);setClipToPadding(false);
        if(input.getId()==View.NO_ID)input.setId(View.generateViewId());
        for(int r=0;r<ROWS.length;r++){
            LinearLayout row=new LinearLayout(context);row.setClipChildren(false);
            LayoutParams lp=new LayoutParams(-1,dp(38));lp.topMargin=dp(5);addView(row,lp);
            String chars=ROWS[r];
            for(int i=0;i<chars.length();i++){
                String letter=chars.substring(i,i+1);
                TextView key=key(letter,()->replace(input,letter));row.addView(key,new LayoutParams(0,-1,1));
                if(r==0){key.setNextFocusUpId(input.getId());if(i==0)input.setNextFocusDownId(key.getId());}
                if(i==chars.length()-1&&leaveRight!=null)key.setOnKeyListener((v,code,event)->{if(code==KeyEvent.KEYCODE_DPAD_RIGHT&&event.getAction()==KeyEvent.ACTION_DOWN){leaveRight.run();return true;}return false;});
            }
        }
        LinearLayout edit=new LinearLayout(context);edit.setClipChildren(false);addView(edit,new LayoutParams(-1,dp(40)));
        edit.addView(key("Delete",()->{int a=Math.max(0,input.getSelectionStart()),b=Math.max(a,input.getSelectionEnd());if(a!=b)input.getText().delete(a,b);else if(a>0)input.getText().delete(input.getText().toString().offsetByCodePoints(a,-1),a);}),new LayoutParams(0,-1,1));
        edit.addView(key("Space",()->replace(input," ")),new LayoutParams(0,-1,1));
        edit.addView(key("Clear",()->input.setText("")),new LayoutParams(0,-1,1));
    }
    private static void replace(EditText input,String value){int a=Math.max(0,input.getSelectionStart()),b=Math.max(a,input.getSelectionEnd());input.getText().replace(a,b,value);}
    private TextView key(String label,Runnable action){TextView key=new TextView(getContext());key.setText(label);key.setTextColor(Color.WHITE);key.setTextSize(14);key.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));key.setGravity(Gravity.CENTER);key.setFocusable(true);key.setId(View.generateViewId());key.setBackground(PreviewDialog.focus(getContext()));key.setOnClickListener(v->action.run());return key;}
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
