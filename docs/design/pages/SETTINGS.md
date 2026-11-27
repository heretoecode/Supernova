# Page sheet: Settings

The one place to build Settings from (CLAUDE.md "Definition of done"). For 0.158 part B. Latest decisions as of
19 Nov 2026.

Sources: `docs/0.157/SETTINGS_INVENTORY.md` (every setting and what happens to it; owner's answers 13–14 Nov; **design
approved 14 Nov**), `docs/design/ABOUT_SETTINGS.md`, `docs/design/BACKUP_RESTORE.md`, `docs/design/RULES.md` (§1, §3,
§5–§9, §15, §20, §24). Mock-ups in `docs/design/proposals/settings-v2/`, latest of each: `04-v2-playback`, `02-audio`,
`09-b2-subtitles`, `14-b3-library`, `15-b3-services`, `22-b3-your-services-v2` / `26-b4-services-and-network-options`
(left), `17-b3-system`, `18-b3-backup-choose-restore`, `23-b4-restore-progress`, `24-b4-about`, `25-b4-credits-licences`,
`27-b5-licences`, `06-v2-subtitle-lists`, `07`/`08` (OpenSubtitles sign-in, Play With).

**The mock-ups predate 18 Nov.** Draw them with today's rules: the 0.158 top bar (no SUPERNOVA, every item a word),
the speckled background (RULES §1), one shape for every small box (RULES §3: 52 px, 20 px corners), the tab focus
approved for Details and Playback Settings (PS1 / DT2: white box on the focused tab, soft grey box with a faded word
on the open tab once focus goes down, no underlines), "this device" never "Shield" (RULES §22). Where a mock-up and a
later dated decision differ, the decision wins (listed under "Newer than the pictures").

## Review 26 Nov (owner; mock-ups `settings-v3/tv/01`–`19`, Drive "2026-11-26 Settings round 1–4")
Drawn on today's rules (top bar, speckle, Files-style tabs with the underline, steppers for three or more choices).
Rounds: 1 Playback, Audio (`01`–`02`) · 2 Subtitles (`03`–`06`) · 3 Library, Services (`07`–`11`) · 4 System, About
(`12`–`19`). **New, proposed by Claude, to approve or park:** Playback › Start a little earlier (rewind on resume);
Subtitles › Subtitles for the hard of hearing (SDH: Prefer · Avoid · Either); Library › Hide spoilers; Trakt panel's
Sync now. Look for new videos (approved 26 Nov) is drawn on Library. Notes go here, round by round.
**Round 1, Playback and Audio (owner, 27 Nov): approved as drawn** (`01`, `02`): Start a little earlier kept (Off ·
5 · 10 · 30 s, default 5); Skipping keeps four choices in Settings (Off · Show button · Skip · Skip when
binge-watching); steppers as drawn. **Flags (owner, 27 Nov):** every language in every list (audio, subtitles,
interface, Details, downloads, the player's track lists) has its own flag; a coding check fails the build if any
language falls back to the plain globe. RULES §9 updated. The neutral mark is redesigned (see RULES §9).

## Whole page
- A full page with tabs, like Files: no tiles, no panel. **Seven tabs:** Playback · Audio · Subtitles · Library ·
  Services · System · About. Settings is a tool: it opens on OK in the top bar, on Playback (or the tab last used
  while the app stays open).
- Side margin on the top bar's edges (RULES §1). Section headings with icons, 26 px below / 64 px above (RULES §5).
  Every row: icon, name, one plain second line, the control on the right, everything on the row's centre line.
- Controls (RULES §8): **switch** for on/off; **chips** inline for two choices; **more than two: one value stepped with Left / Right** (RULES §8, owner 21 Nov; replaces chips for up to five); **›**
  only on rows that open a list, panel or page, with the current value before it; **actions** show their verb as
  plain text (Clear, Send, Fetch, Save, Back up now), progress and "Done · …" appear in the row itself, with Stop
  while running; a warning only where something is lost (Supernova's own delete warning).
- Lists open as the **shared choice list** floating on the right (RULES §9), the page still in view; typing uses the
  one keyboard beside the panel (RULES §15); folders use the one folder picker (RULES §7b), never Android's.
- Every tab fits its rows top to bottom; long tabs scroll with the arrows in the right margin (RULES §16).

## Playback (`04-v2-playback`)
- **General:** Hide controls when paused (switch) · Seek previews **Experimental** (switch; moved here from
  Advanced) · Open videos in another player (switch; when on, Play shows Supernova's **Play With** list, `08`).
- **Resume:** When a video was started (Ask · Resume · Start over) · Remember where you stopped (switch; on this device
  and network files, one setting).
- **Skipping:** Look up skip times online (switch: TheIntroDB and IntroDB, when the file doesn't mark its parts) ·
  Intro · Recap · Preview · Outro · Credits, each **Off · Show button · Skip · Skip when binge-watching**.
- **Picture:** Automatic refresh rate (Off · Match the video) · Dolby Vision (Automatic · Always · Off).

## Audio (`02-audio`)
- **Language:** Preferred audio language (› choice list, flags) · Prefer the original language (switch).
- **Sound:** Surround sound (Automatic · Passthrough · PCM · Stereo; passthrough merged in) · Virtual surround
  (switch) · Audio decoder (Automatic · Hardware · Software).

## Subtitles (`09-b2-subtitles`)
- **When To Show:** Show subtitles · Full subtitles when the audio is in your language (on by default) · Also
  translate other languages **Experimental** (on by default).
- **Languages:** Preferred subtitle language (› one) · Languages to search online (› several).
- **Downloading:** OpenSubtitles (› panel: signed out = one line, Sign in, QR code to create a free account; signed in
  = account, downloads left today, Change account, Sign out; sign-in = username and password with the keyboard beside,
  show/hide eye; `06`–`08`) · Find subtitles when none are included (switch) · Have subtitles ready (switch: fetched
  while a video prepares, and for the next two episodes).
- **Default Appearance** (the same rows as the player's Subtitle Appearance, one for one; Playback Settings' "For
  This Series" saves per show): Text size (Small · Medium · Large · Extra large) · Text colour (colour dots) ·
  Vertical position (− value +) · Outline (switch) · Background (switch) · Background opacity (25 · 50 · 75 · 100%) ·
  Font (› list, previewed) · Custom fonts folder (› folder picker) · Use the black bars (switch) · Text encoding (›
  Automatic, then by language with flags).

## Library (`14-b3-library`)
- **Your Library:** Ignore "The" and "A" when sorting (switch; sort titles rebuild by themselves) · Make thumbnails for
  network videos (switch).
- **Details:** Find details automatically · Details language (›) · Use .nfo files · Write .nfo files for new titles
  (switches) · Write .nfo files for the whole library (action: "Writing 412 of 1,000 · Stop", then "Done · 1,000
  written").
- **Home Hero:** Recently added · Trending on Trakt · Popular on Trakt · Because you watched (switches; at least one
  stays on: the last one can't be turned off) · Change every (8 s · 12 s · 20 s); titles you've started leave the hero.
- **Viewing History:** Keep viewing history (switch) · Clear viewing history (action, Clear: a warning with
  Supernova / Trakt switches; library and files kept).
  **Search history (owner, 25 Nov):** Keep search history (switch, on; off records nothing, hides Search's Recent
  searches and clears it) · Clear search history (Clear; "Done · cleared").

## Services (`15-b3-services`)
- **Where To Watch:** Show where to watch (switch; off removes every streaming feature, RULES §18) · Country (› list
  with flags; default from the device's region) · Your services (› the ordered list, `22` / `26` left: hint above
  Selected "Your first choice goes at the top. Press OK on a ticked service to move it.", Selected in your order with
  the move gesture, then All; colour logos).
- **Trakt:** Trakt (› sign in / signed in as …) · Scrobble while watching · Sync resume points · Sync your library to
  Trakt (switches) · Send everything to Trakt again (Send) · Fetch everything from Trakt again (Fetch) · Remove
  Supernova's data from Trakt (Remove, with warning).
- **Ratings:** OMDb ratings key (› typed with the keyboard, never shown in full; "Key saved"). Then the plain line
  "Streaming availability from JustWatch", nothing to press.

## System (`17-b3-system`)
- **General:** Interface language (›).
- **Backup:** What to back up (Everything · Settings only · Choose; Choose opens the six groups, `18` left: Settings ·
  Home rows and library pages · Viewing history · Fixed matches and hidden titles · Connections · Subtitle fonts) ·
  Backups folder (› Change: folder picker) · Back up now (action, progress and Stop in the row) · Restore (› "3
  backups": the list → In This Backup → Restore this backup, asks first → progress in the Restore row → **Restart
  now** when done, `18` right, `23`).
- **Diagnostics:** Diagnostic logging (switch) · Keep diagnostics for (1 · 2 · 4 weeks; default 2) · Save a diagnostic report (Save; "Done · saved at 23:51 · never includes passwords") · Clear diagnostics
  (Clear, asks first).
- **Advanced:** Projector mode · Force software decoding (switches) · Video decoder (Automatic · Hardware · Software)
  · Parser synchronisation (Automatic · Strict · Relaxed) · Stream buffer (Small · Medium · Large) · Max I-frame size
  (chips, as today's values) · Audio interface (Automatic · AudioTrack · OpenSL) · Sound when speed changes (FFmpeg ·
  Sonic) · Stereo downmix (Automatic · Always · Never) · Automatic audio delay (switch). Each with its one-line
  explanation.

## About (`24-b4-about`, `25`, `27`)
- Header: "Supernova 0.158.x" and "Built <date> · based on NOVA 6.6.x · Android TV <n>" (no App information row).
- **What's New:** the last five versions (number, date) and **All versions** (back to 0.135); OK opens a pop-up like
  Full description, one change per line (from `assets/foundation/release-history.json`).
- **More:** Credits (› a floating list of every service and component; each service opens a pop-up with what it
  supplies, any required wording and a QR code to its site) · Open-source licences (› a floating grouped list of the
  components, name and licence type, search row; OK opens a pop-up with a 170 px QR code to that licence's text; one QR
  code on screen at a time, RULES §20).

- **Look for new videos (owner, 26 Nov; to draw at the Settings review):** a row under Your Library: Every 3 hours ·
  6 hours (default) · 12 hours · Daily · Only when I ask (five choices: stepper, RULES §8). Sets the background scan
  and the "opened after longer than this" check in BRIEF_0158_C D14; plugging in a drive and the Scan buttons always
  scan, whatever the choice.

## Newer than the pictures (apply these)
- **Reports always go to Download › Supernova** (no Reports folder row); **Backups folder** uses Supernova's folder
  picker with Android's grant once behind it (owner, 19 Nov).
- Skipping has **four** choices (owner, 13 Nov round 2), not the three drawn in `04`.
- Open videos in another player stays in **Playback › General** and opens Supernova's own Play With list.
- Network share options (SMB, discovery, SFTP; `26` right) live on **Files › Connections › Advanced**, not Settings.
- Gone from Settings: Appearance tab, Customise Home shortcut (Home has its own), Hide watched titles, Recreate sort
  titles, Use already scraped information, Show first (folded into Your services' order), Show all services, Automatic
  3D TV switch, Reset brightness, Enable playback speed, Issues Digest.
- Top bar, background, small-box shape, tab focus: as in the note at the top.

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| OK on Settings in the top bar | Settings, Playback tab (or the last tab while the app stays open), focus on the tab |
| Left / Right on the tabs | Switches the tab at once; Down goes to that tab's first row |
| Up from a tab's first row | The open tab (RULES §4) |
| Left / Right on a chips row | Moves the choice; saved at once |
| OK on a switch row | Flips it; saved at once |
| OK on a › row | Its choice list, panel or folder picker floating on the right; Back closes it, focus back on the row |
| OK on an action (Send, Fetch, Clear, Save, Back up now, Write .nfo …) | Runs in the row with progress and Stop; "Done · …" after; warning first where data is lost |
| What to back up › Choose | The six groups with ticks, floating on the right |
| Restore › a backup | In This Backup → Restore this backup (warning) → progress in the Restore row → Restart now |
| OpenSubtitles / Trakt › | Their panel (sign in, account, sign out); sign-in fields use the one keyboard beside the panel |
| OMDb ratings key › | The key field with the keyboard beside; saved shows "Key saved", never the key |
| A version in What's New / All versions | Its release notes pop-up / the full list |
| Credits › a service | Its pop-up with the QR code |
| Open-source licences › a component | Its pop-up with the QR code |
| Back | From a panel to its row; from the rows to the open tab; from the tabs to the top bar |

## Never opens from Settings
The old Settings tiles and side panels (`SettingsLayout`'s grid), Android's preference screens, Android's folder
picker, Android's "Open with" chooser, Android's on-screen keyboard, the Issues Digest, NOVA's
old About and licence screens. Add them to the no-old-screens test.
