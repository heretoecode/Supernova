# Supernova roadmap

The one roadmap (owner, 16 Nov 2026; replaces the old picture). Keep it current whenever something moves:
To discuss → Ready → a version → built. Current decisions live in `docs/design/RULES.md`.

## Undecided

Not given a version yet. **Ready** = fully discussed, mocked up and approved; it only needs a version. **To discuss** = still being ironed out.

### Ready (designed and approved)

- A Trakt list as a Home row: only what streams on your services, logo on every card, OK opens the app (mock-ups trakt-list-row/; needs Customise Home and Your services order, 0.158)
- Media Transfers, all of it: copy, checks, resilience, delete after copy, per-series "Bring new episodes", schedule (`docs/0.158/MEDIA_TRANSFERS.md`; copy settings drawn and waiting for approval: `docs/design/proposals/transfers-settings/`)

### To discuss (owner, 16 Nov)

- Episodes: OK plays; long-press menu (Play, Show details, Delete episode); deleting one offers earlier episodes of the season, each your choice
- Network statistics: split by source and protocol (WebDAV, SMB, SFTP), real library sizes; with Files › Your Library
- Feature blueprint (research): pick ideas to take forward (`docs/research/FEATURE_BLUEPRINT.md`)
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

## 0.158

### Redesigns

- Home (hero, caption-box rows, Customise Home; the Home-to-Details glide)
- Library pages (posters, Filters, Sort, Columns, list view, Customise page, empty pages, + page, episode counts)
- Settings (seven tabs, Backup and Restore, Diagnostics, About)
- Onboarding (on Home, Start fresh / Restore, smart steps)
- Search (library first, On your services, recent searches)

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
