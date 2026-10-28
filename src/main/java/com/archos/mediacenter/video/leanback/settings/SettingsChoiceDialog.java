package com.archos.mediacenter.video.leanback.settings;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.preference.ListPreference;
import com.archos.mediacenter.video.leanback.*;

/** Settings radio selector: fixed heading/footer and independently scrolling choices. */
final class SettingsChoiceDialog {
    static Dialog show(Context context,ListPreference preference) {
        CharSequence[] labels=preference.getEntries(),values=preference.getEntryValues();
        if(labels==null||values==null||labels.length!=values.length)return null;
        Dialog dialog=PreviewDialog.create(context,"choice");dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel=new LinearLayout(context);panel.setOrientation(LinearLayout.VERTICAL);
        int padding=dp(context,16);panel.setPadding(padding,dp(context,14),padding,dp(context,12));panel.setBackground(PreviewDialog.dialogSurface(context));
        TextView title=SharedThreePanel.text(context,String.valueOf(preference.getTitle()),21);title.setTypeface(null,android.graphics.Typeface.BOLD);panel.addView(title);
        TextView explanation=SharedThreePanel.text(context,"",15);explanation.setVisibility(View.GONE);explanation.setTextColor(0xffbcc9d9);explanation.setPadding(0,dp(context,5),0,dp(context,10));panel.addView(explanation);
        ScrollView scroll=new ScrollView(context);scroll.setVerticalScrollBarEnabled(false);scroll.setSmoothScrollingEnabled(false);
        LinearLayout rows=new LinearLayout(context);rows.setOrientation(LinearLayout.VERTICAL);scroll.addView(rows);panel.addView(scroll);
        int selected=preference.findIndexOfValue(preference.getValue());View initial=null;
        for(int i=0;i<labels.length;i++) {
            final int index=i;LinearLayout row=new LinearLayout(context);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(context,8),dp(context,6),dp(context,8),dp(context,6));row.setMinimumHeight(dp(context,48));row.setFocusable(true);row.setFocusableInTouchMode(true);row.setBackground(PreviewDialog.focus(context));row.setTag("settings.choice."+i);row.setContentDescription(labels[i]+(i==selected?", selected":", not selected"));
            ImageView radio=new ImageView(context);radio.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);radio.setImageDrawable(new PreviewIcon(i==selected?"settings.radio.on":"settings.radio"));row.addView(radio,new LinearLayout.LayoutParams(dp(context,20),dp(context,20)));
            if(language(preference.getKey())) {ImageView icon=new ImageView(context);icon.setImageDrawable(new PreviewLanguageIcon(context,PreviewLanguageIcon.providerCode(values[i].toString())));LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(dp(context,20),dp(context,20));size.leftMargin=dp(context,8);row.addView(icon,size);}
            TextView label=SharedThreePanel.text(context,labels[i].toString(),16);label.setPadding(dp(context,10),0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));
            row.setOnClickListener(v->{String value=values[index].toString();if(preference.callChangeListener(value)){preference.setValue(value);dialog.dismiss();}});
            rows.addView(row,new LinearLayout.LayoutParams(-1,-2));if(i<labels.length-1)divider(rows,context);if(i==selected||initial==null)initial=row;
        }
        divider(panel,context);LinearLayout footer=new LinearLayout(context);footer.setGravity(Gravity.RIGHT);TextView cancel=TvButton.create(context,"Cancel",dialog::dismiss,false);cancel.setTag("settings.choice.cancel");cancel.setMinHeight(dp(context,48));footer.addView(cancel);panel.addView(footer);
        dialog.setContentView(panel);dialog.show();Window window=dialog.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);window.setDimAmount(.32f);window.setGravity(Gravity.CENTER);
        int width=Math.min(dp(context,360),Math.max(dp(context,270),Math.round(context.getResources().getDisplayMetrics().widthPixels*.28f)));width=Math.min(width,context.getResources().getDisplayMetrics().widthPixels-dp(context,48));
        int measure=View.MeasureSpec.makeMeasureSpec(width-padding*2,View.MeasureSpec.EXACTLY),free=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);title.measure(measure,free);explanation.measure(measure,free);footer.measure(measure,free);rows.measure(measure,free);
        int chrome=dp(context,27)+title.getMeasuredHeight()+explanation.getMeasuredHeight()+footer.getMeasuredHeight();int available=Math.max(dp(context,48),TvLayout.safeHeight(context)-dp(context,48)-chrome);int height=Math.min(rows.getMeasuredHeight(),available);scroll.setLayoutParams(new LinearLayout.LayoutParams(-1,height));window.setLayout(width,height+chrome);
        View opener=context instanceof android.app.Activity?((android.app.Activity)context).getCurrentFocus():null;
        if(opener!=null){View category=opener.getRootView().findViewWithTag("settings.category");if(category instanceof LinearLayout){View content=((LinearLayout)category).getChildAt(1);int[] position=new int[2];content.getLocationOnScreen(position);WindowManager.LayoutParams attrs=window.getAttributes();attrs.x=position[0]+content.getWidth()/2-context.getResources().getDisplayMetrics().widthPixels/2;window.setAttributes(attrs);}}
        if(initial!=null)initial.requestFocus();return dialog;
    }
    private static boolean language(String key){return java.util.Arrays.asList("ui_lang","favSubLang","favAudioLang","languages_list",com.archos.mediacenter.video.utils.VideoPreferencesCommon.KEY_SCRAPER_FAV_LANG).contains(key);}
    private static void divider(LinearLayout panel,Context context){View line=new View(context);line.setBackgroundColor(0x50426a80);panel.addView(line,new LinearLayout.LayoutParams(-1,dp(context,1)));}
    private static int dp(Context context,int value){return PreviewDialog.dp(context,value);}
}
