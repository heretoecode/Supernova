# Foundation conformance and Shield acceptance

**Status:** unsigned automated evidence exists; final candidate review and protected signing remain pending. Do not mark device checks passed without user evidence.

## Automated release gates

- [x] Existing branch/history and current handover inspected; maintenance branch untouched.
- [x] All 132 retained historical APK archives hashed and embedded manifests inspected; literal history and evidence limitations recorded.
- [x] Permanent package/provider/task isolation on pinned build copies; no shared UID in inspected unsigned APK.
- [x] First optimised unsigned universal APK built; actual package `app.supernova.player`, version `0.133`, code `133` confirmed.
- [x] First unsigned APK's 88 native hashes compared against build inputs, all four ABIs inspected.
- [x] Resolved external release/desugaring runtime graph inventoried; full offline legal texts and original notices exported.
- [x] Full latest unsigned unit suite 419/419 passed; About/animation/QR/runtime version/preference preservation/classic-host recreation tests passed.
- [x] Python suite 28/28 passed; workflow actionlint passed; release lint completed with zero errors.
- [ ] Final source/asset changes rerun through full unit tests, lint and optimised unsigned APK conformance.
- [ ] Corresponding-source/relinking obligations established for actual FFmpeg build; source decision pending.
- [ ] Protected existing-key validation succeeds with exact 64-character public certificate pin.
- [ ] Foundation scope/design/signing review recorded against final committed source.
- [ ] User explicitly authorises enabling readiness and protected `prepare` operation.
- [ ] Signed APK produced, actual version/provider/resource/licence/signature/certificate/checksum verified and allowlisted artifact uploaded.

## Device checks after signed APK delivery — all pending

1. Download the verified GitHub Actions artifact; compare APK checksum to public evidence. Install on NVIDIA Shield under `app.supernova.player`, alongside Preview/NOVA. Expect a fresh library/settings namespace; no Preview upgrade/migration is promised.
2. Verify launcher name/icon, TV banner and static launch splash. Check safe margins, legibility and Space Black Blend at the Shield output resolution. No added splash delay, rotating launch logo or interface theme change.
3. Navigate Home, Movies, TV Shows, Files and Settings with the remote. Existing Home hero images/rows/focus must remain. Return from each utility tab and confirm artwork/base behavior.
4. Start a library scan; Home's existing information pill should show two stationary concentric rings with smooth opposing highlights/trails. Check active scanning, completion, failure, cancellation, pause/resume and tab/activity departure: no stuck indicator, poller or keep-screen-on state.
5. Play local and network media. Preparing Playback uses the same branded animation only during existing preparation/buffering visibility. Confirm first-frame hand-off, error/cancel exit, resume position, audio/subtitles, seek and playback controls; no added delay or new menu behavior.
6. About: left rail exposes the five child sections, persistent selected state and remote focus. Confirm centre/right scrolling, Left/Right/Back routing, last selection restoration and no focus traps at top/bottom.
7. Release Notes: current `0.133` remains expanded; older headers expand/collapse independently and retain state on returning. Read history uncertainty and compare original versions to the evidence ledger.
8. Licences: disconnect network and read full legal/NOTICE text in the right panel. Switch entries rapidly, scroll long licences, leave/re-enter. QR is supplementary; scan representative project/provider codes with the iPhone and confirm the official destination.
9. Credits: verify exact TMDB notice and unmodified logo, NOVA/Archos origins, existing optional providers, no implied endorsement/parked-service claims or in-app update/external-link action.
10. Technical Information: verify actual version/code/build/source, non-unique device/API/ABI information, labelled Android decoder reports and graceful unavailable storage/cache values. No credentials, device identifiers or account/library paths should appear.
11. Configure explicit playback/network preferences, force-stop and relaunch. Confirm the `0.N` identity does not cause old NOVA migration resets or image-cache deletion. Check optional Trakt/OpenSubtitles integrations with existing accounts; production account acceptance has not been simulated.

Record device model, Android version, APK SHA-256, source commit, exact steps, expected/actual behavior and screenshots/video where relevant. Physical QA is pending by agreement and cannot be reported as completed by automation.
