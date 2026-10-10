package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.details.*;
import java.util.*;
import com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity;

/** Library Health uses its host browser's centre and Information panels; no second shell. */
public final class LibraryHealthPanel {
    public static final String[] NAMES={"Unmatched Media","Incorrect Matches","Missing Metadata or Artwork","Unavailable Files","Source Problems"};
    private final Context context;private final LinearLayout centre,right;private final Snapshot snapshot;
    public LibraryHealthPanel(Context c,LinearLayout middle,LinearLayout information,Snapshot s){context=c;centre=middle;right=information;snapshot=s==null?new Snapshot():s;}
    private TextView action(String label,Runnable run){return SharedThreePanel.action(context,label,run);}
    private void information(String title,String text){right.removeAllViews();SharedThreePanel.heading(right,title);SharedThreePanel.divider(right);right.addView(SharedThreePanel.text(context,text,15));}
    public void show(int category){centre.removeAllViews();SharedThreePanel.heading(centre,category<0?"Library Health":NAMES[category]);
        if(category<0){centre.addView(SharedThreePanel.text(context,LibraryHealth.needsAttention(context,snapshot)?"Your library has issues to review.":"Your library is healthy",18));centre.addView(SharedThreePanel.text(context,"Expand Library Health to review identification, metadata, availability and source issues. Importing and identification in progress are excluded from warnings.",15));information("Library Health",LibraryHealth.report(context));return;}
        android.content.SharedPreferences health=context.getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0);
        if(category==4){int count=0;for(String key:health.getAll().keySet())if(key.startsWith("offline:")||key.startsWith("problem:")){String source=key.substring(8);TextView row=action(source,()->source(source));row.setOnFocusChangeListener((v,yes)->{if(yes)source(source);});centre.addView(row);SharedThreePanel.divider(centre);count++;}if(count==0)empty();return;}
        boolean importing=com.archos.mediaprovider.ImportState.VIDEO.isInitialImport()||com.archos.mediaprovider.ImportState.VIDEO.isRegularImport()||com.archos.mediaprovider.video.LoaderUtils.getScrapeInProgress();
        int count=0;for(Entry entry:snapshot.technical){if(!(entry.media instanceof Video))continue;Video video=(Video)entry.media;boolean matched=video instanceof Movie||video instanceof Episode;String plot=video.getDescriptionBody();boolean include=category==0?!importing&&!matched:category==1?health.getBoolean("incorrect:"+video.getId(),false):category==2?!importing&&matched&&(video.getPosterUri()==null||plot==null||plot.trim().isEmpty()):LibraryHealth.state(context,video)!=LibraryHealth.State.AVAILABLE;if(!include)continue;TextView row=action(video.getFilenameNonCryptic(),()->review(video,category));row.setTag("health:"+video.getId());row.setOnFocusChangeListener((v,yes)->{if(yes)review(video,category);});centre.addView(row);SharedThreePanel.divider(centre);if(count++==0)review(video,category);}if(count==0)empty();
    }
    private void empty(){centre.addView(SharedThreePanel.text(context,"No issues in this category",16));information("Library Health","Media still being imported or identified is excluded from unmatched and missing-metadata warnings. Your library data has not changed.");}
    private void source(String source){information("Source Problem",source+"\n\nReconnect storage or check this source. Library records and viewing progress are retained.");right.addView(action("Scan Library",()->PreviewLibraryScan.request(context)));}
    private void review(Video video,int category){information(video.getName(),"Filename\n"+video.getFilenameNonCryptic()+"\n\nLocation\n"+com.archos.mediaprovider.video.SupernovaLibraryPolicy.canonical(video.getFileUri())+"\n\nMatch Status\n"+(video instanceof Movie||video instanceof Episode?"Matched":"Unmatched")+"\n\nAvailability\n"+LibraryHealth.message(LibraryHealth.state(context,video)));
        right.addView(action("View Details",()->context.startActivity(new Intent(context,VideoDetailsActivity.class).putExtra(VideoDetailsFragment.EXTRA_VIDEO,video))));
        right.addView(action("Find a Match",()->context.startActivity(new Intent(context,ManualVideoScrappingActivity.class).putExtra(ManualVideoScrappingActivity.EXTRA_VIDEO,video))));
        if(category==1)right.addView(action("Match Is Correct",()->{context.getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0).edit().remove("incorrect:"+video.getId()).apply();show(category);}));
    }
}
