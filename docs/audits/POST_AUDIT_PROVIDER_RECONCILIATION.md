# Provider decisions — post-audit reconciliation (7 October 2026)

**Status:** planning and authority reconciliation only. No new providers approved; no production change authorised. Do not widen the active fixes-only candidate. Prior explicit user decisions outrank exploratory suggestions in assistant replies.

## Retain established provider decisions
- TMDb — approved canonical movie/TV catalogue enrichment; maintain existing integration.
- Local indexed media database — authoritative for library membership, file identity and playback/watch state.
- Native file inspection — authoritative for technical file facts and actual playable runtime where available.
- Trakt — retain existing inherited Nova functionality and already approved discovery/popularity usage. A new Reception reviews consumer is NOT part of that existing integration.
- SkipDB — approved primary intro/outro markers; TheIntroDB fallback.
- MDBList — approved for list curation only, **not** a generic metadata/ratings source.
- OpenSubtitles — retain existing subtitle integration.
- Streaming availability — keep country/provider scoped, subscription/free/ad-supported only, not rental/purchase; no reopening previously rejected direct providers.
- put.io — separate account integration from WebDAV transport; production OAuth remains external.
- Other prior exclusions/parking stay unchanged: Watchmode, Letterboxd direct integration, MyAnimeList (parked), Plex, Rotten Tomatoes direct, Metacritic direct, RogerEbert.com (parked), JustWatch direct as standalone data provider, FlixPatrol, Blu-ray.com, YouTube as metadata source, A Good Movie to Watch (parked), BestSimilar (parked), uNoGS. Do not infer that a provider is newly approved simply because it appeared in an exploratory response.
- StevenLu historical status needs authoritative decision reference before any change; do not treat as approved.

## Post-audit implementation gaps (not a provider reselection)
1. Field-provenance registry: field, identity level, source, precedence, freshness, null behaviour, UI surface and persistence.
2. Precedence: playable local runtime and technical facts from native inspection; catalogue identity/title/year from matched TMDb; source-labelled independent reception values; stable fixed factual slots show Unknown when absent, optional reviews omitted.
3. Format full release dates as British ordinal text (e.g. 10th October 2017).
4. TMDb rating and vote count on one attributed line; preserve IMDb/TMDb IDs through rematch and playback/watch state.
5. **IMDb ratings and vote counts are EXCLUDED from the Supernova Reception UI and implementation scope by explicit user decision (7 October 2026).** No IMDb score row, Unknown placeholder, fallback rating or further provider investigation. Reason: no verified free, lawfully reusable, sustainable rating feed compatible with the project's no-subscription/practical-limits requirement. Retain IMDb title IDs internally only where useful for identity/matching; IDs do not authorise rating display.
6. Trakt review: **conditional, not approved for implementation** pending exact endpoint, rights, spoiler and attribution checks. Preserve existing Nova integration, avoid forced user login for public data if authorised, omit optional review if unavailable.
7. Artwork: role-specific fallback poster/backdrop/logo/episode still, stale-good caching, coalescing, late-response fencing, rematch invalidation, Shield QA. #35 remains PARTIAL until physical acceptance.
8. Provider operational contract: per-provider bounded requests, caching, rate-limit/backoff, no credential leakage, offline fallback, no external-service dependency for local playback.
9. Streaming availability and put.io/WebDAV retain separate responsibility boundaries.

## Release scope and handover
- **Current corrective release:** fixes only, as governed by `docs/qa/next-version-authority.md`; this document does not amend its scope.
- **Future enhancement handover:** metadata field registry, Reception layout without IMDb rating or vote-count fields; no IMDb provider research or implementation, and other enhancements separately approved.
- Do not reopen rejected/parked services, merge to main, change app identity/signing or claim device acceptance from code inspection.

## Audit references
- `docs/audits/PROVIDERS_DATA_SOURCES_AUDIT.md`
- `docs/audits/METADATA_DATA_FIELDS_AUDIT.md`
- `docs/audits/ARTWORK_AUDIT.md`
- `docs/audits/RECEPTION_IMDB_TRAKT_VALIDATION.md`
- `docs/qa/next-version-authority.md`

**Reconciliation note:** Some historic provider decisions are recovered from project conversation rather than an individually verified GitHub decision record. They are preserved as historical user decisions, not independently recertified API/licence findings. No new provider approval follows from this reconciliation.
