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
- **Undo notice and Mark as watched (owner, 20 Nov; RULES §7b; LIBRARY.md › Long-press):** the shared Undo notice gets
  a film / TV show icon left of the title; two buttons, **Understood** (focused, closes it, focus to the next poster)
  and **Undo**; more height so its 6-second line clears the buttons; Back or 6 seconds close it, focus to the page; Mark as
  watched has no notice and its menu row flips to Mark as unwatched on a watched title.
  (The "To fix" row in Library Care is approved but waits for the Files review: do not build it yet.)
- **Top bar clock (owner, 20 Nov; cause found in discussion):** on Home, Movies, TV Shows and Files the clock is thinner
  than the words; on the own page, Search and Settings it matches. Cause: `overlay/Overlay.java` (NOVA's old leanback
  overlay, created by `MainFragment`) **removes `TopNavigation`'s own clock (`preview-default-clock`) and puts its own
  in**, set to `sans-serif-light`, an unscaled 19 sp and `setIncludeFontPadding(true)`, so it is lighter, a different
  size and not centred on its digits. The other pages have no Overlay, so they keep the right clock. Fix: Overlay
  keeps `TopNavigation`'s `BarClock` (one font, weight and size for every word and the clock, RULES §2) and only moves
  the scan progress; test that every page's clock has the bar's typeface and size. **Better (owner, 20 Nov): remove
  NOVA's old Overlay altogether** (`overlay/Overlay.java`, its `leanback_overlay` layout, `Clock`,
  `ScannerAndScraperProgress` use in `MainFragment`): its only visible effect today is the wrong clock, because the top
  bar's own scan signal replaced its progress pill (`TopNavigation` keeps `scanStatus` GONE). Check the scan ring beside
  Files still shows scans after it goes.
- **Top bar spacing (owner, 20 Nov):** the gap from Settings to the clock looks bigger than Files → Search → Settings.
  The clock sits right-aligned in a `status` box at least 64 dp wide, wider than "18:40", so spare room lands on its
  left. **One gap for the whole bar (owner, 20 Nov):** the space from word to word is the same everywhere: Home ·
  Movies · TV Shows · your page on the left, and Files · Search · Settings · the clock on the right, measured between
  the letters as the eye reads them (the boxes' padding included), on every page; check in renders, and add a test.
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

### Library toolbar review: Filters (owner, 20 Nov; mock-ups `library-pages/tv/35`–`42`; LIBRARY.md › Toolbar)
- The Filters menu (Watched · Genre · Year · Streaming services, Clear filters apart by a line) and the Streaming
  services list: approved as drawn in `tv/35`, `39`, `40`.
- **Watched list icons = the poster badge** (`tv/41`): All a grid of four posters; Unwatched an empty white ring;
  In progress the white disc with the black pie; Watched the white disc with the black tick. On the focused (white)
  row the disc keeps a thin dark rim so it still shows. Use the shared badge drawing, not separate icons.
- **Icons (owner, 20 Nov; `tv/42`):** the Filters button uses **sliders** (option D); with filters on, the button
  reads **"Filters · 2"** (the number of filters in use: Watched not All, Genre, Year not Any year, Streaming
  services each count one); the icon stays the same. **Genre: shapes** (circle, square, triangle, diamond; B). **Match:
  ticked list** (B).
- **Year list (owner, 20 Nov; `tv/43`):** **Any year** (the off choice, ticked when no year filter is on; second line
  the library's span and count) · **Recent** (the last 5 years, span and count) · **Your Own Range** with **From** /
  **To**, Left / Right stepping only through years that hold titles. No decades, no This year.
- **Bug, Order button width (owner, 20 Nov):** the button grows when it reads "Newest first" and shrinks for "Oldest
  first", pushing List view along. Cause: `toolbarButton` wraps its text (`-2` width) and Roboto is proportional:
  "Newest first" is about 128 px at 1080p, "Oldest first" about 116 ("N" and "w" are wide, "l" and "d" narrow);
  "Smallest first" / "Largest first" differ by 13. **Fix:** give the Order button a fixed width, measured as the
  wider of its two words for the current sort (`Paint.measureText` on both `orderLabel(key, true/false)`), contents
  centred, so flipping the order never moves anything. It may change width only when the sort changes. Test: both
  states lay out to the same width and List view's x does not move.

### Library toolbar review: Sort (owner, 20 Nov; mock-ups `library-pages/tv/44`–`46`; LIBRARY.md › Sort)
- **Group order: General · Trakt · Series · Files** (Files last; Series only on pages holding TV shows).
- **Removed:** Container, Path, Bitrate, Codec, HDR, Audio, Date modified (owner, 20 Nov).
- **Two dates (owner, 20 Nov):**
  - **Date added** = when the title first came into the library: a film's file; a show's **first** episode. Newest
    first puts the newest arrival top left and the title you've had longest bottom right. **Change:**
    `PreviewLibraryLoader` gives a show the newest episode's date (`newest = max(e.added)`); Date added must use the
    earliest (`min`). Caption line 3: "Added 3 Dec 2025".
  - **Last episode added** (Series group; shows only, hidden on Movies) = when a show already in the library last
    gained an episode: the newest episode's date (today's `max`). Caption line 3: "New episode 18 Nov 2026". On mixed
    pages films fall back to their Date added. Order words Newest first / Oldest first.
  - Home's Recently Added row keeps using the newest arrival (a new episode lifts its show there); unchanged.
- **Reworded (owner, 20 Nov):** Seasons → **Number of seasons**, Episodes → **Number of episodes** (how many the
  library holds).
- **Icons (owner, 20 Nov; `tv/47`):** Date added = **an arrow into a tray** (A); Title = **A–Z with an arrow** (B);
  Number of seasons = **numbered list** (B). Number of episodes keeps the screen with a play mark.
- **Resolution removed too; one Size (owner, 20 Nov; `tv/48`):** File size and Size per episode become **one sort**:
  a film by its file size, a show by its size per episode (a show's total is dropped: it only grows with the episode
  count). A show's figure is the **average**: the episodes' total size divided by the episodes the library holds.
  **Caption line 3 (proposed `tv/49`):** a film "24.6 GB"; a show "**2.2 GB each · 8.8 GB**" (the sorted figure
  first, then the total). Replaced by the wording below. Order words Largest / Smallest first.
- **Six posters a row (owner, 20 Nov; `tv/50`; replaces seven):** posters about 276 px wide at 1080p (was 236), gaps
  32 px, first and last on the side margin, artwork 2:3 (414 px tall), caption box unchanged in height. Every library
  page. Test: six posters a row on the 1080p layout.
- **Size caption (owner, 20 Nov; `tv/50`):** film "24.6 GB"; show "**Average 2.2 GB · total 8.8 GB**" (sentence case:
  only the first word capitalised). The sort is called **Size** on every page. Sort approved: General (Date added ·
  Title · Release date) · Trakt (Trending · Popular) · Series (Last episode added · Number of seasons · Number of
  episodes) · Files (Running time · Size).
- **Final Sort (`tv/48`, approved 20 Nov):** General (Date added · Title · Release date) · Trakt
  (Trending · Popular) · Series (Last episode added · Number of seasons · Number of episodes) · Files (Running time ·
  Size). Ten at most; never scrolls. Last episode added's icon: a screen with a plus.

### Library toolbar review: list view (owner, 20 Nov; mock-ups `library-pages/tv/51`–`54`)
- Approved: the progress pie / watched tick at the right edge of the Title column (same badges as the posters),
  with **about 32 px between the title's last letter (or "…") and the badge and 40 px between the badge and the next
  column** (owner, 20 Nov; `tv/55`).
- **Row lines (owner, 20 Nov; `tv/54`, `tv/55`):** white at **20%**, 1 px, **fading out at both ends** (clear over the
  middle, gone at the row's edges); never as strong as the toolbar line (35%, 2 px). None under the last row or
  beside the focused row.
- **Proposed, waiting for the owner:** columns named for what they hold, one figure each: **Average running time** ·
  **Total running time** · **Average size** · **Total size** (films: their own length and file in both; shows: per
  episode and all episodes). Defaults Average running time, Average size, Total size.
- **Scroll arrows** in the right margin on library pages, grid and list (RULES §16): up faint at the top, down at the
  foot; check they are built.

### Library toolbar review: Columns (owner, 20–21 Nov; `library-pages/tv/56`–`58`)
- Column names = the Columns menu's names = the headings (owner: headings mimic the menu).
- List (16; owner, 21 Nov: approved): Release date · Date added · Last episode added · Number of seasons · Number of episodes ·
  Average running time · Total running time · Average size · Total size · Resolution · HDR · Audio · Video codec ·
  Bitrate · Container · Location (Date modified dropped, as in Sort).
- **At most seven columns besides Title (owner, 21 Nov: approved)** (16 at once is unreadable at 1080p, `tv/56`): the Shown heading reads
  "Shown · 7 of 7"; while full, Hidden rows are dimmed until one is hidden. The menu scrolls with the arrows in its
  margin (`tv/57`, `58`). Shown in column order (OK picks one up, Up / Down moves it), Hidden A–Z, Reset to default.
- **Names follow what the page holds (owner, 21 Nov):** wherever TV shows can appear (TV Shows; an own page whose
  Includes has TV shows, alone or mixed with films) the pairs read Average running time · Total running time ·
  Average size · Total size. Where only films can appear (Movies; an own page that includes Movies only) they read
  **Running time** and **File size** (a film's average and total are the same). Changing Includes renames them live.
- Defaults (seven at most): own page Release date · Number of episodes · Average running time · Average size · Total
  size · Resolution · Date added; Movies Release date · Running time · Resolution · Audio · File size · Date added;
  TV Shows Number of seasons · Number of episodes · Average running time · Average size · Total size · Last episode
  added.

### Library review: Customise page (owner, 21 Nov; mock-ups `library-pages/tv/59`–`68`; approved)
- The panel on today's rules (`tv/59`): no "This Page" heading; Genres shapes icon; Years opens the Year list (value
  "From 1990"); quiet "Any", bright set values; Sort by a row opening the Sort list on top (`tv/61`); Order chips
  follow the sort (two choices stay chips).
- **More than two choices step with Left / Right (`tv/68`; new RULES §8):** Includes ("Movies and TV shows" · Movies ·
  TV shows), Minimum rating (Any · 6+ · 7+ · 8+ · 9+) and Show (All · Unwatched · In progress · Watched); ‹ › only on
  the focused row; values line up at rest. The panel narrows to about 700 px.
- **How To Show It order (owner, 21 Nov):** Sort by · Order · View · Show (so the rows' widths don't climb like a
  staircase; Sort by and its Order sit together).
- **Delete this page (`tv/64`, `tv/65`):** no pop-up; while focused the row shows "Your videos stay in your library."
  on its right; OK deletes at once with the shared **Undo notice** ("Documentaries page deleted" · Understood · Undo);
  the top bar shows + and the "Make this page your own" page.
- **Keyboard (`tv/66`; RULES §15 changed):** the shared keyboard (its own layout, Phone key included) floats over the
  panel, its top straight under Page name, the panel's width; Done returns to the row.
- **Last episode added icon (`tv/67`): A**, a screen with a play mark and a filled "new" dot top right (Sort, Columns).
- **App-wide, from RULES §8:** any Settings row with three or more chips becomes a Left / Right value when next built;
  list each change in the report. **Not Playback Settings' Skipping** (Off · Show button · Skip): leave it as chips
  until the owner reviews the Playback Settings page (owner, 21 Nov).

### Home review (owner, 21 Nov; mock-ups `home-v3/tv/01`–`18`; RULES §14d; HOME.md)
- **One hero, one row** (RULES §14d): one shared hero component for Home and Details (text block bottom-left,
  anchored up from the buttons; description two lines; up to four genres); one shared row component for Home and the
  Details tabs (the player's rows separate). Home's first row heading and Details' tab row at the same height.
- **Rows:** headings never focusable; **no Move or Hide on Home**; cards 40 px apart, focus grows 8%, never overlaps.
- **Carousel dot:** the current pill (about 56 px) fills with white over the time to the next hero (8 s; changes
  start after 30 s idle); the fill pauses while focus is on the hero.
- **Hero More menu on Home (trimmed, `tv/09`, `tv/14`):** Details · Playback (Resume / Play, Play from the start) ·
  This Title (Mark as watched · **Exclude from hero**). No library management on Home.
- **Exclude from hero:** the title leaves the carousel at once; the Undo notice reads "<Title> excluded from hero"
  with "Manage in Files › Library Care › Excluded from hero." under it (`tv/15`). Stored by the title's identity
  (TMDB id), so it stays excluded if the title is removed and added again, until included again. **Files › Library
  Care › Excluded from hero** (`tv/17`): A–Z, search above 12 items, a count, the focused row shows **Include
  again**, **Include all again** at the top right; scroll arrows. A row for it in Library Care with its count
  (draw with the Files review). Same words in the menu, the notice and Files. **Icon: A**, the hero screen with a minus (owner, 21 Nov). The
  Library Care page is approved in principle only: settle it at the Files walkthrough.
- **Customise Home (`tv/07`, `tv/08`; owner chose buttons on the focused row):** Shown on Home (Home's order) ·
  Hidden (A–Z) · Add a row. The focused row shows Move · Hide (own rows also Edit); a hidden row shows Show. Move lifts
  the row, Up / Down, Home follows live; OK or Back puts it down. (No "Kept off the hero" row: that list is in Files.)
- **Row settings (`tv/12`):** today's rules: Includes, Minimum rating, Maximum items step with Left / Right; Sort by
  opens the Sort list; Order two chips; Delete this row warns beside itself and uses Undo, no pop-up.
- **Icon picker (`tv/18`):** opens under the Icon row, the panel's width, like the keyboard (RULES §15); it **never
  covers the Icon row itself** (its top edge sits just below that row, the row stays fully visible).
- **Icons (owner, 21 Nov; `tv/10`):** Fix match = a pencil putting it right (B); Remove from library = crossed off the
  list (A); everywhere these appear (Details More, long-press).

### Bug: the grey "open page" box trails behind in the top bar (owner, 21 Nov)
Moving along the bar from Home to Movies (or between any media pages), Movies gets the white focus box at once but
Home **keeps its grey open-page box until Movies has finished loading**, which looks slow. **Cause:**
`TopNavigation.refreshPath()` draws the grey box on `tab == selected && !focused`; `selected` only changes in
`selectTab()`, which runs after `OPEN_ON_FOCUS_MS` (250 ms) and the new page's build. **Fix:** the moment focus lands
on a different **media** page in the bar (Home, Movies, TV Shows, own page: the ones that open on focus), drop the
grey box from the old page straight away (e.g. treat the pending page as the open one in `refreshPath()`, or call
`refreshPath()` from the bar's focus change with `pendingOpen` counted). **Keep:** the grey box while focus is
below the bar on the page you're on; and when focus rests on a **tool** (Files, Search, Settings), which needs OK,
the current page keeps its grey box, because it is still the open page. Test: focus moves Home → Movies; on the very
next frame Home has no box and Movies the white one; Home → Settings (no OK): Home keeps the grey box.

### Details review, round 1 (owner, 21 Nov; mock-ups `details-v3/tv/01`–`12`; DETAILS.md)
- **Tab row line = the library toolbar line:** white 35%, about 2 px, fading out at both ends; tabs keep the
  **underline** for the open tab (owner: tabs are tabs). Tabs at Home's first-row height (RULES §14d).
- **Details tab (`tv/02`):** Reception bigger (rows about 76 px, 28 px values, the faint fading lines) so it fills
  its column; Cast and Crew bigger (112 × 168 portraits, moved up into the free space); **Container removed** (the
  file name shows the extension) and **Revenue removed** so both columns stay level; **File** and **Location** added
  to Technical information; Bitrate only as a list-view column.
- **More menu leads to (`tv/07`–`12`, to approve):** Remove from library → straight back to the page you came from,
  the poster gone, the Undo notice; Subtitles → side panel (In The File · Next To The File · Download › Find
  subtitles online), OK ticks; Artwork → side panel (For: Hero and rows · Library page; a grid, the hero previews the
  focused picture; tick on the current one); Full description → the text alone, page blurred; Delete file → the
  approved warning; TMDB page → one line and the code. Fix match → Files › Library Care's Fix match page (redesign at
  the Files review).
- **Still open (do not build):** the wording "Stream more from the world of …" on More Like This (owner: it sounds
  like the same franchise); what each of the four kinds (movie, show, season, episode) shows on the Details tab and
  hero; how a season page is reached now that row headings are not focusable.

### Details review, round 2 (owner, 21 Nov; mock-ups `details-v3/tv/02`–`15`)
- Approved: the Details tab layout (`tv/02`), Remove from library with Undo (`tv/07`), Full description (`tv/10`),
  More Like This rows headed with the service's logo and **"On Netflix"** (on Prime Video, on Disney+ …), replacing
  "Stream more from the world of …".
- **One page, no title (proposed `tv/02`–`06`, `13`–`15`):** on a tab the page scrolls so **the hero's foot peeks above
  the tabs** (about 56 px of the card's bottom edge) instead of a separate title or logo; no scroll arrows here.
- **Subtitles (proposed `tv/08`, `08b`):** narrower panel (about 560 px); groups **Built In · Subtitle Files ·
  Online**; Find subtitles online opens on top: Language (stepper), Results (release name, downloads, pills Best match
  · SDH · Forced), the focused result shows the download icon; OK downloads and ticks it.
- **Artwork (proposed `tv/09`):** no title, no footer text; the For chooser stays fixed at the top and the pictures
  scroll underneath it, clipped below a line; scroll arrows in the panel's margin; the suggested picture carries a
  **Suggested** badge (star); no "Backdrop 1 / Poster 1" labels.
- **Delete file (proposed `tv/11`):** the second line drops "Deletes": "**<file>** (2160p · 18.7 GB) on **<server>**."
- **TMDB:** the More item reads **"TMDB page QR code"**; the pop-up shows the code alone (`tv/12`).
- **The four kinds on the Details tab (proposed `tv/02` movie, `13` show, `14` season, `15` episode):** groups show
  Average · total for running time and size; mixed technical values show the most common and the odd ones out
  ("1080p · 2 in 2160p"); seasons and episodes show their own scores where they exist, otherwise "Unknown"; a show
  lists Created by, a season "Ruth Kane and 2 others", an episode Guest stars; Files / File and Location follow the
  kind (folder for groups). Season pages: a season card first in each season row (to draw).

### Details review, round 3 (owner, 21 Nov; mock-ups `details-v3/tv/16`–`21`; photos of 0.158.7)
- **Bugs seen on the Shield (fix first):**
  - **Hero clipped left and right** on Details by an invisible frame (DT3 still not fixed): the hero card, its white
    edge and corners must be whole. Test: the hero's bounds lie fully inside the visible area on every Details kind.
  - **Rows cut off at both ends** on More Like This (the "On Netflix" row) and other Details rows: rows run off the
    screen edges, never ending at an invisible line, also on the left once scrolled.
  - **Top bar must not be a solid band on Details:** content scrolling up runs behind the bar, blurred and darkened,
    the bar's words on top, as on Movies and TV Shows (`tv/16`; RULES "Library pages under the top bar"; R2's cause
    applies here too).
- **Side margin:** library pages use the top bar's edges (52 px at 1080p). Details must use the same for tabs,
  headings, the Details tab and the start of rows. Hero width to settle with the owner (1776 at 72 px today).
- **Approved:** the hero's foot peeking above the tabs (no title); the narrower Subtitles panel and its wording.
- **Proposed:** episode long-press (`tv/17`: Resume / Play, Play from the start · Mark as watched, Mark earlier
  episodes as watched · Subtitles, Delete episode file); Extras cards carry YouTube's red mark (`tv/18`); the Extras
  player (`tv/19`); library titles on More Like This get the long-press menu (`tv/20`: Resume / Play, Play from the
  start · Add to Watch Next, Mark as watched); Delete file: the file name on its own line, its facts under it
  (`tv/21`; RULES "No lone word on a line").
- **Find subtitles online:** the Language value steps with Left / Right (‹ English ›, arrows on focus; RULES §8).
- **Long lists** (RULES §6): Studio, Country, Language, Audio languages, Subtitles, and mixed Video / Resolution /
  HDR across files: at most two then "and N more", never a full line.

### Details review, round 4 (owner, 21 Nov; mock-ups `details-v3/tv/16`–`22`)
- **Top bar, one effect everywhere (owner, firm):** on Home, Movies, TV Shows, **your own page and every Details
  page**, content scrolled up runs behind the top bar **blurred and dimmed, fading out at the bar's foot, with no
  solid band and no hard line**; the bar's words sit on top (`tv/16`–`18`). Same component on every page; test that
  no page draws a solid strip under the bar.
- **Side margin (owner: Claude's recommendation):** the hero (Home and Details, one component) is **1816 × 820 at x 52**
  so its edges sit on the top bar's edges (52 px); tabs, headings, the Details tab and the start of rows on Details
  use the same 52 px. Replaces 1776 (RULES §14d).
- **Long-press menus stay on screen:** a menu opens beside the card on whichever side has room (left when the right
  edge would cut it, `tv/17`).
- **Episode long-press approved (`tv/17`)**; Mark as watched reads **Mark as unwatched** on a watched episode.
- **Delete episode file (`tv/22`):** the delete warning (real deletion, no Undo); when earlier episodes of the same
  season are in the library, an **Earlier Episodes** switch "Also delete S1 E1 and S1 E2 (watched)" (**on** when they
  are all watched, off otherwise), the total under it, the button naming the count ("Delete 3 files"). Replaces the
  "only this one · all · choose" step (group 5).
  **Many earlier episodes (`tv/23`):** a range, never a list: "Also delete S1 E1 – E10 (all watched)"; with gaps,
  "the 7 earlier episodes in your library (E1 – E10)". The switch only ever covers **watched** earlier episodes; if
  some aren't watched: "Also delete the 6 watched earlier episodes (E1 – E8)". **Clean Up** shows the Trakt row
  ("Also remove them from your Trakt history", off) whenever Trakt is connected, hidden otherwise; on every delete
  warning.
- **Behind the top bar:** while focus is below the hero (on the tabs or rows), the hero's buttons are at rest, never
  white; only blurred artwork and text run behind the bar.
- **Extras (`tv/18`, `tv/19`):** YouTube's red mark the same size as the streaming logos and **centred on the
  artwork** (streaming logos centred too); the player as drawn (as large as possible, page blurred behind), the thin
  progress line at the foot, **no YouTube controls over the video**.
- **More Like This library long-press (`tv/20`), Delete file wording (`tv/21`), long lists:** approved.

### Details review, round 5 (proposed 21 Nov; `details-v3/tv/24`, `25`)
- **Season card:** on a show's Episodes tab each season row starts with a season card (the shared 16:9 card): the
  season's own artwork, "Season 1", "10 episodes · 2 watched" (or "not started" / "all watched"), the progress badge
  for the season (episodes watched out of all). OK opens the season page; long-press opens the season's More menu
  (R4). Not on single-season shows (no season page) or on a season page's own Episodes tab.
- **Season page (`tv/25`):** the shared hero, "Season 1" as its title line, facts "2024 · 10 episodes · genres",
  Resume S1 E3 (or Play S1 E1) and More, tabs below opening on Episodes.

### Reports and Backups folders (owner, 19 Nov: Claude's recommendation)
- **Reports:** always saved to Download › Supernova; no Reports folder row to change it.
- **Backups:** Supernova's one folder picker (RULES §7b), with Android's folder grant shown once behind it the first
  time a folder outside Download is chosen. Default Download › Supernova. Built with the in-row backup flow.

### Diagnostics upgrade (owner, 20 Nov; before the 0.159 clean-out, so its "before" numbers are real)
- **Start-up time:** launch to Home ready (first frame and first usable focus), cold and warm.
- **Page switches inside the top bar:** Home, Movies, TV Shows and own pages share `MainActivityLeanback`, so time each
  switch (`page_switch`, from focus landing to the page drawn) beside `screen_open`.
- **NOVA leftovers that run:** an event each time an old NOVA activity, fragment, overlay or background piece starts
  (`legacy_component_started`, with its name and the page it ran on), so the clean-out works from a list.
- **Health summary at the top of each report:** peak and average memory per page, start-up time, slowest page and
  switch, slow frames, freezes, focus anomalies, leftovers that ran; each against a limit, over-limit lines first.

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
