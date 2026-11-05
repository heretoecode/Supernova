package com.archos.mediacenter.video.leanback;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/** Deterministic hero policy, independent of rendering, Android clocks and playback launch intent. */
public final class HeroRotationPolicy {
    public static final long IDLE_MS = 30_000, INTERVAL_MS = 8_000;
    public static final int TARGET = 10, PER_TYPE = 5;
    private static final int[] WEIGHTED_TURNS = {0, 0, 1, 2, 3};
    private HeroRotationPolicy() { }

    public static final class Candidate<T> {
        public final String identity;
        public final boolean television;
        public final T value;
        public Candidate(String identity, boolean television, T value) {
            if (identity == null || identity.isEmpty() || value == null) throw new IllegalArgumentException("A durable indexed identity is required");
            this.identity = identity; this.television = television; this.value = value;
        }
    }
    public static final class Selection<T> {
        public final Candidate<T> candidate;
        public final int source;
        private Selection(Candidate<T> candidate, int source) { this.candidate = candidate; this.source = source; }
    }

    /** Recent:Trending:Popular:Because = 2:1:1:1 turns; dedupe first, quota next, fallback last. */
    public static <T> List<Selection<T>> select(List<List<Candidate<T>>> sources, List<Candidate<T>> recentFallback, Predicate<Candidate<T>> eligible) {
        if (sources.size() != 4) throw new IllegalArgumentException("Four approved hero sources are required");
        Map<String, Selection<T>> ranked = new LinkedHashMap<>(); int[] offsets = new int[4];
        boolean remaining = true;
        while (remaining) {
            remaining = false;
            for (int source : WEIGHTED_TURNS) {
                List<Candidate<T>> rows = sources.get(source);
                while (offsets[source] < rows.size()) {
                    remaining = true; Candidate<T> row = rows.get(offsets[source]++);
                    if (eligible.test(row) && !ranked.containsKey(row.identity)) { ranked.put(row.identity, new Selection<>(row, source)); break; }
                }
            }
        }
        for (Candidate<T> row : recentFallback) if (eligible.test(row)) ranked.putIfAbsent(row.identity, new Selection<>(row, 4));
        List<Selection<T>> result = new ArrayList<>(); int movies = 0, shows = 0;
        for (Selection<T> row : ranked.values()) {
            if (row.candidate.television ? shows >= PER_TYPE : movies >= PER_TYPE) continue;
            result.add(row); if (row.candidate.television) shows++; else movies++;
        }
        if (result.size() < TARGET) for (Selection<T> row : ranked.values()) {
            if (!result.contains(row)) result.add(row);
            if (result.size() == TARGET) break;
        }
        return result;
    }

    /** Inactivity and cadence are independent: input resets both, lifecycle suspension cancels them. */
    public static final class Clock {
        private long inputAt, nextAt;
        private boolean active;
        public void resume(long elapsed) { active = true; input(elapsed); }
        public void suspend() { active = false; }
        public void input(long elapsed) { inputAt = elapsed; nextAt = elapsed + IDLE_MS; }
        public boolean due(long elapsed, boolean homeAndHeroVisible, boolean motionAllowed, int poolSize) {
            if (!active || !homeAndHeroVisible || !motionAllowed || poolSize < 2 || elapsed - inputAt < IDLE_MS || elapsed < nextAt) return false;
            nextAt = elapsed + INTERVAL_MS; return true;
        }
        public long delay(long elapsed) { return Math.max(1, nextAt - elapsed); }
    }
}
