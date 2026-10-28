# Supernova 0.145 / 145 — undelivered maintenance candidate

Preserves Supernova's interface, application identity, saved libraries and settings on the verified 0.144 baseline. Draft PR: https://github.com/heretoecode/Supernova/pull/25 .

Compatible NOVA ports address network playback reopening/latest-seek retention, paused subtitle replay timing, duplicate HDMI receiver registration, private media suggestions, internal subtitle-cache fallback, legacy MediaStore write access and native buffer-allocation overflow.

Local verification: 128 tooling cases and sanitized C boundary tests pass. The initial full Android suite passed 1,144 of 1,147 tests; three new storage fixture cases failed. Corrected-source focused CI passed all 12 new port cases and lint. FFmpeg rebuild/runtime checks and wrapper builds passed on the earlier source, but its unit failures correctly prevented conformance clearance. Corrected-source full conformance is pending. These are software checks; physical Shield, real sources and AV chain, exact-reference visual comparison, signed upgrade and rollback remain pending.

The [upstream matrix](UPSTREAM_MATRIX.md) distinguishes included/adapted changes from deferred and excluded work. Review of the new native subtitle stack and other upstream deltas is unfinished. [Security findings](SECURITY_REVIEW.md) include nine Jetty advisories; no security clearance is claimed.

Signing is blocked by the existing environment's branch allowlist. No signed 0.145 APK, checksum, Drive APK link, public release or main merge exists. The existing Drive HTML notes were updated in place with this candidate status, all 13 earlier articles preserved, and full-byte readback verified.
