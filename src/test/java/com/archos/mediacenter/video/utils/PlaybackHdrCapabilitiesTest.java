package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.view.Display;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.MediaCodecInfoBuilder.CodecCapabilitiesBuilder;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** 9ec1bdc2: real JNI callback contracts, live display queries and candidate filtering. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, manifest = Config.NONE, sdk = 33)
public class PlaybackHdrCapabilitiesTest {
    private int previousMode;

    @Before
    public void rememberMode() {
        previousMode = CodecDiscovery.getDoViMode();
        CodecDiscovery.setDoViMode(CodecDiscovery.DOVI_MODE_AUTO);
    }

    @After
    public void clearDisplay() {
        CodecDiscovery.setPlaybackDisplay(null);
        CodecDiscovery.setDoViMode(previousMode);
    }

    @Test
    public void absentInvalidAndUnknownDisplayReturnUnknownRatherThanSdr() {
        CodecDiscovery.setPlaybackDisplay(null);
        assertEquals(-1, hdr());
        Display display = mock(Display.class);
        CodecDiscovery.setPlaybackDisplay(display);
        assertEquals(-1, hdr());
        when(display.isValid()).thenReturn(true);
        assertEquals(-1, hdr()); // no capabilities object
    }

    @Test
    public void capabilitiesAreReadAgainForEachNativeQuery() {
        Display display = mock(Display.class);
        when(display.isValid()).thenReturn(true);
        when(display.getHdrCapabilities()).thenReturn(hdrCapabilities(), hdrCapabilities(
                Display.HdrCapabilities.HDR_TYPE_HDR10, Display.HdrCapabilities.HDR_TYPE_HLG,
                Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS, Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION));
        CodecDiscovery.setPlaybackDisplay(display);
        assertEquals(0, hdr());
        assertEquals(15, hdr());
    }

    @Test
    public void vendorCapabilityExceptionIsUnknown() {
        Display display = mock(Display.class);
        when(display.isValid()).thenReturn(true);
        when(display.getHdrCapabilities()).thenThrow(new IllegalStateException("display detached"));
        CodecDiscovery.setPlaybackDisplay(display);
        assertEquals(-1, hdr());
    }

    @Test
    @Config(sdk = 23)
    public void api23NeverQueriesHdrDisplayApis() {
        Display display = mock(Display.class);
        CodecDiscovery.setPlaybackDisplay(display);
        assertEquals(-1, hdr());
        verifyNoInteractions(display);
    }

    @Test
    @Config(sdk = 26)
    public void oldApiRetainsTheNativeFallbackWithoutEnumerating() {
        try (MockedConstruction<MediaCodecList> lists = mockConstruction(MediaCodecList.class)) {
            assertNull(candidate(0));
            assertTrue(lists.constructed().isEmpty());
        }
    }

    @Test
    public void nativeCandidateBoundsDoNotEnumerate() {
        try (MockedConstruction<MediaCodecList> lists = mockConstruction(MediaCodecList.class)) {
            assertNull(candidate(-1));
            assertNull(candidate(16));
            assertTrue(lists.constructed().isEmpty());
        }
    }

    @Test
    public void excludesEncoderSoftwareAliasSecureAndTunneledAndKeepsVendorOrder() {
        MediaCodecInfo encoder = codec("encoder");
        when(encoder.isEncoder()).thenReturn(true);
        MediaCodecInfo software = codec("software");
        when(software.isSoftwareOnly()).thenReturn(true);
        MediaCodecInfo alias = codec("alias");
        when(alias.isAlias()).thenReturn(true);
        MediaCodecInfo secure = codec("secure", MediaCodecInfo.CodecCapabilities.FEATURE_SecurePlayback);
        MediaCodecInfo tunneled = codec("tunneled", MediaCodecInfo.CodecCapabilities.FEATURE_TunneledPlayback);
        MediaCodecInfo[] infos = {encoder, software, alias, secure, tunneled, codec("vendor-b"), codec("vendor-a")};
        try (MockedConstruction<MediaCodecList> lists = mockConstruction(MediaCodecList.class,
                (list, context) -> when(list.getCodecInfos()).thenReturn(infos))) {
            assertEquals("vendor-b", candidate(0));
            assertEquals("vendor-a", candidate(1));
            assertNull(candidate(2));
        }
    }

    private Display.HdrCapabilities hdrCapabilities(int... types) {
        return ReflectionHelpers.callConstructor(Display.HdrCapabilities.class,
                ClassParameter.from(int[].class, types), ClassParameter.from(float.class, 1000f),
                ClassParameter.from(float.class, 500f), ClassParameter.from(float.class, 0.1f));
    }

    private MediaCodecInfo codec(String name, String... requiredFeatures) {
        MediaCodecInfo info = mock(MediaCodecInfo.class);
        MediaCodecInfo.CodecProfileLevel profile = new MediaCodecInfo.CodecProfileLevel();
        profile.profile = MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline;
        profile.level = MediaCodecInfo.CodecProfileLevel.AVCLevel51;
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", 1920, 1080);
        for (String feature : requiredFeatures) format.setFeatureEnabled(feature, true);
        MediaCodecInfo.CodecCapabilities caps = CodecCapabilitiesBuilder.newBuilder()
                .setMediaFormat(format)
                .setProfileLevels(new MediaCodecInfo.CodecProfileLevel[] {profile})
                .setColorFormats(new int[] {MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible})
                .setRequiredFeatures(requiredFeatures)
                .build();
        when(info.getName()).thenReturn(name);
        when(info.getSupportedTypes()).thenReturn(new String[] {"video/avc"});
        when(info.getCapabilitiesForType("video/avc")).thenReturn(caps);
        return info;
    }

    private int hdr() {
        return ReflectionHelpers.callStaticMethod(CodecDiscovery.class, "getPlaybackHdrCapabilities");
    }

    private String candidate(int index) {
        return ReflectionHelpers.callStaticMethod(CodecDiscovery.class, "getVideoCodecForFormat",
                ClassParameter.from(String.class, "video/avc"), ClassParameter.from(int.class, 1),
                ClassParameter.from(int.class, 1920), ClassParameter.from(int.class, 1080),
                ClassParameter.from(double.class, 23.976), ClassParameter.from(int.class, index));
    }
}
