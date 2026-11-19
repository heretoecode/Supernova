# 0.158 part B: report

Built unattended from `docs/0.158/BRIEF_0158_B.md`, the review `docs/0.157/REVIEW_0157_2.md`, the owner's first look
at 0.158 (`docs/0.158/REVIEW_0158.md`) and the page sheets (`docs/design/pages/`, Settings from `SETTINGS.md`).

## Signed APKs
- **0.158.1** (code 1581) — signed 19 Nov 2026 by run
  [44479811283](https://github.com/heretoecode/Supernova/actions/runs/44479811283) of `supernova-0157-qa.yml`, from
  green conformance run [44470632446](https://github.com/heretoecode/Supernova/actions/runs/44470632446) (2193 tests,
  0 failing) on source `731261f`. Artifact `supernova-0157-signed-qa` (`Supernova_0.158.1.apk`, archive digest
  `7472563a…7dc6`); kept until 3 Dec 2026. Contains groups 1, 2, 3 and 3b.
- **0.158.2** (code 1582) — signed 19 Nov 2026 by run
  [44500001166](https://github.com/heretoecode/Supernova/actions/runs/44500001166), from green conformance run
  [44493050022](https://github.com/heretoecode/Supernova/actions/runs/44493050022) on source `8b8a8ff`. Artifact
  `supernova-0157-signed-qa` (`Supernova_0.158.2.apk`). 0.158.1 plus groups 4, the Details part of group 5 below, the
  icon picker width and FL6.
- **0.158.3** (code 1583) — signed 19 Nov 2026 by run
  [44506040551](https://github.com/heretoecode/Supernova/actions/runs/44506040551), from green conformance run
  [44500505615](https://github.com/heretoecode/Supernova/actions/runs/44500505615) on source `4d310f4`. Artifact
  `supernova-0157-signed-qa` (`Supernova_0.158.3.apk`), kept until 3 Dec 2026. **Install this one.** 0.158.2 plus the
  Settings redesign, DT4, DT8, the row lines and pages reopening at their top.
- On the branch after 0.158.3, not in any APK yet: FL9 (focus stays on Check matches).

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

### Group 9 — Settings (0.158.3)
- One page with seven tabs (Playback · Audio · Subtitles · Library · Services · System · About), no tiles; the tabs in
  the top bar's look; each tab shows every section under its heading (icon, 26 px below / 64 px above) with the
  sheet's wording.
- Playback: General (Hide controls when paused · **Seek previews, Experimental** · Open videos in another player) ·
  Resume · Skipping (Look up skip times online, then Intro … Credits) · Picture. Audio: Language · Sound. Subtitles:
  When To Show · Languages · Downloading (with OpenSubtitles) · Default Appearance (the player's own appearance rows,
  then fonts, black bars, encoding). Library: Your Library · Details · Home Hero · Viewing History. Services: Where To
  Watch · Trakt · Ratings. System: General · Backup · Diagnostics · Advanced. About: as before.
- PL7: Seek previews had no entry in the settings map, which is why it never showed; it now does.
- The old tiles and Android's screens are in the no-old-screens test; the old Settings tests were rewritten for the
  new page.

### Group 5 / 8 additions (0.158.3)
- DT8: Fix match in More for TV shows. DT4: the gap above each season's heading halved.
- RULES §16: a page left for another reopens at its top (Back already went straight to the top bar).

## Look sweep (Group 1)
The speckle, the one box shape and the letter centring went in through the shared pieces, so every page picked
them up at once; the Playback Settings panel colour (`#16181C` at 90%, no outline, no glow) is settled. Renders of the top bar, library pages (grid and list), Home and
Search on the speckle were checked: text, keys, pills and second lines read well; the lines between rows were too
faint and went from 8% to 12% white (0.158.3). Floating panels sit very close to the speckle's colour (judgement call
26). Files, Details, the player and Settings renders are still to check.

## Not built (plain list, before you install)
- **Settings, still to do from the sheet:** the in-row backup flow (What to back up chips, progress and Restart now in
  the row), the OpenSubtitles and Trakt panels redrawn, Home Hero's "Change every" and "Because you watched",
  "Also translate other languages". Backup and OpenSubtitles open their existing views from a row.
- **Look sweep** — Files, Details, the player and Settings not yet rendered on the speckle.
- **PS9** (Playback tab looked at again after PS1–PS6) — needs your photo of the new panel.
- **X4** — scans still wait for each other instead of queueing (the lock is in MediaLib's build patches; its own job).
- **X5** — no separate focus work beyond the groups' own fixes.
- **PL4** (controls linger on the seek bar) — not changed; I couldn't tell from the notes what looks wrong.
- **PL8** — the skip button doesn't yet join the controls' line.
- **Details:** DT1 (Details tab on one screen), DT3 (rows clipped at the sides), DT5 (one
  row design; More Like This by service), DT6 (artwork side panel), R3 (season
  artwork), R4 (season More menu), R5 (choose a version), **episodes** (OK plays, long-press menu, deleting with
  earlier episodes).
- **Group 6, titles** — logo placement fix and house style A: not started.
- **Group 7, Files** — FL6 done; FL1–FL5, FL7–FL12 not started.
- **Group 8** — R2 (the own page's bar) not done; Back and page position done.

## Judgement calls
See `docs/0.158/JUDGEMENT_CALLS_0158_B.md` (each with its reason).
