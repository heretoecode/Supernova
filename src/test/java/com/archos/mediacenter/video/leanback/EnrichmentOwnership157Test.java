package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class EnrichmentOwnership157Test
{
    @Test public void anAdmissionDatabaseFailureReleasesTheOwnerAndAllowsANewDrain() throws Exception
    {
        Context app = RuntimeEnvironment.getApplication();
        Class<?> storeType = Class.forName(PreviewEnrichmentQueue.class.getName() + "$Store");
        SQLiteOpenHelper original = ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class, "store");
        SQLiteOpenHelper real = (SQLiteOpenHelper)ReflectionHelpers.callConstructor(
            storeType, ClassParameter.from(Context.class, app));
        SQLiteOpenHelper broken = (SQLiteOpenHelper)mock(storeType);
        when(broken.getWritableDatabase()).thenThrow(new SQLiteCantOpenDatabaseException("Controlled storage failure"))
            .thenReturn(real.getWritableDatabase());
        ScheduledExecutorService worker = ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class, "WORK");
        try
        {
            worker.submit(() -> {
                ReflectionHelpers.setStaticField(PreviewEnrichmentQueue.class, "store", broken);
                ReflectionHelpers.setStaticField(PreviewEnrichmentQueue.class, "draining", true);
                ReflectionHelpers.callStaticMethod(PreviewEnrichmentQueue.class, "drain",
                                                  ClassParameter.from(Context.class, app));
                assertFalse(ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class, "draining"));
                ScheduledFuture<?> retry = ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class, "retry");
                assertNotNull(retry);
                assertFalse(retry.isCancelled());
                ReflectionHelpers.callStaticMethod(PreviewEnrichmentQueue.class, "start",
                                                  ClassParameter.from(Context.class, app));
                assertTrue(retry.isCancelled());
            }).get(3, TimeUnit.SECONDS);
            worker.submit(() -> {}).get(3, TimeUnit.SECONDS);
            assertFalse(ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class, "draining"));
        }
        finally
        {
            worker.submit(() -> {
                ScheduledFuture<?> retry = ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class, "retry");
                if (retry != null)
                    retry.cancel(false);
                ReflectionHelpers.setStaticField(PreviewEnrichmentQueue.class, "retry", null);
                ReflectionHelpers.setStaticField(PreviewEnrichmentQueue.class, "draining", false);
                ReflectionHelpers.setStaticField(PreviewEnrichmentQueue.class, "store", original);
            }).get(3, TimeUnit.SECONDS);
            real.close();
        }
    }
}
