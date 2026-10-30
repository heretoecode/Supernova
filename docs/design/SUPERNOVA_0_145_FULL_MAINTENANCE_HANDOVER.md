# Supernova 0.145 — Full Maintenance and NOVA Upstream Integration Handover

**Prepared:** 2026-10-28. **Status:** Ready for Codex baseline verification; no 0.145 implementation, build, signing, or delivery is claimed here.

## Mission: one continuous run
Carry out the complete maintenance release without routine checkpoints or repeated permission requests. Investigate, implement, test, fix, re-test, document, sign and deliver where access permits. Pause only for genuine credentials, permissions, irreversible/destructive actions or personal approval. If blocked on one task, continue all independent work. Never claim unperformed verification.

## Authoritative baseline
Repository: `heretoecode/Supernova`. User reports Codex finished 0.144. Locate and inspect `codex/supernova-0.144-implementation-2026-10-28`, its PR, commit history, validation, delivered APK and notes. **Do not assume 0.144 was merged, signed or delivered until verified.** Select the latest correct, complete source baseline and create a dedicated 0.145 implementation branch. Preserve all changes and uncommitted work. Do not reset, overwrite, merge into main or publish a public release.

## Mandatory upstream integration
1. Verify the official NOVA Video Player upstream repository and its latest stable tagged release at execution time. **6.6.2 is the current proposed target, not an unverified guarantee of latest.** Identify exact upstream commit/tag and compare it against Supernova's original NOVA 6.4.64 baseline, accounting for any upstream changes already incorporated.
2. Produce a traceable upstream delta inventory: security, dependencies, playback engines, codecs, audio/video sync, subtitle engines and caching, WebDAV/network, metadata, scanning, performance, Android/TV compatibility, build tooling, and relevant bug fixes.
3. **Integrate compatible upstream changes into Supernova code**; an audit-only report is insufficient. Prefer surgical ports/cherry-picks or carefully reviewed equivalents over replacing Supernova source wholesale. Resolve conflicts without undoing Supernova-specific behaviour.
4. Exclude or adapt upstream UI changes that would replace Supernova's approved Settings, library, Details, navigation, playback HUD, artwork, branding or remote focus design. Document each included, adapted, deferred or rejected upstream change and reason. Flag high-risk changes with evidence, and never silently omit a security fix.
5. Keep current application identity, signing, data compatibility, stored libraries and settings. Avoid destructive schema changes; test migration and rollback paths.

## Full maintenance scope
- Audit dependencies, security advisories, SDK/Gradle/tooling and build reproducibility; update safely with compatibility testing.
- Profile and address measurable startup, playback, memory, artwork, network/WebDAV, scanning and library performance issues.
- Investigate leaks, lifecycle/concurrency issues, cache correctness, stale records, exception handling, logging, recovery, obsolete code and technical debt. Make focused, justified fixes, not indiscriminate rewrites.
- Regress every 0.144 feature against its approved handover `docs/design/NEXT_VERSION_CODEX_HANDOVER_2026-10-28.md` and linked specs on `docs/settings-from-scratch-blueprint-2026-10-26`.
- Maintain approved Space Black design, Concept A4 playback Settings, eight-category Settings, custom library pages, ratings, overlays, Shield D-pad navigation, scan controls and existing features.
- Assess prior diagnostics and unresolved GitHub issues; distinguish current reproducible defects from historical logs.

## Validation and completion
Build and run all existing and new Android/unit/integration/regression tests, lint and relevant static analysis. Add tests for upstream ported behaviour, subtitles, seeking/resume, audio sync, WebDAV 404/offline/partial scans, database migrations, focus and artwork races. Check 720p/1080p/4K layout and TV remote behaviour with available tools. Fix release blockers and repeat tests. **Physical Nvidia Shield QA and visual parity require actual device evidence; otherwise mark pending, not passed.**

Prepare 0.145 version/version code after baseline verification. When authorised signing infrastructure is accessible, create signed release candidate APK, verify signature, record exact source commit and SHA-256, upload to existing Google Drive APK folder, and update HTML release notes and GitHub changelog. Create/update draft PR and upstream integration matrix. Do not merge into main or publish publicly.

## Final report
Provide baseline and target upstream tag/SHAs, exact upstream changes integrated/adapted/excluded, maintenance fixes, tests and outstanding risks, source branch/commit/draft PR, signed APK and Drive link, checksum, HTML release notes link, and explicitly pending physical Shield QA or unavailable artifacts. Do not call the release complete while any required work is unverified or blocked.
