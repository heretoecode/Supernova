# Brief: 0.158 part C (for Supernova Coding) — the refined pages, NOVA's fixes and the diagnostics read

Approved by the owner, 26 Nov 2026 ("let's get the library pages, Home, Details, Player and Search, the app-wide pieces,
the latest NOVA changes, and the Settings Backup bug"). Installed on the Shield: **0.158.7**. Follow CLAUDE.md
(Definition of done, Working unattended) to the letter: **hands-free until everything here is built** (owner, 26 Nov);
sign finished work as you go (standing approval), first signing **0.158.8** (code 1588), then .9 and so on.

## Part C continued (owner, 26 Nov, after 0.158.9)
0.158.9 built group 1, B1 and a slice of the pages, then stopped. **The rest of this brief is the job now, and the
redesigned pages come first.** Work through `REPORT_0158_C.md` › "Not built", line by line, in this order, each page
finished to its sheet (every action pressed and rendered) before the next:
1. **Search** (SEARCH.md): landing Genres and People cards, progress badge A, streaming headings, the loading row,
   the nothing-found panel, the long-press menu on results, Voice's "Listening…".
2. **Home** (HOME.md, RULES §14d): one shared hero and row component with Details, the filling carousel dot, cards 40 px
   apart with 8 % focus, row design A and the corner badge, Customise Home (Move · Hide · Edit on the focused row), row
   settings.
3. **Details** (DETAILS.md): every round 1–6 item not yet in (tab row line and height, More menu icons, the rest).
4. **Player** (PLAYER.md): the shared title block, Chapters · Episodes tab look, the 700 px panel, lists opened on top of
   the panel, Spatial audio's switch, the Seek preview stepper (Off · Chapter pictures · Video frames).
5. **Library pages** (LIBRARY.md): toolbar line and sizes, Filters / Sort / Order / Columns, captions, Customise page,
   delete warnings with versions.
6. **App-wide check** (group 4): every rule checked on every page above.
7. **NOVA leftovers:** N2's second half with trakt-java 6.22.0, N3, N6, N7, N8, N10, `60df7be7`.
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
