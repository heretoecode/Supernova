# 0.137 QA — TV air-date sorting and Preparing Playback screen

**Status:** User-reported defect and approved next-version design, 2026-10-12. Documentation-only; not yet implemented or source-verified. Include in consolidated 0.138 scope.

## TV Shows — Air Date sort defect
- Filters clear, Sort = **Air Date**, direction **Newest First**. Observed initial order: Father Ted (1995), Only Fools and Horses (1981), DTF St. Louis (2026), Onar (2026), All Her Fault (2025). Clearly inconsistent with descending displayed year.
- With **Oldest First**, initial order Horizon (1964), The O.C. (2003), The Office (2005), Orwell (2007), Wild Scandinavia (2011), apparently plausible at top, but **Father Ted (1995) is at bottom** of list. Thus both sort directions need end-to-end audit.
- Audit sort key (series first air date vs episode air date vs year), metadata missing/invalid dates, comparator and tie-breaking, sort direction, full list vs paginated/sectioned batches, stale cached ordering and refresh, filters and custom Library shared code. Do not assert root cause before tracing.
- Acceptance: full TV collection sorted deterministically by intended **series air date** ascending/descending, with explicit missing-date policy and stable ties, correct top/middle/bottom, direction changes and re-entry. Verify year-only UI vs full-date sorting distinction and regression-test exact reported examples.

## Preparing Playback — approved minimal global design
- Retain **official title typography/logo artwork**, proportional and visually consistent.
- Remove current season/episode counts, duplicated episode number, runtime and other content metadata from Preparing Playback; screen is brief and should not be overloaded.
- Under artwork display **at most one genuine tagline**: Movie -> movie tagline; TV episode -> **episode tagline** if available, otherwise **season tagline**, otherwise **TV show tagline**; if none, **artwork only**. No blank reserved tagline gap.
- Taglines must be verified as such from metadata; **do not substitute synopsis, episode title, invented copy or another unrelated field**. Audit whether episode/season taglines are actually available from providers and cache; skip unavailable levels without false claims.
- Preserve existing loading/progress and playback functionality, navigation, timing, scrim/contrast, branding and cancellation/error behaviour. Ensure long tagline wraps/ellipsises gracefully without disrupting logo.
- Apply consistently to movie and TV playback entry points; test all fallback levels and missing art/tagline cases.

## Privacy and QA
- No raw Shield media or diagnostic logs uploaded to GitHub/Codex without user consent. Require exact-source tests and Shield verification.
