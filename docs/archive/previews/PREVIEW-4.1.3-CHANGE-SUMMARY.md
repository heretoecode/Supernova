# Preview 4.1.3 source/change summary

## Continuation and authority

Continued the delivered Preview 4.1.2 implementation, not an older Nova baseline.
Recovery is recorded separately. The 20 September 2026 FINAL 4.1.3 handover's
written corrections and approved poster-free Details reference are authoritative.
Source, generated build outputs and physical evidence were kept separate.

## Implemented

- Shared restrained perimeter-glow focus, neutral/translucent controls, accent
  text/icons, no top-navigation underline. Fixed the cog's custom Drawable paint
  so focus changes reach the rendered icon. Cards brighten without dimming labels;
  list separators and keyboard focus/character depth are softened.
- Movie and TV Details share the poster-free content column, official transparent
  title artwork or text fallback, narrow synopsis, metadata/rating grouping,
  compact circular unboxed people and See All. Genuine provider actions share the
  main row; primary provider plus More Options retains library playback and native
  additional-provider handlers. No invented providers, awards or rating sources.
- Details More Info is a wide, height-capped centred overlay over a dimmed/frosted
  Details snapshot, without an X. Back restores the originating control. Cast is
  limited initially; complete people remain available. Playback Information also
  uses a subset/See All and a bounded content-sized panel.
- Featured has one More Info action and independent directional navigation. Manual
  item changes slide the hero content and backdrop subtly. Initial readiness now
  includes the official logo request, including unavailable/error completion;
  cached identity aliases prevent a recycled hero flashing fallback text first.
  No arbitrary logo delay was added. The scrolling Home header stays transparent.
- Indexed library source classification now uses `Video.getFileUri()`, not the
  database-row `getUri()`. The old method classified indexed WebDAV/SMB as local.
  Counts and sizes use existing index data, with no per-open network scan. Series
  spanning sources are explicitly explained and unique total shows remain separate.
- Network & Files has one full-viewport supplied background. The previous body
  rendering restarted that image below navigation, creating a compositing seam;
  duplicate browser/background layers and the body's extra gradient were removed.
- Search retains the compact keyboard, parent-series discovery and accent-insensitive
  matching; Top Result, series metadata and View Show now have distinct hierarchy.
- Columns, Customise Home and subtitles are smaller/content-capped. Subtitle
  Settings remains at the bottom. HUD focus/seek/Back/direct-track behaviour remains;
  Play/Pause is outlined/translucent and movie timing reads Movie Ends.
- Modern Settings retains its three-panel organisation. Functional inventory
  identified working projector and torrent path/blocklist preferences that had been
  disabled during redesign; these are reachable again without restoring legacy UI.
- Opt-in, off-by-default structured diagnostics with bounded rotation, process/
  playback IDs, lifecycle/focus/player/track/seek/checkpoint/error events, scan/
  metadata/provider/browser/backup instrumentation and document-picker ZIP export.
  Privacy allow-lists and exception-message exclusion apply. See the diagnostics
  guide for coverage, storage and failure limitations.

## Playback Information investigation

The supplied physical video proves the navigation sequence and restart, but does
not supply the exception. The previous route opened a separate Details activity,
serialised live metadata and could probe the active stream again if metadata was
absent. Preview now formats a copy of current metadata into a read-only dialog
inside the player; no second Activity or metadata probe is started. Null metadata
has an explicit safe state. Instrumentation records entry, availability and close.

This removes the identified lifecycle/probing hazard. It does **not** establish
the original Shield exception or prove physical acceptance. Targeted fixtures and
the native emulator route are reported separately in the final test results.

## Preserved / deferred

Preserved the delivered playback focus graph, first/second Back semantics, remote
seeking, direct Audio/Subtitles, current-track labels, official-logo cache/visible
bounds, existing progress safeguards, parent-series matching, modern browser and
settings architectures. No source/signing/package reset was performed.

Not implemented: Smart Rows, genre rules, automatic subtitle acquisition/triggers,
subtitle-language cleanup, Profiles or unrelated 4.2 features.

Random termination, long-duration A/V sync, abnormal-exit progress persistence,
real providers, populated library readiness/performance and all physical Shield
acceptance remain **NOT TESTED — USER QA REQUIRED**.
