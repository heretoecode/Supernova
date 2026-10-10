package com.archos.mediacenter.video.diagnostics;

import android.net.Uri;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.util.Map;
import java.util.WeakHashMap;

/** UI-thread Picasso adapter. Records no URL, file path, title or person name. */
public final class ArtworkRequest {
    private static final Map<ImageView,ArtworkTrace> ACTIVE=new WeakHashMap<>();
    public static void load(ImageView view,Uri uri,long media,String surface,String type,RequestCreator request){
        load(view,uri,media,surface,type,request,false);
    }
    public static void load(ImageView view,Uri uri,long media,String surface,String type,RequestCreator request,boolean placeholderOnFailure){
        cancel(view);
        ArtworkTrace trace=new ArtworkTrace(media,surface,type,source(uri));ACTIVE.put(view,trace);
        request.into(view,new Callback(){
            @Override public void onSuccess(){trace.ready("picasso_unspecified");}
            @Override public void onError(Exception error){trace.failed(error==null?"unknown":error.getClass().getSimpleName(),placeholderOnFailure);}
        });
    }
    public static void cancel(ImageView view){
        ArtworkTrace old=ACTIVE.remove(view);if(old!=null){old.cancelled();Picasso.get().cancelRequest(view);}
    }
    static String source(Uri uri){
        String scheme=uri==null?null:uri.getScheme();
        if("https".equals(scheme)||"http".equals(scheme))return "network";
        if("file".equals(scheme)||"content".equals(scheme))return "local";
        return "unknown";
    }
    private ArtworkRequest(){}
}
