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
        int count=metadata.getSubtitleTrackNb();if(subtitle< -1||subtitle>count)return false;
        // -1 is unset; count is the established receiving UI's explicit None sentinel.
        if(subtitle== -1||subtitle==count)return choice.language==null;
        VideoMetadata.SubtitleTrack selected=metadata.getSubtitleTrack(subtitle);if(selected==null)return false;
        if(choice.language==null)return true;
        if(choice.language.equals("srt")||choice.language.equals("vtt"))return selected.isExternal&&selected.path!=null&&selected.path.toLowerCase(Locale.ROOT).endsWith("."+choice.language);
        return selected.language!=null&&ISO639codes.isFavoriteLanguageMatch(choice.language,selected.language);
    }
    private TitlePlaybackCompatibility(){}
}
