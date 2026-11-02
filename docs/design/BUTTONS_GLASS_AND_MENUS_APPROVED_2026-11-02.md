# Buttons, glass panels and menus — one style (APPROVED 2 November 2026)

**Status:** APPROVED by the owner. Target: 0.153.
**Applies to:** every button, panel, pop-up and context menu in the app.
**Supersedes:** the individual button/focus/panel styles of the HUD, library toolbars, home hero, Playback
Settings tabs and rows, Edit Page, and the context menus (home/library long-press, details-page More).
**Why:** owner photos (2 Nov 2026) showed four different button designs, five different panel fills, a tab
that shrank when selected, and a cyan row focus that hugged its text.

Mock-ups (1920×1080, rendered by [render.js](proposals/glass-and-buttons/render.js)):
[boxed buttons](proposals/glass-and-buttons/16-boxed-black-buttons.png) ·
[menu headings](proposals/glass-and-buttons/15-menu-heading-inline-line.png) ·
[menu without title, option A](proposals/glass-and-buttons/17-menu-title-options.png) ·
[fitted menu width](proposals/glass-and-buttons/12-context-menu-fitted-97.png) ·
[glass over text at 97%](proposals/glass-and-buttons/08-context-menu-glass-97.png)

## 1. Buttons (HUD, library toolbars, home hero, details Play/More, panel tabs)
| State | Look |
|---|---|
| **Resting** | Black box at 72%, white icon and text, no outline. Inside a glass panel (e.g. Playback Settings tabs) the resting box uses the panel's darker navy instead of black. |
| **Focused** | White box (`#F4F6FB`), black icon and text, soft shadow beneath (so it stays distinct on white/bright artwork). |
| **Selected, not focused** (active tab while focus is in the rows) | Resting box with a thin white outline. |
| **Disabled** | Resting look at 40% opacity. |

- **Size never changes** between states (no scaling, no inset for glows). Fixes the shrinking Playback Settings tab.
- One shape: rounded rectangle, 12dp corners (as the 0.152 HUD buttons). Two heights: large (HUD, hero) and standard (toolbars, tabs).
- Rationale: matches the owner's Netflix reference — white means focused, unmistakably. The black resting box carries its own contrast, so buttons are readable on any background (tested on white, yellow, pale sky, busy artwork, dark film).

## 2. Glass — panels, pop-ups, context menus
- One material: navy `rgb(26,31,92)` at **97%** opacity, soft white outline (40%), rounded corners.
- 97% keeps text behind the panel unreadable (measured: ~6/255 brightness leak over white synopsis text) while
  leaving a faint sense of depth. Readability of the panel's own text always wins over the see-through effect.
- Applies to Playback Settings, Edit Page, Download Subtitles, pickers, dialogs and every context menu.

## 3. Rows inside panels and menus
- **Focus:** white outline (≈3px at 1080p), 12dp corners, **no fill change** — the owner's existing context-menu focus.
- **Breathing room:** ≈16px (1080p) between the focus outline and the row's icon/text on each side.
- Applies to Playback Settings, Edit Page, Settings and all menus/pickers. Replaces the cyan glow in 0.152's side panels.

## 4. Context menus
- **No title.** "Actions" is removed everywhere; the menu opens straight onto its first group. (Owner: the user has just pressed the item, so it is obvious what the menu is for.)
- **Groups:** when a menu has two or more groups, every item sits under a heading; the heading is followed on the
  same line by a thin line spanning the rest of the menu (`Library ———`). A menu with one group has no heading.
- Details-page grouping: **Playback** (Restart Movie) · **Library** (Mark Watched, Add to Row, Report Incorrect Match, Remove From Library) · **Files & Media** (Delete, Subtitles and Artwork).
- **Width:** longest item (icon + text) + 24px each side inside the focus outline + panel padding;
  minimum 360px, maximum 40% of the screen width; longer items end in "…".

## Not changed
Item structure and actions of the existing menus; Playback Settings and Edit Page keep their fixed widths
(values sit on the right); the HUD layout, top navigation and library page layouts.

## Acceptance (Shield)
Owner photos showing: HUD, a library toolbar, the home hero and the details Play/More buttons at rest and focused;
Playback Settings tabs (selected tab same size as the others) and a focused row with breathing room; the home
long-press, library long-press and details More menus with no title, inline group headings and fitted width;
no readable text behind any panel.
