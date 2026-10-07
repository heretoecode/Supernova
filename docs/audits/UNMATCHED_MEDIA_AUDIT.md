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
6. **SUPERSEDED:** the earlier recommendation to retain Unmatched in Movies/TV is no longer current. Once Library Health is implemented, remove the Movies/TV Unmatched/Unscraped user-facing control and make Library Health → Unmatched Media the single management location.
7. Add bulk retry only with clear progress/cancellation and without hammering providers.
8. **SUPERSEDED:** Library Health is no longer parked. It is an **approved future design direction** under Network & Files; it remains outside the active fixes candidate until explicitly promoted for implementation.


## Reconciliation with approved Network & Files design — 7 October 2026
This audit is now accounted for by the approved Network & Files / Library Health design. The durable user-facing flow is **Network & Files → Library Health → Unmatched Media**. The approved submenu mock-up is stored at `docs/design/references/network-files-submenus-approved.png`, with functional authority in `docs/design/NETWORK_FILES.md`.

Unmatched items should expose filename/path and useful inferred evidence; Find a Match remains the primary correction action. Successful rematching must preserve physical file identity and playback/watch state while reconciling metadata, artwork and provider caches. Library Health should surface actionable problems, not temporary failures Supernova can recover automatically.
