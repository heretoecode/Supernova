# Consolidated corrective pass — signed Shield-test candidate

**Candidate only. Physical NVIDIA Shield validation is pending. This is not Final; main remains unmerged.**

## Exact source and lineage

- Branch: `codex/post-4.1.7-shield-fixes`.
- Continued live branch from `563c3374d6227ef290fb61c4899d9d93adb94eea`; existing work was preserved.
- Exact APK build source: **`b021c51dcbbe2d5679fb9015c43830713f02aa0b`**.
- Source tree: `12faf85536b6c78ca43f097f538ca044ea204489`.
- Main verified unchanged: `dcb0e2be008a14b7da5a86e389aeae707ffb00fc`.
- Accepted Preview 4.1.7 Final remains source `7efb4195639205a6526281491af40c0dab1776b7`; this candidate does not confer new physical acceptance.
- Previous physically tested corrective candidate source `61a1ae5a21be90d185d448362c9729eb6c33cc42` is historical. Its earlier FIXED labels do not override latest physical QA/design authority.

Delivery documentation commits after the build source are **not** APK source. Verify with `git diff b021c51dcbbe2d5679fb9015c43830713f02aa0b..HEAD -- src res build.gradle .github`; the final delivery record changes Markdown documentation only.

## APK identity and delivery

| Field | Verified value |
|---|---|
| APK filename | `org.courville.nova.markpreview-6040083-6.4.63-mark.4.1.7-preview-universal-release.apk` |
| APK SHA-256 | `dd468ccd9dde7287d74cc77aa786750d447b627d57a1afa179567cd438fbb89d` |
| Application ID | `org.courville.nova.markpreview` |
| Version name / visible build version | `6.4.63-mark.4.1.7-preview` |
| versionCode | `6040083` |
| Signing certificate SHA-256 | `89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a` |
| Certificate subject | `C=US, O=Android, CN=Android Debug` |
| Signature verification | `apksigner`: v1/v2/v3 true; one RSA 2048-bit signer |
| Binary manifest | aapt verifies retained package/version; minSdk 23, targetSdk 36 |
| Native ABIs | arm64-v8a, armeabi-v7a, x86, x86_64 |

Download [nova-preview-full-apk — artifact 11460257088](https://github.com/heretoecode/Supernova/actions/runs/37568094185/artifacts/11460257088), extract the ZIP and install the APK filename above. Artifact expiry: `2027-01-05T03:43:42Z`. ZIP size is 79,401,745 bytes; ZIP SHA-256 is `8bae21e4042f0f275cf436e20f18a2de8a22a38b2d239c79430617cdaace40e1`. **The ZIP hash differs from the APK hash.**

Existing application/version/signing strategy is retained, including protected CI signing secret `NOVA_SIGNING_KEY_BASE64` and existing androiddebugkey alias. Fixes-release numbering remains NEEDS DECISION; retained-version candidate is authorised. Signed diagnostic-to-release upgrade/restart with Preview enabled passed. No fresh-install identity phase was started.

## Build and automated evidence

- [Full signed build 37568094185 — SUCCESS](https://github.com/heretoecode/Supernova/actions/runs/37568094185), attempt 1, exact source above.
- [Source validation 37568094178 — SUCCESS](https://github.com/heretoecode/Supernova/actions/runs/37568094178).
- [Build diagnostics 11460187294](https://github.com/heretoecode/Supernova/actions/runs/37568094185/artifacts/11460187294): source manifest, complete logs, lint reports, actual native screenshots/XML/logs, APK manifest/hash/certificate verification and upgrade evidence. Artifact expiry: `2027-01-05T03:43:42Z`.
- [Source/render diagnostics 11459947812](https://github.com/heretoecode/Supernova/actions/runs/37568094178/artifacts/11459947812): full source/test logs and production-component renders.
- 148 targeted tests, all 441 Video tests, 98 overlapping stability/library checks and 17 WebDAV tests passed, with no recorded skips. Identity audit four tests and subtitle-log audit two tests passed. No tests were disabled or removed for green validation. Later stability runs overwrite Video XML; full-suite count is retained in `gradle-tests.log`.
- Signed debug assembly/lint, classic/Preview cold/restart startup, actual native playback gate, optimized release assembly, explicit full release lint, signed upgrade/restart, package/version assertions and v1/v2/v3 certificate verification passed.

Build composition is the established pinned NOVA manifest/prebuilts and retained backend corrections, plus `.github/build/avos-shield-corrections.patch`; dependency versions were not moved. Java 17 and `-Puniversal -PmarkPreview -PmarkSigning` use the existing workflow. No main merge or signing/application transition occurred.

APK hash and full signature verification are established by CI, not an independent local APK hash. The APK and final diagnostic ZIP exceed the workspace downloader's 32-MiB limit; direct diagnostic-file retrieval returned HTTP 403. The artifacts were successfully produced and uploaded and remain available through GitHub. Smaller source/render artifacts were retrieved and inspected locally.

## Native playback coverage and limits

The signed diagnostic APK on API-28 phone AVD uses real changing synthetic H.264/AAC video with long GOPs. Enforced checks include hidden-HUD Up reveal without transport changes, explicit pause, four primary controls, absent primary Info, exact technical-Info opener return, changed video pixels after seek, native completed target within tolerance, selected-target commit, deterministic origin cancellation and physical remote keycodes 90/89 displaying the timestamp bubble without seek-bar focus.

Native logs previously exposed a 21.416s target landing at 31.250s. Paused known-duration video seeks now use the existing bounded frame-accurate path from the preceding keyframe; playing transport preserves its existing direction policy. The long-GOP fixture and target assertions were retained and now pass. One in-flight preview and serialized final commit/cancel prevent an earlier preview completion from resuming the wrong position.

A separate real MKV contains English/French subtitle cues preceding selection. Native selection completion/position, stable paused-video pixels sampled from a changing region, actual French caption, return to the previously consumed English cue and Off→same-track re-enabling all pass. Subtitle switching fences subtitle-only queues/decoder state without pause, video seek/restart or surface replacement. The bounded all-track cache is four MiB/4096 packets; completed cues expire, and unknown-duration bitmap history is limited to one minute. Cache saturation/bitmap decoder context, large/network media and transient single-frame behaviour still require physical format/device coverage.

The optimized release is independently verified for upgrade/startup/restart and signature; detailed native playback checks run on the signed diagnostic build from the same source. These are not Shield, long-duration, passthrough, network/provider or actual remote-hardware acceptance.

## Render, interaction and conformance

See [complete per-item register](next-corrective-conformance.md) and [implementation/investigation record](next-corrective-implementation.md). All 35 original items and the consolidated additions are traced to components, tests and remaining physical QA.

Production-component renders reviewed include final Featured geometry and one ordinary initial row, whole-card enlargement/glow, library toolbar, information/people tiers and aligned text rows, Match result hierarchy, Settings panel centring/left children and Network margins. Synthetic artwork validates geometry, not real artwork availability. Trailer review uses a stand-in with actual WebView layout parameters to validate centring, 16:9/~60% area and dimming; real Chromium/YouTube rendering is not proved. Legacy proxy HUD screenshots are not authoritative current HUD evidence. Final native screenshots/XML and frame comparisons are preserved in CI diagnostics; those checks are AUTOMATED VALIDATED, not a claim of manual review of every final native capture.

Development dispositions: 21 FIXED, 13 VERIFIED — NO CODE CHANGE REQUIRED, #35 PARTIAL. Specific evidenced retry/rebinding defects are corrected; broader artwork reliability remains observational. Approved consolidated additions are implemented and automated validated. Latest HOME §11, final playback menu amendments and left-panel Integration amendment supersede older parked/undecided/middle-panel wording. The unavailable final Home PNG did not block the written composition.

Language mapping is resolved: English US, Portuguese Brazil, Chinese China, Spanish Spain, Arabic Saudi Arabia; remaining generic languages use standard likely-region mapping, explicit country tags first. Provenance/license and deterministic bundled national flags are recorded in current design documentation.

No unresolved in-scope product choice was invented. Version numbering remains NEEDS DECISION. Live metadata/provider/YouTube availability is external; no new credentials were invented. All explicitly excluded identity/signing, future-feature, architecture, provider-transport and later-release work remains excluded.

## Physical Shield handoff

1. **IMPLEMENTED:** complete approved corrective scope; #35 broader reliability remains observational.
2. **AUTOMATED VALIDATED:** complete source/full/native/lint/release/upgrade/signature gates green.
3. **RENDER/INTERACTION REVIEWED:** production-component evidence reviewed with the limits above.
4. Signed Shield-test candidate: produced and uploaded.
5. Physical Shield QA: **pending**; no new visual/interaction requirement is SHIELD ACCEPTED.
6. Physically accepted/Final: **not claimed**.

Test every per-item physical check in the conformance register. Prioritise exact Continue Watching playback return and Movies/TV Details return including Grid/List scroll state and refresh, hard keyboard edges, inline row controls, new Home/Details/utility geometry, trailers, menus and flags, seek target/frame/commit/cancel, dedicated REW/FF, subtitle track changes and format/cache limits. Recheck all physically accepted regression protections on this candidate. Record outcomes in GitHub against this exact source/APK hash. Stop here before merge or another release.
