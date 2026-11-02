# Buttons, glass panels and pop-up menus — one global style (APPROVED 2 November 2026)

**Status:** APPROVED by the owner. Design only — **no implementation until the owner explicitly says so.**
**Target:** 0.153 (when authorised).

## Scope — these rules are GLOBAL
These rules apply to **every** button, pop-up menu, context menu, dialog, picker and floating panel in the app,
**including ones not shown in the owner's photos or in the mock-ups**. The owner has deliberately not shown every
screen: the acceptance test is that screens nobody discussed change too. The examples below (HUD, library toolbar,
home hero, details page, Playback Settings, Edit Page, long-press menus) are illustrations, not a list of where the
rules apply.

Implementation requirements (for when it is authorised):
1. **One source of truth.** Every button, menu row, menu and panel takes its colours, shapes, focus and spacing from a
   single shared style. No screen may define its own button background, focus outline, menu surface or panel fill.
2. **No opt-outs.** Existing per-screen styles (custom pills, glows, accent fills, one-off dialog surfaces) are
   replaced by the shared style, not left beside it.
3. **Proof of coverage.** An automated check fails the build if app code creates its own button/focus/menu/panel
   styling outside the shared style, and a render of every menu, dialog and button-bearing screen accompanies the build.
4. Anything that genuinely cannot follow a rule is listed in the build notes with the reason, for the owner to decide.

Mock-ups (1920×1080, [render.js](proposals/glass-and-buttons/render.js)):
[buttons](proposals/glass-and-buttons/16-boxed-black-buttons.png) ·
[menu headings](proposals/glass-and-buttons/15-menu-heading-inline-line.png) ·
[menu without title](proposals/glass-and-buttons/17-menu-title-options.png) (option A) ·
[fitted width](proposals/glass-and-buttons/12-context-menu-fitted-97.png) ·
[glass at 97%](proposals/glass-and-buttons/08-context-menu-glass-97.png)

## 1. Buttons — every button in the app
| State | Look |
|---|---|
| **Resting** | Black box at 72% opacity, white icon and text, no outline. A button inside a glass panel (e.g. panel tabs) uses the panel's darker navy instead of black. |
| **Focused** | White box (`#F4F6FB`), black icon and text, soft shadow beneath so it stays distinct on white or bright backgrounds. |
| **Selected, not focused** (e.g. the active tab while focus is elsewhere) | Resting box plus a thin white outline. |
| **Disabled** | Resting look at 40% opacity. |

- **Size never changes** between states — no scaling, no shrinking for glows (fixes the 0.152 Playback Settings tab).
- One shape: rounded rectangle with 12dp corners. Two heights: large (e.g. HUD, hero) and standard (e.g. toolbars, tabs, dialog buttons).
- Icon-only buttons (e.g. play/pause) follow the same states.
- **Buttons fit their content.** Width = icon + label + the standard padding; no fixed widths, no stretching, no
  layout weights that inflate a button. Buttons in the same row share one height.
- **Icons are one size.** Every button icon, including **brand marks** (e.g. streaming-provider logos such as
  Netflix), is drawn at the standard icon size for that button height — never larger than the text beside it.
- **Icons invert with the text.** On focus, icons and brand marks must stay visible on the white box: monochrome icons
  turn black with the text; a white brand mark switches to its dark or full-colour version. A mark that cannot be
  shown legibly on both boxes is listed in the build notes.
- **"Every button" includes buttons built by other components** — presenters, adapters, cards and dialogs — not only
  buttons created directly by a screen. Example found 2 Nov 2026 (owner photo, Dune: Part Two details page): the
  "Netflix" streaming button is built by a separate provider presenter and forced to a fixed 160dp width and Play's
  height, so it renders far larger than Resume and More. Under these rules it must match them exactly.

### Streaming-provider buttons (approved 2 Nov 2026)
- Label: the provider's name only ("Netflix", "Disney+", "Prime Video").
- Icon: one generic **"open externally"** icon for every provider — a box with an arrow leaving it — telling the user
  the button opens another app ([option A](proposals/streaming-button/01-icon-designs.png),
  [in context](proposals/streaming-button/02-in-context-A-B-C.png)). No provider logo on the button.
- Same size, shape and states as every other button (matches Resume and More).
- Provider logos elsewhere (badges, filters, settings, pickers): see **Provider logos** below.

### Provider logos (approved 2 Nov 2026 — option A)
- Every provider shown with an icon (e.g. the My providers picker, provider filters, availability badges) uses its
  **official single-colour logo**, drawn in the same colour as the row's text (white at rest, black inside a focused
  white box), centred in a **uniform tile** so every row has the same visual weight
  ([mock-up](proposals/provider-picker/01-monochrome-tiles.png)). Never full colour, never recoloured by us.
- **Symbol, not word logo.** Where a provider has an official symbol (e.g. Netflix "N", the Prime Video smile,
  the Disney+ arc), the tile uses the symbol, because the provider's name is already written beside it —
  a word logo next to the same words says it twice and is unreadable at tile size.
- **Coverage goal: every provider.** Official single-colour marks are sourced for every provider the app can list.
  A monogram tile (initials in the same tile shape) is a last resort only where no official mark exists or it cannot
  be used; each such provider is named in the build notes for the owner. The generic Wi-Fi-like fallback icon is removed.
- Reason: owner's Netflix reference — white means focused, unmistakably; the black box keeps buttons readable on any
  background (tested on white, yellow, pale sky, busy artwork and dark film).

## 2. Glass — every pop-up, menu, dialog and floating panel
- One material: navy `rgb(26,31,92)` at **97% opacity**, soft white outline (40%), rounded corners.
- At 97% text behind a panel is not readable (measured leak ≈6/255) while a faint depth remains. Readability of the
  panel's own content always takes priority over the see-through effect.

## 3. Rows — every list row inside a menu, panel, picker or settings screen
- **Focus:** white outline (≈3px at 1080p), 12dp corners, **no fill change** (the owner's existing context-menu focus).
- **Breathing room:** ≈16px (1080p) between the outline and the row's icon/text on each side.
- Replaces every other row focus (e.g. the cyan glow in 0.152's side panels).

## 4. Pop-up and context menus — every one
- **No title.** Generic titles such as "Actions" are removed; the menu opens on its first group or item.
  (Owner: the user has just pressed the item, so its purpose is obvious.)
- **Groups:** if a menu has two or more groups, every item sits under a group heading, and the heading is followed on
  the same line by a thin line spanning the rest of the menu (`Library ———`). A single-group menu has no heading.
- **Width:** longest item (icon + text) + 24px each side inside the focus outline + panel padding;
  minimum 360px, maximum 40% of the screen width; longer items end in "…". No empty space for no reason.
- Example grouping (details-page More menu): **Playback** — Restart Movie · **Library** — Mark Watched, Add to Row,
  Report Incorrect Match, Remove From Library · **Files & Media** — Delete, Subtitles and Artwork.

## Not changed
What each menu contains and what each action does. Large fixed-layout panels whose values sit on the right
(Playback Settings, Edit Page) keep their fixed widths but follow every other rule.

## Acceptance (Shield)
The owner checks screens **not** used in the design discussion first: every button, menu, dialog and panel follows
sections 1–4 without exception. Then the discussed screens: HUD, library toolbars, home hero, details Play/More,
Playback Settings (selected tab same size as the others; focused row with breathing room), Edit Page and the
long-press/More menus (no title, inline group headings, fitted width); no readable text behind any panel.

## 5. Tiles — every large boxed choice (approved 2 Nov 2026)
Applies to the Settings category grid and **every similar boxed choice in the app** (e.g. Customise Home tiles).
Poster and artwork cards are not tiles: they keep their own focus treatment so artwork is never covered.
([mock-up](proposals/settings-tiles/06-single-line-titles.png))
- **Resting:** navy glass (the same 97% material as panels), soft outline.
- **Focused:** the same white box with black text and black icon as a focused button, soft shadow beneath.
  Size never changes.
- Principle: small buttons rest in black; large surfaces (tiles, buttons inside panels) rest in navy glass;
  everything focused becomes the white box.
- **Layout:** icon beside the title, pinned to the title's line; the description runs full width underneath.
- **Titles fit on one line** (reword rather than wrap); descriptions at most two lines, so the headings in a row line up.
- Settings category names: Playback & Video · Audio & Subtitles · Library & Home · **Streaming** (was Streaming &
  Providers) · Appearance · **Connections** (was Connections & Integrations) · **System** (was System & Maintenance) ·
  About Supernova. Streaming description: "Choose your streaming providers and how availability is shown."
  Renamed categories change everywhere their name appears (e.g. category headings, settings search).
