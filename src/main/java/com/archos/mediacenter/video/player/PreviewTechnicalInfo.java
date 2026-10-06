package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.view.View;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.utils.VideoMetadata;

/** Read-only snapshot of the active decoder. Never starts a second metadata probe/player. */
final class PreviewTechnicalInfo {
    private static final java.util.Map<Activity,Modal> MODALS=new java.util.WeakHashMap<>();
    private static final class Saved {
        final View view;final int accessibility,descendants;
        Saved(View view){this.view=view;accessibility=view.getImportantForAccessibility();descendants=view instanceof android.view.ViewGroup?((android.view.ViewGroup)view).getDescendantFocusability():0;}
        void block(){view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);if(view instanceof android.view.ViewGroup)((android.view.ViewGroup)view).setDescendantFocusability(android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS);}
        void restore(){view.setImportantForAccessibility(accessibility);if(view instanceof android.view.ViewGroup)((android.view.ViewGroup)view).setDescendantFocusability(descendants);}
    }
    private static final class Modal {
        final Activity activity;final View origin;final Runnable restoreHud;final android.widget.FrameLayout root;final android.widget.ScrollView scroll;final java.util.List<Saved> siblings=new java.util.ArrayList<>();
        Modal(Activity activity,View origin,Runnable restoreHud,android.widget.FrameLayout root,android.widget.ScrollView scroll){this.activity=activity;this.origin=origin;this.restoreHud=restoreHud;this.root=root;this.scroll=scroll;}
        void close(boolean restore){
            MODALS.remove(activity);if(root.getParent() instanceof android.view.ViewGroup)((android.view.ViewGroup)root.getParent()).removeView(root);
            for(Saved sibling:siblings)sibling.restore();
            if(restore&&!activity.isFinishing()&&!activity.isDestroyed()){if(restoreHud!=null)restoreHud.run();if(origin!=null&&origin.isAttachedToWindow()&&origin.isShown()&&origin.isFocusable())origin.requestFocus();}
            com.archos.mediacenter.video.diagnostics.Diagnostics.event("playback_technical_closed");
        }
    }
    static boolean isShowing(Activity activity){return MODALS.containsKey(activity);}
    static boolean dismiss(Activity activity){Modal modal=MODALS.get(activity);if(modal==null)return false;modal.close(true);return true;}
    static void clear(Activity activity){Modal modal=MODALS.get(activity);if(modal!=null)modal.close(false);}
    static boolean handleKey(Activity activity,android.view.KeyEvent event){
        Modal modal=MODALS.get(activity);if(modal==null)return false;int key=event.getKeyCode();
        if(key==android.view.KeyEvent.KEYCODE_BACK||key==android.view.KeyEvent.KEYCODE_ESCAPE){if(event.getAction()==android.view.KeyEvent.ACTION_UP)modal.close(true);return true;}
        if(key>=android.view.KeyEvent.KEYCODE_DPAD_UP&&key<=android.view.KeyEvent.KEYCODE_DPAD_CENTER){if(event.getAction()==android.view.KeyEvent.ACTION_DOWN&&(key==19||key==20))modal.scroll.smoothScrollBy(0,dp(activity,key==19?-80:80));return true;}
        switch(key){
            case android.view.KeyEvent.KEYCODE_VOLUME_UP:case android.view.KeyEvent.KEYCODE_VOLUME_DOWN:case android.view.KeyEvent.KEYCODE_VOLUME_MUTE:
            case android.view.KeyEvent.KEYCODE_MEDIA_PLAY:case android.view.KeyEvent.KEYCODE_MEDIA_PAUSE:case android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:case android.view.KeyEvent.KEYCODE_MEDIA_STOP:
            case android.view.KeyEvent.KEYCODE_MEDIA_NEXT:case android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS:case android.view.KeyEvent.KEYCODE_MEDIA_REWIND:case android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:return false;
            default:return true;
        }
    }
    static String describe(VideoMetadata metadata,String source,int backend){
        StringBuilder text=new StringBuilder("Source: ").append(source).append("\nPlayer backend: ").append(backend);
        if(metadata==null)return text.append("\n\nTechnical metadata is not available for this playback session yet.").toString();
        text.append("\nFile size: ").append(metadata.getFileSize()).append(" bytes");
        VideoMetadata.VideoTrack video=metadata.getVideoTrack();
        if(video!=null)text.append("\n\nVIDEO\n").append(value(video.format)).append("\nDecoder: ").append(video.decoder)
            .append("\nResolution: ").append(metadata.getVideoWidth()).append(" × ").append(metadata.getVideoHeight())
            .append(video.bitRate>0?"\nBitrate: "+video.bitRate+" kb/s":"")
            .append(video.fpsScale>0?"\nFrame rate: "+String.format(java.util.Locale.UK,"%.3f",video.fpsRate/(double)video.fpsScale):"");
        if(metadata.getAudioTrackNb()>0)text.append("\n\nAUDIO");
        for(int i=0;i<metadata.getAudioTrackNb();i++){VideoMetadata.AudioTrack audio=metadata.getAudioTrack(i);if(audio!=null)text.append('\n').append(i+1).append(". ").append(value(audio.language)).append(" · ").append(value(audio.format)).append(" · ").append(value(audio.channels));}
        if(metadata.getSubtitleTrackNb()>0)text.append("\n\nSUBTITLES");
        for(int i=0;i<metadata.getSubtitleTrackNb();i++){VideoMetadata.SubtitleTrack subtitle=metadata.getSubtitleTrack(i);if(subtitle!=null)text.append('\n').append(i+1).append(". ").append(value(subtitle.language)).append(subtitle.isExternal?" · External":" · Embedded");}
        return text.toString();
    }
    private static int dp(Activity a,int n){return PreviewDialog.dp(a,n);}
    private static android.widget.TextView text(Activity a,String value,int size){android.widget.TextView t=new android.widget.TextView(a);t.setText(value);t.setTextSize(size);t.setTextColor(0xffd2e1ed);return t;}
    private static String value(String text){return text==null||text.isEmpty()?"Unknown":text;}
    static void show(Activity activity,VideoMetadata metadata,android.net.Uri uri,int backend){
        show(activity,metadata,uri,backend,null);
    }
    static void show(Activity activity,VideoMetadata metadata,android.net.Uri uri,int backend,Runnable restoreHud){
        dismiss(activity);
        View origin=activity.getCurrentFocus();
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("playback_technical_snapshot","metadata_present",metadata!=null,"backend",backend);
        if(uri!=null&&"content".equals(uri.getScheme())){String path=com.archos.mediacenter.video.utils.VideoUtils.getFileUriStringFromContentUri(activity,uri.toString());if(path!=null)uri=android.net.Uri.parse(path);}
        String full=describe(metadata,com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(uri),backend);

        android.widget.LinearLayout panel=new android.widget.LinearLayout(activity);panel.setOrientation(android.widget.LinearLayout.VERTICAL);panel.setPadding(dp(activity,20),dp(activity,16),dp(activity,20),dp(activity,16));panel.setBackground(PreviewDialog.surface(activity,false));
        android.widget.LinearLayout columns=new android.widget.LinearLayout(activity);
        String[] titles={"Source","Video","Audio","File"};String[] bodies={"Source: "+com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(uri),"Not available","Not available",metadata==null?"Not available":"File size: "+metadata.getFileSize()+" bytes"};
        String[] parts=full.split("\\n\\n(?=VIDEO|AUDIO|SUBTITLES)");bodies[0]=parts[0];
        bodies[0]="Source: "+com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(uri)+"\nPlayer backend: "+backend;
        for(int i=1;i<parts.length;i++){String value=parts[i];if(value.startsWith("SUBTITLES"))continue;int index=value.startsWith("VIDEO")?1:2;int line=value.indexOf('\n');bodies[index]=line<0?"Not available":value.substring(line+1);}
        for(int i:new int[]{1,2,3,0}){android.widget.LinearLayout column=new android.widget.LinearLayout(activity);column.setOrientation(android.widget.LinearLayout.VERTICAL);column.setPadding(dp(activity,10),0,dp(activity,10),0);column.addView(text(activity,titles[i],15));android.widget.TextView body=text(activity,bodies[i],12);body.setPadding(0,dp(activity,12),0,0);column.addView(body);columns.addView(column,new android.widget.LinearLayout.LayoutParams(0,-2,1));}
        android.widget.ScrollView scroll=new android.widget.ScrollView(activity);scroll.addView(columns);scroll.setFocusable(true);scroll.setFocusableInTouchMode(true);panel.addView(scroll,new android.widget.LinearLayout.LayoutParams(-1,0,1));
        android.widget.FrameLayout overlay=new android.widget.FrameLayout(activity);overlay.setTag("preview-technical-modal");overlay.setBackgroundColor(0x4d000000);overlay.setClickable(true);overlay.setFocusable(true);overlay.setFocusableInTouchMode(true);
        int width=Math.min(dp(activity,800),activity.getResources().getDisplayMetrics().widthPixels-dp(activity,64));int height=Math.min(dp(activity,300),activity.getResources().getDisplayMetrics().heightPixels-dp(activity,64));
        overlay.addView(panel,new android.widget.FrameLayout.LayoutParams(width,height,android.view.Gravity.CENTER));
        android.view.ViewGroup host=activity.findViewById(android.R.id.content);Modal modal=new Modal(activity,origin,restoreHud,overlay,scroll);
        for(int i=0;i<host.getChildCount();i++){Saved saved=new Saved(host.getChildAt(i));modal.siblings.add(saved);saved.block();}
        MODALS.put(activity,modal);host.addView(overlay,new android.view.ViewGroup.LayoutParams(-1,-1));
        scroll.post(()->{if(MODALS.get(activity)==modal){scroll.requestFocus();overlay.sendAccessibilityEvent(android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);}});
    }
}
