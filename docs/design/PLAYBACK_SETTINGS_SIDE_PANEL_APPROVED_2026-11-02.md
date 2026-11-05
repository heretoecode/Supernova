# Playback Settings — complete side-panel design (APPROVED 2 November 2026)

**Status:** APPROVED by the owner in full. Target: 0.152.
**Supersedes:** the four-tab (Subtitles / Audio / Video / More) in-player Settings layout, the centred "Image B" panel size, and the separate Download Subtitles pop-up. Subtitles row content, icons, controls and colours from Image B are kept except where stated.
**Builds on:** [PLAYBACK_SUBTITLES_SIDE_PANEL_APPROVED_2026-11-02.md](PLAYBACK_SUBTITLES_SIDE_PANEL_APPROVED_2026-11-02.md) (panel geometry, separators, focus, subtitle-position decision).

## Approved images (1920×1080, rendered by [render-playback-settings.js](approved-mockups/playback-settings/render-playback-settings.js); film scene and track/result names are placeholders)
| Screen | Image |
|---|---|
| Subtitles tab | [subtitles-tab-approved-2026-11-02.png](approved-mockups/playback-settings/subtitles-tab-approved-2026-11-02.png) |
| Audio tab | [audio-tab-approved-2026-11-02.png](approved-mockups/playback-settings/audio-tab-approved-2026-11-02.png) |
| Playback tab | [playback-tab-approved-2026-11-02.png](approved-mockups/playback-settings/playback-tab-approved-2026-11-02.png) |
| Download Subtitles (in panel) | [download-subtitles-in-panel-approved-2026-11-02.png](approved-mockups/playback-settings/download-subtitles-in-panel-approved-2026-11-02.png) |
| Subtitle Track full list (in panel) | [subtitle-track-list-in-panel-approved-2026-11-02.png](approved-mockups/playback-settings/subtitle-track-list-in-panel-approved-2026-11-02.png) |

## Principles (owner)
1. One side panel, top-right, 640px wide (1080p logical), **height fitted to its contents**; the film stays visible.
2. **Avoid sub-menus.** Prefer inline toggles, choices and steppers. Lists that cannot be inline open **inside the same panel** (page swap), never as a second pop-up.
3. Only settings that genuinely exist on the device are shown. Do not invent options or data labels.

## Tabs — three, Settings button **always opens on Subtitles** (owner: no "remember last tab")
### Subtitles
SUBTITLE SELECTION: Subtitles (toggle) · **Subtitle Track — stepper `‹ flag Name ›`** (Left/Right cycles tracks; **OK opens the full track list in the panel**) · Download Subtitles (Find & Download ›, opens in panel).
SUBTITLE APPEARANCE: Text Size (Small | Medium | Large) · Text Colour (white, yellow, cyan, green, pink — kept) · Vertical Position (Bottom | Middle | Top) · Outline (toggle) · Background (toggle) · Background Opacity (stepper `‹ 100% ›`, 0/25/50/75/100, dimmed while Background is off) · Subtitle Delay (`− 0 ms +`).

### Audio
AUDIO SELECTION: **Audio Track — stepper `‹ flag Name · channels ›`**; OK opens the full list in the panel.
AUDIO ADJUSTMENTS: Audio Delay (`− 0 ms +`) · Audio Boost (toggle) · Night Mode (toggle) · **Spatial Audio** (toggle; renamed from "Platform spatialization").

### Playback
PICTURE: Format (stepper: Original, Fullscreen, Stretched, 4:3, 16:9, 1.85:1, 2.39:1, Optimised) · 3D Mode (stepper: Off, Side by side, Top and bottom) · Picture in Picture (`Start ›`, an action, not a menu).
PLAYBACK: Playback Speed (stepper) · Play Mode (stepper: Single, Folder, Repeat single, Repeat folder, Binge watching) · **Skip Intros & Credits** (toggle; renamed from "Skip intro/outro" / "Segment Skipping").

Removed from the panel (not present on Android TV / disabled in source): Notifications (phone-only; hidden on leanback), Sleep Timer (commented out in source). Do not reintroduce without owner approval.

## In-panel lists
- **Download Subtitles:** header `‹ Download Subtitles`; search field (existing on-screen keyboard) prefilled with the title/episode; results rows = flag · file name (ellipsis) · language (+ "Hearing impaired" only when the provider supplies it) · download icon. "Size unavailable" is **removed**. Panel grows with results to a maximum, then scrolls.
- **Track list (subtitle and audio):** header `‹ Subtitle Track` / `‹ Audio Track`; rows = flag · language · type (SDH / Forced / Full / channels — only when known) · tick on the current track; "N more" hint when scrollable.
- **Back** returns to the tab; Back again closes the panel.

## Remote behaviour
Tab row: Left/Right switches tabs. Rows: Up/Down moves; Left/Right changes the value of steppers/choices; OK toggles switches, opens track lists/Download, starts PiP. Back: one level up, then closes and restores HUD focus.

## Acceptance (Shield)
Owner photos matching each approved image; picture visible beside the panel; panel fitted to content; every row works with the remote; Download Subtitles and track lists open inside the panel; no regression in subtitle/audio/track behaviour.
