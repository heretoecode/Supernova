# Supernova roadmap

The one roadmap (owner, 16 Nov 2026; replaces the old picture). Keep it current whenever something moves:
To discuss → Ready → a version → built. Current decisions live in `docs/design/RULES.md`.

## Undecided

Not given a version yet. **Ready** = fully discussed, mocked up and approved; it only needs a version. **To discuss** = still being ironed out.

### Ready (designed and approved)

These wait until the polish phase is done (owner, 20 Nov): improving what exists comes first.
**Before any of them is built (owner, 20 Nov):** redraw its mock-ups with the rules as they are by then (button size,
background, menu outline and shadow, separators, captions, progress pie…), the owner checks them, and only then write
its page sheet (none exists yet: Onboarding's design is in RULES §23 and the 0.157 notes, the Trakt row's in
`docs/0.158/OWNER_NOTES.md` §4, Media Transfers' in `docs/0.158/MEDIA_TRANSFERS.md`). No redesign, just brought up to
date, so nothing is built on an old look.

- Onboarding (on Home, Start fresh / Restore, smart steps; designed 14 Nov): no version yet (owner, 20 Nov)
- A Trakt list as a Home row: only what streams on your services, logo on every card, OK opens the app (mock-ups trakt-list-row/; needs Customise Home and Your services order, 0.158)
- **Media Transfers, fully designed and approved (16 Nov):** copy, checks, resilience, keep free (percentage, 2% rule, Needs your OK), While you watch, full speed hours, Boost this / Boost all, Play warning during a boost, copies at once, one switch to remove the network copy, per-series "Bring new episodes" (`docs/0.158/MEDIA_TRANSFERS.md`, mock-ups `docs/design/proposals/transfers-settings/`)

### To discuss (owner, 16 Nov)

- [Given a version, 20 Nov: 0.159] NOVA clean-out.

- [Moved to 0.158 part B, 19 Nov] Episodes (answered 18 Nov): OK on an episode card plays it; long-press opens the same menu as More;
  deleting one offers the earlier episodes of the season: only this one · all · choose (with Select all)
- Network statistics: split by source and protocol (WebDAV, SMB, SFTP), real library sizes; with Files › Your Library
- Feature blueprint (research): pick ideas to take forward (`docs/research/FEATURE_BLUEPRINT.md`)
- [Partly in the next build, 20 Nov: start-up and page-switch times, leftovers that run, the health summary] Diagnostics that catch problems before you notice them: a health snapshot (memory, threads, caches, database,
  free space, temperature), smoothness (dropped frames, slow remote response), speed (start-up, page and artwork times),
  playback quality, stability (crashes, freezes, leaks), and a Health summary at the top of each report flagging anything
  over its limit; later the build checks the same limits
- Text centred on its letters, not its font box, in every box (buttons, chips, tabs, pills): "TV Shows" sat low in its
  focus box (owner, 16 Nov). Same photo: grey text in the white box and a blue Home underline; check on 0.157.1
- Playback Settings panel looks different from the mock-up even over a black, paused frame (owner, 18 Nov). The code
  has the same fill (charcoal at 95%) but adds a light edge and a dark glow, and still holds old blue colours; compare
  side by side with a photo (review P6)
- [Moved to 0.158 part B, 18 Nov] Titles in one house style instead of logo artwork (owner, 18 Nov: leaning to it; clean, no glow; "Modern block",
  styles Bold clean / Two-line / Modern bold). Mock-ups drawn 18 Nov: `title-style/01`–`03` (A Modern block · B Bold clean · C Two-line · D Modern bold on Home, Details, player); decided 18 Nov: official logo for now, house style as the fallback (style still to pick); logo placement to fix from the owner's photos
- **Background speckle (owner, 18 Nov):** charcoal feels bland; the old Space Black was too patchy. Idea from a
  black-sand beach: a darker base with fine, faint white grains, fixed to the screen (never moving or shimmering),
  for the whole app. Also hides banding in dark gradients. Mock-ups `background-speckle/01` faint (today's
  charcoal), `02` medium (darker), `03` strong (darkest, closest to the sand); owner to judge on the TV
  **On the TV (owner, 18 Nov):** medium liked on Files, Settings and Home ("distant stars"; even, no light and dark
  patches); text pops more than on charcoal; plain speckle on its own may be a little busy. Asks: a panel or outline
  behind the lists on Files and Settings? Tried in 4K (`tv2-1`–`4`); **decided (owner, 18 Nov): no pane**: text pops
  more straight on the speckle, washed out on the pane (RULES §1 stands). Lighter grain (a third fewer) in `tv2`.
- **Buttons, pills and focus boxes (owner, 18 Nov):** the focused Open button is bigger and less rounded than the
  Reachable pill beside it; focus boxes round words (Files, Connections, Open) feel too big with corners not rounded
  enough. [One height and fully rounded ends for every small box on a row (buttons, pills, chips, tab and top-bar
  focus), tighter side padding; fill tells an action from information. Large things (rows, cards, heroes) keep
  their gentler corners.] Owner: the matched shapes look more uniform; corner strength A 12 · B 20 · C fully
  round compared; **owner, 18 Nov: B (20 px)**. Density compared (`tv3-1`–`3`: 13, 20, 26 grains per 1,000 px): **owner, 18 Nov: 20, the current one (`tv3-2`)**;
  26 still too much. Base `#111215`; grains mostly 7–22% white, about 1 in 7 brighter (45%); fixed seed; no pane.
  For coding: one shared drawable through `PreviewUtilityBackground` (today it paints charcoal), drawn once at the
  screen's own size, no filtering; pages that paint their own charcoal (Files, folder browser, top bar's solid
  strip, the library pages' fade under the top bar) switch to it; panels, menus and caption boxes stay solid.
- Users and tags instead of profiles: user switcher in the top bar (no SUPERNOVA name), user and general tags, views, default user, delete options, editable tags, in Backup (mock-ups v4–v6). Last questions answered 18 Nov: opens as the default user; progress and watchlist per user; a Guest user (mock-up next)

## 0.157

### Redesigns

- Foundation: panels, menus, choice list, focus, headings
- One keyboard: paste from the phone app, password eye
- New mark: icon, banner, splash, loading ring
- Files: Your Library, Library Care, Fix match page, Scan one source, connections, top-bar ring
- Details: four kinds, new tabs, metadata row, full description, artwork picker, More menu, portrait cast and crew
- Player: Preparing playback, title block, seek preview, centre ring, skip button, Playback tab, track names, subtitle lists, Experimental translate

### Engine and NOVA

- NOVA 6.6.5 and later, all of it: match by file name (the wrong matches), Samba fixes, software-decoding notice, picture-subtitle height, Trakt
- Then re-check matches on Library Care
- Merge Codex’s 13 finished items
- Seek loading signal (Codex built it; merge and test)

### Old NOVA out

- Hidden grid and hidden screens — **not done** (checked 20 Nov): Codex's report found nothing provably safe to
  remove in one go (49 files still check NOVA's `try_new_ui` switch; classic grids, anime grids, collection routes
  UNSURE). Only Issues Digest, picture in picture and the old file-access screens went. `NoOldScreensTest` stops
  redesigned pages opening old screens. To schedule: a clean-out done batch by batch (see To discuss).
- Picture in picture, Issue digest
- All files access once (no Android screens)

### Speed and reliability

- Memory (about 750 MB today)
- No page rebuild every minute during scans
- Scan rules (one at a time, 15 min, launch limit)
- Reopen where you were, focus restored, standby
- Focus jumping when Down is held
- Check network drops and Put.io pause sync

### Bugs

- Seek previews: hidden switch and missing pictures
- Episodes row missing in the player
- Chapter names
- Skip button faults
- Stuck “Scanning…” pill
- Started titles stay in Recently Added
- Files deleted elsewhere not tidied
- Details tabs pop in; hero waits for artwork
- JustWatch link

### Data and behaviour

- Subtitles ready at Preparing; next two episodes; full subtitles in your language
- UK age ratings; reception offline; technical info
- Calmer hero backdrops
- Region from the device
- Automatic sort titles
- Diagnostics: 2 weeks, clear after export, memory and delete logging
- Six backup groups
- Licences tidied
- 0.157’s release notes written properly (grouped, one change a line)

## 0.157.2

Bug fixes only (owner, 17 Nov): as many as possible before 0.158. Findings in `docs/0.157/REVIEW_0157_1.md`.

- Every bug from the 0.157.1 review: Files F1–F52, Details D1–D20, Player P1–P5 (`docs/0.157/BRIEF_0157_2.md`)
- Details tab on one screen (B2), TMDB page in the More menu, trailer pop-up (approved 17 Nov)
- Background: Home and the three library pages get the same plain charcoal as Files, Settings and Search (the one
  exception to "no new designs"; their redesigns stay in 0.158)

## 0.158

All the planned redesigns except Settings, then a bug-fixing phase on a settled design (owner, 18 Nov). Each page
gets its page sheet before coding (CLAUDE.md, Definition of done).

### Redesigns

- Library pages (posters, Filters, Sort, Columns, list view, Customise page, empty pages, + page, episode counts;
  the stats row goes)
- Home (hero, caption-box rows, Customise Home; the Home-to-Details glide)
- Search (library first, On your services, recent searches)
- Next episode on the hero and Details' Status (owner, 25 Nov; with Details, before its coding; label above the logo)
- Connections grouped by server: one connection per server and login, its folders inside; one scan button per server
- **One side margin (owner, 18 Nov):** every page except Home lines up with the top bar's own edges: the clock's right
  edge on the right, the first word's box on the left. Library pages, Files, Search and Settings use it for their
  content, the toolbar's dividing line and the artwork. Today: top bar 26 units (about 52 px at 1080p), library pages
  28 (a few pixels in), Files 48 (about 40 px further in). Home keeps no margin so its rows run off the edge
- **Top bar (approved by the owner, 18 Nov; a trial before tabs elsewhere follow):** SUPERNOVA removed from the top left (separately
  from users and tags), places shift left. **Media pages open as focus lands on them** (Home, Movies, TV Shows, your
  own pages); **tools need OK** (Files, Search, Settings). Three looks, as on the Shield's home screen: plain word;
  white box with black text = focus; **soft grey box with a faded word = the open page while focus is below** (or
  while focus is elsewhere in the bar). No underlines in the top bar. Tabs on Details and Files stay as they are
  until the bar has proven itself. Mock-up `files-review-0157-1/08`. The freed space leaves room for more library
  pages. Coding settles loading (outline at once, artwork once you stop) and the exact shade.
  Also (owner, 18 Nov): **one font for the whole bar** — the clock is drawn in the light weight ("sans-serif-light"
  in `TopNavigation`) while the places are regular, which is why it looks skinnier; same font, weight and size for
  every word and the clock. **Text centred on its letters:** labels are centred on the font's box (room kept for
  accents above and descenders below), so words like "Documentaries" sit low with more space above; centre them on
  the letters themselves. **Tools as words (approved 18 Nov, option A of `files-review-0157-1/09`):** Files · Search ·
  Settings, no icons, the same as the media pages.

### Also in 0.158
- Player review (the full one, skipped on 17 Nov) and its fixes; leftovers from 0.157.2's "not built" list

### Next build (owner, 20 Nov): part B's remaining items, tonight's decisions and the diagnostics upgrade
- Everything in `docs/0.158/BRIEF_0158_B.md` not yet built, the owner's notes on 0.158.3–0.158.7 and the library
  review decisions; plus the **diagnostics upgrade**: start-up time, page-switch times inside the top bar, a record of
  every NOVA leftover that runs, and a health summary at the top of each report.

### 0.158 part B (owner, 18 Nov)
- **First, the new look, so everything after it is checked on it:** the speckled background app-wide (density 20,
  `background-speckle/tv3-2`, no pane) and one shape for every small box (buttons, pills, chips, tab and top-bar
  focus: one height, 20 px corners, tighter padding; `tv3-*`). Recipe under To discuss › Background speckle.
- Titles (approved 18 Nov): the logo placement fix and the house style A (with C's colon split) for titles
  without a logo (RULES §19).
- **A look sweep once the background is in:** render every page on it and fix what it changes (faint row lines,
  grey second lines, the open-page grey box, outlined pills and chips at rest, "Unknown", panels now lighter than
  the page, the keyboard's keys, loading placeholders, disabled rows). Only what fails gets changed. The Playback
  Settings panel colour (P6/PS10) is judged in this sweep (owner, 18 Nov).
- **Text centred on its letters in every box** (the 0.158 top bar fix, applied to every button, pill, chip and tab
  with the new shape; owner's "TV Shows sat low", 16 Nov). The shape alone doesn't fix it.
- **Panel shade on the speckle (owner to pick, 19 Nov):** panels nearly match the darker page. Mock-ups
  `background-speckle/tv4-A` (today, `#16181C` 90%), `tv4-B` (`#1D2025`, solid), `tv4-C` (`#24272D`, solid); owner: B and C look alike on the TV, keep today's colour and try more opacity: `tv4-D` 95%, `tv4-E` 98% (owner: best so far), `tv4-F` 99%, `tv4-G` 100%. **Decided: today's colour at 99%** (RULES §11).
- **Back and page position** (RULES §16, 18 Nov): Back goes straight to the top bar; a page left for another one
  reopens at its top; Details → Back returns to the poster.
- Then the owner's 0.157.2 review (`docs/0.157/REVIEW_0157_2.md`), Playback Settings first.
- **Episodes** (OK plays; long-press = the More menu; deleting offers earlier episodes) and **the Settings redesign**
  (moved from 0.159 by the owner, 19 Nov: the last major redesign). Brief: `docs/0.158/BRIEF_0158_B.md`.

### Paused from part B (owner, 19 Nov), for the next version once the discussion is done
- Backup and Restore in the rows, with the Backups folder picker (reports stay in Download › Supernova)
- "Also translate other languages" (engine feature, judged on the Shield); Trakt sign-in as a side panel
- The owner's notes on 0.158.3/0.158.4 (`docs/0.158/REVIEW_0158.md`): the delete bug first, About, Home hero one
  button, Settings tab size and scroll arrows; the NOVA app-side catch-up; release notes for every APK

## 0.159 — NOVA clean-out (owner, 20 Nov)

- Remove NOVA's leftover screens and code batch by batch: pin the `try_new_ui` switch to Supernova's path, list every
  NOVA screen and what reaches it, rewrite the few live pieces Supernova still borrows, delete in batches, each with a
  full build, tests and a Shield check. The playback engine and network-share code stay (engine catch-up is separate).
- Lock it: the no-old-screens test covers the whole app; a test fails if a NOVA overlay or layout appears on a
  Supernova page.
- Measured: diagnostics baseline before (from the next build's upgraded reports), compared after each batch (memory,
  start-up, page switches, stutters, freezes, focus anomalies, which leftovers ran).
- Walkthroughs on the Shield of the pages not yet reviewed wait until after this; design reviews carry on meanwhile.
- [Moved to 0.158 part B, 19 Nov] Settings. [Onboarding moved to Undecided › Ready, 20 Nov]

## Parked

Ideas kept for later (details in `docs/REVISIT_LATER.md`). "Park that" adds here.

### To discuss

- Release notes: proper notes for every version (0.156 has two vague lines)
- Request titles not in the library through Seerr, from Search (owner, 25 Nov; joins Discovery in Search)
- Finer settings, compared with Plozz (owner, 25 Nov; candidates listed, chosen at the Settings review)

### Planned

- Newer NOVA engine stack, as its own version (checked 19 Nov: our engine is NOVA's of 4 Nov, 29 changes behind:
  Dolby Vision profile 7 → 8.1 and profile 5, picture-subtitle position and size, HDR metadata)
- Make the repository private; private Releases page
- “Going public” checklist (history, titles, privacy, diagnostics cut back)
- Supernova’s own TMDB key

### Parked by you

- Supernova screensaver (designed)
- Picture in picture could return
- Documentation website
- Built-in OMDb key (ratings without your own key)
- Discovery / Coming Soon in Search

### Old ideas (concept only)

- Launcher channels on the home screen
- Profiles
- Person pages
- More than one custom library page
- Anime metadata
- Subtitle presets
- Trakt reviews and social
- AI features
- Themes
- Content warnings at playback start
- Automatic Google Drive delivery
