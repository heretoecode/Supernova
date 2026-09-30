package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.HashSet;
import java.util.Set;

/** Optional published metadata; unavailable duration is never estimated from the feature runtime. */
final class PreviewExtraDuration {
    private static final Set<String> pending=new HashSet<>();
    private static SharedPreferences prefs(Context c){return c.getSharedPreferences("preview-extra-duration",Context.MODE_PRIVATE);}
    static long cached(Context c,String key){return prefs(c).getLong(key+":seconds",0);}
    static String label(long seconds){return seconds<=0?"":seconds>=3600?String.format(java.util.Locale.ROOT,"%d:%02d:%02d",seconds/3600,seconds/60%60,seconds%60):String.format(java.util.Locale.ROOT,"%d:%02d",seconds/60,seconds%60);}
    static void request(PreviewLandscapeCard card,String key){
        Context app=card.getContext().getApplicationContext();SharedPreferences prefs=prefs(app);
        String current=label(cached(app,key));card.metadata.setText(current);card.metadata.setVisibility(current.isEmpty()?android.view.View.GONE:android.view.View.VISIBLE);
        long age=System.currentTimeMillis()-prefs.getLong(key+":at",0);
        if(age>=0&&age<24*60*60*1000L)return;
        synchronized(pending){if(pending.size()>=8||!pending.add(key))return;}
        StreamingRepository.IO.submit(()->{
          try{
            long seconds=0;
            try{seconds=StreamingRepository.extraDuration(key);}catch(Exception unavailable){/* Missing public metadata remains unknown. */}
            if(seconds<=0)seconds=cached(app,key);
            prefs.edit().putLong(key+":seconds",seconds).putLong(key+":at",System.currentTimeMillis()).apply();
            String text=label(seconds);card.post(()->{if(card.isAttachedToWindow()&&("extra:"+key).equals(card.getTag())){card.metadata.setText(text);card.metadata.setVisibility(text.isEmpty()?android.view.View.GONE:android.view.View.VISIBLE);}});
          }finally{synchronized(pending){pending.remove(key);}}
        });
    }
    private PreviewExtraDuration(){}
}
