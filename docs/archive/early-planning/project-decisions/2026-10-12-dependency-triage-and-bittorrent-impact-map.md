# Dependency health triage and BitTorrent removal impact map — 0.138 source baseline
Date 2026-10-12. Read-only investigation. **Not a completed full dependency upgrade audit, removal patch, APK inspection, or Shield certification.** No app code changed.

## Scope and evidence
- Source: 0.138 commit b3504c2c4b1725f3070416d14b63e53ae1db3d74, docs/foundation/RUNTIME_SBOM.json; 138 resolved external runtime artifact records, with duplicate coordinate entries in some configurations. SBOM explicitly says conservative inclusion before R8 reachability inspection; cannot equate all records with final packaged code.
- Native manifest .github/build/nova-ci.xml, Codex reports 03, 11 and 12 on docs/maintenance-investigation-2026-10, and official upstream project releases checked 2026-10-12.
- GitHub default-branch code search for torrent/libtorrent/magnet produced no results; **not proof of absence** (branch coverage and index limitations). Full repository tree/JNI/native graph and APK still need dedicated static+build inspection.
- Source pin/provenance labels: FFmpeg n8.0.1; OpenSSL 3.5.7; libtorrent 2.0.14; Boost 1.92; dav1d header 1.5.4; Opus ELF 1.6.1; libmysofa wrapper 1.3.5 vs exported 1.3.3 (Codex report11). These are *attributed receipts*, not newly inspected APK binaries.

## High-priority independently verified upstream differences
| Component | 0.138 evidence | New upstream release evidence | Decision / qualification |
|---|---|---|---|
| Jetty | Seven resolved artifacts 9.2.30.v20200428, jUPnP uses client/server; nine OSV advisory IDs in report11 | Jetty downloads 12.1.14 latest, 12.0.40, 9.4.58 EOL; https://jetty.org/download.html | P0 exception evaluation, API23/Java17 incompatibility risk; source current NOVA still pins 9.2.30; no immediate upgrade |
| OpenSSL | native receipt 3.5.7 | 3.5.9 security patch released Sep 29 2026, https://github.com/openssl/openssl/releases ; 3.5.8 and 3.5.9 contain numerous CVE fixes | P0 evaluate actual shipped source/binary and vulnerable feature reachability, NOVA-first builder adoption, coordinated ABI tests. Version gap two patches, **not proof exploitable** |
| libtorrent | native receipt 2.0.14, torrentd native manifest | upstream 2.0.15 and 2.1.2 Sep 2026, https://github.com/arvidn/libtorrent/releases | RETIRE, not update, if no required shared consumers |
| FFmpeg | Foundation source n8.0.1, 24 rebuilt FFmpeg binaries per prior CI receipt | No authoritative latest-release comparison completed here | P0 playback security and fixes, NOVA-first coherent native migration |
| jUPnP | three 3.0.5 resolved artifacts | Latest not verified | P0 assess alongside Jetty and network reachability |
| Picasso | 2.8 resolved | Latest not verified | P2 assess usage and support |
| dav1d, Opus, libmysofa, libyuv, Boost | native receipts/manifest | Latest not verified | P1 codec/surface/ABI risk; retain until coherent NOVA comparison |
| NOVA network forks | jcifs-ng v2.1.11-nova3; jsch-2.28.7-readahead2; sardine v0.9-nova; smbj v0.15.0-nova2 | Upstream equivalence/latest not verified | P0/P1: fork provenance, network and credentials security; don't replace fork blindly |
| AndroidX, OkHttp 5.5, Retrofit 2.11, Gson 2.11, BouncyCastle 1.85/1.85.2, Sentry 8.53, Kotlin 2.2.21 and others | Resolved 138-entry SBOM | Latest not verified for each | P1/P2 dependency-family inventory; prioritize security/exposure, then support/compatibility |

The 138 artifact coordinates and hashes remain the exhaustive *baseline* evidence in docs/foundation/RUNTIME_SBOM.json. They are **not** 138 independent projects and should be grouped by owner/family. No claim that every project is up to date or safe.

## BitTorrent retirement — owner-selected for next Supernova version (provisional 0.139)
**Product requirement:** remove all BitTorrent functionality, including torrent/magnet integration, libtorrent and torrentd, as safely as possible. Do not remove unrelated functionality, delete user data, or silently remove shared code. Treat 0.139 inclusion as a requested scope addition subject to compatibility gates and owner approval of implementation handover; do not begin coding or alter existing branches without authorisation.

### Confirmed source integration candidates
- .github/build/nova-ci.xml includes native/libtorrent-android-builder, native/torrentd, native/prebuilt/torrentd, plus native/boost and native/openssl. Boost and OpenSSL may be consumed by **other** features and must NOT be removed solely because libtorrent uses them.
- Report11 records libtorrent 2.0.14 and torrentd builder/prebuilt native component provenance. Exact APK packaged torrent binaries, ABI sizes, executable process and JNI call reachability **unverified**.
- Jetty is used for jUPnP and should **not** be treated as a torrent-only dependency.
- Current UI has no visible torrent entry per owner; this does not establish removal safety.

### Required dependency/call impact map
| Surface | Inspection and removal rule | Evidence gate |
|---|---|---|
| Native manifest/build | Trace libtorrent builder/prebuilt, torrentd, .so files, executable, packaging copy rules, ABI deps; remove only torrent-exclusive nodes | Reproducible signed release APK contents before/after; all four ABIs; no unresolved DT_NEEDED/JNI |
| Java and JNI | Find torrent/magnet handlers, bindings, classes, services, reflection, dynamic registration, native methods | Code call graph + runtime startup/feature tests |
| Android manifest | Intent filters, exported components, service/receiver/provider declarations and permissions; check shared INTERNET/storage/network needs | Merged release manifest before/after |
| UI/resources/settings | Remove torrent-only screens, strings, preferences, menus and deep links; preserve backward compatibility for historic saved keys | Focus/navigation and Settings option atlas |
| Storage/backup | Identify torrent data, user-created media file associations, migration/restore readers; never drop unrelated media rows or corrupt old archives | Synthetic 0.138->0.139 upgrade and backup/restore fixtures |
| Shared network | Ensure SMB, NFS, WebDAV, SFTP, HTTP, UPnP, streaming, put.io, subtitle and metadata networking continue unaffected | Shield and API23 smoke/regression |
| Licensing | Remove notices only for artifacts actually excluded; retain LGPL/source obligations for remaining native libraries | New SBOM, license inventory and distribution artifacts |
| Security/performance | Measure attack surface, packaged native bytes, APK/install size and startup/memory after removal | Before/after measurements, not estimates |

### Proposed reversible sequence for 0.139 implementation (not executed)
1. Freeze signed 0.138 source/build and DB/backup test fixtures; inventory packaged torrent symbols, binaries, intents and feature consumers.
2. Remove user-facing and external torrent entrypoints, with compatibility behaviour for old torrent URIs that fails safely rather than crashing.
3. Remove torrent-only Java/JNI/native/service dependencies; preserve shared Boost/OpenSSL/UPnP/HTTP and library scanning components.
4. Remove torrent-specific builder/prebuilt manifest entries and packaging; rebuild all ABIs and run R8/resource shrink; compare APK and merged manifest.
5. Run upgrade-with-data and restore tests; local/network/UPnP playback; cold start; navigation/Settings; Android23 and Shield; record evidence and actual footprint.
6. If any gate fails, defer **that dependency removal** rather than sacrifice data/network/playback stability. Report remaining code clearly and don't falsely claim complete removal.

### Stop conditions
Any user data loss, DB migration uncertainty, missing native symbols, crash, network regression, playback regression, incorrect backups, broken signing/install-over-current, or inability to validate four ABI builds. No unsafe APK downgrade as recovery.

## Next required evidence (not done)
- Full source+native call graph for torrent across Supernova and sibling repos, generated merged manifest and actual 0.138 signed APK binary inventory.
- All-project upstream release/security/support comparison, with per-advisory applicability and current NOVA integration version; updated SBOM/SCA for native and forks.
- Real release build and Shield/API23 regression tests for retirement patch, after separately authorised implementation.

No application code, repository branch management, merge, or release was changed by this research note.
