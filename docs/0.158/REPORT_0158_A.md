# 0.158 (part A): report

Built unattended from `docs/0.158/BRIEF_0158_A.md` and the page sheets (`docs/design/pages/LIBRARY.md`, `HOME.md`,
`SEARCH.md`). Version **0.158**, code **1580**.

## Signed APK
_Filled in when signed: run link, source commit, artifact._

## Diagnostics report
Not in `docs/INBOX.md` and no "Inbox:" issue, so none was read. **Please send the latest report from the Shield**;
it will be read for faults you didn't see.

## What was built

**Top bar** (mock-ups `files-review-0157-1/08`, `09` A; `Review0158TopBarTest`)
- No SUPERNOVA wordmark; the places start at the bar's left edge.
- Every item is a word: Home · Movies · TV Shows · your page, then Files · Search · Settings and the clock, all in one
  font, weight and size, each centred on its letters.
- Media pages open as focus rests on them (no OK); Files, Search and Settings open on OK.
- The open page keeps a soft grey box with a faded word while focus is elsewhere; the white box shows where the
  remote is; no underlines in the bar.

**One side margin** — the library pages, Files and Search line up with the top bar's edges (one shared value);
Home has no margin, its rows run off both edges.

**Library pages** (Movies, TV Shows, your own page: one design, one piece of code; `Review0158LibraryTest`)
- No page title, no statistics row.
- Toolbar: Filters · Sort: <choice> · Order · List view / Grid view (· Columns in list view), Customise page on the
  right; menus drop down under the button pressed.
- Filters: Watched · Genre (Match Any / All, genre icons) · Year (quick choices that hold titles, then From / To) ·
  Streaming services; counts on every choice; Clear filters apart by a line. Choices apply live. From and To step
  with Left / Right through the years that hold titles (OK opens the list).
- Sort: grouped menu (General · Trakt when connected · Files · Series, not on Movies), tick on the current choice, no
  Year sort. Order's words follow the sort (A → Z, Newest first, Largest first, Longest first, Most first, Highest
  ranked first …).
- Grid: seven posters a row, poster and caption one card (title; a show's size or "S2 E3 · 18 of 30 left"; "42 min
  left" for a started film; line three follows the sort), inset progress bar, tick on watched titles; focus grows the
  whole card 8% with the thick ring.
- List view: title then the shown columns, plain headings (sorted one bold), faint lines between rows. Columns menu:
  Shown (ticked, in order; OK picks a column up and Up / Down moves it, live), Hidden A–Z, Reset to default.
- Customise page: a floating panel on the right, live, no Save, Back closes. Your page: Page name (the one keyboard
  beside it), Includes, Genres, Years, Minimum rating, Original language, Studio / Network; every page: Show, View,
  Sort by, Order; Delete page at the foot (own page) with the delete warning.
- Long-press (or the Menu button) on a poster: beside it, Playback · Library · Files & Media — Play / Resume (shows:
  Resume S1 E2, Play from the start), Mark as watched (Undo), Fix match, Remove from library (Undo), Delete file /
  Delete all episode files (warning, "Keep file" first). No "More info".
- Empty pages: "No films yet" / "No TV shows yet" with Add your videos (Files); "Finding your …" with the loading ring
  while the first scan runs; your page before set-up: "Make this page your own", Customise page and the quiet line
  about Files and Settings.
- The old Edit Page panel and the old long-press dialog are gone; `NoOldScreensTest` fails the build if a library
  page names an old screen.

**Home** (`Review0158HomeTest`)
- Row cards: one size (400 × 225 at 1080p, 440 × 248 focused) with the caption box under the picture (title, one soft
  line such as "S1 E2 · 31 min left", inset bar); nothing over the artwork.
- Move and Hide beside the row's heading, plain buttons (Hide a minus circle), shown when focus reaches the heading;
  Move lifts the row ("Row 1 of 6 · OK when done"), Up / Down moves it, OK or Back puts it down; Hide has Undo.
- Customise Home: floating panel on the right — Your Rows (› opens settings; Left / Right switches), Add a row,
  Built-In Rows with a switch each, in Home's order; a long press of OK picks a row up, Up / Down moves it (Home
  follows live), OK or Back puts it down. A row's settings open on top: Row name, Icon, Includes, Genres (+ Match), Years,
  Minimum rating, Sort by, Order, Maximum items (10–30), Add new titles automatically, Delete row (warning). Icon
  opens the grid picker (five a row, icon and name, tick on the current one).
- Long-press on a card opens the library long-press menu. The old Home editor is gone.
- Hero: the description on two lines (never the tagline); buttons Play (or "Resume S1 E5 · 16 min left" /
  "Resume · 42 min left") then More (beside the button: Details, then the library menu's items); a change of hero
  cross-fades the old picture out over the new one (about 450 ms, slight drift), never an empty frame; no movement
  when animations are off.

**Search** (`Review0158SearchTest`)
- Plain charcoal page, no panel: field and keyboard on the left, focus on T.
- Before typing: Recent searches as rows with a magnifying glass, Clear history (✕, plain until focused) with Undo;
  What you can search as plain tips (Titles · People · Genres · Years · Your services only with Where to Watch on).
- Results under one "In your library" heading with outlined pills: Resume S5 E3 · Watched · Unwatched; "Nothing in
  your library matches …" when nothing does. A person match says who and as what ("Avery Lindqvist · director").
- Films: "Stream more from the world of <title>" (cloud icon) lists the other films of its story — its TMDB
  collection, sequels and prequels — that you don't own and that are on your services, with the service's pill and
  the open-in-app icon; OK opens it in the service's app. Only with Where to Watch on, and only when a library film
  matched. The old filter chips no longer come back when results arrive.

**Files › Connections (F52)** — one row per server and login, its folders on the second line ("WebDAV · Films,
TV"); Open asks which folder when there are several; Remove takes the whole server, with Undo; Scan one source has one
button per server, named by the connection's name.

## Not built (and why)
- **A true shared-element glide** from the Home hero into Details: Details now opens from the hero with no slide or
  fade, so the card (same size and place on both pages) stays put; a moving glide needs checking on the Shield.
- **Search streaming groups for TV shows and people** ("Stream more related to …", "Stream more from <person>"):
  TMDB has no spin-off links and the 0.157 related-titles service mixes in "similar" titles, which the sheet rules
  out for Search. Films get their group (below).
- **Genre list's "Selected" group:** the genre list keeps Match: Any · All at its top (as drawn), so ticks stay in
  place there; the services list has its Selected group.
- **Icon picker tile width:** the longest genre names ("Action & Adventure") are cut short in their tile; a wider
  tile for those comes in 0.158.1.
- **Settings and onboarding**: 0.159, as planned.

## Judgement calls
See `docs/0.158/JUDGEMENT_CALLS_0158_A.md` (each with its reason).
