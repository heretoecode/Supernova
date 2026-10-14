# FD-007 — Library statistics/header redesign — owner-approved design and meaning

**Decision date:** 14 October 2026. **Status:** Visual design and intended metric definitions approved; awaiting source-backed Codex feasibility/implementation planning. **Not included in active 0.139, nor automatically scheduled for 0.140.** Supersedes older parked FD-007 proposal that rejected horizontal cards. This is a single continuous full-width statistics panel, NOT four disconnected cards.

## Applies to
Globally on the **Movies, TV Shows, and third/custom Library page** (and equivalent library-page variants using this shared header). Data is scoped to the currently selected library page, not indiscriminately to all media. Preserve existing library filter/sort/view buttons and media poster/grid layout; remove the redundant page title above the stats. The selected top navigation tab already identifies the page.

## Placement and visual contract
- **Exactly matching the existing library content width** and existing left/right margins; do not create a new width system.
- **Equal 16px-equivalent reference gaps above and below the statistics panel at 1080p**: top gap measured from the bottom of the navigation area **including selected-tab underline** to the panel top; bottom gap from panel bottom to top of existing filter/sort buttons. Scale proportionally/responsively for 720p and 4K; 16px is a visual reference, not necessarily literal Android physical pixels. Codex chooses implementation units and verifies focus/zoom clearance, safe areas and clipping.
- Panel should sit high on page, with controls and posters moved upward correspondingly. **Do not add back a page heading**. Do not modify the established navigation, library controls or posters merely to fit the panel.
- One wide, restrained dark/translucent rounded Supernova-style surface with subtle outer border, one horizontal row divider, slim vertical section separators, monochrome iconography and text, consistent aligned numbers. No bright green success status. No animated scan-status icon. Maintain appropriate readable typography and padding.
- **Top row four areas:** Total Library (count), Total Size (media size), Local Storage (count + media size), Network / WebDAV (count + media size). Existing source totals are primarily a *visual re-presentation of currently shown data*; do not infer implementation is complete without checking actual source.
- **Bottom row five areas:** Unwatched (count + share of page library), In Progress (count + share), Added This Month (count + period), Last Updated (completed-scan date/time), Library Status (static icon and state). Align numeric baselines consistently, including values such as 528, 96 and 24.
- Do **not** show one device's drive capacity, a '2 TB drive' chip or aggregated physical-capacity bars. A library may span multiple internal/USB/network sources; such metrics belong per source in Network & Files when reliably known.

## Approved metric semantics; implementation must validate actual data model
1. **Unwatched:** never started, **no active resume state AND no recorded completed/watched state**. Merely lacking a resume position is insufficient to classify a completed title as unwatched. Count/percentage relative to titles on the selected page.
2. **In Progress:** started, active resume state, **not completed**. Count and percentage relative to titles on selected page. This is timeline progress, **not playback speed**. For TV Shows, a show-level count must correctly aggregate episode state and must not mark a series complete merely because one episode finished; verify semantics in source.
3. **Added This Month:** titles added from the **first day of the current calendar month** through now, based on a verified library-added timestamp; clarify if any ambiguity in source timestamp semantics.
4. **Last Updated:** date/time of the **most recent completed library scan**, not the current time, an in-progress scan start, or an unrelated metadata update. Source availability and local-time formatting to be verified.
5. **Library Status:** **'Library up to date' / 'No scan in progress'** when no scan is running and status supports that wording; while running, **'Scanning library' / 'Scan in progress'**, with a different **static monochrome** icon. No animation or spinner. If scan fails or no reliable status exists, Codex must not misrepresent it as successfully up to date; propose appropriate truthful fallback.

## Scope and evidence
- The owner explicitly approved the visual arrangement, placement and these metric meanings. Exact numerical values (e.g. 742 titles, 1.42 TB, 528, 96, 24) in images are **illustrative**, not live library counts.
- Several image-generator attempts labelled gaps '16px' while drawing visibly much larger gaps. **Those generated screenshots are not geometrically accurate implementation references.** The written measurable spacing contract above is authoritative. The final illustrated image assets are currently in conversation, **not committed to GitHub**.
- Codex should inspect existing library counts, watched/resume/completion history, scan lifecycle, added timestamps, custom-page filtering and responsive/focus behaviour. No owner decisions needed for normal engineering choices; surface only actual product ambiguities.
- This approval does **not** authorise modification of 0.139 or assume inclusion in the already-approved 0.140 feature scope.
