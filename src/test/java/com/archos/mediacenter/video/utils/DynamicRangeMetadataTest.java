package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.os.Parcel;
import com.archos.medialib.IMediaMetadataRetriever;
import com.archos.medialib.IMediaPlayer;
import com.archos.medialib.MediaMetadata;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class DynamicRangeMetadataTest {
    private MediaMetadata parcelMetadata(Map<Integer,Integer> values) {
        Parcel parcel=Parcel.obtain();
        parcel.writeInt(8+16*values.size());
        parcel.writeInt(0x4d455441);
        for(Map.Entry<Integer,Integer> value:values.entrySet()) {
            parcel.writeInt(16);parcel.writeInt(value.getKey());
            parcel.writeInt(MediaMetadata.INTEGER_VAL);parcel.writeInt(value.getValue());
        }
        parcel.setDataPosition(0);
        MediaMetadata data=new MediaMetadata();assertTrue(data.parse(parcel));return data;
    }
    private IMediaMetadataRetriever retriever(Map<Integer,String> values) {
        return (IMediaMetadataRetriever)Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class<?>[]{IMediaMetadataRetriever.class},(proxy,method,args)->
                method.getName().equals("extractMetadata")?values.get((Integer)args[0]):null);
    }
    @Test public void nativePlayerParcelExposesDolbyVisionSourceProfile() {
        int base=IMediaPlayer.METADATA_KEY_VIDEO_TRACK;
        VideoMetadata metadata=new VideoMetadata();
        metadata.setData(parcelMetadata(Map.of(IMediaPlayer.METADATA_KEY_NB_VIDEO_TRACK,1,
            base+IMediaPlayer.METADATA_KEY_VIDEO_TRACK_COLOR_TRC,16,
            base+IMediaPlayer.METADATA_KEY_VIDEO_TRACK_DOVI_PROFILE,8,
            base+IMediaPlayer.METADATA_KEY_VIDEO_TRACK_DOVI_COMPAT,1)));
        assertEquals(8,metadata.getVideoTrack().dolbyVisionProfile);assertEquals(1,metadata.getVideoTrack().dolbyVisionCompatibility);
        assertEquals("Dolby Vision",metadata.getVideoTrack().dynamicRange());
    }
    @Test public void backgroundRetrieverUsesItsOwnKeysAndRetainsHdr10Plus() {
        int base=IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK;
        VideoMetadata.VideoTrack track=new VideoMetadata.VideoTrack(retriever(Map.of(
            base+IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK_COLOR_TRC,"16",
            base+IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK_DOVI_PROFILE,"0",
            base+IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK_HDR10_PLUS,"1")));
        assertEquals(16,track.colorTrc);assertTrue(track.hdr10Plus);
        assertEquals("HDR10+",track.dynamicRange());
        assertEquals(13,IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK_COLOR_TRC);
    }
    @Test public void absentHdrFieldsDoNotInventHdrFromNames() {
        VideoMetadata.VideoTrack absent=new VideoMetadata.VideoTrack(retriever(Map.of()));
        assertEquals("",absent.dynamicRange());
        int base=IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK;
        assertEquals("HLG",new VideoMetadata.VideoTrack(retriever(Map.of(
            base+IMediaMetadataRetriever.METADATA_KEY_VIDEO_TRACK_COLOR_TRC,"18"))).dynamicRange());
    }
}
