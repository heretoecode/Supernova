package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;

/** FD-007: one continuous two-row, read-only panel aligned with the library grid. */
public final class LibraryPageHeader {
    public static void add(LinearLayout target,String title,Snapshot snapshot,boolean television){add(target,title,snapshot,television?snapshot.shows:snapshot.movies);}
    public static void add(LinearLayout target,String title,Snapshot snapshot,List<Entry> entries){
        Context c=target.getContext();LibraryStatistics stats=LibraryStatistics.calculate(snapshot,entries,System.currentTimeMillis(),TimeZone.getDefault());
        target.setOrientation(LinearLayout.VERTICAL);target.setMinimumHeight(0);
        // TV reference viewport is 1080px at 540dp. Gaps scale with the actual viewport width.
        int gap=Math.max(1,Math.round(c.getResources().getDisplayMetrics().widthPixels*16f/1920));
        target.setPadding(0,gap,0,Math.max(0,gap-dp(c,3)));target.setFocusable(false);
        LinearLayout panel=new LinearLayout(c);panel.setOrientation(LinearLayout.VERTICAL);panel.setTag("library.statistics");panel.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        GradientDrawable surface=new GradientDrawable();surface.setColor(0xc90b1622);surface.setCornerRadius(dp(c,5));surface.setStroke(Math.max(1,dp(c,1)),0x447e8c99);panel.setBackground(surface);target.addView(panel,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout top=row(c,panel),bottom;
        metric(top,"Total Library",String.valueOf(stats.total),"Titles", "Library");
        metric(top,"Total Size",size(c,stats.localBytes+stats.networkBytes,stats.unknownLocal+stats.unknownNetwork),"Indexed media", "Total Size");
        metric(top,"Local Storage",String.valueOf(stats.localTitles),size(c,stats.localBytes,stats.unknownLocal),"Local Storage");
        metric(top,"Network / WebDAV",String.valueOf(stats.networkTitles),size(c,stats.networkBytes,stats.unknownNetwork),"Network");
        View rule=new View(c);rule.setBackgroundColor(0x337e8c99);panel.addView(rule,new LinearLayout.LayoutParams(-1,Math.max(1,dp(c,1))));bottom=row(c,panel);
        metric(bottom,"Unwatched",String.valueOf(stats.unwatched),stats.share(stats.unwatched),"eye-off");
        metric(bottom,"In Progress",String.valueOf(stats.inProgress),stats.share(stats.inProgress),"history");
        metric(bottom,"Added This Month",String.valueOf(stats.addedThisMonth),new java.text.SimpleDateFormat("MMMM yyyy",Locale.getDefault()).format(new Date()),"Date Added");
        long last=LibraryScanRecord.completed(c);
        LinearLayout lastCell=metric(bottom,"Last Updated",last==0?"Unknown":android.text.format.DateFormat.getDateFormat(c).format(new Date(last)),last==0?"No completed scan recorded":android.text.format.DateFormat.getTimeFormat(c).format(new Date(last)),"history");
        boolean busy=PreviewLibraryScan.building();LinearLayout statusCell=metric(bottom,"Library Status",busy?"Scanning library":LibraryScanRecord.problems(c)?"Scan needs attention":"No scan in progress",busy?"Scan in progress":"Indexed library",busy?"history":"info");
        Runnable refresh=new Runnable(){private boolean lastBusy=busy;public void run(){if(!panel.isAttachedToWindow())return;boolean active=PreviewLibraryScan.building();if(active!=lastBusy){PreviewIcon.apply((TextView)statusCell.getChildAt(0),active?"history":"info",12);lastBusy=active;}((TextView)statusCell.getChildAt(1)).setText(active?"Scanning library":LibraryScanRecord.problems(c)?"Scan needs attention":"No scan in progress");((TextView)statusCell.getChildAt(2)).setText(active?"Scan in progress":"Indexed library");long completed=LibraryScanRecord.completed(c);((TextView)lastCell.getChildAt(1)).setText(completed==0?"Unknown":android.text.format.DateFormat.getDateFormat(c).format(new Date(completed)));((TextView)lastCell.getChildAt(2)).setText(completed==0?"No completed scan recorded":android.text.format.DateFormat.getTimeFormat(c).format(new Date(completed)));panel.postDelayed(this,1000);}};
        panel.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View view){panel.post(refresh);}public void onViewDetachedFromWindow(View view){panel.removeCallbacks(refresh);}});
    }
    private static LinearLayout row(Context c,LinearLayout panel){LinearLayout row=new LinearLayout(c);row.setBaselineAligned(false);panel.addView(row,new LinearLayout.LayoutParams(-1,-2));return row;}
    private static LinearLayout metric(LinearLayout row,String label,String value,String detail,String icon){Context c=row.getContext();if(row.getChildCount()>0){View divider=new View(c);divider.setBackgroundColor(0x337e8c99);LinearLayout.LayoutParams line=new LinearLayout.LayoutParams(Math.max(1,dp(c,1)),-1);line.setMargins(0,dp(c,8),0,dp(c,8));row.addView(divider,line);}LinearLayout cell=new LinearLayout(c);cell.setOrientation(LinearLayout.VERTICAL);cell.setPadding(dp(c,10),dp(c,4),dp(c,8),dp(c,4));cell.setContentDescription(label+": "+value+". "+detail);row.addView(cell,new LinearLayout.LayoutParams(0,-1,1));
        TextView name=SharedThreePanel.text(c,label,11);name.setIncludeFontPadding(false);name.setLineSpacing(0,1);name.setTextColor(0xffbbc4cd);name.setSingleLine(true);name.setHorizontallyScrolling(false);name.setEllipsize(android.text.TextUtils.TruncateAt.END);PreviewIcon.apply(name,icon,12);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(name,8,11,1,android.util.TypedValue.COMPLEX_UNIT_SP);cell.addView(name,new LinearLayout.LayoutParams(-1,lineHeight(name,12)));
        TextView number=SharedThreePanel.text(c,value,17);number.setIncludeFontPadding(false);number.setLineSpacing(0,1);number.setTypeface(null,Typeface.BOLD);number.setSingleLine(true);number.setHorizontallyScrolling(false);number.setEllipsize(android.text.TextUtils.TruncateAt.END);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(number,10,17,1,android.util.TypedValue.COMPLEX_UNIT_SP);cell.addView(number,new LinearLayout.LayoutParams(-1,dp(c,21)));
        TextView secondary=SharedThreePanel.text(c,detail,10);secondary.setIncludeFontPadding(false);secondary.setLineSpacing(0,1);secondary.setTextColor(0xffa6b1bc);secondary.setSingleLine(true);secondary.setHorizontallyScrolling(false);secondary.setEllipsize(android.text.TextUtils.TruncateAt.END);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(secondary,8,10,1,android.util.TypedValue.COMPLEX_UNIT_SP);cell.addView(secondary,new LinearLayout.LayoutParams(-1,lineHeight(secondary,0)));return cell;
    }
    private static int lineHeight(TextView text,int minimumDp){android.graphics.Paint.FontMetrics metrics=text.getPaint().getFontMetrics();return Math.max(dp(text.getContext(),minimumDp),(int)Math.ceil(metrics.descent-metrics.ascent));}
    private static String size(Context c,long bytes,int unknown){return (unknown>0?"≥ ":"")+android.text.format.Formatter.formatShortFileSize(c,bytes);}
    private static int dp(Context c,int n){return SharedThreePanel.dp(c,n);}
    private LibraryPageHeader(){}
}
