# Owner policy: NOVA-first dependencies; Jetty and Android platform research
Date: 2026-10-12. Status: approved policy and research notes, not implementation authorisation.

## Dependency adoption policy (owner-approved)
- For a third-party component shared with NOVA Video Player, examine NOVA's upstream adoption first. Use its integration changes, patches, dependency compatibility and testing as the primary candidate, then independently verify in Supernova's effective source/build/device.
- Selectively adapt nonvisual fixes; preserve Supernova UI, DB, data, native interfaces and behaviour. Upstream adoption is useful evidence, not proof of safety or compatibility.
- For components unique to Supernova, maintain directly.
- Exceptions allowed where NOVA lags on known security risks or is unsuitable, including Jetty; document rationale, compatibility matrix, security reachability, rollback and testing.
- Never automatically upgrade a library based only on version number.

## Jetty research
- Supernova 0.138 and inspected current NOVA MediaLib both pin Jetty 9.2.30.v20200428; copying upstream does not upgrade it. Exact v6.5.5 APK artifact dependency unresolved.
- Official Jetty download page checked 2026-10-12 lists latest 12.1.14, and 12.0.40; Jetty 12 only community-supported family. https://jetty.org/download.html
- Jetty 9.4 requires Java 8; Jetty 10/11 require Java 11; Jetty 12 requires Java 17. Android API level is not the Java runtime level. https://jetty.org/docs/jetty/12/index.html
- Modern Jetty includes newer security fixes, HTTP/2 and HTTP/3 transports, new handler/Servlet architecture, multiple Jakarta/Java EE environments, and 12.1 Brotli/Zstandard compression and QUIC architecture. These do not imply better local video decode or guaranteed UPnP throughput. https://jetty.org/docs/jetty/12.1/programming-guide/migration/12.0-to-12.1.html
- Nine published OSV advisories matched Jetty 9.2.30 SBOM coordinates in Codex report 11; exploitability in Supernova unknown. Assess all nine and newer advisories; no broad claim that Jetty 12 fixes every possible security issue.
- Study paths: isolated Jetty 9.4 compatibility probe (still EOL); security backports; Java 17 Jetty 12 Android feasibility and jUPnP API compatibility; alternative UPnP stack/transport. Keep minSDK23 unless evidence and owner approve change.
- Investigate historical Jetty releases/improvements more exhaustively before deciding; present above as preliminary category-level review only.

## Android platform
- Latest stable Android 17/API37 released June 2026: https://android-developers.googleblog.com/2026/06/Android-17.html
- Supernova 0.138 minSdk23, targetSdk36, compileSdk37 (already latest compile SDK). Raising minSdk excludes older devices; targetSdk37 changes behaviour on Android17 devices and is independent of Jetty compatibility.
- Android17 target behaviour includes local network ACCESS_LOCAL_NETWORK runtime permission, stricter background audio, native dynamic loading restrictions, certificate transparency, large-screen resize policy, lock-free MessageQueue, etc. https://developer.android.com/about/versions/17/behavior-changes-17
- Assess LAN sources (SMB/WebDAV/UPnP), foreground/background/PiP, native loading and D-pad layouts before targetSdk37. Do not assume Android17 benefits appear on current Shield OS.
- Separate Jetty migration, Android target bump, minSdk decisions; preserve installation/data.

## Related accepted design/investigation direction
- Shared Library View Foundation: same stats engine and semantics for Movies, TV Shows and custom Libraries with selection-specific input; correct file-size unknown indicator, unique title/show/episode counting, storage location and cross-source deduplication. Same UI control sizing/focus contract on all pages. Exact statistics visual layout still open.
- Continue investigations/documentation only, no code modifications or release start without distinct owner authorisation.
