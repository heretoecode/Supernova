# Movies & TV Library Controls Audit

Status: **COMPLETED**
Preserved: 7 October 2026
Purpose: preserve the audit evidence and resulting product decisions behind the next-version Movies/TV controls work.

## Scope inspected

The audit reviewed the Movies and TV library toolbar, filter state, sort/order behaviour, Grid/List switching, configurable List columns, Unmatched handling and D-pad interaction.

## Findings

The current toolbar exposes six logical controls:
- Filters
- Sort
- Order
- Unmatched
- Grid/List
- Columns

Underlying library state includes sort field, genre selection, year selection, streaming/provider selection, selected years, unmatched state, ascending/descending state, list mode and column configuration.

Available/observed sorting includes Date Added, Title and Release Date. Trakt Trending exists as a concept but is unavailable where its required data is not available.

Filters include:
- Genre
- Year
- local-library Streaming Service/provider availability where known

Year filtering already has a good property worth preserving: it shows only years represented by the user's current library.

List mode has configurable columns. Sixteen underlying columns were identified:
1. Title
2. Year
3. Seasons
4. Episodes
5. Runtime
6. Resolution
7. HDR
8. Audio
9. File Size
10. Average Episode Size
11. Date Added
12. Modified
13. Codec
14. Bitrate
15. Container
16. Path

Title cannot be hidden. Columns can be shown/hidden/reordered.

Observed defaults:
- Movies: Title · Year · Runtime · Resolution · Audio · File Size · Date Added
- TV: Title · Seasons · Episodes · Runtime · Resolution · File Size · Average Episode Size · Date Added

List column headings can also be sortable, creating some functional duplication with toolbar Sort/Order. This is acceptable; the headings can remain a shortcut while toolbar Sort and Order stay present for consistency between Grid and List.

Technical columns such as Path, Container, Bitrate and Modified are useful and must not simply be deleted. They may later benefit from an Advanced grouping, but that is not required by this pass.

Existing D-pad handling and automated tests cover toolbar navigation, Down into List columns and the final Columns right boundary. Physical Shield QA nevertheless found that the toolbar sits too high and row-centering movement can jump/snaps badly. Automated focus tests therefore do not prove the visual/physical requirement.

## Product conclusions / approved decisions

### Toolbar order
Grid:
**Grid/List · Filters · Sort · Order · Unmatched (only when present)**

List:
**Grid/List · Filters · Sort · Order · Columns · Unmatched (only when present)**

- Grid/List is leftmost.
- Sort and Order remain separate in both modes.
- Columns appears in List only where applicable.
- Unmatched is always rightmost and is hidden completely when there is no unmatched media.
- Order always has an explicit label such as **Order: Newest First**, not only an icon/value.
- The toolbar is moved down so the controls appear deliberately written/sitting on the horizontal divider rather than floating above it.

### Live filters
The existing select → tick → Done → apply interaction is unnecessarily indirect.

Approved rule:
> **Selections are live. Ticking/unticking is the action. No Done confirmation. Back only navigates out.**

Genre:
- All Genres at top.
- available genres below.
- remove Done.
- toggling immediately updates the library behind the menu.
- All Genres clears individual genre selections and immediately restores the all-genre view.

Year:
- continue showing only years present in the current library.
- toggling immediately applies.
- keep Clear Selection.
- remove Done.

Streaming/provider:
- toggling immediately applies.
- remove Done.

### Compact filter summary
Do not allow selected values to create an indefinitely expanding toolbar label:
- none: **Filters**
- one: **Filters: Crime** (or equivalent concise value)
- multiple: **Filters (N)**

Ticks in the submenu remain the authoritative detail.

### Vertical navigation
Keep the active row comfortably/approximately centred where possible, but remove large snapping/jumping. Movement must be smooth and deterministic. Exact focus restoration takes precedence over forced centring.

## Risks / validation

- Validate on physical Shield with large libraries and both Grid/List modes.
- Validate filter changes while the submenu remains open.
- Validate zero/one/many unmatched titles.
- Validate wide combinations of visible List columns.
- Do not claim conformance solely because existing toolbar unit tests pass.

## Authority

The resulting approved behaviour is incorporated into `docs/design/MOVIES_TV.md` and `docs/qa/next-version-authority.md`.
