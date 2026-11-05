# 0.157 — owner notes (from 5 Nov 2026)

Same rules as `docs/0.155/OWNER_NOTES.md`: each point is written down, mocked up, approved, then coded. Nothing here is
approved yet.

## 1. Details page — review (owner photos of Marshals, 5 Nov 2026, 10:03–10:12)
The owner started with Details: "the biggest part of the app that I haven't touched". Five photos: the top half and
the four tabs (Details, Extras, Seasons & Episodes, More Like This).

**Looks broken**
1. The selected tab's label vanishes (empty white notch). "Details" is barely legible even unselected.
2. The panel is bright blue with a heavy white outline, unlike the charcoal glass used everywhere else.
3. "Scanning local library…" covers the bottom right of every tab, with a stray line into the panel. It stayed for
   10+ minutes, so the scan may also be stuck (check against Codex's 0.156 scan fixes).
4. Key Information data:
   - "Also Known As" lists foreign-language titles.
   - Runtime, Budget and Revenue all say "Unknown"; Budget and Revenue are meaningless for a series.
   - Production Company says "WingNut Films" (looks wrong for Marshals).
   - Labels and values are slightly out of line.
5. The Play button looks disabled: washed-out white with faint text.

**Works badly**
6. Every tab is a fixed full-height box, so Extras and Seasons show one row of cards and then empty space.
7. Card titles sit on the pictures:
   - Trailer titles collide with the text already in the thumbnail.
   - In More Like This, cards look faded and change size.
8. Seasons & Episodes: one season heading and stills only — no synopsis, watched tick or air date.
9. The Details tab is crammed:
   - Tiny cast faces and names.
   - Crew is eight Executive Producers.
   - The description is centred.
   - Technical Information shows raw `WEBDAVS · webdav.put.io/…` twice (Source and Path).
   - Mixed date formats (`2026-03-01`, `24/10/26`).
10. The tagline floats far below the year · genre line.

**Questions for the owner (asked 5 Nov 2026)**
- Keep the four tabs (fixed, readable, charcoal), or one scrolling page with no tabs (Episodes → Extras → More Like
  This → Details)?
- For series, should Episodes come first?
- Which Details fields are actually useful (budget, revenue, also known as, file path)?

**Owner's answers (5 Nov 2026)**
- **Keep the Details hero card** as it is ("I do like that actually").
- **Keep the four tabs in principle;** the design of the tabs is "completely wrong". The blue and the invisible titles
  likely come from the button-style changes (code may be unsure whether tabs are buttons). Claude may change the
  Codex-era design where needed.
- **Purpose of Details and Extras:** the owner often looks up trivia on their phone after watching, so that
  information should be in the app from the start. Hence cast and crew, **capped at 8 each**.
- **Unknown values stay visible:** Budget, Revenue and the other fields always show, saying "Unknown" when missing (for
  films too). No hiding.
- **Runtime:** for series, don't say Unknown when episodes have runtimes. To decide: take it from the files or from
  the online episode data.
- Task: find a new way to display the information in the four tabs.

**Owner's comments on mock-ups 01–05 (5 Nov 2026)**
- Reference (not to copy): Disney+ (*Love/Hate*). Hero on top; tabs (Episodes, Suggested, Details) below. Highlighting
  a tab scrolls the page up. Episodes are a list: seasons on the left, then each episode's still, title, synopsis
  and runtime. The owner uses Disney+, Prime Video and Netflix and borrows ideas from all three.
- **No obvious panels.** Each section (Key information, Reception, …) needs its own area, but not a boxed panel.
- **Reception is a fixed set:** IMDb, Rotten Tomatoes, Metacritic, TMDB (every source Supernova has). Each always
  shows, saying "Unknown" when missing, so every Details page looks the same.

**Status:** second round of mock-ups (no panels, list-style episodes, fixed Reception) — see `details-tabs/v2-*`. Planned for 0.157 (0.156 is not held up). Related parked
item: library statistics colouring (GitHub #100), to be discussed together with this page (#99).
