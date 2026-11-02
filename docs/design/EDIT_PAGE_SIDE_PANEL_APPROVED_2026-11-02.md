# Edit Page (custom library) — side panel over the live page (APPROVED 2 November 2026)

**Status:** APPROVED by the owner; implementation authorised for 0.152.
**Supersedes:** the visual design of the four-section Edit Library editor and its Live Preview panel (draft blueprint of 30 Oct). **Features are unchanged** — see the inventory below; only presentation changes. Owner: "the live preview is the page itself."

**Approved image:** [edit-page-side-panel-approved-2026-11-02.png](approved-mockups/edit-page/edit-page-side-panel-approved-2026-11-02.png) (1920×1080; library page and posters behind are placeholders).

## Layout
- A **690px side panel, top-right**, over the user's real custom library page (top navigation stays). Solid panel; the page stays visible on the left two thirds.
- Header: **Edit Page** + live match count ("32 titles match · 119 GB"; amber "No titles match these settings" when empty). **Save Changes** (new page: **Save Page**) top-right.
- One list, no tabs, rows in the approved Playback Settings style (icon · label · value, straight separators, glowing focus outline):
  - **PAGE:** Page Name (keyboard) · Includes `Movies | TV Shows | Both`
  - **WHAT TO SHOW:** Genres (in-panel multi-pick list) · Match `Any | All` · Year (stepper) · Watched (stepper: All / Watched / Unwatched / In Progress) · Minimum Rating (stepper: Any, 5–9) · Original Language (in-panel pick list; Left/Right also steps) · Studio / Network (in-panel pick list; Left/Right also steps)
  - **HOW TO SHOW IT:** View `Grid | List` · Sort By `Title | Year | Date Added` · Order `Newest | Oldest` (title sort: `A–Z | Z–A`; persisted value remains ascending/descending)
- **Delete Page** at the bottom, red, separated, "Removes this page only. Your media is never deleted." Existing pages only; confirmation with safe default Cancel.

## Live behaviour
- Every change re-renders the real page behind immediately (filters, sort, view, content type).
- **New page:** "+" creates a provisional tab ("New Page", dimmed with a draft dot); the page shows "Choose what to show. Your page fills in here."; the Name row is focused; the tab name updates as the name is typed (or on Done if the keyboard covers the bar).
- **Back** with unsaved changes: Save / Discard / Keep editing. Discard restores the previous page exactly (or removes the provisional tab). Save validates name 1–40 chars.

## Feature inventory (unchanged from source)
Name; content type movies/tv/both; genres multi-select + all/any; single year or any; watched all/watched/unwatched/progress; minimum rating any/5–9; original language; studio/network; view grid/list; sort title/year/added; ascending/descending; delete (existing page); save validation and atomic write; preview cap behaviour no longer needed (the page itself shows results).
