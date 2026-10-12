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
