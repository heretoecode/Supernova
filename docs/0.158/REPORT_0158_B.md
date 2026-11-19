# 0.158 part B: report

Built unattended from `docs/0.158/BRIEF_0158_B.md`, the review `docs/0.157/REVIEW_0157_2.md`, the owner's first look
at 0.158 (`docs/0.158/REVIEW_0158.md`) and the page sheets (`docs/design/pages/`, Settings from `SETTINGS.md`).

## Signed APKs
SIGNED_LIST

## Diagnostics report
None in `docs/INBOX.md` and no "Inbox:" issue; the brief says the owner will send a fresh report from 0.158 for the
next session. The faults from the 18 Nov report (X1–X4) are dealt with below.

## What was built

### Group 1 — the new look (0.158.1)
- **Speckled background on every page** (RULES §1): base `#111215`, about 20 fine grains per 1,000 pixels, mostly
  7–22% white, about 1 in 7 at 45%, fixed pattern from the approved recipe, drawn once at the screen's own size and
  never smoothed. Through the shared page background, so Home, the library pages, Files, Search, Details, Settings,
  Fix match, Preparing playback and About all have it; Files and Fix match no longer paint their own charcoal. The
  fade under the top bar uses the speckle's base colour. Panels, menus and caption boxes stay solid; the player keeps
  the video behind it.
- **One shape for every small box** (RULES §3): 52 px high, 20 px corners, about 22 px side padding at 1080p, for
  every shared button, chip, tab and the top bar's boxes (`SupernovaStyle.SMALL_BOX_*`).
- **Text centred on its letters** in every shared button and tab (the top bar's method, now everywhere).

### Group 2 — Playback Settings (0.158.1)
- PS1: tabs use the top bar's focus: white box where the remote is; the open tab keeps a soft grey box with a faded
  word once focus goes down; no underlines.
- PS2: a clear gap under the tab row. PS3: tighter rows (62 px) and headings, so every tab fits.
- PS4: switches on a focused row flip like the text (dark track, white knob; off = light grey track, dark knob).
- PS5: pills sit close to the track name; their outline turns black on a focused row.
- PS6: the chosen chip on a focused row is black with white text.
- PS7: subtitles stay on top at every vertical position (the appearance sample had been put under the player).
- PS8: a track list opens on top with the panel behind kept whole (same width, same height), darkened and pushed back
  to 96%.
- PS10 / P6: the panel is `#16181C` at 90%, no outline, no glow.
- PS11: chips instead of ‹ › steppers — Format (Fit · Fill · Zoom · Stretch), Still watching? (Off · 2 episodes ·
  3 episodes · 2 hours · 3 hours), Background opacity (25 · 50 · 75 · 100%), Audio boost (Off · Low · Medium · High);
  − + for Vertical position; › on rows that open a list; every row has its name (chips go under the name when both
  don't fit, so Stop playing at, Play mode and Skip intros & credits are never cut); Text size has Extra large;
  Subtitle delay sits in Subtitle Selection; For This Series reads "Use these subtitles for all of …" / "Use this
  audio for all of …" with no help line; Download subtitles has no header, a plain search row with the white focus
  box, and names shown as words.

### Group 3 — crashes and engine (0.158.1)
- X1: leaving network discovery can no longer crash Files (the listener is removed once, and a second removal is
  ignored) — in the discovery job, the old network screens and the file browser.
- X2: Scan this folder on a local folder no longer crashes (the scan request is sent the way Android allows), also
  from Library Care's Try again and from adding a folder.
- X3 / PL7: seek previews of network files work again (the preview reader was given no header list and failed every
  time; 109 errors in the report).

### Group 3b — the Home hero (0.158.1)
- H1: the Home hero and the Details hero are one size and place, 1776 × 820 at 1080p, from one shared value
  (`TvLayout.heroWidth/heroHeight/heroTop`), the top under the top bar at the same height on both pages.
- H2: carousel dots under the Home hero (current one a longer white pill, gliding to its place; soft white for the
  rest; hidden when there is only one hero).

### Group 4 — Preparing playback and the player (0.158.2)
- PP1–PP3, PL1: one title block for Preparing playback and the player, in the player's place (top left) and sizes:
  title or logo, then the details line with the **age badge first** ("[15] · Season 2 · Episode 3 · Title"; a film:
  "[15] · 2024"), no reasons; the line keeps its space from the start so the badge never moves anything.
- PL2: the player's clock in the top bar's font (regular, same size).
- PL3: controls to the Chapters tab line about 30% closer; tab line to the cards 35% closer.
- PL5: no stub chapters: a part under 10 seconds joins the next one.
- PL6: chapter and episode cards have the artwork edge at rest.

### Group 5 — Details (0.158.2, part)
- DT2: Details tabs use the top bar's focus (as Playback Settings).
- DT7: More › Full description shows only the text (no title, Close or Copy); Back closes.
- DT9: values read "2 h 3 min", "$25 million" and "Arabic" (not "2h 3m", "$25,000,000.00", "ara").

### Group 8 — leftovers
- The icon picker's tiles are wide enough for the longest genre names (part A's "not built").

### Group 9 — Settings (first pass)
SETTINGS_STATUS

## Look sweep (Group 1)
LOOK_SWEEP

## Not built (plain list, before you install)
NOT_BUILT

## Judgement calls
See `docs/0.158/JUDGEMENT_CALLS_0158_B.md` (each with its reason).
