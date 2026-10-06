package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import java.util.*;
import org.json.JSONArray;

/** Follows stable folder IDs. Rebase only a previously verified WebDAV/API path suffix. */
final class PutioRootLocation {
    interface Files {PutioReadClient.Item get(long id)throws PutioReadClient.Unavailable;}
    static List<String> path(long folder,Files files)throws PutioReadClient.Unavailable {
        List<String> result=new ArrayList<>();Set<Long> seen=new HashSet<>();
        while(folder!=0){
            if(Thread.currentThread().isInterrupted())throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.CANCELLED,0);
            if(result.size()>=128||!seen.add(folder))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
            PutioReadClient.Item item=files.get(folder);
            if(item.id!=folder||!item.folder())throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
            result.add(item.name);folder=item.parentId;
        }
        Collections.reverse(result);return result;
    }
    static Uri rebase(Uri source,List<String> oldPath,List<String> newPath){
        List<String> current=source.getPathSegments();int prefix=current.size()-oldPath.size();
        if(prefix<0||!current.subList(prefix,current.size()).equals(oldPath))throw new IllegalStateException("Folder location needs review");
        Uri.Builder result=source.buildUpon().path("");
        for(String segment:current.subList(0,prefix))result.appendPath(segment);
        for(String segment:newPath)result.appendPath(segment);
        return result.build();
    }
    static List<String> readPath(Context context,PutioReadClient client,long folder)throws PutioReadClient.Unavailable {
        return path(folder,id->client.file(id).item);
    }
    static Uri target(Context context,long account,long folder,Uri source,List<String> current)throws Exception {
        SharedPreferences prefs=context.getSharedPreferences("putio-library-selection-v1",0);String key="root:"+account+":"+folder;
        if(!source.toString().equals(prefs.getString(key+":source",null)))return source;
        JSONArray stored=new JSONArray(prefs.getString(key+":path","[]"));List<String> previous=new ArrayList<>();
        for(int i=0;i<stored.length();i++)previous.add(stored.getString(i));
        return previous.equals(current)?source:rebase(source,previous,current);
    }
    static void remember(Context context,long account,long folder,Uri source,List<String> current){
        // Store even custom mounts: if they move, target() requires review instead of guessing.
        String key="root:"+account+":"+folder;
        if(!context.getSharedPreferences("putio-library-selection-v1",0).edit().putString(key+":source",source.toString()).putString(key+":path",new JSONArray(current).toString()).commit())throw new IllegalStateException("Folder location could not be retained");
    }
    private PutioRootLocation(){}
}
