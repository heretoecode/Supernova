package com.archos.mediacenter.video.streaming.putio;

import android.app.*;
import android.graphics.Bitmap;
import android.os.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.concurrent.Future;

/** Short-lived TV linking flow. The QR encodes only the validated link and temporary code. */
final class PutioAuthFlow {
    private final Activity activity;private final Runnable connected;private final Handler main=new Handler(Looper.getMainLooper());
    private final Object lock=new Object();private boolean stopped;private Future<?> task;private Dialog dialog;private long started;
    private PutioAuthFlow(Activity activity,Runnable connected){this.activity=activity;this.connected=connected;}
    static void open(Activity activity,Runnable connected){new PutioAuthFlow(activity,connected).start();}
    private void start(){
        String clientId=activity.getString(R.string.putio_registered_client_id),template=activity.getString(R.string.putio_validated_link_template);
        if(!PutioOAuthClient.configured(clientId,template)){PreviewDialog.read(activity,"Connect put.io","Account linking is awaiting the registered Supernova OAuth configuration and validated linking URL. Existing WebDAV library access and playback remain available.");return;}
        PutioOAuthClient client=new PutioOAuthClient(clientId,template);started=SystemClock.elapsedRealtime();show(PreviewDialog.read(activity,"Connect put.io","Requesting a temporary linking code…"));
        task=StreamingRepository.IO.submit(()->{try{PutioOAuthClient.Code code=client.begin();Bitmap qr=qr(code.link);main.post(()->{if(!active()){qr.recycle();return;}show(PreviewDialog.read(activity,"Connect put.io","Scan with your phone and approve Supernova.\n\nCode: "+code.code+"\n"+code.link+"\n\nOr open the link and enter the code manually.",qr));schedule(client,code);});}catch(Exception failure){failed();}});
    }
    static Bitmap qr(String link)throws Exception{BitMatrix matrix=new QRCodeWriter().encode(link,BarcodeFormat.QR_CODE,512,512);Bitmap image=Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888);int[] pixels=new int[512*512];for(int y=0;y<512;y++)for(int x=0;x<512;x++)pixels[y*512+x]=matrix.get(x,y)?0xff000000:0xffffffff;image.setPixels(pixels,0,512,0,0,512,512);return image;}
    private void schedule(PutioOAuthClient client,PutioOAuthClient.Code code){main.postDelayed(()->{
        if(!active())return;if(SystemClock.elapsedRealtime()-started>=10*60*1000){failed();return;}
        task=StreamingRepository.IO.submit(()->{try{String token=client.poll(code);if(token==null){main.post(()->{if(active())schedule(client,code);});return;}
            synchronized(lock){if(stopped||activity.isFinishing()||activity.isDestroyed())return;new PutioTokenStore(activity).save(token);stopped=true;}
            main.post(()->{main.removeCallbacksAndMessages(null);if(dialog!=null){dialog.setOnDismissListener(null);dialog.dismiss();}if(!activity.isFinishing()&&!activity.isDestroyed())connected.run();});
        }catch(Exception failure){failed();}});
    },5000);}
    private boolean active(){synchronized(lock){return !stopped&&!activity.isFinishing()&&!activity.isDestroyed();}}
    private void show(Dialog next){if(dialog!=null){dialog.setOnDismissListener(null);dialog.dismiss();}dialog=next;dialog.setOnDismissListener(d->stop());}
    private void stop(){synchronized(lock){stopped=true;}main.removeCallbacksAndMessages(null);if(task!=null)task.cancel(true);}
    private void failed(){main.post(()->{if(!active())return;stop();if(dialog!=null){dialog.setOnDismissListener(null);dialog.dismiss();}PreviewDialog.read(activity,"Linking unavailable","The code may have expired or the connection may be unavailable. Start Connect put.io again. Your existing library has not changed.");});}
}
