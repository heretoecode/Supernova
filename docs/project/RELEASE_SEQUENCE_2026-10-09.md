# Release sequencing and execution gates — 9 October 2026

**Decision:** no unnecessary waiting on planning, but **do not mix separate release scopes**. This is a release-management plan, not authorisation to edit the active fixes-only branch, create a signing key, merge, build or release.

## Stage A — Current fixes-only corrective release (first)
- Accepted baseline: Preview 4.1.7 Final, source `7efb4195639205a6526281491af40c0dab1776b7`.
- Frozen source of truth: `docs/qa/4.1.7-final-shield-qa.md` (35 entries). Active work branch `codex/post-4.1.7-shield-fixes`.
- Deliverable: correction APK using existing package/signing lineage; physical Shield regression verification and conformance against all frozen items. **No branding/identity, backup rewrite, diagnostics expansion or broad new features.**
- Newly observed Advanced/Diagnostics focus defect overlaps existing Settings hierarchy items 22–23, but explicit nested Diagnostics solution is recorded separately in `docs/qa/POST_4_1_7_ADDITIONAL_SHIELD_FINDINGS_2026-10-09.md`; user **approved inclusion on 9 October 2026**, now recorded in the frozen QA document's dated scope-addition section; no source change has yet been made. MobLand playback interruption requires diagnostic evidence before assigning cause/fix.
- Gate to next stage: user accepts corrective build/QA status, unresolved blockers recorded, branch/release status documented. Do not invent an exact completion date or imply current Codex work is complete.

## Stage B — Foundation Release (second)
- Separate fresh-install identity/maintenance scope, using approved visual assets: distinct application ID and signing identity, app name/launcher banner/icon/splash, verified version/credits/About, release binary licence audit, clean install and core Shield smoke tests. Coexistence with Preview/NOVA where feasible. No upgrade compatibility from Preview required.
- Verify key custody without publishing secret material; decide and document actual package ID/versionCode/build mechanics during engineering. Branding design is already closed.
- Gate: final platform assets integrated, correct build identity, successful Shield launch/navigation/playback, upstream/licence obligations validated against the **actual** APK, signing material protected.

## Stage C — Feature releases (after Foundation)
- Schedule separate, reviewable implementations of Backup & Restore archive format 1.0, broad diagnostic instrumentation and other approved future features (custom Library Page, Search, Multiple Versions, playback refinements). Technical source audits and isolated preparation can proceed now on documentation/read-only basis; code work should use a separately authorised branch/task and tests, not modify the fixes-only branch. A critical user-facing bug can be separately prioritised when authorised.
- Backup/Diagnostics need source audit, implementation and real Shield QA; do not mark ready simply because specifications are approved.
- **No requirement to defer preparatory engineering work** while Stage A is active, provided branch isolation and signing/release integrity are maintained. Avoid shipping unrelated changes into the corrective APK.

## Current decisions and open gates
- Release order: **fixes-only → Foundation → separate feature releases**.
- Still open: fixes release version number; final actual Foundation package ID/version mapping; verification of signing key custody and packaged licence inventory; triage of any further newly discovered QA items (Advanced/Diagnostics correction is approved); user-supplied MobLand diagnostic ZIP.
- Set aside by user: wider Settings, Network & Files and full installed-APK walkthroughs. The targeted Advanced defect is not a reopening of the wider walkthrough.

## 9 October 2026 — explicit user supersession: Foundation FIRST
**LATEST USER DECISION OVERRIDES THE EARLIER STAGE ORDER ABOVE:** execute **Foundation Release → fixes-only corrective release → separate feature releases**. The app is single-user on the user's Shield, and existing UI defects do not block the fresh identity release. Do **not** treat the older Stage A/B/C order as current authority. Foundation is now the next implementation release. Preserve the fixes-only requirements and apply them after Foundation on the new application identity/signing lineage; do not accidentally ship an old-package corrective APK as the subsequent release.

**Signing-key correction:** the user has **already created the intended new signing key** and backed it up securely to multiple cloud providers. **Reuse that existing key; do not generate a replacement.** Do not request that the user paste the keystore or password into chat or commit secrets to GitHub. Codex should configure a secure CI signing process using protected GitHub Actions secrets or another supported secure private build environment, verify certificate fingerprint/alias and key custody without publishing secret material, and sign the release with that same identity. The user may need to supply keystore bytes, alias and passwords directly through secure secret entry, without exposing values in docs/logs. Confirm precise supported secret-upload mechanism before giving iOS-specific instructions.

**Foundation scope:** new Android application ID (exact chosen ID requires validation), app display name, approved banner/icon/splash/Space Black Blend resources, versioning/About/credits, binary licence audit, secure signing and clean Shield install. 'Code integration' means updating Android manifest, Gradle/applicationId, resources, launch entry points and related references; not introducing unrelated feature work. A new signing identity is **not** a request to regenerate keys.

**Technical checks before build:** current Gradle and manifest package configuration; any provider authorities, intent filters, file providers, backup/export paths, hardcoded old ID references, build variants and services; keystore alias/certificate fingerprint and CI secret plumbing; Android TV banner sizes/densities and splash; licensing of packaged libraries; reproducible signed APK and install smoke test. Changes belong on a dedicated Foundation implementation branch and must not alter the existing fixes-only branch. No implementation or build has been performed by this documentation update.

## Read-only Foundation preflight — source findings
- `build.gradle` already has release signing support through local `keystore.properties` (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`), but its debug-key signing path is for the old Preview identity. The new Foundation build must use the user's separately created key, not the Preview key.
- `build.gradle` currently selects `org.courville.nova.markpreview` for Preview, uses legacy version labels and generates `APP_INFO` beginning `Nova v`. Codex should update the correct build variant and user-facing version identity.
- `AndroidManifest.xml` derives some provider authorities from the application ID but hardcodes others, including `browser.SearchProviderVideocommunity` and `com.archos.media.videocommunity`. Audit collisions and compatibility for the new install; do not blindly rewrite shared provider contracts.
- Both `.github/workflows/build-apk.yml` and `build-preview-apk.yml` protect the existing **Preview signing certificate** using `NOVA_SIGNING_KEY_BASE64`, `~/.android/debug.keystore` and a pinned certificate hash. Foundation requires an isolated secure signing configuration and a distinct protected secret for the user's already-created new keystore; do not overwrite the old secret or remove identity checks.
- **Remaining user participation:** confirm the exact new application ID if not already approved, and securely provision existing keystore bytes/alias/passwords through a supported private mechanism. Codex handles Gradle, manifest, CI, asset integration and verification. Never request secrets pasted into chat or committed to GitHub.
