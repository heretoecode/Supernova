# Next version — Preparing Playback artwork and playback HUD refinements

**Status:** User-reported visual issues, pending source investigation/implementation. **Target:** next version after 0.143.

## Evidence
User supplied screenshots of playback for *Three-Body* and a video `IMG_5045.mp4` (2026-10-28). Screenshots show bottom playback HUD close to display edge, and timestamp bubble hovering above seek bar. User reports Preparing Playback screen initially displays an unrelated/random textured image until the correct media artwork loads. Exact cause is not established.

## Required changes
1. **Preparing Playback artwork:** Trace placeholder/cached artwork/async image loading. Ensure no unrelated, stale or stretched image is displayed while correct title-specific artwork loads. Prefer correct cached image immediately when available; otherwise show neutral Supernova-branded placeholder. Avoid flashes and layout jumps; preserve existing approved branding.
2. **Bottom HUD safe area:** Raise seek bar, play/pause icon, remaining time and end-time labels together. Use TV-safe responsive insets, not a one-device hardcoded pixel offset. Keep coherent alignment and D-pad focus/navigation.
3. **Seek timestamp tooltip:** Reduce vertical separation from seek bar; add a small downward-pointing pointer/notch aligned to the actual seek position. Clamp tooltip to screen bounds at both ends; avoid jitter or overlap with other HUD elements.
4. **Regression/QA:** Verify video and audio playback, start/resume/seek, 720p/1080p/4K, overscan/safe area, long duration text, remote seeking and focus. Compare screenshots and video from physical Shield when available. Preserve player performance and subtitle visibility.

## Workflow
Include in next-version Codex handover. Documentation only: no code change, APK or merge to main.

## Additional owner Shield screenshots (2026-10-30)
Owner provided two photographs of current playback: HUD over *Dark Matter* and Playback Settings > Subtitles. The HUD photograph shows a bottom-hugging seek line, small time/chapter bubble, remaining time/end-time label and a standalone Settings gear at right; these are observed visual evidence, not a new approved design. The settings photograph shows a large blue-tinted centred panel, tabs Audio/Subtitles/Video/More, left-aligned subtitle track choices and timing/appearance rows with considerable unused panel space. Compare against later approved four-button HUD order Play/Pause, Subtitles, Audio, More (docs/design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md) and compact Concept A4 in-player settings (docs/design/NEXT_VERSION_IN_PLAYER_SETTINGS_AND_PLAY_PAUSE.md). Preserve current player capability and track selections; focus/selection distinct. Physical photos are conversation attachments, **not verified uploaded to GitHub**. Do not treat photographic perspective as pixel-accurate geometry. No code authorisation.
