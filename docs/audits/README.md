# Supernova audits

This directory preserves completed audits as durable project evidence rather than leaving their conclusions only in chat history.

Completed:
- [Movies & TV Library Controls](MOVIES_TV_LIBRARY_CONTROLS_AUDIT.md)
- [Playback Subtitles](PLAYBACK_SUBTITLES_AUDIT.md)
- [Playback Audio](PLAYBACK_AUDIO_AUDIT.md)
- [Playback More](PLAYBACK_MORE_AUDIT.md)
- [Code Health / Old NOVA](CODE_HEALTH_LEGACY_NOVA_AUDIT.md)
- [Settings](SETTINGS_AUDIT.md)
- [Metadata and Data Fields](METADATA_DATA_FIELDS_AUDIT.md)
- [Providers and Data Sources](PROVIDERS_DATA_SOURCES_AUDIT.md)
- [Artwork Pipeline](ARTWORK_AUDIT.md)
- [Unmatched / Unscraped Media](UNMATCHED_MEDIA_AUDIT.md)
- [Network & Files](NETWORK_FILES_AUDIT.md)

Audit reports preserve what was inspected, findings, decisions and remaining validation/risk. Product/design decisions derived from an audit are also carried into the appropriate `docs/design/` authority and `docs/qa/next-version-authority.md`.

## Preservation rule

At the end of each Supernova work session, durable project knowledge belongs in GitHub: discussion outcomes, approved/rejected/parked decisions, QA evidence, audit findings, implementation/build evidence and unresolved dependencies. Chat history is not the authoritative project record.


## Audit programme checkpoint — 7 October 2026

All audits discussed in the current foundation programme are now represented here. These are static architecture/product audits of the current fixes branch, not permission to expand the active Shield-fixes candidate and not physical acceptance. Their recommendations are future-development foundations; explicit later product decisions and physical QA remain authoritative.

## Discussion disposition — 8 October 2026
User approved `Unknown` in fixed factual metadata slots while missing/loading with in-place population on arrival; approved existing text-title fallback for missing title logos, generic poster placeholder, and restrained dark colour-tinted backdrop fallback. Library Health and Downloads functional decisions delegated to implementation judgement within approved Network & Files design. Remaining user-discussion subjects: (1) Settings ownership, organisation and legacy preferences, and (2) evidence-based legacy NOVA code cleanup policy/priority. See `docs/design/DETAILS.md` and `docs/design/NETWORK_FILES.md`. This does not alter the fixes-only candidate scope.
