# Supernova 0.145 / code 145 — signed QA delivery

[Signed Supernova_0.145.apk](https://drive.google.com/file/d/10HmVAOilIRsT8iRcyLyOktz_vgS7uCeM/view?usp=drivesdk) — **87,940,129 bytes**. Exact application source: `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60` on `codex/supernova-0.145-maintenance-2026-10-28`. [Draft PR #25](https://github.com/heretoecode/Supernova/pull/25) remains stacked on preserved 0.144. Main is unmerged and no public release was published.

SHA-256: `1cd4d3c6dcbe9c8c02c3abae26ba4fb37b99d73e0fc68e6a0cf0ca43c2b0c2c8`

Certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`

## Baseline and upstream integration

The 0.144 baseline tip is `43cef0529518d406e11b3ab69e61abded07a12ad`, delivered app source `4e6f1f8f5691c34cd429c17421f92b6290ce3539`. Full Drive byte readback, ZIP CRC and independent apksig certificate verification passed ([baseline receipt](BASELINE_RECHECK.json)).

Official latest stable checked October 28: NOVA **v6.6.2**, AVP `5c5d4ec81b47ba59be8f91c9d877b49b0ab1c01e`, compared with original v6.4.64 AVP `9c5842b74ab180c99441ed23fdd5d508448388a7`. Release-publication-time Video snapshot is `842ac7592ea9188b60a34acd08a450b88d0c4545`. The stable manifest selects mutable component branches and its tag gitlinks are stale; that snapshot is **not proven to be the exact source of NOVA's released APK**. [Inventory](UPSTREAM_DELTA.json) and [per-commit matrix](UPSTREAM_MATRIX.md) retain this provenance limit and all deferred/excluded work.

Seven included/adapted upstream fixes:

| Source commit | Port / adaptation |
|---|---|
| Video `d0be0f1161fb9521ce9f6da44522f4b7e2e6fc9c` | Reopen failed network playback and retain latest requested seek with Supernova session bookkeeping. |
| Video `12cdaea1e5889bef6823a94774c42b1e7074605b` | Paused subtitle duration, monotonic timing, cue clearing and worker teardown. |
| Video `d273346c98bdf809812f5f2e35ec288825ab2b7c` | Synchronized HDMI receiver registration guard; retain working background unregister callers. |
| Video `ffd0eb50cfb1791b837b84b7bd142323468ee172` | Keep Supernova's media-suggestion provider private instead of exporting it for NOVA search. |
| MediaLib `cf75fdcd85822d1f58d90ec35d72f9a59e4a6672` | Internal subtitle-cache fallback when external cache cannot be created; omit raw-path logs. |
| FileCore `868496a9ab` | Guard legacy MediaStore mutations without legacy/all-files access. |
| AVOS `96d5f4fb55` | Bound stream-buffer allocation arithmetic, ring overlap and prebuffer scaling before narrowing. |

Additional maintenance pins the official Gradle distribution checksum and mitigates the legacy Jetty UPnP boundary. UI resources/branding and key Settings/library/Details/HUD sources match 0.144 Git objects ([source preservation](UI_PRESERVATION.json)). No schema, stored preference key, identity or minimum-SDK change is included. Torrent reintroduction and upstream UI replacement are excluded.

## Validation and signing

The requested corrected-source [conformance run 40753216952](https://github.com/heretoecode/Supernova/actions/runs/40753216952) passed at `c92c68d617cc6fe1e11a1f5cdd032a919811ffaa`: independently retrieved artifact digest and **1,147 XML tests, zero failures/errors/skips**, lint zero errors/1,795 warnings. Earlier three storage fixture failures were corrected explicitly; partial runs were not substituted for full-suite clearance. [Readback receipt](CORRECTED_CONFORMANCE_READBACK.json).

The first security build 40758854740 stopped before signing because the app test compile classpath did not expose MediaLib implementation APIs. The correction adds test-only declarations matching the existing servlet/client runtime pins; no app runtime hash changes. [Correction receipt](JETTY_CI_CORRECTION.json).

The final protected [exact-source QA/signing run 40762577939](https://github.com/heretoecode/Supernova/actions/runs/40762577939) **passed** on `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60`:

- **1,158 Android/unit/integration tests**, zero failures/errors/skips, including 12 upstream-port cases and 11 actual-library Jetty cases.
- **128 tooling checks**, sanitized C buffer-boundary regression and native runtime regression pass. Release lint: **zero errors, 1,795 warnings**; warnings are retained debt.
- **138 runtime artifact hashes**, **24 rebuilt FFmpeg libraries** and **88 packaged native hashes** match. Patched wrappers were built for four ABIs.
- Existing-certificate signing, exact tested-unsigned payload parity, source/version/identity, native/ZIP alignment gates and temporary signing-material cleanup pass.

Final test totals come from the CI-generated unsigned conformance embedded in the public signed proof; this signing workflow does not publish raw JUnit XML. The earlier corrected-source XML was independently recounted as recorded above.

Independent downloaded-APK verification passed apksig signature/certificate, `app.supernova.player`, 0.145/code145, min SDK23/target36, ZIP CRC and all 88 native hashes across arm64-v8a/armeabi-v7a/x86/x86_64. Every ELF load segment supports 16KB alignment; compressed libraries use extraction, with ZIP page alignment checked if an entry is uncompressed. The complete uploaded Drive APK was downloaded again and matched byte size and SHA-256.

[Delivery receipt](SIGNED_QA_DELIVERY.json), [signed public proof](SIGNED_PUBLIC_QA_EVIDENCE.json), [unsigned conformance](CONFORMANCE_40762577939.json), [independent verification](INDEPENDENT_APK_VERIFICATION.json) and native evidence retain provenance. The [corresponding FFmpeg source bundle](https://drive.google.com/file/d/1jsId2rJ10jyZ3bvuArKfB4QCHSGrx_o3/view?usp=drivesdk) was retrieved, its GitHub archive digest verified, preserved privately in the existing Supernova Drive folder and fully downloaded for checksum verification; see [source receipt](NATIVE_SOURCE_RECEIPT.json). Later delivery-document commits do not change the frozen application source above.

The existing [HTML release notes](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view) are updated in place after delivery. The final receipt records full byte readback, all 13 historical articles preserved and all 11 previous APK files retained.

## Nine Jetty advisories

[JETTY_SECURITY_DISPOSITION.md](JETTY_SECURITY_DISPOSITION.md) reviews every advisory. Safe fixes reject ambiguous/signed/overflowing framing, close each incoming exchange, suppress exception/path/server-version disclosure and validate discovered outgoing endpoint scheme/host with JDK URI before legacy Jetty. Eleven integration cases pass; the old-server negative control failed five of nine server tests as expected. Normal UPnP methods, bodies, async callbacks, encoded paths, IPv6 and SOAP fault bodies are covered.

The bundled Jetty 9.2.30 version **still triggers all nine package-version findings**. TLS/session/multipart/DefaultServlet/cookie/standalone-validator prerequisites are assessed individually; absence of a configured chain is not a global library fix. A supported API-23-compatible Jetty/jUPnP migration or maintained backport still needs Android/provider/API23 and provenance/legal validation. No SDK-floor increase, feature removal, advisory suppression or TLS weakening was used.

## Outstanding QA and deferred work

- Physical Nvidia Shield, real provider/storage/account/AV-chain, subtitle/HUD/focus and signed installation/upgrade/rollback acceptance remain pending.
- Original Concept A4/hero reference images and October 27 diagnostic ZIP were not found in fresh connected-source searches; exact reference comparison and original-report-only re-analysis are unverified.
- Nine Jetty dependency-version advisories remain scan-affected. UPnP boundary mitigations and individual prerequisite reviews are documented; supported API-23-compatible transport migration/backports and real-provider regression remain deferred.
- Native libass/subtitle-engine, FFmpeg 9 and OpenSSL 4.1 migration and individual unported upstream deltas remain deferred for coordinated ABI, source, security, licensing and device compatibility review.
- No measured physical startup, playback-performance or retained-memory profiling campaign was possible; no hardware performance/leak clearance is claimed.

[QA checklist](QA_CHECKLIST.md), [software visual review](VISUAL_REVIEW.md), [security review](SECURITY_REVIEW.md) and [diagnostic review](DIAGNOSTIC_REVIEW.md) distinguish source/software results from physical acceptance. This is a delivered signed QA candidate. Full maintenance and release acceptance remain incomplete where the above requirements are unverified or deferred.
