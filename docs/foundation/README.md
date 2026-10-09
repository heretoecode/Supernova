# Foundation implementation evidence

Work is isolated to `codex/foundation-release`. **No signed Foundation APK has been produced; readiness remains false.**

- [Implementation report and blockers](IMPLEMENTATION_REPORT.md)
- [Machine-readable readiness](READINESS.json)
- [Candidate release notes](RELEASE_NOTES.md)
- [Automated/device QA checklist](QA_CHECKLIST.md)
- [Historical APK hashes, manifests and sources](APK_HISTORY.json)
- [Branding export hashes/dimensions](BRANDING_EXPORTS.json)
- [Resolved runtime/desugaring SBOM and legal provenance](RUNTIME_SBOM.json)
- [Actual unsigned native ELF inventory](NATIVE_BINARY_INVENTORY.json)
- [Native legal/source evidence](NATIVE_LEGAL_PROVENANCE.json)
- [Existing protected signing setup](../project/FOUNDATION_SIGNING_SETUP.md)

The candidate and previous checkpoint APK evidence are distinguished in the report. Source/asset changes after an APK build require renewed verification. A successful unsigned build cannot satisfy signed APK delivery. Physical NVIDIA Shield checks remain pending until the user tests the delivered artifact.
