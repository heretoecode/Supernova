# Supernova 0.139 — first signed corrective QA candidate

A final dialog-surface correction is in validation; the verified functional candidate below remains available. Its complete record is preserved in RELEASE_NOTES_FIRST_CORRECTIVE_QA_2026-10-16.md.

16 October 2026. **Supernova_0.139.apk**, package **app.supernova.player**, version **0.139**, version code **139**.

[Download the corrective APK](https://drive.google.com/uc?export=download&id=1w3tJRfyvBv0c16OKYzKFVlF22ou9SS_R) · [Drive file](https://drive.google.com/file/d/1w3tJRfyvBv0c16OKYzKFVlF22ou9SS_R/view?usp=drivesdk) · [Successful signing workflow](https://github.com/heretoecode/Supernova/actions/runs/38899461080)

## Confirmed repairs

- Playback launch: initialize the HUD time formatter before the first clock update. The original signed APK resolved and prepared owned local media, then crashed because its formatter did not exist when duration was rendered. The cold-initialization regression reproduces the same exception; the signed correction establishes playback output on software Android TV. The backend, source resolution and permission logic are preserved.
- Diagnostics: restore the Advanced preference ancestor and hide About's extra workspace on reverse entry. Diagnostic Logging, restored Diagnostic Level and Export Diagnostic Report are reachable with the D-pad. Normal/Detailed (QA) retain the existing preference values and detailed, bounded default.
- Settings: nested sections explicitly take ownership of the visible panes. The right reading pane can receive focus and return to the selected centre control.
- Backup & Restore: retain the Settings rail, a vertical Export Backup / Restore Backup list and the selected option's description. Deliberate archive operations use a separate category-selection dialog. Left closes restore review before the next Export. All 26 codecs, privacy defaults, selected replacement, explicit confirmation, cancellation/retry and recreation handling remain.
- Approved Details selected/focus and vertical-overflow behavior, existing 0.139 features, application identity and signing certificate are preserved. PR17 remains draft; no main merge or 0.140 work.

## Verification and provenance

Exact corrective APK source: **`92eac913be5a855d23c5c63a5ab966cc2c04e8a3`**. Signing run **38899461080** passed its full exact-source/native/conformance gates before signing. Both original implementation23f75b38b18f6b34f5ceb94c001260f08bafac09 and original delivered source4e4d2710435ddd9058923a3d9fbd6a2fc27e0464 remain ancestors. Later release-record commits change documentation only.

- Full application suite: **1,014 tests across 211 classes, zero failures/errors/skips**, independently checked against the unsigned CI artifact.
- Release lint: **0 errors / 1,772 warnings**. The added warning is a localization hint for Backup's programmatic description. **81 Python guards** pass.
- Native evidence:24 rebuilt FFmpeg libraries and 64 paired software decode cases across four ABIs; all 88 packaged native hashes independently checked. Corresponding source remains in the workflow artifact.
- Signature, same existing certificate, app identity/version, non-debuggable manifest, ZIP CRC/duplicates, branding/provider isolation, four ABI coverage and 16 KiB alignment pass. Static compatibility is checked over API23–36 with minSdk23/targetSdk36. Exact signed-to-tested-unsigned payload parity passes in the protected workflow.
- Controlled API28/x86 Android TV: signed139→corrective139 install returned Success without uninstall/reset; package UID and first-install time were retained. The same owned H.264/AAC input establishes playback output past the prior HUD crash. This is a software-emulator result.
- APK filename: **Supernova_0.139.apk**; size **87874512 bytes**.
- APK SHA-256: **`cccf3119e86a33d6d8ca18bd5c1f06ca06ac8acd5935f6f191ad33eb7f000ede`**.
- Existing signing certificate SHA-256: **`79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`**.
- Drive fileID **1w3tJRfyvBv0c16OKYzKFVlF22ou9SS_R**, existing APKs folder **1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM**. Downloaded complete bytes, size and SHA-256 match the independently verified signed artifact. The original 15 October APK is retained separately; use the corrective link/checksum above.

## Acceptance limits

The owner physically installed the original 0.139 and reported failures. Its post-prepare formatter crash was independently reproduced on software Android TV; a current Shield log is unavailable, so the exact owner-device cause is not independently attributed. The named Settings recording, screenshot and10October diagnostic ZIP were unavailable in this context; the old ZIP is not a current crash log.

**NOT RUN:** corrective physical Shield retest, physical138→139 signed upgrade, hardware decode/output/HDR/passthrough, the full local/network source and format matrix, and remaining whole-workstream acceptance. Automated and emulator passes do not close those gates.

Original delivery history and earlier0.139 feature notes are retained in [the original signed-QA notes](RELEASE_NOTES_ORIGINAL_SIGNED_QA_2026-10-15.md). Detailed causes, negative regressions and local evidence are in [the repair investigation](SHIELD_REPAIR_2026-10-16.md).

Actual signed D-pad/Backup screenshots were inspected. This emulator lacks a document provider, so destination/save/readback of a diagnostic ZIP is not verified. Final dialog backdrop correction and its new exact-source signing are pending.
