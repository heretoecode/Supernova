# Exact design decisions for review

Status: proposals derived from measured source, NOT locked tokens or implemented layouts. Source measured at baseline `b4a6ee7a`; current screenshots required by the blueprint cannot be called approved mock-ups. No approved image bytes were supplied.

## HUD — source measurements and proposed single row

Android logical reference is 960 × 540 dp, corresponding to 1920 × 1080 px at density 2. Physical resolution alone does not establish density. TvLayout.scale clamps min(width/960,height/540) to .85–1.25; PreviewDialog.dp uses that scaling, while existing XML dimensions use raw dp. A single implementation must remove that inconsistency rather than double scale.

| Element and source | Measured existing value | Proposed baseline (dp/sp) | Reference px at density 2 |
|---|---|---|---|
| Left/right HUD/title inset; player_controller_experimental | 32 dp | 28 dp shared TvLayout safe inset | 56 |
| Control group safe bottom; PlayerController.updatePreviewScrubber | max(16 dp, 4% view height) | max(24 dp, 4% height) | ≥48 |
| Play/Pause group, pause ID | 44 × 44 dp, 36 × 40 icon target | 44 × 44 with centred 22 icon, existing circle/ring | 88 × 88 |
| Seek target, seek_progress | 24 dp hit height, 4 dp track | Retain 24/4 | 48/8 |
| Thumb, preview_seek_thumb | 10 focused / 6 unfocused dp | Retain | 20/12 |
| Total / Ends at text, time ID | 14 sp, currently combined time | Separate total + end label, 14 sp, content width | 28 nominal |
| Settings pill, preview_more_group | 116 × 40 dp, separate row | Retain 116 × 40; same transport centre | 232 × 80 |
| Horizontal gaps | 12 / 14 dp | 12 dp between fixed groups; flexible seek | 24 |
| Title/logo bounds, preview_playback_title | 380 × 64 dp, 23 sp fallback at top | 380 × 64, middle-left safe edge; actual logo preserved | 760 × 128 |
| Metadata | 13 sp, 4 dp title gap | Retain | 26 / 8 |
| Bubble, preview_scrub_time | 13 sp, min 22 dp; 7 horizontal, 3 top / 7 bottom padding | Retain; always visible with seek; clamp within seek bounds | 26 / ≥44 |
| Bubble pointer, SeekTooltipDrawable | 4 dp notch, 5 dp radius, 2 dp bar gap | Retain | 8 / 10 / 4 |
| Scrim, mark_hud_background | Existing resource | Retain local contrast only; no new imagery | — |

All transport items share one vertical centre. At width 960, safe width is 904; fixed group widths approximately 44 + 116 + measured text + 48 gaps, remainder belongs to seek. At large font, text measures before assigning flexible width; no truncated time. 720p/1080p/4K acceptance must use actual configurations and screenshots, including long duration/chapter text. This table is a reviewable proposal, not a claim of those captures.

### HUD decision 1: exact remote graph

Source currently focuses Pause on show, has separate Settings row and intercepts Up/Down in PlayerController; focused seek consumes Left/Right through existing seek policy. Proposed alternatives preserve Left/Right seeking and physical media keys:

A (recommended, follows existing vertical shortcuts): seek initial; **Up → Settings, Down → Play/Pause**. Settings Down and Play/Pause Up return to seek. Endpoint Left/Right stay put. OK on seek retains existing toggle/seek-commit semantics; OK on buttons acts. Back dismisses HUD then exits on next Back. Settings close restores invoking Settings control.

B: seek initial; Down enters Play/Pause; while in controls, Left/Right traverse Play/Pause ↔ Settings; Up from either returns to seek. This uses a virtual second focus row despite the one-row visual. It changes existing endpoint movement more substantially.

Exact choice A or B requires owner sign-off under the locked HUD blueprint. The measured geometry above also needs approval. Paused Ends at proposal: hide it while paused or duration unknown; resume recomputes at actual speed. Owner must confirm this presentation rather than silently adopting the old 'if resumed now' interpretation.

## Edit Library decision packet

Measured current source: five steps; panels .24/.38/.38; 16 dp gaps, 20 dp padding, 16 dp radius; form rows fixed 42 dp; heading 21 sp; action 18 sp (min 50 dp overridden by editor); editor inset 24/14/24/16; preview foreground on ScrollView. These explain current density; no geometry is locked.

Recommended proposed layout for all four approved sections: 960 × 540 logical, shared safe insets 28/24, existing global navigation retained. 22% rail / 35% form / 43% preview, 16 dp gaps, 18 dp internal padding, 16 dp radius, minimum 50 dp rows that grow with text. Below 780 logical dp retain SharedThreePanel's existing 900 dp focus-revealed horizontal viewport until another narrow layout is approved. Focus frame must be stationary outside preview scroll. Existing static PreviewUtilityBackground and sans-serif-light; no generated assets.

| Section | Exact contents / proposed action placement |
|---|---|
| Page Details & Content Type | Name; Movies / TV Shows / Both. Keep **Delete Page here**, existing pages only, preserving confirmation, until a move is approved |
| Filters | Existing genre multi-select + Any/All, single-year/All, watched state, minimum rating, conditional language and studio/network; no new facets |
| Display Options | Existing Grid/List; Title/Year/Date Added; Ascending/Descending |
| Review | Actual draft summary including retained collection when set; **Save pinned inside form panel**, not global footer |
| Preview | Title; count of matching movies/shows, indexed bytes, local/network counts with partial-size caveat; actual first 20 matching posters/items, loading/error/empty distinct. No invented runtime |

Recommended label: **Both**, preserving current naming. No footer Previous/Next/Cancel. Form Back → rail; rail Back → guarded exit; preview Left/Back → last valid semantic field; rail Right → first active field (Review → Save); terminal keys retained. Explicit global tab exit runs its requested continuation after Save/Discard; Continue retains draft.

Owner approval needed for: proposed geometry/type/spacing and normal/large/narrow annotated designs; Pages 3/4; pinned Review Save; retaining Delete in section 1; label Both; exact statistics above. The complete redesign remains blocked until the blueprint's visual/metrics review is satisfied. Source-only fixes already implemented do not imply that approval.

## ChaptersDB prerequisite decision

Official API page and its loaded script were inspected 2026-10-30. Published routes support movie and episode lookups, but the documented examples currently return empty/404 responses (retained JSON probes). Documentation lists free/no-auth access but no verified data reuse licence, caching/rate-limit policy or structured runtime, season/episode and edition fields. `note` free text and votes cannot establish exact cut alignment. `/terms` returns the SPA shell, and the application's route table has no terms/licence page. This is limited evidence, not proof the service has no terms elsewhere.

Required before production wiring: an authoritative provider licence/usage policy and working movie/episode examples with reliable edition/runtime/episode identity information. No substitute provider, title-only guess, highest-voted auto-selection or new preference has been added. Owner may supply those provider agreements/examples or authorise contacting the provider separately. Integration remains approved but blocked on these prerequisites; embedded chapters/IntroDB remain unchanged.
