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
  `supernova-0157-signed-qa` (`Supernova_0.158.3.apk`), kept until 3 Dec 2026. 0.158.2 plus the
  Settings redesign, DT4, DT8, the row lines and pages reopening at their top.
- **0.158.4** (code 1584) — signed 19 Nov 2026 by run
  [44531272454](https://github.com/heretoecode/Supernova/actions/runs/44531272454), from green conformance run
  [44521801198](https://github.com/heretoecode/Supernova/actions/runs/44521801198) on source `ef5af36`. Artifact
  `supernova-0157-signed-qa` (`Supernova_0.158.4.apk`). Playback Settings round two, panels at 99%, FL9.
- **0.158.5** (code 1585) — not signed: a render check found Settings would not open in it (a list declared after
  the code that fills it). Its build was cancelled and everything in it goes into 0.158.6.
- **0.158.6** (code 1586) — not signed: the renders found Settings still wouldn't open (its registry expects an
  exact number of rows, and the four new rows weren't counted). Cancelled; everything goes into 0.158.7.
- **0.158.7** (code 1587) — signed 19 Nov 2026 by run
  [44604872477](https://github.com/heretoecode/Supernova/actions/runs/44604872477), from green conformance run
  [44597211401](https://github.com/heretoecode/Supernova/actions/runs/44597211401) on `ecbfdde` (2202 tests, 0
  failing). Artifact `supernova-0157-signed-qa` (`Supernova_0.158.7.apk`), kept until 3 Dec 2026: https://github.com/heretoecode/Supernova/actions/runs/44604872477/artifacts/14569652013. **Install this one.** 0.158.4 plus
  everything since: the Settings wording and new rows (Home Hero, Keep diagnostics for, When a video was started),
  Save a diagnostic report in its row, the OpenSubtitles panel, the Trakt sign-in look, the episodes feature, R3,
  R4, R5, DT1, DT3, DT5, DT6, titles, PL8, X4, FL1–FL5, FL7, FL8, FL10, FL12. Two earlier attempts failed and were
  fixed: a duplicated setting in the Trakt layout (compile), then 8 tests (logo size tied to the screen; a
  hand-made focus box).
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

### Part B continued (second session, 19 Nov)
- **Playback Settings, round two** (`player/33-v15`; 0.158.4): panel 820 px at 1080p, #16181C at 99%; every size
  from the mock-up (rows 62 px), so every tab fits with no scrolling; every row on one line; no line under the tabs;
  a faint line between rows that steps aside beside the focused row. Text size, Background opacity, Audio boost,
  Format, Speed, Stop playing at, Still watching? and Play mode step with Left / Right, the value alone at rest and
  ‹ › only on the focused row; OK steps on and goes round. **Skipping** keeps its chips Off · Show button · Skip.
  Headings Picture and Speed · Playing On · This Show; "Remember for this show" on all three tabs.
- **Panels** (RULES §11; 0.158.4): one value for every pop-up, menu and side panel, #16181C at 99%, no outline, no
  glow; the old navy menu surface and the old Playback Settings shell use it too.
- **Settings wording** (0.158.5): skip rows read Off · Show button · Skip · Skip when binge-watching (chips); Skip
  when binge-watching skips every part when binge-watching and shows the button otherwise. Every row's name checked
  against the sheet and the NOVA ones renamed (Automatic refresh rate, Dolby Vision, Audio decoder, Full subtitles
  when the audio is in your language, Font, Use the black bars, Text encoding, Write .nfo files …, Recently added,
  Trending on Trakt, Popular on Trakt, Trakt, Send / Fetch everything …, OMDb ratings key, Clear diagnostics, Video
  decoder, Parser synchronisation, Stream buffer, Max I-frame size, Audio interface, Automatic audio delay); choices
  have plain names (Automatic · Hardware · Software and so on). Home Hero gains **Because you watched** and **Change
  every** (8 s · 12 s · 20 s); the last source can't be turned off. Diagnostics gains **Keep diagnostics for**.
- **DT3** (0.158.5; edges 0.158.6): a grown card and its ring are no longer cut at the sides of Details rows, and
  the Episodes rows run out to the screen's edges with the first card where it was.
- **Episodes** (0.158.5): OK on an episode card plays it at once; long-press or Menu opens its menu beside the card
  (Resume or Play, Restart episode · Mark as watched, Fix match · Delete episode). Deleting an episode with earlier
  ones of the season in the library: one warning, "You also have episodes 1 and 2 of this season.", with Keep file ·
  Delete episode 3 only · Delete all three · Choose…; Choose… is the shared choice list with Select all at its top
  and Done at its foot, then the same warning for what's ticked. No renders yet (no mock-up; see judgement call 41).
- **R3** (0.158.5): season pages show the show's backdrop, else an episode's picture, else the poster.
- **DT5** (0.158.6): More Like This in rows: In your library first, then one row per service in the order of Your
  services, each heading with the service's colour logo.
- **R5** (0.158.6): with two or more files for a title, Play opens a menu beside it, one row per file
  ("4K · HDR · 18.2 GB"), the last choice ticked.
- **DT1** (0.158.6): the Details tab's rows, headings and ratings take the B2 mock-up's sizes (they were about 60%
  taller), so the three columns and Cast and Crew fit one screen; not yet checked in a render.
- **DT6** (0.158.6): a TV show's More › Artwork opens the artwork side panel, as a film's does, instead of the
  corner menu and the old grid.
- **R4** (0.158.5): the season page's More menu opens beside More: Series details · Episodes · Delete season (with
  the delete warning, Keep files first).

- **Titles, group 6** (0.158.5): every official logo sized to one area inside 900 × 240 (wide ones capped by width,
  tall ones by height), its foot on the title's foot; names without a logo in house style A (heavy capitals, white,
  no glow, two lines at most, the part before a colon small).

## Look sweep (Group 1)
The speckle, the one box shape and the letter centring went in through the shared pieces, so every page picked
them up at once; the Playback Settings panel colour (`#16181C` at 90%, no outline, no glow) is settled. Renders of the top bar, library pages (grid and list), Home and
Search on the speckle were checked: text, keys, pills and second lines read well; the lines between rows were too
faint and went from 8% to 12% white (0.158.3). Floating panels sit very close to the speckle's colour (judgement call
26). Files, Details, the player and Settings renders are still to check.

## Not built (plain list, before you install)
- **Settings, still to do from the sheet:**
  - The in-row backup flow (What to back up chips, a Backups folder, the list of backups, progress and Restart now in
    the row). This changes where backups are kept (Android's picker → a chosen folder), and a mistake there loses
    data, so it wants its own careful pass with a Shield check. Backup still opens its existing view from the row.
  - "Also translate other languages": a player-engine feature (a second, "foreign parts only" subtitle merged on
    screen) that can only be judged on the Shield.
  - Reports folder › Change (Save now writes to Download › Supernova from the row). It, and the backup flow's
    Backups folder, need Supernova's one folder picker (the Files browser in picking mode, RULES §7b), which isn't
    built yet. A catch to decide: on Android 11 Supernova can only write into Download or a folder granted
    through Android's own folder picker, so a Supernova-style picker for Reports and Backups would still need
    Android's grant behind it once. Your call: keep Download › Supernova, or the picker plus one Android grant.
  - Trakt sign-in as a side panel (it now has Supernova's look; judgement call 61).
  Built this session: the wording and choice names, Home Hero's two rows, Keep diagnostics for, When a video was
  started (Ask · Resume · Start over), Save a diagnostic report in its row, verbs on action rows, the OpenSubtitles
  panel.
- **Look sweep** — rendered on the speckle and checked: Playback Settings (matches v15), Files (connections, the
  form, Library Care), Details (the Details tab on one screen). The renders found and fixed: the form still too
  tall, labels cut, the fifth connection button cut, and two Settings faults that would have stopped Settings opening.
- **X4** — built (0.158.6) as a new MediaLib patch: a scan waits its turn for the scan lock before its five-minute
  clock starts (a one-hour bound covers the wait), so queued scans no longer fail with "scan lock wait interrupted".
  Needs a diagnostics report from the Shield to confirm.
- **PL4** (controls linger on the seek bar) — not changed; I couldn't tell from the notes what looks wrong.
- **PL8** — the skip button is now the one small-box size (it joined the controls' line already in 0.157); not yet
  checked in a render beside the controls.
- **Details:** all built. DT6 is built for TV shows
  (films already had the side panel), not yet checked in a render.
  Episodes: built, but not yet checked in renders.
- **Group 6, titles** — built (logo placement by one area; house style A), not yet checked in renders; a long
  title ends with "…" rather than shrinking, and the colon split doesn't force a new line (judgement call 43).
- **Group 7, Files** — FL1, FL2, FL4, FL6–FL11 done (FL11 was already in: "Scanning · 42%" while running; FL1,
  FL2, FL4 and FL10 not yet checked in renders). FL12 built (0.158.6): when Android brings a page back after you stopped
  the app yourself, it opens Home instead; when Android ended it in the background, the page comes back (Android 11
  says why the app last ended). FL3 + FL5 built (0.158.6): Turn off / Turn on on each connection, smaller buttons.
- **Group 8** — R2: nothing changed; your own page now drives the same fade under the top bar as Movies and TV Shows (same code), so please check it on the Shield and send a photo if the solid bar is still there. Back and page position done.

## For the next session
- Branch: `claude/0158-part-b-continued-d5u9w1` holds everything; the working branch is fast-forwarded to it only
  when a build is due (a push there starts conformance and cancels a running one; signing accepts only the working
  branch). The two 0.158 MediaLib patches live in `.github/build/`, which `.gitignore` ignores: add new patches with
  `git add -f` and list them, with their SHA-256, in `.github/build/supernova155-dependency-patches.json`.
- 0.158.7 is signed (run 44604872477). Part B is paused: see `PART_B_PAUSED.md`.
- Still to do, in this order: (1) the in-row backup flow; (2) "Also translate other languages"; (3) the Trakt
  sign-in as a side panel; (4) Reports folder › Change, waiting for the owner's answer (keep Download › Supernova, or
  a folder picker with one Android grant). Then renders of the episodes screens, titles, R5's Play menu and More
  Like This by service. R2: the owner checks it on the Shield.
- Before any long build, run `preview-renders.yml` on the session branch with the tests the change touches
  (`.github/diagnostics/preview-request.json`): four minutes, and it caught every fault this session.

## Judgement calls
See `docs/0.158/JUDGEMENT_CALLS_0158_B.md` (each with its reason).
