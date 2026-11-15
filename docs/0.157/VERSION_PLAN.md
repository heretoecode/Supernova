# What goes in 0.157 and what waits for 0.158 (draft, 14 Nov 2026)

Everything agreed and not yet coded, for the owner to split between versions. Size is relative, not days:
**S** small, **M** medium, **L** large (for scale: all 45 items of 0.156 took about two days from approved designs to
signed build). Each redesign includes everything
that lives on that page (e.g. Home includes Customise Home; library pages include Customise page, the old Edit Page).

## 1. Redesigns (all designed and approved unless marked)

| # | What | Includes | Size | Needs first |
| --- | --- | --- | --- | --- |
| R0 | **Shared foundation** | Panels and menus (95%, floating, stacking), the shared choice list, the one keyboard, focus and path, headings, artwork edge and focus growth, the move gesture, "no Android screens" (All files access asked once) | L | — (every redesign is built on it: must be in the first version) |
| R1 | **Home** | Hero (description, carousel dots, smoother changes), caption-box row cards, Move/Hide beside headings, Customise Home: Your Rows, Add a row with its options, icon picker | L | R0 |
| R2 | **Library pages** | Posters with caption box, Filters (incl. All/Unwatched/In progress/Watched, replacing Hide Watched), Sort (one Release date), Columns with the move gesture, list view, long-press menu, Customise page (ex Edit Page), empty pages and the + page (`library-pages/34`) | L | R0 |
| R3 | **Details** | Four kinds (film, series, season, episode), tabs Details · Episodes · Extras · More Like This, metadata row, full description pop-up, artwork picker, More menu, hero hand-over from Home | L | R0 |
| R4 | **Search** | Library first, "On your services" headings (From the world of / Related to / More from), recent searches with clear | M | R0 |
| R5 | **Player and Playback Settings** | Title block with age rating, Preparing playback, seek preview layout, centre loading ring, skip button in the control row, Playback tab (play mode, Stop playing at, Still watching), track names and pills, subtitle lists stacked on the panel | L | R0 |
| R6 | **Files** | Tabs incl. Transfers, Your Library stats, Library Care (Fix match page, re-check of matches, Library Health), Scan one source, connections add/remove, one-step delete, top-bar Files with scan ring and attention mark | L | R0 (R2's long-press Fix match opens Files' Fix match page) |
| R7 | **Settings** | Seven tabs, every setting kept/moved/merged/removed as in the inventory, Backup and Restore with progress, Diagnostics, About (credits, licences list with QR pop-ups, release notes) | L | R0 |
| R8 | **App icon, banner, splash and loading ring** | New mark (`brand/03` version 1, approved); the same mark spins as the app's one loading ring everywhere (RULES §21) | S | — |
| R9 | **Onboarding** | On Home under the top bar, never blocking: Welcome (Start fresh / Restore), Access, Add your videos, Languages, first scan; Restore: find, choose, restoring, sign in again, restart; smart steps (`onboarding/03`, approved) | M | R7's Restore |

## 2. New features (agreed behaviour)

| What | Size | Note |
| --- | --- | --- |
| Media Transfers: copy (folders, space check, Transfers page, verification), resilience, delete after verified copy (off by default) | L | Owner decided 0.157 (12 Nov); lives in R6 |
| Media Transfers: per-series "Bring new episodes" and the schedule | M | Owner decided 0.158 |
| Subtitles plan: ready during Preparing playback, next two episodes, "Full subtitles when the audio is in your language", Experimental "Also translate other languages", SDH/Foreign parts pills | M | Goes with R5 |
| More Like This from three sources; Reception stored offline; technical information per file | M | Goes with R3 |
| UK age ratings (with reasons) for films and episodes | M | Goes with R5/R3 |
| Smarter hero backdrops (calmer pick; hero and rows may differ) | S | Goes with R1 |
| Scan rules: no automatic scan within 15 min, one at a time, limit on scans at launch; Recent scans; reachability per connection | M | Goes with R6 |
| "Not a film" (keep a personal video out of Movies/TV Shows) | S | |
| Library statistics (eight stats) | S | Goes with R6 (Your Library) |
| Find it again, everywhere | S | Goes with R6 |
| Put.io sign-in by code | S | Goes with R6/R7 |
| Where to Watch region from the device, not hard-coded | S | |
| Remove picture in picture (recorded so it can return) | S | Decided |
| Remove Issue digest; diagnostics kept 2 weeks (1/2/4); export folder chosen once | S | Goes with R7 |
| Release notes and licences data tidied | S | Goes with R7 |

## 3. Major bugs

| What | Size |
| --- | --- |
| Library page rebuilt every minute during a Put.io scan (stalls up to 417 ms, the screen jump) | S |
| Skip button: shows 10 s whatever the setting, stays after rewinding past the start, fixed offset with controls open, not in diagnostics | S |
| Seek preview pictures missing on Put.io and local files | M |
| Episodes row missing in the player for some series | M |
| Chapter names after merging intro/credits parts | S |
| "Scanning local library…" pill stuck for 10+ minutes | S |
| Focus not restored after standby; reopening on the page you were on (§23.2) | M |
| Started titles stay in Recently Added instead of moving to Continue Watching | S |
| Fix match keyboard focus | S (fixed by R0's one keyboard) |
| Details tabs pop in; Home hero change waits for artwork | M |
| Files deleted elsewhere (e.g. on the Put.io website) not tidied away at the next scan | M |
| Focus jumping when holding Down | S |
| **Memory: the app was about 750 MB when closed (diagnostics, 10 Nov).** Likely why Android sometimes ends it in the background, so it reopens on Home with the splash instead of where you were. First add memory to diagnostics (per page: app, native, artwork cache), then cut the biggest users (likely full-size artwork kept in memory, NOVA's hidden screens, the page rebuilt every minute during scans) | M |
| JustWatch link opens nothing (becomes a plain credit line) | S |
| Still to check on the Shield: Put.io pause/resume sound out of step; network-drop recovery | S |

## 4. Engine and clean-up

| What | Size | Note |
| --- | --- | --- |
| Merge Codex's partial 0.157 delivery (13 of 23 items done) | M | Before anything else in coding |
| Legacy NOVA clean-out, SAFE list only (incl. NOVA's hidden grid and screens) | M | Approved |
| Seek loading engine (#20) | S | Needs a Shield test |
| Newer NOVA engine (#21) | L | Not started; suggest its own version |
| NOVA 6.6.5 and later, the TV-relevant parts (Trakt calls, matching by file name, Samba fixes, software-decoding notice, picture-subtitle position) | M | Notes §35; check Trakt first |
| Simpler backup categories; automatic sort-title rebuild; deletion steps in diagnostics | S | |

## 5. Admin (not code)

- Diagnostics report from the Shield before coding starts.
- Bring `main` up to date with 0.156.
- Make the repository private (or move the secrets); then a private Releases page.
- Is Codex back? Until then Claude does everything.
- "Going public" checklist kept for one day (history, titles, certificate contact, licence notices).

## 0.157 without the redesigns: what is left (14 Nov 2026)

**A. Engine and NOVA**
1. Merge Codex's unmerged 0.157 work (13 of 23 items: tab summaries, reachability, scan one source, scan signal,
   automatic scan choices, recent scans, Find it again everywhere, Not a film, Library Health lists, forgiving Fix
   match search, connections forms, folder artwork, More Like This sources …; each checked before it goes in).
2. NOVA 6.6.5 and later, the TV parts: Trakt's newer calls (checked first), matching by file name, Samba fixes,
   software-decoding notice, picture-subtitle position.
3. Seek loading signal (#20), tested on the Shield.

**B. Clearing out old NOVA**
4. The SAFE clean-out list: NOVA's hidden grid and hidden screens that still run behind Supernova's, unused code.
5. Remove picture in picture (recorded so it can return) and the Issue digest.
6. "No Android screens": ask for All files access once, so deleting and folder picking stay inside Supernova.

**C. Speed and reliability**
7. Memory: measure per page in diagnostics, then cut (about 750 MB today).
8. No page rebuild when a scan changes nothing (the minute-by-minute Put.io stalls).
9. Scan rules: one at a time, none within 15 minutes of the last, a limit on scans at launch.
10. Reopen on the page you were on with focus restored; after standby too. Focus not jumping when Down is held.
11. Network-drop recovery checked; Put.io pause/resume sound in step checked.

**D. Bugs**
12. Seek preview pictures on Put.io and local files. 13. Episodes row missing in the player for some series.
14. Chapter names after merging intro/credits. 15. Skip button: right duration, gone after rewinding, logged.
16. Stuck "Scanning…" pill. 17. Started titles leave Recently Added. 18. Files deleted elsewhere tidied at the next
scan, history kept. 19. Details tabs popping in; Home hero change waiting for artwork. 20. JustWatch link that opens
nothing becomes a plain credit line.

**E. Data and behaviour (no new screens)**
21. Subtitles: ready during Preparing playback, next two episodes fetched, full subtitles when the audio is in your
    language (the Experimental translate option comes with the Player redesign).
22. UK age ratings with reasons; Reception stored offline; technical information per file.
23. Smarter, calmer hero backdrops. 24. Where to Watch region from the device. 25. Automatic sort-title rebuild.
26. Diagnostics: kept 2 weeks (1/2/4), clear after export, memory and delete steps logged.
27. Simpler backup groups (the six agreed). 28. Release notes and licences data tidied.

**F. Foundation (R0), shared by every page**
29. Panels, menus, the shared choice list, focus and path, headings, artwork edge, the move gesture.
30. The one keyboard, with paste from the phone app and the password show/hide eye.
31. The new mark: app icon, banner, splash and the one loading ring.

Anything that needs a redesigned screen to show it (recent scans, reachability, Library Health lists, Not a film,
library statistics) arrives with that page; Files is in 0.157, so most do too.

## Owner's choices so far

- **0.157 must have (owner, 14 Nov 2026):** R6 **Files** (today's Network & Files is half old design, half new: it
  works, but adding a connection or changing folders is not pleasant). With it: R0 (Files is built on it), the first
  half of Media Transfers (already decided for 0.157; it lives in Files), Library Care with the one Fix match page.

- **Principle (owner, 14 Nov 2026):** everything that makes the app better under the bonnet goes in 0.157, whatever
  the redesigns: every bug in section 3 (all diagnostics findings included), memory, focus fixes, the NOVA 6.6.5+
  changes, the SAFE NOVA clean-out and the rest of section 4. Redesigns are then chosen on top (Files already in).
- **Most urgent (Claude, 14 Nov):** 1 memory (Android ends the app, you lose your place); 2 the library page rebuilt
  every minute during a Put.io scan (stalls, screen jumps; small fix); 3 focus lost after coming back to the app or
  from standby; 4 Trakt's API change (sync could stop quietly); then seek previews, the skip button, the Episodes
  row, the stuck scanning pill, Recently Added, files deleted elsewhere, scan limits.
- **Order of work:** fixes in shared code first (engine, scanning, memory, focus handling), so every redesign is
  built on solid ground. A focus bug on a page being redesigned in the same version is fixed in the new page, not
  twice; a page that waits keeps its fix in today's code.

- **NOVA 6.6.5+ back in 0.157 (owner, 15 Nov):** its "match by file name" fix explains the wrong matches, so all of
  NOVA's latest comes in; then re-check matches on Library Care.
- **Users and tags pinned for 0.158 (owner, 15 Nov), still in discussion** (REVISIT_LATER, Profiles).
- **Media Transfers: all of it in 0.158 (owner, 15 Nov);** Files in 0.157 without the Transfers tab.
  Decisions gathered in `docs/0.158/MEDIA_TRANSFERS.md`.
- **0.157 also gets R3 Details** (owner: the layers on the way to watching first). **R1 Home and R2 library pages: 0.158.**
- **Redesigns decided (owner, 14 Nov):** 0.157 also gets **R5 Player** (with Preparing playback). **0.158:** R7 Settings
  (its eight tiles were partly redesigned already and work well, unlike Network & Files), R9 onboarding, R4 Search.
  Open: R1 Home, R2 library pages, R3 Details.

## Claude's suggested split (owner decides; updated 14 Nov)

- **0.157:** R0 foundation, R6 Files (with Transfers' first half and Library Care), R2 library pages (their
  long-press Fix match opens Files' page), R5 Player, R8 brand, the major bugs (memory included), the NOVA 6.6.5 changes, the Codex
  merge and the SAFE clean-out.
- **0.158:** R1 Home, R3 Details, R4 Search, R7 Settings, R9 onboarding, the per-series transfers and schedule.
- **Later, on its own:** the newer NOVA engine.
- Why: Files makes 0.157 a big version, so the pages that already work well today (Home, Details, Search) wait one
  version; anything can still be swapped.

## 0.157 build order (started 15 Nov 2026, owner's go-ahead)

Each batch ends committed and pushed; a conformance build runs at the end of each larger batch.
1. **Start:** read the 14 Nov diagnostics (done: `DIAGNOSTICS_2026-11-14.md`); bring `main` up to date with 0.156
   (needs the owner's yes to push `main`); version 0.157.
2. **Codex merge:** check and merge Codex's unmerged 0.157 work (`codex/0157-2026-11-06`), one item at a time.
3. **NOVA 6.6.5+:** carry the TV-relevant changes across by hand (MediaLib, FileCoreLibrary, app; libdovi in the
   native build last).
4. **Quick, high-value fixes:** seek previews (null headers; hidden switch), no rebuild when nothing changed, scan
   rules, scan history store, dead connections, Recently Added, skip button, chapter names, stuck scanning pill.
5. **Memory and focus:** measure per page, cut; reopen where you were; focus after standby; held Down.
6. **Old NOVA out:** SAFE list, hidden grid and screens, picture in picture, Issue digest, All files access once.
7. **Foundation (R0):** panels, menus, choice list, focus and path, headings, artwork edge, move gesture, the one
   keyboard with phone paste and the password eye, the new mark and loading ring.
8. **Files (R6)** without Transfers. 9. **Details (R3)** with portrait cast. 10. **Player (R5)** with Preparing playback.
11. **Data:** subtitles plan, UK age ratings, reception offline, technical info, calmer hero backdrops, region, sort
    titles, diagnostics retention, backup groups, licences.
12. **Release notes, conformance, then the owner's Shield test and (when asked) signing.**
