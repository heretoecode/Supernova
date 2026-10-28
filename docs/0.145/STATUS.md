# Supernova 0.145 — signed QA delivered; physical acceptance pending

Frozen application source: `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60`. [Draft PR #25](https://github.com/heretoecode/Supernova/pull/25), branch `codex/supernova-0.145-maintenance-2026-10-28`. No main merge or public release.

[Signed APK](https://drive.google.com/file/d/10HmVAOilIRsT8iRcyLyOktz_vgS7uCeM/view?usp=drivesdk) — 87,940,129 bytes; SHA-256 `1cd4d3c6dcbe9c8c02c3abae26ba4fb37b99d73e0fc68e6a0cf0ca43c2b0c2c8`. Existing certificate independently verified. Full Drive byte readback matches. [Final protected QA run](https://github.com/heretoecode/Supernova/actions/runs/40762577939) passed 1,158 Android tests with no failures/errors/skips, 128 tooling checks, zero lint errors (1,795 warnings), 138 runtime hashes, 24 rebuilt FFmpeg libraries and 88 packaged native hashes. Tested-payload parity and temporary-key cleanup passed. Corrected-source conformance 40753216952 passed first with 1,147 tests.

Seven compatible NOVA upstream ports and safe Jetty boundary mitigations are included; Supernova UI, identity, min SDK and stored data contracts are preserved. All nine Jetty findings have individual dispositions; package-level advisories remain unresolved pending compatible transport migration/backports. [Release report](RELEASE_REPORT.md) records baseline/upstream SHAs, all evidence and genuine deferred work.

The owner-authorised exact branch used the existing signing environment. The previous signing-policy blocker is resolved. Previous CLI artifact transport restrictions were overcome using the GitHub connector. Later documentation commits record delivery only and do not redefine the APK's source.

- Physical Nvidia Shield, real provider/storage/account/AV-chain, subtitle/HUD/focus and signed installation/upgrade/rollback acceptance remain pending.
- Original Concept A4/hero reference images and October 27 diagnostic ZIP were not found in fresh connected-source searches; exact reference comparison and original-report-only re-analysis are unverified.
- Nine Jetty dependency-version advisories remain scan-affected. UPnP boundary mitigations and individual prerequisite reviews are documented; supported API-23-compatible transport migration/backports and real-provider regression remain deferred.
- Native libass/subtitle-engine, FFmpeg 9 and OpenSSL 4.1 migration and individual unported upstream deltas remain deferred for coordinated ABI, source, security, licensing and device compatibility review.
- No measured physical startup, playback-performance or retained-memory profiling campaign was possible; no hardware performance/leak clearance is claimed.

Signed QA delivery is complete. Full maintenance/release acceptance is not claimed complete.
