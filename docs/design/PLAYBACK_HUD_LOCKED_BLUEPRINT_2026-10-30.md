# Supernova — Playback HUD implementation blueprint
**Date:** 30 October 2026. **Status:** Owner-approved composition, detailed written implementation contract; exact existing theme tokens/source-level focus graph pending Codex source measurement. Documentation only; not a code-change authorisation.

## Authority and precedence
This document supersedes **visual composition** in older four-button HUD concepts, including `PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md` and the HUD sections of `PLAYBACK.md`. It does **not** remove supported playback functionality or remote shortcuts. In-player Settings remains the approved Concept A4 Audio / Subtitles / Video / More panel, a separate design task. Read maintenance mapping, source audit and `NEXT_VERSION_PLAYBACK_PREPARATION_AND_HUD_REFINEMENTS.md` before changes. Source of truth for current app visual assets and tokens: `GLOBAL_VISUAL_SYSTEM.md` and existing shared components. **Do not copy pixel positions, fake chapter labels, poster art or film metadata from generated mock-up**; it is visual reference only, not an exact UI implementation source.

## Approved composition — 16:9 TV screen
- Existing media video plays behind an unobtrusive HUD with contrast scrim only where needed. No full-screen opaque card, no excessive blue overlay.
- **Official title typography/logo**, if available, at **middle-left, closely aligned to left safe edge** (latest owner adjustment); existing basic media metadata immediately beneath, left-aligned. Do not replace an available official logo with generic title text. Text fallback only if logo unavailable; no additional invented information. This latest left alignment is the owner's requested direction.
- Existing clock at top-right remains in its existing style and location, independent of repositioned title block.
- **One horizontal transport line** raised above the lower TV edge, with elements in this exact visual order:
  1. Focusable Play/Pause icon/button at far left.
  2. Seek bar with blue played section, neutral remainder, distinct playhead, optional **real** chapter tick marks.
  3. **Total duration** `HH:MM:SS` (or supported time formatter).
  4. `Ends at HH:MM` adjacent to total duration (derived from current local time plus actual remaining time).
  5. Single focusable outlined pill **Settings** with existing gear icon at far right, opening **in-player** Audio / Subtitles / Video / More.
- All five groups share **one vertical centre line**, including Play/Pause, seek bar, total duration, Ends at and Settings. No lower secondary action row; do not place duration/end time below bar.
- Seek bar occupies flexible width between Play/Pause and right-hand metadata/settings; labels and pill must not overlap the bar or clip at 720p, 1080p, 4K, large text or overscan. Use constraint-based layout, not mock-up absolute pixel offsets.
- **Current-position timestamp bubble** always shown **above actual playhead whenever the seek bar is visible**, with small notch/pointer aligned to playhead; when chapters exist, display actual current chapter number/name as supported. Never duplicate current time at bar left or beneath bar. Clamp bubble within safe edges and avoid jitter/overlap. During seeking, show candidate seek position; after commit, show actual position. No thumbnail generation (deferred).
- Media with no valid duration/live streams: no false Ends at or fake chapter ticks; use appropriate existing indeterminate/live presentation.

## D-pad, focus and remote semantics
- HUD initial focus **seek bar** on first show and on reopen unless an existing user-accessibility setting requires another default. Left/Right on focused seek bar seek backward/forward using existing player step/acceleration semantics; do not accidentally move focus to Play/Pause or Settings. Seeking works while playing or paused.
- Play/Pause is **focusable and actionable**, with distinct focused/unfocused appearance and state icon; playback status icon is not a focus indicator. Physical Play/Pause remote key remains functional regardless of focus.
- Settings is **focusable**; OK opens the existing in-player Settings panel, not global Settings. Panel dismissal restores stable focus and avoids playback disruption.
- **Unresolved exact focus traversal:** with three focusable regions visually on one row, define a D-pad graph that makes Play/Pause and Settings reachable without stealing Left/Right seek. Codex must derive and document this graph from real Shield remote behaviour and owner must approve it before implementation. A tentative Down navigation path is **not** yet approved. Preserve Back/HUD dismiss and seek commit semantics.
- Distinguish focus from enabled/disabled, selected and playback-state visual cues. No keyboard/mouse-only controls.

## Geometry and design token locking — mandatory before implementation
- Use actual Supernova font assets, icon IDs, focus styles, pill radii, Space Black/blue palette and existing safe-area constraints; **do not invent new branding**.
- Codex to produce a measured 1920×1080 logical reference table for safe margins, baseline height, Play/Pause hit target/ring, seek thickness and thumb, duration/Ends at typography, Settings pill dimensions, title/logo max bounds, metadata spacing, timestamp bubble dimensions/notch, gaps, z-order and scrim. Derive scalable dp values for 720p/1080p/4K and font scaling. Provide annotated screenshots and code resource IDs. Mock-up image perspective is not a pixel ruler.
- Seek tooltip must not obscure logo/metadata, subtitles or chapter ticks; avoid permanent overlay across the media subject. HUD hide/show animation and timeout follow existing approved player behaviour.

## Runtime correctness and regression tests
- Total duration is **total**, not remaining time; `Ends at` reflects real remaining duration and current clock, recalculating after seek, pause/resume, speed changes and duration updates. When paused, do not imply a reliable finish clock unless behaviour is defined (prefer hide or explicit paused state after owner confirmation).
- Display chapter ticks and chapter name **only** from valid, correctly matched chapter data, not fabricated chapter numbers. File-embedded chapters take precedence. Handle multiple chapter tracks, missing labels, offset/stream duration mismatch and out-of-bounds markers. Do not auto-skip chapters; IntroDB segment skipping is a separate subsystem.
- Preserve playback preparation artwork fixes, subtitle visibility, seeking, resume, media-key controls, video rendering and performance. Test real Shield D-pad, first HUD show, repeated show/hide, long media titles, unknown duration, seek to edges, chapter/no-chapter, buffering, pauses, end-time crossing midnight, timezone changes and 720p/1080p/4K.

## External chapter provider — research, **not approved integration**
- **ChaptersDB** `https://chaptersdb.com/api` documents free no-auth JSON endpoints `/api/v1/search`, `/api/v1/show/:id` for movie chapter sets, `/api/v1/episode/:id` and `/api/v1/chapters/:episodeId` for TV episodes, with IMDb/TVDB IDs, named timestamps, edition notes and community votes. Research source: https://chaptersdb.com/api . This is a **candidate**, not confirmed suitable for production: independently test live API availability, coverage, licensing/terms, abuse limits, release/edition alignment and stability before any network calls are implemented. Do not silently choose highest-voted chapter set if duration/edition differs.
- **IntroDB** `https://introdb.app/docs/api` provides intro/recap/outro and similar **segment ranges**, not general scene chapters. Existing Supernova segment controls must not be replaced or confused with chapter navigation.
- Provider strategy proposal: local embedded chapters first; optionally user-enabled ChaptersDB fallback only after research, correct IMDb/TVDB episode matching and edition/duration validation; cache with expiry, timeouts, safe offline fallback, no personal media paths sent to provider. No fabricated ticks on failure.
- Provider is **deferred pending owner decision**. Do not add a new setting or provider request to this HUD release without explicit approval.

## Acceptance gates
1. Owner-approved composition captured here; latest generated image not yet verified stored in GitHub. Preserve approved image in dedicated reference folder after owner uploads, then link filename and SHA.
2. Codex produces measured resource/token appendix and focus graph without changing app code. Owner approves unresolved geometry/navigation.
3. Dedicated implementation authorisation; no interference with current branches/PRs/main.
4. Automated tests + real Nvidia Shield acceptance against this contract; verify screenshot/physical focus and seek behaviour; no claims of verification from simulator alone.
