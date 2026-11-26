# Page sheet: Files

The one place to build Files from (CLAUDE.md "Definition of done"). Latest decisions as of 17 Nov 2026. Sources:
`docs/design/RULES.md`, mock-ups in `docs/design/proposals/network-files-tabs/` and `files-review-0157-1/`, review
`docs/0.157/REVIEW_0157_1.md` (F-numbers). Older mock-ups say "This Shield", "Network & Files", a boxed tab tray and
facts on the right of the tabs: read them as This Device, Files, underline tabs, and the scan button alone.

## Whole page
- Plain charcoal `#16181C`, side margins as today (lined up with the top bar). Files fills the page itself; it is
  not a cell inside the old page list (F3). Nothing clipped: a grown card or ring may reach into the margin.
- Top bar: Files on the right; the signal slot on its left (scan ring, else white dot, else nothing).
- Opens on **This Device** every time (F2). Re-choosing the open tab returns it to its start (F12).
- Tab row: This Device · Connections · Library Care (badge) · Your Library; on the right only **"⟳ Scan library"**
  (OK scans every source; "Scanning · 42%" while running, OK stops). No facts (F18).
- Moving along the tabs switches the page at once. Up into the tab row lands on the open tab (F17). Up from the tab
  row reaches the top bar (F1). Focus never jumps to the top bar by itself (F9).
- No scroll bars: down arrow in the right margin at the foot when there is more; up arrow at the top once there is
  something above, faint when not; none when it all fits (F15, F36).
- Rows: icon, name, second line, pill in a fixed column, buttons on the focused row only, › only when OK opens a
  page; everything on the row's centre line; black ink on the white focus, icons included (F13, F29, F30, F43).

## This Device
- Rows: **Internal storage** (F19) and each USB drive: name, "USB drive · 1,203 videos · 1.1 TB free of 2 TB", and
  the storage bar on every row (F14). USB-stick icon for drives. OK opens the drive.

## Browsing a drive, folder or connection
- Breadcrumb under the tab ("Backup #2 › Movies"): signpost only, not focusable; Back goes up one level.
- Toolbar on the right: **Grid / List** (shows the other view) · **Sort: Name ▾** · **Order** · **Videos only / All
  files**. List is the default (F5).
- Sort menu: Name · Date modified · Size, tick on the current one; Order's words follow it: A → Z / Z → A, Newest /
  Oldest first, Largest / Smallest first. Folders always first (F6, F25).
- List rows: folder: "Folder · 86 videos" + pill In library / Excluded / Not added (F23); file: title line, second line
  "2024 · 14.2 GB" (size only when the year is unknown) + pill (F20).
- Grid cards (16:9): folder: mosaic of the artwork inside, or plain card with a folder icon (F8); library file: its
  artwork; other file: a still from the video, or the plain card with a film icon (F24, `files-review-0157-1/03`);
  pill top right; caption as in list.
- After playing, Back returns to the same folder with focus on the file (F11).

| Action | Leads to |
| --- | --- |
| OK on a folder | Opens it below the same tab (breadcrumb grows); focus on its first item |
| OK on a file | Plays it |
| Long-press (or Menu) on a file | Menu beside it: Play · Details · Mark as watched · Exclude · Fix match |
| Long-press (or Menu) on a folder | Menu beside it: Open · Add to library · Scan this folder · Exclude (`files-review-0157-1/01`) |
| Details | The title's Details page; Back returns here |
| Fix match | Library Care's Fix match page for that title; Back returns here |
| Grid / List | Switches view, focus stays on the same item |
| Sort | Sort menu beside the button |
| Order / Videos only | Switches at once, in place |
| Back | Up one level; from the top of a drive, This Device's list |

## Connections
- **Your connections:** one row per connection: "Put.io · alex", second line "WebDAV · 1,204 videos" (F38), pill
  Reachable / Not reachable. Focused: **Open · Edit · Test · Remove** (F30).
- **Add a connection** (last row): no buttons; OK opens the form (F39).
- **Found on your network:** devices not yet added; focused: **Open · Add** (F40). Once added, a device moves up into
  Your connections and leaves this list.
- 0.158: one connection per server, its folders inside; one row and one scan button per server (F52).

| Action | Leads to |
| --- | --- |
| Open (or OK) on a connection | Browses it like a drive (breadcrumb from its name) (F33) |
| Edit | The connection's form as a page in the tab, filled in (F32) |
| Test | Result in the row ("Connected" / why not) |
| Remove | Row goes at once; notice with Undo (`24`) (F31) |
| OK on Add a connection | New connection form (F34) |
| Open (or OK) on a found device | Browses it; asks for a username and password only if needed (F40) |
| Add on a found device | New connection form with address and type filled in |

- **Connection form** (`files-review-0157-1/04` 2): Type chips **SMB · WebDAV · SFTP · FTP** (help line "SMB: shared
  folders on a Windows PC, Mac or NAS"); fields follow the type: Name (suggested from the address), Address,
  Encryption switch on by default ("Encryption (HTTPS)" for WebDAV, "Encryption (FTPS)" for FTP), Port, Folder,
  Username, Password (eye), Remember password; buttons Test connection · Save and browse. The one keyboard on the page
  (RULES §15). Back leaves without saving.

## Library Care
- **Library health:** Missing files · Couldn't be read · Not matched (count pills) · Check matches ("Last: 17 Nov").
  A "None" row has no › and OK does nothing (F46). The badge equals the total.
- **Scanning:** Scan one source (focused: one button per source; nothing shown at rest) (F48); pressed sources show
  "Waiting" until their turn (F51). **Recent scans:** last three as plain rows, then Show all scans (F50).

| Action | Leads to |
| --- | --- |
| OK on Missing files | Its page in the tab: each file with Find it again · Remove (`12`) |
| OK on Couldn't be read | Its page: Try again · Exclude (`13`) |
| OK on Not matched | Its page: Fix match · Not a movie (`14`) |
| Fix match | Fix match page: search and keyboard left, matches right, "Best match" (`14b`) |
| OK on Check matches | Runs in the row; "No changes · 17 Nov", or "4 to review" and then OK opens the review page (F44) |
| A source button | Scans that source; "Scanning · n%" in the row |
| Show all scans | The last ten scans |
| Fix match from a title (Details › More) | This page for that title: Match and return · Match and stay; Back returns to the title |

## Your Library
- Read-only, nothing focusable: Total Library, By Library Page, Most space per episode (`44`). Picture row shows
  every quality: 4K, 1080p, 720p, SD and HDR (F21). Down / Up scroll; arrows as above.

## Never opens from Files
NOVA's Network Shares / Saved Locations / "Browse a network share" pages, the "Add Network Source" pop-up, Android's
keyboard, the three-panel browser (Locations · list · Information), the old Library Health screens.

## Review 25–26 Nov (owner; mock-ups `files-v2/tv/01`–`22`; rounds 1–4)
**Round 1 notes (owner, 26 Nov):**
- **Tabs (confirmed):** focused tab = the white box; inside the tab the open tab keeps a white underline, no box (RULES
  one tab behaviour).
- **Scan library narrates its work (owner):** "Scanning Backup #1 · 42%" → "Scanning Put.io · 3 of 6 sources" →
  "Fetching details · 12 of 40" → "Done · 23 new" (a few seconds), then "Scan library"; OK while running stops it.
  **Nothing jiggles (owner):** the ⟳ icon sits at the right end, pinned to the margin; the words grow leftwards into
  the empty space; numbers use fixed-width digits; long source names end in "…" so the text has a ceiling.
- **Menus reserve the tick column** on every row (Sort: "Date modified" must fit its tick); drawing fix.
- **Folder size on folder rows (owner):** "Folder · 31 videos · 48.6 GB"; from the library at once for folders in it;
  for folders not yet added (network) it fills in quietly when counted.
- Proposed, waiting: (a) the signal dot beside Files stays **outside** its box, with a clear gap, centred on the word
  (Claude); (b) **Fix match on a folder** only when the folder holds one title (a TV show's or a film's folder: re-matches
  everything inside); not on mixed folders (Claude).

## To fix (approved idea, owner 20 Nov; draw at the Files review)
A **To fix** row in Library Care, with a count pill like the others: every title opened in Fix match (from any Fix
match in the app) and left without a new match is kept here until it is matched or marked "It's right". The dot on
Files in the top bar shows while anything is waiting. **Mock-up and details at the Files review**, not before.

## To settle at the Files review (owner, 20 Nov)
- **Fix match shows the file:** the file name and its location on the Fix match page, so the owner can tell which
  show or film the file really is before choosing a match. Place and look decided at the Files review.

- **New, to walk through at the Files review (owner, 21 Nov):** Library Care › **Excluded from hero** (list of titles
  kept off Home's hero; mock-up `home-v3/tv/17`, approved in principle only) and its row in Library Care.
