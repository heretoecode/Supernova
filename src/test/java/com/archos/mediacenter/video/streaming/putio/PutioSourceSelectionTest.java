package com.archos.mediacenter.video.streaming.putio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.net.Uri;
import com.archos.mediacenter.video.scanning.LibraryScanController;
import com.archos.mediacenter.video.scanning.ScanProgress;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PutioSourceSelectionTest
{
    @Test public void selectionUsesExactSavedApiOwnershipAndDoesNotIncludeOtherFolders()
    {
        var chosen = new PutioAssociationStore.Scope(7, "webdavs://example.test/Movies", "API");
        var other = new PutioAssociationStore.Scope(8, "webdavs://example.test/Shows", "API");
        var dav = new PutioAssociationStore.Scope(9, "webdavs://example.test/Legacy", "GENERIC");
        List<PutioAssociationStore.Scope> saved = List.of(chosen, other, dav);
        assertEquals(List.of(chosen), PutioSyncScheduler.selectScopes(saved, chosen.source));
        assertTrue(PutioSyncScheduler.selectScopes(saved, Uri.parse("webdavs://example.test/")).isEmpty());
        assertTrue(PutioSyncScheduler.selectScopes(saved, dav.source).isEmpty());
        assertEquals(3, saved.size());
    }

    @Test public void accountFailureTerminatesTheChosenSourceWithoutClaimingCompletion() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        LibraryScanController.install(app);
        PutioReadClient client = mock(PutioReadClient.class);
        when(client.account()).thenThrow(new IllegalStateException("private account detail"));
        PutioSyncScheduler.sync(app, "manual", client, Uri.parse("webdavs://example.test/Movies"));
        ScanProgress progress = LibraryScanController.progress();
        assertEquals(ScanProgress.Failure.UNAVAILABLE, progress.failure);
        assertTrue(!progress.running);
    }
}
