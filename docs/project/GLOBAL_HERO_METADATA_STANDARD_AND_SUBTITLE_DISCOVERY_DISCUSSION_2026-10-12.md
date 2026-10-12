# Supernova global hero metadata standard and subtitle discovery — 2026-10-12

**Status:** User approves the proposed design recommendations unless stated otherwise; subtitle import/detection implementation is **not verified**, and no code changes are authorised.

## Global presentation standard (approved direction)

Apply a consistent metadata design language across Home hero cards and movie, TV-show, season and episode details, with layout flexibility and title-type-specific truthfulness.

- Primary order: **age classification → release year → genres → TMDB rating → vote count**. Age classification is a small monochrome outlined badge at the beginning.
- Use **middle-dot separators** between present primary fields; no extra dash/vertical divider. Do not use a star by default. If later introduced, it must be monochrome.
- Label the rating source and scale, e.g. `TMDB 8.1/10`; display vote count, e.g. `8.2k votes`, in **muted secondary text**, not equal emphasis to the rating. Verify actual rating/vote source and available data before implementation; do not misattribute show scores to individual episodes.
- **Dynamic compaction is mandatory:** missing fields have **no reserved space**, and subsequent available fields close up in order. Generate separators only between actually rendered fields; no leading/trailing/doubled dots. Example with missing year: classification · genres · TMDB rating · votes. Vote count should not appear orphaned without its rating. If a row has no data, remove the row and collapse its space.
- Technical badges form a **separate, monochrome, thin-outline row**, in order: **resolution → HDR → audio format/channels → subtitles/captions → audio description**. Render only verified capabilities and compact away missing badges/rows, with no blank slots.
- Candidate technical labels: SD/HD/FHD/4K UHD; HDR10/HDR10+/Dolby Vision/HLG; stereo/5.1/7.1/Dolby Atmos/DTS:X; subtitles vs verified CC/SDH; verified AD. Exact extractability and label choices require source audit. Do not claim entire TV series has an episode-specific file capability.
- Prefer no more than ~4–5 badges on Home hero, with more complete data on details pages; prioritisation/overflow remains a design consideration.
- Do **not** show season counts or runtimes on Home hero; keep such details on appropriate details pages.

All numeric examples are placeholders, not verified title data. Classification provenance/locale and rating source must be verified.

## User question: subtitles and captions during import

User asks whether Supernova currently fetches/detects subtitles and closed captions during media identification and metadata retrieval. **Not yet established from source**. Distinguish:
1. TMDB-style descriptive metadata lookup;
2. local/remote media-file track inspection for embedded subtitles, language, CC/SDH/AD flags, and sidecar subtitle files;
3. optional online subtitle-provider lookup/download for user's selected language.

**Proposed desired behaviour, subject to feasibility/approval:** discover embedded and sidecar subtitle tracks at import or via asynchronous background analysis; record verified availability/languages; honour configured preferred subtitle language with sensible fallback; optionally look up matching online subtitles in preferred language; make downloads configurable rather than assume unconditional bulk download; do not block scans on inaccessible network files; never equate generic subtitles with CC/SDH. Audit current implementation before promising or assigning fixes. No subtitle fetch implementation has been approved.

## Playback HUD reminder

**User specifically requests a reminder when discussion returns to the playback HUD:** user has something to raise regarding subtitles. Ask the user about that subtitle issue/topic at the start of the next playback HUD discussion; do not guess what it is. This is a contextual project note, **not** a timed notification.

## Workflow and scope

Documentation-only GitHub branch; do not touch active development branch, start Codex implementation or broaden the next release without explicit user authorisation.

## Global implementation contract — refined 2026-10-12

User explicitly confirms the **primary metadata row and technical-capability badges are a global Supernova design rule**, not merely a Home hero change. The same shared component/tokens/formatting/ordering/compaction rules should be used anywhere equivalent metadata appears (Home hero, movie details, TV-show details, season details, episode details, and any other applicable screen). **Do not implement visually similar but independent screen-specific copies.** A change to shared metadata presentation should propagate to every consumer; screens may select context-appropriate fields and layouts. Before implementation, audit actual existing detail-page components, data sources and constraints; do not claim a global implementation already exists.

**Additional content-specific fields required before final implementation sign-off:**
- Movie details: runtime, e.g. `2h 14m`.
- TV-show details: number of seasons and episodes, e.g. `3 seasons · 24 episodes`, if counts are accurate and their scope (library vs catalogue) is explicitly defined.
- Season details: episode count for that season.
- Episode details: runtime, e.g. `48m`; episode-specific review data only if actually available and correctly attributed.
- Home hero: **retain prior decision to omit season/episode counts and runtime** to avoid clutter. The shared component must support conditional fields rather than mandate all fields everywhere.

Proposed detail-page order, to confirm when implementing: **age classification → release year → genres (where applicable) → content-specific count/runtime → TMDB score → muted vote count**. All missing fields dynamically collapse with their separators. Episode-level classification/year/genre/rating should not silently inherit misleading show-level values; verify source and attribution. Technical capabilities are a separate shared row and must describe verified playable media, not unsupported season/show-wide assumptions.

**Documentation reconciliation:** Earlier hero QA exploration document has an outdated provisional metadata order and says decisions were pending; this global standard supersedes those older provisional notes. This is a documented approved *design direction*, not evidence that source code has already been updated or that technical detection is implemented. No Codex implementation handover has been authorised.

## User clarification — global consistency, flexible per-screen content

The global standard governs **shared visual/formatting rules**, not a requirement to display identical fields or identical rows on every screen. Show only contextually relevant, accurately sourced information. In particular: do not display season/episode counts on movie pages; do not display runtime on show or season overviews; do display runtime on movie and individual episode details. Home hero stays intentionally more compact. The user expects the changes to be visibly reflected in **both hero cards and detail pages** once the shared standard is implemented, including age badge, legibility/typography, separators, TMDB/votes hierarchy and technical badge styling where supported. Validate all applicable screens during QA; do not consider hero-only implementation sufficient.

**Capitalisation requirement:** Season/Seasons/Episode/Episodes are title-like count labels and must have initial capitals wherever displayed, e.g. `1 Season`, `3 Seasons`, `1 Episode`, `24 Episodes`. Apply correct singular/plural forms. This supersedes lowercase count examples elsewhere in this document.
