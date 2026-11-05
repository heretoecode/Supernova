# Preview 4.1.5 — ordered physical Shield QA

All items below: **PHYSICAL SHIELD QA REQUIRED**. Preserve existing app data; install over 4.1.4 using the same package/signing identity. No factory reset or library deletion is needed.

1. Enable diagnostics, restart Shield, launch Supernova with the existing populated library. Confirm Home becomes usable. If a library warning appears, export diagnostics immediately: the exact original data/window fault is not yet proven fixed.
2. Cold-start twice, return after Android Home, and reopen after force-stop. Check Home, Movies, TV Shows, Search, Settings and Network & Files. Confirm Build information and What's New say 4.1.5.
3. Open indexed file Details from both grid and list. Check Back and focus. The null shared-image transition must no longer crash.
4. Resume a title whose bookmark belongs to a lower-quality encode but automatic Play chooses another version. Confirm start position. Explicitly choose each version and test Start from beginning; neither action should be overridden accidentally.
5. With duplicate encodes, watch/advance from E1 to E2, with one and multiple E2 files. Check season boundary and final episode. It must never advance to another E1 encode. Confirm the chosen file identity and ordinary playback return.
6. Test Continue Watching → playback → Home and Home → Details → playback → Details → Home. Check focus restoration, pause/resume, ±10-second seeks and Settings entry/Back.
7. Play a long WebDAV episode beyond the earlier failure point. Test controlled network loss and reconnection, preserve resume, then check A/V sync. An unexplained premature completion is still unresolved. Export diagnostics before retrying.
8. Test provider intro/outro/credits skips. End-reaching provider targets should leave playback running to real EOF rather than synthesising completion. Confirm ordinary true EOF still advances/marks watched correctly.
9. Browse horizontal file grids, including left/right edges. Check source/context pane movement, D-pad, Back and file selection. Check Sort/Order while scanning is refreshing the library.
10. Toggle Hide Watched and Ignore Articles, return to Movies/TV, compare grid/list, then restart. Test failed artwork and temporary technical-metadata retrieval failure followed by a successful retry.
11. Use two library sources with the same display name. Toggle scanning on only one and confirm the other remains unchanged after restart. Recheck passed WebDAV automatic/manual scanning and reconnect behaviour.
12. Test native and external local/network playback plus subtitles using the internal bridge. From another LAN device the bridge port must not accept connections.
13. On a controlled SFTP server, connect with both backends, reconnect after app restart, then replace its key. It must reject the changed identity. Reset only the affected server after independently verifying the legitimate rotation. Different negotiated key algorithms may require that verified reset. First-use interception remains a limitation.
14. On disposable test data, export/restore and interrupt a restore between replacements. Relaunch and confirm a coherent old or committed new database/preferences generation. Do not test destructive interruptions on the only copy of the real library. Exported backups still contain recoverable credentials and are not password-encrypted.
15. Recheck explicit Unwatched and custom-row membership after preferred-version changes. These known limitations are not fully corrected in 4.1.5 (SNA-007/008). Record large-library search latency (SNA-015).

The emulator and host tests do not establish Shield decoder, passthrough/PCM, HDR, network-server compatibility, long-running playback or physical focus acceptance.
