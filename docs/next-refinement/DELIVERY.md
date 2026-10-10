# Supernova 0.136 signed QA delivery

[Download signed QA APK ZIP](https://github.com/heretoecode/Supernova/actions/runs/38093972688/artifacts/11685453791). Extract `Supernova-0.136-Refinement-QA.apk`; the ZIP also contains original public QA evidence. Artifact expires 2026-10-24T23:39:32Z. [Corresponding FFmpeg source/native proof](https://github.com/heretoecode/Supernova/actions/runs/38093972688/artifacts/11685668489).

APK SHA-256 (not the ZIP archive digest):

```
2c315580483e1086435ac72ae9809e005435da5c4bb72a3538468c7d1eb86e3c
```

[Checksum file](Supernova-0.136-Refinement-QA.apk.sha256) · [Complete signed verification](SIGNED_QA_VERIFICATION.json).

## Verified identity and provenance

- Package: `app.supernova.player`; version: `0.136`; versionCode: `136`.
- Original 0.135 certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.
- Build commit: `00bb55e6082ecf4081f2e8e524e83b2bb75b2f13`; application checkpoint: `92900e4647d1ea614d2ef8f0e1dad9fb736e3e05`. Subsequent commits contain workflow/reporting changes only.
- [Signed QA run](https://github.com/heretoecode/Supernova/actions/runs/38093972688): clean build, original signing identity, tested unsigned payload parity, package/version/resources, ZIP integrity and secret-material cleanup PASS.
- [Independent uploaded-APK verification](https://github.com/heretoecode/Supernova/actions/runs/38095825877): actual artifact signature, package/version/resources, ZIP integrity, build provenance and APK hash against original public evidence PASS. This read-only job has no signing environment/key access and does not rebuild/sign the application.

All 514 tests across 132 classes passed without failures/errors/skips, all 58 Python tests passed, and release lint passed with zero errors / 1,494 warnings. Native/runtime gates passed: 138 reviewed runtime artifact hashes, 88 compiled/packaged native libraries, 24 rebuilt FFmpeg matches and four-ABI registries/tempo/64 decode comparisons.

The user admitted `codex/supernova-next-refinement-2026-10-10` to the existing `supernova-foundation-signing` environment. No replacement signing credentials were created. [Draft PR #10](https://github.com/heretoecode/Supernova/pull/10) remains open and unmerged; main is unchanged. This is provisional QA, not a published final release.

The workspace proxy still denies the GitHub artifact blob host. Verification was performed on the actual uploaded APK in GitHub Actions; no access restriction was bypassed. ZIP archive digests in API metadata are not independently verified locally.

## Remaining acceptance

Matching package/certificate and a higher version code preserve Android package-level upgrade compatibility with recorded 0.135. An actual install over the user's device/data has not been performed. Physical SHIELD focus/contrast/network/licence reproduction, representative playback and diagnostics soak remain pending; the original licence process-death cause remains unproven. Production put.io OAuth requires actual registered client/redirect configuration and end-to-end validation.

[Release notes](RELEASE_NOTES.md) · [All 45 requirements](COMPLETION_REPORT.md) · [Test results](TEST_RESULTS.json) · [Conformance review](CONFORMANCE_REVIEW.md) · [SHIELD checklist](SHIELD_QA.md) · [QA readiness](QA_READINESS.json) · [Progress](PROGRESS.md).
