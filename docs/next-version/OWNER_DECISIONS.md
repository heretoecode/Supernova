# Decision packet — reconciled after PR #29 continuation

**Current direction:** the owner authorised HUD measurements/D-pad engineering within the approved design and chose strict ChaptersDB matching (unverified sets hidden). The HUD A/B and paused-display questions below are historical proposals, not remaining approval requests. Implementation uses A, hides paused finish prediction, and retains the approved composition. See CONTINUATION.md for current source-backed decisions.

The editor now implements four sections with existing SharedThreePanel geometry, Both naming, Delete in Page Details and the existing pinned in-panel Review Save. Retaining those source conventions needs no new approval. Only adopting the proposed new proportions/type/card treatment, alternative poster-grid layout or moving Delete remains a genuine optional visual choice. Candidate drawings below are not approved exact designs.

Historical proposal section below: derived from baseline source; its diagrams do not depict the current implemented layout. Current implementation tokens and validation are in CONTINUATION.md. Source measured at baseline `b4a6ee7a`; current screenshots required by the blueprint cannot be called approved mock-ups. No approved image bytes were supplied.

## HUD — source measurements and proposed single row

Android logical reference is 960 × 540 dp, corresponding to 1920 × 1080 px at density 2. Physical resolution alone does not establish density. TvLayout.scale clamps min(width/960,height/540) to .85–1.25; PreviewDialog.dp uses that scaling, while existing XML dimensions use raw dp. Current implementation retains each established conversion once; HUD XML and shared panel helpers do not apply a second scale.

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

Resolved by the owner’s PR #29 continuation instruction: derive engineering details from existing conventions. Implementation uses A, with Play/Pause Up and Settings Down returning to seek, and hides Ends at while paused/unknown. No additional owner sign-off is requested for this engineering graph or arithmetic. The current measured implementation is documented in CONTINUATION.md; this original table remains a proposal history.

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

Current optional owner choices are limited to adopting the proposed new geometry/type/spacing/card/grid treatment or moving Delete. Four-section source parity, retaining pinned in-panel Review Save, Delete in section 1, Both naming and existing truthful statistics do not require another approval under the continuation instruction. Only the proposed new visual metrics remain unapproved. The source-backed four-section structure, parity, Save, retained Delete location, Both naming and actual preview are implemented using existing shared tokens. That does not silently approve the new 22/35/43 geometry or poster-grid proposal.

## ChaptersDB prerequisite decision

Official API page and its loaded script were inspected 2026-10-30. Published routes support movie and episode lookups, but the documented examples currently return empty/404 responses (retained JSON probes). Documentation lists free/no-auth access but no verified data reuse licence, caching/rate-limit policy or structured runtime, season/episode and edition fields. `note` free text and votes cannot establish exact cut alignment. `/terms` returns the SPA shell, and the application's route table has no terms/licence page. This is limited evidence, not proof the service has no terms elsewhere.

Continuation research resolved API-use permission: the official page explicitly permits free unauthenticated application integration, and existing movie/series IMDb/TVDB lookup routes work. The acquisition/cache/strict ID paths are now wired without a new preference or substitute provider. Current v1 data still lacks cut/runtime proof. The owner has answered the remaining matching policy: **keep unverified sets hidden**. No contact with the provider was authorised or sent, and no additional legal/design approval is requested. Embedded chapters/IntroDB remain preserved. See CHAPTERSDB_CONTINUATION_RESEARCH.json and CONTINUATION.md for current evidence.

## Annotated section diagrams

[Section 1](design-proposals/edit-section-1-normal.png) · [Filters](design-proposals/edit-section-2-normal.png) · [Display Options](design-proposals/edit-section-3-normal.png) · [Review](design-proposals/edit-section-4-normal.png).

Each has a `-large.svg` counterpart for expanded text annotations. These are explicitly labelled design diagrams, with no fabricated media/counts and no screenshot or final typography claim. They supplement the measured table; they do not fulfil physical/production screenshot acceptance. The real global navigation is outside this diagram crop. Narrow screens retain the documented focus-revealed viewport proposal.

### Live coverage follow-up

The examples in the documentation are absent, but **the service is not generally unavailable**. `/api/shows/recent` led to approved public sets; `/api/v1/show/bad-times-at-the-el-royale` returned a movie with IMDb `tt6628394`, and `/api/v1/show/star-trek-voyager` plus its first episode's chapters endpoint returned TV data. Sanitised evidence is in `CHAPTERSDB_LIVE_SETS_2026-10-30.json`. Arrays arrive out of timestamp order; the movie's edition note is null, the TV pilot is a long combined DVD chapter set with no structured runtime, and its TVDB episode identifier is empty. These observations substantiate the edition/runtime/episode matching blocker even though both content types have some coverage. They do not establish global uptime or coverage. Subsequent continuation wired bounded acquisition; these unverified sets still never become visible markers.

## HUD diagram

[Measured HUD proposal](design-proposals/hud-measured-proposal.png) ([editable SVG](design-proposals/hud-measured-proposal.svg)) shows the single shared centre, source-derived icon shapes, target bounds and both remote graphs. Placeholder time strings illustrate measured allocations; no media, chapter or certification data is asserted. It is a review aid, not a production render. HUD engineering decisions have since been resolved under the owner’s continuation instruction; this historical diagram is not the current acceptance evidence.
