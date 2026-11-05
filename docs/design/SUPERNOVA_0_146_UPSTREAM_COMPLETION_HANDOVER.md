# Supernova 0.146 — Upstream Completion and Security Remediation Handover

Prepared 2026-10-28. **Planning document only; no implementation, branch, build or APK is claimed.**

## Objective
Complete the outstanding maintenance objective after signed 0.145. Treat 0.145 as the preserved baseline, not a failed or disposable release. Execute one continuous Codex work run without routine checkpoints. Do not stop after an audit or a handful of easy ports. Pause only for genuine unavailable access, security-sensitive owner approval or destructive actions; continue other work if one task is blocked.

## Mandatory sources
- `docs/0.145/RELEASE_REPORT.md`
- `docs/0.145/UPSTREAM_MATRIX.md`
- `docs/0.145/UPSTREAM_DELTA.json`
- `docs/0.145/JETTY_SECURITY_DISPOSITION.md`
- `docs/0.145/QA_CHECKLIST.md`
- `docs/0.145/SECURITY_REVIEW.md`
- `docs/0.145/DIAGNOSTIC_REVIEW.md`
- Prior approved 0.144 UI requirements and visual references.
Verify exact current repository, release/tag, upstream component commits, CI and signing environment before choosing an implementation baseline. Do not assume a mutable upstream branch equals the exact released APK source. Use `codex/supernova-0.145-maintenance-2026-10-28` and exact 0.145 signed source `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60` as investigation anchors; verify live state before branching.

## Mandatory completion standard
For **every** upstream matrix entry previously marked deferred, assess actual relevance to Supernova and classify:
1. **Integrated/adapted** with source change and regression evidence;
2. **Already present/equivalent** with specific source/test evidence;
3. **Not applicable** with concrete technical reason;
4. **Blocked** with reproduced incompatibility, dependency limitation, failed test or external constraint and a concrete remediation plan.
Do not accept generic “review not completed”, “complex”, “risk”, “UI preservation”, or “needs JNI migration” as final disposition without a specific investigation. Preserve legitimate exclusions such as reverted upstream changes, unrelated mobile UI and third-party updater/identity changes. Do not blindly copy all commits.

## High-priority work
- Coordinate native libass/subtitle-engine, FFmpeg 9, OpenSSL 4.1 and their JNI/ABI/build-system dependencies; attempt validated integration rather than automatically deferring. Review source/licence provenance, 4 ABIs, API 23, playback, subtitles, resume and performance. If a component cannot safely ship, document a reproducible blocker and tested alternatives.
- Resolve nine legacy Jetty 9.2.30 package advisories with a maintained API-23-compatible Jetty/jUPnP transport or reviewed security backports. Preserve UPnP, IPv6, async, SOAP and real-provider interoperability; do not weaken TLS, suppress advisories or silently raise min SDK. Existing 0.145 boundary mitigations remain.
- Revisit outstanding network/SMB, audio routing, playback recovery, metadata, storage, UI-adjacent and other upstream deltas; adapt compatible behavioural fixes without replacing approved Supernova screens.
- Review existing 0.145 lint warnings, dependency risk and actual measurable performance; fix meaningful problems without unrelated large rewrites.

## Safety and verification
Preserve app identity, signing certificate, stored libraries, credentials and preferences, existing 0.144/0.145 features and approved UI. Do not reset/revert uncommitted work, merge into main, publish public releases, or change signing protections without approval. Create a dedicated 0.146 implementation branch after baseline verification.

Run complete Android/unit/integration, native sanitizers, lint, signing/source-parity, dependency/security, API 23 and 4-ABI gates. Test seek/resume, SRT/VTT/PGS/ASS subtitles, HDMI/audio routes, network faults, SMB/WebDAV, UPnP, migrations, remote focus and UI regressions. Device-specific validation needs actual Shield evidence; otherwise mark pending. Fix failures and rerun; document concrete blockers without claiming full clearance.

Produce signed QA APK and upload to existing Drive folder, update HTML release notes, full matrix, security review, QA evidence and draft PR when authorised and technically ready. If signed QA candidate contains unresolved critical risks, clearly identify and do not claim general release acceptance. Final report: exact upstream baseline, disposition of every prior deferred item, integrated commits, native/security status, tests, device QA, source SHA, APK SHA256, Drive URL, draft PR and any genuine blockers.
