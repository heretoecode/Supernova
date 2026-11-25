# Discussion handover (updated 25 Nov 2026)

## 25 Nov — START HERE (new discussion chat)
**Phase:** polish, page by page; designs and planning only, no coding (CLAUDE.md "Polish phase").
**Done and written up:** library pages (LIBRARY.md), Home (HOME.md + RULES §14d), Details (DETAILS.md, rewritten
21 Nov), Player and Playback Settings (PLAYER.md, written 25 Nov), Search (SEARCH.md, 25 Nov). Every decision is also in `docs/0.158/BRIEF_0158_B.md`
(sections "Library toolbar review" … "Player page sheet"), which Supernova Coding merges before each item.
**Still to review:** Files (FILES.md), Settings (SETTINGS.md). Owner picks the order. (Search done 25 Nov.)
**Waiting at those reviews:**
- Files: Library Care › **Excluded from hero** (new, approved in principle only, mock-up `home-v3/tv/17`); **Fix match
  shows the file name and location**; the approved **To fix** row; bug M1 (wrong TV matches, REVIEW_0158).
- Settings: the stepper rule (RULES §8: more than two choices step with Left / Right) applies to every row with 3+
  chips; the Seek preview setting (Off · Chapter pictures · Video frames).
**How we work (owner):** mock-ups for the TV at 1920 × 1080 on the speckle go to Google Drive › Supernova › Media ›
From Claude › a dated folder; renderers live next to them in `docs/design/proposals/<topic>/render*.js` (fonts:
`npm pack @fontsource/roboto@5.0.13`, FONT_DIR = its `files`). Look at every render before uploading (check the
speckle drew: one renderer page per frame, speckle script in its own block). Record each decision the same day in
RULES, the page sheet and the brief; push after each change.
**New rules this week (RULES):** §8 steppers; §14d one hero (1816 × 820 at x 52) and one row (cards 40 px apart,
focus 8%); one tab behaviour everywhere (the player's); behind the top bar = blur on every page; long lists (two then
"and N more"); no lone word on a line; keyboard / pickers open under their row; panels' outline and shadow also over
video; seek preview never a grey box.
**Before the next coding go-ahead:** ask for the diagnostics report (MobLand delete, 19 Nov 14:30–14:45). NOVA was
fully audited on 25 Nov (BRIEF_0158_B › NOVA catch-up: eleven items penned in); at the go-ahead, offer to
recheck only what NOVA adds after 25 Nov (CLAUDE.md).

## 25 Nov — Files review started
- Rounds: 1 This Device and browsing a drive · 2 Connections and the form · 3 Library Care (Excluded from hero, To
  fix, Fix match shows the file, M1) · 4 Your Library (statistics by source).
- Round 1 drawn: `files-v2/tv/01`–`08` (Drive › From Claude › 2026-11-25 Files round 1). Waiting for the owner.
  Proposed in it: pill top left on grid cards (the progress badge keeps top right); Exclude at once with the Undo
  notice; Add to library / Scan this folder progress in the row's pill (ring in the top bar); "No videos in this
  folder" with Show all files; the file menu grouped like the library long-press menu.

## 25 Nov — Search review started
- Owner chose Search first. Redrawn on today's rules: `search-v6/tv/01`–`08` (Drive › From Claude › 2026-11-25
  Search): before typing, a show and its world, a person with more than fits plus Menu on a library title, streaming
  still loading, Clear history's Undo notice, nothing in the library, the Voice key listening, progress A or B.
- Decided: progress A; Keep / Clear search history in Settings. Landing approved (`tv/09`, `10`). No Phone key
  (RULES §15). **Search done (25 Nov):** SEARCH.md rewritten; final set on Drive › 2026-11-25 Search › Final.
- Also 25 Nov: owner's saved notes → Parked: Seerr requests from Search (Plozz already does it), finer settings
  after Plozz (candidates listed). **Next episode on the hero** approved in principle for Details, before its coding:
  `next-episode/tv/01`–`03` (Drive › 2026-11-25 Next episode on the hero), A chosen (above the logo); Details tab: date folded into
  Status, no new row (agreed, `tv/04`). In BRIEF_0158_B and DETAILS.md.

## 25 Nov — Player done
- Player page sheet `docs/design/pages/PLAYER.md` (mock-ups `player-v16/tv/01`–`13`).

## 21 Nov evening — Player review started
- Player redrawn on today's rules: `player-v16/tv/01`–`07` (Drive › From Claude › 2026-11-21 Player): controls with
  Chapters, tab focused, Episodes, Playback Settings × 3 tabs, skip alone. Decided: Skipping steps, panel 700 px, edge
  outline+shadow over video, skip button far right with controls (`tv/08`), seek preview setting Off · Chapter
  pictures · Video frames, bugs (previews, age badge clipped). In the brief. No page sheet for the player yet: write `docs/design/pages/PLAYER.md` after.

## 21 Nov evening — Details done
- **Details done:** page sheet `docs/design/pages/DETAILS.md` rewritten from rounds 1–6. Done so far: library pages,
  Home, Details. Next page: owner's choice. Still to ask at the Playback Settings review: Skipping chips vs stepper.
  At the Files review: Library Care › Excluded from hero; Fix match showing the file.

- Done so far: library pages, Home (with one hero / one row, RULES §14d), the top-bar grey-box bug (brief).
- Details redrawn on today's rules: `details-v3/tv/01`–`06` (Drive › From Claude › 2026-11-21 Details; all four tabs shown on every frame, 06 Episodes): episode hero,
  Details tab with File and Location (Bitrate moved to Columns only), Extras, More Like This by service, More menu.
  Round 1 notes in the brief (tab line, Reception and people bigger, Container and Revenue out, More flows `tv/07`–`12`).
  Round 2 (`tv/02`–`15`, in the brief): hero foot instead of a title, Subtitles and Find online, Artwork scrolling,
  Delete wording, TMDB QR code, "On Netflix", the four kinds (`13`–`15`). Round 3 `tv/16`–`21` (top bar blur, long-press menus,
  Extras YouTube and player, Delete reworded); bugs from photos in the brief. Round 4 `tv/16`–`22` (blur fixed, margin 52 px
  with hero 1816, episode delete with earlier episodes, YouTube mark centred). Season card A (`tv/26`), season page with Season 1 in the facts line (`tv/25`), earlier-episodes stepper (`tv/28`):
  approved.
  After that: the Details page sheet (DETAILS.md) rewritten from rounds 1–5, then the next page. **Before coding: ask for diagnostics AND whether to check NOVA updates (CLAUDE.md).**

## 21 Nov — Home review
- Owner: one global hero (Home and Details identical, same code) and one global row design (Home and Details tabs;
  the player separate). Drawn `home-v3/tv/01`–`05` (Drive › From Claude › 2026-11-21 Home); RULES §14d (proposed
  measurements). Decided 21 Nov (RULES §14d): two-line description; first line under the hero aligned on Home and Details; no Move /
  Hide (order in Customise Home); cards 40 px apart, focus 8%, no overlap. Text block bottom-left; up to four genres. More menu and Customise Home drawn `home-v3/tv/06`–`08`; Customise Home with buttons on the focused row (owner chose it over
  Columns' OK-picks-up). Round 3 `tv/09`–`13` (Drive 9–13) waiting: trimmed Home More menu with Not on the hero,
  filling dot (8 s interval, after 30 s idle), Fix match / Remove icons, Kept off the hero in Customise Home, row
  settings and icon picker on today's rules. Round 4 `tv/14`–`18`: Exclude from hero (menu, Undo notice
  pointing to Files › Library Care › Excluded from hero, list page), icon picker under its row. Icon A. **Home done (21 Nov).**
  At the Files walkthrough: highlight Library Care › Excluded from hero (new; FILES.md › To settle).

## 20 Nov afternoon — Filters review
- Filters redrawn on today's rules: `library-pages/tv/35`–`40` (Drive › From Claude › 2026-11-20 Library Filters).
  Owner: Filters menu and Streaming services fine; Watched list icons = poster badges (`tv/41`, in the brief).
- Decided: Filters button sliders + "Filters · 2" when on (D); Genre shapes (B); Match ticked list (B). Streaming
  logo on posters parked (REVISIT_LATER).
- Year approved (`tv/43`); Order button width bug found and in the brief. **Filters done.**
- **Sort done (20 Nov):** final `tv/48`; one Size sort ("Average 2.2 GB · total 8.8 GB" for shows); **six posters a
  row** (`tv/50`). All in the brief and LIBRARY.md. Noted for later reviews: file name and location in Details'
  Technical information and on Fix match (page sheets › To settle; mismatch M1 in REVIEW_0158).
- **List view done** (20% fading row lines, badge spacing, `tv/55`). **Columns:** cap of seven approved (21 Nov);
  list of 16 approved; names follow what the page holds. **Columns done.**
- **Customise page done (21 Nov):** `tv/59`–`68` approved (steppers for 3+ choices, inline delete with Undo,
  keyboard under Page name, Last episode added icon A). New app-wide rules: RULES §8 (more than two choices step with
  Left / Right), §15 (keyboard over the panel under its row). **The library pages are done.** Next page: owner's choice.
- Then Sort, Order, List / Grid view with list view, Columns, Customise page on the own page.

## 19–20 Nov evening — library pages review (start here next time)
- **The phase (owner, 20 Nov): polish, not features.** Go through every part of each page and ask "does that look
  right? should it be tweaked?" New features (onboarding, Trakt list row, Media Transfers) wait; before any is built, revisit its design
  with the current rules and write its page sheet. See CLAUDE.md.
- **Installed: 0.158.7** (signed, run 44604872477). Part B paused; everything decided since is in `BRIEF_0158_B.md`
  (Coding merges this branch before each item). The owner reviews page by page; mock-ups for the TV go to Drive ›
  Supernova › Media › From Claude (dated folders); sources in `docs/design/proposals/rows-captions/` (`tv/` = 1920×1080).
- **Decided tonight (all in RULES, the page sheets and the brief):** corner progress pie replaces every progress bar
  (48 px, 3 px white rim, RULES §14c); row design A for every row (text over the artwork); library captions three lines,
  sentence case, box `#141619`, about 100 px; artwork line solid white 1 px; focus ring 4 px; menus and panels white
  outline 70% + dark shadow (§11); library toolbar 52 px boxes, 24 px either side of a line at 35% thinning at the ends;
  Customise page only on own pages; empty pages ("No movies yet", "will appear", plain icons, the animated ring);
  long-press menus and delete warnings (versions list, episode list with Not in library, Clean Up with the folder
  switch, "This can't be undone." only beside a focused Delete, summary line on its own); Home hero one button (More),
  heroes one size with the first row's heading showing; hero TV rules (next episode across seasons); page tabs take
  Files' look; app icon on `#111215`, no grain; reports to Download, backups via the folder picker; the facts-line order
  Edition · Resolution · HDR · Sound · Codec; R2 cause found (TopNavigation decides scroll-behind before the own page's
  library exists).
- **To ask at the Playback Settings review (owner, 21 Nov):** should Skipping (Off · Show button · Skip) step with Left /
  Right like every other 3+ choice row (RULES §8)? It kept chips in v15 (19 Nov, REVIEW_0157_2 PS9).
- **To remember at the Files review:** draw and settle the approved **To fix** row in Library Care (FILES.md, end).
- **Where we stopped (20 Nov, about 02:30):** Filters. The approved mock-ups `library-pages/23`, `24` were sent with how
  0.158.7 differs (counts on one line, genre/service lists tick in place with no Selected group, From / To step with
  Left / Right). The owner is to look at Filters on the Shield and say what to tweak; then Sort, Order, List / Grid
  view with list view itself, Columns; then Customise page on the own page.
- **Next in the library review:** the five toolbar buttons (Filters, Sort, Order, List / Grid view, Columns), then
  Customise page on the own page. Then the other pages, one at a time.
- **Reminder:** before Coding's next version, ask for the diagnostics report covering 19 Nov 14:30–14:45 (MobLand).

## 19 Nov — start here
- **Reminder (owner, 19 Nov): before the go-ahead for Coding's next version, ask the owner for the diagnostics
  report covering the MobLand delete, 19 Nov 14:30–14:45 local time** (D1 in `docs/0.158/REVIEW_0158.md`), and read it.
- Waiting for the owner: 0.158.7 installed (then make the whole-app walkthrough checklist), and replies on L1, R6
  (three row mock-ups), Files' tab look, A2 icon, S2, and the top-bar strip on library pages (R2, end of REVIEW_0158).
- **Owner, 19 Nov (afternoon): no Shield review until part B is finished.** Then make one whole-app walkthrough
  checklist from the final signed build (the report's "Review build" line), listing only what is built, with a short
  "not built" box per page. The 0.158.3 checklist (`docs/0.158/review-0158-3-checklist.html`) is superseded.
- **Installed: 0.158.3.** Supernova Coding is running the continuation of part B in a new window (prompt: merge this
  branch before each item; order in `docs/0.158/BRIEF_0158_B.md`, Playback Settings round two first).
- **Decided today:** panels `#16181C` at 99% (RULES §11); Playback Settings v15 (`player/33-v15`: 820 px, one line,
  Left/Right stepping, "Remember for this show", Skipping = Off · Show button · Skip); Settings page sheet
  (`docs/design/pages/SETTINGS.md`); speckle density 20, corners B, titles (RULES §1, §3, §19); Back and page position
  (RULES §16); Guest decisions (`docs/REVISIT_LATER.md` round 8); episodes and Settings added to part B.
- **How new notes reach Coding:** the owner sends them here; add them to the brief (fold into an item Coding hasn't
  reached, else a new group at the end); Coding merges this branch before each item.
- **Next session, first:** make the 0.158.3 review checklist (as `docs/0.157/review-0157-2-checklist.html`; artifact
  link in the owner's last review), covering part A and part B as built, plus H1/H2 and R2. Then take the owner's
  notes into a review file and the brief.
- **Open, for the owner:** a 0.158.3 checklist (offered); Guest mock-up; diagnostics report from 0.158.x (Drive ›
  Supernova › Media › For Claude); PL4 parked unless seen again. APKs: GitHub app hides artifacts; use Safari, desktop
  site, signed in (or allow this session to fetch from GitHub's storage and put APKs in Drive).

## 18 Nov
- 0.157.2 signed and installed (run 44020349261): much better. Owner: redesign first, then fix bugs on settled designs.
- **0.158 = library pages, Home, Search** + the new top bar (approved, mock-ups `files-review-0157-1/08`, `09` A),
  one side margin (the top bar's edges), connections by server, the full Player review. Settings and onboarding: 0.159.
- **Page sheets ready for 0.158:** `docs/design/pages/LIBRARY.md`, `HOME.md`, `SEARCH.md` (plus FILES, DETAILS).
  The owner won't critique Home or the library pages until the redesigns are built.
- Next: the owner reads the sheets, then gives the go-ahead for 0.158; write its brief then.

## Earlier (16 Nov), still current
## Ready (designed and approved)
- **Media Transfers** — complete: `docs/0.158/MEDIA_TRANSFERS.md`, mock-ups `docs/design/proposals/transfers-settings/`.
- **Trakt list as a Home row** — `docs/0.158/OWNER_NOTES.md`, mock-ups `docs/design/proposals/trakt-list-row/` (option B).

## To discuss
- **Users and tags** (out of 0.158 until settled): notes in `docs/REVISIT_LATER.md` ("Profiles", "Tags and views", rounds
  1–7), mock-ups `tags-views/01`–`06-v6-*`. Approved so far: shared tags, record who watched, editable users and tags,
  in Backup, one feature, delete-a-user options, Default user, Together hides one-person titles, nudges (v5), "Watch
  without Alex?", seasonal Home row, suggested tags. Open: "Ask every time" default user, per-user watchlist, Guest view.
  Parked: Trakt per user, per-user playback settings.
- **Episodes** (OK plays, long-press menu, deleting earlier episodes) and **network statistics by source** (with Files ›
  Your Library): `docs/0.158/OWNER_NOTES.md`.
- **Diagnostics that catch problems before the owner notices** (health snapshot, smoothness, speed, playback, leaks,
  Health summary with limits): on the roadmap.
- **Feature blueprint** `docs/research/FEATURE_BLUEPRINT.md`: pick ideas to take forward.

## Rules added today (all in RULES.md / CLAUDE.md)
- Current decisions live in RULES.md; replaced diary entries are marked. A sweep for old contradictions is still to do.
- Small menus fold inline (≤ about five buttons on the row).
- Fix match is one page on Files; from a title it offers Match and return · Match and stay.
- Streaming service order: the top of Your services is used everywhere.
- Mock-ups: full size, up to about four screens per picture; whole flow; the one keyboard in the app.
- Diagnostics read before each version ends with what the owner didn't see, with fixes.
- Revisit later: captions under the player's cards (match Home rows); panels whose rows swap with a choice (app-wide).

## Practical
- **Media swap in Google Drive (owner, 18 Nov):** `Supernova › Media` has two folders.
  - **From Claude (to view on the TV):** mock-ups to judge full screen (via Solid Explorer), one dated subfolder per
    topic (e.g. "2026-11-18 Background speckle"), files numbered in viewing order; 4K (3840 × 2160) when fine detail
    matters. Small comparisons the owner can judge on the iPad are sent in chat instead.
  - **For Claude (screenshots and videos from the Shield):** the owner's own screen captures (the Shield records
    screenshots and videos exactly as shown, unlike a camera). The owner names the file; Claude downloads it, and cuts
    videos into frames with ffmpeg (about 5 a second). Ask what to watch for and roughly when.
  - The Claude app takes photos only, so videos always come this way. Photos plus a line for looks; video for
    behaviour (focus jumps, timing, flicker).
- Push check: after every push confirm `git ls-remote` matches HEAD (a restart once left commits unpushed).
- Owner's notes while Claude is unavailable: `docs/INBOX.md` or a GitHub issue titled "Inbox: …".
