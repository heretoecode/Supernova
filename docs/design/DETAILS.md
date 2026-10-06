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
