# Foundation conformance and Shield acceptance

**Current status:** first signed 0.133 installed successfully according to the user; two Home regressions reported. Targeted corrected 0.134 signed delivery and independent verification passed; physical retest remains pending. Historical automated checks below describe initial Foundation preparation. Current evidence: [correction report](FOUNDATION_UI_CORRECTION.md).

## Automated release gates

- [x] Existing branch/history and current handover inspected; maintenance branch untouched.
- [x] All 132 retained historical APK archives hashed and embedded manifests inspected; literal history and evidence limitations recorded.
- [x] Permanent package/provider/task isolation on pinned build copies; no shared UID in inspected unsigned APK.
- [x] First optimised unsigned universal APK built; actual package `app.supernova.player`, version `0.133`, code `133` confirmed.
- [x] First unsigned APK's 88 native hashes compared against build inputs, all four ABIs inspected.
- [x] Resolved external release/desugaring runtime graph inventoried; full offline legal texts and original notices exported.
- [x] Full latest unsigned unit suite 419/419 passed; About/animation/QR/runtime version/preference preservation/classic-host recreation tests passed.
- [x] Python suite 36/36 passed; workflow actionlint passed; release lint completed with zero errors.
- [x] Final application source/assets at 70f62c28 with verified source-built FFmpeg rerun through full unit tests, lint and optimised unsigned APK conformance; 57 offline legal rows match source.
- [x] Canonical keyless cold CI rehearsal 37929976741 passes at 64e1a2eb; public evidence hashes checked and final review clearance recorded.
- [x] Corresponding-source/relinking recipes and archive established; four source-built ABIs passed native compatibility/regression checks before replacement.
- [x] Protected existing-key validation succeeds with exact 64-character pin (37920686126 and 37925321456).
- [x] Three-pass Foundation scope/design/signing review recorded; all pre-signing gates cleared; final approval/signing/device work remains open.
- [x] User explicitly authorised protected signing for app source 11766e59 and confirmed protected readiness=true.
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

## Corrected Foundation Shield retest — pending

- [ ] Install corrected 0.134 over Foundation 0.133, preserving library/preferences; Legacy remains separately installed.
- [ ] Confirm app.supernova.player, 0.134 / 134 and permanent branding/About history.
- [ ] Home shows local hero cards with neighbouring cards, no full-screen duplicate artwork; wide/tall/missing title logos remain readable.
- [ ] Remote left/right cycles and retains More Info focus; More Info opens the selected item; Back returns to usable Home focus.
- [ ] Focus first/middle/last cards in each Home row: artwork stays inside rounded highlighted frame throughout zoom and scrolling; no clipped edges or focus jumps.
- [ ] Movies/TV rows, Network & Files and Settings remain usable; Try New UI is unavailable.
- [ ] Library scan status pill and Preparing Playback retain animated double-ring indicators only during loading; playback/resume/audio/subtitles still behave as expected.

Automated synthetic render checks establish production view geometry and pixel containment, not physical Shield acceptance or live provider artwork success.

## Corrected automated/delivery checks — passed

- [x] Bounded source port compared with verified f97f6294; unrelated changes deferred.
- [x] 429 application tests and 36 Python guard tests pass in local and fresh CI checks.
- [x] Native source/configuration/licensing, registries/tempo and 64 four-ABI decode comparisons pass.
- [x] Actual signed 0.134 / 134 APK independently verifies package, signature, unchanged certificate, checksum, assets/providers, native bytes and carousel inclusion.
- [x] All 88 native library hashes match the first signed Foundation APK.
- [x] Verified APK and corresponding source artifacts are published; evidence and release notes committed.

[Signed delivery details](UI_SIGNED_DELIVERY.json). Device boxes above remain unchecked.
