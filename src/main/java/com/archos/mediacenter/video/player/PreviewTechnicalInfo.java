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
    private static String value(String text){return text==null||text.isEmpty()?"Unknown":text;}
    static void show(Activity activity,VideoMetadata metadata,android.net.Uri uri,int backend){
        View origin=activity.getCurrentFocus();
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("playback_technical_snapshot","metadata_present",metadata!=null,"backend",backend);
        android.app.Dialog dialog=PreviewDialog.read(activity,"File & Technical Details",describe(metadata,com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(uri),backend));
        dialog.setOnDismissListener(d->{if(origin!=null&&origin.isAttachedToWindow())origin.requestFocus();com.archos.mediacenter.video.diagnostics.Diagnostics.event("playback_technical_closed");});
    }
}
