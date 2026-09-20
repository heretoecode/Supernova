package com.archos.mediacenter.video.leanback.settings;
import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.preference.*;
import java.util.*;
import com.archos.mediacenter.video.leanback.PreviewDialog;
final class PreviewPreferenceDialogs {
 static boolean show(PreferenceFragmentCompat f,Preference p){
  if(p instanceof ListPreference){ListPreference list=(ListPreference)p;CharSequence[] entries=list.getEntries(),values=list.getEntryValues();if(entries==null||values==null)return true;String[] labels=new String[entries.length];for(int i=0;i<labels.length;i++)labels[i]=entries[i].toString();PreviewDialog.choose(f.requireContext(),String.valueOf(p.getTitle()),labels,list.findIndexOfValue(list.getValue()),i->{String value=values[i].toString();if(list.callChangeListener(value))list.setValue(value);});return true;}
  if(p instanceof MultiSelectListPreference){if(p.getKey()!=null&&p.getKey().startsWith("streaming_providers_")){providers(f,(MultiSelectListPreference)p);return true;}multi(f,(MultiSelectListPreference)p,new HashSet<>(((MultiSelectListPreference)p).getValues()),0);return true;}
  if(p instanceof EditTextPreference){edit(f,(EditTextPreference)p);return true;}
  return false;
 }
 private static void providers(PreferenceFragmentCompat f,MultiSelectListPreference p){
  CharSequence[] values=p.getEntryValues(),entries=p.getEntries();if(values==null||entries==null)return;
  Set<String> selected=new HashSet<>(p.getValues());List<String> labels=new ArrayList<>(),ids=new ArrayList<>();Set<Integer> checked=new HashSet<>();
  for(boolean chosen:new boolean[]{true,false}){labels.add(chosen?"— Selected Providers":"— Other Providers");ids.add(null);List<Integer> group=new ArrayList<>();for(int i=0;i<values.length;i++)if(selected.contains(values[i].toString())==chosen)group.add(i);group.sort(Comparator.comparing(i->entries[i].toString().toLowerCase(Locale.ROOT)));for(int i:group){if(chosen)checked.add(labels.size());labels.add(entries[i].toString());ids.add(values[i].toString());}}
  labels.add("Done");ids.add(null);Dialog[] dialog={null};
  dialog[0]=PreviewDialog.choose(f.requireContext(),String.valueOf(p.getTitle()),labels.toArray(new String[0]),-1,checked,false,i->{
   String id=ids.get(i);if(id==null){if(i==ids.size()-1)dialog[0].dismiss();return;}Set<String> next=new HashSet<>(selected);if(!next.remove(id))next.add(id);
   if(p.callChangeListener(next)){p.setValues(next);selected.clear();selected.addAll(next);checked.clear();for(int j=0;j<ids.size();j++)if(selected.contains(ids.get(j)))checked.add(j);PreviewDialog.updateChecks(dialog[0],checked);}
  });
  Map<String,String> logos=com.archos.mediacenter.video.streaming.PreviewProviderIcons.catalogue(f.requireContext());for(int i=0;i<ids.size();i++)com.archos.mediacenter.video.streaming.PreviewProviderIcons.bind(dialog[0],i,logos.get(ids.get(i)));
 }
 private static void multi(PreferenceFragmentCompat f,MultiSelectListPreference p,Set<String> selected,int focus){
  CharSequence[] values=p.getEntryValues(),entries=p.getEntries();if(values==null||entries==null)return;
  String[] labels=new String[entries.length+1];Set<Integer> checked=new HashSet<>();for(int i=0;i<entries.length;i++){labels[i]=entries[i].toString();if(selected.contains(values[i].toString()))checked.add(i);}labels[entries.length]="Done";
  Dialog[] d={null};d[0]=PreviewDialog.choose(f.requireContext(),String.valueOf(p.getTitle()),labels,focus,checked,false,i->{
   if(i==entries.length){d[0].dismiss();return;}
   Set<String> next=new HashSet<>(selected);String value=values[i].toString();if(!next.remove(value))next.add(value);
   if(p.callChangeListener(next)){p.setValues(next);selected.clear();selected.addAll(next);checked.clear();for(int j=0;j<values.length;j++)if(selected.contains(values[j].toString()))checked.add(j);PreviewDialog.updateChecks(d[0],checked);}
  });
 }
 private static void edit(PreferenceFragmentCompat f,EditTextPreference p){Context c=f.requireContext();Dialog d=new Dialog(c);d.requestWindowFeature(Window.FEATURE_NO_TITLE);LinearLayout panel=new LinearLayout(c);panel.setOrientation(android.widget.LinearLayout.VERTICAL);int pad=PreviewDialog.dp(c,16);panel.setPadding(pad,pad,pad,pad);panel.setBackground(PreviewDialog.surface(c,false));TextView heading=new TextView(c);heading.setText(p.getTitle());heading.setTextSize(17);heading.setTextColor(-1);panel.addView(heading);EditText input=new EditText(c);input.setText(p.getText());input.setSingleLine(true);input.setTextSize(15);input.setTextColor(-1);panel.addView(input,new LinearLayout.LayoutParams(-1,PreviewDialog.dp(c,48)));LinearLayout actions=new LinearLayout(c);for(String label:new String[]{"Cancel","Save"}){TextView action=new TextView(c);action.setText(label);action.setTextSize(14);action.setTextColor(-1);action.setGravity(Gravity.CENTER);action.setFocusable(true);action.setBackground(PreviewDialog.focus(c));action.setOnClickListener(v->{if(label.equals("Save")){String value=input.getText().toString();if(p.callChangeListener(value))p.setText(value);}d.dismiss();});actions.addView(action,new LinearLayout.LayoutParams(0,PreviewDialog.dp(c,38),1));}panel.addView(actions);d.setContentView(panel);d.show();d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);d.getWindow().setLayout(PreviewDialog.dp(c,400),-2);input.requestFocus();}
}
