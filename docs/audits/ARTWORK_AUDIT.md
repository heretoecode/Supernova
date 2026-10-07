# Artwork pipeline audit — 7 October 2026

## Scope
Posters, backdrops, official title/logo artwork, card presentation, caching/retry and diagnostic evidence.

## Findings
- Artwork enters Supernova through several paths: scraped/library poster/backdrop data, Preview card presentation, remote image enrichment and Official Title Artwork.
- The corrective candidate already fixed an evidenced failed-logo negative-cache problem and unnecessary section rebinding, but artwork reliability remains observationally PARTIAL.
- Existing diagnostics include artwork request/trace instrumentation and focused-card retry tests. Historical raw request/failure totals are not enough to attribute root cause.
- Official title/logo assets have highly variable aspect ratios. Layout must reserve a stable logo zone rather than deriving metadata placement from the image's raw bottom edge.
- Focus scaling must treat artwork, rounded mask/boundary and focus/glow as one visual unit. This behaviour has physical acceptance on the Home thumbnail path and must be protected.
- UI rebuilds must not trigger avoidable image reloads for unchanged identities.

## Risk areas
- Duplicate requests caused by rebinding/rebuilding.
- Failed-image retry storms or overlong negative caching.
- Wrong-size images selected for TV distance.
- Stale artwork after rematch.
- Cross-title image races when focus changes quickly.
- Backdrop/logo/poster fallbacks being confused with one another.
- Network loss causing flicker despite a usable cached image.

## Recommendations
1. Give every artwork request a stable media ID + artwork-role + size identity.
2. Define role-specific fallback chains: poster, backdrop, logo/title art, episode still.
3. Coalesce identical in-flight requests and retain bounded negative backoff.
4. Keep stale-good cached artwork during refresh failure.
5. Cancel/fence late responses when a view is rebound.
6. Add diagnostics for cache hit, network fetch, coalesced, cancelled/stale, decode failure and fallback chosen.
7. Build a physical Shield soak checklist: rapid horizontal navigation, Details open/back, network disconnect/reconnect, rematch, missing-art titles and large libraries.
8. Do not declare #35 closed until device traces identify acceptable request/failure/retry behaviour over real use.
