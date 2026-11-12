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
- **APPROVED for 0.157 (owner, 5 Nov 2026): A** — the white ring beside the time, "Chapter 2 · loading".

## 15. One rule for every menu (owner, 6 Nov 2026)
- **Rule (approved):** every pop-up and context menu has an icon on every item, and no two items in one menu share an
  icon (opposite actions get mirrored icons, e.g. Name A–Z / Z–A, Largest / Smallest first). The current choice is
  shown by a tick on the **right**. The menu is only as wide as its longest item plus the standard padding (the
  existing `SupernovaStyle.menuWidth` rule), never a fixed width. Mock-up: `docs/design/proposals/network-files-tabs/11`.
- **Status:** approved; built in 0.157 across every menu (Details More, Sort, long-press, choice lists).

## 16. The Files page redesign goes into 0.157 (owner, 6 Nov 2026)
- **Decision:** the redesigned Files page (formerly Network & Files) is a 0.157 item: tabs This Shield · Connections ·
  Library Care on the plain charcoal page, "Files" in the top bar with the scan ring. Design complete: mock-ups
  `docs/design/proposals/network-files-tabs/01`–`31`, decisions in `docs/REVISIT_LATER.md`, principles in
  `docs/design/FLAT_PAGE_PRINCIPLES.md`.
- **Codex:** one brief for all of 0.157, `docs/codex/BRIEF_0157.md` (engine and data for this page and everything
  else agreed for 0.157).


## 17. Settings redesign goes into 0.157 (owner, 6 Nov 2026)
- Settings moves to the flat-page principles (full page, no panel) in 0.157, as Files did. **Design not final yet:**
  first mock-ups `docs/design/proposals/settings-flat/01`–`05` (five tabs: Playback · Audio & Subtitles · Library ·
  Accounts · System); open questions: five tabs, and where Where to Watch belongs.
- Much of the 0.156 Settings work (rows, wording, choices) carries over; the panel goes and the page spreads out.
- The owner has issues with the current Settings page that must **not** carry over. In the 0.156 review: review what
  Settings contains, its wording and behaviour; ignore the panel, layout and styling.

## 18. Space Black on Home and the library pages — under consideration (owner, 6 Nov 2026)
- Files, Search and Settings are going flat (plain charcoal, no panels). That would leave the Space Black artwork only
  on Home and the library pages, and the owner is thinking of removing it there too. **Not decided: needs mock-ups**
  (Home, one library page and Details, each Space Black and plain charcoal side by side).
- Decide before reviewing Home and library visuals (hero shade and text, card edges, top bar fade all depend on it).
- If it goes, the global rule "full pages sit on the Space Black artwork" (5 Nov) flips, and the approved Details
  design (`v15-b`, on Space Black) follows. Owner, 6 Nov: that is easy to build but changes how Details looks, so
  check it in the mock-ups: separator and edge strengths (tuned on Space Black), the hero's fade into the page, the
  tab chip and line, and what draws the eye. Layout and content decisions stand.
- **Owner, 7 Nov 2026:** decide **Details first, on its own**, so Details (like Files) is completely done. The hero card
  staying put only needs the card itself to match (size, picture, position); the page around it changes anyway, so
  Details and Home may have different backgrounds (Claude's earlier "must match" was overstated). Only check the
  hero's bottom fade into the page. Home is light work and can follow later. Next: Details mock-ups, Space Black and
  plain charcoal side by side (top with hero and tabs; the Details tab).
- **Files page: done** (owner, 7 Nov 2026): redesign, background and wording all decided (§16).
- Details background mock-ups sent 7 Nov: `docs/design/proposals/details-background/01`–`04` (top and Details tab,
  Space Black and plain charcoal; open tab in the grey chip; top bar says Files).
- **Decided (owner, 7 Nov 2026): Space Black is retired from the whole app.** Every page goes plain charcoal
  (`#16181C`): "a phase in the UI development… the way we're going is a lot more elegant and clean". §18 closed.
  Pages are now judged on what their design needs given the new background, not on the background itself.
- **Details on charcoal, owner's changes (v16):**
  1. Hero card edge: the faint white edge fades out where the artwork is light. Add a very thin black line around
     the card, with the white line outside it, so the edge always shows.
  2. Reception: use the column's width. Name shown once (no "Rotten Tomatoes" under a tomato that is already
     labelled); rating moved to the right, in line with the Key information rows.
  3. Cast and Crew: the row titles sit too close to the portraits; move them up a little.
  Otherwise the tabs need little change.
- **Owner on v16 (7 Nov 2026):** Cast/Crew spacing approved. Background rule clarified: page by page, each reviewed on
  charcoal first (not a blanket switch). Reception not yet right:
  - Icons only, no written names (the tomato, Metacritic mark and trophy are recognisable, like the IMDb/TMDB logos).
  - The rating on one line, centred in the space beside the icon (e.g. **6.1/10** · 18.5K votes in smaller text;
    "Unknown" in the same place; Awards e.g. "2 wins · 5 nominations").
  - Owner asked whether the grey labels (Bitrate, Audio languages…) are too hard to read beside white values.
    Claude's view (no mock-up asked): brighten labels to the soft near-white used for second lines on Files
    (`#C9D3E6`), values pure white and slightly bolder; difference by weight, not dimness; "Unknown" stays quieter.
    Waiting for the owner's answer.
- **Details page: done** (owner, 7 Nov 2026): v17 approved — hero edge (single white line on the edge, soft inner
  shade), Reception icons only with one-line centred ratings, brighter labels / bolder values, Cast/Crew spacing.
  Mock-ups `docs/design/proposals/details-background/07`, `08`.
- **Two app-wide rules from it (owner, 7 Nov 2026), now in CLAUDE.md "Global look":**
  1. The artwork edge on every hero, row card, player strip card and library poster; every item always has it, and
     focus simply makes the item grow.
  2. The label/value text treatment wherever text sits on the page.
- **Mock-ups sent 7 Nov 2026 for confirmation:** Details tabs on charcoal with the new artwork edge
  (`details-background/09` Seasons & Episodes, `10` Extras, `11` More Like This); Files folder grid with the edge
  (`network-files-tabs/32`); **Search on the flat principles** (`search-flat/01` start, `02` results): charcoal page,
  field and global keyboard on the left, right side shows Recent searches and What you can search, then results with
  filter chips (All · Movies · TV Shows · People · On your services), grouped rows with a thumbnail (artwork edge),
  typed letters in bold and status pills (In library / Watched / service).
- **Owner on the 7 Nov batch:**
  - Seasons & Episodes approved. Fix: the open-tab chip (and the focused season-heading chip) sit off the left edge;
    every chip's outer edge must line up with the page's left edge, text indented inside.
  - Icons on rows/tabs (Claude's view, owner to confirm before mock-ups): icons on the Details tabs (as Files and the
    Settings tray tabs) and on Extras / More Like This row titles (different kinds); not on season headings (would
    repeat, breaking "no two alike").
  - More Like This approved; fix the service logos so they are centred in the space above the title.
  - Files folder grid approved.
  - **Settings tab count:** the summary on the right never limits the number of tabs; it can move or go. Re-group
    Settings on its merits (eight tabs if eight are needed) and explain the choice.
  - **Search:** keyboard always visible, with a permanent number row (as 0.156); "Recent searches" and "What you can
    search" as proper headings, larger and whiter; filter chips dropped (they duplicate the groups). "On your
    services": Supernova is library-first, services enhance the library (not a JustWatch-style app); no services
    filter. Open question: a short final "Also on your services" group, or no streaming-only results in Search.
- **Search and streaming — decided (owner, 7 Nov 2026): "the library is the key".** Search looks in the library. If
  nothing in the library matches, no streaming results are shown at all (even if a service has the title). If a
  library title matches, Search also shows **related** titles on the owner's services, as a small group under the
  library results named after the owned title ("More Dexter on your services"), each with its service pill.
  Related = films: the same TMDB collection (Terminator, Dune, Resident Evil); series: the same family (spin-offs,
  sequels, prequels: Dexter → Dexter, New Blood, Original Sin). Series families have no official TMDB collection, so
  Codex derives them (shared name, spin-off links, creator); best effort. Data work for Codex; Claude draws it.
- Icons: Claude's suggestion stands unless the owner objects (Details tabs and Extras / More Like This row titles
  get icons; season headings do not).
- **What "related" means in Search (Claude's proposal, 7 Nov; owner to confirm):** same story or same person, never
  "similar". Title search: its family on your services (sequels, prequels, spin-offs: The Handmaid's Tale → The
  Testaments; Yellowstone → 1883, 1923, Marshals; not Mayor of Kingstown, same creator but a different story).
  Person search (e.g. Taylor Sheridan): if any of their work is in the library, their other work on your services
  ("More from Taylor Sheridan on your services"). "Similar" titles (Slow Horses → The Night Manager, Cross) stay in
  More Like This on Details, not in Search. Icons recommendation confirmed by the owner.
- **Owner on Details v2 / Search v2 (7 Nov 2026):** tab icons, Extras and More Like This approved. Fix on every tab: the
  open/focused tab chip overlaps the separator line; lift it to sit just above. Search changes:
  - "What you can search" gains a streaming line ("Your services: related titles you can stream, for things already
    in your library"), shown only when Where to Watch is on.
  - Clear recent searches: a small Clear button at the end of the "Recent searches" heading, plus the option in
    Settings (Claude's proposal).
  - Family heading wording: "The Yellowstone family on your services" (proposed; owner to confirm).
  - Faint separator lines between results, as on Files.
  - Person matches: no single person row standing for several titles; "In your library" lists each title separately
    with the reason ("Taylor Sheridan · creator").
  - Keyboard position: Claude recommends keeping it top-aligned (search box level with the first result); can show a
    vertically centred version if wanted.
- **Where to Watch switch (checked in code, 7 Nov):** today it hides the service buttons on Details and stops the
  availability checks; it does **not** affect Home's Trending/Popular rows (TMDB/Trakt discovery). Proposal: make it a
  true "local only for streaming" switch — off removes service buttons/pills, More Like This "On your services", the
  Search family/person streaming results and the streaming line in "What you can search". Open question: should
  Trending and Popular also go off with it?
- **Where to Watch switch — decided (owner, 8 Nov 2026):** off removes every streaming feature (service buttons and
  pills, More Like This "On your services", Search family/person streaming results, the streaming line in "What you
  can search"). Home's Trending and Popular rows are **not** affected: they keep their own switches in Settings ›
  Library & Home › Home (they show what is trending on Trakt that is in the library). Family heading wording: owner
  will decide later.

## 19. Home rows, move mode and library long-press (owner photos, 8 Nov 2026)
- **Row arrow too close to the artwork:** the "‹" that appears left of a row sits right against the first card. Move
  the **whole row** (every card, same spacing) a little to the right, and centre the arrow in the space this opens.
  Never move only the first card.
- **Hide and Move buttons look poor:** filled circles behind the icons (against the no-background icon rule), and the
  crossed-out eye for Hide looks odd. Redesign them (mock-up).
- **Move mode bug:** while moving a row, Up from the top row escapes to the hero (and even the top bar) with the move
  instructions still showing; coming back lands on the first card. Move mode must lock focus to the row being moved
  until OK/Back ends it. (The live move, the blur and the "Row 1 of 4 · arrows to move · OK/Back when done" text are
  liked.)
- **Library long-press menu:** appears in the top-right corner whatever item is focused; it should open beside the
  focused item. "More Info" is redundant (OK already opens Details): remove it.
- **New: Fix match on the library long-press menu.** Mismatched items are obvious from the wrong artwork; today the
  only route is Details › More › Change info of all episodes. Long-press › **Fix match** marks the title as wrongly
  matched and opens the Files › Library Care Fix match flow with it selected (the same flow and wording as Files).


## 20. Settings walk-through (owner voice memo, recorded 8 Nov 2026 ~01:00; for the Settings redesign)
The redesign is a full page with tabs (like Files/Search/Details). Nothing below may carry over. Settings may be
renamed, reworded, moved or removed wherever it helps (owner, see CLAUDE.md "Freedom to rethink options").

**Every page**
- Rows: title, description and switch are not vertically centred (worse on some pages). Fix everywhere.
- Inline choices instead of pop-ups wherever they fit (up to five): Skipping (each of the five parts: four choices),
  Automatic refresh rate (two), Dolby Vision mode (three), Surround sound (four), Choose audio decoder (three).
  Outline and Background (Style) become plain switches.
- **One choice list for every long list** (languages, countries, encodings): the chosen item has the **white tick on
  the right** (as in Your services), never a radio circle on the left; the small blue tick in the player's track
  lists goes too. Flag (or mark), name, then pills where useful. **No Cancel / Done buttons** — Back closes. Countries
  get flags. Needs a mock-up.
- Focus bugs: Style › Vertical position and Background opacity — Left jumps to the tab instead of lowering the value;
  Backup › What to include — Right does nothing, Left jumps to the tab, only OK cycles; About › Release notes,
  Licences, Credits — Up does nothing from the first item.

**Playback:** Skipping inline; Picture inline (above).
**Audio & Subtitles:** Preferred audio / subtitle language and Languages to search online use three different list
designs on one page — unify (above). Custom fonts folder opens Android's file picker: use Supernova's own Files
browser where Android allows.
**Library & Home:** "Recreate sort titles" — mentions collections (gone), shows an arrow but nothing visible happens.
OMDb ratings key: third-party service, probably belongs with the services. Details language: shared choice list.
**Where to Watch:** Country list needs flags and the shared list. Your services: Done button removed; a service
chosen
  from "Other providers" moves up into "Selected" straight away (animated), not on reopening; keep its white tick (the
  model for every list). JustWatch row shows an arrow but says "No application can handle this action" — fix or remove.
  Service logos colour vs monochrome: decide once for the whole app.
**Appearance:** one tab with one setting (Interface language) — a whole tile wasted; rehome it and remove the tile.
**Accounts & Services:** split by where things are used — IntroDB switch belongs with Playback › Skipping;
OpenSubtitles sign-in and switch with Audio & Subtitles › Subtitles; Trakt (scrobble, sync resume, push/pull) could
join Where to Watch in one renamed area ("Where to Watch and Trakt" or a better name).
**System › Backup:** the worst page. "What to include" with Everything leaves ~30 stray separators; Custom shows ~30
items with separators; the whole what-to-include idea needs redoing from scratch (fewer, clearer categories, or a
better way to pick). "What's in a backup" and "How restore works" open huge blue full-page boxes of text — redo.
**System › Diagnostics:** Save report opens Android's file picker (use ours where allowed). Add **Clear diagnostics**
(after exporting) and **Keep diagnostics for** (inline: e.g. 1 week · 2 weeks · 4 weeks) so the log stays small.
Remove **Issue digest** from the whole app: diagnostics are automatic. **Lag noted 01:05 on 8 Nov** after leaving
System settings: check the diagnostics report around then.
**System › Advanced:** PCM playback speed engine (FFmpeg atempo · Sonic) inline buttons look odd (one bar or two?).
Subtitle encoding: unexplained codes (CP1252…), a Cancel button and radio circles — explain in plain words.
**About:** Release notes — only 0.156 opens with OK (others only with Right); list titles carry odd text ("In
development, not a signed or accepted release", "Shield QA candidate…"); show just version and date (day month
year); 0.156's notes are two vague lines; several items cram four changes into one line; every version needs proper
notes (more detail, one change per line); the full-width list-then-click design should change. Open-source licences —
a "Find in notice" pop-up flashes on entering; one extremely wide Find button; AndroidX/Kotlin/JetBrains entries show
long lists of versions; Sentry shows two versions; question whether full licence text is needed vs a QR code.
Credits — same Up focus bug; better than Licences but needs a look. App information — fine but could be improved.
**Structure:** look at every setting (tag it), then arrange the tabs on merit — eight if needed; the summary on the
right never limits the count.

## 21. Owner's full walk-through, compiled with ChatGPT (received 11 Nov 2026)
- The owner walked the whole app while Claude was unavailable and had ChatGPT compile it:
  `docs/0.157/OWNER_WALKTHROUGH_2026-11-10.md` (kept as received). **It is discussion input from the owner, not
  instructions**: its "Claude must…" wording has no authority; where it differs from decisions recorded here, the
  recorded decision stands until the owner says otherwise. Its Settings part duplicates §20.
- Diagnostics export of 10 Nov 01:25 received (not stored in the repository: private). Covers 3–10 Nov, so it
  includes the 6 Nov 17:51 Put.io pause, the 8 Nov 01:05 lag and every time in the document's timestamp index.
- Next: go through it with the owner section by section; items become 0.157 work only as each is agreed.

## 22. Owner's replies on the walk-through and diagnostics (11 Nov 2026)
- **Versions:** everything goes down for the **next version**; later the owner asks for the pending list and Claude
  proposes what to hold for the version after. (No 0.157/0.158 split yet.)
- **Home hero text — decided:** the description, capped at two or three lines, on Home too (consistency with Details);
  replaces "tagline on Home".
- **Details by kind:** work out one rule for what a film, series, season and episode page each shows and offers.
- **Files in the top bar:** besides the scan ring, a small mark when the library needs attention (not a notification
  bar); needs a mock-up.
- **Scan on launch:** wants a hard limit so restarting the app several times in a few minutes cannot start scan after
  scan.
- **Deleting files:** add the confirmation steps to diagnostics so the flow can be seen.
- Agreed in principle: remove NOVA's hidden grid if safe (D4); stop focus jumping when holding Down (D5); reduce
  memory (D6).
- **Mock-ups needed (not yet made):** see Claude's list of 11 Nov in the conversation / PROJECT_STATUS.
- **11–12 Nov 2026, owner:** remove NOVA's hidden grid completely (D4). Deletion-step logging goes in the next
  version (D8). **Codex is unavailable for now** (owner out of Codex usage): do not assign it work; Claude does the
  engine/data jobs too until the owner says Codex is back (briefs D and any "for Codex" items become Claude's).
  Fix match: Claude proposes one Fix match page everywhere (library long-press, Details › More, Files › Library Care)
  and retiring the 0.156 side panel; waiting for the owner's yes.
- **Decided (owner, 12 Nov 2026):**
  - **One Fix match, on Files.** Fix match from anywhere (library long-press, Details › More) takes you to Files ›
    Library Care, straight to the Fix match page for that title; Back returns to where you came from. The 0.156
    Fix match side panel is retired ("no point having two things that do the same").
  - **Search heading for related streaming titles: "From the world of <title>"** (works for films and series: From
    the world of Yellowstone / Terminator / Dune / Dexter). Person searches keep "More from <person> on your services".
  - Tab style (chip vs underline) and Home row text (inside vs below the picture): both to be mocked up and decided
    then.

## 23. Quick decisions (owner, 12 Nov 2026)
1. Search headings: "From the world of …" (fiction), "Related to …" (non-fiction), "More from …" (people), under one
   "On your services" label.
2. When Android has ended the app, it reopens on the page you were on with focus restored; a first launch after a
   reboot opens Home.
3. Back in the middle of a library page: first Back jumps to the top (focus on the first poster); second Back goes to
   the top bar.
4. No automatic scan within 15 minutes of the last one finishing; one scan at a time; Scan now always allowed.
5. Deleting a file: one clear warning in Supernova's own design; **no Android screens** in the flow. (The app does not
   hold "All files access", so Android 11 forces its own consent screen for local files; Claude proposes asking for
   All files access once so deletes, and the folder pickers, stay inside Supernova. Awaiting the owner's yes.)
6. **Continue Watching:** a title you have started leaves Recently Added (owner believes this was the behaviour
   already: treat as a bug). Promote promptly, most recent first.
7. One "Release date" sort everywhere (air date for series); the separate Year sort goes.
8. Hide Watched setting replaced by a filter on each library page (All · Unwatched · In progress · Watched) and a small
   tick on watched posters.
9. Service logos in full colour app-wide.
10. Diagnostics kept 2 weeks by default (1 / 2 / 4); export folder chosen once, then reused.
12. Documentation website parked.
13. **Legacy clean-out approved for the SAFE list only** (includes removing NOVA's hidden grid).
15. Media Transfers: wanted; stages to be agreed (Claude's proposal 12 Nov: copy → destinations and resilience →
    schedule → move → automation).
- Note: "Rescrape all / all movies / all collections" still exist in the code but are not shown in 0.156 Settings
  (they were to become "Refresh details" in Library Health). Any re-check of matches must show changes first and
  protect manual fixes.
- **12 Nov, more decisions:** (5) **No Android screens:** do whatever is needed — ask for All files access once; deletes,
  the fonts folder and the diagnostics folder all use Supernova's own design and Files browser. (11) Title logo and
  episode line appear with the age rating at the start, then fade together. (14) The re-check of matches lives on
  **Files › Library Care** (not Settings): shows changes first, never touches manual fixes; replaces the hidden
  Rescrape options.

## 24. Media Transfers — shaping (owner and Claude, 12 Nov 2026; needs mock-ups; not yet scheduled)
- Stages and rough share of the whole: 1 Copy with a Transfers page, verification and same-title identity (~40%);
  2 Robustness: default folders, free space, resume after restart/power cut, start from Details / long-press / Files
  (~25%); 3 Schedule: overnight window plus Start now (~10%); 4 Move: delete the network original after a verified
  copy (~10%); 5 Automation (~15%).
- Transfers run while the app is in use, including during playback; slow down or pause automatically while playing
  from the network so playback never stutters. Running while the Shield sleeps is the main technical risk: test early.
- Playback itself streams (reads what it needs, keeps nothing), so it is not a download.
- **Automation as a per-series switch (owner's idea):** Details › More › e.g. "Bring new episodes to this Shield". The
  series is flagged; whenever a scan finds a new episode of it on any connection, it is copied, verified, then (if
  chosen) the network copy is deleted. It follows the series, not a folder, so it works when nothing is left on the
  network. The Transfers page lists the series that have it on.
- **Media Transfers, revised stages (owner, 12 Nov):** default folders and the free-space check belong in stage 1.
  1 Copy: choose/default destination folders (films, series), space check, Transfers page, verification, same-title
  identity (~50%); 2 Resilience: carry on after restart / power cut / dropped connection, resume part-copied files,
  start from Details and long-press as well as Files (~15%); 3 Schedule (~10%); 4 Move (~10%); 5 Per-series
  "bring new episodes" with a way to switch it off from Details › More and from the Transfers page (~15%).
  Sleep: the owner has run a Plex server on this Shield while it slept, so background work is known to be possible;
  still verify early.
- **Media Transfers plan agreed (owner, 12 Nov):** first build = stage 1 (copy), stage 2 (resilience) and stage 4
  (move: delete the network copy after a verified, correctly matched transfer; **off by default**, small job since
  copy, verification and deletion already exist; handle failed deletes and empty folders). **Later:** stage 5
  (per-series switch, one item in Details › More that reads "Bring new episodes to this Shield" / "Stop bringing new
  episodes") and stage 3 (schedule, only if wanted; transfers already give way to network playback). Needs mock-ups
  (Transfers page, per-title action); likely a fourth Files tab.
- **Media Transfers versions — decided (owner, 12 Nov 2026):** **0.157:** copy (folders, space check, Transfers page,
  verification, same title), resilience, and delete-after-verified-copy (off by default). **0.158:** the per-series
  "Bring new episodes" switch **and the schedule (the owner does want it)**.
- **12 Nov, more answers:** coding start: on hold while the owner decides about Codex. Re-check of matches: approved as
  proposed. Smarter hero backdrops (calmer/darker pick; hero and rows may use different artwork): simple first version
  in 0.157; test titles from the walk-through (Heart of the Beast, Mr. Robot, High Potential, Michael, Practical Magic
  2). "Still watching?": offer both episodes and time. JustWatch row: plain credit line, nothing to click. Bring `main`
  up to date with 0.156 when coding starts.
- The dead WebDAV connection / missing files: the owner deleted watched files on the Put.io website because deleting
  in the app was awkward. The library should notice files removed elsewhere at the next scan and tidy them away
  (keeping watched history) without errors.
- **Releases page (owner wants one, private):** GitHub Releases can hold each version's notes and signed APK.
  **Found 12 Nov: the repository is public** (and is a GitHub fork of `nova-video-player/aos-Video`, which GitHub does
  not allow to be made private directly). Options put to the owner; decision needed before publishing any release.
- **Privacy clean-up (owner, 12 Nov 2026):** the Put.io account name and real storage figures replaced with made-up
  ones in every mock-up script, the re-drawn pictures and one test; the owner's email removed from four old signing
  logs (it was the signing certificate's contact field). Older commits still contain them until history is rewritten
  or the repository is made private: owner to decide. Rule added to CLAUDE.md.
- **Privacy, continued (owner, 12 Nov 2026):** location references removed from the current documents (24 files,
  including old planning notes and the signing logs' certificate fields). Not yet done: (a) film and series titles
  in notes and mock-ups (they are in hundreds of places and identify the bugs); the owner leans towards making the
  repository private, which covers them; scrub before any return to public; (b) the app's default Where to Watch
  country is hard-coded in the code: change to the device's region in 0.157; (c) old commits still hold everything.
  The owner may release the app publicly one day: keep a "going public" checklist (history, titles, certificate
  contact, licence notices).

## 25. Focus and "where am I" — direction (owner, 12 Nov 2026; mock-up next)
- One brightness scale that follows the path: **white box = where the remote is** (artwork: the card grows);
  **bright white underline = one step back** (the open tab / page you came through); **dull underline = two steps
  back**. E.g. Settings: remote on Put.io row → row boxed, Accounts tab bright underline, cog in the top bar dull
  underline. Applies to the top bar and every set of tabs. Moving along tabs switches the page; the top bar switches
  after a short pause, so a row's box always sits on its open item. Never more than three levels on screen.
- A written breadcrumb under the top bar on deeper pages ("Movies › Marshals") remains an option alongside.
- Replaces both the grey-chip and the plain-underline tab mock-ups (`tab-style/01–07`).
- **§25 approved (owner, 12 Nov 2026):** read correctly from the pictures alone. Focused artwork: solid thick white ring
  plus grow, with room around it. Recorded as app-wide rules in CLAUDE.md "Global look" (replaces the grey-chip tabs
  and "focus = grows only").

## 26. Search — final pass (owner, 12 Nov 2026; approved; mock-ups `search-flat/17`–`21` v5)

Replaces the heading wording in §22 and §23.1. Approved by the owner.
- **Clear history** (not "Clear", which the keyboard already has): plain words with a small ✕, no box until focused.
- **Every section heading has an icon:** Recent searches (clock), What you can search (light bulb), In your library
  (books and films), streaming section (cloud). Recent search rows use a magnifying glass.
- **Sections are separated by a bigger gap, not a line.** No line under a list's last row.
- **Lines only under rows you can select.** The "What you can search" tips are information, so they have no lines.
- **Streaming headings, one line starting with "Stream":** "Stream more from the world of Yellowstone" (fiction),
  "Stream more related to Planet Earth II" (non-fiction), "Stream more from Taylor Sheridan" (people).
- **Streaming pills use the Details open-in-app icon** (box with arrow) and the service name; OK opens the title in that
  app, the same as Details.
- **Library pills show where you are, in the Details wording:** "Resume S5 E3", "Watched", "Unwatched" (no "In
  library"). OK on a library result opens its Details page.
- Focus: on a results screen the remote is on a result; the keyboard shows no focus.
- **Heading spacing (owner, same day; app-wide rule in CLAUDE.md):** more space between a heading and its results, so
  the heading reads as a heading, not part of the list: about 26 px below a heading, 64 px above it.

## 27. One design rulebook, one keyboard (owner, 12 Nov 2026)
- **Search v5 approved** (`search-flat/17`–`21`), with the heading spacing.
- **Design rulebook:** every agreed rule gathered in `docs/design/RULES.md`, so "redesign this page" can be built
  from it and only new questions go to the owner. Rules are tweaked, added and removed as pages are designed.
- **One keyboard everywhere.** Every text box uses the same keyboard (`PreviewKeyboard`, already shared in code), with
  the same layout and focus behaviour; a change or fix made to it applies to every keyboard in the app. Focus starts
  on **T** (the most common first letter of titles) and **stays on the key just pressed**, however fast you type.
- **Bug (owner, Shield):** Details › More › Change info of all episodes (the 0.156 Fix match panel): after typing a
  letter, focus vanishes; Up, Down and Left do nothing; Right lands on "1". On Search the same keyboard keeps focus on
  the key pressed (H, E, L, L, O typed quickly). Likely cause: the old NOVA search screen still runs hidden behind the
  panel and takes focus when results arrive (the same pattern as NOVA's hidden grid). The panel is being retired
  (§22), but the fix belongs in the shared keyboard too: if focus is lost after a key press, it returns to that key,
  so no screen can break it.
- Mock-up correction: the Search pictures drew `-` and `' .` keys and started on M; the real keyboard has `/` after L
  and `. @` after M and starts on T. Redrawn.
- **No hidden NOVA screens (owner, 12 Nov 2026):** rule approved — no NOVA screen may run hidden behind a Supernova
  screen. When coding starts, one sweep finds and removes them all (each with a build and tests), beyond the SAFE
  clean-out list; anything not obviously dead is listed for the owner first. In `docs/design/RULES.md` §3.
- The four open questions in RULES.md are answered when the related mock-ups are made, not now (owner).

## 28. Files — re-tick done (owner, 12 Nov 2026; mock-ups `network-files-tabs/33`–`41`)
The approved Files design (§16) with every rule in `docs/design/RULES.md` applied, plus the new pieces. **Done.**
- **Top bar:** Files stays on the right. One signal slot on its left: scan ring while scanning, else a white dot when
  something needs attention, else nothing.
- **Tab row, right side:** the open tab's facts ("1,808 Videos · 9 To Check"), ending with "⟳ 12 min ago" — the scan
  control on every Files tab (Right from the last tab; OK = Scan now, Stop while scanning). While a scan runs it reads
  "Scanning Put.io · 42%". No separator between tabs and facts: the guaranteed gap and the dots do it.
- **Library Care:** first section renamed **"To check"** (matches the summary and the badge), with about 48 px under
  the tab line. Rows: Missing files, Couldn't be read, Not matched, **Check matches** (new). The badge and "To check"
  count everything waiting (problems plus suggested changes). Section **Scanning:** Scan one source (the focused row
  shows one button per source), then Recent scans as approved (last three, Show all scans). The old Scan now row goes.
- **Check matches:** results page (breadcrumb "Check matches"); each row old → new with the reason and a Movie / TV show
  pill; focused row Accept · Keep current · Fix match; "Accept all" as plain words on the heading; "Left alone" lists
  titles fixed by hand, which checks never change.
- **Transfers (fourth tab):** Copying now (progress, Pause · Cancel), Waiting (gives way to network playback),
  Finished ("Copied and checked"), Where copies go: two rows **Movies** and **TV Shows**, each opening the folder
  picker; "Remove the network copy" switch, off by default.
- **Folder picker (one for the whole app:** copy folders, fonts folder, diagnostics export): heading states the task
  ("Choose a folder for Movies"), one breadcrumb for the location; "Current" pill; focused folder Use this folder ·
  Open; New folder at the bottom.
- **Delete:** one warning in Supernova's design, left-aligned; two switches, off by default: Remove from Supernova
  history, Remove from Trakt history (only when Trakt is connected; Trakt must not re-add what was removed);
  focus starts on Keep file. Approved ("I like having options for things rather than just standard answers").
- Wording: Movies / TV Shows as in the top bar (pills "Movie" / "TV show").

## 29. Details — tabs and metadata row for all four kinds (owner, 12 Nov 2026; decided)
- Details opens for four kinds: movie, TV show, season, episode (episodes do have their own page; streaming-only
  episodes not in the library have none: OK opens the service, or asks which when more than one has it).
- **Tabs: one master order, each tab shown or hidden, never moved:** Details · Episodes · Extras · More Like This.
  Movie: Details · Extras · More Like This. TV show, season and episode: all four. "Episodes" is the one name (replaces
  "Seasons & Episodes"; on a show it holds one row per season). The page opens on the most useful tab (TV show and
  season: Episodes; movie and episode: Details). Replaces the 5 Nov series order (Seasons & Episodes first).
- **Extras:** most specific first — an episode's own videos, then its season's, then the show's (TMDB has season and
  episode videos, patchily). **More Like This** on season and episode pages uses the show's suggestions.
- **Single-season shows:** no season page; the Episodes tab lists the episodes directly and the season heading is not
  clickable.
- **Metadata row, one order everywhere (Home and Details): Age rating · Year · Length · Genres.** Age rating in a small
  outlined badge; Length = running time (movie, episode), seasons (show), episodes (season); genres in TMDB's order,
  three at most; anything missing is left out of the row (the Details tab still says "Unknown"). Season and episode
  ratings come from the first available episode, else the show's.
- **Title line** under the logo for seasons and episodes, the same as the player: "Season 2" /
  "Season 2 · Episode 8 · We Don't Talk Anymore".
- Still to see in pictures: the Details tab under the new heading and line rules, the artwork picker, and the More
  menu's final contents.
- **Owner on v18 (12 Nov 2026):** section headings with icons (no underline) approved; title line and metadata row
  approved; anything not mentioned is approved. Changes: hero buttons sit in a **fixed place at the bottom left** of
  the hero (never move with the text); description **two lines** on Details as on Home; More › Full description opens
  a **pop-up sized to the text** (usual pop-up design, left-aligned, Back closes); **path underlines only on tabs and the
  top bar** (a button that opened a menu returns to its resting look); artwork picker chooses **by place: Hero · Rows ·
  Library page** (backdrops for Hero and Rows, Rows first offers "Same as hero"; posters for Library page), as a side
  panel sliding in from the right with the hero previewing. Open: line structure on the Details tab
  (`details-background/18`: none / row lines / row and column lines). Mock-ups `details-background/15`–`20`.
- **Details re-tick done (owner, 12 Nov 2026).** Final changes: Details tab uses **faint row lines** (middle version of
  `details-background/18`; no column lines); Full description pop-up mirrors the hero, no icon: title, then the title
  line (seasons/episodes), then the text; artwork picker **Choose for: Hero and rows · Library page** (one picture for
  hero and rows, posters for library pages); the picker has **no title** (opened from a named item).

## 30. Player — re-tick (owner, 12 Nov 2026; mock-ups `player/01`–`07`)
- **Title block** (logo, title line, metadata row, as on Details) in one fixed place, upper left; never moves when the
  controls slide up for Chapters / Episodes. **Preparing playback** shows the identical block in the identical place,
  with the app's ring spinner (the same as the scan ring on Files) and "Preparing playback…" under it; the old spinner
  goes.
- **Age rating with reasons** ("15 · Strong language, violence") alone in the top left **once, as playback starts**
  (straight after Preparing playback): fade in, fully shown for 8 seconds, fade out. Not shown again until playback is
  left and started again (the "after a long pause" idea is dropped). Afterwards the title block's badge is enough.
- Chapters / Episodes are a tab row with the line under it. Chapters renumbered after merging IntroDB parts (Recap ·
  Chapter 1 · Intro · Chapter 2 …), every card with a picture; one card size; focused card grows with the ring.
- Soft full-width dark fade behind the controls (no blur); play/pause and Settings keep the dark rest box, no outline.
- **Seek previews:** the controls group moves down into the spare space below the strip; the time bubble holds the
  preview picture with the time and chapter beside it (loading ring while it fetches).
- **Playback Settings, tabs in order of use: Subtitles · Audio · Playback** (opens on Subtitles). Every current setting
  kept, in sections with icon headings:
  - Subtitles — *Subtitle selection:* Subtitles on/off, Subtitle track ›, Download subtitles ›, **Subtitle delay
    (moved here: timing is judged against the real subtitles)**; *Subtitle appearance:* Text size (chips), Text colour
    (colour dots), Vertical position (− +), Outline (switch), Background (switch), Background opacity (chips); *For this
    series:* use these subtitles for the whole show. On any appearance row the real subtitles hide and a sample line
    shows the change.
  - Audio — *Audio selection:* Audio track ›; *For this series:* use this audio for the whole show; *Audio adjustment:*
    Audio delay (− +), Audio boost (chips), Night mode, Spatial audio (switches).
  - Playback — *Picture:* Format (chips; picture in picture removed); *Playback:* Speed, Stop playing at, Still
    watching?, Play mode, Skip intros and credits (Off · Show button · Automatic).
- **Player v4 (owner, 12 Nov 2026):** controls moved lower; the seek preview is a card like the row cards (time and
  chapter inside on the shade); running time and "Ends 18:14" back between the bar and Settings; panel section headings
  as words plus a line (menu style); colour row: one ring marks the chosen colour, Left/Right moves it; Stop playing
  at offers now + 30 / 60 / 90 min (rolling) and End of episode / End of movie; Still watching? on movies shows only
  the time choices (an episode choice falls back to 3 hours); **Play mode: Stop after this · Play next · Repeat this ·
  Repeat all** (Folder and Binge watching merged into Play next — approved; no Shuffle).
- **Player v6–v7 (owner, 12 Nov 2026):** Chapters / Episodes tabs always shown under the bar (cards only when focused);
  positions approved. **One loading ring in the centre of the screen** whenever playback waits (seek, network),
  replacing the ring beside the time; the time box shows only the time over the chapter name, and the preview picture
  uses that same layout inside it, centred over the dot; seek dot 50% larger. **Menu and panel group headings
  everywhere: centred on their line, Title Case, a little larger** (Playback Settings, More menu, long-press…).
  Mock-ups get a light frame so their edge shows.
