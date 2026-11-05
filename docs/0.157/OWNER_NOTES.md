# 0.157 — owner notes (from 5 Nov 2026)

**Approved so far (5 Nov 2026):**
- Details tabs: Seasons & Episodes (`details-tabs/v6-01`), More Like This (`v6-03`), Details (`v15-b`).
- Description in the hero card, with More › Full description (`hero-01`–`04`; polish when built).
- More Like This from three sources (TMDB recommendations, TMDB similar, Trakt related).
- Smooth logo transition.
- Still open: Extras (Home row cards, `v3-03`, not yet commented on); colour vs monochrome service logos (colour used
  in the approved More Like This).

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

**Owner's verdict on round 6 (5 Nov 2026)**
- **Seasons & Episodes: approved** (`v6-01`).
- **More Like This: approved** (`v6-03`):
  - Library titles without a play icon.
  - Centred full-colour service logos with no background.
  - Title · year on one line.
- **Details: not yet.** Key information and Technical information get far too much prominence.

**Status:** Details tab round 7 (`details-tabs/v7-01`, no About):
- Reception leads; then Cast and Crew portraits.
- Key and Technical information as quieter two-column lists at the foot.

**Owner on round 7 (5 Nov 2026):** still not happy. Wanted layout, in two segments:
- **Top:** three columns, each vertical — Reception (left), Key information, Technical information.
- **Below:** a row of cast, then a row of crew, at the bottom.

**Status:** round 8 (`details-tabs/v8-01`) sent 5 Nov 2026.

**Owner on round 8 (5 Nov 2026):** "getting there".
- Reception icons with **no background**; each item bigger; all three top columns the same text size, each filling
  its area (Reception looked crammed top left).
- **Expand Technical information** to fill its column.
- Unsure whether Cast and Crew should show a count ("8 of 32"). Claude's view: drop it; there is no way to see
  the rest, so the count teases.

**Status:** round 9 (`details-tabs/v9-01`):
- Reception adds **Awards** (already in the code via OMDb) and uses the app's own tomato and Metacritic marks, with
  IMDb and TMDB as coloured wordmarks, all without backgrounds.
- Technical information adds resolution, bit depth, HDR, frame rate, bitrate and audio languages (data checked when
  built; some may need Codex).
- No counts on Cast and Crew.

**Owner on round 9 (5 Nov 2026)**
- **Rule: a row is never removed.** Missing values say "Unknown" (Claude chose "Unknown" over "Unavailable" to
  match the rest of the page); e.g. Frame rate stays with "Unknown".
- Use the bottom space better: "everything looks a bit pushed up".

**Status:** round 10 (`details-tabs/v10-01`):
- The page fills exactly two TV screens: the three columns spread over the first, cast and crew over the second.
- Bigger rows and portraits.

**Owner on round 10 (5 Nov 2026)**
- Move Reception, Key information and Technical information slightly further down.
- **Nothing on the Details tab is focusable;** it is for reading only.
- Claude's proposal for moving around:
  - Down scrolls to cast and crew; Up scrolls back.
  - One more Up returns to the tabs; Left/Right switch tabs.

**Status:** round 11 (`details-tabs/v11-01`) sent.

**Owner on round 11 (5 Nov 2026):** "getting there".
- Should Reception have separators between its items? (Claude: yes, to match the other two columns.)
- Global question: should the official Supernova background artwork be behind the Details page and its tabs?
  - Claude: yes, and it already is. `PreviewMoviePage` uses `PreviewUtilityBackground` (Space Black, as Settings,
    Search and About do). The blue in the photos is the tab box painted over it.
  - Rule: **Space Black behind every full page; content sits straight on it;** charcoal glass only for side panels
    and pop-ups.

**Status:** round 12 (`details-tabs/v12-01`) sent: on the real Space Black artwork, with Reception separators.

**Owner on round 12 (5 Nov 2026):** the separators are barely visible on the black (only near the lighter top);
make all of them clearly visible.

**Status:** round 13 (`details-tabs/v13-01`): row separators at 24% white, column dividers at 32%, same 1 px
thickness. Becomes the rule for separators on Space Black.

**Owner on round 13 (5 Nov 2026):** "the best yet". Make the five headings (Reception, Key information, Technical
information, Cast, Crew) clearly titles, e.g. with an underline.

**Status:** round 14 (`details-tabs/v14-01`): titles bigger and white, each with a 2 px underline (60% white)
across its area, firmer than the row separators.

**Owner on round 14 (5 Nov 2026):** the full-width line looks very weird, especially on Cast and Crew. Underline
only the title text?

**Status:** round 15: titles underlined under the text only, two versions:
- `v15-a`: Supernova blue, the same as the open tab.
- `v15-b`: white.

**APPROVED by the owner (5 Nov 2026): the Details tab as `details-tabs/v15-b`**
- On Space Black: Reception (IMDb, Rotten Tomatoes, Metacritic, TMDB, Awards), Key information and Technical
  information as three vertical columns; then a Cast row and a Crew row (8 portraits each, no counts).
- Titles white, with a short white underline under the text only.
- Separators 24% white (rows) / 32% (columns).
- Every row is always shown ("Unknown" when missing).
- Nothing on the tab is focusable: Down/Up scroll, Up again returns to the tabs.

**Logo transition (owner, 5 Nov 2026)**
- Today the official title logo in the hero card shrinks to a small logo above the tabs when you go down into them.
  Keep that, and make it very smooth.
- Plan:
  - One continuous move: the logo glides and scales from its hero position to the compact top-left position, in
    step with the scroll (about 250 ms, ease-out); the hero fades underneath.
  - Up reverses it exactly.
  - No disappear-and-reappear. Planned for 0.157 (0.156 is not held up). Related parked
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
- **Status:** mock-ups `details-tabs/hero-01`–`hero-04` sent 5 Nov 2026 (fits; long one with …; More menu; full text panel).
- **Approved in principle by the owner (5 Nov 2026):**
  - The description goes in the hero card; More › "Full description" shows the full text.
  - It is removed from the Details tab.
  - The hero still needs design polish when built.

## 3. Remaining Details mock-ups and the tab order (5 Nov 2026)
- The owner asked for every outstanding mock-up and to discuss the tab order for series and films.
- Claude's proposed order:
  - **Series:** Seasons & Episodes → Details → Extras → More Like This. Opens on Seasons & Episodes.
  - **Films:** Details → Extras → More Like This. Opens on Details.
  - An empty tab disappears and the rest close up.
- Mock-ups `details-tabs/set2-01`–`05`:
  - `set2-01`: Extras on Space Black.
  - `set2-02`: the polished hero (Resume S1 E1 with progress; Play no longer looks disabled).
  - `set2-03`: a film's Details tab.
  - `set2-04`: a season's own page.
  - `set2-05`: a title with no file, where **Where to watch** replaces Technical information.
- **Owner's decisions (5 Nov 2026):**
  - **Tab order approved:**
    - Series: Seasons & Episodes → Details → Extras → More Like This.
    - Films: Details → Extras → More Like This.
  - **Extras:** show every kind of video received (TMDB: trailers, teasers, clips, featurettes, behind the scenes,
    bloopers, opening credits…), each as its own row in the same design. **Trailers always first.** Empty kinds
    hidden.
  - **Films:** the same Details design, **with Cast and Crew rows** as for series.
  - **A title with no file** (e.g. on Netflix, from More Like This): the owner is minded to **open the service's
    app** rather than a Supernova page, since the user already pays for that service.
    - Claude: agreed. Use the title's own deep link where the service supports it; otherwise open the app.
    - `set2-05` is dropped.
  - **Resume:** show the progress somehow (today it just says Resume). Options in
    `details-tabs/resume-options-a-b-c.png`, left to right:
    - A: a line above the buttons.
    - B: a line inside the button.
    - C: the time in the label.
- **Resume: option C** (owner, 5 Nov 2026): the label carries it — "Resume S1 E1 · 16 min left"; no progress line.
- **Confirmed (owner, 5 Nov 2026):**
  - `set2-01` Extras, with every video kind as its own row, Trailers first.
  - `set2-02` hero, with resume option C.
  - `set2-03` film Details, plus Cast and Crew rows.
  - `set2-04` season page.

**THE DETAILS REDESIGN IS FULLY APPROVED (5 Nov 2026), for 0.157.** Summary for building:
- **Hero card:** as today, plus the description (up to five lines) and the soft text patch. More › Full
  description. "Play" / "Resume Sx Ey · N min left".
- **Logo:** glides and shrinks smoothly to the compact top-left logo when going down into the tabs.
- **Tabs:** plain words on one line; the open tab's stretch of the line in Supernova blue; the focused tab a white
  box. Order: series Seasons & Episodes → Details → Extras → More Like This (opens on Seasons & Episodes); films
  Details → Extras → More Like This (opens on Details). Empty tabs hide.
- **Seasons & Episodes** (`v6-01`):
  - One Home-style row per season, title inside the picture, no synopsis.
  - Season headings line up with the cards; focus fills their box white; OK opens the season page (`set2-04`).
- **Details** (`v15-b`):
  - On Space Black. Reception (IMDb, Rotten Tomatoes, Metacritic, TMDB, Awards), Key information and Technical
    information as three vertical columns.
  - Then Cast and Crew rows, 8 portraits each, no counts.
  - White titles with a short white underline; separators 24% / 32% white.
  - "Unknown" instead of removing a row.
  - Nothing focusable: Down/Up scroll.
- **Extras** (`set2-01`): Home row cards, one row per video kind, Trailers first.
- **More Like This** (`v6-03`):
  - Rows "In your library" and "On your services", from TMDB recommendations + TMDB similar + Trakt related.
  - Ranked by agreement, no fixed count, no genre padding.
  - Centred colour service logos with no background; title · year on one line; no play icon on library items.
  - A service title opens that service's app (deep link where possible).
- **Background rule (app-wide):** Space Black behind every full page; content straight on it; charcoal glass only
  for side panels and pop-ups.
- **Engine and data for Codex:**
  - The new technical fields (resolution, bit depth, HDR, frame rate, bitrate, audio languages).
  - Awards.
  - Series runtime from the episodes.
  - Crew order with the creator first.
  - More Like This sources and ranking.
  - Service deep links.
  - Check the "WingNut Films" studio data.

## 4. Details More menu (owner photos, 5 Nov 2026: a film not yet started; Dark Matter in progress)
- **Restart Movie is offered on a film never started.** That's wrong. Cause: `PreviewMoviePage.more()` adds Restart
  whenever there is a file, without checking the resume point. Fix: show Restart only when there is a resume
  position, i.e. the Play button reads "Resume…". The whole Playback group then disappears for an unstarted title.
- The owner confirms the panel is **charcoal** on the TV; the blue is the camera.
- **Icons need work.** Mark Watched, Report Incorrect Match and Artwork all fall back to the same "remove" circle;
  Remove From Library is an odd shelf glyph.
- Proposal (`docs/design/proposals/more-menu/01`, `02`):
  - A proper line icon for every entry: restart arrow, ticked circle (Mark Watched / Unwatched), list-plus (Add to
    Row), magnifier-tick (Fix Match), boxed minus (Remove From Library), page (Full Description), subtitle box,
    picture (Artwork), bin (Delete), info (Change Info of All Episodes).
  - "Report Incorrect Match" becomes **Fix Match** (the approved panel).
  - "Subtitles and Artwork" splits into **Subtitles** and **Artwork**.
  - **Full Description** joins Files & Media (from the hero decision).
- **Owner (5 Nov 2026): approved.** Also:
  - **Focused row = filled white "button" look** (white fill, black text and icon), as in the mock-ups, instead of
    the current white outline box. Applies to the More menu and every list of this kind.
  - Prefers the **mock-up's panel colour** to today's look.
    - The mock-up uses `#16181C` at 100% (solid), the same as `SupernovaStyle.solidPanel` (the shared choice list
      in 0.156), with no outline.
    - The current Details More menu uses glass `#16181C` at 97% with a light edge, over the brighter artwork.
    - Decision pending: use the solid panel for every pop-up and side panel.
