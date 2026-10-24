# Supernova 0.142 — Shield TV QA quickcheck

This is a signed QA candidate, not final acceptance. Source: `b347707defdbf2ab3ae83d920ba9fa7af3936220`; version 0.142 / Android code 142. [Download the verified signed QA APK](https://drive.google.com/file/d/1CxWm1pxf5K005ZzIO4RWKzQyPF0YQcu4/view?usp=drivesdk). See the [delivery receipt](SIGNED_QA_DELIVERY.json) for the SHA-256 and certificate. All physical results remain PENDING until recorded.

Use controlled media/account fixtures. Upgrade over 0.141 without uninstalling or clearing data. Confirm the version and retain library roots, exclusions, bookmarks, Put.io associations, custom page, watch history and resume positions.

1. **Network & Files:** open all six destinations: Overview, Local Storage, Network Shares, Cloud Services, All Files and Library Health. Check the four Overview cards, fixed rail and scrolling workspace. Browse local/USB and network folders; try list/grid, sorts, filtering, breadcrumbs, bookmarks and staged library changes. Check scanning and Health actions; unplug/reconnect a disposable USB fixture.
2. **WebDAV:** connect to HTTPS with an email-style username containing `@`, explicit port and nested path. Browse encoded filenames, scan and play a fixture. Relaunch to check saved credentials. Test wrong credentials and an offline server: Connect/Retry must retain the folder and not show old rows or duplicate actions. Confirm the HTTP transport notice on an isolated HTTP fixture.
3. **Put.io:** test device-code sign-in, nested browsing and parent return, Browser Options, folder/WebDAV association and fixture playback. Cancel/Back/Finish Later must exit safely without accepting pending ownership changes. Disable/re-enable without losing credentials or associations; disabled mode must not start API sync.
4. **Remote navigation/focus:** on the Network & Files rail, moving focus alone must not activate a destination; OK activates and Right enters its workspace. Check Left/Back/global navigation, deep-folder selection/scroll restoration and long forms with Connect fully visible. Keyboard Done returns to the edited field. Leave during loading/discovery/auth; late callbacks must not redraw the wrong page or steal focus.
5. **Edit Library:** inspect **Page Details → Content Type → Filters → Display Options → Review** over busy artwork. At every step check opaque surfaces, readable text, visible focus, Next/Previous and Live Preview. Exercise the name keyboard and nested choices; save/reopen and test unsaved-change/Close behavior without deleting owner media.

Capture 1920×1080 screenshots of each major state and any failure; compare with the three preserved references. Record PASS/FAIL/BLOCKED with the key sequence and screenshot, redacting credentials and linking codes. The complete 67-case Network & Files checklist plus five editor checks and signed upgrade remains in [SHIELD_QA.md](SHIELD_QA.md).

Playback HUD redesign stays deferred until the owner approves its final design.
