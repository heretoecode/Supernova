# 0.157 on the Shield: the owner's review (16 Nov 2026)

The owner installed the signed 0.157 and reported these. Numbered for reference; each is checked against the approved
decisions (`docs/0.157/OWNER_NOTES.md`, `docs/design/RULES.md`). "Approved" = what should be there.

## Working as approved
- App icon and Shield home banner, splash screen, the new loading ring (also on Preparing playback).
- Top bar: the scan ring and dot beside Files (the dot inside the ring when both show); "Files" rename.
- Details: tabs appear sooner; the hero's metadata row is better.
- Preparing playback: layout of its items is right.
- Track lists: plain names with pills (English · Original · SDH).
- The Episodes row shows in the player.

## Problems
1. **Settings lists not on the shared choice list.** Audio & Subtitles › Preferred audio language: radio dots, a
   Cancel button, no search line though over 12 items, old colours. Approved: the one choice list everywhere (tick on
   the right, search above 12, Back closes, charcoal).
2. **Settings rows not centred** as the rules require (everything in a row on its centre line).
3. **Files page is not the approved design.** Space-blue background with a grey panel; one tab only ("This Device");
   a large list of devices; choosing a USB drive opens the older three-panel file browser. Approved: flat charcoal page,
   tab row This Device · Connections · Library Care (+ Your Library). Photos to follow.
4. **Details tab not redesigned.** Same layout as 0.156 (description, key information, reception on top; cast, crew,
   technical information below). Approved: the Details redesign (four kinds, icon headings, label/value rows, full
   description, portrait cast and crew).
5. **Cast and crew photos are square**, not portrait 2:3 as approved.
6. **Details hero flickers:** the artwork and the buttons on the hero card flicker while loading.
7. **Preparing playback: the title logo pops in** and the other items shift when it arrives. Space must be kept for it.
8. **Time box above the seek bar is still cut off** where subtitles sit (reported before; not fixed).
9. **Playback Settings has no tabs.** Still the large buttons along the top. Approved: tabs Subtitles · Audio ·
   Playback, sections with headings, the Playback tab as one plain list with its save row.
10. **"For This Series" missing from Subtitles.** Approved on both Subtitles and Audio (acts like a save button). On
    Audio it sits between Audio selection and Audio adjustment, which is where it was approved; the owner expected it
    at the bottom like a save row (to discuss).
11. **Track pills only inside the lists.** The main Playback Settings page should show the chosen track with its
    pills too (to confirm in the mock-ups).
12. **Lists opened from Playback Settings replace it** instead of sitting on top of it (approved: the panel behind
    darkens and is pushed back).
13. **"Stop playing at" squashed:** the choices sit against the label ("Stop playing atOFF"), and the chosen one is
    blue (no blue for values).
14. **Chapter names not renumbered:** Recap · Scene 3 · Intro · Scene 5 · Scene 6 … Approved: Recap · Chapter 1 ·
    Intro · Chapter 2 …
15. **Episodes row text:** episode 1 says "Episode 1 / Playing", episode 2 says "Episode 2 / Unknown". Should show the
    episode's name and a real second line; "Unknown" has no meaning here.
16. **Question:** are row captions under the cards (approved for Home rows) meant for the player's Chapters and
    Episodes rows too?
17. **Centred chapter row** was approved (Player v11, 13 Nov) but was skipped as "not approved"
    (`QUESTIONS.md` #8).

## From the owner's photos (16 Nov, 10:47)
18. **Files and Settings are drawn inside the old glass panel on the Space Blue background**, not on flat charcoal.
19. **The Files tab row is broken:** only one tab shows, as a full-width focused row; Library Care appears sometimes
    but switches back to This Device as soon as focus moves down. (Approved: a tab row This Device · Connections ·
    Library Care; moving along the tabs switches the page.)
20. **Focused items have pale grey text on the white box** (top bar "Files", the Files tab row). Approved: black text
    and icon on white.
21. **Playback Settings tabs are big pill buttons**, not the tab row with its underline. Section headings sit on the
    left ("Subtitle selection ——"); approved: centred on their line, Title Case. Values are blue (English, Medium,
    Raised 5%, 75%) and the tick in Subtitle track is blue; approved: no blue for values.
22. **Subtitle track list opens on its own** at the top of the screen; Playback Settings vanishes (see 12).
23. **Seek time box is the old style** and is cut off at its top ("00:03:59 / Scene 6"); approved: the card with the
    time over the chapter name. The Chapters / Episodes tab row shows as a small "Chapters" button.
24. **USB drives show a document icon** on This Device.
25. **Choosing a drive opens the older three-panel file browser** (Locations · folder list · Information). To check
    against the approved Files design for browsing a storage.
26. **Details › More › Change info of all episodes still opens the old side panel.** Decided 12 Nov: one Fix match,
    on Files — from anywhere it goes to Files › Library Care's Fix match page for that title, Back returns; the 0.156
    side panel is retired. The Fix match page is not in the code.
- Photos are taken at an angle, so row alignment can't be judged from them; check in screen renders.

**Owner decision (16 Nov):** "For This Series" sits at the **bottom** of both the Subtitles and Audio tabs, as a save
row (replaces its approved place between Audio selection and Audio adjustment).

## What this means
Much of the approved 0.157 redesign is not in the build, although the build passed every test: the tests check
behaviour, not how screens look. Next: an audit of every approved 0.157 design against the code (built, partly,
not built), then a fix pass that checks real screen renders against the mock-ups before anything is called done.

## Fix progress (Claude, 16 Nov)
Renders are real Android screens from `preview-renders.yml` (branch `previews/claude`, folder `normal/`; latest of
commit 6885119), compared by eye with the named mock-up. ✓ = fixed and checked in a render. ◐ = fixed; behaviour or
data a still render cannot show, checked by code and a unit test instead.
- **1** ✓ Settings lists on the shared choice list (search above 12, flags, tick right, no Cancel). `0157-settings-audio-language-list` vs `choice-list/01`–`03`.
- **2** ✓ Settings rows on their centre line; switches white on / dark off (no blue). `1080p-playback` vs `settings-flat/01`.
- **3 / 18 / 19** ✓ Files on flat charcoal, tab row This Device · Connections · Library Care · Your Library, 96 px
  margins and rows. `0157-files-tab-0…3-content` vs `network-files-tabs/39`, `44`. Transfers waits for 0.158 (plan).
- **4 / 5** ✓ Details tab v20: three columns, icon headings, faint row lines, Cast and Crew as 2:3 portraits, plain
  underline tab row, hero with the white edge. `0140-reference-fixture-1920-1.0`, `movie-details` vs `details-background/21`.
- **6** ◐ Hero flicker: buttons rebuilt only when the offer changes; artwork changes once (shade decided first, no second fade).
- **7** ◐ Preparing playback keeps a fixed two-line space for the logo.
- **8 / 23** ✓ Seek time box is the card (time over chapter), never clipped; Chapters / Episodes on the underline tab
  row. `next-hud-1920-1.0` vs `player/27`.
- **9 / 13 / 21** ✓ Playback Settings: underline tabs, chips (no squash, no blue), plain Playback list, centred Title
  Case headings, white values and tick, sized to its tab. `0152-playback-side-panel-tab-0…2`, `playback-playback-tab` vs `player/25`.
- **10** ◐ For This Series at the bottom of Subtitles and Audio (render fixture has no series); Playback tab save row
  for format and skipping added (unit test `SeriesPlaybackChoicesTest`).
- **11** open: QUESTIONS #12 (track pills on the main page, awaiting your yes).
- **12 / 22** ✓ Track lists open on top of the panel, panel pushed back and darkened. `playback-subtitle-track-list` vs `player/28`.
- **14** ◐ Generic chapter names renumbered (Scene 3 → Chapter n); unit test `PlaybackPartsTest`.
- **15** ◐ Episodes cards: the episode's name, second line "Playing · Episode 2 · 46 min", never "Unknown"; `PlayerPartsStripTest`.
- **16** answered: no captions under player cards (QUESTIONS #10).
- **17** ◐ Chapter row centred between tab line and bottom edge (equal spacing in code).
- **20** ✓ Focus ink black on white (top bar, tabs, rows and icons). `0157-files-topbar-focused`, `0157-files-tab-0-content`.
- **24** ✓ USB-stick icon. `0157-files-tab-0-content`.
- **25** ✓ Browsing a drive stays in Files as the folder grid. `0157-files-folder-grid` vs `network-files-tabs/32` (List view not yet; QUESTIONS #14).
- **26** ✓ One Fix match page; Details › More says "Fix match". `find-a-match` vs `network-files-tabs/14b` (QUESTIONS #13).
