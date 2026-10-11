# Supernova APK delivery and public changelog — user-approved delivery preference
Recorded 2026-10-11. **Delivery workflow requirement; implementation of automation not yet verified.**

## User decision
- Future completed Supernova builds should be delivered as **standalone APK files**, **not ZIP bundles**.
- Do not make the user handle `public-qa-evidence.json`. Keep technical QA evidence in GitHub Actions / development artifacts and link it from technical release records as appropriate.
- **Preferred distribution destination: Google Drive**, rather than giving the user a direct APK download link.
- Drive folders created and verified:
  - [Supernova](https://drive.google.com/drive/folders/1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ) — ID `1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ`
  - [Supernova/APKs](https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM) — ID `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`
- Reader-friendly HTML master changelog **Supernova Development.html** was created in the root Supernova folder: [Drive file](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view?usp=drivesdk). It has a designed structure and placeholders only until exact release notes can be verified. HTML file opening behaviour depends on Google Drive preview/download/browser handling; it is **not** a hosted public website.
- Title: **Supernova Development**. Record versions in reverse chronological order, each with version/date and user-facing changes. Explain practically every verified change without heavy technical jargon; use emoji labels ✨ New, 🎨 Improved, 🛠️ Fixed, ⚡ Performance, 🔒 Security/privacy. Distinguish shipped, installed QA and in-progress work.

## Important engineering boundaries
1. Codex finishing a build does **not automatically upload it to Drive**. An authenticated Google Drive publishing step (e.g. an approved GitHub Actions credential/service-account or explicit connector-mediated upload) must be designed, authorised, implemented and tested before this is automatic.
2. Never commit Google Drive tokens, refresh tokens, private keys or service-account JSON into the repository. Use appropriately scoped secret storage and minimum privileges. Drive access to the destination folder must be configured explicitly.
3. Preserve APK signing, package identity, provenance and checksums. Publish the **actual final validated APK**, not an earlier interim APK. Ensure filename contains accurate release identifier. Prevent accidental duplicate overwrites.
4. Keep QA JSON and checksums in development evidence storage; the user-facing APK folder should contain only APKs. Do not destroy evidence merely because it is excluded from the delivery package.
5. Update the HTML changelog from the **verified** final release change register, not speculative feature lists, and publish it as a file in the parent Supernova folder. Retain history for prior versions; avoid false claims about completed fixes.
6. The user has reported the latest Codex refinement completed and its APK installed. **That is not proof of passed QA** or evidence that automatic Drive publishing already works.
7. Do not change the active codebase/CI/signing/PR without explicit implementation authorisation. This document captures a **future delivery workflow requirement**, not approval to edit CI immediately.

## Work remaining
- Locate exact final build APK and provenance, then arrange standalone publication for future builds.
- Design/authorise secure CI→Drive credentials, permissions, retries and failure reporting.
- Test an end-to-end final APK upload into `Supernova/APKs` and test direct download/installation on NVIDIA Shield.
- Decide whether to preserve historical APKs and how to label QA vs final.
- Extract verified release changes from GitHub history and update the HTML with actual entries.
- Test how HTML opens from Drive on the user's device; if Drive preview does not open as a rendered web page, consider a hosted changelog or downloading/opening the HTML locally. Do not promise Drive will act as a static website.

**State:** Drive folder hierarchy and initial HTML file exist. Automated Codex delivery and full release-history population remain pending.
