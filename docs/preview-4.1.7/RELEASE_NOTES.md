# Supernova Preview 4.1.7 — release notes

Delivery status: **FINAL development build**, from 7efb4195639205a6526281491af40c0dab1776b7. See IMPLEMENTATION_REPORT.md for verified build/hash/certificate evidence and the explicit physical/live limitations.

## Interface and navigation

- Refines the shared Supernova navigation, focus boundary, card clipping/glow and scrolling shade while retaining the six-column library grid.
- Bounds Home display rows without deleting playback history; improves row customisation, genre rules and exact item limits.
- Adds persistent background metadata enrichment and cached title packages, including series/season information, providers, artwork and local technical facts.
- Refines Movies/TV filtering, sorting, columns, unmatched-title entry and exact Details return.
- Completes the shared Details surfaces for episodes, Extras, recommendations, information panels and cast/crew, with title-specific provider links where supported.
- Refines More workflows for watched state, Home rows, physical versions, subtitles, artwork and explicit metadata matching.
- Corrects playback Info return, technical-only information, seeking and cached preparing-playback artwork.
- Uses the shared Search keyboard and retains query/result focus across Details.
- Refines Network & Files, the capability-driven shared browser, source/bookmark distinction and scan controls. Internal protocol aliases display friendly names.
- Retains the Settings foundation and fixed category rail, with explicit child entry and deterministic return.

## Reliability and diagnostics

- Correlates scan, metadata, artwork, playback and provider operations; records semantic focus and restoration outcomes.
- Separates routine diagnostics from important/incident retention, adds safe report references/QRs and linked export summaries.
- Preserves failure-time recorder state and routes post-failure context by event time despite delayed writers.
- Links put.io sync requests to their parent operation and records cancellation/terminal outcomes correctly.
- Preserves monochrome provider logo detail and restores the correct appearance when a view is reused for local media.

## put.io and identity

- Adds native account/discovery/association infrastructure with incomplete-snapshot protections and stable provider identities. Original-quality playback remains on WebDAV and the AVOS/FFmpeg stack.
- Production OAuth configuration/live-account validation remains explicitly outstanding; this is not a claim that an unconfigured production connection works.
- Launcher name is Supernova and the approved Shield banner is retained. Package and signing continuity are preserved.
- Upstream Nova coexistence was investigated without changing identity; the exact Shield installer failure still needs external evidence.

## Validation limits

The final delivery record identifies its source SHA, successful runs, certificate and APK hash. Physical Shield focus, real network/library/playback, installed 4.1.6 data preservation, banner legibility and multi-day diagnostic behaviour remain separately labelled physical QA. The earlier interim APK is not the final release.

No main merge, Phase 1, next-release work, person discovery, trick-play thumbnails, put.io transfer management or primary playback transport replacement is included.
