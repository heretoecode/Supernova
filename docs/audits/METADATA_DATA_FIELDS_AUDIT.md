# Metadata and data-fields audit — 7 October 2026

## Scope
Current local/native metadata, Preview enrichment, Details presentation and remote metadata cache paths.

## Current data layers
- Indexed Nova media objects remain the primary local identity/playback layer.
- Native/local technical extraction hydrates codec, bitrate, dimensions/resolution, HDR transfer and audio-track information.
- Preview remote enrichment uses TMDb through the existing application credential and requests bounded sections including base title data, videos, recommendations, credits, images, external IDs, release dates/content ratings and TV season packages.
- Remote enrichment is persisted in a bounded per-title/section cache with a seven-day freshness window; stale usable data survives refresh failure.
- Details combines local media state with remote enrichment rather than introducing a second scanner.

## Findings
- The layered model is sound but needs a formal field-provenance contract. A displayed value should have a known source, freshness and fallback.
- Technical metadata extraction is intentionally queued/backgrounded and has retry/backoff. UI must tolerate incomplete fields without layout jumps.
- Remote metadata is sectioned, allowing independent completion; this is preferable to blocking Details on one monolithic request.
- Local and remote fields can overlap (title/year/runtime/artwork/ratings). Precedence should be explicit, not incidental to completion order.
- Unknown data should be omitted rather than replaced by guessed values.
- External IDs are strategically important for matching, provider lookup and future cross-source reconciliation.

## Recommended field model
For each field record: canonical name; local/native source; remote source; precedence; freshness; null/fallback behaviour; surfaces using it; whether safe to persist.

Priority future work:
1. Build a machine-readable metadata field registry.
2. Make precedence deterministic for title, year, runtime, genres, certification and artwork.
3. Keep technical file facts local/native; do not overwrite them with catalogue guesses.
4. Treat ratings/reception as source-labelled enrichment.
5. Instrument missing/late fields by field name, not just generic metadata failure.
6. Ensure movie, series, season and episode identities cannot leak fields across levels.
7. Preserve direct TMDb/IMDb IDs through manual rematching.

## Development opportunities
A formal registry will make future Details redesigns, Smart Collections, filters, provider matching and Library Health substantially safer.
