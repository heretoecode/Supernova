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
**CURRENT SHIELD CORRECTION:** Cast & Crew layout has a corrective redesign pending; exact final visual is **NEEDS DECISION**. Do not invent a new geometry during the fixes pass.
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


## Next-version Details amendments — APPROVED / SUPERSEDING

This section supersedes the earlier compact Information and portrait-based Cast/Crew corrective direction where they conflict.

### Hero and actions
- Reduce real loading latency for official logo/title artwork and lower content such as Extras/More Like This; do not merely hide the delay.
- Put genre on the same hero metadata line as year/runtime/rating or season/episode information.
- A streaming/provider action between Play/Resume and More must be D-pad focusable and match neighbouring action height, border, alignment and geometry.

### Information layout
Tier 1 is **Key Information | Technical Information | Reception**: borderless aligned columns, breathing room below navigation, subtle vertical separators, no heading icons. Reception may contain reliable awards/ratings and one short review excerpt where available and appropriate.

Tier 2 sits below a subtle horizontal divider: **Cast | Crew**, two equal-width columns with one subtle vertical divider. Cast aligns with the Key Information left edge; heading baseline and first-row spacing match; no individual row separators.

Cast/Crew: up to **8 Cast + 8 Crew**, text only; no portraits/images/placeholders/artwork requests; no displayed counts; no More Cast/More Crew; no carousel/arrows. Cast person is bright/bold white with character softer; Crew person bright/bold white with job softer; prioritise key creative roles.

### Extras
Each logical category (Trailers, Teasers, Featurettes, Clips, etc.) is exactly one horizontal carousel row and never wraps. Fix card/title bounds so names are not incorrectly clipped.

Trailer playback uses a clean centred 16:9 video at roughly 60% of screen area over the still-visible Details page, strongly blurred/dimmed including header. No Supernova surrounding container, title, Open YouTube, Close control or full playback HUD. Respect mandatory provider/YouTube UI. Back and natural completion restore exact launching trailer-card focus. Simple OK/Play pause is optional only where technically appropriate. A quick ~150–200ms blur/dim transition is acceptable.

Seasons/Episodes and other horizontal Details rows follow the shared carousel rule: one logical row, deliberate partial continuation, no accidental clipping or wrapping.

## APPROVED — single-screen, non-interactive Details tab information layout (8 October 2026)

**Authority:** This explicit user approval supersedes conflicting earlier Details-information and Cast/Crew presentation instructions above. It concerns **only the content inside the Details tab**; it does not approve or alter the hero, artwork, media metadata/actions, lower-tab labels, tab position, other tabs, or global navigation. The isolated 16:9 three-over-two-column mock-up was approved with the refinements below; do not treat invented example metadata as source requirements.

- **No nested Details-tab scrolling.** Upon navigating down from the existing hero to the Details tab, show the complete information layout at once within the available TV viewport. The user can press **UP** to return to the upper Details/hero area, or **RIGHT** to move to Extras (or the next available tab). Keep the Details tab/navigation target focused; **no focusable/selectable metadata, Cast or Crew rows, no field focus glow or highlight**. Do not break the existing hero-collapse or tab-navigation model.
- **Tier 1:** three aligned columns, in this exact left-to-right order: **KEY INFORMATION | TECHNICAL INFORMATION | RECEPTION**. All three use the same header style, consistent label/value alignment and subtle horizontal dividers beneath individual information rows, as in the approved isolated mock-up. Subtle vertical separators between columns; no bulky cards, boxed panels, icons or gratuitous chrome. Reception uses **TMDb rating and votes only** as approved rating source; IMDb and new Trakt ratings/reviews are excluded. Awards/editorial material only if independently authorised, reliably sourced and approved; mock-up example values are not approval of a new source. Keep agreed fixed factual slots and Unknown for unavailable factual data where applicable, omit unavailable optional review.
- **Tier 2:** one full-width subtle horizontal rule beneath Tier 1, then **CAST | CREW** as equal-width side-by-side columns with aligned headings, matching start positions and a subtle vertical separation. Headings are exactly **CAST** and **CREW**, **no counts or brackets**. Up to eight entries per column, **text only**; no portraits, cards, carousel, arrows, links or extra actions. Cast names and characters, and Crew roles and names, displayed in consistent readable pairs.
- **Spacing refinement explicitly approved:** slightly increase the vertical spacing between Cast entries and Crew entries compared with the initial isolated mock-up; enforce **identical row heights, padding, divider spacing and baseline rhythm across both columns**, including when labels wrap. Thin, unobtrusive horizontal separators between Cast and Crew entries are **approved**, superseding the earlier instruction of no individual row separators. Keep all eight rows within one TV viewport by designing responsive typography and spacing rather than adding scrolling or hiding data.
- Preserve the existing Supernova dark visual style, restrained cyan headings, white primary text and softer secondary text. No unnecessary section backgrounds. Design for readable 16:9 Nvidia Shield presentation and overscan-safe margins; verify on the actual Shield that the whole tab is visible without internal scrolling, focus stays on tab navigation and UP/RIGHT/Back transitions behave correctly.
- This is **approved design documentation**, not an instruction to start implementation in the current **fixes-only** corrective release. Do not modify production code or merge branches without the separate implementation handover.

**Visual authority:** the most recent isolated mock-up depicting only the three top columns and two bottom text-only columns, plus the user's subsequent corrections removing “(8)” and equalising/increasing Cast/Crew row spacing. Its placeholder movie-specific values, unsupported review/awards, file-source guesses and other illustrative text are **not** new provider or data-source approvals.
