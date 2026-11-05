package com.archos.mediacenter.video.leanback;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** One inventory for the landing count: media issues are deduplicated and deleted rows never count. */
public final class LibraryAttentionCount
{
    public final int titles;
    public final int sources;
    public final int count;
    public final String reason;

    private LibraryAttentionCount(int titles, int sources)
    {
        this.titles = titles;
        this.sources = sources;
        count = titles + sources;
        reason = count == 0 ? "" : titles + " titles; " + sources + " sources";
    }

    public static LibraryAttentionCount count(Set<Long> indexed, Set<Long> metadataIssues, Map<String, ?> markers)
    {
        Set<Long> titles = new HashSet<>(metadataIssues);
        titles.retainAll(indexed);
        Set<String> sources = new HashSet<>();
        for (Map.Entry<String, ?> marker : markers.entrySet())
        {
            String key = marker.getKey();
            if (key.startsWith("offline:") || key.startsWith("problem:"))
            {
                sources.add(key.substring(key.indexOf(':') + 1));
            }
            else if (key.startsWith("media:") || key.startsWith("incorrect:") && Boolean.TRUE.equals(marker.getValue()))
            {
                try
                {
                    long id = Long.parseLong(key.substring(key.indexOf(':') + 1));
                    if (indexed.contains(id))
                        titles.add(id);
                }
                catch (NumberFormatException malformed)
                {
                    // Orphan/malformed evidence does not describe an actionable indexed row.
                }
            }
        }
        return new LibraryAttentionCount(titles.size(), sources.size());
    }
}
