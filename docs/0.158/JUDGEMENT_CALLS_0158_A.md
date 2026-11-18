# 0.158 part A: judgement calls

Made while working unattended (CLAUDE.md "Working unattended"). Each can be changed; the reason is given so you can
decide quickly.

## Branches and version
1. **Working branch.** The brief says to merge `claude/design-planning-discussion-9poejm` into the working branch.
   It lacked the 0.157.2 commits, so the working branch (`claude/supernova-github-access-yfhe89`) was merged too; one
   conflict in `docs/0.157/REVIEW_0157_1.md` kept both sides. Work was done on the session branch
   `claude/0158-part-a-signed-apk-woiqj6` and the working branch brought up to it for builds and signing, because
   signing only runs from the working branch.
2. **Workflows.** The 0.157 conformance and signing workflows were pointed at 0.158 (1580), as was done for
   0.157.1 and 0.157.2, rather than copied to new 0158 files.
3. **What's new.** The history list must run 0.1 … 0.157 with no gaps, so the 0.157.1 and 0.157.2 notes now sit at the
   top of the 0.157 entry; 0.158 has its own notes.

## Top bar
4. **Open on focus after a quarter of a second.** Media pages open when focus rests on them for 250 ms, so moving
   along the bar (say from Settings to Home) doesn't open every page passed on the way. Only a move along the bar
   opens a page; Up from a page onto its own name never reopens it.
5. **The + for your page stays an icon** until the page is set up (RULES §23 says so); every other item is a word.
6. **Centred on the letters:** each word and the clock is drawn so the middle of its capital letters sits on the
   middle of its box.
7. **Grey box colours:** white at 15% for the box, the word at 72% white, matching mock-up `08`.

## One side margin
8. **The top bar's own padding is the margin** (about 52 px at 1080p), shared through `TvLayout.sideMargin`; Files
   moved in from 96 px to it, Search and the library pages from 56 px.

## Library pages
9. **Toolbar words.** "Sort: Title" (sheet) with the icons from the mock-ups; no ▾ after the words, as mock-ups 08
   and 27 show none.
10. **Seven posters a row** on every screen size (sheet), with equal gaps so the first and last line up with the
    margins.
11. **Films not started** show "2023 · 1 h 24 min" on line two (the mock-ups), since the sheet only defines line two
    for shows and started films.
12. **Line three** follows the sort: "Added 2 Nov 2026", the release date, sizes, "1.0 GB per episode", "Modified …";
    nothing for Title, Trending and Popular. Unknown values say "Unknown". On mixed pages a film under "Size per
    episode" shows its file size (mock-up 27).
13. **Order words for sorts the sheet doesn't list:** Resolution "Highest / Lowest first"; HDR, Audio, Codec,
    Container and Path "A → Z / Z → A".
14. **Counts in menu rows** are written after the name ("Unwatched · 35 titles") because the shared menu has one line
    per row; mock-up 23 puts them on a second line.
15. **Year filter: From and To** step with Left / Right through the years that hold titles (the sheet), shown as
    "From · 1994" rather than "− 1994 +"; OK also opens the list of years.
16. **Genre and service lists** tick in place; the "Selected" group gliding to the top (RULES §9) is not used in these
    two lists.
17. **Columns: hiding a column.** OK on a shown column picks it up; moving it down past the last shown column hides
    it again (the move gesture does both). OK on a hidden column shows it at the end. Title isn't listed (always
    shown).
18. **Column headings are not a stop for the remote** (plain words, the sorted one bold with ⌄ / ⌃), so Down goes from
    the toolbar straight to the first title, as the sheet's action table says.
19. **Customise page "Show"** is the same Watched filter as in Filters (one meaning everywhere), and View, Sort by and
    Order there are the toolbar's own settings, so the page opens as you last left it.
20. **Your page saves as you change it** (no Save): the first change in Customise page creates the page. Delete page
    returns the page to "Make this page your own".
21. **TV show Fix match** opens the Fix match page with the show's first episode (the page matches the show from it).
22. **Mark show as watched** marks every episode season by season so Trakt hears of it, as the Details page does.
23. **The delete warning** names the safe choice and has it focused first: "Keep file" / "Delete file", "Keep files" /
    "Delete all episode files", "Keep page" / "Delete page", "Keep row" / "Delete row".
24. **Empty pages:** "No films yet" (sheet wording) and "Finding your films" / "Finding your TV shows" while the first
    scan runs.
25. **Undo notice:** a small line at the foot of the screen with Undo focused, gone after six seconds.

## Home
26. **Move and Hide** are always in the heading but invisible until focus reaches them (so Up from a row's first card
    and Down from the row above land on Move, as the sheet says).
27. **Your Rows in Customise Home** carry a switch as in mock-up `03`: OK opens the row's settings, Left / Right turns
    it on or off.
28. **Icon picker "Automatic"** uses the sparkle icon, as mock-up `07` shows.
33. **Moving rows in Customise Home** starts with a long press of OK, because OK already opens a row's settings or
    flips its switch. Your rows move among your rows, built-in rows among built-in rows.
34. **Toolbar buttons:** the shared toolbar boxes its buttons; on library pages they keep their own icons and are
    plain until focused (RULES §12). Order uses mirrored up / down arrows.
29. **Row settings: Years** offers the quick choices (any year, this year, last five years, decades); no own range.
30. **Customise Home reopens on the row you left** (kept from 0.157's focus audit).

## Search
31. **Placeholder** "Movies, TV shows, people…" (RULES §6: never "films" or "series" in labels) instead of the mock-up's
    "Films, series, people…".
32. **One "In your library" heading** above every library result (films, shows, episodes and people together, in
    that order); the old filter chips are gone.
