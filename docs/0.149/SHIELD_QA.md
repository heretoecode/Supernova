# Physical Nvidia Shield acceptance — NOT RUN

Use the [verified signed Supernova_0.149.apk](https://drive.google.com/file/d/1ETsEfYnMaiJPcjnl1mjXFxgKOO7ilVLG/view?usp=drivesdk); do not install the historical unsigned artifact. Complete Drive download/signature verification is recorded in [the delivery receipt](DRIVE_APK_DELIVERY.json). Retain installed data and test an in-place upgrade from 0.148; do not reset settings or the library. Record Shield model, Android version, TV resolution, text scale and remote used. No hardware acceptance is implied by software-render/JVM tests.

## Approved Subtitles surfaces

1. At 1920×1080, open Playback Settings during real playback. Compare the main Subtitles surface with Image B and the independent Track/Download surfaces with Image A's right side. Check TV safe margins, ten rows, no inner section boxes, fixed title/tabs, separators, icons, five swatches, selected values and focus glow. Compare enlarged text and smaller/larger displays; scroll content without moving the header.
2. Use Left/Right across the new tabs, OK to activate and Down to enter content. Reach Audio, Video and More and return to Subtitles; confirm their retained UI/actions. Move Up from the first subtitle row to its tab. Repeat navigation rapidly; no focus loop, old content or second settings stack.
3. With embedded, sidecar, bitmap and no subtitles, verify the toggle and current Track state. Open Track, scroll to the last real entry, select and confirm selected tick/current label and actual rendering. Re-enumerate/change tracks while open. Back/B/Escape must close one level and restore the invoking row; a second Back closes Settings and restores its opener.
4. Change every inline control with Left/Right and OK. Confirm immediate renderer changes and persistence after closing/reopening/restarting. Opening alone must preserve custom size/colour/position/opacity values. Check outline/background both ways and opacity endpoints. Position labels refer to the retained bottom-third renderer adjustment range; they do not add full-canvas anchors.
5. Compare delay changes against real speech: each action is 100 ms; verify signed direction, per-video persistence, seeking and subtitle enable/disable. Existing millisecond engine/storage units are retained.
6. From Playback Settings open Download with actual provider access. Check loading, empty/error/quota states, real filename ellipsis, language/SDH metadata, regional/neutral icons, download actions and scrolling. Size must say unavailable when absent. The search field filters returned results. Confirm cancel returns correctly, the existing explicit confirmation downloads exactly once, the downloaded subtitle is associated with the correct video, and playback resumes normally. Verify the local-file chooser remains reachable from Track. Confirm non-playback subtitle downloads retain their existing interface.

## Preserved ten fixes

| Fix | Physical check |
|---|---|
| Library statistics | Enter Movies/TV; complete statistics visible with controls focused, including no-op refresh. |
| Home/Filters focus | Hold/repeat remote navigation and let rows refresh; no transient focus flicker. |
| Settings rail ticks | No navigation tick; genuine option ticks remain. |
| Settings toggle states | Compare focused/unfocused On, Off and Unavailable; disabled actions cannot mutate. |
| Language flags | Inspect Audio and Subtitle explicit en-GB/en-US assets and generic/unknown neutral fallbacks in the shrunk APK. |
| Provider/Done icons | Known logos without cached metadata, unknown fallback and distinct Done action. |
| Reception | Cached/live/absent ratings; supported reviews even without ratings; provider errors and unavailable contexts. |
| Network & Files | Transparent landing workspace, six focusable cards, browser entry and Back restoration. |
| HUD Ends at | Visible paused with known duration; hidden unknown duration; pause/resume/seek/speed changes. |
| HUD timestamps | Separation, seek width and legibility across supported resolutions/text scales. |

Record any differences from the approved images separately from service/decoder/device failures. Emulator/device visual parity, a real downloaded subtitle, an installed upgrade and Shield playback are pending until actually observed.
