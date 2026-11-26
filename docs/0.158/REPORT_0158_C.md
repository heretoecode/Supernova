# 0.158 part C: report (overnight 26 Nov 2026)

Brief: `BRIEF_0158_C.md`. Diagnostics read: `DIAGNOSTICS_0158_7.md`. Judgement calls: `JUDGEMENT_CALLS_0158_C.md`.
Release notes: `RELEASE_NOTES.md`. Work branch: `claude/supernova-github-access-yfhe89`.

## The APKs

| APK | Source | Conformance | Signing | What it holds |
| --- | --- | --- | --- | --- |
| **0.158.8** (code 1588) | `ecd7066` | run 45750826340: all 2226 tests passed; lint found 2 errors (an Android 7 call in the start-up timer, `String.join`) | **Not signed**; fixed in `d1502b3`, everything carried into 0.158.9 | Group 1 (D1–D13), B1, the next-episode label, Keep / Clear search history, no Phone key, NOVA N1 N2½ N4 N5 N9, the diagnostics upgrade |
| **0.158.9** (code 1589) | `a4bf56e` | run 45764555253 (attempt 2 green: all 2229 tests, lint, tooling; attempt 1 had one flaky library-scroll test). Earlier tries: `d1502b3` (lint), `e325203` (the workflow still expected 0.158.7) | **Signed 26 Nov 2026**, run 45774774374; artifact `supernova-0157-signed-qa` (`Supernova_0.158.9.apk`), kept until 10 Dec 2026: https://github.com/heretoecode/Supernova/actions/runs/45774774374/artifacts/15095636245 **Install this one.** | Everything in 0.158.8, plus Home's one-button hero and trimmed More (Exclude from hero), no Move / Hide on Home, the top bar's grey box, player seek box and language names, Audio boost stepper, Search tips under the keyboard, six posters a row, Mark as watched without a notice |

## What each item became

### Group 1 — the diagnostics fixes
- **D1** crash while matching: fixed (the matcher defers the title instead of crashing; test).
- **D2** scans failing at the scan lock: a scan whose wait is cut short by the service closing or the app leaving
  now shows as waiting and runs again when the app is back; a scan cancelled on purpose still fails (tests).
- **D3** discovery listener crash: was already fixed in 0.158.1; a helper and a test added.
- **D4** hidden NOVA grid taking focus: sealed so it can never hold focus (test).
- **D5 + B1** Settings: Up / Down reach every row in order, Backup included; the focused row is always on screen
  (a test walks every tab).
- **D6** delete over WebDAV: the real reason is kept and shown ("Put.io looks after this file … delete it on Put.io";
  server refusals, errors and lost connections have their own words); Put.io-owned WebDAV scans are skipped, not
  failed. Put.io-managed files are still not deleted over WebDAV (J6).
- **D7** TMDB where-to-watch 404s: an old TVDB show id is looked up once on TMDB; a genuine 404 is kept for a day.
- **D8** missing TV posters: fetched again from their source once per run.
- **D9** Home redraws: storage bursts settle before one render; renders that change nothing stop early.
- **D10** hourly wake-ups: the rescan timer pauses while the app is away; a rescan that fell due runs on return.
- **D11** long freezes: diagnostics now name what the main thread was running in any freeze of a second or more;
  no code moved yet (J10).
- **D12** import stuck at 0/0: NOVA's notification fix (N4) taken; confirm with the next report.
- **D13** memory: artwork memory capped at 48 MB and emptied under pressure; memory per page now in the report.

### Group 2 — B1: see D5 above.

### Group 3 — the refined pages (partly)
- **Details:** the next-episode label above the logo and Status "Returning · S2 E4 on Sun 29 Nov".
- **Home:** the same label on the hero; one button, More (Details · Playback · This Title with Exclude from hero);
  no Move / Hide on the rows.
- **Player:** seek previews never show a grey box (time box alone when no picture or the setting is off); track
  languages in words ("chi" → Chinese); Audio boost always the stepper.
- **Search:** Keep / Clear search history in Settings; tips under the keyboard at all times; three recent searches.
- **Library pages:** six posters a row; Mark as watched with no notice.

### Group 4 — app-wide
- The one keyboard without the Phone key (needs a Shield check with the phone app, J14).
- The top bar's grey "open page" box leaves the old page at once.

### Group 5 — NOVA and diagnostics
- NOVA: N1, N2 (first half), N4, N5, N9 taken; N11 needed nothing. See J15 for the rest.
- Diagnostics upgrade: start-up time (cold / warm, first frame, first focus), page switches, NOVA pieces that ran,
  and a health summary with memory per page at the top of each report.

## Not built (plain words)
- **Home:** one shared hero and row component for Home and Details (RULES §14d); the filling carousel dot; cards 40 px
  apart with an 8 % focus growth; row design A (title over the artwork's foot) and the corner badge on Home rows;
  Customise Home with Move · Hide · Edit on the focused row; row settings on today's rules; Files › Library Care ›
  Excluded from hero (the list to include titles again; until then, Undo is the only way back).
- **Details:** round 1 review items beyond the next-episode label (tab row line, tab height, More menu icons etc.).
- **Player:** the title block shared with Preparing playback; Chapters · Episodes tab look; the 700 px panel; the lists
  opened on top of the panel (Subtitle track, Download subtitles, Audio track); Spatial audio's switch; the "Seek
  preview: Off · Chapter pictures · Video frames" stepper (it is still an on / off switch); the value alignment and
  focused-tab colour faults (they look right in the renders; the Shield photos need checking against 0.158.9).
- **Search:** the landing's Genres and People cards; progress badge A on results; streaming groups' new headings; the
  "Looking on your services" loading row; the centred nothing-found panel with its two lines; the long-press menu on
  results; Voice's "Listening…" field.
- **Library pages:** the toolbar line and sizes, Filters / Sort / Order / Columns redraws, caption box colour and
  spacing, Customise page panel, delete warnings with versions.
- **App-wide (group 4):** the speckle, small-box shape, focus-and-path, one tab behaviour, menus / panels / shared choice
  list, Undo and delete warnings, steppers, long lists, "changing text never moves its icon" were not re-checked page
  by page.
- **NOVA:** N2's second half and trakt-java 6.22.0, N3 (USB after wake), N6 (passthrough on TV speakers), N7 (HDMI
  refresh pause race), N8 (subtitle switched while paused), N10 (picture-subtitle position), 60df7be7 (skip focus).
- **Settings:** the in-row backup flow (waits for the folder decision).

## For the owner
- Install the newest signed APK (see the top table).
- Then please send a **diagnostics report** from it: it is the baseline for 0.159's clean-out, and the first with the
  health summary and the freeze traces.
- Please check with the phone app that typing and pasting reach Search and a password box (no Phone key now).

## Part C continued (26 Nov, after 0.158.9)

### 0.158.10 (code 1590)
**Signed 26 Nov 2026**: signing run 45879328975, from green conformance run 45872415548 on `2f55102`; artifact `supernova-0157-signed-qa` (`Supernova_0.158.10.apk`), kept until 10 Dec 2026: https://github.com/heretoecode/Supernova/actions/runs/45879328975/artifacts/15146537628 **Install this one.** Built from `claude/supernova-part-c-build-a9mvtg`, fast-forwarded onto the
working branch (J20).

- **Search (item 1), to its sheet:** landing Genres and People cards; progress badge A on 16:9 thumbnails; Resume-only
  pills; "Stream more from the world of / related to …" headings; the "Looking on your services" row that fades out;
  the centred nothing-found panel; the library long-press menu on results (and the Menu key); Voice's "Listening…".
  Renders `0158c-search-01`–`09`, test `Review0158cSearchTest`.
- **Shared pieces:** the progress badge (RULES §14c) on Search, Home rows and library posters; one library long-press
  menu; one delete warning (versions, episodes, Clean Up); one hero text block for Home and Details (RULES §14d); the
  lifted-pen line; steppers for 3+ choices in side panels.
- **Home (item 2, partly):** row design A with the badge, 40 px apart, 8 % focus; full-width hero, text anchored to
  the foot; filling carousel dot; hero More with Playback first; Customise Home with Shown / Hidden and Move · Hide ·
  Edit on the focused row; row settings (Sort list, inline delete with Undo); presses at a row's end stay put (D17).
- **Details (item 3, partly):** the one tab behaviour (box on the focused tab, underline on the open one, the toolbar
  line); "TMDB page QR code"; Remove from library's own icon; Delete file uses the new warning.
- **Player (item 4, partly):** Playback Settings about 700 px; Seek preview Off · Chapter pictures · Video frames.
- **Library pages (item 5, partly):** caption `#141619`, about 100 px, even spacing; the toolbar line; delete warnings
  with versions; Movie long-press gains Play from the start when started.
- **Item 9:** every failure records its reason (with "not_known", what was tried, the wait and the last thing that
  worked when unknown); top reasons in the summary; player errors in words; D15 duplicates kept once.
- **Bugs:** M2 (already-gone deletes), M6 (rename), M7 (By Library Page size); Your Library gains By Source and
  Biggest Movies.

### Not built (plain words)
- **Home:** the row component is not yet literally shared with Details' rows (Episodes, Extras, More Like This still
  use their own cards); Excluded from hero list in Library Care; the off-screen text label that sometimes takes focus
  (D17) is not found yet.
- **Details:** Episodes tab's season card, Extras player, More Like This "On Netflix" rows, the Subtitles side panel
  (Built In · Subtitle Files · Online) and Find subtitles online, the Artwork panel's redesign, the hero's foot peeking
  above the tabs, the four kinds' Details tab rows, the blur behind the top bar.
- **Player:** the title block shared with Preparing playback; Chapters · Episodes tab look; the lists opened on top of
  the panel; Spatial audio's switch (it shows greyed because the Shield's Android has no spatialiser; a render check
  is still to do).
- **Search:** "Stream more from <person>"; the genre heading icon is the funnel, not a tag.
- **Library pages:** Filters / Sort / Order / Columns redraws; Customise page panel redraw; the Not in library pill on
  show deletes; Trakt history removal on delete (the switch shows but does nothing).
- **App-wide check (item 6):** the speckle, small boxes, focus and path, menus and panels were not re-checked page by
  page; the artwork edge is still 0.75 dp at 60 % (RULES says solid 1 px) and the focus ring 3 dp (RULES 4 px).
- **Files (item 10):** Scan library narration; browsing changes (sort menu, folder sizes, Fix match on one-title
  folders, empty folder); Connections form (Type menu, Encryption greyed with reasons, Network options moved); Library
  Care lists with their "this is fine" buttons, Not matched pending line (M5), To fix, Excluded from hero, the Fix
  match page, Scan one source menu, focusable recent scans and Scan details.
- **Bugs:** M3 (marking missing on a failed open), M4 (scans judging folder by folder; it lives in MediaLib's patched
  scanner), M5 (pending look-ups counted as Not matched); D14 (scan only when there is something to find), D16 (TMDB
  404 for a title), D18 (memory trims).
- **NOVA (item 7):** the check of NOVA's commits since 25 Nov and the leftovers (N2's second half with trakt-java 6.22.0,
  N3, N6, N7, N8, N10, `60df7be7`).

### NOVA check (item 7, 26 Nov, ~17:00 UTC): everything merged since the 25 Nov audit
Checked NOVA's main branches of aos-Video, aos-MediaLib and aos-FileCoreLibrary; no new release tag.

| Repo · commit | Date | What | Taken? |
| --- | --- | --- | --- |
| aos-Video `60df7be7` | 25 Nov | Restore skip-button focus from the seek bar on TV | Not yet: in the brief already; under a day old, so it waits for the "a few days old, not reverted" rule (next run). |
| aos-MediaLib `220f573` | 25 Nov | ShowTags: don't reuse a database episode from another show with the same title (+ test) | Not yet: a fix we likely suffer (two shows with an episode of the same name), but under a day old; take it at the next check if not reverted. |
| aos-MediaLib `0459d28` | 25 Nov | ShowTags: share the premiered-date-to-year conversion | Skipped: tidy-up only. |
| aos-MediaLib `26344b0`, `778b235` | 25 Nov | Scraper integration test cases and logging | Skipped: tests for NOVA's own suite. |
| aos-FileCoreLibrary | — | Nothing new | — |
