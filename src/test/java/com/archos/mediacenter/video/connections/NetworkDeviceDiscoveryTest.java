package com.archos.mediacenter.video.connections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class NetworkDeviceDiscoveryTest
{
    @Test public void savedChildShareOrAdvertisedAliasMarksTheDeviceAddedWithoutDnsOrCredentials()
    {
        var job = new NetworkDeviceDiscovery.Job(Runnable::run, ignored -> {});
        job.saved(List.of(Uri.parse("smb://MYNAS:445/Films"), Uri.parse("upnp://42/0/Movies")));
        job.observe(new NetworkDeviceDiscovery.Observed(Uri.parse("smb://192.168.1.2/"), "NAS",
            NetworkDeviceDiscovery.Kind.SMB_SERVER, List.of(Uri.parse("smb://mynas/"))));
        job.observe(new NetworkDeviceDiscovery.Observed(Uri.parse("upnp://42/0"), "Media server",
            NetworkDeviceDiscovery.Kind.MEDIA_SERVER, List.of()));
        job.observe(new NetworkDeviceDiscovery.Observed(Uri.parse("smb://other/"), "Other",
            NetworkDeviceDiscovery.Kind.SMB_SERVER, List.of()));
        assertEquals(3, job.current().devices().size());
        assertEquals(2, job.current().devices().stream()
            .filter(row -> row.membership() == NetworkDeviceDiscovery.Membership.ADDED).count());
        job.saved(List.of());
        assertTrue(job.current().devices().stream()
            .allMatch(row -> row.membership() == NetworkDeviceDiscovery.Membership.NEW));
        assertEquals(1, job.current().devices().stream()
            .filter(row -> row.kind() == NetworkDeviceDiscovery.Kind.MEDIA_SERVER).count());
        job.close();
    }

    @Test public void deadlineAndDisposalFenceLateProducerAndQueuedUiCallbacks()
    {
        List<Runnable> delivery = new ArrayList<>();
        List<NetworkDeviceDiscovery.Snapshot> received = new ArrayList<>();
        var job = new NetworkDeviceDiscovery.Job(delivery::add, received::add);
        job.saved(List.of());
        job.finish(false);
        job.observe(new NetworkDeviceDiscovery.Observed(Uri.parse("smb://late/"), "Late",
            NetworkDeviceDiscovery.Kind.SMB_SERVER, List.of()));
        assertEquals(NetworkDeviceDiscovery.State.FINISHED, job.current().state());
        assertTrue(job.current().devices().isEmpty());
        job.close();
        delivery.forEach(Runnable::run);
        assertTrue(received.isEmpty());
    }

    @Test public void failedSavedInventoryNeverClaimsAnUnknownConnectionIsNew()
    {
        var job = new NetworkDeviceDiscovery.Job(Runnable::run, ignored -> {});
        job.finish(false);
        job.saved(List.of());
        assertEquals(NetworkDeviceDiscovery.State.FAILED, job.current().state());
        assertFalse(job.accepting());
        job.close();
    }
}
