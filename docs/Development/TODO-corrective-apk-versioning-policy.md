# TODO — Unique version identifiers for every delivered Supernova APK

**Owner decision/request:** 16 October 2026. Applies to future corrective builds and releases. Do not interrupt the current accepted playback/diagnostics repair or begin 0.140.

## Problem
A corrective APK containing playback and Settings fixes was delivered with the same displayed version **0.139** as the preceding APK. This makes installation, QA evidence, diagnostics, release notes and Drive file identification ambiguous.

## Proposed release scheme for owner review / implementation
- Main feature release: `0.139`.
- First corrective release: `0.139.1` (retrospective label only if the already delivered artifact can be accurately identified; do **not** relabel a signed APK or misstate its embedded version).
- Subsequent corrective release: `0.139.2`.
- Next scheduled feature release remains **0.140**, with its five approved feature areas unchanged.
- Every **new delivered APK** must have a unique Android `versionName`, a strictly increasing Android `versionCode` compared with the previously delivered/installable APK, a distinct filename (e.g. `Supernova_0.139.2.apk`), exact source commit and SHA-256.
- Codex must audit the current versionCode scheme and deployed artifacts before choosing new codes; avoid collisions with the reserved `0.140` release and preserve upgrade compatibility, application ID and signing certificate.
- Never simply rename an APK to imply its internal version has changed. Do not overwrite an existing Drive release without explicit authorisation.
- Update ordinary and HTML release notes, diagnostics version reporting and Drive release inventory for each corrective delivery.
- CI must verify versionName/versionCode/filename, uniqueness and upgrade path before release signing/upload.

## Acceptance
Future corrective APKs are unambiguously distinguishable from prior installed versions and install as updates using the same signing identity. The planned `0.140` feature release is not consumed by corrective work.

## Related
Backup & Restore configuration-list regression discovered after playback/diagnostics repair: former grid options disappeared; intended centre vertical list with right-side selected-option description. Preserve working playback, diagnostic access and Continue Watching.
