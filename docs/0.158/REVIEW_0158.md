# 0.158 on the Shield: the owner's review (started 19 Nov 2026)

Numbered H (Home), L (library pages), S (Search), T (top bar), X (diagnostics). "[ ]" = Claude's recommendation.

## Home
- **H1. The Home hero and the Details hero are not the same size** (owner, 19 Nov). RULES §14 and HOME.md: one
  design, 1776 × 820 at 1080p, the same place on both pages, so the card stays put going into Details. Part A's report
  says they match; on the Shield they don't. Fix in 0.158 part B (first signing).
- **H2. No carousel dots under the Home hero** (owner, 19 Nov). Approved 5 Nov (option A): dots under the hero, the
  current one a longer pill, gliding to its new place when the hero changes (HOME.md). The code has
  `PreviewFeaturedIndicators`, but nothing shows on the Shield. Fix in 0.158 part B (first signing).

## Owner's notes, 19 Nov afternoon (0.158.3, then 0.158.4; dictated while Claude was down)
The owner will do the full walk-through when part B is finished; these are things seen meanwhile.

### Deleting (urgent bug)
- **D1. "Delete all episode files" on a show on SFTP went wrong** (0.158.3, about 14:33 local time). Show
  "MobLand": Details listed only episode 9 (watched), though episode 10 was also in its folder on the SFTP
  connection. Long-press › Delete all episode files → a message in the bottom right, "couldn't delete file"; a little
  later the show left the library. On the server episode 9 **was** deleted; episode 10 and the folder remained.
  - Files › Connections then showed the folder as **Excluded**, later **Not added**. Adding it to the library (twice)
    left the pill on "Not added", yet the show came back with **episodes 9 and 10**: episode 9 a ghost (its file
    gone, checked from another device).
  - Deleting episode 9 (about 14:42): Supernova's warning ("… is deleted from the … server. This can't be undone",
    Delete) → **an Android-style "Delete failed" notice** (old screen). Remove from library: a flash, then it went.
  - **Library Care › Missing files never listed episode 9**, though it was in the library with no file.
  - Not sure whether the new "earlier episodes" prompt appeared (that feature is 0.158.5, not in 0.158.3).
  - [Claude: several faults in one go: (1) delete all reported a failure when the file was deleted; (2) deleting
    excluded the folder, which it never should; (3) adding a folder again doesn't update its pill; (4) a library
    entry whose file is gone isn't caught as missing; (5) an old Android notice. Fix all five; one Supernova notice
    saying exactly what was deleted and what wasn't, and why. Diagnostics from the Shield needed (14:30–14:45).]

### Library pages
- **L1. Empty space under posters not started.** Captions: title · seasons/episodes (or year · length) · the sort
  line · then space kept for the progress bar, so unstarted titles have a blank strip. Owner's thoughts: drop the
  title (the poster shows it), or move the bar into the artwork (owner would rather not cover the poster).
  [Claude: a third way: the bar goes **inside the second line** of a started title ("▬▬▬ S2 E3 · 18 left"), so no
  line is kept empty and every caption is the same height. Keep the title (textless and foreign posters). Mock-up.]

### Rows (Home, Details, player): one design everywhere
- **R6. Home rows look square** with the caption box under the 16:9 picture; jarring next to the wide heroes. The
  same empty progress strip as L1 on unstarted items. Owner compared the player's Chapters row (text over the
  artwork) and, of the two, prefers text over the artwork. Wants **one row design for every row**: Home, Details
  (Episodes, Extras, More Like This), the player (Chapters, Episodes). Also wants a mock-up of **posters in Home
  rows**. [Claude to mock up: A text over the artwork (player style) · B caption only under the focused card · C
  posters; each with the bar as in L1.]

### Home hero
- **H3. One button, More** (owner, 19 Nov). Today Play and More: moving along the carousel first means stepping
  between them. Back to the original single **More** (its menu already has Play), so Left / Right change the hero
  at once. [Replaces HOME.md's "Play, then More". Menu order: Details, then Play / Resume, Library, Files & Media.]
- **H1 again:** on 0.158.4 the Home hero and Details hero are still **not the same size**.
- **H4.** Owner asked for the rules for what appears on the hero (answered in chat 19 Nov; see HOME.md).

### Details
- **DT3 again:** the Details hero's white edge is cut off left and right; a focused episode card is cut off on the
  left; More Like This on both sides.

### Settings
- **S1. Settings tabs are smaller than Files' tabs** (RULES §3: one height for every tab box), and Files' tabs keep
  the old underline while Settings has none.
- **S2. Should a tab's sections (General · Resume · Skipping · Picture) be sub-tabs?** [Claude: no; designed as
  one list with headings (approved 14 Nov); a third row of tabs adds a step for the remote.]
- **S3. No scroll arrows** on Settings tabs that scroll (RULES §16; Files has them).
- **A1. About is nearly empty** (0.158.4): the old **blue ring** icon, the version, "based on NOVA 6.6.4", build
  date. No What's New, no Credits, no Open-source licences.
- **A2. App icon and banner on the Shield's home screen** have charcoal behind the new mark: should they get the
  speckle? [Claude: the darker base `#111215`; grain at icon size reads as dirt; a TV look at both.]

### Files
- **FL2 again:** opening a drive still shows "Looking in this folder…"; bring back the placeholder rows.

### Player
- **P8. Playback Settings on 0.158.4: "9 out of 10, very nice".** A few tweaks to come with the walk-through.

### Top bar over the library pages (owner's screenshots, 0.158.4, 18:40 local; Drive › For Claude)
- **R2, still not fixed, and the intended look is now settled (owner, 19 Nov, firmly: raised several times).**
  **Movies and TV Shows are right:** scrolled down, the artwork runs on behind the top bar under its soft fade and
  blends behind the words. **Your own page (Documentaries) is wrong:** the posters stop under a solid dark band.
  No solid band on any library page. Recorded in RULES §2 and LIBRARY.md so it can't be lost again.

### Later notes, 19 Nov evening
- **H5. "Because you watched" is missing from Settings › Library › Home Hero** on 0.158.4 (Featured, Recently
  added, Trending, Popular only). [Claude: added in 0.158.5's work, so it arrives in 0.158.7; check then.]
- **H6. TV shows on the hero:** the owner's rule recorded in HOME.md (next episode only while watching the show);
  edge cases decided: unstarted shows stay candidates as the show, a new season counts, no time limit.
- **S1 decided the other way (owner, 19 Nov): Files' tab look is preferred.** The line under Files' tab row sets
  the tabs apart from the top bar; Settings' plain row of words with icons reads as a second top bar and looks
  unfinished. Settings' tabs take Files' look (size, line under the row, white focus box). Mock-up of the whole
  Settings page as it should be, when mock-ups resume. Details' tabs: left for the full walk-through (owner, 19 Nov).
