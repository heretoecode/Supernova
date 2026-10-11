# Supernova — GitHub Releases and Google Drive publishing implementation handover

Status: **USER APPROVED DELIVERY DIRECTION, IMPLEMENTATION NOT YET CONFIGURED** (2026-10-11). This is a documentation-only Codex handover. No application changes, no workflow deployment, no credentials, no release publication are authorised merely by committing this plan.

## Goals
On completion of an approved signed QA build, provide:
1. A GitHub **pre-release** with readable, version-specific release notes and a direct standalone asset named exactly `Supernova_<version>.apk`. Mark QA versions as pre-release; do not imply final device acceptance. Use a matching version tag (e.g. `v0.136`) anchored to the exact verified source commit. Never retag an existing published version or overwrite an existing asset without explicit user approval.
2. A Google Drive copy of that **same APK bytes**, same filename, in `Supernova/APKs` (folder ID `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`).
3. An in-place update to the existing dark `Supernova Development.html` in parent `Supernova`, file ID `17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW`; retain all historical notes and prepend the new version. No duplicate HTML.
4. Verify SHA-256 equality for local verified signed APK, GitHub release asset and Drive upload; report partial failure honestly.

## Build and publishing boundaries
- Publish only after successful clean build, test, native/conformance, signing and APK identity checks. Confirm package `app.supernova.player`, versionName, versionCode, certificate continuity, source commit, APK SHA-256. The user installs using Solid File Explorer linked to Google Drive on NVIDIA Shield.
- Technical QA JSON/logs/checksums remain in GitHub Actions/repository evidence, **not** the user-facing Google Drive APKs folder. GitHub Releases should have a single APK asset; no mandatory ZIP. Existing Actions artifact can remain as CI evidence.
- Release notes must be factual, readable, grouped with emojis, distinguish tested-by-CI from tested-on-Shield, and avoid claiming unverified fixes or externally blocked integrations. Both GitHub and HTML should derive from the same versioned release-note source to avoid drift.
- Preserve original signing and application identity; no changes to Android UI or playback. No main merge or change to active Codex branch solely for this handover.
- No new APK release is approved by this documentation. Do not publish the historical 0.136 APK retroactively without a separate explicit release-publication decision.

## Secure automation architecture (to implement only when authorised)
- GitHub Actions workflow triggered by an explicit approved release/publish event, NOT arbitrary pushes or completion of any Codex session. Require an environment approval gate, minimal `contents:write` for creating releases, and read-only default permissions otherwise.
- Use authenticated Google Drive API access with **least privilege**; GitHub Actions does not inherit ChatGPT's Google Drive connector session. Prefer short-lived federated credentials where supported; otherwise securely scoped encrypted environment secrets, rotated and never logged/committed. A Google service account may need the folder shared explicitly with it; Google Drive OAuth may instead be needed for My Drive depending on account configuration.
- Build a dry-run/check mode that validates the exact versioned name, SHA-256, destination IDs, existing release/tag/file conflicts, and changelog generation. Upload idempotently; if Drive upload or HTML update fails, mark delivery incomplete and retain evidence for retry without duplicating assets.
- End-to-end acceptance: inspect GitHub pre-release asset, download the standalone APK, verify hash; inspect Drive APK folder and HTML same-file-ID update; user checks Solid File Explorer on Shield.
- Before writing CI workflows, review existing signed QA provenance and current repository release permissions, and request approval for credentials and publishing trigger.

## Existing sources
- `docs/project/SUPERNOVA_MANDATORY_DRIVE_RELEASE_DELIVERY_HANDOVER_2026-10-11.md`
- `docs/project/SUPERNOVA_APK_DELIVERY_AND_CHANGELOG_REQUIREMENTS_2026-10-11.md`
- `docs/next-refinement/DELIVERY.md` and `RELEASE_NOTES.md` on the current refinement branch

**Current status:** GitHub Actions signed QA 0.136 exists as a ZIP artifact. No verified GitHub Release with direct APK has been established. Secure automated publishing and Drive upload remain to be implemented.
