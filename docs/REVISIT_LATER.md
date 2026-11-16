# Parked

Everything parked: ideas and decisions not assigned to any version (the roadmap's **Parked** column). When the owner
says "park that", it goes here. When something the owner raises matches a parked idea (e.g. colour changes → Themes,
another person's history → Profiles), it is added to that idea's notes here, so each idea builds up over time.
Check this list when planning each release.
Gathered 4 Nov 2026 from this session and the older registers (`docs/Future Development/`, `docs/design/PARKED_FUTURE.md`,
`docs/archive/early-planning/project-decisions/POST_0139_NON_MAINTENANCE_FEATURE_BACKLOG.md`, `docs/design/CODEX_WORK_QUEUE_2026-10-27.md`).
Items already built or scheduled are left out (see the bottom of this file).

## Release notes, properly (owner, 14 Nov 2026) — to discuss
- 0.156's notes are two vague lines. Rethink how notes are written and shown, and whether to rewrite older
  versions' notes from their records. 0.157's own notes will already be written properly.

## Supernova's own TMDB key (owner, 14 Nov 2026)
- Supernova still uses NOVA's built-in TMDB key. TMDB keys belong to one application, so Supernova needs its own
  before it is shared or public. Part of the "going public" checklist.

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
| FD-011 | Profiles (separate history, Continue Watching, settings per person) | Parked; discussion below (15 Nov) |
| FD-013 | Discovery / Coming Soon page | Parked; Home must not become advertising |
| FD-016 | Person pages (cast and crew filmographies) | Parked |
| FD-017 | More than one custom Library Page | Parked; check the current limit when revisiting. Owner 15 Nov: maybe a fourth page one day; dropping "SUPERNOVA" from the top bar makes room |
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
  v6 (owner notes 6 Nov): the summary is the same size as the tabs, with an icon per item; the form page shows
  the keyboard on the page itself (no side panel); breadcrumbs start below the tab (the tab already says where you
  are: "Backup #1 › Films", "New WebDAV connection") and are signposts, not buttons — Back goes up one level.
  v7 (owner notes 6 Nov): summary words capitalised with a dot between items; More opens a small menu above the
  More button in the More-menu style (heading, icon rows, white focus), and a choice opens its form page like
  WebDAV's. The top bar (tabs and summary) is confirmed by the owner; the More menu has no heading.
  Library tab (v7, to discuss): one full-width list in the same row style — Scan now (Up to date; Scan now ·
  Scan one source), Library Health (Missing files, Couldn't be read, Not matched, with counts in the pills and
  Review buttons), Scan settings (Automatic scan, When Supernova opens), Recent scans (last 10). Mock-ups 09, 09b.
  v8 (owner notes 6 Nov): pop-up menus get a faint 28% edge on the flat background (the same as card edges); Add a
  connection shows Computer or NAS · WebDAV · SFTP · FTP (no More: WebDAV and FTP each have an Encryption switch
  on their form, on by default); Automatic scan is an inline choice (Off · Hourly · 2 hours · 6 hours · Daily —
  "Twice a day" dropped to keep five) and When Supernova opens a switch; Library Health opens one page per kind in
  the Library tab (Missing files: Find it again · Remove; Couldn't be read: Try again · Exclude; Not matched: Fix
  match · Not a film). Mock-ups 06, 08, 09b, 11–14.
  Owner, 6 Nov: tabs renamed on Claude's recommendation to This Shield · Connections · Library Care; Recent scans
  shows 3 with "Show all scans" for the last 10; Scan settings live in Library Care (Settings links there).
  v9: menus have an icon per item and the tick on the right, and are as wide as their longest item. Find it again
  shows "Searching…" in the row (Stop), then a notice "Conclave is back in your library" and the row leaves; not
  found says so and offers Search everywhere · Remove. Fix match opens the side panel (edge on the left) with the
  name filled in; Not a film acts straight away with a notice and Undo. Mock-ups 11, 12b–d, 14b–c.
  v10: menu rule made global (CLAUDE.md, 0.157 notes #15); Sort icons are unique and mirrored (A→Z / Z→A,
  long-to-short / short-to-long bars, clock / hourglass). Fix match in the redesign is a page, not a side panel:
  search field and keyboard on the left, TMDB results with posters on the right ("Best match" pill). Mock-up 14b.
  v11 (owner, 6 Nov): Fix match uses the global keyboard (numbers row, / . @, Shift, #+=, voice, Space, delete,
  Clear) with "what you can search" under it, and is as forgiving as Search: punctuation, capitals and a leading
  "The" ignored, original titles, one-letter typos, and TMDB, IMDb (tt…), TVDB or Trakt
  numbers. Note: Search looks in the local library; Fix match asks TMDB, which is stricter, so the forgiveness
  (cleaned query, dotted-initials and "The" variants, typo retry) and the Trakt/TVDB number look-ups are engine
  work for Codex when this is built. Trakt has a numeric ID and a slug for every film and show.
  v12 (owner, 6 Nov): Network & Files always opens on the first tab (This Shield). The rest of the journey drawn,
  for confirmation: scan running (progress in the row, Stop), inside a connection (same as a folder), still waiting /
  drive removed, empty pages (no USB drive, no connections, "Everything looks fine"), Test connection result on the
  form, Remove with Undo, Find on my network, Put.io signed out and its sign-in code, the Menu-button menu on a row,
  Show all scans, and a count badge on the Library Care tab. Mock-ups 15–29.
  v13 (owner, 6 Nov): scan row title is the source only (the pill says Scanning…); the tab tray and the summary
  always keep a clear gap (56 px at 1080p; the summary shrinks before they may touch); while a scan runs, a small
  spinning ring sits after "Network & Files" in the global top bar on every page, replacing the bottom-right
  "Scanning local library…" pill. Owner accepts Claude's recommendations on everything else in 15–29.
  Status: the Network & Files redesign is complete as a design (mock-ups 01–30); not tied to a version yet.
  Top bar (owner, 6 Nov): "Network & Files" is renamed **Files** (with the redesign), and the scan ring sits on its
  left so nothing shifts (mock-up 31, option B), the same size and line weight as the search and settings icons. No dots between top-bar items: dots separate facts in a line of
  text (like the summary), not places and buttons.
  Background (owner, 6 Nov): the Files redesign uses the plain charcoal page, not the Space Black artwork. Other pages
  will follow the same principles later, Settings first, each with its own mock-ups — see
  `docs/design/FLAT_PAGE_PRINCIPLES.md`.
  **Scheduled (owner, 6 Nov 2026): the Files redesign is a 0.157 item** (0.157 owner notes §16).

## Parked (owner, 5 Nov 2026) — no version
- **Supernova screensaver** (approved design, owner notes 0.155 #27, mock-ups `docs/design/proposals/settings/16`–`17`):
  library backdrops with a slow drift and cross-fade, title and clock in the corners; Appearance gets Supernova
  screensaver (with Open Android settings), Shows (Films · TV shows · Both), Change picture every (10 · 20 · 30 s) and
  Show the title and clock. Taken out of 0.156 by the owner; not scheduled for 0.157 either. The rest of Appearance
  (interface language) stays in 0.156.

## Picture in picture (owner, 6 Nov 2026)
Removed in 0.157: the owner has never needed it and finds it awkward in other apps; in 0.156 choosing it did nothing.
0.156 had fixed its subtitles and return to full screen (release item 15), so that code is the starting point if it
is ever wanted back.


## Profiles: the owner's use case (15 Nov 2026, discussion)
- Mostly one person; sometimes watching with a partner; the partner has some titles the owner would not watch.
- Three kinds of titles: **mine, my partner's, shared**. None walled off: being on one profile must never block
  watching something together, or reaching the shared titles.
- Today's folders are just TV and Movies; a partner folder could be added later.
- Claude's first thoughts: separate the two jobs profiles usually lump together:
  1. **Whose progress is recorded** (history, Continue Watching, watched ticks, recommendations): per person.
  2. **What is shown first** (Home rows, library pages): leaning towards each person's titles plus shared, but
     nothing hidden: everything stays reachable (Search, a filter), no locked libraries.
- Titles get an owner from their folder (Mine / Partner / Shared), changeable per title.
- **"Watching together"**: a third choice beside the two people ("Who's watching: Me · Partner · Together"). Together
  records progress for both people (an episode watched together is ticked for both) and keeps its own Continue
  Watching for shows you only watch as a pair.
- Switching should be one press from the top bar, never a full sign-in.
- Open questions: does Together need its own progress, or just tick both people? Should the partner's titles appear
  on the owner's Home at all, or only through Search and a filter? PIN or no PIN (probably none)?

### Tags and views instead of profiles (owner, 15 Nov 2026; follows the Profiles discussion above)
- **Owner's preference: no profiles at all**, one library with access to everything, without being drowned. No
  separate folders either. Real cases: a show started alone that the partner then wants to watch together; films
  only for the owner, only for the partner, or for both; rewatchable favourites ("Classics") kept but not wanted in
  view day to day.
- **Tags (owner's idea):** long-press a title › Tags: tick existing tags or create one (any name: a person's name,
  Together, Classics). A series' tag covers its episodes. Tags become a Filters option on library pages.
- **Many at once:** a select mode (from list view or the long-press menu): OK ticks titles, "Select all" then untick
  the exceptions, then "Tag 79 titles".
- **Global view (owner's "master tag"):** one switch that applies everywhere: Home's hero and rows, every library page,
  Continue Watching. Page filters work on top of it; Home changes only with the global view. Stays set until changed.
- **Claude's refinements:**
  - Filters need **hide** as well as **only**: "only Partner", but also "hide Classics". Hiding means very little
    tagging: tag the few partner titles and Classics, then the owner's view simply hides those two tags; untagged
    titles count as everyone's.
  - **Views** = saved sets of tag rules, chosen from a small chip in the top bar ("Viewing: Everything"): e.g. Me
    (hide Partner, hide Classics), Partner (only Partner or Together), Together (only Together), Everything.
    Switching is one press; nothing is locked; Search always finds everything.
  - Limit to accept: one shared history and progress (no per-person Continue Watching); the view keeps the other
    person's half-watched titles out of sight.
- Where it could land: with the library pages redesign (0.158) or later; owner to decide.
- **Owner, 15 Nov:** still a discussion item (parked). Tags fit what profiles were meant to do. Hide/Only works like
  the order button: OK on a tag in Filters cycles **Off › Hide › Only**. The view switch sits at the **start of the
  top bar, left of Home**, because it decides what Home shows. First look: `design/proposals/tags-views/01`.
- **The shared-history limit, made precise:** it only matters for titles **both** people watch **separately**
  (e.g. both following one series at different paces: one resume point; an episode watched alone ticks it for the
  other). Titles tagged as one person's (e.g. the partner's series) don't matter: their progress is theirs anyway
  and hidden from the other's view. Together titles watched together are fine too.
- **Owner, 15 Nov (round 2):** (1) a long view name must never push the tabs right (the third page could run into
  Files): use a **fixed-size icon** instead of a labelled button. (2) **Drop "SUPERNOVA" from the top bar**: you know
  which app you are in, and it frees room; the icon takes its place at the far left. (3) Call it the **user switcher**
  ("user switching menu"), not profiles. (4) **Two kinds of tags:** **user tags** (Me, Alex, Together: the ones the
  user switcher uses) and **general tags** (Christmas, Classics: collections anyone makes). A title can have both, e.g.
  Together + Christmas, or Me + Classics.
- **Claude, round 2:** the icon should still say which view is on without words: a round badge with the person's
  initial (M, A), two overlapping circles for Together, the grid for Everything; same size whatever the name. Long-
  press › Tags opens one panel with two groups, **Users** and **Tags**; library Filters show the same two groups, each
  tag cycling Off › Hide › Only. Views mostly use user tags but may also use general ones (Me can hide Classics).
  Bonus: a general tag can feed a Home row or the custom library page (Customise Home › Add a row › Tag: Christmas),
  so the Christmas films appear as a row in December and vanish otherwise.
- Open: can views use general tags, or users only? Who can create user tags (everyone, or only in Settings)?
- **Owner, 15 Nov (round 3):** each user picks their own **monochrome icon** (like Netflix/Disney+ profile pictures,
  but icons); Together defaults to two overlapping people. Tags show on the **Details page**, and Details › More has
  **Tags** too. Dropping "SUPERNOVA" also leaves room for **more library pages** later (see FD-017).
- **Claude, round 3:** the user icon picker reuses the approved Customise Home icon grid (one picker app-wide). On
  Details, tags appear as two Key information rows, **Users** and **Tags** (label/value like every other fact), and the
  More menu's Tags row shows them as its subtitle. Mock-up `tags-views/02` (v2): top bar without the name, the user
  switcher icon and its menu; Details › More › Tags; the Tags list stacked on More (Users and Tags groups, New tag).
- Clarified questions: (a) users are created in one place, "Edit users and views" (name and icon), so the Tags
  list's Users group has no "New user" and a typo can't create a stray person; general tags can be made anywhere
  ("New tag"). (b) "Views using general tags" means a user's view may hide or show general tags too (Me hides
  Classics), not only other users' titles. Claude: yes to both; owner to decide.
- **Owner, 15 Nov (round 4): pinned for 0.158, still in discussion** (more talk and mock-ups before it is final).
  - **One global icon picker**, extended with new icons for users (two people for Together, rocket, flower …), drawn
    larger and clearer than in `02`.
  - **Users are managed only in the user switcher menu** (top bar, reachable from every page except playback):
    create, edit (name, icon), delete. Everywhere else (long-press, Details › More › Tags) users can only be picked.
  - **Default user per installation**, changeable: e.g. Together in the living room, the partner in the bedroom;
    Supernova opens on that user every time.
  - **Tags show on the Details hero card itself**, not in the tabs below.
  - Titles can have **many tags** (ticks are multi-select); More › Tags opens the Users and Tags list (yes).
  - "New tag" opens the one keyboard in its own panel beside the list (the approved keyboard-from-a-panel rule); the
    new tag is ticked straight away. Possibly an icon for each tag from the same picker (to decide).
- Still open, in plain words: when a user's view is on (say Me), may it also hide a kind of title, e.g. Classics, as
  well as other people's titles? Claude: yes.
- Next mock-up must show the whole flow: user switcher, Edit users and views (list, add, edit with name and icon
  picker, delete with warning, default user), Details hero with tags, More › Tags, New tag with the keyboard, the
  select mode for tagging many titles, library Filters with Users and Tags.
- **Two questions to settle (owner will come back to them):** (1) can a user's view also hide general tags (e.g. Me
  hides Classics by default), not just other users' titles? (2) per-user tags (seen only by their owner, e.g. the
  owner's Classics) as well as global tags (everyone's, e.g. Christmas), or every tag shared? Full-flow mock-up
  `tags-views/03` drawn 15 Nov to discuss.
- **Owner, 15 Nov (round 5), on v3; mock-ups `tags-views/04-v4-*` (changed screens only, one full-size picture each):**
  - **Default user:** "Open Supernova as" is renamed **Default user** (value on the right, second line "Supernova opens
    as this user"), under the This Device heading.
  - **Together** hides titles that belong to one person only (was "only titles tagged Together", which would hide every
    untagged title, i.e. most of the library). Untagged titles are everyone's.
  - **Delete a user** is one panel with real options (RULES §7b): where their titles go (Move to Together · Move to Me
    · Leave untagged, default Move to Together), each tag they made with its title count (OK switches Keep / Delete),
    a new default user (only on a device where they were the default), the line "No videos are deleted from your
    storage", then Keep <name> (focus first) · Delete <name>. Everything and the last user can't be deleted.
    Supernova therefore records who made each tag.
  - **Menus follow RULES §10–11:** a list opened from a named row (Default user, the titles choice) has no heading.
  - The Filters hint line "OK changes Off › Hide › Only" is part of the menu and stays.
  - Approved by the owner apart from the points below; no more mock-ups needed for these screens.
- **Still open (Claude's suggestions, owner to answer):** (1) every tag shared (Claude: yes; views do the hiding, no
  private tags); (2) record who watched each play now, so separate progress can come later; (3) Library Care shows
  "N new titles without a user tag"; (4) a quiet "N hidden by this view" line on library pages, and hidden titles in
  Search show their user icon; (5) another device whose default user was deleted asks "Who should Supernova open as?"
  next time; (6) where general tags are renamed or deleted (a Tags list in Edit users and views?); (7) users and tags
  included in Backup; (8) possibly ship tags and Filters first, the user switcher next, if 0.158 is too full.
- **Mock-up rule (owner, 15 Nov):** full size, up to about four screens per picture, never one sheet of 13.
- **Owner, 15 Nov (round 6), answers to the open list above:** (1) **every tag is shared**; views do the hiding.
  (2) **Record who watched each play from the start** (separate progress can come later). (3) **Users and tags can be
  edited:** rename and re-icon users, and rename, re-icon or delete general tags (a Tags list beside Users in Edit
  users and views; deleting a tag warns how many titles it comes off). (4) **Users and tags are part of Backup.**
  (6) **It stays one feature** in 0.158, not split. (5) Nudges: owner asked how they would look (described 15 Nov:
  a Library Care row "8 new titles without a user tag" with a Tag them button that opens select mode with those
  titles ticked; "12 hidden by Me" in the library page's facts; a hidden title in Search shows its user's icon and
  name in its second line). Waiting for the owner's answer. Mock-ups `tags-views/05-v5-*`.
- **Owner, 16 Nov (round 7), on Claude's further ideas:** (1) **Trakt per user: parked** (good idea; only the owner
  uses Trakt and doesn't sign in on Supernova yet). (2) **"Watch without Alex?"** liked; mock-up `06-v6-01` (a Together
  show's next episode that the other person hasn't seen: Wait for Alex (focus) · Watch now · We're both here, which
  switches to Together). (3) **Automatic tagging** wanted; owner asked how it works (described: folder rules a user
  sets are applied silently; collection matches, e.g. a sequel of a title tagged Alex, are suggested in Library Care
  for one-press Add or Skip; mock-ups `06-v6-05`, `06-v6-06`, folder rule in `06-v6-03`). (4) **Each user's playback
  and subtitle preferences: parked** (both use subtitles on everything today; needed if Supernova is ever used more
  widely: per-user settings without switching accounts). (5) "Ask every time" and (6) a user's watchlist: owner asked
  for details. (7) **Seasonal Home row** liked in theory; mock-ups `06-v6-02`–`04` (Tags list in Edit users and views;
  Edit a tag: Show on Home Off · Always · In season, From, Until). (8) Guest view: owner asked what it does.
