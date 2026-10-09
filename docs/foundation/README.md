# Foundation implementation evidence

Work is isolated to `codex/foundation-release`. **All pre-signing readiness gates passed; awaiting final user approval. No signed Foundation APK has been produced; protected signing remains disabled.**

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
