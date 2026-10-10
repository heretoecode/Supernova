package com.archos.mediacenter.video.leanback.settings;

import android.view.*;
import android.widget.*;
import androidx.preference.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.streaming.PreviewProviderIcons;
import java.util.*;

/** Immediate native preference choices, hosted by the right Settings panel. */
final class InlinePreferenceChoices {
    static void show(LinearLayout target,Preference preference){
        target.removeAllViews();
        if(preference instanceof MultiSelectListPreference){multi(target,(MultiSelectListPreference)preference,null);return;}
        if(!(preference instanceof ListPreference))return;
        ListPreference pref=(ListPreference)preference;CharSequence[] names=pref.getEntries(),values=pref.getEntryValues();if(names==null||values==null)return;
        for(int i=0;i<Math.min(names.length,values.length);i++){String value=values[i].toString();TextView row=SharedThreePanel.accentAction(target.getContext(),(Objects.equals(pref.getValue(),value)?"✓  ":"○  ")+names[i],()->{if(pref.callChangeListener(value)){pref.setValue(value);show(target,pref);View selected=target.findViewWithTag(value);if(selected!=null)selected.requestFocus();}});row.setTag(value);language(row,pref,value);target.addView(row);SharedThreePanel.divider(target);}
    }
    private static void language(TextView row,Preference preference,String value){String key=preference.getKey();if(!Arrays.asList("ui_lang","favSubLang","favAudioLang","languages_list").contains(key))return;PreviewLanguageIcon icon=new PreviewLanguageIcon(value);int size=SharedThreePanel.dp(row.getContext(),22);icon.setBounds(0,0,size,size);row.setCompoundDrawablePadding(SharedThreePanel.dp(row.getContext(),8));row.setCompoundDrawables(icon,null,null,null);}
    private static ScrollView scroll(LinearLayout target,LinearLayout rows,int maximum){ScrollView scroll=new ScrollView(target.getContext()){@Override protected void onMeasure(int width,int height){super.onMeasure(width,MeasureSpec.makeMeasureSpec(SharedThreePanel.dp(getContext(),maximum),MeasureSpec.AT_MOST));}};scroll.addView(rows);target.addView(scroll,new LinearLayout.LayoutParams(-1,-2));return scroll;}
    private static LinearLayout column(LinearLayout target){LinearLayout list=new LinearLayout(target.getContext());list.setOrientation(LinearLayout.VERTICAL);return list;}
    private static void multi(LinearLayout target,MultiSelectListPreference pref,String focus){
        target.removeAllViews();CharSequence[] labels=pref.getEntries(),values=pref.getEntryValues();if(labels==null||values==null)return;
        boolean providers=pref.getKey()!=null&&pref.getKey().startsWith("streaming_providers_");Set<String> selected=new HashSet<>(pref.getValues());Map<String,String> logos=providers?PreviewProviderIcons.catalogue(target.getContext()):Collections.emptyMap();
        List<Integer> order=new ArrayList<>();for(int i=0;i<Math.min(labels.length,values.length);i++)order.add(i);if(providers)order.sort(Comparator.comparing(i->labels[i].toString(),String.CASE_INSENSITIVE_ORDER));
        for(boolean chosen:providers?new boolean[]{true,false}:new boolean[]{false}){
            if(providers)SharedThreePanel.heading(target,chosen?"Selected Providers":"Other Providers");LinearLayout rows=column(target);int count=0;
            for(int n:order){String id=values[n].toString();if(providers&&selected.contains(id)!=chosen)continue;final String next=next(order,values,selected,id,providers,chosen);LinearLayout row=new LinearLayout(target.getContext());row.setGravity(Gravity.CENTER_VERTICAL);row.setFocusable(true);row.setBackground(SharedThreePanel.accentFocus(target.getContext()));row.setTag(id);row.setTransitionName("provider."+id);
                ImageView icon=new ImageView(target.getContext());row.addView(icon,new LinearLayout.LayoutParams(SharedThreePanel.dp(target.getContext(),26),SharedThreePanel.dp(target.getContext(),26)));if(providers)PreviewProviderIcons.bind(icon,logos.get(id),0,"provider.choice");else icon.setImageDrawable(new PreviewLanguageIcon(id));
                TextView label=SharedThreePanel.text(target.getContext(),(selected.contains(id)?"✓  ":"○  ")+labels[n],14);label.setPadding(SharedThreePanel.dp(target.getContext(),8),0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));
                row.setOnClickListener(v->{Set<String> changed=new HashSet<>(pref.getValues());if(!changed.remove(id))changed.add(id);if(pref.callChangeListener(changed)){pref.setValues(changed);if(android.provider.Settings.Global.getFloat(target.getContext().getContentResolver(),android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1)>0)android.transition.TransitionManager.beginDelayedTransition(target,new android.transition.ChangeBounds().setDuration(180));multi(target,pref,next);}});rows.addView(row,new LinearLayout.LayoutParams(-1,SharedThreePanel.dp(target.getContext(),42)));SharedThreePanel.divider(rows);count++;
            }
            if(count==0)rows.addView(SharedThreePanel.text(target.getContext(),chosen?"No providers selected":"No other providers",14));ScrollView group=scroll(target,rows,providers&&chosen?140:230);if(providers&&!chosen)group.setLayoutParams(new LinearLayout.LayoutParams(-1,0,1));
        }
        if(focus!=null)target.post(()->{View row=target.findViewWithTag(focus);if(row==null){ArrayList<View> candidates=new ArrayList<>();target.addFocusables(candidates,View.FOCUS_DOWN);if(!candidates.isEmpty())row=candidates.get(0);}if(row!=null){row.requestFocus();row.requestRectangleOnScreen(new android.graphics.Rect(0,0,row.getWidth(),row.getHeight()),false);}});
    }
    static String next(List<Integer> order,CharSequence[] values,Set<String> selected,String removed,boolean providers,boolean chosen){List<String> group=new ArrayList<>();for(int i:order){String id=values[i].toString();if(!providers||selected.contains(id)==chosen)group.add(id);}int index=group.indexOf(removed);if(index>=0&&index+1<group.size())return group.get(index+1);if(index>0)return group.get(index-1);for(int i:order){String id=values[i].toString();if(!id.equals(removed))return id;}return null;}
    private InlinePreferenceChoices(){}
}
