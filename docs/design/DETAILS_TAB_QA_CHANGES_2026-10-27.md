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

## Additional user-reported issue: Details metadata and artwork loading instability (video evidence)
Source: user-supplied 6.57-second Shield TV recording, `IMG_5035.mp4`, showing opening the Details page for *Mortal Kombat II* from the Home hero. **Pending source investigation and physical verification; no root cause established.**

### Observed in the supplied recording
- At opening, the Home hero has title, artwork and compact metadata.
- After navigating to Details, a largely plain blue hero/background is initially displayed while the title, synopsis, controls and compact metadata are visible.
- Artwork fills in several seconds later, producing a conspicuous late visual change.
- The compact metadata line also changes/reflows during this transition. The video does **not** establish whether this is delayed data, a placeholder replacement, a source merge or multiple asynchronous renders; investigate before attributing a cause.
- User additionally reports metadata appearing/disappearing or popping in/out; treat this as a regression symptom to reproduce, not proof of a specific underlying defect.

### Required investigation and outcome
1. Trace navigation from Home hero to Details, the detail view model, artwork fetch/caching, metadata repository lookups and observer/render update sequence. Measure time to first useful Details paint, time to stable metadata and artwork readiness on Shield.
2. Check for duplicate refreshes, repeated network queries, mismatched item IDs, stale-state renders, transient empty values and unnecessary layout rebuilds. Inspect logs rather than guessing.
3. Ensure that metadata already available from the selected item is displayed immediately and **remains stable** while supplemental information loads. Do not blank out populated fields or overwrite reliable data with temporary placeholders.
4. Introduce appropriate stable loading/empty/error states for genuinely missing fields and artwork, with no disruptive layout jumping or repeated flicker. Reuse artwork cache and avoid blocking D-pad navigation; do not delay the whole page merely to wait for optional external data.
5. Verify navigation back and forth, first-open versus cached-open, slow/offline network, partial metadata, titles with/without backdrop, movie and TV series, and rapid switching between titles. Check for stale artwork/data from a previous title.
6. Test timing, UI stability, focus retention and scrolling on physical Nvidia Shield TV Pro; capture before/after recording and evidence. Avoid modifying unrelated Home hero behaviour.

### Acceptance criteria
- Previously known metadata does not disappear or oscillate during load.
- Details layout and focus remain stable; no conspicuous text jumping or flashing as asynchronous fields resolve.
- Artwork resolves promptly when cached, and missing/late artwork does not cause repeated full-page reflow.
- Optional metadata failure cannot stall the screen or erase valid fields.
- Exact timing targets to be measured and recorded by Codex from device baseline rather than invented here.
