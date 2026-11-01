package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.util.*;
import java.util.function.Consumer;

/** Shared subtitle-result review over the existing OpenSubtitles search/download implementation. */
public final class PreviewSubtitleResults {
    public static Dialog show(Context context,List<OpenSubtitlesSearchResult> results,Consumer<OpenSubtitlesSearchResult> download){
        return SubtitleResultsPanel.show(context,results,download);
    }
    private static String language(OpenSubtitlesSearchResult item){return safe(item.getLanguage(),"Unknown language");}
    private static String safe(String value,String fallback){return value==null||value.trim().isEmpty()?fallback:value;}
    private PreviewSubtitleResults(){}
}
