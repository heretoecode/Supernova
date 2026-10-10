package com.archos.mediacenter.video.leanback;

import android.graphics.Typeface;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.List;

/** One title/statistics geometry for Movies, TV Shows and the filtered custom page. */
public final class LibraryPageHeader {
    public static void add(LinearLayout target,String title,Snapshot snapshot,boolean television){add(target,title,PreviewLibrarySummary.describe(target.getContext(),snapshot,television),television?"TV Shows":"Movies");}
    public static void add(LinearLayout target,String title,Snapshot snapshot,List<Entry> entries){add(target,title,PreviewLibrarySummary.describe(target.getContext(),snapshot,entries),"Library");}
    private static void add(LinearLayout target,String name,String summary,String kind){
        target.setOrientation(LinearLayout.VERTICAL);TextView title=SharedThreePanel.text(target.getContext(),name,30);title.setTypeface(null,Typeface.BOLD);target.addView(title);
        String[] lines=summary.split("\n"),icons={kind,"Total Size","Local Storage","Network"};for(int i=0;i<lines.length;i++){TextView row=SharedThreePanel.text(target.getContext(),lines[i],13);row.setTextColor(0xffe1e9ef);row.setPadding(0,SharedThreePanel.dp(target.getContext(),2),0,SharedThreePanel.dp(target.getContext(),2));if(i<icons.length)PreviewIcon.apply(row,icons[i],17);target.addView(row);}
    }
    private LibraryPageHeader(){}
}
