# Details — Design and Behaviour Authority

Status: **APPROVED/current baseline with one current corrective redesign decision outstanding**

## Page model
One continuous vertically scrolling Details page. Initial cinematic Hero; lower navigation near the bottom with teaser content below. DOWN routes actions → lower nav → content/collapse. As the page scrolls, the same title/logo becomes compact/sticky below global nav while hero metadata/actions scroll away; UP reverses smoothly.

## Hero
Hero artwork, official title/logo and concise metadata. Current product direction is Disney+-influenced but Supernova-native: reduce duplication and clutter.
Primary actions are Play/Resume, one useful provider action where appropriate, and More/Actions only where useful. Trailer belongs in Extras rather than duplicating the hero.
Dynamic Play: unwatched=Play; partial movie=Resume; TV=Resume Sx Ex.
Action buttons are content-sized with a constant gap and deterministic left/right/down edges.

## Lower navigation
Movies: **Details → Extras → More Like This**.
TV: **Seasons & Episodes → Details → Extras → More Like This**.
Missing/empty tabs disappear and remaining tabs reflow.
The divider is one white line. Selected segment is that same line recoloured Supernova blue at exactly the same stroke thickness, with optical glow outside only. No second underline/thicker bar. Tab text remains white.

## Seasons & Episodes
Stacked season rows; no dropdown. Four episode cards across. Focus is the whole episode unit (artwork + title/runtime), using the shared enlargement/boundary/glow. Local focus uses a plain white HUD Play glyph. Streaming-only uses a monochrome/translucent provider mark and Watch/deep link. Unavailable episodes may remain visible but are non-playable. Reconcile local first, then enabled streaming, then unavailable/unknown without duplicate episodes.

## More Like This
Collapsed/compact hero; no repeated heading. Landscape thumbnails, four across, maximum 12 / up to three rows, fewer if relevance is weak. Unfocused: artwork + title + year. Focus whole unit. Source mark appears on focus only: local white Play glyph or preferred streaming provider mark. Strong TMDB relevance first, local reconciliation next, enabled streaming next; genre fallback last. Hide the tab if empty.

## Extras
Collapsed hero. Category rows only when populated; no count labels. Four cards across; title + duration; whole-unit focus/enlarge; white Play glyph. LEFT/RIGHT stays in row, UP/DOWN moves categories. Extras/trailers use full-screen playback with the normal playback HUD where supported.

## Details information
Key Information always left. Middle Reception only when reliable data exists. Right is Technical for local movie/episode, Library for local TV overview, Streaming for streaming-only. Empty panels omit/reflow.
Key Information can include year/dates/runtime/age/genres/studio/network/distributor/country/budget/box office/collection/filming locations/original title/tagline as applicable.
Reception: awards summary, monochrome critic/audience ratings/counts and optional reliable quote.
Technical: resolution/codec/HDR/fps; audio format/channels/rate; container/size; subtitles; source; human-friendly path.
TV Library: season/episode availability, specials, library size/average, technical counts, storage locations.
Streaming: enabled/available providers, region and only reliable quality/HDR/audio/subtitle/expiry data. No “last checked” clutter.

## Cast & Crew
Separate stacked Cast and Crew rows. Current baseline uses square/rounded portraits (not circles), portrait + name + role as one focus unit, whole boundary/glow, complete cards in viewport and subtle edge fade. Principal crew only where appropriate.
**APPROVED CAST & CREW CORRECTIVE DESIGN (user reconfirmation 9 October 2026):** Replace the older portrait-card Cast & Crew presentation in the lower Details information area with the user-supplied compact table mock-up: two balanced columns, **CAST** on left and **CREW** on right, text-only rows separated by restrained thin horizontal rules, with names/roles and crew jobs/people aligned cleanly. **Remove numeric counts** from both headings (no `CAST (8)` / `CREW (8)`). Use **equal, consistent row spacing** within both sections and balanced vertical alignment; do not invent portraits or change the approved overall geometry. The supplied chat image is the authoritative visual evidence; its binary is not yet in GitHub. The mock-up's illustrative film metadata and reception values are not approved as real app data. Confirm responsive content and D-pad focus on Shield during APK walkthrough; this is implementation QA, **not an outstanding design decision**.
Person pages/discovery are future work, not current scope.

## Streaming-only Details
Use the same Details quality/component as local. Where a provider/platform genuinely supports title/episode deep links, open that title; otherwise use safe fallback/logging. Never invent unsupported provider contracts.

## Exact return focus
Returning to Movies/TV must restore the originating item and library state. Nested Details content should likewise restore the originating control where still valid.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-022–031; `docs/archive/root-history/NOVA_CODEX_RETURN_HANDOVER_4.1.2.md`; current Shield QA.

## Historical approved visual references recovered from Project Library
A legacy `NOVA_DESIGN_REFERENCES` record dated 16 September 2026 was recovered during the preservation pass. It is historical evidence; later written 4.1.7 decisions override conflicts. It records these mock-up identifiers so the visual lineage is not lost:
- List View + Genre/Sort/Order: `bb6ed615-ca35-4578-88c5-5855ad766c4d`
- Broad UI states: `9f0856cb-2d33-49af-999d-734853aea861`
- Primary Details: `db06b3cb-2136-4b1c-a1b9-69b01db3b80c`
- Network compact Option 3: `bb06fd93-a13b-49b3-8525-97eadce38c3b`
- Network no-divider/cyan-nav: `063d1a2d-4380-4db3-8251-6025c32bfb8a`
- Custom red-ribbon concept: `8703e387-1c95-4752-a284-7f988338f811`
- Playback current/end-time HUD: `7435ef99-a0f1-443d-b416-e2c44364e07c`
- Playback tighter-bottom: `4287b280-80d5-4023-b6f1-e4b47b57cf29`
- Playback compact controls: `b483b883-3a0e-4042-8525-9d7cb4778d26`
- Seeking thumbnail: `f36ed05a-13e5-4183-bf3d-e4d898240cdb`

The same historical record explicitly rejected permanent left-sidebar primary navigation, two-line grid titles, empty Continue Watching placeholder panels, huge bright-blue list rows, grey stock Android filter/sort/order dialogs, “NEW” Settings badges, invented analytics settings, an assistant-invented launcher icon, and generated Network/Settings mock-ups that invented unsupported features.

**Asset status:** the identifier/provenance record is recovered; the corresponding original generated image bytes are not currently available as a complete recoverable image set. Identifiers are not substitutes for images. If image bytes are recovered later they should be committed under `docs/design/assets/` with a manifest and status.

**Approved Cast & Crew mock-up binary destination:** `docs/design/approved-mockups/details/cast-crew-compact-approved.png` on `main` (pending image upload; do not claim file exists yet). This is the approved text-table reference with heading counts removed and equal spacing mandated in the written spec.
