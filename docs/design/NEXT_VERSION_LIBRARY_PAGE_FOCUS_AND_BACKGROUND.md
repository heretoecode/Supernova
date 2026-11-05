# Next version — library page focus and page-specific background artwork

**Status:** User-reported bugs, investigation and implementation pending. **Priority:** High. **Target:** next Supernova version after 0.143.

## User observations
- During navigation among Movies, TV Shows and a third custom library page (**Documentaries**), remote focus becomes difficult to locate, especially after opening Documentaries. Movies/TV Shows appear more stable.
- User supplied ~29-second navigation video `IMG_5053.mp4`. The visible symptom does not establish whether underlying Android focus is lost or the highlight becomes invisible.
- When entering **Documentaries**, the background appears to continue showing artwork from the **first movie on the Movies page**, rather than the first item on Documentaries.

## Expected behaviour
1. Each library page owns independent focus and background-artwork state; no stale image or focus carried from a different library.
2. On initial page entry, establish predictable visible focus and use the **first item in that page's current displayed order** as the initial background-artwork source, including custom pages such as Documentaries. On return, restore valid prior focus and show its matching artwork; if no prior focus exists, default to first item.
3. As focus moves to a different item, update background using that item's artwork in accordance with existing approved library behaviour. Ensure async loading results from the previous page cannot overwrite the current page background.
4. If page is empty or the current item has no appropriate artwork, use the approved neutral Supernova background; never show unrelated artwork from Movies or another page.
5. Preserve navigation/focus across fast page switches, page rebuilds, content scans, poster loading and empty states. Focus must remain visible, on-screen and associated with the active page.

## Investigation
Compare Movies, TV Shows and custom Documentaries page implementations, page identity keys, focus request/restore paths, background image selection and async artwork cancellation/caching. Determine whether the two symptoms share a state-ownership or lifecycle bug before choosing the fix; do not assume root cause.

## Acceptance tests
- Open Movies, TV Shows and Documentaries in sequence; initial background always belongs to active page.
- Return to each page and verify focus and artwork match the selected item; fast switch repeatedly without cross-page artwork bleed.
- Test first item missing artwork, empty page, loading delay, metadata refresh, source scan and dynamically changed first item.
- Test physical Shield D-pad navigation with video/screenshot comparisons, plus automated focus/state tests. Record confirmed behaviour separately from unverified causes.

## Workflow
Include in next-version Codex handover. Documentation only; no app code or main branch changes.
