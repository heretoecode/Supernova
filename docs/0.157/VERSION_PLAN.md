# What goes in 0.157 and what waits for 0.158 (draft, 14 Nov 2026)

Everything agreed and not yet coded, for the owner to split between versions. Size is a rough guide:
**S** small (a day or less), **M** medium (a few days), **L** large (a week or more). Each redesign includes everything
that lives on that page (e.g. Home includes Customise Home; library pages include Customise page, the old Edit Page).

## 1. Redesigns (all designed and approved unless marked)

| # | What | Includes | Size | Needs first |
| --- | --- | --- | --- | --- |
| R0 | **Shared foundation** | Panels and menus (95%, floating, stacking), the shared choice list, the one keyboard, focus and path, headings, artwork edge and focus growth, the move gesture, "no Android screens" (All files access asked once) | L | — (every redesign is built on it: must be in the first version) |
| R1 | **Home** | Hero (description, carousel dots, smoother changes), caption-box row cards, Move/Hide beside headings, Customise Home: Your Rows, Add a row with its options, icon picker | L | R0 |
| R2 | **Library pages** | Posters with caption box, Filters (incl. All/Unwatched/In progress/Watched, replacing Hide Watched), Sort (one Release date), Columns with the move gesture, list view, long-press menu, Customise page (ex Edit Page) | L | R0 |
| R3 | **Details** | Four kinds (film, series, season, episode), tabs Details · Episodes · Extras · More Like This, metadata row, full description pop-up, artwork picker, More menu, hero hand-over from Home | L | R0 |
| R4 | **Search** | Library first, "On your services" headings (From the world of / Related to / More from), recent searches with clear | M | R0 |
| R5 | **Player and Playback Settings** | Title block with age rating, Preparing playback, seek preview layout, centre loading ring, skip button in the control row, Playback tab (play mode, Stop playing at, Still watching), track names and pills, subtitle lists stacked on the panel | L | R0 |
| R6 | **Files** | Tabs incl. Transfers, Your Library stats, Library Care (Fix match page, re-check of matches, Library Health), Scan one source, connections add/remove, one-step delete, top-bar Files with scan ring and attention mark | L | R0 (R2's long-press Fix match opens Files' Fix match page) |
| R7 | **Settings** | Seven tabs, every setting kept/moved/merged/removed as in the inventory, Backup and Restore with progress, Diagnostics, About (credits, licences list with QR pop-ups, release notes) | L | R0 |
| R8 | **App icon, banner, splash and loading ring** | New mark (`brand/03` version 1, approved); the same mark spins as the app's one loading ring everywhere (RULES §21) | S | — |
| R9 | **Onboarding** *(not designed yet)* | First launch: Start fresh or Restore a backup, permission, add sources, language and subtitles, first scan | M | Mock-ups; R7's Restore |

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
| Focus jumping when holding Down; memory use | M |
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

## Claude's suggested split (owner decides)

- **0.157:** R0 foundation, R1 Home, R2 library pages, R3 Details, R4 Search, R5 Player, R8 brand, the major bugs,
  the Codex merge and the SAFE clean-out. (Everything you touch every evening.)
- **0.158:** R6 Files with Media Transfers, R7 Settings, R9 onboarding, the per-series transfers and schedule.
- **Later, on its own:** the newer NOVA engine.
- Dependency to watch: library long-press › Fix match opens Files' Fix match page. If Files waits for 0.158, 0.157
  keeps today's Fix match until then.
