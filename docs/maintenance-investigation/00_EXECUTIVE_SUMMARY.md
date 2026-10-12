# 00 — Executive summary

**Documentation-only investigation delivered against signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`.** All recommendations are PROPOSED and provisional until rechecked against validated 0.139. This is a substantial static audit with specific incomplete runtime/portability/security work; it is not release readiness or implementation approval.

## Most important verified findings

1. **Exact ancestry differs from release shorthand.** Video common ancestor is `f53a1fc055d1d550a2edad4992bf82f9d12a7872`, before the later manifest Video pin. Latest available upstream release is AVP **v6.5.5** at `eacf19d5ff8de2e250f77a639dfe9c12429ed076`; current Video compared head is `e394eda85058d076c1b85adf7b64a788068d2c8a`. Release manifests use moving component refs; current snapshots are not proven shipped binary pins.
2. **Two incompatible database v62 schemas.** Supernova uses v62 for custom collection tables; upstream uses v62 for genre IDs. Direct upstream adoption misses migration for installed Supernova62. The inherited downgrade helper deletes the DB, while backups require the current version. Data preservation and recovery must precede schema adoption.
3. **Diagnostics already has much of the requested infrastructure.** Default-on local recorder, bounded queues/flight ring, process/session/operation correlation, incidents/manual QR references, heap/native/PSS/GC/CPU trends, stall watchdog, OS exit evidence and export exist. Expand it rather than duplicate it. Seek IDs, first-output measurement quality, transport/native coverage and overhead/global retention need improvement.
4. **Generic privacy boundary is insufficient.** String/Object fields use denylist/regex sanitization rather than event-specific typed allowlists. Source rules and synthetic Python translation probes show surviving token/password/cookie fragments and usernames; no actual leaked report or private log was observed. Other logging sinks need independent review.
5. **Resolved Jetty9.2.30 matches nine advisory IDs.** jUPnP uses Jetty client/server, so it is not unused. Conditions such as TLS/session/multipart are not all established; exploitability remains conditional. Prioritize secure compatible remediation assessment.
6. **Transport and native trust need attention.** StreamOverHttp's ServerSocket(0) lacks explicit loopback binding despite localhost URLs. Upstream FFmpeg builder disables verification by default; locked8.0.1 source also has a legacy default controlled by version macro. Device listener/compiled HTTPS trust behavior remains unverified; neither change should be blindly accepted.
7. **Effective sources include12 dependency patches.** All58 file applications reconstructed in memory; pins alone omit custom scan/source/privacy/native metadata behavior. Existing HTTP range/cancellation and native pause behavior partly overlap upstream fixes. Native/library API changes must preserve Supernova HDR/track and durable progress fields.
8. **Shared UI foundations already exist.** UniversalFileBrowser, SharedThreePanel, common Library renderer, PreviewDialog and shared keyboard/focus helpers are current code. Overview's Information and Overview headings use generic matching styles, not existing semantic header types; centre scan summary is explicitly nonfocusable in0.138.
9. **FFmpeg QA provenance has advanced beyond older audit assumptions.** Foundation lock rebuilds n8.0.1 source `894da5ca7d742e4429ffb2af534fcda0103ef593`; existing QA receipts tie24 rebuilt libraries to signed candidate. Generic builder/prebuilt branch labels do not override this provenance.
10. **No verified dead-code or speedup claims.** Whole source, manifest, symbols, UI consumers and nine byte-identical resource groups were inventoried. Broad keep rules/native/classic/external/JNI paths create removal risk. Actual packaged/installed footprint and runtime comparisons remain needed.

Evidence and impact are detailed in reports01–12 and linked immutable source references. The upstream ledger investigates51 prioritized groups and inventories219 primary-module commits, plus native manifest/build/prebuilt histories. This goes beyond release-note summaries but does not certify all patches portable.

## Priorities and next owner decisions

**PROPOSED P0:** privacy/measurement boundary; database namespace/rollback; Jetty/security prerequisites; SMB readiness/credential boundaries, relay scope and native HTTPS trust policy. **PROPOSED P1:** coherent native seek/pause/subtitle/surface fixes and purpose-aware networking; scan/refresh/cache behavior; semantic UI pilot; safe R8/reachability-based cleanup.

Follow the existing sequence: install/walkthrough 0.138; approve selected fixes and smallest diagnostic expansion for provisional 0.139; validate startup/stability; rebaseline findings; then approve itemized maintenance packages. Do not merge upstream UI/updater, change application identity/signing or implement maintenance from this document alone.

## Outstanding evidence

Shield installation/playback/HDR/passthrough and all performance distributions are UNKNOWN. Full runtime UI/every-option focus atlas, exact upstream release binary pins/native equivalence, all 219 patch portability certifications, exhaustive native/advisory exploit/secret-sink assurance, exact APK/installed/DEX size comparison and media corpus hashes remain INCOMPLETE as precisely recorded in [report15](15_OPEN_QUESTIONS_AND_UNVERIFIED_ITEMS.md). No benchmark, runtime acceptance, historical incident diagnosis or unused-code certainty is fabricated.

## Scope assurance

Application detached checkout remains clean. All changes belong under docs/maintenance-investigation on docs/maintenance-investigation-2026-10. Main,0.137,0.138/release refs are checked at handover; no source/tests/assets/build/dependency/signing/CI edits, APK build/publish,0.139 implementation, PR13 merge or private log/recording collection/upload occurred.
