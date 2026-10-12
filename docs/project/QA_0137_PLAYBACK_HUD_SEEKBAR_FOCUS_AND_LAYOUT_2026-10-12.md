# Supernova 0.137 playback HUD — seek focus and layout refinements

**Status:** User-approved next-version HUD refinements, 2026-10-12. Broader playback HUD walkthrough remains deferred. No implementation performed. User screenshot attached in conversation only; do not upload raw screenshot to GitHub/Codex without permission.

## Observed issue
- When seek bar is focused and timestamp pill appears, a large blurred white focus rectangle surrounds the entire lower HUD. This is incorrect and does not appear when focus is on lower controls.
- Remove the outer lower-HUD focus rectangle; use focused styling local to the actual seek control. Preserve D-pad usability.

## Requested layout
- Seek position timestamp **pill always visible while HUD visible**, tracking current/preview seek position. Handle play vs seek preview, boundary clamping and focus without flicker.
- Remove duplicated elapsed time text **under left end** of seek bar.
- Place **Play/Pause immediately left of seek bar**, aligned vertically with seek track.
- Place **Subtitles, Audio, More above seek bar**, in that exact order.
- Place **total duration at right end of seek bar**, on same vertical alignment as track, not beneath it.
- Maintain existing seek semantics, subtitles/audio/more actions, pause/play, auto-hide, focus navigation and accessibility; verify on actual Shield and different aspect ratios.
- Ensure always-visible pill does not overlap above buttons or clip at beginning/end. Use approved Supernova HUD typography and focus visuals.

## Future follow-up
- User intends to review the entire playback HUD later. **At the start of that discussion, remind user they previously said they have something to mention about subtitles.**
- This spec is for the stated fixes, not approval of any additional seek-bar redesign beyond them.
