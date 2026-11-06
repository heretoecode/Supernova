package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import com.archos.mediacenter.video.leanback.LibraryAttentionCount;
import com.archos.mediacenter.video.leanback.LibraryStatistics;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediacenter.video.scanning.ScanProgress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;

public class LibraryPageFactsTest
{
    @Test public void eightStatsPreserveUnknownSizesAndTheStatusHasAnEvidenceReason()
    {
        LibraryStatistics stats = LibraryStatistics.calculate(new PreviewLibraryLoader.Snapshot(), List.of(),
                                                               1000, java.util.TimeZone.getTimeZone("UTC"));
        stats.total = 3;
        stats.unwatched = 2;
        stats.inProgress = 1;
        stats.localBytes = 100;
        stats.networkBytes = 200;
        stats.unknownNetwork = 1;
        ScanProgress scan = ReflectionHelpers.callConstructor(ScanProgress.class,
            ReflectionHelpers.ClassParameter.from(String.class, "a"),
            ReflectionHelpers.ClassParameter.from(String.class, "source"),
            ReflectionHelpers.ClassParameter.from(String.class, "Source"),
            ReflectionHelpers.ClassParameter.from(long.class, 0L),
            ReflectionHelpers.ClassParameter.from(long.class, -1L),
            ReflectionHelpers.ClassParameter.from(long.class, 0L),
            ReflectionHelpers.ClassParameter.from(long.class, 0L),
            ReflectionHelpers.ClassParameter.from(long.class, 0L),
            ReflectionHelpers.ClassParameter.from(long.class, 0L),
            ReflectionHelpers.ClassParameter.from(long.class, 0L),
            ReflectionHelpers.ClassParameter.from(boolean.class, false),
            ReflectionHelpers.ClassParameter.from(boolean.class, false),
            ReflectionHelpers.ClassParameter.from(ScanProgress.Failure.class, ScanProgress.Failure.NONE));
        LibraryAttentionCount attention = LibraryAttentionCount.count(Set.of(1L), Set.of(1L), Map.of());
        var facts = LibraryPageFacts.from(stats, 50, attention, scan);
        assertEquals(3, facts.titles());
        assertEquals(2, facts.unwatched());
        assertEquals(1, facts.inProgress());
        assertEquals(-1, facts.totalBytes());
        assertEquals(-1, facts.networkBytes());
        assertEquals(100, facts.localBytes());
        assertEquals(50, facts.lastUpdatedUtcMs());
        assertEquals(LibraryPageFacts.Status.NEEDS_ATTENTION, facts.status());
        assertEquals(LibraryPageFacts.Reason.HEALTH_ITEMS, facts.reason());
    }
}
