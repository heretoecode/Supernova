# Unmatched / unscraped media audit — 7 October 2026

## Scope
Detection, library presentation, search/manual matching, rematching and fallback behaviour for media without catalogue metadata.

## Findings
- Legacy Nova already has dedicated non-scraped loaders/screens, grid/list modes, sorting, search and autoscrape/manual-scrape flows.
- Preview also carries unmatched entries in its library snapshot and exposes conditional unmatched controls.
- Two UX generations therefore coexist: legacy NonScraped surfaces and newer Preview library/matching flows.
- Manual matching is strategically important because it establishes the catalogue identity used by Details, artwork, providers and future features.
- Current corrective work improves forgiving title normalisation/ranking and Find a Match keyboard/result presentation, reducing friction but not eliminating ambiguous matches.
- Filename/path information remains essential fallback evidence and should not be hidden when metadata is absent.

## Recommendations
1. Make Preview the eventual single user-facing unmatched workflow; retain legacy machinery only as implementation support until parity is proven.
2. Define unmatched states: never attempted, no confident match, failed provider/network lookup, deliberately left unmatched, and broken/stale prior match.
3. Show enough filename/path/year/episode parsing evidence to make manual correction safe.
4. Preserve direct ID entry/search for difficult titles.
5. After rematch, invalidate/reconcile title-level metadata and artwork caches without losing playback history/file identity.
6. Keep “Unmatched” conditional in Movies/TV and hide it entirely when count is zero.
7. Add bulk retry only with clear progress/cancellation and without hammering providers.
8. Future Library Health can consume these states, but Library Health remains parked.
