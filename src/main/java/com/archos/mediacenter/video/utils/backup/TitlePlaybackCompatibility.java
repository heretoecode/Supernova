package com.archos.mediacenter.video.utils.backup;

import com.archos.mediacenter.video.utils.VideoMetadata;
import com.archos.mediacenter.utils.ISO639codes;
import com.archos.mediaprovider.video.VideoStore;
import java.util.Locale;

/** Receiving backend track enumeration gate; never guesses indices from titles or changes watch state. */
public final class TitlePlaybackCompatibility {
    public static boolean accepts(VideoMetadata metadata,PortableTitlePlayback.State choice) {
        if(metadata==null||choice==null)return false;
        if(choice.params==null)return choice.language==null;
        int audio=VideoStore.paramsToAudioTrack(choice.params),subtitle=VideoStore.paramsToSubtitleTrack(choice.params);
        if(audio<0||audio>=metadata.getAudioTrackNb())return false;
        VideoMetadata.AudioTrack track=metadata.getAudioTrack(audio);if(track==null||!track.supported)return false;
        int count=metadata.getSubtitleTrackNb();if(count<0||subtitle< -1)return false;
        if(Boolean.TRUE.equals(choice.subtitleNone))return choice.language==null&&count<=1022;
        if(subtitle>count)return false;
        // -1 is unset. A schema1 index with no language cannot distinguish a track from source None.
        if(subtitle== -1)return choice.language==null;
        if(choice.subtitleNone==null||subtitle==count)return false;
        VideoMetadata.SubtitleTrack selected=metadata.getSubtitleTrack(subtitle);if(selected==null)return false;
        if(choice.language==null)return true;
        if(choice.language.equals("srt")||choice.language.equals("vtt"))return selected.isExternal&&selected.path!=null&&selected.path.toLowerCase(Locale.ROOT).endsWith("."+choice.language);
        return selected.language!=null&&ISO639codes.isFavoriteLanguageMatch(choice.language,selected.language);
    }
    public static Integer receivingParams(VideoMetadata metadata,PortableTitlePlayback.State choice){if(!accepts(metadata,choice))return null;return Boolean.TRUE.equals(choice.subtitleNone)?VideoStore.paramsFromTracks(VideoStore.paramsToAudioTrack(choice.params),metadata.getSubtitleTrackNb()):choice.params;}
    private TitlePlaybackCompatibility(){}
}
