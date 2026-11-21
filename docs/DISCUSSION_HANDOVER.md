# Discussion handover (updated 19 Nov 2026)

## 21 Nov — Home review (start here next time)
- Owner: one global hero (Home and Details identical, same code) and one global row design (Home and Details tabs;
  the player separate). Drawn `home-v3/tv/01`–`05` (Drive › From Claude › 2026-11-21 Home); RULES §14d (proposed
  measurements). Waiting for the owner's notes on Home. Then Customise Home.

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
