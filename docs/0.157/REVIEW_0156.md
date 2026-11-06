# Owner's review of 0.156 (from 6 Nov 2026)

Notes from the owner's Shield review, in the order of `docs/0.156/REVIEW_BY_PAGE.md`. Fixes and changes go into
0.157 (0.156 stays as signed). **BG** = depends on the Home/library background decision (0.157 notes §18); revisit
those once it is decided.

**Where the review has got to:** 1. Home › Hero (first item); 4. Player started (Playback Settings, HUD, strip).
**Order agreed (owner, 6 Nov):** the background (0.157 notes §18) is decided page by page first, then the visual
review. Until then the owner sends only points the background does not affect (behaviour, wording, content, focus,
the player).

## 1. Home

### Hero
- **Reviewed, fine:** title logo placement looks right on every hero and is the same on all of them; images clear.
- **New idea (0.157), Claude supports it:** going from a Home hero into Details, the card stays exactly where it is.
  Same size (already agreed, 1776×820 at 1080p), same picture, crop and logo position. Inside the card, More info is
  replaced by Play/Resume, service buttons and More, the description fades in and the carousel dots go. The Home rows
  slide away and the Details tabs take their place; Back reverses it and lands on the same Home title.
  - The Home card is the short version: going in, its tagline expands into the description (up to five lines, More ›
    Full description, as already approved).
  - Same picture throughout: Details currently shows the backdrop it was given, then may switch to the title's
    default backdrop when full details arrive. Build note: Details keeps the Home picture so the card never changes.
  - Home and Details must share a background. Today's rule (5 Nov) is Space Black for both; plain charcoal is still
    under consideration (0.157 notes §18). (BG)
  - The logo keeps its approved glide to the small logo when going down into the tabs.

## 4. Player (owner photos, MobLand S2 E8, 6 Nov 2026)

### Bugs
- **P1 Time bubble cut off.** The time and chapter bubble above the seek bar is cut off along its top. The cut sits
  exactly where the subtitles are pushed up to while the bar shows, so the subtitle layer is the likely cause (or the
  bubble's own container). Fix so the bubble is always whole.
- **P2 Chapter names make no sense.** The strip shows Recap (0:00) · Chapter 03 (1:06) · Intro (6:13) · Chapter 05
  (7:13) · Credits (41:48). Merging IntroDB parts replaces chapters 1, 2 and 4 but keeps the file's numbering, so
  numbers jump. The cards also have no pictures (plain grey), although 0.156 promised one per part.
- **P3 No sample subtitle while styling.** Playback Settings › Subtitles: the real subtitles show on Subtitles,
  Subtitle Track and Download Subtitles, vanish on Text Size to Background, and return on Subtitle Delay. On the
  appearance rows nothing is shown, so changes (e.g. Vertical Position) cannot be seen. A sample line should show there.
- **P4 Extra "Subtitle delay: 200ms" pill.** Changing Subtitle Delay in the panel also shows a pill at the bottom. It
  is the player's own notice (inherited from NOVA), not Android's. Not needed while the panel shows the value.
- **P5 Download Subtitles opens on top of Playback Settings.** Two panels stacked looks wrong. Subtitle Track and Audio
  Track replace the panel and Back returns; Download Subtitles should do the same.
- **P6 Download Subtitles: the search box shows no focus.** Up from the first result goes to it (OK opens the
  keyboard), but nothing shows it is focused. The focus around the "‹ Download Subtitles" back header is also off.
- **P7 No Episodes row on some series.** MobLand shows Chapters and Episodes; Marshals and Mayor of Kingstown (several
  episodes in the season) show only Chapters. On Marshals it seemed to appear briefly, then vanish.

### Ideas and questions (owner), with Claude's view
- **New panel look on Playback Settings and Download Subtitles:** yes, the 0.157 look (90% #16181C, no outline,
  focused row filled white with black text and icon) covers both (0.157 notes §5).
- **Download icon on the right of each subtitle:** keep it on the right: the left is what the row is (flag and
  name), the right is what OK does, as on Files. Suggest showing it only on the focused row, to calm the list.
- **Inline choices instead of ‹ value ›:**
  - Playback speed: yes, five chips: 0.75× · 1× · 1.25× · 1.5× · 2×.
  - Still watching?: yes: Off · 2 · 3 · 5 episodes.
  - Play mode: five long labels may not fit the panel's width. Try shortened chips (Single · Folder · Repeat one ·
    Repeat folder · Binge) in a mock-up; if they do not fit, keep a list.
- **Sleep timer:** drop the extra line and show the stop times as the choices: Off · 17:32 · 18:02 · 18:32 · End of
  episode, updating as the clock moves. Claude supports it; perhaps rename the row "Stop playing at".
- **Title logo in the same place:** the Preparing playback screen and the HUD put the logo in different places.
  Claude supports one place for it, matching the hero and Details (same idea as the card that stays put).
- **Same words on Preparing playback and the HUD:** Preparing shows the tagline ("Chaos reigns."); the HUD shows
  "Season 2 · Episode 8 · We Don't Talk Anymore". Claude suggests both show what is about to play: for an episode,
  season, episode and its title; for a film, its year (and tagline if wanted).
