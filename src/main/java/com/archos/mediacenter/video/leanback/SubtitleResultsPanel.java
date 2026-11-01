package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.util.*;
import java.util.function.Consumer;

/** Image A2 presentation over already retrieved results; no new provider queries or backend. */
public final class SubtitleResultsPanel {
    public static Dialog show(Context context,List<OpenSubtitlesSearchResult> results,Consumer<OpenSubtitlesSearchResult> download){
        List<OpenSubtitlesSearchResult> snapshot=new ArrayList<>(results);SubtitlePanelStyle p=new SubtitlePanelStyle(context,false);Dialog dialog=PreviewDialog.create(context);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);LinearLayout panel=p.panel();panel.setTag("subtitles.download");p.header(panel,"Download Subtitles",dialog::cancel);
        TextView search=p.text("Search for subtitles...",30);search.setTag("subtitles.download.search");search.setFocusable(true);search.setFocusableInTouchMode(true);search.setGravity(Gravity.CENTER_VERTICAL);search.setPadding(p.px(24),p.px(12),p.px(24),p.px(12));search.setBackground(p.surface(0x550b2a4a,0xff8fa4ba,22));PreviewIcon.apply(search,"search",24);panel.addView(search,new LinearLayout.LayoutParams(-1,p.px(90)));String[] query={""};
        ScrollView scroll=new ScrollView(context);scroll.setSmoothScrollingEnabled(false);p.scrollbar(scroll);LinearLayout rows=new LinearLayout(context);rows.setOrientation(LinearLayout.VERTICAL);scroll.addView(rows);LinearLayout.LayoutParams body=new LinearLayout.LayoutParams(-1,0,1);body.topMargin=p.px(18);panel.addView(scroll,body);boolean[] accepted={false};
        Runnable render=()->{rows.removeAllViews();int count=0;for(int i=0;i<snapshot.size();i++){OpenSubtitlesSearchResult item=snapshot.get(i);String filename=safe(item.getFileName(),"Subtitle file"),language=language(item);if(!query[0].isEmpty()&&!(filename+" "+language).toLowerCase(Locale.ROOT).contains(query[0].toLowerCase(Locale.ROOT)))continue;
            LinearLayout row=p.row("subtitles.result."+i);row.setMinimumHeight(p.px(113));row.addView(p.icon("cc",62));ImageView flag=new ImageView(context);flag.setImageDrawable(new PreviewLanguageIcon(context,PreviewLanguageIcon.providerCode(item.getLanguage())));LinearLayout.LayoutParams f=new LinearLayout.LayoutParams(p.px(66),p.px(46));f.leftMargin=p.px(22);f.rightMargin=p.px(22);row.addView(flag,f);
            LinearLayout copy=new LinearLayout(context);copy.setOrientation(LinearLayout.VERTICAL);TextView title=p.text(filename,32);title.setTypeface(null,Typeface.BOLD);title.setSingleLine(true);title.setEllipsize(android.text.TextUtils.TruncateAt.END);copy.addView(title,new LinearLayout.LayoutParams(-1,-2));TextView metadata=p.text(language+" · Size unavailable",25);metadata.setTextColor(0xffc9d6e6);metadata.setSingleLine(true);metadata.setEllipsize(android.text.TextUtils.TruncateAt.END);metadata.setPadding(0,p.px(6),0,0);copy.addView(metadata,new LinearLayout.LayoutParams(-1,-2));row.addView(copy,new LinearLayout.LayoutParams(0,-2,1));row.addView(p.icon("download",42));row.setContentDescription(filename+", "+language+", Download");
            row.setOnClickListener(v->{if(accepted[0])return;String details=filename+"\n\nLanguage: "+language+"\nSource: OpenSubtitles";if(Boolean.TRUE.equals(item.getMoviehashMatch()))details+="\nMatches this media file's hash";if(item.getRelease()!=null&&!item.getRelease().trim().isEmpty())details+="\nRelease: "+item.getRelease();PreviewDialog.review(context,"Subtitle Download",details,"Download",()->{if(accepted[0])return;accepted[0]=true;dialog.dismiss();download.accept(item);});});rows.addView(row,new LinearLayout.LayoutParams(-1,-2));p.rule(rows);count++;}
            if(count==0){TextView empty=p.text("No matching subtitles",28);empty.setPadding(0,p.px(22),0,p.px(22));rows.addView(empty);} };
        search.setContentDescription("Search returned subtitle results");search.setOnClickListener(v->PreviewTextInput.showValidated(context,"Search for subtitles",query[0],200,value->null,value->{query[0]=value.trim();search.setText(query[0].isEmpty()?"Search for subtitles...":query[0]);render.run();search.requestFocus();}));render.run();p.show(dialog,panel,p.px(765));if(rows.getChildCount()>0&&rows.getChildAt(0).isFocusable())rows.getChildAt(0).requestFocus();else search.requestFocus();return dialog;
    }
    private static String safe(String value,String fallback){return value==null||value.trim().isEmpty()?fallback:value;}
    private static String language(OpenSubtitlesSearchResult item){String code=safe(item.getLanguage(),"");String name=code.isEmpty()?"Unknown language":Locale.forLanguageTag(code.replace('_','-')).getDisplayLanguage();if(name.isEmpty()||name.equals(code))name=code.isEmpty()?"Unknown language":code;return name+(Boolean.TRUE.equals(item.getHearingImpaired())?" (SDH)":"");}
    private SubtitleResultsPanel(){}
}
