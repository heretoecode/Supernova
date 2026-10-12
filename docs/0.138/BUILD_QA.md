# Supernova 0.138 build and QA provenance

The complete approved software scope is implemented and the user-approved signed Shield QA candidate is verified. Physical Shield acceptance remains unverified. The [48-item conformance matrix](CONFORMANCE.md) retains every handover requirement; [SHIELD_QA.md](SHIELD_QA.md) covers the remaining device/live-service checks and explicit distribution gate.

## Signed Shield QA candidate

Protected [signing run 38239064325](https://github.com/heretoecode/Supernova/actions/runs/38239064325) completed **success** on exact source `b3504c2c4b1725f3070416d14b63e53ae1db3d74` after rebuilding the pinned dependencies, FFmpeg, native wrappers and application and repeating all 564 release tests, Python tooling checks, lint and unsigned conformance. The key and certificate are unchanged. The user approved only QA signing/distribution; no public release or merge occurred.

| Field | Verified value |
|---|---|
| APK | `Supernova_0.138.apk`, **103518717 bytes** |
| APK SHA-256 | `6f46a40c9698e9219b6e26c2f09277de93462d3b9721a9b0b2bfd64814cd04ee` |
| Application / version | `app.supernova.player` / `0.138` / code `138` |
| Existing certificate SHA-256 | `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` |
| Signed QA download | [artifact 11744230421](https://github.com/heretoecode/Supernova/actions/runs/38239064325/artifacts/11744230421), expires `2026-10-26T11:25:07Z` |
| Corresponding FFmpeg source | [artifact 11744035621](https://github.com/heretoecode/Supernova/actions/runs/38239064325/artifacts/11744035621), expires `2026-11-11T11:25:12Z` |
| Automated validation | 564 release tests, zero failures/errors/skips; lint zero errors / 1500 warnings; 138 runtime hashes, 92 native hashes, 24 FFmpeg libraries; 64 native decode comparisons |
| Independent downloaded APK checks | Signature/certificate, identity/version/providers/branding/legal assets, SHA-256, ZIP CRC, no duplicate entries, 16 KiB zip alignment, all 92 native hashes and all 24 FFmpeg library hashes **passed** |
| Payload parity | Exact unsigned-to-signed parity **passed in CI**. Not repeated locally because the unsigned CI APK is not distributed. |
| Shield installation / upgrade / hardware playback | **UNVERIFIED**; final acceptance follows the user's Shield testing |

[SIGNED_QA_VERIFICATION.json](SIGNED_QA_VERIFICATION.json) records independent verification and exact artifact archive digests/expiry. [SIGNED_QA_CI_EVIDENCE.json](SIGNED_QA_CI_EVIDENCE.json) retains the original public CI receipt, including its exact-source unsigned proof. The documentation receipt commit that follows this build changes no application source; it must not be substituted for the APK's source commit.

## Original local unsigned candidate

| Field | Verified value |
|---|---|
| Application source/test commit | `9f9b46fbe1bc61972d98ef21681ff3c368c349f9` |
| Continuation parent | PR #12 head `45521c370563c8c176b0c3d9435fbf08fcd115e0` |
| Review | Draft PR [#13](https://github.com/heretoecode/Supernova/pull/13), stacked on preserved 0.137 branch |
| Application identity | `app.supernova.player` |
| Version | `0.138` / code `138` |
| Variant | `noamazonRelease`, universal, Foundation verification + Supernova QA |
| Private local unsigned filename | `app.supernova.player-138-0.138-universal-release-unsigned.apk` |
| APK bytes | `103341454` |
| APK SHA-256 | `e32ed83ce2bfd1c51492921e56541b79c444a27924589cb409df5be28ee95904` |
| Signature | Unsigned; apksigner rejects it, as required before protected signing |
| ZIP CRC | Passed for every archive entry |
| Required eventual certificate | `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` — same actual verified 0.137 certificate |
| Local Android release build | BUILD SUCCESSFUL, 18m 24s; exact commit above |

The receipt is [UNSIGNED_CONFORMANCE.json](UNSIGNED_CONFORMANCE.json). It derives identity, resources, providers, runtime hashes, native hashes and completed test/lint outcomes from the real built APK. The exact unsigned candidate also passed `supernova_0138_qa_signing.gate`; no keystore was decoded. No APK is committed or uploaded by this evidence checkpoint.

After the tested application commit, `b47582af3337ec2aef8bc7f50c2c852968771890` fixes clean-runner native prerequisite ordering and pins the wrapper NDK, and `074d7c4a90bafdf6ce487ecf0b2f8b4f773c092f` registers the new signing workflow using a push event for which all signing jobs are disabled. These commits change the recipe, workflow and its Python guard test only; application Java/resources and Android tests are unchanged. The subsequent evidence commit adds documentation/receipts/renders only. The protected signing run recorded above rebuilt and re-ran conformance on its exact dispatched branch SHA before accessing the existing key; it did not reuse this local APK under a different source label.

## Verification

| Check | Outcome / receipt |
|---|---|
| Preserved 0.137 baseline | 528 tests, 136 classes; no failures/errors/skips; original signed APK digest/certificate/version/CRC independently checked in [BASELINE_0137_VERIFICATION.json](BASELINE_0137_VERIFICATION.json) |
| Complete 0.138 release suite | **564 tests / 142 classes; 0 failures, 0 errors, 0 skips** |
| Python build/signing/source tooling | **72 passed**, including exact-source/payload/identity, missing-regression, failed/skipped tests, protected environment and manual-only signing gates |
| Release lint | **0 errors/fatal issues; 1500 warnings**. Warning count is disclosed; no clean-lint claim or warning suppression was introduced. |
| Reviewed runtime artifacts | **138 actual artifact hashes match** `docs/foundation/RUNTIME_SBOM.json` |
| Packaged native libraries | **92 compiled/stripped package hashes match**: 88 native module/prebuilt inputs plus four ABI C++ runtime libraries |
| Rebuilt LGPL FFmpeg | **24 actual APK libraries match** four ABI build/configuration/export/licence receipts in [FFMPEG_NATIVE_BUILD.json](FFMPEG_NATIVE_BUILD.json) |
| Native runtime comparison | All four ABI registry/tempo checks and **64 decode comparisons passed**, [NATIVE_RUNTIME_REGRESSION.json](NATIVE_RUNTIME_REGRESSION.json) |
| Isolated native wrapper build | Fresh AVOS/libyuv/nativehelper/FileCore worktrees without existing wrapper obj/libraries compile successfully with full FFmpeg and NDK **29.0.14206865**; **88 inputs** recorded in [CLEAN_NATIVE_BUILD.json](CLEAN_NATIVE_BUILD.json) |
| Application layout/focus evidence | [12 actual app-view renders](ui/README.md), synthetic fixtures; exact test-source hash in [render provenance](ui/RENDER_PROVENANCE.json) |
| Signing/distribution | User-approved QA artifact produced in run 38239064325; existing key/certificate retained and only the exact 0.138 branch added to the existing allowlist |
| Physical Shield / historical logs / live account/provider | UNKNOWN; no device or authorised diagnostic export supplied, and public terms/reference retrieval blocked |

The native runtime harness runs Android API 23 bionic: x86_64 directly and the other architectures through QEMU user emulation. It is a real native decoder/registry comparison, not Android framework, MediaCodec, Shield hardware, passthrough, HDR output, startup-latency or playback acceptance proof.

The preserved native wrapper build configuration is `BUILD=DEBUG`, as used by the existing 0.137 release pipeline. The application is an unsigned **release** variant and is not debuggable. An attempted native RELEASE configuration encountered inherited undefined stream-buffer functions; no unrelated native source workaround or dependency upgrade was introduced. Full wrappers are explicitly built before Gradle packaging. The clean-runner failure revealed that AVOS must follow libyuv/nativehelper static archives; the corrected recipe uses separate prerequisite/AVOS phases and the pinned NDK instead of selecting a runner's latest installed NDK.

The candidate package uses the exact main-workspace native inputs in [NATIVE_PACKAGING_INPUTS.json](NATIVE_PACKAGING_INPUTS.json), plus the NDK C++ runtime. The isolated clean build is an independent recipe check. Its 48 compiled wrapper-file hashes differ before stripping from the main-workspace copies (different build roots/object/debug/build-ID context); the 40 unchanged FFmpeg/prebuilt inputs match byte-for-byte. Neither receipt is relabelled as the other build, and APK verification compares the actual package with its own compiled/stripped inputs.

## Reproduction

Prepare pinned projects from `.github/build/nova-ci.xml`, apply the same 12 reviewed dependency patches as the existing 0.137 workflow, and run `prepare-foundation.py`. Keep the permanent Foundation identity and literal version counter; do not invoke Preview identity conversion.

Rebuild/review FFmpeg through `build-foundation-ffmpeg.py`, run `tools/verify_foundation_native_runtime.py`, then install the verified libraries. The corresponding source bundle was prepared locally from the reviewed allowlist, SHA-256 `0b0a0a930dcc79b9c124d8cc8864de736027ea13218cef58517f8bd1797b8b32`; no user recordings/logs or signing material enter it.

Install Android platform 37.0, build tools 36.0.0 and wrapper NDK 29.0.14206865. Use Java 21. The wrapper command is:

```sh
python3 .github/build/build-foundation-native-wrappers.py --root /path/to/nova \
  --native-evidence /path/to/verified-native-build.json \
  --output /path/to/native-wrappers.json
```

Then, in `nova/Video`, run:

```sh
OPERATION=verify ./gradlew foundationDependencyInventory \
  testNoamazonReleaseUnitTest lintNoamazonRelease assembleNoamazonRelease \
  -PfoundationVerify -PsupernovaQa -Puniversal --no-daemon --no-build-cache \
  --no-configuration-cache --max-workers=4 \
  -x :FileCoreLibrary:ndkBuild -x :MediaLib:ndkBuild
```

Those two Gradle tasks are excluded because their native work has already been completed and recorded explicitly, without upstream dependency bootstrap scripts. They are not waived native checks. Run `record-supernova-0138-conformance.py` against the resulting single unsigned APK, real SDK tools, full test XML, lint XML, runtime inventory and verified native evidence. It refuses incomplete tests, skips, missing 0.138 classes, lint errors, changed runtime/native inputs or a signed APK.

Local build output remains in the execution workspace. Public evidence is allowlisted; full build stdout, user diagnostics and private media are not committed.

## CI and release gate

Unsigned conformance [38207140954](https://github.com/heretoecode/Supernova/actions/runs/38207140954) completed **success** on source `074d7c4a90bafdf6ce487ecf0b2f8b4f773c092f`. The subsequent `b3504c2c4b1725f3070416d14b63e53ae1db3d74` commit adds evidence/documentation only. Signed QA [38239064325](https://github.com/heretoecode/Supernova/actions/runs/38239064325) independently rebuilt and repeated the complete checks on that exact source and completed **success**. Earlier failed/cancelled checkpoints remain historical; the signing registration run `38207141176` was **skipped**, not a signed build.

Signing workflow ID `381805661` is `.github/workflows/supernova-0138-qa.yml`. Its job requires manual dispatch, the exact 0.138 ref and `approved_qa_distribution=true`. After explicit user approval, the integration's environment-policy POST returned HTTP 403; the user added only `codex/supernova-0.138-qa-2026-10-12` (policy `62775528`). All four prior entries remain. The successful protected run verified the existing certificate, signed the exact tested unsigned APK, checked payload parity and removed temporary signing material before uploading its two allowlisted QA artifacts. No key was downloaded locally. Main, 0.137, application identity and key/certificate arrangements remain unchanged; PR #13 remains draft and unmerged. This approval covers QA distribution only, not final acceptance or a public release.
