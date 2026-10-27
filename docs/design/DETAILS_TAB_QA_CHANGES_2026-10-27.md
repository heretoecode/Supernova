# Details tab — Shield QA change requests (2026-10-27)

Status: **user-requested; pending Codex implementation and Shield verification**. Source: user-supplied photograph of the current Details tab (TV series *Marshals*). This is **not** a Settings redesign request. Do not alter application code on this documentation branch.

## Requested changes
1. **Key Information → Language:** remove the globe icon immediately before the language value (shown as English). Retain the existing language label and value, with clean alignment after removing the icon. Do not remove other icons elsewhere without reason.
2. **Cast:** currently 8 people arranged 4 across × 2 rows in the screenshot. Increase the **vertical gap between row 1 and row 2**, while preserving four-across layout, card sizing, portrait alignment, legibility and existing horizontal spacing.
3. **Crew:** currently 8 people arranged 4 across × 2 rows in the screenshot. Increase the **vertical gap between row 1 and row 2** consistently with Cast. Keep four-across layout, portraits, names and role labels intact.
4. **Reception → Reviews:** ensure actual review content is available within Reception, not only aggregate ratings/scores (e.g., TMDb score, IMDb/Rotten Tomatoes/Metacritic). **Audit first** whether reviews already exist, how they are retrieved, availability, supported providers, loading/failure/empty states, focus/navigation and any provider/API limitations. Do not invent reviews, scores or a new integration. Where no reviews exist, provide an honest empty state rather than fabricated content. Any substantive provider/UX change requiring user preference should be presented for approval.

## Codex verification checklist
- Identify existing Details tab components, XML/layout code, language icon source and Cast/Crew row spacing styles. Make only targeted changes, preserving approved Details styling and existing page navigation.
- Verify Cast and Crew spacing with 8 people and fewer/more than 8; prevent clipping and overflow at Shield 1080p/4K scaling and text-size variants.
- Investigate Reception data model and review providers/handlers before deciding implementation. Distinguish *review text* from rating scores; test cases with reviews, no reviews, API errors and offline state.
- Validate D-pad focus, scroll positions, Back behaviour, text truncation and no regression to adjacent tabs (Extras, Seasons & Episodes, More Like This).
- Document tests, screenshots and exact changes in the eventual implementation handover/PR. No claim of completion until implemented and physically verified.

## Boundaries
This is a separate **Details tab** work item, not part of the Settings blueprint's 165-entry mapping. User has specified the changes; Codex handles technical decisions, escalating only real preference/approval questions. Keep active release branches untouched until implementation is authorised.
