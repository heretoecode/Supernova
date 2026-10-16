# Supernova — Details tab full forensic source and UI reconstruction audit
Date: 2026-10-13
Status: **Owner-authorised read-only investigation and documentation ONLY**. Not an implementation task, not a mockup approval, not permission to modify app code or start a build.
Repository: heretoecode/Supernova

## Purpose and source of truth
Produce an exhaustive, source-evidenced description of the **currently implemented Details tab** in signed Supernova 0.138 (identify exact matching source SHA; distinguish if repository source has advanced). The aim is for the design discussion to reproduce, to a high degree of fidelity, **what is currently displayed, where, why, with what data, and under what conditions**, rather than guessing from one photo. Reuse the completed Codex UI Atlas and comprehensive maintenance investigation; perform targeted additional source tracing wherever the existing mapping is insufficient. Clearly label source-verified, inferred, owner-observed and unknown facts.

### Read these existing documents first
- `docs/project-decisions/2026-10-13-supernova-0139-consolidated-handover-scope.md` on `docs/project-decisions-2026-10-12` (especially latest Details interaction contract).
- `docs/project-decisions/2026-10-13-preimplementation-decision-gap-review.md` and `docs/project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md` on the same branch.
- `docs/project-decisions/2026-10-12-supernova-0138-shield-qa-and-library-foundation.md` (chronological decisions; later overrides supersede earlier suggestions).
- Existing UI reference atlas (locate authoritative UI Atlas docs and screen identifiers) and full maintenance investigation on `docs/maintenance-investigation-2026-10`; locate relevant source maps for Details/movie/TV/episodes, metadata, technical file information, navigation/focus, rendering, artwork, storage, and theme.
- Consult other relevant repository docs and tests; report exact paths/SHAs. Do not assume historical Google Docs, screenshots or private files are present in repo.

## Owner-supplied photographic observation (not source proof)
Genuine photograph of NVIDIA Shield TV, `IMG_4941.jpeg`, provided in conversation on 2026-10-13. **Not committed to GitHub; do not claim access to image bytes unless separately supplied.** Owner can attach it to Codex; textual transcription follows.

Visible current screen: top navigation SUPER­NOVA / Home / Movies / TV Shows / Documentaries; Dune: Part Two context; tab headings Details (selected/underlined), Extras, More Like This. Below are side-by-side Cast, Crew, Key Information, Reception and Technical Information; blue-themed translucent/bordered panels for latter three. Text is small and many values ellipsized; lower viewport largely empty. Bottom right scanning-local-library indicator. Photo perspective/distortion and scan state mean **do not treat photographic pixel geometry as definitive layout dimensions**. Movie screenshot cannot prove TV-show Details behaviour.

Example visible labels/values (transcribe from source or supplied photo only; values illustrative, do not hardcode as constants):
- Cast (name + character, truncated), Crew (name + role, truncated).
- Key Information: Year, Runtime, Age rating, Genres, Studio / Network, Release / first air, Original title, Countries, Tagline, Collection, Budget (TMDB), Box office (TMDB).
- Reception: TMDB Rating, TMDB Votes.
- Technical Information: Resolution, Audio, Source, File size, Selected file, Location, Container, Video bitrate, Frame rate, Audio tracks.
- Some visible example values: 2024; 2h 45m; 12A; 1920 x 802; AAC · Stereo; Local storage; 3.0 GB; MP4; 2249 kb/s; 23.976 fps. **Verify exact formatting and conditional presence from source.**
- Synopsis is not visibly present in this photographed Details content region; prior owner decision requires full synopsis be available in Details tab. Determine if missing, conditional, located elsewhere or obscured, and distinguish current from planned behaviour.
- This is a current photo, not an approved redesign reference.

## Audit questions — exhaustive source-to-screen reconstruction
1. **Screen entry / navigation tree:** Identify activities/fragments/views/Compose or XML layouts, adapters, view models, navigation events and screen variants for movie, TV show, season/episode, local/network, multiple file editions. Exact UI Atlas screen IDs. Tab order/visibility conditions, tab underline/divider/focus states and routes from hero, Library, Details card, Back, Up/Down/Left/Right/OK. Trace current implementation, not desired behaviour.
2. **Visual geometry and rendering:** All layout resource IDs and programmatic layouts; parent bounds, dp/sp dimensions, paddings/margins, column weights, alignment, panel borders/radii/opacity/colours, typography/font, truncation/ellipsize/maxLines, responsive/safe-area rules, scroll containers, overflows, background/backdrop and scan overlay. Provide a **text-based annotated 1920x1080 reconstruction diagram**, and optionally a source-derived SVG/HTML/static mock layout only if clearly labelled approximate and without editing app source. Differentiate measured source constants from inferred geometry.
3. **Complete field inventory:** For every section and each displayed label/value, record: display label and localisation key, source model and source method, value format, null/empty fallback, hide/show rules, sorting/order, limits, truncation, file/provider dependence, and clickable/focusable status. Include Cast, Crew, Key Information, Reception, Technical Information, synopsis and any unseen fields. Enumerate all metadata fields supported but currently omitted from Details. No fabricated fields.
4. **Cast and crew:** Source ordering, filtering, max counts (owner wants up to eight each), names/character/role formatting, duplicates, long strings, absent values, portrait imagery (if any), how source updates propagate.
5. **Key info / reception:** Movie vs TV-specific labels and differences, title/year/genres/runtime/certification/network/studio/date/original title/countries/tagline/collection/budget/box office/TMDB rating/votes; distinguish existing visible values from proposed hero/card fields.
6. **Technical information:** Which selected file is represented for multi-file titles, local vs SMB/NFS/UPnP/remote media, how resolution/audio/bitrate/frame rate/container/file size/path are obtained, unknown/unsupported values, multiple audio tracks and subtitle information; potential privacy of paths. No playback tests required.
7. **Content lifecycle:** Data fetch/cache/indexing, scraper/provider sources, DB entities and update callbacks, scan indicator overlap and recomposition, asynchronous placeholder changes, error/empty states, whether displayed info is stale during scan.
8. **Interaction reality versus owner target:** Verify whether existing Details content scrolls or accepts focus, and whether D-pad goes into its children; provide precise navigation graph. Separately record **owner target**: one fixed non-scrollable viewport; no focusable content; focus stays on Details tab title; Left/Right between tab titles, Up to Details card/actions, Down stays on title, OK no content action. Do not misrepresent target as implemented.
9. **Preservation and feasibility:** Inventory all existing useful information that a redesign must not silently delete. Quantify content-height/width pressures, long filenames/paths, 8 cast/8 crew and full synopsis in one viewport. Assess realistic readability at TV distance without inventing owner-approved hiding, tiny text, scrolling or truncation policy. Identify unavoidable trade-offs requiring owner decisions.
10. **Existing test coverage and source quality:** Tests for conditional fields, tab focus, data formats, empty states, truncation, TV vs movie and multi-file. Propose non-device acceptance matrix and missing tests; no APK or physical Shield testing.

## Deliverables — isolated documentation-only branch
Create one or more detailed audit reports, with:
- Exact inspected branch, source SHA and reference baseline; link to reused UI Atlas/maintenance findings.
- **Source inventory/map**: paths, classes, layouts, resource IDs, field-to-method trace and line anchors.
- **Exhaustive current field/visibility matrix** for movie and TV; distinct current versus planned.
- **Layout geometry/style specification** and labelled reconstruction diagram sufficient for an independent designer to recreate the existing Details tab without inventing fields.
- **Current D-pad/focus/scroll state machine** versus approved owner target.
- **Source-derived variation/edge-case matrix** (missing metadata, long text, remote sources, scanning, multiple files, localisation).
- **Gaps and owner decision register**, especially no-scroll single viewport vs full synopsis and all metadata, without choosing policy silently.
- **Reproducible evidence ledger**: source paths and line anchors, direct code evidence, explicit unknowns, no false screenshot or device claims.
- Suggested follow-on design brief but **no new UI mockup presented as owner-approved**.

## Constraints
Read-only audit; commit **docs only** on an isolated new branch; do not modify app source, tests, build/signing, app assets, active development branch, release scope or existing UI Atlas history. No merge, APK, source implementation, credential/private media path publication, or fabricated current behaviour. Do not interrupt or overwrite ongoing backup investigation. If simultaneous tasks conflict, keep them isolated. Owner photograph is available in conversation but not repository; request attachment only if genuinely needed for visual alignment. Return report paths/URLs, exact source SHA, concise findings, and unresolved questions for owner.

## 2026-10-13 — Owner-approved connected tabs and frame margins
See `docs/project-decisions/2026-10-13-details-connected-tabs-panel-design-decision.md` for the authoritative design record. Replace the selected-tab underline with a continuous raised active-tab cap connected to the shared content frame; panel stable across tabs; **4% usable viewport width on each side and 4% usable viewport height at bottom**, not edge-to-edge and not the earlier 5%/90% suggestions. Top begins beneath Details card. Details content remains passive/no-scroll/no-focus; interactive-tab selected/focused distinction requires further design. Audit should map existing source geometry against this *target*, not claim it is already implemented. No source coding authorised.
