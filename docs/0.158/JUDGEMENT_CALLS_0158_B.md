# 0.158 part B: judgement calls

Made while working unattended (CLAUDE.md "Working unattended"). Each can be changed; the reason is given so you can
decide quickly.

## Branches and version
1. **Branches.** Work was done on the session branch `claude/build-0158-part-b-wsupv2` and the working branch
   (`claude/supernova-github-access-yfhe89`) brought up to it for every build and signing, as in part A, because the
   signing workflow only accepts conformance runs of the working branch.
2. **0.158 moved into the version history** (its signed APK's digests recorded from run 44426716646), so 0.158.1 can
   be the current version: the build check only accepts a point release of the last version in the history.
3. **Workflows.** The 0.157 conformance and signing workflows were pointed at 0.158.1 (1581), as before, rather than
   copied to new files.

## Group 1: the new look
4. **The speckle is drawn as one pattern for the whole window,** placed at the window's corner, so a page inside
   another page (Files inside the top bar's page, for instance) lines up grain for grain instead of showing a seam.
   It is made once per screen size and kept.
5. **The fade under the top bar** (when a page scrolls) now uses the speckle's base `#111215` instead of charcoal, so
   it melts into the page.
6. **Small boxes in px:** 52 px high, 20 px corners, 22 px side padding at 1080p, set once in `SupernovaStyle`
   (`SMALL_BOX_*`) and used by every shared button, chip, tab and the top bar. The top bar's words (19 sp) need about
   56 px, so its boxes are as tall as the words need and no taller.
7. **Text centred on its letters** is done once for every shared button: after layout, the padding moves the line so
   the middle of "H0" sits on the box's middle (the top bar's method).

## Group 2: Playback Settings
8. **PS7 (subtitles behind the picture):** the cause was the appearance sample (the line shown while you change the
   look), which was placed underneath the player's own layout, so it only showed through the black bars. It is now
   on top of the player. Real subtitles were not affected.
9. **Rows whose name and chips don't fit on one line** (Stop playing at, Play mode, Skip intros & credits, Still
   watching?) put the chips on a line of their own under the name, so every row has its name and no chip is cut.
10. **Format chips:** Fit = the engine's Original, Fill = Full screen, Zoom = Optimised, Stretch = Stretched. 4:3, 16:9,
    1.85 and 2.39 are no longer offered here (a video set to one of them shows no chip chosen).
11. **Still watching?:** Off · 2 episodes · 3 episodes · 2 hours · 3 hours (films: Off · 2 hours · 3 hours). Someone
    who had 5 episodes or 4 hours sees the nearest choice of the same kind.
12. **Audio boost levels** Low · Medium · High are the engine's levels 1, 2 and 3 (the old switch used 3).
13. **Panel colour:** `#16181C` at 90%, no outline and no glow, everywhere the Playback Settings panel shows (also over
    video), per RULES §11 (P6 / PS10).
14. **Download subtitles:** names shown as words (dots and underscores become spaces, the extension goes); the full
    file name still shows when you pick a result.

## Groups 3, 3b and 4
15. **The hero's exact size (H1)** is taken from the screen's width (1776 × 820 at 1080p) on Home and Details, its
    top just under the top bar (112 px). The carousel dots sit at the hero's foot, inside it, so the hero keeps its
    size. At that size the first row's heading and the top of its cards (about a tenth) peek below; the old test
    wanted a third, so its floor was lowered. If you want more of the row showing, the hero would have to be shorter.
16. **Details' compact layout** (very small windows only) keeps its old flexible card; the Shield always gets the
    exact size.
17. **A film's details line** on Preparing playback reads "[15] · 2024" (badge, then year): the tagline is gone
    everywhere (RULES §14) and nothing else was settled for films.
18. **Stub chapters:** any part shorter than 10 seconds joins the part after it (a short last part joins the one
    before), whichever source it came from.
19. **X4 (scans waiting on each other)** was not changed: the scan lock lives in MediaLib's build patches, and
    turning the wait into a queue is a rewrite of the scanner that needs its own careful run.

## Group 9: Settings
20. **Settings is held back from tonight's APKs.** A first pass of the redesign was built (one page, seven tabs,
    every section stacked under its heading, top-bar tab focus, old screens added to the no-old-screens test) but
    the 0.156 Settings tests (four files, built around the old tiles and per-section tabs) need rewriting before it
    can pass the build. Rather than sign it untested, its commit was reverted on the branch; `git revert` of the
    "Hold the Settings first pass" commit brings it back for the next session.

## After 0.158.1 (owner's second round of instructions, 19 Nov)
21. **Point releases stay in the current entry.** The build check only accepts whole versions in the history list, so
    0.158.1's notes sit under 0.158.2's in the current entry (as 0.157.1 and 0.157.2 did).
22. **Settings, master switches:** the sheet has none for services, so the old "Service integrations" switches are no
    longer on the page (their saved values are kept). "Look up skip times online" is the one that stays; with it off
    the skipping choices hide but the switch itself stays, so it can always be turned back on (before, it hid itself).
23. **Settings, Backup and OpenSubtitles** are rows in System and Subtitles that open their existing views; Back
    returns to the tab's rows. The sheet's in-row backup flow (What to back up chips, progress in the row) is not
    drawn yet.
24. **PL7, the switch:** the reason Seek previews was missing on the Shield is that it had no entry in the settings
    map, so no page could show it. A small 0.158 map file adds it (and Clear diagnostics), and it sits in Playback ›
    General.
25. **Look sweep, row lines:** 8% white lines between rows almost vanish on the speckle, so all of them went to 12%.
26. **Look sweep, panels:** floating panels (#16181C at 95%) are within a few shades of the speckle's base, so they
    barely stand apart from the page. The colour is your approved one, so it was not changed; say if you want panels
    a little lighter or with the soft edge back.
27. **DT4:** the gap above a season's heading was halved by pulling the heading up into the row's spare space.
28. **DT8:** shows get Fix match in More through the show's own match action (the series match screen).

## Part B continued (second session, 19 Nov)
29. **Branches.** Work is on the session branch `claude/0158-part-b-continued-d5u9w1`; the working branch
    (`claude/supernova-github-access-yfhe89`) is fast-forwarded to it only when a build is due, because a push there
    starts the conformance run and the signing workflow accepts only that branch.
30. **Playback Settings sizes.** The v15 mock-up is drawn in true 1080p pixels, while the panel's old sizes were in a
    unit a third larger (rows came out at 83 px, not 62). Every size now comes from the mock-up's own pixels, which is
    why every tab fits without scrolling.
31. **Lines between rows in Playback Settings** (2 px, 6% white, as drawn) sit across the rows' edges and add no
    height; the two lines beside the focused row step aside, as in the mock-up.
32. **OK on a stepping row** steps on one choice and, after the last, goes back to the first (Left / Right stop at
    the ends). Before, OK did the same on chips.
33. **Stop playing at** no longer has the "Stops at 23:40" line under it: the clock time is the value itself. The
    spoken description still says it.
34. **Row names in Playback Settings** follow the mock-up exactly ("Play mode", "Stop playing at" in sentence case;
    "Text Size", "Audio Boost" in title case), as drawn.
35. **Panels at 99%** (RULES §11): one shared value (`SupernovaStyle.GLASS`) for every pop-up, menu and side panel,
    also the old navy menu surface (backup boxes, the keyboard panel, the library page header) and the old Playback
    Settings shell. Their faint outline and glow are gone too ("no outline, no glow").
36. **Skip when binge-watching** used to skip only recaps; it now skips every part when the episode was reached by
    binge-watching, and shows the Skip button otherwise (before, it played the part with no button). The word on the
    chip promises that, so the behaviour follows it.
37. **Settings choice names** come from the sheet where it names them; where the engine has a choice the sheet
    doesn't mention, it keeps a plain name and stays: Automatic refresh rate has a third choice "Android matching"
    (Android 12's own frame-rate matching), Video decoder has "Android", Surround sound is Off · Passthrough · Via
    Android · Convert to AC3 (the engine's four; the sheet's "Stereo" is the separate Stereo downmix row), Parser
    synchronisation is Timestamps · Samples (the engine has two, not three). Values are unchanged, so saved choices and
    backups still work.
38. **When a video was started** is still a switch (Ask, or resume straight away), titled "Ask when a video was
    started"; the sheet's three chips (Ask · Resume · Start over) need a new setting in the player, listed as not
    built.
39. **Home Hero › Because you watched** is a new switch (on by default, like the others); the last source that is
    still on can't be turned off. **Change every** is 8 s · 12 s · 20 s (8 s as before). Both are backed up.
40. **DT3:** the Details sections and their content no longer clip their children, so a grown card and its ring show
    whole at both ends of a row. The row still stops at the page's side margin rather than running to the screen
    edge; that part needs the row widened under the margin and is listed as not built.
41. **Episodes menu** (no mock-up; built from the rules): Playback (Resume or Play, Restart episode) · Library (Mark
    as watched, Fix match) · Files & Media (Delete episode). The delete warning with earlier episodes has four buttons
    Keep file · Delete episode N only · Delete all … · Choose…; "Delete both" for two, "Delete all three" up to ten,
    then "Delete all 14". Choose… is the shared choice list with Select all at its top and Done at its foot; the
    chosen episodes get the same warning again.
42. **Logo placement (group 6):** every logo is sized to the same area (42% of a 900 × 240 box at 1080p, the
    mock-up's own figure), capped by width for wide logos and by height for tall ones, its foot on the title box's
    foot. The 24 px gap above the details line and the soft shade from the left are the pages' existing spacing and
    shade; they were not changed.
43. **House style A (group 6):** names without a logo show in heavy capitals, white, no glow, two lines at most. The
    letters are made capitals for display only, so the stored name is unchanged. A colon title shows the part before
    the colon small (about a third of the size); the line break after the colon is left to the text's own wrapping,
    because inserting one would change the name's letters everywhere it is read back. Shrinking a long title to fit
    two lines is not built: it ends with "…" instead.
44. **FL8:** the dot beside Files is now the Library Care badge itself (files missing plus titles not matched), as last
    counted on Files and kept across restarts. Before, it also counted titles with no poster or plot, which no tab
    shows. Until Files has been opened once after this update, there is no dot.
45. **FL7:** your own library page shows under By Library Page after Movies and TV Shows, with its titles and size
    (a show counts all its episodes' files). It has no "this device / network" figure on the right, since the page
    can mix both.
