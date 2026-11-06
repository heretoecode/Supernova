package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import android.app.Application;
import android.content.Context;
import com.archos.filecorelibrary.connections.DraftConnectionTest;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SavedConnectionReachabilityTest
{
    @Test public void restartRetainsLastSeenWithoutPretendingTheOldResultIsStillReachable()
    {
        Application app = RuntimeEnvironment.getApplication();
        app.getSharedPreferences("connection_last_seen_v1", Context.MODE_PRIVATE).edit().putLong("7", 123).commit();
        try (DraftConnectionTest.Draft draft = new DraftConnectionTest.Draft(DraftConnectionTest.Type.WEBDAV,
            "localhost", 80, "/", "", new char[0]);
             ConnectionReachability repository = SavedConnectionReachability.create(app, Map.of("7", draft)))
        {
            assertEquals(123, repository.snapshot().get("7").lastSeenUtcMs());
            assertFalse(repository.snapshot().get("7").reachable());
            assertThrows(IllegalArgumentException.class,
                () -> SavedConnectionReachability.create(app, Map.of("webdav://private/path", draft)));
        }
    }
}
