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

## User-approved alignment and timing placement refinement (latest)

- **Play/Pause, seek track, and total-duration/end-time text share one horizontal centreline**. Do not align the track with the top or bottom of the neighbouring elements; validate geometry on Shield.
- Secondary buttons **Subtitles, Audio, More** remain in that order but are **left-aligned above the seek bar**, not right-aligned.
- On the right of the seek track, show **total media duration · Episode Ends HH:MM** on one line. For movies, use context-appropriate **Movie Ends HH:MM** rather than Episode Ends. Use middle dot separator `·`, comfortable gaps, same baseline/centreline as Play/Pause and seek track.
- **Top-right HUD contains only the current clock**, no Episode Ends/end-time text. Preserve the clock.
- Timestamp pill stays visible above seek track whenever HUD visible; remove duplicate elapsed time beneath seek track and large outer focus box.
- Calculate predicted end time from current playback position, remaining duration, current clock and playback speed where applicable; refresh appropriately on seek/resume/speed changes and avoid stale or misleading times. Decide reasonable paused-state behaviour via technical audit; do not invent static times.
- This latest approved layout supersedes earlier illustration placing the secondary controls on the right or end time in the top-right.
