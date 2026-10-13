# FD-002 — Playback HUD / playback card redesign — needs further discussion

**Status:** UNDER DISCUSSION — NOT APPROVED. **Release:** unassigned. **Implementation:** not authorised. Updated 13 October 2026.

## Scope and separation
Redesign the in-player playback HUD (also called playback card) and its Settings overlay, **not** the movie/TV Details tab or the playback-start age overlay. The active **0.139 Codex implementation is unfinished and paused due to usage limits**; do not change, reset, merge, cherry-pick into, or compete with that work. Future design documentation only. Existing parked record: [FD-002 historical note](../02%20Parked%20Ideas/FD-002.md). Related [FD-030 seek-bar/end-time refinement](../02%20Parked%20Ideas/README.md).

## Owner's current preferred layout — visual direction, not final approval
- 16:9 TV playback screen; clean dark Supernova styling. The film/background should not introduce extra UI. No invented top bar, decorative metadata or unrelated buttons.
- One **Settings** button **above the seek bar at the right** opens a single settings window with four tabs in this order: **Audio | Subtitles | Video | More**.
- Tabs use a **single connected active-tab/content-panel outline** (like the approved Details tab), a narrower precisely centred raised active tab, **one thin border**, and **floating unboxed inactive tab labels**. No doubled borders.
- Separate **active tab** from **remote focus**; at most **one brightly outlined focused control** at a time, in the user's configured focus/accent colour (white by default). A selected track can show a checkmark without also having a focus outline.
- Remote navigation: Left/Right between tabs when on tab row; Down enters tab's settings; Up returns to tabs; Back dismisses Settings and restores appropriate playback HUD focus. These are proposed behaviours to validate against actual D-pad navigation.
- Bottom HUD: title at lower left; **Play/Pause is the only transport button** to the left of the seek bar; **no rewind or fast-forward buttons**. Seeking via Left/Right on the focused seek/timestamp control must remain. Show progress, remaining time and **Movie ends 22:41**-style estimated finish time. The actual time is dynamic; the time value should have **the same font size, weight and colour** as “Movie ends”, not be bold.
- Preserve established bottom HUD placement and avoid simultaneous focus outlines on Settings, Play/Pause and a selected track. Slightly reduce oversized Settings panel/empty space.

## Non-negotiable functionality rule
**Show exactly the playback settings that exist and work in current Supernova. Do not invent settings, remove any existing settings, or silently alter their choices or conditional visibility.** Do not infer availability merely from upstream NOVA or a generated image. Trace every control, nested menu, values, focus behaviour and conditional visibility to current Supernova code or a reliable existing Codex audit.

## Historical/partial evidence — NOT a complete verified inventory
Earlier discussions refer to a four-control HUD (Subtitles, Audio, Play/Pause, More), Audio and Subtitle Options, Download Subtitles, and More options including Playback Speed, Play Mode and Format. **These are leads requiring source confirmation**, not certification that this list is complete or that every option is present in the current build. A previous Codex investigation reportedly exists, but its complete source-backed inventory was not recovered in this discussion.

## Generated mock-ups — rejected as authoritative option inventories
Several four-tab images were generated in the 13 October 2026 conversation, including a four-quadrant composite. They are **layout explorations only**, not approved implementation assets, and their image files are **not yet committed to GitHub**. The generated images wrongly introduced or failed to verify such controls as video filters, video track, HDR tone mapping, motion smoothing, sharpness, brightness, contrast, video rotation, deinterlacing, colour space, HDR-to-SDR, A–B Repeat, Skip Silence, Night Mode, audio filters, DRC, volume boost, bookmarks, watchlist and others. **Do not treat any of these as confirmed features.** Some mock-ups also showed a double border, multiple focus outlines, extraneous technical metadata and film imagery contrary to the desired minimal layout.

## Required read-only investigation before further visual approval
1. Locate and inspect the previous Codex playback/HUD audit and any supporting documents, and examine the actual current Supernova source at a stable commit. Avoid relying on GitHub code search alone if indexing is incomplete.
2. Produce a **complete, source-referenced** list of all visible playback HUD controls, submenu items, selectable values, conditional track/device/file behaviours, and whether each is currently reachable/functional. Distinguish inherited unused code.
3. Map **every verified option** to Audio/Subtitles/Video/More; explicitly identify whether a genuine Video category exists. Do not manufacture options to populate it. Any question about keeping an empty tab is an owner design decision.
4. Verify seek controls, focus graph, selected-vs-focused styling, opening/dismissing Settings, Back/focus restoration, timeout/visibility, playback end-time computation, and screen size/safe-area constraints.
5. Inspect tests and run safe non-destructive checks as practical. Record confirmed findings, uncertainties, gaps and risks.
6. Only then create corrected, faithful mock-ups of all four tabs and return for **explicit owner approval**. No app coding or release scheduling.

## Decision status
**Preferred design direction:** recorded above. **Not final:** exact option inventories and tab allocation, conditional/empty Video tab, precise geometry and final four-page mock-ups. Codex owns engineering choices; only material UX/visual choices return to the owner.

## References
- [Future Development index](../README.md)
- [Historical FD-002 parked note](../02%20Parked%20Ideas/FD-002.md)
- [Post-0.139 backlog](../../project-decisions/POST_0139_NON_MAINTENANCE_FEATURE_BACKLOG.md)
- [0.139 Codex handover](../../project-decisions/2026-10-13-supernova-0139-codex-implementation-handover.md)

## Latest owner feedback — 13 October 2026 (supersedes earlier bottom-HUD direction)
Owner now prefers **one horizontal playback-control row**: optional Rewind and Fast Forward flanking Play/Pause at left (whether to include both is **undecided**), then the seek bar, with remaining duration and estimated programme/movie end clock time **inline on the same row**. Do **not** show a duplicate elapsed/progress time label at the left: the existing floating timestamp bubble above the seek bar provides current position. The Settings button's position **is unresolved** (above or below row, left or right; owner also considering inline), superseding prior 'above seek bar at right' as final placement. Keep Settings tab structure Audio/Subtitles/Video/More provisional pending actual source-backed inventory; single connected focus treatment remains preferred. No final mock-up or implementation approval yet.

Feature discussion: owner is **interested in investigating embedded chapter navigation and seek-bar thumbnail previews**, plus discussing advanced subtitle controls. Owner does **not want** a playback technical-information panel. Temporary Skip Intro/Skip Credits may already exist: owner will confirm, do not duplicate or claim absent. Existing **Trakt and OpenSubtitles integrations are owner-confirmed**, not new integrations. All engineering feasibility and current-code inventory are Codex tasks. Keep active 0.139 untouched.

## Owner clarification — 14 October 2026
- Preferred no-transport-button single-row HUD (no Play/Pause or skip buttons); Settings above/right. Inline remaining time and generic **`Ends at HH:MM`** label, same typography, no 'Movie'/'TV' prefix.
- The existing floating **`HH:MM:SS` timestamp bubble must remain visible whenever playback HUD is visible**, including ordinary playback, tracking current progress. It is the sole elapsed/progress time display; do not add left-hand elapsed time.
- Preferred optional thumbnail treatment **Design D integrated preview**: timestamp remains readable without overlapping thumbnail. Proposed progressive disclosure: compact timestamp bubble in normal HUD, expand upwards to show thumbnail during seeking; owner has not yet approved exact transition. Thumbnail feature itself still under investigation, not authorised.
- Remote Left/Right held performs **accelerating seeking/skipping through positions**, NOT faster video playback: longer hold means progressively faster traversal; release stops acceleration. Codex to validate existing event handling, seek/preview performance and whether seek commits immediately or on confirmation. Exact OK/Back/Up focus-state model is **not yet owner-approved**; do not presume a two-click model.
