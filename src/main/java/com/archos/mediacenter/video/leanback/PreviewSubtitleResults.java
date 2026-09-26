package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.util.*;
import java.util.function.Consumer;

/** Shared subtitle-result review over the existing OpenSubtitles search/download implementation. */
public final class PreviewSubtitleResults {
    public static Dialog show(Context context,List<OpenSubtitlesSearchResult> results,Consumer<OpenSubtitlesSearchResult> download){
        List<OpenSubtitlesSearchResult> snapshot=new ArrayList<>(results);String[] labels=new String[snapshot.size()];
        for(int i=0;i<labels.length;i++){OpenSubtitlesSearchResult item=snapshot.get(i);labels[i]=language(item)+" · "+safe(item.getFileName(),"Subtitle file");}
        final Dialog[] parent={null};final boolean[] accepted={false};
        parent[0]=PreviewDialog.choose(context,"Download Subtitles",labels,-1,Collections.emptySet(),false,index->{
            if(accepted[0])return;OpenSubtitlesSearchResult selected=snapshot.get(index);
            String details=safe(selected.getFileName(),"Subtitle file")+"\n\nLanguage: "+language(selected)+"\nSource: OpenSubtitles";
            if(Boolean.TRUE.equals(selected.getMoviehashMatch()))details+="\nMatches this media file's hash";
            if(selected.getRelease()!=null&&!selected.getRelease().trim().isEmpty())details+="\nRelease: "+selected.getRelease();
            PreviewDialog.review(context,"Subtitle Download",details,"Download",()->{
                if(accepted[0])return;accepted[0]=true;parent[0].dismiss();download.accept(selected);
            });
        });return parent[0];
    }
    private static String language(OpenSubtitlesSearchResult item){return safe(item.getLanguage(),"Unknown language");}
    private static String safe(String value,String fallback){return value==null||value.trim().isEmpty()?fallback:value;}
    private PreviewSubtitleResults(){}
}
