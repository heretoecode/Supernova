package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.function.Consumer;

/** Small remote-operated keyboard; hardware keyboard editing remains available. */
public final class PreviewTextInput {
    public static void show(Context c,String title,String previous,int limit,Consumer<String> accept){
        Dialog d=new Dialog(c);d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel=new LinearLayout(c);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(c,20),dp(c,16),dp(c,20),dp(c,16));panel.setBackground(PreviewDialog.surface(c,false));
        TextView heading=new TextView(c);heading.setText(title);heading.setTextColor(-1);heading.setTextSize(20);panel.addView(heading);
        EditText input=new EditText(c);input.setSingleLine(true);input.setShowSoftInputOnFocus(false);input.setText(previous);input.setTextColor(-1);input.setTextSize(16);input.setBackground(PreviewDialog.focus(c));input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(limit)});panel.addView(input,new LinearLayout.LayoutParams(-1,dp(c,44)));input.setSelection(input.length());
        panel.addView(new PreviewKeyboard(c,input,null));
        LinearLayout actions=new LinearLayout(c);TextView save=button(c,"Save",()->{String value=input.getText().toString().trim();if(value.isEmpty()){input.setError("Enter a name");return;}accept.accept(value);d.dismiss();});actions.addView(save,new LinearLayout.LayoutParams(0,dp(c,38),1));actions.addView(button(c,"Cancel",d::dismiss),new LinearLayout.LayoutParams(0,dp(c,38),1));panel.addView(actions);
        d.setContentView(panel);d.show();d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);d.getWindow().setDimAmount(.35f);d.getWindow().setLayout(Math.min(dp(c,560),c.getResources().getDisplayMetrics().widthPixels-dp(c,64)),-2);save.requestFocus();
    }
    private static int dp(Context c,int n){return PreviewDialog.dp(c,n);}
    private static TextView button(Context c,String label,Runnable action){TextView v=new TextView(c);v.setText(label);v.setTextColor(-1);v.setTextSize(14);v.setGravity(Gravity.CENTER);v.setFocusable(true);v.setBackground(PreviewDialog.focus(c));v.setOnClickListener(w->action.run());return v;}
}
