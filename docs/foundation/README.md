# Foundation implementation evidence

Work remains isolated to `codex/foundation-release`. **Corrected signed Foundation 0.134 / 134 is produced and independently verified.** [Download APK artifact ZIP](https://github.com/heretoecode/Supernova/actions/runs/37961864830/artifacts/11632643927). Source **cf1211ceea12a6e1ae82e10cbfb247d33d4ad01f**, protected run [37961864830](https://github.com/heretoecode/Supernova/actions/runs/37961864830). First 0.133 installed successfully but revealed two Home source regressions; the targeted correction is backed by actual-view/pixel tests, source/binary comparisons and full cold CI. **Corrected Shield installation/playback and UI acceptance remain pending.**

- [Final signed delivery index and checksums](UI_SIGNED_DELIVERY.json)
- [Independent downloaded APK verification](UI_CORRECTED_SIGNED_APK_VERIFICATION.json)
- [Cold CI test evidence](UI_CORRECTED_CI_RESULTS.json)

- [Regression investigation and commit/file evidence](FOUNDATION_UI_REGRESSION_INVESTIGATION.md)
- [Targeted correction, regression tests and delivery status](FOUNDATION_UI_CORRECTION.md)
- [Other source-confirmed omissions](PREVIEW_OMISSION_REGISTER.json)
- [Independently verified first signed APK](FIRST_SIGNED_APK_VERIFICATION.json)

- [Current authorised signing/retry status](SIGNING_BUILD_STATUS.md)
- [Implementation report and blockers](IMPLEMENTATION_REPORT.md)
- [Machine-readable readiness](READINESS.json)
- [Three-pass Foundation conformance review](CONFORMANCE_REVIEW.md)
- [Candidate release notes](RELEASE_NOTES.md)
- [Automated/device QA checklist](QA_CHECKLIST.md)
- [Historical APK hashes, manifests and sources](APK_HISTORY.json)
- [Branding export hashes/dimensions](BRANDING_EXPORTS.json)
- [Resolved runtime/desugaring SBOM and legal provenance](RUNTIME_SBOM.json)
- [Canonical cold CI proof index](CI_REHEARSAL_INDEX.json)
- [Committed-source local unsigned APK evidence](UNSIGNED_BUILD_EVIDENCE.json)
- [Actual unsigned native ELF inventory](NATIVE_BINARY_INVENTORY.json)
- [Native legal/source evidence](NATIVE_LEGAL_PROVENANCE.json)
- [Verified FFmpeg source rebuild and recipes](FFMPEG_REBUILD.md)
- [Four-ABI native regression evidence](FFMPEG_NATIVE_REGRESSION.json)
- [Existing protected signing setup](../project/FOUNDATION_SIGNING_SETUP.md)

The candidate and previous checkpoint APK evidence are distinguished in the report. Source/asset changes after an APK build require renewed verification. A successful unsigned build cannot satisfy signed APK delivery. Physical NVIDIA Shield checks remain pending until the user tests the delivered artifact.
