# Post-4.1.7 fixes — signed validation candidate

**Candidate only. Physical NVIDIA Shield validation and acceptance are pending. This is not Final.**

## Exact source and lineage

- Development branch: `codex/post-4.1.7-shield-fixes`.
- Authoritative main inspected before branch creation: `dcb0e2be008a14b7da5a86e389aeae707ffb00fc`.
- Accepted Preview 4.1.7 Final application/test source remains `7efb4195639205a6526281491af40c0dab1776b7`; this candidate does not replace its acceptance record.
- Exact candidate build source: `61a1ae5a21be90d185d448362c9729eb6c33cc42`.
- Build-source tree: `9e4afabb64f54d4c033f0ba521bf1962f69ce7ea`.
- Documentation-only delivery record commits after that source are not the APK source. Verify subsequent changes with `git diff 61a1ae5a21be90d185d448362c9729eb6c33cc42..HEAD -- src res build.gradle .github`.

## APK identity

| Field | Verified value |
|---|---|
| Workflow APK filename | `org.courville.nova.markpreview-6040083-6.4.63-mark.4.1.7-preview-universal-release.apk` |
| APK SHA-256 | `d4364661dfbd760a3a66adf2c9e242c20fdccd2fb501e992894de326246aab8c` |
| Application ID | `org.courville.nova.markpreview` |
| Version name / visible build version | `6.4.63-mark.4.1.7-preview` |
| versionCode | `6040083` |
| Signing certificate SHA-256 | `89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a` |
| Certificate subject | `C=US, O=Android, CN=Android Debug` |
| Certificate verification | `apksigner verifies; v1/v2/v3 true` |

The existing version fields are retained for this development candidate. **Fixes-release numbering remains NEEDS DECISION**; no new release version is chosen. Download the APK artifact ZIP and extract the workflow filename above. Its existing version fields do not make this post-4.1.7 candidate physically accepted or Final.

## Build composition and evidence

Established `.github/workflows/build-preview-apk.yml`, full validation mode, Java 17, pinned NOVA manifest/dependencies/prebuilts and retained 4.0/4.1.4/4.1.5/4.1.7 backend patches. Release assembly uses `-Puniversal -PmarkPreview -PmarkSigning` and the existing `NOVA_SIGNING_KEY_BASE64` secret. No package or signing migration, dependency version movement, full Settings redesign, feature release or main merge is included.

- Full candidate workflow: [37498264192 — SUCCESS](https://github.com/heretoecode/Supernova/actions/runs/37498264192).
- Source workflow: [37498264148 — SUCCESS](https://github.com/heretoecode/Supernova/actions/runs/37498264148).
- APK artifact: [nova-preview-full-apk — 11429587362](https://github.com/heretoecode/Supernova/actions/runs/37498264192/artifacts/11429587362); expiry: 2027-01-04T16:45:52Z.
- Build/manifest/signature/lint/runtime evidence: [gradle-build-log — 11428464700](https://github.com/heretoecode/Supernova/actions/runs/37498264192/artifacts/11428464700).
- 146 targeted tests, 422 complete Video tests, 96 overlapping stability/library checks and 17 FileCoreLibrary WebDAV security/range tests passed; identity audit (4 tests) and subtitle-log audit (2 tests) passed. No tests removed or disabled.
- Signed diagnostic build: classic/Preview cold/restart startup and strict API 28 phone-AVD local-clip playback gate passed, including completed Up reveal without pause/seek, explicit pause, four-control HUD, primary Info absence, technical panel and exact More return; no fatal runtime exception.
- Optimized noamazon universal release assembly, full release lint, signed diagnostic-to-release upgrade/restart with Preview retained, pinned certificate and v1/v2/v3 APK verification passed. Source manifest retains the established pinned NOVA/prebuilt/dependency composition and all four native ABIs.

The native playback check uses a synthetic local clip on the signed diagnostic build; the optimized release is separately checked for signed upgrade/startup/restart. It proves hidden-HUD Up does not pause playback, an explicit focused Play/Pause action pauses, the four-control HUD and clock are present, Right reaches More, primary Info is absent, the retained technical-only overlay is reachable through the existing hardware I handler, and Back restores the exact More opener. It is not a Shield, provider/network playback or long-duration test.

Artifact delivery is through GitHub Actions. The APK SHA/certificate are verified in the workflow and retained `apk-sha256.txt`/`apk-verification.log`; no independent local APK hash is claimed. The artifact ZIP is 79,215,975 bytes (this is the ZIP size, not APK size). The workspace file downloader is limited to 32 MiB and direct artifact-file retrieval returned HTTP 403. The signed APK itself was produced and uploaded successfully.

## Scope, conformance and outstanding work

See [all 35 development statuses and traceability](post-4.1.7-fixes-implementation.md). Development statuses are 29 FIXED implementations, one VERIFIED — NO CODE CHANGE REQUIRED (#3), three NEEDS DECISION (#15/#30/#31), and two PARTIAL (#32/#35). None asserts physical acceptance.

- #15: exact compact Cast & Crew replacement visual requires approval.
- #30/#31: exact simplified Subtitles/Audio hierarchy remains undecided; native selection/delay/appearance behavior is retained, with independently approved full application Settings links removed.
- #32: Supernova Settings is removed; exact final More contents remain undecided. Existing permitted playback entries and Report a Problem are retained.
- #35: failed rebind backoff, pending-request readiness and stale-callback safety are corrected and tested; original 795/468 counts still require raw Shield logs to distinguish deliberate rescrapes/provider failures/duplicates/visible missing artwork.
- Fixes-release version remains undecided. Production put.io OAuth is not invented or implemented as part of this pass.

Current `docs/design/` decisions and the QA register control this pass. Historical/archive/mock-up material did not override them. The exposed-card Featured redesign and all other parked/future features remain outside the candidate.

Physical Shield QA remains the user's responsibility. Prioritise #4 Continue Watching → real playback → Back and #9 Movies/TV → Details → Back: exact originating item, Grid/List mode, outer/nested scroll, immediate D-pad navigation, and asynchronous refresh. If an item is genuinely removed from a view, the implementation uses a bounded valid fallback; test that situation separately. Process death/recreation, performance with a real library, network/provider media, repeated playback transitions and actual artwork availability require device evidence. Complete the per-item physical checks in the traceability table, especially accent restoration, preparation flashes, utility panels and keyboard behavior.

## Separate acceptance gates

1. Approved portions implemented: complete.
2. Automated/build validation: complete — both workflows SUCCESS, all mandatory gates passed.
3. Candidate APK production: complete — signed optimized release candidate uploaded to the APK artifact.
4. Physical Shield validation: pending.
5. Physically accepted/Final: not claimed.
