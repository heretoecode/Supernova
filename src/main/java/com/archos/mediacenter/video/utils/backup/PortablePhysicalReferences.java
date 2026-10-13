package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import java.util.*;
import org.json.JSONException;

/** Exact private location fingerprints, never path text or weak title/size guesses. Ambiguity stays unresolved. */
public final class PortablePhysicalReferences {
    private final Map<Long,Video> videos=new LinkedHashMap<>();
    private final Map<Long,String> fingerprints=new HashMap<>();
    private final Map<String,List<Video>> groups=new HashMap<>();
    private final Context context;private PortableTitleReferences unresolved;
    /** The exporter must supply unfiltered physical records, not a visible Library snapshot. */
    public PortablePhysicalReferences(Context context,Collection<? extends Video> records){
        this.context=context;
        for(Video video:records){if(video.getId()<=0)throw new IllegalArgumentException("Indexed physical identity required");
            if(videos.putIfAbsent(video.getId(),video)!=null)throw new IllegalArgumentException("Duplicate indexed physical identity");
            String path=video.getFileUri()==null?video.getFilePath():video.getFileUri().toString();String fingerprint=fingerprint(path);
            if(fingerprint!=null){fingerprints.put(video.getId(),fingerprint);groups.computeIfAbsent(fingerprint,key->new ArrayList<>()).add(video);}
        }
    }
    private static String fingerprint(String path){
        if(path==null)return null;android.net.Uri uri=android.net.Uri.parse(path);
        // Removing account/query material cannot prove the remaining address names the same media.
        // Preserve those records with opaque origin IDs for explicit matching instead.
        if(uri.getEncodedQuery()!=null||uri.getEncodedFragment()!=null||
                uri.getAuthority()!=null&&uri.getAuthority().contains("@"))return null;
        return ViewingHistory.identity(false,null,-1,-1,path);
    }
    public Video video(long sourceId){return videos.get(sourceId);}
    public String reference(long sourceId)throws JSONException{
        if(sourceId<=0)throw new JSONException("Indexed physical identity required");String fingerprint=fingerprints.get(sourceId);
        if(fingerprint!=null&&groups.get(fingerprint).size()==1)return fingerprint;
        return pendingReference(sourceId);
    }
    public String pendingReference(long sourceId)throws JSONException{if(sourceId<=0)throw new JSONException("Indexed physical identity required");if(unresolved==null)unresolved=new PortableTitleReferences(context,null);return unresolved.reference("v"+sourceId);}
    public static void validate(String ref)throws JSONException{
        if(ref.matches("file:[a-f0-9]{64}"))return;
        PortableTitleReferences.validate(ref);if(!ref.startsWith("unresolved:")||!ref.matches(".*:v[1-9][0-9]{0,18}"))throw new JSONException("Physical identity required");
    }
    /** Even an identical provider title never resolves a different physical location or duplicate file rows. */
    public Video resolve(String ref)throws JSONException{validate(ref);List<Video> group=groups.get(ref);return group!=null&&group.size()==1?group.get(0):null;}
}
