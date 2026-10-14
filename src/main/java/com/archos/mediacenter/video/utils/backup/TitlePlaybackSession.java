package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.player.Player;
import com.archos.mediacenter.video.utils.VideoMetadata;
import com.archos.mediaprovider.video.VideoStore;
import java.io.IOException;
import org.json.JSONException;

/** Per-open callback state. Real supported enumeration precedes selection, acknowledgements keep failures pending. */
public final class TitlePlaybackSession {
    private String token;private boolean attempted,accepted,failed,audioConfirmed,subtitleConfirmed;
    private int audio=-1,subtitle=-2;
    public void videoInfo(Context context,VideoDbInfo info)throws JSONException{
        String current=TitlePlaybackOverrides.token(context,info);if(java.util.Objects.equals(current,token))return;token=current;attempted=accepted=failed=false;audioConfirmed=subtitleConfirmed=false;audio=-1;subtitle=-2;
        if(current!=null)TitlePlaybackOverrides.clearNativeDefaults(context,info);
    }
    public boolean metadata(Context context,VideoDbInfo info,VideoMetadata metadata,Player player)throws JSONException,IOException{
        videoInfo(context,info);if(token==null||attempted||player==null)return false;PortableTitlePlayback.State choice=TitlePlaybackOverrides.validatedChoice(context,info,metadata);if(choice==null)return false;attempted=true;
        audio=choice.params==null?-1:VideoStore.paramsToAudioTrack(choice.params);int index=choice.params==null?-1:VideoStore.paramsToSubtitleTrack(choice.params);subtitle=choice.params==null||index<0?-2:Boolean.TRUE.equals(choice.subtitleNone)?-1:index;
        audioConfirmed=audio<0;subtitleConfirmed=subtitle== -2;info.subtitleDelay=choice.delay;info.subtitleRatio=choice.ratio;info.subtitleLanguage=choice.language;info.nbSubtitles=metadata.getSubtitleTrackNb();
        if(audio>=0)info.audioTrack=audio;if(subtitle!= -2)info.subtitleTrack=Boolean.TRUE.equals(choice.subtitleNone)?info.nbSubtitles:index;
        try{boolean queuedAudio=audio<0||player.setAudioTrack(audio),queuedSubtitle=subtitle== -2||player.setSubtitleTrack(subtitle);player.setSubtitleDelay(choice.delay);player.setSubtitleRatio(choice.ratio);accepted=queuedAudio&&queuedSubtitle&&!failed;if(!accepted){failed=true;TitlePlaybackOverrides.clearNativeDefaults(context,info);keepPending(context,info);return false;}acknowledge(context,info);return true;}
        catch(RuntimeException rejected){failed=true;TitlePlaybackOverrides.clearNativeDefaults(context,info);keepPending(context,info);return false;}
    }
    public void selection(Context context,VideoDbInfo info,boolean audioEvent,int track,boolean success)throws JSONException,IOException{
        if(token==null||!attempted)return;if(audioEvent?track!=audio:track!=subtitle)return;if(!java.util.Objects.equals(token,TitlePlaybackOverrides.token(context,info)))return;
        if(!success){failed=true;keepPending(context,info);return;}if(audioEvent)audioConfirmed=true;else subtitleConfirmed=true;acknowledge(context,info);
    }
    private void keepPending(Context context,VideoDbInfo info)throws JSONException,IOException{String saved=TitlePlaybackOverrides.pending(context,info,token,true);if(saved!=null)token=saved;}
    private void acknowledge(Context context,VideoDbInfo info)throws JSONException,IOException{if(accepted&&!failed&&audioConfirmed&&subtitleConfirmed){String saved=TitlePlaybackOverrides.pending(context,info,token,false);if(saved!=null)token=saved;}}
    public void manual(Context context,VideoDbInfo info)throws JSONException{token=TitlePlaybackOverrides.token(context,info);attempted=true;accepted=failed=false;audio=-1;subtitle=-2;}
}
