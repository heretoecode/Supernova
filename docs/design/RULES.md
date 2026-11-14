# Supernova design rules

**The one place for every agreed design rule** (started 12 Nov 2026 at the owner's request). When the owner says
"redesign this page", the page is built from these rules first; the owner is only asked about what they do not
settle. Each rule names where it was decided and the mock-up that shows it. Where an older document disagrees, this
file wins; where this file and the owner disagree, the owner wins and this file is corrected the same day.

How to use it:
1. Read the whole file before drawing a page. Check every element on the page against its section.
2. If a rule nearly fits but the page is different, follow the rule and say so in the message to the owner.
3. Only ask the owner about things no rule covers. Every answer becomes a rule here (or changes one).
4. Rules are tweaked, added and removed as pages are designed. Note the date and the reason; move replaced rules to
   "Replaced rules" at the bottom so nobody revives them.

Sizes are at 1080p. Sources: `docs/0.157/OWNER_NOTES.md` (§n), `docs/0.157/REVIEW_0156.md` (P-numbers and ideas),
`docs/REVISIT_LATER.md` (Files v1–v13), `CLAUDE.md` "Global look".

---

## 1. Pages and background
- **Every page is plain charcoal `#16181C`**, content straight on it, using the full width. No Space Black artwork, no
  page panels (owner, 7 Nov; §18). Pages change over one by one, after each is reviewed on charcoal.
- No page title on a top-level page: the top bar already says where you are.
- TV safe margins: about 56 px at the sides, 54 px at the bottom.
- **Typing happens on a page,** not in a side panel: fields on one side, the keyboard on the page, help under it
  (Files v6, Search, Fix match).
- Flat-page sizes (Files): rows about 128 px tall, names 36 px, second lines 27 px, buttons 64 px. Denser lists
  (Search results) may use about 96 px rows with 32 px names and 24 px second lines.

## 2. Top bar
- Left to right: SUPERNOVA, Home · Movies · TV Shows · library pages; then on the right **Files**, the search icon,
  the settings icon and the clock (Files v13; owner, 12 Nov: Files stays on the right). Places are words, tools are
  icons; **no dots** between them; no line under the bar. (The `tab-style` mock-ups drew Files on the left by mistake;
  only their focus look was approved.)
- **One signal slot on the left of "Files"** (owner, 12 Nov), showing one thing at a time: a small spinning ring while
  a scan runs (the same size and line weight as the icons); otherwise a small white dot when the library needs
  attention; otherwise nothing. Nothing shifts when it changes. Not a notification bar.
- Home + Left stays on Home; the clock is not focusable.
- The top bar follows the focus-and-path rule (section 3): it switches page after a short pause.

## 3. Focus and "where am I" (the most important rule)
One brightness scale that follows your path, everywhere, top bar and tabs included (§25; mock-ups
`tab-style/08`–`15`):
- **White box with black text and icon = where the remote is.** Fill `#F4F6FB`, ink `#0B0D12`. Nothing else on
  screen is a box at rest: anything that would look like a half-focused box (a grey button, a chip behind a tab) is
  plain until focused (e.g. Search's Clear history, §26).
- **Bright white underline = the step you just came from** (e.g. the open tab while you are in its rows).
- **Dull underline = everything else on your path,** including the open tab ahead of the remote.
- **Path underlines are only for tabs and the top bar.** A button that opened a menu goes back to its resting look;
  the menu beside it shows where you came from (owner, 12 Nov).
- **Artwork does not get a box:** it grows and gets a solid, thick white ring, with room around it so the grown card
  never crowds a line or heading. **Hero cards** show focus on their first button instead.
- **Exactly one focus on screen.** E.g. on Search results the remote is on a result, so the keyboard shows no focus.
- Inside a focused row everything turns to dark ink: labels and icons black; the chosen chip black with white text;
  switches: on = dark track, white knob; off = light grey track, dark knob; colour dots keep their colours, the chosen
  one gets a black ring (§5, §6).
- Focus never disappears and never jumps somewhere unexpected: holding Down must not lose it; a hidden view must
  never take it (diagnostics, NOVA's hidden grid); after an action it stays on the item you pressed.
- Mock-ups showing focus are checked by the owner reading them **without captions**.
- **No NOVA screen may run hidden behind a Supernova screen** (owner, 12 Nov). Hidden NOVA screens steal focus (the
  hidden grid; the search screen behind Fix match). When coding starts: one sweep finds them all and removes them,
  each with a build and tests; anything not obviously dead goes to the owner as a list first.

## 4. Tabs
- Plain words on one line, each with its own icon (Details tabs, Files tabs; §18), under the top bar.
- Focus and path as section 3: focused tab = white box; open tab while you are below = bright underline; open tab
  ahead of the remote = dull underline. The underline never overlaps a separator line.
- **Moving along tabs switches the page** straight away.
- **Down from a tab goes to that tab's first row.** Memory is kept where you jump away directly (closing and
  reopening a panel, the top bar and back), and a row that opens its own list gets focus back on return (P12).
- Tab titles **never pop in**: the full set is drawn with the page; content fills in inside with "Loading…", then a
  "none found" line (§12). Empty tabs on Details hide (the rest close up).
- A count badge on a tab when something there needs a look (Files).
- The number of tabs follows the content; nothing on the right of the tab row limits it (§18).

## 5. Sections, headings, lines and gaps
- **Every section heading has an icon**, then the heading in white, 32 px, medium weight (§26).
- **A heading sits closer to its own content than to what is above it:** about 26 px below, 64 px above. Sections
  are separated by that gap, **never by a line**.
- **Lines only between rows you can select**, never under the last row. Exception (owner, 12 Nov): long read-only lists
  of facts (the Details tab) get faint row lines, no column lines. Plain information (tips, read-only facts)
  has no lines (§26). Lines are faint (Search and Files: about 8% white on charcoal).
- On Details' read-only columns, column dividers and row separators are visible but quiet (see Open questions).
- Headings line up with everything else; a focused heading never sticks out to the left (§1 round 5).

## 6. Text and wording
- **British English, plain words.** Short labels; reword rather than wrap.
- **Label / value pairs:** labels soft near-white `#C9D3E6`, values pure white and a little bolder. Difference by
  weight, not dimness. Second lines also `#C9D3E6`.
- **Missing information says "Unknown"** (quieter than real values); a row is never removed (§1).
- **No blue for text or values** anywhere (§6). Single values (steppers, lists) are plain white.
- Dates: day month year, e.g. "12 Nov 2026"; with a time "5 Nov · 14:40". Say "local time", never a place or zone.
- Dots (·) separate facts in a line of text, never places or buttons.
- **Nothing vanishes from the middle of a line** (owner, 13 Nov): anything temporary gets a line of its own and the
  whole line fades, so nothing slides to close a gap.
- **Standard wording (use it everywhere the same thing appears):**
  - Play / "Resume S1 E1 · 16 min left" (Details hero, §3). Short form on pills: "Resume S5 E3" (§26).
  - "Watched", "Unwatched" for state.
  - **Movies and TV Shows, as in the top bar** (owner, 12 Nov): never "film", "series" or "TV series" in labels, pills
    or headings (e.g. pills "Movie" / "TV show", folders "Movies" / "TV Shows").
  - "Fix match" (not "Report Incorrect Match"), "Clear history", "Show all …".
  - Streaming: "Stream more from the world of X" (fiction), "Stream more related to X" (non-fiction), "Stream more
    from <person>" (§26). Never "similar" in Search.
  - Empty states say what is going on: "Everything looks fine", "Nothing in your library matches …".
- Typed letters show in bold inside matching results (Search).

## 7. Rows
- One row style everywhere: icon (or thumbnail), name, plain second line; long names end in "…".
- **Status pills** sit in a fixed column between the names and the buttons: plain text in a thin outline, no colour,
  all the same width, and they stay put when the row is focused (Files v4). A pill may carry an icon (Search §26:
  ▶ Resume, ✓ Watched, ○ Unwatched; the open-in-app icon for a service).
- **Experimental label** (owner, 13 Nov): a large new feature still being proven gets a small outlined "Experimental"
  label right after its name (Seek previews); removed once it has proven itself.
- A pill tells you something the heading doesn't (no "In library" under "In your library").
- The left of a row is what it is; the right is what OK does (e.g. the download icon on a subtitle, shown on the
  focused row only).
- The focused row shows its own buttons (up to about five); the remote's Menu button opens the same actions as a menu.
- **Everything in a row sits on the row's centre line** (owner, 13 Nov): label, value, chip, switch, arrow, tick — in
  every row, menu and panel (§20).
- Instant actions with an **Undo** notice instead of "are you sure?" where the action can be undone. Real deletions:
  one clear warning in Supernova's own design (§23.5).

## 7b. Pickers, warnings and options
- **One folder picker for the whole app** (Files browser in picking mode): the heading states the task ("Choose a
  folder for Movies"), one breadcrumb shows the location; "Current" marks the folder in use; the focused folder offers
  Use this folder · Open; New folder at the bottom (§28).
- **Warnings:** left-aligned (heading, name, text, options, buttons); the safe choice has focus first ("Keep file").
- **Offer real options, not just yes/no** (owner, 12 Nov): e.g. Delete has switches for Supernova history and Trakt
  history, off by default.
- **Badges and counts agree:** a tab's badge, its summary fact and its rows use the same number and words (§28).
- **Tab row facts:** the right of a tab row shows the open tab's facts, dot-separated; on Files it ends with the scan
  control. No line between tabs and facts; a guaranteed gap instead.
- The first heading under a tab row sits about 48 px below the line, so it reads as the page, not part of the tabs.
  A heading never repeats the tab's own name.

## 8. Choices and settings
- **One setting is one row.** Up/Down moves between settings; Left/Right goes through its choices. Chips never wrap
  and a setting is never split across two rows (REVIEW_0156 ideas).
- **Up to five choices → inline chips on the row.** The chosen one sits in a small rounded chip (light grey at rest;
  black with white text on a focused row). No separators between chips (§6).
- **On/off → a switch.**
- More than five → **reduce or reword** the options first (freedom to rethink, CLAUDE.md); only then the shared
  choice list (section 9).
- Prefer clock times and real values to abstract ones (Sleep timer → "Stop playing at": Off · 17:32 · 18:02 · 18:32 ·
  End of episode).
- **› only on rows that open a list or page** (owner, 14 Nov). Actions show their verb in plain text (Clear, Send, Save,
  Back up); their progress and "Done" show in the row itself, never as an Android notification.
- Avoid click-throughs and pop-ups wherever an inline choice fits. No "‹ value ›" arrows where chips fit.
- Long explanations are short help lines, never full-page blue boxes of text (§20 Backup).

## 9. The shared choice list (long lists: languages, countries, encodings, services)
- One design for every long list (§20): flag or mark, name, then pills where useful (e.g. "Original", "SDH").
- **The chosen item has the white tick on the right** — never a radio circle, never a blue tick.
- **No Cancel / Done buttons:** Back closes. A choice takes effect at once (a chosen service moves up into
  "Selected" straight away, animated).
- Countries get flags; languages use flags only for real locales, otherwise a neutral globe.
- Codes get plain words (subtitle encodings: no bare "CP1252").
- **Used for every long list** (owner, 13 Nov; mock-ups `choice-list/01`–`04`): interface, audio, subtitle, download and
  Details languages; Country; Your services; subtitle encodings; the player's Subtitle track and Audio track lists (with
  their approved name + pills rows). Short action menus (More, long-press, Sort) stay menus.
- **Width:** the longest item in the whole list plus the standard padding (min 360 px, max 40% of the screen, "…").
- **One choice:** OK ticks and closes. **Several:** OK ticks / unticks, the list stays open; ticked items sit under
  **Selected, A–Z**; a ticked item glides up into its place (about ¼ s), an unticked one glides back; focus stays where
  you were (next row).
- **Search appears automatically when a choice list has more than 12 items** (grouped menus such as Sort excepted) (more than one screen), at the top, worded for
  the list ("Search countries", "Search services"); a list that grows past 12 (services come and go) gains it, one that
  shrinks loses it. OK opens the shared keyboard in its own matching panel beside the list; results filter live.
- **No title or back header** on a list opened from a row (the row already named it): no "‹ Subtitle Track" header;
  Back returns (fixes 0.156 P6, the odd focus round that header).

## 10. Menus (pop-up and context menus)
- **Every item has its own icon; no two alike in one menu**; opposites get mirrored icons (A–Z / Z–A) (§15).
- **The current choice is a tick on the right.**
- **Only as wide as the longest item plus the standard padding** (24 px each side), never a fixed width.
- **Group headings in menus and side panels** (More menu, long-press, Playback Settings, every one): the words
  **centred on a thin line** ("——— Files & Media ———"), **Title Case** (each word capitalised), a little larger than the
  rows' second lines (28 px). Pages use icon headings and gaps (owner, 12 Nov).
- **A single action at the foot of a panel (Delete row, Delete page, Clear filters) has no heading:** a plain line
  sets it apart (owner, 13 Nov).
- No menu title; a single list has no heading. **Anything opened from a named item has no title** (menus, side panels
  such as Artwork); warnings and the Full description keep their heading because it is content. Two or more groups: each group has a heading followed by a thin line
  on the same line ("Library ———").
- **Opens beside the button or item that opened it** (drops down under a toolbar button), never in a corner (§19; owner,
  13 Nov).
- Never offer what does nothing or makes no sense (no Restart on an unstarted film; no "More info" when OK already
  opens it) (§4, §19).

## 11. Panels and pop-ups
- Pop-ups, menus and side panels: `#16181C` at **95%** opacity (owner, 13 Nov; was 90%, too see-through over posters),
  no outline, soft shadow; one shared value (§4).
  **Over a page** (flat charcoal) every menu and panel gets a faint edge so it separates; **over video** none
  (Playback Settings) (Files v8; owner, 13 Nov).
- Focused item: filled white, black text and icon (section 3).
- **A list opened from a panel sits on top of it** (owner, 13 Nov; replaces "never stack", 0.156 P5): the panel behind
  is darkened and pushed back slightly (about 45% brightness, 96% size) with the row you came from still visible; the
  list in front is offset a little down and left, with the faint edge and a deeper shadow; no title or back header;
  Back closes it and returns to that row. Mock-up `player/28` (bottom).
- Panels are sized to their content. **Every side panel floats** (owner, 13 Nov): rounded corners, a gap from the
  screen edges, sized to its content and scrolling inside, like Playback Settings (Customise page, Artwork, …).

## 12. Buttons
- Rest: plain (no grey box that could read as focus) — on artwork, a dark box at about 72% so text stays readable.
  Focused: the white box. Disabled: the rest look at 40%.
- **Size never changes** between states. One shape: rounded rectangle, 12 dp corners. Buttons fit their content.
- Icons are one size and invert with the text on focus.
- **Streaming button:** the service's name with the **open-in-app icon** (a box with an arrow leaving it); OK opens
  the title in that app (deep link where possible) (BUTTONS doc; §3). The same icon on every streaming pill.

## 13. Icons and logos
- Line icons, no background behind them (no filled circles) (§1 round 4, §19).
- Service logos in **full colour**, app-wide, no background (§23.9).
- Reception: icons only (IMDb, Rotten Tomatoes, Metacritic, TMDB, the awards trophy), no written names.

## 14. Artwork, cards and heroes
- **Every piece of artwork has one thin white line on its edge (about 60%) with a soft inner shade** — heroes, row
  cards, Details tab cards, the player strip, library posters, Search thumbnails (§18 v17).
- Focused artwork grows and gets the solid thick white ring (section 3). **Library posters:** the poster and its two
  lines underneath are one card; the whole card grows and the ring goes round both (owner, 13 Nov).
- **One row card size:** 16:9, 400×225 (focused 440×248), 24 px gap, a sliver of the next card at the edge (§9).
  Title and details inside the picture on a shade; no blue caption bars. Library posters keep one portrait size on
  every library page, with a plain white title and grey line underneath.
- **Heroes:** Home and Details the same size and place, 1776×820, the next row or tabs peeking below; Home has
  carousel dots. Going from a Home hero into Details the card stays exactly where it is (REVIEW_0156 Home).
- Hero text: **no tagline anywhere** (owner, 13 Nov); description **two lines** on Home and Details; More › Full description opens a pop-up sized to the
  text, mirroring the hero: title, title line (seasons/episodes), then the text; no icon.
- **Hero buttons sit in a fixed place at the bottom left** of the hero, whatever the text length (owner, 12 Nov).
- **Artwork picker by place:** Hero and rows · Library page (one backdrop for hero and rows; posters for library
  pages); no title; a side panel sliding in from the right, the hero previewing the picture you are on.
- **Home row cards: caption box under the 16:9 picture** (title, one line, inset progress bar), as library posters
  (owner, 13 Nov); nothing printed over the artwork. The player strip keeps its text inside.
- Service titles in a row: centred colour logo, title · year on one line; no play icon on library items.

## 14b. Details pages (all four kinds)
- **Tabs in one master order, shown or hidden, never moved:** Details · Episodes · Extras · More Like This (§29).
  Movies have no Episodes tab. The page opens on its most useful tab.
- **Metadata row, one order everywhere: Age rating (outlined badge) · Year · Length · Genres** (TMDB order, three at
  most). Missing items are left out of the row. Seasons and episodes add a **title line** under the logo
  ("Season 2 · Episode 8 · Title"), the same as the player.
- General principle: a **master list** for any set of contents (tabs, metadata, menus): fixed order, items hidden when
  they don't apply, never reordered per page.

## 15. The keyboard (one keyboard for the whole app)
- **There is one keyboard** (`PreviewKeyboard`), used by every text box: Search, Fix match, connection forms,
  subtitles search, everything. Any change or fix is made once, in it, and applies everywhere (owner, 12 Nov). No
  screen builds its own keyboard or its own keyboard focus handling.
- Layout: number row always shown; lower-case QWERTY; `/` after L; `. @` after M; Shift; `#+=` opens the symbols
  page (ABC returns); bottom row `#+=` · Voice · Space · Delete · Clear. Forms add Done · Cancel; Search does not.
- **Focus starts on T** (the most common first letter of film and series titles).
- **Focus stays on the key you pressed,** however fast you type. Results arriving or the page refreshing never take
  it away (Fix match bug, §27).
- Shown on the page, not in a side panel. **Opened from a menu or side panel** (e.g. a choice list's search), it sits
  beside that panel, never covering it, in a panel of its own with the same background (owner, 13 Nov). On Search the field and keyboard are on the left, results on the right; the
  keyboard stays visible.
- Typing is forgiving wherever it searches: capitals, punctuation and a leading "The" ignored, one-letter typos,
  original titles, TMDB / IMDb / TVDB / Trakt numbers (Files v11).

## 16. Moving around (remote behaviour)
- Back from the middle of a long page first jumps to the top, then to the top bar (§23.3).
- After Android ends the app it reopens on the same page with focus restored; a first launch after a reboot opens
  Home (§23.2).
- Read-only pages (Details tab) have nothing focusable: Down/Up scroll, one more Up returns to the tabs (§1).
- **Moving things (one gesture app-wide):** OK picks the item up (it lifts, with ⌃⌄), Up/Down moves it, the page behind
  follows live, OK or Back puts it down; focus is locked to the item meanwhile (Home rows, list-view columns) (§19).
- Rows a left arrow appears beside: move the whole row right to make room; never just the first card (§19).

## 17. Motion
- Smooth, never popping: tabs drawn at once (§12); the hero changes by cross-fade with artwork pre-loaded (§11); the
  title logo glides to the compact logo going into the tabs (§1); player controls linger about 3 s after resuming
  (P9); the HUD shade is a soft full-width gradient (P13).
- Follows the system "remove animations" setting.

## 18. Streaming and the library
- **The library is the key:** Search shows streaming titles only when something in the library matches, as related
  titles under it (§18).
- **Where to Watch off removes every streaming feature** (buttons, pills, More Like This "On your services", Search
  streaming results, the streaming tip); Trending and Popular keep their own switches (§18).
- A title with no file opens its service's app, never a Supernova page (§3).

## 19. Player
- **Title logo:** wherever a title is shown large (Home and Details heroes, the compact Details logo, Preparing playback,
  the player) it is the title's **official logo artwork**; when a title has none, its name in plain text. Mock-ups draw
  the name as a stand-in.
- Title block in one fixed place, the same on Preparing playback and the HUD; the age rating alone top left for the
  first 6–8 s; logo and episode line appear with it, then fade together (REVIEW_0156, §23.11).
- Track names in plain words plus pills (`track-names/01`, `02`).
- **One loading ring for the player, in the centre of the screen** (the app's ring, as on Preparing playback and the
  Files scan) whenever playback waits (after a seek, slow network). The time box only ever shows the time over the
  chapter name; the seek preview picture uses exactly that layout inside it (owner, 12 Nov; replaces §14 A).

---

## Open questions (to settle when the page comes up)
- **Home row text:** inside the picture (current rule) or below it — with the Home redesign.
- **Written breadcrumb** under the top bar on deeper pages ("Movies › Marshals") — an option alongside the path rule.
- Settled 12–13 Nov: Details headings (icons, no underline) and Details lines (faint row lines, no column lines).

## Replaced rules (do not use)
- Navy glass at 97% with a light outline for panels → 90% `#16181C`, no outline (5 Nov).
- White outline as row focus; blue glow focus → filled white box (5 Nov).
- Open tab as a grey chip; plain-underline tabs → the focus-and-path rule (12 Nov).
- Card edge 1.5 px at 28%; "focus = grows only" → 60% edge with inner shade; focus = grow + thick ring (7 and 12 Nov).
- Space Black behind full pages → plain charcoal (7 Nov).
- Blue values; blue open tab; dark-blue chosen value on focused rows → chips, plain white values (5 Nov).
- Monochrome provider logos in tiles → full-colour logos, no background (12 Nov).
- Section separator lines → gaps (12 Nov).
- "Clear" (beside Recent searches), "In library" pill, two-line "On your services" headings (12 Nov).
- The Fix match side panel → one Fix match page on Files › Library Care (12 Nov).
