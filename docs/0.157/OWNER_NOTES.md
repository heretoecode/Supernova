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

**Owner's comments on round 2 (5 Nov 2026)**
- Episodes as **horizontal rows**, in keeping with Home and the player's Episodes strip (Claude agreed). One row per
  season; the focused episode's synopsis shows under its row.
- **Details on one page:** description, key information (expandable), reception, cast, crew, technical information.
- **Extras use the Home row cards** (title and details inside the artwork), for consistency. Same for More Like This.
- **More Like This:** some suggestions elsewhere are baffling ("how on earth did that become a suggestion?").
  - No limit of 12 and no "must show 12".
  - Keep TMDB's suggestions, and also look for suggestions from elsewhere.

**Claude's proposal for More Like This (round 3)**
- Today: TMDB "recommendations", kept if in the library or on the owner's services, stopped at 12. If there are fewer
  than 12, it tops up with any library title sharing one genre. That genre padding is the likely source of the odd
  ones.
- New:
  - Ask three sources: TMDB "recommendations", TMDB "similar" and Trakt "related" (Trakt is already in Supernova).
  - Rank titles by how many sources agree. A title suggested by only one source is shown only if it also shares at
    least two genres.
  - Keep only what is in the library or on the owner's services, in two rows: **In your library** and **On your
    services**.
  - Show however many pass, with no fixed number. **Drop the genre padding.**
  - If nothing passes, show a calm empty message.
- The data side is Codex's (non-visual); Claude does the rows.

**Owner's comments on round 3 (5 Nov 2026)**
- **Remove the synopsis** under the episode row.
- Details: that is the data wanted. Claude is free to redesign and rearrange it (e.g. description and reception
  first, crew last).
- More Like This: **two rows (In your library / On your services) approved.**
  - Today a play icon (local) or the service's icon (online) sits in the centre of the thumbnail. With the rows
    separated, still highlight the service, more strongly than the "N"/"prime" tags in round 3.
  - Use the **same service icons as the Where to Watch service list** (`res/drawable/provider_*.xml`).
- **Monochrome or colour service logos: undecided; revisit.** Colour is fine if there is a unifying design across
  them all (earlier concern: pops of colour look distracting).
- More Like This sources **approved: all three** (TMDB recommendations, TMDB similar, Trakt related) — "the more
  recommendations the better".

**Status:** round 4 mock-ups `details-tabs/v4-01`–`v4-04` sent 5 Nov 2026:
- Episodes without the synopsis.
- Details rearranged: description and reception first, key information as a grid, then cast, crew and technical.
- More Like This with the app's own service logos in one uniform dark chip, monochrome (`v4-03`) and full colour
  (`v4-04`).

**Owner's comments on round 4 (5 Nov 2026)**
- Seasons & Episodes now liked.
- Asked whether Season 1's heading opens a season-only Details page: **it already does** (`PreviewSeasonActivity`).
  It keeps working in the redesign, shown with a › and the usual white focus.
- More Like This marks:
  - **No background behind icons:** icons stand alone, as everywhere else in the app.
  - Not sure about the top-left position.
- Details:
  - **Cast and crew with pictures,** as today; plain text "reads like a book".
  - The layout felt too compacted.

**Status:** round 5 mock-ups sent:
- `v5-01`: clickable season headings.
- `v5-02`: Details as one roomy scrolling page with portraits.
- `v5-03` / `v5-04`: icons with no background, centred (as today) or bottom right by the title.

**Owner's comments on round 5 (5 Nov 2026)**
- The focused season heading stuck out to the left: headings must line up with everything else.
- Key information and Technical information should be **vertical** rather than horizontal grids.
- More Like This:
  - **Icons centred** (liked).
  - **No play icon on library items** (Home has none; clicking obviously plays).
  - Service logos stay centred.
  - Drop "On Netflix" etc.; **title and year on one line.**
- Description in the hero (section 2): the owner asked whether text would appear twice. Claude proposed:
  - Hero only, up to about five lines; the Details tab never repeats it.
  - A rare longer one opens in full from the hero's More menu ("Full description").

**Status:** round 6 mock-ups `details-tabs/v6-01`–`v6-03` sent 5 Nov 2026. Planned for 0.157 (0.156 is not held up). Related parked
item: library statistics colouring (GitHub #100), to be discussed together with this page (#99).

## 2. Details hero card: put the description in the hero (owner idea, 5 Nov 2026)
- **Owner:** the hero card shows the tagline; the full description sits in the Details tab. Move the description into
  the hero card, freeing space on the Details tab. No mock-up yet; discuss after round 5.
- **Claude's thoughts:**
  - Cap the hero at about four lines. Show "…" when longer, and only then repeat the full text at the top of the
    Details tab.
  - Use the soft text patch from the Home hero; keep the text in the left half.
  - Keep the tagline as one italic line above the description (owner to decide).
  - Optional, as on Disney+: once a series is in progress, show the next episode's synopsis next to "Resume Sx Ey".
- **Status:** idea noted; waiting for the owner after round 5.
