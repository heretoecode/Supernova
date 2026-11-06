package com.archos.mediacenter.video.player;

import android.app.Application;
import android.net.Uri;
import com.archos.environment.ArchosUtils;
import com.archos.filecorelibrary.RawListerFactory;
import com.archos.filecorelibrary.webdav.WebdavUtils;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Executed only as the private subprocess fixture; real FileCore factory, client and blocking PROPFIND. */
public final class NextListingChildMain
{
    public static void main(String[] arguments)
    {
        System.setProperty("next.listing.fixture.port", arguments[0]);
        System.exit(JUnitCore.runClasses(OwnedWorker.class).wasSuccessful() ? 0 : 1);
    }

    @RunWith(RobolectricTestRunner.class)
    @Config(application = Application.class, sdk = 30)
    public static class OwnedWorker
    {
        @Test public void realBlockingListing() throws Exception
        {
            Application app = RuntimeEnvironment.getApplication();
            ArchosUtils.setGlobalContext(app);
            WebdavUtils.getInstance(app);
            Uri uri = Uri.parse("webdav://127.0.0.1:" + System.getProperty("next.listing.fixture.port") + "/folder/");
            RawListerFactory.getRawListerForUrl(uri).getFileList();
        }
    }

    private NextListingChildMain()
    {
    }
}
