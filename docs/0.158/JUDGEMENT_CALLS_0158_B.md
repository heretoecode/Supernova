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
