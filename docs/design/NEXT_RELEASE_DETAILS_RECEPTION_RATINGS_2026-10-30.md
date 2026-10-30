# Next release — Details Reception rating data and layout (30 October 2026)

Status: owner-reported 0.147 Shield QA issue; investigate and fix next release, not delivered 0.147. Screenshot supplied in chat of Marshals Details > Reception; image is NOT yet in GitHub.

## Observed
- TMDB 8.0/10, 406 votes appears as a small inline text directly under Reception heading.
- Separate IMDb, Rotten Tomatoes and Metacritic rows each say 'No rating available'; trophy/awards row says 'Unavailable'.
- Empty rows occupy more space than the one actual score; layout looks unbalanced.
- These observations do NOT prove IMDb/RT/Metacritic data is available from currently configured APIs or that the integrations exist. Investigate source and matching before promising data.

## Required investigation and correction
1. Trace real data paths for TMDB, IMDb, Rotten Tomatoes, Metacritic and awards: API/provider capability, enabled credentials/entitlements, source ID mapping (especially TV series), network/cache/error and parsing; distinguish unsupported provider from missing score and failed fetch. Never invent ratings or imply unsupported providers are connected.
2. Make genuine available ratings visually prominent and consistently aligned in the Reception panel; TMDB score must not be compressed into a small heading subline. Present provider, numeric score and vote count when reliable, with appropriate typographic hierarchy.
3. Hide empty provider rows and unavailable trophy/awards rows; reflow panel or omit Reception entirely when no reliable data, consistent with current approved docs/design/DETAILS.md. Avoid repeated 'No rating available' labels.
4. Keep TV and movie reception presentation consistent; handle no scores, one score and multiple scores; avoid layout jump from late data, coordinate with details-page preloading requirements in NEXT_RELEASE_DETAILS_PROGRESSIVE_LOADING_2026-10-30.md.
5. Test provider data availability/missing values, cold/warm cache, provider failure, ratings changes and real Shield appearance. Record root cause and supported provider contracts separately from design implementation.

No changes to signing, frozen 0.147 source, main or active PRs.
