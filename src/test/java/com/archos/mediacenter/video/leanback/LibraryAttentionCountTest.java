package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class LibraryAttentionCountTest
{
    @Test
    public void deduplicatesIssuesAndSourcesAndIgnoresDeletedOrExcludedRows()
    {
        LibraryAttentionCount result = LibraryAttentionCount.count(Set.of(1L, 2L), Set.of(1L),
            Map.of("media:1", "missing", "incorrect:1", true, "media:99", "deleted",
                "offline:smb://server/films", true, "problem:smb://server/films", true));
        assertEquals(1, result.titles);
        assertEquals(1, result.sources);
        assertEquals(2, result.count);
        assertEquals("1 title; 1 source", result.reason);
    }
}
