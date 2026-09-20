package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;

/** Indexed values only: opening a page never enumerates a filesystem or contacts a source. */
public final class PreviewLibrarySummary {
    public static String describe(Context context, Snapshot snapshot, boolean television) {
        List<Entry> files=television?snapshot.episodes:snapshot.movies;
        Set<String> local=new HashSet<>(),network=new HashSet<>();
        long localBytes=0,networkBytes=0;int known=0;
        for(Entry entry:files){
            if(!(entry.media instanceof Video))continue;
            // getUri() identifies the database row, including for WebDAV/SMB media.
            // The indexed file URI identifies storage; resolving it performs no I/O.
            Video video=(Video)entry.media;Uri uri=video.getFileUri();String scheme=uri==null?null:uri.getScheme();
            boolean remote=scheme!=null&&!scheme.equalsIgnoreCase("file")&&!scheme.equalsIgnoreCase("content");
            String key=television?"show:"+entry.show:entry.key();
            if(remote){network.add(key);networkBytes+=entry.bytes;}else{local.add(key);localBytes+=entry.bytes;}
            known+=entry.knownSizes;
        }
        String unit=television?" shows":" movies";
        return (television?snapshot.shows.size():snapshot.movies.size())+(television?" Shows":" Movies")
            +"\nTotal Size    "+size(context,localBytes+networkBytes,known<files.size())
            +"\nLocal Storage    "+local.size()+unit+" · "+size(context,localBytes,false)
            +"\nNetwork / WebDAV    "+network.size()+unit+" · "+size(context,networkBytes,false)
            +(known<files.size()?"\nIndexed sizes only; some file sizes are unknown":"")
            +(television&&!Collections.disjoint(local,network)?"\nShows spanning sources appear in both source counts":"");
    }
    private static String size(Context c,long bytes,boolean partial){return (partial?"≥ ":"")+Formatter.formatShortFileSize(c,bytes);}
    private PreviewLibrarySummary(){}
}
