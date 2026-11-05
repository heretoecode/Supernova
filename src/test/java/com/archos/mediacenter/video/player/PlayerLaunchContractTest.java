package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PlayerLaunchContractTest {
    @Test
    public void stereoIsScopedToOneLaunchAndSurvivesIntentRecreation() {
        Context context = RuntimeEnvironment.getApplication();
        Intent forced = new Intent().putExtra(PlayerLaunchContract.STEREO_MODE, true);
        assertTrue(PlayerLaunchContract.stereoForced(context, forced));
        Bundle recreatedExtras = new Bundle(forced.getExtras());
        assertTrue(PlayerLaunchContract.stereoForced(context, new Intent().putExtras(recreatedExtras)));
        assertFalse(PlayerLaunchContract.stereoForced(context, new Intent()));
        assertFalse(PlayerLaunchContract.stereoForced(context,
                new Intent().putExtra(PlayerLaunchContract.STEREO_MODE, false)));
        assertFalse(PlayerLaunchContract.stereoForced(context, null));
    }
}
