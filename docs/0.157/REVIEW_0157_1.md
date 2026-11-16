# 0.157.1 on the Shield: the owner's review (started 16 Nov 2026)

The owner installed 0.157.1 (code 1571): "a lot better than it was; not perfect". Reviewed one redesigned area at a
time (Files, then Details, Player, shared pieces). Numbered F1… for Files. "Decided" names where it was already
settled; "To decide" waits for the owner. Photos are taken at an angle: alignment is checked in renders.

## Files

### Working
- Plain charcoal page; the blue background and glass panel are gone.
- Moving along the tabs switches the page straight away.
- Library files show their artwork in the folder grid, with the In library pill.
- Opening a drive from This Device keeps focus where it should be.

### Problems (already decided: fix to match)
- **F1. Up from a tab's content can't reach the top bar** from the tab row; only Back does. RULES §3/§4.
- **F2. Files reopens on the last tab used.** Decided v12 (6 Nov): Files always opens on the first tab.
- **F3. The old panel's frame is still there, invisible:** the right of the tab row (facts and the scan button) is
  cut off where the panel edge was, and focused folder cards are clipped at the same edge. Content must use the full
  page width (RULES §1).
- **F4. Scroll bar sits on the content** (the old panel's edge), covering the right of the rows. See F15.
- **F5. No List view, and grid is the default.** Approved: list is the default (`network-files-tabs/02`) with a
  Grid / List button that shows the other view (`03`, `32`). QUESTIONS #14.
- **F6. Sort is a toggle** (Name ↔ Newest). Approved: a Sort menu with its choices and mirrored order icons
  (`network-files-tabs/11`; v10).
- **F7. No actions on a file or folder in the grid:** long-press does nothing and release plays; nothing adds a
  folder. Approved: the focused list row shows its buttons (Play · Details · Mark as watched · Exclude, `02`) and the
  Menu button opens the same actions (`28`). See F16.
- **F8. Folder cards show a plain blue-to-black gradient.** Approved: a mosaic of the artwork inside, or a plain card
  when there is none (v4, `32`).
- **F9. Focus jumps to Home in the top bar** every time a folder opens or Back goes up a level, while browsing
  inside a drive (not on This Device's list). The page loads, the Files underline stays, but the white box lands on
  Home; Down brings it back. RULES §3: focus never jumps.
- **F10. Two crashes, at about 23:09 and 23:14 local time,** both pressing Back while focus had jumped to Home (F9).
  Needs the diagnostics report from the Shield.
- **F11. Back after playing a file returns to This Device's list,** not the folder it was played from. Changing tab
  and back also loses the folder.
- **F12. Re-choosing the open tab doesn't reset it:** after F9, choosing This Device still showed "This folder is
  empty"; Back then walked the breadcrumb.
- **F13. Grey text on the white focus box** (the "This device" row). REVIEW_0157 #20 was marked fixed; it isn't on
  the device.
- **F14. The internal storage row has no storage bar,** while the USB drives do (two lines against three). Every
  storage row gets the bar (consistency).

### To decide (Claude's recommendation in brackets)
- **F15. Scroll bar:** none at all? It can't be used, only read. [None. As on Home rows, the next row peeking at the
  bottom edge shows there is more.]
- **F16. Long-press:** should a long-press of OK open the row's actions menu, as the Menu button does, everywhere
  (Files grid and list, libraries)? [Yes, one gesture app-wide. Release without holding still plays.]
- **F17. Up always goes to the open tab:** from anywhere in a tab's content, Up into the tab row lands on the open
  tab, not the tab above the focus. [Yes, as a rule for every tab row, Details included.]
- **F18. Tab-row facts removed:** the summary on the right ("1,934 Videos · 3 To Check · …") repeats the Library Care
  badge, Library Health and Your Library, and won't fit as tabs grow. Owner leans to removing it on every tab; the
  badge stays. Replaces RULES §7b "Tab row facts" and v3/v6/v7. Where does the scan button go? [Remove the facts;
  scanning lives in Library Care's Scan now row and the top-bar ring.]
- **F19. "This device" row renamed "Internal storage"** (the tab is already This Device). [Yes.]

### Confirmed
- The breadcrumb ("Backup 1 › … › Movies") is a signpost, not focusable; Back goes up one level (v6, 6 Nov).
