# Preview 4.1.5 stability pass — validation pending

Baseline: aa4337d7a3d9e51161e406f232a6531f25e70c36, codex/apk-build-fixes.

The full 4.1.4 audit, finding index, current release/QA documentation and all files/JSONL records in Supernova-Diagnostics-1790127329010(1).zip were inspected before source changes. No 3.6 requirements were imported. Missing baseline symlinks after workspace rehydration were recreated with their exact HEAD targets.

## Physical crash evidence
Release R8 mapping maps VideoCursorMapper.bind:7 to original source line 120: ARCHOS_MEDIA_SCRAPER_TYPE. At 02:35:04.720 its CursorWindow integer read throws IllegalStateException. The loader catches, logs and rethrows; the asynchronous FutureTask then wraps and propagates the exception, terminating the process 7 ms later. The archive contains no raw database, field storage type, cursor position or exception message. A malformed value, invalid window or database damage cannot be distinguished conclusively. Null and integer boundaries alone do not establish the cause.

Correction: per-record mapping containment with schema/row/type diagnostics, bounded rejection, no journey/cache writes after partial mapping, and a visible error plus previous/empty snapshot after query failure. SQLite errors abort the load and are reported, not interpreted as individual malformed items. Cancellation still propagates. No destructive repair or swallowed corruption. **Root data/window cause remains unproven; PHYSICAL SHIELD QA REQUIRED.**

Two older uncaught scene-transition exceptions occur in this same 4.1.4 build. Preview cards lack the legacy image view; optional transition creation now requires an attached source view. Details still opens without the animation.

## Validation status
Local Gradle bootstrap attempted: blocked downloading services.gradle.org (Network is unreachable). This environment also lacks an Android SDK/emulator. CI is the compilation, unit-test, packaging and available smoke-check route. No 4.1.5 test success or APK is claimed yet.

## Scope and limits
Security: bridge binds IPv4 loopback; SFTP uses persisted trust on first use, shared SSH wire-key fingerprints across both backends, rejects changed keys, explicit per-host reset. First connection remains vulnerable to an already-present interceptor. Pins are not portable backup credentials.

Restore: durable rollback intent precedes live changes; startup recovery runs before providers, restores the old generation if uncommitted, keeps the new generation after committed marker, and retries interrupted cleanup. Recovery failure deliberately stops before database providers open to avoid using mixed state. Power-loss/disk failure and concurrent non-media database writers still require testing; this is not a claim of a filesystem-wide atomic transaction.

Playback: transferred automatic resume crosses the launch boundary; explicit restart/remote/manual file selection remain distinct. Up Next compares season/episode before choosing quality. Provider skips no longer synthesize completion. No evidence connects this provider path to the original long WebDAV failure, which is not declared fixed.
