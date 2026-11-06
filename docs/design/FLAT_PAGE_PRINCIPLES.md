# Flat-page design principles (owner, 6 Nov 2026)

Worked out on the Files redesign (formerly Network & Files; mock-ups `docs/design/proposals/network-files-tabs/`,
decisions in `docs/REVISIT_LATER.md`). The owner intends to redesign other pages on the same principles, Settings
first. Each page still gets its own mock-ups and approval; this file is the starting point, not a substitute.
Not tied to a version yet.

## Page
- Plain charcoal page `#16181C`, no Space Black artwork, no panels: content uses the full width (owner decision for
  Files, 6 Nov 2026; other pages decided one by one).
- Larger than panel-era pages: rows about 128 px tall at 1080p, names 36 px, second lines 27 px, buttons 64 px.

## Top of the page
- Sections are tabs in a tray (lighter strip with a faint edge) under the global top bar, with a line under the
  tab row. Open tab: grey chip; focused tab: white. The page opens on the first tab.
- The right of the tab row is a summary for the open tab, the same text size as the tabs, each fact with its own
  icon, words capitalised, a dot between facts. It never touches the tabs (it shrinks first).
- A count badge on a tab when something there needs a look.
- Breadcrumbs start below the tab and are signposts, not buttons; Back goes up one level.

## Rows
- One row style everywhere: icon, name, plain second line; long names are shortened with "…".
- Status pills in a fixed column between the names and the buttons: plain text in an outline, no colour, all the
  same width; they stay put when the row is focused.
- The focused row shows its own buttons (up to about five); the remote's Menu button opens the same actions as a menu.
- Choices with up to five options are inline chips on the row; on/off is a switch. Avoid click-throughs.
- Empty states say what is going on in plain words ("Everything looks fine"); rows are never just removed silently.

## Menus and panels
- Menus: an icon on every item, no two alike (opposites mirrored), the tick for the current choice on the right,
  only as wide as the longest item plus padding, and a faint 28% edge on the flat page. No heading for a single list.
- Anything that needs typing is a page with the global keyboard on it (fields left, keyboard right, help underneath),
  not a side panel.
- Instant actions with an Undo notice instead of "are you sure?" where the action can be undone.

## Top bar
- Places are words (Home · Movies · TV Shows · library pages · Files), tools are icons (search, settings); no dots.
- Background work (a scan) shows as a small ring on the left of "Files", the same size and weight as the icons,
  instead of a pill in the corner.
