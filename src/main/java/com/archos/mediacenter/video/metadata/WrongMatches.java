package com.archos.mediacenter.video.metadata;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Worker-only marker/navigation contract for the existing Library Care Fix match flow. */
public final class WrongMatches
{
    private static final String HEALTH = "supernova_library_health_v1";
    private static final String REVISION = "wrong_match_revision:";
    private static final Object LOCK = new Object();
    private final SharedPreferences preferences;

    public record Member(long videoId, String kind, long tmdbId, String matchRevision)
    {
        public Member
        {
            if (videoId <= 0 || tmdbId <= 0 || !("movie".equals(kind) || "tv".equals(kind)) ||
                matchRevision == null || matchRevision.isEmpty())
                throw new IllegalArgumentException("Exact indexed match revision required");
        }
    }

    /** Preselect these file IDs in Library Care's existing wrong-match category (index 1), not a new dialog. */
    public record Selection(String kind, long tmdbId, int category, List<Long> videoIds)
    {
    }

    public WrongMatches(SharedPreferences health)
    {
        preferences = health;
    }

    public static WrongMatches production(Context context)
    {
        return new WrongMatches(context.getSharedPreferences(HEALTH, Context.MODE_PRIVATE));
    }

    /** Pass a fresh complete title/episode inventory, including Put.io. A series marks every indexed episode. */
    public Selection mark(String kind, long tmdbId, List<Member> inventory) throws IOException
    {
        synchronized (LOCK)
        {
            List<Long> ids = new ArrayList<>();
            SharedPreferences.Editor edit = preferences.edit();
            for (Member member : inventory)
                if (member.kind().equals(kind) && member.tmdbId() == tmdbId && !ids.contains(member.videoId()))
                {
                    ids.add(member.videoId());
                    edit.putBoolean("incorrect:" + member.videoId(), true)
                        .putString(REVISION + member.videoId(), member.matchRevision());
                }
            if (ids.isEmpty())
                throw new IOException("Title no longer indexed");
            if (!edit.commit())
                throw new IOException("Wrong-match marker could not be saved");
            return new Selection(kind, tmdbId, 1, List.copyOf(ids));
        }
    }

    /** Reconcile marks against current matches before showing the category; changed/deleted matches clear. */
    public List<Long> read(List<Member> inventory) throws IOException
    {
        synchronized (LOCK)
        {
            Map<Long, Member> current = new HashMap<>();
            for (Member member : inventory)
                current.put(member.videoId(), member);
            List<Long> result = new ArrayList<>();
            SharedPreferences.Editor edit = preferences.edit();
            boolean changed = false;
            for (var entry : preferences.getAll().entrySet())
            {
                if (!entry.getKey().matches("incorrect:[1-9][0-9]{0,18}") || !Boolean.TRUE.equals(entry.getValue()))
                    continue;
                long id;
                try
                {
                    id = Long.parseLong(entry.getKey().substring(10));
                }
                catch (NumberFormatException invalid)
                {
                    continue;
                }
                Member member = current.get(id);
                String revision = preferences.getString(REVISION + id, "");
                if (member != null && (revision.isEmpty() || revision.equals(member.matchRevision())))
                    result.add(id);
                else
                {
                    edit.remove(entry.getKey()).remove(REVISION + id);
                    changed = true;
                }
            }
            if (changed && !edit.commit())
                throw new IOException("Wrong-match marker could not be reconciled");
            result.sort(Long::compare);
            return List.copyOf(result);
        }
    }

    /** Call only after the matching transaction has committed, including when reapplying the same identity. */
    public void applied(long videoId) throws IOException
    {
        synchronized (LOCK)
        {
            if (!preferences.edit().remove("incorrect:" + videoId).remove(REVISION + videoId).commit())
                throw new IOException("Wrong-match marker could not be cleared");
        }
    }
}
