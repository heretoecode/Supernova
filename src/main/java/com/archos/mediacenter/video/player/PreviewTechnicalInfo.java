package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.view.View;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.utils.VideoMetadata;

/** Read-only snapshot of the active decoder. Never starts a second metadata probe/player. */
final class PreviewTechnicalInfo {
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
        View origin=activity.getCurrentFocus();
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("playback_technical_snapshot","metadata_present",metadata!=null,"backend",backend);
        String full=describe(metadata,com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(uri),backend);
        android.app.Dialog dialog=new android.app.Dialog(activity);dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        android.widget.LinearLayout panel=new android.widget.LinearLayout(activity);panel.setOrientation(1);panel.setPadding(dp(activity,20),dp(activity,16),dp(activity,20),dp(activity,16));panel.setBackground(PreviewDialog.surface(activity,false));
        panel.addView(text(activity,"File & Technical Details",20));
        android.net.Uri physical=uri==null?null:com.archos.mediacenter.video.utils.VideoUtils.getFileUriFromMediaLibPath(uri.toString());
        String filename=physical==null?null:physical.getLastPathSegment();
        android.widget.TextView file=text(activity,filename==null?"Selected playback file":filename,13);file.setMaxLines(2);file.setPadding(0,dp(activity,10),0,dp(activity,14));panel.addView(file);
        android.widget.LinearLayout columns=new android.widget.LinearLayout(activity);
        String[] titles={"Source","Video","Audio","Subtitles"};String[] bodies={full,"Not available","Not available","Not available"};
        String[] parts=full.split("\\n\\n(?=VIDEO|AUDIO|SUBTITLES)");bodies[0]=parts[0];
        for(int i=1;i<parts.length;i++){String value=parts[i];int index=value.startsWith("VIDEO")?1:value.startsWith("AUDIO")?2:3;int line=value.indexOf('\n');bodies[index]=line<0?"Not available":value.substring(line+1);}
        for(int i=0;i<4;i++){android.widget.LinearLayout column=new android.widget.LinearLayout(activity);column.setOrientation(1);column.setPadding(dp(activity,10),0,dp(activity,10),0);column.addView(text(activity,titles[i],15));android.widget.TextView body=text(activity,bodies[i],12);body.setPadding(0,dp(activity,12),0,0);column.addView(body);columns.addView(column,new android.widget.LinearLayout.LayoutParams(0,-2,1));}
        android.widget.ScrollView scroll=new android.widget.ScrollView(activity);scroll.addView(columns);scroll.setFocusable(true);panel.addView(scroll,new android.widget.LinearLayout.LayoutParams(-1,0,1));
        android.widget.TextView close=text(activity,"Close",14);close.setGravity(android.view.Gravity.CENTER);close.setFocusable(true);close.setBackground(PreviewDialog.focus(activity));close.setOnClickListener(v->dialog.dismiss());panel.addView(close,new android.widget.LinearLayout.LayoutParams(dp(activity,90),dp(activity,38)));
        dialog.setContentView(panel);dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);dialog.getWindow().setDimAmount(.3f);dialog.getWindow().setLayout(Math.min(dp(activity,800),activity.getResources().getDisplayMetrics().widthPixels-dp(activity,64)),Math.min(dp(activity,300),activity.getResources().getDisplayMetrics().heightPixels-dp(activity,64)));close.requestFocus();
        dialog.setOnDismissListener(d->{if(origin!=null&&origin.isAttachedToWindow())origin.requestFocus();com.archos.mediacenter.video.diagnostics.Diagnostics.event("playback_technical_closed");});
    }
}
