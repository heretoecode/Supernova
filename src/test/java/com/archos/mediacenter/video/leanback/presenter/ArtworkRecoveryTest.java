package com.archos.mediacenter.video.leanback.presenter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** D8 (0.158.8): only a missing kept file is fetched again, and each one once per run. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class ArtworkRecoveryTest
{
    @Test public void onlyKeptFilesAreFetchedAgainAndEachOnlyOnce()
    {
        assertTrue(ArtworkRecovery.isLocalFile(Uri.parse("/data/user/0/app/files/posters/show-42.jpg")));
        assertTrue(ArtworkRecovery.isLocalFile(Uri.parse("file:///data/posters/show-43.jpg")));
        assertFalse(ArtworkRecovery.isLocalFile(Uri.parse("https://image.tmdb.org/t/p/w342/x.jpg")));
        Uri missing = Uri.parse("/data/posters/show-44.jpg");
        assertTrue(ArtworkRecovery.claim(missing));
        assertFalse("a second failure of the same file is not fetched again", ArtworkRecovery.claim(missing));
    }
}
