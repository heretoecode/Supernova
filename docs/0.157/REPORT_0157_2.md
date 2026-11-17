# 0.157.2: overnight report (17–18 Nov 2026)

Built from `docs/0.157/BRIEF_0157_2.md`, the page sheets (`docs/design/pages/FILES.md`, `DETAILS.md`) and the
review `docs/0.157/REVIEW_0157_1.md`. Version **0.157.2**, code **1572**.

## Signed APK
**Signed 18 Nov 2026** by run 44020349261 of `supernova-0157-qa.yml`
(https://github.com/heretoecode/Supernova/actions/runs/44020349261), from green conformance run 44015287084 on
`c3f75d5` (every app, MediaLib, FileCore and tooling test, lint, 2,205 app tests). Artifact
**`supernova-0157-signed-qa`** (`Supernova_0.157.2.apk`, about 32 MB). Earlier conformance runs that night failed on
tests still expecting the old design (updated) and one style rule (fixed).

## Diagnostics report
Not in `docs/INBOX.md` and no "Inbox:" issue, so the two crashes (about 23:09 and 23:14, 16 Nov) and the blank
screen (about 23:39) were not read from the report. Their cause as described (Back while focus had jumped to the top
bar in Files) is fixed at the root: focus no longer jumps (F9), and Back from the top bar now goes up a folder in
Files instead of leaving the app (F10). **Please send the latest report** so it can be checked, with the read of
what you did not see.

## What was fixed
**Files** (every action is pressed in `Review01572FilesTest` with realistic data; renders on `previews/claude`)
- F3: Files fills the page itself; the same 96 px margin, nothing clipped, grown cards may reach into the margin.
- F9, F10: focus never jumps to the top bar while folders open or Back goes up (checked by the test at every step).
- F1: Up from the tab row reaches the top bar. F17: Up from content lands on the open tab. F12: OK on the open tab
  returns it to its start. F2: Files opens on This Device. F11: Back after playing (or moving along tabs and back)
  keeps the folder and the file.
- F13, F30, F47: black text and icons on the white box everywhere on the page, buttons included.
- F18: the tab row's right end is only "⟳ Scan library" / "Scanning · 42%".
- F14, F19: Internal storage (renamed) with its storage bar.
- F5, F6, F25: List by default, Grid / List, Sort menu (Name · Date modified · Size, tick) beside its button, Order
  (A → Z / Z → A, Newest / Oldest first, Largest / Smallest first), folders first.
- F8, F20, F23, F24: folder counts and In library / Excluded / Not added pills on every item; captions "2024 · 14.2
  GB"; folder mosaics; plain cards; a still from the video for files on this device not in the library.
- F16: long-press (or Menu) opens the item's menu beside it: Play · Details · Mark as watched · Exclude · Fix match /
  Open · Add to library · Scan this folder · Exclude.
- F15, F36, F4: no scroll bars; arrows in the right margin (up faint at the top), none when it all fits.
- Connections (F29–F35, F38–F40): "Put.io · alex" with "WebDAV · 1,204 videos", pills in one column, Open · Edit ·
  Test · Remove; Remove with Undo; Edit and Add a connection open the form as a page in the tab (Type chips SMB ·
  WebDAV · SFTP · FTP, fields by type, "Encryption (HTTPS)" / "(FTPS)", the one keyboard beside it, Test connection
  · Save and browse); found devices Open (browse) or Add (form filled in).
- Library Care (F43–F51): rows on their centre line with pills in one column; None rows do nothing; Missing files,
  Couldn't be read and Not matched open pages in the tab (Find it again · Remove / Try again · Exclude / Fix match ·
  Not a movie); Check matches runs in its row; Scan one source buttons only when focused, one focus, Waiting; Recent
  scans plain with Show all scans.
- F21: Your Library › Picture shows 4K, 1080p, 720p, SD and HDR.
- Definition of done 2: `NoOldScreensTest` fails the build if Files (or Details) names a NOVA or three-panel screen;
  every old route from Files was removed.

**Details** (More pressed in `Review01572DetailsTest`)
- D1, D2: More menu beside More, the sheet's items and wording, an icon each, TMDB page at the foot; D18's idea 2:
  TMDB page pop-up with one line and the code.
- D3: Artwork opens the side panel on the right (Hero and rows · Library page), the hero previewing the backdrop you
  are on.
- D4: the focused tab is the white box alone. D5: season heading focus box. D6: cards no longer clipped at the sides.
- D7: Extras cards the same size as Episodes. D8: service titles open the service's app. D9: no more "No application
  can handle this action"; Netflix gets its TV hand-off.
- D10: description, never the tagline. D11: age rating from the show, else its first episode. D13: hero buttons a
  dark box at about 72%. D14: dates in words. D15: Created by for shows; runtime from episodes. D16: no play icon.
- D17: Back to a library page refocuses the item. D18: Details tab on one screen, Cast and Crew side by side, seven
  each. D19: trailers nearly full screen, no title bar, buttons or controls, HD asked for. D20: service logos in
  their brand colours.

**Player**: P1 strip lower (gap halved); P2 title and metadata stay put; P3 Playback Settings opens on the open tab;
P4 one size for all tabs, 40% of the width; P5 focused rows fill white with black text and icons.

**Background (F27)**: Home and the library pages are the same plain charcoal as Files, Settings and Search.

## Not built (and why)
- **F40 login prompt:** Open on a found device that needs a login says the folder can't be opened; Add is the way
  in. A username/password prompt on Open needs its own small design.
- **D12 (nothing pops in):** not reworked overnight; it needs checking on the Shield (timing, not layout).
- **D15 Rotten Tomatoes and Metacritic:** data from OMDb, which needs your personal key in Settings (parked decision).
- **D9 exact title in every app:** Netflix and Disney+ links are improved but untested without a Shield; service
  titles in More Like This open the app at its start (the exact title link is fetched only for the hero button).
- **Artwork panel "Automatic" tile:** there is no "go back to automatic" in the save path yet, so the tile is left
  out (RULES §10) rather than offering something that does nothing.
- **Delete file warning switches** (Supernova history, Trakt history): the shared delete warning is used without them.
- **TMDB page icon:** no QR icon exists yet; the "external" icon stands in.
- **Video stills for network files:** only files on this device get a still (network ones need the thumbnails
  switch and read part of each file).
- **Details rows running off the right edge (rest of D6):** the clipping is gone; rows still end at the page margin.
- **Scroll arrows on other pages (F36 app-wide):** built for Files; scroll bars are off on the shared page list, but
  arrows are not yet on Settings or Search.
- **Playback tab labels at 40% (P4):** the longest label shortens ("Skip In…"); a slightly wider panel or shorter
  words would fix it.
- **0.158 items** (F42 one side margin, F52 connections grouped by server): as the brief says.

## Judgement calls
See `docs/0.157/JUDGEMENT_CALLS_0157_2.md` (each with its reason).
