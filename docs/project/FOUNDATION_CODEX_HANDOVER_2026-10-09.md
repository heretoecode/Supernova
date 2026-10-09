# Codex handover — Foundation Release FIRST

**Status:** user-authorised start of Foundation preparation, 9 October 2026. This document is a handover, **not proof of implementation**.

## Authoritative release order
1. **Foundation Release now** (app identity, signing, branding, versioning, attribution and smoke tests).
2. Existing frozen post-4.1.7 Shield **fixes-only** corrective release afterwards, rebased/reapplied carefully to the new app identity.
3. Approved future feature releases separately.

This supersedes earlier fixes-first sequencing. Single-user Shield deployment; existing UI issues do not block Foundation.

## Branch isolation
- Work only on dedicated `codex/foundation-release` for this release. Do **not** modify `codex/post-4.1.7-shield-fixes`, reset/revert it, or merge Foundation to main without explicit authorisation.
- Start from current main; inspect actual branch state and source before edits.
- Consult `PROJECT_STATUS.md`, `docs/project/RELEASE_SEQUENCE_2026-10-09.md`, `docs/project/TECHNICAL_EXECUTION_READINESS_2026-10-09.md`, `docs/design/ABOUT_SETTINGS.md` and `assets/branding/`.

## User-approved permanent identity
- Android `applicationId`: **`app.supernova.player`**.
- App label: **Supernova**.
- Java/Kotlin namespace need not be renamed automatically; distinguish namespace from install application ID.
- Fresh install acceptable; no Preview upgrade path required. Preserve NOVA attribution and licences.

## Signing — do not improvise
- User has **already created a PKCS#12 `.p12` keystore**, set a password, and securely backed up both; user has access via iPhone.
- **Never create another signing key** or reuse Preview's debug/personal signing identity for Foundation.
- Never request the `.p12` or password in chat; never commit secret material or print in logs.
- Prepare a dedicated, manual `workflow_dispatch` Foundation GitHub Actions build, with distinct protected secret names (e.g. `SUPERNOVA_P12_BASE64`, `SUPERNOVA_STORE_PASSWORD`, `SUPERNOVA_KEY_ALIAS`, `SUPERNOVA_KEY_PASSWORD`). PKCS#12 may use same password for store/key, but verify rather than assume.
- Decode the base64 secret into a temporary `.p12` on the CI runner with restrictive permissions; configure Gradle `storeType='pkcs12'`; remove temp file after build; mask sensitive values. Verify key entry/alias and certificate fingerprint without leaking secrets; record **public certificate fingerprint** in build evidence only after securely verified. Do not overwrite `NOVA_SIGNING_KEY_BASE64` or Preview's pinned certificate.
- Before asking user to configure secrets, provide **verified iPhone/iOS-compatible instructions**. GitHub's web UI secret entry is text-based; a `.p12` is binary and cannot be pasted directly. Assess a safe local-on-iPhone base64 conversion path or another secure mechanism. If no safe supported mobile route exists, stop and ask for user preference; do not route secrets through chat, untrusted web converters, repository commits or build artifacts.
- Do not run signed workflow before secret provisioning; fail closed if missing.

## Technical findings to audit
- `build.gradle` lines ~99–125 already read `keystore.properties`; line ~160 selects old `org.courville.nova.markpreview` when `markPreview`; ~169–170 legacy version; ~211 `APP_INFO` starts `Nova v`.
- `AndroidManifest.xml` derives some authorities from `applicationId`, but others are hardcoded (`browser.SearchProviderVideocommunity`, `com.archos.media.videocommunity`, `com.archos.media.scrapercommunity`). Check provider collisions with installed NOVA/Preview, external integrations and intents; do not blindly rewrite shared contracts.
- `.github/workflows/build-apk.yml` and `build-preview-apk.yml` protect old signing identity; isolate Foundation workflow and artifact naming.
- Verify final package ID, versionName/versionCode (pre-1.0 scheme previously approved), icon, Android TV banner 320x180, splash, Space Black Blend, double-ring branding from authoritative assets, About sections, credits and licence obligations (actual packaged FFmpeg/native libraries).
- Audit all old app ID references, content authorities, providers, exported components, shortcuts, backup/export paths, file provider and build variants.
- Smoke test signed release on Shield: install side-by-side if feasible, launch, navigate, library/network access, playback, resume, About, TV launcher icon/banner. No user-visible feature changes outside Foundation scope.

## Evidence and deliverables
1. Preflight findings and precise implementation plan, including secrets setup guide for iOS.
2. Foundation code/asset/workflow changes isolated to this branch.
3. Build/tests/conformance, signed APK with checksum, public certificate SHA-256 fingerprint, installed app ID/version verification.
4. Release notes and QA checklist; unresolved blockers clearly identified.
5. Ask user only for genuinely missing information or secure secret provisioning. User retains the `.p12` and password.

**Do not claim completed, signed or tested until there is actual evidence.**

## Additional approved versioning requirement — 9 October
Read current `docs/project/RELEASE_SEQUENCE_2026-10-09.md`: retrospectively map first-ever Supernova APK to `0.1` and each subsequent actual APK to `0.2`, `0.3`, ... `0.10` etc. Preserve these as literal release-counter strings, not decimals/SemVer. Foundation continues the sequence, not `0.1.0`. Reconstruct history from source/release notes/build artifacts as far as verifiable. Populate the **already approved About → Release Notes UI** with accurate per-version headings and matching factual changes; do not invent missing notes or rewrite original APK binaries. Keep Android internal versionCode distinct. This is included in Foundation implementation scope; do not ask user to redesign About.
