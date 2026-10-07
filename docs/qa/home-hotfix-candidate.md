# Immediate Home hotfix — signed replacement Shield-test APK

**Replacement test candidate only. H1–H5/H7 await physical NVIDIA Shield QA. H6 retains the user's prior SHIELD ACCEPTED status and is regression-protected; this does not confer physical acceptance on the whole replacement APK. Main is unmerged.**

## Exact source and scope

- Branch: `codex/post-4.1.7-shield-fixes`.
- Live authority/starting HEAD inspected: `eea74f740fcbe5c4325a0917e0241aa34210418e`.
- Exact APK source: **`f97f62942f6b4f1a90e2e15258db452384d6b39c`**.
- Source tree: `3bdc35365c6cb696b21dda20b59ced6bae2118a1`.
- Main remains `dcb0e2be008a14b7da5a86e389aeae707ffb00fc`.
- Previous wider corrective candidate: `b021c51dcbbe2d5679fb9015c43830713f02aa0b`; its user Shield QA prompted this Home-only turnaround.
- Accepted Preview 4.1.7 Final source remains `7efb4195639205a6526281491af40c0dab1776b7`.

Later delivery-documentation commits are **not APK source**. `git diff f97f62942f6b4f1a90e2e15258db452384d6b39c..HEAD -- src res build.gradle .github` is empty for the final documentation record.

The only production changes are `PreviewFeaturedCard.java` and `PreviewPages.java`, implementing H1–H5/H7 from the latest Home hotfix authority. H6's `leanback/presenter/PreviewCardPresenter.java` has no diff from the user-tested baseline. Playback, native corrections, Details, Settings, providers, dependencies and identity/signing configuration were not changed. No reset/restart, replacement branch, unrelated QA implementation, main merge or later release was begun.

## APK identity and download

| Field | Verified value |
|---|---|
| APK filename | `org.courville.nova.markpreview-6040083-6.4.63-mark.4.1.7-preview-universal-release.apk` |
| APK SHA-256 | `11641a15d2480662b56c1eeb9da8b18200ef14a0ce22a803fa99bffa0993a243` |
| Application ID | `org.courville.nova.markpreview` |
| Version name / visible build version | `6.4.63-mark.4.1.7-preview` |
| versionCode | `6040083` |
| Signing certificate SHA-256 | `89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a` |
| Certificate subject | `C=US, O=Android, CN=Android Debug` |
| Signature verification | apksigner v1/v2/v3 true; retained signer |
| Actual binary manifest | minSdk 23, targetSdk 36; retained package/version verified with aapt |
| Native ABIs | arm64-v8a, armeabi-v7a, x86, x86_64 |

[Download replacement APK ZIP — artifact 11480830105](https://github.com/heretoecode/Supernova/actions/runs/37614061018/artifacts/11480830105), extract it and install the APK above. Artifact expiry: `2027-01-05T11:26:14Z`. ZIP size: 81,358,210 bytes. ZIP SHA-256: `2078a2c2ef5d6aee056b888d7a847569fdc00f148dd317564431cdb15ac7feb7`; this is distinct from the APK hash.

The established application/version/signing strategy, protected CI key and androiddebugkey alias are retained. Existing version fields do not make this candidate Final; fixes-release numbering remains unresolved and was not invented. Signed diagnostic-to-release upgrade/restart with Preview retained passed.

## Validation evidence

- [Complete source workflow 37614060979 — SUCCESS](https://github.com/heretoecode/Supernova/actions/runs/37614060979).
- [Full signed build 37614061018 — SUCCESS](https://github.com/heretoecode/Supernova/actions/runs/37614061018), attempt 1, exact source above.
- [Source/test/render evidence 11478558009](https://github.com/heretoecode/Supernova/actions/runs/37614060979/artifacts/11478558009).
- [Build/lint/native/manifest/signature/upgrade diagnostics 11480480180](https://github.com/heretoecode/Supernova/actions/runs/37614061018/artifacts/11480480180), expiry `2027-01-05T11:26:14Z`.

148 targeted tests, all 445 Video tests, 98 overlapping stability/library checks and 17 WebDAV tests pass, alongside existing identity/privacy audits. Targeted/full logs record no skipped tests. Complete-suite counts use retained Gradle logs; later stability runs overwrite Video XML. The earlier fake-row “inline” assertion was replaced by actual row ancestry, layout/render/focus tests; no substantive assertion was removed for green validation. Existing accepted focused-card mask/scaling/glow tests remain intact and pass.

Signed-debug assembly/lint, classic/Preview startup/restart, the existing actual native seek/subtitle/Info-return regression gate, optimized release assembly, explicit full release lint, signed upgrade/restart, binary manifest and pinned certificate verification all pass. Full native checks were rerun as regression validation; no unrelated playback implementation was added.

The established pinned workflow/build composition is retained. APK hash and full signature verification are from CI; no independent local APK hash is claimed. The large APK/diagnostic ZIPs exceed the workspace file downloader's 32-MiB limit. Smaller source/render evidence was retrieved and reviewed locally; GitHub holds the complete uploaded artifacts.

## H1–H7 conformance

| Requirement | Development/validation state | Evidence and remaining physical check |
|---|---|---|
| H1 | IMPLEMENTED; AUTOMATED VALIDATED; RENDER/INTERACTION REVIEWED | Same safe top, downward height extension, equal active/neighbour top/bottom/height; actual initial viewport assertion passes 55–65% artwork teaser, with no second ordinary row. User checks Shield composition. |
| H2 | IMPLEMENTED; AUTOMATED VALIDATED; RENDER/INTERACTION REVIEWED | Home never publishes selected Featured art to the background; rounded internal artwork/gradient remains. Render shows normal surrounding treatment. User browses multiple real titles and checks no duplicate background image. |
| H3 | IMPLEMENTED; AUTOMATED VALIDATED; RENDER/INTERACTION REVIEWED | Home wrappers permit overflow; ordinary rails span display width with original leading content inset as padding; actual card pixels appear at x=10 beyond the old x=28 boundary. First/middle/last focus and edge renders reviewed. User checks both carousels at physical display edges. |
| H4 | IMPLEMENTED; AUTOMATED VALIDATED; RENDER/INTERACTION REVIEWED | More Info is the sole Featured action; existing normal Details callback retained; LEFT/RIGHT browses items with no action-selection layer. User checks OK opens Details and playback remains there. |
| H5 | IMPLEMENTED; AUTOMATED VALIDATED; RENDER/INTERACTION REVIEWED | Fixed 86dp title/logo zone, 24dp breathing space before metadata, 12dp before synopsis and lower More Info; font sizes and official artwork treatment retained. Actual official-logo drawable rendered for wide/tall/compact shapes, including TV metadata; baseline stays stable. User checks real logos/movie/TV copy. |
| H6 | USER SHIELD ACCEPTED; REGRESSION PROTECTED; AUTOMATED VALIDATED | Thumbnail presenter unchanged; mask/scaling/glow regressions pass; actual first/middle/last focused unit/image alignment reviewed. Recheck the accepted behaviour on the replacement, particularly after H3 edge changes. No new device acceptance is self-assigned. |
| H7 | IMPLEMENTED; AUTOMATED VALIDATED; RENDER/INTERACTION REVIEWED | Actual row sibling controls, no floating surface/elevation; row ancestry/alignment, both row positions, Up/Down, Back/Right exact opener, Move/reorder return, Hide/library preservation and valid remaining focus pass. User checks real-remote Move/Hide on multiple rows. |

See [implementation decisions, files/tests and checkpoint history](home-hotfix-implementation.md). Latest HOME §12/QA hotfix amendments supersede older two-action, shorter-neighbour and full-row-fit assumptions. No historical mock-up was substituted.

Rendered gradients and white logo silhouettes are synthetic fixture assets inside production components, not recreated historical originals or claims of real provider artwork delivery. Automated Android/Robolectric/API-28 AVD checks do not confer Shield visual/interaction acceptance. Large libraries, provider art/network timing, remote repeat cadence and physical performance remain user device QA.

## Handoff / stop point

Implementation, automated/build validation, applicable render/focus/interaction review and signed replacement APK production are complete. H1–H5/H7 physical Shield validation is pending. H6's previous user acceptance is preserved as a regression protection; the replacement APK is not physically accepted or Final.

Install this exact source/hash candidate, review H1–H7 on Shield, then resume the wider physical QA. Record device outcomes in GitHub. Do not implement broader outstanding items, merge main, change identity/signing or begin another release as part of this turnaround.
