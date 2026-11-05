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

**Status:** waiting for the owner's answers, then mock-ups. Planned for 0.157 (0.156 is not held up). Related parked
item: library statistics colouring (GitHub #100), to be discussed together with this page (#99).
