# PARKED — Playback seek bar and end-time HUD refinement
**Recorded:** 2026-10-11
**Status:** DESIGN DISCUSSION / NOT AUTHORISED FOR IMPLEMENTATION
**Project:** Supernova (Android TV / NVIDIA Shield)

## Background
During playback Supernova currently shows an estimated programme finish time in the top-right corner. The user wants to explore moving that information into the seek bar region. This is a *specific seek-bar design discussion*, not approval of the previously parked full Playback HUD redesign. No APK testing or implementation requested.

## Agreed visual direction (latest mock-up)
- Preserve the existing **floating playback timestamp pill** positioned above the seek bar's current progress marker; the pill follows the marker. Example `22:17` = elapsed position, NOT the wall-clock time. Do not duplicate elapsed time as a fixed label at the beginning of the bar.
- Make that pill **slightly more translucent** than in the earlier mock-ups, retaining legibility.
- Place the **single combined Play/Pause control** in the same horizontal row **to the far left of the seek bar** (rather than underneath it, as in the existing HUD). Icon only; **no text label**. The icon should transition gracefully between play and pause states. Preserve accessible focus and remote-control semantics.
- A blue played portion and darker unplayed track, with the floating timestamp pill aligned to the progress marker.
- Seek bar may be shortened to make room for the information group on the right.
- Right-hand information arrangement from preferred **Option A**: `−20:55 / 43:12  |  [clock icon] Ends 9:14 PM`. `−20:55` is remaining duration; `43:12` is total duration; `Ends 9:14 PM` is the estimated wall-clock completion time.
- **All right-hand text must use exactly the same font family, size, baseline/alignment.** Differences in brightness/colour are acceptable only if they don't undermine that uniform typography. Separator and clock icon should align neatly with text.
- Keep right-hand timing information on **one horizontal line**, not stacked. Overall appearance: clean, restrained, dark/navy Supernova styling; avoid an oversized pill wrapping the entire right-hand group.
- Latest mock-up showed the icon-only Pause control far left, bar in centre, slightly translucent `22:17` pill, and uniformly sized right-side time information. Treat mock-up as *visual direction*, not pixel-accurate implementation dimensions.

## User feedback and design history
1. Initial proposals A–D had an unnecessary fixed elapsed-time label; user rejected the cluttered D.
2. User asked to combine remaining time and total duration into one information area.
3. User reminded us of the existing floating elapsed-time pill; fixed elapsed label should therefore be removed.
4. User liked **Option A's right-hand layout**, describing it as 'perfect'.
5. User asked to move the combined Play/Pause button from below the bar to the far left, inline with it.
6. User requested icon-only Play/Pause with graceful state transition, identical font/size/alignment across all right-hand information, and a more translucent floating timestamp pill.
7. Latest mock-up reflected these requirements; **final design has not been explicitly approved for implementation**.

## Still to resolve before coding
- Confirm exact positioning/spacing of left Play/Pause, seek track and right timing group in the actual Supernova HUD; use actual screenshot/approved visual baseline.
- Whether estimated finish time remains in the top-right as well or is removed there to avoid duplication (user's original goal suggests moving it, but not explicitly settled).
- Behaviour for live streams, unknown duration, seeking, pause/resume, playback speed and end-time estimate updates; gracefully hide unavailable values.
- Responsive behaviour on 720p/1080p/4K, larger fonts, TV overscan and focus expansion; ensure the floating pill stays visible near seek-bar ends.
- Validate D-pad focus/seek operation, icon transition and performance.
- Decide implementation release only after explicit user approval; no modification to 0.137 or draft PR #12.

## Resume instruction
When the user resumes, start from the **latest one-row mock-up** and these precise preferences. Discuss any final adjustments; do not silently revive the broad HUD redesign or assign work to Codex. Obtain explicit approval before adding to a release handover.
