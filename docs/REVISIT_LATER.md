# To revisit later

Ideas and decisions the owner wants to come back to. Check this list when planning each release.
Gathered 4 Nov 2026 from this session and the older registers (`docs/Future Development/`, `docs/design/PARKED_FUTURE.md`,
`docs/archive/early-planning/project-decisions/POST_0139_NON_MAINTENANCE_FEATURE_BACKLOG.md`, `docs/design/CODEX_WORK_QUEUE_2026-10-27.md`).
Items already built or scheduled are left out (see the bottom of this file).

## Ratings without a personal OMDb key — added 4 Nov 2026
- **Decision (owner):** do not build an OMDb key into the app for now. Reception's IMDb, Rotten Tomatoes and
  Metacritic scores keep using the key typed into Settings (`omdb_api_key`, `metadata/OmdbSupplement.java`,
  900 lookups/day budget).
- TMDB "just works" because Supernova uses NOVA's own TMDB key shipped in the app
  (`aos-MediaLib/res/values/donottranslate.xml`).
- Agreed for 0.156: OMDb ratings are saved per title and refreshed at most weekly
  (`leanback/details/PreviewDetailsData.java`).
- **Options:** (1) one key built in at build time from a GitHub secret, so ratings need no user action (free tier
  1,000/day, paid tier more; a key inside an APK can be extracted); (2) MDBList (adds Letterboxd/Trakt; also a key);
  (3) keyless scores only (TMDB, Trakt) without IMDb/RT/Metacritic.
- **Revisit when:** Supernova is shared with anyone else, or the daily limit is reached.
- Notes: `docs/0.155/OWNER_NOTES.md` item 34, `docs/0.155/DATA_PROVIDERS.md`.

## Diagnostics privacy — added 4 Nov 2026
- Diagnostics record everything in full detail because the owner is the only user. Cut back (or make opt-in) before
  Supernova is ever shared. Notes: `docs/0.155/OWNER_NOTES.md` item 29, `docs/codex/BRIEF_0156_THUMBS_DIAGNOSTICS.md`.

## Parked by the owner, 5 Nov 2026 (to revisit soon)
- **Details page and its tabs** — redesign; owner wants to revisit in the near future.
- **Library statistics on each library page** — mainly the colouring (should it stay blue?), possibly the design;
  discuss together with the Details page.
- ~~GitHub cleanup~~ — **done by Claude on 5 Nov 2026** at the owner's request: `CLAUDE.md`, `AGENTS.md`, new README
  and PROJECT_STATUS, `docs/archive/`, and GitHub Issues #31–#102 (source in `docs/issues/`).

## From this session (4 Nov 2026)
- **Discovery in Search** — owner: "maybe in the future but not for now". Search stays library-only in 0.156
  (`docs/0.155/OWNER_NOTES.md` item 20).
- **Rectangular QR codes** — not used because most phone cameras can't read them; square kept (item 32).

## Parked ideas from the older registers
| ID | Idea | Where it stands |
|---|---|---|
| FD-004 | Shield Home / Android TV launcher channels (rows on the Shield home screen) | Parked; which rows, consent and on/off controls undecided |
| FD-011 | Profiles (separate history, Continue Watching, settings per person) | Parked |
| FD-013 | Discovery / Coming Soon page | Parked; Home must not become advertising |
| FD-016 | Person pages (cast and crew filmographies) | Parked |
| FD-017 | More than one custom Library Page | Parked; check the current limit when revisiting |
| FD-018 | Anime-specific metadata and seasons | Concept only |
| FD-020 | Subtitle presets (saved appearance sets) | Concept only |
| FD-021 | Playback intelligence (smarter version/track choices) | Concept only; per-series audio/subtitle memory is in 0.156 (in principle) |
| FD-022 | Expanded ratings | Partly built (OMDb with the owner's key) — see the OMDb entry above |
| FD-023 | Trakt community reviews and social features | Concept only |
| FD-024 | AI features (matching, recommendations, natural-language search) | Concept only; cost and privacy concerns |
| FD-025 | Personalisation (layouts, themes) | Concept only; note Accent & Colour was removed in the Settings review |
| FD-026 | Other integrations (more metadata/media services) | Concept only; avoid duplicating current providers |
| FD-027 | Downloads (copy WebDAV files to the Shield) | Parked |
| FD-028 | Detailed content warnings at playback start | Parked; no reliable source found (TMDB descriptors empty) |
| FD-032 | Fully automatic Google Drive release publishing | Parked; today the signed APK is a GitHub artifact because Drive secrets aren't set |
| — | Less manual GitHub work at the end of Codex sessions | Parked by the owner (workflow/permission automation) |

## Closed by the owner, 5 Nov 2026
- Smart Collections — not wanted (custom Library Pages cover it). Issue #94.
- Put.io improvements — already built and working. Issue #95.
- Where to Watch opening the service — already works; the "paused" code comment was out of date. Issue #102.

## Moved out of "parked" (built or scheduled)
- Seek thumbnails (FD-019) — experimental in 0.156. Library Health (FD-015) — built. Network & Files and Settings
  redesigns, playback HUD (FD-002/005/006) — built or in 0.156. Broader screen audits (FD-029) — done through the
  0.154–0.156 reviews. Age rating overlay (FD-001) — built.

## Exploring (owner, 5 Nov 2026) — not tied to any version, may not happen
- **Pages without a panel:** what pages look like with no panel, the charcoal (#16181C) filling the whole page and
  the content using the full width. Mock-ups `docs/design/proposals/no-panel/01` (Library Health, panel removed)
  and `02` (Library Health using the full page: bigger header and text, tall rows). Just a look for now.
  `03` Home, `04` Settings › Playback, `05` Movies library, `06` Movies with the approved stats row, `07` Network &
  Files landing, all on the
  charcoal with no panel.
  Real renders (5 Nov, background swapped only, panels kept): Home and Movies on Space Black and on charcoal, by
  `preview-renders.yml` with `"variants": ["normal", "charcoal"]`. Owner: Movies, TV Shows and custom pages share
  one design, so only one library page needs designing.
  Network & Files shade options (real renders, 5 Nov): owner prefers option 2 (page #16181C, tiles #22252B) and
  option 3 (page #0E1013, tiles #1C1F24), and asked for stronger outlines on unfocused tiles: rendered at 2 px
  28% and 2 px 40% white. Waiting for the owner's pick.
- **Network & Files as one page with tabs** (owner idea, 5 Nov; not tied to any version, separate from 0.156/0.157):
  no landing tiles and no panels. Tabs under the top bar: Local Storage · Network Shares (Add connection button) ·
  Library (Scan now, scanning, recent scans, Library Health). Opens on the last tab used; first visit Local
  Storage. Folders open below the tabs with a breadcrumb, keeping Grid/List, Sort, Videos only/All files and the
  inline actions; no "Recently added" (owner). Network Shares holds saved connections (Open, Edit, Test, Remove)
  and Add connection, which opens in the same tab. Mock-ups `docs/design/proposals/network-files-tabs/01`–`09`.
  v3 (owner notes 6 Nov): tabs in a tray with a line under the tab row; a summary per tab at the right of the tab
  row; one status column on every row (In library · Excluded · Not added; Reachable · Not reachable) that stays put
  when the row is focused, with the row's buttons after it; grid uses the Home row card (16:9, title and year
  inside on a shade, state chip top right); Add a connection is the last row of Your connections, not a corner
  button, because it opens a page, not a pop-up.
  v4 (owner notes 6 Nov): everything larger now the panel is gone; status pills are plain text in an outline
  (no colour dots, no strike-through), the same for library state and reachability, in a fixed column between the
  names (long names are shortened) and the buttons; grid folders show a mosaic of the artwork inside (plain card if
  none), library files their artwork, other files a still from the video where one can be made; Add a connection
  shows its types inline when focused (Computer or NAS · WebDAV · SFTP · More).
  v5 (owner notes 6 Nov): tabs lower, with more room above and below; summary text larger; every status pill
  the same width. Choosing a type opens its form as a page in the same tab (breadcrumb Network Shares › Add a
  connection › WebDAV server); OK on a field opens the app's keyboard as a side panel beside the form (Done
  returns); More opens a side panel with FTP, FTP with TLS and WebDAV without encryption. Mock-ups 10–11.

## Parked (owner, 5 Nov 2026) — no version
- **Supernova screensaver** (approved design, owner notes 0.155 #27, mock-ups `docs/design/proposals/settings/16`–`17`):
  library backdrops with a slow drift and cross-fade, title and clock in the corners; Appearance gets Supernova
  screensaver (with Open Android settings), Shows (Films · TV shows · Both), Change picture every (10 · 20 · 30 s) and
  Show the title and clock. Taken out of 0.156 by the owner; not scheduled for 0.157 either. The rest of Appearance
  (interface language) stays in 0.156.
