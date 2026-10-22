package com.archos.mediacenter.video.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

/** Worker-side decision gate. No destination is opened until explicit replacement approval. */
final class SubtitleReplacement {
    static boolean confirm(Activity activity,Handler main,BooleanSupplier cancelled){
        CountDownLatch choice=new CountDownLatch(1);AtomicBoolean replace=new AtomicBoolean();Dialog[] prompt={null};
        main.post(()->{if(cancelled.getAsBoolean()||activity.isFinishing()||activity.isDestroyed()){choice.countDown();return;}
            prompt[0]=PreviewDialog.review(activity,"Replace subtitle file?","A subtitle already exists for this title and language. Replace it with the selected download?","Replace",()->{replace.set(true);choice.countDown();});
            // PreviewDialog dismisses before invoking acceptance; defer the negative completion
            // until that UI callback has run, so dismissal cannot race a positive decision.
            prompt[0].setOnDismissListener(d->main.post(choice::countDown));
        });
        try{while(!choice.await(250,TimeUnit.MILLISECONDS))if(cancelled.getAsBoolean()||activity.isFinishing()||activity.isDestroyed()){main.post(()->{if(prompt[0]!=null)prompt[0].dismiss();});return false;}}
        catch(InterruptedException interrupted){Thread.currentThread().interrupt();main.post(()->{if(prompt[0]!=null)prompt[0].dismiss();});return false;}
        return replace.get()&&!cancelled.getAsBoolean();
    }
    private SubtitleReplacement(){}
}
