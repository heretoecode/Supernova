# Codex brief — 0.157 part C: everything agreed up to 8 Nov 2026

One brief for all remaining 0.157 non-visual work, so you can work straight through. Same rules as `BRIEF_0157.md`:
branch `codex/0157-2026-11-06`, merge the latest `claude/supernova-github-access-yfhe89` first, non-visual work only
(no screen/resource/Gradle/`.github/` edits), API 23, Robolectric for org.json, readable code
(`tools/test_code_layout.py`). Sources: `docs/0.157/OWNER_NOTES.md` (§16–§19 and the 7–8 Nov entries) and
`docs/0.157/REVIEW_0156.md`. Report in the same 0.157 report file; list every API for Claude in
`findings/0157-ui-hooks.md`. Work in the order below; finish an item (with tests) before the next.

## A. Search (owner decisions 7–8 Nov)
1. **Library first.** Search matches the library (titles, people in credits, genres, years). This is the base result.
2. **"The library is the key" for streaming results.** If nothing in the library matches, return **no** streaming
   results at all. If library titles match, also return **related titles on the owner's services**:
   - title match → its **family**: films = the same TMDB collection (Terminator, Dune, Resident Evil); series =
     sequels, prequels and spin-offs (The Handmaid's Tale → The Testaments; Yellowstone → 1883, 1923, Marshals; Dexter:
     Resurrection → Dexter, New Blood, Original Sin). Series have no official TMDB collection: derive the family from
     spin-off links, shared name and creator plus shared characters/universe, best effort, with a confidence
     threshold. **Same creator alone is not family** (Mayor of Kingstown is not Yellowstone family).
   - person match (e.g. Taylor Sheridan) with at least one of their titles in the library → their **other work** on
     the owner's services.
   - **Never "similar"** (Slow Horses must not bring in The Night Manager; that is More Like This on Details).
3. Group the output: library titles (each title separately; a person match lists each matching library title with
   the reason, e.g. "Taylor Sheridan · creator"), then one family group per owned title, then one person group. Each
   streaming item carries its service(s). Worker-only, cancellable per keystroke, cached.
4. **Recent searches:** store, read and **clear** (one call), for the Clear button on the page and an option in
   Settings.

## B. Where to Watch master switch (decided 8 Nov)
5. When Where to Watch is off, every streaming producer returns nothing: service buttons/pills data on Details,
   More Like This "On your services", Search family/person streaming results, and a flag so the streaming line in
   "What you can search" hides. Availability checks stop. **Home Trending and Popular are not affected** (own switches).

## C. Details data (approved v17 design)
6. **Technical information**, one source per title/episode file: resolution, video codec and bit depth, HDR type,
   frame rate, bitrate, audio format and channels, audio languages, subtitle languages, container, size (and per
   episode average for series), source (connection and folder), date added. "Unknown" (honest) when it cannot tell.
7. **Reception** complete and stored offline: IMDb (rating and votes), Rotten Tomatoes, Metacritic, TMDB (rating and
   votes), Awards as plain text (e.g. "2 wins · 5 nominations"). Each "Unknown" when absent.
8. **Hero hand-over:** Details shows exactly the picture Home's hero was showing (pass its identity); it must not swap
   to a different default backdrop when full details arrive.

## D. Library long-press › Fix match (owner, 8 Nov)
9. An API to **mark a library title as wrongly matched** (film or whole series) and open it in the Library Care
   Fix match flow (same flow and wording as Files), pre-selected. The mark clears when a new match is applied.

## E. Carried over
10. From part B: item 9 (Put.io pause test — needs the owner's diagnostics report; do it when it arrives), item 10
    (the real cause of the Marshals production company; seek-loading acceptance once Claude draws the spinner).
    Picture-in-picture removal stays Claude's (screen files), using your removal map.
11. **Last, only after everything above:** the newer NOVA engine (BRIEF_0157 item 21) on a **separate** branch
    `codex/native-next`, as groundwork for a later version. Never mix it into `codex/0157-2026-11-06`.

Not for you: anything visual (row arrow spacing, Hide/Move buttons, move-mode focus lock, long-press menu position,
Settings regrouping, the artwork edge, Search and Details screens). Legacy clean-out removal waits for the owner's
approval of the SAFE list.
