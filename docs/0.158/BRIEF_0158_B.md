# Brief: 0.158 part B (for Supernova Coding), to run overnight

Approved by the owner, 19 Nov 2026. Part B = the new look (decided 18 Nov), the owner's review of 0.157.2
(`docs/0.157/REVIEW_0157_2.md`), the episodes feature and **the Settings redesign** (moved here from 0.159 by the
owner, 19 Nov: the last major redesign). The owner has 0.158 (part A) installed and will review it separately; their notes on
it come later, not in this brief.

## The aim for tonight
**The owner wakes up to a signed APK.** Follow CLAUDE.md › Working unattended to the letter:
- **Never sit idle while a GitHub build runs.** Dispatch the conformance run in the background, carry straight on with
  the next item, and look at the run only when it has finished. A failing run is read with
  `report-test-failures.yml`, fixed, and dispatched again, while other work carries on.
- **Don't stop for questions.** Use the rules, the page sheets and the review; write every judgement call down with its
  reason (`docs/0.158/JUDGEMENT_CALLS_0158_B.md`).
- **Push after every finished item.** Check `git ls-remote` matches HEAD after each push.
- **Sign as you go, so there is always something to install.** Signing has standing approval (CLAUDE.md). When a
  group below is finished and its conformance run is green, sign it: the first signing is **0.158.1** (code 1581),
  then 0.158.2 (1582) and so on if more groups are signed later in the night. The last signed APK is the one the owner
  installs; the report says what each one contains. If the night is nearly over, sign whatever is finished and green
  rather than nothing.

## Before you start
1. **Merge `claude/design-planning-discussion-9poejm` into the working branch.** It holds everything decided on 18
   Nov: this brief, RULES changes, the review notes, the mock-ups. Part A only merged it up to 17 Nov.
2. Read: `CLAUDE.md` (Definition of done, Working unattended); `docs/design/RULES.md` §1 (background), §3 (one shape
   for small boxes), §16 (Back and page position), §19 (titles); `docs/0.157/REVIEW_0157_2.md` (every PP, PL, PS, DT,
   FL, R and X item; "[ ]" marks Claude's recommendation, "Agreed" / "Decided" / "Approved" are the owner's); the page
   sheets in `docs/design/pages/`; `docs/ROADMAP.md` › 0.158 part B.

## Order of work (finish each group, with everything behind it, before the next)

### Group 1 — the new look (first, so everything after it is built and checked on it)
- **Speckled background app-wide** (RULES §1; `docs/design/proposals/background-speckle/tv3-2-current.png`, recipe in
  `tv3.html`): base `#111215`, about 20 grains per 1,000 pixels, mostly 7–22% white, about 1 in 7 at 45%, fixed
  seed, drawn once at the screen's own size, never smoothed, never moving. Through the shared page background
  (`PreviewUtilityBackground` paints charcoal today); pages that paint their own charcoal (Files, the folder browser,
  the top bar's solid strip, the library pages' fade under the top bar) use it too. **No pane behind lists.** Panels,
  menus and caption boxes stay solid. The player keeps the video behind it.
- **One shape for every small box** (RULES §3; `background-speckle/corners-A-B-C.png`, B): buttons, pills, chips and
  the focus and open boxes in tabs and the top bar: 52 px high on a row, **20 px corners**, about 22 px side padding.
  Rows, cards and heroes keep their own corners.
- **Text centred on its letters in every box** (the 0.158 top bar fix, applied everywhere with the new shape).
- **Look sweep:** render every page on the new background (`preview-renders.yml`) and fix only what now fails: faint
  row lines, grey second lines, "Unknown", the open-page grey box, outlined pills and chips at rest, panels over the
  page, the keyboard's keys, loading placeholders, dimmed rows. **Playback Settings panel colour** (P6 / PS10) is
  settled here: `#16181C` at **99%** (owner, 19 Nov; RULES §11), no outline, no glow, no old blue. **Every**
  pop-up, menu and side panel uses the same value (one shared value). List every change in the report.

### Group 2 — Playback Settings (the owner's worst page; PS1–PS11)
Every item in the review: tab focus (PS1), gap under the tabs (PS2), every tab fits (PS3), focused-row switches,
pills and chips (PS4–PS6), subtitles on top at every position (PS7), the track list on top with the panel behind
darkened at about 96% and the same width (PS8), and all of PS11 (chips instead of ‹ › steppers, − + for Vertical
position, names on the two nameless rows, Audio boost chips, Extra large, Subtitle delay in Subtitle Selection, For
This Series wording, the Download subtitles list). Then PS9: render the Playback tab and tidy what is left.

### Settings wording seen on the Shield (owner, 19 Nov, 0.158.3)
The Playback tab's skip rows still show NOVA's names "Play normally · Show skip (5 s) · Auto skip · Smart". Use the
sheet's: **Off · Show button · Skip · Skip when binge-watching** (SETTINGS.md › Playback › Skipping). Check every
Settings row's wording against the sheet while finishing Settings.

### Playback Settings, round two (owner, 19 Nov; build this before anything else still open)
`player/33-v15-playback-settings-step.png` and REVIEW_0157_2 PS9: panel **820 px**, 99%, rows 62 px, every tab fits
with no scrolling, every row on one line with its value on the centre line; **no line under the tab row**; named
choices step with Left / Right (‹ › only on the focused row) on Text size, Background opacity, Audio boost, Format,
Speed, Stop playing at, Still watching?, Play mode; **Skipping** (renamed from "Skip intros & credits") keeps its three chips **Off · Show button · Skip** (was "Automatic"; the same words as Settings); numbers keep − +; "This Show"
heading with **"Remember for this show"**; Playback tab headings Picture and Speed · Playing On · This Show. Replaces
the two-line rows (judgement call 9).

### Group 3 — crashes and engine (from the diagnostics read, X1–X5)
X1 (guard the discovery listener), X2 (Scan this folder crash), X3 with PL7 (seek previews engine fixed and the
switch back where Settings shows it), X4 (scans queue instead of waiting on a lock). X5 is covered by the focus fixes
in the groups around it.

### Group 3b — the Home hero (owner's first look at 0.158, `docs/0.158/REVIEW_0158.md`)
- **H1:** the Home hero and the Details hero are not the same size on the Shield. Make them one size and place,
  1776 × 820 at 1080p (RULES §14, HOME.md); check both in renders side by side, pixel for pixel.
- **H2:** no carousel dots under the Home hero. Show them (HOME.md: the current one a longer pill, gliding when the
  hero changes; `PreviewFeaturedIndicators` exists); check in a render that they are visible on the hero.

**Good point to sign 0.158.1** if groups 1–3b are green.

### Group 4 — Preparing playback and the player (PP1–PP3, PL1–PL8)
Badge first on the details line, no reasons line, space kept from the start (PP1, PP2); one title block for
Preparing playback and the HUD (PP3, PL1); clock font (PL2); gaps (PL3); controls linger (PL4); no stub chapters
under 10 s (PL5); card edges (PL6); skip button in line with the controls (PL8, `player/29`, `30`).

### Group 5 — Details (DT1–DT9, R3, R4, R5)
Details tab on one screen, one size for cast and crew portraits, values ("2 h 3 min", "$25 million", "Arabic"),
tab focus, rows running off the edges, gaps (DT1–DT4, DT9); **one row design for Episodes, Extras and More Like
This, More Like This in rows by service with the service's colour logo** (DT5, `review-0157-2/01` 1–2); artwork
side panel (DT6); full description is the text alone (DT7); Fix match in More (DT8); season artwork (R3); season More
menu beside its button with Delete season (R4); **choose a version on Play** (R5, `review-0157-2/01` 4).
**Episodes** (owner, 18 Nov; `docs/0.158/OWNER_NOTES.md` §2; no mock-up, built from the rules): OK on an episode
card in the Episodes row plays that episode at once; its long-press (or the Menu button) opens **the same menu as
More**, beside the card, as library posters do. **Deleting an episode** when earlier episodes of the same season are
in the library: one delete warning in Supernova's design (RULES §23.5: Keep file first), saying "You also have
episodes 1 and 2 of this season", with **Delete episode 3 only · Delete all three · Choose…**; Choose opens the shared
choice list with ticks and **Select all** at its top, then the same warning for what is ticked. Nothing earlier is
deleted without that step. List the screens in the report with renders, as they had no mock-up.

### Group 6 — titles (RULES §19)
Official logo with the **placement fix** (`title-style/05-logo-placement.png`: empty space trimmed, same area inside
900 × 240, foot 24 px above the details line, soft shade from the left); titles without a logo in **house style A**
with C's colon split (`title-style/04-house-style-lengths.png`).

### Group 7 — Files (FL1–FL12)
Content clipped below the toolbar and tabs (FL1); loading placeholders back (FL2); **turn a connection off** with the
smaller buttons (FL3, FL5; `review-0157-2/01` 3); connection form on one screen (FL4); FL6–FL12 as written (FL10
agreed: focus moves through the scans; FL12 decided: closed fully → Home).

### Group 8 — behaviour and leftovers (before Settings)
**Back and page position** (RULES §16): Back goes straight to the top bar; a page left for another one reopens at its
top; poster → Details → Back returns to the poster. R2: if the own library page still shows a solid bar under the
top bar, make it match Movies and TV Shows. The icon picker tile width (part A's "not built").

### Group 9 — the Settings redesign (the last major redesign; owner, 19 Nov)
**Page sheet: `docs/design/pages/SETTINGS.md`.** Designed and approved by the owner on 14 Nov: `docs/0.157/SETTINGS_INVENTORY.md` (every setting: keep, move, merge,
reword, remove; seven tabs **Playback · Audio · Subtitles · Library · Services · System · About**; a full page with
tabs like Files, no tiles, no panel) and the mock-ups `docs/design/proposals/settings-v2/` (the latest round of each
screen: `b2`–`b5`, e.g. `16-b3-your-services`, `18-b3-backup-choose-restore`, `23-b4-restore-progress`,
`24-b4-about`, `25-b4-credits-licences`, `27-b5-licences`; `07`/`08` for OpenSubtitles), `docs/design/ABOUT_SETTINGS.md`,
`docs/design/BACKUP_RESTORE.md`, RULES §8 and §24.
- **Build from the page sheet `docs/design/pages/SETTINGS.md`** (written 19 Nov from those sources; it lists what is
  newer than the pictures). Where the sheet is silent, follow the sources and log the judgement call.
- Then build it on the new look (speckle, one shape for small boxes), every action pressed and checked in a render,
  the old Settings screens added to the no-old-screens test.
- Onboarding (which points into Settings) stays out: the owner kept it for later. Media Transfers' settings stay out
  (no version yet).

## Not in part B
Users and tags, Guest (still being designed). Onboarding (later). The Trakt list row and Media Transfers (Ready, no version yet).

### Owner's notes on 0.158.3 / 0.158.4 (19 Nov afternoon; `docs/0.158/REVIEW_0158.md`, end) — do these next
- **D1, deleting (first):** "Delete all episode files" on an SFTP show reported "couldn't delete file" though the
  file was deleted; the folder became Excluded; re-adding left the pill on "Not added"; a ghost episode stayed in
  the library and Missing files never showed it; deleting it gave an Android-style "Delete failed". Fix all five:
  delete never excludes a folder; one Supernova notice says what was deleted and what failed, and why; pills
  follow the real state after Add; an entry whose file is gone is caught as missing (and offered Remove); no
  Android notices. Delete all episode files deletes every episode file of the show that the library knows, and
  says if others remain in the folder. Read the diagnostics for 14:30–14:45 if the owner sends them; add tests.
- **A1, About:** the About tab shows only the old blue ring, the version and build line. Build it from the sheet
  (SETTINGS.md › About: What's New with the last five and All versions, Credits, Open-source licences) with the new
  mark. "Based on NOVA …" says the version the app is really built from.
- **H3, Home hero: one button, More** (owner, 19 Nov; replaces HOME.md's Play then More): its menu starts Details,
  then Play / Resume, Library, Files & Media; Left / Right change the hero at once. Update HOME.md.
- **H1 again (owner, 19 Nov, firm: the approved design):** the Home hero and the Details hero are **one size and one
  place, 1776 × 820 at 1080p, top 112 px**; on 0.158.4 they still differ on the Shield. Measure both on 1080p
  renders pixel for pixel, fix the one that's off, and add a test that lays out both pages and fails if their hero
  bounds differ by a single pixel. The size stays. **Below it (owner, 19 Nov):** at rest, Home must show that there
  is more below the hero: at least the first row's heading, any amount of its cards (5, 10, 20% all fine). Never the
  hero alone. Add that to the test (the first row's heading is on screen without scrolling).
- **DT3 again:** the Details hero's edge is cut off left and right; focused cards in Episodes (left) and More Like
  This (both sides) are cut off.
- **R2, own library page (owner, firm; RULES "Library pages under the top bar"). Cause found (discussion, 19 Nov):**
  `TopNavigation` decides **once, in its constructor**, whether the page may scroll behind the bar:
  `prepareScrollingContent(content)` looks for a `PreviewPages` (or `ScrollView`) inside the content, descending
  only through `FrameLayout`s. If it finds one, the stage is laid out over the whole screen and the page gets the
  bar's inset; if not, the stage sits **below** the bar, which is the solid band. `CustomLibraryActivity` builds
  `new TopNavigation(this, workspace, …)` **before** `workspace.render()` creates the `PreviewPages`, so the
  workspace is empty at that moment; and even later its `PreviewPages` sits inside a `LinearLayout`
  (`libraryBody`), which the search never enters. Movies and TV Shows pass a ready `PreviewPages`, so they work.
  The fade listener being wired (what was checked before) is not the problem.
  **Fix:** let the search walk any `ViewGroup`, and re-run it (inset included) whenever the content is rebuilt
  (`Workspace.render()` removes and re-adds views), e.g. a `TopNavigation.refreshScrollingContent()` called at the
  end of `library()`, or a hierarchy-change listener on the stage. **Prove it:** a Robolectric test that the own
  page's stage is laid out from y = 0 with the inset on its `PreviewPages` (it fails today), and renders of Movies
  and the own page scrolled, side by side.
- **H6, next episode across seasons (owner, 19 Nov; HOME.md › TV shows on the hero):** `HeroEligibility.sequential`
  refuses episode 1 of a season. Allow it when every episode of the previous season in the library is watched and the
  new one was added after the last watch ("Next Episode · S3 E1"). No time limit. Unstarted shows stay candidates
  as the show. Add tests for both.
- **A2, app icon and banner (owner, 19 Nov, approved):** the mark on the speckle's base `#111215` instead of
  charcoal, **no grain** (it reads as dirt at icon size). Icon, banner and splash; re-export from `design/brand-mark/`.
- **Progress badge and row design A everywhere (owner, 19 Nov; RULES §14c; mock-ups `rows-captions/`):** remove
  every progress bar from cards, posters and captions (library posters, Home rows, Details Episodes / Extras / More Like
  This, the player's Chapters and Episodes rows, Search) and draw the one shared badge instead: a 48 px white circle top
  right, a black pie filling from 12 o'clock inside a white rim, the black tick when watched, nothing when not started.
  Row cards: title and line over the artwork's foot (no caption box under). Library captions: three lines, no strip, a shorter box with the lines spread
  evenly (LIBRARY.md, `rows-captions/08` After).
  One shared view for the badge, used everywhere; a test that no card in the app draws a progress bar. The player's
  seek bar and task progress in rows stay.
- **Artwork line and focus ring (owner, 19 Nov, judged on the TV; RULES §14):** the line at rest solid white, 1 px at
  1080p (`ArtworkEdge`: 0.5 dp at full white, was 0.75 dp at 60%); the soft shade unchanged; **every** artwork focus
  ring solid white 4 px (2 dp, was 3 dp / 6 px), one shared value for posters, row cards, the player's rows and
  anything else with the ring.
- **Menu and panel edge (owner, 19 Nov, judged on the TV; RULES §11; `rows-captions/tv/05` no. 3):** every pop-up,
  menu and side panel keeps `#16181C` at 99% and gains a white outline at 70% (about 1.5 px at 1080p) and a dark
  shadow spreading outwards (black about 85%, about 48 px soft, 18 px down). One shared look, so all change together.
  Playback Settings over video: leave as it is until the player walk-through.
- **Long-press menu and delete warnings (owner, 20 Nov; LIBRARY.md › Long-press, Delete warnings; RULES §7b;
  mock-ups `rows-captions/tv/11`–`18`):** build the warnings exactly as the sheet says, for Delete file, versions of a
  film and Delete all episode files: Keep focused first; "This can't be undone." only beside a focused Delete; the
  ticked list with a Select all switch, facts line, Not in library pill, four rows visible with scroll arrows; Clean Up
  with the three switches (folder switch on by default and only removing an empty folder); totals; the button names
  the count. This replaces D1's "one notice" wording: the list is how nothing gets left behind. Times as "2 h 22 min".
- **Empty library pages (owner, 19 Nov; LIBRARY.md › Empty pages):** "No movies yet" (never "films"), "Add a folder,
  a drive or a network share and your movies will appear here." (TV shows likewise); while the first scan runs, the
  shared spinning loading ring, animated as on Preparing playback, **no grey circle behind it**, and "The first ones
  will appear here in a moment." The film and page icons too: plain white, **no circle**.
  Today's top bar, speckle and 52 px button throughout.
- **Poster caption colour and pie rim (owner, 19 Nov, judged on the TV; `rows-captions/tv/10`):** the caption box under
  library posters solid `#141619` (`PreviewCardPresenter.POSTER_CAPTION_FILL`, was `#23262C`); the progress badge's white
  rim 3 px (the black pie 42 px across inside the 48 px circle).
- **Library toolbar sizes (owner, 19 Nov, judged on the TV; LIBRARY.md › Toolbar; `rows-captions/tv/08`):** buttons in
  the one 52 px small-box shape (today the toolbar forces about 88 px: `PreviewToolbar` min height 44 dp and 12 / 6 dp
  padding override `SupernovaStyle`); the line white at 35%, about 2 px, thinning to a hair at both margins; 24 px
  from the focused box to the line and 24 px from the line to the posters.
- **Library toolbar (owner, 19 Nov; LIBRARY.md):** remove **Customise page** from Movies and TV Shows (it only repeated
  Filters' Show and the toolbar's View, Sort and Order); it stays on your own pages.
- **S1 (owner, 19 Nov):** Settings' tabs take **Files' tab look** (same size, a line under the tab row, white focus
  box; RULES "Page tabs"), not the plain row; wait for the Settings mock-up before building. **S3:** scroll arrows on Settings tabs that
  scroll (RULES §16).
- **FL2** (already in group 7): placeholder rows instead of "Looking in this folder…".
Not for Coding yet (design to settle first): L1 caption space, R6 one row design, S2, A2, Files' tab underline.

### Reports and Backups folders (owner, 19 Nov: Claude's recommendation)
- **Reports:** always saved to Download › Supernova; no Reports folder row to change it.
- **Backups:** Supernova's one folder picker (RULES §7b), with Android's folder grant shown once behind it the first
  time a folder outside Download is chosen. Default Download › Supernova. Built with the in-row backup flow.

### NOVA catch-up (checked 19 Nov; approved 15 Nov: "all of NOVA 6.6.5 and later", 0.157 notes §35)
NOVA's latest tag is 6.6.5 (10 Nov) with fixes since. 0.157 carried MediaLib's and FileCore's 6.6.5 changes
(matching by file name, Trakt pagination, 64-bit list IDs and lastActivities2, SMB share credentials, messenger
folders skipped, Samba probes) and the software-decoding notice and decoder order. **Still missing, app side (bring
in by hand, our fork is heavily changed):**
- `aos-Video 567d53b` **HDMI refresh-rate switch pause race** (18 Nov): matters with Automatic refresh rate on.
- `e13f8a3` / `68e316f` **picture subtitles (PGS / VobSub): vertical position, kept clear of the controls**; and
  `f67e64a` **their size (scale)** (17 Nov). In our Subtitle Appearance as two rows that show only for a picture
  subtitle track, Vertical position (− +) and Size (chips), same look as the text rows; note the native half below.
- `8a1a231` duplicate Samba discovery at start-up (app side; the FileCore side is in).
- `565ff59` subtitles' default shadow restored (check against our Outline / Background defaults first).
- `aos-MediaLib` 14 Nov **lingering local import notifications** (may be behind the stuck "Scanning…" pill).
Not for a TV, skip: phone cutouts, rotation, double-tap zones, status bars, Android 6 SPDIF, empty playlists.
**Native engine (not now):** ours is aos-avos at 4 Nov (`5cf3ae9`), 29 commits behind: Dolby Vision profile 7 → 8.1
conversion and profile 5 reshaping (libdovi), the native half of the picture-subtitle position and size, Dolby
Vision HDR metadata. That needs a new engine archive build: the roadmap's "Newer NOVA engine stack, as its own
version". Until then, build the two subtitle rows only if the old engine honours them; otherwise list them as not
built.

### Release notes and the front page (owner, 19 Nov; do with every signing from now on)
- **Every signed APK gets its own release notes.** `docs/0.158/RELEASE_NOTES.md` (written 19 Nov for 0.158 to 0.158.5)
  has one section per APK, newest first: add each new one there when it is signed, every fix and tweak in plain words.
- **What's New in the app: one entry per APK,** each with its version and date (0.158.5, 0.158.4 … 0.158, then 0.157.2,
  0.157.1, 0.157), not every point release folded under one entry. Change the build check that only accepts whole
  versions in the history list so point releases are accepted. 0.157.1's notes sit unlabelled in the 0.157 entry
  ("## Every page", "## Files" …): give them their own 0.157.1 entry. The app's 0.158 and 0.158.1 notes miss a
  few things the file has (the Home hero, Stream more from the world of, connections by server; the hero size and
  dots, the three crash fixes): match the file.
- **Front page:** with every signing, fast-forward `main` to the signed source and update the README's "Current
  version" line (main was brought to 0.158.4 on 19 Nov with a new README; this branch holds that commit).

## Finish with
**The review build (owner, 19 Nov):** the owner will not review on the Shield until part B is finished; they will
install the last signed APK and walk the whole app. So finish every group, sign the final build, and put one line at
the top of the report: "**Review build: 0.158.x** (run …)". The discussion session then makes the whole-app
walkthrough checklist from that exact build.

`docs/0.158/REPORT_0158_B.md`: what each signed APK contains, the look-sweep changes, the "not built" list (plain,
for the owner, before they install), the judgement calls file, the run links; update `PROJECT_STATUS.md` and the
roadmap. The owner will send a fresh diagnostics report from 0.158; it is read in the next session.
