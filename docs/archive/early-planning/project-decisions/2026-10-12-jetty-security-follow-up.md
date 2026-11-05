# Jetty dependency security — follow-up investigation (2026-10-12)

Status: read-only findings and owner-approved investigation direction. No code/dependency/build changes or security clearance.

## Confirmed dependency comparison
- Supernova signed 0.138 effective MediaLib source at `heretoecode/aos-MediaLib@5758074049bc153ac4967b4ecd21886469270076`, build.gradle pins Jetty server/servlet/client `9.2.30.v20200428`, via jUPnP 3.0.5.
- Current inspected upstream `nova-video-player/aos-MediaLib@34e319942e8f9df965bb7feae74e939e232f67c4` build.gradle explicitly sets `jettyVersion = '9.2.30.v20200428'`, and uses it for jetty-server/servlet/client. Therefore importing that current upstream source **does not upgrade Jetty**. Exact Jetty coordinates packaged in the released NOVA v6.5.5 APK are **not verified**; release manifest refs are moving.
- Existing Codex maintenance report 11 matched **nine distinct published advisory IDs** to Jetty 9.2.30 artifacts in the 0.138 resolved SBOM. These are third-party dependency vulnerabilities, not nine independently discovered Supernova-specific exploits. App reachability and exploitation are not established. jUPnP uses Jetty server/client; cannot assume unused.
- Upstream source comments cite Android minSdk compatibility and Java APIs as constraints. Treat these as source rationale requiring actual build/runtime tests, not proof no secure solution exists.

## Public upstream Jetty position checked
- Jetty's official downloads/support pages say Jetty 12 is the supported community series; Jetty 9/10/11 EOL and, since 2026-01-01, EOL release binaries are not published to Maven Central. EOL security-fix binaries may be available via commercial channels.
- Jetty security page includes advisories and fixed-version ranges extending into 2026. Jetty 12 requires Java 17, so adopting it on an Android minSdk23 app with jUPnP needs source/API/runtime/desugaring feasibility work.
- Sources: https://jetty.org/download.html ; https://jetty.org/support.html ; https://jetty.org/security.html ; https://jetty.org/docs/jetty/12/index.html

## Proposed decision paths, no automatic update
1. Verify the actual Jetty version in NOVA v6.5.5 release artifact or immutable build provenance.
2. Classify each of the nine SBOM advisory matches by affected code path, actual UPnP handler/client configuration, reachability from local/LAN/untrusted inputs, and fixed-version range. Re-run against 2026 advisories because original OSV result is not exhaustive.
3. Evaluate an isolated compatible update/backport of affected Jetty 9 components and/or a replacement transport/UPnP stack, comparing security support, minSDK23, Java/desugaring, jUPnP ABI, licenses, binary size, and functional regressions.
4. Test device startup, UPnP discovery/browse/stream, network exposure, malicious-request safe synthetic fixtures, Android23 and Shield; ensure dependency tree resolves consistently and avoid Jetty jar mix.
5. If immediately reachable serious issue is confirmed, propose a separate narrowly scoped security patch for owner approval, potentially before general maintenance. Never blindly cherry-pick or silently disable UPnP.
6. Revalidate against 0.139 and keep owner-approved Supernova UI untouched.

## Owner-approved related design direction
- Shared Library View Foundation should own statistics calculation, missing-size accounting, source categorization, title/show/episode identity deduplication, consistent filtering, and one presentation contract across Movies, TV and user-created Libraries, allowing page-specific inputs.
- Shared Library controls: keep existing text/icon sizes, content-fit single-line labels, reduced container padding/height, equal packed left-aligned gaps; universal tab/control/grid focus contract as documented in 2026-10-12-supernova-0138-shield-qa-and-library-foundation.md.
- Investigate and design, do not implement until separately authorised.
