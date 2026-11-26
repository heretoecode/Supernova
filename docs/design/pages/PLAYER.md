# Page sheet: Player and Playback Settings

The one place to build the player from (CLAUDE.md "Definition of done"). Written 25 Nov 2026 from the owner's review
(21–22 Nov). Mock-ups `docs/design/proposals/player-v16/tv/01`–`13` (Drive › From Claude › 2026-11-21 Player).
Rules: `docs/design/RULES.md` (§3, §6, §8, §9, §11, §14, §14c, §14d, §19, one tab behaviour, long lists). Where an
older player mock-up or the review notes differ, this sheet wins. Faults seen on 0.158.7 are listed at the foot.

## Title block (one component, shared with Preparing playback; `tv/09`, `tv/10`)
- Top left, at exactly the same place on Preparing playback and the player (test: bounds identical).
- **Anchored to the facts line**, which sits at the same height for every title; above it the episode's title line
  ("Season 1 · Episode 5 · Wind River"; films have none and **no tagline**), above that the logo inside 900 × 240
  (RULES §19; a two-line logo grows upwards).
- Facts line: age badge (drawn whole, never clipped; no dot after it) · year · length · up to four genres (long-lists
  rule). No reasons line.
- Clock top right, in the top bar's regular weight and size.

## Controls (`tv/01`)
- One line near the foot: pause / play · seek bar with chapter ticks · "12:06 / 46:12 · Ends 22:14" · Settings, in the
  52 px small-box shape. Soft dark fade behind the controls and the title block.
- **Seek preview** above the knob while seeking: the time box (time over the chapter name), with a picture only when
  the setting allows and the picture is ready. **Never a grey box.**
- **Skip button** (`tv/07`, `tv/08`): alone, at the right end of the controls' line; with the controls shown it takes
  the far right, Settings moves in beside it, the seek bar shortens.

## Chapters · Episodes (`tv/01`–`03`)
- Tabs: **the reference tab behaviour for the app**: focused = white box (52 px, sized to its word) clear of the line;
  inside a tab, a white underline under the open tab's word. Line under the tabs = the library toolbar line (white
  35%, about 2 px, fading at both ends). A film shows Chapters only.
- Cards: the shared row card (400 × 225, 40 px apart, focus grows 8%, no overlap), the artwork edge, title and one line
  over the foot, the corner badge (tick for watched, pie for the current), **no bars**. Chapters "From 7:13" (current:
  "Playing · from 7:13"); no stub chapters under 10 s. Episodes "S1 E4 · 46 min" (current: "Playing · 34 min left").

## Playback Settings (`tv/04`–`06`)
Panel on the right, about **700 px**, `#16181C` at 99% with the white outline at 70% and the dark shadow (also over
video). Tabs Subtitles · Audio · Playback with the shared tab behaviour. Rows 62 px, every row on one line, **every
value on its row's centre line**. Two choices = chips; more than two = Left / Right stepper (‹ › on the focused row
only); numbers − +; › for rows that open a list. Never scrolls.
- **Subtitles:** Subtitle Selection: Subtitles (switch) · Subtitle track (language + pills ›) · Download subtitles ›
  · Subtitle delay (− +). Subtitle Appearance: Text size (stepper) · Text colour (dots) · Vertical position (− +) ·
  Outline (switch) · Background (switch) · Background opacity (stepper). This Show: Remember for this show.
- **Audio:** Audio Selection: Audio track (language + pills ›). Audio Adjustments: Audio delay (− +) · Audio boost
  (Off · Low · Medium · High) · Night mode · Spatial audio (switches). This Show: Remember for this show.
- **Playback:** Picture and Speed: Format · Speed. Playing On: Stop playing at · Still watching? · Play mode ·
  **Skipping (Off · Show button · Skip, stepper)**. This Show: Remember for this show.

## Lists opened from the panel (`tv/11`–`13`)
On top of the panel (panel behind ~45% brightness, 96% size, the row you came from visible), no title, Back returns
to the row. Rows: flag (real locales) or globe, **language in words**, pills, tick on the right, all on one centre line.
- **Subtitle track:** Off · In The File (pills Original / SDH / Forced / region) · Subtitle Files (file name under the
  language, a .srt pill). OK chooses and closes.
- **Download subtitles:** Language ‹ English › · Results (release name over "Matches your file · n downloads"; pills
  Best match / SDH / Forced). OK downloads; it becomes the ticked track.
- **Audio track:** pills Original · format · channels · Commentary.

## Settings that shape the player
**Seek preview: Off · Chapter pictures · Video frames** (Settings › Playback). Off = the time box only.

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| OK / play-pause | Pauses or plays; controls show |
| Left / Right on the seek bar | Seeks with the preview; the loading ring in the centre while it waits |
| Settings | Playback Settings panel, focus on the open tab's first row |
| Subtitle track / Audio track / Download subtitles | Its list on top of the panel |
| Skip intro / credits | Jumps past it |
| A chapter card | Jumps to the chapter |
| An episode card | Plays that episode (Preparing playback) |
| Back | Closes the list, then the panel, then hides the controls, then leaves the player to where it was opened from |

## Never opens from the player
NOVA's old player menus and dialogs, its subtitle dialogs, Android's keyboard, YouTube's controls (Extras player).

## Faults seen on 0.158.7 (fix first; BRIEF_0158_B › Player review, Playback Settings faults)
Seek previews fail (grey box), and show even with the setting off; age badge clipped ("12A"); every Playback Settings
value half a row above its label; flag, language and pills on different heights; "chi" instead of Chinese; panel light
and see-through; focused tab's text grey; Audio boost still a switch; Spatial audio greyed with no switch.
