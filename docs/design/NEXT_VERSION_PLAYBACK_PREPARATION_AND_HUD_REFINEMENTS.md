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

## Owner's latest HUD design direction — 2026-10-30 (supersedes conflicting four-button proposals for next design)
- Keep seek bar at **raised** height seen in latest discussion mock-up; actual safe-area pixel measurement pending final approved blueprint.
- **Play/Pause** is immediately **left of and vertically aligned with seek bar**, not a separate lower row. Preserve one-click transport behaviour.
- The **current/seek timestamp bubble** is **always visible above the playhead whenever seek bar/HUD is visible**, follows actual position, clamps at both ends, and may show chapter name/number if real chapter metadata exists. **No duplicate current-time label beneath/left of seek bar**. No thumbnail preview (deferred).
- **Total media duration** appears **in line with the seek bar on the right**. Adjacent show **Ends at HH:MM**, computed from actual playback position/remaining duration and local clock, updated correctly on seek/pause/resume. Do not confuse total duration, remaining duration and wall-clock end time; choose precise final text order/spacing in measured blueprint.
- Consolidate **Subtitles, Audio, Video and More** under **one in-player Settings button**, using the already documented Concept A4 panel. No separate Subtitle/Audio/More HUD buttons. Do not route to global app Settings. Keep Settings accessible and D-pad navigable.
- Earlier docs/design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md four-pill layout and PLAYBACK.md centre Play/Pause four-button layout are **superseded as next-HUD visual proposals** by these explicit later owner changes; preserve functionality and audit navigation semantics. The latest generated mock-up still erroneously places end-time labels below the bar; owner specifically requires inline.
- This is design direction, **not final pixel geometry or implementation authorisation**. Physical Shield visual QA and source audit required. The latest mock-up image is not verified in GitHub.

## Owner APPROVAL — main HUD visual layout (2026-10-30)
Owner explicitly approved latest mock-up's single horizontal HUD line: focusable Play/Pause button at far left, raised seek bar, total duration and adjacent `Ends at HH:MM` on the right, then one Settings pill at far right. The seek timestamp/chapter bubble stays permanently visible above playhead whenever seek bar is shown, no duplicated current-time label below. Settings opens compact in-player Audio/Subtitles/Video/More (Concept A4), NOT global Settings. Older four-button layouts are superseded. Approved image was generated in chat and is NOT verified uploaded to GitHub; textual requirements are binding until image is preserved.

### Proposed Shield D-pad focus contract — requires owner confirmation before locking
- Initial HUD focus is on the **seek bar**, not Settings or Play/Pause, so Left/Right immediately seek backwards/forwards using existing seeking semantics.
- **Play/Pause is a real focusable control** with a distinct focus ring; its icon reflects playback state independently of focus. Remote media Play/Pause key should continue to work globally regardless of HUD focus.
- Candidate: OK while seek bar focused toggles Play/Pause; Down from seek bar enters controls, with access to Play/Pause and Settings. **Do not implement exact focus traversal yet**: the approved visual has Play/Pause and Settings horizontally inline, so a Down-only path is not self-evident. Codex must propose a precise non-conflicting focus graph covering Left/Right seeking, Play/Pause and Settings reachability, Back, seeking/paused state and remote media keys; owner approval required.
- Maintain raised TV-safe inset and exact inline alignment across 720p/1080p/4K; actual dp/pixel tokens to be measured and locked before implementation.

## Title typography placement proposal (2026-10-30)
Owner proposes moving the existing official title typography/logo and basic media information from top-left down to the middle-left region of the playback HUD, above the raised seek bar. Preserve official title logo asset when available, not replace it with generic text as in illustrative generated mock-ups. Keep current supporting information limited to actual supported metadata, avoid overlap with central video subjects and the seek timestamp bubble, maintain responsive safe-area and legibility scrim. This is a **proposal pending owner review of updated mock-up**, not a locked approved placement. Current approved horizontal seek/Play-Pause/total-duration/Ends-at/Settings row remains unchanged. Generated visual is not verified in GitHub.
