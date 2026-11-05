# 0.157 — owner notes (from 5 Nov 2026)

**Approved so far (5 Nov 2026):**
- Details tabs: Seasons & Episodes (`details-tabs/v6-01`), More Like This (`v6-03`), Details (`v15-b`).
- Description in the hero card, with More › Full description (`hero-01`–`04`; polish when built).
- More Like This from three sources (TMDB recommendations, TMDB similar, Trakt related).
- Smooth logo transition.
- Also for 0.157:
  - The More menu fixes (section 4).
  - The global panel look: solid `#16181C`, no outline, filled-white focus.

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
    - **DECIDED (owner, 5 Nov 2026), global rule:**
      - (Superseded below: 90% opacity, not solid.) Every pop-up, menu and side panel `#16181C`, no outline, soft shadow.
      - The focused row is filled white (`#F4F6FB`) with black text and icon (`#0B0D12`).
      - Replaces the 97% glass with a light edge, and the white outline focus box.
      - **For 0.157** (owner, 5 Nov 2026), together with the More menu fixes; built into the shared style so it
        shows everywhere.
    - Owner then asked about keeping some transparency. In Supernova "glass" is only 97% opacity plus a light
      1 px edge, with no blur: real blur needs Android 12, and the Shield runs 11.
    - Comparison sheet `more-menu/03-opacity-100-97-90-80.png`: all with no outline, filled-white focus.
    - **DECIDED (owner, 5 Nov 2026), global rule:**
      - Every pop-up, menu and side panel is `#16181C` at **90%** opacity, with no outline.
      - The focused item is filled white with black text and icon.
      - One shared value, so it can be tuned in one place after the Shield check.
    - Also investigate in 0.157: the owner's Edit Page photo looks far more see-through than its code's 97%; find the
      cause so 90% really shows as 90% on the Shield.

## 5. The global panel look on Playback Settings and Edit Page (owner, 5 Nov 2026)
- The owner asked whether the new look (90% `#16181C`, no outline, filled-white focus) applies to Playback Settings
  and Edit Page too, given their in-row controls. **Yes, it is global.**
- On a focused row everything switches to dark ink, so nothing is white on white:
  - Labels and icons black.
  - The chosen value of a choice or stepper in dark Supernova blue `#0B57D0`, bold; other choices dark grey.
  - Arrows black.
  - Switches: on = dark blue track with a white knob; off = light grey track with a dark knob.
  - Colour dots keep their colours; the chosen one gets a black ring.
- Mock-ups `docs/design/proposals/panel-focus/01`–`03`. **Approved** (owner, 5 Nov 2026), with values changed by section 6.

## 6. No blue for values (owner, 5 Nov 2026)
- Owner: a single value shown (‹ Any ›, ‹ 5% ›, English (TEXT)) needn't be blue; it is obviously the value. Asked to
  mark the chosen option of a choice row without blue too.
- **Decided:** stepper and list values are plain white (black on the focused row).
- Options for choice rows (Movies | TV Shows | Both, Any | All, Small | Medium | Large), no colour, in
  `panel-focus/04`–`06`:
  - A: chosen bright, others dimmed.
  - B: chosen with a short underline (as the Details titles).
  - C: chosen in a small chip.
- **APPROVED (owner, 5 Nov 2026): option C, the chip** (`panel-focus/06`), as a global rule.
  - The chosen option sits in a small rounded chip: light grey at rest; black with white text on a focused row.
  - No separators.
  - Steppers and list values are plain white.
  - No blue for values anywhere.

## 7. Library statistics on library pages (owner photo, Documentaries page, 5 Nov 2026; GitHub #100)
- Owner: the stats box keeps the old blue Supernova look; should it match the buttons below?
- Claude's review:
  - A pale blue box on Space Black (against the new rule).
  - Ten stats in two uneven rows, with tiny text.
  - Old date format "5/11/26".
  - Blue caption bars under the posters.
- Proposal (`docs/design/proposals/library-stats/01`, `02`):
  - One even row of 8: big white number, grey label, an icon, visible separators. Stats: Titles, Unwatched, In
    progress, Total size, Added this month, On this Shield (+ size), On the network (+ size), Last updated
    ("5 Nov · 14:40").
  - Library status on its own line with a reason, e.g. "Scan needs attention · see Library Health".
  - Toolbar in the button style; Edit Library white.
  - Poster captions as plain white title + grey line, no blue bar.
  - `01`: straight on Space Black (as Details). `02`: in a 90% charcoal strip.
- **APPROVED (owner, 5 Nov 2026): `01`, straight on Space Black.**

## 8. Library items and one edge rule for every artwork card (owner photos, 5 Nov 2026)
- The owner also wants **library items off the blue look**: today a blue caption bar under each poster. Change to
  the new scheme (white title + grey line, no bar), as `library-stats/01`.
- Consistency question: the Home hero and Details cards have a white outline; Home row cards and library posters
  only get one when focused (and grow). Should they all match, perhaps with a permanent white outline?
- Options (`docs/design/proposals/card-edges/01`–`03`); in all three the focused card grows and gets a solid white
  ring:
  - A: no edge at rest, on any card (the hero loses its outline).
  - B: a faint edge (about 28% white) on every card, hero included.
  - C: a white outline (85%) on every card.
- **APPROVED (owner, 5 Nov 2026): B, a faint edge, as a global rule for every artwork card.**
  - At rest: 1.5 px, about 28% white.
  - Focused: the card grows, with a solid white 4 px ring.
  - Applies to:
    - All posters on Movies, TV Shows and custom library pages.
    - The Home and Details hero cards.
    - Every Home row.
    - The Details tabs: Seasons & Episodes, Extras, More Like This.
    - The player's Chapters and Episodes strip.
  - Cast and crew portraits (not focusable) get the faint edge only.

## 9. One size for every row card and both heroes (owner, 5 Nov 2026)
- Today's sizes (TV units, 960 wide):
  - Home rows 172×105.
  - Details Extras / More Like This / Seasons & Episodes about 220×124.
  - Player strip about 150×85.
  - The Details hero is bigger than the Home hero (which leaves room for side peeks).
- **Global rule (owner, 5 Nov 2026):** one row card everywhere a row of artwork appears.
  - Home rows, the Details tabs and the player strip.
  - 16:9, 400×225 px at 1080p (200×112 TV units); focused 440×248 with the white ring; 24 px gap.
  - A sliver of the next card shows at the edge.
  - Library posters keep their own portrait size, identical on all library pages.
- Heroes: Home and Details the same size and position. Home's carousel cue options:
  - A: dots on the card.
  - B: slim side peeks.
- Mock-ups `docs/design/proposals/one-size/01`–`04`.
- **Home hero cue: dots (A), approved by the owner (5 Nov 2026).**
- Correction: the row cards are 16:9; the **hero is not**. It is about 2.5:1 in the mock-up (today about 2.3:1); a
  16:9 hero at full width would fill the screen.
- Mock-ups `docs/design/proposals/hero-tabs/01`–`04`:
  - Both heroes at 2.4:1 (1776×740 at 1080p); 2:1 would push the tabs and rows off screen.
  - Details tabs with no tab focused (open tab white with a white underline) and with a tab focused (filled white).
- **Owner (5 Nov 2026):**
  - The Continue Watching row / Details tabs must stay slightly visible below the hero ("what's that down there?");
    the hero may be a little bigger.
  - **Tab focus as in `hero-tabs/04` (filled white) approved.**
- Round 2 (`hero-tabs/v2-01`–`v2-04`):
  - Heroes 1776×820 (about 2.17:1), with the row and tabs peeking at the bottom.
  - The open tab is shown as a chip (as chosen options), matching the global chip rule; focused, it is filled
    white.
- **APPROVED (owner, 5 Nov 2026), round 2 (`hero-tabs/v2-01`–`v2-04`):**
  - Home and Details heroes the same size, 1776×820 at 1080p (about 2.17:1), the row and tabs peeking below.
  - Home carousel dots.
  - The open tab as a grey chip; the focused tab filled white.

## 10. Legacy NOVA clean-out (owner, 5 Nov 2026)
- Owner: unused legacy NOVA code (like the first-play blue help box) shouldn't be in the app; can Codex find and
  remove it?
- Plan:
  - Codex writes a **report first** (`docs/codex/findings/legacy-cleanup-candidates.md`): SAFE / UNSURE / KEEP,
    with evidence and size, grouped into small batches.
  - The owner and Claude approve.
  - Removal in small batches, each with a full build and tests, plus a Shield check for screens.
- Biggest candidate: everything behind the old `try_new_ui` / classic-UI switch (about 95 references).
- Keep engine, playback and network code close to NOVA upstream, so future NOVA fixes can still be brought in.
- Prompt for Codex given to the owner on 5 Nov 2026.

## 11. Home hero change — smooth and seamless (owner, 5 Nov 2026)
- Owner: "The effect of changing hero card on the home page. Can you make that like really smooth and seamless
  feeling?"
- Today (`PreviewPages.changeFeatured` › `renderFeatured`): the whole card is rebuilt, then nudged 36 dp sideways
  and slid back (220 ms). The new artwork loads after the card appears, so it can flash the empty card colour, and
  the title, text and buttons jump with it.
- Proposal (to mock up as a short video before coding):
  - The card frame, edge and buttons stay still; only what is inside changes.
  - New artwork already loaded before the change starts (the next and previous heroes kept warm), so there is never
    an empty frame. If it is not ready in time, the change waits for it instead of flashing.
  - The old picture crossfades into the new one (about 450 ms, gentle ease), with a slight drift in the direction of
    travel and a very slow zoom while it rests.
  - The logo or title, description and details fade out quickly and fade in just after the picture (about 150 ms
    later), so the text never sits over the wrong film.
  - Focus stays on the same button throughout; the dots glide to the new position.
  - Remote presses during a change finish the current one at once and start the next (no queue of lag).
  - Automatic changes pause while the owner is on the hero and never happen while a row below is focused.
  - Follows the system "remove animations" setting (instant swap, still no flash).
- Pairs with the 0.157 hero size change (1776×820) and the dots.

## 12. Details tabs pop in (owner, 5 Nov 2026)
- Owner: opening a film, the Details tab is there at once but the other tab titles "load in" one by one; it does not
  look seamless or professional. Wants the Details page to feel instant.
- Cause (`PreviewMoviePage.rebuildTabs`): a tab is only added once its content has arrived. Extras waits for the
  trailer list, More Like This for the recommendations; each arrival clears and rebuilds the whole tab row, so the
  titles appear one at a time and the row shifts.
- Proposal (waiting for the owner's go-ahead):
  - The full set of tabs is drawn with the page: Details, Extras, Seasons & Episodes (series only), More Like This.
    The row never rebuilds or moves after that.
  - Content fills in inside its tab. While it loads, a quiet "Loading…" line (or card shapes); if nothing is found,
    "No extras found" / "Nothing similar found". Matches the global rule: missing things say so, rows are never
    removed.
  - Optional: what has been fetched before is kept, so going back to a title shows its tabs filled straight away.
- **APPROVED for 0.157 (owner, 5 Nov 2026):** go with the recommendation (all tabs drawn with the page, content
  fills in, "Loading…" then a "none found" line). Tab titles must never pop in: "avoid that at all costs".

## 14. Seek loading spinner off screen (owner photos, Dark Matter on WebDAV, 5 Nov 2026)
- Seen: a small spinning blue circle in the bottom-left corner while seeking, half off the screen.
- Cause: NOVA's `seek_state` spinner (shown while the player is busy after a seek) sits in the control bar *below*
  the transport row, so it falls under the bar's bottom edge; it uses Android's default blue.
- Done in 0.156 (bug fix, not a design): it no longer shows in Supernova's HUD, because the bar now grows for
  Chapters · Episodes and it would have pushed the controls up on every seek.
- Proposed replacement (mock-ups `docs/design/proposals/seek-loading/01`–`02`, awaiting the owner):
  - A: a small white ring beside the time in the bubble, "Chapter 2 · loading".
  - B: the seek dot itself becomes a spinning white ring until the picture is ready.
