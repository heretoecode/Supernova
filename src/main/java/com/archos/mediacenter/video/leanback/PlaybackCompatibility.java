package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.view.Display;
import android.view.WindowManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.VideoMetadata;
import java.util.*;

/** Snapshot of platform-reported capabilities; unknown native capabilities retain playback fallback. */
final class PlaybackCompatibility {
    private final Set<Integer> hdr=new HashSet<>();
    private final Map<String,List<MediaCodecInfo.CodecCapabilities>> decoders=new HashMap<>();
    private boolean displayKnown;
    static PlaybackCompatibility read(Context context) {
        PlaybackCompatibility result=new PlaybackCompatibility();
        if(android.os.Build.VERSION.SDK_INT>=24)try {
            Display display=((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if(display!=null){for(int type:display.getHdrCapabilities().getSupportedHdrTypes())result.hdr.add(type);result.displayKnown=true;}
        }catch(RuntimeException unavailable){/* Preserve the existing player fallback. */}
        try {
            for(MediaCodecInfo codec:new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos()) {
                if(codec.isEncoder())continue;
                for(String type:codec.getSupportedTypes())try {
                    result.decoders.computeIfAbsent(type.toLowerCase(Locale.ROOT),key->new ArrayList<>()).add(codec.getCapabilitiesForType(type));
                }catch(RuntimeException unsupported){/* One broken codec must not hide all other capabilities. */}
            }
        }catch(RuntimeException unavailable){/* Native/software codecs are still available to the player. */}
        return result;
    }
    boolean compatible(Video video) {
        VideoMetadata.VideoTrack track=video.getMetadata()==null?null:video.getMetadata().getVideoTrack();
        String range=track==null?video.getPreviewDynamicRange():track.dynamicRange();
        int profile=track==null?video.getPreviewDolbyVisionProfile():track.dolbyVisionProfile;
        int compatibility=track==null?video.getPreviewDolbyVisionCompatibility():track.dolbyVisionCompatibility;
        if(range.equals("Dolby Vision")) {
            if(dolbySupported(video,profile))return true;
            // The native parser exposes the actual base-layer compatibility; never infer it from the filename.
            if(profile==8&&compatibility==1&&hdrSupported(Display.HdrCapabilities.HDR_TYPE_HDR10))return codecSize(video,"video/hevc");
            if(profile==8&&compatibility==4&&hdrSupported(Display.HdrCapabilities.HDR_TYPE_HLG))return codecSize(video,"video/hevc");
            return !displayKnown;
        }
        if(range.equals("HDR10+")||range.equals("HDR (PQ)")) {
            if(!hdrSupported(Display.HdrCapabilities.HDR_TYPE_HDR10)&&!hdrSupported(Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS))return false;
        }else if(range.equals("HLG")&&!hdrSupported(Display.HdrCapabilities.HDR_TYPE_HLG))return false;
        String format=track==null?video.getCalculatedVideoFormat():track.format;
        String value=format==null?"":format.toLowerCase(Locale.ROOT);
        String mime=value.contains("hevc")||value.contains("h265")||value.contains("h.265")?"video/hevc":value.contains("h264")||value.contains("h.264")||value.contains("avc")?"video/avc":value.contains("av1")?"video/av01":value.contains("vp9")?"video/x-vnd.on2.vp9":"";
        return mime.isEmpty()||codecSize(video,mime);
    }
    int hdrBonus(Video video,String range) {
        if(range.equals("Dolby Vision")) {
            int profile=video.getMetadata()!=null&&video.getMetadata().getVideoTrack()!=null?video.getMetadata().getVideoTrack().dolbyVisionProfile:video.getPreviewDolbyVisionProfile();
            return dolbySupported(video,profile)?2:1;
        }
        if(range.equals("HDR10+"))return hdrSupported(Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS)?2:1;
        return range.equals("HDR (PQ)")||range.equals("HLG")?1:0;
    }
    private boolean hdrSupported(int type){return !displayKnown||hdr.contains(type);}
    private boolean dolbySupported(Video video,int profile) {
        if(!hdrSupported(Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION))return false;
        for(MediaCodecInfo.CodecCapabilities codec:decoders.getOrDefault("video/dolby-vision",Collections.emptyList())) {
            if(!sizeSupported(codec,video))continue;
            if(profile<=0)return true;
            for(MediaCodecInfo.CodecProfileLevel supported:codec.profileLevels)if(supported.profile==1<<profile)return true;
        }
        return !displayKnown&&decoders.isEmpty();
    }
    private boolean codecSize(Video video,String mime) {
        List<MediaCodecInfo.CodecCapabilities> codecs=decoders.get(mime);
        if(codecs==null||codecs.isEmpty())return true; // Unknown platform path: keep AVOS native/software fallback.
        for(MediaCodecInfo.CodecCapabilities codec:codecs)if(sizeSupported(codec,video))return true;
        return false;
    }
    private static boolean sizeSupported(MediaCodecInfo.CodecCapabilities codec,Video video) {
        if(video.getMeasuredWidth()<=0||video.getMeasuredHeight()<=0)return true;
        try {MediaCodecInfo.VideoCapabilities caps=codec.getVideoCapabilities();return caps==null||caps.isSizeSupported(video.getMeasuredWidth(),video.getMeasuredHeight());}
        catch(RuntimeException unavailable){return true;}
    }
}
