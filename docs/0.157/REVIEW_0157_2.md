# 0.157.2 on the Shield: the owner's review (18 Nov 2026)

Dictated in Notes while walking the checklist (`docs/0.157/review-0157-2-checklist.html`), pasted 18 Nov, with the
diagnostics report exported 18 Nov 15:43 local time. Numbered by page: PP Preparing playback, PL Player, PS Playback
Settings, DT Details, FL Files, R elsewhere, X diagnostics. **Becomes 0.158 part B.** "[ ]" = Claude's recommendation
where the owner must decide.

## Preparing playback
- **PP1. Age rating pops in late** and pushes the lines apart when it arrives. Keep its space from the start.
- **PP2. The badge sits alone on its own line** with no reasons (reasons are rarely found). Owner: put it inline:
  `[18] · Season 2 · Episode 3 · Title` on one line under the logo. **Decided (owner, 18 Nov): the badge is always the
  first item of the details line, everywhere (Preparing playback, player, Details); no reasons shown** (they are found
  too rarely). Replaces player v11's own line.
- **PP3. Title block not in the top left** as on the HUD. Owner unsure the two need to match (playback starts with
  nothing on screen, unlike Home hero → Details). [Keep them identical anyway: one block, one place, no second design
  to maintain.] **Agreed (owner, 18 Nov).**
- Working: the Supernova rings on Preparing playback ("love it").

## Player
- **PL1. Title block** top left with the clock top right: good. **Same contents as Preparing playback**, badge
  included (PP2).
- **PL2. Clock:** same font change as the 0.158 top bar (regular weight, same size).
- **PL3. Gaps:** controls to the Chapters tab line −30%; tab line to the row −35%. Today the controls sit about
  40–45% up the screen with Chapters open; the two cuts bring everything down.
- **PL4. Controls linger** looks wrong when focus is on the seek bar (fine on Settings and the tabs). Check the 3 s.
- **PL5. A 2-second first chapter:** "Episode · From 0:00", then "Recap · From 0:02". [Merge any chapter under 10 s
  into the next.] **Agreed (owner, 18 Nov):** no "From 0:00" stub chapters.
- **PL6. Chapter and Episode cards lack the artwork edge** at rest (Home rows and library posters have it). RULES §14:
  every card has the edge; focus thickens it.
- **PL7. Seek previews don't work** and the switch can't be found in Settings. The seek card shows the chapter's
  picture, or grey. Diagnostics: 109 preview errors (X3). The switch exists: Settings › System › Advanced › "Seek
  previews (experimental)" in the code, but **it isn't there on the Shield** (owner checked System › Advanced, 18 Nov):
  the code lists it under Advanced › Video Compatibility, yet the page doesn't show it. Bring it back; fix the engine.
- **PL8. Skip button** is larger than Settings and doesn't sit in line with the controls. Approved (`player/29`, `30`):
  alone on screen it sits in its place; with controls shown it joins their line and the seek bar shortens.
- Working: soft dark fade behind the controls; seek card no longer cut off by subtitles; Episodes row; Chapters have
  names and pictures; focus ring on cards.

## Playback Settings
**Owner (18 Nov): the worst of the three; fix it first in part B.**
- **PS1. Tabs use the new top bar focus (owner, 18 Nov: approved):** white box on the focused tab; once focus goes
  down, the open tab keeps a soft grey box with faded text; no underlines.
- **PS2. Tab line and the first heading's line sit too close** (two lines together). Same fix as Files: a clear gap
  under the tab row (RULES §7b).
- **PS3. Every tab fits without scrolling** (Subtitles, Audio, Playback): tighter rows, not tiny text.
- **PS4. Switch on a focused row:** today white track and black knob, with a thin black ring on focus. Flip on focus
  like the text: dark track, white knob (RULES §3). Same on Outline, Background, For This Series, Audio's switches.
- **PS5. Track rows:** big gap between "English" and its first pill; on focus the pills lose their outline (only the
  text turns black). Pills sit close to the name; on a focused row the outline turns black too.
- **PS6. Chips on a focused row** (Text size): the chosen chip goes darker grey instead of black with white text
  (RULES §3).
- **PS7. Vertical position:** at 10% and above the subtitle slides behind the picture (a fifth visible) until about
  85%, where it reappears in the top black bar. Subtitles must stay on top at every position.
- **PS8. Track list on top:** the panel behind shrinks by about a quarter and changes width. Approved: darkened and
  pushed back slightly (about 96%), same width.
- **PS11. From the owner's photos (18 Nov), "not structured" explained:**
  - Rows mix three kinds of control: chips (Speed, Text size), ‹ value › steppers (Format "‹ Original ›", Still
    watching? "‹ Off ›", Vertical position "‹ Raised 5% ›", Background opacity "‹ 75% ›") and a stray "‹" before the
    track names. Approved: chips (Format Fit · Fill · Zoom · Stretch; Still watching? Off · 2 episodes · 3 episodes ·
    2 hours · 3 hours; Background opacity 25 · 50 · 75 · 100%), − + for Vertical position, › for rows that open a list;
    no ‹ › arrows (RULES §8).
  - **Two rows have no name:** Stop playing at (moon icon only) and Play mode (icon only). Play mode's last chip is
    cut ("Repe…"); "Skip Intros…" is cut short.
  - **Audio boost is a switch;** approved chips Off · Low · Medium · High. Text size lacks Extra large.
  - **Subtitle delay sits at the end of Appearance;** approved in Subtitle Selection (timing is judged against the
    subtitles). For This Series wording: "Use these choices for Dexter: Resurrection" plus a help line; approved "Use
    these subtitles for all of …" (Audio: "… this audio …"), no help line.
  - **Focused rows:** the name turns thin and grey instead of black at the same weight; pills lose their outline (PS5);
    switches as PS4.
  - **Download subtitles list:** has a "‹ Download Subtitles" header (RULES §9: none), a blue-outlined search box and
    a white outline as focus (RULES §3: white box, black text); raw file names; the panel behind isn't darkened.
  - **Track list on top (PS8 confirmed):** the panel behind collapses to a short clipped strip.
  - **Panel edge (P6 confirmed):** a light outline round the panel; over video, none.
- **PS9. Playback tab "not very structured":** after PS1–PS6, look again with a photo.
- **PS10. Panel colour** (roadmap To discuss, P6): still different from the mock-up.

## Details
- **DT1. Details tab still scrolls** to reach Cast and Crew (D18 not met on the Shield). B2: everything on one screen.
- **DT2. Tabs use the new focus** (PS1), as Playback Settings.
- **DT3. Invisible frame still clips** focused cards at both sides on Episodes; rows should run off the edges (D6).
- **DT4. Gap from a season's row to the next season's heading** −50%.
- **DT5. One row design for Episodes, Extras and More Like This** (heading, icon, spacing, card). Owner idea: More
  Like This in rows by service (Netflix, Prime Video, Disney+), plus In your library. Mock-up `review-0157-2/01` 1–2.
  **Agreed (owner, 18 Nov):** the service's official colour logo as the row heading's icon.
- **DT6. Artwork:** More › Artwork opens a sub-menu in the top-right corner, then the old grid. Approved: the side
  panel (D3). Artwork, Posters and Backdrops have placeholder icons.
- **DT7. Full description pop-up (owner, 18 Nov):** only the text: no title, no Close, no Copy. Replaces RULES §10's
  "Full description keeps its heading".
- **DT8. More menu has no Fix match** (D2).
- **DT9. From the owner's photos (a movie, 18 Nov):** rows on the Details tab are tall (about twice B2's), so Cast and
  Crew fall below the fold; cast portraits are smaller than crew portraits (one size for both); names cut short ("Hugh
  Jack…"). Values: "2h 3m" (approved "2 h 3 min"), "$25,000,000.00" (whole numbers: "$25 million"), Subtitles "ara"
  (a bare code: "Arabic"). The focused tab's word is grey on the white box (RULES §3: black). Content scrolls up under
  the tab line (as FL1).

## Files
- **FL1. Content scrolls under the breadcrumb, toolbar, tabs and top bar** and stays visible (folders, Library Care,
  Your Library). It must clip below the toolbar / tab row.
- **FL2. "Looking in this folder…" text** replaced the placeholder cards that showed while a folder loaded (owner liked
  them). Bring back the placeholders (outline at once, filled as it loads).
- **FL3. Turn a connection off without removing it (owner idea; mock-up `review-0157-2/01` 3 approved 18 Nov; in the
  next version):** its titles leave the library, the settings stay;
  turn back on later. [A switch-like "Turn off" / "Turn on" button on the focused row; the row shows "Off".] Needs a
  mock-up; 0.158 part B or later.
- **FL4. Connection form scrolls** (Remember password, Test, Save below the fold). Fit it on one screen.
- **FL5. Open · Edit · Test · Remove take a lot of room.** [Smaller buttons first (row height and padding); if still
  crowded: Open and Edit on the row, Test and Remove on the Menu button.] Mock-up with FL3.
- **FL6. Found device names** end in "…" right against the "New" pill. Leave a clear gap.
- **FL7. Your Library › By library page** lists Movies and TV Shows but not your own page (Documentaries).
- **FL8. White dot beside Files with nothing to see:** no badge, every Library Care row "None". The dot shows only when
  a tab has something (its badge).
- **FL9. OK on Check matches moves focus to Missing files.** Focus stays on the row pressed.
- **FL10. Show all scans: focus jumps to Home**; the list can't be scrolled. [A read-only page like Your Library: Down /
  Up scroll, arrows show more, Back returns to Show all scans.] **Agreed (owner, 18 Nov):** focus stays on the page and
  moves through the scans; nothing to press.
- **FL11. Scan library:** yes, it scans this device and every connection. While scanning it reads "Scanning · 42%"
  and OK stops it (approved 17 Nov). Owner suggests an extra pill. [The button's own words already say it; keep
  them; make sure it never looks like a plain "Scan library" while running.]
- **FL12. App reopens on the last page** even after closing it fully. RULES §16: after **Android** ends it, reopen where
  you were; a first launch after a reboot opens Home. **Decided (owner, 18 Nov): closed fully → Home; only in the
  background → where you left it.** Replaces RULES §16's reopen rule.

## Elsewhere
- **R1. Title logos vs one font (owner idea):** mock-up `review-0157-2/01` 5 drawn; **still to discuss** (roadmap). Owner gathered examples (18 Nov): serif, block, glow; and nine styles
  (bold clean, spaced letters, two-line with a small spaced first line, outline, condensed…). **Owner (18 Nov):
  one consistent look for every title (no logo artwork), clean, no glow; likes "Modern block" best, and of the
  nine styles 1 Bold clean, 4 Two-line and 2 Modern bold.** Mock-ups `title-style/01` Home, `02` Details, `03` player, each
  with A Modern block · B Bold clean · C Two-line · D Modern bold. Two-line puts the part before a colon small and
  spaced on top; titles without a colon stay on one line. Owner to choose.
  **Decided (owner, 18 Nov evening): keep the official logo for now; develop the house style as the fallback for
  titles with no logo** (RULES §19 unchanged in substance). Tested on five of the owner's own titles, short to very
  long (picture kept out of the public repo). Text line breaks: after a colon, else after the last comma, else two
  balanced lines; at most two lines, shrinking if needed. **Logo placement is often wrong** (owner): the owner will
  send photos; likely causes are empty space inside the logo image, scaling to a fixed width, and no fixed anchor.
  [Trim the empty space, fit each logo to a box by its shape, pin it bottom-left on the details line.]
- **R2. Third library page:** a solid bar behind the top bar when scrolling (Movies and TV Shows blur). The same page
  code should behave the same (check after 0.158's library pages).
- **R3. Season Details pages have no artwork** (seasons 2 and 3 of one show; another show likewise). Use the season's
  poster, else the show's.
- **R4. Season More menu:** opens top right (old menu: "Series details", "Episodes"); no Delete season. Approved: the
  Details More menu beside its button; add **Delete season** (with the delete warning).
- **R5. Choose a version (owner idea; mock-up `review-0157-2/01` 4 approved 18 Nov; in the next version):** with two or more files for one title (4K, 1080p, sizes), Play asks which.
  [Play opens a small menu beside it: "4K · HDR · 18.2 GB", "1080p · 6.1 GB"; the last choice is remembered per title
  and marked.] Needs a mock-up.

## From the diagnostics report (exported 18 Nov 15:43; times local)
- **X1. The 16 Nov crashes found:** 22:54, 23:00, **23:09**, 23:12, **23:14** and 23:31, all the same fault in Files on
  0.157.1: leaving the network discovery removed a listener twice (`SambaDiscovery.removeListener`, "This is not a
  listener (anymore?)"). The same crash on 16 Nov 11:55. Not seen since 0.157.2. Fix: remove the listener once, and
  guard it. No record at 23:39 (the blank screen); the nearest event is the 23:31 crash.
- **X2. New crash on 0.157.2, 17 Nov 20:54:** Files › long-press › **Scan this folder** on a local folder
  (`FileUriExposedException`: the scan was sent with a plain file address Android refuses). Fix: send it the way
  Android allows.
- **X3. Seek previews fail every time:** 109 errors, a missing list in the preview engine (`Map.size()` on nothing); a
  few "retriever released". Explains PL7.
- **X4. Scans time out:** 73 failed scans (47 SFTP, 20 WebDAV, 6 local), mostly "scan lock wait interrupted" after
  about 5 minutes: scans waiting for each other. Plus 217 failed scan batches. Fix: queue, don't wait on a lock.
- **X5. Focus problems recorded:** 567 suspected (333 focus off the screen, 154 presses that went nowhere, 41 focused
  item clipped, 18 lost focus in the player). Matches FL1, DT3, FL10.
- **X6. Artwork:** 119 of 813 requests failed; metadata network failures 55.
- **X7. Smoothness:** 688 slow frames out of about 215,000; the worst pauses 1.0 s and 1.4 s on Home / library pages.
- **X8. Memory:** up to about 195 MB Java plus 293 MB native, well under the 750 MB seen before.
- No crash recorded by Android itself; 107 launches, 21 clean shutdowns (the rest were backgrounded or ended by
  Android).

## Mock-ups needed (before coding these)
DT5 (one row design; More Like This by service), FL3 + FL5 (connection off; buttons), R1 (logo vs font), R5 (choose a
version), and PP2 (the badge inline).
