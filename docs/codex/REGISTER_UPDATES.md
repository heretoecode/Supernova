# Future Development register — updates to apply (Part C of the overnight brief)

The register in `docs/Future Development/` still lists features as parked or awaiting scheduling that have
since been built. Apply exactly the moves below. **Do not rewrite, summarise or delete any existing text**;
only move files, add the one status line, and update the README tables.

## How to apply each row
1. `git mv` the file(s) to the destination folder (keep the file name).
2. Directly under the file's first heading, add one line:
   `> **Status (2026-11-04): BUILT** — <evidence>. Moved from "<old folder>". Original text below unchanged.`
   (For the FD-012 row use `CLOSED` instead of `BUILT`.)
3. In the README of the old folder, remove the item's row or line; in the README of the new folder, add a row
   with the ID, title and the same evidence. If a README has no table, add a short bullet instead.
4. Fix any relative links inside the moved file that now point to the wrong place (`../` depth only).

## Moves

| ID | Files (current folder → destination) | Evidence to quote |
|---|---|---|
| FD-001 | `03 Approved - Awaiting Scheduling/FD-001-age-rating-overlay.md` → `05 Completed and Verified/` | `player/PlaybackAgeOverlay.java` |
| FD-002 | `01 Under Discussion/FD-002-playback-hud-redesign.md`, `01 Under Discussion/FD-002-playback-hud-functionality-audit-2026-10-16.md`, `02 Parked Ideas/FD-002-playback-hud-redesign.md`, `02 Parked Ideas/FD-002.md` → `05 Completed and Verified/` | Playback HUD redesigned (0.141–0.152): `player/PlayerController.java`, `player/PlaybackSubtitlePanel.java`. If two files would get the same name in the destination, prefix the one from `01` with `under-discussion-` |
| FD-003 | `02 Parked Ideas/FD-003-preparing-playback-screen.md`, `02 Parked Ideas/FD-003.md` → `05 Completed and Verified/` | `player/PreviewPlaybackLoading.java` (owner-confirmed implemented, see Parked README) |
| FD-005 | `02 Parked Ideas/FD-005-network-files-visual-redesign.md`, `02 Parked Ideas/FD-005.md` → `05 Completed and Verified/` | Network & Files workspace (0.142/0.147): `leanback/filebrowsing/UniversalFileBrowser.java` |
| FD-006 | `02 Parked Ideas/FD-006-edit-library-opacity-readability.md`, `02 Parked Ideas/FD-006-settings-and-context-menus.md`, `02 Parked Ideas/FD-006.md` → `05 Completed and Verified/` | Settings rebuilt (0.143) `leanback/settings/SettingsWorkspace.java`; Edit Page side panel (0.152) `leanback/EditPagePanel.java`; global menu style (0.153) `leanback/SupernovaStyle.java` |
| FD-007 | `02 Parked Ideas/FD-007.md`, `03 Approved - Awaiting Scheduling/FD-007-library-statistics-header-approved-design.md` → `05 Completed and Verified/` | `leanback/LibraryStatistics.java` |
| FD-012 | `01 Under Discussion/FD-012.md` → `06 Rejected and Withdrawn/` | Closed and superseded (stated in the file itself) |
| FD-014 | `03 Approved - Awaiting Scheduling/FD-014-historical-review.md` → `05 Completed and Verified/` | Custom library pages: `leanback/PreviewPages.java` |
| FD-030 | `02 Parked Ideas/FD-030-seekbar-endtime.md` → `05 Completed and Verified/` | "Ends at" on the HUD (0.151): `player/PlayerController.java` (`preview_end_time`) |
| FD-031 | `03 Approved - Awaiting Scheduling/FD-031-about-settings.md` → `05 Completed and Verified/` | `foundation/FoundationAboutWorkspace.java` |
| FD-033 | all five `03 Approved - Awaiting Scheduling/FD-033-*` files → `05 Completed and Verified/` | Details redesign (0.140): `leanback/details/PreviewMoviePage.java` |

All evidence paths are under `src/main/java/com/archos/mediacenter/video/`.

## In `02 Parked Ideas/HISTORICAL_DISCUSSION_CATALOGUE.md` (stays where it is)
Change only the third column of these two rows, keeping the original text after the new prefix:
- FD-015 Library Health: prefix with `**BUILT** — leanback/LibraryHealth.java.`
- FD-022 Expanded ratings: prefix with `**PARTLY BUILT** — IMDb, Rotten Tomatoes and Metacritic via the owner's OMDb key (0.151).`

## Leave alone
FD-004 (Shield Home channels), FD-028 (content warnings), FD-032 (Drive publisher — status needs the owner),
`REPOSITORY_WIDE_RECONCILIATION_2026-10-13.md`, the `04` and `07` folders, and every other catalogue row.
