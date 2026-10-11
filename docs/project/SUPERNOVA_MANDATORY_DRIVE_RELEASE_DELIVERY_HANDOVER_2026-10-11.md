# Supernova — mandatory release delivery handover (approved workflow)

**User decision 2026-10-11.** Applies to future **completed, validated QA APK deliveries**; not interim development snapshots. This is a durable requirement for the next Codex work session. **It does not mean CI-to-Drive authentication or automation is already configured.**

## Google Drive destinations (existing, verified)
- Parent **Supernova**: https://drive.google.com/drive/folders/1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ (folder ID `1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ`).
- Child **Supernova/APKs**: https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM (folder ID `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`).
- Existing root HTML: **Supernova Development.html**, Drive file ID `17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW`, https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view?usp=drivesdk. **Update this file in place**; don't generate duplicate changelog files.

## Required release-completion sequence
1. Complete implementation, source review, tests, conformance, native validation and signed QA build. Confirm exact commit, package, versionCode, signing certificate and APK SHA-256. A completed Codex run alone is not evidence of device QA acceptance.
2. Publish **only the final standalone signed .apk** into the **APKs** folder. No ZIP, JSON, checksum, FFmpeg source bundle or intermediary APK in the user-facing folder. **Mandatory filename: `Supernova_<version>.apk`**, for example `Supernova_0.137.apk` (or `Supernova_4.1.8.apk` when that is the actual app version). Use the exact version reported by the built APK; no `QA`, `Preview`, `Refinement`, timestamps, build numbers or extra suffixes in the filename. Retain prior versions. If the same version is rebuilt, do not silently overwrite the existing file: stop and request a version decision. Do not silently overwrite or duplicate. Technical QA evidence stays in GitHub Actions/repository records.
3. Update **the existing HTML file** in the parent Supernova folder. Preserve the dark responsive theme and all historical versions. Prepend a new release section with exact version, date/status and a detailed but approachable user-facing changelog; emojis: ✨ features, 🎨 visual/UX, 🛠️ fixes, ⚡ speed/stability, 🔒 privacy/security. Translate requirements into actual shipped changes. Do not claim unverified hardware fixes, user acceptance or put.io OAuth success. Parked features are not shipped changes.
4. Verify the APK exists as an individual file in the correct Drive folder and the HTML file content/version is updated, preserving the **same Drive file ID**. Record upload status, APK checksum and changelog version in release delivery evidence. If either fails, explicitly report **delivery incomplete** and retry safely. Do not claim success merely because a build finished.
5. Report to the user that the new APK is ready **in Supernova/APKs via Solid File Explorer on NVIDIA Shield**, plus brief changes. The user does not want to have to retrieve a GitHub artifact ZIP or QA evidence JSON.

## Authentication / implementation gate
- **Automation is not yet configured.** Codex/GitHub Actions does not inherit this ChatGPT session's Google Drive connector authentication.
- Before unattended uploads can work, design and explicitly authorise a narrowly scoped Drive publisher with access to only the destination folder/files. Prefer workload identity federation or a safely stored scoped secret; **never commit credentials or tokens**.
- Protect package/signing identity, no code changes or CI modifications until explicitly authorised. Run an end-to-end test of both uploads and verify retrieval on the user's Shield through Solid File Explorer.
- Google Drive HTML preview may not render as a hosted web page; confirm user-visible behaviour separately.

## Current state
Folder hierarchy and the populated historical dark HTML exist on Drive. Current installed 0.136 QA was delivered previously through a GitHub ZIP and user reports it installed. **No claim is made that its APK is currently in the Drive APK folder.** Future automatic delivery requires the secure publishing step above.

This requirement supplements `docs/project/SUPERNOVA_APK_DELIVERY_AND_CHANGELOG_REQUIREMENTS_2026-10-11.md`; it does not expand application feature scope, modify the active Codex refinement branch or approve release work outside a future authorised delivery workflow.

## Filename decision (2026-10-11)
**User-approved canonical delivery name:** `Supernova_<version>.apk` (underscore, exact version, `.apk` lowercase). The QA/final status belongs in release records and the HTML changelog, **not** the filename. Do not rename historical APKs without separate permission. Ensure the upload step uses this canonical name regardless of Gradle's original output filename.
