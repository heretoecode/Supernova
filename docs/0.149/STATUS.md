# 0.149 QA continuation — signed delivery blocked

Draft [PR #30](https://github.com/heretoecode/Supernova/pull/30), branch `codex/agreed-fixes-2026-11-01`. Continues from owner handover `d84ab3ae37330d8eb65018f68fca53f55756c33a`. Tested APK source: **`cb28b1b936891374bc801f0b58b3397926fc3086`**. Later commits change documentation only. Main is unmerged; no production release/tag or signing identity change.

| Objective | Actual status |
|---|---|
| Preserve all ten completed fixes | PASS: original implementation files frozen by scope checks; all regression classes run in full suite. No restarted/duplicated implementation. |
| Approved Subtitles main/Track/Download | Implemented using blueprint v2, Image B main and only Image A's right-side popups. Existing provider/menu/renderer/preference contracts retained. |
| Combined validation | PASS: 1,276 application cases; 300 library passes plus four existing skips; 147 tooling tests; release build/lint, zero errors and 1,252 warnings. 33 focused cases also pass. Software comparisons inspected; emulator/Shield visual parity NOT VERIFIED. |
| Signed installable Shield QA APK | BLOCKED: GitHub rejected protected signing run 41331247034 before any steps because this branch is excluded from the existing signing environment. No key accessed. |
| Existing Drive upload/full download verification | NOT DONE: no signed 0.149 exists to upload. Folder read access and complete existing 0.148 readback verified; all 16 entries retained. |
| Documentation and QA | Updated implementation, evidence, comparisons, exact signing rejection/resume instructions and physical Shield checklist. |

Maximum verified non-release artifact: [unsigned APK + conformance receipt](https://github.com/heretoecode/Supernova/actions/runs/41328625056/artifacts/13093101711), retained 14 days. **This unsigned APK is not installable.**

- Filename: `Supernova_0.149_unsigned.apk`
- Package: `app.supernova.player`; version **0.149**, versionCode **149**; min SDK 23, target SDK 36; arm64-v8a, armeabi-v7a, x86, x86_64.
- Bytes: **95,398,440**
- SHA-256: **`ec453ae4a9cc4e9d34d4668270c8a243d52163d746741c70f862c24c0370437f`**
- [Full passing conformance](https://github.com/heretoecode/Supernova/actions/runs/41328625056) · [independent unsigned APK checks](INDEPENDENT_UNSIGNED_APK_VERIFICATION.json) · [test/lint XML receipt](FINAL_VALIDATION.json)
- [Actual signing rejection](https://github.com/heretoecode/Supernova/actions/runs/41331247034) · [resume signed delivery](SIGNING_RESUME.md) · [Drive status](DRIVE_STATUS.json)
- [Implementation/mappings/deviations](IMPLEMENTATION.md) · [three approved-image comparisons](VISUAL_REVIEW.md) · [physical Shield checklist](SHIELD_QA.md)

Remaining device acceptance includes installed upgrade from 0.148, all ten original fixes, actual three-surface visual comparisons/remote timing, real track rendering, persisted appearance/delay, and live provider download/association/error states. Position labels retain the existing bottom-third adjustment range; delay changes are 100 ms in existing millisecond storage; size is unavailable where the provider lacks it; search filters returned results. Audio/Video/More and HUD were not redesigned; legacy tab fixture PNGs remain byte-identical.
