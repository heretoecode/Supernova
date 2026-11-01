# Supernova 0.148 — playback HUD Ends at visibility and spacing

**Owner video:** `IMG_5077.mp4` (5.43 seconds), inspected on 1 November 2026. Frame at beginning shows playback HUD with duration `01:02:26` and **no Ends at** text; later frame shows `01:02:26 Ends at 13:17` immediately adjacent. Owner confirms disappearing state is **paused playback**. Original video is conversation-attached, not verified uploaded to GitHub.

## Approved fix for next Supernova version
1. When playback HUD is visible, **Ends at HH:MM must remain visible in playing and paused states**; do not condition its rendering on `isPlaying`. Preserve normal HUD auto-hide behaviour outside the visible HUD; user means persistent *while HUD is displayed*, not a permanent overlay during fullscreen playback.
2. On pause, retain a meaningful finishing-time estimate; on resume or seek, recalculate appropriately using remaining runtime, speed and current clock. Avoid stale or nonsensical times, and define honest fallback if duration/clock is genuinely unavailable.
3. Add a consistent **visual spacer/gap** between the total duration timestamp and the `Ends at HH:MM` label, including long durations, different font scaling, 720p/1080p/4K. Avoid overlap with the Settings control or seek bar.
4. Preserve approved single-row HUD, controls, focus and navigation; no redesign or mock-up requested.

## Codex acceptance
Investigate playback state-dependent visibility and layout. Add regression tests for playing→paused→resumed, seeking while paused, speed changes, HUD show/hide and spacing. Verify on signed Shield QA build; do not mark fixed before physical retest. Documentation only, no app code modified; PR29 stays draft and main unchanged.
