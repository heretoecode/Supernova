# Supernova 0.138 build and QA provenance

The complete approved software scope is implemented and an exact-source unsigned release candidate is verified. It is not yet an installable, signed Shield distribution or a physical Shield acceptance result. The [48-item conformance matrix](CONFORMANCE.md) retains every handover requirement; [SHIELD_QA.md](SHIELD_QA.md) covers the remaining device/live-service checks and explicit distribution gate.

## Exact tested candidate

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

After the tested application commit, `b47582af3337ec2aef8bc7f50c2c852968771890` fixes clean-runner native prerequisite ordering and pins the wrapper NDK, and `074d7c4a90bafdf6ce487ecf0b2f8b4f773c092f` registers the new signing workflow using a push event for which all signing jobs are disabled. These commits change the recipe, workflow and its Python guard test only; application Java/resources and Android tests are unchanged. The subsequent evidence commit adds documentation/receipts/renders only. The protected signing workflow will rebuild and re-run conformance on its exact dispatched branch SHA before accessing the existing key; it does not reuse this APK under a different source label.

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
| Signing/distribution | Not performed; existing key and protected-environment policies retained |
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

Unsigned conformance runs without signing secrets on the isolated branch. The clean-runner recipe correction is independently built locally and is being checked in fresh CI. Current run/status is linked from PR #13; do not mistake an earlier failed or cancelled checkpoint for the final candidate, or a signing-registration **skipped** run for a signed build.

Signing workflow ID `381805661` is now registered as `.github/workflows/supernova-0138-qa.yml`. Its push registration run `38207141176` completed **skipped**; every signing job requires `workflow_dispatch`, the exact 0.138 ref and explicit `approved_qa_distribution=true`. It still performs complete source/native/test/lint/unsigned conformance before decoding a key. The existing protected environment currently excludes the new branch; adding only that exact branch and signing/distributing the QA APK need approval under the handover. Existing branch restrictions, certificate pin, key, main and 0.137 history remain intact.
