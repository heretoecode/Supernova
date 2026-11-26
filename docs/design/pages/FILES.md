# Page sheet: Files

The one place to build Files from (CLAUDE.md "Definition of done"). **Final, 26 Nov 2026:** the owner's Files review
(25–26 Nov, four rounds) is finished and everything below is approved. Mock-ups: `docs/design/proposals/files-v2/tv/`
`01`–`24` (Drive › Supernova › Media › From Claude › "2026-11-25 Files round 1–4"; where a frame was redrawn, the
newest file there). This sheet wins over every older Files document (`NETWORK_FILES.md`, `network-files-tabs/`,
`files-review-0157-1/`, `REVIEW_0157_1.md`); RULES.md wins over this sheet only where it is newer. The 25–26 Nov
review notes are folded in below; the round-by-round record is in this file's git history (commit "FILES.md: Files
review round 3 agreed" and before).

Test data (Definition of done 4): the made-up sources in the mock-ups: Internal storage, Backup #1, Backup #2 (USB),
Put.io (WebDAV), Seedbox and Home server (SFTP), Living-room NAS with a very long name (SMB, three folders), MEDIA-PC
and backup-nas.local found on the network; empty folders; long file and title names; items in every Library Care list.

## Whole page
- Plain charcoal `#16181C`, side margins lined up with the top bar. Files fills the page itself (F3). Nothing clipped:
  a grown card or ring may reach into the margin.
- **Top bar:** Files on the right. The signal slot sits **outside** the Files box, left of it, with a clear gap,
  centred on the word: the scan ring while a scan runs, else a white dot while Library Care has anything waiting, else
  nothing. The slot never shifts anything (RULES §2). (`tv/01`, `tv/21`)
- Opens on **This Device** every time (F2). Re-choosing the open tab returns it to its start (F12).
- **Tab row:** This Device · Connections · Library Care (count badge) · Your Library. Focused tab = the white box; the
  open tab keeps a white underline (RULES focus and path). Moving along the tabs switches the page at once. Up into the
  tab row lands on the open tab (F17); Up again reaches the top bar (F1). Focus never jumps to the top bar by itself.
- **Scan library** (right end of the tab row): OK scans every source; OK while running stops it. **It narrates every
  step, never a bare "Scanning"** (`tv/23`):
  Connecting to Seedbox… → Looking in Seedbox › TV · 42% → Found 3 new videos → Matching 2 of 3 · Blue Planet III →
  Fetching artwork · 5 of 12 → Checking for missing files · Seedbox → Done · 3 new · 1 missing · 1 not matched (a few
  seconds), then "Scan library" again. "· 2 more waiting" when sources queue. A failure says so in place ("Couldn't
  connect to Living-room NAS") and the scan moves on to the next source. Steps shorter than a second are skipped.
  **Nothing jiggles:** the ⟳ icon is pinned at the right margin; the words grow leftwards into the empty space, about
  45 characters at most, long names end "…"; numbers use fixed-width digits.
- **No scroll bars:** a down arrow in the right margin at the foot when there is more; an up arrow at the top once
  there is something above, faint when not; none when it all fits. Menus that scroll carry the arrows in their own
  right-hand margin (RULES §11).
- **Rows:** icon, name, second line, pill in one fixed column, buttons on the focused row only, › only when OK opens a
  page; everything on the row's centre line; black ink on the white focus, icons included. Long names stop a clear
  40 px before the pill column and end "…". Lines only between selectable rows, never under the last.
- **Pills never wobble:** one fixed width; while working, the ring is pinned at the pill's left inset with the words
  after it and fixed-width digits ("Adding · 40%", "Scanning · 100%" fit).
- **Menus** reserve the tick column on every row; the current choice is ticked; each item has its own icon.

## This Device (`tv/01`)
- Rows: **Internal storage** and each USB drive: name, "USB drive · 1,203 videos · 1.1 TB free of 2 TB", and the
  storage bar on every row. USB-stick icon for drives. OK opens the drive.

## Browsing a drive, folder or connection (`tv/02`–`08`)
- Breadcrumb under the tabs ("Backup #2 › Movies"): signpost only, not focusable; Back goes up one level.
- Toolbar on the right: **Grid / List** (shows the other view) · **Sort: Name ▾** · **Order** · **Videos only /
  Show all files** (each with its icon). List is the default.
- **Sort** drops down under its button (`tv/03`): Name · Date modified · Size, tick on the current one. Order's words
  follow it: A → Z / Z → A, Newest / Oldest first, Largest / Smallest first. Folders always first.
- **List rows:** folder: "Folder · 31 videos · 48.6 GB" + pill In library / Excluded / Not added. The size comes from
  the library at once for folders in it; for folders not yet added it fills in quietly once counted. File: title,
  second line "2024 · 14.2 GB" + pill.
- **Grid cards** (16:9, `tv/04`): folder: mosaic of the artwork inside, or the plain card with a folder icon; library
  file: its artwork; other file: a still from the video, or the plain card with a film icon; pill top right.
- **A folder with no videos** says so (`tv/08`): "No videos in this folder" with **Show all files**.
- After playing, Back returns to the same folder with focus on the file.

| Action | Leads to |
| --- | --- |
| OK on a folder | Opens it below the same tab (breadcrumb grows); focus on its first item |
| OK on a file | Plays it |
| Menu (or long-press) on a file (`tv/05`) | Menu beside it: Play · Details · Mark as watched · Exclude · Fix match |
| Menu on a folder (`tv/04`) | Open · Add to library · Scan this folder · Fix match · Exclude. Add / Scan only when they apply; **Fix match only on a folder holding one title** (a film's or a show's folder): it re-matches everything inside in one go |
| Details | The title's Details page; Back returns here |
| Fix match | The Fix match page (below) for that title or folder; Back returns here |
| Exclude (`tv/06`) | At once; the pill becomes Excluded; notice with Understood · Undo |
| Add to library / Scan this folder (`tv/07`) | Progress in the row's pill ("Adding · 40%"), then In library |
| Grid / List | Switches view; focus stays on the same item |
| Sort | Sort menu under the button; Back closes it |
| Order / Videos only | Switches at once, in place |
| Back | Up one level; from the top of a drive, This Device's list |

## Connections (`tv/09`–`15`)
- **Your connections:** one row per connection, one per server with its folders inside: "Put.io · alex.morgan",
  second line "WebDAV · 1,204 videos" or "SMB · 3 folders · 2,140 videos", pill Reachable / Not reachable.
  Focused: **Open · Edit · Test · Remove**.
- **Add a connection** (last row of the section): no buttons; OK opens the form.
- **Found on your network:** devices not yet added, pill New; focused: **Open · Add**. Once added, a device moves up
  into Your connections.
- **Advanced › Network options** (last section): moved here from Settings (`tv/15`).

| Action | Leads to |
| --- | --- |
| Open (or OK) on a connection | Browses it like a drive (breadcrumb from its name) |
| Edit | The connection's form as a page in the tab, filled in (`tv/13`) |
| Test | The answer in the row's second line: "Connected · 3 shared folders found" or why not ("Can't reach it · check it's switched on and on this network") (`tv/10`) |
| Remove | Row goes at once; notice "Put.io · alex.morgan removed · Its videos leave your library. Nothing is deleted on Put.io." with Understood · Undo (`tv/11`) |
| OK on Add a connection | New connection form (`tv/12`) |
| Open on a found device | Browses it; only if it needs a sign-in, the sign-in page first (`tv/14`) |
| Add on a found device | New connection form with address and type filled in |
| Network options | Its page in the tab (`tv/15`); Back returns |

- **Connection form** (`tv/12`, `12b`, `12c`, `13`), a page in the tab, "Connections › New connection" / "Edit ·
  <name>"; form left, the one keyboard right (RULES §15) with a help line for the chosen type. Every row has an icon.
  - **Type** first: OK opens a short menu under the row with the four kinds, each with its line, the current one
    ticked (`tv/12c`): SMB "Shared folders on a Windows PC, Mac or NAS" · WebDAV "Cloud storage and web servers" ·
    SFTP "A computer or server you sign in to over SSH" · FTP "Older file servers; FTPS when encrypted". Back closes it.
  - **Encryption** straight under Type, always: a switch for WebDAV (HTTPS) and FTP (FTPS), on by default; **greyed**
    for the others, saying why ("Always on with SFTP", "Set by the server" for SMB); focus skips it while greyed.
    Rows never jump when the type changes.
  - Then Name (suggested from the address, change any time) · Address · Port (only where it isn't standard; "443 ·
    the usual port with encryption") · Folder / Folders · Username · Password (eye) · Remember password.
  - Height follows the type; buttons **Test connection · Save and browse** (Edit: **Test connection · Save**) sit
    straight under the last field. Test answers under the buttons. Back leaves without saving.
  - **Renaming must save** and show at once everywhere the connection is named (bug M6).
- **Sign-in for a found device** (`tv/14`): "MEDIA-PC asks you to sign in · Windows computer · SMB"; Username,
  Password, Remember password; **Sign in and browse · Add as a connection**. Only when needed.
- **Network options** (`tv/15`): Windows Shares (SMB): Newer SMB method · Allow SMBv1 · Name lookup order (chips
  Network first / Local first). Finding Devices: Look over TCP · UDP · mDNS (Bonjour). SFTP: Newer SFTP method. Each
  switch with its line of explanation.

## Library Care (`tv/16`–`24`)
- **Library health:** Missing files · Couldn't be read · Not matched · To fix (count pills) · Check matches ("Last:
  17 Nov"). A "None" row has no › and OK does nothing. The tab's badge equals the total.
- **Kept by you:** Excluded from hero ("46 titles").
- **Scanning:** **Scan one source ›**, second line "One drive, server or folder only", or while scanning the same
  narration as the tab bar ("Seedbox: Scanning · 42% · 1 waiting").
- **Recent scans:** the three latest, **each focusable with ›** (`tv/23`): source, "Today 18:28 · 3 new · 1 missing ·
  1 folder held back", pill Finished / Failed (second line soft red, with the reason) / Stopped; then **Show all
  scans** ("The last ten").
- **Every problem list has a "this is fine" answer** beside its fix; an item answered either way leaves the list and
  its count:

| List | Second line of each item | Buttons on the focused item |
| --- | --- | --- |
| Missing files (`tv/20`) | where it was and since when ("Living-room NAS › Movies · since 3 Nov") | Find it again · Remove |
| Couldn't be read | where it is and what went wrong | Try again · It plays fine |
| Not matched (`tv/20b`) | the best guess: "Looks like Blue Planet III · S1 E2 · from the file name", or "No guess yet · <where>" | Fix match · It's right (accepts the best guess) |
| To fix (`tv/17`) | "TV show · 1 episode · opened in Fix match on 20 Nov from TV Shows" | Fix match · It's right |
| Excluded from hero (`tv/19`) | "Movie · 2019 · excluded 14 Oct 2026"; search row first; Include all again top right | Include again |

- Missing files ends with a quiet note: "Find it again looks for the same file and links it back, keeping where you
  stopped. Remove takes it out of your library only."
- **Not matched lists only real failures:** titles still being looked up are not listed or counted; while any are
  pending, a line under the list says "Matching N new titles: they're not listed while Supernova is still looking them
  up." (bug M5).
- **To fix:** every title opened in Fix match anywhere in the app and left without a new match stays here until it is
  matched or marked It's right. The Files dot shows while anything is waiting.

| Action | Leads to |
| --- | --- |
| OK on Missing files / Couldn't be read / Not matched / To fix / Excluded from hero | Its list as a page in the tab ("Library Care › Not matched  3 videos") |
| Fix match (any list, any page in the app) | **Fix match page** (`tv/18`) |
| OK on Check matches | Runs in the row; "No changes · 17 Nov", or "4 to review" and OK opens the review page |
| OK on Scan one source | **Menu** (`tv/21`): This device, each USB drive, then for each connection a group heading ("Seedbox · SFTP") with "All of Seedbox" and each of its folders; each line shows where it stands on the right: last scan time · Waiting · Scanning · 42% · Couldn't connect · 17:28. OK queues it; several can wait their turn. Scrolls, arrows in its right margin |
| OK on a recent scan, or on a scan in Show all scans | **Scan details** (`tv/24`) |
| Show all scans | The last ten scans, same rows |

- **Fix match page** (`tv/18`), "Fix match  from <where> · <page>": left column **This file**: the file name, its
  location and size, and "Matched now as Dine Out (2023) · TV show · 1 episode"; under it the search box and the one
  keyboard (narrow column). Right: **Matches**, the first marked "Best match"; the focused match carries **Match and
  return · Match and stay** on its own line, at normal row height. Back returns to where Fix match was opened.
- **Scan details** (`tv/24`), a whole page, "Library Care › Recent scans › Seedbox  Today 18:28":
  - **Summary** (read-only): When (what started it; "Today 18:24 – 18:28 · 4 min 8 s") · Folders looked in ("2 of 3 ·
    1 held back") · Files listed · Result ("3 new · 1 updated · 1 missing · 1 not matched").
  - **What changed** (focusable, ›): each title new, updated, missing or not matched, with its pill; OK opens where it
    went (Details, Missing files, Not matched).
  - **Step by step** (read-only; Down / Up scroll): the transcript, times to the second: started (and by what),
    connecting and connected (time taken), each folder looked in and listed (files, new, changed, seconds), every
    failure with its reason and retries in soft red, folders held back, the missing check and each file found
    missing, each match and how it was made, updates, artwork, finished with the totals.
  - Kept for the last ten scans; the same record feeds the diagnostics report (BRIEF_0158_C item 9).

## Your Library (`tv/22`)
Read-only, nothing focusable; Down / Up scroll; arrows as above. Faint row lines (a long read-only list of facts).
- **Total Library:** All library pages ("520 titles · 1,808 videos", size) · Watched ("318 of 520 titles", %) · Time
  to watch everything (hours) · Picture (4K · 1080p · 720p · SD · HDR counts) · Added this month.
- **By Library Page:** one row per library page with titles (episodes for TV) and size.
- **By Source:** note "The videos in your library on each source, not the space left on it."; one row per drive or
  connection (type, folders) with size and video count; last row **By connection type** (USB · SMB · WebDAV · SFTP ·
  this device).
- **Most Space Per Episode:** the top three shows, "6 episodes · 14.4 GB", "2.4 GB per episode".

## Never opens from Files
NOVA's Network Shares / Saved Locations / "Browse a network share" pages, the "Add Network Source" pop-up, Android's
keyboard, the three-panel browser (Locations · list · Information), the old Library Health screens, the old scan
history.
