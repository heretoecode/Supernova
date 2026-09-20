package com.archos.mediacenter.video.player;

import android.app.Application;
import com.archos.mediacenter.video.utils.VideoMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewTechnicalInfoTest {
    @Test public void missingActiveMetadataDoesNotStartProbe(){assertTrue(PreviewTechnicalInfo.describe(null,"webdavs",0).contains("not available"));}
    @Test public void emptySnapshotHasNoNullTrackFailure(){String text=PreviewTechnicalInfo.describe(new VideoMetadata(),"webdavs",0);assertTrue(text.contains("Source: webdavs"));assertFalse(text.contains("null"));}
}
