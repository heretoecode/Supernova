# Brief: 0.158 part C (for Supernova Coding) — the refined pages, NOVA's fixes and the diagnostics read

Approved by the owner, 26 Nov 2026 ("let's get the library pages, Home, Details, Player and Search, the app-wide pieces,
the latest NOVA changes, and the Settings Backup bug"). Installed on the Shield: **0.158.7**. Follow CLAUDE.md
(Definition of done, Working unattended) to the letter: **hands-free until everything here is built** (owner, 26 Nov);
sign finished work as you go (standing approval), first signing **0.158.8** (code 1588), then .9 and so on.

## Part C continued (owner, 26 Nov, after 0.158.9)
**Ready to start (owner, 26 Nov): the Files review is finished and FILES.md is final.** Order (Claude's,
owner: "an order that makes sense"): (A) the refinements 0.158.9 missed (items 1–6), (B) diagnostics record why (item
9: everything after benefits from reasons in the report), (C) the Files page (item 10: Library Care's Missing files
lives there), (D) the MobLand bugs M2–M7 (item 8, built onto the new Library Care), then (E) the NOVA leftovers (item 7,
the riskiest ports, last).
0.158.9 built group 1, B1 and a slice of the pages, then stopped. **The rest of this brief is the job now, and the
redesigned pages come first.** Work through `REPORT_0158_C.md` › "Not built", line by line, in this order, each page
finished to its sheet (every action pressed and rendered) before the next:
1. **Search** (SEARCH.md): landing Genres and People cards, progress badge A, streaming headings, the loading row,
   the nothing-found panel, the long-press menu on results, Voice's "Listening…".
2. **Home** (HOME.md, RULES §14d): one shared hero and row component with Details, the filling carousel dot, cards 40 px
   apart with 8 % focus, row design A and the corner badge, Customise Home (Move · Hide · Edit on the focused row), row
   settings. **Hero More menu: Playback first (owner, 26 Nov)**: Resume / Play · Play from the start (focus starts there),
   then Details, then This Title (Mark as watched · Exclude from hero); same order as the library long-press menu.
3. **Details** (DETAILS.md): every round 1–6 item not yet in (tab row line and height, More menu icons, the rest).
4. **Player** (PLAYER.md): the shared title block, Chapters · Episodes tab look, the 700 px panel, lists opened on top of
   the panel, Spatial audio's switch, the Seek preview stepper (Off · Chapter pictures · Video frames).
5. **Library pages** (LIBRARY.md): toolbar line and sizes, Filters / Sort / Order / Columns, captions, Customise page,
   delete warnings with versions.
6. **App-wide check** (group 4): every rule checked on every page above.
7. **NOVA leftovers:** N2's second half with trakt-java 6.22.0, N3, N6, N7, N8, N10, `60df7be7`.
   **Check NOVA again first (owner, 26 Nov):** list every commit on NOVA's main branch (and its MediaLib, FileCore
   and other dependency repos) since the 25 Nov audit, and any new release. **How we take NOVA's work (owner, 26 Nov):**
   keep taking unreleased commits, but only fixes we also suffer, security fixes and changes forced by outside services
   (TMDB, Trakt, OpenSubtitles, IntroDB); only commits merged to NOVA's main, at least a few days old and not reverted;
   features wait for our own design review (park them). Record each taken or skipped commit, with why, in the report.
8. **Bugs found on 0.158.9 (owner, 26 Nov), fixed alongside the pages:**
   - **M2. Deleting a file that's already gone "fails".** The owner deleted a MobLand episode by hand on the SFTP server,
     then deleted it in Supernova: "can't be found" error. A delete whose file is already missing is a success: remove
     it from the library and say "This file was already gone from <source>, so it's been removed from your library."
     (delete warnings' wording, RULES §7b). Test with SFTP, SMB, WebDAV and USB.
   - **M3. Missing files only learns from scans.** Whenever Supernova finds a library file missing (delete, play,
     Details, thumbnail or subtitle work), mark it missing at once: it appears in Files › Library Care › Missing files
     with Find it again · Remove, the badge and the dot beside Files update. Test that a failed open of a gone file
     lands it there.
   - **M4. A scan that saw new files didn't flag a removed one (owner, 26 Nov).** The SFTP scan that added Blue Planet 3
     left the hand-deleted MobLand episode in the library, not in Missing files. Leads (MediaLib
     `NetworkScannerServiceVideo.FileVisitListener.onStop` and `SupernovaLibraryPolicy.availability`): (1) missing
     files are marked only when the **whole** traversal had no listing error (`mHadListingError`), so one unreadable or
     interrupted folder anywhere (the 0.158.7 report shows SFTP listings interrupted) stops every missing mark for that
     source; (2) the prescan loop stops filling when the app leaves the foreground (`while (prescan.moveToNext() &&
     isForeground)`), so a partial list is compared; (3) check that Files › Library Care › Missing files reads the same
     `HEALTH` keys the scanner writes (`media:<id + SCANNED_ID_OFFSET>`). Fix: judge **folder by folder**: folders read
     in full mark their missing files; only folders that failed hold back, and Library Care says so ("Couldn't check 1
     folder on Home server"). Diagnostics record per scan how many were marked missing and why any were held back (the
     0.159 "record why" rule, early). Tests: a scan with one failing subfolder still marks a missing file in a good one.
     **Evidence from 0.158.9 (`DIAGNOSTICS_0158_9.md` › 2):** the same two SFTP sources report every file as new on one
     scan and none on the next, never report a missing count, and "sources total" flips 1 / 2 / unknown: known files
     are not being recognised reliably. Fix that first; each scan records new, known, missing and held back.
   - **M2 and M3 confirmed on 0.158.9 (26 Nov, 11:24):** MobLand S2 E9 failed to open over SFTP with no reason recorded,
     then two deletes failed "not found"; it is still in the library and not in Missing files.
   - **M5. Titles still being matched are listed as Not matched (owner, 26 Nov).** New files whose TMDB look-up hasn't
     run yet show in Library Care › Not matched (and its count) as if they had failed. Only titles whose look-up has
     finished without a match go there; while look-ups are pending, the Not matched page says "Matching N new titles:
     they're not listed while Supernova is still looking them up." Test: a freshly scanned file is not counted as Not
     matched until its look-up has ended.
   - **M7. Your Library › By Library Page: the third page has no value on the right (owner, 26 Nov, 0.158.9 on the
     Shield).** Movies and TV Shows show their size on the right; Documentaries shows only its second line ("36 titles ·
     133 GB") and nothing on the right. Every library page, built-in or the owner's own, uses the same row with its
     size on the right. Test: a third, user-made library page renders its value like Movies.
   - **M6. Renaming a connection doesn't save (owner, 26 Nov).** A connection renamed in its edit form keeps its old
     name in Connections and Library Care. Save the new name and show it everywhere the connection is named
     (Connections, Library Care, scans, Missing files) at once. Test: rename, leave Files, come back: the new name shows.
9. **Diagnostics record why, not just what (owner, 26 Nov; moved here from 0.159):** every failure event carries its
   reason: the error, the step (connecting, listing, deleting…), the kind of source, and the server's answer (status
   code and message). A test fails the build if any failure is recorded without one. Where the reason truly isn't
   known, the report says so and records what was attempted, how long it waited and the last thing that worked. The
   report's summary lists top reasons ("Delete failed ×6: server refused (403)").
10. **The Files page (owner's review finished 26 Nov; build from `docs/design/pages/FILES.md`, final).** One page at a
   time to the sheet, each finished (renders of every action, Definition of done) before the next: (a) the whole page:
   top bar signal slot, tabs, **Scan library narrating every step**; (b) This Device and browsing (sort menu, folder
   sizes, Fix match on one-title folders, empty folder, pills that never wobble); (c) Connections and the connection
   form (Type menu, Encryption greyed with reasons, form icons, rename saves: M6), sign-in, Network options moved from
   Settings; (d) Library Care: the lists with their "this is fine" buttons, Not matched best guess and pending line
   (M5), To fix, Excluded from hero, Fix match page with inline buttons, **Scan one source menu**, **focusable recent
   scans and the Scan details page** (the scan record kept for the last ten scans and shared with item 9's
   diagnostics); (e) Your Library with By Source and Biggest Movies (M7). Mock-ups `files-v2/tv/01`–`24`. Sign as a point release when done.
11. **Fixes from the 0.158.9 report (`docs/0.158/DIAGNOSTICS_0158_9.md`, read 26 Nov).** Fit them in where they belong:
   D15 with item 9, D14 and D16 with item 8, D17 and D18 with the pages (A).
   - **D14. Scan only when there is something to find (owner approved the rule, 26 Nov).** The local import ran on every
     return to the app (20 times in 80 minutes, 4–6 minutes each, 97,041 rows checked, nothing new); SFTP was scanned 8
     times in 80 minutes, twice at once. The rule:
     | What happens | Scan? |
     | --- | --- |
     | Back in Supernova within 30 minutes | No |
     | Opened after longer | Only sources not scanned in the last 6 hours |
     | A USB drive plugged in, or Android reports files changed on the device | That drive or folder only, at once |
     | Background | One scheduled scan every 6 hours; never during playback; never two at once (one scheduler) |
     | Scan library / Scan one source pressed | Always, at once |
     An incremental local check must cost seconds, not minutes. Every scan records what started it ("you came back after
     7 hours", "scheduled", "drive plugged in"), shown at the top of Scan details (FILES.md). The 6 hours is fixed for
     now; Settings › Library › Look for new videos (SETTINGS.md, to be drawn at the Settings review) will set it later.
     Tests: leaving and returning within 30 minutes starts no scan; two triggers at once run one scan.
   - **D15. Record each event once.** About 770 duplicates in 80 minutes (focus moves, scanner state, scan results).
     Find the double recorder (two processes or two listeners) and test that one action writes one event.
   - **D16. A title TMDB no longer has.** A 404 for the title itself (not its extras) stops the retries, records "TMDB
     no longer has this title", and puts it in Library Care › To fix ("TMDB no longer knows this show: Fix match").
     Never record a 404 as "Availability service unavailable".
   - **D17. Focus slips left on Home:** an off-screen text label taking focus, and presses at a row's end that do
     nothing (should stay put without a warning, or wrap where the rules say). Settings: 3 off-screen rows.
   - **D18. Memory trims:** Android asked 23 times in 80 minutes; Files up to 433 MB. Release artwork and lists on pages
     left behind; record memory per page (D13); a custom library page had 2.4 % slow frames.
12. **Two old bugs still on the Shield (owner, 27 Nov; first reported 19 and 21 Nov, never carried into this brief).
   Build these first in the next run.**
   - **R2. Your own library page: a solid bar under the top bar when you scroll.** Cause (found 19 Nov, still in the
     code on 27 Nov): `TopNavigation` decides once, in its constructor, whether the page may run behind the bar
     (`prepareScrollingContent`). `CustomLibraryActivity` builds it before `workspace.render()` creates the
     `PreviewPages`, and the search only descends through `FrameLayout`s, while the own page's `PreviewPages` sits in a
     `LinearLayout`. Fix: let the search walk any `ViewGroup`, and run it again (inset included) whenever the content
     is rebuilt (e.g. `TopNavigation.refreshScrollingContent()` at the end of `Workspace.render()`). Prove it: a
     Robolectric test that the own page's stage is laid out from y = 0 with the inset on its `PreviewPages` (fails
     today), and renders of Movies and the own page scrolled, side by side, attached to the report.
   - **DT3. Details: the hero and the rows (Episodes, More Like This, Extras) are cut off left and right by an
     invisible frame.** The 19 Nov attempt (negative margins on Episodes and Extras rows) did not cure it on the Shield
     (owner's 0.158.7 photos, 21 Nov), and nothing has changed since. Find every ancestor that clips: the `reading`
     ScrollView and `lower` inside `PreviewMoviePage`, `sectionFrame`, `body` (4 % side padding), the fragment's
     container and `TopNavigation`'s stage; a scrolling container clips its children to its own bounds whatever
     `clipChildren` says, so rows must live in a container as wide as the screen, with the 4 % margin as padding.
     The hero card must be whole: its white edge and all four corners visible. Prove it: a test that the hero's and
     each row's first and last card's global visible rect equals their full size (or reaches the screen edge for rows
     running off it) on all four Details kinds, plus renders of Details › Episodes and More Like This scrolled
     sideways, attached to the report.
13. **The owner's review of 0.158.11 / .12 on the Shield (27 Nov): `docs/0.158/REVIEW_0158_12.md`, P1–P29.** Order:
   crashes and stale files first (P1–P6: no crash on a file that can't open; reconnecting a server re-links instead
   of adding copies; versions for episodes; stale copies in Missing files with Check now; the wrong-match check),
   then Home (P7–P9, P28, P29), library pages (P10–P14), Details (P15–P21), player (P22, P23, P27), Search (P24–P25), and the outline on every menu, panel and pop-up (P26, app-wide). Each with a test
   and a render; renders of every page touched attached to the report. Read the newest diagnostics report first
   (the owner sends one with the crashes of 27 Nov).
Sign each finished page as the next point release (0.158.10, .11 …). **Signing is not the finish line** (CLAUDE.md):
carry on until the list is empty. Never end a turn while work remains; book a `send_later` wake-up only when truly
waiting on a run.

## Before you start
1. Merge `claude/design-planning-discussion-9poejm` into the working branch (`claude/supernova-github-access-yfhe89`).
   It holds every decision from 19–26 Nov: RULES, page sheets, mock-ups, BRIEF_0158_B's later sections, this brief.
2. Read `CLAUDE.md`, `docs/design/RULES.md` (whole), and the page sheets below. **The page sheets win** over older
   diaries, reviews and BRIEF_0158_B where they differ. Check what 0.158.7 already has before building anything: much
   of part B is in; build what's missing or different from the sheets.
3. Not in this build: **Files** (review paused at round 3; FILES.md "Review 25–26 Nov" is for later) and **Settings**
   (not reviewed yet) — except the Settings Backup bug (item B1) and the diagnostics fixes below that touch them.

## Order of work (each group finished, with its tests and renders, before the next; push after each item)

### Group 1 — fixes from the diagnostics report (26 Nov, 0.158.7; read in `docs/0.158/DIAGNOSTICS_0158_7.md`)
These come first: they crash the app or break what the owner uses every day.
- **D1. Crash: automatic matching vs the scan lock (6 crashes, 20 and 22 Nov).** `AutoScrapeService$3.run` →
  `PersonalVideoActions.saveIfMatchable` → `ScanProcessLock.acquire` throws `InterruptedIOException: Scan lock wait
  interrupted`, rethrown as `IllegalStateException: Matching ownership unavailable`, uncaught → the whole app dies.
  Fix: never let it escape the worker; treat an interrupted wait as "try later" (requeue that title), restore the
  thread's interrupt flag, log it as `scan_waiting`/`match_deferred`. Unit test: interrupted wait → no crash, title
  requeued.
- **D2. Scans failing at the same lock (38 `scan_failed` + 10 batches, stage `process_lock`).** Local scans on resume
  and SFTP scans from the scheduler give up when their wait is interrupted or cancelled. Make waiting for the lock
  queue-friendly: a scan that can't get the lock waits its turn (shows "Waiting" where the UI shows scans) instead of
  failing; find who interrupts the waiters (resume and scheduler both start scans; one queue, as RULES/Files intend:
  "pressed sources wait their turn"). Tests for both triggers.
- **D3. Crash: network discovery stopped twice (19 Nov).** `NetworkDeviceDiscovery.attach` lambda calls
  `SambaDiscovery.removeListener` for a listener already removed → `IllegalStateException: This is not a listener
  (anymore?)`. Guard (remove once; ignore "not a listener") and test.
- **D4. The hidden grid steals focus (187 `focus_anomaly_suspected`, `offscreen_focus` /
  `repeated_unhandled_navigation` on `MainActivityLeanback`, target `VerticalGridView:container_list/0/0/0/0`).**
  An off-screen leanback grid (NOVA's) takes focus on Home and the library pages. Make it never focusable (or remove
  it if nothing live uses it); a test that no off-screen view can hold focus on the main page (RULES §3: no NOVA screen
  may run hidden). This is the one hidden NOVA piece to fix now; the rest waits for 0.159's clean-out.
- **D5. Settings: focus on rows that aren't on screen (61 `offscreen_focus` on `VideoSettingsActivity`)**, e.g. the
  skipping rows, Dolby Vision, refresh rate, diagnostics rows. The focused row must scroll into view (RULES §16 arrows);
  fix together with **B1**.
- **D6. Delete over WebDAV (Put.io) failed 6 times, reason lost (19 Nov 14:32–14:42, the MobLand delete).** It went
  through NOVA's `browser.Delete` (`deleteNOK`, `failure_class: not_supplied`). Route deletes through Supernova's own
  delete path with the real exception kept and shown in the delete warning's language ("Put.io didn't allow this
  file to be deleted" / the server's reason); check whether Put.io's WebDAV permits DELETE at all and say so plainly
  if not. Related: 4 Put.io scans fail with `Source is owned by a provider API` (provider vs WebDAV ownership): find
  which owner should win and fix.
- **D7. TMDB where-to-watch for TV shows: 43 × 404** on `tv/{id}/watch/providers` ("Availability service unavailable").
  Almost certainly the wrong id (a TVDB or episode id sent as a TMDB show id). Send the TMDB show id; cache a genuine
  404 as "no providers" for a day instead of retrying on every view.
- **D8. TV Shows posters: 123 failures, all `tv.grid` posters, local cache file missing (`FileNotFoundException`), no
  fallback.** When the cached file is gone, fetch it again from its source (and re-cache), never leave a blank poster.
- **D9. Home redraws that change nothing: 54 of 100 renders**, 27 triggered by `storage_changed`. Debounce storage
  broadcasts and skip a render whose cells are unchanged.
- **D10. Hourly background starts (31 at minute 0).** Find what wakes the app every hour (the scan scheduler is the
  likely cause); keep it if intended, but the woken process must do its job and exit, and must not start the UI
  process. The `:scanner` process sat empty for 30 min before Android ended it.
- **D11. Long freezes:** Details (7.6 % slow frames, worst 2.1 s), the subtitle downloader (4.5 s), the player (3.1 s),
  TV show page (2.0 s). Find the main-thread work behind each (network, database, bitmap decode) and move it off.
- **D12. Local import stuck at 0/0 (6 `local_import_stalled`)**: likely NOVA's lingering-notifications fix (N4 below);
  confirm after N4.
- **D13. Memory: peak about 640 MB during playback, 360–470 MB on library pages, native up to about 400 MB.** Don't
  chase it blind: build the diagnostics upgrade (group 5) so the next report gives memory per page; cap the artwork
  bitmap cache at a sensible size for the Shield; the real cut comes with 0.159's clean-out.

### Group 2 — B1, the Settings bug (owner, 26 Nov)
- **B1. Settings › System: Backup can't be focused.** Down from General jumps straight to Diagnostics, skipping the
  whole Backup section (What to back up, Backups folder, Back up now, Restore). Find why (rows not focusable, a focus
  order or next-focus override, a hidden view, or the in-row backup flow not built) and fix; every row reachable with
  Up / Down in order, and the focused row always on screen (D5). Test that walks every row of every Settings tab.

### Group 3 — the refined pages (build from the page sheets)
Build what's missing or differs from each sheet, with every action pressed and rendered (Definition of done):
- **Library pages:** `docs/design/pages/LIBRARY.md`.
- **Home:** `docs/design/pages/HOME.md` (+ RULES §14d one hero, one row).
- **Details:** `docs/design/pages/DETAILS.md`, including the **next-episode label** above the logo and the Status row
  ("Returning · S2 E4 on Sun 30 Nov") — BRIEF_0158_B › "Next episode on the hero"; the label appears on Home's hero too.
- **Player and Playback Settings:** `docs/design/pages/PLAYER.md`.
- **Search:** `docs/design/pages/SEARCH.md` (rewritten 25 Nov): the landing (tips under the keyboard always; Genres and
  People cards), progress badge A, streaming groups, the loading line, the nothing-found message, Voice, the library
  long-press menu on results; **Keep / Clear search history** rows in Settings › Library › Viewing History (SETTINGS.md).

### Group 4 — app-wide pieces (RULES)
The 0.158 top bar (§2) with blur behind it; the speckle (§1); one small-box shape (§3); focus and path; one tab
behaviour; artwork edge, focus ring and the corner badge (§14, §14c); menus, panels and the shared choice list (§9–11);
the Undo notice and delete warnings (§7b); steppers for 3+ choices (§8) on the pages above; **the one keyboard without
the Phone key** (§15: keep a real input connection on the box so the phone app types and pastes with no button —
BRIEF_0158_B › Search review › "No Phone key"); long lists, no lone word, sentence-case lines (§6); **changing text
never moves its icon** (§7). Check each against the pages in group 3.

### Group 5 — NOVA and the diagnostics upgrade
- **NOVA catch-up:** the eleven items in BRIEF_0158_B › "NOVA catch-up (full audit 25 Nov)" (IntroDB zero-start credits,
  Trakt sign-outs with trakt-java 6.22.0, USB after the TV wakes, lingering scan notifications, playing through
  notification sounds, passthrough on TV speakers, HDMI refresh-rate pause race, subtitle switched while paused,
  duplicate Samba discovery, picture-subtitle position and size if the engine honours them, subtitle default shadow).
  **Plus, new since the audit:** `aos-Video 60df7be7` (25 Nov) "restore skip button focus from the seek bar on TV":
  check against our player's skip button and focus (PLAYER.md) and take its intent if our player has the same gap.
- **Diagnostics upgrade:** BRIEF_0158_B › "Diagnostics upgrade" (start-up time, page switches, NOVA leftovers that
  run, the health summary with memory per page). It gives 0.159's clean-out its "before" numbers.

## Finish with
`docs/0.158/REPORT_0158_C.md`: what each signed APK contains, the run links, the "not built" list in plain words
(sent to the owner before they install), and the judgement calls (`docs/0.158/JUDGEMENT_CALLS_0158_C.md`). Update
`PROJECT_STATUS.md`, the roadmap, the release notes (each APK its own section and What's New entry) and `main`
(fast-forward to the signed source, README "Current version"). Ask the owner for a diagnostics report from the last
signed build: it is the baseline for 0.159's clean-out.
