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

- **P8 Play/pause symbol never changes.** The symbol at the left of the bar (an indicator; OK on the bar plays or
  pauses, as designed) stays the same whether playing or paused.
- **P9 Controls vanish the moment play resumes.** Too abrupt: keep them for about three seconds after resuming, then
  fade.
- **P10 Seek previews missing entirely.** No pictures on Put.io or local files, and Settings › System › Advanced has
  no "Seek previews (experimental)" switch. The Advanced layout lists `seek_thumbnails_enabled` (SettingsLayout) but
  the switch itself seems never to be created, so the page skips it. Fix both: the switch and the pictures.
- P10 cause of the missing switch (checked in the history): Claude's 0.156 commit `eccb17b58` added the switch to
  the settings file and the Advanced layout but not to `PreviewSettings`' list of keys moved into Advanced, so it is
  left in a hidden section. The missing pictures are a separate fault (engine by Codex, switch defaults on): to
  investigate.
- **P11 Picture in picture does nothing** when chosen. Owner decision: **remove picture in picture completely** in
  0.157 (never needed; awkward in other apps). Noted in `docs/REVISIT_LATER.md` to bring back if ever wanted.
- **P12 Playback Settings tab memory — decided (owner):** in panels whose tabs sit at the top and are reached by
  walking Up through the rows, Down from a tab always goes to the **first row** of that tab. Remembering a row there
  is pointless (it only remembers the route Up). Memory stays where you jump away directly (closing and reopening a
  panel, going to the top bar and back). Resolves audit F2 for these panels; undo the 0.156 per-tab row memory.
  Owner: the one place memory matters here is a row that opens its own list (Subtitle Track, Audio Track, Download
  Subtitles): on return, focus is back on that row.
- First-play tip: seen, works. Owner: it sits towards the top; should it be centred? Claude: yes, centre it.
- NOVA's blue "Press UP" box: not seen since the first install, so it was NOVA's first-run notice. Fine.
- Not yet tested: Still watching?, Sleep timer (running now), auto-next past hidden episodes, Put.io pause (owner
  testing now; diagnostics report to follow).
- Remote checked, as designed: Up reaches Settings, Down the seek bar, Down again the Chapters strip.

### Ideas and questions (owner), with Claude's view
- **Split "For this series" into two.** Today one switch on the Audio tab ("Use these choices for MobLand": audio and
  subtitles together). Owner: one for audio on the Audio tab, one for subtitles on the Subtitles tab. Claude agrees.
- **Track names as name plus pills.** Today: "Original | English (United Kingdom) (EAC3) · 5.1" and "Original |
  English (United Kingdom) (TEXT)", "… (SDH) - Hearing I…". These are the track's details: Original = the film's
  own language; EAC3 = the sound format (Dolby Digital Plus); 5.1 = channels; TEXT = text-based subtitles (not useful:
  drop); SDH = subtitles for the deaf and hard of hearing. Claude's proposal: the name in plain words ("English"),
  then small pills: Audio "Original" "Dolby Digital Plus" "5.1"; Subtitles "Original" "SDH" (or "Forced"). Flag stays.
  Mock-ups `docs/design/proposals/track-names/01`, `02` (0.157 panel look). **Approved by the owner (6 Nov).**
- **Colours of HUD buttons, time bubble, Chapters/Episodes buttons and Playback Settings:** yes, the 0.157 panel
  look (90% #16181C, no outline, focused item filled white with black text and icon) covers all of them. That is
  already agreed and separate from the page background question (0.157 notes §18).

- **New panel look on Playback Settings and Download Subtitles:** yes, the 0.157 look (90% #16181C, no outline,
  focused row filled white with black text and icon) covers both (0.157 notes §5).
- **Download icon on the right of each subtitle:** keep it on the right: the left is what the row is (flag and
  name), the right is what OK does, as on Files. Suggest showing it only on the focused row, to calm the list.
- **Inline choices instead of ‹ value ›:**
  - Playback speed: yes, five chips: 0.75× · 1× · 1.25× · 1.5× · 2×.
  - Play mode: **to explore.** Owner rule: one setting is one row; Left/Right goes through its choices, Up/Down
    between settings; chips never wrap and a setting is never split across two rows. Find a single-row form (shorter
    names, fewer choices) or keep a list if it cannot fit.
  - Still watching?: today it asks after 2, 3 or 5 episodes played untouched. Claude's passing idea: count time
    untouched instead (also covers films playing on from a folder). To look at in the mock-up stage.
- **Sleep timer:** drop the extra line and show the stop times as the choices: Off · 17:32 · 18:02 · 18:32 · End of
  episode, updating as the clock moves. Claude supports it; perhaps rename the row "Stop playing at".
- **Title logo in the same place:** the Preparing playback screen and the HUD put the logo in different places.
  Claude supports one place for it, matching the hero and Details (same idea as the card that stays put).
- **Same words on Preparing playback and the HUD:** Preparing shows the tagline ("Chaos reigns."); the HUD shows
  "Season 2 · Episode 8 · We Don't Talk Anymore". Claude suggests both show what is about to play: for an episode,
  season, episode and its title; for a film, its year (and tagline if wanted).
- **New (owner): age rating at the start of playback,** as streaming services do. Top left for about 6–8 seconds when
  a film or episode starts (and after a long pause), then fades: the UK rating (BBFC, e.g. 15, 12A) and its reasons
  where known (e.g. "strong language, violence"). Sits with the title logo. Nothing shown when there is no rating.
  The owner has asked for this before and it never appeared: Codex to check what rating data Supernova stores, why
  it is missing (likely the wrong country or not saved), and supply rating and reasons per title; Claude draws it. Needs a mock-up first (place beside the logo, size, reasons).
- **Title block (logo, information, age rating), Claude's proposal (needs one mock-up: Preparing and HUD side by side):**
  one block, top left, in the same place on Preparing playback and the HUD, lined up with the hero logo on Home and
  Details. Top to bottom: title logo; one information line, the same on both screens ("Season 2 · Episode 8 · We
  Don't Talk Anymore" for an episode, "1991 · 2 h 17 min" for a film); age rating chip with reasons. Preparing adds
  the spinner and "Preparing playback…" under the block. When the video starts the block stays put (artwork gives way
  to the picture). The age rating shows for the first 6–8 s, then fades; afterwards the block shows with the
  controls. Question for the owner: block stays up for the age-rating moment even if the remote is untouched (Claude:
  yes).
