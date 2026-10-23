# Playback HUD: source-grounded proposal, awaiting visual approval

No player/HUD application source is changed for this proposal. Delivered 0.141 already implements four Playback Settings tabs in `player/PreviewPlaybackMenus.java`. The next design decision concerns fidelity and presentation of that existing behavior. The current HUD differs from the older written four-pill authority: `player_controller_experimental.xml` hides the separate Subtitles/Audio groups, labels the remaining entry Settings, and aligns Play/Pause with the seek row. `Supernova138UiTest.hudTransportAndFocusedTooltipMatch141Contract` asserts that delivered behavior. These differences are reported for design review, not silently corrected in 0.142. The original 0.135 image remains absent; rejected generated illustrations are not authority. A faithful replacement mock-up needs owner approval before implementation.

## Current control inventory and proposed retention

| Surface / proposed tab | Real controls and conditions | Source / persistence |
| --- | --- | --- |
| Main HUD (written intended design, differs from delivered source) | Proposed eventual four centered Play/Pause, Subtitles, Audio, More pills beneath seek bar, white focused pill and charcoal unfocused. Preserve seek, time, transport, title/artwork and buffering behavior; approval must resolve how the intended four buttons relate to delivered Settings entry | res/layout/player_controller_experimental.xml, PlayerController.configurePreviewTransport, PreviewHudTiming; 0.135 written specification |
| Subtitles | None/embedded/external tracks; Other Languages grouping; delay; appearance; Download Subtitles; Choose Subtitles. Reading language, Download languages, Prepare Subtitle Searches, Prepare Current Season, Subtitles by Default | PlayerActivity.refreshSubtitleTVMenu, PreviewPlaybackMenus.addNative/subtitlePreferences/languages; existing subtitle controller and preference keys; provider backoff/search and file chooser unchanged |
| Audio | Available tracks, Audio Boost, Night Mode; spatialization when native capability exposes it; audio delay | PlayerActivity.refreshAudioTracksTVMenu; existing track/enhancement handlers. No unsupported output or codec promise |
| Display | Native video format/aspect choices; disabled/hidden formats respect SurfaceController limits; conditional 3D/effect menu; Picture in Picture only with actual platform feature | PlayerActivity.createPlayerTVMenu; video format/surface persistence stays owned by native controller. No new HDR/display mode switch |
| Playback | Playback Speed relocated from Audio; native play modes; Segment Skipping (this session); notification mode only on non-Leanback devices | PreviewPlaybackMenus.renderTab; PlayerService.menuChangePlayMode, segmentOverride. Session skipping must not silently become a global preference |
| More / related existing actions | Preserve technical information, embedded chapters and other already available native actions. No standalone Info/Settings HUD buttons. Do not add Sleep Timer: its native TV-menu block is commented out | PlayerActivity, PlayerController, PlaybackChapters and existing menu routing |
| Nested editors | Native TVCardDialog delay/appearance and input actions; track check states, unavailable rows, close and opener restoration | PreviewPlaybackMenus.showNested/flushNested/refreshChecks; existing action objects stay the source of truth |

The tab inventory is dynamic: conditional native rows must be copied from the actual adapter for the playing item. A static mock-up must label fixture data as illustrative and include empty/unavailable cases. Track labels are not unique IDs; existing duplicate-label focus protection must survive.

## Proposed interaction contract for approval

Keep a single Settings window and one nested settings stack. Horizontal arrows move between the four tabs in this exact order: Subtitles, Audio, Display, Playback; terminal edges stay put. Down enters the selected tab's first meaningful enabled setting. Up from the first option returns to its tab. Back closes a nested editor, then its parent settings view, then the window, restoring the invoking HUD control. Preserve playback behavior when opening/closing; do not add automatic pause/resume policy.

Retain per-tab selected row and scroll across tab changes where stable native identity permits it. Async track updates must restore the same track identity/occurrence or nearest enabled row without moving to the HUD. These are proposed requirements to validate, not a claim that every current path already does this.

## Visual approval package still needed

Prepare a faithful 1920×1080 mock-up over the original approved HUD image, showing the four-tab window, focused tab, focused option, scroll state and one nested editor. The owner must approve window location/width/height, title/tab/row dimensions, opacity, spacing and focus appearance. No dimensions from rejected illustrations are adopted here. Use existing Supernova font, surfaces and approved white HUD-pill focus; Settings focus treatment should be approved explicitly.

After approval, implementation should retain native listeners/persistence, add per-tab focus/scroll tests, repeat all Playback141RenderTest/PreviewPlaybackMenusTest/PlaybackChaptersTest/subtitle replacement and player launch regressions, capture 1080p before/after states and obtain physical Shield playback/remote acceptance. This proposal does not block approved Network & Files/editor development and does not authorize a speculative HUD change.
