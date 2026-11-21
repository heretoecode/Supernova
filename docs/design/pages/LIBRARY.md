# Page sheet: Library pages (Movies, TV Shows, your own pages)

The one place to build the library pages from (CLAUDE.md "Definition of done"). For 0.158. Latest decisions as of
18 Nov 2026. **One design and one piece of code for every library page**; only which titles differ.

Sources: `docs/design/RULES.md` (§1, §2, §7–§11, §14, §16, §23), `docs/0.157/OWNER_NOTES.md` §32 (v1–v9, "Library pages
done", 13 Nov) and §38 (empty pages, 14 Nov). Latest mock-ups in `docs/design/proposals/library-pages/`:
`27-v8-page-calm-caption`, `31-v9-tidelands-focused`, `23-v7-filters-watched`, `24-v7-filter-lists`, `25-v7-sort-menu`,
`26-v7-list-view`, `28-v8-list-columns`, `33-v9-columns-moving`, `32-v9-customise-page`, `29-v8-page-name-keyboard`,
`30-v8-tv-show-long-press`, `34-v10-empty-pages`. Older pictures (01–22) are history. Where a mock-up still shows the
SUPERNOVA wordmark, an underline in the top bar or a stats row, this sheet wins.

## Whole page
- Plain charcoal `#16181C`. Side margin: the top bar's edges (RULES §1, owner 18 Nov): the toolbar, its line and
  the posters line up with the clock's right edge.
- 0.158 top bar (RULES §2 note): no wordmark; the page opened as focus landed on its name; while focus is on the page,
  its name has the soft grey box.
- **No statistics row** (moved to Files › Your Library). No page title (the top bar says where you are).
- Scroll arrows in the right margin, never a scroll bar (RULES §16).

## Toolbar
Left: **Filters ▾ (sliders icon; "Filters · 2" when two filters are on; owner 20 Nov) · Sort: <choice> ▾ · Order · List view / Grid view** (· **Columns** in list view only, appearing beside
the view button without anything moving). Right, locked: **Customise page, on your own pages only** (owner, 19 Nov: on Movies and TV Shows it only
repeated Filters' Show and the toolbar's View, Sort and Order, so it is removed there). Focus is the white box; a button whose menu
is open returns to rest. Menus open under the button pressed.
**Sizes (owner, 19 Nov, judged on the TV, `rows-captions/tv/08-toolbar-line-35.png`):** the buttons use the one
small-box shape (52 px tall, 20 px corners, about 22 px sides; RULES §3), about 8 px apart. Under them a line from
margin to margin, **white at 35%**, about 2 px in the middle and **thinning towards both ends like a lifted pen**.
**24 px** from the bottom of a focused button's box to the line, **24 px** from the line to the posters at rest (the
focused top-row poster and its ring still clear it). In list view the column headings sit 24 px below the line.

- **Filters** (`23`, `24`; redrawn on today's rules `tv/35`–`40`, owner 20 Nov): Watched (All · Unwatched · In progress · Watched; icons are the poster badge: grid, empty ring, pie, tick, `tv/41`) · Genre (row icon: shapes; genre icons; Match: Any · All at the top, its icon a ticked list) · Year (Any year · Recent · Your Own Range with From / To, Left / Right stepping only through years that hold titles; `tv/43`) · Streaming services
  (several); Clear filters at the bottom in its own group, apart by a line. Every list shows only values present, with
  counts. Lists use the shared choice list (RULES §9).
- **Sort** (`25`; final `tv/48`, owner 20 Nov): grouped menu, no search row, **never scrolls**: General (Date added:
  when the title first arrived, a show's first episode · Title · Release date / Air date) · Trakt (Trending · Popular;
  hidden when Trakt isn't connected) · Series (Last episode added · Number of seasons · Number of episodes, the
  numbers being how many the library holds; hidden on Movies; films fall back to Date added on mixed pages) · **Files
  last** (Running time · Size: a film's file, a show's size per episode; named "File size" on Movies, "Size per
  episode" on TV Shows, "Size" on mixed pages; **owner 20 Nov: "Size" on every page**, caption "24.6 GB" or
  "Average 2.2 GB · total 8.8 GB"). Removed (owner, 20 Nov): Container, Path, Bitrate, Codec, HDR, Audio,
  Date modified, Resolution, the separate File size. Icons: tray, A–Z arrow, calendar, trending line, heart, screen
  with a plus, numbered list, screen with play, stopwatch, drive. No Year sort. Tick on the current choice.
- **Order:** one button whose words follow the sort; **its width is fixed to the wider of its two words**, so flipping it never moves the buttons after it (owner, 20 Nov): A → Z / Z → A, Newest / Oldest first, Largest / Smallest first,
  Longest / Shortest first, Most / Fewest first, Highest / Lowest ranked first.
- **Columns** (list view, `28`, `33`; redrawn `tv/57`, `58`, owner 20–21 Nov): menu under its button: **Shown ·
  n of 7** (ticked, in column order; OK picks a column up and Up / Down moves it, live), Hidden (A–Z; dimmed while
  seven are shown), Reset to default at the bottom; scrolls with arrows in its margin. **At most seven columns
  besides Title.** No Done; Title always shows and keeps the most room. Column headings use the menu's names. List
  and defaults: see BRIEF_0158_B › Columns.

## Grid (default)
- One portrait poster size on every library page, **six a row** (owner, 20 Nov, `tv/50`; was seven: about 276 px wide
  at 1080p, 32 px gaps, edges on the side margin), artwork edge (RULES §14).
- **Poster and caption are one card** (`27`, `31`): caption box under the artwork in **`#141619`, solid** (owner, 19 Nov,
  judged on the TV, `rows-captions/tv/10` E; was `#23262C`). Line 1 the title (the
  only bright line); line 2 always a TV show's size or progress: not started "3 seasons · 30 episodes" (one season: "10
  episodes"), started "S2 E3 · 18 of 30 left"; films started "42 min left"; **line 3 follows the sort** (e.g. "1.0 GB
  per episode"). **No progress bar:** progress is the corner badge (RULES §14c: 48 px white circle, black pie from
  12 o'clock inside a white rim; watched = black tick; not started = none). Every caption the same height.
  **Caption spacing (owner, 19 Nov; `rows-captions/08-caption-spacing.png`, After):** a shorter box (about 100 px
  at 1080p, was about 108) with the three lines spread evenly: equal small gaps above, between and below; title kept
  (owner asked; Claude's view: keep it, small change later if wanted).
- Focus: the whole card grows about 8%, the thick white ring exactly on the card's edge, round artwork and caption.

## List view (`26`, `28`)
Rows with Title first, then the shown columns; long values end in "…"; lines between rows white at 20%, 1 px, fading
out at both ends (owner, 20 Nov); the focused row is the white box. The progress badge sits at the right edge of the
Title column with room either side (`tv/55`). Scroll arrows in the right margin. Same Filters / Sort / Order as the grid.

## Customise page (`32`, `29`; redrawn and approved `tv/59`–`68`, owner 21 Nov) — your own pages only
A side panel floating on the right (about 700 px), sized to fit without scrolling; changes apply live, **no Save
button**, Back closes. Group headings centred on their line, Title Case. Rows with more than two choices step with
Left / Right (RULES §8); two choices are chips.
- **What To Show:** Page name (the one keyboard floats over the panel straight under this row, the panel's width;
  the text appears in the row; Done returns) · Includes (‹ Movies and TV shows · Movies · TV shows ›) · Genres (shapes
  icon; Match: Any · All inside the list) · Years (the Year list: Any year · Recent · Your Own Range; value "From
  1990") · Minimum rating (‹ Any · 6+ · 7+ · 8+ · 9+ ›) · Original language · Studio / Network (search only above 12).
- **How To Show It:** Show (‹ All · Unwatched · In progress · Watched ›) · View (Grid · List) · Sort by (opens the
  Sort list on top) · Order (two chips that follow the sort) — how the page **opens**.
- **Delete this page** at the foot after a plain line, no heading. While focused it shows "Your videos stay in your
  library." on the right; OK deletes at once with the Undo notice; the top bar shows + and "Make this page your own".
- Movies and TV Shows have no Customise page (owner, 19 Nov).

## Long-press (or Menu) on a poster — beside the poster (`30`; reviewed by the owner 19–20 Nov, mock-ups `rows-captions/tv/11`, `12`)
- Movie: **Playback** Play (started: Resume · 42 min left, then Play from the start) · **Library** Mark as watched · Fix
  match · Remove from library · **Files & Media** Delete file.
- TV show: **Playback** Resume S1 E2 · Play from the start (not started: Play) · **Library** Mark show as watched · Fix
  match · Remove from library · **Files & Media** Delete all episode files.
- Icons each, no two alike; group headings centred on a thin line; no "More info" (OK already opens Details); no Play
  from the start on an unstarted title. Times read "2 h 22 min left", never "142 min".
- **Mark as watched** happens at once with **no notice**; on a watched title the row reads **Mark as unwatched** (show:
  Mark show as unwatched), so it is undone from the same menu (owner, 20 Nov).
- **Remove from library** happens at once with the **Undo notice** (RULES §7b; `rows-captions/tv/20`, `21`): the poster
  goes and the others close up. The menu is as wide as its longest
  line plus the standard padding.

## Delete warnings (owner, 20 Nov; mock-ups `rows-captions/tv/16a`, `16`, `17`, `18`; RULES §7b)
- **One file:** "Delete <title>?"; one line "Deletes **<file name>** (2160p · 58.2 GB) from **<server or drive>**.";
  under it "Its own subtitle and .nfo files go with it; anything else in the folder stays.";
  then **Clean Up**; buttons **Keep file** (focused when it opens) · **Delete file**.
- **Several versions of a film:** "You have **2 versions**. Tick the ones to delete." **Select all** as a switch (on =
  all ticked; unticking one turns it off), then one row per version: the facts line (first three of Edition ·
  Resolution · HDR · Sound · Video codec that the file has), under it file name · where it lives · size; the app's tick on
  the right; all ticked to start. Total under the list on its own line ("Deletes **2 files · 72.3 GB**."), under it "Each file's own subtitle and
  .nfo files go with it; anything else in the folder stays."; the button follows the ticks
  ("Delete 2 files").
- **A show (Delete all episode files):** "Tick the episodes to delete, including any not yet in your library." (one line) Select all switch; episodes grouped by season, each "S1 E1 · Episode name" over
  the facts line · size; files the library doesn't know carry a **Not in library** pill centred between the text and the
  tick; **four episodes visible** with both scroll arrows in the right margin (up dimmed at the top). Total on its own line: "Deletes **8
  files · 19.2 GB**."; under it: "An episode's own subtitle files go with it; anything else in the folders stays." Pictures, PDFs and
  other non-video files are never listed or deleted.
- **Clean Up** (a heading centred on a thin line, on every delete warning): Also clear viewing history in Supernova (the
  official mark, monochrome; off) · Also remove it from your Trakt history (Trakt's official one-colour logo; off) ·
  Delete the folder if it's left empty (folder icon; **on**; the folder goes only if nothing at all is left in it).
- **"This can't be undone."** appears **only while Delete is focused**, beside the button, in white, with a full stop
  (owner, 20 Nov: no exclamation mark). Left-aligned throughout (RULES §7b).

## Empty pages (`34`, RULES §23)
**Reviewed by the owner, 19 Nov** (`34` with today's top bar, speckle and 52 px button; wording below replaces `34`'s):
- Nothing added yet: no toolbar; centred **plain white icon, no circle behind it** (owner, 19 Nov), **"No movies yet"** / **"No TV shows yet"**, "Add a
  folder, a drive or a network share and your movies will appear here." (TV shows: "… and your TV shows will appear
  here."), **Add your videos** (to Home's step while setting up, otherwise Files).
- First scan running and nothing found yet: **the one spinning loading ring, animated exactly as on Preparing playback
  and the seek wait, with no grey circle behind it**; "Finding your TV shows" / "Finding your movies", **"The first
  ones will appear here in a moment."** No count; posters appear as soon as one is found.
- Your own page before set-up: approved as drawn in `34` (owner, 19 Nov), except its icon: plain white, no circle.
- Your third page before it is set up: a **+** in the top bar; the page says "Make this page your own", **Customise
  page** (the same panel, floating right; the message steps aside while it's open) and "More options for your library
  are in Files and Settings."

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| Focus lands on the page's name in the top bar | The page opens (no OK) |
| Down from the top bar | The toolbar's first button; then the first poster |
| OK on a poster / list row | That title's Details page; Back returns here with focus on the same poster (D17) |
| Long-press / Menu on a poster | Its menu beside the poster |
| Play / Resume | Preparing playback, then the player; Back returns to the same poster |
| Mark as watched | In place, with an Undo notice; caption and tick update |
| Fix match | Files › Library Care's Fix match page for that title (Match and return · Match and stay); Back returns here |
| Remove from library / Delete file | Undo notice / the delete warning ("Keep file" focused first); the poster leaves |
| Filters / Sort / Columns | Menu under the button; choices apply live; Back closes, focus back on the button |
| Order / List view / Grid view | Switches at once, in place; focus stays on the same title |
| Customise page | Side panel on the right; the page behind updates live; Back closes, focus back on the button |
| Page name | Keyboard panel beside Customise page; Done returns to the row |
| Add your videos (empty page) | Home's Add your videos step while setting up, otherwise Files › This Device |
| Back from the posters | First to the toolbar / top of the page, then to the top bar (RULES §16) |

## Never opens from a library page
NOVA's old browse screens or hidden grid, the old Edit library / Edit page panel, the old long-press dialog, the Fix match
side panel, Android's keyboard.

## Under the top bar (owner, 19 Nov; RULES, before "Replaced rules")
Scrolled posters run on behind the top bar and blend behind its words under the bar's soft fade, exactly as on
Movies and TV Shows in 0.158.4. Same on your own pages. Never a solid band; nothing clipped below the bar.
