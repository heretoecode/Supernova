# Supernova — next-release diagnostic expansion for maintenance baseline
Date: 2026-10-12
Status: **User-requested priority for next Supernova version AFTER the active 0.138 Codex run**, not part of current Codex handover and not implemented by this document. First audit actual current diagnostic collection and privacy behaviour; then specify and implement an expanded diagnostic system in the next appropriate release before the dedicated maintenance milestone.

## User intent
The current Diagnostics facility is enabled by default and already collects some information; preserve what is useful. Expand it to identify **memory leaks, performance issues and other operational problems**, allowing quantitative before/after comparison for the later full codebase consolidation/optimisation milestone. **Absolutely no personally identifiable information or credentials in logs** — specifically no usernames, passwords, and other personal data. No raw Shield logs uploaded externally without explicit user permission.

## Phase 1: inventory the CURRENT implementation (mandatory before design)
Trace logging and diagnostics settings, defaults, consent/privacy controls, persistence, log level, event sources, lifecycle, export paths, crash capture, exceptions, sampling and retention; inspect code and test real behaviour. Inventory **actual** existing fields, event types, coverage, on-device storage, who can access exports and potential leakage paths. Identify gaps. Do not claim current crash, leak, frame or memory telemetry exists until verified. Verify whether diagnostics are on by default in actual current build and what that means. Review past removal of diagnostic-level selector and avoid reintroducing it inadvertently.

## Candidate expanded metrics (research, prioritise, validate overhead)
- **Memory**: Java/Kotlin heap used/allocated/max, native heap if accessible, process PSS/RSS where supported, GC frequency/pause signals, sustained memory growth by screen/session, low-memory callbacks, out-of-memory errors, retained-object/leak detection in controlled QA/debug builds. Leak detection is an inference requiring corroboration, not a definitive automatic diagnosis; avoid heavyweight object graph dumps in production.
- **Performance**: cold/warm startup, screen navigation transition latency, frame-time/jank metrics where supported, UI-thread stalls, long tasks, rendering/animation delays, scan/import duration, library query latency, artwork decoding/cache performance, playback start/resume/seek latency and buffering where measurable.
- **Stability and resource usage**: crash/ANR summaries, error categories, thread/executor pressure, file-descriptor counts, resource/cache utilisation, storage and network operation durations, retry/failure counters, background task queues and throttling, battery/CPU overhead where reliable.
- **Functional operations**: local/network scanning timings and errors, indexing/database health summaries, subtitle lookup rate-limit/cache/backoff outcomes, player lifecycle and recovery events, navigation/focus failures where measurable. Keep media filenames, paths, share addresses, search terms and content identifiers out of logs.
- **Before/after measurement contract**: identical hardware/OS/build mode/test dataset and scenarios, baseline percentile distributions (p50/p95) where enough samples, sample count, collection overhead, baseline vs post-cleanup comparison, confidence and unknowns.

## Privacy and security requirements — non-negotiable
- **Data minimisation by design**: record safe structured event codes, timing, sizes, coarse counters and non-identifying state; prefer allowlisted fields over free-form log strings. Exclude names, email addresses, passwords, tokens, API keys, authentication headers, cookies, IP addresses, MACs, device IDs, serials, network-share paths, file names, folder names, media titles, URLs, search queries, raw stack traces with sensitive arguments, and other identifying or secret values.
- Sanitise **at collection time**, not just export. Audit third-party logging and exception messages. Validate redaction using seeded sensitive test data; if reliable sanitisation is impossible for a field, do not collect it.
- Local-only by default unless the user explicitly approves any upload; no automatic external telemetry or disclosure. Define retention, size caps, rotation, safe export, deletion, crash recovery and performance overhead. On-by-default recording must still be privacy-safe and lightweight.
- Production lightweight counters vs optional, explicitly enabled intensive QA diagnostics; do not keep invasive leak heap dumps or full traces continuously on all devices. Privacy and performance take precedence over 'record everything' literally.
- Do not silently change signing, app permissions, network endpoints or analytics providers.

## Deliverables before implementation
1. Verified current-state diagnostic field/source inventory and privacy gap analysis.
2. Proposed event schema and safe-field allowlist, metric sampling and overhead budget.
3. Instrumentation matrix (screen/action -> metric -> source -> trigger -> retention -> risk).
4. Automated privacy tests and redaction validation; performance overhead and battery assessment.
5. User-reviewed plan for next release, then tested implementation and a reproducible baseline capture guide for the future maintenance milestone.

## Scope boundary
This request was raised while Codex was actively working on 0.138. **Do not interrupt or amend its current implementation scope.** Next-version prioritisation is requested, but the exact release number and final metric design depend on current-state audit and approval. Link this work to the future maintenance milestone for baseline comparisons.
