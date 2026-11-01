# SUPERNOVA — PR #30 continuation: approved Subtitles UI + signed test APK
**Owner authorisation: 1 November 2026. This document authorises implementation, testing and APK delivery for the scope below.**
**Repository:** heretoecode/Supernova
**Continuation:** draft [PR #30](https://github.com/heretoecode/Supernova/pull/30), branch `codex/agreed-fixes-2026-11-01`, reported validated commit `257ae64e79c2269a4afee474799810e491dc25eb`. Verify actual HEAD and preserve newer changes if present.

## Mission
The ten previously agreed fixes are already implemented in PR #30. **Do not redo or restart them.** Continue from the exact existing branch state. Implement the **approved Playback Settings > Subtitles** UI (main tab + two pop-ups), integrate and test with all ten fixes, then **build, sign and upload an installable Shield QA APK to the existing Google Drive APK folder**. Deliver verified link, exact commit SHA, version/versionCode, SHA-256, test results and known limitations.

## Mandatory source of truth
- **Consolidated detailed blueprint v2:** [docs/design/2026-11-01-subtitles-implementation-blueprint.md](https://github.com/heretoecode/Supernova/blob/main/docs/design/2026-11-01-subtitles-implementation-blueprint.md). Read the entire document, including dimensions, D-pad focus, compatibility, screenshot acceptance and image precedence.
- **IMAGE B — approved main Subtitles window:** [AA59180F-F8E0-4697-9423-78621273D28B.png](https://github.com/heretoecode/Supernova/blob/main/docs/design/approved/AA59180F-F8E0-4697-9423-78621273D28B.png). No inner section boxes.
- **IMAGE A — approved Subtitle Track and Download Subtitles pop-ups ONLY:** [A31E82C8-CC81-4E6C-AC1D-6AE60370E152.png](https://github.com/heretoecode/Supernova/blob/main/docs/design/approved/A31E82C8-CC81-4E6C-AC1D-6AE60370E152.png). Ignore the superseded main window visible in A.
- **Ten-fix implementation and validation record:** [docs/handovers/2026-11-01-validation.md](https://github.com/heretoecode/Supernova/blob/codex/agreed-fixes-2026-11-01/docs/handovers/2026-11-01-validation.md).

## Preserve existing work
1. Read PR #30, source, tests, implementation logs, release/signing/upload workflows and current branch state before editing. Preserve uncommitted work; do not reset/rebase/force-push/clean/revert or overwrite other work.
2. Keep the ten implemented fixes intact: library statistics visibility; Home/Filters focus flickering; Settings ticks; toggle state differentiation; language flags; provider/Done icons; Reception ratings/reviews and unavailable states; Network & Files background removal; paused 'Ends at'; HUD timestamp spacing.
3. Implement **only** the Subtitles area in Playback Settings and its Subtitle Track/Download Subtitles pop-ups. Do not redesign Audio, Video, More, the HUD or any unrelated UI. Reuse existing components and preserve preference keys, subtitle/playback behaviour, provider logic and remote Back/focus handling.
4. Treat image B as authoritative for main layout; image A only for pop-ups. Follow the detailed blueprint; do not substitute an approximate redesign. Note any discrepancies, including the agreed 100 ms subtitle delay step versus existing behaviour.
5. Do not merge into main, change application identity/signing keys, or publish a production release. Keep PR #30 draft unless owner separately approves changing its status.

## Validation
- Run targeted and broader feasible automated tests, compilation, lint and regression tests for both the ten fixes and subtitles. Report exact counts, failures, existing skips and warnings; **never call a suite green if it wasn't run or didn't pass**.
- Validate 1920×1080 TV/emulator UI and D-pad focus, scrolling, Back restoration, track selection, subtitle download and all settings controls. Capture actual screenshots of main Subtitles, Track and Download views and compare side-by-side with approved references. If device/emulator not available, mark visual verification **pending**, not complete.
- Check no regression to the ten fixes. Physical Shield QA is pending until the owner actually tests the delivered APK.
- Maintain data/privacy hygiene: never log credentials, tokens, user paths, subtitle search terms or filenames in diagnostics.

## APK build and delivery — explicitly authorised
1. Build an **installable signed QA APK** from the final exact commit containing PR #30's ten fixes **and** approved Subtitles UI. Follow the project's established signing/identity/versioning workflow and naming convention; do not invent signing credentials or silently switch signing keys. Ensure upgrade compatibility with the installed 0.148 app where feasible.
2. Use the **existing Google Drive APK folder**, verify the uploaded file exists and is downloadable, and provide its exact link. Avoid duplicate/conflicting uploads.
3. Record **filename, app version, versionCode, commit SHA, SHA-256 checksum, build/signing validation, and Drive URL**. If signing or Drive access is blocked, investigate available CI/permissions and existing workflow, then report the exact blocker and produce the maximum safely verified non-release artefact without claiming a signed APK was delivered.
4. Keep PR #30 unmerged and draft. No production rollout.
5. Update relevant GitHub implementation/validation/release documentation with final evidence and exact status.

## Completion report
Provide: what changed; reused/new components; test results; screenshot links and visual differences; APK link and SHA-256; final commit; outstanding Shield QA and any unverified items. **Do not stop at code complete if APK delivery is still feasible.** Do not report an upload or signed APK as complete without checking it.

## Execution record — 1 November 2026

The authorised continuation is implemented and has passing combined conformance on `cb28b1b936891374bc801f0b58b3397926fc3086`: 1,276 application tests, 300 library passes/four existing skips, 147 tooling tests, release lint zero errors/1,252 warnings and unsigned 0.149 APK assembly. All ten prior fixes remain intact. Actual Android-view software comparisons are recorded; emulator/Shield visual parity and physical acceptance remain NOT VERIFIED.

**Signed delivery is blocked, not complete.** Protected signing run [41331247034](https://github.com/heretoecode/Supernova/actions/runs/41331247034) was rejected before steps because `codex/agreed-fixes-2026-11-01` is not an allowed deployment branch for `supernova-foundation-signing`. No key/identity/protection was changed, and no 0.149 APK was uploaded to Drive. The exact tested unsigned artifact, checksum, rejection, owner action/resume command and all evidence are in [0.149 status](../0.149/STATUS.md). PR #30 remains draft and unmerged.
