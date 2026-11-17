# Supernova roadmap

The one roadmap (owner, 16 Nov 2026; replaces the old picture). Keep it current whenever something moves:
To discuss → Ready → a version → built. Current decisions live in `docs/design/RULES.md`.

## Undecided

Not given a version yet. **Ready** = fully discussed, mocked up and approved; it only needs a version. **To discuss** = still being ironed out.

### Ready (designed and approved)

- A Trakt list as a Home row: only what streams on your services, logo on every card, OK opens the app (mock-ups trakt-list-row/; needs Customise Home and Your services order, 0.158)
- **Media Transfers, fully designed and approved (16 Nov):** copy, checks, resilience, keep free (percentage, 2% rule, Needs your OK), While you watch, full speed hours, Boost this / Boost all, Play warning during a boost, copies at once, one switch to remove the network copy, per-series "Bring new episodes" (`docs/0.158/MEDIA_TRANSFERS.md`, mock-ups `docs/design/proposals/transfers-settings/`)

### To discuss (owner, 16 Nov)

- Episodes: OK plays; long-press menu (Play, Show details, Delete episode); deleting one offers earlier episodes of the season, each your choice
- Network statistics: split by source and protocol (WebDAV, SMB, SFTP), real library sizes; with Files › Your Library
- Feature blueprint (research): pick ideas to take forward (`docs/research/FEATURE_BLUEPRINT.md`)
- Diagnostics that catch problems before you notice them: a health snapshot (memory, threads, caches, database,
  free space, temperature), smoothness (dropped frames, slow remote response), speed (start-up, page and artwork times),
  playback quality, stability (crashes, freezes, leaks), and a Health summary at the top of each report flagging anything
  over its limit; later the build checks the same limits
- Text centred on its letters, not its font box, in every box (buttons, chips, tabs, pills): "TV Shows" sat low in its
  focus box (owner, 16 Nov). Same photo: grey text in the white box and a blue Home underline; check on 0.157.1
- Users and tags instead of profiles: user switcher in the top bar (no SUPERNOVA name), user and general tags, views, default user, delete options, editable tags, in Backup (mock-ups v4–v6; a few questions open)

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

- Hidden grid and hidden screens
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

## 0.159

- Settings (seven tabs, Backup and Restore, Diagnostics, About): set aside by the owner (18 Nov); the current
  Settings works and is rarely visited
- Onboarding (on Home, Start fresh / Restore, smart steps): it points into Settings and is seen once

## Parked

Ideas kept for later (details in `docs/REVISIT_LATER.md`). "Park that" adds here.

### To discuss

- Release notes: proper notes for every version (0.156 has two vague lines)

### Planned

- Newer NOVA engine stack, as its own version
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
