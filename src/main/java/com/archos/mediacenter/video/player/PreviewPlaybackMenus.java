package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;
import androidx.preference.PreferenceManager;
import java.util.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.player.tvmenu.*;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;

/** One playback window over the real player actions. Tabs never create a second settings stack. */
final class PreviewPlaybackMenus {
    private static final String[] TABS={"Audio","Subtitles","Video","More"};
    private static Dialog current;
    private static Activity owner;
    private static TVMenuAdapter adapter;
    private static View origin;
    private static LinearLayout tabs,rows,trackRows,adjustmentRows;
    private static FrameLayout content;
    private static ScrollView scroll;
    private static int tab,rootFocus=-1;
    private static Runnable restoreParent,backAction;
    private static TVCardDialog nested;
    private static TVCardView liveCard;
    private static boolean closing;
    private static LinearLayout expansion;
    private static View expansionAnchor,invokingRow;
    static boolean isShowing(){return current!=null&&current.isShowing();}
    static boolean back(){if(!isShowing())return false;if(nested!=null)nested.handleBackPressed();else if(expansion!=null)collapseExpansion(true);else if(backAction!=null)backAction.run();else close();return true;}
    static void close(){
        closing=true;flushNested();Dialog old=current;current=null;
        if(old!=null){old.setOnCancelListener(null);old.dismiss();}
        if(owner instanceof PlayerActivity&&!owner.isFinishing())((PlayerActivity)owner).restorePreviewSettingsFocus(origin);else if(origin!=null&&origin.isAttachedToWindow()&&origin.isShown())origin.requestFocus();
        origin=null;owner=null;adapter=null;liveCard=null;restoreParent=null;backAction=null;
        // Detached views still retain their Activity context; release the entire window tree.
        expansion=null;expansionAnchor=null;invokingRow=null;tabs=null;rows=null;trackRows=null;adjustmentRows=null;content=null;scroll=null;
        closing=false;
    }
    private static void flushNested(){if(nested!=null){TVCardDialog card=nested;nested=null;card.setPreviewDismiss(()->{});card.handleBackPressed();}}
    static void show(Activity activity,TVMenuAdapter source,String target){
        close();owner=activity;adapter=source;origin=activity.getCurrentFocus();tab=1;rootFocus=-1;
        if(activity.getString(R.string.menu_audio).equals(target))tab=0;
        if(activity.getString(R.string.player_pref_audio_speed_title).equals(target))tab=3;
        int selected=tab;shell();tab=selected;renderTab();tabs.getChildAt(tab).requestFocus();
    }
    private static void shell(){
        current=PreviewDialog.create(owner);current.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel=new LinearLayout(owner);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(16),dp(16),dp(16),dp(16));android.graphics.drawable.GradientDrawable surface=new android.graphics.drawable.GradientDrawable();surface.setColor(0xe615171b);surface.setCornerRadius(dp(10));panel.setBackground(surface);
        TextView title=SharedThreePanel.text(owner,"Playback Settings",20);title.setPadding(0,0,0,dp(12));panel.addView(title);
        tabs=new LinearLayout(owner);tabs.setBaselineAligned(false);panel.addView(tabs,new LinearLayout.LayoutParams(-1,-2));
        for(int i=0;i<TABS.length;i++){
            final int index=i;TextView button=SharedThreePanel.text(owner,TABS[i],13);button.setGravity(Gravity.CENTER);button.setSingleLine(true);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(button,10,13,1,android.util.TypedValue.COMPLEX_UNIT_SP);button.setFocusable(true);button.setFocusableInTouchMode(true);button.setPadding(dp(4),dp(10),dp(4),dp(10));button.setBackground(tabBackground());button.setTag("playback.tab:"+i);
            button.setOnClickListener(v->switchTab(index));button.setOnFocusChangeListener((v,focused)->{if(focused&&tab!=index)switchTab(index);});
            button.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
                if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT){int next=Math.max(0,Math.min(3,index+(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1)));tabs.getChildAt(next).requestFocus();return true;}
                if(key==KeyEvent.KEYCODE_DPAD_DOWN){focusContent();return true;}return key==KeyEvent.KEYCODE_DPAD_UP;});
            tabs.addView(button,new LinearLayout.LayoutParams(0,-2,1));
        }
        content=new FrameLayout(owner);LinearLayout.LayoutParams body=new LinearLayout.LayoutParams(-1,0,1);body.topMargin=dp(12);panel.addView(content,body);
        current.setContentView(panel);current.setOnCancelListener(d->close());
        current.setOnKeyListener((d,key,event)->{if(key==KeyEvent.KEYCODE_BACK||key==KeyEvent.KEYCODE_ESCAPE||key==KeyEvent.KEYCODE_BUTTON_B){if(event.getAction()==KeyEvent.ACTION_UP)back();return true;}
            if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_MENU){tabs.getChildAt(tab).requestFocus();return true;}
            if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_UP&&nested!=null){View focused=current.getCurrentFocus();View next=focused==null?null:FocusFinder.getInstance().findNextFocus(nested,focused,View.FOCUS_UP);if(next==null||next==focused){tabs.getChildAt(tab).requestFocus();return true;}}
            return false;});
        current.show();Window window=current.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.setDimAmount(.12f);
        int width=Math.min(dp(620),Math.round(TvLayout.safeWidth(owner)*.72f));int height=Math.min(dp(360),TvLayout.safeHeight(owner)-dp(48));
        window.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);WindowManager.LayoutParams attrs=window.getAttributes();attrs.x=dp(24);window.setAttributes(attrs);window.setLayout(width,height);
    }
    private static android.graphics.drawable.Drawable tabBackground(){
        android.graphics.drawable.StateListDrawable states=new android.graphics.drawable.StateListDrawable();
        for(int state:new int[]{android.R.attr.state_focused,android.R.attr.state_selected,0}){
            android.graphics.drawable.GradientDrawable shape=new android.graphics.drawable.GradientDrawable();shape.setCornerRadius(dp(6));shape.setColor(state==0?android.graphics.Color.TRANSPARENT:0x343b6590);if(state!=0)shape.setStroke(Math.max(1,dp(1)),PreviewAccent.color(owner));states.addState(state==0?new int[]{}:new int[]{state},shape);
        }
        return states;
    }
    private static void switchTab(int selected){flushNested();tab=selected;rootFocus=-1;renderTab();}
    private static TVCardView card(int title){for(TVCardView c:adapter==null?Collections.<TVCardView>emptyList():adapter.previewCards())if(owner.getString(title).equals(c.previewTitle()))return c;return null;}
    private static void clear(){expansion=null;expansionAnchor=null;invokingRow=null;trackRows=adjustmentRows=null;flushNested();liveCard=null;if(content.hasFocus())tabs.getChildAt(tab).requestFocus();content.removeAllViews();scroll=new ScrollView(owner);scroll.setFillViewport(false);rows=new LinearLayout(owner);rows.setOrientation(LinearLayout.VERTICAL);scroll.addView(rows);content.addView(scroll,new FrameLayout.LayoutParams(-1,-1));for(int i=0;i<4;i++)tabs.getChildAt(i).setSelected(i==tab);}
    private static void columns(){
        content.removeAllViews();LinearLayout columns=new LinearLayout(owner);columns.setBaselineAligned(false);content.addView(columns,new FrameLayout.LayoutParams(-1,-1));
        for(int i=0;i<2;i++){ScrollView pane=new ScrollView(owner);LinearLayout list=new LinearLayout(owner);list.setOrientation(LinearLayout.VERTICAL);pane.addView(list);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(0,-1,1);if(i==0)size.rightMargin=dp(16);columns.addView(pane,size);if(i==0)trackRows=list;else adjustmentRows=list;}
        rows=trackRows;
    }
    private static void renderTab(){
        if(owner==null||closing)return;clear();backAction=null;restoreParent=PreviewPlaybackMenus::renderTab;
        if(tab==0||tab==1){TVCardView selected=card(tab==1?R.string.menu_subtitles:R.string.menu_audio);if(selected!=null){if(tab==0)columns();addNative(selected,true,false);}else heading(tab==1?"No subtitle tracks":"No audio tracks");if(tab==1)subtitlePreferences();}
        else {
            if(tab==2)columns();
            if(tab==3){TVCardView audio=card(R.string.menu_audio);if(audio!=null&&audio.previewMenu()!=null)for(int i=0;i<audio.previewMenu().getChildCount();i++){View v=audio.previewMenu().getChildAt(i);if(v instanceof TVMenuItem&&owner.getString(R.string.player_pref_audio_speed_title).equals(((TVMenuItem)v).getText()))nativeRow((TVMenuItem)v,"Playback Speed",PreviewPlaybackMenus::renderTab);}}
            for(TVCardView c:adapter==null?Collections.<TVCardView>emptyList():adapter.previewCards()){
                String title=c.previewTitle();boolean play=owner.getString(R.string.pref_play_mode_title).equals(title);
                if(owner.getString(R.string.menu_subtitles).equals(title)||owner.getString(R.string.menu_audio).equals(title)||owner.getString(R.string.menu_info).equals(title)||owner.getString(R.string.preferences).equals(title))continue;
                boolean notification=owner.getString(R.string.notification_mode).equals(title);if(tab==3?!(play||notification):(play||notification))continue;
                if(tab==2&&owner.getString(R.string.pref_format_mode_title).equals(title)){rows=trackRows;heading("Format");addNative(c,true,false);}else {if(tab==2)rows=adjustmentRows;action(title,false,true,null,()->select(c,PreviewPlaybackMenus::renderTab,-1,false));}
            }
        }
        if(trackRows!=null)rows=trackRows;if(rows.getChildCount()==0&&(adjustmentRows==null||adjustmentRows.getChildCount()==0))heading("No available options");
    }
    private static void select(TVCardView selected,Runnable parent,int focus,boolean other){
        LinearLayout target=expandUnderInvoker();if(target==null)return;
        LinearLayout previous=rows;rows=target;restoreParent=parent;addNative(selected,false,other);rows=previous;
        focusExpansion();
    }
    private static void addNative(TVCardView selected,boolean root,boolean other){
        liveCard=selected;TVMenu menu=selected.previewMenu();if(menu==null||menu.getChildCount()==0){heading(tab==0?"No audio tracks":"No available options");return;}
        boolean subs=owner.getString(R.string.menu_subtitles).equals(selected.previewTitle()),audio=owner.getString(R.string.menu_audio).equals(selected.previewTitle());boolean hasOther=false;String last="";
        List<View> items=new ArrayList<>();for(int i=0;i<menu.getChildCount();i++)items.add(menu.getChildAt(i));
        if(audio)items.sort(Comparator.comparingInt(PreviewPlaybackMenus::audioGroup));
        for(View view:items){if(!(view instanceof TVMenuItem)||view.getVisibility()!=View.VISIBLE)continue;TVMenuItem item=(TVMenuItem)view;
            if(audio&&owner.getString(R.string.player_pref_audio_speed_title).equals(item.getText()))continue;
            boolean otherTrack=subs&&Boolean.FALSE.equals(item.getTag());if(otherTrack)hasOther=true;if(other?!otherTrack:otherTrack)continue;
            String label=androidx.core.text.HtmlCompat.fromHtml(item.getText(),androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
            if(subs&&owner.getString(R.string.get_subtitles_online).equals(item.getText()))label="Download Subtitles";
            if(subs&&owner.getString(R.string.get_subtitles_on_drive).equals(item.getText()))label="Choose Subtitles";
            if(subs&&owner.getString(R.string.menu_player_settings).equals(item.getText()))label="Subtitle Appearance";
            if(subs||audio){String group=(item.findViewById(R.id.info_text) instanceof android.widget.Checkable)?(audio&&item.getText().equals(owner.getString(R.string.pref_audio_filt_title))||item.getText().equals(owner.getString(R.string.pref_audio_filt_night_mode))||item.getText().equals(owner.getString(R.string.spatialization_capabilities))?"Enhancements":(audio?"Track":"Tracks")):label.contains("Subtitles")&&!label.equals("Subtitle Appearance")?"Add subtitles":audio?"Sync":"Timing & appearance";if(audio&&trackRows!=null)rows=(group.equals("Track")||group.equals("Tracks"))?trackRows:adjustmentRows;if(!group.equals(last)){heading(group);last=group;}}
            Runnable parent=root?PreviewPlaybackMenus::renderTab:restoreParent;nativeRow(item,label,parent);
        }
        if(hasOther&&!other)action("Other languages",false,true,null,()->select(selected,PreviewPlaybackMenus::renderTab,-1,true));
    }
    private static int audioGroup(View view){
        if(!(view instanceof TVMenuItem))return 3;TVMenuItem item=(TVMenuItem)view;String label=item.getText();
        if(label.equals(owner.getString(R.string.pref_audio_filt_title))||label.equals(owner.getString(R.string.pref_audio_filt_night_mode))||label.equals(owner.getString(R.string.spatialization_capabilities)))return 2;
        return item.findViewById(R.id.info_text) instanceof android.widget.Checkable?0:1;
    }
    private static void nativeRow(TVMenuItem item,String label,Runnable parent){
        Object language=item.getTag(R.id.preview_track_language);
        TextView row=action(label,item.isChecked(),item.isEnabled()&&item.isFocusable(),language instanceof String?(String)language:null,()->{restoreParent=parent;item.previewClick();if(nested==null&&isShowing())refreshChecks();});
        if(item.isSwitchable()||owner.getString(R.string.pref_audio_filt_night_mode).equals(item.getText()))switchState(row,label,item.isChecked());
    }
    private static void switchState(TextView row,String label,boolean on){
        final int accent=PreviewAccent.color(owner);row.setText(label+"  "+(on?"On":"Off"));row.setContentDescription(label+", "+(on?"On":"Off"));
            android.graphics.drawable.Drawable indicator=new android.graphics.drawable.Drawable(){final android.graphics.Paint paint=new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);public void draw(android.graphics.Canvas canvas){android.graphics.Rect bounds=getBounds();paint.setColor(on?accent:0xff526174);canvas.drawRoundRect(bounds.left,bounds.top,bounds.right,bounds.bottom,dp(10),dp(10),paint);paint.setColor(android.graphics.Color.WHITE);canvas.drawCircle(on?bounds.right-dp(10):bounds.left+dp(10),bounds.exactCenterY(),dp(7),paint);}public void setAlpha(int alpha){paint.setAlpha(alpha);}public void setColorFilter(android.graphics.ColorFilter filter){paint.setColorFilter(filter);}public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}};
            indicator.setBounds(0,0,dp(36),dp(20));row.setCompoundDrawables(null,null,indicator,null);row.setCompoundDrawablePadding(dp(12));
    }
    private static void refreshChecks(){
        if(owner==null)return;
        View focused=current.getCurrentFocus();Object key=expansionAnchor!=null?expansionAnchor.getTag():focused==null?null:focused.getTag();int occurrence=0;
        // Tracks may have identical metadata labels. Preserve the matching occurrence,
        // while still following a uniquely named track when new rows precede it.
        if(key!=null&&focused!=null&&expansionAnchor==null&&focused.getParent()==rows){
            for(int i=0;i<rows.indexOfChild(focused);i++)if(Objects.equals(key,rows.getChildAt(i).getTag()))occurrence++;
        }
        Runnable page=restoreParent;if(page!=null)page.run();
        if(key!=null)for(View row:content.getFocusables(View.FOCUS_FORWARD)){
            if(Objects.equals(key,row.getTag())&&occurrence--==0){row.requestFocus();break;}
        }
    }
    static void refresh(Activity activity,TVCardView changed){if(isShowing()&&changed==liveCard&&nested==null)refreshChecks();}
    private static void heading(String text){TextView heading=SharedThreePanel.text(owner,text,12);heading.setTextColor(0xffb7c0cc);heading.setPadding(0,dp(12),0,dp(5));heading.setFocusable(false);rows.addView(heading);}
    private static TextView action(String label,boolean checked,boolean enabled,String code,Runnable run){
        int index=rows.getChildCount();TextView text=SharedThreePanel.text(owner,(checked?"✓  ":"")+label+(enabled?"":" — unavailable"),14);text.setTag("playback.option:"+label);text.setFocusable(enabled);text.setFocusableInTouchMode(enabled);text.setEnabled(enabled);text.setGravity(Gravity.CENTER_VERTICAL);text.setPadding(dp(10),dp(9),dp(10),dp(9));text.setMinimumHeight(dp(40));text.setBackground(SharedThreePanel.accentFocus(owner));text.setOnClickListener(v->{invokingRow=text;run.run();});
        if(code!=null)PreviewLanguageIcon.apply(text,code);
        text.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_UP){ViewGroup scope=text.getParent() instanceof ViewGroup?(ViewGroup)text.getParent():content;View next=FocusFinder.getInstance().findNextFocus(scope,text,View.FOCUS_UP);if(next==null||next==text){tabs.getChildAt(tab).requestFocus();return true;}}if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT){View next=FocusFinder.getInstance().findNextFocus(content,text,key==KeyEvent.KEYCODE_DPAD_LEFT?View.FOCUS_LEFT:View.FOCUS_RIGHT);if(next!=null&&next!=text)next.requestFocus();return true;}return false;});rows.addView(text,new LinearLayout.LayoutParams(-1,-2));return text;
    }
    private static SharedPreferences preferences(){return PreferenceManager.getDefaultSharedPreferences(owner);}
    private static void subtitlePreferences(){
        heading("Subtitle preferences");
        action("Reading language",false,!preferences().getBoolean("subtitles_hide_default",false),null,()->languages(false));
        action("Download languages",false,true,null,()->languages(true));
        toggle("Prepare Subtitle Searches",com.archos.mediacenter.video.utils.SubtitleDiscovery.ENABLED,false,false);
        toggle("Prepare Current Season",com.archos.mediacenter.video.utils.SubtitleDiscovery.PREFETCH,true,false);
        toggle("Subtitles by Default","subtitles_hide_default",false,true);
    }
    private static void toggle(String label,String key,boolean initial,boolean invert){TextView toggleRow=action(label,invert?!preferences().getBoolean(key,initial):preferences().getBoolean(key,initial),true,null,()->{preferences().edit().putBoolean(key,!preferences().getBoolean(key,initial)).apply();renderTab();View row=content.findViewWithTag("playback.option:"+label);if(row!=null)row.requestFocus();});switchState(toggleRow,label,invert?!preferences().getBoolean(key,initial):preferences().getBoolean(key,initial));}
    private static void languages(boolean multiple){
        LinearLayout target=expandUnderInvoker();if(target==null)return;LinearLayout previous=rows;rows=target;restoreParent=PreviewPlaybackMenus::renderTab;heading(multiple?"Download languages":"Reading language");
        Map<String,String> choices=VideoPreferencesCommon.subtitleLanguageChoices(owner);Set<String> selected=new HashSet<>(preferences().getStringSet("languages_list",Collections.singleton(Locale.getDefault().getLanguage())));
        for(Map.Entry<String,String> choice:choices.entrySet()){String code=choice.getKey();boolean checked=multiple?selected.contains(code):code.equals(preferences().getString("favSubLang",Locale.getDefault().getLanguage()));action(choice.getValue(),checked,true,PreviewLanguageIcon.providerCode(code),()->{if(multiple){Set<String> values=new HashSet<>(preferences().getStringSet("languages_list",Collections.singleton(Locale.getDefault().getLanguage())));if(!values.remove(code))values.add(code);if(values.isEmpty())return;preferences().edit().putStringSet("languages_list",values).apply();}else preferences().edit().putString("favSubLang",code).apply();Object anchor=expansionAnchor==null?null:expansionAnchor.getTag();renderTab();View row=anchor==null?null:content.findViewWithTag(anchor);if(row!=null)row.requestFocus();});}
        rows=previous;focusExpansion();
    }
    static void showNested(Activity activity,TVCardDialog card){
        if(!isShowing()){owner=activity;origin=activity.getCurrentFocus();tab=1;shell();renderTab();}
        Runnable parent=restoreParent;LinearLayout target=expandUnderInvoker();if(target==null)return;
        if(card.getParent() instanceof ViewGroup)((ViewGroup)card.getParent()).removeView(card);
        card.setAlpha(1f);card.setPadding(0,0,0,0);card.setBackgroundColor(android.graphics.Color.TRANSPARENT);compact(card,activity);nested=card;
        card.setPreviewDismiss(()->{nested=null;if(!closing){Object key=expansionAnchor==null?null:expansionAnchor.getTag();collapseExpansion(false);if(parent!=null)parent.run();View row=key==null?null:content.findViewWithTag(key);if(row!=null)row.requestFocus();}});
        target.addView(card,new LinearLayout.LayoutParams(-1,-2));backAction=null;
    }
    /** Keep native callbacks and value ranges, changing only their presentation owner. */
    private static LinearLayout expandUnderInvoker(){
        View anchor=invokingRow;collapseExpansion(false);
        if(anchor!=null&&anchor.getParent() instanceof LinearLayout){
            LinearLayout parent=(LinearLayout)anchor.getParent();expansion=new LinearLayout(owner);expansion.setOrientation(LinearLayout.VERTICAL);expansion.setPadding(dp(12),0,0,dp(6));expansion.setTag("playback.inline");expansionAnchor=anchor;
            parent.addView(expansion,parent.indexOfChild(anchor)+1,new LinearLayout.LayoutParams(-1,-2));
        }else if(rows!=null){expansion=new LinearLayout(owner);expansion.setOrientation(LinearLayout.VERTICAL);expansion.setTag("playback.inline");rows.addView(expansion,new LinearLayout.LayoutParams(-1,-2));}
        return expansion;
    }
    private static void focusExpansion(){if(expansion!=null)for(View view:expansion.getFocusables(View.FOCUS_FORWARD))if(view.isEnabled()&&view.requestFocus())return;}
    private static void collapseExpansion(boolean focus){
        View anchor=expansionAnchor;flushNested();
        if(expansion!=null&&expansion.getParent() instanceof ViewGroup)((ViewGroup)expansion.getParent()).removeView(expansion);
        expansion=null;expansionAnchor=null;backAction=null;
        if(focus&&anchor!=null&&anchor.isAttachedToWindow())anchor.requestFocus();
    }

    private static void focusContent(){if(content!=null)for(View view:content.getFocusables(View.FOCUS_FORWARD))if(view.isEnabled()&&view.isShown()&&view.requestFocus())return;}
    private static void compact(View view,Activity activity){
        if(view instanceof TextView){TextView text=(TextView)view;text.setTextSize(14);text.setMaxLines(3);text.setEllipsize(android.text.TextUtils.TruncateAt.END);text.setTextColor(android.graphics.Color.WHITE);text.setMinHeight(0);}
        if(view instanceof TVMenuItem)view.setBackground(SharedThreePanel.accentFocus(activity));
        if(view instanceof ViewGroup){((ViewGroup)view).setLayoutTransition(null);for(int i=0;i<((ViewGroup)view).getChildCount();i++)compact(((ViewGroup)view).getChildAt(i),activity);}
    }
    private static int dp(int n){return PreviewDialog.dp(owner,n);}
}
