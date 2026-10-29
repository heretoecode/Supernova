# Next version — approved playback age-rating overlay

**Status:** Approved by user; implementation and verification pending. **Target:** next Supernova version after 0.143.

## Approved behaviour
- Show the media's age/certification rating **when playback first begins**.
- Position **upper-left**, inset slightly from screen edges.
- Display for **10 seconds**, then gently fade out.
- Style: **white rating text, very thin black outline, slim red vertical accent**; **no solid or translucent background panel**.
- Overlay is **non-interactive** and must not obstruct or interfere with playback or remote controls.
- Use the certification appropriate to the configured **Country/Region**, not merely UI language.
- If the appropriate rating is unavailable, **show nothing** rather than guessing or displaying an incorrect certification.
- Only the **age rating** is in scope; detailed content warnings/advisories are postponed.

## Codex implementation/QA
- Audit current playback start, resume/restart, player overlay, certification data and regional preference handling; preserve existing playback HUD.
- Implement once per qualifying playback start, without flicker, duplicated overlay or focus capture; avoid breaking playback, subtitles, seek HUD or accessibility.
- Test movies and TV episodes, multiple regions/rating schemes, missing or conflicting metadata, start/resume/restart, quick media switching, overlay timing and fade, and Shield resolutions/text scaling.
- Report automated results separately from physical Nvidia Shield playback QA.

## Release workflow
Include in next-version Codex handover and release notes when implemented. This is a planning record only; no application code, APK or main branch modified.

## 2026-10-29 owner reconfirmation
Owner explicitly requests inclusion in the upcoming version alongside the Network & Files Settings-style redesign and shared global top-navigation correction. Retain the exact approved appearance and 10-second behaviour above; do not reinterpret the request as a new design or claim it has been implemented. Include in the same next-version Codex scope, with independent playback verification and release-note entry.
