# 0.155 — owner notes from the 0.154 Shield check (4 Nov 2026)

> **Release order (owner, 4 Nov 2026):** the engine update ships **first, on its own**, as the next version
> (**0.155**). Everything in these notes, plus the stuck-scan fix, the Network & Files redesign and Codex's
> non-engine work, follows in the version after it (**0.156**). Existing file names keep their working labels:
> Codex's `engine-0156-*`, `*supernova156*` and `supernova-0156-*` files are the engine release; this folder
> (`docs/0.155/`) holds the app/UI release notes.

Also queued for 0.155: `docs/0.154/OWNER_NOTES.md` items 13–17 (Network & Files redesign, stuck-scan fix) and
Codex's branch `codex/0155-code-2026-11-04` (DP-01…DP-07, TV-only stages). **Nothing is coded until the owner says so.**

> **How mock-ups are shown (owner, 4 Nov 2026):** only final mock-ups the owner needs to judge, sent once, as one
> numbered group per question. Claude checks renders without opening them in the chat (no draft or check images).

> **Panel and pop-up size rule (owner, 4 Nov 2026):** every list, choice panel and pop-up is only as big as its content
> needs — width fits the longest entry plus padding, height fits the entries (scrolling only when they exceed the
> screen). Applies app-wide, including the shared choice list (item 19.1).

## Confirmed fixed in 0.154 (owner)
- Hero More Info icon dark on focus; no age rating or score on hero cards; hero readability improved; focused
  posters have one crisp outline; Playback Settings has no title and no row lines.

## 1. Hero text readability on some artwork (design)
- **Seen (owner photos, 4 Nov 2026):** Kingdom — year · genre and synopsis on a bright yellow/pink sky, hard to read;
  the synopsis runs across the brightest area. Mayday — readable but weak on a busy crowd. Mr. Robot (flat red) fine.
- **Proposed (Claude):** a soft, vignette-like dark patch behind the text block only (a little stronger than the
  current left fade, artwork elsewhere untouched); year · genre line in Medium weight, pure white, firmer shadow;
  synopsis width limited to the left half of the card.
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 2. Hero title logo placement (design)
- **Seen:** Kingdom and Mr. Robot — wide logos sit at the very top of the card with a large gap above the metadata
  row; Mayday sits close to its metadata.
- **Cause (likely):** the logo box has a fixed height and wide logos scale to its width, drawing at the top.
- **Want:** the logo sits just above the metadata whatever its shape (bottom-aligned in its box, consistent gap).
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 3. Playback Settings: Audio and Playback tab icons invisible when focused (bug)
- **Seen:** Subtitles tab focused → dark icon; Audio or Playback focused → icon barely visible (white on white).
- **Status:** agreed bug.

## 4. Playback Settings: live subtitle preview (design)
- **Want:** while changing subtitle appearance (size, colour, outline, background, opacity, position), show sample
  text on the video so changes are visible live, whether or not a subtitle is currently on screen.
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 5. Download Subtitles: no pop-up in the middle of the screen (design)
- **Seen:** "Download Subtitles · Searching OpenSubtitles… · Cancel" appears as a separate dialog centre-screen,
  then the results appear inside the panel as expected.
- **Want:** the searching state happens inside the panel (same page as the results), Back cancels.
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 6. Panel colour check
- Owner confirmed 4 Nov 2026: the panel is the correct charcoal on the TV; the blue tint was the phone camera. Closed.

## 7. Playback Settings: colour swatch selection ring is clipped (bug)
- **Seen (photo):** the ring round the chosen colour (green) looks like a circle with flat, square-ish edges.
- **Cause (Claude, from the code):** `PlaybackSubtitlePanel.Swatch` draws a 15px-radius dot plus a white ring at
  radius 18.5px with a 3px stroke (outer edge ≈ 20px) inside a 38×38px view (half-width 19px), so the view clips the
  ring on four sides.
- **Want:** a complete, clean ring with a small dark gap between the colour and the ring; the swatch view sized to
  fit it. Row focus (white outline) and "chosen colour" (ring) stay visibly different.
- **Status:** agreed bug; fix approved as mocked (`0155-checks/04`) — awaiting go-ahead to code.

## 8. Playback HUD: buttons, time bubble and seek bar (design)
- **Seen (owner photos):** play/pause and Settings are solid black boxes that look wrong on the picture; the blue time
  bubble is small and sits on the seek dot; seek bar colours (white played, light blue remaining) read oddly.
- **Decision (owner, 4 Nov 2026), mock-up `0155-checks/07` (vs `06` today):** HUD buttons in charcoal glass with a
  faint edge (white box on focus unchanged); larger charcoal time bubble (time large, chapter smaller) raised clearly
  above the dot; seek bar white played / light grey buffered / dim grey remaining, slightly thicker, chapter marks as
  gaps, larger dot with a soft halo when focused.
- **Status:** approved — awaiting go-ahead to code.

## 9. Details: flat blue card before the artwork arrives (bug)
- **Seen (owner photos, Marshals):** Home hero shows the backdrop; pressing through to Details shows the logo and
  text on a flat navy card for a moment, then the backdrop pops in.
- **Cause (Claude, from the code):**
  - The Details card (`CinematicCard`) has a solid navy background (`0xff07131f`), and
    `PreviewMoviePage.cardArtwork()` loads with `noPlaceholder().noFade()`, so the navy shows until Picasso finishes.
  - The Home hero already has that same backdrop decoded in memory, but at `resize(960×540)`; Details asks for
    `fit().centerCrop()` at the card's size. Picasso keys its memory cache by URI *and* size, so it misses and
    decodes again from disk, or downloads again.
  - Details can also switch image part-way: `bind()` starts the hero's backdrop, then `setTags()` (or `bindRemote`
    with the TMDb `w1280` URL) picks a different URI — local file vs remote, or a different default backdrop —
    which restarts the load and the navy shows again.
- **Proposed fix (not coded):**
  1. Hand the hero's on-screen bitmap (or its exact Picasso key) to Details, and show it on the card the moment the
     page opens, before layout finishes.
  2. Load the full-size image behind it and cross-fade it in when ready, instead of a hard swap.
  3. Only change image when the new one is genuinely different, and keep the current one showing while it loads.
  4. Make the empty-card background charcoal (matching the panels), not navy, for titles with no artwork at all.
  5. Test: a Robolectric test that Details opened with a seeded bitmap shows a drawable straight away, and keeps a
     drawable throughout a later URI change.
- **Status:** approved by the owner on 4 Nov 2026 (no mock-up needed) — awaiting go-ahead to code.

## 10. Auto-next / next episode can play a hidden file (bug, Codex DP-09)
- **Found by Codex** (`docs/codex/findings/deep-hunt-1-player.md`, DP-09, on `codex/0155-code-2026-11-04`):
  `player/UpdateNextTask.java` picks the next file in a folder (auto-next/repeat) and the fallback next episode
  without the user-hidden filter (`Archos_hideFile=0`), so a hidden video can start playing by itself. The playlist
  query in the same file already filters correctly.
- **Decision (owner, 4 Nov 2026):** hidden files are skipped by auto-next, repeat and next episode. Playing a hidden
  file directly, when chosen on purpose, still works.
- **Tests:** a hidden file between two visible ones is skipped; every remaining file hidden means no next file;
  repeat wrap-around skips hidden files; cached and uncached next-episode paths both skip them.
- **Status:** approved — awaiting go-ahead to code (Claude, not Codex: it changes what plays).

## 11. Local Storage: show library status on every row (design)
- **Seen (owner photo, Backup #1 › NVIDIA_SHIELD › Movies):** only the focused row shows Play / Exclude; the other
  rows give no hint whether they are in the library or excluded. Today an excluded row is only dimmed with
  "Excluded" at the end of line 2; included and not-added rows look the same.
- **Mock-ups:** `docs/design/proposals/local-storage/22` (A: a small tag after the title on every row — ● In library,
  ⦸ Excluded with the row dimmed, ○ Not added), `23` (B: only rows that differ from their folder are tagged), `24`
  (A in a mixed folder). The focused row's button matches the state: Exclude / Include / Add to library.
- **Question (owner):** what is the green tick on the right? It is **Watched** (existing in 0.154). Two greens with
  different meanings is confusing; Claude proposes the watched tick in neutral white/grey so green means "in library" only.
- **Decision (owner, 4 Nov 2026):** option **A** (every row tagged). The watched tick becomes a **larger** solid light
  disc with a dark tick (about 44px, was a 26px green outline), so green means "in library" only. Mock-up `25`.
  The folder header gains an "N watched" pill.
- **Owner follow-up (4 Nov 2026):** the tick is not self-explanatory at first glance; use words. Proposed `26`: the
  right-hand end of each row reads **✓ Watched** (white), **40% watched** (blue, with a small bar) or **Unwatched**
  (dim grey). Supersedes the disc in `25`.
- **Owner (4 Nov 2026):** words are much clearer, but bar + "40%" + "watched" read as three separate things.
  Proposed `29` (list) / `30` (grid): **one pill** — fills with blue as you watch ("40% watched"), solid white
  "✓ Watched" when finished, faint outline "Unwatched" when not started. The grid uses the same pill on the art and
  drops watched info from the line under the title.
- **Status:** approved by the owner on 4 Nov 2026 (`26`–`30`, the single pill as `29`/`30`) — awaiting go-ahead to code.

## 13. Local Storage Grid view: artwork, status and long-press menu (design + bug)
- **Seen (owner photos):** Grid shows a generic file icon on every card, never artwork, though the card's own code
  comment says "artwork or icon" (`LocalStorageView.Card.bind` only ever sets `PreviewIcon`). Long press opens a
  translucent Play / Exclude box that sits over the next card with that card's name showing through it.
- **Proposed (`27`, `28`):** each card shows the film's poster when matched, otherwise a frame from the file,
  otherwise the icon; a corner dot for the library state (● in, ⦸ excluded and dimmed, ○ not added); a **Watched**
  badge or a progress bar on the art; a status line under the title ("2015 · In library · Watched"). Long press /
  Menu opens an opaque panel beside the card: title, status line, then Play/Resume, Details, Mark as
  watched/unwatched, Exclude/Include/Add to library. Posters depend on item 12 (library lookup) being fixed.
- **Details (owner question):** opens the same film/episode Details page as from Movies or TV Shows (artwork,
  synopsis, cast, Play/Resume). It is only offered for files in the library, and because of item 12 it currently never
  appears. Files not matched or not added get no Details entry. "Mark as unwatched/watched" is new in `28` (not in 0.154).
- **Decision (owner, 4 Nov 2026):** for files not in the library, Details looks the film up online (TMDb, from the file
  name) and opens the same Details page with Play and Add to library and a "not in your library yet" line; with no
  confident match, a short "Couldn't identify this file" page with Play and Find match. Mock-ups of those two pages to
  follow.
- **Status:** grid/menu approved as `27`/`28`/`30` — awaiting go-ahead to code.


## 12. Local Storage: library rows show only file name and size (check)
- **Seen (same photo):** in a folder marked "In your library", rows show the raw file name and only the size
  (e.g. "Aftersun.2022.1080p.WEBRip.x264-RARBG · 2.1 GB"), not the matched title, year, runtime, resolution and audio.
- **Suspicion (Claude, not confirmed):** `LocalStorageView` looks each file up in the library by its URI string; if
  the USB drive's path in the library differs from the browser's (e.g. `file:///storage/XXXX-XXXX/...` vs a
  mount alias), every lookup misses and rows fall back to name + size. To confirm on the next diagnostics export or
  with one photo of a film from this folder in Movies.
- **Status:** owner confirmed 4 Nov 2026 that the rows have no details under the title and wants it fixed — investigate and fix in 0.156 (awaiting go-ahead).

## 14. Home: row controls as buttons beside the row, not a pop-up (design)
- **Seen (owner photos):** Left on the first card of a Home row opens a "Move / Hide" pop-up; Move then opens a
  second numbered list of rows ("1 · Continue Watching …") where choosing a number swaps the rows
  (`PreviewHomeRows.rowControls`). Customise Home at the bottom of Home also needs work (later item).
- **Want (owner, from the Shield home screen):** no pop-up; buttons to the left of the row that appear only when you
  reach the start of the row.
- **Proposed (`docs/design/proposals/home-row-controls/01`–`03`, revised 4 Nov 2026 after the owner asked for the
  buttons side by side and the same size):** Left on the first card slides the row right and shows two same-size
  round buttons side by side, **Hide** and **Move**, each labelled underneath; the focused one is white. Left/Right
  moves between them, and Right from Move returns to the first card. On the first card a faint ‹ in the left margin hints that there is more that way (`00`); one press Left reveals the
  buttons. OK on Move lifts the row's cards slightly and dims the other rows (no outline box — the owner found the cut-off
  box in the first `03` odd); Up/Down moves it live, one place at a
  time, with "Moving · row 2 of 7"; OK or Back drops it. Hide removes the row with an Undo notice (rows come back
  from Customise Home).
- **Moving icon:** the same single Move arrow as before (owner: the up / double / down stack looked duplicated); the
  button stays white and its label reads "Moving".
- **Motion (proposed):** no snapping. Each Up/Down glides the carried row into the next slot (about 0.2 s, ease-out)
  while the row it passes slides the other way at the same time; the page scrolls so the carried row stays at the
  same height on screen and the others move past it. Holding Up/Down repeats. (0.154 rebuilds the whole page after a
  move, so it snaps.)
- **Status:** approved by the owner on 4 Nov 2026 (`00`–`03` and the motion as described) — awaiting go-ahead to code.

## 15. Intro/outro skip on by default — already true in Supernova (correction, 4 Nov 2026)
- **Claude's earlier note was wrong.** Signed Supernova builds use the switch **Settings › Connections › Service
  integrations › Enable IntroDB** (`supernova_segment_enabled`), which defaults to **on**; the player reads it
  (`PlayerService.segmentSkippingEnabled`, `BuildConfig.FOUNDATION`). The default-off `introdb_enabled` key belongs to
  NOVA's own builds and is not what Supernova uses.
- **Owner decision stands:** on by default — already the case. No visible change needed.
- **Tidy-up (internal, no visible change):** a few player paths (e.g. the skip-prompt check and some logging) still
  read the old `introdb_enabled` key; make every path use the one switch, with a test that a fresh install skips and
  turning the Service integrations switch off stops all skipping.
- **Status:** tidy-up approved — awaiting go-ahead to code.

## 16. Settings › IntroDB page (bugs, from the owner's photos 4 Nov 2026)
- **Values one row out of step:** the right-hand values sit one row too high — "Show Skip (5 seconds)" appears on the
  IntroDB heading row, Intro's row shows Recap's "Smart", and so on; only the summaries under each title are right.
- **Each value shown twice:** once as the summary under the title and once on the right.
- **The choice pop-up** is the old translucent box over the list (same problem as the Local Storage long-press menu).
- **The master switch lives elsewhere:** Settings › Connections › Service integrations › Enable IntroDB (see item 15).
  The IntroDB page gives no hint that its choices depend on it.
- **Same misalignment on Service integrations (owner photo):** the three switches sit one row too high — the first is
  beside the "Service integrations" heading and the "Enable IntroDB" row appears to have none, though it is the third
  switch. Likely one shared cause in how these settings rows are laid out; fix once for every Settings page.
- **Proposed (words, mock-up on request):** every value or switch on its own row, aligned with its title; each value
  shown once; the IntroDB page says under its heading whether skipping is on, with a link to the switch; the choice
  opens as an opaque list in the side-panel style.
- **Status:** awaiting the owner's approval — then awaiting go-ahead to code.

## 17. Playback: the IntroDB Skip button (design + bug)
- **Today (Claude, from the code — `PreviewUpNext.segmentPill`):** "Skip Intro" (or Recap/Preview…) appears for
  5 seconds as a charcoal button bottom right, but it never takes focus: OK does nothing to it, and it can only be
  reached by opening the controls, going to More and pressing Down. The owner has never noticed it.
- **Proposed (`docs/design/proposals/skip-button/01`–`04`):** white button "Skip Intro", bottom right, already
  selected when it appears — one OK skips; a thin bar inside counts down the 5 seconds, then it fades and the part
  plays; Back/Left/Right/Up dismiss it; with the controls open it sits just above them, still selected, and Down goes
  to the controls.
- **Owner (4 Nov 2026):** bottom right, and bigger. Revised `01`–`03`: about 104px tall with 38px text (was 72px / 26px);
  bottom-left alternative dropped.
- **Owner (4 Nov 2026):** look liked; still needs to be quite a lot bigger. Revised `01`–`03`: about 136px tall with 50px
  text.
- **Timing (Claude, from other players):** Plex shows its button for about 3–5 seconds (a common complaint: too short)
  and brings it back with the on-screen controls; Jellyfin's Intro Skipper can show it for the whole intro. Proposed:
  **10 seconds** with the countdown, then it fades, but opening the controls brings it back (selected) until the
  intro ends.
- **Owner (4 Nov 2026):** timing approved as proposed; 136px too big. Revised `01`–`03`: about 120px tall with 44px text
  (between the 104px and 136px versions).
- **Owner (4 Nov 2026):** 120px size fine; text and icon look off-centre because the countdown bar sits underneath.
  Options: `04` A — keep the bar, centre text and icon in the space above it (equal gaps, checked); `05` B — no bar,
  the button's white drains away to light grey as the time runs out, text centred on the whole button.
- **Decision (owner, 4 Nov 2026):** option **B** (`05`): no bar; the button's white drains to light grey over the
  10 seconds; text and icon centred on the whole button. Final: bottom right, ~120px / 44px text, selected on
  arrival, OK skips, fades after 10 s and returns with the controls until the part ends.
- **Status:** approved — awaiting go-ahead to code.

## 18. Search page: keyboard, results layout and relevance (design + bug)
- **Seen (owner photos):** the keyboard keys are uneven, staggered and crowded (blob-shaped keys, tiny Clear / Done /
  Cancel row, Shift "ON"), not in a charcoal panel. Result rows repeat noise on every line ("Resolution
  unavailable", the file name, the certificate), on blue bars rather than charcoal.
- **Relevance bug (Claude, from the code — `PreviewSearch` + `PreviewSearchText.rank`):** "dune" returned
  **237 results**, including every Ridley Road episode. Each row is matched against title, episode name, file path,
  cast, crew and studios with typo tolerance on all of them, so short words match far too much. To fix: exact and
  prefix title matches first; typo tolerance on titles only; people/studios only on whole words; file paths only
  when nothing else matches; results grouped and capped per group.
- **Proposed (`docs/design/proposals/search/01`–`02`):** left — a charcoal panel with a large search field, a row of
  Voice (Shield remote mic), Space, Delete, Clear, and large, even keys (first shown as an A–Z grid; owner chose QWERTY). Right — before typing: recent searches and three short "what you can search" lines; while
  typing: results grouped Films · TV shows · Episodes · People, each two lines (title + year; type · runtime ·
  resolution only when known · genres), and a blue "Matched …" line when the match was not the title. No file names,
  certificates or "unavailable".
- **Owner (4 Nov 2026):** keep a traditional QWERTY layout. Revised `01`–`02`: number row, QWERTY rows with the usual
  half-key stagger, Delete at the end of the Z row, and a bottom row of Voice · Space · Clear; every letter key the same
  size; keyboard panel widened to fit.
- **Owner (4 Nov 2026):** the keyboard is app-wide (the same `PreviewKeyboard` is used for network credentials,
  server addresses, renaming, etc.), so it needs symbols. Revised (`01`–`04`): letters page = number row, QWERTY with
  `/` after L and `.` `@` after M, ⇧ Shift for capitals (shows ON, blue edge); bottom row `#+=` · Voice · Space ·
  Delete · Clear. Symbols page (`#+=`) = `! # $ % & * ( ) - _ = + [ ] { } \ | ~ \` : ; , ? ' " < > ^`, ABC returns. `03` shows
  it on a server address, `04` on a password.
- **Owner (4 Nov 2026):** one panel, not two; and a Clear option beside Recent searches. Revised `01`–`04`: a single
  charcoal panel with a thin divider (keyboard left, results right); "Clear recent" button on the Recent searches line.
- **Keyboard rule (owner, 4 Nov 2026):** on Search the keyboard is always shown (and stays while browsing results);
  in forms (Add Connection, credentials, rename) it is hidden until a field is chosen, slides in with the field's name
  above it, and Done/Back puts it away and moves to the next field.
- **Status:** approved by the owner on 4 Nov 2026 (`01`–`04` and the keyboard rule) — awaiting go-ahead to code.

## 19. Suggestions approved by the owner (4 Nov 2026) — designs/checks to follow
1. **One shared choice list** replacing every old translucent pop-up app-wide (Local Storage long-press, IntroDB
   choices, Home Move/Hide and any others found), in the solid side-panel style.
2. **Sleep timer back:** 30 / 60 / 90 minutes or end of this episode (the picker exists but its menu entry is commented
   out in `PlayerActivity`). Mock-up to follow.
3. **Supernova screensaver:** slow full-screen backdrops from the library with the title in a corner (Android TV
   Dream service; none today). Mock-up to follow.
4. **Fix match from Details:** search and pick the right title without leaving Details. Mock-up to follow.
5. **Library Health badge:** e.g. "3 new files couldn't be identified" shown on the Network & Files tile.
6. **Remember audio/subtitle choices per series** — check first how much already exists, then describe.
7. **"Still watching?"** after a long run of auto-played episodes.
8. **Consistent empty states** on every page (calm message + one useful button).
9. **Focus memory everywhere** — audit pages that do not return to where you were.
- **Status:** approved in principle — each gets its mock-up or description for a final look, then awaits go-ahead to code.

## 20. Search: filters, browse and remembering (design; discovery dropped)
- **Proposed by Claude, mocked on request (`docs/design/proposals/search/05`–`07`):** results from your library first,
  then **Not in your library · from TMDb** (OK opens the approved "not in your library yet" Details); a filter row
  **All · Films · TV shows · Episodes · People** reached with Up from the results; the watched pill on library results;
  the typed letters bold and underlined in each title; before typing, **Browse** shortcuts (Unwatched, Recently added,
  Genres, Years) under Recent searches; coming back from Details restores the same query, results and position. Play /
  Resume from results via long press or Menu (shared choice list).
- **Owner (4 Nov 2026):** Search must not become a discovery page — **no TMDb / "Not in your library" results** for now
  (maybe in the future). `05`–`06` re-rendered with library results only. Filters, watched pill, bold match, Browse
  shortcuts (library only), remembering the search and Play/Resume from results stay.
- **Owner (4 Nov 2026):** remove the **Browse** shortcuts — the library pages already cover browsing, sorting and
  filtering, so they would duplicate them. `07` re-rendered: recent searches and the one-line tip only.
- **Owner (4 Nov 2026):** make "what you can search" prominent again and fill the space (today's large text is
  right in spirit; the one-line tip was too small). `07` re-rendered: under Recent searches, five rows with an icon tile,
  30px title and 22px line — Your library · Flexible formatting · Original titles · Minor typos · People & studios.
- **Owner (4 Nov 2026):** no grey icon tiles; centre the list in the space; check every claim is true, reword freely.
  Checked against `PreviewSearch` / `PreviewSearchText`: titles, episode names and file paths are searched; original
  titles (`original_title_movie` / `_show`); actors, directors, writers and studios (movie and episode columns);
  query words may come in any order and match the start of a word; case, accents, punctuation and a leading
  the/a/an are ignored; one wrong letter is allowed in words of 4+ letters. Revised `07`: six rows (Titles and episodes ·
  Original titles · Cast, crew and studios · Any order, part words · Forgiving · Small typos), plain white line icons,
  centred in the space below Recent searches. Wording reflects 0.156 behaviour (file names last, people by whole name).
- **Owner (4 Nov 2026):** wording approved; make the "What you can search" heading more prominent. `07`: heading at
  38px Medium white (rows 28px).
- **Status:** wording approved; heading size awaiting the owner's look — then awaiting go-ahead to code.

## 21. Top bar scroll effect on Home, Movies, TV Shows and custom libraries — rebuild (bug + design)
- **Seen (owner):** scrolling down puts a blue blur under the top bar that does not fully go away when nothing is under
  it; on the third library (Documentaries) it looks like a blur plus a solid box. Not consistent across the four pages.
- **Cause (Claude, from the code):**
  - `PreviewNavigationShade` blurs a small copy of the page's **background artwork**, not the content actually
    under the bar, then paints a fixed **navy** gradient (`0x0b1624`) on top — hence "blue" and "blur of nothing".
  - It switches on from `list.canScrollVertically(-1)` (`PreviewPages.notifyScroll`), which stays true whenever the
    list is not at its very first pixel — the focused-row alignment keeps it slightly scrolled, so the effect lingers.
  - The custom library (`CustomLibraryActivity`) puts its page header outside the scrolling list, so the bar's effect
    sits over a fixed header — the "solid box".
- **Proposed (`docs/design/proposals/top-bar/01`–`03`):** remove the old effect entirely and build one shared one:
  driven by the page's real scroll distance; nothing at all at the top; content passes behind the bar under a
  **charcoal** fade (the panels' colour, no blue, no blur of unrelated artwork) that eases in over the first ~120px of
  scroll and out again as you return; custom libraries use exactly the same page structure (their header scrolls with
  the content). Tests: at the top the effect is 0 on all four pages; scrolled it is full; returning to the top clears it.
- **Owner question:** is the artwork still visible behind the bar? Yes — `04` shows posters sliding under it: the fade is
  ~88% charcoal at the very top (behind the tabs), ~60% at the tabs' lower edge and clear by ~230px, so artwork stays
  visible and fades out towards the top while the tabs stay readable.
- **Status:** approved by the owner on 4 Nov 2026 (`01`–`04`, fade strength as `04`) — awaiting go-ahead to code.

## 22. Customise Home — redesign (design + bug)
- **Seen (owner photos):** three nested translucent blue panels; eye icons and drag handles beside every row; the
  Genres list opens as an overlapping panel with a blank white row stuck in it; built-in rows show an almost empty
  page; a trash icon floats in a corner.
- **Proposed (`docs/design/proposals/customise-home/01`–`04`), using the approved patterns:** one charcoal panel
  split by a thin line (as Search / Local Storage). Left: "Customise Home · 8 rows · 6 shown" with **Add row**, then
  two-line rows (name + Shown/Hidden tag; second line "Built-in · …" or "Your row · Films · Crime · 15 · Date added");
  hidden rows dimmed; **Move** and **Hide/Show** buttons appear on the focused row. Move lifts the row and Up/Down
  moves it live (as on Home, item 14). Right: the selected row — title, one-line description, then settings rows
  with the value on the right (the item-16 fix): built-in rows show "Shown on Home" switch and Position; your own rows
  add Name, Icon, Shows (Films / TV shows / Both as one segmented choice), Genres, Sort by (field + direction in one),
  Maximum titles, "Add new matching titles automatically" switch, and **Delete row** at the bottom. Genres opens a
  solid side panel with tick boxes and counts (Back saves) — the shared choice list (item 19.1).
- **Owner (4 Nov 2026):** too large — it does not need the full screen. Revised `01`–`04`: a **side panel on the right**
  (760px, like Playback Settings / Edit Page) over a live, dimmed Home; pages drill in within the panel (list → row
  settings → Genres, with "‹ back" at the top); while moving a row, Home behind highlights and updates live.
- **Owner (4 Nov 2026):** each genre needs its own monochrome icon, using the app's existing ones. The app already
  draws one per genre in `PreviewGenres.Icon` (24-unit line icons, 1.5 stroke); the mock-ups now use exactly those
  (translated from the drawing code into `customise-home/genre-icons.json`): the Genres picker, the Genres setting and a
  genre row's own icon. `05` shows all 27: 26 have their own icon; **TV Movie has none** and falls back to a plain
  frame — one new icon needed. "All genres" uses the filter icon.
- **Owner (4 Nov 2026):** give TV Movie its own icon in the same style; revisit all genre icons and add missing genres.
  Proposed set `06` (`customise-home/genre-icons-v2.json`, same 24-unit grid, 1.5 stroke, round ends): 22 kept;
  **revised** to be more distinct — Adventure (compass; was a near-copy of Action & Adventure's mountains), Action &
  Adventure (mountain with flag), Thriller (pulse line; the lightning bolt read as "electric"), Soap (heart in a speech
  bubble; the bubbles read as washing-up); **new** — TV Movie (screen with play), and 11 genres that other sources
  (Trakt, IMDb) use: Anime, Biography, Disaster (volcano; first drawn as a tornado, which did not read), Game Show, Holiday, Martial Arts, Mini-series, Musical, Short, Sport,
  Superhero. The picker in `03` uses the proposed set.
- **Status:** approved by the owner on 4 Nov 2026 (side panel `01`–`04`, genre icon set `06` with Disaster as a volcano) — awaiting go-ahead to code.

## 23. Settings review (all eight sections)
- Full review written to `docs/0.155/SETTINGS_REVIEW.md` from the owner's photos (owner brief: move, remove, rename or
  add settings, sections and pages as needed).
- **First mock-ups (`docs/design/proposals/settings/01`–`02`):** Settings › Playback — left: Settings › Playback with
  standard charcoal buttons Playback · Resume · Skipping · Picture (white bar marks the open one, white on focus);
  right: one panel of rows, each a title, one plain line and a single value or switch level with the title. `02`: the
  solid side-panel choice list (Default playback speed).
- **Owner (4 Nov 2026):** no need to keep the two-panel layout. Single-panel version `03`–`04`: one charcoal panel —
  "Settings › Playback" top left, section tabs Playback · Resume · Skipping · Picture top right (white underline on the
  open tab, white on focus), a thin rule, then the rows. Up from the rows reaches the tabs; Left/Right switches tab.
- **Owner (4 Nov 2026):** "Playback" appeared twice top left; dislikes the underline on the open tab. Revised `03`–`04`:
  the page title says Playback once (first tab renamed **General**, the repeated group heading removed); the tabs are
  one segmented control — the open tab a lighter charcoal, the focused tab white, no underline.
- **Owner (4 Nov 2026):** tabs as individual buttons? Choices inline to avoid click-through. Revised `03`–`05`: tabs are
  separate standard buttons (open tab lighter charcoal, focused white); short choices are inline segmented controls in
  the row — Left/Right changes them and saves straight away: Default speed 0.75× · 1.0× · 1.25× · 1.5× · 2.0×;
  Still watching Off · 2 · 3 · 5 episodes; Sleep timer Off · 30 · 60 · 90 min · End of episode. Rule for all of
  Settings: up to ~5 short options inline; longer lists (languages, countries, genres) open the side-panel list.
- **Status:** page pattern approved by the owner on 4 Nov 2026 (`03`–`05`: single panel, separate tab buttons, inline choices for up to ~5 options); review content approved by the owner on 4 Nov 2026: **Accent & Colour removed** (one fixed accent); renames **Where to Watch** and **Accounts & Services**; moves — Rescrape → **Refresh details** in Network & Files › Library Health (collections rescrape removed), Network connections page removed in favour of Network & Files, IntroDB skip modes to Playback. Remaining review items follow SETTINGS_REVIEW.md; each section gets mock-ups — then go-ahead to code.

## 24. Settings › Audio & Subtitles (design)
- **Mock-ups (`docs/design/proposals/settings/06`–`09`), approved page pattern:** tabs Audio · Subtitles · Style · Fonts.
  Audio: preferred audio language (flag + name, opens the list), prefer original language (switch), surround sound
  inline (Automatic · Pass through · Stereo), virtual surround (switch). `07`: language list in the solid side panel —
  a flag for every language, a neutral badge where no country fits (Original, Latin…). Subtitles: Show subtitles inline
  (Off · Forced only · Always), preferred subtitle language, languages to search online, prepare next episodes, find
  subtitles automatically. Style: the live-preview editor approved for Playback Settings (item 4). Fonts: folder shown
  with font count; Change opens Supernova's Local Storage browser, Clear inline.
- Stand-in flags in the mock-ups; the app uses its own flag set (missing flags to be added).
- **Owner (4 Nov 2026):** no tab for one setting; the language list was far too wide. Revised: tabs Audio · Subtitles ·
  Style; the custom fonts folder is the last row of Subtitles (`09`); the language list panel is 440px wide (was 620).
- **Status:** approved by the owner on 4 Nov 2026 (`06`–`09`, language list sized to content) — awaiting go-ahead to code.

## 25. Settings › Library & Home (design)
- **Mock-ups (`docs/design/proposals/settings/10`–`13`), approved pattern:** tabs Library · Details · Home · History.
  Library: hide watched titles, ignore "The"/"A" when sorting, remember each page's view, show every file in folders.
  Details (was metadata/scraping): find details automatically, details language (flag), use .nfo files, save .nfo
  files, make thumbnails for network videos, include adult titles, Export (.nfo files / database) as inline buttons.
  Home: Customise Home… (opens the approved side panel; shows "8 rows · 6 shown"), hero sources as three switches
  (Recently added · Trending · Popular). History: keep viewing history, Clear… (confirmation).
- Moved out (approved): Rescrape → Refresh details in Library Health; "recreate sort titles" joins it there; Clear
  Watch Next lives in Customise Home; collections rescrape removed.
- **Exports (owner, 4 Nov 2026):** today both only show a brief toast; .nfo export runs silently and database export
  copies to a hidden app folder. Decided: keep **Export .nfo files** — confirmation in the side panel ("Write details
  files next to 1,809 videos? Read-only shares such as Put.io are skipped."), progress there ("240 of 1,809"), and a
  result ("1,806 written · 3 skipped (read-only)"); **remove Export database** (Backup & Restore covers it). `11`
  re-rendered.
- **Status:** approved by the owner on 4 Nov 2026 (`10`–`13`) — awaiting go-ahead to code.

## 26. Settings › Where to Watch (design)
- **What "Refresh provider list" did (from the code, `StreamingPreferences`):** clears the cached list and re-downloads
  from TMDb which streaming services exist in the chosen country — which the page already does every time it opens.
- **Mock-ups (`docs/design/proposals/settings/14`–`15`):** one page, no tabs (one section only). Rows: Show where to
  watch (switch), Country (flag + name), Your services (their logos; opens a picker), Show first (preferred service).
  Footer: "Availability from JustWatch via TMDB · service list updated automatically (today)". **Refresh button
  removed.** `15`: the services picker — search field at the top, tick box + logo per service, sized to its content;
  the separate "Search providers" and "Show all providers" rows are folded into it.
- Logos in the mock-ups are stand-ins; the app uses the official single-colour logos approved in 0.153.
- **Status:** approved by the owner on 4 Nov 2026 (`14`–`15`) — awaiting go-ahead to code.

## 27. Settings › Appearance and the screensaver (design)
- **Mock-ups (`docs/design/proposals/settings/16`–`17`):** Appearance is one page (no tabs): Interface language (flag;
  list); Screensaver group — "Supernova screensaver" with its state and **Open Android settings** (Android TV only
  lets the screensaver be chosen in its own Settings › Device Preferences › Screen saver), Shows (Films · TV shows ·
  Both), Change picture every (10 · 20 · 30 s), Show the title and clock (switch). Accent & Colour removed (approved).
  `17`: the screensaver — one library backdrop at a time, slow drift and cross-fade, title and clock small in the
  bottom corners.
- The old Interface size option is already hidden on TV builds and stays out.
- **Status:** approved by the owner on 4 Nov 2026 (`16`–`17`) — awaiting go-ahead to code.

## 28. Settings › Accounts & Services (design)
- **Mock-ups (`docs/design/proposals/settings/18`–`19`):** tabs Services · Trakt. Services: one row per service
  (Trakt, OpenSubtitles, IntroDB) — mark, one-line purpose, status ("Connected as …", "Not signed in", "No account
  needed"), Sign in / Sign out inline, on/off switch (switching off keeps the sign-in); a note points to Playback ›
  Skipping for skip choices and to Network & Files for shares and their passwords. Trakt tab: scrobble while
  watching, sync resume points, sync library to Trakt, Sync now (Get from Trakt / Send to Trakt inline), Remove
  Supernova's data from Trakt… (confirmation).
- Moved out (approved): Network connections page (shares, SFTP trusted servers, VPN preference) → Network & Files;
  IntroDB skip modes → Playback › Skipping.
- Service marks are stand-ins for the services' logos.
- **Where the IntroDB settings are (owner question):** the on/off switch is the IntroDB row in Accounts & Services; the
  per-part choices are in **Playback › Skipping** (`20`): Intro, Recap, Preview, Outro, Credits, each inline
  Play · Skip button · Skip · Smart (Credits: Safe skip instead of Skip). Defaults as today.
- **Status:** approved by the owner on 4 Nov 2026 (`18`–`20`) — awaiting go-ahead to code.

## 29. Settings › System (mock-ups `docs/design/proposals/settings/21`–`25`)
- **Backup** (`21`): Back up now (last backup shown), Save backups to (Supernova's own folder browser), What to include
  inline: Everything (except private items) · Settings only · Custom…; Restore from a backup (shows what will change
  first); What's in a backup (short page replacing the two walls of text).
- **Custom…** (`24`, `25`): side panel in the Playback Settings panel style — no title, section names followed by a rule,
  74 px rows with icon and switch; the list scrolls. **Private** group (Library sources, File bookmarks, File names and
  locations) off by default; switching one on expands the row with what it reveals and Include / Keep off (owner).
- **Diagnostics** (`22`, owner): no Report a problem; logging always records in full detail (no level choice). Rows:
  Save a diagnostic report, Diagnostic logging on/off, Clear diagnostic data. Detail to be cut back for privacy only
  if the app is ever shared.
- **Advanced** (`23`): the four compatibility pages in one, grouped Video · Audio · Other, with one warning line.
- Diagnostic report review of 4 Nov: `docs/0.155/DIAGNOSTICS_2026-11-04.md` — all findings approved for 0.156.
- **Status:** approved by the owner on 4 Nov 2026 (`21`–`25`) — awaiting go-ahead to code.

## 30. Local Storage: Home in the top bar takes focus while a folder loads (owner video, 4 Nov)
- **Seen:** opening a folder that hasn't been opened this session, the Home underline lights up while the
  placeholder rows show, then focus returns to the list.
- **Cause (code):** `LocalStorageView.load()` empties the list and redraws it (`render()`, `notifyDataSetChanged`)
  before anything new can hold focus. Placeholder rows only appear after `LOADING_DELAY_MS`, so for that moment
  nothing inside the page is focusable and Android gives focus to the first thing it finds: Home. It happens again
  when the placeholders are hidden and the real rows arrive (`render()` hides them before `focusAfterRender` runs).
- **Fix (0.156):** keep focus parked inside the page during the swap: an invisible, unhighlighted focus holder in the
  list area takes focus before the list is cleared and before the placeholders are hidden; `focusAfterRender` then
  moves it to the remembered or first row. A Robolectric test opens an uncached folder and checks that focus never
  leaves `LocalStorageView`. Check the other browser (`UniversalFileBrowser.reload()`) for the same gap.
- **Status:** cause found; owner approved fixing in 0.156.

## 31. Local Storage: header jumps when switching All files ↔ Videos only (owner video, 4 Nov)
- **Seen:** for a split second the pills under the folder name (files · folders · size) vanish, the header gets
  shorter and the separator line and list jump up, then everything drops back.
- **Cause (code):** the switch reloads the folder. `LocalStorageView.bindPills()` (`LocalStorageView.java:729`)
  starts with `pills.removeAllViews()`, and while the new listing is not yet in (`!listed`) and the loading delay has
  not passed, it adds nothing (`:739–742`). The pills row is `wrap_content` (`:157`), so an empty row has no height
  and the whole header shrinks.
- **Fix (0.156):** (1) give the pills row a fixed height equal to one pill, so the header never changes height;
  (2) during a reload of the same folder, keep the previous pills on screen until the new counts arrive, then
  replace them in one step (no blank frame). Robolectric test: header height is identical before, during and after a
  filter switch.
- **Status:** cause found; owner approved fixing in 0.156.

## 0.155 on the Shield
- Owner, 4 Nov 2026: signed 0.155 (run 41922310251) installs over 0.154 and works.

## 32. Settings › About Supernova (mock-ups `docs/design/proposals/settings/26`–`29`)
- Tabs: **About · What's new · Licences · Device**, in the approved single-panel pattern.
- **About** (`26`): name, version and date, "based on NOVA Video Player 6.6.4" said once; Credits & acknowledgements
  one row away. App information and Technical Information › Application & Build merged into this.
- **What's new** (`27`): released versions only, written for viewers (from `release-history.json`, reworded).
- **Licences** (`28`): search across all 66, grouped App libraries · Playback engine; no blank "Find in notice" bar.
- **Device** (`29`): device, Android, free space; hardware video formats; library counts and sources. The engineering
  notes ("No new diagnostic viewer…", "Cached count is not authoritative…") are gone.
- **Bug found:** `assets/foundation/credits.json` still says "based on Nova Video Player 6.6.2"; 0.155 uses 6.6.4.
  Fix in 0.156.
- The "S" tile stands in for the app icon.
- **What's new, full history** (`27`, `30`, owner): every version since 0.1 (13 Sep 2026), newest first, grouped by
  month: list on the left, the focused version's changes on the right. 155 versions; 51 with visible changes, the rest
  shown as one quiet "test build · no visible changes" line (runs merged, e.g. 0.46–0.132 = 87 builds). Every note in
  `release-history.json` is rewritten for viewers when coded.
- **Credits** (owner): shown on the About page itself (`26`), under the version — no separate page. Plain-word text
  from `credits.json` (NOVA corrected to 6.6.4), TMDB's required wording kept exactly.
- **One licence opened** (`32`): name, what it does, version, licence, a **QR code** for its website (owner; replaces
  the written address, "Scan for the website"), then the full notice in a scrolling box.
- **QR size** (owner: far too big): now a small square (76 px) in the facts line, "Website [QR] Scan with your phone". QR codes are square by standard; a rectangular version (rMQR) exists but most phone cameras can't read it.
- **Credits for every outside service** (owner): About (`26`) lists TMDB, JustWatch, OMDb, IntroDB and TheIntroDB, ChaptersDB, OpenSubtitles.com, Trakt and YouTube; Archos and NOVA thanked in the line under the version. Full list and backups: `docs/0.155/DATA_PROVIDERS.md`.
- **Status:** awaiting the owner's review.
- **Licence header compacted** (owner): crumb, name with description beside it, version and licence on one line, QR
  code with "Website · Scan with your phone" at the right — about 100 px instead of 240 (`32`).

## 33. Chapters in the player (owner, watching Dark Matter S2E10)
- **Today (code):** chapters come from the file, or from ChaptersDB when the file has none
  (`PlayerActivity.previewChapters`). The HUD shows only the current chapter's name under the scrub timestamp
  (`PlayerController.java:1871–1878`) and small marks on the progress bar (`ChapterMarkers`). There is no chapter list
  and no way to jump to a chapter.
- **Proposal:** a **Chapters** list in Playback Settings (opens inside the side panel like the track lists): each chapter
  with its name and start time, the current one marked, OK jumps there. Only shown when the video has chapters.
  Mock-up to follow.

## 34. Ratings without a personal key (owner question)
- OMDb needs a key; today it is the owner's, typed into Settings. Options: (1) put one key into the app at build time
  (GitHub secret, never in the code), so ratings just work like TMDB — fine while the owner is the only user;
  (2) MDBList, which adds Letterboxd and Trakt scores but also needs a key; (3) Trakt's own score and TMDB's score need
  no extra key but are not IMDb/Rotten Tomatoes/Metacritic. No service gives those three without a key.
- First proposal (Chapters row in the panel's More tab) **rejected by the owner**: chapters belong on or below the
  progress bar.
- **Mock-ups** `docs/design/proposals/chapters/01`–`02` (redone): the progress bar is split into one segment per
  chapter and the time bubble names the chapter; Down from the bar opens a strip of chapter cards below it (name, start
  time, "Playing" on the current one); Left/Right picks a card and its segment of the bar lights up; OK jumps; Up or
  Back closes. Only when the video has chapters; with more than six the strip scrolls.
- **Episodes too** (owner, `03`): Down from the bar opens two tabs, **Chapters · Episodes**. Episodes shows this season's
  episodes with their stills, opened at the one playing ("Playing", progress line); watched ones say Watched; OK plays
  the chosen one. Each tab appears only when it has something (films get Chapters only).
- **Cards like the Home rows** (owner): both tabs use the Home row's landscape card — artwork, title and line over a
  shade, progress bar along the bottom, the focused card grows with the white outline; chapter cards are the same size
  as episode cards and show a **picture taken from the start of each chapter** (made in the background from the file
  while playing; until ready, the episode still). Mock-ups `02`–`03` redone. Awaiting review.
- Licence header spacing loosened (owner: too tight) — `32`.
- **OMDb lookups (owner question):** one lookup each time a Details page opens (`PreviewDetailsData.java:236–240`),
  not per title added; nothing is kept, so reopening the same title looks it up again. Proposal: remember ratings for a
  week per title.
- **TMDB key (owner question):** NOVA's own key, carried over in `aos-MediaLib/res/values/donottranslate.xml`.

## 35. Subtitle Vertical Position (owner, player)
- **Today (code):** three choices Bottom / Middle / Top (`PlaybackSubtitlePanel.java:521`) = values 0 / 128 / 255.
  `SubtitleManager.setVerticalPosition` turns 0–255 into a gap of at most **one third of the screen height**
  (`mScreenHeight * pos / 765`), so "Top" is only a third of the way up and "Middle" a sixth. Default 0 = 1 px above
  the bottom edge of the **screen** (not of the picture).
- **Black bars:** subtitles are placed from the bottom of the screen, not the picture, so on wide films they land in
  the bottom black bar — a consequence of that, not a deliberate choice. Styled (ASS) and picture subtitles (PGS/DVD)
  follow the picture instead.
- **Proposal (mock-ups `docs/design/proposals/subtitle-position/01`–`02`):** one step control,
  "Vertical Position ˅ Raised 30% ˄": Right raises a step (5% of the screen height), Left lowers, from Bottom up to the
  top of the screen; the subtitle moves live while you press. Default a little above the edge (5%, inside the TV's safe
  area and still inside the black bar on wide films). The same control in Settings › Audio & Subtitles. Existing saved
  values are converted so nobody's subtitles jump.
- **Status:** awaiting review.
- **Licence page approved** (owner, 4 Nov) after the QR code was centred on the title and facts lines (`32`).
- **OMDb ratings are not saved (confirmed):** TMDB details are kept on the device (`PreviewDetailsData.cached`), but
  the OMDb ratings are not part of that — they are fetched again on every Details visit. **Fix (0.156):** save them with
  the other details and refresh at most once a week per title.
- **Subtitle position live preview (owner):** today the setting already moves the real subtitles
  (`SubtitleManager.setVerticalPosition`). New: if a subtitle is on screen, it moves; if none is showing, a sample line
  appears while the row is focused and disappears when you leave it. The value text follows the position
  ("Bottom", "Raised 5%" … "Raised 90%", "Top").
- **Preview rule (owner, replaces the above):** while focus is anywhere in **Subtitle appearance** (Text Size, Text
  Colour, Vertical Position, Outline, Background, Background Opacity), the real subtitles are hidden and one sample
  line is shown instead, drawn with the current settings and updating on every change. Leaving the section (or closing
  the panel) removes the sample and brings the real subtitles back. Playback carries on underneath. Subtitle Delay is
  not part of the rule (it needs the real subtitles). If subtitles are off, the sample still shows, so the look can be
  set in advance.

## 36. Picture in picture (owner, first use)
- **Getting back to full screen:** Supernova adds no buttons to the PiP window (no `setActions`), so the only way back is
  Android TV's own PiP menu (press and hold Home on the Shield remote, then Full screen). **Fix (0.156):** OK/Select on
  the remote while the PiP window is focused, and opening Supernova again, both return to full screen; add Play/Pause
  and Full screen buttons to the PiP menu (`PictureInPictureParams.setActions`).
- **Subtitles fill the PiP window:** text size is worked out from the screen's short side
  (`SubtitleManager.getScreenShortSide`, "pt x short side / 720"); in PiP only the vertical position is rescaled
  (`PlayerActivity.java:1634–1638`), so the text keeps its full-screen size inside a small window. **Fix:** scale the
  text to the PiP window — **owner decision: turn subtitles off in PiP** (unreadable on a 52" screen); they come back
  when returning to full screen.

## 37. Edit Page panel (owner photos, 4 Nov)
- **Save Changes loses its tick on focus — cause found:** `EditPagePanel.java:87–91` styles the button
  (`SupernovaStyle.applyButton`, which makes icons turn dark on focus) **before** adding the tick
  (`PreviewIcon.apply`), so the tick never gets that treatment and stays white on the white focus box. It is the only
  place in the code with that order. **Fix (0.156):** make `PreviewIcon.apply` itself use the focus-aware icon, so the
  order can never matter again anywhere.
- **No scrolling** (mock-up `docs/design/proposals/edit-page/01`): rows 74 → 56 px and tighter group headings; all 13
  options including Order and Delete Page fit; panel sized to its content.
- **Year range** (`02`–`03`): the Year row shows the range ("2003 – 2006", or Any) and opens inside the panel:
  From and To (Left/Right through the years in the library) and quick choices (Any year, This year, Last 5 years,
  2020s, 2010s, 2000s, 1990s, Before 1990). Back returns.
- **Owner:** Edit Page changes approved; header (title, match count, Save Changes) now separated from the options by a
  line, and Delete Page sits at the bottom behind its own line (`01`–`03` updated).
- **Status:** approved by the owner on 4 Nov 2026 — awaiting go-ahead to code.
- Delete Page centred between its line and the panel's bottom edge (`01`).

## 38. Playback controls: remote navigation, and the first-play tip
- **Today (code, `PlayerController.java:2838–2854`):** the bar has focus; Left/Right seek. **Up** from the bar goes to
  Settings, **Down** from the bar goes to Play/Pause, Up from Play/Pause comes back to the bar. Left/Right can't move
  between the three because on the bar they seek.
- **The blue box on first play is NOVA's** (`PlayerController.java:885–898`, `res/layout/player_help_overlay.xml`):
  "Press the UP arrow key to display the TV menu", shown once on a TV. It is wrong for Supernova (Up goes to Settings,
  not a TV menu). Remove it.
- **Proposal:**
  - Bar: Left/Right seek; **OK plays/pauses**; **Up** → the buttons (lands on Settings; Left/Right moves between
    Play/Pause and Settings; Down returns to the bar); **Down** → Chapters · Episodes.
  - Strip: Left/Right moves along the cards; Up → the Chapters/Episodes tabs (Left/Right switches); Up again → the bar;
    OK jumps/plays; Back closes the strip.
  - With no chapters and no episodes, Down does nothing.
- **Our own first-play tip** (replaces NOVA's): a small charcoal card on the first video only, three lines with remote
  arrows — "Left/Right seek · OK pause", "Up for Settings", "Down for Chapters and Episodes" — gone after 8 seconds or
  any press. Mock-up once the scheme is agreed.
- **Owner:** scheme agreed in general; the controls keep today's height and position. Chapters and Episodes show as
  two small buttons under the bar; the rows only appear when focus moves down to them, and the controls rise to make
  room. Mock-ups `docs/design/proposals/chapters/01`–`05`: 01 resting, 02 Down to Chapters (rows slide in),
  03 a chapter card focused, 04 Episodes, 05 the first-play tip. Awaiting review.
- **Edit Page approved** (owner, 4 Nov) including the centred Delete Page.
