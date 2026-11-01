> Current source continuation: [original-reference and raw Shield investigation](OWNER_REFERENCE_AND_RAW_DIAGNOSTICS_REVIEW_2026-11-01.md). These candidate corrections are **not in the previously delivered APK linked below**. No replacement APK has been built. Shield acceptance remains open; PR #30 stays draft/unmerged. The delivery record below is historical.

# 0.149 Shield QA APK — delivered and download verified

[Download Supernova_0.149.apk](https://drive.google.com/file/d/1ETsEfYnMaiJPcjnl1mjXFxgKOO7ilVLG/view?usp=drivesdk)

| Item | Verified value |
|---|---|
| Version / versionCode | **0.149 / 149** |
| Package | `app.supernova.player` |
| Filename / bytes | `Supernova_0.149.apk` / **95,598,263** |
| SHA-256 | `cf589e8b78a28267b76892325d628244afe932a3fc140f6f4443054f78cbaa6c` |
| Tested APK source | `cb28b1b936891374bc801f0b58b3397926fc3086` |
| Signing | Existing protected environment, [run 41331247034 attempt 2](https://github.com/heretoecode/Supernova/actions/runs/41331247034/attempts/2), successful; no approval pending |
| Existing certificate | `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` |
| Drive verification | Complete raw download byte-identical to verified signed artifact; signature/manifest/payload checks repeated successfully |

Draft [PR #30](https://github.com/heretoecode/Supernova/pull/30), branch `codex/agreed-fixes-2026-11-01`. The owner resolved the environment permission blocker and restarted signing. This delivery continuation made **documentation-only changes**: no development repeated, completed fixes modified, new APK build/version started, signing identity changed, branch merge, production release or tag. The signed payload is the exact tested unsigned candidate; only APK signing data differs.

| Objective | Actual status |
|---|---|
| Preserve all ten completed fixes | PASS: original implementation files frozen by scope checks; all regression classes ran in full suite. No restarted/duplicated implementation. |
| Approved Subtitles main/Track/Download | Implemented using blueprint v2, Image B main and only Image A's right-side popups. Existing provider/menu/renderer/preference contracts retained. |
| Combined validation | PASS: 1,276 application cases; 300 library passes plus four existing skips; 147 tooling tests; release build/lint, zero errors and 1,252 warnings. 33 focused cases also passed. Existing results reused for the identical source; no development/test cycle repeated for delivery. |
| Signed installable Shield QA APK | PASS: existing certificate verifies for API23–36, v1/v2/v3; manifest, ZIP integrity, alignment, 84 native hashes, 64 offline licences and shrunk flag/provider resources independently verified. Package/certificate and increasing versionCode match 0.148 upgrade prerequisites. Actual installed upgrade remains NOT RUN. |
| Existing Drive upload/full download verification | PASS: one 0.149 APK added; all 16 previous entries preserved; complete 95,598,263-byte download matches signed artifact and verifies again. No sharing/existing-file changes. |
| Physical/emulator QA | PENDING: emulator/Shield visual parity, real track rendering, live provider download, installed upgrade and hardware acceptance remain unverified. |

[Delivery receipt](DRIVE_APK_DELIVERY.json) · [Drive readback verification](DRIVE_APK_READBACK_VERIFICATION.json) · [independent signed APK checks](INDEPENDENT_SIGNED_APK_VERIFICATION.json) · [signing evidence](SIGNED_QA_EVIDENCE.json) · [test/lint evidence](FINAL_VALIDATION.json) · [signing history](SIGNING_RESUME.md).

[Implementation/mappings/deviations](IMPLEMENTATION.md) · [approved-image software comparisons](VISUAL_REVIEW.md) · [physical Shield checklist](SHIELD_QA.md).

Position labels retain the existing bottom-third adjustment range; delay changes are 100 ms in existing millisecond storage; size is unavailable where the provider lacks it; search filters returned results. Audio/Video/More and HUD were not redesigned; legacy tab fixture PNGs remain byte-identical. Shield acceptance includes all ten prior fixes, actual three-surface visual comparison/remote timing, persisted appearance/delay, real subtitle playback/download association and upgrade data preservation.
